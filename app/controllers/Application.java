package controllers;

import helpers.UIConstants;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import play.cache.Cache;
import play.mvc.With;

import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.model.GnUser;

@With(Secure.class)
public class Application extends MedallionController {
	private static Logger log = Logger.getLogger(Application.class);

	public static void index() {
		session.remove("breadcrumb");
		String username = session.get("username");

		Date loginDate = new Date();
		applicationService.updateLastLoginDate(loginDate, username);
		GnUser user = applicationService.getUser(username);

		log.debug("Past Must Change = " + user.getPasswordMustChange());
		log.debug("Group Name = " + session.get("groupName"));

		String loginType = generalService.getValueParam(SystemParamConstants.PARAM_SECURITY_LOGIN_APP_TYPE);
		if ("SYSTEM".equals(loginType)) {
			if (user.getPasswordMustChange()) {
				String from = UIConstants.CHANGE_PASSWORD_SCREEN_FROM;
				ChangePasswords.entry(from);
			}
		}
		render();
	}

	@SuppressWarnings("unchecked")
	public static void getMenuAccessList(String menu) {
		Map<String, String> programList = Cache.get(UIConstants.CACHE_PROGRAMS, Map.class);
		Map<String, String> menuAccessList = Cache.get(UIConstants.CACHE_MENU_ACCESS, Map.class);
		log.debug("[APP] [OPEN] programList : " + programList.get(menu) + " menuAccessList = " + menuAccessList.get(menu));
		if (programList.containsKey(menu)) {
			renderText(menuAccessList.get(menu));
		} else {
			// response.status = 500;
			renderText("NotFound");
		}
	}

	@SuppressWarnings("unchecked")
	public static void open(String menu) {
		Map<String, String> programList = Cache.get(UIConstants.CACHE_PROGRAMS, Map.class);
		Map<String, String> menuAccessList = Cache.get(UIConstants.CACHE_MENU_ACCESS, Map.class);
		log.debug("[APP] [OPEN] programList : " + programList.get(menu) + " menuAccessList = " + menuAccessList.get(menu));
		if (programList.containsKey(menu)) {
			//String url = "http" + (request.secure ? "s" : "") + "://" + request.host + programList.get(menu);
			renderText(programList.get(menu));
		} else {
			response.status = 500;
		}
	}

	public static void unauthorized() {
		render();
	}

	public static void addingValidation() {

	}

}