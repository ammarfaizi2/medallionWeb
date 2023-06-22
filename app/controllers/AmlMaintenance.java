package controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.ObjectUtils;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.PropertyUdfConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.AmlCustomer;
import com.simian.medallion.model.AmlCustomerContact;
import com.simian.medallion.model.AmlCustomerWatchlist;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CfContact;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.CsAccount;
import com.simian.medallion.model.CsCustomerCharge;
import com.simian.medallion.model.GnApplicationDate;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnLookupDetail;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgInvestmentaccount;
import com.simian.medallion.model.comparators.UdfComparator;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.rest.aml.checkNameAndRiskProfile.individu.NameAndRiskProfileResponse;
import com.simian.medallion.rest.aml.checkNameAndRiskProfile.individu.NameAndRiskProfileScreeningResultResponse;
import com.simian.medallion.vo.SelectItem;

import extensions.StatusExtension;
import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import net.lingala.zip4j.exception.ZipException;
import play.Play;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.AmlCustomerSearchParameters;
import vo.CustomerSearchParameters;

@With(Secure.class)
public class AmlMaintenance extends MedallionController {
	private static Logger log = Logger.getLogger(AmlMaintenance.class);

	private static final String maxSize = "Size of file max ";

	@Before(only = { "view", "viewBankAccount", "entry", "edit", "save", "confirm", "confirming", "back", "approval",
			"add", "customerInquiryEnhancement", "printInvestorInquiry", "printUnitValuation", "historyInvestorInquiry",
			"historyInvestorUnit" })
	public static void setup() {
		log.debug("setup. ");

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

		renderArgs.put("riskProfileHigh", LookupConstants.AML_RISKPROFILE_H);

		renderArgs.put("blackListNo", LookupConstants.AML_BLACKLIST_0);

		renderArgs.put("blackListYes", LookupConstants.AML_BLACKLIST_1);

		renderArgs.put("occupationStudent", LookupConstants.AML_OCCUPATION_STUDENT);

	}

	@Before(only = { "list", UIConstants.PARAM_CUSTOMER_DEDUPE })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Check("maintenance.aml.register")
	public static void dedupe() {
		log.debug("dedupe. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String param = UIConstants.PARAM_CUSTOMER_DEDUPE;
		// String breadcrumb = "Register";
		boolean dedupe = true;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_ENTRY));
		render("AmlMaintenance/newList.html", mode, param, dedupe);
	}

	@Check("maintenance.aml.list")
	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		String breadcrumb = "Inquiry";
		String module = null;
		
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_ENTRY));
		} else if (mode.equals(UIConstants.DISPLAY_MODE_EDIT) || (param != null && param.trim().equals(UIConstants.DISPLAY_MODE_EDIT))) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_EDIT));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_VIEW));
		}
		
		renderTemplate("AmlMaintenance/newList.html", mode, param, module, breadcrumb);
	}

	@Check("maintenance.aml.view")
	public static void view(Long id, String param) {
		log.debug("view. id: " + id + " param: " + param);
		String currentDate = getApplicationDate();

		String mode = UIConstants.DISPLAY_MODE_VIEW;

		AmlCustomer amlCustomer = amlCustomerService.getCustomer(id);
		log.debug("amlCustomer name = " + amlCustomer.getCustomerName());
		
		
		boolean isInterfaceDone = false;
		
		if (amlCustomer.getApiStatus() != null) {
			if (amlCustomer.getApiStatus().getLookupId() != null && amlCustomer.getApiStatus().getLookupId().equalsIgnoreCase(LookupConstants.AML_RECORD_STATUS_DONE)) {
				isInterfaceDone = true;
			}
		}
		
		boolean isActive = false;
		
		if (amlCustomer.getIsActive() != null) {
			amlCustomer.getIsActive();
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
		log.trace("before get watchlist");
		List<AmlCustomerWatchlist> watchList = amlCustomerService.watchList(amlCustomer.getAmlKey());
		log.trace("after get watchlist");

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
			if (param != null && param.equalsIgnoreCase(UIConstants.DISPLAY_MODE_EDIT)) {
				flash.put(UIConstants.BREADCRUMB,
						applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_EDIT));
			} else {
				flash.put(UIConstants.BREADCRUMB,
						applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_VIEW));
			}

		
			boolean isCloseReasonMandatory = isCloseReasonMandatory(amlCustomer);

			if (amlCustomer.getCustomerGroup() != null) {
				amlCustomer.setCustRetailFlag(true);
			} else {
				amlCustomer.setCustRetailFlag(false);
			}
			
			render("AmlMaintenance/detail.html", amlCustomer, contacts, detailContacts, watchList, detailWatchList, mode, param, isActive, lastModifiedDate,
					fromInquiry, currentDate,isInterfaceDone, isCloseReasonMandatory);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	private static String getApplicationDate() {
		log.debug("getApplicationDate. ");

		GnApplicationDate current = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
		Date currentDateNonformat = current.getCurrentBusinessDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		String currentDate = dateFormat.format(currentDateNonformat);
		return currentDate;
	}

	@Check("maintenance.aml.register")
	public static void entry() {
		log.debug("entry. ");
		String currentDate = getApplicationDate();

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		String param = "";
		AmlCustomer amlCustomer = new AmlCustomer();

		Set<AmlCustomerContact> contacts = amlCustomer.getContacts();
		Set<AmlCustomerWatchlist> watchList = amlCustomer.getWatchList();

		String detailContacts = null;
		String detailWatchList = null;
		try {
			detailContacts = json.writeValueAsString(contacts);
			log.debug("detailContacts = " + detailContacts);
			detailWatchList = json.writeValueAsString(watchList);
			log.debug("detailWatchList = " + detailWatchList);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_ENTRY));
		render("AmlMaintenance/detail.html", mode, param, amlCustomer, contacts, detailContacts, watchList, detailWatchList, currentDate);
	}

	@Check("maintenance.aml.edit")
	public static void edit(Long id, String param, String mode) {
		log.debug("edit. id: " + id + " param: " + param + " mode: " + mode);
		String currentDate = getApplicationDate();

		AmlCustomer amlCustomer = amlCustomerService.getCustomer(id);
		
		boolean isInterfaceDone = false;
		
		if (amlCustomer.getApiStatus() != null) {
			if (amlCustomer.getApiStatus().getLookupId() != null && amlCustomer.getApiStatus().getLookupId().equalsIgnoreCase(LookupConstants.AML_RECORD_STATUS_DONE)) {
				isInterfaceDone = true;
			}
		} else if (amlCustomer.getRecordStatus() != null && amlCustomer.getRecordStatus().trim().equalsIgnoreCase(LookupConstants.__RECORD_STATUS_NEW)) {
			isInterfaceDone = true;
		}

		if (amlCustomer.getCustomerGroup() != null) {
			amlCustomer.setCustRetailFlag(true);
		} else {
			amlCustomer.setCustRetailFlag(false);
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		if (amlCustomer.getRecordModifiedDate() != null) {
			amlCustomer.setLastModifDateWithTime(Registry.fmtDDMMYYYYHHMMSS(amlCustomer.getRecordModifiedDate()));
			amlCustomer.setLastModifDate(dateFormat.format(amlCustomer.getRecordModifiedDate()));
		} else {
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
			log.debug("edit detailWatchList" + detailWatchList);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		try {

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
			
			boolean isBeneficiaryOwner = getBeneficiary(amlCustomer);
			log.debug("isBeneficiaryOwner: " + isBeneficiaryOwner);
			
			boolean isReadOnly = isReadOnly(amlCustomer.getRecordStatus());
			log.debug("isReadOnly: " + isReadOnly);
			
			boolean isCloseReasonMandatory = isCloseReasonMandatory(amlCustomer);
			
			render("AmlMaintenance/detail.html", amlCustomer, contacts, detailContacts, watchList, detailWatchList, mode, status, currentDate, param, isBeneficiaryOwner, isInterfaceDone,isReadOnly,isCloseReasonMandatory);

		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("maintenance.aml.list")
	public static void amlCustomerPaging(Paging page, AmlCustomerSearchParameters param) {
		log.debug("amlCustomerPaging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("p.amlKey", param.amlKeyOperator,
					UIHelper.withOperator(param.amlSearchKey, param.amlKeyOperator));
			page.addParams("p.customerName", param.customerNameOperator,
					UIHelper.withOperator(param.customerSearchName, param.customerNameOperator));
			page.addParams(
					Helper.searchAll("(p.amlKey||p.customerName||to_char(p.birthDate,'"
							+ Helper.dateOracle(appProp.getDateFormat()) + "')||"
							+ "p.identificationNo||trim(p.recordStatus)||p.isActive)"),
					Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = amlCustomerService.newPagingCustomer(page);
		}
		renderJSON(page);
	}

	@Check("maintenance.aml.save")
	public static void save(String mode, AmlCustomer amlCustomer, List<AmlCustomerContact> contacts, List<AmlCustomerWatchlist> watchList, String param, String apiStatus, Integer amlStatus, String amlId, String eddId) throws ZipException {
		log.debug("amlCustomerIsActive: " + amlCustomer.getIsActive());
		if (apiStatus != null && !apiStatus.trim().isEmpty()) {
			amlCustomer.setApiStatus(wrapLookup(apiStatus));
		}
		if (amlStatus != null) {
			amlCustomer.setAmlStatus(amlStatus);
		}
		if (amlId != null && !amlId.trim().isEmpty()) {
			if (!amlId.equalsIgnoreCase("**AUTO**")) {
				amlCustomer.setAmlId(amlId);
			}
		}
		log.debug("save. mode: " + mode + " amlCustomer: " + amlCustomer + " contacts: " + contacts + " watchList: " + watchList + " apiStatus: " + apiStatus + " amlStatus: " + amlStatus);
		
		if (watchList != null && !watchList.isEmpty()) {
			List<AmlCustomerWatchlist> newWL = new ArrayList<AmlCustomerWatchlist>(watchList);
			watchList.clear();
			watchList = new ArrayList<AmlCustomerWatchlist>();
			for (AmlCustomerWatchlist acw : newWL) {

				if(acw.getMessageApi() != null && !acw.getMessageApi().trim().isEmpty()) {
					
					String decodeData = new String(Base64.decode(acw.getMessageApi().getBytes()));	
					log.debug("decodeData: " + decodeData);
					acw.setMessageApi(decodeData);
					watchList.add(acw);
					
				}
			}
		}

		Long id = amlCustomer.getAmlKey();

		String detailContacts = null;
		String detailWatchList = null;
		try {
			if (contacts == null) {
				contacts = new ArrayList<AmlCustomerContact>();
			}
			log.debug("save before writeContactAsString");
			detailContacts = json.writeValueAsString(contacts);
			log.debug("save before writeContactAsString: " + contacts);
			log.debug("save before writeWatchListAsString");
			detailWatchList = json.writeValueAsString(watchList);
			log.debug("save before writeWatchListAsString: " + watchList);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		
		if (contacts != null) {
			for (AmlCustomerContact contact : contacts) {
				amlCustomer.getContacts().add(contact);
			}
		}
		
		if (watchList != null) {
			for (AmlCustomerWatchlist wl : watchList) {
				log.debug("messageApi: " + wl.getMessageApi());
				amlCustomer.getWatchList().add(wl);
			}
		}
		
		validationForm(amlCustomer);
		if (validation.hasErrors()) {
			String currentDate = getApplicationDate();
			if (detailWatchList == null || (detailWatchList != null && detailWatchList.trim().isEmpty())
					|| (detailWatchList != null && detailWatchList.trim().equalsIgnoreCase("null"))) {
				detailWatchList = "[]";
			}
			if (detailContacts == null || (detailContacts != null && detailContacts.trim().isEmpty())
					|| (detailContacts != null && detailContacts.trim().equalsIgnoreCase("null"))) {
				detailContacts = "[]";
			}
			
			log.debug("isActive: " + amlCustomer.getIsActive());
			
			boolean isCloseReasonMandatory = isCloseReasonMandatory(amlCustomer);
			boolean isError = true;
			Boolean isInActive = amlCustomer.getIsActive();

			boolean isBeneficiaryOwner = getBeneficiary(amlCustomer);
			
			render("AmlMaintenance/detail.html", amlCustomer, contacts, detailContacts, watchList, detailWatchList, mode, param, currentDate,isCloseReasonMandatory, isError, isInActive,eddId,isBeneficiaryOwner);
		}
		log.debug("before serialize: " + amlCustomer);
		serializerService.serialize(session.getId(), id, amlCustomer);
		log.debug("after serialize: " + amlCustomer);
		confirming(id, mode, param, eddId);
	}

	@Check("maintenance.aml.save")
	public static void confirming(Long id, String mode, String param, String eddId) {
		log.debug("confirming. id: " + id + " mode: " + mode + " param: " + param);

		try {
			String currentDate = getApplicationDate();
			renderArgs.put("confirming", true);
			AmlCustomer amlCustomer = serializerService.deserialize(session.getId(), id, AmlCustomer.class);
			log.debug("AmlCustomer [confirming] = " + amlCustomer);
			List<AmlCustomerContact> contacts = new ArrayList<AmlCustomerContact>();
			for (AmlCustomerContact contact : amlCustomer.getContacts()) {
				contacts.add(contact);
			}
			
			List<AmlCustomerWatchlist> watchList = new ArrayList<AmlCustomerWatchlist>();
			for (AmlCustomerWatchlist wl : amlCustomer.getWatchList()) {
				watchList.add(wl);
			}

			String detailContacts = null;
			String detailWatchList = null;
			try {
				detailContacts = json.writeValueAsString(contacts);
				log.debug("detailContacts: " + detailContacts);
				detailWatchList = json.writeValueAsString(watchList);
				log.debug("detailWatchList: " + detailWatchList);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			boolean isCloseReasonMandatory = isCloseReasonMandatory(amlCustomer);

			boolean isBeneficiaryOwner = getBeneficiary(amlCustomer);
			
			log.debug("confirming. isActive: " + amlCustomer.getIsActive());
			render("AmlMaintenance/detail.html", amlCustomer, contacts, detailContacts, watchList, detailWatchList, mode, param, currentDate, isCloseReasonMandatory,eddId,isBeneficiaryOwner);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("maintenance.aml.save")
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

	@Check("maintenance.aml.save")
	public static void confirm(Long id, String mode, String param, AmlCustomer amlCustomer, List<AmlCustomerContact> contacts, List<AmlCustomerWatchlist> watchList,String status,String apiStatus,Integer amlStatus) {
		log.debug("amlCustomerIsActive: " + amlCustomer.getIsActive());
		if (apiStatus != null && !apiStatus.trim().isEmpty()) {
			amlCustomer.setApiStatus(wrapLookup(apiStatus));
		}
		if (amlStatus != null) {
			amlCustomer.setAmlStatus(amlStatus);
		}
		log.debug("confirm. id: " + id + " mode: " + mode + " amlCustomer: " + amlCustomer + " contacts: " + contacts + " watchList: " + watchList + " status: " + status + " apiStatus: " + apiStatus + " amlStatus: " + amlStatus);
		String currentDate = getApplicationDate();

		if (contacts == null) {
			contacts = new ArrayList<AmlCustomerContact>();
		}
		
		if (watchList == null) {
			watchList = new ArrayList<AmlCustomerWatchlist>();
		} else  {
			List<AmlCustomerWatchlist> newWL = new ArrayList<AmlCustomerWatchlist>(watchList);
			watchList.clear();
			watchList = new ArrayList<AmlCustomerWatchlist>();
			for (AmlCustomerWatchlist acw : newWL) {

				if(acw.getMessageApi() != null && !acw.getMessageApi().trim().isEmpty()) {

					String decodeData = new String(Base64.decode(acw.getMessageApi().getBytes()));	
					log.debug("decodeData: " + decodeData);
					acw.setMessageApi(decodeData);
					watchList.add(acw);
					
				}
			}
		}

		String detailContacts = null;
		String detailWatchList = null;
		try {
			detailContacts = json.writeValueAsString(contacts);
			log.debug("confirm. detailContacts: " + detailContacts);
			detailWatchList = json.writeValueAsString(watchList);
			log.debug("confirm. detailWatchList: " + detailWatchList);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		
		try {
			if (contacts != null) {
				for (AmlCustomerContact contact : contacts) {
					if (contact != null) {
						amlCustomer.getContacts().add(contact);
					}
				}
			}
			
			if (watchList != null) {
				for (AmlCustomerWatchlist wl : watchList) {
					if (wl != null) {
						amlCustomer.getWatchList().add(wl);
					}
				}
			}

			if (amlCustomer != null) {
				Helper.showProperties(amlCustomer);

				// set first_name, middle_name, last_name to uppercase
				if (amlCustomer.getFirstName() != null)
					amlCustomer.setFirstName(amlCustomer.getFirstName().toUpperCase());

				if (amlCustomer.getMiddleName() != null)
					amlCustomer.setMiddleName(amlCustomer.getMiddleName().toUpperCase());

				if (amlCustomer.getLastName() != null)
					amlCustomer.setLastName(amlCustomer.getLastName().toUpperCase());

				String menuCode = null;
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
					menuCode = MenuConstants.AML_CUSTOMER_ENTRY;
				} else {
					menuCode = MenuConstants.AML_CUSTOMER_EDIT;
				}
				StringBuffer error = new StringBuffer();
				Map<String, AmlCustomerContact> currContact = new HashMap<String, AmlCustomerContact>();
				
				if (UIConstants.DISPLAY_MODE_EDIT.equals(mode)) {
					for (AmlCustomerContact _c : amlCustomer.getContacts()) {
						if(_c.getContactKey() != null){
							currContact.put(amlCustomer.getAmlKey()+"-"+_c.getAddressType().getLookupId(), _c);
						}
						if (_c.getContactKey() == null) {
							AmlCustomerContact contact = new AmlCustomerContact();
							contact = amlCustomerService.getContactByCustomerAndType(amlCustomer.getAmlKey(), _c.getAddressType().getLookupId());
							if (contact != null)
								_c.setContactKey(contact.getContactKey());
						} else {
							AmlCustomerContact contact = amlCustomerService.getContactByCustomerAndType(amlCustomer.getAmlKey(), _c.getAddressType().getLookupId());
							AmlCustomerContact contactCache = currContact.get(amlCustomer.getAmlKey()+"-"+_c.getAddressType().getLookupId());
							if(contact != null && contactCache != null){
								if(!contact.getContactKey().equals(contactCache.getContactKey())) {
									error.append(_c.getAddressType().getLookupDescription()+",");
								}
							}
						}
					}
				}
				
				if(error.length() > 0) throw new MedallionException("Contact Type "+error.toString().substring(0, error.length()-1)+" Already Exist ");
				
				amlCustomerService.saveCustomer(menuCode, amlCustomer, session.get(UIConstants.SESSION_USERNAME), "",
						session.get(UIConstants.SESSION_USER_KEY));

				if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY) || (param != null && param.trim().equals(UIConstants.DISPLAY_MODE_ENTRY))) {
					// param = null;
					dedupe();
				} else {
					param = UIConstants.DISPLAY_MODE_EDIT;
					list(mode, param);
				}
			} else {
				entry();
			}
		} catch (MedallionException e) {
			log.error(e);
			Map<String, String> data = new HashMap<String, String>();

			Validation.clear();
			if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				param = UIConstants.DISPLAY_MODE_EDIT;
			}
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_ENTRY));
			if (!param.equals("")) {
				if (param.equals(UIConstants.DISPLAY_MODE_EDIT)) {
					flash.put(UIConstants.BREADCRUMB,
							applicationService.getMenuBreadcrumb(MenuConstants.AML_CUSTOMER_EDIT));
				}
			}
			flash.error("Code : ' " + amlCustomer.getAmlKey() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			if (amlCustomer.getAmlKey() != null)
				id = amlCustomer.getAmlKey();
			render("AmlMaintenance/detail.html", amlCustomer, detailContacts, detailWatchList, mode, confirming, status, param, currentDate);
		}
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation
				+ " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		String currentDate = getApplicationDate();
		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			AmlCustomer amlCustomer = json.readValue(maintenanceLog.getNewData(), AmlCustomer.class);
			
			//TODO: add by Fadly #7558
			if(!Helper.isNull(amlCustomer.getCustomerGroup())) {
				amlCustomer.setCustRetailFlag(true);
			}
			//end #7558
			
			List<AmlCustomerContact> contacts = new ArrayList<AmlCustomerContact>(amlCustomer.getContacts());
			
			List<AmlCustomerWatchlist> watchList = new ArrayList<AmlCustomerWatchlist>(amlCustomer.getWatchList());

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

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}

			boolean isBeneficiaryOwner = getBeneficiary(amlCustomer);

			render("AmlMaintenance/approval.html", amlCustomer, contacts, detailContacts, watchList, detailWatchList, mode, taskId, operation, maintenanceLogKey, from,
					currentDate,isBeneficiaryOwner);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug(
				"approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			log.debug(">>> operation = " + operation);
			String data = amlCustomerService.approveCustomer(session.get(UIConstants.SESSION_USERNAME), taskId,
					operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			Map<String, Object> result = Formatter.resultSuccess();
			renderJSON(result);
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug(
				"reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);
		
		try {
			amlCustomerService.approveCustomer(session.get(UIConstants.SESSION_USERNAME), taskId, operation,
					maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static String valueParam(String param) {
		log.debug("valueParam. param: " + param);

		GnSystemParameter sysParam = generalService.getSystemParameter(param);
		return sysParam.getValue();
	}

	public static String valueLookupId(String lookupId) {
		log.debug("valueLookupId. lookupId: " + lookupId);

		GnLookup sysParam = generalService.getLookupReference(lookupId);
		return sysParam.getLookupId();
	}

	public static GnLookup wrapLookup(String lookupId) {
		log.debug("wrapLookup. lookupId: " + lookupId);

		GnLookup lookup = new GnLookup();
		lookup.setLookupId(lookupId);
		
		return lookup;
	}

	public static void isActiveAccountExist(String customerNo) {

		boolean value = false;
		log.debug("isi customerNo " + customerNo);
		if (customerNo != null) {
			List<CsAccount> activeAccounts = accountService.getActiveAccountsByCustomer(customerNo);
			if (activeAccounts != null && activeAccounts.size() > 0)
				value = true;
		}
		log.debug("isi value " + value);
		renderText(value);
	}
	
	public static void interfaceToAml(Long amlKey, String interfaceKey) {
		log.debug("amlKey: " + amlKey + ", interfaceKey: " + interfaceKey);
		try {
			Map<String,Object> interfaceAml = amlCustomerService.interfaceToAml(amlKey, interfaceKey);
			log.debug("interfaceAml: " + interfaceAml);
			renderJSON(interfaceAml);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			Map<String,Object> failedMap = new HashMap<String,Object>();
			failedMap.put("resultCode", "503");
			failedMap.put("resultMessage", "Failed connect to AML Interface, please try again later.!");
			renderJSON(failedMap);
		}
	}
	
	public static void interfaceToAmlConfirmScreening(Long amlKey, List<String> dataIds) {
		log.debug("amlKey: " + amlKey + ", dataIds: " + dataIds);
		Map<String,Object> confirmScreening = amlCustomerService.confirmScreening(amlKey, dataIds);
		log.debug("confirmScreening: " + confirmScreening);
		renderJSON(confirmScreening);
	}
	
	public static void interfaceToAmlCheckEddStatus(Long amlKey) {
		log.debug("amlKey: " + amlKey);
		Map<String,Object> checkEddStatus = amlCustomerService.checkEddStatus(amlKey);
		log.debug("checkEddStatus: " + checkEddStatus);
		renderJSON(checkEddStatus);
	}

	private static void validationForm(AmlCustomer customer) {
		
		log.debug("is active: " + customer.getIsActive());
		//TODO: Modify by Fadly #8235
		AmlCustomer checkCustomer = amlCustomerService.getCustomer(customer.getAmlKey());
		if((customer.getBlackList().getLookupId().equals(LookupConstants.AML_BLACKLIST_1)) && (!checkCustomer.getIsActive())) {
			if (customer.getCloseReason()  == null ){
				validation.required("Close Reason is", customer.getCloseReason());
			} else {
				validation.required("Close Reason is", customer.getCloseReason().getLookupId());
			}
		}
		//End #8235
		if (customer.getIsActive() != null && !customer.getIsActive()) {
			if (customer != null && customer.getAmlKey() != null) {
				if (checkCustomer != null) {
					if (checkCustomer.getIsActive() != null) {
						if (checkCustomer.getIsActive()) {
							if (customer.getIsActive() != null && !customer.getIsActive()) {
								if (customer.getCloseReason()  == null ){
									validation.required("Close Reason is", customer.getCloseReason());
								} else {
									validation.required("Close Reason is", customer.getCloseReason().getLookupId());
								}
								
							}
						} else {
							if (checkCustomer.getIsActive() != null && !checkCustomer.getIsActive()) {
								if (checkCustomer.getRecordStatus() != null &&
										(checkCustomer.getRecordStatus().trim().equalsIgnoreCase(LookupConstants.__RECORD_STATUS_APPROVED)) 
										) {
									if (customer.getCloseReason()  == null ){
										validation.required("Close Reason is", customer.getCloseReason());
									} else {
										validation.required("Close Reason is", customer.getCloseReason().getLookupId());
									}
									
								}
							}
						}
					}
				}
			}
		}

		if (customer.getCustomerType() == null) {
			validation.required("Customer Type is", customer.getCustomerType());
		} else {
			validation.required("Customer Type is", customer.getCustomerType().getLookupId());
		}
		if (LookupConstants.CUSTOMER_TYPE_INDIVIDUAL.equals(customer.getCustomerType().getLookupId())) {
			validation.required("First Name is", customer.getFirstName());
			if (customer.getMiddleName() != null && !customer.getMiddleName().trim().isEmpty()) {
				validation.required("Last Name is", customer.getLastName());
			}
			if (customer.getIdentificationType() == null) {
				validation.required("Identification 1 Type is", customer.getIdentificationType());
			} else {
				validation.required("Identification 1 Type is", customer.getIdentificationType().getLookupId());
			}
			validation.required("Identification Number is", customer.getIdentificationNo());
			
			validation.required("Place of Birth is", customer.getBirthPlace());
			validation.required("Date Of Birth is", customer.getBirthDate());

			if (customer.getOccupation() == null) {
				validation.required("Occupation is", customer.getOccupation());
			} else {
				validation.required("Occupation is", customer.getOccupation().getLookupId());
			}
			// validation.required("Position is", customer.getPosition());
			// validation.required("Mother Maiden Name in is", customer.getMotherName());
		}else if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {

			if (customer.getNatureBusinessType() == null) {
				validation.required("Nature Business is", customer.getNatureBusinessType());
			} else {
				validation.required("Nature Business Type is", customer.getNatureBusinessType().getLookupId());
			}
			validation.required("Company Name is", customer.getCustomerName());
			if (customer.getDocumentType() == null) {
				validation.required("Document Type is", customer.getDocumentType());
			} else {
				validation.required("Document Type is", customer.getDocumentType().getLookupId());
			}
			validation.required("Document No is", customer.getDocumentNo());
			validation.required("Date Of Company Estb is", customer.getBirthDate());
			if (customer.getOwnershipStructure() == null) {
				validation.required("Ownership Structure is", customer.getOwnershipStructure());
			} else {
				validation.required("Ownership Structure is", customer.getOwnershipStructure().getLookupId());
			}
		}
		log.debug("isCustRetailFlag: " + customer.isCustRetailFlag());
		if (customer.isCustRetailFlag()) {
			if (customer.getCustomerGroup() == null) {
				validation.required("Customer Retail Of is", customer.getCustomerGroup());
			} else {
				validation.required("Customer Retail Of is", customer.getCustomerGroup().getThirdPartyKey());
			}
			validation.required("External CIF No. is", customer.getExternalNo());
		}

		if (customer.getProduct() == null) {
			validation.required("Product is", customer.getProduct());
		} else {
			validation.required("Product is", customer.getProduct().getLookupId());
		}
		if (customer.getTransactionFrequency() == null) {
			validation.required("Transaction Frequency is", customer.getTransactionFrequency());
		} else {
			validation.required("Transaction Frequency is", customer.getTransactionFrequency().getLookupId());
		}
		if (customer.getNatureBusiness() == null) {
			validation.required("Nature Business is", customer.getNatureBusiness());
		} else {
			validation.required("Nature Business is", customer.getNatureBusiness().getLookupId());
		}
		if (customer.getAddressType() == null) {
			validation.required("Address Type is", customer.getAddressType());
		} else {
			validation.required("Address Type is", customer.getAddressType().getLookupId());
		}
		validation.required("Address is", customer.getAddress1());
		if (customer.getAddress1Country() == null) {
			validation.required("Country is", customer.getAddress1Country());
		} else {
			validation.required("Country is", customer.getAddress1Country().getLookupId());
		}
		if (customer.getAddress1State() == null) {
			validation.required("State is", customer.getAddress1State());
		} else {
			validation.required("State is", customer.getAddress1State().getLookupId());
		}
		if (customer.getAddress1Area() == null) {
			validation.required("Area is", customer.getAddress1Area());
		} else {
			validation.required("Area is", customer.getAddress1Area().getLookupId());
		}
		if (customer.getBlackList() != null) {
			if (customer.getBlackList().getLookupId().equalsIgnoreCase(LookupConstants.AML_BLACKLIST_0)) {
				validation.required("High Reason is", customer.getHighReason());
			}
		}
		
		if (customer.getRiskProfile() != null) {
			if (customer.getRiskProfile().getLookupId().equalsIgnoreCase(LookupConstants.AML_RISKPROFILE_H)) {
				validation.required("High Reason is", customer.getHighReason());
			}
		}
		
		String hightReason = customer.getHighReason();
		if(hightReason != null && hightReason.length() > 50) {
			validation.required("High Reason").message("max 50 character");
			
		}
		
	}
	
	public static void chekIsBenefiary(String lookupId) {

		
		boolean isBeneficiaryOwner = false;
		
		log.debug("occupation not null");
		String beneficiaryOwnerValue = amlCustomerService.getBenerifiaryOwnerValue(lookupId);
		log.debug("beneficiaryOwnerValue: " + beneficiaryOwnerValue);
		if (beneficiaryOwnerValue != null && !beneficiaryOwnerValue.trim().isEmpty() && beneficiaryOwnerValue.equalsIgnoreCase("1")) {
			isBeneficiaryOwner = true;
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("isBeneficiaryOwner",isBeneficiaryOwner);
		
		renderJSON(data);

	}
	
	public static void checkIdentityAndNpwp(String amlKey, String identity, String npwp) {
		
		Long amlKeyToLong = null;
		
		if (amlKey != null && !amlKey.trim().isEmpty()) {
			amlKeyToLong = Long.parseLong(amlKey);
		}
		
		Map<String,Object> response = amlCustomerService.checkIdentityAndNpwp(amlKeyToLong, identity, npwp);
		
		renderJSON(response);
		
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
	
	public static void sourceOfFundList(String param) {

		List<SelectItem> sourceOfFund = new ArrayList<SelectItem>();
		
		if (param != null) {
			if (param.equalsIgnoreCase("IND")) {
				sourceOfFund = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._SRC_OF_FUND_IND);
			} else if (param.equalsIgnoreCase("CORP")) {
				sourceOfFund = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._SRC_OF_FUND_CORP);
			}
		}
		
		renderJSON(sourceOfFund);
	}
	
	private static Boolean getBeneficiary(AmlCustomer amlCustomer) {

		boolean isBeneficiaryOwner= false;
		
		if (amlCustomer.getOccupation() != null) {
			log.debug("occupation not null");
			String beneficiaryOwnerValue = amlCustomerService.getBenerifiaryOwnerValue(amlCustomer.getOccupation().getLookupId());
			log.debug("beneficiaryOwnerValue: " + beneficiaryOwnerValue);
			if (beneficiaryOwnerValue != null && !beneficiaryOwnerValue.trim().isEmpty() && beneficiaryOwnerValue.equalsIgnoreCase("1")) {
				isBeneficiaryOwner = true;
			}
		}
		
		return isBeneficiaryOwner;
	}
	
}
