$(function() {
		/*$('.buttons button').button();
		$('#save').button();
		$('#cancel').button();
		$('#confirm').button();
		$('#back').button();*/
//		$( "#dialog-message1" ).css("display","none");
		
		var closeDialog = function() {
			$("#dialog-message").dialog('close');
			return false;
		};
		
		$('#tabsInterestTaxSetup').tabs();
		//$('#newInterest').button();
				
		$('.buttons button').button();
		$('.buttons input:button').button();
		
		var data = new Object();
		tabInterestDetail(data);
		//var tableTaxTier;
		
		if (('${mode}'=='entry') || (('${mode}'=='edit') && (('${txProfileRule?.recordStatus?.decodeStatus()}' == 'Reject')|| ($("#status").val() == 'R' )))) {
			$('input[name=isActive]').attr("disabled", "disabled");
		}
	
		$('input[name=isActive]').change(function(){
			$("input[name='isActive']").val($("input[name='isActive']:checked").val());
		});
		
		if (('${confirming}' != 'true')&&('${mode}' == 'entry')){
			$('#tabsInterestTaxSetup').tabs('select', 1);
			$('#tabsInterestTaxSetup').tabs('disable', 0);
			$('#tabsInterestTaxSetup').tabs('disable', 2);
			var	txProfileRule = new Object();
			txProfileRule.txProfRuleInts = new Array();
			var txProfRuleInts = (txProfileRule.txProfRuleInts);
			$("input[name=isActive]")[1].checked = true;
			$("#isActiveHidden").val(false);
		}
		
		if (('${mode}' == 'edit')||('${confirming}' == 'true')||('${mode}'=='view')) {
			$('#tabsInterestTaxSetup').tabs('select', 1);
			$('#tabsInterestTaxSetup').tabs('disable', 0);
			$('#tabsInterestTaxSetup').tabs('disable', 2);
			var txProfileRule = ${interests.raw()};
			
			if($('#isActiveHidden').val()=='true'){
				$('input[name="isActive"]')[0].checked = true;
				if (('${confirming}' == 'true')&&('${mode} != edit')){
					$('#activeYes').attr("disabled","disabled");
					$('#activeNo').attr("disabled","disabled");
				}else{
					$('#activeYes').attr("disabled",false);
					$('#activeNo').attr("disabled",false);
				}
			} else {
				$('input[name="isActive"]')[1].checked = true;
				if (('${confirming}' == 'true')&&('${mode} != edit')){
					$('#activeYes').attr("disabled","disabled");
					$('#activeNo').attr("disabled","disabled");
				} else if (('${mode}'=='edit')&&(('${txProfileRule?.recordStatus?.decodeStatus()}'=='Reject') || ($("#status").val() == 'R'))){
					$('input[name=isActive]').attr("disabled","disabled");
				} else {
					$('#activeYes').attr("disabled",false);
					$('#activeNo').attr("disabled",false);
				}
			}
			
			if ('${mode}'=='view'){
				$('#newInterest').button("option", "disabled", true);
				$('#activeYes').attr("disabled","disabled");
				$('#activeNo').attr("disabled","disabled");
			}
			
		}
		
		$("input[name='isActive']").click(function(){
			if ($("input[name=isActive]")[0].checked == true){
				$("#isActiveHidden").val(true);
			} else {
				$("#isActiveHidden").val(false);
			}
		});
			
		//**************************** START INTEREST DETAIL ************************//
		
		function tabInterestDetail(data) {
			var fmtDate = '${appProp.jqueryDateFormat}';
			var profRuleGrid;
			
			if ('${txProfRuleGrid}' != null) {
				profRuleGrid = ${txProfRuleGrid.raw()};
			}
			if ('${txProfRuleGrid}' == null) {
				profRuleGrid = new Array();
			}
			
			// ==== TABLE INTEREST DETAIL ==== //
			tableInterest = $('#listInterest #gridInterest').dataTable({
				aaData: profRuleGrid,
				aoColumns:[
						{
							fnRender: function(obj){
								var controls;
								//var effectiveDateFrom = $.datepicker.formatDate(fmtDate, new Date(obj.aData.effectiveDateFrom));
								var effectiveDateFrom;
			             		var stringDate = obj.aData.effectiveDateFrom.toString();
			             		if (stringDate.substr(2,1) != "/") {
			             			effectiveDateFrom = $.datepicker.formatDate(fmtDate, new Date(obj.aData.effectiveDateFrom));
			             		} else {
			             			effectiveDateFrom = obj.aData.effectiveDateFrom;
			             		}
			             		controls = effectiveDateFrom;
			            		return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								//var effectiveDateTo = $.datepicker.formatDate(fmtDate, new Date(obj.aData.effectiveDateTo));
								var effectiveDateTo;
								var stringDate = obj.aData.effectiveDateTo.toString();
			             		if (stringDate.substr(2,1) != "/") {
			             			effectiveDateTo = $.datepicker.formatDate(fmtDate, new Date(obj.aData.effectiveDateTo));
			             		} else {
			             			effectiveDateTo = obj.aData.effectiveDateTo;
			             		}
								controls = effectiveDateTo;
			            		return controls;
							}
						},
						{
							fnRender: function(obj) {
		            	 		var controls;
		            	 		var capitalGainTaxRate = '';
		            	 		if ((obj.aData.taxObjectCapitalGain) && (obj.aData.taxObjectCapitalGain.taxMaster) && (obj.aData.taxObjectCapitalGain.taxMaster.taxRate)!=null) {
			            	 		capitalGainTaxRate = obj.aData.taxObjectCapitalGain.taxMaster.taxRate;
								}
		            	 		//capitalGainTaxRate = (jQuery.trim(capitalGainTaxRate) == '') ? '' : capitalGainTaxRate;
		            	 		controls = $('#dummy').autoNumericSet(capitalGainTaxRate, {aPad:true,mDec:4}).val();
		            	 		return controls;
		            	 	}
						},
						{
							fnRender: function(obj) {
		            	 		var controls;
		            	 		var accruedInterestTaxRate = '';
		            	 		if ((obj.aData.taxObjectAccruedInterest) && (obj.aData.taxObjectAccruedInterest.taxMaster) && (obj.aData.taxObjectAccruedInterest.taxMaster.taxRate)!=null) {
		            	 			accruedInterestTaxRate = obj.aData.taxObjectAccruedInterest.taxMaster.taxRate;
								}
		            	 		//accruedInterestTaxRate = (jQuery.trim(accruedInterestTaxRate) == '' ? 0 : accruedInterestTaxRate);
		            	 		controls = $('#dummy').autoNumericSet(accruedInterestTaxRate, {aPad:true,mDec:4}).val();
		            	 		return controls;
		            	 	}
						},
						{
							 fnRender: function(obj) {
							 	var controls;
							 	controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
							 	
							 	var effectiveDateFrom;
								var effectiveDateFromMilis;
			             		var stringDate = obj.aData.effectiveDateFrom.toString();
			             		if (stringDate.substr(2,1) != "/") {
			             			effectiveDateFrom = $.datepicker.formatDate(fmtDate, new Date(obj.aData.effectiveDateFrom));
			             		} else {
			             			effectiveDateFrom = obj.aData.effectiveDateFrom;
			             		}
			             		effectiveDateFromMilis = ($.datepicker.parseDate(fmtDate, effectiveDateFrom)).getTime();
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].effectiveDateFrom" value="' + effectiveDateFrom + '" />';
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].effectiveDateFromLong" value="' + effectiveDateFromMilis + '" />';
							 	
								var effectiveDateTo;
								var effectiveDateToMilis;
								var stringDate = obj.aData.effectiveDateTo.toString();
			             		if (stringDate.substr(2,1) != "/") {
			             			effectiveDateTo = $.datepicker.formatDate(fmtDate, new Date(obj.aData.effectiveDateTo));
			             		} else {
			             			effectiveDateTo = obj.aData.effectiveDateTo;
			             		}
			             		effectiveDateToMilis = ($.datepicker.parseDate(fmtDate, effectiveDateTo)).getTime();
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].effectiveDateTo" value="' + effectiveDateTo + '" />';
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].effectiveDateToLong" value="' + effectiveDateToMilis + '" />';
								
								var taxObjectCapitalGainTaxProfileCode = '';
		            	 		if ((obj.aData.taxObjectCapitalGain) && (obj.aData.taxObjectCapitalGain.id) && (obj.aData.taxObjectCapitalGain.id.taxProfileCode)) {
		            	 			taxObjectCapitalGainTaxProfileCode = obj.aData.taxObjectCapitalGain.id.taxProfileCode;
		            	 		}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectCapitalGain.id.taxProfileCode" value="' + taxObjectCapitalGainTaxProfileCode + '" />';
								
								var taxObjectCapitalGainSecurityTypeCode = '';
								if ((obj.aData.taxObjectCapitalGain) && (obj.aData.taxObjectCapitalGain.id) && (obj.aData.taxObjectCapitalGain.id.securityTypeCode)) {
									taxObjectCapitalGainSecurityTypeCode = obj.aData.taxObjectCapitalGain.id.securityTypeCode;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectCapitalGain.id.securityTypeCode" value="' + taxObjectCapitalGainSecurityTypeCode + '" />';
								
								var taxObjectCapitalGainTaxKey = '';
								if ((obj.aData.taxObjectCapitalGain) && (obj.aData.taxObjectCapitalGain.taxMaster) && (obj.aData.taxObjectCapitalGain.taxMaster.taxKey)) {
									taxObjectCapitalGainTaxKey = obj.aData.taxObjectCapitalGain.taxMaster.taxKey;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectCapitalGain.taxMaster.taxKey" value="' + taxObjectCapitalGainTaxKey + '" />';
								
								var taxObjectCapitalGainTaxCode = '';
								if ((obj.aData.taxObjectCapitalGain) && (obj.aData.taxObjectCapitalGain.taxMaster) && (obj.aData.taxObjectCapitalGain.taxMaster.taxCode)) {
									taxObjectCapitalGainTaxCode = obj.aData.taxObjectCapitalGain.taxMaster.taxCode;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectCapitalGain.taxMaster.taxCode" value="' + taxObjectCapitalGainTaxCode + '" />';
								
								var taxObjectCapitalGainDescription = '';
								if ((obj.aData.taxObjectCapitalGain) && (obj.aData.taxObjectCapitalGain.taxMaster) && (obj.aData.taxObjectCapitalGain.taxMaster.description)) {
									taxObjectCapitalGainDescription = obj.aData.taxObjectCapitalGain.taxMaster.description;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectCapitalGain.taxMaster.description" value="' + taxObjectCapitalGainDescription + '" />';
								
								var taxObjectCapitalGainTaxRate = '';
								if ((obj.aData.taxObjectCapitalGain) && (obj.aData.taxObjectCapitalGain.taxMaster) && (obj.aData.taxObjectCapitalGain.taxMaster.taxRate)!=null) {
									taxObjectCapitalGainTaxRate = obj.aData.taxObjectCapitalGain.taxMaster.taxRate;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectCapitalGain.taxMaster.taxRate" value="' + taxObjectCapitalGainTaxRate + '" />';
								
								var taxObjectCapitalGainTaxCompensation = '';
								if ((obj.aData.taxObjectCapitalGain) && (obj.aData.taxObjectCapitalGain.taxCompensation)) {
									taxObjectCapitalGainTaxCompensation = obj.aData.taxObjectCapitalGain.taxCompensation;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectCapitalGain.taxCompensation" value="' + taxObjectCapitalGainTaxCompensation + '" />';
								
								var taxObjectAccruedInterestTaxProfileCode = '';
		            	 		if ((obj.aData.taxObjectAccruedInterest) && (obj.aData.taxObjectAccruedInterest.id) && (obj.aData.taxObjectAccruedInterest.id.taxProfileCode)) {
		            	 			taxObjectAccruedInterestTaxProfileCode = obj.aData.taxObjectAccruedInterest.id.taxProfileCode;
		            	 		}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectAccruedInterest.id.taxProfileCode" value="' + taxObjectAccruedInterestTaxProfileCode + '" />';
								
								var taxObjectAccruedInterestSecurityTypeCode = '';
								if ((obj.aData.taxObjectAccruedInterest) && (obj.aData.taxObjectAccruedInterest.id) && (obj.aData.taxObjectAccruedInterest.id.securityTypeCode)) {
									taxObjectAccruedInterestSecurityTypeCode = obj.aData.taxObjectAccruedInterest.id.securityTypeCode;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectAccruedInterest.id.securityTypeCode" value="' + taxObjectAccruedInterestSecurityTypeCode + '" />';
								
								var taxObjectAccruedInterestTaxKey = '';
								if ((obj.aData.taxObjectAccruedInterest) && (obj.aData.taxObjectAccruedInterest.taxMaster) && (obj.aData.taxObjectAccruedInterest.taxMaster.taxKey)) {
									taxObjectAccruedInterestTaxKey = obj.aData.taxObjectAccruedInterest.taxMaster.taxKey;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectAccruedInterest.taxMaster.taxKey" value="' + taxObjectAccruedInterestTaxKey + '" />';
								
								var taxObjectAccruedInterestTaxCode = '';
								if ((obj.aData.taxObjectAccruedInterest) && (obj.aData.taxObjectAccruedInterest.taxMaster) && (obj.aData.taxObjectAccruedInterest.taxMaster.taxCode)) {
									taxObjectAccruedInterestTaxCode = obj.aData.taxObjectAccruedInterest.taxMaster.taxCode;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectAccruedInterest.taxMaster.taxCode" value="' + taxObjectAccruedInterestTaxCode + '" />';
								
								var taxObjectAccruedInterestDescription = '';
								if ((obj.aData.taxObjectAccruedInterest) && (obj.aData.taxObjectAccruedInterest.taxMaster) && (obj.aData.taxObjectAccruedInterest.taxMaster.description)) {
									taxObjectAccruedInterestDescription = obj.aData.taxObjectAccruedInterest.taxMaster.description;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectAccruedInterest.taxMaster.description" value="' + taxObjectAccruedInterestDescription + '" />';
								
								var taxObjectAccruedInterestTaxRate = '';
								if ((obj.aData.taxObjectAccruedInterest) && (obj.aData.taxObjectAccruedInterest.taxMaster) && (obj.aData.taxObjectAccruedInterest.taxMaster.taxRate)!=null) {
									taxObjectAccruedInterestTaxRate = obj.aData.taxObjectAccruedInterest.taxMaster.taxRate;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectAccruedInterest.taxMaster.taxRate" value="' + taxObjectAccruedInterestTaxRate + '" />';
								
							 	var taxObjectDiscountTaxProfileCode = '';
							 	if ((obj.aData.taxObjectDiscount) && (obj.aData.taxObjectDiscount.id) && (obj.aData.taxObjectDiscount.id.taxProfileCode)) {
							 		taxObjectDiscountTaxProfileCode = obj.aData.taxObjectDiscount.id.taxProfileCode;
							 	}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDiscount.id.taxProfileCode" value="' + taxObjectDiscountTaxProfileCode + '" />';
								
								var taxObjectDiscountSecurityTypeCode = '';
								if ((obj.aData.taxObjectDiscount) && (obj.aData.taxObjectDiscount.id) && (obj.aData.taxObjectDiscount.id.securityTypeCode)) {
									taxObjectDiscountSecurityTypeCode = obj.aData.taxObjectDiscount.id.securityTypeCode;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDiscount.id.securityTypeCode" value="' + taxObjectDiscountSecurityTypeCode + '" />';
								
								var taxObjectDiscountTaxKey = '';
								if ((obj.aData.taxObjectDiscount) && (obj.aData.taxObjectDiscount.taxMaster) && (obj.aData.taxObjectDiscount.taxMaster.taxKey)) {
									taxObjectDiscountTaxKey = obj.aData.taxObjectDiscount.taxMaster.taxKey;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDiscount.taxMaster.taxKey" value="' + taxObjectDiscountTaxKey + '" />';
								
								var taxObjectDiscountTaxCode = '';
								if ((obj.aData.taxObjectDiscount) && (obj.aData.taxObjectDiscount.taxMaster) && (obj.aData.taxObjectDiscount.taxMaster.taxCode)) {
									taxObjectDiscountTaxCode = obj.aData.taxObjectDiscount.taxMaster.taxCode;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDiscount.taxMaster.taxCode" value="' + taxObjectDiscountTaxCode + '" />';
								
								var taxObjectDiscountDescription = '';
								if ((obj.aData.taxObjectDiscount) && (obj.aData.taxObjectDiscount.taxMaster) && (obj.aData.taxObjectDiscount.taxMaster.description)) {
									taxObjectDiscountDescription = obj.aData.taxObjectDiscount.taxMaster.description;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDiscount.taxMaster.description" value="' + taxObjectDiscountDescription + '" />';
								
								var taxObjectDiscountTaxRate = '';
								if ((obj.aData.taxObjectDiscount) && (obj.aData.taxObjectDiscount.taxMaster) && (obj.aData.taxObjectDiscount.taxMaster.taxRate)!=null) {
									taxObjectDiscountTaxRate = obj.aData.taxObjectDiscount.taxMaster.taxRate;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDiscount.taxMaster.taxRate" value="' + taxObjectDiscountTaxRate + '" />';
								
								var taxObjectDividendTaxProfileCode = '';
								if ((obj.aData.taxObjectDividend) && (obj.aData.taxObjectDividend.id) && (obj.aData.taxObjectDividend.id.taxProfileCode)) {
									taxObjectDividendTaxProfileCode = obj.aData.taxObjectDividend.id.taxProfileCode;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDividend.id.taxProfileCode" value="' + taxObjectDividendTaxProfileCode + '" />';
								
								var taxObjectDividendSecurityTypeCode = '';
								if ((obj.aData.taxObjectDividend) && (obj.aData.taxObjectDividend.id) && (obj.aData.taxObjectDividend.id.securityTypeCode)) {
									taxObjectDividendSecurityTypeCode = obj.aData.taxObjectDividend.id.securityTypeCode;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDividend.id.securityTypeCode" value="' + taxObjectDividendSecurityTypeCode + '" />';
								
								var taxObjectDividendTaxKey = '';
								if ((obj.aData.taxObjectDividend) && (obj.aData.taxObjectDividend.taxMaster) && (obj.aData.taxObjectDividend.taxMaster.taxKey)) {
									taxObjectDividendTaxKey = obj.aData.taxObjectDividend.taxMaster.taxKey;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDividend.taxMaster.taxKey" value="' + taxObjectDividendTaxKey + '" />';
								
								var taxObjectDividendTaxCode = '';
								if ((obj.aData.taxObjectDividend) && (obj.aData.taxObjectDividend.taxMaster) && (obj.aData.taxObjectDividend.taxMaster.taxCode)) {
									taxObjectDividendTaxCode = obj.aData.taxObjectDividend.taxMaster.taxCode;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDividend.taxMaster.taxCode" value="' + taxObjectDividendTaxCode + '" />';
								
								var taxObjectDividendDescription = '';
								if ((obj.aData.taxObjectDividend) && (obj.aData.taxObjectDividend.taxMaster) && (obj.aData.taxObjectDividend.taxMaster.description)) {
									taxObjectDividendDescription = obj.aData.taxObjectDividend.taxMaster.description;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDividend.taxMaster.description" value="' + taxObjectDividendDescription + '" />';
								
								var taxObjectDividendTaxRate = '';
								if ((obj.aData.taxObjectDividend) && (obj.aData.taxObjectDividend.taxMaster) && (obj.aData.taxObjectDividend.taxMaster.taxRate)!=null) {
									taxObjectDividendTaxRate = obj.aData.taxObjectDividend.taxMaster.taxRate;
								}
								controls += '<input type="hidden" name="profRuleGrid[' + obj.iDataRow + '].taxObjectDividend.taxMaster.taxRate" value="' + taxObjectDividendTaxRate + '" />';
							 	return controls;
						 	}
						}
						],
					aaSorting:[[0,'asc']],
					bAutoWidth: false,		
					bFilter: false,
					bInfo: false,
					bJQueryUI: true,
					bPaginate: false,
					bSearch: false,
					bLengthChange: false,
					iDisplayLength:7,
		        	sPaginationType: 'full_numbers'
				});
			
			// ======= END TABLE INTEREST DETAIL ============ //
			
			// ======= START EVENT EDIT INTEREST DETAIL ======== //
			
			$("#listInterest #gridInterest").removeAttr('style');
			$("#listInterest #gridInterest tbody tr td").die('click');
			$("#listInterest #gridInterest tbody tr td").live('click', function(){
				$("#errInterest").removeClass('fieldError');
				$("#effectiveDateFrom").removeClass('fieldError');
				$("#effectiveDateTo").removeClass('fieldError');

				$("#taxCodeAccruedInterest").removeClass('fieldError');
				$("#taxCodeCapitalGain").removeClass('fieldError');
				$("#taxCodeDiscount").removeClass('fieldError');
				$("#taxCodeDividend").removeClass('fieldError');			
				$("#errInterest").html('');
				$("#effectiveDateFromError").html('');
				$("#effectiveDateToError").html('');		
				$("#errmsgTaxCodeAccruedInterest").html('');
				$("#errmsgTaxCodeCapitalGain").html('');
				$("#errmsgTaxCodeDiscount").html('');
				$("#errmsgTaxCodeDividend").html('');
				
				var fmtDate = '${appProp.jqueryDateFormat}';
				var rowPos= $(this).parents('tr');
				if (tableInterest.fnGetNodes().length == 0) {
					return false;
				}
				var rowPosNumber = tableInterest.fnGetPosition(rowPos[0]);
				var pos = tableInterest.fnGetPosition(this);
				if (pos[1] != 4) {
					
					var data = tableInterest.fnGetData(this.parentNode);
					data.id = new Object();
					$("#detailInterest #interestDetailForm #rowPositionRuleInt").val(rowPosNumber);
				 	
					// --- hidden value #taxProfileCodeRuleInt
					$("#detailInterest #interestDetailForm #taxProfileCodeRuleInt").val($('#taxProfileCode').val());
					
					// --- hidden value #securityTypeCodeRuleInt
					$("#detailInterest #interestDetailForm #securityTypeCodeRuleInt").val($('#securityType').val());
				 	
				 	// --- calendar #effectiveDateFrom
				 	var stringStartDate = data.effectiveDateFrom.toString();
					if (stringStartDate.substr(2,1) != "/") {
						$("#detailInterest #interestDetailForm #effectiveDateFrom").val($.datepicker.formatDate(fmtDate, new Date(data.effectiveDateFrom)));
					} else {
						$("#detailInterest #interestDetailForm #effectiveDateFrom").val(data.effectiveDateFrom);
					}
				 	$("#detailInterest #interestDetailForm #oldDateFrom").val($('#detailInterest #interestDetailForm #effectiveDateFrom').val());
					$("#detailInterest #interestDetailForm #newDateFrom").val($('#detailInterest #interestDetailForm #oldDateFrom').val());
					
					// --- calendar #effectiveDateTo
					var stringEndDate = data.effectiveDateTo.toString();
					if (stringEndDate.substr(2,1) != "/") {
						$("#detailInterest #interestDetailForm #effectiveDateTo").val($.datepicker.formatDate(fmtDate, new Date(data.effectiveDateTo)));
					} else {
						$("#detailInterest #interestDetailForm #effectiveDateTo").val(data.effectiveDateTo);
					}
					$("#detailInterest #interestDetailForm #oldDateTo").val($('#detailInterest #interestDetailForm #effectiveDateTo').val());
					$("#detailInterest #interestDetailForm #newDateTo").val($('#detailInterest #interestDetailForm #oldDateTo').val());
					
					// --- check #isAccruedInterest
					if ((data.taxObjectAccruedInterest) && (data.taxObjectAccruedInterest.taxMaster) && (jQuery.trim(data.taxObjectAccruedInterest.taxMaster.taxCode) !== '') && (jQuery.trim(data.taxObjectAccruedInterest.taxMaster.taxCode) !== null)) {
						$("#detailInterest #interestDetailForm #isAccruedInterest").attr('checked', true);
						$("#detailInterest #interestDetailForm #isAccruedInterestHidden").val(true);
						
						if ((('${mode}'=='entry') || ('${mode}'=='edit')) && (('${confirming}' == 'false') || ('${confirming}' == ''))) {
							$("#detailInterest #interestDetailForm #taxCodeAccruedInterest").removeAttr('disabled');
							$("#detailInterest #interestDetailForm #taxCodeHelpAccruedInterest").removeAttr('disabled');
						}
					} else {
						$("#detailInterest #interestDetailForm #isAccruedInterest").attr('checked', false);
						$("#detailInterest #interestDetailForm #isAccruedInterestHidden").val(false);
						
						if ((('${mode}'=='entry') || ('${mode}'=='edit')) && (('${confirming}' == 'false') || ('${confirming}' == ''))) {
							$("#detailInterest #interestDetailForm #taxCodeAccruedInterest").attr('disabled', true);
							$("#detailInterest #interestDetailForm #taxCodeHelpAccruedInterest").attr('disabled', true);
						}
					}
					
					// lookup #taxCodeAccruedInterest
					var dtlTaxObjectAccruedInterestTaxCode = '';
					if ((data.taxObjectAccruedInterest) && (data.taxObjectAccruedInterest.taxMaster) && (data.taxObjectAccruedInterest.taxMaster.taxCode)) {
						dtlTaxObjectAccruedInterestTaxCode = data.taxObjectAccruedInterest.taxMaster.taxCode;
					}
					$("#detailInterest #interestDetailForm #taxCodeAccruedInterest").val(dtlTaxObjectAccruedInterestTaxCode);
					
					var dtlTaxObjectAccruedInterestTaxKey = '';
					if ((data.taxObjectAccruedInterest) && (data.taxObjectAccruedInterest.taxMaster) && (data.taxObjectAccruedInterest.taxMaster.taxKey)) {
						dtlTaxObjectAccruedInterestTaxKey = data.taxObjectAccruedInterest.taxMaster.taxKey;
					}
					$("#detailInterest #interestDetailForm #taxKeyAccruedInterest").val(dtlTaxObjectAccruedInterestTaxKey);
					
					var dtlTaxObjectAccruedInterestDescription = '';
					if ((data.taxObjectAccruedInterest) && (data.taxObjectAccruedInterest.taxMaster) && (data.taxObjectAccruedInterest.taxMaster.description)) {
						dtlTaxObjectAccruedInterestDescription = data.taxObjectAccruedInterest.taxMaster.description;
					}
					$("#detailInterest #interestDetailForm #taxNameAccruedInterest").val(dtlTaxObjectAccruedInterestDescription);
					$("#detailInterest #interestDetailForm #h_taxNameAccruedInterest").val(dtlTaxObjectAccruedInterestDescription);
					
					// text #rateIntDetailAccruedInterest
					var dtlTaxObjectAccruedInterestTaxRate = '';
					if ((data.taxObjectAccruedInterest) && (data.taxObjectAccruedInterest.taxMaster) && (data.taxObjectAccruedInterest.taxMaster.taxRate)!=null) {
						dtlTaxObjectAccruedInterestTaxRate = data.taxObjectAccruedInterest.taxMaster.taxRate;
					}
					
					// check for #taxCodeAccruedInterest for #rateIntDetailAccruedInterest
					$("#detailInterest #interestDetailForm #rateIntDetailAccruedInterest").autoNumericSet(dtlTaxObjectAccruedInterestTaxRate,{vMin:-100});
					if (jQuery.trim($("#detailInterest #interestDetailForm #taxCodeAccruedInterest").val()) == '') {
						$("#detailInterest #interestDetailForm #rateIntDetailAccruedInterest").val('');
					}
					$("#detailInterest #interestDetailForm #rateIntDetailAccruedInterestStripped").val(dtlTaxObjectAccruedInterestTaxRate);
					
					// --- check #isCapitalGain
					if ((data.taxObjectCapitalGain) && (data.taxObjectCapitalGain.taxMaster) && (jQuery.trim(data.taxObjectCapitalGain.taxMaster.taxCode) !== '') && (jQuery.trim(data.taxObjectCapitalGain.taxMaster.taxCode) !== null)) {
						$("#detailInterest #interestDetailForm #isCapitalGain").attr('checked', true);
						$("#detailInterest #interestDetailForm #isCapitalGainHidden").val(true);
						
						if ((('${mode}'=='entry') || ('${mode}'=='edit')) && (('${confirming}' == 'false') || ('${confirming}' == ''))) {
							$("#detailInterest #interestDetailForm #taxCodeCapitalGain").removeAttr('disabled');
							$("#detailInterest #interestDetailForm #taxCodeHelpCapitalGain").removeAttr('disabled');
						}
					} else {
						$("#detailInterest #interestDetailForm #isCapitalGain").attr('checked', false);
						$("#detailInterest #interestDetailForm #isCapitalGainHidden").val(false);
						
						if ((('${mode}'=='entry') || ('${mode}'=='edit')) && (('${confirming}' == 'false') || ('${confirming}' == ''))) {
							$("#detailInterest #interestDetailForm #taxCodeCapitalGain").attr('disabled', true);
							$("#detailInterest #interestDetailForm #taxCodeHelpCapitalGain").attr('disabled', true);
						}
					}
					
					// lookup #taxCodeCapitalGain
					var dtlTaxObjectCapitalGainTaxCode = '';
					if ((data.taxObjectCapitalGain) && (data.taxObjectCapitalGain.taxMaster) && (data.taxObjectCapitalGain.taxMaster.taxCode)) {
						dtlTaxObjectCapitalGainTaxCode = data.taxObjectCapitalGain.taxMaster.taxCode;
					}
					$("#detailInterest #interestDetailForm #taxCodeCapitalGain").val(dtlTaxObjectCapitalGainTaxCode);
					
					var dtlTaxObjectCapitalGainTaxKey = '';
					if ((data.taxObjectCapitalGain) && (data.taxObjectCapitalGain.taxMaster) && (data.taxObjectCapitalGain.taxMaster.taxKey)) {
						dtlTaxObjectCapitalGainTaxKey = data.taxObjectCapitalGain.taxMaster.taxKey;
					}
					$("#detailInterest #interestDetailForm #taxKeyCapitalGain").val(dtlTaxObjectCapitalGainTaxKey);
					
					var dtlTaxObjectCapitalGainDescription = '';
					if ((data.taxObjectCapitalGain) && (data.taxObjectCapitalGain.taxMaster) && (data.taxObjectCapitalGain.taxMaster.description)) {
						dtlTaxObjectCapitalGainDescription = data.taxObjectCapitalGain.taxMaster.description;
					}
					$("#detailInterest #interestDetailForm #taxNameCapitalGain").val(dtlTaxObjectCapitalGainDescription);
					$("#detailInterest #interestDetailForm #h_taxNameCapitalGain").val(dtlTaxObjectCapitalGainDescription);
					
					// text #rateIntDetailCapitalGain
					var dtlTaxObjectCapitalGainTaxRate = '';
					if ((data.taxObjectCapitalGain) && (data.taxObjectCapitalGain.taxMaster) && (data.taxObjectCapitalGain.taxMaster.taxRate)!=null) {
						dtlTaxObjectCapitalGainTaxRate = data.taxObjectCapitalGain.taxMaster.taxRate;
					}
					// check for #taxCodeCapitalGain for #rateIntDetailCapitalGain
					$("#detailInterest #interestDetailForm #rateIntDetailCapitalGain").autoNumericSet(dtlTaxObjectCapitalGainTaxRate,{vMin:-100});
					if (jQuery.trim($("#detailInterest #interestDetailForm #taxCodeCapitalGain").val()) == '') {
						$("#detailInterest #interestDetailForm #rateIntDetailCapitalGain").val('');
					}
					$("#detailInterest #interestDetailForm #rateIntDetailCapitalGainStripped").val(dtlTaxObjectCapitalGainTaxRate);
					
					// Tax Compensation
					var dtlTaxObjectCapitalGainTaxCompensation = '';
					if ((data.taxObjectCapitalGain) && (data.taxObjectCapitalGain.taxCompensation)) {
						dtlTaxObjectCapitalGainTaxCompensation = data.taxObjectCapitalGain.taxCompensation;
					}
					
					var rateAccruedInterestTax = parseFloat(dtlTaxObjectAccruedInterestTaxRate);
					var rateCapitalGainTax = parseFloat(dtlTaxObjectCapitalGainTaxRate);
					var rateLossCompensation = parseFloat(dtlTaxObjectCapitalGainTaxCompensation);

					if (('${mode}'=='view') || ('${confirming}' == 'true')) {
						if (rateLossCompensation > 0) {
							$("#detailInterest #interestDetailForm #isLossCompensation").attr('checked', true);
							$("#detailInterest #interestDetailForm #isLossCompensationHidden").val(true);
							$("#detailInterest #interestDetailForm #rateLossCompensation").autoNumericSet(dtlTaxObjectCapitalGainTaxCompensation);
							$("#detailInterest #interestDetailForm #rateLossCompensationStripped").val(dtlTaxObjectCapitalGainTaxCompensation);
						} else {
							$("#detailInterest #interestDetailForm #isLossCompensation").attr('checked', false);
							$("#detailInterest #interestDetailForm #isLossCompensationHidden").val(false);
						}
					} else if ((('${mode}'=='entry') || ('${mode}'=='edit')) && (('${confirming}' == 'false') || ('${confirming}' == ''))) {
						if (rateLossCompensation > 0) {
							$("#detailInterest #interestDetailForm #isLossCompensation").removeAttr("disabled");
							$("#detailInterest #interestDetailForm #isLossCompensation").attr('checked', true);
							$("#detailInterest #interestDetailForm #isLossCompensationHidden").val(true);
							$("#detailInterest #interestDetailForm #rateLossCompensation").removeAttr("disabled");
							$("#detailInterest #interestDetailForm #rateLossCompensation").autoNumericSet(dtlTaxObjectCapitalGainTaxCompensation);
							$("#detailInterest #interestDetailForm #rateLossCompensationStripped").val(dtlTaxObjectCapitalGainTaxCompensation);
						} else {
							$("#detailInterest #interestDetailForm #isLossCompensation").removeAttr('checked');
							$("#detailInterest #interestDetailForm #isLossCompensationHidden").val(false);
							$("#detailInterest #interestDetailForm #rateLossCompensation").attr('disabled', true);
							if ((rateAccruedInterestTax > 0) && (rateCapitalGainTax > 0)) {
								$("#detailInterest #interestDetailForm #isLossCompensation").removeAttr("disabled");
							} else {
								$("#detailInterest #interestDetailForm #isLossCompensation").attr('disabled', true);
							}
						}
					}
					
					// --- check #isDiscount
					if ((data.taxObjectDiscount) && (data.taxObjectDiscount.taxMaster) && (jQuery.trim(data.taxObjectDiscount.taxMaster.taxCode) !== '') && (jQuery.trim(data.taxObjectDiscount.taxMaster.taxCode) !== null)) {
						$("#detailInterest #interestDetailForm #isDiscount").attr('checked', true);
						$("#detailInterest #interestDetailForm #isDiscountHidden").val(true);
						
						if ((('${mode}'=='entry') || ('${mode}'=='edit')) && (('${confirming}' == 'false') || ('${confirming}' == ''))) {
							$("#detailInterest #interestDetailForm #taxCodeDiscount").removeAttr('disabled');
							$("#detailInterest #interestDetailForm #taxCodeHelpDiscount").removeAttr('disabled');
						}
					} else {
						$("#detailInterest #interestDetailForm #isDiscount").attr('checked', false);
						$("#detailInterest #interestDetailForm #isDiscountHidden").val(false);
						
						if ((('${mode}'=='entry') || ('${mode}'=='edit')) && (('${confirming}' == 'false') || ('${confirming}' == ''))) {
							$("#detailInterest #interestDetailForm #taxCodeDiscount").attr('disabled', true);
							$("#detailInterest #interestDetailForm #taxCodeHelpDiscount").attr('disabled', true);
						}
					}
					
					// lookup #taxCodeDiscount
					var dtlTaxObjectDiscountTaxCode = '';
					if ((data.taxObjectDiscount) && (data.taxObjectDiscount.taxMaster) && (data.taxObjectDiscount.taxMaster.taxCode)) {
						dtlTaxObjectDiscountTaxCode = data.taxObjectDiscount.taxMaster.taxCode;
					}
					$("#detailInterest #interestDetailForm #taxCodeDiscount").val(dtlTaxObjectDiscountTaxCode);
					
					var dtlTaxObjectDiscountTaxKey = '';
					if ((data.taxObjectDiscount) && (data.taxObjectDiscount.taxMaster) && (data.taxObjectDiscount.taxMaster.taxKey)) {
						dtlTaxObjectDiscountTaxKey = data.taxObjectDiscount.taxMaster.taxKey;
					}
					$("#detailInterest #interestDetailForm #taxKeyDiscount").val(dtlTaxObjectDiscountTaxKey);
					
					var dtlTaxObjectDiscountDescription = '';
					if ((data.taxObjectDiscount) && (data.taxObjectDiscount.taxMaster) && (data.taxObjectDiscount.taxMaster.description)) {
						dtlTaxObjectDiscountDescription = data.taxObjectDiscount.taxMaster.description;
					}
					$("#detailInterest #interestDetailForm #taxNameDiscount").val(dtlTaxObjectDiscountDescription);
					$("#detailInterest #interestDetailForm #h_taxNameDiscount").val(dtlTaxObjectDiscountDescription);
					
					// text #rateIntDetailDiscount
					var dtlTaxObjectDiscountTaxRate = '';
					if ((data.taxObjectDiscount) && (data.taxObjectDiscount.taxMaster) && (data.taxObjectDiscount.taxMaster.taxRate)!=null) {
						dtlTaxObjectDiscountTaxRate = data.taxObjectDiscount.taxMaster.taxRate;
					}
					
					// check for #taxCodeDiscount for #rateIntDetailDiscount
					$("#detailInterest #interestDetailForm #rateIntDetailDiscount").autoNumericSet(dtlTaxObjectDiscountTaxRate,{vMin:-100});
					if (jQuery.trim($("#detailInterest #interestDetailForm #taxCodeDiscount").val()) == '') {
						$("#detailInterest #interestDetailForm #rateIntDetailDiscount").val('');
					}
					$("#detailInterest #interestDetailForm #rateIntDetailDiscountStripped").val(dtlTaxObjectDiscountTaxRate);
					
					// --- check #isDividend
					if ((data.taxObjectDividend) && (data.taxObjectDividend.taxMaster) && (jQuery.trim(data.taxObjectDividend.taxMaster.taxCode) !== '') && (jQuery.trim(data.taxObjectDividend.taxMaster.taxCode) !== null)) {
						$("#detailInterest #interestDetailForm #isDividend").attr('checked', true);
						$("#detailInterest #interestDetailForm #isDividendHidden").val(true);
						
						if ((('${mode}'=='entry') || ('${mode}'=='edit')) && (('${confirming}' == 'false') || ('${confirming}' == ''))) {
							$("#detailInterest #interestDetailForm #taxCodeDividend").removeAttr('disabled');
							$("#detailInterest #interestDetailForm #taxCodeHelpDividend").removeAttr('disabled');
						}
					} else {
						$("#detailInterest #interestDetailForm #isDividend").attr('checked', false);
						$("#detailInterest #interestDetailForm #isDividendHidden").val(false);
						
						if ((('${mode}'=='entry') || ('${mode}'=='edit')) && (('${confirming}' == 'false') || ('${confirming}' == ''))) {
							$("#detailInterest #interestDetailForm #taxCodeDividend").attr('disabled', true);
							$("#detailInterest #interestDetailForm #taxCodeHelpDividend").attr('disabled', true);
						}
					}
					
					// lookup #taxCodeDividend
					var dtlTaxObjectDividendTaxCode = '';
					if ((data.taxObjectDividend) && (data.taxObjectDividend.taxMaster) && (data.taxObjectDividend.taxMaster.taxCode)) {
						dtlTaxObjectDividendTaxCode = data.taxObjectDividend.taxMaster.taxCode;
					}
					$("#detailInterest #interestDetailForm #taxCodeDividend").val(dtlTaxObjectDividendTaxCode);
					
					var dtlTaxObjectDividendTaxKey = '';
					if ((data.taxObjectDividend) && (data.taxObjectDividend.taxMaster) && (data.taxObjectDividend.taxMaster.taxKey)) {
						dtlTaxObjectDividendTaxKey = data.taxObjectDividend.taxMaster.taxKey;
					}
					$("#detailInterest #interestDetailForm #taxKeyDividend").val(dtlTaxObjectDividendTaxKey);
					
					var dtlTaxObjectDividendDescription = '';
					if ((data.taxObjectDividend) && (data.taxObjectDividend.taxMaster) && (data.taxObjectDividend.taxMaster.description)) {
						dtlTaxObjectDividendDescription = data.taxObjectDividend.taxMaster.description;
					}
					$("#detailInterest #interestDetailForm #taxNameDividend").val(dtlTaxObjectDividendDescription);
					$("#detailInterest #interestDetailForm #h_taxNameDividend").val(dtlTaxObjectDividendDescription);
					
					// text #rateIntDetailDividend
					var dtlTaxObjectDividendTaxRate = '';
					if ((data.taxObjectDividend) && (data.taxObjectDividend.taxMaster) && (data.taxObjectDividend.taxMaster.taxRate)!=null) {
						dtlTaxObjectDividendTaxRate = data.taxObjectDividend.taxMaster.taxRate;
					}
					
					// check for #taxCodeDividend for #rateIntDetailDividend
					$("#detailInterest #interestDetailForm #rateIntDetailDividend").autoNumericSet(dtlTaxObjectDividendTaxRate,{vMin:-100});
					if (jQuery.trim($("#detailInterest #interestDetailForm #taxCodeDividend").val()) == '') {
						$("#detailInterest #interestDetailForm #rateIntDetailDividend").val('');
					}
					$("#detailInterest #interestDetailForm #rateIntDetailDividendStripped").val(dtlTaxObjectDividendTaxRate);
					
					
					$('#detailInterest').dialog('open');
					$('.ui-widget-overlay').css('height',$('body').height());
				}
			});
			
			//===== END EVENT EDIT INTEREST DETAIL =====//
		};
		
		// -------------EVENT CLICK BUTTON CANCEL FOR INTEREST DETAIL ------ //
		$('#cancelInterestDetail').click(function(){
			$("#detailInterest input:text").val("");
			$("#detailInterest input:hidden").val("");
			$("#errInterest").removeClass('fieldError');
			$("#errInterest").html('');
			$("#effectiveDateFrom").removeClass('fieldError');
			$("#effectiveDateFrom").removeClass('fieldError');
			$("#effectiveDateFromError").html('');
			$("#effectiveDateToError").html('');
			$("#effectiveDateFrom").val('');
			$("#effectiveDateTo").val('');
			$("#effectiveDateFrom").removeClass('fieldError');
			$("#effectiveDateTo").removeClass('fieldError');
			$("#taxCodeAccruedInterest").removeClass('fieldError');
			$("#taxCodeCapitalGain").removeClass('fieldError');
			$("#taxCodeDiscount").removeClass('fieldError');
			$("#taxCodeDividend").removeClass('fieldError');
			$("#errmsgTaxCodeAccruedInterest").html('');
			$("#errmsgTaxCodeCapitalGain").html('');
			$("#errmsgTaxCodeDiscount").html('');
			$("#errmsgTaxCodeDividend").html('');
			$("#detailInterest").dialog('close');
			return false;
		});
		
		$('#closeInterestDetail').click(function() {
			$("#detailInterest").dialog('close');
			return false;
		});
		
		// ---- EVENT BUTTON NEW DATA FOR INTEREST DETAIL ---- //
		
		$('.buttons #newInterest').click(function() {
			if ($('#securityType').val() == '') {
				$('#errSecType').html('Required');
				return false;
			} else {
				$('#errSecType').html('');
			}
			
			$("#errInterest").removeClass('fieldError');
			$("#effectiveDateFrom").removeClass('fieldError');
			$("#effectiveDateTo").removeClass('fieldError');

			$("#taxCodeAccruedInterest").removeClass('fieldError');
			$("#taxCodeCapitalGain").removeClass('fieldError');
			$("#taxCodeDiscount").removeClass('fieldError');
			$("#taxCodeDividend").removeClass('fieldError');			
			$("#errInterest").html('');
			$("#effectiveDateFromError").html('');
			$("#effectiveDateToError").html('');		
			$("#errmsgTaxCodeAccruedInterest").html('');
			$("#errmsgTaxCodeCapitalGain").html('');
			$("#errmsgTaxCodeDiscount").html('');
			$("#errmsgTaxCodeDividend").html('');
			
			$("#effectiveDateFrom").val('');	
			$("#effectiveDateTo").val('');	
			$("#rowPositionRuleInt").val('');	
			$("#oldDateFrom").val('');
			$("#newDateFrom").val('');
			$("#oldDateTo").val('');
			$("#newDateTo").val('');
			
			selectedRow = null; 
			$("#isAccruedInterest").attr('checked', false);
			$("#isAccruedInterestHidden").val(false);
			$("#isCapitalGain").attr('checked', false);
			$("#isCapitalGainHidden").val(false);
			$('#isLossCompensation').removeAttr('checked');
			$("#isLossCompensation").attr('checked', false);
			$('#isLossCompensation').attr('disabled', true);
			$("#isLossCompensationHidden").val(false);
			$("#rateLossCompensation").attr("disabled", true);
			$("#rateLossCompensation").val("");
			$("#rateLossCompensationStripped").val("");
			$("#isDiscount").attr('checked', false);
			$("#isDiscountHidden").val(false);
			$("#isDividend").attr('checked', false);
			$("#isDividendHidden").val(false);
			
			$('#taxCodeHelpAccruedInterest').val('?');
			$('#taxCodeHelpCapitalGain').val('?');
			$('#taxCodeHelpDiscount').val('?');
			$('#taxCodeHelpDividend').val('?');
			
			$('#taxCodeAccruedInterest').val('');
			$('#taxKeyAccruedInterest').val('');
			$('#taxNameAccruedInterest').val('');
			$('#h_taxNameAccruedInterest').val('');
			$('#rateIntDetailAccruedInterest').val('');
			$('#rateIntDetailAccruedInterestStripped').val('');
			
			$('#taxCodeCapitalGain').val('');
			$('#taxKeyCapitalGain').val('');
			$('#taxNameCapitalGain').val('');
			$('#h_taxNameCapitalGain').val('');
			$('#rateIntDetailCapitalGain').val('');
			$('#rateIntDetailCapitalGainStripped').val('');
			
			$('#isLossCompensation').removeAttr('checked');
			$('#isLossCompensationHidden').val(false);
			$('#rateLossCompensation').attr("disabled", true);
			$('#rateLossCompensation').val('');
			$('#rateLossCompensationStripped').val('');
			
			$('#taxCodeDiscount').val('');
			$('#taxKeyDiscount').val('');
			$('#taxNameDiscount').val('');
			$('#h_taxNameDiscount').val('');
			$('#rateIntDetailDiscount').val('');
			$('#rateIntDetailDiscountStripped').val('');
			
			$('#taxCodeDividend').val('');
			$('#taxKeyDividend').val('');
			$('#taxNameDividend').val('');
			$('#h_taxNameDividend').val('');
			$('#rateIntDetailDividend').val('');
			$('#rateIntDetailDividendStripped').val('');
			
			$('#taxCodeAccruedInterest').attr("disabled", true);
			$('#taxCodeHelpAccruedInterest').attr("disabled", true);
			
			$('#taxCodeCapitalGain').attr('disabled',true);
			$('#taxCodeHelpCapitalGain').attr('disabled',true);
			
			$('#taxCodeDiscount').attr('disabled',true);
			$('#taxCodeHelpDiscount').attr('disabled',true);
			
			$('#taxCodeDividend').attr('disabled',true);
			$('#taxCodeHelpDividend').attr('disabled',true);
			
			$("#detailInterest").dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
			$("#detailInterest #interestDetailForm #taxProfileCodeRuleInt").val($('#taxProfileCode').val());
			$("#detailInterest #interestDetailForm #securityTypeCodeRuleInt").val($('#securityType').val());
			return false;
		});
		
		//---- EVENT CLICK BUTTON SAVE IN INTEREST DETAIL  ----//
		$('#addInterestDetail').click(function() {
			var rowPosition = $("#detailInterest #interestDetailForm #rowPositionRuleInt").val();
			var table = $('#listInterest #gridInterest').dataTable();
			var datas = table.fnGetData(parseFloat(rowPosition));
			var effectiveDateFrom = $('#detailInterest #interestDetailForm #effectiveDateFrom').val();
			var oldDateFrom = $('#detailInterest #interestDetailForm #oldDateFrom').val();
			var newDateFrom = $('#detailInterest #interestDetailForm #newDateFrom').val();
			var effectiveDateTo = $('#detailInterest #interestDetailForm #effectiveDateTo').val();
			var oldDateTo = $('#detailInterest #interestDetailForm #oldDateTo').val();
			var newDateTo = $('#detailInterest #interestDetailForm #newDateTo').val();
			var errMsgDate = "Interest already exist!";
			// validate  //
			if ((effectiveDateFrom == "") || (effectiveDateTo == "")){
				if (effectiveDateFrom == "") {
					$('#effectiveDateFromError').html('Required');
				} else {
					$('#effectiveDateFromError').html('');
				}

				if (effectiveDateTo == "") {
					$('#effectiveDateToError').html('Required');
				} else {
					$('#effectiveDateToError').html('');
				}
								
				return false;
			} else {
				if (effectiveDateFrom !== "") {
					$('#effectiveDateFromError').html('');
				}
				if (effectiveDateTo !== "") {
					$('#effectiveDateToError').html('');
				}
			}
			
			if (($('#detailInterest #interestDetailForm #effectiveDateFrom').hasClass('fieldError')) || ($('#detailInterest #interestDetailForm #effectiveDateTo').hasClass('fieldError'))) {
				return false;
			}
			
			if (($('#detailInterest #interestDetailForm #taxCodeAccruedInterest').val() == '') && ($('#detailInterest #interestDetailForm #taxCodeCapitalGain').val() == '') && ($('#detailInterest #interestDetailForm #taxCodeDiscount').val() == '') && ($('#detailInterest #interestDetailForm #taxCodeDividend').val() == '')) {
				$('#errInterest').html('At least one tax object must be filled');
				return false;
			} else {
				$('#errInterest').html('');
			}
			
			if ((($('#detailInterest #interestDetailForm #isAccruedInterest').is(':checked')) && ($('#detailInterest #interestDetailForm #taxCodeAccruedInterest').val() == '')) ||
					(($('#detailInterest #interestDetailForm #isCapitalGain').is(':checked')) && ($('#detailInterest #interestDetailForm #taxCodeCapitalGain').val() == '')) ||
					(($('#detailInterest #interestDetailForm #isDiscount').is(':checked')) && ($('#detailInterest #interestDetailForm #taxCodeDiscount').val() == '')) ||
					(($('#detailInterest #interestDetailForm #isDividend').is(':checked')) && ($('#detailInterest #interestDetailForm #taxCodeDividend').val() == ''))) {
				if (($('#detailInterest #interestDetailForm #isAccruedInterest').is(':checked')) && ($('#detailInterest #interestDetailForm #taxCodeAccruedInterest').val() == '')) {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeAccruedInterest').html('Required');
				} else {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeAccruedInterest').html('');
				}
				
				if (($('#detailInterest #interestDetailForm #isCapitalGain').is(':checked')) && ($('#detailInterest #interestDetailForm #taxCodeCapitalGain').val() == '')) {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeCapitalGain').html('Required');
				} else {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeCapitalGain').html('');
				}
				
				if (($('#detailInterest #interestDetailForm #isDiscount').is(':checked')) && ($('#detailInterest #interestDetailForm #taxCodeDiscount').val() == '')) {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeDiscount').html('Required');
				} else {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeDiscount').html('');
				}
				
				if (($('#detailInterest #interestDetailForm #isDividend').is(':checked')) && ($('#detailInterest #interestDetailForm #taxCodeDividend').val() == '')) {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeDividend').html('Required');
				} else {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeDividend').html('');
				}
				
				return false;
			} else {
				if ($('#detailInterest #interestDetailForm #taxCodeAccruedInterest').val() !== '') {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeAccruedInterest').html('');
				}
				if ($('#detailInterest #interestDetailForm #taxCodeCapitalGain').val() !== '') {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeCapitalGain').html('');
				}
				if ($('#detailInterest #interestDetailForm #taxCodeDiscount').val() !== '') {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeDiscount').html('');
				}
				if ($('#detailInterest #interestDetailForm #taxCodeDividend').val() !== '') {
					$('#detailInterest #interestDetailForm #errmsgTaxCodeDividend').html('');
				}
			}
			
			if (($('#detailInterest #interestDetailForm #isLossCompensation').is(":checked")) && ($('#detailInterest #interestDetailForm #rateLossCompensation').val() == '')) {
				$('#detailInterest #interestDetailForm #errmsgRateLossCompensation').html('Required');
				return false;
			} else {
				$('#detailInterest #interestDetailForm #errmsgRateLossCompensation').html('');
			}
			
			//---------------- //
			
			if (rowPosition != "") {
				var found = false;
				var rows = table.fnGetNodes().length;
				for ( var i = 0; i < rows; i++) {
					var cells = table.fnGetData(i);
					
					if (((cells.effectiveDateFrom == effectiveDateFrom) && (oldDateFrom !== newDateFrom)) && ((cells.effectiveDateTo == effectiveDateTo) && (oldDateTo !== newDateTo))) {
						$('#errInterest').html(errMsgDate);
						found = true;
						return false;
					}
				}
				
				if (!found) {
					/*table.fnUpdate((
							datas.effectiveDateFrom = $.datepicker.formatDate('${appProp.jqueryDateFormat}', $("#detailInterest #interestDetailForm #effectiveDateFrom").val())),parseFloat(rowPosition),0);*/
					table.fnUpdate(((
							datas.effectiveDateFrom = $("#detailInterest #interestDetailForm #effectiveDateFrom").val()) && (datas.effectiveDateFromLong = new Date($("#detailInterest #interestDetailForm #effectiveDateFrom").datepicker('getDate')).getTime())),parseFloat(rowPosition),0);
					
					/*table.fnUpdate((
							datas.effectiveDateTo = $.datepicker.formatDate('${appProp.jqueryDateFormat}', $("#detailInterest #interestDetailForm #effectiveDateTo").val())),parseFloat(rowPosition),1);*/
					table.fnUpdate(((
							datas.effectiveDateTo = $("#detailInterest #interestDetailForm #effectiveDateTo").val()) && (datas.effectiveDateToLong = new Date($("#detailInterest #interestDetailForm #effectiveDateTo").datepicker('getDate')).getTime())),parseFloat(rowPosition),1);
					
					datas.taxObjectCapitalGain = new Object();
					datas.taxObjectCapitalGain.id = new Object();
					datas.taxObjectCapitalGain.taxMaster = new Object();
					
					datas.taxObjectAccruedInterest = new Object();
					datas.taxObjectAccruedInterest.id = new Object();
					datas.taxObjectAccruedInterest.taxMaster = new Object();
					
					datas.taxObjectDiscount = new Object();
					datas.taxObjectDiscount.id = new Object();
					datas.taxObjectDiscount.taxMaster = new Object();
					
					datas.taxObjectDividend = new Object();
					datas.taxObjectDividend.id = new Object();
					datas.taxObjectDividend.taxMaster = new Object();
					
					table.fnUpdate((
							(datas.taxObjectCapitalGain.id.taxProfileCode = $("#detailInterest #interestDetailForm #taxProfileCodeRuleInt").val()) &&
							(datas.taxObjectCapitalGain.id.securityTypeCode = $("#detailInterest #interestDetailForm #securityTypeCodeRuleInt").val()) &&
							(datas.taxObjectCapitalGain.taxMaster.taxKey = $("#detailInterest #interestDetailForm #taxKeyCapitalGain").val()) &&
							(datas.taxObjectCapitalGain.taxMaster.taxCode = $("#detailInterest #interestDetailForm #taxCodeCapitalGain").val()) &&
							(datas.taxObjectCapitalGain.taxMaster.description = $("#detailInterest #interestDetailForm #taxNameCapitalGain").val()) &&
							(datas.taxObjectCapitalGain.taxMaster.taxRate = $("#detailInterest #interestDetailForm #rateIntDetailCapitalGainStripped").val()) &&
							(datas.taxObjectCapitalGain.taxCompensation = ($("#detailInterest #interestDetailForm #rateLossCompensationStripped").val() == '') ? '' : $("#detailInterest #interestDetailForm #rateLossCompensationStripped").val())),parseFloat(rowPosition),2);
					
					table.fnUpdate((
							(datas.taxObjectAccruedInterest.id.taxProfileCode = $("#detailInterest #interestDetailForm #taxProfileCodeRuleInt").val()) &&
							(datas.taxObjectAccruedInterest.id.securityTypeCode = $("#detailInterest #interestDetailForm #securityTypeCodeRuleInt").val()) &&
							(datas.taxObjectAccruedInterest.taxMaster.taxKey = $("#detailInterest #interestDetailForm #taxKeyAccruedInterest").val()) &&
							(datas.taxObjectAccruedInterest.taxMaster.taxCode = $("#detailInterest #interestDetailForm #taxCodeAccruedInterest").val()) &&
							(datas.taxObjectAccruedInterest.taxMaster.description = $("#detailInterest #interestDetailForm #taxNameAccruedInterest").val()) &&
							(datas.taxObjectAccruedInterest.taxMaster.taxRate = $("#detailInterest #interestDetailForm #rateIntDetailAccruedInterestStripped").val())
							),parseFloat(rowPosition),3);
					
					table.fnUpdate((
							(datas.taxObjectDiscount.id.taxProfileCode = $("#detailInterest #interestDetailForm #taxProfileCodeRuleInt").val())&&
							(datas.taxObjectDiscount.id.securityTypeCode = $("#detailInterest #interestDetailForm #securityTypeCodeRuleInt").val())&&
							(datas.taxObjectDiscount.taxMaster.taxKey = $("#detailInterest #interestDetailForm #taxKeyDiscount").val())&&
							(datas.taxObjectDiscount.taxMaster.taxCode = $("#detailInterest #interestDetailForm #taxCodeDiscount").val())&&
							(datas.taxObjectDiscount.taxMaster.description = $("#detailInterest #interestDetailForm #taxNameDiscount").val())&&
							(datas.taxObjectDiscount.taxMaster.taxRate = $("#detailInterest #interestDetailForm #rateIntDetailDiscountStripped").val())
							),parseFloat(rowPosition),3);
					
					table.fnUpdate((
							(datas.taxObjectDividend.id.taxProfileCode = $("#detailInterest #interestDetailForm #taxProfileCodeRuleInt").val())&&
							(datas.taxObjectDividend.id.securityTypeCode = $("#detailInterest #interestDetailForm #securityTypeCodeRuleInt").val())&&
							(datas.taxObjectDividend.taxMaster.taxKey = $("#detailInterest #interestDetailForm #taxKeyDividend").val())&&
							(datas.taxObjectDividend.taxMaster.taxCode = $("#detailInterest #interestDetailForm #taxCodeDividend").val())&&
							(datas.taxObjectDividend.taxMaster.description = $("#detailInterest #interestDetailForm #taxNameDividend").val())&&
							(datas.taxObjectDividend.taxMaster.taxRate = $("#detailInterest #interestDetailForm #rateIntDetailDividendStripped").val())
						),parseFloat(rowPosition),4);
					
					$('#detailInterest').dialog('close');
					return false;
				}
			} else {
				var found = false;
				var rows = table.fnGetNodes().length;
				for ( var i = 0; i < rows; i++) {
					var cells = table.fnGetData(i);
					if ((cells.effectiveDateFrom == effectiveDateFrom) && (cells.effectiveDateTo == effectiveDateTo)) {
						$('#errInterest').html(errMsgDate);
						found = true;
						return false;
					}
				}
				
				if (!found) {
					// ---- ADD INTEREST GRID FOR DATA EMPTY(NEW DATA) ---- //
					var datas = new Object();
					datas.effectiveDateFrom = new Date();
					datas.effectiveDateFromLong = new Object();
					datas.effectiveDateTo = new Date();
					datas.effectiveDateToLong = new Object();
					datas.taxObjectCapitalGain = new Object();
					datas.taxObjectCapitalGain.id = new Object();
					datas.taxObjectCapitalGain.taxMaster = new Object();
					datas.taxObjectCapitalGain.taxObject = new Object();
					datas.taxObjectAccruedInterest = new Object();
					datas.taxObjectAccruedInterest.id = new Object();
					datas.taxObjectAccruedInterest.taxMaster = new Object();
					datas.taxObjectAccruedInterest.taxObject = new Object();
					datas.taxObjectDiscount = new Object();
					datas.taxObjectDiscount.id = new Object();
					datas.taxObjectDiscount.taxMaster = new Object();
					datas.taxObjectDiscount.taxObject = new Object();
					datas.taxObjectDividend = new Object();
					datas.taxObjectDividend.id = new Object();
					datas.taxObjectDividend.taxMaster = new Object();
					datas.taxObjectDividend.taxObject = new Object();
					
					datas.effectiveDateFrom = $("#detailInterest #interestDetailForm #effectiveDateFrom").datepicker('getDate');
					datas.effectiveDateFromLong = new Date($("#detailInterest #interestDetailForm #effectiveDateFrom").datepicker('getDate')).getTime();
					datas.effectiveDateTo = $("#detailInterest #interestDetailForm #effectiveDateTo").datepicker('getDate');
					datas.effectiveDateToLong = new Date($("#detailInterest #interestDetailForm #effectiveDateTo").datepicker('getDate')).getTime();
					datas.taxObjectCapitalGain.id.taxProfileCode = $("#detailInterest #interestDetailForm #taxProfileCodeRuleInt").val();
					datas.taxObjectCapitalGain.id.securityTypeCode = $("#detailInterest #interestDetailForm #securityTypeCodeRuleInt").val();
					datas.taxObjectCapitalGain.taxMaster.taxKey = $("#detailInterest #interestDetailForm #taxKeyCapitalGain").val();
					datas.taxObjectCapitalGain.taxMaster.taxCode = $("#detailInterest #interestDetailForm #taxCodeCapitalGain").val();
					datas.taxObjectCapitalGain.taxMaster.description = $("#detailInterest #interestDetailForm #taxNameCapitalGain").val();
					datas.taxObjectCapitalGain.taxMaster.taxRate = $("#detailInterest #interestDetailForm #rateIntDetailCapitalGainStripped").val();
					datas.taxObjectCapitalGain.taxCompensation = ($("#detailInterest #interestDetailForm #rateLossCompensationStripped").val() == '') ? $("#detailInterest #interestDetailForm #rateLossCompensationStripped").val('') : $("#detailInterest #interestDetailForm #rateLossCompensationStripped").val();
					datas.taxObjectAccruedInterest.id.taxProfileCode = $("#detailInterest #interestDetailForm #taxProfileCodeRuleInt").val();
					datas.taxObjectAccruedInterest.id.securityTypeCode = $("#detailInterest #interestDetailForm #securityTypeCodeRuleInt").val();
					datas.taxObjectAccruedInterest.taxMaster.taxKey = $("#detailInterest #interestDetailForm #taxKeyAccruedInterest").val();
					datas.taxObjectAccruedInterest.taxMaster.taxCode = $("#detailInterest #interestDetailForm #taxCodeAccruedInterest").val();
					datas.taxObjectAccruedInterest.taxMaster.description = $("#detailInterest #interestDetailForm #taxNameAccruedInterest").val();
					datas.taxObjectAccruedInterest.taxMaster.taxRate = $("#detailInterest #interestDetailForm #rateIntDetailAccruedInterestStripped").val();
					datas.taxObjectDiscount.id.taxProfileCode = $("#detailInterest #interestDetailForm #taxProfileCodeRuleInt").val();
					datas.taxObjectDiscount.id.securityTypeCode = $("#detailInterest #interestDetailForm #securityTypeCodeRuleInt").val();
					datas.taxObjectDiscount.taxMaster.taxKey = $("#detailInterest #interestDetailForm #taxKeyDiscount").val();
					datas.taxObjectDiscount.taxMaster.taxCode = $("#detailInterest #interestDetailForm #taxCodeDiscount").val();
					datas.taxObjectDiscount.taxMaster.description = $("#detailInterest #interestDetailForm #taxNameDiscount").val();
					datas.taxObjectDiscount.taxMaster.taxRate = $("#detailInterest #interestDetailForm #rateIntDetailDiscountStripped").val();
					datas.taxObjectDividend.id.taxProfileCode = $("#detailInterest #interestDetailForm #taxProfileCodeRuleInt").val();
					datas.taxObjectDividend.id.securityTypeCode = $("#detailInterest #interestDetailForm #securityTypeCodeRuleInt").val();
					datas.taxObjectDividend.taxMaster.taxKey = $("#detailInterest #interestDetailForm #taxKeyDividend").val();
					datas.taxObjectDividend.taxMaster.taxCode = $("#detailInterest #interestDetailForm #taxCodeDividend").val();
					datas.taxObjectDividend.taxMaster.description = $("#detailInterest #interestDetailForm #taxNameDividend").val();
					datas.taxObjectDividend.taxMaster.taxRate = $("#detailInterest #interestDetailForm #rateIntDetailDividendStripped").val();
					
					table.fnAddData(datas);
					
					$('#detailInterest').dialog('close');
					return false;
				}
			}
			
		});
		
		//-----------------------------------------------------//
		
		// ============ EVENT DELETE ROW IN GRID INTEREST =============== //
		$('#listInterest #gridInterest tbody tr #deleteButton').live('click', function() {
			var row = $(this).parents('tr');
			var rowNumber = ($('#listInterest #gridInterest').dataTable().fnGetPosition(row[0]));
			
			var deleteTaxInterest = function(){
				tableInterest.fnDeleteRow(rowNumber);
				var idTable = $("#listInterest #gridInterest");
				var trs = $("tr", idTable);
				$.each(trs, function(idx, data){
					var hiddens = $("[type=hidden]", $(this));
					$(hiddens).eq(0).attr("name", "profRuleGrid["+(idx-1)+"].effectiveDateFrom");
					$(hiddens).eq(1).attr("name", "profRuleGrid["+(idx-1)+"].effectiveDateFromLong");
					$(hiddens).eq(2).attr("name", "profRuleGrid["+(idx-1)+"].effectiveDateTo");
					$(hiddens).eq(3).attr("name", "profRuleGrid["+(idx-1)+"].effectiveDateToLong");
					
					$(hiddens).eq(4).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectCapitalGain.id.taxProfileCode");
					$(hiddens).eq(5).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectCapitalGain.id.securityTypeCode");
					$(hiddens).eq(6).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectCapitalGain.taxMaster.taxKey");
					$(hiddens).eq(7).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectCapitalGain.taxMaster.taxCode");
					$(hiddens).eq(8).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectCapitalGain.taxMaster.description");
					$(hiddens).eq(9).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectCapitalGain.taxMaster.taxRate");
					$(hiddens).eq(10).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectCapitalGain.taxCompensation");

					$(hiddens).eq(11).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectAccruedInterest.id.taxProfileCode");
					$(hiddens).eq(12).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectAccruedInterest.id.securityTypeCode");
					$(hiddens).eq(13).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectAccruedInterest.taxMaster.taxKey");
					$(hiddens).eq(14).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectAccruedInterest.taxMaster.taxCode");
					$(hiddens).eq(15).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectAccruedInterest.taxMaster.description");
					$(hiddens).eq(16).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectAccruedInterest.taxMaster.taxRate");
					
					$(hiddens).eq(17).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDiscount.id.taxProfileCode");
					$(hiddens).eq(18).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDiscount.id.securityTypeCode");
					$(hiddens).eq(19).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDiscount.taxMaster.taxKey");
					$(hiddens).eq(20).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDiscount.taxMaster.taxCode");
					$(hiddens).eq(21).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDiscount.taxMaster.description");
					$(hiddens).eq(22).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDiscount.taxMaster.taxRate");
					
					$(hiddens).eq(23).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDividend.id.taxProfileCode");
					$(hiddens).eq(24).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDividend.id.securityTypeCode");
					$(hiddens).eq(25).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDividend.taxMaster.taxKey");
					$(hiddens).eq(26).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDividend.taxMaster.taxCode");
					$(hiddens).eq(27).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDividend.taxMaster.description");
					$(hiddens).eq(28).attr("name", "profRuleGrid["+(idx-1)+"].taxObjectDividend.taxMaster.taxRate");
				});
				$("#dialog-message").dialog("close");
				return false;
				
			}
			messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteTaxInterest, closeDialog);
			
		});
		//==========================================================================================//
		
		 $("#detailInterest").dialog({
			autoOpen:false,
			modal:true,
			heigth:'700px',
			width:'550px',
			resizable:false
		});
		
		//*************************** END INTEREST DETAIL *************************************//		
				
		$('#securityType').lookup({
			list:'@{Pick.securityTypes()}',
			get:{
			//	url:'@{Pick.securityTypeWithInterestTax()}',
				url:'@{Pick.securityType()}',
				success: function(data) {
					if (data) {
						$('#securityType').removeClass('fieldError');
						$('#securityType').val(data.code);
						$('#securityTypeDesc').val(data.description);
						$('#h_securityTypeDesc').val(data.description);
					}	
				},
				error: function(data) {
					$('#securityType').addClass('fieldError');
					$('#securityTypeDesc').val('NOT FOUND');
					$('#securityType').val('');
					$('#h_securityTypeDesc').val('');
				}
			},
		//	filter:'$interestTaxSetup',
			description:$('#securityTypeDesc'),
			help:$('#securityTypeHelp')
		});
		
		$("input[id*='effectiveDateFrom']").change(function() {
			if(this.value != ""){
				//var row = $("#rowPosition").val();
				var oldDateFrom = $("#oldDateFrom").val();
				var value = $(this).datepicker('getDate');
				validateEffectiveDateFromTo(value,"start", oldDateFrom);
			}
			
			var dateFrom = $(this).datepicker('getDate');
			var dateTo = $('#effectiveDateTo').datepicker('getDate');
			var origin = 'from';
			var id = '#effectiveDate';
			if (($(this).val()!='') && (!$(this).hasClass('fieldError')) && ($('#effectiveDateTo').val()!=''))
				compareDateFromTo(dateFrom, dateTo, origin, id);
			
			var startDateLong = (new Date($('#effectiveDateFrom').datepicker("getDate"))).getTime();
			$('#effectiveDateFromHidden').val(startDateLong);
			
			var endDateLong = (new Date($('#effectiveDateTo').datepicker("getDate"))).getTime();
			$('#effectiveDateToHidden').val(endDateLong);
			
		});
		
		$("input[id*='effectiveDateTo']").change(function() {
			if(this.value != ""){
				//var row = $("#rowPosition").val();
				var oldDateFrom = $("#oldDateFrom").val();
				var value = $(this).datepicker('getDate');
				validateEffectiveDateFromTo(value,"end", oldDateFrom);
			}
			
			var dateFrom = $('#effectiveDateFrom').datepicker('getDate');
			var dateTo = $(this).datepicker('getDate');
			var origin = 'to';
			var id = '#effectiveDate';
			if (($(this).val()!='') && (!$(this).hasClass('fieldError')) && ($('#effectiveDateFrom').val()!=''))
				compareDateFromTo(dateFrom, dateTo, origin, id);
			
			var startDateLong = (new Date($('#effectiveDateFrom').datepicker("getDate"))).getTime();
			$('#effectiveDateFromHidden').val(startDateLong);
			
			var endDateLong = (new Date($('#effectiveDateTo').datepicker("getDate"))).getTime();
			$('#effectiveDateToHidden').val(endDateLong);
		});
		
		
		//================== BUTTON ACTION ==================//
		// Action buttons 
		/*$('#save').click(function() {
			
			// --- Validation Main Form --- //
			
			if ($('#securityType').val()=="") {
				$('#errSecType').html('Required');
				return false;
			} else {
				$('#errSecType').html('');
			}
			
			// ---------------------------------//
			
			var txProfileRule = new Object();
			txProfileRule.securityType = new Object();
			txProfileRule.txProfile = new Object();
			txProfileRule.id = new Object();
			txProfileRule.id.taxProfileCode = $('#taxProfileRuleCode').val();
			txProfileRule.id.securityTypeCode = $('#taxProfileRuleCodeSecType').val();
			txProfileRule.txProfile.taxProfileCode = $('#taxProfileCode').val();
			txProfileRule.txProfile.description = $('#taxProfileDesc').val();
			txProfileRule.securityType.securityType = $('#securityType').val();
			txProfileRule.securityType.description = $('#securityTypeDesc').val();
			//txProfileRule.txProfRuleInts = $('#gridInterest').dataTable().fnGetData();
			txProfileRule.active = $('#interestTaxSetupForm #isActiveHidden').val();
			
			txProfileRule.lstTxProfRuleIntDetailGridItem = $('#listInterest #gridInterest').dataTable().fnGetData();
			
			loading.dialog('open');
			$.ajax({
				type: 'POST',
				url: '@{TaxProfileRules.save()}?id='+$('#taxProfileRuleCode').val()+$('#taxProfileRuleCodeSecType').val()+'&mode=${mode}'+'&status=${status}'+'&isNewRec=${isNewRec}',
				data: JSON.stringify(txProfileRule),
				contentType: 'application/json; charset=utf-8',
				dataType: 'json',
				success: function() {
					location.href='@{TaxProfileRules.confirming()}?id='+$('#taxProfileRuleCode').val()+$('#taxProfileRuleCodeSecType').val()+'&mode=${mode}'+'&status=${status}'+'&isNewRec=${isNewRec}';
				}
				//return false;
			});
			return false;
			
		});
		
		$('#confirm').click(function() {
			loading.dialog('open');
			$.ajax({
				  url: '@{TaxProfileRules.confirm()}?id='+$('#taxProfileRuleCode').val()+$('#taxProfileRuleCodeSecType').val()+'&mode=${mode}'+'&status=${status}'+'&isNewRec=${isNewRec}',
					success: function(data) {
					if(data == "error") {
						location.href='@{TaxProfileRules.confirming()}?id='+$('#taxProfileRuleCode').val()+$('#taxProfileRuleCodeSecType').val()+'&mode=${mode}'+'&status=${status}'+'&isNewRec=${isNewRec}';
					} else {
						//alert($('#taxProfileRuleCode').val());
						//alert($('#taxProfileCode').val());
						location.href='@{TaxProfileRules.list()}?taxProfileCode='+$('#taxProfileCode').val();
					}
					return data; 	
				 }
			});
			return false;
			
		});
		$('#back').click(function() {
			location.href='@{TaxProfileRules.back()}/'+$('#taxProfileRuleCode').val()+$('#taxProfileRuleCodeSecType').val()+'?mode=edit';
			
			//location.href='@{back(id)}?mode=${mode}#{if status}&status=${status}#{/}#{if group}&group=${group}#{/}';
			return false;
		});
		$('#cancel').click(function() {
			//location.href='@{TaxProfileRules.list(txProfileRule?.txProfile?.taxProfileCode)}';
			location.href='@{TaxProfileRules.list()}?taxProfileCode='+$('#taxProfileCode').val();
			return false;
		});*/
		
		// ==============================================//
		
	});

	function doClose() {
		location.href='@{TaxProfileRules.list()}?taxProfileCode='+$('#taxProfileCode').val();
		return false;
	}

	function doCancel() {
		location.href='@{TaxProfileRules.list()}?taxProfileCode='+$('#taxProfileCode').val();
		return false;
	}

	function validateEffectiveDateFromTo(date, command, oldStartDate) {
		var tableInterest = $('#listInterest #gridInterest').dataTable();
		var length = tableInterest.fnGetNodes().length;
		var effectiveDateFromArr = new Array();
		var effectiveDateToArr = new Array();
		var selectDate = new Date(date).getTime();
		if (oldStartDate != "") {
			oldStartDate = ($.datepicker.parseDate('${appProp.jqueryDateFormat}', oldStartDate)).getTime();
		} else {
			oldStartDate = 0;
		}
		
		$('#listInterest #gridInterest tbody tr').each(function(idxtr, eltr){
			var currentEffectiveDateFromLong = 0;
			var currentEffectiveDateToLong = 0;
		    $('td', $(eltr)).each(function(idxtd, eltd){
		        var inpEffectiveDateFromLong = $(eltd).find("input[name*='effectiveDateFromLong']");
		        var inpEffectiveDateToLong = $(eltd).find("input[name*='effectiveDateToLong']");
		        if (inpEffectiveDateFromLong.val() != undefined) {
		        	currentEffectiveDateFromLong = inpEffectiveDateFromLong.val();
		        }
		        if (inpEffectiveDateToLong.val() != undefined) {
		        	currentEffectiveDateToLong = inpEffectiveDateToLong.val();
				}
		    });
		    if ((currentEffectiveDateFromLong > 0) && (oldStartDate != currentEffectiveDateFromLong)) {
		    	effectiveDateFromArr[idxtr] = currentEffectiveDateFromLong;
				effectiveDateToArr[idxtr] = currentEffectiveDateToLong;
			}
		});
		
		effectiveDateFromArr.sort();
		effectiveDateToArr.sort();
		if (length != 0) {
			//console.debug("min start date :"+new Date(startDateArr[0]));
			if(command == 'start'){
				$("#effectiveDateFromError").html("");
				for(var i=0; i< length;i++){
					if ((selectDate >= parseInt(effectiveDateFromArr[i])) && (selectDate <= parseInt(effectiveDateToArr[i]))) {
						$('#effectiveDateFrom').addClass('fieldError');
						$("#effectiveDateFromError").html("'Effective Date *' (From) has been exist");
						break;
					}
					if($("#effectiveDateTo").val() != ""){
						var endDateNew = new Date($("#effectiveDateTo").datepicker('getDate')).getTime();
						//for (var i=0; i<length; i++) {
						if ((selectDate < parseInt(effectiveDateFromArr[i]))) {
							if ((endDateNew >= parseInt(effectiveDateFromArr[i]))) {
								$('#effectiveDateTo').val("");
								break;
							} 
						}
						//}
					}
				}
			}


			if(command == 'end') {
				if ($("#effectiveDateFrom").val() != "") {
					var newStartDate = new Date($('#effectiveDateFrom').datepicker('getDate')).getTime();
					for(var i=0; i<length; i++){

						if ((newStartDate >= effectiveDateFromArr[i]) && (newStartDate <= effectiveDateToArr[i])) {
							$('#effectiveDateFrom').addClass('fieldError');
							$("#effectiveDateFromError").html("'Effective Date *' (From) has been exist");
							$('#effectiveDateTo').val("");
							break;
						}

						if ((selectDate >= effectiveDateFromArr[i]) && (selectDate <= effectiveDateToArr[i])) {
							$('#effectiveDateTo').addClass('fieldError');
							$("#effectiveDateToError").html("'Effective Date *' (To) has been exist");
							break;
						}

						if ((newStartDate < effectiveDateFromArr[i])) {
							if ((selectDate >= effectiveDateFromArr[i])) {
								//if(selectDate > endDateArr[i]){
								//	$('#endDate'+type).val("");
								//} else {
								$('#effectiveDateTo').addClass('fieldError');
								$("#effectiveDateToError").html("'Effective Date *' (To) has been exist");
								//}
								break;
							} 
						}
					}
				} else {
					$('#effectiveDateTo').val("");
				}

			}
		}	
	}
		
		// Function to get the Maximam value in Array
		Array.max = function( array ){
		return Math.max.apply( Math, array );
		};

		// Function to get the Minimam value in Array
		Array.min = function( array ){
		return Math.min.apply( Math, array );
		};