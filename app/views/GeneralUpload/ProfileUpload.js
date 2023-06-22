function ProfileUpload(html){
	if(this instanceof ProfileUpload){
		var detailMap = new Hashtable();
		var detailMapAdd = new Hashtable();
		var detailMapCode = new Hashtable();
		var detailMapCodeAdd = new Hashtable();
		var formatData = ${formatData.raw()};
		var sourceFieldLists = new Hashtable();
		var sourceFieldListsMandatory = new Hashtable();
		var sourceFieldListsLength = new Hashtable();
		var sourceFieldListsSource = new Hashtable();
		var sourceFieldDataBySource = new Hashtable();
		var sourceFieldDataByParent = new Hashtable();
		var alreadyOpenDialog = false;
		var totalMappingCode = 0;
		var totalMappingCodeAdd = 0;
		
		var detailMapJson = {};
		var detailMapJsonAdd = {};
		
		var tmpCodeMap = new Hashtable();
		
		var app = html.inject(this, true);
		app.datatable = app.profileDetailTable.offlinepaging({
			"convertRow" : 	function( currData ){
				var fixValue = {
						"lookupId" : "",
						"lookupDescription" : ""
				};
				if( currData.fixValue ){
					fixValue.lookupId = ( currData.fixValue.lookupId ? currData.fixValue.lookupId : "" );
					fixValue.lookupDescription = ( currData.fixValue.lookupDescription ? currData.fixValue.lookupDescription : "" );
				}
				var additionalClass = "";
				var disabledButton = "";
				//#{if !confirming}
				additionalClass = "ui-button ui-widget ui-state-default ui-corner-all";
				//#{/if}
				//#{if confirming}
				disabledButton = "disabled";
				//#{/if}
				var deleteBtnTitle = "delete";
				if( currData.systemField ){
					if( currData.systemField == 1 ){
						additionalClass = "";
						disabledButton = "disabled";
						deleteBtnTitle = "system";
					}
				}
				var amandatory = "";
				if( currData.mandatory == 1 ){
					amandatory = "M";
				}else if( currData.mandatory == 0 ){
					amandatory = "O";
				}else if( currData.mandatory == 2 ){
					amandatory = "C";
				}
				
				// format type
				var aFormatType = {
						"lookupId" : "",
						"lookupDescription" : ""
				};
				if( currData.formatType ){
					aFormatType.lookupId = ( currData.formatType.lookupId ? currData.formatType.lookupId : "" );
					aFormatType.lookupDescription = ( currData.formatType.lookupDescription ? currData.formatType.lookupDescription : "" );
				}
				
				return ["<input style='width:50px' noseq='"+currData.noSeq+"' "+disabledButton+" class=\"ordermappingbutton "+additionalClass+"\" type='button' name='profile.orderup' value='^'></input>", 
	                    currData.noSeq, 
	                    currData.sourceField, 
	                    currData.targetField, 
	                    (currData.dataType.lookupDescription ? currData.dataType.lookupDescription : ""), 
	                    amandatory, 
	                    currData.length, 
	                    aFormatType.lookupDescription,
	                    "<input type=\"button\" value=\""+deleteBtnTitle+"\" "+disabledButton+" noseq='"+currData.noSeq+"' class=\"deletedetailbutton "+additionalClass+" \">"];
			},
			"eventRow" : function(columns){
				$("tbody tr", app.profileDetailTable).each(function(i, e){
					$("td", $(this)).each(function(j, e){
						if (columns[j].field != "component") {
							$(this).click(function(){
								var rowTable = $(this).parents("tr");
								var rowNumber = app.datatable.fnGetPosition(rowTable[0]);
								var currData = app.datatable.fnGetData(rowNumber);
								if( currData != null ){
												
									resetEntryDialogForm();
									
									var bean = getDetailMap( currData[1] );
									
									sourceFieldLists.put( bean.targetField, bean.dataType.lookupId );
									sourceFieldListsMandatory.put( bean.targetField, bean.mandatory );
									sourceFieldListsLength.put(bean.targetField, bean.length);
									sourceFieldListsSource.put( bean.targetField, bean.sourceField );
									
									fillEntryDialogForm(bean);
									
									// #{if confirming}
									$( "input.deletemappingbutton" ).disabled();
									app.entryFormDialog.find( "input" ).disabled();
									app.entryFormDialog.find( "select" ).disabled();
									// #{/if}
									app.entryFormDialog.dialog("open");
									
								}
							});							
						}
						
					});
				});
			}
		});
		app.datatable.bind("select", function(event, prop){
						
			resetEntryDialogForm();
			fillEntryDialogForm(prop.bean);
			
			app.entryFormDialog.dialog("open");
			// #{if confirming}
			$( "input.deletemappingbutton" ).disabled();
			app.entryFormDialog.find( "input" ).disabled();
			app.entryFormDialog.find( "select" ).disabled();
			// #{/if}
			
		});
		
		app.datatableAdd = app.profileDetailTableAdd.offlinepaging({
			"convertRow" : 	function( currData ){
				
				var additionalClass = "";
				var disabledButton = "";
				//#{if !confirming}
				additionalClass = "ui-button ui-widget ui-state-default ui-corner-all";
				//#{/if}
				//#{if confirming}
				disabledButton = "disabled";
				//#{/if}
				
				var deleteBtnTitle = "delete";
				if( currData.systemField ){
					if( currData.systemField == 1 ){
						additionalClass = "";
						disabledButton = "disabled";
						deleteBtnTitle = "system";
					}
				}
				
				var amandatory = "";
				if( currData.mandatory == 1 ){
					amandatory = "M";
				}else if( currData.mandatory == 0 ){
					amandatory = "O";
				}else if( currData.mandatory == 2 ){
					amandatory = "C";
				}
				
				var valueAdd = "";
				if(currData.defaultValue == null || currData.defaultValue == undefined) {
					valueAdd = "";
				} else {
					valueAdd = currData.defaultValue;
				}
				
				return [currData.noSeq,
	                    currData.targetField, 
	                    (currData.dataType.lookupDescription ? currData.dataType.lookupDescription : ""), 
	                    amandatory, 
	                    valueAdd, 
	                    "<input type=\"button\" value=\""+deleteBtnTitle+"\" "+disabledButton+"  noseq='"+currData.noSeq+"' class=\"deletedetailbuttonAdd "+additionalClass+" \">"];
			},
			"eventRow" : function(columns){
				$("tbody tr", app.profileDetailTableAdd).each(function(i, e){
					$("td", $(this)).each(function(j, e){
						if (columns[j].field != "component") {
							$(this).click(function(){
								var rowTable = $(this).parents("tr");
								var rowNumber = app.datatableAdd.fnGetPosition(rowTable[0]);
								var currData = app.datatableAdd.fnGetData(rowNumber);
								if( currData != null ){
												
									resetEntryDialogFormAdd();
									var bean = getDetailMapAdd( currData[0] );
									
									app.entryFormDialogAdd.dialog("open");
									// #{if confirming}
									$( "input.deletemappingbutton" ).disabled();
									app.entryFormDialogAdd.find( "input" ).disabled();
									app.entryFormDialogAdd.find( "select" ).disabled();
									// #{/if}
									fillEntryDialogFormAdd(bean);
								}
							});							
						}
						
					});
				});
			}
		});
		
		app.formTab.tabs();
		
		app.formTab.tabs('disable', 1);
		
		// mapping datatable
		app.href_tab_2.bind( "click.tabs", function(){
			createDatatable2();	
		} );
		
		app.datatable2Add = app.mappingTableAdd.offlinepaging({
			"sScrollXInner":"100%", 
			"convertRow" : function(mappingCodeObj){
			return ["<input type=\"text\" class=\"fromCodeEntryAdd\" name=\"mappingCodeAdd[].fromCode\" value=\""+mappingCodeObj.fromCode+"\"/>", 
                    "<input type=\"text\" class=\"toCodeEntryAdd\" name=\"mappingCodeAdd[].toCode\" value=\""+mappingCodeObj.toCode+"\"/>", 
                    "<input type=\"button\" value=\"delete\"/ class=\"deletemappingbuttonAdd\">"];
		}
		});
		
		// #{if !confirming}
		app.btnAddMapping.click(function(){			
			//reset form
			app.dFromCode.val("");
			app.dToCode.val("");
			
			app.mappingCodeForm.dialog('open');
			
		});
		$("input.deletemappingbutton").live("click", function(){
			var cRow = $(this).parents('tr');
			var rowNumber = app.datatable2.fnGetPosition(cRow[0]);
			var currData = app.datatable2.fnGetData(rowNumber);
			app.dialog_message_delete.dialog({
				autoOpen:false,
				height:120,
				width:250,
				modal:true,
				resizable : false,
				buttons: {
					"Yes": function() {
						removeMappingCode( app.sequenceNo.val(), currData[0]);
						app.datatable2.fnDeleteRow(rowNumber);
						app.dialog_message_delete.dialog("close");
					},
					"No ": function() {
						app.dialog_message_delete.dialog("close");
					}
				}
			});
			app.dialog_message_delete.css('overflow','hidden');
			app.dialog_message_delete.dialog('open');
			
		});
		// #{/if}
		
		app.mappingCodeForm.dialog({
			autoOpen:false,
			width:"450px",
			modal:true,
			resizable : false,
			buttons: {
				"Add": function() {
					var isError = false;
					// check emptyness first, then uniqueness
					if( app.dFromCode.val() == "" || app.dToCode.val() == "" ){
						app.dialog_message_empty_mapping_2.dialog({
							autoOpen:false,
							height:120,
							width:250,
							modal:true,
							resizable : false,
							buttons: {
								"OK": function() {
									app.dialog_message_empty_mapping_2.dialog("close");
								}
							}
						});
						app.dialog_message_empty_mapping_2.css('overflow','hidden');
						app.dialog_message_empty_mapping_2.dialog('open');
						isError = true;
					}else{
						// checking uniqueness
						var fromCodeMap = new Hashtable();
						var totalFromCode = app.datatable2.fnGetData().length;
						for( var idx = 0; idx < totalFromCode; idx++ ){
							var currData = app.datatable2.fnGetData(idx);
							fromCodeMap.put( currData[0], currData[1] );
						}
						if( fromCodeMap.get(app.dFromCode.val()) != null ){
							app.dialog_message_unique_mapping.dialog({
								autoOpen:false,
								height:120,
								width:250,
								modal:true,
								resizable : false,
								buttons: {
									"OK": function() {
										app.dialog_message_unique_mapping.dialog("close");
									}
								}
							});
							app.dialog_message_unique_mapping.css('overflow','hidden');
							app.dialog_message_unique_mapping.dialog('open');
							isError = true;
						}
					}
					
					if( !isError ){
						var aMappingCode = {
							"fromCode": app.dFromCode.val(),
							"toCode" : app.dToCode.val(),
							"profileDetail" : {
								"noSeq" : app.sequenceNo.val()
							}
						};
						app.datatable2.addData( aMappingCode );
						app.datatable2.fnDraw();
						app.datatable2.fnPageChange("last");													
						app.mappingCodeForm.dialog("close");						
						addMappingCode( aMappingCode );							
					}
				},
				"Cancel": function() {
					app.mappingCodeForm.dialog("close");
				}
			}
		});
		
		app.entryFormDialog.dialog({
				autoOpen:false,
				modal:true,
				width:"500px",
				resizable : false,
				buttons:{
					// #{if !confirming}
					"Save": function() {
						alreadyOpenDialog = false;
						if( app.systemField.val() == "true" ){
							
							app.entryFormDialog.dialog('close');
							return;
						}
						var isNew = true;
						if( !app.sequenceNo.val().isEmpty() ){
							if( getDetailMap( app.sequenceNo.val() ) != null ){
								isNew = false;
							}
						}
						
						// reset error
						app.headerNameError.html( "" );
						app.lengthError.html( "" );
						app.sourceFieldError.html( "" );
						
						var isError = 0;
						// vaidation
						if( app.headerName.val() == "" ){
							app.headerNameError.html("Required");
							isError = 1;
						}
						
						if( app.sourceField.val() == null ){
							app.sourceFieldError.html("Required");
							isError = 1;
						}
						
						if( app.length.val() == "" ){
							app.lengthError.html("Required");
							isError = 1;
						}
						
						// check mapping code
						var totalFromCode = 0;
						if( app.datatable2 != null ){
							totalFromCode = app.datatable2.fnGetData().length;
						}
						
						// force to clear out mapping code when mapping required is uncheck
						if( app.mappingRequired.attr("checked") == false  ){
							totalFromCode = 0;
						}
						if( totalFromCode == 0 && app.mappingRequired.attr("checked") == true ){
							app.dialog_message_empty_mapping.dialog({
								autoOpen:false,
								height:120,
								width:250,
								modal:true,
								resizable : false,
								buttons: {
									"OK": function() {
										app.dialog_message_empty_mapping.dialog("close");
									}
								}
							});
							app.dialog_message_empty_mapping.css('overflow','hidden');
							app.dialog_message_empty_mapping.dialog('open');
							app.formTab.tabs('select', 1);
							
							isError = 1;
						}
						
						if( isError == 1 ){
							return;
						}
						
						var aFixValue = {
							"lookupId" : app.fixValue.val(),
							"lookupDescription" : app.fixValue.find("option:selected").text()
						};
						
						var aFormatType = {
								"lookupId" : app.formatData.val(),
								"lookupDescription" : app.formatData.find("option:selected").text()
							};
						
						var tmpMappingCode = [];
						for( var idx = 0; idx < totalFromCode; idx++ ){
							var currData = app.datatable2.fnGetData(idx);
							var aMapCode = {
								"mappingKey" : $(currData[2]).attr("mappingKey"),
								"fromCode" : currData[0],
								"toCode" : currData[1],
								"profileDetail" : {
									"noSeq" : app.sequenceNo.val()
								}
							};
							tmpMappingCode.push( aMapCode );
						}
						var aDataType = {
							"lookupId" : app.dataType.val(),
							"lookupDescription" : jQuery.trim( app.dataType.find("option:selected").text() )
						};

						var aSeparator = {
							"lookupId" : app.separator.val(),
							"lookupDescription" : jQuery.trim( app.separator.find("option:selected").text() )
						};
						var amandatory = 0;
						amandatory = $(".valueType:checked").val();
						
						var tmpIdx = 0;
						var detailMapTemp = new Hashtable();
						var tamSeqNo = 0;
						var cekNo = false;
						if(app.na.attr("checked") == true){
							detailMap.each(function(idx, nextidx){
								var currData = getDetailMap( idx );
								currData.noSeq = tmpIdx;
								detailMapTemp.put( tmpIdx , currData);
								if(currData.noSeq == app.sequenceNo.val()){
									tamSeqNo = currData.noSeq;
									cekNo = true;
									return false;
								}
								tmpIdx++;
							});
						}
						
						var bean = getDetailMap( tamSeqNo );
						
						var currData = {
							
							"noSeq": app.sequenceNo.val(),
							"defaultValue": app.defaultValue.val(),
							"sourceField":app.headerName.val(),
							"targetField":app.sourceField.val(),
							"dataType":aDataType,
							"formatType":aFormatType,
							"mandatory":amandatory,
							"length":app.length.val(),
							"fixValue":aFixValue,
							"separator":aSeparator,
							"mappingRequired":app.mappingRequired.attr("checked"),
							"mappingCode" :tmpMappingCode,
							"systemDefaultValue" :app.systemDefaultValue.val(),
							"systemField" :app.systemField.val(),
							"na":app.na.attr("checked")
						};
						addDetailMap( currData.noSeq, currData );
						reOrderData();
						redrawTable();
						
						if(cekNo){
							if(bean.mandatory == '2' || bean.mandatory == '1'){
								var currData = {
									
									"noSeq": detailMapAdd.size(),
									"sourceField":bean.sourceField,
									"dataType":bean.dataType,
									"mandatory":bean.mandatory,
									"defaultValue": "",
									"targetField": bean.targetField,
									"formatType": bean.formatType,
									"length": bean.length,
									"fixValue": bean.fixValue,
									"separator": bean.separator,
									"mappingRequired":bean.mappingRequired,
									"mappingCode": bean.mappingCode,
									"systemDefaultValue" :false,
									"systemField" :bean.systemField
								};
								addDetailMapAdd( currData.noSeq, currData );
								reOrderDataAdd();
								redrawTableAdd();
								app.datatableAdd.fnPageChange("last");	
							}
						}
						
						if( isNew ){
							app.datatable.fnPageChange("last");							
						}
						app.entryFormDialog.dialog('close');
					},
					"Cancel": function() {
						
						alreadyOpenDialog = false;
						app.entryFormDialog.dialog('close');
					},
					// #{/if}
					// #{else}
					"Close": function() {
						
						app.entryFormDialog.dialog('close');
						alreadyOpenDialog = false;
					}
					//#{/else}
				}
		});
		
		app.entryFormDialogAdd.dialog({
			autoOpen:false,
			modal:true,
			width:"500px",
			resizable : false,
			buttons:{
				// #{if !confirming}
				"Save": function() {
					var isNew = true;
					if( !app.sequenceNoAdd.val().isEmpty() ){
						if( getDetailMapAdd( app.sequenceNoAdd.val() ) != null ){
							isNew = false;
						}
					}
					// reset error
					
					app.valueAddError.html( "" );
					app.sourceFieldAddError.html( "" );
					
					var isError = 0;
					// vaidation
					
					
					if( app.sourceFieldAdd.val() == null || app.sourceFieldAdd.val() == ''){
						app.sourceFieldAddError.html("Required");
						isError = 1;
					}
					
					if(!document.getElementById('valueAdCek').checked ){
						if( app.valueAdd.val() == null || app.valueAdd.val() == ''){
							app.valueAddError.html("Required");
							isError = 1;
						}
					}
					
					if( isError == 1 ){
						return;
					}
					
					var aFixValue = {
						"lookupId" : app.fixValueAdd.val(),
						"lookupDescription" : app.fixValueAdd.find("option:selected").text()
					};
					
					var aDataType = {
						"lookupId" : app.dataTypeAdd.val(),
						"lookupDescription" : jQuery.trim( app.dataTypeAdd.find("option:selected").text() )
					};

					var aSeparator = {
						"lookupId" : app.separator.val(),
						"lookupDescription" : jQuery.trim( app.separator.find("option:selected").text() )
					};
					
					var aFormatType = {
							"lookupId" : app.formatDataAdd.val(),
							"lookupDescription" : app.formatDataAdd.find("option:selected").text()
						};
					
					var amandatory = 0;
					amandatory = $(".valueTypeAdd:checked").val();
					
					var rowData = getDetailMapAdd( Number(app.sequenceNoAdd.val()) );
					
					if(rowData == null){
						var currData = {
							"noSeq": app.sequenceNoAdd.val(),
							"sourceField":app.headerNameAdd.val(),
							"dataType":aDataType,
							"mandatory":amandatory,
							"defaultValue": app.valueAdd.val(),
							"targetField": app.sourceFieldAdd.val(),
							"formatType": aFormatType,
							"length": app.lengthAdd.val(),
							"fixValue": aFixValue,
							"separator": aSeparator,
							"mappingRequired":app.mappingRequiredAdd.attr("checked"),
							"mappingCode": "",
							"systemDefaultValue" :app.valueAdCek.attr("checked"),
							"systemField" :app.systemFieldAdd.val(),
							"na":app.na.attr("checked")
						};
						addDetailMapAdd( currData.noSeq, currData );
					} else {
						
						var currData = {
							"noSeq": app.sequenceNoAdd.val(),
							"sourceField":app.headerNameAdd.val(),
							"dataType":aDataType,
							"mandatory":amandatory,
							"defaultValue": app.valueAdd.val(),
							"targetField": app.sourceFieldAdd.val(),
							"formatType": rowData.formatType,
							"length": app.lengthAdd.val(),
							"fixValue": aFixValue,
							"separator": aSeparator,
							"mappingRequired":app.mappingRequiredAdd.attr("checked"),
							"mappingCode": rowData.mappingCode,
							"systemDefaultValue" :app.valueAdCek.attr("checked"),
							"systemField" :app.systemFieldAdd.val(),
							"na":app.na.attr("checked")
						};
						addDetailMapAdd( currData.noSeq, currData );
					}
					
					redrawTableAdd();
					if( isNew ){
						app.datatableAdd.fnPageChange("last");							
					}
					app.entryFormDialogAdd.dialog('close');
				},
				"Cancel": function() {
					
					app.entryFormDialogAdd.dialog('close');
				},
				// #{/if}
				// #{else}
				"Close": function() {
					
					app.entryFormDialogAdd.dialog('close');
				}
				//#{/else}
			}
		});
		
		// #{if !confirming}
		app.btnReset.button()
		app.btnReset.click(function(){  
			var profKey = $("#key").val();
			if(profKey == null || profKey == ""){
				location.href = '@{entry()}';
			}else{
				location.href='@{edit()}/' + profKey; 
			}		
		});
		// #{/if}
		// #{if !confirming}
		app.btnPopulate.button();
		app.btnPopulate.click(function(){
			if( app.template.val().isEmpty()){
				app.templateError.html("Required");
			} else {
				duplicateByName( app.template.val(), "Upload" );
			}
			
		});
		// #{/if}
		
		// #{if !confirming}
		app.btnAddField.add(app.btnAddFieldAdd).button();
		app.btnAddField.click(function(){
			resetEntryDialogForm();
			app.entryFormDialog.dialog("open");
		});
		app.btnAddFieldAdd.click(function(){
			
			resetEntryDialogFormAdd();

			app.entryFormDialogAdd.dialog("open");
		});
		// #{/if}

		app.sourceField.change(function(){
			if( $(this).val() != null ){
				var tmpDataType = sourceFieldLists.get( $(this).val() );
				
				app.dataType.val( tmpDataType.toUpperCase() );
				app.dataType.change();
				var tmpMandatory = sourceFieldListsMandatory.get($(this).val());
				app.valueType1.attr("checked", false);
				app.valueType2.attr("checked", false);
				app.valueType3.attr("checked", false);
				
				if( tmpMandatory == 1 ){
					app.valueType1.attr("checked", true);
				}else if( tmpMandatory == 0 ){
					app.valueType2.attr("checked", true);					
				}else if( tmpMandatory == 2 ){
					app.valueType3.attr("checked", true);				
				}
				
				var tmpLength = sourceFieldListsLength.get($(this).val());
				app.length.val(tmpLength);
//				app.sourceHidden.val($(this).val());
				
			}
		});
		
		app.sourceFieldAdd.change(function(){
			if( $(this).val() != null ){
				var tmpDataType = sourceFieldLists.get( $(this).val() );
				app.dataTypeAdd.val( tmpDataType.toUpperCase() );
				
				app.dataTypeAdd.change();
				var tmpMandatory = sourceFieldListsMandatory.get($(this).val());
				app.valueTypeAdd1.attr("checked", false);
				app.valueTypeAdd2.attr("checked", false);
				app.valueTypeAdd3.attr("checked", false);
				
				if( tmpMandatory == 1 ){
					app.valueTypeAdd1.attr("checked", true);
				}else if( tmpMandatory == 0 ){
					app.valueTypeAdd2.attr("checked", true);					
				}else if( tmpMandatory == 2 ){
					app.valueTypeAdd3.attr("checked", true);				
				}
				
				var tmpLength = sourceFieldListsLength.get($(this).val());
				app.lengthAdd.val(tmpLength);
				var tmpSource = sourceFieldListsSource.get($(this).val());
				app.headerNameAdd.val(tmpSource);
			}
		});
		
		app.formatData.change(function(){
			// fix length checking
		    checkFixValueRequired();
		});
		
		app.dataType.change(function(){
			var me = $(this);
			var key = jQuery.trim( me.find("option:selected").text() );
			
			var formatList = formatData[key];
			
			// reset old data
			var totalOpts = app.formatData[0].options.length;
			
			for( var idx = 0 ; idx < totalOpts ; idx++ ){
				app.formatData[0].options.remove(0);
			}
			
			// add new data
			for(var curidx in formatList){
				var format_ = formatList[curidx];				
			    var newOp = document.createElement( "option" );
			    newOp.value = curidx;
			    newOp.text = format_;
			    app.formatData[0].options.add( newOp );
			}
			if( key == "CHAR" ){
				//redmine #1190
				app.formatData.val("FORMAT_TYPE-STR-1");
				app.formatData.attr("disabled", true);
				
				app.mappingRequired.removeAttr( "disabled" );
				if( app.na.attr("checked") == false ){
					app.mappingRequired.attr( "disabled", false );
				} else {
					app.mappingRequired.attr( "disabled", true );
				}
			}else{
				app.mappingRequired.attr( "disabled", true );
				app.mappingRequired.removeAttr("checked");
				app.mappingRequired.change();
				app.formatData.attr("disabled", false);
			}
			checkFixValueRequired();
			
			if( app.dataType.val() == "DATA_TYPE-DAT" ){
		    	app.length.value( 8 );
		    }
			app.length.attr("disabled", true);
			
		});
		
		app.dataTypeAdd.change(function(){
			var me = $(this);
			var key = jQuery.trim( me.find("option:selected").text() );
			
			var formatList = formatData[key];
			
			// reset old data
			var totalOpts = app.formatDataAdd[0].options.length;
			
			for( var idx = 0 ; idx < totalOpts ; idx++ ){
				app.formatDataAdd[0].options.remove(0);
			}
			
			// add new data
			for(var curidx in formatList){
				var format_ = formatList[curidx];				
			    var newOp = document.createElement( "option" );
			    newOp.value = curidx;
			    newOp.text = format_;
			    app.formatDataAdd[0].options.add( newOp );
			}
			if( key == "CHAR" ){
				
			}else{
				
				app.mappingRequiredAdd.removeAttr("checked");
				
			}
			checkFixValueRequiredAdd();
			
			if( app.dataTypeAdd.val() == "DATA_TYPE-DAT" ){
		    	app.lengthAdd.value( 8 );
		    }else{
		    	app.lengthAdd.value( "" );
		    }
			
		});
		
		app.filePrefix.change(function(){
			if( $(this).val() != null && $(this).val() != '' ){
				app.h_filePrefix.val(app.filePrefix.val().toUpperCase());
			}
		});
		
		app.isPrefix.change(function(){
			if( $(this).attr("checked") == true ){
				app.filePrefix.attr('disabled', false);
			}else{
				app.filePrefix.attr('disabled', true);
			}
			app.filePrefix.val('');
		});
		
		app.mappingRequired.change(function(){
			if( $(this).attr("checked") == true ){
				app.formTab.tabs('enable', 1);
			}else{
				app.formTab.tabs('disable', 1);
				app.defaultValue.value( "" );
				if( app.datatable2 != null ){
					// reset mapping table
					app.datatable2.fnClearTable();					
				}
			}
		});
		
		app.valueAdCek.change(function(){
			if( $(this).attr("checked") == true ){
				app.valueAdd.attr('disabled', true);
				app.valueAdd.val('SYSTEM_DEFAULT_VALUE');
			}else{
				app.valueAdd.attr('disabled', false);
				app.valueAdd.val('');
			}
		});
		
		app.na.change(function(){
			
			if( $(this).attr("checked") == true ){
				if(app.headerName.val().isEmpty()){
					alert("Header Name must be filled");
					app.na.attr("checked", false);
				}else{
					var totalOpts = app.sourceField[0].options.length;
					for( var idx = 0 ; idx < totalOpts ; idx++ ){
						app.sourceField[0].options.remove(0);
					}
					var newOp = document.createElement( "option" );
				    newOp.value = "Dummy_"+app.sequenceNo.val();
				    newOp.text = "Dummy_"+app.sequenceNo.val();
				    app.sourceField.attr( "disabled", true );
				    app.sourceField[0].options.add( newOp );
				    app.dataType.val( "DATA_TYPE-STR" );
					app.dataType.change();
					app.formatData.val("FORMAT_TYPE-STR-1");
					app.valueType2.attr("checked", true);
					app.length.value("100");
					app.mappingRequired.attr("checked", false);
					app.mappingRequired.disabled();
					app.formTab.tabs('disable', 1);
					app.headerName.disabled();
					app.formatData.disabled();
				}
			}else{
				
				/*if ('${mode}'=='edit'){
					getSourceFieldListByParent(app.key.val(),"noAdd");
				} else {
					getSourceFieldListByParent(app.h_templete.val(),"noAdd");
				}*/
				getSourceFieldListByParent(app.h_templete.val(),"noAdd");
				app.sourceField.attr( "disabled", false );
				if(!app.sourceHidden.val().isEmpty()){
					var newOp = document.createElement( "option" );
				    newOp.value = app.sourceHidden.val();
				    newOp.text = app.sourceHidden.val();
				    app.sourceField[0].options.add( newOp );
					app.sourceField.val( app.sourceHidden.val());
					app.sourceField.change();
					app.dataType.change();
					if( app.dataType.val() == "DATA_TYPE-STR"){
						app.mappingRequired.enabled();
					} else {
						app.mappingRequired.disabled();
					}
					
				}
				app.formatData.enabled();
				app.headerName.enabled();
			}
		});
		
		app.template.lookup({
			list:'@{Pick.udProfiles()}',
			get:{
				url:'@{Pick.getUdProfilePickByName()}',
				success: function(data){
					if(data.profileKey != app.h_templete.val()){
						duplicateByName(data.name, data.actionType);
					}
					app.templateError.html("");
					$('#template').removeClass('fieldError');
				},
				error : function(data){
					resetFirst();
					$('#template').addClass('fieldError');
					app.h_templete.value( "" );
					app.template.value("");
					app.process.value("");
					app.names.val('NOT FOUND');
					app.h_names.val('NOT FOUND');
				}
			},			
			filter:'Upload',
			description:$('#names'),
			help:$('#groupTemplateHelp')
		});

			
		/**
		 * Init Event
		 */
		$("input.valueType[value=2]").attr("disabled", true);
		app.length.autoNumeric({vMax:'999', mDec:0, mNum:3});


		// #{if !confirming}
		$("input.ordermappingbutton").live("click", function(){
			var noseq = $(this).attr("noseq");
			var currData = getDetailMap( noseq );
			var prevSeq = noseq - 1;
			// process only
			if( prevSeq >= 0 ){
				var prevData = getDetailMap( prevSeq );
				prevData.noSeq = noseq;
				var prevDataDataTargetField = prevData.targetField.substring(0, 5);
				if(prevDataDataTargetField=='Dummy')
					prevData.targetField= 'Dummy_'+noseq;
				addDetailMap( noseq, prevData );

				currData.noSeq = prevSeq;
				var currDataTargetField = currData.targetField.substring(0, 5);
				if(currDataTargetField=='Dummy')
					currData.targetField = 'Dummy_'+prevSeq;
				addDetailMap( prevSeq, currData );
				redrawTable();
			}
		});
		// #{/if}
		
		/**
		 * start Function
		 */
		function createDatatable2() {
			var tmpNoSeq = app.sequenceNo.val();
			var additionalClass = "";
			var disabledButton = "";
			
			//#{if !confirming}
			additionalClass = "ui-button ui-widget ui-state-default ui-corner-all";
			//#{/if}
			//#{if confirming}
			disabledButton = "disabled";
			//#{/if}
			
			// mapping datatable
			app.datatable2 = app.mappingTable.offlinepaging({
				"sScrollXInner":"100%", 
				"convertRow" : function(mappingCodeObj){
					return [mappingCodeObj.fromCode, 
		                    mappingCodeObj.toCode, 
		                    "<input type=\"button\" "+disabledButton+" value=\"delete\"/ mappingKey=\""+(mappingCodeObj.mappingKey ? mappingCodeObj.mappingKey : "")+"\" class=\"deletemappingbutton "+additionalClass+"\">"];
				},
				"eventRow-xnotdefined" : function(columns){
					$("tbody tr", app.mappingTable).each(function(i, e){
						$("td", $(this)).each(function(j, e){
							if (columns[j].field != "component") {
								$(this).click(function(){
									var rowTable = $(this).parents("tr");
									var rowNumber = app.datatable2.fnGetPosition(rowTable[0]);
									var currData = app.datatable2.fnGetData(rowNumber);
									if( currData != null ){
										app.dFromCode.value( currData[0] );
										app.dToCode.value( currData[1] );
										app.mappingCodeForm.dialog("open");
									}
								});							
							}
						});
					});
				}				
			});
			
		}
		function checkFixValueRequired(){
			// fix length checking
		    
			if( app.dataType.val() == "DATA_TYPE-STR" && app.formatData.val() == "FORMAT_TYPE-STR-1"){
				
			}else if( app.dataType.val() == "DATA_TYPE-NUM" || app.dataType.val() == "DATA_TYPE-DAT"){
				
			}else{
				app.fixValue.val("");
				
			}			
		}
		
		function checkFixValueRequiredAdd(){
			// fix length checking
		    
			if( app.dataTypeAdd.val() == "DATA_TYPE-STR" && app.formatDataAdd.val() == "FORMAT_TYPE-STR-1"){
				
			}else if( app.dataTypeAdd.val() == "DATA_TYPE-NUM" || app.dataType.val() == "DATA_TYPE-DAT"){
				
			}else{
				app.fixValueAdd.val("");
			}			
		}
		
		function fillEntryDialogForm(bean){
			
			app.systemField.value( bean.systemField );
			app.sequenceNo.value( bean.noSeq );
			app.headerName.value( bean.sourceField );
			
			// add current data
			var newOp = document.createElement( "option" );
		    newOp.value = bean.targetField;
		    newOp.text = bean.targetField;
		    app.sourceField[0].options.add( newOp );
		    app.sourceField.val( bean.targetField );
		    app.sourceHidden.val(app.sourceField.val());
		    
		    // update data type
		    var tmpDataType = "";
		    if( bean.dataType.lookupDescription ){
		    	tmpDataType = bean.dataType.lookupId;
		    }
		    
		    app.dataType.val( tmpDataType.toUpperCase() );
		    app.dataType.change();
		    app.dataType.attr("disabled", true);
		    
		    app.formatData.val( bean.formatType.lookupId );
//		    app.formatData.attr("disabled", true);
		    
		    checkFixValueRequired();
		    
		    if( app.dataType.val() == "DATA_TYPE-DAT" ){
		    	bean.length = 8;
		    }
		    
			app.length.value( bean.length );
			app.length.attr("disabled", true);
			
			if( bean.fixValue.lookupId ){
				app.fixValue.val( bean.fixValue.lookupId );				
				
			}
			
			// see mapping code in action in href-tab-2.click
			if( bean.mappingRequired ){
				app.mappingRequired.attr( "checked", bean.mappingRequired );
				app.mappingRequired.change();
			}
			
			app.systemDefaultValue.value(bean.systemDefaultValue);
			
			var tmpNoSeq = app.sequenceNo.val();
			var bean = getDetailMap( tmpNoSeq );
			
			app.valueType1.attr("checked", false);
			app.valueType2.attr("checked", false);
			app.valueType3.attr("checked", false);
			
			var tmp_ = bean.mandatory;
			if( tmp_ == 1 ){
				app.valueType1.attr("checked", true);
			}else if( tmp_ == 0 ){
				app.valueType2.attr("checked", true);					
			}else if( tmp_ == 2 ){
				app.valueType3.attr("checked", true);					
			}
			
			app.valueType1.attr("disabled", true);
			app.valueType2.attr("disabled", true);
			app.valueType3.attr("disabled", true);
			
		    // default value
		    app.defaultValue.value( bean.defaultValue );
			
			
			// #{if !confirming}
			$( "input.deletemappingbutton" ).button();
			
			if( bean.systemField == true ){
				app.headerName.disabled();
				app.sourceField.disabled();
				app.dataType.disabled();
				
				app.formatData.disabled();
				app.na.disabled();
				app.fixValue.disabled();
				app.mappingRequired.disabled();
				
				// disable all mapping code
				var fromCodeList = $("input.fromCodeEntry");
				var toCodeList = $("input.toCodeEntry");
				var totalFromCode = fromCodeList.size();

				for( var idx = 0; idx < totalFromCode; idx++ ){
					$(fromCodeList[idx]).disabled();
					$(toCodeList[idx]).disabled();
				}
				
			}else {
				app.headerName.enabled();
				app.sourceField.enabled();
				app.dataType.disabled();
				app.valueType1.disabled();
				app.valueType2.disabled();
				app.na.enabled();
				
				app.formatData.enabled();
				
			}
			app.length.disabled();
			var cekDummy = "Dummy_"+app.sequenceNo.val();
			if(cekDummy == app.sourceField.val()){
				app.mappingRequired.disabled();
				app.formTab.tabs('disable', 1);
				app.headerName.disabled();
				app.formatData.disabled();
				app.sourceField.disabled();
				app.na.attr("checked", true);
			}
			
			// #{/if}
			// #{else}
			$( "input.deletemappingbutton" ).disabled();
			app.entryFormDialog.find( "input" ).disabled();
			app.entryFormDialog.find( "select" ).disabled();
			// #{/else}
			
			createDatatable2();
			
			if( bean != null && !alreadyOpenDialog ){
				if( bean.mappingRequired ){
				    // default value
				    app.defaultValue.value( bean.defaultValue );
					if( bean.mappingCode ){
						for( var idx in bean.mappingCode ){
							var cm = bean.mappingCode[ idx ];
							app.datatable2.addData( cm );
						}
						app.datatable2.fnDraw();
					}
				}
				
				if( bean.systemField == true ){
					// disable all mapping code
					for( var idx = 0; idx < app.datatable2.fnGetData().length ; idx++){
						var currData = app.datatable2.fnGetData(idx);
						$( currData[0] ).disabled();
						$( currData[1] ).disabled();
					}					
					
					app.defaultValue.disabled();
				}else{
					app.defaultValue.enabled();
				}
				
			}
			
			//redmine #1190
			if(app.dataType.val() == 'DATA_TYPE-STR'){
				app.formatData.val("FORMAT_TYPE-STR-1");
				app.formatData.attr("disabled", true);
			}
			else {
				app.formatData.attr("disabled", false);
			}
			
			if( bean.na ){
				app.na.attr( "checked", bean.na );
				app.na.change();
			}
		}
		
		function fillEntryDialogFormAdd(bean){
			app.systemFieldAdd.value( bean.systemField );
			app.sequenceNoAdd.value( bean.noSeq );
			app.headerNameAdd.value( bean.sourceField );
			
			// add current data
			var newOp = document.createElement( "option" );
		    newOp.value = bean.targetField;
		    newOp.text = bean.targetField;
		    app.sourceFieldAdd[0].options.add( newOp );
		    app.sourceFieldAdd.val( bean.targetField );
		    
		    // update data type
		    var tmpDataType = "";
		    if( bean.dataType.lookupDescription ){
		    	tmpDataType = bean.dataType.lookupId;
		    }
		    app.dataTypeAdd.val( tmpDataType.toUpperCase() );
		    app.dataTypeAdd.change();
		    app.dataTypeAdd.attr("disabled", true);
		    
		    app.formatDataAdd.value( bean.formatType.lookupId );
		    
			app.lengthAdd.value( bean.length );
			
			if( bean.fixValue.lookupId ){
				app.fixValueAdd.val( bean.fixValue.lookupId );				
				
			}
			
			// see mapping code in action in href-tab-2.click
			if( bean.mappingRequired ){
				app.mappingRequiredAdd.attr( "checked", bean.mappingRequired );
			}
			
			app.valueAdd.value( bean.defaultValue );
			if( bean.systemDefaultValue ){
				app.valueAdCek.attr( "checked", bean.systemDefaultValue );
				app.valueAdd.attr("disabled", true);
			}else{
				app.valueAdd.attr("disabled", false);
			}
			app.valueAdCek.enabled();
			
			var tmpNoSeq = app.sequenceNoAdd.val();
			var bean = getDetailMapAdd( tmpNoSeq );
			
			app.valueTypeAdd1.attr("checked", false);
			app.valueTypeAdd2.attr("checked", false);
			app.valueTypeAdd3.attr("checked", false);
			
			var tmp_ = bean.mandatory;
			if( tmp_ == 1 ){
				app.valueTypeAdd1.attr("checked", true);
			}else if( tmp_ == 0 ){
				app.valueTypeAdd2.attr("checked", true);					
			}else if( tmp_ == 2 ){
				app.valueTypeAdd3.attr("checked", true);				
			}
			
			app.valueTypeAdd1.attr("disabled", true);
			app.valueTypeAdd2.attr("disabled", true);
			app.valueTypeAdd3.attr("disabled", true);
			
			if( bean != null ){
				if( bean.mappingRequired ){				
					if( bean.mappingCode ){
						for( var idx in bean.mappingCode ){
							var cm = bean.mappingCode[ idx ];
							app.datatable2Add.addData( cm );
						}
						app.datatable2Add.fnDraw();
					}
				}
			}
			
			// #{if !confirming}
			$( "input.deletemappingbuttonAdd" ).button();
			// #{/if}
			// #{else}
			$( "input.deletemappingbuttonAdd" ).disabled();
			app.entryFormDialogAdd.find( "input" ).disabled();
			app.entryFormDialogAdd.find( "select" ).disabled();
			// #{/else}
			
		}
		
		function resetEntryDialogForm(){
			app.systemField.value( "" );
			app.headerName.value( "" );
			app.sourceField.enabled();
			app.headerName.enabled();
			
			// select first tab
			app.formTab.tabs('select', 0);

			var newNoSeq = detailMap.size();
			app.sequenceNo.value( newNoSeq );
			app.defaultValue.value( "" );
			
			// reset form
			app.headerName.value( "" );
			app.headerNameError.html( "" );
			app.length.value( "" );
			app.lengthError.html( "" );
			app.sourceField.value( "" );
			app.sourceFieldError.html( "" );
			app.mappingRequired.attr( "checked", false );
			app.na.attr( "checked", false );
			app.mappingRequired.change();
			app.fixValue.val("");
			app.systemDefaultValue.val("");
			app.sourceHidden.val("");
			
			app.length.disabled();
			$('.valueType').disabled();
			app.dataType.disabled();
			
			// get source field list
			/*if ('${mode}'=='edit'){
				getSourceFieldListByParent(app.key.val(),"noAdd");
			} else {
				getSourceFieldListByParent(app.h_templete.val(),"noAdd");
			}*/
			
			getSourceFieldListByParent(app.h_templete.val(),"noAdd");
			
			// reset mapping table
			//app.datatable2.fnClearTable();			
			
		}
		
		function resetEntryDialogFormAdd(){
			
			var newNoSeq = detailMapAdd.size();
			app.sequenceNoAdd.value( newNoSeq );
			app.systemFieldAdd.value( "" );
			app.valueAdd.value( "" );
			app.valueAdCek.attr( "checked", false );
			app.sourceFieldAdd.value( "" );
			app.sourceFieldAddError.html( "" );
			app.dataTypeAdd.value( "" );
			$('.valueTypeAdd').disabled();
			/*if ('${mode}'=='edit'){
				getSourceFieldListByParent(app.key.val(),"noAdd");
			} else {
				getSourceFieldListByParent(app.h_templete.val(),"noAdd");
			}*/
			
			getSourceFieldListByParent(app.h_templete.val(),"add");
			app.dataTypeAdd.disabled();
			if(app.sourceFieldAdd.val() == '' || app.sourceFieldAdd.val() == null){
				app.valueAdd.disabled();
				app.valueAdCek.disabled();
			} else {
				app.valueAdd.enabled();
				app.valueAdCek.enabled();
			}
		}
		
		function resetFirst(){
			app.headerName.value( "" );
			app.systemField.value( "" );
			app.sourceField.enabled();
			app.headerName.enabled();
			// select first tab
			app.formTab.tabs('select', 0);

			app.sequenceNo.value( "" );
			app.defaultValue.value( "" );
			// reset form
			app.headerName.value( "" );
			app.headerNameError.html( "" );
			app.length.value( "" );
			app.lengthError.html( "" );
			app.sourceField.value( "" );
			app.sourceFieldError.html( "" );
			app.mappingRequired.attr( "checked", false );
			app.na.attr( "checked", false );
			app.mappingRequired.change();
			app.fixValue.val("");
			app.systemDefaultValue.val("");
			app.sourceHidden.val("");
			
			document.getElementById('separator').selectedIndex = 0; 
			
			sourceFieldLists.clear();
			sourceFieldListsMandatory.clear();
			sourceFieldListsLength.clear();
			sourceFieldListsSource.clear();
			detailMap.clear();
			app.datatable.fnClearTable();
			
			app.sequenceNoAdd.value( "" );
			app.systemFieldAdd.value( "" );
			app.valueAdd.value( "" );
			app.valueAdCek.attr( "checked", false );
			app.sourceFieldAdd.value( "" );
			app.sourceFieldAddError.html( "" );
			app.dataTypeAdd.value( "" );
			detailMapAdd.clear();
			app.datatableAdd.fnClearTable();
			app.datatable2Add.fnClearTable();
		}
		
		function sourceFieldListBySourceHelper( fromPickData ){
			var allRows = fromPickData.rows;
			sourceFieldLists.clear();
			sourceFieldListsMandatory.clear();
			sourceFieldListsLength.clear();
			sourceFieldListsSource.clear();
			var totalOpts = app.sourceField[0].options.length;
			for( var idx = 0 ; idx < totalOpts ; idx++ ){
				app.sourceField[0].options.remove(0);
			}
			
			for( var idxa in allRows ){
				var curra = allRows[idxa];
				var currData = curra.data;
				
				// check if already used
				if( $( "input.sourcefield[value=\""+currData[0]+"\"]" )[0] != undefined){
					
					// already there, no need to add
					continue;
				}
				
				sourceFieldLists.put( currData[0], currData[1] );
				sourceFieldListsMandatory.put( currData[0], currData[2] );
				sourceFieldListsSource.put( currData[0], currData[3] );
				
				// add new data
			    var newOp = document.createElement( "option" );
			    newOp.value = currData[0];
			    newOp.text = currData[0];
			    app.sourceField[0].options.add( newOp );
			}
			app.sourceField.change();
			app.dataType.change();			
		}
		
		function sourceFieldListByParentHelper( parentDetail, param ){
			var totalOpts = 0;
			
			if(param == 'noAdd'){
				totalOpts = app.sourceField[0].options.length;
			} else {
				totalOpts = app.sourceFieldAdd[0].options.length;
			}
			
			for( var idx = 0 ; idx < totalOpts ; idx++ ){
				if(param == 'noAdd'){
					app.sourceField[0].options.remove(0);
				} else {
					app.sourceFieldAdd[0].options.remove(0);
				}
				
			}
			
			for( var idxa in parentDetail ){
				var curra = parentDetail[idxa];
				var currData = curra.targetField;
				
//				var currData = curra.sourceField;
				
				var currType = ( curra.dataType.lookupId ? curra.dataType.lookupId : "");
				var currMandatory = ( curra.mandatory );
				var currLength = ( curra.length );
				var sourceField = (curra.sourceField);
				// check if already used
				if( $( "input.sourcefield[value=\""+currData+"\"]" )[0] != undefined){
					// already there, no need to add
					
					continue;
				} 
				sourceFieldLists.put( currData, currType );
				sourceFieldListsMandatory.put( currData, currMandatory );
				sourceFieldListsLength.put(currData, currLength);
				sourceFieldListsSource.put(currData, sourceField);
				
				// add new data
			    var newOp = document.createElement( "option" );
			    newOp.value = currData;
			    newOp.text = currData;
			    if(param == 'noAdd'){
			    	app.sourceField[0].options.add( newOp );
			    } else {
			    	app.sourceFieldAdd[0].options.add( newOp );
			    }
			    
			}
			
			if(param == 'noAdd'){
				
				app.sourceField.change();
				
				app.dataType.change();
				
			} else {
				app.sourceFieldAdd.change();
				app.dataTypeAdd.change();
			}
						
		}
		
		function getSourceFieldListBySource(source){
			if( sourceFieldDataBySource.get( source ) != null ){
				sourceFieldListBySourceHelper(sourceFieldDataBySource.get( source ));				
			}else{
				$.post("@{Pick.retrieveFieldListOfView()}", {"source":source}, function(data) {
		    		checkRedirect(data);
					sourceFieldDataBySource.put( source, data )
					sourceFieldListBySourceHelper(sourceFieldDataBySource.get( source ) );
				});
			}
		}

		function getSourceFieldListByParent(parentKey,param){
			if( parentKey == "" ){
				return;
			}
			if( sourceFieldDataByParent.get( parentKey ) != null ){
				sourceFieldListByParentHelper(sourceFieldDataByParent.get( parentKey ), param);				
			}else{
				$.post("@{GeneralDownload.getProfileDetail()}", {"profileKey":parentKey}, function(data) {
		    		checkRedirect(data);
					sourceFieldDataByParent.put( parentKey, data );
					sourceFieldListByParentHelper(sourceFieldDataByParent.get( parentKey ), param );
				});
			}
		}

		function duplicateByName(profilename, actionType){

			$.post("@{GeneralDownload.duplicateByName()}", {'name':profilename, "actionType":actionType}, function(data) {				

	    		checkRedirect(data);
				if( app.key.val() != "" ){
					if( app.key.val() == data.profileKey ){
						app.h_templete.value( "" );
						app.template.value("");
						app.names.value("");
						app.process.value("");
						app.dialog_message_self_parent.dialog({
							autoOpen:false,
							height:120,
							width:250,
							modal:true,
							resizable : false,
							buttons: {
								"OK": function() {
									app.dialog_message_self_parent.dialog("close");
								}
							}
						});
						app.dialog_message_self_parent.css('overflow','hidden');
						app.dialog_message_self_parent.dialog('open');
						return;
					}
				}
				
				var details = data.details;
				
				resetFirst();
				if( data.profileKey ){
					getSourceFieldListByParent( data.profileKey, "noAdd" );
				}
				
				if(data.filetype != null){
					app.fileType.value(data.filetype);
				}
				
				app.source.value( data.source );
				// duplicate archive field/status and filters
				app.archiveField.value( data.archiveField );
				app.archiveStatus.value( data.archiveStatus );
				
				app.names.value(data.description);
				
				// clear master data
				detailMap.clear();
				detailMapAdd.clear();
				
				// clear out html field also
				app.hiddenDetailInput.find("div").remove();
				
				for( var idx in details ){
					var currData = details[idx];
					if(currData.sourceType == 'FILE'){
						addDetailMap( currData.noSeq, currData );
					} else {
						addDetailMapAdd( currData.noSeq, currData );
					}
					
				}
				app.source.value( data.source );
				app.h_templete.value( data.profileKey );
				
				if( data.separator.lookupId ){
					app.separator.val( data.separator.lookupId );
				}
				
				if( data.includeHeader ){
					app.includeHeader1.attr("checked", true);
				} else {
					app.includeHeader2.attr("checked", true);
				}
				
				// process
				app.process.value( data.process );
				
				// updateing filter
				if( data.filters ){
					// remove all input
					app.divFilters.find("input").remove();
					var tmpc = 0;
					for ( var idx in data.filters ){
						var afilter = data.filters[idx];
						var tmpname = "profile.filters["+tmpc+"]";
						
						var filterSeq = document.createElement("input");
						$(filterSeq).attr("type", "hidden");
						$(filterSeq).attr("name", tmpname+".noSeq");
						$(filterSeq).attr("value", afilter.noSeq);
						app.divFilters.append( filterSeq );

						var filterName = document.createElement("input");
						$(filterName).attr("type", "hidden");
						$(filterName).attr("name", tmpname+".fieldName");
						$(filterName).attr("value", afilter.fieldName);
						app.divFilters.append( filterName );

						var filterType = document.createElement("input");
						$(filterType).attr("type", "hidden");
						$(filterType).attr("name", tmpname+".dataType");
						$(filterType).attr("value", afilter.dataType);
						app.divFilters.append( filterType );

						var filterDefVal = document.createElement("input");
						$(filterDefVal).attr("type", "hidden");
						$(filterDefVal).attr("name", tmpname+".defValue");
						$(filterDefVal).attr("value", afilter.defValue);
						app.divFilters.append( filterDefVal );

						var filterOperator = document.createElement("input");
						$(filterOperator).attr("type", "hidden");
						$(filterOperator).attr("name", tmpname+".defOperator.lookupId");
						$(filterOperator).attr("value", (afilter.defOperator ? afilter.defOperator.lookupId : ""));
						app.divFilters.append( filterOperator );

						var lookupDisplayOperator = document.createElement("input");
						$(lookupDisplayOperator).attr("type", "hidden");
						$(lookupDisplayOperator).attr("name", tmpname+".lookupDisplay");
						$(lookupDisplayOperator).attr("value", afilter.lookupDisplay);
						app.divFilters.append( lookupDisplayOperator );

						var requiredOperator = document.createElement("input");
						$(requiredOperator).attr("type", "hidden");
						$(requiredOperator).attr("name", tmpname+".required");
						$(requiredOperator).attr("value", afilter.required);
						app.divFilters.append( requiredOperator );
						
						tmpc++;
					}
				}
				
				redrawTable();
				reOrderDataAdd();
				redrawTableAdd();
			});
		}

		function addMappingCode(data){
			// check existence
			if( detailMapCode.get(parseInt( data.profileDetail.noSeq )) == null ){ // no data
				tmpCodeMap.put(data.fromCode, data);
			}else{
				
				tmpCodeMap = detailMapCode.get(parseInt( data.profileDetail.noSeq ));
				tmpCodeMap.put(data.fromCode, data);
			}
			detailMapCode.put(parseInt( data.profileDetail.noSeq ), tmpCodeMap);
			totalMappingCode++;
		}
		
		function addMappingCodeAdd(data){
//			var keyCode = parseInt( detailMapCodeAdd.size() );
//			detailMapCodeAdd.put( keyCode, data );
			
			var keyCode = parseInt( data.profileDetail.noSeq );
			// check existence
			var tmpCode = new Hashtable();
			if( detailMapCodeAdd.get(keyCode) == null ){ // no data
				tmpCode.put(data.fromCode, data);
			}else{
				
				tmpCode = detailMapCodeAdd.get(keyCode);
				tmpCode.put(data.fromCode, data);
				
			}
			detailMapCodeAdd.put(keyCode, tmpCode);
			totalMappingCodeAdd++;
			
		}

		function addDetailMap(idx, data){
			
			detailMap.put( parseInt(idx), data );
			
			app.hiddenDetailInput.find( "#div"+data.noSeq ).remove();

			var divEl = document.createElement( "div" );			
			$(divEl).attr( "id" , "div"+data.noSeq).addClass("divhiddeninput");
			
			/**
			 * no sequence
			 */
			
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".systemField").val( data.systemField ) );
			
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".noSeq").val( data.noSeq ) );
			/**
			 * end no sequence
			 */
			
			/**
			 * default value
			 */
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".defaultValue").val( data.defaultValue ) );
			/**
			 * end default value
			 */
			
			/**
			 * target field
			 */
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").addClass("sourcefield").attr("name", "pdetails["+data.noSeq+"]"+".targetField").val( data.targetField ) );
			/**
			 * end target field
			 */
			
			/**
			 * source field
			 */
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".sourceField").val( data.sourceField ) );
			/**
			 * end source field
			 */
			
			/**
			 * Data Type
			 */
			
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".dataType.lookupId").val( data.dataType.lookupId ? data.dataType.lookupId : "" ) );

			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".dataType.lookupDescription").val( data.dataType.lookupId ? data.dataType.lookupDescription : "" ) );
			
			/**
			 * ENd Data type
			 */
			
			/**
			 * Format type
			 */
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".formatType.lookupId").val( data.formatType.lookupId ) );
			
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".formatType.lookupDescription").val( data.formatType.lookupDescription ) );
			/**
			 * end format type
			 */
			
			/**
			 * mandatory
			 */
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".mandatory").val( data.mandatory ) );
			/**
			 * end mandatory
			 */
			
			/**
			 * length
			 */
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".length").val( data.length ) );
			/**
			 * end length
			 */
			
			/**
			 * fix value
			 */
			
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".fixValue.lookupId").val( data.fixValue.lookupId ? data.fixValue.lookupId : "" ) );

			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".fixValue.lookupDescription").val( data.fixValue.lookupId ? data.fixValue.lookupDescription : "" ) );
			/**
			 * end fix value
			 */
			
			/**
			 * mapping code
			 */
			if( data.mappingCode ){
				for( var idx in data.mappingCode ){
					/* mapping code list */
					if(data.mappingCode[idx].mappingKey == null || data.mappingCode[idx].mappingKey == undefined) data.mappingCode[idx].mappingKey = "";
					if(data.mappingCode[idx].fromCode == null || data.mappingCode[idx].fromCode == undefined) data.mappingCode[idx].fromCode = "";
					if(data.mappingCode[idx].toCode == null || data.mappingCode[idx].toCode == undefined) data.mappingCode[idx].toCode = "";
					
					$(divEl).append(document.innerHTML = '<input class="mappingcode mappingKey" type="hidden" name="mappingCode['+totalMappingCode+'].mappingKey" value='+data.mappingCode[idx].mappingKey+'>');
					$(divEl).append(document.innerHTML = '<input class="mappingcode fromcode" type="hidden" name="mappingCode['+totalMappingCode+'].fromCode" value="'+convertString(data.mappingCode[idx].fromCode)+'">');
					$(divEl).append(document.innerHTML = '<input class="mappingcode tocode" type="hidden" name="mappingCode['+totalMappingCode+'].toCode" value="'+convertString(data.mappingCode[idx].toCode)+'">');
					$(divEl).append(document.innerHTML = '<input class="mappingcode profileDetail" type="hidden" name="mappingCode['+totalMappingCode+'].profileDetail.noSeq" value='+data.noSeq+'>');
					addMappingCode( data.mappingCode[idx] );
					
				}
			}
			/**
			 * end mapping code
			 */
			
			// mapping required field
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".mappingRequired").val( data.mappingRequired ) );
			
			// systemDefaultValue field
			$(divEl).append( $(document.createElement( "input" )).attr("type", "hidden").attr("name", "pdetails["+data.noSeq+"]"+".systemDefaultValue").val( data.systemDefaultValue ) );

			
			app.hiddenDetailInput.append( divEl );
			
			divEl = null;
			
		}
		
		function addDetailMapAdd(idx, data){
			
			detailMapAdd.put( parseInt(idx), data );
			
			var divId = "div"+data.noSeq;
			app.hiddenDetailInputAdd.find( "#"+divId ).remove();

			var divEl = document.createElement( "div" );			
			$(divEl).attr( "id" , divId);
			$(divEl).addClass("divhiddeninputAdd");

			/**
			 * no sequence
			 */
			var partialDetailsName = "pdetailsAdd["+data.noSeq+"]";
			
			var systemFieldEl = document.createElement( "input" );
			$(systemFieldEl).attr("type", "hidden");
			$(systemFieldEl).attr("name", partialDetailsName+".systemField");
			$(systemFieldEl).val( data.systemField );
			$(divEl).append( systemFieldEl );
			
			var seqEl = document.createElement( "input" );
			$(seqEl).attr("type", "hidden");
			$(seqEl).attr("name", partialDetailsName+".noSeq");
			$(seqEl).val( data.noSeq );
			$(divEl).append( seqEl );
			/**
			 * end no sequence
			 */
			

			/**
			 * target field
			 */
			var targetEl = document.createElement( "input" );
			$(targetEl).attr("type", "hidden");
			$(targetEl).addClass("sourcefield");
			$(targetEl).attr("name", partialDetailsName+".targetField");
			$(targetEl).val( data.targetField );
			$(divEl).append( targetEl );
			/**
			 * end target field
			 */
			
			
			/**
			 * Data Type
			 */
			
			var tmpDataTypelId = "";
			var tmpDataTypeDesc = "";
			if( data.dataType.lookupId ){
				tmpDataTypelId = data.dataType.lookupId;
				tmpDataTypeDesc = data.dataType.lookupDescription;
			}

			var dataTypeIdEl = document.createElement( "input" );
			$(dataTypeIdEl).attr("type", "hidden");
			$(dataTypeIdEl).attr("name", partialDetailsName+".dataType.lookupId");
			$(dataTypeIdEl).val( tmpDataTypelId );
			$(divEl).append( dataTypeIdEl );

			var dataTypeCodeEl = document.createElement( "input" );
			$(dataTypeCodeEl).attr("type", "hidden");
			$(dataTypeCodeEl).attr("name", partialDetailsName+".dataType.lookupDescription");
			$(dataTypeCodeEl).val( tmpDataTypeDesc );
			$(divEl).append( dataTypeCodeEl );
			
			/**
			 * ENd Data type
			 */
			
			/**
			 * mandatory
			 */
			var mandatoryEl = document.createElement( "input" );
			$(mandatoryEl).attr("type", "hidden");
			$(mandatoryEl).attr("name", partialDetailsName+".mandatory");
			$(mandatoryEl).val( data.mandatory );
			$(divEl).append( mandatoryEl );
			/**
			 * end mandatory
			 */
			
			/**
			 * default value
			 */
			var defaultValueEl = document.createElement( "input" );
			$(defaultValueEl).attr("type", "hidden");
			$(defaultValueEl).attr("name", partialDetailsName+".defaultValue");
			$(defaultValueEl).val( data.defaultValue );
			$(divEl).append( defaultValueEl );
			/**
			 * end default value
			 */

			/**
			 * source field
			 */
			var sourceEl = document.createElement( "input" );
			$(sourceEl).attr("type", "hidden");
//			$(sourceEl).addClass("sourcefield");
			$(sourceEl).attr("name", partialDetailsName+".sourceField");
			$(sourceEl).val( data.sourceField );
			$(divEl).append( sourceEl );
			/**
			 * end source field
			 */
			
			
			/**
			 * Format type
			 */
			
			var formatTypeEl = document.createElement( "input" );
			$(formatTypeEl).attr("type", "hidden");
			$(formatTypeEl).attr("name", partialDetailsName+".formatType.lookupId");
			$(formatTypeEl).val( data.formatType.lookupId );
			$(divEl).append( formatTypeEl );
			/**
			 * end format type
			 */
			
			/**
			 * length
			 */
			var lengthEl = document.createElement( "input" );
			$(lengthEl).attr("type", "hidden");
			$(lengthEl).attr("name", partialDetailsName+".length");
			$(lengthEl).val( data.length );
			$(divEl).append( lengthEl );
			/**
			 * end length
			 */
			
			/**
			 * fix value
			 */
			var tmpFixValId = "";
			var tmpFixValDesc = "";
			if( data.fixValue.lookupId ){
				tmpFixValId = data.fixValue.lookupId;
				tmpFixValDesc = data.fixValue.lookupDescription;
			}
			var fixValueIdEl = document.createElement( "input" );
			$(fixValueIdEl).attr("type", "hidden");
			$(fixValueIdEl).attr("name", partialDetailsName+".fixValue.lookupId");
			$(fixValueIdEl).val( tmpFixValId );
			$(divEl).append( fixValueIdEl );

			var fixValueDescEl = document.createElement( "input" );
			$(fixValueDescEl).attr("type", "hidden");
			$(fixValueDescEl).attr("name", partialDetailsName+".fixValue.lookupDescription");
			$(fixValueDescEl).val( tmpFixValDesc );
			$(divEl).append( fixValueDescEl );
			/**
			 * end fix value
			 */
			
			/**
			 * mapping code
			 */
			
			if( data.mappingCode ){
				for( var idx in data.mappingCode ){
					var currC = data.mappingCode[idx];
					/* mapping code list */
					var partialMappingCodeName = "mappingCodeAdd["+totalMappingCodeAdd+"]";
					
					var mappingKeyEl = document.createElement( "input" );
					$(mappingKeyEl).attr("type", "hidden");
					$(mappingKeyEl).addClass("mappingcode");
					$(mappingKeyEl).addClass("mappingKey");
					$(mappingKeyEl).attr("name", partialMappingCodeName+".mappingKey");
					$(mappingKeyEl).val( currC.mappingKey );
					$(divEl).append( mappingKeyEl );
					
					var fromCodeEl = document.createElement( "input" );
					$(fromCodeEl).attr("type", "hidden");
					$(fromCodeEl).addClass("mappingcode");
					$(fromCodeEl).addClass("fromcode");
					$(fromCodeEl).attr("name", partialMappingCodeName+".fromCode");
					$(fromCodeEl).val( currC.fromCode );
					$(divEl).append( fromCodeEl );
					
					var toCodeEl = document.createElement( "input" );
					$(toCodeEl).attr("type", "hidden");
					$(toCodeEl).addClass("mappingcode");
					$(toCodeEl).addClass("tocode");
					$(toCodeEl).attr("name", partialMappingCodeName+".toCode");
					$(toCodeEl).val( currC.toCode );
					$(divEl).append( toCodeEl );

					var pdEl = document.createElement( "input" );
					$(pdEl).attr("type", "hidden");
					$(pdEl).addClass("mappingcode");
					$(pdEl).addClass("profileDetail");
					$(pdEl).attr("name", partialMappingCodeName+".profileDetail.noSeq");
					$(pdEl).val( data.noSeq );
					$(divEl).append( pdEl );

					addMappingCodeAdd( currC );
				}
			}
			/**
			 * end mapping code
			 */
			
			// mapping required field
			var mappingRequiredEl = document.createElement( "input" );
			$(mappingRequiredEl).attr("type", "hidden");
			$(mappingRequiredEl).attr("name", partialDetailsName+".mappingRequired");
			$(mappingRequiredEl).val( data.mappingRequired );
			$(divEl).append( mappingRequiredEl );
			
			var systemDefaultValueEl = document.createElement( "input" );
			$(systemDefaultValueEl).attr("type", "hidden");
			$(systemDefaultValueEl).attr("name", partialDetailsName+".systemDefaultValue");
			$(systemDefaultValueEl).val( data.systemDefaultValue );
			$(divEl).append( systemDefaultValueEl );
			
			app.hiddenDetailInputAdd.append( divEl );
			
		}
		
		function removeDetailMap(idx){
			detailMap.remove( parseInt( idx ) );
			app.hiddenDetailInput.find( "#div"+idx ).remove();
		}
		
		function removeDetailMapAdd(idx){
			detailMapAdd.remove( parseInt( idx ) );
			app.hiddenDetailInputAdd.find( "#div"+idx ).remove();
		}

		function getDetailMap(idx){
			return detailMap.get( parseInt( idx ) );
		}
		
		function getDetailMapAdd(idx){
			return detailMapAdd.get( parseInt( idx ) );
		}

		function removeMappingCode(idx, fromCode){
			var tmpCode = detailMapCode.get( parseInt( idx ) );
			if( tmpCode != null ){
				tmpCode.remove(fromCode);
				detailMapCode.put( parseInt( idx ), tmpCode );
			}else{
				console.log( "noSeq:"+ idx+" not found in the list..");
			}
		}

		function removeMappingCodeAdd(idx, fromCode){
			
			var tmpCode = detailMapCodeAdd.get( parseInt( idx ) );
			if( tmpCode != null ){
				tmpCode.remove(fromCode);
				detailMapCodeAdd.put( parseInt( idx ), tmpCode );
			}else{
				console.log( "noSeq:"+ idx+" not found in the list..");
			}
		}

		function reOrderData(){
			var tmpIdx = 0;
			var detailMapTemp = new Hashtable();
			detailMap.each(function(idx, nextidx){
				var currData = getDetailMap( idx );
				var currDataTargetField = currData.targetField.substring(0, 5);

				if(currDataTargetField=='Dummy')
					currData.targetField = 'Dummy_'+tmpIdx;

				currData.noSeq = tmpIdx;
				detailMapTemp.put( tmpIdx , currData);
				tmpIdx++;
			});
			
			// clear master data
			detailMap.clear();
			
			// clear out html field also
			app.hiddenDetailInput.find("div").remove();
			
			detailMapTemp.each(function(idx, nextidx){
				var currData = detailMapTemp.get( idx );
				addDetailMap( idx , currData);
			});
		}
		
		function reOrderDataAdd(){
			var tmpIdx = 0;
			var detailMapTemp = new Hashtable();
			detailMapAdd.each(function(idx, nextidx){
				var currData = getDetailMapAdd( idx );
				currData.noSeq = tmpIdx;
				detailMapTemp.put( tmpIdx , currData);
				tmpIdx++;
			});
			
			// clear master data
			detailMapAdd.clear();
			
			// clear out html field also
			app.hiddenDetailInputAdd.find("div").remove();
			
			detailMapTemp.each(function(idx, nextidx){
				var currData = detailMapTemp.get( idx );
				addDetailMapAdd( idx , currData);
			});
		}
		
		function redrawTable(){
			var detailMapArray = [];
			detailMap.each(function(idx, nextidx){
				var currData = getDetailMap( idx );
				detailMapArray.push( currData );
			});
			var iPage = 0;
			for( var idxSett in $.fn.dataTableSettings ){
				var oSettings = $.fn.dataTableSettings[idxSett];
				if( oSettings.sInstance == "profileDetailTable" ){
					iPage = oSettings._iDisplayLength === -1 ? 0 : Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength );
				}
			}
			app.datatable.reload( detailMapArray );
			// return to original page
			if ( iPage > 0 ){
				for( var iPageC_ = 0; iPageC_ < iPage; iPageC_++ ){
					app.datatable.fnPageChange("next");
				}
			}
		}
		
		function redrawTableAdd(){
			var detailMapArray = [];
			detailMapAdd.each(function(idx, nextidx){
				var currData = getDetailMapAdd( idx );
				
				detailMapArray.push( currData );
			});
			var iPage = 0;
			for( var idxSett in $.fn.dataTableSettings ){
				var oSettings = $.fn.dataTableSettings[idxSett];
				if( oSettings.sInstance == "profileDetailTableAdd" ){
					iPage = oSettings._iDisplayLength === -1 ? 0 : Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength );
				}
			}
			app.datatableAdd.reload( detailMapArray );
			if ( iPage > 0 ){
				for( var iPageC_ = 0; iPageC_ < iPage; iPageC_++ ){
					app.datatableAdd.fnPageChange("next");
				}
			}
		}
		/**
		 * end Function
		 */
		
		
		$( "#profileDetailTable_wrapper" );
		$( "#profileDetailTableAdd_wrapper" );
		// #{if !confirming}
		$("input.deletedetailbutton").live("click", function(){
			var noseq = $(this).attr( "noseq" );
			var cRow = $(this).parents('tr');
			var rowNumber = app.datatable.fnGetPosition(cRow[0]);
			if( detailMap.size() == 1 ){
				app.dialog_message_nodelete.dialog({
					autoOpen:false,
					height:120,
					width:250,
					modal:true,
					resizable : false,
					buttons: {
						"OK": function() {
							app.dialog_message_nodelete.dialog("close");
						}
					}
				});
				app.dialog_message_nodelete.css('overflow','hidden');
				app.dialog_message_nodelete.dialog('open');
				
			} else {
				app.dialog_message_delete.dialog({
					autoOpen:false,
					height:120,
					width:250,
					modal:true,
					resizable : false,
					buttons: {
						"Yes": function() {
							app.datatable.fnDeleteRow(rowNumber);
							var rowData = getDetailMap( rowNumber );
							removeDetailMap( parseInt(noseq) );
							// re order data after remove
							reOrderData();
							redrawTable();
							
							if(rowData.mandatory == '2' || rowData.mandatory == '1'){
								var currData = {
									
									"noSeq": detailMapAdd.size(),
									"sourceField":rowData.sourceField,
									"dataType":rowData.dataType,
									"mandatory":rowData.mandatory,
									"defaultValue": "",
									"targetField": rowData.targetField,
									"formatType": rowData.formatType,
									"length": rowData.length,
									"fixValue": rowData.fixValue,
									"separator": rowData.separator,
									"mappingRequired":rowData.mappingRequired,
									"mappingCode": rowData.mappingCode,
									"systemDefaultValue" :false,
									"systemField" :rowData.systemField
								};
								addDetailMapAdd( currData.noSeq, currData );
								reOrderDataAdd();
								redrawTableAdd();
								app.datatableAdd.fnPageChange("last");
								
							}
							app.dialog_message_delete.dialog("close");
						},
						"No ": function() {
							app.dialog_message_delete.dialog("close");
						}
					}
				});
				app.dialog_message_delete.css('overflow','hidden');
				app.dialog_message_delete.dialog('open');
			}
			
		});
		
		$("input.deletedetailbuttonAdd").live("click", function(){
			var noseq = $(this).attr( "noseq" );
			var cRow = $(this).parents('tr');
			var rowNumber = app.datatableAdd.fnGetPosition(cRow[0]);
			$("#dialog-message-delete").dialog({
				autoOpen:false,
				height:120,
				width:250,
				modal:true,
				resizable : false,
				buttons: {
					"Yes": function() {
						app.datatableAdd.fnDeleteRow(rowNumber);
						var rowData = getDetailMapAdd( rowNumber );
						removeDetailMapAdd( parseInt(noseq) );
						// re order data after remove
						reOrderDataAdd();
						redrawTableAdd();
						if(rowData.mandatory == '2' || rowData.mandatory == '1'){
							var currData = {
								
								"noSeq": detailMap.size(),
								"defaultValue": "",
								"targetField":rowData.targetField,
								"sourceField":rowData.sourceField,
								"dataType":rowData.dataType,
								"formatType":rowData.formatType,
								"mandatory":rowData.mandatory,
								"length":rowData.length,
								"fixValue":rowData.fixValue,
								"separator":rowData.separator,
								"mappingRequired":rowData.mappingRequired,
								"mappingCode" :rowData.mappingCode,
								"systemDefaultValue" :false,
								"systemField" :rowData.systemField
							};
							addDetailMap( currData.noSeq, currData );
							reOrderData();
							redrawTable();
							app.datatable.fnPageChange("last");
						}
						$("#dialog-message-delete").dialog("close");
					},
					"No ": function() {
						$("#dialog-message-delete").dialog("close");
					}	
				}
			});
			$('#dialog-message-delete').css('overflow','hidden');
			$("#dialog-message-delete").dialog('open');
		});
		// #{/if}
		
		//#{if profile?.templete != null}
		
		getSourceFieldListByParent( "${profile?.templete}","noAdd" );
		//#{/if}
		
		//#{list profile?.details,  as:'d'}
		//#{/list}
		//#{if detailMapJson != null}
		detailMapJson = ${detailMapJson};
		for( var idx in detailMapJson ){
			var cd = detailMapJson[idx];
			addDetailMap( idx, cd );
		}
		//#{/if}
		
		//#{if detailMapJsonAdd != null}
		detailMapJsonAdd = ${detailMapJsonAdd};
		for( var idx in detailMapJsonAdd ){
			var cd = detailMapJsonAdd[idx];
			addDetailMapAdd( idx, cd );
		}
		//#{/if}
		
		reOrderData();
		redrawTable();
		reOrderDataAdd();
		redrawTableAdd();
		
		// #{if confirming}
		$( "input.ordermappingbutton" ).disabled();
		$( "input.deletedetailbutton" ).disabled();
		$( "input.deletedetailbuttonAdd" ).disabled();
		// #{/if}
	}else{
		return new ProfileUpload(html);
	}
}