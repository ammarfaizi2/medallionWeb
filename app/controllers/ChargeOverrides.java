package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import play.mvc.Before;
import vo.AccountSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsAccount;
import com.simian.medallion.model.CsChargeMaster;
import com.simian.medallion.model.CsChargeOverride;
import com.simian.medallion.model.CsChargeOverrideTier;
import com.simian.medallion.model.CsChargeProfile;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.vo.SelectItem;

public class ChargeOverrides extends MedallionController {
	private static Logger log = Logger.getLogger(ChargeOverrides.class);

	@Before(only = { "list", "edit", "dedupe" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);

	}

	@Before(only = { "edit", "confirming", "back", "callChargeOverrideJson", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> chargeCategory = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_CATEGORY);
		renderArgs.put("chargeCategory", chargeCategory);
		List<SelectItem> invoiceCharge = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._INVOICE_CHARGE);
		renderArgs.put("invoiceCharge", invoiceCharge);
	}

	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		render(mode, param);
	}

	public static void search(AccountSearchParameters params) {
		log.debug("search. params: " + params);

		List<CsAccount> accounts = accountService.searchAccount(UIHelper.withOperator(params.accountSearchNo, params.accountNoOperator), UIHelper.withOperator(params.accountSearchName, params.accountNameOperator));
		render("Accounts/grid.html", accounts);
	}

	public static void view(Long id, String param) {
		log.debug("view. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_CHARGE_OVERRIDE));
		callChargeOverrideJson(id, mode);

	}

	public static void entry(long customerKey, String param) {
		log.debug("entry. customerKey: " + customerKey + " param: " + param);
	}

	public static void edit(Long id, String param) {
		log.debug("edit. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsAccount account = null;
		String chargeOverrides = null;
		log.debug(">>> id=" + id);
		if (id != null) {
			account = accountService.getAccount(id);
			log.debug(">>> account=" + account);
			if (account != null) {
				try {
					chargeOverrides = json.writeValueAsString(account.getChargeOverrides());
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
			}
		}
		CsChargeProfile chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_CHARGE_OVERRIDE));
		render("ChargeOverrides/overrideCharge.html", account, chargeProfile, chargeOverrides, mode, id, param);

	}

	public static void save(CsAccount account, Long id, String body, String mode, CsChargeOverride chargeOverride, String param) {
		log.debug("save. account: " + account + " id: " + id + " body: " + body + " mode: " + mode + " chargeOverride: " + chargeOverride + " param: " + param);

		try {
			CsChargeOverride[] overrides = json.readValue(body, CsChargeOverride[].class);

			for (int i = 0; i < overrides.length; i++) {
				if (overrides[i].getChargeOverrideKey() == null) {
					Set<CsChargeOverrideTier> chargeOverrideTiers = overrides[i].getChargeOverrideTiers();
					int j = 1;
					for (CsChargeOverrideTier cot : chargeOverrideTiers) {
						cot.getId().setRowNumber(j);
						log.debug("Number : " + j);
						j++;
					}
				}
			}
			String data = serializerService.serialize(session.getId(), id, overrides);
			log.debug("data >>> " + data);
		} catch (JsonParseException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		}

	}

	public static void confirming(Long id, Long key, String mode) {
		log.debug("confirming. id: " + id + " key: " + key + " mode: " + mode);

		Boolean isChargeProfile = true;
		renderArgs.put("confirming", true);
		CsChargeOverride[] overrides = serializerService.deserialize(session.getId(), id, CsChargeOverride[].class);
		CsAccount account = null;
		String chargeOverrides = null;
		account = accountService.getAccount(id);
		log.debug(">>> account=" + account);
		try {
			chargeOverrides = json.writeValueAsString(overrides);
			log.debug(">>> chargeOverrides=" + chargeOverrides);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		CsChargeProfile chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());
		List<CsChargeMaster> chargeMasters = generalService.listChargeItemForChargeOverride(chargeProfile.getChargeProfileKey());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_CHARGE_OVERRIDE));
		render("ChargeOverrides/overrideCharge.html", account, chargeProfile, chargeOverrides, mode, id, isChargeProfile, chargeMasters);
	}

	public static void confirm(Long id, String mode, CsAccount account, String param, CsChargeOverride[] chargeOverrides, String userName, String taskId) {
		log.debug("confirm. id: " + id + " mode: " + mode + " account: " + account + " param: " + param + " chargeOverrides: " + chargeOverrides + " userName: " + userName + " taskId: " + taskId);

		// try {

		// }catch (MedallionException e) {
		// flash.error("Charge Overrides Account : ' "+
		// account.getAccountNo()+" ' " + e.getMessage());
		// boolean confirming = true;
		// Boolean isChargeProfile = true;
		// CsChargeProfile chargeProfile =
		// generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());
		// render("ChargeOverrides/overrideCharge.html", account, chargeProfile,
		// chargeOverrides, mode, id, isChargeProfile);
		// }
		chargeOverrides = serializerService.deserialize(session.getId(), id, CsChargeOverride[].class);
		log.debug("Size Confirm : " + chargeOverrides.length);
		/*
		 * for (int i=0; i < chargeOverrides.length;i++){
		 * chargeOverrides[i].setAccount(new CsAccount());
		 * chargeOverrides[i].getAccount().setCustodyAccountKey(id);
		 * 
		 * if (chargeOverrides[i].getChargeOverrideKey() == null ) {
		 * Set<CsChargeOverrideTier> chargeOverrideTiers =
		 * chargeOverrides[i].getChargeOverrideTiers();
		 * 
		 * List<CsChargeOverrideTier> newChargeOverrideTiers = new
		 * ArrayList<CsChargeOverrideTier>(chargeOverrideTiers);
		 * if(chargeOverrideTiers.size() > 1) {
		 * Collections.sort(newChargeOverrideTiers, new
		 * ChargeOverrideTierComparator()); }
		 * 
		 * for (CsChargeOverrideTier cot : newChargeOverrideTiers) {
		 * cot.setId(new CsChargeOverrideTierId());
		 * logger.debug("id Override Key : "+
		 * cot.getId().getChargeOverrideKey()); } }
		 * 
		 * }
		 */
		account = accountService.getAccount(id);
		try {

			accountService.saveChargeOverride(chargeOverrides, id, session.get("username"), "");
		} catch (MedallionException ex) {
			flash.error("Charge Overrides : account ' " + account.getAccountNo() + " ' " + ex.getMessage());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_CHARGE_OVERRIDE));
			String error = "error";
			renderText(error);
		}
	}

	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		Boolean isChargeProfile = true;
		CsChargeOverride[] overrides = serializerService.deserialize(session.getId(), id, CsChargeOverride[].class);
		CsAccount account = null;
		String chargeOverrides = null;
		account = accountService.getAccount(id);
		try {
			chargeOverrides = json.writeValueAsString(overrides);
			log.debug(">>> chargeOverrides=" + chargeOverrides);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		CsChargeProfile chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());
		List<CsChargeMaster> chargeMasters = generalService.listChargeItemForChargeOverride(chargeProfile.getChargeProfileKey());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_CHARGE_OVERRIDE));
		render("ChargeOverrides/overrideCharge.html", account, chargeProfile, chargeOverrides, mode, id, isChargeProfile, chargeMasters);
	}

	public static void callChargeOverrideJson(Long id, String mode) {
		log.debug("callChargeOverrideJson. id: " + id + " mode: " + mode);

		Boolean isChargeProfile = true;
		CsAccount account = null;
		String chargeOverrides = null;
		log.debug(">>> id=" + id);
		if (id != null) {
			account = accountService.getAccount(id);
			log.debug(">>> account=" + account);
			if (account != null) {
				try {
					chargeOverrides = json.writeValueAsString(account.getChargeOverrides());
					log.debug(">>> chargeOverrides=" + chargeOverrides);
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
			}
		}
		CsChargeProfile chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());
		render("ChargeOverrides/overrideCharge.html", account, chargeProfile, chargeOverrides, mode, id, isChargeProfile);
	}

	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " group: " + group + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			String chargeOverrides = null;
			log.debug("Maintenance log >>> " + maintenanceLogKey);
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			// CsAccount account= json.readValue(maintenanceLog.getNewData(),
			// CsAccount.class);

			CsChargeOverride[] overrides = json.readValue(maintenanceLog.getNewData(), CsChargeOverride[].class);
			CsAccount account = accountService.getAccount(keyId);
			try {
				chargeOverrides = json.writeValueAsString(overrides);
				log.debug(">>> chargeOverrides=" + chargeOverrides);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			CsChargeProfile chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());
			List<CsChargeMaster> chargeMasters = generalService.listChargeItemForChargeOverride(chargeProfile.getChargeProfileKey());
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ChargeOverrides/approval.html", account, chargeProfile, chargeOverrides, mode, taskId, operation, maintenanceLogKey, chargeMasters, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			accountService.approveCsChargeOverride(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			accountService.approveCsChargeOverride(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}