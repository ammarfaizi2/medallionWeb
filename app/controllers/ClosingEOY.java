package controllers;

import helpers.UIConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.FaMaster;
import com.simian.medallion.model.FaTransaction;
import com.simian.medallion.model.FaTransactionDetail;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.PublishMFRow;

@With(Secure.class)
public class ClosingEOY extends MedallionController {
	private static Logger log = Logger.getLogger(ClosingEOY.class);

	@Check("transaction.closingEOY")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaTransaction faTransaction = new FaTransaction();
		faTransaction.setTransactionDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		faTransaction.setName((Integer.parseInt(Helper.formatY(faTransaction.getTransactionDate())) - 1) + "");
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_CLOSING_EOY));
		render("ClosingEOY/entry.html", faTransaction, mode);
	}

	@Check("transaction.closingEOY")
	public static void process(FaTransaction faTransaction, String mode) {
		log.debug("process. faTransaction: " + faTransaction + " mode: " + mode);
		
		if (faTransaction != null) {
			validation.required("Fund Code is", faTransaction.getFaMaster().getFundCode());
			validation.required("Closing Year is", faTransaction.getName());
			validation.required("Closing Date is", faTransaction.getTransactionDate());

			Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();

			if (faTransaction.getTransactionDate() != null && !Helper.isNullOrEmpty(faTransaction.getName())) {

				int appDateYear = Integer.parseInt(Helper.formatY(faTransaction.getTransactionDate()));
				int nameYear = Integer.parseInt(faTransaction.getName());

				if (faTransaction.getTransactionDate().compareTo(appDate) > 0) {
					validation.addError("", "closing.eoy.closingDateGreaterAppDate");
				}

				if (nameYear >= appDateYear) {
					validation.addError("", "closing.eoy.closingYearGreaterAppDate");
				}
				log.info("nameYear " + nameYear);
				log.info("appDateYear " + appDateYear);

				Date lastOEY = Helper.getLastDayOfYear(null, nameYear);
				log.info("lastOEY " + lastOEY);
				lastOEY = Helper.removeTime(lastOEY);
				
				PublishMFRow mfRow = fundService.getPublishMfRow(faTransaction.getFaMaster().getFundKey(), lastOEY);
				if (mfRow.getLastPosted() != null && mfRow.getLastPosted().compareTo(lastOEY) < 0) {
					log.info("mfRow.getLastPosted() " + mfRow.getLastPosted());
					validation.addError("", "Posting Is Required");

				}
				if (faTransaction.getFaMaster().getFundKey() != null) {
					FaMaster faMaster = fundService.getFaFundSetupForPick(faTransaction.getFaMaster().getFundKey());

					Calendar calFa = Calendar.getInstance();
					calFa.setTime(faMaster.getRecordCreatedDate());
					int yearFaMas = calFa.get(Calendar.YEAR);
					if (yearFaMas > nameYear) {
						validation.addError("", "Last EOY is not " + nameYear);
					}
				}

			}

			if (faTransaction.getFaMaster().getFundKey() == null) {
				faTransaction.getFaMaster().setFundDescription(null);
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_CLOSING_EOY));
				render("ClosingEOY/entry.html", faTransaction, mode);
			} else {
				serializerService.serialize(session.getId(), faTransaction.getTransactionKey(), faTransaction);
				confirming(faTransaction.getTransactionKey(), mode);
			}
		} else {
			entry();
		}
	}

	@Check("transaction.closingEOY")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		boolean confirming = true;
		FaTransaction faTransaction = serializerService.deserialize(session.getId(), id, FaTransaction.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_CLOSING_EOY));
		render("ClosingEOY/entry.html", faTransaction, mode, confirming);
	}

	@Check("transaction.closingEOY")
	public static void confirm(FaTransaction faTransaction) {
		log.debug("confirm. faTransaction: " + faTransaction);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		boolean confirming = true;
		if (faTransaction == null) {
			entry();
		}
		int nameYear = Integer.parseInt(faTransaction.getName());
		Date lastOEY = Helper.getLastDayOfYear(null, nameYear);
		faTransaction.setClosingEOY(lastOEY);

		try {
			faTransaction.setFaTransactionDetails(new HashSet<FaTransactionDetail>());
			FaTransaction trx = fundService.createClosingEOY(MenuConstants.FA_CLOSING_EOY, faTransaction, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
			if (trx.getTransactionKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("transaction.confirmed.successful", trx.getTransactionKey()));
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_CLOSING_EOY));
				render("ClosingEOY/entry.html", trx, mode, confirming);
			}
		} catch (MedallionException ex) {
			ex.printStackTrace();
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if (ex != null && ex.getParams() != null) {
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			} else {
				result.put("error", Messages.get(ex.getErrorCode()));
			}
			renderJSON(result);
		} catch (Exception ex) {
			ex.printStackTrace();
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("error", Messages.get(ex.getMessage()));
			renderJSON(result);
		}
	}

	@Check("transaction.closingEOY")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		FaTransaction faTransaction = serializerService.deserialize(session.getId(), id, FaTransaction.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_CLOSING_EOY));
		render("ClosingEOY/entry.html", faTransaction, mode);
	}
}
