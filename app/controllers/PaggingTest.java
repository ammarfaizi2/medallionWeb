package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simian.medallion.model.CpComplianceProfile;
import com.simian.medallion.model.JsonAction;
import com.simian.medallion.model.JsonBank;
import com.simian.medallion.model.JsonForm;
import com.simian.medallion.model.JsonProp;
import com.simian.medallion.model.JsonTest;
import com.simian.medallion.model.ModelForm;

public class PaggingTest extends MedallionController {

	public static void metadataform() {
		ModelForm modelForm = new ModelForm("customer", ModelForm.TYPE_FORM, ModelForm.TYPEVIEW_PAGE);
		modelForm.setModelName("customer");
		modelForm.setModel(getModel());
		modelForm.setMetadata(Metadata.toJson("{'datalist':{'type':'table', 'ajaxsrc':'/PaggingTest/cprofilelist', 'colum':[{'sName':'complianceProfCode', 'sTitle':'key', 'sWidth':'20%','bSortable':'true'}, {'sName':'complianceProfCode', 'sTitle':'Code', 'sWidth':'20%','bSortable':'true'}, {'sName':'desc', 'sTitle':'Description', 'sWidth':'20%','bSortable':'true'}]}, 'cancel':{'readonly':'true', 'type':'button', 'caption':'Batal', 'ajaxsubmit':'/PaggingTest/canceldata'}, 'open':{'type':'button', 'caption':'Open'}, 'save':{'type':'button', 'caption':'Simpan', 'ajaxsubmit':'/PaggingTest/savedata'},'customer.name':{'size':5, 'maxLength':5, 'type':'textfieldlabel'},'name':{'size':20, 'maxLength':10, 'type':'textfieldlabel', 'validate':'required'}}"));
		modelForm.setInject(Metadata.toJson("{'save':{}, 'cancel':{}, 'open':{}, 'datalist':{}}"));

		// global
		// 1.customer.form.customer.name
		// 2.form.customer.name
		// 3.customer.name
		// 4.name

		// modelForm.i("name", Metadata.label.i("value", "Name").i("afor",
		// "textfield.name"));
		// modelForm.i("name", Metadata.textfield.i("length", "10"));
		renderJSON(modelForm);
	}

	public static void layoutform() {
		String layout2 = "{'class':'layout', 'type':'flexGrid', 'columns':3, 'elements':['save', 'cancel', 'open']}";
		String layout1 = "{'class':'layout','type':'flexGrid', 'hgap':0, 'vgap':0, 'columns':2, 'elements':['namelabel', 'name', 'agelabel', 'age', 'commentlabel', 'comment', 'agamalabel', 'agama', 'transDatelabel', 'transDate', 'activelabel', 'active', 'musiclabel', 'music', 'sexlabel', 'sex', 'profesilabel', 'profesi', 'bank_bankNamelabel', 'bank_bankName', 'bank_accountNumberlabel', 'bank_accountNumber', 'fillere', " + layout2 + "]}";
		String layout3 = "{'class':'layout', 'type':'flexGrid', 'columns':1, 'elements':['datalist']}";
		renderJSON(Metadata.toJson("[" + layout1 + ", 'fillerp', " + layout3 + "]"));
	}

	public static void layout() {
		render("PaggingTest/Layout.html");
	}

	public static void list() {
		render("PaggingTest/PaggingTest.html");
	}

	public static void list2() {
		render("PaggingTest/PaggingTest2.html");
	}

	public static void reallist() {
		render("PaggingTest/PaggingTestList.html");
	}

	public static void realentry() {
		render("PaggingTest/PaggingTestEntry.html");
	}

	public static void formdialog() {
		JsonTest entry = new JsonTest();

		JsonForm formdialog = new JsonForm(entry, JsonForm.NEW);
		JsonAction dlginput1 = new JsonAction("input", "/paggingtest/nickname", "Nickname");
		formdialog.getElements().add(dlginput1);
		JsonAction dlginput2 = new JsonAction("input", "/paggingtest/dieat", "Die At");
		formdialog.getElements().add(dlginput2);
		JsonAction datepick = new JsonAction("datepicker", "/paggingtest/transdate", "Trans Date");
		formdialog.getElements().add(datepick);
		JsonAction btnSaveDlg = new JsonAction("button", "/paggingtest/savedlg", "Simpan");
		formdialog.getElements().add(btnSaveDlg);
		JsonAction btnCancelDlg = new JsonAction("button", "/paggingtest/canceldlg", "Cancel");
		formdialog.getElements().add(btnCancelDlg);

		renderJSON(formdialog);
	}

	public static void formaccordion() {
		JsonTest entry = new JsonTest();

		JsonForm formdialog = new JsonForm(entry, JsonForm.NEW);
		JsonAction dlginput1 = new JsonAction("input", "/paggingtest/nickname", "Nickname");
		formdialog.getElements().add(dlginput1);
		JsonAction dlginput2 = new JsonAction("input", "/paggingtest/dieat", "Die At");
		formdialog.getElements().add(dlginput2);
		JsonAction btnSaveDlg = new JsonAction("button", "/paggingtest/savedlg", "Simpan");
		formdialog.getElements().add(btnSaveDlg);
		JsonAction btnCancelDlg = new JsonAction("button", "/paggingtest/canceldlg", "Cancel");
		formdialog.getElements().add(btnCancelDlg);

		renderJSON(formdialog);
	}

	public static JsonTest getModel() {
		JsonTest entry = new JsonTest();
		entry.setAge(new Integer(7));
		entry.setComment("1234567");
		entry.setJob("Programer");
		entry.setLastname("Tan");
		entry.setName("Elvino");
		entry.setAgama("katolik");
		entry.setCode("AAA");
		entry.setCodeDesc("AAAAUUUUUAAAAAHHH");
		entry.setActive(false);
		entry.setSex("pria");
		entry.setProfesi("tukangcukur");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		entry.setTransDate(cal.getTime());
		entry.setMusic(new String[] { "dangdut", "hiphop" });
		JsonBank bank = new JsonBank();
		bank.setBankName("Mandiri");
		bank.setAccountNumber("0101010101");
		entry.setBank(bank);
		return entry;
	}

	public static void form() {
		JsonTest entry = new JsonTest();
		entry.setAge(new Integer(30));
		entry.setComment("Hallo ini comment");
		entry.setJob("Programer");
		entry.setLastname("Tan");
		entry.setName("Elvino");
		// entry.setAgama("katolik");
		entry.setCode("AAA");
		entry.setCodeDesc("AAAAUUUUUAAAAAHHH");
		entry.setActive(true);
		entry.setSex("");
		entry.setMusic(new String[] { "Dangdut", "Rap" });

		JsonForm form = new JsonForm(entry, JsonForm.LIST);

		JsonAction lookup = new JsonAction("lookup", "/paggingtest/cprofiledlg", "This Title Dialog");
		lookup.getColum().add(new JsonProp("complianceProfCode", "key")); // ini
																			// key
																			// wajib
																			// no
																			// 0
		lookup.getColum().add(new JsonProp("description", "desc")); // ini desc
																	// wajib no
																	// 1
		lookup.getColum().add(new JsonProp("complianceProfCode", "Code"));
		lookup.getColum().add(new JsonProp("description", "Description"));
		form.getElements().add(lookup);

		JsonAction mytable = new JsonAction("table", "/paggingtest/cprofilelist");
		mytable.getColum().add(new JsonProp("complianceProfCode", "key"));
		mytable.getColum().add(new JsonProp("description", "desc"));
		mytable.getColum().add(new JsonProp("complianceProfCode", "Code"));
		mytable.getColum().add(new JsonProp("description", "Description"));
		mytable.getColum().add(new JsonProp("status", "Status"));
		mytable.getColum().add(new JsonProp("active", "Active"));
		form.getElements().add(mytable);

		JsonAction select = new JsonAction("select", "/paggingtest/agama", "Agama");
		select.getColum().add(new JsonProp("islam", "Islam"));
		select.getColum().add(new JsonProp("katolik", "Katolik"));
		select.getColum().add(new JsonProp("budha", "Budha"));
		select.getColum().add(new JsonProp("kristen", "Kristen"));
		select.getColum().add(new JsonProp("hindu", "Hindu"));
		select.getColum().add(new JsonProp("konghucu", "KongHucu"));
		form.getElements().add(select);

		JsonAction active = new JsonAction("radio", "/paggingtest/active", "Active");
		active.getColum().add(new JsonProp("true", "Active"));
		active.getColum().add(new JsonProp("false", "Non Active"));
		form.getElements().add(active);

		JsonAction sex = new JsonAction("radio", "/paggingtest/sex", "Jenis Kelamin");
		sex.getColum().add(new JsonProp("wanita", "Wanita"));
		sex.getColum().add(new JsonProp("pria", "Pria"));
		sex.getColum().add(new JsonProp("waria", "Waria"));
		sex.getColum().add(new JsonProp("unkown", "Gax jelas"));
		form.getElements().add(sex);

		JsonAction music = new JsonAction("checkbox", "/paggingtest/music", "Favorite music");
		music.getColum().add(new JsonProp("dangdut", "Dangdut"));
		music.getColum().add(new JsonProp("rock", "Rock"));
		music.getColum().add(new JsonProp("rap", "Rap"));
		music.getColum().add(new JsonProp("hiphop", "Hip Hop"));
		form.getElements().add(music);

		JsonAction autocomplete = new JsonAction("autocomplete", "/paggingtest/agamaautocomplete", "Agama");
		form.getElements().add(autocomplete);
		JsonAction input1 = new JsonAction("input", "/paggingtest/name", "Name");
		form.getElements().add(input1);
		JsonAction input2 = new JsonAction("input", "/paggingtest/age");
		form.getElements().add(input2);
		JsonAction input3 = new JsonAction("input", "/paggingtest/job", "Job");
		form.getElements().add(input3);
		JsonAction input4 = new JsonAction("input", "/paggingtest/lastname", "Last Name");
		form.getElements().add(input4);
		JsonAction dialog1 = new JsonAction("inputlookup", "/paggingtest/code", "Code", lookup);
		form.getElements().add(dialog1);
		JsonAction textarea = new JsonAction("textarea", "/paggingtest/comment", "Commment");
		form.getElements().add(textarea);
		JsonAction btnSave = new JsonAction("button", "/paggingtest/save", "Simpan");
		form.getElements().add(btnSave);
		JsonAction btnCancel = new JsonAction("button", "/paggingtest/cancel", "Cancel");
		form.getElements().add(btnCancel);
		JsonAction btnEntry = new JsonAction("button", "/paggingtest/entry", "Entry");
		form.getElements().add(btnEntry);

		renderJSON(form);
	}

	public static void code(String code) {
		CpComplianceProfile cp = generalService.getComplianceProfile(code);
		String desc = "Invalid code";
		if (cp != null)
			desc = cp.getDescription();
		renderJSON("\"" + desc + "\"");
	}

	public static void listmusic() {
		Object[][] music = new Object[4][2];
		music[0] = new Object[] { "dangdut", "Dangdut" };
		music[1] = new Object[] { "rock", "Rock" };
		music[2] = new Object[] { "rap", "Rap" };
		music[3] = new Object[] { "hiphop", "Hip Hop" };

		renderJSON(music);
	}

	public static void listkelamin() {
		Object[][] agama = new Object[4][2];
		agama[0] = new Object[] { "wanita", "Wanita" };
		agama[1] = new Object[] { "pria", "Pria" };
		agama[2] = new Object[] { "waria", "Waria" };
		agama[3] = new Object[] { "unkown", "Gax Jelas" };

		renderJSON(agama);
	}

	public static void listprofesi(String term) {
		Object[][] profesi = new Object[4][2];
		profesi[0] = new Object[] { "programer", "Programer Java" };
		profesi[1] = new Object[] { "suster", "Suster Rumah Sakit" };
		profesi[2] = new Object[] { "tukangcukur", "Tukang Cukur Rambut" };
		profesi[3] = new Object[] { "pedagang", "Dagang teh botol" };

		List<Object> objs = new ArrayList<Object>();
		for (Object[] objects : profesi) {
			if ("".equals(term)) {
				objs.add(objects);
			} else {
				if (objects[0].toString().startsWith(term)) {
					objs.add(objects);
				}
			}
		}

		renderJSON(objs);
	}

	public static void listagama() {
		Object[][] agama = new Object[6][2];
		agama[0] = new Object[] { "", "" };
		agama[1] = new Object[] { "islam", "Islam" };
		agama[2] = new Object[] { "katolik", "Katolik" };
		agama[3] = new Object[] { "budha", "Budha" };
		agama[4] = new Object[] { "hindu", "Hindu" };
		agama[5] = new Object[] { "konghucu", "Konghucu" };
		renderJSON(agama);
	}

	// public static void liststatus(){
	// Boolean[] status = new Boolean[]{new Boolean(true), new Boolean(false)};
	// renderJSON(status);
	// }

	public static void liststatus() {
		Object[][] status = new Object[2][2];
		status[0] = new Object[] { new Boolean(true), "Active" };
		status[1] = new Object[] { new Boolean(false), "Disactive" };
		renderJSON(status);
	}

	public static void agamaautocomplete(String term) {
		String[] agama = new String[] { "Islam", "Katolik", "Budha", "Hindu", "Konghucu" };
		List<String> filtered = new ArrayList<String>();
		for (String a : agama) {
			if (a.toLowerCase().indexOf(term) >= 0)
				filtered.add(a);
		}

		String[] newarr = new String[filtered.size()];
		newarr = filtered.toArray(newarr);

		renderJSON(newarr);
	}

	public static void savedata(JsonTest object) {

		String[] music = object.getMusic();
		for (String msk : music) {
		}

		String result = "{'title':'Error', 'content':['Terjadi error saving', 'Yakin isinya benar']}";
		renderJSON(Metadata.toJson(result));
	}

	public static void save(JsonTest test) {

		for (String music : test.getMusic()) {
		}
		renderJSON("\"Save success for " + test.getName() + "\"");
	}

	public static void cprofilelist() {
		for (String key : params.allSimple().keySet()) {
		}

		String iDisplayStart = params.get("iDisplayStart"); // start row number
		String iDisplayLength = params.get("iDisplayLength"); // jumlah yang di
																// harus di
																// ambil (jumlah
																// per page)
		//String sEcho = params.get("sEcho");
		//String iSortCol_0 = params.get("iSortCol_0");
		//String sSortDir_0 = params.get("sSortDir_0");
		String sSearch = params.get("sSearch");
		String iColNo = params.get("iSortCol_0");
		String iColOrd = params.get("sSortDir_0");
		String columname = params.get("sColumns").split(",")[Integer.valueOf(iColNo)];

		long totalRecord = generalService.complianceProfileCount();
		List<CpComplianceProfile> listProfile = generalService.getComplianceProfilePaging(Integer.valueOf(iDisplayStart), Integer.valueOf(iDisplayLength), sSearch, columname, iColOrd);
		long totalDisplayRec = generalService.getComplianceProfilePagingCount(sSearch);
		Object[][] aaData = toArray(listProfile);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("iTotalRecords", totalRecord); // ini adalah total row yang
													// ada di database
		result.put("iTotalDisplayRecords", totalDisplayRec); // ini adalah total
																// row yang ada
																// di database
																// setelah di
																// filter
		result.put("aaData", aaData);

		renderJSON(result);
	}

	public static void cprofiledlg() {
//		for (String key : params.allSimple().keySet()) {
//		}

		String iDisplayStart = params.get("iDisplayStart"); // start row number
		String iDisplayLength = params.get("iDisplayLength"); // jumlah yang di
																// harus di
																// ambil (jumlah
																// per page)
		//String sEcho = params.get("sEcho");
		//String iSortCol_0 = params.get("iSortCol_0");
		//String sSortDir_0 = params.get("sSortDir_0");
		String sSearch = params.get("sSearch");
		String iColNo = params.get("iSortCol_0");
		String iColOrd = params.get("sSortDir_0");
		String columname = params.get("sColumns").split(",")[Integer.valueOf(iColNo)];

		long totalRecord = generalService.complianceProfileCount();
		List<CpComplianceProfile> listProfile = generalService.getComplianceProfilePaging(Integer.valueOf(iDisplayStart), Integer.valueOf(iDisplayLength), sSearch, columname, iColOrd);
		long totalDisplayRec = generalService.getComplianceProfilePagingCount(sSearch);
		Object[][] aaData = toArray(listProfile);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("iTotalRecords", totalRecord); // ini adalah total row yang
													// ada di database
		result.put("iTotalDisplayRecords", totalDisplayRec); // ini adalah total
																// row yang ada
																// di database
																// setelah di
																// filter
		result.put("aaData", aaData);

		renderJSON(result);
	}

	private static Object[][] toArray(List<CpComplianceProfile> profiles) {
		Object[][] data = new Object[profiles.size()][6];
		for (int i = 0; i < profiles.size(); i++) {
			data[i][0] = profiles.get(i).getComplianceProfCode();
			data[i][1] = profiles.get(i).getDescription();
			data[i][2] = profiles.get(i).getComplianceProfCode();
			data[i][3] = profiles.get(i).getDescription();
			data[i][4] = "" + profiles.get(i).getStatus();
			data[i][5] = "" + profiles.get(i).getActive();
		}
		return data;
	}

	// This is the real deal
	public static void metadataformlist() {
		ModelForm modelForm = new ModelForm("list", ModelForm.TYPE_FORM, ModelForm.TYPEVIEW_PAGE);
		modelForm.setModelName("customer");
		modelForm.setMetadata(Metadata.toJson("{'dialog':{'caption':'Dialog', 'type':'button', 'ajaxsubmit':'/PaggingTest/dialogsubmit'}, 'new':{'caption':'New', 'type':'button', 'ajaxsubmit':'/PaggingTest/newsubmit'}, 'datalist':{'type':'table', 'ajaxsrc':'/PaggingTest/cprofilelist', 'colum':[{'sName':'complianceProfCode', 'sTitle':'key', 'sWidth':'20%','bSortable':'true'}, {'sName':'complianceProfCode', 'sTitle':'Code', 'sWidth':'20%','bSortable':'true'}, {'sName':'desc', 'sTitle':'Description', 'sWidth':'20%','bSortable':'true'}]}}"));
		modelForm.setInject(Metadata.toJson("{'dialog':{}, 'new':{}, 'datalist':{}}"));
		renderJSON(modelForm);
	}

	public static void layoutformlist() {
		String listbutton = "{'class':'layout', 'type':'flexGrid', 'columns':2, 'elements':['new', 'dialog']}";
		String tablelist = "{'class':'layout', 'type':'flexGrid', 'columns':1, 'elements':['datalist', " + listbutton + "]}";

		// String contentbutton =
		// "{'class':'layout', 'type':'flexGrid', 'columns':2, 'elements':['save', 'cancel']}";
		// String contententry =
		// "{'class':'layout','type':'flexGrid', 'hgap':0, 'vgap':0, 'columns':2, 'elements':['namelabel', 'name', 'agelabel', 'age', 'commentlabel', 'comment', 'agamalabel', 'agama', 'transDatelabel', 'transDate', 'activelabel', 'active', 'musiclabel', 'music', 'sexlabel', 'sex', 'profesilabel', 'profesi', 'bank_bankNamelabel', 'bank_bankName', 'bank_accountNumberlabel', 'bank_accountNumber', 'fillere', "+contentbutton+"]}";

		renderJSON(Metadata.toJson("[" + tablelist + "]"));

		// String layout2 =
		// "{'class':'layout', 'type':'flexGrid', 'columns':3, 'elements':['save', 'cancel', 'open']}";
		// String layout1 =
		// "{'class':'layout','type':'flexGrid', 'hgap':0, 'vgap':0, 'columns':2, 'elements':['namelabel', 'name', 'agelabel', 'age', 'commentlabel', 'comment', 'agamalabel', 'agama', 'transDatelabel', 'transDate', 'activelabel', 'active', 'musiclabel', 'music', 'sexlabel', 'sex', 'profesilabel', 'profesi', 'bank_bankNamelabel', 'bank_bankName', 'bank_accountNumberlabel', 'bank_accountNumber', 'fillere', "+layout2+"]}";
		// String layout3 =
		// "{'class':'layout', 'type':'flexGrid', 'columns':1, 'elements':['datalist']}";
		// renderJSON(Metadata.toJson("["+layout1+", 'fillerp', "+layout3+"]"));
	}

	public static void metadataformentry() {
		ModelForm modelForm = new ModelForm("customer", ModelForm.TYPE_FORM, ModelForm.TYPEVIEW_DIALOG);
		modelForm.setModelName("customer");
		modelForm.setModel(getModel());
		modelForm.setMetadata(Metadata.toJson("{'datalist':{'type':'table', 'ajaxsrc':'/PaggingTest/cprofilelist', 'colum':[{'sName':'complianceProfCode', 'sTitle':'key', 'sWidth':'20%','bSortable':'true'}, {'sName':'complianceProfCode', 'sTitle':'Code', 'sWidth':'20%','bSortable':'true'}, {'sName':'desc', 'sTitle':'Description', 'sWidth':'20%','bSortable':'true'}]}, 'closeDialog':{'type':'button', 'caption':'Close', 'ajaxsubmit':'/PaggingTest/closedialog'}, 'save':{'type':'button', 'caption':'Simpan', 'ajaxsubmit':'/PaggingTest/savedata'},'customer.name':{'size':5, 'maxLength':5, 'type':'textfieldlabel'},'name':{'size':20, 'maxLength':10, 'type':'textfieldlabel', 'validate':'required'}}"));
		modelForm.setInject(Metadata.toJson("{'save':{}, 'closeDialog':{}, 'datalist':{}}"));
		renderJSON(modelForm);
	}

	public static void layoutformentry() {
		String layout2 = "{'class':'layout', 'type':'flexGrid', 'columns':3, 'elements':['save', 'closeDialog']}";
		String layout1 = "{'class':'layout','type':'flexGrid', 'hgap':0, 'vgap':0, 'columns':2, 'elements':['namelabel', 'name', 'agelabel', 'age', 'commentlabel', 'comment', 'agamalabel', 'agama', 'transDatelabel', 'transDate', 'activelabel', 'active', 'musiclabel', 'music', 'sexlabel', 'sex', 'profesilabel', 'profesi', 'bank_bankNamelabel', 'bank_bankName', 'bank_accountNumberlabel', 'bank_accountNumber', 'fillere', " + layout2 + "]}";
		renderJSON(Metadata.toJson("[" + layout1 + "]"));
	}
}
