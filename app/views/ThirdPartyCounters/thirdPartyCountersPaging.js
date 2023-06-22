function Paging(html) {
	if (this instanceof Paging) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Search Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			/*var p = new Object();
			p.customerNoOperator = app.customerNoOperator.val();
			p.customerSearchNo = app.customerSearchNo.val();
			p.customerNameOperator = app.customerNameOperator.val();
			p.customerSearchName = app.customerSearchName.val();
			p.contactNameOperator = app.contactNameOperator.val();
			p.customerSearchContactName = app.customerSearchContactName.val();
			return p;*/
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
//		app.root.accordion({collapsible: true});
		
//		app.search.add(app.reset).button();
		app.datatable = app.tableThirdPartyCounters.paging("@{ThirdPartyCounters.paging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		/*app.search.click(function(){
			app.datatable.fnPageChange("first");
		});

		app.reset.click(function(){
			app.datatable.trigger("fetch", [0, "checked"]);
			app.datatable.search.val("");
		});

		app.datatable.bind("select", function(event, prop){
			alert(prop.bean.customerNo+"-"+prop.row[1]+"-"+prop.tr);
		});
		
		app.datatable.bind("selects", function(event, props){
			alert("a");
			for (x in props) {
				alert(x);
				alert(props.length+"  "+props[x].bean.customerNo+"-"+props[x].row[1]);
			}
		});*/
		
		app.datatable.bind("select", function(event, prop){
			if ((jQuery.trim(prop.bean.recordStatus) == "N")|| (jQuery.trim(prop.bean.recordStatus) == "U")||(jQuery.trim(prop.bean.recordStatus) == '${recordStatusNew}')|| (jQuery.trim(prop.bean.recordStatus) == '${recordStatusUpdate}')){
				location.href='@{view()}/?id=' + prop.bean.thirdPartyKey+'&group=${group}';
			}else{
				location.href='@{edit()}/?id=' + prop.bean.thirdPartyKey+'&group=${group}';
			}
//			
		});
		app.newData.button();
		
		app.newData.click(function(){
			location.href='@{entry()}#{if group}/${group}#{/}';
		});
		
	}else{
		return new Paging(html);
	}
}