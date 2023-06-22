
function Upload(html) {
	if (this instanceof Upload) {
		/*================================================================== 
		 * GUI Variable
		 *================================================================== */
		var allRowSize = 0;
		/*==================================================================
		 * Function
		 *==================================================================*/
				function autocomplete(pAutocomplete, pAjaxSource, pParamTag, selectCallback) {
					var vParamTag = pParamTag;
					var vAutoComplete = pAutocomplete.autocomplete({
						minLength: 0,
						source: function(request, response) {							 
							 try{
								$.get(pAjaxSource, {'param':(vParamTag) ? vParamTag.val() : "", 'term' : request.term}, function(data) {
									checkRedirect(data);
									response(data);
								});
							 }catch(err) { alert(err); }
						},
						select: function(event, ui) {
							pAutocomplete.val(ui.item);
							if( typeof selectCallback === "function"){
								selectCallback.call( null, ui.item.value );
							}
						}
					});
					vAutoComplete.autocomplete("search", "");
//					pAutocomplete.focus(function(){
//						vAutoComplete.autocomplete("search", "");
//					});
				}
				
				function replaceIndexNumber(p_idname, p_idx){
					// check whether is is an array
					if( p_idname.indexOf("[") > -1 && p_idname.indexOf("]") > -1 ){										
						var tmpSplit1 = p_idname.split("[");
						var tmpSplit2 = p_idname.split("]");
						var newTmpElId = tmpSplit1[0]+"["+p_idx+"]"+tmpSplit2[1];
						return newTmpElId;
					}else{
						return p_idname;
					}
				}
				function getAllTargetField(){
					var allTargetField = "";
					$(".additionaltarget").each(function(idx, el){
						var me = $(el);
						if(allTargetField === ""){
							allTargetField = me.val();
						}else{
							allTargetField = allTargetField + ";" +me.val();
						}
						
					});
					$(".tabletarget").each(function(idx, el){
						var me = $(el);
						if(allTargetField === ""){
							allTargetField = me.val();
						}else{
							allTargetField = allTargetField + ";" +me.val();
						}
					});
					
					return allTargetField;
				}
				function reorderIndex(tableid) {
					var idx = 0;
					$("#tblMapping tbody tr").each(function(i, e){
						idx = i;
						$("td", $(e)).each(function(j, f){
							$("input", $(f)).each(function(k, g){
								var vInput = $(g);
								if (vInput.hasClass("noSeq")) { 
									vInput.val(idx); 
								};
								//if (vInput.hasClass("sourceField")) { vInput.val("Column_"+idx); };
								var tmpElId = vInput.attr("id");
								var tmpElName = vInput.attr("name");
								// if id exists
								if( tmpElId != "" ){
									var newTmpElId = replaceIndexNumber(tmpElId, idx);
									vInput.attr("id", newTmpElId);
								}
								// if name exists
								if( tmpElName != "" ){
									var newTmpElName = replaceIndexNumber(tmpElName, idx);
									vInput.attr("name", newTmpElName);
								}
							});
							$("select", $(f)).each(function(k, g){
								var vInput = $(g);
								//if (vInput.hasClass("sourceField")) { vInput.val("Column_"+idx); };
								var tmpElId = vInput.attr("id");
								var tmpElName = vInput.attr("name");
								// if id exists
								if( tmpElId != "" ){
									var newTmpElId = replaceIndexNumber(tmpElId, idx);
									vInput.attr("id", newTmpElId);
								}
								// if name exists
								if( tmpElName != "" ){
									var newTmpElName = replaceIndexNumber(tmpElName, idx);
									vInput.attr("name", newTmpElName);
								}
							});
						});
					});
					
					$("#tblAdditionalMapping tbody tr").each(function(i, e){
						idx++;
						$("td", $(e)).each(function(j, f){
							$("input", $(f)).each(function(k, g){
								var vInput = $(g); 
								if (vInput.hasClass("noSeq")) { vInput.val(idx); };
								
								var tmpElId = vInput.attr("id");
								var tmpElName = vInput.attr("name");
								// if id exists
								if( tmpElId != "" ){
									var newTmpElId = replaceIndexNumber(tmpElId, idx);
									vInput.attr("id", newTmpElId);
								}
								// if name exists
								if( tmpElName != "" ){
									var newTmpElName = replaceIndexNumber(tmpElName, idx);
									vInput.attr("name", newTmpElName);
								}
								
							});
						});
					});					
				}

				function initGeneralEvent(){
					
					// attach event listener for "Del" button
					$("input[name='profile.delete']").unbind();
					$("input[name='profile.delete']").live( "click", function(){
						var con = confirm("Are you sure want to delete row ?");							
						if (con) {						
							var currRow = $(this).parent().parent();
							var currSystemField =  $( "input.systemField", currRow ).attr("checked");
							var parentName = $(this).attr("tableparent");
							if( currSystemField === true ){
								var currDetailIdx = $( "input.detailIdx", currRow ).val();
								var currSeq = $( "input.noSeq", currRow ).val();
								var currTarget = $( "input.tabletarget", currRow ).val();
								var currDataType = $( "input.typedatalist", currRow ).val();
								var currFormatType = $( "input.formatType", currRow ).val();
								var currDefValue = $( "input.defaultValue", currRow ).val();
								var currLength = $( "input.length", currRow ).val();
								
								if( parentName === "tblAdditionalMapping" ){
									var currDetailIdx = $( "input.additionalDetailIdx", currRow ).val();
									var currTarget = $( "input.additionaltarget", currRow ).val();
									var currDataType = $( "input.additionalType", currRow ).val();
									var currFormatType = $( "input.additionalFormat", currRow ).val();
									var currDefValue = $( "input.additionalDefaultValue", currRow ).val();
									var currLength = $( "input.additionalLength", currRow ).val();
									addDownloadRow([currDetailIdx, ((currSystemField) ? "checked" : ""), currSeq, currTarget, currTarget, currDataType, currFormatType, currLength, currDefValue, 1]);
								}else if( parentName === "tblMapping" ){
									addAdditionalRow( [currDetailIdx, ((currSystemField) ? "checked" : ""), currSeq, currTarget, currDataType, currFormatType, currDefValue, currLength] );
								}
								
							}else{
								// do nothing
							}
							currRow.remove();
							reorderIndex();
						}
					} );
					
					// atach event listener to "^" / ordering button
					$("input[name='profile.orderup']").unbind();
					$("input[name='profile.orderup']").live( "click", function(){
						var parentId = $(this).attr( "tableparent" );
						var currTable = $("#"+parentId);
						var currTr = $(this).parent().parent();
						var curidx = currTr.index();
						if (curidx == 0) {
							var trDown = currTable.find("tbody tr").eq(curidx);
							var trUp = currTable.find("tbody tr").eq(curidx-1);
							trUp.after(trDown);
						}else{
							var trDown = currTable.find("tbody tr").eq(curidx);
							var trUp = currTable.find("tbody tr").eq(curidx-1);
							trDown.after(trUp);
						}
						reorderIndex();
					});
					
					// get table field list autcomplete
					$(".tabletarget").live( "focus", function(){
						var targetId = $(this).attr("id");
						var typeId = targetId.replace( "targetIds", "typeIds" );
						var formatId = targetId.replace( "targetIds", "formatIds" );
						var systemFieldId = targetId.replace( "targetIds", "fieldIds" );
						var mandatoryId = targetId.replace( "targetIds", "mandatoryIds" );
						var typeEl = document.getElementById(typeId);
						var formatEl = document.getElementById(formatId);
						var fieldEl = document.getElementById(systemFieldId);
						var mandatoryEl = document.getElementById(mandatoryId);
						var exceptionCols = getAllTargetField();
						
						$(this).lookup({
							list:'@{Pick.getColumnsBySource()}?profileKey='+$("#h_templete").val()+'&exceptionCols='+exceptionCols,
							get:{
							url:'@{Pick.getColumnBySource()}?profileKey='+$("#h_templete").val(),
								success: function(data){
									$("input", $(this)).val(data.targetField);
									$(typeEl).val(data.dataType);
									$(formatEl).val( data.formatType );
									$(mandatoryEl).val( data.mandatory );
									var defaultId = targetId.replace( "targetIds", "defaultIds" );
									var defaultIdElem = document.getElementById(defaultId);
									var defaultLength = formatId.replace( "formatIds", "defaultLengths" );
									var defaultLengthElem = document.getElementById( defaultLength  );
									if ($(typeEl).val() === 'NUMBER'){
										$(defaultLengthElem).removeAttr('readOnly');
										$( defaultIdElem ).datepicker('destroy');
										$(defaultIdElem).removeClass('capitalize');
										$(defaultIdElem).addClass('numberOnly');
									} else if ($(typeEl).val() === 'Date'){
										$(defaultIdElem).removeClass('capitalize');
										$(defaultIdElem).removeClass('numberOnly');
										$(defaultLengthElem).attr("readOnly", "readOnly");
										var formatKu = "";
										if ($(formatEl) === "yyyyMMdd") {
											formatKu = "yymmdd";
											$( defaultIdElem ).datepicker({dateFormat:formatKu});
										} else if ($(formatEl) === "dd/MM/yyyy") {
											formatKu = "dd/mm/yy";
											$( defaultIdElem ).datepicker({dateFormat:formatKu});
										} else if ($(formatEl) === "MM/dd/yyyy") {
											formatKu = "mm/dd/yy";
											$( defaultIdElem ).datepicker({dateFormat:formatKu});
										}
									}  else {
										$(defaultLengthElem).removeAttr('readOnly');
										$( defaultIdElem ).datepicker('destroy');
										$(defaultIdElem).removeClass('numberOnly');
									}
									
									// set checked value for system field
									if (data.systemField === true) {
										$(fieldEl).attr("checked");
									} else {
										if ($(fieldEl).attr("checked")) {
											$(fieldEl).removeAttr("checked");
										}
									}
									
									// disabled mandatory field for checked system field
									if ($(fieldEl).attr("checked")) {
										$(mandatoryEl).attr("disabled", "disabled");
									} else {
										if ($(mandatoryEl).attr("disabled")) {
											$(mandatoryEl).removeAttr("disabled");
										}
									}
								},
								error : function(data){
									$(typeEl).addClass('fieldError');
									$(typeEl).val('');
									$(this).addClass('fieldError');
									$(this).val('');
								}
							},
							filter:'Upload',
								help:$(this)
						});						
					} );
					

					// get table field for variable list autocomplete
					$(".additionaltarget").live( "focus", function(){
						var targetId = $(this).attr("id");
						var typeId = targetId.replace( "targetVarIds", "typeVarIds" );
						var typeEl = document.getElementById(typeId);
						var exceptionCols = getAllTargetField();
						
						var formatId = targetId.replace( "targetVarIds", "formatVarIds" );
						var formatEl = document.getElementById(formatId);
						
						$(this).lookup({
								list:'@{Pick.getColumnsBySource()}?profileKey='+$("#h_templete").val()+'&exceptionCols='+exceptionCols,
								get:{
									url:'@{Pick.getColumnBySource()}?profileKey='+$("#h_templete").val(),
									success: function(data){
										
										$("input", $(this)).val(data.targetField);
										$(typeEl).val(data.dataType);
										$(formatEl).val( data.formatType );
										var defaultVarsId = targetId.replace( "targetVarIds", "defaultVarIds" );
										var defaultVarsIdElem = document.getElementById(defaultVarsId);
										if ($(typeEl).val() === 'NUMBER'){
											$( defaultVarsIdElem ).datepicker('destroy');
											$(defaultVarsIdElem).removeClass('capitalize');
											$(defaultVarsIdElem).addClass('numberOnly');
										} else if ($(typeEl).val() === 'Date'){
											$(defaultVarsIdElem).removeClass('capitalize');
											$(defaultVarsIdElem).removeClass('numberOnly');
											var formatKu = "";
											if ($(formatEl).val() === "yyyyMMdd") {
												formatKu = "yymmdd";
												$( defaultVarsIdElem ).datepicker({dateFormat:formatKu});
											} else if ($(formatEl).val() === "dd/MM/yyyy") {
												formatKu = "dd/mm/yy";
												$( defaultVarsIdElem ).datepicker({dateFormat:formatKu});
											} else if ($(formatEl).val() === "MM/dd/yyyy") {
												formatKu = "mm/dd/yy";
												$( defaultVarsIdElem ).datepicker({dateFormat:formatKu});
											}
										} else {
											$(defaultVarsIdElem).removeClass('numberOnly');
											$( defaultVarsIdElem ).datepicker('destroy');
										}
									},
									error : function(data){
										$(typeEl).addClass('fieldError');
										$(typeEl).val('');
										$(this).addClass('fieldError');
										$(this).val('');
									}
								},
								filter:'Upload',
								help:$(this)
							});						
					} );
										
					// get type data autcomplete
					/*
					$(".typedatalist").live( "focus", function(){
						var typeReadOnly = $(this).attr("readonly");
						if (!typeReadOnly) autocomplete($(this), "@{UploadDownload.typeData()}");
					});
					*/
					
					$(".formatdatalist").live( "focus", function(idx, e){
						var formatId = $(this).attr("id");
						var typeId = formatId.replace( "formatIds", "typeIds" );
						var formatReadOnly = $(this).attr("readonly");
						var typeIdEl = $( document.getElementById( typeId  ) );
						//if (!formatReadOnly) {
							autocomplete($(this), "@{UploadDownload.formatData()}", typeIdEl, function(data){
								var defaultId = formatId.replace( "formatIds", "defaultIds" );
								var defaultIdElem = $( document.getElementById( defaultId  ) );
								var defaultLength = formatId.replace( "formatIds", "defaultLengths" );
								var defaultLengthElem = $( document.getElementById( defaultLength  ) );
								var formatKu = "";
								if (data === "Uppercase") {
									defaultLengthElem.removeAttr('readOnly');
									defaultIdElem.removeClass('hasDatepicker');
									defaultIdElem.addClass('capitalize');
									defaultIdElem.val( defaultIdElem.val().toUpperCase() );
								} else if (data === "Lowercase") {
									defaultLengthElem.removeAttr('readOnly');
									defaultIdElem.removeClass('hasDatepicker');
									defaultIdElem.removeClass('capitalize');
									defaultIdElem.val( defaultIdElem.val().toLowerCase() );
								} else if (data === "yyyyMMdd") {
									defaultIdElem.removeClass('hasDatepicker');
									defaultIdElem.removeClass('capitalize');
									formatKu = "yymmdd";
									defaultIdElem.datepicker({dateFormat:formatKu});
									defaultLengthElem.attr("readOnly", "readOnly");
								} else if (data === "dd/MM/yyyy") {
									defaultIdElem.removeClass('hasDatepicker');
									defaultIdElem.removeClass('capitalize');
									formatKu = "dd/mm/yy";
									defaultIdElem.datepicker({dateFormat:formatKu});
									defaultLengthElem.attr("readOnly", "readOnly");
								} else if (data === "MM/dd/yyyy") {
									defaultIdElem.removeClass('hasDatepicker');
									defaultIdElem.removeClass('capitalize');
									formatKu = "mm/dd/yy";
									defaultIdElem.datepicker({dateFormat:formatKu});
									defaultLengthElem.attr("readOnly", "readOnly");
								} else {
									defaultLengthElem.removeAttr('readOnly');
									defaultIdElem.removeClass('hasDatepicker');
									defaultIdElem.removeClass('capitalize');
								}
								if (typeIdEl.val() === "Date") {
									var currDate = defaultIdElem.datepicker("getDate");
									var formattedDate = $.datepicker.formatDate( formatKu, currDate );
									defaultIdElem.val(formattedDate);
								}
							});
						//}

					});
					
					// get format data based on the type data
					$(".formatvardatalist").live( "focus", function(){
						var formatId = $(this).attr("id");
						var typeId = formatId.replace( "formatVarIds", "typeVarIds" );
						var typeIdEl = $( document.getElementById( typeId  ) );
						autocomplete($(this), "@{UploadDownload.formatData()}", typeIdEl, function(data){
							var defaultId = formatId.replace( "formatVarIds", "defaultVarIds" );
							var defaultIdElem = $( document.getElementById( defaultId  ) );
							var formatKu = "";
							if (data === "Uppercase") {
								defaultIdElem.removeClass('hasDatepicker');
								defaultIdElem.addClass('capitalize');
								defaultIdElem.val( defaultIdElem.val().toUpperCase() );
							} else if (data === "Lowercase") {
								defaultIdElem.removeClass('hasDatepicker');
								defaultIdElem.removeClass('capitalize');
								defaultIdElem.val( defaultIdElem.val().toLowerCase() );
							} else if (data === "yyyyMMdd") {
								defaultIdElem.removeClass('hasDatepicker');
								defaultIdElem.removeClass('capitalize');
								formatKu = "yymmdd";
								$( defaultIdElem ).datepicker({dateFormat:formatKu});
							} else if (data === "dd/MM/yyyy") {
								defaultIdElem.removeClass('hasDatepicker');
								defaultIdElem.removeClass('capitalize');
								formatKu = "dd/mm/yy";
								$( defaultIdElem ).datepicker({dateFormat:formatKu});
							} else if (data === "MM/dd/yyyy") {
								defaultIdElem.removeClass('hasDatepicker');
								defaultIdElem.removeClass('capitalize');
								formatKu = "mm/dd/yy";
								$( defaultIdElem ).datepicker({dateFormat:formatKu});
							} else {
								defaultIdElem.removeClass('hasDatepicker');
								defaultIdElem.removeClass('capitalize');
							}
							if (typeIdEl.val() === "Date") {
								var currDate = defaultIdElem.datepicker("getDate");
								var formattedDate = $.datepicker.formatDate( formatKu, currDate );
								defaultIdElem.val(formattedDate);
							}
						});

					});
					
					// get list of stock price value 
					$(".defaultvalstockprice").live( "focus", function(){
						var targetId = $(this).attr("id");
						var targetIdEl = $( document.getElementById( targetId  ) );
						autocomplete($(this), "@{UploadDownload.securityPriceGroupList()}", targetIdEl, function(data){
							
						});

					});
					
					// default value in variable mapping
					$("#tblAdditionalMapping tbody tr").each(function(i, e){
						$("td", $(e)).each(function(j, f){
							$("input", $(f)).each(function(k, g){
								var vInput = $(g);
								if ((vInput.hasClass('additionaltarget')) && (vInput.val() === 'SECURITY_PRICE_GROUP')) {
									var tmpTargetId = vInput.attr("id");
									var tmpDefaultVarId = tmpTargetId.replace("targetVarIds", "defaultVarIds");
									var defaultVarEl = document.getElementById(tmpDefaultVarId);
									//$( defaultVarEl ).attr('readOnly', 'readOnly');
								}
							});
						});
					});
					
					
					// validate
					$(".validateValue").live("focus", function(){
						autocomplete($(this), "@{UploadDownload.validateData()}");
					});

				}
				
				function readonDonly(tr) {
					var chk = $("input[type=checkbox]", tr);
					if (chk.attr("checked")) {
						$("td input", tr).each(function(i, e){
							if (i == 0) {
								$(e).hide();
							}else{
								$(e).attr("readonly", "readonly");
							}
						});
					}
				}
					
				function addDownloadRow(values) {
					if (values == null) { values = ["","","","","","","","","0", "TRUE"]; }
					
					var tbody = $("#tblMapping tbody");
					if( allRowSize === 0 ){
						allRowSize = tbody.children().size();
					}else{
						allRowSize++;
					}
					var size = allRowSize;
					var tr = $("<tr>").appendTo(tbody);
					
					var tdDel = $("<td><input type='hidden' class='detailIdx' id='detailKey["+size+"]' name='pdetails["+size+"].profileDetailKey' value='"+values[0]+"'></input>"+
									  "<input style='width:50px' type='button' name='profile.delete' class='buttons ui-button ui-widget ui-state-default ui-corner-all' tableparent='tblMapping' value='Del'></input>" +
							          "<input style='width:50px;height:25px;display:none' type='checkbox' id='fieldIds["+size+"]' class='systemField' name='pdetails["+size+"].systemField' "+values[1]+"></input></td>").appendTo(tr);
					var tdNoSeq = $("<td><input style='width:20px;height:25px' class='noSeq' type='text' name='pdetails["+size+"].noSeq' value='"+values[2]+"' readOnly></input></td>").appendTo(tr);
					var tdSource = $("<td><input style='width:150px;height:25px' class='sourceField capitalize' type='text' name='pdetails["+size+"].sourceField' value='"+values[3]+"' >  </input></td>").appendTo(tr);
					var tdTarget = $("<td><input style='width:150px;height:25px' class='tabletarget' id='targetIds["+size+"]' type='text' name='pdetails["+size+"].targetField' value='"+values[4]+"' readonly></input></td>").appendTo(tr);
					var tdType = $("<td><input style='width:75px;height:25px' id='typeIds["+size+"]' type='text' class='typedatalist' name='pdetails["+size+"].dataType' value='"+values[5]+"' readOnly></input></td>").appendTo(tr);
					var tdFormat = $("<td><input style='width:100px;height:25px' type='text' id='formatIds["+size+"]' class='formatType formatdatalist' name='pdetails["+size+"].formatType' value='"+values[6]+"' readonly></input></td>").appendTo(tr);
					var tdLength = $("<td><input style='width:100px;height:25px' class='length numberOnly' id='defaultLengths["+size+"]' type='text' name='pdetails["+size+"].length' value='"+values[7]+"'></input></td>").appendTo(tr);
					var tdDefault = $("<td><input style='width:100px;height:25px' id='defaultIds["+size+"]' type='text' class='defaultValue' name='pdetails["+size+"].defaultValue' value='"+values[8]+"'></input></td>").appendTo(tr);
					var tdMO = $('<td><select name="pdetails['+size+'].mandatory" id="mandatoryIds['+size+']">'+
					'	<option value="TRUE" '+( values[9] === true ? "selected" : "" )+'>MANDATORY</option>'+
					'	<option value="FALSE" '+( values[9] === false ? "selected" : "" )+'>OPTIONAL</option>'+
					'</select></td>').appendTo(tr);
					
					var tdOrder = $("<td><input style='width:50px' type='button' name='profile.orderup' class='buttons ui-button ui-widget ui-state-default ui-corner-all' tableparent='tblMapping' value='^'></input></td>").appendTo(tr);
					
					//readonDonly(tr);
					
					if (values[5] === "NUMBER") {
						$("input", tdDefault).addClass('numberOnly');
						
						$("input", tdLength).removeAttr('readOnly');
						
					} else if (values[5] === "Date") {
						$("input", tdDefault).removeClass('numberOnly');
						var lengthMe = $("input", tdLength);
						var me = $("input", tdDefault);
						var formatKu = "";
						$( lengthMe ).attr("readOnly", "readOnly");
						
						if( values[6] === "yyyyMMdd" ){
							formatKu = "yymmdd";
						} else if ( values[6] === "dd/MM/yyyy" ) {
							formatKu = "dd/mm/yy";
						} else if ( values[6] === "MM/dd/yyyy" ) {
							formatKu = "mm/dd/yy";
						}
						$( me ).datepicker({dateFormat:formatKu});
					} else {
						$("input", tdDefault).removeClass('numberOnly');
						
						$("input", tdLength).removeAttr('readOnly');
					}
					
					if (values[1] === 'checked') {
						$("select", tdMO).attr("disabled", "disabled");
						tdMO.append( "<input type='hidden' name='pdetails["+size+"].mandatory' value='"+values[9]+"'>" );
					} else {
						if ($("select", tdMO).attr("disabled")) {
							$("select", tdMO).removeAttr("disabled");
						}
					}
				}
				
				function addAdditionalRow(values) {
					if (values == null) { values = ["","","","","","", "", "100"]; }
					var tbody = $("#tblAdditionalMapping tbody");
					if( allRowSize === 0 ){
						varRowSize = tbody.children().size();
					}else{
						allRowSize++;
					}
					
					var processType = $('#process').val();
					
					if ((processType !== '') && (processType != null))  {
						$('#templeteError').html('');
					} else {
						$('#templeteError').html('Profile Ref is required for generating Additional default value.');
					}
					
					var size = allRowSize;
					
					var tr = $("<tr>").appendTo(tbody);
					
					var tdDel = $("<td><input type='hidden' class='additionalDetailIdx' id='detailVarKey["+size+"]' name='pdetails["+size+"].profileDetailKey' value='"+values[0]+"'></input>"+
									  "<input style='width:50px' type='button' name='profile.delete' class='buttons ui-button ui-widget ui-state-default ui-corner-all' tableparent='tblAdditionalMapping' value='Del'></input>" +
							          "<input style='width:50px;height:25px;display:none' class='systemField' type='checkbox' name='pdetails["+size+"].systemField' "+values[1]+"></input>"+
							          "<input style='width:150px;height:25px;display:none' class='sourceField' type='text' name='pdetails["+size+"].sourceField' value='"+values[3]+"' >  </input></td>").appendTo(tr);
					var tdNoSeq = $("<td><input style='width:20px;height:25px' class='noSeq' type='text' name='pdetails["+size+"].noSeq' value='"+values[2]+"' readOnly></input></td>").appendTo(tr);
					var tdTarget = $("<td><input style='width:150px;height:25px' class='additionaltarget' id='targetVarIds["+size+"]' type='text' name='pdetails["+size+"].targetField' value='"+values[3]+"' readonly></input></td>").appendTo(tr);
					var tdType = $("<td><input style='width:75px;height:25px' class='additionalType' type='text' id='typeVarIds["+size+"]' name='pdetails["+size+"].dataType' value='"+values[4]+"' readOnly></input></td>").appendTo(tr);
					var tdFormat = $("<td><input style='width:100px;height:25px' id='formatVarIds["+size+"]' class='additionalFormat formatvardatalist' type='text' name='pdetails["+size+"].formatType' value='"+values[5]+"' readonly></input></td>").appendTo(tr);					
					var tdDefaultValue = $("<td><input style='width:150px;height:25px' id='defaultVarIds["+size+"]' class='additionalDefaultValue defaultvalstockprice' type='text' name='pdetails["+size+"].defaultValue' value='"+values[6]+"'></input></td>").appendTo(tr);					
					var tdHidden = $("<td><input type='hidden' name='pdetails["+size+"].sourceType' value='VARIABLE'/><input type='hidden' class='additionalLength' name='pdetails["+size+"].length' value='"+values[7]+"'/></td>").appendTo(tr);					
					
					//readonDonly(tr);
					
					if (values[4] === "NUMBER") {
						$("input", tdDefaultValue).addClass('numberOnly');
					} else if (values[4] === "Date") {
						$("input", tdDefaultValue).removeClass('numberOnly');
						var me = $("input", tdDefaultValue);
						var formatKu = "";
						if( values[5] === "yyyyMMdd" ){
							formatKu = "yymmdd";
						} else if ( values[5] === "dd/MM/yyyy" ) {
							formatKu = "dd/mm/yy";
						} else if ( values[5] === "MM/dd/yyyy" ) {
							formatku = "mm/dd/yy";
						}
						$( me ).datepicker({dateFormat:formatKu});
					}  else {
						// assume this type is String
						$("input", tdDefaultValue).removeClass('numberOnly');
						
						if (processType === 'Market Price') {
							if (values[3] === 'SECURITY_PRICE_GROUP') {
								var defaultValue = $("input", tdDefaultValue);
								$( defaultValue ).removeAttr('autocomplete');
								//$( defaultValue ).attr('readOnly', 'readOnly');
								$( defaultValue ).addClass('defaultvalstockprice');
								$( defaultValue ).attr("readOnly", "readOnly");
							} else {
								$( defaultValue ).attr('autocomplete', 'off');
								$( defaultValue ).removeClass('defaultvalstockprice');
							}
						}
					}
				}
		/*==================================================================
		 * Declare popup tabel /detail
		 * =================================================================*/
		/*================================================================== 
		 * Event
		 *================================================================== */
				
				$("#actionType").children().eq(0).remove();
//				$("#templete").children().eq(0).remove();
				
				
				$("#btnReset").click(function(){
					$( "#tblMapping" ).find("tbody tr").remove();
					$( "#tblAdditionalMapping" ).find("tbody tr").remove();
					
					//manual reset the field
					$( ".leftEntry input,select" ).each( function( idx, el){
						var tmpType = $(this).attr("type");
						var tmpId = $(this).attr("id");
						if( tmpType !== "button" && tmpType !== "radio"){
							if( tmpId === "name" || tmpId === "h_name"){
								if( $("#key").val() === "" ) {
									$(this).value( "" );
								}
							}else{
								$(this).value( "" );
							}
						}
					} );
					
					$(".innerdesc").html("");
					// end manual reset
					return false;
				});
				
				$("#btnMapping").click(function(){
					addDownloadRow();
					reorderIndex();
				});
				$("#btnAdditionalMapping").click(function(){
					addAdditionalRow();
					reorderIndex();
				});
				
				$("#btnPopulate").click(function(){
					
					var profKey = $("#key").val();
					if(profKey == null || profKey == ""){
						location.href = '@{UploadDownload.uploadentry()}';
					}else{
						location.href='@{edit()}/' + profKey; 
					}
				});
				
				/*
				$("#tblMapping tbody tr").each(function(i, e){					
					readonDonly($(e));
				});
				*/
				
//				$('input[name=includeHeader]').change(function(){
//					$("input[name='profile.includeHeader']").val($("input[name='includeHeader']:checked").val());
//				});
//				$('input[name=status]').change(function(){
//					$("input[name='profile.status']").val($("input[name='status']:checked").val());
//				});
				
				$('#templete').lookup({
					list:'@{Pick.udProfiles()}',
					get:{
						url:'@{Pick.udProfileByName()}',
						success: function(data){
							$('#templete').removeClass('fieldError');
							$('#templeteKey').val(data.name);
							$('#names').val(data.description);
							$('#h_names').val(data.description);
							
							var profileName = $("#templete").val();
												
							$.get("@{UploadDownload.duplicateByName()}", {'name':profileName}, function(data) {
								checkRedirect(data);
								$("#source").val(data.source);
								$("#h_templete").val(data.profileKey);
								$("#h_source").val(data.source);
								$("#process").val(data.process);
								$("#h_process").val(data.process);
								$("#separatorCsv").val(data.separatorCsv);
								$("#separatorTxt").val(data.separatorTxt);
								$("#fileType").val(data.fileType);
								$("#filePrefix").val(data.filePrefix);
								$("#tblMapping tbody").empty();
								$("#tblAdditionalMapping tbody").empty();
								$("#tblFilter tbody").empty();
								if( data.processDescription ){
									$( ".innerdesc" ).html(data.processDescription);
								}else{
									$( ".innerdesc" ).html("");
								}
								for (i in data.details) {
									var row = data.details[i];
									if (row.sourceField) { }else{ row.sourceField = ""; }
									if (row.targetField) { }else{ row.targetField = ""; }
									if (row.dataType) { }else{ row.dataType = ""; }
									if (row.formatType) { }else{ row.formatType = ""; }
									if (row.defaultValue) { }else{ row.defaultValue = ""; }
									if (row.length) { }else{ row.length = ""; }
									if (row.validateValue) { }else{ row.validateValue = ""; }
									
									if(row.sourceType === "FILE"){
										addDownloadRow(["", ((row.systemField) ? "checked" : ""), row.noSeq, row.sourceField, row.targetField, row.dataType, row.formatType, row.length, row.defaultValue, row.mandatory]);
									}else if(row.sourceType === "VARIABLE"){
										addAdditionalRow(["", ((row.systemField) ? "checked" : ""), row.noSeq, row.targetField, row.dataType, row.formatType, row.defaultValue, row.length]);										
									}
										
									
								}
								
							});
							
						},
						error : function(data){
							$('#templete').addClass('fieldError');
							$('#templete').val('');
							$('#templeteKey').val('');
							$('#names').val('NOT FOUND');
							$('#h_names').val('');
						}
					},
					
					filter:'Upload',
					description:$('#names'),
					help:$('#groupTemplateHelp')
				});
				
				// initialization
				initGeneralEvent();
				
	}else{
		return new Upload(html);
	}
}