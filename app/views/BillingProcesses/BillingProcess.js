function BillingProcess(html) {
	if (this instanceof BillingProcess) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var components = ["filter","customer","customerKey","customerDesc","customerHelp","accordion","tabs","filterAll","filterCustomerNumber","btnProcess","invMonth","invYear","billingForm","btnReset","message","invDate","invDueDate", "invMonthErr", "invYearErr", "invDateErr", "invDueDateErr", "log", "customerErr"];
		var bill = html.injectComp(this, false, components);
		var app = html.inject(this, false);
/*==================================================================
 * Function
 *==================================================================*/
		
		function radiochange(type) {
			bill.filter.val(type);
			if (type == 'ALL') { resetCustomer(true, true); }
			if (type == 'CustomerNumber') { resetCustomer(false, false); }
		}
		
		function resetCustomer(clear, disabled) {
			if (clear) {
				bill.customer.removeClass("fieldError");
				bill.customer.val("");
				bill.customerKey.val("");
				bill.customerDesc.val("");
			}
			
			if (disabled) {
				bill.customer.disabled();
				bill.customerKey.disabled();
				bill.customerHelp.disabled();
			}else{
				bill.customer.enabled();
				bill.customerKey.enabled();
				bill.customerHelp.enabled();
			}
		}
		
		function validate() {
			var conValidMonth = true;
			var conCustomer  = true;
			
			if (bill.filter.val() == 'CustomerNumber') {
				conCustomer = bill.customerErr.required(bill.customer.isEmpty());
			}
			
			var conInvMonth = bill.invMonthErr.required(bill.invMonth.isEmpty());
			if (conInvMonth) {
				conValidMonth = bill.invMonthErr.valid(bill.invMonth.isMonth(), "Invalid month");
			}
			
			var conValidYear = true;
			var conInvYear = bill.invYearErr.required(bill.invYear.isEmpty());
			if (conInvYear) {
				conValidYear = bill.invYearErr.valid(bill.invYear.isYear(), "Invalid year");
			}
			
			var conInvDate = bill.invDateErr.required(bill.invDate.isEmpty());
			var conInvDueDate = bill.invDueDateErr.required(bill.invDueDate.isEmpty());
			var conInvDueDate = bill.invDueDateErr.required(bill.invDueDate.isEmpty());
			return conCustomer && conInvMonth && conInvYear && conInvDate && conInvDueDate && conValidMonth && conValidYear;
		}
		
		function disabledButton(condition) {
			app.btnProcess.button("option", "disabled", condition);
			app.btnProcessAjax.button("option", "disabled", condition);
			app.btnReset.button("option", "disabled", condition);
			if (condition) {
				app.customer.attr("disabled", "disabled");
				app.customerKey.attr("disabled", "disabled");
				app.customerHelp.attr("disabled", "disabled");
				
			}else{
				app.customer.removeAttr("disabled");
				app.customerKey.removeAttr("disabled");
				app.customerHelp.removeAttr("disabled");
				
			}			
		}
		
		function fetchLog() {
			var param = {
				"filter" : bill.filter.val()
			};
			
			$().fetchAsync("@{processAjaxLog()}", {"param":param}, function(data){
				console.log("fetch log");
				console.log(data);
				/*if (data.status == 'G') {
					//do nothing, stop scheduler;
					loading.dialog('close');
					disabledButton(false);
				}else*/ 
				if (data.status == 'W') {
					setTimeout(fetchLog, 3000);
				}else if (data.status == 'F') {
					var nice = "";
					for (var x in data.logs) {						
						var content = data.logs[x];
						if (nice == '') { nice = content;
						}else{ nice = nice + '\n'+content; }
					}
					app.log.val(nice);
					loading.dialog('close');
					disabledButton(false);
					//do nothing, stop scheduler;
				}
			});
		}
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/

/*================================================================== 
 * Event
 *================================================================== */
		bill.accordion.accordion({
			collapsible: false,
			autoHeight: false 
		});
		
		bill.tabs.tabs();
		
		$('.calendar').datepicker();
		
		$("[name='filterGroup']", html).click(function(e){
			radiochange($(this).val());
		});

		bill.customer.dynapopup('PICK_CF_MASTER_NON_RETAIL_BILLING', '', 'invMonth');
		//bill.customer.popupCustomerNonRetail('','invMonth');

		if (bill.filter.val() == 'ALL') { bill.filterAll.click(); }
		if (bill.filter.val() == 'CustomerNumber') { bill.filterCustomerNumber.click(); }

		bill.btnProcess.click(function(){
			if (validate()) {
		    	$.ajaxSetup({ async : false });
		    	var param = {
		    		"filter" : bill.filter.val(),
		    		"customerKey" : bill.customerKey.val(),
		    		"invMonth" : bill.invMonth.val(),
		    		"invYear" : bill.invYear.val(),
		    		"invDate" : bill.invDate.val(),
		    		"invDueDate" : bill.invDueDate.val()
		    	};
		    	
				$.get("@{BillingProcesses.reprocess()}", {"param":param}, function(data) {
					checkRedirect(data);
					if (data == 0) {
						bill.billingForm.attr('action', 'process');
						bill.billingForm.submit();					
					}else{
						/*var con = confirm("Billing process already done, do you want to reprocess ?");
						if (con) {
							bill.billingForm.attr('action', 'process');
							bill.billingForm.submit();
						}*/
						messageAlertYesNo("Billing process already done, do you want to reprocess ?", "ui-icon ui-icon-notice", "Confirmation Message", reProcessBilling, closeDialog);
					}
				});
			}
		});
		
		app.btnProcessAjax.click(function(){
			if (validate()) {
				
				disabledButton(true);

				var param = {
					"customer" : bill.customer.val(),
		    		"filter" : bill.filter.val(),
		    		"customerKey" : bill.customerKey.val(),
		    		"invMonth" : bill.invMonth.val(),
		    		"invYear" : bill.invYear.val(),
		    		"invDate" : bill.invDate.val(),
		    		"invDueDate" : bill.invDueDate.val()
		    	};
				
				$.get("@{BillingProcesses.reprocess()}", {"param":param}, function(data) {
					checkRedirect(data);
					console.log(data);
					if (data == 0) {
						loading.dialog('open');
						$().fetchAsync("@{processAjax()}", {"param":param}, function(data){
							if (data.errorSize > 0) {
								for (var x in data.validations) {
									$("#"+x).html(data.validations[x]).show();
								}
								loading.dialog('close');
								disabledButton(false);
							}
						});
						setTimeout(fetchLog, 5000);					
					}else{
						/*var con = confirm("Billing process already done, do you want to reprocess ?");
						if (con) {
							bill.billingForm.attr('action', 'process');
							bill.billingForm.submit();
						}*/
						messageAlertYesNo("Billing process already done, do you want to reprocess ?", "ui-icon ui-icon-notice", "Confirmation Message", reProcessBilling, closeDialog);
					}
				});
				
			}
		});
		
		var reProcessBilling = function(){
			console.log('reprocess di sini ');
			$("#dialog-message").dialog("close");
			var param = {
				"customer" : bill.customer.val(),
	    		"filter" : bill.filter.val(),
	    		"customerKey" : bill.customerKey.val(),
	    		"invMonth" : bill.invMonth.val(),
	    		"invYear" : bill.invYear.val(),
	    		"invDate" : bill.invDate.val(),
	    		"invDueDate" : bill.invDueDate.val()
	    	};
			
			loading.dialog('open');
			
			$().fetchAsync("@{processAjax()}", {"param":param}, function(data){
				console.log('processAjax');
				console.debug(data);
				if (data.errorSize > 0) {
					for (var x in data.validations) {
						$("#"+x).html(data.validations[x]).show();
					}
					loading.dialog('close');
					
					disabledButton(false);
				}
			});
			setTimeout(fetchLog, 5000);
		};
		
		var closeDialog = function() {
			$("#dialog-message").dialog("close");
			disabledButton(false);
		};
		
		bill.btnReset.click(function(){
			bill.customerErr.required(false);
			bill.invMonthErr.required(false);
			bill.invYearErr.required(false);
			bill.invDateErr.required(false);
			bill.invDueDateErr.required(false);
			
			bill.filterAll.click();
			bill.invMonth.val("");
			bill.invYear.val("");
			bill.invYear.val("");
			bill.invDate.val("");
			bill.invDueDate.val("");
			bill.log.html("");
		});
		
		app.btnProcess.hide();
	}else{
		return new BillingProcess(html);
	}
}