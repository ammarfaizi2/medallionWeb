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
import org.springframework.util.StringUtils;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.PropertyUdfConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.AmlCustomer;
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
import com.simian.medallion.model.RgPortfolio;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.comparators.UdfComparator;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import extensions.StatusExtension;
import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import net.lingala.zip4j.exception.ZipException;
import play.Play;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.AccountSearchParameters;
import vo.CustomerSearchParameters;

@With(Secure.class)
public class Customers extends MedallionController {
	private static Logger log = Logger.getLogger(Customers.class);

	private static final String maxSize = "Size of file max ";

	@Before(only = { "list", UIConstants.PARAM_CUSTOMER_DEDUPE })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "paginglist", UIConstants.PARAM_CUSTOMER_DEDUPE })
	public static void setupPagingList() {
		log.debug("setupPagingList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);
	}

	@Before(only = { "nonpaginglist", UIConstants.PARAM_CUSTOMER_DEDUPE })
	public static void setupNonPagingList() {
		log.debug("setupNonPagingList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);
	}

	@Before(only = { "view", "viewBankAccount", "entry", "edit", "save", "confirm", "confirming", "back", "approval", "add", "customerInquiryEnhancement", "printInvestorInquiry", "printUnitValuation", "historyInvestorInquiry", "historyInvestorUnit" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> yesNo = UIHelper.yesNoOperators();
		renderArgs.put("yesNo", yesNo);

		List<SelectItem> addressTypeCorp = generalService.listLookupsFilteredForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ADDRESS_TYPE, UIConstants.ADDRESS_TYPE_REFERENCE_KEY, LookupConstants.CUSTOMER_TYPE_CORPORATE);
		renderArgs.put("addressTypeCorp", addressTypeCorp);

		List<SelectItem> addressTypeInd = generalService.listLookupsFilteredForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ADDRESS_TYPE, UIConstants.ADDRESS_TYPE_REFERENCE_KEY, LookupConstants.CUSTOMER_TYPE_INDIVIDUAL);
		renderArgs.put("addressTypeInd", addressTypeInd);

		List<SelectItem> addressType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ADDRESS_TYPE);
		renderArgs.put("addressType", addressType);

		List<SelectItem> companyCharacteristic = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._COMPANY_CHARACTERISTIC);
		renderArgs.put("companyCharacteristic", companyCharacteristic);

		List<SelectItem> customerGroupsForCorp = generalService.listLookupsFilteredForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CUSTOMER_GROUP, UIConstants.CUSTOMER_GROUP_REFERENCE_KEY, LookupConstants.CUSTOMER_TYPE_CORPORATE);
		renderArgs.put("customerGroupsForCorp", customerGroupsForCorp);

		List<SelectItem> customerGroupsForInd = generalService.listLookupsFilteredForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CUSTOMER_GROUP, UIConstants.CUSTOMER_GROUP_REFERENCE_KEY, LookupConstants.CUSTOMER_TYPE_INDIVIDUAL);
		renderArgs.put("customerGroupsForInd", customerGroupsForInd);

		List<SelectItem> customerTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CUSTOMER_TYPE);
		renderArgs.put("customerTypes", customerTypes);

		List<SelectItem> education = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._EDUCATION);
		renderArgs.put("education", education);

		List<SelectItem> gender = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._GENDER);
		renderArgs.put("gender", gender);

		List<SelectItem> identificationTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._IDENTIFICATION_TYPE);
		renderArgs.put("identificationTypes", identificationTypes);

		List<SelectItem> incomeCorp = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ASSET_CORP);
		renderArgs.put("incomeCorp", incomeCorp);

		List<SelectItem> profitCorp = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PROFIT_CORP);
		renderArgs.put("profitCorp", profitCorp);

		List<SelectItem> incomeInd = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._INCOME_IND);
		renderArgs.put("incomeInd", incomeInd);

		List<SelectItem> legalDomicile = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._LEGAL_DOMICILE);
		renderArgs.put("legalDomicile", legalDomicile);

		List<SelectItem> maritalStatus = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._MARITAL_STATUS);
		renderArgs.put("maritalStatus", maritalStatus);

		List<SelectItem> occupation = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._OCCUPATION);
		renderArgs.put("occupation", occupation);

		List<SelectItem> purposeOfInvCorp = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PURPOSE_OF_INV_CORP);
		renderArgs.put("purposeOfInvCorp", purposeOfInvCorp);

		List<SelectItem> purposeOfInvInd = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PURPOSE_OF_INV_IND);
		renderArgs.put("purposeOfInvInd", purposeOfInvInd);

		List<SelectItem> religion = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._RELIGION);
		renderArgs.put("religion", religion);

		// List<SelectItem> sourceOfFundCorp =
		// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants._SRC_OF_FUND_CORP);
		// renderArgs.put("sourceOfFundCorp", sourceOfFundCorp);
		//
		// List<SelectItem> sourceOfFundInd =
		// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants._SRC_OF_FUND_IND);
		// renderArgs.put("sourceOfFundInd", sourceOfFundInd);

		List<SelectItem> typeOfBusiness = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TYPE_OF_BUSINESS);
		renderArgs.put("typeOfBusiness", typeOfBusiness);

		List<SelectItem> customerCategory = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CUSTOMER_CATEGORY);
		renderArgs.put("customerCategory", customerCategory);

		List<GnLookup> sourceOfFundInd = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._SRC_OF_FUND_IND);
		renderArgs.put("sourceOfFundInd", sourceOfFundInd);

		List<GnLookup> investObjInd = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._PURPOSE_OF_INV_IND);
		renderArgs.put("investObjInd", investObjInd);

		List<GnLookup> sourceOfFundCorp = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._SRC_OF_FUND_CORP);
		renderArgs.put("sourceOfFundCorp", sourceOfFundCorp);

		List<GnLookup> investObjCorp = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._PURPOSE_OF_INV_CORP);
		renderArgs.put("investObjCorp", investObjCorp);

		List<SelectItem> assetOwner = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ASSET_OWNER);
		renderArgs.put("assetOwner", assetOwner);

		List<SelectItem> invoiceCharge = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._INVOICE_CHARGE);
		renderArgs.put("invoiceCharge", invoiceCharge);

		List<SelectItem> contactType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CONTACT_TYPE);
		renderArgs.put("contactType", contactType);

		List<SelectItem> supplementDoc = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._SUPPLEMENTARY_DOC);
		log.debug("SUPPLEMENTDOC = " + supplementDoc);
		renderArgs.put("supplementDoc", supplementDoc);
		// List<SelectItem> customerGroups =
		// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants._CUSTOMER_GROUP, filter);
		// renderArgs.put("customerGroups", customerGroups);

		// List<SelectItem> investorTypes =
		// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants._INVESTOR_TYPE);
		// renderArgs.put("investorTypes", investorTypes);

		// List<SelectItem> investorTypeCorps =
		// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants._INVESTOR_TYPE_CORP);
		// renderArgs.put("investorTypeCorps", investorTypeCorps);
		//
		// List<SelectItem> investorTypeIndvs =
		// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants._INVESTOR_TYPE_INDV);
		// renderArgs.put("investorTypeIndvs", investorTypeIndvs);

		// List<SelectItem> addressTypeCorp =
		// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants._CF_ADDRESS_TYPE_CORP);
		// renderArgs.put("addressTypeCorp", addressTypeCorp);

		String otherEducation = valueParam(SystemParamConstants.PARAM_EDUCATION_OTHER);
		renderArgs.put("otherEducation", otherEducation);

		String otherOccupation = valueParam(SystemParamConstants.PARAM_OCCUPATION_OTHER);
		renderArgs.put("otherOccupation", otherOccupation);

		// String tniPolriOccupation =
		// valueParam(SystemParamConstants.PARAM_OCCUPATION_TNI_POLRI);
		// renderArgs.put("tniPolriOccupation", tniPolriOccupation);

		String enterpreneuerOccupation = valueParam(SystemParamConstants.PARAM_OCCUPATION_ENTERPRENEUER);
		renderArgs.put("enterpreneuerOccupation", enterpreneuerOccupation);

		String otherTypeBusiness = valueParam(SystemParamConstants.PARAM_TYPE_BUSINESS_OTHER);
		renderArgs.put("otherTypeBusiness", otherTypeBusiness);

		String otherCompCharacter = valueParam(SystemParamConstants.PARAM_COMPANY_CHARACTERISTIC_OTHER);
		renderArgs.put("otherCompCharacter", otherCompCharacter);

		String otherSourceOfFundInd = valueParam(SystemParamConstants.PARAM_SOURCE_OF_FUND_IND_OTHER);
		renderArgs.put("otherSourceOfFundInd", otherSourceOfFundInd);

		String otherSourceOfFundCorp = valueParam(SystemParamConstants.PARAM_SOURCE_OF_FUND_CORP_OTHER);
		renderArgs.put("otherSourceOfFundCorp", otherSourceOfFundCorp);

		String otherInvObjInd = valueParam(SystemParamConstants.PARAM_INVESTMENT_OBJ_IND_OTHER);
		renderArgs.put("otherInvObjInd", otherInvObjInd);

		String otherInvObjCorp = valueParam(SystemParamConstants.PARAM_INVESTMENT_OBJ_CORP_OTHER);
		renderArgs.put("otherInvObjCorp", otherInvObjCorp);

		String idKtp = valueParam(SystemParamConstants.PARAM_ID_KTP);
		renderArgs.put("idKtp", idKtp);

		String idPassport = valueParam(SystemParamConstants.PARAM_ID_PASSPORT);
		renderArgs.put("idPassport", idPassport);

		String maritalStatusMarried = valueParam(SystemParamConstants.PARAM_MARITAL_STATUS_MARRIED);
		renderArgs.put("maritalStatusMarried", maritalStatusMarried);

		String typeIndi = valueParam(SystemParamConstants.PARAM_CUSTOMER_TYPE_INDIVIDUAL);
		renderArgs.put("typeIndi", typeIndi);

		String typeCorp = valueParam(SystemParamConstants.PARAM_CUSTOMER_TYPE_CORPORATE);
		renderArgs.put("typeCorp", typeCorp);

		String directCat = valueParam(SystemParamConstants.PARAM_CUSTOMER_CATEGORY_DIRECT);
		renderArgs.put("directCat", directCat);

		String indirectCat = valueParam(SystemParamConstants.PARAM_CUSTOMER_CATEGORY_INDIRECT);
		renderArgs.put("indirectCat", indirectCat);

		GnSystemParameter custExchangeRate = generalService.getSystemParameter(SystemParamConstants.CUSTOMER_EXCHANGE_RATE);
		renderArgs.put("custExchangeRate", (custExchangeRate != null) ? custExchangeRate.getValue() : null);

		renderArgs.put("menuInquiry", MenuConstants.CF_MASTER_VIEW);
		renderArgs.put("countryId", LookupConstants.COUNTRY_LOCAL);
		renderArgs.put("addressType1", LookupConstants.ADDRESS_TYPE_1);
		renderArgs.put("addressType2", LookupConstants.ADDRESS_TYPE_2);
		renderArgs.put("lookupCustType", PropertyUdfConstants.FIELD_CUSTOMER_TYPE);

		String validateAttach = valueParam(SystemParamConstants.ORGANIZATION_CUSTOMER_ATTACHMENT);
		StringBuffer paramValidate = new StringBuffer();
		String valAttach = "";
		String mSize = "";
		if (!Helper.isNullOrEmpty(validateAttach)) {
			String[] arrAttach = validateAttach.split("\\|");
			mSize = arrAttach[0];
			String endWith = "";
			if (arrAttach != null && arrAttach.length > 0) {
				if (!arrAttach[0].isEmpty()) {
					paramValidate.append(maxSize + arrAttach[1]);
				}
				paramValidate.append(" (");
				boolean isEndWith = false;
				for (int i = 2; i < arrAttach.length; i++) {
					paramValidate.append(arrAttach[i] + ",");
					isEndWith = true;
				}
				if (isEndWith)
					endWith = ")";
			}

			valAttach = paramValidate.substring(0, paramValidate.length() - 1) + endWith;
		}
		renderArgs.put("validateAttach", valAttach);
		renderArgs.put("maxSize", mSize);
		
		
		GnSystemParameter paramCashProjection = generalService.getSystemParameter(SystemParamConstants.PARAM_CASH_PROJECTION);
		renderArgs.put("paramCashProjection", (paramCashProjection != null) ? paramCashProjection.getValue() : 0);
		
		GnSystemParameter paramUseSwift = generalService.getSystemParameter(SystemParamConstants.PARAM_USING_SWIFT);
		renderArgs.put("paramUseSwift", (paramUseSwift != null)? paramUseSwift.getValue() : null);
		
		List<SelectItem> listSwiftStatus = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._SWIFT_STATUS);
		renderArgs.put("listSwiftStatus", listSwiftStatus);
		
		renderArgs.put("swiftStatusNA", LookupConstants._SWIFT_STATUS_NA);
		
		renderArgs.put("typeBusinessInsurance", LookupConstants._TYPE_OF_BUSINESS_INSURANCE);
		
	}

	@Check("maintenance.customer.register")
	public static void dedupe() {
		log.debug("dedupe. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String param = UIConstants.PARAM_CUSTOMER_DEDUPE;
		// String breadcrumb = "Register";
		boolean dedupe = true;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
		render("Customers/newList.html", mode, param, dedupe);
	}

	@Check("maintenance.customer.list")
	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		// param = "register-cust-acct";
		String breadcrumb = "Inquiry";
		String module = null;
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
		} else if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_VIEW));
		}
		if (param != null) {
			if (param.contains("cust-acct")) {
				module = "Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
			}
			if (param.contains("bank-acct")) {
				module = "Bank Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
			}
			if (param.contains("invt-acct")) {
				module = "Investment Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
			}
		}
		renderTemplate("Customers/newList.html", mode, param, module, breadcrumb);
	}

	@Check("maintenance.customer.list")
	public static void paginglist(String mode, String param) {
		log.debug("paginglist. mode: " + mode + " param: " + param);

		String breadcrumb = "Inquiry";
		String module = null;
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
		} else if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_VIEW));
		}
		if (param != null) {
			if (param.contains("cust-acct")) {
				module = "Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
			}
			if (param.contains("bank-acct")) {
				module = "Bank Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
			}
			if (param.contains("invt-acct")) {
				module = "Investment Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
			}
		}
		renderTemplate("Customers/paginglist.html", mode, param, module, breadcrumb);
	}

	@Check("maintenance.customer.list")
	public static void nonpaginglist(String mode, String param) {
		log.debug("nonpaginglist. mode: " + mode + " param: " + param);

		String breadcrumb = "Inquiry";
		String module = null;
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
		} else if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_VIEW));
		}
		if (param != null) {
			if (param.contains("cust-acct")) {
				module = "Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
			}
			if (param.contains("bank-acct")) {
				module = "Bank Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
			}
			if (param.contains("invt-acct")) {
				module = "Investment Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
			}
		}
		renderTemplate("Customers/nonpaginglist.html", mode, param, module, breadcrumb);
	}

	@Check("maintenance.customer.list")
	public static void search(CustomerSearchParameters params, String param) {
		log.debug("search. params: " + params + " param: " + param);

		List<CfMaster> customers = customerService.searchCustomer(UIHelper.withOperator(params.customerSearchNo, params.customerNoOperator), UIHelper.withOperator(params.customerSearchName, params.customerNameOperator),
		// params.customerSearchBirthDate,
				UIHelper.withOperator(params.customerSearchContactName, params.contactNoOperator), LookupConstants.DOMAIN_CUSTOMER);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
		render("Customers/grid.html", customers, param);

	}

	@Check("maintenance.customer.list")
	public static void customerPaging(Paging page, CustomerSearchParameters param) {
		log.debug("customerPaging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("p.domain", page.EQUAL, LookupConstants.DOMAIN_CUSTOMER);
			page.addParams("p.customerNo", param.customerNoOperator, UIHelper.withOperator(param.customerSearchNo, param.customerNoOperator));
			page.addParams("p.customerName", param.customerNameOperator, UIHelper.withOperator(param.customerSearchName, param.customerNameOperator));
			page.addParams("c.contactName", param.contactNoOperator, UIHelper.withOperator(param.customerSearchContactName, param.contactNoOperator));
			page.addParams(Helper.searchAll("(p.customerNo||p.customerName||to_char(p.birthDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "p.identification1No||trim(p.recordStatus)||p.isActive)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = customerService.newPagingCustomer(page);
		}
		renderJSON(page);
	}

	@Check("maintenance.customer.list")
	public static void paging(Paging page, CustomerSearchParameters param) {
		log.debug("paging. page: " + page + " param: " + param);

		page.addParams("p.domain", Paging.EQUAL, LookupConstants.DOMAIN_CUSTOMER);
		page.addParams("p.customerNo", param.customerNoOperator, UIHelper.withOperator(param.customerSearchNo, param.customerNoOperator));
		page.addParams("p.customerName", param.customerNameOperator, UIHelper.withOperator(param.customerSearchName, param.customerNameOperator));
		page.addParams("(p.customerName||p.customerNo)", Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		page = customerService.pagingCustomer(page);
		renderJSON(page);
	}

	@Check("maintenance.customer.list")
	public static void nonPaging(Paging page, CustomerSearchParameters param) {
		log.debug("nonPaging. page: " + page + " param: " + param);

		page.addParams("p.domain", Paging.EQUAL, LookupConstants.DOMAIN_CUSTOMER);
		page.addParams("p.customerNo", param.customerNoOperator, UIHelper.withOperator(param.customerSearchNo, param.customerNoOperator));
		page.addParams("p.customerName", param.customerNameOperator, UIHelper.withOperator(param.customerSearchName, param.customerNameOperator));
		page.addParams("c.contactName", param.contactNoOperator, UIHelper.withOperator(param.customerSearchContactName, param.contactNoOperator));

		page = customerService.nonPagingCustomer(page);
		renderJSON(page);
	}

	@Check("maintenance.customer.view")
	public static void view(Long id, String param) {
		log.debug("view. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_VIEW;

		CfMaster customer = customerService.getCustomer(id);
		log.debug("customer name = " + customer.getCustomerName());

		boolean isActive = customer.getIsActive();

		String lastModifiedDate = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		if (customer.getRecordModifiedDate() != null) {
			lastModifiedDate = Registry.fmtDDMMYYYYHHMMSS(customer.getRecordModifiedDate());
			customer.setLastModifDateWithTime(Registry.fmtDDMMYYYYHHMMSS(customer.getRecordModifiedDate()));
			customer.setLastModifDate(dateFormat.format(customer.getRecordModifiedDate()));
		} else {
			lastModifiedDate = Registry.fmtDDMMYYYYHHMMSS(customer.getRecordCreatedDate());
			customer.setLastModifDateWithTime(Registry.fmtDDMMYYYYHHMMSS(customer.getRecordCreatedDate()));
			customer.setLastModifDate(dateFormat.format(customer.getRecordCreatedDate()));
		}
		String currentDate = getApplicationDate();
		if (customer.getCustomerGroup() != null) {
			customer.setCustRetailFlag(true);
		} else {
			customer.setCustRetailFlag(false);
		}
		String nameFile = null;
		if (!Helper.isNullOrEmpty(customer.getAttachment())) {
			int dash = customer.getAttachment().indexOf("-");
			nameFile = customer.getAttachment().substring(dash + 1);
			customer.setFlagAttachFile(true);
		}

		// Set bank account and currency invoice
		BnAccount bankAccountInvoice = bankAccountService.getInvoiceBankAccountByCustomer(customer.getCustomerKey());
		log.debug(">>>  bank Invoice = " + bankAccountInvoice);
		if (bankAccountInvoice != null) {
			customer.setOriBankAccountInvoice(bankAccountInvoice);
			customer.getOriBankAccountInvoice().setCurrency(bankAccountInvoice.getCurrency());
			customer.setBankAccountInvoice(bankAccountInvoice);
			customer.getBankAccountInvoice().setCurrency(bankAccountInvoice.getCurrency());
		}

		List<RgInvestmentaccount> invs = new ArrayList<RgInvestmentaccount>();
		/*
		 * List<RgInvestmentaccount> invs =
		 * taService.listInvestmentForCustomers(customer.getCustomerKey());
		 * logger.debug("size invs = " + invs.size()); for (RgInvestmentaccount
		 * inv : invs) { logger.debug("from view >>> accountNumber = " +
		 * inv.getAccountNumber()); BigDecimal balanceAmount =
		 * taService.getBalanceAmountForInvestmentAccountInquiry
		 * (inv.getAccountNumber()); logger.debug("balanceAmount = " +
		 * balanceAmount); inv.setBalanceAmount(balanceAmount); }
		 */

		List<CfContact> contacts = customerService.listContacts(customer.getCustomerKey());
		List<CsCustomerCharge> customerCharges = customerService.listCustomerChargesByCustomer(customer.getCustomerKey());

		List<CfContact> contactAddTypes = new ArrayList<CfContact>();
		List<CfContact> contactTypes = new ArrayList<CfContact>();
		for (CfContact cfContact : contacts) {
			if (cfContact.getIsCustomer() == true) {
				if (cfContact.getAddress1() != null) {
					String[] address = cfContact.getAddress1().split("\n");
					if (address.length == 1) {
						cfContact.setAddressExt1(address[0].trim());
					}
					if (address.length == 2) {
						cfContact.setAddressExt1(address[0].trim());
						cfContact.setAddressExt2(address[1].trim());
					}
					if (address.length == 3) {
						cfContact.setAddressExt1(address[0].trim());
						cfContact.setAddressExt2(address[1].trim());
						cfContact.setAddressExt3(address[2].trim());
					}
				}
				contactAddTypes.add(cfContact);
			}

			if (cfContact.getIsCustomer() == false) {
				cfContact.setContactType(cfContact.getAddressType());
				contactTypes.add(cfContact);
			}
		}

		String sofData = customer.getSourceOfFund();
		String invData = customer.getPurposeOfInvestment();

		String detailContacts = null;
		String detailCorpContacts = null;
		String dataCustomerCharges = null;

		try {
			detailContacts = json.writeValueAsString(contactAddTypes);
			detailCorpContacts = json.writeValueAsString(contactTypes);
			dataCustomerCharges = json.writeValueAsString(customerCharges);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		try {
			// START UDFS
			List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			Map<String, String> data = new HashMap<String, String>();
			// START UDFS
			if (customer.getUdf() != null)
				data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));

			// List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			// List<GnUdfMaster> udfsIndi =
			// generalService.listUdfMasterCustomerTypeIndividual();
			// List<GnUdfMaster> udfsCorp =
			// generalService.listUdfMasterCustomerTypeCorporate();
			if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
				udfs = generalService.listUdfMasterCustomerTypeIndividual();
			}
			if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
				udfs = generalService.listUdfMasterCustomerTypeCorporate();
			}
			List<GnUdfMaster> udfsIndi = new ArrayList<GnUdfMaster>();
			List<GnUdfMaster> udfsCorp = new ArrayList<GnUdfMaster>();
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					// START UDF FOR DROPDOWN
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// END DROPDOWN

					if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
						udfsIndi.add(udf);
					}

					if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
						udfsCorp.add(udf);
					}
				}
			}
			// END UDFS

			if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {
				customer.setBirthPlaceOtherDummy(customer.getBirthPlaceOther());
			}
			String fromInquiry = null;
			if (param != null) {
				if (param.equals(UIConstants.DISPLAY_MODE_EDIT)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));
				} else if (param.equals("register-cust-acct")) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
				} else if (param.equals("register-bank-acct")) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
				} else if (param.equals("register-invt-acct")) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
				} else if (param.equals(UIConstants.DISPLAY_MODE_VIEW)) {
					fromInquiry = MenuConstants.CF_MASTER_VIEW;
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_VIEW));
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
				}
			} else {
				fromInquiry = MenuConstants.CF_MASTER_VIEW;
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_VIEW));
			}
			nameFile = Helper.removeExtensionZip(nameFile);
			for (BnAccount bn : customer.getBankAccounts()) {
				bn.setRecordStatus(StatusExtension.decodeStatus(bn.getRecordStatus()));
			}
			
			render("Customers/detail.html", customer, mode, param, contacts, detailContacts, udfs, udfsIndi, udfsCorp, isActive, invs, currentDate, detailCorpContacts, dataCustomerCharges, sofData, invData, lastModifiedDate, nameFile, fromInquiry);
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
	}

	public static void viewBankAccount(String accountNo, String param) {
		log.debug("viewBankAccount. accountNo: " + accountNo + " param: " + param);

		if (param.equals("register-cust") || param.equals("register-cust-acct") || param.equals("-cust") || param.equals("register-cust-acct-cust") || param.equals("list-cust-acct")) {
			String mode = UIConstants.DISPLAY_MODE_VIEW + param;
			CsAccount account = accountService.getCustAccount(accountNo);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
			render("Accounts/detail.html", account, mode, param);
		}

		if (param.equals("register-bank") || param.equals("register-bank-acct") || param.equals("-bank") || param.equals("register-bank-acct-bank") || param.equals("list-bank-acct")) {
			String mode = UIConstants.DISPLAY_MODE_VIEW + param;
			BnAccount bankAccount = bankAccountService.getBankAccountNo(accountNo);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
			render("BankAccounts/detail.html", bankAccount, mode, param);
		}

		if (param.equals("register-invt") || param.equals("register-invt-acct") || param.equals("-invt") || param.equals("register-invt-acct-invt") || param.equals("list-invt-acct")) {
			String mode = UIConstants.DISPLAY_MODE_VIEW + param;
			RgInvestmentaccount rgInvestmentAccount = taService.loadInvestment(accountNo);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
			render("RegistryInvestment/detail.html", rgInvestmentAccount, mode, param);
		}
	}

	@Check("maintenance.customer.register")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		String param = "";
		CfMaster customer = new CfMaster();
		customer.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		Set<CfContact> contacts = customer.getContacts();
		String currentDate = getApplicationDate();
		log.debug("current date = " + currentDate);
		GnApplicationDate current = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
		customer.setJointDate(current.getCurrentBusinessDate());

		String sofData = customer.getSourceOfFund();
		String invData = customer.getPurposeOfInvestment();

		String detailContacts = null;
		String detailCorpContacts = null;
		String dataCustomerCharges = null;
		try {
			detailContacts = json.writeValueAsString(customer.getContacts());
			detailCorpContacts = json.writeValueAsString(customer.getContacts());
			dataCustomerCharges = json.writeValueAsString(customer.getCustomerCharges());
			log.debug("detailContacts = " + detailContacts);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		List<GnUdfMaster> udfsCorp = generalService.listUdfMasterCustomerTypeCorporate();
		if (udfsCorp != null) {
			for (GnUdfMaster udf : udfsCorp) {
				// START UDF FOR DROPDOWN
				if (udf.getLookupGroup() != null) {
					udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
					udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
				}
				// END DROPDOWN
			}
		}
		List<GnUdfMaster> udfs = generalService.listUdfMasterCustomerTypeAll();
		if (udfs != null) {
			for (GnUdfMaster udf : udfs) {
				// START UDF FOR DROPDOWN
				if (udf.getLookupGroup() != null) {
					udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
					udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
				}
				// END DROPDOWN
			}
		}
		List<GnUdfMaster> udfsIndi = generalService.listUdfMasterCustomerTypeIndividual();
		// List<GnUdfMaster> udfsIndi = new ArrayList<GnUdfMaster>();
		if (udfsIndi != null) {
			for (GnUdfMaster udf : udfsIndi) {
				// START UDF FOR DROPDOWN
				if (udf.getLookupGroup() != null) {
					udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
					udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
				}
				// END DROPDOWN
			}
		}

		GnLookup defaultLookup = generalService.getLookup(LookupConstants.SIMIAN_ORGANIZATION_ID, LookupConstants._PAYMENT_METHOD, LookupConstants._PAYMENT_METHOD_DEFAULT_CODE);
		customer.setPaymentMethod(defaultLookup);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
		render("Customers/detail.html", mode, param, customer, udfs, udfsCorp, udfsIndi, detailContacts, detailCorpContacts, dataCustomerCharges, contacts, currentDate, sofData, invData);
	}

	@Check("maintenance.customer.edit")
	public static void edit(Long id, String param) {
		log.debug("edit. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CfMaster customer = customerService.getCustomer(id);

		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		if (customer.getRecordModifiedDate() != null) {
			customer.setLastModifDateWithTime(Registry.fmtDDMMYYYYHHMMSS(customer.getRecordModifiedDate()));
			customer.setLastModifDate(dateFormat.format(customer.getRecordModifiedDate()));
		} else {
			customer.setLastModifDateWithTime(Registry.fmtDDMMYYYYHHMMSS(customer.getRecordCreatedDate()));
			customer.setLastModifDate(dateFormat.format(customer.getRecordCreatedDate()));
		}
		String currentDate = getApplicationDate();

		if (customer.getCustomerGroup() != null) {
			customer.setCustRetailFlag(true);
		} else {
			customer.setCustRetailFlag(false);
		}
		String nameFile = null;
		if (!Helper.isNullOrEmpty(customer.getAttachment())) {
			int dash = customer.getAttachment().indexOf("-");
			nameFile = customer.getAttachment().substring(dash + 1);
			customer.setFlagAttachFile(true);
		}
		
		List<CfContact> contacts = customerService.listContacts(customer.getCustomerKey());
		List<RgInvestmentaccount> invs = taService.listInvestmentForCustomers(customer.getCustomerKey());
		List<CsCustomerCharge> customerCharges = customerService.listCustomerChargesByCustomer(customer.getCustomerKey());

		customer.putToListBankAccounts();
		customer.putToListCustodyAccounts();
		customer.putToListInvestmentAccounts();

		List<CfContact> contactAddTypes = new ArrayList<CfContact>();
		List<CfContact> contactTypes = new ArrayList<CfContact>();
		for (CfContact cfContact : contacts) {
			if (cfContact.getIsCustomer() == true) {
				if(cfContact.getAddress1() == null) cfContact.setAddress1("");
				String[] address = cfContact.getAddress1().split("\n");
				if (address.length == 1) {
					cfContact.setAddressExt1(address[0].trim());
				}
				if (address.length == 2) {
					cfContact.setAddressExt1(address[0].trim());
					cfContact.setAddressExt2(address[1].trim());
				}
				if (address.length == 3) {
					cfContact.setAddressExt1(address[0].trim());
					cfContact.setAddressExt2(address[1].trim());
					cfContact.setAddressExt3(address[2].trim());
				}
				contactAddTypes.add(cfContact);
			}

			if (cfContact.getIsCustomer() == false) {
				cfContact.setContactType(cfContact.getAddressType());
				contactTypes.add(cfContact);
			}
		}

		// Set bank account and currency invoice
		BnAccount bankAccountInvoice = bankAccountService.getInvoiceBankAccountByCustomer(customer.getCustomerKey());
		log.debug(">>>  bank Invoice = " + bankAccountInvoice);
		if (bankAccountInvoice != null) {
			customer.setOriBankAccountInvoice(bankAccountInvoice);
			customer.getOriBankAccountInvoice().setCurrency(bankAccountInvoice.getCurrency());
			customer.setBankAccountInvoice(bankAccountInvoice);
			customer.getBankAccountInvoice().setCurrency(bankAccountInvoice.getCurrency());
		}

		String sofData = customer.getSourceOfFund();
		String invData = customer.getPurposeOfInvestment();

		String status = customer.getRecordStatus();
		log.debug("status = --" + status + "--");
		String detailContacts = null;
		String detailCorpContacts = null;
		String dataCustomerCharges = null;
		try {
			// JsonHelper json = new JsonHelper().getCustomerSerializer();
			// detailContacts = json.serialize(customer.getContacts());
			detailContacts = json.writeValueAsString(contactAddTypes);
			detailCorpContacts = json.writeValueAsString(contactTypes);
			dataCustomerCharges = json.writeValueAsString(customerCharges);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		try {
			List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			Map<String, String> data = new HashMap<String, String>();
			// START UDFS
			if (customer.getUdf() != null)
				data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));

			// List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			// List<GnUdfMaster> udfsIndi =
			// generalService.listUdfMasterCustomerTypeIndividual();
			// List<GnUdfMaster> udfsCorp =
			// generalService.listUdfMasterCustomerTypeCorporate();
			if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
				udfs = generalService.listUdfMasterCustomerTypeIndividual();
			}
			if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
				udfs = generalService.listUdfMasterCustomerTypeCorporate();
			}
			List<GnUdfMaster> udfsIndi = new ArrayList<GnUdfMaster>();
			List<GnUdfMaster> udfsCorp = new ArrayList<GnUdfMaster>();
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					// START UDF FOR DROPDOWN
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// END DROPDOWN

					if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
						udfsIndi.add(udf);
					}

					if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
						udfsCorp.add(udf);
					}
				}
			}
			// END UDFS

			if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {
				customer.setBirthPlaceOtherDummy(customer.getBirthPlaceOther());
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));

			nameFile = Helper.removeExtensionZip(nameFile);
			
			render("Customers/detail.html", customer, mode, detailContacts, detailCorpContacts, dataCustomerCharges, contacts, udfs, udfsIndi, udfsCorp, status, currentDate, param, invs, sofData, invData, bankAccountInvoice, nameFile);

		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
	}

	private static void setData(CfMaster customer, List<CsAccount> accounts, List<BnAccount> bankAccounts, List<CfContact> contacts, List<CfContact> corpContacts, List<RgInvestmentaccount> invs, List<CsCustomerCharge> customerCharges) {
		log.debug("setData. customer: " + customer + " accounts: " + accounts + " bankAccounts: " + bankAccounts + " contacts: " + contacts + " corpContacts: " + corpContacts + " invs: " + invs + " customerCharges: " + customerCharges);

		if (customer != null) {
			if (accounts != null) {
				customer.setAccounts(new HashSet<CsAccount>(accounts));
			}

			if (bankAccounts != null) {
				customer.setBankAccounts(new HashSet<BnAccount>(bankAccounts));
				log.debug("from setData >>> size of bankAccounts = " + bankAccounts.size());
			}

			if (customer.getCustomerKey() != null) {
				invs = taService.listInvestmentForCustomers(customer.getCustomerKey());
			}

			if (invs != null) {
				log.debug("from setData >>> size of invs before add = " + invs.size());
				customer.setRgInvestmentaccounts(new HashSet<RgInvestmentaccount>(invs));
				log.debug("from setData >>> size of invs = " + invs.size());
			}

			if (contacts != null) {
				if (corpContacts != null) {
					for (CfContact contact : corpContacts) {
						contacts.add(contact);
					}
				}
				customer.setContacts(new HashSet<CfContact>(contacts));
			}

			if (customerCharges != null) {
				customer.setCustomerCharges(new HashSet<CsCustomerCharge>(customerCharges));
			}
		}
	}

	@Check("maintenance.customer.save")
	public static void save(String mode, CfMaster customer, List<CsAccount> accounts, List<BnAccount> bankAccounts, List<RgInvestmentaccount> invs, List<CfContact> contacts, GnUdfMaster[] udfs, List<CfContact> corpContacts, List<CsCustomerCharge> customerCharges, String status, String param, File attachment, String[] sourceOfFunds, String[] investObjs, BnAccount bankAccountInvoice) throws ZipException {
		log.debug("save. mode: " + mode + " customer: " + customer + " accounts: " + accounts + " bankAccounts: " + bankAccounts + " invs: " + invs + " contacts: " + contacts + " udfs: " + udfs + " corpContacts: " + corpContacts + " customerCharges: " + customerCharges + " status: " + status + " param: " + param + " attachment: " + attachment + " sourceOfFunds: " + sourceOfFunds + " investObjs: " + investObjs + " bankAccountInvoice: " + bankAccountInvoice);
		log.debug("amlCustomer: " + customer.getAmlCustomer());
		if (customer != null) {
			// START UDFS
			String currentDate = getApplicationDate();

			try {
				Map<String, String> data = new HashMap<String, String>();
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						log.debug("udf.getFieldName() = " + udf.getFieldName());
						log.debug("udf.getValue() = " + udf.getValue());
						data.put(udf.getFieldName(), udf.getValue());
						// START UDF FOR DROPDOWN
						log.debug("udf lookup group = " + udf.getLookupGroup().getLookupGroup());
						// if (udf.getLookupGroup() != null) {
						if (!udf.getLookupGroup().getLookupGroup().isEmpty()) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
						// END DROPDOWN
					}
					String udfString = json.writeValueAsString(data);
					customer.setUdf(udfString);
				}
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
			// END UDFS

			if (accounts != null) {
				for (CsAccount account : accounts) {
					if (account != null) {
						customer.getAccounts().add(account);
					}
				}
			}

			if (bankAccounts != null) {
				for (BnAccount bankAccount : bankAccounts) {
					if (bankAccount != null) {
						customer.getBankAccounts().add(bankAccount);
					}
				}
			}

			if (invs != null) {
				for (RgInvestmentaccount inv : invs) {
					if (inv != null) {
						customer.getRgInvestmentaccounts().add(inv);
					}
				}
			}
			// try{
			if (customer.getCustomerKey() != null) {
				bankAccountInvoice = bankAccountService.getInvoiceBankAccountByCustomer(customer.getCustomerKey());
			}
			/*
			 * }catch(Exception e){ String url =
			 * Router.getFullUrl("Application.index"); redirect(url); }
			 */

			String detailContacts = null;
			String detailCorpContacts = null;
			String dataCustomerCharges = null;
			try {
				if (contacts == null) {
					contacts = new ArrayList<CfContact>();
				}
				if (corpContacts == null) {
					corpContacts = new ArrayList<CfContact>();
				}
				if (customerCharges == null) {
					customerCharges = new ArrayList<CsCustomerCharge>();
				}
				// logger.debug("from save >>> contacts size = " +
				// contacts.size());
				detailContacts = json.writeValueAsString(contacts);
				detailCorpContacts = json.writeValueAsString(corpContacts);
				log.debug("from save >>> detailContacts = " + detailContacts);
				dataCustomerCharges = json.writeValueAsString(customerCharges);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			// VALIDATE HERE

			log.debug("what error = " + (validation.errorsMap().values().containsAll(contacts)));
			if (validation.errorsMap().values().containsAll(contacts) == false) {
				log.debug(">>> 1. contacts = " + contacts.size());
				validation.clear();
				log.debug(">>> 2. contacts = " + contacts.size());
			}
			// setData(customer, accounts, bankAccounts, contacts);

			boolean isAttachment = false;

			log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			log.debug("attachment  = "+attachment);
			log.debug("customer.getAttachment "+customer.getAttachment());
			if (attachment != null) {
				boolean isMatchAttach = true;

				String ext = "";
				try {
					ext = FilenameUtils.getExtension(attachment.getName());
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					isMatchAttach = false;
					validation.addError("", Messages.get(e.getMessage()));
				}

				if (isMatchAttach) {
					GnSystemParameter sysParam = generalService.getSystemParameter(SystemParamConstants.ORGANIZATION_CUSTOMER_ATTACHMENT);
					String[] valueParam = null;
					if (sysParam != null && !Helper.isNullOrEmpty(sysParam.getValue())) {
						valueParam = sysParam.getValue().split("\\|");
						double sizeFile = attachment.length() / 1024;
						if (valueParam != null && valueParam.length > 0) {
							if (!valueParam[0].isEmpty() && !valueParam[1].isEmpty() && sizeFile > Double.parseDouble(valueParam[0].trim())) {
								isMatchAttach = false;
								validation.addError("", maxSize + valueParam[1]);
							} else {
								if (valueParam.length > 2) {
									isMatchAttach = false;
									if (!Helper.isNullOrEmpty(ext)) {
										for (String type : valueParam) {
											if (type.trim().equalsIgnoreCase(ext.trim())) {
												isMatchAttach = true;
											}
										}
										if (!isMatchAttach) {
											validation.addError("", "Invalid Type Document in tab Additional");
										}
									}
								}
							}
						}
					}
				}

				if (isMatchAttach) {
					String uploadDir = Play.configuration.getProperty("upload.customer");
					String newFileName = uploadDir + System.currentTimeMillis() + "-" + attachment.getName();

					File output = new File(newFileName);
					UIHelper.copyFile(attachment, output);

					File zipoutput = Helper.createZipFile(Helper.convertToZip(newFileName, true), output.getAbsolutePath());
					output.delete();

					customer.setAttachment(zipoutput.getName());
					isAttachment = true;
				}
				log.debug("attachment is not null");
			} else {
//				customer.setAttachment(null);
				//dihilangin karena nanti kalo pas edit dy gk milih file apa2, terus klik save ada error
				log.debug("------------------------");
				log.debug("is attach flag "+customer.isFlagAttachFile());
				if(!customer.isFlagAttachFile())customer.setAttachment(null);
				log.debug("attachment is null");
			}

			// VALIDATION ON TAB MAIN
			validation.required("Customer Type in tab Main is", customer.getCustomerType().getLookupId());
			// validation.required("Customer Code in tab Main is",
			// customer.getCustomerNo());
			if (LookupConstants.CUSTOMER_TYPE_INDIVIDUAL.equals(customer.getCustomerType().getLookupId())) {
				validation.required("First Name in tab Main is", customer.getFirstName());
				if (!customer.getMiddleName().equals("")) {
					validation.required("Last Name in tab Main is", customer.getLastName());
				}
			}
			if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {
				validation.required("Company Name in tab Main is", customer.getCustomerName());
			}
			validation.required("Customer Category in tab Main is", customer.getCustomerCategory().getLookupId());
			if (customer.getCustomerCategory().getLookupId().equals(LookupConstants.CUSTOMER_CATEGORY_INDIRECT))
				validation.required("Direct SID in tab Main is", customer.getDirectId());

			if (customer.isCustRetailFlag()) {
				validation.required("Customer Retail Of in tab Main is", customer.getCustomerGroup().getThirdPartyCode());
				validation.required("External CIF No. in tab Main is", customer.getExternalNo());
			}

			validation.required("Currency in tab Main is", customer.getCurrency().getCurrencyCode());
			validation.required("Tax Profile in tab Main is", customer.getTaxProfile().getTaxProfileCode());
			validation.required("APOLO Code in tab Main is", customer.getLkpbu().getLookupCode()); //#6066 use getLookupCode instead of getLookupId for validation required 
			validation.required("Joint Date in tab Main is", customer.getJointDate());

			if (UIConstants.DISPLAY_MODE_EDIT.equals(mode)) {
				List<CsAccount> accountActives = accountService.listAccountActiveByCustomerKey(customer.getCustomerKey());
				if (customer.getIsActive() == false) {
					if ((accountActives.size() > 0)) {
						validation.addError("", "Can not be inactive because there are still some of its accounts are active");
					}
				}
			}

			// VALIDATION ON TAB INDIVIDUAL
			if (LookupConstants.CUSTOMER_TYPE_INDIVIDUAL.equals(customer.getCustomerType().getLookupId())) {
				validation.required("Tax ID in tab Individual is", customer.getTaxId().getLookupId());
				validation.required("Nationality in tab Individual is", customer.getNationality().getLookupId());
				if (customer.getCustomerCategory().getLookupId().equals(LookupConstants.CUSTOMER_CATEGORY_DIRECT)) {
					// validation.required("Place Of Birth (State) in tab Individual is",
					// customer.getBirthPlace().getLookupId());
					// validation.required("Place Of Birth (Area) in tab Individual is",
					// customer.getBirthArea().getLookupId());
					validation.required("Place of Birth is", customer.getBirthPlaceOther());
					validation.required("Date Of Birth in tab Individual is", customer.getBirthDate());
					validation.required("Gender in tab Individual is", customer.getGender().getLookupId());
					validation.required("Marital Status in tab Individual is", customer.getMaritalStatus().getLookupId());
					if (LookupConstants.CUSTOMER_MARTIAL_STATUS.equals(customer.getMaritalStatus().getLookupId())) {
						validation.required("Spouse Name is", customer.getSpouseName());
					}
					validation.required("Mother Maiden Name in tab Individual is", customer.getMotherName());
					validation.required("Education Background in tab Individual is", customer.getEducationBackground().getLookupId());
					if (customer.getEducationBackground().getLookupId().equals(valueParam(SystemParamConstants.PARAM_EDUCATION_OTHER))) {
						validation.required("Education Background (Others) in tab Individual is", customer.getEducationBackgroundOthers());
					}
					validation.required("Occupation in tab Individual is", customer.getOccupation().getLookupId());
					if (customer.getOccupation().getLookupId().equals(valueParam(SystemParamConstants.PARAM_OCCUPATION_OTHER))) {
						validation.required("Occupation (Others) in tab Individual is", customer.getOccupationOthers());
					}
					if (customer.getOccupation().getLookupId().equals(valueParam(SystemParamConstants.PARAM_OCCUPATION_ENTERPRENEUER))) {
						validation.required("Nature Business For The Enterpreneuer in tab Individual is", customer.getOccupationDescription());
					}
					validation.required("Income per Annum in tab Individual is", customer.getAnnualIncomeRange().getLookupId());

				}

				//s #7528
				if (customer.getNationality() != null && customer.getNationality().getLookupId() !=null && !customer.getNationality().getLookupId().trim().isEmpty() ) {
					if (customer.getNationality().getLookupId().trim().equalsIgnoreCase(LookupConstants.COUNTRY_LOCAL)) {
						validation.required("Identification 1 Type in tab Contacts is", customer.getIdentificationType1().getLookupId());
					} else {
						if (customer.getIdentificationType2() != null) {
							validation.required("Identification 2 Type in tab Contacts is", customer.getIdentificationType2().getLookupId());
						}
					}
				}
				//e #7528

				if (customer.getIdentificationType1() != null && !(customer.getIdentificationType1().getLookupId().equals(""))) {
					if (!customer.getIdentificationType1().getLookupId().equals(LookupConstants.CUSTOMER_IDENTIFICATION_TYPE_KTP)) {
						validation.required("Identification 1 Number in tab Contacts is", customer.getIdentification1No());
						validation.required("Identification 1 Expiry Date in tab Contacts is", customer.getIdentification1Expiry());
					} else {
						validation.required("Identification 1 Number in tab Contacts is", customer.getIdentification1No());
					}
				}

				if (customer.getIdentificationType2() != null && !(customer.getIdentificationType2().getLookupId().equals(""))) {
					if (!customer.getIdentificationType2().getLookupId().equals(LookupConstants.CUSTOMER_IDENTIFICATION_TYPE_KTP)) {
						validation.required("Identification 2 Number in tab Contacts is", customer.getIdentification2No());
						validation.required("Identification 2 Expiry Date in tab Contacts is", customer.getIdentification2Expiry());
					} else {
						validation.required("Identification 2 Number in tab Contacts is", customer.getIdentification2No());
					}
				}

				if (customer.getIdentificationType3() != null && !(customer.getIdentificationType3().getLookupId().equals(""))) {
					if (!customer.getIdentificationType3().getLookupId().equals(LookupConstants.CUSTOMER_IDENTIFICATION_TYPE_KTP)) {
						validation.required("Identification 3 Number in tab Contacts is", customer.getIdentification3No());
						validation.required("Identification 3 Expiry Date in tab Contacts is", customer.getIdentification3Expiry());
					} else {
						validation.required("Identification 3 Number in tab Contacts is", customer.getIdentification3No());
					}
				}

				if (customer.getIdentificationType4() != null && !(customer.getIdentificationType4().getLookupId().equals(""))) {
					if (!customer.getIdentificationType4().getLookupId().equals(LookupConstants.CUSTOMER_IDENTIFICATION_TYPE_KTP)) {
						validation.required("Identification 4 Number in tab Contacts is", customer.getIdentification4No());
						validation.required("Identification 4 Expiry Date in tab Contacts is", customer.getIdentification4Expiry());
					} else {
						validation.required("Identification 4 Number in tab Contacts is", customer.getIdentification4No());
					}
				}
			}
			//
			if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {
				validation.required("Tax ID in tab Institutional is", customer.getTaxId().getLookupId());
				validation.required("Legal Domicile in tab Institutional is", customer.getOriginCountry().getLookupId());
				validation.required("As of (year) in tab Institutional is", customer.getAsOfYear());

				if ((customer.getOriginCountry().getLookupId().equals(LookupConstants.COUNTRY_LOCAL))) {
					validation.required("NPWP Number is", customer.getNpwp());
					if (customer.getSupplementDocReq().equals("1")) {
						validation.required("Document 1 Type in tab Institutional is", customer.getIdentificationType1().getLookupId());
						validation.required("Document 1 No in tab Institutional is", customer.getIdentification1No());
						validation.required("Document 1 Expiry Date in tab Institutional is", customer.getIdentification1Expiry());
					}
				}
				if (customer.getCustomerCategory().getLookupId().equals(LookupConstants.CUSTOMER_CATEGORY_DIRECT)) {
					// validation.required("Place of Comp. Estb. (State) in tab Institutional is",
					// customer.getBirthPlace().getLookupId());
					// validation.required("Place of Comp. Estb. (Area) in tab Institutional is",
					// customer.getBirthArea().getLookupId());
					validation.required("Place of Comp. Estb. (State) in tab Institutional is", customer.getBirthPlaceOtherDummy());
					validation.required("Comp. Characteristic in tab Institutional is", customer.getCompanyCharacteristic().getLookupId());
					if (customer.getCompanyCharacteristic().getLookupId().equals(valueParam(SystemParamConstants.PARAM_COMPANY_CHARACTERISTIC_OTHER))) {
						validation.required("Comp. Characteristic (Others) in tab Institutional is", customer.getCompanyCharacteristicOthers());
					}
					validation.required("Asset Last Year in tab Institutional is", customer.getAssetYear1().getLookupId());
					validation.required("Operating Profit Last Year in tab Institutional is", customer.getProfitYear1().getLookupId());

				}
				validation.required("Date of Company Estb. in tab Institutional is", customer.getBirthDate());
				validation.required("Type Of Business in tab Institutional is", customer.getTypeOfBusiness().getLookupId());
				if (customer.getTypeOfBusiness().getLookupId().equals(valueParam(SystemParamConstants.PARAM_TYPE_BUSINESS_OTHER)))
					validation.required("Type Of Business (Others) in tab Institutional is", customer.getTypeOfBusinessOthers());

				if (customer.getOriginCountry().getLookupId().equals(LookupConstants.COUNTRY_LOCAL)) {
					validation.required("Article of Associate in tab Institutional is", customer.getArticleOfAssociation());
				} else {
					validation.required("Business Reg Cert No in tab Institutional is", customer.getSiupNo());
				}
				
				// Start #6066, validasi Sandi Asuransi required
				if (customer.getTypeOfBusiness() != null && LookupConstants._TYPE_OF_BUSINESS_INSURANCE.equals(customer.getTypeOfBusiness().getLookupId())) {
					validation.required("Sandi Asuransi in tab Institutional is", customer.getInsuranceCode().getLookupCode());//#6066 use getLookupCode instead of getLookupId for validation required
				}
				// End #6066, validasi Sandi Asuransi required

			}

			// VALIDATION ON TAB CONTACT
			if (LookupConstants.CUSTOMER_TYPE_INDIVIDUAL.equals(customer.getCustomerType().getLookupId())) {
				List<GnLookup> lookupAdds = generalService.listLookupsWithDetail(UIConstants.SIMIAN_BANK_ID, LookupConstants._ADDRESS_TYPE);
				StringBuffer addressBuffer = new StringBuffer();
				List<String> detailValueAdd = new ArrayList<String>();
				String addressType = new String();
				for (GnLookup gnLookup : lookupAdds) {
					for (GnLookupDetail gnLookupDetail : gnLookup.getDetail()) {
						if (PropertyUdfConstants.FIELD_ADDRESS_REQUIRED.equals(gnLookupDetail.getUdfMaster().getFieldName())) {
							if (gnLookupDetail.getDetailValue().equals("1")) {
								detailValueAdd.add(gnLookupDetail.getLookup().getLookupId());
								addressBuffer.append(gnLookupDetail.getLookup().getLookupDescription()).append(",");
								if (addressBuffer.length() > 1) {
									addressType = addressBuffer.substring(0, addressBuffer.length() - 1);
								}
							}
						}
					}

				}

				List<String> addressTypeScreens = new ArrayList<String>();
				for (CfContact contact : contacts) {
					addressTypeScreens.add(contact.getAddressType().getLookupId());
				}

				if (!addressTypeScreens.containsAll(detailValueAdd))
					validation.addError("Address Type", addressType + " in tab Contacts is Required");
			}

			if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {
				List<GnLookup> lookupContacts = generalService.listLookupsWithDetail(UIConstants.SIMIAN_BANK_ID, LookupConstants._CONTACT_TYPE);
				StringBuffer contactBuffer = new StringBuffer();
				List<String> detailValue = new ArrayList<String>();
				//String contactType = new String();
				for (GnLookup gnLookup : lookupContacts) {
					log.debug("CUSTOMER_TYPE_CORPORATE. gnLookup: " + gnLookup);
					for (GnLookupDetail gnLookupDetail : gnLookup.getDetail()) {
						if (PropertyUdfConstants.FIELD_CONTACT_REQUIRED.equals(gnLookupDetail.getUdfMaster().getFieldName())) {
							if (gnLookupDetail.getDetailValue() != null && gnLookupDetail.getDetailValue().equals("1")) {
								detailValue.add(gnLookupDetail.getLookup().getLookupId());
								contactBuffer.append(gnLookupDetail.getLookup().getLookupDescription()).append(",");
//								if (contactBuffer.length() > 1) {
//									contactType = contactBuffer.substring(0, contactBuffer.length() - 1);
//								}
							}
						}
					}

				}

				List<String> contactTypeScreens = new ArrayList<String>();
				for (CfContact contact : corpContacts) {
					contactTypeScreens.add(contact.getContactType().getLookupId());
				}

				if (!contactTypeScreens.containsAll(detailValue)) {
					boolean cekContact = true;
					if (!contactTypeScreens.containsAll(detailValue)) {
						StringBuffer buffer = new StringBuffer();
						for (String d_ : detailValue) {
							for (String a_ : contactTypeScreens) {
								if (d_.trim().equals(a_.trim())) {
									cekContact = false;
									break;
								}
							}
							if (cekContact) {
								GnLookup contact = generalService.getLookup(d_);
								buffer.append(contact.getLookupDescription() + ",");
							}
							cekContact = true;
						}
						validation.addError("Contact Type ", buffer.toString().substring(0, buffer.toString().length() - 1) + " in tab Contacts is Required");
					}
				}

				/*
				 * if (!contactTypeScreens.containsAll(detailValue))
				 * validation.addError("Contact Type",
				 * contactType+" in tab Contacts is Required");
				 */

				List<GnLookup> lookupAdds = generalService.listLookupsWithDetail(UIConstants.SIMIAN_BANK_ID, LookupConstants._ADDRESS_TYPE);
				StringBuffer addressBuffer = new StringBuffer();
				List<String> detailValueAdd = new ArrayList<String>();
				String addressType = new String();
				for (GnLookup gnLookup : lookupAdds) {
					for (GnLookupDetail gnLookupDetail : gnLookup.getDetail()) {
						if (PropertyUdfConstants.FIELD_ADDRESS_REQUIRED.equals(gnLookupDetail.getUdfMaster().getFieldName())) {
							if (gnLookupDetail.getDetailValue().equals("1")) {
								detailValueAdd.add(gnLookupDetail.getLookup().getLookupId());
								addressBuffer.append(gnLookupDetail.getLookup().getLookupDescription()).append(",");
								if (addressBuffer.length() > 1) {
									addressType = addressBuffer.substring(0, addressBuffer.length() - 1);
								}
							}
						}
					}

				}

				List<String> addressTypeScreens = new ArrayList<String>();
				for (CfContact contact : contacts) {
					addressTypeScreens.add(contact.getAddressType().getLookupId());
				}

				if (!addressTypeScreens.containsAll(detailValueAdd))
					validation.addError("Address Type", addressType + " in tab Contacts is Required");
			}

			if (!customer.isCustRetailFlag()) {
				validation.required("Email in tab Contacts is", customer.getEmail());
			}

			if (!Helper.isNullOrEmpty(customer.getEmail())) {
				String email = customer.getEmail();
				String[] arrEmail = email.split("\\;");
				for (int i = 0; i < arrEmail.length; i++) {
					if (!Helper.emailValidation(arrEmail[i])) {
						validation.addError("", ExceptionConstants.INVALID_EMAIL);
						break;
					}
				}
			}

			validation.required("Correspondence Address in tab Contacts is", customer.getCorrespondenceAddress().getLookupId());

			// VALIDATION ON TAB ADDITIONAL
			if (LookupConstants.CUSTOMER_TYPE_INDIVIDUAL.equals(customer.getCustomerType().getLookupId())) {
				if (customer.getCustomerCategory().getLookupId().equals(LookupConstants.CUSTOMER_CATEGORY_DIRECT)) {
					validation.required("Source of Fund in tab Additional is", sourceOfFunds);
					validation.required("Investment Objectives in tab Additional is", investObjs);
				}

				if (sourceOfFunds != null) {
					for (String string : sourceOfFunds) {
						if (string.equals(valueParam(SystemParamConstants.PARAM_SOURCE_OF_FUND_IND_OTHER))) {
							validation.required("Source of Fund (Others) in tab Additional is", customer.getSourceOfFundOthers());
						}
					}
				}

				if (investObjs != null) {
					for (String string : investObjs) {
						if (string.equals(valueParam(SystemParamConstants.PARAM_INVESTMENT_OBJ_IND_OTHER))) {
							validation.required("Investment Objectives (Others) in tab Additional is", customer.getPurposeOfInvestmentOthers());
						}
					}
				}
			}

			if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {
				if (customer.getCustomerCategory().getLookupId().equals(LookupConstants.CUSTOMER_CATEGORY_DIRECT)) {
					validation.required("Source of Fund in tab Additional is", sourceOfFunds);
					validation.required("Investment Objectives in tab Additional is", investObjs);
				}

				if (sourceOfFunds != null) {
					for (String string : sourceOfFunds) {
						if (string.equals(valueParam(SystemParamConstants.PARAM_SOURCE_OF_FUND_CORP_OTHER))) {
							validation.required("Source of Fund (Others) in tab Additional is", customer.getSourceOfFundOthers());
						}
					}
				}

				if (investObjs != null) {
					for (String string : investObjs) {
						if (string.equals(valueParam(SystemParamConstants.PARAM_INVESTMENT_OBJ_CORP_OTHER))) {
							validation.required("Investment Objectives (Others) in tab Additional is", customer.getPurposeOfInvestmentOthers());
						}
					}
				}
			}

			validation.required("Asset Owner in tab Additional is", customer.getAssetOwner().getLookupId());

			if (customer.isFlagAttachFile()) {
				log.debug("get attachment " + customer.getAttachment());
				if (Helper.isNullOrEmpty(customer.getAttachment())) {
					validation.required("Attach Document in tab Additional is", attachment);
				}
			}

			if (customer.isFlagChangeBankAccount()) {
				validation.required("Bank Account in tab Charge Invoice is", customer.getBankAccountInvoice().getAccountNo());
			}

			if (!"".equalsIgnoreCase(customer.getSwiftCode()) && customer.getSwiftStatus().getLookupId() == null){
				validation.addError("Status Via SWIFT", " in tab Additional is Required");
			}
			
			if (UIConstants.DISPLAY_MODE_EDIT.equals(mode)) {
				if (customer.getBankAccountInvoice() != null) {
					if (!customer.getBankAccountInvoice().getCurrency().getCurrencyCode().isEmpty()) {
						if (!customer.getCurrency().getCurrencyCode().equals(customer.getBankAccountInvoice().getCurrency().getCurrencyCode())) {
							validation.addError("", "in tab Charge Invoice Currency Bank Account for invoicing should be same " + customer.getCurrency().getCurrencyCode());
						}
					}
				}
			}

			StringBuffer bufferSourceOfFund = new StringBuffer();
			String sof = "";
			if (sourceOfFunds != null) {
				for (String string : sourceOfFunds) {
					bufferSourceOfFund.append(string).append("|");
				}
				sof = bufferSourceOfFund.toString().substring(0, bufferSourceOfFund.toString().length() - 1);
			}
			customer.setSourceOfFund(sof);

			StringBuffer bufferInvObj = new StringBuffer();
			String invObj = "";
			if (investObjs != null) {
				for (String string : investObjs) {
					bufferInvObj.append(string).append("|");
				}
				invObj = bufferInvObj.toString().substring(0, bufferInvObj.toString().length() - 1);
			}
			customer.setPurposeOfInvestment(invObj);

			if (validation.hasErrors()) {
				Map<String, String> data = new HashMap<String, String>();
				try {
					if (customer.getUdf() != null)
						data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
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
				// udfs =
				// generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);
				List<GnUdfMaster> udfsIndi = new ArrayList<GnUdfMaster>();
				List<GnUdfMaster> udfsCorp = new ArrayList<GnUdfMaster>();
				log.debug("CUST TYPE ====== " + customer.getCustomerType().getLookupId());
				if (!customer.getCustomerType().getLookupId().equals("")) {
					//int sequence = 0;
					if (udfs != null) {
						for (GnUdfMaster udf : udfs) {

							udf.setValue(data.get(udf.getFieldName()));
							log.debug("UDF LOOKUP GROUP = " + udf.getLookupGroup());
							// if ( udf.getLookupGroup() != null ) {
							if (!udf.getLookupGroup().getLookupGroup().isEmpty()) {
								udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
								udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
							}

							// item.options =
							// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
							// item.getLookupGroup().getLookupGroup());

							if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
								udfsIndi.add(udf);
							}

							if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
								udfsCorp.add(udf);
							}
						}
						Collections.sort(udfsIndi, new UdfComparator());
						Collections.sort(udfsCorp, new UdfComparator());
					}
				} else {
					log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>");
					udfsCorp = generalService.listUdfMasterCustomerTypeCorporate();
					if (udfsCorp != null) {
						for (GnUdfMaster udf : udfsCorp) {
							// START UDF FOR DROPDOWN
							if (udf.getLookupGroup() != null) {
								udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
								udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
							}
							// END DROPDOWN
						}
					}
					// udfs = generalService.listUdfMasterCustomerTypeAll();
					// List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
					// logger.debug("udf = " +udfs.length);
					if (udfs != null) {
						for (GnUdfMaster udf : udfs) {
							log.debug("lookup group = " + udf.getLookupGroup().getLookupGroup());
							// START UDF FOR DROPDOWN
							if (!udf.getLookupGroup().getLookupGroup().isEmpty()) {
								udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
								udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
							}
							// END DROPDOWN
						}
					}
					udfsIndi = generalService.listUdfMasterCustomerTypeIndividual();
					// List<GnUdfMaster> udfsIndi = new
					// ArrayList<GnUdfMaster>();
					if (udfsIndi != null) {
						for (GnUdfMaster udf : udfsIndi) {
							// START UDF FOR DROPDOWN
							if (udf.getLookupGroup() != null) {
								udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
								udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
							}
							// END DROPDOWN
						}
					}
				}

				String sofData = customer.getSourceOfFund();
				String invData = customer.getPurposeOfInvestment();

				if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));
				}

				render("Customers/detail.html", customer, mode, udfs, udfsCorp, udfsIndi, contacts, detailContacts, detailCorpContacts, dataCustomerCharges, status, currentDate, param, sofData, invData, bankAccountInvoice);
			} else {
				log.debug("size of contacts = " + contacts);
				if (contacts != null) {
					for (CfContact contact : contacts) {
						if (contact != null) {
							log.debug("from save >>> isCustomer = " + contact.getIsCustomer());
							customer.getContacts().add(contact);
						}
					}
				}
				log.debug("size of corp contacts = " + corpContacts);
				if (corpContacts != null) {
					for (CfContact contact : corpContacts) {
						if (contact != null)
							customer.getContacts().add(contact);
					}
				}

				if (customerCharges != null) {
					for (CsCustomerCharge custCharge : customerCharges) {
						if (custCharge != null)
							customer.getCustomerCharges().add(custCharge);
					}
				}

				log.debug("size of customer contact = " + customer.getContacts().size());
				Long id = customer.getCustomerKey();
				log.debug("from save before serialize >>> contacts size = " + contacts.size());
				
				if (customer.getAmlCustomer() == null) {
					log.debug("amlCustomer after set null");
				} else {
					log.debug("amlCustomer after set: " + customer.getAmlCustomer());
				}
				log.debug("before confirming: " + customer);
				String customerSerialize = serializerService.serialize(session.getId(), id, customer);
				log.debug("after customerSerialize: " + customerSerialize);
				log.debug("after confirming: " + customer);

				confirming(id, mode, status, param, isAttachment);
			}

		} else {
			// flash.error("argument.null", customer);
			entry();
		}
	}

	@Check("maintenance.customer.save")
	public static void confirm(Long id, String mode, CfMaster customer, List<CsAccount> accounts, List<BnAccount> bankAccounts, List<RgInvestmentaccount> invs, List<CfContact> contacts, List<CfContact> corpContacts, List<CsCustomerCharge> customerCharges, GnUdfMaster[] udfs, String status, String[] hidSourceOfFunds, String[] hidInvestObjs) {
		log.debug("confirm. id: " + id + " mode: " + mode + " customer: " + customer + " accounts: " + accounts + " bankAccounts: " + bankAccounts + " invs: " + invs + " contacts: " + contacts + " corpContacts: " + corpContacts + " customerCharges: " + customerCharges + " udfs: " + udfs + " status: " + status + " hidSourceOfFunds: " + hidSourceOfFunds + " hidInvestObjs: " + hidInvestObjs);
		
		try {
			// START UDFS
			if (customer == null)
				back(null, mode, null, status, null);
			Map<String, String> data = new HashMap<String, String>();
			if (udfs != null) {
				log.debug("udfs size = " + udfs.length);
				for (GnUdfMaster udf : udfs) {
					data.put(udf.getFieldName(), udf.getValue());
					// START UDF FOR DROPDOWN
					log.debug("udf lookup group = " + udf.getLookupGroup().getLookupGroup());
					// if (udf.getLookupGroup() != null) {
					if (!udf.getLookupGroup().getLookupGroup().isEmpty()) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// END DROPDOWN
				}
				String udfString = json.writeValueAsString(data);
				customer.setUdf(udfString);
				// END UDFS
			}
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}

		if (contacts == null) {
			contacts = new ArrayList<CfContact>();
		}

		if (corpContacts == null) {
			corpContacts = new ArrayList<CfContact>();
		}
		if (customerCharges == null) {
			customerCharges = new ArrayList<CsCustomerCharge>();
		}

		String detailContacts = null;
		String detailCorpContacts = null;
		String dataCustomerCharges = null;
		String sofData = "";
		String invData = "";
		try {
			detailContacts = json.writeValueAsString(contacts);
			detailCorpContacts = json.writeValueAsString(corpContacts);
			dataCustomerCharges = json.writeValueAsString(customerCharges);
			log.debug("from confirm >>> detailContacts = " + detailContacts);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		try {
			if (contacts != null) {
				for (CfContact contact : contacts) {
					if (contact != null) {
						customer.getContacts().add(contact);
					}
				}
			}
			if (corpContacts != null) {
				for (CfContact contact : corpContacts) {
					if (contact != null) {
						contact.setAddressType(contact.getContactType());
						customer.getContacts().add(contact);
					}
				}
			}
			log.debug("size of corp contacts = " + corpContacts);

			if (customer != null) {
				log.debug("confirm: amlCustomer" + customer.getAmlCustomer());
				customer.putToSetBankAccounts();
				customer.putToSetCustodyAccounts();
				customer.putToSetInvestmentAccounts();
				Helper.showProperties(customer);

				setData(customer, accounts, bankAccounts, contacts, corpContacts, invs, customerCharges);

				// set first_name, middle_name, last_name to uppercase
				if (customer.getFirstName() != null)
					customer.setFirstName(customer.getFirstName().toUpperCase());

				if (customer.getMiddleName() != null)
					customer.setMiddleName(customer.getMiddleName().toUpperCase());

				if (customer.getLastName() != null)
					customer.setLastName(customer.getLastName().toUpperCase());

				String menuCode = null;
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
					menuCode = MenuConstants.CF_MASTER_ENTRY;
				} else {
					menuCode = MenuConstants.CF_MASTER_EDIT;
				}

				customer.setDomain(LookupConstants.DOMAIN_CUSTOMER);

				StringBuffer bufferSourceOfFund = new StringBuffer();
				if (hidSourceOfFunds != null) {
					for (String string : hidSourceOfFunds) {
						bufferSourceOfFund.append(string).append("|");
					}
				}
				sofData = bufferSourceOfFund.toString().substring(0, bufferSourceOfFund.toString().length() - 1);
				customer.setSourceOfFund(sofData);

				StringBuffer bufferInvObj = new StringBuffer();
				if (hidInvestObjs != null) {
					for (String string : hidInvestObjs) {
						bufferInvObj.append(string).append("|");
					}
				}
				invData = bufferInvObj.toString().substring(0, bufferInvObj.toString().length() - 1);
				customer.setPurposeOfInvestment(invData);
				StringBuffer error = new StringBuffer();
				Map<String, CfContact> currContact = new HashMap<String, CfContact>();
				
				if (UIConstants.DISPLAY_MODE_EDIT.equals(mode)) {
					for (CfContact _c : customer.getContacts()) {
						if(_c.getContactKey() != null){
							currContact.put(customer.getCustomerKey()+"-"+_c.getAddressType().getLookupId(), _c);
						}
						if (_c.getContactKey() == null) {
							CfContact contact = new CfContact();
							contact = customerService.getContactByCustomerAndType(customer.getCustomerKey(), _c.getAddressType().getLookupId());
							if (contact != null)
								_c.setContactKey(contact.getContactKey());
						} else {
							CfContact contact = customerService.getContactByCustomerAndType(customer.getCustomerKey(), _c.getAddressType().getLookupId());
							CfContact contactCache = currContact.get(customer.getCustomerKey()+"-"+_c.getAddressType().getLookupId());
							if(contact != null && contactCache != null){
								if(!contact.getContactKey().equals(contactCache.getContactKey())) {
									error.append(_c.getAddressType().getLookupDescription()+",");
								}
							}
						}
					}
				}
				
				if(error.length() > 0) throw new MedallionException("Contact Type "+error.toString().substring(0, error.length()-1)+" Already Exist ");
				
				customer.setBirthPlace(null);
				customer.setBirthArea(null);
				if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {
					customer.setBirthPlaceOther(customer.getBirthPlaceOtherDummy());
				}
				if(customer.getSwiftStatus() == null) customer.setSwiftStatus(null);
				if(customer.getSwiftStatus() != null && (customer.getSwiftStatus().getLookupId() == null || customer.getSwiftStatus().getLookupId().trim().equals(""))) customer.setSwiftStatus(null);

				customerService.saveCustomer(menuCode, customer, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));

				// clean up cache
				// Cache customerCache = getCache();
				// String keyCache =
				// String.valueOf(newCustomer.getCustomerKey());
				// if (customerCache.get(keyCache) !=null){
				// customerCache.remove(keyCache);
				// }

				if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
					// param = null;
					dedupe();
				} else {
					String param = UIConstants.DISPLAY_MODE_EDIT;
					list(mode, param);
				}
			} else {
				entry();
			}
		} catch (MedallionException e) {
			Map<String, String> data = new HashMap<String, String>();
			try {
				if (udfs != null) {
					data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
				}
			} catch (JsonParseException e1) {
				log.error(e1.getMessage(), e1);
			} catch (JsonMappingException e1) {
				log.error(e1.getMessage(), e1);
			} catch (IOException e1) {
				log.error(e1.getMessage(), e1);
			}
			// udfs =
			// generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);
			List<GnUdfMaster> udfsIndi = new ArrayList<GnUdfMaster>();
			List<GnUdfMaster> udfsCorp = new ArrayList<GnUdfMaster>();
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					if (udf != null) {
						udf.setValue(data.get(udf.getFieldName()));
						// if ( udf.getLookupGroup() != null ) {
						if (!udf.getLookupGroup().getLookupGroup().isEmpty()) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
						// item.options =
						// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
						// item.getLookupGroup().getLookupGroup());
					}
					if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
						udfsIndi.add(udf);
					}

					if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
						udfsCorp.add(udf);
					}
				}
			}

			validation.clear();
			String param = "";
			if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				param = UIConstants.DISPLAY_MODE_EDIT;
			}
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
			if (!param.equals("")) {
				if (param.equals(UIConstants.DISPLAY_MODE_EDIT)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));
				}
			}
			// flash.error("Duplicate Error! Code : ' "+customer.getCustomerNo()+" ' Already Exist",
			// customer.getCustomerNo());
			flash.error("Code : ' " + customer.getCustomerNo() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			if (customer.getCustomerKey() != null)
				id = customer.getCustomerKey();
			render("Customers/detail.html", customer, mode, confirming, detailContacts, detailCorpContacts, dataCustomerCharges, udfs, udfsCorp, udfsIndi, status, param, sofData, invData);
		}
	}

	@Check("maintenance.customer.save")
	public static void confirming(Long id, String mode, String status, String param, boolean isAttachment) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status + " param: " + param + " isAttachment: " + isAttachment);

		try {
			renderArgs.put("confirming", true);
			CfMaster customer = serializerService.deserialize(session.getId(), id, CfMaster.class);
			log.debug("CUSTOMER [confirming] = customer: " + customer);
			log.debug("CUSTOMER [confirming] = " + customer.getCustomerNo());
			log.debug("CUSTOMER [confirming] AmlCustomer= " + customer.getAmlCustomer());
			String nameFile = null;
			log.debug("attachment = " + customer.getAttachment());
			if (!Helper.isNullOrEmpty(customer.getAttachment())) {
				String[] data = customer.getAttachment().split("-");
				nameFile = data[1];
				customer.setFlagAttachFile(true);
			}
			List<CfContact> contacts = new ArrayList<CfContact>();
			List<CfContact> corpContacts = new ArrayList<CfContact>();
			for (CfContact cfContact : customer.getContacts()) {
				if (cfContact.getIsCustomer() == true)
					contacts.add(cfContact);

				if (cfContact.getIsCustomer() == false)
					corpContacts.add(cfContact);
			}
			log.debug("size of contact = " + contacts.size());
			// for (CfContact cfContact : contacts) {
			// logger.debug("addrress typee = "
			// +cfContact.getAddressType().getLookupCode());
			// }
			// logger.debug("size of contact corp = " +corpContacts.size());
			// for (CfContact cfContact : corpContacts) {
			// logger.debug("contact typee = "
			// +cfContact.getContactType().getLookupCode());
			// }
			log.debug("source of fund = " + customer.getSourceOfFund());
			log.debug("investment purpose = " + customer.getPurposeOfInvestment());
			String sofData = customer.getSourceOfFund();
			String invData = customer.getPurposeOfInvestment();

			String detailContacts = null;
			String detailCorpContacts = null;
			String dataCustomerCharges = null;
			try {

				detailContacts = json.writeValueAsString(contacts);
				detailCorpContacts = json.writeValueAsString(corpContacts);
				log.debug("detailContacts = " + detailContacts);
				log.debug("detailCorpContacts = " + detailCorpContacts);
				dataCustomerCharges = json.writeValueAsString(customer.getCustomerCharges());
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			// START UDFS
			Map<String, String> data = new HashMap<String, String>();
			if (customer.getUdf() != null) {
				data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
			}
			List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			// List<GnUdfMaster> udfsIndi =
			// generalService.listUdfMasterCustomerTypeIndividual();
			// List<GnUdfMaster> udfsCorp =
			// generalService.listUdfMasterCustomerTypeCorporate();
			if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
				udfs = generalService.listUdfMasterCustomerTypeIndividual();
			}
			if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
				udfs = generalService.listUdfMasterCustomerTypeCorporate();
			}
			List<GnUdfMaster> udfsIndi = new ArrayList<GnUdfMaster>();
			List<GnUdfMaster> udfsCorp = new ArrayList<GnUdfMaster>();
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					// START UDF FOR DROPDOWN
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// END DROPDOWN

					if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
						udfsIndi.add(udf);
					}

					if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
						udfsCorp.add(udf);
					}
				}
			}
			// END UDFS

			if (param.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
			}

			if (customer.isFlagAttachFile()) {
				nameFile = Helper.removeExtensionZip(nameFile);
			}

			render("Customers/detail.html", customer, mode, detailContacts, detailCorpContacts, dataCustomerCharges, udfs, udfsCorp, udfsIndi, status, param, isAttachment, sofData, invData, nameFile);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("maintenance.customer.save")
	public static void back(Long id, String mode, List<GnUdfMaster> udfs, String status, String param) {
		log.debug("back. id: " + id + " mode: " + mode + " udfs: " + udfs + " status: " + status + " param: " + param);

		try {
			CfMaster customer = serializerService.deserialize(session.getId(), id, CfMaster.class);
			String nameFile = null;
			log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			log.debug("customer get attachment = "+customer.getAttachment());
			if (!Helper.isNullOrEmpty(customer.getAttachment())) {
				String[] data = customer.getAttachment().split("-");
				nameFile = data[1];
				customer.setFlagAttachFile(true);
			}
			List<CfContact> contacts = new ArrayList<CfContact>();
			List<CfContact> corpContacts = new ArrayList<CfContact>();
			for (CfContact cfContact : customer.getContacts()) {
				if (cfContact.getIsCustomer() == true)
					contacts.add(cfContact);

				if (cfContact.getIsCustomer() == false)
					corpContacts.add(cfContact);
			}
			String sofData = customer.getSourceOfFund();
			String invData = customer.getPurposeOfInvestment();
			String currentDate = getApplicationDate();

			String detailContacts = null;
			String detailCorpContacts = null;
			String dataCustomerCharges = null;

			try {
				detailContacts = json.writeValueAsString(contacts);
				detailCorpContacts = json.writeValueAsString(corpContacts);
				log.debug("detailContacts = " + detailContacts);
				log.debug("detailCorpContacts = " + detailCorpContacts);
				dataCustomerCharges = json.writeValueAsString(customer.getCustomerCharges());
				log.debug("detailContact = " + detailContacts);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			List<GnUdfMaster> udfsIndi = new ArrayList<GnUdfMaster>();
			List<GnUdfMaster> udfsCorp = new ArrayList<GnUdfMaster>();
			if (customer.getUdf() != null) {
				// START UDFS
				Map<String, String> data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
				if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
					udfs = generalService.listUdfMasterCustomerTypeIndividual();
				}
				if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
					udfs = generalService.listUdfMasterCustomerTypeCorporate();
				}

				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						// START UDF FOR DROPDOWN
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
						// END DROPDOWN

						if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
							udfsIndi.add(udf);
						}

						if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
							udfsCorp.add(udf);
						}
					}
				}
				// END UDFS
			}

			// List<CfContact> contacts = customer.getContacts();
			// contacts = customer.getContacts();
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));
			}
			nameFile = Helper.removeExtensionZip(nameFile);
			render("Customers/detail.html", customer, mode, detailContacts, detailCorpContacts, dataCustomerCharges, udfs, udfsCorp, udfsIndi, status, currentDate, param, invData, sofData, nameFile);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CfMaster customer = json.readValue(maintenanceLog.getNewData(), CfMaster.class);

			String nameFile = null;
			if (!Helper.isNullOrEmpty(customer.getAttachment())) {
				String[] data = customer.getAttachment().split("-");
				nameFile = data[1];
				customer.setFlagAttachFile(true);
			}
			List<CsAccount> accounts = new ArrayList<CsAccount>();
			for (CsAccount account : customer.getAccounts()) {
				if (account != null)
					accounts.add(account);

			}
			customer.setAccounts(new HashSet<CsAccount>(accounts));
			BnAccount bankAccount = new BnAccount();
			if (customer.getBankAccountInvoice() != null) {
				bankAccount = bankAccountService.getBankAccount(customer.getBankAccountInvoice().getBankAccountKey());
				if (bankAccount != null) {
					customer.getBankAccountInvoice().setCurrency(bankAccount.getCurrency());
				}
			}
			List<RgInvestmentaccount> invs = taService.listInvestmentForCustomers(customer.getCustomerKey());
			for (RgInvestmentaccount inv : invs) {
				log.debug("from view >>> accountNumber = " + inv.getAccountNumber());
				BigDecimal balanceAmount = taService.getBalanceAmountForInvestmentAccountInquiry(inv.getAccountNumber());
				log.debug("balanceAmount = " + balanceAmount);
				inv.setBalanceAmount(balanceAmount);
			}

			if (customer.getCustomerGroup() != null) {
				customer.setCustRetailFlag(true);
			} else {
				customer.setCustRetailFlag(false);
			}

			if (!Helper.isNullOrEmpty(customer.getAttachment())) {
				customer.setFlagAttachFile(true);
			}

			// START UDFS
			List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			Map<String, String> data = new HashMap<String, String>();

			if (customer.getUdf() != null)
				data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));

			// List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			// List<GnUdfMaster> udfsIndi =
			// generalService.listUdfMasterCustomerTypeIndividual();
			// List<GnUdfMaster> udfsCorp =
			// generalService.listUdfMasterCustomerTypeCorporate();
			if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
				udfs = generalService.listUdfMasterCustomerTypeIndividual();
			}
			if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
				udfs = generalService.listUdfMasterCustomerTypeCorporate();
			}
			List<GnUdfMaster> udfsIndi = new ArrayList<GnUdfMaster>();
			List<GnUdfMaster> udfsCorp = new ArrayList<GnUdfMaster>();
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					// START UDF FOR DROPDOWN
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// END DROPDOWN

					if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_INDIVIDUAL)) {
						udfsIndi.add(udf);
					}

					if (customer.getCustomerType().getLookupId().equals(LookupConstants.CUSTOMER_TYPE_CORPORATE)) {
						udfsCorp.add(udf);
					}
				}
			}
			// END UDFS

			Set<CfContact> contacts = customer.getContacts();
			log.debug("customer key = " + customer.getCustomerKey());
			// List<CsCustomerCharge> customerCharges =
			// customerService.listCustomerChargesByCustomer(customer.getCustomerKey());
			Set<CsCustomerCharge> customerCharges = customer.getCustomerCharges();
			List<CfContact> contactAddTypes = new ArrayList<CfContact>();
			List<CfContact> contactTypes = new ArrayList<CfContact>();
			for (CfContact cfContact : customer.getContacts()) {
				if (cfContact.getIsCustomer() == true) {
					if(cfContact.getAddress1() == null) cfContact.setAddress1("");
					String[] address = cfContact.getAddress1().split("\n");
					if (address.length == 1) {
						cfContact.setAddressExt1(address[0].trim());
					}
					if (address.length == 2) {
						cfContact.setAddressExt1(address[0].trim());
						cfContact.setAddressExt2(address[1].trim());
					}
					if (address.length == 3) {
						cfContact.setAddressExt1(address[0].trim());
						cfContact.setAddressExt2(address[1].trim());
						cfContact.setAddressExt3(address[2].trim());
					}
					contactAddTypes.add(cfContact);
				}

				if (cfContact.getIsCustomer() == false) {
					cfContact.setContactType(cfContact.getAddressType());
					contactTypes.add(cfContact);
				}
			}
			String sofData = customer.getSourceOfFund();
			String invData = customer.getPurposeOfInvestment();

			if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {
				customer.setBirthPlaceOtherDummy(customer.getBirthPlaceOther());
			}

			String detailContacts = null;
			String detailCorpContacts = null;
			String dataCustomerCharges = null;
			try {
				detailContacts = json.writeValueAsString(contactAddTypes);
				detailCorpContacts = json.writeValueAsString(contactTypes);
				dataCustomerCharges = json.writeValueAsString(customerCharges);
				log.debug("detailContact = " + detailContacts);
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
			nameFile = Helper.removeExtensionZip(nameFile);
			
			log.debug("aproval. amlCustomer: " +  customer.getAmlCustomer());
			
			render("Customers/approval.html", customer, detailContacts, contacts, udfs, udfsCorp, udfsIndi, mode, taskId, operation, maintenanceLogKey, invs, from, detailCorpContacts, dataCustomerCharges, sofData, invData, nameFile);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			log.debug(">>> operation = " + operation);
			String data = customerService.approveCustomer(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
			String[] arr = data.split("#");
			String custNo = "**AUTO**";

			if (arr.length > 1) {
				if (!arr[1].equalsIgnoreCase(""))
					custNo = arr[1];
			}

			Map<String, Object> result = Formatter.resultSuccess();
			result.put("custNo", custNo);
			renderJSON(result);
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			customerService.approveCustomer(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
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

	public static void add(AccountSearchParameters param, Long keyId) {
		log.debug("add. param: " + param + " keyId: " + keyId);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CfMaster customer = customerService.getCustomer(keyId);
		customer.setCustomerKey(keyId);

		// List<AccountGridItem> accounts = null;
		// if (params.param.indexOf("-bank-") > 0) {
		// accounts = accountService.searchBankAccount(
		// UIHelper.withOperator(params.accountSearchNo,
		// params.accountNoOperator),
		// UIHelper.withOperator(params.accountSearchName,
		// params.accountNameOperator));
		// } else {
		// accounts = accountService.searchAccount(
		// UIHelper.withOperator(params.accountSearchNo,
		// params.accountNoOperator),
		// UIHelper.withOperator(params.accountSearchName,
		// params.accountNameOperator));
		// }
		// String param = params.param;
		render("Accounts/detail.html", mode);
	}

	public static void customerInquiryEnhancement(String accountNumber, Long id, String param) {
		log.debug("customerInquiryEnhancement. accountNumber: " + accountNumber + " id: " + id + " param: " + param);

		CfMaster customer = customerService.getCustomer(id);
		Date appDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		log.debug("currentDate = " + appDate);
		String currentDate = getApplicationDate();
		List<RgTrade> invsInquirys = taService.listInvestorTransactionInquiry(accountNumber, appDate, appDate);
		List<RgPortfolio> unitValuations = taService.listInvestorUnitValuation(accountNumber, appDate, appDate);
		List<RgInvestmentaccount> invs = taService.listInvestmentForCustomers(customer.getCustomerKey());

		RgInvestmentaccount inv = taService.loadInvestment(accountNumber);
		log.debug("size invsInquirys = " + invsInquirys.size());
		log.debug("size unitValuations = " + unitValuations.size());
		log.debug("size invs = " + invs.size());

		render("Customers/master_investor_inquiry.html", invsInquirys, unitValuations, customer, currentDate, invs, inv, param);
	}

	// public static void historyInvestorInquiry(String accountNumber, Long id,
	// Date dateFrom, Date dateTo) {
	// logger.debug("from [history investor inquiry] >>> dateFrom = " + dateFrom
	// + " dateTo = " + dateTo);
	// CfMaster customer = customerService.getCustomer(id);
	// Date appDate =
	// generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
	// String currentDate = getApplicationDate();
	// String from = "invt-inqy";
	// logger.debug("from [history investor inquiry] >>> from = " + from);
	// List<RgTrade> invsInquirys =
	// taService.listInvestorTransactionInquiryByFromTo(accountNumber, dateFrom,
	// dateTo);
	// if (invsInquirys != null){
	// logger.debug("from [history investor inquiry] >>> size of invsInquirys = "
	// + invsInquirys.size());
	// }
	// List<RgPortfolio> unitValuations =
	// taService.listInvestorUnitValuation(accountNumber, appDate, appDate);
	// List<RgInvestmentaccount> invs =
	// taService.listInvestmentForCustomers(customer.getCustomerKey());
	// RgInvestmentaccount inv = taService.loadInvestment(accountNumber);
	// render("Customers/master_investor_inquiry.html", invsInquirys,
	// unitValuations, customer, currentDate, dateFrom, dateTo, invs, inv,
	// from);
	// }

	// public static void historyInvestorUnit(String accountNumber, Long id,
	// Date dateFrom, Date dateTo) {
	// logger.debug("from [history investor unit] >>> dateFrom = " + dateFrom +
	// " dateTo = " + dateTo);
	// CfMaster customer = customerService.getCustomer(id);
	// Date appDate =
	// generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
	// String currentDate = getApplicationDate();
	// String from = "invt-unit";
	// List<RgTrade> invsInquirys =
	// taService.listInvestorTransactionInquiry(accountNumber, appDate,
	// appDate);
	// List<RgPortfolio> unitValuations =
	// taService.listInvestorUnitValuationByFromTo(accountNumber, dateFrom,
	// dateTo);
	// if (unitValuations != null){
	// logger.debug("from [history investor unit] >>> size of unitValuations = "
	// + unitValuations.size());
	// }
	// List<RgInvestmentaccount> invs =
	// taService.listInvestmentForCustomers(customer.getCustomerKey());
	// RgInvestmentaccount inv = taService.loadInvestment(accountNumber);
	// render("Customers/master_investor_inquiry.html", invsInquirys,
	// unitValuations, customer, currentDate, dateFrom, dateTo, invs, inv,
	// from);
	// }

	public static void printInvestorInquiry(String accountNumber, Date dateFrom, Date dateTo) {
		log.debug("printInvestorInquiry. accountNumber: " + accountNumber + " dateFrom: " + dateFrom + " dateTo: " + dateTo);

		String currentDate = getApplicationDate();
		RgInvestmentaccount inv = taService.loadInvestment(accountNumber);
		if ((dateFrom == null) && (dateTo == null)) {
			log.debug("from [print investor inqury] DATE FROM & DATE TO == NULL");
			dateFrom = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			dateTo = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			List<RgTrade> invsInquirys = taService.listInvestorTransactionInquiry(accountNumber, dateFrom, dateTo);
			render("Customers/investor_inquiry_template.html", invsInquirys, currentDate, inv);
		}
		List<RgTrade> invsInquirys = taService.listInvestorTransactionInquiry(accountNumber, dateFrom, dateTo);
		log.debug("from [print investor inqury] size invsInquirys = " + invsInquirys.size());
		render("Customers/investor_inquiry_template.html", invsInquirys, currentDate, inv, dateFrom, dateTo);
	}

	public static void printUnitValuation(String accountNumber, Date dateFrom, Date dateTo) {
		log.debug("printUnitValuation. accountNumber: " + accountNumber + " dateFrom: " + dateFrom + " dateTo: " + dateTo);

		String currentDate = getApplicationDate();
		RgInvestmentaccount inv = taService.loadInvestment(accountNumber);
		if ((dateFrom == null) && (dateTo == null)) {
			log.debug("from [prin investor inqury] DATE FROM & DATE TO == NULL");
			dateFrom = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			dateTo = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			List<RgPortfolio> unitValuations = taService.listInvestorUnitValuation(accountNumber, dateFrom, dateTo);
			render("Customers/investor_unit_valuation_template.html", unitValuations, currentDate, inv);
		}
		List<RgPortfolio> unitValuations = taService.listInvestorUnitValuation(accountNumber, dateFrom, dateTo);
		log.debug("size unitValuations = " + unitValuations.size());
		render("Customers/investor_unit_valuation_template.html", unitValuations, currentDate, inv, dateFrom, dateTo);
	}

	public static void historyInvestorInquiry(CustomerSearchParameters params) {
		log.debug("historyInvestorInquiry. params: " + params);

		Date dateFrom = params.searchDateFrom;
		Date dateTo = params.searchDateTo;
		List<RgTrade> invsInquirys = taService.listInvestorTransactionInquiryByFromTo(params.searchInvtAcct, dateFrom, dateTo);
		if (invsInquirys != null) {
			log.debug("from [history investor inquiry] >>> size of invsInquirys = " + invsInquirys.size());
		}
		RgInvestmentaccount inv = taService.loadInvestment(params.searchInvtAcct);
		render("Customers/grid_investor_inquiry.html", invsInquirys, dateFrom, dateTo, inv);
	}

	public static void historyInvestorUnit(CustomerSearchParameters params) {
		log.debug("historyInvestorUnit. params: " + params);

		Date dateFrom = params.searchDateFrom;
		Date dateTo = params.searchDateTo;
		List<RgPortfolio> unitValuations = taService.listInvestorUnitValuationByFromTo(params.searchInvtAcct, dateFrom, dateTo);
		if (unitValuations != null) {
			log.debug("from [history investor unit] >>> size of unitValuations = " + unitValuations.size());
		}
		RgInvestmentaccount inv = taService.loadInvestment(params.searchInvtAcct);
		render("Customers/grid_investor_unit.html", unitValuations, dateFrom, dateTo, inv);
	}

	public static String valueParam(String param) {
		log.debug("valueParam. param: " + param);

		GnSystemParameter sysParam = generalService.getSystemParameter(param);
		return sysParam.getValue();
	}

	public static void customerDownload(String downloadfile) {
		log.debug("customerDownload. downloadfile: " + downloadfile);

		CfMaster customer = customerService.getCustomerForUpload(new Long(downloadfile));
		String fromFolder = Play.configuration.getProperty("upload.customer");
		String customerFile = fromFolder + customer.getAttachment();

		File file = null;
		try {
			file = Helper.extractSingleZipFile(customerFile);
		} catch (Exception e) {
		}

		renderBinary(file, file.getName());
	}
	
	public static void isActiveAccountExist(String customerNo){
		
		boolean value = false;
		log.debug("isi customerNo "+customerNo);
		if (customerNo != null) {
			List<CsAccount> activeAccounts = accountService.getActiveAccountsByCustomer(customerNo);
			if (activeAccounts != null && activeAccounts.size() > 0 ) value = true;
		}
		log.debug("isi value "+value);
		renderText(value);
	}
	
	//s 7528
	public static void getAmlData(String amlKey, CfMaster customer) {
		
		log.debug("amlKey: " + amlKey + ", customer: " + customer );
		
		Map<String,Object> amlData = customerService.getAmlData(amlKey, customer);

		log.debug("getAmlData: " + amlData);
		
		renderJSON(amlData);	
	}
	//e 7528
	
	//s 7528
	public static void validateAmlDataBeforApprove(Long maintenanceLogKey) {
		log.debug("validateAmlDataBeforApprove. maintenanceLogKey: " + maintenanceLogKey );
		String resultCodeKey = "resultCode";
		String resultMessageKey = "resultMessage";
		
		Map<String,Object> validateAmlData = customerService.validateAmlDataBeforApprove(maintenanceLogKey);
		
		if(validateAmlData.containsKey(resultCodeKey)) {
			String resultCodeValue = (String) validateAmlData.get(resultCodeKey);
			if (resultCodeValue.equalsIgnoreCase("400")) {
				if (validateAmlData.containsKey(resultMessageKey)) {

					String resultMessageValue = (String) validateAmlData.get(resultMessageKey);
					
					if (!resultMessageValue.contains("KL2001.Customer Name")) {
						validateAmlData.put(resultMessageKey,"This changed will also update AML Maintenance. Are you sure to continue?");
						// validateAmlData.put(resultMessageKey, "Different customer name between CF2002-KL2002. This changed will also update AML Maintenance [KL2001.Customer Name]. Are your sure to continue?");
					}
				}
			}
		}

		log.debug("validateAmlData: " + validateAmlData);
		
		renderJSON(validateAmlData);
	}
	//e 7528
	
	//s 7528
	public static void checkAmlClose(String amlKey){
		log.debug("checkAmlClose amlKey "+amlKey);
		
		AmlCustomer amlCustomer = amlCustomerService.getAmlData(amlKey);
		
		boolean value = false;
		if (amlCustomer != null) {
			if (amlCustomer.getCloseReason() != null && amlCustomer.getCloseReason().getLookupId() != null) {
				value = true;
			}
		}
		log.debug("checkAmlClose value "+value);
		renderText(value);
	}
	//e 7528
}