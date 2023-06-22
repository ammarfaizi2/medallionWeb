package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import play.Play;
import play.data.validation.Validation;
import play.libs.Files;
import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.kettle.KtrLoader;
import com.simian.medallion.kettle.KtrStatus;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnLookupGroup;
import com.simian.medallion.model.GnReportPdi;
import com.simian.medallion.model.GnReportPdiMapping;
import com.simian.medallion.model.GnReportPdiParam;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.vo.SelectItem;

public class ReportPdi extends MedallionController {
	private static Logger log = Logger.getLogger(ReportPdi.class);

	@Before(unless = { "edit entry save back view" })
	public static void setup(GnUdfMaster udfMaster) {
		log.debug("setup. udfMaster: " + udfMaster);

		List<SelectItem> displayTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DISPLAY_TYPE);
		renderArgs.put("displayTypes", displayTypes);

		List<SelectItem> dataTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DATA_TYPE);
		renderArgs.put("dataTypes", dataTypes);

		List<SelectItem> lookupGroups = generalService.listLookupGroupsForDropDownAsSelectItem();
		renderArgs.put("lookupGroups", lookupGroups);

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

	}

	public static void list() {
		log.debug("list. ");

		//String username = session.get("username");
		//GnUser user = applicationService.getUser(username);
		//Long userKey = user.getUserKey();
		List<GnReportPdi> reports = generalService.listReportPdi();
		render(reports);
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		//String username = session.get("username");
		//GnUser user = applicationService.getUser(username);
		//Long userKey = user.getUserKey();
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnReportPdi report = generalService.viewReportPdi(id);
		render("ReportPdi/entry.html", mode, report);
	}

	public static void view() {
		log.debug("view. ");

		//String username = session.get("username");
		//GnUser user = applicationService.getUser(username);
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		//Long userKey = user.getUserKey();
		render(mode);
	}

	public static void entry() {
		log.debug("entry. ");

		//String username = session.get("username");
		//GnUser user = applicationService.getUser(username);
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		//Long userKey = user.getUserKey();
		render(mode);
	}

	public static void pdiloader() {
		log.debug("pdiloader. ");

		render();
	}

	public static void confirming(Long id, String mode, boolean isAttachment) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isAttachment: " + isAttachment);

		if (mode == null) {
			mode = UIConstants.DISPLAY_MODE_CONFIRM;
		}
		GnReportPdi report = serializerService.deserialize(session.getId(), id, GnReportPdi.class);
		renderArgs.put("confirming", true);
		log.debug("mode " + mode + "in confirming");
		render("ReportPdi/entry.html", report, mode, isAttachment);
	}

	private static Set<GnReportPdiParam> convertParamtoSet(String[] paramListName, String[] paramListValue, String[] paramListLength, String[] paramListData, String[] paramListDisplay, String[] paramListLookup, String[] paramSequence, String[] paramRemoved) {
		Set<GnReportPdiParam> params = null;
		if (paramListName == null) {
			return params;
		}
		for (int i = 0; i < paramListName.length; i++) {
			GnReportPdiParam param = new GnReportPdiParam();

			boolean isRemoved = false;

			String nameId = null;
			if (paramListName != null) {
				if (paramListName[i] != null) {
					nameId = paramListName[i];
					param.setParamName(nameId);
				}
			}
			if (nameId.trim().length() == 0) {
				continue;
			}

			String defValue = null;
			if (paramListValue != null) {
				if (paramListValue[i] != null) {
					defValue = paramListValue[i];
					param.setDefaultValue(defValue);
				}
			}

			String lengthSize = null;
			if (paramListLength != null) {
				if (paramListLength[i] != null) {
					lengthSize = paramListLength[i];
					log.debug("lengthSize " + lengthSize);
					if (lengthSize.trim().length() > 0) {
						param.setLength(new BigDecimal(lengthSize));
					}
				}
			}

			String typeId = null;
			if (paramListData != null) {
				if (paramListData[i] != null) {
					typeId = paramListData[i];
					GnLookup dataType = generalService.getLookup(typeId);
					param.setDataType(dataType);
				}
			}

			String displayId = null;
			if (paramListDisplay != null) {
				if (paramListDisplay[i] != null) {
					displayId = paramListDisplay[i];
					GnLookup displayType = generalService.getLookup(displayId);
					param.setDisplayType(displayType);
				}
			}
			String lookupId = null;
			if (paramListLookup != null) {
				if (paramListLookup[i] != null) {
					lookupId = paramListLookup[i];
					GnLookupGroup lookupGroup = generalService.getLookupGroup(lookupId);
					param.setLookupGroup(lookupGroup);
				}
			}
			String removedVal = null;
			if (paramRemoved != null) {
				if (paramRemoved[i] != null) {
					removedVal = paramRemoved[i];
					if (removedVal.equals("1")) {
						isRemoved = true;
					}
				}
			}

			String sequenceVal = null;
			if (paramSequence != null) {
				if (paramSequence[i] != null) {
					sequenceVal = paramSequence[i];
					param.setSequenceOrder(new Long(sequenceVal));
				}
			}

			if (params == null) {
				params = new HashSet<GnReportPdiParam>();
			}
			if (!isRemoved) {
				params.add(param);
			}
		}
		return params;
	}

	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		GnReportPdi report = serializerService.deserialize(session.getId(), id, GnReportPdi.class);
		render("ReportPdi/entry.html", report, mode);
	}

	public static void loader() {
		log.debug("loader. ");

		String username = session.get("username");
		GnUser user = applicationService.getUser(username);
		Long userKey = user.getUserKey();
		List<GnReportPdiMapping> allReport = generalService.listReportPdiMappingByRole(userKey);
		Map mapReport = new HashMap();
		for (GnReportPdiMapping report : allReport) {
			// just show user active record only
			if (!report.getReportPdi().getIsActive()) {
				continue;
			}
			String key = report.getReportPdi().getReportGroup();
			if (mapReport.get(key) == null) {
				List groupedReport = new ArrayList();
				groupedReport.add(report);
				mapReport.put(key, groupedReport);

			} else {
				List groupedReport = (List) mapReport.get(key);
				groupedReport.add(report);
				mapReport.put(key, groupedReport);
			}
		}
		/*
		 * List<GnReportPdi> allReport = generalService.listReportPdi(); Map
		 * mapReport = new HashMap(); for( GnReportPdi report : allReport ){ //
		 * just show user active record only if( !report.getIsActive() ){
		 * continue; } String key = report.getReportGroup(); if( mapReport.get(
		 * key ) == null ){
		 * 
		 * List groupedReport = new ArrayList(); groupedReport.add(report);
		 * mapReport.put(key, groupedReport);
		 * 
		 * }else { List groupedReport = (List)mapReport.get( key );
		 * groupedReport.add(report); mapReport.put(key, groupedReport); } }
		 */
		render(allReport, mapReport);
	}

	public static void loadOutput(String reportKey, String[] paramListName, String[] paramListValue, String[] paramListNameUpload, File[] paramListValueUpload) {
		log.debug("loadOutput. reportKey: " + reportKey + " paramListName: " + paramListName + " paramListValue: " + paramListValue + " paramListNameUpload: " + paramListNameUpload + " paramListValueUpload: " + paramListValueUpload);

		GnReportPdi report = generalService.viewReportPdi(new Long(reportKey));
		String reportFile = report.getRealFileName();
		Map<String, String> reportParam = new HashMap<String, String>();

		if (paramListName != null) {
			for (int i = 0; i < paramListName.length; i++) {
				if (paramListName[i] == null || paramListName[i].length() == 0) {
					continue;
				}

				if (paramListValue != null) {
					reportParam.put(paramListName[i], paramListValue[i]);
				}
			}
		}
		// check file param
		log.debug("paramListNameUpload.length:" + (paramListNameUpload == null ? 0 : paramListNameUpload.length));
		if (paramListNameUpload != null) {
			File paramFile = null;
			for (int i = 0; i < paramListNameUpload.length; i++) {
				if (paramListValueUpload[i] != null) {
					paramFile = paramListValueUpload[i];
					String paramFileName = paramFile.getName();

					String uploadedDirParam = Play.configuration.getProperty("upload.kettle.param");
					String newFileName = uploadedDirParam + paramFileName;
					File destParam = new File(newFileName);
					log.debug("try copy file from " + paramFile.getAbsolutePath() + " into " + destParam.getAbsolutePath());
					Files.copy(paramFile, destParam);
					log.debug("done deleting " + paramFile.getAbsolutePath());
					Files.delete(paramFile);
					reportParam.put(paramListNameUpload[i], newFileName);
				}
			}
		}

		log.debug("param " + reportParam);
		String uploadedDirValid = Play.configuration.getProperty("upload.kettle.valid");
		String newFileName = uploadedDirValid + reportFile;
		KtrStatus ktrStatus = runKtr(reportParam, newFileName);
		render(ktrStatus);
	}

	public static void confirm(GnReportPdi report, String mode, String[] paramListName, String[] paramListValue, String[] paramListLength, String[] paramListData, String[] paramListDisplay, String[] paramListLookup, boolean isAttachment, String[] paramSequence, String[] paramRemoved) {
		log.debug("confirm. report: " + report + " mode: " + mode + " paramListName: " + paramListName + " paramListValue: " + paramListValue + " paramListLength: " + paramListLength + " paramListData: " + paramListData + " paramListDisplay: " + paramListDisplay + " paramListLookup: " + paramListLookup + " isAttachment: " + isAttachment + " paramSequence: " + paramSequence + " paramRemoved: " + paramRemoved);

		try {
			// get all param
			Set<GnReportPdiParam> params = null;
			params = convertParamtoSet(paramListName, paramListValue, paramListLength, paramListData, paramListDisplay, paramListLookup, paramSequence, paramRemoved);
			if (params != null) {
				for (GnReportPdiParam aParam : params) {
					aParam.setGnReportPdi(report);
				}
				report.setGnReportPdiParams(params);
			}

			String username = session.get("username");
			report.setRecordModifiedBy(username);

			if (isAttachment) {
				// move file into valid folder
				String uploadedDirTemp = Play.configuration.getProperty("upload.kettle.temp");
				String uploadedDirValid = Play.configuration.getProperty("upload.kettle.valid");
				String attachmentName = report.getRealFileName();

				String oldFileName = uploadedDirTemp + attachmentName;
				String newFileName = uploadedDirValid + attachmentName;

				log.debug("confirm:moving from " + oldFileName + " to " + newFileName);
				File inFile = new File(oldFileName);
				File outFile = new File(newFileName);
				Files.copy(inFile, outFile);
				Files.delete(inFile);
				// end move file into valid folder
			}

			generalService.saveReportPdi(report);
			list();
		} catch (MedallionException e) {
			boolean confirming = true;
			log.error("MedallionException " + e.getMessage(), e);
			render("ReportPdi/entry.html", report, mode, confirming);
		}
	}

	public static void save(GnReportPdi report, String mode, File attachment, String[] paramListName, String[] paramListValue, String[] paramListLength, String[] paramListData, String[] paramListDisplay, String[] paramListLookup, String[] paramSequence, String[] paramRemoved) {
		log.debug("save. report: " + report + " mode: " + mode + " attachment: " + attachment + " paramListName: " + paramListName + " paramListValue: " + paramListValue + " paramListLength: " + paramListLength + " paramListData: " + paramListData + " paramListDisplay: " + paramListDisplay + " paramListLookup: " + paramListLookup + " paramSequence: " + paramSequence + " paramRemoved: " + paramRemoved);

		Set<GnReportPdiParam> params = null;
		if (report != null) {
			if (report.getReportFile().trim().length() == 0 && attachment == null) {
				Validation.required("File", report.getReportFile());
			}
			Validation.required("Report name", report.getReportName());
			Validation.required("Report group", report.getReportGroup());
			Validation.required("Active", report.getIsActive());
			Validation.required("Sequence No", report.getSequenceNo());
			Validation.min("Sequence No", report.getSequenceNo(), 1);

			// get all param
			params = convertParamtoSet(paramListName, paramListValue, paramListLength, paramListData, paramListDisplay, paramListLookup, paramSequence, paramRemoved);
			report.setGnReportPdiParams(params);
			List<GnReportPdi> doubleReport = null;
			boolean isDoubleReport = false;

			if (report.getReportKey() == null) {
				doubleReport = generalService.checkReportGroupName(report);
			}
			if (doubleReport != null) {
				if (doubleReport.size() > 0) {
					for (GnReportPdi aReport : doubleReport) {
						log.debug("aReport: " + aReport.getReportKey());
					}
					flash.error("Report with the same group and name already exist, choose another name/group.", report);
					isDoubleReport = true;
				}
			}
			if (Validation.hasErrors() || isDoubleReport) {
				render("ReportPdi/entry.html", report, mode);
			} else {
				// this to make sure that only attachment is empty, and
				// reportFile is available, so no need to upload new file on
				// edit
				boolean isAttachment = false;
				if (attachment != null) {
					String attachmentName = attachment.getAbsolutePath();
					String uploadedDirKtr = Play.configuration.getProperty("upload.kettle.temp");
					String newFileName = uploadedDirKtr + System.currentTimeMillis() + "-" + attachment.getName();

					log.debug("save:moving from " + attachmentName + " to " + newFileName);
					File output = new File(newFileName);
					Files.copy(attachment, output);
					report.setReportFile(attachment.getName());
					report.setRealFileName(output.getName());

					isAttachment = true;
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

	public static void upload(List<String> paramname, List<String> paramvalue, String addnew, File attachment, boolean ajaxRequest) {
		log.debug("upload. paramname: " + paramname + " paramvalue: " + paramvalue + " addnew: " + addnew + " attachment: " + attachment + " ajaxRequest: " + ajaxRequest);

		Map<String, String> param = configureParam(paramname, paramvalue);
		String attachmentName = attachment.getAbsolutePath();
		String uploadedDirKtr = Play.configuration.getProperty("upload.kettle.valid");
		String newFileName = uploadedDirKtr + attachment.getName();

		log.debug("upload 1:moving from " + attachmentName + " to " + newFileName);
		File output = new File(newFileName);
		Files.copy(attachment, output);

		KtrStatus ktrStatus = runKtr(param, newFileName);
		renderResult(ajaxRequest, ktrStatus);
	}

	private static void renderResult(boolean ajaxRequest, KtrStatus ktrStatus) {
		if (!ajaxRequest) {
			render("ReportPdi/pdiloader.html", ktrStatus);
		} else {
			render("ReportPdi/loadoutput.html", ktrStatus);
		}
	}

	private static KtrStatus runKtr(Map<String, String> param, String newFileName) {
		log.debug("begin executing.." + newFileName);
		KtrStatus ktrStatus = null;
		// process ktr
		try {
			String uploadedDirOutput = Play.configuration.getProperty("upload.kettleoutput");

			KtrLoader ktrLoader = new KtrLoader();

			ktrLoader.setResultDir(uploadedDirOutput);
			ktrLoader.setParameters(param);
			ktrLoader.setSpoonArtifact(newFileName);
			ktrLoader.execute();
			ktrStatus = ktrLoader.getKtrStatus();
			log.debug("ktrStatus.status " + ktrStatus.isSuccess());
			String[] outputFileName = ktrStatus.getOutputFileName();
			for (int idx = 0; idx < outputFileName.length; idx++) {
				String fileName = outputFileName[idx];
				log.debug("Output fileName " + fileName);
			}
			ktrStatus.setOutputFileName(outputFileName);
		} catch (Exception kx) {
			log.error("Exception executing Kettle", kx);
		}
		log.debug("done executing..");

		return ktrStatus;
	}

	public static void pdidownload(String downloadfile) {
		log.debug("pdidownload. downloadfile: " + downloadfile);

		String uploadedDirOutput = Play.configuration.getProperty("upload.kettleoutput");
		String fullPath = uploadedDirOutput + downloadfile;
		renderBinary(new File(fullPath), downloadfile);
	}

	public static void lookup(String lookupId) {
		log.debug("lookup. lookupId: " + lookupId);

		List<SelectItem> lookupItems = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, lookupId);
		JsonHelper jsonHelper = new JsonHelper();
		String allLookupItems = "{}";
		try {
			allLookupItems = jsonHelper.serialize(lookupItems);
		} catch (Exception ex) {
			log.error("error serializing into json ", ex);
		}
		log.debug("allLookupItems: " + allLookupItems);
		renderJSON(allLookupItems);
	}

	private static Map<String, String> configureParam(List<String> aParamName, List<String> aParamValue) {
		Map<String, String> param = null;
		log.debug("aParamName size: " + aParamName.size());
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
}
