function Layout(ui) {
	if (this instanceof Layout) {
		
		this.attach = function(component) {
			var id = component.attr("id");
			console.log("Attaching for "+id);
			
			vDiv = $("div [field='"+id+"']", ui.getHtml());
			vDiv.addClass(id.split('_').join(' '));
			vDiv.append(component);
		};
		
		this.finish = function(data){
			$(".layout").layout();
			if (data.typeView == 'page') { ui.getHtml().visible(); }
			if (data.typeView == 'dialog') {
				ui.getHtml().visible();
				ui.getHtml().dialog({autoOpen: false}); 
			}
		};
		
		function doLayout(tag, data, model) {
			if (data == 'fillerp') {
				var div = $("<p class=fillerp>").appendTo(tag);
			}else{
				var div = $("<div>").appendTo(tag);
				div.attr("id", data.id);
				div.attr("class", data.class);
				
				if (data.hgap){ } else { data.hgap = 0; }
				if (data.vgap){ } else { data.vgap = 0; }
				if (data.columns){ } else { data.columns = 1; }
				
			    div.attr("data-layout",'{"type": "'+data.type+'", "hgap": '+data.hgap+', "vgap": '+data.vgap+', "columns": '+data.columns+'}');
				if (data.elements) {
					for (x in data.elements) {
						var field = data.elements[x];
						if (field.class && data.class == 'layout') {
							doLayout(div, field, model);
						}else{
							var cdiv = $("<div>").appendTo(div);
							if (field == 'fillere') {
								cdiv.attr("class", "fillere");
							}else{
								var id = model.name+"_"+model.type+"_"+model.modelName+"_"+field;
								if (field.indexOf("label") >= 0) { cdiv.attr("field", id+"_label");
								}else{ cdiv.attr("field", id); }
							}
						}
					}
				}
			}
		}
		
		function initilize() {
			var tag = ui.getHtml();
			var data = ui.getLayout();
			var model = ui.getModel();
			
			tag.invisible();
			for (x in data) {
				doLayout(tag, data[x], model);
			}
		}
		
		initilize();
	}else{
		return new Layout(ui);
	}
};