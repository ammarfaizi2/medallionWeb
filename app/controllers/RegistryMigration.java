package controllers;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import models.Migration;

import org.apache.log4j.Logger;

import com.simian.medallion.model.GnCalendar;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgNavId;

public class RegistryMigration extends Registry {
	private static Logger log = Logger.getLogger(RegistryMigration.class);

	private static final BigDecimal BD_100 = new BigDecimal(100);

	public static void list() {
		log.debug("list. ");

		Migration val = new Migration();

		List<String> messages = new ArrayList<String>(0);

		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(UIConstants.MENU_CODE_RG_TRANSACTION_VALIDATION));
		render("RegistryMigration/list.html", val, messages);
	}

	public static void process(Migration val) throws ParseException {
		log.debug("process. val: " + val);

		List<String> messages = new ArrayList<String>();

		// loop date
		Calendar cal = Calendar.getInstance();
		cal.setTime(val.getFromDate());

		while (!cal.getTime().after(val.getToDate())) {
			GnCalendar holiday = generalService.getCalendar(cal.getTime());
			if (holiday == null) {
				try {
					taService.transactionValidation(val.getProductCode(), cal.getTime(), true);
					taService.pRgPortfolio(val.getProductCode(), null);

					log.debug("" + cal.getTime() + ": success");
					messages.add("" + cal.getTime() + ": success");
				} catch (Throwable t) {
					messages.add("" + cal.getTime() + ": " + t.getMessage());
					break;
				}
			}
			cal.add(Calendar.DATE, 1);
		}

		log.debug("size=" + messages.size());

		// List<String> trades = taService.transactionValidation(val
		// .getRgProduct().getProductCode(), val.getGoodfundDate(),
		// val.isReProcessAll());

		render("RegistryMigration/list.html", val, messages);
	}

	public static void edit(String id) {
		log.debug("edit. id: " + id);
	}

	public static void view(String id) {
		log.debug("view. id: " + id);
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void save(RgNav nav, String mode) {
		log.debug("save. nav: " + nav + " mode: " + mode);
	}

	public static void confirming(RgNavId id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);
	}

	public static void confirm(RgNav nav, String mode) {
		log.debug("confirm. nav: " + nav + " mode: " + mode);
	}

	public static void back(RgNavId id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);
	}
}