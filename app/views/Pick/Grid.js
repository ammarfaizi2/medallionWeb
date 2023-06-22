function Grid(html) {
	if (this instanceof Grid) {
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var query = false;
/* =========================================================================== 
 * Table Parameter
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.lookupId = $("#lookupId", html).val();
			p.filter = $("#filter", html).val();
			p.query = query;
			
			if (query == false) query = true;
			return p;
		};
		
/* =========================================================================== 
 * Function
 * ========================================================================= */
		

/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		var data = html.fetch("@{Pick.pickProp()}", {'lookupId' : $("#lookupId", html).val()});
		
		for (x in data.columns) {
			if (data.widths[x] == '0' || data.widths[x] == undefined) continue; // bila width = 0, di anggap hidden
			
			 var th = $("<th>").appendTo($("#headertr", html));
			 th.attr("width", data.widths[x]+"px");
			 if (data.types[x] == 'String') { th.attr("field", data.columns[x]+"|"+data.types[x].toLowerCase()+"|none|left|sort"); }
			 if (data.types[x] == 'Number') { th.attr("field", data.columns[x]+"|"+data.types[x].toLowerCase()+"|decimal2|right|sort"); }
			 if (data.types[x] == 'Date') { th.attr("field", data.columns[x]+"|"+data.types[x].toLowerCase()+"|date|center|sort"); }
			 th.html(data.titles[x]);
		}
		
		var options = {
			"sPaginationType" : "two_button",
			"defaultSearch" : $("#code", html).val(),
			"showLoading" : "false"
		};
		var c_ = 0;
		var paramInUrl = "";
		var formId = $("#formId").val();
		$(formId+" .reportinput").each(function(idx, el){
			var id = $(el).attr("id");
			paramInUrl = paramInUrl + "&reportParams["+c_+"].field="+id+"&reportParams["+c_+"].value="+$(el).val()+"&reportParams["+c_+"].type="+$(el).parent().find("#"+id+"Type").val();
			c_++;
		});
		var datatable = html.paging("@{Pick.paging()}?"+paramInUrl, this, function(){}, options);

/* =========================================================================== 
 * Event
 * ========================================================================= */

	}else{
		return new Grid(html);
	}
}