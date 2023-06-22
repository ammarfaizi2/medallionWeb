package controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.model.AmlCustomer;
import com.simian.medallion.model.AmlCustomerContact;
import com.simian.medallion.model.AmlCustomerWatchlist;
import com.simian.medallion.model.GnApplicationDate;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.AmlCustomerVO;
import com.simian.medallion.vo.SelectItem;

import helpers.UIConstants;
import helpers.UIHelper;
import play.mvc.Before;
import vo.MonitoringAmlSearchParameters;

public class MonitoringAml extends MedallionController {
	private static Logger log = Logger.getLogger(MonitoringAml.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
		
		List<SelectItem> apiStatus = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.AML_LOOKUP_GROUP_API_STATUS);
		renderArgs.put("stringApiStatus", apiStatus);
	}
	
	@Before(only = { "entry", "save", "confirming", "confirm", "back", "clear", "edit", "approval", "view" })
	public static void setup() {
		log.debug("setup. ");
		
		
		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
		
		List<SelectItem> customerTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants._CUSTOMER_TYPE);
		renderArgs.put("customerTypes", customerTypes);

		List<SelectItem> identificationTypes = generalService
				.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._IDENTIFICATION_TYPE);
		renderArgs.put("identificationTypes", identificationTypes);
		
		List<SelectItem> supplementDoc = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._SUPPLEMENTARY_DOC);
		renderArgs.put("supplementDoc", supplementDoc);

		List<SelectItem> yesNo = UIHelper.yesNoOperators();
		renderArgs.put("yesNo", yesNo);

		List<SelectItem> amlInterfaceIndividu = UIHelper.amlInterfaceIndividuOperators();
		renderArgs.put("amlInterfaceIndividu", amlInterfaceIndividu);

		List<SelectItem> occupation = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants.AML_OCCUPATION);
		renderArgs.put("occupation", occupation);

		List<SelectItem> addressType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants._ADDRESS_TYPE);
		renderArgs.put("addressType", addressType);

		List<SelectItem> blackList = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants.AML_BLACKLIST);
		renderArgs.put("blackList", blackList);

		List<SelectItem> natureBusiness = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants.AML_NATUREBUSINESS);
		renderArgs.put("natureBusiness", natureBusiness);

		List<SelectItem> natureBusinessTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants.AML_NATUREBUSINESSTYPE);
		renderArgs.put("natureBusinessTypes", natureBusinessTypes);

		List<SelectItem> riskProfile = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants.AML_RISKPROFILE);
		renderArgs.put("riskProfile", riskProfile);

		List<SelectItem> products = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants.AML_PRODUCT);
		renderArgs.put("products", products);

		List<SelectItem> transactionFrequencies = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants.AML_FREQUENCY_TRX);
		renderArgs.put("transactionFrequencies", transactionFrequencies);

		List<SelectItem> closeReasons = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants.AML_CLOSEREASON);
		renderArgs.put("closeReasons", closeReasons);

		List<SelectItem> ownershipStructures = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				LookupConstants.AML_OWNERSHIPSTRUCTURED);
		renderArgs.put("ownershipStructures", ownershipStructures);

		List<SelectItem> sourceOfFundInd = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._SRC_OF_FUND_IND);
		renderArgs.put("sourceOfFundInd", sourceOfFundInd);

		List<SelectItem> contactType = generalService.listSelectItemContactIsAml(UIConstants.SIMIAN_BANK_ID);
		renderArgs.put("contactType", contactType);

		String typeIndi = valueParam(SystemParamConstants.PARAM_CUSTOMER_TYPE_INDIVIDUAL);
		renderArgs.put("typeIndi", typeIndi);

		String typeCorp = valueParam(SystemParamConstants.PARAM_CUSTOMER_TYPE_CORPORATE);
		renderArgs.put("typeCorp", typeCorp);

		String tipeIndividu = valueParam(SystemParamConstants.PARAM_AML_TIPEINDIVIDU);
		renderArgs.put("tipeIndividu", tipeIndividu);

		String tipeIndividuBo = valueParam(SystemParamConstants.PARAM_AML_TIPEINDIVIDU_BO);
		renderArgs.put("tipeIndividuBo", tipeIndividuBo);

		renderArgs.put("tipeIndividuKey", SystemParamConstants.PARAM_AML_TIPEINDIVIDU);

		renderArgs.put("tipeIndividuBoKey", SystemParamConstants.PARAM_AML_TIPEINDIVIDU_BO);
		
		renderArgs.put("amlStatusWaitingRiskProfile", LookupConstants.AML_RECORD_STATUS_WAITINGRIRISKPROFILE);

		renderArgs.put("amlStatusWaitingWatchlist", LookupConstants.AML_RECORD_STATUS_WATINGWATCHLIST);

		renderArgs.put("amlStatusWaitingBOWatchlist", LookupConstants.AML_RECORD_STATUS_WATINGBOWATCHLIST);

		renderArgs.put("amlStatusWaitingBOStatus", LookupConstants.AML_RECORD_STATUS_WAITINGBNEFICIALOWNERSTATUS);

		renderArgs.put("amlStatusWatingApprovalEdd", LookupConstants.AML_RECORD_STATUS_WAITINGAPPROVALEDD);

		renderArgs.put("amlStatusWatingBOApprovalEdd", LookupConstants.AML_RECORD_STATUS_WAITINGBOAPPROVALEDD);

		renderArgs.put("amlStatusDone", LookupConstants.AML_RECORD_STATUS_DONE);

		renderArgs.put("amlStatusRiskProfileUpdate", LookupConstants.AML_RECORD_STATUS_RISKPROFILEUPDATE);

		renderArgs.put("riskProfileLow", LookupConstants.AML_RISKPROFILE_L);

		renderArgs.put("riskProfileMedium", LookupConstants.AML_RISKPROFILE_M);

		renderArgs.put("riskProfileHight", LookupConstants.AML_RISKPROFILE_H);

		renderArgs.put("blackListNo", LookupConstants.AML_BLACKLIST_0);

		renderArgs.put("blackListYes", LookupConstants.AML_BLACKLIST_1);

		renderArgs.put("occupationStudent", LookupConstants.AML_OCCUPATION_STUDENT);
	}
	
	public static String valueParam(String param) {
		log.debug("valueParam. param: " + param);

		GnSystemParameter sysParam = generalService.getSystemParameter(param);
		return sysParam.getValue();
	}
	
	@Check("utility.monitoringaml")
	public static void list() {
		log.debug("list. ");
		
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_MONITORING));
		render("MonitoringAml/list.html", params, mode);
	}
	
	@Check("utility.monitoringaml")
	public static void search(MonitoringAmlSearchParameters params) {
		log.debug("params: " + params);
		
		List<AmlCustomerVO> amlCustomer = amlCustomerService.searchMonitoringAml(params.monitoringAmlKey, params.monitoringAmlName, params.retailOf, params.status);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_MONITORING));
		
		renderJSON(amlCustomer);
	}
	
	private static String getApplicationDate() {
		log.debug("getApplicationDate. ");

		GnApplicationDate current = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
		Date currentDateNonformat = current.getCurrentBusinessDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		String currentDate = dateFormat.format(currentDateNonformat);
		return currentDate;
	}

	@Check("utility.monitoringaml")
	public static void view(Long id) {
		log.debug("view. key: " + id);
		
		String param = "";
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String currentDate = getApplicationDate();
		
		AmlCustomer amlCustomer = amlCustomerService.getCustomer(id);
		boolean isActive = false;
		boolean isInterfaceDone = false;
		boolean isBeneficiaryOwner = false;
		
		if (amlCustomer.getIsActive() != null) {
			amlCustomer.getIsActive();
		}
		
		if(Helper.isNull(amlCustomer.getApiStatus())) {
			if(amlCustomer.getRecordStatus().trim().equals("I") || amlCustomer.getRecordStatus().trim().equals("N")){
				//KL2001
				param = UIConstants.DISPLAY_MODE_ENTRY;
				mode = UIConstants.DISPLAY_MODE_EDIT;
			}
		}else {
			if(amlCustomer.getApiStatus().getLookupId().equals(LookupConstants.AML_RECORD_STATUS_DONE)){
				if(amlCustomer.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED) || 
				  (amlCustomer.getApiStatus().getLookupId().equals(LookupConstants.AML_RECORD_STATUS_RISKPROFILEUPDATE))){
					//KL2002
					param = UIConstants.DISPLAY_MODE_EDIT;
					mode = UIConstants.DISPLAY_MODE_EDIT;
				}else {
					//KL2003
					param = UIConstants.DISPLAY_MODE_VIEW;
					mode = UIConstants.DISPLAY_MODE_VIEW;
				}
			}else {
				//KL2001
				param = UIConstants.DISPLAY_MODE_ENTRY;
				mode = UIConstants.DISPLAY_MODE_EDIT;
			}
		}
		
		if (amlCustomer.getApiStatus() != null) {
			if (amlCustomer.getApiStatus().getLookupId() != null && amlCustomer.getApiStatus().getLookupId().equalsIgnoreCase(LookupConstants.AML_RECORD_STATUS_DONE)) {
				isInterfaceDone = true;
			}
		}

		if (amlCustomer.getCustomerGroup() != null) {
			amlCustomer.setCustRetailFlag(true);
		} else {
			amlCustomer.setCustRetailFlag(false);
		}
		
		String lastModifiedDate = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		if (amlCustomer.getRecordModifiedDate() != null) {
			lastModifiedDate = Registry.fmtDDMMYYYYHHMMSS(amlCustomer.getRecordModifiedDate());
			amlCustomer.setLastModifDateWithTime(Registry.fmtDDMMYYYYHHMMSS(amlCustomer.getRecordModifiedDate()));
			amlCustomer.setLastModifDate(dateFormat.format(amlCustomer.getRecordModifiedDate()));
		} else {
			lastModifiedDate = Registry.fmtDDMMYYYYHHMMSS(amlCustomer.getRecordCreatedDate());
			amlCustomer.setLastModifDateWithTime(Registry.fmtDDMMYYYYHHMMSS(amlCustomer.getRecordCreatedDate()));
			amlCustomer.setLastModifDate(dateFormat.format(amlCustomer.getRecordCreatedDate()));
		}
		
		log.trace("before get contacts");
		List<AmlCustomerContact> contacts = amlCustomerService.listContacts(amlCustomer.getAmlKey());
		log.trace("after get contacts");
		
		log.trace("before get watchList");
		List<AmlCustomerWatchlist> watchList = amlCustomerService.watchList(amlCustomer.getAmlKey());
		log.trace("after get watchList");

		String detailContacts = null;
		String detailWatchList = null;

		try {
			detailContacts = json.writeValueAsString(contacts);
			detailWatchList = json.writeValueAsString(watchList);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		try {
			String fromInquiry = MenuConstants.AML_CUSTOMER_VIEW;
			String status = amlCustomer.getRecordStatus();
			if (!Helper.isNullOrEmpty(param)) {
				if (param.equalsIgnoreCase(UIConstants.DISPLAY_MODE_ENTRY)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_ENTRY));
				} else if (param.equalsIgnoreCase(UIConstants.DISPLAY_MODE_EDIT)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_EDIT));
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_VIEW));
				}
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_VIEW));
			}
			
			if (amlCustomer.getOccupation() != null) {
				log.debug("occupation not null");
				String beneficiaryOwnerValue = amlCustomerService.getBenerifiaryOwnerValue(amlCustomer.getOccupation().getLookupId());
				log.debug("beneficiaryOwnerValue: " + beneficiaryOwnerValue);
				if (beneficiaryOwnerValue != null && !beneficiaryOwnerValue.trim().isEmpty() && beneficiaryOwnerValue.equalsIgnoreCase("1")) {
					isBeneficiaryOwner = true;
				}
			}
			
			boolean isCloseReasonMandatory = isCloseReasonMandatory(amlCustomer);
			log.debug("isCloseReasonMandatory: " + isCloseReasonMandatory);
			
			boolean isReadOnly = isReadOnly(amlCustomer.getRecordStatus());
			log.debug("isReadOnly: " + isReadOnly);
			
			log.debug("isBeneficiaryOwner: " + isBeneficiaryOwner);
			render("AmlMaintenance/detail.html", amlCustomer, contacts, detailContacts, watchList, detailWatchList, mode, status, param, isActive, lastModifiedDate,
					fromInquiry, currentDate, isInterfaceDone, isCloseReasonMandatory, isReadOnly);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}		
	}
	
	private static Boolean isReadOnly(String status) {
		log.debug("isReadOnly. status: " + status);
		if (Helper.isEmpty(status)) {
			return false;
		} else if (status.trim().equalsIgnoreCase(LookupConstants.__RECORD_STATUS_APPROVED) ||
				status.trim().equalsIgnoreCase(LookupConstants.__RECORD_STATUS_REJECTED) ||
				status.trim().equalsIgnoreCase(LookupConstants.__RECORD_STATUS_NEW) 
				) {
			return true;
		} else {
			return false;
		}	
	}
	
	private static Boolean isCloseReasonMandatory(AmlCustomer amlCustomer) {
		boolean isCloseReasonMandatory = false;
		
		if (amlCustomer != null && amlCustomer.getAmlKey() != null) {
		AmlCustomer checkCustomer = amlCustomerService.getCustomer(amlCustomer.getAmlKey());
		
		
		if (checkCustomer != null) {
			if (checkCustomer.getIsActive() != null && !checkCustomer.getIsActive()) {
				if (checkCustomer.getRecordStatus() != null && 
						checkCustomer.getRecordStatus().trim().equalsIgnoreCase(LookupConstants.__RECORD_STATUS_APPROVED)) {
					isCloseReasonMandatory = true;
				}
			}
		}
		log.debug("recordStatus: " + amlCustomer.getRecordStatus());
		log.debug("isCloseReasonMandatory: " + isCloseReasonMandatory);
		}
		
		return isCloseReasonMandatory;
	}
	
	public static void back(Long id, String mode, String param) {
		log.debug("back. id: " + id + " mode: " + mode + " param: " + param);

		try {
			String currentDate = getApplicationDate();
			renderArgs.put("confirming", false);
			AmlCustomer amlCustomer = serializerService.deserialize(session.getId(), id, AmlCustomer.class);
			log.debug("AmlCustomer [confirming] = " + amlCustomer);

			List<AmlCustomerContact> contacts = new ArrayList<AmlCustomerContact>();
			if (contacts != null) {
				for (AmlCustomerContact contact : amlCustomer.getContacts()) {
						contacts.add(contact);
				}
			}
			
			List<AmlCustomerWatchlist> watchList = new ArrayList<AmlCustomerWatchlist>();
			for (AmlCustomerWatchlist wl : amlCustomer.getWatchList()) {
				watchList.add(wl);
			}

			String detailContacts = null;
			String detailWatchList = null;
			try {
				detailContacts = json.writeValueAsString(contacts);
				log.debug("back. detailWatchList: " + detailWatchList);
				detailWatchList = json.writeValueAsString(watchList);
				log.debug("back. detailWatchList: " + detailWatchList);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			render("AmlMaintenance/detail.html", amlCustomer, detailContacts, watchList, detailWatchList, mode, param, currentDate);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}
	
	public static void interfaceToAml(Long amlKey, String interfaceKey) {
		log.debug("amlKey: " + amlKey + ", interfaceKey: " + interfaceKey);
		
		try {
			Map<String,Object> interfaceAml = amlCustomerService.getInterfaceToAmlFromMonitoring(amlKey, interfaceKey, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
			log.debug("interfaceAml: " + interfaceAml);
			
			renderJSON(interfaceAml);
		} catch (Exception ex) {
			log.error("error" + ex);
			ex.printStackTrace();
			Map<String,Object> failedMap = new HashMap<String,Object>();
			failedMap.put("resultCode", "503");
			failedMap.put("resultMessage", "Failed connect to AML Interface, please try again later.!");
			renderJSON(failedMap);
		}
	}
	
	public static void interfaceToAmlCheckEddStatus(Long amlKey) {
		log.debug("amlKey: " + amlKey);
		Map<String,Object> checkEddStatus = amlCustomerService.checkEddStatus(amlKey);
		log.debug("checkEddStatus: " + checkEddStatus);
		renderJSON(checkEddStatus);
	}
	
	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit() {
		log.debug("edit. id: ");
	}

	public static void save() {
		log.debug("save. ");
	}

	public static void confirming() {
		log.debug("confirming. ");
	}

	public static void confirm() {
		log.debug("confirm. ");
	}
}
