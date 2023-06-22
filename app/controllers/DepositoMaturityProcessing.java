package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import play.mvc.Before;
import play.mvc.With;
import vo.AccountSearchParameters;
import vo.CustomerFormViewModel;
import vo.CustomerSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CfContact;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.CsAccount;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class DepositoMaturityProcessing extends MedallionController {
	private static Logger log = Logger.getLogger(DepositoMaturityProcessing.class);

	@Before(only = { "list", "dedupe" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
		List<SelectItem> bilyets = UIHelper.bilyetNoOperators();
		renderArgs.put("bilyets", bilyets);
	}

	@Check("maintenance.customer.list")
	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		String breadcrumb = "Inquiry";
		String module = null;
		if (param != null) {
			if (param.contains("cust-acct")) {
				module = "Account";
				breadcrumb = "Register";
			}
			if (param.contains("bank-acct")) {
				module = "Bank Account";
				breadcrumb = "Register";
			}
		}
		render(mode, param, module, breadcrumb);
	}

	@Check("maintenance.customer.list")
	public static void search(CustomerSearchParameters params, String group) {
		log.debug("search. params: " + params + " group: " + group);

		List<CfMaster> customers = customerService.searchCustomer(UIHelper.withOperator(params.customerSearchNo, params.customerNoOperator), UIHelper.withOperator(params.customerSearchName, params.customerNameOperator),
		// params.customerSearchBirthDate,
				UIHelper.withOperator(params.customerSearchIdentificationNo, params.identificationNoOperator), group);
		render("DepositoMaturityProcessing/grid.html", customers);
	}

	@Check("maintenance.customer.view")
	public static void view(Long id, String param) {
		log.debug("view. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CfMaster customer = customerService.getCustomer(id);
		List<CfContact> contacts = customerService.listContacts(customer.getCustomerKey());
		render("Customers/detail.html", customer, mode, param, contacts);
	}

	public static void viewBankAccount(String accountNo, String param) {
		log.debug("viewBankAccount. accountNo: " + accountNo + " param: " + param);

		if (param.equals("register-cust") || param.equals("register-cust-acct") || param.equals("-cust") || param.equals("register-cust-acct-cust") || param.equals("list-cust-acct")) {
			String mode = UIConstants.DISPLAY_MODE_VIEW + param;
			CsAccount account = accountService.getCustAccount(accountNo);
			render("Accounts/detail.html", account, mode, param);
		}

		if (param.equals("register-bank") || param.equals("register-bank-acct") || param.equals("-bank") || param.equals("register-bank-acct-bank") || param.equals("list-bank-acct")) {
			String mode = UIConstants.DISPLAY_MODE_VIEW + param;
			BnAccount bankAccount = bankAccountService.getBankAccountNo(accountNo);
			render("BankAccounts/detail.html", bankAccount, mode, param);
		}
	}

	@Check("maintenance.customer.register")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CfMaster customer = new CfMaster();
		customer.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		render("Customers/detail.html", mode, customer);
	}

	@Check("maintenance.customer.edit")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CfMaster customer = customerService.getCustomer(id);
		List<CfContact> contacts = customerService.listContacts(customer.getCustomerKey());
		// Set<Contact> contacts = customer.getContacts();
		render("Customers/detail.html", customer, mode, contacts);
	}

	private static void setData(CfMaster customer, List<CsAccount> accounts, List<BnAccount> bankAccounts, List<CfContact> contacts) {
		log.debug("setData. customer: " + customer + " accounts: " + accounts + " bankAccounts: " + bankAccounts + " contacts: " + contacts);

		if (customer != null) {
			if (accounts != null) {
				customer.setAccounts(new HashSet<CsAccount>(accounts));
			}

			if (bankAccounts != null) {
				customer.setBankAccounts(new HashSet<BnAccount>(bankAccounts));
			}

			if (contacts != null) {
				customer.setContacts(new HashSet<CfContact>(contacts));
			}
		}
	}

	// private static void listContact() {
	// List<Contact> contacts = customerService.listContacts();
	// //render("Customers/tab_contact.html", contacts);
	// render(contacts);
	// }

	@Check("maintenance.customer.save")
	public static void save(String mode, CfMaster customer, List<CsAccount> accounts, List<BnAccount> bankAccounts, List<CfContact> contacts) {
		log.debug("save. mode: " + mode + " customer: " + customer + " accounts: " + accounts + " bankAccounts: " + bankAccounts + " contacts: " + contacts);

		if (customer.getAccounts() != null)
			// VALIDATE HERE
			if (customer != null) {
				setData(customer, accounts, bankAccounts, contacts);
				validation.required("customer.customerType", customer.getCustomerType());
				validation.required("customer.customerNo", customer.getCustomerNo());
				validation.required("customer.lookupByInvestorType.lookupId", customer.getLookupByInvestorType().getLookupId());
				validation.required("customer.customerName", customer.getCustomerName());
				validation.required("customer.branch.branchKey", customer.getBranch().getBranchKey());
				validation.required("customer.npwp", customer.getNpwp());
				validation.required("customer.npwpDate", customer.getNpwpDate());
				// validation.required("customer.aksesNo",
				// customer.getAksesNo());

				if (LookupConstants.CUSTOMER_TYPE_CORPORATE.equals(customer.getCustomerType())) {
					if (customer.getBirthDate() != null) {
						validation.required("customer.birthPlace", customer.getBirthPlace());
					}
					validation.required("customer.legalDomicile.lookupId", customer.getLegalDomicile().getLookupId());
					validation.required("customer.assetYear1.lookupId", customer.getAssetYear1().getLookupId());
					validation.required("customer.assetYear2.lookupId", customer.getAssetYear2().getLookupId());
					validation.required("customer.assetYear3.lookupId", customer.getAssetYear3().getLookupId());
					validation.required("customer.profitYear1.lookupId", customer.getProfitYear1().getLookupId());
					validation.required("customer.profitYear2.lookupId", customer.getProfitYear2().getLookupId());
					validation.required("customer.profitYear3.lookupId", customer.getProfitYear3().getLookupId());
					// validation.required("customer.sourceOfFund.lookupId",
					// customer.getSourceOfFund().getLookupId());
					if (customer.getSkdDate() != null) {
						validation.required("customer.skdDate", customer.getSkdDate());
					}
				}

				if (LookupConstants.CUSTOMER_TYPE_INDIVIDUAL.equals(customer.getCustomerType())) {
					validation.required("customer.firstName", customer.getFirstName());
					validation.required("customer.middleName", customer.getMiddleName());
					validation.required("customer.lastName", customer.getLastName());
					// validation.required("customer.nationality.lookupId",
					// customer.getNationality().getLookupId());
					validation.required("customer.originCountry.lookupId", customer.getOriginCountry().getLookupId());
					validation.required("customer.birthPlace", customer.getBirthPlace());
					validation.required("customer.birthDate", customer.getBirthDate());
					validation.required("customer.nationality.lookupId", customer.getIdentificationType1().getLookupId());
					validation.required("customer.identification1Expiry", customer.getIdentification1Expiry());
					validation.required("customer.identification1RegDate", customer.getIdentification1RegDate());
					if (customer.getIdentification2Expiry() != null) {
						validation.required("customer.identification2Expiry", customer.getIdentification2Expiry());
					}
					if (customer.getIdentification2RegDate() != null) {
						validation.required("customer.identification2RegDate", customer.getIdentification2RegDate());
					}
					if (customer.getIdentification3Expiry() != null) {
						validation.required("customer.identification3Expiry", customer.getIdentification3Expiry());
					}
					if (customer.getIdentification3RegDate() != null) {
						validation.required("customer.identification3RegDate", customer.getIdentification3RegDate());
					}
					validation.required("customer.maritalStatus.lookupId", customer.getMaritalStatus().getLookupId());
					validation.required("customer.educationBackground.lookupId", customer.getEducationBackground().getLookupId());
					// validation.required("customer.sourceOfFund.lookupId",
					// customer.getSourceOfFund().getLookupId());
				}

				if (validation.hasErrors()) {
					render("Customers/detail.html", customer, mode);

				} else {
					Long id = customer.getCustomerKey();
					// String json =
					// serializerService.serialize(session.getId(),
					// customer.getCustomerKey(), customer);
					serializerService.serialize(session.getId(), id, customer);
					confirming(id, mode);
				}

			} else {
				flash.error("argument.null", customer);
			}
	}

	@Check("maintenance.customer.save")
	public static void confirm(Long id, String mode, CfMaster customer, List<CsAccount> accounts, List<BnAccount> bankAccounts, List<CfContact> contacts) {
		log.debug("confirm. id: " + id + " mode: " + mode + " customer: " + customer + " accounts: " + accounts + " bankAccounts: " + bankAccounts + " contacts: " + contacts);

		try {
			setData(customer, accounts, bankAccounts, contacts);
			// customerService.saveCustomer(customer, session.get("username"),
			// "");
			list(mode, null);
		} catch (MedallionException e) {
			log.error(e.getMessage(), e);
			flash.error("Duplicate Error! Code : ' " + customer.getCustomerNo() + " ' Already Exist", customer.getCustomerNo());
			boolean confirming = true;
			// setData(customer, accounts, bankAccounts, contacts);
			render("Customers/detail.html", customer, mode, confirming);
		}
	}

	@Check("maintenance.customer.save")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		CfMaster customer = serializerService.deserialize(session.getId(), id, CfMaster.class);
		Set<CfContact> contacts = customer.getContacts();
		render("Customers/detail.html", customer, mode, contacts);
	}

	@Check("maintenance.customer.save")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		CfMaster customer = serializerService.deserialize(session.getId(), id, CfMaster.class);
		Set<CfContact> contacts = customer.getContacts();
		render("Customers/detail.html", customer, mode, contacts);
	}

	public static void approval(String taskId, Long keyId) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId);

		CustomerFormViewModel model = getCustomerFormViewModel(UIConstants.DISPLAY_MODE_VIEW);
		CfMaster customer = customerService.getCustomer(keyId);
		List<CsAccount> accounts = accountService.listAccountByCustomerKey(customer.getCustomerKey());
		String param = "";
		render("Customers/approval.html", customer, model, param, accounts, taskId);
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		try {
			customerService.approveTransaction(keyId, taskId);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}

	}

	public static void reject(String taskId, Long keyId, String message) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId + " message: " + message);

		try {
			customerService.rejectTransaction(keyId, taskId, message);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}

	}

	public static void add(AccountSearchParameters param, Long keyId) {
		log.debug("add. param: " + param + " keyId: " + keyId);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CfMaster customer = customerService.getCustomer(keyId);
		customer.setCustomerKey(keyId);

		// TODO [FRS] need to check here
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

	private static CustomerFormViewModel getCustomerFormViewModel(String mode) {
		log.debug("getCustomerFormViewModel. mode: " + mode);

		CustomerFormViewModel vm = new CustomerFormViewModel();
		// vm.mode = mode;
		// vm.customerTypes =
		// generalService.listCustomerTypes(UIConstants.SIMIAN_BANK_ID);
		// vm.identificationTypes =
		// generalService.listIdentificationTypes(UIConstants.SIMIAN_BANK_ID);
		// vm.investorTypes = setInvestorTypes();
		// vm.mailingOptions = setMailingOptions();
		// vm.annualIncome = setAnnualIncome();
		// vm.maritalStatus = setMarriedStatus();
		return vm;
	}
}
