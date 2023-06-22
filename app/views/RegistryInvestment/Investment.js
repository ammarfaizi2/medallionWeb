function Investment(html) {
	if (this instanceof Investment) {
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var inv = html.inject(this);
//		var bankAccountAdd ;
//		var bankInfoTable = new TableBankInfo(inv.bankInfoTable);
/* =========================================================================== 
 * Method Ajax Call
 * ========================================================================= */
		
/* =========================================================================== 
 * Method
 * ========================================================================= */
	/*function isEmail() {
		console.debug("size >>> : "+ $("input[name='inv.isSendToEmail']").val());
		inv.email.css("display","none");
		if (($("input[name='inv.isSendToEmail']:checked").val()=="true") || ($("input[name='inv.isSendToEmail']").val()=="true")) {
			inv.email.css("display","");
		}
	}*/
	
	/*function attachPopupOpeningSABranch(data) {
		$('#openingSABranchCode').lookup({
			list:'@{Pick.thirdPartiesWithChild()}?key='+data,
			get:{
				url:'@{Pick.thirdParty()}?type=THIRD_PARTY-SELLING_AGENT',
				success: function(data){
						$('#openingSABranchCode').removeClass('fieldError');
						$('#openingSABranch').val(data.code);
						$('#openingSABranchDesc').val(data.description);
						$('#h_openingSABranchDesc').val(data.description);
						$('#openingSABranchKey').val(data.code);
					
				},
				error: function(data){
					$('#openingSABranchCode').addClass('fieldError');
					$('#openingSABranch').val('');
					$('#openingSABranchCode').val('');
					$('#openingSABranchDesc').val('NOT FOUND');
					$('#h_openingSABranchDesc').val('');
					$('#openingSABranchKey').val('');
				}
			},
			description:$('#openingSABranchDesc'),
			help:$('#openingSABranchHelp'),
		});
	}*/
	
	function sumDividendOption() {
		console.debug(inv.divInvestorOpt.val());
		if (inv.divInvestorOpt.val() == 'true') {
			var divIopByCashPct = Number(inv.divIopByCashPct.val());
			var divIopByReivenstPct = Number(inv.divIopByReivenstPct.val());
			var divIopByRedeemPct = Number(inv.divIopByRedeemPct.val());
			
			inv.divIopByCashPct.removeClass('fieldError');
			inv.divIopByReivenstPct.removeClass('fieldError');
			inv.divIopByRedeemPct.removeClass('fieldError');
			$('#defaultInvestortypeError').html("");
			$('.defaultInvtOpt').html("");
			
			if (((inv.divIopByCashPct.val() != "") && (inv.divIopByReivenstPct.val() != "") && (inv.divIopByRedeemPct.val() != ""))) {
				if ((divIopByCashPct+divIopByReivenstPct+divIopByRedeemPct) != 100) {
					inv.divIopByCashPct.addClass('fieldError');
					inv.divIopByReivenstPct.addClass('fieldError');
					inv.divIopByRedeemPct.addClass('fieldError');
					$('#defaultInvestortypeError').html("Total By Cash, By Reinvestment and By Redeem must equals 100");
				} 
			}
		}
	}
	
	
/* =========================================================================== 
 * Event
 * ========================================================================= */
		/*inv.bankAccount.lookup({
			list:'@{Pick.bankAccounts()}?by=customer',
			get:{
				url:'@{Pick.bankAccountForSettlementAccountPick()}',
				success: function(data) {
					var found = false;
					// CHECKED DUPLICATE ROW
					var exist = bankInfoTable.isExist(data.bankAccountKey);
					if (exist != null) {
						$("#btnBankAccountAddError").html("Accountnumber "+data.bankAccountNo+" already exist ").show();
						found = true;
					}
					
					// IF NO DUPLICATE, THEN ADD NEW ROW
					if (!found) {
						$("#btnBankAccountAddError").html("");
						bankInfoTable.addRow(data);
						
					}
					inv.bankAccount.val('');
				},
				error: function() {
					console.debug( '>>>> masuk error');
					inv.bankAccountKey.val('');
				}	
			},
			help:inv.btnBankAccountAdd,
			filter: inv.customerKey.val()				
		});*/
		
//		inv.btnBankAccountAdd.click(function(){
//			alert("tes");
//			if (bankAccountAdd != null) {
//				var exist = bankInfoTable.isExist(bankAccountAdd.bankAccountKey);
//				if (exist == null) {
//					bankInfoTable.addRow(bankAccountAdd);
//					inv.bankAccountAdd.val("");
//					inv.bankAccountAddKey.val("");
//					inv.bankAccountAddDesc.val("");
//					inv.h_bankAccountAddDesc.val("");
//				}else{
//					alert("Accountnumber "+bankAccountAdd.bankAccountNo+" already exist ");
//				}
//				bankAccountAdd = null;
//			}
//		});
		
		if (('${mode}'=='entry') || (('${mode}'=='edit') &&(('${inv?.recordStatus?.decodeStatus()}'=='Reject')|| ('${status}' == 'R' )))) {
			$('input[name=active]').attr("disabled", "disabled");
		}
		$("input[name='active']").change(function() {
			$("input[name='inv.active']").val($("input[name='active']:checked").val());
			/*inv.inActiveDate.attr("disabled","disabled");
			inv.inActiveDate.val('');
			inv.inActiveDate.removeClass('fieldError');*/
			$("#inActiveDateError").html('');
			if($("input[name='active']:checked").val() == "false") {
//				inv.inActiveDate.attr("disabled",false);
//				$.post('@{inActiveDateDefault()}', function(data) {
//					inv.inActiveDate.val(data);
//				});	
			}
		});
		
	
		
		/*if (('${mode}'=='entry') || ('${mode}'=='view')) {
			inv.inActiveDate.attr("disabled","disabled");
		}*/
		
		/*if ('${mode}'=='edit') {
//			inv.inActiveDate.attr("disabled","disabled");
			if (('${inv.active}'== 'false') && (!('${confirming}')) && ('${inv?.recordStatus?.decodeStatus()}'!='Reject')) {
				inv.inActiveDate.attr("disabled",false);
			}
		}*/
//		isEmail();
		/*
		
		$("input[name='inv.isSendToEmail']").change(function() {	
			isEmail();
		});
		
		$("#radioStatus${inv?.bankAccount?.bankAccountKey}").attr("checked","checked");*/
		
		var dataType =0;
		if(inv.saCode.val() != ""){
			dataType = inv.saCodeKey.val();
		}
		
		/*inv.saCode.blur(function(){
			//inv.saCode.isChange();
			if((inv.saCode.isChange()) || (inv.saCode.val() == "")){
				inv.openingSABranchCode.removeClass('fieldError');
				inv.openingSABranchCode.val("");
				inv.openingSABranch.val("");
				inv.openingSABranchDesc.val("");
				inv.h_openingSABranchDesc.val("");
				inv.openingSABranchKey.val("");
				attachPopupOpeningSABranch(0);
			}
		});*/
		
		/*inv.openingSABranchCode.blur(function(){
			if((inv.openingSABranchCode.isChange()) || (inv.openingSABranchCode.val() == "")){
//				inv.openingSABranchCode.removeClass('fieldError');
//				inv.openingSABranchCode.val("");
//				inv.openingSABranch.val("");
//				inv.openingSABranchDesc.val("");
				inv.h_openingSABranchDesc.val("");
				inv.openingSABranchKey.val("");
				//attachPopupOpeningSABranch(0);
			}
		});*/
		
		/*inv.divIopByCashPct.change(function(){
			sumDividendOption();
		});
		
		inv.divIopByReivenstPct.change(function(){
			sumDividendOption();
		});
		
		inv.divIopByRedeemPct.change(function(){
			sumDividendOption();
		});*/
		
/* =========================================================================== 
 * Initilize
 * ========================================================================= */
		if (!inv.fundCode.val().isEmpty()) {
			var rgProduct = html.getRgProduct(inv.fundCode.val());
			if (rgProduct) {
				inv.divInvestorOpt.val(rgProduct.divInvestorOpt);
			}
		}
		
		//attachPopupOpeningSABranch(dataType);
		inv.tabs.tabs();
//		inv.tabs.css('height', '390');
		$("#tabs").tabs("disable",3);
		if (inv.divInvestorOpt.val() == 'true') {
			$("#tabs").tabs("enable",3);
		}
		
		html.clazz('calendar').datepicker();
		
		/*inv.btnBankAccountAdd.button();
		inv.btnBankAccountAdd.css('width', '100');*/
		
		/*inv.fundCode.popupProduct("saCode", function(data){
			if (data != null) {
				inv.currency.val(data.currencyCode);
				inv.currencyHidden.val(data.currencyCode);
				$('#defaultInvestortypeError').html("");
				$('.defaultInvtOpt').html("");
				
			}
		});*/
		
		inv.fundCode.dynapopup('PICK_RG_PRODUCT', "", "saCode",
			function(data){
				if (data) {
					inv.currency.val(data.currencyCode);
					inv.currencyHidden.val(data.currencyCode);
					$('#defaultInvestortypeError').html("");
					$('.defaultInvtOpt').html("");
				}
		},function(data){
			$('#fundCode').addClass('fieldError');
			$('#fundCodeDesc').val('NOT FOUND');
			$('#fundCodeKey').val('');
			$('#h_fundCodeDesc').val('');	
	}
		);
		
		
		//inv.fundCode.blurLookup();
		
		/*inv.saCode.dynapopup('PICK_GN_THIRD_PARTY', "THIRD_PARTY-SELLING_AGENT", "branch",
				function(data){
				},
				function(data){
						$('#saCode').addClass('fieldError');
						$('#saCodeDesc').val('NOT FOUND');
						$('#saCode').val('');
						$('#saCodeKey').val('');
						$('#h_saCodeDesc').val('');	
				}); */
		
		$('#saCode').dynapopup('PICK_GN_THIRD_PARTY', "THIRD_PARTY-SELLING_AGENT", "null", 
				function(data){
					if (data) {
						$('#saCode').removeClass('fieldError');
						//$('#newBankCode').val(data.name);
					}
				},
				function(data){
					$('#saCode').addClass('fieldError');
					$('#saCodeDesc').val('NOT FOUND');
					$('#saCode').val('');
					$('#saCodeKey').val('');
					$('#h_saCodeDesc').val('');	
				}
		); 
		
		/*inv.saCode.popupThirdPartiesWithParent("?type=THIRD_PARTY-SELLING_AGENT", "branch", function(data){
			//attachPopupOpeningSABranch(data.code);
			//inv.openingSABranchCode.popupThirdPartiesWithChild("?key="+data.code, "openingSABranch");
		});*/
		//inv.saCode.blurLookup();
		
		inv.customer.popupCustomer("accountNumber", function(data){
			inv.name.val(data.description);
			inv.bankAccount.popupBankAccountCustomer("?by=customer&filter="+data.code, "bankAccount", function(data){
			});
//			inv.bankAccountAdd.popupBankAccountCustomer("?by=customer&filter="+data.code,"bankAccountAdd", function(data){
//				bankAccountAdd = data;
//			});
		});
		
		/*if (!inv.customerKey.isEmpty()) {
			alert("masuk sini");
			var customerkey = inv.customerKey.val();
//			inv.bankAccount.popupBankAccountCustomer("?by=customer&filter="+customerkey, "bankAccountAdd");
			inv.bankAccount.popupBankAccountCustomer("?by=customer&filter="+customerkey,"bankAccount", function(data){
				inv.bankAccount.val(data.bankAccountNo);
			});
		}*/
		$('#bankAccount').lookup({
			list:'@{Pick.bankAccounts()}?by=customer&domain=INVESTOR',
			get:{
				url:'@{Pick.bankAccountByCustomerKey()}?domain=INVESTOR',
				success: function(data) {
					inv.bankAccount.val(data.bankAccountNo);
					inv.bankAccountKey.val(data.bankAccountKey);
					inv.bankAccountDesc.val(data.name);
					inv.bankAccountDescHide.val(data.name);
					inv.bankCode.val(data.bankCode.thirdPartyCode);
					inv.bankCodeHide.val(data.bankCode.thirdPartyCode);
					inv.bankCodeKey.val(data.bankCode.thirdPartyKey);
					inv.bankCodeDesc.val(data.bankCode.thirdPartyName);
					inv.bankCodeDescHide.val(data.bankCode.thirdPartyName);
					inv.bankCurrency.val(data.currency.currencyCode);
					inv.bankCurrencyHide.val(data.currency.currencyCode);
				},
				error: function() {
					inv.bankAccount.val("");
					inv.bankAccountKey.val("");
					inv.bankAccountDesc.val("");
					inv.bankAccountDescHide.val("");
					inv.bankCode.val("");
					inv.bankCodeHide.val("");
					inv.bankCodeKey.val("");
					inv.bankCodeDesc.val("");
					inv.bankCodeDescHide.val("");
					inv.bankCurrency.val("");
					inv.bankCurrencyHide.val("");
				}	
			},
			filter: $('#customerKey'),
			help:$('#bankAccountHelp')			
		});
		$('#bankAccount').blur(function(){
			if ($('#bankAccount').val() == "") {
				inv.bankAccount.val("");
				inv.bankAccountKey.val("");
				inv.bankAccountDesc.val("");
				inv.bankAccountDescHide.val("");
				inv.bankCode.val("");
				inv.bankCodeHide.val("");
				inv.bankCodeKey.val("");
				inv.bankCodeDesc.val("");
				inv.bankCodeDescHide.val("");
				inv.bankCurrency.val("");
				inv.bankCurrencyHide.val("");
			}
		});
		inv.fundCode.change(function(){
			if (inv.fundCode.val() == "") {
				$("#tabs").tabs("disable",3);
				inv.divIopByCashPct.val("");
				inv.divIopByReivenstPct.val("");
				inv.divIopByRedeemPct.val("");
			}
		});
		
		var backToList = function() {
			loading.dialog('open');
			location.href='@{list()}#{if mode}?mode=${mode}#{/}&param=${param}';
		}; 
		
		if ('${mode}'=='entry' && '${status}'=='N'){
			messageAlertOk("Your Account No : '${inv?.accountNumber}'", "ui-icon ui-icon-circle-check", "Notification", backToList);
		}
	}else{
		return new Investment(html);
	}
}
	