function FaEndOfDay(html) {
	if (this instanceof FaEndOfDay) {
		
/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var feod = html.inject(this);
		var businessDate = feod.fromDate.val();
		var closeDialog = function() {
			$("#dialog-message").dialog('close');
		}
		
		function validation() {
			if (feod.fundCode.isEmpty()) {
//				alert("Fund Code No must not empty");
				messageAlertOk("Fund Code must not empty", "ui-icon1 ui-icon-alert", "Warning Message", closeDialog);
				feod.fundCode.focus();
				return false;
			}

			if (feod.fromDate.isEmpty() || feod.toDate.isEmpty()) {
//				alert("Periode must not empty");
				messageAlertOk("Periode must not empty", "ui-icon1 ui-icon-alert", "Warning Message", closeDialog);
				if (feod.fromDate.isEmpty()) feod.fromDate.focus();
				if (feod.toDate.isEmpty()) feod.toDate.focus();
				return false;
			}
			
//			if (feod.fromDate.val() > feod.toDate.val()) {
//				alert("Period To is before Period From");
//				feod.fromDate.focus();
//			}

			return true;
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		html.clazz('calendar').datepicker();
		
		feod.fundCode.popupFaFund("fromDate", function(data){
			feod.fundCodeKey.val(data.code);
			var maxNavDate = html.fetchSync("@{getMaxNavDate()}", {'fundKey':data.code});
				maxNavDate = maxNavDate+"";
			feod.fromDate.value(maxNavDate.toMMDDYYYY("/"));
			feod.toDate.value(maxNavDate.toMMDDYYYY("/"));
		});
		
		feod.btnProcess.click(function(){
			if (validation()) {
				feod.faEndOfDayForm.attr('action', 'process');
				feod.faEndOfDayForm.submit();
			}
		});
		
		feod.btnReset.click(function(){
			feod.fundCode.val("");
			feod.fundCodeKey.val("");
			feod.fundCodeDesc.val("");
			feod.fromDate.val("");
			feod.toDate.val("");
			feod.log.val("");
			feod.confirm.val("no");
			feod.confirmFaDailyNav.val("");
			feod.confirmPendingTrans.val("");
			
//			feod.fromDate.val(businessDate);
//			feod.toDate.val(businessDate);
		});
		
		var confim = feod.confirm.val();
		var confirmFaDailyNav = feod.confirmFaDailyNav.val();
		var confirmPendingTrans = feod.confirmPendingTrans.val();
		
		if (confim == 'ask') {
			var answer1 = true;
			if (!confirmFaDailyNav.isEmpty()) { answer1 = confirm(confirmFaDailyNav); }
			
			var answer2 = true;
			if (!confirmPendingTrans.isEmpty() && answer1) { answer2 = confirm(confirmPendingTrans); }
			
			if (answer1 && answer2) {
				feod.confirm.val("yes");
				feod.btnProcess.click();
			}
		}
		
	}else{
		return new FaEndOfDay(html);
	}
}	