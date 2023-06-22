$(function(){
	$('#cancel').button();
	$('#settle').button();
	$('#confirm').button();
	$('#back').button();
	var closeDialogMessage = function() {
		$("#dialog-message").dialog("close");
	};
	
	var backToList = function() {
		loading.dialog('open');
		location.href = "@{list()}";
	};
	
	var nextProcess = function(){
		loading.dialog('open');
		
		// elvino aku ginini soalnya kalo tidak data gax kebawa dropdownlist aga aneh nih
		$("#transferMethod").removeAttr("disabled");
		var serialize = $('#CorporateSettlementForm').serialize();
		$("#transferMethod").attr("disabled", "disabled");
		
		$.ajaxSetup({ async : true });
		$.post("@{confirm()}", serialize, function(result, status, xhr) {

    		checkRedirect(result);
			if (result.status == 'success') {
				loading.dialog('close');
				messageAlertOk(result.message, "ui-icon ui-icon-circle-check", "Notification", backToList);
			} else {
				loading.dialog('close');
				messageAlertOk(result.error, "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
			}
		});
	}; 
	$('#cancel').click(function() {
		location.href="@{list()}";		
	});
	
	$('#back').click(function(){
		location.href='@{back()}?id='+$('#corporateAnnouncementKey').val()+'&mode=${mode}';
	});
	
	function validate() {
		$("#transferMethodError").html('');
		
		if ($("#transferMethod").val() == '') {
			$("#transferMethodError").html('required');
			return false;
		}
		return true;
	}
	
	$('#settle').click(function(){
		if (validate()) {
			loading.dialog('open');
			var action = "@{settle}";
			$('#CorporateSettlementForm').attr('action', action);
			$('#CorporateSettlementForm').submit();
		}
	});
	
	$('#confirm').click(function(){
		
		var distributionDate = new Date($('#distributionDate').datepicker('getDate')).getTime();
		var currentDate = new Date($('#currentDate').datepicker('getDate')).getTime();
		var sizeOfTransactions = '${transLists.size()}';
		console.log("Distribution Date = " +distributionDate);
		console.log("currentDate = " +currentDate);
		
		if (sizeOfTransactions < 1) {
			messageAlertOk("Must have minimal 1 row entitlement on grid", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
			return false;
		}else if (currentDate < distributionDate){
			messageAlertOk("Distribution date cannot greater than application date", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
			return false;
		} else if (distributionDate < currentDate){
			messageAlertYesNo("Do you want to continue this settlement on <"+$('#distributionDate').val()+">?", "ui-icon ui-icon-notice", "Confirmation Message",nextProcess, closeDialogMessage);
		} else {
			nextProcess();
		}
	});
	
	if ("${editTransferMethod}" == 'YES') {
		$("#transferMethod").removeAttr("disabled");
	}else{
		$("#transferMethod").attr("disabled", "disabled");
	}
});


