package controllers;

import helpers.Cryptography;
import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.mail.MailParseException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.PublishMFParam;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnEmployee;
import com.simian.medallion.model.GnGroup;
import com.simian.medallion.model.GnLimit;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnPasswordParam;
import com.simian.medallion.model.GnPasswordRestricted;
import com.simian.medallion.model.GnRole;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.plugin.vo.MailVO;
import com.simian.medallion.service.ReportQueueService;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Users extends MedallionController {
	private static Logger log = Logger.getLogger(Users.class);

	@Before(only = { "entry", "edit", "view", "save", "confirming", "confirm", "back", "approval", "sessionlist" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		String loginType = generalService.getValueParam(SystemParamConstants.PARAM_SECURITY_LOGIN_APP_TYPE);
		renderArgs.put("loginLDAP",  !"SYSTEM".equals(loginType) ? true : false);
		
		renderArgs.put("amountTypes", generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._AMOUNT_TYPE));
	}

	@Check("security.user")
	public static void list() {
		log.debug("list. ");

		List<GnUser> users = applicationService.listUsers(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_USER));
		render(users);
	}

	@Check("security.user")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnUser user = new GnUser();
		user.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		user.setIsInternal(true);
		user.setIsActive(false);
		user.setPasswordNeverExpired(true);

		// List<GnRoleMapping> roleMaps =
		// applicationService.listRoleByGroupUsers(session.get("groupName"));
		List<GnRole> roles = applicationService.listRoleActiveByUser(session.get(UIConstants.SESSION_USERNAME));
		for (GnRole role : roles) {
			if (role.getRoleKey() == 1) {
				user.setRoleKey(1);
			}
		}

		StringBuffer bufferPassRestristed = new StringBuffer();
		String listPassRestricted = new String();
		// StringBuffer bufferPassCycles = new StringBuffer();
		// String listPassCycles = new String();
		GnPasswordParam passParam = applicationService.getPasswordParam();
		List<GnPasswordRestricted> listPasswordRestricteds = applicationService.listPasswordRestricteds();
		for (GnPasswordRestricted gnPasswordRestricted : listPasswordRestricteds) {
			bufferPassRestristed.append(gnPasswordRestricted.getPassword()).append("|");
		}
		if (bufferPassRestristed.length() > 1) {
			listPassRestricted = bufferPassRestristed.substring(0, bufferPassRestristed.length() - 1);
		}

		// List<String> listPasswordCycles =
		// user.splitPasswordHistories(oldUser.getPasswordHistory());
		// if (listPasswordCycles.size() > passParam.getMaxReuse()){
		// int length = listPasswordCycles.size() - passParam.getMaxReuse();
		// for (int i = 0; i < length; i++) {
		// listPasswordCycles.remove(0);
		// }
		// }
		// for (String item : listPasswordCycles) {
		// bufferPassCycles.append(item).append("|");
		// }
		// if (bufferPassCycles.length() > 1) {
		// listPassCycles = bufferPassCycles.substring(0,
		// bufferPassCycles.length()-1).toString();
		// }
		
		try{
 			user.setJson((new JsonHelper()).serialize(user));
		}catch(Exception e) { e.printStackTrace(); }
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_USER));
		render("Users/detail.html", user, mode, passParam, listPassRestricted);
	}

	@Check("security.user")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnUser user = applicationService.getUser(id);
		// List<GnRoleMapping> roleMaps =
		// applicationService.listRoleByGroupUsers(session.get("groupName"));
		List<GnRole> roles = applicationService.listRoleActiveByUser(session.get(UIConstants.SESSION_USERNAME));
		for (GnRole role : roles) {
			if (role.getRoleKey() == 1) {
				user.setRoleKey(1);
			}
		}
		String status = user.getRecordStatus();
		user.setPassword(null);
		
    	try{
    		for (GnLimit limit : user.getLimits()) {
    			GnUser nUser = new GnUser();
    			nUser.setUserKey(user.getUserKey());
    			limit.setUser(nUser);
    			
    			GnLookup type = new GnLookup();
    			type.setLookupId(limit.getType().getLookupId());
    			type.setLookupCode(limit.getType().getLookupCode());
    			type.setLookupDescription(limit.getType().getLookupDescription());
    			limit.setType(type);
    		}
    		
    		GnUser cpUser = new GnUser();
    		cpUser.setLimits(user.getLimits());
    		
			user.setJson((new JsonHelper()).serialize(cpUser));
		}catch(Exception e) { e.printStackTrace(); }
    	
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_USER));
		render("Users/detail.html", user, mode, status);
	}

	@Check("security.user")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		GnUser user = applicationService.getUser(id);
		// List<GnRoleMapping> roleMaps =
		// applicationService.listRoleByGroupUsers(session.get("groupName"));
		List<GnRole> roles = applicationService.listRoleActiveByUser(session.get(UIConstants.SESSION_USERNAME));
		for (GnRole role : roles) {
			/*** SUPERUSER ****/
			if (role.getRoleKey() == 1) {
				user.setRoleKey(1);
			}
			/*** SUPERVISOR ****/
			if (role.getRoleKey() == 1050) {
				user.setRoleKey(1050);
			}
		}
		
    	try{
    		for (GnLimit limit : user.getLimits()) {
    			GnUser nUser = new GnUser();
    			nUser.setUserKey(user.getUserKey());
    			limit.setUser(nUser);
    			
    			GnLookup type = new GnLookup();
    			type.setLookupId(limit.getType().getLookupId());
    			type.setLookupCode(limit.getType().getLookupCode());
    			type.setLookupDescription(limit.getType().getLookupDescription());
    			limit.setType(type);
    		}
    		
    		GnUser cpUser = new GnUser();
    		cpUser.setLimits(user.getLimits());
			user.setJson((new JsonHelper()).serialize(cpUser));
		}catch(Exception e) { e.printStackTrace(); }
    	
	


		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_USER));
		render("Users/detail.html", user, mode);
	}

	@Check("security.user")
	public static void save(GnUser user, List<GnGroup> groups, String mode, String confirmPassword, String status) {
		log.debug("save. user: " + user + " groups: " + groups + " mode: " + mode + " confirmPassword: " + confirmPassword + " status: " + status);
		//cleanup GnLimit
		List<GnLimit> newLimits = new ArrayList<GnLimit>();
		for (GnLimit limit : user.getLimits()) {
			if (limit != null) { newLimits.add(limit); }
		}
		user.setLimits(newLimits);
		user.setConfirmPassword(confirmPassword);
		if (user != null) {
			if (groups != null) {
				for (GnGroup group : groups) {
					if (group != null) {
						user.getGroups().add(group);
					}
				}
			}
			validation.required("User ID is", user.getUserId());
			validation.required("Email is", user.getEmail());
			if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
				if (!user.isLoginLDAP()) {
					validation.required("New Password is", user.getPassword());
					validation.required("Confirm Password is", confirmPassword);
				}
			}
			validation.required("Name is", user.getUserName());
			validation.required("Employee No is", user.getEmployee().getEmployeeNo());
			validation.required("Branch No is", user.getEmployee().getBranch().getBranchNo());
    		
    		if (user.getLimits() != null) {
    			Map<String, String> codeMap = new HashMap<String, String>();
    			for (int i = 0; i < user.getLimits().size(); i++) {
    				GnLimit limit = user.getLimits().get(i);
    				
    				if (codeMap.get(limit.getCurrency().getCurrencyCode()) != null) {
    					validation.addError("", "Duplicate currency code for "+limit.getCurrency().getCurrencyCode(), "");
    				}
    				    				
    				if (limit.getCurrency().getCurrencyCode() == null || 
    					"".equals(limit.getCurrency().getCurrencyCode())) { validation.required("Currency ["+i+"] is ", limit.getCurrency().getCurrencyCode()); }
    				if (limit.getAmount() == null) { validation.required("Amount ["+i+"] is ", limit.getAmount()); }
    				if (limit.getType().getLookupId() == null || "".equals(limit.getType().getLookupId())) { validation.required("Amount Type ["+i+"] is ", limit.getType().getLookupId()); }
    				
    				codeMap.put(limit.getCurrency().getCurrencyCode(), limit.getCurrency().getCurrencyCode());
    			}
    		}

			if (validation.hasErrors()) {
				StringBuffer bufferPassRestristed = new StringBuffer();
				String listPassRestricted = new String();
				GnPasswordParam passParam = applicationService.getPasswordParam();
				List<GnPasswordRestricted> listPasswordRestricteds = applicationService.listPasswordRestricteds();
				for (GnPasswordRestricted gnPasswordRestricted : listPasswordRestricteds) {
					bufferPassRestristed.append(gnPasswordRestricted.getPassword()).append("|");
				}
				if (bufferPassRestristed.length() > 1) {
					listPassRestricted = bufferPassRestristed.substring(0, bufferPassRestristed.length() - 1);
				}
				
				try{
    				user.setJson((new JsonHelper()).serialize(user));
    			}catch(Exception e) { e.printStackTrace(); }
				
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_USER));
				render("Users/detail.html", user, mode, status, passParam, listPassRestricted);
			} else {
				log.debug("employee NO = " + user.getEmployee().getEmployeeNo());
				log.debug("employee Key = " + user.getEmployee().getEmployeeKey());
				Long key = user.getUserKey();
				serializerService.serialize(session.getId(), key, user);
				confirming(key, mode, status);
			}
		} else {
			// flash.error(ExceptionConstants.PARAMETER_NULL, user);
			entry();
		}
	}

	@Check("security.user")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		GnUser user = serializerService.deserialize(session.getId(), id, GnUser.class);
		String confirmPassword = user.getPassword();
    	
    	try{
			user.setJson((new JsonHelper()).serialize(user));
		}catch(Exception e) { e.printStackTrace(); }
    	
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_USER));
		render("Users/detail.html", user, mode, confirmPassword, status);
	}

	@Check("security.user")
	public static void confirm(GnUser user, List<GnGroup> groups, GnEmployee employee, String mode, String status) {
		log.debug("confirm. user: " + user + " groups: " + groups + " employee: " + employee + " mode: " + mode + " status: " + status);
		Boolean confirmSuccess = new Boolean(false);
		
		if (user == null)
			back(null, mode, status);
		if (!Helper.isNullOrEmpty(user.getPassword())) {
			user.setPassword(Hex.encodeHexString(Cryptography.hash(decrypt(user.getPassword()))));
		}
		if (user != null && groups != null) {
			user.setGroups(new HashSet<GnGroup>(groups));
		}
		List<String> passHistoryArray = new ArrayList<String>();
		passHistoryArray.add(user.getHistory());
		user.setHistoryPassword(passHistoryArray);
		user.setPasswordHistory(user.joinPasswordHistory());
		user.setPasswordMustChange(false);
		user.setJson(null);
		try {
			applicationService.saveUser(MenuConstants.GN_USER, user, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			// applicationService.saveUser(user);
			list();
		} catch (MedallionException ex) {
    		confirmSuccess = new Boolean(false);
			flash.error("User Id : ' " + user.getUserId() + " ' " + Messages.get(ex.getMessage()));
			boolean confirming = true;
    		
    		try{
				user.setJson((new JsonHelper()).serialize(user));
			}catch(Exception e) { e.printStackTrace(); }
    		

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_USER));
			render("Users/detail.html", user, mode, confirming, status, confirmSuccess);
		}
	}

	@Check("security.user")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		StringBuffer bufferPassRestristed = new StringBuffer();
		String listPassRestricted = new String();
		// StringBuffer bufferPassCycles = new StringBuffer();
		// String listPassCycles = new String();
		GnPasswordParam passParam = applicationService.getPasswordParam();
		List<GnPasswordRestricted> listPasswordRestricteds = applicationService.listPasswordRestricteds();
		for (GnPasswordRestricted gnPasswordRestricted : listPasswordRestricteds) {
			bufferPassRestristed.append(gnPasswordRestricted.getPassword()).append("|");
		}
		if (bufferPassRestristed.length() > 1) {
			listPassRestricted = bufferPassRestristed.substring(0, bufferPassRestristed.length() - 1);
		}

		GnUser user = serializerService.deserialize(session.getId(), id, GnUser.class);
		user.setConfirmPassword(user.getConfirmPassword());
    	try{
    		String confirmPassword = "SimianSuper02";
			user.setJson((new JsonHelper()).serialize(user));
		}catch(Exception e) { e.printStackTrace(); }
    	

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_USER));
		render("Users/detail.html", user, mode, status, passParam, listPassRestricted);
	}

	// public static void resetPassword(Long id, String password, String
	// confirmPassword) {
	// try {
	// validation.required("user.password", password);
	// validation.required("confirmPassword", confirmPassword);
	// validation.equals(password, confirmPassword);
	// if (validation.hasErrors()) {
	// renderText("ERROR:" + "");
	// } else {
	// String newPassword =
	// Hex.encodeHexString(Cryptography.hash(decrypt(password)));
	// applicationService.resetPassword(id, newPassword);
	// }
	// } catch (MedallionException e) {
	// renderText("ERROR:" + e.getMessage());
	// }
	// }

	public static void resetPassword(String userId, String password, String historyPass, String username, String emailAddress) {
		log.debug("resetPassword. userId: " + userId + " password: " + password + " historyPass: " + historyPass + " username: " + username + " emailAddress: " + emailAddress);

		String newPassword = Hex.encodeHexString(Cryptography.hash(decrypt(password)));
		String admin = UIConstants.ADMINISTRATOR_NAME;

		// send email
		try {
			// get from email in the system param
			GnSystemParameter gnSysMailFrom = generalService.getSystemParameter(ReportQueueService.MAIL_FROM_RESET_PASSWORD);
			if (gnSysMailFrom == null) {
				throw new MedallionException("RESET-MAILFROM", "no from mail is available(" + ReportQueueService.MAIL_FROM_RESET_PASSWORD + ") in system param");
			}
			final String fromEmail = gnSysMailFrom.getValue();

			GnSystemParameter gnSysMailSubject = generalService.getSystemParameter(ReportQueueService.MAIL_FROM_RESET_PASSWORD_SUBJECT);
			if (gnSysMailFrom == null) {
				throw new MedallionException("RESET-MAILFROM", "no from mail is available(" + ReportQueueService.MAIL_FROM_RESET_PASSWORD_SUBJECT + ") in system param");
			}
			final String subjectEmail = gnSysMailSubject.getValue();

			MailVO email = new MailVO();
			email.setFrom(fromEmail);
			email.setTo(emailAddress);
			email.setMailSubject(subjectEmail);
			email.setHTML(true);
			email.setMailContent("<html>" + "	<tbody>" + "		<p>To " + username + " , </p> <br />" + "      <p>Your password had been reset to <b>" + historyPass + "</b> .Please change your password after login.</p><br />" + "      <p>Regards</p>" + "      <p>" + admin + "</p>" + "	</tbody>" + "</html>");
			mailService.sendMimeMail(email);
			applicationService.resetPassword(userId, newPassword);
		} catch (MailParseException e) {
			log.error(e.getMessage(), e);
			renderText("error.Send.Email");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			renderText("error.Reset.Email");
		}
	}

	private static byte[] decrypt(String password) {
		String key = "";
		try {
			for (int i = 0; i < session.getId().length(); i++) {
				if (Character.isDigit(session.getId().charAt(i))) {
					int digit = Integer.parseInt(session.getId().substring(i, i + 1));
					key = session.getId().substring(digit, digit + 8);
					break;
				}
			}
			return Cryptography.decrypt(Hex.decodeHex(password.toCharArray()), key);
		} catch (DecoderException e) {
			log.error("Error when trying to decrypt", e);
			return null;
		}
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnUser user = json.readValue(maintenanceLog.getNewData(), GnUser.class);
			// List<GnRoleMapping> roleMaps =
			// applicationService.listRoleByGroupUsers(session.get("groupName"));
			List<GnRole> roles = applicationService.listRoleActiveByUser(session.get(UIConstants.SESSION_USERNAME));
			for (GnRole role : roles) {
				/*** SUPERUSER ****/
				if (role.getRoleKey() == 1) {
					user.setRoleKey(1);
				}
				/*** SUPERVISOR ****/
				if (role.getRoleKey() == 1050) {
					user.setRoleKey(1050);
				}

			}
			
			try{
				user.setJson((new JsonHelper()).serialize(user));
			}catch(Exception e) { e.printStackTrace(); }
			
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("Users/approval.html", user, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			applicationService.approveUser(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			applicationService.approveUser(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void saveChangePassword(GnUser user) {
		log.debug("saveChangePassword. user: " + user);
	}

	@Check("users.sessionlist")
	public static void sessionlist() {
		log.debug("sessionlist. ");

		render("Users/session.html");
	}

	public static void table(Paging page, PublishMFParam param) {
		log.debug("table. page: " + page + " param: " + param);

		page.setData(generalService.getRegisterSession(session.getId()));
		renderJSON(page);
	}

	public static void unregisterSession(String[] sessionIds) {
		log.debug("unregisterSession. sessionIds: " + sessionIds);

		for (String sessionId : sessionIds) {
			log.debug("Unregistereed user " + sessionId);
			generalService.unregisterSession(sessionId);
		}
	}
}