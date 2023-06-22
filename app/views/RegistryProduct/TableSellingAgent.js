$(function() {
	
	$('#dialogSellingAgent #sellingAgentCode').lookup({
		list:'@{Pick.thirdPartiesSaCode()}',
		get : {
			url : '@{Pick.thirdPartySaCode()}',
			success : function(data) {
				$("#dialogSellingAgent #sellingAgentForm span[id='sellingAgentCodeError']").html("");
				$("#dialogSellingAgent #sellingAgentForm #sellingAgentCode").removeClass('fieldError');
				$("#dialogSellingAgent #sellingAgentForm #sellingAgentCode").val(data.name);
				$("#dialogSellingAgent #sellingAgentForm #sellingAgentCodeDesc").val(data.description);
				$("#dialogSellingAgent #sellingAgentForm #sellingAgentKey").val(data.code);
				
			},
			error: function(data) {
				$("#dialogSellingAgent #sellingAgentForm #sellingAgentCode").addClass('fieldError');
				$("#dialogSellingAgent #sellingAgentForm #sellingAgentCode").val('');
				$("#dialogSellingAgent #sellingAgentForm #sellingAgentCodeDesc").val('NOT FOUND');
				$("#dialogSellingAgent #sellingAgentForm #sellingAgentKey").val('');
			}
		},
		description: $("#sellingAgentDesc"),
		help: $("#sellingAgentHelp")
	});
	
	$('#dialogSellingAgent #bankAccountCode').lookup({
		list:'@{Pick.thirdPartiesBanks()}',
		get : {
			url:'@{Pick.thirdPartyBankByCode()}',
			success: function(data){
				$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").removeClass("fieldError");
				$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").val(data.name);
				$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeDesc").val(data.description);
				$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeKey").val(data.code);
				
			},
			error: function(data){
				$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").addClass('fieldError');
				$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").val('');
				$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeDesc").val('NOT FOUND');
				$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeKey").val('');
			}
		},
		filter : $("#customerNoKey"),
		description: $("#bankAccountCodeDesc"),
		help: $("#bankAccountCodeHelp")
	});
	
	$('#dialogSellingAgent #currencyCode').lookup({
		list:'@{Pick.currencies()}',
		get : {
			url:'@{Pick.currency()}',
            success: function(data) {

                $('#currencyCode').removeClass('fieldError');
                $('#currencyCode').val(data.code);
                $('#currencyCodeDesc').val(data.description);
            },
            error: function(data){
                $('#currencyCode').addClass('fieldError');
                $('#currencyCode').val('');
                $('#currencyCodeDesc').val('NOT FOUND');
            }
		},
		description: $("#currencyCodeDesc"),
		help: $("#currencyCodeHelp")
	});
	
	$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").blur(function(){
		if ($(this).val() != '') {
			$("#dialogSellingAgent #sellingAgentForm span[id='bankAccountCodeError']").html("");
		}
	});
	
	$("#dialogSellingAgent #sellingAgentForm #accountNo").blur(function(){
		if ($(this).val() != '') {
			$("#dialogSellingAgent #sellingAgentForm span[id='accountNoError']").html("");
		}
	});
	
	$("#dialogSellingAgent #sellingAgentForm #beneficiaryName").blur(function(){
		if ($(this).val() != '') {
			$("#dialogSellingAgent #sellingAgentForm span[id='beneficiaryNameError']").html("");
		}
	});
	
	$("#dialogSellingAgent #sellingAgentForm #currencyCode").blur(function(){
		if ($(this).val() != '') {
			$("#dialogSellingAgent #sellingAgentForm span[id='currencyCodeError']").html("");
		}
	});
	
	$("#dialogSellingAgent #sellingAgentForm #branch").blur(function(){
		if ($(this).val() != '') {
			$("#dialogSellingAgent #sellingAgentForm span[id='branchError']").html("");
		}
	});
	
	$("#dialogSellingAgent #sellingAgentForm #description").blur(function(){
		if ($(this).val() != '') {
			$("#dialogSellingAgent #sellingAgentForm span[id='descriptionError']").html("");
		}
	});
	
	function resetDialogSellingAgentFields(){
		
		targetAcct2();
		
		$("#dialogSellingAgent #sellingAgentCode").val("");
		$("#dialogSellingAgent #sellingAgentCodeDesc").val("");
		$("#dialogSellingAgent #sellingAgentForm span[id='sellingAgentCodeError']").html("");
		
		$("#dialogSellingAgent #paymentType").val("");
		$("#dialogSellingAgent #sellingAgentForm span[id='paymentTypeError']").html("");
		
		$("#dialogSellingAgent #targetAccountType").val("");
		$("#dialogSellingAgent #sellingAgentForm span[id='targetAccountTypeError']").html("");
		
		$("#dialogSellingAgent #accountNo").val("");
		$("#dialogSellingAgent #sellingAgentForm span[id='accountNoError']").html("");
		
		$("#dialogSellingAgent #bankAccountCode").val("");
		$("#dialogSellingAgent #bankAccountCodeDesc").val("");
		$("#dialogSellingAgent #sellingAgentForm span[id='bankAccountCodeError']").html("");
		
		$("#dialogSellingAgent #currencyCode").val("");
		$("#dialogSellingAgent #currencyCodeDesc").val("");
		$("#dialogSellingAgent #sellingAgentForm span[id='currencyCodeError']").html("");
		
		$("#dialogSellingAgent #beneficiaryName").val("");
		$("#dialogSellingAgent #sellingAgentForm span[id='beneficiaryNameError']").html("");
		
		$("#dialogSellingAgent #description").val("");
		$("#dialogSellingAgent #sellingAgentForm span[id='descriptionError']").html("");
		
		$("#dialogSellingAgent #branch").val("");
		$("#dialogSellingAgent #sellingAgentForm span[id='branchError']").html("");
		
		$("#dialogSellingAgent #transferCharge").val("");
		$("#dialogSellingAgent #sellingAgentForm span[id='transferChargeError']").html("");
		
		$("#dialogSellingAgent #rowPosition").val("");
	}
	
	var closeDialog = function() {
        $("#dialog-message").dialog('close');   
    }

	var data = new Object();
	sellingAgent(data);
    

	$('#addSellingAgent').button();
	$('#cancelSellingAgent').button();

	$("#dialogSellingAgent").dialog({
		autoOpen:false,
		modal:true,
		heigth:'600px',
		width:'600px',
		resizable:false
	});
	
	
	$("#dialogSellingAgent #sellingAgentForm #targetAccountType").live('change', function(){
		
		$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").val('');
		$("#dialogSellingAgent #sellingAgentForm span[id='bankAccountCodeError']").html("");
		
		$("#dialogSellingAgent #sellingAgentForm #accountNo").val('');
		$("#dialogSellingAgent #sellingAgentForm span[id='accountNoError']").html("");
		
		$("#dialogSellingAgent #sellingAgentForm #beneficiaryName").val('');
		$("#dialogSellingAgent #sellingAgentForm span[id='beneficiaryNameError']").html("");
		
		$("#dialogSellingAgent #sellingAgentForm #currencyCode").val('');
		$("#dialogSellingAgent #sellingAgentForm span[id='currencyCodeError']").html("");
		
		$("#dialogSellingAgent #sellingAgentForm #branch").val('');
		$("#dialogSellingAgent #sellingAgentForm span[id='branchError']").html("");
		
		$("#dialogSellingAgent #sellingAgentForm #description").val('');
		$("#dialogSellingAgent #sellingAgentForm span[id='descriptionError']").html("");
		
		var targetAccountTypeDesc = '';
		
    	if ($(this).val() == 'TARGET_ACCOUNT-2' ) {
    		
    		$("#dialogSellingAgent #sellingAgentForm #idBankAccountCode span").html("*");
    		$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").enabled();
    		$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeHelp").enabled();
//    		$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").required("true");
    		$("#dialogSellingAgent #sellingAgentForm span[id='bankAccountCodeError']").html("");
    		
    		$("#dialogSellingAgent #sellingAgentForm #idAccountNo span").html("*");
    		$("#dialogSellingAgent #sellingAgentForm #accountNo").enabled();
//    		$("#dialogSellingAgent #sellingAgentForm #accountNo").required("true");
    		$("#dialogSellingAgent #sellingAgentForm span[id='accountNoError']").html("");
    		
    		$("#dialogSellingAgent #sellingAgentForm #idBeneficiaryName span").html("*");
    		$("#dialogSellingAgent #sellingAgentForm #beneficiaryName").enabled();
//    		$("#dialogSellingAgent #sellingAgentForm #beneficiaryName").required("true");
    		$("#dialogSellingAgent #sellingAgentForm span[id='beneficiaryNameError']").html("");
    		
    		$("#dialogSellingAgent #sellingAgentForm #idCurrencyCode span").html("*");
    		$("#dialogSellingAgent #sellingAgentForm #currencyCode").enabled();
    		$("#dialogSellingAgent #sellingAgentForm #currencyCodeHelp").enabled();
//    		$("#dialogSellingAgent #sellingAgentForm #currencyCode").required("true");
    		$("#dialogSellingAgent #sellingAgentForm span[id='currencyCodeError']").html("");
    		
//    		$("#dialogSellingAgent #sellingAgentForm #idBranch span").html("*");
    		$("#dialogSellingAgent #sellingAgentForm #branch").enabled();
//    		$("#dialogSellingAgent #sellingAgentForm #branch").required("true");
    		$("#dialogSellingAgent #sellingAgentForm span[id='branchError']").html("");
    		
//    		$("#dialogSellingAgent #sellingAgentForm #idDescription span").html("*");
    		$("#dialogSellingAgent #sellingAgentForm #description").enabled();
//    		$("#dialogSellingAgent #sellingAgentForm #description").required("true");
    		$("#dialogSellingAgent #sellingAgentForm span[id='descriptionError']").html("");
    		
    		$("#dialogSellingAgent #sellingAgentForm #idBranch span").html("");
    		$("#dialogSellingAgent #sellingAgentForm #idDescription span").html("");
    		
    		targetAccountTypeDesc = '${targetAcct2.lookupDescription}';
    		$("#transferCharge").val("${chargeOnsender}");
        	$("#transferCharge").trigger('change');	    		
    	}else if($(this).val() == 'TARGET_ACCOUNT-1' ) {
    		targetAcct1();
    		targetAccountTypeDesc = '${targetAcct1.lookupDescription}';   
    	}else{
    		targetAcctMan();
    		targetAccountTypeDesc = '${targetAcctManual.lookupDescription}';
    	}
    	var targetAccCode = $(this).val().split('-');
    	
    	$("#dialogSellingAgent #sellingAgentForm #targetAccountTypeCode").val(targetAccCode[1]);
    	console.log("targetAccountTypeDesc :" + targetAccountTypeDesc);
    	$("#dialogSellingAgent #sellingAgentForm #targetAccountTypeDescription").val(targetAccountTypeDesc);
    	$("#dialogSellingAgent #sellingAgentForm span[id='targetAccountTypeError']").html("");
    	
    });
	
	$("#dialogSellingAgent #sellingAgentForm #transferCharge").live('change', function(){
		if ($(this).val() != '') {
			$("#dialogSellingAgent #sellingAgentForm #transferChargeCode").val($("#transferCharge option:selected").text());
	    	$("#dialogSellingAgent #sellingAgentForm #transferChargecription").val($(this).val());
		}
	});
	
    $("#listSellingAgent #sellingAgentTable tbody tr #deleteButtonSellingAgent[disabled!=true]").live("click", function() {
    	
    	var row = $(this).parents('tr');
        var rowNumber = tableSellingAgent.fnGetPosition(row[0]);

		var data = tableSellingAgent.fnGetData(row[0]);
    	var bankAccKey = data.bankAccountKey;
    	var sellingAgent = data.saCode.thirdPartyKey;
    	var isPayable  = checkIsPayableRgTrade($("#productCode").val(), sellingAgent);
    	console.log('isPayable : ' + isPayable);
		if((isPayable == 'true') && (bankAccKey != '') && (bankAccKey != null)) {
			var payableFunc = function(){
				$("#dialog-message").dialog("close");
			}
			messageAlertOk("Can not delete this Selling Agent Account. Still exist payable transaction", "ui-icon ui-icon-notice", "Warning", payableFunc);
		}else {
			var row = $(this).parents('tr');
	        var rowNumber = tableSellingAgent.fnGetPosition(row[0]);

			var data = tableSellingAgent.fnGetData(row[0]);

			tableSellingAgent.fnDeleteRow(rowNumber);
			$("#dialog-message").dialog("close");
		}
        
		return false;
	});
    
    function targetAcct1(){
    	
    	if ($("#dialogSellingAgent #targetAccountType").val() == 'TARGET_ACCOUNT-1')  {
    		$("#dialogSellingAgent #sellingAgentForm #idBankAccountCode span").html("");
    		$("#dialogSellingAgent #sellingAgentForm #idAccountNo span").html("");
    		$("#dialogSellingAgent #sellingAgentForm #idBeneficiaryName span").html("");
    		$("#dialogSellingAgent #sellingAgentForm #idCurrencyCode span").html("");
    		$("#dialogSellingAgent #sellingAgentForm #idBranch span").html("");
    		$("#dialogSellingAgent #sellingAgentForm #idDescription span").html("");
    		
    		$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeDesc").val("");
    		$("#dialogSellingAgent #sellingAgentForm #currencyCodeDesc").val("");
    	}
    	
		
		$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").disabled();
		$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeHelp").disabled();
		$("#dialogSellingAgent #sellingAgentForm #accountNo").disabled();
		$("#dialogSellingAgent #sellingAgentForm #beneficiaryName").disabled();
		$("#dialogSellingAgent #sellingAgentForm #currencyCode").disabled();
		$("#dialogSellingAgent #sellingAgentForm #currencyCodeHelp").disabled();
		$("#dialogSellingAgent #sellingAgentForm #branch").disabled();
		$("#dialogSellingAgent #sellingAgentForm #description").disabled();
    }
    
    function targetAcctMan(){
    	
		$("#dialogSellingAgent #sellingAgentForm #idBankAccountCode span").html("");
		$("#dialogSellingAgent #sellingAgentForm #idAccountNo span").html("");
		$("#dialogSellingAgent #sellingAgentForm #idBeneficiaryName span").html("");
		$("#dialogSellingAgent #sellingAgentForm #idCurrencyCode span").html("");
		$("#dialogSellingAgent #sellingAgentForm #idBranch span").html("");
		$("#dialogSellingAgent #sellingAgentForm #idDescription span").html("");
		
		$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeDesc").val("");
		$("#dialogSellingAgent #sellingAgentForm #currencyCodeDesc").val("");
    	
		
		$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").disabled();
		$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeHelp").disabled();
		$("#dialogSellingAgent #sellingAgentForm #accountNo").disabled();
		$("#dialogSellingAgent #sellingAgentForm #beneficiaryName").disabled();
		$("#dialogSellingAgent #sellingAgentForm #currencyCode").disabled();
		$("#dialogSellingAgent #sellingAgentForm #currencyCodeHelp").disabled();
		$("#dialogSellingAgent #sellingAgentForm #branch").disabled();
		//$("#dialogSellingAgent #sellingAgentForm #description").disabled();
		//$('#description').attr('disabled', false);
    }
    
    function targetAcct2(){
    	console.log("mode ::" + '${mode}');
    	console.log("'${confirming}'" + '${confirming}');
    	
    	$("#dialogSellingAgent #sellingAgentForm #idBankAccountCode span").html("*");
		$("#dialogSellingAgent #sellingAgentForm span[id='bankAccountCodeError']").html("");
		
		$("#dialogSellingAgent #sellingAgentForm #idAccountNo span").html("*");
		$("#dialogSellingAgent #sellingAgentForm span[id='accountNoError']").html("");
		
		$("#dialogSellingAgent #sellingAgentForm #idBeneficiaryName span").html("*");
		$("#dialogSellingAgent #sellingAgentForm span[id='beneficiaryNameError']").html("");
		
		$("#dialogSellingAgent #sellingAgentForm #idCurrencyCode span").html("*");
		$("#dialogSellingAgent #sellingAgentForm span[id='currencyCodeError']").html("");
		
		$("#dialogSellingAgent #sellingAgentForm span[id='branchError']").html("");
		$("#dialogSellingAgent #sellingAgentForm span[id='descriptionError']").html("");
		
		$("#dialogSellingAgent #sellingAgentForm #idBranch span").html("");
		$("#dialogSellingAgent #sellingAgentForm #idDescription span").html("");
    	
    	if ('${mode}' == 'view' || '${confirming}' == 'true' ) {
    		$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").disabled();
    		$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeHelp").disabled();
    		$("#dialogSellingAgent #sellingAgentForm #accountNo").disabled();
    		$("#dialogSellingAgent #sellingAgentForm #beneficiaryName").disabled();
    		$("#dialogSellingAgent #sellingAgentForm #currencyCode").disabled();
    		$("#dialogSellingAgent #sellingAgentForm #currencyCodeHelp").disabled();
    		$("#dialogSellingAgent #sellingAgentForm #branch").disabled();
    		$("#dialogSellingAgent #sellingAgentForm #description").disabled();    		
    	}else {    		
    		$("#dialogSellingAgent #sellingAgentForm #bankAccountCode").enabled();
    		$("#dialogSellingAgent #sellingAgentForm #bankAccountCodeHelp").enabled();
    		$("#dialogSellingAgent #sellingAgentForm #accountNo").enabled();
    		$("#dialogSellingAgent #sellingAgentForm #beneficiaryName").enabled();
    		$("#dialogSellingAgent #sellingAgentForm #currencyCode").enabled();
    		$("#dialogSellingAgent #sellingAgentForm #currencyCodeHelp").enabled();
    		$("#dialogSellingAgent #sellingAgentForm #branch").enabled();
    		$("#dialogSellingAgent #sellingAgentForm #description").enabled();
    	}
		
    }
    

    $('#addSellingAgent').die("click");
    $('#addSellingAgent').live("click", function(){

    	setTimeout(function() {
	        var table = $("#listSellingAgent #sellingAgentTable").dataTable();
	        var rowPosition = $("#dialogSellingAgent #sellingAgentForm #rowPosition").val();
	        var sellingAgentKey = $('#dialogSellingAgent #sellingAgentForm #sellingAgentKey');
	        var sellingAgentCode = $('#dialogSellingAgent #sellingAgentForm #sellingAgentCode');
	        var sellingAgentName = $('#dialogSellingAgent #sellingAgentForm #sellingAgentCodeDesc');
//	        var paymentType = $('#dialogSellingAgent #sellingAgentForm #paymentType');
	        var targetAccountType = $('#dialogSellingAgent #sellingAgentForm #targetAccountType');
	        var accountNo = $('#dialogSellingAgent #sellingAgentForm #accountNo');
	        var bankAccountCodeKey = $('#dialogSellingAgent #sellingAgentForm #bankAccountCodeKey');
	        var bankAccountCode = $('#dialogSellingAgent #sellingAgentForm #bankAccountCode');
	        var bankAccountCodeName = $('#dialogSellingAgent #sellingAgentForm #bankAccountCodeDesc');
	        var currencyCode = $('#dialogSellingAgent #sellingAgentForm #currencyCode');
	        var currencyCodeDesc = $('#dialogSellingAgent #sellingAgentForm #currencyCodeDesc');
	        var beneficiaryName = $('#dialogSellingAgent #sellingAgentForm #beneficiaryName');
	        var description = $('#dialogSellingAgent #sellingAgentForm #description');
	        var branch = $('#dialogSellingAgent #sellingAgentForm #branch');
	        
	        var targetAccountTypeCode = $("#dialogSellingAgent #sellingAgentForm #targetAccountTypeCode");
	        var targetAccountTypeDesc = $("#dialogSellingAgent #sellingAgentForm #targetAccountTypeDescription");
	        var paymentTypeCode = $("#dialogSellingAgent #sellingAgentForm #paymentTypeCode");
	        
	        var transferCharge = $('#dialogSellingAgent #sellingAgentForm #transferCharge');
	        var transferChargeCode = $("#dialogSellingAgent #sellingAgentForm #transferChargeCode");
	        
	        saveSellingAgent();

	        function saveSellingAgent() {
	        	if (((sellingAgentCode.val()=='')|| (targetAccountType.val()=='')|| (bankAccountCode.val()=='') ||
	        			(currencyCode.val()=='') || (beneficiaryName.val()=='') || transferCharge.val()=='') 
	        			&& (targetAccountType.val() == 'TARGET_ACCOUNT-2') ) {
	        			// || (description.val() == '') || (branch.val()=='')
	        		if (sellingAgentCode.val()==''){
		    				$('#sellingAgentCodeError').html('Required');
		    			} else {
		    				$('#sellingAgentCodeError').html('');
		    			}
		        		
//		        		if (paymentType.val()==''){
//		    				$('#paymentTypeError').html('Required');
//		    			} else {
//		    				$('#paymentTypeError').html('');
//		    			}
		        		
		        		if (targetAccountType.val()==''){
		    				$('#targetAccountTypeError').html('Required');
		    			} else {
		    				$('#targetAccountTypeError').html('');
		    			}
		        		
		        		if (bankAccountCode.val()==''){
		    				$('#bankAccountCodeError').html('Required');
		    			} else {
		    				$('#bankAccountCodeError').html('');
		    			}
		        		
		        		if (currencyCode.val()==''){
		    				$('#currencyCodeError').html('Required');
		    			} else {
		    				$('#currencyCodeError').html('');
		    			}
		        		
		        		if (beneficiaryName.val()==''){
		    				$('#beneficiaryNameError').html('Required');
		    			} else {
		    				$('#beneficiaryNameError').html('');
		    			}
		        		
		        		if (transferCharge.val()==''){
		    				$('#transferChargeError').html('Required');
		    			} else {
		    				$('#transferChargeError').html('');
		    			}
		        		
//		        		if (description.val()==''){
//		    				$('#descriptionError').html('Required');
//		    			} else {
//		    				$('#descriptionError').html('');
//		    			}
//		        		
//		        		if (branch.val()==''){
//		    				$('#branchError').html('Required');
//		    			} else {
//		    				$('#branchError').html('');
//		    			}
		        		
		        		return false;
	        	
	        	}else {
	        		if (targetAccountType.val() == 'TARGET_ACCOUNT-2') {
	        			if (accountNo.val()==''){
		    				$('#accountNoError').html('Required'); 
		    				return false;
		    			} else {
		    				$('#accountNoError').html('');
		    			}
	        			
		        		if (bankAccountCode.val()==''){
		    				$('#bankAccountCodeError').html('Required');
		    				return false;
		    			} else {
		    				$('#bankAccountCodeError').html('');
		    			}
		        		
		        		if (currencyCode.val()==''){
		    				$('#currencyCodeError').html('Required');
		    				return false;
		    			} else {
		    				$('#currencyCodeError').html('');
		    			}
		        		
		        		if (beneficiaryName.val()==''){
		    				$('#beneficiaryNameError').html('Required');
		    				return false;
		    			} else {
		    				$('#beneficiaryNameError').html('');
		    			}
		        		
		        		
		        		if (transferCharge.val()==''){
		    				$('#transferChargeError').html('Required');
		    				return false;
		    			} else {
		    				$('#transferChargeError').html('');
		    			}
//		        		if (description.val()==''){
//		    				$('#descriptionError').html('Required');
//		    				return false;
//		    			} else {
//		    				$('#descriptionError').html('');
//		    			}
//		        		
//		        		if (branch.val()==''){
//		    				$('#branchError').html('Required');
//		    				return false;
//		    			} else {
//		    				$('#branchError').html('');
//		    			}
	        		}
	        		
	        		if ((targetAccountType.val() == '') || (sellingAgentCode.val()=='')) {
	        			
	        			if (sellingAgentCode.val()==''){
		    				$('#sellingAgentCodeError').html('Required');
		    				
		    			} else {
		    				$('#sellingAgentCodeError').html('');
		    			}
	        			
	        			if (targetAccountType.val()==''){
		    				$('#targetAccountTypeError').html('Required');
		    			} else {
		    				$('#targetAccountTypeError').html('');
		    			}
	        			return false;
	        		}

	                var dataSellingAgent = table.fnGetData(parseFloat(rowPosition));
	                if(rowPosition != "") {
	                	
	                    var found = false;
	                    // CHECKED DUPLICATE ROW (duplicate row adl jika
						// sellingAgent ada yg sama)-> based on redmine #2833
	                    var rows = table.fnGetNodes().length;
	                    for (i = 0; i < rows; i++) {
	                        var cells = table.fnGetData(i);
	                        
	                        // concate semua field2 mandatory
	                        var sellingAgentCodeCell = cells.saCode.thirdPartyCode;
//	                        var paymentTypeCell = cells.paymentType.lookupId;
	                        var targetAccountTypeCell = cells.targetAccountType.lookupId;
	                        var accountNoCell = '';
	                        var bankAccountCodeCell = '';
	                        var currencyCell = '';
	                        var branchCell = '';
	                        var descriptionCell = '';
	                        var beneficiaryNameCell = '';
	                        
	                        if (cells.accountNo != null) accountNoCell = cells.accountNo;
	                        if (cells.bankCode != null ) bankAccountCodeCell = cells.bankCode.thirdPartyCode;
	                        if (cells.currencyCode != null ) currencyCell = cells.currencyCode.currencyCode;
	                        if (cells.branch != null) branchCell = cells.branch;
	                        if (cells.description != null) descriptionCell = cells.description;
	                        if (cells.name != null) beneficiaryNameCell = cells.name;
	                        var transferChargeCell = cells.transferCharge.lookupId;
//	                        var concateAllMandatoryCell = sellingAgentCodeCell + targetAccountTypeCell + accountNoCell + bankAccountCodeCell
//	                        							+ currencyCell + beneficiaryNameCell + branchCell + descriptionCell ;
	                        var concateAllMandatoryCell = sellingAgentCodeCell;
	                        
	                        
	                        // concate semua field2 mandatory yang old
	                        var oldSellingAgentCode = $("#dialogSellingAgent #sellingAgentForm #oldSellingAgent").val();
//	                        var oldPaymentType = $("#dialogSellingAgent #sellingAgentForm #oldPaymentType").val();
	                        var oldTargetAccountType = $("#dialogSellingAgent #sellingAgentForm #oldTargetAccountType").val();
	                        var oldAccountNo = $("#dialogSellingAgent #sellingAgentForm #oldAccountNo").val();
	                        var oldBankAccount= $("#dialogSellingAgent #sellingAgentForm #oldBankAccount").val();
	                        var oldCurrency = $("#dialogSellingAgent #sellingAgentForm #oldCurrency").val();
	                        var oldBeneficiaryName = $("#dialogSellingAgent #sellingAgentForm #oldBeneficiaryName").val();
	                        var oldBranch = $("#dialogSellingAgent #sellingAgentForm #oldBranch").val();
	                        var oldDescription = $("#dialogSellingAgent #sellingAgentForm #oldDescription").val();
	                        var oldTransferChargeType = $("#dialogSellingAgent #sellingAgentForm #oldTransferCharge").val();
	                        
//	                        var concatenateAllMandatoryOld = oldSellingAgentCode + oldTargetAccountType + oldAccountNo + oldBankAccount
//	                        								+ oldCurrency + oldCurrency + oldBeneficiaryName + oldBranch + oldDescription; 
	                        
	                        var concatenateAllMandatoryOld = oldSellingAgentCode;
	                        // concate semua field2 mandatory yang new
//	            	        var concateAllMandatoryNew = sellingAgentCode.val() +  targetAccountType.val() + accountNo.val()
//	            	        							+ bankAccountCode.val() + currencyCode.val() + beneficiaryName.val() + branch.val() + description.val();
	                        
	                        var concateAllMandatoryNew = sellingAgentCode.val();
	                        
	            	        if (concatenateAllMandatoryOld != concateAllMandatoryNew) {
	            	        	
	            	        	if (concateAllMandatoryCell == concateAllMandatoryNew) {
	            	        		$("#dialogSellingAgent #sellingAgentForm #sellingAgentCodeError").html("This Selling Agent already Exist!");
	            	        		found = true;
	            	        		break;
	            	        	}
	            	        }
	                    } 
//	                    
	                    if(!found) {
	                    	var isPayable  = checkIsPayableRgTrade($("#productCode").val(), sellingAgentKey.val());

	                    	if(isPayable == 'true') {
	    	        			
	    	        			var yesSellingAgent = function() {
	    	        				
	    	        				table.fnUpdate(dataSellingAgent.saCode.thirdPartyKey = sellingAgentKey.val(),parseFloat(rowPosition), 0);
			                    	table.fnUpdate(dataSellingAgent.saCode.thirdPartyCode = sellingAgentCode.val(),parseFloat(rowPosition), 0);
			                    	table.fnUpdate(dataSellingAgent.saCode.thirdPartyName = sellingAgentName.val(),parseFloat(rowPosition), 0);
//			                    	table.fnUpdate(dataSellingAgent.paymentType.lookupId = paymentType.val(),parseFloat(rowPosition), 1);
//	    	                    	table.fnUpdate(dataSellingAgent.paymentType.lookupCode = paymentTypeCode.val(),parseFloat(rowPosition), 1);
	    	                    	table.fnUpdate(dataSellingAgent.targetAccountType.lookupId = targetAccountType.val(),parseFloat(rowPosition), 1);
	    	                    	table.fnUpdate(dataSellingAgent.targetAccountType.lookupCode = targetAccountTypeCode.val(),parseFloat(rowPosition), 1);
	    	                    	table.fnUpdate(dataSellingAgent.targetAccountType.lookupDescription = targetAccountTypeDesc.val(),parseFloat(rowPosition), 1);

	    	                    	if (dataSellingAgent.targetAccountType.lookupId  == 'TARGET_ACCOUNT-2') {
	    	                    		
			                    		table.fnUpdate(dataSellingAgent.accountNo = accountNo.val(),parseFloat(rowPosition), 2);
			                    		dataSellingAgent.bankCode = new Object();
			                    		dataSellingAgent.currencyCode = new Object();
			                    		table.fnUpdate(dataSellingAgent.bankCode.thirdPartyKey = bankAccountCodeKey.val(),parseFloat(rowPosition), 3);
				                    	table.fnUpdate(dataSellingAgent.bankCode.thirdPartyCode = bankAccountCode.val(),parseFloat(rowPosition), 3);
				                    	table.fnUpdate(dataSellingAgent.bankCode.thirdPartyName = bankAccountCodeName.val(),parseFloat(rowPosition), 3);
				                    	table.fnUpdate(dataSellingAgent.name = beneficiaryName.val(),parseFloat(rowPosition), 4);
				                    	table.fnUpdate(dataSellingAgent.branch = branch.val(),parseFloat(rowPosition), 4);
				                    	table.fnUpdate(dataSellingAgent.currencyCode.currencyCode = currencyCode.val(),parseFloat(rowPosition), 3);
				                    	table.fnUpdate(dataSellingAgent.currencyCode.description = currencyCodeDesc.val(),parseFloat(rowPosition), 3);
				                    	table.fnUpdate(dataSellingAgent.description = description.val(),parseFloat(rowPosition), 4);
			                    	
	    	                    	}else {
			                    		
			                    		table.fnUpdate(dataSellingAgent.accountNo = '', parseFloat(rowPosition), 2);
			                    		table.fnUpdate(dataSellingAgent.bankCode = '', parseFloat(rowPosition), 3);
				                    	table.fnUpdate(dataSellingAgent.name = '', parseFloat(rowPosition), 4);
				                    	table.fnUpdate(dataSellingAgent.branch = '', parseFloat(rowPosition), 4);
				                    	table.fnUpdate(dataSellingAgent.currencyCode =  '', parseFloat(rowPosition), 3);
				                    	table.fnUpdate(dataSellingAgent.description = '', parseFloat(rowPosition), 4);
			                    	}
			                    	
	    	                    	table.fnUpdate(dataSellingAgent.transferCharge.lookupId = transferCharge.val(), parseFloat(rowPosition), 5);
	    	                    	table.fnUpdate(dataSellingAgent.transferCharge.lookupCode = transferChargeCode.val(),parseFloat(rowPosition), 5);
	    	                    	
	    	                    	$("#dialog-message").dialog("close");
	    	                        resetDialogSellingAgentFields();
	    	                        $('#dialogSellingAgent').dialog('close');
	    	                        
	    	        				return true;
	    	        			}
	    	        			
	    	        			var noSellingAgent = function(){
	    	        				
	    	        				$("#dialog-message").dialog("close");
	    	        				 resetDialogSellingAgentFields();
	    		                     $('#dialogSellingAgent').dialog('close');
	    		                     
	    	        			}
	    	        			messageAlertYesNo("There are still payable transaction. Are you sure to save changes? ", "ui-icon ui-icon-notice", "Confirmation Message", yesSellingAgent, noSellingAgent);
	    	        			return false;
	    	        			
	    	        		}else {
	    	        			
	    	        			table.fnUpdate(dataSellingAgent.saCode.thirdPartyKey = sellingAgentKey.val(),parseFloat(rowPosition), 0);
		                    	table.fnUpdate(dataSellingAgent.saCode.thirdPartyCode = sellingAgentCode.val(),parseFloat(rowPosition), 0);
		                    	table.fnUpdate(dataSellingAgent.saCode.thirdPartyName = sellingAgentName.val(),parseFloat(rowPosition), 0);
//		                    	table.fnUpdate(dataSellingAgent.paymentType.lookupId = paymentType.val(),parseFloat(rowPosition), 1);
//    	                    	table.fnUpdate(dataSellingAgent.paymentType.lookupCode = paymentTypeCode.val(),parseFloat(rowPosition), 1);
		                    	table.fnUpdate(dataSellingAgent.targetAccountType.lookupId = targetAccountType.val(),parseFloat(rowPosition), 1);
    	                    	table.fnUpdate(dataSellingAgent.targetAccountType.lookupCode = targetAccountTypeCode.val(),parseFloat(rowPosition), 1);
    	                    	table.fnUpdate(dataSellingAgent.targetAccountType.lookupDescription = targetAccountTypeDesc.val(),parseFloat(rowPosition), 1);
		                    	
    	                    	if (dataSellingAgent.targetAccountType.lookupId  == 'TARGET_ACCOUNT-2') {
		                    		table.fnUpdate(dataSellingAgent.accountNo = accountNo.val(),parseFloat(rowPosition), 2);
		                    		dataSellingAgent.bankCode = new Object();
		                    		dataSellingAgent.currencyCode = new Object();
		                    		table.fnUpdate(dataSellingAgent.bankCode.thirdPartyKey = bankAccountCodeKey.val(),parseFloat(rowPosition), 3);
			                    	table.fnUpdate(dataSellingAgent.bankCode.thirdPartyCode = bankAccountCode.val(),parseFloat(rowPosition), 3);
			                    	table.fnUpdate(dataSellingAgent.bankCode.thirdPartyName = bankAccountCodeName.val(),parseFloat(rowPosition), 3);
			                    	table.fnUpdate(dataSellingAgent.name = beneficiaryName.val(),parseFloat(rowPosition), 4);
			                    	table.fnUpdate(dataSellingAgent.branch = branch.val(),parseFloat(rowPosition), 4);
			                    	table.fnUpdate(dataSellingAgent.currencyCode.currencyCode = currencyCode.val(),parseFloat(rowPosition), 3);
			                    	table.fnUpdate(dataSellingAgent.currencyCode.description = currencyCodeDesc.val(),parseFloat(rowPosition), 3);
			                    	table.fnUpdate(dataSellingAgent.description = description.val(),parseFloat(rowPosition), 4);
		                    	}else {
		                    		table.fnUpdate(dataSellingAgent.accountNo = '',parseFloat(rowPosition), 2);
		                    		table.fnUpdate(dataSellingAgent.bankCode = '',parseFloat(rowPosition), 3);
			                    	table.fnUpdate(dataSellingAgent.name =  '',parseFloat(rowPosition), 4);
			                    	table.fnUpdate(dataSellingAgent.branch = '',parseFloat(rowPosition), 4);
			                    	table.fnUpdate(dataSellingAgent.currencyCode =  '',parseFloat(rowPosition), 3);
			                    	table.fnUpdate(dataSellingAgent.description = '',parseFloat(rowPosition), 4);
		                    	}
		                    	
    	                    	table.fnUpdate(dataSellingAgent.transferCharge.lookupId = transferCharge.val(), parseFloat(rowPosition), 5);
		                    	table.fnUpdate(dataSellingAgent.transferCharge.lookupCode = transferChargeCode.val(),parseFloat(rowPosition), 5);
		                    	
		                        resetDialogSellingAgentFields();
		                        $('#dialogSellingAgent').dialog('close');
	    	        		}
	                    }
	                } else {
	                	
	                	if (transferCharge.val()==''){
		    				$('#transferChargeError').html('Required');
		    				return false;
		    			} else {
		    				$('#transferChargeError').html('');
		    			}
	                	
	                	var sellingAgentArr = [];
	                    var found = false;
	                    // CHECKED DUPLICATE ROW (duplicate row adl jika
						// sellingAgent ada yg sama) -> based on redmine #2833
	                    var rows = table.fnGetNodes().length;
	                    for (i = 0; i < rows; i++) {
	                    	
	                        var cells = table.fnGetData(i);
	                        
	                        // concate semua field2 mandatory
	                        var sellingAgentCodeCell = cells.saCode.thirdPartyCode;
//	                        var paymentTypeCell = cells.paymentType.lookupId;
	                        var targetAccountTypeCell = cells.targetAccountType.lookupId;
	                        var targetAccountTypeCell = cells.targetAccountType.lookupId;
	                        var accountNoCell = cells.accountNo;
	                        var bankAccountCodeCell = '';
	                        var currencyCell = '';
	                        var branchCell = '';
	                        var descriptionCell = '';
	                        var beneficiaryNameCell = '';
	                        if (cells.bankCode != null ) bankAccountCodeCell = cells.bankCode.thirdPartyCode;
	                        if (cells.currencyCode != null ) currencyCell = cells.currencyCode.currencyCode;
	                        if (cells.branch != null) branchCell = cells.branch;
	                        if (cells.description != null) descriptionCell = cells.description;
	                        if (cells.name != null) beneficiaryNameCell = cells.name;
	                        var transferChargeCell = cells.transferCharge.lookupId;
	                        
//	                        var concateAllMandatoryCell = sellingAgentCodeCell + targetAccountTypeCell + accountNoCell + bankAccountCodeCell
//	                        							+ currencyCell + beneficiaryNameCell + branchCell + descriptionCell ;
	                        var concateAllMandatoryCell = sellingAgentCodeCell;
	                        
	                        // concate semua field2 mandatory yang old
	                        var oldSellingAgentCode = $("#dialogSellingAgent #sellingAgentForm #oldSellingAgent").val();
//	                        var oldPaymentType = $("#dialogSellingAgent #sellingAgentForm #oldPaymentType").val();
	                        var oldTargetAccountType = $("#dialogSellingAgent #sellingAgentForm #oldTargetAccountType").val();
	                        var oldAccountNo = $("#dialogSellingAgent #sellingAgentForm #oldAccountNo").val();
	                        var oldBankAccount= $("#dialogSellingAgent #sellingAgentForm #oldBankAccount").val();
	                        var oldCurrency = $("#dialogSellingAgent #sellingAgentForm #oldCurrency").val();
	                        var oldBeneficiaryName = $("#dialogSellingAgent #sellingAgentForm #oldBeneficiaryName").val();
	                        var oldBranch = $("#dialogSellingAgent #sellingAgentForm #oldBranch").val();
	                        var oldDescription = $("#dialogSellingAgent #sellingAgentForm #oldDescription").val();
	                        var oldTransferCharge = $("#dialogSellingAgent #sellingAgentForm #oldTransferCharge").val();
	                        
//	                        var concatenateAllMandatoryOld = oldSellingAgentCode + oldTargetAccountType + oldAccountNo + oldBankAccount
//	                        								+ oldCurrency + oldCurrency + oldBeneficiaryName + oldBranch + oldDescription; 
	                        var concatenateAllMandatoryOld = oldSellingAgentCode;
	                        
	                        // concate semua field2 mandatory yang new
//	                        var concateAllMandatoryNew = sellingAgentCode.val() +  targetAccountType.val() + accountNo.val()
//								+ bankAccountCode.val() + currencyCode.val() + beneficiaryName.val() + branch.val() + description.val();
	                        
	                        var concateAllMandatoryNew = sellingAgentCode.val();
	            	        
	            	        if (concatenateAllMandatoryOld != concateAllMandatoryNew) {
	            	        	
	            	        	if (concateAllMandatoryCell == concateAllMandatoryNew) {
	            	        		$("#dialogSellingAgent #sellingAgentForm #sellingAgentCodeError").html("This Selling Agent already Exist!");
	            	        		found = true;
	            	        		break;
	            	        	}
	            	        }
	            	        
	            	        for(var i = 0 ; i < sellingAgentArr.length; i++) {
	            	        	if(sellingAgentArr[i] == sellingAgentCodeCell) {
	            	        		$("#dialogSellingAgent #sellingAgentForm #sellingAgentCodeError").html("This Selling Agent already Exist!");
	            	        		found = true;
	            	        		break;
	            	        	}
	            	        }
	            	        
	            	        sellingAgentArr.push(sellingAgentCodeCell);
	                        
	                    }
	                	
	                    if(!found) {
	                    	
	                        var dataSellingAgent = new Object();
	                        dataSellingAgent.product = new Object();
	                        dataSellingAgent.saCode = new Object();
	                        dataSellingAgent.paymentType = new Object();
	                        dataSellingAgent.targetAccountType = new Object();
	                        dataSellingAgent.bankCode = new Object();
	                        dataSellingAgent.currencyCode = new Object();
	                        dataSellingAgent.id = new Object();
	                        dataSellingAgent.transferCharge = new Object();
	                        
	                        dataSellingAgent.bankAccountKey = $("#dialogSellingAgent #sellingAgentForm #saBankAccountKey").val();
	                        dataSellingAgent.saCode.thirdPartyKey = $("#dialogSellingAgent #sellingAgentForm #sellingAgentKey").val();
	                        dataSellingAgent.saCode.thirdPartyCode = $("#dialogSellingAgent #sellingAgentForm #sellingAgentCode").val();
	                        dataSellingAgent.saCode.thirdPartyName = $("#dialogSellingAgent #sellingAgentForm #sellingAgentCodeDesc").val();
//	                        dataSellingAgent.paymentType.lookupId = $("#dialogSellingAgent #sellingAgentForm #paymentType").val();
//	                        dataSellingAgent.paymentType.lookupCode = $("#dialogSellingAgent #sellingAgentForm #paymentTypeCode").val();
	                        dataSellingAgent.targetAccountType.lookupId = $("#dialogSellingAgent #sellingAgentForm #targetAccountType").val();
	                        dataSellingAgent.targetAccountType.lookupCode = $("#dialogSellingAgent #sellingAgentForm #targetAccountTypeCode").val();
	                        dataSellingAgent.targetAccountType.lookupDescription = $("#dialogSellingAgent #sellingAgentForm #targetAccountTypeDescription").val();
	                        dataSellingAgent.accountNo = $("#dialogSellingAgent #sellingAgentForm #accountNo").val();
	                        dataSellingAgent.bankCode.thirdPartyKey = $("#dialogSellingAgent #sellingAgentForm #bankAccountCodeKey").val();
	                        dataSellingAgent.bankCode.thirdPartyCode = $("#dialogSellingAgent #sellingAgentForm #bankAccountCode").val();
	                        dataSellingAgent.bankCode.thirdPartyName = $("#dialogSellingAgent #sellingAgentForm #bankAccountCodeDesc").val();
	                        dataSellingAgent.currencyCode.currencyCode = $("#dialogSellingAgent #sellingAgentForm #currencyCode").val();
	                        dataSellingAgent.currencyCode.description = $("#dialogSellingAgent #sellingAgentForm #currencyCodeDesc").val();
	                        dataSellingAgent.name = $("#dialogSellingAgent #sellingAgentForm #beneficiaryName").val();
	                        dataSellingAgent.description = $("#dialogSellingAgent #sellingAgentForm #description").val();
	                        dataSellingAgent.branch = $("#dialogSellingAgent #sellingAgentForm #branch").val();

	                        dataSellingAgent.transferCharge.lookupId = $("#dialogSellingAgent #sellingAgentForm #transferCharge").val();
	                        dataSellingAgent.transferCharge.lookupCode = $("#dialogSellingAgent #sellingAgentForm #transferChargeCode").val();

	                    
	                        dataSellingAgent.id.productCode = $("#productCode").val();
	                        
	                      // cek is rgTrade payable or not.
//	    	        		var isPayable  = checkIsPayableRgTrade($("#productCode").val(), dataSellingAgent.saCode.thirdPartyKey);
	                        var isPayable = 'false'; //pengecekan payable atau bukan hanya jika di mode edit grid selling agent
	    	        		if(isPayable == 'true') {
	    	        			
	    	        			var yesSellingAgent = function() {
	    	        				$("#dialog-message").dialog("close");
	    	        				table.fnAddData(dataSellingAgent);
	    	                        resetDialogSellingAgentFields();
	    	                        $('#dialogSellingAgent').dialog('close');
	    	        				return true;
	    	        			}
	    	        			
	    	        			var noSellingAgent = function(){
	    	        				$("#dialog-message").dialog("close");
	    	        				 resetDialogSellingAgentFields();
	    		                     $('#dialogSellingAgent').dialog('close');
	    	        			}
	    	        			messageAlertYesNo("There are still payable transaction. Are you sure to save changes? ", "ui-icon ui-icon-notice", "Confirmation Message", yesSellingAgent, noSellingAgent);
	    	        			return false;
	    	        		}else {
	    	        			//console.log("ooooooooooooooooooooooooooo")
	    	        			//console.log(dataSellingAgent)
	    	        			table.fnAddData(dataSellingAgent);
		                        resetDialogSellingAgentFields();
		                        $('#dialogSellingAgent').dialog('close');
	    	        		}
	                        
	                    }
	                }
	                return false;
	            }
	        }
       }, 500);
    });
    
    function checkIsPayableRgTrade(productCode, sellingAgent) {
    	$.ajaxSetup({async : false});
    	$.get("@{RegistryProduct.isPayableTradeByProductCode()}", {'productCode': productCode, 'sellingAgent' : sellingAgent}, function(data){
    		 $('#isPayable').val(data);
    	});
    	   
		return $('#isPayable').val();
    }
    
    function openSellingAgentDialogForEdit(data, rowPosition) {
    	
    	resetDialogSellingAgentFields();
    	
    	$("#dialogSellingAgent #rowPosition").val(rowPosition);
    	$("#dialogSellingAgent #saBankAccountKey").val(data.bankAccountKey);
    	$("#dialogSellingAgent #sellingAgentCode").val(data.saCode.thirdPartyCode);
    	$("#dialogSellingAgent #sellingAgentCodeDesc").val(data.saCode.thirdPartyName);
//    	$("#dialogSellingAgent #paymentType").val(data.paymentType.lookupId);
//    	$("#dialogSellingAgent #paymentTypeCode").val(data.paymentType.lookupCode);
    	$("#dialogSellingAgent #targetAccountType").val(data.targetAccountType.lookupId);
    	$("#dialogSellingAgent #targetAccountTypeCode").val(data.targetAccountType.lookupCode);
    	$("#dialogSellingAgent #targetAccountTypeDescription").val(data.targetAccountType.lookupDescription);

    	$('#dialogSellingAgent #description').val(data.description);
    	if (data.targetAccountType.lookupId == 'TARGET_ACCOUNT-2') {
    		$("#dialogSellingAgent #accountNo").val(data.accountNo);
    		$("#dialogSellingAgent #bankAccountCodeKey").val(data.bankCode.thirdPartyKey);
    		$("#dialogSellingAgent #bankAccountCode").val(data.bankCode.thirdPartyCode);
        	$("#dialogSellingAgent #bankAccountCodeDesc").val(data.bankCode.thirdPartyName);
        	$("#dialogSellingAgent #currencyCode").val(data.currencyCode.currencyCode);
        	$("#dialogSellingAgent #currencyCodeDesc").val(data.currencyCode.description);
        	$("#dialogSellingAgent #beneficiaryName").val(data.name);
        	$("#dialogSellingAgent #branch").val(data.branch);
    	
    		$('#dialogSellingAgent #oldBankAccount').val(data.bankCode.thirdPartyCode);
        	$('#dialogSellingAgent #oldCurrency').val(data.currencyCode.currencyCode);
        	$('#dialogSellingAgent #oldAccountNo').val(data.accountNo);
        	$('#dialogSellingAgent #oldBeneficiaryName').val(data.name);
    	}
    	
    	
    	$('#dialogSellingAgent #oldSellingAgent').val(data.saCode.thirdPartyCode);
//    	$('#dialogSellingAgent #oldPaymentType').val(data.paymentType.lookupId);
    	$('#dialogSellingAgent #oldTargetAccountType').val(data.targetAccountType.lookupId);
    	
    	
    	
    	$("#dialogSellingAgent #transferCharge").val(data.transferCharge.lookupId);
    	$("#dialogSellingAgent #transferChargeCode").val(data.transferCharge.lookupCode);
    	
    	$('#dialogSellingAgent #oldTransferCharge').val(data.transferCharge.lookupId);
    	
    	$('#dialogSellingAgent input').removeClass('fieldError');
    	$('#dialogSellingAgent .error').html("");
    	
    	$("#dialogSellingAgent").dialog('open');
    	$('.ui-widget-overlay').css('height',$('body').height());
    	
    	if ($("#dialogSellingAgent #targetAccountType").val() == 'TARGET_ACCOUNT-1') {
    		targetAcct1();
    	}else if ($("#dialogSellingAgent #targetAccountType").val() == 'TARGET_ACCOUNT-2') {
    		targetAcct2();
    	}else{
    		targetAcctMan();    		
    	}
    };
    
    
    $('#sellingAgentTable tbody tr td').die('click');
	$('#sellingAgentTable tbody tr td').live('click', function(){
		var rowPos = $(this).parents('tr');
		var table = $('#sellingAgentTable').dataTable();
		var rowPosNumber = table.fnGetPosition(rowPos[0]);
		var pos = table.fnGetPosition(this);

		if (pos[1] != 8) {
			var data = table.fnGetData(this.parentNode);
			openSellingAgentDialogForEdit(data, rowPosNumber);
			
		};
	});

    // Button New Data for Selling Agent
    $('.buttons #newSellingAgentDialog').click(function()
	{
    	$('#btnSellingAgentAddError').html("");
    	selectedRow = null;
        $("#dialogSellingAgent").dialog('open');
        $('.ui-widget-overlay').css('height',$('body').height());
        resetDialogSellingAgentFields();
        $("#dialogSellingAgent input:text").val("");
		$("#dialogSellingAgent input:hidden").val("");
		$("#dialogSellingAgent #sellingAgentForm .errmsg").html("");
        return false;
        
    });
});

function sellingAgent(data) {
    var sellingAgents;
    
    var sa = ${saBnAccounts};
    
    if (sa != '') {
    	sellingAgents = ${saBnAccounts.raw()};
    }else {
    	sellingAgents = new Array();
    }

    tableSellingAgent = 
        $('#form #listSellingAgent #sellingAgentTable').dataTable({
            aaData: sellingAgents,
            aoColumns: [ 
                          {
                             fnRender: function(obj) {
                            	 var controls;
                            	 controls = obj.aData.saCode.thirdPartyName;
                            	 controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].bankAccountKey" value="' + obj.aData.bankAccountKey + '" />';
                            	 controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].saCode.thirdPartyKey" value="' + obj.aData.saCode.thirdPartyKey + '" />';
                            	 controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].saCode.thirdPartyCode" value="' + obj.aData.saCode.thirdPartyCode + '" />';
                                 controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].saCode.thirdPartyName" value="' + obj.aData.saCode.thirdPartyName + '" />';
                                 return controls;
                             }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  controls = obj.aData.targetAccountType.lookupDescription;
                                  controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].targetAccountType.lookupId" value="' + obj.aData.targetAccountType.lookupId + '" />';
                                  controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].targetAccountType.lookupCode" value="' + obj.aData.targetAccountType.lookupCode + '" />';
                                  controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].targetAccountType.lookupDescription" value="' + obj.aData.targetAccountType.lookupDescription + '" />';
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                            	  var controls = '';
                            	  if (obj.aData.targetAccountType.lookupCode == '2') {
                            		  controls = obj.aData.accountNo;
                                      controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].accountNo" value="' + obj.aData.accountNo + '" />';
                            	  }
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                            	  var controls = '';
	                           	  if (obj.aData.targetAccountType.lookupCode == '2') {
	                           		  controls = obj.aData.bankCode.thirdPartyName;
	                           		  controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].bankCode.thirdPartyKey" value="' + obj.aData.bankCode.thirdPartyKey + '" />';
	                                  controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].bankCode.thirdPartyCode" value="' + obj.aData.bankCode.thirdPartyCode + '" />';
	                                  controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].bankCode.thirdPartyName" value="' + obj.aData.bankCode.thirdPartyName + '" />';
	                                  controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].currencyCode.currencyCode" value="' + obj.aData.currencyCode.currencyCode + '" />';
	                                  controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].currencyCode.description" value="' + obj.aData.currencyCode.description + '" />';
	                           	  }
                                 
                                  return controls;
                              }
                          }
                          ,
                          {
                             fnRender: function(obj) {
                            	 var controls = '';
                            	 
                            	 if (obj.aData.targetAccountType.lookupCode == '2') {
                            		 controls = obj.aData.name;
                            		 controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].name" value="' + obj.aData.name + '" />';
                                     controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].branch" value="' + obj.aData.branch + '" />';
                                     controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].description" value="' + obj.aData.description + '" />';
                            	 }
                                 
                                 return controls;
                             }
                          },
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  controls = obj.aData.transferCharge.lookupCode;
                                  controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].transferCharge.lookupId" value="' + obj.aData.transferCharge.lookupId + '" />';
                                  controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].transferCharge.lookupCode" value="' + obj.aData.transferCharge.lookupCode + '" />';
                                  //controls += '<input type="hidden" name="rgSaBnAccounts[' + obj.iDataRow + '].transferCharge.lookupDescription" value="' + obj.aData.transferCharge.lookupDescription + '" />';
                                  return controls;
                              }
                          }
                          ,
                          {
                             fnRender: function(obj) {
                            	 var controls;
	                           	 controls = '<center><input id="deleteButtonSellingAgent" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
                                 return controls;
                             }
                          }
                        ],
                        aaSorting: [[0, 'asc']],
        				bAutoWidth: false,		
        	        	bDestroy: true,
        	        	bFilter: false,
        	        	bInfo: false,
        	        	bJQueryUI: true,
        	        	bPaginate: false,
        	        	bSearch: false,
        	        	bLengthChange: false,
        	        	isDisplayLength:10
    });

}
