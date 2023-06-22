package helpers.report;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.simian.medallion.model.helper.Helper;

public class CallableCleaner implements Callable<Integer> {
	private static Logger log = Logger.getLogger(CallableCleaner.class);

	File[] fileList;

	public CallableCleaner(File[] aFileList) {
		this.fileList = aFileList;
	}

	@Override
	public Integer call() throws Exception {
		Calendar cal = Calendar.getInstance();
		Date currDate = cal.getTime();
		currDate = resetDate(currDate);
		log.debug("currDate:" + currDate);

		for (File currFile : fileList) {
			long lastMod = currFile.lastModified();
			Date dateMod = new Date(lastMod);
			dateMod = resetDate(dateMod);
			log.debug("currFile:" + currFile.getName());
			log.debug("dateMod:" + dateMod);
			// check file last mod
			int diffDay = Helper.substractDay(currDate, dateMod);
			log.debug("diffDay:" + diffDay);
			if (diffDay > 3) {
				log.debug(currFile.getName() + " is removable, last mod:" + dateMod.toString());
				currFile.delete();
			}
		}
		return new Integer(fileList.length);
	}

	private Date resetDate(Date aDate) {
		Calendar tmp = Calendar.getInstance();
		tmp.setTime(aDate);
		tmp.set(Calendar.HOUR_OF_DAY, 0);
		tmp.set(Calendar.MINUTE, 0);
		tmp.set(Calendar.SECOND, 0);
		tmp.set(Calendar.MILLISECOND, 0);
		return tmp.getTime();
	}
}
