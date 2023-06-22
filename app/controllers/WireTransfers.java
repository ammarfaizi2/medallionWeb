package controllers;

import helpers.Formatter;
import helpers.UIConstants;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CBestMessage;
import com.simian.medallion.model.CsAccount;
import com.simian.medallion.model.GnCurrency;

@With(Secure.class)
public class WireTransfers extends MedallionController {
	private static Logger log = Logger.getLogger(WireTransfers.class);

	public static void list(String action) {
		log.debug("list. action: " + action);
	}

	@Check("cbestconnector.entry")
	public static void back(String messageId, String mode, String status) {
		log.debug("back. messageId: " + messageId + " mode: " + mode + " status: " + status);

		mode = UIConstants.DISPLAY_MODE_ENTRY;

		CBestMessage cBestMessage = serializerService.deserialize(session.getId(), messageId, CBestMessage.class);

		CsAccount participantAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getParticipantaccount());
		CsAccount beneficiaryAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getBeneficiaryaccount());
		GnCurrency currencyTransfer = generalService.getCurrencyPick(cBestMessage.getData().getCurrencycode());

		renderArgs.put("participantaccountname", participantAccount.getName());
		renderArgs.put("beneficiaryaccountname", beneficiaryAccount.getName());
		renderArgs.put("currencyname", currencyTransfer.getDescription());

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_WIRE_TRANSFER));
		render("WireTransfers/detail.html", cBestMessage, mode, status);
	}

	@Check("cbestconnector.entry")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CBestMessage cBestMessage = new CBestMessage();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_WIRE_TRANSFER));
		render("WireTransfers/detail.html", cBestMessage, mode);
	}

	@Check("cbestconnector.entry")
	public static void save(CBestMessage cBestMessage, String mode, String status) {
		log.debug("save. cBestMessage: " + cBestMessage + " mode: " + mode + " status: " + status);

		if (cBestMessage != null) {
			validation.required("External Reference", cBestMessage.getData().getExternalreference());
			validation.required("Participant Account", cBestMessage.getData().getParticipantaccount());
			validation.required("Beneficiary Institution", cBestMessage.getData().getBeneficiaryaccount());
			validation.required("Beneficiary Account", cBestMessage.getData().getBeneficiaryaccount());
			validation.required("Value Date", cBestMessage.getData().getValuedate());
			validation.required("Currency Code", cBestMessage.getData().getCurrencycode());
			validation.required("Cash Amount", cBestMessage.getData().getCashamount());

			if (cBestMessage.getData().getParticipantaccount().equalsIgnoreCase(cBestMessage.getData().getBeneficiaryaccount())) {
				validation.addError("", "Participant Account should not equal with Beneficiary Account", "Beneficiary Account");
			}

			if (cbestService.getCountExternalRef(cBestMessage.getData().getExternalreference()) > 0) {
				validation.addError("", "External Reference Already Exists");
			}

			if (cBestMessage.getData().getExternalreference().toUpperCase().indexOf("CIPS") != -1) {
				validation.addError("", "External Reference can\'t contain \'CIPS\'");
			}

			if (!cBestMessage.getData().getCashamount().trim().equals("")) {
				if ((new BigDecimal(cBestMessage.getData().getCashamount().replace(",", "")).compareTo(BigDecimal.ZERO)) <= 0) {
					validation.addError("", "Cash Amount may not equal to or less than 0");
				}
			}

			if (validation.hasErrors()) {
				mode = "entry";

				if ((cBestMessage.getData().getParticipantaccount() != null) && (cBestMessage.getData().getParticipantaccount().trim().length() > 0)) {
					CsAccount bankAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getParticipantaccount());
					renderArgs.put("participantaccountname", bankAccount.getName());
				}

				if ((cBestMessage.getData().getBeneficiaryaccount() != null) && (cBestMessage.getData().getBeneficiaryaccount().trim().length() > 0)) {
					CsAccount beneficiaryAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getBeneficiaryaccount());
					renderArgs.put("beneficiaryaccountname", beneficiaryAccount.getName());

				}

				if ((cBestMessage.getData().getCurrencycode() != null) && (cBestMessage.getData().getCurrencycode().trim().length() > 0)) {
					GnCurrency currencyTransfer = generalService.getCurrencyPick(cBestMessage.getData().getCurrencycode());
					renderArgs.put("currencyname", currencyTransfer.getDescription());
				}

				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_WIRE_TRANSFER));
				render("WireTransfers/detail.html", cBestMessage, mode, status);
			} else {
				serializerService.serialize(session.getId(), cBestMessage.getMessageid(), cBestMessage);
				confirming(cBestMessage.getMessageid(), mode, status);
			}
		} else {
			render("WireTransfers/detail.html", cBestMessage, mode, status);
		}
	}

	@Check("cbestconnector.entry")
	public static void confirming(String messageId, String mode, String status) {
		log.debug("confirming. messageId: " + messageId + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		CBestMessage cBestMessage = serializerService.deserialize(session.getId(), messageId, CBestMessage.class);

		CsAccount bankAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getParticipantaccount());
		CsAccount beneficiaryAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getBeneficiaryaccount());
		GnCurrency currencyTransfer = generalService.getCurrencyPick(cBestMessage.getData().getCurrencycode());

		renderArgs.put("participantaccountname", bankAccount.getName());
		renderArgs.put("beneficiaryaccountname", beneficiaryAccount.getName());
		renderArgs.put("currencyname", currencyTransfer.getDescription());

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_WIRE_TRANSFER));
		render("WireTransfers/detail.html", cBestMessage, mode, status);
	}

	@Check("cbestconnector.entry")
	public static void confirm(String mode, CBestMessage cBestMessage, String status) {
		log.debug("confirm. mode: " + mode + " cBestMessage: " + cBestMessage + " status: " + status);

		cBestMessage.getData().setCashamount(cBestMessage.getData().getCashamount().replace(",", ""));
		cBestMessage.setValuedate(cBestMessage.getData().getValuedate());
		cBestMessage.setName("WireTransfer");
		cBestMessage.setType("OutgoingMessage");

		cbestService.saveCBestMessage(MenuConstants.CB_WIRE_TRANSFER, cBestMessage, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));

		render("WireTransfers/detail.html", new CBestMessage(), mode);
	}

	public static void approval(String taskId, Long keyId, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CBestMessage cBestMessage = cbestService.getMessage(keyId);

		CsAccount bankAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getParticipantaccount());
		CsAccount beneficiaryAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getBeneficiaryaccount());
		GnCurrency currencyTransfer = generalService.getCurrencyPick(cBestMessage.getData().getCurrencycode());

		renderArgs.put("participantaccountname", bankAccount.getName());
		renderArgs.put("beneficiaryaccountname", beneficiaryAccount.getName());
		renderArgs.put("currencyname", currencyTransfer.getDescription());

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			render("WireTransfers/approval.html", cBestMessage, taskId, mode);
		} else {

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			render("WireTransfers/approval.html", cBestMessage, taskId, mode);
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
}
