package controllers;

import helpers.Formatter;
import helpers.UIConstants;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.AbstractPortfolio;
import com.simian.medallion.model.CsDailyInterest;
import com.simian.medallion.model.CsDailyPortfolio;
import com.simian.medallion.model.CsPortfolio;
import com.simian.medallion.model.CsPortfolioAmortization;
import com.simian.medallion.model.CsPortfolioUpdate;
import com.simian.medallion.model.ScCouponSchedule;
import com.simian.medallion.service.TransactionHelper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class PortfolioUpdates extends MedallionController {
	private static Logger log = Logger.getLogger(PortfolioUpdates.class);

	@Before(only = { "entry", "loadProcess", "save", "confirming", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> portfolioUpdateTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PORTFOLIO_UPDATE_TYPE);
		renderArgs.put("portfolioUpdateTypes", portfolioUpdateTypes);
	}

	@Check("transaction.portfolioUpdate")
	public static void list() {
		log.debug("list. ");
	}

	@Check("transaction.portfolioUpdate")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_PORTFOLIO_UPDATE));
		render("PortfolioUpdates/entry.html", mode);
	}

	@Check("transaction.portfolioUpdate")
	public static void loadProcess(CsPortfolioUpdate portfolioUpdate) {
		log.debug("loadProcess. portfolioUpdate: " + portfolioUpdate);

		if (portfolioUpdate == null)
			entry();
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		// VALIDATION BEFORE LOAD
		// =================================================================
		validation.required("Account No is", portfolioUpdate.getAccount().getAccountNo());
		validation.required("Security Type is", portfolioUpdate.getSecurityType().getSecurityType());
		validation.required("Security Code is", portfolioUpdate.getSecurity().getSecurityId());
		validation.required("Porto No is", portfolioUpdate.getPortfolio().getPortfolioKey());
		validation.required("Effective Date is", portfolioUpdate.getEffectiveDate());
		validation.required("Type is", portfolioUpdate.getPortfolioUpdateType().getLookupId());
		log.debug("has interest  = " + portfolioUpdate.getSecurity().getSecurityType().getHasInterest());
		log.debug("protfolioUpdate.effectiveDate = " + portfolioUpdate.getEffectiveDate());
		if (portfolioUpdate.getSecurityType().getHasInterest() != null) {
			if ((portfolioUpdate.getSecurityType().getHasInterest() == false) && (portfolioUpdate.getPortfolioUpdateType().getLookupId().equals(AbstractPortfolio.TYPE_INTEREST_FOR_PORTFOLIO_UPDATE))) {
				validation.addError(null, "Security Type hasn't Interest");
			}
		}

		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		if (portfolioUpdate.getEffectiveDate() != null) {
			if (portfolioUpdate.getEffectiveDate().getTime() > currentBusinessDate.getTime()) {
				validation.addError(null, "Effective Date must be less than Current Date");
			}
		}

		if (validation.hasErrors()) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_PORTFOLIO_UPDATE));
			render("PortfolioUpdates/entry.html", portfolioUpdate, mode);
		}

		// ========================================================================================

		mode = UIConstants.DISPLAY_MODE_VIEW;

		String type = new String();
		if (portfolioUpdate.getPortfolioUpdateType().getLookupId().equals(AbstractPortfolio.TYPE_INTEREST_FOR_PORTFOLIO_UPDATE)) {
			type = AbstractPortfolio.TYPE_INTEREST_FOR_PORTFOLIO_UPDATE;
		} else {
			type = AbstractPortfolio.TYPE_AMORTIZE_FOR_PORTFOLIO_UPDATE;
		}

		CsPortfolio portfolio = accountService.getPortfolioForUpdate(portfolioUpdate.getPortfolio().getPortfolioKey());

		// SET ABSTRACT OBJECT
		// ====================================================================
		portfolioUpdate.setSecurity(portfolio.getSecurity());
		portfolioUpdate.setAccount(portfolio.getAccount());
		portfolioUpdate.setPortfolio(portfolio);
		portfolioUpdate.setSecurityType(portfolio.getSecurity().getSecurityType());
		// ========================================================================================

		Long accountKey = portfolioUpdate.getAccount().getCustodyAccountKey();
		Long securityKey = portfolioUpdate.getSecurity().getSecurityKey();
		String classification = portfolioUpdate.getPortfolio().getClassification().getLookupId();
		String holdingRefs = portfolioUpdate.getPortfolio().getHoldingRefs();
		Date effectiveDate = portfolioUpdate.getEffectiveDate();
		log.debug("ACCOUNT KEY = " + accountKey);
		log.debug("SECURITY KEY = " + securityKey);
		log.debug("CLASSIFICATION = " + classification);
		log.debug("HOLDINGREFS = " + holdingRefs);
		log.debug("EFFECTIVEDATE = " + effectiveDate);

		CsDailyPortfolio dailyPortfolio = accountService.getDailyPortfolio(accountKey, securityKey, classification, holdingRefs, effectiveDate);
		CsDailyInterest dailyInterest = accountService.getLatestDailyInterest(accountKey, securityKey, classification, holdingRefs, effectiveDate);
		CsPortfolioAmortization portfolioAmortization = accountService.getCsPortfolioAmortizationByPortfolio(portfolio.getPortfolioKey(), portfolioUpdate.getEffectiveDate());
		ScCouponSchedule couponSchedule = securityService.getCouponScheduleOn(securityKey, new Date(effectiveDate.toGMTString()));

		// SET OBJECT ON THE SCREEN
		// ===============================================================
		if (currentBusinessDate.getTime() == portfolioUpdate.getEffectiveDate().getTime()) {
			if (portfolio != null) {
				if (portfolio.getPortfolioQuantity() != null) {
					portfolioUpdate.setHoldingQuantity(portfolio.getPortfolioQuantity());
				}
				if (dailyInterest != null) {
					if (dailyInterest.getDailyInterest() != null) {
						portfolioUpdate.setOldDailyIntAmount(dailyInterest.getDailyInterest());
					}
				}
			}
		} else {
			if (dailyPortfolio != null) {
				if (dailyPortfolio.getPortfolioQuantity() != null) {
					log.debug("dailyPortfolio.portfolioQuantity = " + dailyPortfolio.getPortfolioQuantity());
					portfolioUpdate.setHoldingQuantity(dailyPortfolio.getPortfolioQuantity());
				}
				if (dailyInterest != null) {
					if (dailyInterest.getDailyInterest() != null) {
						portfolioUpdate.setOldDailyIntAmount(dailyInterest.getDailyInterest());
					}
				}
			}
		}

		if (couponSchedule != null) {
			log.debug("[LOAD] last payment date = " + couponSchedule.getLastPaymentDate());
			log.debug("[LOAD] next payment date = " + couponSchedule.getNextPaymentDate());
			portfolioUpdate.setLastPaymentDate(couponSchedule.getLastPaymentDate());
			portfolioUpdate.setNextPaymentDate(couponSchedule.getNextPaymentDate());
		}
		portfolioUpdate.setMaturityDate(portfolio.getMaturityDate());

		int base = 0;
		if (portfolioUpdate.getSecurity().getAccrualBase() != null) {
			if (portfolioUpdate.getSecurity().getAccrualBase().getLookupCode().equals("30")) {
				base = 30;
			} else if (portfolioUpdate.getSecurity().getAccrualBase().getLookupCode().equals("31")) {
				base = 31;
			} else {
				base = 0;
			}
		}

		if (AbstractPortfolio.TYPE_INTEREST_FOR_PORTFOLIO_UPDATE.equals(type)) {
			if ((portfolioUpdate.getEffectiveDate() != null) && (portfolioUpdate.getLastPaymentDate() != null)) {
				int accruedDays = TransactionHelper.calculateAccruedDays(portfolioUpdate.getEffectiveDate(), portfolioUpdate.getLastPaymentDate(), base);
				portfolioUpdate.setAccruedDays(accruedDays);
			}

			if ((portfolioUpdate.getNextPaymentDate() != null) && (portfolioUpdate.getLastPaymentDate() != null)) {
				int totalAccruedDays = TransactionHelper.calculateAccruedDays(portfolioUpdate.getNextPaymentDate(), portfolioUpdate.getLastPaymentDate(), base);
				portfolioUpdate.setTotalAccruedDays(totalAccruedDays);
			}
		}

		if (AbstractPortfolio.TYPE_AMORTIZE_FOR_PORTFOLIO_UPDATE.equals(type)) {
			BigDecimal fairValue = BigDecimal.ZERO;
			BigDecimal unamortizedAmount = BigDecimal.ZERO;
			BigDecimal holdingQuantity = portfolioUpdate.getHoldingQuantity();
			if (portfolioUpdate.getHoldingQuantity() == null) {
				holdingQuantity = BigDecimal.ZERO;
			}
			BigDecimal price = BigDecimal.ZERO;
			BigDecimal priceUnit = portfolioUpdate.getSecurityType().getPriceUnit();

			if (portfolioAmortization != null) {
				portfolioUpdate.setPortfolioAmortization(portfolioAmortization);
				fairValue = portfolioUpdate.getPortfolioAmortization().getFairValue();
				unamortizedAmount = portfolioUpdate.getPortfolioAmortization().getUnamortAmount();
				price = portfolioAmortization.getPrice();
			}

			log.debug("[LOAD] fairValue = " + fairValue);
			log.debug("[LOAD] unamortizedAmount = " + unamortizedAmount);
			log.debug("[LOAD] holdingQuantity = " + holdingQuantity);
			log.debug("[LOAD] price = " + price);
			log.debug("[LOAD] priceUnit = " + priceUnit);
			portfolioUpdate.setParValue(fairValue.add(unamortizedAmount));
			portfolioUpdate.setPurchaseValue((holdingQuantity.multiply(price).multiply(priceUnit)));
			portfolioUpdate.setDiscountValue(portfolioUpdate.getParValue().subtract(portfolioUpdate.getPurchaseValue()));
			log.debug("[LOAD] portfolioUpdate.parValue = " + portfolioUpdate.getParValue());
			log.debug("[LOAD] portfolioUpdate.purchaseValue = " + portfolioUpdate.getPurchaseValue());
			log.debug("[LOAD] portfolioUpdate.discountValue = " + portfolioUpdate.getDiscountValue());
		}

		// ========================================================================================
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_PORTFOLIO_UPDATE));
		render("PortfolioUpdates/entry.html", portfolioUpdate, mode);
	}

	public static void edit() {
		log.debug("edit. ");

	}

	public static void view() {
		log.debug("view. ");
	}

	@Check("transaction.portfolioUpdate")
	public static void save(CsPortfolioUpdate portfolioUpdate, String mode) {
		log.debug("save. portfolioUpdate: " + portfolioUpdate + " mode: " + mode);

		if (portfolioUpdate == null)
			entry();
		log.debug("[SAVE] Use Fraction = " + portfolioUpdate.getUseFraction());
		log.debug("[SAVE] Last Payment = " + portfolioUpdate.getLastPaymentDate());
		// validation.required("Effective Date is",
		// portfolio.getEffectiveDate());
		validation.required("Holding Qty is", portfolioUpdate.getHoldingQuantity());
		validation.required("Daily Interest Amount is", portfolioUpdate.getOldDailyIntAmount());
		validation.required("Last Payment Date is", portfolioUpdate.getLastPaymentDate());
		validation.required("Next Payment Date is", portfolioUpdate.getNextPaymentDate());
		validation.required("Maturity Date is", portfolioUpdate.getMaturityDate());

		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		if (portfolioUpdate.getEffectiveDate().getTime() > currentBusinessDate.getTime()) {
			validation.addError(null, "Effective Date must be less than Current Date");
		}

		if ((portfolioUpdate.getLastPaymentDate() != null) && (portfolioUpdate.getNextPaymentDate() != null)) {
			if (portfolioUpdate.getLastPaymentDate().getTime() > portfolioUpdate.getNextPaymentDate().getTime()) {
				validation.addError(null, "Last Payment Date must be less than Next Payment Date");
			}
		}

		if ((portfolioUpdate.getNextPaymentDate() != null) && (portfolioUpdate.getMaturityDate() != null)) {
			if ((portfolioUpdate.getNextPaymentDate().getTime() >= portfolioUpdate.getMaturityDate().getTime())) {
				validation.addError(null, "Next Payment Date must be less than Maturity Date");
			}
		}

		if (AbstractPortfolio.TYPE_AMORTIZE_FOR_PORTFOLIO_UPDATE.equals(portfolioUpdate.getPortfolioUpdateType().getLookupId())) {
			if (portfolioUpdate.getPortfolioAmortization().getFairValue() != null || portfolioUpdate.getPortfolioAmortization().getFairValue() != BigDecimal.ZERO) {
				validation.required("Fair Value is", portfolioUpdate.getPortfolioAmortization().getFairValue());
			}
			if (portfolioUpdate.getPortfolioAmortization().getUnamortAmount() != null || portfolioUpdate.getPortfolioAmortization().getUnamortAmount() != BigDecimal.ZERO) {
				validation.required("Unamortized Amount is", portfolioUpdate.getPortfolioAmortization().getUnamortAmount());
			}
		}

		validation.required("Interest Rate is", portfolioUpdate.getInterestRate());

		if (portfolioUpdate.getUseFraction() != null) {
			if (portfolioUpdate.getUseFraction() == true) {
				validation.required("Using Fraction first field is", portfolioUpdate.getFractionRatio1());
				validation.required("Using Fraction second field is", portfolioUpdate.getFractionRatio2());
			}
		}

		validation.required("Accrued Interest Amount is", portfolioUpdate.getAccruedIntAmount());

		if (validation.hasErrors()) {
			mode = UIConstants.DISPLAY_MODE_VIEW;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_PORTFOLIO_UPDATE));
			render("PortfolioUpdates/entry.html", portfolioUpdate, mode);
		} else {
			log.debug("[SAVE] BEFORE SERIALIZE Use Fraction = " + portfolioUpdate.getUseFraction());
			Long id = portfolioUpdate.getPortfolioUpdateKey();
			serializerService.serialize(session.getId(), id, portfolioUpdate);
			confirming(id, mode);
		}

	}

	@Check("transaction.portfolioUpdate")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		CsPortfolioUpdate portfolioUpdate = serializerService.deserialize(session.getId(), id, CsPortfolioUpdate.class);
		log.debug("[CONFIRMING] Use Fraction = " + portfolioUpdate.getUseFraction());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_PORTFOLIO_UPDATE));
		render("PortfolioUpdates/entry.html", id, portfolioUpdate, mode);
	}

	@Check("transaction.portfolioUpdate")
	public static void confirm(CsPortfolioUpdate portfolioUpdate, String mode) {
		log.debug("confirm. portfolioUpdate: " + portfolioUpdate + " mode: " + mode);

		if (portfolioUpdate == null)
			back(null, mode);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			// TO DO Saving Process to Database //
			log.debug("[CONFIRM] Use Fraction = " + portfolioUpdate.getUseFraction());
			portfolioUpdate = accountService.createCsPortfolioUpdate(MenuConstants.CS_PORTFOLIO_UPDATE, portfolioUpdate, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
			// ==================================//

			Long portfolioNo = portfolioUpdate.getPortfolio().getPortfolioKey();
			result.put("status", "success");
			result.put("portfolioNo", portfolioNo);
			result.put("message", "Please run CS4015 Reprocess Portfolio");
		} catch (MedallionException ex) {
			result.put("status", "error");
			Long portfolioNo = portfolioUpdate.getPortfolio().getPortfolioKey();
			log.debug("[CONFIRM] ex.errorCode = " + ex.getErrorCode());
			result.put("portfolioNo", portfolioNo);
			result.put("error", Messages.get("error." + ex.getErrorCode()));
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_PORTFOLIO_UPDATE));
		renderJSON(result);
	}

	@Check("transaction.portfolioUpdate")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		mode = UIConstants.DISPLAY_MODE_VIEW;
		CsPortfolioUpdate portfolioUpdate = serializerService.deserialize(session.getId(), id, CsPortfolioUpdate.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_PORTFOLIO_UPDATE));
		render("PortfolioUpdates/entry.html", portfolioUpdate, mode);
	}

	public static void approval(String taskId, Long keyId, CsPortfolioUpdate portfolioUpdate, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " portfolioUpdate: " + portfolioUpdate + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		portfolioUpdate = accountService.getCsPortfolioUpdate(keyId);
		CsPortfolio portfolio = accountService.getPortfolioForUpdate(portfolioUpdate.getPortfolio().getPortfolioKey());
		CsPortfolioAmortization portfolioAmortization = accountService.getCsPortfolioAmortizationByPortfolio(portfolio.getPortfolioKey(), portfolioUpdate.getEffectiveDate());

		// set abstract attribute ==============================
		portfolioUpdate.setAccount(portfolio.getAccount());
		portfolioUpdate.setSecurity(portfolio.getSecurity());
		portfolioUpdate.setSecurityType(portfolio.getSecurity().getSecurityType());
		portfolioUpdate.setHoldingQuantity(portfolio.getPortfolioQuantity());

		int base = 0;
		if (portfolioUpdate.getSecurity().getAccrualBase() != null) {
			if (portfolioUpdate.getSecurity().getAccrualBase().getLookupCode().equals("30")) {
				base = 30;
			} else if (portfolioUpdate.getSecurity().getAccrualBase().getLookupCode().equals("31")) {
				base = 31;
			} else {
				base = 0;
			}
		}

		if (AbstractPortfolio.TYPE_INTEREST_FOR_PORTFOLIO_UPDATE.equals(portfolioUpdate.getPortfolioUpdateType().getLookupId())) {
			if ((portfolioUpdate.getNextPaymentDate() != null) && (portfolioUpdate.getLastPaymentDate() != null)) {
				int totalAccruedDays = TransactionHelper.calculateAccruedDays(portfolioUpdate.getNextPaymentDate(), portfolioUpdate.getLastPaymentDate(), base);
				portfolioUpdate.setTotalAccruedDays(totalAccruedDays);
			}
		}

		if (AbstractPortfolio.TYPE_AMORTIZE_FOR_PORTFOLIO_UPDATE.equals(portfolioUpdate.getPortfolioUpdateType().getLookupId())) {
			if (portfolioAmortization != null) {
				portfolioUpdate.setPortfolioAmortization(portfolioAmortization);
			} else {
				portfolioUpdate.setPortfolioAmortization(null);
			}
			BigDecimal fairValue = portfolioUpdate.getPortfolioAmortization().getFairValue();
			BigDecimal unamortizedAmount = portfolioUpdate.getPortfolioAmortization().getUnamortAmount();
			// BigDecimal holdingQuantity =
			// portfolioUpdate.getHoldingQuantity();
			// BigDecimal price = portfolioAmortization.getPrice();
			portfolioUpdate.setParValue(fairValue.add(unamortizedAmount));
			portfolioUpdate.setDiscountValue(portfolioUpdate.getParValue().subtract(portfolioUpdate.getPurchaseValue()));
			// portfolioUpdate.setNewYield(newYield);
		}
		// ====================================================

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_PORTFOLIO_UPDATE));
		render("PortfolioUpdates/approval.html", portfolioUpdate, mode, taskId, from);
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		try {
			accountService.approveCsPortfolioUpdate(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId);

		try {
			accountService.rejectCsPortfolioUpdate(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.portfolioUpdate")
	public static void calculateAccruedDay(Date startDate, Date endDate, int base) {
		log.debug("calculateAccruedDay. startDate: " + startDate + " endDate: " + endDate + " base: " + base);

		int days = TransactionHelper.calculateAccruedDays(startDate, endDate, base);
		renderText(days);
	}

	@Check("transaction.portfolioUpdate")
	public static void getDailyInterestAmount(Long accountKey, Long securityKey, String classification, String holdingRefs, Date effectiveDate) {
		log.debug("getDailyInterestAmount. accountKey: " + accountKey + " securityKey: " + securityKey + " classification: " + classification + " holdingRefs: " + holdingRefs + " effectiveDate: " + effectiveDate);

		BigDecimal dailyInterestAmount;
		if ((accountKey != null) && (securityKey != null) && (classification != null) && (holdingRefs != null) && (effectiveDate != null)) {
			CsDailyInterest dailyInterest = accountService.getDailyInterest(accountKey, securityKey, classification, holdingRefs, effectiveDate);
			if (dailyInterest != null) {
				if (dailyInterest.getDailyInterest() != null) {
					dailyInterestAmount = dailyInterest.getDailyInterest();
				} else {
					dailyInterestAmount = null;
				}
			} else {
				dailyInterestAmount = null;
			}
		} else {
			dailyInterestAmount = null;
		}

		renderText(dailyInterestAmount);
	}
}