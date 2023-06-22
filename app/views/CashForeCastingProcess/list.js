function list(html) {
	if (this instanceof list) {
		var app = html.inject(this, false);
		app.fromDate.val(app.appDate.val());
		app.fromDate.blur();
		app.fromDate.attr("disabled", "disabled");
		
		$("#idCust").hide();
		
		var closeDialogMessage = function() {
			$("#dialog-message").dialog('close');
		};
		
		app.btnReset.click(function(){
			clear();
		});
		
		function clear(){
			app.toDate.val("");
			$("#toDateErr").html("");
			$('#toDate').removeClass('fieldError');
		};
		
		function validateform(){
			$("#toDateErr").html("");
			$('#toDate').removeClass('fieldError');
			goProcess = true;
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
				"customerKey" : 'ALL',
				"fromDate" : app.fromDate.val(),
				"toDate" : app.toDate.val()
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