package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.FaPublishSchedule;
import com.simian.medallion.vo.SelectItem;

@Check("administration.calendar")
public class CalendarsPublishNav extends MedallionController {
	private static Logger log = Logger.getLogger(CalendarsPublishNav.class);

	@Check("administration.calendarPublishNav")
	public static void list(int year, long key) {
		log.debug("list. year: " + year + " key: " + key);

		int month = 0;
		log.debug("fundKey ? " + key);
		log.debug("year input" + year);
		List<SelectItem> months = UIHelper.months;
		List<SelectItem> years = UIHelper.years();
		java.util.Calendar calNull = java.util.Calendar.getInstance();
		Date dateDummy = calNull.getTime();
		log.debug("current Date ?-->" + dateDummy.toString());
		boolean started;
		if (year == 0) {
			started = false;
			year = calNull.get(java.util.Calendar.YEAR);
		} else {
			started = true;
		}
		// year = dateDummy.getYear();
		if (month == 0)
			month = calNull.get(java.util.Calendar.MONTH);
		java.util.Calendar cal = java.util.Calendar.getInstance();
		// cal.set(year, month, 1);
		cal.set(year, 0, 0, 00, 00, 00);
		// cal.set
		Date from = cal.getTime();
		log.debug("from" + cal);
		// cal.set(java.util.Calendar.DATE,
		// cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
		cal.set(year, 11, 31, 00, 00, 00);
		Date to = cal.getTime();
		log.debug("to" + cal);
		List<FaPublishSchedule> publishDates = fundService.listPublishDatesBetween(key, from, to, "");

		log.debug("publishDates :" + publishDates.size());
		log.debug("year --> " + year);
		log.debug("true started " + started);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_CALENDAR_PUBLISH_NAV));
		render(publishDates, year, months, years, started, key);
	}

	@Check("administration.calendarPublishNav")
	public static void next() {
		log.debug("next. ");

		render("CalendarsPublishNav/list.html");
	}

	@Check("administration.calendarPublishNav")
	@SuppressWarnings("unchecked")
	public static void confirm(List<Long> dates, int year, Long key) {
		log.debug("confirm. dates: " + dates + " year: " + year + " key: " + key);

		fundService.saveCalendarPublishNav(dates, year, key);
	}

}
