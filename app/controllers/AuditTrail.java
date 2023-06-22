package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.Before;
import play.mvc.With;
import vo.AuditTrailSearchParameters;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.GnAuditTrail;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class AuditTrail extends MedallionController {
	private static Logger log = Logger.getLogger(AuditTrail.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.bilyetNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.auditTrail")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		List<GnAuditTrail> auditTrails = generalService.listTrail();
		List<SelectItem> operators = UIHelper.bilyetNoOperators();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_AUDIT_TRAIL));
		render(auditTrails, mode, operators);
	}

	@Check("administration.auditTrail")
	public static void view(Long id) {
		log.debug("view. id: " + id);

	}

	@Check("administration.auditTrail")
	public static void search(AuditTrailSearchParameters params) throws IOException {
		log.debug("search. params: " + params);

		List<GnAuditTrail> auditTrails = generalService.searchAuditTrail(params.individualUserKey, new Date(params.dateFrom.getTime()), new Date(params.dateTo.getTime()));

		

		// for (GnAuditTrail gnAuditTrail : auditTrails) {

		// items =
		// gnAuditTrail.getUser().splitPasswordHistories(gnAuditTrail.getUser().getPasswordHistory());
		// for (String itemString : items) {
		// gnAuditTrail.getUser().setPassword(itemString);
		// }
		// }
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.GN_AUDIT_TRAIL));
		render("AuditTrail/grid.html", auditTrails);
	}

	@Check("administration.auditTrail")
	public static void auditPaging(Paging page, AuditTrailSearchParameters param) {
		log.debug("auditPaging. page: " + page + " param: " + param);

		page.addParams("gat.loginDate", page.GREATEQUAL, param.dateFrom);
		page.addParams("gat.loginDate", page.LESSEQUAL, param.dateTo);

		page.addParams(Helper.searchAll("(u.userId||u.userName||to_char(gat.loginDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "to_char(gat.loginTime,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "gat.loginStatus||gat.message)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		if (!"".equals(param.individualUserKey) && param.individualUserKey != null) {
			page.addParams("u.userKey", page.EQUAL, param.individualUserKey);
		}

		page = generalService.pagingAuditTrail(page);
		renderJSON(page);
	}

	@Check("administration.auditTrail")
	public static void entry() {
		log.debug("entry. ");
	}

	@Check("administration.auditTrail")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

	}

	@Check("administration.auditTrail")
	private static void setData() {
		log.debug("setData. ");
	}

	@Check("administration.auditTrail")
	public static void save() {
		log.debug("save. ");
	}

	@Check("administration.auditTrail")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);
	}

	@Check("administration.auditTrail")
	public static void confirm() {
		log.debug("confirm. ");
	}

	@Check("administration.auditTrail")
	public static void back() {
		log.debug("back. ");
	}

	@Check("administration.auditTrail")
	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);
	}

	@Check("administration.auditTrail")
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);
	}

	@Check("administration.auditTrail")
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);
	}
}