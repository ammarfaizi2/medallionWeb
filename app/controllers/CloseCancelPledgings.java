package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.ReleasePledgingSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CsPortfolio;
import com.simian.medallion.model.PlTransaction;
import com.simian.medallion.model.PlTransactionDetail;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class CloseCancelPledgings extends MedallionController {
	private static Logger log = Logger.getLogger(CloseCancelPledgings.class);

	private static JsonHelper jsonPledgingDetail = new JsonHelper().getPledgingDetailSerializer();

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "entry", "edit", "save", "confirming", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);

		List<SelectItem> closeCancelStatus = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PLEDGING_CLOSE_CANCEL);
		renderArgs.put("closeCancelStatus", closeCancelStatus);

		List<SelectItem> accountTypePld = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ACCOUNT_TYPE_PLD);
		renderArgs.put("accountTypePld", accountTypePld);

		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION));
	}

	@Check("transaction.closeCancelPledging")
	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_RELEASE_PLEDGING));
		render("CloseCancelPledgings/list.html", param, mode);
	}

	@Check("transaction.closeCancelPledging")
	public static void search(ReleasePledgingSearchParameters params) {
		log.debug("search. params: " + params);

		List<PlTransaction> plTransactions = transactionService.searchReleasePledging(params.customerCodeSearchId, UIHelper.withOperator(params.releasePledgingSearchNoOperator, params.ReleasePledgingNoOperator), params.releasePledgingSearchDate);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_RELEASE_PLEDGING));
		render("CloseCancelPledgings/grid_releasePledging.html", plTransactions);
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	@Check("transaction.closeCancelPledging")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		PlTransaction cancelPledging = new PlTransaction();
		String detailCancelPledgings = null;
		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		try {
			detailCancelPledgings = json.writeValueAsString(cancelPledging.getPlTransactionDetails());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_CLOSE_CANCEL_PLEDGING));
		render("CloseCancelPledgings/entry.html", cancelPledging, mode, detailCancelPledgings, currentBusinessDate);
	}

	@Check("transaction.closeCancelPledging")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		PlTransaction cancelPledging = transactionService.getPledging(id);
		String detailCancelPledgings = null;
		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		try {
			// detailReleasePledgings =
			// json.writeValueAsString(releasePledging.getPlTransactionDetails());
			detailCancelPledgings = jsonPledgingDetail.serialize(cancelPledging.getPlTransactionDetails());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_RELEASE_PLEDGING));
		render("CloseCancelPledgings/entry.html", cancelPledging, mode, detailCancelPledgings, currentBusinessDate);
	}

	@Check("transaction.closeCancelPledging")
	public static void save(String mode, PlTransaction cancelPledging, List<PlTransactionDetail> pledgingDetails) {
		log.debug("save. mode: " + mode + " cancelPledging: " + cancelPledging + " pledgingDetails: " + pledgingDetails);

		if (pledgingDetails != null) {
			for (PlTransactionDetail pledgingDetail : pledgingDetails) {
				cancelPledging.getPlTransactionDetails().add(pledgingDetail);
			}
		}

		if (cancelPledging != null) {
			String detailCancelPledgings = null;
			try {
				if (pledgingDetails == null)
					pledgingDetails = new ArrayList<PlTransactionDetail>();
				// detailCancelPledgings =
				// json.writeValueAsString(pledgingDetails);
				detailCancelPledgings = jsonPledgingDetail.serialize(pledgingDetails);

			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			if (validation.errorsMap().values().containsAll(pledgingDetails) == false)
				validation.clear();
			log.debug("SIZE PL TRANS DETAIL SAVE = " + pledgingDetails.size());
			validation.required("cancelPledging.closeCancelDate", cancelPledging.getCloseCancelDate());
			validation.required("cancelPledging.closeCancelStatus.lookupId", cancelPledging.getCloseCancelStatus().getLookupId());
			if (mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_ENTRY)) {
				validation.required("cancelPledging.customer.customerNo", cancelPledging.getCustomer().getCustomerNo());
				validation.required("cancelPledging.transactionKey", cancelPledging.getTransactionKey());
			}
			log.debug("VALIDATION = " + validation.errorsMap());

			if (validation.hasErrors()) {
				// mode = UIConstants.DISPLAY_MODE_ENTRY;
				Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_RELEASE_PLEDGING));
				render("CloseCancelPledgings/entry.html", cancelPledging, pledgingDetails, detailCancelPledgings, mode, currentBusinessDate);
			} else {
				log.debug(">>>>>>>>>>>>>>>>>>>>>>");
				Long id = cancelPledging.getTransactionKey();
				String data = serializerService.serialize(session.getId(), cancelPledging.getTransactionKey(), cancelPledging);
				log.debug("DATA = " + data);
				confirming(id, mode);
			}
		} else {
			flash.error(ExceptionConstants.PARAMETER_NULL, cancelPledging);
		}
	}

	@Check("transaction.closeCancelPledging")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		boolean confirming = true;
		PlTransaction cancelPledging = serializerService.deserialize(session.getId(), id, PlTransaction.class);
		String detailCancelPledgings = null;
		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		try {
			// detailCancelPledgings =
			// json.writeValueAsString(cancelPledging.getPlTransactionDetails());
			detailCancelPledgings = jsonPledgingDetail.serialize(cancelPledging.getPlTransactionDetails());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_RELEASE_PLEDGING));
		render("CloseCancelPledgings/entry.html", cancelPledging, mode, detailCancelPledgings, confirming, id, currentBusinessDate);
	}

	@Check("transaction.closeCancelPledging")
	public static void confirm(PlTransaction cancelPledging, List<PlTransactionDetail> pledgingDetails, String mode) {
		log.debug("confirm. cancelPledging: " + cancelPledging + " pledgingDetails: " + pledgingDetails + " mode: " + mode);

		if (pledgingDetails == null)
			pledgingDetails = new ArrayList<PlTransactionDetail>();

//		String detailCancelPledgings = null;
//		try {
//			detailCancelPledgings = json.writeValueAsString(pledgingDetails);
//		} catch (JsonGenerationException e) {
//			log.debug("json.serialize");
//		} catch (JsonMappingException e) {
//			log.debug("json.serialize");
//		} catch (IOException e) {
//			log.debug("json.serialize");
//		}

		try {
			if (pledgingDetails != null) {
				for (PlTransactionDetail pledgingDetail : pledgingDetails) {
					cancelPledging.getPlTransactionDetails().add(pledgingDetail);
				}
			}
			//boolean confirming = true;

			transactionService.createCancelPledging(MenuConstants.PL_RELEASE_PLEDGING, cancelPledging, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
			/*
			 * if (pl.getTransactionKey() != null){ Map<String, Object> result =
			 * new HashMap<String, Object>(); result.put("status", "success");
			 * result.put("message",
			 * Messages.get("transaction.confirmed.successful",
			 * pl.getTransactionKey())); renderJSON(result); } else {
			 * flash.put(UIConstants.BREADCRUMB,
			 * applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
			 * render("CloseCancelPledgings/entry.html", pl, mode, confirming);
			 * }
			 */
			/*
			 * } catch (MedallionException ex) { Map<String, Object> result =
			 * new HashMap<String, Object>(); result.put("status", "error");
			 * List<String> params = new ArrayList<String>(); for (Object param
			 * : ex.getParams()) { params.add(Messages.get(param)); }
			 * flash.put(UIConstants.BREADCRUMB,
			 * applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
			 * result.put("error", Messages.get("error." + ex.getErrorCode(),
			 * params.toArray())); renderJSON(result);
			 */
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		}

		// entry();
		list(null, null);
	}

	@Check("transaction.closeCancelPledging")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		PlTransaction cancelPledging = serializerService.deserialize(session.getId(), id, PlTransaction.class);
		Set<PlTransactionDetail> pledgingDetails = cancelPledging.getPlTransactionDetails();
		String detailCancelPledgings = null;
		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		try {
			// detailCancelPledgings = json.writeValueAsString(pledgingDetails);
			detailCancelPledgings = jsonPledgingDetail.serialize(pledgingDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_RELEASE_PLEDGING));
		render("CloseCancelPledgings/entry.html", cancelPledging, detailCancelPledgings, mode, currentBusinessDate);
	}

	public static void approval(String taskId, Long keyId, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		PlTransaction cancelPledging = transactionService.getPledging(keyId);
		String detailCancelPledgings = null;
		try {
			CsPortfolio portfolio = new CsPortfolio();
			for (PlTransactionDetail plDetail : cancelPledging.getPlTransactionDetails()) {
				portfolio = accountService.getPortfolio(plDetail.getAccount().getCustodyAccountKey(), plDetail.getSecurity().getSecurityKey(), plDetail.getClassification().getLookupId(), plDetail.getHoldingRefs());
				plDetail.setPortfolio(portfolio);
				plDetail.setPortoQuantity(portfolio.getPortfolioQuantity());
				cancelPledging.getPlTransactionDetails().add(plDetail);
			}
			for (PlTransactionDetail p : cancelPledging.getPlTransactionDetails()) {
				log.debug("PORTFOLIO KEY = " + p.getPortfolio().getPortfolioKey());
			}
			detailCancelPledgings = jsonPledgingDetail.serialize(cancelPledging.getPlTransactionDetails());
			log.debug("DATA = " + detailCancelPledgings);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			render("CloseCancelPledgings/approval.html", cancelPledging, detailCancelPledgings, taskId, mode);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			render("CloseCancelPledgings/approval.html", cancelPledging, detailCancelPledgings, taskId, mode);
		}
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		try {
			PlTransaction pl = transactionService.approveCancelPledging(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);
			log.debug("Close Cancel Status = " + pl.getCloseCancelStatus().getLookupId());
			String message = "";
			if (pl.getCloseCancelStatus().getLookupId().equals(LookupConstants.PLEDGING_CLOSE)) {
				message = Messages.get("pledging.closed", pl.getTransactionKey());
			}
			if (pl.getCloseCancelStatus().getLookupId().equals(LookupConstants.PLEDGING_CANCEL)) {
				message = Messages.get("pledging.cancelled", pl.getTransactionKey());
			}

			renderJSON(Formatter.resultSuccess(message));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId);

		try {
			PlTransaction pl = transactionService.rejectCancelPledging(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.rejected", pl.getTransactionKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
