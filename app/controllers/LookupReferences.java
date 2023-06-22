package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnLookupReference;
import com.simian.medallion.model.GnLookupReferenceItem;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class LookupReferences extends MedallionController {
	private static Logger log = Logger.getLogger(LookupReferences.class);

	@Before(unless = { "list", "save", "confirm" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.lookupReference")
	public static void group(String organizationId) {
		log.debug("group. organizationId: " + organizationId);

		// List<GnLookupReference> lookupReferences =
		// generalService.listLookupReferences(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_REFERENCE));
		// render(lookupReferences);
		render("LookupReferences/group.html");
	}

	@Check("administration.lookupReference")
	public static void paging(Paging page) {
		log.debug("paging. page: " + page);

		page.addParams(Helper.searchAll("(a.lookupGroupSource.lookupGroup||a.lookupGroupTarget.lookupGroup)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = generalService.pagingLookupReferences(page, UIConstants.SIMIAN_BANK_ID);
		log.debug("json ---> " + page);
		renderJSON(page);
	}

	@Check("administration.lookupReference")
	public static void list(Long group) {
		log.debug("list. group: " + group);

		List<GnLookupReferenceItem> lookupReferenceItems = generalService.listLookupReferenceItems(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_REFERENCE));
		render(lookupReferenceItems, group);
	}

	public static void view(Long id, Long group) {
		log.debug("view. id: " + id + " group: " + group);
	}

	@Check("administration.lookupReference")
	public static void entry(Long group) {
		log.debug("entry. group: " + group);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnLookupReferenceItem lookupReferenceItem = new GnLookupReferenceItem();
		GnLookupReference lookupReference = generalService.getLookupReference(group);
		lookupReferenceItem.setLookupReference(lookupReference);
		lookupReferenceItem.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_REFERENCE));
		render("LookupReferences/detail.html", lookupReferenceItem, mode, group);
	}

	@Check("administration.lookupReference")
	public static void edit(Long id, Long group) {
		log.debug("edit. id: " + id + " group: " + group);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnLookupReferenceItem lookupReferenceItem = generalService.getLookupReferenceItem(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_REFERENCE));
		render("LookupReferences/detail.html", lookupReferenceItem, group, mode);
	}

	@Check("administration.lookupReference")
	public static void save(Long group, String mode, GnLookupReferenceItem lookupReferenceItem) {
		log.debug("save. group: " + group + " mode: " + mode + " lookupReferenceItem: " + lookupReferenceItem);

		// Validate here
		if (lookupReferenceItem != null) {
			validation.required("Source is", lookupReferenceItem.getLookupSource().getLookupId());
			validation.required("Target is", lookupReferenceItem.getLookupTarget().getLookupId());
			if (validation.hasErrors()) {
				Boolean error = true;
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_REFERENCE));
				render("LookupReferences/detail.html", lookupReferenceItem, group, mode, error);
			} else {
				Long id = lookupReferenceItem.getLookupReferenceItemKey();
				serializerService.serialize(session.getId(), id, lookupReferenceItem);
				confirming(id, group, mode);
			}
		} else {
			flash.error("argument.null", lookupReferenceItem);
		}
	}

	@Check("administration.lookupReference")
	public static void confirming(Long id, Long group, String mode) {
		log.debug("confirming. id: " + id + " group: " + group + " mode: " + mode);

		renderArgs.put("confirming", true);
		GnLookupReferenceItem lookupReferenceItem = serializerService.deserialize(session.getId(), id, GnLookupReferenceItem.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_REFERENCE));
		render("LookupReferences/detail.html", lookupReferenceItem, group, mode);
	}

	@Check("administration.lookupReference")
	public static void confirm(Long group, String mode, GnLookupReferenceItem lookupReferenceItem) {
		log.debug("confirm. group: " + group + " mode: " + mode + " lookupReferenceItem: " + lookupReferenceItem);

		try {
			generalService.saveLookupReferenceItem(lookupReferenceItem);
			list(group);
		} catch (MedallionException e) {
			flash.error("Already Exist");
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_REFERENCE));
			render("LookupReferences/detail.html", lookupReferenceItem, group, mode, confirming);
		}
	}

	@Check("administration.lookupReference")
	public static void back(Long id, Long group, String mode) {
		log.debug("back. id: " + id + " group: " + group + " mode: " + mode);

		GnLookupReferenceItem lookupReferenceItem = serializerService.deserialize(session.getId(), id, GnLookupReferenceItem.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_REFERENCE));
		render("LookupReferences/detail.html", lookupReferenceItem, group, mode);
	}

	@Check("administration.lookupReference")
	public static void delete(Long id) {
		log.debug("delete. id: " + id);

		GnLookupReferenceItem lookupReferenceItem = generalService.deleteLookupReferenceItem(id);
		String status = "success";
		if (lookupReferenceItem != null) {
			status = "error";
		}
		renderText(status);
	}
}
