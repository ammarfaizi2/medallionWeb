function entry(html) {
	if (this instanceof entry) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject2(this, false);
		var confirming = "${confirming}"
		
		$().add(app.saveSpecCustomer).add(app.cancelSpecCustomer).add(app.addCustomer).add(app.btnSave).add(app.btnCancel).add(app.btnConfirm).add(app.btnBack).button();
		
		app.dataTable = app.tblCustomer.dataTable({
    		"bJQueryUI": true,
	        "bLengthChange": true,
	        "bDestroy":true,
			"sPaginationType": "full_numbers",
	        "bSort": false,
			"bScrollCollapse": true,
			"bFilter": true, 
			"bInfo": true,
			"bPaginate":true
    	});
		
		$("label", app.pEntry).css('width','75px');
		
/*==================================================================
 * Function
 *==================================================================*/
		function addToTable(row) {
			row.colCustomerNo = row.customer.customerNo;
			row.colCustomerName = row.customer.customerName;
			row.colButton = "<input type='button' value='Delete'/>";
			
			var rownum = app.dataTable.fnAddData([row.colCustomerNo, row.colCustomerName, row.colButton], true);
			var ptr = app.dataTable.fnSettings().aoData[rownum].nTr;
			$("td", ptr).each(function(idx, el){
				if (idx < 2) { $(this).css("text-align", "left"); }
				if (idx >= 2)  { $(this).css("text-align", "center"); }
			});
			$(ptr).data('bean', row);
			
			var button = $("input[type='button']", ptr).button().data('ptr', ptr).click(function(){
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
			
			if (confirming == 'true') {
				button.button({ disabled: true });
			}
			app.dataTable.fnDraw();
		}
		
		function validate() {
			app.obligasiPriceGroupErr.html('');
			app.sahamPriceGroupErr.html('');
			
			var valid = true;
			if (app.obligasiPriceGroup.val() == '') {
				app.obligasiPriceGroupErr.html('Required');
				valid = false;
			}
			
			if (app.sahamPriceGroup.val() == '') {
				app.sahamPriceGroupErr.html('Required');
				valid = false;
			}
			
			return valid;
		}
		
		function validateEntry() {
			app.customerNoErr.html('');
			app.dialogErr.html('');
						
			var valid = true;
			if (app.customerNo.val() == '') {
				app.customerNoErr.html('Required');
				valid = false;
			}
			
			$(app.dataTable.fnGetNodes()).each(function(i, e){
				var row = $(e).data('bean');
				if (row.customer.customerKey == app.customerKey.val()) {
					app.dialogErr.html('* Customer already exist');
					valid = false;
				}
			});
			
			return valid;
		}
		
		function dialogSave() {
			if (validateEntry()) {
				app.dlgEntry.dialog('close');

				var row = {
					bpjsDetailKey : -1,
					customer : {
						customerKey : app.customerKey.val(),
						customerNo : app.customerNo.val(),
						customerName : app.customerName.val(),
					}
				}
				
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
				app.bpjsForm.append("<input type='hidden' name='bpjs.bpjsDetails["+ctr+"].bpjsDetailKey' value='"+row.bpjsDetailKey+"' />");
				app.bpjsForm.append("<input type='hidden' name='bpjs.bpjsDetails["+ctr+"].customer.customerKey' value='"+row.customer.customerKey+"' />");
				app.bpjsForm.append("<input type='hidden' name='bpjs.bpjsDetails["+ctr+"].customer.customerNo' value='"+row.customer.customerNo+"' />");
				app.bpjsForm.append("<input type='hidden' name='bpjs.bpjsDetails["+ctr+"].customer.customerName' value='"+row.customer.customerName+"'/>");
				ctr++;
			});
		}
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/
		
		app.dlgEntry.dialog({
			autoOpen:false,
			modal:true,
			heigth:'200px',
			width:'530px',
			resizable:false,
			buttons: {
				"Save": function() { dialogSave(); },	
				"Close": function() { 
					app.dlgEntry.dialog('close');
				}
			}
		});
		
		app.obligasiPriceGroup.lookup({
			list : '@{Pick.lookups()}?group=SECURITY_PRICE_GROUP',
			get : {
				url : '@{Pick.lookup()}?group=SECURITY_PRICE_GROUP',
				success: function(data){
					app.obligasiPriceGroup.removeClass('fieldError');
					app.obligasiPriceGroupId.val(data.code);
					app.obligasiPriceGroupName.val(data.description);
					app.h_obligasiPriceGroupName.val(data.description);
				},
				error : function(data){
					app.obligasiPriceGroup.addClass('fieldError');
					app.obligasiPriceGroupId.val('');
					app.obligasiPriceGroup.val('');
					app.obligasiPriceGroupName.val('NOT FOUND');
					app.h_obligasiPriceGroupName.val('');
				}
			},
			description : app.obligasiPriceGroupName,
			help : app.obligasiPriceGroupHelp
		});
		
		app.sahamPriceGroup.lookup({
			list : '@{Pick.lookups()}?group=SECURITY_PRICE_GROUP',
			get : {
				url : '@{Pick.lookup()}?group=SECURITY_PRICE_GROUP',
				success : function(data){
					app.sahamPriceGroup.removeClass('fieldError');
					app.sahamPriceGroupId.val(data.code);
					app.sahamPriceGroupName.val(data.description);
					app.h_sahamPriceGroupName.val(data.description);
				},
				error : function(data){
					app.sahamPriceGroup.addClass('fieldError');
					app.sahamPriceGroupId.val('');
					app.sahamPriceGroup.val('');
					app.sahamPriceGroupName.val('NOT FOUND');
					app.h_sahamPriceGroupName.val('');
				}
			},
			description : app.sahamPriceGroupName,
			help : app.sahamPriceGroupHelp
		});
		
		app.customerNo.lookup({
			list : '@{Pick.customers2()}',
			get : {
				url : '@{Pick.customer()}',
				success : function(data) {
					app.customerNo.removeClass('fieldError');
					app.customerKey.val(data.code);
					app.customerNo.val(data.customerNo);
					app.customerName.val(data.description);
					app.h_customerName.val(data.description);
					app.customerNoErr.html('');
				},
				error : function() {
					app.customerNo.addClass('fieldError');
					app.customerKey.val('');
					app.customerNo.val('');
					app.customerName.val('NOT FOUND');
					app.h_customerName.val('');
				}						
			},
			description : app.customerName,
			help :app.customerHelp
		}); 
/*================================================================== 
 * Event
 *================================================================== */
		app.addCustomer.click(function(){
			app.tr = null;
			
			app.dialogErr.html('');
			app.customerNoErr.html('');

			app.customerNo.val('');
			app.customerNo.removeClass('fieldError');
			app.customerKey.val('');
			app.customerName.val('');
			
			app.dlgEntry.dialog('open');
		});
		
		app.btnCancel.click(function(){
			app.btnCancel.button({ disabled: true });
			app.btnSave.button({ disabled: true });
			app.addCustomer.button({ disabled: true });
			$("input[type='button']", app.dataTable.fnGetNodes()).button({ disabled: true });
			
			app.bpjsForm.attr('action', "@{list()}");
			app.bpjsForm.submit();
		});
		
		app.btnSave.click(function(){
			if (validate()) {
				app.btnCancel.button({ disabled: true });
				app.btnSave.button({ disabled: true });
				app.addCustomer.button({ disabled: true });
				$("input[type='button']", app.dataTable.fnGetNodes()).button({ disabled: true });
				
				prepareSubmit();
				app.bpjsForm.attr('action', "@{save()}");
				app.bpjsForm.submit();
			}
		});
		
		app.btnBack.click(function(){
			app.btnConfirm.button({ disabled: true });
			app.btnBack.button({ disabled: true });
			
			location.href="@{back()}/"+app.bpjsKey.val();
		});
		
		app.btnConfirm.click(function(){
			app.btnConfirm.button({ disabled: true });
			app.btnBack.button({ disabled: true });
			
			app.bpjsForm.attr('action', "@{confirm()}/"+app.bpjsKey.val()+"?mode="+app.mode.val());
			app.bpjsForm.submit();
		});
		
		var bpjsdetails = $.parseJSON(app.json.val());
		for (x in bpjsdetails) {
			var row = bpjsdetails[x];
			addToTable(row);
		}
		
		if (confirming == 'true') {
			app.divEntry.hide();
			app.divConfirm.show();
		}else{
			app.divEntry.show();
			app.divConfirm.hide();
		}
	}else{
		return new entry(html);
	}
}