package controllers;

import helpers.UIConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.GnApplicationDate;
import com.simian.medallion.model.GnCalendar;
import com.simian.medallion.model.GnOrganization;

@With(Secure.class)
public class ApplicationDates extends MedallionController {
	private static Logger log = Logger.getLogger(ApplicationDates.class);

	public static void list(String organizationId) {
		log.debug("list. organizationId: " + organizationId);

		render("ApplicationDates/list.html");
	}

	@Check("administration.applicationDate")
	public static void edit(String id) {
		log.debug("edit. id: " + id);

		// APPLICATION DATE SECTION
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnApplicationDate current = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
		GnApplicationDate next = new GnApplicationDate();
		// ==============================

		// LIST HOLODAY SECTION
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		String currentDate = dateFormat.format(current.getCurrentBusinessDate());
		int year = Integer.parseInt(currentDate);

		int month = 0;
		//List<SelectItem> months = UIHelper.months;
		//List<SelectItem> years = UIHelper.years();
		java.util.Calendar calNull = java.util.Calendar.getInstance();
		//Date dateDummy = calNull.getTime();
		//boolean started;
		if (year == 0) {
			//started = false;
			year = calNull.get(java.util.Calendar.YEAR);
		} else {
			//started = true;
		}
		if (month == 0)
			month = calNull.get(java.util.Calendar.MONTH);
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(year, 0, 0, 00, 00, 00);
		// cal.set
		//Date from = cal.getTime();
		cal.set(year, 11, 31, 00, 00, 00);
		//Date to = cal.getTime();
		// List<GnCalendar> holidays = generalService.listHolidaysBetween(from,
		// to, "");
		List<GnCalendar> holidays = generalService.listHolidays(UIConstants.SIMIAN_BANK_ID);
		// ==============================
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_APPLICATION_DATE));
		render("ApplicationDates/list.html", current, next, mode, holidays);
	}

	@Check("administration.applicationDate")
	public static void save(GnApplicationDate next, String mode, int year) {
		log.debug("save. next: " + next + " mode: " + mode + " year: " + year);

		mode = UIConstants.DISPLAY_MODE_EDIT;
		GnApplicationDate current = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
		if (next != null) {
			validation.required("next.currentBussinessDate", next.getCurrentBusinessDate());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_APPLICATION_DATE));
				render("ApplicationDates/list.html", current, next, mode);
			} else {
				next.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
				String id = next.getOrganization().getOrganizationId();
				serializerService.serialize(session.getId(), id, next);
				confirming(current, id, mode);
			}
		} else {
			flash.error("argument.null", next);
		}
	}

	@Check("administration.applicationDate")
	public static void confirm(GnApplicationDate current, GnApplicationDate next, String id, String mode, int year) {
		log.debug("confirm. current: " + current + " next: " + next + " id: " + id + " mode: " + mode + " year: " + year);

		next.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		try {
			generalService.saveApplicationDate(next);
			edit(id);
		} catch (Exception e) {
			flash.error("argument.null", next);
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_APPLICATION_DATE));
			render("ApplicationDates/list.html", next, current, mode, confirming);
		}
	}

	@Check("administration.applicationDate")
	public static void confirming(GnApplicationDate current, String id, String mode) {
		log.debug("confirming. current: " + current + " id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		GnApplicationDate next = serializerService.deserialize(session.getId(), id, GnApplicationDate.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_APPLICATION_DATE));
		render("ApplicationDates/list.html", next, mode, current);
	}

	@Check("administration.applicationDate")
	public static void back(String id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		GnApplicationDate current = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
		GnApplicationDate next = serializerService.deserialize(session.getId(), id, GnApplicationDate.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_APPLICATION_DATE));
		renderTemplate("ApplicationDates/list.html", current, mode, next);
	}

	@Check("administration.applicationDate")
	public static void onChangeLastDate(Date date) {
		log.debug("onChangeLastDate. date: " + date);

		if (date != null) {
			// Date lastDate = generalService.onChangeBusinessDate(date, -1);
			Date lastDate = generalService.getPreviousWorkDate(date);
			Date nextDate = generalService.addWorkingDate(date, 1);
			Boolean right = false;
			SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
			BusinessDate bizDate = new BusinessDate(dateFormat.format(lastDate), dateFormat.format(nextDate), right);
			renderJSON(bizDate);
			// renderJSON("{\"last\":\"" + dateFormat.format(lastDate) +
			// "\",\"next\":\"" + dateFormat.format(nextDate) + "\"}");
			// renderText(dateFormat.format(lastDate));
			// {"last":"12-01-2001","next":"12-01-2011"}
		}
	}

	// public static void onChangeNextDate(Date date) {
	// Date nextDate = generalService.addWorkingDate(date, 1);
	// logger.debug(">>>> CURRENT = " + date);
	// logger.debug(">>>> NEXT = " + nextDate);
	// SimpleDateFormat dateFormat = new
	// SimpleDateFormat(UIConstants.DATE_FORMAT);
	// renderText(dateFormat.format(nextDate));
	// }

	// FOR MULTIPLE RENDER DUMMY
	public static class BusinessDate {
		public String last;
		public String next;
		public Boolean right;

		public BusinessDate(String last, String next, Boolean right) {
			this.last = last;
			this.next = next;
			this.right = right;
		}
	}

}
