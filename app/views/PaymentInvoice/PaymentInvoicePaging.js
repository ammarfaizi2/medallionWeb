function PaymentInvoicePaging(html) {
	if (this instanceof PaymentInvoicePaging) {
		

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		var trRow;
		var billingkeys = "";
/*==================================================================
 * Function
 *==================================================================*/
		
		
		function resetCustomer(clear, disabled) {
			if (clear) {
				app.customer.removeClass("fieldError");
				app.customer.val("");
				app.customerKey.val("");
				app.customerDesc.val("");
				$('p[id=pcustomerType] label span').html('');
				$('#customerErr').html('');
			}
			
			if (disabled) {
				app.customer.disabled();
				app.customerKey.disabled();
				app.customerHelp.disabled();
				$('p[id=pcustomerType] label span').html('');
				$('#customerErr').html('');
			}else{
				app.customer.enabled();
				app.customerKey.enabled();
				app.customerHelp.enabled();
				$('p[id=pcustomerType] label span').html(' *');
			}
		}
		
		

/* =========================================================================== 
 * Table Search Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.customerKey = app.customerKey.val();
			p.invMonth = app.invMonth.val();
			p.invYear  = app.invYear.val();
			p.invDate = app.paymentDate.val();
			p.billingKeys = app.billingKeys.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		
		app.result.hide();
		
		app.datatable = app.tblBilling.paging("@{PaymentInvoice.search()}", this);
		
		app.hidAppDate.hide();
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.popupDetailPop = app.popupDetail.dialog({
			autoOpen: false,
			height: 550,
			width: 625,
			modal: true
		});
		
		function validateGenerate(){
			$("#paymentDateErr").html("");
			$("#paymentDateErr").hide();
			
			var validPayDt = true;
			
			validPayDt = $("#paymentDateErr").required(app.paymentDate.isEmpty());
			
			var hidDt = app.hidAppDate.val().split('/');
			var paymentDt = app.paymentDate.val().split('/');
			
			var hidDt1 = parseInt(hidDt[2]+hidDt[1]+hidDt[0]);
			var paymentDt1 = parseInt(paymentDt[2]+paymentDt[1]+paymentDt[0]);
			
			if(validPayDt && paymentDt1 > hidDt1){
				$("#paymentDateErr").html("Payment Date cannot be greater than Application Date");
				$("#paymentDateErr").show();
				validPayDt = false;
			}
			return validPayDt;
		}

		function validate() {
			
			var conValidMonth = true;
			if ($('#invMonthErr').val()!='') {
				conValidMonth = $('#invMonthErr').valid(app.invMonth.isMonth(), "Invalid month");
				
			}
			
			var conValidYear = true;
			if ($('#invYearErr').val()!='') {
				conValidYear = $('#invYearErr').valid(app.invYear.isYear(), "Invalid year");
			}
			
			
			return conValidMonth && conValidYear;
		}
		
		app.search.click(function(){
			if (validate()){
				app.query.val(true);
				app.datatable.fnPageChange("first");
				
				app.result.show();
				app.btnGenerate.attr("disabled",false);
				app.datatable.fnAdjustColumnSizing();
			}
			
			
		});

		app.reset.click(function(){
			/*resetCustomer(true, true);	
			app.invMonth.val("");
			app.invYear.val("");
			app.customerErr.required(false);
			app.invMonthErr.required(false);
			app.invYearErr.required(false);
			resetCustomer(true, true);	
			app.customer.removeClass("fieldError");
			app.datatable.fnPageChange("first");
			app.result.hide();
			app.datatable.trigger("fetch", [0, "checked"]);
			app.chkRow.attr('checked',false);*/
			$("#searchForm").attr('action', 'reset').submit();
		});
		
		$('.calendar').datepicker();
		
		app.customer.dynapopup('PICK_CF_MASTER_NON_RETAIL_BILLING', '', 'invMonth');

		app.btnGenerate.button();
		app.btnSave.button();
		app.btnCancel.button();
		var closeDialog = function() {
			$("#dialog-message").dialog('close');	
		}

		app.datatable.bind("selects", function(event, props){
			
			var billingkeys = "";
			for (x in props) {
				if (billingkeys == ""){
					billingkeys = props[x].bean.billingKey;
				}else{
					billingkeys += "_"+props[x].bean.billingKey;
				}
				
			}
			app.billingKeys.val(billingkeys);
		});
		
		app.btnGenerate.click(function(event,prop){
			
			if(validateGenerate()){
				app.datatable.trigger("fetch",[0,"checked"]);
				
				if (app.billingKeys.val() == ""){
					messageAlertOk("Please Select Invoices", "ui-icon ui-icon-circle-check", "Confirmation Message",closeDialog);
				}else{
					$.get("@{PaymentInvoice.generate()}", {'billingKeys':app.billingKeys.val(), "payDate":app.paymentDate.val()}, function(data) {
						checkRedirect(data);
						app.billingMessage.val(data.message);
						app.billingKeys.val(data.billingKeys);
						var message = app.billingMessage.val();
						app.billingMessage.val("");
						messageAlertOk(message, "ui-icon ui-icon-circle-check", "Confirmation Message",closeDialog);
						
					});
					app.datatable.fnPageChange("first");
					app.billingKeys.val("");
				}
			}
		});
		
		if (app.billingMessage.val() != "") {
			var message = app.billingMessage.val();
			app.billingMessage.val("");
			messageAlertOk(message, "ui-icon ui-icon-circle-check", "Confirmation Message",closeDialog);
		}
		
	}else{
		return new PaymentInvoicePaging(html);
	}
}