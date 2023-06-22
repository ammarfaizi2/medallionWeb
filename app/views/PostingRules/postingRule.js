function Paging(html) {
	if (this instanceof Paging) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter
 * )
 * ========================================================================= */
		this.parameter = function() {
			
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
//		app.root.accordion({collapsible: true});
		
//		app.search.add(app.reset).button();
		app.datatable = app.tablePostingRule.paging("@{PostingRules.paging()}/?id=${profileId}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		app.datatable.bind("select", function(event, prop){
			if ((jQuery.trim(prop.bean.recordStatus) == '${recordStatusNew}')|| (jQuery.trim(prop.bean.recordStatus) == '${recordStatusUpdate}') || (jQuery.trim(prop.bean.recordStatus) == "N")|| (jQuery.trim(prop.bean.recordStatus) == "U")){
				location.href='@{view()}/?id=' + prop.bean.postingRuleKey;
			}else{
				location.href='@{edit()}/?id=' + prop.bean.postingRuleKey;
			}
		});
		
		app.newData.button();
		app.backPosRul.button();
		
		app.newData.click(function(){
			location.href='@{entry()}?profileId=${profileId}';
		});
		
	}else{
		return new Paging(html);
	}
}