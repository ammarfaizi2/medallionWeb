package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import vo.RegistrySwitchingSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.AbstractRgSwitching;
import com.simian.medallion.model.AbstractRgTrade;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnTaxMaster;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.GnThirdPartyBank;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgFeeTier;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgPortfolio;
import com.simian.medallion.model.RgProdEod;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgSwitching;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.RgTransaction;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import extensions.StatusExtension;

public class RegistrySwitch extends Registry {
	private static Logger log = Logger.getLogger(RegistrySwitch.class);

	private static final String FROM_UNIT_REGISTRY = "unitRegistry";
	public static final String type = AbstractRgTrade.TRADETYPE_REDEEM_SWITCHING;
	private static final String FROM_CANCEL_SWITCHING = "cancelSwitching";

	public static final String SPECIAL_CHAR = ",,";

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "edit", "view", "entry", "save", "confirming", "confirm", "back", "approval", "cancel" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);

		renderArgs.put("redemptionOperators", UIHelper.redeemSwitchOperators());
		renderArgs.put("subscriptionSwitchOperators", UIHelper.subscriptionSwitchOperators());

		renderArgs.put("switchingType", generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._SWITCHING_TYPE));

		List<SelectItem> outTradeBy = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TRANSACTION_BY);
		renderArgs.put("outTradeBy", outTradeBy);

		List<SelectItem> inTradeBy = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._INPUT_BY);
		renderArgs.put("inTradeBy", inTradeBy);

		renderArgs.put("domainBankSub", LookupConstants._DOMAIN_BANK_SUB);
		renderArgs.put("domainBankRed", LookupConstants._DOMAIN_BANK_RED);

		renderArgs.put("specialChar", SPECIAL_CHAR);
	}

	@Check("transaction.registrySwitch")
	public static void list() {
		log.debug("list. ");

		RegistrySwitchingSearchParameters param = new RegistrySwitchingSearchParameters();
		render("RegistrySwitch/list.html", param);
	}

	public static void pagingSwitching(Paging page, RegistrySwitchingSearchParameters param) {
		log.debug("pagingSwitching. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		if (param.isQuery()) {
			// page.addParams("rs.cancelled", page.EQUAL, 0);
			page.addParams("rs.switchDate", page.GREATEQUAL, param.getSwitchDateFrom());
			page.addParams("rs.switchDate", page.LESSEQUAL, param.getSwitchDateTo());
			page.addParams("rs.tradeStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_REJECTED);
			page.addParams("rs.recordStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_NEED_REVISION);

			page.addParams("rs.switchingKey", param.getSwitchNoOperator(), UIHelper.withOperator(param.getSwitchingKey(), param.getSwitchNoOperator()));
			page.addParams(Helper.searchAll("(rs.switchingKey||a.rgProduct.productCode||" + "b.rgProduct.productCode||to_char(rs.switchDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')" + "||custOut.customerNo||custIn.customerNo||" + "custOut.customerName||custIn.customerName)"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			/*
			 * if(param.getInvestorKey() != null) { page.addParams(
			 * "rs.rgInvestmentaccountByOutAccountNumber.customer.customerKey",
			 * page.EQUAL, param.getInvestorKey()); }
			 */

			page.getParamFixMap().put("investorKey", param.getInvestorKey());
			page.getParamFixMap().put("investorNo", param.getInvestorNo());
			page.getParamFixMap().put("cancelled", 0);

			page = taService.pagingRegistrySwitch(page);
		}
		renderJSON(page);
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		RgSwitching swt = taService.loadSwitching(id);
		swt.splitRgTrade();

		if (swt.getRgInvestmentaccountByOutAccountNumber() != null) {
			if (swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct() != null) {
				swt.getOut().setTaxPct(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getTaxMasterByRedTaxKey().getTaxRate());
				swt.setOutThirdPartyByFundManager(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getThirdPartyByFundManager());
				swt.setNavDate(swt.getOut().getNavDate());
				swt.setPostDate(swt.getOut().getPostDate());
				swt.setGoodfundDate(swt.getOut().getGoodfundDate());
			}

			CfMaster customer = customerService.getCustomer(swt.getRgInvestmentaccountByOutAccountNumber().getCustomer().getCustomerKey());
			swt.setInvestor(customer);

			swt.setExternalReference(swt.getOut().getExternalReference());
		}

		if (swt.getRgInvestmentaccountByInAccountNumber() != null) {
			if (swt.getRgInvestmentaccountByInAccountNumber().getRgProduct() != null) {
				swt.getIn().setTaxPct(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
				swt.setInThirdPartyByFundManager(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getThirdPartyByFundManager());
				swt.setNavDate(swt.getIn().getNavDate());
				swt.setPostDate(swt.getIn().getPostDate());
				swt.setPaymentDate(swt.getIn().getPaymentDate());
				swt.setGoodfundDate(swt.getIn().getGoodfundDate());
			}

			CfMaster customer = customerService.getCustomer(swt.getRgInvestmentaccountByInAccountNumber().getCustomer().getCustomerKey());
			swt.setInvestor(customer);

			swt.setExternalReference(swt.getIn().getExternalReference());
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

		if (swt.getOut() != null && swt.getOut().getFeePct() != null)
			swt.getOut().setFeePercent(true);
		if (swt.getIn() != null && swt.getIn().getFeePct() != null)
			swt.getIn().setFeePercent(true);

		// SWITCH IN
		if (swt.getIn() != null) {
			if (swt.getIn().getFeePct() == null) {
				if ((swt.getIn().getFeeAmt() != null) && (swt.getIn().getNetAmount() != null)) {
					// Double feePct = (swt.getIn().getFeeAmt().doubleValue() /
					// swt.getIn().getNetAmount().doubleValue()) * 100;
					Double feePct = new Double(0);
					if (swt.getIn().getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
						feePct = new Double(0);
					} else {
						feePct = (swt.getIn().getFeeAmt().doubleValue() / swt.getIn().getNetAmount().doubleValue()) * 100;
					}
					BigDecimal feePct1 = new BigDecimal(feePct);
					swt.getIn().setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
				}
			}
		}

		if (swt.getOut() != null && swt.getOut().getPaymentDate() != null)
			swt.setPaymentDate(swt.getOut().getPaymentDate());

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SWITCHING));
		render("RegistrySwitch/entry.html", swt, mode);
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgSwitching swt = taService.loadSwitching(id);
		swt.splitRgTrade();
		swt.setThirdPartyByFundManager(swt.getOut().getRgProduct().getThirdPartyByFundManager());
		swt.setSaCode(swt.getRgInvestmentaccountByOutAccountNumber().getThirdPartyBySaCode().getThirdPartyCode());
		swt.setGoodfundDate(swt.getOut().getGoodfundDate());
		swt.setNavDate(swt.getOut().getNavDate());
		swt.setPostDate(swt.getOut().getPostDate());
		swt.setSwitchAll(swt.getOut().getLiquidation());
		swt.getOut().setTaxPct(swt.getOut().getRgProduct().getTaxMasterByRedTaxKey().getTaxRate());
		swt.getIn().setTaxPct(swt.getIn().getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		swt.getIn().setTrnSABranchKey(swt.getOut().getTrnSABranchKey());
		swt.getOut().setTrnSABranch(generalService.getThirdParty(swt.getOut().getTrnSABranchKey()));
		swt.getIn().setTrnSABranch(generalService.getThirdParty(swt.getIn().getTrnSABranchKey()));
		if (swt.getOut().getFeePct() != null)
			swt.getOut().setFeePercent(true);
		if (swt.getIn().getFeePct() != null)
			swt.getIn().setFeePercent(true);

		// SWITCH IN
		if (swt.getIn().getFeePct() == null) {
			if ((swt.getIn().getFeeAmt() != null) && (swt.getIn().getNetAmount() != null)) {
				// Double feePct = (swt.getIn().getFeeAmt().doubleValue() /
				// swt.getIn().getNetAmount().doubleValue()) * 100;
				Double feePct = new Double(0);
				if (swt.getIn().getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
					feePct = new Double(0);
				} else {
					feePct = (swt.getIn().getFeeAmt().doubleValue() / swt.getIn().getNetAmount().doubleValue()) * 100;
				}
				BigDecimal feePct1 = new BigDecimal(feePct);
				swt.getIn().setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SWITCHING));
		render("RegistrySwitch/entry.html", swt, mode);
	}

	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;

		RgSwitching swt = new RgSwitching();
		swt.setOut(new RgTrade());
		swt.setIn(new RgTrade());
		swt.setSwitchDate(taService.getCurrentBusinessDate());
		swt.setGoodfundDate(swt.getSwitchDate());

		GnLookup lookupAmount = generalService.getLookup(LookupConstants.TRANSACTION_BY_UNIT);
		swt.getOut().setTradeBy(lookupAmount);

		GnTaxMaster taxMaster = generalService.getTaxMaster("NOT");
		swt.getOut().setFeePct(BigDecimal.ZERO);
		swt.getOut().setFeePercent(true);
		swt.getOut().setTaxMaster(taxMaster);

		swt.getIn().setFirstSubscribe(Boolean.FALSE);
		swt.getIn().setFeePct(BigDecimal.ZERO);
		swt.getIn().setFeePercent(true);
		swt.getIn().setOtherAmt(BigDecimal.ZERO);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SWITCHING));
		render("RegistrySwitch/entry.html", swt, mode);
	}

	public static void save(RgSwitching swt, String mode, String from, String outFeeBy, String subscriptionFee) {
		log.debug("save. swt: " + swt + " mode: " + mode + " from: " + from + " outFeeBy: " + outFeeBy + " subscriptionFee: " + subscriptionFee);

		if (swt == null)
			entry();

		// SWITCH OUT
		if (swt.getOut().getFeePct() == null) {
			if (swt.getOut() != null && swt.getOut().getFeePct() != null)
				swt.getOut().setFeePercent(true);
			if ((swt.getOut().getNetAmount() != null) && (swt.getOut().getFeeAmt() != null)) {
				Double feePct = new Double(0);
				if (swt.getOut().getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
					feePct = new Double(0);
				} else {
					feePct = (swt.getOut().getFeeAmt().doubleValue() / swt.getOut().getNetAmount().doubleValue()) * 100;
				}
				// Double feePct = (swt.getOut().getFeeAmt().doubleValue() /
				// swt.getOut().getNetAmount().doubleValue()) * 100;
				BigDecimal feePct1 = new BigDecimal(feePct);
				// pake 4 digit dibelakang koma karena di screen untuk field
				// redemption fee percent hanya
				// mengijinkan 4 digit dibelakang koma
				swt.getOut().setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
			}
		}

		// SWITCHI IN
		if (swt.getIn().getFeePct() == null) {
			if (swt.getIn() != null && swt.getIn().getFeePct() != null)
				swt.getIn().setFeePercent(true);
			if ((swt.getIn().getFeeAmt() != null) && (swt.getIn().getNetAmount() != null)) {
				Double feePct = new Double(0);
				if (swt.getIn().getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
					feePct = new Double(0);
				} else {
					feePct = (swt.getIn().getFeeAmt().doubleValue() / swt.getIn().getNetAmount().doubleValue()) * 100;
				}
				// Double feePct = (swt.getIn().getFeeAmt().doubleValue() /
				// swt.getIn().getNetAmount().doubleValue()) * 100;
				BigDecimal feePct1 = new BigDecimal(feePct);
				swt.getIn().setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
			}
		}

		if (from == null) {
			RgProduct outProduct = taService.loadProduct(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getProductCode());
			RgProduct inProduct = taService.loadProduct(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getProductCode());

			// ***START HEADER***//
			validation.required("Investor No is", swt.getInvestor());
			validation.required("Switching Type is", swt.getSwitchingType());

			// ***START SWITCH OUT-IN***//
			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUTIN)) {
				validation.required("Fund Code (Switch Out) is", swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getProductCode());

				validation.required("Account No (Switch Out) is", swt.getRgInvestmentaccountByOutAccountNumber().getAccountNumber());

				validation.required("Fund Code (Switch In) is", swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getProductCode());

				validation.required("Payment Date is", swt.getPaymentDate());

				if ((inProduct != null) && (outProduct != null)) {
					if (inProduct.getProductCode().equals(outProduct.getProductCode())) {
						validation.addError(null, "Fund Code (Switch In) should not be the same with Fund Code (Switch Out)");
					}

					if (!inProduct.getThirdPartyByFundManager().getThirdPartyKey().equals(outProduct.getThirdPartyByFundManager().getThirdPartyKey())) {
						validation.addError(null, "Fund Manager (Switch In) must be the same with Fund Manager (Switch Out)");
					}
				}

				validation.required("Account No (Switch In) is", swt.getRgInvestmentaccountByInAccountNumber().getAccountNumber());

				if ((swt.getRgInvestmentaccountByOutAccountNumber().getAccountNumber() != null) && (swt.getRgInvestmentaccountByInAccountNumber().getAccountNumber() != null)) {
					if (swt.getRgInvestmentaccountByInAccountNumber().getAccountNumber().equals(swt.getRgInvestmentaccountByOutAccountNumber().getAccountNumber())) {
						validation.addError(null, "Account No (Switch In) should not be the same with Account (Switch Out)");
					}
				}

				if ((swt.getRgInvestmentaccountByOutAccountNumber() != null) && (swt.getRgInvestmentaccountByInAccountNumber() != null)) {
					if (swt.getRgInvestmentaccountByOutAccountNumber().getThirdPartyBySaCode().getThirdPartyKey() != null || swt.getRgInvestmentaccountByInAccountNumber().getThirdPartyBySaCode().getThirdPartyKey() != null) {
						if (!swt.getRgInvestmentaccountByOutAccountNumber().getThirdPartyBySaCode().getThirdPartyKey().equals(swt.getRgInvestmentaccountByInAccountNumber().getThirdPartyBySaCode().getThirdPartyKey())) {
							validation.addError(null, "Selling Agent (Switch In) must be same with Selling Agent (Switch Out)");
						}
					}
				}
			}
			// ***START SWITCH OUT-IN***//

			// ***START SWITCH OUT***//
			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUT)) {
				validation.required("Fund Code (Switch Out) is", swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getProductCode());
				validation.required("Account No (Switch Out) is", swt.getRgInvestmentaccountByOutAccountNumber().getAccountNumber());

				validation.required("Fund Code (Switch In) is", swt.getExternalProduct().getThirdPartyCode());

				validation.required("Payment Date is", swt.getPaymentDate());

				if (outProduct != null) {
					if (swt.getExternalProduct().getFundManager() != null) {
						if (swt.getExternalProduct().getFundManager().getThirdPartyKey() != null) {
							if (!swt.getExternalProduct().getFundManager().getThirdPartyKey().equals(outProduct.getThirdPartyByFundManager().getThirdPartyKey())) {
								validation.addError(null, "Fund Manager (Switch In) must be the same with Fund Manager (Switch Out)");
							}
						}
					}
					Date defaultRedPostingDate = generalService.getWorkingDate(swt.getSwitchDate(), outProduct.getRedPostPeriod());
					if (swt.getPostDate().compareTo(defaultRedPostingDate) > 0) {
						validation.addError("", "redemption.postdate_greater_than_default");
					}
				}
			}
			// ***END SWITCH OUT***//

			// ***START SWITCH IN***//
			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_IN)) {
				validation.required("Fund Code (Switch Out) is", swt.getExternalProduct().getThirdPartyCode());

				validation.required("Fund Code (Switch In) is", swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getProductCode());
				validation.required("Account No (Switch In) is", swt.getRgInvestmentaccountByInAccountNumber().getAccountNumber());
				validation.required("Payment Date is", swt.getPaymentDate());

				if (inProduct != null) {
					if (swt.getExternalProduct().getFundManager() != null) {
						if (swt.getExternalProduct().getFundManager().getThirdPartyKey() != null) {
							if (!swt.getExternalProduct().getFundManager().getThirdPartyKey().equals(inProduct.getThirdPartyByFundManager().getThirdPartyKey())) {
								validation.addError(null, "Fund Manager (Switch In) must be the same with Fund Manager (Switch Out)");
							}
						}
					}
					Date defaultSubPostingDate = generalService.getWorkingDate(swt.getSwitchDate(), inProduct.getSubPostPeriod());
					if (swt.getPostDate().compareTo(defaultSubPostingDate) > 0) {
						validation.addError("", "subscription.postdate_greater_than_default");
					}

					Date defaultMaxPaymentDate = generalService.getWorkingDate(swt.getSwitchDate(), inProduct.getMaxPaymentDate());
					if (swt.getPaymentDate() != null && swt.getPaymentDate().compareTo(defaultMaxPaymentDate) > 0) {
						validation.addError("", "redemption.paymentdate_greater_than_maxpaydate");
					}
				}
			}
			// ***END SWITCH IN***//

			// ***START VALIDATION-DATE***//
			Date applicationDate = taService.getCurrentBusinessDate();

			validation.required("Transaction Date is", swt.getSwitchDate());
			validation.required("NAV Date is", swt.getNavDate());
			validation.required("Posting Date is", swt.getPostDate());

			if (fmtYYYYMMDD(swt.getSwitchDate()).compareTo(fmtYYYYMMDD(applicationDate)) > 0) {
				validation.addError("global", "switching.tradedate_greater_than_applicationDate");
			}

			if (fmtYYYYMMDD(swt.getNavDate()).compareTo(fmtYYYYMMDD(swt.getGoodfundDate())) > 0) {
				validation.addError("global", "switching.navdate_greater_than_goodFundDate");
			}

			if (swt.getPostDate() != null && swt.getOut().getRgProduct() != null) {
				RgProdEod rgProdEod = taService.getProdEodByProductCode(swt.getOut().getRgProduct().getProductCode());
				if (rgProdEod != null) {
					if (rgProdEod.getEodDate() != null) {
						if (swt.getPostDate().compareTo(rgProdEod.getEodDate()) <= 0) {
							validation.addError(null, "switching.postdate_greater_than_eod");
						}
					}
				}
			}

			if (fmtYYYYMMDD(swt.getPostDate()).compareTo(fmtYYYYMMDD(swt.getGoodfundDate())) < 0) {
				validation.addError(null, "switching.postdate_less_than_goodFundDate");
			}

			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUTIN) || swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUT))
				if (swt != null && outProduct != null) {
					if (outProduct != null && swt.getSwitchDate() != null && outProduct.getMaxPaymentDate() != null) {
						Date maxPayDate = generalService.getWorkingDate(swt.getSwitchDate(), outProduct.getMaxPaymentDate());
						if (swt.getPaymentDate() != null && fmtYYYYMMDD(swt.getPaymentDate()).compareTo(fmtYYYYMMDD(maxPayDate)) > 0) {
							validation.addError("", "redemption.paymentdate_greater_than_maxpaydate");
						}
					}
				}
			if (swt.getPostDate() != null && swt.getPaymentDate() != null) {
				if (swt.getPaymentDate().compareTo(swt.getPostDate()) < 0) {
					validation.addError("", "redemption.paymentdate_less_than_postdate");
				}
			}
			// ***END VALIDATION-DATE***//
			// ***END HEADER***//

			// ***START TAB-DETAIL***//
			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUTIN)) {
				if (LookupConstants.RG_REDEMPTION_TRANSACTION_BY_UNIT.equals(swt.getOut().getTradeBy().getLookupId())) {
					validation.required("Unit in tab Detail (Switch Out) is", swt.getOut().getUnit());
				}

				if (swt.getOut().getUnit() != null) {
					if (swt.getOut().getUnit().equals(BigDecimal.ZERO)) {
						validation.addError(null, "switching.out.unit_zero");
					}

					if (swt.getOut().getUnit().compareTo(BigDecimal.ZERO) < 0) {
						validation.addError(null, "switching.out.unit_greate_than_zero");
					}
				}

				if (LookupConstants.RG_REDEMPTION_TRANSACTION_BY_AMOUNT.equals(swt.getOut().getTradeBy().getLookupId())) {
					validation.required("Amount in tab Detail (Switch Out) is", swt.getOut().getNetAmount());
				}

				if (swt.getOut().getFeePct() == null) {
					validation.required("Switching Fee in tab Detail (Switch Out) is", swt.getOut().getFeeAmt());
				} else if (swt.getOut().getFeeAmt() == null) {
					validation.required("Switching Fee in tab Detail (Switch Out) is", swt.getOut().getFeePct());
				}

				if (swt.getOut().getDiscAmt() == null) {
					validation.required("Discount in tab Detail (Switch Out) is", swt.getOut().getDiscAmt());
				}

				if (!swt.getOut().getLiquidation()) {
					BigDecimal tmpUnit = BigDecimal.ZERO;
					if (swt.getOut().getUnit() != null) {
						tmpUnit = swt.getOut().getUnit();
					}

					BigDecimal tmpAvailableUnit = BigDecimal.ZERO;
					if (swt.getOut().getAvailabelUnit() != null) {
						tmpAvailableUnit = swt.getOut().getAvailabelUnit();
					}

					if ((outProduct != null) && (outProduct.getMinBalUnit() != null)) {
						if (tmpAvailableUnit.subtract(tmpUnit).compareTo(outProduct.getMinBalUnit()) < 0) {
							validation.addError(null, "switching.redemption.available_unit_lower_than_min_bal_unit");
						}
					}
				}

				if (outProduct != null) {
					if (outProduct.getRedMaxAmt() != null) {
						BigDecimal redMaxAmt = outProduct.getRedMaxAmt();
						if (swt.getOut().getFeeAmt() != null && swt.getOut().getFeeAmt().compareTo(redMaxAmt) > 0) {
							validation.addError(null, "switching.redemption.fee_amount_greater_max_fee");
						}
					}

					if ((outProduct != null) && (swt.getOut().getFeePct() != null)) {
						if (outProduct.getRgFeeTiers().size() > 0) {
							Set<RgFeeTier> rgFeeTiers = outProduct.getRgFeeTiers();
							for (RgFeeTier feeTier : rgFeeTiers) {
								if (feeTier.getId().getType().equalsIgnoreCase(AbstractRgSwitching.TYPE_SWITCHING)) {
									if (feeTier.getMaxValue() != null) {
										if (swt.getOut().getFeePct().compareTo(feeTier.getMaxValue()) > 0) {
											validation.addError(null, "switching.redemption.fee_pct_greater_max_fee_pct");
										}
									}
								}
							}
						}
					}
				}

				if (LookupConstants.RG_SUBSCRIPTION_INPUT_BY_GROSS.equals(swt.getIn().getTradeBy().getLookupId())) {
					// validation.required("Gross Amount in tab Detail (Switch In) is",
					// swt.getIn().getAmount());
				}

				if (LookupConstants.RG_SUBSCRIPTION_INPUT_BY_NET.equals(swt.getIn().getTradeBy().getLookupId())) {
					validation.required("Net Amount in tab Detail (Switch In) is", swt.getIn().getNetAmount());
				}

				if (swt.getIn().getFeePct() == null) {
					validation.required("Switching Fee in tab Detail (Switch In) is", swt.getIn().getFeeAmt());
				} else if (swt.getIn().getFeeAmt() == null) {
					validation.required("Switching Fee in tab Detail (Switch In) is", swt.getIn().getFeePct());
				}

				validation.required("Other Fee Amount in tab Detail (Switch In) is", swt.getIn().getOtherAmt());

				if (swt.getIn().getNetAmount() != null) {
					if (swt.getIn().getFirstSubscribe()) {
						if (inProduct != null) {
							if (swt.getIn().getNetAmount().doubleValue() < inProduct.getSubInitMinAmt().doubleValue()) {
								validation.addError("", "switching.subscription.newAmount_less_than_subscribeMinimumAmount");
							}
						}
					} else {
						if (inProduct != null) {
							//#3937 -- start
							if (!swt.getOut().getLiquidation()) {
							//#3937 -- end
								if (swt.getIn().getNetAmount().doubleValue() < inProduct.getSubMinAmt().doubleValue()) {
									validation.addError("", "switching.subscription.amount_less_than_topUpMinimumAmount");
								}
							//#3937 -- start
							}
							//#3937 -- end
						}
					}

					if (inProduct != null) {
						if (inProduct.getSubMaxAmt() != null) {
							if (swt.getIn().getNetAmount().doubleValue() > inProduct.getSubMaxAmt().doubleValue()) {
								validation.addError("", "switching.subscription.newAmount_greater_than_subscribeMaximumAmount");
							}
						}
					}
				}

				// kata mr su tidak usah validasi untuk sisi switch in
				/*
				 * if((inProduct != null) && (swt.getIn().getFeePct() != null))
				 * { if(inProduct.getRgFeeTiers().size() > 0) { Set<RgFeeTier>
				 * rgFeeTiers = inProduct.getRgFeeTiers(); for(RgFeeTier feeTier
				 * : rgFeeTiers) {
				 * if(feeTier.getId().getType().equalsIgnoreCase(
				 * AbstractRgSwitching.TYPE_SWITCHING)) {
				 * if(feeTier.getMaxValue() != null) {
				 * if(swt.getIn().getFeePct().compareTo(feeTier.getMaxValue()) >
				 * 0) { validation.addError("",
				 * "switching.subscription.subscriptionFeePercent_greater_than_maxSubscriptionFeePercent"
				 * ); } } } } } }
				 */

				// if(inProduct.getSubLock() != null)
				// {
				// if(inProduct.getSubLock() == true)
				// {
				// List<RgProductLockException> lockExceptionDates =
				// taService.listRgProductLockExceptionByType(inProduct.getProductCode(),
				// UIConstants.TRADE_TYPE_SUBSCRIBE);
				// logger.debug("[save] size lockExceptionDates = " +
				// lockExceptionDates.size());
				// boolean checkSame = true;
				// for(RgProductLockException le : lockExceptionDates)
				// {
				// logger.debug("[save] sub.goodfundDate = " +
				// swt.getGoodfundDate() + "same with exceptionDate = " +
				// le.getId().getExceptionDate());
				// if(swt.getGoodfundDate().getTime() ==
				// le.getId().getExceptionDate().getTime())
				// {
				// checkSame = false;
				// break;
				// }
				// }

				// if(checkSame)
				// {
				// validation.addError("","switching.subscription.not_allowed_same_with_exceptionDate");
				// }
				// }
				// }
			}

			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUT)) {
				if (swt.getOut().getAvailabelUnit() == null) {
					validation.addError(null, "switching.redemption.availableUnit_empty");
				}

				if (LookupConstants.RG_REDEMPTION_TRANSACTION_BY_UNIT.equals(swt.getOut().getTradeBy().getLookupId())) {
					validation.required("Unit in tab Detail (Switch Out) is", swt.getOut().getUnit());
				}

				if (swt.getOut().getUnit() != null) {
					if (swt.getOut().getUnit().equals(BigDecimal.ZERO)) {
						validation.addError(null, "switching.out.unit_zero");
					}

					if (swt.getOut().getUnit().compareTo(BigDecimal.ZERO) < 0) {
						validation.addError(null, "switching.out.unit_greate_than_zero");
					}
				}

				if (LookupConstants.RG_REDEMPTION_TRANSACTION_BY_AMOUNT.equals(swt.getOut().getTradeBy().getLookupId())) {
					validation.required("Amount in tab Detail (Switch Out) is", swt.getOut().getNetAmount());
				}

				if (swt.getOut().getFeePct() == null) {
					validation.required("Switching Fee in tab Detail (Switch Out) is", swt.getOut().getFeeAmt());
				} else if (swt.getOut().getFeeAmt() == null) {
					validation.required("Switching Fee in tab Detail (Switch Out) is", swt.getOut().getFeePct());
				}

				if (swt.getOut().getDiscAmt() == null) {
					validation.required("Discount in tab Detail (Switch Out) is", swt.getOut().getDiscAmt());
				}

				if (!swt.getOut().getLiquidation()) {
					BigDecimal tmpUnit = BigDecimal.ZERO;
					if (swt.getOut().getUnit() != null) {
						tmpUnit = swt.getOut().getUnit();
					}

					BigDecimal tmpAvailableUnit = BigDecimal.ZERO;
					if (swt.getOut().getAvailabelUnit() != null) {
						tmpAvailableUnit = swt.getOut().getAvailabelUnit();
					}

					if ((outProduct != null) && (outProduct.getMinBalUnit() != null)) {
						if (tmpAvailableUnit.subtract(tmpUnit).compareTo(outProduct.getMinBalUnit()) < 0) {
							validation.addError(null, "switching.redemption.available_unit_lower_than_min_bal_unit");
						}
					}
				}

				if (outProduct != null) {
					if (outProduct.getRedMaxAmt() != null) {
						BigDecimal redMaxAmt = outProduct.getRedMaxAmt();
						if (swt.getOut().getFeeAmt().compareTo(redMaxAmt) > 0) {
							validation.addError(null, "switching.redemption.fee_amount_greater_max_fee");
						}
					}

					if ((outProduct != null) && (swt.getOut().getFeePct() != null)) {
						if (outProduct.getRgFeeTiers().size() > 0) {
							Set<RgFeeTier> rgFeeTiers = outProduct.getRgFeeTiers();
							for (RgFeeTier feeTier : rgFeeTiers) {
								if (feeTier.getId().getType().equalsIgnoreCase(AbstractRgSwitching.TYPE_SWITCHING)) {
									if (feeTier.getMaxValue() != null) {
										if (swt.getOut().getFeePct().compareTo(feeTier.getMaxValue()) > 0) {
											validation.addError("", "switching.redemption.fee_pct_greater_max_fee_pct");
										}
									}
								}
							}
						}
					}
				}

				/*
				 * List<RgTrade> trades =
				 * taService.listTradeRedeemSwitching(swt.
				 * getRgInvestmentaccountByOutAccountNumber
				 * ().getAccountNumber(), swt.getGoodfundDate(),
				 * AbstractRgTrade.TRADETYPE_REDEEM); for(RgTrade tradeInTable :
				 * trades) { if(swt.getOut().getLiquidation() == true) { if
				 * ((tradeInTable
				 * .getRgInvestmentaccount().getAccountNumber().equals
				 * (swt.getRgInvestmentaccountByOutAccountNumber
				 * ().getAccountNumber())) &&
				 * (tradeInTable.getGoodfundDate().getTime() ==
				 * swt.getGoodfundDate().getTime()) &&
				 * (tradeInTable.getLiquidation() ==
				 * swt.getOut().getLiquidation())) { validation.addError(null,
				 * "Can't redeem all for good fund date :'" +
				 * FastDateFormat.getInstance
				 * ("MM/dd/yyyy").format(swt.getGoodfundDate
				 * ())+"' with account no : '" +
				 * swt.getRgInvestmentaccountByOutAccountNumber
				 * ().getAccountNumber()); } } }
				 */
			}

			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_IN)) {
				if (LookupConstants.RG_SUBSCRIPTION_INPUT_BY_GROSS.equals(swt.getIn().getTradeBy().getLookupId())) {
					validation.required("Gross Amount in tab Detail (Switch In) is", swt.getIn().getAmount());
				}

				if (LookupConstants.RG_SUBSCRIPTION_INPUT_BY_NET.equals(swt.getIn().getTradeBy().getLookupId())) {
					validation.required("Net Amount in tab Detail (Switch In) is", swt.getIn().getNetAmount());
				}

				if (swt.getIn().getFeePct() == null) {
					validation.required("Switching Fee in tab Detail (Switch In) is", swt.getIn().getFeeAmt());
				} else if (swt.getIn().getFeeAmt() == null) {
					validation.required("Switching Fee in tab Detail (Switch In) is", swt.getIn().getFeePct());
				}

				validation.required("Other Fee Amount in tab Detail (Switch In) is", swt.getIn().getOtherAmt());

				if (swt.getIn().getNetAmount() != null) {
					if (swt.getIn().getFirstSubscribe()) {
						if (swt.getIn().getNetAmount().doubleValue() < inProduct.getSubInitMinAmt().doubleValue()) {
							validation.addError("", "switching.subscription.newAmount_less_than_subscribeMinimumAmount");
						}
					} else {
						if (swt.getIn().getNetAmount().doubleValue() < inProduct.getSubMinAmt().doubleValue()) {
							validation.addError("", "switching.subscription.amount_less_than_topUpMinimumAmount");
						}
					}

					if (inProduct != null) {
						if (inProduct.getSubMaxAmt() != null) {
							if (swt.getIn().getNetAmount().doubleValue() > inProduct.getSubMaxAmt().doubleValue()) {
								validation.addError("", "switching.subscription.newAmount_greater_than_subscribeMaximumAmount");
							}
						}
					}
				}

				// kata mr su tidak usah validasi untuk sisi switch in
				/*
				 * if((inProduct != null) && (swt.getIn().getFeePct() != null))
				 * { if(inProduct.getRgFeeTiers().size() > 0) { Set<RgFeeTier>
				 * rgFeeTiers = inProduct.getRgFeeTiers(); for(RgFeeTier feeTier
				 * : rgFeeTiers) {
				 * if(feeTier.getId().getType().equalsIgnoreCase(
				 * AbstractRgSwitching.TYPE_SWITCHING)) {
				 * if(feeTier.getMaxValue() != null) {
				 * if(swt.getIn().getFeePct().compareTo(feeTier.getMaxValue()) >
				 * 0) { validation.addError("",
				 * "switching.subscription.subscriptionFeePercent_greater_than_maxSubscriptionFeePercent"
				 * ); } } } } } }
				 */
			}

			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUTIN) || swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUT)) {
				if (!swt.getOut().getLiquidation()) {
					if (swt.getOut().getNetAmount() != null) {
						if (swt.getOut().getNetAmount().doubleValue() < outProduct.getSwiMinAmt().doubleValue()) {
							validation.addError("", "switching.netAmount_less_than_switchingMinimumAmount");
						}

						if (outProduct.getSwiMaxAmt() != null) {
							if (swt.getOut().getNetAmount().doubleValue() > outProduct.getSwiMaxAmt().doubleValue()) {
								validation.addError("", "switching.netAmount_greater_than_switchingMaximumAmount");
							}
						}
					}
				}
				BigDecimal outstandingUnit;
				try {
					outstandingUnit = taService.loadOutstanding(outProduct.getProductCode(), swt.getRgInvestmentaccountByOutAccountNumber().getAccountNumber(), swt.getPostDate());
				} catch (Exception e) {
					log.error("error outstanding null product kode n posting : more ..", e);
					outstandingUnit = null;
				}

				if (swt.getOut().getUnit() != null && outstandingUnit != null) {
					RgPortfolio portfolio = taService.loadLastPortfolio(swt.getRgInvestmentaccountByOutAccountNumber().getAccountNumber(), swt.getPostDate());
					if (portfolio != null)
						outstandingUnit = portfolio.getUnit().subtract(outstandingUnit);
					if (swt.getOut().getUnit().doubleValue() > outstandingUnit.doubleValue()) {
						validation.addError("", "switching.unit_greater_than_availableUnit");
					}
					/*
					 * int res =
					 * swt.getOut().getUnit().compareTo(outstandingUnit); if(
					 * res == 1 ) validation.addError("",
					 * "switching.unit_greater_than_availableUnit");
					 */
				}

			}

			// ***END TAB-DETAIL***//

			// ***START TAB-BANK_INFO***//
			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUTIN)) {
				validation.required("Account No in tab Bank Info (Switch Out) is", swt.getOut().getRgProductBnAccount().getBankAccountKey());
				validation.required("Account No in tab Bank Info (Switch In) is", swt.getIn().getRgProductBnAccount().getBankAccountKey());
			}

			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUT)) {
				validation.required("Account No in tab Bank Info (Switch Out) is", swt.getOut().getRgProductBnAccount().getBankAccountKey());
				validation.required("Account No in tab Bank Info (Switch In) is", swt.getExternalProductBank().getAccountNo());
			}

			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_IN)) {
				validation.required("Account No in tab Bank Info (Switch Out) is", swt.getExternalProductBank().getAccountNo());
				validation.required("Account No in tab Bank Info (Switch In) is", swt.getIn().getRgProductBnAccount().getBankAccountKey());
			}
			// ***END TAB-BANK_INFO***//
		}
		
		//#4384  Validasi transaksi RG yang sama diinput/diupload berulang kali
		//khusus untuk switching, switch out dan in dianggap dua transaksi yg sama. karena itu tidak dibedakan out ataupun in
		if (swt.getExternalReference() != null) {
			Boolean isTradeExist = taService.isRgTradeAlreadyExist(swt.getSwitchDate(), AbstractRgTrade.TRADETYPE_SWITCHING, swt.getExternalReference(), swt.getSwitchingKey());
			log.debug("isTradeExist >>>> "  + isTradeExist);
			if (isTradeExist) validation.addError("", "trade.data.duplicate");
		}

		// FOR CANCEL //
		if (from != null && from.equals(FROM_CANCEL_SWITCHING)) {
			validation.clear();
		}

		if (validation.hasErrors()) {
			String haserror = "haserror";
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SWITCHING));
			render("RegistrySwitch/entry.html", swt, mode, outFeeBy, subscriptionFee, haserror);
		} else {
			Long id = swt.getSwitchingKey();
			serializerService.serialize(session.getId(), id, swt);
			confirming(id, mode, from, outFeeBy, subscriptionFee);
		}
	}

	public static void confirming(Long id, String mode, String from, String outFeeBy, String subscriptionFee) {
		log.debug("confirming. id: " + id + " mode: " + mode + " from: " + from + " outFeeBy: " + outFeeBy + " subscriptionFee: " + subscriptionFee);

		renderArgs.put("confirming", true);
		RgSwitching swt = serializerService.deserialize(session.getId(), id, RgSwitching.class);
		if (outFeeBy != null && outFeeBy.equals("outFeeBy1"))
			swt.getOut().setFeePercent(true);
		if (outFeeBy != null && outFeeBy.equals("outFeeBy2"))
			swt.getOut().setFeePercent(false);
		String outFeeByForConfirm = outFeeBy;
		String subscriptionFeeForConfirm = subscriptionFee;

		if (from != null && from.equals(FROM_CANCEL_SWITCHING)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
			render("RegistrySwitch/cancel.html", swt, mode, from, outFeeBy, subscriptionFee);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SWITCHING));
			render("RegistrySwitch/entry.html", swt, mode, outFeeBy, subscriptionFee, outFeeByForConfirm, subscriptionFeeForConfirm);
		}
	}

	private static Map<Long, Long> keyMap = new HashMap<Long, Long>();

	public static void confirm(RgSwitching swt, String mode, String from, String outFeeByForConfirm, String subscriptionFeeForConfirm) {
		log.debug("confirm. swt: " + swt + " mode: " + mode + " from: " + from + " outFeeByForConfirm: " + outFeeByForConfirm + " subscriptionFeeForConfirm: " + subscriptionFeeForConfirm);

		if (swt == null)
			back(null, mode, from);
		String outFeeBy = outFeeByForConfirm;
		String subscriptionFee = subscriptionFeeForConfirm;

		try {

			generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			String username = session.get(UIConstants.SESSION_USERNAME);
			String userKey = session.get(UIConstants.SESSION_USER_KEY);
			mode = UIConstants.DISPLAY_MODE_CONFIRM;
			boolean confirming = true;

			// TODO Saat ini blm ada approver jd status langsung approve
			// nanti ke depan statusnya tetap open stlh ada approval
			swt.getOut().setTradeStatus(LookupConstants.__RECORD_STATUS_APPROVED);
			swt.getIn().setTradeStatus(LookupConstants.__RECORD_STATUS_APPROVED);
			swt.getOut().setSaCode(swt.getRgInvestmentaccountByOutAccountNumber().getThirdPartyBySaCode());
			swt.getIn().setSaCode(swt.getRgInvestmentaccountByInAccountNumber().getThirdPartyBySaCode());
			swt.getOut().setTradeDate(swt.getSwitchDate());
			swt.getIn().setTradeDate(swt.getSwitchDate());
			swt.getOut().setGoodfundDate(swt.getGoodfundDate());
			swt.getIn().setGoodfundDate(swt.getGoodfundDate());
			swt.getOut().setNavDate(swt.getNavDate());
			swt.getIn().setNavDate(swt.getNavDate());
			// swt.getOut().setLiquidation(swt.isSwitchAll());
			swt.getIn().setLiquidation(false);
			swt.getOut().setPostDate(swt.getPostDate());
			swt.getIn().setPostDate(swt.getPostDate());
			swt.getIn().setPaymentDate(swt.getPaymentDate());
			swt.getOut().setPaymentDate(swt.getPaymentDate());
			swt.getOut().setRemark(swt.getRemarks());
			swt.getIn().setRemark(swt.getRemarks());
			swt.getOut().setExternalReference(swt.getExternalReference());
			swt.getIn().setExternalReference(swt.getExternalReference());

			// GnThirdParty saCodeThirdParty = new GnThirdParty();
			// saCodeThirdParty.setThirdPartyCode(swt.getSaCode());
			// saCodeThirdParty.setThirdPartyKey(swt.getSaCodeKey());

			swt.getOut().setRgInvestmentaccount(taService.loadInvestment(swt.getRgInvestmentaccountByOutAccountNumber().getAccountNumber()));
			swt.getIn().setRgInvestmentaccount(taService.loadInvestment(swt.getRgInvestmentaccountByInAccountNumber().getAccountNumber()));
			// swt.setRgInvestmentaccountByInAccountNumber(swt.getIn().getRgInvestmentaccount());
			// swt.getRgInvestmentaccountByInAccountNumber().setThirdPartyBySaCode(thirdParty)

			swt.getOut().setRgProduct(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct());
			swt.getIn().setRgProduct(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct());
			// swt.getOut().getRgInvestmentaccount().getRgProduct().setProductCode(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getProductCode());
			// swt.getIn().getRgProduct().setProductCode(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getProductCode());

			// if (swt.isSwitchAll()) {
			if (swt.getOut().getLiquidation()) {
				swt.getOut().setTradeBy(generalService.getLookup(LookupConstants.TRANSACTION_BY_UNIT));

				// RgPortfolio portfolio =
				// taService.loadLastPortfolio(swt.getOut().getRgInvestmentaccount().getAccountNumber(),
				// swt.getGoodfundDate());
				RgPortfolio portfolio = taService.loadLastPortfolio(swt.getOut().getRgInvestmentaccount().getAccountNumber(), swt.getPostDate());
				RgNav nav = taService.loadLatestNav(swt.getOut().getRgProduct().getProductCode());
				BigDecimal outstandingUnit = taService.loadOutstanding(swt.getOut().getRgProduct().getProductCode(), swt.getOut().getRgInvestmentaccount().getAccountNumber(), swt.getPostDate());

				if (portfolio != null)
					portfolio.setUnit(portfolio.getUnit().subtract(outstandingUnit));

				if (portfolio != null) {
					swt.getOut().setAvailabelUnit(portfolio.getUnit());
				}

				if (portfolio != null && nav != null) {
					swt.getOut().setAvailabelBalance(swt.getOut().getAvailabelUnit().multiply(nav.getNav()));
				}

				swt.getOut().setUnit(portfolio.getUnit());

			}

			swt.setSwitchingTypeAbstract(swt.getSwitchingType());
			swt.combineRgTrade();

			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUT)) {
				swt.setRgInvestmentaccountByInAccountNumber(null);

				// swt.getOut().setPrice(null);
				swt.getOut().setTaxAmt(null);

				// menandakan discount by amount, dan kalau discount by amount
				// maka disc. pct tidak perlu disave
				swt.getOut().setDiscAmt(BigDecimal.ZERO);
				if (swt.getOut().getDiscAmt() == null)
					swt.getOut().setDiscPct(BigDecimal.ZERO);

				if (swt.getOut().getTradeBy().getLookupId().equals(LookupConstants.TRANSACTION_BY_UNIT)) {
					swt.getOut().setNetAmount(null);
					swt.getOut().setAmount(null);
					swt.getOut().setPaymentAmount(null);
					if (swt.getOut().isFeePercent())
						swt.getOut().setFeeAmt(null);
					// swt.getOut().setDiscAmt(null);
					// swt.getOut().setOtherAmt(null);
					/*
					 * if(swt.getOut().getFeePct() != null) {
					 * swt.getOut().setFeeAmt(null); }
					 */
				} else {
					swt.getOut().setUnit(null);
				}

				/*
				 * if(outFeeByForConfirm.equalsIgnoreCase("outFeeBy2")) {
				 * swt.getOut().setFeePct(null); }
				 */
				if (!swt.getOut().isFeePercent())
					swt.getOut().setFeePct(null);
			}

			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_IN)) {
				swt.setRgInvestmentaccountByOutAccountNumber(null);

				// swt.getIn().setPrice(null);
				// swt.getIn().setUnit(null);

				/*
				 * if(subscriptionFeeForConfirm.equalsIgnoreCase(
				 * "subscriptionFeeAmt")) { swt.getIn().setFeePct(null); }
				 */
				if (!swt.getIn().isFeePercent()) {
					swt.getIn().setFeePct(null);
				}

				// by defaul yaitu discount by amount dan sekarang diset 0, jadi
				// kalau discount by amount maka disc. pct tidak perlu disave
				swt.getIn().setDiscAmt(BigDecimal.ZERO);
				if (swt.getIn().getDiscAmt() == null)
					swt.getIn().setDiscPct(BigDecimal.ZERO);
			}

			if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_OUTIN)) {
				// swt.getOut().setPrice(null);
				swt.getOut().setTaxAmt(null);

				// menandakan discount by amount, dan kalau discount by amount
				// maka disc. pct tidak perlu disave
				swt.getOut().setDiscAmt(BigDecimal.ZERO);
				if (swt.getOut().getDiscAmt() == null)
					swt.getOut().setDiscPct(BigDecimal.ZERO);

				if (swt.getOut().getTradeBy().getLookupId().equals(LookupConstants.TRANSACTION_BY_UNIT)) {
					swt.getOut().setNetAmount(null);
					swt.getOut().setAmount(null);
					swt.getOut().setPaymentAmount(null);
					if (swt.getOut().isFeePercent())
						swt.getOut().setFeeAmt(null);
					// swt.getOut().setDiscAmt(null);
					// swt.getOut().setOtherAmt(null);

					/*
					 * if(swt.getOut().getFeePct() != null) {
					 * swt.getOut().setFeeAmt(null); }
					 */

					// Jika sisi switch out Unit, fee amount dan amount
					// dikosongkan
					if (swt.getRgTrades() != null) {
						for (RgTrade trade : swt.getRgTrades()) {
							if (trade.getType() != null && trade.getType().equals(RgTrade.TRADETYPE_SUBSCRIBE_SWITCHING)) {
								trade.setAmount(null);
								trade.setNetAmount(null);
							}
						}
					}

					if ((swt.getIn().isFeePercent()) && (swt.getIn().getFeePct().doubleValue() > 0)) {
						swt.getIn().setFeeAmt(null);
					}
				} else {
					swt.getOut().setUnit(null);
				}
				/*
				 * if(outFeeByForConfirm.equalsIgnoreCase("outFeeBy2")) {
				 * swt.getOut().setFeePct(null); }
				 */
				if (!swt.getOut().isFeePercent())
					swt.getOut().setFeePct(null);

				// swt.getIn().setPrice(null);
				// swt.getIn().setUnit(null);

				if (!swt.getIn().isFeePercent()) {
					swt.getIn().setFeePct(null);
				}

				// by defaul yaitu discount by amount dan sekarang diset 0, jadi
				// kalau discount by amount maka disc. pct tidak perlu disave
				swt.getIn().setDiscAmt(BigDecimal.ZERO);
				if (swt.getIn().getDiscAmt() == null)
					swt.getIn().setDiscPct(BigDecimal.ZERO);
			}

			log.debug("[CONFIRM] switchingKey " + swt.getSwitchingKey());
			// logger.debug("[CONFIRM] fundmanager " +
			// swt.getThirdPartyByFundManager().getThirdPartyKey());

			if (swt.getOut().getBankAccount() != null) {
				BnAccount bankAccount = bankAccountService.getBankAccount(swt.getOut().getBankAccount().getBankAccountKey());
				swt.getOut().setBankAccount(bankAccount);
			}

			if (swt.getIn().getBankAccount() != null) {
				BnAccount bankAccount = bankAccountService.getBankAccount(swt.getIn().getBankAccount().getBankAccountKey());
				swt.getIn().setBankAccount(bankAccount);
			}

			if (from != null && from.equals(FROM_CANCEL_SWITCHING)) {
				try {
					log.debug("--------> CONFIRM CANCEL SWITCH TRADE DATE = " + swt.getCancelTradeDate());
					log.debug("--------> CONFIRM CANCEL SWITCH POST DATE = " + swt.getCancelPostDate());

					// TODO: Add by Fadly 2017-10-25 -> Redmine #3330
					if (keyMap.get(swt.getSwitchingKey()) != null) {
						throw new MedallionException(Messages.get("subscription.cancelTrade_can_not_cancel_trade_more_than_one"));
					} else {
						keyMap.put(swt.getSwitchingKey(), swt.getSwitchingKey());
					}

					RgSwitching cancelTrade = taService.checkCancelSwitch(swt.getSwitchingKey());
					if (cancelTrade == null)
						throw new MedallionException(Messages.get("subscription.cancelTrade_can_not_cancel_trade_more_than_one"));
					// END RedMine #3330

					taService.processCancelSwitching(swt, new RgTrade(), username, userKey);
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", Messages.get("switch.cancelled.successful", swt.getSwitchingKey()));
					renderJSON(result);
				} catch (MedallionException ex) {
					// TODO: Change By Fadly 2017-10-25 RedMine #3330
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "errorx");
					result.put("errorx", Messages.get(ex.getMessage()));
					renderJSON(result);
				} finally {
					keyMap.remove(swt.getSwitchingKey());
					// END RedMine #3330
				}
			} else {
				if (swt.getRemarksCorrection() != null) {
					swt.setRemarksCorrection(null);
				}

				if (swt.getSwitchingKey() != 0) {
					swt.setRecordStatus(LookupConstants.__RECORD_STATUS_UPDATED);
					if (swt.getOut() != null) {
						swt.getOut().setRecordStatus(LookupConstants.__RECORD_STATUS_UPDATED);
						swt.getOut().setTradeStatus(LookupConstants.__RECORD_STATUS_OPEN);
					}

					if (swt.getIn() != null) {
						swt.getIn().setRecordStatus(LookupConstants.__RECORD_STATUS_UPDATED);
						swt.getIn().setTradeStatus(LookupConstants.__RECORD_STATUS_OPEN);
					}
				}

				if (swt.getRgTrades() != null) {
					for (RgTrade trade : swt.getRgTrades()) {
						// price selalu disetting null baik itu redeem atau
						// subscribe (redmine 2316)
						trade.setPrice(null);
						if (trade.getType() != null && trade.getType().equals(RgTrade.TRADETYPE_SUBSCRIBE_SWITCHING)) {
							trade.setUnit(null);
						}
					}
				}
				RgSwitching rgSwi = taService.createSwitching(MenuConstants.RG_SWITCHING, swt, username, session.get(UIConstants.SESSION_USER_KEY));

				if (!(rgSwi.equals(null))) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", Messages.get("switch.confirmed.successful", rgSwi.getSwitchingKey()));
					renderJSON(result);
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SWITCHING));
					render("RegistrySwitch/entry.html", rgSwi, mode, confirming, outFeeBy, subscriptionFee);
				}
			}
		} catch (MedallionException ex) {
			renderJSON(Formatter.resultError(ex));
			/*flash.error(ex.getErrorCode(), ex.getParams());
			renderArgs.put("confirming", true);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SWITCHING));
			render("RegistrySwitch/entry.html", swt, mode, outFeeBy, subscriptionFee);*/
		} catch (Exception ex) {
			List<String> params = new ArrayList<String>();
			flash.error(ex.getMessage(), params.toArray());
			renderArgs.put("confirming", true);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SWITCHING));
			render("RegistrySwitch/entry.html", swt, mode, outFeeBy, subscriptionFee);
		}
	}

	public static void back(Long id, String mode, String from) {
		log.debug("back. id: " + id + " mode: " + mode + " from: " + from);

		Boolean back = true;
		renderArgs.put("confirming", false);

		if (id == null) {
			id = (long) 0;
		}

		RgSwitching swt = serializerService.deserialize(session.getId(), id, RgSwitching.class);

		if (from != null && from.equals(FROM_CANCEL_SWITCHING)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
			render("RegistrySwitch/cancel.html", swt, mode, back, from);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_SWITCHING));
			render("RegistrySwitch/entry.html", swt, mode, back);
		}
	}

	// swt.setFmgrCode(swt.getRgInvestmentaccountByOutAccountNumber().getFmgrAcc());

	public static void approval(String taskId, Long keyId, RgSwitching swt, String from, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " swt: " + swt + " from: " + from + " maintenanceLogKey: " + maintenanceLogKey);

		Boolean isApproval = true;

		RgSwitching swtTemp = null;
		try {
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			swtTemp = json.readValue(maintenanceLog.getNewData(), RgSwitching.class);
		} catch (Exception ex) {
			log.error("error getting from maintenance log", ex);
		}

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		swt = taService.loadSwitching(keyId);
		swt.splitRgTrade();

		if (swt.getRgInvestmentaccountByOutAccountNumber() != null) {
			if (swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct() != null) {
				swt.getOut().setTaxPct(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getTaxMasterByRedTaxKey().getTaxRate());
				swt.setOutThirdPartyByFundManager(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getThirdPartyByFundManager());
			}
			CfMaster customer = customerService.getCustomer(swt.getRgInvestmentaccountByOutAccountNumber().getCustomer().getCustomerKey());
			swt.setInvestor(customer);

			swt.setNavDate(swt.getOut().getNavDate());
			swt.setPostDate(swt.getOut().getPostDate());
			swt.setGoodfundDate(swt.getOut().getGoodfundDate());
			swt.setExternalReference(swt.getOut().getExternalReference());
		}

		if (swt.getRgInvestmentaccountByInAccountNumber() != null) {
			if (swt.getRgInvestmentaccountByInAccountNumber().getRgProduct() != null) {
				swt.getIn().setTaxPct(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
				swt.setInThirdPartyByFundManager(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getThirdPartyByFundManager());
			}
			CfMaster customer = customerService.getCustomer(swt.getRgInvestmentaccountByInAccountNumber().getCustomer().getCustomerKey());
			swt.setInvestor(customer);

			swt.setNavDate(swt.getIn().getNavDate());
			swt.setPostDate(swt.getIn().getPostDate());
			swt.setGoodfundDate(swt.getIn().getGoodfundDate());
			swt.setExternalReference(swt.getIn().getExternalReference());
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

		log.debug("fundmanager " + swt.getThirdPartyByFundManager());

		// swt.getIn().setTrnSABranchKey(swt.getOut().getTrnSABranchKey());
		// swt.getOut().setTrnSABranch(generalService.getThirdParty(swt.getOut().getTrnSABranchKey()));
		// swt.getIn().setTrnSABranch(generalService.getThirdParty(swt.getIn().getTrnSABranchKey()));

		if (swt.getOut() != null && swt.getOut().getFeePct() != null)
			swt.getOut().setFeePercent(true);
		if (swt.getIn() != null && swt.getIn().getFeePct() != null)
			swt.getIn().setFeePercent(true);

		// SWITCHI IN
		if (swt.getIn() != null && swt.getIn().getFeePct() == null) {
			if ((swt.getIn().getFeeAmt() != null) && (swt.getIn().getNetAmount() != null)) {
				// Double feePct = (swt.getIn().getFeeAmt().doubleValue() /
				// swt.getIn().getNetAmount().doubleValue()) * 100;
				Double feePct = new Double(0);
				if (swt.getIn().getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
					feePct = new Double(0);
				} else {
					feePct = (swt.getIn().getFeeAmt().doubleValue() / swt.getIn().getNetAmount().doubleValue()) * 100;
				}
				BigDecimal feePct1 = new BigDecimal(feePct);
				swt.getIn().setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
			}
		}

		// if(swtTemp != null && swtTemp.getOut() != null && swt.getOut() !=
		// null)
		// swt.getOut().setAvailabelUnit(swtTemp.getOut().getAvailabelUnit());
		if (swt.getOut() != null) {
			RgPortfolio portfolio = taService.loadLastPortfolio(swt.getOut().getRgInvestmentaccount().getAccountNumber(), swt.getOut().getPostDate());
			BigDecimal outstandingUnit = taService.loadOutstandingOnlyApprove(swt.getOut().getRgProduct().getProductCode(), swt.getOut().getRgInvestmentaccount().getAccountNumber(), swt.getOut().getPostDate());
			if (portfolio != null) {
				portfolio.setUnit(portfolio.getUnit().subtract(outstandingUnit));
				swt.getOut().setAvailabelUnit(portfolio.getUnit());
			}
		}
		if (swtTemp.getIn() != null && swt.getIn() != null)
			swt.getIn().setPrice(swtTemp.getIn().getPrice());

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		if (swt.getOut() != null && swt.getOut().getPaymentDate() != null)
			swt.setPaymentDate(swt.getOut().getPaymentDate());

		if (swt.getSwitchingType().equals(LookupConstants._SWITCHING_TYPE_IN)) {
			if (swt.getIn() != null && swt.getIn().getPaymentDate() != null) {
				swt.setPaymentDate(swt.getIn().getPaymentDate());
			}
		}
		render("RegistrySwitch/approval.html", swt, mode, taskId, from, isApproval, maintenanceLogKey);
	}

	@Check("transaction.inquiryUnitRegistry")
	public static void cancel(Long keyId, String from) {
		log.debug("cancel. keyId: " + keyId + " from: " + from);
		
		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgSwitching swt = taService.loadSwitching(keyId);
		swt.splitRgTrade();
		RgSwitching canSwt = taService.loadSwitchingByCancelSwitching(swt.getSwitchingKey());

		if (swt.isCancelled())
			swt.setTradeStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_CANCELED));
		else
			swt.setTradeStatus(getDecodeDataStatus(swt.getTradeStatus()));

		if (swt.getRgInvestmentaccountByOutAccountNumber() != null) {
			if (swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct() != null) {
				swt.getOut().setTaxPct(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getTaxMasterByRedTaxKey().getTaxRate());
				swt.setThirdPartyByFundManager(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getThirdPartyByFundManager());
				swt.setOutThirdPartyByFundManager(swt.getRgInvestmentaccountByOutAccountNumber().getRgProduct().getThirdPartyByFundManager());
				swt.setNavDate(swt.getOut().getNavDate());
				swt.setPostDate(swt.getOut().getPostDate());
				swt.setGoodfundDate(swt.getOut().getGoodfundDate());
			}
			CfMaster customer = customerService.getCustomer(swt.getRgInvestmentaccountByOutAccountNumber().getCustomer().getCustomerKey());
			swt.setInvestor(customer);

			swt.setExternalReference(swt.getOut().getExternalReference());
		}

		if (swt.getRgInvestmentaccountByInAccountNumber() != null) {
			if (swt.getRgInvestmentaccountByInAccountNumber().getRgProduct() != null) {
				swt.getIn().setTaxPct(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
				swt.setThirdPartyByFundManager(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getThirdPartyByFundManager());
				swt.setInThirdPartyByFundManager(swt.getRgInvestmentaccountByInAccountNumber().getRgProduct().getThirdPartyByFundManager());
				swt.setNavDate(swt.getIn().getNavDate());
				swt.setPostDate(swt.getIn().getPostDate());
				swt.setGoodfundDate(swt.getIn().getGoodfundDate());
			}
			CfMaster customer = customerService.getCustomer(swt.getRgInvestmentaccountByInAccountNumber().getCustomer().getCustomerKey());
			swt.setInvestor(customer);
			swt.setExternalReference(swt.getIn().getExternalReference());
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

			if (swt.getOut().getFeePct() != null)
				swt.getOut().setFeePercent(true);

		}

		if (swt.getIn() != null) {
			if (swt.getIn().getRgProductBnAccount() != null) {
				BnAccount bnAccount = bankAccountService.getBankAccount(swt.getIn().getRgProductBnAccount().getBankAccountKey());
				swt.getIn().setRgProductBnAccount(bnAccount);
			}

			if (swt.getIn().getFeePct() != null)
				swt.getIn().setFeePercent(true);

			// SWITCHI IN
			if (swt.getIn().getFeePct() == null) {
				if ((swt.getIn().getFeeAmt() != null) && (swt.getIn().getNetAmount() != null)) {
					// Double feePct = (swt.getIn().getFeeAmt().doubleValue() /
					// swt.getIn().getNetAmount().doubleValue()) * 100;
					Double feePct = new Double(0);
					if (swt.getIn().getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
						feePct = new Double(0);
					} else {
						feePct = (swt.getIn().getFeeAmt().doubleValue() / swt.getIn().getNetAmount().doubleValue()) * 100;
					}
					BigDecimal feePct1 = new BigDecimal(feePct);
					swt.getIn().setFeePct(feePct1.setScale(4, BigDecimal.ROUND_HALF_UP));
				}
			}
		}

		if (canSwt != null) {
			canSwt.splitRgTrade();
		}

		if (from.equals("cancelTradeApp")) {
			if (canSwt.getOut() != null) {
				swt.setCancelTradeDate(canSwt.getOut().getTradeDate());
				swt.setCancelPostDate(canSwt.getOut().getPostDate());
				swt.setCancelRemark(canSwt.getOut().getRemark());
			}

			if (canSwt.getIn() != null) {
				swt.setCancelTradeDate(canSwt.getIn().getTradeDate());
				swt.setCancelPostDate(canSwt.getIn().getPostDate());
				swt.setCancelRemark(canSwt.getIn().getRemark());
			}
		}

		if (from.equals(FROM_CANCEL_SWITCHING) || from.equals(FROM_UNIT_REGISTRY)) {
			if (swt.getOut() != null) {
				swt.setCancelTradeDate(swt.getOut().getTradeDate());
				swt.setCancelPostDate(swt.getOut().getPostDate());
				swt.setPaymentDate(swt.getOut().getPaymentDate());
			}

			if (swt.getIn() != null) {
				swt.setCancelTradeDate(swt.getIn().getTradeDate());
				swt.setCancelPostDate(swt.getIn().getPostDate());
				swt.setPaymentDate(swt.getIn().getPaymentDate());
			}
			// swt.setCancelTradeDate(currentBusinessDate);
			// swt.setCancelPostDate(currentBusinessDate);
		}

		List<RgTrade> trades = taService.listTradeBySwitching(keyId);
		log.debug("trades = " + trades.size());
		String messageError = "";
		if (!from.equals("unitRegistry")) {
			for (RgTrade trade : trades) {
				log.debug("trade Key = " + trade.getTradeKey());
				log.debug("trade date = " + trade.getTradeDate());

				if (trade.isCancelled()) {
					trade.setTradeStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_CANCELED));
				} else {
					if (trade.isPosted())
						trade.setTradeStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_POSTED));
					else if (swt.getIn() != null)
						trade.setTradeStatus(getDecodeDataStatus(swt.getIn().getTradeStatus()));
				}

				Date tradeDate = swt.getSwitchDate();
				if (swt.getIn() != null && swt.getIn().getTradeDate() != null) {
					tradeDate = swt.getIn().getTradeDate();
				}
				BigDecimal unitPorto = taService.loadUnitPortfolioByTradeCancel(trade.getTradeKey(), tradeDate);
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

		if (swt.getOut() != null && swt.getOut().getPaymentDate() != null)
			swt.setPaymentDate(swt.getOut().getPaymentDate());

		if (from.equals("unitRegistry")) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_INQUIRY_REGISTRY));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_CANCEL_TRADE));
		}

		//Add Redmine #4183
		Long paid = taService.checkSwitchingAlreadyPaid(swt.getSwitchingKey());
		
		if(swt.getOut()!=null){
			if(swt.getOut().isPosted() && swt.getOut().isPaid() && swt.getOut().getTradeStatus().equals(LookupConstants.__RECORD_STATUS_VALID)){
				swt.setTradeStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_PAID));
			}else if(swt.getOut().isPosted() && !swt.getOut().isPaid() && swt.getOut().getTradeStatus().equals(LookupConstants.__RECORD_STATUS_VALID)){
				swt.setTradeStatus(getDecodeDataStatus(LookupConstants.__RECORD_STATUS_POSTED));
			}
		}
		
		Long dataPaymentOnApproval = null;
		for (RgTrade rgTrade : swt.getRgTrades()) {
			if(rgTrade.getRgPayment()!=null){
				if(rgTrade.isPaid()==false){
					dataPaymentOnApproval = Long.valueOf(0);	
				} 
			} else { 
				dataPaymentOnApproval = Long.valueOf(1);
			}
		}
		//End #4183
		
		render("RegistrySwitch/cancel.html", swt, mode, from, paid, currentBusinessDate, dataPaymentOnApproval, messageError);
	}

	public static void approve(String taskId, Long keyId, RgSwitching swt, Long maintenanceLogKey) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId + " swt: " + swt + " maintenanceLogKey: " + maintenanceLogKey);

		Map<String, BigDecimal> holdingMap = new HashMap<String, BigDecimal>();
		//boolean isMessageValidation = false;
		try {
			RgSwitching swi = taService.loadSwitching(keyId);
			// Set<RgTrade> rgTrades = swi.getRgTrades();
			List<RgTrade> rgTrades = taService.listRgTradeBySwitching(swi.getSwitchingKey());

			log.debug("[APPROVE] rgTrades size => " + rgTrades.size());

			taService.approveSwitching(keyId, session.get(UIConstants.SESSION_USERNAME), taskId, maintenanceLogKey);

			for (RgTrade rgTrade : rgTrades) {
				rgTrade = taService.loadTrade(rgTrade.getTradeKey());
				RgProduct rgProduct = rgTrade.getRgProduct();

				// rg_trade.product_code = rg_nav.product_code and
				// rg_trade.nav_date = rg_nav.nav_date exist
				if (rgProduct != null) {
					RgNav nav = taService.loadActiveNav(rgProduct.getProductCode(), rgTrade.getNavDate());

					log.debug("[APPROVE] rgProduct.isFixnav => " + rgProduct.isFixnav());

					if ((rgProduct != null) && (rgProduct.isFixnav())) {
						rgTrade = taService.processTransactionValidation(rgTrade, holdingMap);
						if (!rgTrade.getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID)) {
							log.debug("[APPROVE ERROR] transaction message => " + rgTrade.getMessage());
							// result.put("error", rgTrade.getMessage());
						}
						// else
						// {
						// result.put("status", "success");
						// result.put("message", Messages.get("switch.approved",
						// swi.getSwitchingKey()));
						// }
					} else if ((nav != null) && (nav.getNav() != null) && (nav.getNav().compareTo(BigDecimal.ZERO) > 0)) {
						rgTrade = taService.processTransactionValidation(rgTrade, holdingMap);
						if (!rgTrade.getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID)) {
							log.debug("[APPROVE ERROR] transaction message => " + rgTrade.getMessage());
							// result.put("error", rgTrade.getMessage());
							//isMessageValidation = true;
							throw new MedallionException(rgTrade.getMessage());
						}
						// else
						// {
						// result.put("status", "success");
						// result.put("message", Messages.get("switch.approved",
						// swi.getSwitchingKey()));
						// }
					}
					// else
					// {

					renderJSON(Formatter.resultSuccess(Messages.get("switch.approved", swi.getSwitchingKey())));
				}
			}
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId, Integer nr, String remarksCorrection) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId + " nr: " + nr + " remarksCorrection: " + remarksCorrection);

		try {
			RgSwitching swi = taService.rejectSwitching(keyId, session.get(UIConstants.SESSION_USERNAME), taskId, nr, remarksCorrection);

			renderJSON(Formatter.resultSuccess(Messages.get("switch.rejected", swi.getSwitchingKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	private static String getDecodeDataStatus(String statusCode) {
		return StatusExtension.decodeDataStatus(statusCode);
	}
}