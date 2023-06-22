$(function(){
	
	$('.buttons button').button();
	$('#calcBal').button();
	
	$('#interestAmount').value($('#interestAmountHidden').val(), true, 2,"S");
	
	popUpAccount('PICK_BN_ACCOUNT_BY_FA_MASTER', '-1|CUSTOMER');
	
	if('${mode}'=='edit'){
		var blc = Number($('#balanceAmount').autoNumericGet());
		if(Number(blc) <= Number(0)){
			if('${confirming}' != 'true' && $("#status").val() == 'R'){
				$('#balanceAmount').attr("disabled", "");
				$('#h_balanceAmount').val(Number($('#balanceAmount').autoNumericGet()));
			}else{
				$('#balanceAmount').attr("disabled", "disabled");
				$('#h_balanceAmount').val(Number($('#balanceAmount').autoNumericGet()));
			}
		}
	}
	
	if (('${mode}'=='entry') || (('${mode}'=='edit') && (('${balance?.recordStatus?.decodeStatus()}' == 'Reject')|| ($("#status").val() == 'R' )))) {
		$('input[name=active]').attr("disabled", "disabled");
	}
	
	$('input[name=active]').change(function(){
		$("input[name='balance.active']").val($("input[name='active']:checked").val());
		if('${mode}'=='edit'){
			if($("input[name='balance.active']").val()==false || $("input[name='balance.active']").val()=='false'){
				$('#balanceAmount').val(0);
				$('#balanceAmountStripped').val(0);
				$('#interestAmount').val(0);
				$('#interestAmountHidden').val(0);
				$('#balanceAmount').attr("disabled", "disabled");
			}else{
				$('#balanceAmount').attr("disabled", "");
			}
		}
	});
	
	$('#fundCode').lookup({
		list:'@{Pick.faMasterInCfMaster()}',
		get:{
			url:'@{Pick.faFundSetup()}',
			success: function(data){
				$('#fundCode').removeClass('fieldError');
				$('#fundCodeKey').val(data.code);
				$('#fundCodeDesc').val(data.description);
				$('#h_fundCodeDesc').val(data.description);
				
				$('#accountNo').val('');
				$('#bankAccountKey').val('');
				$('#thirdPartyName').val('');
				$('#h_thirdPartyName').val('');
				$('#beneficiaryName').val('');
				$('#h_beneficiaryName').val('');
				$('#currencyCode').val('');
				$('#h_currencyCode').val('');
				$('#interestRate').val('');
				$('#h_interestRate').val('');
				$('#interestBase').val('');
				$('#h_interestBase').val('');
				$('#yearBase').val('');
				$('#h_yearBase').val('');
				popUpAccount('PICK_BN_ACCOUNT_BY_FA_MASTER', $('#fundCodeKey').val()+"|CUSTOMER");
			},
			error: function(data){
				popUpAccount('PICK_BN_ACCOUNT_BY_FA_MASTER', '-1|CUSTOMER');
				$('#fundCode').addClass('fieldError');
				$('#fundCodeKey').val('');
				$('#fundCode').val('');
				$('#fundCodeDesc').val('NOT FOUND');
				$('#h_fundCodeDesc').val('');
			}
		},
		description:$('#fundCodeDesc'),
		help:$('#fundCodeHelp')
	});
	
	/*$('#accountNo').lookup({
		list : '@{Pick.bankAccounts()}?by=famaster&domain=CUSTOMER',
		get:{
			url: '@{Pick.bankAccountByFaMaster()}?domain=CUSTOMER',
			success: function(data){
				if (data) {
					reloadCal = false;
					$('#accountNo').removeClass('fieldError');
					var codeSplit = $('#accountNo').val().split("");
					$('#accountNo').val(codeSplit[0]);
					$('#bankAccountKey').val(data.code);
					$('#thirdPartyName').val(data.bankCode.thirdPartyName);
					$('#h_thirdPartyName').val(data.bankCode.thirdPartyName);
					$('#beneficiaryName').val(data.customer.customerName);
					$('#h_beneficiaryName').val(data.customer.customerName);
					$('#currencyCode').val(data.currency.currencyCode);
					$('#currencyCodeHidden').val(data.currency.currencyCode);
					$('#interestRate').val(data.interestRate);
					$('#interestRateHidden').val(data.interestRate);
					$('#interestBase').val(data.interestBase);
					$('#interestBaseHidden').val(data.interestBase);
					$('#yearBase').val(data.yearBase);
					$('#yearBaseHidden').val(data.yearBase);
				}	
			},
			error: function(data) {
				reloadCal = false;
				$('#accountNo').addClass('fieldError');
				$('#thirdPartyName').val('NOT FOUND');
				$('#accountNo').val('');
				$('#bankAccountKey').val('');
				$('#h_thirdPartyName').val('');
				$('#beneficiaryName').val('');
				$('#h_beneficiaryName').val('');
				$('#currencyCode').val('');
				$('#h_currencyCode').val('');
				$('#currencyCodeHidden').val('');
				$('#interestRate').val('');
				$('#h_interestRate').val('');
				$('#interestRateHidden').val('');
				$('#interestBase').val('');
				$('#h_interestBase').val('');
				$('#interestRateHidden').val('');
				$('#yearBase').val('');
				$('#h_yearBase').val('');
				$('#yearBaseHidden').val('');
			}
		},
		description : $('#thirdPartyName'),
		filter : $('#fundCodeKey'),
		help : $('#accountNoHelp')
	});*/
	
	$('#fundCode').change(function(){
		$('#accountNo').val("");
		$('#accountNo').change();
	});
	
	$('#accountNo').change(function(){
		if ($('#accountNo').val() == "") {
			reloadCal = false;
			$('#thirdPartyName').val('');
			$('#accountNo').val('');
			$('#bankAccountKey').val('');
			$('#h_thirdPartyName').val('');
			$('#beneficiaryName').val('');
			$('#h_beneficiaryName').val('');
			$('#currencyCode').val('');
			$('#h_currencyCode').val('');
			$('#currencyCodeHidden').val('');
			$('#interestRate').val('');
			$('#h_interestRate').val('');
			$('#interestRateHidden').val('');
			$('#interestBase').val('');
			$('#h_interestBase').val('');
			$('#interestRateHidden').val('');
			$('#yearBase').val('');
			$('#h_yearBase').val('');
			$('#yearBaseHidden').val('');
			
			popUpAccount('PICK_BN_ACCOUNT_BY_FA_MASTER', '-1|CUSTOMER');
		}
	});
	
	$('#calcBal').live('click', function() {
		
		if ($('#balanceAmount').val()=='') {
			$('#balanceAmountError').html('Required');
			$('#balanceAmount').addClass('fieldError');
			return false;
		}
		var amount = Number($('#balanceAmount').autoNumericGet());
		var bank = $('#bankAccountKey').val();
		$.get("@{BankAccountsBalances.getInterestAmount()}", {'amount':amount,'prosesDate':$('#balanceDate').val(),'bank':bank}, function(data) {

			checkRedirect(data);
			$('#accountNo').val(data.accountNo);
			$('#thirdPartyName').val(data.bankCode.thirdPartyName);
			$('#h_thirdPartyName').val(data.bankCode.thirdPartyName);
			$('#beneficiaryName').val(data.customer.customerName);
			$('#h_beneficiaryName').val(data.customer.customerName);
			$('#currencyCode').val(data.currency.currencyCode);
			$('#currencyCodeHidden').val(data.currency.currencyCode);
			$('#interestRate').val(data.interestRate);
			$('#interestRateHidden').val(data.interestRate);
			$('#interestBase').val(data.interestBase);
			$('#interestBaseHidden').val(data.interestBase);
			$('#yearBase').val(data.yearBase);
			$('#yearBaseHidden').val(data.yearBase);
			
			$('#interestAmount').value(data.accruedInterest, true, 2,"S");
			$("#interestAmountHidden").val(data.accruedInterest);
		});
		reloadCal = true;
		$('#balanceButtonError').html('');
		return false;
	});
	
	if ($.browser.msie){
		$("#remarks[maxlength]").bind('input propertychange', function() {  
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }
	    });
	}
	
	$('#balanceAmount').change(function(){
		reloadCal = false;
		$('#balanceAmountError').html('');
		$('#balanceAmount').removeClass('fieldError');
		$('#interestAmount').val(0);
		$("#interestAmountHidden").val(0);
	});
	
	$('#balanceDate').change(function(){
		reloadCal = false;
	});
	
});