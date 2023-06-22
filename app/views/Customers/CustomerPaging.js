function Paging(html) {
	if (this instanceof Paging) {

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
			p.contactNoOperator = app.contactNoOperator.val();
			p.customerSearchContactName = app.customerSearchContactName.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.customerNoOperator.children().eq(0).remove();
		app.customerNoOperator.attr('selectedIndex', 1);
		app.customerNameOperator.children().eq(0).remove();
		app.customerNameOperator.attr('selectedIndex', 1);
		app.contactNoOperator.children().eq(0).remove();
		app.contactNoOperator.attr('selectedIndex', 1);
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		if ('${param}' == 'dedupe')
			app.newDataCust.button();
		
		
		app.datatable = app.tablecustomer.paging("@{Customers.customerPaging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		//app.datatable.search.val("");
		app.search.click(function(){
			$('#result').css('display', '');
			app.datatable.search.val("");
			app.query.val(true);
			app.datatable.fnPageChange("first");
			return false;
		});

		app.reset.click(function(){
			//app.datatable.trigger("fetch", [0, "checked"]);
			if ('${param}' == 'dedupe') {
				location.href="@{Customers.dedupe()}";
			} else {
				location.href="@{Customers.list()}?mode=${mode}#{if param}&param=${param}#{/if}";
			}
		});

		if ('${param}' == 'dedupe') {
			if (!app.notInList.is(':checked')){
				app.newDataCust.hide();
			}
			
			app.notInList.change(function() {
				if ($(this).is(':checked')) 
			    	app.newDataCust.show();
				else 
					app.newDataCust.hide();
			});
			
			app.newDataCust.click(function() {
				location.href="@{Customers.entry()}";
			});
		}
		
		app.datatable.bind("select", function(event, prop){
			if ('${param}'=='dedupe'){
				location.href='@{view()}?id='+prop.bean.customerKey+'&param=${param}';
			}
			
			if ('${param}'=='edit'){
				if (($.trim(prop.bean.recordStatus) == 'N')||($.trim(prop.bean.recordStatus) == 'U')){
					location.href='@{view()}?id='+prop.bean.customerKey+'&param=${param}';
				} else {
					location.href='@{edit()}?id='+prop.bean.customerKey+'&param=${param}';
				}
			}
			
			if ('${param}'=='view'){
				location.href='@{view()}?id='+prop.bean.customerKey+'&param=${param}';
			}
			
			if ('${param}' == 'register-bank-acct'){
				location.href='@{Customers.view()}?id='+prop.bean.customerKey+'&param=${param}';
			}
			
			if ('${param}' == 'register-cust-acct'){
				location.href='@{view()}?id='+prop.bean.customerKey+'&param=${param}';
			}
			
			//alert(prop.bean.customerNo+"-"+prop.row[1]+"-"+prop.tr);
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				//alert(props.length+"  "+props[x].bean.customerNo+"-"+props[x].row[1]);
			}
		});
		
		if($("#customerSearchNo").val()==null||$("#customerSearchNo").val()==""){
			$("#search").click();
		}	
		
	}else{
		return new Paging(html);
	}
}