package controllers;

import java.io.IOException;
import java.math.BigDecimal;
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
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.beans.BeanUtils;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.PropertyUdfConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CfContact;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.CsAccount;
import com.simian.medallion.model.GnApplicationDate;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgInvestmentaccount;
import com.simian.medallion.model.RgPortfolio;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Router;
import play.mvc.With;
import vo.AccountSearchParameters;
import vo.CustomerSearchParameters;

@With(Secure.class)
public class Investors extends MedallionController {
	protected static Logger log = Logger.getLogger(Investors.class);

	private final static String FROMINQUIRY = "fromInquiry";

	@Before(only = { "list", UIConstants.PARAM_CUSTOMER_DEDUPE })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
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

		List<SelectItem> contactType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CONTACT_TYPE);
		renderArgs.put("contactType", contactType);

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

		List<SelectItem> supplementDoc = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._SUPPLEMENTARY_DOC);
		renderArgs.put("supplementDoc", supplementDoc);

		List<SelectItem> purposeOfInvCorp = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PURPOSE_OF_INV_CORP);
		renderArgs.put("purposeOfInvCorp", purposeOfInvCorp);

		List<SelectItem> purposeOfInvInd = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PURPOSE_OF_INV_IND);
		renderArgs.put("purposeOfInvInd", purposeOfInvInd);

		List<SelectItem> religion = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._RELIGION);
		renderArgs.put("religion", religion);

		List<GnLookup> sourceOfFundCorp = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._SRC_OF_FUND_CORP);
		renderArgs.put("sourceOfFundCorp", sourceOfFundCorp);

		List<GnLookup> investObjCorp = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._PURPOSE_OF_INV_CORP);
		renderArgs.put("investObjCorp", investObjCorp);

		List<GnLookup> investObjInd = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._PURPOSE_OF_INV_IND);
		renderArgs.put("investObjInd", investObjInd);

		List<GnLookup> sourceOfFundInd = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._SRC_OF_FUND_IND);
		renderArgs.put("sourceOfFundInd", sourceOfFundInd);

		List<SelectItem> typeOfBusiness = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TYPE_OF_BUSINESS);
		renderArgs.put("typeOfBusiness", typeOfBusiness);

		String otherEducation = generalService.getValueParam(SystemParamConstants.PARAM_EDUCATION_OTHER);
		renderArgs.put("otherEducation", otherEducation);

		String otherOccupation = generalService.getValueParam(SystemParamConstants.PARAM_OCCUPATION_OTHER);

		renderArgs.put("otherOccupation", otherOccupation);

		// String tniPolriOccupation =
		// generalService.getValueParam(SystemParamConstants.PARAM_OCCUPATION_TNI_POLRI);
		// renderArgs.put("tniPolriOccupation", tniPolriOccupation);
		String enterpreneuerOccupation = generalService.getValueParam(SystemParamConstants.PARAM_OCCUPATION_ENTERPRENEUER);
		renderArgs.put("enterpreneuerOccupation", enterpreneuerOccupation);

		String otherTypeBusiness = generalService.getValueParam(SystemParamConstants.PARAM_TYPE_BUSINESS_OTHER);
		renderArgs.put("otherTypeBusiness", otherTypeBusiness);

		String otherCompCharacter = generalService.getValueParam(SystemParamConstants.PARAM_COMPANY_CHARACTERISTIC_OTHER);
		renderArgs.put("otherCompCharacter", otherCompCharacter);

		String otherSourceOfFundInd = generalService.getValueParam(SystemParamConstants.PARAM_SOURCE_OF_FUND_IND_OTHER);
		renderArgs.put("otherSourceOfFundInd", otherSourceOfFundInd);

		String otherSourceOfFundCorp = generalService.getValueParam(SystemParamConstants.PARAM_SOURCE_OF_FUND_CORP_OTHER);
		renderArgs.put("otherSourceOfFundCorp", otherSourceOfFundCorp);

		String otherInvObjInd = generalService.getValueParam(SystemParamConstants.PARAM_INVESTMENT_OBJ_IND_OTHER);
		renderArgs.put("otherInvObjInd", otherInvObjInd);

		String otherInvObjCorp = generalService.getValueParam(SystemParamConstants.PARAM_INVESTMENT_OBJ_CORP_OTHER);
		renderArgs.put("otherInvObjCorp", otherInvObjCorp);

		String idKtp = generalService.getValueParam(SystemParamConstants.PARAM_ID_KTP);
		renderArgs.put("idKtp", idKtp);

		String idPassport = generalService.getValueParam(SystemParamConstants.PARAM_ID_PASSPORT);
		renderArgs.put("idPassport", idPassport);

		String maritalStatusMarried = generalService.getValueParam(SystemParamConstants.PARAM_MARITAL_STATUS_MARRIED);
		renderArgs.put("maritalStatusMarried", maritalStatusMarried);

		String typeIndi = generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_TYPE_INDIVIDUAL);
		renderArgs.put("typeIndi", typeIndi);

		String typeCorp = generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_TYPE_CORPORATE);
		renderArgs.put("typeCorp", typeCorp);

		String directCat = generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CATEGORY_DIRECT);
		renderArgs.put("directCat", directCat);

		String indirectCat = generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CATEGORY_INDIRECT);
		renderArgs.put("indirectCat", indirectCat);

		renderArgs.put("countryId", LookupConstants.COUNTRY_LOCAL);
		renderArgs.put("lookupCustType", PropertyUdfConstants.FIELD_CUSTOMER_TYPE);

	}

	@Check("maintenance.investor.register")
	public static void dedupe() {
		log.debug("dedupe. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String param = UIConstants.PARAM_CUSTOMER_DEDUPE;
		// String breadcrumb = "Register";
		boolean dedupe = true;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_ENTRY));
		render("Investors/list.html", mode, param, dedupe);
	}

	@Check("maintenance.investor.list")
	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		// param = "register-cust-acct";
		String breadcrumb = "Inquiry";
		CustomerSearchParameters params = new CustomerSearchParameters();
		String module = null;
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_ENTRY));
		} else if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_EDIT));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_VIEW));
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
		renderTemplate("Investors/list.html", mode, param, module, breadcrumb, params);
	}

	@Check("maintenance.investor.list")
	public static void search(CustomerSearchParameters params, String param) {
		log.debug("search. params: " + params + " param: " + param);

		List<CfMaster> customers = customerService.searchInvestor(UIHelper.withOperator(params.customerSearchNo, params.customerNoOperator), UIHelper.withOperator(params.customerSearchName, params.customerNameOperator), params.customerSearchBirthDate, UIHelper.withOperator(params.customerSearchIdentificationNo, params.identificationNoOperator), LookupConstants.DOMAIN_INVESTOR);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
		render("Investors/grid.html", customers, param);
	}

	@Check("maintenance.investor.list")
	public static void pagingList(Paging page, CustomerSearchParameters param) {
		log.debug("pagingList. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("p.domain", Paging.EQUAL, LookupConstants.DOMAIN_INVESTOR);
			page.addParams("p.customerNo", param.customerNoOperator, UIHelper.withOperator(param.customerSearchNo, param.customerNoOperator));
			page.addParams("p.customerName", param.customerNameOperator, UIHelper.withOperator(param.customerSearchName, param.customerNameOperator));
			page.addParams("p.externalNo", param.externalNo, UIHelper.withOperator(param.customerSearchExternalNo, param.externalNo));
			page.addParams(Helper.searchAll("(p.customerNo||p.customerName||to_char(p.birthDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||p.identification1No||p.recordStatus||p.isActive)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
			page = customerService.pagingInvestor(page);
		}
		renderJSON(page);
	}

	@Check("maintenance.investor.view")
	public static void view(Long id, String param) {
		log.debug("view. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CfMaster customer = customerService.getCustomer(id);
		boolean isActive = customer.getIsActive();
		String currentDate = getApplicationDate();
		if (customer.getRecordModifiedDate() == null) {
			customer.setRecordModifiedDate(customer.getRecordCreatedDate());
		}

		List<RgInvestmentaccount> invs = taService.listInvestmentForCustomers(customer.getCustomerKey());
		log.debug("size invs = " + invs.size());
		for (RgInvestmentaccount inv : invs) {
			log.debug("from view >>> accountNumber = " + inv.getAccountNumber());
			BigDecimal balanceAmount = taService.getBalanceAmountForInvestmentAccountInquiry(inv.getAccountNumber());
			log.debug("balanceAmount = " + balanceAmount);
			inv.setBalanceAmount(balanceAmount);
		}

		log.debug("CUSTOMER KEYYNYA " + customer.getCustomerKey());
		List<CfContact> contacts = customerService.listContacts(customer.getCustomerKey());
		List<BnAccount> bnAccounts = bankAccountService.listBankAccountByCustomer(customer.getCustomerKey());
		List<BnAccount> bankAccounts = new ArrayList<BnAccount>();
		for (BnAccount bnAccount : bnAccounts) {
			if (bnAccount.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_NEW)) {
				bankAccounts.add(bnAccount);
			} else {
				if (bnAccount.getIsActive() || (bnAccount.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_REJECTED))) {
					bankAccounts.add(bnAccount);
				}
			}
		}

		customer.putToListBankAccounts();
		customer.putToListCustodyAccounts();
		customer.putToListInvestmentAccounts();

		List<CfContact> gridContacts = new ArrayList<CfContact>();
		for (CfContact contact : contacts) {
			if (contact.getIdentificationType1() == null) {
				contact.setIdentificationType1(new GnLookup());
			}
			if (contact.getIdentificationType2() == null) {
				contact.setIdentificationType2(new GnLookup());
			}
			if (contact.getIdentificationType3() == null) {
				contact.setIdentificationType3(new GnLookup());
			}

			if (contact.getIdentificationType4() == null) {
				contact.setIdentificationType4(new GnLookup());
			}
			if (contact.getAddressType().getLookupId().trim().equals(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT1))) {
				if (contact.getAddress1() != null) {
					String[] address = contact.getAddress1().split("\n");
					if (address.length == 1) {
						contact.setAddressExt1(address[0].trim());
					}
					if (address.length == 2) {
						contact.setAddressExt1(address[0].trim());
						contact.setAddressExt2(address[1].trim());
					}
					if (address.length == 3) {
						contact.setAddressExt1(address[0].trim());
						contact.setAddressExt2(address[1].trim());
						contact.setAddressExt3(address[2].trim());
					}
				}
				customer.setCfContact(contact);
			} else if (contact.getAddressType().getLookupId().trim().equals(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT2))) {
				if (contact.getAddress1() != null) {
					String[] address = contact.getAddress1().split("\n");
					if (address.length == 1) {
						contact.setAddressExt1(address[0].trim());
					}
					if (address.length == 2) {
						contact.setAddressExt1(address[0].trim());
						contact.setAddressExt2(address[1].trim());
					}
					if (address.length == 3) {
						contact.setAddressExt1(address[0].trim());
						contact.setAddressExt2(address[1].trim());
						contact.setAddressExt3(address[2].trim());
					}
				}
				customer.setCfContact2(contact);
			} else {
				gridContacts.add(contact);
			}
		}

		String sofData = customer.getSourceOfFund();
		String invData = customer.getPurposeOfInvestment();

		String detailContacts = null;
		String detailBanks = null;
		try {
			detailContacts = json.writeValueAsString(gridContacts);
			detailBanks = json.writeValueAsString(bankAccounts);
			log.debug("detailContacts = " + detailContacts);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		try {

			List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			if (customer.getUdf() != null) {
				// START UDFS
				Map<String, String> data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
				udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					// START UDF FOR DROPDOWN
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// END DROPDOWN
				}
				// END UDFS
			}
			String fromInquiry = null;
			if (param != null) {
				if (param.equals(UIConstants.DISPLAY_MODE_EDIT)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_EDIT));
				} else if (param.equals("register-cust-acct")) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
				} else if (param.equals("register-bank-acct")) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
				} else if (param.equals("register-invt-acct")) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
				} else if (param.equals(UIConstants.DISPLAY_MODE_VIEW)) {
					fromInquiry = FROMINQUIRY;
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_VIEW));
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_ENTRY));
				}
			} else {
				fromInquiry = FROMINQUIRY;
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_VIEW));
			}

			for (RgInvestmentaccount rgi : customer.getRgInvestmentaccounts()) {
				RgPortfolio portfolio = taService.lastUnitPortfolio(rgi.getAccountNumber());
				if (portfolio != null) {
					rgi.setUnit(portfolio.getUnit());
				} else {
					rgi.setUnit(BigDecimal.ZERO);
				}
			}
			getIdentification(customer);
			render("Investors/detail.html", customer, mode, param, contacts, fromInquiry, detailContacts, detailBanks, udfs, isActive, invs, sofData, invData, currentDate);
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

	@Check("maintenance.investor.register")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		String param = "";
		CfMaster customer = new CfMaster();
		customer.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		Set<CfContact> contacts = customer.getContacts();
		String currentDate = getApplicationDate();

		GnApplicationDate current = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
		customer.setJointDate(current.getCurrentBusinessDate());
		customer.setSendToPost(true);

		String sofData = customer.getSourceOfFund();
		String invData = customer.getPurposeOfInvestment();

		String detailContacts = null;
		String detailBanks = null;
		try {
			detailContacts = json.writeValueAsString(customer.getContacts());
			detailBanks = json.writeValueAsString(customer.getListBankAccounts());
			log.debug("detailContacts = " + detailContacts);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);
		for (GnUdfMaster udf : udfs) {
			// START UDF FOR DROPDOWN
			if (udf.getLookupGroup() != null) {
				udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
				udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
			}
			// END DROPDOWN
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_ENTRY));
		render("Investors/detail.html", param, mode, customer, udfs, sofData, invData, detailContacts, detailBanks, contacts, currentDate);
	}

	@Check("maintenance.investor.edit")
	public static void edit(Long id, String param) {
		log.debug("edit. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CfMaster customer = customerService.getCustomer(id);
		if (customer.getRecordModifiedDate() == null) {
			customer.setRecordModifiedDate(customer.getRecordCreatedDate());
		}
		String currentDate = getApplicationDate();
		log.debug("id = " + id);

		log.debug("customer.customerKey = " + customer.getCustomerKey());
		log.debug("customer.contacts = " + customer.getContacts().size());
		List<CfContact> contacts = customerService.listContacts(customer.getCustomerKey());
		List<RgInvestmentaccount> invs = taService.listInvestmentForCustomers(customer.getCustomerKey());
		List<BnAccount> bnAccounts = bankAccountService.listBankAccountByCustomer(customer.getCustomerKey());
		// List<BnAccount> bnAccounts = new ArrayList<BnAccount>();
		List<BnAccount> bankAccounts = new ArrayList<BnAccount>();
		for (BnAccount bnAccount : bnAccounts) {
			if (bnAccount.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_NEW)) {
				bankAccounts.add(bnAccount);
			} else {
				if (bnAccount.getIsActive() || (bnAccount.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_REJECTED))) {
					bankAccounts.add(bnAccount);
				}
			}
		}

		customer.putToListBankAccounts();
		customer.putToListCustodyAccounts();
		customer.putToListInvestmentAccounts();

		List<CfContact> gridContacts = new ArrayList<CfContact>();
		for (CfContact contact : contacts) {
			if (contact.getIdentificationType1() == null) {
				contact.setIdentificationType1(new GnLookup());
			}
			if (contact.getIdentificationType2() == null) {
				contact.setIdentificationType2(new GnLookup());
			}
			if (contact.getIdentificationType3() == null) {
				contact.setIdentificationType3(new GnLookup());
			}
			if (contact.getIdentificationType4() == null) {
				contact.setIdentificationType4(new GnLookup());
			}
			if (contact.getAddressType().getLookupId().trim().equals(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT1))) {
				if (contact.getAddress1() != null) {
					String[] address = contact.getAddress1().split("\n");
					if (address.length == 1) {
						contact.setAddressExt1(address[0].trim());
					}
					if (address.length == 2) {
						contact.setAddressExt1(address[0].trim());
						contact.setAddressExt2(address[1].trim());
					}
					if (address.length == 3) {
						contact.setAddressExt1(address[0].trim());
						contact.setAddressExt2(address[1].trim());
						contact.setAddressExt3(address[2].trim());
					}
				}

				customer.setCfContact(contact);
			} else if (contact.getAddressType().getLookupId().trim().equals(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT2))) {
				if (contact.getAddress1() != null) {
					String[] address = contact.getAddress1().split("\n");
					if (address.length == 1) {
						contact.setAddressExt1(address[0].trim());
					}
					if (address.length == 2) {
						contact.setAddressExt1(address[0].trim());
						contact.setAddressExt2(address[1].trim());
					}
					if (address.length == 3) {
						contact.setAddressExt1(address[0].trim());
						contact.setAddressExt2(address[1].trim());
						contact.setAddressExt3(address[2].trim());
					}
				}
				customer.setCfContact2(contact);
			} else {
				gridContacts.add(contact);
			}
		}

		log.debug("from edit >>> contacts size = " + contacts.size());
		String status = customer.getRecordStatus();
		String sofData = customer.getSourceOfFund();
		String invData = customer.getPurposeOfInvestment();
		if (customer.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_NEW) || customer.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_UPDATED)) {
			mode = UIConstants.DISPLAY_MODE_VIEW;
		}
		log.debug("status = --" + status + "--");
		String detailContacts = null;
		String detailBanks = null;
		try {
			if (contacts == null) {
				contacts = new ArrayList<CfContact>();
			}

			if (bankAccounts == null) {
				bankAccounts = new ArrayList<BnAccount>();
			}

			detailContacts = json.writeValueAsString(gridContacts);
			detailBanks = json.writeValueAsString(bankAccounts);
			log.debug("detailBanks = " + detailBanks);
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
			if (customer.getUdf() != null) {
				Map<String, String> data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
				udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);
				log.debug("[EDIT] udfs size = " + udfs.size());
				for (GnUdfMaster udf : udfs) {
					if (customer.getUdf() != null) {
						udf.setValue(data.get(udf.getFieldName()));
					} else {
						udf.setValue("");
					}

					// START UDF FOR DROPDOWN
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// END DROPDOWN
				}
				// END UDFS
			}
			getIdentification(customer);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_EDIT));
			render("Investors/detail.html", customer, mode, detailContacts, detailBanks, contacts, udfs, status, sofData, invData, currentDate, param, invs);
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("maintenance.investor.save")
	public static void save(String mode, CfMaster customer, List<CsAccount> accounts, List<BnAccount> bankAccounts, List<RgInvestmentaccount> invs, List<CfContact> contacts, List<GnUdfMaster> udfs, String status, String param, String[] sourceOfFunds, String[] investObjs) {
		log.debug("save. mode: " + mode + " customer: " + customer + " accounts: " + accounts + " bankAccounts: " + bankAccounts + " invs: " + invs + " contacts: " + contacts + " udfs: " + udfs + " status: " + status + " param: " + param + " sourceOfFunds: " + sourceOfFunds + " investObjs: " + investObjs);

		if (customer != null) {
			// START UDFS
			String currentDate = getApplicationDate();
			GnApplicationDate current = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
			udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);
			try {
				Map<String, String> data = new HashMap<String, String>();
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						data.put((udf.getFieldName() != null ? udf.getFieldName() : ""), (udf.getValue() != null ? udf.getValue() : ""));
					}
					String udfString = json.writeValueAsString(data);
					try {
						customer.setUdf(udfString);
					} catch (Exception e) {
						String url = Router.getFullUrl("Application.index");
						redirect(url);
					}
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

			if (contacts != null) {
				for (CfContact contact : contacts) {
					if (contact != null) {
						customer.getContacts().add(contact);
					}
				}
			}

			if (bankAccounts != null) {
				for (BnAccount bankAccount : bankAccounts) {
					if (bankAccount != null) {
						customer.getListBankAccounts().add(bankAccount);
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

			String detailContacts = null;
			String detailBanks = null;
			try {

				if (bankAccounts == null) {
					bankAccounts = new ArrayList<BnAccount>();
				}
				if (contacts == null)
					contacts = new ArrayList<CfContact>();

				detailContacts = json.writeValueAsString(contacts);
				detailBanks = json.writeValueAsString(bankAccounts);
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
				validation.clear();
			}

			if (validation.errorsMap().values().containsAll(bankAccounts) == false)
				validation.clear();

			String idNationality = generalService.getValueParam(SystemParamConstants.PARAM_ID_NATIONALITY);

			validation.required("Investor Type is", customer.getCustomerType().getLookupId());
			validation.required("Joint Date is", customer.getJointDate());
			if (customer.getJointDate() != null) {
				if (customer.getJointDate().compareTo(current.getCurrentBusinessDate()) > 0) {
					validation.addError("", "customer.join_date_greater_application_date");
				}
			}

			if (mode.trim().equals(UIConstants.DISPLAY_MODE_EDIT)) {
				validation.required("Investor No is", customer.getCustomerNo());
			}

			validation.required("Investor Name is", customer.getCustomerName());
			validation.required("Address in Correspondence Address is", customer.getCfContact().getAddressExt1());
			// validation.required("Postal Code in Correspondence Address is",
			// customer.getCfContact().getAddress1ZipCode());

			if (bankAccounts.size() < 1) {
				validation.addError("", "customer.bank_account_less_one_data");
			}

			/*
			 * if((customer.getSendToPost()==null || !customer.getSendToPost())
			 * && (customer.getSendToEmail()==null ||
			 * !customer.getSendToEmail())){ validation.addError("",
			 * ExceptionConstants.STATEMENT_VIA_REQUIRED); }
			 */

			if (customer.getSendToEmail() != null) {
				if (customer.getSendToEmail()) {
					validation.required("Email is", customer.getCfContact().getEmail());
				}
			}

			if (customer.getCfContact() != null) {
				if (!customer.getCfContact().getEmail().trim().equals("") && customer.getCfContact().getEmail() != null) {
					/*
					 * if(!customer.getCfContact().getEmail().contains("@")){
					 * validation.addError("",
					 * ExceptionConstants.INVALID_EMAIL); }
					 */
					if (!Helper.emailValidation(customer.getCfContact().getEmail())) {
						validation.addError("", ExceptionConstants.INVALID_EMAIL);
					}
				}
			}

			if (customer.getNpwpDate() != null) {
				if (customer.getNpwpDate().compareTo(current.getCurrentBusinessDate()) > 0) {
					validation.addError("", "customer.npwp_date_greater_application_date");
				}
			}

			if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {

				// validation.required("Place of Comp. Estb. (State) in tab Institutional is",
				// customer.getBirthPlace());
				validation.required("Legal Domicile in tab Institutional is", customer.getOriginCountry().getLookupId());

				validation.required("Type Of Business in tab Institutional is", customer.getTypeOfBusiness().getLookupId());
				validation.required("Comp. Characteristic in tab Institutional is", customer.getCompanyCharacteristic().getLookupId());

				if (sourceOfFunds != null) {
					for (String string : sourceOfFunds) {
						if (string.equals(generalService.getValueParam(SystemParamConstants.PARAM_SOURCE_OF_FUND_CORP_OTHER))) {
							validation.required("Source of Fund (Others) in tab Additional is", customer.getSourceOfFundOthers());
						}
					}
				}

				if (investObjs != null) {
					for (String string : investObjs) {
						if (string.equals(generalService.getValueParam(SystemParamConstants.PARAM_INVESTMENT_OBJ_CORP_OTHER))) {
							validation.required("Investment Objectives (Others) in tab Additional is", customer.getPurposeOfInvestmentOthers());
						}
					}
				}

				if (customer.getOriginCountry().getLookupId().trim().equals(idNationality.trim())) {
					// validation.required("NPWP Number in tab ID Info is",
					// customer.getNpwp());
					// validation.required("Article of Asosiaciate in tab ID Info is",
					// customer.getArticleOfAssociation());
				}

				if (customer.getTypeOfBusiness().getLookupId().trim().equals(generalService.getValueParam(SystemParamConstants.PARAM_TYPE_BUSINESS_OTHER))) {
					validation.required("Type Of Business (Others) in tab Institutional is", customer.getTypeOfBusinessOthers());
				}

				if (customer.getCompanyCharacteristic().getLookupId().equals(generalService.getValueParam(SystemParamConstants.PARAM_COMPANY_CHARACTERISTIC_OTHER))) {
					validation.required("Comp. Characteristic (Others) in tab Institutional is", customer.getCompanyCharacteristicOthers());
				}

				if (!(customer.getIdentificationType1Tamp().getLookupId().equals(""))) {
					validation.required("Document 1 No in tab ID Info is", customer.getIdentification1NoTamp());
				}

				if (!(customer.getIdentificationType2Tamp().getLookupId().equals(""))) {
					validation.required("Document 2 No in tab ID Info is", customer.getIdentification2NoTamp());

				}

				if (!(customer.getIdentificationType3Tamp().getLookupId().equals(""))) {
					validation.required("Document 3 No in tab ID Info is", customer.getIdentification3NoTamp());

				}

				if (!(customer.getIdentificationType4Tamp().getLookupId().equals(""))) {
					validation.required("Document  4 No in tab ID Info is", customer.getIdentification4NoTamp());
				}

				if (customer.getIdentification1ExpiryTamp() != null) {
					if (customer.getIdentification1ExpiryTamp().compareTo(current.getCurrentBusinessDate()) < 0) {
						validation.addError("", "customer.expiryDoc1_date_less_application_date");
					}
				}
				if (customer.getIdentification2ExpiryTamp() != null) {
					if (customer.getIdentification2ExpiryTamp().compareTo(current.getCurrentBusinessDate()) < 0) {
						validation.addError("", "customer.expiryDoc2_date_less_application_date");
					}
				}
				if (customer.getIdentification3ExpiryTamp() != null) {
					if (customer.getIdentification3ExpiryTamp().compareTo(current.getCurrentBusinessDate()) < 0) {
						validation.addError("", "customer.expiryDoc3_date_less_application_date");
					}
				}
				if (customer.getIdentification4ExpiryTamp() != null) {
					if (customer.getIdentification4ExpiryTamp().compareTo(current.getCurrentBusinessDate()) < 0) {
						validation.addError("", "customer.expiryDoc4_date_less_application_date");
					}
				}
				if (customer.getSiupExDate() != null) {
					if (customer.getSiupExDate().compareTo(current.getCurrentBusinessDate()) < 0) {
						validation.addError("", "customer.siup_date_less_application_date");
					}
				}

				if (customer.getSkdDate() != null) {
					if (customer.getSkdDate().compareTo(current.getCurrentBusinessDate()) < 0) {
						validation.addError("", "customer.skd_date_less_application_date");
					}
				}
			}

			if (LookupConstants.CUSTOMER_TYPE_INDIVIDUAL.equals(customer.getCustomerType().getLookupId())) {

				validation.required("Nationality is", customer.getNationality().getLookupId());

				if (sourceOfFunds != null) {
					for (String string : sourceOfFunds) {
						if (string.equals(generalService.getValueParam(SystemParamConstants.PARAM_SOURCE_OF_FUND_IND_OTHER))) {
							validation.required("Source of Fund (Others) in tab Additional is", customer.getSourceOfFundOthers());
						}
					}
				}

				if (investObjs != null) {
					for (String string : investObjs) {
						if (string.equals(generalService.getValueParam(SystemParamConstants.PARAM_INVESTMENT_OBJ_IND_OTHER))) {
							validation.required("Investment Objectives (Others) in tab Additional is", customer.getPurposeOfInvestmentOthers());
						}
					}
				}

				if (customer.getNationality().getLookupId().trim().equals(idNationality.trim())) {
					// validation.required("NPWP Number in tab ID Info is",
					// customer.getNpwp());
					if (!customer.getIdentificationType1().getLookupId().trim().equals(generalService.getValueParam(SystemParamConstants.PARAM_ID_KTP))) {
						validation.addError("", ExceptionConstants.CUSTOMER_IDENTIFICATION1);
					}
				}

				validation.required("Identification 1 in tab ID Info is", customer.getIdentificationType1().getLookupId());

				if (!(customer.getIdentificationType1().getLookupId().equals(""))) {
					validation.required("Identification 1 Number in tab ID Info is", customer.getIdentification1No());
					// validation.required("Identification 1 Expiry Date in tab ID Info is",
					// customer.getIdentification1Expiry());
				}

				if (customer.getIdentification1Expiry() != null) {
					if (customer.getIdentification1Expiry().compareTo(current.getCurrentBusinessDate()) < 0) {
						validation.addError("", "customer.expiryID1_date_less_application_date");
					}
				}
				if (customer.getIdentification2Expiry() != null) {
					if (customer.getIdentification2Expiry().compareTo(current.getCurrentBusinessDate()) < 0) {
						validation.addError("", "customer.expiryID2_date_less_application_date");
					}
				}
				if (customer.getIdentification3Expiry() != null) {
					if (customer.getIdentification3Expiry().compareTo(current.getCurrentBusinessDate()) < 0) {
						validation.addError("", "customer.expiryID3_date_less_application_date");
					}
				}
				if (customer.getIdentification4Expiry() != null) {
					if (customer.getIdentification4Expiry().compareTo(current.getCurrentBusinessDate()) < 0) {
						validation.addError("", "customer.expiryID4_date_less_application_date");
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
					data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
				} catch (JsonParseException e) {
					log.debug("json.serialize");
					log.error(e.getMessage(), e);
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
					log.error(e.getMessage(), e);
				} catch (IOException e) {
					log.debug("json.serialize");
					log.error(e.getMessage(), e);
				}
				udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);
				for (GnUdfMaster udf : udfs) {
					if (udfs != null) {
						udf.setValue(data.get(udf.getFieldName()));
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
					}
				}

				String sofData = customer.getSourceOfFund();
				String invData = customer.getPurposeOfInvestment();

				if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_ENTRY));
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_EDIT));
				}

				render("Investors/detail.html", customer, mode, udfs, contacts, sofData, invData, detailContacts, detailBanks, status, currentDate, param);

			} else {

				customer.setBankAccounts(new HashSet<BnAccount>(bankAccounts));
				Long id = customer.getCustomerKey();

				serializerService.serialize(session.getId(), id, customer);
				serializerService.deserialize(session.getId(), id, CfMaster.class);

				confirming(id, mode, status, param);
			}

		} else {
			// flash.error("argument.null", customer);
			entry();
		}
	}

	@Check("maintenance.investor.save")
	public static void confirm(Long id, String mode, CfMaster customer, List<CsAccount> accounts, BnAccount[] bankAccounts, List<RgInvestmentaccount> invs, List<CfContact> contacts, List<GnUdfMaster> udfs, String status, String[] hidSourceOfFunds, String[] hidInvestObjs, String param) {
		log.debug("confirm. id: " + id + " mode: " + mode + " customer: " + customer + " accounts: " + accounts + " bankAccounts: " + bankAccounts + " invs: " + invs + " contacts: " + contacts + " udfs: " + udfs + " status: " + status + " hidSourceOfFunds: " + hidSourceOfFunds + " hidInvestObjs: " + hidInvestObjs + " param: " + param);

		if (customer == null)
			back(null, mode, udfs, status, param);

		udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);
		try {
			// START UDFS
			Map<String, String> data = new HashMap<String, String>();
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					data.put(udf.getFieldName(), udf.getValue());
				}
				String udfString = json.writeValueAsString(data);
				customer.setUdf(udfString);

				// END UDFS
			}
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}

		/*
		 * if(mode.trim().equals(UIConstants.DISPLAY_MODE_ENTRY)){ for
		 * (BnAccount bnAccount : bankAccounts) { bnAccount =
		 * bankAccountService.
		 * bankAccountForSettlementAccountPick(UIConstants.SIMIAN_BANK_ID,
		 * bnAccount.getAccountNo(),
		 * bnAccount.getBankCode().getThirdPartyCode(),
		 * LookupConstants.DOMAIN_INVESTOR); if(bnAccount != null){ throw new
		 * MedallionException(ExceptionConstants.DATA_DUPLICATE); } } }
		 */

		if (contacts == null) {
			contacts = new ArrayList<CfContact>();
		}
		/*
		 * if (bankAccounts == null) { bankAccounts = new
		 * ArrayList<BnAccount>(); }
		 */

		CfContact contact1 = new CfContact();
		CfContact contact2 = new CfContact();

		if (customer.getCfContact() != null) {
			if (customer.getCfContact().getEmail() != null) {
				customer.setEmail(customer.getCfContact().getEmail());
			}
			BeanUtils.copyProperties(customer.getCfContact(), contact1);
		}
		if (customer.getCfContact2() != null) {
			BeanUtils.copyProperties(customer.getCfContact2(), contact2);
		}

		String detailContacts = null;
		String detailBanks = null;
		String sofData = "";
		String invData = "";
		try {
			detailContacts = json.writeValueAsString(contacts);
			detailBanks = json.writeValueAsString(bankAccounts);
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

			if (customer.getCfContact() != null) {
				if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
					CfContact contact = new CfContact();
					contact = setContact(customer.getCfContact());
					if (customer.getCfContact().getContactKey() == null) {
						contact.setAddressType(generalService.getLookup(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT1)));
					}
					customer.getContacts().add(contact);
				} else {
					CfContact contact = new CfContact();
					contact = setContact(customer.getCfContact());
					if (customer.getCfContact().getContactKey() == null) {
						contact.setAddressType(generalService.getLookup(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT1)));
					}
					customer.getContacts().add(contact);
				}
			}

			if (Helper.isNullOrEmpty(customer.getCfContact2().getAddressExt1()) && Helper.isNullOrEmpty(customer.getCfContact2().getAddressExt2()) && Helper.isNullOrEmpty(customer.getCfContact2().getAddressExt3()) && Helper.isNullOrEmpty(customer.getCfContact2().getAddress1ZipCode()) && (customer.getCfContact2().getAddress1Country() == null ? true : Helper.isNullOrEmpty(customer.getCfContact2().getAddress1Country().getLookupId())) && (customer.getCfContact2().getAddress1State() == null ? true : Helper.isNullOrEmpty(customer.getCfContact2().getAddress1State().getLookupId())) && (customer.getCfContact2().getAddress1Area() == null ? true : Helper.isNullOrEmpty(customer.getCfContact2().getAddress1Area().getLookupId()))) {
				customer.setCfContact2(null);
			} else {
				if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
					CfContact contact = new CfContact();
					contact = setContact(customer.getCfContact2());
					if (customer.getCfContact2().getContactKey() == null) {
						contact.setAddressType(generalService.getLookup(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT2)));
					}
					customer.getContacts().add(contact);
				} else {
					CfContact contact = new CfContact();
					contact = setContact(customer.getCfContact2());
					if (customer.getCfContact2().getContactKey() == null) {
						contact.setAddressType(generalService.getLookup(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT2)));
					}
					customer.getContacts().add(contact);
				}
			}

			if (bankAccounts != null) {
				Set<BnAccount> tamBnAccountEntry = new HashSet<BnAccount>();
				for (BnAccount bankAccount : bankAccounts) {
					if (bankAccount != null) {
						customer.getListBankAccounts().add(bankAccount);
						customer.getListBankAccountTamps().add(bankAccount);
						tamBnAccountEntry.add(bankAccount);
					}
				}
				customer.setBankAccounts(tamBnAccountEntry);
			}

			Set<BnAccount> tamBnAccount = new HashSet<BnAccount>();
			if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				customer.getListBankAccounts().clear();
				customer.getListBankAccountTamps().clear();
				List<BnAccount> bnAccounts = bankAccountService.listBankAccountByCustomer(customer.getCustomerKey());

				boolean oldToInActive = true;
				for (BnAccount oldBnAccount : bnAccounts) {
					for (BnAccount newBnAccount : bankAccounts) {
						if (newBnAccount.getBankAccountKey() != null) {
							if (oldBnAccount.getBankAccountKey().toString().trim().equals(newBnAccount.getBankAccountKey().toString().trim())) {
								oldToInActive = false;
							}
						}
					}
					if (oldToInActive) {
						oldBnAccount.setIsActive(false);
						customer.getListBankAccounts().add(oldBnAccount);
						tamBnAccount.add(oldBnAccount);
					}
					oldToInActive = true;
				}
				if (bankAccounts != null) {
					for (BnAccount bankAccount : bankAccounts) {
						if (bankAccount != null) {
							bankAccount.setIsActive(customer.getIsActive());
							if (status.trim().equals(LookupConstants.__RECORD_STATUS_REJECTED)) {
								bankAccount.setIsActive(true);
							}
							customer.getListBankAccounts().add(bankAccount);
							customer.getListBankAccountTamps().add(bankAccount);
							tamBnAccount.add(bankAccount);
						}
					}
				}
				customer.setBankAccounts(tamBnAccount);
			}

			// customer.putToSetBankAccounts();
			// customer.putToSetCustodyAccounts();
			// customer.putToSetInvestmentAccounts();
			Helper.showProperties(customer);
			// setData(customer, accounts, bankAccounts, contacts, invs);

			String menuCode = null;
			if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
				menuCode = MenuConstants.CF_INVESTOR_ENTRY;
			} else {
				menuCode = MenuConstants.CF_INVESTOR_EDIT;
			}

			customer.setCorrespondenceAddress(generalService.getLookup(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT1)));
			customer.setDomain(LookupConstants.DOMAIN_INVESTOR);

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

			if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {
				customer.setIdentificationType1(null);
				customer.setIdentificationType2(null);
				customer.setIdentificationType3(null);
				customer.setIdentificationType4(null);
				customer.setIdentification1No(null);
				customer.setIdentification2No(null);
				customer.setIdentification3No(null);
				customer.setIdentification4No(null);
				customer.setIdentification1Expiry(null);
				customer.setIdentification2Expiry(null);
				customer.setIdentification3Expiry(null);
				customer.setIdentification4Expiry(null);
				if (customer.getIdentificationType1Tamp() != null) {
					if (customer.getIdentificationType1Tamp().getLookupId() != null) {
						customer.setIdentificationType1(new GnLookup());
						customer.getIdentificationType1().setLookupId(customer.getIdentificationType1Tamp().getLookupId());
					}
				}
				if (customer.getIdentification1NoTamp() != null) {
					customer.setIdentification1No(customer.getIdentification1NoTamp());
				}
				if (customer.getIdentification1ExpiryTamp() != null) {
					customer.setIdentification1Expiry(customer.getIdentification1ExpiryTamp());
				}
				if (customer.getIdentificationType2Tamp() != null) {
					if (customer.getIdentificationType2Tamp().getLookupId() != null) {
						customer.setIdentificationType2(new GnLookup());
						customer.getIdentificationType2().setLookupId(customer.getIdentificationType2Tamp().getLookupId());
					}
				}
				if (customer.getIdentification2NoTamp() != null) {
					customer.setIdentification2No(customer.getIdentification2NoTamp());
				}
				if (customer.getIdentification2ExpiryTamp() != null) {
					customer.setIdentification2Expiry(customer.getIdentification2ExpiryTamp());
				}
				if (customer.getIdentificationType3Tamp() != null) {
					if (customer.getIdentificationType3Tamp().getLookupId() != null) {
						customer.setIdentificationType3(new GnLookup());
						customer.getIdentificationType3().setLookupId(customer.getIdentificationType3Tamp().getLookupId());
					}
				}
				if (customer.getIdentification3NoTamp() != null) {
					customer.setIdentification3No(customer.getIdentification3NoTamp());
				}
				if (customer.getIdentification3ExpiryTamp() != null) {
					customer.setIdentification3Expiry(customer.getIdentification3ExpiryTamp());
				}
				if (customer.getIdentificationType4Tamp() != null) {
					if (customer.getIdentificationType4Tamp().getLookupId() != null) {
						customer.setIdentificationType4(new GnLookup());
						customer.getIdentificationType4().setLookupId(customer.getIdentificationType4Tamp().getLookupId());
					}
				}
				if (customer.getIdentification4NoTamp() != null) {
					customer.setIdentification4No(customer.getIdentification4NoTamp());
				}
				if (customer.getIdentification4ExpiryTamp() != null) {
					customer.setIdentification4Expiry(customer.getIdentification4ExpiryTamp());
				}
				customer.setBirthPlaceOther(customer.getBirthPlaceOtherDummy());
			}

			if (UIConstants.DISPLAY_MODE_EDIT.equals(mode)) {
				for (CfContact _c : customer.getContacts()) {
					if (_c.getContactKey() == null) {
						CfContact contact = new CfContact();
						contact = customerService.getContactByCustomerAndType(customer.getCustomerKey(), _c.getAddressType().getLookupId());
						if (contact != null)
							_c.setContactKey(contact.getContactKey());
					}
				}
			}

			customer = customerService.saveCustomer(menuCode, customer, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));

			if (validation.errorsMap().values().containsAll(contacts) == false)
				validation.clear();
			// if
			// (validation.errorsMap().values().containsAll(bankAccounts)==false)
			// validation.clear();
			if (validation.errorsMap().values().containsAll(customer.getBankAccounts()) == false)
				validation.clear();
			if (validation.errorsMap().values().containsAll(customer.getListBankAccounts()) == false)
				validation.clear();
			if (validation.errorsMap().values().containsAll(customer.getContacts()) == false)
				validation.clear();

			status = customer.getRecordStatus().trim();
			boolean confirming = true;
			customer.setCfContact(contact1);
			if (contact2 != null) {
				customer.setCfContact2(contact2);
			}
			// String param = null;
			render("Investors/detail.html", customer, mode, confirming, detailContacts, detailBanks, udfs, status, param);

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
			udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);
			for (GnUdfMaster udf : udfs) {
				if (udfs != null) {
					udf.setValue(data.get(udf.getFieldName()));
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
				}
			}
			validation.clear();
			// String param = null;
			if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				param = UIConstants.DISPLAY_MODE_EDIT;
			}
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_ENTRY));
			if (param != null) {
				if (param.equals(UIConstants.DISPLAY_MODE_EDIT)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_EDIT));
				}
			}

			customer.setCfContact(contact1);
			if (contact2 != null) {
				customer.setCfContact2(contact2);
			}

			flash.error("Investor Name : ' " + customer.getCustomerName() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			render("Investors/detail.html", customer, mode, confirming, detailContacts, detailBanks, udfs, status, param);
		}
	}

	@Check("maintenance.investor.save")
	public static void confirming(Long id, String mode, String status, String param) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status + " param: " + param);

		try {
			renderArgs.put("confirming", true);
			log.debug("ID >>>>" + id);
			CfMaster customer = serializerService.deserialize(session.getId(), id, CfMaster.class);
			log.debug("customer = " + customer.getContacts().size());

			String detailContacts = null;
			String detailBanks = null;
			String sofData = customer.getSourceOfFund();
			String invData = customer.getPurposeOfInvestment();

			try {
				detailContacts = json.writeValueAsString(customer.getContacts());
				detailBanks = json.writeValueAsString(customer.getListBankAccounts());
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			// START UDFS
			Map<String, String> data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
			List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);
			for (GnUdfMaster udf : udfs) {
				if (udfs != null) {
					udf.setValue(data.get(udf.getFieldName()));
					// START UDF FOR DROPDOWN
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));

					}
					// END DROPDOWN
				}
			}
			// END UDFS

			if (param.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_EDIT));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_ENTRY));
			}
			render("Investors/detail.html", customer, mode, sofData, invData, detailContacts, detailBanks, udfs, status, param);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("maintenance.investor.save")
	public static void back(Long id, String mode, List<GnUdfMaster> udfs, String status, String param) {
		log.debug("back. id: " + id + " mode: " + mode + " udfs: " + udfs + " status: " + status + " param: " + param);

		try {
			CfMaster customer = serializerService.deserialize(session.getId(), id, CfMaster.class);

			String sofData = customer.getSourceOfFund();
			String invData = customer.getPurposeOfInvestment();
			String currentDate = getApplicationDate();
			String detailContacts = null;
			String detailBanks = null;
			try {
				detailContacts = json.writeValueAsString(customer.getContacts());
				detailBanks = json.writeValueAsString(customer.getListBankAccounts());
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				param = "";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_ENTRY));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_EDIT));
			}
			render("Investors/detail.html", customer, mode, detailContacts, detailBanks, udfs, status, currentDate, param, sofData, invData);
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
			List<RgInvestmentaccount> invs = taService.listInvestmentForCustomers(customer.getCustomerKey());
			for (RgInvestmentaccount inv : invs) {
				log.debug("from view >>> accountNumber = " + inv.getAccountNumber());
				BigDecimal balanceAmount = taService.getBalanceAmountForInvestmentAccountInquiry(inv.getAccountNumber());
				log.debug("balanceAmount = " + balanceAmount);
				inv.setBalanceAmount(balanceAmount);
			}

			String group = customer.getDomain();
			List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			Map<String, String> data = new HashMap<String, String>();
			// START UDFS
			if (customer.getUdf() != null)
				data = json.readValue(customer.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));

			udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER);

			for (GnUdfMaster udf : udfs) {

				if (customer.getUdf() != null)
					udf.setValue(data.get(udf.getFieldName()));
				else
					udf.setValue("");

				if (udf.getLookupGroup() != null) {
					udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
					udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
				}

			}
			// END UDFS

			List<CfContact> gridContacts = new ArrayList<CfContact>();
			for (CfContact contact : customer.getContacts()) {
				if (contact.getIdentificationType1() == null) {
					contact.setIdentificationType1(new GnLookup());
				}
				if (contact.getIdentificationType2() == null) {
					contact.setIdentificationType2(new GnLookup());
				}
				if (contact.getIdentificationType3() == null) {
					contact.setIdentificationType3(new GnLookup());
				}
				if (contact.getIdentificationType4() == null) {
					contact.setIdentificationType4(new GnLookup());
				}
				if (contact.getAddress1() != null) {
					String[] address = contact.getAddress1().split("\n");
					if (address.length == 1) {
						contact.setAddressExt1(address[0].trim());
					}
					if (address.length == 2) {
						contact.setAddressExt1(address[0].trim());
						contact.setAddressExt2(address[1].trim());
					}
					if (address.length == 3) {
						contact.setAddressExt1(address[0].trim());
						contact.setAddressExt2(address[1].trim());
						contact.setAddressExt3(address[2].trim());
					}
				}
				if (contact.getAddressType().getLookupId().trim().equals(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT1))) {
					customer.setCfContact(contact);
				} else if (contact.getAddressType().getLookupId().trim().equals(generalService.getValueParam(SystemParamConstants.PARAM_CUSTOMER_CONTACT2))) {
					customer.setCfContact2(contact);
				} else {
					gridContacts.add(contact);
				}
			}

			List<BnAccount> gridbnAccounts = new ArrayList<BnAccount>();
			for (BnAccount bnAccount : customer.getListBankAccountTamps()) {
				if (bnAccount.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_NEW)) {
					gridbnAccounts.add(bnAccount);
				} else {
					if (bnAccount.getIsActive()) {
						gridbnAccounts.add(bnAccount);
					}
				}
			}

			String sofData = customer.getSourceOfFund();
			String invData = customer.getPurposeOfInvestment();

			Set<CfContact> contacts = customer.getContacts();
			String detailContacts = null;
			String detailBanks = null;
			try {
				detailContacts = json.writeValueAsString(gridContacts);
				detailBanks = json.writeValueAsString(gridbnAccounts);
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
			getIdentification(customer);
			render("Investors/approval.html", customer, detailContacts, detailBanks, sofData, invData, contacts, udfs, mode, taskId, operation, maintenanceLogKey, invs, from, group);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			customerService.approveInvestor(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			customerService.approveInvestor(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

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
		render("Investors/master_investor_inquiry.html", invsInquirys, unitValuations, customer, currentDate, invs, inv, param);
	}

	public static void printInvestorInquiry(String accountNumber, Date dateFrom, Date dateTo) {
		log.debug("printInvestorInquiry. accountNumber: " + accountNumber + " dateFrom: " + dateFrom + " dateTo: " + dateTo);

		String currentDate = getApplicationDate();
		RgInvestmentaccount inv = taService.loadInvestment(accountNumber);
		if ((dateFrom == null) && (dateTo == null)) {
			log.debug("from [print investor inqury] DATE FROM & DATE TO == NULL");
			dateFrom = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			dateTo = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			List<RgTrade> invsInquirys = taService.listInvestorTransactionInquiry(accountNumber, dateFrom, dateTo);
			render("Investors/investor_inquiry_template.html", invsInquirys, currentDate, inv);
		}
		List<RgTrade> invsInquirys = taService.listInvestorTransactionInquiry(accountNumber, dateFrom, dateTo);
		log.debug("from [print investor inqury] size invsInquirys = " + invsInquirys.size());
		render("Investors/investor_inquiry_template.html", invsInquirys, currentDate, inv, dateFrom, dateTo);
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
			render("Investors/investor_unit_valuation_template.html", unitValuations, currentDate, inv);
		}
		List<RgPortfolio> unitValuations = taService.listInvestorUnitValuation(accountNumber, dateFrom, dateTo);
		log.debug("size unitValuations = " + unitValuations.size());
		render("Investors/investor_unit_valuation_template.html", unitValuations, currentDate, inv, dateFrom, dateTo);
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
		render("Investors/grid_investor_inquiry.html", invsInquirys, dateFrom, dateTo, inv);
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
		render("Investors/grid_investor_unit.html", unitValuations, dateFrom, dateTo, inv);
	}

	private static CfContact setContact(CfContact contact) {
		log.debug("setContact. contact: " + contact);

		StringBuffer buffer = new StringBuffer();

		buffer.append(contact.getAddressExt1());
		if (!Helper.isNullOrEmpty(contact.getAddressExt2())) {
			buffer.append("\n" + contact.getAddressExt2());
		}
		if (!Helper.isNullOrEmpty(contact.getAddressExt3())) {
			buffer.append("\n" + contact.getAddressExt3());
		}
		contact.setAddress1(buffer.toString());

		if (!Helper.isNullOrEmpty(contact.getAddress1ZipCode())) {
			contact.setAddress1ZipCode(contact.getAddress1ZipCode());
		}

		if (contact.getAddress1Country() != null) {
			contact.setAddress1Country(generalService.getLookupReference(contact.getAddress1Country().getLookupId()));
		} else {
			contact.setAddress1Country(null);
		}

		if (!Helper.isNullOrEmpty(contact.getAddress1Phone1())) {
			contact.setAddress1Phone1(contact.getAddress1Phone1());
		}

		if (contact.getAddress1State() != null) {
			contact.setAddress1State(generalService.getLookupReference(contact.getAddress1State().getLookupId()));
		} else {
			contact.setAddress1State(null);
		}

		if (!Helper.isNullOrEmpty(contact.getAddress1Phone2())) {
			contact.setAddress1Phone2(contact.getAddress1Phone2());
		}

		if (contact.getAddress1Area() != null) {
			contact.setAddress1Area(generalService.getLookupReference(contact.getAddress1Area().getLookupId()));
		} else {
			contact.setAddress1Area(null);
		}

		if (contact.getIdentificationType1() != null) {
			contact.setIdentificationType1(generalService.getLookupReference(contact.getIdentificationType1().getLookupId()));
		} else {
			contact.setIdentificationType1(null);
		}

		if (contact.getIdentificationType2() != null) {
			contact.setIdentificationType2(generalService.getLookupReference(contact.getIdentificationType2().getLookupId()));
		} else {
			contact.setIdentificationType2(null);
		}

		if (contact.getIdentificationType3() != null) {
			contact.setIdentificationType3(generalService.getLookupReference(contact.getIdentificationType3().getLookupId()));
		} else {
			contact.setIdentificationType3(null);
		}

		if (contact.getIdentificationType4() != null) {
			contact.setIdentificationType4(generalService.getLookupReference(contact.getIdentificationType4().getLookupId()));
		} else {
			contact.setIdentificationType4(null);
		}

		if (!Helper.isNullOrEmpty(contact.getAddress1Phone3())) {
			contact.setAddress1Phone3(contact.getAddress1Phone3());
		}

		if (!Helper.isNullOrEmpty(contact.getEmail())) {
			contact.setEmail(contact.getEmail());
		}
		if (!Helper.isNullOrEmpty(contact.getIdentification1No())) {
			if (contact.getIdentification1No().trim().equals("null")) {
				contact.setIdentification1No(null);
			}
		}
		if (!Helper.isNullOrEmpty(contact.getIdentification2No())) {
			if (contact.getIdentification2No().trim().equals("null")) {
				contact.setIdentification2No(null);
			}
		}
		if (!Helper.isNullOrEmpty(contact.getIdentification2No())) {
			if (contact.getIdentification2No().trim().equals("null")) {
				contact.setIdentification2No(null);
			}
		}
		if (!Helper.isNullOrEmpty(contact.getIdentification3No())) {
			if (contact.getIdentification3No().trim().equals("null")) {
				contact.setIdentification3No(null);
			}
		}
		if (!Helper.isNullOrEmpty(contact.getIdentification4No())) {
			if (contact.getIdentification4No().trim().equals("null")) {
				contact.setIdentification4No(null);
			}
		}
		return contact;
	}

	private static void getIdentification(CfMaster customer) {
		//log.debug("getIdentification. customer: " + customer);

		if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType().getLookupId())) {
			if (customer.getIdentificationType1() != null) {
				customer.setIdentificationType1Tamp(new GnLookup());
				customer.getIdentificationType1Tamp().setLookupId(customer.getIdentificationType1().getLookupId());
			}
			if (customer.getIdentification1No() != null) {
				customer.setIdentification1NoTamp(customer.getIdentification1No());
			}
			if (customer.getIdentification1Expiry() != null) {
				customer.setIdentification1ExpiryTamp(customer.getIdentification1Expiry());
			}
			if (customer.getIdentificationType2() != null) {
				customer.setIdentificationType2Tamp(new GnLookup());
				customer.getIdentificationType2Tamp().setLookupId(customer.getIdentificationType2().getLookupId());
			}
			if (customer.getIdentification2No() != null) {
				customer.setIdentification2NoTamp(customer.getIdentification2No());
			}
			if (customer.getIdentification2Expiry() != null) {
				customer.setIdentification2ExpiryTamp(customer.getIdentification2Expiry());
			}
			if (customer.getIdentificationType3() != null) {
				customer.setIdentificationType3Tamp(new GnLookup());
				customer.getIdentificationType3Tamp().setLookupId(customer.getIdentificationType3().getLookupId());
			}
			if (customer.getIdentification3No() != null) {
				customer.setIdentification3NoTamp(customer.getIdentification3No());
			}
			if (customer.getIdentification3Expiry() != null) {
				customer.setIdentification3ExpiryTamp(customer.getIdentification3Expiry());
			}
			if (customer.getIdentificationType4() != null) {
				customer.setIdentificationType4Tamp(new GnLookup());
				customer.getIdentificationType4Tamp().setLookupId(customer.getIdentificationType4().getLookupId());
			}
			if (customer.getIdentification4No() != null) {
				customer.setIdentification4NoTamp(customer.getIdentification4No());
			}
			if (customer.getIdentification4Expiry() != null) {
				customer.setIdentification4ExpiryTamp(customer.getIdentification4Expiry());
			}
			customer.setIdentificationType1(null);
			customer.setIdentificationType2(null);
			customer.setIdentificationType3(null);
			customer.setIdentificationType4(null);
			customer.setIdentification1No(null);
			customer.setIdentification2No(null);
			customer.setIdentification3No(null);
			customer.setIdentification4No(null);
			customer.setIdentification1Expiry(null);
			customer.setIdentification2Expiry(null);
			customer.setIdentification3Expiry(null);
			customer.setIdentification4Expiry(null);

			customer.setBirthPlaceOtherDummy(customer.getBirthPlaceOther());
			customer.setBirthPlaceOther(null);
		}
	}
}
