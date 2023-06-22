package controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgNavId;
import com.simian.medallion.model.RgPortfolio;
import com.simian.medallion.model.RgTrade;

public class RegistryEndOfDay extends Registry {
	private static Logger log = Logger.getLogger(RegistryEndOfDay.class);

	@Check("transaction.registryEndOfDay")
	public static void list() {
		log.debug("list. ");

		RgTrade val = new RgTrade();
		val.setGoodfundDate(taService.getCurrentBusinessDate());

		List<RgPortfolio> ports = new ArrayList<RgPortfolio>(0);
		boolean readOnly = false;
		render("RegistryEndOfDay/list.html", val, ports, readOnly);
	}

	@Check("transaction.registryEndOfDay")
	public static void next(RgTrade val) throws ParseException {
		log.debug("next. val: " + val);

		taService.rollbackEndOfDay(val.getRgProduct().getProductCode(), val.getGoodfundDate());

		List<RgPortfolio> ports = taService.listEndOfDay(val.getRgProduct().getProductCode(), val.getGoodfundDate());

		boolean readOnly = ports.size() > 0 ? true : false;
		render("RegistryEndOfDay/list.html", val, ports, readOnly);
	}

	@Check("transaction.registryEndOfDay")
	public static void process(RgTrade val) throws ParseException {
		log.debug("process. val: " + val);

		taService.endOfDay(val.getRgProduct().getProductCode(), val.getGoodfundDate());

		taService.postCashDividen(val.getRgProduct().getProductCode(), val.getGoodfundDate());
		// render("RegistryEndOfDay/list.html", val, ports);
		list();
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