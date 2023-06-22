package controllers;

import helpers.UIConstants;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.RgFeeTier;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgNavId;
import com.simian.medallion.model.RgProdEod;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.vo.RgTradeItem;

public class RegistryValidation extends Registry {
	private static Logger log = Logger.getLogger(RegistryValidation.class);

	private static final BigDecimal BD_100 = new BigDecimal(100);

	@Check("transaction.registryValidation")
	public static void list() {
		log.debug("list. ");

		RgTrade val = new RgTrade();
		val.setGoodfundDate(taService.getCurrentBusinessDate());

		List<RgTrade> trades = new ArrayList<RgTrade>(0);
		boolean readOnly = false;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_TRANSACTION_VALIDATION));
		render("RegistryValidation/list.html", val, trades, readOnly);
	}

	@Check("transaction.registryValidation")
	public static void next(RgTrade val) throws ParseException {
		log.debug("next. val: " + val);

		if (val == null)
			list();

		validation.required("Fund Code is", val.getRgProduct().getProductCode());
		validation.required("Good Fund Date is", val.getGoodfundDate());
		if (validation.hasErrors()) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_TRANSACTION_VALIDATION));
			render("RegistryValidation/list.html", val);
		} else {
			log.debug("*****************val.isReProcessAll():" + val.isReProcessAll());
			List<RgTrade> trades = taService.listTransactionValidation(val.getRgProduct().getProductCode(), val.getGoodfundDate(), val.isReProcessAll());

			boolean readOnly = trades.size() > 0 ? true : false;
			List<RgTradeItem> summaries = summary(trades);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_TRANSACTION_VALIDATION));
			render("RegistryValidation/list.html", val, trades, summaries, readOnly);
		}

	}

	@Check("transaction.registryValidation")
	public static void process(RgTrade val) throws ParseException {
		log.debug("process. val: " + val);

		List<RgTrade> trades = taService.transactionValidation(val.getRgProduct().getProductCode(), val.getGoodfundDate(), val.isReProcessAll());
		boolean readOnly = trades.size() > 0 ? true : false;

		List<String> listNotValid = new ArrayList<String>();
		List<RgTrade> newTrades = new ArrayList<RgTrade>();
		for (RgTrade rgTrade : trades) {
			if (!rgTrade.getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID)) {
				listNotValid.add("Not Valid");
				newTrades.add(rgTrade);
			}
		}
		trades = newTrades;
		String notValid = "";
		if (!listNotValid.isEmpty())
			notValid = listNotValid.get(0);
		if (listNotValid.size() > 0) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_TRANSACTION_VALIDATION));
			render("RegistryValidation/list.html", val, trades, readOnly, notValid);
		} else {
			list();
		}
		// render("RegistryValidation/list.html", val, trades);
		// list();
	}

	private static List<RgTradeItem> summary(List<RgTrade> trades) {
		log.debug("summary. trades: " + trades);

		Map<String, RgTradeItem> maps = new HashMap<String, RgTradeItem>();
		BigDecimal limitFromProduct = BigDecimal.ZERO;
		BigDecimal limitRedeem = BigDecimal.ZERO;
		BigDecimal limitRedeemSwitching = BigDecimal.ZERO;
		String limitRule = null;
		for (RgTrade rgTrade : trades) {

			// // if price null, by default calculate bid price (type Redeem)
			// if (rgTrade.getType().equals(RgTrade.TRADETYPE_REDEEM)){
			// logger.debug("PRODUCT CODE = "+rgTrade.getRgProduct().getProductCode());
			// logger.debug("NAV DATE = " +rgTrade.getNavDate());
			// try {
			// if (rgTrade.getPrice() == null) {
			// RgNav nav =
			// taService.loadActiveNav(rgTrade.getRgProduct().getProductCode(),
			// rgTrade.getNavDate());
			// logger.debug("NAV Redeem = " +nav);
			// if (nav == null)
			// throw new MedallionException(ExceptionConstants.DATA_NOTFOUND);
			//
			// BigDecimal newPrice = BigDecimal.ZERO;
			// BigDecimal bidPrice = BigDecimal.ZERO;
			// BigDecimal navPrice = BigDecimal.ZERO;
			// if (nav.getBidPricePct() != null) {
			// logger.debug("BID PCT = " +nav.getBidPricePct());
			// bidPrice =
			// BigDecimal.ONE.add(nav.getBidPricePct().divide(BD_100));
			// navPrice =
			// (BigDecimal.ONE.add(nav.getBidPricePct().divide(BD_100))).multiply(nav.getNav());
			// newPrice = bidPrice.multiply(navPrice);
			// logger.debug("NEW PRICE REDEEM = " +newPrice);
			// rgTrade.getRgTransaction().setPrice(newPrice);
			// }
			// } else {
			// rgTrade.getRgTransaction().setPrice(rgTrade.getPrice());
			// }
			// } catch (MedallionException e) {
			// flash.put("validationError","Nav price for date "
			// +fmtMMDDYYYY(rgTrade.getNavDate())+", "+Messages.get(e.getMessage()));
			// list();
			// }
			// }
			//
			// // if price null, by default calculate offer price (type
			// Subscribe)
			// if (rgTrade.getType().equals(RgTrade.TRADETYPE_SUBSCRIBE)){
			// try {
			// if (rgTrade.getPrice() == null) {
			// RgNav nav =
			// taService.loadActiveNav(rgTrade.getRgProduct().getProductCode(),
			// rgTrade.getNavDate());
			// logger.debug("NAV SUBS = " +nav);
			// if (nav == null)
			// throw new MedallionException(ExceptionConstants.DATA_NOTFOUND);
			//
			// BigDecimal newPrice = BigDecimal.ZERO;
			// BigDecimal offerPrice = BigDecimal.ZERO;
			// BigDecimal navPrice = BigDecimal.ZERO;
			// if (nav.getOfferPricePct() != null) {
			// logger.debug("OFFER PCT = " +nav.getOfferPricePct());
			// offerPrice =
			// BigDecimal.ONE.add(nav.getOfferPricePct().divide(BD_100));
			// navPrice =
			// (BigDecimal.ONE.add(nav.getOfferPricePct().divide(BD_100))).multiply(nav.getNav());
			// newPrice = offerPrice.multiply(navPrice);
			// rgTrade.getRgTransaction().setPrice(newPrice);
			// } else {
			// rgTrade.getRgTransaction().getPrice();
			// }
			//
			// } else {
			// rgTrade.getRgTransaction().setPrice(rgTrade.getPrice());
			// }
			// }catch (MedallionException e) {
			// flash.put("validationError","Nav price for date "
			// +fmtMMDDYYYY(rgTrade.getNavDate())+", "+Messages.get(e.getMessage()));
			// list();
			// }
			// }

			if (LookupConstants.__TRADE_VALIDATION_VALID.equals(rgTrade.getMessage())) {
				RgTradeItem item = maps.get(rgTrade.getType());

				if (item == null) {
					BigDecimal limit = BigDecimal.ZERO;
					if (RgTrade.TRADETYPE_REDEEM.equals(rgTrade.getType())) {
						// limit = rgTrade.getRgProduct().getMaxBalAmt();
						limit = rgTrade.getRgProduct().getRedLimitAmt();
						limitRedeem = limit;
						if (limit == null) {
							BigDecimal balance = taService.loadLastPortfolioBalanceTotal(rgTrade.getRgProduct().getProductCode(), rgTrade.getGoodfundDate());
							// limit =
							// rgTrade.getRgProduct().getMaxBalAmtPct().divide(BD_100).multiply(balance);

							// get ex: RegistrySubscription method entry for
							// redeem
							RgProduct product = taService.loadProduct(rgTrade.getRgProduct().getProductCode());
							// split from other tables
							product.splitProductFees();
							// assume only one redeem fee?
							RgFeeTier subFee = product.getSubFees().get(0);

							log.debug(" >>> subFee.getValue() = " + subFee.getValue() + " >>> balance = " + balance);
							log.debug("******************* >>> subFee.getValue() = " + subFee.getValue() + " >>> balance = " + balance);

							limit = subFee.getValue().divide(BD_100).multiply(balance);
							log.debug("*************************limit:" + limit);
							limitRedeem = limit;
						}
					}

					if (RgTrade.TRADETYPE_REDEEM_SWITCHING.equals(rgTrade.getType())) {
						// limit = rgTrade.getRgProduct().getMaxBalUnit();
						limit = rgTrade.getRgProduct().getSwoLimitAmt();
						limitRedeemSwitching = limit;
						if (limit == null) {
							BigDecimal balance = taService.loadLastPortfolioBalanceTotal(rgTrade.getRgProduct().getProductCode(), rgTrade.getGoodfundDate());
							// limit =
							// rgTrade.getRgProduct().getMaxBalUnitPct().divide(BD_100).multiply(balance);

							RgProduct product = taService.loadProduct(rgTrade.getRgProduct().getProductCode());
							// split from other tables
							product.splitProductFees();
							// assume only one redeem fee?
							RgFeeTier subFee = product.getSubFees().get(0);
							limit = subFee.getValue().divide(BD_100).multiply(balance);
							limitRedeemSwitching = limit;
						}

					}
					// check null rgTrade.rgTrans]
					if (rgTrade.getRgTransaction() != null) {
						item = new RgTradeItem(rgTrade.getType(), rgTrade.getRgTransaction().getNetAmount(), rgTrade.getRgTransaction().getUnit(), new BigDecimal(1), limit);

						maps.put(item.getType(), item);
					}
				} else {
					if (rgTrade.getRgTransaction() != null) {
						item.add(rgTrade.getRgTransaction().getNetAmount(), rgTrade.getRgTransaction().getUnit());
					}
				}
			}
			limitRule = rgTrade.getRgProduct().getLimitrule();

		}

		RgTradeItem redeem = maps.get(RgTrade.TRADETYPE_REDEEM);
		RgTradeItem redeemSwitching = maps.get(RgTrade.TRADETYPE_REDEEM_SWITCHING);
		BigDecimal totalAmount = BigDecimal.ZERO;
		NumberFormat formatDecNumber = new DecimalFormat("#,##0.00");
		if (redeem != null && redeemSwitching != null) {
			if (RgProduct.LIMIT_RULE_ASSUM.equals(limitRule)) {
				limitFromProduct = limitRedeem.add(limitRedeemSwitching);
				if (redeemSwitching == null) {
					totalAmount = redeem.getAmount().add(BigDecimal.ZERO);
					if (totalAmount.compareTo(limitFromProduct) > 0) {
						validation.addError("Warning ! ", "Total Amount Redeem (" + formatDecNumber.format(totalAmount) + ") greather than their Total Limit (" + formatDecNumber.format(limitFromProduct) + ")");
					}
				} else if (redeem == null) {
					totalAmount = redeemSwitching.getAmount().add(BigDecimal.ZERO);
					validation.addError("Warning ! ", "Total Amount Redeem Switching (" + formatDecNumber.format(totalAmount) + ") greather than their Total Limit (" + formatDecNumber.format(limitFromProduct) + ")");
				} else {
					totalAmount = redeem.getAmount().add(redeemSwitching.getAmount());
					if (totalAmount.compareTo(limitFromProduct) > 0) {
						validation.addError("Warning ! ", "Total Amount Redeem and Redeem Switching (" + formatDecNumber.format(totalAmount) + ") greather than their Total Limit (" + formatDecNumber.format(limitFromProduct) + ")");
					}
				}
			}

			// Warnig
			if (RgProduct.LIMIT_RULE_SEPARATE.equals(limitRule)) {
				if (redeem == null) {
					if (redeemSwitching.getAmount().compareTo(limitRedeemSwitching) > 0) {
						validation.addError("Warning ! ", "Total Amount Redeem Switching (" + formatDecNumber.format(redeemSwitching.getAmount()) + ") greather than its Total Limit (" + formatDecNumber.format(limitRedeemSwitching) + ")");
					}
				} else if (redeemSwitching == null) {
					if (redeem.getAmount().compareTo(limitRedeem) > 0) {
						validation.addError("Warning ! ", "Total Amount Redeem (" + formatDecNumber.format(redeem.getAmount()) + ") greather than its Total Limit (" + formatDecNumber.format(limitRedeem) + ")");
					}
				} else {
					if ((redeem.getAmount().compareTo(limitRedeem) > 0) && (redeemSwitching.getAmount().compareTo(limitRedeemSwitching) > 0)) {
						validation.addError("Warning ! ", "Total Amount Redeem (" + formatDecNumber.format(redeem.getAmount()) + ") greather than its Total Limit (" + formatDecNumber.format(limitRedeem) + ") and Total Amount Redeem Switching (" + formatDecNumber.format(redeemSwitching.getAmount()) + ") greather than its Total Limit (" + formatDecNumber.format(limitRedeemSwitching) + ")");
					}

					if ((redeem.getAmount().compareTo(limitRedeem) > 0) && (redeemSwitching.getAmount().compareTo(limitRedeemSwitching) < 0)) {
						validation.addError("Warning ! ", "Total Amount Redeem (" + formatDecNumber.format(redeem.getAmount()) + ") greather than its Total Limit (" + formatDecNumber.format(limitRedeem) + ")");
					}

					if ((redeemSwitching.getAmount().compareTo(limitRedeemSwitching) > 0) && (redeem.getAmount().compareTo(limitRedeem) < 0)) {
						validation.addError("Warning ! ", "Total Amount Redeem Switching (" + formatDecNumber.format(redeemSwitching.getAmount()) + ") greather than its Total Limit (" + formatDecNumber.format(limitRedeemSwitching) + ")");
					}
				}
			}
		}
		return new ArrayList<RgTradeItem>(maps.values());
	}

	public static void setGoodFundDate(String productCode) {
		log.debug("setGoodFundDate. productCode: " + productCode);

		RgProdEod prodEod = taService.getProdEodByProductCode(productCode);
		if (prodEod != null) {
			Date eodDate = prodEod.getEodDate();
			renderText(eodDate.getTime());
		} else {
			Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			renderText(currentBusinessDate.getTime());
		}
	}

	public static void edit(String id) {
		log.debug("edit. id: " + id);
	}

	public static void view(String id) {
		log.debug("view. id: " + id);
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void save(RgNav nav, String mode) {
		log.debug("save. nav: " + nav + " mode: " + mode);
	}

	public static void confirming(RgNavId id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);
	}

	public static void confirm(RgNav nav, String mode) {
		log.debug("confirm. nav: " + nav + " mode: " + mode);
	}

	public static void back(RgNavId id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);
	}
}
