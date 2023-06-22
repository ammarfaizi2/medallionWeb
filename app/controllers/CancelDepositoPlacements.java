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

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.TypeReference;

import play.i18n.Messages;
import play.mvc.Before;
import vo.DepositoSearchParameter;

import com.google.gson.JsonParseException;
import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnTaxMaster;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.TdInterestSchedule;
import com.simian.medallion.model.TdMaster;
import com.simian.medallion.model.TdTransaction;
import com.simian.medallion.model.TdTransactionCharge;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.TransactionHelper;
import com.simian.medallion.vo.SelectItem;

public class CancelDepositoPlacements extends Registry {
	private static Logger log = Logger.getLogger(CancelDepositoPlacements.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);

		renderArgs.put("depositoTemplatePlacement", LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT);
	}

	@Before(only = { "edit", "view", "save", "confirming", "confirm", "back", "approval" })
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

		renderArgs.put("cancelDepositoPlacementOriginated", LookupConstants.CANCEL_DEPOSITO_PLACEMENT_ORIGINATED);
		renderArgs.put("depositoInquiryOriginated", LookupConstants.INQUIRY_DEPOSITO_ORIGINATED);

		List<SelectItem> paymentMethod = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
		renderArgs.put("paymentMethod", paymentMethod);

		renderArgs.put("viaFile", LookupConstants.FUND_TRANSFER_METHOD_FILE);
		renderArgs.put("viaInterface", LookupConstants.FUND_TRANSFER_METHOD_VIA_INTERFACE);
		renderArgs.put("paymentManual", LookupConstants.FUND_TRANSFER_METHOD_MANUAL);
	}

	@Check("transaction.canceldepositoplacement")
	public static void search(Paging page, DepositoSearchParameter param) {
		log.debug("search. page: " + page + " param: " + param);

		page.addParams("dp.placementDate", page.GREATEQUAL, param.dateFrom);
		page.addParams("dp.placementDate", page.LESSEQUAL, param.dateTo);
		if (!param.customerCode.isEmpty())
			page.addParams("a.customer.customerKey", page.EQUAL, Long.parseLong(param.customerCode));
		page.addParams("st.securityType", page.EQUAL, param.securityType);
		if (!param.securityCode.isEmpty())
			page.addParams("s.securityKey", page.EQUAL, Long.parseLong(param.securityCode));
		page.addParams("dp.depositoNo", param.depositoNoOperator, UIHelper.withOperator(param.depositoNo, param.depositoNoOperator));
		page.addParams("dp.amount", page.GREAT, BigDecimal.ZERO);
		page.addParams("trim(dp.recordStatus)", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
		page.addParams("trim(dp.depositoStatus)", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
		page.addParams("trim(tt.recordStatus)", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
		page.addParams("trim(tt.status)", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
		page.addParams("dt.depositoTemplate.lookupId", page.EQUAL, LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT);
		page.addParams(Helper.searchAll("(to_char(dp.placementDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||dp.depositoNo||a.accountNo||a.name||" + "s.securityId||tt.amount||tt.interestRate||to_char(tt.maturityDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		page = depositoService.pagingCancelDepositoPlacement(page);
		renderJSON(page);
	}

	@Check("transaction.canceldepositoplacement")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_PLACEMENT));
		render();
	}

	@Check("transaction.canceldepositoplacement")
	public static void entry() {
		log.debug("entry. ");

		render();
	}

	@Check("transaction.canceldepositoplacement")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		String taskId = "";
		String origin = LookupConstants.CANCEL_DEPOSITO_PLACEMENT_ORIGINATED;
		TdTransaction trx = depositoService.getTdTransaction(id);
		TdMaster deposito = depositoService.getMasterDeposito(trx.getDeposito().getDepositoKey());

		deposito.setTransactionKey(id);
		// TdTransaction trx =
		// depositoService.getTransactionDeposito(deposito.getDepositoKey());
		if (trx.getRemarksCorrection() != null) {
			deposito.setNeedCorrection(true);
			deposito.setRemarksCorrection(trx.getRemarksCorrection());
		}
		deposito.setCancelDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
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
		if (trx != null) {
			for (TdTransactionCharge chargeTrans : trx.getDepositoCharges()) {
				BigDecimal taxAmount = chargeTrans.getTaxAmount();
				if (taxAmount == null)
					taxAmount = BigDecimal.ZERO;
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
		// int iAccrualBase =
		// TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());
		// deposito.setAccruedDays(TransactionHelper.calculateAccruedDays(deposito.getPlacementDate(),
		// deposito.getMaturityDate(),iAccrualBase));
		/*
		 * deposito.setAccruedInterest(TransactionHelper.calculateAccruedInterest
		 * (deposito.getAmount(), deposito.getInterestRate(),
		 * deposito.getSecurity().getAccrualBase().getLookupCode(),
		 * deposito.getSecurity().getYearBase().getLookupCode(),
		 * deposito.getSecurity().getFrequency().getLookupId(),
		 * deposito.getPlacementDate(), deposito.getMaturityDate(),
		 * deposito.getMaturityDate(), false, BigDecimal.ZERO,
		 * BigDecimal.ZERO));
		 */
		
		//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
		Date firstCouponDate = null;
		try {
			firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (deposito.getInterestAdjust() == null) {
			//BigDecimal accruedInterest = TransactionHelper.calculateAccruedInterest(deposito.getAmount(), deposito.getInterestRate(), deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), deposito.getPlacementDate(), deposito.getMaturityDate(), deposito.getMaturityDate(), false, BigDecimal.ZERO, BigDecimal.ZERO);
			BigDecimal accruedInterest = TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
					   deposito.getInterestRate(), 
					   deposito.getSecurity().getAccrualBase().getLookupCode(), 
					   deposito.getSecurity().getYearBase().getLookupCode(), 
					   deposito.getSecurity().getFrequency().getLookupId(), 
					   deposito.getPlacementDate(), 
					   deposito.getMaturityDate(), 
					   deposito.getMaturityDate(), 
					   firstCouponDate,
					   false, BigDecimal.ZERO, BigDecimal.ZERO, false);
			deposito.setAccruedInterest(accruedInterest);
		} else {
			deposito.setAccruedInterest(deposito.getInterestAdjust());
		}

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

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_PLACEMENT));
		render("DepositoPlacements/entry.html", mode, deposito, schedules, udfs, taskId, origin);
	}

	public static void view(Long id, String from) {
		log.debug("view. id: " + id + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String taskId = "";
		TdTransaction trx = depositoService.getTdTransaction(id);
		TdMaster deposito = depositoService.getMasterDeposito(trx.getDeposito().getDepositoKey());
		deposito.setTransactionKey(id);
		// TdTransaction trx =
		// depositoService.getTransactionDeposito(deposito.getDepositoKey());
		if (trx.getRemarksCorrection() != null) {
			deposito.setNeedCorrection(true);
			deposito.setRemarksCorrection(trx.getRemarksCorrection());
		}
		// deposito.setCancelDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
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
		if (trx != null) {
			for (TdTransactionCharge chargeTrans : trx.getDepositoCharges()) {
				BigDecimal taxAmount = chargeTrans.getTaxAmount();
				if (taxAmount == null)
					taxAmount = BigDecimal.ZERO;
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
		// int iAccrualBase =
		// TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());
		// deposito.setAccruedDays(TransactionHelper.calculateAccruedDays(deposito.getPlacementDate(),
		// deposito.getMaturityDate(),iAccrualBase));
		/*
		 * deposito.setAccruedInterest(TransactionHelper.calculateAccruedInterest
		 * (deposito.getAmount(), deposito.getInterestRate(),
		 * deposito.getSecurity().getAccrualBase().getLookupCode(),
		 * deposito.getSecurity().getYearBase().getLookupCode(),
		 * deposito.getSecurity().getFrequency().getLookupId(),
		 * deposito.getPlacementDate(), deposito.getMaturityDate(),
		 * deposito.getMaturityDate(), false, BigDecimal.ZERO,
		 * BigDecimal.ZERO));
		 */

		//TransactionHelper.calculateAccruedInterest(deposito.getAmount(), deposito.getInterestRate(), deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), deposito.getPlacementDate(), deposito.getMaturityDate(), deposito.getMaturityDate(), false, BigDecimal.ZERO, BigDecimal.ZERO);
		
		//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
		Date firstCouponDate = null;
		try {
			firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
				   deposito.getInterestRate(), 
				   deposito.getSecurity().getAccrualBase().getLookupCode(), 
				   deposito.getSecurity().getYearBase().getLookupCode(), 
				   deposito.getSecurity().getFrequency().getLookupId(), 
				   deposito.getPlacementDate(), 
				   deposito.getMaturityDate(), 
				   deposito.getMaturityDate(), 
				   firstCouponDate,
				   false, BigDecimal.ZERO, BigDecimal.ZERO, false);
		deposito.setAccruedInterest(deposito.getInterestAdjust());
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

		String origin = LookupConstants.CANCEL_DEPOSITO_PLACEMENT_ORIGINATED;
		deposito.setStatus(trx.getStatus());
		deposito.setCancelDate(trx.getCancelDate());
		deposito.setRemarksCancel(trx.getRemarksCancel());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_INQUIRY));
		render("DepositoPlacements/entry.html", mode, from, deposito, schedules, udfs, taskId, origin);
	}

	@Check("transaction.canceldepositoplacement")
	public static void save(TdMaster deposito, List<TdTransactionCharge> charges, TdInterestSchedule[] schedules, List<GnUdfMaster> udfs, String mode) {
		log.debug("save. deposito: " + deposito + " charges: " + charges + " schedules: " + schedules + " udfs: " + udfs + " mode: " + mode);

		String origin = LookupConstants.CANCEL_DEPOSITO_PLACEMENT_ORIGINATED;
		if (deposito != null && charges != null) {
			deposito.getDepositoCharges().clear();
			deposito.getDepositoCharges().addAll(charges);
		}

		if (deposito != null && schedules != null) {
			deposito.getInterestSchedules().clear();
			for (TdInterestSchedule schedule : schedules) {
				deposito.getInterestSchedules().add(schedule);
			}
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

			validation.required("Cancel Date is", deposito.getCancelDate());
			Date currentDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			if (deposito.getCancelDate() != null) {
				if ((fmtYYYYMMDD(deposito.getCancelDate()).compareTo(fmtYYYYMMDD(currentDate))) > 0) {
					validation.addError("", ExceptionConstants.CANCELDATE_GREATER_APPLICATION_DATE);
				}
				if (fmtYYYYMMDD(deposito.getCancelDate()).compareTo(fmtYYYYMMDD(deposito.getPlacementDate())) < 0) {
					validation.addError("", ExceptionConstants.CANCELDATE_LESS_THAN_PLACEMENT_DATE);
				}
				if (fmtYYYYMMDD(deposito.getCancelDate()).compareTo(fmtYYYYMMDD(deposito.getMaturityDate())) > 0) {
					validation.addError("", ExceptionConstants.CANCELDATE_GREATER_THAN_MATURITY_DATE);
				}
			}
			if (deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MATURITY)) {
				schedules = null;
				if (deposito.getInterestSchedules() != null) {
					deposito.getInterestSchedules().clear();
					deposito.setInterestSchedules(null);
				}
			}

			// validate
			validation.required("Placement Date is", deposito.getPlacementDate());
			validation.required("Account No is", deposito.getAccount().getAccountNo());

			if (validation.hasErrors()) {
				Map<String, String> data = new HashMap<String, String>();
				try {
					if (deposito.getUdf() != null) {
						data = json.readValue(deposito.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
					}
				} catch (JsonParseException e) {
					log.error(e.getMessage(), e);
				} catch (JsonMappingException e) {
					log.error(e.getMessage(), e);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
				// udfs =
				// generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);

				if (deposito.getUdf() != null) {
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

				String taskId = "";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_PLACEMENT));
				render("DepositoPlacements/entry.html", mode, deposito, charges, schedules, udfs, taskId, origin);
			} else {
				serializerService.serialize(session.getId(), deposito.getTransactionKey(), deposito);

				confirming(deposito.getTransactionKey(), mode);
			}
		} else {
			list();
		}

	}

	@Check("transaction.canceldepositoplacement")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		String taskId = "";
		boolean confirming = true;
		TdMaster deposito = serializerService.deserialize(session.getId(), id, TdMaster.class);
		String origin = LookupConstants.CANCEL_DEPOSITO_PLACEMENT_ORIGINATED;
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
		if (deposito.getInterestSchedules() != null)
			schedules.addAll(deposito.getInterestSchedules());

		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		// TODO : CHANGE THIS
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

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_PLACEMENT));
		render("DepositoPlacements/entry.html", mode, deposito, schedules, confirming, udfs, taskId, origin);
	}

	@Check("transaction.canceldepositoplacement")
	public static void confirm(TdMaster deposito, List<TdTransactionCharge> charges, TdInterestSchedule[] schedules, List<GnUdfMaster> udfs, String mode) {
		log.debug("confirm. deposito: " + deposito + " charges: " + charges + " schedules: " + schedules + " udfs: " + udfs + " mode: " + mode);

		String origin = LookupConstants.CANCEL_DEPOSITO_PLACEMENT_ORIGINATED;
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

			// deposito.setEffectiveDate(deposito.getPlacementDate());
			// deposito.setChargeable(true);
			deposito.setDepositoTemplate(new GnLookup(LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT));

			TdMaster trx = depositoService.createDeposito(MenuConstants.TD_DEPOSITO_CANCEL_PLACEMENT, deposito, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			log.debug("MODE = " + mode);
			if (trx.getDepositoKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("deposito.cancel.confirmed", trx.getDepositoNo()));
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_PLACEMENT));
				render("DepositoPlacements/detail.html", trx, mode, confirming, origin);
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

	@Check("transaction.canceldepositoplacement")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		String taskId = "";
		TdMaster deposito = serializerService.deserialize(session.getId(), id, TdMaster.class);
		String origin = LookupConstants.CANCEL_DEPOSITO_PLACEMENT_ORIGINATED;
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
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					log.debug(">>> udf field name = " + udf.getFieldName());
					log.debug(">>> udf value = " + udf.getValue());
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

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_CANCEL_PLACEMENT));
		render("DepositoPlacements/entry.html", mode, deposito, schedules, udfs, taskId, origin);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, String processDefinition, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " processDefinition: " + processDefinition + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			TdMaster deposito = depositoService.getMasterDeposito(keyId);
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			TdMaster depositoTemp = json.readValue(maintenanceLog.getNewData(), TdMaster.class);
			TdTransaction trx = depositoService.getTransactionDepositoByTemplate(deposito.getDepositoKey(), LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT);
			String origin = LookupConstants.CANCEL_DEPOSITO_PLACEMENT_ORIGINATED;

			deposito.setCancelDate(depositoTemp.getCancelDate());
			deposito.setRemarksCancel(depositoTemp.getRemarksCancel());

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
			if (trx != null) {
				for (TdTransactionCharge chargeTrans : trx.getDepositoCharges()) {
					BigDecimal taxAmount = chargeTrans.getTaxAmount();
					if (taxAmount == null)
						taxAmount = BigDecimal.ZERO;
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
			// int iAccrualBase =
			// TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());
			// deposito.setAccruedDays(TransactionHelper.calculateAccruedDays(deposito.getPlacementDate(),
			// deposito.getMaturityDate(),iAccrualBase));
			/*
			 * deposito.setAccruedInterest(TransactionHelper.
			 * calculateAccruedInterest(deposito.getAmount(),
			 * deposito.getInterestRate(),
			 * deposito.getSecurity().getAccrualBase().getLookupCode(),
			 * deposito.getSecurity().getYearBase().getLookupCode(),
			 * deposito.getSecurity().getFrequency().getLookupId(),
			 * deposito.getPlacementDate(), deposito.getMaturityDate(),
			 * deposito.getMaturityDate(), false, BigDecimal.ZERO,
			 * BigDecimal.ZERO));
			 */
			
			//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
			Date firstCouponDate = null;
			try {
				firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if (deposito.getInterestAdjust() == null) {
				//BigDecimal accruedInterest = TransactionHelper.calculateAccruedInterest(deposito.getAmount(), deposito.getInterestRate(), deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), deposito.getPlacementDate(), deposito.getMaturityDate(), deposito.getMaturityDate(), false, BigDecimal.ZERO, BigDecimal.ZERO);
				BigDecimal accruedInterest = TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
						   deposito.getInterestRate(), 
						   deposito.getSecurity().getAccrualBase().getLookupCode(), 
						   deposito.getSecurity().getYearBase().getLookupCode(), 
						   deposito.getSecurity().getFrequency().getLookupId(), 
						   deposito.getPlacementDate(), 
						   deposito.getMaturityDate(), 
						   deposito.getMaturityDate(), 
						   firstCouponDate,
						   false, BigDecimal.ZERO, BigDecimal.ZERO, false);
				deposito.setAccruedInterest(accruedInterest);
			} else {
				deposito.setAccruedInterest(deposito.getInterestAdjust());
			}

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

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
				render("DepositoPlacements/approval.html", mode, deposito, schedules, udfs, taskId, from, maintenanceLogKey, operation, keyId, origin);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
				render("DepositoPlacements/approval.html", mode, deposito, schedules, udfs, taskId, from, maintenanceLogKey, operation, keyId, origin);
			}

		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			TdMaster deposito = depositoService.approveCancelDepositoPlacement(MenuConstants.TD_DEPOSITO_CANCEL_PLACEMENT, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, session.get(UIConstants.SESSION_USER_KEY));

			renderJSON(Formatter.resultSuccess(Messages.get("deposito.cancelled.approved", deposito.getDepositoNo())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			TdMaster deposito = depositoService.approveCancelDepositoPlacement(MenuConstants.TD_DEPOSITO_CANCEL_PLACEMENT, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, session.get(UIConstants.SESSION_USER_KEY));

			renderJSON(Formatter.resultSuccess(Messages.get("deposito.cancelled.rejected", deposito.getDepositoNo())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
