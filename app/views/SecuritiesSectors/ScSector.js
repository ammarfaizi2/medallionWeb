$(function() {
	if (('${mode}'==='entry')||(('${mode}'==='edit')&&(('${sector?.status?.decodeStatus()}'==='Reject')))){
		$('input[name=isActive]').attr("disabled", "disabled");
	}
	
	$('input[name=isActive]').change(function(){
		$("input[name='sector.active']").val($("input[name='isActive']:checked").val());
	});
	
	var data = new Object();
	tabSectorDetail(data);

	// ========================== PROCESS IN DETAIL SECTOR FORM ========================//
	
	$('input[name=sectorDetail.limited]').change(function(){
		if ($('#limited').is(":checked")){
			$("input[name='sectorDetail.limited']").val(true);
			$('#maximumRange').autoNumericSet(100);
			$('#maximumRangeStripped').val(100);
			$('#minimumRange').autoNumericSet(0);
			$('#minimumRangeStripped').val(0);
			$('#maximumRange').attr("disabled", "disabled");
			$('#minimumRange').attr("disabled", "disabled");
		} else {
			$("input[name='sectorDetail.limited']").val(false);
			$('#maximumRange').removeAttr("disabled");
			$('#minimumRange').removeAttr("disabled");
		}
	});
	
	$('.buttons button').button();
	$('.buttons input:button').button();
	
	$( "#detailSector" ).dialog({
		autoOpen:false,
		modal:true,
		heigth:'650px',
		width:'750px',
		resizable:false
	});
	
	$('.buttons #btnNewSectorDetail').live('click', function() {
		selectedRow = null;
		$("#detailSector").dialog('open');
		$('.ui-widget-overlay').css('height',$('body').height());
		$("#detailSector :text").removeClass("fieldError");
		$("#detailSector :text").val('');
		$("#detailSector :hidden").val('');
		
		$("#detailSector #dialogSectorForm input[name='sectorDetail.limited']").attr('checked', true);
		$('#maximumRange').autoNumericSet(100);
		$('#maximumRangeStripped').val(100);
		$('#minimumRange').autoNumericSet(0);
		$('#minimumRangeStripped').val(0);
		$('#maximumRange').attr("disabled", "disabled");
		$('#minimumRange').attr("disabled", "disabled");
		
		$('#errDetailSectorGlobal').html('');
		$('#sectorLookupError').html('');
		$('#minimumRangeError').html('');
		$('#maximumRangeError').html('');
		
		//$(".error").html("");
		
		return false;
	});
	
	$('#ruleCode').change(function(){
		if ($('#ruleCode').val() === "") {
			$('#ruleDesc').val("");
		}
	});
	
	// RULE ID LOOKUP
	$('#ruleCode').lookup({
		list:'@{Pick.cpRules()}',
		get:{
			url:'@{Pick.cpRule()}',
			success: function(data){
				$('#ruleCode').removeClass('fieldError');
				$('#ruleCode').val(data.name);
				$('#ruleId').val(data.code);
				$('#ruleDesc').val(data.description);
				$('#h_ruleDesc').val(data.description);
			},
			error: function(data){
				$('#ruleCode').addClass('fieldError');
				$('#ruleCode').val('');
				$('#ruleId').val('');
				$('#ruleDesc').val('NOT FOUND');
				$('#h_ruleDesc').val('');
			}
		},
		description:$('#ruleDesc'),
		help:$('#ruleHelp'),
		nextControl:$('#active')
	});
	
	$('#sectorLookupCode').change(function(){
		if ($('#sectorLookupCode').val() == ""){
			$('#sectorLookupCode').removeClass('fieldError');
			$('#newSecurityKey').val("");
			$('#sectorLookupDesc').val("");
			$('#h_sectorLookupDesc').val("");
			$('#newSector').val("");
		}	
	});
	
	$('#sectorLookupCode').lookup({
		list:'@{Pick.lookups()}?group=SUB_SECTOR',
		get:{
			url:'@{Pick.lookup()}?group=SUB_SECTOR',
			success: function(data){
				$('#sectorLookupCode').removeClass('fieldError');
				$('#sectorLookup').val(data.code);
				$('#sectorLookupDesc').val(data.description);
				$('#h_sectorLookupDesc').val(data.description);
				$('#newSector').val(data.lookupCode);
				//state("STATE");
			},
			error: function(data){
				$('#sectorLookupCode').addClass('fieldError');
				$('#sectorLookup').val('');
				$('#sectorLookupCode').val('');
				$('#sectorLookupDesc').val('NOT FOUND');
				$('#h_sectorLookupDesc').val('');
			}
		},
		description:$('#sectorLookupDesc'),
		help:$('#sectorLookupHelp')
	});
	
	// ACTION SAVE SECTOR DETAIL
	$('#btnSaveSector').die("click");
	$('#btnSaveSector').live("click", function(){
		var table = $('#gridSector').dataTable();
		
		var rowPosition = $('#detailSector #dialogSectorForm #rowPosition').val();
		
		if ($('#sectorLookupCode').val()=='') {
			$("#sectorLookupError").html('Required');
			return false;
		} else {
			$("#sectorLookupError").html('');
		}
		
		if ($('#limited').is(':checked')) {
			if ($('#maximumRange').val()=='') {
				$("#maximumRangeError").html('Required');
			} else {
				$("#maximumRangeError").html('');
			}
			
			
			if ($('#minimumRange').val()=='') {
				$("#minimumRangeError").html('Required');
			} else {
				$("#minimumRangeError").html('');
			}
		}

		var errMsgMaxMin = "Maximum should be greater than Minimum";
		var maxVal = parseFloat($('#maximumRangeStripped').val());
		var minVal = parseFloat($('#minimumRangeStripped').val());
		if (!(maxVal > minVal)) {
			$('#maximumRangeError').html(errMsgMaxMin);
			return false;
		}
		
		var dataSector = table.fnGetData(parseFloat(rowPosition));
		var sectorLookup = $('#detailSector #dialogSectorForm #sectorLookup').val();
		var oldSector = $('#detailSector #dialogSectorForm #oldSector').val();
		var newSector = $('#detailSector #dialogSectorForm #newSector').val();
		
		var errMsg = "Security Sector already exist!";
		if (rowPosition != "") {
			var found = false;
			var rows = table.fnGetNodes().length;
			for(i=0; i<rows; i++) {
				var cells = table.fnGetData(i);
				if ((cells.sectorLookup.lookupId == sectorLookup)&&(oldSector != newSector)) {
					$('#errDetailSectorGlobal').html(errMsg);
					return false;
				} else {
					$('#errDetailSectorGlobal').html('');
				}
				
			}
			
			if (!found) {
				
				table.fnUpdate((dataSector.sectorLookup.lookupCode = $('#detailSector #dialogSectorForm #sectorLookupCode').val()), parseFloat(rowPosition), 0);
				table.fnUpdate((dataSector.sectorLookup.lookupDescription = $('#detailSector #dialogSectorForm #sectorLookupDesc').val()), parseFloat(rowPosition), 1);
				table.fnUpdate((dataSector.minimumRange = $('#detailSector #dialogSectorForm #minimumRangeStripped').val()), parseFloat(rowPosition), 2);
				table.fnUpdate((dataSector.maximumRange = $('#detailSector #dialogSectorForm #maximumRangeStripped').val()), parseFloat(rowPosition), 3);
				
				dataSector.sectorLimitDetail = $('#detailSector #dialogSectorForm #sectorLimitDetail').val();
				dataSector.sectorLookup.lookupId = $('#detailSector #dialogSectorForm #sectorLookup').val();
				dataSector.limited = $("#detailSector #dialogSectorForm #limited").val();
				table.fnUpdate(dataSector, parseFloat(rowPosition), 4);
				
				$('#detailSector').dialog('close');
				
				return false;
			}
		} else {
			
			var found = false;
			var rows = table.fnGetNodes().length;
			for(i=0; i<rows; i++) {
				var cells = table.fnGetData(i);
				if (cells.sectorLookup.lookupId == sectorLookup){
					$('#errDetailSectorGlobal').html(errMsg);
					return false;
				}
			}
			if (!found) {
				var dataSector = new Object();
				dataSector.sectorLookup = new Object();
				
				dataSector.sectorLimitDetail = $('#detailSector #dialogSectorForm #sectorLimitDetail').val();
				
				dataSector.sectorLookup.lookupCode = $('#detailSector #dialogSectorForm #sectorLookupCode').val();
				dataSector.sectorLookup.lookupDescription = $('#detailSector #dialogSectorForm #sectorLookupDesc').val();
				dataSector.sectorLookup.lookupId = $('#detailSector #dialogSectorForm #sectorLookup').val();
				
				dataSector.limited = $('#detailSector #dialogSectorForm #limited').val();
				dataSector.maximumRange = $('#detailSector #dialogSectorForm #maximumRangeStripped').val();
				dataSector.minimumRange = $('#detailSector #dialogSectorForm #minimumRangeStripped').val();
				
				table.fnAddData(dataSector);
				
				$('#detailSector').dialog('close');
				
				return false;
			}
		}
	});
	
	$( "#btnCancelSector" ).click(function() {
		$('#detailSector').dialog('close');
		
		return false;
	});
	
	$( "#btnCloseSector" ).click(function() {
		$('#detailSector').dialog('close');
		
		return false;
	});
	
	var closeDialog = function() {
		$("#dialog-message").dialog("close");
	};
	
	$('#gridSector tbody tr #deleteButton').live('click', function() {
		var row = $(this).parents('tr');
		var rowNumber = tableSector.fnGetPosition(row[0]);
		var deleteSector = function(){
			tableSector.fnDeleteRow(rowNumber);
			var idTable = $("#gridSector");
			var trs = $("tr", idTable);
			$.each(trs, function(idx, data){
				var hiddens = $("[type=hidden]", $(this));
					$(hiddens).eq(0).attr("name", "sectorDetails["+(idx-1)+"].sectorLookup.lookupCode");
					$(hiddens).eq(1).attr("name", "sectorDetails["+(idx-1)+"].sectorLookup.lookupDescription");
					$(hiddens).eq(2).attr("name", "sectorDetails["+(idx-1)+"].minimumRange");
					$(hiddens).eq(3).attr("name", "sectorDetails["+(idx-1)+"].maximumRange");
					$(hiddens).eq(4).attr("name", "sectorDetails["+(idx-1)+"].sectorLimitDetail");
					$(hiddens).eq(5).attr("name", "sectorDetails["+(idx-1)+"].sectorLookup.lookupId");
					$(hiddens).eq(6).attr("name", "sectorDetails["+(idx-1)+"].limited");
			});
			$("#dialog-message").dialog("close");
			return false;
		}
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteSector, closeDialog);
	
	});
});

function tabSectorDetail(data) {
	var entrySectorDetails;
	if ('${sectorLimitDetails}' != null )
		entrySectorDetails = ${sectorLimitDetails.raw()};

	if ('${entrySectorDetails}' == null )
		entrySectorDetails = new Array();
	
	tableSector = 
		$('#gridSector').dataTable({
			aaData: entrySectorDetails,
			aoColumns: [
						{
							fnRender: function(obj){
								var controls;
								controls = obj.aData.sectorLookup.lookupCode;
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								controls = obj.aData.sectorLookup.lookupDescription;
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								controls = $('#dummy').autoNumericSet(obj.aData.minimumRange, {aPad:true,mDec:4}).val();
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								controls = $('#dummy').autoNumericSet(obj.aData.maximumRange, {aPad:true,mDec:4}).val();
								return controls;
							}
						},
						{
							fnRender: function(obj) {
							 	var controls;
								controls = '<input type="hidden" name="sectorDetails[' + obj.iDataRow + '].sectorLookup.lookupCode" value="' + obj.aData.sectorLookup.lookupCode + '" />';
								controls += '<input type="hidden" name="sectorDetails[' + obj.iDataRow + '].sectorLookup.lookupDescription" value="' + obj.aData.sectorLookup.lookupDescription + '" />';
								controls += '<input type="hidden" name="sectorDetails[' + obj.iDataRow + '].minimumRange" value="' + obj.aData.minimumRange + '" />';
								controls += '<input type="hidden" name="sectorDetails[' + obj.iDataRow + '].maximumRange" value="' + obj.aData.maximumRange + '" />';
								controls += '<input type="hidden" name="sectorDetails[' + obj.iDataRow + '].sectorLimitDetail" value="' + obj.aData.sectorLimitDetail + '" />';
								controls += '<input type="hidden" name="sectorDetails[' + obj.iDataRow + '].sectorLookup.lookupId" value="' + obj.aData.sectorLookup.lookupId + '" />';
								controls += '<input type="hidden" name="sectorDetails[' + obj.iDataRow + '].limited" value="' + obj.aData.limited + '" />';
							 	controls += '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
							 	return controls;
						 	}
						}
				       	],
			aaSorting:[[0, 'asc']],
			bAutoWidth: false,
			bFilter: false,
			bInfo: false,
			bJQueryUI:true,
			bPaginate: false,
			bSearch: false,
			bLengthChange:false
		});
	
	$('#gridSector ').removeAttr('style'); // tembak nilai widht untuk tampilan
	$('#gridSector tbody tr td').die('click');
	$('#gridSector tbody tr td').live('click', function() {
		var rowPos = $(this).parents('tr');
		if (tableSector.fnGetNodes().length == 0) {
			return false;
		}
		var rowPosNumber = tableSector.fnGetPosition(rowPos[0]);
		var pos = tableSector.fnGetPosition(this);
		cell = tableSector.fnGetData(this.parentNode);
		if (pos[1] != 4) {
			
			dataSectorTable = tableSector.fnGetData(this.parentNode);
			
			$('#detailSector #dialogSectorForm #rowPosition').val(rowPosNumber);
			$('#detailSector #dialogSectorForm #sectorLimitDetail').val(dataSectorTable.sectorLimitDetail);
			$('#detailSector #dialogSectorForm #sectorLookupCode').val(dataSectorTable.sectorLookup.lookupCode);
			$('#detailSector #dialogSectorForm #sectorLookup').val(dataSectorTable.sectorLookup.lookupId);
			$('#detailSector #dialogSectorForm #sectorLookupDesc').val(dataSectorTable.sectorLookup.lookupDescription);
			$('#detailSector #dialogSectorForm #limited').val(dataSectorTable.limited);
			if (typeof dataSectorTable.limited === 'string') {
				if (dataSectorTable.limited === 'true') {
					dataSectorTable.limited = true;
				} else if (dataSectorTable.limited === 'false') {
					dataSectorTable.limited = false;
				}
			}
			
			if (dataSectorTable.limited) {
				$("#detailSector #dialogSectorForm #limited").attr("checked", "checked");
			} else {
				$("#detailSector #dialogSectorForm #limited").removeAttr("checked");
			}
			
			$('#detailSector #dialogSectorForm #maximumRange').autoNumericSet(dataSectorTable.maximumRange);
			$('#detailSector #dialogSectorForm #maximumRangeStripped').val(dataSectorTable.maximumRange);
			$('#detailSector #dialogSectorForm #minimumRange').autoNumericSet(dataSectorTable.minimumRange);
			$('#detailSector #dialogSectorForm #minimumRangeStripped').val(dataSectorTable.minimumRange);
			$('#detailSector #dialogSectorForm #oldSector').val($('#detailSector #dialogSectorForm #sectorLookupCode').val());
			$('#detailSector #dialogSectorForm #newSector').val($('#detailSector #dialogSectorForm #oldSector').val());
			
			if (!(('${confirming}') || ('${mode}'==='view'))) {
				if ($('#limited').is(":checked")){
					$('#maximumRange').attr("disabled", "disabled");
					$('#minimumRange').attr("disabled", "disabled");
				} else {
					$('#maximumRange').removeAttr("disabled");
					$('#minimumRange').removeAttr("disabled");
				}
			}
			
			$('.error').html("");
			$("#detailSector").dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
		}
		
	});
}
	