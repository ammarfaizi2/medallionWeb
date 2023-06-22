package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import play.mvc.Before;
import play.mvc.With;
import vo.CashProjectionParam;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.helper.Helper;

@With(Secure.class)
public class CashForeCastingProcess extends MedallionController {
	public static Logger log = Logger.getLogger(CashForeCastingProcess.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		renderArgs.put("operators", UIHelper.stringOperators());
	}

	@Check("transaction.processCashForecasting")
	public static void list(CashProjectionParam param) {
		log.debug("list. param: " + param);

		if (param == null)
			param = new CashProjectionParam();
 
		Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		param.setAppDate(Helper.formatDMY(appDate));

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_CASH_FORECASTING));
		param.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.BN_CASH_FORECASTING + param.getSessionTag());
		param.setProcessMark(params.get(PROCESSMARK));
		render("CashForeCastingProcess/list.html", param);
	}


	@Check("transaction.processCashForecasting")
	public static void processAjax(CashProjectionParam param) {
		log.debug("processAjax. param: " + param);


	}

	@Check("transaction.processCashForecasting")
	public static void entry(String mode) {
		log.debug("entry. mode: " + mode);

	}

	@Check("transaction.processCashForecasting")
	public static void edit(String mode) {
		log.debug("edit. mode: " + mode);
	}

	@Check("transaction.processCashForecasting")
	public static void view(String mode) {
		log.debug("view. mode: " + mode);
	}
	
	@Check("transaction.processCashForecasting")
	public static void processAjaxLog(CashProjectionParam param) {
		log.debug("processAjaxLog. param: " + param);
		
		GnSystemParameter paramrange= generalService.getSystemParameter(SystemParamConstants.PARAM_RANGE_DATE_CASH_PROJECTION_MAX_SCREEN);
		int rangeDate = (paramrange != null) ? Integer.parseInt(paramrange.getValue()) : 0;
		Date dateToCheck = generalService.getWorkingDate(param.getFromDate(), rangeDate);
		
		Map<String, Object> result = new HashMap<String, Object>();
		if(!Helper.isGreaterYMD(param.getToDate(),dateToCheck)){
			/**/
			try{
				transactionService.processCashProjection(param.getCustomerKey(), param.getFromDate(), param.getToDate(), false, false);
				result.put("message", "Process Successfully");
				result.put("success", "1");
			}catch (MedallionException ex) {
				result.put("message", ex.getMessage());
				result.put("success", "0");
			}catch (Exception e) {
				result.put("message", e.getMessage());
				result.put("success", "0");
			}
		}else{
			result.put("success", "0");
			result.put("message", "Range date process must be "+rangeDate+" days");
		}
		renderJSON(result);
	}

}
