$(function() {	
	$('#tabs').tabs();
	$('.buttons input:button').button();
	$('.calendar').datepicker();
	
	$(window).bind('beforeunload', function(e) {
		alert("unload");
	});
});
