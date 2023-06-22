var ADDRESS_TYPE_1 = '${addressType1}';
var ADDRESS_TYPE_2 = '${addressType2}';

$(function(){
	CUSTOMER_TYPE_I = '${typeIndi}';
	CUSTOMER_TYPE_C = '${typeCorp}';
	
	if("${paramCashProjection}" == "1"){
		$('#pCashprojection').css('display', '');
	}else{
		$('#pCashprojection').css('display', 'none');
	}
	
	$('.specialField').live('keydown', function(event) {
		if ((event.shiftKey && (event.which==55 || event.which==188 || event.which==190 || 
			event.which==222 )) || (event.which==222)) {				
			event.preventDefault();
			return false;
		}
	});
	 
	$('.specialField').live('keyup', function(event) {
		if ((event.shiftKey && (event.which==55 || event.which==188 || event.which==190 || 
			event.which==222 )) || (event.which==222)) {
			
			$(this).val($(this).val().toUpperCase().replace(/&/gi,"").replace(/\?/gi,""));
			$(this).val($(this).val().toUpperCase().replace(/</gi,"").replace(/\?/gi,""));
			$(this).val($(this).val().toUpperCase().replace(/>/gi,"").replace(/\?/gi,""));
			$(this).val($(this).val().toUpperCase().replace(/'/gi,"").replace(/\?/gi,""));
			$(this).val($(this).val().toUpperCase().replace(/"/gi,"").replace(/\?/gi,""));
			return false;
		}
	});
	
	var detailContacts = null;
	detailContacts = ${detailContacts};
	tabContact(detailContacts);
	
	/*if (('${detailContacts}'!='') || ('${detailContacts}'!=null)) {
		detailContacts = ${detailContacts.raw()};
	}
	if (('${detailContacts}'=='') || ('${detailContacts}'==null)) {
		detailContacts = new Array();
	}*/
	
	// enable file upload
//	$( "#form.form" ).attr("enctype", "multipart/form-data");
	
	var currentDate = $('#currentDate').val();
	var closeDialog = function() {
		$("#dialog-message").dialog('close');
	}
	
	$('#continueCust #continueBank #save #saveEdit #mainCancel #confirm #back').button();
	if ($('#customerType').val()==''){
		$('#udfIndividual').css('display', 'none');
		$('#udfCorporate').css('display', 'none');
	}
	
	if ($('#customerType').val()==CUSTOMER_TYPE_I){
		$('#udfIndividual').css('display', 'block');
		$('#udfCorporate').css('display', 'none');
		$('#udfGlobal').css('display', 'none');
	}
	
	if ($('#customerType').val()==CUSTOMER_TYPE_C){
		$('#udfIndividual').css('display', 'none');
		$('#udfCorporate').css('display', 'block');
		$('#udfGlobal').css('display', 'none');
	}


	
	$("#dialog-message-1").css("display","none");
	$("#dialog-message-2").css("display", "none");
	$('#tabs').tabs();
	$('#tabs').css('height','auto' );
	$("#tabs").tabs("disable",1);
	$("#tabs").tabs("disable",3);
	//$("#tabs").tabs("disable",4);
	//$("#tabs").tabs("disable",7);
	$('#detailContact').css("display","none");
	
	if ('${mode}' == 'view'){
		$('#customerType').change(adjustCustomerType);
		adjustCustomerType();
		if ($('#param').val() == 'customer-inquiry') {
			$("#tabs").tabs("select", "#tabs-6");
		}
	}
	
	if ('${mode}' == 'edit'){
		$('#tabs').tabs("disable",5);
		$('#customerType').change(adjustCustomerType);
		adjustCustomerType();
	}

	if ('${mode}' == 'entry'){
		$("#tabs").tabs("disable",2);
		//$("#tabs").tabs("disable",3);
		$('#tabs').tabs("disable",5);
		$('#tabs').tabs("disable",6);
		adjustCustomerType();
		//investorType();
	}
	
	if (('${mode}'=='entry') || (('${mode}'=='edit') && (('${customer?.recordStatus?.decodeStatus()}' == 'Reject') || ($("#status").val() == 'R' )))) {
		//$('input[name=isActive]').attr("disabled", "disabled");
		$('input[name=customer.isActive]').attr("disabled", "disabled");
	}
	
	//$('input[name=isActive]').change(function(){
	$('input[name=customer.isActive]').change(function(){

		var isActiveTrue = function() {
			$("input[name=customer.isActive]")[0].checked = true;
			$("input[name='customer.isActive']").val($("input[name='customer.isActive']").is(":checked"));
			$("#dialog-message").dialog("close");
		} 
		
		$("input[name='customer.isActive']").val($("input[name='customer.isActive']:checked").val());
		
		
		if ('${mode}' == 'edit') {
			
			if($("input[name=customer.isActive]")[0].checked ==false || $("input[name=customer.isActive]")[0].checked == 'false'){
				
				$.get("@{isActiveAccountExist()}", {'customerNo':$('#customerNo').val()}, function(data){
					checkRedirect(data);
					if(data==false || data == 'false'){
						

						var amlKey = $('#amlKey').val();
						if (amlKey) {
							$.get("@{checkAmlClose()}", {'amlKey':amlKey}, function(data){
								if(data==false || data == 'false'){
									
									messageAlertOk("Please fill Close Reason in AML Maintenance (KL2002)", "ui-icon ui-icon-notice", "Confirmation Message", isActiveTrue);
									
								}else{
									
									messageAlertYesNo("Are you sure to inactive data ?", "ui-icon ui-icon-notice", "Confirmation Message", closeDialog, isActiveTrue);
									
									if($("#isActive2").attr('checked')){
										$('#errActive').html('');
										$("input[name='customer.isActive']").val(false);
										$("input[name=customer.isActive]")[1].checked = true;
									}
								}
							});
						} else {
							
							messageAlertYesNo("Are you sure to inactive data ?", "ui-icon ui-icon-notice", "Confirmation Message", closeDialog, isActiveTrue);
							
							if($("#isActive2").attr('checked')){
								$('#errActive').html('');
								$("input[name='customer.isActive']").val(false);
								$("input[name=customer.isActive]")[1].checked = true;
							}
						}
					}else{
						$('#errActive').html('Can not change to In Active');
						$("input[name='customer.isActive']").val(true);
						$("input[name=customer.isActive]")[0].checked = true;
					}
				});
			}
		}
		
		/*var isActiveTrue = function() {
			$("#isActive1").attr("checked", true);
			$("input[name='customer.isActive']").val($("input[name='isActive']:checked").val());
			$("#dialog-message").dialog("close");
		}  
		$("input[name='customer.isActive']").val($("input[name='isActive']:checked").val());
		
		//checkAccountActive();
		if ($("input[name='customer.isActive']").val()=='false'||$("input[name='customer.isActive']").val()==false){
			messageAlertYesNo("Are you sure to inactive data ?", "ui-icon ui-icon-notice", "Confirmation Message", closeDialog, isActiveTrue);
		}*/
	});
	
	$('.buttons input:button').button();
	
	if ( '${mode}' == 'view' || '${confirming}'=='true' || '${taskId}' != '') {
		$('#saveCon').css("display","none");
		$('#cancelCon').css("display","none");
		$('#saveConCorp').css("display","none");
		$('#cancelConCorp').css("display","none");
	} else {
		$('#closeCon').css("display","none");
		$('#closeConCorp').css("display","none");
	}
	
	$('#continueCust').click(function() {	
		location.href='@{Accounts.entry(customer.customerKey)}';
		return false;		
	});
	
	$('#continueBank').click(function() {	
		location.href='@{BankAccounts.entry(customer.customerKey)}';
		return false;		
	});
	
	$('#continueInvt').click(function() {
		location.href='@{RegistryInvestment.entry(customer.customerKey)}';
		return false;		
	});

	$('#backCustBankInvt').click(function() {	
		//history.back();
		if ('${param}' != "") {
			if ('${param}' == 'dedupe') {
				location.href='@{dedupe()}';
			} else if('${param}'=='edit') {
				location.href='@{list()}?mode=edit&param=edit';
			} else {
				location.href='@{list()}#{if mode}?mode=${mode}&param=${param}#{/}';
			}
		} else {
			location.href='@{list()}?mode=view';
		}
		
		return false;
	});
	
	$('#addresses').tabs();
	$('#accounts').tabs();

	$('#corporate_only').tabs();
	
	//$('.calendar').datepicker();
	
	// contact detail initialize ============================================================================================
	$("#detailContact").dialog({
		autoOpen:false,
		heigth:'700px',
		width:'600px',
		resizable:false,
		modal:true			
	});
	
	$("#detailContactCorp").dialog({
		autoOpen:false,
		heigth:'700px',
		width:'auto',
		resizable:false,
		modal:true			
	});
	// ======================================================================================================================
		
	// custody account table initialize =====================================================================================
	var tableCustAccount = 
		$('#grid-cust-account').dataTable({
			aoColumnDefs: [ {bVisible:false,aTargets:[0]},
			                {bSortable:false,aTargets:[1,2,3,4]}
				          ],
			bJQueryUI:true,
			bAutoWidth: false,
			bSort: false,
			bFilter: true,
			bInfo: false,
			bJQueryUI:true,
			bPaginate: false,
			bSearch: false,
			bLengthChange:false,
			iDisplayLength:10
		});
	// ======================================================================================================================
		
	// bank account table initialize =========================================================================================
	var tableBankAccount = 
		$('#grid-bank-account').dataTable({
			aoColumnDefs: [ {bVisible:false,aTargets:[0]},
			                {bSortable:false,aTargets:[1,2,3,4]}
				          ],
			bJQueryUI:true,
			bAutoWidth: false,
			bSort: false,
			bFilter: true,
			bInfo: false,
			bJQueryUI:true,
			bPaginate: false,
			bSearch: false,
			bLengthChange:false,
			iDisplayLength:10
		});
	// ======================================================================================================================
		
	// investment account table initialize ==================================================================================
	var tableInvtAccount = 
		$('#grid-invt-account').dataTable({
			aaSorting:[[0,'asc']],
			aoColumnDefs: [ {bVisible:false,aTargets:[0]},
			                {bSortable:false,aTargets:[1,2,3]}
				       	  ],
	      	bJQueryUI:true,
			bAutoWidth: false,
			bFilter: true,
			bInfo: false,
			bJQueryUI:true,
			bPaginate: false,
			bSearch: false,
			bLengthChange:false,
			iDisplayLength:10
		});
	// ======================================================================================================================
	$('#grid-invt-account').removeAttr('style');
	$('#grid-invt-account tbody tr').die('click');
	$('#grid-invt-account tbody tr td').live('click', function(){
		var rows = tableInvtAccount.fnGetData(this.parentNode);
		//var row = $(this).parents('tr');
		//var rowNumber = tableInvtAccount.fnGetPosition(row[0]);
		//var digitAmount = $("#grid-invt-account tbody tr td input[name='invs.["+rowNumber+"].rgProduct.amountRoundValue']").val();
		//var digitUnit = $("#grid-invt-account tbody tr td input[name='invs.["+rowNumber+"].rgProduct.unitRoundValue']").val();
		//var digitPrice = $("#grid-invt-account tbody tr td input[name='invs.["+rowNumber+"].rgProduct.priceRoundValue']").val();
		//var typeAmount = $("#grid-invt-account tbody tr td input[name='invs.["+rowNumber+"].rgProduct.amountRoundType']").val();
		//var typeUnit = $("#grid-invt-account tbody tr td input[name='invs.["+rowNumber+"].rgProduct.unitRoundType']").val();
		//var typePrice = $("#grid-invt-account tbody tr td input[name='invs.["+rowNumber+"].rgProduct.priceRoundType']").val();
		//alert(digitAmount + " & " + typeAmount);
		//formatAmount(digitAmount, typeAmount);
		//formatUnit(digitUnit, typeUnit);
		//formatPrice(digitPrice, typePrice);
		location.href='@{Customers.customerInquiryEnhancement()}?accountNumber=' + rows[0] + '&id=${customer?.customerKey}&param=${param}';
		/*
		$.post('@{Customers.customerInquiryEnhancement()}?accountNumber=' + rows[0], function(data) {
			$('#investor-inquiry').html(data);
			$("div[id$='investment-account']").css("display", "none");
			//$("div[id$='investor-inquiry']").css("display", "");
			//$("div[id$='button-investor-inquiry']").css("display", "");
			formatAmount(digitAmount, typeAmount);
			formatUnit(digitUnit, typeUnit);
			formatPrice(digitPrice, typePrice);
			//location.href='@{Customers.customerInquiryEnhancement()}'
			return false;
		});
		*/
		
	});
	
			
	// branch lookup ========================================================================================================
	$('#branchNo').lookup({
		list:'@{Pick.branches()}',
		get:{
			url:'@{Pick.branch()}',
			success: function(data) {
				if (data) {
					$('#branchNo').removeClass('fieldError');
					$('#branchKey').val(data.code);
					$('#branchName').val(data.description);
					$('#h_branchName').val(data.description);
				}
			},
			error : function(data) {
				$('#branchNo').addClass('fieldError');
				$('#branchName').val('NOT FOUND');
				$('#branchNo').val('');
				$('#branchKey').val('');
				$('#h_branchName').val('');
			}
		},
		key:$('#branchKey'),
		description:$('#branchName'),
		help:$('#branchHelp'),
		nextControl:$('#firstName')
	});
	// ======================================================================================================================
	
	// branch action change =================================================================================================
	$('#branchNo').change(function(){
		if ($('#branchNo').val() == "") {
			$('#branchNo').val("");
			$('#branchKey').val("");
			$('#branchName').val("");
			$('#h_branchName').val("");
		}
	});
	// ======================================================================================================================
	
	// birthdate validation can not be greather than current date ===========================================================
	$('#birthDate').change(function(){
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if (!$(this).hasClass('fieldError')){
			if (compareDateMustBeforeApplicationDate(thisId, value)){
				if (getAge($('#birthDate').datepicker('getDate')) < 17){
					$(this).addClass('fieldError');
					$("#birthDateError").html("Must have 17 years old or older!");
					return false;
				} else {
					$(this).removeClass('fieldError');
					$("#birthDateError").html("");	
				}
			};
		}
		
	});
	// ======================================================================================================================
	
	// expDate 1 validation must be greather than expDate ===================================================================
	$('#identification1Expiry').change(function(){
		
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ($(this).val()!='' && !$(this).hasClass('fieldError')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
	});
	// ======================================================================================================================
	
	// expDate 2 validation must be greather than expDate ===================================================================
	$('#identification2Expiry').change(function(){
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ($(this).val()!='' && !$(this).hasClass('fieldError')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
	});
	// ======================================================================================================================
	
	// expDate 3 validation must be greather than expDate ===================================================================
	$('#identification3Expiry').change(function(){
		
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ($(this).val()!='' && !$(this).hasClass('fieldError')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
	});
	// ======================================================================================================================
		
	// expDate 4 validation must be greather than expDate ===================================================================
	$('#identification4Expiry').change(function(){
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ($(this).val()!='' && !$(this).hasClass('fieldError')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
		
	});
	// ======================================================================================================================
	
	
	// npwp validation ======================================================================================================
	$('#npwpDate').change(function(){
		
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		//di screen bukan mandatory, tapi main bilang required.......................
		//remove aja errornya
		$(this).removeClass("fieldError");
		if (!$(this).hasClass('fieldError')){
			compareDateMustBeforeApplicationDate(thisId, value);
		}
	});
	// ======================================================================================================================
		
	// date of company estb validation can not be greather than current date ================================================
	$('#dateCompEstb').change(function(){
		
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if (!$(this).hasClass('fieldError')){
			compareDateMustBeforeApplicationDate(thisId, value);
		}
	});
	// ======================================================================================================================
		
	$('#expDateBusinesRegCert').change(function(){
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if (!$(this).hasClass('fieldError')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
	});
	
	// skd expiry date validation can not be greather than current date =====================================================
	$('#skdDate').change(function(){
		
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if($(thisId).val() != ""){
			if (!$(this).hasClass('fieldError')){
				compareDateMustAfterApplicationDate(thisId, value);
			}
		}
		
	});
	// ======================================================================================================================
	
	// customer type action change ==========================================================================================
	$('#customerType').change(function() {
		var select_customer; 
        select_customer = $('#customerType').val(); 	
  		//alert(select_customer);	    		       
		//if ($('#customerType').val() != "") {
		//	$('#customerType').attr("disabled", "disabled");
		//}
		
  		if  (select_customer == CUSTOMER_TYPE_C) {
  			$(".corporate").val("");
  			$('#h_placeCompEstbDesc').val('');
			$('#h_placeCompEstbAreaDesc').val('');
			$('#h_originCountryDesc').val('');
        	$(".all").val("");
        	$(".empty").val("");
        	$('.corporate').attr('checked', false);
        	$('input[name="sourceOfFunds"]').attr('checked', false);
        	$('input[name="investObjs"]').attr('checked', false);
        	adjustCountryCorp();
    		adjustSupplementDocReq();
    		adjustOtherCorp();
    		$('#udfCorporate').css('display', 'block');
    		$('#udfIndividual').css('display', 'none');
    		$('#udfGlobal').css('display', 'none');
		} else if  (select_customer == CUSTOMER_TYPE_I) {
			$(".individual").val("");
			$('#h_birthPlaceStateDesc').val('');
			$('#h_birthPlaceAreaDesc').val('');
			$(".all").val("");
        	$(".empty").val("");
        	$('.individual').attr('checked', false);
        	$('input[name="sourceOfFunds"]').attr('checked', false);
        	$('input[name="investObjs"]').attr('checked', false);
        	adjustCountry();
        	adjustOtherIndividual();
        	$('#udfCorporate').css('display', 'none');
    		$('#udfIndividual').css('display', 'block');
    		$('#udfGlobal').css('display', 'none');
		} else {
			//$(".corporate").val("");
        	$(".all").val("");
        	$(".empty").val("");
        	$('input[name="sourceOfFunds"]').attr('checked', false);
        	$('input[name="investObjs"]').attr('checked', false);
        	$('#udfCorporate').css('display', 'none');
    		$('#udfIndividual').css('display', 'none');
    		$('#udfGlobal').css('display', 'block');
		}
  		$('#customerCategory').val('${directCat}');
  		mandatoryDirectIndirect();
  		$('#custRetailFlag2').attr('checked', 'checked');
  		custRetailOfStatus();
  		$('#jointDate').val('${currentDate}');
		adjustCustomerType();
		$('.all').attr('checked', false);
		$('#attachFile').disabled();
		$('#investorType').val("");
		$('#contact #grid-contact').dataTable().fnClearTable();
		$('#contact #grid-contact-corp').dataTable().fnClearTable();
		$('#gridChargeInvoice').dataTable().fnClearTable();
		if ('${mode}'=='edit'){
			$('#flagChangeBankAccount').attr('checked', false);
			$('#bankAccountInvoice').val($('#oriBankAccountInvoice').val());
			$('#bankAccountInvoiceKey').val($('#oriBankAccountInvoiceKey').val());
			$('#bankAccountInvoiceDesc').val($('#oriBankAccountInvoiceDesc').val());
			$('#currencyInvoice').val($('#oriCurrencyInvoice').val());
			$('#currencyInvoiceId').val($('#oriCurrencyInvoice').val());
			$('#currencyInvoiceDesc').val($('#oriCurrencyInvoiceDesc').val());
		}
		var checkError = $("input").hasClass('fieldError'); {
			if (checkError){
				$('input').removeClass('fieldError');
			}
		}
		// ==================================================================================================================
		//$('#customerTypeHide').val($('#customerType').val());
	});
	// ======================================================================================================================
	
	// legal domicile change action =========================================================================================
	/*
	 * cek dengan dokument field skd mandatory
	 */
	 
	/*$("p[id=skdNoLi] label span").html("");
	$("p[id=skdDateLi] label span").html("");
	if ($('#legalDomicile').val() == 'LEGAL_DOMICILE-2') {
		$("p[id=skdNoLi] label span").html("*");
		$("p[id=skdDateLi] label span").html("*");
	}
	
	$('#legalDomicile').change(function(){
		//alert("1. " + $('#legalDomicile').val());
		if ($('#legalDomicile').val() == 'LEGAL_DOMICILE-2') {
			//alert("2. " + $('#legalDomicile').val());
			$("p[id=skdNoLi] label span").html("*");
			$("p[id=skdDateLi] label span").html("*");
		} else {
			$("p[id=skdNoLi] label span").html("");
			$("p[id=skdDateLi] label span").html("");
		}
		
		$('#originCountry').removeClass('fieldError');
		$('#originCountry').val("");
		$('#originCountryId').val("");
		$('#originCountryDesc').val("");
		$('#h_originCountryDesc').val("");
	});*/
	// ======================================================================================================================
	
	
	// for check identification type 1 in tab contact ====================================================================
	$('#identificationType1').change(function() {
		checkIdentificationTypeIndv("#identificationType", "Identification");
		$('#identificationType1Hide').val($(this).val());
		$('#identification1No').val('');
		$('#identification1Expiry').val('');
		$('#identification1Expiry').removeClass('fieldError');
		$('#identification1ExpiryError').html('');
	});
	// ======================================================================================================================
	
	// for check identification type 2 in tab contact ====================================================================
	$('#identificationType2').change(function() {
		checkIdentificationTypeIndv("#identificationType", "Identification");
		$('#identification2No').val('');
		$('#identification2Expiry').val('');
		$('#identification2Expiry').removeClass('fieldError');
		$('#identification2ExpiryError').html('');
	});
	// ======================================================================================================================
	
	// for check identification type 3 in tab contact ====================================================================
	$('#identificationType3').change(function() {
		checkIdentificationTypeIndv("#identificationType", "Identification");
		$('#identification3No').val('');
		$('#identification3Expiry').val('');
		$('#identification3Expiry').removeClass('fieldError');
		$('#identification3ExpiryError').html('');
	});
	// ======================================================================================================================
		
	// for check identification type 4 in tab contact ====================================================================
	$('#identificationType4').change(function() {
		checkIdentificationTypeIndv("#identificationType", "Identification");
		$('#identification4No').val('');
		$('#identification4Expiry').val('');
		$('#identification4Expiry').removeClass('fieldError');
		$('#identification4ExpiryError').html('');
	});
	// ======================================================================================================================
	
	// for copy value full name to first, middle, last name =================================================================
	
	
	/*$('#customerName').change(function() {
		 //$("#firstName").val("");
		$("#middleName").val("");
		$("#lastName").val("");
		$("#firstNameHide").val("");
		$("#middleNameHide").val("");
		$("#lastNameHide").val("");
		
		var str = $('#customerName').val();
		var strArray = new Array();
		var strSplit = str.split(" ");
		var a=0;
		
		if ($('#customerType').val() == 'CUSTOMER_TYPE_I') {
			
			for (var i=0; i<strSplit.length; i++) {
				if (strSplit[i] != "") {
					strArray[a] = strSplit[i];
					a+=1;
				}
			}
			
			$("#firstName").val(strArray[0]);
			$("#firstNameHide").val($("#firstName").val());
			
			if (strArray.length >= 2) {
				$("#firstName").val(strArray[0]);
				$("#lastName").val(strArray[strArray.length-1]);
				
				$("#firstNameHide").val($("#firstName").val());
				$("#lastNameHide").val($("#lastName").val());
				
				var String = "";
				for (var j=1; j<strArray.length-1; j++) {
					String = String + strArray[j] + " ";
				}
				$("#middleName").val(String);
				$("#middleNameHide").val($("#middleName").val());
			}
		} 
	});*/
	// ======================================================================================================================
		
		// ============ SECTION TAB CHARGE INVOICE ============ //
		// for temporary 
		var customerCharges = '';
	if ('${dataCustomerCharges}'=='')
		dataCustomerCharges = new Array();
	if ('${dataCustomerCharges}'!='')
		dataCustomerCharges = ${dataCustomerCharges.raw()}
	var tableChargeInvoice = 
		$('#gridChargeInvoice').dataTable({
			aaData: dataCustomerCharges,
			aoColumns: [ 
			             {
				             fnRender: function(obj) {
								  	var controls;
										controls = obj.aData.invoiceCharge.lookupDescription;
										controls += '<input type="hidden" name="customerCharges[' + obj.iDataRow + '].invoiceCharge.lookupDescription" value="' + obj.aData.invoiceCharge.lookupDescription + '" />';
										controls += '<input type="hidden" name="customerCharges[' + obj.iDataRow + '].invoiceCharge.lookupId" value="' + obj.aData.invoiceCharge.lookupId + '" />';
										return controls;
							 }
			             },
			            /*  {
				             fnRender: function(obj) {
								  	var controls;
										controls = obj.aData.taxMaster.taxCode;
										controls += '<input type="hidden" name="customerCharges[' + obj.iDataRow + '].taxMaster.taxCode" value="' + obj.aData.taxMaster.taxCode + '" />';
										controls += '<input type="hidden" name="customerCharges[' + obj.iDataRow + '].taxMaster.taxKey" value="' + obj.aData.taxMaster.taxKey + '" />';
										controls += '<input type="hidden" name="customerCharges[' + obj.iDataRow + '].taxMaster.description" value="' + obj.aData.taxMaster.description + '" />';
								  	return controls;
							 }
			             }, */
						 {
			            	 sClass: 'numeric',
							 fnRender: function(obj) {
								  	var controls;
										//controls = obj.aData.minimumValue;
										controls = $('#dummy').autoNumericSet( obj.aData.minimumValue, {aPad:true,mDec:2}).val();
										controls += '<input type="hidden" name="customerCharges[' + obj.iDataRow + '].minimumValue" value="' + obj.aData.minimumValue + '" />';
								  	return controls;
							 }
						 },
						 {
			                	sClass: 'numeric',
				            	fnRender: function(obj) {
				            		var controls;
				            		if ((obj.aData.maximumValue == null) || (obj.aData.maximumValue == "")|| (obj.aData.maximumValue == "MAX")){
				            			controls = "MAX";
				            		} else {
				            			controls = $('#dummy').autoNumericSet(obj.aData.maximumValue, {aPad:true,mDec:2}).val();
				            			controls += '<input type="hidden" name="customerCharges[' + obj.iDataRow + '].maximumValue" value="' + obj.aData.maximumValue + '" />';
				            			
				            		}
				            		
				            		return controls;
				            	}
						},
						{
							fnRender: function(obj) {
						 	var controls;
						 	controls = '<center><input id="deleteButton" type="button" value="delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
						 	controls += '<input type="hidden" name="customerCharges[' + obj.iDataRow + '].customerChargeKey" value="' + obj.aData.customerChargeKey + '" />';
						// 	controls += '<input type="hidden" name="accountCharges[' + obj.iDataRow + '].account.custodyAccountKey" value="' + obj.aData.account.custodyAccountKey + '" />';
						 	return controls;
						 				
						 	}
						  }
						],
						aaSorting:[[1,'asc']],
			        	bAutoWidth: false,		
			        	bDestroy: true,
			        	bFilter: false,
			        	bInfo: false,
			        	bJQueryUI: true,
			        	bPaginate: false,
			        	bSearch: false,
			        	bLengthChange: false   
						//sPaginationType: 'full_numbers',
						//bInfo: true,
						//bPaginate: true
						//iDisplayLength:7               						
	});
	
	// ---- Process when click in grid data ---- //
	$('#listChargeInvoice #gridChargeInvoice').removeAttr('style');
	$('#listChargeInvoice #gridChargeInvoice tbody tr td').die('click');
	$('#listChargeInvoice #gridChargeInvoice tbody tr td').live('click', function(){
		var rowPos= $(this).parents('tr');
		if (tableChargeInvoice.fnGetNodes().length == 0) { // tambah pengecekan ini~~
			return false;
		}
		var rowPosNumber = tableChargeInvoice.fnGetPosition(rowPos[0]);
		var pos = tableChargeInvoice.fnGetPosition(this);
		
		if (pos[1] != 3) {
			dataChargeInvoice = tableChargeInvoice.fnGetData(this.parentNode);
			$("#detailChargeInvoice #chargeInvoiceForm #chargeGroup").attr("disabled","disabled");
			$("#detailChargeInvoice #chargeInvoiceForm #rowPosition").val(rowPosNumber);
			$("#detailChargeInvoice #chargeInvoiceForm #customerChargeKey").val(dataChargeInvoice.customerChargeKey);
			//$("#detailChargeInvoice #chargeInvoiceForm #custodyAccountKey").val(dataChargeInvoice.account.custodyAccountKey);
			$("#detailChargeInvoice #chargeInvoiceForm #chargeGroup").val(dataChargeInvoice.invoiceCharge.lookupId);
			$("#detailChargeInvoice #chargeInvoiceForm #chargeGroupDesc").val(dataChargeInvoice.invoiceCharge.lookupDescription);
			$("#detailChargeInvoice #chargeInvoiceForm #minCharge").autoNumericSet( dataChargeInvoice.minimumValue, {aPad:true,mDec:2}).val();
			if((dataChargeInvoice.maximumValue==null) || (dataChargeInvoice.maximumValue=="")) {
				$("#detailChargeInvoice #chargeInvoiceForm #maxCharge").val("");
				$("#detailChargeInvoice #chargeInvoiceForm #maxCharge").attr("disabled", "disabled");
				$("#detailChargeInvoice #chargeInvoiceForm #cekBoxMaxCharge").attr("checked", true);
				if ('${confirming}'=='true'){
					$("#detailChargeInvoice #chargeInvoiceForm #maxCharge").val("MAX");
				}
			} else {
				$("#detailChargeInvoice #chargeInvoiceForm #maxCharge").autoNumericSet( dataChargeInvoice.maximumValue, {aPad:true,mDec:2}).val();
				if ('${mode}'!='view' && '${confirming}'!='true') {
					$("#detailChargeInvoice #chargeInvoiceForm #maxCharge").attr("disabled", false);
				}
				$("#detailChargeInvoice #chargeInvoiceForm #cekBoxMaxCharge").attr("checked", false);
			} 
			
			$("#detailChargeInvoice #chargeInvoiceForm #minChargeStripped").val(dataChargeInvoice.minimumValue);
			$("#detailChargeInvoice #chargeInvoiceForm #maxChargeStripped").val(dataChargeInvoice.maximumValue);
			//$("#detailChargeInvoice #chargeInvoiceForm #taxCode").val(dataChargeInvoice.taxMaster.taxCode);
			//$("#detailChargeInvoice #chargeInvoiceForm #taxKey").val(dataChargeInvoice.taxMaster.taxKey);
			//$("#detailChargeInvoice #chargeInvoiceForm #taxName").val(dataChargeInvoice.taxMaster.description);
			//editChargeInvoice(pos[0]);
			$("#detailChargeInvoice").dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
		}
	});
	
	// ----- Declare dialog box for input data Charge Invoice ---- //
	$( "#detailChargeInvoice" ).dialog({
		autoOpen:false,
		heigth:'230px',
		width:'580px',
		resizable:false,
		modal:true			
	});
	
	// ------- Process When click button add charge invoice ---- //				
	/*#{if readOnly}
		$('#addChargeInvoice').hide();
	#{/if}
*/	$('#addChargeInvoice').live('click', function() {
		selectedRow = null;
		$('#detailChargeInvoice #chargeInvoiceForm #chargeGroup').val("");
		$('#chargeInvoiceForm #chargeGroup').attr('disabled', false);
		$("#detailChargeInvoice").dialog('open');
		$('.ui-widget-overlay').css('height',$('body').height());
		$("#detailChargeInvoice input:text").val(""); 
		$("#detailChargeInvoice input:hidden").val("");
		$("#errmsgChargeGroup").html('');
		
		$("#errmsgMinCharge").html('');
		$("#cekBoxMaxCharge").attr("checked", false);
		$("#maxCharge").attr("disabled", false);
		$('p[id="pMaxCharge"] label span').html(' *');
		$("#detailChargeInvoice #chargeInvoiceForm #chargeGroup").removeClass('fieldError');
		return false;
	});
	
	
	// ---- Process Delete Charge Invoice in grid data --- //
	$('#listChargeInvoice #gridChargeInvoice tbody tr #deleteButton').live('click', function() {
		if ('${mode}' != 'view') {
			var row = $(this).parents('tr');
			var rowNumber = tableChargeInvoice.fnGetPosition(row[0]);
			var deleteChargeInvoice = function() {
				tableChargeInvoice.fnDeleteRow(rowNumber);
				var idTable = $("#listChargeInvoice #gridChargeInvoice");
				var trs = $("tr", idTable);
				$.each(trs, function(idx, data){
					var hiddens = $("[type=hidden]", $(this));
						$(hiddens).eq(0).attr("name", "customerCharges["+(idx-1)+"].invoiceCharge.lookupDescription");
						$(hiddens).eq(1).attr("name", "customerCharges["+(idx-1)+"].invoiceCharge.lookupId");
						//$(hiddens).eq(2).attr("name", "customerCharges["+(idx-1)+"].taxMaster.taxCode");
						//$(hiddens).eq(3).attr("name", "customerCharges["+(idx-1)+"].taxMaster.taxKey");
						//$(hiddens).eq(3).attr("name", "customerCharges["+(idx-1)+"].taxMaster.description");
						$(hiddens).eq(2).attr("name", "customerCharges["+(idx-1)+"].minimumValue");
						$(hiddens).eq(3).attr("name", "customerCharges["+(idx-1)+"].maximumValue");
						$(hiddens).eq(4).attr("name", "customerCharges["+(idx-1)+"].customerChargeKey");
				});
				$("#dialog-message").dialog("close");
			}
		
			messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteChargeInvoice, closeDialog);
		}
		
	});
	
	// ----- All Adding Process in Charge Invoice in Dialog Box ---- //
	$("#chargeInvoiceForm #chargeGroup").change(function() {
		// SUBSTRING WHEN LOOKUPDESCRIPTION/OPTION FROM DROPDOWN SHOW ON DATATABLES SCREEN
		var selected = $('#chargeInvoiceForm #chargeGroup').val();
		var selectedText = $("#chargeInvoiceForm #chargeGroup option[value=" + selected + "]").text();
		if (selectedText != '') {
				selectedText = jQuery.trim(selectedText);
				selectedText = selectedText.substring(jQuery.trim(selectedText).indexOf('-') + 1);
		}
		$("#contactForm #chargeGroupDesc").val(selectedText);
		// END OF SUBSTRING
		chargeGroupDescription = selectedText;
		$('#chargeInvoiceForm #chargeGroupDesc').val(chargeGroupDescription);
	});

	// ------- Process When click button save charge invoice in dialog box ---- //			
	$('#saveCharge').die("click");
	$('#saveCharge').live("click", function(){
		if ( ($("#detailChargeInvoice #chargeInvoiceForm #chargeGroup").val() == "")||
				//($("#detailChargeInvoice #chargeInvoiceForm #taxCode").val()=="")||
				( $("#detailChargeInvoice #chargeInvoiceForm #minCharge").val()=="") || 
				($("#detailChargeInvoice #chargeInvoiceForm #maxCharge").hasClass('fieldError')) || 
				((!$('#detailChargeInvoice #chargeInvoiceForm #cekBoxMaxCharge').is(':checked'))&&($("#detailChargeInvoice #chargeInvoiceForm #maxCharge").val()==''))) {
			
			 if ( $("#detailChargeInvoice #chargeInvoiceForm #chargeGroup").val() == "") {
				//alert("Max Range, Tax Code or Value can not be Empty! ")
				$("#errmsgChargeGroup").html('Required');
				
			 } else {
				 $("#errmsgChargeGroup").html('');
			 }
			 
			
			/*  if ($("#detailChargeInvoice #chargeInvoiceForm #taxCode").val()==""){
				$("#errmsgTaxCode").html('Required');
				
			 } else {
				 $("#errmsgTaxCode").html('');
			 } */
			
			 if ( $("#detailChargeInvoice #chargeInvoiceForm #minCharge").val()=="") {
				$("#errmsgMinCharge").html('Required');
				
			 } else {
				 $("#errmsgMinCharge").html('');
			 }
		
			 if ((!$('#detailChargeInvoice #chargeInvoiceForm #cekBoxMaxCharge').is(':checked'))&&($("#detailChargeInvoice #chargeInvoiceForm #maxCharge").val()=='')){
				$('#errmsgMaxCharge').html('Required');
			} else {
				$('#errmsgMaxCharge').html('');
			}
			
			 if ($("#detailChargeInvoice #chargeInvoiceForm #maxCharge").hasClass('fieldError')){
				 $("#detailChargeInvoice #chargeInvoiceForm #maxCharge").focus();
			} 
			
			return false;
		} else {
			$("#errmsgChargeGroup").html('');
			$("#errmsgTaxCode").html('');
			$("#errmsgMinCharge").html('');
			var tableChargeInvoice = $('#listChargeInvoice #gridChargeInvoice').dataTable();
			var rowPosition = $("#detailChargeInvoice #chargeInvoiceForm #rowPosition").val();
			var chargeGroupDescription = $("#detailChargeInvoice #chargeInvoiceForm #chargeGroupDesc").val();
			var rows = tableChargeInvoice.fnGetNodes().length;
			var found = false;
			for (i = 0; i < rows; i++) {
				var cell = tableChargeInvoice.fnGetData(i);
				if ((chargeGroupDescription == $(cell[0]).val() && rowPosition == "") ) {					
					$("#detailChargeInvoice #chargeInvoiceForm #chargeGroup").addClass('fieldError');
					$("#errmsgChargeGroup").html('Data already exist ');
					found = true;
					break;
				} else {
					$("#detailChargeInvoice #chargeInvoiceForm #chargeGroup").removeClass('fieldError');
					$("#errmsgChargeGroup").html('');
				}
			}
			
			
			
			if (!found) {
				if (rowPosition != "") {
					var dataChargeInvoice = tableChargeInvoice.fnGetData(parseFloat(rowPosition));
					tableChargeInvoice.fnUpdate(((dataChargeInvoice.invoiceCharge.lookupId = $("#detailChargeInvoice #chargeInvoiceForm #chargeGroup").val()) && (dataChargeInvoice.invoiceCharge.lookupDescription = $("#detailChargeInvoice #chargeInvoiceForm #chargeGroupDesc").val())) ,parseFloat(rowPosition),0);	
					//tableChargeInvoice.fnUpdate(((dataChargeInvoice.taxMaster.taxCode = $("#detailChargeInvoice #chargeInvoiceForm #taxCode").val()) && (dataChargeInvoice.taxMaster.taxKey = $("#detailChargeInvoice #chargeInvoiceForm #taxKey").val()) && (dataChargeInvoice.taxMaster.description = $("#detailChargeInvoice #chargeInvoiceForm #taxName").val())) ,parseFloat(rowPosition),1);
					tableChargeInvoice.fnUpdate(dataChargeInvoice.minimumValue = $("#detailChargeInvoice #chargeInvoiceForm #minChargeStripped").val(),parseFloat(rowPosition),1);
					tableChargeInvoice.fnUpdate(dataChargeInvoice.maximumValue = $("#detailChargeInvoice #chargeInvoiceForm #maxChargeStripped").val(),parseFloat(rowPosition),2);
					dataChargeInvoice.customerChargeKey = $("#detailChargeInvoice #chargeInvoiceForm #customerChargeKey").val();
					//dataChargeInvoice.account.custodyAccountKey = $("#detailChargeInvoice #chargeInvoiceForm #custodyAccountKey").val();
				} else {
					
					var dataChargeInvoice = new Object();
					dataChargeInvoice.customer = new Object();
					dataChargeInvoice.invoiceCharge = new Object();
					//dataChargeInvoice.taxMaster = new Object();
					
					dataChargeInvoice.customerChargeKey = $("#detailChargeInvoice #chargeInvoiceForm #customerChargeKey").val();
					dataChargeInvoice.customer.customerKey = $("#detailChargeInvoice #chargeInvoiceForm #customerKey").val();
					dataChargeInvoice.invoiceCharge.lookupId = $("#detailChargeInvoice #chargeInvoiceForm #chargeGroup").val();
					dataChargeInvoice.invoiceCharge.lookupDescription = $("#detailChargeInvoice #chargeInvoiceForm #chargeGroupDesc").val();
					dataChargeInvoice.minimumValue = $("#detailChargeInvoice #chargeInvoiceForm #minChargeStripped").val();
					dataChargeInvoice.maximumValue = $("#detailChargeInvoice #chargeInvoiceForm #maxChargeStripped").val();					
					//dataChargeInvoice.taxMaster.taxCode = $("#detailChargeInvoice #chargeInvoiceForm #taxCode").val();
					//dataChargeInvoice.taxMaster.taxKey = $("#detailChargeInvoice #chargeInvoiceForm #taxKey").val();
					//dataChargeInvoice.taxMaster.description = $("#detailChargeInvoice #chargeInvoiceForm #taxName").val();
					tableChargeInvoice.fnAddData(dataChargeInvoice);
				}
				
				$('#detailChargeInvoice').dialog('close');		
				return false;
			}
			
		}
	});
	
	// ============= IN TAB INSTUTIONAL ========================== //
	$('.tabs').tabs();

	$('#taxId').lookup({
		list:'@{Pick.lookupByUdfs()}?group=TAX_ID',
		get:{
			url:'@{Pick.lookupByUdf()}?group=TAX_ID',
			success: function(data) {
				$('#taxId').removeClass('fieldError');
				$('#taxIdKey').val(data.code);
				$('#taxIdDesc').val(data.description);
				$('#h_taxIdDesc').val(data.description);
				$('#supplementDocReq').val(data.isRequired);
				
				adjustSupplementDocReq();
				
			},
			error: function(data) {
				$('#taxId').addClass('fieldError');
				$('#taxIdDesc').val('NOT FOUND');
				$('#taxId').val('');
				$('#taxIdKey').val('');
				$('#h_taxIdDesc').val('');
				
			}
		},
		filter:['${lookupCustType}', 'C'],
		description:$('#taxIdDesc'),
		help:$('#taxIdHelp')
	});
	
	$('#taxId').change(function(){
		if ($(this).val()==''){
			$('#taxId').val('');
			$('#taxIdDesc').val('');
			$('#taxId').val('');
			$('#taxIdKey').val('');
			$('#h_taxIdDesc').val('');
		}			
	});
	
	/*$('#originCountry').lookup({
		list:'@{Pick.lookups()}?group=COUNTRY',
		get:{
			url:'@{Pick.lookup()}?group=COUNTRY',
			success: function(data) {
				if (data) {
					$('#originCountry').removeClass('fieldError');
					$('#originCountryId').val(data.code);
					$('#originCountryDesc').val(data.description);
					$('#h_originCountryDesc').val(data.description);
					
					$('#placeCompEstb').val('');
					$('#placeCompEstbDesc').val('');
					$('#placeCompEstbId').val('');
					$('#placeCompEstb').val('');
					$('#h_placeCompEstbDesc').val('');
					
					$('#placeCompEstbArea').val('');
					$('#placeCompEstbAreaDesc').val('');
					$('#placeCompEstbAreaId').val('');
					$('#placeCompEstbArea').val('');
					$('#h_placeCompEstbAreaDesc').val('');
					
					adjustSupplementDocReq();
					adjustCountryCorp();
				}
			},
			error : function(data) {
				$('#originCountry').addClass('fieldError');
				$('#originCountryDesc').val('NOT FOUND');
				$('#originCountry').val('');
				$('#originCountryId').val('');
				$('#h_originCountryDesc').val('');
			}
		},
		//filter: {'key':'3','value':$('#nationality')},
		//filter:$('#legalDomicile'),
		key:$('#originCountryId'),
		description:$('#originCountryDesc'),
		help:$('#originCountryHelp'),
		nextControl:$('#companyCharacteristic')
	});*/
	
	var stateFilterI ="";
	if($('#citizenShipCountryCode').val() != ""){
		stateFilterI = "STATE";
	}
	stateI(stateFilterI);
	
	var areaFilterI ="";
	if($('#birthPlaceState').val() != ""){
		areaFilterI = "AREA";
	}
	areaI(areaFilterI);
	
	$('#originCountry').dynapopup2({key:'originCountryId', help:'originCountryHelp', desc:'originCountryDesc'},'PICK_GN_LOOKUP', "COUNTRY", "placeCompEstb", 
			function(data){
				if (data) {
					$('#originCountry').removeClass('fieldError');
					$('#originCountryId').val(data.code);
					$('#originCountryDesc').val(data.description);
					$('#h_originCountryDesc').val(data.description);
					
					$('#placeCompEstb').val('');
					$('#placeCompEstbDesc').val('');
					$('#placeCompEstbId').val('');
					$('#placeCompEstb').val('');
					$('#h_placeCompEstbDesc').val('');
					
					$('#placeCompEstbArea').val('');
					$('#placeCompEstbAreaDesc').val('');
					$('#placeCompEstbAreaId').val('');
					$('#placeCompEstbArea').val('');
					$('#h_placeCompEstbAreaDesc').val('');
					
					adjustSupplementDocReq();
					adjustCountryCorp();
					stateI("STATE");
					areaI("");
				}
			},
			function(data){
				$('#originCountry').addClass('fieldError');
				$('#originCountryDesc').val('NOT FOUND');
				$('#originCountry').val('');
				$('#originCountryId').val('');
				$('#h_originCountryDesc').val('');
			}
	);
	
	
	adjustCountryCorp();
	adjustSupplementDocReq();
	
	$('#originCountry').change(function(){
		if ($('#originCountry').val() == "") {
			$('#originCountry').val("");
			$('#originCountryId').val("");
			$('#originCountryDesc').val("");
			$('#h_originCountryDesc').val("");
			
			$('#placeCompEstb').val('');
			$('#placeCompEstbDesc').val('');
			$('#placeCompEstbId').val('');
			$('#placeCompEstb').val('');
			$('#h_placeCompEstbDesc').val('');
			
			$('#placeCompEstbArea').val('');
			$('#placeCompEstbAreaDesc').val('');
			$('#placeCompEstbAreaId').val('');
			$('#placeCompEstbArea').val('');
			$('#h_placeCompEstbAreaDesc').val('');
			stateI("");
			areaI("");
		}
	});
	
	function stateI(data){
		$('#placeCompEstb').lookup({
			list:'@{Pick.lookups()}?group='+data,
			get:{
				url:'@{Pick.lookup()}?group='+data,
				success: function(data) {
					if (data) {
						$('#placeCompEstb').removeClass('fieldError');
						$('#placeCompEstbId').val(data.code);
						$('#placeCompEstbDesc').val(data.description);
						$('#h_placeCompEstbDesc').val(data.description);
						
						$('#placeCompEstbArea').val('');
						$('#placeCompEstbAreaDesc').val('');
						$('#placeCompEstbAreaId').val('');
						$('#placeCompEstbArea').val('');
						$('#h_placeCompEstbAreaDesc').val('');
						areaI("AREA");
					}	
				},
				error: function(data) {
					$('#placeCompEstb').addClass('fieldError');
					$('#placeCompEstbDesc').val('NOT FOUND');
					$('#placeCompEstbId').val('');
					$('#placeCompEstb').val('');
					$('#h_placeCompEstbDesc').val('');
					
				}
			},
			filter:{'key':'1','value':$('#originCountryId')},
			description:$('#placeCompEstbDesc'),
			help:$('#placeCompEstbHelp')
		});
	}
	
	
	$('#placeCompEstb').change(function(){
		if ($(this).val()==''){
			$('#placeCompEstb').val('');
			$('#placeCompEstbDesc').val('');
			$('#placeCompEstbId').val('');
			$('#placeCompEstb').val('');
			$('#h_placeCompEstbDesc').val('');
			
			$('#placeCompEstbArea').val('');
			$('#placeCompEstbAreaDesc').val('');
			$('#placeCompEstbAreaId').val('');
			$('#placeCompEstbArea').val('');
			$('#h_placeCompEstbAreaDesc').val('');
			areaI("");
		}
		
	});
	
	function areaI(data){
		$('#placeCompEstbArea').lookup({
			list:'@{Pick.lookups()}?group='+data,
			get:{
				url:'@{Pick.lookup()}?group='+data,
				success: function(data) {
					if (data) {
						$('#placeCompEstbArea').removeClass('fieldError');
						$('#placeCompEstbAreaId').val(data.code);
						$('#placeCompEstbAreaDesc').val(data.description);
						$('#h_placeCompEstbAreaDesc').val(data.description);
						
					}	
				},
				error: function(data) {
					$('#placeCompEstbArea').addClass('fieldError');
					$('#placeCompEstbAreaDesc').val('NOT FOUND');
					$('#placeCompEstbAreaId').val('');
					$('#placeCompEstbArea').val('');
					$('#h_placeCompEstbAreaDesc').val('');
					
				}
			},
			filter:{'key':'2','value':$('#placeCompEstbId')},
			description:$('#placeCompEstbAreaDesc'),
			help:$('#placeCompEstbAreaHelp')
		});
	}
	
	
	$('#placeCompEstbArea').change(function(){
		if ($(this).val()==''){
			$('#placeCompEstbArea').val('');
			$('#placeCompEstbAreaDesc').val('');
			$('#placeCompEstbAreaId').val('');
			$('#placeCompEstbArea').val('');
			$('#h_placeCompEstbAreaDesc').val('');
		}
		
	});
	
	$('#typeOfBusiness').change(function(){
		// s  #7514
		clearFieldInsuranceOrPension();
		// e  #7514
		adjustOtherCorp();
	});
	
	$('#companyCharacteristic').change(function(){
		// s  #7514
		if (!($('#typeOfBusiness').val()=='${typeBusinessInsurance}' || $('#typeOfBusiness').val()=='${typeBusinessPensionFund}')){
			 clearFieldInsuranceOrPension();
		}
		// e  #7514
		adjustOtherCorp();
	});
	
	// s #7514
	adjustOtherCorp();
	// e #7514
	
	$('#documentType1').change(function(){
		checkIdentificationTypeIndv("#documentType", "Document");
		$('#document1No').val('');
		$('#document1Expiry').val('');
		$('#document1Expiry').removeClass('fieldError');
		$('#document1ExpiryError').html('');
	});
	
	$('#documentType2').change(function(){
		checkIdentificationTypeIndv("#documentType", "Document");
		$('#document2No').val('');
		$('#document2Expiry').val('');
		$('#document1Expiry').removeClass('fieldError');
		$('#document1ExpiryError').html('');
	});
	
	$('#documentType3').change(function(){
		checkIdentificationTypeIndv("#documentType", "Document");
		$('#document3No').val('');
		$('#document3Expiry').val('');
		$('#document3Expiry').removeClass('fieldError');
		$('#document3ExpiryError').html('');
	});
	
	$('#documentType4').change(function(){
		checkIdentificationTypeIndv("#documentType", "Document");
		$('#document4No').val('');
		$('#document4Expiry').val('');
		$('#document4Expiry').removeClass('fieldError');
		$('#document4ExpiryError').html('');
	});
	
	$('#document1Expiry').change(function(){
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ($(this).val()!='' && !$(this).hasClass('fieldError')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
	});
	
	$('#document2Expiry').change(function(){
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ($(this).val()!='' && !$(this).hasClass('fieldError')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
	});
	
	$('#document3Expiry').change(function(){
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ($(this).val()!='' && !$(this).hasClass('fieldError')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
	});
	
	$('#document4Expiry').change(function(){
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ($(this).val()!='' && !$(this).hasClass('fieldError')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
	});
	
	
	if ($('#asOfYear').val()!=''){
		$('.spanLatestYear').html(' ('+$('#asOfYear').val()+')');
		$('.spanLast2Year').html(' ('+(Number($('#asOfYear').val())-Number(1))+')');
		$('.spanLast3Year').html(' ('+(Number($('#asOfYear').val())-Number(2))+')');
	}
	
	$('#asOfYear').change(function(){
		if ($(this).val()!=''){
			$('.spanLatestYear').html(' ('+$(this).val()+')');
			$('.spanLast2Year').html(' ('+(Number($(this).val())-Number(1))+')');
			$('.spanLast3Year').html(' ('+(Number($(this).val())-Number(2))+')');
		} else {
			$('.spanLatestYear').html('');
			$('.spanLast2Year').html('');
			$('.spanLast3Year').html('');
		}
	});
	// =============================================================//
	
	// ============ END OF SECTION TAB CHARGE INVOICE ============ //
	
	if (('${sofData}'!='')&&('${invData}'!='')||
		('${sofData}'!=null)&&('${invData}'!=null)) {
		var dataSof = '${sofData}';
		var dataInv = '${invData}';
		var arrDataSof = dataSof.split("|");
		for (var i=0; i < arrDataSof.length; i++) {
			$('#sof'+arrDataSof[i]+'').attr('checked', true);
			$('#hiddenSof'+arrDataSof[i]+'').val(arrDataSof[i]);
		}
		
		var arrDataInv = dataInv.split("|");
		for (var i=0; i < arrDataInv.length; i++) {
			$('#inv'+arrDataInv[i]+'').attr('checked', true);
			$('#hiddenInv'+arrDataInv[i]+'').val(arrDataInv[i]);
		}
		if (('${confirming}')||('${mode}'=='view')) {
			$("input[name='sourceOfFunds']").attr('disabled', 'disabled');
			$("input[name='investObjs']").attr('disabled', 'disabled');
		}
	}
	
	$('#save').click(function(){	
		//s #7528
		$('#identificationType1').attr("disabled", false);
		$('#identificationType2').attr("disabled", false);
		if($('#req1').text() == "*" ){
			if($('#identificationRegDate1').text() == "Can not greater than Application Date"){
				return false;
			}else if($('#identificationRegDate1').text() != "" || $('#identificationRegDate1').text() == "Can not greater than Application Date"){
				$('#identificationRegDate1').html('Required');
				return false;
			}
		}else{
			if($('#identificationRegDate1').text() == "Can not greater than Application Date"){
				return false;
			}
		}
		if($('#req2').text() == "*"){
			if($('#identificationRegDate2').text() == "Can not greater than Application Date"){
				return false;
			}else if($('#identificationRegDate2').text() != "" || $('#identificationRegDate2').text() == "Can not greater than Application Date"){
				$('#identificationRegDate2').html('Required');
				return false;
			}
		}else{
			if($('#identificationRegDate2').text() == "Can not greater than Application Date"){
				return false;
			}
		}
		//e #7528
		if (validateForm()){
			if ($('#customerType').val()=='${typeIndi}'){
				$(".corporate_only").remove();
				$(".default").remove();
				$("#udfGlobal").remove();
				$("#udfCorporate").remove();
		    	$(".udf_type_all").remove();
		    } else {
		    	$(".individual_only").remove();
		    	$(".default").remove();
		    	$("#udfGlobal").remove();
				$("#udfIndividual").remove();
		    	$(".udf_type_all").remove();
		    }
			var amlId = $('#amlKey').val();
			var amlKey = $('#amlKeyHidden').val();
			var action ='@{save()}?mode=${mode}&amlKey='+amlKey+'&amlId='+amlId+'#{if group}&group=${group}#{/}#{if isPartial}&isPartial=${isPartial}#{/}';
			loading.dialog('open');
			$('#customerForm').attr('action', action);
			$('#customerForm').submit();
			//return false;
		} else {
			return false;
		}
	});
	
	$('#mainCancel').click(function(){
		if ('${mode}' == 'entry') {
			location.href='@{dedupe()}';
			return false;
		}
		location.href='@{list()}?mode=edit' + '&param=edit' ;
		return false;
	});
	
	$('#confirm').click(function(){
		var select_customer; 
		select_customer = $('#customerType').val();
	    	if (select_customer == '${typeIndi}'){
				//console.debug('Individual');
				//$("div.individual_only").css("display", "block");
	        	//$("div.corporate_only").css("display", "none");
		    	$(".corporate_only").remove(); 
		    	$(".default").remove();
		    	$("#udfGlobal").remove();
				$("#udfCorporate").remove();
		    	$(".udf_type_all").remove();
		    	//return true;
			} else {
		    	//console.debug('Corporate');
		    	//$("div.individual_only").css("display", "none");
	        	//$("div.corporate_only").css("display", "block");
		    	$(".individual_only").remove();
		    	$(".default").remove();
		    	$("#udfGlobal").remove();
				$("#udfIndividual").remove();
		    	$(".udf_type_all").remove();
		    	//return true;
		    }   
	    	var action ='@{confirm()}?mode=${mode}#{if group}&group=${group}#{/}#{if isPartial}&isPartial=${isPartial}#{/}';
			loading.dialog('open');
			$('#customerForm').attr('action', action);
			$('#customerForm').submit();
	});
	
	$('#back').click(function(){
		location.href='@{back(id)}?mode=${mode}&param=${param}#{if status}&status=${status}#{/}#{if group}&group=${group}#{/}#{if from}&from=${from}#{/}#{if isPartial}&isPartial=${isPartial}#{/}';
		return false;
	});
	
	$('#getAmlKey').click(function(){
		getAmlData();
	});
});	

function getLookUpInsuranceOrPension(businessTypeCodeName) {
	
	$('#insuranceId').lookup({
		list:'@{Pick.lookups()}?group='+businessTypeCodeName,
		get:{
			url:'@{Pick.lookup()}?group='+businessTypeCodeName,
			success: function(data) {
				$('#insuranceId').removeClass('fieldError');
				$('#insuranceKey').val(data.code);
				$('#insuranceDesc').val(data.description);
				$('#h_insuranceDesc').val(data.description);
				
			},
			error: function(data) {
				$('#insuranceId').addClass('fieldError');
				$('#insuranceId').val('');
				$('#insuranceDesc').val('NOT FOUND');
				$('#insuranceKey').val('');
				$('#h_insuranceDesc').val('');
			}
		},
		description:$('#insuranceDesc'),
		help:$('#insuranceHelp')
	});
}

function getAge(birthDate){
	 var currentDate = $('#currentDate').datepicker('getDate');
	 var newCurDate = new Date(currentDate);
	 var birthDate = new Date(birthDate);
	 var age = newCurDate.getFullYear() - birthDate.getFullYear();
	 var m = newCurDate.getMonth() - birthDate.getMonth();
	 
	 if (m < 0 || (m == 0 && newCurDate.getDate() < birthDate.getDate())){
		 age--;
	 }
	 return age;
 }

// function customer type ===================================================================================================
function adjustCustomerType() {
	var select_customer; 
    select_customer = $('#customerType').val(); 	
  		//alert(select_customer);	    		       
		if  (select_customer == CUSTOMER_TYPE_C) {
        	console.debug('Corporate');	
        	$("label:contains('Birth Date *')").html("Founded Date <span class='req'>*</span>");
        	$("#tabs").tabs("disable",1);
        	$("#tabs").tabs("enable",2);
        	$("#tabs").tabs("enable",3);
        	$("#tabs").tabs("enable",6);
        	$(".default").css("display", "none");
        	$("div.individual_only").css("display", "none");
        	$("p.individual_only").css("display", "none");
        	$("div.corporate_only").css("display", "block");
        	$("div.udf_type_all").css("display", "none");
        	$("li.corporate_only").css("display", "");
        	$("p.corporate_only").css("display", "");
        	//$('#contact #grid-contact').dataTable().fnClearTable();
        } else if  (select_customer == CUSTOMER_TYPE_I) {
        	console.debug('Individual');
        	$("label:contains('Founded Date *')").html("Birth Date <span class='req'>*</span>");
        	if ($('#citizenShipCountryCode').val() == "" || $('#citizenShipCountryCode').val() == null || $('#citizenShipCountryCode').val() == undefined) {
        		$("label:contains('Identification 1')").html("Identification 1 <span class='req'>*</span>");
        	}else if ($('#citizenShipCountryCode').val() != "ID") {
        		$("label:contains('Identification 2')").html("Identification 2 <span class='req'>*</span>");
        	} else {
        		$("label:contains('Identification 1')").html("Identification 1 <span class='req'>*</span>");
        	}
        	
        	
        	$("#tabs").tabs("disable",2);
        	$("#tabs").tabs("enable",1);
        	$("#tabs").tabs("enable",3);
        	$("#tabs").tabs("enable",6);
        	$(".default").css("display", "none");
        	$("div.individual_only").css("display", "block");
        	$("div.corporate_only").css("display", "none");
        	$("div.udf_type_all").css("display", "none");
        	$("li.individual_only").css("display", "");
        	$("p.individual_only").css("display", "");
        	$("p.corporate_only").css("display", "none");
        	//$('#contact #grid-contact').dataTable().fnClearTable();
        } else {
        	$("#tabs").tabs("disable",1);
        	$("#tabs").tabs("disable",2);
        	$("#tabs").tabs("disable",3);
        	$("#tabs").tabs("disable",6);
        	$(".individual").val("");
        	$("div.individual_only").css("display", "none");
        	$("div.corporate_only").css("display", "none");
        	$("li.individual_only").css("display", "none");
        	$("li.corporate_only").css("display", "none");
        	$("p.individual_only").css("display", "none");
        	$("p.corporate_only").css("display", "none");
        	$("div.udf_type_all").css("display", "block");
        	$("#investorTypeDefault").attr("disabled",true);
        	$(".default").css("display", "");
        	//$('#contact #grid-contact').dataTable().fnClearTable();
        }
		adjustOther();
		custRetailOfStatus();
		if($("#currencyCode").val()==""){
			$("#currencyCode").val("IDR").change().blur();
		}
		if($("#h_corrAddr").val()==""){
			$("#corrAddress").val(ADDRESS_TYPE_2).change().blur();
		}
};	
// ==========================================================================================================================

// function clear value for contact =========================================================================================
function clearAttrForContact() {
	//$("#contactForm #addressType").val("");
	$(".isCustomer").val("");
	if ($('#customerType').val() == '${typeCorp}') {
		$("#contactForm #sameAsCorp").val("");
	} else {
		$("#contactForm #sameAsInd").val("");
	}
	$(".sameAs").val("");	
	$("#contactForm #remarks").val("");
}
// ==========================================================================================================================

// function clear value for is customer class ===============================================================================
function clearAttrForIsCustomer() {
	$(".isCustomer").val("");	
	$('.partOfName').val("");
	//$("#contactForm #sameAs").val("");
	//$(".sameAs").val("");		
};
// ==========================================================================================================================

// function clear value for same as class ===================================================================================
function clearAttrForSameAs() {
	$(".sameAs").val("");
	$("#address1CountryDesc").val("");
	$("#address1StateDesc").val("");
	$("#address1AreaDesc").val("");
};
// ==========================================================================================================================

// disable properties using sameAs class ====================================================================================
function disableForSameAs() {
	$(".sameAs").attr('disabled', 'disable');
	$(".small").attr('disabled', 'disable');
	//$("#address1CountryHelp").attr('disabled', 'disable');
	//$("#address1StateHelp").attr('disabled', 'disable');
	//$("#address1AreaHelp").attr('disabled', 'disable');
}
// ==========================================================================================================================

// enable properties using sameAs class ===================================================================================== 
function enableForSameAs() {
	$(".sameAs").attr('disabled', false);
	$(".small").attr('disabled', false);
	//$("#contactForm #address1CountryHelp").attr('disabled', 'false');
	//$("#contactForm #address1StateHelp").attr('disabled', 'false');
	//$("#contactForm #address1AreaHelp").attr('disabled', 'false');
}
// ==========================================================================================================================

// for sameAs dropdown ======================================================================================================
function sameAs() {
	if ('${confirming}' != 'true') {
		var select_sameAs; 
		//--for sameAs
        if ($('#customerType').val() =='${typeCorp}') {
			select_sameAs = $('#contactForm #sameAsCorp').val();
     		//if ($("#contactForm #sameAsCorp").val() == "ADDRESS_TYPE-1" || "ADDRESS_TYPE-2" || "ADDRESS_TYPE-3" || "ADDRESS_TYPE-4") {
				//disableForSameAs();
     		//}
			if ($("#contactForm #sameAsCorp").val() == "") {
				enableForSameAs();
			} else {
				disableForSameAs();
			}
        } else {
        	select_sameAs = $('#contactForm #sameAsInd').val();
			if ($("#contactForm #sameAsInd").val() == "") {
				enableForSameAs();
			} else {
				disableForSameAs();
			}
        }
	}
};
// ==========================================================================================================================

// for radio button is customer at tab contact ==============================================================================
function isCustomer() {
	if ('${confirming}' != 'true') {
		var select_isCustomer; 
        select_isCustomer = $('#isCustomer').val();
		//--for isCustomer
		//if ($("input[name='contact.isCustomer']:checked").val() == "true") {
		if($("#isCustomer1").is(":checked")){
			$("input[name='contact.isCustomer']").val('true');
		} else {
			$("input[name='contact.isCustomer']").val('false');
		}
		if (($("input[name='contact.isCustomer']:checked").val() == "true") || ($("input[name='contact.isCustomer']").val() == "true")) {
			console.debug("checked = " + $("input[name='contact.isCustomer']:checked").val());
			$(".isCustomer").attr('disabled', true);
			$("p[id=contactNameP] label span").html("");
			$("td[id=identificationType1Li] label span").html("");
			//$("#sameAs").attr('disabled', true);
			//sameAs();
			//disableForSameAs();
		} else {
			console.debug("unchecked = " + $("input[name='contact.isCustomer']:checked").val());
			$(".isCustomer").attr('disabled', false);
			$("p[id=contactNameP] label span").html("*");
			$("td[id=identificationType1Li] label span").html("*");
			//$("#sameAs").attr('disabled', false);
			//sameAs();
			//enableForSameAs();		
		}
	}			
};
// ==========================================================================================================================

// function same as which is copy value from another contact ================================================================
function copyValue() {
	var table = $('#contact #grid-contact').dataTable();
	var rows = table.fnGetNodes().length;
	var found = false;	
	if ($('#customerType').val() == 'CUSTOMER_TYPE_C') {
		if (rows == 0) {
			//if ($('#customerType').val() == 'CUSTOMER_TYPE_C') {
				if ($('#sameAsCorp').val() != "") {
					$("#sameAsCorp").addClass('fieldError');
					$("#sameAsCorpError").html("Not Available");
					return false;
				}
			//} else {
			//	if ($('#sameAsInd').val() != "") {
			//		$("#sameAsInd").addClass('fieldError');
			//		$("#sameAsIndError").html("Not Available");
			//		return false;
			//	}
			//}
		}
		
		for (i = 0; i < rows; i++) {
			var cell = tableContact.fnGetData(i);
			
			if ($('#sameAsCorp').val() != "") {
				if (($("#sameAsCorp").val() != $(cell[0]).val())) {	
					$("#sameAsCorp").addClass('fieldError');
					$("#sameAsCorpError").html("Not Available");
					//found = true;
					//break;
					//return false;
				}
			}
			
			if ($('#sameAsCorp').val() == $(cell[0]).val()) {	
				$('#sameAsCorp').removeClass('fieldError');
				$('#sameAsCorpError').html("");
				var datas = table.fnGetData(i);
				if ((datas.address1 == '') && (datas.address1City == '') && (datas.address1Country.lookupCode == '') &&
					(datas.address1State.lookupCode == '') && (datas.address1Area.lookupCode == '') && (datas.address1ZipCode == '') &&
					(datas.address1Phone1 == '') && (datas.address1Phone2 == '') && (datas.address1Phone3 == '')) {
					$("#sameAsCorp").addClass('fieldError');
					$("#sameAsCorpError").html("Not allowed, data contact is empty");
				} else {
					assignmentValueForSameAs(datas);
					found = true;
					break;
				}
			}
		}
	} else {
		if (rows == 0) {
			if ($('#sameAsInd').val() != "") {
				$("#sameAsInd").addClass('fieldError');
				$("#sameAsIndError").html("Not Available");
				return false;
			}
		}
		
		for (i = 0; i < rows; i++) {
			var cell = tableContact.fnGetData(i);
			
			if ($('#sameAsInd').val() != "") {
				if (($("#sameAsInd").val() != $(cell[0]).val())) {	
					$("#sameAsInd").addClass('fieldError');
					$("#sameAsIndError").html("Not Available");
				}
			}
			
			if ($('#sameAsInd').val() == $(cell[0]).val()) {	
				$('#sameAsInd').removeClass('fieldError');
				$('#sameAsIndError').html("");
				var datas = table.fnGetData(i);
				if ((datas.address1 == '') && (datas.address1City == '') && (datas.address1Country.lookupCode == '') &&
					(datas.address1State.lookupCode == '') && (datas.address1Area.lookupCode == '') && (datas.address1ZipCode == '') &&
					(datas.address1Phone1 == '') && (datas.address1Phone2 == '') && (datas.address1Phone3 == '')) {
					$("#sameAsInd").addClass('fieldError');
					$("#sameAsIndError").html("Not allowed, data contact is empty");
				} else {
					assignmentValueForSameAs(datas);
					found = true;
					break;
				}
			}
		}
	}
}
// ==========================================================================================================================
	
// function contact registration date 1 validation ==========================================================================
function conRegDate1validation() {
	var el = $('#contactForm #identification1RegDateCon');
	var expiryDate3 = new Date($('#contactForm #identification1ExpiryCon').val());
	var regDate3 = new Date($('#contactForm #identification1RegDateCon').val());
	
	el.removeClass('fieldError');
	$("#errConReg1Msg").html("");
	
	if (expiryDate3.getTime() < regDate3.getTime()) {
		el.addClass('fieldError');
		$("#errConReg1Msg").html("Registration Date must be less than Expiry Date");
	} else {
		el.removeClass('fieldError');
		$("#errConReg1Msg").html("");
	}
};
// ==========================================================================================================================
	
// for compare date with application date ============================================================================
function compareDateMustAfterApplicationDate(thisId, value) {
	var dateValue = new Date(value);
	var currentDate = $('#currentDate').datepicker('getDate');
	if (dateValue.getTime() < currentDate.getTime()) {
		$(thisId).addClass('fieldError');
		$(thisId).addClass('fieldAppDateError');
		$(thisId+"Error").html("can not less than Application Date");
		return false;
	} else {
		$(thisId).removeClass('fieldError');
		$(thisId).removeClass('fieldAppDateError');
		$(thisId+"Error").html("");
		return true;
	}
}

function compareDateMustBeforeApplicationDate(thisId, value) {
	var dateValue = new Date(value);
	var currentDate = $('#currentDate').datepicker('getDate');
	if (dateValue.getTime() > currentDate.getTime()) {
		$(thisId).addClass('fieldError');
		$(thisId).addClass('fieldAppDateError');
		$(thisId+"Error").html("can not greather than Application Date");
		return false;
	} else {
		$(thisId).removeClass('fieldError');
		$(thisId).removeClass('fieldAppDateError');
		$(thisId+"Error").html("");
		return true;
	}
}
// ==========================================================================================================================


// ==========================================================================================================================

	
// for check all identification type at tab contact =========================================================================
function checkIdentificationTypeIndv(id, label) {
	if (($(id+"1").val() == $(id+"2").val()) || ($(id+"2").val() == $(id+"1").val())) {
		$(id+"3").removeClass('fieldError');
		$(id+"3Error").html("");
		$(id+"4").removeClass('fieldError');
		$(id+"4Error").html("");
		if (($(id+"1").val() !="") && ($(id+"2").val() !="")) {
			$(id+"2").addClass('fieldError');
			$(id+"2Error").html("can not same with "+label+" 1");
		}
	} else if (($(id+"1").val() == $(id+"3").val()) || ($(id+"3").val() == $(id+"1").val())) {
		$(id+"2").removeClass('fieldError');
		$(id+"2Error").html("");
		$(id+"4").removeClass('fieldError');
		$(id+"4Error").html("");
		if (($(id+"1").val() != "") && ($(id+"3").val() != "")) {
			$(id+"3").addClass('fieldError');
			$(id+"3Error").html("can not same with "+label+" 1");
		}
	} else if (($(id+"1").val() == $(id+"4").val()) || ($(id+"4").val() == $(id+"1").val())) {
		$(id+"2").removeClass('fieldError');
		$(id+"2Error").html("");
		$(id+"3").removeClass('fieldError');
		$(id+"3Error").html("");
		if (($(id+"1").val() != "") && ($(id+"4").val() != "")) {
			$(id+"4").addClass('fieldError');
			$(id+"4Error").html("can not same with "+label+" 1");
		}
	} else if (($(id+"2").val() == $(id+"3").val()) || ($(id+"3").val() == $(id+"2").val())) {
		$(id+"2").removeClass('fieldError');
		$(id+"2Error").html("");
		$(id+"4").removeClass('fieldError');
		$(id+"4Error").html("");
		if (($(id+"2").val() != "") && ($(id+"3").val() != "")) {
			$(id+"3").addClass('fieldError');
			$(id+"3Error").html("can not same with "+label+" 2");
		} else {
			$(id+"3").removeClass('fieldError');
			$(id+"3Error").html("");
		}
	} else if (($(id+"2").val() == $(id+"4").val()) || ($(id+"4").val() == $(id+"2").val())) {
		$(id+"2").removeClass('fieldError');
		$(id+"2Error").html("");
		$(id+"3").removeClass('fieldError');
		$(id+"3Error").html("");
		if (($(id+"2").val() != "") && ($(id+"4").val() != "")) {
			$(id+"4").addClass('fieldError');
			$(id+"4Error").html("can not same with "+label+" 2");
		} else {
			$(id+"4").removeClass('fieldError');
			$(id+"4Error").html("");
		}
	} else if (($(id+"3").val() == $(id+"4").val()) || ($(id+"4").val() == $(id+"3").val())) {
		$(id+"2").removeClass('fieldError');
		$(id+"2Error").html("");
		$(id+"3").removeClass('fieldError');
		$(id+"3Error").html("");
		if (($(id+"3").val() != "") && ($(id+"4").val() != "")) {
			$(id+"4").addClass('fieldError');
			$(id+"4Error").html("can not same with "+label+" 3");
		} else {
			$(id+"4").removeClass('fieldError');
			$(id+"4Error").html("");
		}
	} else {
		$(id+"2").removeClass('fieldError');
		$(id+"2Error").html("");
		$(id+"3").removeClass('fieldError');
		$(id+"3Error").html("");
		$(id+"4").removeClass('fieldError');
		$(id+"4Error").html("");
	} 
	
}
// ==========================================================================================================================



// setting digit parameter and rounding for amount ==========================================================================
function formatAmount(digitAmount, typeAmount) {
	$("td[name=amountCell]", $("#grid-investor-inquiry")).each(function() {
		console.debug($('#amountRoundValue').val());
		var amount = $(this).html();
		$("#formatAmount").valueRnd(amount.toNumber(","), true, digitAmount, typeAmount);
		$(this).html($("#formatAmount").val());
	});
}
// ==========================================================================================================================

// setting digit parameter and rounding for unit ============================================================================
function formatUnit(digitUnit, typeUnit) {
	$("td[name=unitCell]", $("#grid-investor-inquiry")).each(function() {
		var unit = $(this).html();
		$("#formatUnit").valueRnd(unit.toNumber(","), true, digitUnit, typeUnit);
		$(this).html($("#formatUnit").val());
	});
}
// ==========================================================================================================================

// setting digit parameter and rounding for price ===========================================================================
function formatPrice(digitPrice, typePrice) {
	$("td[name=priceCell]", $("#grid-investor-inquiry")).each(function() {
		var price = $(this).html();
		$("#formatPrice").valueRnd(price.toNumber(","), true, digitPrice, typePrice);
		$(this).html($("#formatPrice").val());
	});
}
// ==========================================================================================================================

function validateForm(){
	var select_customer;
	var checkError = false;
	$('input.fieldError').each(function(idx, obj){
		var curObj = $(obj);
		var reqClass = curObj.parent().find("label span.req");
		/*if( reqClass[0] == undefined ){// not required
			reqClass = curObj.parent().parent().find("label span.req");
		}*/
		if( reqClass[0] == undefined ) {
			// not required
			if( curObj.val() !='' && curObj.hasClass("fieldAppDateError") ){//date harus dikosongin
				//console.log(curObj.attr("id")+" has fieldError1"); 
				checkError = true;
			}

		}else{ 
			// required
			if( curObj.hasClass('fieldError') ){
//				console.log(curObj.attr("id")+" has fieldError2");
				checkError = true;
			}
		}
	});
	
	var checkSelectError = false;
	$('select.fieldError').each(function(idx, obj){
		var curObj = $(obj);
		var reqClass = curObj.parent().find("label span.req");
		/*if( reqClass[0] == undefined ){// not required
			reqClass = curObj.parent().parent().find("label span.req");
		}*/
		
		if( reqClass[0] == undefined ) {
			// not required
			if( curObj.hasClass("fieldAppDateError") ){
				checkSelectError = true;
			}
		}else{ 
			// required
			if( curObj.hasClass('fieldError') ){
				checkSelectError = true;
			}
		}
	});
	
	var lengthTableAddress = $('#grid-contact').dataTable().fnGetNodes().length;
	var lengthTableContact = $('#grid-contact-corp').dataTable().fnGetNodes().length;
	select_customer = $('#customerType').val();
	var isValidate = false;
	
	  if (select_customer!=''){
		 
		  if ((select_customer == '${typeIndi}') || (select_customer == '${typeCorp}')){
			  
			  if ((select_customer == '${typeIndi}') || (select_customer == '${typeCorp}')) {
				if (lengthTableAddress == 0){
					$("#tabs").tabs("select", "#tabs-4");
					$('#errGlobalAddressCorp').html('Must have One Data!');
					isValidate = false;
				} else {
					$('#errGlobalAddressCorp').html('');
					isValidate = true;
				}
			  }
			  
			  if (($('#customerCategory').val()=='${directCat}')&&(select_customer == '${typeCorp}')) {
				 if (lengthTableContact == 0){
					$("#tabs").tabs("select", "#tabs-4");
					$('#errGlobalContactCorp').html('Must have One Data!');
					isValidate = false;
				} else {
					$('#errGlobalContactCorp').html('');
					isValidate = true;
				}
			  } else {
				  isValidate = true; 
			  }
		  }
	  } else {
		  isValidate = true;
	  }
	
	 if ((checkSelectError) || (checkError)) {
		 $('#customerMessageErrorGlobal').html("There are some error fields!");
		 isValidate = false;
	 }
	 
	 var married = '${maritalStatusMarried}';
	 // validasi required spouse
	 if($("#maritalStatus").val()==married){
		 if($("#spouseName").val()==''){
			 $("#tabs").tabs("select", "#tabs-2");
			 $("#spouseNameLi").find("span.error").html("Spouse Name is Required");
			 isValidate = false;
		 }else{
			 $("#spouseNameLi").find("span.error").html("");
		 }
	 }
	 
	 //validasi apakah ada main address
	 var addrValid = false;
	 $("#grid-contact tr").each(function(){
		 if($(this).find('.address-type').val()==ADDRESS_TYPE_1)addrValid = true; 
	 });
	 
	 if(!addrValid){
		 $("#tabs").tabs("select", "#tabs-4");
		 $('#errGlobalAddressCorp').html('Main Address is mandatory!');
		 isValidate=false;
	 }else{
		 $('#errGlobalAddressCorp').html('');
	 }
	 
	 
	 //validasi apakah Exchange Rate keiisi
	 if($("#exchangeRateRef").val()==""||$("#exchangeRateRefCode").val()==""){
		 $("#exchangeRateRefCodeError").html("Exchange Rate must be filled!");
		 $("#tabs").tabs("select", "#tabs-7");
		 isValidate=false;
	 }
	 
	 if($("#paymentMethod").val()==""||$("#paymentMethodCode").val()==""){
		 $("#paymentMethodCodeError").html("Payment Method must be filled!");
		 $("#tabs").tabs("select", "#tabs-7");
		 isValidate=false;
	 }
	 
	  return isValidate;
}

	
function doSave() {
	var select_customer;
		select_customer = $('#customerType').val();
		if (validateForm()){
		if (select_customer=='${typeIndi}'){
			//console.debug('Individual');
			//$("div.individual_only").css("display", "block");
        	//$("div.corporate_only").css("display", "none");
	    	$(".corporate_only").remove();
	    	$(".default").remove();
	    	$("#udfGlobal").remove();
			$("#udfCorporate").remove();
	    	$(".udf_type_all").remove();
	    	return true;
		} else {
	    	//console.debug('Corporate');
	    	//$("div.individual_only").css("display", "none");
        	//$("div.corporate_only").css("display", "block");
	    	$(".individual_only").remove();
	    	$(".default").remove();
	    	$("#udfGlobal").remove();
			$("#udfIndividual").remove();
	    	$(".udf_type_all").remove();
	    	return true;
	    }
		
		return true;
		} else {
			alert("test");
			return false;
		}
};

function doConfirm() {		
	var select_customer; 
	select_customer = $('#customerType').val();
    	if (select_customer == '${typeIndi}'){
			//console.debug('Individual');
			//$("div.individual_only").css("display", "block");
        	//$("div.corporate_only").css("display", "none");
	    	$(".corporate_only").remove(); 
	    	$(".default").remove();
	    	$("#udfGlobal").remove();
			$("#udfCorporate").remove();
	    	$(".udf_type_all").remove();
	    	//return true;
		} else {
	    	//console.debug('Corporate');
	    	//$("div.individual_only").css("display", "none");
        	//$("div.corporate_only").css("display", "block");
	    	$(".individual_only").remove();
	    	$(".default").remove();
	    	$("#udfGlobal").remove();
			$("#udfIndividual").remove();
	    	$(".udf_type_all").remove();
	    	//return true;
	    }   
    	return true;
    //$('#addressType').trigger('change');  
};

/*function doCancel() {
	#{if mode == 'edit'}
		location.href='@{list()}#{if mode}?mode=${mode}#{/}';
	#{/if}
	#{elseif mode == 'entry'}
		location.href='@{dedupe()}';
	#{/elseif}
	#{else}
		history.back();
	#{/else}
};*/

function adjustOtherCorp(){
	if ($('#typeOfBusiness').val()=='${otherTypeBusiness}'){
		if((('${mode}'=='entry')&&(!'${confirming}'))||(('${mode}'=='edit')&&(!'${confirming}'))){
			$('#typeOfBusinessOther').enabled();
		}
		$('p[id=otherTypeOfBusinessMandatory] label span').html(' *');
	}
	
	if ($('#typeOfBusiness').val()!='${otherTypeBusiness}'){
		$('#typeOfBusinessOther').disabled();
		if (!'${confirming}'){
			$('#typeOfBusinessOther').val('');
		}
		$('p[id=otherTypeOfBusinessMandatory] label span').html('');
	}
	
	if ($('#companyCharacteristic').val()=='${otherCompCharacter}'){
		
		if((('${mode}'=='entry')&&(!'${confirming}'))||(('${mode}'=='edit')&&(!'${confirming}'))){
			$('#companyCharacteristicOther').enabled();
		}
		$('p[id=otherCompCharMandatory] label span').html(' *');
	}
	
	if ($('#companyCharacteristic').val()!='${otherCompCharacter}'){
		$('#companyCharacteristicOther').disabled();
		if (!'${confirming}'){
			$('#companyCharacteristicOther').val('');
		}
		$('p[id=otherCompCharMandatory] label span').html(' ');
	}
	
	if ($('#typeOfBusiness').val()=='${typeBusinessInsurance}' || $('#typeOfBusiness').val()=='${typeBusinessPensionFund}'){
		// #7514 clearFieldInsuranceOrPension();
		if((('${mode}'=='entry')&&(!'${confirming}'))||(('${mode}'=='edit')&&(!'${confirming}'))){
			$('#insuranceId').enabled();
			$('#insuranceHelp').enabled();
		}
		$('p[id=insuranceCode] label span').html(' *');
		getLookUpInsuranceOrPension(buildTypeOfBusiness());
	}else{
		clearFieldInsuranceOrPension();
		$('#insuranceId').disabled();
		$('#insuranceHelp').disabled();
	}
}

function clearFieldInsuranceOrPension() {
	$('p[id=insuranceCode] label span').html(' ');
	$('#insuranceId').val('');
	$('#insuranceDesc').val('');
	$('#insuranceKey').val('');
	$('#h_insuranceDesc').val('');	
}

function buildTypeOfBusiness() {

	if($('#typeOfBusiness').val()=='${typeBusinessPensionFund}') {
		return 'SANDI_DANA_PENSIUN';
	}else if($('#typeOfBusiness').val()=='${typeBusinessInsurance}') {
		return 'SANDI_ASURANSI';
	}
	
	return 'SANDI_ASURANSI';
	
}

function adjustCountryCorp(){
	if ($('#originCountryId').val()== '${countryId}'){
		$('p[atr=corpIdMandatory] label span').html(' *');
		$('p[id=liArticleOfAssociation] label span').html(' *');
		$('p[id=liSiupNo] label span').html('');
	}
	
	if ($('#originCountryId').val()!= '${countryId}'){
		$('p[atr=corpIdMandatory] label span').html('');
		$('p[id=liArticleOfAssociation] label span').html('');
		$('p[id=liSiupNo] label span').html(' *');
	}
}

function adjustSupplementDocReq(){
	if (($('#originCountryId').val()== '${countryId}')&&($('#supplementDocReq').val()=='1')){
		$('td[id=tdDocType] label span').html(' *')
	}
	
	if (($('#originCountryId').val()!= '${countryId}')&&($('#isSupplementDocReq').val()!='1')){
		$('td[id=tdDocType] label span').html('')
	}
}

function doClose() {
	location.href='@{list()}#{if mode}?mode=${mode}#{/}';
	if (param) {
		if (param == 'register-bank-acct') {
			location.href='@{Customers.list()}?mode=view'+ '&param=register-bank-acct';
		} else if (param == 'register-cust-acct') {
			location.href='@{Customers.list()}?mode=view'+ '&param=register-bank-acct';
		} 
	} else {
		location.href='@{Customers.dedupe()}';
	}
	//history.back();
};

//s 7528
function getAmlData() {
	var closeDialog = function() {
		$("#dialog-message").dialog('close');
	}
	var amlKey = $('#amlKey').val();
	var customer = new Object();
	customer.customerType = new Object();
	customer.customerType.lookupId = $('#customerType').val();
	customer.identificationType1 = new Object();
	customer.identificationType2 = new Object();
	var contacts = [];	
	var messageError = '';
	if(!customer.customerType.lookupId){
		messageError += "CustomerType in tab main, ";
	} else if (!amlKey) {
		messageError += "AML key in tab main, ";
	} else if ($('#customerType').val()=='${typeIndi}'){
		customer.firstName = $('#firstName').val();
		customer.middleName = $('#middleName').val();
		customer.lastName = $('#lastName').val();
		
		customer.birthArea = new Object();
		customer.birthArea.lookupId = $('#birthPlaceAreaId').val();
		customer.birthArea.lookupDescription = $('#birthPlaceAreaDesc').val();
		customer.birthPlaceOther =  $('#birthPlace').val();
		customer.motherName = $('#motherName').val();
		customer.nationality = new Object();
		customer.nationality.lookupId = $('#citizenShipCountry').val();
		customer.nationality.lookupCode = $('#citizenShipCountryCode').val();
		customer.nationality.lookupDescription = $('#citizenShipCountryDesc').val();

    	customer.identificationType1.lookupId = $('#identificationType1').val();
    	customer.identification1No = $('#identification1No').val();

    	customer.identificationType2.lookupId = $('#identificationType2').val();
    	customer.identification2No = $('#identification2No').val();
    	
    	if (!customer.nationality.lookupId) {
    		messageError += "Nationality in tab Individual, ";
    	} 
    } else {
    	customer.legalDomicile = new Object(); 
		customer.legalDomicile.lookupId = $('#originCountryId').val();
		customer.legalDomicile.lookupCode = $('#originCountry').val();
		customer.legalDomicile.lookupDescription = $('#originCountryDesc').val();
		
    	var table = $('#contact #grid-contact-corp').dataTable();
    	var rows = table.fnGetNodes().length;
    	for (i = 0; i < rows; i++) {
    		var datas = tableContact.fnGetData(i);
    		var contact = new Object();
    		contact.addressType = datas.addressType;
    		contact.firstName = datas.firstName;
    		contact.middleName = datas.middleName;
    		contact.lastName = datas.lastName;
    		contact.contactName = datas.contactName;
    		contact.identificationType1 = datas.identificationType1;
    		contact.identification1No = datas.identification1No;
    		contacts.add(contact);
    	}

    	customer.identificationType1.lookupId = $('#documentType1').val();
    	customer.identification1No = $('#document1No').val();
    	
    	if (!customer.legalDomicile.lookupId) {
    		messageError += "Legal Domicile in tab Institutional, ";
    	} else if (customer.legalDomicile.lookupId != '${countryId}') {
        	/*if (!customer.identification1No) {
        		messageError += "Document No in tab Institutional, ";
        	}*/
    	} /*else {
    		var npwp = $('#npwp').val();
    		if (!npwp) {
        		messageError += "Npwp in tab Contacts, ";
    		}
    	}*/
    }
	customer.customerName = $('#customerName').val();
	
	customer.npwp = $('#npwp').val();
	customer.birthDate = $('#birthDate').val();

	customer.customerGroup = new Object();
	customer.customerGroup.thirdPartyCode = $('#custRetailOf').val();
	customer.customerGroup.thirdPartyKey = $('#custRetailOfKey').val();
	customer.customerGroup.thirdPartyName = $('#custRetailOfDesc').val();

	var table = $('#contact #grid-contact').dataTable();
	var rows = table.fnGetNodes().length;
	for (i = 0; i < rows; i++) {
		var datas = tableContact.fnGetData(i);
		var contact = new Object();
		contact.addressType = datas.addressType;
		contact.address1 = datas.address1;
		contact.address1 = datas.addressExt1;
		contact.address1 = datas.addressExt2;
		contact.address1 = datas.addressExt3;
		contacts.add(contact);
	}
	customer.contacts = contacts;

	if (messageError) {
		messageAlertOk("Field "+ messageError + " must be fill", "ui-icon ui-icon-notice", "Warning", closeDialog);
	} else {
		$().fetchSync("@{getAmlData()}", 
				{
					'amlKey' : amlKey,
					'customer' : customer
				}, function(response) {
					if (response) {
						resultCode = response.resultCode;
						resultMessage = response.resultMessage;
						var datas = response.amlCustomer;
						if (resultCode == "0") {
							fillAmlData(datas);
						} else {
							if (datas) {
								var checkYesFunction = function() {
									fillAmlData(datas);
									$("#dialog-message").dialog("close");
								};
								var checkNoFunction = function() {
									$("#dialog-message").dialog("close");
								};
	
								messageAlertYesNo(resultMessage, "ui-icon ui-icon-notice", "Confirmation Message", checkYesFunction, checkNoFunction);
							} else {
								messageAlertOk(resultMessage, "ui-icon ui-icon-notice", "Warning", closeDialog);
							}
						}
					} 
			});
	}
	
}
//e 7528

//s 7528
function fillAmlData(datas) {
	if (datas) {
		$('#amlKeyHidden').val(datas.amlKey);
		var fmtDate = '${appProp.jqueryDateFormat}';
		var birthDate = datas.birthDate;
		birthDate = $.datepicker.formatDate(fmtDate, new Date(birthDate));
		
		if ($('#customerType').val()=='${typeIndi}'){
			$('#firstName').val(datas.firstName);
			$('#middleName').val(datas.middleName);
			$('#lastName').val(datas.lastName);
			var firstName = $('#firstName').val();
			var middleName = $('#middleName').val();
			var lastName = $('#lastName').val();

			if (firstName){
				$('#customerName').val($('#firstName').val() + " " +$('#middleName').val() + " " +$('#lastName').val());
				$('#customerNameHide').val($.trim($('#customerName').val()));
			}
			
			if (middleName){
				$('#customerName').val($('#firstName').val() + " " +middleName + " " +$('#lastName').val());
				$('#customerNameHide').val($.trim($('#customerName').val()));
				if ($(this).val()!=''){
					$('p[id=pLastName] label span').html(' *');
				} else {
					$('p[id=pLastName] label span').html('');
				}
			};
			
			if (lastName){
				$('#customerName').val($('#firstName').val() + " " +$('#middleName').val() + " " +lastName);
				$('#customerNameHide').val($.trim($('#customerName').val()));
			};
			
			$('#birthPlace').val(datas.birthPlace);
			$('#motherName').val(datas.motherName);
			$('#citizenShipCountry').val(datas.address1Country.lookupId);
			$('#citizenShipCountryCode').val(datas.address1Country.lookupCode);
			$('#citizenShipCountryDesc').val(datas.address1Country.lookupDescription);
			$('#customerName').val(datas.customerName);
			$('#birthDate').val(birthDate);
	    } else {
			$('#corporateName').val(datas.customerName);
	    	$('#originCountryId').val(datas.address1Country.lookupId);
	    	$('#originCountry').val(datas.address1Country.lookupCode);
	    	$('#originCountryDesc').val(datas.address1Country.lookupDescription);
	    	var table = $('#contact #grid-contact-corp').dataTable();
	    	if (datas.contacts) {
	    		var corpContacts = datas.contacts;
	    		
	    		for (var row in corpContacts) {
	    			var data = corpContacts[row];
	    			data.contactPosition = data.position;
	    			data.npwp="";
	    			data.identification1No=""
	    			table.fnAddData(data, false);
					table.fnDraw();
	    		}
	    	}
			
			if (datas.document.lookupId) {
				$('#documentType1').val(datas.document.lookupId);
				$('#document1No').val(datas.documentNo);
			}
			$('#dateCompEstb').val(birthDate);
	    }
		
    	var tableAddress = $('#contact #grid-contact').dataTable();
		var contact = new Object();
		contact.addressType = datas.addressType;
		contact.address1 = datas.address1;
		contact.addressExt1 = datas.address1;
		contact.addressExt2 = datas.address2;
		contact.addressExt3 = datas.address3;
		contact.isCustomer = true;
		contact.address1Phone1 = "";
		contact.address1Phone2 = "";
		contact.address1Phone3 = "";

		if (datas.address1Country.lookupId) {
			contact.address1Country = new Object();
			contact.address1Country.lookupCode = datas.address1Country.lookupCode;
			contact.address1Country.lookupId = datas.address1Country.lookupId;
			contact.address1Country.lookupDescription = datas.address1Country.lookupDescription;
		}
		if (datas.address1State.lookupId) {
			contact.address1State = new Object();
			contact.address1State.lookupCode = datas.address1State.lookupCode;
			contact.address1State.lookupId = datas.address1State.lookupId;
			contact.address1State.lookupDescription = datas.address1State.lookupDescription;
		}
		if (datas.address1Area.lookupId) {
			contact.address1Area = new Object();
			contact.address1Area.lookupCode = datas.address1Area.lookupCode;
			contact.address1Area.lookupId = datas.address1Area.lookupId;
			contact.address1Area.lookupDescription = datas.address1Area.lookupDescription;
		}
		tableAddress.fnAddData(contact);
		tableAddress.fnDraw();
		
		if (datas.identificationType.lookupId) {
			var nationalityCode = $('#citizenShipCountry').val();
			if(nationalityCode == '${countryId}') {
				$('#identificationType1').val(datas.identificationType.lookupId);
				$('#identification1No').val(datas.identificationNo);
			} else {
				$('#identification2No').val(datas.identificationNo);
			}
		}
		
		$('#npwp').val(datas.npwp);
		
		if (datas.customerGroup.thirdPartyCode) {
			$('#custRetailFlag1').attr('checked',true);
			$('#custRetailOf').enabled();
			$('#custRetailOfHelp').enabled();
			$('#externalCifNo').enabled();

			$('#custRetailOf').val(datas.customerGroup.thirdPartyCode);
			$('#custRetailOfKey').val(datas.customerGroup.thirdPartyKey);
			$('#custRetailOfDesc').val(datas.customerGroup.thirdPartyName);
			$('#externalCifNo').val(datas.externalCifNo);
		}
		
	}
}
//e 7528
