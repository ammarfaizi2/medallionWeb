package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.TradeSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.AbstractRgTrade;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnTaxMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgFeeTier;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgPortfolio;
import com.simian.medallion.model.RgProdEod;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgProductLockException;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import extensions.StatusExtension;

@With(Secure.class)
public class RegistryRedemption extends Registry {
	private static Logger log = Logger.getLogger(RegistryRedemption.class);
	private static final String FROM_UNIT_REGISTRY = "unitRegistry";
	private static final String FROM_CANCEL_TRADE = "cancelTrade";

	public static final String SPECIAL_CHAR = ",,";

	private static BigDecimal getTaxAmt(RgTrade red) {
		log.debug("getTaxAmt. red: " + red);

		double taxPct = (red.getTaxPct() == null) ? 0 : red.getTaxPct().doubleValue();
		double feeAmt = (red.getFeeAmt() == null) ? 0 : red.getFeeAmt().doubleValue();
		double discAmt = (red.getDiscAmt() == null) ? 0 : red.getDiscAmt().doubleValue();
		double otherAmt = (red.getOtherAmt() == null) ? 0 : red.getOtherAmt().doubleValue();
		double taxAmt = 0;
		if (taxPct != 0D) {
			taxAmt = ((taxPct / 100) * (feeAmt - discAmt + otherAmt));
		}
		return new BigDecimal(taxAmt);
	}

	@Before(only = { "view", "entry", "save", "confirming", "confirm", "back", "approval", "cancel", "edit", "search" })
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("operators", UIHelper.yesNoOperators());
		renderArgs.put("redemptionOperators", UIHelper.redeemOperators());

		renderArgs.put("stringOperators", UIHelper.stringOperators());

		List<SelectItem> transactionBy = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TRANSACTION_BY);
		renderArgs.put("transactionBy", transactionBy);

		renderArgs.put("domainBankRed", LookupConstants._DOMAIN_BANK_RED);

		renderArgs.put("specialChar", SPECIAL_CHAR);
	}

	public static void list() {
		log.debug("list. ");

		List<RgTrade> reds = taService.listTrade(AbstractRgTrade.TRADETYPE_REDEEM);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
		render("RegistryRedemption/list.html", reds);
	}

	@Check("transaction.registryLiquidationProcess")
	public static void search() {
		log.debug("search. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
		TradeSearchParameters params = new TradeSearchParameters();
		render("RegistryRedemption/search.html", params);
	}

	// public static void edit(Long id) {
	// String mode = UIConstants.DISPLAY_MODE_EDIT;
	// RgTrade red = taService.loadTrade(id);
	// red.setRgProduct(red.getRgInvestmentaccount().getRgProduct());
	// red.setTaxPct(red.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
	// red.setTaxAmt(getTaxAmt(red));
	// render("RegistryRedemption/detail.html", red, mode);
	// }

	@Check("transaction.registryLiquidationProcess")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgTrade red = taService.loadTrade(id);

		/**
		 * ii. Point 2 : remarks untuk view/insert kerg_trade (untuk view, price
		 * diambildarirg_navberdasarkannav_date, unit berdasarkan amount/price )
		 * 1. Kalau fix nav == 1, ambil dari navprice dari rg_product, 2. Kalau
		 * fix nav == 0, ambil dari rg_nav berdasarkan nav date, product 3.
		 * Langsung bagi rg_trade.amount ke priced di poin 1/2
		 */
		BigDecimal newPrice = null;
		if (red.getRgProduct().isFixnav()) {
			newPrice = red.getRgProduct().getFixNavPrice();
		} else {
			RgNav rgNav = taService.loadActiveNav(red.getRgProduct().getProductCode(), red.getNavDate());
			if (rgNav != null) {
				newPrice = rgNav.getNav();
			}
		}
		red.setPrice(newPrice);

		if (newPrice != null) {
			BigDecimal newUnit = BigDecimal.ZERO;
			newUnit = red.getAmount().divide(newPrice);
			red.setUnit(newUnit);
		}

		/*
		 * end of point 2
		 */

		red.setRgProduct(red.getRgInvestmentaccount().getRgProduct());
		red.setTaxPct(red.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		red.setTaxAmt(getTaxAmt(red));
		red.setTrnSABranch(generalService.getThirdParty(red.getTrnSABranchKey()));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
		render("RegistryRedemption/entry.html", red, mode);
	}

	@Check("transaction.registryLiquidationProcess")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		RgTrade red = new RgTrade();
		red.setTradeDate(taService.getCurrentBusinessDate());
		red.setGoodfundDate(red.getTradeDate());
		red.setFeePct(BigDecimal.ZERO);
		red.setFeeAmt(null);
		// red.setFeeCap(new Boolean(false));

		// red.setDiscPct(BigDecimal.ZERO);

		GnTaxMaster taxMaster = generalService.getTaxMaster("NOT");
		red.setTaxMaster(taxMaster);

		GnLookup lookupUnit = generalService.getLookup(LookupConstants.TRANSACTION_BY_UNIT);
		red.setTradeBy(lookupUnit);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
		render("RegistryRedemption/entry.html", red, mode);
	}

	public static void save(RgTrade red, String mode, String feeby, RgTrade rg, String from) {
		log.debug("save. red: " + red + " mode: " + mode + " feeby: " + feeby + " rg: " + rg + " from: " + from);

		if (red == null)
			entry();
		RgProduct product = taService.loadProduct(red.getRgProduct().getProductCode());

		validation.required("Fund Code is", red.getRgProduct().getProductCode());
		validation.required("Investment Acct is", red.getRgInvestmentaccount().getAccountNumber());
		validation.required("Transaction Date is", red.getTradeDate());
		validation.required("NAV Date is", red.getNavDate());
		validation.required("Post Date is", red.getPostDate());
		validation.required("Payment Date is", red.getPaymentDate());
		validation.required("SA Code is", red.getRgInvestmentaccount().getThirdPartyBySaCode().getThirdPartyKey());
		validation.required("Fund Bank Account is", red.getRgProductBnAccount().getBankAccountKey());
		validation.required("Investor Bank Account is", red.getBankAccount().getBankAccountKey());
		// validation.required("Available Unit is", red.getAvailabelUnit());
		// validation.required("Available Balance is",
		// red.getAvailabelBalance());
		// validation.required("Trn SA Branch is",
		// red.getTrnSABranch().getThirdPartyKey());
		validation.required("Transaction By is", red.getTradeBy().getLookupId());
		validation.required("Total Fee Amt is", red.getTotFeeAmt());
		// validation.required("Net Amount is", red.getAmount());
		// validation.required("Price is", red.getPrice());
		// validation.required("Unit is", red.getUnit());
		// validation.required("Cap Gain Tax is",
		// red.getTaxMaster().getTaxKey());
		// validation.required("Payment Amount is", red.getPaymentAmount());
		validation.required("Investment Acc is", red.getRgInvestmentaccount().getAccountNumber());
		// validation.required("redemption.netAmount", red.getNetAmount());

		if (product != null) {
			// split from other tables
			product.splitProductFees();
			// assume only one redeem fee?
			RgFeeTier redFee = product.getRedFees().get(0);
			product.setRedMaxFee(redFee.getMaxValue());
		}
		RgPortfolio portfolio = null;
		if (red.getRgInvestmentaccount() != null && red.getPostDate() != null) {
			portfolio = taService.loadLastPortfolio(red.getRgInvestmentaccount().getAccountNumber(), red.getPostDate());
			// portfolio =
			// taService.loadPortfolio(red.getRgInvestmentaccount().getAccountNumber(),
			// fmtYYYYMMDD(red.getPostDate()));
			BigDecimal outstandingUnit = taService.loadOutstanding(red.getRgProduct().getProductCode(), red.getRgInvestmentaccount().getAccountNumber(), red.getPostDate());

			RgNav nav = null;
			if (red.getRgProduct() != null && !red.getRgProduct().getProductCode().isEmpty()) {
				nav = taService.loadLatestNav(red.getRgProduct().getProductCode());
			}

			if (portfolio != null)
				portfolio.setUnit(portfolio.getUnit().subtract(outstandingUnit));
			// if (portfolio != null) red.setUnit(portfolio.getUnit());
			if (portfolio != null)
				red.setAvailabelUnit(portfolio.getUnit());
			if (portfolio != null && nav != null)
				red.setAvailabelBalance(red.getAvailabelUnit().multiply(nav.getNav()));
			if (red.getLiquidation()) {
				// TODO: Change By Fadly 2017-10-30 Redmine #3326
				if (portfolio != null) {
					if (!FROM_CANCEL_TRADE.equals(from))
						red.setUnit(portfolio.getUnit());
				}
				// END Change Redmine #3326
				red.setTradeBy(generalService.getLookup(LookupConstants.TRANSACTION_BY_UNIT));
				if (red.getUnit() != null) {
					if (red.getUnit().doubleValue() <= 0) {
						validation.addError("", "redemption.unit_must_greater_than_zero");
					}
				}
			}
		}
		// set fee pct if null
		if (red.getFeePct() == null) {
			if ((red.getNetAmount() != null) && (red.getFeeAmt() != null)) {
				// red.setFeePct((red.getFeeAmt().divide(red.getNetAmount())).multiply(new
				// BigDecimal(100)));
				Double feePct = new Double(0);
				if (red.getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
					feePct = new Double(0);
				} else {
					feePct = (red.getFeeAmt().doubleValue() / red.getNetAmount().doubleValue()) * 100;
				}

				BigDecimal feePct1 = new BigDecimal(feePct);
				// pake 4 digit dibelakang koma karena di screen untuk field
				// redemption fee percent hanya
				// mengijinkan 4 digit dibelakang koma
				// TODO: Change By Fadly 2017-10-30 Redmine #3326
				if (!FROM_CANCEL_TRADE.equals(from))
					red.setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
				// END Change Redmine #3326
			}
		}

		if (red != null && product != null) {

			if (LookupConstants.RG_REDEMPTION_TRANSACTION_BY_UNIT.equals(red.getTradeBy().getLookupId())) {
				validation.required("Unit is", red.getUnit());
				if (red.getUnit() != null) {
					if (red.getUnit().doubleValue() <= 0) {
						validation.addError("", "redemption.unit_must_greater_than_zero");
					}
				}
				// validation.required("Fee is", red.getFeePct());
				// validation.required("Discount is", red.getDiscPct());
				// validation.required("Tax Of Fee is", red.getTaxPct());
				// validation.required("Other Fee is", red.getOtherPct());
			}
			if (LookupConstants.RG_REDEMPTION_TRANSACTION_BY_AMOUNT.equals(red.getTradeBy().getLookupId())) {
				validation.required("Amount is", red.getNetAmount());
			}
			// if
			// (LookupConstants.RG_REDEMPTION_TRANSACTION_BY_AMMOUNT.equals(red.getTradeBy().getLookupId()))
			// {
			if (red.getFeePct() == null) {
				validation.required("Fee is", red.getFeeAmt());
			} else if (red.getFeeAmt() == null) {
				validation.required("Fee is", red.getFeePct());
			}

			red.setDiscAmt(BigDecimal.ZERO);
			if (red.getDiscPct() == null) {
				validation.required("Discount is", red.getDiscAmt());
			}
			// validation.required("Tax Of Fee is", red.getTaxAmt());
			if (red.getOtherPct() == null) {
				// validation.required("Other Fee is", red.getOtherAmt());
			}

			Date applicationDate = taService.getCurrentBusinessDate();

			if (fmtYYYYMMDD(red.getTradeDate()).compareTo(fmtYYYYMMDD(applicationDate)) > 0) {
				validation.addError("", "redemption.tradedate_greater_than_applicationDate");
			}
			// if
			// (fmtYYYYMMDD(red.getGoodfundDate()).compareTo(fmtYYYYMMDD(red.getTradeDate()))
			// < 0) {
			// validation.addError("global", "validation.goodfunddate");
			// }
			/*
			 * if (fmtYYYYMMDD(red.getNavDate()).compareTo(fmtYYYYMMDD(red.
			 * getGoodfundDate())) > 0) { validation.addError("",
			 * "redemption.navdate_greater_than_goodFundDate"); }
			 */
			if (fmtYYYYMMDD(red.getNavDate()).compareTo(fmtYYYYMMDD(red.getTradeDate())) > 0) {
				validation.addError("", "redemption.navdate_greater_than_transactionDate");
			}
			// if ((red.getPostDate() != null) && (red.getGoodfundDate() !=
			// null)) {
			// if
			// (fmtYYYYMMDD(red.getPostDate()).compareTo(fmtYYYYMMDD(red.getGoodfundDate()))
			// < 0) {
			// logger.debug("masuk sini!!!");
			// validation.addError("global",
			// "redemption.postdate_less_than_goodFundDate");
			// }
			// }
			if ((red.getPostDate() != null) && (red.getTradeDate() != null)) {
				if (fmtYYYYMMDD(red.getPostDate()).compareTo(fmtYYYYMMDD(red.getTradeDate())) < 0) {
					validation.addError("", "redemption.postdate_less_than_transactionDate");
				}
			}

			if (product != null && red.getTradeDate() != null && product.getMaxPaymentDate() != null) {
				Date maxPayDate = generalService.getWorkingDate(red.getTradeDate(), product.getMaxPaymentDate());
				if (fmtYYYYMMDD(red.getPaymentDate()).compareTo(fmtYYYYMMDD(maxPayDate)) > 0) {
					validation.addError("", "redemption.paymentdate_greater_than_maxpaydate");
				}
			}

			// check available unit < min bal unit and redemption type is
			// partial
			if (!red.getLiquidation()) {
				BigDecimal tmpUnit = BigDecimal.ZERO;
				if (red.getUnit() != null) {
					tmpUnit = red.getUnit();
				}
				BigDecimal tmpAvailableUnit = BigDecimal.ZERO;
				if (red.getAvailabelUnit() != null) {
					tmpAvailableUnit = red.getAvailabelUnit();
				}

				if (product.getMinBalUnit() != null) {
					if (tmpAvailableUnit.subtract(tmpUnit).compareTo(product.getMinBalUnit()) < 0) {
						validation.addError("", "redemption.available_unit_lower_than_min_bal_unit");
					}
				}

			}

			log.debug("red.getFeePct():" + red.getFeePct());
			log.debug("product.getRedMinFee():" + product.getRedMinFee());
			log.debug("product.getRedMaxFee():" + product.getRedMaxFee());
			if (product.getRedMinFee() != null && red.getFeePct() != null) {
				if (red.getFeePct().compareTo(product.getRedMinFee()) < 0) {
					validation.addError("Fee Percent is", "redemption.feepct_lower_than_minfee");
				}
			}

			if (product.getRedMaxFee() != null && red.getFeePct() != null) {
				if (red.getFeePct().compareTo(product.getRedMaxFee()) > 0) {
					validation.addError("Fee Percent is", "redemption.feepct_greater_than_minfee");
				}
			}

			// Tambahan validasi dimana post date tidak boleh <= dari fund
			// date(rg_prod_eod.eod_date).
			if (red.getPostDate() != null && red.getRgProduct() != null) {
				RgProdEod rgProdEod = taService.getProdEodByProductCode(red.getRgProduct().getProductCode());
				if (rgProdEod != null) {
					if (rgProdEod.getLastEod() != null) {
						if (red.getPostDate().compareTo(rgProdEod.getLastEod()) <= 0) {
							validation.addError("", "redemption.postdate_must_greater_than_eod");
						}
					}
				}
			}
			if (red.getPostDate() != null && red.getDefaultPostDate() != null) {
				if (red.getPostDate().compareTo(red.getDefaultPostDate()) > 0) {
					validation.addError("", "redemption.postdate_greater_than_default");
				}
			}
			// - Tambahan validasi dimana tidak boleh diedit > dari default
			// value posting date.

			if (from == null) {
				if (red.getPostDate() != null && red.getPaymentDate() != null) {
					if (red.getPaymentDate().compareTo(red.getPostDate()) < 0) {
						validation.addError("", "redemption.paymentdate_less_than_postdate");
					}
				}
			}

			// if
			// (fmtYYYYMMDD(red.getPostDate()).compareTo(fmtYYYYMMDD(APPLICATION_DATE))
			// < 0) {
			// validation.addError("global",
			// "validation.postdate_less_than_applicationDate");
			// }
			if (red.getNetAmount() != null) {
				//#3937 --start
				if (!red.getLiquidation()) {
				//#3937 --end
					if (red.getNetAmount().doubleValue() < product.getRedMinAmt().doubleValue()) {
						validation.addError("", "redemption.netAmount_less_than_redemptionMinimumAmount");
					}
					if (product.getRedMaxAmt() != null) {
						if (red.getNetAmount().doubleValue() > product.getRedMaxAmt().doubleValue()) {
							validation.addError("", "redemption.netAmount_greater_than_redemptionMaximumAmount");
						}
					}
				//#3937 --start
				}
				//#3937 --end
			}
			// if (portfolio != null) {
			// if (red.getNetAmount() != null) {
			// if
			// (LookupConstants.TRANSACTION_BY_AMOUNT.equals(red.getTradeBy().getLookupId()))
			// {
			// if (red.getNetAmount().doubleValue() >
			// portfolio.getBalanceAmount().doubleValue()) {
			// validation.addError("global",
			// "redemption.netAmount_greater_than_balanceAmount");
			// }
			// }
			// }
			// if
			// (LookupConstants.TRANSACTION_BY_UNIT.equals(red.getTradeBy().getLookupId()))
			// {
			double availableUnit = 0;
			double unit = 0;
			if (portfolio != null) {
				if (portfolio.getUnit() != null) {
					availableUnit = portfolio.getUnit().doubleValue();
				}
			}
			log.debug("[save] availableUnit = " + availableUnit);
			if (red.getUnit() != null) {
				unit = red.getUnit().doubleValue();
			}
			if (unit > availableUnit) {
				validation.addError("", "redemption.unit_greater_than_balanceUnit");
			}
			// }
			// }

			log.debug("[save] red.rgProduct.redlock = " + red.getRgProduct().getRedLock());
			if (red.getRgProduct().getRedLock() == true) {
				List<RgProductLockException> lockExceptionDates = taService.listRgProductLockExceptionByType(red.getRgProduct().getProductCode(), UIConstants.TRADE_TYPE_REDEMPTION);
				log.debug("[save] size lockExceptionDates = " + lockExceptionDates.size());
				boolean checkSame = true;
				for (RgProductLockException le : lockExceptionDates) {
					log.debug("[save] red.goodfundDate = " + red.getGoodfundDate() + "same with exceptionDate = " + le.getId().getExceptionDate());
					if (red.getGoodfundDate().getTime() == le.getId().getExceptionDate().getTime()) {
						checkSame = false;
						break;
					}
				}
				if (checkSame) {
					validation.addError("", "redemption.not_allowed_same_with_exceptionDate");
				}
			}

			/**
			 * Point 1: Validasi max fee amt dilihatdarirg_product.red_max_fee
			 * amt r [jika null, makanilai maximum]
			 */
			if (red.getRgProduct().getRedMaxAmt() != null) {
				BigDecimal redMaxAmt = red.getRgProduct().getRedMaxAmt();
				if (red.getFeeAmt().compareTo(redMaxAmt) > 0) {
					validation.addError("", "redemption.fee_amount_greater_max_fee");
				}
			}
			/**
			 * End of Point 1
			 */

			/**
			 * checking fee percent is must not greater than redMaxFee
			 * 
			 */
			if (red.getRgProduct().getRedMaxFee() != null && red.getFeePct() != null) {
				if (red.getFeePct().compareTo(red.getRgProduct().getRedMaxFee()) > 0) {
					validation.addError("", "redemption.fee_pct_greater_max_fee_pct");
				}
			}
			/**
			 * end of fee checking
			 * */
			//#4384  Validasi transaksi RG yang sama diinput/diupload berulang kali
			log.debug("type  " + red.getType() + " externalRefs " + red.getExternalReference() + " tradeDate " + red.getTradeDate());
			if (red.getExternalReference() != null) {
				Boolean isTradeExist = taService.isRgTradeAlreadyExist(red.getTradeDate(), AbstractRgTrade.TRADETYPE_REDEEM, red.getExternalReference(), red.getTradeKey());
				log.debug("isTradeExist >>>> "  + isTradeExist);
				if (isTradeExist) validation.addError("", "trade.data.duplicate");
			}
		}

		if (from != null && from.equals(FROM_CANCEL_TRADE)) {
			validation.clear();
			red.setType(rg.getType());
			red.setCancelTradeDate(rg.getCancelTradeDate());
			red.setCancelPostDate(rg.getCancelPostDate());
			red.setCancelRemark(rg.getCancelRemark());
		}

		if (validation.hasErrors()) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
			render("RegistryRedemption/entry.html", red, mode, feeby);
		} else {
			Long id = red.getTradeKey();
			if (id == null) {
				id = 0l;
			}
			red.setTrnSABranchKey(red.getTrnSABranch().getThirdPartyKey());
			serializerService.serialize(session.getId(), id, red);
			confirming(id, mode, feeby, from);
		}
	}

	public static void confirming(Long id, String mode, String feeby, String from) {
		log.debug("confirming. id: " + id + " mode: " + mode + " feeby: " + feeby + " from: " + from);

		renderArgs.put("confirming", true);
		if (id == null) {
			id = (long) 0;
		}
		RgTrade red = serializerService.deserialize(session.getId(), id, RgTrade.class);

		if (feeby != null && feeby.equals("feeby1"))
			red.setFeePercent(true);
		if (feeby != null && feeby.equals("feeby2"))
			red.setFeePercent(false);
		if (red.getLiquidation()) {
			GnLookup lookupUnit = generalService.getLookup(LookupConstants.TRANSACTION_BY_UNIT);
			red.setTradeBy(lookupUnit);
		}
		if (from != null && from.equals(FROM_CANCEL_TRADE)) {
			RgTrade rg = new RgTrade();
			rg.setTradeKey(red.getTradeKey());
			rg.setType(red.getType());
			rg.setCancelTradeDate(red.getCancelTradeDate());
			rg.setCancelPostDate(red.getCancelPostDate());
			rg.setCancelRemark(red.getCancelRemark());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
			render("RegistryRedemption/cancel.html", red, mode, from, rg);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
			render("RegistryRedemption/entry.html", red, mode, feeby);
		}
	}

	private static Map<Long, Long> keyMap = new HashMap<Long, Long>();

	public static void confirm(RgTrade red, String mode, RgTrade rg, String from) {
		log.debug("confirm. red: " + red + " mode: " + mode + " rg: " + rg + " from: " + from);

		if (red == null)
			back(null, mode, from);
		if (from != null && from.equals(FROM_CANCEL_TRADE)) {
			//Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			try {
				log.debug("--------> CONFIRM CANCEL REDEMP TRADE DATE = " + rg.getCancelTradeDate());
				log.debug("--------> CONFIRM CANCEL REDEMP POST DATE = " + rg.getCancelPostDate());

				// TODO: Add By Fadly 2017-10-25 RedMine #3330
				if (keyMap.get(rg.getTradeKey()) != null) {
					throw new MedallionException(Messages.get("subscription.cancelTrade_can_not_cancel_trade_more_than_one"));
				} else {
					keyMap.put(rg.getTradeKey(), rg.getTradeKey());
				}

				RgTrade cancelTrade = taService.checkCancelTrade(rg.getTradeKey());
				if (cancelTrade == null)
					throw new MedallionException(Messages.get("subscription.cancelTrade_can_not_cancel_trade_more_than_one"));
				// END RedMine #3330

				taService.processCancelTradeSubRedeem(rg, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
				Map<String, Object> result = new HashMap<String, Object>();
				log.debug("[CONFIRM] TRADE ID => " + rg.getTradeKey());
				result.put("status", "success");
				result.put("message", Messages.get("trade.cancelled.successful", rg.getTradeKey()));
				renderJSON(result);
			} catch (MedallionException ex) {
				// TODO: Change By Fadly 2017-10-25 RedMine #3330
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "errorx");
				result.put("errorx", Messages.get(ex.getMessage()));
				renderJSON(result);
			} finally {
				keyMap.remove(rg.getTradeKey());
				// END RedMine #3330
			}
		} else {
			try {
				RgPortfolio portfolio = null;
				if (red.getRgInvestmentaccount() != null && red.getPostDate() != null) {
					portfolio = taService.loadLastPortfolio(red.getRgInvestmentaccount().getAccountNumber(), red.getPostDate());
					// portfolio =
					// taService.loadPortfolio(red.getRgInvestmentaccount().getAccountNumber(),
					// fmtYYYYMMDD(red.getPostDate()));
					BigDecimal outstandingUnit = taService.loadOutstanding(red.getRgProduct().getProductCode(), red.getRgInvestmentaccount().getAccountNumber(), red.getPostDate());

					RgNav nav = null;
					if (red.getRgProduct() != null && !red.getRgProduct().getProductCode().isEmpty()) {
						nav = taService.loadLatestNav(red.getRgProduct().getProductCode());
					}

					if (portfolio != null)
						portfolio.setUnit(portfolio.getUnit().subtract(outstandingUnit));

					if (portfolio != null)
						red.setAvailabelUnit(portfolio.getUnit());
					if (portfolio != null && nav != null)
						red.setAvailabelBalance(red.getAvailabelUnit().multiply(nav.getNav()));
					if (red.getLiquidation()) {
						if (portfolio != null)
							red.setUnit(portfolio.getUnit());
						red.setTradeBy(generalService.getLookup(LookupConstants.TRANSACTION_BY_UNIT));
					}
				}

				// if (red.getLiquidation()) {
				// // RgPortfolio portfolio =
				// taService.loadPortfolio(red.getRgInvestmentaccount().getAccountNumber(),
				// fmtYYYYMMDD(red.getTradeDate()));
				// RgPortfolio portfolio =
				// taService.loadLastPortfolio(red.getRgInvestmentaccount().getAccountNumber(),
				// red.getGoodfundDate());
				// RgNav nav =
				// taService.loadLatestNav(red.getRgProduct().getProductCode());
				// red.setTradeBy(generalService.getLookup(LookupConstants.TRANSACTION_BY_UNIT));
				// if (portfolio != null)
				// red.setAvailabelUnit(portfolio.getUnit());
				// if (portfolio != null && nav != null)
				// red.setAvailabelBalance(red.getAvailabelUnit().multiply(nav.getNav()));
				// }

				red.setTmpPrice(red.getPrice());
				red.setTmpAmount(red.getAmount());
				red.setTmpNetAmount(red.getNetAmount());
				red.setTmpUnit(red.getUnit());

				red.setType(RgTrade.TRADETYPE_REDEEM);
				red.setTypeOrder(RgTrade.TRADETYPEORDER_REDEEM);
				red.setTradeStatus(LookupConstants.__RECORD_STATUS_OPEN);

				// if (red.getFeeAmt() == null) red.setFeeAmt(new
				// BigDecimal(0));
				// if (red.getDiscAmt() == null) red.setDiscAmt(new
				// BigDecimal(0));
				// if (red.getTaxAmt() == null) red.setTaxAmt(new
				// BigDecimal(0));
				// if (red.getOtherAmt() == null) red.setOtherAmt(new
				// BigDecimal(0));

				// String username = session.get(UIConstants.SESSION_USERNAME);
				// createTrade
				/**
				 * Nilai price dan unit or amount(fee,net,gross,payment) Jangan
				 * disimpan di rg_trade. Tampilan only . Tujuannya supaya nanti
				 * pas revisi nav, transaction validation bisa proses ulang
				 * menggunakan hasil revisi
				 */

				red.setTaxAmt(null);
				red.setTotFeeAmt(null);
				red.setPaymentAmount(null);
				red.setPrice(null);

				// menandakan discount by amount, dan kalau discount by amount
				// maka disc. pct tidak perlu disave
				red.setDiscAmt(BigDecimal.ZERO);
				if (red.getDiscAmt() == null)
					red.setDiscPct(BigDecimal.ZERO);

				if (LookupConstants.RG_REDEMPTION_TRANSACTION_BY_UNIT.equals(red.getTradeBy().getLookupId())) {
					red.setNetAmount(null);
					red.setPaymentAmount(null);
					red.setAmount(null);
					if (red.isFeePercent())
						red.setFeeAmt(null);
					// red.setOtherAmt(null);
				}
				if (LookupConstants.RG_REDEMPTION_TRANSACTION_BY_AMOUNT.equals(red.getTradeBy().getLookupId())) {
					red.setUnit(null);
				}

				// jika fee by amount maka fee percent tidak perlu save ke
				// database
				if (!red.isFeePercent())
					red.setFeePct(null);

				//#4384 Validasi transaksi RG yang sama diinput/diupload berulang kali
				log.debug("type  " + red.getType() + " externalRefs " + red.getExternalReference() + " tradeDate " + red.getTradeDate());
				if (red.getExternalReference() != null) {
					Boolean isTradeExist = taService.isRgTradeAlreadyExist(red.getTradeDate(), AbstractRgTrade.TRADETYPE_REDEEM, red.getExternalReference(), red.getTradeKey());
					log.debug("isTradeExist >>>> "  + isTradeExist);
					if (isTradeExist) throw new MedallionException("trade.data.duplicate", "");
				}
				
				RgTrade redTrade = taService.createTrade(MenuConstants.RG_REDEMPTION, red, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
				boolean confirming = true;
				if (!(redTrade.equals(null))) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", Messages.get("trade.confirmed.successful", redTrade.getTradeKey()));
					renderJSON(result);
					// renderText (trx.getTransactionNumber());
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
					render("RegistryRedemption/entry.html", redTrade, mode, confirming);
				}
				// entry();
				// render("RegistryRedemption/detail.html", red, mode);
			} catch (MedallionException ex) {
				renderJSON(Formatter.resultError(ex));
				/*flash.error(ex.getErrorCode(), ex.getParams());
				renderArgs.put("confirming", true);
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
				render("RegistryRedemption/entry.html", red, mode);*/
			} catch (Exception ex) {
				List<String> params = new ArrayList<String>();
				flash.error(ex.getMessage(), params.toArray());
				renderArgs.put("confirming", true);
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
				render("RegistryRedemption/entry.html", red, mode);
			}
		}
	}

	public static void back(Long id, String mode, String from) {
		log.debug("back. id: " + id + " mode: " + mode + " from: " + from);

		renderArgs.put("confirming", false);
		if (id == null) {
			id = (long) 0;
		}
		RgTrade red = serializerService.deserialize(session.getId(), id, RgTrade.class);
		if (from != null && from.equals(FROM_CANCEL_TRADE)) {
			RgTrade rg = new RgTrade();
			rg.setTradeKey(red.getTradeKey());
			rg.setType(red.getType());
			rg.setCancelTradeDate(red.getCancelTradeDate());
			rg.setCancelPostDate(red.getCancelPostDate());
			rg.setCancelRemark(red.getCancelRemark());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
			render("RegistryRedemption/cancel.html", red, mode, from, rg);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
			render("RegistryRedemption/entry.html", red, mode);
		}

	}

	public static void approval(String taskId, Long keyId, RgTrade red, String from, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " red: " + red + " from: " + from + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			red = json.readValue(maintenanceLog.getNewData(), RgTrade.class);
		} catch (Exception ex) {
			log.error("error getting from maintenance log", ex);
		}

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		Boolean isApproval = true;
		if (red != null) {
			red.setRgProduct(red.getRgInvestmentaccount().getRgProduct());
			red.setTaxPct(red.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
			red.setTaxAmt(getTaxAmt(red));
			red.setTrnSABranch(generalService.getThirdParty(red.getTrnSABranchKey()));
			red.setPrice(red.getTmpPrice());
			red.setNetAmount(red.getTmpNetAmount());
			if (red.getIsUpload() == null || !red.getIsUpload()) {
				red.setAmount(red.getTmpAmount());
				red.setUnit(red.getTmpUnit());
			}
		}

		// TODO: Change by Fadly 2017-10-30 Redmine #3326
		if (red.getFeePct() != null)
			red.setFeePercent(true);
		// END Change Redmine #3326

		RgTrade rgTrade = taService.loadTrade(keyId);
		RgPortfolio portfolio = taService.loadLastPortfolio(rgTrade.getRgInvestmentaccount().getAccountNumber(), rgTrade.getPostDate());
		BigDecimal outstandingUnit = taService.loadOutstandingOnlyApprove(rgTrade.getRgProduct().getProductCode(), rgTrade.getRgInvestmentaccount().getAccountNumber(), rgTrade.getPostDate());
		if (portfolio != null) {
			portfolio.setUnit(portfolio.getUnit().subtract(outstandingUnit));
			red.setAvailabelUnit(portfolio.getUnit());
		}

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		render("RegistryRedemption/approval.html", red, mode, taskId, from, isApproval, maintenanceLogKey);
	}

	@Check("transaction.inquiryUnitRegistry")
	public static void cancel(Long keyId, boolean liquidation, String from, RgTrade rg) {
		log.debug("cancel. keyId: " + keyId + " liquidation: " + liquidation + " from: " + from + " rg: " + rg);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgTrade red = taService.loadTrade(keyId);
		RgTrade canRed = taService.loadTradeByCancelTrade(red.getTradeKey());
		red.setRgProduct(red.getRgInvestmentaccount().getRgProduct());
		red.setTaxPct(red.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		red.setTaxAmt(getTaxAmt(red));
		red.setTrnSABranch(generalService.getThirdParty(red.getTrnSABranchKey()));
		BigDecimal outstandingUnit = taService.loadOutstanding(red.getRgProduct().getProductCode(), red.getRgInvestmentaccount().getAccountNumber(), red.getPostDate());
		log.debug("UNIT = " + outstandingUnit);
		red.setAvailabelUnit(outstandingUnit);
		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);

		// TODO: Add By Fadly 2017-10-30 Redmine #3326
		if (red.getFeePct() != null)
			red.setFeePercent(true);
		// END Redmine #3326

		if (red.isCancelled()) {
			red.setTradeStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_CANCELED));
		} else {
			if (red.isPosted() && red.isPaid() && red.getTradeStatus().equals(LookupConstants.__RECORD_STATUS_VALID))
				red.setTradeStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_PAID));
			else if (red.isPosted() && !red.isPaid() && red.getTradeStatus().equals(LookupConstants.__RECORD_STATUS_VALID))
				red.setTradeStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_POSTED));
			else
				red.setTradeStatus(getDecodeDataStatus(red.getTradeStatus()));
		}

		if (from.equals("cancelTradeApp")) {
			rg = new RgTrade();
			rg.setTradeKey(keyId);
			rg.setCancelTradeDate(canRed.getTradeDate());
			rg.setCancelPostDate(canRed.getPostDate());
			rg.setCancelRemark(canRed.getRemark());
			rg.setCancelLiquidation(liquidation);
		}

		if (from.equals(FROM_CANCEL_TRADE) || from.equals(FROM_UNIT_REGISTRY)) {
			rg = new RgTrade();
			rg.setTradeKey(keyId);
			// rg.setCancelTradeDate(currentBusinessDate);
			// rg.setCancelPostDate(currentBusinessDate);
			rg.setCancelTradeDate(red.getTradeDate());
			rg.setCancelPostDate(red.getPostDate());
			rg.setType(AbstractRgTrade.TRADETYPE_REDEEM);
			rg.setCancelLiquidation(liquidation);
		}

		List<String> tradesNo = new ArrayList<String>();

		Long dataPaymentOnApproval = null;
		Long dataPaymentAlreadyPaid = null;
		if ((red.getRgPaymentDetail() != null) || (red.getRgPayment() != null)) {
			Long dataPayment = taService.chekcPaymentOnApprovalList(red.getTradeKey());
			Long dataPaymentPaid = taService.chekcPaymentAlreadyPaid(red.getTradeKey());
			if (dataPayment == 0) {
				dataPaymentOnApproval = dataPayment;
			}
			if (dataPaymentPaid == 0) {
				dataPaymentAlreadyPaid = dataPaymentPaid;
			}
		}

		boolean warningMsg = false;
		if (rg.getLiquidation() == true) {
			// warningMsg = true;
			List<RgTrade> tradeByLiqs = taService.listTradeByRedeemRef(rg.getTradeKey());
			if (tradeByLiqs.size() > 0) {
				warningMsg = true;
				for (RgTrade rgTrade : tradeByLiqs) {
					tradesNo.add(Long.toString(rgTrade.getTradeKey()));
				}
			}
		}

		if (from.equals("unitRegistry"))
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_INQUIRY_REGISTRY));
		else
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
		render("RegistryRedemption/cancel.html", red, mode, from, rg, warningMsg, tradesNo, dataPaymentAlreadyPaid, dataPaymentOnApproval, currentBusinessDate);
	}

	public static void approve(String taskId, Long keyId, RgTrade red, Long maintenanceLogKey) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId + " red: " + red + " maintenanceLogKey: " + maintenanceLogKey);

		//boolean isMessageValidation = false;
		try {
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			try {
				red = json.readValue(maintenanceLog.getNewData(), RgTrade.class);
			} catch (JsonParseException e) {
				log.error(e.getMessage(), e);
				throw new MedallionException(ExceptionConstants.INVALID_OPERATION);
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
				throw new MedallionException(ExceptionConstants.INVALID_OPERATION);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new MedallionException(ExceptionConstants.INVALID_OPERATION);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new MedallionException(ExceptionConstants.INVALID_OPERATION);
			}

			RgTrade rgTrade = taService.loadTrade(keyId);
			Map<String, BigDecimal> holdingMap = new HashMap<String, BigDecimal>();
			boolean isPriceNotNull = false;
			RgProduct rgProduct = rgTrade.getRgProduct();

			// price checking to know whether to process transaction validation
			// or not
			if (rgProduct != null) {
				if (rgProduct.isFixnav()) {
					if (rgProduct.getFixNavPrice() != null) {
						isPriceNotNull = true;
					}
				} else {
					RgNav nav = taService.loadActiveNav(rgProduct.getProductCode(), rgTrade.getNavDate());
					if (nav != null) {
						if (nav.getNav() != null) {
							isPriceNotNull = true;
						}
					}
				}
			}

			RgPortfolio portfolio = taService.loadLastPortfolio(rgTrade.getRgInvestmentaccount().getAccountNumber(), rgTrade.getPostDate());
			BigDecimal outstandingUnit = taService.loadOutstandingOnlyApprove(rgTrade.getRgProduct().getProductCode(), rgTrade.getRgInvestmentaccount().getAccountNumber(), rgTrade.getPostDate());
			log.debug("outstanding unit " + outstandingUnit);
			if (portfolio != null) {
				portfolio.setUnit(portfolio.getUnit().subtract(outstandingUnit));
				log.debug("available unit: " + portfolio.getUnit() + ", temp Unit: " + red.getTmpUnit() + ", unit: " + red.getUnit() + ", key: " + red.getTradeKey());
				// TODO: Change by Fadly 2017-10-30 Redmine #3326
				if (portfolio.getUnit().compareTo(red.getTmpUnit() == null ? new BigDecimal(0) : red.getTmpUnit()) < 0) {
					// END Redmine #3326
					throw new MedallionException(Messages.get("switching.unit_greater_than_availableUnit"));
				}
			}

			RgTrade trade = null;

			if (portfolio != null) {
				trade = taService.approveTradeRedeem(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);
			}

			// go with trans validation
			if (trade != null) {
				if (trade.getRecordStatus() != null && trade.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED)) {
					if (isPriceNotNull) {
						// trade.setAmount(red.getAmount());
						// trade.setPrice(red.getPrice());
						// trade.setUnit(red.getUnit());

						// harusnya ambil dari tabel langsung jangan dari screen
						// lagi
						trade.setAmount(rgTrade.getAmount());
						trade.setPrice(rgTrade.getPrice());
						trade.setUnit(rgTrade.getUnit());

						rgTrade = taService.processTransactionValidation(trade, holdingMap);
						if (!rgTrade.getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID)) {
							// throw new
							// MedallionException("transaction.validation");
							//isMessageValidation = true;
							throw new MedallionException(rgTrade.getMessage());
						}
					}
				}
			} else {
				//isMessageValidation = true;
				throw new MedallionException("Holding Not Found");
			}

			renderJSON(Formatter.resultSuccess(Messages.get("trade.approved", trade.getTradeKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId, Integer nr, String remarksCorrection) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId + " nr: " + nr + " remarksCorrection: " + remarksCorrection);

		try {
			RgTrade trade = taService.rejectTrade(keyId, session.get(UIConstants.SESSION_USERNAME), taskId, nr, remarksCorrection, null);

			renderJSON(Formatter.resultSuccess(Messages.get("trade.rejected", trade.getTradeKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("maintenance.trade.list")
	public static void tradePaging(Paging page, TradeSearchParameters param) {
		log.debug("tradePaging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			log.debug("param.transactionDateFrom:" + param.transactionDateFrom);
			log.debug("param.transactionDateTo:" + param.transactionDateTo);
			log.debug("param.transactionNoOperator:" + param.transactionNoOperator);
			log.debug("param.transactionNo:" + param.transactionNo);
			validation.required("Transaction Date is", param.transactionDateFrom);
			validation.required("Transaction Date is", param.transactionDateTo);

			page.addParams("p.recordStatus", Paging.EQUAL, LookupConstants.__RECORD_STATUS_NEED_REVISION);
			page.addParams("p.tradeStatus", Paging.EQUAL, LookupConstants.__RECORD_STATUS_REJECTED);
			page.addParams("p.tradeDate", Paging.GREATEQUAL, param.transactionDateFrom);
			page.addParams("p.tradeDate", Paging.LESSEQUAL, param.transactionDateTo);
			page.addParams("p.type", Paging.EQUAL, AbstractRgTrade.TRADETYPE_REDEEM);
			if (param.transactionNo != null) {
				page.addParams("p.tradeKey", param.transactionNoOperator, UIHelper.withOperator(param.transactionNo.toString(), param.transactionNoOperator));
			}
			page.addParams(Helper.searchAll("(p.tradeKey||p.rgInvestmentaccount.accountNumber||" + "to_char(p.tradeDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "p.rgProduct.productCode||p.bankAccount.bankCode.thirdPartyName)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			if (param.investorNo != null) {
				// page.addParams( "p.rgInvestmentaccount.accountNumber",
				// Paging.EQUAL, param.investorNo );
				page.addParams("rg.customer.customerNo", Paging.EQUAL, param.investorNo);
			}
			if (param.fundCode != null) {
				page.addParams("p.rgProduct.productCode", Paging.EQUAL, param.fundCode);
			}
			page = taService.pagingTrade(page);
		}

		renderJSON(page);
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		RgTrade red = taService.loadTrade(id);
		red.setRgProduct(red.getRgInvestmentaccount().getRgProduct());
		red.setTaxPct(red.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		red.setTaxAmt(getTaxAmt(red));
		red.setTrnSABranch(generalService.getThirdParty(red.getTrnSABranchKey()));
		red.setDefaultPostDate(red.getPostDate());
		render("RegistryRedemption/entry.html", red, mode);

	}

	private static String getDecodeDataStatus(String statusCode) {
		log.debug("getDecodeDataStatus. statusCode: " + statusCode);

		return StatusExtension.decodeDataStatus(statusCode);
	}

}
