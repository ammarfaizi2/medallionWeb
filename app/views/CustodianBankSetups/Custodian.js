$(function(){
	$('#tabs').tabs();
	var dataDk;
	var dataCk;
	var dataDb;
	var dataCb;
	var dataDe;
	var dataCe;
	var dataDp;
	var dataCp;
	var dataInvoice;
	var dataComplience;
	
	TYPE_DEBET_KSEI = "DK";
	TYPE_CREDIT_KSEI = "CK";
	TYPE_DEBET_BI = "DB";
	TYPE_CREDIT_BI = "CB";
	TYPE_DEBET_EURO = "DE";
	TYPE_CREDIT_EURO = "CE";
	TYPE_DEBET_POOL = "DP";
	TYPE_CREDIT_POOL = "CP";
	CASH_ACCOUNT = "CASH_ACCOUNT";
	GL_ACCOUNT = "GL_ACCOUNT";
	
	$('#dialogBankAccount').dialog({
		autoOpen:false,
		modal:true,
		heigth:'600px',
		width:'600px',
		resizable:false
	});
	
	$('#dialogInvoice').dialog({
		autoOpen:false,
		modal:true,
		heigth:'600px',
		width:'500px',
		resizable:false
	});
	
	$('#dialogComplience').dialog({
		autoOpen:false,
		modal:true,
		heigth:'600px',
		width:'500px',
		resizable:false
	});
	
	var closeDialog = function() {
		$("#dialog-message").dialog("close");
	};
	
	if (('${dataBankDk}'=='') || ('${dataBankCk}'=='') ||
		('${dataBankDb}'=='') || ('${dataBankCb}'=='') ||
		('${dataBankDe}'=='') || ('${dataBankCe}'=='') ||
		('${dataBankDp}'=='') || ('${dataBankCp}'=='') ||
		('${dataInvoice}'=='') || ('${dataComplience}'=='')) {
			dataDk = new Array();
			dataCk = new Array();
			dataDb = new Array();
			dataCb = new Array();
			dataDe = new Array();
			dataCe = new Array();
			dataDp = new Array();
			dataCp = new Array();
			dataInvoice = new Array();
			dataComplience = new Array();
		}
	
	if ('${dataBankDk}'!='') { dataDk = ${dataBankDk.raw()}; }
	if ('${dataBankCk}'!='') { dataCk = ${dataBankCk.raw()}; }
	if ('${dataBankDb}'!='') { dataDb = ${dataBankDb.raw()}; }
	if ('${dataBankCb}'!='') { dataCb = ${dataBankCb.raw()}; }
	if ('${dataBankDe}'!='') { dataDe = ${dataBankDe.raw()}; }
	if ('${dataBankCe}'!='') { dataCe = ${dataBankCe.raw()}; }
	if ('${dataBankDp}'!='') { dataDp = ${dataBankDp.raw()}; }
	if ('${dataBankCp}'!='') { dataCp = ${dataBankCp.raw()}; }
	if ('${dataInvoice}'!='') { dataInvoice = ${dataInvoice.raw()}; }
	if ('${dataComplience}'!='') { dataComplience = ${dataComplience.raw()}; }
	
	tabBankAccount('#gridBankAccountDebetKsei', dataDk, "bankAccountsDk");
	tabBankAccount('#gridBankAccountCreditKsei', dataCk, "bankAccountsCk");
	tabBankAccount('#gridBankAccountDebetBi', dataDb, "bankAccountsDb");
	tabBankAccount('#gridBankAccountCreditBi', dataCb, "bankAccountsCb");
	tabBankAccount('#gridBankAccountDebetEuro', dataDe, "bankAccountsDe");
	tabBankAccount('#gridBankAccountCreditEuro', dataCe, "bankAccountsCe");
	tabBankAccount('#gridBankAccountDebetPool', dataDp, "bankAccountsDp");
	tabBankAccount('#gridBankAccountCreditPool', dataCp, "bankAccountsCp");
	tabInvoice(dataInvoice);
	tabComplience(dataComplience);
	
	// ========= ON TAB DETAILS ========== //
	$('#custodianCode').change(function(){
		$('#custodianCodeHidden').val($(this).val());
	});
	
	var stateFilter ="";
	if($('#addressCountryCode').val() != ""){
		stateFilter = "STATE";
	}
	state(stateFilter);
	
	var areaFilter ="";
	if($('#addressStateCode').val() != ""){
		areaFilter = "AREA";
	}
	area(areaFilter);
	
	
	$('#addressCountryCode').change(function(){
		if (($('#addressCountryCode').val() == "") || ($('#addressCountryCode').isChange())) {
			$('#addressCountry').val("");
			$('#h_addressCountryDesc').val("");
			$('#addressCountryCode').removeClass('fieldError');
			
			$('#addressStateCode').val("");
			$('#addressState').val("");
			$('#addressStateDesc').val("");
			$('#h_addressStateDesc').val("");
			$('#addressStateCode').removeClass('fieldError');
			
			$('#addressAreaCode').val("");
			$('#addressArea').val("");
			$('#addressAreaDesc').val("");
			$('#h_addressAreaDesc').val("");
			$('#addressAreaCode').removeClass('fieldError');
			
			state("");
			area("");
		}
		
	});
	// ======================================================================================================================
	
	// address 1 state action blur ========================================================================================
	$('#addressStateCode').change(function(){
		if (($('#addressStateCode').val() == "") || ($('#addressStateCode').isChange())) {
			//$('#addressStateCode').val("");
			$('#addressState').val("");
			$('#addressStateDesc').val("");
			$('#h_addressStateDesc').val("");
			$('#addressStateCode').removeClass('fieldError');
			
			$('#addressAreaCode').val("");
			$('#addressArea').val("");
			$('#addressAreaDesc').val("");
			$('#h_addressAreaDesc').val("");
			$('#addressAreaCode').removeClass('fieldError');
			
			area("");
		}
	});
	// ======================================================================================================================
	
	// address 1 area action change =========================================================================================
	$('#addressAreaCode').change(function(){
		if ($('#addressAreaCode').val() == "") {
			$('#addressAreaCode').val("");
			$('#addressArea').val("");
			$('#addressAreaDesc').val("");
			$('#h_addressAreaDesc').val("");
			$('#addressAreaCode').removeClass('fieldError');
		}
	});
	
	
	$('#addressCountryCode').lookup({
		list:'@{Pick.lookups()}?group=COUNTRY',
		get:{
			url:'@{Pick.lookup()}?group=COUNTRY',
			success: function(data) {
				if (data) {
					$('#addressCountryCode').removeClass('fieldError');
					$('#addressCountry').val(data.code);
					$('#addressCountryDesc').val(data.description);
					$('#h_addressCountryDesc').val(data.description);
					
					$('#addressStateCode').val("");
					$('#addressState').val("");
					$('#addressStateDesc').val("");
					$('#h_addressStateDesc').val("");
					$('#addressStateCode').removeClass('fieldError');
						
					$('#addressAreaCode').val("");
					$('#addressArea').val("");
					$('#addressAreaDesc').val("");
					$('#h_addressAreaDesc').val("");
					$('#addressAreaCode').removeClass('fieldError');
					state("STATE");
					
				}
			},
			error : function(data) {
				$('#addressCountryCode').addClass('fieldError');
				$('#addressCountryDesc').val('NOT FOUND');
				$('#addressCountryCode').val('');
				$('#addressCountry').val('');
				$('#h_addressCountryDesc').val('');
			}
		},
		key:$('#addressCountry'),
		description:$('#addressCountryDesc'),
		help:$('#addressCountryHelp'),
		nextControl:$('#addressStateCode')
	});
	
	$('#address1').change(function(){
		fillAddress();
	});
	$('#address2').change(function(){
		fillAddress();
	});
	$('#address3').change(function(){
		fillAddress();
	});
	
	// ======== ON TAB KSEI AND BI =================//
	
	
	if (('${confirming}'!='true')&&('${mode}'=='entry')&&('${errors}'=='[]')){
		if($("input[name='processTypeDebetKsei']")[1].checked = true){
			$('#debetTypeDk').val($('#radiodebetTypeDkGl').val());
		};
		
		if($("input[name='processTypeCreditKsei']")[1].checked = true){
			$('#debetTypeCk').val($('#radiodebetTypeCkGl').val());
		};
		
		if($("input[name='processTypeDebetBi']")[1].checked = true){
			$('#debetTypeDb').val($('#radiodebetTypeDbGl').val());
		};
		
		if($("input[name='processTypeCreditBi']")[1].checked = true){
			$('#debetTypeCb').val($('#radiodebetTypeCbGl').val());
		};
	};
	
	if ('${confirming}'=='true'){
		$("input[name='processTypeDebetKsei']").attr('disabled', 'disabled');
		$("input[name='processTypeCreditKsei']").attr('disabled', 'disabled');
		$("input[name='processTypeDebetBi']").attr('disabled', 'disabled');
		$("input[name='processTypeCreditBi']").attr('disabled', 'disabled');
		$("input[name='processTypeDebetEuro']").attr('disabled', 'disabled');
		$("input[name='processTypeCreditEuro']").attr('disabled', 'disabled');
	}
	
	//=== DEBET TYPE EUROCLEAR ====/
	$('#debetTypeDe').val(CASH_ACCOUNT);
	$('#debetTypeCe').val(CASH_ACCOUNT);
	
	//=== DEBET TYPE KSEI ====/
	$('#debetTypeDk').val(CASH_ACCOUNT);
	$('#debetTypeCk').val(CASH_ACCOUNT);
	
//	if ($('#debetTypeDk').val()==CASH_ACCOUNT){
//		$("input[name='processTypeDebetKsei']")[0].checked = true;
//	}
//	
//	if ($('#debetTypeDk').val()==GL_ACCOUNT){
//		$("input[name='processTypeDebetKsei']")[1].checked = true;
//	}
//	
//	$('#radiodebetTypeDkCash').click(function(){
//		$("input[name='processTypeDebetKsei']")[0].checked = true;
//		$('#debetTypeDk').val($(this).val());
//	});
//	
//	$('#radiodebetTypeDkGl').click(function(){
//		$("input[name='processTypeDebetKsei']")[1].checked = true;
//		$('#debetTypeDk').val($(this).val());
//	});
//	
//	//=== CREDIT TYPE KSEI ====/
//	if ($('#debetTypeCk').val()==CASH_ACCOUNT){
//		$("input[name='processTypeCreditKsei']")[0].checked = true;
//	}
//	
//	if ($('#debetTypeCk').val()==GL_ACCOUNT){
//		$("input[name='processTypeCreditKsei']")[1].checked = true;
//	}
//	
//	$('#radiodebetTypeCkCash').click(function(){
//		$("input[name='processTypeCreditKsei']")[0].checked = true;
//		$('#debetTypeCk').val($(this).val());
//	});
//	
//	$('#radiodebetTypeCkGl').click(function(){
//		$("input[name='processTypeCreditKsei']")[1].checked = true;
//		$('#debetTypeCk').val($(this).val());
//	});
	
	//=== DEBET TYPE BI ====/
	$('#debetTypeDb').val(CASH_ACCOUNT);
//	if ($('#debetTypeDb').val()==CASH_ACCOUNT){
//		$("input[name='processTypeDebetBi']")[0].checked = true;
//	}
//	
//	if ($('#debetTypeDb').val()==GL_ACCOUNT){
//		$("input[name='processTypeDebetBi']")[1].checked = true;
//	}
//	
//	$('#radiodebetTypeDbCash').click(function(){
//		$("input[name='processTypeDebetBi']")[0].checked = true;
//		$('#debetTypeDb').val($(this).val());
//	});
//	
//	$('#radiodebetTypeDbGl').click(function(){
//		$("input[name='processTypeDebetBi']")[1].checked = true;
//		$('#debetTypeDb').val($(this).val());
//	});
//	
	//=== CREDIT TYPE KSEI ====/
	$('#debetTypeCb').val(CASH_ACCOUNT);
//	if ($('#debetTypeCb').val()==CASH_ACCOUNT){
//		$("input[name='processTypeCreditBi']")[0].checked = true;
//	}
//	
//	if ($('#debetTypeCb').val()==GL_ACCOUNT){
//		$("input[name='processTypeCreditBi']")[1].checked = true;
//	}
//	
//	$('#radiodebetTypeCbCash').click(function(){
//		$("input[name='processTypeCreditBi']")[0].checked = true;
//		$('#debetTypeCb').val($(this).val());
//	});
//	
//	$('#radiodebetTypeCbGl').click(function(){
//		$("input[name='processTypeCreditBi']")[1].checked = true;
//		$('#debetTypeCb').val($(this).val());
//	});

	$('#newDebetKsei').click(function(){
		openBankAccountDialog(TYPE_DEBET_KSEI);
		return false;
	});
	
	$('#newCreditKsei').click(function(){
		openBankAccountDialog(TYPE_CREDIT_KSEI);
		return false;
	});
	
	$('#newDebetBi').click(function(){
		openBankAccountDialog(TYPE_DEBET_BI);
		return false;
	});
	
	$('#newCreditBi').click(function(){
		openBankAccountDialog(TYPE_CREDIT_BI);
		return false;
	});
	
	$('#newDebetEuro').click(function(){
		openBankAccountDialog(TYPE_DEBET_EURO);
		return false;
	});
	
	$('#newCreditEuro').click(function(){
		openBankAccountDialog(TYPE_CREDIT_EURO);
		return false;
	});
	
	$('#newDebetPool').click(function(){
		openBankAccountDialog(TYPE_DEBET_POOL);
		return false;
	});
	
	$('#newCreditPool').click(function(){
		openBankAccountDialog(TYPE_CREDIT_POOL);
		return false;
	});
	
	
	// DETAIL BANK ACCOUNT
	
	// POPUP BANK CODE
	$('#bankCode').popupThirdParties("?type=THIRD_PARTY-BANK", "bankBranch", function(data){
		$('#bankCode').removeClass('fieldError');
		$('#newBankCode').val(data.name);
	}, function(data) {
		$('#bankCode').addClass('fieldError');
	});
	
	$("#invBankReferences").keyup(function(){
		var maxLength = 200;  
	    if ($(this).val().length > maxLength) {  
	        $(this).val($(this).val().substring(0, maxLength));  
	    }
	});
	
	//elvino
	$('#invBankCode').popupThirdParties("?type=THIRD_PARTY-BANK", "invBankBranch", function(data){
		$('#invBankCode').removeClass('fieldError');
	}, function(data) {
		$('#invBankCode').addClass('fieldError');
	});
	
	//elvino
	$('#invBankCurrency').popupCurrencies("invBankReferences", function(data){
		$('#invBankCurrency').removeClass('fieldError');
	}, function(data){
		$('#invBankCurrency').addClass('fieldError');
		$('#invBankCurrencyDesc').val('NOT FOUND');
		$('#h_invBankCurrencyDesc').val('');
		$('#invBankCurrency').val('');
	});

	// POPUP CURRENCY
	$('#bankCurrency').popupCurrencies("bankReferences", function(data){
		$('#bankCurrency').removeClass('fieldError');
		$('#newCurrencyCode').val(data.code);
	}, function(data){
		$('#bankCurrency').addClass('fieldError');
		$('#bankCurrencyDesc').val('NOT FOUND');
		$('#h_bankCurrencyDesc').val('');
		$('#bankCurrency').val('');
	});
	
	$('#bankAccountNo').change(function(){
		$('#newBankAccountNo').val($(this).val());
	});
	
	$('#cancelBankDialog').click(function(){
		$("#dialogBankAccount").dialog('close');
		return false;
	});
	
	$('#addBankDialog').die('click');
	$('#addBankDialog').live('click',function(){
		var type = $('#dialogType').val();
		var table = null;
		var oldBankCode = $('#dialogBankAccount #oldBankCode').val();
		var newBankCode = $('#dialogBankAccount #newBankCode').val();
		var oldBankAccountNo = $('#dialogBankAccount #oldBankAccountNo').val();
		var newBankAccountNo = $('#dialogBankAccount #newBankAccountNo').val();
		var oldCurrency = $('#dialogBankAccount #oldCurrencyCode').val();
		var newCurrency = $('#dialogBankAccount #newCurrencyCode').val();
		var bankCode = $('#dialogBankAccount #bankCode').val();
		var bankAccountNo= $('#dialogBankAccount #bankAccountNo').val();
		var bankCurrency= $('#dialogBankAccount #bankCurrency').val();
		var bankUsedBy = $("#usedBy option:selected").text().split("-")[1];
		
		if (type == TYPE_DEBET_KSEI) {
			table = $('#gridBankAccountDebetKsei').dataTable();
			idTable = '#gridBankAccountDebetKsei';
		}
		if (type == TYPE_CREDIT_KSEI) {
			table = $('#gridBankAccountCreditKsei').dataTable();
			idTable = '#gridBankAccountCreditKsei';
		}
		if (type == TYPE_DEBET_BI) {
			table = $('#gridBankAccountDebetBi').dataTable();
			idTable = '#gridBankAccountDebetBi';
		}
		if (type == TYPE_CREDIT_BI) {
			table = $('#gridBankAccountCreditBi').dataTable();
			idTable = '#gridBankAccountCreditBi';
		}
		if (type == TYPE_DEBET_EURO) {
			table = $('#gridBankAccountDebetEuro').dataTable();
			idTable = '#gridBankAccountDebetEuro';
		}
		if (type == TYPE_CREDIT_EURO) {
			table = $('#gridBankAccountCreditEuro').dataTable();
			idTable = '#gridBankAccountCreditEuro';
		}
		if (type == TYPE_DEBET_POOL) {
			table = $('#gridBankAccountDebetPool').dataTable();
			idTable = '#gridBankAccountDebetPool';
		}
		if (type == TYPE_CREDIT_POOL) {
			table = $('#gridBankAccountCreditPool').dataTable();
			idTable = '#gridBankAccountCreditPool';
		}
		var rowPosition = $('#dialogId').val();
		var data = table.fnGetData(parseFloat(rowPosition));
		
		if (($('#bankAccountNo').val()=='')||($('#accountHolderName').val()=='')||
			($('#bankCode').val()=='')|| ($('#bankAccountType').val()=='')||($('#bankCurrency').val()=='')||
			($('#usedBy').val()=='')) {
			
			if ($('#bankAccountNo').val()==''){
				$('#bankAccountNoError').html('Required');
			} else {
				$('#bankAccountNoError').html('');
			}
			
			if ($('#accountHolderName').val()=='') {
				$('#accountHolderNameError').html('Required');
			} else {
				$('#accountHolderNameError').html('');
			}
			
			if ($('#bankCode').val()=='') {
				$('#bankCodeError').html('Required');
			} else {
				$('#bankCodeError').html('');
			}
			
//			if ($('#bankBranch').val()=='') {
//				$('#bankBranchError').html('Required');
//			} else {
//				$('#bankBranchError').html('');
//			}
			if ($('#usedBy').val()=='') {
				$('#usedByError').html('Required');
			} else {
				$('#usedByError').html('');
			}
			
			if ($('#bankAccountType').val()=='') {
				$('#bankAccountTypeError').html('Required');
			} else {
				$('#bankAccountTypeError').html('');
			}
			
			if ($('#bankCurrency').val()=='') {
				$('#bankCurrencyError').html('Required');
			} else {
				$('#bankCurrencyError').html('');
			}
			
			if ($('#bicCode').val()=='') {
				$('#bicCodeError').html('Required');
			} else {
				$('#bicCodeError').html('');
			}
			
			if ((type == TYPE_DEBET_KSEI) || (type == TYPE_CREDIT_KSEI) ) {
				if ($('#participantAccount').val()=='') {
					$('#participantAccountError').html('Required');
					return false;
				}else {
					$('#participantAccountError').html('');
				}
			}
		
			return false;
		};
		
		if ((type == TYPE_DEBET_KSEI) || (type == TYPE_CREDIT_KSEI) ) {
			if ($('#participantAccount').val()=='') {
				$('#participantAccountError').html('Required');
				return false;
			}else {
				$('#participantAccountError').html('');
			}
		}
		
		if (rowPosition >= 0) {
			var found = false;
			var rows = table.fnGetNodes().length;
			for(var i=0; i<rows; i++){
				var cells = table.fnGetData(i);
				console.debug("bank Account No = "+bankAccountNo);
				console.debug("Cell Account No = "+cells.accountNo);
				console.debug("OLD bank Account No = "+oldBankAccountNo);
				console.debug("NEW bank Account No = "+newBankAccountNo);
				
				console.debug("bank Code = "+bankCode);
				console.debug("Cell bank Code = "+cells.bankCode.thirdPartyCode);
				console.debug("OLD bank code = "+oldBankCode);
				console.debug("NEW Bank code = "+newBankCode);
				if (((bankAccountNo == cells.accountNo)&&(oldBankAccountNo != newBankAccountNo)&&(bankCode == cells.bankCode.thirdPartyCode))||
					((bankAccountNo == cells.accountNo)&&(bankCode == cells.bankCode.thirdPartyCode)&&(oldBankCode != newBankCode)) || 
					(bankCurrency == cells.currency.currencyCode)&&(oldCurrencyCode != newCurrencyCode)){
//					((bankCurrency == cells.currency.currencyCode)&&(oldCurrencyCode != newCurrencyCode)&&)){
					if (((bankAccountNo == cells.accountNo)&&(oldBankAccountNo != newBankAccountNo)&&(bankCode == cells.bankCode.thirdPartyCode))||
						((bankAccountNo == cells.accountNo)&&(bankCode == cells.bankCode.thirdPartyCode)&&(oldBankCode != newBankCode))) {
							$('#bankGeneralMsg').html('Account No and Bank already exist!');
							$('#dialogBankAccount #bankAccountNo').addClass('fieldError');
							$('#dialogBankAccount #bankCode').addClass('fieldError');
							found = true;
							return false;
							break;
					}
					
					/*if ((bankCurrency == cells.currency.currencyCode)&&(oldCurrency != newCurrency)){
						$('#bankGeneralMsg').html('Currency already exist!');
						$('#dialogBankAccount #bankCurrency').addClass('fieldError');
						found = true;
						return false;
						break;
					}*/
					
					if ((rowPosition!=i)&&($.trim(bankCurrency) == $.trim(cells.currency.currencyCode)) && ($.trim(bankUsedBy) == $.trim(cells.usedBy.lookupDescription))){
						$('#bankGeneralMsg').html('Currency with specific Used By already exist');
						$('#dialogBankAccount #bankCurrency').addClass('fieldError');
						found = true;
						return false;
						break;
					}
					
				} else {
					$('#bankGeneralMsg').html('');
					$('#dialogBankAccount #bankAccountNo').removeClass('fieldError');
					$('#dialogBankAccount #bankCode').removeClass('fieldError');
				}
				
			}
			
			if (!found) {
				table.fnUpdate(((data.bankCode.thirdPartyCode = $('#dialogBankAccount #bankCode').val())&&(data.bankCode.thirdPartyKey = $('#dialogBankAccount #bankCodeKey').val())&&
						   (data.bankCode.thirdPartyName = $('#dialogBankAccount #bankCodeDesc').val())), parseFloat(rowPosition), 1);
				table.fnUpdate(data.accountNo = $('#dialogBankAccount #bankAccountNo').val(), parseFloat(rowPosition), 0);
				table.fnUpdate(data.participantAccount = $('#dialogBankAccount #participantAccount').val(), parseFloat(rowPosition), 0);
				table.fnUpdate(data.accountName = $('#dialogBankAccount #accountHolderName').val(), parseFloat(rowPosition), 2);
				table.fnUpdate(((data.currency.currencyCode = $('#dialogBankAccount #bankCurrency').val())&&
						   (data.currency.description = $('#dialogBankAccount #bankCurrencyDesc').val())), parseFloat(rowPosition), 3);
								
				data.branchCode = (($('#dialogBankAccount #bankBranch').val() != "") || ($('#dialogBankAccount #bankBranch').val() != null))? $('#dialogBankAccount #bankBranch').val() : "";
				data.bicCode = (($('#dialogBankAccount #bicCode').val() != "") || ($('#dialogBankAccount #bicCode').val() != null))? $('#dialogBankAccount #bicCode').val() : "";
				data.description = (($('#dialogBankAccount #bankReferences').val() !=  "") || ($('#dialogBankAccount #bankReferences').val() !=  null))? $('#dialogBankAccount #bankReferences').val() : "";
				data.custodianBankKey = (($('#dialogBankAccount #custodianBankKey').val() != "") || ($('#dialogBankAccount #custodianBankKey').val() != null))?  $('#dialogBankAccount #custodianBankKey').val() : "";
				if (data.usedBy == null) { data.usedBy = new Object(); }
				data.usedBy.lookupId = $('#dialogBankAccount #usedBy').val();				
				data.usedBy.lookupDescription = $("#usedBy option:selected").text().split("-")[1];
				table.fnUpdate(data, parseFloat(rowPosition),4);
				$('#dialogBankAccount').dialog('close');
			};
		} else {
			var found = false;
			var rows = table.fnGetNodes().length;
			for(var i=0; i<rows; i++){
				var cells = table.fnGetData(i);
				if (((bankAccountNo == cells.accountNo)&&(bankCode == cells.bankCode.thirdPartyCode))||
					(bankCurrency == cells.currency.currencyCode)){
					
					if ((bankAccountNo == cells.accountNo)&&(bankCode == cells.bankCode.thirdPartyCode)){
						$('#bankGeneralMsg').html('Account No and Bank already exist!');
						$('#dialogBankAccount #bankAccountNo').addClass('fieldError');
						$('#dialogBankAccount #bankCode').addClass('fieldError');
						found = true;
						return false;
						break;
					}
					
					/*if (bankCurrency == cells.currency.currencyCode) {
						$('#bankGeneralMsg').html('Currency already exist!');
						$('#dialogBankAccount #bankCurrency').addClass('fieldError');
						found = true;
						return false;
						break;
					}*/
					
				} else {
					$('#bankGeneralMsg').html('');
					$('#dialogBankAccount #bankAccountNo').removeClass('fieldError');
					$('#dialogBankAccount #bankCode').removeClass('fieldError');
				}

				if (($.trim(bankCurrency) == $.trim(cells.currency.currencyCode)) && ($.trim(bankUsedBy) == $.trim(cells.usedBy.lookupDescription))){
					$('#bankGeneralMsg').html('Currency with specific Used By already exist');
					$('#dialogBankAccount #bankCurrency').addClass('fieldError');
					found = true;
					return false;
					break;
				}
				
			}
			
			if(!found){
				var dataBankAccount = new Object();
				dataBankAccount.bankCode = new Object();
				dataBankAccount.currency = new Object();
				dataBankAccount.participantAccount = $('#dialogBankAccount #participantAccount').val();
				dataBankAccount.accountNo = $('#dialogBankAccount #bankAccountNo').val();
				dataBankAccount.accountName = $('#dialogBankAccount #accountHolderName').val();
				dataBankAccount.bankCode.thirdPartyCode = $('#dialogBankAccount #bankCode').val();
				dataBankAccount.bankCode.thirdPartyKey = $('#dialogBankAccount #bankCodeKey').val();
				dataBankAccount.bankCode.thirdPartyName = $('#dialogBankAccount #bankCodeDesc').val();
				dataBankAccount.currency.currencyCode = $('#dialogBankAccount #bankCurrency').val();
				dataBankAccount.currency.description = $('#dialogBankAccount #bankCurrencyDesc').val();
				dataBankAccount.branchCode = $('#dialogBankAccount #bankBranch').val();
				dataBankAccount.bicCode = $('#dialogBankAccount #bicCode').val();
				dataBankAccount.description = $('#dialogBankAccount #bankReferences').val();
				dataBankAccount.custodianBankKey = $('#dialogBankAccount #custodianBankKey').val();
				dataBankAccount.usedBy = new Object();
				dataBankAccount.usedBy.lookupId = $('#dialogBankAccount #usedBy').val();
				dataBankAccount.usedBy.lookupDescription = $("#usedBy option:selected").text().split("-")[1];
				
				table.fnAddData(dataBankAccount);
				$('#dialogBankAccount').dialog('close');
			};
		};
	});
	
	
	$('#gridBankAccountDebetKsei tbody tr #deleteButton').live('click', function(){
		var row = $(this).parents('tr');
		var rowNumber = $('#gridBankAccountDebetKsei').dataTable().fnGetPosition(row[0]);
//		var dataSub = $('#gridBankAccountDebetKsei').dataTable().fnGetData(parseFloat(rowNumber));
		
		var deleteBankAccountSub = function(){
			/*if ($(':radio[name="subBankAccounts"]', tr_row).is(':checked')) {
				$('#subBankAccError').html('Default can not be delete');
				$("#dialog-message").dialog("close");
				return false;
			}*/
			$('#gridBankAccountDebetKsei').dataTable().fnDeleteRow(rowNumber);
			/*var idTable = $('#gridBankAccountDebetKsei');
			var trs = $('tr', idTable);
			$.each(trs, function(idx, data){
				var hiddens = $('[type=hidden]', $(this));
				$(hiddens).eq(0).attr("name", "bankAccountsDk["+(idx-1)+"].bankCode.thirdPartyCode");
				$(hiddens).eq(1).attr("name", "bankAccountsDk["+(idx-1)+"].bankCode.thirdPartyKey");
				$(hiddens).eq(2).attr("name", "bankAccountsDk["+(idx-1)+"].bankCode.thirdPartyName");
				$(hiddens).eq(3).attr("name", "bankAccountsDk["+(idx-1)+"].accountNo");
				$(hiddens).eq(4).attr("name", "bankAccountsDk["+(idx-1)+"].accountName");
				$(hiddens).eq(5).attr("name", "bankAccountsDk["+(idx-1)+"].currency.currencyCode");
				$(hiddens).eq(6).attr("name", "bankAccountsDk["+(idx-1)+"].currency.description");
				$(hiddens).eq(7).attr("name", "bankAccountsDk["+(idx-1)+"].branchCode");
				$(hiddens).eq(8).attr("name", "bankAccountsDk["+(idx-1)+"].bicCode");
				$(hiddens).eq(9).attr("name", "bankAccountsDk["+(idx-1)+"].description");
				$(hiddens).eq(10).attr("name", "bankAccountsDk["+(idx-1)+"].custodianBankKey");
			
			});*/
			$("#dialog-message").dialog("close");
		};
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccountSub, closeDialog);
	});
	
	
	$('#gridBankAccountCreditKsei tbody tr #deleteButton').live('click', function(){
		var row = $(this).parents('tr');
		var rowNumber = $('#gridBankAccountCreditKsei').dataTable().fnGetPosition(row[0]);
//		var dataRed = $('#redBankAccountTable').dataTable().fnGetData(parseFloat(rowNumber));
		
		var deleteBankAccountRed = function(){
			/*if ($(':radio[name="redBankAccounts"]', tr_row).is(':checked')) {
				$('#redBankAccError').html('Default can not be delete');
				$("#dialog-message").dialog("close");
				return false;
			}*/
			$('#gridBankAccountCreditKsei').dataTable().fnDeleteRow(rowNumber);
			/*var idTable = $('#gridBankAccountCreditKsei');
			var trs = $('tr', idTable);
			$.each(trs, function(idx, data){
				var hiddens = $('[type=hidden]', $(this));
				$.each(trs, function(idx, data){
					var hiddens = $('[type=hidden]', $(this));
					$(hiddens).eq(0).attr("name", "bankAccountsCk["+(idx-1)+"].bankCode.thirdPartyCode");
					$(hiddens).eq(1).attr("name", "bankAccountsCk["+(idx-1)+"].bankCode.thirdPartyKey");
					$(hiddens).eq(2).attr("name", "bankAccountsCk["+(idx-1)+"].bankCode.thirdPartyName");
					$(hiddens).eq(3).attr("name", "bankAccountsCk["+(idx-1)+"].accountNo");
					$(hiddens).eq(4).attr("name", "bankAccountsCk["+(idx-1)+"].accountName");
					$(hiddens).eq(5).attr("name", "bankAccountsCk["+(idx-1)+"].currency.currencyCode");
					$(hiddens).eq(6).attr("name", "bankAccountsCk["+(idx-1)+"].currency.description");
					$(hiddens).eq(7).attr("name", "bankAccountsCk["+(idx-1)+"].branchCode");
					$(hiddens).eq(8).attr("name", "bankAccountsCk["+(idx-1)+"].bicCode");
					$(hiddens).eq(9).attr("name", "bankAccountsCk["+(idx-1)+"].description");
					$(hiddens).eq(10).attr("name", "bankAccountsCk["+(idx-1)+"].custodianBankKey");
				
				});
			
			});*/
			$("#dialog-message").dialog("close");
		};
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccountRed, closeDialog);
	});
	
	$('#gridBankAccountDebetBi tbody tr #deleteButton').live('click', function(){
		var row = $(this).parents('tr');
		var rowNumber = $('#gridBankAccountDebetBi').dataTable().fnGetPosition(row[0]);
//		var dataRed = $('#redBankAccountTable').dataTable().fnGetData(parseFloat(rowNumber));
		
		var deleteBankAccountRed = function(){
			/*if ($(':radio[name="redBankAccounts"]', tr_row).is(':checked')) {
				$('#redBankAccError').html('Default can not be delete');
				$("#dialog-message").dialog("close");
				return false;
			}*/
			$('#gridBankAccountDebetBi').dataTable().fnDeleteRow(rowNumber);
			/*var idTable = $('#gridBankAccountDebetBi');
			var trs = $('tr', idTable);
			$.each(trs, function(idx, data){
				var hiddens = $('[type=hidden]', $(this));
				$.each(trs, function(idx, data){
					var hiddens = $('[type=hidden]', $(this));
					$(hiddens).eq(0).attr("name", "bankAccountsDb["+(idx-1)+"].bankCode.thirdPartyCode");
					$(hiddens).eq(1).attr("name", "bankAccountsDb["+(idx-1)+"].bankCode.thirdPartyKey");
					$(hiddens).eq(2).attr("name", "bankAccountsDb["+(idx-1)+"].bankCode.thirdPartyName");
					$(hiddens).eq(3).attr("name", "bankAccountsDb["+(idx-1)+"].accountNo");
					$(hiddens).eq(4).attr("name", "bankAccountsDb["+(idx-1)+"].accountName");
					$(hiddens).eq(5).attr("name", "bankAccountsDb["+(idx-1)+"].currency.currencyCode");
					$(hiddens).eq(6).attr("name", "bankAccountsDb["+(idx-1)+"].currency.description");
					$(hiddens).eq(7).attr("name", "bankAccountsDb["+(idx-1)+"].branchCode");
					$(hiddens).eq(8).attr("name", "bankAccountsDb["+(idx-1)+"].bicCode");
					$(hiddens).eq(9).attr("name", "bankAccountsDb["+(idx-1)+"].description");
					$(hiddens).eq(10).attr("name", "bankAccountsDb["+(idx-1)+"].custodianBankKey");
				
				});
			
			});*/
			$("#dialog-message").dialog("close");
		};
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccountRed, closeDialog);
	});
	
	$('#gridBankAccountCreditBi tbody tr #deleteButton').live('click', function(){
		var row = $(this).parents('tr');
		var rowNumber = $('#gridBankAccountCreditBi').dataTable().fnGetPosition(row[0]);
//		var dataRed = $('#redBankAccountTable').dataTable().fnGetData(parseFloat(rowNumber));
		
		var deleteBankAccountRed = function(){
			/*if ($(':radio[name="redBankAccounts"]', tr_row).is(':checked')) {
				$('#redBankAccError').html('Default can not be delete');
				$("#dialog-message").dialog("close");
				return false;
			}*/
			$('#gridBankAccountCreditBi').dataTable().fnDeleteRow(rowNumber);
			/*var idTable = $('#gridBankAccountCreditBi');
			var trs = $('tr', idTable);
			$.each(trs, function(idx, data){
				var hiddens = $('[type=hidden]', $(this));
				$.each(trs, function(idx, data){
					var hiddens = $('[type=hidden]', $(this));
					$(hiddens).eq(0).attr("name", "bankAccountsCb["+(idx-1)+"].bankCode.thirdPartyCode");
					$(hiddens).eq(1).attr("name", "bankAccountsCb["+(idx-1)+"].bankCode.thirdPartyKey");
					$(hiddens).eq(2).attr("name", "bankAccountsCb["+(idx-1)+"].bankCode.thirdPartyName");
					$(hiddens).eq(3).attr("name", "bankAccountsCb["+(idx-1)+"].accountNo");
					$(hiddens).eq(4).attr("name", "bankAccountsCb["+(idx-1)+"].accountName");
					$(hiddens).eq(5).attr("name", "bankAccountsCb["+(idx-1)+"].currency.currencyCode");
					$(hiddens).eq(6).attr("name", "bankAccountsCb["+(idx-1)+"].currency.description");
					$(hiddens).eq(7).attr("name", "bankAccountsCb["+(idx-1)+"].branchCode");
					$(hiddens).eq(8).attr("name", "bankAccountsCb["+(idx-1)+"].bicCode");
					$(hiddens).eq(9).attr("name", "bankAccountsCb["+(idx-1)+"].description");
					$(hiddens).eq(10).attr("name", "bankAccountsCb["+(idx-1)+"].custodianBankKey");
				
				});
			
			});*/
			$("#dialog-message").dialog("close");
		};
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccountRed, closeDialog);
	});
	
	
	$('#gridBankAccountDebetEuro tbody tr #deleteButton').live('click', function(){
		var row = $(this).parents('tr');
		var rowNumber = $('#gridBankAccountDebetEuro').dataTable().fnGetPosition(row[0]);
//		var dataSub = $('#gridBankAccountDebetKsei').dataTable().fnGetData(parseFloat(rowNumber));
		
		var deleteBankAccountSub = function(){
			/*if ($(':radio[name="subBankAccounts"]', tr_row).is(':checked')) {
				$('#subBankAccError').html('Default can not be delete');
				$("#dialog-message").dialog("close");
				return false;
			}*/
			$('#gridBankAccountDebetEuro').dataTable().fnDeleteRow(rowNumber);
			/*var idTable = $('#gridBankAccountDebetEuro');
			var trs = $('tr', idTable);
			$.each(trs, function(idx, data){
				var hiddens = $('[type=hidden]', $(this));
				$(hiddens).eq(0).attr("name", "bankAccountsDe["+(idx-1)+"].bankCode.thirdPartyCode");
				$(hiddens).eq(1).attr("name", "bankAccountsDe["+(idx-1)+"].bankCode.thirdPartyKey");
				$(hiddens).eq(2).attr("name", "bankAccountsDe["+(idx-1)+"].bankCode.thirdPartyName");
				$(hiddens).eq(3).attr("name", "bankAccountsDe["+(idx-1)+"].accountNo");
				$(hiddens).eq(4).attr("name", "bankAccountsDe["+(idx-1)+"].accountName");
				$(hiddens).eq(5).attr("name", "bankAccountsDe["+(idx-1)+"].currency.currencyCode");
				$(hiddens).eq(6).attr("name", "bankAccountsDe["+(idx-1)+"].currency.description");
				$(hiddens).eq(7).attr("name", "bankAccountsDe["+(idx-1)+"].branchCode");
				$(hiddens).eq(8).attr("name", "bankAccountsDe["+(idx-1)+"].bicCode");
				$(hiddens).eq(9).attr("name", "bankAccountsDe["+(idx-1)+"].description");
				$(hiddens).eq(10).attr("name", "bankAccountsDe["+(idx-1)+"].custodianBankKey");
			
			});*/
			$("#dialog-message").dialog("close");
		};
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccountSub, closeDialog);
	});

	
	$('#gridBankAccountCreditEuro tbody tr #deleteButton').live('click', function(){
		var row = $(this).parents('tr');
		var rowNumber = $('#gridBankAccountCreditEuro').dataTable().fnGetPosition(row[0]);
//		var dataRed = $('#redBankAccountTable').dataTable().fnGetData(parseFloat(rowNumber));
		
		var deleteBankAccountRed = function(){
			/*if ($(':radio[name="redBankAccounts"]', tr_row).is(':checked')) {
				$('#redBankAccError').html('Default can not be delete');
				$("#dialog-message").dialog("close");
				return false;
			}*/
			$('#gridBankAccountCreditEuro').dataTable().fnDeleteRow(rowNumber);
			/*var idTable = $('#gridBankAccountCreditEuro');
			var trs = $('tr', idTable);
			$.each(trs, function(idx, data){
				var hiddens = $('[type=hidden]', $(this));
				$.each(trs, function(idx, data){
					var hiddens = $('[type=hidden]', $(this));
					$(hiddens).eq(0).attr("name", "bankAccountsCe["+(idx-1)+"].bankCode.thirdPartyCode");
					$(hiddens).eq(1).attr("name", "bankAccountsCe["+(idx-1)+"].bankCode.thirdPartyKey");
					$(hiddens).eq(2).attr("name", "bankAccountsCe["+(idx-1)+"].bankCode.thirdPartyName");
					$(hiddens).eq(3).attr("name", "bankAccountsCe["+(idx-1)+"].accountNo");
					$(hiddens).eq(4).attr("name", "bankAccountsCe["+(idx-1)+"].accountName");
					$(hiddens).eq(5).attr("name", "bankAccountsCe["+(idx-1)+"].currency.currencyCode");
					$(hiddens).eq(6).attr("name", "bankAccountsCe["+(idx-1)+"].currency.description");
					$(hiddens).eq(7).attr("name", "bankAccountsCe["+(idx-1)+"].branchCode");
					$(hiddens).eq(8).attr("name", "bankAccountsCe["+(idx-1)+"].bicCode");
					$(hiddens).eq(9).attr("name", "bankAccountsCe["+(idx-1)+"].description");
					$(hiddens).eq(10).attr("name", "bankAccountsCe["+(idx-1)+"].custodianBankKey");
				
				});
			
			});*/
			$("#dialog-message").dialog("close");
		};
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccountRed, closeDialog);
	});
	
	$('#gridBankAccountDebetPool tbody tr #deleteButton').live('click', function(){
		var row = $(this).parents('tr');
		var rowNumber = $('#gridBankAccountDebetPool').dataTable().fnGetPosition(row[0]);
//		var dataRed = $('#redBankAccountTable').dataTable().fnGetData(parseFloat(rowNumber));
		
		var deleteBankAccountRed = function(){
			/*if ($(':radio[name="redBankAccounts"]', tr_row).is(':checked')) {
				$('#redBankAccError').html('Default can not be delete');
				$("#dialog-message").dialog("close");
				return false;
			}*/
			$('#gridBankAccountDebetPool').dataTable().fnDeleteRow(rowNumber);
			/*var idTable = $('#gridBankAccountDebetPool');
			var trs = $('tr', idTable);
			$.each(trs, function(idx, data){
				var hiddens = $('[type=hidden]', $(this));
				$.each(trs, function(idx, data){
					var hiddens = $('[type=hidden]', $(this));
					$(hiddens).eq(0).attr("name", "bankAccountsDp["+(idx-1)+"].bankCode.thirdPartyCode");
					$(hiddens).eq(1).attr("name", "bankAccountsDp["+(idx-1)+"].bankCode.thirdPartyKey");
					$(hiddens).eq(2).attr("name", "bankAccountsDp["+(idx-1)+"].bankCode.thirdPartyName");
					$(hiddens).eq(3).attr("name", "bankAccountsDp["+(idx-1)+"].accountNo");
					$(hiddens).eq(4).attr("name", "bankAccountsDp["+(idx-1)+"].accountName");
					$(hiddens).eq(5).attr("name", "bankAccountsDp["+(idx-1)+"].currency.currencyCode");
					$(hiddens).eq(6).attr("name", "bankAccountsDp["+(idx-1)+"].currency.description");
					$(hiddens).eq(7).attr("name", "bankAccountsDp["+(idx-1)+"].branchCode");
					$(hiddens).eq(8).attr("name", "bankAccountsDp["+(idx-1)+"].bicCode");
					$(hiddens).eq(9).attr("name", "bankAccountsDp["+(idx-1)+"].description");
					$(hiddens).eq(10).attr("name", "bankAccountsDp["+(idx-1)+"].custodianBankKey");
				
				});
			
			});*/
			$("#dialog-message").dialog("close");
		};
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccountRed, closeDialog);
	});
	
	$('#gridBankAccountCreditPool tbody tr #deleteButton').live('click', function(){
		var row = $(this).parents('tr');
		var rowNumber = $('#gridBankAccountCreditPool').dataTable().fnGetPosition(row[0]);
//		var dataRed = $('#redBankAccountTable').dataTable().fnGetData(parseFloat(rowNumber));
		
		var deleteBankAccountRed = function(){
			/*if ($(':radio[name="redBankAccounts"]', tr_row).is(':checked')) {
				$('#redBankAccError').html('Default can not be delete');
				$("#dialog-message").dialog("close");
				return false;
			}*/
			$('#gridBankAccountCreditPool').dataTable().fnDeleteRow(rowNumber);
			/*var idTable = $('#gridBankAccountCreditPool');
			var trs = $('tr', idTable);
			$.each(trs, function(idx, data){
				var hiddens = $('[type=hidden]', $(this));
				$.each(trs, function(idx, data){
					var hiddens = $('[type=hidden]', $(this));
					$(hiddens).eq(0).attr("name", "bankAccountsCp["+(idx-1)+"].bankCode.thirdPartyCode");
					$(hiddens).eq(1).attr("name", "bankAccountsCp["+(idx-1)+"].bankCode.thirdPartyKey");
					$(hiddens).eq(2).attr("name", "bankAccountsCp["+(idx-1)+"].bankCode.thirdPartyName");
					$(hiddens).eq(3).attr("name", "bankAccountsCp["+(idx-1)+"].accountNo");
					$(hiddens).eq(4).attr("name", "bankAccountsCp["+(idx-1)+"].accountName");
					$(hiddens).eq(5).attr("name", "bankAccountsCp["+(idx-1)+"].currency.currencyCode");
					$(hiddens).eq(6).attr("name", "bankAccountsCp["+(idx-1)+"].currency.description");
					$(hiddens).eq(7).attr("name", "bankAccountsCp["+(idx-1)+"].branchCode");
					$(hiddens).eq(8).attr("name", "bankAccountsCp["+(idx-1)+"].bicCode");
					$(hiddens).eq(9).attr("name", "bankAccountsCp["+(idx-1)+"].description");
					$(hiddens).eq(10).attr("name", "bankAccountsCp["+(idx-1)+"].custodianBankKey");
				
				});
			
			});*/
			$("#dialog-message").dialog("close");
		};
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccountRed, closeDialog);
	});
	
	// =================== ON TAB INVOICE ================ //
	$('#defaultTaxCode').change(function(){
		if ($(this).val()==''){
			$('#defaultTaxKey').val('');
			$('#defaultTaxCode').val('');
			$('#defaultTaxDesc').val('');
			$('#h_defaultTaxDesc').val('');
			$('#defaultTaxCode').removeClass('fieldError');
		}
	});
	
	$('#defaultTaxCode').lookup({
		list:'@{Pick.taxMasters()}',
		get:{
			url:'@{Pick.taxMaster()}',
			success: function(data) {
						$('#defaultTaxCode').removeClass('fieldError');
						$('#defaultTaxKey').val(data.code);
						$('#defaultTaxCode').val(data.taxCode);
						$('#defaultTaxDesc').val(data.description);
						$('#h_defaultTaxDesc').val(data.description);
					},
			error: function() {
					$('#defaultTaxCode').addClass('fieldError');
					$('#defaultTaxKey').val('');
					$('#defaultTaxCode').val('');
					$('#defaultTaxDesc').val('NOT FOUND');
					$('#h_defaultTaxDesc').val('');
				}
		},
		key:$('#defaultTaxKey'),
		description:$('#defaultTaxDesc'),
		help:$('#defaultTaxHelp')
	});
	
	$('#newInvoice').click(function(){
		$('#dialogInvoice').dialog('open');
		$('.ui-widget-overlay').css('height',$('body').height());
		$('#dialogInvoice #rowPosition').val('');
		$('#dialogInvoice #chargeGroup').val('');
		$('#dialogInvoice #chargeGroupDesc').val('');
		$('#dialogInvoice #invoiceTaxCode').val('');
		$('#dialogInvoice #invoiceTaxKey').val('');
		$('#dialogInvoice #invoiceTaxDesc').val('');
		$('#dialogInvoice .error').html('');
		$('#dialogInvoice input').remove('fieldError');
		return false;
	});
	
	$('#dialogInvoice #chargeGroup').change(function(){
		var selected = $(this).val();
		var selectedText = $("#dialogInvoice #chargeGroup option[value=" + selected + "]").text();
		if (selectedText != '') {
			selectedText = jQuery.trim(selectedText);
			selectedText = selectedText.substring(jQuery.trim(selectedText).indexOf('-') + 1);
		}
		$("#dialogInvoice #chargeGroupDesc").val(selectedText);
		$('#dialogInvoice #newChargeGroup').val($("#dialogInvoice #chargeGroupDesc").val());
		$(this).removeClass('fieldError');
		$('#invoiceGeneralMsg').html('');
	});
	
	$('#defaultTaxCode').change(function(){
		if ($(this).val()==''){
			$('#defaultTaxKey').val('');
			$('#defaultTaxCode').val('');
			$('#defaultTaxDesc').val('');
			$('#h_defaultTaxDesc').val('');
			$('#defaultTaxCode').removeClass('fieldError');
		}
	});
	
	$('#dialogInvoice #invoiceTaxCode').lookup({
		list:'@{Pick.taxMasters()}',
		get:{
			url:'@{Pick.taxMaster()}',
			success: function(data) {
						$('#dialogInvoice #invoiceTaxCode').removeClass('fieldError');
						$('#dialogInvoice #invoiceTaxKey').val(data.code);
						$('#dialogInvoice #invoiceTaxCode').val(data.taxCode);
						$('#dialogInvoice #invoiceTaxDesc').val(data.description);
						$('#dialogInvoice #h_invoiceTaxDesc').val(data.description);
					},
			error: function() {
					$('#dialogInvoice #invoiceTaxCode').addClass('fieldError');
					$('#dialogInvoice #invoiceTaxKey').val('');
					$('#dialogInvoice #invoiceTaxCode').val('');
					$('#dialogInvoice #invoiceTaxDesc').val('NOT FOUND');
					$('#dialogInvoice #h_invoiceTaxDesc').val('');
				}
		},
		key:$('#dialogInvoice #invoiceTaxKey'),
		description:$('#dialogInvoice #invoiceTaxDesc'),
		help:$('#dialogInvoice #invoiceTaxHelp')
	});
	
	$('#cancelInvoiceDialog').click(function(){
		$('#dialogInvoice').dialog('close');
		return false;
	});
	
	$('#addInvoiceDialog').die('click');
	$('#addInvoiceDialog').live('click', function(){
		var oldChargeGroup = $('#dialogInvoice #oldChargeGroup').val();
		var newChargeGroup = $('#dialogInvoice #newChargeGroup').val();
		var table = $('#gridInvoice').dataTable();
		var rowPosition = $('#dialogInvoice #rowPosition').val();
		var found = false;
		
		if (($('#dialogInvoice #chargeGroup').val()=='') || 
			($('#dialogInvoice #invoiceTaxCode').val()=='')||
			($('#dialogInvoice #chargeGroup').hasClass('fieldError'))){
			
			if ($('#dialogInvoice #chargeGroup').val()==''){
				$('#dialogInvoice #chargeGroupError').html('Required');
			} else {
				$('#dialogInvoice #chargeGroupError').html('');
			}
			
			if ($('#dialogInvoice #invoiceTaxCode').val()==''){
				$('#dialogInvoice #invoiceTaxCodeError').html('Required');
			} else {
				$('#dialogInvoice #invoiceTaxCodeError').html('');
			}
			
			return false;
		}
		
		var dataInvoice = table.fnGetData(parseFloat(rowPosition));
		if (rowPosition!="") {
			var rows = table.fnGetNodes().length;
			
			for (i = 0; i < rows; i++) {
				var cell = table.fnGetData(i);
				if (($("#dialogInvoice #chargeGroupDesc").val() == $(cell[0]).val()) && (oldChargeGroup != newChargeGroup)) {					
					$('#dialogInvoice #chargeGroup').addClass('fieldError');
					$("#invoiceGeneralMsg").html("Already Exist");
					found = true;
					break;
				} else {
					$('#dialogInvoice #chargeGroup').removeClass('fieldError');
					$("#invoiceGeneralMsg").html("");
				}
			}
			
			if (!found) {
				table.fnUpdate(((dataInvoice.invoiceGroup.lookupDescription = $('#dialogInvoice #chargeGroupDesc').val())&&(dataInvoice.invoiceGroup.lookupId= $('#dialogInvoice #chargeGroup').val())), parseFloat(rowPosition), 0);
				table.fnUpdate(((dataInvoice.taxMaster.taxCode = $('#dialogInvoice #invoiceTaxCode').val())
								&&(dataInvoice.taxMaster.taxKey= $('#dialogInvoice #invoiceTaxKey').val())
								&&(dataInvoice.taxMaster.description = $('#dialogInvoice #invoiceTaxDesc').val())), parseFloat(rowPosition), 1);
				
				$('#dialogInvoice').dialog('close');
			}
		} else {
			var found = false;
			var rows = table.fnGetNodes().length;
			for(var i=0; i<rows; i++){
				var cells = table.fnGetData(i);
					
				if ($('#chargeGroupDesc').val() == $(cells[0]).val()){
					$('#dialogInvoice #chargeGroup').addClass('fieldError');
					$("#invoiceGeneralMsg").html("Already Exist");
					found = true;
					break;
				} else {
					$('#dialogInvoice #chargeGroup').removeClass('fieldError');
					$("#invoiceGeneralMsg").html("");
				}
				
			}
			
			if(!found){
				var dataInvoice = new Object();
				dataInvoice.invoiceGroup = new Object();
				dataInvoice.taxMaster = new Object();
				dataInvoice.invoiceGroup.lookupDescription = $('#dialogInvoice #chargeGroupDesc').val();
				dataInvoice.invoiceGroup.lookupId = $('#dialogInvoice #chargeGroup').val();
				dataInvoice.taxMaster.taxCode = $('#dialogInvoice #invoiceTaxCode').val();
				dataInvoice.taxMaster.taxKey = $('#dialogInvoice #invoiceTaxKey').val();
				dataInvoice.taxMaster.description = $('#dialogInvoice #invoiceTaxDesc').val();
				
				table.fnAddData(dataInvoice);
				$('#dialogInvoice').dialog('close');
			};
		}
	});
	
	$('#gridInvoice tbody tr #deleteButton').live('click', function(){
		var row = $(this).parents('tr');
		var rowNumber = $('#gridInvoice').dataTable().fnGetPosition(row[0]);
		
		var deleteInvoice = function(){
			$('#gridInvoice').dataTable().fnDeleteRow(rowNumber);
			var idTable = $('#gridInvoice');
			var trs = $('tr', idTable);
			$.each(trs, function(idx, data){
				var hiddens = $('[type=hidden]', $(this));
				$(hiddens).eq(0).attr("name", "invoices["+(idx-1)+"].invoiceGroup.lookupDescription");
				$(hiddens).eq(1).attr("name", "invoices["+(idx-1)+"].invoiceGroup.lookupId");
				$(hiddens).eq(2).attr("name", "invoices["+(idx-1)+"].taxMaster.taxCode");
				$(hiddens).eq(3).attr("name", "invoices["+(idx-1)+"].taxMaster.taxKey");
				$(hiddens).eq(4).attr("name", "invoices["+(idx-1)+"].taxMaster.description");
			});
			$("#dialog-message").dialog("close");
		};
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteInvoice, closeDialog);
	});
	
	//=================== ON TAB COMPLIENCE ================ //
	$('#cancelComplienceDialog').click(function(){
		$('#securityType').change();
		$("#dialogComplience").dialog('close');
	});
	
	$('#newComplience').click(function(){
		$('#dialogComplience').dialog('open');
		$('.ui-widget-overlay').css('height',$('body').height());
		$('#dialogComplience #rowPosition').val('');
		$('#dialogComplience #securityType').val('');
		$('#dialogComplience #securityTypeKey').val('');
		$('#dialogComplience #securityTypeDesc').val('');
		$("#complianceGeneralMsg").html("");
		
		$('#dialogComplience #groupCode').val('');
		$('#dialogComplience #groupKey').val('');
		$('#dialogComplience #groupDesc').val('');
		
		$('#dialogComplience .error').html('');
		$('#dialogComplience input').remove('fieldError');
		$('#securityType').change();
		return false;
	});
	
	$('#securityType').change(function(){
		$('#securityCode').val('');
		$('#securityKey').val('');
		$('#securityTypeDescription').val('');
		$('#h_securityTypeDescription').val('');
	});
	
	$('#securityType').lookup({
		list:'@{Pick.securityTypesWithPrice()}',
		get:{
			url:'@{Pick.securityTypeWithPrice()}',
			success: function(data){
				$('#securityType').removeClass('fieldError');
				$('#securityType').val(data.code);
				$('#securityTypeDescription').val(data.description);
				$('#h_securityTypeDescription').val(data.description);
				$('#priceUnit').val(data.priceUnit);
			},
			error: function(data){
				$('#securityType').addClass('fieldError');
				$('#securityType').val('');
				$('#securityTypeDescription').val('NOT FOUND');
				$('#h_securityTypeDescription').val('');
				$('#securityType').change();
			}
		},
		description:$('#securityTypeDescription'),
		help:$('#securityTypeHelp'),
		nextControl:$('#securityCode')
	});
	
	$('#groupCode').lookup({
		list:'@{Pick.lookups()}?group=SECURITY_PRICE_GROUP',
		get:{
			url:'@{Pick.lookup()}?group=SECURITY_PRICE_GROUP',
			success: function(data){
				$('#groupCode').removeClass('fieldError');
				$('#groupKey').val(data.code);
				$('#groupDesc').val(data.description);
				$('#h_groupDesc').val(data.description);
			},
			error: function(data){
				$('#groupCode').addClass('fieldError');
				$('#groupKey').val('');
				$('#groupDesc').val('NOT FOUND');
				$('#h_groupDescDesc').val('');
			}
		},
		description:$('#groupDesc'),
		help:$('#groupHelp')
	});
	
	$('#addComplienceDialog').die('click');
	$('#addComplienceDialog').live('click', function(){
		var oldSecurityType = $('#dialogComplience #oldSecurityType').val();
		var newSecurityType = $('#dialogComplience #newSecurityType').val();
		var table = $('#gridCompliance').dataTable();
		var rowPosition = $('#dialogComplience #rowPosition').val();
		var found = false;
		
		if (($('#dialogComplience #securityType').val()=='') || 
				($('#dialogComplience #groupCode').val()=='')||
				($('#dialogComplience #securityType').hasClass('fieldError'))){
			if ($('#dialogComplience #securityType').val()==''){
				$('#dialogComplience #securityTypeError').html('Required');
			} else {
				$('#dialogComplience #securityTypeError').html('');
			}

			if ($('#dialogComplience #groupCode').val()==''){
				$('#dialogComplience #groupCodeError').html('Required');
			} else {
				$('#dialogComplience #groupCode').html('');
			}

			return false;
		}	
		
		var dataComplience = table.fnGetData(parseFloat(rowPosition));
		if (rowPosition!="") {
			var rows = table.fnGetNodes().length;
			for (i = 0; i < rows; i++) {
				var cell = table.fnGetData(i);
				if (($("#dialogComplience #securityType").val() == $(cell[0]).val())) {					
					$('#dialogComplience #securityType').addClass('fieldError');
					$("#complianceGeneralMsg").html("Data already Exist");
					found = true;
					break;
				} else {
					$('#dialogComplience #securityType').removeClass('fieldError');
					$("#complianceGeneralMsg").html("");
				}
			}
			
			if (!found) {
				table.fnUpdate(((dataComplience.securityType.description = $('#dialogComplience #securityTypeDescription').val())&&(dataComplience.securityType.securityType= $('#dialogComplience #securityType').val())), parseFloat(rowPosition), 0);
				table.fnUpdate(((dataComplience.priceReference.lookupCode = $('#dialogComplience #groupCode').val())&&(dataComplience.priceReference.lookupId = $('#dialogComplience #groupKey').val())&&(dataComplience.priceReference.lookupDescription= $('#dialogComplience #groupDesc').val())), parseFloat(rowPosition), 1);
				$("#dialogComplience").dialog('close');
			}
			
		} else {
			var found = false;
			var rows = table.fnGetNodes().length;
			for(var i=0; i<rows; i++){
				var cells = table.fnGetData(i);
					
				if ($('#securityType').val() == $(cells[0]).val()){
					$('#dialogComplience #securityType').addClass('fieldError');
					$("#complianceGeneralMsg").html("Data already Exist");
					found = true;
					return false;
					break;
				} else {
					$('#dialogComplience #securityType').removeClass('fieldError');
					$("#complianceGeneralMsg").html("");
				}
				
			}
			
			if(!found){
				var dataComplience = new Object();
				dataComplience.securityType = new Object();
				dataComplience.priceReference = new Object();
				dataComplience.securityType.securityType = $('#dialogComplience #securityType').val();
				dataComplience.securityType.description = $('#dialogComplience #securityTypeDescription').val();
				dataComplience.priceReference.lookupCode = $('#dialogComplience #groupCode').val();
				dataComplience.priceReference.lookupId = $('#dialogComplience #groupKey').val();
				dataComplience.priceReference.lookupDescription = $('#dialogComplience #groupDesc').val();
				
				table.fnAddData(dataComplience);
				$('#dialogComplience').dialog('close');
			};
		}
	});
	
	//delete button
	$('#gridCompliance tbody tr #deleteButton').live('click', function(){
		var row = $(this).parents('tr');
		var rowNumber = $('#gridCompliance').dataTable().fnGetPosition(row[0]);
		
		var deleteComplience = function(){
			$('#gridCompliance').dataTable().fnDeleteRow(rowNumber);
			var idTable = $('#gridCompliance');
			var trs = $('tr', idTable);
			$.each(trs, function(idx, data){
				var hiddens = $('[type=hidden]', $(this));
				$(hiddens).eq(0).attr("name", "complience["+(idx-1)+"].securityType.securityType");
				$(hiddens).eq(1).attr("name", "complience["+(idx-1)+"].securityType.description");
				$(hiddens).eq(2).attr("name", "complience["+(idx-1)+"].priceReference.lookupCode");
				$(hiddens).eq(3).attr("name", "complience["+(idx-1)+"].priceReference.lookupId");
				$(hiddens).eq(4).attr("name", "complience["+(idx-1)+"].priceReference.lookupDescription");
			});
			$("#dialog-message").dialog("close");
		};
		
		if (isIE()){
			if($('#deleteButton').is(':disabled') == true){
				// do nothing
			}else{
				messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteComplience, closeDialog);
			}
		}else{
			messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteComplience, closeDialog);	
		}
	});
	
	$("#cancelComplianceDialog").click(function(){
		$("#dialogComplience").dialog('close');
	})
	
	
	
	//========================== END ======================= //  
});


function isIE() {
	ua = navigator.userAgent;
	/* MSIE used to detect old browsers and Trident used to newer ones*/
	var is_ie = ua.indexOf("MSIE ") > -1 || ua.indexOf("Trident/") > -1;

	return is_ie; 
}

function state(data) {
	// address 1 state lookup at tab contacts ===============================================================================
	$('#addressStateCode').lookup({
		list:'@{Pick.lookups()}?group='+data,
		get:{
			url:'@{Pick.lookup()}?group='+data,
			success: function(data) {
				if (data) {
					if ($('#addressCountryCode').val() == "" || $('#addressCountryCode').val() == null) {
						$('#addressStateCode').addClass('fieldError');
						$('#addressStateDesc').val('NOT FOUND');
						$('#addressStateCode').val('');
						$('#addressState').val('');
						$('#h_addressStateDesc').val('');
						return false;
					}
					$('#addressStateCode').removeClass('fieldError');
					$('#addressState').val(data.code);
					$('#addressStateDesc').val(data.description);
					$('#h_addressStateDesc').val(data.description);
					
					$('#addressAreaCode').val("");
					$('#addressArea').val("");
					$('#addressAreaDesc').val("");
					$('#h_addressAreaDesc').val("");
					$('#addressAreaCode').removeClass('fieldError');
					area("AREA");
				}
			},
			error : function(data) {
				$('#addressStateCode').addClass('fieldError');
				$('#addressStateDesc').val('NOT FOUND');
				$('#addressStateCode').val('');
				$('#addressState').val('');
				$('#h_addressStateDesc').val('');
			}
		},
		filter: {'key':'1','value':$('#addressCountry')},
		key:$('#addressState'),
		description:$('#addressStateDesc'),
		help:$('#addressStateHelp'),
		nextControl:$('#addressAreaCode')
	});
	// ======================================================================================================================
}

function area(data) {
	// address 1 area lookup at tab contacts ================================================================================
	$('#addressAreaCode').lookup({
		list:'@{Pick.lookups()}?group='+data,
		get:{
			url:'@{Pick.lookup()}?group='+data,
			success: function(data) {
				if (data) {
					if ($('#addressStateCode').val() == "" || $('#addressStateCode').val() == null) {
						$('#addressAreaCode').addClass('fieldError');
						$('#addressAreaDesc').val('NOT FOUND');
						$('#addressAreaCode').val('');
						$('#addressArea').val('');
						$('#h_addressAreaDesc').val('');
						return false;
					}
					$('#addressAreaCode').removeClass('fieldError');
					$('#addressArea').val(data.code);
					$('#addressAreaDesc').val(data.description);
					$('#h_addressAreaDesc').val(data.description);
				}
			},
			error : function(data) {
				$('#addressAreaCode').addClass('fieldError');
				$('#addressAreaDesc').val('NOT FOUND');
				$('#addressAreaCode').val('');
				$('#addressArea').val('');
				$('#h_addressAreaDesc').val('');
			}
		},
		filter:{'key':'2','value':$('#addressState')},
		key:$('#addressArea'),
		description:$('#addressAreaDesc'),
		help:$('#addressAreaHelp'),
		nextControl:$('#addressZipCode')
	});
	// ======================================================================================================================
}

function tabBankAccount(id, data, name){
	$(id).dataTable({
		aaData: data,
		aoColumns: [
					{
						fnRender: function(obj){
							var controls;
							controls = obj.aData.accountNo;
							controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].participantAccount" value="' + obj.aData.participantAccount + '" />';
							controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].accountNo" value="' + obj.aData.accountNo + '" />';
							return controls;
						}
					},
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		controls = obj.aData.bankCode.thirdPartyName;
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].bankCode.thirdPartyCode" value="' + obj.aData.bankCode.thirdPartyCode + '" />';
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].bankCode.thirdPartyKey" value="' + obj.aData.bankCode.thirdPartyKey + '" />';
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].bankCode.thirdPartyName" value="' + obj.aData.bankCode.thirdPartyName + '" />';
		            		return controls;
		            	}
		            },
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		controls = obj.aData.accountName;
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].accountName" value="' + obj.aData.accountName + '" />';
		            		return controls;
		            	}
		            },
		            
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		controls = obj.aData.currency.currencyCode;
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].currency.currencyCode" value="' + obj.aData.currency.currencyCode + '" />';
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].currency.description" value="' + obj.aData.currency.description + '" />';
		            		return controls;
		            	}
		            },
		            
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		controls = obj.aData.branchCode==null?"":obj.aData.branchCode;
		            		if (obj.aData.branchLevel == null) { obj.aData.branchLevel = {lookupId :'', lookupCode: '', lookupDescription:''} }
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].branchLevel.lookupId" value="' + obj.aData.branchLevel.lookupId + '" />';
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].branchLevel.lookupCode" value="' + obj.aData.branchLevel.lookupCode + '" />';
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].branchLevel.lookupDescription" value="' + obj.aData.branchLevel.lookupDescription + '" />';
		            		return controls;
		            	}
		            },
		            
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		if (obj.aData.usedBy == null) {
		            			obj.aData.usedBy = new Object();
		            			obj.aData.usedBy.lookupId = -1;
		            			obj.aData.usedBy.lookupDescription = '';
		            		}
		            		controls = obj.aData.usedBy.lookupDescription;
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].usedBy.lookupId" value="' + obj.aData.usedBy.lookupId + '" />';
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].usedBy.lookupDescription" value="' + obj.aData.usedBy.lookupDescription + '" />';
		            		return controls;
		            	}
		            },
		            
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].branchCode" value="' + obj.aData.branchCode + '" />';
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].bicCode" value="' + obj.aData.bicCode + '" />';
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].description" value="' + obj.aData.description + '" />';
		            		controls += '<input type="hidden" name="'+name+'[' + obj.iDataRow + '].custodianBankKey" value="' + obj.aData.custodianBankKey + '" />';
		            		
		            		return controls;
		            	}
		            }
		            ],
		            aaSorting:[[0, 'asc']],
		    		bAutoWidth: false,
		    		bFilter: false,
		    		bInfo: false,
		    		bJQueryUI:true,
		    		bPaginate: false,
		    		bSearch: false,
		    		bLengthChange:false
	});
	
	$('#gridBankAccountDebetKsei').removeAttr('style');
	$('#gridBankAccountDebetKsei tbody tr td').die('click');
	$('#gridBankAccountDebetKsei tbody tr td').live('click', function(){
		
		var rowPos = $(this).parents('tr');
		var table = $('#gridBankAccountDebetKsei').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);
		cell = table.fnGetData(this.parentNode);
		if (pos[1] != 6) {
			
			data = table.fnGetData(this.parentNode);
			openBankAccountDialogForEdit(data, TYPE_DEBET_KSEI, rowPosNumber);
			
		};
	});
	
	$('#gridBankAccountCreditKsei').removeAttr('style');
	$('#gridBankAccountCreditKsei tbody tr td').die('click');
	$('#gridBankAccountCreditKsei tbody tr td').live('click', function(){
		
		var rowPos = $(this).parents('tr');
		var table = $('#gridBankAccountCreditKsei').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);
		cell = table.fnGetData(this.parentNode);
		if (pos[1] != 6) {
			
			data = table.fnGetData(this.parentNode);
			openBankAccountDialogForEdit(data, TYPE_CREDIT_KSEI, rowPosNumber);
			
		};
	});
	
	$('#gridBankAccountDebetBi').removeAttr('style');
	$('#gridBankAccountDebetBi tbody tr td').die('click');
	$('#gridBankAccountDebetBi tbody tr td').live('click', function(){
		
		var rowPos = $(this).parents('tr');
		var table = $('#gridBankAccountDebetBi').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);
		cell = table.fnGetData(this.parentNode);
		if (pos[1] != 6) {
			
			data = table.fnGetData(this.parentNode);
			openBankAccountDialogForEdit(data, TYPE_DEBET_BI, rowPosNumber);
			
		};
	});
	
	$('#gridBankAccountCreditBi').removeAttr('style');
	$('#gridBankAccountCreditBi tbody tr td').die('click');
	$('#gridBankAccountCreditBi tbody tr td').live('click', function(){
		
		var rowPos = $(this).parents('tr');
		var table = $('#gridBankAccountCreditBi').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);
		cell = table.fnGetData(this.parentNode);
		if (pos[1] != 6) {
			
			data = table.fnGetData(this.parentNode);
			openBankAccountDialogForEdit(data, TYPE_CREDIT_BI, rowPosNumber);
			
		};
	});
	
	$('#gridBankAccountDebetEuro').removeAttr('style');
	$('#gridBankAccountDebetEuro tbody tr td').die('click');
	$('#gridBankAccountDebetEuro tbody tr td').live('click', function(){
		
		var rowPos = $(this).parents('tr');
		var table = $('#gridBankAccountDebetEuro').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);
		cell = table.fnGetData(this.parentNode);
		if (pos[1] != 6) {
			
			data = table.fnGetData(this.parentNode);
			openBankAccountDialogForEdit(data, TYPE_DEBET_EURO, rowPosNumber);
			
		};
	});
	
	$('#gridBankAccountCreditEuro').removeAttr('style');
	$('#gridBankAccountCreditEuro tbody tr td').die('click');
	$('#gridBankAccountCreditEuro tbody tr td').live('click', function(){
		
		var rowPos = $(this).parents('tr');
		var table = $('#gridBankAccountCreditEuro').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);
		cell = table.fnGetData(this.parentNode);
		if (pos[1] != 6) {
			
			data = table.fnGetData(this.parentNode);
			openBankAccountDialogForEdit(data, TYPE_CREDIT_EURO, rowPosNumber);
			
		};
	});
	
	$('#gridBankAccountDebetPool').removeAttr('style');
	$('#gridBankAccountDebetPool tbody tr td').die('click');
	$('#gridBankAccountDebetPool tbody tr td').live('click', function(){		
		var rowPos = $(this).parents('tr');
		var table = $('#gridBankAccountDebetPool').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);
		cell = table.fnGetData(this.parentNode);
		if (pos[1] != 6) {			
			data = table.fnGetData(this.parentNode);
			openBankAccountDialogForEdit(data, TYPE_DEBET_POOL, rowPosNumber);			
		};
	});
	
	$('#gridBankAccountCreditPool').removeAttr('style');
	$('#gridBankAccountCreditPool tbody tr td').die('click');
	$('#gridBankAccountCreditPool tbody tr td').live('click', function(){		
		var rowPos = $(this).parents('tr');
		var table = $('#gridBankAccountCreditPool').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);
		cell = table.fnGetData(this.parentNode);
		if (pos[1] != 6) {			
			data = table.fnGetData(this.parentNode);
			openBankAccountDialogForEdit(data, TYPE_CREDIT_POOL, rowPosNumber);			
		};
	});
}

function openBankAccountDialog(type){
	$("#bankAccountType").val('');
	$("#dialogBankAccount").dialog('open');

	if ((type == "DK") || (type == "CK")) {
		$("#participantAccountCashId").show();
	}else {
		$("#participantAccountCashId").hide();
	}
	$('.ui-widget-overlay').css('height',$('body').height());
	$('.clearData').val('');
	$('#dialogType').val(type);
	$('#dialogId').val("-1");
	$('#dialogBankAccount .error').html('');
	$('#dialogBankAccount input').removeClass('fieldError');
	
}

function openBankAccountDialogForEdit(data, type, rowPosition) {
	$("#dialogBankAccount #dialogType").val(type);
	$("#dialogBankAccount #dialogId").val(rowPosition);
	$("#dialogBankAccount #bankCode").val(data.bankCode.thirdPartyCode);
	$("#dialogBankAccount #bankCodeKey").val(data.bankCode.thirdPartyKey);
	$("#dialogBankAccount #bankCodeDesc").val(data.bankCode.thirdPartyName);
	$("#dialogBankAccount #bankBranch").val(data.branchCode);
	$("#dialogBankAccount #bankAccountNo").val(data.accountNo);
	$("#dialogBankAccount #participantAccount").val(data.participantAccount);
	$("#dialogBankAccount #accountHolderName").val(data.accountName);
	$("#dialogBankAccount #bankCurrency").val(data.currency.currencyCode);
	$("#dialogBankAccount #bankCurrencyDesc").val(data.currency.description);
	$("#dialogBankAccount #bicCode").val(data.bicCode);
	$('#dialogBankAccount #bankReferences').val(data.description);
	$('#dialogBankAccount #custodianBankKey').val(data.custodianBankKey);
	$('#dialogBankAccount #oldBankAccountNo').val($('#dialogBankAccount #bankAccountNo').val());
	$('#dialogBankAccount #newBankAccountNo').val($('#dialogBankAccount #oldBankAccountNo').val());
	$('#dialogBankAccount #oldCurrencyCode').val($('#dialogBankAccount #bankCurrency').val());
	$('#dialogBankAccount #newCurrencyCode').val($('#dialogBankAccount #oldCurrencyCode').val());
	$('#dialogBankAccount #oldBankCode').val($('#dialogBankAccount #bankCode').val());
	$('#dialogBankAccount #newBankCode').val($('#dialogBankAccount #oldBankCode').val());
	$('#dialogBankAccount #oldUsedBy').val($('#dialogBankAccount #usedBy').val());
	$('#dialogBankAccount #newUsedBy').val($('#dialogBankAccount #oldUsedBy').val());
	
	if (data.usedBy.lookupId == -1) {
		$('#usedBy').val("");
	}else{
		$('#usedBy').val(data.usedBy.lookupId);
	}
	
	$('#dialogBankAccount input').removeClass('fieldError');
	$('#dialogBankAccount .error').html("");
	
	if ((type == "DK") || (type == "CK")) {
		$("#participantAccountCashId").show();
	}else {
		$("#participantAccountCashId").hide();
	}
	
	$("#dialogBankAccount").dialog('open');
	$("#dialogBankAccount #bankAccountNo").focus();
	$('.ui-widget-overlay').css('height',$('body').height());
};

function fillAddress(){
	var address1 = $('#address1').val().toUpperCase();
	var address2 = $('#address2').val().toUpperCase();
	var address3 = $('#address3').val().toUpperCase();
	
	if ((address1 != '')||(address2 != '')||(address3 != '')){
		if (address1 !='') {
			$('#addressHide').val(address1);
			
		}
		if ((address1 != '')&&(address2!='')){
			$('#addressHide').val(address1+"\n"+address2);	
		}
		
		if ((address1 != '')&&(address3!='')){
			$('#addressHide').val(address1+"\n"+address3);	
		}
		
		if ((address1 != '')&&(address2!='')&&(address3!='')){
			$('#addressHide').val(address1+"\n"+address2+"\n"+address3);
		}
		
	}
};

function tabInvoice(data){
	tableInvoice = $('#gridInvoice').dataTable({
		aaData: data,
		aoColumns: [
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		controls = obj.aData.invoiceGroup.lookupDescription;
		            		controls += '<input type="hidden" name="invoices[' + obj.iDataRow + '].invoiceGroup.lookupDescription" value="' + obj.aData.invoiceGroup.lookupDescription + '" />';
		            		controls += '<input type="hidden" name="invoices[' + obj.iDataRow + '].invoiceGroup.lookupId" value="' + obj.aData.invoiceGroup.lookupId + '" />';
		            		return controls;
		            	}
		            },
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		controls = obj.aData.taxMaster.taxCode;
		            		controls += '<input type="hidden" name="invoices[' + obj.iDataRow + '].taxMaster.taxCode" value="' + obj.aData.taxMaster.taxCode + '" />';
		            		controls += '<input type="hidden" name="invoices[' + obj.iDataRow + '].taxMaster.taxKey" value="' + obj.aData.taxMaster.taxKey + '" />';
		            		controls += '<input type="hidden" name="invoices[' + obj.iDataRow + '].taxMaster.description" value="' + obj.aData.taxMaster.description + '" />';
		            		return controls;
		            	}
		            },
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
		            		return controls;
		            	}
		            }
		            ],
		            aaSorting:[[0, 'asc']],
		    		bAutoWidth: false,
		    		bFilter: false,
		    		bInfo: false,
		    		bJQueryUI:true,
		    		bPaginate: false,
		    		bSearch: false,
		    		bLengthChange:false
	});
	
	$('#gridInvoice').removeAttr('style');
	$('#gridInvoice tbody tr td').die('click');
	$('#gridInvoice tbody tr td').live('click', function(){
		
		var rowPos = $(this).parents('tr');
		var table = $('#gridInvoice').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);
		cell = table.fnGetData(this.parentNode);
		if (pos[1] != 2) {
			
			data = table.fnGetData(this.parentNode);
			$('#dialogInvoice #rowPosition').val(rowPosNumber);
			$('#dialogInvoice #chargeGroup').val(data.invoiceGroup.lookupId);
			$('#dialogInvoice #chargeGroupDesc').val(data.invoiceGroup.lookupDescription);
			$('#dialogInvoice #invoiceTaxCode').val(data.taxMaster.taxCode);
			$('#dialogInvoice #invoiceTaxKey').val(data.taxMaster.taxKey);
			$('#dialogInvoice #invoiceTaxDesc').val(data.taxMaster.description);
			$('#dialogInvoice #oldChargeGroup').val($('#dialogInvoice #chargeGroupDesc').val());
			$('#dialogInvoice #newChargeGroup').val($('#dialogInvoice #oldChargeGroup').val());
			$("#dialogInvoice").dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
		};
	});
	
}

function tabComplience(data){
	tableComplience = $('#gridCompliance').dataTable({
		aaData: data,
		aoColumns: [
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		if((obj.aData.securityType==null)||(controls=='null')){
		            			controls="";
							}
		            		controls = obj.aData.securityType.securityType;
		            		controls += '<input type="hidden" name="complience[' + obj.iDataRow + '].securityType.securityType" value="' + obj.aData.securityType.securityType + '" />';
		            		controls += '<input type="hidden" name="complience[' + obj.iDataRow + '].securityType.description" value="' + obj.aData.securityType.description + '" />';
		            		return controls;
		            	}
		            },
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		controls = obj.aData.priceReference.lookupCode;
		            		controls += '<input type="hidden" name="complience[' + obj.iDataRow + '].priceReference.lookupCode" value="' + obj.aData.priceReference.lookupCode + '" />';
		            		controls += '<input type="hidden" name="complience[' + obj.iDataRow + '].priceReference.lookupId" value="' + obj.aData.priceReference.lookupId + '" />';
		            		controls += '<input type="hidden" name="complience[' + obj.iDataRow + '].priceReference.lookupDescription" value="' + obj.aData.priceReference.lookupDescription + '" />';
		            		return controls;
		            	}
		            },
		            {
		            	fnRender: function(obj){
		            		var controls;
		            		controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
		            		return controls;
		            	}
		            }
		            ],
		            aaSorting:[[0, 'asc']],
		    		bAutoWidth: false,
		    		bFilter: false,
		    		bInfo: false,
		    		bJQueryUI:true,
		    		bPaginate: false,
		    		bSearch: false,
		    		bLengthChange:false
	});
	
	$('#gridCompliance').removeAttr('style');
	$('#gridCompliance tbody tr td').die('click');
	$('#gridCompliance tbody tr td').live('click', function(){
		
		var rowPos = $(this).parents('tr');
		var table = $('#gridCompliance').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);
		cell = table.fnGetData(this.parentNode);
		$('#dialogComplience #groupCodeError').html('');
		$('#dialogComplience #securityTypeError').html('');
		$('#dialogComplience #complianceGeneralMsg').html('');		
		if (pos[1] != 2) {
			
			data = table.fnGetData(this.parentNode);
			$('#dialogComplience #rowPosition').val(rowPosNumber);
			$('#dialogComplience #securityType').val(data.securityType.securityType);
			$('#dialogComplience #securityTypeDescription').val(data.securityType.description);
			$('#dialogComplience #groupCode').val(data.priceReference.lookupCode);
			$('#dialogComplience #groupKey').val(data.priceReference.lookupId);
			$('#dialogComplience #groupDesc').val(data.priceReference.lookupDescription);
			$("#dialogComplience").dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
		};
	});
	
}