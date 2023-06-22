var tab2Visited = false;
var chargeGrid;
var reloadCharge = true;

var closeDialogMessage = function() {
	$("#dialog-message").dialog('close');
};

$(function() {
	var defaultBankCode = '${defaultbankcode}';
	attachInhouseReference();
	$('input.percentTax').autoNumeric({vMax: '99999999.0000', mDec: 4});
	$('input.percentTax').live('blur', function() {
		var el = $(this);
		var id = this.id;
		var stripped = "#" + id + "Stripped";
		if (el.val() == '') {
			el.siblings(stripped).val('');
			return;
		}
		el.siblings(stripped).val(el.autoNumericGet());
	});

	if ('${error}' == 'true'){
		alert("error");
		getCharges();
	}
	$('#addCharge').button({disabled: true});
	$('#tabs').tabs();
	$('.buttons input:button').button();
	$('.calendar').datepicker();

	$("#settlementAgentCodeLbl span.req").hide();

	if ('${mode}'=='view'){
		if ($('#securityClass').val()=='${secClassFI}'){
			$('#effMatDate').hide();
			$('#lastNextDate').show();
		}
	}
	
	if('${param}'=='${menuPrematch}'){
		$("#settlementAccount").removeAttr('disabled');
		$("#settlementAccountHelp").removeAttr('disabled');
	}
	
	// CONDITION FOR SOME FIELD IF MODE CONFIRMING ===================================================================//
	if ('${confirming}'=='true'){
		if ($('#accruedInterestStripped').val() == -1){
			$('#accruedInterest').val(0);
		}

		if ($('#securityClass').val()=='${secClassFI}'){
			$('#effMatDate').hide();
			$('#lastNextDate').show();
		}
	}

	if(('${fromApproval}')||('${fromInquiry}'=='fromInquiry')|| '${confirming}'=='true'){
		yieldPrematch();
	}

	if(('${fromApproval}')||('${fromInquiry}'=='fromInquiry')){
		$("#descriptionLbl span.req").hide();
		if($("#settlementReasonCode").isEmpty()) $("#reasonLbl span.req").hide();
		if($('#custodyTransactionCode').val() != "RVP" && $('#custodyTransactionCode').val() != "DVP"){
			$("#purposeLbl span.req").show();
		} else {			
			$("#purposeLbl span.req").hide();
			$("#referenceLbl span.req").hide();
			$("#reasonLbl span.req").hide();
			$("#descriptionLbl span.req").hide();

			$("#settlementPurpose").attr('disabled', true);
			$("#settlementReference").attr('disabled', true);
			$("#settlementReason").attr('disabled', true);
			$("#descriptionForPrematch").attr('disabled', true);
		}
		if($("#settlementReference").isEmpty()){
			if($("#settlementPurpose").val() == '${EXCH}'){
				$("#referenceLbl span.req").show();
			} else {
				$("#referenceLbl span.req").hide();
			}
		}
	}

	if('${confirming}'){
		$("#settlementAgentCode").attr('disabled', true);
		$("#settlementAgentHelp").attr('disabled', true);
	}

	// ============================================================================================================//
	
	// =========== GIRI'S CHANGE ========
	
	if ($('#hasInterest').val()=='true'){
		$('p[id=pTaxOnInterest] label span').html(" *");
		//$('p[id=pTaxOnInterestCode] label span').html(" *");
		$('p[id=pAccruedInterest] label span').html(" *");
		$('p[id=pInterestRate] label span').html(" *");
	}else{
		$('p[id=pTaxOnInterest] label span').html(" ");
		$('p[id=pTaxOnInterestCode] label span').html(" ");
		$('p[id=pAccruedInterest] label span').html(" ");
		$('p[id=pInterestRate] label span').html(" ");
	}
	if (($('#securityClass').val()=='${secClassMM}')&&($('#transactionType').val()=='${transTypeB}')){
		$('p[id=pMaturityDate] label span').html(" *");
	}else{
		$('p[id=pMaturityDate] label span').html(" ");
	}
	if ($('#transactionType').val()=='${transTypeS}'){
		$('p[id=pHoldingRef] label span').html(" *");
	}else{
		$('p[id=pHoldingRef] label span').html(" ");
	}
	
	// =========== END CHANGE ===========
	
	// DEFAULT PRICE UNIT VALUE ===================================================================================//
	if ($('#priceUnit').val() == '0.01'){
		$('#percentage').show();
	}else{
		$('#percentage').hide();
	}
	// ============================================================================================================//
	
	// CONDITION FOR SOME FIELD IF MODE ENTRY AND SETTLEMENT FALSE=================================================//
	if ((('${confirming}'!='true')&&('${mode}'=='entry')&&('${settlement}'!='true')) ||
		(('${confirming}'!='true')&&('${mode}'=='edit')&&('${settlement}'!='true'))) {
		
		$('p[id=pDiscountTaxCode] label span').html(" ");
		if ($('#discounted').val()=='true'){
			$('#discountTaxCode').removeAttr('disabled');
			$('#discountTaxHelp').removeAttr('disabled');
			$('#pDiscountTaxCode label').html("Tax Code <span class=\"req\">*</span>");
//			$('p[id=pDiscountTaxCode] label span').html(" *");
		};
		
		$('p[id=pDiscountTax] label span').html(" ");
		
		if (($('#isPrice').val()=='true')&&($('#discounted').val()=='true')){
			$('#discountTax').removeAttr('disabled');
			$('p[id=pDiscountTax] label span').html(" *");
		}
		
		$('p[id=pNetAmount] label span').html(" ");
		if (($('#isPrice').val()=='false')&&($('#discounted').val()=='true')){
			$('#price').attr('disabled', 'disabled');
			$('p[id=pPrice] label span').html("");
			
			$('#discountTax').removeAttr('disabled');
			$('p[id=pDiscountTax] label span').html(" *");
			
			$('#netAmount').removeAttr('disabled');
			$('p[id=pNetAmount] label span').html(" *");
		}
		
		if (($('#isPrice').val()=='false')&&($('#discounted').val()=='false')){
			$('#price').attr('disabled', 'disabled');
			$('p[id=pPrice] label span').html("");
		}
		
		if ($('#isPrice').val()=='false'){
			$('#price').attr('disabled', 'disabled');
			$('p[id=pPrice] label span').html("");
		}
		
		$('p[id=pInterestRate] label span').html(" ");
		$('p[id=pAccruedInterest] label span').html(" ");
		$('p[id=pTaxOnInterestCode] label span').html(" ");
		$('p[id=pTaxOnInterest] label span').html(" ");
		
		if ($('#hasInterest').val()=='true'){
			$('#interestRate').removeAttr('disabled');
			$('p[id=pInterestRate] label span').html(" *");
			
			$('#accruedInterest').removeAttr('disabled');
			$('#cekBoxWaiveAccInt').removeAttr('disabled');
			$('p[id=pAccruedInterest] label span').html(" *");
			
			$('#taxOnInterestCode').removeAttr('disabled');
			$('#taxOnInterestHelp').removeAttr('disabled');
			//$('p[id=pTaxOnInterestCode] label span').html(" *");
			
			$('#taxOnInterest').removeAttr('disabled');
			$('p[id=pTaxOnInterest] label span').html(" *");
		}
		
		if ($('#transactionType').val()=='${transTypeS}'){
			$('#holdingHelp').removeAttr('disabled');
			$('#holdingRefs').removeAttr('disabled');
		} else {
			$('p[id=pHoldingRef] label span').html("");
		}
		
		if ($('#securityClass').val()=='${secClassFI}'){
			$('#effMatDate').hide();
			$('#lastNextDate').show();
		}
		$('p[id=pMaturityDate] label span').html(" ");
		if (($('#securityClass').val()=='${secClassMM}')&&($('#transactionType').val()=='${transTypeB}')){
			$('#maturityDate').removeAttr('disabled');
			$('p[id=pMaturityDate] label span').html(" *");
		}
	}
	// ============================================================================================================//
	
	// CONDITION FOR SOME FIELD IF MODE ENTRY AND SETTLEMENT TRUE===================================================//
	if (('${confirming}'!='true')&&('${mode}'=='entry')&&('${settlement}'=='true')){
		if ($('#securityClass').val()=='${secClassFI}'){
			$('#effMatDate').hide();
			$('#lastNextDate').show();
		}
	}
	// ============================================================================================================//
	
	// FIELD TRANSACTION NO ======================================================================================//
	if ($('#transactionNo').val() != "") {
		if ('${transactionDate}') {
			//console.debug("transactionDate transaction entry = " + '${transactionDate}');
			$('#transactionDate').val('${transactionDate}');
		}
	}
	// ============================================================================================================//
	
///	if($('#cBestMessage').val() != ''){
//		$('#cBestMessageSection').css("display","");
//	} else {
//		$('#cBestMessageSection').css("display","none");
//	}
	
	//console.debug("settlementNumber = " + $("#settlementNumber").val() + " cashTransCodeSett = " + $('#cashTransCodeSett').val());
	
	// DEFAULT FIELD SETTLEMENT NUMBER ========================================================================//
	/*if ($("#settlementNumber").val() == "") {
		if ($('#settTransKey').val()!=''){
			if ($('#cashTransCode').val()!=''){
				$('#settlementAmount').val($('#netProceed').val());
			} else {
				$('#settlementAmount').val(0);
			}
		} else {
			$('#settlementAmount').val(0);
		}
	} else {
		if ($('#cashTransCodeSett').val()!=''){
			$('#settlementAmount').val($('#netProceed').val());
		} else {
			$('#settlementAmount').val(0);
		}
	}*/
	if ($("#settlementNumber").val() == "") {
		if ($('#settTransKey').val()!=''){
			if ($('#cashTransCode').val()==''){
				$('#settlementAmount').val(0);
			}
		} else {
			$('#settlementAmount').val(0);
		}
	} else {
		//reverse condition from the true condition
		//if ($('#cashTransCodeSett').val()!=''){ kenapa terbalik dengan calculate?
		if ($('#cashTransCodeSett').val()==''){
			$('#settlementAmount').val(0);
		}
	}
	// ============================================================================================================//
	
	// FIELD TRANSACTION DATE =====================================================================================//
	$('#transactionDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends,
		onSelect : function() {
			$('#settlementDate').val(''); 
			getSecurity();
			$('#accountNo').focus();
		}
	});
	
	$('#transactionDate').change(function() {
		if ($(this).val()==''){
			$(this).val('');
		} else{
			$('#settlementDate').val('');
			getSecurity();
			getDefDiscTaxCode();
			calculate(true);
		}
	});
	// ============================================================================================================//

	// FIELD ACCOUNT NO // ========================================================================================//
	$('#accountNo').change(function() {
		if ($('#accountNo').val() == "") {
			$('#accountNo').val("");
			$('#accountNoKey').val("");
			$('#accountNoDesc').val("");
			$('#h_accountNoDesc').val("");
			
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
			$('#customerKey').val('');
		}
		$('#settlementAccount').val("");
		$('#settlementAccountKey').val("");
		$('#settlementAccountName').val("");
		$('#h_settlementAccountName').val("");
		$('#settlementBankAccount').val("");
		$('#h_settlementBankAccount').val("");
		$('#settlementBankAccountKey').val("");
		$('#settlementBankAccountName').val("");
		$('#h_settlementBankAccountName').val("");
		$('#settlementAccountBeneficiary').val("");
		$('#h_settlementAccountBeneficiary').val("");
		$('#settlementAccountCurrency').val("");
		$('#h_settlementAccountCurrency').val("");
	});
	
	$('#accountNo').dynapopup('PICK_CS_ACCOUNT','', 'transactionCode',
			function(data) {
				var accountKey = $('#accountNoKey').val();
				$('#accountNo').removeClass('fieldError');
				$('#accountNoDesc').val(data.description);
				$('#h_accountNoDesc').val(data.description);
				$('#accountNoKey').val(data.code);
				$('#settlementAccount').removeClass('fieldError');
				$('#settlementAccount').val(data.bankAccountNo);
				$('#settlementAccountKey').val(data.bankAccountKey);
				$('#settlementAccountName').val(data.bankAccountDesc);
				$('#h_settlementAccountName').val(data.bankAccountDesc);
				$('#settlementBankAccount').removeClass('fieldError');
				$('#settlementBankAccount').val(data.bankCodeName);
				$('#h_settlementBankAccount').val(data.bankCodeName);
				$('#settlementBankAccountKey').val(data.bankCodeKey);
				$('#settlementBankAccountName').val(data.bankCodeDesc);
				$('#h_settlementBankAccountName').val(data.bankCodeDesc);
				$('#settlementAccountBeneficiary').val(data.bankCodeBeneficiary);
				$('#h_settlementAccountBeneficiary').val(data.bankCodeBeneficiary);
				$('#settlementAccountCurrency').val(data.bankCodeCurrency);
				$('#h_settlementAccountCurrency').val(data.bankCodeCurrency);
				$('#customerKey').val(data.customerKey);
				
				$('#transactionCode').focus();
				if (accountKey != data.code) {
//					disableCharge();
				}
				$('#holdingRefs').removeClass('fieldError');
				$('#holdingRefs').val('');
				$('#h_holdingRefs').val('');
				$('#holdingQuantity').val('');
				$('#h_holdingQuantity').val('');
				
				if (data.blocked == true) {
					$('#accountNoError').html('Account is Blocked');
					$('#blockedAccount').val('Account is Blocked');
				} else {
					$('#accountNoError').html('');
					$('#blockedAccount').val('');
				}
				
				getDefDiscTaxCode();
			}
	);
	
	if (jQuery.trim($('#accountNo').val()).length > 0) {
		var vAccount = $('#accountNo').val();
		$.get("@{Pick.account()}", {'code':vAccount}, function(data) {
			$('#customerKey').val(data.customerKey);
		});
	}
	/*$('#accountNo').lookup({
		list : '@{Pick.accounts()}',
		get : {
			url: '@{Pick.account()}',
			success: function(data) {
					var accountKey = $('#accountKey').val();
					$('#accountNo').removeClass('fieldError');
					$('#accountDesc').val(data.description);
					$('#h_accountDesc').val(data.description);
					$('#accountKey').val(data.code);
					$('#settlementAccount').removeClass('fieldError');
					$('#settlementAccount').val(data.bankAccountNo);
					$('#settlementAccountKey').val(data.bankAccountKey);
					$('#settlementAccountName').val(data.bankAccountDesc);
					$('#h_settlementAccountName').val(data.bankAccountDesc);
					$('#settlementBankAccount').removeClass('fieldError');
					$('#settlementBankAccount').val(data.bankCodeName);
					$('#h_settlementBankAccount').val(data.bankCodeName);
					$('#settlementBankAccountKey').val(data.bankCodeKey);
					$('#settlementBankAccountName').val(data.bankCodeDesc);
					$('#h_settlementBankAccountName').val(data.bankCodeDesc);
					$('#settlementAccountBeneficiary').val(data.bankCodeBeneficiary);
					$('#h_settlementAccountBeneficiary').val(data.bankCodeBeneficiary);
					$('#settlementAccountCurrency').val(data.bankCodeCurrency);
					$('#h_settlementAccountCurrency').val(data.bankCodeCurrency);
					
					$('#transactionCode').focus();
					if (accountKey != data.code) {
						disableCharge();
					}
					$('#holdingRefs').val('');
					$('#h_holdingRefs').val('');
					$('#holdingQuantity').val('');
					$('#h_holdingQuantity').val('');
					
					if (data.blocked == true) {
						$('#accountNoError').html('Account is Blocked');
						$('#blockedAccount').val('Account is Blocked');
					} else {
						$('#accountNoError').html('');
						$('#blockedAccount').val('');
					}
			},
			error: function(data){
					$('#accountNo').addClass('fieldError');
					$('#accountNo').val('');
					$('#accountDesc').val('NOT FOUND');
					$('#h_accountDesc').val('');
					$('#accountKey').val('');
			}
		},
		help : $('#accountHelp')
	});*/
	// ============================================================================================================//

	// FIELD TRANSACTION CODE // ==================================================================================//
	$('#transactionCode').change(function() {
		if ($('#transactionCode').val() == "") {
			clearValueForTransCode();
		}
	});
	
	$('#transactionCode').lookup({
		list : '@{Pick.transactionTemplates()}',
		get : function() {
			getTransactionCode();
		},
		filter:'USED_BY-1',
		help : $('#transactionHelp'),
		nextControl : $('#securityId')
	});
	// ============================================================================================================//
	
	

	// FIELD SECURITY CODE // ===================================================================//
	$('#securityId').change(function() {
		if ($('#securityId').val() == "") {
			$('#securityId').val("");
			$('#currTrans').val('');
			$('#currencyCode').val("");
			$('#securityKey').val("");
			$('#securityDesc').val("");
			$('#h_securityDesc').val("");
			$('#settlementDate').val('');
			$('#effectiveDate').val('');
			$('#effectiveDateHidden').val('');
			$('#maturityDate').val('');
			$('#maturityDateHidden').val('');
			$('#lastPaymentDate').val('');
			$('#lastPaymentDateHidden').val('');
			$('#nextPaymentDate').val('');
			$('#nextPaymentDateHidden').val('');
			$('#classification').val('');
			$('#classificationId').val('');
			$('#classificationDesc').val('');
			$('#h_classificationDesc').val('');
			$('#currencyCode').val('');
			$('#currencyCodeHid').val('');
			$('#expiredDate').val('');
			$('#isExpiredDate').val('');
			$('#ctpNo').val('');
			$('#ctpNoHidden').val('');
			$('#ctpNo').attr('disabled', 'disabled');
			$('p[id="pCtpRef"] label span').html("");
		}
		
	});
	
	
	attachSecuritiesPaging();
	getCustodianInfo();
//	$('#securityId').lookup({
//		list : '@{Pick.securities()}',
//		get : function() {
//			getSecurity();
//			
//			var minTrxQuantity = parseFloat($('#minTrxQuantity').val());
//			var quantity = parseFloat($('#quantityStripped').val());
//			
//			if ($('#quantity').val()!=''){
//				if (quantity < minTrxQuantity){
//					$('#quantity').addClass('fieldError');
//					$('#errQuantity').html("Can not less than Min Trx Quantity in Security Code " + ($('#securityId').val()));
//					return false;
//				} else {
//					$('#quantity').removeClass('fieldError');
//					$('#errQuantity').html("");
////					alert('maturityDate in Quantity = ' +$('#maturityDateHidden').val());
//					if ($('#yieldPorto').val()==''){
//						if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
//							countYield();
//						}
//					}
////					calculate(true);
//					calculate();
//				}
//			} 
//			
//			if ($('#accruedInterest').hasClass('fieldError')){
//				$('#accruedInterest').removeClass('fieldError');
//				$('#accruedInterestError').html('');
//			}
//		},
//		filter : $('#securityType'),
//		help : $('#securityHelp')
//	});
	// ============================================================================================================//

	// FIELD COUNTER PARTY ======================================================================================= //
	$('#counterPartyCode').change(function() {
		if ($('#counterPartyCode').val() == "") {
			$('#counterPartyCode').val("");
			$('#counterPartyKey').val("");
			$('#counterPartyName').val("");
			$('#h_counterPartyName').val("");
		}
	});

	$('#counterPartyCode').dynapopup2({key:'counterPartyKey', help:'counterPartyHelp', desc:'counterPartyName'},'PICK_GN_THIRD_PARTY', '${thirdPartyCounterPart}', 'classification', function(data){
		$('#counterPartyCode').removeClass('fieldError');
		$('#counterPartyKey').val(data.code);
		$('#counterPartyName').val(data.description);
		$('#h_counterPartyName').val(data.description);		
	},function(data){
		$('#counterPartyCode').addClass('fieldError');
		$('#counterPartyCode').val('');
		$('#counterPartyKey').val('');
		$('#counterPartyName').val('NOT FOUND');
		$('#h_counterPartyName').val('');		
	});

	var settlementAgentCode = $('#settlementAgentCode').val();
	$('#settlementAgentCode').change(function() {
		$('#settlementAgentCodeError').html("");
		if ($('#settlementAgentCode').val() == "") {
			$('#settlementAgentCode').val("");
			$('#settlementAgentKey').val("");
			$('#settlementAgentName').val("");
			$('#h_settlementAgentName').val("");
			$('#inhouseReference').val("");
			settlementAgentCode = null;
			attachInhouseReference();
		}
	});

	$('#settlementAgentCode').dynapopup2({key:'settlementAgentKey', help:'settlementAgentHelp', desc:'settlementAgentName'},'PICK_GN_THIRD_PARTY', '${thirdPartyCounterPart}', 'classification', function(data){
		console.debug(data);
		$('#settlementAgentCode').removeClass('fieldError');
		$('#settlementAgentKey').val(data.code);
		$('#settlementAgentName').val(data.description);
		$('#h_settlementAgentName').val(data.description);
		console.debug("settlementAgentCode " + settlementAgentCode + " -- " + data.name);
		if(settlementAgentCode != data.name) $('#inhouseReference').val("");
		settlementAgentCode = data.name;
		attachInhouseReference();
		getCounterPartyType();
	},function(data){
		$('#settlementAgentCode').addClass('fieldError');
		$('#settlementAgentCode').val('');
		$('#settlementAgentKey').val('');
		$('#settlementAgentName').val('NOT FOUND');
		$('#h_settlementAgentName').val('');
		$('#inhouseReference').val("");
	});

	$('#settlementReasonCode').change(function() {
		if ($('#settlementReasonCode').val() == "") {
			$('#settlementReasonCode').val("");
			$('#settlementReasonKey').val("");
			$('#settlementReasonName').val("");
			$('#h_settlementReasonName').val("");
		}
	});

	$('#settlementReasonCode').dynapopup2({key:'settlementReasonKey', help:'settlementReasonHelp', desc:'settlementReasonName'},'PICK_GN_LOOKUP', "SETTLEMENT_REASON", "null", 
			function(data){
				if (data) {
					$('#settlementReasonCode').removeClass('fieldError');
					$('#settlementReasonKey').val(data.code);
					$('#settlementReasonName').val(data.description);
					$('#h_settlementReasonName').val(data.description);
					$('#settlementReasonCodeForPrematchError').html("");

					if($('#settlementReasonKey').val() == '${OTHR}'){
						$("#descriptionLbl span.req").show();
					} else {
						$("#descriptionLbl span.req").hide();
						$('#descriptionForPrematchError').html("");
					}
				}
			},
			function(data){
				$('#settlementReasonCode').addClass('fieldError');
				$('#settlementReasonName').val('NOT FOUND');
				$('#settlementReasonCode').val('');
				$('#settlementReasonKey').val('');
				$('#h_settlementReasonName').val('');
			}
	);

//	$('#counterPartyCode').lookup({
//		list : '@{Pick.thirdParties()}?type=${thirdPartyCounterPart}',
//		get : {
//			url:'@{Pick.thirdParty()}?type=${thirdPartyCounterPart}',
//			success: function(data){
//				$('#counterPartyCode').removeClass('fieldError');
//				$('#counterPartyKey').val(data.code);
//				$('#counterPartyName').val(data.description);
//				$('#h_counterPartyName').val(data.description);
//			},
//			error: function(data){
//				$('#counterPartyCode').addClass('fieldError');
//				$('#counterPartyCode').val('');
//				$('#counterPartyKey').val('');
//				$('#counterPartyName').val('NOT FOUND');
//				$('#h_counterPartyName').val('');
//			}
//		},
////		key : $('#counterPartyKey'),
//		description : $('#counterPartyName'),
//		help : $('#counterPartyHelp'),
//		nextControl : $('#classification')
//	});
	// ======================================================================================================== //

	$('#settlementReasonCode').change(function() {
		if ($('#settlementReasonCode').val() == "") {
			$('#settlementReasonCode').val("");
			$('#settlementReasonKey').val("");
			$('#settlementReasonName').val("");
			$('#h_settlementReasonName').val("");
		}
	});

	$('#settlementReasonCode').dynapopup2({key:'settlementReasonKey', help:'settlementReasonHelp', desc:'settlementReasonName'},'PICK_GN_LOOKUP', "SETTLEMENT_REASON", "null", 
			function(data){
				if (data) {
					$('#settlementReasonCode').removeClass('fieldError');
					$('#settlementReasonKey').val(data.code);
					$('#settlementReasonName').val(data.description);
					$('#h_settlementReasonName').val(data.description);
					$('#settlementReasonCodeForPrematchError').html("");

					if($('#settlementReasonKey').val() == '${OTHR}'){
						$("#descriptionLbl span.req").show();
					} else {
						$("#descriptionLbl span.req").hide();
						$('#descriptionForPrematchError').html("");
					}
				}
			},
			function(data){
				$('#settlementReasonCode').addClass('fieldError');
				$('#settlementReasonName').val('NOT FOUND');
				$('#settlementReasonCode').val('');
				$('#settlementReasonKey').val('');
				$('#h_settlementReasonName').val('');
			}
	);

	function attachInhouseReference() {
		var settlementAgentKey = $("#settlementAgentKey").val(); 
		console.debug("settlementAgentKey => " + settlementAgentKey);
		$("#inhouseReference").lookup({
			list:'@{Pick.getListTransaction()}?filter=' + $("#securityId").val()
																			+ "|" + settlementAgentKey
																			+ "|" + $("#transactionDate").val().fmtYYYYMMDD("/")
																			+ "|" + $("#settlementDate").val().fmtYYYYMMDD("/")
																			+ "|" + $("#quantityStripped").val()
																			+ "|" + $("#priceStripped").val()
																			+ "|" + $("#ctpNoHidden").val()
																			+ "|" + $("#accountNoKey").val()
																			+ "|" + $('#transactionTypeId').val(),
			get:{
				url:'@{Pick.getTransactionNew()}?filter=' + $("#securityId").val()
															+ "|" + settlementAgentKey
															+ "|" + $("#transactionDate").val().fmtYYYYMMDD("/")
															+ "|" + $("#settlementDate").val().fmtYYYYMMDD("/")
															+ "|" + $("#quantityStripped").val()
															+ "|" + $("#priceStripped").val()
															+ "|" + $("#ctpNoHidden").val()
															+ "|" + $("#accountNoKey").val()
															+ "|" + $('#transactionTypeId').val(),
				success: function(data){
					$("#inhouseReference").removeClass("fieldError");
					$("#inhouseReference").val(data.transactionNo);
					//start yusuf 5728  std dari 5724 rev 39114
					var ctpReq = $("#ctpRequired").val();
					if(ctpReq!="true"){
						$("#ctpNo").val(data.transactionNo);
						$("#ctpNoHidden").val(data.transactionNo);
					}
					//end
				},
				error: function(data){
					$("#inhouseReference").addClass('fieldError');
					$("#inhouseReference").val("");
				}
			},
			filter: $("#inhouseReference"),
			help: $("#inhouseReferenceHelp")
		});
	}
	
	function getCounterPartyType(){
		$.get("@{Transactions.getCounterPartyType()}?settlementAgentKey="+$("#settlementAgentKey").val(), function(data) {
			checkRedirect(data);
			$("#counterPartyTypeDescAccountCode").val(data.accountCode)
			$("#counterPartyTypeDescAccountAgentCode").val(data.accountAgentCode)
			cekCounterParty();
			setCounterPartyTypeDesc();
		});
	}

	//start yusuf 5728  std dari 5724 rev 40385, kalo inhouse reference nya di null in, ctp_no nya gk kosong
	$("#inhouseReference").change(function(){
		var req = $("#ctpRequired").val();
		var ih = $("#inhouseReference").val();
		if(req!="true" && ih==""){
			$("#ctpNo").val("");
			$("#ctpNoHidden").val("");
			attachInhouseReference();
		}
	});
	//end yusuf
	
	// FIELD CLASSIFICATION dropdown ========================================================================== //
	$('#classification').change(function(){
		if ($(this).val()==''){
			$('#classification').removeClass('fieldError');
			$('#classification').val('');
			$('#classificationId').val('');
			$('#classificationDesc').val('');
			$('#h_classificationDesc').val('');
		}
	});
	$('#classification').lookup({
		list : '@{Pick.lookups()}?group=CLASSIFICATION',
		get:{ 
			url:'@{Pick.lookup()}?group=CLASSIFICATION',
			success: function (data) {
				$('#classification').removeClass('fieldError');
				$('#classificationId').val(data.code);
				$('#classificationDesc').val(data.description);
				$('#h_classificationDesc').val(data.description);
				var classification = data.code;
				var viewClassification = $('#viewClassification').val();
				
				if (viewClassification.indexOf(classification) < 0){
					$('#classification').addClass('fieldError');
					$('#classificationError').html('This Classification not in security code '+$('#securityId').val());
					return false;
				} else {
					$('#classificationError').html('');
					if (data.code == '${classificationAfs}'){
						$('#valuationMethod').val($("#valuationAFS").val());
						$("#amortizationMethod").val($("#amortizationAFS").val());
						
						if ($('#valuationMethod').val()!='${valuationAmortized}'){
							$('#yield').val('');
							$('#yieldStripped').val('');
						} else {
							if ($('#yieldPorto').val()==''){
								if ($('#maturityDateHidden').val() != ''){
									countYield();
								}
							}
						}
					}
					
					if (data.code == '${classificationTrd}'){
						$('#valuationMethod').val($("#valuationTrade").val());
						$("#amortizationMethod").val($("#amortizationTrade").val());
						if ($('#valuationMethod').val()!='${valuationAmortized}'){
							$('#yield').val('');
							$('#yieldStripped').val('');
						} else {
							if ($('#yieldPorto').val()==''){
								if ($('#maturityDateHidden').val() != ''){
									countYield();
								}
							}
						}
					}
					
					if (data.code == '${classificationHtm}'){
						$('#valuationMethod').val($("#valuationHTM").val());
						$("#amortizationMethod").val($("#amortizationHTM").val());
						if ($('#valuationMethod').val()!='${valuationAmortized}'){
							$('#yield').val('');
							$('#yieldStripped').val('');
						} else {
							if ($('#yieldPorto').val()==''){
								if ($('#maturityDateHidden').val() != ''){
									countYield();
								}
							}
						}
					}
					$('#holdingRefs').removeClass('fieldError');
					$('#holdingRefs').val('');
					$('#h_holdingRefs').val('');
					$('#holdingQuantity').val('');
					$('#h_holdingQuantity').val('');
				}
			}, 
			error: function (data) {
				$('#classification').addClass('fieldError');
				$('#classification').val('');
				$('#classificationId').val('');
				$('#classificationDesc').val('NOT FOUND');
				$('#h_classificationDesc').val('');
			}
		},
		help : $('#classificationHelp')
	});

	// FIELD SETTLEMENT DATE ===================================================================================== //
	
	$('#settlementDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends
	});
	
	$('#settlementDate').change(function() {
		if (!($('#settlementDate').hasClass('fieldError')) && $('#securityId').val()!=''){
			var settlementDate = $('#settlementDate').datepicker('getDate');
			getSecurity();
			 $('#settlementDate').datepicker('setDate', settlementDate);
//			if ($('#transactionNo').val() == "") {//#4145 -- handle jika transaksi edit need correction
				if ($('#securityClass').val()!='${secClassEQ}'){
					$('#effectiveDate').datepicker('setDate', settlementDate);
					$('#effectiveDateHidden').datepicker('setDate', settlementDate);
				}
				
				if ($('#securityClass').val()!='${secClassMM}'){
					$.post('@{getCouponDates()}', {'securityKey':$('#securityKey').val(),'settlementDate':$(this).val()}, function(data) {
			    		checkRedirect(data);
						if (data!=null){
							var lastPaymentDate = new Date(data.lastPaymentDate);
							$('#lastPaymentDate').datepicker('setDate', lastPaymentDate);
							$('#lastPaymentDateHidden').datepicker('setDate', lastPaymentDate);
							$('#nextPaymentDate').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.nextPaymentDate)));
							$('#nextPaymentDateHidden').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.nextPaymentDate)));
						} else {
							$('#lastPaymentDate').val('');
							$('#lastPaymentDateHidden').val('');
							$('#nextPaymentDate').val('');
							$('#nextPaymentDateHidden').val('');
						}
					});
				} else {
					$('#lastPaymentDate').datepicker('setDate', settlementDate);
					$('#lastPaymentDateHidden').datepicker('setDate', settlementDate);	
				}
				
				calculateAccruedDays();
//				calculateAccruedInterest();
				calculate(true);
//				alert('maturityDate in settlement date = ' +$('#maturityDateHidden').val());
				if ($('#yieldPorto').val()==''){
					if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
						countYield();
					}
				}
//			}
		}
	});
	
	/*$('#settlementDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends, 
		onSelect : function() {
			if (!($('#settlementDate').hasClass('fieldError')) && $('#securityId').val()!=''){
				var settlementDate = $('#settlementDate').datepicker('getDate');
				if ($('#transactionNo').val() == "") {
					if ($('#securityClass').val()!='${secClassEQ}'){
						$('#effectiveDate').datepicker('setDate', settlementDate);
						$('#effectiveDateHidden').datepicker('setDate', settlementDate);
					}
					
					if ($('#securityClass').val()!='${secClassMM}'){
						$.post('@{getCouponDates()}', {'securityKey':$('#securityKey').val(),'settlementDate':$(this).val()}, function(data) {
				    		checkRedirect(data);
							if (data!=null){
								var lastPaymentDate = new Date(data.lastPaymentDate);
								$('#lastPaymentDate').datepicker('setDate', lastPaymentDate);
								$('#lastPaymentDateHidden').datepicker('setDate', lastPaymentDate);
								$('#nextPaymentDate').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.nextPaymentDate)));
								$('#nextPaymentDateHidden').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.nextPaymentDate)));
							}
						});
					} else {
						$('#lastPaymentDate').datepicker('setDate', settlementDate);
						$('#lastPaymentDateHidden').datepicker('setDate', settlementDate);	
					}
					
					calculateAccruedDays();
					calculateAccruedInterest();
					//calculate(true);
//					alert('maturityDate in settlement date = ' +$('#maturityDateHidden').val());
					if ($('#yieldPorto').val()==''){
						if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
							countYield();
						}
					}
				}
			}
		}
	});*/
	
	/*$('#settlementDate').change(function() {
		console.log("Settlement Date = " +$(this).val());
		if (!($('#settlementDate').hasClass('fieldError')) && $('#securityId').val()!=''){
			var settlementDate = $('#settlementDate').datepicker('getDate');
			if ($('#transactionNo').val() == "") {
				if ($('#securityClass').val()!='${secClassEQ}'){
					$('#effectiveDate').datepicker('setDate', settlementDate);
					$('#effectiveDateHidden').datepicker('setDate', settlementDate);
				}
				
				if ($('#securityClass').val()!='${secClassMM}'){
					$.post('@{getCouponDates()}', {'securityKey':$('#securityKey').val(),'settlementDate':$(this).val()}, function(data) {
			    		checkRedirect(data);
						if (data!=null){
							var lastPaymentDate = new Date(data.lastPaymentDate);
							$('#lastPaymentDate').datepicker('setDate', lastPaymentDate);
							$('#lastPaymentDateHidden').datepicker('setDate', lastPaymentDate);
							$('#nextPaymentDate').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.nextPaymentDate)));
							$('#nextPaymentDateHidden').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.nextPaymentDate)));
						}
					});
				} else {
					$('#lastPaymentDate').datepicker('setDate', settlementDate);
					$('#lastPaymentDateHidden').datepicker('setDate', settlementDate);	
				}
				
				calculateAccruedDays();
				calculateAccruedInterest();
				//calculate(true);
//				alert('maturityDate in settlement date = ' +$('#maturityDateHidden').val());
				if ($('#yieldPorto').val()==''){
					if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
						countYield();
					}
				}
			}
		}
	});*/
	// ============================================================================================================//
	
	// FIELD PORTFOLIO ============================================================================================//
	$('#holdingRefs').lookup({
		list : '@{Pick.holdings()}',
		get : {
			url : '@{Pick.holding()}',
			success : function(data) {
				if(data.quantity == undefined){
					$('#holdingRefs').addClass('fieldError');
					$('#h_holdingRefs').val('');
					$('#holdingRefs').val('');
					$('#holdingQuantityStripped').val('');
					$('#holdingQuantity').val('NOT FOUND');
					$('#h_holdingQuantity').val('');
				} else {
					$('#holdingRefs').removeClass('fieldError');
					$('#holdingQuantity').autoNumericSet(data.quantity);
					$('#h_holdingQuantity').val(data.quantity);
					$('#h_holdingRefs').val(data.code);
					var transactionType = $('#transactionType').val();
					if (transactionType == '${transTypeS}') {
						var securityClass = $('#securityClass').val();
						if (data.code != 'TOTAL') {
							$('#quantity').autoNumericSet(data.quantity);
							$('#quantityStripped').val(data.quantity);
						}
						if (securityClass == '${secClassMM}') {
							$('#interestRate').autoNumericSet(data.interestRate);
							$('#interestRateStripped').val(data.interestRate);
							//$('#lastPaymentDate').datepicker('setDate', new Date(data.lastPaymentDate));
							$('#lastPaymentDateHidden').datepicker('setDate', new Date(data.lastPaymentDate));
							$('#nextPaymentDateHidden').datepicker('setDate', new Date(data.nextPaymentDate));
							$('#effectiveDate').datepicker('setDate', new Date(data.effectiveDate));
							$('#effectiveDateHidden').datepicker('setDate', new Date(data.effectiveDate));
							$('#maturityDate').datepicker('setDate', new Date(data.maturityDate));
							$('#maturityDateHidden').datepicker('setDate', new Date(data.maturityDate));
							//$('#maturityDateHidden').datepicker('setDate', new Date(data.nextPaymentDate));
							//calculateAccruedDays();
						}
					}
					$('#yield').val(data.yield);
					$('#yieldStripped').val(data.yield);
					$('#yieldPorto').val(data.yield);
					
					if (('${mode}'=='entry' || '${mode}'=='edit') && '${confirming}' != 'true') {
						calculate(true);
					}
				}
			}
		},
		filter : [ $('#settlementDate'), $('#accountNoKey'),
				$('#securityKey'), $('#classificationId') ],
		help : $('#holdingHelp'),
		nextControl : $('#quantity')
	});
	
	// additional condition for holdingRef lookup
	if ('${mode}'=='edit') {
		if ($('#transactionType').val()=='${transTypeS}'){
			$('#holdingHelp').removeAttr('disabled');
		} else {
			$('p[id=pHoldingRef] label span').html("");
		}
		
		if ('${securityClass}' == '${secClassFI}') {
			$('#interestRate').attr('disabled', 'disabled');
		}
		
		if($('#frequency').val() != ''){
			$('#frequency').val($('#frequency').val().replace("FREQUENCY-", ""));
		}
		
		if($('#accrualBase').val() != ''){
			$('#accrualBase').val($('#accrualBase').val().replace("ACCRUAL_BASE-", ""));
		}
		
		if($('#yearBase').val() != ''){
			$('#yearBase').val($('#yearBase').val().replace("YEAR_BASE-",""));
		}
	}
	// ============================================================================================================//

	// FIELD SETTLEMENT ACCOUNT ===================================================================================//
	$('#settlementAccount').lookup({
		list : '@{Pick.bankAccountsByAcctNo()}?domain=CUSTOMER&by=bnaccount',
		get:{
			url: '@{Pick.bankAccountByAccountNoAndCustomerKey()}?domain=CUSTOMER',
			success: function(data){
				if (data) {
					
					//if ($("#accountNo").val() != ""){
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
					//	$('#settlementAccountBeneficiary').val(data.customer.customerName);
						$('#settlementAccountBeneficiary').val(data.description);
						$('#h_settlementAccountBeneficiary').val(data.description);
						$('#settlementAccountCurrency').val(data.currency.currencyCode);
						$('#h_settlementAccountCurrency').val(data.currency.currencyCode);
						$('#h_settlementAccount').val(codeSplit[0]);
				//	} else {
//						$('#settlementAccount').addClass('fieldError');
//						$('#settlementAccountName').val('NOT FOUND');
//						$('#settlementAccount').val('');
//						$('#settlementAccountKey').val('');
//						$('#h_settlementAccountName').val('');
//						$('#settlementBankAccount').val('');
//						$('#h_settlementBankAccount').val('');
//						$('#settlementBankAccountKey').val('');
//						$('#settlementBankAccountName').val('NOT FOUND');
//						$('#h_settlementBankAccountName').val('');
//						$('#settlementAccountBeneficiary').val('');
//						$('#h_settlementAccountBeneficiary').val('');
//						$('#settlementAccountCurrency').val('');
//						$('#h_settlementAccountCurrency').val('');
				//	}
					
				}	
			},
			error: function(data) {
				$('#settlementAccount').addClass('fieldError');
				$('#settlementAccountName').val('NOT FOUND');
				$('#settlementAccount').val('');
				$('#settlementAccountKey').val('');
				$('#h_settlementAccountName').val('');
				$('#settlementBankAccount').val('');
				$('#h_settlementBankAccount').val('');
				$('#settlementBankAccountKey').val('');
				$('#settlementBankAccountName').val('NOT FOUND');
				$('#h_settlementBankAccountName').val('');
				$('#settlementAccountBeneficiary').val('');
				$('#h_settlementAccountBeneficiary').val('');
				$('#settlementAccountCurrency').val('');
				$('#h_settlementAccountCurrency').val('');
			}
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
	
	
//	$('input.calculate').live('blur', function() {
//		calculate();
//	});
	
	// FIELD QUANTITY =============================================================================================//
	$('#quantity').live('blur', function() {
		var minTrxQuantity = parseFloat($('#minTrxQuantity').val());
		var quantity = parseFloat($('#quantityStripped').val());
		
		if ($('#quantity').val()!=''){
			if (quantity < minTrxQuantity){
				$('#quantity').addClass('fieldError');
				$('#errQuantity').html("Can not less than Min Trx Quantity in Security Code " + ($('#securityId').val()));
				return false;
			} else {
				$('#quantity').removeClass('fieldError');
				$('#errQuantity').html("");
//				alert('maturityDate in Quantity = ' +$('#maturityDateHidden').val());
//				calculate(true);
				calculate();
			}
			calculateAccruedInterest();
		} 
		
		if ($('#accruedInterest').hasClass('fieldError')){
			$('#accruedInterest').removeClass('fieldError');
			$('#accruedInterestError').html('');
		}
		if ($('#price').val() != ''){
			$('#price').blur();
		}
		
		if ($('#discountTaxCode').val() != ''){
			fillDiscAmount();
		}
		
		if ($('#yieldPorto').val()==''){ //dipindahkan ke sini agar nilai yield ikut terupdate #4145
			if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
				countYield();
			}
		}
		
	});
	// ============================================================================================================ //

	$("#holdingRefs").blur();
	// FIELD PRICE =============================================================================================//
	$('#price').live('blur', function() {
		var holdRef = $("#holdingRefs").val();
		getSecurity();//4145 panggil ini karena butuh nilai valuationMethod dan amortizationMethod
		$("#holdingRefs").val(holdRef).blur(); //isiin lagi karena abis getSecurity() nilai holdingrefs ilang
//		alert('maturityDate in Price = ' +$('#maturityDateHidden').val());
		/*if ($('#yieldPorto').val()==''){
			if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
				countYield();
			}
		}*/
//		calculate(true);
		calculate();
		
		// re calculate discount after tax
		var quantity = $('#quantityStripped').val();
		var parPrice = $('#parPrice').val();
		var priceUnit = $('#priceUnit').val();
		var netAmount = $('#netAmountStripped').val();

		if($('#discounted').val() == 'true')
		{
			var discAfterTax = (Number(quantity) * Number (parPrice) * Number(priceUnit)) - Number (netAmount);
			$('#discountNet').autoNumericSet(discAfterTax);
			$('#discountNetStripped').val(discAfterTax);
		}
		
		//4145 perhitungan dipindah di sini karena butuh nilai netAmount
		if ($('#yieldPorto').val()==''){
			if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
				countYield();
			}
		}
		
	});
	// ============================================================================================================ //
	
	// FIELD NOMINAL ============================================================================================== //
	$('#netAmount').live('blur',function() {
		// fill price
		var nominal = $('#netAmountStripped').val();
		var quantity = parseFloat($('#quantityStripped').val());
		var priceUnit = parseFloat($("#priceUnit").val());
		var price = null;
		
		if(nominal != ''){
			if(quantity!=''){
				price = Number(nominal)/Number(quantity)/Number(priceUnit);
				$('#price').autoNumericSet(price);
				$('#priceStripped').val(price);
			}
			// fill disc after tax
			var parPrice = $('#parPrice').val();
			if (parPrice=='') parPrice = 0;
			var parValue = Number(quantity) * Number(parPrice) * Number(priceUnit);
			if (parValue=='') parValue = 0;
			var discAfterTax = Number(parValue) - Number(nominal);
	//		$('#discountNet').autoNumericSet(discAfterTax);
	//		$('#discountNetStripped').val(discAfterTax);
	
			//fill tax of disc/ Tax Amount
			/*var discountRate = $('#discountTaxRate').val();
			if (discountRate=='') discountRate = 0;
			var countRate = Number(100) - Number(discountRate);
			var taxOfDisc = Number(Number(discountRate)/Number(countRate)*Number(discAfterTax));
			$('#discountTax').autoNumericSet(taxOfDisc);
			$('#discountTaxStripped').val(taxOfDisc);
			
			//fill disc amount
			var discountAmount = Number(discAfterTax) + Number(taxOfDisc);
			$('#discountAmount').autoNumericSet(discountAmount);
			$('#discountAmountStripped').val(discountAmount);
	//		alert('maturityDate in Nominal = ' +$('#maturityDateHidden').val());
			*/
			
			if ($('#yieldPorto').val()==''){
				if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
					countYield();
				}
			}
			
			if ($('#discountTaxCode').val() != ''){
				fillDiscAmount();
			}
	
	//		calculate(true);
			calculate();
		}

	});
	// ============================================================================================================ //
	
	// FIELD DISCOUNT AMOUNT ====================================================================================== //
	$('#discountAmount').live('blur', function() {
//		alert('maturityDate in discount amount = ' +$('#maturityDateHidden').val());
		if ($('#yieldPorto').val()==''){
			if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
				countYield();
			}
		}
	});
	// ============================================================================================================ //
	
	// FIELD TAX CODE ON THE LEFT =================================================================================== //
	$('#discountTaxCode').change(function(){
		if ($('#discountTaxCode').val() == "") {
			$('#discountTaxCode').val('');
			$('#discountTaxKey').val('');
			$('#discountTaxRate').val('');
			$('#discountTaxDesc').val('');
			$('#h_discountTaxDesc').val('');
		}
	});
	
	$('#discountTaxCode').lookup({
		list : '@{Pick.taxMasters()}',
		get : {
			url : '@{Pick.taxMaster()}',
			success : function(data) {
				$('#discountTaxCode').removeClass('fieldError');
				$('#h_discountTaxCode').val(data.taxCode);
				$('#discountTaxKey').val(data.code);
				$('#discountTaxDesc').val(data.description);
				$('#h_discountTaxDesc').val(data.description);
				$('#discountTaxRate').val(data.taxRate);
				
				/*var discAfterTax = $('#discountNetStripped').val();
				if(discAfterTax == '') discAfterTax = 0;

				//fill tax of disc/ Tax Amount
				var discountRate = $('#discountTaxRate').val();
				if (discountRate=='') discountRate = 0;
				var countRate = Number(100) - Number(discountRate);
				var taxOfDisc = Number(Number(discountRate)/countRate*discAfterTax);
				$('#discountTax').autoNumericSet(taxOfDisc);
				$('#discountTaxStripped').val(taxOfDisc);
				
				//fill disc amount
				var discountAmount = Number(discAfterTax) + Number(taxOfDisc);
				$('#discountAmount').autoNumericSet(discountAmount);
				$('#discountAmountStripped').val(discountAmount);*/
				//fill tax of disc/tax amount pindahin ke method fillDiscAmount agar gampang maintenance
				fillDiscAmount();
				calculate();
			},
			error: function(data) {
				$('#discountTaxCode').addClass('fieldError');
				$('#discountTaxDesc').val('NOT FOUND');
				$('#discountTaxCode').val('');
				$('#discountTaxKey').val('');
				$('#discountTaxRate').val('');
				$('#h_discountTaxDesc').val('');
			}
		},
	//	key : $('#discountTaxKey'),
		help : $('#discountTaxHelp'),
		nextControl : $('#discountTax')
	});
	// ============================================================================================================ //
	
	// TAX AMOUNT ON THE LEFT ===================================================================================== //
	$('#discountTax').live('blur', function(){
		var discAfterTax = parseFloat($('#discountNetStripped').val());
		var discountTax = parseFloat($('#discountTaxStripped').val());
		var nominal = $('#netAmountStripped').val();
		var quantity = parseFloat($('#quantityStripped').val());
		var parPrice = $('#parPrice').val();
		
		if (!$('#netAmount').attr('disabled')){
			if (discAfterTax == '') discAfterTax = 0;
			if (discountTax == '') discountTax = 0;
			var discountAmount = Number(discAfterTax) + Number(discountTax);
			$('#discountAmount').autoNumericSet(discountAmount);
			$('#discountAmountStripped').val(discountAmount);
		
		} else {
			calculate();

			var discountAmount = Number(discAfterTax) + Number(discountTax);
			$('#discountAmount').autoNumericSet(discountAmount);
			$('#discountAmountStripped').val(discountAmount);
			if ($('#yieldPorto').val()==''){
				if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
					countYield();
				}
			}
		}
	});
	// ============================================================================================================ //
	
	// FIELD INTEREST RATE ======================================================================================== //
	$('#interestRate').live('blur', function() {
//		alert('maturityDate in interest rate = ' +$('#maturityDateHidden').val());
		if ($('#yieldPorto').val()==''){
			if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
				countYield();
			}
		}
		calculate(true);
	});
	// ============================================================================================================ //
	
	// FIELD MATURITY DATE ======================================================================================== //
	$('#maturityDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends
	});
	
	$('#maturityDate').change(function(){
		if (($('#maturityDate').val()!='')&&(!$('#maturityDate').hasClass('fieldError'))){
			var maturityDate = $('#maturityDate').datepicker('getDate');
			$('#maturityDateHidden').datepicker('setDate', maturityDate);
			$('#nextPaymentDateHidden').datepicker('setDate', maturityDate);
//			alert('maturityDate in maturity date = ' +$('#maturityDateHidden').val());
			if ($('#yieldPorto').val()==''){
				if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
					countYield();
				}
			}
//			calculate(true);
			calculate();
		}
	});
	// ============================================================================================================ //
	
	// FIELD ACCRUED DAYS ======================================================================================== //
	$('#accruedDays').live('blur', function() {
		calculate(true);
	});
	// ============================================================================================================ //
	//$('#h_accruedInterest').val('');
//	$('#currentAccruedInterest').val($('#accruedInterestStripped').val());
	
	// FIELD ACCRUED INTEREST  ================================================================================== //
//	$('#accruedInterest').removeAttr('disabled');
	$('#accruedInterest').live('blur', function() {
		/*var waveValue = $("#waveAccruedInterest").val();
		var thisValue = $('#accruedInterestStripped').val();
		var currentValuePlusOne = Number(waveValue) + Number(1);
		var currentValueMinusOne = Number(waveValue) - Number(1);*/
		
		var valid = checkAccInterest();
		if ($('#holdingType').val()=='${holdingTypeTotal}'){
				if (!valid){
					$(this).addClass('fieldError');
					return false;
				} else {
					$(this).removeClass('fieldError');
					$('#accruedInterestError').html('');
					calculate();
				}
		}
		
		calculate();
		
	});
	$('#cekBoxWaiveAccInt').change(function(){
		if ($(this).is(':checked')){
			$('#cekBoxWaiveAccInt').val();
			$('#hidChk').val("true");
			$('#accruedInterest').removeClass('fieldError');
			$('#accruedInterestError').html('');
			$('#accruedInterest').attr('disabled', 'disabled');
			$('#accruedInterest').val(0);
			$('#accruedInterestStripped').val(0);
			calculate(false);
		} else {
			$('#hidChk').val('false');
			$('#cekBoxWaiveAccInt').val(false);
			$('#accruedInterest').removeAttr('disabled');
			$('#accruedInterest').autoNumericSet($('#waveAccruedInterest').val());
			$('#accruedInterestStripped').val($('#waveAccruedInterest').val());
			calculate();
		}
	});
	// ============================================================================================================ //
	// FIELD TAX CODE ON THE RIGHT ================================================================================ //
	
	$('#taxOnInterestCode').change(function() {
		if ($('#taxOnInterestCode').val() == "") {
			$('#taxOnInterestCode').val('');
			$('#taxOnInterestKey').val('');
			$('#taxOnInterestRate').val('');
			$('#taxOnInterestDesc').val('');
			$('#h_taxOnInterestDesc').val('');
		}
	});

	$('#taxOnInterestCode').lookup({
		list : '@{Pick.taxMasters()}',
		get : {
			url : '@{Pick.taxMaster()}',
			success : function(data) {
				$('#taxOnInterestCode').removeClass('fieldError');
				$('#h_taxOnInterestCode').val(data.taxCode);
				$('#taxOnInterestKey').val(data.code);
				$('#taxOnInterestDesc').val(data.description);
				$('#h_taxOnInterestDesc').val(data.description);
				$('#taxOnInterestRate').val(data.taxRate);
//					var accruedInterest = $('#accruedInterest').autoNumericGet();
//					var taxOnInterest = accruedInterest * data.taxRate * 0.01;
//					$('#taxOnInterest').autoNumericSet(taxOnInterest);
//					$('#taxOnInterestStripped').val(taxOnInterest);
					calculate();
			},
			error: function() {
				$('#taxOnInterestCode').addClass('fieldError');
				$('#taxOnInterestCode').val('');
				$('#h_taxOnInterestCode').val('');
				$('#taxOnInterestDesc').val('NOT FOUND');
				$('#h_taxOnInterestDesc').val('');
				$('#taxOnInterestRate').val('');
				$('#taxOnInterest').val('');
				$('#taxOnInterestStripped').val('');
			}
		},
	//	key : $('#taxOnInterestKey'),
		help : $('#taxOnInterestHelp'),
		nextControl : $('#taxOnInterest')
	});
	// ============================================================================================================ //
	
	$('#taxOnInterest').live('blur', function(){
		calculate();
	});
	
	// FIELD CAPITAL GAIN TAX ========================================================================================= //
	$('#capitalGainTaxCode').change(function(){
		if ($('#capitalGainTaxCode').val() == "") {
			$('#capitalGainTaxCode').val('');
			$('#capitalGainTaxKey').val('');
			$('#capitalGainTaxRate').val('');
			$('#capitalGainTaxDesc').val('');
			$('#h_capitalGainTaxDesc').val('');
		}
	});

	$('#capitalGainTaxCode').lookup({
		list : '@{Pick.taxMasters()}',
		get : {
			url : '@{Pick.taxMaster()}',
			success : function(data) {
				$('#capitalGainTaxCode').removeClass('fieldError');
				$('#capitalGainTaxDesc').val(data.description);
				$('#h_capitalGainTaxDesc').val(data.description);
				$('#capitalGainTaxRate').val(data.taxRate);
//				if ($('#capitalGainTax').val() == '') {
//					var proceed = $('#proceedStripped').autoNumericGet();
//					var netAmount = $('#netAmountStripped').autoNumericGet();
					//var capitalGainTax = proceed * data.taxRate * 0.01;
//					var capitalGainTax = netAmount * data.taxRate * 0.01;
//					$('#capitalGainTax').autoNumericSet(capitalGainTax);
//					$('#capitalGainTaxStripped').val(capitalGainTax);
					calculate();
//				}
			},
			error : function(data) {
				$('#capitalGainTaxCode').addClass('fieldError');
				$('#capitalGainTaxDesc').val('NOT FOUND');
				$('#capitalGainTaxCode').val('');
				$('#capitalGainTaxKey').val('');
				$('#capitalGainTaxRate').val('');
				$('#h_capitalGainTaxDesc').val('');
			}
		},
		key : $('#capitalGainTaxKey'),
		help : $('#capitalGainTaxHelp'),
		nextControl : $('#next1')
	});
	
	// ============================================================================================================ //
	
	$('#capitalGainTax').live('blur', function(){
		calculate();
	});
	
	// BUTTON NEXT ON TAB DETAIL ============================================================================= //
	$('#next1').click(function() {
		$('#tabs').tabs('select', 1);
		if (tab2Visited) {
			$('#next2').focus();
		} else {
			$('#chargeList input:text').first().focus();
		}
	});
	// ======================================================================================================== //
	
	// BUTTON PREV ON TAB CHARGES ============================================================================= //
	$('#prev2').click(function() {
		$('#tabs').tabs('select', 0);
	});
	// ======================================================================================================== //
	
	// BUTTON NEXT ON TAB CHARGES ============================================================================= //
	$('#next2').click(function() {
		$('#tabs').tabs('select', 2);
	});
	// ======================================================================================================== //
	
	// BUTTON PREV ON TAB BANK INFO ============================================================================= //
	$('#prev3').click(function() {
		$('#tabs').tabs('select', 1);
	});
	// ======================================================================================================== //
	
	// BUTTON NEXT ON TAB BANK INFO ============================================================================= //
	$('#next3').click(function() {
		$('#tabs').tabs('select', 3);
	});
	// ======================================================================================================== //
	
	// BUTTON PREV ON TAB ADDITIONAL ============================================================================= //
	$('#prev4').click(function() {
		$('#tabs').tabs('select', 2);
	});
	// ======================================================================================================== //
	
	$('.netAmount').change(function() {
//		disableCharge();
	});
	
	// ACTION BUTTON ADD ON TAB CHARGES ======================================================================= //
	$('#chargeKey').lookup({
		list : '@{Pick.chargesForTransaction()}',
		get : function() {
			$.post('@{Transactions.charge()}',
					{
						'code': $('#chargeKey').val(),
						'quantity':$('#quantityStripped').val(),
						'nominal':$('#netAmountStripped').val(),
						'custodyAccountKey':$('#accountNoKey').val()
					},
					function(response, status, xhr) {
			    		checkRedirect(response);
						chargeGrid.fnAddData(response);
						//$('#chargeList tbody .mask').autoNumeric({vMin:'-999999999999.'});
						var rowlength =  $("#chargeList tbody tr").length;
						var tr = $("#chargeValue\\["+(rowlength-1)+"\\]").parent().parent();
						$('.mask', tr).autoNumeric({vMin:'-999999999999.'});
						calculate();
						$('#chargeKey').val('');
					}
				);
		},
		help : $('#addCharge')
	});
	// ======================================================================================================== //
	
	// ACTION BUTTON RELOAD ON TAB CHARGES ===================================================================== //
	$('#resetCharge').click(function() {
		if (($('#accountNoKey').val()=='') || ($('#transactionTemplateKey').val()=='') || ($('#securityKey').val()=='') || ($('#quantityStripped').val()=='') || ($('#netAmountStripped').val()=='')){
			$('#errGlobal').html("Please check Account No, Transaction Code, Security Code, Quantity and Nominal. Make sure all of them are not empty !");
			return false;
		}
		getCharges();
		$('#isreload').val(true);
	});
	// ========================================================================================================= //
	
	// ACTION BUTTON ">>" ON TAB DETAIL SIDE ON TOTAL CHARGES FIELD ============================================= //
	$('#showCharges').click(function() {
		$('#tabs').tabs('select', 1);
		tab2Visited = true;
		$('#chargeDone').css('display', '');
		var firstElement = $('#chargeList input:text').first();
		firstElement.focus();
	});
	// ========================================================================================================= //
	
	// ACTION BUTTON DONE ON TAB CHARGES ======================================================================= //
	$('#chargeDone').click(function() {
		$('#chargeDone').css('display', 'none');
		$('#tabs').tabs('select', 0);
		if ($('#interestRate').attr('disabled')) {
			$('#tax').focus();
		} else {
			$('#interestRate').focus();
		}
	});
	// ========================================================================================================= //
	
	// ACTION RADION BUTTON PREMATCH REQUIRED ================================================================== //
	$('input[name=prematchRequired]').change(function(){
		if ($('#prematchRequired1').is(":checked")){
			$("input[name='transaction.prematchRequired']").val(true);
		} else {
			$("input[name='transaction.prematchRequired']").val(false);
		}
		
/*		var x = $("input[name='prematchRequired']:radio:checked").val();
		if (x) {
				("YES 1");
			$("input[name='transaction.prematchRequired']").val(true);
		} else {
			alert("NO 1");
			$("input[name='transaction.prematchRequired']").val(false);
		}
*/		
	});	
	// ========================================================================================================= //

	// ACTION RADION BUTTON IS_SCRIPT ========================================================================== //
	$('input[name=isScript]').change(function(){
		if ($('#isScript1').is(":checked")){
			$("input[name='transaction.isScript']").val(true);
		} else {
			$("input[name='transaction.isScript']").val(false);
		}
	});	
	// ========================================================================================================= //

	$('#ctpNo').change(function(){
		$('#ctpNoHidden').val($(this).val());
		$("#inhouseReference").val("").blur();
		attachInhouseReference();
	});

	var param = '${param}';
	if (('${fromInquiry}' == 'fromInquiry') && ('${statusTrans}'=='Waiting for prematch approval')){
		$('#forSettlePrematch').css('display','');
		$('#forSettlePrematch2').css('display','');
		$('#autoPrematchType').attr('disabled', 'disabled');
		$('input[name="sentToInterface"]').attr('disabled', 'disabled');
		$('#matchStatus').attr('disabled', 'disabled');
		$('#remarkForPrematch').attr('disabled', 'disabled');
		$('#pTransferType').css('display','');
	}

	if (param == 'settlement-prematch-view'){
		setPrematchCondition();
	}

	console.debug("${confirming} -- ${mode} -- ${fromApproval} -- ${fromInquiry}" + ", param = " + param.indexOf('settlement-prematch'));
	if (param.indexOf('settlement-prematch') > -1){
		$("#settlementAgentCodeLbl span.req").show();
		if('${fromApproval}' == 'true' || '${confirming}' == 'true'){
			$("#settlementAgentCode").attr('disabled', true);
			$("#settlementAgentHelp").attr('disabled', true);
			$('#dropdowncounterPartyType').attr('disabled', true);
		} else {
			$("#settlementAgentCode").attr('disabled', false);
			$("#settlementAgentHelp").attr('disabled', false);
			//$("#settlementAgentName").attr('disabled', false);
			$("#settlementAmountAdj").attr('disabled', false);
		}

		redmine2847();
	} else {
		if('${mode}' != 'entry' && '${mode}' != 'edit'){
			$("#settlementAgentCode").attr('disabled', true);
			$("#settlementAgentHelp").attr('disabled', true);

			var vTransactionTemplateKey = $('#transactionTemplateKey').val();
			$.get("@{Transactions.getCashAccountAmountType()}?transactionTemplateKey="+vTransactionTemplateKey, function(data) {
				checkRedirect(data);
				var amountType = data.amounttype;
				console.debug("amountType => " + amountType);
				if (amountType == '${bnTransTypeDebet}') {
					$('p[id=pCustomerCashAmount] label').html("Debet Amount");
					$('p[id=pCustodianAmount] label').html("Credit Amount");
				} else if (amountType == '${bnTransTypeCredit}') {
					$('p[id=pCustomerCashAmount] label').html("Credit Amount");
					$('p[id=pCustodianAmount] label').html("Debet Amount");
				}
			});
		}

		if('${fromInquiry}'=='fromInquiry'){
			$("#settlementAgentCodeLbl span.req").show();
			$("#forSettlePrematch :input").attr("disabled", true);
			$("#forSettlePrematch2 :input").attr("disabled", true);
		}
	}

	if (param.indexOf('settlement-prematch') > -1 && !($('#prematchStatus').val() == '${prematchP}' || $('#prematchStatus').val() == '${prematchW}')){
		$('#forSettlePrematch').css('display','');
		$('#forSettlePrematch2').css('display','');
		$('#pTransferType').css('display','');
		/*if(!'${confirming}' && '${mode}' != 'view'){
			alert(1)
		}else{
			alert(2)
		}*/
		/*$('#matchStatus').removeAttr('disabled');
		$('#remarkForPrematch').removeAttr('readonly');*/
		
		// old condition
		/*$("input[name*='sentToInterface']").change(function(){
			$('#sentToInterface1').val(true);
			$('#sentToInterface2').val(false);				
			if ($('#sentToInterface1').is(":checked")){
				$('#matchStatus').attr('disabled', 'disabled');
				$('#matchStatus').val('');
				$('p[id=pMatchStatus] label span').html("");
				$("input[name='transaction.sentToInterface']").val(true);
			} else {
				$('#matchStatus').removeAttr('disabled');
				//$('#matchStatus').val('TRANSACTION_MATCH_STATUS-U');
				$('p[id=pMatchStatus] label span').html(" *");
				$("input[name='transaction.sentToInterface']").val(false);
				//check for prematch 'P' or 'A'
				if ($('#prematchStatus').val() == '${prematchP}' || $('#prematchStatus').val() == '${prematchA}') {
					//$('#sentToInterface1').attr('disabled', 'disabled');
					//$('#sentToInterface2').attr('disabled', 'disabled');
				}
			}
		});*/

		$('#sentToInterface1').click(function(){
			$(this).val(true);
			$('#sentToInterface2').val(false);
			$("input[name='transaction.sentToInterface']").val(true);

			if (($('#ctpRequired').val()=='true') 
					&& ($('#depositoryCode').val() == '${depositoryCbest}' || $('#depositoryCode').val() == '${depositoryBis}')){
				$('p[id="pCtpRef"] label span').html(" *");
			}

			yieldPrematch();
			counterPartyTypeLive();
		});

		$('#sentToInterface2').click(function(){
			$(this).val(false);
			$('#sentToInterface1').val(true);
			$("input[name='transaction.sentToInterface']").val(false);

			if (($('#ctpRequired').val()=='true') 
					&& ($('#depositoryCode').val() == '${depositoryCbest}' || $('#depositoryCode').val() == '${depositoryBis}')){
				$('p[id="pCtpRef"] label span').html("");
				$('#ctpNoError').html("");
			}

			yieldPrematch();
			counterPartyTypeLive();
		});

		// check when prematch type (auto/manual prematch) is choosen
		$( "#autoPrematchType" ).change(function(){
//			var autoPrematchTypeVal = $(this).val();
			if ($(this).is(':checked')){
				$(this).val(true);
				$('p[id=pMatchStatus] label span').html('');
				$("input[name='sentToInterface']").attr("disabled","disabled");
				$("input[name='transaction.sentToInterface']").val(true);
				$('#sentToInterface1').attr('checked',true);
				$('#matchStatus').attr('disabled','disabled');
				$('p[id=pMatchStatus] label span').html(' ');
				$('#matchStatusError').html('');
			} else {
				$(this).val(false);
				$('p[id=pMatchStatus] label span').html(' *');
				$("input[name='sentToInterface']").removeAttr("disabled");
				$('#matchStatus').removeAttr('disabled');
			}
			isSentToInterface();
			$('#matchStatus').val('');
			$('#remarkForPrematch').val('');
			$('#remarkForPrematch').attr('disabled', 'disabled');
			$('p[id=pRemarkForPrematch] label span').html('');
			/*if( autoPrematchTypeVal === "true" || autoPrematchTypeVal === true){
				$("#sentToInterface1").attr("checked", "checked");
				$("#sentToInterface1").attr( "disabled", "disabled" );
				$("#sentToInterface2").attr( "disabled", "disabled" );
			}else{
				$("#sentToInterface2").attr("checked", "checked");
				$("#sentToInterface1").removeAttr( "disabled");
				$("#sentToInterface2").removeAttr( "disabled");
			}*/
//			$("input[name*='sentToInterface']").change();
		});
		
		$('#matchStatus').change(function(){
			$('#remarkForPrematch').val('');
			if ($(this).val()=='${matchU}'){
				$('#remarkForPrematch').removeAttr('disabled');
				$('p[id=pRemarkForPrematch] label span').html(' *');
				$('#matchStatusError').html('');
			} else {
				$('#remarkForPrematch').attr('disabled', 'disabled');
				$('p[id=pRemarkForPrematch] label span').html('');
				$('#remarkForPrematchError').html('');
				$('#matchStatusError').html('');
			}
		});
		
		//$("input[name*='sentToInterface']").change();
		//$("input[name*='autoPrematchType']").change();
		if (!'${confirming}' || '${confirming}'){
			setPrematchCondition();
		}
		
	} else {
		if ($('#prematchStatus').val() == '${prematchP}' || $('#prematchStatus').val() == '${prematchW}') {
			$('#forSettlePrematch').css('display','');
			$('#forSettlePrematch2').css('display','');
			$('#pTransferType').css('display','');
		}
	}

	if($('#forSettlePrematch').css('display') == 'table-cell') { 
		$('#generalSettlement').css('display','none');
	  } else {
		  $('#generalSettlement').css('display','block');
		  if ($('#generalSentToInterface2').val() == '' && $('#generalSentToInterface1').val() == '')
			  $('#generalSentToInterface2').val(false);				
	  }
	
	
	if (('${fromView}' == 'viewCsCancelTrade') || (('${fromInquiry}'=='fromInquiry') && ($('#statusTrans').val()=='Cancelled'))){
		$('#cancelDate').attr('disabled', false);
		$('#remarkForCancelTrade').attr('disabled', false);
		if ('${confirming}' == 'true') {
			$('#cancelDate').attr('disabled', true);
			$('#remarkForCancelTrade').attr('disabled', true);
		}
		
		$('#cancelDate').change(function(){
			$('#h_cancelDate').val($('#cancelDate').val());
		});
		
		if (('${fromInquiry}'=='fromInquiry') && ($('#statusTrans').val()=='Cancelled')){
			$('#cancelDate').attr('disabled', 'disabled');
			$('#remarkForCancelTrade').attr('disabled', 'disabled');
		}
		
		if('${fromView}' == 'viewCsCancelTrade'){
			redmine2847();
			$('#cancelDate').attr('disabled', true);
			$("#forSettlePrematch :input").attr("disabled", true);
			$("#forSettlePrematch2 :input").attr("disabled", true);
		}
	}
	
	if ('${fromInquiry}'=='fromInquiry'){
		$("#settlementAccount").attr('disabled', true);
		$("#settlementAccountHelp").attr('disabled', true);
	}
	
	function setPrematchCondition() {
		// old condition
		/*if ($('#sentToInterface1').is(":checked")){
			$('#matchStatus').attr('disabled', 'disabled');
			//$('#sentToInterface1').attr('disabled', 'disabled');
			//$('#sentToInterface2').attr('disabled', 'disabled');
			$('p[id=pMatchStatus] label span').html("");
			$('#matchStatus').val($('#matchStatus').val());
		} else {
			$('#matchStatus').removeAttr('disabled');
			$('p[id=pMatchStatus] label span').html(" *");
			//check for prematch 'P' or 'A'
			if ($('#prematchStatus').val() == '${prematchP}' || $('#prematchStatus').val() == '${prematchA}') {
				$('#sentToInterface1').attr('disabled', 'disabled');
				$('#sentToInterface2').attr('disabled', 'disabled');
			}
		}*/
		// new condition
		$('#autoPrematchType').attr('disabled', 'disabled');
		if ($('#depositoryCode').val()=='${depositoryCbest}'){
			if (param=='settlement-prematch'){
				$('#autoPrematchType').removeAttr('disabled');
			}
		}
		if ($('#autoPrematchType').is(':checked')){
			$('#matchStatus').attr('disabled', 'disabled');
			$('p[id=pMatchStatus] label span').html('');
		}
		
		if (($('#matchStatus').val()!='${matchU}') || (($('#matchStatus').val()==''))){
			$('#remarkForPrematch').attr('disabled', 'disabled');
			$('p[id=pRemarkForPrematch] label span').html('');
		}
		
		if ($('#depositoryCode').val()=='${depositoryNA}') {
			$("input[name='sentToInterface']").attr('disabled', 'disabled');
			$("#sentToInterface2").attr("checked", true);
			$("input[name='transaction.sentToInterface']").val(false);
		}
		
		
		if (!$('#prematchStatus').val()){ //prematch NULL
			$('#dropdownPrematchStatus').val('${prematchO}');
		} else if ($('#prematchStatus').val() == '${prematchW}') { //prematch 'W'
			//check matchStatus
			if ($('#matchStatus').val() == '${matchU}') {
				$('#dropdownPrematchStatus').val('${prematchWU}');
			} else if ($('#matchStatus').val() == '${matchM}') {
				$('#dropdownPrematchStatus').val('${prematchWM}');
			} else {
				$('#dropdownPrematchStatus').val('${prematchWO}');
			} //end matchStatus
		} else {
			$('#dropdownPrematchStatus').val($('#prematchStatus').val());
		}; //end if 'W'

		if ('${param}' == 'settlement-prematch-view'){
			//$('#sentToInterface1').attr('disabled', 'disabled');
			//$('#sentToInterface2').attr('disabled', 'disabled');
			$('#remarkForPrematch').attr('readonly', 'readonly');
			$('#matchStatus').attr('disabled', 'disabled');
		} else {
			//$('#sentToInterface1').removeAttr('disabled');
			//$('#sentToInterface2').removeAttr('disabled');
			if ('${param}' == 'settlement-prematch'){
				if (!$('#autoPrematchType').is(':checked')){
					$('#matchStatus').removeAttr('disabled');
					
				} else {
					$('input[name="sentToInterface"]').attr('disabled', 'disabled');
				}
			} else {
			$('#remarkForPrematch').removeAttr('readonly');
			$('#matchStatus').removeAttr('disabled');
			}
		}
	}; //end function setPrematchCondition	

	checkSentToInterface();
	$("#generalSentToInterface1").change(function() {
		$("input[name='transaction.sentToInterface']").val('true');
		isSentToInterface();
	});

	$("#generalSentToInterface2").change(function() {
		$("input[name='transaction.sentToInterface']").val('false');
		isSentToInterface();
	});

	if (('${mode}'=='entry')) {
		$("input[name='transaction.needCorrection']:checked").val('false');
		$("input[name='transaction.needCorrection']").val('false');
		$('#correction').hide();
		$("#correction").css("display","none");
	}
	
//	if (('${param}' === 'settlement-prematch') || ('${param}' === 'settlement-prematch-view')) {
//		$(".rightBuyCell").remove();
//		$(".leftTrx").attr("style", "width:100%;float:left;");
//	}
	if ('${maintenanceLogKey}' != '') {
		$('#correction').show();
		$("#correction").css("display","");
	} else {
		if (($("input[name='transaction.needCorrection']").val() == 'true') && ($("input[name='remarkCorrection']").val() != '')) {
			$('#correction').show();
			$("#correction").css("display","");
			$('#needCorrection').attr('disabled', true);
			$('#remarkCorrection').attr('disabled', true);
		}
	}

	if (('${param}' === 'settlement-prematch') || ('${param}' === 'settlement-prematch-view') || 
			('${fromInquiry}'=='fromInquiry') || ('${fromView}'=='viewCsCancelTrade') || 
			('${transaction.settlementNumber}'!='') || ('${param}' == 'cancelTrxApp')) {
		$('#correction').hide();
		$("#correction").css("display","none");
	}
	$('input[name=transaction.needCorrection]').change(function(){
		if ($("input[name='transaction.needCorrection']:checked").val() === 'true') {
			$('p[id=pRemarkCorrection] label span').html(" *");
		} else {
			$('p[id=pRemarkCorrection] label span').html("");
		}
	});
	
	var tSecurityId = $('#securityId').val();
	var tSecurityType = $('#securityType').val();
	var tTransactionDate = $('#transactionDate').val();
	if ((tSecurityId !== '') && (tSecurityType !== '') && (tTransactionDate !== '')) {
		$.get("@{Pick.security()}", {'code' : tSecurityId, 'filter' : tSecurityType, 'transactionDate' : tTransactionDate}, function(data) {
			checkRedirect(data);
			$('#currencyCode').val(data.securityCurrency);
			$('#currencyCodeHid').val(data.securityCurrency);
			$('#currTrans').val(data.securityCurrency);
		});
	}
	
	
	// CTP REFERENCE DEFAULT CONDITION
		/*if (($('#ctpRequired').val()=='true') && ($('#marketOfRisk').val() == '${countryId}') && ($('#prematchRequiredTemp').val() == 'false')) {
			$('p[id="pCtpRef"] label span').html(" *");
		} else {
			$('p[id="pCtpRef"] label span').html(" ");
		}*/
	// CS4004
	if (('${param}' == 'settlement-prematch') || ('${param}' == 'settlement-prematch-view')){
		if (($('#ctpRequired').val()=='true') && ($('#depositoryCode').val() == '${depositoryCbest}' || $('#depositoryCode').val() == '${depositoryBis}')){
			if ('${fromView}'!='viewCsCancelTrade') {
				$('#ctpNo').removeAttr('disabled');
				$('p[id="pCtpRef"] label span').html(" *");
				if (('${confirming}'=='true')){
					if ($('#ctpNo').val() != '') {
						//$('#ctpNo').autoNumericSet($('#ctpNo').val());
						$('#ctpNoHidden').val($('#ctpNo').val());
					}
					
					$('#ctpNo').attr('disabled', 'disabled');
				}
				if (('${fromApproval}')||('${fromInquiry}'=='fromInquiry')){
					$('#ctpNo').attr('disabled', 'disabled');
					if ($('#ctpNo').val()!=''){
						$('#ctpNoHidden').val($('#ctpNo').val());
						$('p[id="pCtpRef"] label span').html(" *");
					}
				}
				
			} else {
				$('#ctpNo').attr('disabled', 'disabled');
				$('p[id="pCtpRef"] label span').html("");
			}
		} else {
			$('#ctpNo').attr('disabled', 'disabled');
			$('p[id="pCtpRef"] label span').html("");
		}
	}
	
	// CS4001
	else if (($('#ctpRequired').val()=='true') && ($('#depositoryCode').val() == '${depositoryCbest}' || $('#depositoryCode').val() == '${depositoryBis}') && ($('#prematchRequiredTemp').val() == 'false')) {
		$('#ctpNo').removeAttr('disabled');
		$('p[id="pCtpRef"] label span').html(" *");
		if (('${confirming}'=='true')||('${mode}'=='view')){
			if ($('#ctpNo').val() != '') {
				//$('#ctpNo').autoNumericSet($('#ctpNo').val());
				$('#ctpNoHidden').val($('#ctpNo').val());
			}
			$('#ctpNo').attr('disabled', 'disabled');
			$('p[id="pCtpRef"] label span').html("");
		}
		
	}
	
	else {
		$('#ctpNo').attr('disabled', 'disabled');
		$('p[id="pCtpRef"] label span').html("");
	}
	
	
	if ($.browser.msie){
		$("#remaks[maxlength]").bind('input propertychange', function() {
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    });
		$("#remarkCorrection[maxlength]").bind('input propertychange', function() {
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    });
	}
	
	//penambahan redmine #2933
	$("#transactionCodeDesc").width('150px');
	
	$("#chkIsTransferAsset").click(function(){
		var isNom = $("#chkIsTransferAsset:checked").length;
		if(isNom<1){
			//gk dicentang
			$("#isTransferAsset").val("false");
		}else{
			//dicentang
			$("#isTransferAsset").val("true");
		}
	});
	
	$("#chkIsInpNominal").click(function(){
		changeIsNominal();
	});
	
	if($("#isInpNominal").val()=='true'){
		$("#chkIsInpNominal").attr('checked','checked');
	}else{
		$("#chkIsInpNominal").removeAttr('checked');
	}
	
	if($("#isTransferAsset").val()=='true'){
		$("#chkIsTransferAsset").attr('checked','checked');
	}else{
		$("#chkIsTransferAsset").removeAttr('checked');
	}
	
	if(!'${confirming}' && '${mode}' != 'view'){
		changeIsNominal();
	}

	cekCounterParty();
	
	console.log('${checkIssuerCounterPartyType}')
	console.log('${cbestOtb}')
	console.log("param : ${param}");
	console.log("confirm : ${confirming}");
	if (!('${param}' == 'settlement-prematch' || '${param}' == 'settlement-prematch-view')){
		$("#pCounterPartyType").hide();
		$('#dropdowncounterPartyType').val('');
	}
	if ('${param}' == 'settlement-prematch-view'){
		if($('#dropdowncounterPartyType').val() == ""){
			$("#counterPartyTypeLbl span.req").hide();
		}
	}
	
	$("#dropdowncounterPartyType").change(function(){
		setCounterPartyTypeDesc();
	});
	setCounterPartyTypeDesc();

	function showHideFundTrfMethod(){
		$("#dTransferType" ).removeAttr( "disabled");
		/*if($("#settlementAmountStripped").val() == 0){
			//jika settlementnya 0, sya cek di disable field transfer methodnya	
			alert(991)
			$("#dTransferType").attr('disabled','disabled');
		}else{*/
			if('${fromApproval}' == 'true' || '${confirming}' == 'true'){
				$("#dTransferType").attr('disabled','disabled');
			}else{
				$("#dTransferType").val("${transferManual}");
				//alert($('#custodyTransactionCode').val())
				if($('#custodyTransactionCode').val() == "RVP" || $('#custodyTransactionCode').val() == "DVP"){
					var paramRetailExclude = '${paramRetailExclude}';
					var paramExcludeAcctno = '${paramExcludeAcctno}';
					var setmanual = false;
					if(paramRetailExclude != ""){
						var arrParamRetailExclude = paramRetailExclude.split(";");
						for(i=0;i<arrParamRetailExclude.length;i++){
							if(arrParamRetailExclude[i] == $("#custRetailGroup").val()){
								setmanual = true;
								break;
							}
						}
					}
					if(paramExcludeAcctno != ""){
						var arrParamExcludeAcctno = paramExcludeAcctno.split(";");
						for(i=0;i<arrParamExcludeAcctno.length;i++){
							if(arrParamExcludeAcctno[i] == $("#accountNo").val()){
								setmanual = true;
								break;
							}
						}
					}
					
					if($("#depoCode").val() == "N/A"){
						setmanual = true;
					}
					
					if(setmanual){
						$("#transferMethod").val('${transferMethodManual}');
					}else{
						$('#transferMethod').val("${transferMethodInterface}");	
					}
					if($('#custodyTransactionCode').val() == "RVP" && defaultBankCode == $("#settlementBankAccount").val()){
						$("#dTransferType").val("${transferTypePool}");
					}else if($('#custodyTransactionCode').val() == "RVP" && defaultBankCode != $("#settlementBankAccount").val()){
						$("#dTransferType").val("${transferManual}");
					}else{
						$("#dTransferType").val("${transferTypePool}");
						$("#dTransferType").attr('disabled', true);
					}
					
				}else{
					$('#transferMethod').val("");
					$("#transferMethod").attr('disabled', true);
				}
			}
		//}
	}
	showHideFundTrfMethod();
}); //end ready function

function cekCounterParty(){
	if('${param}' == 'settlement-prematch'){
		if($('#depositoryCode').val() == '${depositoryCbest}' && $("#cbestMessageType").val() == '${cbestOtb}' && $('input[name="transaction.sentToInterface"]').val()=='true'){
			if ('${checkIssuerCounterPartyType}' == 0){
				$("#counterPartyTypeLbl span.req").show();
				$('#dropdowncounterPartyType').val('${counterPartyTypeInternal}');
				$('#dropdowncounterPartyType').attr('disabled', true);
				$('#counterPartyType').val('${counterPartyTypeInternal}');
				$('#counterPartyTypeDesc').val($('#counterPartyTypeDescAccountCode').val());
				$('#txtCounterPartyTypeDesc').val($('#counterPartyTypeDescAccountCode').val());
			}else{
				$("#counterPartyTypeLbl span.req").show();
				$('#dropdowncounterPartyType').attr('disabled', false);
			}
		}else{
			$("#counterPartyTypeLbl span.req").hide();
			$('#dropdowncounterPartyType').val('');
			$('#txtCounterPartyTypeDesc').val('');
			$('#counterPartyType').val('');
			$('#counterPartyTypeDesc').val('');
			$('#dropdowncounterPartyType').attr('disabled', true);
		}
	}
}

function setCounterPartyTypeDesc(){
	console.log($("#dropdowncounterPartyType").val());
	if($('#counterPartyTypeDescAccountCode').val() != '' || $('#counterPartyTypeDescAccountAgentCode').val() != ''){
		$("#counterPartyType").val( $("#dropdowncounterPartyType").val());
		if($("#dropdowncounterPartyType").val() == '${counterPartyTypeInternal}'){
			$('#counterPartyTypeDesc').val($('#counterPartyTypeDescAccountCode').val());
			$('#txtCounterPartyTypeDesc').val($('#counterPartyTypeDescAccountCode').val());
		}
		else if($("#dropdowncounterPartyType").val() == '${counterPartyTypeExternal}'){
			$('#counterPartyTypeDesc').val($('#counterPartyTypeDescAccountAgentCode').val());
			$('#txtCounterPartyTypeDesc').val($('#counterPartyTypeDescAccountAgentCode').val());
		}
		else {
			$('#counterPartyTypeDesc').val('');
			$('#txtCounterPartyTypeDesc').val('');
		}
	}
	
}

function disableCharge() {
	$('#chargeList tbody input').each(function() {
		$(this).attr('disabled', 'disabled');
	});
	$('#addCharge').button({disabled: true});
	reloadCharge = true;
}

function getCustodianInfo(){
	var accountNoKey = $("#accountNoKey").val();
	var securityKey = $("#securityKey").val();
	var transactionTemplateKey = $("#transactionTemplateKey").val();
	var currencyCode = $("#currencyCode").val();
	if(currencyCode == "") currencyCode = $("#currTrans").val();
	/*console.log("iiiiiiiiiiiiiiiiiiiiii")
	console.log(accountNoKey)
	console.log(securityKey)
	console.log(transactionTemplateKey)
	console.log(currencyCode)*/
	var isCustodianCredit = false;
	if($('#custodyTransactionCode').val() == "RVP" || $('#custodyTransactionCode').val() == "RFOP"){
		isCustodianCredit = true;
	}
	if(accountNoKey != "" && securityKey != "" && transactionTemplateKey != ""){
	
		$.post('@{Transactions.getBankInfoCustodian()}', {'accountNoKey' : accountNoKey, 'securityKey' : securityKey, 'transactionTemplateKey' : transactionTemplateKey, 'isCustodianCredit':isCustodianCredit, 'currrencyCode': currencyCode}, function(data) {
			//console.log("111111111111111111111111")
			//console.log(data)
			
			$("#bankAccountNo").val(data.accountNo);
			$("#bankCode").val(data.bankCode);
			$("#h_bankCode").val(data.bankCode);
			$("#bankCodeKey").val(data.bankKey);
			$("#bankCodeName").val(data.bankName);			
			$("#currencyCode456").val(data.benefName);
			$("#currencyCode789").val(data.currency);
			$("#h_currencyCode789").val(data.currency);
			
		});
	}
	

}

function attachSecuritiesPaging() {
	var securityType = $('#securityType').val();
	var pickName = (securityType == '') ? 'PICK_SC_MASTER' : 'PICK_SC_MASTER_BY_SEC_TYPE';
	$('#securityId').dynapopup2({key:'securityKey', help:'securityHelp', desc:'securityDesc'}, pickName, securityType, 'counterPartyCode', function(data){
		getSecurity();
		getCustodianInfo();
		var minTrxQuantity = parseFloat($('#minTrxQuantity').val());
		var quantity = parseFloat($('#quantityStripped').val());
		
		if ($('#quantity').val()!=''){
			if (quantity < minTrxQuantity){
				$('#quantity').addClass('fieldError');
				$('#errQuantity').html("Can not less than Min Trx Quantity in Security Code " + ($('#securityId').val()));
				return false;
			} else {
				$('#quantity').removeClass('fieldError');
				$('#errQuantity').html("");
//				alert('maturityDate in Quantity = ' +$('#maturityDateHidden').val());
				if ($('#yieldPorto').val()==''){
					if (($('#valuationMethod').val()=='${valuationAmortized}') && ($('#maturityDateHidden').val() != '')) {
						countYield();
					}
				}
//				calculate(true);
				calculate();
			}
		} 
		
		if ($('#accruedInterest').hasClass('fieldError')){
			$('#accruedInterest').removeClass('fieldError');
			$('#accruedInterestError').html('');
		}
	},function(data){
	
	});
}

function getTransactionCode() {
	if ($('#transactionCode').val()) {
		$.post(
				'@{Pick.transactionTemplate()}',
				{
					'code'  : $('#transactionCode').val(),
					'filter' :'USED_BY-1'
				},
				function(data) {
		    		checkRedirect(data);
					if (data) {						
						var transactionTemplateKey = $('#transactionTemplateKey').val();
						$('#transactionCode').removeClass('fieldError');
						$('#transactionTemplateKey').val(data.code);
						$('#transactionCodeDesc').val(data.description);
						$('#h_transactionCodeDesc').val(data.description);
						$('#securityClassId').val(data.securityClassId);
						$('#securityClass').val(data.securityClass);
						$('#h_securityClass').val(data.securityClass);
						$('#securityClassDesc').val(data.securityClassDesc);
						$('#h_securityClassDesc').val(data.securityClassDesc);
						$('#securityType').val(data.securityType);
						$('#h_securityType').val(data.securityType);
						$('#securityTypeDesc').val(data.securityTypeDesc);
						$('#h_securityTypeDesc').val(data.securityTypeDesc);
						$('#priceUnit').val(data.priceUnit);
						$('#discounted').val(data.discounted);
						$('#transactionType').val(data.transactionType);
						$('#defaultCorebanking').val(data.defaultCorebanking);
						$('#isPrice').val(data.isPrice);
						$('#settTransKey').val(data.settlementTransactionKey);
						$('#cashTransCode').val(data.cashTransactionCode);
						$('#cashTransCodeSett').val(data.cashTransactionCodeSettlement);
						$('#prematchRequiredTemp').val(data.defPrematch);
						$('#ctpRequired').val(data.ctpRequired);
						$('#holdingType').val(data.holdingType);
						$('#custodyTransactionCode').val(data.custodyTransactionCode);
						
						if($('#custodyTransactionCode').val() == "RVP" || $('#custodyTransactionCode').val() == "RFOP"){
							$('p[id=pCustomerCashAmount] label').html("Credit Amount");
							$('p[id=pCustodianAmount] label').html("Debet Amount");
						}else{
							$('p[id=pCustomerCashAmount] label').html("Debet Amount");
							$('p[id=pCustodianAmount] label').html("Credit Amount");
						}
						
						if (data.defPrematch) {
							$('#prematchRequired2').removeAttr("checked");
							$('#prematchRequired1').attr('checked', 'checked');
							$('#prematchRequired1').val(true);
							$('#prematchRequiredHidden').val($('#prematchRequired1').val());
							//$("input[name$='prematchRequired']").val('true');
						} else {
							$('#prematchRequired1').removeAttr("checked");
							$('#prematchRequired2').attr('checked', 'checked');
							$('#prematchRequired2').val(false);
							$('#prematchRequiredHidden').val($('#prematchRequired2').val());
							//$("input[name$='prematchRequired']").val('false');
						}

						if (data.priceUnit == '0.01')
							$('#percentage').show();
						else
							$('#percentage').hide();

						clearTaxCode();
						
						if (data.discounted) {
							$('#discountTaxCode').removeAttr('disabled');
							$('#discountTaxHelp').removeAttr('disabled');
							getDefDiscTaxCode();
							$('#pDiscountTaxCode label').html("Tax Code <span class=\"req\">*</span>");
//							$('p[id=pDiscountTaxCode] label span').html(' *');
						} else {
							$('#discountTaxCode').attr('disabled', 'disabled');
							$('#discountTaxHelp').attr('disabled', 'disabled');
							$('#pDiscountTaxCode label').html("Tax Code ");
//							$('p[id=pDiscountTaxCode] label span').html(' ');
						}

						
						// Ex: SHS
						if ((!data.discounted)&&(data.isPrice)){
							//$('#netAmount').attr('disabled','disabled');
							changeIsNominal();
							$('p[id=pNetAmount] label span').html("");
							
							$('#discountTax').attr('disabled','disabled');
							$('p[id=pDiscountTax] label span').html("");
							
							$('#discountTax').val('');
							$('#discountTaxStripped').val('');
							$('#discountNet').val('');
							$('#discountNetStripped').val('');
							$('#discountAmount').val('');
							$('#discountAmountStripped').val('');
						}

						// Ex: DOC
						if ((data.discounted)&&(!data.isPrice)) {
							$('#price').val('');
							$('#priceStripped').val('');
							$('#price').attr('disabled', 'disabled');
							$('p[id=pPrice] label span').html("");
							
							$('#quantity').val('');
							$('#quantityStripped').val('');
							$('#grossAmount').val(0);
							$('#grossAmountStripped').val(0);
							$('#proceed').val(0);
							$('#proceedStripped').val(0);
							$('#netProceed').val(0);
							$('#netProceedStripped').val(0);
							
							$('#discountTax').removeAttr('disabled');
							$('p[id=pDiscountTax] label span').html(" *");
							
							$('#netAmount').val('');
							$('#netAmountStripped').val('');
							$('#netAmount').removeAttr('disabled');
							$('p[id=pNetAmount] label span').html(" *");
						}
						
						// Ex: FIB
						if ((data.discounted)&&(data.isPrice)){
							$('#discountTax').removeAttr('disabled');
							$('p[id=pDiscountTax] label span').html(" *");
							
							//$('#netAmount').attr('disabled','disabled');
							changeIsNominal();
							$('p[id=pNetAmount] label span').html("");
						}
						
						// Ex: CASH
						if ((!data.discounted)&&(!data.isPrice)){
							$('#price').attr('disabled', 'disabled');
							$('p[id=pPrice] label span').html("");
							
							//$('#netAmount').attr('disabled','disabled');
							changeIsNominal();
							$('p[id=pNetAmount] label span').html("");
							
							$('#discountTax').val('');
							$('#discountTaxStripped').val('');
							$('#discountTax').attr('disabled','disabled');
							$('p[id=pDiscountTax] label span').html("");
							
							$('#discountNet').val('');
							$('#discountNetStripped').val('');
							$('#discountAmount').val('');
							$('#discountAmountStripped').val('');
						}
						
						if (data.hasInterest) {
							$('#hasInterest').val(true);
							$('#interestRate').removeAttr('disabled');
							$('p[id=pInterestRate] label span').html(" *");
							
							$('#accruedDays').attr('disabled', 'disabled');
							$('p[id=pAccruedDays] label span').html("");
							
							$('#accruedInterest').removeAttr('disabled');
							$('#accruedInterest').val('');
							$('#accruedInterest').removeClass('fieldError');
							$('#accruedInterestError').html('');
							$('#accruedInterestStripped').val('');
							$('p[id=pAccruedInterest] label span').html(" *");
							
							$('#taxOnInterestCode').removeAttr('disabled');
							$('#taxOnInterestHelp').removeAttr('disabled');
							//$('p[id=pTaxOnInterestCode] label span').html(" *");
							
							$('#taxOnInterest').removeAttr('disabled');
							$('p[id=pTaxOnInterest] label span').html(" *");
							
							if (data.holdingType == '${holdingTypeTotal}'){
								$('#cekBoxWaiveAccInt').removeAttr('disabled');
							} else {
								$('#cekBoxWaiveAccInt').attr('disabled', 'disabled');
								$('#cekBoxWaiveAccInt').attr('checked', false);
								$('#cekBoxWaiveAccInt').change();
							}
							
						} else {
							$('#hasInterest').val(false);
							$('#interestRate').attr('disabled', 'disabled');
							$('p[id=pInterestRate] label span').html("");
							
							$('#accruedDays').attr('disabled', 'disabled');
							$('p[id=pAccruedDays] label span').html("");
							
							$('#cekBoxWaiveAccInt').attr('disabled', 'disabled');
							$('#cekBoxWaiveAccInt').attr('checked', false);
							$('#cekBoxWaiveAccInt').change();
							
							$('#waveAccruedInterest').val('');
							$('#accruedInterest').val('');
							$('#accruedInterestStripped').val('');
							$('#accruedInterest').removeClass('fieldError');
							$('#accruedInterestError').html('');
							$('#accruedInterest').attr('disabled', 'disabled');
							$('p[id=pAccruedInterest] label span').html("");
							
							$('#taxOnInterestCode').attr('disabled', 'disabled');
							$('#taxOnInterestHelp').attr('disabled', 'disabled');
							//$('p[id=pTaxOnInterestCode] label span').html("");
							
							$('#taxOnInterest').attr('disabled', 'disabled');
							$('p[id=pTaxOnInterest] label span').html("");
						}

						//dikomen karena redmine 2933, skarang bs input by nominal
						/*if (!data.isPrice) {
							$('#price').val(1);
							$('#priceStripped').val(1);
							$('#price').attr('disabled', 'disabled');
							$('p[id=pPrice] label span').html("");
						} else {
							$('#price').val('');
							$('#priceStripped').val('');
							$('#price').removeAttr('disabled');
							$('p[id=pPrice] label span').html(" *");
						}*/

						// Check transactionType
						$('#holdingRefs').removeClass('fieldError');
						if (data.transactionType == '${transTypeB}') {
							$('#holdingRefs').attr('disabled', 'disabled');
							$('#holdingHelp').attr('disabled', 'disabled');
							$('p[id=pHoldingRef] label span').html("");
						} else if (data.transactionType == '${transTypeS}') {
							$('#holdingRefs').removeAttr('disabled');
							$('#holdingHelp').removeAttr('disabled');
							$('p[id=pHoldingRef] label span').html(" *");
						}

						$('#effectiveDate').val('');

						// Check securityClass
						if (data.securityClass == '${secClassEQ}') {
							$('#interestRate').val('');
							$('#interestRateStripped').val('');
							$('#accruedDays').val('');
							$('#accruedDaysStripped').val('');
							$('#accruedInterest').val('');
							$('#accruedInterestStripped').val('');
							$('#taxOnInterestCode').val('');
							$('#taxOnInterestDesc').val('');
							$('#taxOnInterest').val('');
							$('#taxOnInterestStripped').val('');
							$('#taxOnInterestNet').val('');

							
							$('#lastNextDate').hide();
							$('#effMatDate').show();
							$('#maturityDate').attr('disabled', 'disabled');
							$('p[id=pMaturityDate] label span').html("");
							$('#maturityDate').val('');				
						} else if (data.securityClass == '${secClassFI}') {
							$('#lastNextDate').show();
							$('#effMatDate').hide();
							$('#maturityDate').attr('disabled', 'disabled');
							$('p[id=pMaturityDate] label span').html("");
							$('#maturityDate').val('');
							$('#interestRate').attr('disabled', 'disabled');
						} else if (data.securityClass == '${secClassMM}') {
							$('#lastNextDate').hide();
							$('#effMatDate').show();
							if (data.transactionType == '${transTypeB}'){
								$('#maturityDate').removeAttr('disabled');
								$('p[id=pMaturityDate] label span').html(" *");
								$('#maturityDate').val('');
							} else {
								$('#maturityDate').attr('disabled', 'disabled');
								$('p[id=pMaturityDate] label span').html("");
								$('#maturityDate').val('');
							}
						} else {
							messageAlertOk("Transaction is not currently supported", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
							//alert('Transaction is not currently supported');
						}

						var vTransactionTemplateKey = $('#transactionTemplateKey').val();
						$.get("@{Transactions.getCashAccountAmountType()}?transactionTemplateKey="+vTransactionTemplateKey, function(data) {
							checkRedirect(data);
							var amountType = data.amounttype;
							if (amountType == '${bnTransTypeDebet}') {
								$('p[id=pCustomerCashAmount] label').html("Debet Amount");
								$('p[id=pCustodianAmount] label').html("Credit Amount");
							} else if (amountType == '${bnTransTypeCredit}') {
								$('p[id=pCustomerCashAmount] label').html("Credit Amount");
								$('p[id=pCustodianAmount] label').html("Debet Amount");
							}
						});

						$('#securityId').val('');
						$('#securityKey').val('');
						$('#securityDesc').val('');
						$('#h_securityDesc').val('');
						$('#settlementDate').val('');
						$('#holdingRefs').removeClass('fieldError');
						$('#holdingRefs').val('');
						$('#h_holdingRefs').val('');
						$('#holdingQuantity').val('');
						$('#h_holdingQuantity').val('');
						if (transactionTemplateKey != data.code) {
//							disableCharge();
						}
						
						$('#yield').val("");
						$('#yieldStripped').val("");
						attachSecuritiesPaging();
					} else {
						$('#transactionCode').addClass('fieldError');
						$('#transactionCode').val('');
						$('#transactionTemplateKey').val('');
						$('#transactionCodeDesc').val('NOT FOUND');
						$('#h_transactionCodeDesc').val('');
						$('#securityClass').val('');
						$('#h_securityClass').val('');
						$('#securityClassDesc').val('');
						$('#h_securityClassDesc').val('');
						$('#securityType').val('');
						$('#h_securityType').val('');
						$('#securityTypeDesc').val('');
						$('#h_securityTypeDesc').val('');
						$('#securityClassId').val('');
						$('#holdingRefs').removeClass('fieldError');
						$('#holdingRefs').val('');
						$('#h_holdingRefs').val('');
						$('#holdingQuantity').val('');
						$('#h_holdingQuantity').val('');

						$('#prematchRequired1').removeAttr("checked");
						$('#prematchRequired2').attr('checked', 'checked');
						$('#prematchRequired').val('');
						attachSecuritiesPaging();
					}
				});
		
	} else {
		
	}
}  // end --- function getTransactionCode()

function getSecurity() {
	if ($('#securityId').val()) {
		$.post(
			'@{Pick.security()}',
			{	
				'code' : $('#securityId').val(),
				'filter' : $('#securityType').val(),
				'transactionDate' : $('#transactionDate').val()
			},
			function(data) {
	    		checkRedirect(data);
				if (data) {
					var securityKey = $('#securityKey').val(); 
					$('#securityId').removeClass('fieldError');
					$('#securityKey').val(data.securityKey);
					$('#securityDesc').val(data.description);
					$('#h_securityDesc').val(data.description);
					$('#depositoryCode').val(data.depositoryCode);
					$('#interestRate').autoNumericSet(data.interestRate);
					$('#interestRateStripped').val(data.interestRate);
					$('#isFraction').val(data.isFraction);
					$('#fractionRatioSource').val(data.fractionRatioSource);
					$('#fractionRatioTarget').val(data.fractionRatioTarget);
					$('#minTrxQuantity').val(data.minTrxQuantity);
					$('#expiredDate').val(data.expiredDate);
					$('#isExpiredDate').val(data.isExpiredDate);
					$('#parPrice').val(data.parValue);
					$('#currencyCode').val(data.securityCurrency);
					$('#currTrans').val(data.securityCurrency);
					$('#currencyCodeHid').val(data.securityCurrency);
					$('#marketOfRisk').val(data.marketOfRisk);
					$('#isScriptHidden').val(data.isScript);
					if (data.isScript === true) {
						$('#isScript2').removeAttr("checked");
						$('#isScript1').attr('checked', 'checked');
					} else {
						$('#isScript1').removeAttr('checked');
						$('#isScript2').attr('checked', 'checked');
					}
						
					
					if (data.lastPaymentDate) {
						$('#lastPaymentDate').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.lastPaymentDate)));
						$('#lastPaymentDateHidden').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.lastPaymentDate)));
					} else {
						$('#lastPaymentDate').val('');
						$('#lastPaymentDateHidden').val('');
					}
					
					if (data.firstCouponDate) {
						$('#firstCouponDate').val(data.firstCouponDate);
					}else {
						$('#firstCouponDate').val('');
					}
					
					if (data.issueDate) {
						$('#issueDate').val(data.issueDate);
					}else {
						$('#issueDate').val('');
					}
					
					if (data.frequency) {
						$('#frequency').val(data.frequency.replace("FREQUENCY-", ""));
					}
					if (data.accrualBase)
						$('#accrualBase').val(data.accrualBase.replace("ACCRUAL_BASE-", ""));
					if (data.yearBase)
						$('#yearBase').val(data.yearBase.replace("YEAR_BASE-",""));
					
					var settlementDate = $('#settlementDate').datepicker('getDate');
					var date = $('#transactionDate').datepicker('getDate');
					if (date) {
						if (!settlementDate) {
							date.setDate(date.getDate()	+ data.settlementDays);
							$('#settlementDate').datepicker('setDate', date);
						}
						//$('#settlementDate').val($.datepicker.formatDate('mm/dd/yy', new Date(date)));
					}
					
					if ($('#securityClass').val() == '${secClassEQ}') {
						if (data.maturityDate) {
							$('#maturityDate').datepicker('setDate', new Date(data.maturityDate));
							$('#maturityDateHidden').datepicker('setDate', new Date(data.maturityDate));
						}
					} else if ($('#securityClass').val() == '${secClassFI}') {
						if (data.nextPaymentDate) {
							$('#nextPaymentDate').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.nextPaymentDate)));
							$('#nextPaymentDateHidden').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.nextPaymentDate)));
							
						} else {
							$('#nextPaymentDate').val('');
							$('#nextPaymentDateHidden').val('');
						}
						if (data.maturityDate) {
							$('#maturityDate').datepicker('setDate', new Date(data.maturityDate));
							$('#maturityDateHidden').datepicker('setDate', new Date(data.maturityDate));
						}
					} else if ($('#securityClass').val() == '${secClassMM}') {
						//#3024
						//$('#maturityDate').val('');
						if (data.maturityDate) {
							if (data.maturityDate != null) {
								$('#maturityDate').datepicker('setDate', new Date(data.maturityDate));
								$('#maturityDateHidden').datepicker('setDate', new Date(data.maturityDate));
							}
						}
						
						$('#nextPaymentDate').val('');
						$('#nextPaymentDateHidden').val('');
					}
					if (securityKey != data.securityKey) {
//						disableCharge();
					}
					
					
					
					// refs #1375 for classification dropdown ===================================================================
					//$('#classification option').remove();
					$('#classification').attr('disabled', false);
//					console.log("ValuationMethodAfs = " +data.valuationMethodAFS);
//					console.log("ValuationMethodTrade = " +data.valuationMethodTrade);
//					console.log("ValuationMethodHTm = " +data.valuationMethodHTM);
					if ((data.valuationMethodAFS)&&(!data.valuationMethodTrade)&&(!data.valuationMethodHTM)) {
						$('#viewClassification').val('');
						$('#viewClassification').val('${classificationAfs}');
						$('#classificationId').val('${classificationAfs}');
						$('#classification').val('${classAfs.lookupCode}');
						$('#classificationDesc').val('${classAfs.lookupDescription}');
						$('#valuationMethod').val(data.valuationMethodAFS); //4145
						$('#amortizationMethod').val(data.amortizationMethodAFS);//4145
					}
					
					if ((data.valuationMethodTrade)&&(!data.valuationMethodAFS)&&(!data.valuationMethodHTM)) {
						$('#viewClassification').val('');
						$('#viewClassification').val('${classificationTrd}');
						$('#classificationId').val('${classificationTrd}');
						$('#classification').val('${classTrd.lookupCode}');
						$('#classificationDesc').val('${classTrd.lookupDescription}');
						$('#valuationMethod').val(data.valuationMethodTrade); //4145
						$('#amortizationMethod').val(data.amortizationMethodTrade);//4145
					}
					
					if ((data.valuationMethodHTM)&&(!data.valuationMethodAFS)&&(!data.valuationMethodTrade)) {
						$('#viewClassification').val('');
						$('#viewClassification').val('${classificationHtm}');
						$('#classificationId').val('${classificationHtm}');
						$('#classification').val('${classHtm.lookupCode}');
						$('#classificationDesc').val('${classHtm.lookupDescription}');
						$('#valuationMethod').val(data.valuationMethodHTM); //4145
						$('#amortizationMethod').val(data.amortizationMethodHTM);//4145
					}
						
					if ((data.valuationMethodTrade) && (data.valuationMethodAFS)&&(!data.valuationMethodHTM)) {
						$('#viewClassification').val('');
						$('#viewClassification').val('${classificationAfs}'+'${classificationTrd}');
						if ('${classTrd.lookupSequence}' < '${classAfs.lookupSequence}'){
							$('#classificationId').val('${classificationTrd}');
							$('#classification').val('${classTrd.lookupCode}');
							$('#classificationDesc').val('${classTrd.lookupDescription}');
						} else {
							$('#classificationId').val('${classificationAfs}');
							$('#classification').val('${classAfs.lookupCode}');
							$('#classificationDesc').val('${classAfs.lookupDescription}');
						}
					}
					
					if ((data.valuationMethodAFS) && (data.valuationMethodHTM)&&(!data.valuationMethodTrade)) {
						$('#viewClassification').val('');
						$('#viewClassification').val('${classificationAfs}'+'${classificationHtm}');
						if ('${classAfs.lookupSequence}' < '${classHtm.lookupSequence}'){
							$('#classificationId').val('${classificationAfs}');
							$('#classification').val('${classAfs.lookupCode}');
							$('#classificationDesc').val('${classAfs.lookupDescription}');
						} else {
							$('#classificationId').val('${classificationHtm}');
							$('#classification').val('${classHtm.lookupCode}');
							$('#classificationDesc').val('${classHtm.lookupDescription}');
						}
					}
					
					if ((data.valuationMethodTrade) && (data.valuationMethodHTM)&&(!data.valuationMethodAFS)) {
						$('#viewClassification').val('');
						$('#viewClassification').val('${classificationTrd}'+'${classificationHtm}');
						if ('${classTrd.lookupSequence}' < '${classHtm.lookupSequence}'){
							$('#classificationId').val('${classificationTrd}');
							$('#classification').val('${classTrd.lookupCode}');
							$('#classificationDesc').val('${classTrd.lookupDescription}');
						} else {
							$('#classificationId').val('${classificationHtm}');
							$('#classification').val('${classHtm.lookupCode}');
							$('#classificationDesc').val('${classHtm.lookupDescription}');
						}
					} 
					
					if ((data.valuationMethodTrade) && (data.valuationMethodAFS) && (data.valuationMethodHTM)) {
						$('#viewClassification').val('');
						$('#viewClassification').val('${classificationAfs}'+'${classificationTrd}'+'${classificationHtm}');
						if (('${classTrd.lookupSequence}' < '${classAfs.lookupSequence}') && 
							('${classTrd.lookupSequence}' < '${classHtm.lookupSequence}')){
							$('#classificationId').val('${classificationTrd}');
							$('#classification').val('${classTrd.lookupCode}');
							$('#classificationDesc').val('${classTrd.lookupDescription}');
						}
						
						if (('${classAfs.lookupSequence}' < '${classTrd.lookupSequence}') && 
							('${classAfs.lookupSequence}' < '${classHtm.lookupSequence}')){
							$('#classificationId').val('${classificationAfs}');
							$('#classification').val('${classAfs.lookupCode}');
							$('#classificationDesc').val('${classAfs.lookupDescription}');
						}
						
						if (('${classHtm.lookupSequence}' < '${classTrd.lookupSequence}') && 
							('${classHtm.lookupSequence}' < '${classAfs.lookupSequence}')){
							$('#classificationId').val('${classificationHtm}');
							$('#classification').val('${classHtm.lookupCode}');
							$('#classificationDesc').val('${classHtm.lookupDescription}');
						}
					}
					
					if (!(data.valuationMethodTrade) && !(data.valuationMethodAFS) && !(data.valuationMethodHTM)) {
						$('#classification').attr('disabled', 'disabled');
					}
					
					if (($('#ctpRequired').val()=='true') && ($('#depositoryCode').val() == '${depositoryCbest}' || $('#depositoryCode').val() == '${depositoryBis}') && ($('#prematchRequiredTemp').val() == 'false')) {
						$('#ctpNo').removeAttr('disabled');
						$('p[id="pCtpRef"] label span').html(" *");
						if (('${confirming}'=='true')||('${mode}'=='view')){
							if ($('#ctpNo').val() != '') {
								//$('#ctpNo').autoNumericSet($('#ctpNo').val());
								$('#ctpNoHidden').val($('#ctpNo').val());
							}
							$('#ctpNo').attr('disabled', 'disabled');
							$('p[id="pCtpRef"] label span').html("");
						}
					} else {
						$('#ctpNo').attr('disabled', 'disabled');
						$('p[id="pCtpRef"] label span').html("");
					}
					// ==========================================================================================================
					
					$("#valuationTrade").val(data.valuationMethodTrade);
					$("#valuationAFS").val(data.valuationMethodAFS);
					$("#valuationHTM").val(data.valuationMethodHTM);
					
					$("#amortizationTrade").val(data.amortizationMethodTrade);
					$("#amortizationAFS").val(data.amortizationMethodAFS);
					$("#amortizationHTM").val(data.amortizationMethodHTM);
					
					calculateAccruedDays();
					//calculate();

					$('#holdingRefs').removeClass('fieldError');
//					$('#counterPartyCode').focus();
					$('#holdingRefs').val('');
					$('#h_holdingRefs').val('');
					$('#holdingQuantity').val('');
					$('#h_holdingQuantity').val('');
					$('#yield').val("");
					$('#yieldStripped').val("");

				} else {
					$('#securityId').addClass('fieldError');
					$('#securityId').val('');
					$('#securityDesc').val('NOT FOUND');
					$('#securityKey').val('');
					$('#marketOfRisk').val('');
					
					$('#lastPaymentDate').val('');
					$('#lastPaymentDateHidden').val('');
					$('#nextPaymentDate').val('');
					$('#nextPaymentDateHidden').val('');
				}
			});
	} else {
		//console.debug('getSecurity() not running, #securityId is empty');
	}
}

function getCharges() {
	$.post('@{charges}', 
		{ 
			'custodyAccountKey':$('#accountNoKey').val(),
			'securityClass':$('#securityClassId').val(),
			'securityType':$('#securityType').val(),
			'transactionTemplateKey':$('#transactionTemplateKey').val(),
			'securityKey':$('#securityKey').val(),
			'quantity':$('#quantityStripped').val(),
			'nominal':$('#netAmountStripped').val()
		}, 
		function(response, status, xhr) {
    		checkRedirect(response);
			if (status == 'error') {
				// Error handling here
			} else {
				var charges = response;
				var chargeValueTam = 0;
				var taxAmountAfterTrunc = 0;
				chargeGrid = $('#chargeList').dataTable({
					aaData:response,
					aoColumnDefs: [{bSortable:false,aTargets:[1,2,3,4,5,6]}],
					aoColumns:
						[
						 	{ 
						 		asSorting:["asc"], mDataProp: "chargeMaster.chargeCode"
						 	},
						 	{ 
						 		fnRender: function(obj) {
						 			var chargeValue = 0;
						 			chargeValueTam = 0;
						 			if (!isNaN(obj.aData.chargeValue)) {
						 				chargeValueTam = obj.aData.chargeValue;
						 				chargeValue = $('#dummy').autoNumericSet(obj.aData.chargeValue).val();
						 			}
						 				
						 			var controls;
//						 			if (obj.aData.chargeMaster.chargeType.lookupId == 'CHARGE_TYPE-C') {
//						 				controls = '<input type="text" id="chargeValue[' + obj.iDataRow + ']" value="' + chargeValue + '" class="numeric" size="15" disabled="disabled" />';
//						 				controls += '<input type="hidden" id="hiddenChargeValue[' + obj.iDataRow + ']" value="' + chargeValue + '" class="numeric" size="15" />';
//						 				controls += '<input type="hidden" id="chargeValueStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeValue" value="' + obj.aData.chargeValue + '" />';
//						 				controls += '<input type="hidden" id="hiddenChargeValueStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeValue" value="' + obj.aData.chargeValue + '" />';
//						 			} else {
						 				controls = '<input type="text" id="chargeValue[' + obj.iDataRow + ']" value="' + chargeValue + '" class="rgNumeric mask" size="15"/>';
						 				controls += '<input type="hidden" id="hiddenChargeValue[' + obj.iDataRow + ']" value="' + chargeValue + '" class="rgNumeric mask" size="15" />';
						 				controls += '<input type="hidden" id="chargeValueStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeValue" value="' + obj.aData.chargeValue + '" />';
						 				controls += '<input type="hidden" id="hiddenChargeValueStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeValue" value="' + obj.aData.chargeValue + '" />';
//						 			}
						 			return controls;
						 		} 
						 	},
						 	{ 
						 		fnRender: function(obj) {
						 			var taxAmount = 0;
						 			taxAmountAfterTrunc = 0;
						 			if (obj.aData.taxAmount && !isNaN(obj.aData.taxAmount)) {
						 				taxAmountAfterTrunc = Math.ceil(obj.aData.taxAmount);
						 				taxAmount = $('#dummy').autoNumericSet(taxAmountAfterTrunc, {vMin:'-999999999999.'}).val();
						 			}
						 			var controls;
//						 			if (obj.aData.chargeMaster.chargeType.lookupId == 'CHARGE_TYPE-C') {
//						 				controls = '<input type="text" id="taxAmount[' + obj.iDataRow + ']" value="' + taxAmount + '" class="numeric" size="15" disabled="disabled" />';
//						 				controls += '<input type="hidden" id="hiddenTaxAmount[' + obj.iDataRow + ']" value="' + taxAmount + '" class="numeric" size="15" />';
//						 				controls += '<input type="hidden" id="taxAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].taxAmount" value="' + obj.aData.taxAmount + '" />';
//						 				controls += '<input type="hidden" id="hiddenTaxAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].taxAmount" value="' + obj.aData.taxAmount + '" />';
//						 			} else {
						 				controls = '<input type="text" id="taxAmount[' + obj.iDataRow + ']" value="' + taxAmount + '" class="rgNumeric mask" size="15" />';
						 				controls += '<input type="hidden" id="hiddenTaxAmount[' + obj.iDataRow + ']" value="' + taxAmount + '" class="rgNumeric mask" size="15" />';
						 				controls += '<input type="hidden" id="taxAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].taxAmount" value="' + taxAmountAfterTrunc + '" />';
						 				controls += '<input type="hidden" id="hiddenTaxAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].taxAmount" value="' + taxAmountAfterTrunc + '" />';
//						 			}
						 			return controls;
						 		}
						 	},
						 	{ 
						 		fnRender: function(obj) {
						 			var chargeNetAmt = 0;
						 			var chargeNetAmtTam = 0;
						 			if (obj.aData.chargeNetAmount && !isNaN(obj.aData.chargeNetAmount)) {
						 				chargeNetAmtTam = Number(chargeValueTam) + Number(taxAmountAfterTrunc);
						 				chargeNetAmt = $('#dummy').autoNumericSet(chargeNetAmtTam).val();
						 			}
						 			var controls;
						 			controls = '<input type="text" id="chargeNetAmount[' + obj.iDataRow + ']" value="' +chargeNetAmt+ '" size="15" class="rgNumeric mask" disabled="disabled"/>';
						 			controls += '<input type="hidden" id="hiddenChargeNetAmount[' + obj.iDataRow + ']" value="' + chargeNetAmt + '" class="rgNumeric mask" size="15" />';
					 				controls += '<input type="hidden" id="chargeNetAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeNetAmount" value="' + chargeNetAmtTam + '" />';
					 				controls += '<input type="hidden" id="hiddenChargeNetAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeNetAmount" value="' + chargeNetAmtTam + '" />';
						 			return controls;
						 		}
						 	},
						 	{ mDataProp: "chargeFrequency" },
						 	{ 
						 		fnRender: function(obj) {
						 			var controls;
						 			if (obj.aData.chargeFrequency == '${chargeFreqT}') {
						 				//if ($('#classificationId').val()=='${classificationTrd}'){
						 					controls = '<input type="checkbox" name="charges[' + obj.iDataRow + '].chargeCapitalized" value="true" />';
						 				/*} else {
						 					controls = '<input type="checkbox" name="charges[' + obj.iDataRow + '].chargeCapitalized" value="true" checked="checked" />';
						 				}*/
						 			} else {
						 				controls = '<input type="checkbox" name="charges[' + obj.iDataRow + '].chargeCapitalized" value="true" disabled="disabled" />';
						 			}
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].chargeCapitalized" value="false" />';
						 			return controls;
						 		}
						 	},
						 	{
						 		fnRender: function(obj) {
						 			var controls;
						 			controls = '<input type="checkbox" name="charges[' + obj.iDataRow + '].chargeWaived" value="true" ' + (obj.aData.chargeWaived ? 'checked="checked"' : '') + ' />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].chargeMaster.chargeKey" value="' + obj.aData.chargeMaster.chargeKey + '" />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].chargeMaster.chargeCode" value="' + obj.aData.chargeMaster.chargeCode + '" />';
						 			controls += '<input type="hidden" id="chargeType[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeMaster.chargeType.lookupId" value="' + obj.aData.chargeMaster.chargeType.lookupId + '" />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].taxMaster.taxKey" value="' + (obj.aData.taxMaster ? obj.aData.taxMaster.taxKey : '') + '" />';
						 			controls += '<input type="hidden" id="taxRate[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].taxMaster.taxRate" value="' + (obj.aData.taxMaster ? obj.aData.taxMaster.taxRate : '') + '" />';
						 			controls += '<input type="hidden" id="freq[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeFrequency" value="' + obj.aData.chargeFrequency + '" />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].id.transactionKey" value="' + (obj.aData.id ? obj.aData.id.transactionKey : '') + '" />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].id.rowNumber" value="' + (obj.aData.id ? obj.aData.id.rowNumber : '') + '" />';
						 			return controls;
						 		}
						 	}
						],
					bAutoWidth:false,
					bDestroy:true,
					bFilter:false,
					bInfo:false,
					bJQueryUI:true,
					bPaginate: false			
				});	
				$('#addCharge').button({disabled: false});
				$('#chargeList tbody .mask').autoNumeric({vMin:'-999999999999.'});
				reloadCharge = false;
				calculate();
			}
		});
	
	$('#errGlobal').html("");
}



function calculateAccruedDays() {
	// Accrual Days
	var securityClass = $('#securityClass').val();
	var transactionType = $('#transactionType').val();
	var settlementDate = $('#settlementDate').datepicker('getDate');
	
	// securityClass == EQ, skip!
	if (securityClass == '${secClassEQ}') return;
		
	$('#effectiveDate').datepicker('setDate', settlementDate);
	$('#effectiveDateHidden').datepicker('setDate', settlementDate);


	if ((securityClass == '${secClassMM}') && (transactionType == '${transTypeB}')) {	
		//settlementDate.setDate(settlementDate.getDate()-1);
		$('#lastPaymentDateHidden').datepicker('setDate', settlementDate);
	}

	// Setup transaction
	var transaction = new Transaction();
	var lastPaymentDate = $('#lastPaymentDateHidden').datepicker('getDate');
	var accrualBase = transaction.decodeCalculationBase($('#accrualBase').val());
	var accruedDays = 0;
	
	if ($('#securityClass').val()!='${secClassMM}'){
		$.post('@{Transactions.getCouponDatesOnly()}', {'securityKey':$('#securityKey').val(),'settlementDate':$('#settlementDate').val()}, function(data) {
    		checkRedirect(data);
			if (data==null){
				lastPaymentDate = '';
			}
		});
	}
	
	if (lastPaymentDate!=null && settlementDate!= null){

		//jika has_interest = 1 maka anggap dia FI. Jadi panggil calculate accrued days porto
		if ($('#hasInterest').val() == 'true' && $('#firstCouponDate').val() != null) {
			var firstCoupon = $('#firstCouponDate').val().split("/");
			var firstCouponDD = firstCoupon[0];
			accruedDays = transaction.calculateAccruedDaysPorto(lastPaymentDate, settlementDate, firstCouponDD, accrualBase);
		}else{
			accruedDays = transaction.calculateAccruedDays(lastPaymentDate, settlementDate, accrualBase);
		}
	}
	
	$('#accruedDays').val(accruedDays);
	$('#accruedDaysStripped').val(accruedDays);
	
}

function calculateAccruedInterest() {
	// securityClass == EQ, skip!
	if ($('#securityClass').val() == '${secClassEQ}')
		return;

	var transaction = new Transaction();
	transaction.lastPaymentDate = $('#lastPaymentDateHidden').datepicker('getDate');
	transaction.nextPaymentDate = $.datepicker.parseDate('${appProp.jqueryDateFormat}', $('#nextPaymentDateHidden').val());
	transaction.firstCouponDate = $.datepicker.parseDate('${appProp.jqueryDateFormat}', $('#firstCouponDate').val());
	transaction.issueDate = $.datepicker.parseDate('${appProp.jqueryDateFormat}', $('#issueDate').val());
	
	if ($('#securityClass').val()!='${secClassMM}'){
		$.post('@{Transactions.getCouponDatesOnly()}', {'securityKey':$('#securityKey').val(),'settlementDate':$('#settlementDate').val()}, function(data) {
    		checkRedirect(data);
			if (data==null){
				transaction.lastPaymentDate = '';
				transaction.nextPaymentDate = '';
			}
		});
	}
	
	transaction.settlementDate = $('#settlementDate').datepicker('getDate');
	transaction.interestRate = $('#interestRateStripped').val();
	transaction.quantity  = $('#quantityStripped').val();
	transaction.accruedInterest  = $('#accruedInterestStripped').val();
	transaction.frequency = transaction.decodeFrequency($('#frequency').val());
	transaction.accrualBase = transaction.decodeCalculationBase($('#accrualBase').val());
	transaction.yearBase = transaction.decodeCalculationBase($('#yearBase').val());
	transaction.isFraction = $('#isFraction').val();
	transaction.fractionRatioSource = $('#fractionRatioSource').val();
	transaction.fractionRatioTarget = $('#fractionRatioTarget').val();
	
	var accruedInterest = transaction.calculateAccruedInterest();
	$('#accruedDays').autoNumericSet(transaction.accruedDays);
	$('#accruedDaysStripped').val(transaction.accruedDays);
	if (!$('#cekBoxWaiveAccInt').is(":checked")){
		$('#accruedInterest').autoNumericSet(accruedInterest);
		$('#accruedInterestStripped').val(accruedInterest);
		$('#waveAccruedInterest').val(accruedInterest);
	}
	if ($('#accruedInterestStripped').val()!='0'){
//		$('#waveAccruedInterest').val(accruedInterest);
	}
	
	if ($('#hasInterest').val()=='false'){
		$('#accruedDays').autoNumericSet(0);
		$('#accruedDaysStripped').val(0);
		$('#accruedInterest').autoNumericSet(0);
		$('#accruedInterestStripped').val(0);
	}
}

function getAccInterestDef(){
	if ($('#securityClass').val() == '${secClassEQ}')
		return;

	var transaction = new Transaction();
	transaction.lastPaymentDate = $('#lastPaymentDateHidden').datepicker('getDate');
	transaction.nextPaymentDate = $.datepicker.parseDate('${appProp.jqueryDateFormat}', $('#nextPaymentDateHidden').val());
	transaction.firstCouponDate = $.datepicker.parseDate('${appProp.jqueryDateFormat}', $('#firstCouponDate').val());

	if ($('#securityClass').val()!='${secClassMM}'){
		$.post('@{Transactions.getCouponDatesOnly()}', {'securityKey':$('#securityKey').val(),'settlementDate':$('#settlementDate').val()}, function(data) {
    		checkRedirect(data);
			if (data==null){
				transaction.lastPaymentDate = '';
				transaction.nextPaymentDate = '';
			}
		});
	}
	
	transaction.settlementDate = $('#settlementDate').datepicker('getDate');
	transaction.interestRate = $('#interestRateStripped').val();
	transaction.quantity  = $('#quantityStripped').val();
	transaction.accruedInterest  = $('#accruedInterestStripped').val();
	transaction.frequency = transaction.decodeFrequency($('#frequency').val());
	transaction.accrualBase = transaction.decodeCalculationBase($('#accrualBase').val());
	transaction.yearBase = transaction.decodeCalculationBase($('#yearBase').val());
	transaction.isFraction = $('#isFraction').val();
	transaction.fractionRatioSource = $('#fractionRatioSource').val();
	transaction.fractionRatioTarget = $('#fractionRatioTarget').val();
	
	var accruedInterest = transaction.calculateAccruedInterest();
	
	var accInt = parseFloat(accruedInterest);
	accInt = Math.round(accInt * 10000) / 10000;
	return accInt;
}

function calculate(resetAccruedInterest) {
	var transaction = new Transaction();
	var isNom = false;
	
	if($("#isInpNominal").val()=='true'){
		isNom=true;
	}
	
	transaction.isInputNominal = isNom;
	transaction.transactionType = $('#transactionType').val();
	transaction.securityClass = $('#securityClass').val();
	transaction.securityType = $('#securityType').val();
	transaction.frequency = transaction.decodeFrequency($('#frequency').val());
	transaction.accrualBase = transaction.decodeCalculationBase($('#accrualBase').val());
	transaction.yearBase = transaction.decodeCalculationBase($('#yearBase').val());

	transaction.lastPaymentDate = $('#lastPaymentDateHidden').datepicker('getDate');
	transaction.nextPaymentDate = $.datepicker.parseDate('${appProp.jqueryDateFormat}', $('#nextPaymentDateHidden').val());
	transaction.settlementDate = $('#settlementDate').datepicker('getDate');
	transaction.firstCouponDate = $.datepicker.parseDate('${appProp.jqueryDateFormat}', $('#firstCouponDate').val());
	
	if ($('#securityClass').val()!='${secClassMM}'){
		$.post('@{Transactions.getCouponDatesOnly()}', {'securityKey':$('#securityKey').val(),'settlementDate':$('#settlementDate').val()}, function(data) {
    		checkRedirect(data);
			if (data==null){
				transaction.lastPaymentDate = null;
				transaction.nextPaymentDate = null;
			}
		});
	}
	
	transaction.hasInterest = $('#hasInterest').val();
	transaction.isDiscounted = $('#discounted').val();
	
	transaction.isFraction = $('#isFraction').val();

	transaction.fractionRatioSource = $('#fractionRatioSource').val();
	transaction.fractionRatioTarget = $('#fractionRatioTarget').val();
	

	if (transaction.hasInterest == 'true') {
		if (transaction.nextPaymentDate != null){
			if (transaction.settlementDate > transaction.nextPaymentDate) {
				transaction.settlementDate = transaction.nextPaymentDate;
			}
		}
	}
	
	transaction.quantity = $('#quantityStripped').val();
	transaction.price = $('#priceStripped').val();
	transaction.priceUnit = $('#priceUnit').val();
	transaction.parPrice = $('#parPrice').val();
	transaction.discountAmount = $('#discountAmountStripped').val();
	transaction.discountTax = $('#discountTaxStripped').val();
	transaction.isPrice = $('#isPrice').val();
	transaction.netAmount = $('#netAmountStripped').val();
	
	// Charges
	var totalCharges = 0;
	var sumOfChargeNetAmount = 0;
	var sumOfTax = 0;
	var nodes = chargeGrid.fnGetNodes();
//	var nodes =  $('#chargeList').dataTable().fnGetNodes();
	for ( var i = 0; i < nodes.length; i++) {
		var node = $(nodes[i]);
		var chargeValue = node.find('[name$="chargeValue"]').val();
		var chargeTax = node.find('[name$="taxAmount"]').val();
		var chargeNet = node.find('#chargeNetAmount');
		var chargeFrequency = node.find('[name$="chargeFrequency"]').val();
//		console.debug('CHARGE VALUE = ' +chargeValue);
//		console.debug('CHARGE TAX = ' +chargeTax);
		var chargeNetAmount = 0;
		var totalChargeValue = 0;
		var totalTaxCharges = 0;
		
		if (!isNaN(chargeValue)) {
			totalChargeValue += Number(chargeValue);
			chargeNetAmount += Number(chargeValue); 
		}
		if (!isNaN(chargeTax)) {
			chargeNetAmount += Number(chargeTax);
			totalTaxCharges += Number(chargeTax);
		}
//		console.debug('CHARGE NET AMOUNT = ' +chargeNetAmount);
//		console.debug('Total Charges Tax = ' +)
	
		$(chargeNet).autoNumericSet(chargeNetAmount, {vMin:'-999999999999.'});
		
		if (chargeFrequency == '${chargeFreqT}') {
			//totalCharges += chargeNetAmount;
			totalCharges += totalChargeValue;
			sumOfChargeNetAmount += chargeNetAmount;
			sumOfTax += totalTaxCharges;
		}
	}
	transaction.totalCharges = totalCharges;
	transaction.sumOfChargeNetAmount = sumOfChargeNetAmount;
	
	$('#totalCharges').autoNumericSet(totalCharges, {vMin:'-999999999999.'});
	$('#totalChargesStripped').val(totalCharges);	
	$('#taxOfCharges').autoNumericSet(sumOfTax,{vMin:'-999999999999.'});
	$('#taxOfChargesStripped').val(sumOfTax);
	transaction.interestRate = $('#interestRateStripped').val();
	transaction.taxOnInterestRate = $('#taxOnInterestRate').val();
	transaction.taxOnInterest = $('#taxOnInterestStripped').val();
	transaction.capitalGainTaxRate = $('#capitaGainTaxRate').val();
	transaction.capitalGainTax = $('#capitalGainTaxStripped').val();
	
	transaction.accruedInterest = $('#accruedInterestStripped').val();
	if (resetAccruedInterest) {
		transaction.accruedInterest = -1;
	}
	
	transaction.calculate();
	
	if ((($('#discounted').val()=='true')&&($('#isPrice').val()=='false')) || 
		(($('#discounted').val()=='true')&&($('#isPrice').val()=='true'))){	
		$('#discountNet').autoNumericSet(transaction.discountNet);
		$('#discountNetStripped').val(transaction.discountNet);
		if ($('#discountNet').val()=='0'){
			$('#discountNet').val(transaction.discountNet);
		}
	}
	
	if(isNom){
		//input by nominal
		$('#price').autoNumericSet(transaction.price);
		$('#priceStripped').val(transaction.price);
		
		if($('#netAmount').val() == ''){
			$("#price").val('');
			$("#priceStripped").val('');
		}
	}else{
		//input by price
		$('#netAmount').autoNumericSet(transaction.netAmount);
		$('#netAmountStripped').val(transaction.netAmount);
		
		if ($('#price').val() == '') {
			$('#netAmount').val('');
			$('#netAmountStripped').val('');
		}
	}
	
	$('#grossAmount').autoNumericSet(transaction.grossAmount);
	$('#grossAmountStripped').val(transaction.grossAmount);
	
	$('#accruedDays').autoNumericSet(transaction.accruedDays);
	$('#accruedDaysStripped').val(transaction.accruedDays);
	if (!$('#cekBoxWaiveAccInt').is(":checked")){
		$('#accruedInterest').autoNumericSet(transaction.accruedInterest);
		$('#accruedInterestStripped').val(transaction.accruedInterest);
//		checkAccInterest();
	}

	if ((resetAccruedInterest) || (resetAccruedInterest=='undefined')){
		$('#waveAccruedInterest').val(transaction.accruedInterest);
	}
	
//	$('#taxOnInterest').autoNumericSet(transaction.taxOnInterest);
//	$('#taxOnInterestStripped').val(transaction.taxOnInterest);
	
	$('#taxOnInterestNet').autoNumericSet(transaction.netAccruedInterest);
	$('#taxOnInterestNetStripped').val(transaction.netAccruedInterest);
	
	$('#proceed').autoNumericSet(transaction.proceed);
	$('#proceedStripped').val(transaction.proceed);
	
//	$('#capitalGainTax').autoNumericSet(transaction.capitalGainTax);
//	$('#capitalGainTaxStripped').val(transaction.capitalGainTax);
	
	$('#netProceed').autoNumericSet(transaction.netProceed);
	$('#netProceedStripped').val(transaction.netProceed);
//	console.debug("Settlement Number = " +$("#settlementNumber").val());
	if ($("#settlementNumber").val() == "") {
//		console.debug("Settlement Number null");
		if ($('#settTransKey').val()!=''){
//			console.debug("Settlement Trans Key not null");
			if ($('#cashTransCode').val()!=''){
//				console.debug("Cash Trans Code not null");
				//$('#settlementAmount').val($('#netProceed').val());
				$('#settlementAmount').autoNumericSet(transaction.settlementAmount);
				$('#settlementAmountStripped').val(transaction.settlementAmount);
				$('#customerCashAmountType').autoNumericSet(transaction.settlementAmount);
				$('#custodianAmountType').autoNumericSet(transaction.settlementAmount);
			} else {
//				console.debug("Cash Trans Code null");
				$('#settlementAmount').val(0);
				$('#settlementAmountStripped').val(0);
				$('#customerCashAmountType').val(0);
				$('#custodianAmountType').val(0);
			}
		} else {
//			console.debug("Settlement Trans Key null");
			$('#settlementAmount').val(0);
			$('#settlementAmountStripped').val(0);
		}
	} else {
//		console.debug("Settlement Number not null");
		if ($('#cashTransCodeSett').val()!=''){
//			console.debug("Cash Trans Code Settle not null");
			$('#settlementAmount').autoNumericSet(transaction.settlementAmount);
			$('#settlementAmountStripped').val(transaction.settlementAmount);
		} else {
//			console.debug("Cash Trans Code Settle null");
			$('#settlementAmount').val(0);
			$('#settlementAmountStripped').val(0);
		}
	}
	
	
}

function countYield(){
	var amortizationMethod = $("#amortizationMethod").val();
	var parPrice = $('#parPrice').val();
	var priceUnit = $('#priceUnit').val();
	var discAfterTax = $('#discountNetStripped').val();
	var quantity = $('#quantityStripped').val();
	var nominal = $('#netAmountStripped').val();
	var interestRate = $('#interestRateStripped').val();
	var maturityDate = new Date($('#maturityDateHidden').datepicker('getDate'));
	var settlementDate = new Date($('#settlementDate').datepicker('getDate'));
	
	var parValue = quantity * parPrice * priceUnit;
	
	var yield = calculateYield(
					amortizationMethod,
					parValue,
					quantity,
					nominal,
					interestRate,
					discAfterTax,
					maturityDate,
					settlementDate,
					parPrice
			) * 100;
	
	$('#yield').autoNumericSet(yield, {vMin:'-999999999999.9999'});
	$('#yieldStripped').val(yield);
}

function fillDiscAmount(){
	console.log(">> fillDiscAmount <<");
	var discAfterTax = $('#discountNetStripped').val();
	if(discAfterTax == '') discAfterTax = 0;

	//fill tax of disc/ Tax Amount
	var discountRate = $('#discountTaxRate').val();
	if (discountRate=='') discountRate = 0;
	var countRate = Number(100) - Number(discountRate);
	var taxOfDisc = Number(Number(discountRate)/countRate*discAfterTax);
	$('#discountTax').autoNumericSet(taxOfDisc);
	$('#discountTaxStripped').val(taxOfDisc);
	
	//fill disc amount
	var discountAmount = Number(discAfterTax) + Number(taxOfDisc);
	$('#discountAmount').autoNumericSet(discountAmount, {vMin:'-999999999999.',vMax:'9999999999999.', mDec:15});
	$('#discountAmountStripped').val(discountAmount);
}

function clearValueForTransCode(){
	$('#transactionCode').val("");
	$('#transactionTemplateKey').val("");
	$('#transactionCodeDesc').val("");
	$('#h_transactionCodeDesc').val("");
	$('#securityClassId').val("");
	$('#securityClass').val("");
	$('#h_securityClass').val("");
	$('#securityClassDesc').val("");
	$('#h_securityClassDesc').val("");
	$('#securityType').val("");
	$('#h_securityType').val("");
	$('#securityTypeDesc').val("");
	$('#h_securityTypeDesc').val("");
	$('#securityId').val("");
	$('#securityKey').val("");
	$('#securityDesc').val("");
	$('#h_securityDesc').val("");
	$('#classification').val("");
	$('#classificationId').val("");
	$('#classificationDesc').val("");
	$('#h_classificationDesc').val("");
	$('#holdingRefs').removeClass('fieldError');
	$('#holdingRefs').val('');
	$('#h_holdingRefs').val('');
	$('#holdingQuantity').val('');
	$('#h_holdingQuantity').val('');
	$('#prematchRequired1').removeAttr("checked");
	$('#prematchRequired2').attr('checked', 'checked');
	$('#prematchRequired').val('');
	$('#settlementDate').val('');
	$('#effectiveDate').val('');
	$('#effectiveDateHidden').val('');
	$('#maturityDate').val('');
	$('#maturityDateHidden').val('');
	$('#lastPaymentDate').val('');
	$('#lastPaymentDateHidden').val('');
	$('#nextPaymentDate').val('');
	$('#nextPaymentDateHidden').val('');
	$('#discountAmount').val('');
	$('#discountAmountStripped').val('');
	$('#discountTaxCode').val('');
	$('#discountTaxKey').val('');
	$('#discountTaxDesc').val('');
	$('#discountTaxRate').val('');
	$('#discountTax').val('');
	$('#discountTaxStripped').val('');
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
	$('#ctpNo').val('');
	$('#ctpNoHidden').val('');
	$('#ctpNo').attr('disabled', 'disabled');
	$('p[id="pCtpRef"] label span').html("");
	
	$('#currencyCode').val('');
	$('#currTrans').val('');
	$('#currencyCodeHid').val('');
	$('#accruedInterest').attr('disabled', 'disabled');
	$('#accruedInterest').val('');
	$('#accruedInterestStripped').val('');
	$('#cekBoxWaiveAccInt').attr('disabled', 'disabled');
	$('#cekBoxWaiveAccInt').attr('checked', false);
	$('#cekBoxWaiveAccInt').change();
	$('#waveAccruedInterest').val('');
	$('#accruedInterest').removeClass('fieldError');
	$('#accruedInterestError').html('');
}

function checkSentToInterface() {
	if ('${confirming}' != 'true' && '${taskId}' == '') {
		if ($('#depositoryCode').val() == '${depositoryCbest}') {
			$('#generalSentToInterface1').removeAttr('disabled');
			$('#generalSentToInterface2').removeAttr('disabled');
		} else {
			$('#generalSentToInterface1').attr('disabled', 'disabled');
			$('#generalSentToInterface2').attr('disabled', 'disabled');
		}
	}
	if ('${fromView}' == 'viewCsCancelTrade' ) {
		$('#generalSentToInterface1').attr('disabled', 'disabled');
		$('#generalSentToInterface2').attr('disabled', 'disabled');
	}
}

function yieldPrematch() {
	if(($("input[name='transaction.sentToInterface']").val() =='true'
	 	&& $('#depositoryCode').val() == '${depositoryCbest}'
			&& $("#cbestMessageType").val() == "${cbestOtb}")){
		$("#yieldPrematchLbl span.req").show();
		if (('${fromApproval}'!= 'true') && ('${fromInquiry}'!='fromInquiry')&&('${confirming}'!='true')){
			$("#yieldPrematch").attr('disabled', false);
		}
	} else {
		$("#yieldPrematchLbl span.req").hide();
		$("#yieldPrematch").val("");
		$("#yieldPrematch").attr('disabled', true);
	}
}

function counterPartyTypeLive(){
	setCounterPartyTypeDesc();
	cekCounterParty();
}

function redmine2847() {
	console.debug("redmine2847");
	
	if($('#custodyTransactionCode').val() == "RVP" || $('#custodyTransactionCode').val() == "RFOP"){
		$('p[id=pCustomerCashAmount] label').html("Credit Amount");
		$('p[id=pCustodianAmount] label').html("Debet Amount");
	}else{
		$('p[id=pCustomerCashAmount] label').html("Debet Amount");
		$('p[id=pCustodianAmount] label').html("Credit Amount");
	}
	
	var vTransactionTemplateKey = $('#transactionTemplateKey').val();
	$.get("@{Transactions.getCashAccountAmountType()}?transactionTemplateKey="+vTransactionTemplateKey, function(data) {
		checkRedirect(data);
		var amountType = data.amounttype;
		console.debug("amountType => " + amountType);
		if (amountType == '${bnTransTypeDebet}') {
			$('p[id=pCustomerCashAmount] label').html("Debet Amount");
			$('p[id=pCustodianAmount] label').html("Credit Amount");
		} else if (amountType == '${bnTransTypeCredit}') {
			$('p[id=pCustomerCashAmount] label').html("Credit Amount");
			$('p[id=pCustodianAmount] label').html("Debet Amount");
		}
	});

	$("#descriptionLbl span.req").hide();
	if($('#custodyTransactionCode').val() != "RVP" && $('#custodyTransactionCode').val() != "DVP"){
		$("#purposeLbl span.req").show();
		if(!'${confirming}' && '${mode}' != 'view'){
			$("#settlementPurpose").attr('disabled', false);
			$("#descriptionForPrematch").attr('disabled', false);
		}
	} else {
		$("#purposeLbl span.req").hide();
		$("#referenceLbl span.req").hide();
		$("#reasonLbl span.req").hide();
		$("#descriptionLbl span.req").hide();

		$("#settlementPurpose").attr('disabled', true);
		$("#settlementReference").attr('disabled', true);
		$("#settlementReason").attr('disabled', true);
		$("#descriptionForPrematch").attr('disabled', true);
		/*if($("#settlementPurpose").isEmpty()){
			$("#settlementPurpose").attr('disabled', true);
		} else {
			if(!'${confirming}' && '${mode}' != 'view'){
				$("#settlementPurpose").attr('disabled', false);
			}
		}*/
	}

	if($("#settlementReference").isEmpty()){
		$("#settlementReference").attr('disabled', true);
		if($("#settlementPurpose").val() == '${EXCH}'){
			$("#referenceLbl span.req").show();
		} else {
			$("#referenceLbl span.req").hide();
		}
	} else {
		if(!'${confirming}' && '${mode}' != 'view'){
			$("#settlementReference").attr('disabled', false);
			if($("#settlementPurpose").val() == '${EXCH}'){
				$("#referenceLbl span.req").show();
			} else {
				$("#referenceLbl span.req").hide();
			}
		}
	}

	if($("#settlementReasonCode").isEmpty()){
		$("#settlementReasonCode").attr('disabled', true);
		$("#settlementReasonHelp").attr('disabled', true);
		$("#settlementReasonName").attr('disabled', true);
		$("#reasonLbl span.req").hide();
	} else {
		if(!'${confirming}' && '${mode}' != 'view'){
			$("#settlementReasonCode").change();
			$("#settlementReasonCode").attr('disabled', false);
			$("#settlementReasonHelp").attr('disabled', false);
			$("#settlementReasonName").attr('disabled', false);
			if($('#settlementReasonKey').val() == '${OTHR}'){
				$("#descriptionLbl span.req").show();
			} else {
				$("#descriptionLbl span.req").hide();
				$('#descriptionForPrematchError').html("");
			}
		}
	}

	$("#settlementPurpose").change(function(){
		$("#descriptionLbl span.req").hide();
		if($(this).val() == '${EXCH}'){
			$("#referenceLbl span.req").show();
			$("#settlementReference").attr('disabled', false);

			$("#reasonLbl span.req").hide();
			$("#settlementReasonCode").attr('disabled', true);
			$("#settlementReasonHelp").attr('disabled', true);
			$("#settlementReasonCode").val("");
			$("#settlementReasonName").val("");
			$('#settlementReasonCodeForPrematchError').html("");
		} else {
			$("#referenceLbl span.req").hide();
			$("#settlementReference").attr('disabled', true);
			$("#settlementReference").val("");
			$('#settlementReferenceForPrematchError').html("");

			$("#reasonLbl span.req").show();
			$("#settlementReasonCode").attr('disabled', false);
			$("#settlementReasonHelp").attr('disabled', false);
		}
	});

	yieldPrematch();

	/*if($("#settlementAmountStripped").val() == 0){
		$("#transferMethod").attr('disabled', true);
		$("#transferMethod").val('${transferMethodManual}');
	}*/
}

function isSentToInterface() {
	if ($("input[name='transaction.sentToInterface']").val() =='true') {
		$("input[name='transaction.sentToInterface']").val('true');
		$("#generalSentToInterface1").attr("checked", true);
		$("#generalSentToInterface2").removeAttr('checked');

		if (($('#ctpRequired').val()=='true') && ($('#depositoryCode').val() == '${depositoryCbest}' || $('#depositoryCode').val() == '${depositoryBis}')){
			$('p[id="pCtpRef"] label span').html(" *");
		}
	} else {
		$("input[name='transaction.sentToInterface']").val('false');
		$("#generalSentToInterface1").removeAttr('checked');	
		$("#generalSentToInterface2").attr("checked", true);

		if (($('#ctpRequired').val()=='true') && ($('#depositoryCode').val() == '${depositoryCbest}' || $('#depositoryCode').val() == '${depositoryBis}')){
			$('p[id="pCtpRef"] label span').html("");
		}
	}
}

function doSave(){
	/*alert("last payment date = " +$('#lastPaymentDateHidden').val());
	alert("next payment date = " +$('#nextPaymentDateHidden').val());
	alert("effective date = " +$('#effectiveDateHidden').val());*/
	if ($('#quantity').hasClass('fieldError')){
		$('#quantity').focus();
		return false;
	}
	
	$('#netAmtErr').html(" ");
	$("#netAmount").removeClass('fieldError');
	if($("#netAmountStripped").val()==""){
		$('#netAmtErr').html("required");
		$("#netAmount").focus().addClass('fieldError');
		return false;
	}
	
	/*var expiredDate = new Date($('#expiredDate').datepicker('getDate'));
	var currentDate = new Date($('#transactionDate').datepicker('getDate'));
	if ($('#isExpiredDate').val()=='true'){
		if (expiredDate.getTime() < currentDate.getTime()){
			$('#securityId').focus();
			$('#errmsgSec').html("This Security Code has been expired");
			return false;
		}
	}*/
	
	if ($("#transactionDate").hasClass('fieldError')) {
		$("#transactionDate").focus();
		return false;
	}
			
	if ($("#settlementDate").hasClass('fieldError')) {
		$("#settlementDate").focus();
		return false;
	}
	
	if ($("#maturityDate").hasClass('fieldError')) {
		$("#maturityDate").focus();
		return false;
	}
	
	$( "#sentToInterface1" ).removeAttr( "disabled");
	$( "#sentToInterface2" ).removeAttr( "disabled");

	$( "#matchStatus" ).removeAttr( "disabled");
	
	if ($('#accruedInterest').hasClass('fieldError')) {
		$('#accruedInterest').focus();
		return false;
	}
	
	if ($('#classification').hasClass('fieldError')) {
		$('#classification').focus();
		return false;
	}

	return true;
}

function changeIsNominal(){
	var isNom = $("#chkIsInpNominal:checked").length;
	if(isNom<1){
		//gk dicentang
		$("#netAmount").attr('disabled','disabled');
		$("#price").removeAttr('disabled');
		$("#isInpNominal").val(false);
	}else{
		//dicentang
		$("#price").attr('disabled','disabled');
		$("#netAmount").removeAttr('disabled');
		$("#isInpNominal").val(true);
	}
}


function checkAccInterest(){
	var curr = $("#currencyCode").val();
	var accIntStripped = getAccInterestDef();
	var isWaive = $("#cekBoxWaiveAccInt").is(':checked');
	var valid = true;
	var accInt = parseFloat($("#accruedInterestStripped").val());
	
	accInt = Math.round(accInt * 10000) / 10000;
	
//	console.log("accIntStripped = "+accIntStripped);
//	console.log("accInt = "+accInt);
	var diff = accIntStripped-accInt;
	
	$('#accruedInterestError').html("");
	
	if ($('#hasInterest').val()=='true'){
		if(accIntStripped!=accInt){
			if(curr!=null && !isWaive){
				$.post('@{Transactions.getAdjMaint()}', {'currency':curr}, function(data) {
					if (data.status=='success'){
						var val = parseFloat(data.value);
						if(diff<0){diff *= -1;}
						
						if(data.operator=='EQ'){
							if(diff!=val){valid=false; $('#accruedInterestError').html("Accrued Interest Different must be equal to "+val);}
						}else if(data.operator=='GT'){
							if(!(diff>val)){valid=false; $('#accruedInterestError').html("Accrued Interest Different must be greater than "+val);}
						}else if(data.operator=='GTE'){
							if(!(diff>=val)){valid=false; $('#accruedInterestError').html("Accrued Interest Different must greater than or equal to  "+val);}
						}else if(data.operator=='LT'){
							if(diff>val){valid=false; $('#accruedInterestError').html("Accrued Interest Different must be lesser than "+val);}
						}else if(data.operator=='LTE'){
							if(!(diff<=val)){valid=false; $('#accruedInterestError').html("Accrued Interest Different must be lesser than or equal to "+val);}
						}else if(data.operator=='IN'){
							if(diff==val){valid=false; $('#accruedInterestError').html("Accrued Interest Different must not equal "+val);}
						}
					}
				});
			}//end if curr!=null && !isWaive
		}//end if accIntStripped!=accInt
	}//end if $('#hasInterest').val()=='true'
	return valid;
}

function clearTaxCode(){
	$("#discountTaxDesc").val('');
	$("#discountTaxCode").val('');
	$("#h_discountTaxCode").val('');
	$("#discountTaxKey").val('');
	$("#h_discountTaxDesc").val('');
	$("#discountTaxRate").val('');
}

function getDefDiscTaxCode(){
	var secType = $("#securityType").val();
	var accNo = $("#accountNoKey").val();
	var transDate = $("#transactionDate").val();
	
	clearTaxCode();
	
	if(secType!='' && accNo!='' && transDate){
		$.post('@{Transactions.getTaxAccInt()}', {'custodyAccountKey':$('#accountNoKey').val(),'securityType':$("#securityType").val(),'transactionDate':$("#transactionDate").val()}, function(data) {
			if (data.status=='success'){
				$("#discountTaxCode").val(data.taxCode).blur();
			} 
		});
	}
}