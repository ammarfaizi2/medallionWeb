function ModelForm(layout, ui) {
	if (this instanceof ModelForm) {
		var parent = this;
		var elements = [];
		var populate = Populate();
		
		function newAjaxsubmit(vTag, attr, id) {
			console.log('Under Construction New');
		}
		
		function closeDialogAjaxsubmit(vTag, attr, id) {
			vTag.click(function(){
				ui.getHtml().dialog('close');	
			});
		}
		
		function saveAjaxsubmit(vTag, attr, id) {
			vTag.bind('click', function(){
				var messages = populate.validate(elements);
				if (messages.length == 0) {
					var object = populate.populate(elements);
					$.post(attr.ajaxsubmit, {'object':object}, function(data) {
			    		checkRedirect(data);
						$().popupDialog(data.model.title, data.model.messages, "<div class='validation'>", {
							Close: function() {
								$(this).dialog("close");
								if (data.model.forward) {
									if (data.model.forward == 'close') { ui.getHtml().dialog('close');
									}else if (data.forward) { window.location = data.forward; }	
								}								
							}
						});
					});
				}else{
					$().popupDialog("Validation", messages, "<div class='validation'>", {
						Close: function() {
							$(this).dialog("close");
						}
					});
				}
			});
		}
		
		function editAjaxsubmit(vTag, attr, id) {
			console.log('Under Construction Edit');
		}
		
		function updateAjaxsubmit(vTag, attr, id) {
			console.log('Under Construction Update');
		}
		
		function cancelAjaxsubmit(vTag, attr, id) {
			console.log('Under Construction Cancel');
		}
		
		function clearAjaxsubmit(vTag, attr, id) {
			console.log('Under Construction Clear');
		}
		
		function approveAjaxsubmit(vTag, attr, id) {
			console.log('Under Construction Approve');
		}
		
		function comboboxAjaxsrc(vTag, attr, id) {
			var vCombobox = vTag;
			$.get(attr.ajaxsrc, function(data) {
				checkRedirect(data);
				for (x in data) {
					var localval = data[x][0];
					var localDesc = data[x][1];
					$("<option value="+localval+">"+localDesc+"</option>").appendTo(vCombobox);
				}
				vCombobox.combobox();
			});
		}
		
		function radioAjaxsrc(vTag, attr, id) {
			var vRadio = vTag;
			$.get(attr.ajaxsrc, function(data) {
				checkRedirect(data);
				for (x in data) {
					var localval = data[x][0];
					var localDesc = data[x][1];
					$("<input type=radio id="+id+x+" name="+id+" value="+localval+"></input><label for="+id+x+">"+localDesc+"</label>").appendTo(vRadio);
				}
				vRadio.buttonset();
			});
		}
		
		function checkboxAjaxsrc(vTag, attr, id) {
			var vCheckbox = vTag;
			$.get(attr.ajaxsrc, function(data) {
				checkRedirect(data);
				for (x in data) {
					var localval = data[x][0];
					var localDesc = data[x][1];					
					$("<input type=checkbox id="+id+x+" name="+id+" value="+localval+"></input><label for="+id+x+">"+localDesc+"</label>").appendTo(vCheckbox);
				}				
				vCheckbox.buttonset();
			});
		}
		
		function autocompleteAjaxsrc(vTag, attr, id) {
			var cache = {};
			var ajaxsrc = attr.ajaxsrc;
			var vAutocomplete = vTag;
			vAutocomplete.autocomplete({
				minLength: 0,
				source: function(request, response) {
					var term = request.term;
					if (term in cache) {
						response(cache[term]);
						return;
					}
					
					$.getJSON(ajaxsrc, request, function(data, status, xhr) {
						cache[term] = data;
						response(data);
					});
				},
				focus: function(event, ui) {
					vAutocomplete.val(ui.item[0]);
					return false;
				},								
				select: function(event, ui) {
					vAutocomplete.val(ui.item[0]);
					return false;
				}
			})
			.data( "ui-autocomplete" )._renderItem = function(ul, item) {
				return $("<li>").append("<a>" + item[0] + " - " + item[1] + "</a>").appendTo(ul);
			};
			vAutocomplete.focus(function(){
				vAutocomplete.autocomplete("search", vTag.val());
			});
		}
		
		function tableAjaxsrc(vTag, attr, id) {
			var vTable = vTag;
			vTable.dataTable( {
				"bProcessing" : true,
				"bStateSave" : true,
				"bServerSide" : true,
				"bJQueryUI" : true,
				"bScrollCollapse" : true,			
				"sAjaxSource" : attr.ajaxsrc,
				"aoColumns" : attr.colum,
				"sPaginationType" : "full_numbers",
				"sScrollX" : '100%',			
				"aaSorting" : [[1,'asc']],
				"oLanguage" : {
					"sLengthMenu": "Page length: _MENU_",
					"sSearch": "Code :",
					"sZeroRecords": "No matching records found"
				},
				"fnServerData" : function(sSource, aoData, fnCallback ) {
					aoData.push(
						{ "name": "table", "value": "CpComplianceProfile" },
						{ "name": "sql", "value": "complianceProfCode, description, active, status" }
					);
					$.ajax({"dataType": 'json',
						"type": "POST",
						"url": sSource,
						"data": aoData,
						"success": fnCallback,
						"error": function() {
							alert("Fail to fetch data");
						}
						});
				}
			});			
		}
		
		function values(data, field) {
			var value = (data.model) ? $().getValue(data.model, field) : "";
			var id = data.name+"_"+data.type+"_"+data.modelName+"_"+field;
//			alert("values id"+id+" = "+value);
			
			var vTag = $("#"+id);
			var attr = vTag.data("attribute");
			if (attr != null) {
				if (attr.type.indexOf("textfield") >= 0) {
					vTag.attr("value", value);
				}
				if (attr.type.indexOf("textarea") >= 0) {
					vTag.val(value);
				}
				if (attr.type.indexOf("combobox") >= 0) {
					$("option", vTag).each(function(i, op){
						if ($(op).attr("value") == value) { $(op).attr("selected", "selected"); }
					});
				}				
				if (attr.type.indexOf("radio") >= 0) {
					$("input[type=radio]", vTag).each(function(i, op){
						if ($(op).attr("value") == value+"") {
							$(op).attr("checked", true);
							vTag.buttonset("refresh");
						}
					});
				}
				if (attr.type.indexOf("checkbox") >= 0) {
					$("input[type=checkbox]", vTag).each(function(i, op){
						for (y in value) {
							if ($(op).attr("value") == value[y]) {
								$(op).attr("checked", true);
								vTag.buttonset("refresh");
							}
						}
					});
				}
				if (attr.type.indexOf("datepicker") >= 0) {
					value = $().fmtMMDDYYYY(new Date(value));
					vTag.attr("value", value);
				}
				
				if (attr.type.indexOf("autocomplete") >= 0) {
					vTag.attr("value", value);
				}				
			}
		}
		
		function component(data, field) {
//			var value = data.model[field];
			var id = data.name+"_"+data.type+"_"+data.modelName+"_"+field;
			
			var attr = $().loadAttribute(id, eval("(" + data.metadata + ')'));
			if (attr == null) {
				attr = $().loadAttribute(id); //load attribute global
			}
			
			if (attr != null) {
				if (attr.type.indexOf("label") >= 0) {
					vTag = $("<label id="+id+"label_label for="+id+">"+$().formatLabel(field)+"</label>");
					layout.attach(vTag);
				}
				
				if (attr.type.indexOf("textfield") >= 0) {
					vTag = $("<input type=text id="+id+"></input>");
					elements[elements.length] = vTag;
					layout.attach(vTag);
				}
				
				if (attr.type.indexOf("textarea") >= 0) {
					vTag = $("<textarea type=text id="+id+"></textarea>");
					elements[elements.length] = vTag;
					layout.attach(vTag);
				}
				
				if (attr.type.indexOf("combobox") >= 0) {
					vTag = $("<select type=text id="+id+"></select>");
					elements[elements.length] = vTag;
					layout.attach(vTag);
				}
				
				if (attr.type.indexOf("datepicker") >= 0) {
					vTag = $("<input type=text id="+id+"></input>");
					vTag.datepicker();
					elements[elements.length] = vTag;
					layout.attach(vTag);
				}					
	
				if (attr.type.indexOf("radio") >= 0) {
					vTag = $("<div id="+id+">");
					elements[elements.length] = vTag;
					layout.attach(vTag);
				}
				
				if (attr.type.indexOf("checkbox") >= 0) {
					vTag = $("<div id="+id+">");
					elements[elements.length] = vTag;
					layout.attach(vTag);
				}
				
				if (attr.type.indexOf("autocomplete") >= 0) {
					vTag = $("<input type=text id="+id+">");
					elements[elements.length] = vTag;
					layout.attach(vTag);
				}
				
				if (attr.type.indexOf("button") >= 0) {
					vTag = $("<input type=button id="+id+"></input>");
					vTag.button();
					layout.attach(vTag);
				}
				if (attr.type.indexOf("table") >= 0) {
					vTag = $("<table id="+id+"></table>");
					layout.attach(vTag);
				}
				
				vTag.data("attribute", attr);	
				
				if (attr.hasOwnProperty("caption")) vTag.attr("value", attr.caption);
				if (attr.hasOwnProperty("maxLength")) vTag.attr("maxLength", attr.maxLength);
				if (attr.hasOwnProperty("size")) vTag.attr("size", attr.size);
				if (attr.hasOwnProperty("cols")) vTag.attr("cols", attr.cols);
				if (attr.hasOwnProperty("rows")) vTag.attr("rows", attr.rows);
				if (attr.hasOwnProperty("ajaxsrc")) {
					console.log("Ajaxsrc "+attr.ajaxsrc);
					
					if (attr.type.indexOf("combobox") >= 0) { comboboxAjaxsrc(vTag, attr, id); }
					
					if (attr.type.indexOf("radio") >= 0) { radioAjaxsrc(vTag, attr, id); }
	
					if (attr.type.indexOf("checkbox") >= 0) { checkboxAjaxsrc(vTag, attr, id); }
					
					if (attr.type.indexOf("autocomplete") >= 0) { autocompleteAjaxsrc(vTag, attr, id);  }
					
					if (attr.type.indexOf("table") >= 0) {
						console.log(attr.colum);
						tableAjaxsrc(vTag, attr, id); 
					}
				}
				
				if (attr.hasOwnProperty("ajaxsubmit")) {
					if (attr.type.indexOf("button") >= 0) {
						if (field == 'closeDialog') {
							closeDialogAjaxsubmit(vTag, attr, id); 
						}
						if (field == 'new') { newAjaxsubmit(vTag, attr, id); }
						if (field == 'save') { saveAjaxsubmit(vTag, attr, id); }
						if (field == 'edit') { editAjaxsubmit(vTag, attr, id); }
						if (field == 'update') { updateAjaxsubmit(vTag, attr, id); }
						if (field == 'cancel') { cancelAjaxsubmit(vTag, attr, id); }
						if (field == 'clear') { clearAjaxsubmit(vTag, attr, id); }
						if (field == 'approve') { approveAjaxsubmit(vTag, attr, id); }
					}
				}
			}
		}
		
		function disabled(data, field, isModel) {
			var value = (data.model) ? data.model[field] : "";
			var id = data.name+"_"+data.type+"_"+data.modelName+"_"+field;
			
			var attr = $().loadAttribute(id, eval("(" + data.metadata + ')'));
			if (attr == null) {
				attr = $().loadAttribute(id); //load attribute global
			}
			
			if (attr != null) {
				if (isModel && data.type == 'view') attr.readonly = 'readonly';
				
				if (attr.hasOwnProperty("readonly")) {
					if (attr.readonly == 'readonly' || attr.readonly == 'true') {
						vTag.disabled();
					}
				}
			}			
		}
		
		function initilize() {
			var fields = [];
			var data = ui.getModel();
			
			for (var field in data.model) {
				var hasProp = false;
				var newfield = data.model[field];
				for (var childfield in newfield) {
					if (isNaN(parseFloat(childfield))) {
						hasProp = true;
						fields[fields.length] = field+"_"+childfield;
					}else{ break; }
				}
				if (!hasProp) { fields[fields.length] = field; }
			}
			
			if (fields.length > 0) {
				for (x in fields) {
					var field = fields[x];
					component(data, field);
					values(data, field);
					disabled(data, field, true);//tidak di gabung dgn component krn tidak bisa disabled saat initilized
				}
			}
			
			var inject = eval("(" + data.inject + ')');
			for (var field in inject) { 
				component(data, field);
				values(data, field);
				disabled(data, field, false);//tidak di gabung dgn component krn tidak bisa disabled saat initilized
			}
			
			layout.finish(data);
		}
		
		initilize();		
	}else{
		return new ModelForm(layout, ui);
	}
};