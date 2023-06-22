package controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.rcp.RemoteAuthenticationException;

import com.simian.medallion.model.GnAuditTrail;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.model.helper.SessionContainer;
import com.simian.medallion.service.ApplicationService;
import com.simian.medallion.service.GeneralService;

import helpers.Cryptography;
import helpers.MenuLoader;
import helpers.UIConstants;
import play.cache.Cache;
import play.i18n.Messages;
import play.mvc.Http.Header;

public class ApplicationSecurity extends Secure.Security {
	private static Logger log = Logger.getLogger(ApplicationSecurity.class);

	@Inject
	static ApplicationService applicationService;

	@Inject
	static GeneralService generalService;

	@Inject
	static AuthenticationManager authenticationManager;

	public static boolean preAuthenticate(String username, String password, String uuid) {
		try {
			String key = "";

			for (int i = 0; i < uuid.length(); i++) {
				if (Character.isDigit(uuid.charAt(i))) {
					int digit = Integer.parseInt(uuid.substring(i, i + 1));
					key = uuid.substring(digit, digit + 8);
					break;
				}
			}

			byte[] decrypted = Cryptography.decrypt(Hex.decodeHex(password.toCharArray()), key);
			String plainPassword = new String(decrypted);

			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, plainPassword));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	static boolean authenticate(String username, String password) {
		if (generalService.isUserExceed(username)) {
			validation.isTrue(false).message("User exceeded");
			validation.keep();
			return false;
		}

		String uuid = params.get("uuid");

		String key = "";
		try {
			for (int i = 0; i < uuid.length(); i++) {
				if (Character.isDigit(uuid.charAt(i))) {
					int digit = Integer.parseInt(uuid.substring(i, i + 1));
					key = uuid.substring(digit, digit + 8);
					break;
				}
			}
			byte[] decrypted = Cryptography.decrypt(Hex.decodeHex(password.toCharArray()), key);
			String plainPassword = new String(decrypted);

			try {
				authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, plainPassword));

				// Check for master database user
				GnUser user = applicationService.getUser(username);
				if (user == null) {
					String errMsg = Messages.get("auth.unregistered.user.in.app");
					validation.isTrue(false).message(errMsg);
					validation.keep();
					return false;
				}

				applicationService.logAccess(username, request.remoteAddress, "Login Success");

				return true;
			} catch (RemoteAuthenticationException ex) {
				log.error(ex.getMessage(), ex);
				validation.isTrue(false).message(ex.getMessage());
				validation.keep();
				applicationService.logAccess(username, request.remoteAddress, " Invalid Login ");
				return false;
			}
		} catch (DecoderException e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	static boolean check(String profile) {
		// logger.debug("check profile="+profile);

		// String access = Cache.get("a_" + session.getId(), String.class);
		// // String groupName = Cache.get("g_" + session.getId(),
		// String.class);
		//
		// if (access == null) {
		// String sessionId = session.getId();
		// String username = session.get(UIConstants.SESSION_USERNAME);
		// logger.debug("username "+username);
		// GnUser user = applicationService.getUser(username);
		// if (user != null) {
		// String[] result =
		// MenuLoader.parseUserMenu(applicationService.listMenusForUser(user.getUserKey()));
		// logger.debug("		"+Arrays.toString(result));
		// Cache.add("m_" + sessionId, result[0]);
		// Cache.add("s_" + sessionId, result[1]);
		// Cache.add("a_" + sessionId, result[2]);
		// // Cache.add("g_" + sessionId, result[3]);
		// access = result[2];
		// }
		// }
		//
		// logger.debug("Got access "+access);

		// Di ubah berdasarkan userid bukan berdasarkan sessionId
		String userid = session.get(UIConstants.SESSION_USERNAME);
		String access = Cache.get("a_" + userid, String.class);

		if (access == null) {
			GnUser user = applicationService.getUser(userid);
			if (user != null) {
				String[] result = MenuLoader.parseUserMenu(applicationService.listMenusForUser(user.getUserKey()));
				Cache.add("m_" + userid, result[0]);
				Cache.add("s_" + userid, result[1]);
				Cache.add("a_" + userid, result[2]);
				// Cache.add("g_" + sessionId, result[3]);
				access = result[2];
			}
		}

		// logger.debug("Got access "+access);

		// TODO [JUN] need to reload cache if not exist
		if (access != null) {
			boolean hasAccess = (access.indexOf(profile) >= 0);
			return hasAccess;
		}

		return false;
	}

	static void onAuthenticated() {
		String userId = session.get(UIConstants.SESSION_USERNAME);

		GnUser gnUser = applicationService.getUser(userId);
		String branchNo = "?";
		if ((gnUser.getEmployee() != null) && (gnUser.getEmployee().getBranch() != null)) {
			branchNo = gnUser.getEmployee().getBranch().getBranchNo();
		}

		GnAuditTrail auditTrail = new GnAuditTrail();
		auditTrail.setUser(gnUser);
		auditTrail.setRoles(gnUser.getRoles());
		auditTrail.setLoginDate(new Date());
		auditTrail.setLoginTime(new Date());
		auditTrail.setLoginStatus("OK");
		auditTrail = generalService.saveAuditTrail(auditTrail);

		session.put(UIConstants.SESSION_AUDIT_TRAIL_KEY, auditTrail.getAuditTrailKey());
		session.put(UIConstants.SESSION_USER_KEY, gnUser.getUserKey());
		session.put(UIConstants.SESSION_BRANCH, branchNo);

		Date loginStartDate = new Date();
		session.put(UIConstants.SESSION_LOGIN_START, loginStartDate.getTime());

		Map<String, Object> headers = new HashMap<String, Object>();
		for (String keyHeader : request.get().headers.keySet()) {
			Header header = request.get().headers.get(keyHeader);
			headers.put(keyHeader, header.value());
		}

		SessionContainer continer = new SessionContainer(session.getId(), gnUser, request.remoteAddress, new Date(), headers);
		generalService.registerSession(session.getId(), continer);
	}

	static void onDisconnect() {
		String userid = session.get(UIConstants.SESSION_USERNAME);

		applicationService.logAccess(userid, request.remoteAddress, "Logout Success ");
		generalService.unregisterSession(session.getId());
		// Cache.get("a_" + userid, String.class);
		Cache.delete("m_" + userid);
		Cache.delete("s_" + userid);
		Cache.delete("a_" + userid);

		Cache.delete(UIConstants.CACHE_URLS);
		Cache.delete(UIConstants.CACHE_PROGRAMS);
		Cache.delete(UIConstants.CACHE_MENU);
		Cache.delete(UIConstants.CACHE_MENU_ACCESS);
		Cache.delete(UIConstants.CACHE_PGM);
	}
}