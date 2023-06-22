$(function(){
	$("input.numberOnly").keypress(function (e) { 
		if ( e.which!=8 && e.which!=0 && (e.which<48 || e.which>57)) {
		    return false;
		}	
	});
	
	$("input.numberOnly").bind('paste', function(e) {
		e.preventDefault();
	});
	
	$("input.float").keypress(function (e) {
		var el = $(this);
		if ((el.val().length + 1) == 1) {
			if ( e.which!=8 && e.which!=0 && (e.which<48 || e.which>57)) {
			    return false;
			}	
		} else {
			if ((e.which != 46 || el.val().indexOf('.') != -1) && (e.which < 48 || e.which > 57) &&  e.which!=8 && e.which!=0) {
				return false;
			}
		}
	});
	
});