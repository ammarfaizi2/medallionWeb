package controllers;

import helpers.UIConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnCustodianAccount;
import com.simian.medallion.model.GnCustodianBank;
import com.simian.medallion.model.GnCustodianCoa;
import com.simian.medallion.model.GnCustodianComplience;
import com.simian.medallion.model.GnCustodianInfo;
import com.simian.medallion.model.GnCustodianInvoice;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class CustodianBankSetups extends MedallionController {
	private static Logger log = Logger.getLogger(CustodianBankSetups.class);

	@Before(only = { "edit", "entry", "save", "confirming", "confirm", "back" })
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("cash", "CASH_ACCOUNT");
		renderArgs.put("gl", "GL_ACCOUNT");

		List<SelectItem> invoiceCharge = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._INVOICE_CHARGE);
		renderArgs.put("invoiceCharge", invoiceCharge);
	}

	public static void list() {
		log.debug("list. ");
	}

	@Check("administration.custodianbanksetup")
	public static void edit() {
		log.debug("edit. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnCustodianInfo cust = new GnCustodianInfo();
		
		List<SelectItem> accountUsedByOption = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._USED_BY);
		renderArgs.put("accountUsedByOption", filterSelectItem(accountUsedByOption));
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CUSTODIAN_BANK_SETUP));
		render("CustodianBankSetups/detail.html", cust, mode);
	}

	@Check("administration.custodianbanksetup")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnCustodianInfo cust = generalService.getCustodianBankSetup();
		if(cust == null) cust = new GnCustodianInfo();
		String[] address = cust.getAddress().split("\n");
		if (address.length == 1) {
			cust.setAddress1(address[0].trim());
		}
		if (address.length == 2) {
			cust.setAddress1(address[0].trim());
			cust.setAddress2(address[1].trim());
		}

		if (address.length == 3) {
			cust.setAddress1(address[0].trim());
			cust.setAddress2(address[1].trim());
			cust.setAddress3(address[2].trim());
		}

		for (GnCustodianAccount account : cust.getCustodianAccounts()) {
			if (account.getAccountCategory() != null && LookupConstants.DEPOSITORY_CODE_INVOICE.equals(account.getAccountCategory().getLookupId())) {
				cust.setInvoiceAccount(account);
				cust.getInvoiceAccount().setCustodianInfo(null);

				if (account.getCustodianBanks().isEmpty()) {
					cust.getInvoiceAccount().setInvoiceCustodianBank(new GnCustodianBank());
				} else {
					List<GnCustodianBank> list = new ArrayList<GnCustodianBank>();
					list.addAll(account.getCustodianBanks());
					cust.getInvoiceAccount().setInvoiceCustodianBank(list.get(0));
				}
			}

			if (account.getAccountCategory() != null && LookupConstants.DEPOSITORY_CODE_KSEI_CBEST.equals(account.getAccountCategory().getLookupId())) {
				log.debug("Account = " + account);
				cust.setKseiAccount(account);
				cust.getKseiAccount().setCustodianInfo(null);
				cust.getKseiAccount().setBankDebetKsei(new ArrayList<GnCustodianBank>());
				cust.getKseiAccount().setBankCreditKsei(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank bank : account.getCustodianBanks()) {
					if (bank.getAccountDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_BANK_DEBET)) {
						bank.setCustodianAccount(null);
						cust.getKseiAccount().getBankDebetKsei().add(bank);
					}
					if (bank.getAccountDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_BANK_CREDIT)) {
						bank.setCustodianAccount(null);
						cust.getKseiAccount().getBankCreditKsei().add(bank);
					}
				}

			}
			if (account.getAccountCategory() != null && LookupConstants.DEPOSITORY_CODE_BI_BISSSS.equals(account.getAccountCategory().getLookupId())) {
				cust.setBiAccount(account);
				cust.getBiAccount().setCustodianInfo(null);
				cust.getBiAccount().setBankDebetBi(new ArrayList<GnCustodianBank>());
				cust.getBiAccount().setBankCreditBi(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank bank : account.getCustodianBanks()) {
					if (bank.getAccountDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_BANK_DEBET)) {
						bank.setCustodianAccount(null);
						cust.getBiAccount().getBankDebetBi().add(bank);
					}
					if (bank.getAccountDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_BANK_CREDIT)) {
						bank.setCustodianAccount(null);
						cust.getBiAccount().getBankCreditBi().add(bank);
					}
				}

			}

			if (account.getAccountCategory() != null && LookupConstants.DEPOSITORY_CODE_EUROCLEARS.equals(account.getAccountCategory().getLookupId())) {
				log.debug("Account = " + account);

				cust.setEuroAccount(account);
				cust.getEuroAccount().setCustodianInfo(null);
				cust.getEuroAccount().setBankDebetEuro(new ArrayList<GnCustodianBank>());
				cust.getEuroAccount().setBankCreditEuro(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank bank : account.getCustodianBanks()) {
					if (bank.getAccountDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_BANK_DEBET)) {
						bank.setCustodianAccount(null);
						cust.getEuroAccount().getBankDebetEuro().add(bank);
					}
					if (bank.getAccountDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_BANK_CREDIT)) {
						bank.setCustodianAccount(null);
						cust.getEuroAccount().getBankCreditEuro().add(bank);
					}
				}
			}
			
			if (account.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_POOLACCOUNT)){
				cust.setPoolAccount(account);
				cust.getPoolAccount().setCustodianInfo(null);
				cust.getPoolAccount().setBankDebetPool(new ArrayList<GnCustodianBank>());
				cust.getPoolAccount().setBankCreditPool(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank bank : account.getCustodianBanks()) {
					if (bank.getAccountDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_BANK_DEBET)){
						bank.setCustodianAccount(null);
						cust.getPoolAccount().getBankDebetPool().add(bank);
					}
					if (bank.getAccountDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_BANK_CREDIT)){
						bank.setCustodianAccount(null);
						cust.getPoolAccount().getBankCreditPool().add(bank);
					}
				}				
			}
		}

		List<GnCustodianCoa> kseiCoas = generalService.listCustodianCoas(cust.getKseiAccount().getCustodianAccountKey());
		for (GnCustodianCoa gnCustodianCoa : kseiCoas) {
			if (gnCustodianCoa.getId().getDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_COA_DEBET))
				cust.getKseiAccount().setCoaDebetKsei(gnCustodianCoa);
			if (gnCustodianCoa.getId().getDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_COA_CREDIT))
				cust.getKseiAccount().setCoaCreditKsei(gnCustodianCoa);
		}

		List<GnCustodianCoa> biCoas = generalService.listCustodianCoas(cust.getBiAccount().getCustodianAccountKey());
		for (GnCustodianCoa gnCustodianCoa : biCoas) {
			if (gnCustodianCoa.getId().getDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_COA_DEBET))
				cust.getBiAccount().setCoaDebetBi(gnCustodianCoa);
			if (gnCustodianCoa.getId().getDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_COA_CREDIT))
				cust.getBiAccount().setCoaCreditBi(gnCustodianCoa);
		}

		if (cust.getEuroAccount() != null) {
			List<GnCustodianCoa> euroCoas = generalService.listCustodianCoas(cust.getEuroAccount().getCustodianAccountKey());
			for (GnCustodianCoa gnCustodianCoa : euroCoas) {
				if (gnCustodianCoa.getId().getDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_COA_DEBET))
					cust.getEuroAccount().setCoaDebetEuro(gnCustodianCoa);
				if (gnCustodianCoa.getId().getDomain().equals(LookupConstants.DOMAIN_CUSTODIAN_COA_CREDIT))
					cust.getEuroAccount().setCoaCreditEuro(gnCustodianCoa);
			}
		} else {
			GnCustodianAccount account = new GnCustodianAccount();
			cust.setEuroAccount(account);
		}

		if (cust.getCustodianInvoices() != null) {
			for (GnCustodianInvoice invoice : cust.getCustodianInvoices()) {
				invoice.setCustodianInfo(null);
			}
		}
		
		if (cust.getCustodianComplience()!=null){
			for (GnCustodianComplience complience : cust.getCustodianComplience()) {
				complience.setCustodianInfo(null);
			}
		}
		
		String dataBankDk = null;
		String dataBankCk = null;
		String dataBankDb = null;
		String dataBankCb = null;
		String dataBankDe = null;
		String dataBankCe = null;
		String dataBankDp = null;
		String dataBankCp = null;
		String dataInvoice = null;
		String dataComplience = null;
		try {
			if (cust.getKseiAccount().getBankDebetKsei() == null)
				cust.getKseiAccount().setBankDebetKsei(new ArrayList<GnCustodianBank>());
			if (cust.getKseiAccount().getBankCreditKsei() == null)
				cust.getKseiAccount().setBankCreditKsei(new ArrayList<GnCustodianBank>());
			if (cust.getBiAccount().getBankDebetBi() == null)
				cust.getBiAccount().setBankDebetBi(new ArrayList<GnCustodianBank>());
			if (cust.getBiAccount().getBankCreditBi() == null)
				cust.getBiAccount().setBankCreditBi(new ArrayList<GnCustodianBank>());
			if (cust.getCustodianInvoices() == null)
				cust.setCustodianInvoices(new HashSet<GnCustodianInvoice>());
			if (cust.getCustodianComplience() == null) cust.setCustodianComplience(new HashSet<GnCustodianComplience>());
			if (cust.getEuroAccount() != null) {
				if (cust.getEuroAccount().getBankDebetEuro() == null)
					cust.getEuroAccount().setBankDebetEuro(new ArrayList<GnCustodianBank>());
				if (cust.getEuroAccount().getBankCreditEuro() == null)
					cust.getEuroAccount().setBankCreditEuro(new ArrayList<GnCustodianBank>());
				;
			}
			if (cust.getPoolAccount().getBankDebetPool() == null) cust.getPoolAccount().setBankDebetPool(new ArrayList<GnCustodianBank>());
			if (cust.getPoolAccount().getBankCreditPool() == null) cust.getPoolAccount().setBankCreditPool(new ArrayList<GnCustodianBank>());

			dataBankDk = json.writeValueAsString(cust.getKseiAccount().getBankDebetKsei());
			dataBankCk = json.writeValueAsString(cust.getKseiAccount().getBankCreditKsei());
			dataBankDb = json.writeValueAsString(cust.getBiAccount().getBankDebetBi());
			dataBankCb = json.writeValueAsString(cust.getBiAccount().getBankCreditBi());
			dataBankDe = json.writeValueAsString(cust.getEuroAccount().getBankDebetEuro());
			dataBankCe = json.writeValueAsString(cust.getEuroAccount().getBankCreditEuro());
			dataBankDp = json.writeValueAsString(cust.getPoolAccount().getBankDebetPool());
			dataBankCp = json.writeValueAsString(cust.getPoolAccount().getBankCreditPool());
			dataInvoice = json.writeValueAsString(cust.getCustodianInvoices());
			dataComplience = json.writeValueAsString(cust.getCustodianComplience());
		} catch (JsonGenerationException e) {
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		List<SelectItem> accountUsedByOption = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._USED_BY);
		renderArgs.put("accountUsedByOption", filterSelectItem(accountUsedByOption));
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CUSTODIAN_BANK_SETUP));
		render("CustodianBankSetups/detail.html", cust, mode, dataBankDk, dataBankCk, dataBankDb, dataBankCb, dataBankDe, dataBankCe, dataBankDp, dataBankCp, dataInvoice, dataComplience);
	}

	public static void view() {
		log.debug("view. ");
	}

	@Check("administration.custodianbanksetup")
	public static void save(GnCustodianInfo cust, GnCustodianBank[] bankAccountsDk, GnCustodianBank[] bankAccountsCk, GnCustodianBank[] bankAccountsDb, GnCustodianBank[] bankAccountsCb, GnCustodianBank[] bankAccountsDe, GnCustodianBank[] bankAccountsCe, GnCustodianBank[] bankAccountsDp, GnCustodianBank[] bankAccountsCp, GnCustodianInvoice[] invoices, GnCustodianComplience[] complience, String mode) {
		log.debug("save. cust: " + cust + " bankAccountsDk: " + bankAccountsDk + " bankAccountsCk: " + bankAccountsCk + " bankAccountsDb: " + bankAccountsDb + " bankAccountsCb: " + bankAccountsCb + " bankAccountsDe: " + bankAccountsDe + " bankAccountsCe: " + bankAccountsCe + " invoices: " + invoices + " mode: " + mode);

		if (cust != null) {
			if (bankAccountsDk != null) {
				cust.getKseiAccount().setBankDebetKsei(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsDk) {
					// honestly, gw ga suka cara coding begini... tapi masih
					// belum tahu kenapa bisa membentuk string 'null' tiap kali
					// nge-save kalau nilainya kosong
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getKseiAccount().getBankDebetKsei().add(gnCustodianBank);
				}
			}

			if (bankAccountsCk != null) {
				cust.getKseiAccount().setBankCreditKsei(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsCk) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getKseiAccount().getBankCreditKsei().add(gnCustodianBank);
				}
			}

			if (bankAccountsDb != null) {
				cust.getBiAccount().setBankDebetBi(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsDb) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getBiAccount().getBankDebetBi().add(gnCustodianBank);
				}
			}
			if (bankAccountsCb != null) {
				cust.getBiAccount().setBankCreditBi(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsCb) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getBiAccount().getBankCreditBi().add(gnCustodianBank);
				}
			}
			if (bankAccountsDe != null) {
				cust.getEuroAccount().setBankDebetEuro(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsDe) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getEuroAccount().getBankDebetEuro().add(gnCustodianBank);
				}
			}

			if (bankAccountsCe != null) {
				cust.getEuroAccount().setBankCreditEuro(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsCe) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getEuroAccount().getBankCreditEuro().add(gnCustodianBank);
				}
			}
			
			if (bankAccountsDp!=null){
				cust.getPoolAccount().setBankDebetPool(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsDp) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getPoolAccount().getBankDebetPool().add(gnCustodianBank);
				}
			}
			
			if (bankAccountsCp!=null){
				cust.getPoolAccount().setBankCreditPool(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsCp) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getPoolAccount().getBankCreditPool().add(gnCustodianBank);
				}
			}

			if (invoices != null) {
				for (GnCustodianInvoice gnCustodianInvoice : invoices) {
					cust.getCustodianInvoices().add(gnCustodianInvoice);
				}

			}
			
			if (complience!=null){
				for (GnCustodianComplience gnCustodianComplience : complience) {
					cust.getCustodianComplience().add(gnCustodianComplience);
				}
				
			}

			String dataBankDk = null;
			String dataBankCk = null;
			String dataBankDb = null;
			String dataBankCb = null;
			String dataBankDe = null;
			String dataBankCe = null;
			String dataBankDp =null;
			String dataBankCp =null;
			String dataInvoice = null;
			String dataComplience =null;

			try {
				if (bankAccountsDk != null)
					dataBankDk = json.writeValueAsString(bankAccountsDk);
				else
					dataBankDk = json.writeValueAsString(new ArrayList<GnCustodianBank>());
				if (bankAccountsCk != null)
					dataBankCk = json.writeValueAsString(bankAccountsCk);
				else
					dataBankCk = json.writeValueAsString(new ArrayList<GnCustodianBank>());
				if (bankAccountsDb != null)
					dataBankDb = json.writeValueAsString(bankAccountsDb);
				else
					dataBankDb = json.writeValueAsString(new ArrayList<GnCustodianBank>());
				if (bankAccountsCb != null)
					dataBankCb = json.writeValueAsString(bankAccountsCb);
				else
					dataBankCb = json.writeValueAsString(new ArrayList<GnCustodianBank>());
				if (bankAccountsDe != null)
					dataBankDe = json.writeValueAsString(bankAccountsDe);
				else
					dataBankDe = json.writeValueAsString(new ArrayList<GnCustodianBank>());
				if (bankAccountsCe != null)
					dataBankCe = json.writeValueAsString(bankAccountsCe);
				else
					dataBankCe = json.writeValueAsString(new ArrayList<GnCustodianBank>());
				if (invoices != null)
					dataInvoice = json.writeValueAsString(invoices);
				else
					dataInvoice = json.writeValueAsString(new ArrayList<GnCustodianInvoice>());
				
				if (complience != null) dataComplience = json.writeValueAsString(complience);
				else  dataComplience = json.writeValueAsString(new ArrayList<GnCustodianComplience>());
				
				if (bankAccountsDp != null) dataBankDp = json.writeValueAsString(bankAccountsDp);
				else dataBankDp = json.writeValueAsString(new ArrayList<GnCustodianBank>());
				
				if (bankAccountsCp != null) dataBankCp = json.writeValueAsString(bankAccountsCp);
				else dataBankCp = json.writeValueAsString(new ArrayList<GnCustodianBank>());
				
			} catch (JsonGenerationException e) {
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}

			// VALIDATION
			validation.required("Code is", cust.getCustodianCode());
			validation.required("Name is", cust.getCustodianName());
			validation.required("Contact Person 1 is", cust.getContact1Name());
			validation.required("Position 1 is", cust.getContact1Position());
			validation.required("Contact Person 2 is", cust.getContact2Name());
			validation.required("Position 2 is", cust.getContact2Position());
			validation.required("Contact Person 3 is", cust.getContact3Name());
			validation.required("Position 3 is", cust.getContact3Position());
			validation.required("Address is", cust.getAddress1());
			log.debug("address 3 = " + cust.getAddress3());
			if (cust.getAddress3() != null)
				validation.required("Address 2 is", cust.getAddress2());
			validation.required("Country is", cust.getAddressCountry().getLookupId());
			validation.required("State is", cust.getAddressState().getLookupId());
			validation.required("Area is", cust.getAddressArea().getLookupId());
			validation.required("Postal Code is", cust.getAddressZipCode());
			validation.required("Phone 1 is", cust.getAddressPhone1());
			validation.required("Fax Number is", cust.getAddressPhone3());
			validation.required("Participant Code in tab KSEI/C-BEST is", cust.getKseiAccount().getAccountCode());
			validation.required("Member Code in tab BI/BI-SSSS is", cust.getBiAccount().getAccountCode());
			validation.required("Agent Code in tab BI/BI-SSSS is", cust.getBiAccount().getAccountAgentCode());
			validation.required("Terminal Code in tab BI/BI-SSSS is", cust.getBiAccount().getAccountTerminalCode());
			validation.required("Default Tax Code in tab Invoice is", cust.getTaxMaster().getTaxCode());

			validation.required("Invoice Account No is", cust.getInvoiceAccount().getInvoiceCustodianBank().getAccountNo());
			validation.required("Invoice Bank is", cust.getInvoiceAccount().getInvoiceCustodianBank().getBankCode().getThirdPartyCode());
			validation.required("Invoice Beneficiary Name is", cust.getInvoiceAccount().getInvoiceCustodianBank().getAccountName());
			validation.required("Invoice Currency is", cust.getInvoiceAccount().getInvoiceCustodianBank().getCurrency().getCurrencyCode());
			validation.required("Email", cust.getEmail());
			
			if (!Helper.isNullOrEmpty(cust.getEmail())){
				String email = cust.getEmail();
				String[] arrEmail = email.split("\\;");
				for(int i = 0;i<arrEmail.length;i++ ){
					if (!Helper.emailValidation(arrEmail[i])){
						validation.addError("", ExceptionConstants.INVALID_EMAIL);
						break;
					}
				}
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CUSTODIAN_BANK_SETUP));
				render("CustodianBankSetups/detail.html", cust, mode, dataBankDk, dataBankCk, dataBankDb, dataBankCb, dataBankDe, dataBankCe, dataBankDp, dataBankCp, dataInvoice, dataComplience);
			} else {
				String id = cust.getCustodianCode();
				serializerService.serialize(session.getId(), cust.getCustodianCode(), cust);
				confirming(id, mode);
			}
		} else {
			// flash.error("argument.null", cust);
			entry();
		}
	}

	@Check("administration.custodianbanksetup")
	public static void confirming(String id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		GnCustodianInfo cust = serializerService.deserialize(session.getId(), id, GnCustodianInfo.class);

		String dataBankDk = null;
		String dataBankCk = null;
		String dataBankDb = null;
		String dataBankCb = null;
		String dataBankDe = null;
		String dataBankCe = null;
		String dataBankDp = null;
		String dataBankCp = null;
		String dataInvoice = null;
		String dataComplience = null;

		try {
			if (cust.getKseiAccount().getBankDebetKsei() == null)
				cust.getKseiAccount().setBankDebetKsei(new ArrayList<GnCustodianBank>());
			if (cust.getKseiAccount().getBankCreditKsei() == null)
				cust.getKseiAccount().setBankCreditKsei(new ArrayList<GnCustodianBank>());
			if (cust.getBiAccount().getBankDebetBi() == null)
				cust.getBiAccount().setBankDebetBi(new ArrayList<GnCustodianBank>());
			if (cust.getBiAccount().getBankCreditBi() == null)
				cust.getBiAccount().setBankCreditBi(new ArrayList<GnCustodianBank>());
			if (cust.getEuroAccount().getBankDebetEuro() == null)
				cust.getEuroAccount().setBankDebetEuro(new ArrayList<GnCustodianBank>());
			if (cust.getEuroAccount().getBankCreditEuro() == null)
				cust.getEuroAccount().setBankCreditEuro(new ArrayList<GnCustodianBank>());
			if (cust.getCustodianInvoices() == null)
				cust.setCustodianInvoices(new HashSet<GnCustodianInvoice>());
			dataBankDk = json.writeValueAsString(cust.getKseiAccount().getBankDebetKsei());
			dataBankCk = json.writeValueAsString(cust.getKseiAccount().getBankCreditKsei());
			dataBankDb = json.writeValueAsString(cust.getBiAccount().getBankDebetBi());
			dataBankCb = json.writeValueAsString(cust.getBiAccount().getBankCreditBi());
			dataBankDe = json.writeValueAsString(cust.getEuroAccount().getBankDebetEuro());
			dataBankCe = json.writeValueAsString(cust.getEuroAccount().getBankCreditEuro());
			dataBankCe = json.writeValueAsString(cust.getEuroAccount().getBankCreditEuro());
			dataBankDp = json.writeValueAsString(cust.getPoolAccount().getBankDebetPool());
			dataBankCp = json.writeValueAsString(cust.getPoolAccount().getBankCreditPool());
			dataInvoice = json.writeValueAsString(cust.getCustodianInvoices());
			dataComplience = json.writeValueAsString(cust.getCustodianComplience());
		} catch (JsonGenerationException e) {
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		List<SelectItem> accountUsedByOption = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._USED_BY);
		renderArgs.put("accountUsedByOption", filterSelectItem(accountUsedByOption));
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CUSTODIAN_BANK_SETUP));
		render("CustodianBankSetups/detail.html", cust, mode, dataBankDk, dataBankCk, dataBankDb, dataBankCb, dataBankDe, dataBankCe, dataBankDp, dataBankCp, dataInvoice, dataComplience);
	}

	@Check("administration.custodianbanksetup")
	public static void confirm(GnCustodianInfo cust, GnCustodianBank[] bankAccountsDk, GnCustodianBank[] bankAccountsCk, GnCustodianBank[] bankAccountsDb, GnCustodianBank[] bankAccountsCb, GnCustodianBank[] bankAccountsDe, GnCustodianBank[] bankAccountsCe, GnCustodianBank[] bankAccountsDp, GnCustodianBank[] bankAccountsCp, GnCustodianInvoice[] invoices, GnCustodianComplience[] complience, String mode) {
		log.debug("confirm. cust: " + cust + " bankAccountsDk: " + bankAccountsDk + " bankAccountsCk: " + bankAccountsCk + " bankAccountsDb: " + bankAccountsDb + " bankAccountsCb: " + bankAccountsCb + " bankAccountsDe: " + bankAccountsDe + " bankAccountsCe: " + bankAccountsCe + " invoices: " + invoices + " mode: " + mode);

		if (cust == null)
			entry();
		String dataBankDk = null;
		String dataBankCk = null;
		String dataBankDb = null;
		String dataBankCb = null;
		String dataBankDe = null;
		String dataBankCe = null;
		String dataBankDp = null;
		String dataBankCp = null;
		String dataInvoice = null;
		String dataComplience = null;

		try {

			if (bankAccountsDk != null) {
				cust.getKseiAccount().setBankDebetKsei(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsDk) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getKseiAccount().getBankDebetKsei().add(gnCustodianBank);
				}
			}

			if (bankAccountsCk != null) {
				cust.getKseiAccount().setBankCreditKsei(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsCk) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getKseiAccount().getBankCreditKsei().add(gnCustodianBank);
				}
			}

			if (bankAccountsDb != null) {
				cust.getBiAccount().setBankDebetBi(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsDb) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getBiAccount().getBankDebetBi().add(gnCustodianBank);
				}
			}

			if (bankAccountsCb != null) {
				cust.getBiAccount().setBankCreditBi(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsCb) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getBiAccount().getBankCreditBi().add(gnCustodianBank);
				}
			}

			if (bankAccountsDe != null) {
				cust.getEuroAccount().setBankDebetEuro(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsDe) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getEuroAccount().getBankDebetEuro().add(gnCustodianBank);
				}
			}

			if (bankAccountsCe != null) {
				cust.getEuroAccount().setBankCreditEuro(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsCe) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getEuroAccount().getBankCreditEuro().add(gnCustodianBank);
				}
			}
			
			if (bankAccountsDp!=null){
				cust.getPoolAccount().setBankDebetPool(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsDp) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getPoolAccount().getBankDebetPool().add(gnCustodianBank);
				}
			}
			
			if (bankAccountsCp!=null){
				cust.getPoolAccount().setBankCreditPool(new ArrayList<GnCustodianBank>());
				for (GnCustodianBank gnCustodianBank : bankAccountsCp) {
					if ((!gnCustodianBank.getBranchCode().isEmpty()) && (gnCustodianBank.getBranchCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBranchCode(null);
					if ((!gnCustodianBank.getBicCode().isEmpty()) && (gnCustodianBank.getBicCode().equalsIgnoreCase("null")))
						gnCustodianBank.setBicCode(null);
					if ((!gnCustodianBank.getDescription().isEmpty()) && (gnCustodianBank.getDescription().equalsIgnoreCase("null")))
						gnCustodianBank.setDescription(null);
					if ((!gnCustodianBank.getParticipantAccount().isEmpty()) && (gnCustodianBank.getParticipantAccount().equalsIgnoreCase("null")))
						gnCustodianBank.setParticipantAccount(null);
					cust.getPoolAccount().getBankCreditPool().add(gnCustodianBank);
				}
			}

			if (invoices != null) {
				for (GnCustodianInvoice gnCustodianInvoice : invoices) {
					cust.getCustodianInvoices().add(gnCustodianInvoice);
				}
			}
			if (complience!=null){
				for (GnCustodianComplience gnCustodianComplience : complience) {
					cust.getCustodianComplience().add(gnCustodianComplience);
				}
			}
			try {
				dataBankDk = json.writeValueAsString(bankAccountsDk);
				dataBankCk = json.writeValueAsString(bankAccountsCk);
				dataBankDb = json.writeValueAsString(bankAccountsDb);
				dataBankCb = json.writeValueAsString(bankAccountsCb);
				dataBankDe = json.writeValueAsString(bankAccountsDe);
				dataBankCe = json.writeValueAsString(bankAccountsCe);
				dataBankDp = json.writeValueAsString(bankAccountsDp);
				dataBankCp = json.writeValueAsString(bankAccountsCp);
				dataInvoice = json.writeValueAsString(invoices);
				dataComplience = json.writeValueAsString(complience);
			} catch (JsonGenerationException e) {
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
			
			validation.clear();
			generalService.saveCustodianBankSetup("", cust, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			entry();

		} catch (MedallionException e) {
			flash.error("Code : '" + cust.getCustodianCode() + "' " + Messages.get(e.getMessage()));
			renderArgs.put("confirming", true);
			
			List<SelectItem> accountUsedByOption = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._USED_BY);
			renderArgs.put("accountUsedByOption", filterSelectItem(accountUsedByOption));
			
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CUSTODIAN_BANK_SETUP));
			render("CustodianBankSetups/detail.html", cust, mode, dataBankDk, dataBankCk, dataBankDb, dataBankCb, dataBankDe, dataBankCe, dataBankDp, dataBankCp, dataInvoice, dataComplience);
		}
	}

	@Check("administration.custodianbanksetup")
	public static void back(String id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		GnCustodianInfo cust = serializerService.deserialize(session.getId(), id, GnCustodianInfo.class);

		String dataBankDk = null;
		String dataBankCk = null;
		String dataBankDb = null;
		String dataBankCb = null;
		String dataBankDe = null;
		String dataBankCe = null;
		String dataBankDp = null;
		String dataBankCp = null;
		String dataInvoice = null;
		String dataComplience = null;

		try {
			if (cust.getKseiAccount().getBankDebetKsei() == null)
				cust.getKseiAccount().setBankDebetKsei(new ArrayList<GnCustodianBank>());
			if (cust.getKseiAccount().getBankCreditKsei() == null)
				cust.getKseiAccount().setBankCreditKsei(new ArrayList<GnCustodianBank>());
			if (cust.getBiAccount().getBankDebetBi() == null)
				cust.getBiAccount().setBankDebetBi(new ArrayList<GnCustodianBank>());
			if (cust.getBiAccount().getBankCreditBi() == null)
				cust.getBiAccount().setBankCreditBi(new ArrayList<GnCustodianBank>());
			if (cust.getEuroAccount().getBankDebetEuro() == null)
				cust.getEuroAccount().setBankDebetEuro(new ArrayList<GnCustodianBank>());
			if (cust.getEuroAccount().getBankCreditEuro() == null)
				cust.getEuroAccount().setBankCreditEuro(new ArrayList<GnCustodianBank>());
			if (cust.getCustodianInvoices() == null)
				cust.setCustodianInvoices(new HashSet<GnCustodianInvoice>());
			dataBankDk = json.writeValueAsString(cust.getKseiAccount().getBankDebetKsei());
			dataBankCk = json.writeValueAsString(cust.getKseiAccount().getBankCreditKsei());
			dataBankDb = json.writeValueAsString(cust.getBiAccount().getBankDebetBi());
			dataBankCb = json.writeValueAsString(cust.getBiAccount().getBankCreditBi());
			dataBankDe = json.writeValueAsString(cust.getEuroAccount().getBankDebetEuro());
			dataBankCe = json.writeValueAsString(cust.getEuroAccount().getBankCreditEuro());
			dataInvoice = json.writeValueAsString(cust.getCustodianInvoices());
			dataBankDp = json.writeValueAsString(cust.getPoolAccount().getBankDebetPool());
			dataBankCp = json.writeValueAsString(cust.getPoolAccount().getBankCreditPool());
			dataComplience = json.writeValueAsString(cust.getCustodianComplience());
			
			List<SelectItem> accountUsedByOption = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._USED_BY);
			renderArgs.put("accountUsedByOption", filterSelectItem(accountUsedByOption));
		} catch (JsonGenerationException e) {
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CUSTODIAN_BANK_SETUP));
		render("CustodianBankSetups/detail.html", cust, mode, dataBankDk, dataBankCk, dataBankDb, dataBankCb, dataBankDe, dataBankCe, dataBankDp, dataBankCp, dataInvoice, dataComplience);
	}
	
	private static List<SelectItem> filterSelectItem(List<SelectItem> items) {
		List<SelectItem> nitems = new ArrayList<SelectItem>();
		for (SelectItem item : items) {
			//if ("USED_BY-ALL".equals(item.value) || "USED_BY-1".equals(item.value) || "USED_BY-2".equals(item.value)) {
				nitems.add(item); 
			//}
		}
		return nitems;
	}
}
