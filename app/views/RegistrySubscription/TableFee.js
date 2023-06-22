function TableFee(html, rowCallback, type) {
	if (this instanceof TableFee) {
		var table = html.dataTable({
			aoColumns: [ {bVisible:false},
			               null,
		    	           null,
		    	           null
				   	   ],
			aoColumnDefs: [{asSorting:[1,"asc"], aTargets:[0]}],
			bFilter:false,
			bSort:false,
			bInfo:false,
			bJQueryUI:true,
			bPaginate: false			
		});
		
		var type = type;
		
		$('th:first', html).css('width', '');
		
/*		this.addRow = function(name) {			
			html.clazz("dataTables_empty").parent().remove();
			var idx = html.tbody().children().length;

			html.tbody().append(
					"<tr class='odd'>"+
					"	<td>"+
					"		<input id='tierNumber"+idx+"' class='numeric mask calculate' type='text' disabled='disabled' value='"+(idx+1)+"'>"+
					"		<input id='h_tierNumber"+idx+"' type='hidden' value='"+(idx+1)+"'>"+
					"		<input id='tierNumber"+idx+"Stripped' type='hidden' value='"+(idx+1)+"' name='"+name+"["+idx+"].tierNumber'>"+
					"	</td>"+
					"	<td>"+
					"		<input id='upperLimit"+idx+"' class='numeric mask calculate' type='text' value=''>"+
					"		<input id='upperLimit"+idx+"Stripped' type='hidden' value='' name='"+name+"["+idx+"].upperLimit'>"+
					"	</td>"+
					"	<td>"+
					"		<input id='value"+idx+"' class='numeric mask calculate' type='text' value=''>"+
					"		<input id='value"+idx+"Stripped' type='hidden' value='' name='"+name+"["+idx+"].value'>"+
					"	</td>"+
					"</tr>"
			);
		};
*/			
		this.addRow = function(name, object) {
			html.clazz("dataTables_empty").parent().remove();
			var idx = getBeforeIndex(object.upperLimit);
			
			$("tr", html.tbody()).eq(idx).before(
//			html.tbody().append(
					"<tr class='odd'>"+
//					"	<td>"+
//							+(idx+1)+ 
//					
//					"	</td>"+
					"	<td>"+
							+ object.upperLimit +
					"	</td>"+
					"	<td>"+
							+ object.value +
					"	</td>"+
					"	<td del='true'>"+
					"		<center><input id='deleteButton' type='button' value='Delete' feeType='"+type+"'></center>"+
					"		<input id='h_tierNumber"+idx+"' type='hidden' value='"+(idx+1)+"'>"+
					"		<input id='tierNumber"+idx+"Stripped' type='hidden' value='"+(idx+1)+"' name='"+name+"["+idx+"].tierNumber'>"+
					"		<input id='upperLimit"+idx+"Stripped' type='hidden' value='"+ object.upperLimit + "' name='"+name+"["+idx+"].upperLimit'>"+
					"		<input id='value"+idx+"Stripped' type='hidden' value='" + object.value + "' name='"+name+"["+idx+"].value'>"+
					"		<input id='feeTierType"+idx+"' type='hidden' value='" + object.type + "' name='"+name+"["+idx+"].id.type'>"+
					"	</td>"+
					"</tr>"
			);
			rearrage(name, object);
			creteTableListener();
//			html.dataTable().fnSort( [[1,'asc']] );
		};
		
//		this.getSelectedRow = function() {
//			if (selectedIdx == -1) return null;
//			var object = new Object();
//			object.idx = 0;
//			object.upperLimit = 0;
//			object.value = 0;
//			selectedIdx = -1;
//			return object;
//		};
		
		this.updateRow = function(object) {
			if (object.upperLimit == '') {
				$('#upperLimit'+object.index+'Stripped', html).val("");
				$('#upperLimit'+object.index+'Stripped', html).parent().parent().children().eq(0).html("MAX");
			} else {
				$('#upperLimit'+object.index+'Stripped', html).val(object.upperLimit);
				$('#upperLimit'+object.index+'Stripped', html).parent().parent().children().eq(0).html(object.upperLimit);
			}
			$('#value'+object.index+'Stripped', html).val(object.value);
			$('#value'+object.index+'Stripped', html).parent().parent().children().eq(1).html(object.value);
			$('#feeTierType'+object.index, html).val(object.type);
		};
		
		this.delRow = function(name, idx) {
			html.tbody().children().eq(idx).remove();
			rearrage(name);
		};
		
		function rearrage(name) {
			var ctr = 0;
			$(html.tbody().tr()).each(function(idx, etr){
				$("td[del='true'] > input", etr).each(function(idx, e){
					if (idx == 0) $(this).attr("id", "h_tierNumber"+ctr).attr("value", (ctr+1));
					if (idx == 1) $(this).attr("id", "tierNumber"+ctr+"Stripped").attr("value", (ctr+1)).attr("name", name+"["+ctr+"].tierNumber");
					if (idx == 2) $(this).attr("id", "upperLimit"+ctr+"Stripped").attr("name", name+"["+ctr+"].upperLimit");
					if (idx == 3) $(this).attr("id", "value"+ctr+"Stripped").attr("name", name+"["+ctr+"].value");
					if (idx == 4) $(this).attr("id", "feeTierType"+ctr).attr("name", name+"["+ctr+"].id.type");
				}); 
				ctr++;
			});
		}
		
		
		//td(2) td ke 2 // untuk table Sub, Red, Swi data upperLimit (Value in table)
		function getBeforeIndex(feeValue){
			var returnIdx = -1;
			$(html.tbody().tr()).each(function(idx, e){
				var feeValue2 = $("> input", $(this).td(2)).eq(2).val().toNumber(",");
				if (returnIdx == -1) {
					if (feeValue2 == "" || feeValue2 > feeValue) {
						returnIdx = idx;
					};
				};
			});
			return returnIdx;
		}
		
		function creteTableListener() {
			//console.log("create table listener");
			$("td", html.tbody()).unbind("click");
			$("td", html.tbody()).click(function(){
				
				if (typeof rowCallback == 'function') {
					var eRow = $(this).parent();
					var row = eRow.index();
//					alert(row);
//					if (eRow.children().eq(1).html().trim() == "MAX"){
//						alert("dd");
////						prd.upperLimit.val("MAX");
//					}
					var object = new Object();
					object.upperLimit = $('#upperLimit'+row+'Stripped', eRow).val(); 
					object.value = $('#value'+row+'Stripped', eRow).val(); 
					object.index = row;
					object.type = type;
					rowCallback(object);
				}
			}); 
			$("td[del='true']", html.tbody()).unbind("click");
		}
		
		creteTableListener();
	} else{
		return new TableFee(html, rowCallback);
	}
}
	