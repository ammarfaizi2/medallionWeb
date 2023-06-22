package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.FaPostingProfile;
import com.simian.medallion.model.FaPostingRule;
import com.simian.medallion.model.FaPostingRuleDetail;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class PostingRules extends MedallionController {
	private static Logger log = Logger.getLogger(PostingRules.class);

	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> classification = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION);
		classification.add(0, new SelectItem(null, "-ALL-"));
		renderArgs.put("classification", classification);

		List<SelectItem> position = generalService.listLookupsForDropDownAsSelectItemPosition(UIConstants.SIMIAN_BANK_ID, LookupConstants._POSITION);
		renderArgs.put("position", position);

		List<SelectItem> postingTreated = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._POSTING_TREATED);
		renderArgs.put("postingTreated", postingTreated);

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		String recordStatusNew = LookupConstants.__RECORD_STATUS_NEW;
		renderArgs.put("recordStatusNew", recordStatusNew);

		String recordStatusUpdate = LookupConstants.__RECORD_STATUS_UPDATED;
		renderArgs.put("recordStatusUpdate", recordStatusUpdate);
	}

	@Check("administration.postingProfile")
	public static void list(long profileId) {
		log.debug("list. profileId: " + profileId);

		// List<FaPostingRule> faPostingRules =
		// fundService.listFaPostingRule(profileId);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
		render(profileId, params);
	}

	@Check("administration.postingProfile")
	public static void paging(Paging page, String id) {
		log.debug("paging. page: " + page + " id: " + id);

		page.addParams("1", page.EQUAL, 1);
		page.addParams("pp.faPostingProfile.postingProfileKey ", page.EQUAL, Long.parseLong(id));
		page.addParams(Helper.searchAll("(pp.postingRuleCode||pp.description)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = fundService.pagingFaPostingRule(page);
		log.debug("json ---> " + page);
		renderJSON(page);
	}

	@Check("administration.postingProfile")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaPostingRule faPostingRule = null;
		// Set<ChargeItem> items = profile.getChargeItems();
		String posrulDetails = null;
		if (id != null) {
			faPostingRule = fundService.getFaPostingRule(id);
			if (faPostingRule != null) {
				try {
					JsonHelper json = new JsonHelper().getFaPostingRuleSerializer();
					posrulDetails = json.serialize(faPostingRule.getFaPostingRuleDetails());
				} catch (JsonGenerationException ex) {
					log.debug("json.serialize");
				} catch (JsonMappingException ex) {
					log.debug("json.serialize");
				} catch (IOException ex) {
					log.debug("json.serialize");
				}
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
		render("PostingRules/detail.html", faPostingRule, mode, posrulDetails);

	}

	@Check("administration.postingProfile")
	public static void entry(long profileId) {
		log.debug("entry. profileId: " + profileId);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaPostingRule faPostingRule = new FaPostingRule();
		faPostingRule.setAutoReversal(true);
		FaPostingProfile faPostingProfile = fundService.getFaPostingProfile(profileId);
		faPostingRule.setFaPostingProfile(faPostingProfile);
		// Set<FaPostingRuleDetail> faPostingRuleDetails =
		// faPostingRule.getFaPostingRuleDetails();
		String posrulDetails = null;
		try {
			posrulDetails = json.writeValueAsString(faPostingRule.getFaPostingRuleDetails());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
		render("PostingRules/detail.html", faPostingRule, mode, faPostingProfile, posrulDetails);
	}

	@Check("administration.postingProfile")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		FaPostingRule faPostingRule = null;
		String posrulDetails = null;
		String status = null;
		if (id != null) {
			faPostingRule = fundService.getFaPostingRule(id);
			status = faPostingRule.getRecordStatus();
			if (faPostingRule != null) {
				try {
					JsonHelper json = new JsonHelper().getFaPostingRuleSerializer();
					posrulDetails = json.serialize(faPostingRule.getFaPostingRuleDetails());
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
		render("PostingRules/detail.html", faPostingRule, posrulDetails, mode, status);
	}

	@Check("administration.postingProfile")
	public static void save(String mode, FaPostingRule faPostingRule, FaPostingRuleDetail[] faPostingRuleDetails, String status) {
		log.debug("save. mode: " + mode + " faPostingRule: " + faPostingRule + " faPostingRuleDetails: " + faPostingRuleDetails + " status: " + status);

		if (faPostingRuleDetails != null) {
			for (FaPostingRuleDetail faPostingRuleDetail : faPostingRuleDetails) {
				if (faPostingRuleDetail != null) {
					log.debug("posting detail Key >> " + faPostingRuleDetail.getPostingDetailKey());
					faPostingRule.getFaPostingRuleDetails().add(faPostingRuleDetail);
				}
			}
		}

		if (faPostingRule != null) {
			log.debug("As Recap = " + faPostingRule.getAsRecap());
			String posrulDetails = null;
			try {
				if (faPostingRuleDetails == null) {
					// faPostingRuleDetails = new
					// ArrayList<FaPostingRuleDetail>();
				}
				posrulDetails = json.writeValueAsString(faPostingRuleDetails);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			validation.required("Posting Rules ID is", faPostingRule.getPostingRuleCode());
			validation.required("Posting Rules Name is", faPostingRule.getDescription());
			validation.required("Instruction Type is", faPostingRule.getPostingTreated().getLookupId());
			validation.required("Transaction Template is", faPostingRule.getFaTransactionMaster().getTransactionCode());

			/*
			 * if(faPostingRule.getFaPostingRuleDetails().size() == 0){
			 * validation.required("Posting Rules Detail is",
			 * faPostingRule.getFaPostingRuleDetails()); }
			 */

			for (FaPostingRuleDetail faPostingRuleDetail : faPostingRuleDetails) {
				if ((faPostingRuleDetail.getPostingDetailKey() == null) && (!faPostingRule.getPostingRuleCode().equals("")) && (!faPostingRule.getDescription().equals("")) && (!faPostingRule.getPostingTreated().getLookupId().equals("")) && (!faPostingRule.getFaTransactionMaster().getTransactionCode().equals(""))) {
					validation.clear();
				}
			}
			if (validation.hasErrors()) {
				mode = UIConstants.DISPLAY_MODE_ENTRY;
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
				render("PostingRules/detail.html", faPostingRule, faPostingRuleDetails, mode, posrulDetails, status);
			} else {

				Long id = faPostingRule.getPostingRuleKey();
				log.debug("check1 : " + id);
				String tes = serializerService.serialize(session.getId(), faPostingRule.getPostingRuleKey(), faPostingRule);
				log.debug("check : " + tes);
				confirming(id, mode, status);
			}
		} else {
			flash.error(ExceptionConstants.PARAMETER_NULL, faPostingRule);
		}
	}

	@Check("administration.postingProfile")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		boolean confirming = true;
		FaPostingRule faPostingRule = serializerService.deserialize(session.getId(), id, FaPostingRule.class);
		String posrulDetails = null;
		try {
			posrulDetails = json.writeValueAsString(faPostingRule.getFaPostingRuleDetails());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
		render("PostingRules/detail.html", faPostingRule, mode, posrulDetails, confirming, id, status);
	}

	@Check("administration.postingProfile")
	public static void confirm(FaPostingRule faPostingRule, FaPostingRuleDetail[] faPostingRuleDetails, String mode, String status) {
		log.debug("confirm. faPostingRule: " + faPostingRule + " faPostingRuleDetails: " + faPostingRuleDetails + " mode: " + mode + " status: " + status);

		if (faPostingRuleDetails == null) {
			// faPostingRuleDetails = new ArrayList<FaPostingRuleDetail>();
		}
		String posrulDetails = null;
		try {
			posrulDetails = json.writeValueAsString(faPostingRuleDetails);
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}

		try {
			fundService.listFaPostingRule(faPostingRule.getFaPostingProfile().getPostingProfileKey());
			// line 212-220 di comment sementara karenan menimbulkan error
			// incorrect value
			// for(FaPostingRule posRulInTable : posRuls){
			// if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)){
			// if
			// (posRulInTable.getFaPostingProfile().getPostingProfileKey().equals(faPostingRule.getFaPostingProfile().getPostingProfileKey())){
			// if
			// (posRulInTable.getPostingRuleCode().equals(faPostingRule.getPostingRuleCode())){
			// throw new MedallionException(ExceptionConstants.DATA_DUPLICATE);
			// }
			// }
			// }
			// }

			if (faPostingRule.getAutoReversal() == null) {
				faPostingRule.setAutoReversal(false);
			}

			if (faPostingRule.getAsRecap() == null) {
				faPostingRule.setAsRecap(false);
			}

			if (faPostingRuleDetails != null) {
				faPostingRule.setFaPostingRuleDetails(new HashSet<FaPostingRuleDetail>());
				for (FaPostingRuleDetail item : faPostingRuleDetails) {
					faPostingRule.getFaPostingRuleDetails().add(item);
				}
			}

			fundService.saveFaPostingRule(MenuConstants.FA_POSTING_RULE_A, faPostingRule, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list(faPostingRule.getFaPostingProfile().getPostingProfileKey());
		} catch (MedallionException e) {
			flash.error("Posting Rule ID : '" + faPostingRule.getPostingRuleCode() + "' with Profile : '" + faPostingRule.getFaPostingProfile().getProfileName() + "' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
			render("PostingRules/detail.html", faPostingRule, faPostingRuleDetails, posrulDetails, mode, confirming, status);
		}
	}

	@Check("administration.postingProfile")
	public static void back(Long id, String mode, String param, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " param: " + param + " status: " + status);

		FaPostingRule faPostingRule = serializerService.deserialize(session.getId(), id, FaPostingRule.class);
		log.debug("faPostingRule>> " + faPostingRule);
		Set<FaPostingRuleDetail> faPostingRuleDetails = faPostingRule.getFaPostingRuleDetails();
		log.debug("faPostingRuleDetails >> " + faPostingRuleDetails);
		String posrulDetails = null;
		try {
			posrulDetails = json.writeValueAsString(faPostingRuleDetails);
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
		renderTemplate("PostingRules/detail.html", faPostingRule, posrulDetails, mode, status);
	}

	// @Check("administration.postingProfile")
	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			FaPostingRule faPostingRule = json.readValue(maintenanceLog.getNewData(), FaPostingRule.class);
			String posrulDetails = null;
			try {
				posrulDetails = json.writeValueAsString(faPostingRule.getFaPostingRuleDetails());
			} catch (JsonGenerationException ex) {
				log.debug("json.serialize");
			} catch (JsonMappingException ex) {
				log.debug("json.serialize");
			} catch (IOException ex) {
				log.debug("json.serialize");
			}
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("PostingRules/approval.html", faPostingRule, posrulDetails, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// @Check("administration.postingProfile")
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFaPostingRule(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("workflow.approval")
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFaPostingRule(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}