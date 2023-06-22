package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.DepositoRollbackProcessParameters;
import vo.DepositoryMaturityProcessParameters;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ReturnParam;
import com.simian.medallion.model.TdTransaction;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class DepositoRollbackProcess extends MedallionController {
	private static Logger log = Logger.getLogger(DepositoRollbackProcess.class);

	@Before()
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);
	}

	@Check("transaction.depositorollbackprocess")
	public static void reset(DepositoRollbackProcessParameters param) {
		log.debug("reset. param: " + param);

		list(null);
	}

	@Check("transaction.depositorollbackprocess")
	public static void back(DepositoRollbackProcessParameters param) {
		log.debug("back. param: " + param);

		param.setTransactionKey(null);
		list(param);
	}

	@Check("transaction.depositorollbackprocess")
	public static void list(DepositoRollbackProcessParameters param) {
		log.debug("list. param: " + param);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_ROLLBACK));
		if (param == null) {
			param = new DepositoRollbackProcessParameters();
			param.setAll(false);
			param.setMaturityDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		}
		param.setDispatch(DepositoryMaturityProcessParameters.DISPATCH_LIST);

		render("DepositoRollbackProcess/list.html", param);
	}

	@Check("transaction.depositorollbackprocess")
	public static void process(DepositoRollbackProcessParameters param) {
		log.debug("process. param: " + param);

		param.setDispatch(DepositoryMaturityProcessParameters.DISPATCH_PROCESS);

		render("DepositoRollbackProcess/list.html", param);
	}

	@Check("transaction.depositorollbackprocess")
	public static void pagingDeposito(Paging page, DepositoRollbackProcessParameters param) {
		log.debug("pagingDeposito. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		param.setMaturityDate(new Date(param.getMaturityDateMili()));

		String dbformate = Helper.dateOracle(appProp.getDateFormat());
		if (param.isQuery()) {
			log.debug("Parameters : " + param.toString());

			page.getParamFixMap().put("transactionKeys", param.getTransactionKey());

			if (!param.isAll()) {
				page.addParams("tx.deposito.account.customer.customerNo", Paging.EQUAL, param.getCfMaster().getCustomerNo());
			}
			page.addParams("tx.security.securityType.securityType", Paging.EQUAL, param.getScTypeMaster().getSecurityType());
			page.addParams("tx.security.securityId", Paging.EQUAL, param.getScMaster().getSecurityId());
			page.addParams("tx.deposito.depositoNo", param.getDepositNoSign(), UIHelper.withOperator(param.getDepositNo(), param.getDepositNoSign()));
			page.addParams("tx.transactionDate", Paging.EQUAL, param.getMaturityDate());
			page.addParams(Helper.searchAll("(to_char(tx.maturityDate, '" + dbformate + "')||tx.deposito.depositoNo||tx.deposito.maturityInstruction.lookupDescription||tx.deposito.account.accountNo||" + "tx.deposito.account.name||tx.security.securityType.securityType||" + "tx.security.securityId||to_char(tx.transactionDate, '" + dbformate + "')||tx.amount||tx.security.description)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = depositoService.pagingDepositoRollback(page);
		}
		renderJSON(page);
	}

	private static Boolean validateDeposito(Long depositoKey) {
		log.debug("validateDeposito. depositoKey: " + depositoKey);

		return depositoService.validateDeposito(depositoKey);
	}

	@Check("transaction.depositorollbackprocess")
	public static void confirmDepositos(Long[] transactionKeys) {
		log.debug("confirmDepositos. transactionKeys: " + transactionKeys);

		ReturnParam returnParam = new ReturnParam();
		String username = session.get(UIConstants.SESSION_USERNAME);
		String userKey = session.get(UIConstants.SESSION_USER_KEY);

		for (TdTransaction tdTransaction : depositoService.getTdTransactions(transactionKeys)) {
			try {
				if (!validateDeposito(tdTransaction.getDeposito().getDepositoKey())) {
					returnParam.getMessages().add("Deposito already processed to break");
					returnParam.getKeys().add(tdTransaction.getTransactionKey().toString());
				} else {
					depositoService.processRollback(tdTransaction, username, userKey);
					returnParam.getMessages().add("Save Success");
					returnParam.getKeys().add(tdTransaction.getTransactionKey().toString());
				}
			} catch (MedallionException ex) {
				log.error(ex.getMessage(), ex);

				List<String> params = new ArrayList<String>();
				if (ex != null && ex.getParams() != null) {
					for (Object param : ex.getParams()) {
						params.add(Messages.get(param));
					}
				}

				returnParam.getMessages().add(Messages.get("error." + ex.getErrorCode(), params.toArray()));
				returnParam.getKeys().add(tdTransaction.getTransactionKey().toString());
			} catch (Exception e) {
				log.error(e.getMessage(), e);

				returnParam.getMessages().add(e.getMessage());
				returnParam.getKeys().add(tdTransaction.getTransactionKey().toString());
			}
		}

		returnParam.setGlobal("Process Finish");

		renderJSON(returnParam);
	}

}