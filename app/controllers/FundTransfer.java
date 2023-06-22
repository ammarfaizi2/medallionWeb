package controllers;

import helpers.UIConstants;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.AbstractRgPayment;
import com.simian.medallion.model.BnFundtransfer;
import com.simian.medallion.model.BnFundtransferDetail;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;
import com.simian.medallion.vo.SettlementFundTransferVO;
import com.simian.medallion.vo.FundTransferParam;

@With(Secure.class)
public class FundTransfer extends MedallionController{
	public static Logger logger = Logger.getLogger(FundTransfer.class);
	
	public static final String TRADETYPEONSCREEN_SUBSCRIPTION = "Subscription";
	
	@Check("settlement.fundtransfer")
	public static void list() {
		// Default for Settlement Ksei
		Date applicationDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("applicationDate", Helper.formatDMY(applicationDate));
		
		
		List<SelectItem> transactionType = new ArrayList<SelectItem>();
		transactionType.add(new SelectItem("DVP", "DVP"));
		transactionType.add(new SelectItem("RVP", "RVP"));		
		renderArgs.put("transactionType", transactionType);
		
		GnSystemParameter gnsysKseiDigit=generalService.getSystemParameter(SystemParamConstants.INTERFACE_KSEI_DIGIT);
		
		renderArgs.put("digitKsei", gnsysKseiDigit== null?0:Integer.parseInt(gnsysKseiDigit.getValue()));
		
		List<SelectItem> transferType1 = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.TRANSFER_TYPE);
		List<SelectItem> transferType = new ArrayList<SelectItem>();
		transferType.add(new SelectItem("ALL", "ALL"));
		transferType.addAll(transferType1);
		renderArgs.put("transferType", transferType);
		
		
		List<SelectItem> listType = new ArrayList<SelectItem>();
		listType.add(new SelectItem("ALL", "ALL"));
		listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_REDEMPTION, AbstractRgPayment.BY_TYPE_REDEMPTION.toUpperCase()));
		listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_SWITCH_OUT, AbstractRgPayment.BY_TYPE_SWITCH_OUT.toUpperCase()));
		listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_DIV_BY_CASH, AbstractRgPayment.BY_TYPE_DIV_BY_CASH.toUpperCase()));
		renderArgs.put("listType", listType);
		
		render("FundTransfer/list.html", param);
	}
	
	@Check("settlement.fundtransfer")
	public static void entry(String mode) {}
	
	@Check("settlement.fundtransfer")
	public static void edit(String mode) {}
	
	@Check("settlement.fundtransfer")
	public static void view(String mode) {}
	
	/* 		SETTLEMENT KSEI 		*/
	public static void getSettlementKSEIPopulate(FundTransferParam param) throws ParseException {
		logger.debug("getSettlementKSEIPopulate param="+param);

		if (param.getAsOfFlag().booleanValue()) {
			param.setAsOfDate(Helper.parseDate(param.getStrAsOfDate(), appProp.getDateFormat()));
		}else{
			param.setFromDate(Helper.parseDate(param.getStrFromDate(), appProp.getDateFormat()));
			param.setToDate(Helper.parseDate(param.getStrToDate(), appProp.getDateFormat()));
		}

		logger.debug("getSettlementKSEIPopulate param="+param);
		
		List<SettlementFundTransferVO> data = fundService.getSettlementKSEIPopulate(param);
		renderJSON(data);
	}
	
	public static void getSettlementBIPopulate(FundTransferParam param) throws ParseException {
		logger.debug("getSettlementBIPopulate param="+param);

		if (param.getAsOfFlag().booleanValue()) {
			param.setAsOfDate(Helper.parseDate(param.getStrAsOfDate(), appProp.getDateFormat()));
		}else{
			param.setFromDate(Helper.parseDate(param.getStrFromDate(), appProp.getDateFormat()));
			param.setToDate(Helper.parseDate(param.getStrToDate(), appProp.getDateFormat()));
		}

		logger.debug("getSettlementKSEIPopulate param="+param);
		
		List<SettlementFundTransferVO> data = fundService.getSettlementBIPopulate(param);
		renderJSON(data);
	}
	
	
	public static void getDepositoPlacementPopulate(FundTransferParam param) throws ParseException {
		logger.debug("getDepositoPlacementPopulate param="+param);
		
		if (param.getAsOfFlag().booleanValue()) {
			param.setAsOfDate(Helper.parseDate(param.getStrAsOfDate(), appProp.getDateFormat()));
		}else{
			param.setFromDate(Helper.parseDate(param.getStrFromDate(), appProp.getDateFormat()));
			param.setToDate(Helper.parseDate(param.getStrToDate(), appProp.getDateFormat()));
		}
		
		logger.debug("getDepositoPlacementPopulate param="+param);
		
		List<SettlementFundTransferVO> data = fundService.getDepositoPlacementPopulate(param);
		renderJSON(data);
	}
	
	public static void getCASettlementPopulate(FundTransferParam param) throws ParseException {
		logger.debug("getCASettlementPopulate param="+param);
		
		if (param.getAsOfFlag().booleanValue()) {
			param.setAsOfDate(Helper.parseDate(param.getStrAsOfDate(), appProp.getDateFormat()));
		}else{
			param.setFromDate(Helper.parseDate(param.getStrFromDate(), appProp.getDateFormat()));
			param.setToDate(Helper.parseDate(param.getStrToDate(), appProp.getDateFormat()));
		}
		
		logger.debug("getCASettlementPopulate param="+param);
		
		List<SettlementFundTransferVO> data = fundService.getCASettlementPopulate(param);
		renderJSON(data);
	}
	
	public static void getRgPaymentPopulate(FundTransferParam param) throws ParseException {
		logger.debug("getRgPaymentPopulate param="+param);

		if (param.getAsOfFlag().booleanValue()) {
			param.setAsOfDate(Helper.parseDate(param.getStrAsOfDate(), appProp.getDateFormat()));
		}else{
			param.setFromDate(Helper.parseDate(param.getStrFromDate(), appProp.getDateFormat()));
			param.setToDate(Helper.parseDate(param.getStrToDate(), appProp.getDateFormat()));
		}

		logger.debug("getSettlementKSEIPopulate param="+param);
		
		List<SettlementFundTransferVO> data = fundService.getRgPaymentPopulate2(param);
		renderJSON(data);
	}
	
	public static void saveFundTransfer( BnFundtransferDetail[] paramDetail, FundTransferParam paramScreen, String processType) {
		logger.debug("saveFundTransfer detail="+paramDetail.length);
		Map<String, Object> map = new HashMap<String, Object>();
		try{
	
			Long batchId = fundService.saveFundTransfer(
												paramDetail, 
												session.get(UIConstants.SESSION_USERNAME), 
												new GnLookup(processType), 
												paramScreen,
												MenuConstants.FUND_TRANSFER,
												session.get(UIConstants.SESSION_USER_KEY));
			
			map.put("status", "SUCCESS");
			map.put("batchId", batchId);
			if (LookupConstants._FUNDTRANSFER_DEPOSITO.equals(processType)) { map.put("msg", "Success creating batch deposito placement, with Batch No : "+batchId); }
			if (LookupConstants._FUNDTRANSFER_SETTLEMENT_KSEI.equals(processType)) { map.put("msg", "Success creating Settlement KSEI Batch, with Batch No : "+batchId); }
			if (LookupConstants._FUNDTRANSFER_SETTLEMENT_BI.equals(processType)) { map.put("msg", "Success creating Settlement BI Batch, with Batch No : "+batchId); }			
			if (LookupConstants._FUNDTRANSFER_CA_SETTLEMENT.equals(processType)) { map.put("msg", "Success creating Settlement CA Batch, with Batch No : "+batchId); }			
			if (LookupConstants._FUNDTRANSFER_RG_PAYMENT.equals(processType)) { map.put("msg", "Success creating Settlement RG Payment Batch, with Batch No : "+batchId); }
		}catch(Exception e) {
			map.put("status", "FAIL");
			if (LookupConstants._FUNDTRANSFER_SETTLEMENT_KSEI.equals(processType)) { map.put("msg", "Fail creating Settlement KSEI Batch"); }
		}
		
		renderJSON(map);
	}
	
	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		try {
			String mode = UIConstants.DISPLAY_MODE_CONFIRM;
			String operationCreate = LookupConstants.MAINTENANCE_OPERATION_CREATE;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			//System.out.println("======================"+maintenanceLog.getEntityKey());
			GnSystemParameter gnsysKseiDigit=generalService.getSystemParameter(SystemParamConstants.INTERFACE_KSEI_DIGIT);
			//GnSystemParameter gnsysKseiDigitMethod=generalService.getSystemParameter(SystemParamConstants.INTERFACE_KSEI_DIGIT_METHOD);
			
			GnSystemParameter gnsysBiDigit=generalService.getSystemParameter(SystemParamConstants.INTERFACE_BI_DIGIT);
			//GnSystemParameter gnsysBiDigitMethod=generalService.getSystemParameter(SystemParamConstants.INTERFACE_BI_DIGIT_METHOD);
			
			GnSystemParameter gnsysTwsDigit = generalService.getSystemParameter(SystemParamConstants.INTERFACE_TWS_DIGIT);
			//GnSystemParameter gnsysTwsDigitMethod = generalService.getSystemParameter(SystemParamConstants.INTERFACE_TWS_DIGIT_METHOD);
			
			GnSystemParameter gnsysRedDigit = generalService.getSystemParameter(SystemParamConstants.INTERFACE_RED_DIGIT);
			//GnSystemParameter gnsysRedDigitMethod = generalService.getSystemParameter(SystemParamConstants.INTERFACE_RED_DIGIT_METHOD);
			
			GnSystemParameter gnsysDivDigit = generalService.getSystemParameter(SystemParamConstants.INTERFACE_DIV_DIGIT);
			//GnSystemParameter gnsysDivDigitMethod = generalService.getSystemParameter(SystemParamConstants.INTERFACE_DIV_DIGIT_METHOD);
			
			GnSystemParameter gnsysSwiDigit = generalService.getSystemParameter(SystemParamConstants.INTERFACE_SWI_DIGIT);
			//GnSystemParameter gnsysSwiDigitMethod = generalService.getSystemParameter(SystemParamConstants.INTERFACE_SWI_DIGIT_METHOD);
			
			renderArgs.put("digitKsei", gnsysKseiDigit== null?0:Integer.parseInt(gnsysKseiDigit.getValue()));
			renderArgs.put("digitBI", gnsysBiDigit== null?0:Integer.parseInt(gnsysBiDigit.getValue()));
			
			List<SelectItem> transactionType = new ArrayList<SelectItem>();
			transactionType.add(new SelectItem("DVP", "DVP"));
			transactionType.add(new SelectItem("RVP", "RVP"));		
			renderArgs.put("transactionType", transactionType);
			
			
			List<SelectItem> transferType1 = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.TRANSFER_TYPE);
			List<SelectItem> transferType = new ArrayList<SelectItem>();
			transferType.add(new SelectItem("ALL", "ALL"));
			transferType.addAll(transferType1);
			renderArgs.put("transferType", transferType);
			
			List<SelectItem> listType = new ArrayList<SelectItem>();
			listType.add(new SelectItem("ALL", "ALL"));
			listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_REDEMPTION, AbstractRgPayment.BY_TYPE_REDEMPTION.toUpperCase()));
			listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_SWITCH_OUT, AbstractRgPayment.BY_TYPE_SWITCH_OUT.toUpperCase()));
			listType.add(new SelectItem(AbstractRgPayment.BY_TYPE_DIV_BY_CASH, AbstractRgPayment.BY_TYPE_DIV_BY_CASH.toUpperCase()));
			renderArgs.put("listType", listType);
			
			
			BnFundtransfer fundTransfer = fundService.getFundTransfer(Long.parseLong(maintenanceLog.getEntityKey()));
			List<BnFundtransferDetail> details = new ArrayList<BnFundtransferDetail>();
			for (BnFundtransferDetail det : fundTransfer.getDetails()) {
				String strChargeAmount = "0";
				String strAmount = "0";
				String strNetAmount = "0";
				if(fundTransfer.getProcessType().getLookupId().equals(LookupConstants._FUNDTRANSFER_DEPOSITO)
						|| fundTransfer.getProcessType().getLookupId().equals(LookupConstants._FUNDTRANSFER_CA_SETTLEMENT)){					
					strChargeAmount =Helper.format(det.getChargeAmount(), true, gnsysTwsDigit== null?0:Integer.parseInt(gnsysTwsDigit.getValue()));
					strAmount =Helper.format(det.getAmount(), true, gnsysTwsDigit== null?0:Integer.parseInt(gnsysTwsDigit.getValue()));
					strNetAmount =Helper.format(det.getNetAmount(), true, gnsysTwsDigit== null?0:Integer.parseInt(gnsysTwsDigit.getValue()));

				}
				if(fundTransfer.getProcessType().getLookupId().equals(LookupConstants._FUNDTRANSFER_SETTLEMENT_KSEI)){
					strChargeAmount =Helper.format(det.getChargeAmount(), true, gnsysKseiDigit== null?0:Integer.parseInt(gnsysKseiDigit.getValue()));
					strAmount =Helper.format(det.getAmount(), true, gnsysKseiDigit== null?0:Integer.parseInt(gnsysKseiDigit.getValue()));
					strNetAmount =Helper.format(det.getNetAmount(), true, gnsysKseiDigit== null?0:Integer.parseInt(gnsysKseiDigit.getValue()));
				}
				
				if(fundTransfer.getProcessType().getLookupId().equals(LookupConstants._FUNDTRANSFER_SETTLEMENT_BI)){
					strChargeAmount =Helper.format(det.getChargeAmount(), true, gnsysBiDigit== null?0:Integer.parseInt(gnsysBiDigit.getValue()));
					strAmount =Helper.format(det.getAmount(), true, gnsysBiDigit== null?0:Integer.parseInt(gnsysBiDigit.getValue()));
					strNetAmount =Helper.format(det.getNetAmount(), true, gnsysBiDigit== null?0:Integer.parseInt(gnsysBiDigit.getValue()));
				}
				
				if(fundTransfer.getProcessType().getLookupId().equals(LookupConstants._FUNDTRANSFER_RG_PAYMENT)){
					if(det.getTransType().equals("RED")){
						strChargeAmount =Helper.format(det.getChargeAmount(), true, gnsysRedDigit== null?0:Integer.parseInt(gnsysRedDigit.getValue()));
						strAmount =Helper.format(det.getAmount(), true, gnsysRedDigit== null?0:Integer.parseInt(gnsysRedDigit.getValue()));
						strNetAmount =Helper.format(det.getNetAmount(), true, gnsysRedDigit== null?0:Integer.parseInt(gnsysRedDigit.getValue()));
					}
					if(det.getTransType().equals("SWI")){
						strChargeAmount =Helper.format(det.getChargeAmount(), true, gnsysSwiDigit== null?0:Integer.parseInt(gnsysSwiDigit.getValue()));
						strAmount =Helper.format(det.getAmount(), true, gnsysSwiDigit== null?0:Integer.parseInt(gnsysSwiDigit.getValue()));
						strNetAmount =Helper.format(det.getNetAmount(), true, gnsysSwiDigit== null?0:Integer.parseInt(gnsysSwiDigit.getValue()));
					}
					if(det.getTransType().equals("DIV")){
						strChargeAmount =Helper.format(det.getChargeAmount(), true, gnsysDivDigit== null?0:Integer.parseInt(gnsysDivDigit.getValue()));
						strAmount =Helper.format(det.getAmount(), true, gnsysDivDigit== null?0:Integer.parseInt(gnsysDivDigit.getValue()));
						strNetAmount =Helper.format(det.getNetAmount(), true, gnsysDivDigit== null?0:Integer.parseInt(gnsysDivDigit.getValue()));
					}
				}
				
				det.setAmountStr(strAmount);
				det.setChargeAmountStr(strChargeAmount);
				det.setNetAmountStr(strNetAmount);
				details.add(det);
			}
			
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			}else{
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			if(fundTransfer.getProcessType().getLookupId().equals(LookupConstants._FUNDTRANSFER_DEPOSITO)){
				render("FundTransfer/approvalDeposito.html", fundTransfer,details, mode, taskId, operation, maintenanceLogKey, from, operationCreate);
			}else if(fundTransfer.getProcessType().getLookupId().equals(LookupConstants._FUNDTRANSFER_SETTLEMENT_KSEI) 
					|| fundTransfer.getProcessType().getLookupId().equals(LookupConstants._FUNDTRANSFER_SETTLEMENT_BI)){
				render("FundTransfer/approval_BI_Ksei.html", fundTransfer,details, mode, taskId, operation, maintenanceLogKey, from, operationCreate);
			}else if(fundTransfer.getProcessType().getLookupId().equals(LookupConstants._FUNDTRANSFER_CA_SETTLEMENT) ){
				render("FundTransfer/approval_ca.html", fundTransfer,details, mode, taskId, operation, maintenanceLogKey, from, operationCreate);
			}else{
				render("FundTransfer/approvalRg.html", fundTransfer,details, mode, taskId, operation, maintenanceLogKey, from, operationCreate);
			}
			
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static void approve(String taskId, Long maintenanceLogKey, String operation/*, boolean updatedInvoiceAccount*/) {
		logger.debug("============approve");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			fundService.approveFundTransfer(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			result.put("status", "success");
		} catch (MedallionException e) {
			result.put("status", "error");
			result.put("message", Messages.get(e.getErrorCode()));
		} catch (Exception e) {
			result.put("status", "error");
			result.put("message", e.getMessage());
		}
		renderJSON(result);
	}
	
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		logger.debug("============reject");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			fundService.approveFundTransfer(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			result.put("status", "success");
		} catch (MedallionException e) {
			result.put("status", "error");
			result.put("message", Messages.get(e.getErrorCode()));
		} catch (Exception e) {
			result.put("status", "error");
			result.put("message", e.getMessage());
		}
		renderJSON(result);
	}

}