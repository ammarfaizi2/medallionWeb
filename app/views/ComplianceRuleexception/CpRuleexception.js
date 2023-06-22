function CpRuleexception(html) {
	if (this instanceof CpRuleexception) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var rule = html.inject(this);
		var table = rule.issuerTable.dataTable({
//			error di IE, popupnya tidak ke hide			
//			"aoColumns": [
//		        { "sWidth": "30%", "bSortable": false},
//		        { "sWidth": "40%", "bSortable": false},
//		        { "sWidth": "30%", "sClass": "center", "bSortable": false },
//		    ],
		    bJQueryUI:true, 
	        sPaginationType: 'full_numbers',
	        bPaginate: true,
        	bLengthChange:false,
	        iDisplayLength:10,
	        fnDrawCallback : function(){
	        	 if (typeof attachEventButton != 'undefined' ) { attachEventButton(); }
	        	 if (typeof attachEventRow != 'undefined') { attachEventRow(); }
	        } 
		});
		var issuer = null;
		var editPos = null;
		var editKey = null;
		var backToWorkList = function() {
			loading.dialog('open');
			if ($("#from").val() == 'listBatch') {
				location = "@{Approvals.listbatch()}";
			} else {
				location = "@{Approvals.list()}";
			};
		};

/*==================================================================
 * Function
 *==================================================================*/
		function isRowExist(id) {
			var keys = rule.issuerkeys.val();
			var arrKeys = keys.split("-");
			for (x in arrKeys) {
				if (arrKeys[x] == id) { return true; }
			}
			return false;
		}
		
		function addKey(id) {
			var keys = rule.issuerkeys.val();
			if (keys == '') keys += id;
			else keys += "-"+id;
			rule.issuerkeys.val(keys);
		}
		
		function replaceKey(oldId, id) {
			var keys = rule.issuerkeys.val();
			keys = keys.replace(oldId, id);
			rule.issuerkeys.val(keys);
		}
		
		function removeKey(id) {
			var keys = rule.issuerkeys.val();
			keys = keys.replace("-"+id, "");
			keys = keys.replace(id+"-", "");
			keys = keys.replace(id, "");
			rule.issuerkeys.val(keys);
		}
		
		function attachEventButton() {
			var cells = $("input", rule.issuerTable.tbody());
			for (i = 0; i < cells.length; i++) {
				$(cells[i]).button().unbind().click(function(){
					var con = confirm("Are you sure want to delete issuer ["+$(this).attr("code")+"] ?");
					if (con) {
						var rowa = $(this).closest("tr").get(0);
						table.fnDeleteRow(rowa);
						removeKey($(this).attr("key"));
					}
				});
				
				var isReadOnly = rule.isReadOnly.val();
				if (isReadOnly == 'true') {
					$(cells[i]).button("option", "disabled", true);
				}
			}
		} 
		
		function attachEventRow() {
			$("tr", rule.issuerTable.tbody()).die('click');
			$("tr", rule.issuerTable.tbody()).live('click', function(){
				if (table.fnGetNodes().length == 0) {
					return false;
				}
				var data = table.fnGetData(this);
				editKey = $("input", $(this)).attr("key");
				editPos = table.fnGetPosition(this);
				html.getThirdParties({'type':'THIRD_PARTY-ISSUER', 'code':data[0]}, function(data){
					rule.issuer.val(data.name);
					rule.issuerDesc.val(data.description);
					rule.issuerKey.val(data.code);
					rule.h_issuerDesc.val(data.description);
					rule.dialogIssuer.dialog('open');
					rule.issuerHelp.focus();
				});				
			});
		}
		
		function clearIssuerEntry() {
			issuer = null;
			rule.issuer.val('');
			rule.issuerDesc.val('');
			rule.issuerKey.val('');
			rule.h_issuerDesc.val('');
			rule.issuerError.html('');
			rule.issuer.removeClass('fieldError');
		}
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/
		rule.dialogIssuer.dialog({
			autoOpen:false,
			modal:true,
			heigth:'200px',
			width:'570px',
			resizable:false
		});
/*================================================================== 
 * Event
 *================================================================== */
		rule.ruleCode.popupCprule("", "issuerTable", function(data){
			rule.ruleCode.removeClass('fieldError');
			rule.ruleCodeDesc.html(data.description);
			rule.h_ruleCodeDesc.val(data.description);
		}, function() {
			rule.ruleCode.addClass('fieldError');
			rule.ruleCodeDesc.html('NOT FOUND');
			rule.ruleCodeKey.val('');
			rule.ruleCode.val('');
			rule.h_ruleCodeDesc.val('');
		});
		
		rule.btnNewIssuer.click(function(){
			rule.dialogIssuer.dialog('open');
			rule.issuerError.html('');
		});
		
		if (rule.cancel)
			rule.cancel.click(function() {
				rule.ruleExceptionForm.attr('action', '@{list()}');
				rule.ruleExceptionForm.submit();
				
			});
		
		if (rule.close)
			rule.close.click(function() {
				rule.ruleExceptionForm.attr('action', '@{list()}');
				rule.ruleExceptionForm.submit();
			});

		
		if (rule.save)
			rule.save.click(function(){
				html.loadingDialog();	
				rule.ruleExceptionForm.attr('action', "@{save()}?mode=${mode}");
				rule.ruleExceptionForm.submit();
			});		
		
		if (rule.back)
			rule.back.click(function(){
				rule.ruleExceptionForm.attr('action', '@{back()}?id=${id}&mode=${mode}');
				rule.ruleExceptionForm.submit();
			});
		
		if (rule.confirm)
			rule.confirm.click(function(){
				html.loadingDialog();
				rule.ruleExceptionForm.attr('action', '@{confirm()}?mode=${mode}');
				rule.ruleExceptionForm.submit();
			});	
		
		rule.isActive1.click(function(){
			rule.active.val(true);
		});
		rule.isActive2.click(function(){
			rule.active.val(false);
		});
		
		if (rule.dispatch.val() == 'view') {
			rule.btnNewIssuer.button("option", "disabled", true);
		}


/*================================================================== 
 * POPUP.....................................
 *================================================================== */

		rule.issuer.dynapopup('PICK_GN_THIRD_PARTY', 'THIRD_PARTY-ISSUER', 'issuerTable', 
			function(data){
	    	  	issuer = data;
				rule.issuer.removeClass('fieldError');
			},
			function(){
				issuer = null;
				rule.issuer.addClass('fieldError');
				rule.issuerDesc.val('NOT FOUND');
				rule.issuerKey.val('');
				rule.issuer.val('');
				rule.h_issuerDesc.val('');
			}
		);

//		rule.issuer.popupThirdParties("?type=THIRD_PARTY-ISSUER", "issuerTable", function(data){
//			issuer = data;
//			rule.issuer.removeClass('fieldError');
//		}, function() {
//			issuer = null;
//			rule.issuer.addClass('fieldError');
//			rule.issuerDesc.val('NOT FOUND');
//			rule.issuerKey.val('');
//			rule.issuer.val('');
//			rule.h_issuerDesc.val('');
//		});

		if (rule.btnCancelIssuer)
			rule.btnCancelIssuer.click(function(){
				rule.dialogIssuer.dialog('close');
				clearIssuerEntry();
			});
		
		if (rule.btnSaveIssuer)
			rule.btnSaveIssuer.click(function(){
				if (rule.issuer.val().isEmpty()) {
					//alert("Issuer Code can not be empty");
					rule.issuerError.html('Required');
				}else{
					if (isRowExist(issuer.code)) {
						//alert("Issuer With Code ["+issuer.name+"], already exist");
						rule.issuerError.html('Issuer With Code ['+issuer.name+'], already exist');
						return false;
					}else{
						if (editPos == null) {
							var btnId = 'btn'+issuer.code;				
							table.fnAddData([issuer.name, issuer.description, "<center><input id="+btnId+" code="+issuer.name+" key="+issuer.code+" type='button' value='Delete'></center>"]);
							addKey(issuer.code);
						}else{
							var btnId = 'btn'+issuer.code;
							table.fnUpdate([issuer.name, issuer.description, "<center><input id="+btnId+" code="+issuer.name+" key="+issuer.code+" type='button' value='Delete'></center>"], editPos);
							replaceKey(editKey, issuer.code);
						}
					}
					editPos = null;
					clearIssuerEntry();
					rule.dialogIssuer.dialog('close');
				}
			});
		
		if (rule.btnCloseIssuer)
			rule.btnCloseIssuer.click(function(){
				rule.dialogIssuer.dialog('close');
			});
		
		if (rule.dispatch.val() == 'entry' || (rule.dispatch.val() == 'edit' && rule.status.val() == 'R')) {
			rule.isActive1.disabled();
			rule.isActive2.disabled();
		}
		
/*================================================================== 
 * WORKFLOW  .....................................
 *================================================================== */
		if (rule.cancelWorkflow)
			rule.cancelWorkflow.click(function(){
				if (rule.from.val() == 'listBatch') {
					location = "@{Approvals.listbatch()}";
				} else {
					location = "@{Approvals.list()}";
				}
			});
		
		if (rule.reject)
			rule.reject.click(function(){
				var action = "@{reject()}?taskId=${taskId}&maintenanceLogKey=${maintenanceLogKey}&operation=${operation}";
				loading.dialog('open');
				$.post(action, function(data, status, xhr) {
		    		checkRedirect(data);
					loading.dialog('close');
					if (status == 'success') {
//						alert("Rule Code ${rule?.rule?.ruleCode} is Rejected " + data);
						messageAlertOk("Rule Code '${rule?.rule?.ruleCode}' is Rejected", "ui-icon ui-icon-circle-check", "Approval Message", backToWorkList);
					}
				});
			});
		
		if (rule.approve)
			rule.approve.click(function(){
				var action = "@{approve()}?taskId=${taskId}&maintenanceLogKey=${maintenanceLogKey}&operation=${operation}";
				loading.dialog('open');
				$.post(action, function(data, status, xhr) {
		    		checkRedirect(data);
					loading.dialog('close');
					if (status == 'success') {
//						alert("Rule Code ${rule?.rule?.ruleCode} is Approved " + data);
						messageAlertOk("Rule Code '${rule?.rule?.ruleCode}' is Approved", "ui-icon ui-icon-circle-check", "Approval Message", backToWorkList);
					} else {
//						alert("Fail to approve ${rule?.rule?.ruleCode} " + data);
						messageAlertOk("Fail to Approve '${rule?.rule?.ruleCode}'", "ui-icon1 ui-icon-circle-close", "Error Message", backToWorkList);
					}
				});
			});

		attachEventButton();
		attachEventRow();
	}else{
		return new CpRuleexception(html);
	}
}