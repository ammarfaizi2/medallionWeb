function CustodyEndOfDay(html) {
	if (this instanceof CustodyEndOfDay) {
/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var ALL = "ALL";
		var CIF = "CIF";
		var ACCOUNT = "ACCOUNT";
		
		var ceod = html.inject(this);
		
		function clear(customer, account) {
			if (customer) {
				ceod.cfmaster.val("");
				ceod.cfmasterKey.val("");
				ceod.cfmasterDesc.val("");
			}
			
			if (account) {
				ceod.custInvt.val("");
				ceod.custInvtKey.val("");
				ceod.custInvtDesc.val("");
			}
		}
		
		function reset(customerCon, accountCon) {
			if (customerCon != null) {
				if (customerCon == ALL) {
					var bolCustomer = (customerCon != null) && (customerCon == 'ALL');
					var bolAccount = (accountCon != null) && (accountCon == 'ALL');
					clear(bolCustomer, bolAccount);

					ceod.cfmaster.disabled();
					ceod.cfmasterHelp.disabled();
				}else{
					ceod.cfmaster.enabled();
					ceod.cfmasterHelp.enabled();
				}
			}
			
			if (accountCon != null) {
				if (accountCon == ALL) {
					var bolCustomer = (customerCon != null) && (customerCon == 'ALL');
					var bolAccount = (accountCon != null) && (accountCon == 'ALL');
					clear(bolCustomer, bolAccount);

					ceod.custInvt.disabled();
					ceod.custInvtHelp.disabled();
				}else{
					ceod.custInvt.enabled();
					ceod.custInvtHelp.enabled();
				}
			}
		}
		
		function validation() {
			var msg = ceod.processDateError.html();
			return (msg == '');
		}
		
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		$("option", ceod.customerFilter).eq(0).remove();
		// edit rizki redmine #815
		ceod.customerFilter.change(function(){
			if ((this).value == 'ALL') {
//				ceod.custInvt.popupAccountCustomerNotInFa2("?customerKey=-99", "btnProcess");
				ceod.custInvt.dynapopup('PICK_CS_ACCOUNT_NOT_IN_FA', '', "btnProcess");
				$("label span", ceod.pcfmaster).remove();
			}else{
				$("label", ceod.pcfmaster).append("<span class='req'>*</span>");
			}
			reset((this).value, null);
		});
		
		$("option", ceod.accountFilter).eq(0).remove();
		ceod.accountFilter.change(function(){
			if ((this).value == 'ALL') {
				$("label span", ceod.pcustInvt).remove();
			}else{
				$("label", ceod.pcustInvt).append("<span class='req'>*</span>");
			}

			reset(null, (this).value);
		});

		ceod.cfmaster.dynapopup('PICK_CF_MASTER_NOT_IN_FA_MASTER', '', 'accountFilter', 
			function(data){
				clear(false, true);
//				ceod.custInvt.popupAccountCustomerNotInFa("?customerKey="+data.code, "btnProcess");
				ceod.custInvt.dynapopup('PICK_CS_ACCOUNT_BY_CUST', data.code, "btnProcess");
			},
			function(){
				clear(false, true);
//				ceod.custInvt.popupAccountCustomerNotInFa("?customerKey="+data.code, "btnProcess");
				ceod.custInvt.dynapopup('PICK_CS_ACCOUNT_BY_CUST', data.code, "btnProcess");
			}
		);

//		ceod.cfmaster.popupCustomerNotInFa('',"accountFilter", function(data){
//			clear(false, true);
//			ceod.custInvt.popupAccountCustomerNotInFa("?customerKey="+data.code, "btnProcess");
//		});

//		ceod.custInvt.popupAccountCustomerNotInFa("?customerKey=-99", "btnProcess");  ini aku matikan krn dari awal component disabled	
		var check = 0;
		ceod.btnProcess.click(function(){
			//if (validation()) {
			/*$('#processDate').removeClass('fieldError');*/
		 	//$('#processDateError').html("");
		 	//ceod.processDateError.html("");
			ceod.custodyEndOfDayForm.attr('action', 'process');
			ceod.custodyEndOfDayForm.submit();
			ceod.btnProcess.disabled();
			check = 1;
			loading.dialog('open');
		//}
	});
		
		ceod.btnReset.click(function(){
			if (check == 1)
				loading.dialog('open');
			else
			{
				ceod.log.val("");
				ceod.customerFilter.val(ALL);
				ceod.customerFilter.change();

				ceod.accountFilter.val(ALL);
				ceod.accountFilter.change();
				
				var appDate = new Date(html.getApplicationDate());
				console.log( appDate );
				var formateddate = $.datepicker.formatDate( '${appProp.jqueryDateFormat}', appDate );
				ceod.processDate.val(formateddate);
				ceod.btnProcess.enabled();
				
				$(".error", html).html("");
			}
			
		});
		
		ceod.customerFilter.change();
		ceod.accountFilter.change();
	}else{
		return new CustodyEndOfDay(html);
	}
}
	