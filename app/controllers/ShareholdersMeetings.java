package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsMeeting;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScCorporateAnnouncement;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.vo.SelectItem;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import net.lingala.zip4j.exception.ZipException;
import play.Play;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.ShareholderMeetingSearchParameters;

@With(Secure.class)
public class ShareholdersMeetings extends MedallionController {
	private static Logger log = Logger.getLogger(ShareholdersMeetings.class);

	private static String TIMEDELIMITER = ":";
	private static String FIELDDELIMITER = "#split#";
	
	private static final String maxSize = "Size of file max ";

	@Before(only = { "view", "entry", "edit", "save", "confirm", "confirming", "back", "approval", "add"})
	public static void setup() {
		String validateAttach = valueParam(SystemParamConstants.ORGANIZATION_SHAREHOLDER_ATTACHMENT);
		StringBuffer paramValidate = new StringBuffer();
		String valAttach = "";
		String mSize = "";
		if (!Helper.isNullOrEmpty(validateAttach)) {
			String[] arrAttach = validateAttach.split("\\|");
			mSize = arrAttach[0];
			String endWith = "";
			if (arrAttach != null && arrAttach.length > 0) {
				if (!arrAttach[0].isEmpty()) {
					paramValidate.append(maxSize + arrAttach[1]);
				}
				paramValidate.append(" (");
				boolean isEndWith = false;
				for (int i = 2; i < arrAttach.length; i++) {
					paramValidate.append(arrAttach[i] + ",");
					isEndWith = true;
				}
				if (isEndWith)
					endWith = ")";
			}

			valAttach = paramValidate.substring(0, paramValidate.length() - 1) + endWith;
		}
		renderArgs.put("validateAttach", valAttach);
		renderArgs.put("maxSize", mSize);
	}
	
	@Before(only = { "cancel" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Check("transaction.shareHolderMeeting")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsMeeting csMeeting = new CsMeeting();
		csMeeting.setIsActive(false);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING));
		render("ShareholdersMeetings/entry.html", csMeeting, mode);
	}

	@Check("transaction.shareHolderMeeting.save")
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

			if (meetingTimeHour.isEmpty() && !meetingTimeMinute.isEmpty()) {
				validation.required("Hour and minute for Meeting Time is", meetingTimeHour);
			} else if (!meetingTimeHour.isEmpty() && meetingTimeMinute.isEmpty()) {
				validation.required("Hour and minute for Meeting Time is", meetingTimeMinute);
			} else if (meetingTimeHour.isEmpty() && meetingTimeMinute.isEmpty()) {
				validation.required("Hour and minute for Meeting Time is", meetingTimeHour);
			}

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
			if (mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_EDIT)) {
				validation.required("Cancel Date is", csMeeting.getCancelDate());
				validation.required("Remarks is", csMeeting.getCancelRemarks());

				if ((csMeeting.getAnnouncementDate() != null) && (csMeeting.getCancelDate() != null) && (csMeeting.getAnnouncementDate().after(csMeeting.getCancelDate()))) {
					validation.addError("Announcement Date", "must be same or before than Cancel Date");
				}
			}

			String newUploadedFileName = "";
			Boolean isValid = true;
			if (fileAth != null) {
				try {
					Map<String,String> data = generalService.validateAttachment(fileAth.getName(), fileAth.length());
					isValid = Boolean.parseBoolean(data.get("valid"));
					
					if(!isValid) {
						validation.addError("", data.get("errorMsg"));
					}else {
						if (fileAth.getName().contains(";")) {
							String newName = fileAth.getName().replaceAll(";", ",");
							File newFile = new File(fileAth.getParentFile(), newName);
							try {
								UIHelper.copyFile(fileAth, newFile);
							} catch (RuntimeException e) {
								throw new Exception("Error runtime upload file ", e);
							} catch (Exception e) {
								throw new Exception("Error exception upload file ", e);
							}
							newUploadedFileName = clientFileService.doUploadZip(newFile, SimpleFileService.UPLOAD_SHAREHOLDERS);
						} else {
							newUploadedFileName = clientFileService.doUploadZip(fileAth, SimpleFileService.UPLOAD_SHAREHOLDERS);
						}
					}
					// newUploadedFileName =
					// clientFileService.doUploadZip(fileAth,
					// SimpleFileService.UPLOAD_SHAREHOLDERS);
					log.debug("done uploading " + newUploadedFileName);
					// setting uploaded file name
					csMeeting.setAttachement(newUploadedFileName);
				} catch (Exception e) {
					log.error("Error uploading file:" + (fileAth != null ? fileAth.getName() : fileAth), e);
					validation.addError("File", "Error Uploading file");
				}
			}
			
			if (validation.hasErrors()) {
				if (mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_ENTRY)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING));
				} else if (mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_EDIT)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_CANCEL));
				}
					render("ShareholdersMeetings/entry.html", csMeeting, mode, meetingTimeHour, meetingTimeMinute, meetingPlace1, meetingPlace2, meetingPlace3, isAttached);
				} else {
					csMeeting.setMeetingPlace(meetingPlace1 + FIELDDELIMITER + meetingPlace2 + FIELDDELIMITER + meetingPlace3);
					csMeeting.setMeetingTime(meetingTimeHour + TIMEDELIMITER + meetingTimeMinute);
	
					serializerService.serialize(session.getId(), csMeeting.getMeetingKey(), csMeeting);
					confirming(csMeeting.getMeetingKey(), mode, isAttached);
				}
			} else {
				// flash.error("argument.null", csMeeting);
				entry();
			}
	}

	public static void confirming(Long meetingKey, String mode, Boolean isAttached) {
		log.debug("confirming. meetingKey: " + meetingKey + " mode: " + mode + " isAttached: " + isAttached);

		boolean confirming = true;
		CsMeeting csMeeting = serializerService.deserialize(session.getId(), meetingKey, CsMeeting.class);

		renderArgs.put("editOnly", true);
		
		String nameFile = null;
		if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
			String[] data = csMeeting.getAttachement().split("-");
			nameFile = data[1];
			csMeeting.setFlagAttachFile(true);
		}

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

		if (mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING));
		} else if (mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_EDIT)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_CANCEL));
		}

		if ((csMeeting.getFlagAttachFile()!= null) &&  (csMeeting.getFlagAttachFile())) {
			nameFile = Helper.removeExtensionZip(nameFile);
		}
//		attachement = Helper.removeExtensionZip(attachement);
		
		render("ShareholdersMeetings/entry.html", csMeeting, mode, confirming, meetingTimeHour, meetingTimeMinute, meetingPlace1, meetingPlace2, meetingPlace3, isAttached, attachement, nameFile);
	}

	public static void confirm(CsMeeting csMeeting, String mode) {
		log.debug("confirm. csMeeting: " + csMeeting + " mode: " + mode);

		if (csMeeting == null)
			back(null, mode, false);
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

			CsMeeting meeting = null;
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				meeting = shareHolderService.saveMeeting(MenuConstants.CS_SHAREHOLDERS_MEETING, csMeeting, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			} else if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				meeting = shareHolderService.saveMeeting(MenuConstants.CS_SHAREHOLDERS_MEETING_CANCEL, csMeeting, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			}

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

	public static void back(Long meetingKey, String mode, Boolean isAttached) {
		log.debug("back. meetingKey: " + meetingKey + " mode: " + mode + " isAttached: " + isAttached);

		CsMeeting csMeeting = serializerService.deserialize(session.getId(), meetingKey, CsMeeting.class);

		String nameFile = null;
		if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
			String[] data = csMeeting.getAttachement().split("-");
			nameFile = data[1];
			csMeeting.setFlagAttachFile(true);
		}
		
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

		if (mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING));
		} else if (mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_EDIT)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_CANCEL));
		}

		if ((csMeeting.getFlagAttachFile() != null ) && (csMeeting.getFlagAttachFile())) {
			nameFile = Helper.removeExtensionZip(nameFile);
		}
		
		render("ShareholdersMeetings/entry.html", csMeeting, mode, meetingTimeHour, meetingTimeMinute, meetingPlace1, meetingPlace2, meetingPlace3, isAttached, attachement, nameFile);
	}

	@Check("transaction.shareHolderMeetingCancel")
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
		
		String nameFile = null;
		if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
			int dash = csMeeting.getAttachement().indexOf("-");
			nameFile = csMeeting.getAttachement().substring(dash + 1);
			csMeeting.setFlagAttachFile(true);
		}
		
		if ((csMeeting.getFlagAttachFile() != null ) && (csMeeting.getFlagAttachFile())) {
			nameFile = Helper.removeExtensionZip(nameFile);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_CANCEL));
		render("ShareholdersMeetings/entry.html", csMeeting, mode, meetingTimeHour, meetingTimeMinute, meetingPlace1, meetingPlace2, meetingPlace3, attachement, nameFile);
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsMeeting csMeeting = shareHolderService.getMeeting(id);
		
		String nameFile = null;
		if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
			String[] data = csMeeting.getAttachement().split("-");
			nameFile = data[1];
			csMeeting.setFlagAttachFile(true);
		}

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

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING));
		render("ShareholdersMeetings/entry.html", csMeeting, mode, meetingTimeHour, meetingTimeMinute, meetingPlace1, meetingPlace2, meetingPlace3, attachement, nameFile);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CsMeeting csMeeting = json.readValue(maintenanceLog.getNewData(), CsMeeting.class);

			String nameFile = null;
			if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
				String[] data = csMeeting.getAttachement().split("-");
				nameFile = data[1];
				csMeeting.setFlagAttachFile(true);
			}
			
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

			if ((csMeeting.getMeetingDate() != null) && (!Helper.isNullOrEmpty(csMeeting.getCancelRemarks()))) {
				renderArgs.put("editOnly", true);
			}

			String attachement = null;
			if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
				int dashPos = csMeeting.getAttachement().indexOf("-");
				attachement = csMeeting.getAttachement().substring(dashPos + 1);
			}
			
			if ((csMeeting.getFlagAttachFile() != null) && (csMeeting.getFlagAttachFile())) {
				nameFile = Helper.removeExtensionZip(nameFile);
			}

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ShareholdersMeetings/approval.html", csMeeting, mode, operation, meetingPlace1, meetingPlace2, meetingPlace3, attachement, maintenanceLogKey, from, taskId, nameFile);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			shareHolderService.approveMeeting(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			shareHolderService.approveMeeting(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void cancel(String mode, String param) {
		log.debug("cancel. mode: " + mode + " param: " + param);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_CANCEL));
		ShareholderMeetingSearchParameters params = new ShareholderMeetingSearchParameters();
		render("ShareholdersMeetings/cancel.html", mode, param, params);
	}

	public static void search(ShareholderMeetingSearchParameters params) {
		log.debug("search. params: " + params);

		List<CsMeeting> csMeetings = shareHolderService.searchMeeting(params.meetingDateFrom, params.meetingDateTo, params.searchThirdPartyKey, UIHelper.withOperator(params.meetingSearchNoOperator, params.MeetingNoOperator));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SHAREHOLDERS_MEETING_CANCEL));
		render("ShareholdersMeetings/grid.html", csMeetings);
	}

	@Check("transaction.shareHolderMeetingCancel")
	public static void meetingPaging(Paging page, ShareholderMeetingSearchParameters param) {
		log.debug("meetingPaging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("m.meetingDate", page.GREATEQUAL, param.meetingDateFrom);
			page.addParams("m.meetingDate", page.LESSEQUAL, param.meetingDateTo);
			page.addParams("i.thirdPartyKey", page.EQUAL, param.searchThirdPartyKey);
			page.addParams("m.meetingKey", page.LIKE, UIHelper.withOperator(param.meetingSearchNoOperator, param.MeetingNoOperator));
			page.addParams("m.recordStatus", page.EQUAL, "A");
			page.addParams("m.meetingStatus", page.EQUAL, "A");
			page.addParams("m.isActive", page.EQUAL, true);

			page.addParams(Helper.searchAll("(m.meetingKey||m.meetingSubject||m.security.securityType.securityType||m.security.securityId||to_char(m.announcementDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "to_char(m.meetingDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = shareHolderService.searchInquiryMeetingPaging(page);
		}
		renderJSON(page);
	}
	
	public static void shareHolderDownload(String id) {
		log.debug("shareHolderDownload = "+id);
		CsMeeting csMeeting = shareHolderService.getMeetingForUpload(new Long(id));
		String fileAttach = "";
		if (csMeeting != null) {
			if (!Helper.isNullOrEmpty(csMeeting.getAttachement())) {
				fileAttach = csMeeting.getAttachement();
			}
		}
		// downloadfile = downloadfile.substring(0,
		// downloadfile.lastIndexOf("|"));
		String uploadedDirOutput = Play.configuration.getProperty("upload.shareholder");
		String fullPath = uploadedDirOutput + fileAttach;
		File fileDest = new File(fullPath);

		if (fileDest.exists()) {
			log.debug("no need to download " + fileAttach + ", it's already downloaded");
		} else {
			log.debug("download " + fileAttach + "...");
			try {
				clientFileService.doDownload(fileAttach, fileDest, SimpleFileService.UPLOAD_SHAREHOLDERS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		log.debug("fullPath:" + fullPath);

		File file = null;
		try {
			file = Helper.extractSingleZipFile(fileDest);
		} catch (Exception e) {
		}

		renderBinary(file, file.getName());
	}
	
	private static String valueParam(String param) {
		log.debug("valueParam. param: " + param);

		GnSystemParameter sysParam = generalService.getSystemParameter(param);
		return sysParam.getValue();
	}
}
