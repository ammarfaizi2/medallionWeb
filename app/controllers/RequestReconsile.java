package controllers;

import helpers.UIConstants;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.CBestMessage;

@With(Secure.class)
public class RequestReconsile extends MedallionController {
	private static Logger log = Logger.getLogger(RequestReconsile.class);

	public static void list(String action) {
		log.debug("list. action: " + action);
	}

	@Check("cbestconnector.list")
	public static void back(String messageId, String mode, String status) {
		log.debug("back. messageId: " + messageId + " mode: " + mode + " status: " + status);

		mode = UIConstants.DISPLAY_MODE_ENTRY;
		CBestMessage cBestMessage = serializerService.deserialize(session.getId(), messageId, CBestMessage.class);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_REQUEST_RECONSILE));
		render("RequestReconsile/detail.html", cBestMessage, mode, status);
	}

	@Check("cbestconnector.list")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CBestMessage cBestMessage = new CBestMessage();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CB_REQUEST_RECONSILE));
		render("RequestReconsile/detail.html", cBestMessage, mode);
	}

	@Check("cbestconnector.list")
	public static void save(CBestMessage cBestMessage, String mode, String status) {
		log.debug("save. cBestMessage: " + cBestMessage + " mode: " + mode + " status: " + status);

		if (cBestMessage != null) {
			try {
				cBestMessage = cbestService.createMessage(cBestMessage);

				cBestMessage = cbestService.createAndSendMessage(cBestMessage);

				if (cBestMessage != null) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", "Successing sending data for date " + cBestMessage.getData().getValuedate());
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
}