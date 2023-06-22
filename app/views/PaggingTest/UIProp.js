function UIProp(tag) {
	if (this instanceof UIProp) {
		var ajax = tag.attr("ajax");
		var layout = tag.attr("layout");
		
		var ajaxData;
		var layoutData;
		
		$.ajaxSetup({async : false});
		
		this.getHtml = function() { return tag; };
		
		this.getModel = function() { 
			if (ajaxData == null) {
				$.get(ajax, function(data) { 
					checkRedirect(data);
					ajaxData = data;
				});
			}
			return ajaxData;
		};
		
		this.getLayout = function() {
			if (layoutData == null) {
				$.get(layout, function(data) {
					checkRedirect(data);
					layoutData = data;
				});
			}
			return layoutData; 
		};
	}else{
		return new UIProp(tag);
	}
};