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
			p.amlKeyOperator = app.amlKeyOperator.val();
			p.amlSearchKey = app.amlSearchKey.val();
			p.customerNameOperator = app.customerNameOperator.val();
			p.customerSearchName = app.customerSearchName.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.amlKeyOperator.children().eq(0).remove();
		app.amlKeyOperator.attr('selectedIndex', 1);
		app.customerNameOperator.children().eq(0).remove();
		app.customerNameOperator.attr('selectedIndex', 1);
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		if ('${param}' == 'dedupe')
			app.newDataCust.button();
		
		
		app.datatable = app.tableAmlCustomer.paging("@{AmlMaintenance.amlCustomerPaging()}", this);

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
				location.href="@{AmlMaintenance.dedupe()}";
			} else {
				location.href="@{AmlMaintenance.list()}?mode=${mode}#{if param}&param=${param}#{/if}";
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
				location.href="@{AmlMaintenance.entry()}";
			});
		}
		
		app.datatable.bind("select", function(event, prop){
			if ('${param}'=='dedupe'){
				location.href='@{edit()}?id='+prop.bean.amlKey+'&param=entry&mode=edit';
			}
			
			if ('${param}'=='edit'){
				if (($.trim(prop.bean.recordStatus) == 'N')||($.trim(prop.bean.recordStatus) == 'U')||($.trim(prop.bean.recordStatus) == 'I')){
					location.href='@{view()}?id='+prop.bean.amlKey+'&param=${param}';
				} else {
					location.href='@{edit()}?id='+prop.bean.amlKey+'&param=${param}';
				}
			}
			
			if ('${param}'=='view'){
				location.href='@{view()}?id='+prop.bean.amlKey+'&param=${param}';
			}
			
			//alert(prop.bean.customerNo+"-"+prop.row[1]+"-"+prop.tr);
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				//alert(props.length+"  "+props[x].bean.customerNo+"-"+props[x].row[1]);
			}
		});
		
		if($("#amlSearchKey").val()==null||$("#amlSearchKey").val()==""){
			$("#search").click();
		}	
		
	}else{
		return new Paging(html);
	}
}