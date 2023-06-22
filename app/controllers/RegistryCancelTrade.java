package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import helpers.serializers.BankAccountPickSerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import vo.RegistryCancelTradeSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.AbstractRgFundAction;
import com.simian.medallion.model.AbstractRgPayment;
import com.simian.medallion.model.AbstractRgSwitching;
import com.simian.medallion.model.AbstractRgTrade;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.GnThirdPartyBank;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgFundAction;
import com.simian.medallion.model.RgInvestmentaccount;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgNavId;
import com.simian.medallion.model.RgPortfolio;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgSwitching;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.RgTransaction;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class RegistryCancelTrade extends Registry {
	private static Logger log = Logger.getLogger(RegistryCancelTrade.class);

	private static final GnLookup subscriptionTopUp = generalService.getLookup(generalService.getValueParam(SystemParamConstants.PARAM_SUBSCRIPTION_TYPE_TOP_UP));
	private static final GnLookup subscriptionNew = generalService.getLookup(generalService.getValueParam(SystemParamConstants.PARAM_SUBSCRIPTION_TYPE_NEW));
	public static final String TRADETYPEONSCREEN_SUBSCRIPTION = "Subscription";
	private static final BigDecimal BD_100 = new BigDecimal(100);
	public static final String UNIT_PER_UNIT = "UNIT_PER_UNIT";
	public static final String TOTAL_AMOUNT = "TOTAL_AMOUNT";
	public static final String APPROVAL_CANCEL_SWITCHING = "approvalCancelSwitching";
	private static final String FROM_CANCEL_TRADE_APPROVAL = "cancelTradeApp";

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> listType = new ArrayList<SelectItem>();
		listType.add(new SelectItem(AbstractRgTrade.TRADETYPE_SUBSCRIBE, TRADETYPEONSCREEN_SUBSCRIPTION));
		listType.add(new SelectItem(AbstractRgTrade.TRADETYPE_REDEEM, AbstractRgPayment.BY_TYPE_REDEMPTION));
		listType.add(new SelectItem(AbstractRgSwitching.TYPE_SWITCHING, AbstractRgSwitching.TYPE_SWITCHING));
		listType.add(new SelectItem(AbstractRgFundAction.TYPE_DIVIDEND, AbstractRgFundAction.TYPE_DIVIDEND));
		renderArgs.put("listType", listType);

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);
	}

	@Before(only = { "approval", "approvalCancelSwitching", "approvalCancelFa", "cancelSubscription", "cancelRedemption", "cancelSwitching", "cancelDividend", "confirming", "confirm", "back", "data" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> listType = new ArrayList<SelectItem>();
		listType.add(new SelectItem(AbstractRgTrade.TRADETYPE_SUBSCRIBE, TRADETYPEONSCREEN_SUBSCRIPTION));
		listType.add(new SelectItem(AbstractRgTrade.TRADETYPE_REDEEM, AbstractRgPayment.BY_TYPE_REDEMPTION));
		listType.add(new SelectItem(AbstractRgSwitching.TYPE_SWITCHING, AbstractRgSwitching.TYPE_SWITCHING));
		listType.add(new SelectItem(AbstractRgFundAction.TYPE_DIVIDEND, AbstractRgFundAction.TYPE_DIVIDEND));
		renderArgs.put("listType", listType);

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> inputBy = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._INPUT_BY);
		renderArgs.put("inputBy", inputBy);

		renderArgs.put("subscriptiontype_topup_val", subscriptionTopUp.getLookupCode());
		renderArgs.put("subscriptiontype_new_val", subscriptionNew.getLookupCode());
		renderArgs.put("subscriptiontype_topup_descr", subscriptionTopUp.getLookupDescription());
		renderArgs.put("subscriptiontype_new_descr", subscriptionNew.getLookupDescription());

		List<SelectItem> transactionBy = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TRANSACTION_BY);
		renderArgs.put("transactionBy", transactionBy);

		renderArgs.put("redemptionOperators", UIHelper.redeemOperators());

		// FOR DIVIDEN
		renderArgs.put("dividendType", UIHelper.dividenTypes());
		renderArgs.put("roundingType", UIHelper.roundingOperators());
		renderArgs.put("inputByOptions", UIHelper.inputByOperators());
		renderArgs.put("ratioPerUnitByOptions", UIHelper.ratioPerUnitByOptions());
		renderArgs.put("totAmount", LookupConstants.DIVIDEN_BY_1);
		// renderArgs.put("factor",LookupConstants.DIVIDEN_BY_2);
		renderArgs.put("amountUnit", LookupConstants.DIVIDEN_BY_3);

		List<SelectItem> dividenBy = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._DIVIDEN_BY);
		renderArgs.put("dividenBy", dividenBy);

		renderArgs.put("redemptionOperators", UIHelper.redeemSwitchOperators());
		renderArgs.put("subscriptionSwitchOperators", UIHelper.subscriptionSwitchOperators());

		renderArgs.put("switchingType", generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._SWITCHING_TYPE));

		List<SelectItem> outTradeBy = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TRANSACTION_BY);
		renderArgs.put("outTradeBy", outTradeBy);

		List<SelectItem> inTradeBy = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._INPUT_BY);
		renderArgs.put("inTradeBy", inTradeBy);
	}

	@Check("transaction.registryCancelTrade")
	public static void list() {
		log.debug("list. ");
		// String mode = UIConstants.DISPLAY_MODE_ENTRY;
		// RgTrade rg = new RgTrade();
		// rg.setType(RgTrade.TRADETYPE_SUBSCRIBE);
		// rg.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER);
		// rg.setTradeDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		// rg.setRedemRefKey(null);
		// render("RegistryCancelTrade/list.html", mode, rg);

		RegistryCancelTradeSearchParameters param = new RegistryCancelTradeSearchParameters();
		param.setRgProduct(new RgProduct());
		param.setType(RgTrade.TRADETYPE_SUBSCRIBE);
		param.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER);
		param.setRedemRefKey(null);
		param.setDispatch(RegistryCancelTradeSearchParameters.DISPATCH_VIEW);

		render("RegistryCancelTrade/list.html", param);
	}

	@Check("transaction.registryCancelTrade")
	public static void pagingCancelTradeRedeem(Paging page, RegistryCancelTradeSearchParameters param) {
		log.debug("pagingCancelTradeRedeem. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		if (param.isQuery()) {
			// page.addParams("rt.posted", page.EQUAL, 1);
			page.addParams("rt.cancelled", page.EQUAL, 0);
			page.addParams("rt.type", page.EQUAL, param.getType());
			page.addParams("rt.tradeDate", page.GREATEQUAL, param.getTradeDateFrom());
			page.addParams("rt.tradeDate", page.LESSEQUAL, param.getTradeDateTo());

			if (param.getRgProduct() != null) {
				page.addParams("rt.rgProduct.productCode", page.EQUAL, param.getRgProduct().getProductCode());
			}

			page.addParams("rt.tradeKey", param.getTransactionNoOperator(), UIHelper.withOperator(param.getRedemRefKey(), param.getTransactionNoOperator()));
			page.addParams(Helper.searchAll("rt.tradeKey || rt.rgProduct.productCode || rt.rgInvestmentaccount.accountNumber || " + "rt.rgInvestmentaccount.name || rt.tradeStatus || rt.unit || rt.price || " + "rt.amount||" + "to_char(rt.tradeDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = taService.pagingCancelTradeRedeem(page);
		}
		renderJSON(page);
	}

	@Check("transaction.registryCancelTrade")
	public static void pagingCancelTradeSwitching(Paging page, RegistryCancelTradeSearchParameters param) {
		log.debug("pagingCancelTradeSwitching. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		if (param.isQuery()) {
			page.addParams("rs.cancelled", page.EQUAL, 0);
			page.addParams("rs.type", page.EQUAL, param.getType());
			page.addParams("rs.switchDate", page.GREATEQUAL, param.getTradeDateFrom());
			page.addParams("rs.switchDate", page.LESSEQUAL, param.getTradeDateTo());
			page.addParams("rs.tradeStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);

			if (param.getRgProduct() != null) {
				page.getParamFixMap().put("productCode", param.getRgProduct().getProductCode());
				// page.addParams("rs.rgInvestmentaccountByOutAccountNumber.rgProduct.productCode",
				// page.EQUAL, param.getRgProduct().getProductCode());
			}

			page.addParams("rs.switchingKey", param.getTransactionNoOperator(), UIHelper.withOperator(param.getRedemRefKey(), param.getTransactionNoOperator()));
			page.addParams(Helper.searchAll("rs.switchingKey || rs.tradeStatus"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page.getParamFixMap().put("redemRefKey", param.getRedemRefKey());

			page = taService.pagingCancelTradeSwitching(page);
		}
		renderJSON(page);
	}

	@Check("transaction.registryCancelTrade")
	public static void pagingCancelTradeDividend(Paging page, RegistryCancelTradeSearchParameters param) {
		log.debug("pagingCancelTradeDividend. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		if (param.isQuery()) {
			page.addParams("rf.cancelled", page.EQUAL, 0);
			page.addParams("rf.type", page.EQUAL, param.getType());
			page.addParams("rf.divDate", page.GREATEQUAL, param.getTradeDateFrom());
			page.addParams("rf.divDate", page.LESSEQUAL, param.getTradeDateTo());
			page.addParams("rf.status", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);

			if (param.getRgProduct() != null) {
				page.addParams("rf.rgProduct.productCode", page.EQUAL, param.getRgProduct().getProductCode());
			}

			page.addParams("rf.fundActionKey", param.getTransactionNoOperator(), UIHelper.withOperator(param.getRedemRefKey(), param.getTransactionNoOperator()));
			page.addParams(Helper.searchAll("rf.fundActionKey || rf.status || rf.amount || rf.divDate"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page.getParamFixMap().put("redemRefKey", param.getRedemRefKey());

			page = taService.pagingCancelTradeDividend(page);
		}
		renderJSON(page);
	}

	public static void showTransaction(RgTrade rg) {
		log.debug("showTransaction. rg: " + rg);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;

		String productCode = rg.getRgProduct().getProductCode();
		String type = rg.getType();
		String transDate = fmtYYYYMMDD(rg.getTradeDate());
		Long transNo = rg.getRedemRefKey();
		rg.setSelected(type);

		List<RgTrade> rgTrades = new ArrayList<RgTrade>();
		List<RgSwitching> rgSwitchings = new ArrayList<RgSwitching>();
		List<RgFundAction> rgFundActions = new ArrayList<RgFundAction>();
		BigDecimal priceSubRed = BigDecimal.ZERO;
		if ((AbstractRgTrade.TRADETYPE_REDEEM.equals(type)) || (AbstractRgTrade.TRADETYPE_SUBSCRIBE.equals(type))) {
			rgTrades = taService.listCancelTrade(productCode, type, transDate, transNo);
			for (RgTrade rgTrade : rgTrades) {
				if (rgTrade.getRgTransaction() != null) {
					priceSubRed = rgTrade.getRgTransaction().getPrice();
				} else {
					priceSubRed = rgTrade.getPrice();
				}
			}
		}

		BigDecimal amountForSwitching = BigDecimal.ZERO;
		BigDecimal priceForSwitching = BigDecimal.ZERO;
		String fundCodeTo = "";
		if ((AbstractRgSwitching.TYPE_SWITCHING.equals(type))) {
			rgSwitchings = taService.listCancelSwitching(productCode, type, transDate, transNo);
			for (RgSwitching rgSwitching : rgSwitchings) {
				RgTrade inTrade = taService.loadTrade(rgSwitching.getInTradeKey());
				if (inTrade != null)
					fundCodeTo = inTrade.getRgProduct().getProductCode();

				RgTrade outTrade = taService.loadTrade(rgSwitching.getOutTradeKey());
				if (outTrade != null)
					amountForSwitching = outTrade.getAmount();

				RgTransaction trans = taService.loadTransaction(rgSwitching.getOutTradeKey());
				if (trans != null) {
					priceForSwitching = trans.getPrice();
				} else {
					priceForSwitching = outTrade.getPrice();
				}
			}
		}

		BigDecimal navPrice = BigDecimal.ZERO;
		if ((AbstractRgFundAction.TYPE_DIVIDEND.equals(type))) {
			rgFundActions = taService.listCancelFundAction(productCode, type, transDate, transNo);
			for (RgFundAction rgFundAction : rgFundActions) {
				RgNav nav = taService.loadActiveNav(rgFundAction.getRgProduct().getProductCode(), rgFundAction.getNavDate());
				navPrice = nav.getNav();
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
		render("RegistryCancelTrade/list.html", mode, rg, rgTrades, priceSubRed, rgSwitchings, rgFundActions, navPrice, amountForSwitching, priceForSwitching, fundCodeTo, navPrice);
	}

	public static void cancelTransaction(RgTrade rg) {
		log.debug("cancelTransaction. rg: " + rg);

		/*
		 * if (rg.getRemark().indexOf(String.valueOf(rg.getTradeKey())) < 0) {
		 * String remark = rg.getRemark() + "," + rg.getTradeKey();
		 * rg.setRemark(constractId(remark)); }
		 */

		String username = session.get(UIConstants.SESSION_USERNAME);
		String userKey = session.get(UIConstants.SESSION_USER_KEY);

		// TYPE SUBSCRIBE / REDEMPTION
		if ((AbstractRgTrade.TRADETYPE_SUBSCRIBE.equals(rg.getType())) || (AbstractRgTrade.TRADETYPE_REDEEM.equals(rg.getType()))) {
			rg = taService.processCancelTradeSubRedeem(rg, username, userKey);
		}

		// TYPE SWITCHING
		if (AbstractRgSwitching.TYPE_SWITCHING.equals(rg.getType())) {
			RgSwitching swi = new RgSwitching();
			swi.setSwitchingKey(rg.getTradeKey());
			taService.processCancelSwitching(swi, rg, username, userKey);
		}

		// TYPE DIVIDEND / FUND ACTION
		if (AbstractRgFundAction.TYPE_DIVIDEND.equals(rg.getType())) {
			RgFundAction fa = new RgFundAction();
			fa.setFundActionKey(rg.getTradeKey());
			taService.processCancelFundAction(fa, rg, username, userKey);
		}

		// start delay 3 seconds
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		// end delay

		list();
	}

	public static void approval(String taskId, Long keyId, RgTrade rg, String from, String type) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " rg: " + rg + " from: " + from + " type: " + type);

		if (type.equals(AbstractRgTrade.TRADETYPE_REDEMPTION) || type.equals(AbstractRgTrade.TRADETYPE_SUBSCRIPTION)) {
			log.debug("masuk sini");
			String mode = UIConstants.DISPLAY_MODE_VIEW;

			rg = taService.loadTrade(keyId);
			if (rg != null) {

				RgTrade cancelTrade = taService.loadTradeByCancelTrade(rg.getTradeKey());

				if (AbstractRgTrade.TRADETYPE_SUBSCRIBE.equals(rg.getType())) {
					rg.setRgProduct(rg.getRgInvestmentaccount().getRgProduct());
					rg.setTaxPct(rg.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
					rg.setTaxAmt(getTaxAmt(rg));
					rg.setTrnSABranch(generalService.getThirdParty(rg.getTrnSABranchKey()));
					rg.setCancelTradeDate(cancelTrade.getTradeDate());
					rg.setCancelPostDate(cancelTrade.getPostDate());
					rg.setCancelRemark(cancelTrade.getRemark());

					if (from.equals(UIConstants.WORKFLOW_FROM)) {
						flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
					} else {
						flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
					}
					RgTrade sub = rg;
					from = FROM_CANCEL_TRADE_APPROVAL;
					render("RegistryCancelTrade/approvalCancelSubscription.html", rg, sub, mode, taskId, from);
				}

				if (AbstractRgTrade.TRADETYPE_REDEEM.equals(rg.getType())) {

					rg.setCancelTradeDate(cancelTrade.getTradeDate());
					rg.setCancelPostDate(cancelTrade.getPostDate());
					rg.setCancelRemark(cancelTrade.getRemark());
					RgTrade red = rg;
					if (from.equals(UIConstants.WORKFLOW_FROM)) {
						flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
					} else {
						flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
					}
					String feeby = "";
					String isCancel = "isCancel";
					if (red.getFeePct() != null)
						red.setFeePercent(true);
					render("RegistryCancelTrade/approvalCancelRedemption.html", rg, red, mode, taskId, from, feeby, isCancel);
				}
			}
		}

		if (type.equals(AbstractRgTrade.TRADETYPE_SWITCHING)) {
			RgSwitching swt = null;
			approvalCancelSwitching(taskId, keyId, swt, from);
		}

		if (type.equals(AbstractRgTrade.TRADETYPE_DIVIDEND)) {
			RgFundAction fa = null;
			approvalCancelFa(taskId, keyId, fa, from);
		}
	}

	public static void approvalCancelSwitching(String taskId, Long keyId, RgSwitching swt, String from) {
		log.debug("approvalCancelSwitching. taskId: " + taskId + " keyId: " + keyId + " swt: " + swt + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		// swi = taService.loadSwitching(keyId);
		// RgTrade rg = taService.loadTrade(swi.getOutTradeKey());
		// RgTrade inTrade = taService.loadTrade(swi.getInTradeKey());
		// String fundCodeTo = inTrade.getRgProduct().getProductCode();
		// BigDecimal amountForSwitching = rg.getAmount();
		// BigDecimal priceForSwitching = BigDecimal.ZERO;
		// RgTransaction trans =
		// taService.loadTransaction(swi.getOutTradeKey());
		// if (trans != null) priceForSwitching = trans.getPrice();
		//
		// rg.setType(AbstractRgSwitching.TYPE_SWITCHING);
		// // List<RgSwitching> rgSwitchings =
		// taService.listSwitchingByCancel(keyId);
		// List<RgSwitching> rgSwitchings = taService.listSwitchingById(keyId);
		// logger.debug("SIZE = " +rgSwitchings.size());
		//
		// if (from.equals(UIConstants.WORKFLOW_FROM)) {
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		// } else {
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		// }
		// render("RegistryCancelTrade/approvalCancelSwitching.html", rg,
		// rgSwitchings, mode, taskId,from, amountForSwitching,
		// priceForSwitching, fundCodeTo, keyId);

		swt = taService.loadSwitching(keyId);
		if (swt != null) {
			RgSwitching canSwi = taService.loadSwitchingByCancelSwitching(swt.getSwitchingKey());

			if (canSwi != null) {
				swt.setCancelTradeDate(canSwi.getSwitchDate());
				swt.setCancelPostDate(canSwi.getPostDate());
				swt.setCancelRemark(canSwi.getRemarks());
			}

			swt.splitRgTrade();

			RgInvestmentaccount rgInvest = new RgInvestmentaccount();
			if (swt.getRgInvestmentaccountByOutAccountNumber() != null) {
				rgInvest = swt.getRgInvestmentaccountByOutAccountNumber();
			}

			if (swt.getRgInvestmentaccountByInAccountNumber() != null && swt.getRgInvestmentaccountByOutAccountNumber() == null) {
				rgInvest = swt.getRgInvestmentaccountByInAccountNumber();
			}

			CfMaster customer = customerService.getCustomer(rgInvest.getCustomer().getCustomerKey());
			swt.setCustomer(customer);
			swt.setInvestor(customer);
			swt.setThirdPartyByFundManager(rgInvest.getRgProduct().getThirdPartyByFundManager());
			swt.setSaCode(rgInvest.getThirdPartyBySaCode().getThirdPartyCode());

			if (swt.getRgInvestmentaccountByOutAccountNumber() != null) {
				if (swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct() != null) {
					swt.getOut().setTaxPct(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getTaxMasterByRedTaxKey().getTaxRate());
					swt.setThirdPartyByFundManager(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getThirdPartyByFundManager());
					swt.setNavDate(swt.getOut().getNavDate());
					swt.setPostDate(swt.getOut().getPostDate());
					swt.setGoodfundDate(swt.getOut().getGoodfundDate());
					swt.setPaymentDate(swt.getOut().getPaymentDate());
					swt.setOutThirdPartyByFundManager(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getThirdPartyByFundManager());
					swt.setExternalReference(swt.getOut().getExternalReference());
				}
			}

			if (swt.getRgInvestmentaccountByInAccountNumber() != null) {
				if (swt.getRgInvestmentaccountByInAccountNumber().getRgProduct() != null) {
					swt.getIn().setTaxPct(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
					swt.setThirdPartyByFundManager(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getThirdPartyByFundManager());
					swt.setNavDate(swt.getIn().getNavDate());
					swt.setPostDate(swt.getIn().getPostDate());
					swt.setGoodfundDate(swt.getIn().getGoodfundDate());
					swt.setPaymentDate(swt.getIn().getPaymentDate());
					swt.setInThirdPartyByFundManager(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getThirdPartyByFundManager());
					swt.setExternalReference(swt.getIn().getExternalReference());
				}
			}

			if (swt.getRgInvestmentaccountByOutAccountNumber() != null) {
				if (swt.getOut().getRgProductBnAccount() != null) {
					BnAccount prodBankAccount = bankAccountService.getBankAccount(swt.getOut().getRgProductBnAccount().getBankAccountKey());
					swt.getOut().setRgProductBnAccount(prodBankAccount);
				}
			}

			if (swt.getRgInvestmentaccountByInAccountNumber() != null) {
				if (swt.getIn().getRgProductBnAccount() != null) {
					BnAccount prodBankAccount = bankAccountService.getBankAccount(swt.getIn().getRgProductBnAccount().getBankAccountKey());
					swt.getIn().setRgProductBnAccount(prodBankAccount);
				}
			}

			if (swt.getExternalProduct() != null) {
				GnThirdParty externalProduct = generalService.getThirdParty(swt.getExternalProduct().getThirdPartyKey());
				swt.setExternalProduct(externalProduct);
			}

			if (swt.getExternalProductBank() != null) {
				GnThirdPartyBank externalProductBank = generalService.getThirdPartyBank(swt.getExternalProductBank().getThirdPartyBankKey());
				swt.setExternalProductBank(externalProductBank);
			}

			if (swt.getOut() != null) {
				if (swt.getOut().getRgProductBnAccount() != null) {
					BnAccount bnAccount = bankAccountService.getBankAccount(swt.getOut().getRgProductBnAccount().getBankAccountKey());
					swt.getOut().setRgProductBnAccount(bnAccount);
				}
			}

			if (swt.getIn() != null) {
				if (swt.getIn().getRgProductBnAccount() != null) {
					BnAccount bnAccount = bankAccountService.getBankAccount(swt.getIn().getRgProductBnAccount().getBankAccountKey());
					swt.getIn().setRgProductBnAccount(bnAccount);
				}
			}

			if (swt.getOut() != null) {
				swt.setGoodfundDate(swt.getOut().getGoodfundDate());
				swt.setNavDate(swt.getOut().getNavDate());
				swt.setPostDate(swt.getOut().getPostDate());
				swt.setPaymentDate(swt.getOut().getPaymentDate());
				swt.setSwitchAll(swt.getOut().getLiquidation());
				BigDecimal outstandingUnit = taService.loadOutstanding(swt.getOut().getRgProduct().getProductCode(), swt.getOut().getRgInvestmentaccount().getAccountNumber(), swt.getGoodfundDate());
				swt.getOut().setAvailabelUnit(outstandingUnit);

				if (swt.getRgInvestmentaccountByOutAccountNumber() != null && swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getTaxMasterByRedTaxKey() != null) {
					swt.getOut().setTaxPct(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getTaxMasterByRedTaxKey().getTaxRate());
				}
				swt.getOut().setTrnSABranch(generalService.getThirdParty(swt.getOut().getTrnSABranchKey()));

				if (swt.getRgInvestmentaccountByInAccountNumber() != null && swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getTaxMasterBySubTaxKey() != null) {
					swt.getIn().setTaxPct(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
				}

				if (swt.getIn() != null) {
					swt.getIn().setTrnSABranchKey(swt.getOut().getTrnSABranchKey());
					swt.getIn().setTrnSABranch(generalService.getThirdParty(swt.getIn().getTrnSABranchKey()));
				}
			}

			log.debug("[APPROVAL] SwitchingType = > " + swt.getSwitchingType());
			if (swt.getOut() != null && swt.getOut().getFeePct() != null)
				swt.getOut().setFeePercent(true);
			if (swt.getIn() != null && swt.getIn().getFeePct() != null)
				swt.getIn().setFeePercent(true);

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}

			String type = APPROVAL_CANCEL_SWITCHING;

			render("RegistryCancelTrade/approvalCancelSwitching.html", swt, mode, taskId, from, type);
		}
	}

	public static void approvalCancelFa(String taskId, Long keyId, RgFundAction fa, String from) {
		log.debug("approvalCancelFa. taskId: " + taskId + " keyId: " + keyId + " fa: " + fa + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;

		log.info("OPTIMIZE START loadFundAction----------------------------");
		fa = taService.loadFundAction(keyId);
		log.info("OPTIMIZE END loadFundAction----------------------------");
		if (fa != null) {
			log.info("OPTIMIZE START loadFundActionByCancelFundAction----------------------------");
			RgFundAction canFa = taService.loadFundActionByCancelFundActionOnly(fa.getFundActionKey());
			log.info("OPTIMIZE END loadFundActionByCancelFundAction----------------------------");
			if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_1))
				fa.setTotAmount(fa.getAmount());
			// if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_2))
			// fa.setFactorAmount(fa.getAmount());
			if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_3))
				fa.setUnitAmount(fa.getAmount());

			log.info("OPTIMIZE START listTrades----------------------------");
			// List<RgTrade> trades = listTrades(fa);
			List<RgTrade> trades = new ArrayList<RgTrade>();
			log.info("OPTIMIZE END listTrades----------------------------");
			if (AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) {
				log.info("OPTIMIZE START loadNav----------------------------");
				RgNav rgNav = taService.loadNav(new RgNavId(fa.getRgProduct().getProductCode(), fa.getNavDate()));
				log.info("OPTIMIZE END loadNav----------------------------");
				if (rgNav != null) {
					fa.setPrice(rgNav.getNav().doubleValue());
				}
			}

			log.info("OPTIMIZE START loadPortfolioProduct----------------------------");
			BigDecimal totalUnit = taService.loadPortfolioProduct(fa.getRgProduct().getProductCode(), fmtYYYYMMDD(fa.getCumDate()));
			log.info("OPTIMIZE END loadPortfolioProduct----------------------------");
			fa.setTotalUnit(totalUnit);
			fa.setCancelTradeDate(canFa.getDivDate());
			fa.setCancelPostDate(canFa.getPostDate());
			fa.setCancelRemark(canFa.getRemarks());

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}

			try {
				GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(RgFundAction.class.getName(), String.valueOf(keyId), new GnLookup(LookupConstants.MAINTENANCE_OPERATION_CREATE));
				RgFundAction logfa = json.readValue(maintenanceLog.getNewData(), RgFundAction.class);
				fa.setRgTradeTemp(logfa.getRgTradeTemp());
				log.debug("JSON " + fa.getRgTradeTemp());
			} catch (Exception ee) {
				log.debug(ee.getMessage(), ee);
			}

			log.info("OPTIMIZE START totalDividen----------------------------" + fa.getRgTradeTemp() + "," + fa.getFundActionKey());
			RgTrade total = taService.totalDividen(fa.getRgTradeTemp(), fa.getFundActionKey());
			// fa.setTotalUnit(total.getTotUnit());

			render("RegistryCancelTrade/approvalCancelFa.html", fa, mode, taskId, from, trades, total, canFa);
		}
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		try {
			Map<String, BigDecimal> holdingMap = new HashMap<String, BigDecimal>();
			boolean isPriceNotNull = false;

			RgTransaction rt = taService.loadTransaction(keyId);
			if (rt != null) {
				log.debug("[APPROVE] RGTRANSACTION BY RGTRADE ORIGINAL => " + rt.getTradeKey());

				if (rt.getPrice() != null) {
					isPriceNotNull = true;
				}

				// Go with trans validation
				if (isPriceNotNull) {
					taService.approveCancelTrade(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

					RgTrade rgTradeRgTransaction = taService.loadTrade(keyId);
					RgTrade tradeCancel = taService.loadTradeByCancelTrade(rgTradeRgTransaction.getTradeKey());
					tradeCancel.setCancelTrade(taService.loadTrade(tradeCancel.getCancelTrade().getTradeKey()));

					RgTrade rgTradeCancel = taService.processTransactionValidation(tradeCancel, holdingMap);
					if (!rgTradeCancel.getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID)) {
						log.debug("[APPROVE] TRANSACTION-VALIDATION ERROR " + rgTradeCancel.getMessage());
						throw new MedallionException("transaction.validation");
					} else {
						log.debug("[APPROVE] TRANSACTION-VALIDATION SUCCESS");
						renderJSON(Formatter.resultSuccess());
					}
				}
			} else {
				taService.approveCancelTrade(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);
				renderJSON(Formatter.resultSuccess());
			}

		} catch (MedallionException e) {
			if ((ExceptionConstants.INVALID_DATA + ".error.sentToFaInterface").equals(e.getErrorCode())) {
				e = new MedallionException("error.sentToFaInterface");
			}
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId);

		try {
			taService.rejectCancelTrade(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void approveCancelSwitching(String taskId, Long keyId) {
		log.debug("approveCancelSwitching. taskId: " + taskId + " keyId: " + keyId);

		try {
			Map<String, BigDecimal> holdingMap = new HashMap<String, BigDecimal>();
			boolean isPriceNotNull = false;

			List<RgTransaction> rgTransactions = taService.listRgTransactionByRgSwitching(keyId);
			for (RgTransaction rt : rgTransactions) {

				log.debug("[APPROVE] RGTRANSACTION BY RGSWITCHING => " + rt);
				log.debug("[APPROVE] RGTRANSACTION PRICE => " + rt.getPrice());

				if (rt.getPrice() != null) {
					isPriceNotNull = true;
					break;
				}
			}

			// Go with trans validation
			if (isPriceNotNull) {
				taService.approveCancelSwitching(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

				RgSwitching rswi = taService.loadSwitchingByCancelSwitching(keyId);
				// RgTrade trade =
				// taService.loadRgTradeByRgSwitching(rswi.getSwitchingKey());
				List<RgTrade> trades = taService.listRgTradeByRgSwitching(rswi.getSwitchingKey());
				for (RgTrade rgTrade : trades) {
					rgTrade.setCancelTrade(taService.loadTrade(rgTrade.getCancelTrade().getTradeKey()));
					RgTrade rgTradeCancel = taService.processTransactionValidation(rgTrade, holdingMap);
					if (!rgTradeCancel.getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID)) {
						throw new MedallionException("transaction.validation");
					} else {
						renderJSON(Formatter.resultSuccess());
					}
				}
			}

			else {
				taService.approveCancelSwitching(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);
				renderJSON(Formatter.resultSuccess());
			}
		} catch (MedallionException e) {
			if ((ExceptionConstants.INVALID_DATA + ".error.sentToFaInterface").equals(e.getErrorCode())) {
				e = new MedallionException("error.sentToFaInterface");
			}
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void rejectCancelSwitching(String taskId, Long keyId) {
		log.debug("rejectCancelSwitching. taskId: " + taskId + " keyId: " + keyId);

		try {
			taService.rejectCancelSwitching(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}

	}

	public static void approveCancelFa(String taskId, Long keyId) {
		log.debug("approveCancelFa. taskId: " + taskId + " keyId: " + keyId);

		try {
			// Map<String, BigDecimal> holdingMap = new HashMap<String,
			// BigDecimal>();

			log.info("OPTIMIZE START approveCancelFundAction2----------------------------");
			taService.approveCancelFundAction2(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);
			log.info("OPTIMIZE END approveCancelFundAction2----------------------------");

			taService.approveCancelTransactionValidation(keyId);

			// method di bawah di ganti dengan
			// approveCancelTransactionValidation;
			// logger.info("OPTIMIZE START listRgTransactionByRgFundAction----------------------------");
			// List<RgTransaction> transactions =
			// taService.listRgTransactionByRgFundAction(keyId);
			// logger.info("OPTIMIZE END listRgTransactionByRgFundAction----------------------------");
			// for (RgTransaction rt : transactions) {
			// logger.info("OPTIMIZE START looping----------------------------");
			// if (rt.getPrice() != null) {
			// //RgFundAction rgFund =
			// taService.loadFundActionByCancelFundAction(keyId);
			// RgTrade trade =
			// taService.loadTradeByCancelTrade(rt.getRgTrade().getTradeKey());
			// trade.setCancelTrade(taService.loadTrade(trade.getCancelTrade().getTradeKey()));
			//
			// RgTrade rgTradeCancel =
			// taService.processTransactionValidation(trade, holdingMap);
			// if
			// (!rgTradeCancel.getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID))
			// {
			// throw new MedallionException("transaction.validation");
			// }
			// }
			// logger.info("OPTIMIZE END looping----------------------------");
			// }
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

	public static void rejectCancelFa(String taskId, Long keyId) {
		log.debug("rejectCancelFa. taskId: " + taskId + " keyId: " + keyId);

		try {
			taService.rejectCancelFundAction(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void uncancelTransaction(RgTrade rg) {
		log.debug("uncancelTransaction. rg: " + rg);

		String remark = rg.getRemark();
		remark = remark.replaceAll(String.valueOf(rg.getTradeKey()), "");

		rg.setRemark(constractId(remark));

		showTransaction(rg);
	}

	private static String constractId(String remak) {
		log.debug("constractId. remak: " + remak);

		String[] ids = remak.split(",");
		remak = "";
		for (String id : ids) {
			if (!"".equals(id.trim())) {
				if ("".equals(remak)) {
					remak = id;
				} else {
					remak += ("," + id);
				}
			}
		}
		return remak;
	}

	public static void process(RgTrade rg) {
		log.debug("process. rg: " + rg);

		String[] tradeKeys = rg.getRemark().split(",");
		//String type = rg.getSelected();
		String mode = UIConstants.DISPLAY_MODE_VIEW;

		List<RgTrade> rgTrades = new ArrayList<RgTrade>();
		for (String tradeKey : tradeKeys) {
			RgTrade rgTrade = new RgTrade(Long.valueOf(tradeKey));
			rgTrades.add(rgTrade);
		}

		if (validation()) {
//			try {
//				String username = session.get(UIConstants.SESSION_USERNAME);
//				// taService.processCancelTrade(rgTrades, type, username);
//			} catch (Exception e) {
//				log.error(e.getMessage(), e);
//			}

			list();
		} else {
			renderText("Validation Fail");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
		render("RegistryCancelTrade/list.html", mode, rg, rgTrades);
	}

	public static boolean validation() {
		log.debug("validation. ");

		return false;
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

	// NOT USED //
	@Check("transaction.registryCancelTrade")
	public static void cancelSubscription(Long keyId, String from, RgTrade rg) {
		log.debug("cancelSubscription. keyId: " + keyId + " from: " + from + " rg: " + rg);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		rg = taService.loadTrade(keyId);
		RgTrade canSub = taService.loadTradeByCancelTrade(rg.getTradeKey());

		rg.setRgProduct(rg.getRgInvestmentaccount().getRgProduct());
		rg.setTaxPct(rg.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		rg.setTaxAmt(getTaxAmt(rg));
		rg.setTrnSABranch(generalService.getThirdParty(rg.getTrnSABranchKey()));

		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		if (from.equals("cancelTradeApp")) {
			rg.setCancelTradeDate(canSub.getTradeDate());
			rg.setCancelPostDate(canSub.getPostDate());
			rg.setCancelRemark(canSub.getRemark());
		}

		if (from.equals("cancelTrade")) {
			rg.setCancelTradeDate(currentBusinessDate);
			rg.setCancelPostDate(currentBusinessDate);
		}

		BigDecimal unitPorto = taService.loadUnitPortfolioByTradeCancel(keyId, rg.getTradeDate());
		log.debug("unit porto = " + unitPorto);

		String messageError = "";
		if (rg.getRgTransaction() != null) {
			if (rg.getTradeStatus().equals(RgTrade.TRADESTATUS_VALID) && rg.getRgTransaction().getType().equals(RgTransaction.TRANSTYPE_SUBSCRIBE)) {
				if (unitPorto == null) {
					messageError = "Unit Portfolio null !";
				} else {
					if (rg.getRgTransaction().getUnit().compareTo(unitPorto) > 0) {
						messageError = "Unit this trade greater than unit in portfolio !";
					}
				}
			}
		} else {
			messageError = "RgTransaction is null !";
		}

		String status = rg.getRecordStatus();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
		render("RegistryCancelTrade/cancelSubscription.html", rg, mode, from, currentBusinessDate, status, messageError);
	}

	// NOT USED //
	@Check("transaction.registryCancelTrade")
	public static void cancelRedemption(Long keyId, boolean liquidation, String from, RgTrade rg) {
		log.debug("cancelRedemption. keyId: " + keyId + " liquidation: " + liquidation + " from: " + from + " rg: " + rg);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		rg = taService.loadTrade(keyId);
		RgTrade canRed = taService.loadTradeByCancelTrade(rg.getTradeKey());
		rg.setRgProduct(rg.getRgInvestmentaccount().getRgProduct());
		rg.setTaxPct(rg.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		rg.setTaxAmt(getTaxAmt(rg));
		rg.setTrnSABranch(generalService.getThirdParty(rg.getTrnSABranchKey()));

		BigDecimal outstandingUnit = taService.loadOutstanding(rg.getRgProduct().getProductCode(), rg.getRgInvestmentaccount().getAccountNumber(), rg.getGoodfundDate());
		log.debug("UNIT = " + outstandingUnit);
		rg.setAvailabelUnit(outstandingUnit);

		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		if (from.equals("cancelTradeApp")) {
			rg.setCancelTradeDate(canRed.getTradeDate());
			rg.setCancelPostDate(canRed.getPostDate());
			rg.setCancelRemark(canRed.getRemark());
			rg.setCancelLiquidation(liquidation);
		}
		if (from.equals("cancelTrade")) {
			rg.setCancelTradeDate(currentBusinessDate);
			rg.setCancelPostDate(currentBusinessDate);
			rg.setCancelLiquidation(liquidation);
		}

		List<String> tradesNo = new ArrayList<String>();

		Long dataPaymentOnApproval = null;
		Long dataPaymentAlreadyPaid = null;
		if ((rg.getRgPaymentDetail() != null) || (rg.getRgPayment() != null)) {
			Long dataPayment = taService.chekcPaymentOnApprovalList(rg.getTradeKey());
			Long dataPaymentPaid = taService.chekcPaymentAlreadyPaid(rg.getTradeKey());
			if (dataPayment == 0) {
				dataPaymentOnApproval = dataPayment;
			}
			if (dataPaymentPaid == 0) {
				dataPaymentAlreadyPaid = dataPaymentPaid;
			}
		}

		boolean warningMsg = false;
		if (rg.getLiquidation() == true) {
			List<RgTrade> tradeByLiqs = taService.listTradeByRedeemRef(rg.getTradeKey());
			if (tradeByLiqs.size() > 0) {
				warningMsg = true;
				for (RgTrade rgTrade : tradeByLiqs) {
					tradesNo.add(Long.toString(rgTrade.getTradeKey()));
				}
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
		render("RegistryCancelTrade/cancelRedemption.html", rg, mode, from, warningMsg, tradesNo, dataPaymentAlreadyPaid, dataPaymentOnApproval, currentBusinessDate);
	}

	// NOT USED //
	@Check("transaction.registryCancelTrade")
	public static void cancelSwitching(Long keyId, String from, RgTrade rg) {
		log.debug("cancelSwitching. keyId: " + keyId + " from: " + from + " rg: " + rg);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);

		RgSwitching swt = taService.loadSwitching(keyId);
		swt.splitRgTrade();
		RgSwitching canSwt = taService.loadSwitchingByCancelSwitching(swt.getSwitchingKey());

		if (canSwt != null) {
			canSwt.splitRgTrade();
		}

		CfMaster customer = customerService.getCustomer(swt.getRgInvestmentaccountByOutAccountNumber().getCustomer().getCustomerKey());
		swt.setCustomer(customer);
		swt.setThirdPartyByFundManager(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getThirdPartyByFundManager());
		swt.setSaCode(swt.getRgInvestmentaccountByOutAccountNumber().getThirdPartyBySaCode().getThirdPartyCode());

		if (swt.getOut() != null) {
			swt.setGoodfundDate(swt.getOut().getGoodfundDate());
			swt.setNavDate(swt.getOut().getNavDate());
			swt.setPostDate(swt.getOut().getPostDate());
			swt.setSwitchAll(swt.getOut().getLiquidation());
			BigDecimal outstandingUnit = taService.loadOutstanding(swt.getOut().getRgProduct().getProductCode(), swt.getOut().getRgInvestmentaccount().getAccountNumber(), swt.getGoodfundDate());
			swt.getOut().setAvailabelUnit(outstandingUnit);

			if (swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getTaxMasterByRedTaxKey() != null) {
				swt.getOut().setTaxPct(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getTaxMasterByRedTaxKey().getTaxRate());
			}
			swt.getOut().setTrnSABranch(generalService.getThirdParty(swt.getOut().getTrnSABranchKey()));

			if (swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getTaxMasterBySubTaxKey() != null) {
				swt.getIn().setTaxPct(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
			}
			swt.getIn().setTrnSABranchKey(swt.getOut().getTrnSABranchKey());
			swt.getIn().setTrnSABranch(generalService.getThirdParty(swt.getIn().getTrnSABranchKey()));
		}

		if (from.equals("cancelTradeApp")) {
			swt.setCancelTradeDate(canSwt.getOut().getTradeDate());
			swt.setCancelPostDate(canSwt.getOut().getPostDate());
			swt.setCancelRemark(canSwt.getOut().getRemark());
		}

		if (from.equals("cancelSwitching")) {
			swt.setCancelTradeDate(currentBusinessDate);
			swt.setCancelPostDate(currentBusinessDate);
		}

		List<RgTrade> trades = taService.listTradeBySwitching(keyId);
		log.debug("trades = " + trades.size());
		String messageError = "";
		if (!from.equals("unitRegistry")) {
			for (RgTrade trade : trades) {
				if (trade != null) {
					BigDecimal unitPorto = taService.loadUnitPortfolioByTradeCancel(trade.getTradeKey(), swt.getCancelTradeDate());
					log.debug("UNIT PORT0 = " + unitPorto);
					if (trade.getTradeStatus().equals(RgTrade.TRADESTATUS_VALID) && trade.getRgTransaction().getType().equals(RgTransaction.TRANSTYPE_SUBSCRIBE)) {
						if (unitPorto == null) {
							messageError = "Unit Portfolio null !";
						} else {
							BigDecimal unitFromTransaction = new BigDecimal(0);
							if (trade.getRgTransaction() == null) {
								unitFromTransaction = BigDecimal.ZERO;
							} else {
								unitFromTransaction = trade.getRgTransaction().getUnit();
							}

							if (unitFromTransaction.compareTo(unitPorto) > 0) {
								messageError = "Unit this trade greater than unit in portfolio !";
							}
						}
					}
				}
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
		render("RegistryCancelTrade/cancelSwitching.html", swt, rg, mode, from, currentBusinessDate, messageError);
	}

	// NOT USED //
	@Check("transaction.registryCancelTrade")
	public static void cancelDividend(Long keyId, String from, RgTrade rg) {
		log.debug("cancelDividend. keyId: " + keyId + " from: " + from + " rg: " + rg);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);

		RgFundAction fa = taService.loadFundAction(keyId);
		RgFundAction canFa = taService.loadFundActionByCancelFundAction(fa.getFundActionKey());

		if (from.equals("cancelTradeApp")) {
			for (RgTrade tr : canFa.getRgTrades()) {
				fa.setCancelTradeDate(tr.getTradeDate());
				fa.setCancelPostDate(tr.getPostDate());
				fa.setCancelRemark(tr.getRemark());
			}
		}

		if (from.equals("cancelFundAction")) {
			fa.setCancelTradeDate(currentBusinessDate);
			fa.setCancelPostDate(currentBusinessDate);
		}

		List<RgTrade> trades = listTrades(fa);
		log.debug("[CANCEL DIVIDEND] TRADES SIZE => " + trades.size());

		if (AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) {
			RgNav rgNav = taService.loadNav(new RgNavId(fa.getRgProduct().getProductCode(), fa.getNavDate()));
			if (rgNav != null) {
				fa.setPrice(rgNav.getNav().doubleValue());
			}
		}

		BigDecimal totalUnit = taService.loadPortfolioProduct(fa.getRgProduct().getProductCode(), fmtYYYYMMDD(fa.getCumDate()));
		fa.setTotalUnit(totalUnit);

		List<String> tradesNo = new ArrayList<String>();

		Long dataPaymentOnApproval = null;
		Long dataPaymentAlreadyPaid = null;
		List<RgTrade> rgTrades = taService.listTradeByFundAction(keyId);

		String messageError = "";
		for (RgTrade rgTrade : rgTrades) {
			if ((rgTrade.getRgPaymentDetail() != null) || (rgTrade.getRgPayment() != null)) {
				Long dataPayment = taService.chekcPaymentOnApprovalList(rgTrade.getTradeKey());
				Long dataPaymentPaid = taService.chekcPaymentAlreadyPaid(rgTrade.getTradeKey());
				if (dataPayment == 0) {
					log.debug(">>>>>***** " + dataPayment);
					dataPaymentOnApproval = dataPayment;
				}
				if (dataPaymentPaid == 0) {
					log.debug(">>>>>~~~~~ " + dataPaymentPaid);
					dataPaymentAlreadyPaid = dataPaymentPaid;
				}
			}

			BigDecimal unitPorto = taService.loadUnitPortfolioByTradeCancel(rgTrade.getTradeKey(), fa.getCancelTradeDate());
			if (rgTrade.getTradeStatus().equals(RgTrade.TRADESTATUS_VALID) && rgTrade.getRgTransaction().getType().equals(RgTransaction.TRANSTYPE_SUBSCRIBE)) {
				if (unitPorto == null) {
					messageError = "Unit Portfolio null !";
				} else {
					if (rgTrade.getRgTransaction().getUnit().compareTo(unitPorto) > 0) {
						messageError = "Unit this trade greater than unit in portfolio !";
					}
				}
			}
		}

		boolean warningMsg = false;
		List<RgTrade> tradeByRedeems = taService.listTradeRedeemByFundAction(fa.getFundActionKey());
		if ((!tradeByRedeems.isEmpty()) && (tradeByRedeems.size() > 0)) {
			warningMsg = true;
			for (RgTrade rgTrade : tradeByRedeems) {
				tradesNo.add(Long.toString(rgTrade.getTradeKey()));
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
		render("RegistryCancelTrade/cancelDividend.html", fa, mode, from, currentBusinessDate, messageError, warningMsg, dataPaymentOnApproval, dataPaymentAlreadyPaid, trades);
	}

	public static void save(RgTrade rg, RgSwitching swt, RgFundAction fa, String mode, boolean isNewRec, String status) {
		log.debug("save. rg: " + rg + " swt: " + swt + " fa: " + fa + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		if (rg != null) {
			if ((AbstractRgTrade.TRADETYPE_SUBSCRIBE.equals(rg.getType())) || (AbstractRgTrade.TRADETYPE_REDEEM.equals(rg.getType()))) {
				log.debug("[SAVE] TRADE KEY = " + rg.getTradeKey());
				log.debug("[SAVE] TYPE = " + rg.getType());
				log.debug("[SAVE] CANCEL DATE = " + rg.getCancelTradeDate());
				log.debug("[SAVE] CANCEL POST DATE = " + rg.getCancelPostDate());
				log.debug("[SAVE] REMARK = " + rg.getCancelRemark());

				RgTrade rgOriginal = taService.loadTrade(rg.getTradeKey());
				rgOriginal.setCancelRemark(rg.getCancelRemark());
				rgOriginal.setCancelTradeDate(rg.getCancelTradeDate());
				rgOriginal.setCancelPostDate(rg.getCancelPostDate());
				rgOriginal.setRgTransaction(null);

				Long tradeKey = rgOriginal.getTradeKey();

				serializerService.serialize(session.getId(), tradeKey, rgOriginal);
				confirming(tradeKey, mode, isNewRec, status);
			}
		}

		if (swt != null) {
			if (AbstractRgSwitching.TYPE_SWITCHING.equals(swt.getType())) {
				RgSwitching rgOriginal = taService.loadSwitching(swt.getSwitchingKey());
				rgOriginal.setCancelRemark(swt.getCancelRemark());
				rgOriginal.setCancelTradeDate(swt.getCancelTradeDate());
				rgOriginal.setCancelPostDate(swt.getCancelPostDate());
				rgOriginal.setThirdPartyByFundManager(new GnThirdParty());
				rgOriginal.getThirdPartyByFundManager().setThirdPartyCode(swt.getThirdPartyByFundManager().getThirdPartyCode());
				rgOriginal.setSaCode(swt.getSaCode());

				rgOriginal.setRgTrades(null);
				Long switchingKey = rgOriginal.getSwitchingKey();

				serializerService.serialize(session.getId(), switchingKey, rgOriginal);
				confirming(switchingKey, mode, isNewRec, status);
			}
		}

		if (fa != null) {
			if (AbstractRgFundAction.TYPE_DIVIDEND.equals(fa.getType())) {
				RgFundAction rgOriginal = taService.loadFundAction(fa.getFundActionKey());
				rgOriginal.setTotalUnit(fa.getTotalUnit());
				rgOriginal.setPrice(fa.getPrice());
				rgOriginal.setInputBy(fa.getInputBy());
				rgOriginal.setRatio(fa.getRatio());
				rgOriginal.setAmountPerUnit(fa.getAmountPerUnit());
				rgOriginal.setTotal(fa.getTotal());
				rgOriginal.setCancelRemark(fa.getCancelRemark());
				rgOriginal.setCancelTradeDate(fa.getCancelTradeDate());
				rgOriginal.setCancelPostDate(fa.getCancelPostDate());

				Long fundActionKey = rgOriginal.getFundActionKey();
				serializerService.serialize(session.getId(), fundActionKey, rgOriginal);
				confirming(fundActionKey, mode, isNewRec, status);
			}
		}
	}

	public static void confirming(Long id, String mode, boolean isNewRec, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		RgTrade rg = serializerService.deserialize(session.getId(), id, RgTrade.class);
		RgSwitching swt = serializerService.deserialize(session.getId(), id, RgSwitching.class);
		RgFundAction fa = serializerService.deserialize(session.getId(), id, RgFundAction.class);

		renderArgs.put("confirming", true);
		// String from = "cancelTrade";
		if (rg != null) {
			if (AbstractRgTrade.TRADETYPE_SUBSCRIBE.equals(rg.getType())) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
				render("RegistryCancelTrade/cancelSubscription.html", rg, mode, isNewRec, status);
			}

			if (AbstractRgTrade.TRADETYPE_REDEEM.equals(rg.getType())) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
				render("RegistryCancelTrade/cancelRedemption.html", rg, mode, isNewRec, status);
			}
		}

		if (swt != null) {
			if (AbstractRgSwitching.TYPE_SWITCHING.equals(swt.getType())) {
				RgInvestmentaccount rgInvestOut = taService.loadInvestment(swt.getRgInvestmentaccountByOutAccountNumber().getAccountNumber());
				RgInvestmentaccount rgInvestIn = taService.loadInvestment(swt.getRgInvestmentaccountByInAccountNumber().getAccountNumber());

				CfMaster customer = customerService.getCustomer(rgInvestOut.getCustomer().getCustomerKey());
				swt.setCustomer(customer);
				swt.setThirdPartyByFundManager(rgInvestOut.getRgProduct().getThirdPartyByFundManager());
				swt.setSaCode(swt.getRgInvestmentaccountByOutAccountNumber().getThirdPartyBySaCode().getThirdPartyCode());

				if (swt.getOut() != null) {
					swt.setGoodfundDate(swt.getOut().getGoodfundDate());
					swt.setNavDate(swt.getOut().getNavDate());
					swt.setPostDate(swt.getOut().getPostDate());
					swt.setSwitchAll(swt.getOut().getLiquidation());
					BigDecimal outstandingUnit = taService.loadOutstanding(swt.getOut().getRgProduct().getProductCode(), swt.getOut().getRgInvestmentaccount().getAccountNumber(), swt.getGoodfundDate());
					swt.getOut().setAvailabelUnit(outstandingUnit);

					if (rgInvestOut.getRgProduct().getTaxMasterByRedTaxKey() != null) {
						swt.getOut().setTaxPct(rgInvestOut.getRgProduct().getTaxMasterByRedTaxKey().getTaxRate());
					}
					swt.getOut().setTrnSABranch(generalService.getThirdParty(swt.getOut().getTrnSABranchKey()));

					if (rgInvestIn.getRgProduct().getTaxMasterBySubTaxKey() != null) {
						swt.getIn().setTaxPct(rgInvestIn.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
					}
					swt.getIn().setTrnSABranchKey(swt.getOut().getTrnSABranchKey());
					swt.getIn().setTrnSABranch(generalService.getThirdParty(swt.getIn().getTrnSABranchKey()));
				}

				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
				render("RegistryCancelTrade/cancelSwitching.html", swt, mode, isNewRec, status);
			}
		}

		if (fa != null) {
			if (AbstractRgFundAction.TYPE_DIVIDEND.equals(fa.getType())) {
				List<RgTrade> trades = listTrades(fa);
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
				render("RegistryCancelTrade/cancelDividend.html", fa, mode, isNewRec, status, trades);
			}
		}
	}

	public static void confirm(RgTrade rg, RgSwitching swt, RgFundAction fa, String mode, boolean isNewRec, String status) {
		log.debug("confirm. rg: " + rg + " swt: " + swt + " fa: " + fa + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		String username = session.get(UIConstants.SESSION_USERNAME);
		String userKey = session.get(UIConstants.SESSION_USER_KEY);
		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);

		if (rg != null) {
			if (AbstractRgTrade.TRADETYPE_SUBSCRIBE.equals(rg.getType())) {
				try {
					taService.processCancelTradeSubRedeem(rg, username, userKey);
					Map<String, Object> result = new HashMap<String, Object>();
					log.debug("[CONFIRM] TRADE ID => " + rg.getTradeKey());
					result.put("status", "success");
					result.put("message", Messages.get("trade.cancelled.successful", rg.getTradeKey()));
					renderJSON(result);
				} catch (MedallionException ex) {
					flash.error("Trade No : ' " + rg.getTradeKey() + " ' " + Messages.get(ex.getMessage()));
					renderArgs.put("confirming", true);
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
					render("RegistryCancelTrade/cancelSubscription.html", rg, mode, "cancelTrade", currentBusinessDate, status);
				}
			}

			if (AbstractRgTrade.TRADETYPE_REDEEM.equals(rg.getType())) {
				try {
					taService.processCancelTradeSubRedeem(rg, username, userKey);
					Map<String, Object> result = new HashMap<String, Object>();
					log.debug("[CONFIRM] TRADE ID => " + rg.getTradeKey());
					result.put("status", "success");
					result.put("message", Messages.get("trade.cancelled.successful", rg.getTradeKey()));
					renderJSON(result);
				} catch (MedallionException ex) {
					flash.error("Trade No : ' " + rg.getTradeKey() + " ' " + Messages.get(ex.getMessage()));
					renderArgs.put("confirming", true);
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
					render("RegistryCancelTrade/cancelSubscription.html", rg, mode, "cancelTrade", currentBusinessDate, status);
				}
			}
		}

		if (swt != null) {
			if (AbstractRgSwitching.TYPE_SWITCHING.equals(swt.getType())) {
				try {
					taService.processCancelSwitching(swt, rg, username, userKey);
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", Messages.get("switch.cancelled.successful", swt.getSwitchingKey()));
					renderJSON(result);
				} catch (MedallionException ex) {
					flash.error("Switch No : ' " + swt.getSwitchingKey() + " ' " + Messages.get(ex.getMessage()));
					renderArgs.put("confirming", true);
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
					render("RegistryCancelTrade/cancelSubscription.html", swt, mode, "cancelSwitch", currentBusinessDate, status);
				}
			}
		}

		if (fa != null) {
			if (AbstractRgFundAction.TYPE_DIVIDEND.equals(fa.getType())) {
				try {
					taService.processCancelFundAction(fa, rg, username, userKey);
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", Messages.get("fundAction.cancelled.successful", fa.getFundActionKey()));
					renderJSON(result);
				} catch (MedallionException ex) {
					flash.error("Dividen No : ' " + fa.getFundActionKey() + " ' " + Messages.get(ex.getMessage()));
					renderArgs.put("confirming", true);
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
					render("RegistryCancelTrade/cancelSubscription.html", fa, mode, "cancelDividen", currentBusinessDate, status);
				}
			}
		}
	}

	public static void back(Long id, String mode, boolean isNewRec, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
			isNewRec = true;
		} else {
			isNewRec = false;
		}

		RgTrade rg = serializerService.deserialize(session.getId(), id, RgTrade.class);
		RgSwitching swt = serializerService.deserialize(session.getId(), id, RgSwitching.class);
		RgFundAction fa = serializerService.deserialize(session.getId(), id, RgFundAction.class);

		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);

		if (rg != null) {
			log.debug("[BACK] TRADE KEY = " + rg.getTradeKey());
			log.debug("[BACK] TYPE = " + rg.getType());
			log.debug("[BACK] CANCEL DATE = " + rg.getCancelTradeDate());
			log.debug("[BACK] CANCEL POST DATE = " + rg.getCancelPostDate());
			log.debug("[BACK] REMARK = " + rg.getCancelRemark());

			if (AbstractRgTrade.TRADETYPE_SUBSCRIBE.equals(rg.getType())) {
				render("RegistryCancelTrade/cancelSubscription.html", rg, mode, currentBusinessDate, status);
			}

			if (AbstractRgTrade.TRADETYPE_REDEEM.equals(rg.getType())) {
				render("RegistryCancelTrade/cancelRedemption.html", rg, mode, currentBusinessDate, status);
			}
		}

		if (swt != null) {
			if (AbstractRgSwitching.TYPE_SWITCHING.equals(swt.getType())) {
				render("RegistryCancelTrade/cancelSwitching.html", swt, mode, currentBusinessDate, status);
			}
		}

		if (fa != null) {
			if (AbstractRgFundAction.TYPE_DIVIDEND.equals(fa.getType())) {
				render("RegistryCancelTrade/cancelDividend.html", fa, mode, currentBusinessDate, status);
			}
		}
	}

	private static BigDecimal getTaxAmt(RgTrade sub) {
		log.debug("getTaxAmt. sub: " + sub);

		double taxPct = (sub.getTaxPct() == null) ? 0 : sub.getTaxPct().doubleValue();
		double feeAmt = (sub.getFeeAmt() == null) ? 0 : sub.getFeeAmt().doubleValue();
		double discAmt = (sub.getDiscAmt() == null) ? 0 : sub.getDiscAmt().doubleValue();
		double otherAmt = (sub.getOtherAmt() == null) ? 0 : sub.getOtherAmt().doubleValue();
		double taxAmt = 0;
		if (taxPct != 0D) {
			taxAmt = ((taxPct / 100) * (feeAmt + discAmt + otherAmt));
		}
		return new BigDecimal(taxAmt);
	}

	public static void getBankAccountFromRgProduct(String productCode) {
		log.debug("getBankAccountFromRgProduct. productCode: " + productCode);

		BnAccount bnAccount = bankAccountService.getBankAccountByRgProduct(productCode);
		renderJSON(bnAccount, new BankAccountPickSerializer());
	}

	private static List<RgTrade> listTrades(RgFundAction fa) {
		log.debug("listTrades. fa: " + fa);

		// logger.debug("from listTrades >>> fa.type = " + fa.getDivType());
		// if (!(AbstractRgFundAction.DIVTYPE_BY_CASH.equals(fa.getDivType())))
		// {

		RgNav rgNav = taService.loadNav(new RgNavId(fa.getRgProduct().getProductCode(), fa.getNavDate()));
		// logger.debug("from listTrades >>> nav = " + rgNav.getNav());
		if (rgNav != null)
			fa.setPrice(rgNav.getNav().doubleValue());
		log.debug("1. from listTrades >>> fa.price = " + fa.getPrice());
		// }
		log.debug("2. from listTrades >>> fa.price = " + fa.getPrice());
		fa.setTotalUnit(taService.loadPortfolioProduct(fa.getRgProduct().getProductCode(), fmtYYYYMMDD(fa.getCumDate())));
		log.debug("fa.getInputMethodBy() = " + fa.getInputMethodBy());
		if (fa != null) {
			double netAmount = 0;
			//double netAmountInvestorOption = 0;

			double amount = 0;
			double unit = 0;
			double price = 0;
			double totAmount = 0;

			BigDecimal totAmountRoundMinus = BigDecimal.ZERO;
			DecimalFormat df = new DecimalFormat("#.##");

			if (fa.getRgProduct().isFixnav()) {
				if (fa.getRgProduct().getFixNavPrice() != null) {
					price = fa.getRgProduct().getFixNavPrice().doubleValue();
				} else {
					price = 0;
				}

			} else {
				price = fa.getPrice();
			}
			List<RgTrade> trades = new ArrayList<RgTrade>();

			for (RgInvestmentaccount invstAcc : taService.loadPortfolioAccount(fa.getRgProduct().getProductCode(), fmtYYYYMMDD(fa.getCumDate()))) {
				RgPortfolio portfolio = taService.loadPortfolio(invstAcc.getAccountNumber(), fmtYYYYMMDD(fa.getCumDate()));
				if ((AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) || (AbstractRgFundAction.DIVTYPE_BY_CASH.equals(fa.getDivType())) || (AbstractRgFundAction.DIVTYPE_BY_REDEMPTION.equals(fa.getDivType()))) {
					RgTrade trade = new RgTrade();
					trade.setRgInvestmentaccount(new RgInvestmentaccount());
					trade.getRgInvestmentaccount().setAccountNumber(invstAcc.getAccountNumber());
					if (AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) {
						trade.setType(AbstractRgTrade.TRADETYPE_SUBSCRIBE_FUND_ACTION);
						trade.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER);
					}
					if (AbstractRgFundAction.DIVTYPE_BY_CASH.equals(fa.getDivType())) {
						trade.setType(AbstractRgTrade.TRADETYPE_CASH_FUND_ACTION);
						trade.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER);
					}
					if (AbstractRgFundAction.DIVTYPE_BY_REDEMPTION.equals(fa.getDivType())) {
						trade.setType(AbstractRgTrade.TRADETYPE_REDEEM_FUND_ACTION);
						trade.setTypeOrder(RgTrade.TRADETYPEORDER_REDEEM_FUND_ACTION);
					}
					trade.getRgInvestmentaccount().setName(invstAcc.getName());
					trade.setAvailabelUnit(portfolio.getUnit());
					trade.setTradeDate(fa.getDivDate());
					trade.setPostDate(fa.getPostDate());
					trade.setPaymentDate(fa.getPaymentDate());
					if (portfolio != null) {
						if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_1)) {
							amount = portfolio.getUnit().doubleValue() / fa.getTotalUnit().doubleValue() * fa.getTotAmount().doubleValue();
							unit = amount / price;
						}/*
						 * else if
						 * (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_2
						 * )){
						 * logger.debug("UNIT = "+portfolio.getUnit().doubleValue
						 * ());
						 * logger.debug("FAKTOR AMOUNT = "+fa.getFactorAmount
						 * ().doubleValue());
						 * 
						 * unit = portfolio.getUnit().doubleValue() *
						 * fa.getFactorAmount().doubleValue() / 100 ; amount =
						 * unit * price; }
						 */else if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_3)) {
							amount = portfolio.getUnit().doubleValue() * fa.getUnitAmount().doubleValue();
							unit = amount / price;
						}

						if (fa.getRoundType() != null && fa.getRoundValue() != null) {

							log.debug("[1] [trade reinvestment] [under zero] roundValue = " + fa.getRoundValue());
							log.debug("[1] [trade cash] [under zero] roundValue = " + fa.getRoundValue() + " netAmount = " + netAmount);
							if (fa.getRoundValue() >= 0) {
								trade.setAmount(round(fa.getRoundType(), amount, fa.getRoundValue()));

							} else {
								if (ROUND.equals(fa.getRoundType())) {
									log.debug("[1] [trade cash] [under zero] [round] new netAmout = " + new BigDecimal(df.format(new BigDecimal(netAmount).setScale(fa.getRoundValue(), RoundingMode.HALF_UP))));
									totAmountRoundMinus = new BigDecimal(df.format(new BigDecimal(amount).setScale(fa.getRoundValue(), RoundingMode.HALF_UP)));
									trade.setAmount(totAmountRoundMinus);
								}
								if (ROUNDUP.equals(fa.getRoundType())) {
									log.debug("[1] [trade cash] [under zero] [roundup] new netAmount = " + new BigDecimal(df.format(new BigDecimal(netAmount).setScale(fa.getRoundValue(), RoundingMode.UP))));
									totAmountRoundMinus = new BigDecimal(df.format(new BigDecimal(amount).setScale(fa.getRoundValue(), RoundingMode.UP)));
									trade.setAmount(totAmountRoundMinus);
								}
								if (ROUNDDOWN.equals(fa.getRoundType())) {
									log.debug("[1] [trade cash] [under zero] [rounddown] new netAmount = " + new BigDecimal(df.format(new BigDecimal(netAmount).setScale(fa.getRoundValue(), RoundingMode.DOWN))));
									totAmountRoundMinus = new BigDecimal(df.format(new BigDecimal(amount).setScale(fa.getRoundValue(), RoundingMode.DOWN)));
									trade.setAmount(totAmountRoundMinus);
								}
							}

						} else {
							trade.setAmount(new BigDecimal(amount));
						}
					}
					trade.setUnit(new BigDecimal(unit));
					log.debug("unit ----> " + unit);
					totAmount = totAmount + amount;
					trade.setPrice(new BigDecimal(price));
					trades.add(trade);
				}
			}
			return trades;
		}
		return null;
	}

	public static void data(RgFundAction fa) {
		log.debug("data. fa: " + fa);

		if (AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) {
			RgNav rgNav = taService.loadNav(new RgNavId(fa.getRgProduct().getProductCode(), fa.getNavDate()));
			if (rgNav != null)
				fa.setPrice(rgNav.getNav().doubleValue());
		}

		fa.setTotalUnit(taService.loadPortfolioProduct(fa.getRgProduct().getProductCode(), fmtYYYYMMDD(fa.getCumDate())));
		if (!(TOTAL_AMOUNT.equals(fa.getInputBy()))) {
			fa.setAmount(fa.getTotal());
		}
		double netAmount = 0;
		double netAmountInvestorOption = 0;
		List<RgTrade> trades = new ArrayList<RgTrade>();
		for (RgInvestmentaccount invstAcc : taService.loadPortfolioAccount(fa.getRgProduct().getProductCode(), fmtYYYYMMDD(fa.getCumDate()))) {
			RgPortfolio portfolio = taService.loadPortfolio(invstAcc.getAccountNumber(), fmtYYYYMMDD(fa.getCumDate()));

			if ((AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) || (AbstractRgFundAction.DIVTYPE_BY_CASH.equals(fa.getDivType())) || (AbstractRgFundAction.DIVTYPE_BY_REDEMPTION.equals(fa.getDivType()))) {
				RgTrade trade = new RgTrade();
				trade.setRgInvestmentaccount(new RgInvestmentaccount());
				trade.getRgInvestmentaccount().setAccountNumber(invstAcc.getAccountNumber());
				trade.setTradeDate(fa.getDivDate());
				trade.setPostDate(fa.getPostDate());
				trade.setPaymentDate(fa.getPaymentDate());
				if (RgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) {
					trade.setType(RgTrade.TRADETYPE_SUBSCRIBE_FUND_ACTION);
					trade.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER);
				}
				if (RgFundAction.DIVTYPE_BY_CASH.equals(fa.getDivType())) {
					trade.setType(RgTrade.TRADETYPE_CASH_FUND_ACTION);
					trade.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER);
				}
				if (RgFundAction.DIVTYPE_BY_REDEMPTION.equals(fa.getDivType())) {
					trade.setType(RgTrade.TRADETYPE_REDEEM_FUND_ACTION);
					trade.setTypeOrder(RgTrade.TRADETYPEORDER_REDEEM_FUND_ACTION);
				}
				if (portfolio != null) {
					trade.setAvailabelUnit(portfolio.getUnit());
					if (!(TOTAL_AMOUNT.equals(fa.getInputBy()))) {
						log.debug("fa.total() = " + fa.getTotal());
						fa.setAmount(fa.getTotal());
					}

					log.debug("portfolio.unit = " + portfolio.getUnit().doubleValue());
					log.debug("fa.totalUnit = " + fa.getTotalUnit().doubleValue());
					log.debug("fa.amount = " + fa.getAmount().doubleValue());
					log.debug("fa.taxMaster.taxRate = " + fa.getTaxMaster().getTaxRate());

					netAmount = (portfolio.getUnit().doubleValue() / fa.getTotalUnit().doubleValue()) * (fa.getAmount().doubleValue() * (1 - fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue()));
					log.debug("[DATA] fa.inputBy = " + fa.getInputBy());
					if (UNIT_PER_UNIT.equals(fa.getInputBy())) {
						netAmount = (portfolio.getUnit().doubleValue() * fa.getRatio().doubleValue() * fa.getPrice().doubleValue()) * (1 - fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue());
						log.debug("[DATA] [UNIT_PER_UNIT] netAmount = " + netAmount);
					}
					log.debug("netAmount = " + netAmount);
					trade.setNetAmount(new BigDecimal(netAmount));
					if (fa.getPrice() != null) {
						trade.setPrice(new BigDecimal(fa.getPrice()));
						double unit = trade.getNetAmount().doubleValue() / trade.getPrice().doubleValue();
						trade.setUnit(new BigDecimal(unit));
					}
				}
				trades.add(trade);
			} else {
				// for Dividen Type Investor Option
				// =======================================================================
				log.debug("[DATA] [INVESTOR TYPE]");
				BigDecimal cashPct = BigDecimal.ZERO;
				BigDecimal reinvestmentPct = BigDecimal.ZERO;
				BigDecimal redeemPct = BigDecimal.ZERO;

				if (fa.getRgProduct().getDivIopByCashPct().doubleValue() > 0) {
					cashPct = fa.getRgProduct().getDivIopByCashPct();
					if (invstAcc.getDivIopByCashPct() != null) {
						cashPct = invstAcc.getDivIopByCashPct();
					}
					log.debug("[DATA] [INVESTOR TYPE] [CASH FUND ACTION] with invstAcc.divIopByCashPct = " + invstAcc.getDivIopByCashPct());
					RgTrade trade = new RgTrade();
					trade.setTradeDate(fa.getDivDate());
					trade.setPostDate(fa.getPostDate());
					trade.setPaymentDate(fa.getPaymentDate());
					trade.setRgInvestmentaccount(new RgInvestmentaccount());
					trade.getRgInvestmentaccount().setAccountNumber(invstAcc.getAccountNumber());
					trade.setType(AbstractRgTrade.TRADETYPE_CASH_FUND_ACTION);
					trade.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER);
					if (portfolio != null) {
						trade.setAvailabelUnit(portfolio.getUnit());
						if (!(TOTAL_AMOUNT.equals(fa.getInputBy()))) {
							log.debug("fa.total() = " + fa.getTotal());
							fa.setAmount(fa.getTotal());
						}
						netAmount = (portfolio.getUnit().doubleValue() / fa.getTotalUnit().doubleValue()) * (fa.getAmount().doubleValue() * (1 - fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue()));
						if (UNIT_PER_UNIT.equals(fa.getInputBy())) {
							netAmount = (portfolio.getUnit().doubleValue() * fa.getRatio().doubleValue() * fa.getPrice().doubleValue()) * (1 - fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue());
							log.debug("[DATA] [UNIT_PER_UNIT] netAmount = " + netAmount);
						}
					}
					netAmountInvestorOption = (netAmount * cashPct.divide(BD_100).doubleValue());
					trade.setNetAmount(new BigDecimal(netAmountInvestorOption));
					if (fa.getPrice() != null) {
						trade.setPrice(new BigDecimal(fa.getPrice()));
						double unit = trade.getNetAmount().doubleValue() / trade.getPrice().doubleValue();
						trade.setUnit(new BigDecimal(unit));
					}
					trades.add(trade);
				}
				if (fa.getRgProduct().getDivIopByReinvestmentPct().doubleValue() > 0) {
					reinvestmentPct = fa.getRgProduct().getDivIopByReinvestmentPct();
					if (invstAcc.getDivIopByReinvestmentPct() != null) {
						reinvestmentPct = invstAcc.getDivIopByReinvestmentPct();
					}

					log.debug("[DATA] [INVESTOR TYPE] [SUBSCRIBE FUND ACTION] with invstAcc.divIopByReinvestmentPct = " + invstAcc.getDivIopByReinvestmentPct());

					RgTrade trade = new RgTrade();
					trade.setRgInvestmentaccount(new RgInvestmentaccount());
					trade.getRgInvestmentaccount().setAccountNumber(invstAcc.getAccountNumber());
					trade.setTradeDate(fa.getDivDate());
					trade.setPostDate(fa.getPostDate());
					trade.setPaymentDate(fa.getPaymentDate());
					trade.setType(AbstractRgTrade.TRADETYPE_SUBSCRIBE_FUND_ACTION);
					trade.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER);
					if (portfolio != null) {
						trade.setAvailabelUnit(portfolio.getUnit());
						if (!(TOTAL_AMOUNT.equals(fa.getInputBy()))) {
							log.debug("fa.total() = " + fa.getTotal());
							fa.setAmount(fa.getTotal());
						}
						netAmount = (portfolio.getUnit().doubleValue() / fa.getTotalUnit().doubleValue()) * (fa.getAmount().doubleValue() * (1 - fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue()));
						if (UNIT_PER_UNIT.equals(fa.getInputBy())) {
							netAmount = (portfolio.getUnit().doubleValue() * fa.getRatio().doubleValue() * fa.getPrice().doubleValue()) * (1 - fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue());
							log.debug("[DATA] [UNIT_PER_UNIT] netAmount = " + netAmount);
						}
					}
					netAmountInvestorOption = (netAmount * reinvestmentPct.divide(BD_100).doubleValue());
					trade.setNetAmount(new BigDecimal(netAmountInvestorOption));
					if (fa.getPrice() != null) {
						trade.setPrice(new BigDecimal(fa.getPrice()));
						double unit = trade.getNetAmount().doubleValue() / trade.getPrice().doubleValue();
						trade.setUnit(new BigDecimal(unit));
					}
					trades.add(trade);
				}
				if (fa.getRgProduct().getDivIopByRedeemPct().doubleValue() > 0) {
					redeemPct = fa.getRgProduct().getDivIopByRedeemPct();
					if (invstAcc.getDivIopByRedeemPct() != null) {
						redeemPct = invstAcc.getDivIopByRedeemPct();
					}
					log.debug("[DATA] [INVESTOR TYPE] [REDEEM FUND ACTION] with invstAcc.divIopByRedeemPct = " + invstAcc.getDivIopByRedeemPct());
					RgTrade trade = new RgTrade();
					trade.setTradeDate(fa.getDivDate());
					trade.setPostDate(fa.getPostDate());
					trade.setPaymentDate(fa.getPaymentDate());
					trade.setRgInvestmentaccount(new RgInvestmentaccount());
					trade.getRgInvestmentaccount().setAccountNumber(invstAcc.getAccountNumber());
					trade.setType(AbstractRgTrade.TRADETYPE_REDEEM_FUND_ACTION);
					trade.setTypeOrder(RgTrade.TRADETYPEORDER_REDEEM_FUND_ACTION);
					if (portfolio != null) {
						trade.setAvailabelUnit(portfolio.getUnit());
						if (!(TOTAL_AMOUNT.equals(fa.getInputBy()))) {
							log.debug("fa.total() = " + fa.getTotal());
							fa.setAmount(fa.getTotal());
						}
						netAmount = (portfolio.getUnit().doubleValue() / fa.getTotalUnit().doubleValue()) * (fa.getAmount().doubleValue() * (1 - fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue()));
						if (UNIT_PER_UNIT.equals(fa.getInputBy())) {
							netAmount = (portfolio.getUnit().doubleValue() * fa.getRatio().doubleValue() * fa.getPrice().doubleValue()) * (1 - fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue());
							log.debug("[DATA] [UNIT_PER_UNIT] netAmount = " + netAmount);
						}
					}
					netAmountInvestorOption = (netAmount * redeemPct.divide(BD_100).doubleValue());
					trade.setNetAmount(new BigDecimal(netAmountInvestorOption));
					if (fa.getPrice() != null) {
						trade.setPrice(new BigDecimal(fa.getPrice()));
						double unit = trade.getNetAmount().doubleValue() / trade.getPrice().doubleValue();
						trade.setUnit(new BigDecimal(unit));
					}
					trades.add(trade);
				}
			}
			// ========================================================================================================
		}

		serializerService.serialize(session.getId(), -1, fa);
		render("RegistryFundAction/detail_dividen.html", trades);
	}

}