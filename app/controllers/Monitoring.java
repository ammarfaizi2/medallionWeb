package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import play.Play;
import play.mvc.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.simian.medallion.helper.ClientFileService;
import com.simian.medallion.service.MonitoringService;
import com.simian.medallion.vo.LogFiles;

public class Monitoring extends Controller {
	private static Logger log = Logger.getLogger(Monitoring.class);

	@Inject
	protected static MonitoringService monitoringService;

	@Inject
	protected static ClientFileService clientFileService;

	public static void home() {
		log.debug("home. ");

		render("Monitoring/home.html");
	}

	public static void autentifikasi(String key) {
		log.debug("autentifikasi. key: " + key);

		if (key != null) {
			log.info("key=" + key);
			String magicWord = MonitoringService.DEFAULT_SIMIAN_MAGIC_WORDS;
			if (key.equals(magicWord)) {
				session.put("magicWord", magicWord);
			}

			List<LogFiles> serverLogList = monitoringService.getPathServerPath(MonitoringService.DOWNLOAD_LOGS_FILE_SERVER);
			File[] webLogList = logsServerWeb();
			List<LogFiles> schedulerLogList = monitoringService.getPathServerPath(MonitoringService.DOWNLOAD_LOGS_FILE_SCHEDULER);

			render("Monitoring/detail.html", serverLogList, webLogList, schedulerLogList);
		} else {
			render("Monitoring/home.html");
		}
	}

	public static File[] logsServerWeb() {
		log.debug("logsServerWeb. ");

		String folderLocation;
		File folder = null;
		File[] listOfFiles = null;

		folderLocation = monitoringService.getPathServerLog(MonitoringService.DOWNLOAD_LOGS_FILE_WEB);
		folder = new File(folderLocation);

		listOfFiles = folder.listFiles();

		return listOfFiles;
	}

	public static void downloadGeneratedFileServer(String downloadfile, String type) {
		log.debug("downloadGeneratedFileServer. downloadfile: " + downloadfile + " type: " + type);

		String downloadFolder = Play.configuration.getProperty("download.tmp");
		File fileDest = new File(downloadFolder, new File(downloadfile).getName());
		try {
			if (type.equals("server"))
				clientFileService.doDownload(downloadfile, fileDest, MonitoringService.DOWNLOAD_LOGS_FILE_SERVER);
			else
				clientFileService.doDownload(downloadfile, fileDest, MonitoringService.DOWNLOAD_LOGS_FILE_SCHEDULER);
		} catch (Exception e) {
			log.error("Error downloading file:" + downloadfile, e);
		}

		renderBinary(fileDest, fileDest.getName());
	}

	public static void downloadGeneratedFileWeb(String downloadfile) {
		log.debug("downloadGeneratedFileWeb. downloadfile: " + downloadfile);

		String downloadFolder = monitoringService.getPathServerLog(MonitoringService.DOWNLOAD_LOGS_FILE_WEB);
		File fileDest = new File(downloadFolder, new File(downloadfile).getName());

		renderBinary(fileDest, fileDest.getName());
	}

	public static void goQuery(String query, String limit) {
		log.debug("goQuery. query: " + query + " limit: " + limit);

		Map<String, Object> map = new HashMap<String, Object>();
		List<String> columNames = new ArrayList<String>();
		Integer rowLimit = Integer.parseInt(limit);

		if (session.get("magicWord").equals(MonitoringService.DEFAULT_SIMIAN_MAGIC_WORDS)) {
			log.debug("query =" + query);
			log.debug("rowLimit =" + rowLimit);

			List<Map<String, Object>> queryResults = monitoringService.getDataFromQuery(query, rowLimit);
			Map<String, Object> singleRowMap = queryResults.get(0);
			for (Map.Entry<String, Object> entry : singleRowMap.entrySet()) {
				columNames.add(entry.getKey());
			}

			map.put("columns", columNames);
			map.put("data", queryResults);
			Gson gson = new GsonBuilder().serializeNulls().create();
			renderJSON(gson.toJson(map));
		} else {
			Gson gson = new GsonBuilder().serializeNulls().create();
			renderJSON(gson.toJson(map));
		}
	}

	public static void logoutMonitor() {
		log.debug("logoutMonitor. ");

		session.remove("magicWord");
		render("Monitoring/home.html");
	}
}
