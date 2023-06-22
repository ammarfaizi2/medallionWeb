function CustodyEndOfDayReProcess(html) {
	if (this instanceof CustodyEndOfDayReProcess) {
		
/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var ceod = html.inject(this);
		var businessDate = ceod.fromDate.val();
		var backDatedDate = null;
		var varLastEodDate = null;
		var isBackDate = "0";
		
		var closeDialogMessage = function() {
			$("#dialog-message").dialog('close');
		};
		
		function validation() {
			if (ceod.custInvt.isEmpty()) {
				messageAlertOk("Account No must not empty", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
				ceod.custInvt.focus();
				return false;
			}

			if (ceod.fromDate.isEmpty() || ceod.toDate.isEmpty()) {
				messageAlertOk("Periode must not empty", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
				if (ceod.fromDate.isEmpty()) ceod.fromDate.focus();
				if (ceod.toDate.isEmpty()) ceod.toDate.focus();
				return false;
			}
			
			var fromDate = $('#fromDate').datepicker('getDate');
			var toDate = $('#toDate').datepicker('getDate');
			if (fromDate > toDate) {
				messageAlertOk("Invalid range of periode", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
				ceod.fromDate.focus();
				return false;
			}
			
			var appDate = new Date(html.getApplicationDate());
			var toDateFormatDefault =  $.datepicker.formatDate( '${appProp.jqueryDateFormat}', toDate );
			if (toDate > appDate) {
				messageAlertOk("Posting on "+ toDateFormatDefault+" failed. "+toDateFormatDefault+" is greater than application date", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
				ceod.toDate.focus();
				return false;
			}
			
			if (fromDate > varLastEodDate) {
				messageAlertOk("From date can not greater than last eod date["+varLastEodDate.fmtDateDMY("/")+"]", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
				ceod.fromDate.focus();
				return false;
			}
			
			return true;
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		function setDate(){
			mapGetBackDatedDate = html.fetchSync("@{getBackDatedDate()}", {'accountNo':ceod.custInvt.val()});
			backDatedDate = new Date(new Number(mapGetBackDatedDate.valBackDate));
			fromDate = new Date(new Number(mapGetBackDatedDate.fromDate));
			var currTmpDatepicker = ceod.fromDate.datepicker();
			ceod.fromDate.datepicker("setDate", fromDate);
			isBackDate = mapGetBackDatedDate.isBackDate;
			var lastEodDate = html.fetchSync("@{getLastEODDate()}", {'accountNo':ceod.custInvt.val()});
			if (lastEodDate != null) {
				var toDate = new Date(new Number(lastEodDate));
				varLastEodDate = new Date(new Number(lastEodDate));
				var currTmpDatepicker = ceod.toDate.datepicker();
				ceod.toDate.datepicker("setDate", toDate);
			}
			/*var fromDateCheck = $('#fromDate').datepicker('getDate');
			var toDateCheck = $('#toDate').datepicker('getDate');
			if(fromDateCheck > toDateCheck){
				ceod.toDate.datepicker("setDate", fromDate);
			}*/
		}
			
		ceod.custInvt.dynapopup('PICK_CS_ACCOUNT_NOT_IN_FA', '', "fromDate", function(data){
			setDate();
		});
		
		ceod.btnProcess.click(function(){
			if (validation()) {
				var fromDate = $('#fromDate').datepicker('getDate');
				fromDate.setHours(0, 0, 0, 0);

				if (isBackDate == "1" && backDatedDate < fromDate) {
					var answer = confirm("There is backdated transaction on "+backDatedDate.fmtDateDMY("/")+", do you want to continue ? ");
					if (answer) {
						ceod.fromDate.val(backDatedDate.fmtDateDMY("/"));
						ceod.custodyEndOfDayReProcessForm.attr('action', 'process');
						ceod.custodyEndOfDayReProcessForm.submit();
					//}else{
						//ceod.fromDate.val(backDatedDate.fmtDate("/"));
					}
				}else{
					loading.dialog('open');
					ceod.custodyEndOfDayReProcessForm.attr('action', 'process');
					ceod.custodyEndOfDayReProcessForm.submit();
				}
			}
		});
		
		ceod.btnReset.click(function(){
			ceod.custInvt.val("");
			ceod.custInvtKey.val("");
			ceod.custInvtDesc.val("");
			
			ceod.fromDate.val(businessDate);
			ceod.toDate.val(businessDate);
		});
		if(ceod.custInvt.val()!=""){
			setDate();
		}
	}else{
		return new CustodyEndOfDayReProcess(html);
	}
}	
