function CpRuleProfile(html) {
	if (this instanceof CpRuleProfile) {
/*================================================================== 
* GUI Variable
*================================================================== */
		var prof = html.inject(this);
		var table = prof.ruleTable.dataTable({
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
		var rule = null;
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
*================================================================== */	
		function isRowExist(id) {
			var keys = prof.rulekeys.val();
			var arrKeys = keys.split("-");
			for (x in arrKeys) {
				if (arrKeys[x] == id) { return true; }
			}
			return false;
		}
		
		function addKey(id) {
			var keys = prof.rulekeys.val();
			if (keys == '') keys += id;
			else keys += "-"+id;
			prof.rulekeys.val(keys);
		}
		
		function replaceKey(oldId, id) {
			var keys = prof.rulekeys.val();
			keys = keys.replace(oldId, id);
			prof.rulekeys.val(keys);
		}
		
		function removeKey(id) {
			var keys = prof.rulekeys.val();
			keys = keys.replace("-"+id, "");
			keys = keys.replace(id+"-", "");
			keys = keys.replace(id, "");
			prof.rulekeys.val(keys);
		}
		
		var closeDialog = function() {
			$("#dialog-message").dialog('close');	
		}
		
		function attachEventButton() {
			var cells = $("input", prof.ruleTable.tbody());
			
			for (i = 0; i < cells.length; i++) {
				$(cells[i]).button().unbind().click(function(){
					/*	var con = confirm("Are you sure want to delete rule ["+$(this).attr("code")+"] ?");
					
					if (con) {
						var rowa = $(this).closest("tr").get(0);
						table.fnDeleteRow(rowa);
						removeKey($(this).attr("key"));
					}
					*/
					var key = $(this).attr("key");
					var row = $(this).parents('tr');
					var rowNumber = table.fnGetPosition(row[0]);
					var deleteRuleGrid = function(){
						
						table.fnDeleteRow(rowNumber);
						removeKey(key);
						$("#dialog-message").dialog('close');
					}
					var con = messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message",deleteRuleGrid, closeDialog);
				});
				
				var isReadOnly = prof.isReadOnly.val();
				if (isReadOnly == 'true') {
					$(cells[i]).button("option", "disabled", true);
				}
			}
		} 

		function attachEventRow() {
			$("tr", prof.ruleTable.tbody()).die('click');
			$("tr td", prof.ruleTable.tbody()).live('click', function(){
				
				var rowPos= $(this).parent('tr');
				
				var rowPosNumber = table.fnGetPosition(rowPos[0]);
				var pos = table.fnGetPosition(this);
				if(pos[1] != 4){
					
					if (table.fnGetNodes().length == 0) {
						return false;
					}
					
					//var data = table.fnGetData(this);
					var data = table.fnGetData($(this).parent()[0]);
					
					editKey = $("input", $(this)).attr("key");
					editPos = table.fnGetPosition(this);
					html.getCprule({'code':data[0]}, function(data){
						prof.rule.val(data.name);
						prof.ruleDesc.html(data.description);
						prof.ruleKey.val(data.code);
						prof.h_ruleDesc.val(data.description);
						
						prof.ruleType.val(data.type);
						prof.ruleTypeDesc.val(data.typeDesc);
						
						prof.operator.val(data.operator);
						prof.operatorVal.val(data.value);
						
						prof.comparisonVal.val(data.comparisonDesc);
						
						prof.dialogRule.dialog('open');
						prof.ruleHelp.focus();
					});				
			    }
			});
		}
		
		function clearRuleEntry() {
			issuer = null;
			prof.rule.val('');
			prof.ruleDesc.html('');
			prof.ruleKey.val('');
			prof.h_ruleDesc.val('');
			prof.ruleType.val('');
			prof.ruleTypeDesc.val('');
			prof.operator.val('');
			prof.operatorVal.val('');
			prof.comparisonVal.val('');
			prof.ruleError.html('');
			prof.rule.removeClass('fieldError');
		}
				
/*==================================================================
* Declare popup tabel /detail
* ================================================================= */
		prof.dialogRule.dialog({
			autoOpen:false,
			modal:true,
			heigth:'200px',
			width:'525px',
			resizable:false
		});

/*================================================================== 
* Event
*================================================================== */
		prof.btnNewRule.click(function(){
			prof.dialogRule.dialog('open');
			prof.ruleError.html('');
		});
		
		if (prof.cancel)
			prof.cancel.click(function() {
				prof.ruleProfileForm.attr('action', '@{list()}');
				prof.ruleProfileForm.submit();
			});
		
		if (prof.close)
			prof.close.click(function() {
				prof.ruleProfileForm.attr('action', '@{list()}');
				prof.ruleProfileForm.submit();
			});
		
		if (prof.save)
			prof.save.click(function(){
				html.loadingDialog();	
				prof.ruleProfileForm.attr('action', "@{save()}?mode=${mode}");
				prof.ruleProfileForm.submit();
			});		
		
		if (prof.back)
			prof.back.click(function(){
				prof.ruleProfileForm.attr('action', '@{back()}?id=${id}&mode=${mode}');
				prof.ruleProfileForm.submit();
			});
		
		if (prof.confirm)
			prof.confirm.click(function(){
				prof.ruleProfileForm.attr('action', '@{confirm()}?mode=${mode}');
				prof.ruleProfileForm.submit();
			});	
		
		prof.isActive1.click(function(){
			prof.active.val(true);
		});
		
		prof.isActive2.click(function(){
			prof.active.val(false);
		});
/*================================================================== 
* POPUP.....................................
*================================================================== */
		prof.rule.popupCprule('', 'btnSaveRule', function(data){
			rule = data;
			prof.rule.removeClass('fieldError');
			prof.ruleDesc.html(data.description);
			prof.h_ruleDesc.val(data.description);
			prof.ruleType.val(data.type);
			prof.ruleTypeDesc.val(data.typeDesc);
			prof.operator.val(data.operator);
			prof.operatorVal.val(data.value);
			prof.comparisonVal.val(data.comparisonDesc);
		}, function() {
			rule = null;
			prof.rule.addClass('fieldError');
			prof.ruleDesc.html('NOT FOUND');
			prof.h_ruleDesc.val('');
			prof.ruleKey.val('');
			prof.rule.val('');
			prof.ruleType.val('');
			prof.ruleTypeDesc.val('');
			prof.operator.val('');
			prof.operatorVal.val('');
			prof.comparisonVal.val('');
		});
		
		if (prof.btnCancelRule)
			prof.btnCancelRule.click(function(){
				prof.dialogRule.dialog('close');
				clearRuleEntry();
			});
		
		if (prof.btnSaveRule)
			prof.btnSaveRule.click(function(){
				if (prof.rule.val().isEmpty()) {
					//alert("Rule Code can not be empty");
					prof.ruleError.html('Required');
				}else{
					
					if (isRowExist(rule.code)) {
						//alert("Rule With Code ["+rule.name+"], already exist");
						prof.ruleError.html('Rule With Code ['+rule.name+'], already exist');
						return false;
					}else{
						if (editPos == null) {
							var btnId = 'btn'+rule.code;				
							var operator = (rule.operator) ? rule.operator : ''; 
							var value = (rule.value) ? rule.value : '';
							table.fnAddData([rule.name, rule.typeDesc, "<center>"+operator+"</center>", "<center>"+value+"</center>", "<center><input id="+btnId+" code="+rule.name+" key="+rule.code+" type='button' value='Delete'></center>"]);
							addKey(rule.code);
						}else{
							var btnId = 'btn'+rule.code;				
							var operator = (rule.operator) ? rule.operator : ''; 
							var value = (rule.value) ? rule.value : '';
							table.fnUpdate([rule.name, rule.typeDesc, "<center>"+operator+"</center>", "<center>"+value+"</center>", "<center><input id="+btnId+" code="+rule.name+" key="+rule.code+" type='button' value='Delete'></center>"], editPos);
							replaceKey(editKey, rule.code);
						}
					}
					editPos = null;
					clearRuleEntry();
					
					prof.dialogRule.dialog('close');
				}
			});
		
		if (prof.btnCloseRule)
			prof.btnCloseRule.click(function(){
				prof.dialogRule.dialog('close');
			});
		
		if (prof.dispatch.val() == 'entry' || (prof.dispatch.val() == 'edit' && prof.status.val() == 'R')) {
			prof.isActive1.disabled();
			prof.isActive2.disabled();
		}
		
		if (prof.dispatch.val() == 'view') {
			//prof.btnNewRule.button("option", "disabled", true);
			prof.btnNewRule.hide();
		}
/*================================================================== 
* WORKFLOW  .....................................
*================================================================== */
		if (prof.cancelWorkflow)
			prof.cancelWorkflow.click(function(){
				if (prof.from.val() == 'listBatch') {
					location = "@{Approvals.listbatch()}";
				} else {
					location = "@{Approvals.list()}";
				}
			});
		
		if (prof.reject)
			prof.reject.click(function(){
				var action = "@{reject()}?taskId=${taskId}&maintenanceLogKey=${maintenanceLogKey}&operation=${operation}";
				loading.dialog('open');
				$.post(action, function(data, status, xhr) {
		    		checkRedirect(data);
					loading.dialog('close');
					if (status == 'success') {
//						alert("Compliance Profile Code ${profile?.complianceProfCode} is Rejected " + data);
						messageAlertOk("Compliance Profile Code '${profile?.complianceProfCode}' is Rejected", "ui-icon ui-icon-circle-check", "Approval Message", backToWorkList);
					}
				});
			});
		
		if (prof.approve)
			prof.approve.click(function(){
				var action = "@{approve()}?taskId=${taskId}&maintenanceLogKey=${maintenanceLogKey}&operation=${operation}";
				loading.dialog('open');
				$.post(action, function(data, status, xhr) {
		    		checkRedirect(data);
					loading.dialog('close');
					if (status == 'success') {
//						alert("Compliance Profile Code ${profile?.complianceProfCode} is Approved " + data);
						messageAlertOk("Compliance Profile Code '${profile?.complianceProfCode}' is Approved", "ui-icon ui-icon-circle-check", "Approval Message", backToWorkList);
					}else{
//						alert("Fail to approve ${profile?.complianceProfCode} " + data);
						messageAlertOk("Fail to Approve '${profile?.complianceProfCode}'", "ui-icon1 ui-icon-circle-close", "Error Message", backToWorkList);
					}
				});
			});

		attachEventButton();
		attachEventRow();
	}else{
		return new CpRuleProfile(html);
	}
}