function RunUpload(html){
	if( this instanceof RunUpload ){
		
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		var table = null;
		var parent = this;
		var STEP_POPULATE = "POPULATE";
		var STEP_PROCESS = "PROCESS";
		var step = "";
		var oldProcessId = "";
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.batchNo = app.batchNo.val();
			p.totalSuccess = app.totalValidatePassed.val();
			p.totalFail = app.totalValidateError.val();
			return p;
		};

		
/* =========================================================================== 
 * Function
 * ========================================================================= */
		
		function buttonDisabled(condition) {
			app.btnPopulate.button("option", "disabled", condition);
			app.btnReset.button("option", "disabled", condition);
			app.linkSaveValidationError.button("option", "disabled", condition);
			app.linkSaveProcessError.button("option", "disabled", condition);
		}
		
		function constractTable(data) {
			if (table != null) {
				// PENTING penyebab dari script take tolong adalah fnDestory ini processnya lama bgt
				// ada yang bilang solusinya delete langsung aja gax usah pake fnDestory
				// aku coba remove dulu datanya lalu fnDestory blm ketaun bisa atau tidak
				$("tbody", app.tablePopulate).remove();
				table.fnDestroy();
				table.hide();
			}
			app.batchNo.val(data["BATCHID"]);
			app.linkSaveValidationError.attr("href", "/generalupload/saveValidationError?param.batchNo="+data["BATCHID"]+"&param.output=csv");
			
			var trHeader = $("thead tr", app.tablePopulate).html("");

			// variable headers = data row, variable sheaders = headernya 
			var headers = data["TARGET"];
//			var headers = data["SOURCE"];
			var sheaders = data["SOURCE"];
			var types = data["TYPE"];
			for (x in headers) {
				var align = "left";
				if (types[x].toLowerCase() == "number") { align = "right"; }
				if (types[x].toLowerCase() == "date") { align = "center"; }
					
				var width = 0;
				if (x == 0) width = 50; 
				if (x == 1) width = 200;
				headers[x] = headers[x].replace("\'","&quot;");
				trHeader.append("<th width='"+width+"px' field='"+headers[x]+"|string|none|"+align+"|nosort'>"+sheaders[x]+"</th>");
			}
			
			var options = {
				showLoading : "false"
			};
			
			table = app.tablePopulate.paging("@{GeneralUpload.previewPaging()}", parent, function(p){
				if (step == STEP_POPULATE) {
					app.totalValidateRow.val(p.iTotalSuccess + p.iTotalFail);
					app.totalValidatePassed.val(p.iTotalSuccess);
					app.totalValidateError.val(p.iTotalFail);
					if (p.iTotalFail > 0) { 
						app.linkSaveValidationError.show();
//						app.btnProcess.button("option", "disabled", true);
					}
					if (p.iTotalFail == 0) {
						app.linkSaveValidationError.hide();
						app.btnProcess.button("option", "disabled", false); 
					}
					
					if (p.iTotalSuccess > 0){
						app.btnProcess.button("option", "disabled", false);
					}
					
					if (p.iTotalSuccess == 0){
						app.btnProcess.button("option", "disabled", true);
					}
				}
				
				if (step == STEP_PROCESS) {
					
				}
			}, options);
			table.show();
		}
		
		function validate() {
			var conUpdProfile = app.updProfileError.required(app.updProfile.isEmpty());
			var conFile = app.fileError.required(app.file.isEmpty());
			var conCorrectFiltetype = true;
			if (conUpdProfile && conFile) {
				var filenames = app.file.val();
					filenames = filenames.split("\\");
					filenames = filenames[filenames.length-1];
				var fileparts = filenames.split(".");
				var prefix = app.fileprefix.val();
				var errmsg = "";
				
				if (app.filetype.val().toUpperCase() != fileparts[fileparts.length-1].toUpperCase() ) {
					conCorrectFiltetype = false;
					errmsg = "Invalid file type";
					app.fileError.html(errmsg);
					app.fileError.show();
				}
				
				if (!prefix.isEmpty() && filenames.toUpperCase().indexOf(prefix.toUpperCase()) != 0) {
					conCorrectFiltetype = false;
					if (errmsg != "") errmsg += ", ";
					errmsg += "Invalid file prefix";
					app.fileError.html(errmsg);
					app.fileError.show();
				}
			}
			
			return conUpdProfile && conFile && conCorrectFiltetype;
		}
		
		function resetGUI() {
			if (table != null) {
				$("tbody", app.tablePopulate).remove();
				table.fnDestroy();
				table.hide();
				table = null;
			}

			app.totalValidateRow.val("0");
			app.totalProcessSuccess.val("0");
			app.totalValidatePassed.val("0");
			app.linkSaveValidationError.hide();
			
			app.accordionResult.hide();
			app.totalProcessSuccess.val("0");
			app.totalProcessFail.val("0");
			app.linkSaveProcessError.hide();
		}
		
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.file.add(app.btnPopulate).add(app.btnReset).add(app.btnProcess).add(app.linkSaveValidationError).add(app.linkSaveProcessError).button();
		
		app.btnProcess.button("option", "disabled", true);
		
		app.accordionValidation.add(app.accordionResult).accordion({collapsible: false});
		
		app.updProfile.dynapopup('PICK_UD_PROFILE_ROLE', 'Upload|{userkey}', 'includeHeaderRow', 
			function(data){
				app.filetype.val(data.filetype);
				app.fileprefix.val(data.fileprefix);
				resetGUI();
			},
			function(){ 
//				messageAlertOk("Fail to fetch data", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
			}
		);
		
		// ini untuk mengakali kalo click populate buka lagi popup gax bisa di pilih profilenya
		// di panggil setelahs selesai populate
		function refreshpopup() {
			app.updProfileHelp.click();
			$('#lookuppaging').dialog('close');
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		var globalProgress = null;		
		function processPopulateFinish(data) {
			step = STEP_POPULATE;
	    	
	    	if (data.ERROR) {
	    		messageAlertOk(data.ERROR, "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
	    	}else{
	    		constractTable(data);
	    	}
			
	    	buttonDisabled(false);
	    	globalProgress.dialog('close');		
//	    	refreshpopup();
		}
		
		function processBatchFinish(data) {
			if (data.status == 'SUCCESS') {
				step = STEP_PROCESS;
//				app.accordionValidation.hide();
				app.accordionResult.show().accordion("destroy").accordion({collapsible: true});
				app.totalProcessSuccess.val(data.monitor.successRow);
				app.totalProcessFail.val(data.monitor.failRow);
				app.btnProcess.button("option", "disabled", true);
				
				table.fnPageChange("first");
				
				if (data.monitor.failRow > 0) {
//					app.linkSaveProcessError.attr("href", "/generalupload/saveValidationError?param.batchNo="+data.monitor.batchId+"&param.output=csv");
					app.linkSaveProcessError.attr("href", "/generalupload/linkSaveProcessError?param.batchNo="+data.monitor.batchId+"&param.output=csv");
					app.linkSaveProcessError.show();
				}else{
					app.linkSaveProcessError.hide();
				}
				buttonDisabled(false);
				globalProgress.dialog('close');
			}
			if (data.status == 'FAIL') {
				messageAlertOk("Fail to process data", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
				buttonDisabled(false);
				globalProgress.dialog('close');
			}
		}
		
		app.btnPopulate.click(function(){
			app.accordionResult.hide();
			if (validate()){
				globalProgress = html.progressDialog().dialog('open');
				buttonDisabled(true);
				
//				$.ajaxSetup({ async : true });
				app.updProcessId.val($().getRandomChar(5)+""+(new Date()).getTime());
				oldProcessId = app.updProcessId.val();
				pingProgressPopulate(); // do ping
				app.searchForm.ajaxForm({
					dataType: "json",
				    contentType: "application/x-www-form-urlencoded;charset=utf-8",
				    url: "@{GeneralUpload.preProcessRunUpload()}",
				    success: function(data, status, jxhr, form) {
				    	if (data.RESULT == 'SUCCESS') {
					    	html.fetchAsyncTimeout("@{GeneralUpload.processRunUpload()}", {"processId" : app.updProcessId.val()});				    		
				    	}
				    	if (data.RESULT == 'FAIL') {
				    		messageAlertOk("Fail to populate data", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });

					    	buttonDisabled(false);
					    	progressDialog.dialog('close');
				    	}
				    },
				    error: function(jxhr, status){
				    	messageAlertOk("Fail to populate data", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });

				    	buttonDisabled(false);
				    	progressDialog.dialog('close');
				    }
				}).submit();
			}
		});
		
		app.btnReset.click(function(){
			buttonDisabled(true);
			location.href="@{GeneralUpload.reset()}";	
			buttonDisabled(false);
		});
		
		app.btnProcess.click(function(){
			if (validate()) {
				globalProgress = html.loadingDialog().dialog('open');
				buttonDisabled(true);

				app.updProcessId.val($().getRandomChar(5)+""+(new Date()).getTime());
				pingProgressBatch(); // do ping
				
				var param = {
					"batchNo" : app.batchNo.val(),
					"processId" : app.updProcessId.val(),
					"oldProcessId" : oldProcessId
				};
				
				var result = html.fetchAsyncTimeout("@{GeneralUpload.processBatch()}", {"param" : param});
			}
		});
		
		function pingProgressPopulate(){
			if (app.updProcessId.val() != '') {
				try{
					html.fetchAsync("@{GeneralUpload.getProcessRunUploadResult()}", {"processId" : app.updProcessId.val()}, function(data){
			    		if (data == null) {
			    			setTimeout(pingProgressPopulate, 5000);
			    		}else{
			    			processPopulateFinish(data);
			    			html.fetchAsync("@{GeneralUpload.delProcessRunUploadResult()}", {"processId" : app.updProcessId.val()}, function(data){
			    				app.updProcessId.val("");
			    			});
			    		}
					});
				}catch(err) { setTimeout(pingProgressPopulate, 5000); }
			}
		}
		
		function pingProgressBatch(){
			if (app.updProcessId.val() != '') {
				try{
					html.fetchAsync("@{GeneralUpload.getProcessBatchResult()}", {"processId" : app.updProcessId.val()}, function(data){
			    		if (data == null) {
			    			setTimeout(pingProgressBatch, 5000);
			    		}else{
			    			processBatchFinish(data);
			    			html.fetchAsync("@{GeneralUpload.delProcessBatchResult()}", {"processId" : app.updProcessId.val()}, function(data){
			    				app.updProcessId.val("");
			    			});
			    		}
					});
				}catch(err) { setTimeout(pingProgressBatch, 5000); }
			}
		}
	}else{
		return new RunUpload(html);
	}
}