package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import vo.AnnouncementSearchParameter;
import vo.CorporateActionParam;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.FormatRoundHelper;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.CsTransactionBatch;
import com.simian.medallion.model.GnCurrency;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScActionTemplate;
import com.simian.medallion.model.ScAnnouncementDetail;
import com.simian.medallion.model.ScCorporateAnnouncement;
import com.simian.medallion.model.ScCouponSchedule;
import com.simian.medallion.model.ScEntitlement;
import com.simian.medallion.model.TxTransactionFifo;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class CorporateSettlements extends MedallionController{
	private static Logger logger = Logger.getLogger(CorporateSettlements.class);
	
	static List<String> hitConfirm = new ArrayList<String>();
	static List<String> hitApprove = new ArrayList<String>();
	
	@Before(only={"list"})
	public static void setupList() {
		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);	
	}

	@Before(unless={"list"})
	public static void setup(){
		renderArgs.put("currentDate", generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		
		renderArgs.put("viaFile", LookupConstants.FUND_TRANSFER_METHOD_FILE);
		renderArgs.put("viaInterface", LookupConstants.FUND_TRANSFER_METHOD_VIA_INTERFACE);
	}

	@Check("transaction.corporatesettlement")
	public static void list(String mode, String param) {
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CA_SETTLEMENT_ENTRY));
		render();
	}
	
	@Check("transaction.corporatesettlement")
	public static void paging(Paging page, AnnouncementSearchParameter param) {

		page.addParams("ca.distributionDate", page.GREATEQUAL, param.dateFrom);
		page.addParams("ca.distributionDate", page.LESSEQUAL, param.dateTo);
		page.addParams("ca.actionTemplate.actionTemplateKey", page.EQUAL, param.actionCode);
		page.addParams("ca.securityType.securityType", page.EQUAL, param.securityType);
		page.addParams("ca.security.securityKey", page.EQUAL, param.securityCode);
		if (!param.announcementNo.isEmpty()){
			page.addParams("ca.corporateAnnouncementCode", param.announcementNoOperator, UIHelper.withOperator(param.announcementNo, param.announcementNoOperator));
		}
		page.addParams("ca.recordStatus", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
		page.addParams("ca.status", page.EQUAL, LookupConstants.__RECORD_STATUS_ENTITLED);
		page.addParams(Helper.searchAll("ca.corporateAnnouncementCode||ca.description||to_char(ca.announcementDate,'"+Helper.dateOracle(appProp.getDateFormat())+"')||to_char(ca.recordingDate, '"+Helper.dateOracle(appProp.getDateFormat())+"')||" +
				"to_char(ca.distributionDate, '"+Helper.dateOracle(appProp.getDateFormat())+"')"),Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = securityService.pagingCorporateSettlement(page);

		renderJSON(page);
	}
	
	@Check("transaction.corporatesettlement")
	public static void view(Long id) {
		
	}
	
	@Check("transaction.corporatesettlement")
	public static void entry(){}
	
	@Check("transaction.corporatesettlement")
	public static void cancel(Long id){
	}
	
	@Check("transaction.corporatesettlement")
	public static void edit(Long id){
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String taskId = "";
		ScEntitlement entitlement = securityService.getEntitlementByAnnouncement(id);
		boolean nextProcess = false;
		
		GnSystemParameter currencyTaxSys = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX);
		GnSystemParameter currencyTaxRoundSys = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX_ROUND);
		GnSystemParameter currencyTaxRoundTypeSys = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX_ROUND_TYPE);
								
		GnCurrency currencySys = generalService.getCurrency(currencyTaxSys.getValue().trim().toUpperCase());
	
		if (entitlement!=null){
			
			ScCorporateAnnouncement announcement = securityService.getCorporateAnnouncementByIdWidthDetail(entitlement.getCorporateAnnouncement().getCorporateAnnouncementKey());
			logger.debug("announcement = "+announcement);
			
			ScActionTemplate actionTemplate = securityService.getActionTemplate(announcement.getActionTemplate().getActionTemplateKey());
			logger.debug("actionTemplate = "+actionTemplate);
			
			List<CsTransaction> trxLinks = new ArrayList<CsTransaction>();
			List<ScCorporateAnnouncement> announcements = securityService.listAnnouncementByTaxCaLink(id);
			boolean isAnnouncement = false;
			if (announcements!=null){
				if(announcements.size() > 0){
					isAnnouncement = true;
				}
				for (ScCorporateAnnouncement scCorporateAnnouncement : announcements) {
					logger.debug("Status = " + scCorporateAnnouncement.getStatus());
					if (scCorporateAnnouncement.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_ENTITLED) || 
						scCorporateAnnouncement.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_SETTLED)){
						ScEntitlement entLink = securityService.getEntitlementByAnnouncement(scCorporateAnnouncement.getCorporateAnnouncementKey());
						List<CsTransaction> transactionCaLinks = accountService.listCaTransaction(entLink.getEntitlementKey());
						for (CsTransaction csTransaction : transactionCaLinks) {
							CsTransaction csTrans = accountService.getTransactionByEntitlement(csTransaction.getTransactionKey());
							trxLinks.add(csTrans);
						}
						
					}
				}
				
				nextProcess = true;
			}
			
			
			
			List<CsTransaction> listCaTransactions = accountService.listCaTransaction(entitlement.getEntitlementKey());
			List<CsTransaction> transLists = new ArrayList<CsTransaction>();
			BigDecimal sumQty = BigDecimal.ZERO;
			BigDecimal sumSettle = BigDecimal.ZERO;
			
			String taxObject = null;
			if (entitlement.getCorporateAnnouncement().getTaxable()){
				taxObject = entitlement.getCorporateAnnouncement().getActionTemplate().getTaxObject().getLookupId();
			}
			
			for (CsTransaction csTransaction : listCaTransactions) {
				BigDecimal sumOfTaxAfterComp = BigDecimal.ZERO;
				CsTransaction csTrans = accountService.getTransactionByEntitlement(csTransaction.getTransactionKey());
				
				if (taxObject!=null){
					if ((nextProcess) && (taxObject.equals(LookupConstants.TAX_OBJECT_ACCRUED_INTEREST))){
						List<TxTransactionFifo> listTrxFifo = transactionService.listTransactionFifo(csTrans.getTransactionKey());
						
						//TODO Change By Fadly Redmine #2798
						if(entitlement.getCorporateAnnouncement().getTaxable() != null && entitlement.getCorporateAnnouncement().getTaxable()){
							for (TxTransactionFifo txTransactionFifo : listTrxFifo) {
								if(isAnnouncement){
									sumOfTaxAfterComp = sumOfTaxAfterComp.add(txTransactionFifo.getTaxAfterCompensate());
								}else{
									if(txTransactionFifo.getTaxOfInt() == null){
										txTransactionFifo.setTaxOfInt(BigDecimal.ZERO);
									}
									sumOfTaxAfterComp = sumOfTaxAfterComp.add(txTransactionFifo.getTaxOfInt());
								}
							}
							//start #2968 - rounding
							//#4028 - trunct dipindahkan ke pkg_tax (sebelum di-sum). Di sini tidak perlu di-rounding lagi
							/*if (csTrans.getCurrency().getCurrencyCode().equals(currencySys.getCurrencyCode())) {
								
								Integer entitlementRoundInt = Integer.parseInt(currencyTaxRoundSys.getValue());
								String roundType = currencyTaxRoundTypeSys.getValue().trim().toUpperCase();
								BigDecimal taxAmountTruncated = new BigDecimal(0);
								taxAmountTruncated = Helper.format(sumOfTaxAfterComp, entitlementRoundInt, FormatRoundHelper.getRoundMode(roundType));
								sumOfTaxAfterComp = taxAmountTruncated;
							}*/
							//end #2968
							
							csTrans.setSettlementAmount(csTrans.getSettlementAmount().add(csTrans.getTaxOnInterest()).subtract(sumOfTaxAfterComp));
						}
						//END Change Redmine #2798
						
						sumSettle = sumSettle.add(csTrans.getSettlementAmountAdjustment());
					} else if ((nextProcess) && (taxObject.equals(LookupConstants.TAX_OBJECT_DIVIDEND))){
						for (CsTransaction csTransaction2 : trxLinks) {
							/*logger.debug("Cust Accoount Key 	 = "+csTransaction.getAccount().getCustodyAccountKey());
							logger.debug("Cust Accoount Key Link = "+csTransaction2.getAccount().getCustodyAccountKey());
							logger.debug("COMPARE ACCOUNT = " +(csTransaction2.getAccount().getCustodyAccountKey().compareTo(csTransaction.getAccount().getCustodyAccountKey()) == 0));
							logger.debug("--------------------");
							logger.debug("Security Key 	 	= "+csTransaction.getSecurity().getSecurityKey());
							logger.debug("Security Key Link = "+csTransaction2.getSecurity().getSecurityKey());
							logger.debug("--------------------");
							logger.debug("Classification 	  = "+csTransaction.getClassification().getLookupId());
							logger.debug("Classification Link = "+csTransaction2.getClassification().getLookupId());
							logger.debug("--------------------");
							logger.debug("Holding Refs 	 	= "+csTransaction.getHoldingRefs());
							logger.debug("Holding Refs Link = "+csTransaction2.getHoldingRefs());
							logger.debug("--------------------");*/
							
							if ((csTransaction2.getAccount().getCustodyAccountKey().compareTo(csTrans.getAccount().getCustodyAccountKey())==0) &&
							    (csTransaction2.getSecurity().getSecurityKey().compareTo(csTrans.getSecurity().getSecurityKey())==0) &&
							    (csTransaction2.getClassification().getLookupId().equals(csTrans.getClassification().getLookupId())) &&
							    (csTransaction2.getHoldingRefs().equals(csTrans.getHoldingRefs()))) {
								csTrans.setSettlementAmount(csTrans.getSettlementAmount().subtract(csTransaction2.getCapitalGainTax()));
							}
						}
						sumSettle = sumSettle.add(csTrans.getSettlementAmountAdjustment());
					} else {
						sumSettle = sumSettle.add(csTrans.getSettlementAmountAdjustment());
					}
				} else {
					sumSettle = sumSettle.add(csTrans.getSettlementAmountAdjustment());
				}
				sumQty = sumQty.add(csTrans.getQuantity());
				
				if (actionTemplate != null && csTrans.getTaxAdjust() != null) {
					if(LookupConstants.TAX_OBJECT_ACCRUED_INTEREST.equals(actionTemplate.getTaxObject().getLookupId())) {
						csTrans.setTaxOnInterest(csTrans.getTaxAdjust());
					}else if(LookupConstants.TAX_OBJECT_CAPITAL_GAIN.equals(actionTemplate.getTaxObject().getLookupId())) {
						csTrans.setCapitalGainTax(csTrans.getTaxAdjust());
					}else if(LookupConstants.TAX_OBJECT_DISCOUNT.equals(actionTemplate.getTaxObject().getLookupId())) {
						csTrans.setCapitalGainTax(csTrans.getTaxAdjust());
					}else if(LookupConstants.TAX_OBJECT_DIVIDEND.equals(actionTemplate.getTaxObject().getLookupId())) {
						csTrans.setCapitalGainTax(csTrans.getTaxAdjust());
					}
				}
				
				csTrans.setSettlementAmount(csTrans.getSettlementAmountAdjustment());
				
				transLists.add(csTrans);
			}
			
			Long securityKey = new Long(0);
			Integer couponNo = new Integer(0);
			
			if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_ANNOUNCEMENT_DATE)){
				entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getAnnouncementDate());
			}else if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_CUM_DATE)){
				entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getCumDate());
			}else if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_EX_DATE)){
				entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getExDate());
			}else if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_RECORDING_DATE)){
				entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getRecordingDate());
			}else if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_COUPON_DATE)){
				for (ScAnnouncementDetail scd : entitlement.getCorporateAnnouncement().getAnnouncementDetails()) {
					securityKey = scd.getCorporateAnnouncement().getSecurity().getSecurityKey();
					couponNo = scd.getCouponSchedule().getId().getCouponNo();
				}
				ScCouponSchedule coupon = securityService.getCouponScheduleFilter(securityKey, couponNo);
				entitlement.getCorporateAnnouncement().setAnnouncementDate(coupon.getPaymentDate());
			}else if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_DISTRIBUTION_DATE)){
				entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getDistributionDate());
			}
			
			entitlement.setSizeOfTransaction(transLists.size());
			entitlement.setSumOfQuantity(sumQty);
			entitlement.setSumOfSettlementAmount(sumSettle);
			
			List<SelectItem> transferMethods = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
			renderArgs.put("transferMethods", transferMethods);
			
			String editTransferMethod = "YES";
			
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CA_SETTLEMENT_ENTRY));
			render("CorporateSettlements/detail.html", entitlement, mode, transLists, taskId, editTransferMethod );
		}
	}
	
	@Check("transaction.corporatesettlement")
	public static void editPartial(Long id){
	}
	
	@Check("transaction.corporatesettlement")
	public static void settle(ScEntitlement entitlement, String mode, List<CsTransaction> transactions){
		
		GnSystemParameter currencyTaxSys = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX);
		GnSystemParameter currencyTaxRoundSys = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX_ROUND);
		GnSystemParameter currencyTaxRoundTypeSys = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX_ROUND_TYPE);
								
		GnCurrency currencySys = generalService.getCurrency(currencyTaxSys.getValue().trim().toUpperCase());
		
		if (entitlement!=null){
			// validation
			
			boolean nextProcess = false;
			List<CsTransaction> trxLinks = new ArrayList<CsTransaction>();
			List<ScCorporateAnnouncement> announcements = securityService.listAnnouncementByTaxCaLink(entitlement.getCorporateAnnouncement().getCorporateAnnouncementKey());
			boolean isAnnouncement = false;
			if (announcements!=null){
				if(announcements.size() > 0){
					isAnnouncement = true;
				}
				for (ScCorporateAnnouncement scCorporateAnnouncement : announcements) {
					if (scCorporateAnnouncement.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_ENTITLED) || 
						scCorporateAnnouncement.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_SETTLED)){
						ScEntitlement entLink = securityService.getEntitlementByAnnouncement(scCorporateAnnouncement.getCorporateAnnouncementKey());
						List<CsTransaction> transactionCaLinks = accountService.listCaTransaction(entLink.getEntitlementKey());
						for (CsTransaction csTransaction : transactionCaLinks) {
							CsTransaction csTrans = accountService.getTransactionByEntitlement(csTransaction.getTransactionKey());
							trxLinks.add(csTrans);
						}
						
					}
				}
				nextProcess = true;
			}
			
			transactions = accountService.listCaTransaction(entitlement.getEntitlementKey());
			List<CsTransaction> transLists = new ArrayList<CsTransaction>();
			
			String taxObject = null;
			ScActionTemplate actionTemplate = null;
			
			if (entitlement.getCorporateAnnouncement().getActionTemplate().getTaxObject()!=null){
				taxObject = entitlement.getCorporateAnnouncement().getActionTemplate().getTaxObject().getLookupId();
				
				actionTemplate = securityService.getActionTemplate(entitlement.getCorporateAnnouncement().getActionTemplate().getActionTemplateKey());
				logger.debug("actionTemplate = "+actionTemplate);
			}
			if (transactions!=null){
				for (CsTransaction csTransaction : transactions) {
					BigDecimal sumOfTaxAfterComp = BigDecimal.ZERO;
					CsTransaction csTrans = accountService.getTransactionByEntitlement(csTransaction.getTransactionKey());
					if (taxObject!=null){
						logger.debug("next process = "+nextProcess);
						if ((nextProcess) && (taxObject.equals(LookupConstants.TAX_OBJECT_ACCRUED_INTEREST))){

							//TODO Change by Fadly 2017-10-19 Redmine #2798
							ScCorporateAnnouncement announcement = securityService.getCorporateAnnouncementByIdPlain(entitlement.getCorporateAnnouncement().getCorporateAnnouncementKey());
							//if(entitlement.getCorporateAnnouncement().getTaxable() != null && entitlement.getCorporateAnnouncement().getTaxable()){
							if(announcement.getTaxable() != null && announcement.getTaxable()) {
								List<TxTransactionFifo> listTrxFifo = transactionService.listTransactionFifo(csTrans.getTransactionKey());
								for (TxTransactionFifo txTransactionFifo : listTrxFifo) {
									if(isAnnouncement){
										sumOfTaxAfterComp = sumOfTaxAfterComp.add(txTransactionFifo.getTaxAfterCompensate());
									}else{
										if(txTransactionFifo.getTaxOfInt() == null){
											txTransactionFifo.setTaxOfInt(BigDecimal.ZERO);
										}
										sumOfTaxAfterComp = sumOfTaxAfterComp.add(txTransactionFifo.getTaxOfInt());
									}
								}
								
								
								//start #2968 - rounding
								//#4028 - trunct dipindahkan ke pkg_tax (sebelum di-sum). Di sini tidak perlu di-rounding lagi
								/*if (csTrans.getCurrency().getCurrencyCode().equals(currencySys.getCurrencyCode())) {
									
									Integer entitlementRoundInt = Integer.parseInt(currencyTaxRoundSys.getValue());
									String roundType = currencyTaxRoundTypeSys.getValue().trim().toUpperCase();
									BigDecimal taxAmountTruncated = new BigDecimal(0);
									taxAmountTruncated = Helper.format(sumOfTaxAfterComp, entitlementRoundInt, FormatRoundHelper.getRoundMode(roundType));
									sumOfTaxAfterComp = taxAmountTruncated;
								}*/
								//end #2968
								
								csTrans.setSettlementAmount(csTrans.getSettlementAmount().add(csTrans.getTaxOnInterest()).subtract(sumOfTaxAfterComp));
							}
							//END Change Redmine #2798
							
						}
						logger.debug("trxLinks size = "+trxLinks.size());
						if ((nextProcess) && (taxObject.equals(LookupConstants.TAX_OBJECT_DIVIDEND))){
							for (CsTransaction csTransaction2 : trxLinks) {
								if ((csTransaction2.getAccount().getCustodyAccountKey().compareTo(csTrans.getAccount().getCustodyAccountKey())==0) &&
								    (csTransaction2.getSecurity().getSecurityKey().compareTo(csTrans.getSecurity().getSecurityKey())==0) &&
								    (csTransaction2.getClassification().getLookupId().equals(csTrans.getClassification().getLookupId())) &&
								    (csTransaction2.getHoldingRefs().equals(csTrans.getHoldingRefs()))) {
	
									csTrans.setSettlementAmount(csTrans.getSettlementAmount().subtract(csTransaction2.getCapitalGainTax()));
								}
							}
						}
					}
					logger.debug("trans key = "+csTrans.getTranLogKey());
					logger.debug("trans sett amt 1 = "+csTrans.getSettlementAmount());
					logger.debug("trans sett amt adj 1 = "+csTrans.getSettlementAmountAdj());
					if (actionTemplate != null && csTrans.getTaxAdjust() != null) {
						if(LookupConstants.TAX_OBJECT_ACCRUED_INTEREST.equals(actionTemplate.getTaxObject().getLookupId())) {
							csTrans.setTaxOnInterest(csTrans.getTaxAdjust());
						}else if(LookupConstants.TAX_OBJECT_CAPITAL_GAIN.equals(actionTemplate.getTaxObject().getLookupId())) {
							csTrans.setCapitalGainTax(csTrans.getTaxAdjust());
						}else if(LookupConstants.TAX_OBJECT_DISCOUNT.equals(actionTemplate.getTaxObject().getLookupId())) {
							csTrans.setCapitalGainTax(csTrans.getTaxAdjust());
						}else if(LookupConstants.TAX_OBJECT_DIVIDEND.equals(actionTemplate.getTaxObject().getLookupId())) {
							csTrans.setCapitalGainTax(csTrans.getTaxAdjust());
						}
					}
					
					csTrans.setSettlementAmount(csTrans.getSettlementAmountAdjustment());
					
					logger.debug("trans sett amt 2 = "+csTrans.getSettlementAmount());
					logger.debug("trans sett amt adj 2 = "+csTrans.getSettlementAmountAdj());
					
					transLists.add(csTrans);
				}
				entitlement.setTransactions(transLists);
			}
			if (validation.hasErrors()){
				
			} else {
				Long id = entitlement.getCorporateAnnouncement().getCorporateAnnouncementKey();
				String json = serializerService.serialize(session.getId(), id, entitlement);
				confirming(id, mode);
			}
		}
	}
	
	@Check("transaction.corporatesettlement")
	public static void confirming(Long id, String mode){
		renderArgs.put("confirming", true);
		ScEntitlement entitlement = serializerService.deserialize(session.getId(), id, ScEntitlement.class);
		List<CsTransaction> transLists = new ArrayList<CsTransaction>();
		if (entitlement.getTransactions()!=null)
			transLists = entitlement.getTransactions();
		
		List<SelectItem> transferMethods = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
		renderArgs.put("transferMethods", transferMethods);
		
		String editTransferMethod = "NO";
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CA_SETTLEMENT_ENTRY));
		render("CorporateSettlements/detail.html", entitlement, mode, transLists, editTransferMethod);
	}
	
	@Check("transaction.corporatesettlement")
	public static void confirm(ScEntitlement entitlement, List<CsTransaction> transactions, String mode){
		
		GnSystemParameter currencyTaxSys = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX);
		GnSystemParameter currencyTaxRoundSys = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX_ROUND);
		GnSystemParameter currencyTaxRoundTypeSys = generalService.getSystemParameter(SystemParamConstants.CURRENCY_TAX_ROUND_TYPE);
								
		GnCurrency currencySys = generalService.getCurrency(currencyTaxSys.getValue().trim().toUpperCase());
		
		ScCorporateAnnouncement announcement = securityService.getCorporateAnnouncementByCode(entitlement.getCorporateAnnouncement().getCorporateAnnouncementCode());
		Map<String, Object> result = new HashMap<String, Object>();
		
		boolean nextProcess = false;
		List<CsTransaction> trxLinks = new ArrayList<CsTransaction>();
		List<ScCorporateAnnouncement> announcement1s = securityService.listAnnouncementByTaxCaLink(announcement.getCorporateAnnouncementKey());
		boolean isAnnouncement = false;
		if (announcement1s!=null){
			if(announcement1s.size() > 0){
				isAnnouncement = true;
			}
			for (ScCorporateAnnouncement scCorporateAnnouncement : announcement1s) {
				if (scCorporateAnnouncement.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_ENTITLED) || 
					scCorporateAnnouncement.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_SETTLED)){
					ScEntitlement entLink = securityService.getEntitlementByAnnouncement(scCorporateAnnouncement.getCorporateAnnouncementKey());
					List<CsTransaction> transactionCaLinks = accountService.listCaTransaction(entLink.getEntitlementKey());
					for (CsTransaction csTransaction : transactionCaLinks) {
						CsTransaction csTrans = accountService.getTransactionByEntitlement(csTransaction.getTransactionKey());
						trxLinks.add(csTrans);
					}
					
				}
			}
			nextProcess = true;
		}
		
		if ((announcement.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_ENTITLED))&&
			(announcement.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED))){
		
			if (hitConfirm.contains(announcement.getCorporateAnnouncementKey().toString())) {
				result.put("status", "error");
				result.put("error", "Announcement No :"+announcement.getCorporateAnnouncementCode()+" currently in process");
				renderJSON(result);
			}else{
				hitConfirm.add(announcement.getCorporateAnnouncementKey().toString());
				try {
					transactions = accountService.listCaTransaction(entitlement.getEntitlementKey());
					List<CsTransaction> transactionLists = new ArrayList<CsTransaction>();
					
					
					String taxObject = null;
					if (announcement.getActionTemplate().getTaxObject()!=null){
						taxObject = announcement.getActionTemplate().getTaxObject().getLookupId();
					}
					for (CsTransaction csTransaction : transactions) {
						BigDecimal sumOfTaxAfterComp = BigDecimal.ZERO;	
						CsTransaction csTrans = accountService.getTransactionByEntitlement(csTransaction.getTransactionKey());
						if (taxObject!=null){
							
							//TODO Change by Fadly 2017-10-19 Redmine #2798
							if ((nextProcess) && (taxObject.equals(LookupConstants.TAX_OBJECT_ACCRUED_INTEREST)) && (announcement.getTaxable() != null && announcement.getTaxable())){
							//END  Change
								List<TxTransactionFifo> listTrxFifo = transactionService.listTransactionFifo(csTrans.getTransactionKey());
								for (TxTransactionFifo txTransactionFifo : listTrxFifo) {
									// If tax link = true then getTaxAfterCompensate else get taxOfInt
									if(isAnnouncement){
										sumOfTaxAfterComp = sumOfTaxAfterComp.add(txTransactionFifo.getTaxAfterCompensate());
									}else{
										if(txTransactionFifo.getTaxOfInt() == null){
											txTransactionFifo.setTaxOfInt(BigDecimal.ZERO);
										}
										sumOfTaxAfterComp = sumOfTaxAfterComp.add(txTransactionFifo.getTaxOfInt());
									}
								}
								
								//start #2968 - rounding
								//#4028 - trunct dipindahkan ke pkg_tax (sebelum di-sum). Di sini tidak perlu di-rounding lagi 
								/*if (csTrans.getCurrency().getCurrencyCode().equals(currencySys.getCurrencyCode())) {
									
									Integer entitlementRoundInt = Integer.parseInt(currencyTaxRoundSys.getValue());
									String roundType = currencyTaxRoundTypeSys.getValue().trim().toUpperCase();
									BigDecimal taxAmountTruncated = new BigDecimal(0);
									taxAmountTruncated = Helper.format(sumOfTaxAfterComp, entitlementRoundInt, FormatRoundHelper.getRoundMode(roundType));
									sumOfTaxAfterComp = taxAmountTruncated;
								}*/
								//end #2968
								
								csTrans.setProceed(csTrans.getProceed().add(csTrans.getTaxOnInterest()).subtract(sumOfTaxAfterComp));
								csTrans.setSettlementAmount(csTrans.getSettlementAmount().add(csTrans.getTaxOnInterest()).subtract(sumOfTaxAfterComp));
								csTrans.setBaseSettlementAmount(csTrans.getBaseSettlementAmount().add(csTrans.getTaxOnInterest()).subtract(sumOfTaxAfterComp));
								csTrans.setTaxOnInterestNet(csTrans.getTaxOnInterestNet().add(csTrans.getTaxOnInterest()).subtract(sumOfTaxAfterComp));
								csTrans.setNetProceed(csTrans.getNetProceed().add(csTrans.getTaxOnInterest()).subtract(sumOfTaxAfterComp));
								csTrans.setTaxOnInterest(sumOfTaxAfterComp);
							}
							
							if ((nextProcess) && (taxObject.equals(LookupConstants.TAX_OBJECT_DIVIDEND))){
								for (CsTransaction csTransaction2 : trxLinks) {
									if ((csTransaction2.getAccount().getCustodyAccountKey().compareTo(csTrans.getAccount().getCustodyAccountKey())==0) &&
									    (csTransaction2.getSecurity().getSecurityKey().compareTo(csTrans.getSecurity().getSecurityKey())==0) &&
									    (csTransaction2.getClassification().getLookupId().equals(csTrans.getClassification().getLookupId())) &&
									    (csTransaction2.getHoldingRefs().equals(csTrans.getHoldingRefs()))) {
//										csTrans.setProceed(csTrans.getProceed().add(csTransaction2.getCapitalGainTax()));
										csTrans.setSettlementAmount(csTrans.getSettlementAmount().subtract(csTransaction2.getCapitalGainTax()));
										csTrans.setNetProceed(csTrans.getNetProceed().subtract(csTransaction2.getCapitalGainTax()));
										
										csTrans.setCapitalGainTax(csTrans.getCapitalGainTax().add(csTransaction2.getCapitalGainTax()));
//										csTrans.setBaseSettlementAmount(csTrans.getBaseSettlementAmount().add(csTransaction2.getCapitalGainTax()));
//										csTrans.setTaxOnInterestNet(csTrans.getTaxOnInterestNet().add(csTransaction2.getCapitalGainTax()));
										
									}
								}
							}
						}
						
						transactionLists.add(csTrans);
					}
					
					try {
			//			String from = AbstractTransaction.PARAM_CA_SETTLE;
						List<ScCorporateAnnouncement> announcements = securityService.listAnnouncementByTaxCaLink(announcement.getCorporateAnnouncementKey());
						logger.debug("ANNOUNCEMENT SIZE = " +announcements.size());
						
						StringBuffer statusBuffer = new StringBuffer();
						List<String> caCode = new ArrayList<String>();
						if (announcements!=null){
							for (ScCorporateAnnouncement ca : announcements) {
								logger.debug("STATUS = " +ca.getStatus());
								if (!ca.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_REJECTED) && 
									!ca.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_SETTLED) &&
									!ca.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_CANCELED)){
									caCode.add(ca.getCorporateAnnouncementCode());
								}
							}
							
							if (!caCode.isEmpty()){
								throw new MedallionException(ExceptionConstants.ANNOUNCEMENT_HAS_TAX_CA_LINK_NOT_SETTLE, caCode);
							}
						}
						hitConfirm.remove(announcement.getCorporateAnnouncementKey().toString());
//						CsTransactionBatch batch = new CsTransactionBatch();
						CsTransactionBatch batch = transactionService.caSettleTransactions(transactionLists, session.get(UIConstants.SESSION_USERNAME), entitlement, session.get(UIConstants.SESSION_USER_KEY), nextProcess, taxObject);
						if (batch.getTrxNoError().size()>0){
							result.put("status", "error");
							result.put("error", Messages.get("error."+batch.getMessageError().split("/")[0], batch.getMessageError().split("/")[1]+" "+batch.getTrxNoError().toString()));
							renderJSON(result);
						} else {
							result.put("status", "success");
							result.put("message", Messages.get("settlement.batch.successful", batch.getTransactionBatchKey()));
							renderJSON(result);
						}
					} catch (MedallionException ex) {
						result.put("status", "error");
						List<String> params = new ArrayList<String>();
						if(ex.getParams()!= null){
							for (Object param : ex.getParams()) {
								params.add(Messages.get(param));
							}
						}
						result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
						hitConfirm.remove(announcement.getCorporateAnnouncementKey().toString());
					}
				} catch (MedallionException ex2){
					result.put("status", "error");
					List<String> params = new ArrayList<String>();
					if(ex2.getParams()!= null){
						for (Object param : ex2.getParams()) {
							params.add(Messages.get(param));
						}
						result.put("error", Messages.get("error." + ex2.getErrorCode(), params.toArray()));
					}else{
						result.put("error",ex2.getErrorCode());
					}
					hitConfirm.remove(announcement.getCorporateAnnouncementKey().toString());
				}catch (Exception e) {
					hitConfirm.remove(announcement.getCorporateAnnouncementKey().toString());
					result.put("status", "error");
					result.put("error",e.getMessage());
				} finally {
					hitConfirm.remove(announcement.getCorporateAnnouncementKey().toString());
				}
				renderJSON(result);
			}
		} else {
			hitConfirm.remove(announcement.getCorporateAnnouncementKey().toString());
			result.put("status", "error");
			//result.put("error", "Announcement No :"+announcement.getCorporateAnnouncementCode()+" can not settle");
			if(announcement.getIsProcessed() != null && announcement.getIsProcessed()){
				result.put("error", "Announcement No :"+announcement.getCorporateAnnouncementCode()+" currently in use");
			} else {
				result.put("error", "Announcement No :"+announcement.getCorporateAnnouncementCode()+" can not settle");
			}
			
			renderJSON(result);
		}
		
	}
	
	@Check("transaction.corporatesettlement")
	public static void back(Long id, String mode) {
		ScEntitlement entitlement = serializerService.deserialize(session.getId(), id, ScEntitlement.class);
		List<CsTransaction> transLists = new ArrayList<CsTransaction>();
		if (entitlement.getTransactions()!=null)
			transLists = entitlement.getTransactions();
		
		List<SelectItem> transferMethods = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
		renderArgs.put("transferMethods", transferMethods);
		
		String editTransferMethod = "YES";
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CA_SETTLEMENT_ENTRY));
		render("CorporateSettlements/detail.html", entitlement, transLists, mode, editTransferMethod);
	}
	
	public static void approval(String taskId, Long keyId, String from){
		logger.debug("TASK ID = " +taskId);
		logger.debug("keyId = " +keyId);
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		ScEntitlement entitlement = securityService.getEntitlementByAnnouncement(keyId);
//		List<CsTransaction> listCancel = accountService.listCancelEntitlement(entitlement.getEntitlementKey().toString());
		ScCorporateAnnouncement announcement = securityService.getCorporateAnnouncementByIdPlain(keyId);
		ScActionTemplate actionTemplate = securityService.getActionTemplate(announcement.getActionTemplate().getActionTemplateKey());
		List<CsTransaction> listTransSettlements = accountService.listCaSettlementTransaction(entitlement.getEntitlementKey());
		List<CsTransaction> transLists = new ArrayList<CsTransaction>();
		BigDecimal sumQty = BigDecimal.ZERO;
		BigDecimal sumSettle = BigDecimal.ZERO;
		for (CsTransaction csTransaction : listTransSettlements) {
			CsTransaction csTrans = accountService.getTransactionByEntitlement(csTransaction.getTransactionKey());
			if (csTransaction.getTransferMethod() != null) {
				entitlement.setTransferMethod(csTrans.getTransferMethod());
			}
			sumQty = sumQty.add(csTrans.getQuantity());
			sumSettle = sumSettle.add(csTrans.getSettlementAmount());
			
			if (actionTemplate != null && csTrans.getTaxAdjust() != null) {
				if(LookupConstants.TAX_OBJECT_ACCRUED_INTEREST.equals(actionTemplate.getTaxObject().getLookupId())) {
					csTrans.setTaxOnInterest(csTrans.getTaxAdjust());
				}else if(LookupConstants.TAX_OBJECT_CAPITAL_GAIN.equals(actionTemplate.getTaxObject().getLookupId())) {
					csTrans.setCapitalGainTax(csTrans.getTaxAdjust());
				}else if(LookupConstants.TAX_OBJECT_DISCOUNT.equals(actionTemplate.getTaxObject().getLookupId())) {
					csTrans.setCapitalGainTax(csTrans.getTaxAdjust());
				}else if(LookupConstants.TAX_OBJECT_DIVIDEND.equals(actionTemplate.getTaxObject().getLookupId())) {
					csTrans.setCapitalGainTax(csTrans.getTaxAdjust());
				}
			}
			
			csTrans.setSettlementAmount(csTrans.getSettlementAmountAdjustment());
			
			transLists.add(csTrans);
		}
		
		Long securityKey = new Long(0);
		Integer couponNo = new Integer(0);
		
		if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_ANNOUNCEMENT_DATE)){
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getAnnouncementDate());
		}else if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_CUM_DATE)){
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getCumDate());
		}else if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_EX_DATE)){
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getExDate());
		}else if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_RECORDING_DATE)){
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getRecordingDate());
		}else if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_COUPON_DATE)){
			for (ScAnnouncementDetail scd : entitlement.getCorporateAnnouncement().getAnnouncementDetails()) {
				securityKey = scd.getCorporateAnnouncement().getSecurity().getSecurityKey();
				couponNo = scd.getCouponSchedule().getId().getCouponNo();
			}
			ScCouponSchedule coupon = securityService.getCouponScheduleFilter(securityKey, couponNo);
			entitlement.getCorporateAnnouncement().setAnnouncementDate(coupon.getPaymentDate());
		}else if(entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_DISTRIBUTION_DATE)){
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getDistributionDate());
		}
		
		entitlement.setSizeOfTransaction(transLists.size());
		entitlement.setSumOfQuantity(sumQty);
		entitlement.setSumOfSettlementAmount(sumSettle);
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		
		List<SelectItem> transferMethods = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
		renderArgs.put("transferMethods", transferMethods);
		
		String editTransferMethod = "NO";
		
		render("CorporateSettlements/approval.html", entitlement, mode, taskId, transLists, editTransferMethod);
	}
	
	/**** Single Approval menggunakan method processAjax() ****/
	public static void approve(String keyId, String taskId, String announcementKey, String typeTransaction) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if (hitApprove.contains(keyId)) {
			result.put("status", "error");
			result.put("error","error.Record has already been modified");
			renderJSON(result);
		}else{
			hitApprove.add(keyId);
//			List<CsTransaction> transactions = accountService.listTransactionByBatchKey(Long.parseLong(keyId));
			List<CsTransaction> transactions = accountService.listCaSettlementTransaction(Long.parseLong(keyId));
			logger.debug("size of transactions = " +transactions.size());
			
			typeTransaction = "casettlement";
			
			try {	
				CsTransactionBatch batch = transactionService.approveTransactionSettlementCa(transactions, session.get(UIConstants.SESSION_USERNAME), taskId, announcementKey);	
				String message = Messages.get("batch.approved", batch.getTransactionBatchKey());
				result.put("status", "success");
				result.put("message", message);
				hitApprove.remove(keyId);
				renderJSON(result);	
			} catch (MedallionException ex) {
				hitApprove.remove(keyId);
				renderJSON(Formatter.resultError(ex));
			}catch (Exception e) {
				hitApprove.remove(keyId);
				renderJSON(Formatter.resultError(e));
			} finally {
				hitApprove.remove(keyId);
			}
		}
	}
	
	public static void processAjax(CorporateActionParam param) {
		logger.debug("processAjax keyId="+param.getKeyId()+", taskId="+param.getTaskId()+", announcementKey="+param.getAnnouncementKey()+", typeTransaction="+param.getTypeTransaction());
		String keyId = param.getKeyId();
		String taskId = param.getTaskId();
		String announcementKey = param.getAnnouncementKey();
		String typeTransaction = param.getTypeTransaction();
		Map<String, Object> result = new HashMap<String, Object>();
		
		if (hitApprove.contains(keyId)) {
			result.put("status", "error");
			result.put("error","error.Record has already been modified");
			renderJSON(result);
		}else{
			hitApprove.add(keyId);
//			List<CsTransaction> transactions = accountService.listTransactionByBatchKey(Long.parseLong(keyId));
			List<CsTransaction> transactions = accountService.listCaSettlementTransaction(Long.parseLong(keyId));
			logger.debug("size of transactions = " +transactions.size());
			
			typeTransaction = "casettlement";
			
			try {	
				CsTransactionBatch batch = transactionService.approveTransactionSettlementCa(transactions, session.get(UIConstants.SESSION_USERNAME), taskId, announcementKey);	
				String message = Messages.get("batch.approved", batch.getTransactionBatchKey());
				result.put("status", "success");
				result.put("message", message);
				hitApprove.remove(keyId);
				renderJSON(result);	
			} catch (MedallionException ex) {
				hitApprove.remove(keyId);
				renderJSON(Formatter.resultError(ex));
			}catch (Exception e) {
				hitApprove.remove(keyId);
				renderJSON(Formatter.resultError(e));;
			} finally {
				hitApprove.remove(keyId);
			
			}
		}
	}
	
	public static void reject(String keyId, String taskId, String announcementKey, String typeTransaction) {
		Map<String, Object> result = new HashMap<String, Object>();
//		List<CsTransaction> transactions = accountService.listTransactionByBatchKey(Long.parseLong(keyId));
		List<CsTransaction> transactions = accountService.listCaSettlementTransaction(Long.parseLong(keyId));
		typeTransaction = "casettlement";
		
		try {
			CsTransactionBatch batch = transactionService.rejectTransactionSettlementCa(transactions, session.get(UIConstants.SESSION_USERNAME), taskId, announcementKey);	
			String message = Messages.get("batch.rejected", batch.getTransactionBatchKey());
			result.put("status", "success");
			result.put("message", message);
			renderJSON(result);	
		} catch (MedallionException ex) {
			renderJSON(Formatter.resultError(ex));
		}catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
