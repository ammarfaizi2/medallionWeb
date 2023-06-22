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
			p.actionCode = $('#actionCodeKey').val();
			p.securityType = $('#securityTypeId').val();
			p.securityCode = $('#securityCodeId').val();
			p.announcementNoOperator = $('#announcementNoOperator').val();
			p.announcementNo = $('#announcementSearchNo').val();
			p.query = $('#query').val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		$('#announcementNoOperator').children().eq(0).remove();
		//app.search.add(app.reset).button();
		$('#result').hide();
		app.datatable = app.tableannouncement.paging("@{CorporateActionAdjustment.paging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		$('#search').click(function(){
			
			/*if(app.actionCodeKey.val() == "" || app.securityTypeId.val() == "" || app.securityCodeId.val() == "" || 
				app.announecementDateFrom.hasClass('fieldError') || app.announecementDateTo.hasClass('fieldError')){*/
			if($('#announcementSearchNo').val() == ""){
				
				if($('#announcementSearchNo').val() == ""){
					$('#announcementSearchNoError').html("required");
				}else{
					$('#announcementSearchNoError').html("");
				}
				
				return false;
			}else{
				$('#announcementSearchNoError').html("");
				$('#result').show();
				$('#query').val(true);
				app.datatable.fnPageChange("first");
			}	
		});

		$('#reset').click(function(){
			location.href="@{list()}";	
		});

		app.datatable.bind("select", function(event, prop){
			console.log('prop.bean.recordStatus = '+prop.bean.recordStatus);
			if ((jQuery.trim(prop.bean.recordStatus) == "N")|| (jQuery.trim(prop.bean.recordStatus) == "U")){
				location.href='@{view()}/?id=' + prop.bean.adjustmentKey + '&annId=' + prop.bean.corporateAnnouncementKey + '&entId=' + prop.bean.entitlementKey + '&entdId=' + prop.bean.entitlementDetailKey;
			} else {
				if (prop.bean.adjustmentKey == null || prop.bean.adjustmentKey == 'undefined'){
					location.href='@{entry()}/?annId=' + prop.bean.corporateAnnouncementKey + '&entId=' + prop.bean.entitlementKey + '&entdId=' + prop.bean.entitlementDetailKey;
				}else{
					location.href='@{edit()}/?id=' + prop.bean.adjustmentKey + '&annId=' + prop.bean.corporateAnnouncementKey + '&entId=' + prop.bean.entitlementKey + '&entdId=' + prop.bean.entitlementDetailKey;
				}
			}
			
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				
			}
		});
		
	}else{
		return new Paging(html);
	}
}