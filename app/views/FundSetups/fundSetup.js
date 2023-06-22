$(function(){
	$('#account').dynapopup('PICK_CF_MASTER', '', 'fundManagerCode');
	
	var data = new Object();
	tableModelPublishSchedule(data);
	tableInvestment(data);
	$('.calendar').datepicker();
	$('.buttons button').button();
	$('.buttons input:button').button();
	var closeDialog = function() {
		$("#dialog-message").dialog('close');	
	}
	
	if (('${mode}'=='entry') || (('${mode}'=='edit') && (('${faMaster?.recordStatus?.decodeStatus()}' == 'Reject') || ($("#status").val() == 'R' )))) {
		$('input[name=isActive]').attr("disabled", "disabled");
	}
	
	$('input[name=isActive]').change(function(){
		$("input[name='faMaster.isActive']").val($("input[name='isActive']:checked").val());
	});
	
	$("#dialog-message-1").css("display","none");
	$('#tabsInvestmentModel').tabs();
	
	/*$('#viewCalender').click(function(){
		alert("view");
		var action="@{viewCalender()}?year="+$('#year').val()+"&key="+''+"";
		$('#viewCal').attr('action', action);
		$('#viewCal').submit();
	})*/
	//VIEW CALENDER
	
	
	
	// ACCOUNT LOOKUP
	
//	$('#accountNo').lookup({
//		list : '@{Pick.customers()}',
//		get : {
//			url: '@{Pick.customer()}',
//			success: function(data) {
//				if (data) {
//					var accountKey = $('#PostingProfileKey').val();
//					$('#accountNo').removeClass('fieldError');
//					$('#accountKey').val(data.customerNo);
//					$('#accountNo').val(data.customerNo);
//					$('#accountName').val(data.description);
//					$('#h_accountName').val(data.description);
//					$('#customerKey').val(data.customerKey);
//					//$('#transactionCode').focus();
//					//if (accountKey != data.code) {
//					//	disableCharge();
//					//}
//				} 
//			},
//			error: function(data) {
//				$('#accountNo').addClass('fieldError');
//				$('#accountName').val('NOT FOUND');
//				$('#accountNo').val('');
//				$('#accountKey').val('');
//				$('#customerNo').val('');
//				$('#customerKey').val('');
//				$('#h_accountName').val('');
//			}
//		},
//		help : $('#accountHelp'),
//		nextControl:$('#fundManagerCode')
//	});
	
	
	
	// FUND MANAGER LOOKUP
	$('#fundManagerCode').dynapopup2({key:'fundManagerKey', help:'fundManagerHelp', desc:'fundManagerName'},'PICK_GN_THIRD_PARTY', 'THIRD_PARTY-FUND_MANAGER', 'null', 
		function(data){
			if (data) {
				$('#fundManagerCode').removeClass('fieldError');
				$('#fundManagerKey').val(data.code);
				$('#fundManagerName').val(data.description);
				$('#h_fundManagerName').val(data.description);
				$('#email').val(data.email);
			}
		},
		function(){
			$('#fundManagerCode').addClass('fieldError');
			$('#fundManagerName').val('NOT FOUND');
			$('#fundManagerCode').val('');
			$('#fundManagerKey').val('');
			$('#h_fundManagerName').val('');
			$('#email').val('');
		}
	);

//	$('#fundManagerCode').lookup({
//		list:'@{Pick.thirdParties()}?type=THIRD_PARTY-FUND_MANAGER',
//		get:{
//			url:'@{Pick.thirdParty()}?type=THIRD_PARTY-FUND_MANAGER',
//			success: function(data) {
//				if (data) {
//					$('#fundManagerCode').removeClass('fieldError');
//					$('#fundManagerKey').val(data.code);
//					$('#fundManagerName').val(data.description);
//					$('#h_fundManagerName').val(data.description);
//					$('#email').val(data.email);
//				}
//			},
//			error : function(data) {
//				$('#fundManagerCode').addClass('fieldError');
//				$('#fundManagerName').val('NOT FOUND');
//				$('#fundManagerCode').val('');
//				$('#fundManagerKey').val('');
//				$('#h_fundManagerName').val('');
//				$('#email').val('');
//			}
//		},
//		key:$('#fundManagerKey'),
//		description:$('#fundManagerName'),
//		help:$('#fundManagerHelp'),
//		nextControl:$('#fundTypeCode')
//	});
		
	// FUND TYPE LOOKUP
	$('#fundTypeCode').lookup({
		list:'@{Pick.lookups()}?group=FUND_TYPE',
		get:{
			url:'@{Pick.lookup()}?group=FUND_TYPE',
			success: function(data) {
				if (data) {
					$('#fundTypeCode').removeClass('fieldError');
					$('#fundTypeKey').val(data.code);
					$('#fundTypeName').val(data.description);
					$('#h_fundTypeName').val(data.description);
				}
			},
			error : function(data) {
				$('#fundTypeCode').addClass('fieldError');
				$('#fundTypeName').val('NOT FOUND');
				$('#fundTypeCode').val('');
				$('#fundTypeKey').val('');
				$('#h_fundTypeName').val('');
			}
		},
		key:$('#fundTypeKey'),
		description:$('#fundTypeName'),
		help:$('#fundTypeHelp'),
		nextControl:$('#backDatedAllow')
	});
	
	//FUND CLASS LOOKUP
	$('#fundClassCode').lookup({
		list:'@{Pick.lookups()}?group=FUND_CLASS',
		get:{
			url:'@{Pick.lookup()}?group=FUND_CLASS',
			success: function(data) {
				if (data) {
					$('#fundClassCode').removeClass('fieldError');
					$('#fundClassKey').val(data.code);
					$('#fundClassName').val(data.description);
					$('#h_fundClassName').val(data.description);
				}
			},
			error : function(data) {
				$('#fundClassCode').addClass('fieldError');
				$('#fundClassName').val('NOT FOUND');
				$('#fundClassCode').val('');
				$('#fundClassKey').val('');
				$('#h_fundClassName').val('');
			}
		},
		key:$('#fundClassKey'),
		description:$('#fundClassName'),
		help:$('#fundClassHelp')
	});
	//CURRENCY
	$('#currency').lookup({
		list:'@{Pick.currencies()}',
		get:{
			url:'@{Pick.currency()}',
			success: function(data) {
				if (data) {
					$('#currency').removeClass('fieldError');
					$('#currency').val(data.code);
					$('#currencyDesc').val(data.description);
					$('#h_currencyDesc').val(data.description);
				}
			},
			error : function(data) {
				$('#currency').addClass('fieldError');
				$('#currencyDesc').val('NOT FOUND');
				$('#currency').val('');
				$('#h_currencyDesc').val('');
			}
		},
		key:$('#currencyCode'),
		description:$('#currencyDesc'),
		help:$('#currencyHelp'),
		nextControl:$('#interestRate')
	});
	
	// POSTING PROFILE LOOKUP
	$('#postingProfileCode').lookup({
		list : '@{Pick.faPostingProfiles()}',
		get : {
			url: '@{Pick.faPostingProfile()}',
			success: function(data) {
				if (data) {
					var postingProfileKey = $('#postingProfileKey').val();
					$('#postingProfileCode').removeClass('fieldError');
					$('#postingProfileName').val(data.description);
					$('#h_postingProfileName').val(data.description);
					$('#postingProfileKey').val(data.code);
					//$('#transactionCode').focus();
					//if (accountKey != data.code) {
					//	disableCharge();
					//}
				}			
			},
			error: function(data) {
				$('#postingProfileCode').addClass('fieldError');
				$('#postingProfileName').val('NOT FOUND');
				$('#postingProfileCode').val('');
				$('#postingProfileKey').val('');
				$('#h_postingProfileName').val('');
			}
		},
		help : $('#postingProfileHelp'),
		key:$('#postingProfileKey'),
		description:$('#postingProfileName'),
		help:$('#postingProfileHelp'),
		nextControl:$('#financialYear')
	});
	
	// COMPLIANCE PROFILE LOOKUP
	$('#complianceProfileCode').lookup({ 
		list : '@{Pick.cpProfiles()}',
		get : {
			url: '@{Pick.cpProfile()}',
			success: function(data) {
				if (data) {
					$('#complianceProfileCode').removeClass('fieldError');
					$('#complianceProfileCode').val(data.code);
					$('#complianceProfileDesc').val(data.description);
					$('#h_complianceProfileDesc').val(data.description);
				} 
			},
			error: function(data) {
				$('#complianceProfileCode').addClass('fieldError');
				$('#complianceProfileDesc').val('NOT FOUND');
				$('#complianceProfileCode').val('');
				$('#h_complianceProfileDesc').val('');
			}
		},
		help : $('#complianceProfileHelp'),
		nextControl:$('#financialYear')
	});
	
	// end of DataTables
	
	// DIALOG BOX INVESTMENT MODEL DETAIL
	$('#detailInvestmentModel').dialog({
		autoOpen:false,
		heigth:'300px',
		width:'750px',
		resizable:false,
		modal:true			
	});
	
	$('#detailDays').dialog({
		autoOpen:false,
		heigth:'300px',
		width:'750px',
		resizable:false,
		modal:true			
	});
	
	//AFTER VIEW CALENDER
	if ('${tabSchedule}'){
		$("#tabsInvestmentModel").tabs("select",1);
//		$("#tabFormula").tabs("enable",0);
//		$("#tabFormula").tabs("select",0);
//		$("#tabFormula").tabs("disable",1);
	}
	// NEW INVESTMENT MODEL ACTION
	$('#newInvestmentModel').click(function() {
		$("#detailInvestmentModel").dialog('open');
		$('.ui-widget-overlay').css('height',$('body').height());
		$("#detailInvestmentModel :text").val(""); 
		$("#detailInvestmentModel :hidden").val(""); 
		$("#detailInvestmentModel #investmentModelForm #securityClassCode").attr("disabled",false);
		$("#detailInvestmentModel #investmentModelForm #securityClassHelp").attr("disabled",false);
		$("#detailInvestmentModel #investmentModelForm #minimum").removeClass('fieldError');
		$("#detailInvestmentModel #investmentModelForm #maximum").removeClass('fieldError');
		$("#detailInvestmentModel #investmentModelForm .errmsg").html("");
		return false;
	});
	
	// DELETE INVESTMENT MODEL ROW
	$('#listInvestmentModel #gridInvestmentModel tbody tr #deleteButton').live('click', function() {
		if ('${mode}' != 'view') {
			var row = $(this).parents('tr');
			var rowNumber = tableInvestmentModel.fnGetPosition(row[0]);
			var deleteInvestmentModel = function() {
				tableInvestmentModel.fnDeleteRow(rowNumber);	
				$("#dialog-message").dialog('close');
			}
			messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteInvestmentModel, closeDialog);
		}
		
	});
	
	// Financial Year ALLOW VALIDATION
	$("#financialYear").blur(function(){
		var el = $(this);
		var length = el.val().length;
		el.removeClass('fieldError');
		$("#errmsgFinacial").html("");

		if ((length != 4) && ($("#financialYear").val() != "")) {
			el.addClass('fieldError');
			$("#errmsgFinacial").html("Invalid year format").show();
		}
	})
	
	// ACCOUNT NO EVENT CHANGE
	$("#account").change(function(){
		if ($('#account').val() == "") {
			$('#account').val("");
			$('#accountKey').val("");
			$('#accountDesc').val("");
			$('#h_accountName').val("");
			$('#customerNo').val("");
		} 
	});
	
	// FUND MANAGER CODE EVENT CHANGE
	$("#fundManagerCode").change(function(){
		if ($('#fundManagerCode').val() == "") {
			$('#fundManagerCode').val("");
			$('#fundManagerKey').val("");
			$('#fundManagerName').val("");
			$('#h_fundManagerName').val("");
		} 
	});
	
	// FUND TYPE CODE EVENT CHANGE
	$("#fundTypeCode").change(function(){
		if ($('#fundTypeCode').val() == "") {
			$('#fundTypeCode').val("");
			$('#fundTypeKey').val("");
			$('#fundTypeName').val("");
			$('#h_fundTypeName').val("");
		} 
	});
	
	// POSTING PROFILE CODE EVENT CHANGE
	$("#postingProfileCode").change(function(){
		if ($('#postingProfileCode').val() == "") {
			$('#postingProfileCode').val("");
			$('#postingProfileKey').val("");
			$('#postingProfileName').val("");
			$('#h_postingProfileName').val("");
		} 
	});
	
	// COMPLIANCE PROFILE EVENT CHANGE
	$("#complianceProfileCode").change(function(){
		if ($('#complianceProfileCode').val() == "") {
			$('#complianceProfileCode').val("");
			$('#complianceProfileDesc').val("");
			$('#h_complianceProfileDesc').val("");
		} 
	});
	
	// CURRENCY EVENT CHANGE
	$('#currency').change(function(){
		if ($('#currency').val() == "") {
			$('#currency').val("");
			$('#currencyDesc').val("");
			$('#h_currencyDesc').val("");
		}
	});
	$('#newDate').click(function() {
		if ($("#date").val() == null || $("#date").val() == ""){
			return false;
		}else{
			var table = $('#listPublishSchedule #gridPublishSchedule').dataTable();
			//var rowPosition = $('#detailIpoCust #rowPosition').val();
			var rows = table.fnGetNodes().length;
			var dataDate = new Object();
			dataDate.id = new Object();
			
			for(var i=0; i<rows; i++) {
				var cells = table.fnGetData(i);
				if (($("#date").val() != null) && ($("#date").val() != "" )){
					
					var entryDate  = new Date($("#date").val());
					var publis = new Date(cells.id.publishDate);
					if (new Date($("#date").datepicker('getDate')).getTime() == new Date(cells.id.publishDate).getTime()){
						$("#date").val("");
						return false;
					}
				}else{
					$("#date").val("");
					return false;
				}
			}
			
			dataDate.id.publishDate = new Date($("#date").datepicker('getDate'));
			table.fnAddData(dataDate);
			$("#date").val("");
			return false;
		}
		
	});
	
	
	//DELETE DATE
	$('#listPublishSchedule #gridPublishSchedule tbody tr #deleteButton').live('click', function() {
		if ('${mode}' != 'view') {
			var row = $(this).parents('tr');
			var rowNumber = tablePublishSchedule.fnGetPosition(row[0]);
			var deletePublishSchedule = function() {
				tablePublishSchedule.fnDeleteRow(rowNumber);	
				$("#dialog-message").dialog('close');
			};

			if ($('input[name=isUsedAppDate]:checked').val() != 'CALENDAR') messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deletePublishSchedule, closeDialog);
		}
		
	});
	
	$('#select').click(function(){	
		if ($('#year').val() ==''){
			$('#yearErrMsg').html('Required');
			return false
		}
		var year = $('#year').val();
		var isUsedAppDate = $('input[name=isUsedAppDate]:checked').val();
		var action="@{viewCalender()}?year="+year+"&mode=${mode}#{if group}&group=${group}#{/}#{if isPartial}&isPartial=${isPartial}#{/}&isUsedAppDate=" +isUsedAppDate;
		 $('#fundSetupForm').attr('action', action);
		 $('#fundSetupForm').submit();
		
	});
	
	if('${isUsedAppDate}' == 'CALENDAR') {
		$('#year').val($('#yearNow').val());
	}else{
		$('#year').val('${year}');
	}
//	alert("Yeart "+$('#year').val());
	$('#confirm').click(function() {
		var action="@{confirm()}?mode=${mode}#{if group}&group=${group}#{/}#{if isPartial}&isPartial=${isPartial}#{/}";
		$('#fundSetupForm').attr('action', action);
		$('#fundSetupForm').submit();
	});
	
	$('#save').click(function(){
//		alert("Save");
//		var length = $('#financialYear').val().length;
//		 $('#financialYear').removeClass('fieldError');
//		$("#errmsgFinacial").html("");
//
//		if ((length != 4) && ($("#financialYear").val() != "")) {
//			$('#financialYear').addClass('fieldError');
//			$("#errmsgFinacial").html("Invalid year format").show();
//			return false;
//		}
//		submit('@{save()}?mode=${mode}#{if group}&group=${group}#{/}#{if isPartial}&isPartial=${isPartial}#{/}');
		var year = $('#year').val();
		var action="@{save()}?year="+year+"&mode=${mode}#{if group}&group=${group}#{/}#{if isPartial}&isPartial=${isPartial}#{/}";
		 $('#fundSetupForm').attr('action', action);
		 $('#fundSetupForm').submit();
	});
	
	$('#back').click(function() {
		var isUsedAppDate = ($('#isUsedAppDateHidden').val() == 'true') ? 'CALENDAR' : 'SPECIFIC';
		var year = $('#year').val();
		location.href='@{back(id)}?isUsedAppDate='+isUsedAppDate+'&year='+year+'&mode=${mode}#{if status}&status=${status}#{/}#{if group}&group=${group}#{/}#{if param}&param=${param}#{/}#{if from}&from=${from}#{/}#{if isPartial}&isPartial=${isPartial}#{/}';
		return false;
	});
	
	$('#cancel').click(function() {
		location.href='@{list()}';
	});
	
	$('#close').click(function(){
		location.href='@{list()}';
	});
	
	// TAB ADDITIONAL
	$('input[name=autoEmail]').change(function(){
		
		if ($("input[name='autoEmail']:checked").val() == "true"){
			$("#emailHidden").val($('#email').val());
			$("input[name='faMaster.autoEmail']").val($('#autoEmail1').val());
		}else{
			$("#emailHidden").val("");
			$("input[name='faMaster.autoEmail']").val($('#autoEmail2').val());
		}
	});
	
	$('input[name=published]').change(function(){
		
		if ($("input[name='published']:checked").val() == "true"){
			$("input[name='faMaster.published']").val($('#published1').val());
		}else{
			$("input[name='faMaster.published']").val($('#published2').val());
		}
	});
 	$('input[name=reportTime]').change(function(){
		if ($("input[name='reportTime']:checked").val() == "true"){
			$("#eReportTime").attr("disabled",false);
			$('p[id=pEreportCode] label span').html(' *');
			$("input[name='faMaster.reportTime']").val($('#reportTime1').val());
		}else{
			$("#eReportTime").val("");
			$("#eReportTime").attr("disabled","disabled");
			$('p[id=pEreportCode] label span').html('');
			$("input[name='faMaster.reportTime']").val($('#reportTime2').val());
			
		}
 	});
 	
 	if (($('input[name=faMaster.autoEmail]').val()== null) || ($('input[name=faMaster.autoEmail]').val()== "") || ($('input[name=faMaster.autoEmail]').val()== false) || ($('input[name=faMaster.autoEmail]').val()== "false")){
		$("#emailHidden").val("");
	}else{
		$("#emailHidden").val($('#email').val());
	}
 	
 	var report = jQuery.trim($('#eReportTime').val());
 	if ((report != null && report != "") || $('input[name=faMaster.reportTime]').val() == true || $('input[name=faMaster.reportTime]').val() == "true" ){
 		if ('${confirming}' || ('${mode}'=='view') ){
 			$("#eReportTime").attr("disabled","disabled");
 		}else{
 			$("#eReportTime").attr("disabled",false);
 		}
		$('#reportTime1').attr('checked',"true");
		$('#reportTime2').attr('checked',"");
		$('p[id=pEreportCode] label span').html(' *');
		$("input[name='faMaster.reportTime']").val($('#reportTime1').val());
 	}else{
 		$('#reportTime1').attr('checked',"");
		$('#reportTime2').attr('checked',"true");
 		$("#eReportTime").attr("disabled","disabled");
 		$('p[id=pEreportCode] label span').html('');
 		$("input[name='faMaster.reportTime']").val($('#reportTime2').val());
 	}
 	
 	/*alert("E report time "+$('input[name=faMaster.eReportTime.lookupId]').val());
 	if (($('input[name=faMaster.eReportTime.lookupId]').val()!= null) && ($('input[name=faMaster.eReportTime.lookupId]').val()!= "")){
 		
 		alert("a --> "+$('#eReportTime').val()+"....");
		$("#eReportTime").attr("disabled",false);
		$('#reportTime1').attr('checked',"true");
	}else{
		alert("b --> "+$('#eReportTime').val()+"----");
 		$("#eReportTime").attr("disabled","disabled");
 		$('#reportTime2').attr('checked', "false");
	}*/
 	
 	$( '#autoFIMM' ).change(function(){
 		if ($('#autoFIMM').is(':checked')){
 			$('#autoFIMM').val(true);
 			$('#autoFIMMHidden').val(true);
 		} else {
 			$('#autoFIMM').val(false);
 			$('#autoFIMMHidden').val(false);
 		}
 	});
 	
 	
 	var isUsedAppDate = '${isUsedAppDate}';
 	console.log("isUsedAAppDate::" + isUsedAppDate);
 	if ( isUsedAppDate != '') {
 		if (isUsedAppDate == 'SPECIFIC') {
 			$('#isUsedAppDate1').attr('checked',"");
 			$('#isUsedAppDate2').attr('checked',"true");
 			if('${confirming}' != 'true') $("#date").enabled();
 			$("#newDate").enabled();
 			if('${confirming}' != 'true') $("#year").enabled();
 			if('${confirming}' != 'true') $(".deleteListBtn").enabled();
 			$("#isUsedAppDateHidden").val(false);
 		}
 		
 		if (isUsedAppDate == 'CALENDAR') {
 			$('#year').val($('#yearNow').val());
 			$('#isUsedAppDate1').attr('checked',"true");
 			$('#isUsedAppDate2').attr('checked',"");
 			$("#date").disabled();
 			$("#newDate").disabled();
 			$("#year").disabled();
 			$(".deleteListBtn").disabled();
 			$("#isUsedAppDateHidden").val(true);
 		}
 		
 	}else {
 		
 		var isUsedAppDateHidden = $("#isUsedAppDateHidden").val();
 		if ('${mode}' == 'entry') {
 			$('#isUsedAppDate1').attr('checked',"true");
 			$('#isUsedAppDate2').attr('checked',"");
 			$('#year').val($('#yearNow').val());
 			$("#date").disabled();
 			$("#newDate").disabled();
 			$("#year").disabled();
 			
 		}else {
 			if (isUsedAppDateHidden  == 'true') {
 	 			
 	 			$('#isUsedAppDate1').attr('checked',"true");
 	 			$('#isUsedAppDate2').attr('checked',"");
 	 			$('#year').val($('#yearNow').val());
 	 			$("#date").disabled();
 	 			$("#newDate").disabled();
 	 			$("#year").disabled();
 	 			$(".deleteListBtn").disabled();
 	 			
 	 			
 	 		}else {
 	 			
 	 			$('#isUsedAppDate1').attr('checked',"");
 	 			$('#isUsedAppDate2').attr('checked',"true");
 	 			if('${mode}' != 'view') $("#date").enabled();
 	 			$("#newDate").enabled();
 	 			if('${mode}' != 'view') $("#year").enabled();
 	 			if('${mode}' != 'view') $(".deleteListBtn").enabled();
 	 		}
 		}
 		
 	}
 	
 	
 	$('input[name=isUsedAppDate]').change(function(){
 		
 		if ($(this).val() == 'CALENDAR') {
 			$('#year').val($('#yearNow').val());
 			$("#date").disabled();
 			$("#newDate").disabled();
 			$("#year").disabled();
 			$(".deleteListBtn").disabled();
 			$("#isUsedAppDateHidden").val(true);
 			
 		}
 		
 		if ($(this).val() == 'SPECIFIC') {
 			$("#date").enabled();
 			$("#newDate").enabled();
 			$("#year").enabled();
 			$(".deleteListBtn").enabled();
 			$("#isUsedAppDateHidden").val(false);
 			
 		}
 		
 	});
 	
 	
 	
});

function submit(action, id) {
	var loading = $('<div id="loading" class="loading"><img src="@@{'/public/images/loading2.gif'}" style="margin:10px" align="middle" /> Sending data to server ...</div>').appendTo('body');
	loading.dialog({
		closeOnEscape: false,
		draggable: false,
		modal: true,
		resizable: false,
		open:function() {
			$(this).parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();
		} 
	});
	$('#form').attr('action', action);
	$('#form').submit();
}

function tableModelPublishSchedule(data){
	
	var fmtDate = '${appProp.jqueryDateFormat}';
	var fmtDateToGrid;
	if (fmtDate == 'dd/mm/yy') {
		fmtDateToGrid = 'dd/mm/yy';
	}
	
	var publishSchedule;
	

		if (('${publishSchedule}' != null ))
			publishSchedule = ${publishSchedule.raw()};
			
		if ('${publishSchedule}' == null ){
			publishSchedule = new Array();
		};
		
		/* Create an array with the values of all the input boxes in a column */
		/*
		$.fn.dataTableExt.afnSortData['sort-date'] = function  ( oSettings, iColumn )
		{
			console.log( "sorting..." );
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+') input', tr).val();
			} );
		}
		*/
		jQuery.fn.dataTableExt.oSort['uk_date-asc']  = function(ai,bi) {
			var a = ai.substring( 0,10 );
			var b = bi.substring( 0,10 );
			
		    var ukDatea = a.split('/');
		    var ukDateb = b.split('/');

		    var x = (ukDatea[2] + ukDatea[1] + ukDatea[0]) * 1;
		    var y = (ukDateb[2] + ukDateb[1] + ukDateb[0]) * 1;

		    return ((x < y) ? -1 : ((x > y) ?  1 : 0));
		};

		jQuery.fn.dataTableExt.oSort['uk_date-desc'] = function(ai,bi) {
			var a = ai.substring( 0,10 );
			var b = bi.substring( 0,10 );
		    var ukDatea = a.split('/');
		    var ukDateb = b.split('/');

		    var x = (ukDatea[2] + ukDatea[1] + ukDatea[0]) * 1;
		    var y = (ukDateb[2] + ukDateb[1] + ukDateb[0]) * 1;

		    return ((x < y) ? 1 : ((x > y) ?  -1 : 0));
		};
				
		
		tablePublishSchedule = $('#listPublishSchedule #gridPublishSchedule').dataTable({
			aaData: publishSchedule, 
			aoColumns: [
						{
							//sDefaultContent: "MAX",
							fnRender: function(obj){
								
								var controls;
								
										var stringDate = obj.aData.id.publishDate.toString();
										var startDate;
										if (stringDate.substr(2,1) !="/"){
											startDate =  $.datepicker.formatDate(fmtDateToGrid, new Date(obj.aData.id.publishDate));
										}else{
											startDate = obj.aData.id.publishDate;
										}
										controls = startDate;
										controls += '<input type="hidden" name="listPublishSchedule[' + obj.iDataRow + '].id.publishDate" value="' + startDate+ '" />';										
										
								return controls;
							}
							,"sType" : "uk_date"
						},
						{
							
							fnRender: function(obj){
								var controls;
								
								if(obj.aData.faMaster != null){
							 		controls += '<input type="hidden" name="listPublishSchedule[' + obj.iDataRow + '].faMaster.fundKey" value="' + obj.aData.faMaster.fundKey + '" />';
							 	}

								controls = '<center><input id="deleteButton" class="deleteListBtn" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
								return controls;
								
								
								/*if ((obj.aData.publishDate == null ) || (obj.aData.publishDate == null)){
									controls = '';
								}else{
									controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
								}	
								return controls;*/
								
							}
						}
				       ],
				       
//			aaSorting:[[0,'uk_date-asc']],
//			{ "sSortDataType": "sort-date" },
			bJQueryUI:true,
			bAutoWidth:false,
			bFilter:false,
			bRetrieve:true,
			bSort:true,											
			bPaginate:false,
			bInfo:false
		});
};
function doSave() {
	var length = $('#financialYear').val().length;
	 $('#financialYear').removeClass('fieldError');
	$("#errmsgFinacial").html("");

	if ((length != 4) && ($("#financialYear").val() != "")) {
		$('#financialYear').addClass('fieldError');
		$("#errmsgFinacial").html("Invalid year format").show();
		return false;
	}
	return true;
};

function tableInvestment(data) {
	var faInvestmentModels;
	/* #{if '${investmentModels}' != "" }
		faInvestmentModels = ${investmentModels.raw()};
	#{/if}
	#{if '${investmentModels}' == "" }
		faInvestmentModels = new Array();
	#{/if}
	*/
	if (('${investmentModels}' != null ))
		faInvestmentModels = ${investmentModels.raw()};
		
	if ('${investmentModels}' == null ){
		faInvestmentModels = new Array();
	};
		
	tableInvestmentModel = $('#listInvestmentModel #gridInvestmentModel').dataTable({
		aaData: faInvestmentModels,
		aoColumns: [
					{
						fnRender: function(obj){
							var controls;
								controls = obj.aData.securityClass.lookupCode;
								controls += '<input type="hidden" name="faInvestmentModels[' + obj.iDataRow + '].securityClass.lookupCode" value="' + obj.aData.securityClass.lookupCode + '" />';
								controls += '<input type="hidden" name="faInvestmentModels[' + obj.iDataRow + '].securityClass.lookupId" value="' + obj.aData.securityClass.lookupId + '" />';
								controls += '<input type="hidden" name="faInvestmentModels[' + obj.iDataRow + '].securityClass.lookupDescription" value="' + obj.aData.securityClass.lookupDescription + '" />';
							return controls;
						}
					},
					{
						sClass: 'numeric',
						fnRender: function(obj){
							var controls;
								controls = $('#dummy').autoNumericSet(obj.aData.min).val();
								controls += '<input type="hidden" name="faInvestmentModels[' + obj.iDataRow + '].min" value="' + obj.aData.min + '" />';
							return controls;
						}
					},
					{
						sClass: 'numeric',
						fnRender: function(obj){
							var controls;
								controls = $('#dummy').autoNumericSet(obj.aData.max).val();
								controls += '<input type="hidden" name="faInvestmentModels[' + obj.iDataRow + '].max" value="' + obj.aData.max + '" />';
							return controls;
						}
					},
					{
						fnRender: function(obj) {
						 	var controls;
						 	
						 	if(obj.aData.faMaster != null){
						 		controls += '<input type="hidden" name="faInvestmentModels[' + obj.iDataRow + '].faMaster.fundKey" value="' + obj.aData.faMaster.fundKey + '" />';
						 	}
						 	controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
//						 	controls += '<input type="hidden" name="faInvestmentModels[' + obj.iDataRow + '].investmentModelKey" value="' + obj.aData.investmentModelKey + '" />';
						 	
						 	if ((obj.aData.investmentModelKey != null) && (obj.aData.investmentModelKey != "")){
						 		controls += '<input type="hidden" name="faInvestmentModels[' + obj.iDataRow + '].investmentModelKey" value="' + obj.aData.investmentModelKey + '" />';
							}else{
								controls += '<input type="hidden" name="faInvestmentModels[' + obj.iDataRow + '].investmentModelKey" value="" />';
							}
						 	return controls;
					 	}
					}
			       ],
		aaSorting:[[1,'asc']],
		bJQueryUI:true,
		bAutoWidth: false,
		bFilter:false,												
		bSort: false,											// disabled sorting data tables
		bPaginate:false,
		bInfo:false
	});
	
	$('#listInvestmentModel #gridInvestmentModel ').removeAttr('style'); // tembak nilai widht untuk tampilan
	$('#listInvestmentModel #gridInvestmentModel tbody tr td').die('click');
	$('#listInvestmentModel #gridInvestmentModel tbody tr td').live('click', function() {

		//nodata klik ga error
		if (tableInvestmentModel.fnGetNodes().length == 0) {
			return false;
		}
		
		var rowPos = $(this).parents('tr');
		var rowPosNumber = tableInvestmentModel.fnGetPosition(rowPos[0]);
		var pos = tableInvestmentModel.fnGetPosition(this);
		cell = tableInvestmentModel.fnGetData(this.parentNode);
		
		if (pos[1] != 3) {			
			dataInvst = tableInvestmentModel.fnGetData(this.parentNode);
			$("#detailInvestmentModel #investmentModelForm #minimum").removeClass('fieldError');
			$("#detailInvestmentModel #investmentModelForm #maximum").removeClass('fieldError');
			$("#detailInvestmentModel #investmentModelForm .errmsg").html("");
			$('#detailInvestmentModel #investmentModelForm #rowPosition').val(rowPosNumber);
			$("#detailInvestmentModel #investmentModelForm #securityClassCode").attr("disabled","disabled");
			$("#detailInvestmentModel #investmentModelForm #securityClassHelp").attr("disabled","disabled");
			$("#detailInvestmentModel #investmentModelForm #investmentModelKey").val(dataInvst.investmentModelKey);
			if(dataInvst.faMaster == null) {
				dataInvst.faMaster = new Object();
			}
			
			$("#detailInvestmentModel #investmentModelForm #fundKey").val(dataInvst.faMaster.fundKey);
			$("#detailInvestmentModel #investmentModelForm #securityClassCode").val(dataInvst.securityClass.lookupCode);
			$("#detailInvestmentModel #investmentModelForm #securityClass").val(dataInvst.securityClass.lookupId);
			$("#detailInvestmentModel #investmentModelForm #securityClassDesc").val(dataInvst.securityClass.lookupDescription);
			
			if ((dataInvst.min == null) || (dataInvst.min == "")){
				$('#detailInvestmentModel #investmentModelForm #minimum').val(dataInvst.min);
			} else {
				$("#detailInvestmentModel #investmentModelForm #minimum").autoNumericSet( dataInvst.min).val();
			}
			
			if ((dataInvst.max == null) || (dataInvst.max == "")){
				$('#detailInvestmentModel #investmentModelForm #maximum').val(dataInvst.max);
			} else {
				$("#detailInvestmentModel #investmentModelForm #maximum").autoNumericSet(dataInvst.max).val();
			}
			
			$("#detailInvestmentModel #investmentModelForm #minimumStripped").val(dataInvst.min);
			$("#detailInvestmentModel #investmentModelForm #maximumStripped").val(dataInvst.max);
			$("#detailInvestmentModel").dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
		}
	}); 
	
	
	$('#detailInvestmentModel #addInvestmentModel').click(function() {	
		if (( $("#detailInvestmentModel #investmentModelForm #securityClassCode").val() == "") || ($("#detailInvestmentModel #investmentModelForm #minimum").val()=="") || ( $("#detailInvestmentModel #investmentModelForm #maximum").val()=="")){
			$("#detailInvestmentModel #investmentModelForm #errmsgNull").html("Security Class, Minimum or Maximum can not be Empty!").show();
			return false;
		} else {
			$("#detailInvestmentModel #investmentModelForm #errmsgNull").html("").show();
			if($("#detailInvestmentModel #investmentModelForm #minimum").hasClass('fieldError') || $("#detailInvestmentModel #investmentModelForm #maximum").hasClass('fieldError')){
				return false;
				
			} else {
				var table = $('#listInvestmentModel #gridInvestmentModel').dataTable();
				var rowPosition = $("#detailInvestmentModel #investmentModelForm #rowPosition").val();
				var found = false;
				
				var rows = table.fnGetNodes().length;	
				var found = false;
				for (i = 0; i < rows; i++) {
					var cell = table.fnGetData(i);
					if (($("#detailInvestmentModel #investmentModelForm #securityClassCode").val() == $(cell[0]).val() && rowPosition == "") ) {					
						//alert('"' + $("#detailInvestmentModel #investmentModelForm #securityClassCode").val() + '" Data already exist ');
						$("#detailInvestmentModel #investmentModelForm #errmsgExist").html('Security Class "' + $("#detailInvestmentModel #investmentModelForm #securityClassCode").val() +'" is already exist ').show();
						found = true;
						break;
					}						
				}
					
				if (!found) {
					if (rowPosition != "") {
						var dataInvst = table.fnGetData(parseFloat(rowPosition));
						dataInvst.investmentModelKey = $("#detailInvestmentModel #investmentModelForm #investmentModelKey").val();
						dataInvst.faMaster.fundKey = $("#detailInvestmentModel #investmentModelForm #fundKey").val();							
						table.fnUpdate((dataInvst.securityClass.lookupCode = $("#detailInvestmentModel #investmentModelForm #securityClassCode").val()) && (dataInvst.securityClass.lookupId = $("#detailInvestmentModel #investmentModelForm #securityClass").val()) && (dataInvst.securityClass.lookupDescription = $("#detailInvestmentModel #investmentModelForm #securityClassDesc").val()),parseFloat(rowPosition),0);
						table.fnUpdate(dataInvst.min = $("#detailInvestmentModel #investmentModelForm #minimumStripped").val(),parseFloat(rowPosition),1);
						table.fnUpdate(dataInvst.max = $("#detailInvestmentModel #investmentModelForm #maximumStripped").val(),parseFloat(rowPosition),2);							
						$("#detailInvestmentModel").dialog('close');
						
					} else {
						var dataInvst = new Object();
						dataInvst.faMaster = new Object();
						dataInvst.securityClass = new Object();							
						dataInvst.investmentModelKey = $("#detailInvestmentModel #investmentModelForm #investmentModelKey").val();
						dataInvst.faMaster.fundKey = $("#detailInvestmentModel #investmentModelForm #fundKey").val();
						dataInvst.securityClass.lookupCode = $("#detailInvestmentModel #investmentModelForm #securityClassCode").val();
						dataInvst.securityClass.lookupId = $("#detailInvestmentModel #investmentModelForm #securityClass").val();
						dataInvst.securityClass.lookupDescription = $("#detailInvestmentModel #investmentModelForm #securityClassDesc").val();							
						dataInvst.min = $("#detailInvestmentModel #investmentModelForm #minimumStripped").val();
						dataInvst.max = $("#detailInvestmentModel #investmentModelForm #maximumStripped").val()							
						table.fnAddData(dataInvst);
					}
					
					$('#detailInvestmentModel').dialog('close');		
					return false;
				}
			}
			
			
		}
	});
};
