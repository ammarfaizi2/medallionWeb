package controllers;

import helpers.UIConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.mvc.Before;
import play.mvc.With;
import vo.BpjsMonthlyProcessParameter;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.GnBpjsDetail;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class BpjsMonthlyProcess extends MedallionController {
	public static Logger log = Logger.getLogger(BpjsMonthlyProcess.class);

	@Before(only = { "list", "dedupe" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> customers = new ArrayList<SelectItem>();
		for (GnBpjsDetail gbd : bpjsService.getBpjs().getBpjsDetails()) {
			if (gbd.getCustomer() != null) {
				customers.add(new SelectItem(gbd.getCustomer().getCustomerKey(), gbd.getCustomer().getCustomerNo()));
			}
		}
		renderArgs.put("customers", customers);

		renderArgs.put("obligasi", LookupConstants.SECURITY_CLASS_FIXED_INCOME);
		renderArgs.put("saham", LookupConstants.SECURITY_CLASS_EQUITY);

		List<SelectItem> classification = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION);
		renderArgs.put("classification", classification);
	}

	@Check("bpjs.monthlyprocess")
	public static void list(BpjsMonthlyProcessParameter param) {
		log.debug("list. param: " + param);

		if (param == null) {
			param = new BpjsMonthlyProcessParameter();
			param.setFilterAll1(BpjsMonthlyProcessParameter.FILTER_ALL);
			param.setFilterAll2(BpjsMonthlyProcessParameter.FILTER_ALL);
			param.setFilterAll3(BpjsMonthlyProcessParameter.FILTER_ALL);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BPJS_MONTHLY_PROCESS));
		render("BpjsMonthlyProcess/list.html", param);
	}

	public static void entry(String mode) {
		log.debug("entry. mode: " + mode);
	}

	public static void edit(String mode) {
		log.debug("edit. mode: " + mode);
	}

	public static void view(String mode) {
		log.debug("view. mode: " + mode);
	}

	@Check("bpjs.monthlyprocess")
	public static void process(BpjsMonthlyProcessParameter param) {
		log.debug("process. param: " + param);

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String month = param.getMonth();
			String year = param.getYear();
			String customer = (!param.getFilterAll1().isEmpty()) ? null : param.getFilterSpecific1();
			String jenisEfek = (!param.getFilterAll2().isEmpty()) ? null : param.getFilterSpecific2();
			String classification = (!param.getFilterAll3().isEmpty()) ? null : param.getFilterSpecific3();

			log.debug("month => " + month + ", year => " + year + ", customer => " + customer + ", jenisEfek => " + jenisEfek + ", classification => " + classification);

			bpjsService.bpjsMonthlyProcess(month, year, customer, jenisEfek, classification);
			result.put("messageSuccess", "Process Successfully.");
		} catch (Exception e) {
			result.put("messageError", e.getMessage());
		}

		renderJSON(result);
	}

}