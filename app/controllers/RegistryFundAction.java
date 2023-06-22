package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import vo.TradeSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.FormatRoundHelper;
import com.simian.medallion.model.AbstractRgFundAction;
import com.simian.medallion.model.AbstractRgTrade;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnTaxMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgFundAction;
import com.simian.medallion.model.RgInvestmentaccount;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgNavId;
import com.simian.medallion.model.RgPortfolio;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgProductLockException;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.TaService;
import com.simian.medallion.vo.SelectItem;

import extensions.StatusExtension;

public class RegistryFundAction extends Registry {
	private static Logger log = Logger.getLogger(RegistryFundAction.class);

	private static final BigDecimal BD_100 = new BigDecimal(100);
	public static final String DISPLAY_MODE_PROCESS = "process";
	private static final String FROM_UNIT_REGISTRY = "unitRegistry";
	private static final String FROM_CANCEL_DIVIDEN = "cancelFundAction";

	public static final String TOTAL_AMOUNT = "TOTAL_AMOUNT";
	public static final String AMOUNT_PER_UNIT = "AMOUNT_PER_UNIT";
	public static final String UNIT_PER_UNIT = "UNIT_PER_UNIT";
	public static final String ALLOCATE_TOTAL_AMOUNT = "ALLOCATE_TOTAL_AMOUNT";
	public static final String RATIO_PER_UNIT = "RATIO_PER_UNIT";
	public static final String TRANSACTION_REDEEMFUNDACTION_CAPGAINTAX = "transaction.redemfundaction.capgaintax";

	@Before(only = { "data", "process", "edit", "view", "entry", "save", "confirming", "confirm", "back", "approval", "cancel" })
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("dividendType", UIHelper.dividenTypes());
		renderArgs.put("roundingType", UIHelper.roundingOperators());
		renderArgs.put("inputByOptions", UIHelper.inputByOperators());
		renderArgs.put("ratioPerUnitByOptions", UIHelper.ratioPerUnitByOptions());

		renderArgs.put("totAmount", LookupConstants.DIVIDEN_BY_1);
		// renderArgs.put("factor",LookupConstants.DIVIDEN_BY_2);
		renderArgs.put("amountUnit", LookupConstants.DIVIDEN_BY_3);

		List<SelectItem> dividenBy = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._DIVIDEN_BY);
		renderArgs.put("dividenBy", dividenBy);

	}

	@Check("transaction.registryFundAction")
	public static void list() {
		log.debug("list. ");

		List<RgFundAction> fas = taService.listFundAction();
		render("RegistryFundAction/list.html", fas);
	}

	// ORIGINAL BY ELVINO
	// public static void data(Long id) {
	// RgFundAction fa = taService.loadFundAction(id);
	// if (UNIT.equals(fa.getType())) {
	// RgNav rgNav = taService.loadNav(new RgNavId(fa.getRgProduct()
	// .getProductCode(), fa.getNavDate()));
	// if (rgNav != null)
	// fa.setPrice(rgNav.getNav().doubleValue());
	// }
	// fa.setTotalUnit(taService.loadPortfolioProduct(fa.getRgProduct()
	// .getProductCode(), fmtYYYYMMDD(fa.getCumDate())));
	//
	// List<RgTrade> trades = new ArrayList<RgTrade>();
	// for (RgInvestmentaccount invstAcc : taService.loadPortfolioAccount(fa
	// .getRgProduct().getProductCode(), fmtYYYYMMDD(fa.getCumDate()))) {
	// RgPortfolio portfolio = taService.loadPortfolio(
	// invstAcc.getAccountNumber(), fmtYYYYMMDD(fa.getCumDate()));
	//
	// RgTrade trade = new RgTrade();
	// trade.setRgInvestmentaccount(new RgInvestmentaccount());
	// trade.getRgInvestmentaccount().setAccountNumber(
	// invstAcc.getAccountNumber());
	// trade.setTradeDate(fa.getDivDate());
	// trade.setPostDate(fa.getPostDate());
	// if (portfolio != null) {
	// trade.setAvailabelUnit(portfolio.getUnit());
	// double netAmount = (portfolio.getUnit().doubleValue() / fa
	// .getTotalUnit().doubleValue())
	// * (fa.getAmount().doubleValue() * (1 - fa
	// .getTaxMaster().getTaxRate().divide(BD_100)
	// .doubleValue()));
	// trade.setNetAmount(round(fa.getRoundType(), netAmount,
	// fa.getRoundValue()));
	//
	// if (fa.getPrice() != null) {
	// trade.setPrice(new BigDecimal(fa.getPrice()));
	// double unit = trade.getNetAmount().doubleValue()
	// / trade.getPrice().doubleValue();
	// trade.setUnit(new BigDecimal(unit));
	// }
	// }
	// trades.add(trade);
	// }
	//
	// render("RegistryFundAction/process.html", trades, id);
	// }

	public static void pagingDividen(Paging page, TradeSearchParameters param) {
		log.debug("pagingDividen. page: " + page + " param: " + param);

		if (param.isQuery()) {
			if ("".equals(param.fundActionKey)) { // ini artinya rg_trade blm ke
													// bentuk sehingga harus
													// ambil dari rg_trade_temp
				page.addParams("trade_type", page.EQUAL, "DIVIDEN");
				page.addParams("unique_id", page.EQUAL, param.uniqeId);
				page.addParams(Helper.searchAll("(account_no||account_name||unit_balance||amount||price||unit)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch().toUpperCase(), 1));
				page = taService.pagingDividen(page);
			} else { // ini artinya sudah terbentuk sehingga ambil dari real
						// table
				page.addParams("b.fund_action_key", page.EQUAL, Long.parseLong(param.fundActionKey));
				page.addParams(Helper.searchAll("(a.account_number||d.name||c.unit||a.amount||a.price||a.unit)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch().toUpperCase(), 1));
				page = taService.pagingDividenDirect(page);
			}
		}

		renderJSON(page);
	}

	@Check("transaction.registryFundAction")
	public static void data(RgFundAction fa) {
		log.debug("data. fa: " + fa);

		String uniqeId = fa.getRgTradeTemp();
		fa = taService.populateDividen(uniqeId, fa);
		RgTrade total = taService.totalDividen(uniqeId, fa.getFundActionKey());

		String fundAction = serializerService.serialize(session.getId(), -1, fa);
		log.debug("[data] fundAction = " + fundAction);

		render("RegistryFundAction/detail_dividen.html", total);
	}

	public static void resetPaging(TradeSearchParameters param) {
		log.debug("resetPaging. param: " + param);

		taService.deletePagingDividen(param.uniqeId);
		Map<String, String> successMap = new HashMap<String, String>();
		renderJSON(successMap);
	}

	/*
	 * public static void data(RgFundAction fa) { // RgFundAction fa =
	 * taService.loadFundAction(id);
	 * logger.debug("welcome to data method with divType = " + fa.getDivType());
	 * logger.debug("welcome to data method with fa.inputMEthodBy = " +
	 * fa.getInputMethodBy()); if
	 * (AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) {
	 * RgNav rgNav = taService.loadNav(new RgNavId(fa.getRgProduct()
	 * .getProductCode(), fa.getNavDate())); if (rgNav != null)
	 * fa.setPrice(rgNav.getNav().doubleValue()); }
	 * fa.setTotalUnit(taService.loadPortfolioProduct(fa.getRgProduct()
	 * .getProductCode(), fmtYYYYMMDD(fa.getCumDate()))); if
	 * (!(TOTAL_AMOUNT.equals(fa.getInputBy()))) { fa.setAmount(fa.getTotal());
	 * } double netAmount = 0; double netAmountInvestorOption = 0; List<RgTrade>
	 * trades = new ArrayList<RgTrade>(); for (RgInvestmentaccount invstAcc :
	 * taService.loadPortfolioAccount(fa .getRgProduct().getProductCode(),
	 * fmtYYYYMMDD(fa.getCumDate()))) { RgPortfolio portfolio =
	 * taService.loadPortfolio( invstAcc.getAccountNumber(),
	 * fmtYYYYMMDD(fa.getCumDate()));
	 * 
	 * if
	 * ((AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType()))
	 * || (AbstractRgFundAction.DIVTYPE_BY_CASH.equals(fa.getDivType())) ||
	 * (AbstractRgFundAction.DIVTYPE_BY_REDEMPTION.equals(fa.getDivType()))) {
	 * RgTrade trade = new RgTrade(); trade.setRgInvestmentaccount(new
	 * RgInvestmentaccount()); trade.getRgInvestmentaccount().setAccountNumber(
	 * invstAcc.getAccountNumber()); trade.setTradeDate(fa.getDivDate());
	 * trade.setPostDate(fa.getPostDate());
	 * trade.setPaymentDate(fa.getPaymentDate()); if
	 * (RgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) {
	 * trade.setType(RgTrade.TRADETYPE_SUBSCRIBE_FUND_ACTION);
	 * trade.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER); } if
	 * (RgFundAction.DIVTYPE_BY_CASH.equals(fa.getDivType())) {
	 * trade.setType(RgTrade.TRADETYPE_CASH_FUND_ACTION);
	 * trade.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER); } if
	 * (RgFundAction.DIVTYPE_BY_REDEMPTION.equals(fa.getDivType())) {
	 * trade.setType(RgTrade.TRADETYPE_REDEEM_FUND_ACTION);
	 * trade.setTypeOrder(RgTrade.TRADETYPEORDER_REDEEM_FUND_ACTION); } if
	 * (portfolio != null) { trade.setAvailabelUnit(portfolio.getUnit()); if
	 * (!(TOTAL_AMOUNT.equals(fa.getInputBy()))) { logger.debug("fa.total() = "
	 * + fa.getTotal()); fa.setAmount(fa.getTotal()); } //netAMount = ((unit /
	 * totalUnit) * (amount *(1-(taxrate/100))))
	 * logger.debug("portfolio.unit = " + portfolio.getUnit().doubleValue());
	 * logger.debug("fa.totalUnit = " + fa.getTotalUnit().doubleValue());
	 * logger.debug("fa.amount = " + fa.getAmount().doubleValue());
	 * logger.debug("fa.taxMaster.taxRate = " + fa.getTaxMaster().getTaxRate());
	 * netAmount = (portfolio.getUnit().doubleValue() / fa
	 * .getTotalUnit().doubleValue()) (fa.getAmount().doubleValue() * (1 - fa
	 * .getTaxMaster().getTaxRate().divide(BD_100) .doubleValue()));
	 * logger.debug("[DATA] fa.inputBy = " + fa.getInputBy()); if
	 * (UNIT_PER_UNIT.equals(fa.getInputBy())) { netAmount =
	 * (portfolio.getUnit().doubleValue() * fa.getRatio().doubleValue() *
	 * fa.getPrice().doubleValue()) (1 -
	 * fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue());
	 * logger.debug("[DATA] [UNIT_PER_UNIT] netAmount = " + netAmount); }
	 * logger.debug("netAmount = " + netAmount); // if (fa.getRoundType() !=
	 * null && fa.getRoundValue()!= null) { //
	 * trade.setNetAmount(round(fa.getRoundType(), netAmount, //
	 * fa.getRoundValue())); // } else { trade.setNetAmount(new
	 * BigDecimal(netAmount)); // } if (fa.getPrice() != null) {
	 * trade.setPrice(new BigDecimal(fa.getPrice())); double unit =
	 * trade.getNetAmount().doubleValue() / trade.getPrice().doubleValue();
	 * trade.setUnit(new BigDecimal(unit)); } } trades.add(trade); } else { //
	 * for Dividen Type Investor Option
	 * =======================================================================
	 * logger.debug("[DATA] [INVESTOR TYPE]"); BigDecimal cashPct =
	 * BigDecimal.ZERO; BigDecimal reinvestmentPct = BigDecimal.ZERO; BigDecimal
	 * redeemPct = BigDecimal.ZERO; // logger.debug(fa.getRgProduct()); //
	 * logger.debug(fa.getRgProduct().getDivIopByCashPct()); if
	 * (fa.getRgProduct().getDivIopByCashPct().doubleValue() > 0) { cashPct =
	 * fa.getRgProduct().getDivIopByCashPct(); if (invstAcc.getDivIopByCashPct()
	 * != null) { cashPct = invstAcc.getDivIopByCashPct(); } logger.debug(
	 * "[DATA] [INVESTOR TYPE] [CASH FUND ACTION] with invstAcc.divIopByCashPct = "
	 * + invstAcc.getDivIopByCashPct()); RgTrade trade = new RgTrade();
	 * trade.setTradeDate(fa.getDivDate()); trade.setPostDate(fa.getPostDate());
	 * trade.setPaymentDate(fa.getPaymentDate());
	 * trade.setRgInvestmentaccount(new RgInvestmentaccount());
	 * trade.getRgInvestmentaccount().setAccountNumber(
	 * invstAcc.getAccountNumber());
	 * trade.setType(AbstractRgTrade.TRADETYPE_CASH_FUND_ACTION);
	 * trade.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER); if (portfolio != null)
	 * { trade.setAvailabelUnit(portfolio.getUnit()); if
	 * (!(TOTAL_AMOUNT.equals(fa.getInputBy()))) { logger.debug("fa.total() = "
	 * + fa.getTotal()); fa.setAmount(fa.getTotal()); } netAmount =
	 * (portfolio.getUnit().doubleValue() / fa .getTotalUnit().doubleValue())
	 * (fa.getAmount().doubleValue() * (1 - fa
	 * .getTaxMaster().getTaxRate().divide(BD_100) .doubleValue())); if
	 * (UNIT_PER_UNIT.equals(fa.getInputBy())) { netAmount =
	 * (portfolio.getUnit().doubleValue() * fa.getRatio().doubleValue() *
	 * fa.getPrice().doubleValue()) (1 -
	 * fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue());
	 * logger.debug("[DATA] [UNIT_PER_UNIT] netAmount = " + netAmount); } }
	 * netAmountInvestorOption = (netAmount *
	 * cashPct.divide(BD_100).doubleValue()); // if (fa.getRoundType() != null
	 * && fa.getRoundValue()!= null) { //
	 * trade.setNetAmount(round(fa.getRoundType(), netAmountInvestorOption, //
	 * fa.getRoundValue())); // } else { trade.setNetAmount(new
	 * BigDecimal(netAmountInvestorOption)); // } if (fa.getPrice() != null) {
	 * trade.setPrice(new BigDecimal(fa.getPrice())); double unit =
	 * trade.getNetAmount().doubleValue() / trade.getPrice().doubleValue();
	 * trade.setUnit(new BigDecimal(unit)); } trades.add(trade); } if
	 * (fa.getRgProduct().getDivIopByReinvestmentPct().doubleValue() > 0) {
	 * reinvestmentPct = fa.getRgProduct().getDivIopByReinvestmentPct(); if
	 * (invstAcc.getDivIopByReinvestmentPct() != null) { reinvestmentPct =
	 * invstAcc.getDivIopByReinvestmentPct(); } logger.debug(
	 * "[DATA] [INVESTOR TYPE] [SUBSCRIBE FUND ACTION] with invstAcc.divIopByReinvestmentPct = "
	 * + invstAcc.getDivIopByReinvestmentPct()); RgTrade trade = new RgTrade();
	 * trade.setRgInvestmentaccount(new RgInvestmentaccount());
	 * trade.getRgInvestmentaccount().setAccountNumber(
	 * invstAcc.getAccountNumber()); trade.setTradeDate(fa.getDivDate());
	 * trade.setPostDate(fa.getPostDate());
	 * trade.setPaymentDate(fa.getPaymentDate());
	 * trade.setType(AbstractRgTrade.TRADETYPE_SUBSCRIBE_FUND_ACTION);
	 * trade.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER); if (portfolio != null)
	 * { trade.setAvailabelUnit(portfolio.getUnit()); if
	 * (!(TOTAL_AMOUNT.equals(fa.getInputBy()))) { logger.debug("fa.total() = "
	 * + fa.getTotal()); fa.setAmount(fa.getTotal()); } netAmount =
	 * (portfolio.getUnit().doubleValue() / fa .getTotalUnit().doubleValue())
	 * (fa.getAmount().doubleValue() * (1 - fa
	 * .getTaxMaster().getTaxRate().divide(BD_100) .doubleValue())); if
	 * (UNIT_PER_UNIT.equals(fa.getInputBy())) { netAmount =
	 * (portfolio.getUnit().doubleValue() * fa.getRatio().doubleValue() *
	 * fa.getPrice().doubleValue()) (1 -
	 * fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue());
	 * logger.debug("[DATA] [UNIT_PER_UNIT] netAmount = " + netAmount); } }
	 * netAmountInvestorOption = (netAmount *
	 * reinvestmentPct.divide(BD_100).doubleValue()); // if (fa.getRoundType()
	 * != null && fa.getRoundValue()!= null) { //
	 * trade.setNetAmount(round(fa.getRoundType(), netAmountInvestorOption, //
	 * fa.getRoundValue())); // } else { trade.setNetAmount(new
	 * BigDecimal(netAmountInvestorOption)); // } if (fa.getPrice() != null) {
	 * trade.setPrice(new BigDecimal(fa.getPrice())); double unit =
	 * trade.getNetAmount().doubleValue() / trade.getPrice().doubleValue();
	 * trade.setUnit(new BigDecimal(unit)); } trades.add(trade); } if
	 * (fa.getRgProduct().getDivIopByRedeemPct().doubleValue() > 0) { redeemPct
	 * = fa.getRgProduct().getDivIopByRedeemPct(); if
	 * (invstAcc.getDivIopByRedeemPct() != null) { redeemPct =
	 * invstAcc.getDivIopByRedeemPct(); } logger.debug(
	 * "[DATA] [INVESTOR TYPE] [REDEEM FUND ACTION] with invstAcc.divIopByRedeemPct = "
	 * + invstAcc.getDivIopByRedeemPct()); RgTrade trade = new RgTrade();
	 * trade.setTradeDate(fa.getDivDate()); trade.setPostDate(fa.getPostDate());
	 * trade.setPaymentDate(fa.getPaymentDate());
	 * trade.setRgInvestmentaccount(new RgInvestmentaccount());
	 * trade.getRgInvestmentaccount().setAccountNumber(
	 * invstAcc.getAccountNumber());
	 * trade.setType(AbstractRgTrade.TRADETYPE_REDEEM_FUND_ACTION);
	 * trade.setTypeOrder(RgTrade.TRADETYPEORDER_REDEEM_FUND_ACTION); if
	 * (portfolio != null) { trade.setAvailabelUnit(portfolio.getUnit()); if
	 * (!(TOTAL_AMOUNT.equals(fa.getInputBy()))) { logger.debug("fa.total() = "
	 * + fa.getTotal()); fa.setAmount(fa.getTotal()); } netAmount =
	 * (portfolio.getUnit().doubleValue() / fa .getTotalUnit().doubleValue())
	 * (fa.getAmount().doubleValue() * (1 - fa
	 * .getTaxMaster().getTaxRate().divide(BD_100) .doubleValue())); if
	 * (UNIT_PER_UNIT.equals(fa.getInputBy())) { netAmount =
	 * (portfolio.getUnit().doubleValue() * fa.getRatio().doubleValue() *
	 * fa.getPrice().doubleValue()) (1 -
	 * fa.getTaxMaster().getTaxRate().divide(BD_100).doubleValue());
	 * logger.debug("[DATA] [UNIT_PER_UNIT] netAmount = " + netAmount); } }
	 * netAmountInvestorOption = (netAmount *
	 * redeemPct.divide(BD_100).doubleValue()); // if (fa.getRoundType() != null
	 * && fa.getRoundValue()!= null) { //
	 * trade.setNetAmount(round(fa.getRoundType(), netAmountInvestorOption, //
	 * fa.getRoundValue())); // } else { trade.setNetAmount(new
	 * BigDecimal(netAmountInvestorOption)); // } if (fa.getPrice() != null) {
	 * trade.setPrice(new BigDecimal(fa.getPrice())); double unit =
	 * trade.getNetAmount().doubleValue() / trade.getPrice().doubleValue();
	 * trade.setUnit(new BigDecimal(unit)); } trades.add(trade); } } //
	 * ==========
	 * ================================================================
	 * ============================== } String fundAction =
	 * serializerService.serialize(session.getId(), -1, fa);
	 * logger.debug("[data] fundAction = " + fundAction);
	 * logger.debug("size trades from [data] >>> " + trades.size()); //
	 * logger.debug("[data] calculated = " + calculated);
	 * render("RegistryFundAction/detail_dividen.html", trades); }
	 */

	@Check("transaction.registryFundAction")
	public static void process(Long id, BigDecimal amountTrades, String uniqeId) {
		log.debug("process. id: " + id + " amountTrades: " + amountTrades + " uniqeId: " + uniqeId);

		String usename = session.get(UIConstants.SESSION_USERNAME);
		RgFundAction fa = taService.processDividen(id, amountTrades, usename);

		taService.deletePagingDividen(uniqeId);

		RgTrade total = taService.totalDividen(uniqeId, fa.getFundActionKey());

		if (!(fa.equals(null))) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "success");
			result.put("message", Messages.get("fundAction.confirmed.successful", fa.getFundActionKey()));
			renderJSON(result);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_DIVIDEN));
			List<RgTrade> trades = new ArrayList<RgTrade>(); // ini oper kosong
																// aja agar gax
																// di render di
																// depan
			render("RegistryFundAction/detail.html", fa, trades, total);
		}
	}

	@Check("transaction.registryFundAction")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		RgFundAction fa = taService.loadFundAction(id);
		if (AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) {
			RgNav rgNav = taService.loadNav(new RgNavId(fa.getRgProduct().getProductCode(), fa.getNavDate()));
			if (rgNav != null)
				fa.setPrice(rgNav.getNav().doubleValue());
		}
		BigDecimal totalUnit = taService.loadPortfolioProduct(fa.getRgProduct().getProductCode(), fmtYYYYMMDD(fa.getCumDate()));
		fa.setTotalUnit(totalUnit);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_DIVIDEN));
		render("RegistryFundAction/entry.html", fa, mode);
	}

	@Check("transaction.registryFundAction")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgFundAction fa = taService.loadFundAction(id);
		if (AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType())) {
			RgNav rgNav = taService.loadNav(new RgNavId(fa.getRgProduct().getProductCode(), fa.getNavDate()));
			if (rgNav != null)
				fa.setPrice(rgNav.getNav().doubleValue());
		}

		BigDecimal totalUnit = taService.loadPortfolioProduct(fa.getRgProduct().getProductCode(), fmtYYYYMMDD(fa.getCumDate()));
		fa.setTotalUnit(totalUnit);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_DIVIDEN));
		render("RegistryFundAction/entry.html", fa, mode);
	}

	@Check("transaction.registryFundAction")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;

		GnSystemParameter gnSysDigitAmount = generalService.getSystemParameter(TaService.PRD_DIGIT_MGN_AMOUNT);
		GnSystemParameter gnSysDigitPrice = generalService.getSystemParameter(TaService.PRD_DIGIT_MGN_PRICE);
		GnSystemParameter gnSysDigitUnit = generalService.getSystemParameter(TaService.PRD_DIGIT_MGN_UNIT);

		String isDigitAmount = gnSysDigitAmount.getValue();
		String isDigitPrice = gnSysDigitPrice.getValue();
		String isDigitUnit = gnSysDigitUnit.getValue();

		RgFundAction fa = new RgFundAction();
		fa.setRgTradeTemp(Helper.getZipPassword());
		fa.setDivDate(taService.getCurrentBusinessDate());
		fa.setCurrentBusinessDate(taService.getCurrentBusinessDate());
		fa.setActive(false);
		// logger.debug("fund action key ---> "+fa.getFundActionKey());
		String taxKey = generalService.getValueParam(SystemParamConstants.PARAM_DEFAULT_CAP_GAIN_TAX);
		GnTaxMaster tax = generalService.getTaxMaster(Long.parseLong(taxKey));
		fa.setTaxMaster(tax);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_DIVIDEN));

		render("RegistryFundAction/entry.html", fa, mode, isDigitAmount, isDigitPrice, isDigitUnit);
	}

	@Check("transaction.registryFundAction")
	public static void save(RgFundAction fa, String mode, List<RgTrade> trades, Boolean calculated, RgTrade rg, String from) {
		log.debug("save. fa: " + fa + " mode: " + mode + " trades: " + trades + " calculated: " + calculated + " rg: " + rg + " from: " + from);

		if (from == null || !from.equals(FROM_CANCEL_DIVIDEN)) {
			fa.setSumTotalAmount(null);
		}
		if (from == null) {
			if (!calculated) {
				if (fa != null) {
					log.debug("price 1 ----" + fa.getPrice());
					validation.required("Fund Code is", fa.getRgProduct().getProductCode());
					validation.required("Dividend Type is", fa.getDivType());
					validation.required("Input By is", fa.getInputBy());
					validation.required("Dividend Date is", fa.getDivDate());
					validation.required("Cum Date is", fa.getCumDate());
					validation.required("Total Unit is", fa.getTotalUnit());
					if ((AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType()) || AbstractRgFundAction.DIVTYPE_BY_REDEMPTION.equals(fa.getDivType()))) {
						validation.required("NAV Date is", fa.getNavDate());
					}
					validation.required("Price is", fa.getPrice());
					if ((AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType()) || AbstractRgFundAction.DIVTYPE_BY_REDEMPTION.equals(fa.getDivType()))) {
						validation.required("Post Date is", fa.getPostDate());
					}
					if ((AbstractRgFundAction.DIVTYPE_BY_CASH.equals(fa.getDivType()) || AbstractRgFundAction.DIVTYPE_BY_REDEMPTION.equals(fa.getDivType()))) {
						validation.required("Payment Date is", fa.getPaymentDate());
					}
					if (fa.getInputMethodBy().equals("ALLOCATE_TOTAL_AMOUNT")) {
						validation.required("Amount is", fa.getAmount());
						validation.required("Amount/Unit is", fa.getAmountPerUnit());
					}
					validation.required("Tax Code is", fa.getTaxMaster().getTaxKey());
					validation.required("Net Amount is", fa.getNetAmount());
					// if (AMOUNT_PER_UNIT.equals(fa.getInputBy())) {
					// validation.required("Amount/Unit is",
					// fa.getAmountPerUnit());
					// }
					// validation.required("Rounding Type is",
					// fa.getRoundType());
					// validation.required("Rounding Value is",
					// fa.getRoundValue());
					validationFundAction(fa);
					log.debug("price 2 ----" + fa.getPrice());
				}
			} else {
				fa = serializerService.deserialize(session.getId(), -1, RgFundAction.class);
				validationFundAction(fa);
			}
		}

		// FOR CANCEL //
		if (from != null && from.equals(FROM_CANCEL_DIVIDEN)) {
			validation.clear();
		}

		if (validation.hasErrors()) {
			// trades = listTrades(fa);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_DIVIDEN));
			render("RegistryFundAction/entry.html", fa, mode, trades, calculated);
		} else {
			Long id = fa.getFundActionKey();
			log.debug("id from [save] >>> " + id);
			if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_1))
				fa.setAmount(fa.getTotAmount());
			/*
			 * if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_2))
			 * fa.setAmount(fa.getFactorAmount());
			 */
			if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_3))
				fa.setAmount(fa.getUnitAmount());
			serializerService.serialize(session.getId(), id, fa);
			log.debug("[save] calculated = " + calculated);
			confirming(id, mode, trades, calculated, from);
		}
	}

	@Check("transaction.registryFundAction")
	public static void confirming(Long id, String mode, List<RgTrade> trades, Boolean calculated, String from) {
		log.debug("confirming. id: " + id + " mode: " + mode + " trades: " + trades + " calculated: " + calculated + " from: " + from);

		GnSystemParameter gnSysDigitAmount = generalService.getSystemParameter(TaService.PRD_DIGIT_MGN_AMOUNT);
		GnSystemParameter gnSysDigitPrice = generalService.getSystemParameter(TaService.PRD_DIGIT_MGN_PRICE);
		GnSystemParameter gnSysDigitUnit = generalService.getSystemParameter(TaService.PRD_DIGIT_MGN_UNIT);

		String isDigitAmount = gnSysDigitAmount.getValue();
		String isDigitPrice = gnSysDigitPrice.getValue();
		String isDigitUnit = gnSysDigitUnit.getValue();

		renderArgs.put("confirming", true);
		renderArgs.put("isDigitAmount", isDigitAmount);
		renderArgs.put("isDigitPrice", isDigitPrice);
		renderArgs.put("isDigitUnit", isDigitUnit);

		RgFundAction fa = serializerService.deserialize(session.getId(), id, RgFundAction.class);
		if (fa.getRatio() != null) {
			log.debug("[CONFIRMING] fa.ratio = " + fa.getRatio().doubleValue());
		}
		// trades = listTrades(fa);
		if (fa.getRgProduct().isFixnav()) {
			if (fa.getRgProduct().getFixNavPrice() != null) {
				fa.setPrice(fa.getRgProduct().getFixNavPrice().doubleValue());
			}

		}
		String uniqeId = fa.getRgTradeTemp();
		RgTrade total = taService.totalDividen(uniqeId, fa.getFundActionKey());

		// logger.debug("size trades from [save] >>> " + trades.size());
		if (from != null && from.equals(FROM_CANCEL_DIVIDEN)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
			render("RegistryFundAction/cancel.html", fa, mode, from, trades, calculated, total);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_DIVIDEN));
			render("RegistryFundAction/entry.html", fa, mode, trades, calculated, total);
		}
	}

	private static Map<Long, Long> keyMap = new HashMap<Long, Long>();

	@Check("transaction.registryFundAction")
	public static void confirm(RgFundAction fa, String mode, List<RgTrade> trades, RgTrade rg, String from) {
		log.debug("confirm. fa: " + fa + " mode: " + mode + " trades: " + trades + " rg: " + rg + " from: " + from);

		if (from != null && from.equals(FROM_CANCEL_DIVIDEN)) {
			//Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			try {
				log.debug("--------> CONFIRM CANCEL DIVIDEN TRADE DATE = " + fa.getCancelTradeDate());
				log.debug("--------> CONFIRM CANCEL DIVIDEN POST DATE = " + fa.getCancelPostDate());
				log.debug("fa " + fa.getRgTradeTemp());

				// TODO: Add By Fadly 2017-10-25 RedMine #3330
				if (keyMap.get(fa.getFundActionKey()) != null) {
					throw new MedallionException(Messages.get("subscription.cancelTrade_can_not_cancel_trade_more_than_one"));
				} else {
					keyMap.put(fa.getFundActionKey(), fa.getFundActionKey());
				}

				RgFundAction cancelTrade = taService.checkCancelDividend(fa.getFundActionKey());
				if (cancelTrade == null)
					throw new MedallionException(Messages.get("subscription.cancelTrade_can_not_cancel_trade_more_than_one"));
				// END Add RedMine #3330

				String uniqeId = fa.getRgTradeTemp();
				RgTrade total = taService.totalDividen(uniqeId, fa.getFundActionKey());
				fa.setTotalUnit(total.getTotUnit());
				fa.setSumTotalAmount(total.getTotNetAmt());

				log.debug("Rg Total Amount = " + rg.getTotNetAmt());
				taService.processCancelFundAction(fa, rg, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("fundAction.cancelled.successful", fa.getFundActionKey()));
				renderJSON(result);
			} catch (MedallionException ex) {
				// TODO: Change By Fadly 2017-10-25 RedMine #3330
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "errorx");
				result.put("errorx", Messages.get(ex.getMessage()));
				renderJSON(result);
			} finally {
				keyMap.remove(fa.getFundActionKey());
				// END Change RedMine #3330
			}

		} else {
			try {
				log.debug("[CONFIRM] fa.inputMethodBy = " + fa.getInputMethodBy());
				log.debug("[CONFIRM] fa.inputBy = " + fa.getInputBy());
				log.debug("totNetAmtDiv confirm 2------> " + fa.getTotNetAmtDiv());
				BigDecimal amountTrades = new BigDecimal(0);

				if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_1))
					amountTrades = fa.getTotAmount();
				/*
				 * if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_2))
				 * amountTrades = fa.getFactorAmount();
				 */
				if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_3))
					amountTrades = fa.getUnitAmount();

				// trades = listTrades(fa);
				trades = new ArrayList<RgTrade>();
				log.debug("amount fa " + fa.getAmount());

				// overwirte total dan masukin ke fa
				String uniqeId = fa.getRgTradeTemp();
				RgTrade total = taService.totalDividen(uniqeId, fa.getFundActionKey());
				fa.setTotalUnit(total.getTotUnit());
				fa.setNetAmount(total.getTotNetAmt().doubleValue());
				fa.setTotNetAmtDiv(total.getTotNetAmt());

				fa = taService.createFundAction(MenuConstants.RG_DIVIDEN, fa, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY), amountTrades, uniqeId);

				// Long id = fa.getFundActionKey();
				// logger.debug("fund Action key in confirm "+id);
				// process(id,amountTrades, uniqeId);
				// logger.debug("id from [confirm] >>> " + id);
				// boolean confirming = true;

				if (!(fa.equals(null))) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", Messages.get("fundAction.confirmed.successful", fa.getFundActionKey()));
					renderJSON(result);
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_DIVIDEN));
					// List<RgTrade> trades = new ArrayList<RgTrade>(); // ini
					// oper kosong aja agar gax di render di depan
					render("RegistryFundAction/detail.html", fa, trades, total);
				}

			} catch (MedallionException ex) {
				String uniqeId = fa.getRgTradeTemp();
				RgTrade total = taService.totalDividen(uniqeId, fa.getFundActionKey());
				flash.error(ex.getErrorCode(), ex.getParams());
				renderArgs.put("confirming", true);
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_DIVIDEN));
				render("RegistryFundAction/entry.html", fa, mode, trades, total);
			} catch (Exception ex) {
				List<String> params = new ArrayList<String>();
				String uniqeId = fa.getRgTradeTemp();
				RgTrade total = taService.totalDividen(uniqeId, fa.getFundActionKey());
				flash.error(ex.getMessage(), params.toArray());
				renderArgs.put("confirming", true);
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_DIVIDEN));
				render("RegistryFundAction/entry.html", fa, mode, trades, total);
			}
		}
	}

	@Check("transaction.registryFundAction")
	public static void back(Long id, String mode, List<RgTrade> trades, Boolean calculated, String from) {
		log.debug("back. id: " + id + " mode: " + mode + " trades: " + trades + " calculated: " + calculated + " from: " + from);

		renderArgs.put("confirming", false);
		Boolean back = true;
		/*
		 * if (id==null) { id = (long) 0; }
		 */

		RgFundAction fa = serializerService.deserialize(session.getId(), id, RgFundAction.class);
		if (fa.getAmount() == null) {
			if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_1))
				fa.setAmount(fa.getTotAmount());
			/*
			 * if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_2))
			 * fa.setAmount(fa.getFactorAmount());
			 */
			if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_3))
				fa.setAmount(fa.getUnitAmount());
		}

		trades = listTrades(fa);
		if (fa.getRgProduct().isFixnav()) {
			log.debug("price 75 ----" + fa.getPrice());
			if (fa.getRgProduct().getFixNavPrice() != null) {
				log.debug("price 75 ----" + fa.getPrice());
				fa.setPrice(fa.getRgProduct().getFixNavPrice().doubleValue());
			}

		}

		if (from != null && from.equals(FROM_CANCEL_DIVIDEN)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
			render("RegistryFundAction/cancel.html", fa, mode, trades, calculated, back, from);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_DIVIDEN));
			render("RegistryFundAction/entry.html", fa, mode, trades, calculated, back);
		}
	}

	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " group: " + group + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			RgFundAction fa = json.readValue(maintenanceLog.getNewData(), RgFundAction.class);

			// List<RgTrade> trades = listTrades(fa);
			List<RgTrade> trades = new ArrayList<RgTrade>();

			fa.setStatus(fa.getStatus().trim());

			RgNav rgNav = taService.loadNav(new RgNavId(fa.getRgProduct().getProductCode(), fa.getNavDate()));
			if (rgNav != null)
				fa.setPrice(rgNav.getNav().doubleValue());

			log.debug("fa.getInputBy() = " + fa.getInputBy());
			// for screen approval
			if (fa.getInputBy().equals(AMOUNT_PER_UNIT) || fa.getInputBy().equals(UNIT_PER_UNIT)) {
				log.debug("fa.getAmount() = " + fa.getAmount());
				log.debug("fa.getAmountPerUnit() = " + fa.getAmountPerUnit());
				fa.setTotal(fa.getAmount());
				fa.setRatio(fa.getAmountPerUnit());
				log.debug("fa.getTotal( = " + fa.getTotal());
				log.debug("fa.getRatio() = " + fa.getRatio());
				// fa.setAmount(null);
				// fa.setAmountPerUnit(null);
			}

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			log.debug("from " + from);

			String uniqeId = fa.getRgTradeTemp();
			RgTrade total = taService.totalDividen(uniqeId, fa.getFundActionKey());

			render("RegistryFundAction/approval.html", fa, mode, taskId, trades, keyId, from, total, uniqeId);
		} catch (Exception e) {
			log.debug("Error ---> " + e);
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long keyId, String uniqeId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId + " uniqeId: " + uniqeId);

		try {
			// process(keyId);
			// List<RgTrade> trades =
			// taService.listTradesByFundActionKey(keyId);
			// logger.debug("from approve >>> size trades = " + trades.size());
			//
			// for (RgTrade trade : trades) {
			// logger.debug("from approve >>> trade.tradeKey = " +
			// trade.getTradeKey());
			// taService.approveTrade(trade.getTradeKey(),
			// session.get(UIConstants.SESSION_USERNAME), null);
			// }
			//
			// RgFundAction fa = taService.approveFundAction(keyId,
			// session.get(UIConstants.SESSION_USERNAME), taskId);

			// di atas tidak transactional di ubah jadi transactional
			RgFundAction fa = taService.approveFundAction2(new Long(keyId), session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("fundAction.approved", fa.getFundActionKey())));
		} catch (MedallionException e) {
			if ((ExceptionConstants.INVALID_DATA + ".error.sentToFaInterface").equals(e.getErrorCode())) {
				e = new MedallionException("error.sentToFaInterface");
			}
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId, String uniqeId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId + " uniqeId: " + uniqeId);

		try {
			// List<RgTrade> trades =
			// taService.listTradesByFundActionKey(keyId);
			// for (RgTrade trade : trades) {
			// logger.debug("from reject >>> trade.tradeKey = " +
			// trade.getTradeKey());
			// taService.rejectTrade(trade.getTradeKey(),
			// session.get(UIConstants.SESSION_USERNAME), null);
			// }
			// RgFundAction fa = taService.rejectFundAction(keyId,
			// session.get(UIConstants.SESSION_USERNAME), taskId);

			// di atas tidak transactional di ubah jadi transactional
			RgFundAction fa = taService.rejectFundAction2(new Long(keyId), session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("fundAction.rejected", fa.getFundActionKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.registryFundAction")
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

		RgProduct rgprod = taService.loadProduct(fa.getRgProduct().getProductCode());

		GnSystemParameter gnSysDigitAmount = generalService.getSystemParameter(TaService.PRD_DIGIT_MGN_AMOUNT);
		GnSystemParameter gnSysDigitPrice = generalService.getSystemParameter(TaService.PRD_DIGIT_MGN_PRICE);
		GnSystemParameter gnSysDigitUnit = generalService.getSystemParameter(TaService.PRD_DIGIT_MGN_UNIT);

		if (fa != null) {
			//BigDecimal netAmount = BigDecimal.ZERO;
			//BigDecimal netAmountInvestorOption = BigDecimal.ZERO;

			BigDecimal amount = BigDecimal.ZERO;
			BigDecimal unit = BigDecimal.ZERO;
			BigDecimal price = BigDecimal.ZERO;
			BigDecimal totAmount = BigDecimal.ZERO;

			//BigDecimal totAmountRoundMinus = BigDecimal.ZERO;
			//DecimalFormat df = new DecimalFormat("#.##");

			if (fa.getRgProduct().isFixnav()) {
				if (fa.getRgProduct().getFixNavPrice() != null) {
					price = fa.getRgProduct().getFixNavPrice();
				} else {
					price = BigDecimal.ZERO;
				}

			} else {
				// jika rg_nav null
				// price = new
				// BigDecimal(fa.getPrice())==null?BigDecimal.ZERO:new
				// BigDecimal(fa.getPrice());
				if (fa.getPrice() != null) {
					price = new BigDecimal(fa.getPrice());
				} else {
					price = BigDecimal.ZERO;
				}
			}
			if (gnSysDigitPrice.getValue() != null && gnSysDigitPrice.getValue().equals("1"))
				price = Helper.format(price, rgprod.getPriceRoundValue().intValue(), FormatRoundHelper.getRoundMode(rgprod.getPriceRoundType()));
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
						if (price.compareTo(BigDecimal.ZERO) > 0) {
							if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_1)) {
								amount = portfolio.getUnit().divide(fa.getTotalUnit(), MathContext.DECIMAL128).multiply(fa.getTotAmount());

								if (fa.getRoundType() != null && fa.getRoundValue() != null) {
									if (fa.getRoundValue() >= 0) {
										amount = Helper.format(amount, fa.getRoundValue(), FormatRoundHelper.getRoundMode(fa.getRoundType()));
									}
								}
								if (gnSysDigitAmount.getValue() != null && gnSysDigitAmount.getValue().equals("1")) {
									amount = Helper.format(amount, rgprod.getAmountRoundValue().intValue(), FormatRoundHelper.getRoundMode(rgprod.getAmountRoundType()));
								}
								unit = amount.divide(price, MathContext.DECIMAL128);
								if (gnSysDigitUnit.getValue() != null && gnSysDigitUnit.getValue().equals("1"))
									unit = Helper.format(unit, rgprod.getUnitRoundValue().intValue(), FormatRoundHelper.getRoundMode(rgprod.getUnitRoundType()));
							}/*
							 * else if
							 * (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_2
							 * )){ logger.debug("UNIT = "+portfolio.getUnit().
							 * doubleValue());
							 * logger.debug("FAKTOR AMOUNT = "+fa
							 * .getFactorAmount().doubleValue());
							 * 
							 * unit =
							 * portfolio.getUnit().multiply(fa.getFactorAmount
							 * ()).divide(new BigDecimal(100));
							 * if(gnSysDigitUnit.getValue() != null
							 * &&gnSysDigitUnit.getValue().equals("1")) unit =
							 * Helper.format(unit,
							 * rgprod.getUnitRoundValue().intValue(),
							 * FormatRoundHelper
							 * .getRoundMode(rgprod.getUnitRoundType())); amount
							 * = unit.multiply(price);
							 * if(gnSysDigitAmount.getValue() != null
							 * &&gnSysDigitAmount.getValue().equals("1")) amount
							 * = Helper.format(amount,
							 * rgprod.getAmountRoundValue().intValue(),
							 * FormatRoundHelper
							 * .getRoundMode(rgprod.getAmountRoundType())); }
							 */else if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_3)) {
								amount = portfolio.getUnit().multiply(fa.getUnitAmount());
								if (gnSysDigitAmount.getValue() != null && gnSysDigitAmount.getValue().equals("1"))
									amount = Helper.format(amount, rgprod.getAmountRoundValue().intValue(), FormatRoundHelper.getRoundMode(rgprod.getAmountRoundType()));
								unit = amount.divide(price, MathContext.DECIMAL128);
								if (gnSysDigitUnit.getValue() != null && gnSysDigitUnit.getValue().equals("1"))
									unit = Helper.format(unit, rgprod.getUnitRoundValue().intValue(), FormatRoundHelper.getRoundMode(rgprod.getUnitRoundType()));
							}
						}

						trade.setAmount(amount);
					}
					trade.setUnit(unit);
					log.debug("unit ----> " + unit);

					if (RgFundAction.DIVTYPE_BY_CASH.equals(fa.getDivType())) {
						trade.setUnit(new BigDecimal(0));
					}

					totAmount = totAmount.add(amount);
					trade.setPrice(price);
					trades.add(trade);
				}
			}

			return trades;
		}
		return null;
	}

	@Check("transaction.registryFundAction")
	public static void cancel(Long keyId, String from, RgTrade rg) {
		log.debug("cancel. keyId: " + keyId + " from: " + from + " rg: " + rg);

		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgFundAction fa = taService.loadFundAction(keyId);
		// RgFundAction cancelFa =
		// taService.loadFundActionByCancelFundAction(fa.getFundActionKey());

		// fa.setStatus(getDecodeDataStatus(fa.getStatus()));
		if (fa.isCancelled()) {
			fa.setStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_CANCELED));
		} else {
			fa.setStatus(getDecodeDataStatus(fa.getStatus()));
		}

		// what the f*** ???? gax di f***ing pake
		// if ("cancelTradeApp".equals(from)) {
		// for (RgTrade tr :cancelFa.getRgTrades()){
		// rg = new RgTrade();
		// rg.setTradeKey(keyId);
		// rg.setCancelTradeDate(tr.getTradeDate());
		// rg.setCancelPostDate(tr.getPostDate());
		// rg.setCancelRemark(tr.getRemark());
		// }
		// }

		if (from.equals(FROM_CANCEL_DIVIDEN) || from.equals(FROM_UNIT_REGISTRY)) {
			if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_1))
				fa.setTotAmount(fa.getAmount());
			// if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_2))
			// fa.setFactorAmount(fa.getAmount());
			if (fa.getInputBy().equals(LookupConstants.DIVIDEN_BY_3))
				fa.setUnitAmount(fa.getAmount());
			// fa.setCancelTradeDate(currentBusinessDate);
			// fa.setCancelPostDate(currentBusinessDate);
			fa.setCancelTradeDate(fa.getDivDate());
			fa.setCancelPostDate(fa.getPostDate());
		}

		/*
		 * if (from.equals("cancelTradeError")) validation.addError("",
		 * messageError);
		 */

		// List<RgTrade> trades = listTrades(fa); ini di matikan gax lagi sumber
		// data dari calculate tetapi dari rg_trade_temp
		List<RgTrade> trades = new ArrayList<RgTrade>();
		// if
		// (AbstractRgFundAction.DIVTYPE_BY_REINVESTMENT.equals(fa.getDivType()))
		// {
		RgNav rgNav = taService.loadNav(new RgNavId(fa.getRgProduct().getProductCode(), fa.getNavDate()));
		if (rgNav != null)
			fa.setPrice(rgNav.getNav().doubleValue());
		// }

		// Sudah di set di bagian bawah ambil dari total bean;
		BigDecimal totalUnit = taService.loadPortfolioProduct(fa.getRgProduct().getProductCode(), fmtYYYYMMDD(fa.getCumDate()));
		fa.setTotalUnit(totalUnit);

		Long dataPaymentOnApproval = null;
		Long dataPaymentAlreadyPaid = null;

		BigDecimal dataPayment = taService.getTotalRowPaymentByStatus(fa.getFundActionKey(), LookupConstants.__RECORD_STATUS_APPROVED);
		if (dataPayment.intValue() > 0) {
			log.debug("dataPayment----> " + dataPayment); // ini artinya
															// sudah tidak
															// ada yang open
															// semua maka
															// munculin
															// Cannot cancel
															// an already
															// paid trade
			dataPaymentOnApproval = 0L;
		}

		BigDecimal dataPaymentPaid = taService.getTotalRowPaymentByStatus(fa.getFundActionKey(), LookupConstants.__RECORD_STATUS_OPEN);
		if (dataPaymentPaid.intValue() > 0) {
			log.debug("dataPaymentPaid----> " + dataPaymentPaid);
			dataPaymentAlreadyPaid = 0L; // ini artinya blm di approve, maka
											// munculin There is an unapproved
											// payment for this trade on payment
											// approval work list
		}

		String messageError = "";
		boolean isUnitGraterThenPortoUnit = taService.isUnitGreaterThenPortoUnitOrNull(fa.getFundActionKey());
		if (isUnitGraterThenPortoUnit) {
			messageError = "Portfolio unit is null or Trade Unit is trade greater than unit in portfolio !";
		}

		// List<RgTrade> rgTrades = taService.listTradeByFundAction(keyId);
		// for (RgTrade rgTrade : rgTrades) {
		// logger.debug("TRADE KEY = " +rgTrade.getTradeKey());
		// // if ((rgTrade.getRgPaymentDetail() !=
		// null)||(rgTrade.getRgPayment() != null)){
		// //
		// // Long dataPayment =
		// taService.chekcPaymentOnApprovalList(rgTrade.getTradeKey());
		// // Long dataPaymentPaid =
		// taService.chekcPaymentAlreadyPaid(rgTrade.getTradeKey());
		// // if (dataPayment == 0) { // bararti tidak ada yang open, errorin
		// // logger.debug("dataPayment----> " +dataPayment); // ini artinya
		// sudah tidak ada yang open semua maka munculin Cannot cancel an
		// already paid trade
		// // dataPaymentOnApproval = dataPayment;
		// // }
		// // if (dataPaymentPaid == 0) {
		// // logger.debug("dataPaymentPaid----> " +dataPaymentPaid);
		// // dataPaymentAlreadyPaid = dataPaymentPaid; // ini artinya blm di
		// approve, maka munculin There is an unapproved payment for this trade
		// on payment approval work list
		// // }
		// // }
		//
		// logger.debug("[CANCEL] HOLDINGDATE => " + fa.getDivDate());
		// BigDecimal unitPorto =
		// taService.loadUnitPortfolioByTradeCancel(rgTrade.getTradeKey(),
		// fa.getDivDate());
		// if (rgTrade.getTradeStatus() != null &&
		// rgTrade.getTradeStatus().equals(RgTrade.TRADESTATUS_VALID) &&
		// rgTrade.getRgTransaction() != null &&
		// rgTrade.getRgTransaction().getType().equals(RgTransaction.TRANSTYPE_SUBSCRIBE)){
		// if (unitPorto == null) {
		// messageError = "Unit Portfolio null !";
		// } else {
		// if (rgTrade.getRgTransaction().getUnit().compareTo(unitPorto) > 0)
		// messageError = "Unit this trade greater than unit in portfolio !";
		// }
		// }
		// if (!"".equals(messageError)) break;
		// }

		log.debug("dataPaymentOnApproval = " + dataPaymentOnApproval);
		log.debug("dataPaymentAlreadyPaid = " + dataPaymentAlreadyPaid);

		boolean warningMsg = false;
		List<String> tradesNo = new ArrayList<String>();
		List<Object> tradeByRedeems = taService.listTradeRedeemByFundAction2(fa.getFundActionKey());
		if ((!tradeByRedeems.isEmpty()) && (tradeByRedeems.size() > 0)) {
			warningMsg = true;
			for (Object object : tradeByRedeems) {
				tradesNo.add(object.toString());
			}
		}

		// ambil uniqeid
		try {
			log.debug(RgFundAction.class.getName() + "_" + keyId + "_" + LookupConstants.MAINTENANCE_OPERATION_CREATE.toString());
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(RgFundAction.class.getName(), String.valueOf(keyId), new GnLookup(LookupConstants.MAINTENANCE_OPERATION_CREATE));
			RgFundAction logfa = json.readValue(maintenanceLog.getNewData(), RgFundAction.class);
			fa.setRgTradeTemp(logfa.getRgTradeTemp());
			log.debug("JSON " + fa.getRgTradeTemp());
		} catch (Exception ee) {
			log.debug(ee.getMessage(), ee);
		}

		RgTrade total = taService.totalDividen(fa.getRgTradeTemp(), fa.getFundActionKey());
		// fa.setTotalUnit(total.getTotUnit());

		if (from.equals("unitRegistry"))
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_INQUIRY_REGISTRY));
		else
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
		render("RegistryFundAction/cancel.html", total, fa, mode, from, warningMsg, tradesNo, trades, dataPaymentAlreadyPaid, dataPaymentOnApproval, keyId, currentBusinessDate, messageError);
	}

	@Check("transaction.registryFundAction")
	protected static RgFundAction validationFundAction(RgFundAction fa) {
		log.debug("validationFundAction. fa: " + fa);

		if (fa.getRgProduct() != null) {
			if (fa.getRgProduct().getDivLock() == true) {
				List<RgProductLockException> lockExceptionDates = taService.listRgProductLockExceptionByType(fa.getRgProduct().getProductCode(), UIConstants.TRADE_TYPE_DIVIDEND);
				log.debug("[validation] size lockExceptionDates = " + lockExceptionDates.size());
				boolean checkSame = true;
				for (RgProductLockException le : lockExceptionDates) {
					log.debug("[validation] red.divDate = " + fa.getDivDate() + "same with exceptionDate = " + le.getId().getExceptionDate());
					if (fa.getDivDate().getTime() == le.getId().getExceptionDate().getTime()) {
						checkSame = false;
						break;
					}
				}
				if (checkSame) {
					validation.addError("", "fundAction.not_allowed_same_with_exceptionDate");
				}
			}

			if (fa.getDivDate().getTime() < fa.getRgProduct().getEffectiveDate().getTime()) {
				validation.addError("", "fundAction.dividendDate.lt.effectiveDate");
			}
			if (fa.getDivDate().getTime() > fa.getRgProduct().getLiquidDate().getTime()) {
				validation.addError("", "fundAction.dividendDate.gt.liquidDate");
			}
		}
		return fa;
	}

	private static String getDecodeDataStatus(String statusCode) {
		log.debug("getDecodeDataStatus. statusCode: " + statusCode);

		return StatusExtension.decodeDataStatus(statusCode);
	}
}