$(document).ready(function(){
	$("input.calendar").datepicker({dateFormat:'dd/mm/yy'});
	$('input.calendar').change( function(){
		var el = $(this);
		var dateVal =  el.val();
		var id = this.id;
		var errorId= "#" + id + "Error";
		el.removeClass("fieldError");
		$(errorId).html("");
		try {
	        $.datepicker.parseDate('dd/mm/yy', dateVal, null);
	    } catch(error) {
	    	el.addClass("fieldError");
	    	$(errorId).html("Invalid date format").show();
	    }
	});
});