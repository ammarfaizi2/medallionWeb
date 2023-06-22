package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.Play;
import play.mvc.Before;
import play.mvc.With;
import vo.GenerateConfirmationParameter;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class DownloadUnitConfirmation extends MedallionController {
	private static Logger log = Logger.getLogger(DownloadUnitConfirmation.class);

	@Before
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("utility.downloadunitconfirmation")
	public static void list() {
		log.debug("list. ");

		GenerateConfirmationParameter params = new GenerateConfirmationParameter();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_DOWNLOAD_UNIT_CONFIRMATION));
		render("DownloadUnitConfirmation/list.html", params);
	}

	/*
	 * public static void generateFileDownload(String fund, String sa, String
	 * account, String dateFrom, String dateTo, String downloadTo){
	 * logger.debug("parameter "+fund+" : " + sa + " : "+ account+" : "+
	 * dateFrom+" : "+ dateTo+" : "+downloadTo); String hasil[] = new String[3];
	 * boolean isEmpty = true; try{ List<Object[]> post =
	 * generalService.findUnitConfirmation(fund, sa, account, dateFrom, dateTo,
	 * true); List<Object[]> mail = generalService.findUnitConfirmation(fund,
	 * sa, account, dateFrom, dateTo, false); String fileName = (new
	 * SimpleDateFormat("ddMMyyyyhhmmss")).format(new Date()); StringBuffer
	 * bufferPost = new StringBuffer(); bufferPost.append("POST");
	 * bufferPost.append(fileName); StringBuffer bufferMail = new
	 * StringBuffer(); bufferMail.append("MAIL"); bufferMail.append(fileName);
	 * 
	 * // String fullFileNamePost =
	 * Play.configuration.getProperty("download.tmp"
	 * )+bufferPost.toString()+"."+downloadTo; // String fullFileNameMail =
	 * Play.configuration.getProperty("download.tmp")+bufferMail.toString()+"."+
	 * downloadTo; if (post.size() > 0) { String fullFileNamePost =
	 * downloadUtilityService.downloadUnitConfirmToTxt(post,
	 * bufferPost.toString()+"."+downloadTo); hasil[1] = fullFileNamePost;
	 * isEmpty = false; } else { hasil[1] = ""; } if (mail.size() > 0) { String
	 * fullFileNameMail = downloadUtilityService.downloadUnitConfirmToTxt(mail,
	 * bufferMail.toString()+"."+downloadTo); hasil[2] = fullFileNameMail;
	 * isEmpty = false; } else { hasil[2] = ""; }
	 * 
	 * if(isEmpty){ hasil[0] = "empty"; }else{ hasil[0] = "success"; }
	 * 
	 * }catch (Exception e) { hasil[0] = "fail"; hasil[1] = ""; hasil[2] = "";
	 * logger.error(e.getMessage(), e);
	 * logger.debug("error DownloadUnitConfirmation generateFileDownload "
	 * +e.getMessage()); } renderJSON(hasil); }
	 */

	public static void generateFileDownload(String fund, String sa, String account, String dateFrom, String dateTo, String downloadTo) {
		log.debug("generateFileDownload. fund: " + fund + " sa: " + sa + " account: " + account + " dateFrom: " + dateFrom + " dateTo: " + dateTo + " downloadTo: " + downloadTo);

		String hasil[] = new String[3];
		boolean isEmpty = true;

		try {
			String fileName = (new SimpleDateFormat("ddMMyyyyhhmmss")).format(new Date());

			String fullFileNamePost = downloadUtilityService.downloadUnitConfirmToTxtPosted(fund, sa, account, dateFrom, dateTo, downloadTo, fileName);
			if (fullFileNamePost != null) {
				hasil[1] = fullFileNamePost;
				isEmpty = false;
			} else {
				hasil[1] = "";
			}

			String fullFileNameMail = downloadUtilityService.downloadUnitConfirmToTxtUnPosted(fund, sa, account, dateFrom, dateTo, downloadTo, fileName);
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
			log.debug("error DownloadUnitConfirmation generateFileDownload " + e.getMessage());
		}
		renderJSON(hasil);
	}

	public static void downloadReport(String id) {
		log.debug("downloadReport. id: " + id);

		/*
		 * String fileName = Play.configuration.getProperty("download.tmp");
		 * String fullPath = fileName + id;
		 */
		String fileName = "";
		/***/
		// even filename is not exists here, "new File(filename).getName()" is
		// the way we get the file name only, not the full path
		File fileDest = null;
		try {

			fileName = downloadUtilityService.getDownloadUnitConfirmToTxt(id);
			String simpleFilename = new File(fileName).getName();
			String downloadFolder = Play.configuration.getProperty("download.tmp");
			fileDest = new File(downloadFolder, simpleFilename);
			try {
				clientFileService.doDownload(simpleFilename, fileDest, SimpleFileService.DOWNLOAD_UNIT_TRUST_FILE);
			} catch (Exception e) {
				log.error("Error downloading file:" + simpleFilename, e);
			}
			/**/
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
			log.error("error DownloadUnitConfirmation downloadReport " + e1.getMessage(), e1);
		}
	}
}