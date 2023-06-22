function Paging(html) {
	if (this instanceof Paging) {

		function view(id) {
			#{if subList}
				location.href='@{list()}/' + id; 
			#{/if}
			#{else}
				#{if isPartial}
					location.href='@{viewpartial()}/' +id + "#{if group}?group=${group}#{/}#{if param}${group?'&':'?'}param=${param}#{/}"; 
				#{/if}
				#{else}
					location.href='@{view()}/' +id + "#{if group}?group=${group}#{/}#{if param}${group?'&':'?'}param=${param}#{/}";
				#{/else}
			#{/else}
		}

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
		
		app.datatable = app.meting.paging("@{ShareholdersMeetingInquiries.inquiryPaging()}", this);

		app.datatable.bind("select", function(event, prop){
			view(prop.bean.meetingKey);
			//location.href='@{edit()}?id='+prop.bean.corporateAnnouncementKey;
			//alert(prop.bean.customerNo+"-"+prop.row[1]+"-"+prop.tr);
		});
		
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
		
////		var search = function() {
////			var resultValidate = validateSearch();
////			if (!resultValidate) {
////				return;
////			}
////			
////			$('#result').css('display', '');
////			app.datatable.search.val("");
////			app.datatable.fnPageChange("first");
////			
////			//$('#result .loading').show();
////			//$('#result .response').hide();
////			//$('#result').show();
////			//$.get('@{searchInquiry()}?param=${param}&group=${group}', $('#searchInquiryForm').serialize(), function(data) {
////			//	$('#result .response').html(data);
////			//	$('#result .loading').hide();
////			//	$('#result .response').show();
////			//	setupTable();
////			//});
//		};
		

		
	}else{
		return new Paging(html);
	}
}