function NonPaging(html) {
	if (this instanceof NonPaging) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.customerNoOperator = app.customerNoOperator.val();
			p.customerSearchNo = app.customerSearchNo.val();
			p.customerNameOperator = app.customerNameOperator.val();
			p.customerSearchName = app.customerSearchName.val();
			p.contactNameOperator = app.contactNameOperator.val();
			p.customerSearchContactName = app.customerSearchContactName.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		
		app.datatable = app.tablecustomer.nonpaging("@{Customers.nonPaging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.search.click(function(){
			app.datatable.trigger("reload");
		});

		app.reset.click(function(){
			app.datatable.trigger("fetch", [0, "checked"]);
		});

		app.datatable.bind("select", function(event, prop){
			alert("SELECT "+prop.bean.customerNo+"-"+prop.row[1]+"-"+prop.tr);
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				alert(props.length+"  "+props[x].bean.customerNo+"-"+props[x].row[1]);
			}
		});
		
	}else{
		return new NonPaging(html);
	}
}