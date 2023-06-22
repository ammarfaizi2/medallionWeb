package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.IntBatch;

@With(Secure.class)
public class ReprocessH2H extends MedallionController {
	private static Logger log = Logger.getLogger(ReprocessH2H.class);

	@Check("utility.reprocessh2h")
	public static void list() {
		log.debug("list. ");

		Date appDate = generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID);
		String prevDate = Registry.fmtMMDDYYYY(generalService.getLastBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID));
		IntBatch ib = new IntBatch();
		ib.setProcessFrom(appDate);
		ib.setProcessTo(appDate);
		ib.setTotalTransaction(0);

		render("ReprocessH2H/list.html", ib, prevDate);
	}

	@Check("utility.reprocessh2h")
	public static void entry() {
		log.debug("entry. ");
	}

	@Check("utility.reprocessh2h")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);
	}

	@Check("utility.reprocessh2h")
	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	@Check("utility.reprocessh2h")
	public static void load(Date processFrom, Date processTo) {
		log.debug("load. processFrom: " + processFrom + " processTo: " + processTo);

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
			result.put("processFrom", dateFormat.format(processFrom));
			result.put("processTo", dateFormat.format(processTo));
			result.put("totalTransaction", sftpService.listH2HReprocess(processFrom, processTo).size());
			result.put("status", "success");
		} catch (MedallionException e) {
			result.put("status", "error");
		}

		renderJSON(result);
	}

	@Check("utility.reprocessh2h")
	public static void process(Date processFrom, Date processTo) {
		log.debug("process. processFrom: " + processFrom + " processTo: " + processTo);

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			sftpService.h2hReprocess(processFrom, processTo);

			result.put("message", "Please check your transaction status on monitoring H2H report");
			result.put("status", "success");
		} catch (MedallionException e) {
			result.put("status", "error");
		}
		renderJSON(result);
	}
}