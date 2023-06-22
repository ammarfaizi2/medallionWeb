package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.DepositoryInterestPaymentProcessParameters;
import vo.DepositoryMaturityProcessParameters;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ReturnParam;
import com.simian.medallion.model.TdInterestSchedule;
import com.simian.medallion.model.TdInterestScheduleId;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class DepositoInterestPaymentProcess extends MedallionController {
	private static Logger log = Logger.getLogger(DepositoInterestPaymentProcess.class);

	@Before()
	@Check("transaction.depositointerestpaymentprocess")
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);
	}

	@Check("transaction.depositointerestpaymentprocess")
	public static void reset(DepositoryInterestPaymentProcessParameters param) {
		log.debug("reset. param: " + param);

		list(null);
	}

	@Check("transaction.depositointerestpaymentprocess")
	public static void back(DepositoryInterestPaymentProcessParameters param) {
		log.debug("back. param: " + param);

		param.setIds(null);
		list(param);
	}

	@Check("transaction.depositointerestpaymentprocess")
	public static void list(DepositoryInterestPaymentProcessParameters param) {
		log.debug("list. param: " + param);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_INTEREST_PAYMENT));
		if (param == null) {
			param = new DepositoryInterestPaymentProcessParameters();
			param.setAll(true);
			param.setPaymentDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		}
		param.setDispatch(DepositoryMaturityProcessParameters.DISPATCH_LIST);
		render("DepositoInterestPaymentProcess/list.html", param);
	}

	@Check("transaction.depositointerestpaymentprocess")
	public static void process(DepositoryInterestPaymentProcessParameters param) {
		log.debug("process. param: " + param);

		param.setDispatch(DepositoryMaturityProcessParameters.DISPATCH_PROCESS);

		render("DepositoInterestPaymentProcess/list.html", param);
	}

	@Check("transaction.depositointerestpaymentprocess")
	public static void pagingDeposito(Paging page, DepositoryInterestPaymentProcessParameters param) {
		log.debug("pagingDeposito. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		param.setPaymentDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));

		String dbformate = Helper.dateOracle(appProp.getDateFormat());
		if (param.isQuery()) {
			log.debug("Parameters : " + param.toString());

			page.getParamFixMap().put("ids", param.getIds());

			if (!param.isAll()) {
				page.addParams("tx.deposito.account.customer.customerNo", Paging.EQUAL, param.getCfMaster().getCustomerNo());
			}
			page.addParams("tx.deposito.security.securityType.securityType", Paging.EQUAL, param.getScTypeMaster().getSecurityType());
			page.addParams("tx.deposito.security.securityId", Paging.EQUAL, param.getScMaster().getSecurityId());
			page.addParams("tx.deposito.depositoNo", param.getDepositNoSign(), UIHelper.withOperator(param.getDepositNo(), param.getDepositNoSign()));
			page.addParams("tx.paymentDate", Paging.LESSEQUAL, param.getPaymentDate());
			page.addParams(Helper.searchAll("(to_char(tx.paymentDate, '" + dbformate + "')||tx.deposito.depositoNo||tx.id.paymentNo||tx.deposito.account.accountNo||tx.deposito.account.name||tx.deposito.security.securityType.securityType||" + "tx.deposito.security.securityId||tx.deposito.amount||to_char(tx.startDate, '" + dbformate + "')||" + "to_char(tx.endDate, '" + dbformate + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = depositoService.pagingDepositoInterest(page);
		}
		renderJSON(page);
	}

	@Check("transaction.depositointerestpaymentprocess")
	public static void confirmDepositos(String[] ids) {
		log.debug("confirmDepositos. ids: " + ids);

		ReturnParam returnParam = new ReturnParam();
		String username = session.get(UIConstants.SESSION_USERNAME);
		String userKey = session.get(UIConstants.SESSION_USER_KEY);

		List<TdInterestScheduleId> scheduleIds = new ArrayList<TdInterestScheduleId>();

		for (String id : ids) {
			String[] keys = id.split("-");
			scheduleIds.add(new TdInterestScheduleId(Long.valueOf(keys[0]), Integer.valueOf(keys[1])));
		}

		for (TdInterestSchedule tdInterestSchedule : depositoService.getInterestSchedules(scheduleIds)) {
			try {
				depositoService.processInterestSchedule(tdInterestSchedule, username, userKey);

				returnParam.getMessages().add("Save Success");
				returnParam.getKeys().add(tdInterestSchedule.getId().getDepositoKey() + "-" + tdInterestSchedule.getId().getPaymentNo());
			} catch (MedallionException ex) {
				log.error(ex.getMessage(), ex);

				List<String> params = new ArrayList<String>();
				if (ex != null && ex.getParams() != null) {
					for (Object param : ex.getParams()) {
						params.add(Messages.get(param));
					}
				}

				returnParam.getMessages().add(Messages.get("error." + ex.getErrorCode(), params.toArray()));
				returnParam.getKeys().add(tdInterestSchedule.getId().getDepositoKey() + "-" + tdInterestSchedule.getId().getPaymentNo());
			} catch (Exception e) {
				log.error(e.getMessage(), e);

				returnParam.getMessages().add(e.getMessage());
				returnParam.getKeys().add(tdInterestSchedule.getId().getDepositoKey() + "-" + tdInterestSchedule.getId().getPaymentNo());
			}
		}

		returnParam.setGlobal("Process Finish");

		renderJSON(returnParam);
	}
}