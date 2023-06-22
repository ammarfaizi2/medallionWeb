function PagingApprovals(html, fromSingle) {
	if (this instanceof PagingApprovals) {
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this);
		var fromSingle = fromSingle;
		var keepshowing = false; // sebagai flag apakah message hasil process di tampilkan atau tidak, krn process tidak synch dgn paging
		
		var APPROVE = "approve";
		var REJECT = "reject";
		var selectAll = "Select All";
		var unSelectAll = "Unselect All";
		
		var proses = true;
		
/* =========================================================================== 
 * Table Parameter
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.activity = app.activity.val();
			p.referenceno = app.referenceno.val();
			p.batchno = app.batchno.val();
			p.description = app.description.val();
			p.fromSingle = fromSingle;
			
			if (app.createdDate.datepicker('getDate') != null) 
				p.createdDateMili = app.createdDate.datepicker('getDate').getTime();
			
			p.query = true;
			return p;
		};
		
/* =========================================================================== 
 * Function
 * ========================================================================= */
		function errorMessage(clean, show) {
			if (clean) { app.errorMessage.html(""); }
			
			if (show) { app.errorMessage.show();
			}else { app.errorMessage.hide(); }
		}
		
		function generateUrl(bean) {
			var from =  fromSingle ? 'singleList' : 'listBatch';
			var refKey = (bean.refKey != undefined)? parseFloat(bean.refKey.substring(jQuery.trim(bean.refKey).indexOf(':') + 1)) : null;
			var keyObject = bean.key.replace(/#/g, '${specialChar}').toString();
			var url = '${ctx}' + bean.url + '?taskId=' + bean.taskId + '&keyId=' + keyObject + '&operation=' + bean.operation + '&maintenanceLogKey=' + bean.maintenanceLogKey + '&group=' + bean.group + '&refKey=' + refKey + '&from='+from + '&processDefinition=' + bean.processDefinition + '&type=' + bean.type;
			return url; 
		}
		
		function validateSelect() {
			if(app.selectAll.val() == unSelectAll){
				return true;
			} else {
				var props = app.datatable.selects(0, "checked");
				if (props.length == 0) {
					messageAlertOk("Must be at least one item to procced", "ui-icon ui-icon-circle-check", "Information", function(){
						return false;
					});
					return false;
				}else{
					return true;
				}
			}
		}
		
		function processBatch(processType) {
			if(app.selectAll.val() == unSelectAll){
				var sentence = $('#tblApproval_info').html();
				var firstTrim = sentence.indexOf('of') + 3;
				var lastTrim = sentence.indexOf('entries') - 1;
				var selectText = sentence.substring(firstTrim, lastTrim);
				messageAlertYesNo(
						"Total Item to be "+processType+" = "+selectText+", process ?",
						"ui-icon ui-icon-notice", "Confirmation Message", 
						function(){
							
							var p = new Object();
							p.activity = app.activity.val();
							p.referenceno = app.referenceno.val();
							p.batchno = app.batchno.val();
							p.description = app.description.val();
							p.fromSingle = fromSingle;
							if (app.createdDate.datepicker('getDate') != null) 
								p.createdDateMili = app.createdDate.datepicker('getDate').getTime();
							
							p.query = true;
							var listBatch = [];
							$.get("@{Approvals.processBatchSelectAll()}", {'param':p,'processType':processType}, function(data) {
								var jsonList = {};
								try{
									jsonList = JSON.parse(data);
								}catch(e){
									console.log("error processBatchSelectAll "+e);
								}
								
								for(x in jsonList ){
									listBatch[listBatch.length] = jsonList[x].taskId +'|'+ jsonList[x].maintenanceLogKey + '|'+ jsonList[x].operation + '|' + jsonList[x].processDefinition + '|' + jsonList[x].refKey +'|'+ jsonList[x].activity + '|'+ jsonList[x].key + '|'+ jsonList[x].type + '|'+ jsonList[x].createdDate;
								}
								
							});
							
							var action = "@{Approvals.processBatch()}";
							var postData = {
											"listBatch": listBatch,
											"processType": processType
										   };
							if(proses){
								$.post(action, postData, function(data, xhr) {
									$("#dialog-message").dialog("close");
//									var loadingDialog = html.loadingDialog().dialog("open");
									
									errorMessage(true, true);
									
									$("<p>Total item "+processType+" successfully = "+data.success.length+"</p>").appendTo(app.errorMessage);
									//$("<p>"+data.success+"</p>").appendTo(app.errorMessage);
									$("<p>Total item "+processType+" unsuccessfully = "+data.fail.length+"</p>").appendTo(app.errorMessage);
									//$("<p>"+data.fail+"</p>").appendTo(app.errorMessage);
									
									keepshowing = true;
//									loadingDialog.dialog("close");
									app.datatable.fnPageChange("first");
									if (!fromSingle) {
										var sizeTbl = app.datatable.fnGetNodes().length;
										if(sizeTbl > 0 && jQuery.trim(app.batchno.val()).length > 0){
											app.selectAll.removeAttr("disabled");
											app.selectAll.button('refresh');
										} else {
											app.selectAll.attr("disabled", "disabled");
											app.selectAll.button('refresh');
											app.selectAll.val(selectAll);
											$("#tblApproval td:first-child").show();
											$("#idcheck").css("display","");
										}
									}
								});
								proses = false;
							}
								
						}, 
						function(){ $("#dialog-message").dialog("close"); });
			} else {
				var props = app.datatable.selects(0, "checked");
				var listBatch = [];
				for (x in props) { 
					var approval = props[x].bean;
					listBatch[listBatch.length] = approval.taskId +'|'+ approval.maintenanceLogKey + '|'+ approval.operation + '|' + approval.processDefinition + '|' + approval.refKey +'|'+ approval.activity + '|'+ approval.key + '|'+ approval.type + '|'+ approval.createdDate; 
				}
				messageAlertYesNo(
					"Total Item to be "+processType+" = "+listBatch.length+", process ?", 
					"ui-icon ui-icon-notice", "Confirmation Message", 
					function(){
						var action = "@{Approvals.processBatch()}";
						var postData = {
								"listBatch": listBatch,
								"processType": processType
							};
						if(proses){
							$.post(action, postData, function(data, xhr) {
								$("#dialog-message").dialog("close");
								var loadingDialog = html.loadingDialog().dialog("open");
								
								errorMessage(true, true);
								
								$("<p>Total item "+processType+" successfully = "+data.success.length+"</p>").appendTo(app.errorMessage);
								//$("<p>"+data.success+"</p>").appendTo(app.errorMessage);
								$("<p>Total item "+processType+" unsuccessfully = "+data.fail.length+"</p>").appendTo(app.errorMessage);
								//$("<p>"+data.fail+"</p>").appendTo(app.errorMessage);
								
								keepshowing = true;
								loadingDialog.dialog("close");
								app.datatable.fnPageChange("first");
							});
							proses = false;
						}
							
					}, 
					function(){ $("#dialog-message").dialog("close"); });
			}
			
		}
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
//		app.tblApproval.hide();
		app.datatable = app.tblApproval.paging("@{Approvals.paging()}", this, function(){
			if (keepshowing) {
				keepshowing = false;
			}else{
				errorMessage(true, false);	
			}
			if (!fromSingle) {
				if(app.selectAll.val() == selectAll){
					$("#tblApproval td:first-child").show();
					$("#idcheck").css("display","");
					$("tbody tr", html.tag("tblApproval")).each(function(){
						var prop = $(this).data("prop");
						if (prop.bean) {
							if (prop.bean.bulkApproval) {
								$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled");
							}
						}
					});

				} else {
					$("#tblApproval td:first-child").hide();
					$("#idcheck").css("display","none");
				}
			}
		});
//		$("#"+app.datatable.attr("id")+"_filter").hide();
//		app.datatable.show();

/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		//activity, createdDate, description, key, module, processDefinition, recordCreatedDate, refKey, taskId, url, userKey
		//Example /registrysubscription/approval?taskId=685764&keyId=147518243&operation=MAINTENANCE_OPERATION-C&maintenanceLogKey=4840384&group=&refKey=147518243&from=singleList&processDefinition=RegistryTransaction
		app.datatable.bind("select", function(event, prop){
			
			var url = generateUrl(prop.bean);
			window.location = url;
		});
		
		app.datatable.bind("checkboxs", function(event, prop, isChecked){
//			alert("isChecked "+isChecked);
		});
		app.datatable.bind("checkbox", function(event, prop, isChecked){
//			alert("isChecked");
		});
		
		app.search.click(function(){
			errorMessage(true, false);
			app.datatable.fnPageChange("first");
			if (!fromSingle) {
				var sizeTbl = app.datatable.fnGetNodes().length;
				if(sizeTbl > 0 && jQuery.trim(app.batchno.val()).length > 0){
					app.selectAll.removeAttr("disabled");
					app.selectAll.button('refresh');
				} else {
					app.selectAll.attr("disabled", "disabled");
					app.selectAll.button('refresh');
					app.selectAll.val(selectAll);
					$("#tblApproval td:first-child").show();
					$("#idcheck").css("display","");
				}
			}
		});
		
		if (app.approve)
			app.approve.click(function(){
				if (validateSelect()) { proses = true; processBatch(APPROVE); } 
			});
		
		if (app.reject)
			app.reject.click(function(){
				if (validateSelect()) { proses = true; processBatch(REJECT); }
			});
		
		app.reset.click(function(){
			if (fromSingle) {
				app.approvalForm.attr('action', 'reset');
				app.approvalForm.submit();
			}else{
				app.approvalForm.attr('action', 'resetBatch');
				app.approvalForm.submit();
			}
		});
		
		if (!fromSingle) {
			app.selectAll.click(function(){
				if(app.selectAll.val() == selectAll){
					app.selectAll.val(unSelectAll);
					$("#tblApproval td:first-child").hide();
					$("#idcheck").css("display","none");
				} else {
					app.selectAll.val(selectAll);
					$("#tblApproval td:first-child").show();
					$("#idcheck").css("display","");
				}
				app.selectAll.removeAttr("disabled");
				app.selectAll.button('refresh');
				
				var myTable = app.datatable;
				var sizeTbl = app.datatable.fnGetNodes().length;
				for (x in sizeTbl) {
					var checkboxs = $("tbody tr td:nth-child("+(x+1)+") input[type=checkbox]", myTable);
					checkboxs.attr("checked", false);
				}
			});
		}
		
	}else{
		return new PagingApprovals(html, fromSingle);
	}
}