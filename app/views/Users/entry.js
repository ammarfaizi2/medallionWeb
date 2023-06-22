function entry(html) {
	if (this instanceof entry) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject(this, false);
		
		var isConfirming = "${confirming}";
		var isConfirmSuccess = "${confirmSuccess}";
		var mode = "${mode}";
		
		
		app.dataTable = app.tblCashLimit.dataTable({
    		"bJQueryUI": true,
	        "bLengthChange": true,
	        "bDestroy":true,
			"sPaginationType": "full_numbers",
	        "bSort": false,
			"bScrollCollapse": true,
			"bFilter": false, 
			"bInfo": false,
			"bPaginate":false
    	});
		app.tblCashLimit.removeAttr('style');
		
		app.dlgCurrency.dynapopup2({key:'dlgCurrencyKey', help:'dlgCurrencyHelp', desc:'dlgCurrencyDesc'}, 'PICK_GN_CURRENCY', {}, 'dlgAmountLimit');
		app.dlgAmountLimit.autoNumeric({vMax: '99999999999999999999.99', dGroup: '3'});
		
		app.dlgEntry.dialog({
			autoOpen:false,
			modal:true,
			heigth:'600px',
			width:'600px',
			resizable:false,
			buttons: {
				"Save": function() { dialogSave(); },	
				"Close": function() { app.dlgEntry.dialog('close'); }
			}
		});
		
		app.dialogView.dialog({
			autoOpen:false,
			modal:true,
			heigth:'600px',
			width:'600px',
			resizable:false,
			buttons: {
				"Close": function() { app.dialogView.dialog('close'); }
			}
		});
		
/*==================================================================
 * Function
 *==================================================================*/
		function dialogSave() {
			if (validate()) {
				app.dlgEntry.dialog('close');
				
				var row = new Object();
				row.limitKey = -1;
				row.user = new Object();
				row.user.userKey = ($("#userKey").val() == '') ? '-1' : $("#userKey").val();
				row.currency = new Object();
				row.currency.currencyCode = app.dlgCurrency.val();
				row.currency.description = app.dlgCurrencyDesc.val();
				row.amount = new Number(app.dlgAmountLimit.autoNumericGet());
				row.type = new Object();
				row.type.lookupId = app.dlgAmountType.val();
				row.type.lookupDescription = $("option:selected", app.dlgAmountType).text();
				
				if (app.tr == null) {
					addToTable(row);
				}else{
					editToTable(row);
				}
			}
		}
		
		function validate() {
			app.dlgEntryErr.html('');
			app.dlgCurrencyErr.html('');
			app.dlgAmountLimitErr.html('');
			app.dlgAmountTypeErr.html('');
			
			var valid = true;
			
			if (app.dlgCurrency.val() == '') {
				app.dlgCurrencyErr.html('Required');
				valid = false;
			}
			if (app.dlgAmountLimit.val() == '') {
				app.dlgAmountLimitErr.html('Required');
				valid = false;
			}
			if (app.dlgAmountType.val() == '') {
				app.dlgAmountTypeErr.html('Required');
				valid = false;
			}
			
			if (app.tr != null) { app.tr.data("flag", "yes"); }
			$(app.dataTable.fnGetNodes()).each(function(i, e){
				if ($(e).data("flag") == null) {
					var bean = $(e).data('bean');				
					if (bean.currency.currencyCode == app.dlgCurrency.val()) {
						app.dlgEntryErr.html('Currency '+app.dlgCurrency.val()+', already exist');
						valid = false;
					}
				}
			});
			if (app.tr != null) { app.tr.removeData("flag"); }
			
			return valid;
		}
		
		function addToTable(row) {
			row.colCurrency = row.currency.currencyCode
			row.colAmountLimit = row.amount.fmtNumber(2);
			row.colAmountType = row.type.lookupDescription;
			row.colButton = "<input type='button' value='Delete'/>";
			
			var rownum = app.dataTable.fnAddData([row.colCurrency, row.colAmountLimit, row.colAmountType, row.colButton], true);
			var ptr = app.dataTable.fnSettings().aoData[rownum].nTr;
			$("td", ptr).each(function(idx, el){
				if (idx == 0)  { $(this).css("text-align", "left"); }
				if (idx == 1)  { $(this).css("text-align", "right"); }
				if (idx == 2)  { $(this).css("text-align", "left"); }
				if (idx == 3)  { $(this).css("text-align", "center"); }
			});
			$(ptr).data('bean', row);
			$("input[type='button']", ptr).button().data('ptr', ptr).click(function(){
				var ptr = $(this).data('ptr');
				
				messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", 
					function(){
						app.dataTable.fnDeleteRow(ptr);
						$("#dialog-message").dialog("close");
					}, 
					function(){
						$("#dialog-message").dialog("close");
					}
				);
			});
			$("td", $(ptr)).each(function(i, e){
				if (isConfirming || mode == 'view') {
					if (i != 3) {
						$(this).click(function(){
							var row = $(ptr).data("bean");
							app.dlgCurrency2.val(row.currency.currencyCode);
							app.dlgCurrency2Desc.val(row.currency.description);
							app.dlgAmountLimit2.val(row.amount.fmtNumber(2));
							app.dlgAmountType2.val(row.type.lookupId);
							app.dialogView.dialog('open');
						});
					}
				}else{
					if (i != 3) {
						$(this).click(function(){
							var row = $(ptr).data("bean");
							app.tr = $(ptr);
							
							app.dlgEntryErr.html('');
							app.dlgCurrencyErr.html('');
							app.dlgAmountLimitErr.html('');
							app.dlgAmountTypeErr.html('');
							
							app.dlgCurrency.removeClass("fieldError");
							
							app.dlgCurrencyKey.val(row.currency.currencyCode);
							app.dlgCurrency.val(row.currency.currencyCode);
							app.dlgCurrencyDesc.val(row.currency.description);
							app.dlgAmountLimit.val(row.amount.fmtNumber(2));
							app.dlgAmountType.val(row.type.lookupId);
							
							app.dlgEntry.dialog('open');
						});
					}
				}
			});
			app.dataTable.fnDraw();
		}
		
		function editToTable(row) {
			app.tr.data("bean", row);
			$("td", app.tr).eq(0).html(row.currency.currencyCode);
			$("td", app.tr).eq(1).html(row.amount.fmtNumber(2));
			$("td", app.tr).eq(2).html(row.type.lookupDescription);
		}
		
		function prepareSubmit() {
			var ctr = 0;
			$(app.dataTable.fnGetNodes()).each(function(i, e){
				var row = $(e).data('bean');
				app.prepareSubmit.append("<input name='user.limits["+ctr+"].limitKey' value='"+row.limitKey+"' />");
				app.prepareSubmit.append("<input name='user.limits["+ctr+"].user.userKey' value='"+row.user.userKey+"' />");
				app.prepareSubmit.append("<input name='user.limits["+ctr+"].currency.currencyCode'  value='"+row.currency.currencyCode+"' />");
				app.prepareSubmit.append("<input name='user.limits["+ctr+"].currency.description'  value='"+row.currency.description+"' />");
				app.prepareSubmit.append("<input name='user.limits["+ctr+"].amount' value='"+row.amount+"'/>");
				app.prepareSubmit.append("<input name='user.limits["+ctr+"].type.lookupId' value='"+row.type.lookupId+"'/>");
				app.prepareSubmit.append("<input name='user.limits["+ctr+"].type.lookupDescription' value='"+row.type.lookupDescription+"'/>");
				ctr++;
			});
		}
		
		app.prepareSubmit.bind("prepareSubmit", function(){
			prepareSubmit();
		});
		
		jQuery.fn.userDataDummy = function generateData() {
			for (var i = 0; i < 20; i++) {
				var row = new Object();
				row.limitKey = -1;
				row.user = new Object();
				row.user.userKey = $("#userKey").val();
				row.currency = new Object();
				row.currency.currencyCode = 'IDR';
				row.currency.description = 'Rupiah Indonesia';
				row.amount = 500000;
				row.type = new Object();
				row.type.lookupId = 'AMOUNT_TYPE-TOTAL';
				row.type.lookupDescription = 'TOTAL-LIMIT APPROVAL AS TOTAL';
				addToTable(row);
			}
		}
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/

/*================================================================== 
 * Event
 *================================================================== */
		app.btnNewData.button().click(function(){
			app.tr = null;
			
			app.dlgEntryErr.html('');
			app.dlgCurrencyErr.html('');
			app.dlgAmountLimitErr.html('');
			app.dlgAmountTypeErr.html('');
			
			app.dlgCurrency.removeClass("fieldError");
			
			app.dlgCurrency.val('');
			app.dlgCurrencyDesc.val('');
			app.dlgAmountLimit.val('');
			app.dlgAmountType.val('');
			
			app.dlgEntry.dialog('open');
		});
		
		var user = $.parseJSON($("#json").val());
		console.log(user);
		console.log(user.confirmPassword);
		console.log("user "+ user.limits);
		for (x in user.limits) {
			var row = user.limits[x];
			addToTable(row);
		}
		
		if (isConfirming == 'true' || mode == 'view') {
			app.btnNewData.button({ disabled: true });
			$("input[type='button']", app.dataTable).button({ disabled: true });
		}
		
//		if (isConfirmSuccess == 'true') {
//			app.dialogConfirmSuccess.dialog('open');
//		}
	}else{
		return new entry(html);
	}
}
