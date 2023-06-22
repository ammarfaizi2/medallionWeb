$(function() {
	
    function attachTransactionDeliverer() {
		var settlementAgentKey = $("#settlementAgentKey").val(); 
		console.debug("settlementAgentKey => " + settlementAgentKey);
		$("#transactionDeliverer").lookup({
			list:'@{Pick.getListTransaction()}?filter=' + $("#securityId").val()
																			+ "|" + settlementAgentKey
																			+ "|" + $("#transactionRefDate").val()
																			+ "|" + $("#settlementRefDate").val()
																			+ "|" + $("#quantityStripped").val()
																			+ "|" + $("#priceStripped").val()
																			+ "|" + $("#ctpNo").val()
																			+ "|" + $("#accountNoKey").val()
																			+ "|" + $("#transactionTypeId").val(),
			get:{
				url:'@{Pick.getTransactionNew()}?filter=' + $("#securityId").val()
															+ "|" + settlementAgentKey
															+ "|" + $("#transactionRefDate").val()
															+ "|" + $("#settlementRefDate").val()
															+ "|" + $("#quantityStripped").val()
															+ "|" + $("#priceStripped").val()
															+ "|" + $("#ctpNo").val()
															+ "|" + $("#accountNoKey").val()
															+ "|" + $("#transactionTypeId").val(),
				success: function(data){
					$('#transactionDeliverer').removeClass('fieldError');
					$('#transactionDeliverer').val(data.transactionNo);
					$('#remarkDeliver').val(data.transactionNo);
				},
				error: function(data){
					$("#transactionDeliverer").addClass('fieldError');
					$("#transactionDeliverer").val('');
				}
			},
			filter: $("#transactionDeliverer"),
			help: $("#transactionDelivererHelp")
		});
	}
    
    function attachTransactionReceiver() {
		var settlementAgentKey = $("#settlementAgentKey").val(); 
		console.debug("settlementAgentKey => " + settlementAgentKey);
		$("#transactionReceiver").lookup({
			list:'@{Pick.getListTransaction()}?filter=' + $("#securityId").val()
																			+ "|" + settlementAgentKey
																			+ "|" + $("#transactionRefDate").val()
																			+ "|" + $("#settlementRefDate").val()
																			+ "|" + $("#quantityStripped").val()
																			+ "|" + $("#priceStripped").val()
																			+ "|" + $("#ctpNo").val()
																			+ "|" + $("#accountNoKey").val()
																			+ "|" + $("#transactionTypeId").val(),
			get:{
				url:'@{Pick.getTransactionNew()}?filter=' + $("#securityId").val()
															+ "|" + settlementAgentKey
															+ "|" + $("#transactionRefDate").val()
															+ "|" + $("#settlementRefDate").val()
															+ "|" + $("#quantityStripped").val()
															+ "|" + $("#priceStripped").val()
															+ "|" + $("#ctpNo").val()
															+ "|" + $("#accountNoKey").val()
															+ "|" + $("#transactionTypeId").val(),
				success: function(data){
					$("#transactionReceiver").removeClass("fieldError");
					$("#transactionReceiver").val(data.transactionNo);
					$('#remarkReceiver').val(data.transactionNo);
				},
				error: function(data){
					$("#transactionReceiver").addClass('fieldError');
					$("#transactionReceiver").val('');
				}
			},
			filter: $("#transactionReceiver"),
			help: $("#transactionReceiverHelp")
		});
	} 
    
    function checkTransacting() {
    	if (!'${confirming}') {
    		
    		if (!$('#chkIsInHouse').is(":checked")) {

    			if ($('#transactionCode').val() == 'B') {

    				if ($('#ctpReport').val() == '0'
    						&& $('#reportType').val() == 'CTP_REPORT_TYPE-ONE') {

    					$('#transactingPartiesCodeDeliverer')
    							.removeAttr('disabled');
    					$('#transactingPartiesNameDeliverer')
    							.removeAttr('disabled');

    				}

    			} else if ($('#transactionCode').val() == 'S') {

    				if ($('#ctpReport').val() == '0'
    						&& $('#reportType').val() == 'CTP_REPORT_TYPE-ONE') {

    					$('#transactingPartiesCodeReceiver').removeAttr('disabled');
    					$('#transactingPartiesNameReceiver').removeAttr('disabled');

    				}

    			}

    		} else {

    			if ($('#transactionCode').val() == 'B') {

    				$('#transactionDeliverer').removeAttr('disabled');
    				$('#transactionDelivererHelp').removeAttr('disabled');

    				//if (!$('#inHouseReference').isEmpty()) {
    				//	$('#transactionDeliverer').val($('#inHouseReference').val());
    				//	$('#h_transactionDeliverer').val(
    				//			$('#inHouseReference').val());
    				//}

    			} else if ($('#transactionCode').val() == 'S') {

    				$('#transactionReceiver').removeAttr('disabled');
    				$('#transactionReceiverHelp').removeAttr('disabled');

    				//if (!$('#inHouseReference').isEmpty()) {
    				//	$('#transactionReceiver')
    				//			.val($('#inHouseReference').val());
    				//	$('#h_transactionReceiver').val(
    				//			$('#inHouseReference').val());
    				//}
    			}
    		}
    	} else {
    		
    		inhouseChecked($('#chkIsInHouse').is(':checked'));
    		
    		$('#transactingPartiesCodeReceiver').attr('disabled', 'disabled');
			$('#transactingPartiesNameReceiver').attr('disabled', 'disabled');
    	}
    }
    
    function inhouseChecked(isChecked) {
    	if (isChecked) {
    		$('#dropdownCtpTransactionType').val('${CTP_REPORT_LATE_TRANSACTION_TYPE_T}');
			$('#ctpTransactionType').val($('#dropdownCtpTransactionType').val());
			
			if ($('#transactionCode').val() == 'B') {
				$('#transactionDeliverer').removeAttr('disabled');
				$('#transactionDelivererHelp').removeAttr('disabled');
				
				//$('#inHouseReference').val($('#transactionReceiver').val());
					
				$('#transactingPartiesCodeDeliverer').attr('disabled', 'disabled');
				$('#transactingPartiesNameDeliverer').attr('disabled', 'disabled');
				
			} else {
				$('#transactionReceiver').removeAttr('disabled');
				$('#transactionReceiverHelp').removeAttr('disabled');
				
				//$('#inHouseReference').val($('#transactionDeliverer').val());
				
				$('#transactingPartiesCodeReceiver').attr('disabled', 'disabled');
				$('#transactingPartiesNameReceiver').attr('disabled', 'disabled');
				
			}
    	} else {
    		
    		$("#transactionDelivererError").html('');
    		$("#transactionReceiverError").html('');
    		
    		if ($('#transactionCode').val() == 'B') {
				
    			$('#transactionDeliverer').val('');
				$('#transactionDeliverer').attr('disabled', 'disabled');
				$('#transactionDelivererHelp').attr('disabled', 'disabled');
				
				$('#transactionReceiver').val($('#transactionNumber').val());
				
				if ($('#ctpReport').val() == '0' && $('#reportType').val() == 'CTP_REPORT_TYPE-ONE') {
				
					$('#transactingPartiesCodeDeliverer').removeAttr('disabled');
					$('#transactingPartiesNameDeliverer').removeAttr('disabled');
					
				} else {
					
					$('#transactingPartiesCodeDeliverer').attr('disabled', 'disabled');
					$('#transactingPartiesNameDeliverer').attr('disabled', 'disabled');
					
				}
				
			} else if ($('#transactionCode').val() == 'S') {
				
				$('#transactionReceiver').val('');
				$('#transactionReceiver').attr('disabled', 'disabled');
				$('#transactionReceiverHelp').attr('disabled', 'disabled');
				
				$('#transactionDeliverer').val($('#transactionNumber').val());
				
				if ($('#ctpReport').val() == '0' && $('#reportType').val() == 'CTP_REPORT_TYPE-ONE') {
					
					$('#transactingPartiesCodeReceiver').removeAttr('disabled');
					$('#transactingPartiesNameReceiver').removeAttr('disabled');
					
				} else {
					
					$('#transactingPartiesCodeReceiver').attr('disabled', 'disabled');
					$('#transactingPartiesNameReceiver').attr('disabled', 'disabled');
					
				}
			}
			
			$('#dropdownCtpTransactionType').val($('#ctpTransactionTypeTemp').val());
			$('#ctpTransactionType').val($('#ctpTransactionTypeTemp').val());
			
			//$('#inHouseReference').val('');
    	}
    }
    
    function disableAll() {
    	$('#ctp').attr('disabled', 'disabled');
    	$('#ctpNo').attr('disabled', 'disabled');
    	$('#dropdownReportType').attr('disabled', 'disabled');
    	$('#transactionCode').attr('disabled', 'disabled');
    	$('#securityId').attr('disabled', 'disabled');
    	$('#counterPartyCode').attr('disabled', 'disabled');
    	$('#price').attr('disabled', 'disabled');
    	$('#yield').attr('disabled', 'disabled');
    	$('#netAmount').attr('disabled', 'disabled');
    	$('#tradeDate').attr('disabled', 'disabled');
    	$('#tradeTime').attr('disabled', 'disabled');
    	$('#settlementDate').attr('disabled', 'disabled');
    	$('#counterPartyDeliverer').attr('disabled', 'disabled');
    	$('#transactingPartiesCodeDeliverer').attr('disabled', 'disabled');
		$('#transactingPartiesNameDeliverer').attr('disabled', 'disabled');
		$('#transactingPartiesCodeReceiver').attr('disabled', 'disabled');
		$('#transactingPartiesNameReceiver').attr('disabled', 'disabled');
    	$('#transactionReceiver').attr('disabled', 'disabled');
    	$('#transactionReceiverHelp').attr('disabled', 'disabled');
    	$('#transactionDeliverer').attr('disabled', 'disabled');
    	$('#transactionDelivererHelp').attr('disabled', 'disabled');
    }
    
    function enableAll() {
    	$('#ctp').enabled();
		$('#ctpNo').enabled();
		$('#dropdownReportType').enabled();
		$('#transactionCode').enabled();
		$('#securityId').enabled();
		$('#counterPartyCode').enabled();
		$('#price').enabled();
		$('#yield').enabled();
		$('#netAmount').enabled();
		$('#tradeDate').enabled();
		$('#tradeTime').enabled();
		$('#settlementDate').enabled();
		$('#counterPartyDeliverer').enabled();
		$('#counterPartyReceiver').enabled();
		$('#transactingPartiesCodeDeliverer').enabled();
		$('#transactingPartiesNameDeliverer').enabled();
		$('#transactingPartiesCodeReceiver').enabled();
		$('#transactingPartiesNameReceiver').enabled();
		$('#transactionDeliverer').enabled();
		$('#transactionReceiver').enabled();
		$('#remarkDeliver').enabled();
		$('#remarkReceiver').enabled();
		$("#referenceDeliver").enabled();
		$("#referenceReceiver").enabled();
		$("#dropdownLateReasonType").enabled();
		$("#lateReasonType").enabled();
		$("#lateReason").enabled();
		$("#dropdownCtpTransactionType").enabled();
		$("#chkIsInHouse").enabled();
    }
	
    disableAll();
    
	attachTransactionDeliverer();
	attachTransactionReceiver();
	
	if ($('#ctpReport').val() == '${INT_INPUT}') {
		$('#ctp').val('${INPUT}');
	} else {
		$('#ctp').val('${CONFIRM}');
	}
	
	if ($('#inHouseReference').val() == null || $('#inHouseReference').isEmpty()) {
		$('#chkIsInHouse').removeAttr('checked');
	} else {
		$('#chkIsInHouse').attr('checked', 'checked');
	}
	
	$('#transactingPartiesCodeDeliverer').change(function(){
		if($('#transactingPartiesCodeDeliverer').val() == null || $('#transactingPartiesCodeDeliverer').val() == '') {
			console.log($('#transactingPartiesCodeDeliverer').val());
			$('#transactingPartiesCodeDelivererStripped').val('');
		}
	});
	$('#transactingPartiesNameDeliverer').change(function(){
		if($('#transactingPartiesNameDeliverer').val() == null || $('#transactingPartiesNameDeliverer').val() == '') {
			$('#transactingPartiesNameDelivererStripped').val('');
		}
	});
	$('#transactingPartiesCodeReceiver').change(function(){
		if($('#transactingPartiesCodeReceiver').val() == null || $('#transactingPartiesCodeReceiver').val() == '') {
			$('#transactingPartiesCodeReceiverStripped').val('');
		}
	});
	$('#transactingPartiesNameReceiver').change(function(){
		if($('#transactingPartiesNameReceiver').val() == null || $('#transactingPartiesNameReceiver').val() == '') {
			$('#transactingPartiesNameReceiverStripped').val('');
		}
	});
	
	$('#chkIsInHouse').change(function(){ 
		
		inhouseChecked($(this).is(':checked'));
		
	});
	
	$('#dropdownReportType').val($('#reportType').val());
	$('#ctpTransactionTypeTemp').val($('#ctpTransactionType').val());
	$('#dropdownCtpTransactionType').val($('#ctpTransactionType').val());
	
	$('#dropdownReportType').change(function() {
		$('#reportType').val($('#dropdownReportType').val());
	});
	
	$('#dropdownCtpTransactionType').change(function() {
		$('#ctpTransactionType').val($('#dropdownCtpTransactionType').val());
	});
	
	$('#price').attr('disabled', 'disabled');
	
	if ($('#remarkDeliver').isEmpty()) {
		$('#remarkDeliver').val($('#transactionDeliverer').val());
	}
	
	$('#remarkDeliver').attr('disabled', 'disabled');
	
	if ($('#remarkReceiver').isEmpty()) {
		$('#remarkReceiver').val($('#transactionReceiver').val());
	}
	
	$('#remarkReceiver').removeAttr('disabled');
	$('#referenceReceiver').removeAttr('disabled');
	if ($('#transactionCode').val() == 'S' && $('#ctpReport').val() == '0' && $('#reportType').val() == 'CTP_REPORT_TYPE-TWO') {
		$('#remarkReceiver').attr('disabled', 'disabled');
		$('#referenceReceiver').attr('disabled', 'disabled');
	} 
	
	$('#counterPartyDeliverer').attr('disabled', 'disabled');
	$('#counterPartyReceiver').attr('disabled', 'disabled');
	
	$('#dropdownLateReasonType').val($('#lateReasonType').val());
	$('#dropdownLateReasonType').change(function(){
		$('#lateReasonType').val($('#dropdownLateReasonType').val());
	});
	
	if (('${mode}' != 'edit' && '${mode}' != 'entry') || '${confirming}' ) {
		//$('#transactionReceiver').attr('disabled', 'disabled');
		//$('#transactionReceiverHelp').attr('disabled', 'disabled');
		
		//$('#transactionDeliverer').attr('disabled', 'disabled');
		//$('#transactionDelivererHelp').attr('disabled', 'disabled');
		
		$('#remarkReceiver').attr('disabled', 'disabled');
		$('#referenceReceiver').attr('disabled', 'disabled');
		
		$('#remarkDeliverer').attr('disabled', 'disabled');
		$('#referenceDeliverer').attr('disabled', 'disabled');
		
		$('#dropdownCtpTransactionType').attr('disabled', 'disabled');
		
		$('#chkIsInHouse').attr('disabled', 'disabled');
		
		$('#lateReason').attr('disabled', 'disabled');
	} else {
		$('#lateReason').removeAttr('disabled');
		$('#dropdownCtpTransactionType').removeAttr('disabled');
	}
	
	// DEFAULT PRICE UNIT VALUE ===================================================================================//
	if ($('#priceUnit').val() == '0.01'){
		$('#percentage').show();
	}else{
		$('#percentage').hide();
	}
	// ============================================================================================================//
	
	// BUTTON FUNCTIONS ===========================================================================================//
$(function() {
		
		var backToList = function() {
			loading.dialog('open');
			location = "@{list()}";
		}
		
		function confirmTrans(){
			//$(".prematch").remove(); 
			var action = "@{confirm()}";
			loading.dialog('open');
			$.post(action, $('#transactionForm').serialize(), function(data, status, xhr) {
	    		checkRedirect(data);
				loading.dialog('close');
				if (data.status ==  'success') {
					loading.dialog('close');
					messageAlertOk(data.messages, "ui-icon ui-icon-circle-check", "Notification", backToList);
				} else {
					loading.dialog('close');
					messageAlertOk(data.messages, "ui-icon1 ui-icon-circle-close", "Error Message", backToList);
				}
			});
		}
		
		$('#confirm').click(function(){
			
			var action = "@{confirm()}?mode=${mode}";
			
			$('#transactionForm').attr('action', action);
			$('#transactionForm').submit();
		});
		
		$('#back').click(function() {
			location.href = "@{back()}?id=${ctpTransaction?.seqCtpId}&mode=${mode}";
		});
		
		$('#clear').click(function() {
			
			if($('#seqCtpId').val() == null || $('#seqCtpId').val() == undefined) {
				location.href='@{entry()}?transactionKey='+$('#ctpTransactionKey').val();
			} else {
				location.href='@{edit()}?id='+$('#seqCtpId').val()+'&transactionKey='+$('#ctpTransactionKey').val();
			}
			
		});
		
		$('#close').click(function() {
			location.href = "@{list()}";
		})
		
		$('#saveCtp').click(function(){
			
			var isValid = true;
			
			enableAll();
			
			if ($('#ctp').val()===''){
				$("#ctpError").html('Required');
				isValid = false;
			}
			if ($('#reportType').val()===''){
				$("#reportTypeError").html('Required');
				isValid = false;
			}
			if ($('#transactionCode').val()===''){
				$("#transactionCodeError").html('Required');
				isValid = false;
			}
			if ($('#securityId').val()===''){
				$("#securityIdError").html('Required');
				isValid = false;
			}
			if ($('#ctpTransactionType').val()===''){
				$("#ctpTransactionTypeError").html('Required');
				isValid = false;
			}
			if ($('#counterPartyCode').val()===''){
				$("#counterPartyCodeError").html('Required');
				isValid = false;
			}
			if ($('#price').val()===''){
				$("#priceError").html('Required');
				isValid = false;
			}
			if ($('#netAmount').val()===''){
				$("#netAmountError").html('Required');
				isValid = false;
			}
			if ($('#tradeDate').val()===''){
				$("#tradeDateError").html('Required');
					isValid = false;
			}
			if ($('#tradeTime').val()===''){
				$("#tradeTimeError").html('Required');
				isValid = false;
			}
			if ($('#settlementDate').val()===''){
				$("#settlementDateTimeError").html('Required');
				isValid = false;
			}
			
			if ($('#chkIsInHouse').is(":checked")){ 
				if($('#transactionReceiver').val().trim() === '' || $('#transactionDeliverer').val().trim() === '') { 
					if($('#transactionReceiver').val().trim() === '') {
						$("#transactionReceiverError").html('Required');
					}
					
					if($('#transactionDeliverer').val().trim() === '') {
						$('#transactionDelivererError').html('Required');
					}
					
					isValid = false;
				}
					
			}
			
			if(!isValid){
				checkTransacting();
				
				inhouseChecked($('#chkIsInHouse').is(':checked'));
				
				return false;
			} else {
				enableAll();
			}
			
			var action = "@{save()}";
			loading.dialog('open');
			
			$('#transactionForm').attr('action', action);
			$('#transactionForm').submit();
		});

	});	

	checkTransacting();
	
	$('#chkIsInHouse').removeAttr('checked');
	if (!$("#transactionDeliverer").isEmpty() && !$("#transactionReceiver").isEmpty()) {
		$('#chkIsInHouse').attr('checked', 'checked');
	} 
	
	$('#dropdownReportType').children('option:first').remove();
	$('#dropdownCtpTransactionType').children('option:first').remove();

	// ================================================================================================================ //
}); //end ready function