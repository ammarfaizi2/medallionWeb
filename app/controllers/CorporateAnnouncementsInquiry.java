package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import play.Play;
import play.mvc.Before;
import play.mvc.With;
import vo.InquiryCorporateAnnoncementSearchParameter;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.ScAnnouncementDetail;
import com.simian.medallion.model.ScCorporateAnnouncement;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class CorporateAnnouncementsInquiry extends MedallionController {
	private static Logger log = Logger.getLogger(CorporateAnnouncementsInquiry.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "view" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> holdingBasedOn = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._HOLDING_BASED_ON);
		renderArgs.put("holdingBasedOn", holdingBasedOn);

		List<SelectItem> roundingType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ROUNDING_TYPE);
		renderArgs.put("roundingType", roundingType);

		renderArgs.put("targetTypeCash", LookupConstants.CA_TARGET_TYPE_CASH);
		renderArgs.put("targetTypeSame", LookupConstants.CA_TARGET_TYPE_SAME);
		renderArgs.put("targetTypeOther", LookupConstants.CA_TARGET_TYPE_OTHER);
		renderArgs.put("securityTypeCash", LookupConstants.__SECURITY_TYPE_CASH);
		//start end yusuf 6145 std dari 5974,rubah pencarian query untuk security id cash menjadi cash_MM
		//ScMaster security = securityService.getSecurity(LookupConstants.__SECURITY_TYPE_CASH, LookupConstants.__SECURITY_TYPE_CASH);
		ScMaster security = securityService.getSecurity(LookupConstants.__SECURITY_ID_CASH_MM, LookupConstants.__SECURITY_TYPE_CASH);
		//end yusuf
		renderArgs.put("securityKeyCash", security.getSecurityKey());

	}

	@Check("transaction.corporateannouncementsinquiry")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		InquiryCorporateAnnoncementSearchParameter params = new InquiryCorporateAnnoncementSearchParameter();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_INQUIRY_CORPORATE_ANNOUNCEMENT));
		render("CorporateAnnouncementsInquiry/list.html", params, mode);
	}

	@Check("transaction.corporateannouncementsinquiry")
	public static void search(InquiryCorporateAnnoncementSearchParameter params) {
		log.debug("search. params: " + params);

		List<ScCorporateAnnouncement> corporateAnnouncements = securityService.searchInquiryCorporateAnnoucement(params.dateFrom, params.dateTo, params.actionCode, params.securityType, params.securityCode, UIHelper.withOperator(params.corporateSearchNo, params.corporateNoOperator));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_INQUIRY_CORPORATE_ANNOUNCEMENT));
		for (ScCorporateAnnouncement corp : corporateAnnouncements) {
			if (corp.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED)) {
				corp.setStatus(LookupConstants.TRX_STATUS_WAITING_ENTITLEMENT_APPROVE);
			}
		}
		render("CorporateAnnouncementsInquiry/grid.html", corporateAnnouncements);
	}

	@Check("transaction.corporateannouncementsinquiry")
	public static void view(Long id, boolean fromDownload) {
		log.debug("view. id: " + id + " fromDownload: " + fromDownload);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(id);
		Set<ScAnnouncementDetail> announcements = corporateAnnouncement.getAnnouncementDetails();
		ScAnnouncementDetail announcementDetail = announcements.iterator().next();
		String inquiry = "inquiry";
		if (corporateAnnouncement.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED)) {
			corporateAnnouncement.setStatus(LookupConstants.TRX_STATUS_WAITING_ENTITLEMENT_APPROVE);
		}

		String nameFile = null;
		if (corporateAnnouncement.getDocument() != null) {
			int dash = corporateAnnouncement.getDocument().indexOf("-");
			nameFile = corporateAnnouncement.getDocument().substring(dash + 1);
			nameFile = Helper.removeExtensionZip(nameFile);
			corporateAnnouncement.setFlagAttachFile(true);
		}
		if (fromDownload) {
			validation.addError("", "File does not exist");
		}

		if (!corporateAnnouncement.getTaxable()) {
			if (corporateAnnouncement.getActionTemplate() != null) {
				corporateAnnouncement.getActionTemplate().setTaxObject(null);
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_INQUIRY_CORPORATE_ANNOUNCEMENT));
		render("CorporateAnnouncements/corporateAnnouncement.html", corporateAnnouncement, announcementDetail, mode, inquiry, nameFile);
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);
	}

	public static void save() {
		log.debug("save. ");
	}

	public static void confirming() {
		log.debug("confirming. ");
	}

	public static void confirm() {
		log.debug("confirm. ");
	}

	public static void back() {
		log.debug("back. ");
	}

	@Check("transaction.corporateannouncementsinquiry")
	public static void announcementdownload(String downloadfile) {
		log.debug("announcementdownload. downloadfile: " + downloadfile);

		// Long id = new
		// Long(downloadfile.substring(downloadfile.lastIndexOf("|") + 1,
		// downloadfile.length()));
		Long id = new Long(downloadfile);
		ScCorporateAnnouncement announcement = securityService.getCorporateAnnouncementByIdPlain(id);
		String fileAttach = "";
		if (announcement != null) {
			if (!Helper.isNullOrEmpty(announcement.getDocument())) {
				fileAttach = announcement.getDocument();
			}
		}
		// downloadfile = downloadfile.substring(0,
		// downloadfile.lastIndexOf("|"));
		String uploadedDirOutput = Play.configuration.getProperty("upload.announcement");
		String fullPath = uploadedDirOutput + fileAttach;
		File fileDest = new File(fullPath);

		if (fileDest.exists()) {
			log.debug("no need to download " + fileAttach + ", it's already downloaded");
		} else {
			log.debug("download " + fileAttach + "...");
			try {
				clientFileService.doDownload(fileAttach, fileDest, SimpleFileService.UPLOAD_CORPORATEANNOUNCEMENT);
			} catch (Exception e) {
				view(id, true);
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
}