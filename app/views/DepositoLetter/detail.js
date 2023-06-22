$(function(){
	//-------------------------INIT--------------------
	

	var inputdata56col = $("#jkWaktuDiffVal").val();
	var inputdata25col = $("#val25c").val();
	
	$("#jkWaktuDiffVal").val(inputdata56col+" Days"); 
	
	var validasiContainsRollover = '${validasiContainsRollover}';
	var newIntRateParam = '${newIntRateParam}';
	var validasiRollover = $("#transTypeVal").val();  
	var rolloverListType = '${rolloverListType}';
	
	
	
	if(validasiRollover == 'Deposit Placement'){	
		document.getElementById("tglPencairanVal").style.visibility = "hidden";
		document.getElementById("tglPencairanDiffVal").style.visibility = "hidden";
		document.getElementById("validasiRollover1").style.visibility = "hidden"; 
		$("#validasiRollover1").remove();
		
		document.getElementById("interestRateGross").style.visibility = "hidden";
		document.getElementById("interestRateDiff").style.visibility = "hidden";
		document.getElementById("validasiRollover2").style.visibility = "hidden"; 
		$("#validasiRollover2").remove();
		
		document.getElementById("val25c").style.visibility = "hidden";
		document.getElementById("jkWaktuDiffVal").style.visibility = "hidden";
		document.getElementById("validasiRollover3").style.visibility = "hidden"; 
		$("#validasiRollover3").remove();
		
		if(validasiContainsRollover == 'ROLLOVER'){			
			var settlementAmountList = '${settlementAmountList}';
			$("#netAmountVal").val(settlementAmountList);
			$("#netAmountStrippedVal").val(settlementAmountList);
			$("#transTypeVal").val('Deposit Rollover');  
		}
	}else if( validasiRollover == 'DEPOSITO WITHDRAWAL (LIQUIDATE)'){
		document.getElementById("tglPencairanVal").style.visibility = "hidden";
		document.getElementById("tglPencairanDiffVal").style.visibility = "hidden";
		document.getElementById("validasiRollover1").style.visibility = "hidden"; 
		$("#validasiRollover1").remove();
		
		document.getElementById("interestRateGross").style.visibility = "hidden";
		document.getElementById("interestRateDiff").style.visibility = "hidden";
		document.getElementById("validasiRollover2").style.visibility = "hidden"; 
		$("#validasiRollover2").remove();
		
		document.getElementById("val25c").style.visibility = "hidden";
		document.getElementById("jkWaktuDiffVal").style.visibility = "hidden";
		document.getElementById("validasiRollover3").style.visibility = "hidden"; 
		$("#validasiRollover3").remove();		
	}
	
	
	
    var nopajakvar = $("#nopajakhidden").val();
    
	if(nopajakvar == '0'){
	    $("#nopajak").attr("checked", "checked");
	    $("#nopajak").attr("disabled",true);
	}else{
		$("#nopajak").attr("disabled",true);
		$("#nopajak").removeAttr("checked");
	}
	
	$('#back').button();
	$('#reset').button();
	$("#printPreview").button();
	$("#tempMoney").hide();
	
	var isPlacement = '${isPlacement}';
	var isRollover = '${isRollover}';
	var isRedeem = '${isRedeem}';
	var isBreak = '${isBreak}';	
	var isPlacementCencelBreak = '${datadisabledPlacementBreak}';
	var mountlyval = '${mountlyval}';
	
	if(isPlacement == 'true') {
		if(isPlacementCencelBreak == "PLACEMENT CENCEL BREAK"){
			$("#maturityDate").attr("disabled","disabled");
			$("#maturityDateVal").attr("disabled","disabled");
			$("#interestRate").attr("disabled","disabled");
			$("#interestRateVal").attr("disabled","disabled");
		}else{
			if(mountlyval == "1"){ 
				$("#maturityDate").attr("disabled","disabled");
				$("#maturityDateVal").attr("disabled","disabled");
				$("#interestRate").attr("disabled","disabled");
				$("#interestRateVal").attr("disabled","disabled");
			}else{
				if(isPlacement == 'true') {
					$("#maturityDate").attr("disabled","disabled");
					$("#maturityDateVal").attr("disabled","disabled");
					$("#interestRate").attr("disabled","disabled");
					$("#interestRateVal").attr("disabled","disabled");
				}else{
					$("#maturityDate").removeAttr("disabled");
					$("#maturityDateVal").removeAttr("disabled");
					$("#interestRate").removeAttr("disabled");
					$("#interestRateVal").removeAttr("disabled");
				}
			}			
		}		
	}else{
		$("#maturityDate").attr("disabled","disabled");
		$("#maturityDateVal").attr("disabled","disabled");
		$("#interestRate").attr("disabled","disabled");
		$("#interestRateVal").attr("disabled","disabled");
	}
	
	
	$("#maturityDate").blur(function(){
		var val = $("#maturityDate").val();
		if(val==null || val=="") $("#h_maturityDate").val(''); 
		else $("#h_maturityDate").val(val);
	});
	
	$("#maturityDateVal").blur(function(){
		var val = $("#maturityDateVal").val();
		if(val==null || val=="") $("#h_maturityDate").val(''); 
		else $("#h_maturityDate").val(val);
	});
	
	$("#interestRate").blur(function(){
		var val = $("#interestRate").val();
		if(val==null || val=="") $("#h_interestRate").val(''); 
		else $("#h_interestRate").val(val);
	});
	
	$("#interestRateVal").blur(function(){
		var val = $("#interestRateVal").val();
		if(val==null || val=="") $("#h_interestRate").val(''); 
		else $("#h_interestRate").val(val);
	});
	
	if($("#interestRate").val()!=null){
		var ir = $("#interestRate").val();
		ir = ir.replace(' %','');
		ir += ' %';
		
		if(validasiContainsRollover == 'ROLLOVER'){
			var result = newIntRateParam.split(".");
			if(result[0] == '00'){
				$("#interestRateVal").val(result[0] += ' %');
				$("#h_interestRate").val(result[0] += ' %');
			}else{
				$("#interestRateVal").val(newIntRateParam += ' %');
				$("#h_interestRate").val(newIntRateParam += ' %');
			}
			
		}else{
			$("#interestRate").val(ir);
			$("#h_interestRate").val(ir);
		}
	}
	
	if($("#interestRateVal").val()!=null){
		
		var ir = $("#interestRateVal").val();
		ir = ir.replace(' %','');
		ir += ' %';
		if(validasiContainsRollover == 'ROLLOVER'){
			var result = newIntRateParam.split(".");
			$("#interestRateVal").val(result[0] += ' %');
			$("#h_interestRate").val(result[0] += ' %');
		}else{
			$("#interestRateVal").val(ir);
			$("#h_interestRate").val(ir);
		}
	}
	
	if($("#accruedDays").val()!=null){
		var ad = $("#accruedDays").val();
		ad = ad.replace(' Hari','');
//		ad += " Hari";
		ad += " Days";
		$("#accruedDays").val(ad);
		$("#h_accruedDays").val(ad);
	}
	
	if($("#accruedDaysVal").val()!=null){
		var ad = $("#accruedDaysVal").val();
		ad = ad.replace(' Hari','');
//		ad += " Hari";
		ad += " Days";
		$("#accruedDaysVal").val(ad);
		$("#h_accruedDays").val(ad);
	}
	
	//-------------------------EVENT-------------------
	$('#reset').click(function() {
		location.href='@{view()}?transNo=${transNo}&custCode=${custCode}&type=${typeId}&secCode=${secCode}&dtFrom=${dtFrom}&dtTo=${dtTo}';			
	});
	
	if ('${mode}'=='edit'){
		$('#clear').css('display', 'none');
	}
	
	$("#instruction").blur(function(){
		var inst = $("#instruction").val();		
		$("#instruction").val(inst.substring(0,350));
		
	});
	
	$("#instructionview").blur(function(){
		var inst2 = $("#instructionview").val();		
		$("#instructionview").val(inst2.substring(0,350));
	});
	
	$('#back').click(function(){
		location.href="@{list()}";
	});
	
	
	$("#downloadDiv").hide();
	$("#downloadLink").hide();
	$("#printPreview").click(function(){
//		$("#downloadDiv").show();
//		$("#downloadLink").hide();
//		$("#downloadSpan").html("");
		
		var isValid = validForm();
		
		if(!isValid) return false;
		
		var inputval1 = $("#contactpersonval").val(); //Contact Person
		if(inputval1 == ""){inputval1 = "A1A"}else{ inputval1 = $("#contactpersonval").val();}
	    var inputval2 = $("#tlpnval").val();
	    if(inputval2 == ""){inputval2 = "A1A"}else{ inputval2 = $("#tlpnval").val();}
		var inputval3 = $("#faxval").val();
		if(inputval3 == ""){inputval3 = "A1A"}else{ inputval3 = $("#faxval").val();}
		var inputval4 = $("#headerTransTypeVal").val(); //Company Name
		if(inputval4 == ""){inputval4 = "A1A"}else{ inputval4 = $("#headerTransTypeVal").val();}
	    var inputval5 = $("#tdMasteraccountaccountNo").val(); //Account Code
	    if(inputval5 == ""){inputval5 = "A1A"}else{ inputval5 = $("#tdMasteraccountaccountNo").val();}
		var inputval6 = $("#custInfocontact1NameVal").val();  //From Contact Person
		if(inputval6 == ""){inputval6 = "A1A"}else{ inputval6 = $("#custInfocontact1NameVal").val();}
		var inputval7 = $("#custInfoaddressPhone1").val();
		if(inputval7 == ""){inputval7 = "A1A"}else{ inputval7 = $("#custInfoaddressPhone1").val();}
		var inputval8 = $("#custInfoaddressPhone3").val();
		if(inputval8 == ""){inputval8 = "A1A"}else{ inputval8 = $("#custInfoaddressPhone3").val();}
	    var inputval9 = $("#nomor").val();         //Ref No Custody
	    if(inputval9 == ""){inputval9 = "A1A"}else{ inputval9 = $("#nomor").val();}
	    var inputval10= $("#depositosecuritycurrencycurrencyCode").val(); //Currency
	    if(inputval10 == ""){inputval10 = "A1A"}else{ inputval10 = $("#depositosecuritycurrencycurrencyCode").val();}
		var inputval11= $("#netAmountVal").val();    //Amount to be transferred (withdrawn)
		if(inputval11 == ""){inputval11 = "A1A"}else{ inputval11 = $("#netAmountVal").val();}
	    var inputval12= $("#tglPencairanVal").val(); //27
	    if(inputval12 == ""){inputval12 = "A1A"}else{ inputval12 = $("#tglPencairanVal").val();}
	    var inputval13= $("#interestRateGross").val();//28
	    if(inputval13 == ""){inputval13 = "A1A"}else{ inputval13 = $("#interestRateGross").val();}
	    var inputval14= $("#val25c").val();//25
	    if(inputval14 == ""){inputval14 = "A1A"}else{ inputval14 = $("#val25c").val();}
	    var inputval15= $("#jkWaktuDiffVal").val();//56
	    if(inputval15 == ""){inputval15 = "A1A"}else{ inputval15 = $("#jkWaktuDiffVal").val();}
	    var inputval16= $("#val26c").val(); //DEscription
	    if(inputval16 == ""){inputval16 = "A1A"}else{ inputval16 = $("#val26c").val();}
	    var inputval17= $("#iddetailfoot").val(); //val58
	    if(inputval17 == ""){inputval17 = "A1A"}else{ inputval17 = $("#iddetailfoot").val();}
	    var inputval18= $("#userName").val(); //val48
	    if(inputval18 == ""){inputval18 = "A1A"}else{ inputval18 = $("#userName").val();}
	    var inputval19= $("#accNameVAl").val(); //
	    if(inputval19 == ""){inputval19 = "A1A"}else{ inputval19 = $("#accNameVAl").val();}
	    var inputval20= $("#maturityDateVal").val(); 
	    if(inputval20 == ""){inputval20 = "A1A"}else{ inputval20 = $("#maturityDateVal").val();}
	    var inputval21= $("#transTypeVal").val(); 
	    if(inputval21 == ""){inputval21 = "A1A"}else{ inputval21 = $("#transTypeVal").val();}
	    var inputval22= $("#accNameTabelVal").val(); 
	    if(inputval22 == ""){inputval22 = "A1A"}else{ inputval22 = $("#accNameTabelVal").val();}
	    var inputval23= $("#nasabahBankNameVal").val(); 
	    if(inputval23 == ""){inputval23 = "A1A"}else{ inputval23 = $("#nasabahBankNameVal").val();}
	    var inputval24= $("#penempatanNamaRekVal").val(); 
	    if(inputval24 == ""){inputval24 = "A1A"}else{ inputval24 = $("#penempatanNamaRekVal").val();}
	    var inputval25= $("#penempatanNoRekVal").val(); 
	    if(inputval25 == ""){inputval25 = "A1A"}else{ inputval25 = $("#penempatanNoRekVal").val();}
	    var inputval26= $("#datePlacementVal").val(); 
	    if(inputval26 == ""){inputval26 = "A1A"}else{ inputval26 = $("#datePlacementVal").val();}
	    var inputval27= $("#f13").val(); 
	    if(inputval27 == ""){inputval27 = "A1A"}else{ inputval27 = $("#f13").val();}
	    var inputval28= $("#f13Detail").val(); 
	    if(inputval28 == ""){inputval28 = "A1A"}else{ inputval28 = $("#f13Detail").val();}
	    var inputval29= $("#f14").val(); 
	    if(inputval29 == ""){inputval29 = "A1A"}else{ inputval29 = $("#f14").val();}
	    var inputval30= $("#f14Detail").val(); 
	    if(inputval30 == ""){inputval30 = "A1A"}else{ inputval30 = $("#f14Detail").val();}
	    var inputval31= $("#instructionview").val(); 
	    if(inputval31 == ""){inputval31 = "A1A"}else{ inputval31 = $("#instructionview").val();}
	    var inputval32= $("#interestRateVal").val(); 
	    if(inputval32 == ""){inputval32 = "A1A"}else{ inputval32 = $("#interestRateVal").val();}	
	    var inputval33= $("#nasabahBankName2").val(); 
	    if(inputval33 == ""){inputval33 = "A1A"}else{ inputval33 = $("#nasabahBankName2").val();}
	    
	    
	    
var masukdata = inputval1+"||"+inputval2+"||"+inputval3+"||"+inputval4+"||"+inputval5+"||"+inputval6+"||"+inputval7+"||"+inputval8+"||"+inputval9+"||"+inputval10+"||"+inputval11+"||" +
		""+inputval12+"||"+inputval13+"||"+inputval14+"||"+inputval15+"||"+inputval16+"||"+inputval17+"||"+inputval18+"||"+inputval19+"||"+inputval20+"||"+inputval21+"||"+inputval22+"||" +
				""+inputval23+"||"+inputval24+"||"+inputval25+"||"+inputval26+"||" +
				""+inputval27+"||"+inputval28+"||"+inputval29+"||"+inputval30+"||"+inputval31+"||"+inputval32+"||"+inputval33;

		$("#keterangan").val(masukdata); 
		
		
		$('#print').button({disabled: true});
		$('#printPreview').button({disabled: true});
		$.ajax({
			  type: 'POST',
			  url: "@{DepositoLetter.printDeposito()}",
			  data : $("#printDepositoForm").serialize(),
			  async: true,
			  success: function(data, textStatus, jqXHR){
				  checkRedirect(data);
				  if( data.status === "1" ){
//					  console.log("success");
//					  $("#downloadSpan").html("Generate Letter Success");
					  $("#downloadLink").attr('href',"@{DepositoLetter.downloadReport()}?downloadfile="+data.reportFile);
//					  $("#downloadLink").show();
					  
					  //pake document.getElement karena pake jquery gk jalan
					  document.getElementById("downloadLink").click();
				  }else if(data.status === "0") {
//					  console.log("fail");
//					  $("#downloadSpan").html("Generate Letter Failed");
//					  $("#downloadLink").hide();
				  }
				  $('#print').button({disabled: false});
				  $('#printPreview').button({disabled: false});
			  },
			  error: function(jxhr, status){
				  $("#downloadSpan").html("Generate Letter Error");
				  $("#downloadLink").hide();
				  $('#print').button({disabled: false});
				  $('#printPreview').button({disabled: false});
			  }
		});
		
	});
	
	//---------------------FUNCTION--------------------
	function getSymbolCurrency(){
		var nom = $("#nominal").val();
		var cur = '${currency} ';
		nom = nom.replace(cur,'');
		
		var nom = $("#nominalVal").val();
		
		$("#nominalVal").val(cur+nom);
		$("#nominal").val(cur+nom);
		$("#h_nominal").val(cur+nom);
	}
	
	function validForm(){
		var valid = true;
		
		var inpMandatory = ["tmpDepInst.col6","tmpDepInst.col9","tmpDepInst.col12",
		                    "tmpDepInst.col13","tmpDepInst.col14","tmpDepInst.col16",
		                    "tmpDepInst.col33","tmpDepInst.col34",//"tmpDepInst.col44",
		                    "tmpDepInst.col45","tmpDepInst.col46","tmpDepInst.col47",
		                    "tmpDepInst.col48"
		                    ];
		
		for(var i=0; i<inpMandatory.length; i++){
			var selector = $("input[name="+inpMandatory[i]+"]");
			if(selector.val()==""){
				selector.addClass("fieldError");
				selector.focus();
				valid=false;
				$("#downloadSpan").html("Please fill form correctly");
			}else{
				selector.removeClass("fieldError");
			}
		}
		
		return valid;
	}
	
	function fillDefaultNo(){
		var appDate = '${cutAppDate}';	
		$("#nomor").val(appDate+"/xxx-3/TIG");
		//$("#nomor").val("B.  -DIS/CUS/"+appDate); 22/xxx-3/TIG
	}
	
	function fillPhoneInfo(){
		var phone1 = '${custInfo.addressPhone1}';
		var phone2 = '${custInfo.addressPhone2}';
		var phone = 'Telepon : ';
		
		if(phone1!=null){
			phone += phone1;
		}
		if(phone2!=null){
			if(phone1!=null) phone += ', '+phone2;
			else phone += phone2;
		}
		
		$("#custInfoPhone").val(phone);
		$("#h_custInfoPhone").val(phone);
		$("#custInfoPhoneVal").val(phone);
	}
	
	function fillFaxNum(){
		var faxNum = '${custInfo.addressPhone3}';
		var fax = 'Facsimile : ';
		
		fax += faxNum;
		$("#custInfoFaxNum").val(fax);
		$("#h_custInfoFaxNum").val(fax);
		$("#custInfoFaxNumVal").val(fax);
	}
	
	function fillTransType(){
		var typeHal = '';
		if(isPlacement == 'true')typeHal="Penempatan Deposito";
		else if(isRedeem == 'true')typeHal="Pencairan Deposito";
		else if(isBreak == 'true')typeHal="Pencairan Deposito (BREAK)";
		else if(isRollover == 'true')typeHal="Rollover Deposito";
		
	
		$("#headerTransType").val(typeHal);
		$("#h_headerTransType").val(typeHal);
		$("#cTransType").val(typeHal);
		$("#h_cTransType").val(typeHal);
		$("#transType").val(typeHal);
//		$("#transTypeVal").val(typeHal);
	}
	
	function fillPerihal(){
		var acc = '${depositoVo.account}';
		if(acc!=null && acc!=""){
			var split = acc.split(" - ");
			if(split!=null && split.length>1){
				$("#accName").val(split[1]);
				$("#accNameVAl").val(split[1]);
				$("#accNameTabel").val(split[1]);
				$("#accNameTabelVal").val(split[1]);
				$("#h_accName").val(split[1]);
				$("#headerTransTypeVal").val(split[1]);
			}
		}//end if
	}
	
	function getFormattedNum(num){
		if(num==null)return 0;
		
		var newNum = Number(num);
		if(newNum != NaN){
			newNum = parseFloat(Math.round(newNum * 100) / 100).toFixed(2);
			$("#tempMoney").val(newNum).focusout();
			return $("#tempMoney").val();
		}
		
		return 0;
	}
	
	function fillColInstruksi(){
		if(isPlacement == 'true'){
			$("#bungaBerjalan").val('');
			$("#h_bungaBerjalan").val('');
			
			$("#jkWaktu").val('');
			$("#h_jkWaktu").val('');
			
			$("#tglPenempatan").val('');
			$("#h_tglPenempatan").val('');
			
			$("#tglPencairan").val('');
			$("#tglPencairanVal").val('');
			$("#h_tglPencairan").val('');
			
			$("#interestRateGross").val('');
//			$("#interestRateGrossVal").val('');
//			$("#h_interestRateGross").val('');
		}else if(isRollover == 'true'){
			$("#bungaBerjalan").val("${currency} "+getFormattedNum('${tdTrans.amount}'));
			$("#h_bungaBerjalan").val("${currency} "+getFormattedNum('${tdTrans.amount}'));
			
			$("#jkWaktu").val('${tdTrans.accruedDays}' + ' Hari');
			$("#h_jkWaktu").val('${tdTrans.accruedDays}' + ' Hari');
			
			
			
//			$("#h_interestRateGross").val(intRateGross+' %');
//			var intRateGrossVal = $("#interestRateGrossVal").val();
//			intRateGrossVal = intRateGrossVal.replace(' %','');
//			$("#interestRateGrossVal").val(intRateGrossVal+' %');
//			$("#interestRateGross").val(getFormattedNum('${depositoVo.newIntRate}') + ' %');
//			$("#h_interestRateGross").val(getFormattedNum('${depositoVo.newIntRate}') + ' %');
			

			
		}else if(isRedeem == 'true' || isBreak == 'true'){
			$("#bungaBerjalan").val('Bagi Hasil Berjalan');
			$("#h_bungaBerjalan").val('Bagi Hasil Berjalan');
			
			$("#jkWaktu").val('Jk. Waktu');
			$("#h_jkWaktu").val('Jk. Waktu');
			
			$("#tglPenempatan").val('Tgl. Penempatan');
			$("#h_tglPenempatan").val('Tgl. Penempatan');
			
			$("#tglPencairan").val('Tgl. Pencairan');
			$("#tglPencairanVal").val('Tgl. Pencairan');
			$("#h_tglPencairan").val('Tgl. Pencairan');
			
			$("#interestRateGross").val('Penyesuaian Tk. Bagi Hasil(%)');
//			$("#h_interestRateGross").val('Penyesuaian Tk. Bagi Hasil(%)');
		}
		
	}
	
	function fillLastTableCol(){
		if(isPlacement == 'true' || isRollover == 'true'){
			$("#jkWaktuDiff").val('');
			$("#h_jkWaktuDiff").val('');
			
			$("#tglPenempatanDiff").val('');
			$("#h_tglPenempatanDiff").val('');
			
			$("#tglPencairanDiff").val('');
			$("#tglPencairanDiffVal").val('');
			$("#h_tglPencairanDiff").val('');
			
			$("#interestRateDiff").val('');
			$("#h_interestRateDiff").val('');
		}else if(isRedeem == 'true'){
			$("#jkWaktuDiff").val('${tdTrans.accruedDays} Hari');
			$("#h_jkWaktuDiff").val('${tdTrans.accruedDays} Hari');
			
			$("#tglPenempatanDiff").val('${tglPenempatan}');
			$("#h_tglPenempatanDiff").val('${tglPenempatan}');
			
			$("#tglPencairanDiff").val('${jatuhTempoMature}');
			$("#tglPencairanDiffVal").val('${jatuhTempoMature}');
			$("#h_tglPencairanDiff").val('${jatuhTempoMature}');
			
			$("#interestRateDiff").val('');
			$("#h_interestRateDiff").val('');
			
			var netInterest = $("#h_netInterest").val();
			netInterest = netInterest.replace('${currency} ','');
			netInterest = netInterest.replace(/,/g,'');
			
			$("#netInterest").val('${currency} '+getFormattedNum(netInterest));
			$("#h_netInterest").val('${currency} '+getFormattedNum(netInterest));
			
		}else if(isBreak == 'true'){
			//var netInterest = $("#h_netInterest").val();
			//netInterest = netInterest.replace('${currency} ','');
			//netInterest = netInterest.replace(/,/g,'');
			//$("#netInterest").val('${currency} '+getFormattedNum(netInterest));
			//$("#h_netInterest").val('${currency} '+getFormattedNum(netInterest));
			
			$("#jkWaktuDiff").val('${jkDiff} Hari');
			$("#h_jkWaktuDiff").val('${jkDiff} Hari');
			
			$("#tglPenempatanDiff").val('${tglPenempatan}');
			$("#h_tglPenempatanDiff").val('${tglPenempatan}');
			
			
			$("#tglPencairanDiff").val('${jatuhTempoBreak}');
			$("#tglPencairanDiffVal").val('${jatuhTempoBreak}');
			$("#h_tglPencairanDiff").val('${jatuhTempoBreak}');
			
			$("#interestRateDiff").val(('${tdTrans.interestRate}') + ' %');
			$("#h_interestRateDiff").val(('${tdTrans.interestRate}') + ' %');
		}
	}
	
	function hideShowPlacementP(){
		var i = 1;
		for(i=1;i<=4;i++){
			if(isPlacement == 'true'){
				$("#placementP"+i).show();
				$("#printPlacementP"+i).show();
			}else{
				$("#placementP"+i).hide();
				$("#printPlacementP"+i).hide();
			}
		}
		
		var penName = $("#penempatanBankName").val();
		var penNoRek = $("#penempatanNoRek").val();
		var penNoRek = $("#penempatanNoRekVal").val();
		var penNamaRek = $("#penempatanNamaRek").val();
		var penNamaRek = $("#penempatanNamaRekVal").val();
		
		$("#penempatanNamaRekVal").val(penName.substring(0,50));
		$("#penempatanBankName").val(penName.substring(0,50));
		$("#penempatanNoRek").val(penNoRek.substring(0,50));
		$("#penempatanNoRekVal").val(penNoRek.substring(0,50));
		$("#penempatanNamaRek").val(penNamaRek.substring(0,50));
	}
	
	function fillInstruksi(){
		var placeIns = "Bila tidak ada instruksi lebih lanjut, mohon pada tanggal jatuh tempo, pokok dan bagi hasil deposito dikirim ke Rekening Sumber Dana. " +
			"Dimohon agar Bilyet Deposito dapat secepatnya dikirimkan kepada Capital Market Service, Treasury & International Banking Group PT. Bank Syariah Indonesia dengan alamat Wisma Mandiri II Jl. Kebon Sirih No.83 Jakarta 10340, Indonesia";
		
		var rollIns = "Bagi Hasil Deposito harap dikirim melalui Kliring/RTGS ke Rekening Nasabah. "+
			"Dimohon agar Bilyet Deposito / Surat Konfirmasi Roll Over dapat secepatnya dikirimkan kepada Capital Market Service, Treasury & International Banking Group PT. Bank Syariah Indonesia dengan alamat Wisma Mandiri II Jl. Kebon Sirih No.83 Jakarta 10340, Indonesia";
 
		var fullRedIns = "Kami mohon kerja sama saudara untuk mencairkan dan melimpahkan Nominal Pokok dan Bagi Hasil Deposito tersebut melalui sarana Kliring/RTGS ke Rekening Nasabah pada Tanggal Pencairan";
		
		var headerdetailval = '${headerdetailval}';

		if(rolloverListType == 'ROLLOVER'){			
			var settlementAmountList = '${settlementAmountList}';
			$("#netAmountVal").val(settlementAmountList);
			$("#netAmountStrippedVal").val(settlementAmountList);
			$("#transTypeVal").val('Deposit Rollover');  
		}
		
		if(isPlacement == 'true'){
			$("#instruction").val('isPlacement');
			$("#instructionview").val('Upon maturity if there is no further instruction, kindly transfer the nominal plus profit sharing to account bellow.');			
		}else if(isRollover == 'true'){
			$("#instruction").val('isRollover');
			//start yusuf 6075 menambahkan deskripsi seperti deposito placement di rollover
			$("#instructionview").val('Upon maturity if there is no further instruction, kindly transfer the nominal plus profit sharing to account bellow.');			
			//end yusuf
			var intRateGross = $("#interestRateGross").val(); 
			console.log(intRateGross);
			intRateGross = intRateGross.replace(' %','');
			$("#interestRateGross").val(intRateGross+' %');
			$("#instructionview").val('Upon maturity if there is no further instruction, kindly transfer the nominal plus profit sharing to account bellow.');			
		}else if(isRedeem == 'true' || isBreak == 'true'){
			$("#instruction").val('isRedeem isBreak');
			$("#instructionview").val(fullRedIns);		
			if(headerdetailval == 'DEPOSITO WITHDRAWAL (LIQUIDATE)'){
				$("#instructionview").val('Kindly transfer the nominal plus profit sharing to account bellow.');		
			}else{
				$("#instructionview").val('Kindly transfer the nominal plus profit sharing to account bellow.');		
			}
		}else{
			$("#instruction").val('isPlacement');
			$("#instructionview").val('');		
		}
	
		
		
	}
	
//	function printPreview(){
//		var variables = [
//		    "custInfoName", "custInfoPhone", "custInfoFaxNum",
//		    "nomor", "headerTransType", "accName", "appDate", 
//		    "secName", "secAddr", "up", "telp", "fax",
//		    "externalRef","pDate","cTransType", "transType",
//		    "nominal", "bungaBerjalan", "accruedDays", "jkWaktu", 
//		    "jkWaktuDiff", "placeDate", "tglPenempatan", "tglPenempatanDiff", 
//		    "maturityDate", "tglPencairan", "tglPencairanDiff", "interestRate",
//		    "interestRateGross", "interestRateDiff", "nasabahBankName", "nasabahNoRek",
//		    "nasabahNamaRek", "penempatanBank", "penempatanBankName", "penempatanNoRek",
//		    "penempatanNamaRek", "instruction", "custodianName", "leftSignName",
//		    "leftSignRole", "rightSignName"
//		];
//		
//		$("#printCustInfoAddress").html('${printAddr}');
//		
//		for(var i=0;i<variables.length;i++){
//			var data = $("#"+variables[i]).val();
//			
//			var oldDataPrefix = variables[i].substring(0,1);
//			var printData = "print"+oldDataPrefix.toUpperCase()+variables[i].substring(1);
//			
//			$("#"+printData).html(data);
//		}
//	}
	
//	function checkFrMonthly(){
//		if(isFrMonthly){
//			var rmFrom = '${redeemMonthlyFrom}';
//			var rmTo = '${redeemMonthlyTo}';
//			
//			$("#placeDate").val(rmFrom);
//			$("#h_placeDate").val(rmFrom);
//			
//			$("#tglPenempatanDiff").val(rmFrom);
//			$("#h_tglPenempatanDiff").val(rmFrom);
//			
//			$("#maturityDate").val(rmTo);
//			$("#h_maturityDate").val(rmTo);
//			
//			$("#tglPencairanDiff").val(rmTo);
//			$("#h_tglPencairanDiff").val(rmTo);
//		}
//	}
	
//	function changeTglValuta(){
//		var tglVal = '${tglValutaRedeem}';
//		if(isRedeem || isBreak){
//			var variables = [ "placeDate", "tglPenempatanDiff" ];
//			
////			$("#placeDate").val("${tglValutaRedeem}");
//			for(var i=0;i<variables.length;i++){
//				$("#"+variables[i]).val(tglVal);
//				$("#h_"+variables[i]).val(tglVal);
//			}
//		}else if(isRollover){
//			$("#placeDate").val(tglVal);
//			$("#h_placeDate").val(tglVal);
//		}
//	}
		
	fillDefaultNo();
	fillPhoneInfo();
	fillFaxNum();
	fillPerihal();
	fillTransType();
	fillColInstruksi();
	fillInstruksi();
	fillLastTableCol();
//	changeTglValuta();
	hideShowPlacementP();
	getSymbolCurrency();
//	printPreview();
//	checkFrMonthly();

});

















