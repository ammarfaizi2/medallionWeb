$(function() {
	if (('${mode}' == 'entry')||(('${mode}' == 'edit')&&(('${porto?.status?.decodeStatus()}' == 'Reject')))){
		$('input[name=isActive]').attr("disabled", "disabled");
	}
	
	$('input[name=isActive]').change(function(){
		$("input[name='porto.active']").val($("input[name='isActive']:checked").val());
	});
	
	$('input.range').autoNumeric({vMax: '999.999999'});
	$('input.range').live('blur', function() {
		var el = $(this);
		var id = this.id;
		var stripped = "#" + id + "Stripped";
		if (el.val() == '') {
			el.siblings(stripped).val('');
			return;
		}
		el.siblings(stripped).val(el.autoNumericGet());
	});
	
	var data = new Object();
	tabPortfolioDetail(data);

	// ========================== PROCESS IN DETAIL PORTFOLIO LIMIT FORM ========================//
	
	$('input[name=portfolioDetail.range]').change(function(){
		if ($('#range').is(":checked")){
			$("input[name='portfolioDetail.range']").val(true);
			$('#maximumRange').removeAttr("disabled");
			$('#minimumRange').removeAttr("disabled");
		} else {
			$("input[name='portfolioDetail.range']").val(false);
			$('#maximumRange').val(999.999999);
			$('#maximumRangeStripped').val(999.999999);
			$('#minimumRange').autoNumericSet(0);
			$('#minimumRangeStripped').val(0);
			$('#maximumRange').attr("disabled", "disabled");
			$('#minimumRange').attr("disabled", "disabled");
		}
	});
	
	$('.buttons button').button();
	$('.buttons input:button').button();
	
	$( "#detailPortfolio" ).dialog({
		autoOpen:false,
		modal:true,
		heigth:'650px',
		width:'750px',
		resizable:false
	});
	
	$('.buttons #btnNewPortfolioDetail').live('click', function() {
		selectedRow = null;
		$("#detailPortfolio").dialog('open');
		$('.ui-widget-overlay').css('height',$('body').height());
		$("#detailPortfolio :text").removeClass("fieldError");
		$("#detailPortfolio :text").val('');
		$("#detailPortfolio :hidden").val('');
		$("#range").val(false);
		$("#range").attr('checked',false);
		
		//$("#detailPortfolio #dialogPortfolioForm input[name='portfolioDetail.range']").attr("checked", true);
		//$("#range").val(true);
		$('#maximumRange').val(999.999999);
		$('#maximumRangeStripped').val(999.999999);
		$('#minimumRange').autoNumericSet(0);
		$('#minimumRangeStripped').val(0);
		$('#maximumRange').attr("disabled", "disabled");
		$('#minimumRange').attr("disabled", "disabled");
		
		//$(".error").html("");
		$("#errDetailPortfolioGlobal").html("");
		$("#securityTypeError").html("");
		$("#securityCodeError").html("");
		$("#minimumRangeError").html("");
		$("#maximumRangeError").html("");
		$("#securityType").focus();
		
		return false;
	});
	
	$('#ruleCode').change(function(){
		if ($('#ruleCode').val() === "") {
			$('#ruleDesc').val("");
		}
	});
	
	// RULE ID LOOKUP
	$('#ruleCode').lookup({
		list:'@{Pick.cpRulesPortfolio()}',
		get:{
			url:'@{Pick.cpRulePortfolio()}',
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
	
	$('#securityCode').dynapopup2({key:'securityKey', help:'securityHelp', desc:'securityDesc'},'PICK_SC_MASTER', '', 'range', function(data){
		$('#securityCode').removeClass('fieldError');
		$('#securityCode').val(data.securityId);
		$('#securityKey').val(data.code);
		$('#securityDesc').val(data.description);
		$('#h_securityDesc').val(data.description);
	},function(data){
		$('#securityCode').addClass('fieldError');
		$('#securityCode').val('');
		$('#securityKey').val('');
		$('#securityDesc').val('NOT FOUND');
		$('#h_securityDesc').val('');
	});
	
	function attachSecuritiesPaging() {
		var securityType = $('#securityType').val();
		var pickName = (securityType == '') ? 'PICK_SC_MASTER' : 'PICK_SC_MASTER_BY_SEC_TYPE';
		$('#securityCode').dynapopup2({key:'securityKey', help:'securityHelp', desc:'securityDesc'}, pickName, securityType, 'range', function(data){
			$('#securityCode').removeClass('fieldError');
			$('#securityCode').val(data.securityId);
			$('#securityKey').val(data.code);
			$('#securityDesc').val(data.description);
			$('#h_securityDesc').val(data.description);
		},function(data){
			$('#securityCode').addClass('fieldError');
			$('#securityCode').val('');
			$('#securityKey').val('');
			$('#securityDesc').val('NOT FOUND');
			$('#h_securityDesc').val('');
		});
	}
	
	$('#securityType').change(function(){
		if ($('#securityType').val() == ""){
			$('#securityType').removeClass('fieldError');
			$('#securityType').val("");
			$('#newSecurityKey').val("");
			$('#securityTypeDesc').val("");
			$('#h_securityTypeDesc').val("");
			
			$('#securityCode').val("");
			$('#securityKey').val("");
			$('#securityDesc').val("");
			$('#h_securityDesc').val("");
		}
		
		$('#securityCode').val("");
		$('#securityKey').val("");
		$('#securityDesc').val("");
		$('#h_securityDesc').val("");	
		attachSecuritiesPaging();
	});
	
	$('#securityType').lookup({
		list:'@{Pick.securityTypes()}',
		get:{
			url:'@{Pick.securityType()}',
			success: function(data){
				$('#securityType').removeClass('fieldError');
				$('#securityType').val(data.code);
				$('#newSecurityKey').val(data.code);
				$('#securityTypeDesc').val(data.securityTypeDesc);
				$('#h_securityTypeDesc').val(data.securityTypeDesc);
				$('#securityType').change();
			},
			error: function(data){
				$('#securityType').addClass('fieldError');
				$('#securityType').val('');
				$('#securityType').val('');
				$('#securityTypeDesc').val('NOT FOUND');
				$('#h_securityTypeDesc').val('');
				$('#securityType').change();
			}
		},
		description:$('#securityTypeDesc'),
		help:$('#securityTypeHelp')
	});
	
	$('#securityCode').change(function(){
		if ($('#securityCode').val() == ""){
			$('#securityCode').removeClass('fieldError');
			
			$('#securityCode').val("");
			$('#securityKey').val("");
			$('#securityDesc').val("");
			$('#h_securityDesc').val("");
		}	
	});
	
	attachSecuritiesPaging();
	
//	$('#securityCode').lookup({
//		list:'@{Pick.securities()}',
//		get:{
//			url:'@{Pick.security()}',
//			success: function(data){
//				$('#securityCode').removeClass('fieldError');
//				$('#securityCode').val(data.securityId);
//				$('#securityKey').val(data.code);
//				$('#securityDesc').val(data.description);
//				$('#h_securityDesc').val(data.description);
//			},
//			error: function(data){
//				$('#securityCode').addClass('fieldError');
//				$('#securityCode').val('');
//				$('#securityKey').val('');
//				$('#securityDesc').val('NOT FOUND');
//				$('#h_securityDesc').val('');
//			}
//		},
//		filter:$('#securityType'),
//		description:$('#securityDesc'),
//		help:$('#securityHelp')
//	});
	
	// ACTION SAVE PORTFOLIO DETAIL
	$('#btnSavePortfolio').die("click");
	$('#btnSavePortfolio').live("click", function(){
		var table = $('#gridPortfolio').dataTable();
		
		var rowPosition = $('#detailPortfolio #dialogPortfolioForm #rowPosition').val();
		
		if (($('#securityType').val()=='') || ($('#securityCode').val()=='') || ($('#maximumRange').val()=='') || ($('#minimumRange').val()=='')){
			if ($('#securityType').val()=='') {
				$("#securityTypeError").html('Required');
			}
			if ($('#securityCode').val()=='') {
				$("#securityCodeError").html('Required');
			}
			if ($('#maximumRange').val()=='') {
				$("#maximumRangeError").html('Required');
			}
			if ($('#minimumRange').val()=='') {
				$("#minimumRangeError").html('Required');
			}
			
			return false;
		}

		var errMsgMaxMin = "Maximum should be greater than or equal to Minimum";
		var maxVal = parseFloat($('#maximumRangeStripped').val());
		var minVal = parseFloat($('#minimumRangeStripped').val());
		if (!(maxVal >= minVal)) {
			$('#maximumRangeError').html(errMsgMaxMin);
			return false;
		}
		
		var dataPortfolio = table.fnGetData(parseFloat(rowPosition));
		var securityType = $('#detailPortfolio #dialogPortfolioForm #securityType').val();
		var oldSecurityType = $('#detailPortfolio #dialogPortfolioForm #oldSecurityType').val();
		var newSecurityType = $('#detailPortfolio #dialogPortfolioForm #newSecurityType').val();
		var securityKey = $('#detailPortfolio #dialogPortfolioForm #securityKey').val();
		var oldSecurityKey = $('#detailPortfolio #dialogPortfolioForm #oldSecurityKey').val();
		var newSecurityKey = $('#detailPortfolio #dialogPortfolioForm #newSecurityKey').val();
		
		var errMsg = "Security Type & Security Code already exist!";
		if (rowPosition != "") {
			var found = false;
			var rows = table.fnGetNodes().length;
			for(i=0; i<rows; i++) {
				var cells = table.fnGetData(i);
				if ((cells.security.securityType.securityType == securityType)&&(oldSecurityType != newSecurityType)&&(cells.security.securityKey == securityKey)&&(oldSecurityKey != newSecurityKey)){
					$('#errDetailPortfolioGlobal').html(errMsg);
					return false;
				}
			}
			
			if (!found) {
				
				table.fnUpdate((dataPortfolio.security.securityType.securityType = $('#detailPortfolio #dialogPortfolioForm #securityType').val()), parseFloat(rowPosition), 0);
				table.fnUpdate((dataPortfolio.security.securityId = $('#detailPortfolio #dialogPortfolioForm #securityCode').val()), parseFloat(rowPosition), 1);
				table.fnUpdate((dataPortfolio.security.description = $('#detailPortfolio #dialogPortfolioForm #securityDesc').val()), parseFloat(rowPosition), 2);
				table.fnUpdate((dataPortfolio.maximumRange = $('#detailPortfolio #dialogPortfolioForm #maximumRangeStripped').val()), parseFloat(rowPosition), 3);
				table.fnUpdate((dataPortfolio.minimumRange = $('#detailPortfolio #dialogPortfolioForm #minimumRangeStripped').val()), parseFloat(rowPosition), 4);
				
				dataPortfolio.portfolioLimitDetail = $('#detailPortfolio #dialogPortfolioForm #portfolioLimitDetail').val();
				dataPortfolio.security.securityKey = $('#detailPortfolio #dialogPortfolioForm #securityKey').val();
				dataPortfolio.security.securityType.description = $('#detailPortfolio #dialogPortfolioForm #securityTypeDesc').val();
				dataPortfolio.range = $("#detailPortfolio #dialogPortfolioForm #range").val();
				dataPortfolio.maximumRange = $('#detailPortfolio #dialogPortfolioForm #maximumRangeStripped').val();
				dataPortfolio.minimumRange = $('#detailPortfolio #dialogPortfolioForm #minimumRangeStripped').val();
				table.fnUpdate(dataPortfolio, parseFloat(rowPosition), 5);
				
				$('#detailPortfolio').dialog('close');
				
				return false;
			}
		} else {
			
			var found = false;
			var rows = table.fnGetNodes().length;
			for(i=0; i<rows; i++) {
				var cells = table.fnGetData(i);
				if ((cells.security.securityType.securityType == securityType)&&(cells.security.securityKey == securityKey)){
					$('#errDetailPortfolioGlobal').html(errMsg);
					return false;
				}
			}
			if (!found) {
				var dataPortfolio = new Object();
				dataPortfolio.security = new Object();
				dataPortfolio.security.securityType = new Object();
				
				dataPortfolio.security.securityType.securityType = $('#detailPortfolio #dialogPortfolioForm #securityType').val();
				dataPortfolio.security.securityId = $('#detailPortfolio #dialogPortfolioForm #securityCode').val();
				dataPortfolio.security.description = $('#detailPortfolio #dialogPortfolioForm #securityDesc').val();
				
				dataPortfolio.portfolioLimitDetail = $('#detailPortfolio #dialogPortfolioForm #portfolioLimitDetail').val();
				dataPortfolio.security.securityKey = $('#detailPortfolio #dialogPortfolioForm #securityKey').val();
				dataPortfolio.security.securityType.description = $('#detailPortfolio #dialogPortfolioForm #securityTypeDesc').val();
				dataPortfolio.range = $('#detailPortfolio #dialogPortfolioForm #range').val();
				dataPortfolio.maximumRange = $('#detailPortfolio #dialogPortfolioForm #maximumRangeStripped').val();
				dataPortfolio.minimumRange = $('#detailPortfolio #dialogPortfolioForm #minimumRangeStripped').val();
				
				table.fnAddData(dataPortfolio);
				
				$('#detailPortfolio').dialog('close');
				
				return false;
			}
		}
	});
	
	$( "#btnCancelPortfolio" ).click(function() {
		$('#detailPortfolio').dialog('close');
		
		return false;
	});
	
	$( "#btnClosePortfolio" ).click(function() {
		$('#detailPortfolio').dialog('close');
		
		return false;
	});
	
	var closeDialog = function() {
		$("#dialog-message").dialog("close");
	};
	
	$('#gridPortfolio tbody tr #deleteButton').live('click', function() {
		var row = $(this).parents('tr');
		var rowNumber = tablePortfolio.fnGetPosition(row[0]);
		var deletePortfolio = function(){
			tablePortfolio.fnDeleteRow(rowNumber);
			var idTable = $("#gridPortfolio");
			var trs = $("tr", idTable);
			$.each(trs, function(idx, data){
				var hiddens = $("[type=hidden]", $(this));
					$(hiddens).eq(0).attr("name", "portfolioDetails["+(idx-1)+"].security.securityType.securityType");
					$(hiddens).eq(1).attr("name", "portfolioDetails["+(idx-1)+"].security.securityId");
					$(hiddens).eq(2).attr("name", "portfolioDetails["+(idx-1)+"].security.description");
					$(hiddens).eq(3).attr("name", "portfolioDetails["+(idx-1)+"].portfolioLimitDetail");
					$(hiddens).eq(4).attr("name", "portfolioDetails["+(idx-1)+"].security.securityKey");
					$(hiddens).eq(5).attr("name", "portfolioDetails["+(idx-1)+"].security.securityType.description");
					$(hiddens).eq(6).attr("name", "portfolioDetails["+(idx-1)+"].range");
					$(hiddens).eq(7).attr("name", "portfolioDetails["+(idx-1)+"].maximumRange");
					$(hiddens).eq(8).attr("name", "portfolioDetails["+(idx-1)+"].minimumRange");
			});
			$("#dialog-message").dialog("close");
			return false;
		}
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deletePortfolio, closeDialog);
	
	});
});

function tabPortfolioDetail(data) {
	var entryPortfolioDetails;
	if ('${portoDetails}' != null )
		entryPortfolioDetails = ${portoDetails.raw()};

	if ('${entryPortfolioDetails}' == null )
		entryPortfolioDetails = new Array();
	
	tablePortfolio = 
		$('#gridPortfolio').dataTable({
			aaData: entryPortfolioDetails,
			aoColumns: [
						{
							fnRender: function(obj){
								var controls;
								controls = obj.aData.security.securityType.securityType;
								controls += '<input type="hidden" name="portfolioDetails[' + obj.iDataRow + '].security.securityType.securityType" value="' + obj.aData.security.securityType.securityType + '" />';
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								controls = obj.aData.security.securityId;
								controls += '<input type="hidden" name="portfolioDetails[' + obj.iDataRow + '].security.securityId" value="' + obj.aData.security.securityId + '" />';
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								controls = obj.aData.security.description;
								controls += '<input type="hidden" name="portfolioDetails[' + obj.iDataRow + '].security.description" value="' + obj.aData.security.description + '" />';
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								//controls = $('#dummy').autoNumericSet(obj.aData.minimumRange, {aPad:true,mDec:4}).val();
								controls = obj.aData.minimumRange;
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								//controls = $('#dummy').autoNumericSet(obj.aData.maximumRange, {aPad:true,mDec:4}).val();
								controls = obj.aData.maximumRange;
								return controls;
							}
						},
						{
							fnRender: function(obj) {
							 	var controls;
								controls = '<input type="hidden" name="portfolioDetails[' + obj.iDataRow + '].portfolioLimitDetail" value="' + obj.aData.portfolioLimitDetail + '" />';
								controls += '<input type="hidden" name="portfolioDetails[' + obj.iDataRow + '].security.securityKey" value="' + obj.aData.security.securityKey + '" />';
								controls += '<input type="hidden" name="portfolioDetails[' + obj.iDataRow + '].security.securityType.description" value="' + obj.aData.security.securityType.description + '" />';
								controls += '<input type="hidden" name="portfolioDetails[' + obj.iDataRow + '].range" value="' + obj.aData.range + '" />';
								controls += '<input type="hidden" name="portfolioDetails[' + obj.iDataRow + '].maximumRange" value="' + obj.aData.maximumRange + '" />';
								controls += '<input type="hidden" name="portfolioDetails[' + obj.iDataRow + '].minimumRange" value="' + obj.aData.minimumRange + '" />';
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
	
	$('#gridPortfolio ').removeAttr('style'); // tembak nilai widht untuk tampilan
	$('#gridPortfolio tbody tr td').die('click');
	$('#gridPortfolio tbody tr td').live('click', function() {
		var rowPos = $(this).parents('tr');
		if (tablePortfolio.fnGetNodes().length == 0) {
			return false;
		}
		var rowPosNumber = tablePortfolio.fnGetPosition(rowPos[0]);
		var pos = tablePortfolio.fnGetPosition(this);
		cell = tablePortfolio.fnGetData(this.parentNode);
		if (pos[1] != 5) {
			
			dataPortfolioTable = tablePortfolio.fnGetData(this.parentNode);
			
			$('#detailPortfolio #dialogPortfolioForm #rowPosition').val(rowPosNumber);
			$('#detailPortfolio #dialogPortfolioForm #portfolioLimitDetail').val(dataPortfolioTable.portfolioLimitDetail);
			$('#detailPortfolio #dialogPortfolioForm #securityType').val(dataPortfolioTable.security.securityType.securityType);
			$('#detailPortfolio #dialogPortfolioForm #securityTypeDesc').val(dataPortfolioTable.security.securityType.description);
			$('#detailPortfolio #dialogPortfolioForm #securityCode').val(dataPortfolioTable.security.securityId);
			$('#detailPortfolio #dialogPortfolioForm #securityKey').val(dataPortfolioTable.security.securityKey);
			$('#detailPortfolio #dialogPortfolioForm #securityDesc').val(dataPortfolioTable.security.description);
			$('#detailPortfolio #dialogPortfolioForm #range').val(dataPortfolioTable.range);
			if (typeof dataPortfolioTable.range === 'string') {
				if (dataPortfolioTable.range === 'true') {
					dataPortfolioTable.range = true;
				} else if (dataPortfolioTable.range === 'false') {
					dataPortfolioTable.range = false;
				}
			}
			
			if (dataPortfolioTable.range) {
				$("#detailPortfolio #dialogPortfolioForm #range").attr("checked", "checked");
			} else {
				$("#detailPortfolio #dialogPortfolioForm #range").removeAttr("checked");
			}
			
			$('#detailPortfolio #dialogPortfolioForm #maximumRange').val(dataPortfolioTable.maximumRange);
			$('#detailPortfolio #dialogPortfolioForm #maximumRangeStripped').val(dataPortfolioTable.maximumRange);
			$('#detailPortfolio #dialogPortfolioForm #minimumRange').val(dataPortfolioTable.minimumRange);
			$('#detailPortfolio #dialogPortfolioForm #minimumRangeStripped').val(dataPortfolioTable.minimumRange);
			$('#detailPortfolio #dialogPortfolioForm #oldSecurityType').val($('#detailPortfolio #dialogPortfolioForm #securityType').val());
			$('#detailPortfolio #dialogPortfolioForm #newSecurityType').val($('#detailPortfolio #dialogPortfolioForm #oldSecurityType').val());
			$('#detailPortfolio #dialogPortfolioForm #oldSecurityKey').val($('#detailPortfolio #dialogPortfolioForm #securityKey').val());
			$('#detailPortfolio #dialogPortfolioForm #newSecurityKey').val($('#detailPortfolio #dialogPortfolioForm #oldSecurityKey').val());
			
			if (!(('${confirming}') || ('${mode}'==='view'))) {
				if ($('#range').is(":checked")){
					$('#maximumRange').removeAttr("disabled");
					$('#minimumRange').removeAttr("disabled");
				} else {
					$('#maximumRange').attr("disabled", "disabled");
					$('#minimumRange').attr("disabled", "disabled");
				}
			}
			
			$('.error').html("");
			$("#detailPortfolio").dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
		}
		
	});
}
	