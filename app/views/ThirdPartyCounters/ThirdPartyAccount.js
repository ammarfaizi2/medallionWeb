$(function(){	
	/*----------------------------
	 * Set Component & Form Detail
	 *----------------------------*/	
	var closeDialog = function() {
		$("#dialog-message").dialog('close');	
	}
	
	$('#newThirdParty').button();
	$('#tabsThirdParty').tabs();
	$('#addThirdPartyAccount').button();
	$('#cancelThirdPartyAccount').button();
	
	$( "#detailThirdPartyAccount" ).dialog({
		autoOpen:false,
		modal:true,
		heigth:'600px',
		width:'400px',
		resizable:false
	}); 
	
	/*----------------------------
	 * Set DataTable
	 *----------------------------*/
	var data = new Object();
	tabsThirdParty(data);
	
	//table Third Party Account
	function tabsThirdParty(data) {
		var thirdPartyDetails = null;
		
		#{if '${thirdPartyAccounts}' != '' }
			if(${thirdPartyAccounts.raw()} == null){
				thirdPartyDetails = new Array();
			}else{
				thirdPartyDetails = ${thirdPartyAccounts.raw()};
			}
		#{/if}
		#{if '${thirdPartyAccounts}' == '' }
			thirdPartyDetails = new Array();
		#{/if}
				
		tableThirdParty = 
			$('#listThirdParty #gridThirdParty').dataTable({
				aaData: thirdPartyDetails,
				aoColumns: [ {
				 				fnRender: function(obj) {
								  	var controls;
									controls = obj.aData.accountCategory.lookupDescription;
								  	controls += '<input type="hidden" name="thirdPartyAccountss[' + obj.iDataRow + '].accountCategory.lookupId" value="' + obj.aData.accountCategory.lookupId + '" />';
									controls += '<input type="hidden" name="thirdPartyAccountss[' + obj.iDataRow + '].accountCategory.lookupDescription" value="' + obj.aData.accountCategory.lookupDescription + '" />';
									return controls;
								 }
							  }, 
							  {
								  fnRender: function(obj) {
									  	var controls;
										controls = obj.aData.accountCode;
										if((obj.aData.accountCode==null)||(controls=='null')){
											controls="";
										}
										controls += '<input type="hidden" name="thirdPartyAccountss[' + obj.iDataRow + '].accountCode" value="' + obj.aData.accountCode + '" />';
									  	return controls;
								  }
							  },
							  {
								  fnRender: function(obj) {
									  	var controls;
										controls = obj.aData.accountAgentCode;
										if((obj.aData.accountAgentCode==null)||(controls=='null')){
											controls="";
										}
										controls += '<input type="hidden" name="thirdPartyAccountss[' + obj.iDataRow + '].accountAgentCode" value="' + obj.aData.accountAgentCode + '" />';
									  	return controls;
								  }
							  },
							  {
								  fnRender: function(obj) {
									  	var controls;
										controls = obj.aData.investorCode;
										if((obj.aData.investorCode==null)||(controls=='null')){
											controls="";											
										}
										
										controls += '<input type="hidden" name="thirdPartyAccountss[' + obj.iDataRow + '].investorCode" value="' + obj.aData.investorCode + '" />';
									  	return controls;
								  }
							  },
							  {
								  fnRender: function(obj) {
									  	var controls;
										controls = obj.aData.subAccountCode;
										if((obj.aData.subAccountCode==null)||(controls=='null')){
											controls="";
										}
										controls += '<input type="hidden" name="thirdPartyAccountss[' + obj.iDataRow + '].subAccountCode" value="' + obj.aData.subAccountCode + '" />';
									  	return controls;
								  }
							  },
							  {
								  fnRender: function(obj) {
									  	var controls;
										controls = obj.aData.cashAccount;
										if((obj.aData.cashAccount==null)||(controls=='null')){
											controls="";
										}
										controls += '<input type="hidden" name="thirdPartyAccountss[' + obj.iDataRow + '].cashAccount" value="' + obj.aData.cashAccount + '" />';
									  	return controls;
								  }
							  },
							  {
								fnRender: function(obj) {
								 	var controls;
								 	controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
								 	controls += '<input type="hidden" name="thirdPartyAccountss[' + obj.iDataRow + '].thirdPartyAccountKey" value="' + obj.aData.thirdPartyAccountKey + '" />';
								 	return controls;
							 	}
							  }
							  
							],
				aaSorting:[[1,'asc']],
	        	bAutoWidth: false,		
	        	bDestroy: true,
	        	bFilter: false,
	        	bInfo: false,
	        	bJQueryUI: true,
	        	bPaginate: false,
	        	bSearch: false,
	        	bLengthChange: false,
	        	isDisplayLength:10
		});
				
		$('#listThirdParty #gridThirdParty' ).removeAttr('style');
		$('#listThirdParty #gridThirdParty tbody tr td').die('click');
		$('#listThirdParty #gridThirdParty tbody tr td').live('click', function(){
			
			var txt = $(this).text();
			if(txt == 'No data available in table')
			{
				return false;
			}else{
				var rowPos= $(this).parents('tr');
				var rowPosNumber = tableThirdParty.fnGetPosition(rowPos[0]);
				var pos = tableThirdParty.fnGetPosition(this);
				cell = tableThirdParty.fnGetData(this.parentNode);
				if (pos[1] != 7) {
					thirdPartyAccountDetails = tableThirdParty.fnGetData(this.parentNode);
					$("#detailThirdPartyAccount #inputThirdParty .errmsg").html("");
					$("#detailThirdPartyAccount #inputThirdParty").find("span[id*='Error']").html("");
					$("#detailThirdPartyAccount #inputThirdParty #rowPosition").val(rowPosNumber);
					$("#detailThirdPartyAccount #inputThirdParty #thirdPartyAccountKey").val(thirdPartyAccountDetails.thirdPartyAccountKey);
					$("#detailThirdPartyAccount #inputThirdParty #accountCat").val(thirdPartyAccountDetails.accountCategory.lookupId);
					$("#detailThirdPartyAccount #inputThirdParty #lookupDescription").val(thirdPartyAccountDetails.accountCategory.lookupDescription);
					$("#detailThirdPartyAccount #inputThirdParty #accountCode").val(thirdPartyAccountDetails.accountCode);
					$("#detailThirdPartyAccount #inputThirdParty #accountAgentCode").val(thirdPartyAccountDetails.accountAgentCode);
					$("#detailThirdPartyAccount #inputThirdParty #investorCode").val(thirdPartyAccountDetails.investorCode);
					$("#detailThirdPartyAccount #inputThirdParty #subAccountCode").val(thirdPartyAccountDetails.subAccountCode);
					$("#detailThirdPartyAccount #inputThirdParty #cashAccount").val(thirdPartyAccountDetails.cashAccount);
					$("#detailThirdPartyAccount #inputThirdParty #accountCat").attr('disabled', true);
					$("#detailThirdPartyAccount").dialog('open');
					$('.ui-widget-overlay').css('height',$('body').height());	
				}
			}
		});
	}
	
	/*----------------------------
	 * Action
	 *----------------------------*/
	//Reordering
	function reordering() {

		var grid = $('#listThirdParty #gridThirdParty tbody');
		var trs = $("tr", grid);

		$.each(trs, function(idx, data){
			var hiddens = $("[type=hidden]", $(this));
			$(hiddens).eq(0).attr("name", "thirdPartyAccountss["+idx+"].accountCategory.lookupId");
			$(hiddens).eq(1).attr("name", "thirdPartyAccountss["+idx+"].accountCategory.lookupDescription");
			$(hiddens).eq(2).attr("name", "thirdPartyAccountss["+idx+"].accountCode");
			$(hiddens).eq(3).attr("name", "thirdPartyAccountss["+idx+"].accountAgentCode");
			$(hiddens).eq(4).attr("name", "thirdPartyAccountss["+idx+"].investorCode");
			$(hiddens).eq(5).attr("name", "thirdPartyAccountss["+idx+"].subAccountCode");
			$(hiddens).eq(6).attr("name", "thirdPartyAccountss["+idx+"].cashAccount");
			$(hiddens).eq(7).attr("name", "thirdPartyAccountss["+idx+"].thirdPartyAccountKey");
		});
	}
		
	//Button detail
	$('.buttons #newThirdParty').click(function() {
			selectedRow = null;
			$("#detailThirdPartyAccount").dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
			$("#detailThirdPartyAccount #accountCat").val("");
			$("#detailThirdPartyAccount #lookupDescription").val("");
			$("#detailThirdPartyAccount input:text").val(""); 
			$("#detailThirdPartyAccount input:hidden").val("");
			$("#accountCategoryError").html('Required').hide();
			$("#accountCodeError").html('Required').hide();
			$("#accountAgentCodeError").html('Required').hide();
			$("#investorCodeError").html('Required').hide();
			$("#subAccountCodeError").html('Required').hide();
			$("#cashAccountError").html('Required').hide();
			$("#detailThirdPartyAccount #inputThirdParty #accountCat").attr('disabled', false);
		return false;
	});
	
	//Cancel
	$('#cancelThirdPartyAccount').die("click");
	$('#cancelThirdPartyAccount').live("click", function(){
		$('#detailThirdPartyAccount').dialog('close');
	});
	
	//Save data detail
	$('#addThirdPartyAccount').die("click");
	$('#addThirdPartyAccount').live("click", function(){
		setTimeout(function() {
		var table = $('#listThirdParty #gridThirdParty').dataTable();
		var rowPosition = $("#detailThirdPartyAccount #inputThirdParty #rowPosition").val();
		var i = 0;
		savethirdPartyAccount();
		
		function savethirdPartyAccount(){
			if (($("#detailThirdPartyAccount #inputThirdParty #accountCat").val()=="") || ($("#detailThirdPartyAccount #inputThirdParty #accountCode").val()=="") ||
				($("#detailThirdPartyAccount #inputThirdParty #accountAgentCode").val()=="") || ($("#detailThirdPartyAccount #inputThirdParty #investorCode").val()=="") ||
				($("#detailThirdPartyAccount #inputThirdParty #subAccountCode").val()=="") || ($("#detailThirdPartyAccount #inputThirdParty #cashAccount").val()=="")){
				$("#detailThirdPartyAccount #inputThirdParty").find("span[id*='Error']").html("");
				
				if ($("#detailThirdPartyAccount #inputThirdParty #accountCat").val()=="") {
					$("#accountCategoryError").html('Required').show();
				} 
				if ($("#detailThirdPartyAccount #inputThirdParty #accountCode").val()=="") {
					$("#accountCodeError").html('Required').show();
				}
				if ($("#detailThirdPartyAccount #inputThirdParty #accountAgentCode").val()=="") {
					$("#accountAgentCodeError").html('Required').show();
				}
				if ($("#detailThirdPartyAccount #inputThirdParty #investorCode").val()=="") {
					$("#investorCodeError").html('Required').show();
				}
				if ($("#detailThirdPartyAccount #inputThirdParty #subAccountCode").val()=="") {
					$("#subAccountCodeError").html('Required').show();
				}
				if ($("#detailThirdPartyAccount #inputThirdParty #cashAccount").val()=="") {
					$("#cashAccountError").html('Required').show();
				}
				
			} else {
				var thirdPartyAccountDetails = table.fnGetData(parseFloat(rowPosition));
				if (rowPosition != "") {
					var found = false;
					// CHECKED DUPLICATE ROW
					var rows = table.fnGetNodes().length;
					for (i = 0; i < rows; i++) {
						var cells = table.fnGetData(i);
					} 
					if (!found) {
						table.fnUpdate(thirdPartyAccountDetails.accountCategory.lookupId = $("#detailThirdPartyAccount #inputThirdParty #accountCat").val(), parseFloat(rowPosition),0);
						table.fnUpdate(thirdPartyAccountDetails.accountCode = $("#detailThirdPartyAccount #inputThirdParty #accountCode").val(),parseFloat(rowPosition),1);
						table.fnUpdate(thirdPartyAccountDetails.accountAgentCode = $("#detailThirdPartyAccount #inputThirdParty #accountAgentCode").val(),parseFloat(rowPosition),2);
						table.fnUpdate(thirdPartyAccountDetails.investorCode = $("#detailThirdPartyAccount #inputThirdParty #investorCode").val(),parseFloat(rowPosition),3);
						table.fnUpdate(thirdPartyAccountDetails.subAccountCode = $("#detailThirdPartyAccount #inputThirdParty #subAccountCode").val(),parseFloat(rowPosition),4);
						table.fnUpdate(thirdPartyAccountDetails.cashAccount = $("#detailThirdPartyAccount #inputThirdParty #cashAccount").val(),parseFloat(rowPosition),5);
						console.log("hehe");
						$('#detailThirdPartyAccount').dialog('close');
					}
				} else {
					var found = false;
					// CHECKED DUPLICATE ROW
					var rows = table.fnGetNodes().length;
					for (i = 0; i < rows; i++) {
						var cells = table.fnGetData(i);
						if(cells.accountCategory.lookupId == $("#detailThirdPartyAccount #inputThirdParty #accountCat").val()){
							var found = true;
							messageAlertOk("Depository already exist", "ui-icon ui-icon-notice", "Confirmation Message", closeDialog);
						};
					} 
					if (!found) {
						var thirdPartyAccountDetails = new Object();
						thirdPartyAccountDetails.thirdParty = new Object();
						thirdPartyAccountDetails.accountCategory = new Object();
						thirdPartyAccountDetails.thirdPartyAccountKey = $("#detailThirdPartyAccount #inputThirdParty #thirdPartyAccountKey").val();
						thirdPartyAccountDetails.accountCategory.lookupId = $("#detailThirdPartyAccount #inputThirdParty #accountCat").val();
						thirdPartyAccountDetails.accountCategory.lookupDescription = $("#detailThirdPartyAccount #inputThirdParty #lookupDescription").val();
						thirdPartyAccountDetails.accountCode = $("#detailThirdPartyAccount #inputThirdParty #accountCode").val();
						thirdPartyAccountDetails.accountAgentCode = $("#detailThirdPartyAccount #inputThirdParty #accountAgentCode").val();
						thirdPartyAccountDetails.investorCode = $("#detailThirdPartyAccount #inputThirdParty #investorCode").val();
						thirdPartyAccountDetails.subAccountCode = $("#detailThirdPartyAccount #inputThirdParty #subAccountCode").val();
						thirdPartyAccountDetails.cashAccount = $("#detailThirdPartyAccount #inputThirdParty #cashAccount").val();
						console.log("haha");
						table.fnAddData(thirdPartyAccountDetails);					
						$('#detailThirdPartyAccount').dialog('close');
					};	
				}
				return false;		
			};
		};
	   }, 500);
	});

	//Delete data detail
	$('#listThirdParty #gridThirdParty tbody tr #deleteButton').live('click', function() {
		var row = $(this).parents('tr');
		var rowNumber = tableThirdParty.fnGetPosition(row[0]);
		var deletetableThirdParty = function(){
			tableThirdParty.fnDeleteRow(rowNumber);
			reordering();
			$("#dialog-message").dialog('close');
		}
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deletetableThirdParty, closeDialog);
		return false;
	});
	
	//Lookup Description		
	$('#accountCat').change(function(){
		//$("#lookupDescription").val($.trim($("#lookupId option:selected").text()));
		$("#lookupDescription").val($.trim($("#accountCat option:selected").text()));
		//$("#lookupId").val($.trim($("#accountCat option:selected").val()));
	});

});