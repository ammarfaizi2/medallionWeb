var chargeGrid;
var scheduleGrid;
var tabChargeVisited = false;
var tabScheduleVisited = false;
var reloadCharge = true;
var oldAccrued="";
var oldAccruedStriped="";
$(function(){
	
	$('#addCharge').button({disabled: true});
	$('#tabs').tabs();
	$('#save, #clear, #cancel, #confirm, #back, '+ 
	  '#generateButton, #next1, #next2, #next3, '+
	  '#next4, #next5, #prev1, #prev2, #prev3, '+
	  '#prev4, #prev5, #addCharge, #resetCharge, #chargeDone').button();
	
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
		$("#remarksCancel[maxlength]").bind('input propertychange', function() {
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    });

	}
	
	if ('${from}' == '${depositoInquiryFullRedeem}'){
		$('#dtDeposito label').html("Full Redeem Date <span class=\"req\">*</span>");
	}
	
	if ('${mode}' == 'edit' || '${mode}' == 'view'){
		if ('${confirming}'=='false'){
			var gros = Number($('#accruedInterest').autoNumericGet());
			var net = Number($('#discountNet').autoNumericGet());
			
			var amount = Number(gros) - Number(net);
			$('#taxAmount').autoNumericSet(amount);
			$('#taxAmountStripped').val(amount);
		}
	}
	
	$('#showPayments').attr('disabled', true);
	
	if ('${deposito?.interestFrequency?.lookupId}' == '${intPaymentFreqMonthly}'){
		
		$('#paymentFreq2').attr('checked', true);
		if ('${confirming}'=='false')
			$('#showPayments').attr('disabled', false);
	}
	
	if ('${maintenanceLogKey}' != '') {
		if ('${origin}' == '${cancelDepositoBreakOriginated}') {
			$('#correction').css("display","none");
			$('#cancelDepositoBreak').css("display","");
		} else if ('${origin}' == '${depositoBreakOriginated}') {
			$('#cancelDepositoBreak').css("display","none");
			$('#correction').show();
			$('#correction').css("display","");
		}
	} else {
		if ('${from}' == '${depositoInquiryOriginated}' || '${from}' == '${depositoInquiryFullRedeem}'){
			$('#needCorrection').attr('disabled', true);
			$('#remarkCorrection').attr('disabled', true);
		}
		
		if (($("input[name='deposito.needCorrection']").val() == 'true') && ($("input[name='deposito.remarksCorrection']").val() != '')) {
			
			$('#correction').show();
			$("#correction").css("display","");
			$('#needCorrection').attr('disabled', true);
			$('#needCorrection').attr("checked", true);
			$('#remarkCorrection').attr('disabled', true);
		}
	}
	
	if ('${origin}' == '${cancelDepositoBreakOriginated}') {
		$('#addCharge').button({disabled: true});
		$('#resetCharge').button({disabled: true});
		$('#chargeDone').button({disabled: true});
	}

	$('#showCharges').click(function() {
		$('#tabs').tabs('select', 1);
		tabChargeVisited = true;
		$('#chargeDone').css('display', '');
		var firstElement = $('#chargeList input:text').first();
		firstElement.focus();
	});
	
	$('#chargeDone').click(function() {
		$('#chargeDone').css('display', 'none');
		$('#tabs').tabs('select', 0);
		if ($('#interestRate').attr('disabled')) {
			$('#tax').focus();
		} else {
			$('#interestRate').focus();
		}
	});
	
	$('#next1').click(function() {
		$('#tabs').tabs('select', 1);
		if (tabScheduleVisited) {
			$('#next2').focus();
		} else {
			$('#chargeList input:text').first().focus();
		}
	});
	
	$('#next2').click(function() {
		$('#tabs').tabs('select', 2);
	});
	
	$('#next4').click(function() {
		$('#tabs').tabs('select', 3);
	});
	
	$('#prev2').click(function() {
		$('#tabs').tabs('select', 0);
	});
	
	$('#prev4').click(function() {
		if ($('#paymentFreqHidden').val()=='${intPaymentFreqMonthly}'){
			$('#tabs').tabs('select', 2);
		} else {
			$('#tabs').tabs('select', 1);
		}
	});
	
	$('#prev5').click(function() {
		$('#tabs').tabs('select', 2);
	});
	
	$('#depositoDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends
	});
	
	$('#depositoDate').change(function() {
		
		if($('#depositoKey').val()!=null && $('#depositoKey').val() != ''){
			$.get("@{DepositoBreaks.paymentDate()}", {'deposito':$('#depositoKey').val(),'date':$('#depositoDate').val()}, function(datas) {
				checkRedirect(datas);
				if(datas==null){
					$("#lastPaymentDate").val('');
					$("#lastPaymentDateHidden").val('');
					$("#nextPaymentDate").val('');
					$("#nextPaymentDateHidden").val('');
				}else{
					$("#lastPaymentDate").datepicker('setDate', new Date(datas.startDate));
					$("#lastPaymentDateHidden").datepicker('setDate', new Date(datas.startDate));
					$("#nextPaymentDate").datepicker('setDate', new Date(datas.endDate));
					$("#nextPaymentDateHidden").datepicker('setDate', new Date(datas.endDate));
				}
			});
		}
		
		if (!($(this).hasClass('fieldError'))) {
			if ($('#maturityDate').val()!=''){
				calculate();
				if($('#taxKey').val() != null && $('#taxKey').val() != ''){
					getTaxAmount();
				}
			}
		}
	});
	
	$('#accruedInterest').change(function() {
        
		oldAccrued=$('#accruedInterest').val();
		oldAccruedStriped = $('#accruedInterest').autoNumericGet();//$('#accruedInterestStripped').val();
		if($('#taxKey').val() != null && $('#taxKey').val() != ''){
			getTaxAmount();
		}

	});
	
	$('#taxCode').change(function(){
		if ($('#taxCode').val() == "") {
			$('#taxCode').val('');
			$('#taxKey').val('');
			$('#taxRate').val('');
			$('#h_taxRate').val('');
			$('#taxRateHidden').val('');
		}
	});
	
	$('#taxCode').lookup({
		list : '@{Pick.taxMasters()}',
		get : {
			url : '@{Pick.taxMaster()}',
			success : function(data) {
				$('#taxCode').removeClass('fieldError');
				$('#taxKey').val(data.code);
				$('#taxRate').autoNumericSet(data.taxRate);
//				$('#taxRate').valueRnd(data.taxRate, true, 2, "ROUND");
				$('#h_taxRate').val(data.taxRate);
				$('#taxRateHidden').val(data.taxRate);
				if($('#accruedInterest').val() != '' && $('#accruedInterest').val() != null ){
					getTaxAmount();
				}
				
			},
			error: function(data) {
				$('#taxCode').addClass('fieldError');
				$('#taxRate').val('NOT FOUND');
				$('#h_taxRate').val('');
				$('#taxRateHidden').val('');
				$('#taxKey').val('');
			}
		},	
		help : $('#discountTaxHelp')
	});
	
	// ACOUNT NO
	$('#accountNo').change(function() {
		if ($(this).val() == "") {
			$(this).removeClass("fieldError");
			$('#accountNo').val("");
			$('#accountKey').val("");
			$('#accountName').val("");
			$('#h_accountName').val("");
			
			$('#bankAccount').val("");
			$('#bankAccountKey').val("");
			$('#bankAccountName').val("");
			$('#h_bankAccountName').val("");
			$('#bankCode').val("");
			$('#h_bankCode').val("");
			$('#bankCodeKey').val("");
			$('#bankCodeName').val("");
			$('#h_bankCodeName').val("");
			$('#bankAccountBeneficiary').val("");
			$('#h_bankAccountBeneficiary').val("");
			$('#bankAccountCurrency').val("");
			$('#h_bankAccountCurrency').val("");
			$('#customerKey').val('');
		}
		
	});
	
	$('#accountNo').dynapopup2({key:'accountKey', help:'accountHelp', desc:'accountName'},'PICK_CS_ACCOUNT', '', 'securityType', function(data){
		var accountKey = $('#accountKey').val();
		$('#accountNo').removeClass('fieldError');
		$('#accountName').val(data.description);
		$('#h_accountName').val(data.description);
		$('#accountKey').val(data.code);
		
		$('#bankAccount').removeClass('fieldError');
		$('#bankAccount').val(data.bankAccountNo);
		$('#bankAccountKey').val(data.bankAccountKey);
		$('#bankAccountName').val(data.bankAccountDesc);
		$('#h_bankAccountName').val(data.bankAccountDesc);
		$('#bankCode').removeClass('fieldError');
		$('#bankCode').val(data.bankCodeName);
		$('#h_bankCode').val(data.bankCodeName);
		$('#bankCodeKey').val(data.bankCodeKey);
		$('#bankCodeName').val(data.bankCodeDesc);
		$('#h_bankCodeName').val(data.bankCodeDesc);
		$('#bankAccountBeneficiary').val(data.bankCodeBeneficiary);
		$('#h_bankAccountBeneficiary').val(data.bankCodeBeneficiary);
		$('#bankAccountCurrency').val(data.bankCodeCurrency);
		$('#h_bankAccountCurrency').val(data.bankCodeCurrency);
		$('#customerKey').val(data.customerKey);
		
		if (accountKey != data.code) {
//			disableCharge();
		}
		
		if (data.blocked == true) {
			$('#accountNo').addClass('fieldError');
			$('#accountNoError').html('Account is Blocked');
			$('#accountBlocked').val(data.blocked);
		} else {
			$('#accountNo').removeClass('fieldError');
			$('#accountNoError').html('');
			$('#accountBlocked').val('');
		}
		$('#accountNo').trigger("refreshDepositoPopup");
	},function(data){
		$('#accountNo').addClass('fieldError');
		$('#accountNo').val('');
		$('#accountName').val('NOT FOUND');
		$('#h_accountName').val('');
		$('#accountKey').val('');
		
		$('#bankAccount').val("");
		$('#bankAccountKey').val("");
		$('#bankAccountName').val("");
		$('#h_bankAccountName').val("");
		$('#bankCode').val("");
		$('#h_bankCode').val("");
		$('#bankCodeKey').val("");
		$('#bankCodeName').val("");
		$('#h_bankCodeName').val("");
		$('#bankAccountBeneficiary').val("");
		$('#h_bankAccountBeneficiary').val("");
		$('#bankAccountCurrency').val("");
		$('#h_bankAccountCurrency').val("");
		$('#accountNo').trigger("refreshDepositoPopup");
	});
	
	
	if (jQuery.trim($('#accountNo').val()).length > 0) {
		var vAccount = $('#accountNo').val();
		$.get("@{Pick.account()}", {'code':vAccount}, function(data) {
			$('#customerKey').val(data.customerKey);
		});
	}
	
	$('#bankAccount').lookup({
		list : '@{Pick.bankAccountsByAcctNo()}?domain=CUSTOMER',
		get:{
			url: '@{Pick.bankAccountByAccountNoAndCustomerKey()}?domain=CUSTOMER',
			success: function(data){
				if (data) {
					if ($("#accountNo").val() != ""){
						$('#bankAccount').removeClass('fieldError');
						var codeSplit = $('#bankAccount').val().split("");
						$('#bankAccount').val(codeSplit[0]);
						$('#bankAccountKey').val(data.code);
						$('#bankAccountName').val(data.description);
						$('#h_bankAccountName').val(data.description);
						$('#bankCode').val(data.bankCode.thirdPartyCode);
						$('#h_bankCode').val(data.bankCode.thirdPartyCode);
						$('#bankCodeKey').val(data.bankCode.thirdPartyKey);
						$('#bankCodeName').val(data.bankCode.thirdPartyName);
						$('#h_bankCodeName').val(data.bankCode.thirdPartyName);
						$('#bankAccountBeneficiary').val(data.customer.customerName);
						$('#h_bankAccountBeneficiary').val(data.customer.customerName);
						$('#bankAccountCurrency').val(data.currency.currencyCode);
						$('#h_bankAccountCurrency').val(data.currency.currencyCode);
					} else {
						$('#bankAccount').addClass('fieldError');
						$('#bankAccountName').val('NOT FOUND');
						$('#bankAccount').val('');
						$('#bankAccountKey').val('');
						$('#h_bankAccountName').val('');
						$('#bankCode').val('');
						$('#h_bankCode').val('');
						$('#bankCodeKey').val('');
						$('#bankCodeName').val('NOT FOUND');
						$('#h_bankCodeName').val('');
						$('#bankAccountBeneficiary').val('');
						$('#h_bankAccountBeneficiary').val('');
						$('#bankAccountCurrency').val('');
						$('#h_bankAccountCurrency').val('');
					}
					
				}	
			},
			error: function(data) {
				$('#bankAccount').addClass('fieldError');
				$('#bankAccountName').val('NOT FOUND');
				$('#bankAccount').val('');
				$('#bankAccountKey').val('');
				$('#h_bankAccountName').val('');
				$('#bankCode').val('');
				$('#h_bankCode').val('');
				$('#bankCodeKey').val('');
				$('#bankCodeName').val('NOT FOUND');
				$('#h_bankCodeName').val('');
				$('#bankAccountBeneficiary').val('');
				$('#h_bankAccountBeneficiary').val('');
				$('#bankAccountCurrency').val('');
				$('#h_bankAccountCurrency').val('');
			}
		},
		key : $('#bankAccountKey'),
		description : $('#bankAccountName'),
		filter : [$('#accountKey'), $('#customerKey')],
		help : $('#bankAccountHelp')
	});
	
	$('#bankAccount').change(function() {
		if ($('#bankAccount').val() == "") {
			$('#bankAccount').val('');
			$('#bankAccountKey').val('');
			$('#bankAccountName').val('');
			$('#h_bankAccountName').val('');
			$('#bankCode').val('');
			$('#h_bankCode').val('');
			$('#bankCodeKey').val('');
			$('#bankCodeName').val('');
			$('#h_bankCodeName').val('');
			$('#bankAccountBeneficiary').val('');
			$('#h_bankAccountBeneficiary').val('');
			$('#bankAccountCurrency').val('');
			$('#h_bankAccountCurrency').val('');
		}
	});
	
	$('#securityType').change(function(){
		if ($(this).val()==''){
			$(this).removeClass("fieldError");
			$('#securityTypeId').val('');
			$('#securityTypeDesc').val('');
			$('#h_securityTypeDesc').val('');
			$('#securityClass').val('');
			$('#hasInterest').val('');
			clearSecurity();
		}
		attachSecuritiesPaging();
	});
	
	$('#securityType').lookup({
		list:'@{Pick.securityTypes()}',
		get:{
			url:'@{Pick.securityTypeDeposito()}',
			success: function(data){
				$('#securityType').removeClass('fieldError');
				$('#securityTypeDesc').val(data.description);
				$('#h_securityTypeDesc').val(data.description);
				$('#securityClass').val(data.securityClass);
				$('#transactionTemplateKey').val(data.transactionTemplateKey);
				$('#depositoTemplateKey').val(data.depositoTemplateKey);
				$('#hasInterest').val(data.hasInterest);
				clearSecurity();
				attachSecuritiesPaging();
				},
			error: function(data){
				$('#securityType').addClass('fieldError');
				$('#securityType').val('');
				$('#securityTypeDesc').val('NOT FOUND');
				$('#h_securityTypeDesc').val('');
				$('#securityClass').val('');
				attachSecuritiesPaging();
			}
		},
		description:$('#securityTypeDesc'),
		filter:'${depositoTemplateBreak}',
		help:$('#securityTypeHelp'),
		nextControl:$('#securityId')
	});
	
	$('#depositoNo').change(function() {
		if ($(this).val() == "") {
			clearDeposito();
		}
	});
	
	$("#accountNo").add($("#securityId")).bind("refreshDepositoPopup", function(){
		attachDepositoNo();
	});
	
	function attachDepositoNo() {
		var accountKey = $('#accountKey').val() == "" ? "0" : $('#accountKey').val();
		var securityKey = $('#securityKey').val() == "" ? "0" : $('#securityKey').val();
		
		$('#depositoNo').dynapopup2({key:'depositoKey', help:'depositoHelp', desc:'depositoNominal'}, 'PICK_DEPOSITO', accountKey +"|"+ securityKey, 'externalReference', function(data){
			$('#depositoNo').removeClass('fieldError');
			$('#depositoNominal').autoNumericSet(data.amount);
			$('#nominal').autoNumericSet(data.amount);
			$('#nominalStripped').val(data.amount);
			$('#interestRate').autoNumericSet(data.interestRate);
			$('#interestRateStripped').val(data.interestRate);
			$('#depositoKey').val(data.depositoKey);
			$('#depositoKeyAdd').val(data.depositoKey);
			$('#branchCode').val(data.branchCode);
			$('#h_branchCode').val(data.branchCode);
			$('#maturityDate').val(data.maturityDate);
			$('#maturityDateHidden').val(data.maturityDate);
			$('#effectiveDate').val(data.effectiveDate);
			$('#effectiveDateHidden').val(data.effectiveDate);
			
			$('#interestFrequencyId').val(data.interestFrequencyId);
			$('#interestFrequencyDesc').val(data.interestFrequencyDesc);
			$('#h_interestFrequencyDesc').val(data.interestFrequencyDesc);
			
			$('#maturityInsHidden').val(data.maturityInstructionId);
			$('#maturityIns').val(data.maturityInstructionId);
			$('#h_maturityIns').val(data.maturityInstructionId);
			
			if($('#depositoDate').val()!=null && $('#depositoDate').val() != ''){
				$.get("@{DepositoBreaks.paymentDate()}", {'deposito':$('#depositoKeyAdd').val(),'date':$('#depositoDate').val()}, function(datas) {
					checkRedirect(datas);
					if(datas==null){
						$("#lastPaymentDate").val('');
						$("#lastPaymentDateHidden").val('');
						$("#nextPaymentDate").val('');
						$("#nextPaymentDateHidden").val('');
					}else{
						$("#lastPaymentDate").datepicker('setDate', new Date(datas.startDate));
						$("#lastPaymentDateHidden").datepicker('setDate', new Date(datas.startDate));
						$("#nextPaymentDate").datepicker('setDate', new Date(datas.endDate));
						$("#nextPaymentDateHidden").datepicker('setDate', new Date(datas.endDate));
					}
				});
			}
			
			$.get("@{DepositoBreaks.getTaxUser()}", {'accountNo':$('#accountNo').val(), 'securityType':$('#securityType').val()
							, 'depositoNo':$('#depositoNo').val()}, function(dataTax){
								if(dataTax!='') $('#taxCode').val(dataTax).blur();
			});
			
			$('#accruedInterest').val('');
			$('#accruedInterestStripped').val('');
			
	//		checkNominal();
			calculate();
			
			
			
			if($('#taxKey').val() != '' && $('#taxKey').val() != null ){
				getTaxAmount();
			}
		},function(data){
			$('#depositoNo').addClass('fieldError');
			$('#depositoNo').val('');
			$('#depositoNominal').val('NOT FOUND');
			$('#nominal').val('NOT FOUND');
			$('#nominalStripped').val('');
			$('#depositoKey').val('');
			$('#branchCode').val('');
			$('#h_branchCode').val('');
			$('#depositoKeyAdd').val('');
		});
	}
	
	attachDepositoNo();
	
//	$('#depositoNo').lookup({
//		list:'@{Pick.depositos()}',
//		get:{
//			url:'@{Pick.deposito()}',
//			success: function(data){
//				$('#depositoNo').removeClass('fieldError');
//				$('#depositoNominal').autoNumericSet(data.amount);
//				$('#nominal').autoNumericSet(data.amount);
//				$('#nominalStripped').val(data.amount);
//				$('#interestRate').autoNumericSet(data.interestRate);
//				$('#interestRateStripped').val(data.interestRate);
//				$('#depositoKey').val(data.depositoKey);
//				$('#branchCode').val(data.branchCode);
//				$('#h_branchCode').val(data.branchCode);
//				$('#maturityDate').val(data.maturityDate);
//				$('#maturityDateHidden').val(data.maturityDate);
//				$('#effectiveDate').val(data.effectiveDate);
//				$('#effectiveDateHidden').val(data.effectiveDate);
//				
//				$('#interestFrequencyId').val(data.interestFrequencyId);
//				$('#interestFrequencyDesc').val(data.interestFrequencyDesc);
//				$('#h_interestFrequencyDesc').val(data.interestFrequencyDesc);
//				
//				$('#maturityInsHidden').val(data.maturityInstructionId);
//				$('#maturityIns').val(data.maturityInstructionId);
//				$('#h_maturityIns').val(data.maturityInstructionId);
//				
//				if($('#depositoDate').val()!=null && $('#depositoDate').val() != ''){
//					$.get("@{DepositoBreaks.paymentDate()}", {'deposito':$('#depositoKey').val(),'date':$('#depositoDate').val()}, function(datas) {
//						checkRedirect(datas);
//						if(datas==null){
//							$("#lastPaymentDate").val('');
//							$("#lastPaymentDateHidden").val('');
//							$("#nextPaymentDate").val('');
//							$("#nextPaymentDateHidden").val('');
//						}else{
//							$("#lastPaymentDate").datepicker('setDate', new Date(datas.startDate));
//							$("#lastPaymentDateHidden").datepicker('setDate', new Date(datas.startDate));
//							$("#nextPaymentDate").datepicker('setDate', new Date(datas.endDate));
//							$("#nextPaymentDateHidden").datepicker('setDate', new Date(datas.endDate));
//						}
//					});
//				}
//				
////				checkNominal();
//				calculate();
//				
//				if($('#taxKey').val() != '' && $('#taxKey').val() != null ){
//					getTaxAmount();
//				}
//				
//				},
//			error: function(data){
//				$('#depositoNo').addClass('fieldError');
//				$('#depositoNo').val('');
//				$('#depositoNominal').val('NOT FOUND');
//				$('#nominal').val('NOT FOUND');
//				$('#nominalStripped').val('');
//				$('#depositoKey').val('');
//				$('#branchCode').val('');
//				$('#h_branchCode').val('');
//			}
//		},
//		filter:[$('#accountKey'),$('#securityKey')],
//		help:$('#depositoHelp')
//	});
	
	$('#securityId').change(function() {
		if ($(this).val() == "") {
			clearSecurity();
		}
	});
	
	function attachSecuritiesPaging() {
		var securityType = $('#securityType').val();
		var pickName = (securityType == '') ? 'PICK_SC_MASTER' : 'PICK_SC_MASTER_BY_SEC_TYPE';
		$('#securityId').dynapopup2({key:'securityKey', help:'securityHelp', desc:'securityDesc'}, pickName, securityType, 'depositoNo', function(sdata){
			var data = $().fetchSync("@{Pick.security()}", {'code':$('#securityId').val(), 'filter':$('#securityType').val()});

			$('#securityId').removeClass('fieldError');
			$('#securityKey').val(data.code);
			$('#securityDesc').val(data.description);
			$('#h_securityDesc').val(data.description);
			
			$('#currencyCode').val(data.securityCurrency);
			$('#currencyCodeHidden').val(data.securityCurrency);
			
			$('#issuerCode').val(data.issuerBank.thirdPartyCode);
			$('#issuerCodeHide').val(data.issuerBank.thirdPartyCode);
			$('#issuerCodeKey').val(data.issuerBank.thirdPartyKey);
			$('#issuerCodeDesc').val(data.issuerBank.thirdPartyName);
			$('#issuerCodeDescHide').val(data.issuerBank.thirdPartyName);
			
			$('#accrualBase').val(data.accrualBase.replace("ACCRUAL_BASE-", ""));
			$('#accrualBaseCodeHidden').val($('#accrualBase').val());
			$('#accrualBaseHidden').val(data.accrualBase);
			$('#yearBase').val(data.yearBase.replace("YEAR_BASE-",""));
			$('#yearBaseCodeHidden').val($('#yearBase').val());
			$('#yearBaseHidden').val(data.yearBase);
			$('#frequency').val(data.frequency.replace("FREQUENCY-", ""));
			$('#frequencyId').val(data.frequency);
			$('#isFraction').val(data.isFraction);
			$('#fractionRatioSource').val(data.fractionRatioSource);
			$('#fractionRatioTarget').val(data.fractionRatioTarget);
			$('#minTrxQuantity').val(data.minTrxQuantity);
			$('#isScriptHidden').val(data.isScript);
			$('#classification').val('');
			if (data.isScript === true) {
				$('#isScript2').removeAttr("checked");
				$('#isScript1').attr('checked', 'checked');
			} else {
				$('#isScript1').removeAttr('checked');
				$('#isScript2').attr('checked', 'checked');
			}
			
			if ((data.valuationMethodAFS)&&(!data.valuationMethodTrade)&&(!data.valuationMethodHTM)) {
				$('#classification').val('${classificationAfs}');
			}
			
			if ((data.valuationMethodTrade)&&(!data.valuationMethodAFS)&&(!data.valuationMethodHTM)) {
				$('#classification').val('${classificationTrd}');
			}
			
			if ((data.valuationMethodHTM)&&(!data.valuationMethodAFS)&&(!data.valuationMethodTrade)) {
				$('#classification').val('${classificationHtm}');
			}
				
			if ((data.valuationMethodTrade) && (data.valuationMethodAFS)&&(!data.valuationMethodHTM)) {
				if ('${classTrd.lookupSequence}' < '${classAfs.lookupSequence}'){
					$('#classification').val('${classificationTrd}');
				} else {
					$('#classification').val('${classificationAfs}');
				}
			}
			
			if ((data.valuationMethodAFS) && (data.valuationMethodHTM)&&(!data.valuationMethodTrade)) {
				if ('${classAfs.lookupSequence}' < '${classHtm.lookupSequence}'){
					$('#classification').val('${classificationAfs}');
				} else {
					$('#classification').val('${classificationHtm}');
				}
			}
			
			if ((data.valuationMethodTrade) && (data.valuationMethodHTM)&&(!data.valuationMethodAFS)) {
				if ('${classTrd.lookupSequence}' < '${classHtm.lookupSequence}'){
					$('#classification').val('${classificationTrd}');
				} else {
					$('#classification').val('${classificationHtm}');
				}
			} 
			
			if ((data.valuationMethodTrade) && (data.valuationMethodAFS) && (data.valuationMethodHTM)) {
				if (('${classTrd.lookupSequence}' < '${classAfs.lookupSequence}') && 
					('${classTrd.lookupSequence}' < '${classHtm.lookupSequence}')){
					$('#classification').val('${classificationTrd}');
				}
				
				if (('${classAfs.lookupSequence}' < '${classTrd.lookupSequence}') && 
					('${classAfs.lookupSequence}' < '${classHtm.lookupSequence}')){
					$('#classification').val('${classificationAfs}');
				}
				
				if (('${classHtm.lookupSequence}' < '${classTrd.lookupSequence}') && 
					('${classHtm.lookupSequence}' < '${classAfs.lookupSequence}')){
					$('#classification').val('${classificationHtm}');
				}
			}
			$('#securityId').trigger("refreshDepositoPopup");
			clearDeposito();
			
			//karena terclear di clearDeposito maka di set ulang
			$('#accrualBase').val(data.accrualBase.replace("ACCRUAL_BASE-", ""));
			$('#accrualBaseCodeHidden').val($('#accrualBase').val());
			$('#accrualBaseHidden').val(data.accrualBase);
			$('#yearBase').val(data.yearBase.replace("YEAR_BASE-",""));
			$('#yearBaseCodeHidden').val($('#yearBase').val());
			$('#yearBaseHidden').val(data.yearBase);
		},function(data){
			clearSecurity();
			$('#securityId').addClass('fieldError');
			$('#securityDesc').val('NOT FOUND');
			$('#securityId').trigger("refreshDepositoPopup");
		});
	}
	attachSecuritiesPaging();
	
//	$('#securityId').lookup({
//		list : '@{Pick.securities()}',
//		get : {
//			url:'@{Pick.security()}',
//			success: function(data){
//				$('#securityId').removeClass('fieldError');
//				$('#securityKey').val(data.code);
//				$('#securityDesc').val(data.description);
//				$('#h_securityDesc').val(data.description);
//				
//				$('#currencyCode').val(data.securityCurrency);
//				$('#currencyCodeHidden').val(data.securityCurrency);
//				
//				$('#issuerCode').val(data.issuerBank.thirdPartyCode);
//				$('#issuerCodeHide').val(data.issuerBank.thirdPartyCode);
//				$('#issuerCodeKey').val(data.issuerBank.thirdPartyKey);
//				$('#issuerCodeDesc').val(data.issuerBank.thirdPartyName);
//				$('#issuerCodeDescHide').val(data.issuerBank.thirdPartyName);
//				
//				$('#accrualBase').val(data.accrualBase.replace("ACCRUAL_BASE-", ""));
//				$('#accrualBaseCodeHidden').val($('#accrualBase').val());
//				$('#accrualBaseHidden').val(data.accrualBase);
//				$('#yearBase').val(data.yearBase.replace("YEAR_BASE-",""));
//				$('#yearBaseCodeHidden').val($('#yearBase').val());
//				$('#yearBaseHidden').val(data.yearBase);
//				$('#frequency').val(data.frequency.replace("FREQUENCY-", ""));
//				$('#frequencyId').val(data.frequency);
//				$('#isFraction').val(data.isFraction);
//				$('#fractionRatioSource').val(data.fractionRatioSource);
//				$('#fractionRatioTarget').val(data.fractionRatioTarget);
//				$('#minTrxQuantity').val(data.minTrxQuantity);
//				$('#isScriptHidden').val(data.isScript);
//				if (data.isScript === true) {
//					$('#isScript2').removeAttr("checked");
//					$('#isScript1').attr('checked', 'checked');
//				} else {
//					$('#isScript1').removeAttr('checked');
//					$('#isScript2').attr('checked', 'checked');
//				}
//				
//				if ((data.valuationMethodAFS)&&(!data.valuationMethodTrade)&&(!data.valuationMethodHTM)) {
//					$('#classification').val('${classificationAfs}');
//				}
//				
//				if ((data.valuationMethodTrade)&&(!data.valuationMethodAFS)&&(!data.valuationMethodHTM)) {
//					$('#classification').val('${classificationTrd}');
//				}
//				
//				if ((data.valuationMethodHTM)&&(!data.valuationMethodAFS)&&(!data.valuationMethodTrade)) {
//					$('#classification').val('${classificationHtm}');
//				}
//					
//				if ((data.valuationMethodTrade) && (data.valuationMethodAFS)&&(!data.valuationMethodHTM)) {
//					if ('${classTrd.lookupSequence}' < '${classAfs.lookupSequence}'){
//						$('#classification').val('${classificationTrd}');
//					} else {
//						$('#classification').val('${classificationAfs}');
//					}
//				}
//				
//				if ((data.valuationMethodAFS) && (data.valuationMethodHTM)&&(!data.valuationMethodTrade)) {
//					if ('${classAfs.lookupSequence}' < '${classHtm.lookupSequence}'){
//						$('#classification').val('${classificationAfs}');
//					} else {
//						$('#classification').val('${classificationHtm}');
//					}
//				}
//				
//				if ((data.valuationMethodTrade) && (data.valuationMethodHTM)&&(!data.valuationMethodAFS)) {
//					if ('${classTrd.lookupSequence}' < '${classHtm.lookupSequence}'){
//						$('#classification').val('${classificationTrd}');
//					} else {
//						$('#classification').val('${classificationHtm}');
//					}
//				} 
//				
//				if ((data.valuationMethodTrade) && (data.valuationMethodAFS) && (data.valuationMethodHTM)) {
//					if (('${classTrd.lookupSequence}' < '${classAfs.lookupSequence}') && 
//						('${classTrd.lookupSequence}' < '${classHtm.lookupSequence}')){
//						$('#classification').val('${classificationTrd}');
//					}
//					
//					if (('${classAfs.lookupSequence}' < '${classTrd.lookupSequence}') && 
//						('${classAfs.lookupSequence}' < '${classHtm.lookupSequence}')){
//						$('#classification').val('${classificationAfs}');
//					}
//					
//					if (('${classHtm.lookupSequence}' < '${classTrd.lookupSequence}') && 
//						('${classHtm.lookupSequence}' < '${classAfs.lookupSequence}')){
//						$('#classification').val('${classificationHtm}');
//					}
//				}
//				
//			},
//			error: function(data){
//				clearSecurity();
//				$('#securityId').addClass('fieldError');
//				$('#securityDesc').val('NOT FOUND');
//				
//			}
//		},
//		filter : $('#securityType'),
//		help : $('#securityHelp')
//	});
	
	
	
	$('#showPayments').click(function() {
		$('#tabs').tabs('select', 2);
		tabScheduleVisited = true;
		var freq = null;
		
		if ($('#frequency').val() == 'M'){
			freq = 1;
		} else if ($('#frequency').val() == 'Q'){
			freq = 3;
		} else if ($('#frequency').val() == 'H'){
			freq = 6;
		} else if ($('#frequency').val() == 'Y'){
			freq = 12;
		}
		
		var maturityDateCal = new Date($("#maturityDate").datepicker('getDate'));
        var monthMaturity = maturityDateCal.getMonth();
        var yearMaturity = maturityDateCal.getFullYear();
        
        var couponDateCal = new Date($("#depositoDate").datepicker('getDate'));
        var monthCoupon = couponDateCal.getMonth();
        var yearCoupon = couponDateCal.getFullYear();
        var result = ((((yearMaturity-yearCoupon)*12+(monthMaturity-monthCoupon))/freq)+1);
        
        $('#totalCoupon').valueRnd(result, true, 0, "ROUNDUP");
	});
	
	$('#resetCharge').click(function() {
		if (($('#accountKey').val()=='') || ($('#securityType').val()=='')||($('#securityKey').val()=='') || ($('#nominalStripped').val()=='')){
			$('#errGlobal').html("Please check Account No, Security Type, Security Code and Nominal. Make sure all of them are not empty !");
			return false;
		}
		getCharges();
		$('#isreload').val(true);
	});
	
	
	$('#chargeKey').lookup({
		list : '@{Pick.chargesForTransaction()}',
		get : function() {
			$.post('@{DepositoPlacements.charge()}',
					{
						'code': $('#chargeKey').val(),
						'quantity':$('#nominalStripped').val(),
						'nominal':$('#nominalStripped').val(),
						'custodyAccountKey':$('#accountKey').val()
					},
					function(response, status, xhr) {
			    		checkRedirect(response);
						chargeGrid.fnAddData(response);
//						$('#chargeList tbody .mask').autoNumeric({vMin:'-999999999999.'});
						var rowlength =  $("#chargeList tbody tr").length;
						var tr = $("#chargeValue\\["+(rowlength-1)+"\\]").parent().parent();
						$('.mask', tr).autoNumeric({vMin:'-999999999999.'});
						calculate();
						if($('#taxKey').val() != '' && $('#taxKey').val() != null ){
							getTaxAmount();
						}
					}
				);
		},
		help : $('#addCharge')
	});
	
	// NOMINAL
	$("#nominal").live('blur', function() {
//		checkNominal();
	});
	
	
	// INTEREST RATE
	$('#interestRate').live('blur', function(){
		calculate();
		if($('#taxKey').val() != '' && $('#taxKey').val() != null ){
			getTaxAmount();
		} 
	});
	
	$('#taxAmount').live('blur', function(){
		
		var gros = Number($('#accruedInterest').autoNumericGet());
		var amount = $(this).autoNumericGet();
		
//		var amount = (Number(rate)/Number(100)) * Number(gros);
		var net = Number(gros) - Number(amount);
		$('#discountNet').autoNumericSet(net);
		$('#discountNetStripped').val(net);
		calculate();
	});
	
	// MATURITY DATE
	$('#maturityDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends
	});
	
	$('#maturityDate').change(function() {
		if (!($(this).hasClass('fieldError'))) {
			calculate();
		}
	});
	
});

function disableCharge() {
	$('#chargeList tbody input').each(function() {
		$(this).attr('disabled', 'disabled');
	});
	$('#addCharge').button({disabled: true});
	reloadCharge = true;
}

function checkNominal(){
	var minTrxQuantity = parseFloat($('#minTrxQuantity').val());
	var nominal = parseFloat($('#nominalStripped').val());
	
	if ($(this).val()!=''){
		if (nominal < minTrxQuantity){
			$('#nominal').addClass('fieldError');
			$('#errNominal').html("Can not less than Min Trx Quantity in Security Code " + ($('#securityId').val()));
			return false;
		} else {
			$('#nominal').removeClass('fieldError');
			$('#errNominal').html("");
			calculate();
		}
	}
}

function clearDeposito(){
	$('#depositoNo').removeClass("fieldError");
	$('#depositoNo').val('');
	$('#depositoNominal').val('');
	$('#nominal').val('');
	$('#nominalStripped').val('');
	$('#depositoKey').val('');
	$('#branchCode').val('');
	$('#h_branchCode').val('');
	
	$('#interestRate').val('');
	$('#interestRateStripped').val('');
	$('#accruedInterest').val('');
	$('#accruedInterestStripped').val('');
	$('#taxCode').val('');
	$('#taxKey').val('');
	$('#taxRate').val('');
	$('#taxRateHidden').val('');
	$('#taxAmount').val('');
	$('#taxAmountStripped').val('');
	$('#discountNet').val('');
	$('#discountNetStripped').val('');
	$('#totalCharges').val('');
	$('#totalChargesStripped').val('');
	$('#settlementAmount').val('');
	$('#settlementAmountStripped').val('');
	$('#accrualBase').val('');
	$('#accrualBaseCodeHidden').val('');
	$('#accrualBaseHidden').val('');
	$('#yearBase').val('');
	$('#yearBaseCodeHidden').val('');
	$('#yearBaseHidden').val('');
	$('#accruedDays').val('');
	$('#accruedDaysStripped').val('');
	$('#effectiveDate').val('');
	$('#effectiveDateHidden').val('');
	$('#maturityDate').val('');
	$('#maturityDateHidden').val('');
	$('#interestFrequencyDesc').val('');
	$('#interestFrequencyId').val('');
	$('#lastPaymentDate').val('');
	$('#lastPaymentDateHidden').val('');
	$('#nextPaymentDate').val('');
	$('#nextPaymentDateHidden').val('');
	$('#maturityIns').val('');
	$('#maturityInsHidden').val('');
}

function clearSecurityType(){
	$('#securityType').val('');
	$('#securityTypeDesc').val('');
	$('#h_securityTypeDesc').val('');
	$('#securityClass').val('');
	clearSecurity();
}

function clearSecurity(){
	$('#securityId').removeClass("fieldError");
	$('#securityId').val("");
	$('#securityKey').val("");
	$('#securityDesc').val("");
	$('#h_securityDesc').val("");
	$('#currencyCode').val('');
	$('#currencyCodeHidden').val('');
	
	$('#issuerCode').val('');
	$('#issuerCodeHide').val('');
	$('#issuerCodeKey').val('');
	$('#issuerCodeDesc').val('');
	$('#issuerCodeDescHide').val('');
	
	$('#accrualBase').val('');
	$('#accrualBaseHidden').val('');
	$('#yearBase').val('');
	$('#yearBaseHidden').val('');
	
	$('#frequency').val('');
	$('#frequencyId').val('');
	
	$('#minTrxQuantity').val('');
	$('#isScriptHidden').val('');
	$('#classification').val('');
	
	clearDeposito();
}

function getCharges() {
	$.post('@{DepositoPlacements.loadCharges}', 
		{ 
			'custodyAccountKey':$('#accountKey').val(),
			'securityClass':$('#securityClass').val(),
			'securityType':$('#securityType').val(),
			'transactionTemplateKey':$('#transactionTemplateKey').val(),
			'securityKey':$('#securityKey').val(),
			'quantity':$('#nominalStripped').val(),
			'nominal':$('#nominalStripped').val()
		}, 
		function(response, status, xhr) {
    		checkRedirect(response);
			if (status == 'error') {
				// Error handling here
			} else {
				var charges = response;
				chargeGrid = $('#chargeList').dataTable({
					aaData:response,
					aoColumnDefs: [{bSortable:false,aTargets:[1,2,3,4,5]}],
					aoColumns:
						[
						 	{ 
						 		asSorting:["asc"], mDataProp: "chargeMaster.chargeCode"
						 	},
						 	{ 
						 		fnRender: function(obj) {
						 			var chargeValue = 0;
						 			if (!isNaN(obj.aData.chargeValue)) 
						 				chargeValue = $('#dummy').autoNumericSet(obj.aData.chargeValue).val();
						 			var controls;
						 				controls = '<input type="text" id="chargeValue[' + obj.iDataRow + ']" value="' + chargeValue + '" class="rgNumeric mask" size="15" />';
						 				controls += '<input type="hidden" id="hiddenChargeValue[' + obj.iDataRow + ']" value="' + chargeValue + '" class="rgNumeric mask" size="15" />';
						 				controls += '<input type="hidden" id="chargeValueStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeValue" value="' + obj.aData.chargeValue + '" />';
						 				controls += '<input type="hidden" id="hiddenChargeValueStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeValue" value="' + obj.aData.chargeValue + '" />';
						 			return controls;
						 		} 
						 	},
						 	{ 
						 		fnRender: function(obj) {
						 			var taxAmount = 0;
						 			if (obj.aData.taxAmount && !isNaN(obj.aData.taxAmount)) 
						 				taxAmount = $('#dummy').autoNumericSet(obj.aData.taxAmount, {vMin:'-999999999999.'}).val();
						 			var controls;
						 				controls = '<input type="text" id="taxAmount[' + obj.iDataRow + ']" value="' + taxAmount + '" class="rgNumeric mask" size="15" />';
						 				controls += '<input type="hidden" id="hiddenTaxAmount[' + obj.iDataRow + ']" value="' + taxAmount + '" class="rgNumeric mask" size="15" />';
						 				controls += '<input type="hidden" id="taxAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].taxAmount" value="' + obj.aData.taxAmount + '" />';
						 				controls += '<input type="hidden" id="hiddenTaxAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].taxAmount" value="' + obj.aData.taxAmount + '" />';
						 			return controls;
						 		}
						 	},
						 	{ 
						 		fnRender: function(obj) {
						 			var chargeNetAmt = 0;
						 			if (obj.aData.chargeNetAmount && !isNaN(obj.aData.chargeNetAmount)) 
						 				chargeNetAmt = $('#dummy').autoNumericSet(obj.aData.chargeNetAmount).val();
						 			var controls;
						 			controls = '<input type="text" id="chargeNetAmount[' + obj.iDataRow + ']" value="' +chargeNetAmt+ '" size="15" class="rgNumeric mask" disabled="disabled"/>';
						 			controls += '<input type="hidden" id="hiddenChargeNetAmount[' + obj.iDataRow + ']" value="' + chargeNetAmt + '" class="rgNumeric mask" size="15" />';
					 				controls += '<input type="hidden" id="chargeNetAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeNetAmount" value="' + obj.aData.chargeNetAmount + '" />';
					 				controls += '<input type="hidden" id="hiddenChargeNetAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeNetAmount" value="' + obj.aData.chargeNetAmount + '" />';
						 			return controls;
						 		}
						 	},
						 	{ mDataProp: "chargeFrequency" },
						 	
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
				if($('#taxKey').val() != null && $('#taxKey').val() != ''){
					getTaxAmount();
				}
			}
		});
	
	$('#errGlobal').html("");
}

function calculate(){
	var transaction = new Transaction();
	transaction.securityType = $('#securityType').val();
	transaction.settlementDate = $('#depositoDate').datepicker('getDate');
	transaction.quantity = $('#nominalStripped').val();
	transaction.price = 1;
	transaction.priceUnit = 1;
	transaction.parPrice = $('#parPrice').val();
	transaction.netAmount = $('#nominalStripped').val();
	if ($('#interestFrequencyId').val() == '${intPaymentFreqMonthly}'){
		transaction.lastPaymentDate = new Date($('#lastPaymentDate').datepicker('getDate'));
	} else {
		transaction.lastPaymentDate = new Date($('#effectiveDate').datepicker('getDate'));
	}
	
	transaction.nextPaymentDate = new Date($("#depositoDate").datepicker('getDate'));
	transaction.settlementDate = new Date($("#depositoDate").datepicker('getDate'));
	transaction.interestRate = $('#interestRateStripped').val();
	transaction.frequency = transaction.decodeFrequency($('#frequency').val());
	transaction.accrualBase = transaction.decodeCalculationBase($('#accrualBase').val());
	transaction.yearBase = transaction.decodeCalculationBase($('#yearBase').val());
	transaction.isFraction = $('#isFraction').val();
	transaction.fractionRatioSource = $('#fractionRatioSource').val();
	transaction.fractionRatioTarget = $('#fractionRatioTarget').val();
//	transaction.capitalGainTax = $('#taxRateHidden').val();
	transaction.hasInterest = $('#hasInterest').val();
	transaction.transactionType = 'S';
	transaction.taxOnInterest = $('#taxAmountStripped').val();
	
	var totalCharges = 0;
	var sumOfChargeNetAmount = 0;
	var sumOfTax = 0;
	var nodes = chargeGrid.fnGetNodes();
	for ( var i = 0; i < nodes.length; i++) {
		var node = $(nodes[i]);
		var chargeValue = node.find('[name$="chargeValue"]').val();
		var chargeTax = node.find('[name$="taxAmount"]').val();
		var chargeNet = node.find('#chargeNetAmount');
		var chargeFrequency = node.find('[name$="chargeFrequency"]').val();
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

		$(chargeNet).autoNumericSet(chargeNetAmount, {vMin:'-999999999999.'});
		
		if (chargeFrequency == '${chargeFreqT}') {
			totalCharges += totalChargeValue;
			sumOfChargeNetAmount += chargeNetAmount;
			sumOfTax += totalTaxCharges;
		}
	}
	transaction.totalCharges = totalCharges;
	transaction.sumOfChargeNetAmount = sumOfChargeNetAmount;
	
	$('#totalCharges').autoNumericSet(totalCharges, {vMin:'-999999999999.'});
	$('#totalChargesStripped').val(totalCharges);	
	
	
	transaction.calculateAccruedInterest();
	
	$('#accruedDays').autoNumericSet(transaction.accruedDays);
	$('#accruedDaysStripped').val(transaction.accruedDays);
	
	if(oldAccrued != ""){
		$('#accruedInterest').val(oldAccrued).blur();
		$('#accruedInterestStripped').val(oldAccruedStriped);
	}else{
		$('#accruedInterest').autoNumericSet(transaction.accruedInterest);
		$('#accruedInterestStripped').val(transaction.accruedInterest);
	}
	
	transaction.accruedInterest = $('#accruedInterest').autoNumericGet();
	transaction.calculate();
	$('#settlementAmount').autoNumericSet(transaction.settlementAmount);
	$('#settlementAmountStripped').val(transaction.settlementAmount);
	
}

function getTaxAmount(){
	var rate = Number($('#taxRateHidden').val());
	var gros = Number($('#accruedInterest').autoNumericGet());
	
	var amount = (Number(rate)/Number(100)) * Number(gros);
	if($('#currencyCodeHidden').val() == "${currencyTax}"){
		if ("${currencyTaxRoundType}" == "ROUNDUP") {
			$('#taxAmount').value(amount, true, Number("${currencyTaxRound}"), 'U').blur();  //penjelasan ada di public/javascript/external/Util.js
		}else if ( "${currencyTaxRoundType}" == "ROUNDDOWN") {
			$('#taxAmount').value(amount, true, Number("${currencyTaxRound}"), 'D').blur();  
		}else {
			$('#taxAmount').value(amount, true, Number("${currencyTaxRound}"), 'S').blur();  
		}
		$('#taxAmountStripped').val($('#taxAmount').autoNumericGet());
		
	}else{
		$('#taxAmount').autoNumericSet(amount);
		$('#taxAmountStripped').val(amount);
	}
	
	var net = Number(gros) - Number($('#taxAmountStripped').val());
	$('#discountNet').autoNumericSet(net);
	$('#discountNetStripped').val(net);
	calculate();
}

function gridCoupons(){
    $('.calendar').datepicker();
    gridCouponDataTables();
    $('#gridCoupon').removeAttr('style');

    $('input.numerics').live('blur', function() {
        var el = $(this);
        var id = this.id;
        var stripCoupon= $("input[id='" + id +"StrippedCoupon']");
        stripCoupon.val($.fn.autoNumeric.Strip(id));
    });
    //$('input.numerics').autoNumeric();
}

function gridCouponDataTables() {
    var gridCoupon = $('#gridCoupon').dataTable({
        aoColumns: [
                     null,
                     null,
                     null,
                     null,
                     null,
                     null
                   ],
                   
        aaSorting:[[1,'asc']],
        bAutoWidth: false,  
        bJQueryUI:true,
        bLengthChange:false,
        bFilter:false,
        bPaginate:false,
        bRetrieve:true,
        bInfo:false
    });
}