package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CpPortfolio;
import com.simian.medallion.model.CpPortfolioDetail;
import com.simian.medallion.model.GnMaintenanceLog;

@With(Secure.class)
public class CompliancePortfolio extends MedallionController {
	private static Logger log = Logger.getLogger(CompliancePortfolio.class);

	private static JsonHelper jsonPortfolioDetail = new JsonHelper().getCpPortfolioLimitDetailSerializer();

	@Before
	public static void setup() {
		log.debug("setup");

		renderArgs.put("operators", UIHelper.yesNoOperators());
	}

	@Check("administration.compliancePortfolio")
	public static void list() {
		log.debug("list");

		List<CpPortfolio> portfolios = generalService.listPortfolio();
		render("CompliancePortfolio/list.html", portfolios);
	}

	@Check("administration.compliancePortfolio")
	public static void entry() {
		log.debug("entry");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;

		CpPortfolio porto = new CpPortfolio();
		String portoDetails = null;
		porto.setActive(new Boolean(false));
		try {
			portoDetails = jsonPortfolioDetail.serialize(porto.getPortfolioDetails());
			portoDetails = portoDetails.replaceAll("\'", "&#39;");// handle jika
																	// ada error
																	// single
																	// quote
																	// (#3554)
		} catch (JsonGenerationException ex) {
			log.error("json.serialize", ex);
		} catch (JsonMappingException ex) {
			log.error("json.serialize", ex);
		} catch (IOException ex) {
			log.error("json.serialize", ex);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PORTFOLIO));
		render("CompliancePortfolio/entry.html", porto, portoDetails, mode);
	}

	@Check("administration.compliancePortfolio")
	public static void view(Long id) {
		log.debug("view id=" + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CpPortfolio porto = generalService.getPortfolio(id);
		String portoDetails = null;

		try {
			portoDetails = jsonPortfolioDetail.serialize(porto.getPortfolioDetails());
			portoDetails = portoDetails.replaceAll("\'", "&#39;");// handle jika
																	// ada error
																	// single
																	// quote
																	// (#3554)
		} catch (JsonGenerationException ex) {
			log.error("json.serialize", ex);
		} catch (JsonMappingException ex) {
			log.error("json.serialize", ex);
		} catch (IOException ex) {
			log.error("json.serialize", ex);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PORTFOLIO));
		render("CompliancePortfolio/entry.html", porto, portoDetails, mode);
	}

	@Check("administration.compliancePortfolio")
	public static void edit(Long id) {
		log.debug("edit id=" + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;

		CpPortfolio porto = generalService.getPortfolio(id);
		String portoDetails = null;
		try {
			portoDetails = jsonPortfolioDetail.serialize(porto.getPortfolioDetails());
			portoDetails = portoDetails.replaceAll("\'", "&#39;");// handle jika
																	// ada error
																	// single
																	// quote
																	// (#3554)
		} catch (JsonGenerationException ex) {
			log.error("json.serialize", ex);
		} catch (JsonMappingException ex) {
			log.error("json.serialize", ex);
		} catch (IOException ex) {
			log.error("json.serialize", ex);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PORTFOLIO));
		render("CompliancePortfolio/entry.html", porto, portoDetails, mode);
	}

	@Check("administration.compliancePortfolio")
	public static void save(CpPortfolio porto, CpPortfolioDetail[] portfolioDetails, String mode) {
		if (porto == null)
			entry();
		log.debug("save id=" + porto + ", mode=" + mode);
		List<CpPortfolioDetail> lstPortfolioDetails = new ArrayList<CpPortfolioDetail>();
		porto.setPortfolioDetails(new HashSet<CpPortfolioDetail>(0));

		if ((portfolioDetails != null) && (portfolioDetails.length > 0)) {
			for (CpPortfolioDetail cpPortfolioDetail : portfolioDetails) {
				lstPortfolioDetails.add(cpPortfolioDetail);

				porto.getPortfolioDetails().add(cpPortfolioDetail);
			}
		}

		if (validation.errorsMap().values().containsAll(lstPortfolioDetails) == false) {
			validation.clear();
		}

		validation.required("Rule Code", porto.getComplianceRule().getRuleCode());

		if ((porto.getPortfolioDetails() == null) || (porto.getPortfolioDetails().size() == 0)) {
			validation.required("Positive Securities Rules", porto.getPortfolioDetails());
		}

		if ((lstPortfolioDetails != null) && (lstPortfolioDetails.size() > 0)) {
			for (CpPortfolioDetail cpPortfolioDetail : lstPortfolioDetails) {
				if (cpPortfolioDetail.getMaximumRange().compareTo(cpPortfolioDetail.getMinimumRange()) < 0) {
					validation.addError("", "Maximum for " + cpPortfolioDetail.getSecurity().getSecurityType().getSecurityType() + " should be greater than Minimum");
				}
			}
		}

		if (validation.hasErrors()) {
			String portoDetails = null;

			if (lstPortfolioDetails == null) {
				lstPortfolioDetails = new ArrayList<CpPortfolioDetail>();
			}
			try {
				portoDetails = jsonPortfolioDetail.serialize(lstPortfolioDetails);
			} catch (JsonGenerationException ex) {
				log.error("json.serialize", ex);
			} catch (JsonMappingException ex) {
				log.error("json.serialize", ex);
			} catch (IOException ex) {
				log.error("json.serialize", ex);
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PORTFOLIO));
			render("CompliancePortfolio/entry.html", porto, portoDetails, mode);
		} else {
			Long id = porto.getPortfolioLimitKey();
			serializerService.serialize(session.getId(), id, porto);
			confirming(id, mode);
		}
	}

	@Check("administration.compliancePortfolio")
	public static void confirming(Long id, String mode) {
		log.debug("confirming id=" + id + ", mode=" + mode);

		renderArgs.put("confirming", true);
		CpPortfolio porto = serializerService.deserialize(session.getId(), id, CpPortfolio.class);
		String portoDetails = null;
		try {
			portoDetails = jsonPortfolioDetail.serialize(porto.getPortfolioDetails());
			portoDetails = portoDetails.replaceAll("\'", "&#39;");// handle jika
																	// ada error
																	// single
																	// quote
																	// (#3554)
		} catch (JsonGenerationException ex) {
			log.error("json.serialize", ex);
		} catch (JsonMappingException ex) {
			log.error("json.serialize", ex);
		} catch (IOException ex) {
			log.error("json.serialize", ex);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PORTFOLIO));
		render("CompliancePortfolio/entry.html", porto, mode, portoDetails);
	}

	@Check("administration.compliancePortfolio")
	public static void confirm(CpPortfolio porto, CpPortfolioDetail[] portfolioDetails, String mode) {
		log.debug("confirm porto=" + porto + ", mode=" + mode);
		if (porto == null)
			back(null, mode);
		try {
			if ((portfolioDetails != null) && (portfolioDetails.length > 0)) {
				for (CpPortfolioDetail cpPortfolioDetail : portfolioDetails) {
					porto.getPortfolioDetails().add(cpPortfolioDetail);
				}
			}
			validation.clear();
			generalService.savePortfolio(MenuConstants.CP_PORTFOLIO, porto, session.get("username"), session.get("userKey"), null);
			mode = UIConstants.DISPLAY_MODE_CONFIRM;

			list();
		} catch (MedallionException ex) {

			flash.error("Rule ID : ' " + porto.getComplianceRule().getRuleCode() + " ' " + Messages.get(ex.getMessage()));
			renderArgs.put("confirming", true);

			String portoDetails = null;
			try {
				portoDetails = jsonPortfolioDetail.serialize(porto.getPortfolioDetails());
				portoDetails = portoDetails.replaceAll("\'", "&#39;");// handle
																		// jika
																		// ada
																		// error
																		// single
																		// quote
																		// (#3554)
			} catch (JsonGenerationException ex1) {
				log.error("json.serialize", ex1);
			} catch (JsonMappingException ex1) {
				log.error("json.serialize", ex1);
			} catch (IOException ex1) {
				log.error("json.serialize", ex1);
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PORTFOLIO));
			render("CompliancePortfolio/entry.html", porto, portoDetails, mode);
		}
	}

	@Check("administration.compliancePortfolio")
	public static void back(Long id, String mode) {
		log.debug("back id=" + id + ", mode=" + mode);

		CpPortfolio porto = serializerService.deserialize(session.getId(), id, CpPortfolio.class);
		String portoDetails = null;
		try {
			portoDetails = jsonPortfolioDetail.serialize(porto.getPortfolioDetails());
			portoDetails = portoDetails.replaceAll("\'", "&#39;");// handle jika
																	// ada error
																	// single
																	// quote
																	// (#3554)
		} catch (JsonGenerationException ex) {
			log.error("json.serialize", ex);
		} catch (JsonMappingException ex) {
			log.error("json.serialize", ex);
		} catch (IOException ex) {
			log.error("json.serialize", ex);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PORTFOLIO));
		render("CompliancePortfolio/entry.html", porto, portoDetails, mode);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			JsonHelper jsonPorto = new JsonHelper().getCpPortfolioLimitSerializer();
			CpPortfolio porto = jsonPorto.deserialize(maintenanceLog.getNewData(), CpPortfolio.class);
			String portoDetails = null;
			portoDetails = jsonPortfolioDetail.serialize(porto.getPortfolioDetails());
			portoDetails = portoDetails.replaceAll("\'", "&#39;");// handle jika
																	// ada error
																	// single
																	// quote
																	// (#3554)
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("CompliancePortfolio/approval.html", porto, portoDetails, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		try {
			generalService.approvePortfolio(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		try {
			generalService.approvePortfolio(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
