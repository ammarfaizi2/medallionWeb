function InvoiceGenerationPaging(html) {
	if (this instanceof InvoiceGenerationPaging) {
		

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		var trRow;
/*==================================================================
 * Function
 *==================================================================*/
		
		
		function radiochange(type) {
			app.filter.val(type);
			if (type == 'ALL') {
				resetCustomer(true, true);	
			}
			if (type == 'CustomerNumber') {
				resetCustomer(false, false);
			}
		}
		
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
			p.includeZeroAmount = app.includeZero.val();
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
		
		app.datatable = app.tblBilling.paging("@{InvoiceGeneration.search()}", this);
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.popupDetailPop = app.popupDetail.dialog({
			autoOpen: false,
			height: 550,
			width: 625,
			modal: true
		});

		/*$('#invMonth').change(function(){
			validateMonth($(this).val());
		});*/
		
		/*var validateMonth = function(inputField) {
		   	 //var isValid = /^([0-1]?[0-9]|2[0-4]):([0-5][0-9])(:[0-5][0-9])?$/.test(inputField);
		       
		   	 var isValid = /^([0-1]?[0-9]|1[0-2])?$/.test(inputField);

		       if (isValid) {
		    	  alert('sesuai'); 
		       	//$(id).removeClass('fieldError');
		       } else {
		    	   alert('g sesuai');
		       	//$(id).addClass('fieldError');
		       }

		       return isValid;
		   };*/
		$('#isIncludeZero').change(function(){
			if ($('#isIncludeZero').is(':checked')){
				$("input[name='param.includeZeroAmount']").val("true");
			}else{
				$("input[name='param.includeZeroAmount']").val("false");
			}
		});
		function modeView(){
			app.btnGenerate.attr("disabled","disabled");
//			app.chkRow.attr("disabled","disabled");
//			app.filterAll.attr("disabled","disabled");
//			app.isIncludeZero.attr("disabled","disabled");
//			app.filterCustomerNumber.attr("disabled","disabled");
//			app.invMonth.attr("disabled","disabled");
//			app.invYear.attr("disabled","disabled");
			app.popInvDate.attr("disabled","disabled");
			app.popInvDueDate.attr("disabled","disabled");
			app.btnSave.attr("disabled","disabled");
			
			/*var row = $(this).parents('tr');
			var rowNumber = tblBilling.fnGetPosition(row[0]);
			var tr_row = tblBilling.fnGetNodes(rowNumber);
			alert("tr_row "+tr_row);
			$("input[name='chkRow']", tr_row).attr('disabled', "disabled");*/
			

			$("tbody tr", app.tblBilling).each(function(){
					var prop = $(this).data("prop");
//					if (!prop.bean.allow) {
						$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled");
//					}
			});
		}
		
		function modeEntry(){
			app.search.attr("disabled",false);
			app.btnGenerate.attr("disabled",false);
			app.chkRow.attr("disabled",false);
			app.filterAll.attr("disabled",false);
			app.isIncludeZero.attr("disabled",false);
			app.filterCustomerNumber.attr("disabled",false);
			app.invMonth.attr("disabled",false);
			app.invYear.attr("disabled",false);
			app.popInvDate.attr("disabled",false);
			app.popInvDueDate.attr("disabled",false);
			app.btnSave.attr("disabled",false);
		}
		
		function validate() {
			var conValidMonth = true;
			var conInvMonth = $('#invMonthErr').required(app.invMonth.isEmpty());
			var typeCust  = true;
			if (conInvMonth) {
				conValidMonth = $('#invMonthErr').valid(app.invMonth.isMonth(), "Invalid month");
				
			}
			
			var conValidYear = true;
			var conInvYear = $('#invYearErr').required(app.invYear.isEmpty());
			if (conInvYear) {
				conValidYear = $('#invYearErr').valid(app.invYear.isYear(), "Invalid year");
			}
			
			if (app.filter.val() == 'CustomerNumber'){
				typeCust = $('#customerErr').required(app.customer.isEmpty());
			}
			
			return conInvMonth && conInvYear && conValidMonth && conValidYear && typeCust;
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
			resetCustomer(true, true);	
			app.filterAll.attr('checked', true);
			app.invMonth.val("");
			app.invYear.val("");
			app.includeZero.val("");
			app.isIncludeZero.val("");
			app.isIncludeZero.attr('checked', false);
			app.customerErr.required(false);
			app.invMonthErr.required(false);
			app.invYearErr.required(false);
			app.filter.val('ALL');
			resetCustomer(true, true);	
			app.customer.removeClass("fieldError");
			app.datatable.fnPageChange("first");
			$('p[id=pcustomerType] label span').html('');
			app.result.hide();
			app.datatable.trigger("fetch", [0, "checked"]);
			app.chkRow.attr('checked',false);
			modeEntry();
		});
		
		$('.calendar').datepicker();
		
		$("[name='filterGroup']", html).click(function(e){
			radiochange($(this).val());
		});

		//app.customer.dynapopup('PICK_CF_MASTER_NON_RETAIL', '', 'invMonth');
		app.customer.dynapopup('PICK_CF_MASTER_NON_RETAIL_BILLING', '', 'invMonth');
		//app.customer.popupCustomerNonRetail('','invMonth');

		if (app.filter.val() == 'ALL') { app.filterAll.click(); }
		if (app.filter.val() == 'CustomerNumber') { app.filterCustomerNumber.click(); }
		if (app.filter.val() == 'AccountNumber') { app.filterAccountNumber.click(); }
		
		
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
		
		app.btnGenerate.button();
		app.btnSave.button();
		app.btnCancel.button();
		var closeDialog = function() {
			$("#dialog-message").dialog('close');	
		}
		app.btnGenerate.click(function(event, prop){
			app.datatable.trigger("fetch", [0, "checked"]);
			
				if (app.billingKeys.val() == ""){
					messageAlertOk("Please Select Invoices", "ui-icon ui-icon-circle-check", "Confirmation Message",closeDialog);
					//alert("Please select invoices");
				}else{
					$.get("@{InvoiceGeneration.generate()}", {'billingKeys':app.billingKeys.val()}, function(data) {
						checkRedirect(data);
						app.billingMessage.val(data.message);
						app.billingKeys.val(data.billingKeys);
						var message = app.billingMessage.val();
						app.billingMessage.val("");
						messageAlertOk(message, "ui-icon ui-icon-circle-check", "Confirmation Message",closeDialog);
						
					});
					app.datatable.fnPageChange("first");
					modeView();
					app.billingKeys.val("");
				}
			
		});
		app.btnCancel.click(function(){
			app.popupDetailPop.dialog("close");
		});
		app.datatable.bind("select", function(event, prop){
//			alert(prop.bean.billingKey+"-"+prop.row[1]+"-"+prop.tr);
			
			var billingKey = prop.bean.billingKey;
			$.get("@{InvoiceGeneration.getBillingByBillingKey()}", {'billingKey':billingKey}, function(data) {
				checkRedirect(data);
				app.billingKey.val(data.billingKey);
				app.popCustomer.val(data.customer);
				app.popCustomerDesc.val(data.customerDesc);
				app.popInvoiceNo.val(data.invNo);
				app.popMonth.val(data.invMonth);
				app.popYear.val(data.invYear);
				//app.popInvDate.val((new Date(data.invDate)).fmtDateMDY("/"));
				app.popInvDate.datepicker( "setDate", new Date(data.invDate) );
				app.popInvDueDate.val((new Date(data.invDueDate)).fmtDateMDY("/"));
				app.popInvDateError.html('');
				app.popInvDueDateError.html('');
				var vTable = $("#tblBillingDetail").local();
				$("thead", vTable).click();
				
				var oSettings = vTable.fnSettings();
				var iTotalRecords = oSettings.fnRecordsTotal();
				for (i=0 ; i<=iTotalRecords; i++) {
					vTable.fnDeleteRow(0,null,true);
				}
				
				var totAmtAfterTax = 0;
				for (var i = 0; i < data.details.length; i++) {
					var detailrow = data.details[i];
					var rows = [detailrow.chargeGroup.lookupDescription, $("div").autoNumericSet(detailrow.billingFee)[0].value, $("div").autoNumericSet(detailrow.billingTax)[0].value, $("div").autoNumericSet(detailrow.billingAmount)[0].value];
					totAmtAfterTax += detailrow.billingAmount;
					vTable.fnAddData(rows);
				}
				
				$("tbody tr td:first-child", vTable).attr("align", "left");
				$("tbody tr td", vTable).attr("align", "right")
				
				
				app.popTotAmtAfterTax.val($("div").autoNumericSet(totAmtAfterTax)[0].value);
				
//				$("#popupDetail").dialog('open');
				app.popupDetail.dialog('open');
				vTable.fnPageChange("first");
				app.btnCancel.focus();
			});
		});
		
		if (app.billingMessage.val() != "") {
			var message = app.billingMessage.val();
			app.billingMessage.val("");
			messageAlertOk(message, "ui-icon ui-icon-circle-check", "Confirmation Message",closeDialog);
			//alert(message);
		}
		
		app.btnSave.click(function(){
			var invDate = new Date(app.popInvDate.datepicker( "getDate" ));
			var invDueDate = new Date (app.popInvDueDate.datepicker( "getDate" ));
			var cekDate = new Date(app.popInvDate.val());
			cekDate.setMonth(parseInt(app.invMonth.val()));
			cekDate.setFullYear(app.invYear.val());
			cekDate.setDate(0);
//			alert("cekDate "+cekDate+"inv date "+new Date(invDate)+'cekDate '+new Date(cekDate));
			console.log( invDate );
			
			
//			var invDueDate = new Date(app.popInvDueDate.val());
			
			if (app.popInvDate.val() == '' || app.popInvDueDate.val() == ''||app.popInvDate.val() == null || app.popInvDueDate == null ){
				if (app.popInvDate.val() == '' || app.popInvDate.val() == null){
					app.popInvDateError.html('Required\n');
				}
				if (app.popInvDueDate.val() == '' || app.popInvDueDate.val() == null){ 
					app.popInvDueDateError.html('Required\n');
				}
				return false;
			}else {
				
				if ((invDueDate.getTime() <= invDate.getTime()) || ((invDate.getTime() < cekDate.getTime()))){
					if (invDueDate.getTime() <= invDate.getTime()){
						app.popInvDueDateError.html("Invoice Due Date can not be less or equal then Invoice Date");
					}
					
					
					
					if (invDate.getTime() < cekDate.getTime()){
						app.popInvDateError.html("Invoice Date can not be less then end of month and year invoice");
					}
					
					return false;
				}
				
			}
		/*	if (invDate == '') {
				message += "Invoice Date is required\n"; 
			}
			if (invDueDate == '') { message += "Invoice Due Date is required"; }*/
			
			/*if (message == "") {
				if (invDate.getTime() <= invDueDate.getTime()){
//				if (new Date(invDate) >= new Date(invDueDate)) {
					message = "Invoice Date can not be less then Invoice Due Date";
				}
				if (invDate.getTime() <= cekDate.getTime()){
					message = "Invoice Date can not be less then end of month and year invoice";
				}
			}*/
			
			$.get("@{InvoiceGeneration.updateBillingDate()}", {'billingKey':app.billingKey.val(), 'invoiceDate':app.popInvDate.val().fmtYYYYMMDD("/"), 'dueDate':app.popInvDueDate.val().fmtYYYYMMDD("/")}, function(data) {
				checkRedirect(data);
				if (data == 1) {
					/*if (trRow != null) {
						$("td:nth-child(6)", trRow).html(app.popInvDate.val());
						$("td:nth-child(7)", trRow).html(app.popInvDueDate.val());
						trRow == null;
					}*/
					app.datatable.fnPageChange("first");
					messageAlertOk("Update Data Success", "ui-icon ui-icon-circle-check", "Confirmation Message",closeDialog);
					app.popupDetail.dialog("close");
				}else{
					messageAlertOk("Update Data Fail", "ui-icon ui-icon-circle-check", "Confirmation Message",closeDialog);
				}
			});
		});
		
	}else{
		return new InvoiceGenerationPaging(html);
	}
}