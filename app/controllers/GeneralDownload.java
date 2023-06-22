package controllers;

import helpers.UIConstants;
import helpers.UIHelper;
import helpers.serializers.UpdFilterSerializer;
import helpers.serializers.UpdProfileDetailPickSerializer;
import helpers.serializers.UpdProfilePickSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.Play;
import play.mvc.Before;
import play.mvc.With;
import vo.ProfileSearchParameters;
import vo.UploadDownloadParameters;

import com.google.gson.GsonBuilder;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.UploadDownloadConstants;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnLookupReferenceItem;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RunDownloadParameters;
import com.simian.medallion.model.UdProfileDetailMap;
import com.simian.medallion.model.UpdFilter;
import com.simian.medallion.model.UpdProfile;
import com.simian.medallion.model.UpdProfileDetail;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class GeneralDownload extends MedallionController {
	private static Logger log = Logger.getLogger(GeneralDownload.class);

	private static String VIEW_DIR = "GeneralDownload";
	public static final String COLUMN = "COLUMN";
	public static final String DOWNLOADED_FILE = "DOWNLOADED_FILE";
	public static final String DOWNLOADED_FILTER = "DOWNLOADED_FILTER";

	public static Map<String, String> sessionMap = new HashMap<String, String>();

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		renderArgs.put("VIEW_DIR", VIEW_DIR);
	}

	@Before(only = { "entry", "edit", "save", "confirming", "back", "confirm", "rundownload" })
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("VIEW_DIR", VIEW_DIR);

		renderArgs.put("yesNoOperators", UIHelper.yesNoOperators());

		/*
		 * Separator
		 */
		List<SelectItem> separatorOperators = new ArrayList<SelectItem>();
		List<GnLookup> lookupsSeparator = generalService.listLookups(LookupConstants.SIMIAN_ORGANIZATION_ID, "FILE_SEPARATOR");
		if (lookupsSeparator != null) {
			for (GnLookup look : lookupsSeparator) {
				separatorOperators.add(new SelectItem(look.getLookupId(), look.getLookupDescription()));
			}
		}

		renderArgs.put("separatorOperators", separatorOperators);

		/*
		 * Data type format data
		 */
		Map<String, Map<String, String>> formatData = new HashMap<String, Map<String, String>>();

		renderArgs.put("outputTypes", UIHelper.outputTypes(false));
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
						tmpFormat.put(i.getLookupTarget().getLookupId(), i.getLookupTarget().getLookupDescription());
					}
					formatData.put(look.getLookupDescription(), tmpFormat);
				}

			}
		}

		renderArgs.put("dataTypeOperators", dataType);

		/*
		 * Map<String, String> numberFormat = new HashMap<String, String>();
		 * numberFormat.put("###0", "###0"); numberFormat.put("###0.00",
		 * "###0.00"); numberFormat.put("###0.0000", "###0.0000");
		 * numberFormat.put("###0.000000", "###0.000000");
		 * formatData.put("NUMERIC", numberFormat);
		 * 
		 * Map<String, String> stringFormat = new HashMap<String, String>();
		 * stringFormat.put("N/A", "N/A"); stringFormat.put("\"XXX\"",
		 * "\"XXX\""); formatData.put("CHAR", stringFormat);
		 * 
		 * Map<String, String> dateFormat = new HashMap<String, String>();
		 * //YYYYMMDD atau DDMMYYYY atau MMDDYYYY dateFormat.put("YYYYMMDD",
		 * "YYYYMMDD"); dateFormat.put("DDMMYYYY", "DDMMYYYY");
		 * dateFormat.put("MMDDYYYY", "MMDDYYYY"); formatData.put("DATE",
		 * dateFormat);
		 */

		JsonHelper json = new JsonHelper();
		try {
			renderArgs.put("formatData", json.serialize(formatData));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		/**
		 * value type
		 */
		List<SelectItem> valueType = new ArrayList<SelectItem>();
		valueType.add(new SelectItem("1", "Mandatory"));
		valueType.add(new SelectItem("0", "Optional"));
		valueType.add(new SelectItem("2", "Conditional"));
		renderArgs.put("valueTypeOperators", valueType);

		/**
		 * Fix Value
		 */
		List<SelectItem> fixValueOperators = new ArrayList<SelectItem>();
		List<GnLookup> lookups = generalService.listLookups(LookupConstants.SIMIAN_ORGANIZATION_ID, "DOWNLOAD_FIX_VALUE");
		if (lookups != null) {
			for (GnLookup look : lookups) {
				fixValueOperators.add(new SelectItem(look.getLookupId(), look.getLookupDescription()));
			}
		}
		renderArgs.put("fixValueOperators", fixValueOperators);

	}

	public static void index() {
		log.debug("index. ");

		list();
	}

	@Check("utility.generaldownload")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_DOWNLOAD));
		render(VIEW_DIR + "/list.html");
	}

	@Check("utility.generaldownload")
	public static void entry() {
		log.debug("entry. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_DOWNLOAD));
		UpdProfile profile = new UpdProfile();
		profile.setStatus(true);
		UpdProfile profileTemplate = new UpdProfile();
		String detailMapJson = null;
		render(VIEW_DIR + "/entry.html", profile, profileTemplate, detailMapJson);
	}

	@Check("utility.generaldownload")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_DOWNLOAD));
		UpdProfile profile = generalService.getUpdProfile(id);
		profile.setDetails(generalService.getUpdProfileDetail(id));
		UpdProfile profileTemplate = null;
		if (profile.getTemplete() != null) {
			profileTemplate = generalService.getUpdProfile(Long.parseLong(profile.getTemplete()));
		}
		String detailMapJson = null;
		try {
			detailMapJson = detailToJson(profile);
		} catch (Exception e) {
			log.error("Error serializing detailMap", e);
		}

		render(VIEW_DIR + "/entry.html", profile, profileTemplate, detailMapJson);
	}

	@Check("utility.generaldownload")
	public static void save(UpdProfile profile, UpdProfileDetail[] pdetails, UdProfileDetailMap[] mappingCode) {
		log.debug("save. profile: " + profile + " pdetails: " + pdetails + " mappingCode: " + mappingCode);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_DOWNLOAD));

		if (profile == null) {
			profile = new UpdProfile();
		}
		if (profile.getStatus() == null) {
			profile.setStatus(new Boolean(false));
		}
		if (profile.getArchiveStatus() == null) {
			profile.setArchiveStatus(new Boolean(false));
		}
		profile.setSystemTemplete(new Boolean(false));

		if (profile.getIncludeHeader() == null)
			profile.setIncludeHeader(new Boolean(false));
		if (profile.getSeparator() != null) {
			if (profile.getSeparator().getLookupId().trim().equals("")) {
				profile.setSeparator(null);
			}
		}
		log.debug("save() pdetails: " + (pdetails == null ? "null" : pdetails.length));
		validation.required("Profile ID", profile.getName());
		validation.required("Profile Name", profile.getDescription());
		validation.required("Profile Ref", profile.getTemplete());
		validation.required("Separator", profile.getSeparator());

		Map<Long, List<UdProfileDetailMap>> mapCodeMap = new HashMap<Long, List<UdProfileDetailMap>>();
		if (mappingCode != null) {
			for (UdProfileDetailMap map : mappingCode) {
				if (map == null) {
					continue;
				}
				Long tmpNoSeq = map.getProfileDetail().getNoSeq();
				log.debug("no:" + tmpNoSeq + ", from:" + map.getFromCode() + ", to:" + map.getToCode());
				List<UdProfileDetailMap> tmpList = mapCodeMap.get(tmpNoSeq);
				if (tmpList == null) {
					tmpList = new ArrayList<UdProfileDetailMap>();
				}
				tmpList.add(map);
				log.debug("mapCodeMap.put(" + tmpNoSeq + ", " + tmpList.size() + ");");
				mapCodeMap.put(tmpNoSeq, tmpList);
			}
		}

		if (pdetails != null) {
			profile.getDetails().clear();
			//Map<String, String> mapKey = new HashMap<String, String>();
			for (int idx = 0; idx < pdetails.length; idx++) {
				UpdProfileDetail currDetail = pdetails[idx];
				Long tmpNoSeq = currDetail.getNoSeq();
				if (mapCodeMap.get(tmpNoSeq) != null) {
					List<UdProfileDetailMap> tmpList = mapCodeMap.get(tmpNoSeq);
					for (UdProfileDetailMap map : tmpList) {
						currDetail.getMappingCode().add(map);
						map.setProfileDetail(currDetail);
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
				if (tmpError.length() > 0) {
					validation.addError("", tmpError + " at sequence:" + currDetail.getNoSeq() + " is required.");
				}
				/*
				 * 
				 * no checking here if( mapKey.get(currDetail.getSourceField())
				 * == null ){ mapKey.put(currDetail.getSourceField(),
				 * currDetail.getSourceField()); }else{ validation.addError("",
				 * currDetail.getSourceField()+" is used more than once."); }
				 */

				/*
				 * // FOR NOW IGNORE THIS VALIDATION if
				 * (profile.getIncludeHeader() != null &&
				 * !profile.getIncludeHeader().booleanValue()) { if
				 * (!currDetail.
				 * getSourceType().equals(UploadDownloadConstants.VAR_SOURCE_TYPE
				 * )) {;
				 * 
				 * String[] srcPattern = currDetail.getSourceField().split("_");
				 * if (srcPattern.length != 2 || !COLUMN.equals(srcPattern[0])
				 * || "0".equals(srcPattern[1])) { validation.addError("",
				 * "Source Field must match with pattern COLUMN_{number}, starting with 1"
				 * ); }else{ char[] chars = srcPattern[1].toCharArray(); for
				 * (char c : chars) { if (!Character.isDigit(c)) {
				 * validation.addError("",
				 * "Source Field must match with pattern COLUMN_{number}, starting with 1"
				 * ); break; } } } } }
				 */
				profile.getDetails().add(currDetail);
			}
		}

		UpdProfile profileTemplate = null;
		if (!validation.hasErrors()) {
			log.debug("profile.getDetails():" + profile.getDetails().size());
			// get profile ref
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
			try {
				detailMapJson = detailToJson(profile);
			} catch (Exception e) {
				log.error("Error serializing detailMap", e);
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_DOWNLOAD));
			render(VIEW_DIR + "/entry.html", sort(profile), mappingCode, profileTemplate, detailMapJson);
		}

	}

	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		UpdProfile profile = serializerService.deserialize(session.getId(), id, UpdProfile.class);
		if (profile == null) {
			entry();
		}
		log.debug("ActionType " + profile.getActionType());
		log.debug("profile.getStatus():" + profile.getStatus());

		// get profile ref
		UpdProfile profileTemplate = null;

		Long profRefKey = Long.parseLong(profile.getTemplete());
		profileTemplate = generalService.getUpdProfile(profRefKey);
		renderArgs.put("profileTemplate", profileTemplate);

		String detailMapJson = null;
		try {
			detailMapJson = detailToJson(profile);
		} catch (Exception e) {
			log.error("Error serializing detailMap", e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_DOWNLOAD));
		render(VIEW_DIR + "/entry.html", sort(profile), profileTemplate, detailMapJson);

	}

	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		UpdProfile profile = serializerService.deserialize(session.getId(), id, UpdProfile.class);

		// get profile ref
		UpdProfile profileTemplate = null;
		log.debug("getTemplete():" + profile.getTemplete());
		Long profRefKey = Long.parseLong(profile.getTemplete());
		profileTemplate = generalService.getUpdProfile(profRefKey);
		renderArgs.put("profileTemplate", profileTemplate);

		String detailMapJson = null;
		try {
			detailMapJson = detailToJson(profile);
		} catch (Exception e) {
			log.error("Error serializing detailMap", e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_DOWNLOAD));
		render(VIEW_DIR + "/entry.html", sort(profile), profileTemplate, detailMapJson);

	}

	public static void confirm(UpdProfile profile, UpdProfileDetail[] pdetails, UdProfileDetailMap[] mappingCode) {
		log.debug("confirm. profile: " + profile + " pdetails: " + pdetails + " mappingCode: " + mappingCode);

		// get profile ref
		UpdProfile profileTemplate = null;
		if (profile == null) {
			entry();
		}
		if (profile.getTemplete().length() > 0) {
			Long profRefKey = Long.parseLong(profile.getTemplete());
			profileTemplate = generalService.getUpdProfile(profRefKey);
		}

		Map<Long, List<UdProfileDetailMap>> mapCodeMap = new HashMap<Long, List<UdProfileDetailMap>>();
		if (mappingCode != null) {
			for (UdProfileDetailMap map : mappingCode) {
				if (map == null) {
					continue;
				}
				Long tmpNoSeq = map.getProfileDetail().getNoSeq();
				log.debug("no:" + tmpNoSeq + ", from:" + map.getFromCode() + ", to:" + map.getToCode());
				List<UdProfileDetailMap> tmpList = mapCodeMap.get(tmpNoSeq);
				if (tmpList == null) {
					tmpList = new ArrayList<UdProfileDetailMap>();
				}
				tmpList.add(map);
				mapCodeMap.put(tmpNoSeq, tmpList);
			}
		}

		if (pdetails != null) {
			profile.getDetails().clear();
			for (UpdProfileDetail d : pdetails) {

				Long tmpNoSeq = d.getNoSeq();
				if (mapCodeMap.get(tmpNoSeq) != null) {
					List<UdProfileDetailMap> tmpList = mapCodeMap.get(tmpNoSeq);
					log.debug("tmpList at " + tmpNoSeq + ":" + tmpList.size());
					for (UdProfileDetailMap map : tmpList) {
						d.getMappingCode().add(map);
					}
				}

				profile.getDetails().add(d);
			}
		}
		renderArgs.put("profileTemplate", profileTemplate);

		UpdProfile profile2 = generalService.getUpdProfileByName(profile.getName(), profile.getActionType());
		log.debug("profile2:" + (profile2 == null ? "null" : profile2.getName()));
		if (profile2 != null && profile.getProfileKey() == null) {
			validation.addError("", "Profile ID \"" + profile2.getName() + "\" is already exist.");
		}

		String detailMapJson = null;
		try {
			detailMapJson = detailToJson(profile);
		} catch (Exception e) {
			log.error("Error serializing detailMap", e);
		}

		if (validation.hasErrors()) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_GENERAL_DOWNLOAD));
			renderArgs.put("confirming", false);
			render(VIEW_DIR + "/entry.html", sort(profile), detailMapJson);
		} else {
			try {
				if (profile.getProfileKey() == null) {
					profile.setRecordCreatedDate(new Date());
					profile.setRecordCreatedBy(session.get(UIConstants.SESSION_USERNAME));
				} else {
					profile.setRecordModifiedDate(new Date());
					profile.setRecordModifiedBy(session.get(UIConstants.SESSION_USERNAME));
				}

				// setting back the parent of mapping code
				for (UpdProfileDetail pd : profile.getDetails()) {
					for (UdProfileDetailMap map : pd.getMappingCode()) {
						map.setProfileDetail(pd);
					}
				}
				generalService.saveUpdProfile(sort(profile));
				list();
			} catch (Exception ex) {
				log.error("err in confirm", ex);
				validation.addError("", ex.getMessage());
				renderArgs.put("confirming", true);
				render(VIEW_DIR + "/entry.html", sort(profile), detailMapJson);
			}
		}

	}

	public static void populate(Long profileKey) {
		log.debug("populate. profileKey: " + profileKey);
	}

	public static void download(Long profilekey) {
		log.debug("download. profilekey: " + profilekey);
	}

	public static void profilePaging(Paging page, ProfileSearchParameters param) {
		log.debug("profilePaging. page: " + page + " param: " + param);

		page.addParams("(p.name||p.description)", Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page.addParams("p.actionType", Paging.EQUAL, "Download");
		page = generalService.profilePaging(page);
		renderJSON(page);
	}

	// no need to check json
	public static void duplicateByName(String name, String actionType) {
		log.debug("duplicateByName. name: " + name + " actionType: " + actionType);

		UpdProfile profile = generalService.getUpdProfileByName(name, actionType);
		log.debug("find by name : [" + name + "], actionType :[" + actionType + "]");
		if (profile != null) {
			log.debug("profile.source: " + profile.getSource());
			log.debug("proses " + profile.getProcess());
			profile.setDetails(generalService.getUpdProfileDetail(profile.getProfileKey()));
			profile = duplicateHelper(profile);
		} else {
			profile = new UpdProfile();
		}
		renderJSON(sort(profile), new UpdProfileDetailPickSerializer(), new UpdProfilePickSerializer(), new UpdFilterSerializer());
	}

	// no need to check json
	public static void getProfileDetail(Long profileKey) {
		log.debug("getProfileDetail. profileKey: " + profileKey);

		List<UpdProfileDetail> details = generalService.getUpdProfileDetail(profileKey);
		Map<Long, UpdProfileDetail> detailMap = new HashMap<Long, UpdProfileDetail>();
		for (UpdProfileDetail d : details) {
			d.setUpdProfile(null);
			detailMap.put(d.getNoSeq(), d);
		}
		renderJSON(detailMap, new UpdProfileDetailPickSerializer());
	}

	public static String detailToJson(UpdProfile profile) throws JsonGenerationException, JsonMappingException, IOException {
		log.debug("detailToJson. profile: " + profile);

		String detailMapJson = null;
		Map<Long, UpdProfileDetail> detailMap = new HashMap<Long, UpdProfileDetail>();
		for (UpdProfileDetail d_ : profile.getDetails()) {
			detailMap.put(d_.getNoSeq(), d_);
		}
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(UpdProfileDetail.class, new UpdProfileDetailPickSerializer());
		detailMapJson = gson.create().toJson(detailMap);
		return detailMapJson;
	}

	private static UpdProfile duplicateHelper(UpdProfile profile) {
		log.debug("duplicateHelper. profile: " + profile);

		for (UpdProfileDetail detail : profile.getDetails()) {
			detail.setUpdProfile(null);
		}
		log.debug("profile.getFilters():" + profile.getFilters());
		for (UpdFilter filter : profile.getFilters()) {
			log.debug("filter:" + filter);
			filter.setUpdProfile(null);
		}
		return profile;
	}

	public static UpdProfile sort(UpdProfile profile) {
		log.debug("sort. profile: " + profile);

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

	// Elvino
	@Check("generaldownload.rundownload")
	public static void reset() {
		log.debug("reset. ");

		rundownload(null);
	}

	@Check("generaldownload.rundownload")
	public static void rundownload(RunDownloadParameters param) {
		log.debug("rundownload. param: " + param);

		if (sessionMap != null) {
			String yyyymmdd = Helper.formatYMD(new Date());

			List<String> sesionKeys = new ArrayList<String>();
			for (String key : sessionMap.keySet()) {
				if (key.indexOf(yyyymmdd) == -1) {
					sesionKeys.add(key);
				}
			}
			for (String sessionkey : sesionKeys) {
				sessionMap.remove(sessionkey);
			}
		}

		if (param == null) {
			param = new RunDownloadParameters();
			param.setDispatch(RunDownloadParameters.DISPATCH_LIST);
		}
		session.remove(DOWNLOADED_FILE);
		// session.remove(DOWNLOADED_FILTER);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_RUN_DOWNLOAD));
		render(VIEW_DIR + "/rundownload.html", param);
	}

	public static void processRunDownload(Long profileId, List<String> filters, String uniqe) {
		log.debug("processRunDownload. profileId: " + profileId + " filters: " + filters + " uniqe: " + uniqe);

		String username = session.get(UIConstants.SESSION_USERNAME);

		Map<String, Object> map = new HashMap<String, Object>();

		String[] array = new String[filters.size()];
		array = filters.toArray(array);

		try {
			String filterS = (new JsonHelper()).serialize(filters);

			sessionMap.put(DOWNLOADED_FILTER + "_" + uniqe + "_" + Helper.formatYMD(new Date()), filterS);

			map = downloadUtilityService.processRunDownload(profileId, array, username, true);
			Iterator<String> keySet = map.keySet().iterator();
			while (keySet.hasNext()) {
				String key = keySet.next();
				log.debug("key:" + key);
			}
			log.debug("=======================================================================");
			log.debug("map:" + map);
			renderJSON(map);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			map.put("ERROR", e.getMessage());
			renderJSON(map);
		}
	}

	public static void processGenerateErrorFile(RunDownloadParameters param) {
		log.debug("processGenerateErrorFile. param: " + param);

		String username = session.get(UIConstants.SESSION_USERNAME);
		try {
			GnLookup gnLookup = generalService.getLookup(LookupConstants.FILE_SEPARATOR_COMMA);

			String filename = downloadUtilityService.writeFileToDownload(param.getBatchNo(), UploadDownloadConstants.DOWNLOAD_FAILED, username, param.getOutput(), gnLookup.getLookupCode(), new Boolean(true), null, UploadDownloadConstants.ACTIONTYPE_DOWNLOAD, true);

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

	public static void processGenerateFileFundTransfer(RunDownloadParameters param, String uniqe) {
		log.debug("processGenerateFileFundTransfer. param: " + param + " uniqe: " + uniqe);

		String downloadType = UploadDownloadConstants.DOWNLOAD_SUCCESS;

		String username = session.get(UIConstants.SESSION_USERNAME);
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			String filterjson = sessionMap.get(DOWNLOADED_FILTER + "_" + uniqe + "_" + Helper.formatYMD(new Date()));

			List<String> filters = (new JsonHelper()).deserialize(filterjson, List.class);

			Map<String, String> data = downloadUtilityService.writeFileToDownloadFundTransfer(param.getBatchNo(), downloadType, username, param.getOutput(), null, null, filters, UploadDownloadConstants.ACTIONTYPE_DOWNLOAD, null);
			File tmpFile = new File(data.get("fileName"));

			map.put("status", "SUCCESS");
			map.put("filename", tmpFile.getName());
			map.put("type", downloadType);
			map.put("countOfSuccess", data.get("countOfSuccess"));
			map.put("countOfError", data.get("countOfError"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			map.put("status", "FAIL");
		}
		renderJSON(map);
	}

	public static void processGenerateFile(RunDownloadParameters param, String uniqe, String successOnly) {
		log.debug("processGenerateFile. param: " + param + " uniqe: " + uniqe + " successOnly: " + successOnly);

		boolean isSuccess = Boolean.parseBoolean(successOnly);
		String downloadType = UploadDownloadConstants.DOWNLOAD_ALL;
		if (isSuccess) {
			downloadType = UploadDownloadConstants.DOWNLOAD_SUCCESS;
		}

		String username = session.get(UIConstants.SESSION_USERNAME);
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			String filterjson = sessionMap.get(DOWNLOADED_FILTER + "_" + uniqe + "_" + Helper.formatYMD(new Date()));

			List<String> filters = (new JsonHelper()).deserialize(filterjson, List.class);

			String filename = downloadUtilityService.writeFileToDownload(param.getBatchNo(), downloadType, username, param.getOutput(), null, null, filters, UploadDownloadConstants.ACTIONTYPE_DOWNLOAD, null);
			File tmpFile = new File(filename);
			map.put("status", "SUCCESS");
			map.put("filename", tmpFile.getName());
			map.put("type", downloadType);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			map.put("status", "FAIL");
		}
		renderJSON(map);
	}

	public static void downloadGeneratedFile(String downloadfile) {
		log.debug("downloadGeneratedFile. downloadfile: " + downloadfile);

		String downloadFolder = Play.configuration.getProperty("download.tmp");
		File fileDest = new File(downloadFolder, new File(downloadfile).getName());
		try {
			clientFileService.doDownload(downloadfile, fileDest, SimpleFileService.DOWNLOAD_GENERAL_FILE);
		} catch (Exception e) {
			log.error("Error downloading file:" + downloadfile, e);
		}

		renderBinary(fileDest, fileDest.getName());
	}

	public static void fetchFilter(Long profileKey) {
		log.debug("fetchFilter. profileKey: " + profileKey);

		Map<String, Object> result = new HashMap<String, Object>();
		List<UpdFilter> filters = generalService.getUpdFilter(profileKey);
		Date applicationDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		for (UpdFilter updFilter : filters) {
			if ("DATE".equals(updFilter.getDataType())) {
				if (!Helper.isNullOrEmpty(updFilter.getDefValue())) {
					int defValue = Integer.parseInt(updFilter.getDefValue());
					Date deVal = generalService.getWorkingDate(applicationDate, defValue);
					updFilter.setDefValue(Helper.format(deVal, appProp.getDateFormat()));
				} else {
					updFilter.setDefValue(Helper.format(applicationDate, appProp.getDateFormat()));
				}
			}
		}

		result.put("filters", filters);
		result.put("displayDateFormat", appProp.getDisplayDateFormat());
		result.put("jQuerydisplayDateFormat", appProp.getJqueryDateFormat());
		result.put("dateMask", appProp.getDateMask());
		renderJSON(result);
	}

	public static void previewPaging(Paging page, UploadDownloadParameters param) {
		log.debug("previewPaging. page: " + page + " param: " + param);

		page.getParamFixMap().put("TYPE", "DOWNLOAD");

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

		log.debug("Starting Fetching");
		page = generalService.previewPaging(page, param.getBatchNo());
		if (fromSearch) {
			page.setiTotalSuccess(totalSuccess);
			page.setiTotalFail(totalFailed);
		}
		log.debug("Finishing Fetching");
		renderJSON(page);
	}

	public static void previewPagingDownload(Paging page, UploadDownloadParameters param) {
		log.debug("previewPagingDownload. page: " + page + " param: " + param);

		page.getParamFixMap().put("TYPE", "DOWNLOAD");

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

		log.debug("Starting Fetching");
		page = generalService.previewPagingDownload(page, param.getBatchNo());
		if (fromSearch) {
			page.setiTotalSuccess(totalSuccess);
			page.setiTotalFail(totalFailed);
		}
		log.debug("Finishing Fetching");
		renderJSON(page);
	}
}

// select * from ud_filter
// where 1=1
// --and profile_key = 45266002
// and profile_key = 45265563
// and lookup_display is not null

// UD ROLE MAPPING
