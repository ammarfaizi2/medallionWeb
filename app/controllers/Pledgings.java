package controllers;

import helpers.Formatter;
import helpers.UIConstants;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CsPortfolio;
import com.simian.medallion.model.PlTransaction;
import com.simian.medallion.model.PlTransactionDetail;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Pledgings extends MedallionController {
	private static Logger log = Logger.getLogger(Pledgings.class);

	@Before(only = { "entry", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> accountTypePld = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ACCOUNT_TYPE_PLD);
		renderArgs.put("accountTypePld", accountTypePld);

		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION));
	}

	public static void list() {
		log.debug("list. ");
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	@Check("transaction.pledging")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		PlTransaction pledging = new PlTransaction();
		String detailPledgings = null;
		Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		try {
			detailPledgings = json.writeValueAsString(pledging.getPlTransactionDetails());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
		render("Pledgings/entry.html", pledging, mode, detailPledgings, currentBusinessDate);

	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);
	}

	@Check("transaction.pledging")
	public static void save(String mode, PlTransaction pledging, PlTransactionDetail[] pledgingDetails) {
		log.debug("save. mode: " + mode + " pledging: " + pledging + " pledgingDetails: " + pledgingDetails);

		if (pledgingDetails != null) {
			for (PlTransactionDetail pledgingDetail : pledgingDetails) {
				pledging.getPlTransactionDetails().add(pledgingDetail);
			}
		}

		if (pledging != null) {
			String detailPledgings = null;
			try {
				// if (pledgingDetails==null) pledgingDetails = new
				// ArrayList<PlTransactionDetail>();
				detailPledgings = json.writeValueAsString(pledgingDetails);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			// if
			// (validation.errorsMap().values().containsAll(pledgingDetails)==false)
			// validation.clear();
			if (validation.errorsMap().values().contains(pledgingDetails) == false)
				validation.clear();
			// if (pledgingDetails.size()==0)
			// validation.required("pledging.plTransactionDetails",
			// pledging.getPlTransactionDetails());
			validation.required("Date is", pledging.getTransactionDate());
			validation.required("Customer No is", pledging.getCustomer().getCustomerNo());
			validation.required("Pledging Amount is", pledging.getAmount());
			if ((pledging.getAutoRelease() != null) && (pledging.getAutoRelease())) {
				validation.required("Due Date is", pledging.getDueDate());
			}

			log.debug("VALIDATION = " + validation.errorsMap());

			if (validation.hasErrors()) {
				mode = UIConstants.DISPLAY_MODE_ENTRY;
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
				render("Pledgings/entry.html", pledging, pledgingDetails, detailPledgings, mode);
			} else {
				log.debug(">>>>>>>>>>>>>>>>>>>>>>");
				Long id = pledging.getTransactionKey();
				serializerService.serialize(session.getId(), pledging.getTransactionKey(), pledging);
				confirming(id, mode);
			}
		} else {
			// flash.error(ExceptionConstants.PARAMETER_NULL, pledging);
			entry();
		}
	}

	@Check("transaction.pledging")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		boolean confirming = true;
		PlTransaction pledging = serializerService.deserialize(session.getId(), id, PlTransaction.class);
		String detailPledgings = null;
		try {
			detailPledgings = json.writeValueAsString(pledging.getPlTransactionDetails());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
		render("Pledgings/entry.html", pledging, mode, detailPledgings, confirming, id);

	}

	@Check("transaction.pledging")
	public static void confirm(PlTransaction pledging, PlTransactionDetail[] pledgingDetails, String mode) {
		log.debug("confirm. pledging: " + pledging + " pledgingDetails: " + pledgingDetails + " mode: " + mode);

//		String detailPledgings = null;
//		try {
//			detailPledgings = json.writeValueAsString(pledgingDetails);
//		} catch (JsonGenerationException e) {
//			log.debug("json.serialize");
//		} catch (JsonMappingException e) {
//			log.debug("json.serialize");
//		} catch (IOException e) {
//			log.debug("json.serialize");
//		}
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (pledgingDetails != null) {
				for (PlTransactionDetail pledgingDetail : pledgingDetails) {
					pledging.getPlTransactionDetails().add(pledgingDetail);
				}
			}

			boolean confirming = true;

			PlTransaction pl = transactionService.createPledging(MenuConstants.PL_PLEDGING, pledging, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
			if (pl.getTransactionKey() != null) {
				result.put("status", "success");
				result.put("message", Messages.get("transaction.confirmed.successful", pl.getTransactionKey()));
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
				render("Pledgings/entry.html", pl, mode, confirming);
			}
		} catch (MedallionException ex) {
			
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if(ex.getParams()!= null){
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			}else{
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
			
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
			
			renderJSON(result);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			result.put("error",e.getMessage());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
			
			renderJSON(result);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			result.put("error",e.getMessage());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
			
			renderJSON(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			result.put("error",e.getMessage());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
			
			renderJSON(result);
		}
	}

	@Check("transaction.pledging")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		PlTransaction pledging = serializerService.deserialize(session.getId(), id, PlTransaction.class);
		Set<PlTransactionDetail> pledgingDetails = pledging.getPlTransactionDetails();
		String detailPledgings = null;
		try {
			detailPledgings = json.writeValueAsString(pledgingDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.PL_PLEDGING));
		render("Pledgings/entry.html", pledging, detailPledgings, mode);
	}

	public static void approval(String taskId, Long keyId, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		PlTransaction pledging = transactionService.getPledging(keyId);
		log.debug("size = " + pledging.getPlTransactionDetails());
		String detailPledgings = null;
		try {
			CsPortfolio portfolio = new CsPortfolio();
			for (PlTransactionDetail plDetail : pledging.getPlTransactionDetails()) {
				portfolio = accountService.getPortfolio(plDetail.getAccount().getCustodyAccountKey(), plDetail.getSecurity().getSecurityKey(), plDetail.getClassification().getLookupId(), plDetail.getHoldingRefs());
				plDetail.setPortfolio(portfolio);
				plDetail.setPortoQuantity(portfolio.getPortfolioQuantity());
				pledging.getPlTransactionDetails().add(plDetail);
			}
			for (PlTransactionDetail p : pledging.getPlTransactionDetails()) {
				log.debug("PORTFOLIO KEY = " + p.getPortfolio().getPortfolioKey());
			}
			JsonHelper json = new JsonHelper().getPledgingSerializer();
			detailPledgings = json.serialize(pledging.getPlTransactionDetails());
			log.debug("DATA = " + detailPledgings);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			render("Pledgings/approval.html", pledging, detailPledgings, taskId, mode);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			render("Pledgings/approval.html", pledging, detailPledgings, taskId, mode);
		}
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		try {
			PlTransaction pl = transactionService.approvePledging(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.approved", pl.getTransactionKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId);

		try {
			PlTransaction pl = transactionService.rejectPledging(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.rejected", pl.getTransactionKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void getAvailableQuantity(String holdingRefs, Long accountKey, Long securityKey, BigDecimal portfolioQty, Long transNo) {
		log.debug("getAvailableQuantity. holdingRefs: " + holdingRefs + " accountKey: " + accountKey + " securityKey: " + securityKey + " portfolioQty: " + portfolioQty + " transNo: " + transNo);

		BigDecimal outstandingTransSell = transactionService.getOutstandingTransactionSell(holdingRefs, accountKey, securityKey);
		BigDecimal outstandingTransCancelBuy = transactionService.getOutstandingTransactionCancelBuy(holdingRefs, accountKey, securityKey);
		BigDecimal outstandingCloseCancel = transactionService.getOutstandingCloseCancel(holdingRefs, accountKey, securityKey, transNo);
		log.debug("outstandingTransSell = " + outstandingTransSell);
		log.debug("outstandingTransCancelBuy = " + outstandingTransCancelBuy);
		log.debug("outstandingCloseCancel = " + outstandingCloseCancel);
		Double availableQty = portfolioQty.doubleValue() - (outstandingTransSell.doubleValue() + outstandingTransCancelBuy.doubleValue()) - outstandingCloseCancel.doubleValue();
		renderText(new BigDecimal(availableQty));
	}

}
