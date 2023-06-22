package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import play.Play;
import play.i18n.Messages;
import play.mvc.Before;
import vo.ShareholderMeetingSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsMeeting;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.vo.SelectItem;

public class ShareholdersMeetingInquiries extends MedallionController {
	private static Logger log = Logger.getLogger(ShareholdersMeetings.class);

	private static String TIMEDELIMITER = ":";
	private static String FIELDDELIMITER = "#split#";

	@Before(only = { "inquiry" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Check("transaction.shareHolderMeetingInquiry")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsMeeting csMeeting = new CsMeeting();
		csMeeting.setIsActive(false);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_INQUIRY_LIST));
		render("ShareholdersMeetingInquiries/entry.html", csMeeting, mode);
	}

	@Check("transaction.shareHolderMeetingInquiry")
	public static void save(String mode, CsMeeting csMeeting, String meetingTimeHour, String meetingTimeMinute, String meetingPlace1, String meetingPlace2, String meetingPlace3, File fileAth, Boolean isAttached) {
		log.debug("save. mode: " + mode + " csMeeting: " + csMeeting + " meetingTimeHour: " + meetingTimeHour + " meetingTimeMinute: " + meetingTimeMinute + " meetingPlace1: " + meetingPlace1 + " meetingPlace2: " + meetingPlace2 + " meetingPlace3: " + meetingPlace3 + " fileAth: " + fileAth + " isAttached: " + isAttached);

		if (csMeeting != null) {
			validation.required("Meeting Subject is", csMeeting.getMeetingSubject());
			validation.required("Issuer Code is", csMeeting.getIssuer().getThirdPartyCode());
			validation.required("Security Type is", csMeeting.getSecurity().getSecurityType().getSecurityType());
			validation.required("Security Code is", csMeeting.getSecurity().getSecurityId());
			validation.required("Announcement Date", csMeeting.getAnnouncementDate());
			validation.required("Recording Date", csMeeting.getBookCloseDate());
			validation.required("Confirmation Date", csMeeting.getProxyDeadlineDate());
			validation.required("Meeting Date", csMeeting.getMeetingDate());
			validation.required("Result Date", csMeeting.getResultDate());
			validation.required("Hour and minute for Meeting Time is", meetingTimeHour);
			validation.required("Hour and minute for Meeting Time is", meetingTimeMinute);
			validation.required("Meeting Place is", meetingPlace1);

			if ((csMeeting.getAnnouncementDate() != null) && (csMeeting.getBookCloseDate() != null) && (csMeeting.getAnnouncementDate().after(csMeeting.getBookCloseDate()))) {
				validation.addError("Announcement Date", "must be same or before than Recording Date");
			}

			if ((csMeeting.getBookCloseDate() != null) && (csMeeting.getProxyDeadlineDate() != null) && (csMeeting.getBookCloseDate().after(csMeeting.getProxyDeadlineDate()))) {
				validation.addError("Recording Date", "must be same or before than Confirmation Date");
			}

			if ((csMeeting.getProxyDeadlineDate() != null) && (csMeeting.getMeetingDate() != null) && (csMeeting.getProxyDeadlineDate().after(csMeeting.getMeetingDate()))) {
				validation.addError("Confirmation Date", "must be same or before than Meeting Date");
			}

			if ((csMeeting.getMeetingDate() != null) && (csMeeting.getResultDate() != null) && (csMeeting.getMeetingDate().after(csMeeting.getResultDate()))) {
				validation.addError("Meeting Date", "must be same or before than Result Date");
			}

			if ((isAttached != null) && (isAttached)) {
				validation.required("Attach Document is", fileAth);
			}
			if ((mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_EDIT)) && (csMeeting.getMeetingStatus().trim().equals("C"))) {
				validation.required("Cancel Date is", csMeeting.getCancelDate());
				validation.required("Remarks is", csMeeting.getCancelRemarks());

				if ((csMeeting.getAnnouncementDate() != null) && (csMeeting.getCancelDate() != null) && (csMeeting.getAnnouncementDate().after(csMeeting.getCancelDate()))) {
					validation.addError("Announcement Date", "must be same or before than Cancel Date");
				}
			}
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_INQUIRY_LIST));
				render("ShareholdersMeetingInquiries/entry.html", csMeeting, mode, meetingTimeHour, meetingTimeMinute, meetingPlace1, meetingPlace2, meetingPlace3, isAttached);
			} else {
				csMeeting.setMeetingPlace(meetingPlace1 + FIELDDELIMITER + meetingPlace2 + FIELDDELIMITER + meetingPlace3);
				csMeeting.setMeetingTime(meetingTimeHour + TIMEDELIMITER + meetingTimeMinute);

				if (fileAth != null) {
					String uploadDir = Play.configuration.getProperty("upload.shareholder");
					String newFileName = uploadDir + System.currentTimeMillis() + "-" + fileAth.getName();

					File output = new File(newFileName);
					copyFile(fileAth, output);
					csMeeting.setAttachement(output.getName());
				}

				serializerService.serialize(session.getId(), csMeeting.getMeetingKey(), csMeeting);
				confirming(csMeeting.getMeetingKey(), mode, isAttached);
			}
		} else {
			flash.error("argument.null", csMeeting);

		}
	}

	@Check("transaction.shareHolderMeetingInquiry")
	public static void confirming(Long meetingKey, String mode, Boolean isAttached) {
		log.debug("confirming. meetingKey: " + meetingKey + " mode: " + mode + " isAttached: " + isAttached);

		boolean confirming = true;
		CsMeeting csMeeting = serializerService.deserialize(session.getId(), meetingKey, CsMeeting.class);

		renderArgs.put("editOnly", true);

		String[] meetingTime = new String[] { "" };
		String meetingTimeHour = new String();
		String meetingTimeMinute = new String();

		String[] meetingPlace = new String[] { "" };
		String meetingPlace1 = new String();
		String meetingPlace2 = new String();
		String meetingPlace3 = new String();

		meetingTime = csMeeting.getMeetingTime().split(TIMEDELIMITER);
		meetingTimeHour = meetingTime[0];
		meetingTimeMinute = meetingTime[1];

		meetingPlace = csMeeting.getMeetingPlace().split(FIELDDELIMITER);
		if (meetingPlace.length >= 1) {
			meetingPlace1 = meetingPlace[0];
		}
		if ((meetingPlace.length == 2) || (meetingPlace.length == 3)) {
			meetingPlace2 = meetingPlace[1];
		}
		if (meetingPlace.length == 3) {
			meetingPlace3 = meetingPlace[2];
		}

		String attachement = null;
		if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
			int dashPos = csMeeting.getAttachement().indexOf("-");
			attachement = csMeeting.getAttachement().substring(dashPos + 1);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_INQUIRY_LIST));

		render("ShareholdersMeetingInquiries/entry.html", csMeeting, mode, confirming, meetingTimeHour, meetingTimeMinute, meetingPlace1, meetingPlace2, meetingPlace3, isAttached, attachement);
	}

	@Check("transaction.shareHolderMeetingInquiry")
	public static void confirm(CsMeeting csMeeting, String mode, Boolean isAttached) {
		log.debug("confirm. csMeeting: " + csMeeting + " mode: " + mode + " isAttached: " + isAttached);

		try {
			String[] meetingTime = new String[] { "" };
			String meetingTimeHour = new String();
			String meetingTimeMinute = new String();

			meetingTime = csMeeting.getMeetingTime().split(TIMEDELIMITER);
			meetingTimeHour = meetingTime[0];
			meetingTimeMinute = meetingTime[1];

			csMeeting.setMeetingTime(meetingTimeHour + meetingTimeMinute);

			log.debug("confirm <> cancelDate " + csMeeting.getCancelDate());
			log.debug("confirm <> cancelRemarks " + csMeeting.getCancelRemarks());

			CsMeeting meeting = shareHolderService.saveMeeting(MenuConstants.CS_SHAREHOLDERS_MEETING_INQUIRY_LIST, csMeeting, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			if (meeting.getMeetingKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("shareholdermeeting.confirmed.successful", meeting.getMeetingKey()));
				renderJSON(result);
			}
		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if (ex.getParams() != null) {
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

	@Check("transaction.shareHolderMeetingInquiry")
	public static void back(Long meetingKey, String mode, Boolean isAttached) {
		log.debug("back. meetingKey: " + meetingKey + " mode: " + mode + " isAttached: " + isAttached);

		CsMeeting csMeeting = serializerService.deserialize(session.getId(), meetingKey, CsMeeting.class);

		String[] meetingTime = new String[] { "" };
		String meetingTimeHour = new String();
		String meetingTimeMinute = new String();

		String[] meetingPlace = new String[] { "" };
		String meetingPlace1 = new String();
		String meetingPlace2 = new String();
		String meetingPlace3 = new String();

		meetingTime = csMeeting.getMeetingTime().split(TIMEDELIMITER);
		meetingTimeHour = meetingTime[0];
		meetingTimeMinute = meetingTime[1];

		meetingPlace = csMeeting.getMeetingPlace().split(FIELDDELIMITER);
		if (meetingPlace.length >= 1) {
			meetingPlace1 = meetingPlace[0];
		}
		if ((meetingPlace.length == 2) || (meetingPlace.length == 3)) {
			meetingPlace2 = meetingPlace[1];
		}
		if (meetingPlace.length == 3) {
			meetingPlace3 = meetingPlace[2];
		}

		String attachement = null;
		if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
			int dashPos = csMeeting.getAttachement().indexOf("-");
			attachement = csMeeting.getAttachement().substring(dashPos + 1);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_INQUIRY_LIST));

		render("ShareholdersMeetingInquiries/entry.html", csMeeting, mode, meetingTimeHour, meetingTimeMinute, meetingPlace1, meetingPlace2, meetingPlace3, isAttached, attachement);
	}

	@Check("transaction.shareHolderMeetingInquiry")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsMeeting csMeeting = shareHolderService.getMeeting(id);

		// prepare meeting time data for view UI
		String meetingTimeHour = csMeeting.getMeetingTime().substring(0, 2);
		String meetingTimeMinute = csMeeting.getMeetingTime().substring(2);
		csMeeting.setMeetingTime(meetingTimeHour + TIMEDELIMITER + meetingTimeMinute);

		String[] meetingPlace = new String[] { "" };
		String meetingPlace1 = new String();
		String meetingPlace2 = new String();
		String meetingPlace3 = new String();

		meetingPlace = csMeeting.getMeetingPlace().split(FIELDDELIMITER);
		if (meetingPlace.length >= 1) {
			meetingPlace1 = meetingPlace[0];
		}
		if ((meetingPlace.length == 2) || (meetingPlace.length == 3)) {
			meetingPlace2 = meetingPlace[1];
		}
		if (meetingPlace.length == 3) {
			meetingPlace3 = meetingPlace[2];
		}

		String attachement = null;
		if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
			int dashPos = csMeeting.getAttachement().indexOf("-");
			attachement = csMeeting.getAttachement().substring(dashPos + 1);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_INQUIRY_LIST));
		render("ShareholdersMeetingInquiries/entry.html", csMeeting, mode, meetingTimeHour, meetingTimeMinute, meetingPlace1, meetingPlace2, meetingPlace3, attachement);
	}

	@Check("transaction.shareHolderMeetingInquiry")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsMeeting csMeeting = shareHolderService.getMeeting(id);

		// prepare meeting time data for view UI
		String meetingTimeHour = csMeeting.getMeetingTime().substring(0, 2);
		String meetingTimeMinute = csMeeting.getMeetingTime().substring(2);
		csMeeting.setMeetingTime(meetingTimeHour + TIMEDELIMITER + meetingTimeMinute);

		String[] meetingPlace = new String[] { "" };
		String meetingPlace1 = new String();
		String meetingPlace2 = new String();
		String meetingPlace3 = new String();

		meetingPlace = csMeeting.getMeetingPlace().split(FIELDDELIMITER);
		if (meetingPlace.length >= 1) {
			meetingPlace1 = meetingPlace[0];
		}
		if ((meetingPlace.length == 2) || (meetingPlace.length == 3)) {
			meetingPlace2 = meetingPlace[1];
		}
		if (meetingPlace.length == 3) {
			meetingPlace3 = meetingPlace[2];
		}

		String attachement = null;
		if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
			int dashPos = csMeeting.getAttachement().indexOf("-");
			attachement = csMeeting.getAttachement().substring(dashPos + 1);
			attachement = Helper.removeExtensionZip(attachement);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_INQUIRY_LIST));
		render("ShareholdersMeetingInquiries/entry.html", csMeeting, mode, meetingTimeHour, meetingTimeMinute, meetingPlace1, meetingPlace2, meetingPlace3, attachement);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);
	}

	@Check("transaction.shareHolderMeetingInquiry")
	public static void inquiry(String mode, String param) {
		log.debug("inquiry. mode: " + mode + " param: " + param);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_INQUIRY_LIST));
		ShareholderMeetingSearchParameters params = new ShareholderMeetingSearchParameters();
		render("ShareholdersMeetingInquiries/inquiry.html", mode, param, params);
	}

	@Check("transaction.shareHolderMeetingInquiry")
	public static void searchInquiry(ShareholderMeetingSearchParameters params) {
		log.debug("searchInquiry. params: " + params);

		List<CsMeeting> csMeetings = shareHolderService.searchInquiryMeeting(params.meetingDateFrom, params.meetingDateTo, params.searchThirdPartyKey, UIHelper.withOperator(params.meetingSearchNoOperator, params.MeetingNoOperator));
		renderArgs.put("cancelopen", LookupConstants.__RECORD_STATUS_CLOSE_CANCEL_OPEN);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_INQUIRY_LIST));
		render("ShareholdersMeetingInquiries/grid_inquiry.html", csMeetings);
	}

	@Check("transaction.shareHolderMeetingInquiry")
	public static void inquiryPaging(Paging page, ShareholderMeetingSearchParameters param) {
		log.debug("inquiryPaging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("m.meetingDate", page.GREATEQUAL, param.meetingDateFrom);
			page.addParams("m.meetingDate", page.LESSEQUAL, param.meetingDateTo);
			page.addParams("i.thirdPartyKey", page.EQUAL, param.searchThirdPartyKey);
			page.addParams("m.meetingKey", page.LIKE, UIHelper.withOperator(param.meetingSearchNoOperator, param.MeetingNoOperator));

			//
			page.addParams(Helper.searchAll("(m.meetingKey||m.meetingSubject||m.security.securityType.securityType||m.security.securityId||to_char(m.announcementDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "to_char(m.meetingDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||m.meetingStatus)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
			//
			// if (!"".equals(param.individualUserKey) &&
			// param.individualUserKey != null) {
			// page.addParams("u.userKey", page.EQUAL, param.individualUserKey);
			// }

			page = shareHolderService.searchInquiryMeetingPaging(page);
		}

		renderJSON(page);
	}

	@Check("transaction.shareHolderMeetingInquiry")
	public static void attachmentDownload(String downloadfile) throws IOException {
		log.debug("attachmentDownload. downloadfile: " + downloadfile);

		CsMeeting csMeeting = shareHolderService.getMeeting(new Long(downloadfile));
		String uploadedDirOutput = Play.configuration.getProperty("upload.shareholder");
		String fullPath = uploadedDirOutput + csMeeting.getAttachement();
		File fileDest = new File(fullPath);
		if (fileDest.exists()) {
			log.debug("no need to download " + csMeeting.getAttachement() + ", it's already downloaded");
		} else {
			log.debug("download " + downloadfile + "...");
			clientFileService.doDownload(csMeeting.getAttachement(), fileDest, SimpleFileService.UPLOAD_SHAREHOLDERS);
		}

		File file = null;
		try {
			file = Helper.extractSingleZipFile(fileDest);
		} catch (Exception e) {
		}

		log.debug("fullPath:" + fullPath);
		renderBinary(file, file.getName());
	}

	@Check("transaction.shareHolderMeetingInquiry")
	private static String copyFile(File source, File dest) {
		String fileName = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(dest);
			fileName = dest.getName();
			IOUtils.copy(fis, fos);
			return fileName;
		} catch (Exception ex) {
			throw new RuntimeException("Error when moving file " + ex.getMessage());
		} finally {
			try {
				fos.flush();
				fis.close();
				fos.close();
				fis = null;
				fos = null;
			} catch (Exception ex) {
			}
		}
	}
}
