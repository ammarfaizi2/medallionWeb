package controllers;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CaNews;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.AttachmentFile;
import com.simian.medallion.vo.SelectItem;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import play.Play;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.CaNewsParameters;

@With(Secure.class)
public class CANewsAnnouncement extends MedallionController {
	private static Logger log = Logger.getLogger(CANewsAnnouncement.class);
	
	@Check("transaction.canewsannouncement")
	public static void list() {
		log.debug("list. ");
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CA_NEWS_ANNOUCEMENT));
		render("CANewsAnnouncement/list.html");
	}
	
	@Check("transaction.canewsannouncement")
	public static void paging(Paging page, CaNewsParameters param) {
		log.debug("paging. page: " + page + " param: " + param);

		if (param.query) {
			page.addParams("a.ca_news_date", Paging.GREATEQUAL, param.fromDate);
			page.addParams("a.ca_news_date", Paging.LESSEQUAL, param.toDate);
			
			page.addParams("b.security_type", Paging.EQUAL, param.securityType);
			page.addParams("b.security_key", Paging.EQUAL, param.securityKey);
			
			page.addParams("(to_char(a.ca_news_date,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||b.security_type||b.security_id||a.subject||to_char(a.recording_date,'" + Helper.dateOracle(appProp.getDateFormat()) + "') )", Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
			page = securityService.pagingCaNews(page);
		}
		
		renderJSON(page);
	}

	@Check("transaction.canewsannouncement")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CaNews canews = securityService.getCaNews(id);
		
		// Prepare for attachment file
		String uploadProps = generalService.getSystemParameter(SystemParamConstants.ORGANIZATION_CANEWS_ATTACHMENT).getValue();
		String uploadDir = Play.configuration.getProperty("upload.canews");
		AttachmentFile attachment = new AttachmentFile(uploadProps, canews.getAttachmentFile(), uploadDir);
		serializerService.serialize(session.getId(), null, attachment);
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CA_NEWS_ANNOUCEMENT));
		render("CANewsAnnouncement/entry.html", canews, mode, attachment);
	}

	@Check("transaction.canewsannouncement")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CaNews canews = new CaNews();
		canews.setNewsDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		canews.setDeliverDate(canews.getNewsDate());
		
		// Prepare for attachment file
		String uploadProps = generalService.getSystemParameter(SystemParamConstants.ORGANIZATION_CANEWS_ATTACHMENT).getValue();
		String uploadDir = Play.configuration.getProperty("upload.canews");
		AttachmentFile attachment = new AttachmentFile(uploadProps, canews.getAttachmentFile(), uploadDir);
		serializerService.serialize(session.getId(), null, attachment);
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CA_NEWS_ANNOUCEMENT));
		render("CANewsAnnouncement/entry.html", canews, mode, attachment);
	}

	@Check("transaction.canewsannouncement")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CaNews canews = securityService.getCaNews(id);
		
		String status = canews.getRecordStatus();
		
		// Prepare for attachment file
		String uploadProps = generalService.getSystemParameter(SystemParamConstants.ORGANIZATION_CANEWS_ATTACHMENT).getValue();
		String uploadDir = Play.configuration.getProperty("upload.canews");
		AttachmentFile attachment = new AttachmentFile(uploadProps, canews.getAttachmentFile(), uploadDir);
		serializerService.serialize(session.getId(), null, attachment);
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CA_NEWS_ANNOUCEMENT));
		render("CANewsAnnouncement/entry.html", canews, mode, status, attachment);
	}

	@Check("transaction.canewsannouncement")
	public static void save(CaNews canews, String mode, String status, com.simian.medallion.vo.AttachmentFile attachment) {
		log.debug("save. canews: " + canews + " mode: " + mode + " status: " + status+" attachment: "+attachment);
		
		
		if (canews != null) {
			validation.required("Security Type", canews.getSecurity().getSecurityType().getSecurityType().trim());
			validation.required("Security Code", canews.getSecurity().getSecurityId().trim());
			validation.required("Recording Date", canews.getRecordingDate());
			validation.required("Subject", canews.getSubject().trim());
			validation.required("Narrative", removeTags(canews.getNarrative().trim()));
			validation.required("Deliver Date", canews.getDeliverDate());
			
			if (canews.getSubject().length() > 100) {
				validation.addError("", "Subject maxlength (100 charcters)");
			}
			
			if (canews.getNewsDate() != null && canews.getDeliverDate() != null) {
				if (canews.getDeliverDate().compareTo(canews.getNewsDate()) < 0) {
					validation.addError("", "Delivery Date must be greater or equal than News Date");
				}
			}

			if (attachment.isAddAttachment()) { //  UI Attach Document di click yes
				if (attachment.getFile() == null) { // User click Attach Document tp tidak browser file, ada 2 kemungkinan (1. dia emang gax browser, 2. file sudha di pilih sebelumnya)
					if (Helper.isEmpty(attachment.getFilepath())) { // User click attach tp tidak pernah browse file
						validation.addError("", "Attach Document is required");	
					}					
				}else {
					boolean isAttachmentValid = true;
					
					// Ukuran file melebihi batas yang di tentukan
					if ((attachment.getFile().length()/1024) > attachment.getAttachmentMaxSize()) {
						isAttachmentValid = false;
						validation.addError("", "Size of file max " + attachment.getAttachmentMaxSizeStr());
					}

					// file ext tidak terdaftar file yang allow
					String ext = FilenameUtils.getExtension(attachment.getFile().getName()).toLowerCase();
					if (attachment.getAttachmentExt().indexOf(ext) == -1) {
						isAttachmentValid = false;
						validation.addError("", "Invalid Type Document on Attachment Document "+attachment.getAttachmentExt());
					}
					
					if (isAttachmentValid) {
						
						// User mengganti file yang attach, lalukan hapus yang sebelumnya
						//if (!Helper.isEmpty(attachment.getFilepath())) {
							//(new File(attachment.getFilepath())).delete();
						//} // Depricated soalnya bisa aja dia save click lalu di keluar
						
						// lalukan penyimpanan dari folder temp, ke folder canews
						attachment.setFilename(attachment.getFile().getName()); // harus set manual jgn di setFile
						attachment.setFilepath(attachment.getFileDirectory() + System.currentTimeMillis() + "-" + attachment.getFilename());

						File output = new File(attachment.getFilepath());
						UIHelper.copyFile(attachment.getFile(), output);
						
						attachment.getFile().delete();
						attachment.setFile(null);
					}
				}
			}else {
				attachment.setFile(null);
				attachment.setFilepath(null);
				attachment.setFilename(null);
				
				// Depricated soalnya bisa aja dia save click lalu di keluar
				//if (!Helper.isEmpty(attachment.getFilepath())) {
					//(new File(attachment.getFilepath())).delete();
					//attachment.setFilename(null);
					//attachment.setFilepath(null);
				//}
			}
			
			if (validation.hasErrors()) {				
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CA_NEWS_ANNOUCEMENT));
				render("CANewsAnnouncement/entry.html", canews, mode, attachment);
			} else {
				Long id = canews.getCaNewsKey();
				serializerService.serialize(session.getId(), id, canews);
				
				serializerService.serialize(session.getId(), null, attachment);
				
				confirming(id, mode, status);
			}
		} else {
			entry();
		}
	}

	@Check("transaction.canewsannouncement")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		CaNews canews = serializerService.deserialize(session.getId(), id, CaNews.class);
		AttachmentFile attachment = serializerService.deserialize(session.getId(), null, AttachmentFile.class);
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CA_NEWS_ANNOUCEMENT));
		render("CANewsAnnouncement/entry.html", canews, mode, attachment);
	}

	@Check("transaction.canewsannouncement")
	public static void confirm(CaNews canews, String mode, String status) {
		log.debug("confirm. canews: " + canews + " mode: " + mode + " status: " + status);
		
		log.debug("NewsDate " +canews.getNewsDate());
		log.debug("SecurityType " +canews.getSecurity().getSecurityType().getSecurityType());
		log.debug("SecurityType Description " +canews.getSecurity().getSecurityType().getDescription());
		log.debug("SecurityKey " +canews.getSecurity().getSecurityKey());
		log.debug("Security Id " +canews.getSecurity().getSecurityId());
		log.debug("Security Description " +canews.getSecurity().getDescription());
		log.debug("Recording Date " +canews.getRecordingDate());
		log.debug("Subject " +canews.getSubject());
		log.debug("Narrative " +canews.getNarrative());
		log.debug("DeliverDate " +canews.getDeliverDate());
		
		// Check attachment properties, bila valid maka safe canews with attachment
		AttachmentFile attachment = serializerService.deserialize(session.getId(), null, AttachmentFile.class);
		if (!Helper.isNull(attachment) && !Helper.isEmpty(attachment.getFilepath())) {
			File file = new File(attachment.getFilepath());
			if (file.exists()) canews.setAttachmentFile(file.getAbsolutePath());
		}else {
			canews.setAttachmentFile(null);
		}
		
		try {
			//if (true) throw new MedallionException("Sengaja GUe errorin");	
			if (canews == null)
				back(null, mode, status);
			
			securityService.saveCaNews(MenuConstants.CA_NEWS_ANNOUCEMENT, canews, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Ca News : " + Messages.get(e.getMessage()));
			boolean confirming = true;
			
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CA_NEWS_ANNOUCEMENT));
			render("CANewsAnnouncement/entry.html", canews, mode, confirming, attachment);
		}
	}

	@Check("transaction.canewsannouncement")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + ", status: " + status);

		CaNews canews = serializerService.deserialize(session.getId(), id, CaNews.class);
		AttachmentFile attachment = serializerService.deserialize(session.getId(), null, AttachmentFile.class);
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CA_NEWS_ANNOUCEMENT));
		render("CANewsAnnouncement/entry.html", canews, mode, attachment);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CaNews canews = json.readValue(maintenanceLog.getNewData(), CaNews.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			
			String strNewsDate = Helper.formatDMY(canews.getNewsDate());
			
			// Prepare for attachment file
			String uploadProps = generalService.getSystemParameter(SystemParamConstants.ORGANIZATION_CANEWS_ATTACHMENT).getValue();
			String uploadDir = Play.configuration.getProperty("upload.canews");
			AttachmentFile attachment = new AttachmentFile(uploadProps, canews.getAttachmentFile(), uploadDir);
			serializerService.serialize(session.getId(), null, attachment);
			
			render("CANewsAnnouncement/approval.html", canews, mode, taskId, operation, maintenanceLogKey, from, strNewsDate, attachment);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			securityService.approveCaNews(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) { renderJSON(Formatter.resultError(e)); 
		} catch (Exception e) { renderJSON(Formatter.resultError(e)); }
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			securityService.approveCaNews(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) { renderJSON(Formatter.resultError(e));
		} catch (Exception e) { renderJSON(Formatter.resultError(e)); }
	}
	
	@Before(only = { "entry", "view", "edit", "save", "confirming", "confirm", "approval", "back" })
	public static void setEntryAttribute() {
		List<SelectItem> variables = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CA_NEWS_VARIABLE);
		renderArgs.put("variables", variables);
		
		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}
	
	public static void downloadAttachment() {
		log.debug("downloadAttachment");
		
		AttachmentFile attachment = serializerService.deserialize(session.getId(), null, AttachmentFile.class);

		File file = new File(attachment.getFilepath());
		renderBinary(file, file.getName());
	}
	
	public static String removeTags(String string) {
		Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
	    if (string == null || string.length() == 0) {
	        return string;
	    }

	    Matcher m = REMOVE_TAGS.matcher(string);
	    return m.replaceAll("");
	}
}
