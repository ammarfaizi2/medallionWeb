function BpjsMonthlyProcess(html) {
	if (this instanceof BpjsMonthlyProcess) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var components = ["bpjsMonthlyProcessForm"
		                  , "btnProcess"
		                  , "btnReset"
		                  , "month"
		                  , "year"
		                  , "monthErr"
		                  , "yearErr"
		                  , "filterAll1"
		                  , "filterAll2"
		                  , "filterAll3"
		                  , "filterSpecific2"
		                  , "all1"
		                  , "all2"
		                  , "all3"
		                  , "specific1"
		                  , "obligasi"
		                  , "saham"
		                  , "specific3"
		                  , "classification1"
		                  , "classification3"
		                  , "classification1Err"
		                  , "classification3Err"];
		var bpjs = html.injectComp(this, false, components);

/*==================================================================
 * Function
 *==================================================================*/

		function validate() {
			var conValidMonth = true;
			var conMonth = bpjs.monthErr.required(bpjs.month.isEmpty());
			if(conMonth) conValidMonth = bpjs.monthErr.valid(bpjs.month.isMonth(), "Invalid month");

			var conValidYear = true;
			var conYear = bpjs.yearErr.required(bpjs.year.isEmpty());
			if(conYear) conValidYear = bpjs.yearErr.valid(bpjs.year.isYear(), "Invalid year");

			var specific1 = true;
			if(bpjs.specific1.is(':checked')) specific1 = bpjs.classification1Err.required(bpjs.classification1.isEmpty());

			var specific3 = true;
			if(bpjs.specific3.is(':checked')) specific3 = bpjs.classification3Err.required(bpjs.classification3.isEmpty());

			return conMonth && conYear && conValidMonth && conValidYear && specific1 && specific3;
		}

/*================================================================== 
 * Event
 *================================================================== */

		bpjs.all1.click(function(e){
			bpjs.filterAll1.val("ALL");
			bpjs.specific1.removeAttr("checked");
			bpjs.classification1.val("");
			bpjs.classification1.attr("disabled", true);
		});

		bpjs.all2.click(function(e){
			bpjs.filterAll2.val("ALL");
			bpjs.obligasi.removeAttr("checked");
			bpjs.saham.removeAttr("checked");
			bpjs.filterSpecific2.val("");
		});

		bpjs.all3.click(function(e){
			bpjs.filterAll3.val("ALL");
			bpjs.specific3.removeAttr("checked");
			bpjs.classification3.val("");
			bpjs.classification3.attr("disabled", true);
		});

		bpjs.specific1.click(function(e){
			bpjs.classification1.attr("disabled", false);
			bpjs.all1.removeAttr("checked");
			bpjs.filterAll1.val("");
		});

		bpjs.obligasi.click(function(e){
			bpjs.filterSpecific2.val("${obligasi}");
			bpjs.all2.removeAttr("checked");
			bpjs.filterAll2.val("");
			bpjs.saham.removeAttr("checked");
		});

		bpjs.saham.click(function(e){
			bpjs.filterSpecific2.val("${saham}");
			bpjs.all2.removeAttr("checked");
			bpjs.filterAll2.val("");
			bpjs.obligasi.removeAttr("checked");
		});

		bpjs.specific3.click(function(e){
			bpjs.classification3.attr("disabled", false);
			bpjs.all3.removeAttr("checked");
			bpjs.filterAll3.val("");
		});

		if(bpjs.filterAll1.val() == 'ALL') { bpjs.all1.click(); }
		if(bpjs.filterAll2.val() == 'ALL') { bpjs.all2.click(); }
		if(bpjs.filterAll3.val() == 'ALL') { bpjs.all3.click(); }

		bpjs.btnProcess.click(function(){
			var isValid = validate();
			console.debug("btnProcess isValid => " + isValid);
			if(isValid) {
				messageAlertYesNo("Are you sure want to process?", "ui-icon ui-icon-notice", "Confirmation Message", yesConfirm, closeDialog);
				return false;
			}
		});

		var yesConfirm = function() {
			$("#dialog-message").dialog("close");
			var param = {
	    		"filterAll1" : bpjs.filterAll1.val()
	    		, "filterAll2" : bpjs.filterAll2.val()
	    		, "filterAll3" : bpjs.filterAll3.val()
	    		, "filterAll3" : bpjs.filterAll3.val()
	    		, "filterSpecific1" : bpjs.classification1.val()
	    		, "filterSpecific2" : bpjs.filterSpecific2.val()
	    		, "filterSpecific3" : bpjs.classification3.val()
	    		, "month" : bpjs.month.val()
	    		, "year" : bpjs.year.val()
	    	};

			console.debug(param);
			$.post("@{BpjsMonthlyProcess.process()}", {"param":param}, function(data) {
				console.debug(data);
				if (data.messageError != null) {
					messageAlertOk(data.messageError, "ui-icon1 ui-icon-circle-close", "Error Message", closeDialog);
					return false;
				} else {
					messageAlertOk(data.messageSuccess, "ui-icon1 ui-icon-circle-check", "Success Message", closeDialog);
				}
				//checkRedirect(data);
			});
			return true;
		};

		var closeDialog = function() {
			$("#dialog-message").dialog("close");
		};

		bpjs.btnReset.click(function(){
			console.debug("btnReset");
			bpjs.monthErr.required(false);
			bpjs.yearErr.required(false);
			bpjs.classification1Err.html("");
			bpjs.classification3Err.html("");

			bpjs.all1.click();
			bpjs.all2.click();
			bpjs.all3.click();
			bpjs.month.val("");
			bpjs.year.val("");
		});
	} else {
		return new BpjsMonthlyProcess(html);
	}
}