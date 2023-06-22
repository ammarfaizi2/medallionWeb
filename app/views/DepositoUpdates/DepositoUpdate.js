var scheduleGrid;
var reloadSchedule = true;
$(function(){
	
	var messageErrorMaturityDate = "Can not less than Maturity Date";
	$('#tabs').tabs();
	$('#save, #clear, #cancel, #confirm, #back, #generateButton').button();
	
	if (!$('#changeAccruedInterest').attr("checked")) 
		$('#newInterest').val('');
	
	if ($('#paymentFreqMaturity').val()=='${paymentFreqMonthly}'){
		$('.onMonthly').css('display', 'block');
		$('.onMaturity').css('display', 'block');
		$("#tabs").tabs("enable",1);
	} else {
		$('.onMonthly').css('display', 'none');
		$('.onMaturity').css('display', 'block');
		$("#tabs").tabs("disable",1);
	}
	
	if ('${mode}'=='edit' && '${confirming}'!='true'){
		if (!$('#changeMaturityDate').is(':checked')){
			$('td[id=tdMaturityDate] label span').html('');
			$('#newMaturityDate').attr('disabled', 'disabled');
		} else {
			$('td[id=tdMaturityDate] label span').html(' *');
			$('#newMaturityDate').attr('disabled', false);
		}
		
		if (!$('#changeInterestRate').is(':checked')){
			$('td[id=tdInterestRate] label span').html('');
			$('#newInterestRate').attr('disabled', 'disabled');
		} else {
			$('td[id=tdInterestRate] label span').html(' *');
			$('#newInterestRate').attr('disabled', false);
		}
		
		if (!$('#changeAccruedInterest').is(':checked')){
			$('td[id=tdAccruedInterest] label span').html('');
			$('#newInterest').attr('disabled', 'disabled');
		} else {
			$('td[id=tdAccruedInterest] label span').html(' *');
			$('#newInterest').attr('disabled', false);
		}
		
		if (!$('#changeMaturityIns').is(':checked')){
			$('td[id=tdMaturityInst] label span').html('');
			$('#newMaturityIns').attr('disabled', 'disabled');
		} else {
			$('td[id=tdMaturityInst] label span').html('');
			$('#newMaturityIns').attr('disabled', false);
		}
		
		if (($('#newMaturityIns').val()!='')&&($('#newMaturityIns').val()=='${maturityInsFullRedeem}')){
			$('#nextMaturityDate').attr('disabled', 'disabled');
			$('#nextInterestRate').attr('disabled', 'disabled');
			$('p[id=pNextMaturityDate] label span').html('');
			$('p[id=pNextInterestRate] label span').html('');
		} 
		
		if (($('#newMaturityIns').val()=='')&&($('#oldMaturityIns').val()=='${maturityInsFullRedeem}')){
			$('#nextMaturityDate').attr('disabled', 'disabled');
			$('#nextInterestRate').attr('disabled', 'disabled');
			$('p[id=pNextMaturityDate] label span').html('');
			$('p[id=pNextInterestRate] label span').html('');
		}
		
	}
	
	$('#changeMaturityDate').change(function(){
		if (!$(this).is(':checked')){
			$('td[id=tdMaturityDate] label span').html('');
			$('#newMaturityDate').attr('disabled', 'disabled');
			$('#newMaturityDate').val('');
			$('#newMaturityDate').removeClass('fieldError');
			$('#newMaturityDateError').html('');
			if ($('#nextMaturityDate').val()!=''){
				var date1 = new Date($('#oldMaturityDate').datepicker('getDate'));
				var date2 = new Date($('#nextMaturityDate').datepicker('getDate'));
				if (date1.getTime() > date2.getTime()){
					$('#nextMaturityDate').addClass('fieldError');
					$('#nextMaturityDateError').html(messageErrorMaturityDate);
				} else {
					$('#nextMaturityDate').removeClass('fieldError');
					$('#nextMaturityDateError').html('');
				}
			}
		} else {
			$('td[id=tdMaturityDate] label span').html(' *');
			$('#newMaturityDate').attr('disabled', false);
		}
		if ($('#paymentFreqMaturity').val()!='${paymentFreqMonthly}'){
			calculate();
		} else {
			calculateTotalCoupon();
			reloadSchedule = true;
		}
	});
	
	$('#changeInterestRate').change(function(){
		if (!$(this).is(':checked')){
			$('td[id=tdInterestRate] label span').html('');
			$('#newInterestRate').attr('disabled', 'disabled');
			$('#newInterestRate').val('');
			$('#newInterestRateStripped').val('');
			$('#newInterestRate').removeClass('fieldError');
			$('#newInterestRateError').html('');
		} else {
			$('td[id=tdInterestRate] label span').html(' *');
			$('#newInterestRate').attr('disabled', false);
		}
		/*if ($('#paymentFreqMaturity').val()!='${paymentFreqMonthly}'){
			calculate();
		} else {
			calculateTotalCoupon();
			reloadSchedule = true;
		}*/
	});
	
	$('#changeAccruedInterest').change(function(){
		if (!$(this).is(':checked')){
			$('td[id=tdAccruedInterest] label span').html('');
			$('#newInterest').attr('disabled', 'disabled');
			$('#newInterest').val('');
			$('#newInterestStripped').val('');
			$('#newInterestError').removeClass('fieldError');
			$('#newInterestError').html('');
		} else {
			$('td[id=tdAccruedInterest] label span').html(' *');
			$('#newInterest').attr('disabled', false);
		}
		/*if ($('#paymentFreqMaturity').val()!='${paymentFreqMonthly}'){
			calculate();
		} else {
			calculateTotalCoupon();
			reloadSchedule = true;
		}*/
	});
	
	
	$('#changeMaturityIns').change(function(){
		if (!$(this).is(':checked')){
			$('td[id=tdMaturityInst] label span').html('');
			$('#newMaturityIns').attr('disabled', 'disabled');
			$('#newMaturityIns').removeClass('fieldError');
			$('#newMaturityInsError').html('');
			$('#newMaturityIns').val('');
			
			$("#newMaturityIns option").remove();
			$('#tmpMaturityIns option').each(function(){
			     $("#newMaturityIns").append('<option value="'+this.value+'">'+this.text+'</option>');
			});
			
			if ($('#oldMaturityIns').val()=='${maturityInsFullRedeem}'){
				$('#nextMaturityDate').attr('disabled', 'disabled');
				$('#nextInterestRate').attr('disabled', 'disabled');
				$('#nextMaturityDate').val('');
				$('#nextInterestRate').val('');
				$('#nextInterestRateStripped').val('');
				$('p[id=pNextMaturityDate] label span').html('');
				$('p[id=pNextInterestRate] label span').html('');
				$('#nextMaturityDate').removeClass('fieldError');
				$('#nextMaturityDateError').html('');
			} else {
				$('#nextMaturityDate').attr('disabled', false);
				$('#nextInterestRate').attr('disabled', false);
				$('p[id=pNextMaturityDate] label span').html(' *');
				$('p[id=pNextInterestRate] label span').html(' *');
			}
			var ch = $('#chargeableTam').val();
			if(ch == false || ch == 'false') {
				$("#chargeAble2").attr('checked', true);
				$('input[name="deposito.chargeable"]').val($('#chargeableTam').val());
			} else if(ch == true || ch == 'true') {
				$("#chargeAble1").attr('checked',$('#chargeableTam').val());
			}
			$('input[name="deposito.chargeable"]').val($('#chargeableTam').val());
		} else {
			if ($('#paymentFreqMaturity').val()=='${paymentFreqMonthly}'){
				$("#newMaturityIns option").remove();
				$("#newMaturityIns").append('<option value="${maturityInsFullARO}">${maturityInsFullARODesc}</option>');
				$("#newMaturityIns").val('${maturityInsFullARO}');	
				$("#newMaturityIns").trigger('change');
				
				$('#nextMaturityDate').attr('disabled', false);
				$("#nextMaturityDate").val($("#maturityDateHidden").val());
				$("#nextMaturityDate").blur();
				$("#nextMaturityDate").attr('disabled', 'disabled');
			}
			
			$('td[id=tdMaturityInst] label span').html(' *');
			$('#newMaturityIns').attr('disabled', false);
		}
	});
	
	$('#showPayments').click(function() {
		$('#tabs').tabs('select', 1);
	});
	
	if ($('#paymentFreqMaturity').val()=='${paymentFreqMonthly}') {
		$('#changeAccruedInterest').attr('disabled', true);
		$('#changeMaturityDate').attr('disabled', true);
		$('#changeInterestRate').attr('disabled', true);
		$('#generateButton').hide();
		$('#totalCoupon').attr('disabled', true);
		$('#considerHoliday').attr('disabled', true);
		$("#nextMaturityDate").attr('disabled', 'disabled');
	}
	
	$('#newMaturityDate').change(function(){
		var date1 = new Date($('#oldMaturityDate').datepicker('getDate'));
		var date2 = new Date($(this).datepicker('getDate'));
		var date3 = new Date($('#effectiveDate').datepicker('getDate'));
		var date4 = new Date($('#lastPaymentDate').datepicker('getDate'));
		var date5 = new Date($('#nextMaturityDate').datepicker('getDate'));
			if ($(this).val()!=''){
				if (date2.getTime() == date1.getTime()){
					$(this).addClass('fieldError');
					$('#newMaturityDateError').html("Can not equals to Current Maturity Date");
				} else if ($('#paymentFreqMaturity').val()!='${paymentFreqMonthly}'){
					if (date2.getTime() <= date3.getTime()) {
						$(this).addClass('fieldError');
						$('#newMaturityDateError').html("Can not less or equals to Effective Date");
					} else if (($('#nextMaturityDate').val()!='')&&(date2.getTime() > date5.getTime())){
						$('#nextMaturityDate').addClass('fieldError');
						$('#nextMaturityDateError').html(messageErrorMaturityDate);
					} else {
						$(this).removeClass('fieldError');
						$('#newMaturityDateError').html("");
						$('#nextMaturityDate').removeClass('fieldError');
						$('#nextMaturityDateError').html("");
						calculate();
					}
				} else {
					if (date2.getTime() <= date4.getTime()) {
						$(this).addClass('fieldError');
						$('#newMaturityDateError').html("Can not less or equals to Last Payment Date");
					} else {
						$(this).removeClass('fieldError');
						$('#newMaturityDateError').html("");
						calculateTotalCoupon();
						reloadSchedule = true;
					}
				}
				
			}
			
	});
	
	$('#newInterestRate').blur(function(){
		var oldInterestRate = $('#oldInterestRate').val();
		var newInterestRate = $(this).val();
		if ($(this).val()!=''){
			if (newInterestRate==oldInterestRate){
				$(this).addClass('fieldError');
				$('#newInterestRateError').html('Can not equals to Current Interest Rate');
			} else {
				$(this).removeClass('fieldError');
				$('#newInterestRateError').html('');
				if ($('#paymentFreqMaturity').val()!='${paymentFreqMonthly}'){
					calculate();
				} else {
					reloadSchedule = true;
				}
			}
		}
	});
	
	$('#newInterest').blur(function(){
		var oldInterest = $('#interest').val();
		var newInterest = $(this).val();
		if ($(this).val()!=''){
			if (oldInterest==newInterest){
				$(this).addClass('fieldError');
				$('#newInterestError').html('Can not equals to Current Interest');
			} else {
				$(this).removeClass('fieldError');
				$('#newInterestError').html('');
			}
		}
	});
	
	$('#newMaturityIns').change(function(){
		if ($(this).val()!=''){
			if ($(this).val()==$('#oldMaturityIns').val()){
				$(this).addClass('fieldError');
				$('#newMaturityInsError').html('Can not equals to Current Maturity Instruction');
			} else {
				if ($(this).val()!='${maturityInsFullRedeem}'){
					$('#nextMaturityDate').attr('disabled', false);
					$('#nextInterestRate').attr('disabled', false);
					$('p[id=pNextMaturityDate] label span').html(' *');
					$('p[id=pNextInterestRate] label span').html(' *');
					$("#chargeAble2").attr('checked',true);
					$('input[name="deposito.chargeable"]').val(false);
				} else {
					$('#nextMaturityDate').attr('disabled', 'disabled');
					$('#nextInterestRate').attr('disabled', 'disabled');
					$('#nextMaturityDate').val('');
					$('#nextInterestRate').val('');
					$('#nextInterestRateStripped').val('');
					$('p[id=pNextMaturityDate] label span').html('');
					$('p[id=pNextInterestRate] label span').html('');
					$('#nextMaturityDate').removeClass('fieldError');
					$('#nextMaturityDateError').html('');
					$("#chargeAble1").attr('checked',true);
					$('input[name="deposito.chargeable"]').val(true);
				}
				$(this).removeClass('fieldError');
				$('#newMaturityInsError').html('');
			}
		}
	});
	
	$('#nextMaturityDate').change(function(){
		var date1 = null;
		if ($('#newMaturityDate').val()!='')
			date1 = new Date($('#newMaturityDate').datepicker('getDate'));
		else 
			date1 = new Date($('#oldMaturityDate').datepicker('getDate'));
		var date2 = new Date($(this).datepicker('getDate'));
		
		console.log("Date 1 = " + date1);
		console.log("Date 2 = " + date2);
		if ($(this).val()!=''){
			if (date2.getTime() <= date1.getTime()){
				$(this).addClass('fieldError');
				$('#nextMaturityDateError').html(messageErrorMaturityDate);
			} else {
				$(this).removeClass('fieldError');
				$('#nextMaturityDateError').html('');
			}
		}
		
	});
	$('#chargeAble1').add($('#chargeAble2')).click(function(){
		if ($('#chargeAble1').is(':checked')){
			$('input[name="deposito.chargeable"]').val(true);
		}
		
		if ($('#chargeAble2').is(':checked')){
			$('input[name="deposito.chargeable"]').val(false);
		}
	});
	$('#considerHoliday').val('${deposito?.considerHoliday}');
	$('#considerHoliday').change(function(){
		if ($(this).is(":checked")){
			$(this).val(true);
		} else {
			$(this).val(false);
		}
	});
	
	$("#generateButton").click(function() {
		 var maturityDate = null;
		 var interestRate = null;
		 
		 $('#totalCouponHidden').val($('#totalCoupon').val());
		 $('#newMaturityDate').blur()
		 if ($('#newMaturityDate').val()!=''){
			 maturityDate = $('#newMaturityDate').val();
		 } else {
			 maturityDate = $('#oldMaturityDate').val();
		 }
		 if ($('#newInterestRate').val()!=''){
			 interestRate = $('#newInterestRateStripped').val();
		 } else {
			 interestRate = $('#oldInterestRateStripped').val();
		 }
		 
		 if (($('#placementDate').val()=='')||(maturityDate=='')||(interestRate=='')||
			 ($('#nominalStripped').val()=='')||($('#accrualBase').val()=='')||($('#yearBase').val()=='')||
			 (($('#totalCoupon').val()=='')&&(!$('#totalCoupon').hasClass('fieldError')))||($('#securityKey').val()=='')) {
			 $('#generateButtonError').html('Please check Placement Date, Security Code, Maturity Date, Nominal, Interest Rate(gross), Accrual Type, No. of int. payment. Make sure all of them are not empty!');
			 return false;
		 }
		 $('#generateButtonError').html('');
		 $.post('@{populatePaymentSchedule()}', 
					{ 
			 			'depositoKey':$('#depositoKey').val(),
					 	'placementDate':$('#placementDate').val(),
						'maturityDate':maturityDate,
						'nominal':$('#nominalStripped').val(),
						'interestRate':interestRate,
						'accrualBase':$('#accrualBase').val(),
						'yearBase':$('#yearBase').val(),
						'totalCoupon':$('#totalCoupon').val(),
						'considerHoliday':$('#considerHoliday').val(),
						'freqSecurity':$('#frequency').val(),
						'totalPaid':$('#totalPaid').val()
					}, function(data) {

			    		checkRedirect(data);
						$('#paymentSchedule').html(data);
						reloadSchedule = false;
					});
    });
	
	$('#bankAccount').lookup({
		list : '@{Pick.bankAccounts()}?domain=CUSTOMER',
		get:{
			url: '@{Pick.PickbankAccountForSettlementAccount()}?domain=CUSTOMER',
			success: function(data){
				if (data) {
					if ($("#accountNo").val() != ""){
						$('#bankAccount').removeClass('fieldError');
						var codeSplit = $('#bankAccount').val().split("");
						$('#bankAccount').val(codeSplit[0]);
						$('#bankAccountKey').val(data.code);
						$('#bankAccountName').val(data.description);
						$('#h_bankAccountName').val(data.description);
						$('#bankCode').val(data.bankCode.thirdPartyCode);
						$('#h_bankCode').val(data.bankCode.thirdPartyCode);
						$('#bankCodeKey').val(data.bankCode.thirdPartyKey);
						$('#bankCodeName').val(data.bankCode.thirdPartyName);
						$('#h_bankCodeName').val(data.bankCode.thirdPartyName);
						$('#bankAccountBeneficiary').val(data.customer.customerName);
						$('#h_bankAccountBeneficiary').val(data.customer.customerName);
						$('#bankAccountCurrency').val(data.currency.currencyCode);
						$('#h_bankAccountCurrency').val(data.currency.currencyCode);
					} else {
						$('#bankAccount').addClass('fieldError');
						$('#bankAccountName').val('NOT FOUND');
						$('#bankAccount').val('');
						$('#bankAccountKey').val('');
						$('#h_bankAccountName').val('');
						$('#bankCode').val('');
						$('#h_bankCode').val('');
						$('#bankCodeKey').val('');
						$('#bankCodeName').val('NOT FOUND');
						$('#h_bankCodeName').val('');
						$('#bankAccountBeneficiary').val('');
						$('#h_bankAccountBeneficiary').val('');
						$('#bankAccountCurrency').val('');
						$('#h_bankAccountCurrency').val('');
					}					
				}	
			},
			error: function(data) {
				$('#bankAccount').addClass('fieldError');
				$('#bankAccountName').val('NOT FOUND');
				$('#bankAccount').val('');
				$('#bankAccountKey').val('');
				$('#h_bankAccountName').val('');
				$('#bankCode').val('');
				$('#h_bankCode').val('');
				$('#bankCodeKey').val('');
				$('#bankCodeName').val('NOT FOUND');
				$('#h_bankCodeName').val('');
				$('#bankAccountBeneficiary').val('');
				$('#h_bankAccountBeneficiary').val('');
				$('#bankAccountCurrency').val('');
				$('#h_bankAccountCurrency').val('');
			}
		},
		key : $('#bankAccountKey'),
		description : $('#bankAccountName'),
		filter : $('#accountKey'),
		help : $('#bankAccountHelp')
	});
	
	$('#bankAccount').change(function() {
		if ($('#bankAccount').val() == "") {
			$('#bankAccount').val('');
			$('#bankAccountKey').val('');
			$('#bankAccountName').val('');
			$('#h_bankAccountName').val('');
			$('#bankCode').val('');
			$('#h_bankCode').val('');
			$('#bankCodeKey').val('');
			$('#bankCodeName').val('');
			$('#h_bankCodeName').val('');
			$('#bankAccountBeneficiary').val('');
			$('#h_bankAccountBeneficiary').val('');
			$('#bankAccountCurrency').val('');
			$('#h_bankAccountCurrency').val('');
		}
	});
});

function gridCoupons(){
    $('.calendar').datepicker();
    gridCouponDataTables();
    $('#gridCoupon').removeAttr('style');

    onChangeNextCpnDate();
    
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
                     null,
                     null
                   ],
                   
        aaSorting:[[0,'asc']],
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

function calculate(){
	var transaction = new Transaction();
	transaction.securityType = $('#securityType').val();
	transaction.quantity = $('#nominalStripped').val();
	transaction.price = 1;
	transaction.priceUnit = 1;
	transaction.netAmount = $('#nominalStripped').val();
	transaction.lastPaymentDate = new Date($('#effectiveDate').datepicker('getDate'));
	var maturityDate = null;
	if ($("#newMaturityDate").val()!=''){
		maturityDate = new Date($("#newMaturityDate").datepicker('getDate'));
	} else {
		maturityDate = new Date($("#oldMaturityDate").datepicker('getDate'));
	}
	console.log("new maturityDate = "+$("#newMaturityDate").datepicker('getDate'));
	console.log("maturityDate = "+maturityDate);
	transaction.nextPaymentDate = maturityDate;
	transaction.settlementDate = maturityDate;
	
	var valueInterestRate = 0;
	if ($('#newInterestRate').val()!=''){
		valueInterestRate = $('#newInterestRateStripped').val();
	} else {
		valueInterestRate = $('#oldInterestRateStripped').val();
	}
	console.log("interest Rate = "+valueInterestRate);
	transaction.interestRate = valueInterestRate;
	transaction.frequency = transaction.decodeFrequency($('#frequency').val());
	transaction.accrualBase = transaction.decodeCalculationBase($('#accrualBase').val());
	transaction.yearBase = transaction.decodeCalculationBase($('#yearBase').val());
	transaction.isFraction = false;
	transaction.fractionRatioSource = 0;
	transaction.fractionRatioTarget = 0;
	
	transaction.calculateAccruedInterest();
	console.log('accrued interest = ' +transaction.accruedInterest);
	
	if ($('#changeAccruedInterest').attr("checked")){
		$('#newInterest').autoNumericSet(transaction.accruedInterest);
		$('#newInterestStripped').val(transaction.accruedInterest);
	}
}

function calculateTotalCoupon() {
	// FREQ MONTHLY
	var freq = 1;
	var maturityDateCal = null;
	if ($('#newMaturityDate').val()!=''){
		maturityDateCal = new Date($("#newMaturityDate").datepicker('getDate'));
	} else {
		maturityDateCal = new Date($("#oldMaturityDate").datepicker('getDate'));
	}

	var monthMaturity = maturityDateCal.getMonth();
    var yearMaturity = maturityDateCal.getFullYear();
    
    var couponDateCal = new Date($("#placementDate").datepicker('getDate'));
    
    var monthCoupon = couponDateCal.getMonth();
    var yearCoupon = couponDateCal.getFullYear();
    var a = (yearMaturity-yearCoupon)*12;
    var b = (monthMaturity-monthCoupon);
    var c = Number(a) + Number(b);
    var d = Number(c) / Number(freq);
    
    var result = 0;
    
    result = ((((yearMaturity-yearCoupon)*12+(monthMaturity-monthCoupon))/freq));
    
    if ((maturityDateCal.getDate() > couponDateCal.getDate())){
    	result = result + 1;
    }

    $('#totalCoupon').val(result);
    //$('#intPaymentNo').val(result);
}

function onChangeNextCpnDate(){
	$("input.coupon").mask("${appProp?.dateMask}", { placeholder:" " });
	$('input.coupon').datepicker({dateFormat:'${appProp?.jqueryDateFormat}'});
	$('input.coupon').change( function(){
	
	var el = $(this);
	var dateVal = el.val();
	var id = this.id;
	var errorId= "#couponError";
	//el.removeClass("fieldError");
	$(errorId).html("");
	//$(el.next()).html("");
	
	var elementCouponDate = $(this);
	var rowCouponDate = elementCouponDate.parents("tr");
	var newRowCoupon = elementCouponDate.parents("tr").next();
	
	var indexTable = id.split("[")[1].split("]")[0];
	/*var idDays = "input[id*='days[" + indexTable + "]']";
	var idDaysHidden = "input[id*='daysHidden[" + indexTable + "]']";
	var idGrossInterest = "input[id*='grossInterest[" + indexTable + "]']";
	var idGrossInterestHidden = "input[id*='grossInterestHidden[" + indexTable + "]']";
	var idPaymentDate = "input[id*='paymentDate[" + indexTable + "]']";
	var idPaymentDateHidden = "input[id*='paymentDateHidden[" + indexTable + "]']";*/
	var startDate = $("input[id*='lastPaymentDate["+indexTable+"]']");
	var endDate = $("input[id*='nextPaymentDate["+indexTable+"]']");
	var startDateVal = startDate.val();
	var dateFormatCompare = "dd/mm/yyyy";
	var dateFormatCompareCoupon = "yyyy/mm/dd";
	
	var dtFrmSelectNextDate = new Date(el.datepicker('getDate')).format(dateFormatCompare);
	
    var indexRowTable = elementCouponDate.parents("tr").index();

    var total = parseInt($("#totalCoupon").val());
	var idx = parseInt(indexRowTable) + 1;
	var d = dtFrmSelectNextDate.split("/")[0];
	var m = dtFrmSelectNextDate.split("/")[1];
	var y = dtFrmSelectNextDate.split("/")[2];

	var lastDate = m + "/" + d + "/" + y;
	var dt = new Date(lastDate);

	for (x = idx; x <= total; x++) {
		$("#gridCoupon tbody tr:nth-child(" + (x+1) +")").find("input[id*='lastPaymentDate']").val(dt.format(dateFormatCompare));
	    dt.setMonth(dt.getMonth() + 1);
	    $("#gridCoupon tbody tr:nth-child(" + (x+1) +")").find("input[id*='nextPaymentDate']").val(dt.format(dateFormatCompare));
	    var pyDate = $.datepicker.formatDate("yymmdd", dt);
	    var fixDate = getPaymentDate(pyDate, 0);
        var fixPaymentDate = new Date(fixDate);
	    $("#gridCoupon tbody tr:nth-child(" + (x+1) +")").find("input[id*='paymentDate']").val(fixPaymentDate.format(dateFormatCompare));
	}
	
	try {
	    $.datepicker.parseDate('${appProp?.jqueryDateFormat}', dateVal, null);
	} catch(error) {
	    el.addClass("fieldError");
	    $(errorId).html("* Invalid date format").show();
	    }
	});
	
	function processNextPaymentDate(nextPaymentRow, selectedPaymentRow) {
	    var childPaymentRowNode = Number(1);
	    var dateFormatCompare = "yyyy/mm/dd";
	
	var currentSelectedPaymentDate = $("input[id*='paymentDate']", selectedPaymentRow.children(childPaymentRowNode));
	var frmtCurrentSelectedPaymentDate = new Date(currentSelectedPaymentDate.datepicker('getDate')).format(dateFormatCompare);
	
	var advancePaymentDate = $("input[id*='paymentDate']", nextPaymentRow.children(childPaymentRowNode));
	var frmtAdvancePaymentDate = new Date(advancePaymentDate.datepicker('getDate')).format(dateFormatCompare);
	
	if (frmtCurrentSelectedPaymentDate > frmtAdvancePaymentDate) {
	    $(advancePaymentDate).addClass('fieldError');
	} else {
	    $(advancePaymentDate).removeClass('fieldError');
	    }
	}
	
	$(".payDate").change(function(){
	var dateFormatCompare = "yyyy/mm/dd";
	var elementPaymentDate = $(this);
	var rowPaymentDate = elementPaymentDate.parents("tr");
	var newRowPayment = elementPaymentDate.parents("tr");
	var currentPayment = $(rowPaymentDate).children(0).html();
	var childNodePayment = Number(1);
	
	var selectPayment = $(rowPaymentDate).find("input[id*='paymentDate']");
	var dtFrmSelectPayment = new Date(selectPayment.datepicker('getDate')).format(dateFormatCompare);
	
	// compare current changed payment date with all next payment date
	while (true) {
	    newRowPayment = newRowPayment.next();
	    if (newRowPayment.children().length == 0) {
	        break;
	    } else {
	        processNextPaymentDate(newRowPayment, rowPaymentDate);
	    }
	}
	
	// compare changed payment date with same row end date
	var currentNextPaymentDate = $(rowPaymentDate).find("input[id*='nextPaymentDate']");
	var dtFrmNextPaymentDate = new Date(currentNextPaymentDate.datepicker('getDate')).format(dateFormatCompare);
	if (dtFrmSelectPayment < dtFrmNextPaymentDate) {
	    $(selectPayment).addClass('fieldError');
	} else {
	    $(selectPayment).removeClass('fieldError');
	    }
	});
	
	function processRow(nextRow, selectRow) {
	    var childProcessRowNode = Number(1);
	    var dateFormatCompare = "yyyy/mm/dd";
	
	var maturityDateVal = new Date($("#maturityDate").datepicker('getDate'));
	var dtFrmtMaturityDateVal = maturityDateVal.format(dateFormatCompare);
	
	var currentNextPaymentDate = $("input[id*='nextPaymentDate']", selectRow.children(childProcessRowNode));
	var dtCurrentNextPayment = new Date(currentNextPaymentDate.datepicker('getDate'));
	var frmtCurrentNextPayment = dtCurrentNextPayment.format(dateFormatCompare);
	
	var advNextPaymentDate = $("input[id*='nextPaymentDate']", nextRow.children(childProcessRowNode));
	var dtAdvNextPayment = new Date(advNextPaymentDate.datepicker('getDate'));
	var frmtAdvNextPayment = dtAdvNextPayment.format(dateFormatCompare);
	
	if ((frmtCurrentNextPayment > frmtAdvNextPayment) || (frmtAdvNextPayment > dtFrmtMaturityDateVal)) {
		$(advNextPaymentDate).addClass('fieldError');
	} else {
		$(advNextPaymentDate).removeClass('fieldError');
	    }
	}
	
	$(".nextDate").bind("keyup change", function(e){
	    
	var dateFormatCompare = "yyyy/mm/dd";
	var element = $(this);
	var id = this.id;
	var row = element.parents("tr");
	var newRow = element.parents("tr");
	var current = $(row).children(0).html();
	var childNode = Number(1);
	var dateFormat1 = "dd/mm/yyyy";
	var dtFrmSelectNextDate = new Date(element.datepicker('getDate')).format(dateFormat1);
	   
	    while(true) {
	        newRow = newRow.next();
	        if (newRow.children().length == 0) {
	            break;
	        }else{
	            processRow(newRow, row);
	        }
	    }
	    var closeDialogMessage = function() {
			$("#dialog-message").dialog('close');
		return false;
	};
	var selectDate = $(row).find("input[id*='nextPaymentDate']");
	var dtSelect = new Date(selectDate.datepicker('getDate'));
	var dtFrmtSelect = dtSelect.format(dateFormatCompare);
	
	var maturityDateVal = new Date($("#maturityDate").datepicker('getDate'));
	var dtFrmtMaturityDateVal = maturityDateVal.format(dateFormatCompare);
	
	var indexTable = id.split("[")[1].split("]")[0];
	var startDate = $("input[id*='lastPaymentDate["+indexTable+"]']");
	var dateSelectStartDate = new Date(startDate.datepicker('getDate')).format(dateFormat1);
	
	var idDays = "input[id*='days[" + indexTable + "]']";
	var idDaysHidden = "input[id*='daysHidden[" + indexTable + "]']";
	var idGrossInterest = "input[id*='grossInterest[" + indexTable + "]']";
	var idGrossInterestHidden = "input[id*='grossInterestHidden[" + indexTable + "]']";
	var idPaymentDate = "input[id*='paymentDate[" + indexTable + "]']";
	var idPaymentDateHidden = "input[id*='paymentDateHidden[" + indexTable + "]']";
	   
	    // compare current end date with next start date
	var nextRow = row.next();
	var lastPaymentDate = undefined;
	var dtLastPayment = undefined;
	var dtFrmtLastPayment = undefined;
	
	if (nextRow != undefined && nextRow.length > 0) {
	    lastPaymentDate = $("input", nextRow.children(childNode));
	    //dtLastPayment = new Date(lastPaymentDate.datepicker('getDate'));
	    dtLastPayment =  $.datepicker.parseDate('${appProp?.jqueryDateFormat}', lastPaymentDate.val(), null);
	    dtFrmtLastPayment = dtLastPayment.format(dateFormatCompare);
	}
	
	// compare current end date with same row start date
	var currentLastPaymentDate = $(row).find("input[id*='lastPaymentDate']");
	//var dtFrmtCurrentLastPaymentDate = new Date(currentLastPaymentDate.datepicker('getDate')).format(dateFormatCompare);
	var dtFrmtCurrentLastPaymentDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', currentLastPaymentDate.val(), null).format(dateFormatCompare);
	
	var holiday = 'false';
	
	if ($('#considerHoliday').val()=='true'){
	    $.post('@{DepositoPlacements.checkHoliday()}?date='+dtFrmSelectNextDate , 
				$('#form').serialize(), function(data) {
    		checkRedirect(data);
			holiday = data;
		});
	}
	
	if ((holiday =='true')||(dtFrmtSelect < dtFrmtCurrentLastPaymentDate) || (dtFrmtSelect > dtFrmtLastPayment) || (dtFrmtSelect > dtFrmtMaturityDateVal)) {
		if (holiday =='true'){
			messageAlertOk("Date is Holiday", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
		}
	    $(selectDate).addClass('fieldError');
	//        return false;
	} else {
		var interestRate = null;
		if ($('#newInterestRate').val()!=''){
			interestRate = $('#newInterestRateStripped').val();
		} else {
			interestRate = $('#oldInterestRateStripped').val();
		}
	    $(selectDate).removeClass('fieldError');
	    $.post('@{DepositoPlacements.onChangeNextPaymentDate()}',
				{
					'startDate':startDate.val(),
					'endDate':dtFrmSelectNextDate,
					'nominal':$('#nominalStripped').val(),
					'interestRate':interestRate,
					'accrualBase':$('#accrualBase').val(),
					'yearBase':$('#yearBase').val(),
					'considerHoliday':$('considerHoliday').val(),
					'frequency':$('#frequency').val()
					
				}, function(data) {
		    		checkRedirect(data);
	//        	$(".nextDate").parents("tr").find(idPaymentDate).val(data);
					if (data.status=='success') {
						$(".nextDate").parents("tr").find(idDays).val(data.days);
						$(".nextDate").parents("tr").find(idDaysHidden).val(data.days);
						$(".nextDate").parents("tr").find(idGrossInterest).autoNumericSet(data.grossInterest);
						$(".nextDate").parents("tr").find(idGrossInterestHidden).val(data.grossInterest);
						$(".nextDate").parents("tr").find(idPaymentDate).val(data.paymentDate);
						$(".nextDate").parents("tr").find(idPaymentDateHidden).val(data.paymentDate);
					} else {
						el.addClass("fieldError");
						/*messageAlertOk(data.message, "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
						return false;*/
						}
		    });
	    }
	});
}