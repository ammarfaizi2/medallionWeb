package controllers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.vo.SelectItem;

import helpers.UIConstants;
import helpers.UIHelper;
import play.Play;
import play.mvc.Before;
import play.mvc.With;
import vo.GenerateConfirmationParameter;

@With(Secure.class)
public class DownloadUnitTrust extends MedallionController {
	private static Logger log = Logger.getLogger(DownloadUnitTrust.class);

	@Before
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("utility.downloadunittrust")
	public static void list() {
		log.debug("list. ");

		GenerateConfirmationParameter params = new GenerateConfirmationParameter();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_DOWNLOAD_UNIT_TRUST));
		render("DownloadUnitTrust/list.html", params);
	}

	/*
	 * public static void generateFileDownload(String fund, String sa, String
	 * account, String dateFrom, String dateTo, boolean active, String
	 * downloadTo, String fundClassCode) {
	 * logger.debug("[GENERATE DOWNLOAD UNITTRUST] parameter " + fund + " : " +
	 * sa + " : " + account + " : " + dateFrom + " : " + dateTo + " : " + active
	 * + " : " + downloadTo+" : "+fundClassCode); String hasil[] = new
	 * String[3]; boolean isEmpty = true; try { List<Object[]> postParent =
	 * generalService.findUnitTrustParent(fund, sa, account, dateFrom, dateTo,
	 * active, true, fundClassCode); List<Object[]> postChild =
	 * generalService.findUnitTrustChild(fund, sa, account, dateFrom, dateTo,
	 * active, true, fundClassCode); List<Object[]> mailParent =
	 * generalService.findUnitTrustParent(fund, sa, account, dateFrom, dateTo,
	 * active, false, fundClassCode); List<Object[]> mailChild =
	 * generalService.findUnitTrustChild(fund, sa, account, dateFrom, dateTo,
	 * active, false, fundClassCode);
	 * 
	 * List<Object[]> dividen =
	 * downloadUtilityService.findDevidenUnitTrust(account, dateFrom, dateTo,
	 * RgTrade.TRADETYPE_CASH_FUND_ACTION, RgTrade.TRADESTATUS_APPROVE,
	 * RgTrade.TRADESTATUS_APPROVE);
	 * 
	 * logger.debug("[GENERATE DOWNLOAD] postParent size => " +
	 * postParent.size()); logger.debug("[GENERATE DOWNLOAD] postChild size => "
	 * + postChild.size());
	 * logger.debug("[GENERATE DOWNLOAD] mailParent size => " +
	 * mailParent.size()); logger.debug("[GENERATE DOWNLOAD] mailChild size => "
	 * + mailChild.size());
	 * 
	 * String fileName = (new SimpleDateFormat("ddMMyyyyhhmmss")).format(new
	 * Date()); StringBuffer bufferPost = new StringBuffer();
	 * bufferPost.append("POST"); bufferPost.append(fileName); StringBuffer
	 * bufferMail = new StringBuffer(); bufferMail.append("MAIL");
	 * bufferMail.append(fileName);
	 * 
	 * //String fullFileNamePost =
	 * Play.configuration.getProperty("download.tmp") + bufferPost.toString() +
	 * "." + downloadTo; //String fullFileNameMail =
	 * Play.configuration.getProperty("download.tmp") + bufferMail.toString() +
	 * "." + downloadTo;
	 * 
	 * if(postParent.size() > 0) { String fullFileNamePost =
	 * downloadUtilityService.downloadUnitTrustToTxt(postParent, postChild,
	 * dateFrom, dateTo, bufferPost.toString() + "." + downloadTo, dividen);
	 * hasil[1] = fullFileNamePost; isEmpty = false; } else { hasil[1] = ""; }
	 * 
	 * if(mailParent.size() > 0) { String fullFileNameMail =
	 * downloadUtilityService.downloadUnitTrustToTxt(mailParent,mailChild,
	 * dateFrom, dateTo, bufferMail.toString() + "." + downloadTo, dividen);
	 * hasil[2] = fullFileNameMail; isEmpty = false; } else { hasil[2] = ""; }
	 * 
	 * if(isEmpty) { hasil[0] = "empty"; } else { hasil[0] = "success"; }
	 * 
	 * } catch (Exception e) { hasil[0] = "fail"; hasil[1] = ""; hasil[2] = "";
	 * logger.error(e.getMessage(), e);
	 * logger.debug("error DownloadUnitTrust generateFileDownload "
	 * +e.getMessage()); } renderJSON(hasil); }
	 */

	public static void generateFileDownload(String fund, String sa, String cif, String dateFrom, String dateTo, boolean active, String downloadTo, String fundClassCode) {
		log.debug("generateFileDownload. fund: " + fund + " sa: " + sa + " cif: " + cif + " dateFrom: " + dateFrom + " dateTo: " + dateTo + " active: " + active + " downloadTo: " + downloadTo + " fundClassCode: " + fundClassCode);

		String hasil[] = new String[3];
		boolean isEmpty = true;

		try {
			String fileName = (new SimpleDateFormat("ddMMyyyyhhmmss")).format(new Date());

			String fullFileNamePost = downloadUtilityService.downloadUnitTrustToTxtPosted(fund, sa, cif, dateFrom, dateTo, active, downloadTo, fundClassCode, fileName);

			if (fullFileNamePost != null) {
				hasil[1] = fullFileNamePost;
				isEmpty = false;
			} else {
				hasil[1] = "";
			}

			String fullFileNameMail = downloadUtilityService.downloadUnitTrustToTxtUnPosted(fund, sa, cif, dateFrom, dateTo, active, downloadTo, fundClassCode, fileName);

			if (fullFileNameMail != null) {
				hasil[2] = fullFileNameMail;
				isEmpty = false;
			} else {
				hasil[2] = "";
			}

			if (isEmpty) {
				hasil[0] = "empty";
			} else {
				hasil[0] = "success";
			}

		} catch (Exception e) {
			hasil[0] = "fail";
			hasil[1] = "";
			hasil[2] = "";
			log.error(e.getMessage(), e);
			log.debug("error DownloadUnitTrust generateFileDownload " + e.getMessage());
		}
		renderJSON(hasil);
	}

	public static void downloadReport(String id) {
		log.debug("downloadReport. id: " + id);

		String fileName = "";
		File fileDest = null;
		try {
			fileName = downloadUtilityService.getDownloadUnitTrustToTxt(id);

			String simpleFilename = new File(fileName).getName();
			String downloadFolder = Play.configuration.getProperty("download.tmp");
			fileDest = new File(downloadFolder, simpleFilename);
			try {
				clientFileService.doDownload(simpleFilename, fileDest, SimpleFileService.DOWNLOAD_UNIT_TRUST_FILE);
			} catch (Exception e) {
				log.error("Error downloading file:" + simpleFilename, e);
			}

			String returnFile = null;
			if (!fileDest.exists()) {
				try {
					returnFile = json.writeValueAsString("not");
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
				renderJSON(returnFile);
			} else {
				renderBinary(fileDest, id);
			}

		} catch (Exception e1) {
			log.error("error DownloadUnitTrust downloadReport " + e1.getMessage(), e1);
		}
	}
}