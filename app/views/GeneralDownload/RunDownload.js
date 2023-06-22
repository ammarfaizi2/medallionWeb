function RunDownload(html){
	if( this instanceof RunDownload ){
		
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		var table = null;
		var parent = this;
		var filterCounter = 0;
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.batchNo = app.batchNo.val();
			p.totalSuccess = app.totalSuccess.val();
			p.totalFail = app.totalFail.val();
			return p;
		};


/* =========================================================================== 
 * Function
 * ========================================================================= */
		
		function formatDataText(c) {
			c.live('change', function() {
				$(this).val($.trim($(this).val().toUpperCase()));
			});	
			
			c.live('blur', function() {
				$(this).val($.trim($(this).val().toUpperCase()));
			});	
			
			c.live('keydown', function(event) {
				//shift+5 = % -> prevent;
				//console.log(event);
				if (event.shiftKey && (event.which==53 || event.which==191)) {				
					event.preventDefault();
					return false;
				}
			});	

			c.live('keyup', function(event) {		
				//regex : /%/gi 
				if (event.shiftKey && (event.which==53 || event.which==191)) {
					$(this).val($(this).val().toUpperCase().replace(/%/gi,"").replace(/\?/gi,""));
					return false;
				}
			});	

		}
		
		function formatDataDate(c, dateMask, jqueryDateFormat) {
			c.mask(dateMask, { placeholder:" " });
			c.datepicker({dateFormat:jqueryDateFormat, yearRange: '1000:9999'});
			c.change( function(){
				var el = $(this);
				var dateVal = el.val();
				var id = this.id;
				var errorId= "#" + id + "Error";
				el.removeClass("fieldError");
				$(errorId).html("");
				try {
					var formatdate = jqueryDateFormat;
					var formatArr = formatdate.split("/");
					var valueArr = dateVal.split("/");
					if (formatArr.length == 3 && valueArr.length == 3) {
						var yearIdx = -1;
						for (x in formatArr) { if (formatArr[x] == 'yy') yearIdx = x; }
						if (yearIdx != -1) {
							var number = Number(valueArr[yearIdx]);
							if (number < 1 || number > 9999) { throw true; }
						}
					}
					
			        $.datepicker.parseDate(jqueryDateFormat, dateVal, null);
			    } catch(error) {
			    	el.addClass("fieldError");
			    	$(errorId).html("Invalid date format").show();
			    	el.val('');
			    }
			});

		}
		
		function createTextBox(td, data) {
			var d = $("<div>").appendTo(td);
			var id = "filter"+filterCounter;
			
			var c = $("<input id="+id+" class='capitalize' length=50 maxLength=50 kind='TEXT'>").appendTo(d);
			if (data.defValue) { c.val(data.defValue); }
			formatDataText(c);
			c.blur(function(){ td.data("value", c.val()); }).blur();
			
			var s = $("<span id="+id+"Error class='error validate' style='display:none'>Required</span>").appendTo(d);
			s.data("input", c).data("data", data);;
		}
		
		function createTextHidden(td, data) {
			var d = $("<div>").appendTo(td);
			var id = "filter"+filterCounter;
			
			var c = $("<input id="+id+" class='capitalize' length=50 maxLength=50 kind='HIDDEN'>").appendTo(d);
			if (data.defValue) { c.val(data.defValue); }
			formatDataText(c);
			c.blur(function(){ td.data("value", c.val()); }).blur();
			
			var s = $("<span id="+id+"Error class='error validate' style='display:none'>Required</span>").appendTo(d);
			s.data("input", c).data("data", data);;
		}
		
		function createNumber(td, data) {
			var d = $("<div>").appendTo(td);
			var id = "filter"+filterCounter;
			
			var c = $("<input id="+id+" class='capitalize' length=50 maxLength=50 kind='NUMBER'>").appendTo(d);
			if (data.defValue) { c.val(data.defValue); }
			c.autoNumeric({vMax: '999999999999999'});
			c.blur(function(){ td.data("value", c.val()); }).blur();
			
			var s = $("<span id="+id+"Error class='error validate' style='display:none'>Required</span>").appendTo(d);
			s.data("input", c).data("data", data);
		}
		
		function createDate(td, data, displayDateFormat, jQuerydisplayDateFormat, datemask) {
			var d = $("<div>").appendTo(td);
			var id = "filter"+filterCounter;
			
			var c = $("<input id="+id+" style='width:100px' class='calendar' kind='DATE'>").appendTo(d);
			if (data.defValue) { c.val(data.defValue); }
			formatDataDate(c, datemask, jQuerydisplayDateFormat);
			c.blur(function(){ td.data("value", c.val()); }).blur();
			c.change(function(){ td.data("value", c.val()); });

			d.append(" ("+displayDateFormat+")");
			
			var s = $("<span id="+id+"Error class='error validate' style='display:none'>Required</span>").appendTo(d);
			s.data("input", c).data("data", data);
		}
		
		function createSelect(td, data) {
			var id = "filter"+filterCounter;
			var d = $("<div>").appendTo(td);
			var c = $("<select id="+id+" kind='SELECT'>").appendTo(d);
			
			try{
				var result = html.fetch("@{Pick.pickSelect()}", {"id" : data.lookupView});
				if(data.defValue == undefined || data.defValue == null)
					var o = $("<option value=''>").appendTo(c);
				
				for (x in result.rows) {
					var row = result.rows[x];
					var selected = (jQuery.trim(row.data[0]) == data.defValue) ? "selected" : "";
					var o = $("<option "+selected+">").appendTo(c);
					o.attr("value", row.data[0]);
					o.html(row.data[1]);
				}
			}catch(err) { 
				messageAlertOk("Fail to fetch data", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
			}
			c.blur(function(){ td.data("value", c.val()); }).blur();
			
			var s = $("<span id="+id+"Error class='error validate' style='display:none'>Required</span>").appendTo(d);
			s.data("input", c).data("data", data);;
		}
		
		function createPicker(td, data) {
			var fieldname = data.fieldName;
			if (data.reference == undefined) data.reference = "";
			var id = "filter"+filterCounter;
			var c = $("<div>").appendTo(td);
			var i = $("<input id='"+id+"' name='"+fieldname+"' value='' type='text' class='capitalize' style='width:100px' kind='PICK' json='"+data.reference+"' >").appendTo(c);
			var b = $("<input id='"+id+"Help' class='small'  type='button' value='?'>").appendTo(c);
			var d = $("<input id='"+id+"Desc' type='text' disabled='disabled' value='' style='width:200px'>").appendTo(c);
			var h = $("<input id='"+id+"Key' type='hidden' >").appendTo(c);
			
			i.dynapopup(data.lookupView, '', 'separator');
			if (data.defValue) {
				var arrays = data.defValue.split(';');
				i.val(arrays[0]);
				if (arrays.length == 2) { d.val(arrays[1]); }
			}
			i.blur(function() { 
				td.data("value", i.val());
				var pointer = $(this);
				$("input[kind='PICK']", "#filterContainer").each(function() {
					var json = $(this).attr("json");
					if (json != undefined && json != '') {
						var ref = $.parseJSON(json);
						if (ref.field == pointer.attr("id")) {
							var refVal =  pointer.val();
							var id = $(this).attr('id');
							if (refVal == '' || refVal == 'ALL' || refVal == ' ALL') {
								
								$("#"+id).dynapopup(ref.pick, '', 'separator');
								$("#"+id).val('ALL');
								$("#"+id+"Desc").val(' ALL');
								$("#"+id+"Key").val('');
							}else{
								$("#"+id).dynapopup(ref.newpick, pointer.val(), 'separator');
								$("#"+id).val('ALL');
								$("#"+id).removeClass('fieldError')
								$("#"+id+"Desc").val(' ALL');
								$("#"+id+"Key").val('');
							}
						}
					}
				});				
			}).blur();
			
			var s = $("<span id="+id+"Error class='error validate' style='display:none'>Required</span>").appendTo(c);
			s.data("input", i).data("data", data);
			
		}
		
		/*function changeFundCode(){
			var filter = $('#filter0Key').val();
			$('#filter1').val("");
			$('#filter1Key').val("");
			$('#filter1Desc').val("");
			if(filter == null || filter == '' || filter.isEmpty() || filter == "" ||jQuery.trim(filter) == 'ALL'){
				$('#filter1').dynapopup('PICK_DWN_PRODUCT_FM_ALL', '', 'accountCode');
			} else {
				$('#filter1').dynapopup('PICK_DWN_PRODUCT_FM', filter, 'accountCode');
			}
		}*/
		
		function constactFilters() {
			var profileKey = app.updProfileKey.val();
			try{
				var result = html.fetch("@{GeneralDownload.fetchFilter()}", {"profileKey" : profileKey});
				var datas = result.filters;
				var displayDateFormat = result.displayDateFormat;
				
				//clean up
				var tbody = $("#filterContainer", html);
				tbody.html("");
				
				for (x in datas) {
					filterCounter = x;
					var data = datas[x];
					var tr = null;
					if (data.lookupDisplay == "DISPLAY_TYPE-HIDDEN") {
						tr = $("<tr style='display:none'>").appendTo(tbody);
					}else{
						tr = $("<tr>").appendTo(tbody);	
					}
					tr.data("prop", data);
					
					// tambah label
					var td = $("<td width='120px'>").appendTo(tr);
					var label = $("<label>").appendTo(td);
					var starReq = data.required ? "&nbsp;<span class='req'>*</span>" : ""; 
					label.html(data.fieldNameDesc + starReq);
					td.data("value", data.fieldName);
						
					// tambah sign
					var td = $("<td width='100px'>").appendTo(tr);
					var select = $("<select disabled='disabled' style='width:100%'>").appendTo(td);
					var option = $("<option>").appendTo(select);	
					var operator = data.defOperator.lookupId;
					if (operator == "OPERATOR_FILTER-EQ") { option.html("="); td.data("value", operator); }
					if (operator == "OPERATOR_FILTER-GT") { option.html(">"); td.data("value", operator); }
					if (operator == "OPERATOR_FILTER-GTE") { option.html(">="); td.data("value", operator); }
					if (operator == "OPERATOR_FILTER-IN") { option.html("!="); td.data("value", operator); }
					if (operator == "OPERATOR_FILTER-LT") { option.html("<"); td.data("value", operator); }
					if (operator == "OPERATOR_FILTER-LTE") { option.html("<="); td.data("value", operator); }
					if (operator == "OPERATOR_FILTER-INN") { option.html("IN"); td.data("value", operator); }
					
					// tambah component
					var td = $("<td>").appendTo(tr);
					var display = data.lookupDisplay;
					if (display == "DISPLAY_TYPE-TEXT") { createTextBox(td, data); }
					if (display == "DISPLAY_TYPE-HIDDEN") { createTextHidden(td, data); }
					if (display == "DISPLAY_TYPE-NUMBER") { createNumber(td, data); }
					if (display == "DISPLAY_TYPE-DATE") { createDate(td, data, result.displayDateFormat, result.jQuerydisplayDateFormat, result.dateMask); }
					if (display == "DISPLAY_TYPE-SELECT") { createSelect(td, data); }
					if (display == "DISPLAY_TYPE-PICK") { createPicker(td, data);}
					
					var td = $("<td>").appendTo(tr);
					td.data("value", data.dataType);
				}
			}catch(err) {
				messageAlertOk("Fail to constract Filter", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
			}
			app.accordionFilter.accordion("destroy").accordion({collapsible: true});
		}
		
		function fetchFilters() {
			var filters = [];
			var tbody = $("#filterContainer", html);
			$("tr", tbody).each(function(){
				var filter = "";
				var nilai = "";
				$("td", $(this)).each(function(i, e){
					if (filter != "") filter +=";";
					filter += $(this).data("value"); 
					if (i == 2) { nilai = $(this).data("value"); }
				});
				if (nilai != "") {
					filters[filters.length] = filter;
				}
			});
			
			return filters;
		}
		
		function linkFileHidden(condition, filename) {
			if (condition) {
				$("#downloadLink", html).hide();
			}else {
				$("#downloadLink", html).show();
				$("#downloadFile").attr("href", "@{downloadGeneratedFile()}?downloadfile="+filename)
			}
		}
		
		function buttonDisabled(condition) {
			app.populate.button("option", "disabled", condition);
			app.reset.button("option", "disabled", condition);
			app.saveToCsv.button("option", "disabled", condition);
//			app.generate.button("option", "disabled", condition);
		}
		
		function constractTable(data) {
			if (table != null) {
				table.fnDestroy();
				table.hide();
			}
			
			app.batchNo.val(data["BATCHID"]);
			app.saveToCsv.attr("href", "/generaldownload/processGenerateErrorFile?param.batchNo="+data["BATCHID"]+"&param.output=csv");
			
			var trHeader = $("thead tr", app.tablePopulate).html("");
			var headers = data["TARGET"];
			var sheaders = data["SOURCE"];
			var types = data["TYPE"];
			var keys = data["KEYS"];
			for (x in headers) {
				var align = "left";
				if (types[x].toLowerCase() == "number") { align = "right"; }
				if (types[x].toLowerCase() == "date") { align = "center"; }
					
				// todo nanti kalo jumlah kolom <? baru set widthnya
				trHeader.append("<th  field='"+keys[x]+"|string|none|"+align+"|nosort'>"+headers[x]+"</th>");
			}
			
			var options = {
				showLoading : "false"
			};
			
			table = app.tablePopulate.paging("@{GeneralDownload.previewPagingDownload()}", parent, function(p){
				app.totalRow.val(p.iTotalSuccess + p.iTotalFail);
				app.totalSuccess.val(p.iTotalSuccess);
				app.totalFail.val(p.iTotalFail);
				if (p.data.length > 0) {
					
					app.generate.button("option", "disabled", false);
					app.output.removeAttr("disabled");
				}else{
					
					app.generate.button("option", "disabled", true);
					app.output.attr("disabled", "disabled");
					linkFileHidden(true);
				}
				
				if (p.iTotalFail > 0){
					app.saveToCsv.show();
				} else {
					app.saveToCsv.hide();
				}
			}, options);
			table.show();
		}
		
		function validate() {
			var conUpdProfile = app.updProfileError.required(app.updProfile.isEmpty());
			var conTagError = true;
			
			$(".validate", app.accordionFilter).each(function(){
				var comp = $(this).data("input");
				var data = $(this).data("data");
				if (data.required) {
					var conUpdProfile = $(this).required(comp.isEmpty());
					conTagError = conTagError && conUpdProfile; 
				}
			});
			
			return conUpdProfile && conTagError;
		}
		
		function resetGUI() {
			linkFileHidden(true);
			app.saveToCsv.hide();
			
			if (table != null) {
				table.fnDestroy();
				table.hide();
				table = null;
			}
			
			constactFilters();
			
			app.batchNo.val("0");
			app.totalRow.val("0");
			app.totalSuccess.val("0");
			app.totalFail.val("0");
		}
		
		var allData = function() {
			$("#dialog-message").dialog('close');
			generateFile(false);
		}
		var onlySuccess = function() {
			$("#dialog-message").dialog('close');
			generateFile(true);
		}
		
		var closeDialogMessage = function() {
			$("#dialog-message").dialog('close');
			return false;
		}
		
		var generateFile = function(successOnly){
			linkFileHidden(true);
			var loadingDialog = html.loadingDialog().dialog('open');
			buttonDisabled(true);
			
			var param = {
				"batchNo" : app.batchNo.val(),
				"output" : app.output.val()
			};
			
			var result = html.fetch("@{GeneralDownload.processGenerateFile()}", {"param" : param, "uniqe":app.gDate.val(), "successOnly":successOnly}, function(data){
				if (data.status == 'SUCCESS') {
					linkFileHidden(false, data.filename);
				}
				if (data.status == 'FAIL') {
					messageAlertOk("Fail to generate file", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
				}
			});
			buttonDisabled(false);
			loadingDialog.dialog('close');
		}
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.updProfile.dynapopup('PICK_UD_PROFILE_ROLE', 'Download|{userkey}', 'includeHeaderRow', 
			function(data){
				if (data.includeHeader) { app.includeHeaderRow.attr("checked", "checked");	
				}else{ app.includeHeaderRow.removeAttr("checked"); }
				app.separator.val(data.separator.lookupId);
				
				console.log(data);
				if(data.filetype!=null && data.filetype!='') app.output.val(data.filetype);
				
				if(data.source == "VW_LKPBU")
				{
					if($("#output option").length > 4)
					{
						$("#output option[value='xml']").remove();
					}
				}
				else
				{
					if($("#output option").length < 5)
					{
						$('#output').append($("<option></option>").attr("value", "xml").text("xml"));
					}
				}
				
				resetGUI();
			},
			function(){ 
//				messageAlertOk("Fail to fetch data", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
			}
		);
		
		app.separator.children().eq(0).remove();
		
		app.output.children().eq(0).remove();
		
		app.accordionFilter.accordion({collapsible: false});
		
		app.populate.add(app.reset).add(app.saveToCsv).add(app.generate).button();

		app.generate.button("option", "disabled", true);
		app.output.attr("disabled", "disabled");
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.populate.click(function(){
			linkFileHidden(true);

			if (validate()){
				var loadingDialog = html.loadingDialog().dialog('open');
				buttonDisabled(true);
				try{
					var filters = fetchFilters();
					app.gDate.val((new Date()).getTime());
					var result = html.fetch("@{GeneralDownload.processRunDownload()}", {"profileId" : app.updProfileKey.val(), "filters":filters, "uniqe":app.gDate.val()});
					if (result["ERROR"]) {
						messageAlertOk(result["ERROR"], "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
					}else{
						constractTable(result);	
					}					
				}catch(err) {					
					messageAlertOk("Fail to populate data", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
				}
				buttonDisabled(false);
				loadingDialog.dialog('close');
			}
		});
		
		app.reset.click(function(){
			buttonDisabled(true);
			$("#form").attr('action', 'reset').submit();
			buttonDisabled(false);
		});
		
		app.generate.click(function(){
			if (validate()) {
				var nFailed = app.totalFail.val();
				if(Number(nFailed) > 0 ) {
					messageAlertYesNoCancel("Do you want generate all record ?", "ui-icon ui-icon-notice", "Confirmation Message", allData, onlySuccess, closeDialogMessage);
				} else {
					generateFile(false);
				}
			}
		});
	}else{
		return new RunDownload(html);
	}
}