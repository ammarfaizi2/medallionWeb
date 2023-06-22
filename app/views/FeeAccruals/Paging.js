function Paging(html) {
	if (this instanceof Paging) {

		var app = html.inject(this, true);
		
		this.parameter = function() {
			
		};

		app.newFeeAccrual.button();
		app.datatable = app.tableFaFeeAccrual.paging("@{FeeAccruals.paging()}", this);
//
//		$('#result').css('display', '');
//		app.datatable.search.val("");
//		app.datatable.fnPageChange("first");
//		
		app.datatable.bind("select", function(event, prop){
			if ($.trim(prop.bean.recordStatus) == 'U' ||  $.trim(prop.bean.recordStatus) == 'N'){
				location.href='@{view()}/'+prop.bean.feeKey;
			}else{
				location.href='@{edit()}/'+prop.bean.feeKey;
			}
		});
		
		
		app.newFeeAccrual.click(function(){
			location.href="@{entry()}";
		});
	}else{
		return new Paging(html);
	}
}