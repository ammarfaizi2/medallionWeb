function pagingList(html) {
	if (this instanceof pagingList) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Search Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.customerNoOperator = app.customerNoOperator.val();
			p.customerSearchNo = app.customerSearchNo.val();
			p.customerNameOperator = app.customerNameOperator.val();
			p.customerSearchName = app.customerSearchName.val();
			p.externalNo = app.externalNo.val();
			p.customerSearchExternalNo = app.customerSearchExternalNo.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.customerNoOperator.attr('selectedIndex', 1);
		app.customerNameOperator.attr('selectedIndex', 1);
		app.externalNo.attr('selectedIndex', 1);
		
		app.root.accordion({collapsible: true});
		app.search.add(app.reset).button();
		app.result.hide();
		app.datatable = app.tableinvestor.paging("@{Investors.pagingList()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.search.click(function(){
			app.result.show();
			app.query.val(true);
			if ('${param}'=='dedupe'){
				app.buttonNotInList.show();
			}
			app.datatable.fnPageChange("first");	
		});

		app.reset.click(function(){
			app.datatable.trigger("fetch", [0, "checked"]);
		});

		app.datatable.bind("select", function(event, prop){
			if ('${param}'=='dedupe'){
				location.href='@{view()}?id='+prop.bean.customerKey+'&param=${param}';
			}
			if ('${param}'=='register-invt-acct'){
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
				location.href='@{view()}?id='+prop.bean.customerKey+'&param=${param}';
			}
			
			if ('${param}' == 'register-cust-acct'){
				location.href='@{view()}?id='+prop.bean.customerKey+'&param=${param}';
			}
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				
			}
		});
		
	}else{
		return new pagingList(html);
	}
}