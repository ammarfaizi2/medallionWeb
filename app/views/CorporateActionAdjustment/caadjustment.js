$(function(){
	$('#cancel').button();
	$('#save').button();
	$('#confirm').button();
	$('#back').button();
	var closeDialogMessage = function() {
		$("#dialog-message").dialog("close");
	};
	
	var backToList = function() {
		loading.dialog('open');
		location.href = "@{list()}";
	};
	
	$('#confirm').click(function(){
		var action = "@{confirm()}?mode=${mode}";
		
		$('#corporateAnnouncementAdjustmentForm').attr('action', action);
		$('#corporateAnnouncementAdjustmentForm').submit();
	});
	
	$('#cancel').click(function() {
		location.href="@{list()}";		
	});
	
	$('#back').click(function(){
		location.href='@{back()}?id='+$('#adjustmentKey').val()+'&mode=${mode}';
	});
	
	function validate() {
		$("#settlementAmountAdjustError").html('');
		if ($('#cashTransactionEmpty').val() == 'false') {
			if ($('#settlementAmountAdjustStripped').val() < 0) {
				$("#settlementAmountAdjustError").html('minus not allowed');
			}
		} else {
			$('#settlementAmountAdjust').autoNumericSet(0, {vMin:'-999999999999.99',vMax:'9999999999999.99', mDec:2});
			$('#settlementAmountAdjustStripped').val(0);
		}
	}
	
	$('#save').click(function() {
		
		$("#settlementAmountAdjustError").html('');
		if ($('#cashTransactionEmpty').val() == 'false') {
			if ($('#settlementAmountAdjustStripped').val() < 0) {
				$("#settlementAmountAdjustError").html('minus not allowed');
				return false;
			}
		} else {
			$('#settlementAmountAdjust').autoNumericSet(0, {vMin:'-999999999999.99',vMax:'9999999999999.99', mDec:2});
			$('#settlementAmountAdjustStripped').val(0);
		}
		var action = "@{save()}?mode=${mode}";
		loading.dialog('open');
		
		$('#corporateAnnouncementAdjustmentForm').attr('action', action);
		$('#corporateAnnouncementAdjustmentForm').submit();
	});
	
	if ("${editTransferMethod}" == 'YES') {
		$("#transferMethod").removeAttr("disabled");
	}else{
		$("#transferMethod").attr("disabled", "disabled");
	}
	
	// FIELD SETTLEMENT ACCOUNT ===================================================================================//
	$('#settlementAccount').lookup({
		list : '@{Pick.bankAccountsByAcctNo()}?domain=CUSTOMER&by=customerCaOrNull',
		get:{
			url: '@{Pick.bankAccountByAccountNoAndCustomerKey()}?domain=CUSTOMER',
			success: function(data){
				if (data) {
						$('#settlementAccount').removeClass('fieldError');
						var codeSplit = $('#settlementAccount').val().split("");
						$('#settlementAccount').val(codeSplit[0]);
						$('#settlementAccountKey').val(data.code);
						$('#settlementAccountName').val(data.description);
						$('#h_settlementAccountName').val(data.description);
						$('#settlementBankAccount').val(data.bankCode.thirdPartyCode);
						$('#h_settlementBankAccount').val(data.bankCode.thirdPartyCode);
						$('#settlementBankAccountKey').val(data.bankCode.thirdPartyKey);
						$('#settlementBankAccountName').val(data.bankCode.thirdPartyName);
						$('#h_settlementBankAccountName').val(data.bankCode.thirdPartyName);
						$('#settlementAccountBeneficiary').val(data.description);
						$('#h_settlementAccountBeneficiary').val(data.description);
						$('#settlementAccountCurrency').val(data.currency.currencyCode);
						$('#h_settlementAccountCurrency').val(data.currency.currencyCode);
					
				}	
			},
			
		},
		key : $('#settlementAccountKey'),
		description : $('#settlementAccountName'),
		filter : [$('#accountNoKey'), $('#customerKey')],
		help : $('#settlementAccountHelp')
	});
	
	$('#settlementAccount').change(function() {
		if ($('#settlementAccount').val() == "") {
			$('#settlementAccount').val('');
			$('#settlementAccountKey').val('');
			$('#settlementAccountName').val('');
			$('#h_settlementAccountName').val('');
			$('#settlementBankAccount').val('');
			$('#h_settlementBankAccount').val('');
			$('#settlementBankAccountKey').val('');
			$('#settlementBankAccountName').val('');
			$('#h_settlementBankAccountName').val('');
			$('#settlementAccountBeneficiary').val('');
			$('#h_settlementAccountBeneficiary').val('');
			$('#settlementAccountCurrency').val('');
			$('#h_settlementAccountCurrency').val('');
		}
	});
	// ============================================================================================================ //
		
	$('#taxAdjust').live('blur', function() {
		var taxAmount = $('#taxAdjustStripped').val();
		$("#settlementAmountAdjustError").html('');
		var taxAmount = new Number($('#taxAdjust').autoNumericGet());
		
		if(!$('#taxAdjust').isEmpty() && taxAmount > 0) {
			var setAmount = new Number($('#entitledAmountStripped').autoNumericGet());
			var chargeAmount = new Number($('#totalFee').autoNumericGet());
			var setAmountAdj = setAmount-taxAmount-chargeAmount;
			
			console.log('setAmount = '+setAmount);
			console.log('taxAmount = '+taxAmount);
			console.log('chargeAmount = '+chargeAmount);
			console.log('setAmountAdj = '+setAmountAdj);
			
			//if (setAmountAdj >= 0) {
				$('#settlementAmountAdjust').autoNumericSet(setAmountAdj, {vMin:'-999999999999.99',vMax:'9999999999999.99', mDec:2});
				$('#settlementAmountAdjustStripped').val(setAmountAdj);
			//} else {
			if ($('#cashTransactionEmpty').val() == 'false') {
				if (setAmountAdj < 0) {
					//alert('Settlement Amount Adjustment got minus value!');
					//$('#taxAdjust').autoNumericSet($('#taxAdjustTmpStripped').val(), {vMin:'-999999999999.',vMax:'9999999999999.', mDec:15});
					//$('#taxAdjustStripped').val($('#taxAdjustTmpStripped').val());
					$("#settlementAmountAdjustError").html('minus not allowed');
				}
			} else {
				$('#settlementAmountAdjust').autoNumericSet(0, {vMin:'-999999999999.99',vMax:'9999999999999.99', mDec:2});
				$('#settlementAmountAdjustStripped').val(0);
			}
		} else {
			$('#settlementAmountAdjust').val('');
			$('#settlementAmountAdjustStripped').val('');
		}
	});
	
	$('#settlementAccount').blur();
	
	validate();
	
});


