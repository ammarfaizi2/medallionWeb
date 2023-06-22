package controllers;

import helpers.UIConstants;
import helpers.UIHelper;
import helpers.serializers.UpdProfileDetailPickSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.Play;
import play.mvc.Before;
import play.mvc.With;
import vo.MigrateParameters;
import vo.ProfileSearchParameters;
import vo.UploadDownloadParameters;

import com.google.gson.GsonBuilder;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.UploadDownloadConstants;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnLookupReferenceItem;
import com.simian.medallion.model.McAudit;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ProgressCallback;
import com.simian.medallion.model.ReturnParam;
import com.simian.medallion.model.RunUploadParameters;
import com.simian.medallion.model.UdProfileDetailMap;
import com.simian.medallion.model.UpdProfile;
import com.simian.medallion.model.UpdProfileDetail;
import com.simian.medallion.model.UploadMonitor;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.service.UploadMonitorService;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class GeneralUpload extends MedallionController {
	private static Logger log = Logger.getLogger(GeneralUpload.class);

	public static final String COLUMN = "COLUMN";
	public static final String BATCH_ID = "BATCH_ID";

	private static String VIEW_DIR = "GeneralUpload";
	public static final String UPLOAD_FILE = "UPLOAD_FILE";
	public static final String UPLOAD_FILTER = "UPLOAD_FILTER";

	public static String runProcess;

	@Before(only = { "index", "list", "edit", "entry", "confirming", "back", "confirm", "runupload", "save" })
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("VIEW_DIR", VIEW_DIR);

		renderArgs.put("yesNoOperators", UIHelper.yesNoOperators());

		List<SelectItem> separatorOperators = new ArrayList<SelectItem>();
		List<GnLookup> lookupsSeparator = generalService.listLookups(LookupConstants.SIMIAN_ORGANIZATION_ID, "FILE_SEPARATOR");
		if (lookupsSeparator != null) {
			for (GnLookup look : lookupsSeparator) {
				separatorOperators.add(new SelectItem(look.getLookupId(), look.getLookupDescription()));
			}
		}
		renderArgs.put("separatorOperators", separatorOperators);

		Map<String, Map<String, String>> formatData = new HashMap<String, Map<String, String>>();
		// renderArgs.put("outputTypes", UIHelper.outputTypes(false));
		List<SelectItem> dataType = new ArrayList<SelectItem>();
		String gLook = "DATA_TYPE";
		List<GnLookup> lookupsDataType = generalService.listLookups(LookupConstants.SIMIAN_ORGANIZATION_ID, gLook);
		if (lookupsDataType != null) {
			for (GnLookup look : lookupsDataType) {
				dataType.add(new SelectItem(look.getLookupId(), look.getLookupDescription()));
				List<GnLookupReferenceItem> gli = generalService.getLookupReferenceByLookupSource(gLook, look.getLookupId());
				log.debug("find group:" + gLook + ", lookup source:" + look.getLookupId() + " is " + gli);
				if (gli != null) {
					Map<String, String> tmpFormat = formatData.get(look.getLookupDescription());
					if (tmpFormat == null) {
						tmpFormat = new HashMap<String, String>();
					}
					for (GnLookupReferenceItem i : gli) {
						log.debug("i:" + i.getLookupTarget().getLookupCode());
						tmpFormat.put(i.getLookupTarget().getLookupId(), i.getLookupTarget().getLookupDescription());
					}
					formatData.put(look.getLookupDescription(), tmpFormat);
				}

			}
		}

		renderArgs.put("dataTypeOperators", dataType);

		JsonHelper json = new JsonHelper();
		try {
			renderArgs.put("formatData", json.serialize(formatData));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		List<SelectItem> valueType = new ArrayList<SelectItem>();
		valueType.add(new SelectItem("1", "Mandatory"));
		valueType.add(new SelectItem("0", "Optional"));
		valueType.add(new SelectItem("2", "Conditional"));
		renderArgs.put("valueTypeOperators", valueType);

		List<SelectItem> fixValueOperators = new ArrayList<SelectItem>();
		List<GnLookup> lookups = generalService.listLookups(LookupConstants.SIMIAN_ORGANIZATION_ID, "DOWNLOAD_FIX_VALUE");
		if (lookups != null) {
			for (GnLookup look : lookups) {
				fixValueOperators.add(new SelectItem(look.getLookupId(), look.getLookupDescription()));
			}
		}
		renderArgs.put("fixValueOperators", fixValueOperators);

		renderArgs.put("outputTypes", UIHelper.outputTypes(true));
	}

	public static void index() {
		log.debug("index. ");

		list();
	}

	@Check("utility.generalupload")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_UPLOAD));
		render(VIEW_DIR + "/list.html");
	}

	@Check("utility.generalupload")
	public static void entry() {
		log.debug("entry. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_UPLOAD));
		UpdProfile profile = new UpdProfile();
		profile.setProcess("Upload");
		UpdProfile profileTemplate = new UpdProfile();
		String detailMapJson = null;
		String detailMapJsonAdd = null;
		profile.setIsPrefix(false);
		profile.setStatus(true);
		render(VIEW_DIR + "/entry.html", profile, profileTemplate, detailMapJson, detailMapJsonAdd);
	}

	@Check("utility.generalupload")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_UPLOAD));
		UpdProfile profile = generalService.getProfileOnly(id);
		profile.setDetails(sortDetail(generalService.getUpdProfileDetail(id)));
		UpdProfile profileTemplate = null;
		if (profile.getTemplete() != null) {
			profileTemplate = generalService.getProfileOnly(Long.parseLong(profile.getTemplete()));
		}
		if (profile.getFilePrefix() != null) {
			if (profile.getFilePrefix() != "") {
				profile.setIsPrefix(true);
			}
		}
		String detailMapJson = null;
		String detailMapJsonAdd = null;
		List<UpdProfileDetail> detailTam = new ArrayList<UpdProfileDetail>(profile.getDetails());
		profile.getDetails().clear();
		Long noSeq = new Long(0);
		for (UpdProfileDetail upd : detailTam) {
			if (upd.getSourceType().equals(UploadDownloadConstants.VAR_SOURCE_TYPE)) {
				upd.setNoSeq(noSeq);
				profile.getDetailsAdd().add(upd);
				noSeq++;
			} else {
				profile.getDetails().add(upd);
			}
		}
		try {
			detailMapJson = detailToJson(GeneralDownload.sort(profile));
			detailMapJsonAdd = detailAddToJson(profile);
		} catch (Exception e) {
			log.error("Error serializing detailMap", e);
		}

		render(VIEW_DIR + "/entry.html", profile, profileTemplate, detailMapJson, detailMapJsonAdd, mode);
	}

	@Check("utility.generalupload")
	public static void save(UpdProfile profile, UpdProfileDetail[] pdetails, UdProfileDetailMap[] mappingCode, UpdProfileDetail[] pdetailsAdd, UdProfileDetailMap[] mappingCodeAdd) {
		log.debug("save. profile: " + profile + " pdetails: " + pdetails + " mappingCode: " + mappingCode + " pdetailsAdd: " + pdetailsAdd + " mappingCodeAdd: " + mappingCodeAdd);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_UPLOAD));

		if (profile.getStatus() == null) {
			profile.setStatus(new Boolean(false));
		}
		if (profile.getArchiveStatus() == null) {
			profile.setArchiveStatus(new Boolean(false));
		}
		profile.setSystemTemplete(new Boolean(false));

		if (profile.getIncludeHeader() == null)
			profile.setIncludeHeader(new Boolean(false));

		validation.required("Profile ID is", profile.getName());
		validation.required("Profile Name is", profile.getDescription());
		validation.required("Profile Ref is", profile.getTemplete());
		validation.required("Separator is", profile.getSeparator().getLookupId());
		validation.required("File Type is", profile.getFileType());

		profile.getDetails().clear();
		profile.getDetailsAdd().clear();
		Map<Long, List<UdProfileDetailMap>> mapCodeMap = new HashMap<Long, List<UdProfileDetailMap>>();

		if (mappingCode != null) {
			for (UdProfileDetailMap map : mappingCode) {
				Long tmpNoSeq = map.getProfileDetail().getNoSeq();

				List<UdProfileDetailMap> tmpList = mapCodeMap.get(tmpNoSeq);
				if (tmpList == null) {
					tmpList = new ArrayList<UdProfileDetailMap>();
				}

				tmpList.add(map);
				mapCodeMap.put(tmpNoSeq, tmpList);
			}
		}

		Map<Long, List<UdProfileDetailMap>> mapCodeMapAdd = new HashMap<Long, List<UdProfileDetailMap>>();

		if (mappingCodeAdd != null) {
			for (UdProfileDetailMap map : mappingCodeAdd) {
				Long tmpNoSeq = map.getProfileDetail().getNoSeq();

				List<UdProfileDetailMap> tmpList = mapCodeMapAdd.get(tmpNoSeq);

				if (tmpList == null) {
					tmpList = new ArrayList<UdProfileDetailMap>();
				}

				tmpList.add(map);
				mapCodeMapAdd.put(tmpNoSeq, tmpList);
			}
		}

		if (pdetails != null) {
			Map<String, String> mapKey = new HashMap<String, String>();
			List<UpdProfileDetail> updDetail = new ArrayList<UpdProfileDetail>();
			for (int idx = 0; idx < pdetails.length; idx++) {
				UpdProfileDetail currDetail = pdetails[idx];
				Long tmpNoSeq = currDetail.getNoSeq();

				// logger.debug("mapCodeMap.get(tmpNoSeq) "+mapCodeMap.get(tmpNoSeq));
				if (mapCodeMap.get(tmpNoSeq) != null) {
					List<UdProfileDetailMap> tmpList = mapCodeMap.get(tmpNoSeq);
					currDetail.setMappingCode(new HashSet<UdProfileDetailMap>());

					for (UdProfileDetailMap map : tmpList) {
						if (map.getFromCode() == null) {
							validation.addError("", " From Code at sequence:" + currDetail.getNoSeq() + " is required.");
						} else {
							if (map.getFromCode().equals("")) {
								validation.addError("", " From Code at sequence:" + currDetail.getNoSeq() + " is required.");
							}
						}
						if (map.getToCode() == null) {
							validation.addError("", " To Code at sequence:" + currDetail.getNoSeq() + " is required.");
						} else {
							if (map.getToCode().equals("")) {
								validation.addError("", " To Code at sequence:" + currDetail.getNoSeq() + " is required.");
							}
						}
						currDetail.getMappingCode().add(map);
					}
				}
				String tmpError = "";
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
				if (currDetail.getFormatType().equals("")) {
					tmpError = tmpError + "Format";
				}
				if (tmpError.length() > 0) {
					validation.addError("", tmpError + " at sequence:" + currDetail.getNoSeq() + " is required.");
				}
				if (mapKey.get(currDetail.getSourceField()) == null) {
					mapKey.put(currDetail.getSourceField(), currDetail.getSourceField());
				} else {
					validation.addError("", currDetail.getSourceField() + " is used more than once.");
				}
				updDetail.add(currDetail);
			}
			List<UpdProfileDetail> updDetailTam = new ArrayList<UpdProfileDetail>();
			if (updDetail.size() > 0) {
				updDetailTam = sortDetail(updDetail);
				profile.getDetails().addAll(updDetailTam);
			}
		}

		if (pdetailsAdd != null) {
			Map<String, String> mapKey = new HashMap<String, String>();
			List<UpdProfileDetail> updDetail = new ArrayList<UpdProfileDetail>();
			for (int idx = 0; idx < pdetailsAdd.length; idx++) {
				UpdProfileDetail currDetail = pdetailsAdd[idx];
				Long tmpNoSeq = currDetail.getNoSeq();

				if (mapCodeMapAdd.get(tmpNoSeq) != null) {
					List<UdProfileDetailMap> tmpList = mapCodeMapAdd.get(tmpNoSeq);
					currDetail.setMappingCode(new HashSet<UdProfileDetailMap>());

					for (UdProfileDetailMap map : tmpList) {
						if (map.getFromCode() == null) {
							validation.addError("", " From Code at sequence:" + currDetail.getNoSeq() + " in Additional Field is required.");
						} else {
							if (map.getFromCode().equals("")) {
								validation.addError("", " From Code at sequence:" + currDetail.getNoSeq() + " in Additional Field  is required.");
							}
						}
						if (map.getToCode() == null) {
							validation.addError("", " To Code at sequence:" + currDetail.getNoSeq() + " in Additional Field  is required.");
						} else {
							if (map.getToCode().equals("")) {
								validation.addError("", " To Code at sequence:" + currDetail.getNoSeq() + " in Additional Field  is required.");
							}
						}
						currDetail.getMappingCode().add(map);
					}
				}
				if (mapKey.get(currDetail.getSourceField()) == null) {
					mapKey.put(currDetail.getSourceField(), currDetail.getSourceField());
				} else {
					validation.addError("", currDetail.getSourceField() + " is used more than once.");
				}
				if (!currDetail.isSystemDefaultValue()) {
					if (currDetail.getDefaultValue() == null || currDetail.getDefaultValue().equals("")) {
						validation.addError("", "Value '" + currDetail.getTargetField() + "' in Additional Field is required ");
					}
				}
				updDetail.add(currDetail);
			}
			List<UpdProfileDetail> updDetailTam = new ArrayList<UpdProfileDetail>();
			if (updDetail.size() > 0) {
				updDetailTam = sortDetail(updDetail);
				profile.getDetailsAdd().addAll(updDetailTam);
			}
		}

		// get profile ref
		UpdProfile profileTemplate = null;
		if (profile.getTemplete().length() > 0) {
			Long profRefKey = Long.parseLong(profile.getTemplete());
			profileTemplate = generalService.getUpdProfile(profRefKey);
		}
		renderArgs.put("profileTemplate", profileTemplate);

		if (profile.getStatus() == null)
			profile.setStatus(new Boolean(false));
		if (profile.getArchiveStatus() == null)
			profile.setArchiveStatus(new Boolean(false));
		profile.setSystemTemplete(new Boolean(false));

		if (profile.getIncludeHeader() == null)
			profile.setIncludeHeader(new Boolean(false));

		/*
		 * validation.required("Templete", profile.getTemplete());
		 * validation.required("Name is", profile.getName());
		 * validation.required("Description is", profile.getDescription());
		 */
		// validation.required("Source", profile.getSource());
		if (!validation.hasErrors()) {
			Long id = profile.getProfileKey();
			serializerService.serialize(session.getId(), id, profile);
			String mode = "confirm";
			confirming(id, mode);
		} else {
			Long profRefKey = 0l;

			try {
				profRefKey = Long.parseLong(profile.getTemplete());
			} catch (Exception ex) {

			}

			profileTemplate = generalService.getUpdProfile(profRefKey);
			renderArgs.put("profileTemplate", profileTemplate);

			String detailMapJson = null;
			String detailMapJsonAdd = null;

			try {
				detailMapJson = detailToJson(GeneralDownload.sort(profile));
				detailMapJsonAdd = detailAddToJson(profile);
			} catch (Exception e) {
				log.error("Error serializing detailMap", e);
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_UPLOAD));
			render(VIEW_DIR + "/entry.html", GeneralDownload.sort(profile), mappingCode, mappingCodeAdd, profileTemplate, detailMapJson, detailMapJsonAdd);
		}

	}

	@Check("utility.generalupload")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		UpdProfile profile = serializerService.deserialize(session.getId(), id, UpdProfile.class);

		// get profile ref
		UpdProfile profileTemplate = null;

		Long profRefKey = Long.parseLong(profile.getTemplete());
		profileTemplate = generalService.getUpdProfile(profRefKey);
		renderArgs.put("profileTemplate", profileTemplate);

		String detailMapJson = null;
		String detailMapJsonAdd = null;
		try {
			detailMapJson = detailToJson(GeneralDownload.sort(profile));
			detailMapJsonAdd = detailAddToJson(profile);
		} catch (Exception e) {
			log.error("Error serializing detailMap", e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_UPLOAD));
		render(VIEW_DIR + "/entry.html", GeneralDownload.sort(profile), profileTemplate, detailMapJson, detailMapJsonAdd);

	}

	@Check("utility.generalupload")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		UpdProfile profile = serializerService.deserialize(session.getId(), id, UpdProfile.class);
		log.debug("profile:" + profile);

		// get profile ref
		UpdProfile profileTemplate = null;
		log.debug("getTemplete():" + profile.getTemplete());
		Long profRefKey = Long.parseLong(profile.getTemplete());
		profileTemplate = generalService.getUpdProfile(profRefKey);
		renderArgs.put("profileTemplate", profileTemplate);

		String detailMapJson = null;
		String detailMapJsonAdd = null;
		try {
			detailMapJson = detailToJson(GeneralDownload.sort(profile));
			detailMapJsonAdd = detailAddToJson(profile);
		} catch (Exception e) {
			log.error("Error serializing detailMap", e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_UPLOAD));
		render(VIEW_DIR + "/entry.html", GeneralDownload.sort(profile), profileTemplate, detailMapJson, detailMapJsonAdd);

	}

	@Check("utility.generalupload")
	public static void confirm(UpdProfile profile, UpdProfileDetail[] pdetails, UdProfileDetailMap[] mappingCode, UpdProfileDetail[] pdetailsAdd, UdProfileDetailMap[] mappingCodeAdd) {
		log.debug("confirm. profile: " + profile + " pdetails: " + pdetails + " mappingCode: " + mappingCode + " pdetailsAdd: " + pdetailsAdd + " mappingCodeAdd: " + mappingCodeAdd);

		// get profile ref
		UpdProfile profileTemplate = null;
		if (profile.getTemplete().length() > 0) {
			Long profRefKey = Long.parseLong(profile.getTemplete());
			profileTemplate = generalService.getUpdProfile(profRefKey);
		}

		Map<Long, List<UdProfileDetailMap>> mapCodeMap = new HashMap<Long, List<UdProfileDetailMap>>();
		if (mappingCode != null) {
			for (UdProfileDetailMap map : mappingCode) {
				Long tmpNoSeq = map.getProfileDetail().getNoSeq();
				List<UdProfileDetailMap> tmpList = mapCodeMap.get(tmpNoSeq);
				if (tmpList == null) {
					tmpList = new ArrayList<UdProfileDetailMap>();
				}
				tmpList.add(map);
				mapCodeMap.put(tmpNoSeq, tmpList);
			}
		}

		Map<Long, List<UdProfileDetailMap>> mapCodeMapAdd = new HashMap<Long, List<UdProfileDetailMap>>();
		if (mappingCodeAdd != null) {
			for (UdProfileDetailMap map : mappingCodeAdd) {
				Long tmpNoSeq = map.getProfileDetail().getNoSeq();
				List<UdProfileDetailMap> tmpList = mapCodeMapAdd.get(tmpNoSeq);
				if (tmpList == null) {
					tmpList = new ArrayList<UdProfileDetailMap>();
				}
				tmpList.add(map);
				mapCodeMapAdd.put(tmpNoSeq, tmpList);
			}
		}

		if (pdetails != null) {
			profile.getDetails().clear();
			List<UpdProfileDetail> updDetail = new ArrayList<UpdProfileDetail>();
			for (UpdProfileDetail d : pdetails) {
				Long tmpNoSeq = d.getNoSeq();
				if (mapCodeMap.get(tmpNoSeq) != null) {
					List<UdProfileDetailMap> tmpList = mapCodeMap.get(tmpNoSeq);
					d.setMappingCode(new HashSet<UdProfileDetailMap>());
					if (d.isMappingRequired()) {
						for (UdProfileDetailMap map : tmpList) {
							d.getMappingCode().add(map);
							map.setProfileDetail(d);
						}
					} else {
						d.setDefaultValue("");
					}
				}
				updDetail.add(d);
			}
			List<UpdProfileDetail> updDetailTam = new ArrayList<UpdProfileDetail>();
			if (updDetail.size() > 0) {
				updDetailTam = sortDetail(updDetail);
				profile.getDetails().addAll(updDetailTam);
			}
		}

		if (pdetailsAdd != null) {
			profile.getDetailsAdd().clear();
			List<UpdProfileDetail> updDetail = new ArrayList<UpdProfileDetail>();
			for (UpdProfileDetail d : pdetailsAdd) {
				Long tmpNoSeq = d.getNoSeq();
				if (mapCodeMapAdd.get(tmpNoSeq) != null) {
					List<UdProfileDetailMap> tmpList = mapCodeMapAdd.get(tmpNoSeq);
					d.setMappingCode(new HashSet<UdProfileDetailMap>());
					for (UdProfileDetailMap map : tmpList) {
						d.getMappingCode().add(map);
						map.setProfileDetail(d);
					}
				}
				updDetail.add(d);
			}
			List<UpdProfileDetail> updDetailTam = new ArrayList<UpdProfileDetail>();
			if (updDetail.size() > 0) {
				updDetailTam = sortDetail(updDetail);
				profile.getDetailsAdd().addAll(updDetailTam);
			}
		}

		renderArgs.put("profileTemplate", profileTemplate);

		UpdProfile profile2 = generalService.getUpdProfileByName(profile.getName(), profile.getActionType());
		if (profile2 != null && profile.getProfileKey() == null) {
			validation.addError("", "Profile ID required to be unique, this \"" + profile.getName() + "\" already exist.");
		}

		String detailMapJson = null;
		String detailMapJsonAdd = null;
		try {
			detailMapJson = detailToJson(GeneralDownload.sort(profile));
			detailMapJsonAdd = detailAddToJson(profile);
		} catch (Exception e) {
			log.error("Error serializing detailMap", e);
		}

		if (validation.hasErrors()) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_UPLOAD));
			renderArgs.put("confirming", true);
			render(VIEW_DIR + "/entry.html", GeneralDownload.sort(profile), detailMapJson, detailMapJsonAdd);
		} else {
			try {

				int noSeq = profile.getDetails().size();
				for (UpdProfileDetail d : profile.getDetailsAdd()) {

					d.setNoSeq(Long.parseLong(noSeq + ""));
					d.setSourceType(UploadDownloadConstants.VAR_SOURCE_TYPE);
					profile.getDetails().add(d);
					noSeq++;
				}
				if (profile.getProfileKey() == null) {
					profile.setRecordCreatedDate(new Date());
					profile.setRecordCreatedBy(session.get(UIConstants.SESSION_USERNAME));
				} else {
					profile.setRecordModifiedDate(new Date());
					profile.setRecordModifiedBy(session.get(UIConstants.SESSION_USERNAME));
				}
				for (UpdProfileDetail pd : profile.getDetails()) {
					for (UdProfileDetailMap map : pd.getMappingCode()) {
						map.setMappingKey(null);
						map.setProfileDetail(pd);
					}
				}
				profile.setSystemTemplete(false);
				generalService.saveUpdProfile(GeneralDownload.sort(profile));
				list();
			} catch (Exception ex) {

				log.error("err in confirm", ex);
				validation.addError("", ex.getMessage());
				renderArgs.put("confirming", true);
				render(VIEW_DIR + "/entry.html", GeneralDownload.sort(profile), detailMapJson, detailMapJsonAdd);
			}
		}

	}

	public static void profilePaging(Paging page, ProfileSearchParameters param) {
		log.debug("profilePaging. page: " + page + " param: " + param);

		page.addParams(Helper.searchAll("(p.name||p.description)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page.addParams("p.actionType", Paging.EQUAL, "Upload");
		page = generalService.profilePaging(page);
		renderJSON(page);
	}

	@Check("generalupload.runupload")
	public static void reset() {
		log.debug("reset. ");

		runupload(null);
	}

	@Check("generalupload.runupload")
	public static void runupload(RunUploadParameters param) {
		log.debug("runupload. param: " + param);

		if (param == null) {
			param = new RunUploadParameters();
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_RUN_UPLOAD));
		render("GeneralUpload/runupload.html", param);
	}

	private static final Map<String, RunUploadParameters> uploadMapCache = new HashMap<String, RunUploadParameters>();
	private static final Map<String, Map> uploadMapCacheResult = new HashMap<String, Map>();
	private static final Map<String, Map> batchMapCacheResult = new HashMap<String, Map>();
	private static final Map<String, String> bridgeMapCache = new HashMap<String, String>();

	public static void preProcessRunUpload(RunUploadParameters param) {
		log.debug("preProcessRunUpload. param: " + param);

		try {
			String newFileName = clientFileService.doUpload(param.getFile(), SimpleFileService.UPLOAD_TMP_FILE);
			String newAbsolutePath = clientFileService.getAbsolutePath(newFileName, SimpleFileService.UPLOAD_TMP_FILE);
			param.setNewAbsolutePath(newAbsolutePath);

			uploadMapCache.put(param.getProcessId(), param);

			Map<String, String> preturn = new HashMap<String, String>();
			preturn.put("RESULT", "SUCCESS");
			String resultJson = (new GsonBuilder()).create().toJson(preturn);
			render(resultJson);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Map<String, String> preturn = new HashMap<String, String>();
			preturn.put("RESULT", "FAIL");
			String resultJson = (new GsonBuilder()).create().toJson(preturn);
			render(resultJson);

		}
	}

	public static void getProcessRunUploadResult(String processId) {
		log.debug("getProcessRunUploadResult. processId: " + processId);

		Map map = uploadMapCacheResult.get(processId);
		renderJSON(map);
	}

	public static void delProcessRunUploadResult(String processId) {
		log.debug("delProcessRunUploadResult. processId: " + processId);

		uploadMapCacheResult.remove(processId);
	}

	public static void getProcessBatchResult(String processId) {
		log.debug("getProcessBatchResult. processId: " + processId);

		Map map = batchMapCacheResult.get(processId);
		renderJSON(map);
	}

	public static void delProcessBatchResult(String processId) {
		log.debug("delProcessBatchResult. processId: " + processId);

		batchMapCacheResult.remove(processId);
	}

	public static void processRunUpload(String processId) {
		log.debug("processRunUpload. processId: " + processId);

		Map<String, Object> map = new HashMap<String, Object>();

		try {

			RunUploadParameters param = uploadMapCache.get(processId);
			String username = session.get(UIConstants.SESSION_USERNAME);
			String userkey = session.get(UIConstants.SESSION_USER_KEY);
			String newAbsolutePath = param.getNewAbsolutePath();
			// String newFileName = clientFileService.doUpload(param.getFile(),
			// SimpleFileService.UPLOAD_TMP_FILE);
			//
			// logger.debug("ProfileKey "+param.getUpdProfile().getProfileKey());
			// logger.debug("File "+param.getFile());
			// String newAbsolutePath =
			// clientFileService.getAbsolutePath(newFileName,
			// SimpleFileService.UPLOAD_TMP_FILE);
			// logger.debug("Uploaded to "+newAbsolutePath);
			// logger.debug("uploadUtilityService "+uploadUtilityService);

			// do populate
			map = uploadUtilityService.processRunUpload(param.getUpdProfile().getProfileKey(), newAbsolutePath, username, true);
			// session.put(processId+UIConstants.SESSION_ZIPFILE,
			// map.get("ZIPFILE"));
			// session.put(processId+UIConstants.SESSION_FILEEXTENSION,
			// map.get("EXTENSION"));
			bridgeMapCache.put(processId + UIConstants.SESSION_ZIPFILE, (String) map.get("ZIPFILE"));
			bridgeMapCache.put(processId + UIConstants.SESSION_FILEEXTENSION, (String) map.get("EXTENSION"));

			// langsung di sumulate
			String[] parts = newAbsolutePath.split("\\.");
			UploadMonitor uploadMonitor = new UploadMonitor((Long) map.get("BATCHID"), "", username, Long.valueOf(userkey), newAbsolutePath, parts[parts.length - 1]);
			uploadMonitorService.simulation(uploadMonitor);

			// render
			// renderJSON(map);
			// String resultJson = (new GsonBuilder()).create().toJson(map);
			// logger.debug("resultJson "+resultJson);
			// render(resultJson);

			uploadMapCache.remove(processId);
			uploadMapCacheResult.put(processId, map);
			renderJSON(map);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			map.put("ERROR", e.getMessage());
			uploadMapCacheResult.put(processId, map);
			// renderJSON(map);
			// String resultJson = (new GsonBuilder()).create().toJson(map);
			// render(resultJson);
			uploadMapCache.remove(processId);
			renderJSON(map);
		}
	}

	public static void getRunningProcess() {
		log.debug("getRunningProcess. ");

		try {
			ReturnParam param = new ReturnParam();
			log.debug("getRunningProcess runProcess " + runProcess);
			param.setRunProcess(runProcess);
			renderJSON(param);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void saveValidationError(RunUploadParameters param) {
		log.debug("saveValidationError. param: " + param);

		String username = session.get(UIConstants.SESSION_USERNAME);
		try {
			GnLookup gnLookup = generalService.getLookup(LookupConstants.FILE_SEPARATOR_COMMA);
			String filename = downloadUtilityService.writeFileToDownload(param.getBatchNo(), UploadDownloadConstants.DOWNLOAD_FAILED, username, param.getOutput(), gnLookup.getLookupCode(), new Boolean(true), null, UploadDownloadConstants.ACTIONTYPE_UPLOAD, true);

			/***/
			String downloadFolder = Play.configuration.getProperty("download.tmp");

			// even filename is not exists here, "new File(filename).getName()"
			// is the way we get the file name only, not the full path
			String simpleFilename = new File(filename).getName();
			File fileDest = new File(downloadFolder, simpleFilename);
			try {
				clientFileService.doDownload(simpleFilename, fileDest, SimpleFileService.DOWNLOAD_GENERAL_FILE);
			} catch (Exception e) {
				log.error("Error downloading file:" + simpleFilename, e);
			}
			/**/
			renderBinary(fileDest, simpleFilename);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void linkSaveProcessError(RunUploadParameters param) {
		log.debug("linkSaveProcessError. param: " + param);

		String username = session.get(UIConstants.SESSION_USERNAME);
		try {
			GnLookup gnLookup = generalService.getLookup(LookupConstants.FILE_SEPARATOR_COMMA);

			String filename = downloadUtilityService.writeFileToDownload(param.getBatchNo(), UploadDownloadConstants.DOWNLOAD_FAILED, username, param.getOutput(), gnLookup.getLookupCode(), new Boolean(true), null, UploadDownloadConstants.ACTIONTYPE_UPLOAD, false);
			/***/
			String downloadFolder = Play.configuration.getProperty("download.tmp");

			// even filename is not exists here, "new File(filename).getName()"
			// is the way we get the file name only, not the full path
			String simpleFilename = new File(filename).getName();
			File fileDest = new File(downloadFolder, simpleFilename);
			try {
				clientFileService.doDownload(simpleFilename, fileDest, SimpleFileService.DOWNLOAD_GENERAL_FILE);
			} catch (Exception e) {
				log.error("Error downloading file:" + simpleFilename, e);
			}
			/**/
			renderBinary(fileDest, simpleFilename);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void processBatch(RunUploadParameters param) {
		log.debug("processBatch. param: " + param);

		Map<String, Object> map = new HashMap<String, Object>();

		String username = session.get(UIConstants.SESSION_USERNAME);
		String userkey = session.get(UIConstants.SESSION_USER_KEY);
		// String zipfile =
		// session.get(param.getOldProcessId()+UIConstants.SESSION_ZIPFILE);
		// String fileextension =
		// session.get(param.getOldProcessId()+UIConstants.SESSION_FILEEXTENSION);
		String zipfile = bridgeMapCache.get(param.getOldProcessId() + UIConstants.SESSION_ZIPFILE);
		String fileextension = bridgeMapCache.get(param.getOldProcessId() + UIConstants.SESSION_FILEEXTENSION);

		UploadMonitor uploadMonitor = new UploadMonitor(param.getBatchNo(), "", username, Long.valueOf(userkey), zipfile, fileextension);

		try {
			uploadMonitor = uploadMonitorService.realization(uploadMonitor);
			map.put("status", "SUCCESS");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			map.put("status", "FAIL");
		}

		map.put("monitor", uploadMonitor);
		batchMapCacheResult.put(param.getProcessId(), map);

		bridgeMapCache.remove(param.getOldProcessId() + UIConstants.SESSION_ZIPFILE);
		bridgeMapCache.remove(param.getOldProcessId() + UIConstants.SESSION_FILEEXTENSION);

		renderJSON(map);
	}

	public static void previewPaging(Paging page, UploadDownloadParameters param) {
		log.debug("previewPaging. page: " + page + " param: " + param);

		page.getParamFixMap().put("TYPE", "UPLOAD");

		long totalSuccess = 0;
		long totalFailed = 0;
		boolean fromSearch = false;
		if (page.getsSearch() != null && !"".equals(page.getsSearch())) {
			page.addParams("(c.rawMessage||c.errorDescription)", Paging.LIKE, "%" + page.getsSearch() + "%");
			totalSuccess = param.getTotalSuccess();
			totalFailed = param.getTotalFail();
			fromSearch = true;
		}
		// if (param.isErrorOnly()) {
		// page.addParams("c.errorDescription", Paging.ISNOTNULL, null);
		// }

		page = generalService.previewPaging(page, param.getBatchNo());
		if (fromSearch) {
			page.setiTotalSuccess(totalSuccess);
			page.setiTotalFail(totalFailed);
		}
		renderJSON(page);
	}

	private static String detailAddToJson(UpdProfile profile) throws JsonGenerationException, JsonMappingException, IOException {
		log.debug("detailAddToJson. profile: " + profile);

		String detailMapJson = null;
		Map<Long, UpdProfileDetail> detailMap = new HashMap<Long, UpdProfileDetail>();
		for (UpdProfileDetail d_ : profile.getDetailsAdd()) {
			detailMap.put(d_.getNoSeq(), d_);
		}
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(UpdProfileDetail.class, new UpdProfileDetailPickSerializer());
		detailMapJson = gson.create().toJson(detailMap);
		return detailMapJson;
	}

	public static String detailToJson(UpdProfile profile) throws JsonGenerationException, JsonMappingException, IOException {
		log.debug("detailToJson. profile: " + profile);

		String detailMapJson = null;
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(UpdProfileDetail.class, new UpdProfileDetailPickSerializer());
		detailMapJson = gson.create().toJson(profile.getDetails());
		return detailMapJson;
	}

	@Check("generalupload.migratelist")
	public static void migratelist() {
		log.debug("migratelist. ");

		List<McAudit> cips = uploadMonitorService.findMcAudits();
		List<McAudit> audits = new ArrayList<McAudit>();

		for (McAudit mcAudit : cips) {
			if (!Helper.isNullOrEmpty(mcAudit.getProcessCode()) && mcAudit.getProcessCode().trim().equals(UploadMonitorService.PROCESS_CIPS_ENTITTLEMENT))
				continue;
			audits.add(mcAudit);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("displayDateFormat", appProp.getDisplayDateFormat());
		result.put("jQuerydisplayDateFormat", appProp.getJqueryDateFormat());
		result.put("dateMask", appProp.getDateMask());

		Date applicationDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		Calendar cal = Calendar.getInstance();
		cal.setTime(applicationDate);

		result.put("defCurrent", new Date());
		result.put("defAppDate", applicationDate);
		result.put("defMonth", cal.get(Calendar.MONTH) + 1);
		result.put("defYear", cal.get(Calendar.YEAR));

		render(VIEW_DIR + "/migrate.html", audits, result);
	}

	public static void migrateData(String process, MigrateParameters filter) {
		log.debug("migrateData. process: " + process + " filter: " + filter);

		ReturnParam param = new ReturnParam();

		try {
			if (runProcess != null)
				throw new Exception("Please wait, other process is currently running");
			runProcess = process;

			String username = session.get(UIConstants.SESSION_USERNAME);

			Map<String, Object> maps = new HashMap<String, Object>();
			// if (filter.getAsOfDate() != null) maps.put("asOfDate",
			// filter.getAsOfDate());
			// if (filter.getCaType() != null) maps.put("caType",
			// filter.getCaType());
			// if (filter.getSecurity() != null) maps.put("security",
			// filter.getSecurity());
			// if (filter.getRecordingDate() != null) maps.put("recordingDate",
			// filter.getRecordingDate());
			// if (filter.getReferenceNo() != null) maps.put("referenceNo",
			// filter.getReferenceNo());

			if (UploadMonitorService.PROCESS_CIPS_ENTITTLEMENT.equals(process)) {
				Date date = Helper.parse(filter.getRecordingDate(), Helper.convertPlayFormat(appProp.getDisplayDateFormat()));
				log.debug("Parameter date " + date);
				maps.put("asofdate", Helper.formatDDMMMYY(date));
				maps.put("date", Helper.formatYMD(date));
			}
			if (UploadMonitorService.PROCESS_CIPS_SUMMARY_HOLDING.equals(process)) {
				Date date = Helper.parse(filter.getAsOfDate(), Helper.convertPlayFormat(appProp.getDisplayDateFormat()));
				log.debug("Parameter date " + date);
				maps.put("asofdate", Helper.formatDDMMMYY(date));
				maps.put("date", Helper.formatYMD(date));
			}

			if (UploadMonitorService.PROCESS_CIPS_CUST_ACTIVITY.equals(process)) {
				maps.put("month", filter.getFillMonth());
				maps.put("year", filter.getYear());
			}

			if (UploadMonitorService.PROCESS_CIPS_LKPBU.equals(process)) {
				maps.put("month", filter.getFillMonth());
				maps.put("year", filter.getYear());
			}

			if (UploadMonitorService.PROCESS_CIPS_POSISI_BI.equals(process)) {
				maps.put("month", filter.getFillMonth());
				maps.put("year", filter.getYear());
				maps.put("yearmonth", filter.getYear() + filter.getFillMonth());
			}

			log.debug("Start runUploadFromCips");
			McAudit audit = uploadMonitorService.runUploadFromCips(process, username, maps);
			param.setStatus("SUCCESS");
			param.setMessage(param.getStatus());
			param.setAudit(audit);
			log.debug("End runUploadFromCips");

			runProcess = null;
			log.debug("finish remove RunningProcess runProcess " + runProcess);
		} catch (Exception e) {
			runProcess = null;
			log.debug("error remove RunningProcess runProcess " + runProcess);

			log.error(e.getMessage(), e);
			param.setStatus("FAIL");
			param.setMessage(e.getMessage());
		} finally {
			runProcess = null;
		}

		renderJSON(param);
	}

	public static void progressData(String process) {
		log.debug("progressData. process: " + process);

		String username = session.get(UIConstants.SESSION_USERNAME);
		ProgressCallback progress = uploadMonitorService.getPogressCallback(process, username);
		renderJSON(progress);
	}

	public static List<UpdProfileDetail> sortDetail(List<UpdProfileDetail> profile) {
		log.debug("sortDetail. profile: " + profile);

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
		Collections.sort(profile, new UpdDetailComparator());
		return profile;
	}
}