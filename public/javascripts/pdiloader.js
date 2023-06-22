;(function($) {
	jQuery.fn.pdiloader = function(options){
		var html = $( this );
		var myTitle, contentId;
		var submitButton;
		var myContent;
		
		var urlLoadOutput = options.urlOutput;
		/*=================================*/
		/*
		 * METHOD
		 */
		/*=================================*/
		
		/*=================================*/
		/*
		 * EVENT LISTENER
		 */
		/*=================================*/
		
		$( ".accordion form", html ).each(function(idx, form){
			// prepare Options Object 
			var options = {
			    url:        urlLoadOutput, 
			    beforeSubmit : function(arr, form, options){
			    	submitButton = $("input[type=submit]", form);
			    	myTitle = submitButton.val();
					submitButton.val("Executing now....");
					contentId = submitButton.attr( "contentid" );					
					myContent = $("#"+contentId, html);
					myContent.fadeOut("slow");
					myContent.html("");
			    },
			    success: function(data, status, jxhr, form){
					submitButton.val( myTitle );
					
					var newContent = 
					 '<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>'+data+'</p>';
					myContent.html( newContent );
					myContent.removeClass("ui-state-error");
					
					myContent.addClass("ui-state-highlight");
					
					myContent.fadeIn("slow");
					submitButton.val(myTitle);
					  
				  },
				  error: function(jxhr, status){
					  submitButton.val(myTitle);
					  var newContent = 
						  '<p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;">'+
						  '</span>Error executing file.</p>';
					  myContent.html( newContent );
					  
					  myContent.addClass("ui-state-error");
					  
					  myContent.removeClass("ui-state-highlight");
					  
					  myContent.fadeIn("slow");
				  }
			}; 
			 
			// validate
			$( form ).validate();
			// pass options to ajaxForm 
			$( form ).ajaxForm(options);
		});		
		
		$(".accordion", html).accordion({
			collapsible: true ,
			autoHeight: false,
			header: "h3"
		});
		$(".accordion_inner", html).accordion({
			collapsible: true ,
			autoHeight: false,
			header: "h4"
		});
		
		$(".forLookupGroup", html).click(function(){
			var innerHTML = $( this );
			dropDownId = innerHTML.attr("id");
			splitId = dropDownId.split("_");
			var lookupId = dropDownId.substr( dropDownId.indexOf( splitId[1] ) );
			if( innerHTML.find("option").length == 1 ){
				var result = $( "#resulttmp", html ).html();
				innerHTML.parent().append( result );
				innerHTML.parent().find("span.loading").show();
				$.ajax({
					  type: 'GET',
					  url: "lookup/"+lookupId,
					  success: function(data){
						  console.log("sukses bro "+data.length);
						  for(var i = 0; i < data.length; i++){
							  member = data[i];
							  innerHTML.append( "<option value="+member.value+">"+member.text+"</option>" );
						  }
						  innerHTML.parent().find("span.loading").hide().remove();
					  }
				});
			}
		});
		
		// add date picker to date field
		$(".calendar", html).datepicker();		
		
	};
	
})(jQuery);