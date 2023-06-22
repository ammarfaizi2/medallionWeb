$(function(){
	$('#root').accordion({
		collapsible: true
	});	
});

function doEdit(data) {
	if ((data[3] == "New") || (data[3] == "Updated")) {
		view(data[0]);
		return false;
	} else {
		return true;
	}
}

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
			var p = new Object();
			return p;
		};
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});

		app.datatable = app.tableProductSetup.paging("@{RegistryProduct.paging()}", this);
		
		$('#newDataProductSetup').button();

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.datatable.bind("select", function(event, prop) {
			var productCode = prop.bean.productCode.replace(/#/g, '${specialCharPagar}').toString();

			if(prop.bean.recordStatus == "A" || prop.bean.recordStatus == "R")
			{
				location.href='@{edit()}/' + productCode;
			}
			else
			{
				location.href='@{view()}/' + productCode;
			}
		});

		$('#newDataProductSetup').click(function(){
			location.href="@{entry()}/?isNewRec=true";
		});
	}else{
		return new Paging(html);
	}
}