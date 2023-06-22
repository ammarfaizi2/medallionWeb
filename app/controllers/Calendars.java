package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.GnCalendar;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Calendars extends MedallionController {
	private static Logger log = Logger.getLogger(Calendars.class);

	@Check("administration.calendar")
	public static void list(int year) {
		log.debug("list. year: " + year);

		int month = 0;
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
		// cal.set]
		log.debug("Calendar >>> " + cal);
		Date from = cal.getTime();
		log.debug("from" + cal);
		// cal.set(java.util.Calendar.DATE,
		// cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
		cal.set(year, 11, 31, 00, 00, 00);
		Date to = cal.getTime();
		List<GnCalendar> holidays = generalService.listHolidaysBetween(from, to, "");

		for (GnCalendar holy : holidays) {
			log.debug("holy:" + holy.getId().getCalendarDate());
		}
		log.debug("holidays:" + holidays.size());
		log.debug("year -->" + year);
		log.debug("true started" + started);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CALENDAR));
		render(holidays, year, months, years, started);
	}

	@Check("administration.calendar")
	public static void next() {
		log.debug("next. ");

		render("Calendars/list.html");
	}

	// public static void save(List<java.util.Calendar> holidays, int year,
	// String mode) {
	// CoaDetailViewModel model = getViewModel(mode);
	// //List<SelectItem> operators = UIHelper.debitCreditOperators();
	// model.mode = mode;
	// model.confirming = true;
	// //renderTemplate("Coa/detail.html", model, operators);
	// render(holidays, year, mode);
	// }

	@Check("administration.calendar")
	@SuppressWarnings("unchecked")
	public static void confirm(List<Long> dates, int year) {
		log.debug("confirm. dates: " + dates + " year: " + year);

		GnOrganization org = new GnOrganization(UIConstants.SIMIAN_BANK_ID);
		generalService.saveCalendar(dates, year, org);
	}

	// public static void back(String id, String mode, COA coa) {
	// CoaDetailViewModel model = getViewModel(mode);
	// List<SelectItem> operators = UIHelper.debitCreditOperators();
	// model.mode = mode;
	// model.confirming = false;
	// renderTemplate("Coa/detail.html", coa, model, operators);
	// }
	//
	// private static CoaDetailViewModel getViewModel(String mode) {
	// CoaDetailViewModel model = new CoaDetailViewModel();
	// // model.coaParentKey=
	// // getfundService().listCoaParentKey(Constants.SIMIAN_BANK_ID);
	// // model.mode
	// model.mode = mode;
	// return model;
	// }

}
