package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.mvc.Before;
import play.mvc.With;
import vo.DepositoSearchParameter;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.CsCertificate;
import com.simian.medallion.model.CsChargeMaster;
import com.simian.medallion.model.CsTransactionCharge;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.TdInterestSchedule;
import com.simian.medallion.model.TdMaster;
import com.simian.medallion.model.TdTransaction;
import com.simian.medallion.model.TdTransactionCharge;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import extensions.StatusExtension;

@With(Secure.class)
public class InquiryDeposito extends Registry {
	private static Logger log = Logger.getLogger(AuditTrail.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
		List<SelectItem> operatorNominal = UIHelper.operatorNominal();
		renderArgs.put("operatorNominal", operatorNominal);
	}

	@Before(only = { "view", "populatePaymentSchedule" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		GnLookup paymentFreqMonthly = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MONTHLY);
		GnLookup paymentFreqMaturity = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MATURITY);
		renderArgs.put("paymentFreqMonthlyId", paymentFreqMonthly.getLookupId());
		renderArgs.put("paymentFreqMonthlyDesc", paymentFreqMonthly.getLookupDescription());
		renderArgs.put("paymentFreqMaturityId", paymentFreqMaturity.getLookupId());
		renderArgs.put("paymentFreqMaturityDesc", paymentFreqMaturity.getLookupDescription());
		List<SelectItem> maturityIns = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITO_TYPE);
		renderArgs.put("maturityIns", maturityIns);

		renderArgs.put("intPaymentFreqMonthly", LookupConstants.INTEREST_FREQUENCY_MONTHLY);

		GnLookup classTrd = generalService.getLookup(LookupConstants.CLASSIFICATION_TRADING);
		renderArgs.put("classificationTrd", LookupConstants.CLASSIFICATION_TRADING);
		renderArgs.put("classTrd", classTrd);

		GnLookup classAfs = generalService.getLookup(LookupConstants.CLASSIFICATION_AFS);
		renderArgs.put("classificationAfs", LookupConstants.CLASSIFICATION_AFS);
		renderArgs.put("classAfs", classAfs);

		GnLookup classHtm = generalService.getLookup(LookupConstants.CLASSIFICATION_HTM);
		renderArgs.put("classificationHtm", LookupConstants.CLASSIFICATION_HTM);
		renderArgs.put("classHtm", classHtm);

		renderArgs.put("depositoInquiryOriginated", LookupConstants.INQUIRY_DEPOSITO_ORIGINATED);
		renderArgs.put("depositoInquiryFullRedeem", LookupConstants.INQUIRY_DEPOSITO_FULL_REDEEM);

		GnLookup depositoTemplatePlacement = generalService.getLookup(LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT);
		renderArgs.put("depositoTemplatePlacementDescription", depositoTemplatePlacement.getLookupDescription());

		GnLookup depositoTemplateBreak = generalService.getLookup(LookupConstants.DEPOSITO_TEMPLATE_BREAK);
		renderArgs.put("depositoTemplateBreakDescription", depositoTemplateBreak.getLookupDescription());

		GnLookup depositoTemplateFullRedeem = generalService.getLookup(LookupConstants.DEPOSITO_TEMPLATE_FULL_REDEEM);
		renderArgs.put("depositoTemplateFullRedeemDescription", depositoTemplateFullRedeem.getLookupDescription());

		renderArgs.put("open", StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_OPEN));
		renderArgs.put("reject", StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_REJECTED));
		renderArgs.put("cancelled", StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_CANCELED));
		renderArgs.put("approved", StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_APPROVED));
	}

	@Check("transaction.inquirydeposito")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_INQUIRY));
		render();
	}

	@Check("transaction.inquirydeposito")
	public static void search(Paging page, DepositoSearchParameter param) {
		log.debug("search. page: " + page + " param: " + param);

		if (!Helper.isNullOrEmpty(param.customerCode)) {
			page.addParams("acc.customer.customerKey", page.EQUAL, Long.parseLong(param.customerCode));
		}
		page.addParams("sectype.securityType.securityType", page.EQUAL, param.securityType);
		if (!Helper.isNullOrEmpty(param.securityCode)) {
			page.addParams("sec.securityKey", page.EQUAL, Long.parseLong(param.securityCode));
		}
		page.addParams("acc.isActive", page.EQUAL, Boolean.TRUE);
		page.addParams("m.depositoNo", param.depositoNoOperator, UIHelper.withOperator(param.depositoNo, param.depositoNoOperator));
		page.addParams(Helper.searchAll("(m.depositoNo||acc.accountNo||to_char(m.maturityDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "acc.name||sec.securityType.securityType||sec.securityId)"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		if (param.nominal) {
			page.addParams("m.amount", page.GREAT, new BigDecimal(0));
		} else {
			page.addParams("m.amount", page.GREATEQUAL, new BigDecimal(0));
		}

		page = depositoService.pagingInquiryDeposito(page);

		renderJSON(page);
	}

	@Check("transaction.inquirydeposito")
	public static void entry() {
		log.debug("entry. ");
	}

	@Check("transaction.inquirydeposito")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		TdMaster deposito = depositoService.getMasterDeposito(id);
		List<TdTransaction> transactionHistory = depositoService.listTransactionHistoryByMasterDepositoId(id);
		List<TdTransaction> rolloverHistory = depositoService.listRolloverHistoryByMasterDepositoId(id);
		String rstatus = Helper.setRecordStatusDeposito(deposito.getRecordStatus());
		CsCertificate certificate = customerService.getCertificateFromDeposito(deposito.getDepositoKey());
		String sertifikat = "";
		if (certificate != null) {
			sertifikat = certificate.getCertificateId();
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_INQUIRY));
		render("InquiryDeposito/entry.html", mode, deposito, transactionHistory, rolloverHistory, rstatus, sertifikat);
	}

	public static void save(TdMaster deposito, List<TdTransactionCharge> charges, List<TdInterestSchedule> schedules, List<GnUdfMaster> udfs, String mode) {
		log.debug("save. deposito: " + deposito + " charges: " + charges + " schedules: " + schedules + " udfs: " + udfs + " mode: " + mode);
	}

	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);
	}

	public static void confirm(TdMaster deposito, List<TdTransactionCharge> charges, List<TdInterestSchedule> schedules, List<GnUdfMaster> udfs, String mode) {
		log.debug("confirm. deposito: " + deposito + " charges: " + charges + " schedules: " + schedules + " udfs: " + udfs + " mode: " + mode);
	}

	public static void back(Long id) {
		log.debug("back. id: " + id);
	}

	@Check("transaction.inquirydeposito")
	public static void loadCharges(Long custodyAccountKey, String securityClass, String securityType, Long transactionTemplateKey, Long securityKey, BigDecimal quantity, BigDecimal nominal) {
		log.debug("loadCharges. custodyAccountKey: " + custodyAccountKey + " securityClass: " + securityClass + " securityType: " + securityType + " transactionTemplateKey: " + transactionTemplateKey + " securityKey: " + securityKey + " quantity: " + quantity + " nominal: " + nominal);

		if (custodyAccountKey != null) {
			String charges = null;
			List<TdTransactionCharge> depositoCharges = new ArrayList<TdTransactionCharge>();
			List<CsTransactionCharge> transactionCharges = transactionService.getDefaultCharges(LookupConstants.CHARGE_CATEGORY_TRANSACTION, custodyAccountKey, securityClass, securityType, transactionTemplateKey, securityKey, quantity, nominal);

			for (CsTransactionCharge csTransactionCharge : transactionCharges) {
				TdTransactionCharge depositoCharge = new TdTransactionCharge();
				depositoCharge.setChargeMaster(csTransactionCharge.getChargeMaster());
				depositoCharge.setChargeCapitalized(csTransactionCharge.getChargeCapitalized());
				depositoCharge.setTaxAmount(csTransactionCharge.getTaxAmount());
				depositoCharge.setChargeFrequency(csTransactionCharge.getChargeFrequency());
				depositoCharge.setChargeValue(csTransactionCharge.getChargeValue());
				depositoCharge.setChargeWaived(csTransactionCharge.getChargeWaived());
				BigDecimal taxAmount = depositoCharge.getTaxAmount();

				if (taxAmount == null)
					taxAmount = BigDecimal.ZERO;
				depositoCharge.setChargeNetAmount(depositoCharge.getChargeValue().add(taxAmount));
				depositoCharge.setTaxMaster(depositoCharge.getChargeMaster().getTaxMaster());

				depositoCharges.add(depositoCharge);
			}
			try {
				// charges = json.writeValueAsString(transactionCharges);
				charges = json.writeValueAsString(depositoCharges);
			} catch (JsonGenerationException e) {
				log.debug("serialize", e);
			} catch (JsonMappingException e) {
				log.debug("serialize", e);
			} catch (IOException e) {
				log.debug("serialize", e);
			}
			log.debug("### charges: " + charges);
			renderJSON(charges);
		}
	}

	@Check("transaction.inquirydeposito")
	public static void charge(String code, BigDecimal quantity, BigDecimal nominal, Long custodyAccountKey) {
		log.debug("charge. code: " + code + " quantity: " + quantity + " nominal: " + nominal + " custodyAccountKey: " + custodyAccountKey);

		// CsChargeMaster charge =
		// generalService.getChargeMaster(UIConstants.SIMIAN_BANK_ID, code);
		CsChargeMaster charge = generalService.getChargeMasterForTransaction(UIConstants.SIMIAN_BANK_ID, code);
		log.debug("[CONT] >>> custodyAccountKey = " + custodyAccountKey);
		if (charge != null) {
			CsTransactionCharge transactionCharge = transactionService.calculateCharge(charge, quantity, nominal, custodyAccountKey);
			TdTransactionCharge depositoCharge = new TdTransactionCharge();

			BigDecimal taxAmount = transactionCharge.getTaxAmount();
			if (taxAmount == null)
				taxAmount = BigDecimal.ZERO;
			depositoCharge.setChargeNetAmount(transactionCharge.getChargeValue().add(taxAmount));
			// transactionCharge.setChargeNetAmount(transactionCharge.getChargeValue().add(taxAmount));
			transactionCharge.setTaxMaster(transactionCharge.getChargeMaster().getTaxMaster());
			try {
				renderJSON(json.writeValueAsString(transactionCharge));
			} catch (JsonGenerationException e) {
				log.debug("serialize", e);
			} catch (JsonMappingException e) {
				log.debug("serialize", e);
			} catch (IOException e) {
				log.debug("serialize", e);
			}
		}
	}

	@Check("transaction.inquirydeposito")
	public static void populatePaymentSchedule(Date placementDate, Date maturityDate, BigDecimal nominal, BigDecimal interestRate, String accrualBase, String yearBase, Integer totalCoupon, boolean considerHoliday, String freqSecurity) {
		log.debug("populatePaymentSchedule. placementDate: " + placementDate + " maturityDate: " + maturityDate + " nominal: " + nominal + " interestRate: " + interestRate + " accrualBase: " + accrualBase + " yearBase: " + yearBase + " totalCoupon: " + totalCoupon + " considerHoliday: " + considerHoliday + " freqSecurity: " + freqSecurity);

		String frequency = LookupConstants.FREQUENCY_MONTH;
		List<TdInterestSchedule> schedules = depositoService.populatePaymentSchedule(placementDate, maturityDate, nominal, interestRate, accrualBase, yearBase, frequency, totalCoupon, considerHoliday, freqSecurity);

		render("DepositoPlacements/gridPaymentSchedules.html", schedules);
	}
}
