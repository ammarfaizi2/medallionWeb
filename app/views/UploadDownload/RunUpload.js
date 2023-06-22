function RunUpload(html) {
	if (this instanceof RunUpload) {
/*================================================================== 
 * GUI Variable
 *================================================================== */		
		var app = html.inject(this, false);
		var table = null;
		var parent = this;
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function(type) {
			var p = new Object();
			p.batchNo = app.batchNo.val();
			p.errorOnly = app.errorOnly.checked();
			return p;
		};
		
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		
		app.profile.popupUpprofile("?filter=Upload");
		
/*==================================================================
 * Function
 *==================================================================*/		
		
		function validate() {
			var conProfileCode = app.profileError.required(app.profile.isEmpty());
			if (!conProfileCode) { app.profileError.removeClass("fieldError"); }
			
			var conFile = app.fileError.required(app.file.isEmpty());
			if (!conFile) { app.fileError.removeClass("fieldError"); }
			
			return conProfileCode && conFile;
		}
		
		function doPopulate() {
			if (validate()) {
				app.populate.val("Loading now ...");
				app.populateError.html("");
				app.populate.button("option", "disabled", true);
				app.runUploadForm.ajaxForm({
					dataType: "json",
					contentType: "application/x-www-form-urlencoded;charset=utf-8",
				    url: "@{previewUpload()}", 
				    success: function(data, status, jxhr, form) {
				    	if (data["ERROR"]) { ajaxError(data);
				    	}else{ ajaxSuccess(data); }
				    	app.populate.val("Populate");
				    },
				    error: function(jxhr, status){
				    	ajaxError({"ERROR":"Error processing profile or file."});
				    	app.populate.val("Populate");
				    }
				}).submit();
			}
			app.populate.button("option", "disabled", false);
		}
		
		function ajaxError(data) {
			var errors = data["ERROR"];
			for (x in errors) {
				app.populateError.append("<p>"+errors[x]+"</p>");
			}
		}
		
		function ajaxSuccess(data) {
			app.populate.button("option", "disabled", false);
			app.process.button("option", "disabled", false);
			app.reset.button("option", "disabled", false);
			app.save.button("option", "disabled", true);
			
			app.batchNo.val(data["BATCHID"]);
			
			if (table != null) table.fnDestroy();
			
			var trHeader = $("thead tr", app.table).html("");
			var headers = data["PROFILE_HEADER"];
			var sheaders = data["SOURCE_HEADER"];
			var types = data["PROFILE_TYPE"];
			for (x in headers) {
				var align = "left";
				if (types[x].toLowerCase() == "number") { align = "right"; }
				if (types[x].toLowerCase() == "date") { align = "center"; }
					
				trHeader.append("<th width='150px' field='"+sheaders[x]+"|string|none|"+align+"|nosort'>"+headers[x]+"<br>"+sheaders[x]+"</th>");
			}
			
			table = app.table.paging("@{UploadDownload.previewUploadPaging()}", parent, function(p){
				app.totalRow.val(p.iTotalSuccess + p.iTotalFail);
				app.totalSuccess.val(p.iTotalSuccess);
				app.totalFail.val(p.iTotalFail);
				
				if (p.iTotalSuccess == "0") {
					app.process.button("option", "disabled", true);
				}else {
					app.process.button("option", "disabled", false);
				}
			});
			
			app.contentPopulate.show();
			$("#table_wrapper").show();
			app.processForm.show();
			
			table.fnAdjustColumnSizing();
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */		

		app.populate.button("option", "disabled", false);
		app.process.button("option", "disabled", true);
		app.reset.button("option", "disabled", true);
		app.save.button("option", "disabled", true);
		
		app.contentPopulate.hide();
		$("#table_wrapper").hide();
		app.processForm.hide();
		
		app.errorOnly.change(function(){
			table.fnPageChange("first");
		});
		
		app.process.click(function(){
			app.process.val("Loading now ...");
			app.populate.button("option", "disabled", true);
			app.process.button("option", "disabled", true);
			app.reset.button("option", "disabled", true);
			app.save.button("option", "disabled", true);

			try{
				html.fetch("@{UploadDownload.realUpload()}", {"id":app.batchNo.val()});
				table.fnPageChange("first");
			}catch(error) { alert(error); }
			
			app.populate.button("option", "disabled", false);
			app.process.button("option", "disabled", false);
			app.reset.button("option", "disabled", false);
			if (app.totalFail.val() != "0") { app.save.button("option", "disabled", false);					
			}else{ app.save.button("option", "disabled", true); }

			app.process.val("Process");
		});
		
		app.populate.click(function(){
			app.contentPopulate.hide();
			$("#table_wrapper").hide();
			app.processForm.hide();
			doPopulate();
		});

		app.reset.click(function(){
			app.populate.button("option", "disabled", true);
			app.process.button("option", "disabled", true);
			app.reset.button("option", "disabled", true);
			app.save.button("option", "disabled", true);
			
			window.location.reload();
		});
		
		app.save.click(function(){			
			window.location.href = "saveErrorBatch?batchId="+app.batchNo.val();
		});
	} else{
		return new RunUpload(html);
	}
}
