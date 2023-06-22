package controllers;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import play.mvc.Controller;

import com.simian.medallion.service.CBestInterface;
import com.simian.medallion.service.TransactionService;

public class Tests extends Controller {
	private static Logger log = Logger.getLogger(Tests.class);

	@Inject
	static TransactionService transactionService;

	@Inject
	static CBestInterface cbestInterface;

	public static void index() {
		log.debug("index. ");

		render();
	}

	public static void cbest() {
		log.debug("cbest. ");

		transactionService.cbestTest(27101l);
		renderText("OK");
	}
}
