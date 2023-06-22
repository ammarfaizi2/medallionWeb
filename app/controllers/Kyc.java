package controllers;

import helpers.UIConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.Play;
import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnApplicationDate;
import com.simian.medallion.model.GnKycParam;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.vo.SelectItem;

public class Kyc extends MedallionController {
	private static Logger log = Logger.getLogger(Kyc.class);

	//private static final String COMPLIANCE_REPORT_KEY = "COMPLIANCE_REPORT_KEY";

	/*
	 * @Before(only={"process"}) private static void setup2(){
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.UT_KYC_PROCESS)); }
	 */

	@Before(only = { "edit", "confirming", "back", "save" })
	private static void setup() {
		log.debug("setup. ");

		List<SelectItem> renewalOperators = new ArrayList<SelectItem>();
		renewalOperators.add(new SelectItem("true", "Every Year On Join Date"));
		renewalOperators.add(new SelectItem("false", "Maximum Last Update"));
		renderArgs.put("renewalOperators", renewalOperators);
	}

	@Check({ "administration.kyc.process" })
	public static void process(String post, Date processDate, GnKycParam param) {
		log.debug("process. post: " + post + " processDate: " + processDate + " param: " + param);

		if (param == null)
			param = new GnKycParam();
		if (post == null) {
			param.setSessionTag(Helper.getRandomText(10));
			markSubmission(MenuConstants.UT_KYC_PROCESS + param.getSessionTag());
			param.setProcessMark(params.get(PROCESSMARK));
		}
		List<String> logs = new ArrayList<String>();
		log.debug("post:" + post);
		log.debug("processDate:" + processDate);
		Date currentBusinessDate = null;
		GnApplicationDate gnAppDate = generalService.getApplicationDate(LookupConstants.SIMIAN_ORGANIZATION_ID);
		if (gnAppDate != null) {
			currentBusinessDate = gnAppDate.getCurrentBusinessDate();
		}

		// validation run when post/process had been started
		if (post != null) {
			validation.required("Process Date", processDate);
			if (!validation.hasErrors()) {
				try {
					String sessionUuidX = session.get(PROCESSMARK + MenuConstants.UT_KYC_PROCESS + param.getSessionTag());
					if (sessionUuidX == null) {
						session.put(PROCESSMARK + MenuConstants.UT_KYC_PROCESS + param.getSessionTag(), param.getProcessMark());
						sessionUuidX = session.get(PROCESSMARK + MenuConstants.UT_KYC_PROCESS + param.getSessionTag());
					}
					if (isDoubleSubmission(MenuConstants.UT_KYC_PROCESS + param.getSessionTag())) {
						process(null, null, param);
					}
					param.setSessionTag(Helper.getRandomText(10));
					markSubmission(MenuConstants.UT_KYC_PROCESS + param.getSessionTag());
					param.setProcessMark(params.get(PROCESSMARK));
					// do it
					kycService.doComplianceCustomer(processDate);

					// now get the log
					Long totalCustomer = kycService.countComplianceCustomer(processDate);
					log.debug("found kyc process for :" + processDate + " is :" + totalCustomer);

					logs.add("Compliance process success. Customer invalid = " + totalCustomer + " rows");

					// generating report
					String simpleFileName = String.valueOf(System.currentTimeMillis() + "-ComplianceReport");
					String resultFileName = kycService.createAndConsumeQueue(simpleFileName);
					log.debug("resultFileName:" + resultFileName);
					String[] tmpSplit = resultFileName.split("\\.");
					String fileExt = "";
					if (tmpSplit != null) {
						fileExt = tmpSplit[1];
					}
					renderArgs.put("reportFile", simpleFileName + "." + fileExt);

				} catch (Exception ex) {
					log.error("Compliance process failed. Message", ex);
					logs.add("Compliance process failed. Message:" + ex.getMessage());
				}

			}
		} else {
			processDate = currentBusinessDate;
		}
		log.debug("validation.hasErrors():" + validation.hasErrors());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_KYC_PROCESS));
		if (validation.hasErrors()) {
			render("Kyc/process.html", currentBusinessDate, processDate, param);
		} else {
			render("Kyc/process.html", currentBusinessDate, logs, processDate, param);
		}
	}

	@Check({ "administration.kyc.edit" })
	public static void edit() {
		log.debug("edit. ");

		GnKycParam kycParam = generalService.getKycConfiguration(LookupConstants.SIMIAN_ORGANIZATION_ID);
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_KYC_CONFIGURATION));
		render("Kyc/configuration.html", kycParam, mode);
	}

	@Check("administration.kyc.edit")
	public static void save(GnKycParam kycParam) {
		log.debug("save. kycParam: " + kycParam);

		if (kycParam == null) {
			edit();
		}
		Long id = kycParam.getParamKey();
		if (kycParam.getAlertByEmail() == null) {
			// just assume it false;
			kycParam.setAlertByEmail(false);
		}

		if (kycParam.getRenewalAlertMethod() == null) {
			// just assume it false;
			kycParam.setRenewalAlertMethod(false);
		}

		if (kycParam.getAlertByEmail() == true) {
			validation.required("Email To", kycParam.getEmailTo());
			validation.email("Email To", kycParam.getEmailTo());
		}
		if (kycParam.getRenewalAlertMethod() == false) {
			validation.required("Maximum Last Update", kycParam.getMaximumLastUpdate());
			validation.min(kycParam.getMaximumLastUpdate(), 0);
		}
		validation.required("ID Expired", kycParam.getIdExpired());

		if (validation.hasErrors()) {
			String mode = "edit";
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_KYC_CONFIGURATION));
			render("Kyc/configuration.html", kycParam, mode);
		} else {
			serializerService.serialize(session.getId(), kycParam.getParamKey(), kycParam);
			String mode = UIConstants.DISPLAY_MODE_CONFIRM;
			confirming(id, mode);
		}
	}

	@Check("administration.kyc.edit")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		GnKycParam kycParam = serializerService.deserialize(session.getId(), id, GnKycParam.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_KYC_CONFIGURATION));
		render("Kyc/configuration.html", id, mode, kycParam);
	}

	@Check("administration.kyc.edit")
	public static void confirm(Long id, GnKycParam kycParam) {
		log.debug("confirm. id: " + id + " kycParam: " + kycParam);

		generalService.saveKycConfiguration(kycParam);
		edit();
	}

	@Check("administration.kyc.edit")
	public static void back(Long id) {
		log.debug("back. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnKycParam kycParam = serializerService.deserialize(session.getId(), id, GnKycParam.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_KYC_CONFIGURATION));
		render("Kyc/configuration.html", id, mode, kycParam);
	}

	public static void list() {
		log.debug("list. ");
	}

	public static void reportDownload(String downloadfile) {
		log.debug("reportDownload. downloadfile: " + downloadfile);

		String uploadedDirOutput = Play.configuration.getProperty("upload.reportloaderoutput");
		String fullPath = uploadedDirOutput + downloadfile;
		File fileDest = new File(fullPath);
		try {
			clientFileService.doDownload(downloadfile, fileDest, SimpleFileService.UPLOAD_REPORT_OUTPUT_FILE);
		} catch (Exception e) {
			log.error("Error downloading file:" + downloadfile, e);
			throw new MedallionException("KYC-C-001", "Error downloading file:" + downloadfile);
		}
		log.debug("fullPath:" + fullPath);
		renderBinary(fileDest, fileDest.getName());
	}

}
