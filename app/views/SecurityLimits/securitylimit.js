$(function() {
		if (('${mode}'==='entry')||(('${mode}'==='edit')&&(('${cpSecurity?.recordStatus?.decodeStatus()}'==='Reject')))){
			$('input[name=active]').attr("disabled", "disabled");
		}
		
		$('input[name=active]').change(function(){
			$("input[name='cpSecurity.active']").val($("input[name='active']:checked").val());
		});
		
		$('#ruleCode').change(function(){
			if ($('#ruleCode').val() === "") {
				$('#ruleDesc').val("");
			}
		});
		
		// RULE ID LOOKUP
//		$('#ruleCode').lookup({
//			list:'@{Pick.cpRulesByRuleType()}',
//			get:{
//				url:'@{Pick.cpRuleByRuleType()}',
//				success: function(data){
//					$('#ruleCode').removeClass('fieldError');
//					$('#ruleCode').val(data.name);
//					$('#ruleId').val(data.code);
//					$('#ruleDesc').val(data.description);
//					$('#h_ruleDesc').val(data.description);
//				},
//				error: function(data){
//					$('#ruleCode').addClass('fieldError');
//					$('#ruleCode').val('');
//					$('#ruleId').val('');
//					$('#ruleDesc').val('NOT FOUND');
//					$('#h_ruleDesc').val('');
//				}
//			},
//			filter:'CP_RULE_TYPE-004',
//			description:$('#ruleDesc'),
//			help:$('#ruleHelp'),
//			nextControl:$('#active')
//		});
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
		
		var data = new Object();
		tabSecurityLimitDetail(data);

		// ========================== PROCESS IN DETAIL SECURITY LIMIT FORM ========================//
		

		$('#tabsDetailSecurityLimit').tabs();
		
		$('.buttons button').button();
		$('.buttons input:button').button();
		
		$( "#detailSecurityLimitDetail" ).dialog({
			autoOpen:false,
			modal:true,
			heigth:'650px',
			width:'750px',
			resizable:false
		});

		$("#securityType").children().eq(0).remove();
		
		$('.buttons #newSecurityLimitDetailData').live('click', function() {
			selectedRow = null;
			$("#detailSecurityLimitDetail").dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
			$("#detailSecurityLimitDetail :text").removeClass("fieldError");
			$("#detailSecurityLimitDetail :text").val('');
			$("#detailSecurityLimitDetail :hidden").val('');
//			$('#detailSecurityLimitDetail #securityLimitForm #rowPosition').val('');
//			$('#detailSecurityLimitDetail #securityLimitForm #securityType').val('');
//			$('#detailSecurityLimitDetail #securityLimitForm #securityTypeDesc').val('');
			//$(".error").html("");
			$("#securityTypeError").html("");
			
			return false;
		});

		$('#securityType').change(function(){
			if ($('#securityType').val() == ""){
				$('#securityType').removeClass('fieldError');
				$('#securityType').val("");
				$('#securityTypeDesc').val("");
				$('#h_securityTypeDesc').val("");
				$('#newSecurityTypeKey').val("");
			}	
		});
		
		$('#securityType').lookup({
			list:'@{Pick.securityTypes()}',
			get:{
				url:'@{Pick.securityType()}',
				success: function(data){
					$('#securityType').removeClass('fieldError');
					$('#securityType').val(data.code);
					$('#securityTypeDesc').val(data.description);
					$('#h_securityTypeDesc').val(data.description);
					$('#newSecurityTypeKey').val(data.code);
				},
				error: function(data){
					$('#securityType').addClass('fieldError');
					$('#securityType').val('');
					$('#securityType').val('');
					$('#securityTypeDesc').val('NOT FOUND');
					$('#h_securityTypeDesc').val('');
				}
			},
			description:$('#securityTypeDesc'),
			help:$('#securityTypeHelp')
		});
		
		// ACTION SAVE PLEDGING DETAIL
		$('#saveSecurityLimitDetail').die("click");
		$('#saveSecurityLimitDetail').live("click", function(){
			var table = $('#listSecurityLimitDetail #gridSecurityLimit').dataTable();
			var rowPosition = $('#detailSecurityLimitDetail #securityLimitForm #rowPosition').val();
			if (($('#securityType').val()=='')){
				$("#securityTypeError").html('Required');
				
				return false;
			}
			
			var dataSecurityType = table.fnGetData(parseFloat(rowPosition));
			var securityTypeKey = $('#detailSecurityLimitDetail #securityLimitForm #securityType').val();
			var oldSecurityTypeKey = $('#detailSecurityLimitDetail #securityLimitForm #oldSecurityTypeKey').val();
			var newSecurityTypeKey = $('#detailSecurityLimitDetail #securityLimitForm #newSecurityTypeKey').val();
			var errMsg = "Security Type already exist!";
			if (rowPosition != "") {
				var found = false;
				var rows = table.fnGetNodes().length;
				for(i=0; i<rows; i++) {
					var cells = table.fnGetData(i);
					if ((cells.securityType.securityType == securityTypeKey)&&(oldSecurityTypeKey!=newSecurityTypeKey)){
						$('#errDetailSecLimitGlobal').html(errMsg);
						return false;
					}
				}
				
				if (!found) {
					
					//seharusnya pake yang ini
					//table.fnUpdate(((dataPledging.accountType.lookupDescription = $('#detailSecurityLimitDetail #securityLimitForm #accountTypeDesc').val())&&(dataPledging.accountType.lookupId = $('#detailSecurityLimitDetail #securityLimitForm #accountType').val())), parseFloat(rowPosition), 0);
					//sementara
					
					table.fnUpdate(((dataSecurityType.securityType.securityType = $('#detailSecurityLimitDetail #securityLimitForm #securityType').val())), parseFloat(rowPosition), 0);
					table.fnUpdate((dataSecurityType.securityType.description = $('#detailSecurityLimitDetail #securityLimitForm #securityTypeDesc').val()), parseFloat(rowPosition), 1);
					table.fnUpdate((dataSecurityType.securityLimitDetail = $('#detailSecurityLimitDetail #securityLimitForm #securityLimitDetail').val()), parseFloat(rowPosition), 2);
					
					dataSecurityType.securityType.securityType = $('#detailSecurityLimitDetail #securityLimitForm #securityType').val();
					dataSecurityType.securityType.description = $('#detailSecurityLimitDetail #securityLimitForm #securityTypeDesc').val();
					dataSecurityType.securityLimitDetail = $('#detailSecurityLimitDetail #securityLimitForm #securityLimitDetail').val();
					
					$('#detailSecurityLimitDetail').dialog('close');

					return false;
				}
			} else {
				
				var found = false;
				var rows = table.fnGetNodes().length;
				for(i=0; i<rows; i++) {
					var cells = table.fnGetData(i);
					if (cells.securityType.securityType == securityTypeKey){
						$('#errDetailSecLimitGlobal').html(errMsg);
						return false;
					}
				}
				if (!found) {
					var dataSecurityType = new Object();
					dataSecurityType.securityType = new Object();
					
					
					//seharusnya pake yg ini
//					dataPledging.accountType.lookupId = $('#detailSecurityLimitDetail #securityLimitForm #accountType').val();
					//sementara
					dataSecurityType.securityType.securityType = $('#detailSecurityLimitDetail #securityLimitForm #securityType').val();
					dataSecurityType.securityType.description = $('#detailSecurityLimitDetail #securityLimitForm #securityTypeDesc').val();
					dataSecurityType.securityLimitDetail = $('#detailSecurityLimitDetail #securityLimitForm #securityLimitDetail').val();
					table.fnAddData(dataSecurityType);
					$('#detailSecurityLimitDetail').dialog('close');
					
					return false;
				}
			}
		});
		
		$( "#cancelSecurityLimitDetail" ).click(function() {
			$('#detailSecurityLimitDetail').dialog('close');
			
			return false;
		});
		
		var closeDialog = function() {
			$("#dialog-message").dialog("close");
		};
		
		$('#listSecurityLimitDetail #gridSecurityLimit tbody tr #deleteButton').live('click', function() {
			var row = $(this).parents('tr');
			var rowNumber = tableSecurityLimit.fnGetPosition(row[0]);
			var deletePledging = function(){
				tableSecurityLimit.fnDeleteRow(rowNumber);
				var idTable = $("#listSecurityLimitDetail #gridSecurityLimit");
				var trs = $("tr", idTable);
				$.each(trs, function(idx, data){
					var hiddens = $("[type=hidden]", $(this));
						$(hiddens).eq(0).attr("name", "securityLimitDetails["+(idx-1)+"].securityType.securityType");
						$(hiddens).eq(1).attr("name", "securityLimitDetails["+(idx-1)+"].securityType.description");
						$(hiddens).eq(2).attr("name", "securityLimitDetails["+(idx-1)+"].securityType.securityLimitDetail");
				});
				$("#dialog-message").dialog("close");
				
				return false;
			}
			messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deletePledging, closeDialog);
		
		});
		
	}); // end ready function

	function tabSecurityLimitDetail(data) {
		var securityLimitDetails;
		if ('${securityLimitTabs}' != null )
			securityLimitDetails = ${securityLimitTabs.raw()};

		if ('${securityLimitDetails}' == null )
			securityLimitDetails = new Array();
		
		tableSecurityLimit = 
			$('#listSecurityLimitDetail #gridSecurityLimit').dataTable({
				aaData: securityLimitDetails,
				aoColumns: [
							{
								fnRender: function(obj){
									var controls;
									controls = obj.aData.securityType.securityType;
									controls += '<input type="hidden" name="securityLimitDetails[' + obj.iDataRow + '].securityType.securityType" value="' + obj.aData.securityType.securityType + '" />';
									return controls;
								}
							},
							{
								fnRender: function(obj){
									var controls;
									controls = obj.aData.securityType.description;
									controls += '<input type="hidden" name="securityLimitDetails[' + obj.iDataRow + '].securityType.description" value="' + obj.aData.securityType.description + '" />';
									return controls;
								}
							},
							{
								fnRender: function(obj) {
								 	var controls;
								 	controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
									controls += '<input type="hidden" name="securityLimitDetails[' + obj.iDataRow + '].securityLimitDetail" value="' + obj.aData.securityLimitDetail + '" />';
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
		
		$('#listSecurityLimitDetail #gridSecurityLimit ').removeAttr('style'); // tembak nilai widht untuk tampilan
		$('#listSecurityLimitDetail #gridSecurityLimit tbody tr td').die('click');
		$('#listSecurityLimitDetail #gridSecurityLimit tbody tr td').live('click', function() {
			var rowPos = $(this).parents('tr');
			if (tableSecurityLimit.fnGetNodes().length == 0) {
				return false;
			}
			var rowPosNumber = tableSecurityLimit.fnGetPosition(rowPos[0]);
			var pos = tableSecurityLimit.fnGetPosition(this);
			cell = tableSecurityLimit.fnGetData(this.parentNode);
			if (pos[1] != 2) {
				
				dataSecurityLimit = tableSecurityLimit.fnGetData(this.parentNode);
				
				$('#detailSecurityLimitDetail #securityLimitForm #rowPosition').val(rowPosNumber);
				$('#detailSecurityLimitDetail #securityLimitForm #securityType').val(dataSecurityLimit.securityType.securityType);
				$('#detailSecurityLimitDetail #securityLimitForm #securityTypeDesc').val(dataSecurityLimit.securityType.description);
				$('#detailSecurityLimitDetail #securityLimitForm #securityLimitDetail').val(dataSecurityLimit.securityLimitDetail);
				$('#detailSecurityLimitDetail #securityLimitForm #oldSecurityTypeKey').val($('#detailSecurityLimitDetail #securityLimitForm #securityType').val());
				$('#detailSecurityLimitDetail #securityLimitForm #newSecurityTypeKey').val($('#detailSecurityLimitDetail #securityLimitForm #oldSecurityTypeKey').val());
				$('.error').html("");
				$("#detailSecurityLimitDetail").dialog('open');
				$('.ui-widget-overlay').css('height',$('body').height());
			}
			
		});
	};
	
	
	function doSave(){
		/*if (tableSecurityLimit.fnGetNodes().length < 1){
			$('#errSecurityLimitDetail').html("*Error saving! Make sure there is a minimum of one data in 'Security Type'");
			return false;
		}*/
		
		return true;
	};
	