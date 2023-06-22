var chargeGrid;
var scheduleGrid;
var tabChargeVisited = false;
var tabScheduleVisited = false;
var reloadCharge = true;
$(function(){
	$('#tabs').tabs();
	$('#save').button();
	$('#clear').button();
	$('#cancel').button();
	$('#confirm').button();
	$('#back').button();
	$('#generateButton').button();
	$('#next1').button();
	$('#next2').button();
	$('#prev1').button();
	$('#prev2').button();
	$('#addCharge').button();
	$('#resetCharge').button();
	$('#chargeDone').button();
	
	if ('${mode}'=='entry'){
		$('#paymentFreq1').attr('checked', true);
		$('#paymentFreqHidden').val($('#paymentFreq1').val());
	}
	
	if ($.browser.msie){
		$("#remaks[maxlength]").bind('input propertychange', function() {
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    });
		/*$("#remarkCorrection[maxlength]").bind('input propertychange', function() {
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    });*/
	}
	
	$('#showPayments').attr('disabled', true);
//	if ((('${mode}'=='entry')&&('${confirming}'!='true')) || (('${mode}'=='edit')&&('${confirming}'!='true'))) { 
		if ('${deposito?.interestFrequency?.lookupId}' == '${intPaymentFreqMonthly}'){
			$("#tabs").tabs("enable",2);
			$('#paymentFreq2').attr('checked', true);
			if ('${confirming}'=='false')
				$('#showPayments').attr('disabled', false);
		}
//	}
	
	$('input[name=paymentFreq]').change(function(){
		if ($('#paymentFreq1').attr('checked')){
			$('#paymentFreqHidden').val($('#paymentFreq1').val());
			$('#showPayments').attr('disabled', true);
			$("#tabs").tabs("disable",2);
			$("#totalCoupon").val('');
		}else{
			$('#paymentFreqHidden').val($('#paymentFreq2').val());
			$('#showPayments').attr('disabled', false);
			$("#tabs").tabs("enable",2);
		}
	});

	$('#showCharges').click(function() {
		$('#tabs').tabs('select', 1);
		tabChargeVisited = true;
		$('#chargeDone').css('display', '');
		var firstElement = $('#chargeList input:text').first();
		firstElement.focus();
	});
	
	$('#chargeDone').click(function() {
		$('#chargeDone').css('display', 'none');
		$('#tabs').tabs('select', 0);
		if ($('#interestRate').attr('disabled')) {
			$('#tax').focus();
		} else {
			$('#interestRate').focus();
		}
	});
	
	$('#next1').click(function() {
		$('#tabs').tabs('select', 1);
	});
	
	$('#next2').click(function() {
		
	});
	
	$('#next3').click(function() {
		
	});
	
	$('#next4').click(function() {
		
	});
	
	$('#prev2').click(function() {
		$('#tabs').tabs('select', 0);
	});
	
	$('#prev3').click(function() {
		
	});
	
	$('#prev4').click(function() {
		
	});
	
	$('#prev5').click(function() {
		
	});
	
	

	// PLACEMENT DATE
	$('#depositoDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends
	});
	
	// MATURITY DATE
	$('#maturityDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends
	});
	
	 
	 transactionGrid = $('#transactionList').dataTable({
		aoColumnDefs: [{asSorting:["asc"], aTargets:[1]}, {asSorting:[""], aTargets:[1,2,3,4,5]}],
		bAutoWidth:false,
		bDestroy:true,
		bFilter:false,
		bInfo:false,
		bJQueryUI:true,
		bPaginate: false			
	 });
	 
	 rolloverGrid = $('#rolloverList').dataTable({
		aoColumnDefs: [{asSorting:["asc"], aTargets:[0]}, {asSorting:[""], aTargets:[1,2,3,4]}],
		bAutoWidth:false,
		bDestroy:true,
		bFilter:false,
		bInfo:false,
		bJQueryUI:true,
		bPaginate: false			
	 });
	 
	 $('#transactionList tbody tr td').die('click');
	 
	 $('#transactionList tbody tr td').live('click', function(){
		var elem = $(this);
		var row = elem.parents('tr');
		var depositoKey = '${deposito?.depositoKey}';
		var transactionType = jQuery.trim($(row).find("td[id*='transactionType']").html());
		var status = jQuery.trim($(row).find("td[id*='status']").html());
		var keytrans = jQuery.trim($(row).find("td[id*='keytrans']").html());
		
		if (transactionType == '${depositoTemplatePlacementDescription}') {
			if ((status == '${open}') || (status == '${reject}') || (status == '${approved}')) {
				location.href = '@{DepositoPlacements.view}?id=' + keytrans + '&from=${depositoInquiryOriginated}';
				return false;
			} else if (status == '${cancelled}') {
				location.href = '@{CancelDepositoPlacements.view}?id=' + keytrans + '&from=${depositoInquiryOriginated}';
				return false;
			}
		} else if (transactionType == '${depositoTemplateBreakDescription}') {
			if ((status == '${open}') || (status == '${reject}') || (status == '${approved}')) {
				location.href = '@{DepositoBreaks.view}?id=' + keytrans + '&from=${depositoInquiryOriginated}';
				return false;
			} else if (status == '${cancelled}') {
				location.href = '@{CancelDepositoBreaks.view}?id=' + keytrans + '&from=${depositoInquiryOriginated}';
				return false;
			}
		} else if (transactionType == '${depositoTemplateFullRedeemDescription}') {
			if (status == '${cancelled}') {
				location.href = '@{CancelDepositoBreaks.view}?id=' + keytrans + '&from=${depositoInquiryFullRedeem}';
				return false;
			} else if (status == '${approved}') {
				location.href = '@{DepositoBreaks.view}?id=' + keytrans + '&from=${depositoInquiryFullRedeem}';
				return false;
			}
		}
		
		
	 });
}); // end function()

function disableCharge() {
	$('#chargeList tbody input').each(function() {
		$(this).attr('disabled', 'disabled');
	});
	$('#addCharge').button({disabled: true});
	reloadCharge = true;
}

function clearSecurity(){
	$('#securityId').removeClass("fieldError");
	$('#securityId').val("");
	$('#securityKey').val("");
	$('#securityDesc').val("");
	$('#h_securityDesc').val("");
	$('#currencyCode').val('');
	$('#currencyCodeHidden').val('');
	
	$('#issuerCode').val('');
	$('#issuerCodeHide').val('');
	$('#issuerCodeKey').val('');
	$('#issuerCodeDesc').val('');
	$('#issuerCodeDescHide').val('');
	
	$('#accrualBase').val('');
	$('#accrualBaseHidden').val('');
	$('#yearBase').val('');
	$('#yearBaseHidden').val('');
	
	$('#frequency').val('');
	$('#frequencyId').val('');
	
	$('#minTrxQuantity').val('');
	$('#isScriptHidden').val('');
	$('#classification').val('');
}

function getCharges() {
	$.post('@{loadCharges}', 
		{ 
			'custodyAccountKey':$('#accountKey').val(),
			'securityClass':$('#securityClass').val(),
			'securityType':$('#securityType').val(),
			'transactionTemplateKey':$('#transactionTemplateKey').val(),
			'securityKey':$('#securityKey').val(),
			'quantity':$('#nominalStripped').val(),
			'nominal':$('#nominalStripped').val()
		}, 
		function(response, status, xhr) {
    		checkRedirect(response);
			if (status == 'error') {
				// Error handling here
			} else {
				var charges = response;
				chargeGrid = $('#chargeList').dataTable({
					aaData:response,
					aoColumnDefs: [{bSortable:false,aTargets:[1,2,3,4,5,6]}],
					aoColumns:
						[
						 	{ 
						 		asSorting:["asc"], mDataProp: "chargeMaster.chargeCode"
						 	},
						 	{ 
						 		fnRender: function(obj) {
						 			var chargeValue = 0;
						 			if (!isNaN(obj.aData.chargeValue)) 
						 				chargeValue = $('#dummy').autoNumericSet(obj.aData.chargeValue).val();
						 			var controls;
						 				controls = '<input type="text" id="chargeValue[' + obj.iDataRow + ']" value="' + chargeValue + '" class="rgNumeric mask" size="15"/>';
						 				controls += '<input type="hidden" id="hiddenChargeValue[' + obj.iDataRow + ']" value="' + chargeValue + '" class="rgNumeric mask" size="15" />';
						 				controls += '<input type="hidden" id="chargeValueStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeValue" value="' + obj.aData.chargeValue + '" />';
						 				controls += '<input type="hidden" id="hiddenChargeValueStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeValue" value="' + obj.aData.chargeValue + '" />';
						 			return controls;
						 		} 
						 	},
						 	{ 
						 		fnRender: function(obj) {
						 			var taxAmount = 0;
						 			if (obj.aData.taxAmount && !isNaN(obj.aData.taxAmount)) 
						 				taxAmount = $('#dummy').autoNumericSet(obj.aData.taxAmount, {vMin:'-999999999999.'}).val();
						 			var controls;
						 				controls = '<input type="text" id="taxAmount[' + obj.iDataRow + ']" value="' + taxAmount + '" class="rgNumeric mask" size="15" />';
						 				controls += '<input type="hidden" id="hiddenTaxAmount[' + obj.iDataRow + ']" value="' + taxAmount + '" class="rgNumeric mask" size="15" />';
						 				controls += '<input type="hidden" id="taxAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].taxAmount" value="' + obj.aData.taxAmount + '" />';
						 				controls += '<input type="hidden" id="hiddenTaxAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].taxAmount" value="' + obj.aData.taxAmount + '" />';
						 			return controls;
						 		}
						 	},
						 	{ 
						 		fnRender: function(obj) {
						 			var chargeNetAmt = 0;
						 			if (obj.aData.chargeNetAmount && !isNaN(obj.aData.chargeNetAmount)) 
						 				chargeNetAmt = $('#dummy').autoNumericSet(obj.aData.chargeNetAmount).val();
						 			var controls;
						 			controls = '<input type="text" id="chargeNetAmount[' + obj.iDataRow + ']" value="' +chargeNetAmt+ '" size="15" class="rgNumeric mask" disabled="disabled"/>';
						 			controls += '<input type="hidden" id="hiddenChargeNetAmount[' + obj.iDataRow + ']" value="' + chargeNetAmt + '" class="rgNumeric mask" size="15" />';
					 				controls += '<input type="hidden" id="chargeNetAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeNetAmount" value="' + obj.aData.chargeNetAmount + '" />';
					 				controls += '<input type="hidden" id="hiddenChargeNetAmountStripped[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeNetAmount" value="' + obj.aData.chargeNetAmount + '" />';
						 			return controls;
						 		}
						 	},
						 	
						 	{ 
						 		fnRender: function(obj) {
						 			var controls;
						 			if (obj.aData.chargeFrequency == '${chargeFreqT}') {
						 				if ($('#classificationId').val()=='${classificationTrd}'){
						 					controls = '<input type="checkbox" name="charges[' + obj.iDataRow + '].chargeCapitalized" value="true" />';
						 				} else {
						 					controls = '<input type="checkbox" name="charges[' + obj.iDataRow + '].chargeCapitalized" value="true" checked="checked" />';
						 				}
						 			} else {
						 				controls = '<input type="checkbox" name="charges[' + obj.iDataRow + '].chargeCapitalized" value="true" disabled="disabled" />';
						 			}
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].chargeCapitalized" value="false" />';
						 			return controls;
						 		}
						 	},
						 	{
						 		fnRender: function(obj) {
						 			var controls;
						 			controls = '<input type="checkbox" name="charges[' + obj.iDataRow + '].chargeWaived" value="true" ' + (obj.aData.chargeWaived ? 'checked="checked"' : '') + ' />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].chargeWaived" value="false" />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].chargeMaster.chargeKey" value="' + obj.aData.chargeMaster.chargeKey + '" />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].chargeMaster.chargeCode" value="' + obj.aData.chargeMaster.chargeCode + '" />';
						 			controls += '<input type="hidden" id="chargeType[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeMaster.chargeType.lookupId" value="' + obj.aData.chargeMaster.chargeType.lookupId + '" />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].taxMaster.taxKey" value="' + (obj.aData.taxMaster ? obj.aData.taxMaster.taxKey : '') + '" />';
						 			controls += '<input type="hidden" id="taxRate[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].taxMaster.taxRate" value="' + (obj.aData.taxMaster ? obj.aData.taxMaster.taxRate : '') + '" />';
						 			controls += '<input type="hidden" id="freq[' + obj.iDataRow + ']" name="charges[' + obj.iDataRow + '].chargeFrequency" value="' + obj.aData.chargeFrequency + '" />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].id.transactionKey" value="' + (obj.aData.id ? obj.aData.id.transactionKey : '') + '" />';
						 			controls += '<input type="hidden" name="charges[' + obj.iDataRow + '].id.rowNumber" value="' + (obj.aData.id ? obj.aData.id.rowNumber : '') + '" />';
						 			return controls;
						 		}
						 	}
						],
					bAutoWidth:false,
					bDestroy:true,
					bFilter:false,
					bInfo:false,
					bJQueryUI:true,
					bPaginate: false			
				});	
				$('#addCharge').button({disabled: false});
				$('#chargeList tbody .mask').autoNumeric({vMin:'-999999999999.'});
				reloadCharge = false;
				calculate();
			}
		});
	
	$('#errGlobal').html("");
}

function getPaymentSchedules() {
	$.post('@{populatePaymentSchedule}',
		{
			'placementDate':$('#depositoDate').val(),
			'maturityDate':$('#maturityDate').val(),
			'nominal':$('#nominalStripped').val(),
			'interestRate':$('#interestRateStripped').val(),
			'accrualBase':$('#accrualBase').val(),
			'yearBase':$('#yearBase').val(),
			'totalCoupon':$('#totalCoupon').val(),
			'considerHoliday':$('considerHoliday').val(),
			'freqSecurity':$('#frequency').val()
		},
		function(response, status, xhr) {
    		checkRedirect(response);
			if (status == 'error') {
				// Error handling here
			} else {
				var schedules = response;
				scheduleGrid = $('#gridCoupon').dataTable({
					aaData:response,
					aoColumnDefs: [{bSortable:false,aTargets:[0,1,2,3,4,5]}],
					aoColumns:
						[
							{ 
								asSorting:["asc"], mDataProp: "id.paymentNo"
							},
							{
								fnRender: function(obj) {
						 			var controls;
					 				controls = '<input type="text" id="startDate[' + obj.iDataRow + ']" value="' + obj.aData.strStartDate + '" class="calendar" disabled="disabled" />';
					 				controls += '<input type="hidden" id="startDateHidden[' + obj.iDataRow + ']" name="schedules['+obj.iDataRow+'].strStartDate" value="' +  obj.aData.strStartDate + '" class="calendar" />';
						 			return controls;
						 		}
							},
							{
								fnRender: function(obj) {
						 			var controls;
					 				controls = '<input type="text" id="endDate[' + obj.iDataRow + ']" value="' + obj.aData.strEndDate + '"  class="calendar hasDatePicker" />';
					 				controls += '<input type="hidden" id="endDateHidden[' + obj.iDataRow + ']" name="schedules['+obj.iDataRow+'].strEndDate" value="' +  obj.aData.strEndDate + '" />';
						 			return controls;
						 		}
							},
							{
								fnRender: function(obj) {
						 			var controls;
					 				controls = '<input type="text" id="days[' + obj.iDataRow + ']" value="' + obj.aData.days + '" class="numeric" disabled="disabled" width="50px" />';
					 				controls += '<input type="hidden" id="daysHidden[' + obj.iDataRow + ']" name="schedules['+obj.iDataRow+'].days" value="' +  obj.aData.days + '"  />';
						 			return controls;
						 		}
							},
							{
								fnRender: function(obj) {
									var grossInterest = 0;
									if (obj.aData.grossInterest && !isNaN(obj.aData.grossInterest))
										grossInterest = $('#dummy').autoNumericSet(obj.aData.grossInterest).val();
						 			var controls;
					 				controls = '<input type="text" id="grossAmount[' + obj.iDataRow + ']" value="' + grossInterest + '" class="numeric" disabled="disabled" />';
					 				controls += '<input type="hidden" id="grossAmountHidden[' + obj.iDataRow + ']" name="schedules['+obj.iDataRow+'].grossInterest" value="' +  obj.aData.grossInterest + '" />';
						 			return controls;
						 		}
							},
							{
								fnRender: function(obj) {
						 			var controls;
					 				controls = '<input type="text" id="paymentDate[' + obj.iDataRow + ']" value="' + obj.aData.strPaymentDate + '" class="calendar" disabled="disabled" />';
					 				controls += '<input type="hidden" id="paymentDateHidden[' + obj.iDataRow + ']" name="schedules['+obj.iDataRow+'].strPaymentDate" value="' +  obj.aData.paymentDate + '" class="calendar" />';
					 				controls += '<input type="hidden" id="paymentNo[' + obj.iDataRow + ']" name="schedules['+obj.iDataRow+'].id.paymentNo" value="' +  obj.aData.id.paymentNo + '"  />';
						 			return controls;
						 		}
							}
						 ],
						bAutoWidth:false,
						bDestroy:true,
						bFilter:false,
						bInfo:false,
						bJQueryUI:true,
						bPaginate: false
				});
			}
		});
}


function calculate(){
	var transaction = new Transaction();
	transaction.securityType = $('#securityType').val();
	transaction.settlementDate = $('#depositoDate').datepicker('getDate');
	transaction.quantity = $('#nominalStripped').val();
	transaction.price = 1;
	transaction.priceUnit = 1;
	transaction.netAmount = $('#nominalStripped').val();
	transaction.lastPaymentDate = new Date($('#depositoDate').datepicker('getDate'));
	transaction.nextPaymentDate = new Date($("#maturityDate").datepicker('getDate'));
	transaction.settlementDate = new Date($("#maturityDate").datepicker('getDate'));
	transaction.interestRate = $('#interestRateStripped').val();
	transaction.frequency = transaction.decodeFrequency($('#frequency').val());
	transaction.accrualBase = transaction.decodeCalculationBase($('#accrualBase').val());
	transaction.yearBase = transaction.decodeCalculationBase($('#yearBase').val());
	transaction.isFraction = $('#isFraction').val();
	transaction.fractionRatioSource = $('#fractionRatioSource').val();
	transaction.fractionRatioTarget = $('#fractionRatioTarget').val();
	transaction.capitalGainTax = $('#taxRate').val();
	
	var totalCharges = 0;
	var sumOfChargeNetAmount = 0;
	var sumOfTax = 0;
	var nodes = chargeGrid.fnGetNodes();
	for ( var i = 0; i < nodes.length; i++) {
		var node = $(nodes[i]);
		var chargeValue = node.find('[name$="chargeValue"]').val();
		var chargeTax = node.find('[name$="taxAmount"]').val();
		var chargeNet = node.find('#chargeNetAmount');
		var chargeFrequency = node.find('[name$="chargeFrequency"]').val();
//		console.debug('CHARGE VALUE = ' +chargeValue);
//		console.debug('CHARGE TAX = ' +chargeTax);
		var chargeNetAmount = 0;
		var totalChargeValue = 0;
		var totalTaxCharges = 0;
		
		if (!isNaN(chargeValue)) {
			totalChargeValue += Number(chargeValue);
			chargeNetAmount += Number(chargeValue); 
		}
		if (!isNaN(chargeTax)) {
			chargeNetAmount += Number(chargeTax);
			totalTaxCharges += Number(chargeTax);
		}
//		console.debug('CHARGE NET AMOUNT = ' +chargeNetAmount);
//		console.debug('Total Charges Tax = ' +)
	
		$(chargeNet).autoNumericSet(chargeNetAmount, {vMin:'-999999999999.'});
		
		if (chargeFrequency == '${chargeFreqT}') {
			//totalCharges += chargeNetAmount;
			totalCharges += totalChargeValue;
			sumOfChargeNetAmount += chargeNetAmount;
			sumOfTax += totalTaxCharges;
		}
	}
	transaction.totalCharges = totalCharges;
	transaction.sumOfChargeNetAmount = sumOfChargeNetAmount;
	console.debug("TOTAL CHARGES = "+totalCharges);
	$('#totalCharges').autoNumericSet(totalCharges, {vMin:'-999999999999.'});
	$('#totalChargesStripped').val(totalCharges);	
	
	transaction.calculateAccruedInterest();
	transaction.calculate();
	
	$('#accruedDays').autoNumericSet(transaction.accruedDays);
	$('#accruedDaysStripped').val(transaction.accruedDays);
	
	$('#accruedInterest').autoNumericSet(transaction.accruedInterest);
	$('#accruedInterestStripped').val(transaction.accruedInterest);
	
	$('#settlementAmount').autoNumericSet(transaction.settlementAmount);
	$('#settlementAmountStripped').val(transaction.settlementAmount);
	
}

function gridCoupons(){
    $('.calendar').datepicker();
    gridCouponDataTables();
    $('#gridCoupon').removeAttr('style');

//    onChangeNextCpnDate();
    
    $('input.numerics').live('blur', function() {
        var el = $(this);
        var id = this.id;
        var stripCoupon= $("input[id='" + id +"StrippedCoupon']");
        stripCoupon.val($.fn.autoNumeric.Strip(id));
    });
    //$('input.numerics').autoNumeric();
}

function gridCouponDataTables() {
    var gridCoupon = $('#gridCoupon').dataTable({
        aoColumns: [
                    //null,
//                    {bVisible:false},
//                    {sType: "numeric" },
                     null,
                     null,
                     null,
                     null,
                     null,
                     null
                   ],
                   
        aaSorting:[[1,'asc']],
        //aoColumnDefs: [{asSorting:[""], aTargets:[8]}, {asSorting:[""], aTargets:[0,1,2,3,4,5,6,7]}],
        //sScrollY :"360px",
        //bScrollCollapse : true,
        bAutoWidth: false,  
        bJQueryUI:true,
        bLengthChange:false,
        bFilter:false,
        bPaginate:false,
        bRetrieve:true,
        //bSort:false,
        //iDisplayLength:10,
        bInfo:false
    });
}