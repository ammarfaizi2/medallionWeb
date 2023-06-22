import helpers.report.ReportFileCleaner;

import org.apache.log4j.Logger;

import play.Play;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@Every("1h")
@OnApplicationStart
public class FileCleanerBootstrap extends Job<Object> {

	private static Logger log = Logger.getLogger(FileCleanerBootstrap.class);

	@Override
	public void doJob() throws Exception {
		log.debug("FileCleanerBootstrap Job is started...");
		String[] allDirLocation = new String[] { Play.configuration.getProperty("upload.kettle.temp"), Play.configuration.getProperty("upload.kettle.param"), Play.configuration.getProperty("upload.kettleoutput"),

		Play.configuration.getProperty("upload.reportloader.temp"), Play.configuration.getProperty("upload.reportloader.param"), Play.configuration.getProperty("upload.reportloaderoutput"), Play.configuration.getProperty("download.tmp") };

		for (String dirLocation : allDirLocation) {
			ReportFileCleaner cleanerThread;
			try {
				log.debug("start clean up dir: " + dirLocation);
				cleanerThread = new ReportFileCleaner(dirLocation);
				cleanerThread.doCleanUp();
				log.debug("done clean up dir: " + dirLocation);
			} catch (Exception e) {
				log.error("error cleaning up " + dirLocation, e);
			}
		}

	}

}
