package controllers;

import helpers.Cryptography;
import helpers.UIConstants;

import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnPasswordParam;
import com.simian.medallion.model.GnPasswordRestricted;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.model.helper.Helper;

@With(Secure.class)
public class ChangePasswords extends MedallionController {
	private static Logger log = Logger.getLogger(ChangePasswords.class);

	@Before(only = { "entry", "save", "confirming", "confirm", "back" })
	public static void setup() {
		log.debug("setup. ");

		String loginType = generalService.getValueParam(SystemParamConstants.PARAM_SECURITY_LOGIN_APP_TYPE);
		renderArgs.put("loginLDAP",  !"SYSTEM".equals(loginType) ? true : false);
	}

	public static void list() {
		log.debug("list. ");
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	// @Check("security.changePassword")
	public static void entry(String from) {
		log.debug("entry. from: " + from);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;

		GnUser user = new GnUser();
		GnUser oldUser = applicationService.getUser(session.get("username"));
		log.debug("PASS MUST CHANGE");
		if (from != null) {
			if (from.equals(UIConstants.CHANGE_PASSWORD_SCREEN_FROM)) {
				user.setPasswordMustChange(true);
			}
		}

		StringBuffer bufferPassRestristed = new StringBuffer();
		String listPassRestricted = new String();
		StringBuffer bufferPassCycles = new StringBuffer();
		String listPassCycles = new String();
		GnPasswordParam passParam = applicationService.getPasswordParam();
		List<GnPasswordRestricted> listPasswordRestricteds = applicationService.listPasswordRestricteds();
		for (GnPasswordRestricted gnPasswordRestricted : listPasswordRestricteds) {
			bufferPassRestristed.append(gnPasswordRestricted.getPassword()).append("|");
		}
		if (bufferPassRestristed.length() > 1) {
			listPassRestricted = bufferPassRestristed.substring(0, bufferPassRestristed.length() - 1);
		}

		List<String> listPasswordCycles = user.splitPasswordHistories(oldUser.getPasswordHistory());
		log.debug("LIST PASS HISTORY = " + listPasswordCycles);
		if (passParam != null) {
			if (listPasswordCycles.size() > passParam.getMaxReuse()) {
				int length = listPasswordCycles.size() - passParam.getMaxReuse();
				// int idx = passParam.getMaxReuse();
				for (int i = 0; i < length; i++) {
					listPasswordCycles.remove(0);
				}
			}
		}
		log.debug("List Password Cycles = " + listPasswordCycles);
		for (String item : listPasswordCycles) {
			bufferPassCycles.append(item).append("|");
		}
		if (bufferPassCycles.length() > 1) {
			listPassCycles = bufferPassCycles.substring(0, bufferPassCycles.length() - 1).toString();
		}
		log.debug("List Password Cycles 1 = " + listPassCycles);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CHANGE_PASSWORD));
		render("ChangePasswords/detail.html", user, mode, passParam, listPassRestricted, listPassCycles, from);
	}

	// @Check("security.changePassword")
	public static void save(GnUser user, String mode, String from) throws Exception {
		log.debug("save. user: " + user + " mode: " + mode + " from: " + from);

		if (user != null) {
			GnUser oldUser = applicationService.getUser(session.get("username"));
			if (!Helper.isNullOrEmpty(user.getCurrentPassword())) {
				user.setCurrentPassword(Hex.encodeHexString(Cryptography.hash(decrypt(user.getCurrentPassword()))));
			}

			validation.required("Current Password is", user.getCurrentPassword());
			validation.required("New Password is", user.getPassword());
			validation.required("Confirm Password is", user.getConfirmPassword());

			if (!user.getCurrentPassword().isEmpty()) {
				if (!user.getCurrentPassword().equals(oldUser.getPassword())) {
					validation.addError(null, "pass.invalid_oldPassword");
				}
			}

			if (validation.hasErrors()) {
				StringBuffer bufferPassRestristed = new StringBuffer();
				String listPassRestricted = new String();
				StringBuffer bufferPassCycles = new StringBuffer();
				String listPassCycles = new String();
				GnPasswordParam passParam = applicationService.getPasswordParam();
				List<GnPasswordRestricted> listPasswordRestricteds = applicationService.listPasswordRestricteds();
				for (GnPasswordRestricted gnPasswordRestricted : listPasswordRestricteds) {
					bufferPassRestristed.append(gnPasswordRestricted.getPassword()).append("|");
				}
				if (bufferPassRestristed.length() > 1) {
					listPassRestricted = bufferPassRestristed.substring(0, bufferPassRestristed.length() - 1);
				}

				List<String> listPasswordCycles = user.splitPasswordHistories(oldUser.getPasswordHistory());
				if (passParam != null) {
					if (listPasswordCycles.size() > passParam.getMaxReuse()) {
						int length = listPasswordCycles.size() - passParam.getMaxReuse();
						// int idx = passParam.getMaxReuse();
						for (int i = 0; i < length; i++) {
							listPasswordCycles.remove(0);
						}
					}
				}
				for (String item : listPasswordCycles) {
					bufferPassCycles.append(item).append("|");
				}
				if (bufferPassCycles.length() > 1) {
					listPassCycles = bufferPassCycles.substring(0, bufferPassCycles.length() - 1).toString();
				}
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CHANGE_PASSWORD));
				render("ChangePasswords/detail.html", user, mode, passParam, listPassRestricted, listPassCycles, from);
			} else {
				Long id = oldUser.getUserKey();
				serializerService.serialize(session.getId(), id, user);
				confirming(id, mode, from);
			}
		}

	}

	// @Check("security.changePassword")
	public static void confirming(Long id, String mode, String from) {
		log.debug("confirming. id: " + id + " mode: " + mode + " from: " + from);

		renderArgs.put("confirming", true);
		GnUser user = serializerService.deserialize(session.getId(), id, GnUser.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CHANGE_PASSWORD));
		render("ChangePasswords/detail.html", user, mode, id, from);
	}

	// @Check("security.changePassword")
	public static void confirm(GnUser user, String mode, String from) {
		log.debug("confirm. user: " + user + " mode: " + mode + " from: " + from);

		if (!Helper.isNullOrEmpty(user.getPassword())) {
			user.setPassword(Hex.encodeHexString(Cryptography.hash(decrypt(user.getPassword()))));
		}

		try {
			String username = session.get("username");
			applicationService.updateChangePassword(user, username);
			if (from != null) {
				if (from.equals(UIConstants.CHANGE_PASSWORD_SCREEN_FROM)) {
					String successMessage = "Your Password has been changed, please login with new password.";
					flash.put("successMessage", successMessage);
					entry(from);
				} else {
					entry("");
				}
			}

		} catch (MedallionException ex) {
			flash.error(Messages.get(ex.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CHANGE_PASSWORD));
			render("ChangePasswords/detail.html", user, mode, confirming, from);
		}
	}

	// @Check("security.changePassword")
	public static void back(Long id, String mode, String from) {
		log.debug("back. id: " + id + " mode: " + mode + " from: " + from);

		GnUser user = serializerService.deserialize(session.getId(), id, GnUser.class);
		log.debug("USER = " + user);
		GnUser oldUser = applicationService.getUser(session.get("username"));

		StringBuffer bufferPassRestristed = new StringBuffer();
		String listPassRestricted = new String();
		StringBuffer bufferPassCycles = new StringBuffer();
		String listPassCycles = new String();
		GnPasswordParam passParam = applicationService.getPasswordParam();
		List<GnPasswordRestricted> listPasswordRestricteds = applicationService.listPasswordRestricteds();
		for (GnPasswordRestricted gnPasswordRestricted : listPasswordRestricteds) {
			bufferPassRestristed.append(gnPasswordRestricted.getPassword()).append("|");
		}
		if (bufferPassRestristed.length() > 1) {
			listPassRestricted = bufferPassRestristed.substring(0, bufferPassRestristed.length() - 1);
		}

		List<String> listPasswordCycles = user.splitPasswordHistories(oldUser.getPasswordHistory());
		if (passParam != null) {
			if (listPasswordCycles.size() > passParam.getMaxReuse()) {
				int length = listPasswordCycles.size() - passParam.getMaxReuse();
				for (int i = 0; i < length; i++) {
					listPasswordCycles.remove(0);
				}
			}
		}
		for (String item : listPasswordCycles) {
			bufferPassCycles.append(item).append("|");
		}
		if (bufferPassCycles.length() > 1) {
			listPassCycles = bufferPassCycles.substring(0, bufferPassCycles.length() - 1).toString();
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CHANGE_PASSWORD));
		render("ChangePasswords/detail.html", user, mode, passParam, listPassRestricted, listPassCycles, from);

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
			log.error("Error when trying to decrypt ", e);
			return null;
		}
	}
}