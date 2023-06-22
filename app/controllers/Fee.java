package controllers;

import org.apache.log4j.Logger;

import play.mvc.Controller;

public class Fee extends Controller {
	private static Logger log = Logger.getLogger(FaTransactionMasters.class);

	public static void index() {
		log.debug("index. ");

		render();
	}

	public static void feeMaintenance() {
		log.debug("feeMaintenance. ");

		render();
	}

	public static void feeProfile() {
		log.debug("feeProfile. ");

		render();
	}

	public static void feeDescriptionDetail() {
		log.debug("feeDescriptionDetail. ");

		render();
	}

	public static void feeProfileDetail() {
		log.debug("feeProfileDetail. ");

		render();
	}

}