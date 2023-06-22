function ExecuteUpload(html) {
	if (this instanceof ExecuteUpload) {
		$("#tblExecUpload").dataTable({
			aaSorting: [[1,'asc']],
			aoColumnDefs: [ { bVisible:false, aTargets:[0] }, 
			                { bSearchable: false, aTargets: [ 0 ] 
						  } ],	
			bJQueryUI:true,
			bRetrieve:true, 
			sScrollX:'100%',
			bScrollCollapse: true,		
			sPaginationType: 'full_numbers'
		});
		$('#tblExecUpload tbody tr').die('click');
		$('#tblExecUpload tbody tr').live('click', function() {
			
			var data = $("#tblExecUpload").dataTable().fnGetData(this);
			
			$.get("@{UploadDownload.fetchBatchDesc()}", {'id': data[1]}, function(data) {
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
				$('#idBatch').val(data.batchKey);
				$('#totalSuccess').val(data.sizeSuccess);
				$('#totalError').val(data.sizeError);
				$('#errorDescription').val(data.details.errorDescription);
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
				
				$("#tblPopulateUpload").dataTable({
					bJQueryUI:true,
					bRetrieve:true, 
					bDestroy:true,
					sScrollX:'100%',
					bScrollCollapse: true,		
					sPaginationType: 'full_numbers'
				});
				//$("#batchKey").attr('disabled', true);
				/*$('#btnRealReset').css('display','');*/
				$('.buttons').css('display', '');
				$("#divDescription").css('display','');
				//$('#tblExecUpload tbody tr').die('click');
			});
		});
/*		$("#batchKey").change(function(){
			$.get("@{UploadDownload.fetchBatch()}", {'id': $(this).val()}, function(data) {
				$("#tblTr").empty();
				$("#tblBody").empty();
				$("#status").val(data.status);
				$("#filename").val(data.filename);
				$("#uploadDate").val(new Date(data.uploadDate).fmtDate("/"));
				$("#description").val(data.description);
				if (data.hasTitle) { $("#hasTitle").attr("checked", "checked");	
				}else{ $("#hasTitle").removeAttr("checked"); }
				
				for (x in data.profile.details) {
					var d = data.profile.details[x];
					$("#tblTr").append("<td width='100px' class=ui-state-default><b>"+d.targetField+"</b></td>");
				}
				
				for (y in data.contents) {
					var row = data.contents[y];
					var vTr = $("<tr>").appendTo($("#tblBody"));
					for (z in row) { vTr.append("<td>"+row[z]+"</td>"); }					
				}
				
				$("#tblPopulateUpload").dataTable({
					bJQueryUI:true,
					bRetrieve:true, 
					sScrollX:'100%',
					bScrollCollapse: true,		
					sPaginationType: 'full_numbers'
				});
				$("#batchKey").attr('disabled', true);
				$('#btnRealReset').css('display','');
				
			});
		});*/
		
		$("#btnRealUpload").click(function(){
			$.get("@{UploadDownload.realUpload()}", {'id':$("#idBatch").val()}, function(data) {
				checkRedirect(data);
			});
		}); 
		
		$("#btnRealCancel").click(function(){
			location.href = '@{UploadDownload.list()}';
		});
		$("#btnRealReset").click(function(){
			location.href = '@{UploadDownload.executeupload()}';
		});
	}else{
		return new ExecuteUpload(html);
	}
}