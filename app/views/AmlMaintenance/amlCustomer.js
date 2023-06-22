var ADDRESS_TYPE_1 = '${addressType1}';
var ADDRESS_TYPE_2 = '${addressType2}';

$(function(){
	CUSTOMER_TYPE_I = '${typeIndi}';
	CUSTOMER_TYPE_C = '${typeCorp}';
	var eddId = $('#eddIdHide').val();
	$('#eddId').val(eddId);
	var apiStatus = $('#apiStatus').val();
	if (apiStatus == '${amlStatusWatingApprovalEdd}' 
		|| apiStatus == '${amlStatusWatingBOApprovalEdd}'
			|| apiStatus == '${amlStatusWaitingBOWatchlist}'
				|| apiStatus == '${amlStatusWaitingBOStatus}') {
		enableTipeIndividuBo();
	} else if (apiStatus == '${amlStatusDone}') {
		$('#amlInterface').disabled();
		if ('${isBeneficiaryOwner}' == 'false') {
			$('input[name=amlInterfaceRadio][value=aml.tipeIndividu]').attr('checked', true);
			$('input[name=amlInterfaceRadio][value=aml.tipeIndividu]').attr('disabled', true);
			$('input[name=amlInterfaceRadio][value=aml.tipeIndividu.bo]').attr('disabled', true)
		} else {
			$('input[name=amlInterfaceRadio][value=aml.tipeIndividu.bo]').attr('checked', true);
			$('input[name=amlInterfaceRadio][value=aml.tipeIndividu.bo]').attr('disabled', true);
			$('input[name=amlInterfaceRadio][value=aml.tipeIndividu]').attr('disabled', true);
		}
	}else {
		$('input[name=amlInterfaceRadio][value=aml.tipeIndividu]').attr('checked', true);
		$('input[name=amlInterfaceRadio][value=aml.tipeIndividu.bo]').attr('disabled', true);
	}
	
	if ('${param}' == 'edit' || '${param}' == 'view' || '${confirming}'=='true' || (('${mode}'=='edit') && ('${param}'=='entry') && ('${isReadOnly}' == 'true'))) {
		$('#newContact').disabled();
		$('input[name=amlInterfaceRadio][value=aml.tipeIndividu]').attr('disabled', true);
		$('input[name=amlInterfaceRadio][value=aml.tipeIndividu.bo]').attr('disabled', true);
	}
	
	var select_customer = $('#customerType').val();
	
	if (select_customer){
	  if ('${isBeneficiaryOwner}' == 'false' && select_customer == '${typeIndi}') {
			$('#newContact').disabled();
	  }
	}

	$("[type='button']", this).button();
	
	if("${paramCashProjection}" == "1"){
		$('#pCashprojection').css('display', '');
	}else{
		$('#pCashprojection').css('display', 'none');
	}

	$('.specialField').live('keydown', function(event) {
		if ((event.shiftKey && (event.which==55 || event.which==188 || event.which==190 || 
			event.which==222 )) || (event.which==222)) {				
			event.preventDefault();
			return false;
		}
	});
	 
	$('.specialField').live('keyup', function(event) {
		if ((event.shiftKey && (event.which==55 || event.which==188 || event.which==190 || 
			event.which==222 )) || (event.which==222)) {
			
			$(this).val($(this).val().toUpperCase().replace(/&/gi,"").replace(/\?/gi,""));
			$(this).val($(this).val().toUpperCase().replace(/</gi,"").replace(/\?/gi,""));
			$(this).val($(this).val().toUpperCase().replace(/>/gi,"").replace(/\?/gi,""));
			$(this).val($(this).val().toUpperCase().replace(/'/gi,"").replace(/\?/gi,""));
			$(this).val($(this).val().toUpperCase().replace(/"/gi,"").replace(/\?/gi,""));
			return false;
		}
	});
	
	var currentDate = $('#currentDate').val();
	var closeDialog = function() {
		$("#dialog-message").dialog('close');
	}
	
	$('#continueCust #continueBank #save #saveEdit #mainCancel #confirm #back').button();
	if ($('#customerType').val()==''){
    	$("p.individual_only").css("display", "none");
    	$("p.corporate_only").css("display", "none");
	}
	
	if ($('#customerType').val()==CUSTOMER_TYPE_I){
    	$("p.individual_only").css("display", "block");
    	$("p.corporate_only").css("display", "none");
	}
	
	if ($('#customerType').val()==CUSTOMER_TYPE_C){
    	$("p.individual_only").css("display", "none");
    	$("p.corporate_only").css("display", "block");
	}

	$("#dialog-message-1").css("display","none");
	$("#dialog-message-2").css("display", "none");
	$('#tabs').tabs();
	$('#detailContact').css("display","none");
	$('#interfaceAml').css("display","none");
	
	if ('${mode}' == 'view'){
		$('#customerType').change(adjustCustomerType);
		adjustCustomerType();
		if ($('#param').val() == 'customer-inquiry') {
			$("#tabs").tabs("select", "#tabs-6");
		}
	}
	
	if ('${mode}' == 'edit'){
		$('#customerType').change(adjustCustomerType);
		adjustCustomerType();
	}

	if ('${mode}' == 'entry'){
		adjustCustomerType();
		// investorType();
	}
	
	if ('${param}' == 'edit'){
		$('#amlInterface').disabled();
	}
	
	var detailContacts = null;
	detailContacts = ${detailContacts};
	tabContact(detailContacts);
	
	var detailWatchList = null;
	detailWatchList = ${detailWatchList};
	var watchListDataTable = tblAmlWatchList(detailWatchList);
	
	if (('${mode}'=='entry') || ((('${mode}'=='edit') || ('${mode}'=='edit')) && (('${customer?.recordStatus?.decodeStatus()}' == 'Reject') || ($("#status").val() == 'R' )))) {
		// $('input[name=isActive]').attr("disabled", "disabled");
		$('input[name=amlCustomer.isActive]').attr("disabled", "disabled");
	}
	
	// $('input[name=isActive]').change(function(){
	$('input[name=amlCustomer.isActive]').change(function(){
		
		var isActiveTrue = function() {
			$("input[name=amlCustomer.isActive]")[0].checked = true;
			$("input[name='amlCustomer.isActive']").val($("input[name='amlCustomer.isActive']").is(":checked"));
			$("#dialog-message").dialog("close");
		} 
		
		$("input[name='amlCustomer.isActive']").val($("input[name='amlCustomer.isActive']:checked").val());
		
		
	});
	
	$('.buttons input:button').button();
	
	if ( '${mode}' == 'view' || '${param}' == 'edit' || '${confirming}'=='true' || '${taskId}' != '' || ('${isReadOnly}'  == 'true')) {
		$('#saveCon').css("display","none");
		$('#cancelCon').css("display","none");
		$('#saveConCorp').css("display","none");
		$('#cancelConCorp').css("display","none");
	} else {
		$('#closeCon').css("display","none");
		$('#closeConCorp').css("display","none");
	}
			
	// birthdate validation can not be greather than current date
	// ===========================================================
	$('#birthDate').change(function(){
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if (!$(this).hasClass('fieldError')){
			if (compareDateMustBeforeApplicationDate(thisId, value)){
				var age = getAge($('#birthDate').datepicker('getDate'));
				if (age < 17){
					$(this).addClass('fieldError');
					$("#birthDateError").html("Must have 17 years old or older!");
					return false;
				} else {
					$(this).removeClass('fieldError');
					$("#birthDateError").html("");	
				}
			};
		}
		
	});
	// ======================================================================================================================
	
	// date of company estb validation can not be greather than current date
	// ================================================
	$('#dateCompEstb').change(function(){
		
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if (!$(this).hasClass('fieldError')){
			compareDateMustBeforeApplicationDate(thisId, value);
		}
	});
	// ======================================================================================================================

	// customer type action change
	// ==========================================================================================
	buildSourceOfFund();
	$('#customerType').change(function() {
		var select_customer; 
        select_customer = $('#customerType').val();
		var param = '${param}';
  		if  (select_customer == CUSTOMER_TYPE_C) {
  			$(".corporate").val("");
  			if (param) {
	  	    	$(".individual_only").css("display", "none");
	  	    	$(".corporate_only").css("display", "block");
  			}
    		$('#newContact').enabled();

    		buildSourceOfFund();
		} else if  (select_customer == CUSTOMER_TYPE_I) {
			$(".individual").val("");
  			if (param) {
	        	$(".individual_only").css("display", "block");
	        	$(".corporate_only").css("display", "none");
  			}
    		if ('${isBeneficiaryOwner}' == 'true') {
    			$('#newContact').enabled();
    		} else {
    			$('#newContact').disabled();
    		}
    		buildSourceOfFund();
		} else {

			// $(".corporate").val("");
        	$(".all").val("");
        	$(".empty").val("");
        	$('#udfCorporate').css('display', 'none');
    		$('#udfIndividual').css('display', 'none');
    		$('#udfGlobal').css('display', 'block');
		}

		var table = $("#grid-contact-corp").dataTable();
		table.fnClearTable();
		
		adjustCustomerType();
		$('.all').attr('checked', false);
		var checkError = $("input").hasClass('fieldError'); {
			if (checkError){
				$('input').removeClass('fieldError');
			}
		}
		// ==================================================================================================================
		// $('#customerTypeHide').val($('#customerType').val());
	});
	// ======================================================================================================================
		
	// occupation action change
	// ==========================================================================================
	$('#occupation').change(function() {
		var lookupId = $('#occupation').val();
        chekIsBenefiary(lookupId);
	});

	var lookupId = $('#occupation').val();
    chekIsBenefiary(lookupId);
	
	// ======================================================================================================================
	
	// for check identification type 1 in tab contact
	// ====================================================================
	$('#identificationType').change(function() {
		$('#identificationTypeHide').val($(this).val());
		$('#identificationNo').val('');
	});
	// ======================================================================================================================
	$('#riskProfile').change(function() {
		if ($(this).val() == '${riskProfileHigh}') {
			$("p[atr=isHightReasonMandatory] label span").html(" *");
		} else {
			$("p[atr=isHightReasonMandatory] label span").html("");
		}
	});
	
	var riskProfile = $("#riskProfile").val();
	if (riskProfile == '${riskProfileHigh}') {
		$("p[atr=isHightReasonMandatory] label span").html(" *");
	} else {
		$("p[atr=isHightReasonMandatory] label span").html("");
	}

	$('#save').click(function(){

		if (validateForm()) {

				if ($('#customerType').val()=='${typeIndi}'){
					$(".corporate_only").remove();
					$(".default").remove();
					$("#udfGlobal").remove();
					$("#udfCorporate").remove();
			    	$(".udf_type_all").remove();
			    } else {
			    	$(".individual_only").remove();
			    	$(".default").remove();
			    	$("#udfGlobal").remove();
					$("#udfIndividual").remove();
			    	$(".udf_type_all").remove();
			    }
			
			  var identityNo = $('#identificationNo').val();
			  var documentNo = $('#documentNo').val();
			  var npwp = $('#npwp').val();
			  var amlKey = $('#amlKey').val();
			  
			  if ((identityNo || documentNo)) {
				  
				  if (!identityNo) {
					  identityNo = documentNo;
				  }
				  
				  if (!npwp) {
					  npwp = null;
				  }
		
					$().fetchSync("@{AmlMaintenance.checkIdentityAndNpwp()}", 
							{
								'amlKey' : amlKey,
								'npwp' : npwp,
								'identity' : identityNo
							}, function(datas) {
								if (datas) {
									var resultCode = datas.resultCode;
									var resultMessage = datas.resultMessage;
		
									var checkYesFunction = function() {
										$("#dialog-message").dialog("close");
										saveProcess();
									};
									var checkNoFunction = function() {
										$("#dialog-message").dialog("close");
									};
									if (resultCode == "400") {
										messageAlertYesNo("Duplicate " + resultMessage + " found. Are you sure to continue?", "ui-icon ui-icon-notice", "Confirmation Message", checkYesFunction, checkNoFunction);
									} else {
										saveProcess();
									}
								}
						});
				  
				  
			  } else {
					saveProcess();
			  }
		} else {
			console.log("not validForm()");
		}
			
	});
	
	$('#mainCancel').click(function(){
		if ('${mode}' == 'entry' || '${param}' == 'entry') {
			location.href='@{AmlMaintenance.dedupe()}';
			return false;
		}
		location.href='@{list()}?mode=edit' + '&param=edit' ;
		return false;
	});
	
	$('#confirm').click(function(){
		var select_customer; 
		select_customer = $('#customerType').val();
	    	if (select_customer == '${typeIndi}'){
		    	$(".corporate_only").remove(); 
		    	$(".default").remove();
		    	$("#udfGlobal").remove();
				$("#udfCorporate").remove();
		    	$(".udf_type_all").remove();
			} else {
		    	$(".individual_only").remove();
		    	$(".default").remove();
		    	$("#udfGlobal").remove();
				$("#udfIndividual").remove();
		    	$(".udf_type_all").remove();
		    }   
			var apiStatus = $('#apiStatus').val();
			var amlStatus = $('#amlStatus').val();
			
			if (apiStatus == '${amlStatusDone}') {
				var closeDialog = function(){
					confirm(apiStatus, amlStatus);
					$("#dialog-message").dialog("close");
				};
				
				var amlId = getAmlId();
				
				var alertMessage = "is generated";
				if ('${param}' == 'edit') {
					alertMessage = "is updated";
				}
				messageAlertOk("AML Key: '"+amlId+"' "+alertMessage, "ui-icon ui-icon-notice", "Warning", closeDialog);
			} else {
				confirm(apiStatus, amlStatus);
			}
	});
	
	$('#back').click(function(){
		location.href='@{back(id)}?mode=${mode}&param=${param}#{if status}&status=${status}#{/}#{if group}&group=${group}#{/}#{if from}&from=${from}#{/}#{if isPartial}&isPartial=${isPartial}#{/}';
		return false;
	});
	
	$("#detailContact").dialog({
		autoOpen:false,
		heigth:'700px',
		width:'auto',
		resizable:false,
		modal:true			
	});
	
	$("#interfaceAml").dialog({
		autoOpen:false,
		heigth:'700px',
		width:'auto',
		resizable:false,
		modal:true			
	});

	$('#backCustBankInvt').click(function() {	
		// history.back();
		if ('${param}' != "") {
			if ('${param}' == 'dedupe' || '${param}' == 'entry') {
				location.href='@{AmlMaintenance.dedupe()}';
			} else if('${param}'=='edit') {
				location.href='@{list()}?mode=edit&param=edit';
			} else {
				location.href='@{list()}#{if mode}?mode=${mode}&param=${param}#{/}';
			}
		} else {
			location.href='@{list()}?mode=view';
		}
		
		return false;
	});
	
	var tblAmlWatchListData = $("#tblAmlWatchListData").dataTable({
		"bAutoWidth": false,
		"bFilter": false,
		"bInfo": false,
		"bSearch": false,
		"bJQueryUI": true,
		"bPaginate": false,
        "bLengthChange": false,
		"sPaginationType": "full_numbers",
        "bSort": false,
		"iDisplayLength":10
	});
	
	$('#amlInterface').click(function() {	
		var isBlackList = $('#blackList').val();
		if (isBlackList == '${blackListYes}') {
			messageAlertOk('Interface To AML cant process, cause Customer is blacklist.! ', "ui-icon ui-icon-notice", "Warning", closeDialog);
		} else {
			
			var apiStatus = $('#apiStatus').val();
			console.log("apiStatus: " + apiStatus);
			if (!apiStatus) {
				checkNameAndProfile();
			} else if (apiStatus == '${amlStatusWaitingBOStatus}') {
				checkNameAndProfile();
			} else if (apiStatus == '${amlStatusWaitingBOWatchlist}' || apiStatus == '${amlStatusWaitingWatchlist}') {
				confirmScreening();
			} else if ( apiStatus == '${amlStatusWatingApprovalEdd}' || apiStatus == '${amlStatusWatingBOApprovalEdd}') {
				checkEddStatus();
			} else if ( apiStatus == '${amlStatusDone}') {
				messageAlertOk('Interface to AML success and DONE', "ui-icon ui-icon-notice", "Warning", closeDialog);	
			}
			
		}
	});
	
	$("#checkAll").click(function(){
		var isChecked = $(this).is(':checked');
		$(watchListDataTable.fnGetNodes()).each(function(i, e){
			var b = $(this).data("bean");
			var inputChk = $("input", $("td", $(this)).eq(0));
			inputChk.attr("checked", "checked");
		});
		
		$(watchListDataTable.fnGetNodes()).each(function(i, e){
			var b = $(this).data("bean");
			var inputChk = $("input", $("td", $(this)).eq(0));
			if (isChecked) {
				inputChk.attr("checked"); 
			} else {
				inputChk.removeAttr("checked"); 
			}
		});
	});
	
	// BLOCK customer ROW prohibited
	// ==================================================================================================
	$('#watchListSegment #tblAmlWatchList tbody tr #watchListCheckbox').live('click', function() {
		var interfaceKey = getInterfaceKey();
		var isChecked = $(this).is(':checked');
		if (isChecked) {
			if ('${mode}' != 'view') {
				var table = $('#tblAmlWatchList').dataTable();
				var rows = table.fnGetNodes().length;
				var row = $(this).parents('tr');
				var rowNumber = tableContact.fnGetPosition(row[0]);
				var datas = table.fnGetData(row[0]);
				var isProhibited = false;
				
				if (datas) {
					if (datas.interfaceKey != interfaceKey) {
						$(this).attr('checked','checked');
					}

					var checkIncludes = datas.matchSource.includes('Prohibited');
					if (checkIncludes) {
						if (checkIncludes) {
							isProhibited = checkIncludes;
						}
					} else {
						checkIncludes = datas.matchSource.includes('prohibited');
						if (checkIncludes) {
							isProhibited = checkIncludes;
						}
					}
				}
	
				var prohibitedYesFunction = function() {
					$('input[name=amlCustomer.isActive][value=false]').attr('checked', true);
					$("#blackList").val("${blackListYes}");
					$("#dialog-message").dialog("close");
					$('#interfaceAml').dialog("close");
				};
				var prohibitedNoFunction = function() {
					$("#blackList").val("${blackListNo}");
					$("#dialog-message").dialog("close");
					$('#interfaceAml').dialog("close");
				};
				
				/*if (isProhibited) {
					messageAlertYesNo("Are you sure to block this customer ?", "ui-icon ui-icon-notice", "Confirmation Message", prohibitedYesFunction, prohibitedNoFunction);
				}*/
			}
		}
	});
	// ======================================================================================================================
	
	disableAmlWatchlist();
	
});

function getAmlId() {
	var table = $('#tblAmlWatchList').dataTable();
	var datas = table.fnGetData();
	
	var amlId = "";
	for (var row in datas) {
		var data = datas[row];
		if (data.interfaceKey == '${tipeIndividuKey}') {
			amlId = data.amlId;
			break;
		}
	}
	
	if (!amlId) {
		amlId = $('#amlId').val();
	}
	
	return amlId;
}

function chekIsBenefiary(lookupId) {
	var select_customer; 
    select_customer = $('#customerType').val();

	$().fetchSync("@{AmlMaintenance.chekIsBenefiary()}", 
			{
				'lookupId' : lookupId
			}, function(datas) {
				var isBeneficiaryOwner = datas.isBeneficiaryOwner;
				if (isBeneficiaryOwner || select_customer == CUSTOMER_TYPE_C) {
					$('#newContact').enabled();
					$('#isBO').val('true');
				} else {
					$('#newContact').disabled();
					$('#isBO').val('false');
				}
		});
}

function confirm(apiStatus, amlStatus) {
	var isActive = $('input[name="amlCustomer.isActive"]:checked').val();
	var action ='@{confirm()}?mode=${mode}&apiStatus='+ apiStatus +'&amlStatus='+ amlStatus +'&isActive='+ isActive +'#{if group}&group=${group}#{/}#{if isPartial}&isPartial=${isPartial}#{/}';
	loading.dialog('open');
	$('#customerForm').attr('action', action);
	$('#customerForm').submit();
}

function saveProcess() {
	var watchListDataTable = $('#tblAmlWatchList').dataTable();
	var apiStatus = $('#apiStatus').val();
	var amlStatus = $('#amlStatus').val();
	var amlId = $('#amlId').val();
	var eddId = $('#eddId').val();
	var isChecked = $('#checkAll').is(':checked');
	/*if (!apiStatus && ('${param}' && '${param}' != 'edit') ) {
		var closeDialog = function(){
			$("#dialog-message").dialog("close");
		};
		
		messageAlertOk("Please hit Interface to AML first.!", "ui-icon ui-icon-notice", "Warning", closeDialog);
	} else {*/
		/*if ( (('${isBeneficiaryOwner}' == 'true' && $('#customerType').val()=='${typeIndi}') || $('#customerType').val()=='${typeCorp}' ) 
				&& ('${mode}' == 'edit' && '${param}' == 'entry') ) {*/
			if ('${mode}' == 'edit' && '${param}' == 'entry') {
				var valid = watchlistData(watchListDataTable);
				if (valid) {
					var isActive = $('input[name="amlCustomer.isActive"]:checked').val();
					var action ='@{AmlMaintenance.save()}?mode=${mode}&apiStatus='+ apiStatus +'&amlStatus=' + amlStatus +'&amlId=' + amlId +'&eddId=' + eddId +'&isActive='+ isActive +'#{if group}&group=${group}#{/}#{if isPartial}&isPartial=${isPartial}#{/}';
					loading.dialog('open');
					$('#customerForm').attr('action', action);
					$('#customerForm').submit();
				}
			} else {
				var isActive = $('input[name="amlCustomer.isActive"]:checked').val();
				var action ='@{save()}?mode=${mode}&apiStatus='+ apiStatus +'&amlStatus='+ amlStatus +'&amlId=' + amlId +'&eddId=' + eddId +'&isActive='+ isActive +'#{if group}&group=${group}#{/}#{if isPartial}&isPartial=${isPartial}#{/}';
				loading.dialog('open');
				$('#customerForm').attr('action', action);
				$('#customerForm').submit();
			}
		/*} else {
			var isActive = $('input[name="amlCustomer.isActive"]:checked').val();
			var action ='@{save()}?mode=${mode}&apiStatus='+ apiStatus +'&amlStatus='+ amlStatus +'&isActive='+ isActive +'#{if group}&group=${group}#{/}#{if isPartial}&isPartial=${isPartial}#{/}';
			loading.dialog('open');
			$('#customerForm').attr('action', action);
			$('#customerForm').submit();
		}*/
	/*}*/
	
}

function checkNameAndProfile() {
	var interfaceKey = getInterfaceKey();
	var dataTable = $("#tblAmlWatchList").dataTable();
	var existingData = [];

	$("input:checked", dataTable.fnGetNodes()).each(function(index){
		var data = dataTable.fnGetData(index);
		if (data) {
			if (data.interfaceKey != interfaceKey) {
				existingData[existingData.length] = data;
			}
		}
	});

	var amlKey = $("#amlKey").val()
	if (!interfaceKey){
		$('#amlInterfaceError').html('Required');
	} else {

		loading.dialog('open');
		$('#amlInterfaceError').html('');
		var resultCode;
		var resultMessage="";
		var riskProfile = "";
		//add by Fadly #8235
		var prohibited = "";
		var isCustomer = $("#amlInterfaceRadio1").is(':checked');

		$().fetchSync("@{AmlMaintenance.interfaceToAml()}", 
				{
			'amlKey' : amlKey,
			'interfaceKey' : interfaceKey
				}, function(datas) {
					resultCode = datas.resultCode;
					resultMessage = datas.resultMessage;
					riskProfile = datas.riskProfile.toLowerCase();
					prohibited = datas.prohibited.toLowerCase();
					
					//add by Fadly #8235
					var checkBO = false;
					if('${isBeneficiaryOwner}' == 'true'){
						if(!isCustomer){
							checkBO = true; 
						}else{
							checkBO = false;
						}
					}else{
						checkBO = true;
					}
							
					if((prohibited == 'true') && (checkBO)){
						$("#blackList").val('${blackListYes}');
						$("#isActive1").attr('checked');
						if (riskProfile == 'high') {
							$("#riskProfile").val("${riskProfileHigh}");
							$("p[atr=isHightReasonMandatory] label span").html(" *");
						} else if (riskProfile == 'medium') {
							$("#riskProfile").val("${riskProfileMedium}");
						} else if (riskProfile == 'low') {
							$("#riskProfile").val("${riskProfileLow}");
						}
						
						$("#checkAll", this).removeAttr('checked');
						var interfaceKey = getInterfaceKey();
						var table = $("#tblAmlWatchList").dataTable();
						table.fnClearTable();
						
						for (var row in datas.screeningResult) {
							var b = datas.screeningResult[row];
							var rownum;
							// get risk profile from response api
							var riskProfileOld = $("#riskProfile").val();
							if (riskProfileOld) {
								riskProfileAddujstment(riskProfileOld, riskProfile);
							} else {
								if (riskProfile == 'high') {
									$("#riskProfile").val("${riskProfileHigh}");
									$("p[atr=isHightReasonMandatory] label span").html(" *");
								} else if (riskProfile == 'medium') {
									$("#riskProfile").val("${riskProfileMedium}");
								} else if (riskProfile == 'low') {
									$("#riskProfile").val("${riskProfileLow}");
								}
							}

							// get resultMessage from response api
							if (b.resultMessage) {
								resultMessage = b.resultMessage;
							}
							// get result code from response api
							if (b.resultCode) {
								resultCode = b.resultCode;

								if (resultCode == '0') {
									if (riskProfile == 'high') {
										rownum = table.fnAddData(b, false);
										var ptr = table.fnSettings().aoData[rownum].nTr;
										$(ptr).data("bean", b);
										table.fnDraw();
										if (getCustomerType() == '${typeIndi}') {
											if('${tipeIndividuBoKey}' == interfaceKey) {
												$('#apiStatus').val('${amlStatusWaitingBOWatchlist}');
											} else {
												$('#apiStatus').val('${amlStatusWaitingWatchlist}');			
											}
										} else {
											$('#apiStatus').val('${amlStatusWaitingWatchlist}');
										}
									} else {
										rownum = table.fnAddData(b, false);
										var ptr = table.fnSettings().aoData[rownum].nTr;
										$(ptr).data("bean", b);
										table.fnDraw();

										var amlId = getAmlId();
										if(!amlId) {
											amlId = b.amlId;
										}

										if (getCustomerType() == '${typeIndi}') {
											if('${tipeIndividuBoKey}' == interfaceKey) {
												$('#amlId').val(amlId);
												$('#apiStatus').val('${amlStatusDone}');
											} else {
												if ('${isBeneficiaryOwner}' == 'false') {
													$('#amlId').val(amlId);
													$('#apiStatus').val('${amlStatusDone}');
												} else {
													$('#apiStatus').val('${amlStatusWaitingBOStatus}');	
													enableTipeIndividuBo();									
												}
											}
										} else {
											$('#amlId').val(amlId);
											$('#apiStatus').val('${amlStatusDone}');
										}
									}
								}
							//	showWatchListDataTable(resultCode,resultMessage,riskProfile);

							}
							$("input[type='checkbox']", table).disabled();
						}
						
						showBlackList(blackList);
						loading.dialog('close');
					} else {
						if (!datas.screeningResult || (datas.screeningResult && datas.screeningResult.length < 1)) {

							loading.dialog('close');

							if (riskProfile == 'low' || riskProfile == 'medium') {
								if (resultCode == '0') {
									var amlId = datas.amlId;
									var riskProfileOld = $("#riskProfile").val();
									if (riskProfileOld) {
										riskProfileAddujstment(riskProfileOld, riskProfile);
									} else {
										if (riskProfile == 'medium') {
											$("#riskProfile").val("${riskProfileMedium}");
										} else if (riskProfile == 'low') {
											$("#riskProfile").val("${riskProfileLow}");
										}
									}
									if (getCustomerType() == '${typeIndi}') {
										var interfaceKey = getInterfaceKey();

										if('${tipeIndividuBoKey}' == interfaceKey) {
											$('#apiStatus').val('${amlStatusDone}');
										} else {
											if ('${isBeneficiaryOwner}' == 'false') {
												$('#apiStatus').val('${amlStatusDone}');
											} else {
												$('#apiStatus').val('${amlStatusWaitingBOStatus}');	
												enableTipeIndividuBo();									
											}
										}
										if ('${tipeIndividuKey}' == interfaceKey) {
											$('#amlId').val(amlId);
										}
									} else {
										$('#amlId').val(amlId);
										$('#apiStatus').val('${amlStatusDone}');
									}
								}
								showWatchListDataTable(resultCode,resultMessage,riskProfile);
							} else {
								var closeDialog = function(){
									$("#dialog-message").dialog("close");
								};
								messageAlertOk("Watchlist mandatory for riskProfile High!", "ui-icon ui-icon-notice", "Warning", closeDialog);
							}


						} else {
							$("#checkAll", this).removeAttr('checked');
							var interfaceKey = getInterfaceKey();
							var table = $("#tblAmlWatchList").dataTable();
							table.fnClearTable();
							for (var row in existingData) {
								var b = existingData[row];

								var rownum;
								rownum = table.fnAddData(b, false);
								$(ptr).data("bean", b);
								table.fnDraw();
								$("input[type='checkbox']", table).attr('checked', 'checked');
							}
							for (var row in datas.screeningResult) {
								var b = datas.screeningResult[row];
								var rownum;
								// get risk profile from response api
								var riskProfileOld = $("#riskProfile").val();
								if (riskProfileOld) {
									riskProfileAddujstment(riskProfileOld, riskProfile);
								} else {
									if (riskProfile == 'high') {
										$("#riskProfile").val("${riskProfileHigh}");
										$("p[atr=isHightReasonMandatory] label span").html(" *");
									} else if (riskProfile == 'medium') {
										$("#riskProfile").val("${riskProfileMedium}");
									} else if (riskProfile == 'low') {
										$("#riskProfile").val("${riskProfileLow}");
									}
								}

								// get resultMessage from response api
								if (b.resultMessage) {
									resultMessage = b.resultMessage;
								}
								// get result code from response api
								if (b.resultCode) {
									resultCode = b.resultCode;

									if (resultCode == '0') {
										if (riskProfile == 'high') {
											rownum = table.fnAddData(b, false);
											var ptr = table.fnSettings().aoData[rownum].nTr;
											$(ptr).data("bean", b);
											table.fnDraw();
											if (getCustomerType() == '${typeIndi}') {
												if('${tipeIndividuBoKey}' == interfaceKey) {
													$('#apiStatus').val('${amlStatusWaitingBOWatchlist}');
												} else {
													$('#apiStatus').val('${amlStatusWaitingWatchlist}');			
												}
											} else {
												$('#apiStatus').val('${amlStatusWaitingWatchlist}');
											}
										} else {
											rownum = table.fnAddData(b, false);
											var ptr = table.fnSettings().aoData[rownum].nTr;
											$(ptr).data("bean", b);
											table.fnDraw();

											var amlId = getAmlId();
											if(!amlId) {
												amlId = b.amlId;
											}

											if (getCustomerType() == '${typeIndi}') {
												if('${tipeIndividuBoKey}' == interfaceKey) {
													$('#amlId').val(amlId);
													$('#apiStatus').val('${amlStatusDone}');
												} else {
													if ('${isBeneficiaryOwner}' == 'false') {
														$('#amlId').val(amlId);
														$('#apiStatus').val('${amlStatusDone}');
													} else {
														$('#apiStatus').val('${amlStatusWaitingBOStatus}');	
														enableTipeIndividuBo();									
													}
												}
											} else {
												$('#amlId').val(amlId);
												$('#apiStatus').val('${amlStatusDone}');
											}
										}
									}
									showWatchListDataTable(resultCode,resultMessage,riskProfile);
								}
							}
							loading.dialog('close');
						}
					}
				});
	}

}

function riskProfileAddujstment(riskProfileOld, riskProfile) {

	if (riskProfileOld == '${riskProfileMedium}' && riskProfile != 'low') {
		if (riskProfile == 'high') {
			$("#riskProfile").val("${riskProfileHigh}");
			$("p[atr=isHightReasonMandatory] label span").html(" *");
		}
	} else if (riskProfileOld == '${riskProfileMedium}') {
		if (riskProfile == 'high') {
			$("#riskProfile").val("${riskProfileHigh}");
			$("p[atr=isHightReasonMandatory] label span").html(" *");
		} else if (riskProfile == 'medium') {
			$("#riskProfile").val("${riskProfileMedium}");
		}
	}
	
}

function getCustomerType() {
	var customerType = $('#customerType').val();
	return customerType;
}

function confirmScreening() {
	loading.dialog('open');
	
	var interfaceKey = getInterfaceKey();
	var amlKey = $("#amlKey").val()
	var dataIds = [];
	var watchlistData = [];

	var dataTable = $("#tblAmlWatchList").dataTable();
	var existingData = [];

	$("input:checked", dataTable.fnGetNodes()).each(function(index){
		var data = dataTable.fnGetData(index);
		if (data) {
			if (data.interfaceKey != interfaceKey) {
				existingData[existingData.length] = data;
			}
		}
	});
	
	var rows = dataTable.fnGetNodes().length;
	var datas = dataTable.fnGetData();
	$(dataTable.fnGetNodes()).each(function(i, e){
		var data = $(this).data("bean");
		if(data){
			var inputChk = $("input", $("td", $(this)).eq(0));
			var isChecked = inputChk.is(':checked');
			if (isChecked) {
				if (data.interfaceKey == interfaceKey) {
					var tr = inputChk.parent().parent();
					watchlistData[watchlistData.length] = data;
					dataIds[dataIds.length] = data.dataId;
				}
			}
		}
	});
	if (dataIds.length == 0) {
		if (datas) {

			for (var x in datas) {
				var b = datas[x];
				dataIds[dataIds.length] = b.dataId;
			}
		}
	}
	
	var closeDialog = function(){
		$("#dialog-message").dialog("close");
	};
	
	if (watchlistData.length == 0 && dataIds.length == 0) {
		var closeDialog = function(){
			$("#dialog-message").dialog("close");
		};
		
		loading.dialog('close');
		
		messageAlertOk("Please choose data, minimal 1 row", "ui-icon ui-icon-notice", "Warning", closeDialog);
		return false;
	} else{
		$().fetchSync("@{AmlMaintenance.interfaceToAmlConfirmScreening()}", 
				{
					'amlKey' : amlKey,
					'dataIds' : dataIds
				}, function(datas) {
					var resultCode = datas.resultCode;
					var hostResultCode = datas.hostResultCode;
					if (resultCode != "0") {
						messageAlertOk(datas.resultMessage, "ui-icon ui-icon-notice", "Warning", closeDialog);
					} else {
						var url = datas.host;
						if (!url) {
							messageAlertOk("Host not found", "ui-icon ui-icon-notice", "Warning", closeDialog);
						} else {
							
							if (watchlistData.length != 0) {
								dataTable.fnClearTable();
								for (var row in existingData) {
									var b = existingData[row];
									
									var rownum;
									rownum = dataTable.fnAddData(b, false);
									$(ptr).data("bean", b);
									dataTable.fnDraw();
									$("input[type='checkbox']", dataTable).attr('checked', 'checked');
								}
								for (var x in watchlistData) {
									var b = watchlistData[x];
									var rownum;
									
									rownum = dataTable.fnAddData(b,false);
	
									var ptr = dataTable.fnSettings().aoData[rownum].nTr;
									$(ptr).data("bean", b);
									dataTable.fnDraw();
	
									$("input[type='checkbox']", $('#tblAmlWatchList')).attr('checked', 'checked');
								}
							}
							
							if (hostResultCode == "404") {
								messageAlertOk("Please input EDD form in " + url, "ui-icon ui-icon-notice", "Warning", closeDialog);
							} else {
								var newWindow = window.open(url, 'main');
							}

							if('${tipeIndividuBoKey}' == interfaceKey) {
								$('#apiStatus').val('${amlStatusWatingBOApprovalEdd}');
							} else {
								$('#apiStatus').val('${amlStatusWatingApprovalEdd}');			
							}

						}
					}
					
					loading.dialog('close');
			});
	}
	
}

function checkEddStatus() {
	loading.dialog('open');
	var amlKey = $("#amlKey").val()
							
	var interfaceKey = getInterfaceKey();

	if('${tipeIndividuBoKey}' == interfaceKey) {
		$('#apiStatus').val('${amlStatusWatingBOApprovalEdd}');
	} else {
		$('#apiStatus').val('${amlStatusWatingApprovalEdd}');			
	}
	
	var closeDialog = function(){
		$("#dialog-message").dialog("close");
	};
	
	if (watchlistData.length == 0) {
		
		loading.dialog('close');
		
		messageAlertOk("Please choose data, minimal 1 row", "ui-icon ui-icon-notice", "Warning", closeDialog);
		return false;
	} else{
		var dataTable = $("#tblAmlWatchList").dataTable();
		var tempHighRisk;
		var amlId = getAmlId();;
		$(dataTable.fnGetNodes()).each(function(i, e){
			var data = $(this).data("bean");
			
			if (data) {
				var inputChk = $("input", $("td", $(this)).eq(0));
				var isChecked = inputChk.is(':checked');
				if (isChecked) {
					
					if (!tempHighRisk) {
						if (data.riskProfile) {
							tempHighRisk = data.riskProfile;
						}
					} else if (tempHighRisk == 'Low') {
						tempHighRisk = data.riskProfile;
					} else if (tempHighRisk == 'Medium') {
						if (data.riskProfile == 'Medium' || data.riskProfile == 'High') {
							tempHighRisk = data.riskProfile;
						}
					} else if (tempHighRisk == 'High') {
						if (data.riskProfile == 'High') {
							tempHighRisk = data.riskProfile;
						}
					}
				}
			}
		});
		
		var datas = dataTable.fnGetData();
		if (datas) {

			for (var x in datas) {
				var data = datas[x];
				if (!tempHighRisk) {
					if (data.riskProfile) {
						tempHighRisk = data.riskProfile;
					}
				} else if (tempHighRisk == 'Low') {
					tempHighRisk = data.riskProfile;
				} else if (tempHighRisk == 'Medium') {
					if (data.riskProfile == 'Medium' || data.riskProfile == 'High') {
						tempHighRisk = data.riskProfile;
					}
				} else if (tempHighRisk == 'High') {
					if (data.riskProfile == 'High') {
						tempHighRisk = data.riskProfile;
					}
				}
			}
		}
		
		$().fetchSync("@{AmlMaintenance.interfaceToAmlCheckEddStatus()}", 
				{
					'amlKey' : amlKey
				}, function(datas) {
					var resultCode = datas.resultCode;
					var hostResultCode = datas.hostResultCode;
					if (resultCode != "10") {
						var closeDialogOk = function(){
							$("#dialog-message").dialog("close");
							if (resultCode == "205") {
								location.reload();
							}
						};
						messageAlertOk("Interface to AML failed for High Risk. Please retry again " + datas.resultMessage, "ui-icon ui-icon-notice", "Warning", closeDialogOk);
					} else {
						var highReason = $('#highReason').val();
						var eddId = datas.eddId;
						$('#eddId').val(eddId);
						$('#eddIdHide').val(eddId);
						if (getCustomerType() == '${typeIndi}') {
							var interfaceKey = getInterfaceKey();
							var tipeIndividuBo = '${tipeIndividuBoKey}';
							if('${tipeIndividuBoKey}' == interfaceKey) {
								$('#amlId').val(amlId);
								$('#apiStatus').val('${amlStatusDone}');
							} else {
								if ('${isBeneficiaryOwner}' == 'false') {
									$('#amlId').val(amlId);
									$('#apiStatus').val('${amlStatusDone}');
								} else {
									$('#apiStatus').val('${amlStatusWaitingBOStatus}');	
									enableTipeIndividuBo();									
								}
							}
							var apiStatus = $('#apiStatus').val();
						} else {
							$('#amlId').val(amlId);
							$('#apiStatus').val('${amlStatusDone}');
						}
						messageAlertOk("Interface to AML success and Done for High Risk", "ui-icon ui-icon-notice", "Warning", closeDialog);
						
						if (!highReason) {
							messageAlertOk("High Reason is mandatory", "ui-icon ui-icon-notice", "Warning", closeDialog);
						}
					}
					
					loading.dialog('close');
			});
	}
	
}

function enableTipeIndividuBo() {
	if ('${isBeneficiaryOwner}' == 'true') {
		$('input[name=amlInterfaceRadio][value=aml.tipeIndividu]').attr('checked', false);
		$('input[name=amlInterfaceRadio][value=aml.tipeIndividu]').attr('disabled', true);
		$('input[name=amlInterfaceRadio][value=aml.tipeIndividu.bo]').attr('disabled', false);
		$('input[name=amlInterfaceRadio][value=aml.tipeIndividu.bo]').attr('checked', true);
	}
}

function getInterfaceKey() {
	var interfaceKey = 'aml.tipeIndividu';
	
	if ('${isBeneficiaryOwner}' == 'true') {
		var interfaceKeyChecked = $("input[name=amlInterfaceRadio]:checked").val()
		if (interfaceKeyChecked) {
			interfaceKey = interfaceKeyChecked;
		}
	}
	return interfaceKey;
}

function removeDataByInterfaceKey(interfaceKey,dataTable) {
	var datas = [];
	$(dataTable.fnGetNodes()).each(function(i, e){

		var inputChk = $("input", $("td", $(this)).eq(0));
		var isChecked = inputChk.is(':checked');
		if (isChecked) {
		var data = $(this).data("bean");
			if (data) {
				if (data.interfaceKey != interfaceKey) {
					datas[datas.length] = data;
				}
			}
		}
	});

	if (datas.length == 0) {
		var existingData = dataTable.fnGetData();
		
		if (existingData) {
			for (var row in existingData) {
				var data = existingData[row];
				if (data) {
					if (data.interfaceKey != interfaceKey) {
						datas[datas.length] = data;
					}
				}
			}
		}
	}
	
	return datas;
}

function removeDuplicates(arr) {
    return arr.filter((item, 
        index) => arr.indexOf(item) === index);
}

function watchlistData(dataTable){
	var interfaceKey = getInterfaceKey();
	var datas = [];

	var ctr = 0;
	$(dataTable.fnGetNodes()).each(function(i, e){
		var inputChk = $("input", $("td", $(this)).eq(0));
		var isChecked = inputChk.is(':checked');
		if (isChecked) {
			var tr = inputChk.parent().parent();
			var data = tr.data("bean");
			if (data) {
				if (data.interfaceKey == interfaceKey) {
					datas[datas.length] = tr.data("bean");
				}
			}
		}
	});

	if (datas.length == 0) {
		/*var tableData = dataTable.fnGetData();

		for (var row in tableData) {
			var data = tableData[row];
			if (data) {
				if (data.interfaceKey == interfaceKey && data.watchlistKey) {
					datas[datas.length] = data;
				} else if (data.interfaceKey == interfaceKey && ('${mode}' =='edit' && '${param}' == 'entry')) {
					datas[datas.length] = data;

				}
			}
		}*/
	}

	var existingDatas = removeDataByInterfaceKey(interfaceKey,dataTable);
	var riskProfile = "";
	dataTable.fnClearTable();

	for (var x in datas) {
		var b = datas[x];
		// get risk profile from response api
		if (b.riskProfile && b.riskProfile.toLowerCase() == 'high') {
			riskProfile = b.riskProfile;
		}    		
	}

	for (var x in existingDatas) {
		var b = existingDatas[x];
		var rownum;

		rownum = dataTable.fnAddData(b,false);

		var ptr = dataTable.fnSettings().aoData[rownum].nTr;
		$(ptr).data("bean", b);
	}

	for (var x in datas) {
		var b = datas[x];
		var rownum = dataTable.fnAddData(b, false);
		$("input[type='checkbox']", dataTable).attr('checked', 'checked');	
		var ptr = dataTable.fnSettings().aoData[rownum].nTr;

		$(ptr).data("bean", b);
	}
	dataTable.fnDraw();
	return true;
}

//add by Fadly #8235
function showBlackList(blackList){
	var closeDialog = function() {
		$("#dialog-message").dialog('close');
	}
	if ('${isBeneficiaryOwner}' == 'true') {
		messageAlertOk("This is DTTOT/Blacklist Benefical Owner", "ui-icon ui-icon-notice", "Warning", closeDialog);
	}else{
		messageAlertOk("This is DTTOT/Blacklist Customer", "ui-icon ui-icon-notice", "Warning", closeDialog);	
	}
	
	return false;
}
//end #8235

function showWatchListDataTable(resultCode,resultMessage,riskProfile) {
// $('#interfaceAml').dialog('open');
	
// $('.ui-widget-overlay').css('height',$('body').height());
	var closeDialog = function() {
		$("#dialog-message").dialog('close');
	}
	
	riskProfile = riskProfile.toLowerCase();
	
	if (resultCode && resultCode == '0') {
		if (riskProfile == 'high') {
			messageAlertOk("Interface to AML success with High Risk. Please choose watch list", "ui-icon ui-icon-notice", "Warning", closeDialog);
		} else {
			messageAlertOk("Interface to AML success and DONE", "ui-icon ui-icon-notice", "Warning", closeDialog);
		}
	} else {
		if (riskProfile == 'high') {
			messageAlertOk("Interface to AML failed with High Risk. Please retry again (" + resultMessage + ")", "ui-icon ui-icon-notice", "Warning", closeDialog);
		} else {
			messageAlertOk("Interface to AML failed (" + resultMessage + ")", "ui-icon ui-icon-notice", "Warning", closeDialog);
		}
	}
	
	return false;
}

function tblAmlWatchList(data) {
	var fmtDate = '${appProp.jqueryDateFormat}';
		
	var tblAmlWatchList = $('#tblAmlWatchList').dataTable({
		aaData: data,
		aoColumns: [  	
						{
							fnRender: function(obj){
			
								var controls="";
			
								if (obj.aData.watchlistKey) {
									controls += "<input id='watchListCheckbox' type='checkbox' checked disabled />";
								} else {
									controls += "<input id='watchListCheckbox' type='checkbox' />";
								}
								
								return controls;										
							}
						},
						{
							fnRender: function(obj){
								
								var controls;
								if (obj.aData.matchSource != null) {
									controls = obj.aData.matchSource;
									controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].matchSource" value="' + obj.aData.matchSource + '" />';
								} else {
									controls = "";
									controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].matchSource" value="" />';
								}
								return controls;										
							}
						},
						{
							fnRender: function(obj){
								var controls;
								if (obj.aData.name != null) {
									controls = obj.aData.name;
									controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].name" value="' + obj.aData.name + '" />';
								} else {
									controls = "";
									controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].name" value="" />';
								}
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								if (obj.aData.dataId != null) {
									controls = obj.aData.dataId;
									controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].dataId" value="' + obj.aData.dataId + '" />';
							 	} else {
							 		controls = '';
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].dataId" value="" />';
							 	}
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								if (obj.aData.detailData != null) {
									controls = obj.aData.detailData;
									controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].detailData" value="' + obj.aData.detailData + '" />';
							 	} else {
							 		controls = '';
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].detailData" value="" />';
							 	}
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								if (obj.aData.source != null) {
									controls = obj.aData.source;
									controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].source" value="' + obj.aData.source + '" />';
							 	} else {
							 		controls = '';
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].source" value="" />';
							 	}
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
						 		controls = '';
								if (obj.aData.dataType != null) {
									controls = obj.aData.dataType;
									controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].dataType" value="' + obj.aData.dataType + '" />';
							 	} else {
							 		controls = '';
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].dataType" value="" />';
							 	}
								if (obj.aData.watchListKey != null) {
									controls = "";
									controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].watchlistKey" value="' + obj.aData.watchlistKey + '" />';
								} else {
									controls = "";
									controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].watchlistKey" value="" />';
								}
								
								if ((obj.aData.eddId == '')||(obj.aData.eddId == null)){
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].eddId" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].eddId" value="' + obj.aData.eddId + '" />';
							 	}
								
								if ((obj.aData.blackList == '')||(obj.aData.blackList == null)){
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].blackList" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].blackList" value="' + obj.aData.blackList + '" />';
							 	}
								
								if ((obj.aData.riskProfile == '')||(obj.aData.riskProfile == null)){
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].riskProfile" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].riskProfile" value="' + obj.aData.riskProfile + '" />';
							 	}

								if ((obj.aData.highReason == '')||(obj.aData.highReason == null)){
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].highReason" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].highReason" value="' + obj.aData.highReason + '" />';
							 	}


								if ((obj.aData.messageApi == '')||(obj.aData.messageApi == null)){
							 		controls += '<textarea hidden="hidden" name="watchList[' + obj.iDataRow + '].messageApi" value="" />';
							 	} else {
									var messageApiEncode = btoa(obj.aData.messageApi);
									obj.aData.messageApi = messageApiEncode;
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].messageApi" value=" ' + messageApiEncode + '" > </input>';
							 	}

								if ((obj.aData.interfaceKey == '')||(obj.aData.interfaceKey == null)){
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].interfaceKey" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].interfaceKey" value="' + obj.aData.interfaceKey + '" />';
							 	}
								
								if ((obj.aData.amlId == '')||(obj.aData.amlId == null)){
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].amlId" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="watchList[' + obj.iDataRow + '].amlId" value="' + obj.aData.amlId + '" />';
							 	}
								return controls;
							}
						}
			       	  ],
		bAutoWidth: false,
		bFilter: false,
		bInfo: false,
		bSearch: false,
		bJQueryUI: true,
		bPaginate: false,
        bLengthChange: false,
		sPaginationType: "full_numbers",
        bSort: false,
		iDisplayLength:10
	});

	var apiStatus = $('#apiStatus').val();

	if ('${confirming}'=='true' || '${mode}'=='view' || (apiStatus == '${amlStatusDone}') || (('${mode}'=='edit') && ('${param}'=='entry') && ('${isReadOnly}'  == 'true'))) {
		$("input[type='checkbox']", $('#tblAmlWatchList')).attr('checked', 'checked').attr('disabled', 'disabled');
	}
	
	if (((apiStatus) && ('${isError}') == 'true')) {
		$("input[type='checkbox']", $('#tblAmlWatchList')).attr('checked', 'checked');
	}
	
	return tblAmlWatchList;
}

function getAge(birthDate){
	 var currentDate = $('#currentDate').datepicker('getDate');
	 var newCurDate = new Date(currentDate);
	 var birthDate = new Date(birthDate);
	 var age = newCurDate.getFullYear() - birthDate.getFullYear();
	 var m = newCurDate.getMonth() - birthDate.getMonth();
	 
	 if (m < 0 || (m == 0 && newCurDate.getDate() < birthDate.getDate())){
		 age--;
	 }
	 return age;
 }

// function customer type
// ===================================================================================================
function adjustCustomerType() {
	var select_customer; 
	var param = '${param}';
    select_customer = $('#customerType').val(); 	
  		// alert(select_customer);
		if  (select_customer == CUSTOMER_TYPE_C) {
        	$("label:contains('Birth Date *')").html("Founded Date <span class='req'>*</span>");
    		$("label:contains('Document')").html("Document <span class='req'>*</span>");
        	$(".default").css("display", "none");
        	$("div.individual_only").css("display", "none");
        	$("p.individual_only").css("display", "none");
        	$("div.corporate_only").css("display", "block");
        	$("div.udf_type_all").css("display", "none");
        	$("li.corporate_only").css("display", "");
        	$("p.corporate_only").css("display", "");
//        	$('#grid-contact-corp').dataTable().fnClearTable();
        } else if  (select_customer == CUSTOMER_TYPE_I) {
    		$("label:contains('Identification 1')").html("Identification 1 <span class='req'>*</span>");
        	
        	$(".default").css("display", "none");
        	$("div.individual_only").css("display", "block");
        	$("div.corporate_only").css("display", "none");
        	$("div.udf_type_all").css("display", "none");
        	$("li.individual_only").css("display", "");
        	$("p.individual_only").css("display", "");
        	$("p.corporate_only").css("display", "none");
//        	$('#grid-contact-corp').dataTable().fnClearTable();
        	var param = ('${param}').trim();
        	if (!param) {
        		$('#amlInterfaceRadio1').disabled();
        		$('#amlInterface').disabled();
        	}
        } else {
        	$(".individual").val("");
        	$("div.individual_only").css("display", "none");
        	$("div.corporate_only").css("display", "none");
        	$("li.individual_only").css("display", "none");
        	$("li.corporate_only").css("display", "none");
        	$("p.individual_only").css("display", "none");
        	$("p.corporate_only").css("display", "none");
        	$("div.udf_type_all").css("display", "block");
        	$(".default").css("display", "");
        }
		if($("#currencyCode").val()==""){
			$("#currencyCode").val("IDR").change().blur();
		}
		if($("#h_corrAddr").val()==""){
			$("#corrAddress").val(ADDRESS_TYPE_2).change().blur();
		}
};	
// ==========================================================================================================================

function compareDateMustBeforeApplicationDate(thisId, value) {
	var dateValue = new Date(value);
	var currentDate = $('#currentDate').datepicker('getDate');
	if (dateValue.getTime() > currentDate.getTime()) {
		$(thisId).addClass('fieldError');
		$(thisId).addClass('fieldAppDateError');
		$(thisId+"Error").html("can not greather than Application Date");
		return false;
	} else {
		$(thisId).removeClass('fieldError');
		$(thisId).removeClass('fieldAppDateError');
		$(thisId+"Error").html("");
		return true;
	}
}
// ==========================================================================================================================
function validateForm(){
	var select_customer;
	
	var lengthTableContact = $('#grid-contact-corp').dataTable().fnGetNodes().length;
	select_customer = $('#customerType').val();
	var isValidate = false;
	
	  if (select_customer!=''){
		  
		  if ((($('#isBO').val() == 'true' || '${isBeneficiaryOwner}' == 'true') && select_customer == '${typeIndi}') || (select_customer == '${typeCorp}')) {
			 if (lengthTableContact == 0){
				$('#errGlobalContactCorp').html('Must have One Data!');
				isValidate = false;
			} else {
				$('#errGlobalContactCorp').html('');
				isValidate = true;
			}
		  } else {
			  $('#errGlobalContactCorp').html('');
			  isValidate = true; 
		  }
	  } else {
		  isValidate = false;
		 $('#customerMessageErrorGlobal').html("There are some error fields!");
	  }
	  
	  return isValidate;
}

function buildSourceOfFund() {
    var select_customer = $('#customerType').val();

	CUSTOMER_TYPE_I = '${typeIndi}';
	CUSTOMER_TYPE_C = '${typeCorp}';
	
	$('#sourceOfFundContact').empty();
	var html = '<option></option>';
	if  (select_customer == CUSTOMER_TYPE_C) {
		
		$().fetchSync("@{AmlMaintenance.sourceOfFundList()}", 
				{
					'param' : 'CORP',
				}, function(datas) {
					for (var row in datas) {
						const data = datas[row];
						if (data.selected == 'true') {
							html += '<option value="'+data.value+'" selected>' + data.text +'</option>';
						} else {
							html += '<option value="'+data.value+'">' + data.text +'</option>';
						}
					}
			});

	} else if  (select_customer == CUSTOMER_TYPE_I) {

		
		$().fetchSync("@{AmlMaintenance.sourceOfFundList()}", 
				{
					'param' : 'IND',
				}, function(datas) {
					for (var row in datas) {
						const data = datas[row];
						if (data.selected == 'true') {
							html += '<option value="'+data.value+'" selected>' + data.text +'</option>';
						} else {
							html += '<option value="'+data.value+'">' + data.text +'</option>';
						}
					}
			});
		

	} 
	$('#sourceOfFundContact').html(html);
}

function disableAmlWatchlist() {
	var param = '${param}';
	if (!param) {
		$('#amlInterface').disabled();
	}
}