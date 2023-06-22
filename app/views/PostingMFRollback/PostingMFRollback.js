function PostingMFRollback(html) {
	if (this instanceof PostingMFRollback) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject(this, false);
		
		var DISPTACH_CONFIRM = "CONFIRM";
		var DISPTACH_ENTRY = "ENTRY";
		var DISPTACH_APPROVAL = "APPROVAL";
		
		var CAPTION_SAVE = "Save";
		var CAPTION_RESET = "Reset";
		
		var CAPTION_CONFIRM = "Confirm";
		var CAPTION_BACK = "Back";
		
		var CAPTION_APPROVE = "Approve";
		var CAPTION_REJECT = "Reject";
		var CAPTION_CANCEL = "Cancel";
		
		if ($.browser.msie){
			$("#rollbackRemarks[maxlength]").bind('input propertychange', function() {
		        var maxLength = $(this).attr('maxlength');  
		        if ($(this).val().length > maxLength) {  
		            $(this).val($(this).val().substring(0, maxLength));  
		        }  
		    });
		}
/*==================================================================
 * Function
 *==================================================================*/
		
		app.rollbackMFForm = $("#rollbackMFForm", html);
		
		function validate() {
			/*var conFundCode = */app.fundCodeErr.required(app.fundCode.isEmpty());
			/*var conRollbackDate = */app.rollbackDateErr.required(app.rollbackDate.isEmpty());
			
			return true;
			//return conFundCode && conRollbackDate;
		}
		
		function disabledButton() {
			app.save.add(app.reset)
			.add(app.confirm).add(app.back)
			.add(app.approve).add(app.reject).add(app.cancel)
			.button("option", "disabled", true);
		}
		
		function initilize() {
			app.buttonEntry.hide();
			app.buttonConfirm.hide();
			
			app.hiddenTag.keep(app.currentStatus).hide();
			app.hiddenTag.keep(app.rollbackStatus).hide();
			
			if (app.dispatch.val() == DISPTACH_ENTRY) {
				app.buttonEntry.show();
				if (app.fundCode.isEmpty()) {
					app.rollbackDate.attr("disabled", "disabled");
					app.rollbackRemarks.attr("disabled", "disabled");
				}
			}
			
			if (app.dispatch.val() == DISPTACH_CONFIRM) {
				app.buttonConfirm.show();
				
				app.fundCode.attr("disabled", "disabled");
				app.fundCodeHelp.attr("disabled", "disabled");
				
				app.rollbackDate.attr("disabled", "disabled");
				app.rollbackRemarks.attr("disabled", "disabled");

				// mekanisme mengatasi disabled data tidak terkirim
				app.hiddenTag.keep(app.rollbackDate).hide();
				app.hiddenTag.keep(app.rollbackRemarks).hide();
			}
			
			if (app.dispatch.val() == DISPTACH_APPROVAL) {
				app.fundCode.attr("disabled", "disabled");
				app.fundCodeHelp.attr("disabled", "disabled");
				
				app.rollbackDate.attr("disabled", "disabled");
				app.rollbackRemarks.attr("disabled", "disabled");

				// mekanisme mengatasi disabled data tidak terkirim
				app.hiddenTag.keep(app.rollbackDate).hide();
				app.hiddenTag.keep(app.rollbackRemarks).hide();
			}
		}
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/
		app.fundCode.popupFaFund("rollbackDate", function(){
			disabledButton();
			app.rollbackDate.val(null);
			app.rollbackMFForm.attr('action', 'fetch');
			app.rollbackMFForm.submit();
		});
		
/*================================================================== 
 * Event
 *================================================================== */
		initilize();
		
		app.rollbackDate.change(function(){
			if (!app.rollbackDate.isEmpty()) {
				disabledButton();
				app.rollbackMFForm.attr('action', 'fetch');
				app.rollbackMFForm.submit();
			}
		});
		
		app.save.click(function(){
			if (validate()) {
				disabledButton();
				app.rollbackMFForm.attr('action', 'next');
				app.rollbackMFForm.submit();
			}
		});
		
		app.reset.click(function(){
			disabledButton();
			app.rollbackMFForm.attr('action', 'list');
			app.rollbackMFForm.submit();
		});
		
		app.confirm.click(function(){
			disabledButton();
			app.rollbackMFForm.attr('action', 'confirm');
			app.rollbackMFForm.submit();	
		});
		
		app.back.click(function(){
			disabledButton();
			app.rollbackMFForm.attr('action', 'fetch');
			app.rollbackMFForm.submit();	
		});
		
		var backToList = function() {
			loading.dialog('open');
			location = "@{list()}";
		}
		if (app.dispatch.val() == DISPTACH_CONFIRM && !app.rollbackKey.isEmpty()) {
			/*html.simpleDialog("Information", "Succcess Saving Posting MF Rollback with id "+app.rollbackKey.val(), function(){
				app.rollbackMFForm.attr('action', 'list');
				app.rollbackMFForm.submit();
			}).dialog('open');*/
			messageAlertOk("Your Rollback Transaction No is "+app.rollbackKey.val(), "ui-icon ui-icon-circle-check", "Notification", backToList);
		}
	}else{
		return new PostingMFRollback(html);
	}
}