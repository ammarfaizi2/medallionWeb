function UploadData(html) {
	if (this instanceof UploadData) {
		
		$("#btnPopulate").click(function(){
			var profileKey = $("#profileKey").val();
			var filename = $("#filename").val();
			var description = $("#description").val();
			var hasTitle = $("#hasTitle").attr("checked");

			var action = "@{UploadDownload.populateUpload()}";
			$('#uploadDataForm').attr('action', action);
			$('#uploadDataForm').submit();
		});
		
		$('#btnClear').click(function(){
			location.href = '@{UploadDownload.uploaddata()}';
		});
		
		var backToList = function() {
			//location = "@{uploaddata()}";
			$.get("@{UploadDownload.fetchBatchDesc()}", {'id': $('#batchKey').val()}, function(data) {
				checkRedirect(data);
				var jsonMessage = {};
				var jsonIsiMessage = {};
				try{
					// hanya mengambil index 0 untuk show, silahkan loop
					
					var rawMessage = data.details[0].rawMessage;
					jsonMessage = JSON.parse(rawMessage);
					
				}catch(e){
					console.log("error "+e);
				}
				
				$("#tblTr").empty();
				$("#tblBody").empty();
				$("#status").val(data.status);
				$("#filename").val(data.filename);
				$("#uploadDate").val(new Date(data.uploadDate).fmtDate("/"));
				$("#description").val(data.description);
				$("#profileKey").val(data.profile.profileKey);
				if (data.hasTitle) { $("#hasTitle").attr("checked", "checked");	
				}else{ $("#hasTitle").removeAttr("checked"); }
				
				$("#tblTr").append("<td width='100px' class=ui-state-default><b>Error_Description</b></td>");
				$("#tblTr").append("<td width='100px' class=ui-state-default><b>Status</b></td>");
				for(fieldHeader in jsonMessage){
					$("#tblTr").append("<td width='100px' class=ui-state-default><b>"+fieldHeader+"</b></td>");
				}
				
				for (x in data.details){
					var d = data.details[x];

					if(d.errorDescription == undefined){
						d.errorDescription = '';
					}

					if(d.status == undefined){
						d.status = '';
					}
					jsonIsiMessage = JSON.parse(d.rawMessage);
					var vTr = $("<tr>").appendTo($("#tblBody"));
					vTr.append("<td><b>"+d.errorDescription+"</b></td>");
					vTr.append("<td><b>"+d.status+"</b></td>");
					
					for(z in jsonIsiMessage){
						vTr.append("<td><b>"+jsonIsiMessage[z]+"</b></td>");	
					}					
				}
				
				$("#tblPopulate").dataTable({
					bJQueryUI:true,
					bRetrieve:true, 
					sScrollX:'100%',
					bScrollCollapse: true,		
					sPaginationType: 'full_numbers'
				});
			});
			$("#description").attr('disabled', true);
			$("#hasTitle").attr('disabled', true);
			$("#profileKey").attr('disabled', true);
		}
		
		if (('${mode}'!='entry') && ($('#batchKey').val() != '')){
			messageAlertOk('Upload success with id '+$('#batchKey').val()+'', "ui-icon ui-icon-circle-check", "Notification", backToList);
		}
		
	}else{
		return new UploadData(html);
	}
}