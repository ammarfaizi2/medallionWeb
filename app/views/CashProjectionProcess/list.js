function list(html) {
	if (this instanceof list) {
		var app = html.inject(this, false);
		app.fromDate.val(app.appDate.val());
		app.fromDate.blur();
		app.fromDate.attr("disabled", "disabled");
		app.customer.popupCustomerCashProjection("", function(){
				console.log(1)
		});
		
		var closeDialogMessage = function() {
			$("#dialog-message").dialog('close');
		};
		
		app.btnReset.click(function(){
			clear();
		});
		
		function clear(){
			app.customer.val("");
			app.customerKey.val("");
			app.customerDesc.val("");
			app.toDate.val("");
			$("#toDateErr").html("");
			$("#customerErr").html("");
			$('#toDate').removeClass('fieldError');
			
			if("${sentMailCashProjection}" =="1"){
				$("#sentmail1").attr("checked", true);
				$("#sentmail2").attr("checked", false);
			}else{
				$("#sentmail1").attr("checked", false);
				$("#sentmail2").attr("checked", true);
			}
		};

		clear();
		function validateform(){
			$("#toDateErr").html("");
			$("#customerErr").html("");
			$('#toDate').removeClass('fieldError');
			goProcess = true;
			if(app.customer.val()==''){
				$("#customerErr").html("Required");
				goProcess = false;
			}
			if(app.toDate.val()==''){
				$("#toDateErr").html("Required");
				goProcess = false;
			}
			if(app.toDate.val()!=''){
				fromDate = app.fromDate.val();
				strFrom = fromDate.split("/");
				toDate = app.toDate.val();
				strTo = toDate.split("/");
				if(Number(strFrom[2]+""+strFrom[1]+""+strFrom[0]) > Number(strTo[2]+""+strTo[1]+""+strTo[0])){
					goProcess = false;
					$("#toDateErr").html("Period To must be greater or equal than period from");
				}
			}
			return goProcess;
		}
		
		app.btnProcess.click(function(){
			var goProcess = validateform();
			if(!goProcess) return;
			var param = {
				"sessionTag" : $("input[name='param.sessionTag']", html).val(),
				"customerKey" : app.customerKey.val(),
				"fromDate" : app.fromDate.val(),
				"toDate" : app.toDate.val(),
				"sentMail" : app.sentmail1.is(':checked')
			};
			
			loading.dialog('open');
			$().fetchAsync("@{processAjaxLog()}", {"param":param}, function(data){
				loading.dialog('close');
				if(data.success == "1"){
					messageAlertOk(data.message, "ui-icon ui-icon-circle-check", "Process Message", closeDialogMessage);
					clear();
				}else{
					messageAlertOk(data.message, "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
				}
			});
		});
	}else{
		return new list(html);
	}
}