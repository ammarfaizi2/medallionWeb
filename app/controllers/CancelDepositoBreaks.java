package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.TypeReference;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.DepositoSearchParameter;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.FormatRoundHelper;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.GnApplicationDate;
import com.simian.medallion.model.GnCurrency;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.TdInterestSchedule;
import com.simian.medallion.model.TdMaster;
import com.simian.medallion.model.TdTransaction;
import com.simian.medallion.model.TdTransactionCharge;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.TransactionHelper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class CancelDepositoBreaks extends Registry {
	private static Logger log = Logger.getLogger(CancelDepositoBreaks.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "entry", "edit", "view", "save", "confirming", "back", "confirm", "approval", "populatePaymentSchedule" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> yesNo = UIHelper.yesNoOperators();
		renderArgs.put("yesNo", yesNo);

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

		renderArgs.put("cancelDepositoBreakOriginated", LookupConstants.CANCEL_DEPOSITO_BREAK_ORIGINATED);
		renderArgs.put("depositoInquiryOriginated", LookupConstants.INQUIRY_DEPOSITO_ORIGINATED);
		renderArgs.put("depositoInquiryFullRedeem", LookupConstants.INQUIRY_DEPOSITO_FULL_REDEEM);
	}

	@Check("transaction.canceldepositobreak")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_BREAK));
		render();
	}

	@Check("transaction.canceldepositobreak")
	public static void paging(Paging page, DepositoSearchParameter param) {
		log.debug("paging. page: " + page + " param: " + param);

		page.addParams("tt.transactionDate", page.GREATEQUAL, param.dateFrom);
		page.addParams("tt.transactionDate", page.LESSEQUAL, param.dateTo);
		if (!param.customerCode.isEmpty())
			page.addParams("a.customer.customerKey", page.EQUAL, Long.parseLong(param.customerCode));
		page.addParams("st.securityType", page.EQUAL, param.securityType);
		if (!param.securityCode.isEmpty())
			page.addParams("s.securityKey", page.EQUAL, Long.parseLong(param.securityCode));
		page.addParams("dt.depositoTemplate.lookupId", page.EQUAL, LookupConstants.DEPOSITO_TEMPLATE_BREAK);
		page.addParams("tt.status", page.LIKE, "%" + LookupConstants.__RECORD_STATUS_APPROVED + "%");
		page.addParams("dp.amount", page.EQUAL, new Integer(0));
		page.addParams("dp.recordStatus", page.LIKE, "%" + LookupConstants.__RECORD_STATUS_APPROVED + "%");
		page.addParams("dp.depositoNo", param.depositoNoOperator, UIHelper.withOperator(param.depositoNo, param.depositoNoOperator));
		page.addParams(Helper.searchAll("(to_char(tt.transactionDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||dp.depositoNo||a.accountNo||a.name||" + "s.securityId||tt.amount||tt.interestRate||to_char(tt.maturityDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = depositoService.pagingDepositoBreak(page);

		log.debug("depositoSearchNo = " + param.depositoNo);
		log.debug("depositoNoOperator = " + param.depositoNoOperator);
		renderJSON(page);
	}

	@Check("transaction.canceldepositobreak")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		TdMaster deposito = new TdMaster();
		String taskId = "";
		/*
		 * // TdTransactionTemplate depTemplate =
		 * depositoService.getTdTransactionTemplate
		 * (LookupConstants.DEPOSITO_TEMPLATE_BREAK);
		 * deposito.setDepositoTemplate(depTemplate.getDepositoTemplate());
		 * deposito
		 * .setTransactionTemplate(depTemplate.getTransactionTemplate());
		 * deposito
		 * .setDepositoTemplateKey(depTemplate.getDepositoTemplateKey());
		 */
		GnLookup paymentFreqMaturity = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MATURITY);
		deposito.setInterestFrequency(paymentFreqMaturity);
		deposito.setDepositoBreak(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));

		deposito.setMaturityInstruction(new GnLookup(LookupConstants.DEPOSITO_TYPE_FULL_REDEEM));

		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		for (GnUdfMaster udf : udfs) {
			// START UDF FOR DROPDOWN
			if (udf.getLookupGroup() != null) {
				udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
				udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
			}
			// END DROPDOWN
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_BREAK));
		render("DepositoBreaks/entry.html", mode, deposito, taskId, udfs);
	}

	@Check("transaction.canceldepositobreak")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String taskId = "";
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		String origin = LookupConstants.CANCEL_DEPOSITO_BREAK_ORIGINATED;
		TdTransaction trx = depositoService.getTdTransaction(id);
		TdMaster deposito = depositoService.getMasterDeposito(trx.getDeposito().getDepositoKey());

		deposito.setAmount(trx.getAmount());
		deposito.setInterestRate(trx.getInterestRate());
		GnApplicationDate currentApplicationDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
		deposito.setCancelDate(currentApplicationDate.getCurrentBusinessDate());
		if (trx.getRemarksCorrection() != null) {
			deposito.setNeedCorrection(true);
			deposito.setRemarksCorrection(trx.getRemarksCorrection());
		}

		deposito.setTransactionKey(trx.getTransactionKey());

		if (deposito.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_NEW) || deposito.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_UPDATED)) {
			mode = UIConstants.DISPLAY_MODE_VIEW;
		}

		deposito.setTaxMaster(generalService.getTaxMaster(trx.getTaxOnInterestCode().getTaxKey()));

		// DEFAULT TEMPLATE
		deposito.setDepositoTemplate(trx.getDepositoTrxTemplate().getDepositoTemplate());
		deposito.setTransactionTemplate(trx.getDepositoTrxTemplate().getTransactionTemplate());
		deposito.setDepositoTemplateKey(trx.getDepositoTrxTemplate().getDepositoTemplateKey());

		deposito.setDepositoBreak(trx.getTransactionDate());
		deposito.setTaxOnInterestNet(trx.getTaxOnInterestNet());
		deposito.setAccruedInterest(trx.getTaxOnInterest());

		BigDecimal amountTax = transactionService.calculateTax(deposito.getAccruedInterest(), deposito.getTaxMaster());
		GnSystemParameter currencyTaxParam = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX);
		GnSystemParameter currencyTaxParamRound = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX_ROUND);
		GnSystemParameter currencyTaxParamRoundType = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX_ROUND_TYPE);

		GnCurrency currencySys = generalService.getCurrency(currencyTaxParam.getValue().trim().toUpperCase());

		if (deposito.getSecurity().getCurrency().getCurrencyCode().equals(currencySys.getCurrencyCode())) {
			Integer roundInt = Integer.parseInt(currencyTaxParamRound.getValue());
			String roundType = currencyTaxParamRoundType.getValue().trim().toUpperCase();
			amountTax = Helper.format(amountTax, roundInt, FormatRoundHelper.getRoundMode(roundType));
		}

		deposito.setTaxAmount(amountTax);

		List<TdTransactionCharge> listTrxCharges = new ArrayList<TdTransactionCharge>();
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
		BigDecimal totalFee = BigDecimal.ZERO;
		BigDecimal sumOfNetCharge = BigDecimal.ZERO;
		if (trx != null) {
			for (TdTransactionCharge chargeTrans : trx.getDepositoCharges()) {
				BigDecimal taxAmount = chargeTrans.getTaxAmount();
				if (taxAmount == null)
					taxAmount = BigDecimal.ZERO;
				chargeTrans.setChargeNetAmount(chargeTrans.getChargeValue().add(chargeTrans.getTaxAmount()));
				if (chargeTrans.getChargeMaster().getTaxMaster() != null) {
					chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
				}
				if (chargeTrans.getChargeFrequency().equals(LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE)) {
					totalFee = totalFee.add(chargeTrans.getChargeValue());
					sumOfNetCharge = sumOfNetCharge.add(chargeTrans.getChargeNetAmount());
				}

				listTrxCharges.add(chargeTrans);
			}
			deposito.setDepositoCharges(listTrxCharges);
		}

		deposito.setTotalFee(totalFee);
		int iAccrualBase = TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());

		/*
		 * deposito.setAccruedInterest(TransactionHelper.calculateAccruedInterest
		 * (deposito.getAmount(), deposito.getInterestRate(),
		 * deposito.getSecurity().getAccrualBase().getLookupCode(),
		 * deposito.getSecurity().getYearBase().getLookupCode(),
		 * deposito.getSecurity().getFrequency().getLookupId(),
		 * deposito.getDepositoBreak(), deposito.getMaturityDate(),
		 * deposito.getMaturityDate(), false, BigDecimal.ZERO,
		 * BigDecimal.ZERO));
		 */
		BigDecimal nominal = deposito.getAmount();
		BigDecimal netInterest = deposito.getTaxOnInterestNet();
		BigDecimal settlementAmount = nominal.add(netInterest).subtract(sumOfNetCharge);
		// deposito.setSettlementAmount(deposito.getTotalFee().add(deposito.getAmount()));
		deposito.setSettlementAmount(settlementAmount);
		deposito.setNoOfPayment(deposito.getInterestSchedules().size());
		
		//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
		Date firstCouponDate = null;
		try {
			firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		for (TdInterestSchedule tdInterestSchedule : deposito.getInterestSchedules()) {
			// tdInterestSchedule.setDays(TransactionHelper.calculateAccruedDays(tdInterestSchedule.getStartDate(),
			// tdInterestSchedule.getEndDate(), iAccrualBase));
			//tdInterestSchedule.setGrossInterest(TransactionHelper.calculateAccruedInterest(deposito.getAmount(), deposito.getInterestRate(), deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), tdInterestSchedule.getEndDate(), false, BigDecimal.ZERO, BigDecimal.ZERO));
			tdInterestSchedule.setGrossInterest(TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
					   deposito.getInterestRate(), 
					   deposito.getSecurity().getAccrualBase().getLookupCode(), 
					   deposito.getSecurity().getYearBase().getLookupCode(), 
					   deposito.getSecurity().getFrequency().getLookupId(), 
					   tdInterestSchedule.getStartDate(), 
					   tdInterestSchedule.getEndDate(), 
					   tdInterestSchedule.getEndDate(), 
					   firstCouponDate,
					   false, BigDecimal.ZERO, BigDecimal.ZERO, false));
			tdInterestSchedule.setDays(TransactionHelper.getAccruedDays());
			schedules.add(tdInterestSchedule);
		}

		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		if (deposito.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(deposito.getUdf(), new TypeReference<HashMap<String, String>>() {
				});
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
				}
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		}

		log.debug("============== panjang chargers " + deposito.getDepositoCharges().size());

		TdInterestSchedule interest = getPaymentDate(deposito.getDepositoKey().toString(), deposito.getDepositoBreak());
		if (interest != null) {
			deposito.setLastPaymentDate(interest.getStartDate());
			deposito.setNextPaymentDate(interest.getEndDate());
		}

		// kalo monthly end date kalo bukan monthly maturity date
		if (deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MONTHLY)) {
			//deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPorto(deposito.getLastPaymentDate(), deposito.getDepositoBreak(), deposito.getNextPaymentDate(), iAccrualBase));
			deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPortoNew(deposito.getLastPaymentDate(), deposito.getDepositoBreak(), firstCouponDate.getDate(), iAccrualBase));
		} else {
			//deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPorto(deposito.getEffectiveDate(), deposito.getDepositoBreak(), deposito.getMaturityDate(), iAccrualBase));
			deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPortoNew(deposito.getEffectiveDate(), deposito.getDepositoBreak(), firstCouponDate.getDate(), iAccrualBase));
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_BREAK));
		render("DepositoBreaks/entry.html", mode, deposito, schedules, udfs, origin, taskId);
	}

	public static void view(Long id, String from) {
		log.debug("view. id: " + id + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String origin = LookupConstants.CANCEL_DEPOSITO_BREAK_ORIGINATED;
		String taskId = "";
		log.debug(" ID MENDAPATKAN TRX = "+id);
		TdTransaction trx = depositoService.getTdTransaction(id);
		TdMaster deposito = depositoService.getMasterDeposito(trx.getDeposito().getDepositoKey());
		
		deposito.setExternalReference(trx.getExternalReference());
		
		deposito.setAmount(trx.getAmount());
		deposito.setInterestRate(trx.getInterestRate());
//		TdTransaction trx = depositoService.getTransactionDepositoByTemplate(deposito.getDepositoKey(), LookupConstants.DEPOSITO_TEMPLATE_BREAK);
		if (trx.getRemarksCorrection()!=null){
			deposito.setNeedCorrection(true);
			deposito.setRemarksCorrection(trx.getRemarksCorrection());
		}
		
		deposito.setTransactionKey(trx.getTransactionKey());
		log.debug("TAX KEYNYA = "+trx.getTaxOnInterestCode().getTaxKey());
		deposito.setTaxMaster(generalService.getTaxMaster(trx.getTaxOnInterestCode().getTaxKey()));
		
		// DEFAULT TEMPLATE
		deposito.setDepositoTemplate(trx.getDepositoTrxTemplate().getDepositoTemplate());
		deposito.setTransactionTemplate(trx.getDepositoTrxTemplate().getTransactionTemplate());
		deposito.setDepositoTemplateKey(trx.getDepositoTrxTemplate().getDepositoTemplateKey());
		
		deposito.setDepositoBreak(trx.getTransactionDate());
		deposito.setTaxOnInterestNet(trx.getTaxOnInterestNet());
		deposito.setAccruedInterest(trx.getTaxOnInterest());
		if(trx.getTransactionBatch() != null){
			CsTransaction trans = accountService.getTransactionCancelledNullByBatchKey(trx.getTransactionBatch().getTransactionBatchKey());
			if(trans != null){
				deposito.setAccruedInterest(trans.getAccruedInterest());
			}
		}
		
		BigDecimal amountTax = transactionService.calculateTax(deposito.getAccruedInterest(), deposito.getTaxMaster());
		deposito.setTaxAmount(amountTax);
		
		List<TdTransactionCharge> listTrxCharges = new ArrayList<TdTransactionCharge>();
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
		BigDecimal totalFee = BigDecimal.ZERO;
		BigDecimal sumOfNetCharge = BigDecimal.ZERO;
		if (trx!=null) {
			for (TdTransactionCharge chargeTrans : trx.getDepositoCharges()) {
				BigDecimal taxAmount = chargeTrans.getTaxAmount();
				if (taxAmount==null) taxAmount = BigDecimal.ZERO;
				chargeTrans.setChargeNetAmount(chargeTrans.getChargeValue().add(chargeTrans.getTaxAmount()));
				if (chargeTrans.getChargeMaster().getTaxMaster()!=null){
					chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
				}
				if (chargeTrans.getChargeFrequency().equals(LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE)){
					totalFee = totalFee.add(chargeTrans.getChargeValue());
					sumOfNetCharge = sumOfNetCharge.add(chargeTrans.getChargeNetAmount());
				}
				
				listTrxCharges.add(chargeTrans);
			}
			deposito.setDepositoCharges(listTrxCharges);
		}
		
		deposito.setTotalFee(totalFee);
		int iAccrualBase = TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());
		//deposito.setAccruedDays(TransactionHelper.calculateAccruedDays(deposito.getDepositoBreak(), deposito.getMaturityDate(),iAccrualBase));
		/*deposito.setAccruedInterest(TransactionHelper.calculateAccruedInterest(deposito.getAmount(), 
																			   deposito.getInterestRate(), 
																			   deposito.getSecurity().getAccrualBase().getLookupCode(), 
																			   deposito.getSecurity().getYearBase().getLookupCode(), 
																			   deposito.getSecurity().getFrequency().getLookupId(), 
																			   deposito.getDepositoBreak(), 
																			   deposito.getMaturityDate(), 
																			   deposito.getMaturityDate(), 
																			   false, BigDecimal.ZERO, BigDecimal.ZERO));*/
		BigDecimal nominal = deposito.getAmount();
		BigDecimal netInterest = deposito.getTaxOnInterestNet();
		BigDecimal settlementAmount = nominal.add(netInterest).subtract(sumOfNetCharge);
//		deposito.setSettlementAmount(deposito.getTotalFee().add(deposito.getAmount()));
		deposito.setSettlementAmount(settlementAmount);
		deposito.setNoOfPayment(deposito.getInterestSchedules().size());
		
		//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
		Date firstCouponDate = null;
		try {
			firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		for (TdInterestSchedule tdInterestSchedule : deposito.getInterestSchedules()) {
			//tdInterestSchedule.setDays(TransactionHelper.calculateAccruedDays(tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), iAccrualBase));
			/*tdInterestSchedule.setGrossInterest(TransactionHelper.calculateAccruedInterest(deposito.getAmount(), 
																						   deposito.getInterestRate(), 
																						   deposito.getSecurity().getAccrualBase().getLookupCode(), 
																						   deposito.getSecurity().getYearBase().getLookupCode(), 
																						   deposito.getSecurity().getFrequency().getLookupId(), 
																						   tdInterestSchedule.getStartDate(), 
																						   tdInterestSchedule.getEndDate(), 
																						   tdInterestSchedule.getEndDate(), 
																						   false, BigDecimal.ZERO, BigDecimal.ZERO));*/
			tdInterestSchedule.setGrossInterest(TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
					   deposito.getInterestRate(), 
					   deposito.getSecurity().getAccrualBase().getLookupCode(), 
					   deposito.getSecurity().getYearBase().getLookupCode(), 
					   deposito.getSecurity().getFrequency().getLookupId(), 
					   tdInterestSchedule.getStartDate(), 
					   tdInterestSchedule.getEndDate(), 
					   tdInterestSchedule.getEndDate(), 
					   firstCouponDate,
					   false, BigDecimal.ZERO, BigDecimal.ZERO, false));
			tdInterestSchedule.setDays(TransactionHelper.getAccruedDays());
			schedules.add(tdInterestSchedule);
		}
		
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		if (deposito.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(deposito.getUdf(), new TypeReference<HashMap<String, String>>() {});
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					if ( udf.getLookupGroup() != null ) {
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
		
		TdInterestSchedule interest;
		if(from != null && from.trim().equals(LookupConstants.INQUIRY_DEPOSITO_FULL_REDEEM)){
			interest = DepositoBreaks.getPaymentDateFullRedeem(deposito.getDepositoKey().toString());
		} else {
			interest = getPaymentDateBreak(deposito.getDepositoKey().toString(), deposito.getDepositoBreak());
		}
		
		if( interest != null){
			deposito.setLastPaymentDate(interest.getStartDate());
			deposito.setNextPaymentDate(interest.getEndDate());
		}
		
		if(deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MONTHLY)){
//			deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPorto(deposito.getLastPaymentDate(), deposito.getDepositoBreak(), deposito.getNextPaymentDate(), iAccrualBase));
			deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPortoNew(deposito.getLastPaymentDate(), deposito.getDepositoBreak(), firstCouponDate.getDate(), iAccrualBase));
		} else {
//			deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPorto(deposito.getEffectiveDate(), deposito.getDepositoBreak(), deposito.getMaturityDate(), iAccrualBase));
			deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPortoNew(deposito.getEffectiveDate(), deposito.getDepositoBreak(), firstCouponDate.getDate(), iAccrualBase));
		}
		
//		deposito.setTaxAmount(deposito.getAccruedInterest().subtract(deposito.getTaxOnInterestNet()));
		deposito.setStatus(trx.getStatus());
		deposito.setCancelDate(trx.getCancelDate());
		deposito.setRemarksCancel(trx.getRemarksCancel());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_INQUIRY));
		render("DepositoBreaks/entry.html", mode, from, deposito, schedules, udfs, origin, taskId);
	}

	@Check("transaction.canceldepositobreak")
	public static void save(TdMaster deposito, List<TdTransactionCharge> charges, List<TdInterestSchedule> schedules, List<GnUdfMaster> udfs, String mode) {
		log.debug("save. deposito: " + deposito + " charges: " + charges + " schedules: " + schedules + " udfs: " + udfs + " mode: " + mode);

		String taskId = "";
		String origin = LookupConstants.CANCEL_DEPOSITO_BREAK_ORIGINATED;

		if (deposito != null && charges != null) {
			deposito.getDepositoCharges().clear();
			deposito.getDepositoCharges().addAll(charges);
		}

		if (deposito != null && schedules != null) {
			deposito.getInterestSchedules().clear();
			deposito.getInterestSchedules().addAll(schedules);
		}

		if (deposito != null) {
			try {
				Map<String, String> data = new HashMap<String, String>();
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						data.put(udf.getFieldName(), udf.getValue());
						if (!udf.getLookupGroup().getLookupGroup().isEmpty()) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
					}
					String udfString = json.writeValueAsString(data);
					deposito.setUdf(udfString);
				}
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}

			Date currentDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			GnApplicationDate currentApplicationDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);

			// validate
			validation.required("Cancel Date is", deposito.getCancelDate());
			validation.required("Account No is", deposito.getAccount().getAccountNo());
			validation.required("Security Type is", deposito.getSecurity().getSecurityType().getSecurityType());
			validation.required("Security Code is", deposito.getSecurity().getSecurityId());
			validation.required("Deposito No is", deposito.getDepositoKey());
			validation.required("Nominal is", deposito.getAmount());
			validation.required("Interest Rate (gross) is", deposito.getInterestRate());
			validation.required("Tax Code (left) is", deposito.getTaxMaster().getTaxKey());
			if (deposito.getSecurity().getIsExpire() != null) {
				if (deposito.getSecurity().getIsExpire() == true) {
					if (fmtYYYYMMDD(deposito.getSecurity().getExpiredDate()).compareTo(fmtYYYYMMDD(currentDate)) < 0) {
						validation.addError("", "Security code " + deposito.getSecurity().getSecurityId() + " has been expired");
					}
				}
			}

			if (deposito.getCancelDate() != null) {
				if (deposito.getCancelDate().compareTo(currentApplicationDate.getCurrentBusinessDate()) > 0) {
					validation.addError("", ExceptionConstants.CANCELDATE_GREATER_APPLICATION_DATE);
				}
				if (deposito.getCancelDate().compareTo(deposito.getDepositoBreak()) < 0) {
					validation.addError("", ExceptionConstants.CANCELDATE_LESS_THAN_DEPOSITBREAK_DATE);
				}
				if (deposito.getCancelDate().compareTo(deposito.getMaturityDate()) > 0) {
					validation.addError("", ExceptionConstants.CANCELDATE_GREATER_THAN_MATURITY_DATE);
				}
			}

			if (validation.hasErrors()) {
				Map<String, String> data = new HashMap<String, String>();
				try {
					if (deposito.getUdf() != null) {
						data = json.readValue(deposito.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
					}
				} catch (Exception e) {
					log.debug(e.getMessage(), e);
				}
				// udfs =
				// generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						if (udfs != null) {
							udf.setValue(data.get(udf.getFieldName()));
							// START UDF FOR DROPDOWN
							// if (udf.getLookupGroup() != null) {
							if (!udf.getLookupGroup().getLookupGroup().isEmpty()) {
								udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
								udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
							}
							// END DROPDOWN
						}
					}
				}

				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_BREAK));
				render("DepositoBreaks/entry.html", mode, deposito, charges, schedules, udfs, origin, taskId);
			} else {
				serializerService.serialize(session.getId(), deposito.getDepositoKey(), deposito);
				confirming(deposito.getDepositoKey(), mode);
			}
		} else {
			list();
		}

	}

	@Check("transaction.canceldepositobreak")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		String taskId = "";
		boolean confirming = true;
		String origin = LookupConstants.CANCEL_DEPOSITO_BREAK_ORIGINATED;
		TdMaster deposito = serializerService.deserialize(session.getId(), id, TdMaster.class);
		Set<TdInterestSchedule> schedules = deposito.getInterestSchedules();
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		// TODO : CHANGE THIS
		log.debug("settlement3 " + deposito.getSettlementAmount());
		if (deposito.getUdf() != null) {
			try {
				if (deposito.getUdf() != null) {
					Map<String, String> data = json.readValue(deposito.getUdf(), new TypeReference<HashMap<String, String>>() {
					});
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						log.debug(">>> udf field name = " + udf.getFieldName());
						log.debug(">>> udf value = " + udf.getValue());
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
					}
				}
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		}
		log.debug("settlement4 " + deposito.getSettlementAmount());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_BREAK));
		render("DepositoBreaks/entry.html", mode, deposito, schedules, confirming, udfs, origin, taskId);
	}

	@Check("transaction.canceldepositobreak")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		String taskId = "";
		String origin = LookupConstants.CANCEL_DEPOSITO_BREAK_ORIGINATED;
		TdMaster deposito = serializerService.deserialize(session.getId(), id, TdMaster.class);
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
		for (TdInterestSchedule tdInterestSchedule : deposito.getInterestSchedules()) {
			schedules.add(tdInterestSchedule);
		}
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		if (deposito.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(deposito.getUdf(), new TypeReference<HashMap<String, String>>() {
				});
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					log.debug(">>> udf field name = " + udf.getFieldName());
					log.debug(">>> udf value = " + udf.getValue());
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
				}
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_BREAK));
		render("DepositoBreaks/entry.html", mode, deposito, schedules, udfs, origin, taskId);
	}

	@Check("transaction.canceldepositobreak")
	public static void confirm(TdMaster deposito, List<TdTransactionCharge> charges, List<TdInterestSchedule> schedules, List<GnUdfMaster> udfs, String mode) {
		log.debug("confirm. deposito: " + deposito + " charges: " + charges + " schedules: " + schedules + " udfs: " + udfs + " mode: " + mode);

		// logger.debug("charges "+charges.size());
		if (deposito!=null && charges !=null){
			deposito.getDepositoCharges().clear();
			deposito.getDepositoCharges().addAll(charges);
		}
		
		//logger.debug("deposito.getDepositoCharges() "+deposito.getDepositoCharges().size());
		if (deposito!=null && schedules !=null){
			deposito.getInterestSchedules().clear();
			deposito.getInterestSchedules().addAll(schedules);
		}
		
		if (udfs != null) {
			try {
				Map<String, String> data = new HashMap<String, String>();
				for (GnUdfMaster udf : udfs) {
					data.put(udf.getFieldName(), udf.getValue());
				}
				
				String udfString = json.writeValueAsString(data);
				deposito.setUdf(udfString);
			} catch (IOException e) {
				log.debug(e.getMessage(),e);
			}
		}
		boolean confirming = true;
		try {
			/*List<GnCalendar> holidays = generalService.listHolidays(UIConstants.SIMIAN_BANK_ID);
			for (GnCalendar holiday : holidays){
				if (holiday.getId().getCalendarDate().getTime() == (deposito.getPlacementDate().getTime())){
					throw new MedallionException(ExceptionConstants.DATE_IS_HOLIDAY, "deposito.placementDate");
				}
				
				if (holiday.getId().getCalendarDate().getTime() == (deposito.getMaturityDate().getTime())){
					throw new MedallionException(ExceptionConstants.DATE_IS_HOLIDAY, "transaction.maturityDate");
				}
			}*/
			
			if (deposito.getDepositoKey() != null) {
				TdMaster currentDeposito = depositoService.getMasterDeposito(deposito.getDepositoKey());
				if ((currentDeposito.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_UPDATED))) {
					throw new MedallionException(ExceptionConstants.DATA_CANT_EDITED, "");
				}
			}
			
//			deposito.setEffectiveDate(deposito.getPlacementDate());
			deposito.setDepositoTemplate(new GnLookup(LookupConstants.DEPOSITO_TEMPLATE_BREAK));
			log.debug("deposito.getDepositoCharges()2 "+deposito.getDepositoCharges().size());
			TdMaster trx = depositoService.createDeposito(MenuConstants.TD_DEPOSITO_CANCEL_BREAK, deposito, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
//			TdMaster trx = new TdMaster();
			/*if (trx.getDepositoKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("deposito.confirmed.successful", trx.getDepositoNo()));
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_BREAK));
				render("DepositoBreaks/detail.html", trx, mode, confirming);
			}*/
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "success");
			log.debug("mode "+mode);
			result.put("message", Messages.get("deposito.cancel.confirmed", trx.getDepositoNo()));
			
			renderJSON(result);
		} catch (MedallionException ex) {
			renderJSON(Formatter.resultError(ex));
		}catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void approval(String taskId, Long keyId, String from, String operation, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			String origin = LookupConstants.CANCEL_DEPOSITO_BREAK_ORIGINATED;
			TdMaster deposito = depositoService.getMasterDeposito(keyId);
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			TdMaster depositoTamp = json.readValue(maintenanceLog.getNewData(), TdMaster.class);
			// TdTransaction trx =
			// depositoService.getTransactionDepositoByTemplate(deposito.getDepositoKey(),
			// LookupConstants.DEPOSITO_TEMPLATE_BREAK);
			
			//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
			Date firstCouponDate = null;
			try {
				firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			TdTransaction trx = depositoService.getTdTransaction(depositoTamp.getTransactionKey());

			deposito.setAmount(trx.getAmount());
			deposito.setInterestRate(trx.getInterestRate());
			// deposito.setDepositoBreak(trx.getTransactionDate());
			// DEFAULT TEMPLATE
			deposito.setDepositoTemplate(trx.getDepositoTrxTemplate().getDepositoTemplate());
			deposito.setTransactionTemplate(trx.getDepositoTrxTemplate().getTransactionTemplate());
			deposito.setDepositoTemplateKey(trx.getDepositoTrxTemplate().getDepositoTemplateKey());
			deposito.setCancelDate(depositoTamp.getCancelDate());
			deposito.setRemarksCancel(depositoTamp.getRemarksCancel());

			deposito.setTransactionKey(trx.getTransactionKey());
			deposito.setTaxMaster(generalService.getTaxMaster(trx.getTaxOnInterestCode().getTaxKey()));
			deposito.setDepositoBreak(trx.getTransactionDate());
			deposito.setTaxOnInterestNet(trx.getTaxOnInterestNet());
			deposito.setAccruedInterest(trx.getTaxOnInterest());

			BigDecimal amountTax = transactionService.calculateTax(deposito.getAccruedInterest(), deposito.getTaxMaster());
			deposito.setTaxAmount(amountTax);
			// logger.debug("trx "+trx.getTransactionKey());

			// deposito.setTaxMaster(generalService.getTaxMaster(trx.getTaxOnInterestCode().getTaxKey()));

			List<TdTransactionCharge> listTrxCharges = new ArrayList<TdTransactionCharge>();
			List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
			BigDecimal totalFee = BigDecimal.ZERO;
			BigDecimal sumOfNetCharge = BigDecimal.ZERO;
			if (trx != null) {
				for (TdTransactionCharge chargeTrans : trx.getDepositoCharges()) {
					BigDecimal taxAmount = chargeTrans.getTaxAmount();
					if (taxAmount == null)
						taxAmount = BigDecimal.ZERO;
					chargeTrans.setChargeNetAmount(chargeTrans.getChargeValue().add(chargeTrans.getTaxAmount()));
					if (chargeTrans.getChargeMaster().getTaxMaster() != null) {
						chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
					}
					if (chargeTrans.getChargeFrequency().equals(LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE)) {
						totalFee = totalFee.add(chargeTrans.getChargeValue());
						sumOfNetCharge = sumOfNetCharge.add(chargeTrans.getChargeNetAmount());
					}

					listTrxCharges.add(chargeTrans);
				}
				deposito.setDepositoCharges(listTrxCharges);
			}

			deposito.setTotalFee(totalFee);
			deposito.setSecurity(securityService.getSecurity(deposito.getSecurity().getSecurityKey()));
			// int iAccrualBase =
			// TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());
			// logger.debug("======== isi "+deposito.getDepositoBreak()+" : "+deposito.getMaturityDate()+" : "+iAccrualBase);
			deposito.setAccruedDays(depositoTamp.getAccruedDays());
			// deposito.setAccruedDays(TransactionHelper.calculateAccruedDays(deposito.getDepositoBreak(),
			// deposito.getMaturityDate(),iAccrualBase));
			/*
			 * deposito.setAccruedInterest(TransactionHelper.
			 * calculateAccruedInterest(deposito.getAmount(),
			 * deposito.getInterestRate(),
			 * deposito.getSecurity().getAccrualBase().getLookupCode(),
			 * deposito.getSecurity().getYearBase().getLookupCode(),
			 * deposito.getSecurity().getFrequency().getLookupId(),
			 * deposito.getDepositoBreak(), deposito.getMaturityDate(),
			 * deposito.getMaturityDate(), false, BigDecimal.ZERO,
			 * BigDecimal.ZERO));
			 */
			BigDecimal nominal = deposito.getAmount();
			BigDecimal netInterest = deposito.getTaxOnInterestNet();
			BigDecimal settlementAmount = nominal.add(netInterest).subtract(sumOfNetCharge);
			// deposito.setSettlementAmount(deposito.getTotalFee().add(deposito.getAmount()));
			deposito.setSettlementAmount(settlementAmount);
			deposito.setNoOfPayment(deposito.getInterestSchedules().size());

			for (TdInterestSchedule tdInterestSchedule : deposito.getInterestSchedules()) {
				// tdInterestSchedule.setDays(TransactionHelper.calculateAccruedDays(tdInterestSchedule.getStartDate(),
				// tdInterestSchedule.getEndDate(), iAccrualBase));
				//tdInterestSchedule.setGrossInterest(TransactionHelper.calculateAccruedInterest(deposito.getAmount(), deposito.getInterestRate(), deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), tdInterestSchedule.getEndDate(), false, BigDecimal.ZERO, BigDecimal.ZERO));
				tdInterestSchedule.setGrossInterest(TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
						   deposito.getInterestRate(), 
						   deposito.getSecurity().getAccrualBase().getLookupCode(), 
						   deposito.getSecurity().getYearBase().getLookupCode(), 
						   deposito.getSecurity().getFrequency().getLookupId(), 
						   tdInterestSchedule.getStartDate(), 
						   tdInterestSchedule.getEndDate(), 
						   tdInterestSchedule.getEndDate(),
						   firstCouponDate,
						   false, BigDecimal.ZERO, BigDecimal.ZERO, false));
				tdInterestSchedule.setDays(TransactionHelper.getAccruedDays());
				schedules.add(tdInterestSchedule);
			}

			List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
			if (deposito.getUdf() != null) {
				try {
					Map<String, String> data = json.readValue(deposito.getUdf(), new TypeReference<HashMap<String, String>>() {
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

			deposito.setTaxAmount(deposito.getAccruedInterest().subtract(deposito.getTaxOnInterestNet()));

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
				render("DepositoBreaks/approval.html", mode, deposito, schedules, udfs, taskId, from, maintenanceLogKey, operation, keyId, origin);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
				render("DepositoBreaks/approval.html", mode, deposito, schedules, udfs, taskId, from, maintenanceLogKey, operation, keyId, origin);
			}

		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TdMaster deposito = depositoService.approveCancelDepositoBreak(MenuConstants.TD_DEPOSITO_CANCEL_BREAK, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, session.get(UIConstants.SESSION_USER_KEY));
			result.put("status", "success");
			result.put("message", Messages.get("deposito.cancelled.approved", deposito.getDepositoNo()));
		} catch (MedallionException ex) {
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if (ex != null && ex.getParams() != null) {
				for (Object paramItem : ex.getParams()) {
					log.debug("PARAMS ITEM = " + paramItem);
					params.add(Messages.get(paramItem));
				}

				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			} else {
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}

		}catch (Exception e) {
			result.put("status", "error");
			result.put("error",e.getMessage());
		}
		renderJSON(result);
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TdMaster deposito = null;

			deposito = depositoService.approveCancelDepositoBreak(MenuConstants.TD_DEPOSITO_CANCEL_BREAK, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, session.get(UIConstants.SESSION_USER_KEY));

			result.put("status", "success");
			result.put("message", Messages.get("deposito.cancelled.rejected", deposito.getDepositoNo()));
		} catch (MedallionException ex) {
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if (ex != null && ex.getParams() != null) {
				for (Object paramItem : ex.getParams()) {
					log.debug("PARAMS ITEM = " + paramItem);
					params.add(Messages.get(paramItem));
				}

				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			} else {
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
		}catch (Exception e) {
			result.put("status", "error");
			result.put("error",e.getMessage());
		}
		renderJSON(result);
	}

	public static void populatePaymentSchedule(Date placementDate, Date maturityDate, BigDecimal nominal, BigDecimal interestRate, String accrualBase, String yearBase, Integer totalCoupon, boolean considerHoliday, String freqSecurity) {
		log.debug("populatePaymentSchedule. placementDate: " + placementDate + " maturityDate: " + maturityDate + " nominal: " + nominal + " interestRate: " + interestRate + " accrualBase: " + accrualBase + " yearBase: " + yearBase + " totalCoupon: " + totalCoupon + " considerHoliday: " + considerHoliday + " freqSecurity: " + freqSecurity);

		String frequency = LookupConstants.FREQUENCY_MONTH;
		List<TdInterestSchedule> schedules = depositoService.populatePaymentSchedule(placementDate, maturityDate, nominal, interestRate, accrualBase, yearBase, frequency, totalCoupon, considerHoliday, freqSecurity);

		render("DepositoBreaks/gridPaymentSchedules.html", schedules);
	}

	public static void paymentDate(String deposito, Date date) {
		log.debug("paymentDate. deposito: " + deposito + " date: " + date);

		String schedule = null;
		try {
			String fmtDate = fmtDDMMMYYYYY(date);
			log.debug("isi date " + deposito + " : " + date + " : " + fmtDate);
			TdInterestSchedule interest = depositoService.getInterestScheduleByDate(Long.parseLong(deposito), fmtDate);
			schedule = json.writeValueAsString(interest);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		renderJSON(schedule);
	}

	public static TdInterestSchedule getPaymentDate(String deposito, Date date) {
		log.debug("getPaymentDate. deposito: " + deposito + " date: " + date);

		TdInterestSchedule interest = null;
		try {
			String fmtDate = fmtDDMMMYYYYY(date);
			interest = depositoService.getInterestScheduleByDate(Long.parseLong(deposito), fmtDate);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		return interest;
	}

	public static TdInterestSchedule getPaymentDateBreak(String deposito, Date date) {
		log.debug("getPaymentDateBreak. deposito: " + deposito + " date: " + date);

		TdInterestSchedule interest = null;
		try {
			String fmtDate = fmtDDMMMYYYYY(date);
			interest = depositoService.getInterestScheduleByDateBreak(Long.parseLong(deposito), fmtDate);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		return interest;
	}
}