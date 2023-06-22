package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class LatestStatusInquiries extends MedallionController {
	private static Logger log = Logger.getLogger(LatestStatusInquiries.class);

	public static final String MESSAGE_TYPE_ALL = "ALL";
	public static final String MESSAGE_TYPE_OTC = "OTC";
	public static final String MESSAGE_TYPE_OTCBond = "OTCBond";
	public static final String MESSAGE_TYPE_PrematchOTC = "PrematchOTC";
	public static final String MESSAGE_TYPE_PrematchOTCBond = "PrematchOTCBond";

	private static final Object EXTENSION_XLS = "xls";
	private static final Object EXTENSION_XLSX = "xlsx";

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> messageTypeOptions = new ArrayList<SelectItem>();
		messageTypeOptions.add(new SelectItem(MESSAGE_TYPE_ALL, MESSAGE_TYPE_ALL));
		messageTypeOptions.add(new SelectItem(MESSAGE_TYPE_OTC, MESSAGE_TYPE_OTC));
		messageTypeOptions.add(new SelectItem(MESSAGE_TYPE_OTCBond, MESSAGE_TYPE_OTCBond));
		messageTypeOptions.add(new SelectItem(MESSAGE_TYPE_PrematchOTC, MESSAGE_TYPE_PrematchOTC));
		messageTypeOptions.add(new SelectItem(MESSAGE_TYPE_PrematchOTCBond, MESSAGE_TYPE_PrematchOTCBond));
		renderArgs.put("messageTypeOptions", messageTypeOptions);
	}

	public static void entry(long msgId, String param) {
		log.debug("entry. msgId: " + msgId + " param: " + param);
	}

	public static void edit(Long msgId, String param) {
		log.debug("edit. msgId: " + msgId + " param: " + param);
	}

	public static void view(Long msgId, String param) {
		log.debug("view. msgId: " + msgId + " param: " + param);
	}

	@Check("cbestconnector.list")
	public static void list(String mode) {
		log.debug("list. mode: " + mode);

		render(mode);
	}

	@Check("cbestconnector.list")
	public static void downloadLatestStatusInquiryAjax(String messageType, String fromDateTime, String toDateTime) throws IOException {
		log.debug("downloadLatestStatusInquiryAjax. messageType: " + messageType + " fromDateTime: " + fromDateTime + " toDateTime: " + toDateTime);

		Map<String, String> maps = new HashMap<String, String>();
		if (messageType != null && !"".equals(messageType)) {
			maps.put("name", messageType);
		}
		if (fromDateTime != null && !"".equals(fromDateTime)) {
			maps.put("createddate", fromDateTime + ";" + toDateTime);
		}
		List<Object> messages = cbestService.getLatestStatusInquiry(maps);
		log.debug("Total message to download:" + messages.size());
		List<Object[]> reRenderData = new ArrayList<Object[]>();
		for (Object msg : messages) {
			Object[] msgArray = (Object[]) msg;
			List<Object> tmpList = new ArrayList<Object>();
			for (Object tmp_ : msgArray) {
				tmpList.add(tmp_);
			}
			reRenderData.add(tmpList.toArray());
		}

		downloadToExcel(reRenderData, null, null, "xls", messageType.replace(";", "-") + "_" + fromDateTime + "_" + toDateTime);
		// write to file, then render as a file
	}

	// download to XLS
	@Check("cbestconnector.list")
	public static String downloadToExcel(List<Object[]> datas, Long id, String idFilter, String downloadTo, String nameFile) throws IOException {
		log.debug("downloadToExcel. datas: " + datas + " id: " + id + " idFilter: " + idFilter + " downloadTo: " + downloadTo + " nameFile: " + nameFile);

		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		XSSFWorkbook workbookx = null;
		XSSFSheet sheetx = null;
		if (downloadTo.equals(EXTENSION_XLS)) {
			workbook = new HSSFWorkbook();
			sheet = workbook.createSheet("Sheet 1");
		}
		if (downloadTo.equals(EXTENSION_XLSX)) {
			workbookx = new XSSFWorkbook();
			sheetx = workbookx.createSheet("Sheet 1");
		}

		Map<Integer, Object[]> dataExcels = new HashMap<Integer, Object[]>();
		int i = 1;
		for (Object[] objects : datas) {
			dataExcels.put(i, objects);
			i++;
		}
		Set<Integer> keyset = dataExcels.keySet();

		String[] titleName = new String[] { "Outgoing Date", "Outgoing Name", "Incoming Name", "Prematch Name", "Incoming Status", "Prematch Status", "External Reference", "Counterpart Code", "Security Code", "Trade Date", "Quantity", "Settlement Date", "Settlement Amount" };
		// add row for title
		Row rowTitle = sheet.createRow(0);
		int tmpCount_ = 0;
		for (String title : titleName) {
			Cell cell_ = rowTitle.createCell(tmpCount_++);
			HSSFCellStyle cell0Style = workbook.createCellStyle();
			cell0Style.setAlignment(CellStyle.ALIGN_CENTER);
			cell_.setCellValue(title);
			cell_.setCellStyle(cell0Style);
		}

		int rownum = 1;
		for (Integer key : keyset) {
			Row row = null;
			if (downloadTo.equals(EXTENSION_XLS))
				row = sheet.createRow(rownum++);
			if (downloadTo.equals(EXTENSION_XLSX))
				row = sheetx.createRow(rownum++);
			Object[] objArr = dataExcels.get(key);
			int cellnum = 0;
			int tmpCount2_ = 0;
			for (Object obj : objArr) {
				if (tmpCount2_ == 0) {
					tmpCount2_++;
					continue; // skipp
				}
				tmpCount2_++;
				Cell cell = row.createCell(cellnum);
				if (obj instanceof Date) {
					cell.setCellValue(obj.toString());
				} else if (obj instanceof Boolean) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof Double) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof String) {
					cell.setCellValue((String) obj);
				} else {
					cell.setCellValue((obj != null ? obj.toString() : ""));
				}
				cellnum++;
			}

		}

		FileOutputStream outputStream = null;
		File file = null;
		try {
			if (downloadTo.equals(EXTENSION_XLS))
				file = new File("..//medallionWeb/tmp/" + nameFile + ".xls");
			if (downloadTo.equals(EXTENSION_XLSX))
				file = new File("..//medallionWeb/tmp/" + nameFile + ".xlsx");

			log.debug("FILE PATH = " + file.getPath());
			outputStream = new FileOutputStream(file);

			if (!file.exists()) {
				file.createNewFile();
			}
			if (downloadTo.equals(EXTENSION_XLS))
				workbook.write(outputStream);
			if (downloadTo.equals(EXTENSION_XLSX))
				workbookx.write(outputStream);

			outputStream.close();
			log.debug("DONE " + nameFile + ".xls");
			if (downloadTo.equals(EXTENSION_XLS))
				renderBinary(file, nameFile + ".xls");
			if (downloadTo.equals(EXTENSION_XLSX))
				renderBinary(file, nameFile + ".xlsx");

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Check("cbestconnector.list")
	public static void getLatestStatusInquiryAjax(String messageType, String fromDateTime, String toDateTime, String aoData) {
		log.debug("getLatestStatusInquiryAjax. messageType: " + messageType + " fromDateTime: " + fromDateTime + " toDateTime: " + toDateTime + " aoData: " + aoData);

		Object sEcho = null;
		Object iDisplayStart = null;
		Object iDisplayLength = null;
		Object sSearch = null;

		ObjectMapper json = new ObjectMapper();
		List listData = new ArrayList();
		try {
			listData = json.readValue(aoData, List.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		}

		for (Object object : listData) {
			LinkedHashMap<String, String> map = (LinkedHashMap) object;
			if (map.get("name").equals("sEcho")) {
				sEcho = map.get("value");
			}

			if (map.get("name").equals("iDisplayStart")) {
				iDisplayStart = map.get("value");
			}

			if (map.get("name").equals("iDisplayLength")) {
				iDisplayLength = map.get("value");
			}

			if (map.get("name").equals("sSearch")) {
				sSearch = map.get("value");
			}
		}

		Map<String, String> maps = new HashMap<String, String>();
		if (messageType != null && !"".equals(messageType)) {
			maps.put("name", messageType);
		}
		if (fromDateTime != null && !"".equals(fromDateTime)) {
			maps.put("createddate", fromDateTime + ";" + toDateTime);
		}
		Long total = cbestService.getLatestStatusInquiryCount(maps);
		Long totalRecords = total;
		Long totalDisplayRecords = total;

		if (sSearch != null && !"".equals(sSearch)) {
			maps.put("sSearch", sSearch.toString());
			totalDisplayRecords = cbestService.getLatestStatusInquiryCount(maps);
		}
		log.debug("totalRecords:" + totalRecords);
		log.debug("totalDisplayRecords:" + totalDisplayRecords);

		if (iDisplayStart != null && !"".equals(iDisplayStart)) {
			maps.put("iDisplayStart", iDisplayStart.toString());
		}
		if (iDisplayLength != null && !"".equals(iDisplayLength)) {
			maps.put("iDisplayLength", iDisplayLength.toString());
		}

		List<Object> messages = cbestService.getLatestStatusInquiryAjax(maps);
		log.debug("messages.size():" + messages.size());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sEcho", (sEcho == null ? "" : sEcho.toString()));
		map.put("iTotalRecords", totalRecords);
		map.put("iTotalDisplayRecords", totalDisplayRecords);
		map.put("aaData", messages);

		renderJSON(map);
	}
}
