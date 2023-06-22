function InvoiceGeneration(html) {
	if (this instanceof InvoiceGeneration) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var inv = html.inject(this);
		var trRow;

/*==================================================================
 * Function
 *==================================================================*/
		
		function radiochange(type) {
			inv.filter.val(type);
			if (type == 'ALL') {
				resetCustomer(true, true);	
				resetAccount(true, true);
			}
			if (type == 'CustomerNumber') {
				resetAccount(true, true);
				resetCustomer(false, false);
			}
			if (type == 'AccountNumber') {
				resetCustomer(true, true);
				resetAccount(false, false);
			}
		}
		
		function resetCustomer(clear, disabled) {
			if (clear) {
				inv.customer.val("");
				inv.customerKey.val("");
				inv.customerDesc.val("");
			}
			
			if (disabled) {
				inv.customer.disabled();
				inv.customerKey.disabled();
				inv.customerHelp.disabled();
			}else{
				inv.customer.enabled();
				inv.customerKey.enabled();
				inv.customerHelp.enabled();
			}
		}
		
		function resetAccount(clear, disabled) {
			if (clear) {
				inv.account.val("");
				inv.accountKey.val("");
				inv.accountDesc.val("");
			}
			
			if (disabled) {
				inv.account.disabled();
				inv.accountKey.disabled();
				inv.accountHelp.disabled();
			}else{
				inv.account.enabled();
				inv.accountKey.enabled();
				inv.accountHelp.enabled();
			}
		}
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/
/*================================================================== 
 * Event
 *================================================================== */
		
		inv.popupDetail.dialog({
			autoOpen: false,
			height: 550,
			width: 625,
			modal: true
		});
		
		inv.accordion.accordion({
			collapsible: false,
			autoHeight: false 
		});
		
		inv.tabs.tabs();
		
		$('.calendar').datepicker();
		
		$("[name='filterGroup']", html).click(function(e){
			radiochange($(this).val());
		});
		
		inv.customer.popupCustomer('','invMonth');
		
		inv.account.popupAccountCustomer("?customerKey=-99", "invMonth");
		
		if (inv.filter.val() == 'ALL') { inv.filterAll.click(); }
		if (inv.filter.val() == 'CustomerNumber') { inv.filterCustomerNumber.click(); }
		if (inv.filter.val() == 'AccountNumber') { inv.filterAccountNumber.click(); }
		
		inv.btnGenerate.click(function(){
			alert(" customer key "+$('#customerKey').val());
			var billingkeys = "";
			var checkRow = $("[name='chkRow']", html).filter(':checked');			
			checkRow.each(function() {
				if (billingkeys == "") {
					billingkeys = $(this).val();
				}else{
					billingkeys += "_"+$(this).val();
				}
			});
			
			inv.billingKeys.val(billingkeys);
			if (billingkeys == "") {
				alert("Please select invoices");
			}else{
				inv.invoiceGenForm.attr('action', 'generate');
				inv.invoiceGenForm.submit();
			}
		});
		
		inv.chkSelectAll.click(function(){
			$("[name='chkRow']", html).attr("checked", $(this).attr("checked"));
		});
		
		inv.btnSearch.click(function(){
			inv.invoiceGenForm.attr('action', 'search');
			inv.invoiceGenForm.submit();
		});
		
		inv.btnReset.click(function(){
			inv.invoiceGenForm.attr('action', 'reset');
			inv.invoiceGenForm.submit();
		});
		
		if (inv.message.val() != "") {
			var message = inv.message.val();
			inv.message.val("");
			alert(message);
		}
		
		$("#tblBilling tbody tr td:not(:first-child)", html).click(function(){
			var billingKey = $(this).parent().attr("billingkey");
			trRow = $(this).parent();

			$.get("@{InvoiceGeneration.getBillingByBillingKey()}", {'billingKey':billingKey}, function(data) {
				checkRedirect(data);
				inv.billingKey.val(data.billingKey);
				inv.popCustomer.val(data.customer);
				inv.popCustomer.val(data.customer);
				inv.popCustomerDesc.val(data.customerDesc);
				inv.popAccount.val(data.account);
				inv.popAccountDesc.val(data.accountDesc);
				inv.popMonth.val(data.invMonth);
				inv.popYear.val(data.invYear);
				inv.popInvDate.val((new Date(data.invDate)).fmtDateMDY("/"));
				inv.popInvDueDate.val((new Date(data.invDueDate)).fmtDateMDY("/"));
				
				var vTable = $("#tblBillingDetail").dataTable();
				$("thead", vTable).click();
				
				var oSettings = vTable.fnSettings();
				var iTotalRecords = oSettings.fnRecordsTotal();
				for (i=0 ; i<=iTotalRecords; i++) {
					vTable.fnDeleteRow(0,null,true);
				}
				
				var totAmtAfterTax = 0;
				for (var i = 0; i < data.details.length; i++) {
					var detailrow = data.details[i];
					var rows = [detailrow.chargeGroup.lookupDescription, detailrow.chargeGroup.lookupDescription, $("div").autoNumericSet(detailrow.billingFee)[0].value, $("div").autoNumericSet(detailrow.billingTax)[0].value, $("div").autoNumericSet(detailrow.billingAmount)[0].value];
					totAmtAfterTax += detailrow.billingAmount;
					vTable.fnAddData(rows);
				}
				$("tbody tr td", vTable).attr("align", "right");
				$("tbody tr td:first-child", vTable).attr("align", "left");
				
				inv.popTotAmtAfterTax.val($("div").autoNumericSet(totAmtAfterTax)[0].value);
				
				inv.popupDetail.dialog("open");
				inv.btnCancel.focus();
			});
		});
		
		
		$("#tblBilling").dataTable().fnAdjustColumnSizing();
		
		inv.btnSave.click(function(){
			var invDate = inv.popInvDate.val();
			var invDueDate = inv.popInvDueDate.val();
			var message = ""; 
			if (invDate == '') { message += "Invoice Date is required\n"; }
			if (invDueDate == '') { message += "Invoice Due Date is required"; }
			if (message == "") {
				if (new Date(invDate) >= new Date(invDueDate)) {
					message = "Invoice Date can not be greater then Invoice Due Date";
				}
			}
			
			if (message == "") {
				$.get("@{InvoiceGeneration.updateBillingDate()}", {'billingKey':inv.billingKey.val(), 'invoiceDate':invDate.fmtYYYYMMDD("/"), 'dueDate':invDueDate.fmtYYYYMMDD("/")}, function(data) {
					checkRedirect(data);
					if (data == 1) {
						if (trRow != null) {
							$("td:nth-child(6)", trRow).html(invDate);
							$("td:nth-child(7)", trRow).html(invDueDate);
							trRow == null;
						}
						alert("Execute Success");
						inv.popupDetail.dialog("close");
					}else{
						alert("Execute Fail");
					}
				});
			}else{
				alert(message);
			}
		});
		
		inv.btnCancel.click(function(){
			inv.popupDetail.dialog("close");
		});
	}else{
		return new InvoiceGeneration(html);
	}
}
