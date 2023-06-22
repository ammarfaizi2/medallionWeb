function PostingMF(html) {
	if (this instanceof PostingMF) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject(this, false);
		
/*==================================================================
 * Function
 *==================================================================*/

		function validate() {
			var conFundCode = app.fundCodeErr.required(app.fundCode.isEmpty());
			var conFromDate = app.fromDateErr.required(app.fromDate.isEmpty());
			var conToDate = app.toDateErr.required(app.toDate.isEmpty());
			return conFundCode && conFromDate && conToDate;
		}
		
		function disabledButton(condition) {
			app.btnProcess.button("option", "disabled", condition);
			app.btnProcessAjax.button("option", "disabled", condition);
			app.btnReset.button("option", "disabled", condition);
			if (condition) {
				app.fundCode.attr("disabled", "disabled");
				app.fundCodeHelp.attr("disabled", "disabled");
				app.fromDate.attr("disabled", "disabled");
				app.toDate.attr("disabled", "disabled");	
			}else{
				app.fundCode.removeAttr("disabled");
				app.fundCodeHelp.removeAttr("disabled");
				app.fromDate.removeAttr("disabled");
				app.toDate.removeAttr("disabled");
			}			
		}
		
		function fetchLog() {
			var param = {
				"fundCode" : app.fundCode.val(),
				"sessionTag" : app.sessionTag.val()
			}
			
			$().fetchAsync("@{processAjaxLog()}", {"param":param}, function(data){
				if (data.status == 'G') {
					//do nothing, stop scheduler;
					loading.dialog('close');
					disabledButton(false);
				}else if (data.status == 'W') {
					setTimeout(fetchLog, 3000);
				}else if (data.status == 'F') {
					var nice = "";
					for (var x in data.logs) {						
						var content = data.logs[x];
						if (nice == '') { nice = content;
						}else{ nice = nice + '\n'+content; }
					}
					app.log.val(nice);
					loading.dialog('close');
					disabledButton(false);
					//do nothing, stop scheduler;
				}
			});
		}
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/

/*================================================================== 
 * Event
 *================================================================== */
		$('.calendar').datepicker();
		
		app.fundCode.popupFaFund("fromDate", function(){
			app.log.html('');
			if (app.fundCodeKey.val() != '' && app.fundCodeKey.val() != null){
				$.get("@{getPostingMFLog()}", {'fundKey':app.fundCodeKey.val()}, function(datas) {
					checkRedirect(datas);
					if(datas != null){
						if(datas.lastPosted != null){
							app.fromDate.datepicker('setDate', new Date(datas.lastPosted));
						}
					} else {
						app.fromDate.val(app.appDate.val());
					}
				});
			} else {
				app.fromDate.val(app.appDate.val());
			}
		});
		
		app.btnProcess.click(function(){
			if (validate()) {
				disabledButton(true);
				
				app.fundCode.removeAttr("disabled");
				app.fundCodeHelp.removeAttr("disabled");
				app.fromDate.removeAttr("disabled");
				app.toDate.removeAttr("disabled");
				
				app.postingMFForm.attr('action', 'process');
				app.postingMFForm.submit();				
			}
		});
		
		app.btnProcessAjax.click(function(){
			if (validate()) {
				loading.dialog('open');
				disabledButton(true);
				
				app.fundCodeErr.html('');
				app.fromDateErr.html('');
				app.toDateErr.html('');
				app.log.html('');
				
				var param = {
					"fundCode" : app.fundCode.val(),
					"fundCodeKey" : app.fundCodeKey.val(),
					"fundCodeDesc" : app.fundCodeDesc.val(),
					"fromDateStr" : app.fromDate.val(),
					"toDateStr" : app.toDate.val(),
					"sessionTag" : app.sessionTag.val(),
					"processMark" : app.processMark.val()
				}
					
				$().fetchAsync("@{processAjax()}", {"param":param}, function(data){
					if (data.errorSize > 0) {
						for (var x in data.validations) {
							$("#"+x).html(data.validations[x]).show();
						}
						loading.dialog('close');
						disabledButton(false);
					}
				});
				setTimeout(fetchLog, 5000);
			}
		});
		
		app.btnReset.click(function(){
			app.fundCodeErr.required(false);
			app.fromDateErr.required(false);
			app.toDateErr.required(false);
			
			app.fundCode.removeClass("fieldError");
			app.fromDate.removeClass("fieldError");
			app.toDate.removeClass("fieldError");
			
			app.fundCode.val("");
			app.fundCodeKey.val("");
			app.fundCodeDesc.val("");
			app.fromDate.val(app.appDate.val());
			app.toDate.val(app.appDate.val());
			app.log.html("");
			
			app.fundCode.focus();
		});
		
		app.btnProcess.hide();
	
	}else{
		return new PostingMF(html);
	}
}