package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Validation extends MedallionController {
	// 0 : validasi gagal
	// 1 : validasi success

	public static void profesi(String data) {
		List<String> profesilist = new ArrayList<String>();
		profesilist.add("programer");
		profesilist.add("suster");
		profesilist.add("tukangcukur");
		profesilist.add("pedagang");

		boolean contain = (profesilist.contains(data));
		renderJSON(contain ? 1 : 0);
	}

	public static void required(String data) {
		int result = 0;
		if (data != null && !"".equals(data)) {
			result = 1;
		}
		renderJSON(result);
	}

	public static void maxLength(String data, int length) {
		int result = 0;
		if (data != null) {
			result = (data.length() > length) ? 0 : 1;
		}
		renderJSON(result);
	}

	public static void minLength(String data, int length) {
		int result = 0;
		if (data != null) {
			result = (data.length() < length) ? 0 : 1;
		}
		renderJSON(result);
	}

	public static void max(int data, int max) {
		int result = (data > max) ? 0 : 1;
		renderJSON(result);
	}

	public static void min(int data, int min) {
		int result = (data < min) ? 0 : 1;
		renderJSON(result);
	}

	public static void between(int data, int min, int max) {
		int result = (data < min || data > max) ? 0 : 1;
		renderJSON(result);
	}
}
