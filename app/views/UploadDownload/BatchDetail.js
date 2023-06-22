
function BatchDetail(html) {
	if (this instanceof BatchDetail) {
		/*================================================================== 
		 * GUI Variable
		 *================================================================== */
		var allRowSize = 0;
		/*==================================================================
		 * Function
		 *==================================================================*/
				
		/*==================================================================
		 * Declare popup tabel /detail
		 * =================================================================*/
		/*================================================================== 
		 * Event
		 *================================================================== */
			var table = null;
			var myContent = $("#contentdetail");
			
			$('#udbatch').dynapopup('PICK_UD_BATCH', $('#udbatch').val(), 'populate');
			
			$("#btnPopulate").click(function(){
				var errSpan = $("#udbatch").parent().find("span.error");
				errSpan.html("");
				if( $("#udbatch").val() == "" ){
					errSpan.html("Required");
					return false;
				}
				
				$.ajax({
					type: 'POST',
				    url: "@{previewBatch()}",
				    data: { "batchId" :  $("#udbatch").val() },
				    success: function( data ){
						checkRedirect(data);
				    	if( data.BATCHDETAIL ){
							//format into table
							var myNewTableheader = "\n";
							var myNewTableBody = "\n";

							// new body
							if( data.BATCHDETAIL ){
								for( var idx in  data.BATCHDETAIL){
									var myNewRow = "<tr>";
									var currDetail = data.BATCHDETAIL[ idx ];
									
									// construct html
									myNewRow = myNewRow + "<td>" + currDetail.batchId + "</td>";
									myNewRow = myNewRow + "<td>" + currDetail.batchDetailId + "</td>";
									myNewRow = myNewRow + "<td>" + currDetail.status + "</td>";
									myNewRow = myNewRow + "<td>" + currDetail.errorDescription + "</td>";
									var json = [];
									if( currDetail.rawMessage == null || currDetail.rawMessage == undefined ){
										// create a dummy then
										for( tmpx in data.PROFILE_HEADER ){
											var currHeader = data.PROFILE_HEADER[tmpx];
											json[currHeader] = "";
										}
									}else{
										json = JSON.parse( currDetail.rawMessage );
									}
									for( tmpx in data.PROFILE_HEADER ){
										var currHeader = data.PROFILE_HEADER[tmpx];
										var tmpval = json[currHeader];
										if( tmpval === "null" || tmpval === null || tmpval == undefined){
											tmpval = "";	
										}
										
										myNewRow = myNewRow + "<td>"+tmpval+"</td>";
									}
									
									myNewRow = myNewRow + "</tr>";
									myNewTableBody = myNewTableBody + myNewRow;
								}
							}
							myNewTableBody = myNewTableBody + "\n";
							
							// header
							var myNewRow = "<tr>";
							myNewRow = myNewRow + "<th width=\"7px\" class=\"ui-state-default\"><b>Batch ID</b></th>";
							myNewRow = myNewRow + "<th width=\"7px\" class=\"ui-state-default\"><b>Batch Detail ID</b></th>";
							myNewRow = myNewRow + "<th width=\"100px\" class=\"ui-state-default\"><b>Status</b></th>";
							myNewRow = myNewRow + "<th width=\"500px\" class=\"ui-state-default\"><b>Error Description</b></th>";
							for( tmpx in data.PROFILE_HEADER ){
								var currRow = data.PROFILE_HEADER[tmpx];
								myNewRow = myNewRow + "<th class=\"ui-state-default\"><b>"+currRow+"</b></th>";
							}
							myNewRow = myNewRow + "</tr>";
							myNewTableheader = myNewTableheader + myNewRow + "";
							
							// clear first
							if( table !== null ){
								table.fnDestroy();
							}
							$("th", $("#displaydetail").find("thead")).remove();
							$("tr", $("#displaydetail").find("tbody")).remove();

							$("#displaydetail").find("thead").append( myNewTableheader );
							$("#displaydetail").find("tbody").append( myNewTableBody );
																								
							myContent.fadeIn("slow");
							
							// create data table
							table = $("#displaydetail").dataTable({
								bDestroy: true,
								bJQueryUI:true,
								sScrollX:'100%',
								bScrollCollapse: true,
								sPaginationType: 'full_numbers'
							});
						}
				    },
					dataType: "json"
				});	
			});
	}else{
		return new BatchDetail(html);
	}
}