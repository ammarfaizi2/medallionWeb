$(function(){
	
	$('.buttons input:button').button();
	
	if ($.browser.msie){
		$("#remarksCancel[maxlength]").bind('input propertychange', function() {  
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }
	    });
	}
	
	$('#cancelDate').change(function(){
		var dateFrom = $('#entitlementDate').datepicker('getDate');
		var dateTo = $(this).datepicker('getDate');
		var origin = 'to';
		var id = '#cancelDate';
		if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#entitlementDate').val()!=''))
			compareDateCancel(dateFrom, dateTo, origin, id);
	});
	
});

function compareDateCancel(dateFrom, dateTo, origin, id){
	var dateA = new Date(dateFrom);
	var dateB = new Date(dateTo);
	if (dateB.getTime() < dateA.getTime()) {
		$(id).addClass('fieldError');
		$(id+'ToError').html('Cancel Date must be greather than Entitlement Date');
	} else {
		$(id).removeClass('fieldError');
		$(id+'ToError').html('');
	}
}
