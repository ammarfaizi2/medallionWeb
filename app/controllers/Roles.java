package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.cache.Cache;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.google.gson.JsonParseException;
import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnMenu;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnReportMapping;
import com.simian.medallion.model.GnReportPdiMapping;
import com.simian.medallion.model.GnRole;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.model.GnWorkflowMapping;
import com.simian.medallion.model.UdRoleMapping;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Roles extends MedallionController {
	private static Logger log = Logger.getLogger(Roles.class);

	public static String Menu_Number_Report_Loader = "RPL001";
	public static String Menu_Number_WF_SINGLE = "WF0000";
	public static String Menu_Number_WF_BATCH = "WF0001";
	public static String Menu_Number_REPORT_PDI = "PDI002";
	public static String Menu_Number_UPLOAD = "UT1002";
	public static String Menu_Number_DOWNLOAD = "UT1005";

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		renderArgs.put("menuNumbers", Menu_Number_Report_Loader);
		renderArgs.put("wfSingles", Menu_Number_WF_SINGLE);
		renderArgs.put("wfBatchs", Menu_Number_WF_BATCH);
		renderArgs.put("RptPdis", Menu_Number_REPORT_PDI);
		renderArgs.put("menuUpls", Menu_Number_UPLOAD);
		renderArgs.put("menuDwls", Menu_Number_DOWNLOAD);
	}

	@Check("security.role")
	public static void list() {
		log.debug("list. ");

		List<GnRole> roles = applicationService.listRoles(UIConstants.SIMIAN_BANK_ID);
		/*
		 * for (GnRole gnRole : roles) { for (UdRoleMapping udm :
		 * gnRole.getRoleMappings()) { if(udm.getProfile() != null){
		 * if(udm.getProfile().getActionType() != null){
		 * if(udm.getProfile().getActionType().trim().equals(P_UPLOAD)){
		 * gnRole.getListUploads().add(udm); } else
		 * if(udm.getProfile().getActionType().trim().equals(P_DOWNLOAD)){
		 * gnRole.getListDownloads().add(udm); } } } } }
		 */

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ROLE));
		render(roles);
	}

	@Check("security.role")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnRole role = new GnRole();
		role.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		role.setIsActive(false);
		String roles = null;
		try {
			roles = json.writeValueAsString(role);
		} catch (JsonGenerationException e) {
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ROLE));
		render("Roles/detail1.html", roles, mode);
	}

	@Check("security.role")
	@SuppressWarnings("unchecked")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnRole role = null;
		String roles = null;
		String menuNumber = "";
		String wfSingle = "";
		String wfBatch = "";
		String RptPdi = "";
		String menuUpl = "";
		String menuDwl = "";
		if (id != null) {
			role = applicationService.getRole(id);
			if (role.getMenus() != null) {
				Map<String, GnMenu> urlList = Cache.get("urlList", Map.class);
				Set<GnMenu> menus = new HashSet<GnMenu>();
				for (GnMenu menu : role.getMenus()) {
					menus.add(urlList.get(menu.getMenuLink()));
					if (menu.getMenuNumber().equals(Menu_Number_Report_Loader)) {
						menuNumber = Menu_Number_Report_Loader;
					}
					if (menu.getMenuNumber().equals(Menu_Number_WF_SINGLE)) {
						wfSingle = Menu_Number_WF_SINGLE;
					}
					if (menu.getMenuNumber().equals(Menu_Number_WF_BATCH)) {
						wfBatch = Menu_Number_WF_BATCH;
					}
					if (menu.getMenuNumber().equals(Menu_Number_REPORT_PDI)) {
						RptPdi = Menu_Number_REPORT_PDI;
					}
					if (menu.getMenuNumber().equals(Menu_Number_UPLOAD)) {
						menuUpl = Menu_Number_UPLOAD;
					}
					if (menu.getMenuNumber().equals(Menu_Number_DOWNLOAD)) {
						menuDwl = Menu_Number_DOWNLOAD;
					}
				}
				role.setMenus(menus);
				//
			}
			role.putToListMenus();
			role.putToListReportMappings();
			role.putToListUsers();
			role.putToListWorkflowMappings();
			role.putToListReportPdiMappings();
			role.putToListRoleMappings();
			role.putToListUploadDownloads();
			try {
				// roles = json.writeValueAsString(role);
				JsonHelper json = new JsonHelper().getRoleSerializer();
				roles = json.serialize(role);

			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ROLE));
		render("Roles/detail1.html", roles, mode, id, role, menuNumber, wfSingle, wfBatch, RptPdi, menuUpl, menuDwl);
	}

	@Check("security.role")
	@SuppressWarnings("unchecked")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		GnRole role = null;
		String roles = null;
		if (id != null) {
			role = applicationService.getRole(id);
			if (role.getMenus() != null) {
				Map<String, GnMenu> urlList = Cache.get("urlList", Map.class);
				Set<GnMenu> menus = new HashSet<GnMenu>();
				for (GnMenu menu : role.getMenus()) {
					menus.add(urlList.get(menu.getMenuLink()));
				}
				role.setMenus(menus);
			}
			role.putToListUploadDownloads();
			try {
				JsonHelper json = new JsonHelper().getRoleSerializer();
				roles = json.serialize(role);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ROLE));
		render("Roles/detail1.html", roles, mode, id, role);
	}

	@Check("security.role")
	public static void save(String body, String mode, Long id, String status) {
		log.debug("save. body: " + body + " mode: " + mode + " id: " + id + " status: " + status);

		try {
			GnRole role = json.readValue(body, GnRole.class);
			role.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
			if (role != null) {

				// if (role.getRoleMappings() != null) {
				// Set<GnRoleMapping> mappings = role.getRoleMappings();
				// for (GnRoleMapping mapping : mappings) {
				// if (mapping != null) {
				// role.getRoleMappings().add(mapping);
				// }
				// }
				// }

				if (role.getUsers() != null) {
					Set<GnUser> users = role.getUsers();
					for (GnUser user : users) {
						if (user != null) {
							role.getUsers().add(user);
						}
					}
				}

				if (role.getMenus() != null) {
					Set<GnMenu> menus = role.getMenus();
					for (GnMenu menu : menus) {
						if (menu != null) {
							role.getMenus().add(menu);
						}
					}
				}

				if (role.getReportMappings() != null) {
					Set<GnReportMapping> reports = role.getReportMappings();
					for (GnReportMapping report : reports) {
						if (report != null) {
							if (report.getReportLoader().getReportKey() == null) {
								report.setReportLoader(null);
							}
							role.getReportMappings().add(report);
						}
					}
				}

				if (role.getWorkflowMappings() != null) {
					Set<GnWorkflowMapping> workflowMappings = role.getWorkflowMappings();
					for (GnWorkflowMapping worklist : workflowMappings) {
						if (worklist != null) {
							if (worklist.getWorkflowDetail().getWorkflowDetailKey() == null) {
								worklist.setWorkflowDetail(null);
							}
							role.getWorkflowMappings().add(worklist);
						}
					}
				}

				if (role.getReportPdiMappings() != null) {
					Set<GnReportPdiMapping> reportPdiMappings = role.getReportPdiMappings();
					for (GnReportPdiMapping report : reportPdiMappings) {
						if (report != null) {
							if (report.getReportPdi().getReportKey() == null) {
								report.setReportPdi(null);
							}
							role.getReportPdiMappings().add(report);
						}
					}
				}

				/*
				 * if(role.getListUploads() != null){
				 * logger.debug("role.getListUploads() "
				 * +role.getListUploads().size()); List<UdRoleMapping>
				 * listUploads = role.getListUploads(); //
				 * role.getListUploads().clear(); for (UdRoleMapping udm :
				 * listUploads) { if(udm != null){
				 * logger.debug("udm.getProfile().getProfileKey() "
				 * +udm.getProfile().getProfileKey());
				 * if(udm.getProfile().getProfileKey() == null){
				 * udm.setProfile(null); } role.getListUploads().add(udm); } } }
				 * 
				 * if(role.getListDownloads() != null){ List<UdRoleMapping>
				 * listDownloads = role.getListDownloads();
				 * logger.debug("role.getListDownloads() "
				 * +role.getListDownloads().size()); //
				 * role.getListDownloads().clear(); for (UdRoleMapping udm :
				 * listDownloads) { if(udm != null){
				 * if(udm.getProfile().getProfileKey() == null){
				 * udm.setProfile(null); } role.getListDownloads().add(udm); } }
				 * }
				 */

				serializerService.serialize(session.getId(), id, role);
			}
		} catch (JsonParseException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		}
	}

	@Check("security.role")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		GnRole role = serializerService.deserialize(session.getId(), id, GnRole.class);
		String roles = null;
		try {
			roles = json.writeValueAsString(role);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ROLE));
		render("Roles/detail1.html", roles, role, mode, id, status);
	}

	@Check("security.role")
	public static void confirm(Long id, String mode, GnRole role, String status) throws MedallionException {
		log.debug("confirm. id: " + id + " mode: " + mode + " role: " + role + " status: " + status);

		role = serializerService.deserialize(session.getId(), id, GnRole.class);
		role.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		// if (role.getRoleMappings() != null) {
		// role.setRoleMappings(new
		// HashSet<GnRoleMapping>(role.getRoleMappings()));
		// }
		if (role.getUsers() != null) {
			role.setUsers(new HashSet<GnUser>(role.getUsers()));
		}
		if (role.getMenus() != null) {
			role.setMenus(new HashSet<GnMenu>(role.getMenus()));
		}
		if (role.getReportMappings() != null) {
			role.setReportMappings(new HashSet<GnReportMapping>(role.getReportMappings()));
		}
		if (role.getWorkflowMappings() != null) {
			role.setWorkflowMappings(new HashSet<GnWorkflowMapping>(role.getWorkflowMappings()));
		}
		if (role.getReportPdiMappings() != null) {
			role.setReportPdiMappings(new HashSet<GnReportPdiMapping>(role.getReportPdiMappings()));
		}
		if (role.getListUploads() != null) {
			role.setListUploads(new ArrayList<UdRoleMapping>(role.getListUploads()));
		}
		if (role.getListDownloads() != null) {
			role.setListDownloads(new ArrayList<UdRoleMapping>(role.getListDownloads()));
		}
		// role.putToSetMenus();
		// role.putToSetReportMappings();
		// role.putToSetRoleMappings();
		// role.putToSetWorkflowMappings();
		// Helper.showProperties(role);

		try {
			List<GnRole> roles = applicationService.listRoles(UIConstants.SIMIAN_BANK_ID);
			if (role.getRoleKey() == null) {
				for (GnRole roleInDb : roles) {
					if (roleInDb.getRoleName().trim().equals(role.getRoleName().trim())) {
						flash.error("Role Name : '" + role.getRoleName() + "' " + Messages.get(ExceptionConstants.DATA_DUPLICATE));
						flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ROLE));
						String error = "error";
						renderText(error);
					}
				}
			} else {
				GnRole oldRole = applicationService.getRole(role.getRoleKey());
				if (!role.getRoleName().trim().equals(oldRole.getRoleName().trim())) {
					for (GnRole roleInDb : roles) {
						if (roleInDb.getRoleName().trim().equals(role.getRoleName().trim())) {
							flash.error("Role Name : '" + role.getRoleName() + "' " + Messages.get(ExceptionConstants.DATA_DUPLICATE));
							flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ROLE));
							String error = "error";
							renderText(error);
						}
					}
				}
			}

			for (UdRoleMapping udm : role.getListUploads()) {
				udm.setRole(role);
				udm.setProfile(generalService.getUdProfile(udm.getProfile().getProfileKey()));
				role.getRoleMappings().add(udm);
			}

			for (UdRoleMapping udm : role.getListDownloads()) {
				udm.setRole(role);
				udm.setProfile(generalService.getUdProfile(udm.getProfile().getProfileKey()));
				role.getRoleMappings().add(udm);
			}

			applicationService.saveRole(MenuConstants.GN_ROLE, role, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));

		} catch (MedallionException ex) {
			String menuNumber = "";
			String wfSingle = "";
			String wfBatch = "";
			String RptPdi = "";
			String menuUpl = "";
			String menuDwl = "";
			if (role.getMenus() != null) {
				Map<String, GnMenu> urlList = Cache.get("urlList", Map.class);
				Set<GnMenu> menus = new HashSet<GnMenu>();
				for (GnMenu menu : role.getMenus()) {
					menus.add(urlList.get(menu.getMenuLink()));
					if (menu.getMenuNumber().equals(Menu_Number_Report_Loader)) {
						menuNumber = Menu_Number_Report_Loader;
					}
					if (menu.getMenuNumber().equals(Menu_Number_WF_SINGLE)) {
						wfSingle = Menu_Number_WF_SINGLE;
					}
					if (menu.getMenuNumber().equals(Menu_Number_WF_BATCH)) {
						wfBatch = Menu_Number_WF_BATCH;
					}
					if (menu.getMenuNumber().equals(Menu_Number_REPORT_PDI)) {
						RptPdi = Menu_Number_REPORT_PDI;
					}
					if (menu.getMenuNumber().equals(Menu_Number_UPLOAD)) {
						menuUpl = Menu_Number_UPLOAD;
					}
					if (menu.getMenuNumber().equals(Menu_Number_DOWNLOAD)) {
						menuDwl = Menu_Number_DOWNLOAD;
					}
				}
			}
			flash.error("Role Name : '" + role.getRoleName() + "' " + Messages.get(ex.getMessage()));
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ROLE));
			String error = "error";
			renderArgs.put("menuNumber", menuNumber);
			renderArgs.put("wfSingle", wfSingle);
			renderArgs.put("wfBatch", wfBatch);
			renderArgs.put("RptPdi", RptPdi);
			renderArgs.put("menuUpl", menuUpl);
			renderArgs.put("menuDwl", menuDwl);
			renderText(error);
		}
	}

	@Check("security.role")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		GnRole role = serializerService.deserialize(session.getId(), id, GnRole.class);
		String roles = null;
		try {
			roles = json.writeValueAsString(role);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		String menuNumber = "";
		String wfSingle = "";
		String wfBatch = "";
		String RptPdi = "";
		String menuUpl = "";
		String menuDwl = "";
		if (role.getMenus() != null) {
			Map<String, GnMenu> urlList = Cache.get("urlList", Map.class);
			Set<GnMenu> menus = new HashSet<GnMenu>();
			for (GnMenu menu : role.getMenus()) {
				menus.add(urlList.get(menu.getMenuLink()));
				if (menu.getMenuNumber().equals(Menu_Number_Report_Loader)) {
					menuNumber = Menu_Number_Report_Loader;
				}
				if (menu.getMenuNumber().equals(Menu_Number_WF_SINGLE)) {
					wfSingle = Menu_Number_WF_SINGLE;
				}
				if (menu.getMenuNumber().equals(Menu_Number_WF_BATCH)) {
					wfBatch = Menu_Number_WF_BATCH;
				}
				if (menu.getMenuNumber().equals(Menu_Number_REPORT_PDI)) {
					RptPdi = Menu_Number_REPORT_PDI;
				}
				if (menu.getMenuNumber().equals(Menu_Number_UPLOAD)) {
					menuUpl = Menu_Number_UPLOAD;
				}
				if (menu.getMenuNumber().equals(Menu_Number_DOWNLOAD)) {
					menuDwl = Menu_Number_DOWNLOAD;
				}
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ROLE));
		render("Roles/detail1.html", roles, role, mode, id, status, menuNumber, wfSingle, wfBatch, RptPdi, menuUpl, menuDwl);
	}

	@SuppressWarnings("unchecked")
	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			String roles = null;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnRole role = json.readValue(maintenanceLog.getNewData(), GnRole.class);

			if (role.getMenus() != null) {
				Map<String, GnMenu> urlList = Cache.get("urlList", Map.class);
				Set<GnMenu> menus = new HashSet<GnMenu>();
				for (GnMenu menu : role.getMenus()) {
					GnMenu men = generalService.getMenu(menu.getMenuKey());
					menus.add(urlList.get(men.getMenuLink()));
				}
				role.setMenus(menus);
			}
			try {
				// JsonHelper json = new JsonHelper().getRoleSerializer();
				// roles = json.serialize(role);
				roles = json.writeValueAsString(role);
				log.debug("role menu size >>> " + roles);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("Roles/approval.html", roles, role, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			applicationService.approveRole(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			applicationService.approveRole(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

}
