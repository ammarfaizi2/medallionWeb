package controllers;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScAnnouncementDetail;
import com.simian.medallion.model.ScCorporateAnnouncement;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.ScTypeMaster;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.vo.SelectItem;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import play.i18n.Messages;
import play.mvc.Before;
import vo.AnnouncementSearchParameter;

public class CorporateAnnouncements extends MedallionController {
	private static Logger log = Logger.getLogger(CorporateAnnouncements.class);

	private static final String maxSize = "Size of file max ";
	
	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		AnnouncementSearchParameter params = new AnnouncementSearchParameter();
		renderArgs.put("stringOperators", operators);
		renderArgs.put("params", params);
	}

	@Before(only = { "entry", "edit", "save", "confirming", "confirm", "back", "clear", "approval" })
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
		//start end yusuf 6145 std dari 5974 rubah pencarian query untuk security id cash menjadi cash_MM
		//ScMaster security = securityService.getSecurity(LookupConstants.__SECURITY_TYPE_CASH, LookupConstants.__SECURITY_TYPE_CASH);
		ScMaster security = securityService.getSecurity(LookupConstants.__SECURITY_ID_CASH_MM, LookupConstants.__SECURITY_TYPE_CASH);
		//end yusuf
		renderArgs.put("securityKeyCash", security.getSecurityKey());
		
		String validateAttach = valueParam(SystemParamConstants.ORGANIZATION_CORP_ANNOUNCE_ATTACHMENT);
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

	/*
	 * @Check("transaction.corporateActionAnnouncement") public static void
	 * search(AnnouncementSearchParameter params) {
	 * List<ScCorporateAnnouncement> corporateAnnouncements =
	 * securityService.searchAnnouncements(params.actionCode,
	 * params.securityType, params.securityCode, params.dateFrom, params.dateTo,
	 * UIHelper.withOperator(params.announcementNo,
	 * params.announcementNoOperator));
	 * render("CorporateAnnouncements/grid.html", corporateAnnouncements); }
	 */

	@Check("transaction.corporateActionAnnouncement")
	public static void search(Paging page, AnnouncementSearchParameter param) {
		log.debug("search. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("at.actionTemplateKey", page.EQUAL, param.actionCode);
			page.addParams("st.securityType", page.EQUAL, param.securityType);
			page.addParams("se.securityKey", page.EQUAL, param.securityCode);
			page.addParams("ca.distributionDate", page.GREATEQUAL, param.dateFrom);
			page.addParams("ca.distributionDate", page.LESSEQUAL, param.dateTo);
			page.addParams("ca.corporateAnnouncementCode", param.announcementNoOperator, UIHelper.withOperator(param.announcementNo, param.announcementNoOperator));
			// page.addParams("trim(ca.recordStatus)", page.EQUAL,
			// LookupConstants.__RECORD_STATUS_NEED_REVISION);
			// page.addParams("trim(ca.status)", page.EQUAL,
			// LookupConstants.__RECORD_STATUS_REJECTED);
			page.addParams(Helper.searchAll("(ca.corporateAnnouncementCode||ca.description||to_char(ca.announcementDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "to_char(ca.recordingDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||to_char(ca.distributionDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = securityService.pagingAnnouncement(page);
		}

		renderJSON(page);
	}

	@Check("transaction.corporateActionAnnouncement")
	public static void list(String mode) {
		log.debug("list. mode: " + mode);

		List<ScCorporateAnnouncement> corporateAnnouncements = securityService.listCorporateAnnouncement(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ANNOUNCEMENT));
		render("CorporateAnnouncements/listpaging.html", corporateAnnouncements);
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	@Check("transaction.corporateActionAnnouncement")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		ScCorporateAnnouncement corporateAnnouncement = new ScCorporateAnnouncement();
		corporateAnnouncement.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		corporateAnnouncement.setRoundingValue(BigDecimal.ZERO);
		corporateAnnouncement.setChargeable(true);
		corporateAnnouncement.setAutoEmail(true);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ANNOUNCEMENT));

		renderTemplate("CorporateAnnouncements/corporateAnnouncement.html", mode, corporateAnnouncement);
	}

	@Check("transaction.corporateActionAnnouncement")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(id);
		String nameFile = null;
		if (corporateAnnouncement.getDocument() != null) {
			int dash = corporateAnnouncement.getDocument().indexOf("-");
			nameFile = corporateAnnouncement.getDocument().substring(dash + 1);
			nameFile = Helper.removeExtensionZip(nameFile);
			corporateAnnouncement.setFlagAttachFile(true);
		}
		if (corporateAnnouncement.getTaxable() != null) {
			if (corporateAnnouncement.getTaxable() == false) {
				corporateAnnouncement.getActionTemplate().setTaxObject(null);
			}
		}
		Set<ScAnnouncementDetail> announcements = corporateAnnouncement.getAnnouncementDetails();
		ScAnnouncementDetail announcementDetail = announcements.iterator().next();
		if (corporateAnnouncement.getActionTemplate().getTargetType().getLookupId().equals(LookupConstants.CA_TARGET_TYPE_CASH)) {
			announcementDetail.setSecurityTypeTarget(new ScTypeMaster());
			announcementDetail.getSecurityTypeTarget().setSecurityType("CASH");
			announcementDetail.getSecurityTypeTarget().setDescription("CASH");
			announcementDetail.setSecurityTarget(new ScMaster());
			//start end yusuf 6145 std dari 5974 rubah securityID cash jadi cash_MM
			//announcementDetail.getSecurityTarget().setSecurityId("CASH");
			announcementDetail.getSecurityTarget().setSecurityId(LookupConstants.__SECURITY_ID_CASH_MM);
			//end yusuf
			announcementDetail.getSecurityTarget().setDescription("CASH");
		}
		corporateAnnouncement.getActionTemplate().setExercisePriceTamp(corporateAnnouncement.getActionTemplate().getExercisePrice());
		log.debug("Announcement Detail Coupon = " + announcementDetail.getCouponSchedule());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ANNOUNCEMENT));
		render("CorporateAnnouncements/corporateAnnouncement.html", corporateAnnouncement, announcementDetail, nameFile, mode);

	}

	@Check("transaction.corporateActionAnnouncement")
	public static void save(String mode, ScCorporateAnnouncement corporateAnnouncement, ScAnnouncementDetail announcementDetail, File attachment) {
		log.debug("save. mode: " + mode + " corporateAnnouncement: " + corporateAnnouncement + " announcementDetail: " + announcementDetail + " attachment: " + attachment);

		if (corporateAnnouncement != null) {
			log.debug("tax object = " + corporateAnnouncement.getActionTemplate().getTaxObject().getLookupId());
			// validation.required("corporateAnnouncement.corporateAnnouncementCode",
			// corporateAnnouncement.getCorporateAnnouncementCode());
			validation.required("Subject is", corporateAnnouncement.getDescription());
			validation.required("Security Type is", corporateAnnouncement.getSecurityType().getSecurityType());
			validation.required("Security Code is", corporateAnnouncement.getSecurity().getSecurityId());
			validation.required("Action Code is", corporateAnnouncement.getActionTemplate().getActionCode());
			if (corporateAnnouncement.isFlagAttachFile()) {
				if (corporateAnnouncement.getDocument().isEmpty())
					validation.required("Attach Document is", attachment);

				/*if (attachment != null) {
					double sizeFile = attachment.length() / 1024;
					if (sizeFile > 200) {
						validation.addError("", "Size of file max 200 Kb");
					}
				}*/
			}

			if (corporateAnnouncement.getActionTemplate().getTaxObject() != null) {
				if (corporateAnnouncement.getActionTemplate().getExercisePrice() != null) {
					if (corporateAnnouncement.getActionTemplate().getExercisePrice())
						validation.required("Exercise Price is", corporateAnnouncement.getExercisePrice());
				}

				if (corporateAnnouncement.getTaxable() != null) {
					if (corporateAnnouncement.getTaxable()) {
						if (corporateAnnouncement.getActionTemplate().getActionTemplateLink() != null) {
							if (corporateAnnouncement.getActionTemplate().getActionTemplateLink().getActionTemplateKey() != null)
								validation.required("Tax applied to is", corporateAnnouncement.getTaxCaLink().getCorporateAnnouncementCode());
						}
					}
				}

			}
			if (corporateAnnouncement.getActionTemplate().getIsCoupon() != null) {
				if (corporateAnnouncement.getActionTemplate().getIsCoupon())
					validation.required("Coupon Schedule is", announcementDetail.getCouponNo());
			}
			validation.required("Announcement Date is", corporateAnnouncement.getAnnouncementDate());
			validation.required("Cum Date is", corporateAnnouncement.getCumDate());
			validation.required("Ex Date is", corporateAnnouncement.getExDate());
			validation.required("Recording Date is", corporateAnnouncement.getRecordingDate());
			validation.required("Distribution Date is", corporateAnnouncement.getDistributionDate());
			if (corporateAnnouncement.getActionTemplate().getTargetType() != null) {
				if (corporateAnnouncement.getActionTemplate().getTargetType().getLookupId().equals(LookupConstants.CA_TARGET_TYPE_OTHER)) {
					validation.required("Security Type Target in tab Ratio is", announcementDetail.getSecurityTypeTarget().getSecurityType());
					validation.required("Security Code Target in tab Ratio is", announcementDetail.getSecurityTarget().getSecurityId());
				}
			}
			if (corporateAnnouncement.getActionTemplate().getIsCoupon() != null) {
				if (corporateAnnouncement.getActionTemplate().getIsCoupon() == false) {
					validation.required("Ratio (X:Y) in tab Ratio is", announcementDetail.getSourceQuantity() != null ? announcementDetail.getSourceQuantity() : announcementDetail.getTargetQuantity());
				}
			}
			validation.required("Rounding Type in tab Ratio is", corporateAnnouncement.getRoundingType().getLookupId());
			validation.required("No. of decimal in tab Ratio is", corporateAnnouncement.getRoundingValue());

			// validation.required("Security Type source and target is", co)
			if (corporateAnnouncement.getActionTemplate().getTaxObject() != null)
				corporateAnnouncement.getActionTemplate().setTaxObject(generalService.getLookup(corporateAnnouncement.getActionTemplate().getTaxObject().getLookupId()));

			/*
			 * if (announcementDetail.getCouponSchedule()!=null){
			 * announcementDetail
			 * .setCouponSchedule(securityService.getCouponScheduleFilter
			 * (announcementDetail.getCouponSchedule().getId().getSecurityKey(),
			 * announcementDetail.getCouponSchedule().getId().getCouponNo()));
			 * 
			 * }
			 */

			Date announcementDate = corporateAnnouncement.getAnnouncementDate();
			Date cumDate = corporateAnnouncement.getCumDate();
			Date recordingDate = corporateAnnouncement.getRecordingDate();
			Date exDate = corporateAnnouncement.getExDate();
			Date distributionDate = corporateAnnouncement.getDistributionDate();

			if ((announcementDate != null) && (cumDate != null) && (recordingDate != null) && (exDate != null) && (distributionDate != null)) {
				if ((cumDate.getTime() < announcementDate.getTime()) || (exDate.getTime() < cumDate.getTime()) || (recordingDate.getTime() < exDate.getTime()) || (distributionDate.getTime() < recordingDate.getTime())) {
					// flash.error(
					// "Check date's sequence and complete the form");
					validation.addError("", "Check date's sequence and complete the form");
				}
			}

			// file upload
			boolean isAttachment = false;
			boolean isValid = true;
			log.debug("announcement file = " + attachment);
			String newUploadedFileName = "";
			if (attachment != null) {
				try {
					Map<String,String> data = generalService.validateAttachment(attachment.getName(), attachment.length());
					isValid = Boolean.parseBoolean(data.get("valid"));
					log.debug("isMatchAttach = "+isValid);
					log.debug("data valid = "+data.get("valid"));
					log.debug("data error = "+data.get("errorMsg"));
					
					if(!isValid) {
						validation.addError("", data.get("errorMsg"));
					}else {
						if (attachment.getName().contains(";")) {
							String newName = attachment.getName().replaceAll(";", ",");
							File newFile = new File(attachment.getParentFile(), newName);
							try {
								UIHelper.copyFile(attachment, newFile);
							} catch (RuntimeException e) {
								throw new Exception("Error runtime upload file ", e);
							} catch (Exception e) {
								throw new Exception("Error exception upload file ", e);
							}
							newUploadedFileName = clientFileService.doUploadZip(newFile, SimpleFileService.UPLOAD_CORPORATEANNOUNCEMENT);
						} else {
							newUploadedFileName = clientFileService.doUploadZip(attachment, SimpleFileService.UPLOAD_CORPORATEANNOUNCEMENT);
						}

						// newUploadedFileName =
						// clientFileService.doUpload(attachment,
						// SimpleFileService.UPLOAD_CORPORATEANNOUNCEMENT);

						log.debug("done uploading " + newUploadedFileName);
						// setting uploaded file name
						corporateAnnouncement.setDocument(newUploadedFileName);
						isAttachment = true;
					}
				} catch (Exception e) {
					log.error("Error uploading file:" + (attachment != null ? attachment.getName() : attachment), e);
					validation.addError("Attach Document is", "Error Uploading file");
				}
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ANNOUNCEMENT));
				render("CorporateAnnouncements/corporateAnnouncement.html", corporateAnnouncement, announcementDetail, mode);
			} else {
				Set<ScAnnouncementDetail> announcementDetails = new HashSet<ScAnnouncementDetail>();
				if (announcementDetail != null) {
					announcementDetails.add(announcementDetail);
				}

				corporateAnnouncement.setAnnouncementDetails(announcementDetails);
				Long id = corporateAnnouncement.getCorporateAnnouncementKey();
				// setData(corporateAnnouncement, announcements);
				serializerService.serialize(session.getId(), id, corporateAnnouncement);
				confirming(id, mode, isAttachment);
			}
		} else {
			// flash.error("argument.null", corporateAnnouncement);
			entry();
		}
	}

	@Check("transaction.corporateActionAnnouncement")
	public static void confirming(Long id, String mode, boolean isAttachment) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isAttachment: " + isAttachment);

		boolean confirming = true;
		ScCorporateAnnouncement corporateAnnouncement = serializerService.deserialize(session.getId(), id, ScCorporateAnnouncement.class);
		Set<ScAnnouncementDetail> announcementDetails = corporateAnnouncement.getAnnouncementDetails();
		ScAnnouncementDetail announcementDetail = announcementDetails.iterator().next();
		String nameFile = null;
		if (!corporateAnnouncement.getDocument().isEmpty()) {
			int dash = corporateAnnouncement.getDocument().indexOf("-");
			nameFile = corporateAnnouncement.getDocument().substring(dash + 1);
			nameFile = Helper.removeExtensionZip(nameFile);
			corporateAnnouncement.setFlagAttachFile(true);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ANNOUNCEMENT));
		render("CorporateAnnouncements/corporateAnnouncement.html", mode, confirming, corporateAnnouncement, announcementDetail, nameFile);
	}

	@Check("transaction.corporateActionAnnouncement")
	public static void confirm(String mode, ScCorporateAnnouncement corporateAnnouncement, ScAnnouncementDetail announcementDetail) {
		log.debug("confirm. mode: " + mode + " corporateAnnouncement: " + corporateAnnouncement + " announcementDetail: " + announcementDetail);

		// List<ScAnnouncementDetail> announcements) {
		if (corporateAnnouncement == null)
			back(null, mode, corporateAnnouncement);
		boolean confirming = true;
		try {
			corporateAnnouncement.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
			// setData(corporateAnnouncement, announcements);
			/*
			 * if(announcements != null) { for (ScAnnouncementDetail
			 * announcement : announcements) { if
			 * (announcement.getCouponSchedule() != null) { if
			 * (announcement.getSecuritySource() != null &&
			 * announcement.getSecuritySource().getSecurityKey() != null) {
			 * announcement
			 * .getCouponSchedule().getId().setSecurityKey(announcement
			 * .getSecuritySource().getSecurityKey()); } else {
			 * announcement.getCouponSchedule
			 * ().getId().setSecurityKey(announcement
			 * .getSecurityTarget().getSecurityKey());
			 * logger.debug(">>>>> [FRS] CEK SECURITY KEY ? " +
			 * announcement.getSecurityTarget().getSecurityKey()); }
			 * logger.debug(">>>>> [FRS] CEK COUPON NO ? " +
			 * announcement.getCouponSchedule().getId().getCouponNo());
			 * 
			 * } } }
			 */
			Set<ScAnnouncementDetail> announcementDetails = new HashSet<ScAnnouncementDetail>();
			if (announcementDetail != null) {
				if (corporateAnnouncement.getActionTemplate().getTargetType().getLookupId().equals(LookupConstants.CA_TARGET_TYPE_CASH)) {
					announcementDetail.setSecurityTypeTarget(announcementDetail.getSecurityTypeSource());
					announcementDetail.setSecurityTarget(announcementDetail.getSecuritySource());
				}
				log.debug("announcementDetail.getCouponSchedule()  = " + announcementDetail.getCouponSchedule());
				log.debug("announcementDetail.getCouponSchedule().getId()  = " + announcementDetail.getCouponSchedule().getId());
				log.debug("announcementDetail.getCouponSchedule().getId().getcoupon  = " + announcementDetail.getCouponSchedule().getId().getCouponNo());
				log.debug("announcementDetail.getCouponSchedule().getId().getsecurity  = " + announcementDetail.getCouponSchedule().getId().getSecurityKey());
				if (announcementDetail.getCouponSchedule().getId() != null) {
					// if (announcementDetail.getSecuritySource() != null &&
					// announcementDetail.getSecuritySource().getSecurityKey()
					// != null) {
					announcementDetail.getCouponSchedule().getId().setSecurityKey(corporateAnnouncement.getSecurity().getSecurityKey());
					/*
					 * } else {
					 * announcementDetail.getCouponSchedule().getId().setSecurityKey
					 * (
					 * announcementDetail.getSecurityTarget().getSecurityKey());
					 * logger.debug(">>>>> [FRS] CEK SECURITY KEY ? " +
					 * announcementDetail.getSecurityTarget().getSecurityKey());
					 * }
					 */
				}

				announcementDetails.add(announcementDetail);
			}
			corporateAnnouncement.setAnnouncementDetails(announcementDetails);

			if (corporateAnnouncement.getChargeable() == null) {
				corporateAnnouncement.setChargeable(false);
			}
			if (corporateAnnouncement.getTaxable() == null) {
				corporateAnnouncement.setTaxable(false);
			}
			if (corporateAnnouncement.getAutoEmail() == null) {
				corporateAnnouncement.setAutoEmail(false);
			}

			// ScCorporateAnnouncement corpoAnnouncement = new
			// ScCorporateAnnouncement();
			if (corporateAnnouncement.getActionTemplate() != null) {
				corporateAnnouncement.getActionTemplate().setTaxObjectTamp(null);
				corporateAnnouncement.getActionTemplate().setExercisePriceTamp(null);
			}
			ScCorporateAnnouncement corpoAnnouncement = securityService.createNewAnnouncement(MenuConstants.CS_CORPORATE_ACTION_ANNOUNCEMENT, corporateAnnouncement, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY), "");
			if (corpoAnnouncement.getCorporateAnnouncementKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				// result.put("message",
				// Messages.get("announcement.confirmed.successful",
				// corpoAnnouncement.getCorporateAnnouncementKey()));
				if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
					result.put("message", Messages.get("announcement.confirmed.successful.edit", corpoAnnouncement.getCorporateAnnouncementCode()));
				}
				if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
					result.put("message", Messages.get("announcement.confirmed.successful", corpoAnnouncement.getCorporateAnnouncementCode()));
				}
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ANNOUNCEMENT));
				render("CorporateAnnouncements/corporateAnnouncement.html", corpoAnnouncement, mode, confirming);
			}
			// list(mode);
		} catch (MedallionException ex) {
			/*
			 * flash.error("Duplicate Error! Code : ' "+corporateAnnouncement.
			 * getCorporateAnnouncementCode()+" ' Already Exist",
			 * corporateAnnouncement.getCorporateAnnouncementCode());
			 * flash.put(UIConstants.BREADCRUMB,
			 * applicationService.getMenuBreadcrumb
			 * (MenuConstants.CS_CORPORATE_ACTION_ANNOUNCEMENT));
			 * render("CorporateAnnouncements/detail.html",
			 * corporateAnnouncement, mode, confirming, announcementDetail);
			 */
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if (ex != null && ex.getParams() != null) {
				for (Object paramItem : ex.getParams()) {
					params.add(Messages.get(paramItem));
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

	@Check("transaction.corporateActionAnnouncement")
	public static void back(Long id, String mode, ScCorporateAnnouncement corporateAnnouncement) {
		log.debug("back. id: " + id + " mode: " + mode + " corporateAnnouncement: " + corporateAnnouncement);

		corporateAnnouncement = serializerService.deserialize(session.getId(), id, ScCorporateAnnouncement.class);

		Set<ScAnnouncementDetail> announcements = corporateAnnouncement.getAnnouncementDetails();
		ScAnnouncementDetail announcementDetail = announcements.iterator().next();
		String nameFile = null;
		if (!corporateAnnouncement.getDocument().isEmpty()) {
			int dash = corporateAnnouncement.getDocument().indexOf("-");
			nameFile = corporateAnnouncement.getDocument().substring(dash + 1);
			nameFile = Helper.removeExtensionZip(nameFile);
			corporateAnnouncement.setFlagAttachFile(true);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ANNOUNCEMENT));
		render("CorporateAnnouncements/corporateAnnouncement.html", corporateAnnouncement, mode, announcementDetail, nameFile);
	}

	@Check("transaction.corporateActionAnnouncement")
	public static void clear(String mode) {
		log.debug("clear. mode: " + mode);

		ScCorporateAnnouncement corporateAnnouncement = new ScCorporateAnnouncement();
		corporateAnnouncement.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		render("CorporateAnnouncements/detail.html", corporateAnnouncement, mode);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, String processDefinition, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " processDefinition: " + processDefinition + " maintenanceLogKey: " + maintenanceLogKey);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(keyId);
		if (corporateAnnouncement.getTaxable() != null) {
			if (corporateAnnouncement.getTaxable() == false) {
				corporateAnnouncement.getActionTemplate().setTaxObject(null);
			}
		}
		Set<ScAnnouncementDetail> announcements = corporateAnnouncement.getAnnouncementDetails();
		ScAnnouncementDetail announcementDetail = announcements.iterator().next();
		if (corporateAnnouncement.getActionTemplate().getTargetType().getLookupId().equals(LookupConstants.CA_TARGET_TYPE_CASH)) {
			announcementDetail.setSecurityTypeTarget(new ScTypeMaster());
			announcementDetail.getSecurityTypeTarget().setSecurityType("CASH");
			announcementDetail.getSecurityTypeTarget().setDescription("CASH");
			announcementDetail.setSecurityTarget(new ScMaster());
			//start end yusuf 6145 std dari 5974 rubah securityID cash jadi cash_MM
			//announcementDetail.getSecurityTarget().setSecurityId("CASH");
			announcementDetail.getSecurityTarget().setSecurityId(LookupConstants.__SECURITY_ID_CASH_MM);
			//end yusuf
			announcementDetail.getSecurityTarget().setDescription("CASH");
		}
		corporateAnnouncement.setRemarkCorrection(null);
		String nameFile = null;
		if (corporateAnnouncement.getDocument() != null) {
			int dash = corporateAnnouncement.getDocument().indexOf("-");
			nameFile = corporateAnnouncement.getDocument().substring(dash + 1);
			nameFile = Helper.removeExtensionZip(nameFile);
			corporateAnnouncement.setFlagAttachFile(true);
		}
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		render("CorporateAnnouncements/approval.html", corporateAnnouncement, announcementDetail, taskId, mode, from, operation, maintenanceLogKey, keyId, nameFile);
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			// ScCorporateAnnouncement announcement =
			// securityService.approveAnnouncement(keyId,
			// session.get(UIConstants.SESSION_USERNAME), taskId);
			ScCorporateAnnouncement announcement = securityService.approveNewAnnouncement(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, null);

			renderJSON(Formatter.resultSuccess(Messages.get("announcement.approved", announcement.getCorporateAnnouncementCode())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}

	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			// ScCorporateAnnouncement announcement =
			// securityService.rejectAnnouncement(keyId, taskId,
			// session.get(UIConstants.SESSION_USERNAME));
			ScCorporateAnnouncement announcement = null;
			if (Helper.isNullOrEmpty(correction)) {
				announcement = securityService.approveNewAnnouncement(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, null);
			} else {
				announcement = securityService.approveNewAnnouncement(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "", correction);
			}

			renderJSON(Formatter.resultSuccess(Messages.get("announcement.rejected", announcement.getCorporateAnnouncementCode())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
	
	private static String valueParam(String param) {
		log.debug("valueParam. param: " + param);

		GnSystemParameter sysParam = generalService.getSystemParameter(param);
		return sysParam.getValue();
	}
}
