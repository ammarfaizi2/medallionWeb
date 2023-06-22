package helpers;

import play.Play;

public class ApplicationProperties {

	private String dateFormat;
	private String jqueryDateFormat;
	private String javascriptDateFormat;
	private String displayDateFormat;
	private String dateMask;

	public void init() {
		setDateFormat(Play.configuration.getProperty("date.format"));
		setJqueryDateFormat(java2JqueryDateFormat(getDateFormat()));
		setDisplayDateFormat(java2JqueryDisplayDateFormat(getDateFormat()));
		setDateMask(jqueryDateFormatToMask(getDateFormat()));
		setJavascriptDateFormat(java2JavascriptDateFormat(getDateFormat()));
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getJqueryDateFormat() {
		return jqueryDateFormat;
	}

	public void setJqueryDateFormat(String jqueryDateFormat) {
		this.jqueryDateFormat = jqueryDateFormat;
	}

	public String getDisplayDateFormat() {
		return displayDateFormat;
	}

	public void setDisplayDateFormat(String displayDateFormat) {
		this.displayDateFormat = displayDateFormat;
	}

	private String java2JavascriptDateFormat(String format) {
		String jsDate = "";
		if (format != null) {
			if (format.equals("MM/dd/yyyy")) {
				jsDate = "mm/dd/yyyy";
			} else if (format.equals("dd/MM/yyyy")) {
				jsDate = "dd/mm/yyyy";
			} else if (format.equals("yyyy/MM/dd")) {
				jsDate = "yyyy/mm/dd";
			} else if (format.equals("yyyy/dd/MM")) {
				jsDate = "yyyy/dd/mm";
			}
		}
		return jsDate;
	}

	private String java2JqueryDateFormat(String format) {
		String jsDate = "";
		if (format != null) {
			if (format.equals("MM/dd/yyyy")) {
				jsDate = "mm/dd/yy";
			} else if (format.equals("dd/MM/yyyy")) {
				jsDate = "dd/mm/yy";
			} else if (format.equals("yyyy/MM/dd")) {
				jsDate = "yy/mm/dd";
			} else if (format.equals("yyyy/dd/MM")) {
				jsDate = "yy/dd/mm";
			}
		}
		return jsDate;
	}

	private String jqueryDateFormatToMask(String format) {
		String dateMask = "";
		if (format != null) {
			if (format.equals("MM/dd/yyyy")) {
				dateMask = "99/99/9999";
			} else if (format.equals("dd/MM/yyyy")) {
				dateMask = "99/99/9999";
			} else if (format.equals("yyyy/MM/dd")) {
				dateMask = "9999/99/99";
			} else if (format.equals("yyyy/dd/MM")) {
				dateMask = "9999/99/99";
			}
		}
		return dateMask;
	}

	private String java2JqueryDisplayDateFormat(String format) {
		String displayDate = "";
		if (format != null) {
			if (format.equals("MM/dd/yyyy")) {
				displayDate = "MM/DD/YYYY";
			} else if (format.equals("dd/MM/yyyy")) {
				displayDate = "DD/MM/YYYY";
			} else if (format.equals("yyyy/MM/dd")) {
				displayDate = "YYYY/MM/DD";
			} else if (format.equals("yyyy/dd/MM")) {
				displayDate = "YYYY/DD/MM";
			}
		}
		return displayDate;
	}

	public String getDateMask() {
		return dateMask;
	}

	public void setDateMask(String dateMask) {
		this.dateMask = dateMask;
	}

	public String getJavascriptDateFormat() {
		return javascriptDateFormat;
	}

	public void setJavascriptDateFormat(String javascriptDateFormat) {
		this.javascriptDateFormat = javascriptDateFormat;
	}
}
