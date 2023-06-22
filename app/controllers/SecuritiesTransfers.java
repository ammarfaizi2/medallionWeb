package controllers;

import helpers.Formatter;
import helpers.UIConstants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CBestMessage;
import com.simian.medallion.model.CsAccount;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class SecuritiesTransfers extends MedallionController {
	private static Logger log = Logger.getLogger(SecuritiesTransfers.class);

	public static final String SECURITY_CODE_LOCAL = "LOCAL";
	public static final String SECURITY_CODE_ISO = "ISO";

	@Before(only = { "entry", "save", "back", "confirming", "confirm", "approval" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> securityCodeOptions = new ArrayList<SelectItem>();
		securityCodeOptions.add(new SelectItem(SECURITY_CODE_LOCAL, SECURITY_CODE_LOCAL));
		securityCodeOptions.add(new SelectItem(SECURITY_CODE_ISO, SECURITY_CODE_ISO));
		renderArgs.put("securityCodeOptions", securityCodeOptions);
	}

	public static void list(String action) {
		log.debug("list. action: " + action);
	}

	@Check("cbestconnector.entry")
	public static void back(String messageId, String mode, String status) {
		log.debug("back. messageId: " + messageId + " mode: " + mode + " status: " + status);

		mode = UIConstants.DISPLAY_MODE_ENTRY;

		CBestMessage cBestMessage = serializerService.deserialize(session.getId(), messageId, CBestMessage.class);
		CsAccount sourceAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getSourceaccount());
		CsAccount targetAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getTargetaccount());
		ScMaster securityCode = securityService.getSecurityPickForChargeItem(cBestMessage.getData().getSecuritycode());

		renderArgs.put("sourceaccountname", sourceAccount.getName());
		renderArgs.put("targetaccountname", targetAccount.getName());
		renderArgs.put("securitycodename", securityCode.getDescription());

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_SECURITIES_TRANSFER));
		render("SecuritiesTransfers/detail.html", cBestMessage, mode, status);
	}

	@Check("cbestconnector.entry")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CBestMessage cBestMessage = new CBestMessage();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_SECURITIES_TRANSFER));
		render("SecuritiesTransfers/detail.html", cBestMessage, mode);
	}

	@Check("cbestconnector.entry")
	public static void save(CBestMessage cBestMessage, String mode, String status) {
		log.debug("save. cBestMessage: " + cBestMessage + " mode: " + mode + " status: " + status);

		if (cBestMessage != null) {
			validation.required("External Reference", cBestMessage.getData().getExternalreference());
			validation.required("Source Account", cBestMessage.getData().getSourceaccount());
			validation.required("Target Account", cBestMessage.getData().getTargetaccount());
			validation.required("Security Code Type", cBestMessage.getData().getSecuritycodetype());
			validation.required("Security Code", cBestMessage.getData().getSecuritycode());
			validation.required("Security Quantity", cBestMessage.getData().getSecurityquantity());
			validation.required("Settlement Date", cBestMessage.getData().getSettlementdate());

			if (cBestMessage.getData().getSourceaccount().equalsIgnoreCase(cBestMessage.getData().getTargetaccount())) {
				validation.addError("", "Source Account should not equal with Target Account");
			}

			if (cbestService.getCountExternalRef(cBestMessage.getData().getExternalreference()) > 0) {
				validation.addError("", "External Reference Already Exists");
			}

			if (cBestMessage.getData().getExternalreference().toUpperCase().indexOf("CIPS") != -1) {
				validation.addError("", "External Reference can\'t contain \'CIPS\'");
			}

			if (!cBestMessage.getData().getSecurityquantity().equals("")) {
				if ((new BigDecimal(cBestMessage.getData().getSecurityquantity().replace(",", "")).compareTo(BigDecimal.ZERO)) <= 0) {
					validation.addError("", "Security quantity may not equal to or less than 0");
				}
			}

			if (validation.hasErrors()) {
				mode = "entry";

				if ((cBestMessage.getData().getSourceaccount() != null) && (cBestMessage.getData().getSourceaccount().trim().length() > 0)) {
					CsAccount sourceAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getSourceaccount());
					renderArgs.put("sourceaccountname", sourceAccount.getName());
				}

				if ((cBestMessage.getData().getTargetaccount() != null) && (cBestMessage.getData().getTargetaccount().trim().length() > 0)) {
					CsAccount targetAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getTargetaccount());
					renderArgs.put("targetaccountname", targetAccount.getName());
				}

				if ((cBestMessage.getData().getSecuritycode() != null) && (cBestMessage.getData().getSecuritycode().trim().length() > 0)) {
					ScMaster securityCode = securityService.getSecurityPickForChargeItem(cBestMessage.getData().getSecuritycode());
					renderArgs.put("securitycodename", securityCode.getDescription());
				}

				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_SECURITIES_TRANSFER));
				render("SecuritiesTransfers/detail.html", cBestMessage, mode, status);
			} else {
				serializerService.serialize(session.getId(), cBestMessage.getMessageid(), cBestMessage);
				confirming(cBestMessage.getMessageid(), mode, status);
			}
		} else {
			render("SecuritiesTransfers/detail.html", cBestMessage, mode, status);
		}
	}

	@Check("cbestconnector.entry")
	public static void confirming(String messageId, String mode, String status) {
		log.debug("confirming. messageId: " + messageId + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		CBestMessage cBestMessage = serializerService.deserialize(session.getId(), messageId, CBestMessage.class);
		CsAccount sourceAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getSourceaccount());
		CsAccount targetAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getTargetaccount());
		ScMaster securityCode = securityService.getSecurityPickForChargeItem(cBestMessage.getData().getSecuritycode());

		renderArgs.put("sourceaccountname", sourceAccount.getName());
		renderArgs.put("targetaccountname", targetAccount.getName());
		renderArgs.put("securitycodename", securityCode.getDescription());

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_SECURITIES_TRANSFER));
		render("SecuritiesTransfers/detail.html", cBestMessage, mode, status);
	}

	@Check("cbestconnector.entry")
	public static void confirm(String mode, CBestMessage cBestMessage, String status) {
		log.debug("confirm. mode: " + mode + " cBestMessage: " + cBestMessage + " status: " + status);

		cBestMessage.getData().setSecurityquantity(cBestMessage.getData().getSecurityquantity().replace(",", ""));
		cBestMessage.setName("SecuritiesTransfer");
		cBestMessage.setType("OutgoingMessage");

		cbestService.saveCBestMessage(MenuConstants.CB_SECURITIES_TRANSFER, cBestMessage, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));

		render("SecuritiesTransfers/detail.html", new CBestMessage(), mode);
	}

	public static void approval(String taskId, Long keyId, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CBestMessage cBestMessage = cbestService.getMessage(keyId);

		CsAccount sourceAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getSourceaccount());
		CsAccount targetAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, cBestMessage.getData().getTargetaccount());
		ScMaster securityCode = securityService.getSecurityPickForChargeItem(cBestMessage.getData().getSecuritycode());

		renderArgs.put("sourceaccountname", sourceAccount.getName());
		renderArgs.put("targetaccountname", targetAccount.getName());
		renderArgs.put("securitycodename", securityCode.getDescription());

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			render("SecuritiesTransfers/approval.html", cBestMessage, taskId, mode);
		} else {

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			render("SecuritiesTransfers/approval.html", cBestMessage, taskId, mode);
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
