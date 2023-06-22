package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class CashProjectionProcess extends MedallionController {
	public static Logger log = Logger.getLogger(CashProjectionProcess.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");
		List<SelectItem> yesNo = UIHelper.yesNoOperators();
		renderArgs.put("yesNo", yesNo);
		
		String sentMailCashProjection = generalService.getSystemParameter(SystemParamConstants.PARAM_SENT_MAIL_CASH_PROJECTION).getValue().trim();
		renderArgs.put("sentMailCashProjection", sentMailCashProjection);
		renderArgs.put("operators", UIHelper.stringOperators());
	}

	@Check("transaction.processCashProjection")
	public static void list(CashProjectionParam param) {
		log.debug("list. param: " + param);

		if (param == null)
			param = new CashProjectionParam();

		Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		param.setAppDate(Helper.formatDMY(appDate));

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_CASH_PROJECTION));
		param.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.BN_CASH_PROJECTION + param.getSessionTag());
		param.setProcessMark(params.get(PROCESSMARK));
		render("CashProjectionProcess/list.html", param);
	}


	@Check("transaction.processCashProjection")
	public static void processAjax(CashProjectionParam param) {
		log.debug("processAjax. param: " + param);


	}

	@Check("transaction.processCashProjection")
	public static void entry(String mode) {
		log.debug("entry. mode: " + mode);

	}

	@Check("transaction.processCashProjection")
	public static void edit(String mode) {
		log.debug("edit. mode: " + mode);
	}

	@Check("transaction.processCashProjection")
	public static void view(String mode) {
		log.debug("view. mode: " + mode);
	}
	
	@Check("transaction.processCashProjection")
	public static void processAjaxLog(CashProjectionParam param) {
		log.debug("processAjaxLog. param: " + param);
		
		GnSystemParameter paramrange= generalService.getSystemParameter(SystemParamConstants.PARAM_RANGE_DATE_CASH_PROJECTION_MAX_SCREEN);
		int rangeDate = (paramrange != null) ? Integer.parseInt(paramrange.getValue()) : 0;
		Date dateToCheck = generalService.getWorkingDate(param.getFromDate(), rangeDate);
		
		Map<String, Object> result = new HashMap<String, Object>();
		log.debug("--------------------------dateToCheck:"+dateToCheck);
		log.debug("--------------------------param.getToDate():"+param.getToDate());
		if(!Helper.isGreaterYMD(param.getToDate(),dateToCheck)){
			/**/
			try{
				transactionService.processCashProjection(param.getCustomerKey(), param.getFromDate(), param.getToDate(), false, param.getSentMail());
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
