function Liquidation(html) {
	if (this instanceof Liquidation) {
/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var liq = html.inject(this); 
		$('.calendar').datepicker();
		var roundType;
		var roundDigit;
		var digitPrice = $("#priceRoundValue").val();
		var typePrice = $("#priceRoundType").val();
		var digitAmount = $("#amountRoundValue").val();
		var typeAmount = $("#amountRoundType").val();
		
		var digitPrice = $("#priceRoundValue").val();
		var typePrice = $("#priceRoundType").val();
		var rgProduct = null;

		
/* =========================================================================== 
 * Function
 * ========================================================================= */
		
		if ($.browser.msie){
			$("#externalreference[maxlength]").bind('input propertychange', function() {  
		        var maxLength = $(this).attr('maxlength');  
		        if ($(this).val().length > maxLength) {  
		            $(this).val($(this).val().substring(0, maxLength));  
		        }  
		    });
			
			$("#remarks[maxlength]").bind('input propertychange', function() {  
		        var maxLength = $(this).attr('maxlength');  
		        if ($(this).val().length > maxLength) {  
		            $(this).val($(this).val().substring(0, maxLength));  
		        }  
		    });
		}
		
		function validateNavDate(){
		    var navDateYYYYMMDD = $.datepicker.formatDate( "yymmdd", liq.navDate.datepicker("getDate") );
		    var productCode = liq.fundCode.val();
			
			html.getPriceByRgNav(navDateYYYYMMDD, productCode, function(data){
				if(data){
					liq.price.valueRnd( data.nav, true, digitPrice, typePrice );					
				} else {
					liq.price.val(null);
				}
			});
			
			if (!liq.navDate.hasClass('fieldError')){
				validateNavTransactionDate();
			}
		}
		

		function validateTransactionDate(){
			// set default value of nav date, post date and payment date
			var redNavUsed = $('#redNavUsed').val();
			var redPostPeriod = $('#redPostPeriod').val();
			var redPayPeriod = $('#redPayPeriod').val();
			if ( '${mode}' == 'edit' && '${confirming}' != 'true') {
				console.log(" confirming : " + '${confirming}');
				var aDate = liq.transactionDate.datepicker("getDate");
				var transDate = $.datepicker.formatDate("yymmdd", aDate);

				var newNavDate = html.getWorkingDate(transDate, (redNavUsed ? redNavUsed : 0 )) + "";
				aDate = $.datepicker.parseDate('yymmdd', newNavDate, null);
				$('#navDate').val( $.datepicker.formatDate('${appProp?.jqueryDateFormat}', aDate) );
				liq.navDate.val($('#navDate').val());
				
				var newPostDate = html.getWorkingDate(transDate, (redPostPeriod ? redPostPeriod : 0 )) + "";
				aDate = $.datepicker.parseDate('yymmdd', newPostDate, null);
				$('#postDate').val( $.datepicker.formatDate('${appProp?.jqueryDateFormat}', aDate) );
				liq.postDate.val($('#postDate').val());
				
				var newPayDate = html.getWorkingDate(transDate, (redPayPeriod ? redPayPeriod : 0 )) + "";
				aDate = $.datepicker.parseDate('yymmdd', newPayDate, null);
				$('#paymentDate').val( $.datepicker.formatDate('${appProp?.jqueryDateFormat}', aDate) );
				liq.paymentDate.val($('#paymentDate').val());
			}
			
//			liq.h_postDate.val(liq.transactionDate.val());
			var id = $('#postDate');
			var postDate = liq.postDate.datepicker('getDate');
			var navDate = liq.navDate.datepicker('getDate');
			var transactionDate = liq.transactionDate.datepicker('getDate');
			if (postDate != null && transactionDate != null){
				if (postDate.getTime() < transactionDate.getTime()){
					id.addClass('fieldError');
					$('#postDateError').html("Post Date must be greater than Transaction Date");
					return false;
				} else {
					var postDateYYYYMMDD = $.datepicker.formatDate( "yymmdd", postDate );
					var navDateYYYYMMDD = $.datepicker.formatDate( "yymmdd", navDate );
					var productCode = liq.fundCode.val();
					html.getPortfolioTotalUnitMaturity(productCode, postDateYYYYMMDD, navDateYYYYMMDD, function(data){
//					html.getPortfolioTotalUnit(productCode, postDateYYYYMMDD, function(data){
						if(data){							
							liq.totalUnit.valueRnd(data, true, digitPrice, typePrice);
						}else{
							liq.totalUnit.value("");
						}
					});
					id.removeClass('fieldError');
					$('#postDateError').html('');
				}
//				validateNavTransactionDate();
				validateNavDate();
			}			
		}
		
		function validatePaymentDate(){
			console.log( "payment date" );
			var id = $('#paymentDate');
			var paymentDate = liq.paymentDate.datepicker('getDate');
			var transactionDate = liq.transactionDate.datepicker('getDate');
			$('#paymentDateError').html("");
//			if (!id.hasClass('fieldError')){
				if (paymentDate!= null && transactionDate!= null){
					if (paymentDate.getTime() < transactionDate.getTime()){
						id.addClass('fieldError');
						$('#paymentDateError').html("Payment Date must be greater or equal than Transaction Date");
						return false;
					} else {
						id.removeClass('fieldError');
						$('#paymentDateError').html('');
						liq.paymentDate.value(liq.paymentDate.val());
					}
				}
//			}
		}
		
		function validateNavTransactionDate(){
			var navDate = liq.navDate.datepicker('getDate');
			var transactionDate = liq.transactionDate.datepicker('getDate');
			
			if(navDate != null && transactionDate != null){
				if (navDate.getTime() > transactionDate.getTime()){
					liq.navDate.addClass('fieldError');
					$('#navDateError').html("Nav Date must be less than Transaction Date");
					return false;
				} else {
					liq.navDate.removeClass('fieldError');
					$('#navDateError').html('');
				}
			}
		}
		
		function validatePostDate(){
			var id = $('#postDate');
			var postDate = liq.postDate.datepicker('getDate');
			var navDate = liq.navDate.datepicker('getDate');
			var transactionDate = liq.transactionDate.datepicker('getDate');
			if (postDate != null && transactionDate != null){
				if (postDate.getTime() < transactionDate.getTime()){
					id.addClass('fieldError');
					$('#postDateError').html("Post Date must be greater than Transaction Date");
					return false;
				} else {
					var postDateYYYYMMDD = $.datepicker.formatDate( "yymmdd", postDate );
					var navDateYYYYMMDD = $.datepicker.formatDate( "yymmdd", navDate );
					var productCode = liq.fundCode.val();
					html.getPortfolioTotalUnitMaturity(productCode, postDateYYYYMMDD, navDateYYYYMMDD, function(data){
//					html.getPortfolioTotalUnit(productCode, postDateYYYYMMDD, function(data){
						if(data){							
							liq.totalUnit.valueRnd(data, true, digitPrice, typePrice);
						}else{
							liq.totalUnit.value("");
						}
					});
					id.removeClass('fieldError');
					$('#postDateError').html('');
				}
				validateNavTransactionDate();
			}			
		}

/* =========================================================================== 
 * Event
 * ========================================================================= */
		if (('${mode}'=='edit')&&(('${confirming}' != 'true'))) {
			if ($('#tabDetail #gridLiquid').dataTable().length == 1) $("#tabs").tabs("disable",1);
		} 
		
		if ('${confirming}'=='true') {
			liq.clearAll.button('option', 'disabled', true);
//			liq.tabs.tabs('select', 'tabDetail');
			$("#tabs").tabs("select", "#tabDetail");
			//liq.tabs.tabs("disable", 0);
		}
		
		liq.fundCode.popupProduct("navDate", function(data){
			if (data) {
				$('#processCalculate').button('option', 'enabled', true);

				roundType = data.amountRoundType;
				roundDigit = data.amountRoundValue;
				digitPrice = data.priceRoundValue;
				typePrice = data.priceRoundType;
				digitAmount = data.amountRoundValue;
				typeAmount = data.amountRoundType;

				digitPrice = data.priceRoundValue;
				typePrice = data.priceRoundType;
				
				liq.maturityDate.value( "" );
				liq.goodFundDate.value( "" );
				liq.navDate.value( "" );
				liq.postDate.value( "" );
				liq.paymentDate.value( "" );

				// trade date and maturity date
				if( data.liquidDate ){
					var aLiquidDate = $.datepicker.parseDate( "${appProp.jqueryDateFormat}", data.liquidDate, null );
					var formattedLiquidDate = $.datepicker.formatDate("${appProp.jqueryDateFormat}", aLiquidDate);
					
					liq.maturityDate.value( formattedLiquidDate );
					liq.goodFundDate.value( formattedLiquidDate );
					
					console.log( "liquidDate:"+data.liquidDate );
					
				}

				// nav date
				if( data.navDateForMaturity ){
					var aNavDateForMaturity = new Date(data.navDateForMaturity);
					var formattedNavDateForMaturity= $.datepicker.formatDate("${appProp.jqueryDateFormat}", aNavDateForMaturity);

					liq.navDate.value( formattedNavDateForMaturity) ;					
					console.log( "aNavDateForMaturity:"+aNavDateForMaturity );					
				}

				// post date
				/*
				if( data.postDateForMaturity ){
					var aPostDateForMaturity = new Date(data.postDateForMaturity);
					var formattedPostDateForMaturity = $.datepicker.formatDate("${appProp.jqueryDateFormat}", aPostDateForMaturity);
					
					liq.postDate.value( formattedPostDateForMaturity );
					console.log( "aPostDateForMaturity:"+aPostDateForMaturity );					
				}
				*/
				
				// payment date
				if( data.paymentDateForMaturity ){
					var aPaymentDateForMaturity = new Date(data.paymentDateForMaturity);
					var formattedPaymentDateForMaturity = $.datepicker.formatDate("${appProp.jqueryDateFormat}", aPaymentDateForMaturity);
					
					liq.paymentDate.value( formattedPaymentDateForMaturity );
					console.log( "aPaymentDateForMaturity:"+aPaymentDateForMaturity );					
				}

				
			}
		}, function(){
			liq.maturityDate.value( "" );
			liq.goodFundDate.value( "" );
			liq.navDate.value( "" );
			liq.postDate.value( "" );
			liq.paymentDate.value( "" );

		});
		
		liq.navDate.change(function(){
			validateNavDate();
		});

		liq.transactionDate.change(function(){
			validateTransactionDate();
			validatePaymentDate();
		});
		
		liq.postDate.change(function(){
			validatePostDate();
		});
		
		liq.paymentDate.change(function(){
			validatePaymentDate();
		});
		
		liq.fundCode.change(function(){
			if (liq.fundCode.val().isEmpty()){
				liq.maturityDate.val('');
				liq.goodFundDate.val('');
			}	
		});
		
		liq.feePct.autoNumeric({vMax: '100'});
		liq.feePct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		liq.feePct.change(function(){
			liq.feePctStripped.val(liq.feePct.val());
			liq.feeAmt.val('');
			if (liq.feePct.val()!='') $('#errFee').html('');
		});
		
		liq.feeAmt.change(function(){
			liq.feeAmt.value(liq.feeAmt.val(), true, roundDigit, roundType);
			liq.feePct.val('');
			if (liq.feeAmt.val()!='') $('#errFee').html('');
		});
		
		liq.discPct.autoNumeric({vMax: '100'});
		liq.discPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		liq.discPct.change(function(){
			liq.discPctStripped.val(liq.discPct.val());
			liq.discAmt.val('');
			if (liq.discPct.val()!='') $('#errDiscount').html('');
		});
		
		liq.discAmt.change(function(){
			liq.discAmt.value(liq.discAmt.val(),true, roundDigit, roundType);
			liq.discPct.val('');
			if (liq.discAmt.val()!='') $('#errDiscount').html('');
		});
		
		liq.otherPct.autoNumeric({vMax: '100'});
		liq.otherPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		liq.otherPct.change(function(){
			liq.otherPctStripped.val(liq.otherPct.val());
			liq.otherAmt.val('');
			if (liq.otherPct.val()!='') $('#errOther').html('');
		});
		
		liq.otherAmt.change(function(){
			liq.otherAmt.value(liq.otherAmt.val(), true, roundDigit, roundType);
			liq.otherPct.val('');
			if (liq.otherAmt.val()!='') $('#errOther').html('');
		});
		
		liq.tax.popupTax("remarks", function(data){	
			if (data) {
				liq.taxRate.val(data.taxRate);
				$('#errTax').html('');
			}
		});
		
		liq.tax.change(function(){
			if (!liq.tax.hasClass('fieldError'))
				liq.taxHidden.val(liq.tax.val());
		});
		
		//=== PROCESS BUTTON CALCULATE ===//
		liq.processCalculate.click(function(){
			if (((liq.feePct.val()=='')&&(liq.feeAmt.val()==''))||((liq.discPct.val()=='')&&
				(liq.discAmt.val()=='')) ||((liq.otherPct.val()=='')&&(liq.otherAmt.val()=='')) || 
				(liq.tax.val()=='') || (liq.fundCode.val()=='') || (liq.navDate.val()=='') || 
				(liq.postDate.val()=='') || (liq.paymentDate.val()=='') 
				|| (liq.totalUnit.val()=='')|| (liq.price.val()=='')
				) {
				
				if (liq.fundCode.val()==''){
					$('#errFundCode').html('Required');
				} else {
					$('#errFundCode').html('');
				}

				if (liq.navDate.val()==''){
					$('#navDateError').html('Required');
				} else {
					$('#navDateError').html('');
				}
				
				if (liq.postDate.val()==''){
					$('#postDateError').html('Required');
				} else {
					$('#postDateError').html('');
				}
				
				if (liq.paymentDate.val()==''){
					$('#paymentDateError').html('Required');
				} else {
					$('#paymentDateError').html('');
				}
				
				if ((liq.feePct.val()=='')&&(liq.feeAmt.val()=='')){
					$('#errFee').html('Required');
				} else {
					$('#errFee').html('');
				}
				
				if ((liq.discPct.val()=='')&&(liq.discAmt.val()=='')){
					$('#errDiscount').html('Required');
				} else {
					$('#errDiscount').html('');
				}
				
				if ((liq.otherPct.val()=='')&&(liq.otherAmt.val()=='')){
					$('#errOther').html('Required');
				} else {
					$('#errOther').html('');
				}
				
				if ((liq.tax.val()=='')) {
					$('#errTax').html('Required');
				} else {
					$('#errTax').html('');
				}
				
				if ((liq.totalUnit.val()=='')) {
					$('#totalUnitError').html('Required');
				} else {
					$('#totalUnitError').html('');
				}
				
				if ((liq.price.val()=='')) {
					$('#priceError').html('Required');
				} else {
					$('#priceError').html('');
				}
				console.log("Error..");
				return false;
			} else if ((liq.navDate.hasClass('fieldError'))||(liq.transactionDate.hasClass('fieldError'))||(liq.paymentDate.hasClass('fieldError'))|| (liq.postDate.hasClass('fieldError'))){
				console.log("no field error");
				return false;
			} else {
				var aPaymentDate = liq.paymentDate.datepicker( "getDate" );
				var aTransactionDate = liq.transactionDate.datepicker( "getDate" );;
				var aTransactionDateYYYYMMDD = $.datepicker.formatDate( "yymmdd", aTransactionDate );
				if(rgProduct == null){
					rgProduct = html.getRgProduct(liq.fundCode.val(), function(data){
						rgProduct = data;
					});
				}
				var maxPaymentDateYYYMMDD = html.getWorkingDate(aTransactionDateYYYYMMDD, rgProduct.maxPaymentDate);
				var maxPaymentDate = $.datepicker.parseDate( "yymmdd", maxPaymentDateYYYMMDD);
				if( aPaymentDate.getTime() >  maxPaymentDate.getTime() ){
					$("#paymentDateError").html( "Payment date exceeded Max Payment date." );
					liq.paymentDate.addClass('fieldError');
					return false;
				}
				
				$.post('@{RegistryLiquidationProcess.calculate()}/?productCode='+$('#fundCode').val(), liq.liquidationProcess.serialize(), function(data) {
		    		checkRedirect(data);
					$('#listLiquid').html(data);
					liquidationGrid();
					$("#tabs").tabs("enable",1);
					$("#tabs").tabs("select", "#tabDetail");
					$('.error').html('');
//					liq.tabs.tabs("enable", 1);
//					liq.tabs.tabs('select', 'tabDetail');
					//liq.tabs.tabs("disable", 0);
					
					// disabled modeView field and add their respective hidden element, if it doesnot exists yet
					$('.modeView').each(function(idx, el){
					    var cId = $(el).attr("id");
					    var cName = $(el).attr("name");
					    var cVal = $(el).val();
					    if( $("#h_"+cId)[0] == undefined || $("#h_"+cId)[0] == "undefined"){
					        $(el).parent().append( "<input id=\"h_"+cId+"\" type=\"hidden\" name=\""+cName+"\" value=\""+cVal+"\"/>" );
					    }
					    // finally, get them disabled one by one
					    $(el).attr('disabled', 'disabled');
					});
					$('#processCalculate').button('option', 'disabled', true);
					liq.calculate.val(false);
				});
			}
		});
		
//		validateNavDate();
		validateTransactionDate();
		validatePaymentDate();
		liq.tax.change();
	} else {
		return new Liquidation(html);
	}
}