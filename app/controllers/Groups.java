package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.google.gson.JsonParseException;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnGroup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Groups extends MedallionController {
	private static Logger log = Logger.getLogger(Groups.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("security.group")
	public static void list() {
		log.debug("list. ");

		List<GnGroup> groups = applicationService.listGroups(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_GROUP));
		render(groups);
	}

	@Check("security.group")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnGroup grp = new GnGroup();
		grp.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		grp.setIsActive(false);
		String dataGroups = null;
		try {
			dataGroups = json.writeValueAsString(grp);
		} catch (JsonGenerationException e) {
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_GROUP));
		render("Groups/detail.html", dataGroups, grp, mode);
	}

	@Check("security.group")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnGroup grp = applicationService.getGroup(id);
		String status = grp.getRecordStatus();
		String dataGroups = null;
		try {
			JsonHelper json = new JsonHelper().getGroupSerializer();
			dataGroups = json.serialize(grp);
		} catch (JsonGenerationException e) {
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_GROUP));
		render("Groups/detail.html", dataGroups, mode, id, status);
	}

	@Check("security.group")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		GnGroup grp = applicationService.getGroup(id);
		String dataGroups = null;
		try {
			JsonHelper json = new JsonHelper().getGroupSerializer();
			dataGroups = json.serialize(grp);
		} catch (JsonGenerationException e) {
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_GROUP));
		render("Groups/detail.html", dataGroups, grp, mode, id);
	}

	@Check("security.group")
	public static void save(String body, String mode, Long id, String status) {
		log.debug("save. body: " + body + " mode: " + mode + " id: " + id + " status: " + status);

		try {
			log.debug("data = " + body);
			GnGroup grp = json.readValue(body, GnGroup.class);
			grp.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
			if (grp != null) {
				if (grp.getUsers() != null) {
					Set<GnUser> users = grp.getUsers();
					for (GnUser user : users) {
						if (user != null) {
							grp.getUsers().add(user);
						}
					}
				}

				serializerService.serialize(session.getId(), id, grp);
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

	@Check("security.group")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		GnGroup grp = serializerService.deserialize(session.getId(), id, GnGroup.class);
		String dataGroups = null;
		try {
			dataGroups = json.writeValueAsString(grp);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_GROUP));
		render("Groups/detail.html", dataGroups, grp, mode, id, status);
	}

	@Check("security.group")
	public static void confirm(Long id, String mode, GnGroup grp, String status) {
		log.debug("confirm. id: " + id + " mode: " + mode + " grp: " + grp + " status: " + status);

		grp = serializerService.deserialize(session.getId(), id, GnGroup.class);

		if (grp.getUsers() != null) {
			grp.setUsers(new HashSet<GnUser>(grp.getUsers()));
		}

		try {
			applicationService.saveGroup(grp, session.get(UIConstants.SESSION_USERNAME), "");
		} catch (MedallionException ex) {
			flash.error("Group : '" + grp.getGroupName() + "' " + Messages.get(ex.getMessage()));
			String error = "error";
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_GROUP));
			renderText(error);
		}
	}

	@Check("security.group")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		GnGroup grp = serializerService.deserialize(session.getId(), id, GnGroup.class);
		String dataGroups = null;
		try {
			dataGroups = json.writeValueAsString(grp);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_GROUP));
		render("Groups/detail.html", dataGroups, grp, mode, id, status);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			String dataGroups = null;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnGroup grp = json.readValue(maintenanceLog.getNewData(), GnGroup.class);
			try {
				dataGroups = json.writeValueAsString(grp);
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
			render("Groups/approval.html", dataGroups, grp, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			applicationService.approveGroup(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			applicationService.approveGroup(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
