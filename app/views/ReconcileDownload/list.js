function RecondUpload(html){
	if( this instanceof RecondUpload ){
		var app = html.inject(this, true);
		
		var kseiFile = "${kseiFile}";
		var biFile = "${biFile}";
		var entFile = "${entFile}";
		
		$("#divDwn").hide();
		
		var legitExt = "";
		
		var globalProgress = null;
		
		function chgType(){
			$("#medInp").val('SUMMARY HOLDING');
			
			if($("#holdingType1").attr('checked')){
				$("#depository").val(1).blur();
			}else if ($("#holdingType2").attr('checked')){
				$("#depository").val(2).blur();
//			}else if ($("#holdingType3").attr('checked')){
//				$("#depository").val(3).blur();
			//nanti else if yang bawah diganti "#holdingType4 
			}else if ($("#holdingType3").attr('checked')){
				$("#depository").val(1).blur();
				$("#medInp").val('CORPORATE ACTION');
			}
		}
		
		function validate() {
			var conFile = app.fileError.required(app.file.isEmpty());
			var filenames = app.file.val();
			var valid = true;
			var skipCheck = false;
			
			app.fileError.hide();
			app.holdingDateError.hide();
			app.depositoryError.hide();
			
			app.depository.removeClass('fieldError');
			app.holdingDate.removeClass('fieldError');
			
			if($("#holdingType1").attr('checked'))legitExt = kseiFile;
			else if ($("#holdingType2").attr('checked')) legitExt = biFile;
			//else if ($("#holdingType3").attr('checked')) skipCheck = true;
			else if ($("#holdingType3").attr('checked')) legitExt = entFile;
			
			if(conFile){
				filenames = filenames.split("\\");
				filenames = filenames[filenames.length-1];
				
				var fileparts = filenames.split(".");
				var ext = fileparts[fileparts.length-1];
				if(!skipCheck && legitExt.toUpperCase().indexOf(ext.toUpperCase())<0){
					app.fileError.html("File type must be "+legitExt);
					app.fileError.show();
					valid = false;
				}
			}else {
				valid = false;
				app.fileError.html("required");
				app.fileError.show();
			}
			
			if(app.holdingDate.isEmpty()){
				valid = false;
				app.holdingDate.addClass('fieldError');
				app.holdingDateError.html("required");
				app.holdingDateError.show();
			}
			
			if(app.depository.isEmpty()){
				valid = false;
				app.depository.addClass('fieldError');
				app.depositoryError.html("required");
				app.depositoryError.show();
			}
			
			if(valid)app.btnReconcile.button("option","disabled", true);
			
			return valid;
		}
		
		function showDwn(cnt,filename){
			$("#divDwn").show();
			$("#downloadLabel").html(cnt+" record mismatch, download");
			$("#downloadLink").attr("href", "@{ReconcileDownload.downloadGeneratedFile()}?downloadfile="+filename);
		}
		
		function hideDwn(){
			$("#divDwn").hide();
		}
		
		function getRecon(){
			var valid = true;
			$.ajax({
				  type: 'GET',
				  url: "@{ReconcileDownload.processRecon()}",
				  data : {"processId" : app.processId.val()},
				  async : false,
				  success: function(data){
					  checkRedirect(data);
					  if( data.SUCCESS === "SUCCESS" ){
						  valid = true;
						  showDwn(data.CNT,data.FILENAME);
						  $("#downloadLabel").html(data.CNT+" record mismatch, download");
						  app.btnReconcile.button("option","disabled", false);
						  globalProgress.dialog('close');
					  }else{
						  if(data.MESSAGE!=null && data.MESSAGE!=""){
							  $("#pError").html(data.MESSAGE);
						  }else{
							  $("#pError").html("File format is not correct");
						  }
						  $("#pError").show();
						  globalProgress.dialog('close');
						  app.btnReconcile.button("option","disabled", false);
					  }
				  },
				  error: function(jxhr, status){
					 valid = false;
					 app.btnReconcile.button("option","disabled", false);
					 globalProgress.dialog('close');
				  }
			});
			return valid;
		}
		
		
		if($("input[type='radio']:checked").val()==null){
			$("#holdingType1").attr('checked',true);
		}
		//PICK_FA_COA_BOTTOM_LEVEL
		$('#depository').dynapopup2({key:'depositoryKey', help:'depositoryHelp', desc:'depositoryDesc'},'PICKRPT_DEP_RECONCILE', '', 'depository', function(data){
			$('#depository').removeClass('fieldError');
			$('#depositoryKey').val(data.code);
			$('#depositoryDesc').val(data.description);
			$('#h_depository').val(data.description);
		},function(data){
			$('#depository').val('');
			$('#depository').addClass('fieldError');
			$('#depositoryKey').val('');
			$('#depositoryDesc').val('NOT FOUND');
			$('#h_depository').val('');			
		});
		
		chgType();
		
		$("input[type=radio]").click(function(){
			chgType();
		});
		
		app.file.add(app.btnPopulate).add(app.btnReset).add(app.btnProcess).add(app.linkSaveValidationError).add(app.linkSaveProcessError).button();
		
		app.btnReconcile.button();
		app.btnReset.button();
		
		app.btnReset.click(function(){
			location.href="@{ReconcileDownload.list()}";
		});
		
		app.btnReconcile.click(function(){
			hideDwn();
			$("#pError").hide();
			if(validate()){
				globalProgress = html.progressDialog().dialog('open');
				app.processId.val($().getRandomChar(5)+""+(new Date()).getTime());
				app.searchForm.ajaxForm({
					dataType: "json",
				    contentType: "application/x-www-form-urlencoded;charset=utf-8",
				    url: "@{ReconcileDownload.preReconDownload()}",
				    success: function(data, status, jxhr, form) {
				    	console.log("data = "+data);
				    	console.log("success "+data.RESULT);
				    	if (data.RESULT == 'SUCCESS') {
					    	getRecon();
				    	}else{
				    		app.btnReconcile.button("option","disabled", false);
				    		globalProgress.dialog('close');
				    	}
				    },
				    error: function(jxhr, status){
				    	messageAlertOk("Fail to populate data", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
				    	app.btnReconcile.button("option","disabled", false);
				    	globalProgress.dialog('close');
				    }
				}).submit();
			}
		});
		
		var i = 0;
		$("#pHold input[type=radio]").each(function(){
			if(i>0){
				$(this).before("<br/>");
			}
			i++;
		});
		
	}else{
		return new RecondUpload(html);
	}
}



















