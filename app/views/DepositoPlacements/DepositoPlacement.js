var chargeGrid;
var scheduleGrid;
var tabChargeVisited = false;
var tabScheduleVisited = false;
var reloadCharge = true;
$(function(){
	
	$('#pbaBankAccountCurrency').val($('#currencyCode').val());
	$('#pbaBankAccountCurrencyHidden').val($('#currencyCode').val());
	
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
		
		$("#cancelRemaks[maxlength]").bind('input propertychange', function() {
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    });
	}
	
	if ('${origin}' == '${cancelDepositoPlacementOriginated}') {
		$('#cancelDepositoPlacement').css("display","");
		$('#addCharge').button("option","disabled", true);
		$('#resetCharge').button("option","disabled", true);
		$('#chargeDone').button("option","disabled", true);
	} else if ('${origin}' == '${depositoPlacementOriginated}') {
		$('#cancelDepositoPlacement').css("display","none");
		
	}
	
	if ('${maintenanceLogKey}' != '') {
		if ('${origin}'=='${cancelDepositoPlacementOriginated}'){
			$('#correction').css("display","none");
			$('#cancelDepositoPlacement').css("display","");
		} else if ('${origin}' == '${depositoPlacementOriginated}'){
			$('#cancelDepositoPlacement').css("display","none");
			$('#correction').show();
			$("#correction").css("display","");	
		}
	} else {
		if (($("input[name='deposito.needCorrection']").val() == 'true') && ($("input[name='deposito.remarksCorrection']").val() != '') &&('${origin}'=='${depositoPlacementOriginated}')) {
			$('#correction').show();
			$("#correction").css("display","");
			$('#needCorrection').attr('disabled', true);
			$('#needCorrection').attr("checked", true);
			$('#remarkCorrection').attr('disabled', true);
		}
	}
	
	if('${deposito.considerHoliday}'== 'true' || '${deposito.considerHoliday}'== true){
		$('#considerHoliday').val(true);
	} else if('${deposito.considerHoliday}'== 'false' || '${deposito.considerHoliday}'== false){
		$('#considerHoliday').val(false);
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
		if ($('#paymentFreqHidden').val()=='${intPaymentFreqMonthly}'){
			$('#tabs').tabs('select', 2);
		} else {
			$('#tabs').tabs('select', 3);
		}
	});
	
	$('#next3').click(function() {
		$('#tabs').tabs('select', 3);
	});
	
	$('#next4').click(function() {
		$('#tabs').tabs('select', 4);
	});
	
	$('#prev2').click(function() {
		$('#tabs').tabs('select', 0);
	});
	
	$('#prev3').click(function() {
		$('#tabs').tabs('select', 1);
	});
	
	$('#prev4').click(function() {
		if ($('#paymentFreqHidden').val()=='${intPaymentFreqMonthly}'){
			$('#tabs').tabs('select', 2);
		} else {
			$('#tabs').tabs('select', 1);
		}
	});
	
	$('#prev5').click(function() {
		$('#tabs').tabs('select', 3);
	});
	
	if (('${mode}'=='edit')&&('${confirming}'!='true')){
		$('#placementDate').attr('disabled', 'disabled');
		$('#cancelPlacementDate').attr('disabled', 'disabled');
	}

	// PLACEMENT DATE
	$('#placementDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends
	});
	
	$('#placementDate').change(function() {
		if (($(this).val()!='') && ($('#maturityDate').val()!='')){
			var date1 = new Date($(this).datepicker('getDate'));
			var date2 = new Date($('#maturityDate').datepicker('getDate'));
			
			if (date1.getTime() > date2.getTime()){
				$(this).addClass('fieldError');
				$('#placementDateError').html("Can not greater than Maturity Date");
			} else {
				$(this).removeClass('fieldError');
				$('#placementDateError').html("");
				calculateTotalCoupon();
				$('#gridCoupon').dataTable().fnClearTable();
			}
			
			getIntFreq($(this).val(),$('#maturityDate').val());
		}
		
		if (!($(this).hasClass('fieldError'))) {
			if (($('#maturityDate').val()!='') && ($('#frequency').val()!='')) {
				calculate();
			}
		}
		
		$('#placementDateHidden').val($(this).val());
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
		$('#bankAccountBeneficiary').val(data.bankAccountDesc);
		$('#h_bankAccountBeneficiary').val(data.bankAccountDesc);
		$('#bankAccountCurrency').val(data.bankCodeCurrency);
		$('#h_bankAccountCurrency').val(data.bankCodeCurrency);
		$('#customerKey').val(data.customerKey);
		
		var pbaDec = $('#securityType').val()+" "+data.description;
		$('#pbaBankAccountDescription').val(pbaDec);
		
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
						$('#bankAccountBeneficiary').val(data.name);
						$('#h_bankAccountBeneficiary').val(data.name);
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
			
			clearSecurity();
			attachSecuritiesPaging();
		}
	});

//	$('#depositoTemplate').val('${depositoTemplatePlacement}');
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
				
				var pbaDec = $('#securityType').val()+" "+$('#h_accountName').val();
				$('#pbaBankAccountDescription').val(pbaDec);
				
				clearSecurity();
				attachSecuritiesPaging();
				
				$('#pbaBankCode').removeClass('fieldError');
				$('#pbaBankCodeName').val('');
				$('#pbaBankCode').val('');
				$('#pbaBankCodeKey').val('');
				$('#h_pbaBankCodeName').val('');
				
				$.get("@{DepositoPlacements.getDefPaymentFreq()}", {'securityType':$('#securityType').val()}, function(dataFreq){
						$('#paymentFreqHidden').val(dataFreq);
						if (dataFreq == '${intPaymentFreqMonthly}'){
							$("#tabs").tabs("enable",2);
							$('#paymentFreq2').attr('checked', true);
							if (('${confirming}'!='true')&&('${mode}'!='view')){
								$('#showPayments').attr('disabled', false);
							}
							$('#gridCoupon').dataTable().fnClearTable();
						} else {
							$('#paymentFreq1').attr('checked', true);
							$('#paymentFreqHidden').val($('#paymentFreq1').val());
							$('#showPayments').attr('disabled', true);
							$("#totalCoupon").val('');
							$('#gridCoupon').dataTable().fnClearTable();
							//$("#tabs").tabs("option", "active", 0);
							$("#idtab1").trigger('click');
							$("#tabs").tabs("disable",2);
						}
					});
				
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
		filter:'${depositoTemplatePlacement}',
		help:$('#securityTypeHelp'),
		nextControl:$('#securityId')
	});
	
	$('#securityId').change(function() {
		if ($(this).val() == "") {
			clearSecurity();
		}
	});
	
	$('#pbaBankCode').dynapopup2({key:'pbaBankCodeKey', help:'pbaBankCodeHelp', desc:'pbaBankCodeName'},'PICK_GN_THIRD_PARTY', "THIRD_PARTY-BANK", "null", 
			function(data){
				if (data) {
					$('#pbaBankCode').removeClass('fieldError');
					$('#pbaBankCodeKey').val(data.code);
					$('#pbaBankCodeName').val(data.description);
					$('#h_pbaBankCodeName').val(data.description);
					$("#pbaBankCodeError").html("");
				}
			},
			function(data){
				$('#pbaBankCode').addClass('fieldError');
				$('#pbaBankCodeName').val('NOT FOUND');
				$('#pbaBankCode').val('');
				$('#pbaBankCodeKey').val('');
				$('#h_pbaBankCodeName').val('');
			}
	);
	
	function attachSecuritiesPaging() {
		var securityType = $('#securityType').val();
		var pickName = (securityType == '') ? 'PICK_SC_MASTER_TD' : 'PICK_SC_MASTER_BY_SEC_TYPE';
		$('#securityId').dynapopup2({key:'securityKey', help:'securityHelp', desc:'securityDesc'}, pickName, securityType, 'branchCode', function(sdata){
			var data = $().fetchSync("@{Pick.security()}", {'code':$('#securityId').val(), 'filter':$('#securityType').val()});
			$('#securityId').removeClass('fieldError');
			$('#securityKey').val(data.code);
			$('#securityDesc').val(data.description);
			$('#h_securityDesc').val(data.description);
			
			$('#currencyCode').val(data.securityCurrency);
			$('#currencyCodeHidden').val(data.securityCurrency);
			
			$('#pbaBankAccountCurrency').val(data.securityCurrency);
			$('#pbaBankAccountCurrencyHidden').val(data.securityCurrency);
			
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
			$('#minTrxQuantity').val(data.minTrxQuantity);
			$('#isScriptHidden').val(data.isScript);
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
			
			$.get("@{DepositoPlacements.getDataIssuer()}", {'key':$('#issuerCodeKey').val()}, function(dataPbaBank){
				$('#pbaBankCode').val(dataPbaBank).blur();
				if (dataPbaBank == ''){
					$('#pbaBankCode').removeClass('fieldError');
					$('#pbaBankCodeName').val('');
					$('#pbaBankCode').val('');
					$('#pbaBankCodeKey').val('');
					$('#h_pbaBankCodeName').val('');
				}
			});
			
		},function(data){
			clearSecurity();
			$('#securityId').addClass('fieldError');
			$('#securityDesc').val('NOT FOUND');
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
	
	
	// IS SCRIPT
	$('input[name="isScript"]').change(function(){
		$('input[name="deposito.script"]').val($('input[name="isScript"]:checked').val());
	});
	
	$('#showPayments').attr('disabled', true);
	//$("#tabs").tabs("option", "active", 0);
	$("#tabs").tabs("disable",2);

	if ('${deposito?.interestFrequency?.lookupId}' == '${intPaymentFreqMonthly}'){
		$("#tabs").tabs("enable",2);
		$('#paymentFreq2').attr('checked', true);
		if (('${confirming}'!='true')&&('${mode}'!='view')){
			$('#showPayments').attr('disabled', false);
		}
	} else {
		$('#paymentFreq1').attr('checked', true);
	}
	
	$('input[name=paymentFreq]').change(function(){
		if ($('#paymentFreq1').attr('checked')){
			$('#paymentFreqHidden').val($('#paymentFreq1').val());
			$('#showPayments').attr('disabled', true);
			$("#totalCoupon").val('');
			$('#gridCoupon').dataTable().fnClearTable();
			$("#tabs").tabs("option", "active", 0);
			$("#idtab1").trigger('click');
			$("#tabs").tabs("disable",2);
		}else{
			$('#paymentFreqHidden').val($('#paymentFreq2').val());
			$('#showPayments').attr('disabled', false);
			$('#gridCoupon').dataTable().fnClearTable();
			$("#tabs").tabs("enable",2);
			if (($('#maturityDate').val()!='') && ($('#placementDate').val()!='')){
				calculateTotalCoupon();
			}
		}
	});
	
	$('#showPayments').click(function() {
		$('#tabs').tabs('select', 2);
		tabScheduleVisited = true;
		
		if (($('#maturityDate').val()!='') && ($('#placementDate').val()!='')){
			calculateTotalCoupon();
			$('#gridCoupon').dataTable().fnClearTable();
		}
		
	});
	
	function disabledPlacemenBank(isDisabled){
		if('${mode}' == 'view' || ${readOnly}) isDisabled=${readOnly};
		$('#pbaBankAccount').attr('disabled', isDisabled);
		/*$('#pbaBankCode').attr('disabled', isDisabled);
		$('#pbaBankCodeHelp').attr('disabled', isDisabled);
		$('#pbaBankCodeName').attr('disabled', isDisabled);*/
		$('#pbaBankAccountBeneficiary').attr('disabled', isDisabled);
		//$('#pbaBankAccountCurrency').attr('disabled', isDisabled);
		//$('#pbaBankAccountBranch').attr('disabled', isDisabled);
		//$('#pbaBankAccountDescription').attr('disabled', isDisabled);
	}
	
	$('#paymentMethod').change(function(){
		$('#paymentMethodHidden').val($(this).val());
		if($('#paymentMethod').val() != '${paymentManual}'){
			disabledPlacemenBank(false);
			$('#pbaAcc label').html("Account. No <span class=\"req\">*</span>");
			$('#pbaBenf label').html("Beneficiary Name <span class=\"req\">*</span>");
		}else{
			disabledPlacemenBank(true);
			$('#pbaAcc label').html("Account. No <span class=\"req\"></span>");
			$('#pbaBenf label').html("Beneficiary Name <span class=\"req\"></span>");
			$('#pbaBankAccount').val("");
			$('#pbaBankAccountBeneficiary').val("");
		}
	});
	
	$('#branchCode').change(function(){
		$('#pbaBankAccountBranch').val($(this).val());
	});	
	
	$('#paymentMethod').trigger('change');
	
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
						$('#chargeKey').val('');
					}
				);
		},
		help : $('#addCharge')
	});
	
	// NOMINAL
	$("#nominal").live('blur', function() {
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
	});
	
	// INTEREST RATE
	$('#interestRate').live('blur', function(){
		calculate();
	});
	
	// MATURITY DATE
	$('#maturityDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends
	});
	
	$('#maturityDate').change(function() {
		if (($(this).val()!='') && ($('#placementDate').val()!='')) {
			var date1 = new Date($('#placementDate').datepicker('getDate'));
			var date2 = new Date($(this).datepicker('getDate'));
			
			if (date2.getTime() < date1.getTime()){
				$(this).addClass('fieldError');
				$('#maturityDateError').html("Can not less than Placement Date");
			} else {
				$(this).removeClass('fieldError');
				$('#maturityDateError').html("");
				calculateTotalCoupon();
				$('#gridCoupon').dataTable().fnClearTable();
			}

			getIntFreq($('#placementDate').val(),$(this).val());
		}
		
		if (!($(this).hasClass('fieldError')) && ($('#frequency').val()!='')) {
			calculate();
		}
	});
	
	$("#totalCoupon").change(function () {
		if ( $(this).val()=='0') {
			$(this).addClass('fieldError');
			$('#totalCouponError').html("not allowed!");
			return false;
		} else {
			$(this).removeClass('fieldError');
			$('#totalCouponError').html("");
			$('#gridCoupon').dataTable().fnClearTable();
		}
	});
	
	$('#considerHoliday').change(function(){
		if ($(this).is(":checked")){
			$(this).val(true);
		} else {
			$(this).val(false);
		}
	});
	
	 $("#generateButton").click(function() {
		 if (($('#placementDate').val()=='')||($('#maturityDate').val()=='')||($('#interestRateStripped').val()=='')||
			 ($('#nominalStripped').val()=='')||($('#accrualBase').val()=='')||($('#yearBase').val()=='')||
			 (($('#totalCoupon').val()=='')&&(!$('#totalCoupon').hasClass('fieldError')))||($('#securityKey').val()=='')) {
			 $('#generateButtonError').html('Please check Placement Date, Security Code, Maturity Date, Nominal, Interest Rate(gross), Accrual Type, No. of int. payment. Make sure all of them are not empty!');
			 return false;
		 }
		 $('#generateButtonError').html('');
		 $.post('@{DepositoPlacements.populatePaymentSchedule()}', 
					{ 
					 	'placementDate':$('#placementDate').val(),
						'maturityDate':$('#maturityDate').val(),
						'nominal':$('#nominalStripped').val(),
						'interestRate':$('#interestRateStripped').val(),
						'accrualBase':$('#accrualBase').val(),
						'yearBase':$('#yearBase').val(),
						'totalCoupon':$('#totalCoupon').val(),
						'considerHoliday':$('#considerHoliday').val(),
						'freqSecurity':$('#frequency').val()
					}, function(data) {

			    		checkRedirect(data);
						$('#paymentSchedule').html(data);
						
					});
     });
});

function disableCharge() {
	$('#chargeList tbody input').each(function() {
		$(this).attr('disabled', 'disabled');
	});
	$('#addCharge').button({disabled: true});
	reloadCharge = true;
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
						 				controls = '<input type="text" id="chargeValue[' + obj.iDataRow + ']" value="' + chargeValue + '" class="rgNumeric mask" size="15"/>';
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
			}
		});
	
	$('#errGlobal').html("");
}



function calculate(){
	var transaction = new Transaction();
	transaction.securityType = $('#securityType').val();
	transaction.settlementDate = $('#placementDate').datepicker('getDate');
	transaction.quantity = $('#nominalStripped').val();
	transaction.price = 1;
	transaction.priceUnit = 1;
	transaction.netAmount = $('#nominalStripped').val();
	transaction.lastPaymentDate = new Date($('#placementDate').datepicker('getDate'));
	transaction.nextPaymentDate = new Date($("#maturityDate").datepicker('getDate'));
	transaction.settlementDate = new Date($("#maturityDate").datepicker('getDate'));
	transaction.interestRate = $('#interestRateStripped').val();
	transaction.frequency = transaction.decodeFrequency($('#frequency').val());
	transaction.accrualBase = transaction.decodeCalculationBase($('#accrualBase').val());
	transaction.yearBase = transaction.decodeCalculationBase($('#yearBase').val());
	transaction.isFraction = $('#isFraction').val();
	transaction.fractionRatioSource = $('#fractionRatioSource').val();
	transaction.fractionRatioTarget = $('#fractionRatioTarget').val();
	transaction.capitalGainTax = $('#taxRate').val();
	
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
	console.debug("TOTAL CHARGES = "+totalCharges);
	$('#totalCharges').autoNumericSet(totalCharges, {vMin:'-999999999999.'});
	$('#totalChargesStripped').val(totalCharges);	
	
	transaction.calculateAccruedInterest();
	transaction.calculate();
	
	$('#accruedDays').autoNumericSet(transaction.accruedDays);
	$('#accruedDaysStripped').val(transaction.accruedDays);
	
	$('#accruedInterest').autoNumericSet(transaction.accruedInterest);
	$('#accruedInterestStripped').val(transaction.accruedInterest);
	if ($('#accruedInterestStripped').val()=='NaN'){
		$('#accruedInterestStripped').val('');
	}
	$('#settlementAmount').autoNumericSet(transaction.settlementAmount);
	$('#settlementAmountStripped').val(transaction.settlementAmount);
	
}

function gridCoupons(){
    $('.calendar').datepicker();
    gridCouponDataTables();
    $('#gridCoupon').removeAttr('style');

    onChangeNextCpnDate();
    
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
                    //null,
//                    {bVisible:false},
//                    {sType: "numeric" },
                     null,
                     null,
                     null,
                     null,
                     null,
                     null
                   ],
                   
        aaSorting:[[0,'asc']],
        //aoColumnDefs: [{asSorting:[""], aTargets:[8]}, {asSorting:[""], aTargets:[0,1,2,3,4,5,6,7]}],
        //sScrollY :"360px",
        //bScrollCollapse : true,
        bAutoWidth: false,  
        bJQueryUI:true,
        bLengthChange:false,
        bFilter:false,
        bPaginate:false,
        bRetrieve:true,
        //bSort:false,
        //iDisplayLength:10,
        bInfo:false
    });
}

function getIntFreq(placeDate, maturityDate){
	if(placeDate!=null && maturityDate !=null){
		 $.post('@{DepositoPlacements.getIntFrequency()}',
					{
						'pDate':placeDate,
						'mDate':maturityDate
					}, function(data) {
			    		checkRedirect(data);
			    		if(data.success!=""){
			    			$("input[name='paymentFreq'][value='"+data.diff+"']").attr('checked',true).trigger('change');
			    		}
			    		
		    });
	}else{
		console.log("else");
	}
}

function calculateTotalCoupon() {
	// FREQ MONTHLY
	var freq = 1;
	
	var maturityDateCal = new Date($("#maturityDate").datepicker('getDate'));
	var monthMaturity = maturityDateCal.getMonth();
    var yearMaturity = maturityDateCal.getFullYear();
    
    var couponDateCal = new Date($("#placementDate").datepicker('getDate'));
    
    console.log("maturityDateCal = " +maturityDateCal);
    console.log("couponDateCal = " +couponDateCal);
    var monthCoupon = couponDateCal.getMonth();
    var yearCoupon = couponDateCal.getFullYear();
    console.log("yearMaturity = " +yearMaturity);
    console.log("yearCoupon = " +yearCoupon);
    console.log("monthMaturity = " +monthMaturity);
    console.log("monthCoupon = " +monthCoupon);
    console.log("1 = " +(yearMaturity-yearCoupon)*12);
    console.log("2 = " +(monthMaturity-monthCoupon));
    var a = (yearMaturity-yearCoupon)*12;
    var b = (monthMaturity-monthCoupon);
    var c = Number(a) + Number(b);
    var d = Number(c) / Number(freq);
    
    console.log("a = " +a);
    console.log("b = " +b);
    console.log("c = " +a);
    console.log("d = " +d);
    console.log("date placement = " +couponDateCal.getDate());
    console.log("date maturity = " +maturityDateCal.getDate());
    
    var result = 0;
    
    
    result = ((((yearMaturity-yearCoupon)*12+(monthMaturity-monthCoupon))/freq));
    
//    if ((monthMaturity==monthCoupon)&&(maturityDateCal.getDate() > couponDateCal.getDate())){
    if ((maturityDateCal.getDate() > couponDateCal.getDate())){
    	result = result + 1;
    }
	
	
    $('#totalCoupon').val(result);
}


function onChangeNextCpnDate(){
    $("input.coupon").mask("${appProp?.dateMask}", { placeholder:" " });
    $('input.coupon').datepicker({dateFormat:'${appProp?.jqueryDateFormat}'});
    $('input.coupon').change( function(){
    	
        var el = $(this);
        var dateVal = el.val();
        var id = this.id;
        var errorId= "#couponError";
        //el.removeClass("fieldError");
        $(errorId).html("");
        //$(el.next()).html("");

        var elementCouponDate = $(this);
        var rowCouponDate = elementCouponDate.parents("tr");
        var newRowCoupon = elementCouponDate.parents("tr").next();
        
        var indexTable = id.split("[")[1].split("]")[0];
        /*var idDays = "input[id*='days[" + indexTable + "]']";
        var idDaysHidden = "input[id*='daysHidden[" + indexTable + "]']";
        var idGrossInterest = "input[id*='grossInterest[" + indexTable + "]']";
        var idGrossInterestHidden = "input[id*='grossInterestHidden[" + indexTable + "]']";
        var idPaymentDate = "input[id*='paymentDate[" + indexTable + "]']";
        var idPaymentDateHidden = "input[id*='paymentDateHidden[" + indexTable + "]']";*/
        var startDate = $("input[id*='lastPaymentDate["+indexTable+"]']");
        var endDate = $("input[id*='nextPaymentDate["+indexTable+"]']");
        var startDateVal = startDate.val();
        var dateFormatCompare = "dd/mm/yyyy";
        var dateFormatCompareCoupon = "yyyy/mm/dd";
        
        var dtFrmSelectNextDate = new Date(el.datepicker('getDate')).format(dateFormatCompare);
        
        var indexRowTable = elementCouponDate.parents("tr").index();

        var total = parseInt($("#totalCoupon").val());
    	var idx = parseInt(indexRowTable) + 1;
    	var d = dtFrmSelectNextDate.split("/")[0];
    	var m = dtFrmSelectNextDate.split("/")[1];
    	var y = dtFrmSelectNextDate.split("/")[2];

    	var lastDate = m + "/" + d + "/" + y;
    	var dt = new Date(lastDate);

    	for (x = idx; x <= total; x++) {
    		$("#gridCoupon tbody tr:nth-child(" + (x+1) +")").find("input[id*='lastPaymentDate']").val(dt.format(dateFormatCompare));
    	    dt.setMonth(dt.getMonth() + 1);
    	    $("#gridCoupon tbody tr:nth-child(" + (x+1) +")").find("input[id*='nextPaymentDate']").val(dt.format(dateFormatCompare));
    	    var pyDate = $.datepicker.formatDate("yymmdd", dt);
    	    var fixDate = getPaymentDate(pyDate, 0);
	        var fixPaymentDate = new Date(fixDate);
    	    $("#gridCoupon tbody tr:nth-child(" + (x+1) +")").find("input[id*='paymentDate']").val(fixPaymentDate.format(dateFormatCompare));
    	}

        try {
            $.datepicker.parseDate('${appProp?.jqueryDateFormat}', dateVal, null);
        } catch(error) {
            el.addClass("fieldError");
            $(errorId).html("* Invalid date format").show();
        }
    });

	function processNextPaymentDate(nextPaymentRow, selectedPaymentRow) {
	    var childPaymentRowNode = Number(1);
	    var dateFormatCompare = "yyyy/mm/dd";
	    
	    var currentSelectedPaymentDate = $("input[id*='paymentDate']", selectedPaymentRow.children(childPaymentRowNode));
	    var frmtCurrentSelectedPaymentDate = new Date(currentSelectedPaymentDate.datepicker('getDate')).format(dateFormatCompare);
	    
	    var advancePaymentDate = $("input[id*='paymentDate']", nextPaymentRow.children(childPaymentRowNode));
	    var frmtAdvancePaymentDate = new Date(advancePaymentDate.datepicker('getDate')).format(dateFormatCompare);
	    
	    if (frmtCurrentSelectedPaymentDate > frmtAdvancePaymentDate) {
	        $(advancePaymentDate).addClass('fieldError');
	    } else {
	        $(advancePaymentDate).removeClass('fieldError');
	    }
	}

	$(".payDate").change(function(){
	    var dateFormatCompare = "yyyy/mm/dd";
	    var elementPaymentDate = $(this);
	    var rowPaymentDate = elementPaymentDate.parents("tr");
	    var newRowPayment = elementPaymentDate.parents("tr");
	    var currentPayment = $(rowPaymentDate).children(0).html();
	    var childNodePayment = Number(1);
	
	    var selectPayment = $(rowPaymentDate).find("input[id*='paymentDate']");
	    var dtFrmSelectPayment = new Date(selectPayment.datepicker('getDate')).format(dateFormatCompare);
	
	    // compare current changed payment date with all next payment date
	    while (true) {
	        newRowPayment = newRowPayment.next();
	        if (newRowPayment.children().length == 0) {
	            break;
	        } else {
	            processNextPaymentDate(newRowPayment, rowPaymentDate);
	        }
	    }
	
	    // compare changed payment date with same row end date
	    var currentNextPaymentDate = $(rowPaymentDate).find("input[id*='nextPaymentDate']");
	    var dtFrmNextPaymentDate = new Date(currentNextPaymentDate.datepicker('getDate')).format(dateFormatCompare);
	    if (dtFrmSelectPayment < dtFrmNextPaymentDate) {
	        $(selectPayment).addClass('fieldError');
	    } else {
	        $(selectPayment).removeClass('fieldError');
	    }
	});
	
	function processRow(nextRow, selectRow) {
	    var childProcessRowNode = Number(1);
	    var dateFormatCompare = "yyyy/mm/dd";
	    
	    var maturityDateVal = new Date($("#maturityDate").datepicker('getDate'));
	    var dtFrmtMaturityDateVal = maturityDateVal.format(dateFormatCompare);
	    
	    var currentNextPaymentDate = $("input[id*='nextPaymentDate']", selectRow.children(childProcessRowNode));
	    var dtCurrentNextPayment = new Date(currentNextPaymentDate.datepicker('getDate'));
	    var frmtCurrentNextPayment = dtCurrentNextPayment.format(dateFormatCompare);
	
	    var advNextPaymentDate = $("input[id*='nextPaymentDate']", nextRow.children(childProcessRowNode));
	    var dtAdvNextPayment = new Date(advNextPaymentDate.datepicker('getDate'));
	    var frmtAdvNextPayment = dtAdvNextPayment.format(dateFormatCompare);
	
	    if ((frmtCurrentNextPayment > frmtAdvNextPayment) || (frmtAdvNextPayment > dtFrmtMaturityDateVal)) {
	    	$(advNextPaymentDate).addClass('fieldError');
	    } else {
	    	$(advNextPaymentDate).removeClass('fieldError');
	    }
	}
	
	$(".nextDate").bind("keyup change", function(e){
		    
	    var dateFormatCompare = "yyyy/mm/dd";
	    var element = $(this);
	    var id = this.id;
	    var row = element.parents("tr");
	    var newRow = element.parents("tr");
	    var current = $(row).children(0).html();
	    var childNode = Number(1);
	    var dateFormat1 = "dd/mm/yyyy";
	    var dtFrmSelectNextDate = new Date(element.datepicker('getDate')).format(dateFormat1);
	   
	    while(true) {
	        newRow = newRow.next();
	        if (newRow.children().length == 0) {
	            break;
	        }else{
	            processRow(newRow, row);
	        }
	    }
	    var closeDialogMessage = function() {
			$("#dialog-message").dialog('close');
			return false;
		};
	    var selectDate = $(row).find("input[id*='nextPaymentDate']");
	    var dtSelect = new Date(selectDate.datepicker('getDate'));
	    var dtFrmtSelect = dtSelect.format(dateFormatCompare);
	    
	    var maturityDateVal = new Date($("#maturityDate").datepicker('getDate'));
	    var dtFrmtMaturityDateVal = maturityDateVal.format(dateFormatCompare);
	    
	    var indexTable = id.split("[")[1].split("]")[0];
	    var startDate = $("input[id*='lastPaymentDate["+indexTable+"]']");
        var dateSelectStartDate = new Date(startDate.datepicker('getDate')).format(dateFormat1);
        
        var idDays = "input[id*='days[" + indexTable + "]']";
        var idDaysHidden = "input[id*='daysHidden[" + indexTable + "]']";
        var idGrossInterest = "input[id*='grossInterest[" + indexTable + "]']";
        var idGrossInterestHidden = "input[id*='grossInterestHidden[" + indexTable + "]']";
        var idPaymentDate = "input[id*='paymentDate[" + indexTable + "]']";
        var idPaymentDateHidden = "input[id*='paymentDateHidden[" + indexTable + "]']";
       
	    // compare current end date with next start date
	    var nextRow = row.next();
	    var lastPaymentDate = undefined;
	    var dtLastPayment = undefined;
	    var dtFrmtLastPayment = undefined;
	
	    if (nextRow != undefined && nextRow.length > 0) {
	        lastPaymentDate = $("input", nextRow.children(childNode));
	        //dtLastPayment = new Date(lastPaymentDate.datepicker('getDate'));
	        dtLastPayment =  $.datepicker.parseDate('${appProp?.jqueryDateFormat}', lastPaymentDate.val(), null);
	        dtFrmtLastPayment = dtLastPayment.format(dateFormatCompare);
	    }
	
	    // compare current end date with same row start date
	    var currentLastPaymentDate = $(row).find("input[id*='lastPaymentDate']");
	    //var dtFrmtCurrentLastPaymentDate = new Date(currentLastPaymentDate.datepicker('getDate')).format(dateFormatCompare);
	    var dtFrmtCurrentLastPaymentDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', currentLastPaymentDate.val(), null).format(dateFormatCompare);
	
	    var holiday = 'false';
	    if ($('#considerHoliday').val()=='true'){
		    $.post('@{DepositoPlacements.checkHoliday()}?date='+dtFrmSelectNextDate , 
					$('#form').serialize(), function(data) {
	    		checkRedirect(data);
				holiday = data;
			});
	    }
	    
	    if ((holiday =='true')||(dtFrmtSelect < dtFrmtCurrentLastPaymentDate) || (dtFrmtSelect > dtFrmtLastPayment) || (dtFrmtSelect > dtFrmtMaturityDateVal)) {
	    	if (holiday =='true'){
	    		messageAlertOk("Date is Holiday", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
	    	}
	        $(selectDate).addClass('fieldError');
//	        return false;
	    } else {
	        $(selectDate).removeClass('fieldError');
	        $.post('@{DepositoPlacements.onChangeNextPaymentDate()}',
					{
						'startDate':startDate.val(),
						'endDate':dtFrmSelectNextDate,
						'nominal':$('#nominalStripped').val(),
						'interestRate':$('#interestRateStripped').val(),
						'accrualBase':$('#accrualBase').val(),
						'yearBase':$('#yearBase').val(),
						'considerHoliday':$('considerHoliday').val(),
						'frequency':$('#frequency').val()
						
					}, function(data) {
			    		checkRedirect(data);
		//        	$(".nextDate").parents("tr").find(idPaymentDate).val(data);
						if (data.status=='success') {
							$(".nextDate").parents("tr").find(idDays).val(data.days);
							$(".nextDate").parents("tr").find(idDaysHidden).val(data.days);
							$(".nextDate").parents("tr").find(idGrossInterest).autoNumericSet(data.grossInterest);
							$(".nextDate").parents("tr").find(idGrossInterestHidden).val(data.grossInterest);
							$(".nextDate").parents("tr").find(idPaymentDate).val(data.paymentDate);
							$(".nextDate").parents("tr").find(idPaymentDateHidden).val(data.paymentDate);
						} else {
							el.addClass("fieldError");
							/*messageAlertOk(data.message, "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
							return false;*/
						}
		    });
	    }
	});
     
}
//End Of function when change Next Coupon Date -----//

/*function doSave(){
	 	var checkClass = $("#gridCoupon tbody tr").find("input[id*='nextPaymentDate']").hasClass('fieldError');
	 	
	 	if (checkClass){
	 		$('#tabs').tabs('select', 2);
	 		return false;
	 	}
	 	
	 	
	 	return true;
}*/