package controllers;

import java.util.List;

import org.apache.log4j.Logger;

import com.simian.medallion.model.GnReportMapping;
import com.simian.medallion.model.GnUser;

public class Reports extends MedallionController {
	private static Logger log = Logger.getLogger(Reports.class);

	public static void index() {
		log.debug("index. ");

		render();
	}

	// public static void show(String reportName) {
	//
	// PreviewReport report;
	// try {
	// report = new PreviewReport("/reports/" + reportName + ".prpt");
	// report.generateReport(OutputType.PDF, response.out);
	// response.contentType = "application/pdf";
	// } catch (ReportProcessingException e) {
	// logger.error(e.getMessage());
	// }
	// }

	public static void list() {
		log.debug("list. ");

		String username = session.get("username");
		GnUser user = applicationService.getUser(username);
		Long userKey = user.getUserKey();

		List<GnReportMapping> reportMappings = generalService.listReportMappingByRole(userKey);
		String url = request.domain;
		render(url, reportMappings);
	}

	public static void param(String id) {
		log.debug("param. id: " + id);

		String name = "";
		if ("HoldingBalance".equals(id)) {
			name = "Holding Balance Report";
		} else if ("PortfolioValuation".equals(id)) {
			name = "Portfolio Valuation Report";
		} else if ("TransactionListing".equals(id)) {
			name = "Transaction Listing Report";
		}
		render(id, name);
	}
}