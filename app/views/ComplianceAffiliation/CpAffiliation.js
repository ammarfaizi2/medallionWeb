function CpAffiliation(html) {
	if (this instanceof CpAffiliation) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var aff = html.inject(this);
		
		var COUNTER_PART = "THIRD_PARTY-COUNTER_PART";
		var C_ISSUER = "THIRD_PARTY-ISSUER";
		
		var table = aff.issuerTable.dataTable({
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
			var keys = aff.issuerkeys.val();
			var arrKeys = keys.split("-");
			for (x in arrKeys) {
				if (arrKeys[x] == id) { return true; }
			}
			return false;
		}
		
		function addKey(id) {
			var keys = aff.issuerkeys.val();
			if (keys == '') keys += id;
			else keys += "-"+id;
			aff.issuerkeys.val(keys);
		}
		
		function replaceKey(oldId, id) {
			var keys = aff.issuerkeys.val();
			keys = keys.replace(oldId, id);
			aff.issuerkeys.val(keys);
		}
		
		function removeKey(id) {
			var keys = aff.issuerkeys.val();
			keys = keys.replace("-"+id, "");
			keys = keys.replace(id+"-", "");
			keys = keys.replace(id, "");
			aff.issuerkeys.val(keys);
		}
		
		function attachEventButton() {
			var cells = $("input", aff.issuerTable.tbody());
			for (i = 0; i < cells.length; i++) {
				$(cells[i]).button().unbind().click(function(){
					var con = confirm("Are you sure want to delete issuer ["+$(this).attr("code")+"] ?");
					if (con) {
						var rowa = $(this).closest("tr").get(0);
						table.fnDeleteRow(rowa);
						removeKey($(this).attr("key"));
					}
					return false;
				});
				
				var isReadOnly = aff.isReadOnly.val();
				if (isReadOnly == 'true') {
					$(cells[i]).button("option", "disabled", true);
				}
			}
		}
		
		function attachEventRow() {
			$("tr", aff.issuerTable.tbody()).die('click');
			$("tr", aff.issuerTable.tbody()).live('click', function(){
				if (table.fnGetNodes().length == 0) {
					return false;
				}
				var data = table.fnGetData(this);
				editKey = $("input", $(this)).attr("key");
				editPos = table.fnGetPosition(this);
				var counterParty;
				if(data[0]=='${CounterParty}'){
					counterParty = COUNTER_PART;
					$('#tParty1').attr('checked', true);
				} else if(data[0]=='${Issuer}'){
					counterParty = C_ISSUER;
					$('#tParty2').attr('checked', true);
				}
				
				var isReadOnly = aff.isReadOnly.val();
				if (isReadOnly == 'true') {
					aff.tParty1.disabled();
					aff.tParty2.disabled();
				} else {
					aff.tParty1.enabled();
					aff.tParty2.enabled();
				}
				
				html.getThirdParties({'type':counterParty, 'code':data[1]}, function(data){
					aff.issuer.val(data.name);
					aff.issuerDesc.val(data.description);
					aff.issuerKey.val(data.code);
					aff.h_issuerDesc.val(data.description);
					aff.dialogIssuer.dialog('open');
					aff.issuerHelp.focus();
				});
				
				issuerF();
				});
		}
		
		function clearIssuerEntry() {
			issuer = null;
			aff.issuer.val('');
			aff.issuerDesc.val('');
			aff.issuerKey.val('');
			aff.h_issuerDesc.val('');
			aff.issuerError.html('');
			aff.issuer.removeClass('fieldError');
		}
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/
		aff.dialogIssuer.dialog({
			autoOpen:false,
			modal:true,
			heigth:'200px',
			width:'570px',
			resizable:false
		});
/*================================================================== 
 * Event
 *================================================================== */
		aff.fundManager.dynapopup('PICK_GN_THIRD_PARTY', 'THIRD_PARTY-FUND_MANAGER', 'issuerTable', 
			function(data){
				aff.fundManager.removeClass('fieldError');
			},
			function(){
				aff.fundManager.addClass('fieldError');
				aff.fundManagerDesc.val('NOT FOUND');
				aff.fundManagerKey.val('');
				aff.fundManager.val('');
				aff.h_fundManagerDesc.val('');
			}
		);

//		aff.fundManager.popupThirdParties("?type=THIRD_PARTY-FUND_MANAGER", "issuerTable", function(data){
//			aff.fundManager.removeClass('fieldError');
//		}, function() {
//			aff.fundManager.addClass('fieldError');
//			aff.fundManagerDesc.val('NOT FOUND');
//			aff.fundManagerKey.val('');
//			aff.fundManager.val('');
//			aff.h_fundManagerDesc.val('');
//		});

		aff.btnNewIssuer.click(function(){
			aff.dialogIssuer.dialog('open');
			aff.issuerError.html('');
			$('#tParty1').attr('checked', true);
			issuerF();
		});
		
		if (aff.cancel)
			aff.cancel.click(function() {
				aff.affiliationForm.attr('action', '@{list()}');
				aff.affiliationForm.submit();
			});
		
		if (aff.close)
			aff.close.click(function() {
				aff.affiliationForm.attr('action', '@{list()}');
				aff.affiliationForm.submit();
			});
		
		if (aff.save)
			aff.save.click(function(){
				html.loadingDialog();	
				aff.affiliationForm.attr('action', "@{save()}?mode=${mode}");
				aff.affiliationForm.submit();
			});		
		
		if (aff.back)
			aff.back.click(function(){
				aff.affiliationForm.attr('action', '@{back()}?id=${id}&mode=${mode}');
				aff.affiliationForm.submit();
			});
		
		if (aff.confirm)
			aff.confirm.click(function(){
				html.loadingDialog();	
				aff.affiliationForm.attr('action', '@{confirm()}?mode=${mode}');
				aff.affiliationForm.submit();
			});	
		
		aff.isActive1.click(function(){
			aff.active.val(true);
		});
		
		aff.isActive2.click(function(){
			aff.active.val(false);
		});
		
		aff.tParty1.click(function(){
			issuerF();
			clearIssuerEntry();
		});
		
		aff.tParty2.click(function(){
			issuerF();
			clearIssuerEntry();
		});
		
/*================================================================== 
 * POPUP.....................................
 *================================================================== */

		function issuerF(){
			var counterParty;
			if($("input[name=tParty]")[0].checked == true){
				counterParty = COUNTER_PART;
			} else if($("input[name=tParty]")[1].checked == true){
				counterParty = C_ISSUER;
			}
			aff.issuer.dynapopup('PICK_GN_THIRD_PARTY', counterParty, 'issuerTable', 
				function(data){
					issuer = data;
					aff.issuer.removeClass('fieldError');
				},
				function(){
					issuer = null;
					aff.issuer.addClass('fieldError');
					aff.issuerDesc.val('NOT FOUND');
					aff.issuerKey.val('');
					aff.issuer.val('');
					aff.h_issuerDesc.val('');
				}
			);
		}
		
//		aff.issuer.popupThirdParties("?type=THIRD_PARTY-ISSUER", "issuerTable", function(data){
//			issuer = data;
//			aff.issuer.removeClass('fieldError');
//		}, function() {
//			issuer = null;
//			aff.issuer.addClass('fieldError');
//			aff.issuerDesc.val('NOT FOUND');
//			aff.issuerKey.val('');
//			aff.issuer.val('');
//			aff.h_issuerDesc.val('');
//		});

		if (aff.btnCancelIssuer)
			aff.btnCancelIssuer.click(function(){
				aff.dialogIssuer.dialog('close');
				clearIssuerEntry();
				$('#tParty1').attr('checked', true);
			});
		
		if (aff.btnSaveIssuer)
			aff.btnSaveIssuer.click(function(){
				if (aff.issuer.val().isEmpty()) {
					//alert("Issuer Code can not be empty");
					aff.issuerError.html('Required');
				}else{
					var counterParty;
					if($("input[name=tParty]")[0].checked == true){
						counterParty = '${CounterParty}';
					} else if($("input[name=tParty]")[1].checked == true){
						counterParty = '${Issuer}';
					}
					
					if (isRowExist(issuer.code)) {
						//alert("Issuer With Code ["+issuer.name+"], already exist");
						aff.issuerError.html(counterParty+' With Code ['+issuer.name+'], already exist');
						return false;
					}else{
						
						if (editPos == null) {
							var btnId = 'btn'+issuer.code;				
							table.fnAddData([counterParty,issuer.name, issuer.description, "<center><input id="+btnId+" code="+issuer.name+" key="+issuer.code+" type='button' value='Delete'></center>"]);
							addKey(issuer.code);
						}else{
							var btnId = 'btn'+issuer.code;
							table.fnUpdate([counterParty,issuer.name, issuer.description, "<center><input id="+btnId+" code="+issuer.name+" key="+issuer.code+" type='button' value='Delete'></center>"], editPos);
							replaceKey(editKey, issuer.code);
						}
					}
					editPos = null;
					clearIssuerEntry();
					$('#tParty1').attr('checked', true);
					aff.dialogIssuer.dialog('close');
				}
			});
		
		if (aff.btnCloseIssuer)
			aff.btnCloseIssuer.click(function(){
				aff.dialogIssuer.dialog('close');
			});
		
		if (aff.dispatch.val() == 'entry' || (aff.dispatch.val() == 'edit' && aff.status.val() == 'R')) {
			aff.isActive1.disabled();
			aff.isActive2.disabled();
		}
		
		if (aff.dispatch.val() == 'view') {
			aff.btnNewIssuer.button("option", "disabled", true);
		}
		
/*================================================================== 
 * WORKFLOW  .....................................
 *================================================================== */
		if (aff.cancelWorkflow)
			aff.cancelWorkflow.click(function(){
				if (aff.from.val() == 'listBatch') {
					location = "@{Approvals.listbatch()}";
				} else {
					location = "@{Approvals.list()}";
				}
			});
		
		if (aff.reject)
			aff.reject.click(function(){
				var action = "@{reject()}?taskId=${taskId}&maintenanceLogKey=${maintenanceLogKey}&operation=${operation}";
				loading.dialog('open');
				$.post(action, function(data, status, xhr) {
		    		checkRedirect(data);
					loading.dialog('close');
					if (status == 'success') {
//						alert("Fund Manager Code ${aff?.fundManager?.thirdPartyCode} is Rejected " + data);
						messageAlertOk("Fund Manager Code '${aff?.fundManager?.thirdPartyCode}' is Rejected", "ui-icon ui-icon-circle-check", "Approval Message", backToWorkList);
					}
				});
			});
		
		if (aff.approve)
			aff.approve.click(function(){
				var action = "@{approve()}?taskId=${taskId}&maintenanceLogKey=${maintenanceLogKey}&operation=${operation}";
				loading.dialog('open');
				$.post(action, function(data, status, xhr) {
		    		checkRedirect(data);
					loading.dialog('close');
					if (status == 'success') {
//						alert("Fund Manager Code ${aff?.fundManager?.thirdPartyCode} is Approved " + data);
						messageAlertOk("Fund Manager Code '${aff?.fundManager?.thirdPartyCode}' is Approved", "ui-icon ui-icon-circle-check", "Approval Message", backToWorkList);
					}else{
//						alert("Fail to approve ${aff?.fundManager?.thirdPartyCode} " + data);
						messageAlertOk("Fail to Approve '${aff?.fundManager?.thirdPartyCode}'", "ui-icon1 ui-icon-circle-close", "Error Message", backToWorkList);
					}
				});
			});
		
		attachEventButton();
		attachEventRow();
	}else{
		return new CpAffiliation(html);
	}
}