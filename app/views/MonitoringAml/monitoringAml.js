function MonitoringAml(html) {
	if (this instanceof MonitoringAml) {
/*================================================================== 
* GUI Variable
*================================================================== */
		var app = html.inject2(this, false);
				
		var dataTable = app.tblAmlMonitoringTable.dataTable({
			"bJQueryUI": true,
			"bLengthChange": true,
			"bDestroy":true,
			"sPaginationType": "full_numbers",
			"bSort": false,
			"bScrollCollapse": true
		});
		
		app.tblAmlMonitoringTable.wrap("<div style='overflow:scroll'>");
/*================================================================== 
* Event
*================================================================== */
		app.custRetailOf.lookup({
			list:'@{Pick.thirdParties()}?type=THIRD_PARTY-RETAIL',
			get:{
				url:'@{Pick.thirdParty()}?type=THIRD_PARTY-RETAIL',
				success: function(data){
					$('#custRetailOf').removeClass('fieldError');
					$('#custRetailOfKey').val(data.code);
					$('#custRetailOfDesc').val(data.description);
					$('#h_custRetailOfDesc').val(data.description);
				},
				error: function(data){
					$('#custRetailOf').addClass('fieldError');
					$('#custRetailOfKey').val('');
					$('#custRetailOf').val('');
					$('#custRetailOfDesc').val('NOT FOUND');
					$('#h_custRetailOfDesc').val('');
				}
			},
			description:$('#custRetailOfDesc'),
			help:$('#custRetailOfHelp')
		});
		
		$('#monitoringNoOperator').children().eq(0).remove();
		$('#monitoringNameOperator').children().eq(0).remove();
		$("#monitoringNoOperator").attr('selectedIndex', 1);
		$("#monitoringNameOperator").attr('selectedIndex', 1);
		
		$('.buttons input:button').button();
		// Focus on first input:text
		$('form input:text').first().focus();
		
		/* set sent to interface aml */
		var interface = false;
		$('#sentInterface').show();
		$("#customer").attr("checked", true);
		$("#benefOwner").click(function() {
			$("#benefOwner").attr("checked", true);
			$("#customer").attr("checked", false);
		});
		
		$("#customer").click(function() {
			$("#customer").attr("checked", true);
			$("#benefOwner").attr("checked", false);
		});
		
		$("#interface").click(function(){
			console.log("go interface");
			var interfaceKey;
			var prohibited = "";
			interfaceKey = $("#customer").val();
			if(!$(customer).is(":checked")){
				interfaceKey = $("#benefOwner").val();
			}

			var ctr = 0;
			var datas = [];
			$(dataTable.fnGetNodes()).each(function(i, e){
				var inputChk = $("input", $("td", $(this)).eq(0));
				var isChecked = inputChk.is(':checked');
				if (isChecked) {
					var tr = inputChk.parent().parent();
					tr.data("bean").colNo = ctr++; 
					datas[datas.length] = tr.data("bean");
				}
			});

			if (datas.length == 0) {
				var closeDialog = function(){
					$("#dialog-message").dialog("close");
				};
				var isCheck = $("#checkAll").is(':disabled');
				if(!isCheck){
					messageAlertOk("Please choose data, minimal 1 row", "ui-icon ui-icon-notice", "Warning", closeDialog);	
				}
			}else{
				loading.dialog('open');
				var isFlag = false;
				for (var x in datas) {
					var b = datas[x];

					console.log("interfaceKey: " + interfaceKey)
					if($("#apiStatus").val() == 'AML_API_STATUS-WRP'){
						isFlag = (interfaceKey == 'aml.tipeIndividu') ? true : false;
					}else if($("#apiStatus").val() == 'AML_API_STATUS-WBS'){
						isFlag = (interfaceKey == 'aml.tipeIndividu.bo') ? true : false;
					}else if($("#apiStatus").val() == 'AML_API_STATUS-WAE'){
						isFlag = (interfaceKey == 'aml.tipeIndividu') ? true : false;
					}else if($("#apiStatus").val() == 'AML_API_STATUS-WBE'){
						isFlag = (interfaceKey == 'aml.tipeIndividu.bo') ? true : false;
					}

					if(isFlag){
						if(($("#apiStatus").val() == 'AML_API_STATUS-WAE') || ($("#apiStatus").val() == 'AML_API_STATUS-WBE')){
							$().fetchSync("@{interfaceToAmlCheckEddStatus()}", {'amlKey' : amlKey}, function(datas) {
								var resultCode = datas.resultCode;
								var hostResultCode = datas.hostResultCode;
								if (resultCode != "10") {
									var closeDialogOk = function(){
										$("#dialog-message").dialog("close");
										if (resultCode == "205") {
											location.reload();
										}
									};
									messageAlertOk("Interface to AML failed for High Risk. Please retry again " + datas.resultMessage, "ui-icon ui-icon-notice", "Warning", closeDialogOk);
								} else {
									var highReason = $('#highReason').val();
									var eddId = datas.eddId;
									$('#eddId').val(eddId);
									$('#eddIdHide').val(eddId);
									if (getCustomerType() == '${typeIndi}') {
										var interfaceKey = getInterfaceKey();
										var tipeIndividuBo = '${tipeIndividuBoKey}';
										if('${tipeIndividuBoKey}' == interfaceKey) {
											$('#amlId').val(amlId);
											$('#apiStatus').val('${amlStatusDone}');
										} else {
											if ('${isBeneficiaryOwner}' == 'false') {
												$('#amlId').val(amlId);
												$('#apiStatus').val('${amlStatusDone}');
											} else {
												$('#apiStatus').val('${amlStatusWaitingBOStatus}');								
											}
										}
										var apiStatus = $('#apiStatus').val();
									} else {
										$('#amlId').val(amlId);
										$('#apiStatus').val('${amlStatusDone}');
									}
									messageAlertOk("Interface to AML success and Done for High Risk", "ui-icon ui-icon-notice", "Warning", closeDialog);

									if (!highReason) {
										messageAlertOk("High Reason is mandatory", "ui-icon ui-icon-notice", "Warning", closeDialog);
									}
								}

								loading.dialog('close');
							});
						}else{
							$().fetchSync("@{interfaceToAml()}", {
								'amlKey' : b.amlKey,
								'interfaceKey' : interfaceKey
							}, 
							function(datas) {
								prohibited = datas.prohibited.toLowerCase();
								var closeDialog = function(){
									$("#dialog-message").dialog("close");
								};
								loading.dialog('close');
								if(prohibited == 'true'){
									showBlackList('${blackListYes}');
								}else if (datas.resultCode && datas.resultCode == '0') {
									if (datas.riskProfile == 'high') {
										messageAlertOk("Interface to AML success with High Risk. Please choose watch list", "ui-icon ui-icon-notice", "Warning", closeDialog);
									} else {
										messageAlertOk("Interface to AML success and DONE", "ui-icon ui-icon-notice", "Warning", closeDialog);
									}
								} else {
									if (datas.riskProfile == 'high') {
										messageAlertOk("Interface to AML failed with High Risk. Please retry again (" + datas.resultMessage + ")", "ui-icon ui-icon-notice", "Warning", closeDialog);
									} else {
										messageAlertOk("Interface to AML failed (" + datas.resultMessage + ")", "ui-icon ui-icon-notice", "Warning", closeDialog);
									}
								}
							});
						}
					}
					loading.dialog('close');
				}	
			}
		});	
		
		app.reset.click(function() {
			location.href="@{list()}";	
			return false;
		});
		
		app.search.click(function() {
			search();
			return false;
		});
		
		var search = function() {
			var param = {
					"monitoringAmlKey" : $("#amlKey").val(),
					"monitoringAmlName" : $("#amlName").val(),
					"retailOf" : $('#custRetailOfKey').val(),
					"status" : $("#apiStatus").val()
				};
			
			$().fetchAsync("@{search()}", {'params':param}, function(datas) {
				const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric' };
				$("#checkAll").removeAttr('checked');
				
				var table = $("#tblAmlMonitoringTable").dataTable();
				table.fnClearTable();
				
				for (var x in datas) {
					var b = datas[x];
	
					var rownum; 
					if((b.statusCode ==  'AML_API_STATUS-DON') || (b.statusCode == 'AML_API_STATUS-WWL') 
					  || (b.statusCode == 'AML_API_STATUS-RPU') || (b.statusCode == 'AML_API_STATUS-WBL')) {
						$("#checkAll").attr("disabled", true);
						rownum = table.fnAddData(["<input type='checkbox' disabled/>", b.amlKey, b.customerName, b.birthDate, b.identificationNo, b.retailOf, b.riskProfile, b.status], false);
					}else{
						$("#checkAll").removeAttr("disabled");
						rownum = table.fnAddData(["<input type='checkbox'/>", b.amlKey, b.customerName, b.birthDate, b.identificationNo, b.retailOf, b.riskProfile, b.status], false);
					}
					
					var ptr = table.fnSettings().aoData[rownum].nTr;
					$(ptr).data("bean", b);
				}
				
				table.fnDraw();
				checkRedirect(datas);
			});
		};
		
		$("#checkAll", html).click(function(){
			var isChecked = $(this).is(':checked');
			$(dataTable.fnGetNodes()).each(function(i, e){
				var inputChk = $("input", $("td", $(this)).eq(0));
				if(inputChk.attr("disabled")){
					inputChk.removeAttr("checked"); 
				}else{
					inputChk.attr("checked", "checked");
				}
				
				if (!isChecked) {
					inputChk.removeAttr("checked"); 
				}
			});
		});
		
		function showBlackList(blackList){
			console.log("blackList: " + blackList);
			var closeDialog = function() {
				$("#dialog-message").dialog('close');
			}
			
			var isCustomerBO = $("#benefOwner").is(':checked');
			if (isCustomerBO) {
				messageAlertOk("This is DTTOT/Blacklist Benefical Owner", "ui-icon ui-icon-notice", "Warning", closeDialog);
			}else{
				messageAlertOk("This is DTTOT/Blacklist Customer", "ui-icon ui-icon-notice", "Warning", closeDialog);	
			}
			
			return false;
		}
		
		$("#tblAmlMonitoringTable").removeAttr('style');
		$("#tblAmlMonitoringTable tbody tr td").die('click');
		$("#tblAmlMonitoringTable tbody tr td").live('click', function(){
			var dataCustomer = String(app.tblAmlMonitoringTable.fnGetData(this.parentNode));
			var myArray = dataCustomer.split(",");
			if($(this).index() != 0){
				view(myArray[1]);
			}
		});
		
	}else{
		return new MonitoringAml(html);
	}
}
	