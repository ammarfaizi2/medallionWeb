package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import helpers.serializers.BankAccountPickSerializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.RgTradeSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.AbstractRgTrade;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgFeeTier;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgProdEod;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgProductLockException;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.RgTransaction;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import extensions.StatusExtension;

@With(Secure.class)
public class RegistrySubscription extends Registry {
	private static Logger log = Logger.getLogger(RegistrySubscription.class);

	// for rendering radio button purpose
	private static final GnLookup subscriptionTopUp = generalService.getLookup(generalService.getValueParam(SystemParamConstants.PARAM_SUBSCRIPTION_TYPE_TOP_UP));
	private static final GnLookup subscriptionNew = generalService.getLookup(generalService.getValueParam(SystemParamConstants.PARAM_SUBSCRIPTION_TYPE_NEW));

	private static final String SUBSCRIPTION_FEE_PERCENTAGE = "SUBSCRIPTION_FEE_PERCENTAGE";
	private static final String SUBSCRIPTION_FEE_AMOUNT = "SUBSCRIPTION_FEE_AMOUNT";

	public static final String SPECIAL_CHAR = ",,";

	// java jdk only support string "true" as true value representative
	// Example: Boolean.parseBoolean("True") returns true.
	// Example: Boolean.parseBoolean("yes") returns false.
	private static final String TRUE_NUMBER = "1";
	private static final String FALSE_NUMBER = "0";

	private static final String FROM_UNIT_REGISTRY = "unitRegistry";
	private static final String FROM_CANCEL_TRADE = "cancelTrade";
	private static final String FROM_CANCEL_TRADE_APPROVAL = "cancelTradeApp";

	private static BigDecimal getTaxAmt(RgTrade sub) {
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

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "edit", "view", "entry", "save", "confirming", "confirm", "back", "approval", "cancel" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> inputBy = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._INPUT_BY);
		renderArgs.put("inputBy", inputBy);

		renderArgs.put("mode_entry", UIConstants.DISPLAY_MODE_ENTRY);

		renderArgs.put("subscriptiontype_topup_val", subscriptionTopUp.getLookupCode());
		renderArgs.put("subscriptiontype_new_val", subscriptionNew.getLookupCode());
		renderArgs.put("subscriptiontype_topup_descr", subscriptionTopUp.getLookupDescription());
		renderArgs.put("subscriptiontype_new_descr", subscriptionNew.getLookupDescription());

		renderArgs.put("subscriptionfee_percentage", SUBSCRIPTION_FEE_PERCENTAGE);
		renderArgs.put("subscriptionfee_amount", SUBSCRIPTION_FEE_AMOUNT);

		renderArgs.put("domainBankSub", LookupConstants._DOMAIN_BANK_SUB);

		renderArgs.put("specialChar", SPECIAL_CHAR);
	}

	@Check("transaction.registrySubscription")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
		RgTradeSearchParameters params = new RgTradeSearchParameters();
		render("RegistrySubscription/list.html", params);
	}

	@Check("transaction.registrySubscription")
	public static void search(RgTradeSearchParameters params) {
		log.debug("search. params: " + params);

		List<RgTrade> subs = taService.searchRgTrade(params.rgTradeSearchTransactionDateFrom, params.rgTradeSearchTransactionDateTo, params.rgTradeSearchFundKey, params.rgTradeSearchInvestorAcct, UIHelper.withOperator(params.transactionSearchNoOperator, params.TransactionNoOperator));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
		render("RegistrySubscription/grid.html", subs);
	}

	@Check("transaction.registrySubscription")
	public static void searchPaging(Paging page, RgTradeSearchParameters param) {
		log.debug("searchPaging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("rt.tradeDate", page.GREATEQUAL, param.rgTradeSearchTransactionDateFrom);
			page.addParams("rt.tradeDate", page.LESSEQUAL, param.rgTradeSearchTransactionDateTo);
			page.addParams("rt.rgProduct.productCode", page.EQUAL, param.rgTradeSearchFundKey);
			// page.addParams("rt.rgInvestmentaccount.accountNumber",
			// page.EQUAL, param.rgTradeSearchInvestorAcct);
			page.addParams("rg.customer.customerNo", page.EQUAL, param.rgTradeSearchInvestorAcct);
			page.addParams("rt.tradeStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_REJECTED);
			page.addParams("rt.recordStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_NEED_REVISION);
			page.addParams("rt.tradeKey", param.TransactionNoOperator, UIHelper.withOperator(param.transactionSearchNoOperator, param.TransactionNoOperator));
			// page.addParams("rt.tradeKey", page.LIKE,
			// UIHelper.withOperator(page.getsSearch(), 1));
			page.addParams("rt.type", Paging.EQUAL, AbstractRgTrade.TRADETYPE_SUBSCRIBE);
			page.addParams(Helper.searchAll("(rt.tradeKey||rp.productCode||to_char(rt.tradeDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "rg.accountNumber||rg.name||rt.netAmount||rt.price||rt.unit)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = taService.pagingRgTrade(page);
		}

		renderJSON(page);
	}

	@Check("transaction.registrySubscription")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		RgTrade sub = taService.loadTrade(id);
		sub.setRgProduct(sub.getRgInvestmentaccount().getRgProduct());

		// RgProductBnAccount rgProdBn =
		// taService.getRgProductBnAccount(sub.getRgProduct().getBankAccount().getBankAccountKey(),
		// sub.getRgProduct().getProductCode());
		// sub.setRgProductBnAccount(rgProdBn.getBnAccount());

		sub.setTaxPct(sub.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		sub.setTaxAmt(getTaxAmt(sub));
		if (sub.getFeePct() != null)
			sub.setFeePercent(true);

		if (sub.getFeePct() == null) {
			// untuk hitung fee percent selalu menggunakan fee amount dan net
			// amount (req dari mr suwanly)
			if ((sub.getFeeAmt() != null) && (sub.getNetAmount() != null)) {
				/*
				 * Double feePct = (sub.getFeeAmt().doubleValue() /
				 * sub.getNetAmount().doubleValue()) * 100; BigDecimal feePct1 =
				 * new BigDecimal(feePct);
				 */
				Double feePct = new Double(0);
				if (sub.getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
					feePct = new Double(0);
				} else {
					feePct = (sub.getFeeAmt().doubleValue() / sub.getNetAmount().doubleValue()) * 100;
				}
				// Double feePct = (sub.getFeeAmt().doubleValue() /
				// sub.getNetAmount().doubleValue()) * 100;
				BigDecimal feePct1 = new BigDecimal(feePct);
				sub.setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
			}
		}
		sub.setDefaultPostDate(sub.getPostDate());

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
		render("RegistrySubscription/entry.html", sub, mode);
	}

	@Check("transaction.registrySubscription")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgTrade sub = taService.loadTrade(id);
		sub.setRgProduct(sub.getRgInvestmentaccount().getRgProduct());
		sub.setTaxPct(sub.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		sub.setTaxAmt(getTaxAmt(sub));
		if (sub.getFeePct() != null)
			sub.setFeePercent(true);

		if (sub.getFeePct() == null) {
			// untuk hitung fee percent selalu menggunakan fee amount dan net
			// amount (req dari mr suwanly)
			if ((sub.getFeeAmt() != null) && (sub.getNetAmount() != null)) {
				/*
				 * Double feePct = (sub.getFeeAmt().doubleValue() /
				 * sub.getNetAmount().doubleValue()) * 100; BigDecimal feePct1 =
				 * new BigDecimal(feePct);
				 */
				Double feePct = new Double(0);
				if (sub.getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
					feePct = new Double(0);
				} else {
					feePct = (sub.getFeeAmt().doubleValue() / sub.getNetAmount().doubleValue()) * 100;
				}
				// Double feePct = (sub.getFeeAmt().doubleValue() /
				// sub.getNetAmount().doubleValue()) * 100;
				BigDecimal feePct1 = new BigDecimal(feePct);
				sub.setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
			}
		}
		// RgProductBnAccount rgProdBn =
		// taService.getRgProductBnAccount(sub.getRgProduct().getBankAccount().getBankAccountKey(),
		// sub.getRgProduct().getProductCode());
		// sub.setRgProductBnAccount(rgProdBn.getBnAccount());

		// sub.setTrnSABranch(generalService.getThirdPartyReference(sub.getTrnSABranchKey()));
		sub.setTrnSABranch(generalService.getThirdParty(sub.getTrnSABranchKey()));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
		render("RegistrySubscription/entry.html", sub, mode);
	}

	@Check("transaction.registrySubscription")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		RgTrade sub = new RgTrade();

		sub.setTradeDate(taService.getCurrentBusinessDate());
		sub.setGoodfundDate(sub.getTradeDate());
		sub.setFeePct(BigDecimal.ZERO);
		sub.setFeeAmt(null);
		sub.setDiscAmt(new BigDecimal(0));
		sub.setOtherAmt(new BigDecimal(0));
		sub.setFirstSubscribe(Boolean.TRUE);
		GnLookup lookupGross = generalService.getLookup(LookupConstants.INPUT_BY_GROSS);
		sub.setTradeBy(lookupGross);
		sub.setFeePercent(true);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
		render("RegistrySubscription/entry.html", sub, mode);
	}

	@Check("transaction.registrySubscription")
	public static void save(RgTrade sub, String mode, RgTrade rg, String from) {
		log.debug("save. sub: " + sub + " mode: " + mode + " rg: " + rg + " from: " + from);

		if (sub == null)
			entry();

		// Helper.showProperties(sub);

		log.debug("unit " + sub.getUnit());
		log.debug("price " + sub.getPrice());

		RgProduct product = taService.loadProduct(sub.getRgProduct().getProductCode());
		if (product != null) {
			// split from other tables
			product.splitProductFees();
			// assume only one subscribe fee?
			RgFeeTier subFee = product.getSubFees().get(0);
			product.setSubMaxFee(subFee.getMaxValue());
		}
		// set fee pct if null
		if (sub.getFeePct() == null) {
			/*
			 * if ((sub.getFeeAmt() != null) &&
			 * ((sub.getTradeBy().getLookupId().
			 * equals(LookupConstants.INPUT_BY_GROSS)) && (sub.getAmount() !=
			 * null)) ||
			 * ((sub.getTradeBy().getLookupId().equals(LookupConstants.
			 * INPUT_BY_NET)) && (sub.getNetAmount() != null))) { if
			 * (sub.getTradeBy
			 * ().getLookupId().equals(LookupConstants.INPUT_BY_GROSS)) { //
			 * sub.
			 * setFeePct((sub.getFeeAmt().divide(sub.getAmount())).multiply(new
			 * BigDecimal(100))); Double feePct = (sub.getFeeAmt().doubleValue()
			 * / sub.getAmount().doubleValue()) * 100; BigDecimal feePct1 = new
			 * BigDecimal(feePct); // pake 4 digit dibelakang koma karena di
			 * screen untuk field subscription fee percent hanya // mengijinkan
			 * 4 digit dibelakang koma sub.setFeePct(feePct1.setScale(4,
			 * BigDecimal.ROUND_HALF_UP)); } if
			 * (sub.getTradeBy().getLookupId().equals
			 * (LookupConstants.INPUT_BY_NET)) { //
			 * sub.setFeePct((sub.getFeeAmt(
			 * ).divide(sub.getNetAmount())).multiply(new BigDecimal(100)));
			 * Double feePct = (sub.getFeeAmt().doubleValue() /
			 * sub.getNetAmount().doubleValue()) * 100; BigDecimal feePct1 = new
			 * BigDecimal(feePct); sub.setFeePct(feePct1.setScale(4,
			 * BigDecimal.ROUND_HALF_UP)); } }
			 */

			// untuk hitung fee percent selalu menggunakan fee amount dan net
			// amount (req dari mr suwanly)
			if ((sub.getFeeAmt() != null) && (sub.getNetAmount() != null)) {
				Double feePct = new Double(0);
				if (sub.getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
					feePct = new Double(0);
				} else {
					feePct = (sub.getFeeAmt().doubleValue() / sub.getNetAmount().doubleValue()) * 100;
				}
				// Double feePct = (sub.getFeeAmt().doubleValue() /
				// sub.getNetAmount().doubleValue()) * 100;
				BigDecimal feePct1 = new BigDecimal(feePct);
				sub.setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
			}
		}

		validation.required("Fund Code is", sub.getRgProduct().getProductCode());
		validation.required("Account No is", sub.getRgInvestmentaccount().getAccountNumber());
		validation.required("Transaction date is", sub.getTradeDate());
		// validation.required("Good Fund Date is", sub.getGoodfundDate());
		validation.required("NAV Date is", sub.getNavDate());
		validation.required("Posting Date is", sub.getPostDate());
		// validation.required("SA Code is",
		// sub.getRgInvestmentaccount().getThirdPartyBySaCode().getThirdPartyKey());
		validation.required("Subscribe By is", sub.getTradeBy().getLookupId());

		if (LookupConstants.RG_SUBSCRIPTION_INPUT_BY_GROSS.equals(sub.getTradeBy().getLookupId())) {
			validation.required("Gross Amount is", sub.getAmount());
		}
		if (LookupConstants.RG_SUBSCRIPTION_INPUT_BY_NET.equals(sub.getTradeBy().getLookupId())) {
			validation.required("Net Amount is", sub.getNetAmount());
		}
		validation.required("Subscription Fee is", sub.getFeeAmt());
		// validation.required("Discount Amount is", sub.getDiscAmt());
		// validation.required("Tax Of Fee Amount is", sub.getTaxAmt());
		validation.required("Other Fee Amount is", sub.getOtherAmt());
		// validation.required("Total Fee Amount is", sub.getTotFeeAmt());
		// validation.required("Net Amount is", sub.getNetAmount());
		// validation.required("Unit is", sub.getUnit());

		// validation.required("Investment Acc is",
		// sub.getRgInvestmentaccount().getAccountNumber());
		// validation.required("Bank Account is",
		// sub.getBankAccount().getAccountNo());
		// validation.required("Bank Code is",
		// sub.getBankAccount().getBankCode().getThirdPartyCode());
		// validation.required("Trn SA Branch is",
		// sub.getTrnSABranch().getThirdPartyCode());

		// validation.required("subscription.netAmount", sub.getNetAmount());
		validation.required("Fund Account No is", sub.getRgProductBnAccount().getAccountNo());

		Date applicationDate = taService.getCurrentBusinessDate();

		if (fmtYYYYMMDD(sub.getTradeDate()).compareTo(fmtYYYYMMDD(applicationDate)) > 0) {
			// validation.addError("global",
			// "subscription.tradedate_greater_than_applicationDate");
			validation.addError("", "subscription.tradedate_greater_than_applicationDate");
		}
		// if
		// (fmtYYYYMMDD(sub.getGoodfundDate()).compareTo(fmtYYYYMMDD(sub.getTradeDate()))
		// < 0) {
		// validation.addError("global",
		// "validation.goodfunddate_greater_than_tradedate");
		// }
		if (fmtYYYYMMDD(sub.getNavDate()).compareTo(fmtYYYYMMDD(sub.getGoodfundDate())) > 0) {
			// validation.addError("global",
			// "subscription.navdate_greater_than_goodFundDate");
			validation.addError("", "subscription.navdate_greater_than_goodFundDate");
		}
		if ((sub.getPostDate() != null) && (fmtYYYYMMDD(sub.getPostDate()).compareTo(fmtYYYYMMDD(sub.getGoodfundDate())) < 0)) {
			// validation.addError("global",
			// "subscription.postdate_less_than_goodFundDate");
			validation.addError("", "subscription.postdate_less_than_goodFundDate");
		}
		// if
		// (fmtYYYYMMDD(sub.getPostDate()).compareTo(fmtYYYYMMDD(APPLICATION_DATE))
		// < 0) {
		// validation.addError("global",
		// "validation.postdate_less_than_applicationDate");
		// }
		if (sub.getNetAmount() != null) {
			if (sub.getFirstSubscribe()) {
				if (product != null && product.getSubInitMinAmt() != null) {
					if (sub.getNetAmount().doubleValue() < product.getSubInitMinAmt().doubleValue()) {
						// validation.addError("global",
						// "subscription.newAmount_less_than_subscribeMinimumAmount");
						validation.addError("", "subscription.newAmount_less_than_subscribeMinimumAmount");
					}
				}
			} else {
				if (product != null && product.getSubMinAmt() != null) {
					if (sub.getNetAmount().doubleValue() < product.getSubMinAmt().doubleValue()) {
						// validation.addError("global",
						// "subscription.newAmount_less_than_subscribeMinimumAmount");
						validation.addError("", "subscription.amount_less_than_topUpMinimumAmount");
					}
				}
			}

			if (product != null && product.getSubMaxAmt() != null) {
				if (sub.getNetAmount().doubleValue() > product.getSubMaxAmt().doubleValue()) {
					// validation.addError("global",
					// "subscription.newAmount_greater_than_subscribeMaximumAmount");
					validation.addError("", "subscription.newAmount_greater_than_subscribeMaximumAmount");
				}
			}
		}

		/*
		 * if ((sub.getFeePct() != null) && (product != null) &&
		 * ((product.getRgFeeTiers()!= null) && (product.getRgFeeTiers().size()
		 * > 0))) { for (RgFeeTier rgFeeTier : product.getRgFeeTiers()) { if
		 * (rgFeeTier.getId().getType().equals(RgProduct.FEE_SUBSCRIBE)) { if
		 * (((rgFeeTier.getMaxValue() != null) &&
		 * (rgFeeTier.getMaxValue().compareTo(BigDecimal.ZERO) > 0)) &&
		 * (sub.getFeePct().compareTo(rgFeeTier.getMaxValue()) > 0)) {
		 * validation.addError("",
		 * "subscription.subscriptionFeePercent_greater_than_maxSubscriptionFeePercent"
		 * ); } break; } } }
		 */

		if (((sub.getFeePct() != null) && (product != null)) && ((sub.getFeePct() != null) && (sub.getFeePct().compareTo(BigDecimal.ZERO) > 0)) && ((product.getSubMaxFee() != null) && (product.getSubMaxFee().compareTo(BigDecimal.ZERO) >= 0))) {
			if (sub.getFeePct().compareTo(product.getSubMaxFee()) > 0) {
				validation.addError("", "subscription.subscriptionFeePercent_greater_than_maxSubscriptionFeePercent");
			}
		}

		log.debug("[save] sub.rgProduct.sublock = " + sub.getRgProduct().getSubLock());
		if (sub.getRgProduct().getSubLock() != null) {
			if (sub.getRgProduct().getSubLock() == true) {
				List<RgProductLockException> lockExceptionDates = taService.listRgProductLockExceptionByType(sub.getRgProduct().getProductCode(), UIConstants.TRADE_TYPE_SUBSCRIBE);
				log.debug("[save] size lockExceptionDates = " + lockExceptionDates.size());
				boolean checkSame = true;
				for (RgProductLockException le : lockExceptionDates) {
					log.debug("[save] sub.goodfundDate = " + sub.getGoodfundDate() + "same with exceptionDate = " + le.getId().getExceptionDate());
					if (sub.getGoodfundDate().getTime() == le.getId().getExceptionDate().getTime()) {
						checkSame = false;
						break;
					}
				}
				if (checkSame) {
					validation.addError("", "subscription.not_allowed_same_with_exceptionDate");
				}
			}
		}

		if ((sub.getRgProductBnAccount() != null) && (sub.getRgProductBnAccount().getCurrency() != null) && (!Helper.isNullOrEmpty(sub.getRgProductBnAccount().getCurrency().getCurrencyCode()))) {
			if ((sub.getRgProduct() != null) && (sub.getRgProduct().getCurrency() != null) && (!Helper.isNullOrEmpty(sub.getRgProduct().getCurrency().getCurrencyCode()))) {
				if (!sub.getRgProductBnAccount().getCurrency().getCurrencyCode().equals(sub.getRgProduct().getCurrency().getCurrencyCode())) {
					validation.addError("", "subscription.currencyFundAcount_not_same_as_currencyFund");
				}
			}
		}

		if ((sub.getBankAccount() != null) && (sub.getBankAccount().getCurrency() != null) && (!Helper.isNullOrEmpty(sub.getBankAccount().getCurrency().getCurrencyCode()))) {
			if ((sub.getRgInvestmentaccount() != null) && (sub.getRgInvestmentaccount().getBankAccount() != null) && (sub.getRgInvestmentaccount().getBankAccount().getCurrency() != null) && (!Helper.isNullOrEmpty(sub.getRgInvestmentaccount().getBankAccount().getCurrency().getCurrencyCode()))) {
				if (!sub.getBankAccount().getCurrency().getCurrencyCode().equals(sub.getRgInvestmentaccount().getBankAccount().getCurrency().getCurrencyCode())) {
					validation.addError("", "subscription.currencyInvestorAcount_not_same_as_currencyAccountNo");
				}
			}
		}

		if (from != null && from.equals(FROM_CANCEL_TRADE)) {
			validation.clear();
			log.debug("[SAVE] TRADE KEY = " + rg.getTradeKey());
			log.debug("[SAVE] TYPE = " + rg.getType());
			log.debug("[SAVE] CANCEL DATE = " + rg.getCancelTradeDate());
			log.debug("[SAVE] CANCEL POST DATE = " + rg.getCancelPostDate());
			log.debug("[SAVE] REMARK = " + rg.getCancelRemark());
			sub.setType(rg.getType());
			sub.setCancelTradeDate(rg.getCancelTradeDate());
			sub.setCancelPostDate(rg.getCancelPostDate());
			sub.setCancelRemark(rg.getCancelRemark());
		}

		if (sub.getPostDate() != null && sub.getRgProduct() != null) {
			RgProdEod rgProdEod = taService.getProdEodByProductCode(sub.getRgProduct().getProductCode());
			if (rgProdEod != null) {
				if (rgProdEod.getLastEod() != null) {
					log.debug("POST DATE = " + sub.getPostDate());
					log.debug("LAST EOD DATE = " + rgProdEod.getLastEod());
					if (sub.getPostDate().compareTo(rgProdEod.getLastEod()) <= 0) {
						validation.addError("", "subscription.postdate_must_greater_than_eod");
					}
				}
			}
		}

		if (sub.getPostDate() != null && sub.getDefaultPostDate() != null) {
			if (sub.getPostDate().compareTo(sub.getDefaultPostDate()) > 0) {
				validation.addError("", "subscription.postdate_greater_than_default");
			}
		}
		
		//#4384  Validasi transaksi RG yang sama diinput/diupload berulang kali
		log.debug("type  " + sub.getType() + " externalRefs " + sub.getExternalReference() + " tradeDate " + sub.getTradeDate());
		if (sub.getExternalReference() != null) {
			Boolean isTradeExist = taService.isRgTradeAlreadyExist(sub.getTradeDate(), AbstractRgTrade.TRADETYPE_SUBSCRIBE, sub.getExternalReference(), sub.getTradeKey());
			log.debug("isTradeExist >>>> "  + isTradeExist);
			if (isTradeExist) validation.addError("", "trade.data.duplicate");
		}

		if (validation.hasErrors()) {
			if (from != null && from.equals(FROM_CANCEL_TRADE)) {
				BigDecimal unitPorto = taService.loadUnitPortfolioByTradeCancel(rg.getTradeKey(), sub.getTradeDate());
				String messageError = "";
				Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
				if (sub.getRgTransaction() != null) {
					if (sub.getTradeStatus().equals(RgTrade.TRADESTATUS_VALID) && sub.getRgTransaction().getType().equals(RgTransaction.TRANSTYPE_SUBSCRIBE)) {
						if (unitPorto == null) {
							messageError = "Unit Portfolio null !";
						} else {
							if (sub.getRgTransaction().getUnit().compareTo(unitPorto) > 0) {
								messageError = "Unit this trade greater than unit in portfolio !";
							}
						}
					}
				}

				String status = rg.getRecordStatus();
				if (from.equals("unitRegistry"))
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_INQUIRY_REGISTRY));
				else
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
				render("RegistrySubscription/cancel.html", sub, mode, from, rg, currentBusinessDate, status, messageError);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
				render("RegistrySubscription/entry.html", sub, mode);
			}
		} else {
			Long id = sub.getTradeKey();
			// sub.setTrnSABranchKey(sub.getTrnSABranch().getThirdPartyKey());
			serializerService.serialize(session.getId(), id, sub);
			confirming(id, mode, from);
		}
	}

	@Check("transaction.registrySubscription")
	public static void confirming(Long id, String mode, String from) {
		log.debug("confirming. id: " + id + " mode: " + mode + " from: " + from);

		renderArgs.put("confirming", true);
		RgTrade sub = serializerService.deserialize(session.getId(), id, RgTrade.class);

		if (from != null && from.equals(FROM_CANCEL_TRADE)) {
			RgTrade rg = new RgTrade();
			rg.setTradeKey(sub.getTradeKey());
			rg.setType(sub.getType());
			rg.setCancelTradeDate(sub.getCancelTradeDate());
			rg.setCancelPostDate(sub.getCancelPostDate());
			rg.setCancelRemark(sub.getCancelRemark());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
			render("RegistrySubscription/cancel.html", sub, mode, from, rg);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
			render("RegistrySubscription/entry.html", sub, mode);
		}
	}

	private static Map<Long, Long> keyMap = new HashMap<Long, Long>();

	@Check("transaction.registrySubscription")
	public static void confirm(RgTrade sub, String mode, RgTrade rg, String from) {
		log.debug("confirm. sub: " + sub + " mode: " + mode + " rg: " + rg + " from: " + from);

		if (sub == null)
			back(null, mode, from);
		if (from != null && from.equals(FROM_CANCEL_TRADE)) {
			Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			try {
				log.debug("??????? CONFIRM CANCEL= ");
				log.debug("--------> CONFIRM CANCEL SUBSCRIBE TRADE DATE = " + rg.getCancelTradeDate());
				log.debug("--------> CONFIRM CANCEL SUBSCRIBE POST DATE = " + rg.getCancelPostDate());

				// TODO: Add by Fadly 2017-10-25 -> RedMine #3330
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
				if (sub.getFeeAmt() == null)
					sub.setFeeAmt(new BigDecimal(0));
				if (sub.getDiscAmt() == null)
					sub.setDiscAmt(new BigDecimal(0));
				if (sub.getTaxAmt() == null)
					sub.setTaxAmt(new BigDecimal(0));
				if (sub.getOtherAmt() == null)
					sub.setOtherAmt(new BigDecimal(0));

				mode = UIConstants.DISPLAY_MODE_CONFIRM;
				boolean confirming = true;

				// set price null
				sub.setPrice(null);

				sub.setUnit(null);

				// jika fee by amount maka fee percent tidak perlu save ke
				// database
				if (!sub.isFeePercent()) {
					sub.setFeePct(null);
				}

				// by defaul yaitu discount by amount dan sekarang diset 0, jadi
				// kalau discount by amount maka disc. pct tidak perlu disave
				sub.setDiscAmt(BigDecimal.ZERO);
				if (sub.getDiscAmt() == null)
					sub.setDiscPct(BigDecimal.ZERO);

				//#4384  Validasi transaksi RG yang sama diinput/diupload berulang kali
				log.debug("type  " + sub.getType() + " externalRefs " + sub.getExternalReference() + " tradeDate " + sub.getTradeDate());
				if (sub.getExternalReference() != null) {
					Boolean isTradeExist = taService.isRgTradeAlreadyExist(sub.getTradeDate(), AbstractRgTrade.TRADETYPE_SUBSCRIBE, sub.getExternalReference(), sub.getTradeKey());
					log.debug("isTradeExist >>>> "  + isTradeExist);
					if (isTradeExist) throw new MedallionException("trade.data.duplicate", "");
				}
				
				RgTrade subTrade = taService.createTrade(MenuConstants.RG_SUBSCRIPTION, sub, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
				if (!(subTrade.equals(null))) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", Messages.get("trade.confirmed.successful", subTrade.getTradeKey()));
					renderJSON(result);
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
					render("RegistrySubscription/entry.html", subTrade, mode, confirming);
				}
				// entry();
				// render("RegistrySubscription/detail.html", sub, mode);
				// entry();
			} catch (MedallionException ex) {
				renderJSON(Formatter.resultError(ex));
				/*flash.error(ex.getErrorCode(), ex.getParams());
				renderArgs.put("confirming", true);
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
				render("RegistrySubscription/entry.html", sub, mode);*/
			} catch (Exception ex) {
				List<String> params = new ArrayList<String>();
				flash.error(ex.getMessage(), params.toArray());
				renderArgs.put("confirming", true);
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
				render("RegistrySubscription/entry.html", sub, mode);
			}
		}
	}

	@Check("transaction.registrySubscription")
	public static void back(Long id, String mode, String from) {
		log.debug("back. id: " + id + " mode: " + mode + " from: " + from);

		renderArgs.put("confirming", false);

		RgTrade sub = serializerService.deserialize(session.getId(), id, RgTrade.class);
		if (from != null && from.equals(FROM_CANCEL_TRADE)) {
			RgTrade rg = new RgTrade();
			rg.setTradeKey(sub.getTradeKey());
			rg.setType(sub.getType());
			rg.setCancelTradeDate(sub.getCancelTradeDate());
			rg.setCancelPostDate(sub.getCancelPostDate());
			rg.setCancelRemark(sub.getCancelRemark());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
			render("RegistrySubscription/cancel.html", sub, mode, from, rg);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SUBSCRIPTION));
			render("RegistrySubscription/entry.html", sub, mode);
		}
	}

	// public static void approval(String taskId, Long keyId, RgTrade sub,
	// String from) {
	public static void approval(String taskId, Long keyId, RgTrade sub, String from, String operation, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " sub: " + sub + " from: " + from + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		sub = taService.loadTrade(keyId);
		sub.setRgProduct(sub.getRgInvestmentaccount().getRgProduct());
		sub.setTaxPct(sub.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		sub.setTaxAmt(getTaxAmt(sub));
		if (sub.getFeePct() != null)
			sub.setFeePercent(true);

		if (sub.getFeePct() == null) {
			// untuk hitung fee percent selalu menggunakan fee amount dan net
			// amount (req dari mr suwanly)
			if ((sub.getFeeAmt() != null) && (sub.getNetAmount() != null)) {
				/*
				 * Double feePct = (sub.getFeeAmt().doubleValue() /
				 * sub.getNetAmount().doubleValue()) * 100; BigDecimal feePct1 =
				 * new BigDecimal(feePct);
				 */
				Double feePct = new Double(0);
				if (sub.getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
					feePct = new Double(0);
				} else {
					feePct = (sub.getFeeAmt().doubleValue() / sub.getNetAmount().doubleValue()) * 100;
				}
				// Double feePct = (sub.getFeeAmt().doubleValue() /
				// sub.getNetAmount().doubleValue()) * 100;
				BigDecimal feePct1 = new BigDecimal(feePct);
				sub.setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
			}
		}

		// sub.setTrnSABranch(generalService.getThirdPartyReference(sub.getTrnSABranchKey()));
		sub.setTrnSABranch(generalService.getThirdParty(sub.getTrnSABranchKey()));
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		render("RegistrySubscription/approval.html", sub, mode, taskId, from, keyId, operation, maintenanceLogKey);
	}

	@Check("transaction.inquiryUnitRegistry")
	public static void cancel(Long keyId, String from, RgTrade rg) {
		log.debug("cancel. keyId: " + keyId + " from: " + from + " rg: " + rg);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgTrade sub = taService.loadTrade(keyId);
		RgTrade canSub = taService.loadTradeByCancelTrade(sub.getTradeKey());
		sub.setRgProduct(sub.getRgInvestmentaccount().getRgProduct());
		sub.setTaxPct(sub.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		sub.setTaxAmt(getTaxAmt(sub));
		sub.setTrnSABranch(generalService.getThirdParty(sub.getTrnSABranchKey()));
		if (sub.getFeePct() != null)
			sub.setFeePercent(true);

		if (sub.getFeePct() == null) {
			// untuk hitung fee percent selalu menggunakan fee amount dan net
			// amount (req dari mr suwanly)
			if ((sub.getFeeAmt() != null) && (sub.getNetAmount() != null)) {
				/*
				 * Double feePct = (sub.getFeeAmt().doubleValue() /
				 * sub.getNetAmount().doubleValue()) * 100; BigDecimal feePct1 =
				 * new BigDecimal(feePct);
				 */
				Double feePct = new Double(0);
				if (sub.getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
					feePct = new Double(0);
				} else {
					feePct = (sub.getFeeAmt().doubleValue() / sub.getNetAmount().doubleValue()) * 100;
				}
				// Double feePct = (sub.getFeeAmt().doubleValue() /
				// sub.getNetAmount().doubleValue()) * 100;
				BigDecimal feePct1 = new BigDecimal(feePct);
				sub.setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
			}
		}

		if (sub.isCancelled()) {
			sub.setTradeStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_CANCELED));
		} else {
			if (sub.isPosted())
				sub.setTradeStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_POSTED));
			else
				sub.setTradeStatus(getDecodeDataStatus(sub.getTradeStatus()));
		}

		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		if (from.equals(FROM_CANCEL_TRADE_APPROVAL)) {
			rg = new RgTrade();
			rg.setTradeKey(keyId);
			/*
			 * rg.setTradeDate(canSub.getTradeDate());
			 * rg.setPostDate(canSub.getPostDate());
			 * rg.setRemark(canSub.getRemark());
			 */
			rg.setCancelTradeDate(canSub.getTradeDate());
			rg.setCancelPostDate(canSub.getPostDate());
			rg.setCancelRemark(canSub.getRemark());
		}

		if (from.equals(FROM_CANCEL_TRADE) || from.equals(FROM_UNIT_REGISTRY)) {
			rg = new RgTrade();
			rg.setTradeKey(keyId);
			/*
			 * rg.setTradeDate(currentBusinessDate);
			 * rg.setPostDate(currentBusinessDate);
			 */
			// rg.setCancelTradeDate(currentBusinessDate);
			// rg.setCancelPostDate(currentBusinessDate);
			rg.setCancelTradeDate(sub.getTradeDate());
			rg.setCancelPostDate(sub.getPostDate());
			rg.setType(AbstractRgTrade.TRADETYPE_SUBSCRIBE);
		}

		// BigDecimal unitPorto =
		// taService.loadUnitPortfolioByTradeCancel(keyId, rg.getTradeDate());
		log.debug("[CANCEL] tradeDate = " + sub.getTradeDate());
		BigDecimal unitPorto = taService.loadUnitPortfolioByTradeCancel(keyId, sub.getTradeDate());
		log.debug("unit porto = " + unitPorto);
		String messageError = "";
		if (sub.getRgTransaction() != null) {
			if (sub.getTradeStatus().equals(RgTrade.TRADESTATUS_VALID) && sub.getRgTransaction().getType().equals(RgTransaction.TRANSTYPE_SUBSCRIBE)) {
				if (unitPorto == null) {
					messageError = "Unit Portfolio null !";
				} else {
					if (sub.getRgTransaction().getUnit().compareTo(unitPorto) > 0) {
						messageError = "Unit this trade greater than unit in portfolio !";
					}
				}
			}
		}

		String status = rg.getRecordStatus();
		if (from.equals("unitRegistry"))
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_INQUIRY_REGISTRY));
		else
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
		render("RegistrySubscription/cancel.html", sub, mode, from, rg, currentBusinessDate, status, messageError);
	}

	// public static void approve(String taskId, Long keyId) {
	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		boolean isMessageValidation = false;
		try {
			GnMaintenanceLog maintenance = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			RgTrade rgTrade = taService.loadTrade(Long.parseLong(maintenance.getEntityKey()));
			Map<String, BigDecimal> holdingMap = new HashMap<String, BigDecimal>();
			boolean isPriceNotNull = false;
			RgProduct rgProduct = rgTrade.getRgProduct();

			if (rgProduct != null) {
				if (rgProduct.isFixnav()) {
					isPriceNotNull = true;
				} else {
					RgNav nav = taService.loadActiveNav(rgProduct.getProductCode(), rgTrade.getNavDate());
					if (nav != null) {
						if (nav.getNav() != null) {
							isPriceNotNull = true;
						}
					}
				}
			}

			RgTrade trade = taService.approveTrade(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, null);
			// RgTrade trade =
			// taService.approveTradeSubscribe(session.get(UIConstants.SESSION_USERNAME),
			// taskId, operation, maintenanceLogKey,
			// WorkflowConstants.PROCESS_TYPE_APPROVE, null);

			if (isPriceNotNull) {
				trade.setAmount(rgTrade.getAmount());
				trade.setPrice(rgTrade.getPrice());
				trade.setUnit(rgTrade.getUnit());

				rgTrade = taService.processTransactionValidation(trade, holdingMap);
				if (!rgTrade.getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID)) {
					// throw new MedallionException("transaction.validation");
					isMessageValidation = true;
					throw new MedallionException(rgTrade.getMessage());
				}
			}
			// success if message == valid from validation process
			// or empty string from approval
			/*
			 * if (((trade.getMessage() == null) ||
			 * (trade.getMessage().trim().length() == 0)) ||
			 * (trade.getMessage().
			 * equals(LookupConstants.__TRADE_VALIDATION_VALID))) {
			 * result.put("status", "success"); result.put("message",
			 * Messages.get("trade.approved", trade.getTradeKey())); } else {
			 * result.put("error", trade.getMessage()); }
			 */
			renderJSON(Formatter.resultSuccess(Messages.get("trade.approved", trade.getTradeKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// public static void reject(String taskId, Long keyId) {
	public static void reject(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			// RgTrade trade = taService.rejectTrade(keyId,
			// session.get(UIConstants.SESSION_USERNAME), taskId);
			RgTrade trade = new RgTrade();
			if (Helper.isNullOrEmpty(correction)) {
				trade = taService.approveTrade(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, correction);
			} else {
				trade = taService.approveTrade(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "", correction);
			}

			renderJSON(Formatter.resultSuccess(Messages.get("trade.rejected", trade.getTradeKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void getBankAccountFromRgProduct(String productCode) {
		log.debug("getBankAccountFromRgProduct. productCode: " + productCode);

		// BnAccount bnAccount =
		// bankAccountService.getBankAccountByRgProduct(productCode);
		BnAccount bnAccount = bankAccountService.getBankAccountByRgProductSub(productCode);
		renderJSON(bnAccount, new BankAccountPickSerializer());
	}

	private static String getDecodeDataStatus(String statusCode) {
		return StatusExtension.decodeDataStatus(statusCode);
	}
}