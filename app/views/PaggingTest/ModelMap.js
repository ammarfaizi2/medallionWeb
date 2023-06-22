// default to labeledtextfield, readonly true, 
// if (type = form) readonly false 

(function(jQuery){
	var metadata;
	
	jQuery.fn.visible = function() {
	    return this.css('visibility', 'visible');
	};

	jQuery.fn.invisible = function() {
	    return this.css('visibility', 'hidden');
	};
	
	jQuery.fn.getValue = function(data, field) {
		var arr = field.split("_");
		if (arr.length == 1) return data[field]; 
		if (arr.length == 2) {
			var obj = data[arr[0]];
			return obj[arr[1]];
		}
		if (arr.length == 3) {
			var obj = data[arr[0]];
			var obj1 = obj[arr[1]];
			return obj1[arr[2]];
		}
	};
	
	jQuery.fn.formatLabel = function(key) {
		var arr = key.split('_');
		key = arr[arr.length-1];
		var chars = key.split('');
		var formated = "";
		for (x in chars) {
			var ori = chars[x];
			var upper = chars[x].toUpperCase();;
			if (formated == "") {
				formated = upper;
			}else {
				if (ori == upper) { formated += " "+upper;
				}else{ formated += ori; } 
			} 
		}
		return formated;
	};
	
	jQuery.fn.loadAttribute = function(key, overwrite) {
		var arr = key.split("_");
		for (i = 0; i < arr.length; i++) {
			var line = "";
			for (j = i; j < arr.length; j++) {
				if (line == "") line = arr[j];
				else line += "_"+arr[j];
			}

			if (overwrite == null) {
				var attr = $.metadata()[line];
				if (attr != null) return attr;
			}else{
				var attr = overwrite[line];
				if (attr != null) return attr;
			}
		}
	};
	
	jQuery.fn.popupDialog = function(title, content, htmltag, pbuttons, width, height, modal) {
		var vDialog = $("<div title="+title+">");
		for (x in content) {
			$(htmltag).appendTo(vDialog).html("* "+content[x]);
		}
		if (width == null) width = 350;
		if (height == null) height = 250;
		if (modal == null) modal = true;
		var vDialog = vDialog.dialog({
			width : width,
			height: height,
			modal: modal,
			buttons: pbuttons
		});
		return vDialog;
	};

	jQuery.metadata = function(){    	
    	$.ajaxSetup({ async : false });
    
    	if (metadata == null) {
	    	$.get("@{Metadata.metadata()}", function(data) {
				checkRedirect(data);
	    		console.log('fetching data metadata');
	    		metadata = data;
	    	});
    	}
    	
    	return metadata; 
    };
})(jQuery);
