(function($){
	/**
	 * 
	 * Options:
	 * dataSource : for data
	 * onComplete : callback to execute when generating parameter is done
	 * onSelectChange : callback to execute when one of the select is change
	 * onGenerateReport : callback for generate button
	 * 
	 */
	jQuery.fn.generateParameterForm = function(options){
		var callCompleteAction;
		var selectOnChangeAction;
		var generateReportAction;
		
		var json = {};
		var htmlForm = this;
		var formId = htmlForm.attr( "id" );
		
		if( options.dataSource ){
			json = options.dataSource;
		}
		if( options.onComplete ){
			callCompleteAction = options.onComplete;
		}
		if( options.onSelectChange ){
			selectOnChangeAction = function(){
				options.onSelectChange( htmlForm );
			};
		}
		if( options.onGenerateReport ){
			generateReportAction = function(aCallback){
				options.onGenerateReport( htmlForm, aCallback);
			}
		}
		/*var outputType = [ { "key":"PDF", "value":"PDF" }, { "key" : "HTML", "value" : "HTML" }, 
		                   { "key" : "DOC", "value" : "DOC" }, {"key" : "XLS", "value" : "XLS"}, 
		                   {"key" : "XLSX", "value" : "XLSX"}, {"key" : "CSV", "value" : "CSV" }, 
		                   { "key" : "RTF", "value" : "RTF"}, { "key" : "TXT", "value" : "TXT"} ];*/
		var outputType = [ { "key":"PDF", "value":"PDF" }, {"key" : "XLSX", "value" : "XLSX"}, {"key" : "XLS", "value" : "XLS"}, {"key" : "CSV", "value" : "CSV" } ];
		var tmpFormId = formId.substring( 4 );
		var elementContent = htmlForm.find( "#content"+tmpFormId );
		elementContent.find( ".ui-corner-all" ).hide();
		elementContent.html( "" );
		elementContent.find( ".ui-corner-all" ).html("");
		if( jQuery.isArray( json ) ){ // make sure it is an array
			for( var i = 0; i < json.length; i++ ){
				var currentItem = json[ i ];			
				var keyParam = currentItem.key;
				var valueType = currentItem.value.valueType;
				var tmpSplit = valueType.split( "." );
				valueType = tmpSplit[ tmpSplit.length-1 ];
				
				var paramField = "reportParam["+i+"].field";
				var paramType = "reportParam["+i+"].type";;
				var paramValue = "reportParam["+i+"].value";
				
				// generate random id
				var idRandom = "";
				var currentParam = currentItem.value;
				var renderType = currentParam["renderType"];
				var labelName = currentParam["label"];
				var hiddenField = currentParam["isHidden"];
				
				// hidden attr
				idRandom = generateRandomId();
				if( hiddenField == "false" ){
					hidden( elementContent, paramField, "", idRandom, keyParam);
					hidden( elementContent, paramType, "", idRandom, valueType);					
					if( renderType == "datepicker" ){
						idRandom = generateRandomId();
						datepicker(elementContent, labelName, paramValue, keyParam+formId+idRandom, currentParam);
					}else if( renderType == "dropdown"){
						// ridiculous options :p
						idRandom = generateRandomId();
						var objCreated = dropDown(elementContent, labelName, paramValue, currentParam["valueCollection"], currentParam["defaultValue"], tmpFormId, false, true, idRandom, selectOnChangeAction);						
						if( labelName.toUpperCase() == "LOGO" ){
							objCreated.removeClass( "dropdown-auto-chosen" );
							objCreated.removeClass( "dropdown-auto-combo" );
						}
						// set them  become auto complete dropdown
						objCreated.chosen();
					}else if( renderType == "list"){
						idRandom = generateRandomId();
						dropDown(elementContent, labelName, paramValue, currentParam["valueCollection"], currentParam["defaultValue"], tmpFormId, true, true, idRandom, selectOnChangeAction);
					}else if( renderType == "textbox"){
						idRandom = generateRandomId();
						textbox(elementContent, labelName, paramValue, idRandom, /*paramValue, */"", currentParam["defaultValue"], false);
					}
				}
			}
			idRandom = generateRandomId();
			dropDown(elementContent, "Output Type", "outputType", outputType, outputType[0], tmpFormId, false, false, idRandom);
			var buttonGenerate = button( elementContent, "generate", "Generate", "", 
					function(){
						var aValidator = htmlForm.validate({
							errorPlacement: function(label, elmnt){
								elmnt.parent().append( label );
							}
						});
						var isValid = aValidator.form();
						if( isValid ){
							buttonGenerate.val( "Loading report..." );
							generateReportAction(function(){
								buttonGenerate.val( "Generate" );
							});
						}
					} 
			);
		};
		htmlForm.show();
		//htmlForm.find(".dropdown-auto-chosen").chosen();
		//htmlForm.find(".dropdown-auto-combo").chosen();
		if( callCompleteAction != undefined ){
			callCompleteAction();
		}
	};
		
	function textbox(parent, labelName, paramName, idRandom, className, defaultValue, readOnly){
		var p = par(parent);
		label(p, labelName );
		var textObj = text(p, paramName, className, idRandom, defaultValue, readOnly);
		return textObj;
	}
	
	function dropDown(parent, labelName, paramName, listItem, defaultValue, formId, ismulti, isAutocomplete, elementId, onChange){
		var p = par(parent);
		label(p, labelName );
		var selectObj = select( p, paramName, "", elementId, listItem , defaultValue, formId, ismulti);
		
		if( isAutocomplete ){
			if( ismulti ){
				selectObj.addClass("dropdown-auto-chosen");
			}else {
				selectObj.addClass("dropdown-auto-combo");
			}
		}
		
		selectObj.change(function(event){
			if( onChange != undefined ){
				onChange();
			}
		});
		
		return selectObj;
	}
	function datepicker(parent, labelName, paramName, paramKey, currentParam){
		var p = par(parent);		
		label(p, labelName );
		// ignore default value
		var textObj = text( p, "display_"+paramName, "calendar", "display_"+paramKey, "");
		var hiddenVal = hidden( p, paramName, "", paramName, "");
		textObj.datepicker({dateFormat: "dd/mm/yy"});
		// dependency: jquery.maskedinput-1.3.min.js
		textObj.mask("99/99/9999", { placeholder:" " });
		var defaultDate = new Date();
		
		defaultDate.setHours(0);
		defaultDate.setMinutes(0);
		defaultDate.setSeconds(0);
		defaultDate.setMilliseconds(0);
		
		if( currentParam["defaultValue"] != null && currentParam["defaultValue"] != "" && currentParam["defaultValue"] != 0){
			var defaultValueInTime = currentParam["defaultValue"];
			var timeInNumber = new Number( defaultValueInTime );
			defaultDate = new Date( timeInNumber );
		}
		textObj.change(function(event){
			var currentDate = textObj.datepicker('getDate');
			if( currentDate == null ){
				currentDate = defaultDate;
			}
			console.log( "update time to "+currentDate.getTime() );
			hiddenVal.val( currentDate.getTime() );
		});
		hiddenVal.val( defaultDate.getTime() );
		textObj.datepicker('setDate',defaultDate);
		return textObj;
	};
	
	function button(parent, id, text, className, handler){
		var elmnt = document.createElement( "input" );
		$(elmnt).attr( "type", "button" );
		$(elmnt).addClass( className );
		$(elmnt).attr( "id", id.replace( " ", "_" ) );
		$(elmnt).val( text );
		$(elmnt).click( handler );
		$(elmnt).addClass( "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" );
		parent.append( $(elmnt) );
		return $(elmnt);
	}
	
	function label(parent, text){
		var elmnt = document.createElement( "label" );
		$(elmnt).html( text );
		parent.append( $(elmnt) );
		return $(elmnt);
	}
	
	function text( parent, name, className, id, value, readonly){
		var elmnt = document.createElement( "input" );
		$(elmnt).attr( "type", "text" );
		$(elmnt).attr( "name", name );
		$(elmnt).attr( "id", id.replace( " ", "_" ) );
		if( readonly ){
			$(elmnt).attr( "readonly", true );
		}
		// for now set all required
		$(elmnt).attr( "required", "required" );

		$(elmnt).val( value );
		$(elmnt).addClass( className );
		parent.append( $(elmnt) );
		return $(elmnt);
	}
	function hidden( parent, name, className, id, value){
		var elmnt = document.createElement( "input" );
		$(elmnt).attr( "type", "hidden" );
		$(elmnt).attr( "name", name );
		$(elmnt).attr( "id", id.replace( " ", "_" ) );
		$(elmnt).val( value );
		parent.append( $(elmnt) );
		return $(elmnt);
	}	
	function par(parent){
		var p = $( document.createElement( "p" ) );
		parent.append( p );
		return p;
	}
	
	function select( parent, name, className, id, data , defaultValue, formId, ismultiselect){
		id = id + formId;
		var elmnt = document.createElement( "select" );
		$(elmnt).attr( "name", name );
		$(elmnt).attr( "id", id.replace( " ", "_" ) );
		// for now set all required
		//$(elmnt).attr( "required", "required" );
		
		if( ismultiselect ){
			$(elmnt).attr( "multiple", "multiple" );
			$(elmnt).attr( "size", "5" );
			$(elmnt).attr( "name", name+"[]" );
		}
		$(elmnt).addClass( className );
		for( var i = 0; i < data.length; i++){
			var opt = document.createElement( "option" );
			var currentItem = data[ i ];
			var key = currentItem.key;
			var dataValue = currentItem.value;
			$(opt).attr( "value", key );
			if(defaultValue != null &&  !jQuery.isEmptyObject( defaultValue )){
				if( $.isArray( defaultValue ) ){
					for(var ia = 0; ia < defaultValue.length; ia++){
						if( defaultValue[ia] == key ){
							$(opt).attr( "selected", "selected");
						}
						if( defaultValue[ia] == dataValue ){
							$(opt).attr( "selected", "selected");
						}
					}
				}else {
					if( defaultValue == dataValue ){
						$(opt).attr( "selected", "selected");
					}
					if( defaultValue == key ){
						$(opt).attr( "selected", "selected");
					}
				}
			}
			$(opt).html( dataValue );
			$(elmnt).append( $(opt) );
		}
		parent.append( $(elmnt) );
		return $(elmnt);
	}
	
	// helper
	
	function generateHTMLTable(listItem, labelName){
		var column = ["KEY", labelName];
		
		var tableConstruct = "<table id=\"datasheet\" class=\"display\">";
		
		tableConstruct = tableConstruct +
							"<thead><tr>";
		for( var i = 0; i < column.length; i++ ){
			tableConstruct = tableConstruct + 
							"<th>"+column[i]+"</th>";
		}
		tableConstruct = tableConstruct + 
							"</tr></thead>";
		
		tableConstruct = tableConstruct +
		"<tbody>";
		for( var key in  listItem){
			tableConstruct = tableConstruct +
			"<tr>";
			tableConstruct = tableConstruct +
			"<td>"+key+"</td>";							
			tableConstruct = tableConstruct +
			"<td>"+listItem[key]+"</td>";							
			tableConstruct = tableConstruct +
			"</tr>";
		}
		
		tableConstruct = tableConstruct +
		"</tbody>";
		tableConstruct = tableConstruct + "</table>";
		return tableConstruct;		
	}
	
	function generateRandomId(){
		var xDate = new Date();
		var idRandom = "id-"+xDate.getTime()+"-"+xDate.getMilliseconds();
		return idRandom;
	}
		
})(jQuery);