package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import org.springframework.beans.BeanUtils;

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
import com.simian.medallion.model.CsChargeMaster;
import com.simian.medallion.model.CsTransactionCharge;
import com.simian.medallion.model.GnCalendar;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnTaxMaster;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.TdInterestSchedule;
import com.simian.medallion.model.TdMaster;
import com.simian.medallion.model.TdTransaction;
import com.simian.medallion.model.TdTransactionCharge;
import com.simian.medallion.model.comparators.InterestScheduleComparator;
import com.simian.medallion.model.helper.DateTool;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.TransactionHelper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class DepositoPlacements extends Registry {
	private static Logger log = Logger.getLogger(DepositoPlacements.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);

		renderArgs.put("depositoTemplatePlacement", LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT);
	}

	@Before(only = { "entry", "edit", "view", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		GnLookup paymentFreqMonthly = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MONTHLY);
		GnLookup paymentFreqMaturity = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MATURITY);
		renderArgs.put("chargeFreqT", LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE);
		renderArgs.put("paymentFreqMonthlyId", paymentFreqMonthly.getLookupId());
		renderArgs.put("paymentFreqMonthlyDesc", paymentFreqMonthly.getLookupDescription());
		renderArgs.put("paymentFreqMaturityId", paymentFreqMaturity.getLookupId());
		renderArgs.put("paymentFreqMaturityDesc", paymentFreqMaturity.getLookupDescription());
		// List<SelectItem> maturityIns =
		// generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants._DEPOSITO_TYPE);
		List<SelectItem> maturityIns = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITO_TYPE);
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

		renderArgs.put("depositoTemplatePlacement", LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT);

		renderArgs.put("depositoInquiryOriginated", LookupConstants.INQUIRY_DEPOSITO_ORIGINATED);
		renderArgs.put("depositoPlacementOriginated", LookupConstants.DEPOSITO_PLACEMENT_ORIGINATED);

		// List<SelectItem> paymentMethod =
		// generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants.FUND_TRANSFER_METHOD);
		List<SelectItem> paymentMethod = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
		renderArgs.put("paymentMethod", paymentMethod);

		renderArgs.put("viaFile", LookupConstants.FUND_TRANSFER_METHOD_FILE);
		renderArgs.put("viaInterface", LookupConstants.FUND_TRANSFER_METHOD_VIA_INTERFACE);
		renderArgs.put("paymentManual", LookupConstants.FUND_TRANSFER_METHOD_MANUAL);

	}

	@Check("transaction.depositoplacement")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_PLACEMENT));
		render();
	}

	@Check("transaction.depositoplacement")
	public static void search(Paging page, DepositoSearchParameter param) {
		log.debug("search. page: " + page + " param: " + param);

		page.addParams("dp.placementDate", page.GREATEQUAL, param.dateFrom);
		page.addParams("dp.placementDate", page.LESSEQUAL, param.dateTo);
		if (!param.customerCode.isEmpty())
			page.addParams("a.customer.customerKey", page.EQUAL, Long.parseLong(param.customerCode));
		page.addParams("st.securityType", page.EQUAL, param.securityType);
		if (!param.securityCode.isEmpty())
			page.addParams("s.securityKey", page.EQUAL, Long.parseLong(param.securityCode));
		log.debug("deposito no = " + param.depositoNo);
		log.debug("deposito no = " + param.depositoNoOperator);
		page.addParams("dp.depositoNo", param.depositoNoOperator, UIHelper.withOperator(param.depositoNo, param.depositoNoOperator));
		page.addParams("trim(tt.status)", page.EQUAL, LookupConstants.__RECORD_STATUS_REJECTED);
		page.addParams("trim(tt.recordStatus)", page.EQUAL, LookupConstants.__RECORD_STATUS_NEED_REVISION);
		page.addParams("dt.depositoTemplate.lookupId", page.EQUAL, LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT);
		page.addParams(Helper.searchAll("(to_char(dp.placementDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||dp.depositoNo||a.accountNo||a.name||" + "s.securityId||tt.amount||tt.interestRate||to_char(tt.maturityDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		page = depositoService.pagingDepositoPlacement(page);
		renderJSON(page);
	}

	@Check("transaction.depositoplacement")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		String origin = LookupConstants.DEPOSITO_PLACEMENT_ORIGINATED;
		TdMaster deposito = new TdMaster();
		String taskId = "";

		// DEFAULT TEMPLATE
		/*
		 * TdTransactionTemplate depTemplate =
		 * depositoService.getTdTransactionTemplate
		 * (LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT);
		 * deposito.setDepositoTemplate(depTemplate.getDepositoTemplate());
		 * deposito
		 * .setTransactionTemplate(depTemplate.getTransactionTemplate());
		 * deposito
		 * .setDepositoTemplateKey(depTemplate.getDepositoTemplateKey());
		 */

		deposito.setPlacementDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));

		deposito.setMaturityInstruction(new GnLookup(LookupConstants.DEPOSITO_TYPE_FULL_REDEEM));

		// DEFAULT TAX
		String taxKey = generalService.getValueParam(SystemParamConstants.PARAM_DEFAULT_CAP_GAIN_TAX);
		GnTaxMaster tax = generalService.getTaxMaster(Long.parseLong(taxKey));
		deposito.setTaxMaster(tax);

		// DEFAULT INTEREST FREQ
		deposito.setInterestFrequency(new GnLookup(LookupConstants.INTEREST_FREQUENCY_MATURITY));

		// DEFAULT CONSIDER HOLIDAY
		deposito.setConsiderHoliday(false);

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
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_PLACEMENT));
		boolean isreload = false;
		render("DepositoPlacements/entry.html", mode, deposito, taskId, udfs, origin, isreload);
	}

	@Check("transaction.depositoplacement")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		String origin = LookupConstants.DEPOSITO_PLACEMENT_ORIGINATED;
		String taskId = "";
		TdMaster deposito = depositoService.getMasterDeposito(id);
		TdTransaction trx = depositoService.getTransactionDeposito(deposito.getDepositoKey());
		if (trx.getRemarksCorrection() != null) {
			deposito.setNeedCorrection(true);
			deposito.setRemarksCorrection(trx.getRemarksCorrection());
		}

		if (deposito.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_NEW) || deposito.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_UPDATED)) {
			mode = UIConstants.DISPLAY_MODE_VIEW;
		}

		// DEFAULT TEMPLATE
		deposito.setDepositoTemplate(trx.getDepositoTrxTemplate().getDepositoTemplate());
		deposito.setTransactionTemplate(trx.getDepositoTrxTemplate().getTransactionTemplate());
		deposito.setDepositoTemplateKey(trx.getDepositoTrxTemplate().getDepositoTemplateKey());

		// DEFAULT TAX
		String taxKey = generalService.getValueParam(SystemParamConstants.PARAM_DEFAULT_CAP_GAIN_TAX);
		GnTaxMaster tax = generalService.getTaxMaster(Long.parseLong(taxKey));
		deposito.setTaxMaster(tax);
		
		//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
		Date firstCouponDate = null;
		try {
			firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<TdTransactionCharge> listTrxCharges = new ArrayList<TdTransactionCharge>();
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
		BigDecimal totalFee = BigDecimal.ZERO;
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
				if (chargeTrans.getChargeFrequency().equals(LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE))
					totalFee = totalFee.add(chargeTrans.getChargeValue());

				listTrxCharges.add(chargeTrans);
			}
			deposito.setDepositoCharges(listTrxCharges);
		}

		deposito.setTotalFee(totalFee);
		TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());
		// deposito.setAccruedDays(TransactionHelper.calculateAccruedDays(deposito.getPlacementDate(),
		// deposito.getMaturityDate(),iAccrualBase));
		//deposito.setAccruedInterest(TransactionHelper.calculateAccruedInterest(deposito.getAmount(), deposito.getInterestRate(), deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), deposito.getPlacementDate(), deposito.getMaturityDate(), deposito.getMaturityDate(), false, BigDecimal.ZERO, BigDecimal.ZERO));
		deposito.setAccruedInterest(TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
				   deposito.getInterestRate(), 
				   deposito.getSecurity().getAccrualBase().getLookupCode(), 
				   deposito.getSecurity().getYearBase().getLookupCode(), 
				   deposito.getSecurity().getFrequency().getLookupId(), 
				   deposito.getPlacementDate(), 
				   deposito.getMaturityDate(), 
				   deposito.getMaturityDate(), 
				   firstCouponDate,
				   false, BigDecimal.ZERO, BigDecimal.ZERO, false));
		deposito.setAccruedDays(TransactionHelper.getAccruedDays());
		deposito.setSettlementAmount(deposito.getTotalFee().add(deposito.getAmount()));
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

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_PLACEMENT));
		boolean isreload = true;
		render("DepositoPlacements/entry.html", mode, deposito, schedules, udfs, taskId, origin, isreload);
	}

	public static void view(Long id, String from) {
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String origin = LookupConstants.DEPOSITO_PLACEMENT_ORIGINATED;
		String taskId = "";
		//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
		Date firstCouponDate = null;
		try {
			firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		TdTransaction trx = depositoService.getTdTransaction(id);
		TdMaster deposito = depositoService.getMasterDeposito(trx.getDeposito().getDepositoKey());
		
		Date depositoEffDate = trx.getTransactionDate();
		Date depositoMatureDate = trx.getMaturityDate();
		
		deposito.setAmount(trx.getAmount());
		deposito.setInterestRate(trx.getInterestRate());
//		TdTransaction trx = depositoService.getTransactionDeposito(deposito.getDepositoKey());
		if (trx.getRemarksCorrection()!=null){
			deposito.setNeedCorrection(true);
			deposito.setRemarksCorrection(trx.getRemarksCorrection());
		}
		
		if(!Helper.isNullOrEmpty(from) && from.trim().equals(LookupConstants.INQUIRY_DEPOSITO_ORIGINATED)){
			deposito.setMaturityDate(trx.getMaturityDate());
		}
		
		// DEFAULT TEMPLATE
		deposito.setDepositoTemplate(trx.getDepositoTrxTemplate().getDepositoTemplate());
		deposito.setTransactionTemplate(trx.getDepositoTrxTemplate().getTransactionTemplate());
		deposito.setDepositoTemplateKey(trx.getDepositoTrxTemplate().getDepositoTemplateKey());
		
		// DEFAULT TAX
		String taxKey = generalService.getValueParam(SystemParamConstants.PARAM_DEFAULT_CAP_GAIN_TAX);
		GnTaxMaster tax = generalService.getTaxMaster(Long.parseLong(taxKey));
		deposito.setTaxMaster(tax);
		
		List<TdTransactionCharge> listTrxCharges = new ArrayList<TdTransactionCharge>();
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
		BigDecimal totalFee = BigDecimal.ZERO;
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
				if (chargeTrans.getChargeFrequency().equals(LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE))
					totalFee = totalFee.add(chargeTrans.getChargeValue());
				
				listTrxCharges.add(chargeTrans);
			}
			deposito.setDepositoCharges(listTrxCharges);
		}
		
		deposito.setTotalFee(totalFee);
		//int iAccrualBase = TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());
		//deposito.setAccruedDays(TransactionHelper.calculateAccruedDays(deposito.getPlacementDate(), deposito.getMaturityDate(),iAccrualBase));
		/*deposito.setAccruedInterest(TransactionHelper.calculateAccruedInterest(deposito.getAmount(), 
																			   deposito.getInterestRate(), 
																			   deposito.getSecurity().getAccrualBase().getLookupCode(), 
																			   deposito.getSecurity().getYearBase().getLookupCode(), 
																			   deposito.getSecurity().getFrequency().getLookupId(), 
																			   deposito.getPlacementDate(), 
																			   deposito.getMaturityDate(), 
																			   deposito.getMaturityDate(), 
																			   false, BigDecimal.ZERO, BigDecimal.ZERO));*/
		deposito.setAccruedInterest(TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
				   deposito.getInterestRate(), 
				   deposito.getSecurity().getAccrualBase().getLookupCode(), 
				   deposito.getSecurity().getYearBase().getLookupCode(), 
				   deposito.getSecurity().getFrequency().getLookupId(), 
				   deposito.getPlacementDate(), 
				   deposito.getMaturityDate(), 
				   deposito.getMaturityDate(),
				   firstCouponDate,
				   false, BigDecimal.ZERO, BigDecimal.ZERO, false));
		deposito.setAccruedDays(TransactionHelper.getAccruedDays());
		deposito.setSettlementAmount(deposito.getTotalFee().add(deposito.getAmount()));
		deposito.setNoOfPayment(deposito.getInterestSchedules().size());
		
		int i =0;
		List<TdInterestSchedule> schedules1 = new ArrayList<TdInterestSchedule>();
		for (TdInterestSchedule tdInterestSchedule : deposito.getInterestSchedules()) {
			schedules1.add(tdInterestSchedule);
		}
		Collections.sort(schedules1, new InterestScheduleComparator());
		for (TdInterestSchedule tdInterestSchedule : schedules1) {
			//if(DateTool.isGreaterEqualYMD(tdInterestSchedule.getEndDate(), depositoMatureDate)){
			if(DateTool.isGreaterEqualYMD(depositoMatureDate, tdInterestSchedule.getEndDate())){
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
				i++;
				tdInterestSchedule.getId().setPaymentNo(i);
				schedules.add(tdInterestSchedule);
			}
			
		}
		Collections.sort(schedules, new InterestScheduleComparator());
		deposito.setNoOfPayment(schedules.size());
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
		deposito.setStatus(trx.getStatus());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_INQUIRY));
		render("DepositoPlacements/entry.html", mode, from, deposito, schedules, udfs, taskId, origin);
	}

	@Check("transaction.depositoplacement")
	public static void save(TdMaster deposito, List<TdTransactionCharge> charges, TdInterestSchedule[] schedules, List<GnUdfMaster> udfs, String mode, boolean isreload) {
		log.debug("save. deposito: " + deposito + " charges: " + charges + " schedules: " + schedules + " udfs: " + udfs + " mode: " + mode + " isreload: " + isreload);

		String origin = LookupConstants.DEPOSITO_PLACEMENT_ORIGINATED;
		if (deposito != null && charges != null) {
			deposito.getDepositoCharges().clear();
			deposito.getDepositoCharges().addAll(charges);
		}

		if (deposito != null && schedules != null) {
			deposito.getInterestSchedules().clear();
			// deposito.getInterestSchedules().addAll(schedules);
			for (TdInterestSchedule schedule : schedules) {
				deposito.getInterestSchedules().add(schedule);
			}
		}

		if (deposito != null) {
			deposito.setPbaCurrency(deposito.getSecurity().getCurrency());
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

			if (deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MATURITY)) {
				schedules = null;
				if (deposito.getInterestSchedules() != null) {
					deposito.getInterestSchedules().clear();
					deposito.setInterestSchedules(null);
				}
			}

			Date currentBussinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);

			// validate
			validation.required("Placement Date is", deposito.getPlacementDate());
			if (deposito.getPlacementDate() != null) {
				if (fmtYYYYMMDD(deposito.getPlacementDate()).compareTo(fmtYYYYMMDD(currentBussinessDate)) > 0) {
					validation.addError("", "placementDate.greater_than.applicationDate");
				}
			}
			validation.required("Account No is", deposito.getAccount().getAccountNo());
			if (deposito.getAccount().getBlocked() != null) {
				if (deposito.getAccount().getBlocked() == true) {
					validation.addError("", Messages.get(ExceptionConstants.ACCOUNT_BLOCKED, deposito.getAccount().getAccountNo()));
				}
			}
			validation.required("Security Type is", deposito.getSecurity().getSecurityType().getSecurityType());
			validation.required("Security Code is", deposito.getSecurity().getSecurityId());
			if (deposito.getSecurity().getIsExpire() != null) {
				if (deposito.getSecurity().getIsExpire() == true) {
					if (fmtYYYYMMDD(deposito.getSecurity().getExpiredDate()).compareTo(fmtYYYYMMDD(currentDate)) < 0) {
						validation.addError("", Messages.get(ExceptionConstants.SECURITY_CODE_EXPIRED, deposito.getSecurity().getSecurityId()));
					}
				}
			}
			if (!deposito.getSecurity().getCurrency().getCurrencyCode().equals(deposito.getBankAccount().getCurrency().getCurrencyCode())) {
				validation.addError("", Messages.get(ExceptionConstants.BANK_ACCOUNT_CURRENCY_SHOULD_SAME, deposito.getSecurity().getCurrency().getCurrencyCode()));
			}

			validation.required("Branch is", deposito.getBranchCode());
			validation.required("Nominal is", deposito.getAmount());
			validation.required("Interest Rate (gross) is", deposito.getInterestRate());
			validation.required("Maturity Date is", deposito.getMaturityDate());
			if (deposito.getPlacementDate() != null && deposito.getMaturityDate() != null) {
				if (fmtYYYYMMDD(deposito.getPlacementDate()).compareTo(fmtYYYYMMDD(deposito.getMaturityDate())) > 0) {
					validation.addError("", "maturityDate.less_than.placementDate");
				}
			}
			if (deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MONTHLY)) {
				validation.required("No. of int. payment is", deposito.getInterestFrequency().getLookupId());
				if (schedules == null) {
					validation.addError("", ExceptionConstants.PAYMENT_SHEDULE_MUST_NOT_NULL);
				}
			}

			List<GnCalendar> holidays = generalService.listHolidays(UIConstants.SIMIAN_BANK_ID);
			if (deposito.getMaturityDate() != null) {
				for (GnCalendar holiday : holidays) {
					if (holiday.getId().getCalendarDate().getTime() == (deposito.getMaturityDate().getTime())) {
						deposito.setHoliday(true);
					}
				}
				boolean isSameMaturityDate = false;
				if (schedules != null) {
					for (TdInterestSchedule schedule : schedules) {
						if (deposito.getMaturityDate().getTime() == schedule.getEndDate().getTime()) {
							isSameMaturityDate = true;
						}
					}
				} else {
					isSameMaturityDate = true;
				}

				if (!isSameMaturityDate) {
					validation.addError("", ExceptionConstants.LAST_END_DATE_PAYMENT_SCHEDULE_SAME_MATURITY_DATE);
				}
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
			if (deposito.getConsiderHoliday() != null) {
				if (deposito.getConsiderHoliday() == true) {
					if (schedules != null) {
						for (TdInterestSchedule schedule : schedules) {
							for (GnCalendar gnCalendar : holidays) {
								if (gnCalendar.getId().getCalendarDate().getTime() == (schedule.getEndDate().getTime())) {
									validation.addError("", Messages.get(ExceptionConstants.END_DATE_PAYMENT_SCHEDULE_HOLIDAY, dateFormat.format(schedule.getEndDate())));
								}
							}
						}
					}
				}
			}

			validation.required("Account No in tab Bank Info is", deposito.getBankAccount().getAccountNo());
			for (TdTransactionCharge charge : deposito.getDepositoCharges()) {
				if (charge.getChargeWaived() != null) {
					if (charge.getChargeWaived()) {
						charge.setChargeValue(BigDecimal.ZERO);
						charge.setTaxAmount(BigDecimal.ZERO);
					}
				}
			}
			validation.required("Fund Transfer Method is", deposito.getPbaTransferMethod().getLookupId());
			if (deposito.getPbaTransferMethod().getLookupId().equals(LookupConstants.FUND_TRANSFER_METHOD_VIA_INTERFACE)) {
				validation.required("Placement Bank Account No is", deposito.getPbaAccountNo());
				validation.required("Placement Bank Beneficiary Name is", deposito.getPbaBeneficiaryName());
			}

			validation.required("Placement Bank Code is", deposito.getPbaBankCode().getThirdPartyCode());
			validation.required("Placement Description", deposito.getPbaDescription());
			deposito.setInterestAdjust(deposito.getAccruedInterest());
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

				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_PLACEMENT));
				render("DepositoPlacements/entry.html", mode, deposito, charges, schedules, udfs, origin, isreload);
			} else {

				serializerService.serialize(session.getId(), deposito.getDepositoKey(), deposito);

				confirming(deposito.getDepositoKey(), mode, isreload);
			}
		} else {
			entry();
		}

	}

	@Check("transaction.depositoplacement")
	public static void confirming(Long id, String mode, boolean isreload) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isreload: " + isreload);

		boolean confirming = true;
		TdMaster deposito = serializerService.deserialize(session.getId(), id, TdMaster.class);
		log.debug("Payment Freq COnfirming = " + deposito.getInterestFrequency().getLookupId());
		String origin = LookupConstants.DEPOSITO_PLACEMENT_ORIGINATED;
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
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_PLACEMENT));
		render("DepositoPlacements/entry.html", mode, deposito, schedules, confirming, udfs, origin, isreload);
	}

	@Check("transaction.depositoplacement")
	public static void confirm(TdMaster deposito, List<TdTransactionCharge> charges, TdInterestSchedule[] schedules, List<GnUdfMaster> udfs, String mode, boolean isreload) {
		log.debug("confirm. deposito: " + deposito + " charges: " + charges + " schedules: " + schedules + " udfs: " + udfs + " mode: " + mode + " isreload: " + isreload);

		String origin = LookupConstants.DEPOSITO_PLACEMENT_ORIGINATED;
		if (deposito != null && charges != null) {
			deposito.getDepositoCharges().clear();
			deposito.getDepositoCharges().addAll(charges);
		}

		if (deposito != null && schedules != null) {
			deposito.getInterestSchedules().clear();
			// deposito.getInterestSchedules().addAll(schedules);
			for (TdInterestSchedule schedule : schedules) {
				deposito.getInterestSchedules().add(schedule);
			}
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
				log.debug(e.getMessage(), e);
			}
		}
		boolean confirming = true;
		try {
			/*
			 * List<GnCalendar> holidays =
			 * generalService.listHolidays(UIConstants.SIMIAN_BANK_ID); for
			 * (GnCalendar holiday : holidays){ if
			 * (holiday.getId().getCalendarDate().getTime() ==
			 * (deposito.getPlacementDate().getTime())){ throw new
			 * MedallionException(ExceptionConstants.DATE_IS_HOLIDAY,
			 * "deposito.placementDate"); }
			 * 
			 * if (holiday.getId().getCalendarDate().getTime() ==
			 * (deposito.getMaturityDate().getTime())){ throw new
			 * MedallionException(ExceptionConstants.DATE_IS_HOLIDAY,
			 * "transaction.maturityDate"); } }
			 */

			if (deposito.getDepositoKey() != null) {
				TdMaster currentDeposito = depositoService.getMasterDeposito(deposito.getDepositoKey());
				if ((currentDeposito.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_UPDATED))) {
					throw new MedallionException(ExceptionConstants.DATA_CANT_EDITED, "");
				}
			}

			deposito.setEffectiveDate(deposito.getPlacementDate());
			deposito.setChargeable(true);
			deposito.setDepositoTemplate(new GnLookup(LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT));
			// TdMaster trx = new TdMaster();
			for (TdTransactionCharge charge : deposito.getDepositoCharges()) {
				if (charge.getChargeWaived() == null)
					charge.setChargeWaived(false);
				if (charge.getChargeWaived()) {
					charge.setChargeValue(BigDecimal.ZERO);
					charge.setTaxAmount(BigDecimal.ZERO);
				}
			}
			// deposito.setInterestAdjust(deposito.getAccruedInterest());
			TdMaster trx = depositoService.createDeposito(MenuConstants.TD_DEPOSITO_PLACEMENT, deposito, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			log.debug("MODE = " + mode);
			if (trx.getDepositoKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
					result.put("message", Messages.get("deposito.confirmed.successful.edit", trx.getDepositoNo()));
				}
				if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
					result.put("message", Messages.get("deposito.confirmed.successful", trx.getDepositoNo()));
				}
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_PLACEMENT));
				render("DepositoPlacements/detail.html", trx, mode, confirming, origin, isreload);
			}
		} catch (MedallionException ex) {
			// TODO: handle exception
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			log.debug("get Param = " + ex.getParams());
			if (ex != null && ex.getParams() != null) {
				for (Object paramItem : ex.getParams()) {
					log.debug("PARAMS ITEM = " + paramItem);
					params.add(Messages.get(paramItem));
				}

				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			} else {
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
			renderJSON(result);
		}catch (Exception e) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("error",e.getMessage());
			renderJSON(result);
		}
	}

	@Check("transaction.depositoplacement")
	public static void back(Long id, String mode, boolean isreload) {
		log.debug("back. id: " + id + " mode: " + mode + " isreload: " + isreload);

		TdMaster deposito = serializerService.deserialize(session.getId(), id, TdMaster.class);
		String taskId = "";
		String origin = LookupConstants.DEPOSITO_PLACEMENT_ORIGINATED;
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
		if (deposito.getInterestSchedules() != null) {
			for (TdInterestSchedule tdInterestSchedule : deposito.getInterestSchedules()) {
				schedules.add(tdInterestSchedule);
			}
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

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_PLACEMENT));
		render("DepositoPlacements/entry.html", mode, deposito, schedules, udfs, taskId, origin, isreload);
	}

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

	public static void charge(String code, BigDecimal quantity, BigDecimal nominal, Long custodyAccountKey) {
		log.debug("charge. code: " + code + " quantity: " + quantity + " nominal: " + nominal + " custodyAccountKey: " + custodyAccountKey);

		// CsChargeMaster charge =
		// generalService.getChargeMaster(UIConstants.SIMIAN_BANK_ID, code);
		CsChargeMaster charge = generalService.getChargeMasterForTransaction(UIConstants.SIMIAN_BANK_ID, code);
		log.debug("[CONT] >>> custodyAccountKey = " + custodyAccountKey);
		if (charge != null) {
			CsTransactionCharge transactionCharge = transactionService.calculateCharge(charge, quantity, nominal, custodyAccountKey);
			TdTransactionCharge depositoCharge = new TdTransactionCharge();
			BeanUtils.copyProperties(transactionCharge, depositoCharge);

			BigDecimal taxAmount = transactionCharge.getTaxAmount();
			// if (taxAmount==null) taxAmount = BigDecimal.ZERO;
			if (taxAmount == null) {
				taxAmount = BigDecimal.ZERO;
				transactionCharge.setTaxAmount(taxAmount);
			}
			depositoCharge.setChargeNetAmount(transactionCharge.getChargeValue().add(taxAmount));
			depositoCharge.setTaxMaster(transactionCharge.getChargeMaster().getTaxMaster());
			try {
				renderJSON(json.writeValueAsString(depositoCharge));
			} catch (JsonGenerationException e) {
				log.debug("serialize", e);
			} catch (JsonMappingException e) {
				log.debug("serialize", e);
			} catch (IOException e) {
				log.debug("serialize", e);
			}
		}
	}

	public static void populatePaymentSchedule(Date placementDate, Date maturityDate, BigDecimal nominal, BigDecimal interestRate, String accrualBase, String yearBase, Integer totalCoupon, boolean considerHoliday, String freqSecurity) {
		log.debug("populatePaymentSchedule. placementDate: " + placementDate + " maturityDate: " + maturityDate + " nominal: " + nominal + " interestRate: " + interestRate + " accrualBase: " + accrualBase + " yearBase: " + yearBase + " totalCoupon: " + totalCoupon + " considerHoliday: " + considerHoliday + " freqSecurity: " + freqSecurity);

		String frequency = LookupConstants.FREQUENCY_MONTH;
		totalCoupon = totalCoupon + 1;
		List<TdInterestSchedule> schedules = depositoService.populatePaymentSchedule(placementDate, maturityDate, nominal, interestRate, accrualBase, yearBase, frequency, totalCoupon, considerHoliday, freqSecurity);

		render("DepositoPlacements/gridPaymentSchedules.html", schedules);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, String processDefinition, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " processDefinition: " + processDefinition + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			TdMaster deposito = depositoService.getMasterDeposito(keyId);
			TdTransaction trx = depositoService.getTransactionDeposito(deposito.getDepositoKey());
			String origin = LookupConstants.DEPOSITO_PLACEMENT_ORIGINATED;
			// DEFAULT TEMPLATE
			deposito.setDepositoTemplate(trx.getDepositoTrxTemplate().getDepositoTemplate());
			deposito.setTransactionTemplate(trx.getDepositoTrxTemplate().getTransactionTemplate());
			deposito.setDepositoTemplateKey(trx.getDepositoTrxTemplate().getDepositoTemplateKey());
			
			//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
			Date firstCouponDate = null;
			try {
				firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			// DEFAULT TAX
			String taxKey = generalService.getValueParam(SystemParamConstants.PARAM_DEFAULT_CAP_GAIN_TAX);
			GnTaxMaster tax = generalService.getTaxMaster(Long.parseLong(taxKey));
			deposito.setTaxMaster(tax);

			List<TdTransactionCharge> listTrxCharges = new ArrayList<TdTransactionCharge>();
			List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
			BigDecimal totalFee = BigDecimal.ZERO;
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
					if (chargeTrans.getChargeFrequency().equals(LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE))
						totalFee = totalFee.add(chargeTrans.getChargeValue());

					listTrxCharges.add(chargeTrans);
				}
				deposito.setDepositoCharges(listTrxCharges);
			}

			deposito.setTotalFee(totalFee);
			TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());
			// deposito.setAccruedDays(TransactionHelper.calculateAccruedDays(deposito.getPlacementDate(),
			// deposito.getMaturityDate(),iAccrualBase));
			//deposito.setAccruedInterest(TransactionHelper.calculateAccruedInterest(deposito.getAmount(), deposito.getInterestRate(), deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), deposito.getPlacementDate(), deposito.getMaturityDate(), deposito.getMaturityDate(), false, BigDecimal.ZERO, BigDecimal.ZERO));
			deposito.setAccruedInterest(TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
					   deposito.getInterestRate(), 
					   deposito.getSecurity().getAccrualBase().getLookupCode(), 
					   deposito.getSecurity().getYearBase().getLookupCode(), 
					   deposito.getSecurity().getFrequency().getLookupId(), 
					   deposito.getPlacementDate(), 
					   deposito.getMaturityDate(), 
					   deposito.getMaturityDate(), 
					   firstCouponDate,
					   false, BigDecimal.ZERO, BigDecimal.ZERO, false));
			deposito.setInterestAdjust(deposito.getAccruedInterest());
			deposito.setAccruedDays(TransactionHelper.getAccruedDays());
			deposito.setSettlementAmount(deposito.getTotalFee().add(deposito.getAmount()));
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

			boolean isreload = true;
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
				render("DepositoPlacements/approval.html", mode, deposito, schedules, udfs, taskId, from, maintenanceLogKey, operation, keyId, origin, isreload);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
				render("DepositoPlacements/approval.html", mode, deposito, schedules, udfs, taskId, from, maintenanceLogKey, operation, keyId, origin, isreload);
			}

		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			TdMaster deposito = depositoService.approveDepositoPlacement(MenuConstants.TD_DEPOSITO_PLACEMENT, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, null, session.get(UIConstants.SESSION_USER_KEY));

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
				deposito = depositoService.approveDepositoPlacement(MenuConstants.TD_DEPOSITO_PLACEMENT, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, null, session.get(UIConstants.SESSION_USER_KEY));
			} else {
				deposito = depositoService.approveDepositoPlacement(MenuConstants.TD_DEPOSITO_PLACEMENT, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "", correction, session.get(UIConstants.SESSION_USER_KEY));
			}

			renderJSON(Formatter.resultSuccess(Messages.get("deposito.rejected", deposito.getDepositoNo())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void checkHoliday(Date date) {
		log.debug("checkHoliday. date: " + date);

		// List<GnCalendar> holidays =
		// generalService.listHolidays(UIConstants.SIMIAN_BANK_ID);
		boolean isHoliday = false;
		/*
		 * for (GnCalendar holiday : holidays) { if
		 * (holiday.getId().getCalendarDate().getTime() == (date.getTime())){
		 * isHoliday = true; } }
		 */
		GnCalendar holidays = generalService.getCalendar(date);
		if (holidays != null) {
			isHoliday = true;
		}
		renderText(isHoliday);
	}

	public static void getIntFrequency(String pDate, String mDate) {
		log.debug("getIntFrequency. pDate: " + pDate + " mDate: " + mDate);

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Date placementDate = Helper.parse(pDate, "dd/MM/yyyy");
			Date maturityDate = Helper.parse(mDate, "dd/MM/yyyy");
			log.debug("placementDate = " + placementDate);
			log.debug("maturityDate = " + maturityDate);
			result.put("diff", depositoService.getInterestFrequency(placementDate, maturityDate));
			result.put("success", "success");
		} catch (Exception e) {
			result.put("error", e.getMessage());
			result.put("diff", "");
		}
		renderJSON(result);
	}

	public static void onChangeNextPaymentDate(Date startDate, Date endDate, BigDecimal nominal, BigDecimal interestRate, String accrualBase, String yearBase, boolean considerHoliday, String frequency) {
		log.debug("onChangeNextPaymentDate. startDate: " + startDate + " endDate: " + endDate + " nominal: " + nominal + " interestRate: " + interestRate + " accrualBase: " + accrualBase + " yearBase: " + yearBase + " considerHoliday: " + considerHoliday + " frequency: " + frequency);

		Map<String, Object> result = new HashMap<String, Object>();

		Date paymentDate = generalService.addWorkingDate(endDate, 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
		Date firstCouponDate = null;
		try {
			firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// int iAccrualBase = TransactionHelper.decodeAccrualBase(accrualBase);
		// Integer accruedDays =
		// TransactionHelper.calculateAccruedDays(startDate, endDate,
		// iAccrualBase);
		//BigDecimal grossInterest = TransactionHelper.calculateAccruedInterest(nominal, interestRate, accrualBase, yearBase, frequency, startDate, endDate, endDate, false, BigDecimal.ZERO, BigDecimal.ZERO);
		BigDecimal grossInterest = TransactionHelper.calculateAccruedInterestNew(nominal, interestRate, accrualBase, yearBase, 
				  frequency, startDate, endDate, endDate, firstCouponDate, false, 
				  BigDecimal.ZERO, BigDecimal.ZERO, false);
		Integer accruedDays = TransactionHelper.getAccruedDays();
		log.debug("DAYS = " + accruedDays);
		log.debug("GROSS INTEREST = " + grossInterest);

		result.put("status", "success");
		result.put("days", accruedDays);
		result.put("grossInterest", grossInterest);
		result.put("paymentDate", dateFormat.format(paymentDate));

		renderJSON(result);

	}

	public static void calculateTotalCoupon(Date sDate, Date eDate) {
		log.debug("calculateTotalCoupon. sDate: " + sDate + " eDate: " + eDate);

		Calendar sCalDate = Calendar.getInstance();
		Calendar eCalDate = Calendar.getInstance();

		sCalDate.setTime(sDate);
		eCalDate.setTime(eDate);

		int difMonth = sCalDate.get(Calendar.MONTH) - eCalDate.get(Calendar.MONTH);

		log.debug(">>> " + difMonth / 1);

		renderText(difMonth);

	}

	public static void getDefPaymentFreq(String securityType) {
		log.debug("getDefPaymentFreq. securityType: " + securityType);

		String value = depositoService.getDefPaymentFreq(securityType);
		if (value == null)
			value = "";

		renderText(value);
	}

	public static void checkComplianceSyariah(Long custodyAccountKey, Long securityKey) {
		log.debug("checkComplianceSyariah, custodyAccountKey = "+custodyAccountKey+", securityKey = "+securityKey);
		
		Map<String,Object> result = new HashMap<String, Object>();
		Boolean valid = generalService.checkComplianceDeposito(custodyAccountKey, securityKey);
		String res = (valid)?"1":"0";
		result.put("valid", res);
		renderJSON(result);
	}
	
	public static void getDataIssuer(Long key) {
		log.debug("getDataIssuer. key: " + key);

		String value = "";
		GnThirdParty gnThirdParty = generalService.getThirdParty(key);
		if (gnThirdParty.getBankCode() != null)
			value = gnThirdParty.getBankCode().getThirdPartyCode();
		if (value == null)
			value = "";

		renderText(value);
	}
}
