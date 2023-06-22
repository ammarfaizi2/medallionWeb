package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.With;
import vo.SwiftUpdateParam;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.IntSwift;
import com.simian.medallion.model.IntSwiftUI;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class SwiftUpdate extends MedallionController {
	public static Logger log = Logger.getLogger(SwiftUpdate.class);
	
	//ini hanya sebagai method dummy
	//Transaction Number = 101554
	//Transaction Key = 21018
	//http://localhost:9000/swiftupdate/generateSwift?transactionKey=21064
	public static void generateSwift(Long transactionKey) {
		log.debug("generateSwift transactionKey="+transactionKey);
		
		try{
			String userId = session.get(UIConstants.SESSION_USERNAME);
			IntSwift bean = swiftService.generateSwift(transactionKey, userId);
			System.out.println(bean.getSwiftType().getLookupId());
			System.out.println(bean.getMessage());
			//bean = swiftService.saveGeneratedSwift(bean);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	//http://localhost:9000/swiftupdate/generateSettlementBatchScheduler
	public static void generateSettlementBatchScheduler() {
		log.debug("generateSettlementBatchScheduler");
		
		try{
			accountService.settlementBatchSecheduler();
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Check("swift.update")
	public static void list(SwiftUpdateParam param) {
		log.debug("list param="+param);
		if (param == null) {
			param = new SwiftUpdateParam();
			param.setStatus("IN");
		}
		
		renderArgs.put("messageModeOptions", swiftService.getLookupOptions("SWIFT_MSG_MODE"));
		renderArgs.put("statusOptions", swiftService.getLookupOptions("SWIFT_STATUS"));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SW_UPDATE));
		
		render("SwiftUpdate/list.html", param);
	}
	
	@Check("swift.update")
	public static void paging(Paging page, SwiftUpdateParam param) {
		log.debug("paging page="+page+", param="+param);
		
		log.debug("Page "+page.getsEcho());
		log.debug("Page "+page.getiDisplayStart());
		log.debug("Page "+page.getiDisplayLength());
		
		log.debug("param.getMessageMode "+param.getMessageMode());
		log.debug("param.getFromDate "+param.getFromDate());
		log.debug("param.getToDate "+param.getToDate());
		log.debug("param.getSettleFrom "+param.getSettleFrom());
		log.debug("param.getSettleTo "+param.getSettleTo());
		log.debug("param.getFromDate "+param.getFromDate());
		log.debug("param.getToDate "+param.getToDate());
		log.debug("param.getSender "+param.getSender());
		log.debug("param.getReceiver "+param.getReceiver());
		log.debug("param.getSecurityType "+param.getSecurityType());
		log.debug("param.getSecurityCode "+param.getSecurityCode());
		log.debug("param.getIsinCode "+param.getIsinCode());
		log.debug("param.getStatus "+param.getStatus());
		
		log.debug("getsSearch "+page.getsSearch());
		log.debug("getsiSortCol "+page.getiSortCol_0());
		log.debug("getsiSortDir "+page.getsSortDir_0());
		log.debug("getSortName "+page.getSortName());		
		
		if (param.isQuery()) {
			page.addParams("a.messageMode", Paging.EQUAL, param.getMessageMode());
			page.addParams("a.transactionDate", Paging.GREATEQUAL, param.getFromDate());
			page.addParams("a.transactionDate", Paging.LESSEQUAL, param.getToDate());
			page.addParams("a.SETTLEMENT_DATE", Paging.GREATEQUAL, param.getSettleFrom());
			page.addParams("a.SETTLEMENT_DATE", Paging.LESSEQUAL, param.getSettleTo());
			page.addParams("a.SENDER_CODE", Paging.EQUAL, param.getSender());
			page.addParams("a.RECEIVER_CODE", Paging.EQUAL, param.getReceiver());
			page.addParams("a.SECURITY_TYPE", Paging.EQUAL, param.getSecurityType());
			page.addParams("a.SECURITY_CODE", Paging.EQUAL, param.getSecurityCode());
			page.addParams("a.ISINCODE", Paging.EQUAL, param.getIsinCode());
			page.addParams("a.swiftNo", Paging.EQUAL, param.getSwiftNo());
			page.addParams("a.STATUS", Paging.EQUAL, param.getStatus());
			page.addParams("(a.messageMode||'^'||"
						 + "to_char(a.transactionDate, 'DD/MM/YYYY')||'^'||"
						 + "a.swiftNo||'^'||"
						 + "a.TRANSACTION_NO||'^'||"
						 //+ "p.subAccount.account.accountNo||'^'||" // koordinasi kalo datanya depositoAccount maka tidak bisa di jadikan filter
						 //+ "p.subAccount.account.name||'^'||"
						 + "a.SWIFT_TYPE||'^'||"
						 + "a.SWIFTTYPEDESC||'^'||"
						 //+ "p.transactionTemplate.description||'^'||"
						 + "a.ISINCODE||'^'||"
						 + "a.DESCSTATUS||'^'||"
						 + "a.resp_status||'^'||"
						 + "a.ACK_STATUS||'^'||"
						 + "a.settlementAmount||'^'||"
						 + "a.filename)", Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
			
			page = swiftService.pagingSwift(page);
		}
		renderJSON(page);
	}
	
	@Check("swift.update")
	public static void edit(Long id) {
		log.debug("edit id="+id);
		
		try{
			String mode = UIConstants.DISPLAY_MODE_EDIT;
			IntSwift bean = swiftService.getSwift(id);
			IntSwiftUI ui = bean.getSwiftUI();
			ui.print();
			ui.swiftKey = bean.getSwiftKey();
			if (!IntSwift.SWIFT_STATUS_INCOMPLETE.equals(bean.getStatus())) {
				mode = UIConstants.DISPLAY_MODE_VIEW;
			}
			
			ui = addRequestOptions(ui);
			
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SW_UPDATE));
			render("SwiftUpdate/detail.html", ui, mode);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Check("swift.update")
	public static void entry() {
		log.debug("entry");
		
		try{
			String mode = UIConstants.DISPLAY_MODE_ENTRY;
			IntSwiftUI ui = new IntSwiftUI();
			
			ui = addRequestOptions(ui);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SW_UPDATE));
			render("SwiftUpdate/detail.html", ui, mode);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Check("swift.update")
	public static void save(IntSwiftUI ui, String mode, String status) {
		log.debug("save ui="+ui+", mode="+mode+", status="+status);
				
		if (ui != null){
			// ini terjaid bila ada double mestinya balik lagi tidak error page
			// untuk fix bila terjadi error saat validasi, tetap tampil ke ui
			String transactionError = null;
			try {
				transactionError =  swiftService.validateSwiftTransaction(ui);
			}catch(Exception e) { 
				transactionError = e.getMessage();
				if (transactionError == null) { transactionError = "Error when trying to save"; }
			}
			
			if (Helper.isNull(transactionError)) {
				Long id = ui.swiftKey;
				serializerService.serialize(session.getId(), id, ui);
				confirming(id, mode, status);
			} else {
				String[] errors = transactionError.split("\\|"); //untuk fix pemisah pesan lewat | krn ada pesan yang ada , di tengahnya
				for (String error : errors) { validation.addError("", error); }
				ui = addRequestOptions(ui);
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SW_UPDATE));				
				render("SwiftUpdate/detail.html", ui, mode, status);
			}
		} else {
			entry();
		}
	}	
	
	@Check("swift.update")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming id="+id+", mode="+mode+", status="+status); 
		
		renderArgs.put("confirming", true);
		IntSwiftUI ui = serializerService.deserialize(session.getId(), id, IntSwiftUI.class);
		
		ui = addRequestOptions(ui);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SW_UPDATE));
		render("SwiftUpdate/detail.html", ui, mode, status);		
	}
	
	@Check("swift.update")
	public static void confirm(IntSwiftUI ui, String mode, String status) {
		try {
			if (ui == null) {
				back(null, mode, status);
			}
//			simulasi success
//			IntSwift bean = new IntSwift();
//			bean.setStatus(IntSwift.SWIFT_STATUS_COMPLETE);
//			bean.setTrasactionNo("123456");
			
//			simulasi fail			
//			IntSwift bean = new IntSwift();
//			bean.setStatus(IntSwift.SWIFT_STATUS_INCOMPLETE);
//			bean.setErrorDescription("Sengaja di errorin");
		
			IntSwift bean = swiftService.saveSwift(MenuConstants.SW_UPDATE, ui, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			boolean confirming = true;
			
			ui = addRequestOptions(ui);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SW_UPDATE));
			render("SwiftUpdate/detail.html", ui, mode, confirming, status, bean);
		}catch (Exception e) {
			flash.error("Swift No : ' "+ ui.swiftKey+" ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			
			ui = addRequestOptions(ui);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SW_UPDATE));
			render("SwiftUpdate/detail.html", ui, mode, confirming, status);
		} 
	}
	
	@Check("swift.update")
	public static void back(Long id, String mode, String status) {
		log.debug("back id="+id+", mode="+mode+", status="+status);
		IntSwiftUI ui = serializerService.deserialize(session.getId(), id, IntSwiftUI.class);
		
		ui = addRequestOptions(ui);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SW_UPDATE));
		render("SwiftUpdate/detail.html", ui, mode, status);
	}
	
	private static IntSwiftUI addRequestOptions(IntSwiftUI ui) {
		if (ui.swiftKey != null) {
			try {
				IntSwift bean = swiftService.getSwift(ui.swiftKey);
				
				ui.chk = bean.getChk();
				ui.mac = bean.getMac();
				ui.errors = bean.getErrorDescription();
				ui.ackStatus = bean.getAckStatus();
				ui.responseStatus = IntSwift.getResponseStatus(bean.getResponseStatus());
				
			}catch(Exception e) {}
		}
		
		//messageMode
		renderArgs.put("messageModeOptions", swiftService.getLookupOptions("SWIFT_MSG_MODE"));
		
		//messageTypeOptions
		//renderArgs.put("messageTypeOptions", swiftService.getLookupOptions("SWIFT_MSG_TYPE"));
		renderArgs.put("messageTypeOptions", swiftService.getLookupOptions("SWIFT_TYPE"));
		
		//messagePriority
		renderArgs.put("messagePriorityOptions", swiftService.getLookupOptions("SWIFT_MSG_PRIORITY"));
		
		//nonDeliveryNotificationPeriodeOptions
		renderArgs.put("nonDiliveryNotificationPeriod1Options", swiftService.getLookupOptions("SWIFT_DELIVERY_MONITORING"));
		
		//nonDeliveryNotificationPeriodeOptions
		renderArgs.put("nonDiliveryNotificationPeriod2Options", swiftService.getLookupOptions("SWIFT_NON_DELIVERY_NOTIFICATION_PERIOD"));
		
		//messageFunction
		renderArgs.put("messageFunctionOptions", swiftService.getLookupOptions("SWIFT_MSG_FUNCTION"));
		
		//accountTypeOptions
		renderArgs.put("accountTypeOptions", swiftService.getLookupOptions("SWIFT_ACCOUNT_TYPES"));
		
		//settlementIndicatorOptions
		renderArgs.put("settlementIndicatorOptions", swiftService.getLookupOptions("SWIFT_SETTLEMENT_INDICATOR"));
		
		//paymentIndicatorOptions
		renderArgs.put("paymentIndicatorOptions", swiftService.getLookupOptions("SWIFT_PAYMENT_INDICATOR"));
		
		ui = swiftService.populateSwift(ui);
		
		//receivingAgentOptions && deliveringAgentOptions
		if ("BIC".equals(ui.field95ToggleA)) {
			List<SelectItem> reciviengAgent = new ArrayList<SelectItem>();
			reciviengAgent.add(new SelectItem(ui.field95PReagBic, ui.field95PReagBic));		
			renderArgs.put("receivingAgentBICOptions", reciviengAgent);
//			ui.sett2BicReceivingAgent = "sett2BicReceivingAgent";
			
			List<SelectItem> deliveringAgent = new ArrayList<SelectItem>();
			deliveringAgent.add(new SelectItem(ui.field95PDeagBic, ui.field95PDeagBic));
			renderArgs.put("deliveringAgentBICOptions", deliveringAgent);
//			ui.sett2BicDeliveringAgent = "sett2BicDeliveringAgent";

			renderArgs.put("receivingAgentDSCOptions", new ArrayList<SelectItem>());
			renderArgs.put("deliveringAgentDSCOptions", new ArrayList<SelectItem>());
		}else{
			List<SelectItem> reciviengAgent = new ArrayList<SelectItem>();
			reciviengAgent.add(new SelectItem(ui.field95RReagDataSourceSchemeMC, ui.field95RReagDataSourceSchemeMC));		
			renderArgs.put("receivingAgentDSCOptions", reciviengAgent);
//			ui.sett2DssReceivingAgent = "sett2DssReceivingAgent";
			
			List<SelectItem> deliveringAgent = new ArrayList<SelectItem>();
			deliveringAgent.add(new SelectItem(ui.field95RDeagDataSourceSchemeMC, ui.field95RDeagDataSourceSchemeMC));
			renderArgs.put("deliveringAgentDSCOptions", deliveringAgent);
//			ui.sett2DssDeliveringAgent = "sett2DssDeliveringAgent";
			
			renderArgs.put("receivingAgentBICOptions", new ArrayList<SelectItem>());
			renderArgs.put("deliveringAgentBICOptions", new ArrayList<SelectItem>());
		}		
		
		//receivingCustodianOptions && deliveringCustodianOptions
		if ("BIC".equals(ui.field95ToggleB)) {
			List<SelectItem> reciviengCustodian = new ArrayList<SelectItem>();
			reciviengCustodian.add(new SelectItem(ui.field95PRecuBic, ui.field95PRecuBic));		
			renderArgs.put("receivingCustodianBICOptions", reciviengCustodian);
//			ui.sett3BicReceivingCustodian = "sett3BicReceivingCustodian";
			
			List<SelectItem> deliveringCustodian = new ArrayList<SelectItem>();
			deliveringCustodian.add(new SelectItem(ui.field95PDecuBic, ui.field95PDecuBic));
			renderArgs.put("deliveringCustodianBICOptions", deliveringCustodian);
//			ui.sett3BicDeliveringCustodian = "sett3BicDeliveringCustodian";

			renderArgs.put("receivingCustodianDSCOptions", new ArrayList<SelectItem>());
			renderArgs.put("deliveringCustodianDSCOptions", new ArrayList<SelectItem>());
		}else{
			List<SelectItem> reciviengCustodian = new ArrayList<SelectItem>();
			reciviengCustodian.add(new SelectItem(ui.field95RRecuDataSourceSchemeMC, ui.field95RRecuDataSourceSchemeMC));		
			renderArgs.put("receivingCustodianDSCOptions", reciviengCustodian);
//			ui.sett3DssReceivingCustodian = "sett3DssReceivingCustodian"; 
			
			List<SelectItem> deliveringCustodian = new ArrayList<SelectItem>();
			deliveringCustodian.add(new SelectItem(ui.field95RDecuDataSourceSchemeMC, ui.field95RDecuDataSourceSchemeMC));
			renderArgs.put("deliveringCustodianDSCOptions", deliveringCustodian);
//			ui.sett3DssDeliveringCustodian = "sett3DssDeliveringCustodian";
			
			renderArgs.put("receivingCustodianBICOptions", new ArrayList<SelectItem>());
			renderArgs.put("deliveringCustodianBICOptions", new ArrayList<SelectItem>());
		}
		
		//settlementDate format
		List<SelectItem> settlementDateFormat = new ArrayList<SelectItem>();
		settlementDateFormat.add(new SelectItem("YYYYMMDD", "YYYYMMDD"));
		settlementDateFormat.add(new SelectItem("YYYYMMDDHHMMSS", "YYYYMMDDHHMMSS"));
		renderArgs.put("settlementDateFormatOptions", settlementDateFormat);
		
		if (Helper.isEmpty(ui.fieldSettDateFormat)) {
			ui.fieldSettDateFormat = Helper.isEmpty(ui.field98ASettDate) ? "YYYYMMDDHHMMSS" : "YYYYMMDD";
		}
		
		//receivingCustodianOptions
		List<SelectItem> reciviengCustodioan = new ArrayList<SelectItem>();
		reciviengCustodioan.add(new SelectItem(ui.field95PRecuBic, ui.field95PRecuBic));		
		renderArgs.put("receivingCustodianOptions", reciviengCustodioan);
		
		//deliveringCustodianOptions
		List<SelectItem> deliveringCustodioan = new ArrayList<SelectItem>();
		deliveringCustodioan.add(new SelectItem(ui.field95PDecuBic, ui.field95PDecuBic));
		renderArgs.put("deliveringCustodianOptions", deliveringCustodioan);
		
		//placeOfSettlementOptions
		renderArgs.put("placeOfSettlementOptions", swiftService.getLookupOptions("SWIFT_PLACE_OF_SETTLEMENT"));
		
		//settlementOptions
		renderArgs.put("settlementOptions", swiftService.getLookupOptions("SWIFT_SETTELEMENT_OPTIONS"));
		
		//messageFunction
		List<SelectItem> isinSecurityOptions = new ArrayList<SelectItem>();
		isinSecurityOptions.add(new SelectItem("ISIN", "ISIN"));
		isinSecurityOptions.add(new SelectItem("/XX/", "Security"));
		renderArgs.put("isinSecurityOptions", isinSecurityOptions);
		
		//systemStatus
		renderArgs.put("statusOptions", swiftService.getLookupOptions("SWIFT_STATUS"));
		
		return ui;
	}
}
