package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.DepositoSearchParameter;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnCalendar;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.TdInterestSchedule;
import com.simian.medallion.model.TdMaster;
import com.simian.medallion.model.comparators.InterestScheduleComparator;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.TransactionHelper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class DepositoUpdates extends Registry {
	private static Logger log = Logger.getLogger(DepositoUpdates.class);
	
      private final static double AVERAGE_MILLIS_PER_MONTH = 365.24 * 24 * 60 * 60 * 1000 / 12;

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
                renderArgs.put("operators", operators);	
	}

	@Before(only = { "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		// List<SelectItem> maturityIns =
		// generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants._DEPOSITO_TYPE);
		List<SelectItem> maturityIns = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITO_TYPE);
		renderArgs.put("maturityIns", maturityIns);

		renderArgs.put("paymentFreqMonthly", LookupConstants.INTEREST_FREQUENCY_MONTHLY);
		
		renderArgs.put("maturityInsFullRedeem", LookupConstants.DEPOSITO_TYPE_FULL_REDEEM);
		
		GnLookup lookupAro = generalService.getLookup(LookupConstants.DEPOSITO_TYPE_FULL_ARO);
		renderArgs.put("maturityInsFullARO", LookupConstants.DEPOSITO_TYPE_FULL_ARO);
		renderArgs.put("maturityInsFullARODesc", lookupAro.getLookupCode() +"-"+ lookupAro.getLookupDescription());

	}

	@Check("transaction.depositoupdate")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_UPDATE));
		render();
	}

	@Check("transaction.depositoupdate")
	public static void search(Paging page, DepositoSearchParameter param) {
		log.debug("search. page: " + page + " param: " + param);

		if (!param.customerCode.isEmpty())
			page.addParams("tm.account.customer.customerKey", page.EQUAL, Long.parseLong(param.customerCode));
		page.addParams("tm.security.securityType.securityType", page.EQUAL, param.securityType);
		if (!param.securityCode.isEmpty())
			page.addParams("tm.security.securityKey", page.EQUAL, Long.parseLong(param.securityCode));
		page.addParams("tm.depositoNo", param.depositoNoOperator, UIHelper.withOperator(param.depositoNo, param.depositoNoOperator));
		page.addParams("trim(tm.depositoStatus)", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
		page.addParams("trim(tm.recordStatus)", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
		page.addParams("tm.amount", page.GREAT, BigDecimal.ZERO);
		page.addParams(Helper.searchAll("(tm.depositoNo||tm.account.accountNo||tm.account.name||tm.security.securityType.securityType||" + "tm.security.securityId||tm.amount||to_char(tm.effectiveDate, '" + Helper.dateOracle(appProp.getDateFormat()) + "')" + "||to_char(tm.maturityDate, '" + Helper.dateOracle(appProp.getDateFormat()) + "')||tm.interestFrequency.lookupDescription)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		page = depositoService.pagingDepositoUpdate(page);
		renderJSON(page);
	}

	@Check("transaction.depositoupdate")
	public static void populatePaymentSchedule(Long depositoKey, Date placementDate, Date maturityDate, BigDecimal nominal, BigDecimal interestRate, String accrualBase, String yearBase, Integer totalCoupon, boolean considerHoliday, String freqSecurity, int totalPaid) {
		log.debug("populatePaymentSchedule. depositoKey: " + depositoKey + " placementDate: " + placementDate + " maturityDate: " + maturityDate + " nominal: " + nominal + " interestRate: " + interestRate + " accrualBase: " + accrualBase + " yearBase: " + yearBase + " totalCoupon: " + totalCoupon + " considerHoliday: " + considerHoliday + " freqSecurity: " + freqSecurity + " totalPaid: " + totalPaid);

		String frequency = LookupConstants.FREQUENCY_MONTH;
		totalCoupon = totalCoupon + 1;
		List<TdInterestSchedule> schedules = depositoService.populatePaymentSchedule(placementDate, maturityDate, nominal, interestRate, accrualBase, yearBase, frequency, totalCoupon, considerHoliday, freqSecurity);
		for (int i = 0; i < totalPaid; i++) {
			schedules.remove(0);
		}
		
		Date firstCouponDate = null;
		try {
			firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<TdInterestSchedule> schedulesFromDb = depositoService.listInterestSchedule(depositoKey);
		int iAccrualBase = TransactionHelper.decodeAccrualBase(accrualBase);
		for (TdInterestSchedule tdInterestSchedule : schedulesFromDb) {
			if (tdInterestSchedule.getId().getPaymentNo() < (totalPaid+1)){
				tdInterestSchedule.setDays(TransactionHelper.calculateAccruedDaysPortoNew(tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), firstCouponDate.getDate(), iAccrualBase));
//				tdInterestSchedule.setDays(TransactionHelper.calculateAccruedDaysPorto(tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), tdInterestSchedule.getEndDate(), iAccrualBase));
				//tdInterestSchedule.setGrossInterest(TransactionHelper.calculateAccruedInterest(nominal, interestRate, accrualBase, yearBase, freqSecurity, tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), tdInterestSchedule.getEndDate(), false, BigDecimal.ZERO, BigDecimal.ZERO));
				tdInterestSchedule.setGrossInterest(tdInterestSchedule.getInterestPaid());
				tdInterestSchedule.setPaid(true);
				schedules.add(tdInterestSchedule);
			}
		}

		Date endDate = null;
		Collections.sort(schedules, new Comparator<TdInterestSchedule>() {
			@Override
			public int compare(TdInterestSchedule arg0, TdInterestSchedule arg1) {
				Date b0 = arg0.getStartDate();
				Date b1 = arg1.getStartDate();
				
				if ((b0.compareTo(b1) == 0)) return 0;
				if ((b0.compareTo(b1) < 1)) return -1;
				if ((b0.compareTo(b1) > 1)) return 1;
				
				if (!b0.equals(b1)) return (b0.compareTo(b1));
				
				return 0;
			}
		});
		
		for (TdInterestSchedule tdInterestSchedule : schedules) {
			log.debug("[2] PAYMENT NO | END DATE = " + tdInterestSchedule.getId().getPaymentNo() + " | " +fmtYYYYMMDD(tdInterestSchedule.getEndDate()));
			if(endDate != null){
				if(!tdInterestSchedule.getStartDate().equals(endDate)){
					tdInterestSchedule.setStartDate(endDate);
				}
			}
			endDate = tdInterestSchedule.getEndDate();
		}
		render("DepositoUpdates/gridPaymentSchedules.html", schedules);
	}
	
	@Check("transaction.depositoupdate")
	public static void entry() {
		log.debug("entry. ");
	}

	@Check("transaction.depositoupdate")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		TdMaster deposito = depositoService.getMasterDeposito(id);

		//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
		Date firstCouponDate = null;
		try {
			firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		deposito.setUpdateDate(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID));
		deposito.setCountLastRollover(depositoService.countLastRollover(id));
		
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>(deposito.getInterestSchedules());
		Collections.sort(schedules, new InterestScheduleComparator());
		
		deposito.setNoOfPayment(deposito.getInterestSchedules().size());
		int iAccrualBase = TransactionHelper.decodeAccrualBase(deposito.getSecurity().getAccrualBase().getLookupCode());
		int paymentNo = 0;
		
		log.debug("Consider holiday = " +deposito.getConsiderHoliday());
		for (TdInterestSchedule tdInterestSchedule : schedules) {
			
//			tdInterestSchedule.setDays(TransactionHelper.calculateAccruedDaysPorto(tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), tdInterestSchedule.getEndDate(), iAccrualBase));
			tdInterestSchedule.setDays(TransactionHelper.calculateAccruedDaysPortoNew(tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), firstCouponDate.getDate(), iAccrualBase));

				if (tdInterestSchedule.getInterestPaid()!=null){
				tdInterestSchedule.setGrossInterest(tdInterestSchedule.getInterestPaid());
				tdInterestSchedule.setPaid(true);
				deposito.setLastPaymentDate(tdInterestSchedule.getPaymentDate());
				paymentNo = (tdInterestSchedule.getId().getPaymentNo());
			} else {
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
				tdInterestSchedule.setPaid(false);
			}

		}

		if (!schedules.isEmpty()) {
			// Artinya ambil paymentDate row pertama yg blom paid
			if (schedules.size() == paymentNo) {
				deposito.setNextPaymentDate(schedules.get(paymentNo - 1).getPaymentDate());
			} else {
				deposito.setNextPaymentDate(schedules.get(paymentNo).getPaymentDate());
			}
		}

		deposito.setTotalPaidSchedule(paymentNo);
		if (!schedules.isEmpty()) {
			if (deposito.getTotalPaidSchedule() < 0) {
				deposito.setNextPaymentDate(schedules.get(0).getPaymentDate());
			}
		}

		// if
		// (deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MATURITY)){
		/*
		 * BigDecimal accruedInterest =
		 * TransactionHelper.calculateAccruedInterest(deposito.getAmount(),
		 * deposito.getInterestRate(),
		 * deposito.getSecurity().getAccrualBase().getLookupCode(),
		 * deposito.getSecurity().getYearBase().getLookupCode(),
		 * deposito.getSecurity().getFrequency().getLookupId(),
		 * deposito.getEffectiveDate(), deposito.getMaturityDate(),
		 * deposito.getMaturityDate(), false, BigDecimal.ZERO, BigDecimal.ZERO);
		 * 
		 * deposito.setAccruedInterest(accruedInterest);
		 */
		if (deposito.getInterestAdjust() == null) {
			//BigDecimal accruedInterest = TransactionHelper.calculateAccruedInterest(deposito.getAmount(), deposito.getInterestRate(), deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), deposito.getEffectiveDate(), deposito.getMaturityDate(), deposito.getMaturityDate(), false, BigDecimal.ZERO, BigDecimal.ZERO);
			BigDecimal accruedInterest = TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), deposito.getInterestRate(), deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), deposito.getEffectiveDate(), deposito.getMaturityDate(), deposito.getMaturityDate(), firstCouponDate, false, BigDecimal.ZERO, BigDecimal.ZERO, false);

			deposito.setAccruedInterest(accruedInterest);

		} else {
			deposito.setAccruedInterest(deposito.getInterestAdjust());
		}
			
//		}
		
		if(deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MONTHLY)){
			BigDecimal selisihMonth = new BigDecimal( monthsBetween(deposito.getMaturityDate(), deposito.getEffectiveDate()));
			selisihMonth = selisihMonth.setScale(0, RoundingMode.HALF_UP);
			
			Calendar newMaturityDate = Calendar.getInstance();
			newMaturityDate.setTime(deposito.getMaturityDate());
			
			newMaturityDate.add(Calendar.MONTH, selisihMonth.abs().intValueExact());
			deposito.setMaturityDateHidden(newMaturityDate.getTime());
		}
		
		
		deposito.setChargeableTam(deposito.getChargeable());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_UPDATE));
		render("DepositoUpdates/entry.html", deposito, schedules, mode);
		
	}
	
	public static double monthsBetween(Date d1, Date d2) {
	    return (d2.getTime() - d1.getTime()) / AVERAGE_MILLIS_PER_MONTH; 
	}

	@Check("transaction.depositoupdate")
	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	@Check("transaction.depositoupdate")
	public static void save(TdMaster deposito, TdInterestSchedule[] schedules, String mode) {
		log.debug("save. deposito: " + deposito + " schedules: " + schedules + " mode: " + mode);

		if (deposito != null && schedules != null) {
			deposito.getInterestSchedules().clear();
			for (TdInterestSchedule schedule : schedules) {
				deposito.getInterestSchedules().add(schedule);
			}
		}

		if (deposito != null) {
			Date currentDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			validation.required("Update as of is", deposito.getUpdateDate());

			if (fmtYYYYMMDD(deposito.getUpdateDate()).compareTo(fmtYYYYMMDD(currentDate)) > 0) {
				validation.addError("", "Update as of can not greater than Application Date");
			}

			if (deposito.getNewMaturityDate() != null) {
				if (fmtYYYYMMDD(deposito.getUpdateDate()).compareTo(fmtYYYYMMDD(deposito.getNewMaturityDate())) > 0) {
					validation.addError("", "Update as of can not greater than Maturity Date");
				}
			} else {
				if (fmtYYYYMMDD(deposito.getUpdateDate()).compareTo(fmtYYYYMMDD(deposito.getMaturityDate())) > 0) {
					validation.addError("", "Update as of can not greater than Maturity Date");
				}
			}
			if (deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MATURITY)) {
				if (fmtYYYYMMDD(deposito.getUpdateDate()).compareTo(fmtYYYYMMDD(deposito.getEffectiveDate())) < 0) {
					validation.addError("", "Update as of can not less than Effective Date");
				}
			} else {
				if (fmtYYYYMMDD(deposito.getUpdateDate()).compareTo(fmtYYYYMMDD(deposito.getLastPaymentDate())) < 0) {
					validation.addError("", "Update as of can not less than Last Payment Date");
				}
			}

			if (deposito.isChangeMaturityDate())
				validation.required("Maturity Date is", deposito.getNewMaturityDate());

			if (deposito.isChangeInterestRate())
				validation.required("Interest Rate is", deposito.getNewInterestRate());

			if (deposito.isChangeAccruedInterest())
				validation.required("Interest (Gross) is", deposito.getNewAccruedInterest());

			if (deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MATURITY)) {
				if (deposito.isChangeMaturityIns())
					validation.required("Maturity Instruction is", deposito.getNewMaturityInstruction().getLookupId());
				if (deposito.getNewMaturityInstruction() != null) {
					if (!deposito.getNewMaturityInstruction().getLookupId().isEmpty()) {
						if (!deposito.getNewMaturityInstruction().getLookupId().equals(LookupConstants.DEPOSITO_TYPE_FULL_REDEEM)) {
							validation.required("Next Maturity Date is", deposito.getNextMaturityDate());
							validation.required("Next Interest Rate is", deposito.getNextInterestRate());
						}
					}
				}
			}
			List<GnCalendar> holidays = generalService.listHolidays(UIConstants.SIMIAN_BANK_ID);
			Date maturityDate = new Date();
			if (deposito.getMaturityDate() != null) {
				maturityDate = deposito.getMaturityDate();
			}
			if (deposito.getNewMaturityDate() != null) {
				maturityDate = deposito.getNewMaturityDate();
			}
			if (maturityDate != null) {
				boolean isSameMaturityDate = false;
				if (schedules != null) {
					for (TdInterestSchedule schedule : schedules) {
						if (maturityDate.getTime() == schedule.getEndDate().getTime()) {
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
			List<String> endDateSchedule = new ArrayList<String>();
			if (deposito.getConsiderHoliday() != null) {
				if (deposito.getConsiderHoliday() == true) {
					if (schedules != null) {
						for (TdInterestSchedule schedule : schedules) {
							for (GnCalendar gnCalendar : holidays) {
								if (schedule.getInterestPaid() == null) {
									if (gnCalendar.getId().getCalendarDate().getTime() == (schedule.getEndDate().getTime())) {
										endDateSchedule.add(dateFormat.format(schedule.getEndDate()));
										//
									}
								}
							}
						}
					}
				}
			}

			if (!endDateSchedule.isEmpty()) {
				validation.addError("", Messages.get(ExceptionConstants.END_DATE_PAYMENT_SCHEDULE_HOLIDAY, endDateSchedule));
			}
			validation.required("Account No in tab Bank Info is", deposito.getBankAccount().getAccountNo());
			if (!deposito.getSecurity().getCurrency().getCurrencyCode().equals(deposito.getBankAccount().getCurrency().getCurrencyCode())) {
				validation.addError("", Messages.get(ExceptionConstants.BANK_ACCOUNT_CURRENCY_SHOULD_SAME, deposito.getSecurity().getCurrency().getCurrencyCode()));
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_UPDATE));
				render("DepositoUpdates/entry.html", mode, deposito, schedules);
			} else {
				serializerService.serialize(session.getId(), deposito.getDepositoKey(), deposito);
				confirming(deposito.getDepositoKey(), mode);
			}
		} else {
			list();
		}

	}

	@Check("transaction.depositoupdate")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		boolean confirming = true;
		TdMaster deposito = serializerService.deserialize(session.getId(), id, TdMaster.class);
		log.debug("Payment Freq COnfirming = " + deposito.getInterestFrequency().getLookupId());
		Set<TdInterestSchedule> schedules = deposito.getInterestSchedules();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_UPDATE));
		render("DepositoUpdates/entry.html", mode, deposito, schedules, confirming);
	}

	@Check("transaction.depositoupdate")
	public static void confirm(TdMaster deposito, TdInterestSchedule[] schedules, String mode) {
		log.debug("confirm. deposito: " + deposito + " schedules: " + schedules + " mode: " + mode);

		if (deposito != null && schedules != null) {
			deposito.getInterestSchedules().clear();
			for (TdInterestSchedule schedule : schedules) {
				deposito.getInterestSchedules().add(schedule);
			}
		}
		boolean confirming = true;
		// deposito.setEffectiveDate(deposito.getPlacementDate());
		try {
			deposito.setChargeableTam(null);
			TdMaster trx = depositoService.createDeposito(MenuConstants.TD_DEPOSITO_UPDATE, deposito, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));

			if (trx.getDepositoKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("deposito.confirmed.successful.edit", trx.getDepositoNo()));
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_PLACEMENT));
				render("DepositoPlacements/detail.html", trx, mode, confirming);
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

	@Check("transaction.depositoupdate")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		TdMaster deposito = serializerService.deserialize(session.getId(), id, TdMaster.class);
		List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>();
		if (deposito.getInterestSchedules() != null) {
			for (TdInterestSchedule tdInterestSchedule : deposito.getInterestSchedules()) {
				schedules.add(tdInterestSchedule);
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_UPDATE));
		render("DepositoUpdates/entry.html", mode, deposito, schedules);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, String processDefinition, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " processDefinition: " + processDefinition + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenance = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			TdMaster deposito = json.readValue(maintenance.getNewData(), TdMaster.class);
			ScMaster security = securityService.getSecurity(deposito.getSecurity().getSecurityKey());
			List<TdInterestSchedule> schedules = new ArrayList<TdInterestSchedule>(deposito.getInterestSchedules());
			Collections.sort(schedules, new InterestScheduleComparator());
			
			//selain FI, firstCouponDate di-hardcode menjadi 1 Jan 1900
			Date firstCouponDate = null;
			try {
				firstCouponDate = Helper.parse("01/01/1900", "dd/MM/yyyy");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			deposito.setNoOfPayment(deposito.getInterestSchedules().size());
			deposito.setSecurity(security);
			int iAccrualBase = TransactionHelper.decodeAccrualBase(security.getAccrualBase().getLookupCode());
			int paymentNo = 0;
			for (TdInterestSchedule tdInterestSchedule : schedules) {
//				tdInterestSchedule.setDays(TransactionHelper.calculateAccruedDaysPorto(tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), tdInterestSchedule.getEndDate(), iAccrualBase));
				tdInterestSchedule.setDays(TransactionHelper.calculateAccruedDaysPortoNew(tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), firstCouponDate.getDate(), iAccrualBase));

				if (tdInterestSchedule.getInterestPaid() != null) {
					tdInterestSchedule.setGrossInterest(tdInterestSchedule.getInterestPaid());
					tdInterestSchedule.setPaid(true);
					deposito.setLastPaymentDate(tdInterestSchedule.getPaymentDate());
					paymentNo = (tdInterestSchedule.getId().getPaymentNo());
				} else {
					BigDecimal interestRate = (deposito.getNewInterestRate() == null ? deposito.getInterestRate() : deposito.getNewInterestRate());
					//tdInterestSchedule.setGrossInterest(TransactionHelper.calculateAccruedInterest(deposito.getAmount(), interestRate, deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), tdInterestSchedule.getStartDate(), tdInterestSchedule.getEndDate(), tdInterestSchedule.getEndDate(), false, BigDecimal.ZERO, BigDecimal.ZERO));
					tdInterestSchedule.setGrossInterest(TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
							   interestRate, 
							   deposito.getSecurity().getAccrualBase().getLookupCode(), 
							   deposito.getSecurity().getYearBase().getLookupCode(), 
							   deposito.getSecurity().getFrequency().getLookupId(), 
							   tdInterestSchedule.getStartDate(), 
							   tdInterestSchedule.getEndDate(), 
							   tdInterestSchedule.getEndDate(), 
							   firstCouponDate,
							   false, BigDecimal.ZERO, BigDecimal.ZERO, false));
					tdInterestSchedule.setDays(TransactionHelper.getAccruedDays());
					tdInterestSchedule.setPaid(false);
				}
			}

			if (!schedules.isEmpty()) {
				// Artinya ambil paymentDate row pertama yg blom paid
				if (schedules.size() == paymentNo) {
					deposito.setNextPaymentDate(schedules.get(paymentNo - 1).getPaymentDate());
				} else {
					deposito.setNextPaymentDate(schedules.get(paymentNo).getPaymentDate());
				}
			}

			deposito.setTotalPaidSchedule(paymentNo);
			if (deposito.getInterestFrequency().getLookupId().equals(LookupConstants.INTEREST_FREQUENCY_MATURITY)) {
				if (deposito.getInterestAdjust() == null) {
					//BigDecimal accruedInterest = TransactionHelper.calculateAccruedInterest(deposito.getAmount(), deposito.getInterestRate(), deposito.getSecurity().getAccrualBase().getLookupCode(), deposito.getSecurity().getYearBase().getLookupCode(), deposito.getSecurity().getFrequency().getLookupId(), deposito.getEffectiveDate(), deposito.getMaturityDate(), deposito.getMaturityDate(), false, BigDecimal.ZERO, BigDecimal.ZERO);
					BigDecimal accruedInterest = TransactionHelper.calculateAccruedInterestNew(deposito.getAmount(), 
							deposito.getInterestRate(), 
							deposito.getSecurity().getAccrualBase().getLookupCode(), 
							deposito.getSecurity().getYearBase().getLookupCode(), 
							deposito.getSecurity().getFrequency().getLookupId(), 
							deposito.getEffectiveDate(), 
							deposito.getMaturityDate(), 
							deposito.getMaturityDate(),
							firstCouponDate,
							false, BigDecimal.ZERO, BigDecimal.ZERO, false);
					
					deposito.setAccruedInterest(accruedInterest);
				} else {
					deposito.setAccruedInterest(deposito.getInterestAdjust());
				}
				/*
				 * BigDecimal accruedInterest =
				 * TransactionHelper.calculateAccruedInterest
				 * (deposito.getAmount(), deposito.getInterestRate(),
				 * deposito.getSecurity().getAccrualBase().getLookupCode(),
				 * deposito.getSecurity().getYearBase().getLookupCode(),
				 * deposito.getSecurity().getFrequency().getLookupId(),
				 * deposito.getEffectiveDate(), deposito.getMaturityDate(),
				 * deposito.getMaturityDate(), false, BigDecimal.ZERO,
				 * BigDecimal.ZERO);
				 * 
				 * deposito.setAccruedInterest(accruedInterest);
				 */
				// deposito.setAccruedInterest(deposito.getInterestAdjust());
			}

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}

			render("DepositoUpdates/approval.html", mode, deposito, schedules, taskId, from, maintenanceLogKey, operation, keyId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			TdMaster deposito = depositoService.approveDepositoUpdate(MenuConstants.TD_DEPOSITO_PLACEMENT, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, null, session.get(UIConstants.SESSION_USER_KEY));

			renderJSON(Formatter.resultSuccess(Messages.get("deposito.approved", deposito.getDepositoNo())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			TdMaster deposito = depositoService.approveDepositoUpdate(MenuConstants.TD_DEPOSITO_PLACEMENT, session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, null, session.get(UIConstants.SESSION_USER_KEY));

			renderJSON(Formatter.resultSuccess(Messages.get("deposito.rejected", deposito.getDepositoNo())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}