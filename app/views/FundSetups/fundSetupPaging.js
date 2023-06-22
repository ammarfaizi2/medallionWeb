function Paging(html) {
	if (this instanceof Paging) {

		var app = html.inject(this, true);
		
		//app.accordion.accordion({collapsible: true});
		this.parameter = function() {
			
		};
		
		app.accordion.accordion({collapsible: false});
		app.datatable = app.tableFundSetup.paging("@{FundSetups.fsPaging()}", this);

		$('#result').css('display', '');
		app.datatable.search.val("");
		app.datatable.fnPageChange("first");
		
		app.datatable.bind("select", function(event, prop){
			if ($.trim(prop.bean.recordStatus) == 'U' ||  $.trim(prop.bean.recordStatus) == 'N'){
				location.href='@{view()}/'+prop.bean.fundKey;
			}else{
				location.href='@{edit()}/'+prop.bean.fundKey;
			}
			
			
		});
		
	}else{
		return new Paging(html);
	}
}