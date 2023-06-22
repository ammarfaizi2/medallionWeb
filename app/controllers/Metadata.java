package controllers;

public class Metadata extends MedallionController {
	//private static String textfield = "textfield";
	//private static String textarea = "textarea";
	//private static String combobox = "combobox";
	//private static String datepicker = "datepicker";
	//private static String radio = "radio";
	//private static String checkbox = "checkbox";
	//private static String autocomplete = "autocomplete";
	//private static String label = "label";

	//private static String textfieldlabel = "textfieldlabel";
	//private static String textarealabel = "textarealabel";
	//private static String comboboxlabel = "comboboxlabel";
	//private static String datepickerlabel = "datepickerlabel";
	//private static String radiolabel = "radiolabel";
	//private static String checkboxlabel = "checkboxlabel";
	//private static String autocompletelabel = "autocompletelabel";

	private static String nameattr = "'name':{'size':10, 'maxLength':6, 'type':'textfieldlabel', 'readonly':'true'}";
	private static String bankattr = "'bankName':{'size':10, 'maxLength':6, 'type':'textfieldlabel'}";
	private static String accountnumberattr = "'accountNumber':{'size':10, 'maxLength':6, 'type':'textfieldlabel'}";
	// private static String customerNameattr =
	// "'customer.name':{'size':10, 'maxLength':5, 'type':'textfieldlabel'}";
	// private static String formCustomerNameattr =
	// "'form.customer.name':{'size':10, 'maxLength':4, 'type':'textfieldlabel'}";
	// private static String customerFormCustomerNameattr =
	// "'customer.form.customer.name':{'size':10, 'maxLength':3, 'type':'textfieldlabel'}";

	private static String ageattr = "'age':{'size':10, 'maxLength':6, 'type':'textfieldlabel', 'readonly':'false', 'validate':'required between_5_10'}";
	private static String commentattr = "'comment':{'cols':20, 'rows':3, 'type':'textarealabel', 'readonly':'false', 'validate':'required maxlength_10 minlength_3'}";
	private static String agamaattr = "'agama':{'type':'comboboxlabel', 'ajaxsrc':'/PaggingTest/listagama', 'validate':'required'}";
	private static String transdateattr = "'transDate':{'type':'datepickerlabel'}";
	private static String activeattr = "'active':{'type':'radiolabel', 'ajaxsrc':'/PaggingTest/liststatus', 'readonly':'false'}";
	private static String musicattr = "'music':{'type':'checkboxlabel', 'ajaxsrc':'/PaggingTest/listmusic', 'validate':'required'}";
	private static String kelaminattr = "'sex':{'type':'radiolabel', 'ajaxsrc':'/PaggingTest/listkelamin', 'validate':'required'}";
	private static String profesiattr = "'profesi':{'type':'autocompletelabel', 'ajaxsrc':'/PaggingTest/listprofesi', 'validate':'required profesi'}";

	// public static void metadata() {
	// renderJSON(toJson("{"+activeattr+"}"));
	// }

	public static void metadata() {
		renderJSON(toJson("{" + nameattr + "," + ageattr + "," + commentattr + "," + agamaattr + "," + transdateattr + "," + activeattr + "," + musicattr + "," + kelaminattr + "," + profesiattr + "," + bankattr + "," + accountnumberattr + "}"));
	}

	public static String toJson(String tag) {
		return tag.replace("\'", "\"");
	}
}
