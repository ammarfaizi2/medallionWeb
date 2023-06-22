package controllers;

import org.apache.log4j.Logger;

import play.mvc.Controller;
import vo.TransactionEntry;

public class Transaction1 extends Controller {
	private static Logger log = Logger.getLogger(Transaction1.class);

	public static void index() {
		log.debug("index. ");

		// List<Organization> orgs = Organization.findAll();
		// render(orgs);
		render();
	}

	public static void transactionEntryBonds() {
		log.debug("transactionEntryBonds. ");

		// String quantity, String price, String nominal, String capGainTax,
		// String interestRate, String accruedInterest, String interestTax,
		// String netInterest
		TransactionEntry trans = new TransactionEntry("1/1/2010", "1234567890", "B", "1110", "FI", "FIB", "ASII", "1/3/2010", "Sandi", "BUY", "RVP FIXED INTEREST BOND", "FIXED INCOME", "FIXED INTEREST BOND", "PT ASTRA INTERNASIONAL", "NA", "NOT AVAILABLE", "10,023,287.67", "6,000.00", "10,029,287.67", "F1", "F2", "F3", "1,000.00", "2,000.00", "3,000.00", "10,000,000.00", "100", "10,000,000.00", "180,000.00", "10", "10", "27,397.26", "4,109.59", "23,287.67");
		render(trans);
		// render();
	}

	public static void transactionEntrySHS() {
		log.debug("transactionEntrySHS. ");

		// String quantity, String price, String nominal ,String capGainTax
		TransactionEntry trans = new TransactionEntry("1/1/2010", "1234567890", "B", "1110", "EQ", "SHS", "ASII", "1/3/2010", "Sandi", "BUY", "RVP EQ ORDINARY SHARES", "EQUITY", "ORDINARY SHARES", "PT ASTRA INTERNASIONAL", "NA", "NOT AVAILABLE", "1,000,000.00", "6,000.00", "1,006,000.00", "F1", "F2", "F3", "1,000.00", "2,000.00", "3,000.00", "5,000.00", "20,000.00", "1,000,000.00", "180,000.00");
		render(trans);
	}

	public static void transactionEntrySBI() {
		log.debug("transactionEntrySBI. ");

		// String nominal, String discountAmount, String discountTax, String
		// maturityDate, String discountDays, String discountRate
		TransactionEntry trans = new TransactionEntry("1/1/2010", "1234567890", "B", "1110", "EQ", "SHS", "ASII", "1/1/2010", "Sandi", "BUY", "RVP EQ ORDINARY SHARES", "EQUITY", "ORDINARY SHARES", "PT ASTRA INTERNASIONAL", "NA", "NOT AVAILABLE", "9,000,000.00", "6,000.00", "9,006,000.00", "F1", "F2", "F3", "1,000.00", "2,000.00", "3,000.00", "10,000,000.00", "10,000,000.00", "10,000.00", "1/10/2010", "9.00", "4.51");
		render(trans);
	}

	public static void transactionEntryDOC() {
		log.debug("transactionEntryDOC. ");

		// String nominal, String maturityDate, String accrualDays, String
		// interestRate, String interestAmount, String interestTax, String
		// netInterestRate, String bilyetNumber
		TransactionEntry trans = new TransactionEntry("1/1/2010", "1234567890", "B", "1110", "EQ", "SHS", "ASII", "1/1/2010", "Sandi", "BUY", "RVP EQ ORDINARY SHARES", "EQUITY", "ORDINARY SHARES", "PT ASTRA INTERNASIONAL", "NA", "NOT AVAILABLE", "10,000,000.00", "6,000.00", "10,006,000.00", "F1", "F2", "F3", "1,000.00", "2,000.00", "3,000.00", "1,000.00", "1/10/2010", "9.00", "10.00", "24,657.53", "4,931.51", "19,726.03", "ABCD123");
		render(trans);
	}
}