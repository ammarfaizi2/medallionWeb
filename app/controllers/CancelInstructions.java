package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.serializers.CBestMessagePickSerializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.mvc.Before;
import play.mvc.With;

import com.google.gson.GsonBuilder;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CBestMessage;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class CancelInstructions extends MedallionController {
	private static Logger log = Logger.getLogger(CancelInstructions.class);

	private static final SimpleDateFormat sdfData = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat sdfForm = new SimpleDateFormat("dd/MM/yyyy");

	private static final String InstructionData = "Instruction";
	private static final String OTCBondData = "OTCBond";
	private static final String PrematchData = "PrematchData";

	@Before(only = { "entry", "save", "back", "confirming", "confirm", "approval" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> cancelMessageType = new ArrayList<SelectItem>();
		cancelMessageType.add(new SelectItem("InstructionData", InstructionData));
		cancelMessageType.add(new SelectItem("OTCBondData", OTCBondData));
		cancelMessageType.add(new SelectItem("PrematchData", PrematchData));
		renderArgs.put("cancelMessageType", cancelMessageType);

		List<SelectItem> taxsubmittedbykseis = new ArrayList<SelectItem>();
		taxsubmittedbykseis.add(new SelectItem("Y", "Y"));
		taxsubmittedbykseis.add(new SelectItem("N", "N"));
		renderArgs.put("taxsubmittedbykseis", taxsubmittedbykseis);
	}

	public static void list(String action) {
		log.debug("list. action: " + action);
	}

	@Check("cbestconnector.action")
	public static void back(String messageId, String mode, String status) {
		log.debug("back. messageId: " + messageId + " mode: " + mode + " status: " + status);

		mode = UIConstants.DISPLAY_MODE_ENTRY;

		CBestMessage cBestMessage = serializerService.deserialize(session.getId(), messageId, CBestMessage.class);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_CANCEL_INSTRUCTION));
		render("CancelInstructions/detail.html", cBestMessage, mode, status);
	}

	@Check("cbestconnector.action")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CBestMessage cBestMessage = new CBestMessage();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_CANCEL_INSTRUCTION));
		render("CancelInstructions/detail.html", cBestMessage, mode);
	}

	@Check("cbestconnector.action")
	public static void save(CBestMessage cBestMessage, String mode, String status) {
		log.debug("save. cBestMessage: " + cBestMessage + " mode: " + mode + " status: " + status);

		if (cBestMessage != null) {
			try {
				cBestMessage.setName("CancelInstruction");
				cBestMessage.setType("OutgoingMessage");

				cBestMessage = cbestService.saveCBestMessage(MenuConstants.CB_CANCEL_INSTRUCTION, cBestMessage, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));

				if (cBestMessage != null) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", "Success create Cancel Instruction");
					renderJSON(result);
				}
			} catch (Exception e) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "failed");
				result.put("error", e.getMessage());
				renderJSON(result);
			}
		} else {
			flash.error("argument.null", cBestMessage);
		}
	}

	public static void confirming(String messageId, String mode, String status) {
		log.debug("confirming. messageId: " + messageId + " mode: " + mode + " status: " + status);
	}

	public static void confirm(String mode, CBestMessage cBestMessage, String status) {
		log.debug("confirm. mode: " + mode + " cBestMessage: " + cBestMessage + " status: " + status);
	}

	public static void approval(String taskId, Long keyId, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CBestMessage cBestMessage = cbestService.getMessage(keyId);

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			render("CancelInstructions/approval.html", cBestMessage, taskId, mode);
		} else {

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			render("CancelInstructions/approval.html", cBestMessage, taskId, mode);
		}
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		try {
			CBestMessage cBestMessage = cbestService.approveTransaction(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess("Transaction No " + cBestMessage.getData().getExternalreference() + " is Approved"));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId);

		try {
			CBestMessage cBestMessage = cbestService.rejectTransaction(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess("Transaction No " + cBestMessage.getData().getExternalreference() + " is Rejected"));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("cbestconnector.action")
	public static void getOutMessageNotCancel(String externalReference) {
		log.debug("getOutMessageNotCancel. externalReference: " + externalReference);

		Map<String, Object> mapValue = new HashMap<String, Object>();
		CBestMessage cBestMessage = null;
		String dataJSONParameter = new String();

		if ((externalReference != null) && (externalReference.trim().length() > 0)) {
			cBestMessage = cbestService.getOutMessageByExtrefNotCancel(externalReference);

			convertDataDateFormat(cBestMessage);
			mapValue.put("cbestmessage", cBestMessage);
			GsonBuilder gsonBuilder = new GsonBuilder();

			gsonBuilder.registerTypeAdapter(CBestMessage.class, new CBestMessagePickSerializer());

			dataJSONParameter = gsonBuilder.create().toJson(mapValue);
		}
		renderJSON(dataJSONParameter);
	}

	@Check("cbestconnector.action")
	private static void convertDataDateFormat(CBestMessage cBestMessage) {
		log.debug("convertDataDateFormat. cBestMessage: " + cBestMessage);

		try {
			if ((cBestMessage.getData().getMaturitydate() != null) && (cBestMessage.getData().getMaturitydate().trim().length() > 0)) {
				cBestMessage.getData().setMaturitydate(sdfForm.format(sdfData.parse(cBestMessage.getData().getMaturitydate())));
			}
			if ((cBestMessage.getData().getTradedate() != null) && (cBestMessage.getData().getTradedate().trim().length() > 0)) {
				cBestMessage.getData().setTradedate(sdfForm.format(sdfData.parse(cBestMessage.getData().getTradedate())));
			}
			if ((cBestMessage.getData().getSettlementdate() != null) && (cBestMessage.getData().getSettlementdate().trim().length() > 0)) {
				cBestMessage.getData().setSettlementdate(sdfForm.format(sdfData.parse(cBestMessage.getData().getSettlementdate())));
			}
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
	}
}