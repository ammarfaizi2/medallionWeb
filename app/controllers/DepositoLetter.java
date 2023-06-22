package controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.FastDateFormat;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.deser.std.StdDeserializer.LongDeserializer;
import org.codehaus.jackson.type.TypeReference;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.GnCustodianInfo;
import com.simian.medallion.model.GnReportLoader;
import com.simian.medallion.model.GnReportQueue;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.TdMaster;
import com.simian.medallion.model.TdTransaction;
import com.simian.medallion.model.TdTransactionCharge;
import com.simian.medallion.model.TmpDepInst;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.pre.AbstractReportGenerator.OutputType;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.service.TransactionHelper;
import com.simian.medallion.vo.DepositoLetterVO;
import com.simian.medallion.vo.ReportParam;

import helpers.UIConstants;
import helpers.UIHelper;
import play.Play;
import play.mvc.With;
import vo.DepositoSearchParameter;

@With(Secure.class)
public class DepositoLetter extends MedallionController  {

	@Check("administration.depositoletter")
	public static void list() {
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_LETTER));
		render();
	}
	
	@Check("administration.depositoletter")
	public static void search(Paging page, DepositoSearchParameter param){
		String customer = " All";
		String type = " All";
		String securityId = " All";
		String securityType = "('TDP','DOC')";
		String status = LookupConstants.__RECORD_STATUS_APPROVED;
		Date dateFrom = null;
		Date dateTo = null;
		
		page.addParams(Helper.searchAll("(to_char(date_01,'"+Helper.dateOracle(appProp.getDateFormat())+"')||varchar_03||varchar_04||" +
				"varchar_05||varchar_06||varchar_09||number_06|| number_07||" +
				"to_char(date_02,'"+Helper.dateOracle(appProp.getDateFormat())+"')||to_char(date_03,'"+Helper.dateOracle(appProp.getDateFormat())+"')||" +
						"number_02||number_03||number_04||number_05||to_char(date_04,'"+Helper.dateOracle(appProp.getDateFormat())+"')||number_07||varchar_08)" )
				, page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		
		if(!Helper.isNullOrEmpty(param.customerCode)){ customer = param.customerCode; }
		if(!Helper.isNullOrEmpty(param.typeId)){ type = param.typeId; }
		if(!Helper.isNullOrEmpty(param.securityCode)){ securityId = param.securityCode; }
		if(!Helper.isNullOrEmpty(param.securityType)){ securityType = "('"+param.securityType+"')"; }
		
		if(!Helper.isNullOrEmpty(param.strDateFrom)){
			dateFrom = Helper.parseDate(param.strDateFrom, appProp.getDateFormat());
		}
		
		if(!Helper.isNullOrEmpty(param.strDateTo)){
			dateTo = Helper.parseDate(param.strDateTo, appProp.getDateFormat());
		}
		
		if(customer!=null && dateFrom !=null && dateTo !=null) {
			log.debug("masuk ke search");
			page = depositoService.getListDepositoInstLetter(page, customer,securityId,type,status,Helper.formatDMY(dateFrom), Helper.formatDMY(dateTo),securityType);
		}
		
		renderJSON(page);
	}
	
	@Check("administration.depositoletter")
	public static void entry(){
		
	}
	
	@Check("administration.depositoletter")
	public static void edit(){
		
	}
	
	public static String getnotpajak(String param1,String param2) {               //BRISI999-000301============================1387
		String get1=""; 
		String get2=""; 
		String hasil="0";
				
		CfMaster getCustemmerKey = customerService.getCustomerByCustomerNo(param1);			
		get1 = getCustemmerKey.getTaxProfile().getTaxProfileCode();
		get2 = param2;
		
		String getnotpajakImpl = customerService.getnotpajakImpl(get1 , get2);
				
		if(getnotpajakImpl != null) {
			hasil = getnotpajakImpl;
		}
		
		return hasil;
	}
	
	
	public static void view(String transNo, String custCode, String typeId, String secCode, String dtFrom, String dtTo,String newIntRateParam,String settlementAmountList,String lookupDescription) {
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		
		String customer = " All";
		String type = " All";
		String securityId = " All";
		String status = LookupConstants.__RECORD_STATUS_APPROVED;
		Date dateFrom = null;
		Date dateTo = null;
		
		TmpDepInst tmpDepInst = new TmpDepInst();
		
		if(!Helper.isNullOrEmpty(custCode)){ customer = custCode; }
		if(!Helper.isNullOrEmpty(typeId)){ type = typeId; }
		if(!Helper.isNullOrEmpty(secCode)){ securityId = secCode; }
		
		if(!Helper.isNullOrEmpty(dtFrom)){
			dateFrom = Helper.parseDate(dtFrom, appProp.getDateFormat());
		}
		
		if(!Helper.isNullOrEmpty(dtTo)){
			dateTo = Helper.parseDate(dtTo, appProp.getDateFormat());
		}
		
		DepositoLetterVO depositoVo = depositoService.getDepositoInstLetter(customer, securityId, type, status, Helper.formatDMY(dateFrom), Helper.formatDMY(dateTo), transNo);
		Date effDate = null;
		Date transDate = null;
		Date maturDate = null;
		Date tglValutaRedeemDt = null;
		String tglValutaRedeem = null;
		BigDecimal netInterest = null;
		String tglPenempatan = null;
		
		Boolean isPlacement = false;
		Boolean isRedeem = false;
		Boolean isBreak = false;
		Boolean isRollover = false;
		
		if(depositoVo.getDepositoTemplate().equals(LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT))isPlacement = true;
		else if(depositoVo.getDepositoTemplate().equals(LookupConstants.DEPOSITO_TEMPLATE_FULL_REDEEM))isRedeem = true;
		else if(depositoVo.getDepositoTemplate().equals(LookupConstants.DEPOSITO_TEMPLATE_BREAK))isBreak = true;
		else if(depositoVo.getDepositoTemplate().equals(LookupConstants.DEPOSITO_TEMPLATE_ROLLOVER_PRINCIPAL) || 
				depositoVo.getDepositoTemplate().equals(LookupConstants.DEPOSITO_TEMPLATE_ROLLOVER_PRINCIPAL_INTREST))
			isRollover = true;
		
		try {
			effDate = Helper.parse(depositoVo.getEffectiveDate(), "dd/MM/yyyy");
			transDate = Helper.parse(depositoVo.getTransactionDate(), "dd/MM/yyyy");
			maturDate = Helper.parse(depositoVo.getMaturityDate(), "dd/MM/yyyy");
			tglValutaRedeemDt = Helper.parse(depositoVo.getEffectiveDate(), "dd/MM/yyyy");
			tglValutaRedeem = Helper.formatDMMMMMMY(tglValutaRedeemDt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		log.debug("transaction date = "+depositoVo.getTransactionDate());
		log.debug("deposito no = "+depositoVo.getDepositoNo());
		log.debug("eff date = "+effDate);
		log.debug("dep vo mat "+depositoVo.getMaturityDate());
		log.debug("matur Date = "+maturDate);
		log.debug("tglValutaRedeem "+tglValutaRedeem);
		
		GnCustodianInfo custInfo = generalService.getCustodianBankSetup();
		TdMaster tdMaster = depositoService.getMasterDepositoByDepositoNo(depositoVo.getDepositoNo());
		TdTransaction tdTrans = depositoService.getTdTransactionForLetter(depositoVo.getDepositoNo(), depositoVo.getDepositoTemplate(),transDate);
		
		log.debug("trans key = "+tdTrans.getTransactionKey());
		
		TdTransaction trx = depositoService.getTdTransactionDepositoBreak(tdMaster.getDepositoKey());		
		
		
		if(isRedeem || isBreak) {
			netInterest = tdTrans.getTaxOnInterestNet();
			tglPenempatan = Helper.formatDMMMMMMY(depositoVo.getEffectiveDateDt());
		}
		
		BigDecimal totalFee = BigDecimal.ZERO;
		Date breakDate = null;
		if (trx!=null) {
			breakDate = trx.getTransactionDate();
		}
		
		String jatuhTempoMature = "";
		String jatuhTempoBreak = "";
		
		
		if(breakDate!=null){
			jatuhTempoBreak = Helper.formatDMMMMMMY(breakDate);
		}
		jatuhTempoMature = Helper.formatDMMMMMMY(tdMaster.getMaturityDate());
		
		
		try{
			Date paramFrom = depositoVo.getEffectiveDateDt();
			Date paramTo = depositoVo.getMaturityDateDt();
			
			log.debug("sisi kiri");
			log.debug("paramFrom = "+paramFrom);
			log.debug("paramTo = "+paramTo);
			
			int ad = TransactionHelper.calculateAccruedDaysPortoNew(paramFrom, paramTo, 1, TransactionHelper.decodeAccrualBase(tdMaster.getSecurity().getAccrualBase().getLookupCode()));
			log.debug("sisi kiri ad = "+ad);
			tdMaster.setAccruedDays(ad);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			Date paramFrom = depositoVo.getMaturityDateDt();
			Date paramTo = depositoVo.getNextPaymentDateDt();
			
			if(isRedeem||isBreak) {
				paramFrom = depositoVo.getEffectiveDateDt();
				if(isBreak) paramTo = breakDate; 
				if(isRedeem) paramTo = depositoVo.getMaturityDateDt();
			}
			
			log.debug("sisi kanan");
			log.debug("paramFrom = "+paramFrom);
			log.debug("paramTo = "+paramTo);
			
			int ad = TransactionHelper.calculateAccruedDaysPortoNew(paramFrom, paramTo, 1, TransactionHelper.decodeAccrualBase(tdTrans.getSecurity().getAccrualBase().getLookupCode()));
			tdTrans.setAccruedDays(ad);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		tdMaster.setTotalFee(totalFee);
		tdMaster.setSettlementAmount(tdMaster.getTotalFee().add(tdMaster.getAmount()));
		tdMaster.setNoOfPayment(tdMaster.getInterestSchedules().size());
		
		String userKey = session.get(UIConstants.SESSION_USER_KEY);
		
		GnUser user = applicationService.getUser(Long.parseLong(userKey));
		String userName = user.getUserName();
		
		Date appDate = generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID);
		String strAppDate = Helper.formatDMMMMMMY(appDate); 
		String effdateStr =  Helper.formatDMMMMMMY(effDate); 
//		String cutAppDate = FastDateFormat.getInstance("MM/yyyy").format(appDate);
		String cutAppDate = FastDateFormat.getInstance("yy").format(appDate);
		if(cutAppDate != null) {
			cutAppDate = ""+(Integer.parseInt(cutAppDate)+2);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_LETTER));
		
		String printAddr = custInfo.getAddress().replaceAll("(\r\n|\n)", "<br />");
		
		String currency = tdTrans.getPbaCurrency().getCurrencyCode();
		
		Integer jkDiff = tdTrans.getAccruedDays();
		
		
		String getDataNotPajak="";
		getDataNotPajak = getnotpajak(custCode,(tdMaster.getSecurity().getSecurityKey().toString())); 
		if(getDataNotPajak != null) {
			if("1".equals(getDataNotPajak)) {
				tdMaster.setNopajak("1");
			}else{
				tdMaster.setNopajak("0");
			}
		}else {
			tdMaster.setNopajak("0");
		}
		
		
				
		TdMaster deposito = depositoService.getMasterDeposito(tdMaster.getDepositoKey());	
		TdTransaction trxs = depositoService.getTdTransaction(tdTrans.getTransactionKey());
		
		BigDecimal sumOfNetCharge = BigDecimal.ZERO;
		BigDecimal nominal = deposito.getAmount();
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		String contactperson="";
		String tlpn="";
		String fax="";
		if (deposito.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(deposito.getUdf(), new TypeReference<HashMap<String, String>>() {	});				
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						if("CONTACT_PERSON".equals(udf.getFieldName())) 
							contactperson=udf.getValue();
						if("PHONE_NO".equals(udf.getFieldName())) 
							tlpn=udf.getValue();
						if("FAX_NO".equals(udf.getFieldName())) 
							fax=udf.getValue();
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
					}
				}
			} catch (JsonParseException e) {
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		String val25c="";
		String val26c="";
		String headerdetailval="";
		List<TdTransaction> transactionHistory = depositoService.listTransactionHistoryByMasterDepositoId(tdMaster.getDepositoKey());
		List<TdTransaction> rolloverHistory = depositoService.listRolloverHistoryByMasterDepositoId(tdMaster.getDepositoKey());
		
		Boolean valExist = false;
		String datadisabledPlacementBreak="";
		String validasiContainsRollover="";
		String mountlyval = "";
		String rolloverListType = "";
		if(transactionHistory.size() > 1) {
			datadisabledPlacementBreak = "PLACEMENT CENCEL BREAK";
		}
//		System.out.println("----------mulai-----------"+datadisabledPlacementBreak);
		for (TdTransaction transactionHistoryparam : transactionHistory) {
			if(rolloverHistory.size() > 0) {
				for (TdTransaction transactionHistoryparam2 : rolloverHistory) {
					if(transactionHistoryparam2.getTransactionKey().equals(tdTrans.getTransactionKey())) {		
						SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
					    try {  
					        Date date1 = formatter.parse(depositoVo.getMaturityDate());  
					        effdateStr = Helper.formatDMMMMMMY(date1);
					    } catch (ParseException e) {e.printStackTrace();}  
					    depositoVo.setMaturityDateDt(depositoVo.getNextPaymentDateDt());
					    valExist = true;
					    if(transactionHistoryparam2.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription().contains("ROLLOVER")) {
					    	validasiContainsRollover = "ROLLOVER";
					    }
					}
				}					    
			}			
			if("BREAK".equals(transactionHistoryparam.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription())) {
				datadisabledPlacementBreak = "PLACEMENT CENCEL BREAK";
			}
		  if(transactionHistoryparam.getTransactionKey().equals(tdTrans.getTransactionKey()) || valExist == true) {	
			  if("PLACEMENT".contains(transactionHistoryparam.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription())) {
				val25c = "Rollover";
				if("ROLLOVER".equals(validasiContainsRollover)) {
//					headerdetailval = "Deposit Rollover";
					headerdetailval = "Deposit Placement";
				    try{
				    	DateFormat dateformate = new SimpleDateFormat("dd/MM/yyyy");	
				        String dawal = depositoVo.getMaturityDate();
				        Date Dawal = dateformate.parse(dawal);
				        
				        String dakhir = dateformate.format(depositoVo.getMaturityDateDt());
				        Date Dakhir = dateformate.parse(dakhir);	
				        
				        long selisihTenor = Math.abs(Dakhir.getTime() - Dawal.getTime());				        
				        String hasil = String.valueOf(((selisihTenor)/ (1000 * 60 * 60 * 24)));		
				        tdMaster.setAccruedDays(Integer.parseInt(hasil));  
				    }catch(Exception e){ }
				}else {
					headerdetailval = "Deposit Placement";
					if((LookupConstants.INTEREST_FREQUENCY_MONTHLY).equals(deposito.getInterestFrequency().getLookupId())) {
						mountlyval = "1";
					}
				}
				
				deposito.setSettlementAmount(deposito.getAmount());	
				totalFee = BigDecimal.ZERO;
				if (trxs!=null) {
					deposito.setAmount(trxs.getAmount());
					for (TdTransactionCharge chargeTrans : trxs.getDepositoCharges()) {
						BigDecimal taxAmount = chargeTrans.getTaxAmount();
						if (taxAmount==null) {
							taxAmount = BigDecimal.ZERO;
							chargeTrans.setTaxAmount(taxAmount);
						}
						chargeTrans.setChargeNetAmount(chargeTrans.getChargeValue().add(chargeTrans.getTaxAmount()));
						if (chargeTrans.getChargeMaster().getTaxMaster()!=null){
							chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
						}
						if (chargeTrans.getChargeFrequency().equals(LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE))
							totalFee = totalFee.add(chargeTrans.getChargeValue());
					}
					deposito.setTotalFee(totalFee);				
					deposito.setSettlementAmount(deposito.getTotalFee().add(deposito.getAmount()));
				}
//				val26c="Upon maturity if there is no further instruction, kindly transfer the nominal plus profit sharing to account bellow. ";
				val26c=deposito.getRemarks();
			}
			if("FULL REDEEM".contains(transactionHistoryparam.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription()) || 
			   "BREAK".contains(transactionHistoryparam.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription()) ||
			   "ROLLOVER".contains(transactionHistoryparam.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription())
			  ) {
				val25c = "Adjusted Tenor";
				if("FULL REDEEM".contains(transactionHistoryparam.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription()) || 
						   "BREAK".contains(transactionHistoryparam.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription())		
				   ) {
//							val26c="Kindly transfer the nominal plus profit sharing to account bellow.";
					val26c=deposito.getRemarks();
							if("FULL REDEEM".contains(transactionHistoryparam.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription())) {
								if(lookupDescription.contains("ROLLOVER")) {
									rolloverListType = "ROLLOVER";
								}
								headerdetailval = "DEPOSITO WITHDRAWAL (LIQUIDATE)";
								deposito.setSettlementAmount(deposito.getAmount());
								if (trxs != null) {
									for (TdTransactionCharge chargeTrans : trxs.getDepositoCharges()) {		
										BigDecimal taxAmount = chargeTrans.getTaxAmount();
										if (taxAmount==null) taxAmount = BigDecimal.ZERO;
										chargeTrans.setChargeNetAmount(chargeTrans.getChargeValue().add(chargeTrans.getTaxAmount()));
										if (chargeTrans.getChargeMaster().getTaxMaster()!=null){
											chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
										}
										if (chargeTrans.getChargeFrequency().equals(LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE)){
											sumOfNetCharge = sumOfNetCharge.add(chargeTrans.getChargeNetAmount());
										}
									}
									
									if(deposito.getTaxOnInterestNet() == null) {
										jatuhTempoBreak = Helper.formatDMMMMMMY(trxs.getTransactionDate());
										netInterest = trxs.getTaxOnInterestNet();
										nominal = trxs.getAmount();
										BigDecimal settlementAmount = nominal.add(netInterest).subtract(sumOfNetCharge);
										deposito.setSettlementAmount(settlementAmount);
									}else {
										jatuhTempoBreak = Helper.formatDMMMMMMY(deposito.getDepositoBreak());
										netInterest = deposito.getTaxOnInterestNet();
										BigDecimal settlementAmount = nominal.add(netInterest).subtract(sumOfNetCharge);
										deposito.setSettlementAmount(settlementAmount);
									}
									
									tdMaster.setPbaBeneficiaryName(deposito.getBankAccount().getName());
									tdMaster.setPbaAccountNo(deposito.getBankAccount().getAccountNo());
								}
							}
							if("BREAK".contains(transactionHistoryparam.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription())) {
								headerdetailval = "Deposit Withdrawal(BREAK)";
								deposito.setSettlementAmount(deposito.getAmount());
								if (trxs != null) {
									for (TdTransactionCharge chargeTrans : trxs.getDepositoCharges()) {		
										BigDecimal taxAmount = chargeTrans.getTaxAmount();
										if (taxAmount==null) taxAmount = BigDecimal.ZERO;
										chargeTrans.setChargeNetAmount(chargeTrans.getChargeValue().add(chargeTrans.getTaxAmount()));
										if (chargeTrans.getChargeMaster().getTaxMaster()!=null){
											chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
										}
										if (chargeTrans.getChargeFrequency().equals(LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE)){
											sumOfNetCharge = sumOfNetCharge.add(chargeTrans.getChargeNetAmount());
										}
									}
									
									if(deposito.getTaxOnInterestNet() == null) {
										jatuhTempoBreak = Helper.formatDMMMMMMY(trxs.getTransactionDate());
										netInterest = trxs.getTaxOnInterestNet();
										nominal = trxs.getAmount();
										BigDecimal settlementAmount = nominal.add(netInterest).subtract(sumOfNetCharge);
										deposito.setSettlementAmount(settlementAmount);
									}else {
										jatuhTempoBreak = Helper.formatDMMMMMMY(deposito.getDepositoBreak());
										netInterest = deposito.getTaxOnInterestNet();
										BigDecimal settlementAmount = nominal.add(netInterest).subtract(sumOfNetCharge);
										deposito.setSettlementAmount(settlementAmount);
									}
								}
							}	
					}
				if("ROLLOVER".contains(transactionHistoryparam.getDepositoTrxTemplate().getDepositoTemplate().getLookupDescription())) {
//					val26c=" - ";
					val26c=deposito.getRemarks();
						headerdetailval = "Deposit Rollover";
				}
			}			
		  }		  
		}
		//deposito.setAccruedDays(TransactionHelper.getAccruedDays());
		render("DepositoLetter/detail.html",transNo, custCode, typeId, secCode, dtFrom, dtTo,
				mode,depositoVo,tdMaster,custInfo,appDate,
				strAppDate,cutAppDate, currency,jkDiff,
				jatuhTempoMature,jatuhTempoBreak,userName,printAddr,validasiContainsRollover,
				tmpDepInst, tdTrans, tglValutaRedeem,netInterest,tglPenempatan,effdateStr,rolloverListType,
				isPlacement,isRedeem,isBreak,isRollover,contactperson,tlpn,fax,val25c,headerdetailval,deposito,val26c,transactionHistory,datadisabledPlacementBreak,mountlyval,newIntRateParam,settlementAmountList,lookupDescription);
	}
	
		
	public static void printDeposito(TmpDepInst tmpDepInst) {
		Date today = new Date();
		
		GnSystemParameter depLetterParam = generalService.getSystemParameter(SystemParamConstants.PARAM_PRINT_DEPOSITO_LETTER_KEY);
		GnSystemParameter imageUrl = generalService.getSystemParameter(SystemParamConstants.REPORT_IMAGE_NO_TEXT_URL);
		
		String depInstNo = Helper.formatYMD(today)+"|"+tmpDepInst.getDepositoNo()+"|"+session.get(UIConstants.SESSION_USERNAME);
		tmpDepInst.setDepInstNo(depInstNo);
		tmpDepInst.setCreateDate(today);
		tmpDepInst.setCol1(imageUrl.getValue());
		
//		log.debug("depInstNo = "+depInstNo);
		String userKey = session.get(UIConstants.SESSION_USER_KEY);
		
		//save data ke table temp
		depositoService.printDepositoLetter(userKey, tmpDepInst);
		
		//panggil report generator untuk generate report
		Map<String, String> jsonResult = new HashMap<String, String>();
		
		GnReportLoader report = generalService.viewReportLoader(Long.parseLong(depLetterParam.getValue()));
		OutputType defaultOutput = OutputType.PDF;
		ReportParam[] reportParam = new ReportParam[1];
		
		reportParam[0] = new ReportParam();
		
//		log.debug("reportParam = "+reportParam[0]);
		
		reportParam[0].setType("String");
		reportParam[0].setValue(depInstNo);
		reportParam[0].setField("dep_inst_no");
		
//		String reportFileName = "Deposito Instruction Letter";
		String reportFileName = generateUniqueReportName("Deposito Instruction Letter");
		
		try{
			GnReportQueue reportQ = reportQueueService.generateReport(report, reportParam, reportFileName, defaultOutput.toString(), userKey);
			String resultFileName = reportQ.getResultFilename();
			String[] tmpSplit = resultFileName.split("\\.");
			String fileExt = "";
			if(tmpSplit != null){
				fileExt = tmpSplit[1];
			}
			String fileDestName = reportFileName+"."+fileExt;
			log.debug( "done generating report for "+reportFileName );
			jsonResult.put( "status", "1" );
			jsonResult.put( "reportFile", fileDestName );
		}catch(Exception e){
			e.printStackTrace();
			log.debug( "error generating report for "+reportFileName );
			jsonResult.put( "status", "0" );
			jsonResult.put( "reportFile", reportFileName );
		}
		renderJSON(jsonResult);
	}
	
	public static void downloadReport(String downloadfile) {
		log.debug("downloadFile = "+downloadfile);
		String uploadedDirOutput = Play.configuration.getProperty("upload.reportloaderoutput");
		String fullPath = uploadedDirOutput + downloadfile;
		log.debug("fullPath = "+fullPath);
		File fileDest = new File(fullPath);
		try {
			clientFileService.doDownload(downloadfile, fileDest, SimpleFileService.UPLOAD_REPORT_OUTPUT_FILE);
		} catch (Exception e) {
			log.error( "Error downloading file:"+downloadfile, e );
			throw new MedallionException("RL-C-001", "Error downloading file:"+downloadfile);
		}
		log.debug( "fullPath:"+fullPath );
		renderBinary(new File(fullPath), fileDest.getName());
	}
	
	private static String generateUniqueReportName(String seed){		
		Calendar cal = Calendar.getInstance();
		String fileName = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
		String time = cal.get( Calendar.SECOND ) +""+cal.get( Calendar.MILLISECOND) +""+cal.get( Calendar.MINUTE)+""+cal.get( Calendar.HOUR);
		return seed+"_"+fileName+time;
	}
	
	public static void confirming(Long id, String mode) {
		
	}
	
	public static void back(Long id) {
		
	}
	
	public static void confirm(Long id, String mode) {
		
	}
	
}
