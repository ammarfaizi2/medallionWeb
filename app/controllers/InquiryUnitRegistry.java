package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.Before;
import vo.RegistryCancelTradeSearchParameters;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.AbstractRgFundAction;
import com.simian.medallion.model.AbstractRgPayment;
import com.simian.medallion.model.AbstractRgSwitching;
import com.simian.medallion.model.AbstractRgTrade;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgFundAction;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgSwitching;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.RgTransaction;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class InquiryUnitRegistry extends Registry {
	private static Logger log = Logger.getLogger(InquiryPortfolioTransaction.class);

	public static final String TRADETYPEONSCREEN_SUBSCRIPTION = "Subscription";

	@Before
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
	}

	@Check("transaction.inquiryUnitRegistry")
	public static void list() {
		log.debug("list. ");

		// String mode = UIConstants.DISPLAY_MODE_ENTRY;
		//
		// RgTrade rg = new RgTrade();
		// rg.setType(RgTrade.TRADETYPE_SUBSCRIBE);
		// rg.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER);
		// rg.setTradeDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		// rg.setRedemRefKey(null);
		// //rg.setViewFrom("InquiryUnitRegistry");
		//
		// render(mode,rg);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_INQUIRY_REGISTRY));
		RegistryCancelTradeSearchParameters param = new RegistryCancelTradeSearchParameters();
		param.setRgProduct(new RgProduct());
		param.setType(RgTrade.TRADETYPE_SUBSCRIBE);
		param.setTypeOrder(RgTrade.TRADETYPEORDER_OTHER);
		param.setRedemRefKey(null);
		param.setDispatch(RegistryCancelTradeSearchParameters.DISPATCH_VIEW);

		render("InquiryUnitRegistry/list.html", param);
	}

	public static void pagingInquiryRegistryUnitSubscribe(Paging page, RegistryCancelTradeSearchParameters param) {
		log.debug("pagingInquiryRegistryUnitSubscribe. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		if (param.isQuery()) {
			// page.addParams("rt.posted", page.EQUAL, 1);
			// page.addParams("rt.cancelled", page.EQUAL, 0);
			page.addParams("rt.type", page.EQUAL, param.getType());
			page.addParams("rt.tradeDate", page.GREATEQUAL, param.getTradeDateFrom());
			page.addParams("rt.tradeDate", page.LESSEQUAL, param.getTradeDateTo());

			if (param.getRgProduct() != null) {
				page.addParams("rt.rgProduct.productCode", page.EQUAL, param.getRgProduct().getProductCode());
			}

			page.addParams("rt.tradeKey", param.getTransactionNoOperator(), UIHelper.withOperator(param.getRedemRefKey(), param.getTransactionNoOperator()));
			page.addParams(Helper.searchAll("rt.tradeKey || rt.rgProduct.productCode || rt.rgInvestmentaccount.accountNumber || " + "rt.rgInvestmentaccount.name || rt.tradeStatus || rt.unit || rt.price || " + "rt.amount||" + "to_char(rt.tradeDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = taService.pagingInquiryRegistryUnitSubscribe(page);
		}
		renderJSON(page);
	}

	@Check("transaction.inquiryUnitRegistry")
	public static void pagingInquiryRegistryUnitRedeem(Paging page, RegistryCancelTradeSearchParameters param) {
		log.debug("pagingInquiryRegistryUnitRedeem. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		if (param.isQuery()) {
			// page.addParams("rt.posted", page.EQUAL, 1);
			// page.addParams("rt.cancelled", page.EQUAL, 0);
			page.addParams("rt.type", page.EQUAL, param.getType());
			page.addParams("rt.tradeDate", page.GREATEQUAL, param.getTradeDateFrom());
			page.addParams("rt.tradeDate", page.LESSEQUAL, param.getTradeDateTo());

			if (param.getRgProduct() != null) {
				page.addParams("rt.rgProduct.productCode", page.EQUAL, param.getRgProduct().getProductCode());
			}

			page.addParams("rt.tradeKey", param.getTransactionNoOperator(), UIHelper.withOperator(param.getRedemRefKey(), param.getTransactionNoOperator()));
			page.addParams(Helper.searchAll("rt.tradeKey || rt.rgProduct.productCode || rt.rgInvestmentaccount.accountNumber || " + "rt.rgInvestmentaccount.name || rt.tradeStatus || rt.unit || rt.price || " + "rt.amount||" + "to_char(rt.tradeDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = taService.pagingInquiryRegistryUnitRedeem(page);
		}
		renderJSON(page);
	}

	@Check("transaction.inquiryUnitRegistry")
	public static void pagingInquiryRegistryUnitSwitching(Paging page, RegistryCancelTradeSearchParameters param) {
		log.debug("pagingInquiryRegistryUnitSwitching. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		if (param.isQuery()) {
			// page.addParams("rs.cancelled", page.EQUAL, 0);
			page.addParams("rs.type", page.EQUAL, param.getType());
			page.addParams("rs.switchDate", page.GREATEQUAL, param.getTradeDateFrom());
			page.addParams("rs.switchDate", page.LESSEQUAL, param.getTradeDateTo());
			// page.addParams("rs.tradeStatus", page.EQUAL,
			// LookupConstants.__RECORD_STATUS_APPROVED);

			if (param.getRgProduct() != null) {
				page.getParamFixMap().put("productCode", param.getRgProduct().getProductCode());
				// page.addParams("rs.rgInvestmentaccountByOutAccountNumber.rgProduct.productCode",
				// page.EQUAL, param.getRgProduct().getProductCode());
			}

			page.addParams("rs.switchingKey", param.getTransactionNoOperator(), UIHelper.withOperator(param.getRedemRefKey(), param.getTransactionNoOperator()));
			page.addParams(Helper.searchAll("rs.switchingKey || rs.tradeStatus"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page.getParamFixMap().put("redemRefKey", param.getRedemRefKey());

			page = taService.pagingInquiryRegistryUnitSwitching(page);
		}
		renderJSON(page);
	}

	@Check("transaction.inquiryUnitRegistry")
	public static void pagingInquiryRegistryUnitDividend(Paging page, RegistryCancelTradeSearchParameters param) {
		log.debug("pagingInquiryRegistryUnitDividend. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		if (param.isQuery()) {
			// page.addParams("rf.cancelled", page.EQUAL, 0);
			page.addParams("rf.type", page.EQUAL, param.getType());
			page.addParams("rf.divDate", page.GREATEQUAL, param.getTradeDateFrom());
			page.addParams("rf.divDate", page.LESSEQUAL, param.getTradeDateTo());
			// page.addParams("rf.status", page.EQUAL,
			// LookupConstants.__RECORD_STATUS_APPROVED);

			if (param.getRgProduct() != null) {
				page.addParams("rf.rgProduct.productCode", page.EQUAL, param.getRgProduct().getProductCode());
			}

			page.addParams("rf.fundActionKey", param.getTransactionNoOperator(), UIHelper.withOperator(param.getRedemRefKey(), param.getTransactionNoOperator()));
			page.addParams(Helper.searchAll("rf.fundActionKey || rf.status || rf.amount || rf.divDate"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page.getParamFixMap().put("redemRefKey", param.getRedemRefKey());

			page = taService.pagingInquiryRegistryUnitDividend(page);
		}
		renderJSON(page);
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	public static void search() {
		log.debug("search. ");

		render("InquiryUnitRegistry/list.html");
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);
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
				if (rgTrade.getRgTransaction() != null)
					priceSubRed = rgTrade.getRgTransaction().getPrice();
				else
					priceSubRed = rgTrade.getPrice();
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

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_INQUIRY_REGISTRY));
		render("InquiryUnitRegistry/list.html", mode, rg, rgTrades, priceSubRed, rgSwitchings, rgFundActions, navPrice, amountForSwitching, priceForSwitching, fundCodeTo, navPrice);
	}

	public static void save() {
		log.debug("save. ");
	}

	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);
	}

	public static void confirm() {
		log.debug("confirm. ");
	}

	public static void back() {
		log.debug("back. ");
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);
	}
}