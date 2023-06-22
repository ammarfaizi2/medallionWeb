package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.DepositoryMaturityProcessParameters;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsTransactionCharge;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ReturnParam;
import com.simian.medallion.model.TdMaster;
import com.simian.medallion.model.TdTransactionCharge;
import com.simian.medallion.model.TdTransactionChargeId;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class DepositoMaturityProcess extends MedallionController {
	private static Logger log = Logger.getLogger(DepositoMaturityProcess.class);

	@Before()
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);
	}

	@Check("transaction.depositmaturityprocess")
	public static void reset(DepositoryMaturityProcessParameters param) {
		log.debug("reset. param: " + param);

		list(null);
	}

	@Check("transaction.depositmaturityprocess")
	public static void back(DepositoryMaturityProcessParameters param) {
		log.debug("back. param: " + param);

		param.setDepositoKey(null);
		list(param);
	}

	@Check("transaction.depositmaturityprocess")
	public static void list(DepositoryMaturityProcessParameters param) {
		log.debug("list. param: " + param);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_MATURITY_PROCESS));
		if (param == null) {
			param = new DepositoryMaturityProcessParameters();
			param.setAll(false);
			param.setMaturityDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		}
		param.setDispatch(DepositoryMaturityProcessParameters.DISPATCH_LIST);
		param.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.TD_DEPOSITO_MATURITY_PROCESS + param.getSessionTag());
		param.setProcessMark(params.get(PROCESSMARK));

		render("DepositoMaturityProcess/list.html", param);
	}

	@Check("transaction.depositmaturityprocess")
	public static void listdemo(DepositoryMaturityProcessParameters param) {
		log.debug("listdemo. param: " + param);

		if (param == null) {
			param = new DepositoryMaturityProcessParameters();
			param.setAll(true);
			param.setMaturityDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		}
		param.setDispatch(DepositoryMaturityProcessParameters.DISPATCH_LIST);

		render("DepositoMaturityProcess/listdemo.html", param);
	}

	@Check("transaction.depositmaturityprocess")
	public static void process(DepositoryMaturityProcessParameters param) {
		log.debug("process. param: " + param);

		String sessionUuidX = session.get(PROCESSMARK + MenuConstants.TD_DEPOSITO_MATURITY_PROCESS + param.getSessionTag());
		if (sessionUuidX == null) {
			session.put(PROCESSMARK + MenuConstants.TD_DEPOSITO_MATURITY_PROCESS + param.getSessionTag(), param.getProcessMark());
			sessionUuidX = session.get(PROCESSMARK + MenuConstants.TD_DEPOSITO_MATURITY_PROCESS + param.getSessionTag());
		}

		if (isDoubleSubmission(MenuConstants.TD_DEPOSITO_MATURITY_PROCESS + param.getSessionTag())) {
			list(null);
		}
		param.setDispatch(DepositoryMaturityProcessParameters.DISPATCH_PROCESS);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_MATURITY_PROCESS));
		param.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.TD_DEPOSITO_MATURITY_PROCESS + param.getSessionTag());
		param.setProcessMark(params.get(PROCESSMARK));
		render("DepositoMaturityProcess/list.html", param);
	}

	@Check("transaction.depositmaturityprocess")
	public static void pagingDeposito(Paging page, DepositoryMaturityProcessParameters param) {
		log.debug("pagingDeposito. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		param.setMaturityDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));

		String dbformate = Helper.dateOracle(appProp.getDateFormat());
		if (param.isQuery()) {
			log.debug("Parameters : " + param.toString());

			page.getParamFixMap().put("depositoKeys", param.getDepositoKey());

			if (!param.isAll()) {
				page.addParams("tx.account.customer.customerNo", Paging.EQUAL, param.getCfMaster().getCustomerNo());
			}
			page.addParams("tx.security.securityType.securityType", Paging.EQUAL, param.getScTypeMaster().getSecurityType());
			page.addParams("tx.security.securityId", Paging.EQUAL, param.getScMaster().getSecurityId());
			page.addParams("tx.depositoNo", param.getDepositNoSign(), UIHelper.withOperator(param.getDepositNo(), param.getDepositNoSign()));
			page.addParams("tx.maturityDate", Paging.LESSEQUAL, param.getMaturityDate());
			page.addParams(Helper.searchAll("(to_char(tx.maturityDate, '" + dbformate + "')||tx.maturityInstruction.lookupDescription||tx.depositoNo||" + "tx.account.accountNo||tx.account.name||tx.security.securityType.securityType||tx.security.securityId||" + "to_char(tx.effectiveDate, '" + dbformate + "')||tx.amount||" + "to_char(tx.nextMaturityDate, '" + dbformate + "')||tx.nextInterestRate||tx.security.description)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = depositoService.pagingDeposito(page);
		}
		renderJSON(page);
	}

	public static List<TdTransactionCharge> convertCharges(List<CsTransactionCharge> transactionCharges) {
		log.debug("convertCharges. transactionCharges: " + transactionCharges);

		List<TdTransactionCharge> depositoCharges = new ArrayList<TdTransactionCharge>();

		for (CsTransactionCharge csTransactionCharge : transactionCharges) {
			TdTransactionCharge depositoCharge = new TdTransactionCharge();

			depositoCharge.setId(new TdTransactionChargeId());
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
		return depositoCharges;
	}

	@Check("transaction.depositmaturityprocess")
	public static void confirmDepositos(Long[] depositoKeys) {
		log.debug("confirmDepositos. depositoKeys: " + depositoKeys);

		ReturnParam returnParam = new ReturnParam();
		String username = session.get(UIConstants.SESSION_USERNAME);
		String userKey = session.get(UIConstants.SESSION_USER_KEY);

		for (TdMaster tdMaster : depositoService.getMasterDepositos(depositoKeys)) {
			try {
				depositoService.processMaturity(tdMaster, username, userKey);

				returnParam.getMessages().add("Save Success");
				returnParam.getKeys().add(tdMaster.getDepositoKey().toString());
			} catch (MedallionException ex) {
				log.error(ex.getMessage(), ex);

				List<String> params = new ArrayList<String>();

				if (ex != null && ex.getParams() != null) {
					for (Object param : ex.getParams()) {
						params.add(Messages.get(param));
					}
					returnParam.getMessages().add(Messages.get("error." + ex.getErrorCode(), params.toArray()));
				} else {
					returnParam.getMessages().add(Messages.get(ex.getErrorCode()));
				}

				returnParam.getKeys().add(tdMaster.getDepositoKey().toString());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				if (e.getMessage() != null)
					returnParam.getMessages().add(e.getMessage());
				else
					returnParam.getMessages().add("Failed to execute Deposito Maturity Process");
				returnParam.getKeys().add(tdMaster.getDepositoKey().toString());
			}
		}

		returnParam.setGlobal("Process Finish");

		renderJSON(returnParam);
	}
}