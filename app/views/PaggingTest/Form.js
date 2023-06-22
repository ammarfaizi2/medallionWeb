function Form(tag, cb) {
	if (this instanceof Form) {

		var parent = this;
		
		$.ajaxSetup({ async : false });
		
		$.get(tag.attr("ajax"), function(data) {
			checkRedirect(data);
			var jactions = data.elements;
			var entries = data.entry;
			
			for (x in jactions) {
				var jaction = jactions[x];
				
				if (jaction.type == 'lookup') {
					var vDiv = $("<div id="+jaction.id+" title="+jaction.label+"/>").appendTo(tag);
					var vTable = $("<table id="+jaction.id+"table/>").appendTo(vDiv);
					Lookup(vDiv, vTable, jaction, cb);
				}
				
				if (jaction.type == 'table') {
					var vDiv = $("<div>").appendTo(tag);
					var vTable = $("<table id="+jaction.id+"/>").appendTo(vDiv);
					Table(vTable, jaction, cb);
				}
				
				if (jaction.type == 'input') {
					var vDiv = $("<div>").appendTo(tag);
					if (jaction.label) { $("<label for="+jaction.id+">"+jaction.label+"</label>").appendTo(vDiv);  }
					vInput = $("<input type=text id="+jaction.id+"></input>").appendTo(vDiv);
					Input(vInput, jaction, cb);
					
					if (entries[jaction.id]) vInput.val(entries[jaction.id]);
				}
				
				if (jaction.type == 'datepicker') {
					var vDiv = $("<div>").appendTo(tag);
					if (jaction.label) { $("<label for="+jaction.id+">"+jaction.label+"</label>").appendTo(vDiv);  }
					vDatepicker = $("<input type=text id="+jaction.id+"></input>").appendTo(vDiv);
					Datepicker(vDatepicker, jaction, cb);
					
					if (entries[jaction.id]) vDatepicker.val(entries[jaction.id]);
				}
				
				if (jaction.type == 'autocomplete') {
					var vDiv = $("<div>").appendTo(tag);
					if (jaction.label) { $("<label for="+jaction.id+">"+jaction.label+"</label>").appendTo(vDiv);  }
					vAutocomplete = $("<input type=text id="+jaction.id+"></input>").appendTo(vDiv);
					Autocomplete(vAutocomplete, jaction, cb);
					
					if (entries[jaction.id]) vAutocomplete.val(entries[jaction.id]);
				}
				
				if (jaction.type == 'inputlookup') {
					var vDiv = $("<div>").appendTo(tag);
					if (jaction.label) { $("<label for="+jaction.id+">"+jaction.label+"</label>").appendTo(vDiv);  }
					var vInput = $("<input type=text id="+jaction.id+">").appendTo(vDiv);
					var vButton = $("<button>?</button>").appendTo(vDiv);
					var vInputDesc = $("<input type=text id="+jaction.id+"desc>").appendTo(vDiv);
					InputLookup(vInput, vInputDesc, jaction, cb);
					
					if (entries[jaction.id]) vInput.val(entries[jaction.id]);
					if (entries[jaction.id+"Desc"]) vInputDesc.val(entries[jaction.id+"Desc"]);
					
					vButton.data("popup", jaction.popup);
					vButton.click(function(){
						$("#"+$(this).data("popup"), tag).dialog('open');
					});
				}
				
				if (jaction.type == 'textarea') {
					var vDiv = $("<div>").appendTo(tag);
					if (jaction.label) { $("<label for="+jaction.id+">"+jaction.label+"</label>").appendTo(vDiv);  }
					var vTextarea = $("<textarea id="+jaction.id+"/>").appendTo(vDiv);
					Textarea(vTextarea, jaction, cb);

					if (entries[jaction.id]) vTextarea.val(entries[jaction.id]);
				}
				
				if (jaction.type == 'select') {
					var vDiv = $("<div class=ui-widget>").appendTo(tag);
					if (jaction.label) { $("<label for="+jaction.id+">"+jaction.label+"</label>").appendTo(vDiv);  }
					var vSelect = $("<select id="+jaction.id+"/>").appendTo(vDiv);
					Select(vSelect, jaction, cb);
					
					for (x in jaction.colum) {
						var option = jaction.colum[x];
						vSelect.append("<option value="+option.sName+">"+option.sTitle+"</option>");
					}					
					if (entries[jaction.id]) $("option[value="+entries[jaction.id]+"]", vSelect).attr("selected", "selected");
				}
				
				if (jaction.type == 'radio') {
					var vDiv = $("<div>").appendTo(tag);
					if (jaction.label) { $("<label for="+jaction.id+">"+jaction.label+"</label>").appendTo(vDiv);  }
					
					for (x in jaction.colum) {
						var option = jaction.colum[x];
						vDiv.append("<input type=radio id="+(jaction.id+x)+" name="+jaction.id+" value="+option.sName+"></input><label for="+(jaction.id+x)+">"+option.sTitle+"</label>");
					}
					if (entries[jaction.id]) $("input[value="+entries[jaction.id]+"]", vDiv).attr("checked", "checked");
					Radio(vDiv, $("input[type=radio]", vDiv), jaction, cb);
				}
				
				if (jaction.type == 'checkbox') {
					var vDiv = $("<div>").appendTo(tag);
					if (jaction.label) { $("<label for="+jaction.id+">"+jaction.label+"</label>").appendTo(vDiv);  }

					for (x in jaction.colum) {
						var option = jaction.colum[x];
						vDiv.append("<input type=checkbox id="+(jaction.id+x)+" name="+jaction.id+" value="+option.sName+"></input><label for="+(jaction.id+x)+">"+option.sTitle+"</label>");
					}
					
					if (entries[jaction.id]) {
						var values = entries[jaction.id];
						for (x in values) {
							$("input[value="+values[x]+"]", vDiv).attr("checked", "checked");
						}
					}
					
					Checkbox(vDiv, $("input[type=checkbox]", vDiv), jaction, cb);
				}
				
				if (jaction.type == 'button') {
					var vDiv = $("<div>").appendTo(tag);
					var vButton = $("<button id="+jaction.id+">"+jaction.label+"</button>").appendTo(vDiv);
					Button(vButton, jaction, cb);
				}
			}
		});
	}else{
		return new Form(tag, cb);
	}
};