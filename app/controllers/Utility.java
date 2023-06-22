package controllers;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import play.mvc.Controller;

import com.simian.medallion.service.TaService;

public class Utility extends Controller {
	private static Logger log = Logger.getLogger(Utility.class);

	@Inject
	protected static TaService taService;

	public static void upload(String batchNo, String taskName) {
		log.debug("upload. batchNo: " + batchNo + " taskName: " + taskName);

		taService.uploadTrade(batchNo, taskName);
	}

	public static void uploadCFMaster(String batchNo, String taskName, Long key) {
		log.debug("uploadCFMaster. batchNo: " + batchNo + " taskName: " + taskName + " key: " + key);

		taService.uploadCFMaster(batchNo, taskName, key);
	}
}
