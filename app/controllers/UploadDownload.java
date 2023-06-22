package controllers;

import helpers.UIConstants;
import helpers.UIHelper;
import helpers.serializers.UpdBatchDetailsSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.remoting.RemoteAccessException;

import play.Play;
import play.mvc.Before;
import play.mvc.With;
import vo.UploadDownloadParameters;

import com.google.gson.GsonBuilder;
import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.UploadDownloadConstants;
import com.simian.medallion.helper.UploadDownloadHelper;
import com.simian.medallion.helper.UploadDownloadHelperOld;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.UdArchive;
import com.simian.medallion.model.UpdBatch;
import com.simian.medallion.model.UpdBatchDetail;
import com.simian.medallion.model.UpdFilter;
import com.simian.medallion.model.UpdProfile;
import com.simian.medallion.model.UpdProfileDetail;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class UploadDownload extends MedallionController {
	private static Logger log = Logger.getLogger(UploadDownload.class);

	public static final String COLUMN = "COLUMN";
	public static final String ERROR = "ERROR";

	private static Map<String, List<String>> columMap = new HashMap<String, List<String>>();

	private static final List<SelectItem> securityPriceSelectItem = generalService.listLookupsForDropDownAsSelectItemPosition(UIConstants.SIMIAN_BANK_ID, LookupConstants._SECURITY_PRICE_GROUP);

	// no need to check json
	public static void validateData() {
		log.debug("validateData. ");

		List<String> items = new ArrayList<String>();
		items.add(UploadDownloadConstants.VALIDATE_NOTNULL);
		items.add(UploadDownloadConstants.VALIDATE_NUMBER);
		items.add(UploadDownloadConstants.VALIDATE_NONSIGNNUMBER);
		items.add(UploadDownloadConstants.VALIDATE_DATE);
		renderJSON(items);
	}

	// no need to check json
	public static void typeData(String column) {
		log.debug("typeData. column: " + column);

		List<String> items = new ArrayList<String>();
		items.add(UploadDownloadConstants.DATA_TYPE_STRING);
		items.add(UploadDownloadConstants.DATA_TYPE_NUMBER);
		items.add(UploadDownloadConstants.DATA_TYPE_DATE);
		items.add(UploadDownloadConstants.DATA_TYPE_BOOLEAN);
		// logger.debug("column>>> "+column);
		// List<String> items = generalService.getTypeData(column);
		renderJSON(items);
	}

	// no need to check json
	public static void formatData(String param) {
		log.debug("formatData. param: " + param);

		List<String> items = new ArrayList<String>();
		if (UploadDownloadConstants.DATA_TYPE_STRING.equalsIgnoreCase(param)) {
			items.add(UploadDownloadConstants.FORMAT_STRING_FORMAT1);
			items.add(UploadDownloadConstants.FORMAT_STRING_FORMAT2);
		}
		if (UploadDownloadConstants.DATA_TYPE_NUMBER.equalsIgnoreCase(param)) {
			items.add(UploadDownloadConstants.FORMAT_NUMBER_0DIGIT);
			items.add(UploadDownloadConstants.FORMAT_NUMBER_1DIGIT);
			items.add(UploadDownloadConstants.FORMAT_NUMBER_2DIGIT);
			items.add(UploadDownloadConstants.FORMAT_NUMBER_3DIGIT);
			items.add(UploadDownloadConstants.FORMAT_NUMBER_4DIGIT);
			items.add(UploadDownloadConstants.FORMAT_NUMBER_5DIGIT);
			items.add(UploadDownloadConstants.FORMAT_NUMBER_6DIGIT);
			items.add(UploadDownloadConstants.FORMAT_NUMBER_7DIGIT);
			items.add(UploadDownloadConstants.FORMAT_NUMBER_8DIGIT);
			items.add(UploadDownloadConstants.FORMAT_NUMBER_9DIGIT);
			items.add(UploadDownloadConstants.FORMAT_NUMBER_10DIGIT);
		}
		if (UploadDownloadConstants.DATA_TYPE_DATE.equalsIgnoreCase(param)) {
			items.add(UploadDownloadConstants.FORMAT_DATE_FORMAT1);
			items.add(UploadDownloadConstants.FORMAT_DATE_FORMAT2);
			items.add(UploadDownloadConstants.FORMAT_DATE_FORMAT3);
		}
		if (UploadDownloadConstants.DATA_TYPE_BOOLEAN.equalsIgnoreCase(param)) {
			items.add(UploadDownloadConstants.FORMAT_BOOLEAN_FORMAT1);
			items.add(UploadDownloadConstants.FORMAT_BOOLEAN_FORMAT2);
		}
		renderJSON(items);
	}

	// no need to check json
	public static void securityPriceGroupList() {
		log.debug("securityPriceGroupList. ");

		List<String> items = new ArrayList<String>();
		for (SelectItem securityPrice : securityPriceSelectItem) {
			items.add((String) securityPrice.value);
		}
		renderJSON(items);
	}

	// no need to check json
	public static void targetUploadField(String param, String term) {
		log.debug("targetUploadField. param: " + param + " term: " + term);

		List<String> items = columMap.get(param);
		if (items == null || items.isEmpty()) {
			columMap.put(param, items = applicationService.listColumnNameFromTable(param));
		}

		List<String> filterItems = new ArrayList<String>();
		for (String item : items) {
			if (term == null || "".equals(term)) {
				filterItems.add(item);
			} else if (item.toUpperCase().startsWith(term.toUpperCase())) {
				filterItems.add(item);
			}
		}

		renderJSON(filterItems);
	}

	// no need to check json
	public static void sourceDownloadField(String param, String term) {
		log.debug("sourceDownloadField. param: " + param + " term: " + term);

		List<String> items = columMap.get(param);
		if (items == null || items.isEmpty()) {
			columMap.put(param, items = applicationService.listColumnNameFromTable(param));
		}

		List<String> filterItems = new ArrayList<String>();
		for (String item : items) {
			if (term == null || "".equals(term)) {
				filterItems.add(item);
			} else if (item.toUpperCase().startsWith(term.toUpperCase())) {
				filterItems.add(item);
			}
		}

		renderJSON(filterItems);
	}

	// no need to check json
	public static void sourceUploadField(String term) {
		log.debug("sourceUploadField. term: " + term);

		List<String> items = new ArrayList<String>();
		for (int i = 0; i <= 100; i++) {
			items.add("Colum_" + i);
		}

		List<String> filterItems = new ArrayList<String>();
		for (String item : items) {
			if (term == null || "".equals(term)) {
				filterItems.add(item);
			} else if (item.toUpperCase().startsWith(term.toUpperCase())) {
				filterItems.add(item);
			}
		}

		renderJSON(filterItems);
	}

	@Before(only = { "downloadentry", "uploadentry", "save", "edit", "back", "confirming", "confirm" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> actionOptions = new ArrayList<SelectItem>();
		actionOptions.add(new SelectItem(UploadDownloadConstants.ACTIONTYPE_UPLOAD, UploadDownloadConstants.ACTIONTYPE_UPLOAD));
		actionOptions.add(new SelectItem(UploadDownloadConstants.ACTIONTYPE_DOWNLOAD, UploadDownloadConstants.ACTIONTYPE_DOWNLOAD));
		renderArgs.put("actionOptions", actionOptions);

		List<SelectItem> fileTypeOptions = new ArrayList<SelectItem>();
		fileTypeOptions.add(new SelectItem(UploadDownloadConstants.EXTENSION_CSV, UploadDownloadConstants.EXTENSION_CSV));
		fileTypeOptions.add(new SelectItem(UploadDownloadConstants.EXTENSION_TXT, UploadDownloadConstants.EXTENSION_TXT));
		fileTypeOptions.add(new SelectItem(UploadDownloadConstants.EXTENSION_XLS, UploadDownloadConstants.EXTENSION_XLS));
		fileTypeOptions.add(new SelectItem(UploadDownloadConstants.EXTENSION_XLSX, UploadDownloadConstants.EXTENSION_XLSX));
		fileTypeOptions.add(new SelectItem(UploadDownloadConstants.EXTENSION_DBF, UploadDownloadConstants.EXTENSION_DBF));
		fileTypeOptions.add(new SelectItem(UploadDownloadConstants.EXTENSION_XML, UploadDownloadConstants.EXTENSION_XML));
		renderArgs.put("fileTypeOptions", fileTypeOptions);

		List<SelectItem> moOption = new ArrayList<SelectItem>();
		moOption.add(new SelectItem(UploadDownloadConstants.MANDATORY_MO, UploadDownloadConstants.MANDATORY_MO));
		moOption.add(new SelectItem(UploadDownloadConstants.OPTIONAL_MO, UploadDownloadConstants.OPTIONAL_MO));
		renderArgs.put("moOption", fileTypeOptions);

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add(new SelectItem(",", ","));
		items.add(new SelectItem(";", ";"));
		items.add(new SelectItem("|", "|"));
		renderArgs.put("inHead", items);

	}

	@Check("utility.uploadDownload")
	public static void list(String action) {
		log.debug("list. action: " + action);

		List<Object> profiles = generalService.findUpdProfileParent(action);
		if (action.equals(UploadDownloadConstants.ACTIONTYPE_DOWNLOAD))
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_DOWNLOAD));
		if (action.equals(UploadDownloadConstants.ACTIONTYPE_UPLOAD))
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_UPLOAD));
		render("UploadDownload/list.html", profiles, action);
	}

	@Check("utility.uploadDownload")
	public static void uploaddata(Long id) {
		log.debug("uploaddata. id: " + id);

		UpdBatch batch = new UpdBatch();
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		renderArgs.put("templetes", generalService.findUpdProfilenames(UploadDownloadConstants.ACTIONTYPE_UPLOAD));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROCESS_UPLOAD));
		render("UploadDownload/uploaddata.html", batch, mode);
	}

	@Check("utility.uploadDownload")
	public static void downloaddata(Long id) {
		log.debug("downloaddata. id: " + id);

		id = 2251L;
		UpdProfile profile = new UpdProfile();
		if (id != null) {
			profile = generalService.getUpdProfile(id);
		}

		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.UT_PROCESS_DOWNLOAD));
		render("UploadDownload/downloaddata.html", sort(profile));
	}

	@Check("utility.uploadDownload")
	public static void downloadentry(String mode) {
		log.debug("downloadentry. mode: " + mode);

		UpdProfile profile = new UpdProfile();
		profile.setActionType(UploadDownloadConstants.ACTIONTYPE_DOWNLOAD);
		profile.setSeparatorCsv(",");
		profile.setSeparatorTxt(";");
		profile.setStatus(new Boolean(true));
		profile.setIncludeHeader(new Boolean(true));

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_DOWNLOAD));
		render("UploadDownload/downloadentry.html", sort(profile));
	}

	// no need to check render file
	public static void generateFileDownload(Long id, String[] arrFilter, String downloadTo) throws IOException {
		log.debug("generateFileDownload. id: " + id + " arrFilter: " + arrFilter + " downloadTo: " + downloadTo);

		UpdProfile profile = generalService.getUpdProfile(id);
		profile.setDetails(generalService.getUpdProfileDetail(id));
		List<Object[]> datas = generalService.populate(id, arrFilter);
		List<String> headers = new ArrayList<String>();
		for (UpdProfileDetail detail : profile.getDetails()) {
			headers.add(detail.getTargetField());
		}

		try {
			List<UpdBatchDetail> validatedDetails = UploadDownloadHelperOld.validateBatchDetail(datas, profile);
			log.debug("validatedDetails:" + validatedDetails.size());

			UpdBatch batch = new UpdBatch();
			batch.setProfile(profile);
			batch.setAuditDate(new Date());
			batch.setStatus(LookupConstants.__RECORD_STATUS_OPEN);
			batch.setUploadDate(new Date());
			batch.setDetails(validatedDetails);
			batch.setFilename(null);
			batch = generalService.saveUpdBatch(batch);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		if (profile.getArchiveStatus()) {
			for (int i = 0; i < datas.size(); i++) {
				Object[] object = datas.get(i);

				// get last object
				String archieveUniqueCode = (String) object[object.length - 1];
				log.debug("archieveUniqueCode:" + archieveUniqueCode);
				UdArchive archive = new UdArchive();
				archive.setArchiveTime(Calendar.getInstance().getTime());
				archive.setArchiveUniqueCode(archieveUniqueCode);
				archive.setProfile(profile);
				generalService.saveArchive(archive);
			}
		}

		String fullFilename = Play.configuration.getProperty("download.tmp") + profile.getName() + "." + downloadTo;
		log.debug("fullFilename:" + fullFilename);
		String separator;
		if (downloadTo.equalsIgnoreCase(UploadDownloadConstants.EXTENSION_XLS) || downloadTo.equalsIgnoreCase(UploadDownloadConstants.EXTENSION_XLSX)) {
			log.debug("ext is :" + UploadDownloadConstants.EXTENSION_XLS + " or " + UploadDownloadConstants.EXTENSION_XLSX);
			String newfullFilename = UploadDownloadHelper.writeToXls(headers.toArray(new String[0]), datas, fullFilename);
			renderBinary(new File(newfullFilename), profile.getName() + "." + downloadTo);
		}
		if (downloadTo.equals(UploadDownloadConstants.EXTENSION_CSV) || downloadTo.equals(UploadDownloadConstants.EXTENSION_TXT)) {
			separator = profile.getSeparatorCsv();
			if (separator == null) {
				separator = profile.getSeparatorTxt();
			}
			log.debug("separator:" + separator);
			String newfullFilename = UploadDownloadHelper.writeToTxt(headers.toArray(new String[0]), datas, fullFilename, separator);
			renderBinary(new File(newfullFilename), profile.getName() + "." + downloadTo);
		}
		if (downloadTo.equals(UploadDownloadConstants.EXTENSION_XML) || downloadTo.equals(UploadDownloadConstants.EXTENSION_XML)) {
			String newfullFilename = UploadDownloadHelper.writeToXML(profile, datas, fullFilename);
			renderBinary(new File(newfullFilename), profile.getName() + "." + downloadTo);
		}
	}

	@Check("utility.uploadDownload")
	public static void uploadentry(String mode) {
		log.debug("uploadentry. mode: " + mode);

		UpdProfile profile = new UpdProfile();
		profile.setActionType(UploadDownloadConstants.ACTIONTYPE_UPLOAD);
		profile.setStatus(new Boolean(true));
		profile.setIncludeHeader(new Boolean(true));

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_UPLOAD));
		render("UploadDownload/uploadentry.html", sort(profile));
	}

	@Check("utility.uploadDownload")
	public static void viewbatchdetail(String mode) {
		log.debug("viewbatchdetail. mode: " + mode);

		UpdBatch updBatch = new UpdBatch();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_BATCHDETAIL_UPLOAD));
		render("UploadDownload/viewbatchdetail.html", sort(updBatch));
	}

	// no need to check json
	public static void duplicate(Long id) {
		log.debug("duplicate. id: " + id);

		UpdProfile profile = generalService.getUpdProfile(id);
		profile = duplicateHelper(profile);

		renderJSON(sort(profile));
	}

	// no need to check json
	public static void duplicateByName(String name, String action) {
		log.debug("duplicateByName. name: " + name + " action: " + action);

		UpdProfile profile = generalService.getUpdProfileByName(name, action);
		profile.setDetails(generalService.getUpdProfileDetail(profile.getProfileKey()));
		profile = duplicateHelper(profile);

		renderJSON(sort(profile));
	}

	// no need to check local
	private static UpdProfile duplicateHelper(UpdProfile profile) {
		log.debug("duplicateHelper. profile: " + profile);

		for (UpdProfileDetail detail : profile.getDetails()) {
			detail.setUpdProfile(null);
		}
		for (UpdFilter filter : profile.getFilters()) {
			filter.setUpdProfile(null);
		}
		// put columname on cache
		if (profile.getSource() != null) {
			List<String> items = columMap.get(profile.getSource());
			if (items == null || items.isEmpty()) {
				log.debug("isi profile.getSource() >> " + profile.getSource());
				columMap.put(profile.getSource(), applicationService.listColumnNameFromTable(profile.getSource()));
			}
		}
		return profile;
	}

	@Check("utility.uploadDownload")
	public static void entry(String mode) {
		log.debug("entry. mode: " + mode);
	}

	@Check("utility.uploadDownload")
	public static void view(String mode) {
		log.debug("view. mode: " + mode);
	}

	@Check("utility.uploadDownload")
	public static void save(UpdProfile profile, UpdProfileDetail[] pdetails, UpdFilter[] pfilters, String[] archiveField) {
		log.debug("save. profile: " + profile + " pdetails: " + pdetails + " pfilters: " + pfilters + " archiveField: " + archiveField);

		if (profile.getStatus() == null) {
			profile.setStatus(new Boolean(false));
		}
		if (profile.getArchiveStatus() == null) {
			profile.setArchiveStatus(new Boolean(false));
		}
		profile.setSystemTemplete(new Boolean(false));

		if (profile.getIncludeHeader() == null)
			profile.setIncludeHeader(new Boolean(false));

		validation.required("Profile ID", profile.getName());
		validation.required("Profile Name", profile.getDescription());
		validation.required("Profile Ref", profile.getTemplete());

		if (pdetails != null) {
			profile.getDetails().clear();
			Map<String, String> mapKey = new HashMap<String, String>();
			for (int idx = 0; idx < pdetails.length; idx++) {
				UpdProfileDetail currDetail = pdetails[idx];
				String tmpError = "";
				profile.getDetails().add(currDetail);
				log.debug("save() currDetail:" + currDetail.getProfileDetailKey() + " - " + currDetail.getNoSeq());
				if (currDetail.getSourceType().equals(UploadDownloadConstants.FILE_SOURCE_TYPE)) {
					if (currDetail.getSourceField().equals("")) {
						tmpError = "Source Field,";
					}
				} else {
					if (currDetail.getDefaultValue().equals("")) {
						tmpError = "Value,";
					}
				}
				if (currDetail.getTargetField().equals("")) {
					tmpError = tmpError + "Target Field";
				}
				if (tmpError.length() > 0) {
					validation.addError("", tmpError + " at sequence:" + currDetail.getNoSeq() + " is required.");
				}
				if (mapKey.get(currDetail.getSourceField()) == null) {
					mapKey.put(currDetail.getSourceField(), currDetail.getSourceField());
				} else {
					validation.addError("", currDetail.getSourceField() + " is used more than once.");
				}

				if (profile.getIncludeHeader() != null && !profile.getIncludeHeader().booleanValue()) {
					if (!currDetail.getSourceType().equals(UploadDownloadConstants.VAR_SOURCE_TYPE)) {
						;

						String[] srcPattern = currDetail.getSourceField().split("_");
						if (srcPattern.length != 2 || !COLUMN.equals(srcPattern[0]) || "0".equals(srcPattern[1])) {
							validation.addError("", "Source Field must match with pattern COLUMN_{number}, starting with 1");
						} else {
							char[] chars = srcPattern[1].toCharArray();
							for (char c : chars) {
								if (!Character.isDigit(c)) {
									validation.addError("", "Source Field must match with pattern COLUMN_{number}, starting with 1");
									break;
								}
							}
						}
					}
				}
			}
		}

		// get profile ref
		UpdProfile profileTemplate = null;
		if (profile.getTemplete().length() > 0) {
			Long profRefKey = Long.parseLong(profile.getTemplete());
			profileTemplate = generalService.getUpdProfile(profRefKey);
		}
		renderArgs.put("profileTemplate", profileTemplate);

		if (UploadDownloadConstants.ACTIONTYPE_UPLOAD.equals(profile.getActionType())) {

			validation.required("File Type", profile.getFileType());
			// validation.required("Prefix file name", profile.getFilePrefix());
			// ada kasus dimana tidak pake prefix

			if (profile.getFileType() != null) {
				if (profile.getFileType().equalsIgnoreCase(UploadDownloadConstants.EXTENSION_TXT)) {
					validation.required("Separator", profile.getSeparatorTxt());
				}
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_UPLOAD));
				render("UploadDownload/uploadentry.html", sort(profile));

			} else {
				Long id = profile.getProfileKey();
				serializerService.serialize(session.getId(), id, profile);
				String mode = "confirm";
				confirming(id, mode);
			}
		}

		if (UploadDownloadConstants.ACTIONTYPE_DOWNLOAD.equals(profile.getActionType())) {
			if (profile.getStatus() == null)
				profile.setStatus(new Boolean(false));
			if (profile.getArchiveStatus() == null)
				profile.setArchiveStatus(new Boolean(false));
			profile.setSystemTemplete(new Boolean(false));

			if (profile.getIncludeHeader() == null)
				profile.setIncludeHeader(new Boolean(false));

			validation.required("Templete", profile.getTemplete());
			validation.required("Name", profile.getName());
			validation.required("Description", profile.getDescription());
			validation.required("Source", profile.getSource());

			if (pfilters != null) {
				profile.getFilters().clear();
				//Map<String, String> mapKey = new HashMap<String, String>();
				for (int idx = 0; idx < pfilters.length; idx++) {
					UpdFilter currFilter = pfilters[idx];
					profile.getFilters().add(currFilter);
				}
			}
			if (profile.getArchiveStatus()) {
				String allArchiveField = "";
				log.debug("archiveField:" + (archiveField != null ? archiveField.length : "0"));
				if (archiveField != null) {
					for (String tmp_ : archiveField) {
						if (allArchiveField.length() > 0) {
							allArchiveField = allArchiveField + ";" + tmp_;
						} else {
							allArchiveField = tmp_;
						}
					}
				} else {
					validation.required("Archive List", archiveField);
				}
				profile.setArchiveField(allArchiveField);
			}
			if (!validation.hasErrors()) {
				Long id = profile.getProfileKey();
				serializerService.serialize(session.getId(), id, profile);
				String mode = "confirm";
				confirming(id, mode);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_DOWNLOAD));
				render("UploadDownload/downloadentry.html", sort(profile));
			}

		}
	}

	@Check("utility.uploadDownload")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		UpdProfile profile = generalService.getUpdProfile(id);
		profile.setDetails(generalService.getUpdProfileDetail(id));
		if (profile.getSystemTemplete() != null) {
			if (profile.getSystemTemplete()) {
				String action = profile.getActionType();
				list(action);
			}
		}
		log.debug("profile.getActionType():" + profile.getActionType());
		log.debug("profile.getDetails():" + profile.getDetails().size());
		if (UploadDownloadConstants.ACTIONTYPE_UPLOAD.equals(profile.getActionType())) {
			renderArgs.put("mode", "save");

			// get profile ref
			UpdProfile profileTemplate = null;
			if (profile.getTemplete() != null) {
				Long profRefKey = Long.parseLong(profile.getTemplete());
				profileTemplate = generalService.getUpdProfile(profRefKey);
			}
			renderArgs.put("profileTemplate", profileTemplate);

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_UPLOAD));
			render("UploadDownload/uploadentry.html", sort(profile));
		}
		if (UploadDownloadConstants.ACTIONTYPE_DOWNLOAD.equals(profile.getActionType())) {
			// get profile ref
			UpdProfile profileTemplate = null;
			if (profile.getTemplete() != null) {
				Long profRefKey = Long.parseLong(profile.getTemplete());
				profileTemplate = generalService.getUpdProfile(profRefKey);
			}

			if (profile.getStatus() == null) {
				profile.setStatus(new Boolean(false));
			}
			List<String> listArchive = new ArrayList<String>();
			if (profile.getArchiveStatus() == null) {
				profile.setArchiveStatus(new Boolean(false));
			} else {
				if (profile.getArchiveStatus()) {
					String archiveField = profile.getArchiveField();
					if (archiveField != null) {
						String[] tmpField_ = archiveField.split(";");
						for (String tmp_ : tmpField_) {
							listArchive.add(tmp_);
						}
					}
				}
			}

			renderArgs.put("profileTemplate", profileTemplate);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_DOWNLOAD));
			render("UploadDownload/downloadentry.html", sort(profile), listArchive);
		}
	}

	@Check("utility.uploadDownload")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		UpdProfile profile = serializerService.deserialize(session.getId(), id, UpdProfile.class);

		// get profile ref
		UpdProfile profileTemplate = null;
		if (profile.getTemplete() != null) {
			Long profRefKey = Long.parseLong(profile.getTemplete());
			profileTemplate = generalService.getUpdProfile(profRefKey);
		}
		renderArgs.put("profileTemplate", profileTemplate);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_UPLOAD));
		if (profile.getActionType().equals(UploadDownloadConstants.ACTIONTYPE_DOWNLOAD)) {

			List<String> listArchive = new ArrayList<String>();
			if (profile.getArchiveStatus()) {
				String archiveField = profile.getArchiveField();
				if (archiveField != null) {
					String[] archiveFieldSplit = archiveField.split(";");
					for (String tmp_ : archiveFieldSplit) {
						listArchive.add(tmp_);
					}
				}
			}

			render("UploadDownload/downloadentry.html", sort(profile), mode, listArchive);
		} else if (profile.getActionType().equals(UploadDownloadConstants.ACTIONTYPE_UPLOAD)) {
			render("UploadDownload/uploadentry.html", sort(profile), mode);
		}
	}

	// no need to check json
	public static void populateDownload(Long id, String[] arrFilter) {
		log.debug("populateDownload. id: " + id + " arrFilter: " + arrFilter);

		StringBuffer idFilter = new StringBuffer();
		if (!arrFilter[0].isEmpty()) {
			for (String filter : arrFilter) {
				idFilter.append(filter).append(",");
			}
			idFilter.deleteCharAt(idFilter.length() - 1);
		}

		List<Object[]> datas = generalService.populate(id, arrFilter);
		UpdProfile profile = generalService.getUpdProfile(id);
		profile.setDetails(generalService.getUpdProfileDetail(id));
		datas = UploadDownloadHelperOld.format(profile, datas);

		// reset each parent to avoid reference in the json
		for (UpdProfileDetail d : profile.getDetails()) {
			d.setUpdProfile(null);
		}
		for (UpdFilter f : profile.getFilters()) {
			f.setUpdProfile(null);
		}

		log.debug("size of Data on populate = " + (datas != null ? datas.size() : null));
		profile.setDatas(datas);
		renderJSON(profile);
	}

	// no need to check json
	public static void previewBatch(Long batchId) {
		log.debug("previewBatch. batchId: " + batchId);

		Map<String, Object> map = new HashMap<String, Object>();
		UpdBatch batch = generalService.getUpdBatch(batchId);
		UpdProfile profile = batch.getProfile();

		List<String> profileHeaderList = new ArrayList<String>();
		List<UpdProfileDetail> details = profile.getDetails();
		for (int xc = 0; xc < details.size(); xc++) {
			if (profile.getActionType().equals(UploadDownloadConstants.ACTIONTYPE_DOWNLOAD)) {
				profileHeaderList.add(details.get(xc).getSourceField());
			} else {
				profileHeaderList.add(details.get(xc).getTargetField());
			}
		}

		map.put("BATCHDETAIL", batch.getDetails());
		map.put("PROFILE_HEADER", profileHeaderList);

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(UpdBatchDetail.class, new UpdBatchDetailsSerializer());
		String dataJSONParameter = gsonBuilder.create().toJson(map);
		render(dataJSONParameter);
	}

	// no need to check local
	private static String getDuplicateHeader(List<String> headers) {
		Map<String, String> map = new HashMap<String, String>();
		for (String header : headers) {
			String result = map.get(header);
			if (result == null)
				map.put(header, header);
			else {
				map.clear();
				return header;
			}
		}

		map.clear();
		return null;
	}

	private static List<Object[]> filterData(List<Object[]> objects, List<Integer> sources) {
		log.debug("objects " + objects.size());
		for (Integer integer : sources) {
			log.debug("Index " + integer);
		}
		List<Object[]> filtered = new ArrayList<Object[]>();

		for (Object[] obj : objects) {
			Object[] row = new Object[sources.size()];
			for (int i = 0; i < sources.size(); i++) {
				row[i] = obj[sources.get(i)];
			}
			filtered.add(row);
		}

		return filtered;
	}

	// no need to check json
	public static void previewUpload() {
		log.debug("previewUpload. ");

		Map<String, String> errors = new HashMap<String, String>();
		Map<String, Object> storeMap = new HashMap<String, Object>();
		List<String> sources = new ArrayList<String>();
		List<String> targets = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		List<Integer> sourceIdxs = new ArrayList<Integer>();
		List<Object[]> datauploaded = null;
		List<UpdBatchDetail> batchDetail = null;
		UpdBatch batch = new UpdBatch();

		File uploadedFile = params.get("file", File.class);
		String profileKey = params.get("profileKey", String.class);

		log.debug("uploadedFile " + uploadedFile);
		log.debug("profileKey " + profileKey);

		String extension = "";
		UpdProfile profile = null;

		// Validate Uploaded File
		if (uploadedFile == null) {
			errors.put("file", "Required file is not submitted");
		} else {
			log.debug("Uploaded file : " + uploadedFile.getAbsolutePath());

			extension = uploadedFile.getName().split("\\.")[1];
			if ((!extension.equalsIgnoreCase(UploadDownloadConstants.EXTENSION_XLS)) && (!extension.equalsIgnoreCase(UploadDownloadConstants.EXTENSION_CSV)) && (!extension.equalsIgnoreCase(UploadDownloadConstants.EXTENSION_TXT)) && (!extension.equalsIgnoreCase(UploadDownloadConstants.EXTENSION_XLSX)) && (!extension.equalsIgnoreCase(UploadDownloadConstants.EXTENSION_DBF))) {

				errors.put("type", "This type of file is not supported for execution");
			}
		}

		// Validate Profile
		if ((profileKey == null) || (profileKey.trim().length() == 0)) {
			errors.put("template", "Required template is not submitted");
		} else {
			log.debug("Profile Key : " + profileKey);

			profile = generalService.getUpdProfile(Long.valueOf(profileKey));
			if (profile == null) {
				errors.put("template", "Templete is not registered for key " + profileKey);
			} else {
				if (profile.getSeparatorTxt() == null)
					profile.setSeparatorTxt("");
				if (profile.getIncludeHeader() == null)
					errors.put("template", "Include header in profile is null, please fill inculde header value");
				if (profile.getFileType() == null || "".equals(profile.getFileType()))
					errors.put("template", "File type in profile is empty, please fill file type");
				if (!extension.equalsIgnoreCase(profile.getFileType())) {
					errors.put("type", "This type of file is not matched with profile");
				}
				if (extension.equals(UploadDownloadConstants.EXTENSION_CSV) && !",".equals(profile.getSeparatorTxt())) {
					errors.put("prefix", "Invalid file separator delimeter");
				}
			}
		}

		// Validate for Profile and uploaded File
		if (profile != null && uploadedFile != null) {
			if (profile.getFilePrefix() != null && !"".equals(profile.getFilePrefix())) {
				if (!uploadedFile.getName().startsWith(profile.getFilePrefix())) {
					errors.put("prefix", "This file prefix does not match with required");
				}
			}
		}

		if (errors.isEmpty()) {
			try {
				// Checking for second validation
				//List<UpdProfileDetail> details = profile.getDetails();

				try {
					datauploaded = extractFile(uploadedFile, profile.getSeparatorTxt(), profile);
				} catch (Exception ex) {
					log.debug("Error extracting file ", ex);
					throw new Exception("Error extracting file");
				}

				log.debug("Size of raw data uploaded: " + datauploaded.size() + " row[s]");

				if (datauploaded.size() == 0 || (profile.getIncludeHeader() && datauploaded.size() == 1)) {
					errors.put("file", "empty content file upload");
				}

				log.debug("Is Include Header : " + profile.getIncludeHeader());
				if (errors.isEmpty()) {
					if (profile.getIncludeHeader()) {
						// validasi berdasarkan nama column

						// now compare header in file with required header in
						// profile
						for (UpdProfileDetail upd : profile.getDetails()) {
							if (upd.getSourceType().equals(UploadDownloadConstants.VAR_SOURCE_TYPE))
								continue;

							boolean exist = false;
							for (int x = 0; x < datauploaded.get(0).length; x++) {
								// logger.debug("COMPARE FIELD "+datauploaded.get(0)[x]+"   "+upd.getSourceField());
								if (datauploaded.get(0)[x].equals(upd.getSourceField())) {
									exist = true;
									sources.add(upd.getSourceField());
									targets.add(upd.getTargetField());
									types.add(upd.getDataType().getLookupCode());
									sourceIdxs.add(new Integer(x));
								}
							}

							String duplicateHeader = getDuplicateHeader(sources);
							if (duplicateHeader != null) {
								errors.put("header", "Duplicate header exist for column " + duplicateHeader);
							}

							if (!exist) {
								errors.put("header", "Invalid mapping for target " + upd.getSourceField());
							}
						}

						datauploaded = filterData(datauploaded, sourceIdxs);

						// remove header
						datauploaded.remove(0);

					} else {
						// PENTING saat masukin yang header exist == false,
						// harus ada validasi sourcenya yaitu COLUMN_{number}
						// COLUMN_1 // ini format yang benar
						// validasi berdasarkan index column contoh COL_1, cek
						// ada gax di file colum ke 1

						// validasi berdasarkan nama column

						// now compare header in file with required header in
						// profile
						for (UpdProfileDetail upd : profile.getDetails()) {
							if (upd.getSourceType().equals(UploadDownloadConstants.VAR_SOURCE_TYPE))
								continue;

							String[] arr = upd.getSourceField().split("\\_");
							int index = Integer.valueOf(arr[1]);

							if (index > datauploaded.get(0).length) {
								errors.put("header", "Invalid mapping for target " + upd.getSourceField());
							} else {
								sources.add(upd.getSourceField());
								targets.add(upd.getTargetField());
								types.add(upd.getDataType().getLookupCode());
								sourceIdxs.add(index - 1); // di kurangi 1
															// soalnya mulainya
															// secara java
															// mulainya dari 0
							}
						}

						datauploaded = filterData(datauploaded, sourceIdxs);
					}

					if (errors.size() == 0) {
						batchDetail = UploadDownloadHelperOld.validateBatchDetail(datauploaded, profile);
					}
				}
			} catch (Exception e) {
				log.error("error parsing file " + uploadedFile.getName(), e);
			}
		}

		if (errors.isEmpty()) {
			log.debug("Batch Detail : " + (batchDetail == null ? "null" : batchDetail.size() + ""));
			batch.setProfile(profile);
			batch.setAuditDate(new Date());
			batch.setStatus(LookupConstants.__RECORD_STATUS_OPEN);
			batch.setUploadDate(new Date());
			batch.setFilename(uploadedFile.getName());
			batch.setUserId(session.get(UIConstants.SESSION_USER_KEY));
			batch.setDetails(batchDetail);
			batch = generalService.saveUpdBatch(batch);

			sources.add(ERROR);
			targets.add(ERROR);
			types.add("String");

			log.debug("Batch saved with key : " + batch.getBatchKey());
			storeMap.put("BATCHID", batch.getBatchKey());
			storeMap.put("BATCHDETAIL", batch.getDetails());
			storeMap.put("SOURCE_HEADER", sources);
			storeMap.put("PROFILE_HEADER", targets);
			storeMap.put("PROFILE_TYPE", types);
		} else {
			storeMap.put("ERROR", errors);
		}

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(UpdBatchDetail.class, new UpdBatchDetailsSerializer());
		String dataJSONParameter = gsonBuilder.create().toJson(storeMap);
		render(dataJSONParameter);
	}

	// no need to check json
	public static void previewUploadPaging(Paging page, UploadDownloadParameters param) {
		log.debug("previewUploadPaging. page: " + page + " param: " + param);

		page.addParams(Helper.searchAll("(c.rawMessage||c.errorDescription)"), Paging.LIKE, "%" + page.getsSearch() + "%");
		if (param.isErrorOnly()) {
			page.addParams("c.errorDescription", Paging.ISNOTNULL, null);
		}

		page = generalService.previewPaging(page, param.getBatchNo());
		renderJSON(page);
	}

	@Check("utility.uploadDownload")
	public static void populateUpload(UpdBatch batch, File file) {
		log.debug("populateUpload. batch: " + batch + " file: " + file);

		file = params.get("file", File.class);
		log.debug("FILE = " + file);

		// validate

		String extension = "";
		if (file != null) {
			extension = file.getName().split("\\.")[1];
			if ((!extension.equals(UploadDownloadConstants.EXTENSION_XLS)) && (!extension.equals(UploadDownloadConstants.EXTENSION_CSV)) && (!extension.equals(UploadDownloadConstants.EXTENSION_TXT)) && (!extension.equals(UploadDownloadConstants.EXTENSION_XLSX))) {
				validation.addError("", "This type of file doesn't match for execution");
			}
		}

		validation.required("Template is", batch.getProfile().getProfileKey());
		validation.required("File is", file);

		if (validation.hasErrors()) {
			renderArgs.put("templetes", generalService.findUpdProfilenames(UploadDownloadConstants.ACTIONTYPE_UPLOAD));
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROCESS_UPLOAD));
			render("Uploaddownload/uploaddata.html", batch);
		}

		UpdProfile profile = generalService.getUpdProfile(batch.getProfile().getProfileKey());

		batch.setProfile(new UpdProfile(batch.getProfile().getProfileKey()));
		batch.setAuditDate(new Date());
		batch.setStatus(LookupConstants.__RECORD_STATUS_OPEN);
		batch.setUploadDate(new Date());

		String separator;
		if (extension.equals(UploadDownloadConstants.EXTENSION_CSV)) {
			if (profile.getSeparatorCsv() != null)
				separator = profile.getSeparatorCsv();
			else
				separator = ",";
		} else {
			if (profile.getSeparatorTxt() != null)
				separator = profile.getSeparatorTxt();
			else
				separator = ",";
		}
		try {
			List<Object[]> datauploaded = extractFile(file, separator, profile);

			boolean isIncludeHeader = false;
			if (profile.getIncludeHeader() != null) {
				isIncludeHeader = profile.getIncludeHeader();
			} else {
				isIncludeHeader = false;
			}

			if (isIncludeHeader) {
				// remove header
				datauploaded.remove(0);
			}
			log.debug("SIZE OF DATA UPLOADED = " + datauploaded.size());
			List<UpdBatchDetail> details = UploadDownloadHelperOld.validateBatchDetail(datauploaded, profile);
			batch.setDetails(details);
			batch.setFilename(file.getPath());
			batch = generalService.saveUpdBatch(batch);
			batch.setProfile(null);
			for (UpdBatchDetail d : batch.getDetails()) {
				d.setUpdBatch(null);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		renderArgs.put("templetes", generalService.findUpdProfilenames(UploadDownloadConstants.ACTIONTYPE_UPLOAD));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROCESS_UPLOAD));

		// fetchBatchDesc(batch.getBatchKey());
		render("UploadDownload/uploadData.html", batch);
	}

	@Check("utility.uploadDownload")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		UpdProfile profile = serializerService.deserialize(session.getId(), id, UpdProfile.class);

		log.debug("confirming() profile.getDetails(): " + (profile.getDetails() == null ? "null" : profile.getDetails().size()));
		log.debug("ActionType " + profile.getActionType());
		log.debug("profile.getStatus():" + profile.getStatus());
		log.debug("profile.getArchiveField():" + profile.getArchiveField());

		// get profile ref
		UpdProfile profileTemplate = null;

		Long profRefKey = Long.parseLong(profile.getTemplete());
		profileTemplate = generalService.getUpdProfile(profRefKey);
		renderArgs.put("profileTemplate", profileTemplate);

		if (profile.getActionType().equalsIgnoreCase(UploadDownloadConstants.ACTIONTYPE_UPLOAD)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_UPLOAD));
			render("UploadDownload/uploadconfirm.html", sort(profile));
		} else if (profile.getActionType().equalsIgnoreCase(UploadDownloadConstants.ACTIONTYPE_DOWNLOAD)) {

			List<String> listArchive = new ArrayList<String>();
			if (profile.getArchiveStatus()) {
				String archiveField = profile.getArchiveField();
				if (archiveField != null) {
					String[] archiveFieldSplit = archiveField.split(";");
					for (String tmp_ : archiveFieldSplit) {
						listArchive.add(tmp_);
					}
				}
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_DOWNLOAD));
			render("UploadDownload/downloadconfirm.html", sort(profile), listArchive);
		}
	}

	@Check("utility.uploadDownload")
	public static void confirm(UpdProfile profile, String mode, UpdProfileDetail[] pdetails, UpdFilter[] pfilters) {
		log.debug("confirm. profile: " + profile + " mode: " + mode + " pdetails: " + pdetails + " pfilters: " + pfilters);

		// get profile ref
		UpdProfile profileTemplate = null;
		if (profile.getTemplete().length() > 0) {
			Long profRefKey = Long.parseLong(profile.getTemplete());
			profileTemplate = generalService.getUpdProfile(profRefKey);
		}
		renderArgs.put("profileTemplate", profileTemplate);

		// check unique profile name
		log.debug("profile.getProfileKey():" + profile.getProfileKey());
		log.debug("profile.getStatus():" + profile.getStatus());
		log.debug("profile.getArchiveField():" + profile.getArchiveField());
		UpdProfile profile2 = generalService.getUpdProfileByName(profile.getName(), profile.getActionType());
		log.debug("profile2:" + (profile2 == null ? "null" : profile2.getName()));
		if (profile2 != null && profile.getProfileKey() == null) {
			validation.addError("", "Profile ID required to be unique, this ID already exist.");
		}

		if (pdetails != null) {
			profile.getDetails().clear();
			for (int idx = 0; idx < pdetails.length; idx++) {
				UpdProfileDetail currDetail = pdetails[idx];
				currDetail.setSystemField(pdetails[idx].getSystemField() == null ? Boolean.FALSE : Boolean.TRUE);
				log.debug("currDetail::" + currDetail.getProfileDetailKey() + " - " + currDetail.getNoSeq());
				profile.getDetails().add(currDetail);
			}
		}

		if (profile.getActionType().equals(UploadDownloadConstants.ACTIONTYPE_UPLOAD)) {

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_UPLOAD));
				render("UploadDownload/uploadentry.html", sort(profile));
			} else {
				try {
					generalService.saveUpdProfile(sort(profile));
					list(profile.getActionType());
				} catch (Exception ex) {
					log.error("err in confirm", ex);
					validation.addError("", ex.getMessage());
					renderArgs.put("confirming", true);
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_UPLOAD));
					if (ex.getMessage().equals(ExceptionConstants.DATA_NOCHANGE)) {
						render("UploadDownload/uploadconfirm.html", sort(profile));
					}
					render("UploadDownload/uploadentry.html", sort(profile), pdetails);
				}
			}
		} else if (profile.getActionType().equals(UploadDownloadConstants.ACTIONTYPE_DOWNLOAD)) {
			log.debug("pfilters:" + (pfilters == null ? null : pfilters.length));
			if (pfilters != null) {
				profile.getFilters().clear();
				for (int idx = 0; idx < pfilters.length; idx++) {
					UpdFilter currFilter = pfilters[idx];
					log.debug("currFilter::" + currFilter.getFieldName() + " - " + currFilter.getNoSeq());
					profile.getFilters().add(currFilter);
				}
			}
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_DOWNLOAD));
				render("UploadDownload/downloadentry.html", sort(profile));
			} else {
				try {
					generalService.saveUpdProfile(sort(profile));
					list(profile.getActionType());
				} catch (Exception ex) {
					log.error("err in confirm", ex);
					validation.addError("", ex.getMessage());
					renderArgs.put("confirming", true);
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_PROFILE_DOWNLOAD));
					if (ex.getMessage().equals(ExceptionConstants.DATA_NOCHANGE)) {
						render("UploadDownload/downloadconfirm.html", sort(profile));
					}
					render("UploadDownload/downloadentry.html", sort(profile), pdetails);
				}
			}
		}
	}

	@Check("utility.uploadDownload")
	public static void executeupload(Long id) {
		log.debug("executeupload. id: " + id);

		List<UpdBatch> batch = generalService.findUpdBatch();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_EXECUTE_UPLOAD));
		render("UploadDownload/executeupload.html", batch);
	}

	@Check("utility.uploadDownload")
	public static void runupload(Long id) {
		log.debug("runupload. id: " + id);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_RUN_UPLOAD_DEPRICATED));
		render("UploadDownload/runupload.html");
	}

	// no need to check json
	public static void realUpload(Long id) {
		log.debug("realUpload. id: " + id);

		UpdBatch batch = generalService.getUpdBatch(id);
		// String batchNo = "Batch-"+Calendar.getInstance().getTimeInMillis();
		String batchNo = String.valueOf(id);
		for (UpdBatchDetail batchdetail : batch.getDetails()) {
			try {
				String errMsg = batchdetail.getErrorDescription();
				if (errMsg != null && errMsg.trim().length() > 0)
					continue;

				if (UploadDownloadConstants.PROCESS_INVESTOR.equals(batch.getProfile().getProcess())) {
					CfMaster master = uploadInvestorService.createCfMaster(batchdetail, batch.getProfile());
					uploadInvestorService.process(master, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY), batchNo);
					// clear out error message
					generalService.errorUpdBatchDetail(batchdetail.getBatchDetailId(), "");
				}
				if (UploadDownloadConstants.PROCESS_SUBSCRIBE.equals(batch.getProfile().getProcess())) {
					RgTrade rgTrade = uploadSubscriberService.createSubscribe(batchdetail, batch.getProfile());
					RgTrade newRgTrade = uploadSubscriberService.process(rgTrade, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY), batchNo);
					log.debug("try to approve trade:" + newRgTrade.getTradeKey());
					RgTrade approvedRgTrade = uploadSubscriberService.approve(newRgTrade, batchNo, session.get(UIConstants.SESSION_USERNAME));
					log.debug("done approving trade:" + approvedRgTrade.getTradeKey());
				}
				if (UploadDownloadConstants.PROCESS_REDEEM.equals(batch.getProfile().getProcess())) {
					uploadRedeemService.createRedeem(batchdetail, batch.getProfile());
				}
				if (UploadDownloadConstants.PROCESS_SWITCHING.equals(batch.getProfile().getProcess())) {
					uploadRedeemService.createRedeem(batchdetail, batch.getProfile());
				}
				if (UploadDownloadConstants.PROCESS_EQUITY.equals(batch.getProfile().getProcess())) {
				}
				if (UploadDownloadConstants.PROCESS_MARKET_PRICE.equals(batch.getProfile().getProcess())) {
					uploadMarketPriceService.createProcessApprove(batchNo, batchdetail, batch.getProfile(), session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
				}

				if (UploadDownloadConstants.PROCESS_UPL_TRN_SWITCHING.equals(batch.getProfile().getProcess())) {
				}

			} catch (Exception e) {
				log.error("err executing upload investor ", e);
				String errMsg = e.getMessage();
				if (e instanceof RemoteAccessException) {
					errMsg = "Process time out, please try again";
				}
				log.debug("Updating :" + batchdetail.getBatchDetailId() + " with ERROR = " + e.getMessage());
				if (errMsg == null) {
					errMsg = "";
				}
				generalService.errorUpdBatchDetail(batchdetail.getBatchDetailId(), "Error " + (errMsg.length() > 500 ? errMsg.substring(0, 400) : errMsg));
			}
		}
		Map<String, Long> status = generalService.getExecutionResult(batch.getBatchKey());
		status.put("BATCHNO", id);
		renderJSON(status);
	}

	// no need to check json
	public static void fetchBatch(Long id) {
		log.debug("fetchBatch. id: " + id);

		UpdBatch batch = generalService.getUpdBatch(id);

		String colums = null;
		for (UpdProfileDetail d : batch.getProfile().getDetails()) {
			d.setUpdProfile(null);
			if (colums == null)
				colums = d.getTargetField();
			else
				colums += "," + d.getTargetField();
		}

		for (UpdBatchDetail d : batch.getDetails()) {
			d.setUpdBatch(null);
		}

		List<Object[]> contents = generalService.populateUploadData(batch.getProfile().getSource(), colums, batch.getBatchKey());
		Map<String, Long> status = generalService.getExecutionResult(batch.getBatchKey());
		batch.setSizeSuccess(status.get("SUCCESS").intValue());
		batch.setSizeError(status.get("FAIL").intValue());

		batch.setContents(contents);

		renderJSON(batch);
	}

	// no need to check json
	public static void fetchBatchDesc(Long id) {
		log.debug("fetchBatchDesc. id: " + id);

		UpdBatch batch = generalService.getUpdBatch(id);

		String colums = null;
		for (UpdProfileDetail d : batch.getProfile().getDetails()) {
			d.setUpdProfile(null);
			if (colums == null)
				colums = d.getTargetField();
			else
				colums += "," + d.getTargetField();
		}

		for (UpdBatchDetail d : batch.getDetails()) {
			d.setUpdBatch(null);
		}

		Map<String, Long> status = generalService.getExecutionResult(batch.getBatchKey());
		batch.setSizeSuccess(status.get("SUCCESS").intValue());
		batch.setSizeError(status.get("FAIL").intValue());

		renderJSON(batch);
	}

	// no need to check local
	private static List<Object[]> extractFile(File file, String separator, UpdProfile upd) throws Exception {
		log.debug("extractFile. file: " + file + " separator: " + separator + " upd: " + upd);

		List<Object[]> dataUploaded = new ArrayList<Object[]>();
		String extension = file.getName().split("\\.")[1];

		if (extension.equals(UploadDownloadConstants.EXTENSION_CSV) || extension.equals(UploadDownloadConstants.EXTENSION_TXT)) {
			try {
				// CSVReader csvReader = null;
				// csvReader = new CSVReader(new FileReader(file), separator);
				//
				// Object[] row = null;
				// while ((row = csvReader.readNext())!=null){
				// dataUploaded.add(row);
				// }
				// csvReader.close();

				InputStreamReader inputstream = new InputStreamReader(new FileInputStream(file));
				BufferedReader reader = new BufferedReader(inputstream);

				String line;
				while ((line = reader.readLine()) != null) {
					String[] row = line.split("\\" + separator, -1);
					skipZeroRow(dataUploaded, row);
				}

				inputstream.close();
				reader.close();

			} catch (IOException e) {
				log.error("Error reading file:" + file.getName(), e);
			}
		}

		if (extension.equals(UploadDownloadConstants.EXTENSION_XLS)) {
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				POIFSFileSystem myFileSystem = new POIFSFileSystem(fileInputStream);
				HSSFWorkbook myWorkbook = new HSSFWorkbook(myFileSystem);

				HSSFSheet mySheet = myWorkbook.getSheetAt(0);
				Iterator<?> rowIterator = mySheet.rowIterator();
				while (rowIterator.hasNext()) {
					HSSFRow myRow = (HSSFRow) rowIterator.next();

					List<String> listString = new ArrayList<String>();
					for (short i = 0; i < myRow.getLastCellNum(); i++) {
						HSSFCell cell = myRow.getCell(i);
						String value = "";

						if (cell == null) {
							cell = myRow.createCell(i);
							cell.setCellValue(" ");
						}
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC:
							int dataNum = (int) cell.getNumericCellValue();
							value = String.valueOf(dataNum);
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							value = cell.getStringCellValue();
							break;
						case HSSFCell.CELL_TYPE_BLANK:
							value = "";
							break;
						case HSSFCell.CELL_TYPE_ERROR:
							value = "";
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
							value = "";
							break;
						default:
							value = cell.getStringCellValue();
							break;
						}
						listString.add(value);
					}
					skipZeroRow(dataUploaded, listString);
				}
			} catch (IOException e) {
				log.error("Error reading file:" + file.getName(), e);
			}
		}

		if (extension.equals(UploadDownloadConstants.EXTENSION_XLSX)) {
			try {
				InputStream inputStream = new FileInputStream(file);
				XSSFWorkbook myWorkbookx = new XSSFWorkbook(inputStream);
				XSSFSheet mySheetx = myWorkbookx.getSheetAt(0);
				Iterator<?> rowIterator = mySheetx.rowIterator();
				while (rowIterator.hasNext()) {
					XSSFRow myRowx = (XSSFRow) rowIterator.next();

					List<String> listString = new ArrayList<String>();
					for (short i = 0; i < myRowx.getLastCellNum(); i++) {
						XSSFCell cellx = myRowx.getCell(i);

						if (cellx == null) {
							cellx = myRowx.createCell(i);
							cellx.setCellValue(" ");
						}
						String value = "";
						switch (cellx.getCellType()) {
						case XSSFCell.CELL_TYPE_NUMERIC:
							int dataNum = (int) cellx.getNumericCellValue();
							value = String.valueOf(dataNum);
							break;
						case XSSFCell.CELL_TYPE_BOOLEAN:
							value = cellx.getStringCellValue();
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							value = "";
							break;
						case XSSFCell.CELL_TYPE_ERROR:
							value = "";
							break;
						case XSSFCell.CELL_TYPE_FORMULA:
							value = "";
							break;
						default:
							value = cellx.getStringCellValue();
							break;
						}
						listString.add(value);

					}
					skipZeroRow(dataUploaded, listString);
				}
			} catch (IOException e) {
				log.error("Error reading file:" + file.getName(), e);
			}
		}

		if (extension.equalsIgnoreCase(UploadDownloadConstants.EXTENSION_DBF)) {
			try {
				// create a DBFReader object
				InputStream inputStream = new FileInputStream(file); // take dbf
																		// file
																		// stream
				DBFReader reader = new DBFReader(inputStream);

				// get the field count if you want for some reasons like the
				int numberOfFields = reader.getFieldCount();

				// use this count to fetch all field information
				List<String> listHeader = new ArrayList<String>();
				for (int i = 0; i < numberOfFields; i++) {
					if (upd.getIncludeHeader() != null && upd.getIncludeHeader().booleanValue()) {
						DBFField field = reader.getField(i);
						listHeader.add(field.getName());
					} else {
						listHeader.add(COLUMN + "_" + (i + 1));
					}
				}
				skipZeroRow(dataUploaded, listHeader);

				// Now, lets us start reading the rows
				Object[] rowObjects;

				while ((rowObjects = reader.nextRecord()) != null) {
					List<String> listData = new ArrayList<String>();
					for (int i = 0; i < rowObjects.length; i++) {
						log.debug("tipe  " + rowObjects[i].getClass().getName());
						if (rowObjects[i] != null) {
							if (rowObjects[i] instanceof Date) {
								UpdProfileDetail pd = getProfileDetail(upd, listHeader.get(i));
								if (pd != null) {
									listData.add((new SimpleDateFormat(pd.getFormatType().getLookupDescription())).format((Date) rowObjects[i]));
								} else {
									listData.add(rowObjects[i].toString());
								}
							} else if (rowObjects[i] instanceof Double) {
								UpdProfileDetail pd = getProfileDetail(upd, listHeader.get(i));

								if (pd != null) {
									DecimalFormat df = null;
									if (pd.getFormatType() == null) {
										df = new DecimalFormat();
									} else {
										df = new DecimalFormat(pd.getFormatType().getLookupDescription());
									}

									df.setGroupingUsed(false);
									listData.add(df.format(rowObjects[i]));
								} else {
									listData.add(rowObjects[i].toString());
								}
							} else {
								listData.add(rowObjects[i].toString());
							}
						}
						// String tmpValue = "";
						// if (rowObjects[i] != null){
						// UpdProfileDetail currProfDet = details.get(i);
						// tmpValue = rowObjects[i].toString();
						//
						// if(
						// currProfDet.getDataType().equalsIgnoreCase(UploadDownloadConstants.TYPEDATA_DATE)){
						// SimpleDateFormat sdf = new SimpleDateFormat(
						// currProfDet.getFormatType() );
						// Date tmpDate = (Date) rowObjects[i];
						// tmpValue = sdf.format(tmpDate);
						// } else if (
						// currProfDet.getDataType().equalsIgnoreCase(UploadDownloadConstants.TYPEDATA_NUMBER)
						// ) {
						// DecimalFormat df = null;
						// if( currProfDet.getFormatType() == null ){
						// df = new DecimalFormat();
						// }else{
						// df = new DecimalFormat(currProfDet.getFormatType());
						// }
						// df.setGroupingUsed(false);
						// log.debug("formating data "+rowObjects[i]);
						// tmpValue = df.format(rowObjects[i]);
						// }
						// }

					}
					skipZeroRow(dataUploaded, listData);
				}

				// By now, we have itereated through all of the rows
				inputStream.close();
			} catch (DBFException e) {
				log.error("error reading dbf ", e);
			} catch (IOException e) {
				log.error("error reading dbf IO", e);
			}
		}

		return dataUploaded;
	}

	// no need to check local
	private static UpdProfileDetail getProfileDetail(UpdProfile upd, String columnName) {
		for (UpdProfileDetail updd : upd.getDetails()) {
			if (updd.getSourceField() != null && updd.getSourceField().equals(columnName)) {
				return updd;
			}
		}
		return null;
	}

	// no need to check local
	private static void skipZeroRow(List<Object[]> dataUploaded, List<String> listString) {
		int countEmpty = 0;
		for (String aCell : listString) {
			if ((aCell == null) || (aCell.equals("")) || (aCell.trim().length() == 0)) {
				countEmpty++;
			}
		}
		if (countEmpty < listString.size()) {
			dataUploaded.add(listString.toArray());
		}
	}

	// no need to check local
	private static void skipZeroRow(List<Object[]> dataUploaded, String[] arrString) {
		if (arrString != null && arrString.length > 0) {
			List<String> listString = new ArrayList<String>();
			for (String string : arrString) {
				listString.add(string);
			}

			if (!listString.isEmpty())
				skipZeroRow(dataUploaded, listString);
		}
	}

	// no need to check local
	private static UpdBatch sort(UpdBatch batch) {
		class UpdBatchComparator implements Comparator<UpdBatchDetail> {

			@Override
			public int compare(UpdBatchDetail o1, UpdBatchDetail o2) {
				if (o1 == null || o2 == null) {
					// still dont know why some list value become null/larger
					// size from original
					return 0;
				}
				return o1.getBatchDetailId().compareTo(o2.getBatchDetailId());
			}
		}
		Collections.sort(batch.getDetails(), new UpdBatchComparator());
		return batch;
	}

	// no need to check private
	private static UpdProfile sort(UpdProfile profile) {
		class UpdDetailComparator implements Comparator<UpdProfileDetail> {

			@Override
			public int compare(UpdProfileDetail o1, UpdProfileDetail o2) {
				if (o1 == null || o2 == null) {
					// still dont know why some list value become null/larger
					// size from original
					return 0;
				}
				return o1.getNoSeq().compareTo(o2.getNoSeq());
			}
		}
		Collections.sort(profile.getDetails(), new UpdDetailComparator());
		return profile;
	}

	// no need to check file
	public static void saveErrorBatch(Long batchId) {
		log.debug("saveErrorBatch. batchId: " + batchId);

		String errorMsgTitle = "Error Message";
		// data
		UpdBatch batch = generalService.getUpdBatch(batchId);
		UpdProfile profile = batch.getProfile();

		// create new workbook
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("error");

		List<String> headerList = new ArrayList<String>();

		// prepare data for header
		// header always first index = 0
		Row headerRow = sheet.createRow(0);

		// set first column is Error Message
		headerList.add(errorMsgTitle);
		Cell errCell = headerRow.createCell(headerList.size() - 1);
		errCell.setCellValue(headerList.get(headerList.size() - 1));

		for (int idx = 0; idx < profile.getDetails().size(); idx++) {
			UpdProfileDetail detail = profile.getDetails().get(idx);
			headerList.add(detail.getSourceField());
			// since error message is at cell 0, start batch column name with
			// idx=1
			Cell cell = headerRow.createCell((idx + 1));
			if (detail.getSourceType().equalsIgnoreCase(UploadDownloadConstants.FILE_SOURCE_TYPE)) {
				cell.setCellValue(detail.getSourceField());
			} else {
				cell.setCellValue(detail.getSourceField());
			}
		}

		// prepare data, get when error description is not null only
		for (int idx = 0; idx < batch.getDetails().size(); idx++) {
			UpdBatchDetail batchDetail = batch.getDetails().get(idx);
			// skip when error is null
			log.debug("batchDetail.getErrorDescription():" + batchDetail.getErrorDescription());
			if (batchDetail.getErrorDescription() == null) {
				continue;
			}
			String rawMessage = batchDetail.getRawMessage();
			JsonHelper json = new JsonHelper().getUploadSerializer();
			try {
				Map<String, String> rawMap = json.deserialize(rawMessage, Map.class);
				// put error message manually
				rawMap.put(errorMsgTitle, batchDetail.getErrorDescription());
				// start data index at row idx=1
				Row dataRow = sheet.createRow(idx + 1);
				// add new cell on each row data
				for (int idx2 = 0; idx2 < headerList.size(); idx2++) {
					Cell cell = dataRow.createCell(idx2);
					cell.setCellValue(rawMap.get(headerList.get(idx2)));
				}

			} catch (Exception e) {
				log.error("Error when creating workbook", e);
			}

		}

		String fileName = profile.getProfileKey() + "-" + profile.getName().replace(" ", "") + ".xls";
		File file = new File(fileName);
		try {
			workbook.write(new FileOutputStream(file));
		} catch (Exception e) {
			log.error("Error when writing workbook", e);
		}
		renderBinary(file, fileName);
	}

}
