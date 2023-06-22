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
import org.codehaus.jackson.JsonGenerationException;
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
public class DepositoBreaks extends Registry {
	private static Logger log = Logger.getLogger(DepositoBreaks.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);

		renderArgs.put("depositoTemplateBreak", LookupConstants.DEPOSITO_TEMPLATE_BREAK);
		renderArgs.put("currDefault", LookupConstants.__CURRENCY_IDR);
	}

	@Before(only = { "entry", "edit", "view", "save", "confirming", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> yesNo = UIHelper.yesNoOperators();
		renderArgs.put("yesNo", yesNo);

		GnLookup paymentFreqMonthly = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MONTHLY);
		GnLookup paymentFreqMaturity = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MATURITY);
		renderArgs.put("chargeFreqT", LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE);
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

		renderArgs.put("depositoBreakOriginated", LookupConstants.DEPOSITO_BREAK_ORIGINATED);
		renderArgs.put("depositoTemplateBreak", LookupConstants.DEPOSITO_TEMPLATE_BREAK);
		renderArgs.put("depositoInquiryOriginated", LookupConstants.INQUIRY_DEPOSITO_ORIGINATED);
		renderArgs.put("depositoInquiryFullRedeem", LookupConstants.INQUIRY_DEPOSITO_FULL_REDEEM);

		GnSystemParameter currencyTaxParam = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX);
		GnSystemParameter currencyTaxRoundParam = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX_ROUND);
		GnSystemParameter currencyTaxRoundTypeParam = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX_ROUND_TYPE);

		renderArgs.put("currencyTax", currencyTaxParam.getValue());
		renderArgs.put("currencyTaxRound", currencyTaxRoundParam.getValue());
		renderArgs.put("currencyTaxRoundType", currencyTaxRoundTypeParam.getValue());

	}

	@Check("transaction.depositobreak")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_BREAK));
		render();
	}

	public static void paging(Paging page, DepositoSearchParameter param) {
		log.debug("paging. page: " + page + " param: " + param);

		page.addParams("tt.transactionDate", page.GREATEQUAL, param.dateFrom);
		page.addParams("tt.transactionDate", page.LESSEQUAL, param.dateTo);
		if (!param.customerCode.isEmpty())
			page.addParams("a.customer.customerKey", page.EQUAL, Long.parseLong(param.customerCode));
		page.addParams("st.securityType.securityType", page.EQUAL, param.securityType);
		if (!param.securityCode.isEmpty())
			page.addParams("s.securityKey", page.EQUAL, Long.parseLong(param.securityCode));
		page.addParams("dt.depositoTemplate.lookupId", page.EQUAL, LookupConstants.DEPOSITO_TEMPLATE_BREAK);

		log.debug("param.depositoNoOperator " + param.depositoNoOperator + " : " + param.depositoNo + " : " + UIHelper.withOperator(param.depositoNo, param.depositoNoOperator));
		page.addParams("dp.depositoNo", param.depositoNoOperator, UIHelper.withOperator(param.depositoNo, param.depositoNoOperator));
		page.addParams("tt.status", page.EQUAL, LookupConstants.__RECORD_STATUS_REJECTED);
		page.addParams("tt.recordStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_NEED_REVISION);
		page.addParams(Helper.searchAll("(dp.depositoNo||a.accountNo||to_char(tt.transactionDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "a.name||s.securityId||dp.amount||tt.interestRate||to_char(dp.maturityDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		page = depositoService.pagingDepositoBreak(page);
		renderJSON(page);
	}

	@Check("transaction.depositobreak")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		String origin = LookupConstants.DEPOSITO_BREAK_ORIGINATED;
		TdMaster deposito = new TdMaster();
		String taskId = "";

		deposito.setDepositoBreak(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));

		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
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
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_BREAK));
		boolean isreload = false;
		render("DepositoBreaks/entry.html", mode, deposito, taskId, udfs, origin, isreload);
	}

	@Check("transaction.depositobreak")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		String origin = LookupConstants.DEPOSITO_BREAK_ORIGINATED;
		String taskId = "";
		TdTransaction trx = depositoService.getTdTransaction(id);
		TdMaster deposito = depositoService.getMasterDeposito(trx.getDeposito().getDepositoKey());
		// TdTransaction trx =
		// depositoService.getTransactionDepositoByTemplate(deposito.getDepositoKey(),
		// LookupConstants.DEPOSITO_TEMPLATE_BREAK);
		deposito.setInterestRate(trx.getInterestRate());
		if (trx.getRemarksCorrection() != null) {
			deposito.setNeedCorrection(true);
			deposito.setRemarksCorrection(trx.getRemarksCorrection());
		}

		deposito.setTransactionKey(trx.getTransactionKey());

		if (deposito.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_NEW) || deposito.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_UPDATED)) {
			mode = UIConstants.DISPLAY_MODE_VIEW;
		}

		if (trx.getTaxOnInterestCode() != null) {
			deposito.setTaxMaster(generalService.getTaxMaster(trx.getTaxOnInterestCode().getTaxKey()));
		} else {
			deposito.setTaxMaster(null);
		}

		// DEFAULT TEMPLATE
		deposito.setDepositoTemplate(trx.getDepositoTrxTemplate().getDepositoTemplate());
		deposito.setTransactionTemplate(trx.getDepositoTrxTemplate().getTransactionTemplate());
		deposito.setDepositoTemplateKey(trx.getDepositoTrxTemplate().getDepositoTemplateKey());

		deposito.setDepositoBreak(trx.getTransactionDate());
		deposito.setTaxOnInterestNet(trx.getTaxOnInterestNet());
		deposito.setAccruedInterest(trx.getTaxOnInterest());

		List<TdTransactionCharge> listTrxCharges = new ArrayList<TdTransactionCharge>();
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
		BigDecimal totalFee = BigDecimal.ZERO;
		BigDecimal sumOfNetCharge = BigDecimal.ZERO;
		if (trx != null) {
			for (TdTransactionCharge chargeTrans : trx.getDepositoCharges()) {
				BigDecimal taxAmount = chargeTrans.getTaxAmount();
				// if (taxAmount==null) taxAmount = BigDecimal.ZERO;
				if (taxAmount == null) {
					taxAmount = BigDecimal.ZERO;
					chargeTrans.setTaxAmount(taxAmount);
				}
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
		// deposito.setAccruedDays(TransactionHelper.calculateAccruedDays(deposito.getDepositoBreak(),
		// deposito.getMaturityDate(),iAccrualBase));
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
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
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

		TdInterestSchedule interest = getPaymentDate(deposito.getDepositoKey().toString(), deposito.getDepositoBreak());
		if (interest != null) {
			deposito.setLastPaymentDate(interest.getStartDate());
			deposito.setNextPaymentDate(interest.getEndDate());
		}
		if (deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MONTHLY)) {
			//deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPorto(deposito.getLastPaymentDate(), deposito.getDepositoBreak(), deposito.getNextPaymentDate(), iAccrualBase));
			deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPortoNew(deposito.getLastPaymentDate(), deposito.getDepositoBreak(), firstCouponDate.getDate(), iAccrualBase));
		} else {
			//deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPorto(deposito.getEffectiveDate(), deposito.getDepositoBreak(), deposito.getMaturityDate(), iAccrualBase));
			deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPortoNew(deposito.getEffectiveDate(), deposito.getDepositoBreak(), firstCouponDate.getDate(), iAccrualBase));
		}
		BigDecimal iRate = new BigDecimal(0);
		if (deposito.getAccruedInterest() != null)
			iRate = deposito.getAccruedInterest();

		BigDecimal nInterest = new BigDecimal(0);
		if (deposito.getTaxOnInterestNet() != null)
			nInterest = deposito.getTaxOnInterestNet();

		deposito.setTaxAmount(iRate.subtract(nInterest));
		/*
		 * deposito.setTaxAmount(deposito.getAccruedInterest().subtract(deposito.
		 * getTaxOnInterestNet()));
		 * logger.debug("tax amount "+deposito.getTaxAmount());
		 */
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_BREAK));
		boolean isreload = true;
		render("DepositoBreaks/entry.html", mode, deposito, schedules, udfs, taskId, origin, isreload);
	}

	public static void view(Long id, String from) {
		log.debug("view. id: " + id + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String origin = LookupConstants.DEPOSITO_BREAK_ORIGINATED;
		String taskId = "";
		TdTransaction trx = depositoService.getTdTransaction(id);
		TdMaster deposito = depositoService.getMasterDeposito(trx.getDeposito().getDepositoKey());
		deposito.setAmount(trx.getAmount());
		deposito.setInterestRate(trx.getInterestRate());
//		TdTransaction trx = depositoService.getTransactionDepositoByTemplate(deposito.getDepositoKey(), LookupConstants.DEPOSITO_TEMPLATE_BREAK);
		if (trx.getRemarksCorrection()!=null){
			deposito.setNeedCorrection(true);
			deposito.setRemarksCorrection(trx.getRemarksCorrection());
		}
		
		deposito.setTransactionKey(trx.getTransactionKey());
		
		if(trx.getTaxOnInterestCode() != null){
			deposito.setTaxMaster(generalService.getTaxMaster(trx.getTaxOnInterestCode().getTaxKey()));
		}else{
			deposito.setTaxMaster(null);
		}
		
		// DEFAULT TEMPLATE
		deposito.setDepositoTemplate(trx.getDepositoTrxTemplate().getDepositoTemplate());
		deposito.setTransactionTemplate(trx.getDepositoTrxTemplate().getTransactionTemplate());
		deposito.setDepositoTemplateKey(trx.getDepositoTrxTemplate().getDepositoTemplateKey());
		
		deposito.setDepositoBreak(trx.getTransactionDate());
		deposito.setTaxOnInterestNet(trx.getTaxOnInterestNet());
		deposito.setAccruedInterest(trx.getTaxOnInterest());
		
		/*BigDecimal amountTax = transactionService.calculateTax(deposito.getAccruedInterest(), deposito.getTaxMaster());
		deposito.setTaxAmount(amountTax);*/
		
		List<TdTransactionCharge> listTrxCharges = new ArrayList<TdTransactionCharge>();
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
		BigDecimal totalFee = BigDecimal.ZERO;
		BigDecimal sumOfNetCharge = BigDecimal.ZERO;
		if (trx!=null) {
			for (TdTransactionCharge chargeTrans : trx.getDepositoCharges()) {
				BigDecimal taxAmount = chargeTrans.getTaxAmount();
//				if (taxAmount==null) taxAmount = BigDecimal.ZERO;
				if (taxAmount==null) {
					taxAmount = BigDecimal.ZERO;
					chargeTrans.setTaxAmount(taxAmount);
				}
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
		
		BigDecimal nominal = deposito.getAmount();
		BigDecimal netInterest = deposito.getTaxOnInterestNet();
		BigDecimal settlementAmount = nominal.add(netInterest).subtract(sumOfNetCharge);//		deposito.setSettlementAmount(deposito.getTotalFee().add(deposito.getAmount()));
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
				if (udfs!=null){
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						if ( udf.getLookupGroup() != null ) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						} 
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
			interest = getPaymentDateFullRedeem(deposito.getDepositoKey().toString());
		} else {
			interest = getPaymentDate(deposito.getDepositoKey().toString(), deposito.getDepositoBreak());
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
		BigDecimal iRate = new BigDecimal(0);
		if(deposito.getAccruedInterest()!=null) iRate = deposito.getAccruedInterest();
		
		BigDecimal nInterest = new BigDecimal(0);
		if(deposito.getTaxOnInterestNet() != null) nInterest = deposito.getTaxOnInterestNet();
		
		deposito.setTaxAmount(iRate.subtract(nInterest));
//		deposito.setTaxAmount(deposito.getAccruedInterest().subtract(deposito.getTaxOnInterestNet()));
		deposito.setStatus(trx.getStatus());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_INQUIRY));
		render("DepositoBreaks/entry.html", mode, from, deposito, schedules, udfs, taskId, origin);
	}

	public static void save(TdMaster deposito, List<TdTransactionCharge> charges, List<TdInterestSchedule> schedules, List<GnUdfMaster> udfs, String mode, boolean isreload) {
		log.debug("save. deposito: " + deposito + " charges: " + charges + " schedules: " + schedules + " udfs: " + udfs + " mode: " + mode + " isreload: " + isreload);

		String origin = LookupConstants.DEPOSITO_BREAK_ORIGINATED;

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

			// validate
			validation.required("Break Date is", deposito.getDepositoBreak());
			validation.required("Account No is", deposito.getAccount().getAccountNo());
			validation.required("Security Type is", deposito.getSecurity().getSecurityType().getSecurityType());
			validation.required("Security Code is", deposito.getSecurity().getSecurityId());
			validation.required("Deposito No is", deposito.getDepositoKey());
			validation.required("Nominal is", deposito.getAmount());
			validation.required("Interest Rate (gross) is", deposito.getInterestRate());
			validation.required("Tax Code (left) is", deposito.getTaxMaster().getTaxKey());

			if (deposito.getDepositoBreak() != null) {
				if (deposito.getDepositoBreak().compareTo(currentDate) > 0) {
					validation.addError("", "Break Date can't greater than application date");
				}

				if (deposito.getMaturityDate() != null) {
					if (deposito.getDepositoBreak().compareTo(deposito.getMaturityDate()) > 0) {
						validation.addError("", "Break Date can't greater than Maturity date");
					}
				}

				if (deposito.getEffectiveDate() != null) {
					if (deposito.getDepositoBreak().compareTo(deposito.getEffectiveDate()) < 0) {
						validation.addError("", "Break Date can't less than Effective date");
					}
				}
			}
			if (deposito.getSecurity().getIsExpire() != null) {
				if (deposito.getSecurity().getIsExpire() == true) {
					if (fmtYYYYMMDD(deposito.getSecurity().getExpiredDate()).compareTo(fmtYYYYMMDD(currentDate)) < 0) {
						validation.addError("", "Security code " + deposito.getSecurity().getSecurityId() + " has been expired");
					}
				}
			}

			if (deposito.getAccount().getBlocked() != null) {
				if (deposito.getAccount().getBlocked() == true) {
					validation.addError("", Messages.get(ExceptionConstants.ACCOUNT_BLOCKED, deposito.getAccount().getAccountNo()));
				}
			}

			if (deposito.getLastPaymentDate() != null) {
				GnLookup paymentFreqMonthly = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MONTHLY);
				if (deposito.getInterestFrequency().getLookupId() != null) {
					if (deposito.getInterestFrequency().getLookupId().trim().equals(paymentFreqMonthly.getLookupId().trim())) {
						if (deposito.getDepositoBreak().compareTo(deposito.getLastPaymentDate()) < 0) {
							validation.addError("", "Break Date can't less than Start date");
						}
					}
				}
			}
			if (deposito.getNextPaymentDate() != null) {
				GnLookup paymentFreqMonthly = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MONTHLY);
				if (deposito.getInterestFrequency().getLookupId() != null) {
					if (deposito.getInterestFrequency().getLookupId().trim().equals(paymentFreqMonthly.getLookupId().trim())) {
						if (deposito.getDepositoBreak().compareTo(deposito.getNextPaymentDate()) > 0) {
							validation.addError("", "Break Date can't greater than End date");
						}
					}
				}
			}

			if (!deposito.getSecurity().getCurrency().getCurrencyCode().equals(deposito.getBankAccount().getCurrency().getCurrencyCode())) {
				validation.addError("", Messages.get(ExceptionConstants.BANK_ACCOUNT_CURRENCY_SHOULD_SAME, deposito.getSecurity().getCurrency().getCurrencyCode()));
			}
			for (TdTransactionCharge charge : deposito.getDepositoCharges()) {
				if (charge.getChargeWaived() != null) {
					if (charge.getChargeWaived()) {
						charge.setChargeValue(BigDecimal.ZERO);
						charge.setTaxAmount(BigDecimal.ZERO);
					}
				}
			}
			if (validation.hasErrors()) {
				Map<String, String> data = new HashMap<String, String>();
				try {
					if (deposito.getUdf() != null)
						data = json.readValue(deposito.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
				} catch (JsonParseException e) {
					log.error(e.getMessage(), e);
				} catch (JsonMappingException e) {
					log.error(e.getMessage(), e);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}

				// udfs =
				// generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);

				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
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
				String taskId = "";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_BREAK));
				render("DepositoBreaks/entry.html", mode, deposito, charges, schedules, udfs, origin, taskId, isreload);
			} else {

				serializerService.serialize(session.getId(), deposito.getDepositoKey(), deposito);
				confirming(deposito.getDepositoKey(), mode, isreload);
			}
		} else {
			entry();
		}
	}

	public static void confirming(Long id, String mode, boolean isreload) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isreload: " + isreload);

		boolean confirming = true;
		String origin = LookupConstants.DEPOSITO_BREAK_ORIGINATED;
		TdMaster deposito = serializerService.deserialize(session.getId(), id, TdMaster.class);
		Set<TdInterestSchedule> schedules = deposito.getInterestSchedules();
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		// TODO : CHANGE THIS

		if (deposito.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(deposito.getUdf(), new TypeReference<HashMap<String, String>>() {
				});
				if (udfs != null) {
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
			} catch (JsonParseException e) {
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		String taskId = "";
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_BREAK));
		render("DepositoBreaks/entry.html", mode, deposito, schedules, confirming, udfs, origin, taskId, isreload);
	}

	@Check("transaction.depositobreak")
	public static void back(Long id, String mode, boolean isreload) {
		log.debug("back. id: " + id + " mode: " + mode + " isreload: " + isreload);

		String origin = LookupConstants.DEPOSITO_BREAK_ORIGINATED;
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
				if (udfs != null) {
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
			} catch (JsonParseException e) {
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		String taskId = "";
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_BREAK));
		render("DepositoBreaks/entry.html", mode, deposito, schedules, udfs, origin, taskId, isreload);
	}

	public static void confirm(TdMaster deposito, List<TdTransactionCharge> charges, List<TdInterestSchedule> schedules, List<GnUdfMaster> udfs, String mode, boolean isreload) {
		log.debug("confirm. deposito: " + deposito + " charges: " + charges + " schedules: " + schedules + " udfs: " + udfs + " mode: " + mode + " isreload: " + isreload);

		if (deposito!=null && charges !=null){
			deposito.getDepositoCharges().clear();
			deposito.getDepositoCharges().addAll(charges);
		}
		
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
		
		try {
			
			if (deposito.getDepositoKey() != null) {
				TdMaster currentDeposito = depositoService.getMasterDeposito(deposito.getDepositoKey());
				if ((currentDeposito.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_UPDATED))) {
					throw new MedallionException(ExceptionConstants.DATA_CANT_EDITED, "");
				}
			}
			
			deposito.setDepositoTemplate(new GnLookup(LookupConstants.DEPOSITO_TEMPLATE_BREAK));
			
			for (TdTransactionCharge charge : deposito.getDepositoCharges()) {
				if(charge.getChargeWaived()==null) charge.setChargeWaived(false);
			}
			TdMaster trx = depositoService.createDeposito(MenuConstants.TD_DEPOSITO_BREAK, deposito, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));

			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "success");
			
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)){
				result.put("message", Messages.get("deposito.confirmed.successful", trx.getDepositoNo()));
			} else {
				result.put("message", Messages.get("deposito.confirmed.successful.edit", trx.getDepositoNo()));
			}
			
			renderJSON(result);
		} catch (MedallionException ex) {
			renderJSON(Formatter.resultError(ex));
		}catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void approval(String taskId, Long keyId, String from, String operation, String processDefinition, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " processDefinition: " + processDefinition + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			String origin = LookupConstants.DEPOSITO_BREAK_ORIGINATED;
			TdMaster deposito = depositoService.getMasterDeposito(keyId);
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			TdMaster depositoTamp = json.readValue(maintenanceLog.getNewData(), TdMaster.class);
			// TdTransaction trx =
			// depositoService.getTransactionDepositoByTemplate(deposito.getDepositoKey(),
			// LookupConstants.DEPOSITO_TEMPLATE_BREAK);
			TdTransaction trx = depositoService.getTdTransaction(depositoTamp.getTransactionKey());

			deposito.setAmount(trx.getAmount());
			deposito.setInterestRate(trx.getInterestRate());
			// deposito.setDepositoBreak(trx.getTransactionDate());
			// DEFAULT TEMPLATE
			deposito.setDepositoTemplate(trx.getDepositoTrxTemplate().getDepositoTemplate());
			deposito.setTransactionTemplate(trx.getDepositoTrxTemplate().getTransactionTemplate());
			deposito.setDepositoTemplateKey(trx.getDepositoTrxTemplate().getDepositoTemplateKey());

			deposito.setTransactionKey(trx.getTransactionKey());
			deposito.setTaxMaster(generalService.getTaxMaster(trx.getTaxOnInterestCode().getTaxKey()));
			deposito.setDepositoBreak(trx.getTransactionDate());
			deposito.setTaxOnInterestNet(trx.getTaxOnInterestNet());
			deposito.setAccruedInterest(trx.getTaxOnInterest());

			/*
			 * BigDecimal amountTax =
			 * transactionService.calculateTax(deposito.getAccruedInterest(),
			 * deposito.getTaxMaster()); deposito.setTaxAmount(amountTax);
			 */

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
			int iAccrualBase = TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());
			log.debug("======== isi " + deposito.getDepositoBreak() + " : " + deposito.getMaturityDate() + " : " + iAccrualBase);
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
					if (udfs != null) {
						for (GnUdfMaster udf : udfs) {
							udf.setValue(data.get(udf.getFieldName()));
							if (udf.getLookupGroup() != null) {
								udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
								udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
							}
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

			// deposito.setTaxAmount(deposito.getAccruedInterest().subtract(deposito.getTaxOnInterestNet()));
			TdInterestSchedule interest = getPaymentDate(deposito.getDepositoKey().toString(), deposito.getDepositoBreak());
			if (interest != null) {
				deposito.setLastPaymentDate(interest.getStartDate());
				deposito.setNextPaymentDate(interest.getEndDate());
			}
			if (deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MONTHLY)) {
				//deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPorto(deposito.getLastPaymentDate(), deposito.getDepositoBreak(), deposito.getNextPaymentDate(), iAccrualBase));
				deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPortoNew(deposito.getLastPaymentDate(), deposito.getDepositoBreak(), firstCouponDate.getDate(), iAccrualBase));
			} else {
				//deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPorto(deposito.getEffectiveDate(), deposito.getDepositoBreak(), deposito.getMaturityDate(), iAccrualBase));
				deposito.setAccruedDays(TransactionHelper.calculateAccruedDaysPortoNew(deposito.getEffectiveDate(), deposito.getDepositoBreak(), firstCouponDate.getDate(), iAccrualBase));
			}
			boolean isreload = true;
			deposito.setTaxAmount(depositoTamp.getTaxAmount());
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
				render("DepositoBreaks/approval.html", mode, deposito, schedules, udfs, taskId, from, maintenanceLogKey, operation, keyId, origin, isreload);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
				render("DepositoBreaks/approval.html", mode, deposito, schedules, udfs, taskId, from, maintenanceLogKey, operation, keyId, origin, isreload);
			}

		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			TdMaster deposito = depositoService.approveDepositoBreak(MenuConstants.TD_DEPOSITO_BREAK, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, null, session.get(UIConstants.SESSION_USER_KEY));

			renderJSON(Formatter.resultSuccess(Messages.get("deposito.approved", deposito.getDepositoNo())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			TdMaster deposito = null;
			if (Helper.isNullOrEmpty(correction)) {
				deposito = depositoService.approveDepositoBreak(MenuConstants.TD_DEPOSITO_BREAK, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, null, session.get(UIConstants.SESSION_USER_KEY));
			} else {
				deposito = depositoService.approveDepositoBreak(MenuConstants.TD_DEPOSITO_BREAK, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "", correction, session.get(UIConstants.SESSION_USER_KEY));
			}

			renderJSON(Formatter.resultSuccess(Messages.get("deposito.rejected", deposito.getDepositoNo())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void populatePaymentSchedule(Date placementDate, Date maturityDate, BigDecimal nominal, BigDecimal interestRate, String accrualBase, String yearBase, Integer totalCoupon, boolean considerHoliday, String freqSecurity) {
		log.debug("populatePaymentSchedule. placementDate: " + placementDate + " maturityDate: " + maturityDate + " nominal: " + nominal + " interestRate: " + interestRate + " accrualBase: " + accrualBase + " yearBase: " + yearBase + " totalCoupon: " + totalCoupon + " considerHoliday: " + considerHoliday + " freqSecurity: " + freqSecurity);

		String frequency = LookupConstants.FREQUENCY_MONTH;
		totalCoupon = totalCoupon + 1;
		List<TdInterestSchedule> schedules = depositoService.populatePaymentSchedule(placementDate, maturityDate, nominal, interestRate, accrualBase, yearBase, frequency, totalCoupon, considerHoliday, freqSecurity);

		render("DepositoBreaks/gridPaymentSchedules.html", schedules);
	}

	public static void paymentDate(String deposito, Date date) {
		log.debug("paymentDate. deposito: " + deposito + " date: " + date);

		String schedule = null;
		try {
			// String fmtDate = fmtDDMMMYYYYY(date);
			TdInterestSchedule interest = getPaymentDate(deposito, date);
			// depositoService.getInterestScheduleByDate(Long.parseLong(deposito),
			// fmtDate);
			schedule = json.writeValueAsString(interest);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
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

	public static TdInterestSchedule getPaymentDateFullRedeem(String deposito) {
		log.debug("getPaymentDateFullRedeem. deposito: " + deposito);

		TdInterestSchedule interest = null;
		try {
			interest = depositoService.getInterestScheduleFullRedeem(Long.parseLong(deposito));
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		return interest;
	}

	public static void getTaxUser(String accountNo, String securityType, String depositoNo) {
		log.debug("getTaxUser. accountNo: " + accountNo + " securityType: " + securityType + " depositoNo: " + depositoNo);

		String value = depositoService.getTaxDepositoUser(accountNo, securityType, depositoNo, LookupConstants.TAX_OBJECT_ACCRUED_INTEREST);
		if (value == null)
			value = "";
		renderText(value);
	}
}