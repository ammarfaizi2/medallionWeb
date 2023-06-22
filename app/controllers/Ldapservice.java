package controllers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import play.mvc.Controller;

import com.simian.medallion.model.GnUser;
import com.simian.medallion.service.ApplicationService;

public class Ldapservice extends Controller {
	private static Logger log = Logger.getLogger(Ldapservice.class);

	// 00 SUCCESS
	// 01 MUST CHANGE PASSWORD
	// 99 Error: Global Exception
	// 97 Error: Password Is Invalid
	// 90 Minimum password length is 8 character
	// 91 Maximum password length is 16 character
	// 92 Password does not contains required character
	// 93 Password is already in used
	// 98 Error: User Is Invalid

	@Inject
	protected static ApplicationService applicationService;

	// http://localhost:9000/ldapService?operation=verifyPassword&id=SIMIANUSER1&password=SimianSuper01
	public static void ldapService(String operation, String id, String password) {
		log.debug("ldapService. operation: " + operation + " id: " + id + " password: " + password);

		Map<String, String> map = new HashMap<String, String>();

		if (!"verifyPassword".equals(operation)) {
			map.put("ResponseCode", "97");
			map.put("ResponseDescription", "Invalid Operation " + operation);
			map.put("Addon", operation + " " + id + " " + password);
		} else {
			GnUser gnUser = applicationService.getUser(id);
			if (gnUser == null) {
				gnUser = new GnUser();
			}

			log.error("uiPassword : " + password);
			log.error("dbPassword : " + gnUser.getPassword());
			boolean isMatch = password.equals(gnUser.getPassword());

			map.put("ResponseCode", isMatch ? "00" : "97");
			// map.put("ResponseCode", "93");
			map.put("ResponseDescription", isMatch ? "SUCCESS" : "FAIL TO LOGIN");
			map.put("Addon", operation + " " + id + " " + password);
		}

		renderJSON(map);
	}
}
