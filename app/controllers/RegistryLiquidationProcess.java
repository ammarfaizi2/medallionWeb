package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.LiquidationProcessSearchParameter;
import vo.TradeSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.FormatRoundHelper;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgInvestmentaccount;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgPortfolio;
import com.simian.medallion.model.RgProdEod;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.RgLiquidationProcess;

@With(Secure.class)
public class RegistryLiquidationProcess extends Registry {
	private static Logger log = Logger.getLogger(RegistryLiquidationProcess.class);

	private static final BigDecimal BD_100 = new BigDecimal(100);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		// List<SelectItem> operators = UIHelper.yesNoOperators();
		// renderArgs.put("operators", operators);
	}

	@Check("transaction.registryLiquidationProcess")
	public static void list(String mode) {
		log.debug("list. mode: " + mode);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_MATURITY_PROCESS));
		LiquidationProcessSearchParameter params = new LiquidationProcessSearchParameter();
		render(mode, params);
	}

	@Check("transaction.registryLiquidationProcess")
	public static void search() {
		log.debug("search. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_MATURITY_PROCESS));
		render("RegistryLiquidationProcess/search.html");
	}

	@Check("transaction.registryLiquidationProcess")
	public static void entry(boolean isNewRec) {
		log.debug("entry. isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		RgProduct product = new RgProduct();
		RgLiquidationProcess liq = new RgLiquidationProcess();
		liq.setRgTradeTemp(Helper.getZipPassword());
		liq.setTransactionDate(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID));
		isNewRec = true;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_MATURITY_PROCESS));
		render("RegistryLiquidationProcess/detail.html", product, mode, isNewRec, liq);

	}

	@Check("transaction.registryLiquidationProcess")
	public static void edit(String id, boolean isNewRec) {
		log.debug("edit. id: " + id + " isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		isNewRec = false;
		RgProduct product = taService.loadProduct(id);
		RgLiquidationProcess liq = new RgLiquidationProcess();
		liq.setRgTradeTemp(Helper.getZipPassword());

		if (product.getLiquidDate() != null) {
			if (product.getRedNavUsed() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(product.getLiquidDate());
				cal.add(Calendar.DATE, product.getRedNavUsed());
				liq.setNavDate(cal.getTime());
			}

			if (product.getRedPostPeriod() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(product.getLiquidDate());
				cal.add(Calendar.DATE, product.getRedPostPeriod());
				liq.setPostDate(cal.getTime());
			}

			if (product.getRedPayPeriod() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(product.getLiquidDate());
				cal.add(Calendar.DATE, product.getRedPayPeriod());
				liq.setPaymentDate(cal.getTime());
			}
		}
		Random rnd = new Random();
		int batchNo = Math.abs(rnd.nextInt(1000000000));
		// while(batchNo <= 0){
		// batchNo = rnd.nextInt(1000000000);
		// }

		RgNav rgNav = taService.loadActiveNav(product.getProductCode(), liq.getNavDate());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		if (product.isFixnav()) {
			liq.setPrice(product.getFixNavPrice());
		} else {
			if (rgNav != null) {
				liq.setPrice(rgNav.getNav());
			} else {
				liq.setPrice(null);
			}
		}
		log.debug("batchNo :::" + batchNo);
		liq.setBatchNo(batchNo);
		liq.setGoodFundDate(product.getLiquidDate());
		liq.setRgProduct(product);
		liq.setTransactionDate(product.getLiquidDate());

		if (liq.getPostDate() != null) {
			BigDecimal totalUnit = taService.loadPortfolioProduct(product.getProductCode(), sdf.format(liq.getPostDate()));
			liq.setTotalUnit(totalUnit);
			log.debug("totalUnit:" + totalUnit);
		}
		String taxKey = generalService.getValueParam(SystemParamConstants.PARAM_DEFAULT_MATURITY_GAIN_TAX);
		liq.setCapGainTax(generalService.getTaxMaster(Long.parseLong(taxKey)));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_MATURITY_PROCESS));
		render("RegistryLiquidationProcess/detail.html", liq, mode, isNewRec);
	}

	public static void view(String productCode, String navDate) {
		log.debug("view. productCode: " + productCode + " navDate: " + navDate);
	}

	/*
	 * public static void calculate(String productCode, RgLiquidationProcess
	 * liq){ List<RgInvestmentaccount> investAccs =
	 * taService.listInvestmentByProduct(liq.getRgProduct().getProductCode());
	 * dataInvest(liq, investAccs); Set<RgInvestmentaccount> investmentaccounts
	 * = new HashSet<RgInvestmentaccount>(investAccs); BigDecimal totalUnit =
	 * BigDecimal.ZERO; BigDecimal totalAmount = BigDecimal.ZERO; RgProduct
	 * product = taService.loadProduct(liq.getRgProduct().getProductCode());
	 * RgNav rgNav = taService.loadActiveNav(product.getProductCode(),
	 * liq.getNavDate()); for( RgInvestmentaccount ia : investmentaccounts ){
	 * 
	 * if(product.isFixnav()){ ia.setPrice(product.getFixNavPrice()); } else {
	 * if( rgNav != null ){ ia.setPrice(rgNav.getNav()); }else {
	 * ia.setPrice(null); } }
	 * 
	 * if( ia.getUnit() != null ){ totalUnit = totalUnit.add(ia.getUnit()); }
	 * if( ia.getBalanceAmount() != null ){ totalAmount =
	 * totalAmount.add(ia.getBalanceAmount()); }
	 * liq.getInvestmentaccounts().add(ia); } liq.setTotalUnitDetail(totalUnit);
	 * liq.setTotalAmount(totalAmount); flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.RG_MATURITY_PROCESS));
	 * render("RegistryLiquidationProcess/tab_detail.html", liq); }
	 */

	public static void calculate(String productCode, RgLiquidationProcess liq) {
		log.debug("calculate. productCode: " + productCode + " liq: " + liq);

		// List<RgInvestmentaccount> portfolio =
		// taService.loadPortfolioByProduct(
		// liq.getRgProduct().getProductCode(), fmtYYYYMMDD(liq.getPostDate()));
		//
		// BigDecimal totalUnit = BigDecimal.ZERO;
		// BigDecimal totalAmount = BigDecimal.ZERO;
		// RgProduct product =
		// taService.loadProduct(liq.getRgProduct().getProductCode());
		// RgNav rgNav = taService.loadActiveNav(product.getProductCode(),
		// liq.getNavDate());
		//
		// Date holdingDate = null;
		// try {
		// holdingDate = parseYYYYMMDD(fmtYYYYMMDD(liq.getPostDate()));
		// } catch (ParseException e) {
		// logger.debug("error holdingDate calculate registryLiquidation "+e.getMessage());
		// }
		//
		// for (RgInvestmentaccount folio : portfolio) {
		// RgInvestmentaccount acc = new RgInvestmentaccount();
		// acc.setAccountNumber(folio.getAccountNumber());
		// acc.setName(folio.getName());
		//
		// BigDecimal outstandingUnit =
		// taService.loadOutstanding(liq.getRgProduct().getProductCode(),
		// folio.getAccountNumber(), holdingDate);
		// acc.setUnit(folio.getUnit());
		//
		// if(acc.getUnit()==null){
		// acc.setUnit(BigDecimal.ZERO);
		// }
		//
		// acc.setUnit(acc.getUnit().subtract(outstandingUnit));
		// // acc.setUnit(folio.getUnit().subtract(product.getMinBalUnit()));
		// if(product.isFixnav()){
		// acc.setPrice(product.getFixNavPrice());
		// } else {
		// if( rgNav != null ){
		// acc.setPrice(rgNav.getNav());
		// }else {
		// acc.setPrice(null);
		// }
		// }
		// if(acc.getUnit() != null){
		// acc.setUnitRound(Helper.format(acc.getUnit(),
		// product.getUnitRoundValue().intValue(),
		// FormatRoundHelper.getRoundMode(product.getUnitRoundType())));
		// }
		// if(acc.getPrice() != null){
		// acc.setPriceRound(Helper.format(acc.getPrice(),
		// product.getPriceRoundValue().intValue(),
		// FormatRoundHelper.getRoundMode(product.getPriceRoundType())));
		// }
		// if(acc.getPrice() == null || acc.getUnit() == null){
		// acc.setBalanceAmount(BigDecimal.ZERO);
		// acc.setBalanceAmountRound(Helper.format(acc.getBalanceAmount(),
		// product.getAmountRoundValue().intValue(),
		// FormatRoundHelper.getRoundMode(product.getAmountRoundType())));
		// } else {
		//
		// acc.setBalanceAmount(acc.getPrice().multiply(acc.getUnit()));
		// acc.setBalanceAmountRound(acc.getPriceRound().multiply(acc.getUnitRound()));
		// acc.setBalanceAmountRound(Helper.format(acc.getBalanceAmountRound(),
		// product.getAmountRoundValue().intValue(),
		// FormatRoundHelper.getRoundMode(product.getAmountRoundType())));
		// totalAmount = totalAmount.add(acc.getBalanceAmountRound());
		//
		// }
		// if(acc.getUnit() != null){
		// totalUnit = totalUnit.add(acc.getUnit());
		// }
		// liq.getInvestmentaccounts().add(acc);
		// }
		//
		// liq.setTotalUnitDetail(totalUnit);
		// liq.setTotalAmount(totalAmount);

		String uniqeId = liq.getRgTradeTemp();
		liq = taService.populteAllUnit(uniqeId, liq, true, null);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_MATURITY_PROCESS));
		render("RegistryLiquidationProcess/tab_detail.html", liq);
	}

	public static void resetPaging(TradeSearchParameters param) {
		log.debug("resetPaging. param: " + param);

		taService.deletePagingLiquidation(param.uniqeId);
		Map<String, String> successMap = new HashMap<String, String>();
		renderJSON(successMap);
	}

	public static void pagingLiquidation(Paging page, TradeSearchParameters param) {
		log.debug("pagingLiquidation. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("trade_type", page.EQUAL, "LIQ");
			page.addParams("unique_id", page.EQUAL, param.uniqeId);
			page.addParams(Helper.searchAll("(index_order||account_no||account_name||unit||price||amount)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
			page = taService.pagingLiquidation(page);
		}
		renderJSON(page);
	}

	private static List<RgInvestmentaccount> dataInvest(RgLiquidationProcess liq, List<RgInvestmentaccount> investAccts) {
		log.debug("dataInvest. liq: " + liq + " investAccts: " + investAccts);

		RgPortfolio portfolio = new RgPortfolio();
		RgNav nav = new RgNav();

		for (RgInvestmentaccount rgInvestmentaccount : investAccts) {

			// SET UNIT
			log.debug("taService.loadPortfolioForLiquidation(" + rgInvestmentaccount.getAccountNumber() + ", " + liq.getGoodFundDate() + ")");
			portfolio = taService.loadPortfolioForLiquidation(rgInvestmentaccount.getAccountNumber(), liq.getGoodFundDate());
			// BigDecimal sumUnit =
			// taService.getSumUnitRgTrade(rgInvestmentaccount.getAccountNumber());
			log.debug("taService.loadOutstanding()");
			BigDecimal sumUnit = taService.loadOutstanding(liq.getRgProduct().getProductCode(), rgInvestmentaccount.getAccountNumber(), liq.getGoodFundDate());
			if (sumUnit == null)
				sumUnit = BigDecimal.ZERO;
			if (portfolio != null)
				rgInvestmentaccount.setUnit(portfolio.getUnit().subtract(sumUnit));

			// SET PRICE
			log.debug("taService.loadActiveNavByProduct()");
			nav = taService.loadActiveNavByProduct(rgInvestmentaccount.getRgProduct().getProductCode());
			rgInvestmentaccount.setPrice(nav.getNav());

			// COUNT NET AMOUNT
			if (rgInvestmentaccount.getUnit() != null)
				rgInvestmentaccount.setNetAmount(rgInvestmentaccount.getUnit().multiply(rgInvestmentaccount.getPrice()));

			// COUNT FEE
			if (rgInvestmentaccount.getNetAmount() != null) {
				if (liq.getFeePct() != null)
					rgInvestmentaccount.setFee(rgInvestmentaccount.getNetAmount().multiply(liq.getFeePct().divide(BD_100)));
			}
			if (liq.getFeeAmt() != null)
				rgInvestmentaccount.setFee(liq.getFeeAmt());

			// COUNT DISCOUNT
			// if (liq.getDiscountPct() != null)
			// rgInvestmentaccount.setDiscount(rgInvestmentaccount.getNetAmount().multiply(liq.getDiscountPct().divide(BD_100)).multiply(new
			// BigDecimal(-1)));
			if (rgInvestmentaccount.getFee() != null) {
				if (liq.getDiscountPct() != null)
					rgInvestmentaccount.setDiscount(rgInvestmentaccount.getFee().multiply(liq.getDiscountPct().divide(BD_100)).multiply(new BigDecimal(-1)));
			}
			if (liq.getDiscountAmt() != null)
				rgInvestmentaccount.setDiscount(liq.getDiscountAmt().multiply(new BigDecimal(-1)));

			// COUNT OTHER
			if (rgInvestmentaccount.getNetAmount() != null) {
				if (liq.getOtherPct() != null)
					rgInvestmentaccount.setOther(rgInvestmentaccount.getNetAmount().multiply(liq.getOtherPct().divide(BD_100)));
			}
			if (liq.getOtherAmt() != null)
				rgInvestmentaccount.setOther(liq.getOtherAmt());

			// COUNT AMOUNT
			// rgInvestmentaccount.setBalanceAmount(rgInvestmentaccount.getNetAmount().subtract(liq.getFeeAmt()).subtract(liq.getDiscountAmt()).subtract(liq.getOtherAmt()));
			if ((rgInvestmentaccount.getNetAmount() != null) && (rgInvestmentaccount.getFee() != null) && (rgInvestmentaccount.getDiscount() != null) && (rgInvestmentaccount.getOther() != null))
				rgInvestmentaccount.setBalanceAmount(rgInvestmentaccount.getNetAmount().subtract(rgInvestmentaccount.getFee()).subtract(rgInvestmentaccount.getDiscount()).subtract(rgInvestmentaccount.getOther()));

			// COUNT TOTAL FEE AMOUNT
			if ((rgInvestmentaccount.getFee() != null) && (rgInvestmentaccount.getDiscount() != null) && (rgInvestmentaccount.getOther() != null))
				rgInvestmentaccount.setTotalFee(rgInvestmentaccount.getFee().add(rgInvestmentaccount.getDiscount()).add(rgInvestmentaccount.getOther()));

			// COUNT CAPITAL TAX AMOUNT
			if (rgInvestmentaccount.getNetAmount() != null && liq.getCapGainTax() != null) {
				if (liq.getCapGainTax().getTaxRate() != null) {
					rgInvestmentaccount.setCapTaxAmount(rgInvestmentaccount.getNetAmount().multiply(liq.getCapGainTax().getTaxRate().divide(BD_100)));
				}
			}

			// COUNT PAYMENT AMOUNT
			if ((rgInvestmentaccount.getBalanceAmount() != null) && (rgInvestmentaccount.getCapTaxAmount() != null))
				rgInvestmentaccount.setPaymentAmount(rgInvestmentaccount.getBalanceAmount().subtract(rgInvestmentaccount.getCapTaxAmount()));

		}

		return new ArrayList<RgInvestmentaccount>(investAccts);

	}

	@Check("transaction.registryLiquidationProcess")
	public static void save(RgLiquidationProcess liq, RgInvestmentaccount[] investmentaccounts, String id, String mode) {
		log.debug("save. liq: " + liq + " investmentaccounts: " + investmentaccounts + " id: " + id + " mode: " + mode);

		//Date currentDate = generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID);
		log.debug("remarks:" + liq.getRemarks());
		log.debug("externalReference:" + liq.getExternalReference());
		validation.required("Transactio Date is", liq.getTransactionDate());
		validation.required("NAV Date is", liq.getNavDate());
		validation.required("Posting Date is", liq.getPostDate());
		validation.required("Payment Date is", liq.getPaymentDate());

		log.debug("investmentaccounts " + investmentaccounts);

		// if
		// (validation.errorsMap().values().containsAll(investmentaccounts)==false)
		// validation.clear();
		if (validation.errorsMap().values().contains(investmentaccounts) == false)
			validation.clear();

		// if(investmentaccounts != null){
		// liq.getInvestmentaccounts().clear();
		// liq.getInvestmentaccounts().addAll(investmentaccounts);
		// }

		// **** RIZKI
		RgProduct product = taService.loadProduct(liq.getRgProduct().getProductCode());
		// **** RIZKI
		if (investmentaccounts != null) {
			for (RgInvestmentaccount rgInvestmentaccount : investmentaccounts) {

				if (rgInvestmentaccount != null) {

					// **** RIZKI
					if (rgInvestmentaccount.getUnit() != null) {
						rgInvestmentaccount.setUnitRound(Helper.format(rgInvestmentaccount.getUnit(), product.getUnitRoundValue().intValue(), FormatRoundHelper.getRoundMode(product.getUnitRoundType())));
					}
					if (rgInvestmentaccount.getPrice() != null) {
						rgInvestmentaccount.setPriceRound(Helper.format(rgInvestmentaccount.getPrice(), product.getPriceRoundValue().intValue(), FormatRoundHelper.getRoundMode(product.getPriceRoundType())));
					}
					if (rgInvestmentaccount.getPrice() == null || rgInvestmentaccount.getUnit() == null) {
						rgInvestmentaccount.setBalanceAmount(BigDecimal.ZERO);
						rgInvestmentaccount.setBalanceAmountRound(Helper.format(rgInvestmentaccount.getBalanceAmount(), product.getAmountRoundValue().intValue(), FormatRoundHelper.getRoundMode(product.getAmountRoundType())));
					} else {
						rgInvestmentaccount.setBalanceAmount(rgInvestmentaccount.getPrice().multiply(rgInvestmentaccount.getUnit()));
						rgInvestmentaccount.setBalanceAmountRound(rgInvestmentaccount.getPriceRound().multiply(rgInvestmentaccount.getUnitRound()));
						rgInvestmentaccount.setBalanceAmountRound(Helper.format(rgInvestmentaccount.getBalanceAmountRound(), product.getAmountRoundValue().intValue(), FormatRoundHelper.getRoundMode(product.getAmountRoundType())));

					}
					// **** RIZKI
					liq.getInvestmentaccounts().add(rgInvestmentaccount);
				}
			}
		}

		/*
		 * COMMENT RIZKY if( liq.getTransactionDate() != null ){ if(
		 * liq.getTransactionDate().compareTo(currentDate) > 0){
		 * validation.addError("",
		 * "liquidation.tradedate_greater_than_applicationDate"); } }
		 * 
		 * if( liq.getNavDate() != null ){ if(
		 * liq.getNavDate().compareTo(liq.getGoodFundDate()) > 0 ){
		 * validation.addError("",
		 * "liquidation.navdate_greater_than_goodFundDate"); } }
		 */
		if ((liq.getPostDate() != null) && (liq.getTransactionDate() != null)) {
			if (fmtYYYYMMDD(liq.getPostDate()).compareTo(fmtYYYYMMDD(liq.getTransactionDate())) < 0) {
				validation.addError("", "redemption.postdate_less_than_transactionDate");
			}
		}

		// Tambahan validasi dimana post date tidak boleh <= dari fund
		// date(rg_prod_eod.eod_date).
		if (liq.getPostDate() != null && liq.getRgProduct() != null) {
			RgProdEod rgProdEod = taService.getProdEodByProductCode(liq.getRgProduct().getProductCode());
			if (rgProdEod != null) {
				if (rgProdEod.getLastEod() != null) {
					if (liq.getPostDate().compareTo(rgProdEod.getLastEod()) <= 0) {
						validation.addError("", "redemption.postdate_must_greater_than_eod");
					}
				}
			}
		}

		if (liq.getPostDate() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(liq.getTransactionDate());
			cal.add(Calendar.DATE, product.getRedPostPeriod());
			Date defaultPostDate = cal.getTime();
			// liq.setPostDate(cal.getTime());
			log.debug("defaultPostDate:" + defaultPostDate);
			if (liq.getPostDate().compareTo(defaultPostDate) > 0) {
				validation.addError("", "redemption.postdate_greater_than_default");
			}
		}

		if (liq.getPostDate() != null && liq.getPaymentDate() != null) {
			if (liq.getPaymentDate().compareTo(liq.getPostDate()) < 0) {
				validation.addError("", "redemption.paymentdate_less_than_postdate");
			}
		}

		if (liq.getRgProduct() != null && liq.getPaymentDate() != null) {
			int maxPaymentDate = 0;

			if (liq.getRgProduct().getMaxPaymentDate() != null) {
				maxPaymentDate = liq.getRgProduct().getMaxPaymentDate().intValue();
			}
			/*
			 * Calendar cal = Calendar.getInstance(); cal.setTime(currentDate);
			 * logger.debug( "currentDate:"+currentDate ); logger.debug(
			 * "currentDate:"+currentDate ); cal.add(Calendar.DATE,
			 * maxPaymentDate); logger.debug( "cal.getTime():"+cal.getTime());
			 */
			log.debug("maxPaymentDate:" + maxPaymentDate);
			log.debug("liq.getPaymentDate():" + liq.getPaymentDate());
			log.debug("liq.getGoodFundDate():" + liq.getGoodFundDate());
			Date workingDate = generalService.getWorkingDate(liq.getTransactionDate(), maxPaymentDate);
			log.debug("workingDate : " + workingDate);
			if (liq.getPaymentDate().compareTo(workingDate) > 0) {
				validation.addError("", "liquidation.payment_date_exceed_max");
			}
		}

		if (liq.getTransactionDate() != null) {
			Date cDate = generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID);
			if (liq.getTransactionDate().compareTo(cDate) > 0) {
				validation.addError("", "liquidation.trans_date_less_than_current_date");
			}

		}
		/*
		 * if (liq.getRgProduct() != null) { List<RgInvestmentaccount>
		 * investAccs =
		 * taService.listInvestmentByProduct(liq.getRgProduct().getProductCode
		 * ()); dataInvest(liq, investAccs); investmentaccounts = new
		 * ArrayList<RgInvestmentaccount>(investAccs); }
		 * 
		 * if (investmentaccounts != null) { for (RgInvestmentaccount
		 * rgInvestmentaccount : investmentaccounts) {
		 * liq.getInvestmentaccounts().add(rgInvestmentaccount); } }
		 */

		//boolean confirming = true;

		if (validation.hasErrors()) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_MATURITY_PROCESS));
			render("RegistryLiquidationProcess/detail.html", liq, mode);
		} else {

			if (id == null) {
				id = liq.getRgProduct().getProductCode();
			}
			log.debug("serializing with serializerService.serialize(" + session.getId() + ", " + id + ", " + liq + ");");
			serializerService.serialize(session.getId(), id, liq);
			confirming(id, mode);
		}

	}

	public static void confirming(String id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		RgProduct product = taService.loadProduct(id);

		RgLiquidationProcess liq = serializerService.deserialize(session.getId(), id, RgLiquidationProcess.class);

		if (product.getLiquidDate() != null) {
			/*
			 * if (product.getRedNavUsed() != null){ Calendar cal =
			 * Calendar.getInstance(); cal.setTime(product.getLiquidDate());
			 * cal.add(Calendar.DATE, product.getRedNavUsed());
			 * liq.setNavDate(cal.getTime()); }
			 * 
			 * if (product.getRedPostPeriod() != null){ Calendar cal =
			 * Calendar.getInstance(); cal.setTime(product.getLiquidDate());
			 * cal.add(Calendar.DATE, product.getRedPostPeriod());
			 * liq.setPostDate(cal.getTime()); }
			 */

			if (product.getRedPayPeriod() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(product.getLiquidDate());
				cal.add(Calendar.DATE, product.getRedPayPeriod());

				// *RIZKY liq.setPaymentDate(cal.getTime());
			}
		}
		Random rnd = new Random();
		int batchNo = rnd.nextInt();
		liq.setBatchNo(batchNo);
		liq.setGoodFundDate(product.getLiquidDate());

		liq.setRgProduct(product);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_REDEMPTION));
		render("RegistryLiquidationProcess/detail.html", liq, mode);

	}

	@Check("transaction.registryLiquidationProcess")
	public static void confirm(RgLiquidationProcess liq) {
		log.debug("confirm. liq: " + liq);

		try {
			if (liq.getRgProduct() != null) {

				String userKey = session.get(UIConstants.SESSION_USER_KEY);
				String username = session.get(UIConstants.SESSION_USERNAME);

				Map<String, Object> map = taService.processLiquidation(liq, username, userKey);
				int tradeKey = (Integer) map.get("tradeKey");
				liq = (RgLiquidationProcess) map.get("liq");

				/***
				 * THIS PART OF NUMBERING PROCESS FOR DISPLAY ON SCREEN, SHOULD
				 * BE BY 'UPLOAD BATCH NO', NOW STILL USING RANDOM NUMBER BY
				 * JAVA
				 ***/

				boolean confirming = true;

				// int tradeKey = 100;
				// Random random = new Random();
				if (tradeKey != 0) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", Messages.get("liquidation.confirmed.successful", liq.getLiquidationNo()));
					renderJSON(result);

					/** END OF PART **/
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_MATURITY_PROCESS));
					render("RegistryLiquidationProcess/detail.html", confirming, liq);
				}

			}
		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if(ex.getParams()!= null){
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			}else{
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
			renderJSON(result);
		}catch (Exception e) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("error",e.getMessage());
			renderJSON(result);
		}
	}

	public static void back(String id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		RgLiquidationProcess liq = serializerService.deserialize(session.getId(), id, RgLiquidationProcess.class);
		List<RgInvestmentaccount> investAccs = taService.listInvestmentByProduct(liq.getRgProduct().getProductCode());
		dataInvest(liq, investAccs);
		Set<RgInvestmentaccount> investmentaccounts = new HashSet<RgInvestmentaccount>(investAccs);
		// RgLiquidationProcess liq = new RgLiquidationProcess();
		// List<RgInvestmentaccount> investAccs =
		// taService.listInvestmentByProduct(id);
		// for (RgInvestmentaccount rgInvestmentaccount : investAccs) {
		// liq.setRgProduct(rgInvestmentaccount.getRgProduct());
		// }
		// liq.setGoodFundDate(liq.getRgProduct().getLiquidDate());
		// dataInvest(liq, investAccs);
		// List<RgInvestmentaccount> investmentaccounts = new
		// ArrayList<RgInvestmentaccount>(investAccs);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_MATURITY_PROCESS));
		render("RegistryLiquidationProcess/detail.html", liq, investmentaccounts, mode);

	}

	public static void approval(String taskId, String operation, Long keyId, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " operation: " + operation + " keyId: " + keyId + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		// render("RegistryLiquidationProcess/approval.html", trade, mode,
		// taskId, from);
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);
	}

	public static void maturityPaging(Paging page, LiquidationProcessSearchParameter param) {
		log.debug("maturityPaging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			if (param.maturityDateFrom != null) {
				page.addParams("trunc(p.liquidDate)", Paging.GREATEQUAL, param.maturityDateFrom);
			}

			if (param.maturityDateTo != null) {
				page.addParams("trunc(p.liquidDate)", Paging.LESSEQUAL, param.maturityDateTo);
			}

			page.addParams("trunc(p.liquidDate)", Paging.LESS, param.maxDate);
			page.addParams("p.isActive", Paging.EQUAL, "1");

			if (param.fundCode != null) {
				page.addParams("p.productCode", Paging.EQUAL, param.fundCode);
			}
			page.addParams(Helper.searchAll("(to_char(p.liquidDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "p.productCode||p.name||p.lookupByClass||p.lookupByType)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = taService.maturityPaging(page);
		}
		renderJSON(page);
	}
}