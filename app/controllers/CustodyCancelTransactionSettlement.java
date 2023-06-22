package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import helpers.serializers.CouponSchedulePickSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import play.i18n.Messages;
import play.mvc.Before;
import vo.CustodyCancelTransactionSettlementSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.CsTransactionCharge;
import com.simian.medallion.model.GnCalendar;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.ScCouponSchedule;
import com.simian.medallion.vo.SelectItem;

public class CustodyCancelTransactionSettlement extends MedallionController {
	private static Logger log = Logger.getLogger(CustodyCancelTransactionSettlement.class);

	@Before(only = { "view", "save", "confirming", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION));
		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		GnLookup classTrd = generalService.getLookup(LookupConstants.CLASSIFICATION_TRADING);
		renderArgs.put("classificationTrd", LookupConstants.CLASSIFICATION_TRADING);
		renderArgs.put("classTrd", classTrd);

		GnLookup classAfs = generalService.getLookup(LookupConstants.CLASSIFICATION_AFS);
		renderArgs.put("classificationAfs", LookupConstants.CLASSIFICATION_AFS);
		renderArgs.put("classAfs", classAfs);

		GnLookup classHtm = generalService.getLookup(LookupConstants.CLASSIFICATION_HTM);
		renderArgs.put("classificationHtm", LookupConstants.CLASSIFICATION_HTM);
		renderArgs.put("classHtm", classHtm);
		renderArgs.put("chargeFreqT", LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE);
		renderArgs.put("transTypeS", LookupConstants.TRANSACTION_TYPE_SELL_CODE);
		renderArgs.put("transTypeB", LookupConstants.TRANSACTION_TYPE_BUY_CODE);
		renderArgs.put("secClassEQ", LookupConstants.SECURITY_CLASS_EQUITY_CODE);
		renderArgs.put("secClassFI", LookupConstants.SECURITY_CLASS_FIXED_INCOME_CODE);
		renderArgs.put("secClassMM", LookupConstants.SECURITY_CLASS_MONEY_MARKET_CODE);
		renderArgs.put("valuationAmortized", LookupConstants.VALUATION_METHOD_AMORTIZED);
		renderArgs.put("countryId", LookupConstants.COUNTRY_LOCAL);
		renderArgs.put("prematchP", LookupConstants.PREMACH_STATUS_P);
		renderArgs.put("prematchA", LookupConstants.PREMACH_STATUS_A);
		renderArgs.put("prematchO", LookupConstants.PREMACH_STATUS_O);
		renderArgs.put("prematchW", LookupConstants.PREMACH_STATUS_W);
		renderArgs.put("prematchWU", LookupConstants.PREMACH_STATUS_WU);
		renderArgs.put("prematchWM", LookupConstants.PREMACH_STATUS_WM);
		renderArgs.put("prematchWO", LookupConstants.PREMACH_STATUS_WO);
		renderArgs.put("matchU", LookupConstants.MATCH_STATUS_U);
		renderArgs.put("matchM", LookupConstants.MATCH_STATUS_M);
		renderArgs.put("bnTransTypeDebet", LookupConstants.BN_TRANSACTION_TYPE_DEBIT);
		renderArgs.put("bnTransTypeCredit", LookupConstants.BN_TRANSACTION_TYPE_CREDIT);
		renderArgs.put("depositoryCbest", LookupConstants.DEPOSITORY_CODE_KSEI_CBEST);
		renderArgs.put("depositoryNA", LookupConstants.DEPOSITORY_CODE_NA);
		renderArgs.put("depositoryEuro", LookupConstants.DEPOSITORY_CODE_EUROCLEARS);

	}

	@Check("transaction.custodyCancelTransactionSettlement")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CustodyCancelTransactionSettlementSearchParameters params = new CustodyCancelTransactionSettlementSearchParameters();
		params.settlementDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_TRANSACTION_SETTLEMENT));
		render("CustodyCancelTransactionSettlement/list.html", params, mode);
	}

	@Check("transaction.custodyCancelTransactionSettlement")
	public static void searchCustodyCancelTrade(CustodyCancelTransactionSettlementSearchParameters params) {
		log.debug("searchCustodyCancelTrade. params: " + params);

		List<CsTransaction> csCancelTrades = accountService.searchCustodyCancelTradeSettlement(params.settlementNo, params.custodyAccountKey, params.securityType, params.securityId, params.settlementDate);
		render("CustodyCancelTransactionSettlement/grid.html", csCancelTrades);
	}

	@Check("transaction.custodyCancelTransactionSettlement")
	public static void view(Long id, String fromView) {
		log.debug("view. id: " + id + " fromView: " + fromView);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsTransaction transaction = accountService.getTransaction(id);
		BigDecimal txCharge = new BigDecimal(0);
		for (CsTransactionCharge chargeTrans : transaction.getTransactionCharges()) {
			BigDecimal taxAmount = chargeTrans.getTaxAmount();
			if (taxAmount == null)
				taxAmount = BigDecimal.ZERO;
			txCharge = taxAmount.add(txCharge);
			chargeTrans.setChargeNetAmount(chargeTrans.getChargeValue().add(chargeTrans.getTaxAmount()));
			chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
		}
		log.debug("[VIEW CANCEL TRADE] transaction.classification.lookupId = " + transaction.getClassification().getLookupId());
		transaction.setTaxOfCharges(txCharge);
		transaction.setCancelledDate(transaction.getSettlementDate());
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		if (transaction.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(transaction.getUdf(), new TypeReference<HashMap<String, String>>() {
				});
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
				}
			} catch (JsonParseException e) {
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		} else {
			for (GnUdfMaster udf : udfs) {
				if (udf.getLookupGroup() != null) {
					udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
					udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
				}
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_TRANSACTION_SETTLEMENT));
		render("CustodyCancelTransactionSettlement/entry.html", transaction, mode, fromView, udfs);
	}

	@Check("transaction.custodyCancelTransactionSettlement")
	public static void save(CsTransaction transaction, List<CsTransactionCharge> charges, List<GnUdfMaster> udfs, boolean settlement, String fromView) {
		log.debug("save. transaction: " + transaction + " charges: " + charges + " udfs: " + udfs + " settlement: " + settlement + " fromView: " + fromView);

		Date applicationDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		if (transaction != null && charges != null) {
			transaction.getTransactionCharges().clear();
			transaction.getTransactionCharges().addAll(charges);
		}

		// TODO : CHANGE THIS
		try {
			log.debug("udfs:" + udfs);
			if (udfs != null) {
				Map<String, String> data = new HashMap<String, String>();
				for (GnUdfMaster udf : udfs) {
					data.put(udf.getFieldName(), udf.getValue());
				}
				String udfString = json.writeValueAsString(data);
				transaction.setUdf(udfString);
			}
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
		// validate
		validation.required("Cancel Date is", transaction.getCancelledDate());
		validation.required("Transaction Date is", transaction.getTransactionDate());
		validation.required("Account No is", transaction.getAccount().getAccountNo());
		validation.required("Transaction Code is", transaction.getTransactionTemplate().getTransactionCode());
		validation.required("Security Code is", transaction.getSecurity().getSecurityId());
		validation.required("Counter Party is", transaction.getCounterParty().getThirdPartyCode());
		validation.required("Classification is", transaction.getClassification().getLookupId());
		validation.required("Settlement Date is", transaction.getSettlementDate());
		validation.required("Settlement Account is", transaction.getSettlementAccount().getAccountNo());

		if (transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupCode().equals(LookupConstants.TRANSACTION_TYPE_SELL_CODE)) {
			validation.required("Portfolio is", transaction.getHoldingRefs());
		}

		validation.required("Quantity/Face Value is", transaction.getQuantity());
		validation.required("Price is", transaction.getPrice());

		if (transaction.getTransactionTemplate().getSecurityType().getIsDiscounted() == true && transaction.getTransactionTemplate().getSecurityType().getIsPrice() == false) {
			validation.required("Nominal is", transaction.getAmount());
			validation.required("Tax Amount (left) is", transaction.getDiscountTax());
		}

		if (transaction.getTransactionTemplate().getSecurityType().getIsDiscounted() == true && transaction.getTransactionTemplate().getSecurityType().getIsPrice() == true) {
			validation.required("Tax Amount is", transaction.getDiscountTax());
		}

		if (transaction.getTransactionTemplate().getSecurityType().getIsDiscounted() == true) {
			// validation.required("Tax Code (left) is",
			// transaction.getDiscountTaxMaster().getTaxCode());
		}

		if (transaction.getTransactionTemplate().getSecurityType().getHasInterest() == true) {
			validation.required("Interest Rate is", transaction.getInterestRate());
			validation.required("Accrued Interest is", transaction.getAccruedInterest());
			// validation.required("Tax Code (right) is",
			// transaction.getTaxOnInterestMaster().getTaxCode());
			validation.required("Tax Amount (right) is", transaction.getTaxOnInterest());
		}

		if (transaction.getTransactionTemplate().getSecurityType().getSecurityClass().getLookupCode().equals(LookupConstants.SECURITY_CLASS_MONEY_MARKET_CODE)) {
			validation.required("Maturity Date is", transaction.getMaturityDate());
		}

		if (transaction.getCancelledDate() != null) {
			log.debug("[SAVE] what value = " + (transaction.getCancelledDate().getTime() < transaction.getSettlementDate().getTime()));
			if (transaction.getCancelledDate().getTime() < transaction.getSettlementDate().getTime()) {
				validation.addError("", "Cancel Date is less than Settlement Date");
			}

			if (transaction.getCancelledDate().getTime() > applicationDate.getTime()) {
				validation.addError("", "Cancel Date is greater than Application Date");
			}
		}

		if (validation.hasErrors()) {
			log.debug("[SAVE] masuk sini!!");
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_TRANSACTION_SETTLEMENT));
			render("CustodyCancelTransactionSettlement/entry.html", transaction, fromView, udfs, mode);
		}
		serializerService.serialize(session.getId(), transaction.getTransactionKey(), transaction);
		confirming(transaction.getTransactionKey(), settlement, fromView, mode);

	}

	@Check("transaction.custodyCancelTransactionSettlement")
	public static void confirming(Long id, boolean settlement, String fromView, String mode) {
		log.debug("confirming. id: " + id + " settlement: " + settlement + " fromView: " + fromView + " mode: " + mode);

		boolean confirming = true;
		CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);

		log.debug(">>> [JUN] transaction=" + transaction);

		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		if (transaction.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(transaction.getUdf(), new TypeReference<HashMap<String, String>>() {
				});
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
				}
			} catch (JsonParseException e) {
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_TRANSACTION_SETTLEMENT));
		render("CustodyCancelTransactionSettlement/entry.html", transaction, mode, fromView, udfs, confirming);
	}

	@Check("transaction.custodyCancelTransactionSettlement")
	public static void confirm(CsTransaction transaction, List<CsTransactionCharge> charges, List<GnUdfMaster> udfs, String param) {
		log.debug("confirm. transaction: " + transaction + " charges: " + charges + " udfs: " + udfs + " param: " + param);

		if (transaction != null && charges != null) {
			transaction.getTransactionCharges().clear();
			transaction.getTransactionCharges().addAll(charges);
		}
		if (udfs != null) {
			try {
				Map<String, String> data = new HashMap<String, String>();
				for (GnUdfMaster udf : udfs) {
					data.put(udf.getFieldName(), udf.getValue());
				}

				String udfString = json.writeValueAsString(data);
				transaction.setUdf(udfString);
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
		}

		//String mode = UIConstants.DISPLAY_MODE_ENTRY;
		//boolean confirming = true;
		try {
			List<GnCalendar> holidays = generalService.listHolidays(UIConstants.SIMIAN_BANK_ID);
			for (GnCalendar holiday : holidays) {
				if (holiday.getId().getCalendarDate().getTime() == (transaction.getSettlementDate().getTime())) {
					throw new MedallionException(ExceptionConstants.DATE_IS_HOLIDAY, "transaction.settlementDate");
				}

				if (holiday.getId().getCalendarDate().getTime() == (transaction.getTransactionDate().getTime())) {
					throw new MedallionException(ExceptionConstants.DATE_IS_HOLIDAY, "transaction.transactionDate");
				}
			}
			log.debug("[CONFIRM] transaction.source = " + transaction.getSource());
			CsTransaction trx = transactionService.createCsCancelTransactionSettlement(MenuConstants.CS_CANCEL_TRANSACTION_SETTLEMENT, transaction, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_TRANSACTION_SETTLEMENT));
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "success");
			result.put("messages", Messages.get("settlement.confirmed.successful", trx.getSettlementNumber()));
			renderJSON(result);
		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("messages", Messages.get(ex.getErrorCode()));
			renderJSON(result);
		}
	}

	@Check("transaction.custodyCancelTransactionSettlement")
	public static void back(Long id, boolean settlement, String mode, String fromView) throws JsonParseException, JsonMappingException, IOException {
		log.debug("back. id: " + id + " settlement: " + settlement + " mode: " + mode + " fromView: " + fromView);

		CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_TRANSACTION_SETTLEMENT));
		render("CustodyCancelTransactionSettlement/entry.html", transaction, mode, fromView);
	}

	@Check("transaction.custodyCancelTransactionSettlement")
	public static void getCouponDates(long securityKey, Date settlementDate) {
		log.debug("getCouponDates. securityKey: " + securityKey + " settlementDate: " + settlementDate);

		ScCouponSchedule schedule = securityService.getCouponScheduleOn(securityKey, settlementDate);
		renderJSON(schedule, new CouponSchedulePickSerializer());
	}

	@Check("transaction.custodyCancelTransactionSettlement")
	public static void charges(Long custodyAccountKey, String securityClass, String securityType, Long transactionTemplateKey, Long securityKey, BigDecimal quantity, BigDecimal nominal) {
		log.debug("charges. custodyAccountKey: " + custodyAccountKey + " securityClass: " + securityClass + " securityType: " + securityType + " transactionTemplateKey: " + transactionTemplateKey + " securityKey: " + securityKey + " quantity: " + quantity + " nominal: " + nominal);

		if (custodyAccountKey != null) {

			List<CsTransactionCharge> transactionCharges = transactionService.getDefaultCharges(LookupConstants.CHARGE_CATEGORY_TRANSACTION, custodyAccountKey, securityClass, securityType, transactionTemplateKey, securityKey, quantity, nominal);
			String charges = null;
			try {
				charges = json.writeValueAsString(transactionCharges);
			} catch (JsonGenerationException e) {
				log.debug("serialize");
			} catch (JsonMappingException e) {
				log.debug("serialize");
			} catch (IOException e) {
				log.debug("serialize");
			}
			log.debug("### charges: " + charges);
			renderJSON(charges);
		}
	}

	public static void approval(String taskId, Long keyId, RgTrade rg, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " rg: " + rg + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsTransaction transaction = accountService.getTransaction(keyId);

		BigDecimal txCharge = new BigDecimal(0);
		for (CsTransactionCharge chargeTrans : transaction.getTransactionCharges()) {
			BigDecimal taxAmount = chargeTrans.getTaxAmount();
			// if (taxAmount==null) taxAmount = BigDecimal.ZERO;
			if (taxAmount == null) {
				taxAmount = BigDecimal.ZERO;
			}
			txCharge = taxAmount.add(txCharge);
		}
		transaction.setTaxOfCharges(txCharge);
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}

		render("CustodyCancelTransactionSettlement/approval.html", transaction, mode, taskId, from, keyId);
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		try {
			transactionService.approveCsCancelTransactionSettlement(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			if ((ExceptionConstants.INVALID_DATA + ".error.sentToFaInterface").equals(e.getErrorCode())) {
				e = new MedallionException("error.sentToFaInterface");
			}
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId);

		try {
			transactionService.rejectCsCancelTransactionSettlement(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit() {
		log.debug("edit. ");
	}

}
