package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.mvc.Before;
import vo.RegistryPaymentSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.AbstractRgPayment;
import com.simian.medallion.model.AbstractRgTrade;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RegistryPaymentSearchParametersModel;
import com.simian.medallion.model.RgPayment;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.RgTransaction;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class RegistryPayment extends Registry {
	private static Logger log = Logger.getLogger(RegistryPayment.class);

	@Before(only = {"approval", "list", "listBack", "showDividen", "showRedeem", "process","reset", "save" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> listType = new ArrayList<SelectItem>();
		listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_REDEMPTION, AbstractRgPayment.BY_TYPE_REDEMPTION.toUpperCase()));
		listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_SWITCH_OUT, AbstractRgPayment.BY_TYPE_SWITCH_OUT.toUpperCase()));
		// listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_DIV_BY_REDEEM,
		// AbstractRgPayment.BY_TYPE_DIV_BY_REDEEM.toUpperCase()));
		listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_DIV_BY_CASH, AbstractRgPayment.BY_TYPE_DIV_BY_CASH.toUpperCase()));
		renderArgs.put("listType", listType);

		List<SelectItem> filterBy = new ArrayList<SelectItem>();
		filterBy.add(new SelectItem(AbstractRgPayment.FILTER_BY_NO_FILTER, "ALL"));
		filterBy.add(new SelectItem(AbstractRgPayment.FILTER_BY_NO_TRX, "TRANSACTION NO"));
		filterBy.add(new SelectItem(AbstractRgPayment.FILTER_BY_NO_SWITCHING, "SWITCHING NO"));
		filterBy.add(new SelectItem(AbstractRgPayment.FILTER_BY_NO_DIVIDEND, "DIVIDEND NO"));
		renderArgs.put("filterBy", filterBy);

		//renderArgs.put("viaFile", LookupConstants.FUND_TRANSFER_METHOD_FILE);
		renderArgs.put("viaInterface", LookupConstants.FUND_TRANSFER_METHOD_VIA_INTERFACE);
		renderArgs.put("domainBankRed", LookupConstants._DOMAIN_BANK_RED);

		List<SelectItem> transferMethods = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
		renderArgs.put("transferMethods", transferMethods);
	}

	@Check("transaction.registryPayment")
	public static void list(RegistryPaymentSearchParameters param) {
		log.debug("list. param: " + param);

		if (param == null) {
			param = new RegistryPaymentSearchParameters();
			param.setPaymentDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
			param.setType(AbstractRgPayment.BY_TYPE_REDEMPTION);
			param.setRgProduct(new RgProduct());
			List<RgTrade> trades = new ArrayList<RgTrade>();
			param.setTrades(trades);
		}

		param.setDispatch(RegistryPaymentSearchParameters.DISPATCH_VIEW);
		param.setPaymentKey(Long.parseLong("0"));

		param.setSelectedProductThirdPartyCodeChange("");
		param.setSelectedInvestorThirdPartyCodeChange("");
		param.setSelectedbankCodeChange("");
		param.setSelectedbankCodeNameChange("");

		render("RegistryPayment/list.html", param);
	}

	public static void listBack(RegistryPaymentSearchParameters param) {
		log.debug("listBack. param: " + param);

		param.setSelectedProductThirdPartyCodeChange("");
		param.setSelectedInvestorThirdPartyCodeChange("");
		param.setSelectedbankCodeChange("");
		param.setSelectedbankCodeNameChange("");

		param.setDispatch(RegistryPaymentSearchParameters.DISPATCH_VIEW);

		RgPayment pay = serializerService.deserialize(session.getId(), null, RgPayment.class);
		param.setTransferMethod(pay.getTransferMethod());

		render("RegistryPayment/list.html", param);
	}

	public static void paging(Paging page, RegistryPaymentSearchParameters param) {
		log.debug("paging. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		if (param.isQuery()) {
			page.addParams("rt.paymentDate", page.LESSEQUAL, param.getPaymentDate());

			if (param.getPaymentKey() == null) {
				page.addParams("rt.paid", page.EQUAL, 0);
			}

			if (param.getDispatch().equalsIgnoreCase(RegistryPaymentSearchParameters.DISPATCH_APPROVE)) {
				page.addParams("rt.rgPayment.paymentKey", page.EQUAL, param.getPaymentKey());
			} else {
				page.addParams("rt.rgPayment", page.ISNULL, null);
			}

			if (param.getRgProduct() != null) {
				page.addParams("rt.rgProduct.productCode", page.EQUAL, param.getRgProduct().getProductCode());
			}

			page.addParams(Helper.searchAll("(rt.tradeKey||to_char(rt.paymentDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "rt.rgInvestmentaccount.accountNumber||rt.rgInvestmentaccount.name||" + "to_char(rt.tradeDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||rt.amount||rt.bankAccount.accountNo||" + "rt.bankAccount.bankCode.thirdPartyCode||rt.bankAccount.customer.customerName)"), page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			if (AbstractRgPayment.BY_TYPE_REDEMPTION.equals(param.getType())) {
				page.addParams("rt.posted", page.EQUAL, 1);
				page.addParams("rt.type", page.EQUAL, AbstractRgTrade.TRADETYPE_REDEEM);
				page.addParams("rt.tradeStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_VALID);
				if (param.getFilterByNo() != null) {
					page.addParams("rt.tradeKey", page.EQUAL, param.getFilterByNo());
				}
			} else if (AbstractRgPayment.BY_TYPE_SWITCH_OUT.equals(param.getType())) {
				page.addParams("rt.posted", page.EQUAL, 1);
				page.addParams("rt.type", page.EQUAL, AbstractRgTrade.TRADETYPE_REDEEM_SWITCHING);
				page.addParams("rt.tradeStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_VALID);
				if (param.getFilterByNo() != null) {
					page.addParams("rt.rgSwitching.switchingKey", page.EQUAL, param.getFilterByNo());
				}
			} else {
				if (AbstractRgPayment.BY_TYPE_DIV_BY_REDEEM.equals(param.getType())) {
					page.addParams("rt.posted", page.EQUAL, 1);
					page.addParams("rt.tradeStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_VALID);
					page.addParams("rt.type", page.EQUAL, AbstractRgTrade.TRADETYPE_REDEEM_FUND_ACTION);
					page.addParams("rt.rgFundAction.divType", page.EQUAL, AbstractRgTrade.DIV_TYPE_BY_REDEEM);
				}

				if (AbstractRgPayment.BY_TYPE_DIV_BY_CASH.equals(param.getType())) {
					page.addParams("rt.posted", page.EQUAL, 0);
					page.addParams("rt.tradeStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
					page.addParams("rt.type", page.EQUAL, AbstractRgTrade.TRADETYPE_CASH_FUND_ACTION);
					page.addParams("rt.rgFundAction.divType", page.EQUAL, AbstractRgTrade.DIV_TYPE_BY_CASH);
				}

				if (param.getFilterByNo() != null) {
					page.addParams("rt.rgFundAction.fundActionKey", page.EQUAL, param.getFilterByNo());
				}
			}

			if (param.getPaymentKey() != null) {
				page.getParamFixMap().put("paymentKey", param.getPaymentKey());
			}

			page = taService.pagingRegistryPayment(page, param.getType());

			/*
			 * if(param.getPaymentKey() != null) { page.getData().clear();
			 * List<RgPaymentDetail> paymentDetailList =
			 * taService.listPaymentDetailByPayment(param.getPaymentKey()); for
			 * (RgPaymentDetail payDetail : paymentDetailList) { if(payDetail !=
			 * null) { RgTrade rt = new RgTrade();
			 * rt.setTradeKey(payDetail.getRgTrade().getTradeKey());
			 * rt.setPaymentDate(payDetail.getRgTrade().getPaymentDate());
			 * rt.setTradeDate(payDetail.getRgTrade().getTradeDate());
			 * rt.setExternalReference
			 * (payDetail.getRgTrade().getExternalReference());
			 * 
			 * rt.setRgProduct(new RgProduct());
			 * if(payDetail.getRgTrade().getRgProduct() != null) {
			 * rt.getRgProduct
			 * ().setProductCode(payDetail.getRgTrade().getRgProduct
			 * ().getProductCode());
			 * rt.getRgProduct().setName(payDetail.getRgTrade
			 * ().getRgProduct().getName());
			 * 
			 * rt.getRgProduct().setCfMaster(new CfMaster());
			 * if(payDetail.getRgTrade().getRgProduct().getCfMaster() != null) {
			 * CfMaster customer =
			 * customerService.getCustomer(payDetail.getRgTrade
			 * ().getRgProduct().getCfMaster().getCustomerKey());
			 * rt.getRgProduct
			 * ().getCfMaster().setCustomerKey(customer.getCustomerKey());
			 * rt.getRgProduct
			 * ().getCfMaster().setCustomerNo(customer.getCustomerNo());
			 * rt.getRgProduct
			 * ().getCfMaster().setCustomerName(customer.getCustomerName()); }
			 * 
			 * rt.getRgProduct().setCurrency(new GnCurrency());
			 * rt.getRgProduct()
			 * .getCurrency().setCurrencyCode(payDetail.getRgTrade
			 * ().getRgProduct().getCurrency().getCurrencyCode());
			 * rt.getRgProduct
			 * ().getCurrency().setDescription(payDetail.getRgTrade
			 * ().getRgProduct().getCurrency().getDescription()); }
			 * 
			 * rt.setRgInvestmentaccount(new RgInvestmentaccount());
			 * if(payDetail.getRgTrade().getRgInvestmentaccount() != null) {
			 * rt.getRgInvestmentaccount
			 * ().setAccountNumber(payDetail.getRgTrade(
			 * ).getRgInvestmentaccount().getAccountNumber());
			 * rt.getRgInvestmentaccount
			 * ().setName(payDetail.getRgTrade().getRgInvestmentaccount
			 * ().getName()); }
			 * 
			 * rt.setSaCode(new GnThirdParty());
			 * if(payDetail.getRgTrade().getSaCode() != null) {
			 * rt.getSaCode().setThirdPartyCode
			 * (payDetail.getRgTrade().getSaCode().getThirdPartyCode());
			 * rt.getSaCode
			 * ().setThirdPartyName(payDetail.getRgTrade().getSaCode(
			 * ).getThirdPartyName()); }
			 * 
			 * rt.setRgTransaction(new RgTransaction());
			 * if(payDetail.getRgTrade().getRgTransaction() != null) {
			 * rt.getRgTransaction
			 * ().setUnit(payDetail.getRgTrade().getRgTransaction().getUnit());
			 * rt
			 * .getRgTransaction().setPrice(payDetail.getRgTrade().getRgTransaction
			 * ().getPrice());
			 * rt.getRgTransaction().setAmount(payDetail.getRgTrade
			 * ().getRgTransaction().getAmount());
			 * rt.getRgTransaction().setFeeAmount
			 * (payDetail.getRgTrade().getRgTransaction().getFeeAmount());
			 * rt.getRgTransaction
			 * ().setDiscAmount(payDetail.getRgTrade().getRgTransaction
			 * ().getDiscAmount());
			 * rt.getRgTransaction().setOtherAmount(payDetail
			 * .getRgTrade().getRgTransaction().getOtherAmount());
			 * 
			 * rt.setAmount(payDetail.getRgTrade().getRgTransaction().getAmount()
			 * .
			 * subtract(payDetail.getRgTrade().getRgTransaction().getCapTaxAmount
			 * ())); }
			 * 
			 * rt.setBankAccount(new BnAccount());
			 * if(payDetail.getRgTrade().getBankAccount() != null) { BnAccount
			 * bnAccountInvestor =
			 * bankAccountService.getBankAccount(payDetail.getRgTrade
			 * ().getBankAccount().getBankAccountKey());
			 * 
			 * rt.getBankAccount().setAccountNo(bnAccountInvestor.getAccountNo())
			 * ; rt.getBankAccount().setBankAccountKey(bnAccountInvestor.
			 * getBankAccountKey()); rt.getBankAccount().setBankCode(new
			 * GnThirdParty());
			 * rt.getBankAccount().getBankCode().setThirdPartyKey
			 * (bnAccountInvestor.getBankCode().getThirdPartyKey());
			 * rt.getBankAccount
			 * ().getBankCode().setThirdPartyCode(bnAccountInvestor
			 * .getBankCode().getThirdPartyCode());
			 * rt.getBankAccount().getBankCode
			 * ().setThirdPartyName(bnAccountInvestor
			 * .getBankCode().getThirdPartyName());
			 * rt.getBankAccount().setCustomer(new CfMaster());
			 * rt.getBankAccount
			 * ().getCustomer().setCustomerKey(bnAccountInvestor
			 * .getCustomer().getCustomerKey());
			 * rt.getBankAccount().getCustomer(
			 * ).setCustomerNo(bnAccountInvestor.getCustomer().getCustomerNo());
			 * rt
			 * .getBankAccount().getCustomer().setCustomerName(bnAccountInvestor
			 * .getCustomer().getCustomerName());
			 * rt.getBankAccount().setCurrency(new GnCurrency());
			 * rt.getBankAccount
			 * ().getCurrency().setCurrencyCode(bnAccountInvestor
			 * .getCurrency().getCurrencyCode());
			 * rt.getBankAccount().getCurrency
			 * ().setDescription(bnAccountInvestor
			 * .getCurrency().getDescription()); }
			 * 
			 * rt.setRgProductBnAccount(new BnAccount());
			 * if(payDetail.getRgTrade().getRgProductBnAccount() != null) {
			 * BnAccount bnAccountProduct =
			 * bankAccountService.getBankAccount(payDetail
			 * .getRgTrade().getRgProductBnAccount().getBankAccountKey());
			 * 
			 * rt.getRgProductBnAccount().setAccountNo(bnAccountProduct.getAccountNo
			 * ());
			 * rt.getRgProductBnAccount().setBankAccountKey(bnAccountProduct
			 * .getBankAccountKey()); rt.getRgProductBnAccount().setBankCode(new
			 * GnThirdParty());
			 * rt.getRgProductBnAccount().getBankCode().setThirdPartyKey
			 * (bnAccountProduct.getBankCode().getThirdPartyKey());
			 * rt.getRgProductBnAccount
			 * ().getBankCode().setThirdPartyCode(bnAccountProduct
			 * .getBankCode().getThirdPartyCode());
			 * rt.getRgProductBnAccount().getBankCode
			 * ().setThirdPartyName(bnAccountProduct
			 * .getBankCode().getThirdPartyName());
			 * rt.getRgProductBnAccount().setCustomer(new CfMaster());
			 * rt.getRgProductBnAccount
			 * ().getCustomer().setCustomerKey(bnAccountProduct
			 * .getCustomer().getCustomerKey());
			 * rt.getRgProductBnAccount().getCustomer
			 * ().setCustomerNo(bnAccountProduct.getCustomer().getCustomerNo());
			 * rt.getRgProductBnAccount().getCustomer().setCustomerName(
			 * bnAccountProduct.getCustomer().getCustomerName());
			 * rt.getRgProductBnAccount().setCurrency(new GnCurrency());
			 * rt.getRgProductBnAccount
			 * ().getCurrency().setCurrencyCode(bnAccountProduct
			 * .getCurrency().getCurrencyCode());
			 * rt.getRgProductBnAccount().getCurrency
			 * ().setDescription(bnAccountProduct
			 * .getCurrency().getDescription()); }
			 * 
			 * rt.setRecordStatus(payDetail.getRgTrade().getRecordStatus());
			 * page.addData(rt); } } }
			 */
		}
		renderJSON(page);
	}

	@Check("transaction.registryPayment")
	public static void showDividen(RgPayment pay) {
		log.debug("showDividen. pay: " + pay);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;

		log.debug("TYPE = " + pay.getType() + " with Filter By = " + pay.getFilterBy());
		long filterByNo = (pay.getFilterByNo() == null || "".equals(pay.getFilterByNo())) ? 0 : Long.valueOf(pay.getFilterByNo());

		List<RgTrade> tradeList = taService.listPaymentForDividend(pay.getRgProduct().getProductCode(), pay.getType(), pay.getFilterBy(), filterByNo, fmtYYYYMMDD(pay.getAppDate()));
		log.debug("SIZE DIVIDENDS = " + tradeList.size());

		for (RgTrade rgTrade : tradeList) {
			if (rgTrade.getRgFundAction() != null) {
				log.debug("FUND ACTION KEY = " + rgTrade.getRgFundAction().getFundActionKey());
			}

			log.debug("PRODUCT => " + rgTrade.getRgProduct().getProductCode());

			if (rgTrade.getBankAccount() != null) {
				BnAccount investorBnAccount = bankAccountService.getBankAccount(rgTrade.getBankAccount().getBankAccountKey());
				rgTrade.setBankAccount(investorBnAccount);
			}

			if (rgTrade.getRgProductBnAccount() != null) {
				BnAccount productBnAccount = bankAccountService.getBankAccount(rgTrade.getRgProductBnAccount().getBankAccountKey());
				rgTrade.setRgProductBnAccount(productBnAccount);
			}

			CfMaster customerProduct = customerService.getCustomer(rgTrade.getRgProduct().getCfMaster().getCustomerKey());
			rgTrade.getRgProduct().setCfMaster(customerProduct);
		}

		String trades = null;

		try {
			JsonHelper json = new JsonHelper().getRgTradeSerializerForRegistryPayment();
			if (tradeList != null) {
				trades = json.serialize(tradeList);
			}
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PAYMENT));
		render("RegistryPayment/list.html", pay, trades, mode);
	}

	@Check("transaction.registryPayment")
	public static void showRedeem(RgPayment pay) {
		log.debug("showRedeem. pay: " + pay);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		log.debug("TYPE = " + pay.getType() + " with Filter By = " + pay.getFilterBy());
		long filterByNo = (pay.getFilterByNo() == null || "".equals(pay.getFilterByNo())) ? 0 : Long.valueOf(pay.getFilterByNo());
		log.debug("filterByNo = " + filterByNo);

		List<RgTrade> tradeList = taService.listPaymentForRedemption(pay.getRgProduct().getProductCode(), pay.getType(), pay.getFilterBy(), filterByNo, fmtYYYYMMDD(pay.getPaymentDate()));

		log.debug("PAYMENT DATE = " + pay.getPaymentDate());
		log.debug("SIZE TRADES BY REDEEM = " + tradeList.size());

		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal capTaxAmount = BigDecimal.ZERO;

		for (RgTrade rgTrade : tradeList) {
			if (rgTrade.getRgTransaction() != null) {
				log.debug(">>>>> ");
				amount = rgTrade.getRgTransaction().getAmount();
				capTaxAmount = rgTrade.getRgTransaction().getCapTaxAmount();
				log.debug("AMOUNT = " + amount);
				log.debug("CAP TAX AMOUNT = " + capTaxAmount);
				rgTrade.setAmount(amount.subtract(capTaxAmount));
			}

			if (rgTrade.getBankAccount() != null) {
				BnAccount bnAccountInvestor = bankAccountService.getBankAccount(rgTrade.getBankAccount().getBankAccountKey());
				rgTrade.setBankAccount(bnAccountInvestor);
			}

			if (rgTrade.getRgProductBnAccount() != null) {
				BnAccount bnAccountProduct = bankAccountService.getBankAccount(rgTrade.getRgProductBnAccount().getBankAccountKey());
				rgTrade.setRgProductBnAccount(bnAccountProduct);
			}

			CfMaster customerProduct = new CfMaster();
			if (rgTrade.getRgProduct().getCfMaster() != null) {
				customerProduct = customerService.getCustomer(rgTrade.getRgProduct().getCfMaster().getCustomerKey());
			}

			rgTrade.getRgProduct().setCfMaster(customerProduct);
		}

		String trades = null;

		try {
			JsonHelper json = new JsonHelper().getRgTradeSerializerForRegistryPayment();
			if (tradeList != null) {
				trades = json.serialize(tradeList);
			}
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PAYMENT));
		render("RegistryPayment/list.html", pay, trades, mode);
	}

	public static void process(RgPayment pay) {
		log.debug("process. pay: " + pay);

		// String[] tradeKeys = pay.getSelected().split(",");
		// String[] tradeAmounts = pay.getSelectedNominal().split(",");
		// pay.setRgTrades(new HashSet<RgTrade>());
		// pay.setRgPaymentDetails(new HashSet<RgPaymentDetail>());
		//
		// for (int i = 0; i < tradeKeys.length; i++) {
		// RgTrade rgt = new RgTrade(Long.valueOf(tradeKeys[i]));
		// RgPaymentDetail payDetail = new RgPaymentDetail(rgt);
		// rgt.setAmount(new BigDecimal(tradeAmounts[i]));
		// payDetail.setRgPayment(pay);
		// payDetail.setcancelled(false);
		// payDetail.setAmount(new BigDecimal(tradeAmounts[i]));
		// pay.getRgTrades().add(rgt);
		// pay.getRgPaymentDetails().add(payDetail);
		// }
		//
		String username = session.get(UIConstants.SESSION_USERNAME);
		// RgPayment rgPay = new RgPayment();

		// pay = taService.confirmValidateRegistryPayment(pay);

		// coding di atas di pindahakan di service di ganti dgn manggil
		// confirmValidateRegistryPayment
		RgPayment rgPay = taService.createPayment(MenuConstants.RG_PAYMENT, pay, username, session.get(UIConstants.SESSION_USER_KEY), false);

		pay = new RgPayment();
		pay.setPaymentKey(rgPay.getPaymentKey());
		pay.setAppDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		pay.setType(AbstractRgPayment.BY_TYPE_REDEMPTION);
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		List<RgTransaction> trades = new ArrayList<RgTransaction>();
		pay.setPaymentDate(null);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PAYMENT));
		render("RegistryPayment/list.html", pay, trades, mode);
	}

	@Check("transaction.registryPayment")
	public static void entry() {
		log.debug("entry. ");
	}

	@Check("transaction.registryPayment")
	public static void edit(RgTransaction tran) {
		log.debug("edit. tran: " + tran);
	}

	@Check("transaction.registryPayment")
	public static void view(Long key) {
		log.debug("view. key: " + key);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgTrade pay = taService.loadTrade(key);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PAYMENT));
		render("RegistryPayment/detail.html", pay, mode);
	}

	public static void approval(String taskId, Long keyId, RgPayment pay, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " pay: " + pay + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RegistryPaymentSearchParameters param = new RegistryPaymentSearchParameters();

		log.info("OPTIMIZE START loadPayment----------------------------");
		pay = taService.loadPayment(keyId);
		log.info("OPTIMIZE END loadPayment----------------------------");

		param.setTransferMethod(pay.getTransferMethod());

		String selected = null;
		String productBankAccount = null;
		String investorBankAccount = null;
		for (RgTrade trade : pay.getRgTrades()) {
			if (AbstractRgPayment.BY_TYPE_REDEMPTION.equals(pay.getType())) {
				pay.setType(AbstractRgPayment.BY_TYPE_REDEMPTION);
			}

			if (AbstractRgPayment.BY_TYPE_SWITCH_OUT.equals(pay.getType())) {
				pay.setType(AbstractRgPayment.BY_TYPE_SWITCH_OUT);
			}

			if (AbstractRgPayment.BY_TYPE_DIV_BY_REDEEM.equals(pay.getType())) {
				pay.setType(AbstractRgPayment.BY_TYPE_DIV_BY_REDEEM);
				if (trade.getRgFundAction() != null) {
					param.setFilterByNo(trade.getRgFundAction().getFundActionKey());
				}
			}

			if (AbstractRgPayment.BY_TYPE_DIV_BY_CASH.equals(pay.getType())) {
				pay.setType(AbstractRgPayment.BY_TYPE_DIV_BY_CASH);
				if (trade.getRgFundAction() != null) {
					param.setFilterByNo(trade.getRgFundAction().getFundActionKey());
				}
			}

			if (selected == null) {
				selected = Long.toString(trade.getTradeKey());
				if (trade.getRgProductBnAccount() != null) {
					productBankAccount = Long.toString(trade.getRgProductBnAccount().getBankAccountKey());
				}

				if (trade.getBankAccount() != null) {
					investorBankAccount = Long.toString(trade.getBankAccount().getBankAccountKey());
				}
			} else {
				selected += "_" + Long.toString(trade.getTradeKey());
				if (trade.getRgProductBnAccount() != null) {
					productBankAccount = Long.toString(trade.getRgProductBnAccount().getBankAccountKey());
				}
				if (trade.getBankAccount() != null) {
					investorBankAccount = Long.toString(trade.getBankAccount().getBankAccountKey());
				}
			}
		}

		// BigDecimal newAmount = null;
		// List<RgTrade> tradeList = new ArrayList<RgTrade>();

		// logger.debug("[APPROVAL] PaymentDetails SIZE => " +
		// taService.listPaymentDetailByPayment(pay.getPaymentKey()).size());

		// di bawah di matikan krn data yang di tampilkan dari paging
		// BigDecimal totalAmount = BigDecimal.ZERO;
		// for(RgPaymentDetail payDetail :
		// taService.listPaymentDetailByPayment(pay.getPaymentKey())){
		// if (AbstractRgPayment.BY_TYPE_REDEMPTION.equals(pay.getType())) {
		// if (payDetail.getRgTrade().getRgTransaction() != null) {
		// logger.debug("size >> " +
		// payDetail.getRgTrade().getRgTransaction().getAmount());
		// newAmount =
		// payDetail.getRgTrade().getRgTransaction().getAmount().subtract(payDetail.getRgTrade().getRgTransaction().getCapTaxAmount());
		//
		// payDetail.getRgTrade().setAmount(newAmount);
		// payDetail.getRgTrade().setTotFeeAmt(newAmount);
		//
		// totalAmount = totalAmount.add(newAmount);
		// pay.setAmount(totalAmount);
		//
		// if (payDetail.getBankAccount() != null) {
		// BnAccount bnAccountInvestor =
		// bankAccountService.getBankAccount(payDetail.getBankAccount().getBankAccountKey());
		// payDetail.getRgTrade().setBankAccount(bnAccountInvestor);
		// logger.debug("[APPROVAL] RGTRADE INVESTORBANKACCOUNT => " +
		// payDetail.getBankAccount().getBankAccountKey());
		// }
		//
		// if (payDetail.getRgProductBnAccount() != null) {
		// BnAccount bnAccountProduct =
		// bankAccountService.getBankAccount(payDetail.getRgProductBnAccount().getBankAccountKey());
		// payDetail.getRgTrade().setRgProductBnAccount(bnAccountProduct);
		// logger.debug("[APPROVAL] RGTRADE PRODUCTBANKACCOUNT => " +
		// payDetail.getRgProductBnAccount().getBankAccountKey());
		// }
		//
		// if (payDetail.getRgTrade().getRgProduct().getCfMaster() != null) {
		// CfMaster customerProduct =
		// customerService.getCustomer(payDetail.getRgTrade().getRgProduct().getCfMaster().getCustomerKey());
		// payDetail.getRgTrade().getRgProduct().setCfMaster(customerProduct);
		// }
		//
		// tradeList.add(payDetail.getRgTrade());
		// }
		// } else {
		// totalAmount = totalAmount.add(payDetail.getRgTrade().getAmount());
		// logger.debug("Pay.amount = " + pay.getAmount());
		// pay.setAmount(totalAmount);
		//
		// CfMaster customerProduct =
		// customerService.getCustomer(payDetail.getRgTrade().getRgProduct().getCfMaster().getCustomerKey());
		// payDetail.getRgTrade().getRgProduct().setCfMaster(customerProduct);
		//
		// if (payDetail.getBankAccount() != null) {
		// BnAccount bnAccountInvestor =
		// bankAccountService.getBankAccount(payDetail.getBankAccount().getBankAccountKey());
		// payDetail.getRgTrade().setBankAccount(bnAccountInvestor);
		// logger.debug("[APPROVAL] RGTRADE INVESTORBANKACCOUNT => " +
		// payDetail.getBankAccount().getBankAccountKey());
		// }
		//
		// if (payDetail.getRgProductBnAccount() != null) {
		// BnAccount bnAccountProduct =
		// bankAccountService.getBankAccount(payDetail.getRgProductBnAccount().getBankAccountKey());
		// payDetail.getRgTrade().setRgProductBnAccount(bnAccountProduct);
		// logger.debug("[APPROVAL] RGTRADE PRODUCTBANKACCOUNT => " +
		// payDetail.getRgProductBnAccount().getBankAccountKey());
		// }
		//
		// tradeList.add(payDetail.getRgTrade());
		// }
		// }

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}

		// Gax ketemua kegunaan di bawah ini soalnya dari approve dan reject
		// tidak ada yang ambil dari serilized
		String trades = null;
		//
		// try {
		// logger.info("OPTIMIZE START getRgTradeSerializerForRegistryPayment----------------------------");
		// JsonHelper json = new
		// JsonHelper().getRgTradeSerializerForRegistryPayment();
		// logger.info("OPTIMIZE END getRgTradeSerializerForRegistryPayment----------------------------");
		// if(tradeList != null)
		// {
		// logger.info("OPTIMIZE START serialize----------------------------");
		// trades = json.serialize(tradeList);
		// logger.info("OPTIMIZE END serialize----------------------------");
		// }
		// } catch (JsonGenerationException e) {
		// logger.debug( "json.serialize");
		// } catch (JsonMappingException e) {
		// logger.debug( "json.serialize");
		// } catch (IOException e) {
		// logger.debug( "json.serialize");
		// }

		pay.setAppDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());

		param.setDispatch(RegistryPaymentSearchParameters.DISPATCH_APPROVE);
		param.setQuery(true);
		param.setType(pay.getType());
		param.setPaymentKey(pay.getPaymentKey());
		param.setRgProduct(pay.getRgProduct());
		param.setPaymentDate(pay.getPaymentDate());
		param.setRemarks(pay.getRemarks());
		param.setTotalPaidAmount(pay.getAmount());
		param.setSelected(selected);
		param.setSelectedProductBankAccountKey(productBankAccount);
		param.setSelectedInvestorBankAccountKey(investorBankAccount);

		render("RegistryPayment/approval.html", taskId, keyId, param, trades, mode, from);
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		//Map<String, Object> result = new HashMap<String, Object>();
		try {
			log.info("OPTIMIZE START approvePayment----------------------------");
			RgPayment rgPayment = taService.approvePayment(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);
			log.info("OPTIMIZE END approvePayment----------------------------");

			// load baru agar sama dgn di db
			rgPayment = taService.loadPayment(rgPayment.getPaymentKey());
			// logger.debug("[APPROVE] size rgTrades = " +
			// rgPayment.getRgTrades().size());
			if (rgPayment.getRgTrades() != null) {
				log.debug("[APPROVE] size rgTrades = " + rgPayment.getRgTrades().size());
				for (RgTrade rgTrade : rgPayment.getRgTrades()) {
					if (LookupConstants.__RECORD_STATUS_APPROVED.equals(rgPayment.getPaymentStatus().trim())) {
						Integer cancelFlag = 0;
						String type = "Pay " + rgTrade.getType();

						if (AbstractRgTrade.TRADETYPE_REDEEM_SWITCHING.equals(rgTrade.getType()))
							type = "Pay Switch-Out";

						taService.sentFaInterface(rgTrade.getTradeKey(), type, cancelFlag, rgPayment.getPaymentDate());
					}
				}
			}

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

	public static void reject(String taskId, Long keyId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId);

		try {
			taService.rejectPayment(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}

	}

	@Check("transaction.registryPayment")
	public static void reset() {
		log.debug("reset. ");

		RegistryPaymentSearchParameters param = new RegistryPaymentSearchParameters();
		param.setSelectedProductThirdPartyCodeChange("");
		param.setSelectedInvestorThirdPartyCodeChange("");
		param.setSelectedbankCodeChange("");
		param.setSelectedbankCodeNameChange("");
		param.setPaymentDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		param.setRgProduct(new RgProduct());
		param.setDispatch(RegistryPaymentSearchParameters.DISPATCH_VIEW);
		render("RegistryPayment/list.html", param);
	}

	@Check("transaction.registryPayment")
	public static void save(RegistryPaymentSearchParametersModel param) {
		log.debug("save. param: " + param);

		// RegistryPaymentSearchParameters di bikin beda agar bisa digunain di
		// service
		param.setDispatch(RegistryPaymentSearchParameters.DISPATCH_SAVE);

		RgPayment pay = taService.saveValidateRegistryPayment(param);
		pay.setTransferMethod(param.getTransferMethod());

		serializerService.serialize(session.getId(), null, pay);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PAYMENT));
		render("RegistryPayment/list.html", param);
	}

	public static void confirm() {
		log.debug("confirm. ");

		try {
			// param.setDispatch(RegistryPaymentSearchParameters.DISPATCH_SAVE);

			/*
			 * String[] tradeKeys = pay.getSelected().split("_"); String[]
			 * tradeAmounts = pay.getSelectedNominal().split("_"); String[]
			 * tradeProductBankAccountKey =
			 * pay.getSelectedProductBankAccountKey().split("_"); String[]
			 * tradeInvestorBankAccountKey =
			 * pay.getSelectedInvestorBankAccountKey().split("_");
			 * 
			 * logger.debug("[CONFIRM] PAYMENT AMOUNT => " + pay.getAmount());
			 * 
			 * for (int i = 0; i < tradeKeys.length; i++) { RgTrade rgt =
			 * taService.loadTrade(Long.valueOf(tradeKeys[i]));
			 * 
			 * BigDecimal tradeAmount = new BigDecimal(0);
			 * if(!tradeAmounts[i].equals("undefined") &&
			 * !tradeAmounts[i].equals("")) tradeAmount = new
			 * BigDecimal(tradeAmounts[i]);
			 * if(!tradeProductBankAccountKey[i].equalsIgnoreCase("undefined"))
			 * { BnAccount bnAccountProduct =
			 * bankAccountService.getBankAccount(Long
			 * .parseLong(tradeProductBankAccountKey[i]));
			 * rgt.setRgProductBnAccount(bnAccountProduct); }
			 * 
			 * if(!tradeInvestorBankAccountKey[i].equalsIgnoreCase("undefined"))
			 * { BnAccount bnAccountInvestor =
			 * bankAccountService.getBankAccount(
			 * Long.parseLong(tradeInvestorBankAccountKey[i]));
			 * rgt.setBankAccount(bnAccountInvestor); }
			 * 
			 * RgPaymentDetail payDetail = new RgPaymentDetail(rgt);
			 * //if(tradeAmounts[i].equalsIgnoreCase("undefined")) //{
			 * rgt.setAmount(tradeAmount); //}
			 * 
			 * payDetail.setRgPayment(pay); payDetail.setcancelled(false);
			 * 
			 * //if(tradeAmounts[i].equalsIgnoreCase("undefined")) //{
			 * payDetail.setAmount(tradeAmount); //}
			 * logger.debug("tradeAmount"+i+"--"+tradeAmounts[i]);
			 * logger.debug("Amount-->"+payDetail.getAmount());
			 * 
			 * payDetail.setBankAccount(rgt.getBankAccount());
			 * payDetail.setRgProductBnAccount(rgt.getRgProductBnAccount());
			 * 
			 * 
			 * logger.debug("[CONFIRM] RGTRADE PRODUCT BANK ACCOUNT => " +
			 * rgt.getRgProductBnAccount().getBankAccountKey());
			 * logger.debug("[CONFIRM] RGTRADE INVESTOR BANK ACCOUNT => " +
			 * rgt.getBankAccount().getBankAccountKey());
			 * logger.debug("[CONFIRM] PAYMENT DETAIL PRODUCT BANK ACCOUNT => "
			 * + payDetail.getRgProductBnAccount().getBankAccountKey());
			 * logger.debug("[CONFIRM] PAYMENT DETAIL INVESTOR BANK ACCOUNT => "
			 * + payDetail.getBankAccount().getBankAccountKey());
			 * 
			 * pay.getRgTrades().add(rgt);
			 * pay.getRgPaymentDetails().add(payDetail); }
			 */

			RgPayment pay = serializerService.deserialize(session.getId(), null, RgPayment.class);
			pay.getRgTrades().clear();

			String username = session.get(UIConstants.SESSION_USERNAME);

			// yang di bawah dimatikan masukin ke service
			// logger.info("OPTIMIZE END confirmValidateRegistryPayment----------------------------");
			pay = taService.confirmValidateRegistryPayment(pay, false);
			// logger.info("OPTIMIZE END confirmValidateRegistryPayment----------------------------");

			log.info("OPTIMIZE START createPayment----------------------------");
			RgPayment payment = taService.createPayment(MenuConstants.RG_PAYMENT, pay, username,  session.get(UIConstants.SESSION_USER_KEY), false);
			log.info("OPTIMIZE END createPayment----------------------------");

			pay = new RgPayment(payment.getPaymentKey());
			renderJSON(pay);
		} catch (MedallionException ex) {
			RgPayment pay = new RgPayment();
			pay.setRemarks(ex.getMessage()); // numpang pesan
			// pay.getErrors().add(new String[]{"global", ex.getMessage()});
			renderJSON(pay);
		}
	}

}