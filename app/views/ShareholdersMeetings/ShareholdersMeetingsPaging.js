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
			p.meetingDateFrom = app.meetingDateFrom.val();
			p.meetingDateTo = app.meetingDateTo.val();
			p.searchThirdPartyKey = app.issuerKey.val();
			p.MeetingNoOperator = app.MeetingNoOperator.val();
			p.meetingSearchNoOperator = app.meetingSearchNoOperator.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
//		app.customerNoOperator.children().eq(0).remove();
//		app.customerNameOperator.children().eq(0).remove();
//		app.contactNameOperator.children().eq(0).remove();
		app.accordion.accordion({collapsible: true});
		
		app.datatable = app.metingCancel.paging("@{ShareholdersMeetings.meetingPaging()}", this);

		app.meetingDateFrom.change(function() {
	        var dateFrom = $(this).datepicker('getDate');
	        var dateTo = $('#meetingDateTo').datepicker('getDate');
	        var origin = 'from';
	        var id = '#meetingDate';
	        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#meetingDateTo').val()!='')) {
	            //compareDateFromTo(dateFrom, dateTo, origin, id);
	            compareDateFromToEqual(dateFrom, dateTo, origin, id);
	        }
	        
	        var checkError = $("input").hasClass('fieldError');
			if (checkError){
				$('#search').button('disable');
			}  else {
				$('#search').button('enable');
			}
	    });
	    
	    app.meetingDateTo.change(function(){
	        var dateFrom = $('#meetingDateFrom').datepicker('getDate');
	        var dateTo = $(this).datepicker('getDate');
	        var origin = 'to';
	        var id = '#meetingDate';
	        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#meetingDateFrom').val()!='')) {
	            //compareDateFromTo(dateFrom, dateTo, origin, id);
	            compareDateFromToEqual(dateFrom, dateTo, origin, id);
	        }
	        
	        var checkError = $("input").hasClass('fieldError');
			if (checkError){
				$('#search').button('disable');
			}  else {
				$('#search').button('enable');
			}
	    });
		
		$('#search').click(function() {
//			var resultValidate = validateSearch();
//			if (!resultValidate) {
//				return;
//			}
			
			if($('#issuerCode').val() == '')
			{
				$('#issuerKey').val('');
			}
			
			app.meetingDateFromError.html("");
			app.meetingDateToError.html("");
			
			if( app.meetingDateFrom.val() == "" || app.meetingDateTo.val() == "" )
			{
				if( app.meetingDateFrom.val() == "" )
				{
					app.meetingDateFromError.html("Required");
				}
				if( app.meetingDateTo.val() == "" )
				{
					app.meetingDateToError.html("Required");
				}	
			}
			else
			{
				app.query.val(true);
				$('#result').css('display', '');
				app.datatable.search.val("");
				app.datatable.fnPageChange("first");
			}
		});
		
		app.datatable.bind("select", function(event, prop){
			if ((jQuery.trim(prop.bean.recordStatus) == "N")|| (jQuery.trim(prop.bean.recordStatus) == "U")||(jQuery.trim(prop.bean.recordStatus) == '${recordStatusNew}')|| (jQuery.trim(prop.bean.recordStatus) == '${recordStatusUpdate}')){
				location.href='@{view()}/?id=' + prop.bean.meetingKey+'&group=${group}';
			}else{
				location.href='@{edit()}/?id=' + prop.bean.meetingKey+'&group=${group}';
			}
//			
		});
	}else{
		return new Paging(html);
	}
}