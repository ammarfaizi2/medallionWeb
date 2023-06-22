package controllers;

import helpers.UIConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnPasswordParam;
import com.simian.medallion.model.GnPasswordRestricted;

public class PasswordParams extends MedallionController {
	private static Logger log = Logger.getLogger(PasswordParams.class);

	public static void list() {
		log.debug("list. ");
		// List<GnPasswordParam> passParams =
		// applicationService.listPasswordParams();
		// render(passParams);
	}

	@Check("security.passwordParams")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnPasswordParam pass = new GnPasswordParam();
		List<GnPasswordRestricted> passRestricteds = new ArrayList<GnPasswordRestricted>();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UTL_PASSWORD_PARAM));
		render("PasswordParams/detail.html", pass, mode, passRestricteds);
	}

	@Check("security.passwordParams")
	public static void edit() {
		log.debug("edit. ");

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnPasswordParam pass = applicationService.getPasswordParam();
		List<GnPasswordRestricted> passRestricteds = applicationService.listPasswordRestricteds();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UTL_PASSWORD_PARAM));
		render("PasswordParams/detail.html", pass, mode, passRestricteds);

	}

	public static void view() {
		log.debug("view. ");
	}

	@Check("security.passwordParams")
	public static void save(GnPasswordParam pass, List<GnPasswordRestricted> passRestricteds, String mode) {
		log.debug("save. pass: " + pass + " passRestricteds: " + passRestricteds + " mode: " + mode);

		if (passRestricteds != null) {
			for (GnPasswordRestricted passwordRestricted : passRestricteds) {
				pass.getPassRestricteds().add(passwordRestricted);
			}
		}
		if (pass != null) {
			// === Validation === //
			validation.required("Minimum Length is", pass.getMinLength());
			validation.required("Maximum Length is", pass.getMaxLength());
			if ((pass.getCombiChar() == null) && (pass.getCombiNum() == null) && (pass.getCombiSpechar() == null))
				validation.required("Combination of is", pass.getCombiSpechar());
			if (pass.getCombiChar() != null) {
				if (pass.getCombiChar() == true) {
					validation.required("Has Minimum Character is", pass.getMinChar());
					validation.required("Minimum Uppercase is", pass.getMinUpper());
					validation.required("Minimum Lowercase is", pass.getMinLower());
					if ((pass.getMinUpper() != null) && (pass.getMinLower() != null) && (pass.getMinChar() != null)) {
						if (pass.getMinUpper() + pass.getMinLower() > pass.getMinChar()) {
							validation.addError(null, "passParam.minChar_lt_totMinUpperLower");
						}
					}
				}
			}

			if (pass.getCombiNum() != null) {
				if (pass.getCombiNum() == true) {
					validation.required("Has Minimum Numeric is", pass.getMinNum());
					validation.required("Number of Ordered Numeric Not Allowed is", pass.getNumOrdered());
				}
			}

			if (pass.getCombiSpechar() != null) {
				if (pass.getCombiSpechar() == true) {
					validation.required("Has Minimum Spec. Char is", pass.getMinSpechar());
				}
			}
			// logger.debug("combi char" + pass.getCombiChar());
			// logger.debug("combi num" + pass.getCombiNum());
			// logger.debug("combi spechar" + pass.getCombiSpechar());
			if ((pass.getCombiChar() != null) || (pass.getCombiNum() != null) || (pass.getCombiSpechar() != null)) {
				int minChar;
				int minNum;
				int minSpechar;
				// if
				// ((pass.getCombiChar()==true)||(pass.getCombiNum()==true)||(pass.getCombiSpechar()==true)){
				if (((pass.getMinChar() != null) || (pass.getMinNum() != null) || (pass.getMinSpechar() != null)) && (pass.getMaxLength() != null)) {
					if (pass.getMinChar() == null)
						minChar = 0;
					else
						minChar = pass.getMinChar();
					if (pass.getMinNum() == null)
						minNum = 0;
					else
						minNum = pass.getMinNum();
					if (pass.getMinSpechar() == null)
						minSpechar = 0;
					else
						minSpechar = pass.getMinSpechar();
					log.debug("TOTAL = " + (minChar + minNum + minSpechar));
					if ((minChar + minNum + minSpechar) > pass.getMaxLength()) {
						validation.addError(null, "passParam.maxLength_less_or_equal_totMinCharNumSpechar");
					}
				}
				// }

			}

			validation.required("Max. Password Retry is", pass.getMaxRetry());
			validation.required("Reuse Password Cycle is", pass.getMaxReuse());
			validation.required("Disable Login Period is", pass.getDisablePeriod());
			validation.required("Expired Password Warning is", pass.getExpireWarningPeriod());
			validation.required("Expire Password After is", pass.getExpirePeriod());

			if (validation.hasErrors()) {
				log.debug("value combichar" + pass.getCombiChar());
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UTL_PASSWORD_PARAM));
				render("PasswordParams/detail.html", pass, passRestricteds, mode);
			} else {
				Long id = pass.getParamId();
				serializerService.serialize(session.getId(), pass.getParamId(), pass);
				confirming(id, mode);
			}
		} else {
			flash.error("argument.null", pass);
		}
	}

	@Check("security.passwordParams")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		GnPasswordParam pass = serializerService.deserialize(session.getId(), id, GnPasswordParam.class);
		List<GnPasswordRestricted> passRestricteds = new ArrayList<GnPasswordRestricted>(pass.getPassRestricteds());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UTL_PASSWORD_PARAM));
		render("PasswordParams/detail.html", pass, passRestricteds, mode);
	}

	@Check("security.passwordParams")
	public static void confirm(GnPasswordParam pass, List<GnPasswordRestricted> passRestricteds, String mode) {
		log.debug("confirm. pass: " + pass + " passRestricteds: " + passRestricteds + " mode: " + mode);

		try {
			if (passRestricteds != null) {
				pass.setPassRestricteds(new HashSet<GnPasswordRestricted>(passRestricteds));
			}
			if (pass.getCombiNum() != null)
				pass.setListOrderedNumber(orderedNumber(pass.getNumOrdered()));
			applicationService.savePasswordParam(pass);
			edit();
		} catch (MedallionException e) {
			flash.error("Param id : '" + pass.getParamId() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UTL_PASSWORD_PARAM));
			render("PasswordParams/detail.html", pass, mode, passRestricteds, confirming);
		}
	}

	@Check("security.passwordParams")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		GnPasswordParam pass = serializerService.deserialize(session.getId(), id, GnPasswordParam.class);
		List<GnPasswordRestricted> passRestricteds = new ArrayList<GnPasswordRestricted>(pass.getPassRestricteds());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.UTL_PASSWORD_PARAM));
		render("PasswordParams/detail.html", pass, passRestricteds, mode);
	}

	@Check("security.passwordParams")
	public static String orderedNumber(int numOrdered) {
		log.debug("orderedNumber. numOrdered: " + numOrdered);

		String separate = new String("|");
		StringBuffer strOrderNumber = new StringBuffer();

		int[][] asc = new int[9][numOrdered];
		int[][] desc = new int[10][numOrdered];
		int row, col = 0;

		// ORDER ASCENDING
		for (row = 0; row < 9; row++) {
			for (col = 0; col < numOrdered; col++) {
				asc[row][col] = row + col;
				if ((asc[row][col]) > 9) {
					break;
				}
			}
		}

		for (row = 0; row < 9; row++) {
			for (col = 0; col < numOrdered; col++) {
				if (asc[row][col] > 9)
					break;
				strOrderNumber = strOrderNumber.append(asc[row][col]);
				if (asc[row][col] == 9)
					break;
			}
			if (col < numOrdered)
				break;
			strOrderNumber = strOrderNumber.append(separate);
		}

		strOrderNumber = strOrderNumber.append(separate);

		// ORDER DESCENDING
		for (row = 9; row > 0; row--) {
			for (col = 0; col < numOrdered; col++) {
				desc[row][col] = row - col;
				if ((desc[row][col]) < 0) {
					break;
				}
			}
		}

		for (row = 9; row > 0; row--) {
			for (col = 0; col < numOrdered; col++) {
				if (desc[row][col] < 0)
					break;
				strOrderNumber = strOrderNumber.append(desc[row][col]);
				if (desc[row][col] == 0)
					break;
			}
			if (col < numOrdered)
				break;
			strOrderNumber = strOrderNumber.append(separate);
		}
		return strOrderNumber.toString();
	}

}
