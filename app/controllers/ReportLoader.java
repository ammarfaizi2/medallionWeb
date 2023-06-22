package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.pentaho.reporting.engine.classic.core.MasterReport;

import play.Play;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnApplicationDate;
import com.simian.medallion.model.GnReportLoader;
import com.simian.medallion.model.GnReportMapping;
import com.simian.medallion.model.GnReportQueue;
import com.simian.medallion.model.GnReportVariable;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.pre.AbstractReportGenerator.OutputType;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.vo.ReportParam;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class ReportLoader extends MedallionController {
	private static Logger log = Logger.getLogger(ReportLoader.class);

	@Before(unless = { "save" })
	static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("reporting.reportLoader")
	public static void list() {
		log.debug("list. ");

		List<GnReportLoader> reports = generalService.listReportLoader();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RPL_LOADER_LIST));
		render(reports);
	}

	@Check("reporting.reportLoader")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnReportLoader report = generalService.viewReportLoader(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RPL_LOADER_LIST));
		render("ReportLoader/entry.html", mode, report);
	}

	@Check("reporting.reportLoader")
	public static void view() {
		log.debug("view. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		render(mode);
	}

	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RPL_LOADER_LIST));
		GnReportLoader report = new GnReportLoader();
		report.setIsPentahoLoader(true);
		render(mode, report);
	}

	public static void pdiloader() {
		log.debug("pdiloader. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RPL_LOADER_LIST));
		render();
	}

	public static void confirming(Long id, String mode, boolean isAttachment) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isAttachment: " + isAttachment);

		if (mode == null) {
			mode = UIConstants.DISPLAY_MODE_CONFIRM;
		}
		GnReportLoader report = serializerService.deserialize(session.getId(), id, GnReportLoader.class);
		renderArgs.put("confirming", true);
		log.debug("mode " + mode);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RPL_LOADER_LIST));
		render("ReportLoader/entry.html", report, mode, isAttachment);
	}

	public static Map<String, String> convertParamToMap(String paramList) {
		log.debug("convertParamToMap. paramList: " + paramList);

		Map<String, String> reportParam = null;
		try {
			String[] paramLine = paramList.split("\n");

			for (int i = 0; i < paramLine.length; i++) {
				String currLine = paramLine[i];
				String[] pairParam = currLine.split("=");
				if (reportParam == null) {
					reportParam = new HashMap<String, String>();
				}

				if (pairParam.length == 2) { // name=value
					reportParam.put(pairParam[0], pairParam[1]);
				} else { // name=
					reportParam.put(pairParam[0], "");
				}
			}
		} catch (Exception ex) {
			log.error("convertParamToMap:", ex);
		}
		return reportParam;
	}

	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		GnReportLoader report = serializerService.deserialize(session.getId(), id, GnReportLoader.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RPL_LOADER_LIST));
		render("ReportLoader/entry.html", report, mode);
	}

	private static Integer index(List<SelectItem> reportGroups, String name) {
		for (int i = 0; i < reportGroups.size(); i++) {
			String[] arr = reportGroups.get(i).text.split("\\-");
			if (name.equals(arr[1])) {
				return new Integer(i);
			}
		}
		return new Integer(-1);
	}

	@Check("reporting.reportLoader")
	public static void loader() {
		log.debug("loader. ");

		String username = session.get("username");
		GnUser user = applicationService.getUser(username);

		final List<SelectItem> reportGroups = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.REPORT_GROUP);
		List<GnReportMapping> allReport = generalService.listReportMappingByRole(user.getUserKey());

		Comparator<String> reportComparator = new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return index(reportGroups, s1).compareTo(index(reportGroups, s2));
			}
		};

		SortedMap<String, List<GnReportMapping>> mapReport = new TreeMap<String, List<GnReportMapping>>(reportComparator);
		Long report_key = 0L;
		for (GnReportMapping report : allReport) {
			if (report_key.compareTo(report.getReportLoader().getReportKey()) != 0) {
				if (report.getReportLoader() != null) {
					if (!report.getReportLoader().getIsActive()) {
						continue;
					}
					String key = report.getReportLoader().getReportGroup().getLookupDescription();
					if (mapReport.get(key) == null) {
						List<GnReportMapping> groupedReport = new ArrayList<GnReportMapping>();
						groupedReport.add(report);
						mapReport.put(key, groupedReport);
					} else {
						List<GnReportMapping> groupedReport = mapReport.get(key);
						groupedReport.add(report);
						mapReport.put(key, groupedReport);
					}
				}
				report_key = report.getReportLoader().getReportKey();
			}
		}
		/*
		 * List<GnReportLoader> allReport = generalService.listReportLoader();
		 * Map<String, List<GnReportLoader>> mapReport = new HashMap<String,
		 * List<GnReportLoader>>(); for( GnReportLoader report : allReport ){ //
		 * just show user active record only if( !report.getIsActive() ){
		 * continue; } String key = report.getReportGroup().getLookupId(); if(
		 * mapReport.get( key ) == null ){
		 * 
		 * List<GnReportLoader> groupedReport = new ArrayList<GnReportLoader>();
		 * groupedReport.add(report); mapReport.put(key, groupedReport);
		 * 
		 * }else { List<GnReportLoader> groupedReport = mapReport.get( key );
		 * groupedReport.add(report); mapReport.put(key, groupedReport); } }
		 */
		Long timeout = 30 * 60 * 1000l;
		try {
			timeout = Long.parseLong(sessionTimeout) * 60 * 1000l;
			log.debug("parsing session timeout is good:" + timeout);
		} catch (Exception ex) {
			log.error("Error parsing session timeout:" + sessionTimeout + ", set default to:" + timeout, ex);
		}
		render(allReport, mapReport, timeout);
	}

	public static void generateReport(Long reportKey, String outputType, ReportParam[] reportParam) {
		log.debug("generateReport. reportKey: " + reportKey + " outputType: " + outputType + " reportParam: " + reportParam);

		OutputType defaultOutput = OutputType.PDF;
		if (outputType.equals("PDF")) {
			defaultOutput = OutputType.PDF;
		} else if (outputType.equals("HTML")) {
			defaultOutput = OutputType.HTML;
		} else if (outputType.equals("DOC")) {
			defaultOutput = OutputType.DOC;
		} else if (outputType.equals("XLS")) {
			defaultOutput = OutputType.XLS;
		} else if (outputType.equals("XLSX")) {
			defaultOutput = OutputType.XLSX;
		} else if (outputType.equals("CSV")) {
			defaultOutput = OutputType.CSV;
		} else if (outputType.equals("RTF")) {
			defaultOutput = OutputType.RTF;
		} else if (outputType.equals("TXT")) {
			defaultOutput = OutputType.TXT;
		}
		log.debug("reportParam is: " + ((reportParam != null) ? reportParam.length : null));
		if (reportParam != null) {
			for (int i = 0; i < reportParam.length; i++) {
				ReportParam param_ = reportParam[i];
				log.debug("param_[" + i + "].field:" + param_.getField());
				log.debug("param_[" + i + "].type:" + param_.getType());
				log.debug("param_[" + i + "].value:" + param_.getValue());
			}
		}
		Map<String, String> jsonResult = new HashMap<String, String>();
		GnReportLoader report = generalService.viewReportLoader(reportKey);
		String reportFileName = report.getReportFile();

		// split file_name
		String tmpFileName = report.getReportFile();
		String[] tmpSplit_ = tmpFileName.split("\\.");

		String simpleReportFile = "";
		try {
			simpleReportFile = tmpSplit_[0];
		} catch (Exception ex) {
			log.error("Invalid report file:" + tmpFileName, ex);
			throw new MedallionException("RL-A-003", "Invalid file name:" + tmpFileName);
		}

		// TODO: Change by fadly 2017-11-07 Redmine #3162
		// String simpleFileName = generateUniqueReportName( simpleReportFile );
		String simpleFileName = generateUniqueReportName(report.getReportName());
		// End Redmine #3162

		try {
			String userKey = session.get(UIConstants.SESSION_USER_KEY);
			GnReportQueue reportQ = reportQueueService.generateReport(report, reportParam, simpleFileName, defaultOutput.toString(), userKey);

			String resultFileName = reportQ.getResultFilename();
			log.debug("resultFileName:" + resultFileName);
			String[] tmpSplit = resultFileName.split("\\.");
			String fileExt = "";
			if (tmpSplit != null) {
				fileExt = tmpSplit[1];
			}
			String fileDestName = simpleFileName + "." + fileExt;
			log.debug("done generating report for " + reportFileName);
			jsonResult.put("status", "1");
			jsonResult.put("message", "done generating report for " + reportFileName);
			jsonResult.put("reportFile", fileDestName);
		} catch (Exception e) {
			log.error("error generate the specified report:" + reportKey + " " + e.getMessage(), e);
			jsonResult.put("status", "0");
			jsonResult.put("message", "error generating report for " + report.getReportName());
			jsonResult.put("errorMessage", e.getMessage());
			jsonResult.put("reportFile", simpleFileName);
		}
		renderJSON(jsonResult);
	}

	private static Map<String, Object> applyParameter(List<Map<String, Object>> reportParamDefinition, Map<String, Object> aParam) throws Exception {

		Map<String, Object> reportParameter = new HashMap<String, Object>();
		for (int i = 0; i < reportParamDefinition.size(); i++) {
			Map<String, Object> currentParamDef = reportParamDefinition.get(i);

			String key = currentParamDef.get("key").toString();
			Object paramValue = aParam.get(key);
			Map<String, Object> param = (Map<String, Object>) currentParamDef.get("value");

			String valueType = (String) param.get("valueType");
			log.debug("try to parse name:" + key + ", value:" + paramValue + " with " + valueType);
			Object castedValue = getValueFromType(valueType, paramValue);
			reportParameter.put(key, castedValue);
		}
		return reportParameter;
	}

	private static Object getValueFromType(String type, Object value) {
		Object castObject = value;
		if (castObject == null) {
			return castObject;
		}
		if (type.equalsIgnoreCase(BigDecimal.class.getCanonicalName())) {
			BigDecimal aNumber = new BigDecimal(value.toString());
			return aNumber;
		} else if (type.equalsIgnoreCase(BigInteger.class.getCanonicalName())) {
			if (castObject instanceof String[]) {
				log.debug("Its an array of BigInteger");
				String[] tmpCast = (String[]) castObject;
				BigInteger[] aBigArray = new BigInteger[tmpCast.length];
				for (int i = 0; i < tmpCast.length; i++) {
					String num = tmpCast[i];
					aBigArray[i] = new BigInteger(num);
				}
				return aBigArray;
			} else {
				log.debug("Its a BigInteger");
				BigInteger aNumber = new BigInteger(value.toString());
				return aNumber;
			}
		} else if (type.equalsIgnoreCase(String[].class.getCanonicalName())) {
			return (String[]) castObject;
		} else if (type.equalsIgnoreCase(java.util.Date.class.getCanonicalName())) {
			log.debug("try to cast " + castObject + " to " + type);
			long inTime = Long.parseLong(castObject.toString());
			return new java.util.Date(inTime);
		} else if (type.equalsIgnoreCase(java.sql.Date.class.getCanonicalName())) {
			log.debug("try to cast " + castObject + " to " + type);
			long inTime = Long.parseLong(castObject.toString());
			return new java.sql.Date(inTime);
		}
		return castObject;
	}

	private static String generateUniqueReportName(String seed) {
		Calendar cal = Calendar.getInstance();
		String fileName = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
		String time = cal.get(Calendar.SECOND) + "" + cal.get(Calendar.MILLISECOND) + "" + cal.get(Calendar.MINUTE) + "" + cal.get(Calendar.HOUR);
		return seed + "_" + fileName + time;
	}

	/*
	 * public static void loadReportParameterDefinition(Long reportKey){
	 * GnReportLoader report = generalService.viewReportLoader(reportKey);
	 * String validFolder =
	 * Play.configuration.getProperty("upload.reportloader.valid"); String
	 * targetFolder =
	 * Play.configuration.getProperty("upload.reportloaderoutput"); String
	 * reportFileName = validFolder + report.getReportFile(); String
	 * targetFileName = targetFolder + report.getReportFile(); List<Map<String,
	 * Object>> reportParamDefinition = new java.util.LinkedList<Map<String,
	 * Object>>(); String keyCache = String.valueOf(reportKey); aLogger.debug(
	 * "reportFileName: "+reportFileName ); aLogger.debug(
	 * "targetFileName: "+targetFileName );
	 * 
	 * File fileReport = new File(reportFileName); try { if(
	 * !fileReport.exists() ){ aLogger.debug(
	 * "downloading "+report.getReportFile()+" from server" );
	 * clientFileService.doDownload(report.getReportFile(), fileReport,
	 * SimpleFileService.UPLOAD_REPORT_FILE); }else{ aLogger.debug(
	 * report.getReportFile()+" already exist in local folder" ); }
	 * ReportEngineLoader loader = null; MasterReport reportInCache =
	 * getReportInCache( keyCache ); if( reportInCache == null ){ loader = new
	 * ReportEngineLoader(fileReport.getAbsolutePath(), targetFileName);
	 * loader.initReportDefinition();
	 * 
	 * // put it to cache putReportToCache(keyCache, loader.getReport());
	 * aLogger.debug(
	 * "Add to cache:"+keyCache+", report:"+loader.getReport().getName() );
	 * }else { loader = new ReportEngineLoader(reportInCache, targetFileName); }
	 * Map<String, Object> reportParameter = new HashMap<String, Object>();
	 * Map<String, String> webParam = request.params.allSimple(); Map<String,
	 * String[]> webParamObject = request.params.all();
	 * 
	 * for( String key : webParam.keySet() ){ Object currentParam =
	 * webParam.get(key); if( key.indexOf("[]") > 0 ){ currentParam =
	 * webParamObject.get(key); key = key.substring(0, key.length()- 2); }
	 * reportParameter.put(key, currentParam); }
	 * 
	 * aLogger.debug( "reportParameter "+reportParameter );
	 * loader.setReportParameters( reportParameter ); reportParamDefinition =
	 * loader.getReportParameterDefinition(); } catch (Exception e) {
	 * aLogger.error("error load the specified report ", e); throw new
	 * RuntimeException("error load the specified report"); } ObjectMapper json
	 * = new ObjectMapper(); json.configure(Feature.FAIL_ON_EMPTY_BEANS, false);
	 * json.configure(Feature.WRITE_NULL_MAP_VALUES, true);
	 * 
	 * json.getSerializationConfig().withSerializationInclusion(Inclusion.ALWAYS)
	 * ;
	 * 
	 * String dataJSONParameter = "{}"; try { dataJSONParameter =
	 * json.writeValueAsString(reportParamDefinition);
	 * aLogger.debug("generated dataJSONParameter:"); aLogger.debug(
	 * dataJSONParameter ); } catch (Exception ex) {
	 * aLogger.debug("Error writing into json "+ex.getMessage(),ex); }
	 * render(dataJSONParameter); }
	 */
	public static void loadReportParameterVariable(Long reportKey) {
		log.debug("loadReportParameterVariable. reportKey: " + reportKey);

		List<GnReportVariable> vars = generalService.listReportVariable(reportKey);
		for (GnReportVariable var_ : vars) {
			var_.setReport(null);
		}
		log.debug("vars:" + vars.size());
		JsonHelper jsonHelper = new JsonHelper().getReportVariableSerializer();
		String varsJson = "{}";
		try {
			varsJson = jsonHelper.serialize(vars);
		} catch (Exception ex) {
			log.error("error serializing", ex);
		}
		renderJSON(varsJson);
	}

	public static void newlookup(String elementCode, String elementType, String elementLabel, String elementPicker, String formId, String refBy, String defaultValue, boolean readOnly) {
		render("ReportLoader/newlookup.html", elementCode, elementType, elementLabel, elementPicker, formId, refBy, defaultValue, readOnly);
	}

	public static void newNumber(String elementCode, String elementType, String elementLabel, String elementPicker, String formId, String refBy, String defaultValue, boolean readOnly) {
		GnApplicationDate gnAppDate = generalService.getApplicationDate(LookupConstants.SIMIAN_ORGANIZATION_ID);
		String timeNow = Helper.formatM(gnAppDate.getCurrentBusinessDate());
		if (elementCode.trim().equals("year")) {
			timeNow = Helper.formatY(gnAppDate.getCurrentBusinessDate());
		}
		defaultValue = String.valueOf(timeNow);
		render("ReportLoader/newNumber.html", elementCode, elementType, elementLabel, elementPicker, formId, refBy, defaultValue, readOnly);
	}

	public static void newdate(String elementCode, String elementType, String elementLabel, String formId, String refBy) {
		GnApplicationDate gnAppDate = generalService.getApplicationDate(LookupConstants.SIMIAN_ORGANIZATION_ID);
		Long timeNow = gnAppDate.getCurrentBusinessDate().getTime();
		render("ReportLoader/newdate.html", elementCode, elementType, elementLabel, formId, refBy, timeNow);
	}

	public static void loadReportParameterDefinition(Long reportKey, ReportParam[] reportParam) {
		log.debug("loadReportParameterDefinition. reportKey: " + reportKey + " reportParam: " + reportParam);

		GnReportLoader reportLoader = generalService.viewReportLoader(reportKey);
		String validFolder = Play.configuration.getProperty("upload.reportloader.valid");
		String targetFolder = Play.configuration.getProperty("upload.reportloaderoutput");
		String reportFileName = validFolder + reportLoader.getReportFile();
		String targetFileName = targetFolder + reportLoader.getReportFile();
		List<Map<String, Object>> reportParamDefinition = new java.util.LinkedList<Map<String, Object>>();
		log.debug("reportFileName: " + reportFileName);
		log.debug("targetFileName: " + targetFileName);
		if (reportParam != null) {
			for (int i = 0; i < reportParam.length; i++) {
				ReportParam param_ = reportParam[i];
				log.debug("param_[" + i + "].field:" + param_.getField());
				log.debug("param_[" + i + "].type:" + param_.getType());
				log.debug("param_[" + i + "].value:" + param_.getValue());
			}
		}

		try {
			reportParamDefinition = reportGeneratorService.getReportDefinition(reportParam, reportLoader.getReportFile());
		} catch (Exception e) {
			log.error("error load the specified report ", e);
			throw new RuntimeException("error load the specified report");
		}
		ObjectMapper json = new ObjectMapper();
		json.configure(Feature.FAIL_ON_EMPTY_BEANS, false);
		json.configure(Feature.WRITE_NULL_MAP_VALUES, true);

		json.getSerializationConfig().withSerializationInclusion(Inclusion.ALWAYS);

		String dataJSONParameter = "{}";
		try {
			dataJSONParameter = json.writeValueAsString(reportParamDefinition);
			log.debug("generated dataJSONParameter:");
			log.debug(dataJSONParameter);
		} catch (Exception ex) {
			log.debug("Error writing into json " + ex.getMessage(), ex);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RPL_LOADER_LIST));
		render(dataJSONParameter);
	}

	public static void confirm(GnReportLoader report, String mode, String[] paramListName, String[] paramListValue, String[] paramListLength, String[] paramListData, String[] paramListDisplay, String[] paramListLookup, boolean isAttachment) {
		log.debug("confirm. report: " + report + " mode: " + mode + " paramListName: " + paramListName + " paramListValue: " + paramListValue + " paramListLength: " + paramListLength + " paramListData: " + paramListData + " paramListDisplay: " + paramListDisplay + " paramListLookup: " + paramListLookup + " isAttachment: " + isAttachment);

		try {
			GnReportLoader oldReportLoader = null;
			if (report.getReportKey() != null) {
				oldReportLoader = generalService.viewReportLoader(report.getReportKey());
				log.debug("oldReportLoader ReportFile " + oldReportLoader.getReportFile());
				report.setPrePackage(oldReportLoader.getPrePackage());
				report.setPostPackage(oldReportLoader.getPostPackage());
			}

			log.debug("mode " + mode);

			if (isAttachment) {
				// move file into valid folder
				String uploadedDirTemp = Play.configuration.getProperty("upload.reportloader.temp");
				String attachmentName = report.getReportFile();

				String oldFileName = uploadedDirTemp + attachmentName;

				File inFile = new File(oldFileName);

				log.debug("confirm:download file from tmp and upload back to report file in server");
				try {
					log.debug("downloading " + report.getReportFile() + " from server");
					clientFileService.doDownload(attachmentName, inFile, SimpleFileService.UPLOAD_TMP_FILE);

					String newFileName = clientFileService.doUploadPrpt(inFile, SimpleFileService.UPLOAD_REPORT_FILE);
					report.setReportFile(newFileName);
				} catch (Exception e) {
					log.error("Error handling file", e);
				}
			}

			GnReportLoader newReport = generalService.saveReportLoader(report);

			if (oldReportLoader != null && isAttachment) {
				try {
					// Coding baru tidak menghapus yang lama, biar aja yg lama
					// tetap ada
					// // delete file di
					// C:\MedallionFiles\data\attachments\reportloader\valid
					// boolean deleted =
					// clientFileService.doDelete(oldReportLoader.getReportFile(),
					// SimpleFileService.UPLOAD_REPORT_FILE);
					// aLogger.debug("Deleting report tempete is "+oldReportLoader.getReportFile()
					// +((deleted) ? "Success" : "Fail"));

					// delete file di C:\MedallionFiles\ upload\tmp
					String oldFileName = oldReportLoader.getReportFile();
					clientFileService.doDelete(oldFileName, SimpleFileService.UPLOAD_TMP_FILE);

				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}

			// clean up cache
			Cache reportCache = getCache();
			String keyCache = String.valueOf(newReport.getReportKey());
			log.debug("Checking " + keyCache + " in report cache.");
			if (reportCache.get(keyCache) != null) {
				log.debug(keyCache + " is in report cache, remove it then.");
				reportCache.remove(keyCache);
			}
			// list all of them
			list();
		} catch (MedallionException e) {
			boolean confirming = true;
			log.error("MedallionException ", e);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RPL_LOADER_LIST));
			render("ReportLoader/entry.html", report, mode, confirming);
		}
	}

	public static void save(GnReportLoader report, String mode, File attachment, String[] paramListName, String[] paramListValue, String[] paramListLength, String[] paramListData, String[] paramListDisplay, String[] paramListLookup) {
		log.debug("save. report: " + report + " mode: " + mode + " attachment: " + attachment + " paramListName: " + paramListName + " paramListValue: " + paramListValue + " paramListLength: " + paramListLength + " paramListData: " + paramListData + " paramListDisplay: " + paramListDisplay + " paramListLookup: " + paramListLookup);

		if (report != null) {

			if (attachment == null) {
				Validation.required("File", report.getReportFile());
			} else {
			}

			Validation.required("Report name", report.getReportName());
			Validation.required("Report group", report.getReportGroup().getLookupCode());
			Validation.required("Active", report.getIsActive());
			// Validation.required("File", attachment.getAbsolutePath());

			if (Validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RPL_LOADER_LIST));
				render("ReportLoader/entry.html", report, mode);
			} else {
				// this to make sure that only attachment is empty, and
				// reportFile is available, so no need to upload new file on
				// edit
				boolean isAttachment = false;
				if (attachment != null) {
					try {
						String reportFileName = clientFileService.doUploadPrpt(attachment, SimpleFileService.UPLOAD_TMP_FILE);
						report.setReportFile(reportFileName);
						isAttachment = true;
					} catch (Exception ex) {
						log.error("Error uploading file:" + attachment.getName(), ex);
						flash.error("Upload file", "Error uploading file:" + attachment.getName());
					}
				}

				Long id = report.getReportKey();
				report.setRecordCreatedBy(session.get("username"));
				report.setRecordCreatedDate(new Date());
				serializerService.serialize(session.getId(), report.getReportKey(), report);
				confirming(id, mode, isAttachment);
			}
		} else {
			flash.error("argument.null", report);
		}
	}

	public static void reportDownload(String downloadfile) {
		log.debug("reportDownload. downloadfile: " + downloadfile);

		String uploadedDirOutput = Play.configuration.getProperty("upload.reportloaderoutput");
		String fullPath = uploadedDirOutput + downloadfile;
		File fileDest = new File(fullPath);
		try {
			clientFileService.doDownload(downloadfile, fileDest, SimpleFileService.UPLOAD_REPORT_OUTPUT_FILE);
		} catch (Exception e) {
			log.error("Error downloading file:" + downloadfile, e);
			throw new MedallionException("RL-C-001", "Error downloading file:" + downloadfile);
		}
		log.debug("fullPath:" + fullPath);
		renderBinary(new File(fullPath), fileDest.getName());
	}

	private static Map<String, String> configureParam(List<String> aParamName, List<String> aParamValue) {
		Map<String, String> param = null;

		if (aParamName != null && aParamValue != null) {
			param = new HashMap<String, String>();
			for (int i = 0; i < aParamName.size(); i++) {
				String paramName = aParamName.get(i);
				String paramValue = aParamValue.get(i);
				if (paramName.trim().length() > 0) {
					log.debug("Map " + paramName + " into " + paramValue);
					param.put(paramName, paramValue);
				}
			}
		}
		return param;
	}

	private static Cache getCache() {
		CacheManager cacheManager = CacheManager.create();
		Cache reportCache = cacheManager.getCache("report");
		log.debug((reportCache == null) ? "cache is not there, CREATE it" : "cache is there, GET it");
		if (reportCache == null) {
			cacheManager.addCache("report");
			reportCache = cacheManager.getCache("report");
		}
		return reportCache;
	}

	private static MasterReport getReportInCache(String keyCache) {
		Cache reportCache = getCache();
		MasterReport reportInCache = null;
		if (reportCache.get(keyCache) != null) {
			Element elmt = reportCache.get(keyCache);
			reportInCache = (MasterReport) elmt.getObjectValue();
			log.debug("get from cache:" + reportInCache);
		}
		return reportInCache;
	}

	private static void putReportToCache(String keyCache, MasterReport report) {
		Cache reportCache = getCache();
		// clear out the parameter then put it to cache
		report.getParameterValues().clear();
		Element newElmnt = new Element(keyCache, report);
		reportCache.put(newElmnt);
	}

}
