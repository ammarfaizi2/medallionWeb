(function(jQuery){
	jQuery.fn.disabled = function(){
		// create new hidden value
		var tmpHidden = "<input type='hidden' id='h_"+$(this).attr("id")+"' name='"+$(this).attr("name")+"'/>";
		//remove name from original input
		$(this).removeAttr( "name" );
		$(this).attr( "disabled", "disabled" );
		
		$(this).parent().append( tmpHidden );
	};
	jQuery.fn.enabled = function(){
		// hidden object
		var hiddenObj_ = $( "#h_"+$(this).attr("id") );
		// get back name and value from hidden field
		
		$(this).attr( "name", hiddenObj_.attr("name") );
		$(this).val( hiddenObj_.val() );
		//remove hidden field
		hiddenObj_.remove();
		$(this).removeAttr( "disabled" );
		
	};
	
})(jQuery);
