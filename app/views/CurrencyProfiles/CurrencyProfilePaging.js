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
		
		app.datatable = app.tableCurrencyProfile.paging("@{CurrencyProfiles.paging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		app.datatable.bind("select", function(event, prop){
			if ((jQuery.trim(prop.bean.recordStatus) == "N")|| (jQuery.trim(prop.bean.recordStatus) == "U")||(jQuery.trim(prop.bean.recordStatus) == '${recordStatusNew}')|| (jQuery.trim(prop.bean.recordStatus) == '${recordStatusUpdate}')){
				location.href='@{view()}/?id=' + prop.bean.currencyProfileKey;
			}else{
				location.href='@{edit()}/?id=' + prop.bean.currencyProfileKey;
			}
		});
		
		app.newData.button().click(function(){
			location.href='@{entry()}';
		});
	}else{
		return new Paging(html);
	}
}