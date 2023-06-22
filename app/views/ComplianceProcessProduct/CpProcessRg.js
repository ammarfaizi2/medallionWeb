function CpProcessRg(html) {
	if (this instanceof CpProcessRg) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject(this);

/*================================================================== 
 * Function
 *================================================================== */
		function prosesComp(){
			app.processForm.attr('action', '@{ComplianceProcessRg.process()}');
			app.processForm.submit();
		}
/*================================================================== 
 * Event
 *================================================================== */
		
		app.productCode.popupProduct("date");
		
		app.reset.click(function(){
			app.processForm.attr('action', '@{list()}');
			app.processForm.submit();
		});
		
		app.process.click(function(){
			var conProductCode = app.productCodeErr.required(app.productCode.isEmpty());
			var conDate = app.dateErr.required(app.processDate.isEmpty());
			
			var valid = (conProductCode && conDate);
			if (valid) {
				try{
		    		var data = html.fetch("@{ComplianceProcessRg.validate()}", {"productCode":app.productCode.val(), "date":app.processDate.val()});
		    		if (data == null) {
		    			app.dateErr.html("Product code "+app.productCode.val()+" is not registered");
		    			return false;
		    		}else{
		    			var proses = true;
		    			app.globalErr.html("");
			    		if (data.validateDate) { app.dateErr.html(data.validateDate); app.dateErr.show(); proses = false;}
			    		if (data.validateEod) { app.globalErr.html(app.globalErr.html()+"<p>"+data.validateEod+"<p>"); proses = false;}
			    		if (data.validateNav) { app.globalErr.html(app.globalErr.html()+"<p>"+data.validateNav+"<p>"); proses = false;}
			    		if (data.validateReprocess) {
			    			var succedDialog2 = function() {
			    				prosesComp();
			    				return false;
			    			}
			    			
			    			var closeDialog2 = function() {
			    				$("#dialog-message").dialog('close');
			    				return false;
			    			}
			    			
			    			messageAlertYesNo(data.validateReprocess, "ui-icon ui-icon-notice", "Confirmation Message", succedDialog2, closeDialog2);
			    			proses = false;
			    		} 
			    		
			    		if(proses){
			    			prosesComp();
			    		}
		    		}
		    	}catch(e) { alert(e); }
			}
			/*if (validate()) {
				app.processForm.attr('action', '@{ComplianceProcessRg.process()}');
				app.processForm.submit();
			}*/
		});
		
		html.clazz('calendar').datepicker();
	}else{
		return new CpProcessRg(html);
	}
}