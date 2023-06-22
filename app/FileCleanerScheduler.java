import javax.inject.Inject;

import org.apache.log4j.Logger;

import play.Play;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import com.simian.medallion.model.ReturnParam;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.GeneralService;
import com.simian.medallion.service.SerializerService;

//@Every("1s")
@Every("1h")
@OnApplicationStart
public class FileCleanerScheduler extends Job<Object> {
	private static Logger log = Logger.getLogger(FileCleanerScheduler.class);

	@Inject
	private static GeneralService generalService;
	@Inject
	private static SerializerService serializerService;


	private boolean runCleanerWeb = false;
	private boolean runCleanerServer = false;
	
	@Override
	public void doJob() throws Exception {
		log.debug("Running FileCleanerScheduler...");
		
		if(!runCleanerWeb){
			runCleanerWeb = true;

			try{	
				fileCleanWeb();
			}catch(Exception e){
				runCleanerWeb = false;
				e.printStackTrace();
			}finally{
				runCleanerWeb = false;
			}
		}
		if(!runCleanerServer){
			runCleanerServer = true;
			try{	
				fileCleanServer();
			}catch(Exception e){
				runCleanerServer = false;
				e.printStackTrace();
			}finally{
				runCleanerServer = false;
			}
		}
		try{
			cleanGnSessionData();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void fileCleanWeb() {
		log.debug("Running File Cleaning Web");

		ReturnParam param = new ReturnParam();
		
		ReturnParam a = Helper.fileCleaner(Play.configuration.getProperty("upload.customer"), true, true, 3);
		param.setDeletedFiles(param.getDeletedFiles() + a.getDeletedFiles());
		param.setDeletedFolders(param.getDeletedFolders() + a.getDeletedFolders());
		
		ReturnParam b = Helper.fileCleaner(Play.configuration.getProperty("upload.shareholder"), false, true, 3);
		param.setDeletedFiles(param.getDeletedFiles() + b.getDeletedFiles());
		param.setDeletedFolders(param.getDeletedFolders() + b.getDeletedFolders());
		
		ReturnParam c = Helper.fileCleaner(Play.configuration.getProperty("upload.announcement"), false, true, 3);
		param.setDeletedFiles(param.getDeletedFiles() + c.getDeletedFiles());
		param.setDeletedFolders(param.getDeletedFolders() + c.getDeletedFolders());
		
		ReturnParam d = Helper.fileCleaner(Play.configuration.getProperty("upload.certificate"), true, true, 3);
		param.setDeletedFiles(param.getDeletedFiles() + d.getDeletedFiles());
		param.setDeletedFolders(param.getDeletedFolders() + d.getDeletedFolders());

		print("Web", param);
	}
	
	private void fileCleanServer() {
		log.debug("Running File Cleaning Server");
		ReturnParam param = generalService.fileCleaner();
		print("Server", param);	
	}

	private void cleanGnSessionData() {
		log.debug("Running Clean Session Data....");
		ReturnParam param = serializerService.cleanSessionData();
	}

	private void print(String tag, ReturnParam param) {
		log.debug("Sucess deleting on " + tag + " " + param.getDeletedFiles() + " files and " + param.getDeletedFolders() + " folders");
	}
}