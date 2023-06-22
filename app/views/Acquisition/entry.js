function entry(html) {
	if (this instanceof entry) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject2(this, false);
		
		// UI cleanup
		$("#csTransaction").show();
		$("input", "#csTransaction").attr("disabled", "disabled");
		$("textarea", "#csTransaction").attr("disabled", "disabled");
		$("#tabs").after(app.acquisitionTab);
		app.tab1.html($("#tab1").html());
		$("#prev1", app.tab1).remove();
		$("#next1", app.tab1).remove();
		$("#tabs").remove();
		$("#cancel").remove();
		$("#save").remove();
		$("#confirm").remove();
		$("#back").remove();
		$("#saveSettle").remove();
		$("#close").remove();
		$(".rightBuyCell").remove();
		$("h2", $("#csTransaction")).eq(0).html("Acquisition Maintenance");
		
		var mode = "${mode}";
		var fromWf = "${from}";		
		var isConfirming = "${confirming}";
		var isConfirmSuccess = "${confirmSuccess}";
		
		
/*==================================================================
 * Function
 *==================================================================*/
		function editToTable(row) {
			row.nominal = new Number(app.priceUnit.val());
			row.nominal = row.quantity * row.price * row.nominal;
			
			app.tr.data("bean", row);
			$("td", app.tr).eq(0).html(row.acquisitionDate.fmtPattern('dd/MM/yyyy'));
			$("td", app.tr).eq(1).html(row.quantity.fmtNumber(2));
			$("td", app.tr).eq(2).html(row.price.fmtNumber(2));
			$("td", app.tr).eq(3).html(row.nominal.fmtNumber(2));
		}
		
		function addToTable(row) {
			row.colAcquisitionDate = row.acquisitionDate.fmtPattern('dd/MM/yyyy');
			row.colQuantity = row.quantity.fmtNumber(2);
			row.colPrice = row.price.fmtNumber(2);
			row.colNominal= row.nominal.fmtNumber(2);
			row.colButton = "<input type='button' value='Delete'/>";
			
			var rownum = app.dataTable.fnAddData([row.colAcquisitionDate, row.colQuantity, row.colPrice, row.colNominal, row.colButton], true);
			var ptr = app.dataTable.fnSettings().aoData[rownum].nTr;
			$("td", ptr).each(function(idx, el){
				if (idx > 0 && idx < 4) { $(this).css("text-align", "right"); }
				if (idx >= 4)  { $(this).css("text-align", "center"); }
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
					if (i != 4) {
						$(this).click(function(){
							var row = $(ptr).data("bean");
							app.dlgAcquisitionDate2.val(row.acquisitionDate.fmtPattern('dd/MM/yyyy'))
							app.dlgQuantity2.val(row.quantity.fmtNumber(2));
							app.dlgPrice2.val(row.price.fmtNumber(2));
							app.dlgNominal2.val(row.nominal.fmtNumber(2));
							app.dialogView.dialog('open');
						});
					}
				}else{
					if (i != 4) {
						$(this).click(function(){
							var row = $(ptr).data("bean");
							app.tr = $(ptr);
							
							app.dlgAcquisitionDateErr.html('');
							app.dlgQuantityErr.html('');
							app.dlgPriceErr.html('');
							app.dlgNominalErr.html('');
							
							app.dlgAcquisitionDate.val(row.acquisitionDate.fmtPattern('dd/MM/yyyy'))
							app.dlgAcquisitionDate.removeClass("fieldError");
							app.dlgQuantity.val(row.quantity.fmtNumber(2));
							app.dlgPrice.val(row.price.fmtNumber(2));
							app.dlgNominal.val(row.nominal.fmtNumber(2));
							app.dlgDummy.show();
							app.dialogEntry.dialog('open');
							app.dlgDummy.hide();
						});
					}
				}
			});
			app.dataTable.fnDraw();
		}
		
		function validate() {
			app.dlgAcquisitionDateErr.html('');
			app.dlgQuantityErr.html('');
			app.dlgPriceErr.html('');
			app.dlgNominalErr.html('');
			
			var valid = true;
			if (app.dlgAcquisitionDate.val() == '') {
				app.dlgAcquisitionDateErr.html('Required');
				valid = false;
			}
			if (app.dlgQuantity.val() == '') {
				app.dlgQuantityErr.html('Required');
				valid = false;
			}
			if (app.dlgPrice.val() == '') {
				app.dlgPriceErr.html('Required');
				valid = false;
			}
			
			// Validasi tidak bisa > dari CS4001.Settlement Date
			var acquisitionDate = app.dlgAcquisitionDate.datepicker("getDate");
			var settlementDate = $("#settlementDate").datepicker("getDate");
			if (acquisitionDate > settlementDate) {
				app.dlgAcquisitionDateErr.html('Must not greater then Settlement Date');
				valid = false;
			}
			
			// Validasi tidak bisa > dari CS4001.Quantity
			var aqQuantity = new Number(app.dlgQuantity.autoNumericGet());
			var quantity = new Number($("#quantity").autoNumericGet());
			if (aqQuantity > quantity) {
				app.dlgQuantityErr.html('Must not greater then Transaction Quantity');
				valid = false;
			}
			
			return valid;
		}
		
		function dialogSave() {
			if (validate()) {
				app.dialogEntry.dialog('close');
				
				var row = new Object();
				row.acquisitionDetailKey = -1;
				row.acquisition = new Object();
				row.acquisition.acquisitionKey = app.acquisitionKey.val();
				row.acquisitionDate = app.dlgAcquisitionDate.datepicker("getDate");
				row.quantity = new Number(app.dlgQuantity.autoNumericGet());
				row.price = new Number(app.dlgPrice.autoNumericGet());
				row.nominal = new Number(app.priceUnit.val());
				row.nominal = row.quantity * row.price * row.nominal;
				
				if (app.tr == null) {
					addToTable(row);
				}else{
					editToTable(row);
				}
			}
		}	
		
		function prepareSubmit() {
			var ctr = 0;
			$(app.dataTable.fnGetNodes()).each(function(i, e){
				var row = $(e).data('bean');
				app.acquisitionForm.append("<input name='acquisition.details["+ctr+"].acquisitionDetailKey'  value='"+row.acquisitionDetailKey+"' />");
				app.acquisitionForm.append("<input name='acquisition.details["+ctr+"].acquisitionDate'  value='"+row.acquisitionDate.fmtPattern('dd/MM/yyyy')+"' />");
				app.acquisitionForm.append("<input name='acquisition.details["+ctr+"].quantity'  value='"+row.quantity+"' />");
				app.acquisitionForm.append("<input name='acquisition.details["+ctr+"].price'  value='"+row.price+"'/>");
				ctr++;
			});
			console.log(app.acquisitionForm.html());
		}
		
		 jQuery.fn.acqusitionDataDummy = function generateData() {
			for (var i = 0; i < 20; i++) {
				var row = new Object();
				row.acquisitionDetailKey = -1;
				row.acquisition = new Object();
				row.acquisition.acquisitionKey = -1;
				app.dlgAcquisitionDate.val((10+i)+'/05/2017');
				row.acquisitionDate = app.dlgAcquisitionDate.datepicker("getDate")
				row.quantity = 50000
				row.price = 1+i;
				row.nominal = new Number(app.priceUnit.val());
				row.nominal = row.quantity * row.price * row.nominal;
				addToTable(row);
			}
		}
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/

/*================================================================== 
 * Event
 *================================================================== */
		
		$().add(app.dlgQuantity).add(app.dlgPrice).blur(function(){
			var quantity = new Number(app.dlgQuantity.autoNumericGet());
			var price = new Number(app.dlgPrice.autoNumericGet());
			var priceUnit = new Number(app.priceUnit.val());
			var nominal = quantity * price * priceUnit;
			app.dlgNominal.autoNumericSet(nominal);
		});
		
		app.acquisitionTab.tabs();
		
		app.dataTable = app.tblAcquisition.dataTable({
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
		app.tblAcquisition.removeAttr('style');
		
		app.dialogEntry.dialog({
			autoOpen:false,
			modal:true,
			heigth:'600px',
			width:'465px',
			resizable:false,
			buttons: {
				"Save": function() { dialogSave(); },	
				"Close": function() { 
					app.dialogEntry.dialog('close');
				}
			}
		});
		
		app.dialogView.dialog({
			autoOpen:false,
			modal:true,
			heigth:'600px',
			width:'465px',
			resizable:false,
			buttons: {
				"Close": function() { app.dialogView.dialog('close') }
			}
		});
		
		app.dialogConfirmSuccess.dialog({
			autoOpen:false,
			modal:true,
			heigth:'300px',
			width:'400px',
			resizable:false,
			buttons: {
				"Close": function() { 
					app.dialogView.dialog('close');
					location.href="@{list()}";
				}
			}
		});
		
		app.dlgQuantity.autoNumeric({vMax: '99999999999999999999.99', dGroup: '3'});
		app.dlgPrice.autoNumeric({vMax: '99999999999999999999.99', dGroup: '3'});
		app.dlgNominal.autoNumeric({vMax: '99999999999999999999.99', dGroup: '3'});
		
		app.addNewAquisition.button().click(function(){
			app.tr = null;
			app.dlgAcquisitionDateErr.html('');
			app.dlgQuantityErr.html('');
			app.dlgPriceErr.html('');
			app.dlgNominalErr.html('');
			
			app.dlgAcquisitionDate.val('');
			app.dlgAcquisitionDate.removeClass("fieldError");
			app.dlgQuantity.val('');
			app.dlgPrice.val('');
			app.dlgNominal.val('');
			app.dlgDummy.show();
			app.dialogEntry.dialog('open');
			app.dlgDummy.hide();
		});
		
		// entry
		$("#btnCancel").click(function(){
			loading.dialog('open');
			location.href="@{list()}";
		})
		
		$("#btnSave").click(function(){
			loading.dialog('open');
			prepareSubmit();
			app.acquisitionForm.attr('action', "@{save()}");
			app.acquisitionForm.submit();
		});
		
		$("#btnBack").click(function(){
			loading.dialog('open');
			location.href="@{back()}/"+app.acquisitionKey.val()+"?mode="+app.mode.val();
		})
		
		$("#btnConfirm").click(function(){
			loading.dialog('open');
			location.href="@{confirm()}/"+app.acquisitionKey.val()+"?mode="+app.mode.val();
		})
		
		// approval
		$("#btnCloseWf").click(function(){
			if (fromWf == 'listBatch') {
				location.href = "@{Approvals.listbatch()}";
			} else {
				location.href = "@{Approvals.list()}";
			}
		});
		
		$("#btnApproveWf").click(function(){
			var action = "@{approve()}?taskId=${taskId}&maintenanceLogKey=${maintenanceLogKey}&operation=${operation}";
			loading.dialog('open');
			$.post(action, function(data, status, xhr) {
	    		checkRedirect(data);
				loading.dialog('close');
				if (status == 'success') {
					messageAlertOk("Acquisition is Approved " + data, "ui-icon ui-icon-circle-check", "Approval Message", function(){ $("#btnCloseWf").click(); });
				}
			});
		});
		
		$("#btnRejectWf").click(function(){
			var action = "@{reject()}?taskId=${taskId}&maintenanceLogKey=${maintenanceLogKey}&operation=${operation}";
			loading.dialog('open');
			$.post(action, function(data, status, xhr) {
	    		checkRedirect(data);
				loading.dialog('close');
				if (status == 'success') {
					messageAlertOk("Acquisition is Rejected " + data, "ui-icon ui-icon-circle-check", "Approval Message", function(){ $("#btnCloseWf").click(); });
				}
			});
		});
		
		//app.hiddenProp.hide();
		var acquisition = $.parseJSON(app.json.val());
		for (x in acquisition.details) {
			var row = acquisition.details[x]
			row.acquisitionDate = new Date(row.acquisitionDate);
			row.nominal = new Number(app.priceUnit.val());
			row.nominal = row.quantity * row.price * row.nominal;
			addToTable(row);
		}
		
		if (isConfirming == 'true' || mode == 'view') {
			app.addNewAquisition.button({ disabled: true });
			$("input[type='button']", app.dataTable).button({ disabled: true });
		}
		
		if (isConfirmSuccess == 'true') {
			app.dialogConfirmSuccess.dialog('open');
		}
		
		app.acquisitionForm.hide();
	}else{
		return new entry(html);
	}
}
