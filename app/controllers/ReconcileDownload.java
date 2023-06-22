package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.GsonBuilder;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.ReconParameters;
import com.simian.medallion.model.UpdProfile;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.vo.SelectItem;

import helpers.UIConstants;
import play.Play;
import play.mvc.Before;
import play.mvc.With;

@With(Secure.class)
public class ReconcileDownload extends MedallionController {
	private static Logger log = Logger.getLogger(ReconcileDownload.class);

	public static final String COLUMN = "COLUMN";
	public static final String BATCH_ID = "BATCH_ID";

	private static String VIEW_DIR = "ReconcileDownload";
	public static final String UPLOAD_FILE = "UPLOAD_FILE";
	public static final String UPLOAD_FILTER = "UPLOAD_FILTER";

	public static String runProcess;

	@Before(only = {"list"})
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("VIEW_DIR", VIEW_DIR);

		List<SelectItem> recondOpt = new ArrayList<SelectItem>();
		recondOpt.add(new SelectItem("1", "Holding W/ Depository KSEI"));
		recondOpt.add(new SelectItem("2", "Holding W/ Depository BI"));
		recondOpt.add(new SelectItem("4", "Entitlement CA W/ MER KSEI"));
		renderArgs.put("recondOpt", recondOpt);
		
		
		GnSystemParameter ksei = generalService.getSystemParameter(SystemParamConstants.PARAM_RECON_KSEI);
		UpdProfile kseiProf = generalService.getUdProfile(Long.valueOf(ksei.getValue()));
		renderArgs.put("kseiFile", kseiProf.getFileType());
		
		GnSystemParameter bi = generalService.getSystemParameter(SystemParamConstants.PARAM_RECON_BI);
		UpdProfile biProf = generalService.getUdProfile(Long.valueOf(bi.getValue()));
		renderArgs.put("biFile", biProf.getFileType());
		
		GnSystemParameter ent = generalService.getSystemParameter(SystemParamConstants.PARAM_RECON_ENTITLEMENT);
        UpdProfile entProf = generalService.getUdProfile(Long.valueOf(ent.getValue()));
        renderArgs.put("entFile", entProf.getFileType());
		
	}

	public static void index() {
		log.debug("index. ");

		list();
	}

	@Check("utility.reconciledownload")
	public static void list() {
		log.debug("list. ");

		ReconParameters param = new ReconParameters();
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UT_RECONCILE_DOWNLOAD));
		render("ReconcileDownload/list.html",param);
	}
	
	private static final Map<String, ReconParameters> uploadMapCache = new HashMap<String, ReconParameters>();
	private static final Map<String, Map> uploadMapCacheResult = new HashMap<String, Map>();

	public static void preReconDownload(ReconParameters param) {
		log.debug("reconDownload ");
		try {
			String newFileName = clientFileService.doUpload(param.getFile(), SimpleFileService.UPLOAD_TMP_FILE);
			String newAbsolutePath = clientFileService.getAbsolutePath(newFileName, SimpleFileService.UPLOAD_TMP_FILE);
			param.setNewAbsolutePath(newAbsolutePath);
			
			uploadMapCache.put(param.getProcessId(), param);

			Map<String, String> preturn = new HashMap<String, String>();
			preturn.put("RESULT", "SUCCESS");
			String resultJson = (new GsonBuilder()).create().toJson(preturn);
			renderJSON(resultJson);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			Map<String, String> preturn = new HashMap<String, String>();
			preturn.put("RESULT", "FAIL");
			String resultJson = (new GsonBuilder()).create().toJson(preturn);
			renderJSON(resultJson);
		}
	}

	public static void processRecon(String processId) {
		log.debug("processRecon "+processId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			ReconParameters param = uploadMapCache.get(processId);
			String username = session.get(UIConstants.SESSION_USERNAME);
			String newAbsolutePath = param.getNewAbsolutePath();
			
			map = uploadUtilityService.processRecon(newAbsolutePath, username, param, processId);
			renderJSON(map);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			map.put("ERROR", e.getMessage());
			uploadMapCacheResult.put(processId, map);
			// renderJSON(map);
			// String resultJson = (new GsonBuilder()).create().toJson(map);
			// render(resultJson);
			uploadMapCache.remove(processId);
			renderJSON(map);
		}
	}
	
	public static void downloadGeneratedFile(String downloadfile) {
		log.debug("downloadGeneratedFile. downloadfile: " + downloadfile);

		String downloadFolder = Play.configuration.getProperty("download.tmp");
		File fileDest = new File(downloadFolder, new File(downloadfile).getName());
		try {
			clientFileService.doDownload(downloadfile, fileDest, SimpleFileService.DOWNLOAD_GENERAL_FILE);
		} catch (Exception e) {
			log.error("Error downloading file:" + downloadfile, e);
		}

		renderBinary(fileDest, fileDest.getName());
	}
}



















