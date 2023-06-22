import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.simian.medallion.service.GeneralService;

import play.Play;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@Every("3s")
@OnApplicationStart
public class LogoutScheduler extends Job<Object> {
	private static Logger logger = Logger.getLogger(LogoutScheduler.class);
	
	@Inject
	private static GeneralService generalService;
	
	@Override
	public void doJob() throws Exception {
		doCleanIdleUser();
	}
	
	private void doCleanIdleUser() {
		try {
			String paramSessionTimeout = Play.configuration.getProperty("session.timeout");			
			Integer sessionTimeout = Integer.parseInt(paramSessionTimeout);			
			generalService.doLogoutScheduler(sessionTimeout);			
		}catch(Exception e) {
			play.Logger.debug(""+e);
		}
	}
	
}















