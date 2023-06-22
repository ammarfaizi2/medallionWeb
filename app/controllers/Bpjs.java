package controllers;

import helpers.UIConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnBpjs;
import com.simian.medallion.model.GnBpjsDetail;

@With(Secure.class)
public class Bpjs extends MedallionController {
	public static Logger log = Logger.getLogger(Bpjs.class);

	public static void list() {
		log.debug("list. ");

		entry();
	}

	@Check("administration.bpjs")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnBpjs bpjs = bpjsService.getBpjs();

		try {
			bpjs.setJson(new JsonHelper().getBpjsSerializer().serialize(bpjs.getBpjsDetails()));
			bpjs.setBpjsDetails(null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BPJS_MAINTENANCE));
		render("Bpjs/entry.html", bpjs, mode);
	}

	@Check("administration.bpjs")
	public static void save(GnBpjs bpjs, String mode) {
		log.debug("save. bpjs: " + bpjs + " mode: " + mode);

		log.debug("Ukurn detail >>>> " + bpjs.getBpjsDetails().size());

		List<GnBpjsDetail> bpjsDetailClean = new ArrayList<GnBpjsDetail>();
		for (GnBpjsDetail det : bpjs.getBpjsDetails()) {
			if (det != null)
				bpjsDetailClean.add(det);
		}
		bpjs.getBpjsDetails().clear();
		bpjs.getBpjsDetails().addAll(bpjsDetailClean);

		log.debug("Ukurn detail >>>> " + bpjs.getBpjsDetails().size());

		validation.required("Obligasi Price is", bpjs.getPriceObligasi().getLookupId());
		validation.required("Saham Price is", bpjs.getPriceSaham().getLookupId());

		if (validation.hasErrors()) {
			try {
				bpjs.setJson(new JsonHelper().getBpjsSerializer().serialize(bpjs.getBpjsDetails()));
				bpjs.setBpjsDetails(null);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BPJS_MAINTENANCE));
			render("Bpjs/entry.html", bpjs, mode);
		} else {
			Long id = bpjs.getBpjsKey();
			serializerService.serialize(session.getId(), bpjs.getBpjsKey(), bpjs);
			confirming(id, mode);
		}
	}

	@Check("administration.bpjs")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);

		GnBpjs bpjs = serializerService.deserialize(session.getId(), id, GnBpjs.class);
		try {
			bpjs.setJson(new JsonHelper().getBpjsSerializer().serialize(bpjs.getBpjsDetails()));
			bpjs.setBpjsDetails(null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BPJS_MAINTENANCE));
		render("Bpjs/entry.html", bpjs, mode);
	}

	@Check("administration.bpjs")
	public static void back(Long id) {
		log.debug("back. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnBpjs bpjs = serializerService.deserialize(session.getId(), id, GnBpjs.class);
		try {
			bpjs.setJson(new JsonHelper().getBpjsSerializer().serialize(bpjs.getBpjsDetails()));
			bpjs.setBpjsDetails(null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BPJS_MAINTENANCE));
		render("Bpjs/entry.html", bpjs, mode);
	}

	@Check("administration.bpjs")
	public static void confirm(Long id, String mode) {
		log.debug("confirm. id: " + id + " mode: " + mode);

		GnBpjs bpjs = null;
		try {
			bpjs = serializerService.deserialize(session.getId(), id, GnBpjs.class);
			bpjsService.saveBpjs(MenuConstants.BPJS_MAINTENANCE, bpjs, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			entry();
		} catch (MedallionException e) {
			log.error(e.getMessage(), e);

			try {
				bpjs.setJson(new JsonHelper().getBpjsSerializer().serialize(bpjs.getBpjsDetails()));
				bpjs.setBpjsDetails(null);
			} catch (Exception ee) {
				log.error(ee.getMessage(), ee);
			}

			renderArgs.put("confirming", true);

			flash.error(Messages.get(e.getMessage()));
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BPJS_MAINTENANCE));
			render("Bpjs/entry.html", bpjs, mode);
		}
	}
}