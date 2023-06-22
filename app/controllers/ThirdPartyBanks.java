package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.GnThirdPartyAccount;
import com.simian.medallion.model.GnThirdPartyBank;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class ThirdPartyBanks extends MedallionController {
	private static Logger logger = Logger.getLogger(ThirdPartyBanks.class);
	
	@Before(unless={"list"})
	public static void setup() {
		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}
	
	@Check("administration.thirdParty.bank")
	public static void list() {
		//List<GnThirdParty> thirdParties = generalService.listThirdPartiesByType(LookupConstants.THIRD_PARTY_BANK);
//		GnLookup lookup = generalService.getLookup(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_BANK));
		//renderArgs.put("pagetype", group);
		//renderArgs.put("canGoBack", false);
		//render("ThirdPartyBanks/list.html", thirdParties);
		render("ThirdPartyBanks/list.html");
	}

	@Check("administration.thirdParty.bank")
	public static void paging(Paging page){
		logger.debug("Masuk paging");
		page.addParams("tp.third_party_type",page.EQUAL,LookupConstants.THIRD_PARTY_BANK);
		page.addParams(Helper.searchAll("(tp.THIRD_PARTY_CODE||tp.THIRD_PARTY_NAME)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = generalService.pagingListThirdPartiesByType(page);
		logger.debug("json ---> "+page);
		renderJSON(page);
	}

	@Check("administration.thirdParty.bank")
	public static void view(Long id, String group, String pagetype) {
		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnThirdParty thirdParty = generalService.getThirdParty(id);
			GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
			if (!thirdParty.getThirdPartyBanks().isEmpty()){
				Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
				thirdPartyBank = tpBanks.iterator().next();
			}
			if (thirdParty.getThirdPartyAccounts()!=null){
				for (GnThirdPartyAccount gnThirdPartyAccount : thirdParty.getThirdPartyAccounts()) {
					if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)){
						if (gnThirdPartyAccount.getAccountCode()!=null)
							gnThirdPartyAccount.setCbestFlag(true);
						thirdParty.setCbestAccount(gnThirdPartyAccount);
					}
					if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS)){
						if (gnThirdPartyAccount.getAccountCode()!=null)
							gnThirdPartyAccount.setBiFlag(true);
						thirdParty.setBiAccount(gnThirdPartyAccount);
					}
				}
			}
			if (thirdParty.getAddress1()!=null){
				String[] address = thirdParty.getAddress1().split("\n");
				if (address.length==1){
					thirdParty.setAddress1Ext(address[0].trim());
				}
				if (address.length==2){
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
				}
				
				if (address.length==3){
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
					thirdParty.setAddress3Ext(address[2].trim());
				}
			}
		   	
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_BANK));
			render("ThirdPartyBanks/detail.html", thirdParty, thirdPartyBank, mode);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}
	
	@Check("administration.thirdParty.bank")
	public static void entry(){
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnThirdParty thirdParty = new GnThirdParty();
		thirdParty.setCbestAccount(new GnThirdPartyAccount());
		thirdParty.setBiAccount(new GnThirdPartyAccount());
		thirdParty.getCbestAccount().setCbestFlag(true);
		thirdParty.getBiAccount().setBiFlag(true);
		GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
		thirdParty.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		thirdParty.setIsActive(false);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_BANK));
		render("ThirdPartyBanks/detail.html", thirdParty,thirdPartyBank, mode);
	}
	
	public static void edit(Long id){
		try {
			String mode = UIConstants.DISPLAY_MODE_EDIT;
			GnThirdParty thirdParty = generalService.getThirdParty(id);
			String status = thirdParty.getRecordStatus();
			logger.debug("third party = " +thirdParty.getThirdPartyBanks());
			GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
			if (!thirdParty.getThirdPartyBanks().isEmpty()){
				Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
				thirdPartyBank = tpBanks.iterator().next();
			}
			if (thirdParty.getThirdPartyAccounts()!=null){
				for (GnThirdPartyAccount gnThirdPartyAccount : thirdParty.getThirdPartyAccounts()) {
					if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)){
						if (gnThirdPartyAccount.getAccountCode()!=null)
							gnThirdPartyAccount.setCbestFlag(true);
						thirdParty.setCbestAccount(gnThirdPartyAccount);
					}
					if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS)){
						if (gnThirdPartyAccount.getAccountCode()!=null)
							gnThirdPartyAccount.setBiFlag(true);
						thirdParty.setBiAccount(gnThirdPartyAccount);
					}
				}
			}
			if (thirdParty.getAddress1()!=null){
				String[] address = thirdParty.getAddress1().split("\n");
				logger.debug("address = " +address.length);
				if (address.length==1){
					thirdParty.setAddress1Ext(address[0].trim());
				}
				if (address.length==2){
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
				}
				
				if (address.length==3){
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
					thirdParty.setAddress3Ext(address[2].trim());
				}
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_BANK));
			render("ThirdPartyBanks/detail.html", thirdParty, thirdPartyBank, mode, status);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}
	
	public static void save(GnThirdParty thirdParty, GnThirdPartyBank thirdPartyBank, String mode, String status){
		
		if (thirdParty != null){
			validation.required("Code is", thirdParty.getThirdPartyCode());
			validation.required("Name is", thirdParty.getThirdPartyName());
			validation.required("Bank Code(SKN) is", thirdPartyBank.getBankCode2());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_BANK));
				render("ThirdPartyBanks/detail.html", thirdParty,thirdPartyBank, mode);
			} else {
				Set<GnThirdPartyBank> tpBanks = new HashSet<GnThirdPartyBank>();
				if (thirdPartyBank!=null){
					tpBanks.add(thirdPartyBank);
				}
				thirdParty.setThirdPartyBanks(tpBanks);
				Long id = thirdParty.getThirdPartyKey();
				serializerService.serialize(session.getId(), id, thirdParty);
				confirming(id, mode, status);
			}
		} else {
			//flash.error("argument.null", thirdParty);
			entry();
		}
	}
	
	public static void confirming(Long id, String mode, String status){
		try {
			renderArgs.put("confirming", true);
			GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
			Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
			GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_BANK));
			render("ThirdPartyBanks/detail.html", thirdParty,thirdPartyBank, mode,status);
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
		}
	}
	public static void confirm(GnThirdParty thirdParty, GnThirdPartyBank thirdPartyBank, String mode, String status){
		try {
			if(thirdPartyBank == null)
				back(null, mode, status);
			Set<GnThirdPartyBank> tpBanks = new HashSet<GnThirdPartyBank>();
			if (thirdPartyBank!=null){
				tpBanks.add(thirdPartyBank);
			}
			thirdParty.setThirdPartyBanks(tpBanks);
			thirdParty.setThirdPartyType(new GnLookup(LookupConstants.THIRD_PARTY_BANK));
			generalService.saveThirdParty(MenuConstants.GN_THIRD_PARTY_BANK, thirdParty, session.get(UIConstants.SESSION_USERNAME), "",  session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Third Party Code : ' " + thirdParty.getThirdPartyCode()+ " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_BANK));
			render("ThirdPartyBanks/detail.html", thirdParty,thirdPartyBank, mode,status, confirming);
		}
	}
	public static void back(Long id, String mode, String status){
		GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
		Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
		GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_BANK));
		render("ThirdPartyBanks/detail.html", thirdParty,thirdPartyBank, mode,status);
	}
	
	
	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			boolean approvalMode = true;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnThirdParty thirdParty = json.readValue(maintenanceLog.getNewData(), GnThirdParty.class);
			GnLookup lookup = generalService.getLookup(thirdParty.getThirdPartyType().getLookupId());
			Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
			GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();
			
			for (GnThirdPartyAccount gnThirdPartyAccount : thirdParty.getThirdPartyAccounts()) {
				if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)){
					if (gnThirdPartyAccount.getAccountCode()!=null)
						gnThirdPartyAccount.setCbestFlag(true);
					thirdParty.setCbestAccount(gnThirdPartyAccount);
				}
				if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS)){
					if (gnThirdPartyAccount.getAccountCode()!=null)
						gnThirdPartyAccount.setBiFlag(true);
					thirdParty.setBiAccount(gnThirdPartyAccount);
				}
			}
			if (thirdParty.getAddress1()!=null){
				String[] address = thirdParty.getAddress1().split("\n");
				if (address.length==1){
					thirdParty.setAddress1Ext(address[0].trim());
				}
				if (address.length==2){
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
				}
				
				if (address.length==3){
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
					thirdParty.setAddress3Ext(address[2].trim());
				}
			}
		   	if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
		   	render("ThirdPartyBanks/approval.html", thirdParty, thirdPartyBank, lookup, mode, taskId, operation, maintenanceLogKey, from, approvalMode);
		} catch (Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	public static void approve(String taskId, Long maintenanceLogKey, String operation){
		try {
			generalService.approveThirdParty(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
	
	public static void reject(String taskId, Long maintenanceLogKey, String operation){
		generalService.approveThirdParty(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);
	}
}
