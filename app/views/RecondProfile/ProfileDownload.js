function ProfileDownload(html){
	if(this instanceof ProfileDownload){
		var lastDuplicatedBy = "${profileTemplate?.name?.raw()}${profileTemplate?.actionType?.raw()}";
		var detailMap = new Hashtable();
		var detailMapCode = new Hashtable();
		var formatData = ${formatData.raw()};
		var sourceFieldLists = new Hashtable();
		var sourceFieldDataBySource = new Hashtable();
		var sourceFieldDataByParent = new Hashtable();
		var alreadyOpenDialog = false;
		var totalMappingCode = 0;
		
		var detailMapJson = {};
		
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
						additionalClass = "ui-button ui-widget ui-state-default ui-corner-all ui-button-disabled ui-state-disabled";
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
				var aFormatType = null;
				if( currData.formatType ){
					aFormatType = {
						"lookupId" : "",
						"lookupDescription" : ""
					};
					aFormatType.lookupId = ( currData.formatType.lookupId ? currData.formatType.lookupId : "" );
					aFormatType.lookupDescription = ( currData.formatType.lookupDescription ? currData.formatType.lookupDescription : "" );
				}
				return ["<input style='width:50px' noseq='"+currData.noSeq+"' class=\"ordermappingbutton ui-button ui-widget ui-state-default ui-corner-all\" type='button' name='profile.orderup' value='^'></input>", 
	                    currData.noSeq, 
	                    currData.targetField, 
	                    currData.sourceField, 
	                    (currData.dataType.lookupDescription ? currData.dataType.lookupDescription : ""), 
	                    amandatory, 
	                    fixValue.lookupDescription, 
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
									console.log("Open edit data");			
									resetEntryDialogForm();
									var bean = getDetailMap( currData[1] );
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
		
		app.formTab.tabs();
		
		app.href_tab_2.bind( "click.tabs", function(){
			// mapping datatable
			createDatatable2();			
		} );
		
		app.formTab.tabs('disable', 1);
		
		
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
							console.log("No save, it is a system field");
							app.entryFormDialog.dialog('close');
							return;
						}
						var isNew = true;
						if( !app.sequenceNo.val().isEmpty() ){
							if( getDetailMap( app.sequenceNo.val() ) != null ){
								isNew = false;
							}
						}
						console.log("Save "+(isNew ? "new" : "old") + " data");
						// reset error
						app.headerNameError.html( "" );
						app.lengthError.html( "" );
						app.sourceFieldError.html( "" );
						
						var isError = 0;
						// vaidation
						if( app.headerName.val() == "" ){
							app.headerNameError.html("Required");
							console.log( "header name required" );
							isError = 1;
						}
						
						if( app.sourceField.val() == null ){
							app.sourceFieldError.html("Required");
							console.log( "source field required" );
							isError = 1;
						}
						
						if( app.length.val() == "" ){
							app.lengthError.html("Required");
							console.log( "length required" );
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

						var aFixValue = {};
						
						if( app.fixValueRequired.attr("checked") == true ){
							aFixValue = {
									"lookupId" : app.fixValue.val(),
									"lookupDescription" : app.fixValue.find("option:selected").text()
								};							
						}

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
						var currData = {
							"profileDetailKey" : (app.profileDetailKey.val() == "" ? null : app.profileDetailKey.val()),
							"noSeq": app.sequenceNo.val(),
							"defaultValue": app.defaultValue.val(),
							"targetField":app.headerName.val(),
							"sourceField":app.sourceField.val(),
							"dataType":aDataType,
							"formatType":aFormatType,
							"mandatory":amandatory,
							"length":app.length.val(),
							"fixValue":aFixValue,
							"separator":aSeparator,
							"mappingRequired":app.mappingRequired.attr("checked"),
							"mappingCode" :tmpMappingCode,
							"systemField" :app.systemField.val()
						};
						addDetailMap( currData.noSeq, currData );
						redrawTable();
						if( isNew ){
							app.datatable.fnPageChange("last");							
						}
						app.entryFormDialog.dialog('close');
					},
					"Cancel": function() {
						console.log("Cancel");
						alreadyOpenDialog = false;
						app.entryFormDialog.dialog('close');
					}
					// #{/if}
					// #{else}
					"Close": function() {
						console.log("Close");
						app.entryFormDialog.dialog('close');
						alreadyOpenDialog = false;
					}
					//#{/else}
				}
		});
		
		// #{if !confirming}
		app.btnReset.button()
		app.btnReset.click(function(){  
			var profKey = app.key.val();
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
			if( app.template.val() == "" ){
				app.profileRefError.html("Required");
			}else{
				duplicateByName( app.template.val(), "Reconcile" );
			}
		});
		// #{/if}
		
		// #{if !confirming}
		app.btnAddField.button();
		app.btnAddField.click(function(){
			console.log( "add field" );
			resetEntryDialogForm();
			// no source field available
			if( app.sourceField[0].options.length == 0 ){
				app.dialog_message_nosourcefield.dialog({
					autoOpen:false,
					height:120,
					width:250,
					modal:true,
					resizable : false,
					buttons: {
						"OK": function() {
							app.dialog_message_nosourcefield.dialog("close");
						}	
					}
				});
				app.dialog_message_nosourcefield.css('overflow','hidden');
				app.dialog_message_nosourcefield.dialog('open');				
			}else{				
				app.entryFormDialog.dialog("open");
			}
		});
		// #{/if}

		app.sourceField.change(function(){
			if( $(this).val() != null ){
				var tmpDataType = sourceFieldLists.get( $(this).val() );
				app.dataType.val( tmpDataType.toUpperCase() );				
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
						
			// mapping required checking
			if( key == "CHAR" ){
				app.mappingRequired.removeAttr( "disabled" );
			}else{
				app.mappingRequired.attr( "disabled", true );
				app.mappingRequired.removeAttr("checked");
				app.mappingRequired.change();
			}
			
			checkFixValueRequired();
			
			// checking data type to set length 
		    if( app.dataType.val() == "DATA_TYPE-DAT" ){
		    	app.length.attr("disabled", true);
		    	app.length.value( 8 );
		    }else{
		    	app.length.attr("disabled", false);
		    	//app.length.value( "" );
		    }

		});
		
		app.fixValueRequired.change(function(){
			if( $(this).attr("checked") == true ){
				app.fixValue.attr('disabled', false);
				app.fixValue.show();
				app.dummyFix.hide();
			}else{
				app.fixValue.attr('disabled', true);
				app.fixValue.hide();
				app.dummyFix.show();
			}			
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
		
		app.template.lookup({
			list:'@{Pick.udProfiles()}',
			get:{
				url:'@{Pick.getUdProfilePickByName()}',
				success: function(data){
					duplicateByName(data.name, data.actionType);
					app.profileRefError.html("");
				},
				error : function(data){
					lastDuplicatedBy = "";
					app.h_templete.value( "" );
					app.template.value("");
					app.process.value("");
					app.names.value("NOT FOUND");
				}
			},			
			filter:'Reconcile',
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
			if( $(this).attr("disabled") == true ){
				return false;
			}
			var noseq = $(this).attr("noseq");
			var currData = getDetailMap( noseq );
			var prevSeq = noseq - 1;
			// process only
			if( prevSeq >= 0 ){
				var prevData = getDetailMap( prevSeq );
				prevData.noSeq = noseq;
				addDetailMap( noseq, prevData );

				currData.noSeq = prevSeq;
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
			
			// #{if !confirming}
			additionalClass = "ui-button ui-widget ui-state-default ui-corner-all";
			// #{/if}
			// #{if confirming}
			disabledButton = "disabled";
			// #{/if}
			
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
			app.valueType2.attr("disabled", false);
			// fix length checking
			if( app.dataType.val() == "DATA_TYPE-STR" && app.formatData.val() == "FORMAT_TYPE-STR-1"){
				app.fixValueRequired.attr("disabled", false);
				// app.fixValueRequired.attr("checked", true);
				app.fixValueRequired.change();
				
			}else if( app.dataType.val() == "DATA_TYPE-NUM" || app.dataType.val() == "DATA_TYPE-DAT"){
				app.fixValueRequired.attr("disabled", false);
				// app.fixValueRequired.attr("checked", true);
				app.fixValueRequired.change();
				
				// when numeric, only mandatory is active
				if( app.dataType.val() == "DATA_TYPE-NUM" ){
					app.valueType1.attr("checked", true);
					app.valueType2.attr("disabled", true);					
				}
			}else{
				app.fixValueRequired.attr("disabled", true);
				app.fixValueRequired.attr("checked", false);
				app.fixValue.val("");
				app.fixValue.attr('disabled', true);
				app.fixValueRequired.change();
			}
		}
		
		function fillEntryDialogForm(bean){			
			app.profileDetailKey.value( bean.profileDetailKey );
			app.systemField.value( bean.systemField );
			app.sequenceNo.value( bean.noSeq );
			app.headerName.value( bean.targetField );
			
			// add current data
			var newOp = document.createElement( "option" );
		    newOp.value = bean.sourceField;
		    newOp.text = bean.sourceField;
		    app.sourceField[0].options.add( newOp );
		    app.sourceField.val( bean.sourceField );
		    
		    
		    // update data type
		    var tmpDataType = "";
		    if( bean.dataType.lookupDescription ){
		    	tmpDataType = bean.dataType.lookupId;
		    }
		    app.dataType.val( tmpDataType.toUpperCase() );
		    checkFixValueRequired();
		    
		    if( app.dataType.val() == "DATA_TYPE-DAT" ){
		    	app.length.attr("disabled", true);
		    	bean.length = 8;
		    }else{
		    	app.length.attr("disabled", false);
		    }
			app.length.value( bean.length );
			
			if( bean.fixValue.lookupId ){
				app.fixValue.val( bean.fixValue.lookupId );				
				app.fixValueRequired.attr("checked", true);
				app.fixValueRequired.change();
			}
			
			// see mapping code in action in href-tab-2.click
			if( bean.mappingRequired ){
				app.mappingRequired.attr( "checked", bean.mappingRequired );
				app.mappingRequired.change();
			}
			
			
			// fill mapping code tab
			
			var tmpNoSeq = app.sequenceNo.val();
			var bean = getDetailMap( tmpNoSeq );
			
			// mandatory
			app.valueType1.attr("checked", false);
			app.valueType2.attr("checked", false);
			app.valueType3.attr("checked", false);
			app.valueType1.attr("disabled", false);
			app.valueType2.attr("disabled", false);
			
			var tmp_ = bean.mandatory;
			if( tmp_ == 1 ){
				app.valueType1.attr("checked", true);
			}else if( tmp_ == 0 ){
				app.valueType2.attr("checked", true);					
			}else if( tmp_ == 2 ){
				app.valueType1.attr("disabled", true);
				app.valueType2.attr("disabled", true);
				app.valueType3.attr("checked", true);					
			}
						
			// #{if !confirming}
			$( "input.deletemappingbutton" ).button();
			
			// create a readonly popup when it is a system field
			if( bean.systemField == true ){
				app.headerName.disabled();
				app.sourceField.disabled();
				app.dataType.disabled();
				app.valueType1.disabled();
				app.valueType2.disabled();
				app.length.disabled();
				app.formatData.disabled();
				app.fixValueRequired.disabled();
				app.fixValue.disabled();
				app.mappingRequired.disabled();
								
			}else {
				app.headerName.enabled();
				app.sourceField.enabled();
				app.dataType.enabled();
				app.valueType1.enabled();
				app.valueType2.enabled();
				app.length.enabled();
				app.formatData.enabled();
			}
			
			// #{/if}
			// #{else}
			$( "input.deletemappingbutton" ).disabled();
			app.entryFormDialog.find( "input" ).disabled();
			app.entryFormDialog.find( "select" ).disabled();
			// #{/else}

			// update format data based on data type
			app.dataType.change();
			// set format data based on data in the bean
			app.formatData.val( bean.formatType.lookupId );

			// mapping datatable
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
			// #{if !confirming}
			$( "input.deletemappingbutton" ).button();
			// #{/if}
			// #{else}
			$( "input.deletemappingbutton" ).disabled();
			app.entryFormDialog.find( "input" ).disabled();
			app.entryFormDialog.find( "select" ).disabled();
			// #{/else}
			
		}
		
		function resetEntryDialogForm(){
			app.headerName.value( "" );
			
			// select first tab
			app.formTab.tabs('select', 0);

			var newNoSeq = detailMap.size();
			app.sequenceNo.value( newNoSeq );
			app.defaultValue.value( "" );
			
			// reset form
			app.headerName.value( "" );
			app.headerNameError.html( "" );
			app.length.value( "" );
			app.defaultValueError.html( "" );
			app.lengthError.html( "" );
			app.sourceField.value( "" );
			app.sourceFieldError.html( "" );
			app.mappingRequired.attr( "checked", false );
			app.mappingRequired.change();
			app.fixValue.val("");
			app.fixValueRequired.attr("checked", false);
			app.fixValueRequired.change();
			
			// get source field list
			getSourceFieldListByParent(app.h_templete.val());
			
		}
		function sourceFieldListBySourceHelper( fromPickData ){
			var allRows = fromPickData.rows;
			sourceFieldLists.clear();
			var totalOpts = app.sourceField[0].options.length;
			for( var idx = 0 ; idx < totalOpts ; idx++ ){
				app.sourceField[0].options.remove(0);
			}
			
			for( var idxa in allRows ){
				var curra = allRows[idxa];
				var currData = curra.data;
				
				/*
				 * No need this code anymore
				 * 
				 * 
				// check if already used
				if( $( "input.sourcefield[value=\""+currData[0]+"\"]" )[0] != undefined){
					
					// already there, no need to add
					continue;
				}
				*/
				sourceFieldLists.put( currData[0], currData[1] );
				
				// add new data
			    var newOp = document.createElement( "option" );
			    newOp.value = currData[0];
			    newOp.text = currData[0];
			    app.sourceField[0].options.add( newOp );
			}
			app.sourceField.change();
			app.dataType.change();			
		}
		
		function sourceFieldListByParentHelper( parentDetail ){
			var totalOpts = app.sourceField[0].options.length;
			for( var idx = 0 ; idx < totalOpts ; idx++ ){
				app.sourceField[0].options.remove(0);
			}
			
			for( var idxa in parentDetail ){
				var curra = parentDetail[idxa];
				var currData = curra.sourceField;
				var currType = ( curra.dataType.lookupId ? curra.dataType.lookupId : "");
				
				/*
				 * No need this code anymore
				 * 
				 * 

				// check if already used
				if( $( "input.sourcefield[value=\""+currData+"\"]" )[0] != undefined){
					// already there, no need to add
					continue;
				}
				*/
				sourceFieldLists.put( currData, currType );
				
				// add new data
			    var newOp = document.createElement( "option" );
			    newOp.value = currData;
			    newOp.text = currData;
			    app.sourceField[0].options.add( newOp );
			}
			app.sourceField.change();
			app.dataType.change();			
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

		function getSourceFieldListByParent(parentKey){
			if( parentKey == "" ){
				return;
			}
			if( sourceFieldDataByParent.get( parentKey ) != null ){
				sourceFieldListByParentHelper(sourceFieldDataByParent.get( parentKey ));				
			}else{
				$.post("@{getProfileDetail()}", {"profileKey":parentKey}, function(data) {
		    		checkRedirect(data);				
					sourceFieldDataByParent.put( parentKey, data );
					sourceFieldListByParentHelper(sourceFieldDataByParent.get( parentKey ) );
				});
			}
		}

		function duplicateByName(profilename, actionType){
			if( profilename+actionType == lastDuplicatedBy ){
				return true;
			}
			lastDuplicatedBy = profilename+actionType;
			$.post("@{duplicateByName()}", {'name':profilename, "actionType" : actionType}, function(data) {	
	    		checkRedirect(data);			
				if( app.key.val() != "" ){
					if( app.key.val() == data.profileKey ){
						app.h_templete.value( "" );
						app.template.value("");
						app.process.value("");
						app.names.value("");
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
				console.log( "try to get field list from parent :"+(data.profileKey ? data.profileKey : "(no parent)") );
				if( data.profileKey ){
					getSourceFieldListByParent( data.profileKey );
				}
				app.names.value( data.description );
				if( data.separator.lookupId ){
					app.separator.val( data.separator.lookupId );
				}
				// duplicate archive field/status and filters
				app.archiveField.value( data.archiveField );
				app.archiveStatus.value( data.archiveStatus );
				
				// clear master data
				detailMap.clear();
				
				// clear out html field also
				app.hiddenDetailInput.find("div").remove();
				
				for( var idx in details ){
					var currData = details[idx];
					// reset/set null the id to make it a new one to the child profile
					currData.profileDetailKey = null;
					// reset the mapping code also
					if( currData.mappingCode ){
						var tmpMappingCode = [];
						for( var idx2 in currData.mappingCode ){
							var currC_ = currData.mappingCode[idx2];
							currC_.mappingKey = null;
							tmpMappingCode.push( currC_ );
						}
						currData.mappingCode = tmpMappingCode;
					}
					addDetailMap( currData.noSeq, currData );
				}
				app.source.value( data.source );
				app.h_templete.value( data.profileKey );
				
				if( data.separator.lookupId ){
					app.separator.val( data.separator.lookupId );
				}
				
				if( data.includeHeader ){
					app.includeHeader.attr("checked", data.includeHeader);
				}
				
				// process
				app.process.value( data.process );
				
				// updating filter
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

						var lookupViewOperator = document.createElement("input");
						$(lookupViewOperator).attr("type", "hidden");
						$(lookupViewOperator).attr("name", tmpname+".lookupView");
						$(lookupViewOperator).attr("value", afilter.lookupView);
						app.divFilters.append( lookupViewOperator );

						var requiredOperator = document.createElement("input");
						$(requiredOperator).attr("type", "hidden");
						$(requiredOperator).attr("name", tmpname+".required");
						$(requiredOperator).attr("value", afilter.required);
						app.divFilters.append( requiredOperator );

						
						tmpc++;
					}
				}
				
				redrawTable();
			});
		}

		function addMappingCode(data){
			var keyCode = parseInt( data.profileDetail.noSeq );
			// check existence
			var tmpCode = new Hashtable();
			if( detailMapCode.get(keyCode) == null ){ // no data
				tmpCode.put(data.fromCode, data);
			}else{
				tmpCode = detailMapCode.get(keyCode);
				tmpCode.put(data.fromCode, data);
			}
			detailMapCode.put(keyCode, tmpCode);
			totalMappingCode++;
		}

		function addDetailMap(idx, data){
			
			detailMap.put( parseInt(idx), data );
			
			var divId = "div"+data.noSeq;
			app.hiddenDetailInput.find( "#"+divId ).remove();

			var divEl = document.createElement( "div" );			
			$(divEl).attr( "id" , divId);
			$(divEl).addClass("divhiddeninput");

			var partialDetailsName = "pdetails["+data.noSeq+"]";

			/**
			 * profile detail key
			 */
			if( data.profileDetailKey ){
				var profileDetailKeyEl = document.createElement( "input" );
				$(profileDetailKeyEl).attr("type", "hidden");
				$(profileDetailKeyEl).addClass("profileDetailKey");
				$(profileDetailKeyEl).attr("name", partialDetailsName+".profileDetailKey");
				$(profileDetailKeyEl).val( data.profileDetailKey );
				$(divEl).append( profileDetailKeyEl );				
			}
			/**
			 * end profile detail key
			 */
			
			/**
			 * system field
			 */
			var systemFieldEl = document.createElement( "input" );
			$(systemFieldEl).attr("type", "hidden");
			$(systemFieldEl).attr("name", partialDetailsName+".systemField");
			$(systemFieldEl).val( data.systemField );
			$(divEl).append( systemFieldEl );
			/**
			 * end system field
			 */

			/**
			 * no sequence
			 */
			var seqEl = document.createElement( "input" );
			$(seqEl).attr("type", "hidden");
			$(seqEl).attr("name", partialDetailsName+".noSeq");
			$(seqEl).val( data.noSeq );
			$(divEl).append( seqEl );
			/**
			 * end no sequence
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
			 * target field
			 */
			var targetEl = document.createElement( "input" );
			$(targetEl).attr("type", "hidden");
			$(targetEl).attr("name", partialDetailsName+".targetField");
			$(targetEl).val( data.targetField );
			$(divEl).append( targetEl );
			/**
			 * end target field
			 */
			
			/**
			 * source field
			 */
			var sourceEl = document.createElement( "input" );
			$(sourceEl).attr("type", "hidden");
			$(sourceEl).addClass("sourcefield");
			$(sourceEl).attr("name", partialDetailsName+".sourceField");
			$(sourceEl).val( data.sourceField );
			$(divEl).append( sourceEl );
			/**
			 * end source field
			 */
			
			/**
			 * Data Type
			 */
			
			if(data.dataType){
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
			}
			/**
			 * ENd Data type
			 */
			
			/**
			 * Format type
			 */
			if( data.formatType.lookupId ){
				var formatTypeEl = document.createElement( "input" );
				$(formatTypeEl).attr("type", "hidden");
				$(formatTypeEl).addClass("formatType");
				$(formatTypeEl).attr("name", partialDetailsName+".formatType.lookupId");
				$(formatTypeEl).val( data.formatType.lookupId );
				$(divEl).append( formatTypeEl );
				
				var formatTypeDesc = document.createElement( "input" );
				$(formatTypeDesc).attr("type", "hidden");
				$(formatTypeDesc).addClass("formatType");
				$(formatTypeDesc).attr("name", partialDetailsName+".formatType.lookupDescription");
				$(formatTypeDesc).val( data.formatType.lookupDescription );
				$(divEl).append( formatTypeDesc );				
			}
			/**
			 * end format type
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
			if( data.fixValue.lookupId ){
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
			}
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
					var partialMappingCodeName = "mappingCode["+totalMappingCode+"]";
					
					if( currC.mappingKey != null ){
						var mappingKeyEl = document.createElement( "input" );
						$(mappingKeyEl).attr("type", "hidden");
						$(mappingKeyEl).addClass("mappingcode");
						$(mappingKeyEl).addClass("mappingKey");
						$(mappingKeyEl).attr("name", partialMappingCodeName+".mappingKey");
						$(mappingKeyEl).val( currC.mappingKey );
						$(divEl).append( mappingKeyEl );						
					}
					
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

					var pdKeyEl = document.createElement( "input" );
					$(pdKeyEl).attr("type", "hidden");
					$(pdKeyEl).addClass("mappingcode");
					$(pdKeyEl).addClass("mappingprofileDetailKey");
					$(pdKeyEl).attr("name", partialMappingCodeName+".profileDetail.profileDetailKey");
					$(pdKeyEl).val( data.profileDetailKey );
					$(divEl).append( pdKeyEl );

					addMappingCode( currC );
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

			
			app.hiddenDetailInput.append( divEl );
			
		}
		
		function removeDetailMap(idx){
			detailMap.remove( parseInt( idx ) );
			app.hiddenDetailInput.find( "#div"+idx ).remove();
		}

		function getDetailMap(idx){
			return detailMap.get( parseInt( idx ) );
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


		function reOrderData( deletedIdx ){
			var detailMapTemp = new Hashtable();
			detailMap.each(function(idx, nextidx){
				var tmpIdx = idx;
				// reorder idx > deleted idx
				if( deletedIdx < idx ){
					tmpIdx = tmpIdx - 1;
				}
				var currData = getDetailMap( idx );
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
		/**
		 * end Function
		 */
		
		$( "#profileDetailTable_wrapper" );
		// #{if !confirming}
		$("input.deletedetailbutton").live("click", function(){
			if( $(this).attr("disabled") == true ){
				return false;
			}
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
				
			}else{
				app.dialog_message_delete.dialog({
					autoOpen:false,
					height:120,
					width:250,
					modal:true,
					resizable : false,
					buttons: {
						"Yes": function() {
							app.datatable.fnDeleteRow(rowNumber);
							removeDetailMap( parseInt(noseq) );
							// re order data after remove
							reOrderData(parseInt(noseq));
							redrawTable();
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
		// #{/if}
		
		// #{if profile?.templete != null}
		getSourceFieldListByParent( "${profile?.templete}" );
		// #{/if}

		// #{list profile?.details, as:'d'}
		// #{/list}
		// #{if detailMapJson != null}
		detailMapJson = ${detailMapJson};
		for( var idx in detailMapJson ){
			var cd = detailMapJson[idx];
			addDetailMap( idx, cd );
		}
		// #{/if}
		
		redrawTable();
		
		// #{if confirming}
		//$( "input.ordermappingbutton" ).disabled();
		// #{/if}
	}else{
		return new ProfileDownload(html);
	}
}