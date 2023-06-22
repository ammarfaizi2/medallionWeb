function TableExceptionSpecificDate(html, type) {
	if (this instanceof TableExceptionSpecificDate) {
		var table = html.dataTable({
			aoColumns: [ 	 
							 null,
				             null
					   ],
			bJQueryUI:true,
			aaSorting:[[0,'asc']],
			bFilter:false,
			bPaginate:false,
			bInfo:false
		});
		
		var type = type;
	
		this.addRow = function(name, data) {
			html.clazz("dataTables_empty").parent().remove();
			var idx =  html.fnGetNodes().length;
			
			html.fnAddData([
			                data.exceptionDate,
			                "<center><input id='deleteButton' type='button' value='Delete' exceDateType='" +type+"'></center>" +
			                "<input type='hidden' value='"+data.exceptionDate+ "' name='"+name+"["+idx+"].id.exceptionDate'>"+
			                "<input type='hidden' value='"+data.productCode+"' name='"+name+"["+idx+"].id.productCode'>"+
			                "<input type='hidden' value='"+data.type+"' name='"+name+"["+idx+"].id.type'>"
			]);
			idx++;
			rearrage(name, data);
//			creteTableListener();
		};
		
		this.delRow = function(name, idx) {
			console.debug(name +" | "+ idx);
			html.fnDeleteRow(idx);	
			rearrage(name);
			
		};
		
		function rearrage(name) {
			var ctr = 0;
			$(html.tbody().tr()).each(function(idx, etr){
				console.debug("[REARRANGE] idx = " +idx);
				$("td[del='true'] > input", etr).each(function(idx, e){
					if (idx == 0) $(this).attr("id", "exceptionDate"+ctr).attr("name", name+"["+ctr+"].id.exceptionDate");
					if (idx == 1) $(this).attr("id", "productCode"+ctr).attr("name", name+"["+ctr+"].id.productCode");
					if (idx == 2) $(this).attr("id", "exceDateType"+ctr).attr("name", name+"["+ctr+"].id.type");
				});
				ctr++;
			});
		}
		
		function creteTableListener() {
			//console.log("create table listener lock exception");
			$("td[del='true']", html.tbody()).unbind("click");
		}
		creteTableListener();
		
	} else {
		return new TableExceptionSpecificDate(html);
	}
};