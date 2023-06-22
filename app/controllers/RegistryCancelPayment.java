package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import vo.TradeSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.AbstractRgPayment;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgPayment;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class RegistryCancelPayment extends Registry {
	private static Logger log = Logger.getLogger(RegistryCancelPayment.class);

	@Before
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> listType = new ArrayList<SelectItem>();
		listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_REDEMPTION, AbstractRgPayment.BY_TYPE_REDEMPTION));
		listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_SWITCH_OUT, AbstractRgPayment.BY_TYPE_SWITCH_OUT));
		// listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_DIV_BY_REDEEM,
		// AbstractRgPayment.BY_TYPE_DIV_BY_REDEEM));
		listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_DIV_BY_CASH, AbstractRgPayment.BY_TYPE_DIV_BY_CASH));
		// listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_DIV_BY_INVEST,
		// AbstractRgPayment.BY_TYPE_DIV_BY_INVEST));
		renderArgs.put("listType", listType);

		List<SelectItem> cancelBy = new ArrayList<SelectItem>();
		cancelBy.add(new SelectItem(AbstractRgPayment.FILTER_BY_NO_FILTER, "No Filter"));
		cancelBy.add(new SelectItem(AbstractRgPayment.FILTER_BY_NO_TRX, "Transaction No"));
		cancelBy.add(new SelectItem(AbstractRgPayment.FILTER_BY_NO_SWITCHING, "Switching No"));
		cancelBy.add(new SelectItem(AbstractRgPayment.FILTER_BY_NO_PAYMENT, "Payment No"));
		cancelBy.add(new SelectItem(AbstractRgPayment.FILTER_BY_NO_DIVIDEND, "Dividend No"));

		renderArgs.put("cancelBy", cancelBy);
	}

	@Check("transaction.registryCancelPayment")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		RgPayment pay = new RgPayment();
		// pay.setPayDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		pay.setPaymentDate(appDate);
		pay.setAppDate(appDate);
		pay.setType(AbstractRgPayment.BY_TYPE_REDEMPTION);

		List<RgTrade> rgTrades = new ArrayList<RgTrade>();

		render("RegistryCancelPayment/list.html", pay, mode, rgTrades, appDate);
	}

	public static void pagingCancelPayment(Paging page, TradeSearchParameters param) {
		log.debug("pagingCancelPayment. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.getParamFixMap().put("type", param.type);
			page.getParamFixMap().put("cancelBy", param.cancelBy);
			page.getParamFixMap().put("cancelByNo", Long.valueOf(param.cancelByNo));
			page.getParamFixMap().put("productCode", param.productCode);
			page.getParamFixMap().put("paymentDate", param.paymentDate);

			page.addParams("1", page.EQUAL, 1);

			page.addParams(Helper.searchAll("(rt.tradeKey||rt.rgPayment.paymentKey||rt.rgInvestmentaccount.accountNumber||to_char(rt.tradeDate, 'YYYYMMDD')||to_char(rt.paidDate, 'YYYYMMDD')||rt.paidAmt||rt.bankAccount.bankCode.thirdPartyName||rt.bankAccount.name||rt.bankAccount.accountNo)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch().toUpperCase(), 1));
			page = taService.pagingCancelPayment(page);
		}

		renderJSON(page);
	}

	@Check("transaction.registryCancelPayment")
	public static void showTransaction(RgPayment pay) {
		log.debug("showTransaction. pay: " + pay);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		if (pay != null) {
			long cancelByNo = (pay.getCancelByNo() == null || "".equals(pay.getCancelByNo())) ? 0 : Long.valueOf(pay.getCancelByNo());
			List<RgTrade> rgTrades = taService.listCancelPayment(pay.getRgProduct().getProductCode(), fmtYYYYMMDD(pay.getAppDate()), pay.getType(), pay.getCancelBy(), cancelByNo);
			BigDecimal totAmount = new BigDecimal(0);
			for (RgTrade rgTrade : rgTrades) {
				if (rgTrade.getAmount() == null)
					rgTrade.setAmount(new BigDecimal(0));
				totAmount = totAmount.add(rgTrade.getAmount());
			}
			if (!pay.getType().equals(AbstractRgPayment.BY_TYPE_REDEMPTION) || !pay.getType().equals(AbstractRgPayment.BY_TYPE_SWITCH_OUT)) {
				pay.setTotalPaidAmount(totAmount);
			}

			// pay.setTotalPaidAmount(new BigDecimal(0));
			// if (rgTrades != null)
			// for (RgTrade rgTrade : rgTrades) {
			// if (rgTrade.getPaidAmt() == null) rgTrade.setPaidAmt(new
			// BigDecimal(0));
			// pay.setTotalPaidAmount(pay.getTotalPaidAmount().add(rgTrade.getPaidAmt()));
			// logger.debug("RG PAYMENT KEY >>> "
			// +rgTrade.getRgPayment().getPaymentKey());
			// }
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_PAYMENT));
			/*
			 * if(pay.getType().equals(AbstractRgPayment.BY_TYPE_REDEMPTION))
			 * render("RegistryCancelPayment/list.html", pay, mode, rgTrades);
			 * else render("RegistryCancelPayment/list_approval.html", pay,
			 * mode, rgTrades);
			 */
			render("RegistryCancelPayment/list.html", pay, mode, rgTrades);
		} else {
			list();
		}
	}

	@Check("transaction.registryCancelPayment")
	public static void process(RgPayment pay) {
		log.debug("process. pay: " + pay);
		/*
		 * if(pay.getType().equals(AbstractRgPayment.BY_TYPE_REDEMPTION)){
		 * String selected = pay.getSelected(); String[] tradeKeys =
		 * selected.split(","); String[] tradeAmounts =
		 * pay.getSelectedNominal().split(",");
		 * 
		 * pay.setRgTrades(new HashSet<RgTrade>()); pay.setRgPaymentDetails(new
		 * HashSet<RgPaymentDetail>()); for (int i = 0; i < tradeKeys.length;
		 * i++) { RgTrade rgt = new RgTrade(Long.valueOf(tradeKeys[i])); //rgt =
		 * taService.loadTrade(rgt.getTradeKey()); rgt =
		 * taService.loadTrade(Long.valueOf(tradeKeys[i])); RgPaymentDetail
		 * payDetail = new RgPaymentDetail(rgt); logger.debug("tradeAmounts = "
		 * +tradeAmounts[i]); logger.debug("rgt.payDetail.amount = "
		 * +rgt.getRgPaymentDetail().getAmount()); payDetail.setRgPayment(pay);
		 * // payDetail.setAmount(rgt.getRgPaymentDetail().getAmount());
		 * if(!tradeAmounts[i].isEmpty()) { payDetail.setAmount(new
		 * BigDecimal(tradeAmounts[i])); }
		 * 
		 * payDetail.setcancelled(false); payDetail.setRgTrade(rgt);
		 * pay.getRgTrades().add(rgt); pay.getRgPaymentDetails().add(payDetail);
		 * }
		 * 
		 * pay.setAmount(pay.getTotalPaidAmount()); }else{ long cancelByNo =
		 * (pay.getCancelByNo() == null || "".equals(pay.getCancelByNo())) ? 0 :
		 * Long.valueOf(pay.getCancelByNo()); List<RgTrade> rgTrades =
		 * taService.listCancelPayment(pay.getRgProduct().getProductCode(),
		 * fmtYYYYMMDD(pay.getAppDate()), pay.getType(), pay.getCancelBy(),
		 * cancelByNo); BigDecimal totAmount = new BigDecimal(0); for(RgTrade
		 * rgTrade : rgTrades){
		 * logger.debug("trade key:"+rgTrade.getTradeKey());
		 * 
		 * RgTrade rgt = new RgTrade(Long.valueOf(rgTrade.getTradeKey())); //rgt
		 * = taService.loadTrade(rgt.getTradeKey()); rgt =
		 * taService.loadTrade(Long.valueOf(rgTrade.getTradeKey()));
		 * RgPaymentDetail payDetail = new RgPaymentDetail(rgt);
		 * logger.debug("tradeAmounts = " +rgTrade.getAmount());
		 * logger.debug("rgt.payDetail.amount = "
		 * +rgt.getRgPaymentDetail().getAmount()); payDetail.setRgPayment(pay);
		 * // payDetail.setAmount(rgt.getRgPaymentDetail().getAmount());
		 * payDetail.setAmount(rgTrade.getAmount());
		 * 
		 * 
		 * payDetail.setcancelled(false); payDetail.setRgTrade(rgt);
		 * pay.getRgTrades().add(rgt); pay.getRgPaymentDetails().add(payDetail);
		 * totAmount = totAmount.add(rgTrade.getAmount()); }
		 * pay.setAmount(totAmount);
		 * logger.debug("*****************totAmount:"+totAmount); }
		 */

		pay = taService.getProcessCancelPayment(pay);
		String username = session.get(UIConstants.SESSION_USERNAME);
		boolean isSuccess = false;
		try {
			taService.processCancelPayment(MenuConstants.RG_CANCEL_PAYMENT, pay, username, session.get(UIConstants.SESSION_USER_KEY));
			isSuccess = true;
		} catch (MedallionException e) {
			isSuccess = false;
			log.error(e.getMessage(), e);
		} catch (JsonGenerationException e) {
			log.error(e.getMessage(), e);
			isSuccess = false;
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
			isSuccess = false;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			isSuccess = false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			isSuccess = false;
		}

		Map<String, Object> result = new HashMap<String, Object>();
		if (isSuccess) {
			result.put("status", "success");
			result.put("message", Messages.get("cancelPayment.success"));
		} else {
			result.put("status", "failed");
			result.put("message", Messages.get("cancelPayment.failed"));
		}
		renderJSON(result);

		// list();
		// pay = new RgPayment();
		// pay.setPaymentDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		// pay.setType(AbstractRgTrade.TRADETYPE_REDEEM);
		//
		// String mode = UIConstants.DISPLAY_MODE_ENTRY;
		// List<RgTrade> rgTrades = new ArrayList<RgTrade>();
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_PAYMENT));
		// render("RegistryCancelPayment/list.html", pay, mode, rgTrades);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, RgPayment pay, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " pay: " + pay + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
		// List<RgTrade> rgTrades = new ArrayList<RgTrade>();
		try {
			pay = json.readValue(maintenanceLog.getNewData(), RgPayment.class);
			pay.setRgProduct(taService.loadRgProductForPick(pay.getRgProduct().getProductCode()));

			// List<RgPaymentDetail> payDetails =
			// taService.listPaymentDetailByPayment(pay.getPaymentKey());
			pay.setRgTrades(new HashSet<RgTrade>());

			if (AbstractRgPayment.CANCEL_BY_TYPE_SWITCH_OUT.equals(pay.getType())) {
				pay.setType(AbstractRgPayment.BY_TYPE_SWITCH_OUT);
			}
			/*
			 * for (RgPaymentDetail rgPaymentDetail : payDetails) { RgTrade
			 * trade =
			 * taService.loadTrade(rgPaymentDetail.getRgTrade().getTradeKey());
			 * 
			 * pay.setTotalPaidAmount(rgPaymentDetail.getRgPayment().getAmount())
			 * ; pay.setAppDate(trade.getPaidDate());
			 * pay.getRgTrades().add(trade); }
			 */
			// logger.debug("SIZE TRADES = " +pay.getRgTrades().size());
			// for (RgTrade rgTrade : pay.getRgTrades()) {
			// rgTrade.setRgPayment(taService.loadPayment(rgTrade.getPaymentKey()));
			// logger.debug("SIZE >> " +rgTrade.getPaymentKey());
			// pay.getRgTrades().add(rgTrade);
			// }
			RgPayment oldPayment = taService.loadPayment(pay.getPaymentKey());
			if (oldPayment != null) {
				if (oldPayment.getAmount() != null)
					pay.setTotalPaidAmount(oldPayment.getAmount());
				if (oldPayment.getPaymentDate() != null)
					pay.setAppDate(oldPayment.getPaymentDate());
			}

		} catch (JsonParseException e) {
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		// List<RgTrade> rgTrades = new ArrayList<RgTrade>(pay.getRgTrades());
		List<RgTrade> rgTrades = new ArrayList<RgTrade>();
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		String flek = "approve";
		render("RegistryCancelPayment/approval.html", taskId, keyId, pay, rgTrades, mode, maintenanceLogKey, from, flek);
	}

	public static void paging(Paging page, String id) {
		log.debug("paging. page: " + page + " id: " + id);

		try {
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(Long.parseLong(id));
			RgPayment pay = json.readValue(maintenanceLog.getNewData(), RgPayment.class);
			page.addParams("1", page.EQUAL, 1);
			page.addParams("pd.rgPayment.paymentKey ", page.EQUAL, pay.getPaymentKey());

			page.addParams(Helper.searchAll("(pd.rgPayment.paymentKey||pd.rgTrade.tradeKey||pd.rgTrade.rgInvestmentaccount.accountNumber||" + "to_char(pd.rgTrade.tradeDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||to_char(pd.rgTrade.paidDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')" + "||pd.rgTrade.paidAmt||pd.rgTrade.bankAccount.bankCode.thirdPartyName||pd.rgTrade.bankAccount.name||pd.rgTrade.bankAccount.accountNo)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = taService.pagingApprovalCancelPayment(page);
		} catch (JsonParseException e) {
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		log.debug("json ---> " + page);
		renderJSON(page);
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			String[] arr = taService.approveCancelPayment(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
			if ("1".equals(arr[1])) {
				throw new MedallionException(ExceptionConstants.INVALID_DATA + ".error.sentToFaInterface");
			}

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

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			log.debug("TaskID >>> " + taskId);
			String[] arr = taService.approveCancelPayment(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);
			if ("1".equals(arr[1])) {
				throw new MedallionException(ExceptionConstants.INVALID_DATA + ".error.sentToFaInterface");
			}
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

	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit(RgTrade rgTrade) {
		log.debug("edit. rgTrade: " + rgTrade);
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}
}
