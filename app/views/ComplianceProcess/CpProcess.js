function CpProcess(html) {
	if (this instanceof CpProcess) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var process = html.inject(this);

/*================================================================== 
 * Function
 *================================================================== */
	    function checkFundDate(fundKey, yyyyMMdd) {
	    	var returnData;
	    	$.ajaxSetup({ async : false });
			$.get("@{ComplianceProcess.check()}", {'fundKey':fundKey, 'yyyyMMdd':yyyyMMdd}, function(data) {
				checkRedirect(data);
				returnData = data;
				if (typeof func == 'function') {
					func(data);
				}
			});
			return returnData;
	    };

/*================================================================== 
 * Event
 *================================================================== */
		process.fundCode.popupFaFundWithProfile("date", function(data){
			process.fundCode.removeClass('fieldError');
		}, function() {
			process.fundCode.addClass('fieldError');
			process.fundCodeDesc.val('NOT FOUND');
			process.fundCodeKey.val('');
			process.fundCode.val('');
			process.h_fundCodeDesc.val('');
		});
		
		process.btnReset.click(function(){
			process.processForm.attr('action', '@{list()}');
			process.processForm.submit();
		});
		
		process.btnProcess.click(function(){
			var date = process.date.val().fmtYYYYMMDD('/');
			var result = checkFundDate(process.fundCodeKey.val(), date);
			if (result == 1) {
				var answer = confirm('This fund already process, do you want to re-process?');
				 if (answer) {
					process.processForm.attr('action', '@{process()}');
					process.processForm.submit();
				 }
			}else{
				process.processForm.attr('action', '@{process()}');
				process.processForm.submit();
			}
		});
		
		html.clazz('calendar').datepicker();
	}else{
		return new CpProcess(html);
	}
} 