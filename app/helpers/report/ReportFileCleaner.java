package helpers.report;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

public class ReportFileCleaner extends Thread {
	private static Logger log = Logger.getLogger(ReportFileCleaner.class);

	File currDir;
	private static boolean STOP = false;
	private String dirLocation = "";

	public ReportFileCleaner(String aDir) throws Exception {
		dirLocation = aDir;
		currDir = new File(dirLocation);
	}

	@Override
	public void run() {
		while (!STOP) {
			try {
				doCleanUp();
			} catch (Exception e) {
				log.error("error doing clean up", e);
				STOP = true;
			}
		}
	}

	public void doCleanUp() throws Exception {
		File[] fileList = currDir.listFiles();
		ExecutorService pool = Executors.newFixedThreadPool(2);
		CallableCleaner cleaner = new CallableCleaner(fileList);
		Future<Integer> future1 = pool.submit(cleaner);
		long startExec = System.currentTimeMillis();
		Integer totaFile = future1.get();
		long endExec = System.currentTimeMillis();
		log.debug("totaFile:" + totaFile + " with " + (endExec - startExec) + " ms spent.");
		pool.shutdown();
		log.debug("Sleep for 1000 ms.");
		Thread.sleep(1000);
		log.debug("waking up, next cleaner will be running....");
	}

	public static void main(String[] args) {
		ReportFileCleaner cleanerThread;
		try {
			cleanerThread = new ReportFileCleaner("D:/theworkspace/workspacemedallion/medallionWeb/data/attachments/kettle/temp");
			cleanerThread.run();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}