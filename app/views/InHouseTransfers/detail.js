$(function(){
	inHouseTransfers();
	$('.button').css("display","none")
	$('.buttons input:button').button();
	$('.button').button();
	$('#accordion').accordion();
	$('#accordion2').accordion();
	//$('#targetTransaction').lookup({
	//	list:'@{Pick.transactionTemplates()}',
	//	get:'@{Pick.transactionTemplate()}',
	//	key:$('#targetTransactionKey'),
	//	filter:'$transaction',
	//	description:$('#targetTransactionDesc'),
	//	help:$('#targetTransactionHelp'),
	//});
	$('#copyButton').attr("disabled","disabled");

	$('#paymentType').lookup({
		list:'@{Pick.lookups()}?group=PAYMENT_TYPE',
		get:'@{Pick.lookup()}?group=PAYMENT_TYPE',
		key:$('#paymentTypeId'),
		description:$('#paymentTypeDesc'),
		help:$('#paymentTypeHelp'),
		nextControl:$('#amount')
	});

	$('#settlementDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends
	});
	$('.calendar').datepicker();
	$("#securityId").change(function(){
		process();
	});
	$("#transactionCode").change(function(){
		process();
	});
	$("#counterPartyCode").change(function(){
		process();
	});
	$("#classification").change(function(){
		process();
	});
	$("#fromButton").click(function(){
		onFromButtonClick();
	});
	$("#toButton").click(function(){
		onToButtonClick();
	});

});


function inHouseTransfers() {
		//$('#tabGenerals').tabs();
		$('#tabs').tabs();
		
		if ($('#securityClass').val() == 'EQ') {
			$('#interestRate').attr('disabled', 'disabled');
			$('#accrualDays').attr('disabled', 'disabled');	
			$('#accruedInterest').attr('disabled', 'disabled');	
			$('#taxOnInterest').attr('disabled', 'disabled');
			$('#interestRate').val('');
			$('#interestRateStripped').val('');
			$('#accrualDays').val('');	
			$('#accrualDaysStripped').val('');	
			$('#accruedInterest').val('');	
			$('#accruedInterestStripped').val('');	
			$('#taxOnInterest').val('');
			$('#taxOnInterestStripped').val('');
		} else if ($('#securityClass').val() == 'FI') {
			$('#interestRate').removeAttr('disabled');
			$('#accrualDays').removeAttr('disabled');	
			$('#accruedInterest').removeAttr('disabled');	
			$('#taxOnInterest').removeAttr('disabled');						
		}
	
	
		$('#transactionDateGeneral').change(function(){
			$('#transactionDate').val($('#transactionDateGeneral').val());
			$('#transactionDateTo').val($('#transactionDateGeneral').val());
		});
		$('#settlementDateGeneral').change(function(){
			$('#settlementDate').val($('#settlementDateGeneral').val());
			$('#settlementDateTo').val($('#settlementDateGeneral').val());
		});
		$('#depositoryCodeGeneral').change(function(){
			$('#depositoryCode').val($('#depositoryCodeGeneral').val());
			$('#depositoryCodeTo').val($('#depositoryCodeGeneral').val());
		});
		
		$('#accountNoSource').lookup({
			list:'@{Pick.accounts()}',
			get:function() {
				$.get('@{Pick.account()}', {'code':$('#accountNoSource').val()}, function(data) {
					checkRedirect(data);
					if (data) {
						$('#accountNoSource').removeClass('fieldError');
						$('#accountNameSource').val(data.description);
						$('#h_accountNameSource').val(data.description);
						$('#accountKeySource').val(data.code);
						$('#accountNoTarget').focus();
						//getCharges(true);
						$('#accountNo').val($('#accountNoSource').val());
						$('#accountKey').val($('#accountKeySource').val());
						$('#accountName').val($('#accountNameSource').val());
						$('#accountNoTo').val($('#accountNoSource').val());
						$('#accountKeyTo').val($('#accountKeySource').val());
						$('#accountNameTo').val($('#accountNameSource').val());
					} else {
						$('#accountNoSource').addClass('fieldError');
						$('#accountNameSource').val('NOT FOUND');
						$('#h_accountNameSource').val('');
						$('#accountKeySource').val('');						
					}
				});
			},
			help:$('#accountHelpSource')
		});
	
		$('#accountNoTarget').lookup({
			list:'@{Pick.accounts()}',
			get:function() {
				$.get('@{Pick.account()}', {'code':$('#accountNoTarget').val()}, function(data) {
					checkRedirect(data);
					if (data) {
						$('#accountNoTarget').removeClass('fieldError');
						$('#accountNameTarget').val(data.description);
						$('#h_accountNameTarget').val(data.description);
						$('#accountKeyTarget').val(data.code);
						$('#transactionCode').focus();
						//getCharges(true);
					} else {
						$('#accountNoTarget').addClass('fieldError');
						$('#accountNameTarget').val('NOT FOUND');
						$('#h_accountNameTarget').val('');
						$('#accountKeyTarget').val('');						
					}
				});
			},
			help:$('#accountHelpTarget')
		});
		
		//$('#accountKey').change(function() {
		//	getCharges(false);
		//});
		$('#transactionCode').lookup({
			list:'@{Pick.transactionTemplates()}',
			get: {
				url:'@{Pick.transactionTemplate()}',
				success: function(data) {
					console.debug( '>>>> masuk success');
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
					
					
					//$('#transactionType').val(data.transactionType);
					//$('#transactionTypeCode').val(data.transactionTypeCode);
					//$('#h_transactionTypeCode').val(data.transactionTypeCode);
					//$('#transactionTypeDesc').val(data.transactionTypeDesc);
					//$('#h_transactionTypeDesc').val(data.transactionTypeDesc);
					//$('#securityClassId').val(data.securityClass + '-' + data.securityType);
					console.debug( 'securityClass=' + data.securityClass);
					// Check securityClass
					if (data.securityClass == 'EQ') {
						$('#interestRate').attr('disabled', 'disabled');
						$('#accrualDays').attr('disabled', 'disabled');	
						$('#accruedInterest').attr('disabled', 'disabled');	
						$('#taxOnInterest').attr('disabled', 'disabled');
						$('#interestRate').val('');
						$('#interestRateStripped').val('');
						$('#accrualDays').val('');	
						$('#accrualDaysStripped').val('');	
						$('#accruedInterest').val('');	
						$('#accruedInterestStripped').val('');	
						$('#taxOnInterest').val('');
						$('#taxOnInterestStripped').val('');
					} else if (data.securityClass == 'FI') {
						$('#interestRate').removeAttr('disabled');
						$('#accrualDays').removeAttr('disabled');	
						$('#accruedInterest').removeAttr('disabled');	
						$('#taxOnInterest').removeAttr('disabled');						
					} else {
						alert('Transaction is not currently supported');
					}
					//getCharges(true);
				},
				error: function() {
					console.debug( '>>>> masuk error');
					$('#transactionCode').addClass('fieldError');
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
					//$('#transactionType').val('');
					//$('#transactionTypeCode').val('');
					//$('#h_transactionTypeCode').val('');
					//$('#transactionTypeDesc').val('');
					//$('#h_transactionTypeDesc').val('');
					$('#securityClassId').val('');	
				}
			},
			filter : '$inHouseTransferSell',
			help:$('#transactionHelp'),
			nextControl:$('#securityId')
		});


		$('#transactionCodeTo').lookup({
			list:'@{Pick.transactionTemplateBuyWithSecurityTypes()}',
			get: {
				url:'@{Pick.transactionTemplate()}',
				success: function(data) {
					console.debug( '>>>> masuk success');
					$('#transactionCodeTo').removeClass('fieldError');
					$('#transactionTemplateKeyTo').val(data.code);
					$('#transactionCodeDescTo').val(data.description);
					$('#h_transactionCodeDescTo').val(data.description);
					$('#securityClassIdTo').val(data.securityClassId);
					$('#securityClassTo').val(data.securityClass);
					$('#h_securityClassTo').val(data.securityClass);
					$('#securityClassDescTo').val(data.securityClassDesc);
					$('#h_securityClassDescTo').val(data.securityClassDesc);
					$('#securityTypeTo').val(data.securityType);
					$('#h_securityTypeTo').val(data.securityType);
					$('#securityTypeDescTo').val(data.securityTypeDesc);
					$('#h_securityTypeDescTo').val(data.securityTypeDesc);
					//$('#transactionType').val(data.transactionType);
					//$('#transactionTypeCode').val(data.transactionTypeCode);
					//$('#h_transactionTypeCode').val(data.transactionTypeCode);
					//$('#transactionTypeDesc').val(data.transactionTypeDesc);
					//$('#h_transactionTypeDesc').val(data.transactionTypeDesc);
					//$('#securityClassId').val(data.securityClass + '-' + data.securityType);
					console.debug( 'securityClass=' + data.securityClass);
					// Check securityClass
					if (data.securityClass == 'EQ') {
						$('#interestRate').attr('disabled', 'disabled');
						$('#accrualDays').attr('disabled', 'disabled');	
						$('#accruedInterest').attr('disabled', 'disabled');	
						$('#taxOnInterest').attr('disabled', 'disabled');
						$('#interestRate').val('');
						$('#interestRateStripped').val('');
						$('#accrualDays').val('');	
						$('#accrualDaysStripped').val('');	
						$('#accruedInterest').val('');	
						$('#accruedInterestStripped').val('');	
						$('#taxOnInterest').val('');
						$('#taxOnInterestStripped').val('');
					} else if (data.securityClass == 'FI') {
						$('#interestRate').removeAttr('disabled');
						$('#accrualDays').removeAttr('disabled');	
						$('#accruedInterest').removeAttr('disabled');	
						$('#taxOnInterest').removeAttr('disabled');						
					} else {
						alert('Transaction is not currently supported');
					}
					//getCharges(true);
				},
				error: function() {
					console.debug( '>>>> masuk error');
					$('#transactionCodeTo').addClass('fieldError');
					$('#transactionTemplateKeyTo').val('');
					$('#transactionCodeDescTo').val('NOT FOUND');
					$('#h_transactionCodeDescTo').val('');
					$('#securityClassTo').val('');
					$('#h_securityClassTo').val('');
					$('#securityClassDescTo').val('');
					$('#h_securityClassDescTo').val('');
					$('#securityTypeTo').val('');
					$('#h_securityTypeTo').val('');
					$('#securityTypeDescTo').val('');
					$('#h_securityTypeDescTo').val('');
					//$('#transactionType').val('');
					//$('#transactionTypeCode').val('');
					//$('#h_transactionTypeCode').val('');
					//$('#transactionTypeDesc').val('');
					//$('#h_transactionTypeDesc').val('');
					$('#securityClassIdTo').val('');	
				}
			},
			filter : $('#securityType'),
			help:$('#transactionHelpTo'),
			nextControl:$('#securityIdTo')
		});


		
		$('#securityId').lookup({
			list:'@{Pick.securities()}',
			get:function() {
				getSecurity();
			},
			filter:$('#securityType'),
			help:$('#securityHelp')
		});
		$('#securityIdTo').lookup({
			list:'@{Pick.securities()}',
			get:function() {
				getSecurityTo();
			},
			filter:$('#securityTypeTo'),
			help:$('#securityHelpTo')
		});
		$('#taxCode').lookup({
			list:'@{Pick.taxMasters()}',
			get:{
				url:'@{Pick.taxMaster()}',
				success: function(data) {
							$('#taxCode').removeClass('fieldError');
							$('#taxKey').val(data.code);
							$('input#taxCode').val(data.taxCode);
							$('#taxName').val(data.description);
						},
				error: function() {
						$('#taxCode').addClass('fieldError');
						$('#taxKey').val('');
						$('#taxCode').val('');
						$('#taxName').val('NOT FOUND');
					}
			},
			key:$('#taxKey'),
			help:$('#taxCodeHelp'),
			nextControl:$('#tierValue')
		});
		
				
		$('#settlementDate').datepicker();
		$('#transactionDate').datepicker();
		$('.calendar').datepicker();
	
		$('#next1').click(function() {
			$('#tabs').tabs('select', 1);
			if (tab2Visited) {
				$('#next2').focus();
			} else {
				$('#chargeList input:text').first().focus();
			}
		});
		$('#prev2').click(function() {
			$('#tabs').tabs('select', 0);
		});
		$('#next2').click(function() {
			$('#tabs').tabs('select', 2);
			//$('input:text').first().focus();
		});
		$('#prev3').click(function() {
			$('#tabs').tabs('select', 1);
		});
		
	
	
	function getSecurity() {
		if ($('#securityId').val()) {
			$.post('@{Pick.security()}', {'code':$('#securityId').val(),'filter':$('#securityType').val(),'transactionDate':$('#transactionDate').val()}, function(data) {
	    		checkRedirect(data);
				if (data) {
					$('#securityId').removeClass('fieldError');
					$('#securityKey').val(data.securityKey);
					$('#securityDesc').val(data.description);
					$('#h_securityDesc').val(data.description);
					$('#depositoryCode').val(data.depositoryCode);
					$('#interestRate').val($.fn.autoNumeric.Format('interestRate', data.interestRate));
					$('#interestRateStripped').val(data.interestRate);
					$('#lastPaymentDate').val(data.lastPaymentDate);
					//$('#accrualBase').val(data.accrualBase.replace("ACCRUAL_BASE-", ""));
					//$('#yearBase').val(data.yearBase.replace("YEAR_BASE-", ""));
					var date = $('#transactionDate').datepicker('getDate');
					if (date) {
						date.setDate(date.getDate() + data.settlementDays);
						$('#settlementDate').datepicker('setDate', date);
					}
					$('#counterPartyCode').focus();
					//calculateAccrualDays();
					//calculate();
					
					//$('#counterPartyCode').focus();
					//getCharges(true);
				} else {
					$('#securityId').addClass('fieldError');
					$('#securityDesc').val('NOT FOUND');
					$('#securityKey').val('');
				}
			});		
		} else {
			console.debug('getSecurity() not running, #securityId is empty');
		}
	}


	function getSecurityTo() {
		if ($('#securityIdTo').val()) {
			$.post('@{Pick.security()}', {'code':$('#securityId').val(),'filter':$('#securityType').val(),'transactionDate':$('#transactionDate').val()}, function(data) {
	    		checkRedirect(data);
				if (data) {
					$('#securityIdTo').removeClass('fieldError');
					$('#securityKeyTo').val(data.securityKey);
					$('#securityDescTo').val(data.description);
					$('#h_securityDescTo').val(data.description);
					$('#depositoryCodeTo').val(data.depositoryCode);
					$('#interestRateTo').val($.fn.autoNumeric.Format('interestRate', data.interestRate));
					$('#interestRateToStripped').val(data.interestRate);
					$('#lastPaymentDateTo').val(data.lastPaymentDate);
					//$('#accrualBase').val(data.accrualBase.replace("ACCRUAL_BASE-", ""));
					//$('#yearBase').val(data.yearBase.replace("YEAR_BASE-", ""));
					var date = $('#transactionDateTo').datepicker('getDate');
					if (date) {
						date.setDate(date.getDate() + data.settlementDays);
						$('#settlementDateTo').datepicker('setDate', date);
					}
					$('#counterPartyCodeTo').focus();
					//calculateAccrualDays();
					//calculate();
					
					//$('#counterPartyCode').focus();
					//getCharges(true);
				} else {
					$('#securityIdTo').addClass('fieldError');
					$('#securityDescTo').val('NOT FOUND');
					$('#securityKeyTo').val('');
				}
			});		
		} else {
			console.debug('getSecurity() not running, #securityId is empty');
		}
	}
	$('#counterPartyCode').lookup({
		list : '@{Pick.thirdParties()}?type=THIRD_PARTY-BROKER',
		get : '@{Pick.thirdParty()}?type=THIRD_PARTY-BROKER',
		key : $('#counterPartyKey'),
		description : $('#counterPartyName'),
		help : $('#counterPartyHelp'),
		nextControl : $('#classification')
	});
	$('#counterPartyCodeTo').lookup({
		list : '@{Pick.thirdParties()}?type=THIRD_PARTY-BROKER',
		get : '@{Pick.thirdParty()}?type=THIRD_PARTY-BROKER',
		key : $('#counterPartyKeyTo'),
		description : $('#counterPartyNameTo'),
		help : $('#counterPartyHelpTo'),
		nextControl : $('#classificationTo')
	});
	$('#holdingRefs').lookup(
			{
				list : '@{Pick.holdings()}',
				get : {
					url : '@{Pick.holding()}',
					success : function(data) {
						$('#holdingQuantity').autoNumericSet(data.quantity);
						$('#h_holdingQuantity').val(data.quantity);
						$('#h_holdingRefs').val(data.code);
						console.debug( 'holdingRefs:' + $('#h_holdingRefs').val());
						var transactionType = $('#transactionType').val();
						if (transactionType == 'S') {
							var securityClass = $('#securityClass').val();
							if (data.code != 'TOTAL') {
								$('#quantity').autoNumericSet(data.quantity);
								$('#quantityStripped').val(data.quantity);
							}
							if (securityClass == 'MM') {
								$('#interestRate').autoNumericSet(data.interestRate);
								$('#interestRateStripped').val(data.interestRate);
								console.debug( '>>> [JUN] data.lastPaymentDate: ' + data.lastPaymentDate);
								$('#lastPaymentDate').datepicker('setDate', new Date(data.lastPaymentDate));
								$('#h_lastPaymentDate').datepicker('setDate', new Date(data.lastPaymentDate));
								$('#effectiveDate').datepicker('setDate', new Date(data.lastPaymentDate));
								$('#nextPaymentDate').val($.datepicker.formatDate('mm/dd/yy', new Date(data.nextPaymentDate)));
								//$('#nextPaymentDate').datepicker('setDate', new Date(data.nextPaymentDate));
								$('#maturityDate').datepicker('setDate', new Date(data.nextPaymentDate));
								$('#h_maturityDate').datepicker('setDate', new Date(data.nextPaymentDate));
								console.debug( '>>> [JUN] lastPaymentDate1: ' + $('#lastPaymentDate').val());
								console.debug( '>>> [JUN] nextPaymentDate: ' + $('#nextPaymentDate').val());
								calculateAccruedDays();
							}
						}
					}
				},
				filter : [ $('#settlementDate'), $('#accountKey'),
						$('#securityKey'), $('#classification') ],
				help : $('#holdingHelp'),
				nextControl : $('#quantity')
			});

	
}

//--function for show populate generate button 
function process() {
	if (($("#securityId").val()!='') && ($("#transactionDate").val()!='')&& ($("#transactionCode").val()!='') && ($("#counterPartyCode").val()!='')&& ($("#classification").val()!='')&& ($("#depositoryCode").val()!='') && ($("#settlementDate").val()!=''))
	{
		$('.button').css("display","")
	} else {
		$('.button').css("display","none")
	}
	
}
//--------------------------//
function onFromButtonClick() {
// TODO Button From 	
	alert("button from");
}
function onToButtonClick() {
// TODO Button To 
	alert("button To");
/*	if ((("#securityClass").val() == "EQ" ) || (("#securityClass").val() == "MM" ) ) {
		// Qty To ==  Qty from
	} else  {
		// Qty To ==  Qty from
		// Price To ==  Qty from
		// InterestRate To ==  InterestRate from
		// Accrued Interest To ==  Accrued Interest from
		// Net Proceed  To ==  Net Proceed  from
		
	} 
*/
}
