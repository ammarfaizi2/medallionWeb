function Table(tag, jaction, cb) {
	if (this instanceof Table) {
		
		this.render = function() {
			var table = tag.dataTable( {
				"bProcessing" : true,
				"bStateSave" : true,
				"bServerSide" : true,
				"bJQueryUI" : true,
				"bScrollCollapse" : true,			
				"sAjaxSource" : tag.data("url"),
				"aoColumns" : tag.data("colum"),
				"sPaginationType" : "full_numbers",
				"sScrollX" : '100%',			
				"aaSorting" : [[1,'asc']],
				"oLanguage" : {
					"sLengthMenu": "Page length: _MENU_",
					"sSearch": "Code :",
					"sZeroRecords": "No matching records found"
				},
				"fnServerData" : function(sSource, aoData, fnCallback ) {
					aoData.push(
						{ "name": "table", "value": "CpComplianceProfile" },
						{ "name": "sql", "value": "complianceProfCode, description, active, status" }
					);
					$.ajax({"dataType": 'json',
						"type": "POST",
						"url": sSource,
						"data": aoData,
						"success": fnCallback,
						"error": function() {
							alert("Fail to fetch data");
						}
						});
				}
			});
			
//			$("div.dataTables_filter input", $("#"+(table.attr("id")+"_wrapper")))
//			.unbind('keyup')
//			.keyup(function(e) {
//				if (e.keyCode == 13) { table.fnFilter(this.value); }
//			});
			
//			table.fnSetColumnVis(0, false);
//			table.fnSetColumnVis(1, false);
			
			//PENTING!!!! jgn pake live, krn ini akan berat pake .on di jquery 1.7
			
//			$("#"+jaction.id+" tbody tr").live("mouseover", function(event){
//				$(this).addClass("ui-state-hover");
//			});
//			$("#"+jaction.id+" tbody tr").live("mouseout", function(event){
//				$(this).removeClass("ui-state-hover");
//			});
			
//			if (cb) {
//				tag.data('action', jaction);
//				if (cb[jaction.id]) {
//					cb[jaction.id](table);
//					if (cb[jaction.id+'Click']) {
//						$("#"+jaction.id+" tbody tr").live("click", function(event){
//							var data = table.fnGetData(this);
//							$("#"+jaction.id).dialog('close');
//							cb[jaction.id+'Click'](table, event, data); 
//						});
//					}
//				}
//			}
		};
		
		var parent = this;
    	
		$.ajaxSetup({ async : false });
		
//		tag.data("url", jaction.sAjaxSource);
//		tag.data("colum", jaction.colum);
		parent.render();
	}else{
		return new Table(tag, jaction, cb);
	}
};