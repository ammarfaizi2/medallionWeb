package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.GnThirdPartyAccount;
import com.simian.medallion.model.GnThirdPartyBank;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import play.i18n.Messages;
import play.mvc.Before;

public class ThirdPartyCounters extends MedallionController {
	private static Logger log = Logger.getLogger(ThirdPartyCounters.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);	
		
		List<SelectItem> ctpParticipan = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID,LookupConstants._CTP_PARTICIPAN);
		renderArgs.put("ctpParticipan", ctpParticipan);
		
		List<SelectItem> accountCategory = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID,LookupConstants._DEPOSITORY_CODE);
		renderArgs.put("accountCategory", accountCategory);
	}

	@Check("administration.thirdParty.counterparty")
	public static void list() {
		log.debug("list. ");

		// List<GnThirdParty> thirdParties =
		// generalService.listThirdPartiesByType(LookupConstants.THIRD_PARTY_COUNTER_PART);
		// GnLookup lookup = generalService.getLookup(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY));
		// renderArgs.put("pagetype", group);
		// renderArgs.put("canGoBack", false);
		// render("ThirdPartyCounters/list.html", thirdParties);
		render("ThirdPartyCounters/list.html");
	}

	@Check("administration.thirdParty.bank")
	public static void paging(Paging page) {
		log.debug("paging. page: " + page);

		page.addParams("tp.third_party_type", page.EQUAL, LookupConstants.THIRD_PARTY_COUNTER_PART);
		page.addParams(Helper.searchAll("(tp.THIRD_PARTY_CODE||tp.THIRD_PARTY_NAME)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = generalService.pagingListThirdPartiesByType(page);
		renderJSON(page);
	}

	@Check("administration.thirdParty.counterparty")
	public static void view(Long id, String group, String pagetype) {
		log.debug("view. id: " + id + " group: " + group + " pagetype: " + pagetype);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnThirdParty thirdParty = generalService.getThirdParty(id);
			GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
			if (!thirdParty.getThirdPartyBanks().isEmpty()) {
				Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
				thirdPartyBank = tpBanks.iterator().next();
			}
/*
			if (thirdParty.getThirdPartyAccounts() != null) {
				for (GnThirdPartyAccount gnThirdPartyAccount : thirdParty.getThirdPartyAccounts()) {
					if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)) {
						if (gnThirdPartyAccount.getAccountCode() != null || gnThirdPartyAccount.getAccountAgentCode() != null)
							gnThirdPartyAccount.setCbestFlag(true);
						thirdParty.setCbestAccount(gnThirdPartyAccount);
					}
					if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS)) {
						if (gnThirdPartyAccount.getAccountCode() != null)
							gnThirdPartyAccount.setBiFlag(true);
						thirdParty.setBiAccount(gnThirdPartyAccount);
					}
				}
			}
*/
			for (GnThirdPartyAccount acc : thirdParty.getThirdPartyAccounts()) {
				acc.setAccountCategory(acc.getAccountCategory() == null ? new GnLookup() : generalService.getLookup(acc.getAccountCategory().getLookupId()));
			}
			
			String thirdPartyAccounts = null;
			try {
				JsonHelper json = new JsonHelper().getThirdPartyAccountSerializer();
				thirdPartyAccounts = json.serialize(thirdParty.getThirdPartyAccounts());
			} catch (JsonGenerationException e) {
				log.error( "json.serialize");
			} catch (JsonMappingException e) {
				log.error( "json.serialize");
			} catch (IOException e) {
				log.error( "json.serialize");
			}
			
			if (thirdParty.getAddress1() != null) {
				String[] address = thirdParty.getAddress1().split("\n");
				if (address.length == 1) {
					thirdParty.setAddress1Ext(address[0].trim());
				}
				if (address.length == 2) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
				}

				if (address.length == 3) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
					thirdParty.setAddress3Ext(address[2].trim());
				}
			}
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY));
			render("ThirdPartyCounters/detail.html", thirdParty, thirdPartyBank, thirdPartyAccounts, mode);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.thirdParty.counterparty")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnThirdParty thirdParty = new GnThirdParty();
		GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
		thirdParty.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		thirdParty.setIsActive(false);
		
		//TODO Add by Fadly Redmine #3457
		for (GnThirdPartyAccount acc : thirdParty.getThirdPartyAccounts()) {
			acc.setAccountCategory(acc.getAccountCategory() == null ? new GnLookup() : generalService.getLookup(acc.getAccountCategory().getLookupId()));
		}
		
		String thirdPartyAccounts = null;
		try {
			JsonHelper json = new JsonHelper().getThirdPartyAccountSerializer();
			thirdPartyAccounts = json.serialize(thirdParty.getThirdPartyAccounts());
		} catch (JsonGenerationException e) {
			log.error( "json.serialize");
		} catch (JsonMappingException e) {
			log.error( "json.serialize");
		} catch (IOException e) {
			log.error( "json.serialize");
		} 
		
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY));
		render("ThirdPartyCounters/detail.html", thirdParty, thirdPartyBank, thirdPartyAccounts, mode);
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		try {
			String mode = UIConstants.DISPLAY_MODE_EDIT;
			GnThirdParty thirdParty = generalService.getThirdParty(id);
			String status = thirdParty.getRecordStatus();
			log.debug("third party = " + thirdParty.getThirdPartyBanks());
			GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
			if (!thirdParty.getThirdPartyBanks().isEmpty()) {
				Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
				thirdPartyBank = tpBanks.iterator().next();
			}
			
			//TODO Add by Fadly Redmine #3457
			for (GnThirdPartyAccount acc : thirdParty.getThirdPartyAccounts()) {
				acc.setAccountCategory(acc.getAccountCategory() == null ? new GnLookup() : generalService.getLookup(acc.getAccountCategory().getLookupId()));
			}
			
			String thirdPartyAccounts = null;
			try {
				JsonHelper json = new JsonHelper().getThirdPartyAccountSerializer();
				thirdPartyAccounts = json.serialize(thirdParty.getThirdPartyAccounts());
			} catch (JsonGenerationException e) {
				log.error( "json.serialize");
			} catch (JsonMappingException e) {
				log.error( "json.serialize");
			} catch (IOException e) {
				log.error( "json.serialize");
			} 
			
			if (thirdParty.getAddress1() != null) {
				String[] address = thirdParty.getAddress1().split("\n");
				log.debug("address = " + address.length);
				if (address.length == 1) {
					thirdParty.setAddress1Ext(address[0].trim());
				}
				if (address.length == 2) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
				}

				if (address.length == 3) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
					thirdParty.setAddress3Ext(address[2].trim());
				}
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY));
			render("ThirdPartyCounters/detail.html", thirdParty, thirdPartyBank, thirdPartyAccounts, mode, status);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	public static void save(GnThirdParty thirdParty, GnThirdPartyBank thirdPartyBank,  GnThirdPartyAccount[] thirdPartyAccountss, String mode, String status) {
		log.debug("save. thirdParty: " + thirdParty + " thirdPartyBank: " + thirdPartyBank + " mode: " + mode + " status: " + status);

		if (thirdParty != null) {
			
			//TODO Add By Fadly 2017-08-12 #redmine 3457 save thirdPartyAccount
			ArrayList<GnThirdPartyAccount> thirdPartyAcct = new ArrayList<GnThirdPartyAccount>();
			String thirdPartyAccounts = null;
			try {
				JsonHelper json = new JsonHelper().getThirdPartyAccountSerializer();
				if (thirdPartyAccountss==null){
					thirdPartyAccounts = json.serialize(thirdPartyAcct);
				}else{
					thirdPartyAccounts = json.serialize(thirdPartyAccountss);	
				}
			} catch (JsonGenerationException e) {
				log.error( "json.serialize");
			} catch (JsonMappingException e) {
				log.error( "json.serialize");
			} catch (IOException e) {
				log.error( "json.serialize");
			}
			
			validation.required("Code is", thirdParty.getThirdPartyCode());
			validation.required("Name is", thirdParty.getThirdPartyName());

			/*
			 * if (thirdParty.getBiAccount().isBiFlag()) {
			 * validation.required("BI-SSSS Member Code is",
			 * thirdParty.getBiAccount().getAccountCode());
			 * validation.required("BI-SSSS Agent Code is",
			 * thirdParty.getBiAccount().getAccountAgentCode()); }
			 * 
			 * if (thirdParty.getCbestAccount().isCbestFlag() &&
			 * Helper.isNullOrEmpty(thirdParty.getCbestAccount().getAccountCode()) &&
			 * Helper.isNullOrEmpty(thirdParty.getCbestAccount().getAccountAgentCode()))
			 * validation.required("C-BEST Code or External Code ",
			 * thirdParty.getCbestAccount().getAccountCode());
			 */
			
			
			if(thirdParty.getCtpParticipantFlag()!=null) {
				if (thirdParty.getCtpParticipantFlag() == true) {
					if (Helper.isNull(thirdParty.getCtpParticipanAs().getLookupId()) || thirdParty.getCtpParticipanAs().getLookupId().equals("")) {
						validation.required("CTP Participan As is", thirdParty.getCtpParticipanAs().getLookupId());
					}
					if (Helper.isNull(thirdParty.getCtpParticipanCode()) || thirdParty.getCtpParticipanCode().equals("")) {
						validation.required("Participan Code is", thirdParty.getCtpParticipanCode());	
					}
				}
			}
			
			
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY));
				render("ThirdPartyCounters/detail.html", thirdParty, thirdPartyBank, thirdPartyAccounts, mode);
			} else {
				Set<GnThirdPartyBank> tpBanks = new HashSet<GnThirdPartyBank>();
				if (tpBanks != null) {
					tpBanks.add(thirdPartyBank);
				}
				thirdParty.setThirdPartyBanks(tpBanks);
				Long id = thirdParty.getThirdPartyKey();
				
				if (thirdPartyAccounts != null) {
					if (thirdPartyAccountss==null){
						for (GnThirdPartyAccount thirdPartyAcc : thirdPartyAcct) {
							thirdParty.getThirdPartyAccounts().add(thirdPartyAcc);
						}						
					}else{
						for (GnThirdPartyAccount thirdPartyAcc : thirdPartyAccountss) {
							if (thirdPartyAcc != null) {
								thirdParty.getThirdPartyAccounts().add(thirdPartyAcc);
							}
						}	
					}
				}
				
				serializerService.serialize(session.getId(), id, thirdParty);
				confirming(id, mode, status);
			}
		} else {
			// flash.error("argument.null", thirdParty);
			entry();
		}
	}

	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);
		
		try {
			renderArgs.put("confirming", true);
			GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
			Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
			GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();
			
			String thirdPartyAccounts = null;
			try {
				JsonHelper json = new JsonHelper().getThirdPartyAccountSerializer();
				thirdPartyAccounts = json.serialize(thirdParty.getThirdPartyAccounts());
			} catch (JsonGenerationException e) {
				log.error( "json.serialize");
			} catch (JsonMappingException e) {
				log.error( "json.serialize");
			} catch (IOException e) {
				log.error( "json.serialize");
			}
			
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY));
			render("ThirdPartyCounters/detail.html", thirdParty, thirdPartyBank,thirdPartyAccounts, mode, status);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	public static void confirm(GnThirdParty thirdParty, GnThirdPartyBank thirdPartyBank, GnThirdPartyAccount[] thirdPartyAccountss, String mode, String status) {
		log.debug("confirm. thirdParty: " + thirdParty + " thirdPartyBank: " + thirdPartyBank + " mode: " + mode + " status: " + status);
        
		if (thirdParty == null) {
			back(null, mode, status);
		}
		
		ArrayList<GnThirdPartyAccount> thirdPartyAcct = new ArrayList<GnThirdPartyAccount>();
		
		String thirdPartyAccounts = null;
		try {
			JsonHelper json = new JsonHelper().getThirdPartyAccountSerializer();
			if(thirdPartyAccountss == null){	
				thirdPartyAccounts = json.serialize(thirdPartyAcct);				
			}else{
				thirdPartyAccounts = json.serialize(thirdPartyAccountss);	
			}
		} catch (JsonGenerationException e) {
			log.error( "json.serialize");
		} catch (JsonMappingException e) {
			log.error( "json.serialize");
		} catch (IOException e) {
			log.error( "json.serialize");
		}
		
		try {
			Set<GnThirdPartyBank> tpBanks = new HashSet<GnThirdPartyBank>();
			//List<GnThirdPartyAccount> tpAccounts = new ArrayList<GnThirdPartyAccount>();
			if (thirdPartyBank != null) {
				tpBanks.add(thirdPartyBank);
			}
			/*
			 * if (thirdParty.getCbestAccount().isCbestFlag()) {
			 * thirdParty.getCbestAccount().setAccountCategory(generalService.getLookup(
			 * LookupConstants.DEPOSITORY_CODE_KSEI_CBEST));
			 * tpAccounts.add(thirdParty.getCbestAccount()); }
			 * 
			 * if (thirdParty.getBiAccount().isBiFlag()) {
			 * thirdParty.getBiAccount().setAccountCategory(generalService.getLookup(
			 * LookupConstants.DEPOSITORY_CODE_BI_BISSSS));
			 * tpAccounts.add(thirdParty.getBiAccount()); }
			 */
			
			thirdParty.setThirdPartyAccounts(new HashSet<GnThirdPartyAccount>());
			
			if (thirdPartyAccounts != null) {
				if(thirdPartyAccountss == null){
					for (GnThirdPartyAccount thirdPartyAcc : thirdPartyAcct) {
							thirdParty.getThirdPartyAccounts().add(thirdPartyAcc);
					}	
				}else{
					for (GnThirdPartyAccount thirdPartyAcc : thirdPartyAccountss) {
						if (thirdPartyAcc != null) {
							if(thirdPartyAcc.getAccountCode().equals("null")){
								thirdPartyAcc.setAccountCode("");
							}
							if(thirdPartyAcc.getAccountAgentCode().equals("null")){
								thirdPartyAcc.setAccountAgentCode("");
							}
							if(thirdPartyAcc.getSubAccountCode().equals("null")){
								thirdPartyAcc.setSubAccountCode("");
							}
							if(thirdPartyAcc.getCashAccount().equals("null")){
								thirdPartyAcc.setCashAccount("");
							}
							if(thirdPartyAcc.getInvestorCode().equals("null")){
								thirdPartyAcc.setInvestorCode("");
							}
							thirdParty.getThirdPartyAccounts().add(thirdPartyAcc);
						}
					}	
				}
			}
						
			if((thirdParty.getThirdPartyCode()==null) || (thirdParty.getThirdPartyCode().equals("")) || 
				(thirdParty.getThirdPartyName()==null) || (thirdParty.getThirdPartyName().equals(""))){
				validation.required("Code is", thirdParty.getThirdPartyCode());
				validation.required("Name is", thirdParty.getThirdPartyName());
			}else{
				if((thirdPartyAccountss==null) || (thirdParty.getThirdPartyAccounts()!=null)){
					validation.clear();
				}
			}
			
			thirdParty.setThirdPartyBanks(tpBanks);
			thirdParty.setThirdPartyType(new GnLookup(LookupConstants.THIRD_PARTY_COUNTER_PART));
			generalService.saveThirdParty(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY, thirdParty, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Third Party Code : ' " + thirdParty.getThirdPartyCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY));
			render("ThirdPartyCounters/detail.html", thirdParty, thirdPartyBank, mode, status, confirming);
		}
	}

	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
		Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
		GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();
		
		String thirdPartyAccounts = null;
		try {
			JsonHelper json = new JsonHelper().getThirdPartyAccountSerializer();
			thirdPartyAccounts = json.serialize(thirdParty.getThirdPartyAccounts());	
		} catch (JsonGenerationException e) {
			log.debug( "json.serialize");
		} catch (JsonMappingException e) {
			log.debug( "json.serialize");
		} catch (IOException e) {
			log.debug( "json.serialize");
		}
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY));
		render("ThirdPartyCounters/detail.html", thirdParty, thirdPartyBank, thirdPartyAccounts, mode, status);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			boolean approvalMode = true;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnThirdParty thirdParty = json.readValue(maintenanceLog.getNewData(), GnThirdParty.class);
			GnLookup lookup = generalService.getLookup(thirdParty.getThirdPartyType().getLookupId());
			Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
			GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();

			/*
			 * for (GnThirdPartyAccount gnThirdPartyAccount :
			 * thirdParty.getThirdPartyAccounts()) { if
			 * (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(
			 * LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)) { if
			 * (gnThirdPartyAccount.getAccountCode() != null ||
			 * gnThirdPartyAccount.getAccountAgentCode() != null)
			 * gnThirdPartyAccount.setCbestFlag(true);
			 * thirdParty.setCbestAccount(gnThirdPartyAccount); } if
			 * (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(
			 * LookupConstants.DEPOSITORY_CODE_BI_BISSSS)) { if
			 * (gnThirdPartyAccount.getAccountCode() != null)
			 * gnThirdPartyAccount.setBiFlag(true);
			 * thirdParty.setBiAccount(gnThirdPartyAccount); } }
			 */
			
			if ((thirdParty.getThirdPartyAccounts() != null) && (thirdParty.getThirdPartyAccounts().size() > 0)) {
				List<GnThirdPartyAccount> lstGnThirdPartyAccount = new ArrayList<GnThirdPartyAccount>(thirdParty.getThirdPartyAccounts());
				thirdParty.getThirdPartyAccounts().clear();
				
				for (GnThirdPartyAccount gnThirdPartyAccount : lstGnThirdPartyAccount) {
					gnThirdPartyAccount.setThirdParty(thirdParty);
				}
				
				thirdParty.getThirdPartyAccounts().addAll(lstGnThirdPartyAccount);
			}
			
			String thirdPartyAccounts = null;
			try {
				JsonHelper json = new JsonHelper().getThirdPartyAccountSerializer();
				thirdPartyAccounts = json.serialize(thirdParty.getThirdPartyAccounts());
				log.debug(thirdPartyAccounts);
			} catch (JsonGenerationException e) {
				log.error( "json.serialize");
			} catch (JsonMappingException e) {
				log.error( "json.serialize");
			} catch (IOException e) {
				log.error( "json.serialize");
			} 
			
			if (thirdParty.getAddress1() != null) {
				String[] address = thirdParty.getAddress1().split("\n");
				if (address.length == 1) {
					thirdParty.setAddress1Ext(address[0].trim());
				}
				if (address.length == 2) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
				}

				if (address.length == 3) {
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
			render("ThirdPartyCounters/approval.html", thirdParty, thirdPartyBank, lookup, mode, thirdPartyAccounts, taskId, operation, maintenanceLogKey, from, approvalMode);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveThirdParty(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveThirdParty(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}