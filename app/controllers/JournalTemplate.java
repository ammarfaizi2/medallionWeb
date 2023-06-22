package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.JournalSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.FaJournalMaster;
import com.simian.medallion.model.FaJournalTemplate;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class JournalTemplate extends MedallionController {
	private static Logger log = Logger.getLogger(JournalTemplate.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(unless = { "list", "confirm", "save" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> debitCredit = UIHelper.debitCreditOperators();
		renderArgs.put("debitCredit", debitCredit);

		List<SelectItem> yesNo = UIHelper.yesNoOperators();
		renderArgs.put("yesNo", yesNo);

	}

	@Check("administration.journalTemplate")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_JURNAL_TEMPLATE));
		render("JournalTemplate/list.html");
	}

	@Check("administration.journalTemplate")
	public static void search(JournalSearchParameters params) {
		log.debug("search. params: " + params);

		List<FaJournalMaster> faJournalMasters = fundService.searchFaJournalMaster(UIHelper.withOperator(params.idSearchOperator, params.idOperator), UIHelper.withOperator(params.nameSearchOperator, params.nameOperator));

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_JURNAL_TEMPLATE));
		render("JournalTemplate/grid.html", faJournalMasters);
	}

	@Check("administration.journalTemplate")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaJournalMaster faJournalMaster = new FaJournalMaster();
		String journalTemplates = null;
		try {
			journalTemplates = json.writeValueAsString(faJournalMaster.getFaJournalTemplates());
			log.debug(">>> transactionDetails=" + journalTemplates);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		// faTransaction.setTransactionDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_JURNAL_TEMPLATE));
		render("JournalTemplate/entry.html", faJournalMaster, mode, journalTemplates);
	}

	@Check("administration.journalTemplate")
	public static void save(FaJournalMaster faJournalMaster, FaJournalTemplate[] faJournalTemplates, String mode) {
		log.debug("save. faJournalMaster: " + faJournalMaster + " faJournalTemplates: " + faJournalTemplates + " mode: " + mode);

		if (faJournalMaster != null) {
			String journalTemplates = null;

			/*
			 * List<FaJournalTemplate> faJournalTemplates = new
			 * ArrayList<FaJournalTemplate>();
			 * 
			 * for(FaJournalTemplate faJournalTemplate : OldFaJournalTemplates)
			 * { if(faJournalTemplate.getFaCoaMaster()!=null) {
			 * faJournalTemplates.add(faJournalTemplate); } }
			 */

			validation.clear();

			try {

				if (faJournalTemplates == null) {
					validation.addError("", "Journal Template Detail Empty.");
				} else {
					int D = 0, C = 0;
					for (FaJournalTemplate faJournalTemplate : faJournalTemplates) {
						if (faJournalTemplate.getDorc() != null) {
							if (faJournalTemplate.getDorc().equalsIgnoreCase("D"))
								D = D + 1;
							else
								C = C + 1;
						}
					}

					if (D < 1 || C < 1)
						validation.addError("", "Invalid details of template.");
				}

				journalTemplates = json.writeValueAsString(faJournalTemplates);

			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			validation.required("Template ID is", faJournalMaster.getId());
			validation.required("Name is", faJournalMaster.getName());

			/*
			 * FaJournalMaster faJournalMaster2 =
			 * fundService.getFaJournalMasterById(faJournalMaster.getId());
			 * 
			 * if (faJournalMaster2 !=null && mode.equalsIgnoreCase("entry")){
			 * validation.addError("","Template ID "+faJournalMaster.getId()+
			 * " is Already Exists"); }
			 */

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_JURNAL_TEMPLATE));
				render("JournalTemplate/entry.html", faJournalMaster, faJournalTemplates, mode, journalTemplates);
			} else {
				if (faJournalTemplates != null) {
					for (FaJournalTemplate faJournalTemplate : faJournalTemplates) {
						if (faJournalTemplate.getFaCoaMaster() != null) {
							faJournalMaster.getFaJournalTemplates().add(faJournalTemplate);
						}
					}
				}
				// Validate here
				serializerService.serialize(session.getId(), faJournalMaster.getJournalMasterKey(), faJournalMaster);
				confirming(faJournalMaster.getJournalMasterKey(), mode);
			}
		} else {
			// flash.error("argument.null", faTransaction);
			entry();

		}
	}

	@Check("administration.journalTemplate")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		boolean confirming = true;
		FaJournalMaster faJournalMaster = serializerService.deserialize(session.getId(), id, FaJournalMaster.class);

		//List<FaJournalTemplate> faJournalTemplates = null;
		String journalTemplates = null;
		try {
			journalTemplates = json.writeValueAsString(faJournalMaster.getFaJournalTemplates());
			log.debug(">>> journalTemplate =" + journalTemplates);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_JURNAL_TEMPLATE));
		render("JournalTemplate/entry.html", faJournalMaster, mode, confirming, journalTemplates);
	}

	@Check("administration.journalTemplate")
	public static void confirm(FaJournalMaster faJournalMaster, FaJournalTemplate[] faJournalTemplates, String mode) {
		log.debug("confirm. faJournalMaster: " + faJournalMaster + " faJournalTemplates: " + faJournalTemplates + " mode: " + mode);

		boolean confirming = true;
		if (faJournalMaster == null) {
			back(null, mode);
		}
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			Map<String, Object> result = new HashMap<String, Object>();
			FaJournalMaster getfaJournalMaster = fundService.getFaJournalMasterById(faJournalMaster.getId());
			if (getfaJournalMaster != null) {
				result.put("status", "error");
				result.put("error", "Template ID " + faJournalMaster.getId() + " is Already Exists");
				renderJSON(result);
			}
		}

		String journalTemplate = null;
		try {
			journalTemplate = json.writeValueAsString(faJournalTemplates);
			log.debug(">>> journalTemplate=" + journalTemplate);
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}

		try {

			if (faJournalTemplates != null) {
				for (FaJournalTemplate faJournalTemplate : faJournalTemplates) {
					if (faJournalTemplate != null) {
						faJournalMaster.getFaJournalTemplates().add(faJournalTemplate);
					}
				}
			}
			// faJournalMaster.setSource(MenuConstants.FA_JURNAL_TEMPLATE);

			FaJournalMaster trx = fundService.createJournalTemplate(MenuConstants.FA_JURNAL_TEMPLATE, faJournalMaster, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
			log.debug("LENGTH DETAILNYA ADALAH = " + trx.getFaJournalTemplates().size() + " --- " + trx.getJournalMasterKey());
			if (trx.getJournalMasterKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("templateId.confirmed.successful") + trx.getId());
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_JURNAL_TEMPLATE));
				render("JournalTemplate/entry.html", trx, faJournalTemplates, journalTemplate, mode, confirming);
			}
		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if (ex != null && ex.getParams() != null) {
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			} else {
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

	@Check("administration.journalTemplate")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		FaJournalMaster faJournalMaster = serializerService.deserialize(session.getId(), id, FaJournalMaster.class);
		log.debug("faJournalMaster.getJournalMasterKey " + faJournalMaster.getJournalMasterKey());
		String journalTemplates = null;
		try {
			journalTemplates = json.writeValueAsString(faJournalMaster.getFaJournalTemplates());
			log.debug(">>> journalTemplate=" + journalTemplates);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_JURNAL_TEMPLATE));
		render("JournalTemplate/entry.html", faJournalMaster, mode, journalTemplates);
	}

	@Check("administration.journalTemplate")
	public static void clear(String mode) {
		log.debug("clear. mode: " + mode);

		CsTransaction transaction = new CsTransaction();
		transaction.setTransactionDate(new Date());
		transaction = accountService.getTransactionCharges(UIConstants.SIMIAN_BANK_ID, transaction);
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory("TRANSACTION");
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_JURNAL_TEMPLATE));
		render("Transactions/detail.html", transaction, mode, udfs);

	}

	public static void cancel(String taskId) {
		log.debug("cancel. taskId: " + taskId);
	}

	@Check("administration.journalTemplate")
	public static void edit(Long id, String taskId) {
		log.debug("edit. id: " + id + " taskId: " + taskId);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		FaJournalMaster faJournalMaster = fundService.getFaJournalMaster(id);
		List<FaJournalTemplate> faJournalTemplates = fundService.listFaJournalTemplate(id);
		String journalTemplates = null;
		try {
			journalTemplates = json.writeValueAsString(faJournalTemplates);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		if (faJournalMaster.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_NEW) || faJournalMaster.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_UPDATED)) {
			mode = UIConstants.DISPLAY_MODE_VIEW;
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_JURNAL_TEMPLATE));
		render("JournalTemplate/entry.html", faJournalMaster, journalTemplates, taskId, mode);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			FaJournalMaster faJournalMaster = json.readValue(maintenanceLog.getNewData(), FaJournalMaster.class);

			JsonHelper jsonFaJournalTemplateSerializer = new JsonHelper().getFaJournalTemplateSerializer();
			/*
			 * FaJournalMaster faJournalMaster =
			 * fundService.getFaJournalMaster(keyId);
			 */

			if ((faJournalMaster.getFaJournalTemplates() != null) && (faJournalMaster.getFaJournalTemplates().size() > 0)) {
				List<FaJournalTemplate> lstFaJournalTemplate = new ArrayList<FaJournalTemplate>(faJournalMaster.getFaJournalTemplates());
				faJournalMaster.getFaJournalTemplates().clear();

				for (FaJournalTemplate faJournalTemplate : lstFaJournalTemplate) {
					faJournalTemplate.setFaJournalMaster(faJournalMaster);
				}

				faJournalMaster.getFaJournalTemplates().addAll(lstFaJournalTemplate);
			}

			String journalTemplates = null;
			try {
				journalTemplates = jsonFaJournalTemplateSerializer.serialize(faJournalMaster.getFaJournalTemplates());
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("JournalTemplate/approval.html", faJournalMaster, taskId, mode, journalTemplates, from, operation, maintenanceLogKey);
		} catch (Exception e) {
			log.error("Fa Journal Template Approval", e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			FaJournalMaster faJournalMaster = fundService.approveJournalTemplate(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, null);

			renderJSON(Formatter.resultSuccess(Messages.get("templateId.approved", faJournalMaster.getId())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation, Long fakey) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " fakey: " + fakey);

		try {
			FaJournalMaster faJournalMaster = new FaJournalMaster();

			faJournalMaster = fundService.approveJournalTemplate(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, null);

			renderJSON(Formatter.resultSuccess(Messages.get("templateId.rejected", faJournalMaster == null ? fakey : faJournalMaster.getId())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("administration.journalTemplate")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaJournalMaster faJournalMaster = fundService.getFaJournalMaster(id);
		List<FaJournalTemplate> faJournalTemplates = fundService.listFaJournalTemplate(id);
		String journalTemplates = null;
		try {
			journalTemplates = json.writeValueAsString(faJournalTemplates);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_JURNAL_TEMPLATE));
		render("JournalTemplate/detail.html", faJournalMaster, journalTemplates, mode);
	}
}