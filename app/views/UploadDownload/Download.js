function Download(html) {
	if (this instanceof Download) {
/*================================================================== 
 * GUI Variable
 *================================================================== */

/*==================================================================
 * Function
 *==================================================================*/
		
		function defOperator(target, targetId){
			
			 $(target).lookup({
				list:'@{Pick.lookups()}?group=OPERATOR_FILTER',
				get:{
					url:'@{Pick.lookup()}?group=OPERATOR_FILTER',
					success: function(data){
						$(target).val(data.description);
						$(targetId).val(data.code);
					},
					error : function(data){
						$(target).addClass('fieldError');
						$(target).val('');
						$(targetId).addClass('fieldError');
						$(targetId).val('');
					}
				},
				help:$(target)
			});	
		}
		
		function myFunction(targetField, targetType){
			var fieldDefault = targetField.parent().parent().find("input.defaultValue");
			$(targetField).lookup({
				list:'@{Pick.tblNames()}?source='+$("#source").val(),
				get:{
					url:'@{Pick.tblName()}?source='+$("#source").val(),
					success: function(data){
						
						$(targetField).val(data.targetField);
						$(targetType).val(data.dataType);
						if( data.dataType == "DATE" || data.dataType == "Date" ){
							var formatKu = "${appProp.jqueryDateFormat}";
							fieldDefault.datepicker({dateFormat:formatKu});
						}
					},
					error : function(data){
						$(targetType).addClass('fieldError');
						$(targetType).val('');
						$(targetField).addClass('fieldError');
						$(targetField).val('');
					}
				},
				help:$(targetField)
			});	
		}
		
		function archiveFieldLookup(){
			var clickme = function() {
				var me = $(this);
				me.parent().remove();
				return false;
			};
			
			$("#archiveField").lookup({
				list:'@{Pick.tblNames()}?source='+$("#source").val(),
				get:{
					url:'@{Pick.tblName()}?source='+$("#source").val(),
					success: function(data){
						
						$("#archiveField").val("");
						var divoftext = $( "#divoftext" );
						var newDivTextLine = $("<div>");
						newDivTextLine.addClass( "textline" );
						var newInput = $("<input type=\"text\" name=\"archiveField[]\" value=\""+data.targetField+"\"/>");
						var newSpanHref = $("<a href=\"#\" class=\"closedivline\"><span style=\"padding-left:4px\">X</span></a>");
						newInput.appendTo( newDivTextLine );
						newSpanHref.appendTo( newDivTextLine );
						newSpanHref.bind("click", clickme);
						newDivTextLine.appendTo(divoftext);
					},
					error : function(data){
						$("#archiveField").addClass('fieldError');
						$("#archiveField").val('');
					}
				},
				help:$("#archiveHelp")
			});	
			
			$("a.closedivline").bind("click", clickme);
		}
		
		function autocomplete(pAutocomplete, pAjaxSource, pParamTag) {
			var vParamTag = pParamTag;
			var vAutoComplete = pAutocomplete.autocomplete({
				minLength: 0,
				source: function(request, response) {
//					 var term = request.term;
//					 if (term in cache) {
//						 response(cache[term]);
//						 return;
//					 }
					 
					 try{
						$.get(pAjaxSource, {'param':(vParamTag) ? vParamTag.val() : "", 'term' : request.term}, function(data) {
							checkRedirect(data);
							response(data);
						});
					 }catch(err) { alert(err); }
				},
				select: function(event, ui) {
					pAutocomplete.val(ui.item);
				}
			});
			
			pAutocomplete.focus(function(){
				vAutoComplete.autocomplete("search", "");
			});
		}
		
		function attachAutocomplete(vTr) {
			//var cellSource = $("td input", vTr).eq(3);
			var cellType = $("td input", vTr).eq(5);
			var cellFormat = $("td input", vTr).eq(6);
			
		    //var srcReadOnly = cellSource.attr("readonly");
			//if (!srcReadOnly) autocomplete(cellSource, "@{UploadDownload.sourceDownloadField()}", $("#source"));
			
			//var typeReadOnly = cellType.attr("readonly");
			//if (!typeReadOnly) autocomplete(cellType, "@{UploadDownload.typeData()}");
			
			var formatReadOnly = cellFormat.attr("readonly");
			if (!formatReadOnly) autocomplete(cellFormat, "@{UploadDownload.formatData()}", cellType);
		}
		
		function attachAutocompleteFilter(vTr) {
			
			/*var cellType = $("td input", vTr).eq(3);
			
			var typeReadOnly = cellType.attr("readonly");
			if (!typeReadOnly) autocomplete(cellType, "@{UploadDownload.typeData()}");
			*/
		}
		
		function reorderIndex() {
			$("#tblMapping tbody tr").each(function(i, e){
				var idx = i;
				$("td", $(e)).each(function(j, f){
					$("input", $(f)).each(function(k, g){
						var vInput = $(g); 
						var name = vInput.attr("name");
						
						if (vInput.hasClass("noSeq")) { vInput.val(idx); };
					});
				});
			});
		}
		
		function reorderIndexFilter() {
			$("#tblFilter tbody tr").each(function(i, e){
				var idx = i;
				$("td", $(e)).each(function(j, f){
					$("input", $(f)).each(function(k, g){
						var vInput = $(g); 						
						if (vInput.hasClass("noSeqFilter")) { vInput.val(idx); };				
					});
				});
			});
		}
		
		function attachRowbutton() {
			$("input[name='profile.delete']").unbind();
			$("input[name='profile.delete']").each(function(i, e){
				$(e).button();
				$(e).click(function(){
					var con = confirm("Are you sure want to delete row ?");
					if (con) $(e).parent().parent().remove();
					reorderIndex();
				});
			});
			
			$("input[name='profile.orderup']").unbind();
			$("input[name='profile.orderup']").each(function(i, e){
				$(e).button();
				$(e).click(function(){
					var curidx = $(e).parent().parent().index();
					if (curidx == 0) {
						var trDown = $("#tblMapping tbody tr").eq(curidx);
						var trUp = $("#tblMapping tbody tr").eq(curidx-1);
						trUp.after(trDown);
					}else{
						var trDown = $("#tblMapping tbody tr").eq(curidx);
						var trUp = $("#tblMapping tbody tr").eq(curidx-1);
						trDown.after(trUp);
					}
					reorderIndex();
				});
			});
		}
		
		function attachRowbuttonFilter() {
			$("input[name='profile.deleteF']").unbind();
			$("input[name='profile.deleteF']").each(function(i, e){
				$(e).button();
				$(e).click(function(){
					var con = confirm("Are you sure want to delete row ?");
					if (con) $(e).parent().parent().remove();
					reorderIndexFilter();
				});
			});
			
			$("input[name='profile.orderupF']").unbind();
			$("input[name='profile.orderupF']").each(function(i, e){
				$(e).button();
				$(e).click(function(){
					var curidx = $(e).parent().parent().index();
					if (curidx == 0) {
						var trDown = $("#tblFilter tbody tr").eq(curidx);
						var trUp = $("#tblFilter tbody tr").eq(curidx-1);
						trUp.after(trDown);
					}else{
						var trDown = $("#tblFilter tbody tr").eq(curidx);
						var trUp = $("#tblFilter tbody tr").eq(curidx-1);
						trDown.after(trUp);
					}
					reorderIndexFilter();
				});
			});
		}
		
		function readonDonly(tr) {
			var chk = $("input[type=checkbox]", tr);
			if (chk.attr("checked")) {
				$("td input", tr).each(function(i, e){
					if (i == 0) {
						$(e).attr("disabled", "disabled");
						$(e).addClass("ui-state-disabled");
					}else{
						$(e).attr("readonly", "readonly");
					}
				});
			}
		}
			
		function addDownloadRow(values) {
			if (values == null) { values = ["","","","","","","","0", 1]; }
			
			var tbody = $("#tblMapping tbody");
			var size = tbody.children().size();
			var tr = $("<tr>").appendTo(tbody);
			
			var tdDel = $("<td><input style='width:50px' type='button' name='profile.delete' value='Del'></input>" +
			          "<input style='width:50px;height:25px;display:none' type='checkbox' class=\"systemField\" name='pdetails["+size+"].systemField' "+values[0]+"></input>"+
			          "<input style='width:50px;height:25px;display:none' type='hidden' class=\"cdata\" name='pdetails["+size+"].cdata' value="+values[8]+"></input></td>").appendTo(tr);
			var tdNoSeq = $("<td><input style='width:20px;height:25px' class='noSeq' type='text' name='pdetails["+size+"].noSeq' value='"+values[1]+"' readOnly></input></td>").appendTo(tr);
			var tdSource = $("<td><input style='width:150px;height:25px' class=\"sourceField\" id='sourceIds["+size+"]' type='text' name='pdetails["+size+"].sourceField' value='"+values[2]+"' ></input></td>").appendTo(tr);
			var tdTarget = $("<td><input style='width:150px;height:25px' class=\"targetField\" type='text' name='pdetails["+size+"].targetField' value='"+values[3]+"'></input></td>").appendTo(tr);
			var tdType = $("<td><input style='width:75px;height:25px' type='text' class=\"dataType\" name='pdetails["+size+"].dataType' value='"+values[4]+"' readOnly></input></td>").appendTo(tr);
			var tdFormat = $("<td><input style='width:100px;height:25px' type='text' name='pdetails["+size+"].formatType' value='"+values[5]+"'></input></td>").appendTo(tr);
			var tdDefault = $("<td><input style='width:100px;height:25px' class=\"defaultValue\" type='text' name='pdetails["+size+"].defaultValue' value='"+values[6]+"'></input></td>").appendTo(tr);
			var tdLength = $("<td><input style='width:100px;height:25px' type='text' name='pdetails["+size+"].length' value='"+values[7]+"'></input></td>").appendTo(tr);
			var tdOrder = $("<td><input style='width:50px' type='button' name='profile.orderup' value='^'></input></td>").appendTo(tr);
			
			attachRowbutton();
			readonDonly(tr);
			attachAutocomplete(tr);
//			attachPopup(tdSource, tdType);
			
			myFunction(tdSource.find("input"), tdType.find("input"));
		}
		
		function addFilterRow(values) {
			if (values == null) { values = ["","","","","","",""]; }
			
			var tbody = $("#tblFilter tbody");
			var size = tbody.children().size();
			var tr = $("<tr>").appendTo(tbody);
			
			var tdDel = $("<td><input style='width:50px' type='button' name='profile.deleteF' value='Del'></input>").appendTo(tr);
			var tdNoSeq = $("<td><input style='width:20px;height:25px' class='noSeqFilter' type='text' name='pfilters["+size+"].noSeq' value='"+values[1]+"' readOnly></input></td>").appendTo(tr);
			var tdField = $("<td><input style='width:150px;height:25px' id='filterIds["+size+"]' class='filterField' type='text' name='pfilters["+size+"].fieldName' value='"+values[2]+"' readOnly>  </input></td>").appendTo(tr);
			var tdType = $("<td><input style='width:150px;height:25px' class=\"dataType\" type='text' name='pfilters["+size+"].dataType' value='"+values[3]+"' readOnly></input></td>").appendTo(tr);
			
			var fieldTdOperator = $("<td>").appendTo( tr );
			var fieldOperator = $("<input style='width:150px;height:25px' id='operatorIds["+size+"]' type='text' name='pfilters["+size+"].defOperator.lookupDescription' value='"+values[5]+"'></input>").appendTo(fieldTdOperator);
			var tdIdOperator = $("<input type='hidden' name='pfilters["+size+"].defOperator.lookupId' value='"+values[4]+"'></input>").appendTo(fieldTdOperator);
			
			var tdValue = $("<td><input class=\"defaultValue\" style='width:250px;height:25px' type='text' name='pfilters["+size+"].defValue' value='"+values[6]+"'></input></td>").appendTo(tr);
			var tdOrder = $("<td><input style='width:50px' type='button' name='profile.orderupF' value='^'></input></td>").appendTo(tr);
			
			attachRowbuttonFilter();
			readonDonly(tr);
			attachAutocompleteFilter(tr);
			myFunction(tdField.find("input"), tdType.find("input"));
			defOperator(fieldOperator, tdIdOperator);
		}
	
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/
/*================================================================== 
 * Event
 *================================================================== */
		
		$( ".sourceField" ).each(function(idx, el){
			var tdField = $(el);
			var tdType = tdField.parent().parent().find( "input.dataType" );
			myFunction(tdField, tdType);
		});
		
		$( ".filterField" ).each(function(idx, el){
			var tdField = $(el);
			var tdType = tdField.parent().parent().find( "input.dataType" );
			myFunction(tdField, tdType);
		});
		
		$( ".defaultOperator" ).each(function(idx, el){
			var tdField = $(el);
			var fieldLookupId = tdField.parent().find( ".defaultOperatorCode" );
			defOperator(tdField, fieldLookupId);
		});
		
		$( ".dataType" ).each(function(idx, el){
			var dataTypeVal = $(el).val();
			var defValField = $(el).parent().parent().find("input.defaultValue");
			if( dataTypeVal == "DATE" || dataTypeVal == "Date" ){
				var formatKu = "${appProp.jqueryDateFormat}";
				defValField.datepicker({dateFormat:formatKu});
			}
		});
		
		$("#actionType").children().eq(0).remove();
//		$("#templete").children().eq(0).remove();
		
		$("#btnMapping").click(function(){
			addDownloadRow();
			reorderIndex();
		});
		
		$("#btnField").click(function(){
			addFilterRow();
			reorderIndexFilter();
		});
		
		$("#btnPopulate").click(function(){
			
			var profileId = $("#templete").val();
			
			if (profileId == "") {

			}else{
				
				$.get("@{UploadDownload.duplicate()}", {'id':profileId}, function(data) {
					checkRedirect(data);
					//$("#name").val(data.name+"_new");
					//$("#description").val(data.description+"_new");
					$("#source").val(data.source);
					$("#h_source").val(data.source);
					$("#process").val(data.process);
					$("#h_process").val(data.process);							
					$("#filter1").val(data.filter1);
					$("#filter2").val(data.filter2);
					$("#separatorCsv").val(data.separatorCsv);
					$("#separatorTxt").val(data.separatorTxt);
					$("#tblMapping tbody").empty();
					$("#tblFilter tbody").empty();
					
					for (i in data.details) {
						var row = data.details[i];
						if (row.sourceField) { }else{ row.sourceField = ""; }
						if (row.targetField) { }else{ row.targetField = ""; }
						if (row.dataType) { }else{ row.dataType = ""; }
						if (row.formatType) { }else{ row.formatType = ""; }
						if (row.defaultValue) { }else{ row.defaultValue = ""; }
						if (row.length) { }else{ row.length = ""; }
						
						addDownloadRow([((row.systemField) ? "checked" : ""), row.noSeq, row.sourceField, row.targetField, row.dataType, row.formatType, row.defaultValue, row.length, row.cdata]);
					}
					
					for (i in data.filters) {
						var row = data.filters[i];
						row.defOperator = new Object();
						if (row.fieldName) { }else{ row.fieldName = ""; }
						if (row.dataType) { }else{ row.dataType = ""; }
						if (row.defOperator.lookupId) { }else{ row.defOperator.lookupId = ""; }
						if (row.defOperator.lookupDescription) { }else{ row.defOperator.lookupDescription = ""; }
						if (row.defValue) { }else{ row.defValue = ""; }
						
						addFilterRow(["",row.noSeq, row.fieldName, row.dataType,row.defOperator.lookupId,row.defOperator.lookupDescription, row.defValue]);
					}
					
				});
			}
			
		});
		
		$("#btnReset").click(function(){
			
			var profKey = $("#key").val();
			if(profKey == null || profKey == ""){
				location.href = '@{UploadDownload.downloadentry()}';
			}else{
				location.href='@{edit()}/' + profKey; 
			}
			
		});
		
		$("#tblMapping tbody tr").each(function(i, e){
			attachRowbutton();
			readonDonly($(e));
			attachAutocomplete($(e));
		});
		
		$("#tblFilter tbody tr").each(function(i, e){
			attachRowbuttonFilter();
			readonDonly($(e));
			attachAutocompleteFilter($(e));
		});
		
		$('input[name=includeHeader]').change(function(){
			$("input[name='profile.includeHeader']").val($("input[name='includeHeader']:checked").val());
		});
		$('input[name=status]').change(function(){
			$("input[name='profile.status']").val($("input[name='status']:checked").val());
		});
		
		$('input[name=archiving]').change(function(){
			$("input[name='profile.archiveStatus']").val($("input[name='archiving']:checked").val());
			if( $(this).val() === "true" ){
				$( "#divofarchive" ).show();
				$( "#archiveHelp" ).removeAttr("disabled");
			}else{
				$( "#divofarchive" ).hide();
				$( "#archiveHelp" ).attr("disabled", true);
			}
		});
		
		$('#templete').lookup({
			list:'@{Pick.udProfiles()}',
			get:{
				url:'@{Pick.udProfileByName()}',
				success: function(data){
					$('#templete').removeClass('fieldError');
					$('#templeteKey').val(data.profileKey);
					$('#h_templete').val(data.profileKey);
					$('#names').val(data.name);
					$('#h_names').val(data.name);
					
					var profileId = $("#h_names").val();
					$.post("@{UploadDownload.duplicateByName()}", {'name':profileId}, function(data) {
			    		checkRedirect(data);
						//$("#name").val(data.name+"_new");
						//$("#description").val(data.description+"_new");
						$("#source").val(data.source);
						$("#h_source").val(data.source);
						$("#process").val(data.process);
						
						$("#h_process").val(data.process);							
						$("#filter1").val(data.filter1);
						$("#filter2").val(data.filter2);
						$("#separatorCsv").val(data.separatorCsv);
						$("#separatorTxt").val(data.separatorTxt);
						$("#tblMapping tbody").empty();
						$("#tblFilter tbody").empty();
						
						for (i in data.details) {
							var row = data.details[i];
							if (row.sourceField) { }else{ row.sourceField = ""; }
							if (row.targetField) { }else{ row.targetField = ""; }
							if (row.dataType) { }else{ row.dataType = ""; }
							if (row.formatType) { }else{ row.formatType = ""; }
							if (row.defaultValue) { }else{ row.defaultValue = ""; }
							if (row.length) { }else{ row.length = ""; }
							addDownloadRow([((row.systemField) ? "checked" : ""), row.noSeq, row.sourceField, row.targetField, row.dataType, row.formatType, row.defaultValue, row.length, row.cdata]);
						}
						
						for (i in data.filters) {
							var row = data.filters[i];
							row.defOperator = new Object();
							if (row.fieldName) { }else{ row.fieldName = ""; }
							if (row.dataType) { }else{ row.dataType = ""; }
							if (row.defOperator.lookupId) { }else{ row.defOperator.lookupId = ""; }
							if (row.defOperator.lookupDescription) { }else{ row.defOperator.lookupDescription = ""; }
							if (row.defValue) { }else{ row.defValue = ""; }
							
							addFilterRow(["",row.noSeq, row.fieldName, row.dataType, row.defOperator.lookupId,row.defOperator.lookupDescription, row.defValue]);
						}
						archiveFieldLookup();
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
			
			filter:'Download',
			description:$('#names'),
			help:$('#groupTemplateHelp')
			
		});
		
		archiveFieldLookup();
				
	}else{
		return new Download(html);
	}
}