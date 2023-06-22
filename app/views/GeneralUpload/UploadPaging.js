function UploadPaging(html) {
	if (this instanceof UploadPaging) {
		/* =========================================================================== 
		 * Variable
		 * ========================================================================= */
		var app = html.inject(this, true);
		/* =========================================================================== 
		 * Table Search Parameter (penamaan harus fix yaitu parameter)
		 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			return p;
		};
		app.datatable = app.tableProfile.paging("@{profilePaging()}", this);
		app.datatable.bind("select", function(event, prop){
			console.log( prop.bean );
			if( prop.bean.systemField ){
				return false;
			}else{
				location.href='@{edit()}/'+prop.bean.id;
			}
		});
		
		app.newdata.click(function(){
			location.href='@{entry()}';
		});
		
	}else{
		return new UploadPaging(html);
	}
}
