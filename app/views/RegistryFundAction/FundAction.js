function FundAction(html) {
	if (this instanceof FundAction) {
/* =========================================================================== 
 * Constant
 * ========================================================================= */		
		var BY_CASH = "By Cash";
		var BY_REINVESTMENT = "By Reinvestment";
		var BY_REDEMPTION = "By Redemption";
		var INVESTOR_OPTION = "Investor Option";
		
		var TOTAL_AMOUNT = "TOTAL_AMOUNT";
		var AMOUNT_PER_UNIT = "AMOUNT_PER_UNIT";
		var UNIT_PER_UNIT = "UNIT_PER_UNIT";
		
		var ALLOCATE_TOTAL_AMOUNT = "ALLOCATE_TOTAL_AMOUNT";
		var RATIO_PER_UNIT = "RATIO_PER_UNIT";
		
		var typeAmount;
		var digitAmount;
		var typeUnit;
		var digitUnit;
		var typePrice;
		var digitPrice;
		var fixNav;
		var fixNavPrice;
		
		var isAllocate = true;
		
		var totalUnitError = '#totalUnitError';
		var priceError = '#priceError';
		var roundTypeError = '#roundTypeError';
		var roundValueError = '#roundValueError';
		var ratioError = '#ratioError';
		
/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var fa = html.inject(this);
//		fa.divType.children().eq(0).remove();
//		fa.roundType.children().eq(0).remove();
//		fa.inputBy.children().eq(0).remove();
//		fa.ratioPerUnitBy.children().eq(0).remove();--del
/* =========================================================================== 
 * Variable
 * ========================================================================= */
//		var rgProduct = (fa.productCode.isEmpty()) ? null: html.getRgProduct(fa.productCode.val());
		var rgNav = (fa.navDate.isEmpty() || fa.productCode.isEmpty()) ? null : html.getRgNav(fa.navDate.val().fmtYYYYMMDD("/"), fa.productCode.val());
		var rgTax = (fa.taxCode.isEmpty()) ? null : html.getRgTax(fa.taxCode.val());
/* =========================================================================== 
 * Method Ajax Call
 * ========================================================================= */
		
/* =========================================================================== 
 * Method
 * ========================================================================= */

		function postDate() {
			if(fa.postDate.val() == fa.divDate.val()){
				$("#priceLbl span.req").show();
			} else {
				$("#priceLbl span.req").hide();
				$(priceError).html("");
			}
		}

		function calcRgProduct() {
			console.log('calcRgProduct');
			
			if (rgProduct) {
				var divDate = fa.divDate.val().fmtYYYYMMDD("/");
//				if (fa.divType.val() == BY_REINVESTMENT) {
				var navDate = html.getWorkingDate(divDate, rgProduct.divNavUsed) + "";
				fa.navDate.value(navDate.toMMDDYYYY("/"));
				fa.navDate.change();
//				}
				
				var cumDate = html.getWorkingDate(divDate, rgProduct.divCumPeriod) + "";
				fa.cumDate.value(cumDate.toMMDDYYYY("/"));
				fa.cumDate.change();
				
				var postDate = html.getWorkingDate(divDate, rgProduct.divPostPeriod) + "";
				fa.postDate.value(postDate.toMMDDYYYY("/"));
				fa.postDate.html("");
				
				var paymentDate = html.getWorkingDate(divDate, rgProduct.divPayPeriod) + "";
				fa.paymentDate.value(paymentDate.toMMDDYYYY("/"));
				fa.paymentDate.html("");
				
//				fa.taxCode.value(rgProduct.taxMasterByDivTaxKey.taxRate, true);
			}
		}
		
		function calcRgTax() {/*
			console.log('calcRgTax');
			
//			if (rgTax) {
//				var inputBy =  fa.inputBy.val();
//				
//				if (inputBy == TOTAL_AMOUNT) {
//					var amount = fa.amount.val().toNumber(",");
//					var taxAmount = (Number(rgTax.taxRate)/100) * amount;
//					var netAmount = amount-taxAmount;
////					fa.netAmount.value(netAmount, true);
//					fa.netAmount.valueRnd(netAmount, true, digitAmount, typeAmount);
//					
//					if (!fa.totalUnit.isEmpty()) {
//						var totalUnit = fa.totalUnit.val().toNumber(",");
//						var amountPerUnit = netAmount / totalUnit; 
////						fa.amountPerUnit.value(amountPerUnit, true);
//						fa.amountPerUnit.valueRnd(amountPerUnit, true, digitAmount, typeAmount);
//					}
//				}
//
//				if (inputBy == AMOUNT_PER_UNIT) {
//					var amountPerUnit = fa.amountPerUnit.val().toNumber(",");
//					
//					if (!fa.totalUnit.isEmpty()) {
//						var totalUnit = fa.totalUnit.val().toNumber(",");
//						var netAmount = amountPerUnit * totalUnit;
////						fa.netAmount.value(netAmount, true);
//						fa.netAmount.valueRnd(netAmount, true, digitAmount, typeAmount);
//						
//						var taxPct = (Number(rgTax.taxRate)/100);
//						var amount = (1/(1-taxPct)) * netAmount;
////						fa.amount.value(amount, true);
//						fa.amount.valueRnd(amount, true, digitAmount, typeAmount);
//					}
//				}

				//var inputMethodByAllocate = fa.allocateTotalAmount.val();--del
				//var inputMethodByRatio = fa.ratioPerUnit.val();
				if (isAllocate == true) {
//				if ((inputMethodByAllocate == ALLOCATE_TOTAL_AMOUNT) || ('${fa?.inputMethodBy}' == ALLOCATE_TOTAL_AMOUNT)) {
					var totalAmount = fa.amountStripped.val().toNumber(",");
					if (rgTax) {
						console.debug("[ALLOCATE_TOTAL_AMOUNT] rgTax.taxRate = " + rgTax.taxRate);
						var taxAmount = (Number(rgTax.taxRate)/100) * totalAmount;
						var netAmount = totalAmount-taxAmount;
						console.debug("[ALLOCATE_TOTAL_AMOUNT] taxAmount = " + taxAmount + " netAmount = " + netAmount
						+ " digitAmount = " + digitAmount + " type = " + typeAmount);
//						fa.netAmount.valueRnd(netAmount, true, digitAmount, typeAmount);
					}
				
					if (!fa.totalUnit.isEmpty()) {
						var totalUnit = fa.totalUnit.val().toNumber(",");
						console.debug("[ALLOCATE_TOTAL_AMOUNT] totalUnit = " + totalUnit + " totalAmount = " + totalAmount);
						var amountPerUnit = totalAmount / totalUnit; 
						console.debug("[ALLOCATE_TOTAL_AMOUNT] amountPerUnit = totalAmount / totalUnit = " + amountPerUnit
						+ " digitAmount = " + digitAmount + " type = " + typeAmount);
//						fa.amountPerUnit.valueRnd(amountPerUnit, true, digitAmount, typeAmount);
//						fa.total.val("");
					}
					
				} else {
				
					var ratio = fa.ratio.val().toNumber(",");
					if (!fa.totalUnit.isEmpty()) {
						var totalUnit = fa.totalUnit.val().toNumber(",");
						console.debug("totalUnit = " + totalUnit + " ratio = " + ratio);
						if ('${taskId}' != "") {
							fa.amount.val("");
							ratio = fa.total.val().toNumber(",") / totalUnit;
							fa.ratio.val(ratio);
						} else {
							var total = totalUnit * ratio; 
							console.debug("total = totalUnit * ratio = " + total);
							fa.total.valueRnd(total, true, digitAmount, typeAmount);
							fa.amountPerUnit.val("");
						}
					}
					
					if (rgTax) {
						console.debug("fa.ratioPerUnitBy = " + fa.ratioPerUnitBy.val());
						console.debug("rgTax.taxRate = " + rgTax.taxRate);
						var total = fa.total.val().toNumber(",");
						if (fa.ratioPerUnitBy.val() == AMOUNT_PER_UNIT) {
							var taxAmount = (Number(rgTax.taxRate)/100) * total;
							var netAmount = total-taxAmount;
							console.debug("[RATIO_PER_UNIT] [AMOUNR/UNIT] taxAmount = " + taxAmount + " netAmount = " + netAmount
							+ " digitAmount = " + digitAmount + " typeAmount = " + typeAmount);
							fa.netAmount.valueRnd(netAmount, true, digitAmount, typeAmount);
						}
						if (fa.ratioPerUnitBy.val() == UNIT_PER_UNIT) {
							var price = fa.price.val().toNumber(",");
							var taxAmount = (Number(rgTax.taxRate)/100) * total;
							var netAmount = (total * price)-taxAmount;
							console.debug("[RATIO_PER_UNIT] [UNIT/UNIT] taxAmount = " + taxAmount);
							console.debug("[RATIO_PER_UNIT] [UNIT/UNIT] netAmount = (total * price)-taxAmount = " + netAmount +
							" digitUnit = " + digitUnit + " typeUnit = " +typeUnit);
							fa.netAmount.valueRnd(netAmount, true, digitUnit, typeUnit);
						}
					}
				}
//			}
	--del	*/}

		function typeChangeView() {
			if (fa.dispatch.val() == "entry" || fa.dispatch.val() == "edit" || '${confirming}' != 'true') {
				$("input[name=fa.divType]").val(fa.divType.val());
				
				// redmine #2881 -- start
//				fa.navDate.disabled();
//				fa.paymentDate.disabled();
//				fa.postDate.disabled();
				
				

//				if (fa.divType.val() == BY_CASH) {
//					if(!'${confirming}' && '${mode}' != 'view') {
//						fa.paymentDate.enabled();
//						fa.navDate.enabled();//
//						fa.postDate.enabled();//
//					}
//				}

//				if (fa.divType.val() == BY_REINVESTMENT) {
//					if(!'${confirming}' && '${mode}' != 'view'){
//						fa.navDate.enabled();
//						fa.paymentDate.enabled(); //
//						fa.postDate.enabled();
//					}

//					if($.browser.msie){
//						$('#divType option').remove();
//						var options = $('#divType').attr('options');
//						options[options.length] = new Option('By Reinvestment', 'By Reinvestment');
//					}
					//if ('${confirming}' != 'true') {
						//fa.navDate.enabled();
						//fa.paymentDate.val("");
						//fa.postDate.enabled();
					//}
					//$("p[id=navDateP] label span").html(" *");
					//$("p[id=postDateP] label span").html(" *");
					//$("p[id=paymentDateP] label span").html(" *");
//				}
//				if (fa.divType.val() == BY_REDEMPTION) {
//					if($.browser.msie){
//						$('#divType option').remove();
//						var options = $('#divType').attr('options');
//						options[options.length] = new Option('By Redemption', 'By Redemption');
//					}
					//if ('${confirming}' != 'true') {
						//fa.navDate.enabled();
					//}
					//if (fa.isReadOnly.val() == "false") {
						//fa.paymentDate.enabled();
						//fa.postDate.enabled();
					//}
					//$("p[id=navDateP] label span").html(" *");
					//$("p[id=postDateP] label span").html(" *");
					//$("p[id=paymentDateP] label span").html(" *");
//				}
				/* #2881 end */
			}
		}

		function formatRound() {
			$("td[name=unitCell]", html).each(function(){
				var unit = $(this).html();
				if (digitUnit == null) {
					digitUnit = fa.unitRoundValue.val();
					typeUnit = fa.unitRoundType.val();
				}
				console.debug(typeUnit + " | " + digitUnit);
				fa.formatUnit.valueRnd(unit.toNumber(","), true, digitUnit, typeUnit);
				$(this).html(fa.formatUnit.val());
			});

			$("td[name=priceCell]", html).each(function(){
				var price = $(this).html();
				if (digitPrice == null) {
					digitPrice = fa.priceRoundValue.val();
					typePrice = fa.priceRoundType.val();
				}
				fa.formatPrice.valueRnd(price.toNumber(","), true, digitPrice, typePrice);
				$(this).html(fa.formatPrice.val());
			});
			//digitAmount
			if (jQuery.trim($('#divBy').val())=='${totAmount}'){
				$("td[name=amountCell]", html).each(function(){
					var amount = $(this).html();
					digitAmount1 = fa.roundValue.val();
					typeAmount1 = fa.roundType.val();
					
					fa.formatAmount.valueRnd(amount.toNumber(","), true, digitAmount1, typeAmount1);
					//if('${isDigitAmount}' == "1"){
						fa.formatAmount.valueRnd(fa.formatAmount.val().toNumber(","), true, digitAmount, typeAmount);
					//}
					$(this).html(fa.formatAmount.val());
				});
			}else{
				$("td[name=amountCell]", html).each(function(){
					var amount = $(this).html();
					//if (digitAmount == null) {
						digitAmount1 = fa.amountRoundValue.val();
						typeAmount1 = fa.amountRoundType.val();
					//}
					//fa.formatAmount.valueRnd(amount.toNumber(","), true, digitAmount, typeAmount);
					fa.formatAmount.valueRnd(amount.toNumber(","), true, digitAmount1, typeAmount1);
					//if('${isDigitAmount}' == "1"){
						fa.formatAmount.valueRnd(fa.formatAmount.val().toNumber(","), true, digitAmount, typeAmount);
					//}
					$(this).html(fa.formatAmount.val());
				});
			}

				//$("#tradeUnit").autoNumericSet(sumUnit, {aPad:true,mDec: digitUnit, mRound:typeUnit}).val();
				$("#tradeUnit").autoNumericSet(fa.tradeUnit.val(),{aPad:true,mDec: digitUnit, mRound:typeUnit}).val();
//				$("#tradeNetAmount").val(fa.formatAmount.val());
//				$("#tradeUnit").val(fa.tradeUnit.val(),true,fa.roundValue.val(),fa.roundType.val());
				/*if (jQuery.trim($('#divBy').val())=='${totAmount}'){
					$("#tradeNetAmount").autoNumericSet(fa.tradeNetAmount.val(),{aPad:true,mDec: fa.roundValue.val(), mRound:fa.roundType.val()}).val();
//					$("#tradeNetAmount").valueRnd(fa.tradeNetAmount.val(),true,fa.roundValue.val(),fa.roundType.val());
				}else{
					$("#tradeNetAmount").autoNumericSet(fa.tradeNetAmount.val(),{aPad:true,mDec: digitAmount, mRound:typeAmount}).val();
//					$("#tradeNetAmount").valueRnd(fa.tradeNetAmount.val(),true,digitAmount,typeAmount);
				}*/
		}
		
		function divType(rgProduct) {
			console.debug("rgProduct.divByCash = " + rgProduct.divByCash + " | rgProduct.divByReinvest = " + rgProduct.divByReinvest + " | rgProduct.divByRedeem = " + rgProduct.divByRedeem + " | rgProduct.divInvestorOpt = " + rgProduct.divInvestorOpt);
			if (rgProduct.divByCash == true) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Cash', 'By Cash');
				}
				$('#divType option[value="By Cash"]').show();
				$('#divType option[value="By Reinvestment"]').hide();
				$('#divType option[value="By Redemption"]').hide();
				$('#divType option[value="Investor Option"]').hide();
				fa.divType.enabled();
			} 
			if (rgProduct.divByReinvest == true) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Reinvestment', 'By Reinvestment');
				}
				$('#divType option[value="By Cash"]').hide();
				$('#divType option[value="By Reinvestment"]').show();
				$('#divType option[value="By Redemption"]').hide();
				$('#divType option[value="Investor Option"]').hide();
				fa.divType.enabled();
			} 
			if (rgProduct.divByRedeem == true) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Redemption', 'By Redemption');
				}
				$('#divType option[value="By Cash"]').hide();
				$('#divType option[value="By Reinvestment"]').hide();
				$('#divType option[value="By Redemption"]').show();
				$('#divType option[value="Investor Option"]').hide();
				fa.divType.enabled();
			} 
			if (rgProduct.divInvestorOpt == true) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('Investor Option', 'Investor Option');
				}
				$('#divType option[value="By Cash"]').hide();
				$('#divType option[value="By Reinvestment"]').hide();
				$('#divType option[value="By Redemption"]').hide();
				$('#divType option[value="Investor Option"]').show();
				fa.divType.enabled();
			}
			if ((rgProduct.divByCash == true) && (rgProduct.divByReinvest == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Cash', 'By Cash');
					options[options.length] = new Option('By Reinvestment', 'By Reinvestment');
				}
				$('#divType option[value="By Cash"]').show();
				$('#divType option[value="By Reinvestment"]').show();
				$('#divType option[value="BY_REDEMPT ION"]').hide();
				$('#divType option[value="Investor Option"]').hide();
				fa.divType.enabled();
			} 
			if ((rgProduct.divByCash == true) && (rgProduct.divByRedeem == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Cash', 'By Cash');
					options[options.length] = new Option('By Redemption', 'By Redemption');
				}
				$('#divType option[value="By Cash"]').show();
				$('#divType option[value="By Reinvestment"]').hide();
				$('#divType option[value="By Redemption"]').show();
				$('#divType option[value="Investor Option"]').hide();
				fa.divType.enabled();
			} 
			if ((rgProduct.divByCash == true) && (rgProduct.divInvestorOpt == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Cash', 'By Cash');
					options[options.length] = new Option('Investor Option', 'Investor Option');
				}
				$('#divType option[value="By Cash"]').show();
				$('#divType option[value="By Reinvestment"]').hide();
				$('#divType option[value="By Redemption"]').hide();
				$('#divType option[value="Investor Option"]').show();
				fa.divType.enabled();
			} 
			if ((rgProduct.divByReinvest == true) && (rgProduct.divByRedeem == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Reinvestment', 'By Reinvestment');
					options[options.length] = new Option('By Redemption', 'By Redemption');
				}
				$('#divType option[value="By Cash"]').hide();
				$('#divType option[value="By Reinvestment"]').show();
				$('#divType option[value="By Redemption"]').show();
				$('#divType option[value="Investor Option"]').hide();
				fa.divType.enabled();
			} 
			if ((rgProduct.divByReinvest == true) && (rgProduct.divInvestorOpt == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Reinvestment', 'By Reinvestment');
					options[options.length] = new Option('Investor Option', 'Investor Option');
				}
				$('#divType option[value="By Cash"]').hide();
				$('#divType option[value="By Reinvestment"]').show();
				$('#divType option[value="By Redemption"]').hide();
				$('#divType option[value="Investor Option"]').show();
				fa.divType.enabled();
			} 
			if ((rgProduct.divByRedeem == true) && (rgProduct.divInvestorOpt == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Redemption', 'By Redemption');
					options[options.length] = new Option('Investor Option', 'Investor Option');
				}
				$('#divType option[value="By Cash"]').hide();
				$('#divType option[value="By Reinvestment"]').hide();
				$('#divType option[value="By Redemption"]').show();
				$('#divType option[value="Investor Option"]').show();
				fa.divType.enabled();
			} 
			if ((rgProduct.divByCash == true) && (rgProduct.divByReinvest == true) && (rgProduct.divByRedeem == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Cash', 'By Cash');
					options[options.length] = new Option('By Reinvestment', 'By Reinvestment');
					options[options.length] = new Option('By Redemption', 'By Redemption');
				}
				$('#divType option[value="By Cash"]').show();
				$('#divType option[value="By Reinvestment"]').show();
				$('#divType option[value="By Redemption"]').show();
				$('#divType option[value="Investor Option"]').hide();
				fa.divType.enabled();
			} 
			if ((rgProduct.divByCash == true) && (rgProduct.divByReinvest == true) && (rgProduct.divInvestorOpt == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Cash', 'By Cash');
					options[options.length] = new Option('By Reinvestment', 'By Reinvestment');
					options[options.length] = new Option('Investor Option', 'Investor Option');
				}
				$('#divType option[value="By Cash"]').show();
				$('#divType option[value="By Reinvestment"]').show();
				$('#divType option[value="By Redemption"]').hide();
				$('#divType option[value="Investor Option"]').show();
				fa.divType.enabled();
			} 
			if ((rgProduct.divByCash == true) && (rgProduct.divByRedeem == true) && (rgProduct.divInvestorOpt == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Cash', 'By Cash');
					options[options.length] = new Option('By Redemption', 'By Redemption');
					options[options.length] = new Option('Investor Option', 'Investor Option');
				}
				$('#divType option[value="By Cash"]').show();
				$('#divType option[value="By Reinvestment"]').hide();
				$('#divType option[value="By Redemption"]').show();
				$('#divType option[value="Investor Option"]').show();
				fa.divType.enabled();
			}
			if ((rgProduct.divByRedeem == true) && (rgProduct.divByReinvest == true) && (rgProduct.divInvestorOpt == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Reinvestment', 'By Reinvestment');
					options[options.length] = new Option('By Redemption', 'By Redemption');
					options[options.length] = new Option('Investor Option', 'Investor Option');
				}
				$('#divType option[value="By Cash"]').hide();
				$('#divType option[value="By Reinvestment"]').show();
				$('#divType option[value="By Redemption"]').show();
				$('#divType option[value="Investor Option"]').show();
				fa.divType.enabled();
			}
			if ((rgProduct.divByCash == true) && (rgProduct.divByReinvest == true) && (rgProduct.divByRedeem == true) && (rgProduct.divInvestorOpt == true)) {
				
				if($.browser.msie){
					$('#divType option').remove();
					var options = $('#divType').attr('options');
					options[options.length] = new Option('', '');
					options[options.length] = new Option('By Cash', 'By Cash');
					options[options.length] = new Option('By Reinvestment', 'By Reinvestment');
					options[options.length] = new Option('By Redemption', 'By Redemption');
					options[options.length] = new Option('Investor Option', 'Investor Option');
				}
				$('#divType option[value="By Cash"]').show();
				$('#divType option[value="By Reinvestment"]').show();
				$('#divType option[value="By Redemption"]').show();
				$('#divType option[value="Investor Option"]').show();
				fa.divType.enabled();
			}
			if ((rgProduct.divByCash == "") && (rgProduct.divByReinvest == "") && (rgProduct.divByRedeem == "") && (rgProduct.divInvestorOpt == "")) {
				
				fa.divType.disabled();
			}
			if($.browser.msie){
				fa.divType.val('${fa?.divType}');
			}
		}
		
		function totalUnitAndNetAmount() {
			var dividendLength = tableDividen.fnGetNodes().length;
//			alert("dividendLength "+dividendLength);
//			var sumUnit = 0;
//			var sumNetAmount = 0;
			var unit = 0;
			var netAmount = 0;
			var unitForGrid="";
			var netAmountForGrid="";
			if (dividendLength==0) {
				$("#tradeUnit").val("");
				$("#tradeNetAmount").val("");
			}
			var unitArrayDec = new Array(dividendLength);
			var unitArrayFront = new Array(dividendLength);
			var netAmountArrayDec = new Array(dividendLength);
			var netAmountArrayFront = new Array(dividendLength);
			var amountTot = 0;
			if (jQuery.trim($('#divBy').val())=='${totAmount}'){
				digitAmount = fa.roundValue.val();
				typeAmount = fa.roundType.val();
			}
			var trueTradeUnit = 0;
			var trueTradeNetAmount = 0;
			for (var i = 0 ;i < dividendLength; i++ ) {
				var data = tableDividen.fnGetData(i);
				unit = (data[6]).toNumber(",");
//				alert("sumUnit = " + data[7] + " sumNetAmount = " + data[9]);
				netAmount = (data[4]).toNumber(",");
				amountTot = amountTot+(data[4]).toNumber(",");
				if (data[6] == undefined) {
					unit = 0;
				}
				if (data[4] == undefined) {
					netAmount = 0;
				}	
				
				unitForGrid = fa.dummy.valueRnd(unit, true, digitUnit, typeUnit);
				unit = unitForGrid.val().toNumber(",");
				/*var unitFullValue = unitForGrid.val().split(".");
				unitArrayDec[i] =  unitFullValue[1];;
				unitArrayFront[i] =  unitFullValue[0].toNumber(",");*/
				netAmountForGrid = fa.dummy.valueRnd(netAmount, true, digitAmount, typeAmount);
				
				netAmount = netAmountForGrid.val().toNumber(",");
				
				var trueTradeUnit = trueTradeUnit + unit;
				var trueTradeNetAmount = trueTradeNetAmount+netAmount;
				
				/*var netAmountFullValue = netAmountForGrid.val().split(".");
				
				var tempSplitDec = 0;
				var tempSplit2 = netAmountFullValue[0];
				
				if (netAmountFullValue.length > 1){
					tempSplitDec = netAmountFullValue[1];
//					tempSplit2 = netAmountFullValue[0];
				}
				
//				var netAmountFullValue = netAmount;
				netAmountArrayDec[i] = tempSplitDec;
				netAmountArrayFront[i] = tempSplit2.toNumber(",");*/
				
				
				/*netAmountForGrid = fa.dummy.valueRnd(netAmount, true, digitAmount, typeAmount);
				netAmount = netAmountForGrid.val().toNumber(",");
				var netAmountFullValue = netAmountForGrid.val().split(".");
				netAmountArrayDec[i] = netAmountFullValue[1];
				netAmountArrayFront[i] = netAmountFullValue[0].toNumber(",");*/
				
			}
			
			/*var unitSumDec = 0;
			var unitSumFront = 0;
//			var tradeUnit = $("#tradeUnit").val().split(".");
			var netAmountSumDec = 0;
			var netAmountSumFront = 0;
//			var tradeNetAmount = $("#tradeNetAmount").val().split(".");
			
			for(var i=0;i<unitArrayDec.length;i++){
				unitSumDec=unitSumDec+Number(unitArrayDec[i]);
				unitSumFront=unitSumFront+Number(unitArrayFront[i]);
			}
			
			for(var i=0;i<netAmountArrayDec.length;i++){
				
				netAmountSumDec=netAmountSumDec+Number(netAmountArrayDec[i]);
				netAmountSumFront=netAmountSumFront+Number(netAmountArrayFront[i]);
			}*/
//			console.debug("array : "+tradeUnit[0].toNumber(","));
//			console.debug("array : "+sumFront);
//			console.debug("array : "+formatDot(sumDec, digitUnit));
			
			/*var trueTradeUnit = parseFloat(unitSumFront)+ formatDot(unitSumDec, digitUnit);
			var trueTradeNetAmount = parseFloat(netAmountSumFront)+ formatDot(netAmountSumDec, digitAmount);*/
			
			if (dividendLength > 0) {
				
				if (jQuery.trim($('#divBy').val())=='${totAmount}'){
					digitAmount = fa.roundValue.val();
					typeAmount = fa.roundType.val();
//					$("#tradeNetAmount").autoNumericSet(trueTradeNetAmount, {aPad:true, mDec:digitAmount, mRound:typeAmount}).val();
//					$('#tradeNetAmount').autoNumericSet(trueTradeNetAmount, {aPad:true, mDec:$('#roundValue').val(), mRound:$('#roundType').val()}).val();
//					console.debug("tradeNetAmount tot : "+$('#tradeNetAmount').val());
				}else{
//					$('#tradeNetAmount').autoNumericSet(trueTradeNetAmount, {aPad:true, mDec:digitAmount, mRound:typeAmount}).val();
//					console.debug("tradeNetAmount  : "+$('#tradeNetAmount').val());
				}
				
//				$('#tradeNetAmount').autoNumericSet(amountTot, {aPad:true, mDec:digitAmount, mRound:typeAmount}).val();	
//				$("#tradeUnit").autoNumericSet(amountTot, {aPad:true,mDec: digitUnit, mRound:typeUnit}).val();
				console.debug("tradeNetAmount tot : "+$('#tradeNetAmount').val());
					
//				}
				$("#tradeUnit").autoNumericSet(trueTradeUnit, {aPad:true,mDec: digitUnit, mRound:typeUnit}).val();
				$("#tradeNetAmount").autoNumericSet(trueTradeNetAmount, {aPad:true, mDec:digitAmount, mRound:typeAmount}).val();
				// FOR CANCEL DIVIDEN //

				//FOR CANCELTRADE
				if('${from}' != ""){
					var totalAmountCancel = Number($('#tradeNetAmount').autoNumericGet());
					$('#totalAmountForCancel').val(totalAmountCancel);
				}
				
				if (trueTradeUnit == 0) {
					//$("#tradeUnit").val("");
					$("#tradeUnit").autoNumericSet(trueTradeUnit, {aPad:true, mDec:digitUnit, mRound:typeUnit}).val();
					//$("#tradeUnit").val(trueTradeUnit);
				}
				/*if (trueTradeNetAmount == 0) {
					$("#tradeNetAmount").val("");
				}*/
			}
			$("#totNetAmtDiv").val(amountTot);
		}
		
		function formatDot(pnumber,decimal){
//			fa.dummy.val(pnumber);
//			var test = fa.dummy.val().length - decimals;
//		    var front = fa.dummy.val().substring(0,test);
//		    var back = fa.dummy.val().substring(test,pnumber.length);
//		    var cek = front +"."+back;
			var div = 1;
			for (var i=0;i<decimal;i++) {
				div = div + "0";
			}
			console.debug("div : "+div);
			console.debug("pnumber : "+pnumber);
			var dec = pnumber/Number(div);
			console.debug("dec : "+dec);
		    return parseFloat(dec);
		}
		
		function cancelDateValidation(){
			/*var cancelPostDate = new Date(fa.cancelPostDate.datepicker('getDate')).getTime();
			var cancelDate = new Date(fa.cancelDate.datepicker('getDate')).getTime();
			var currentAppDate = new Date(fa.currentBusinessDate.datepicker('getDate')).getTime();
			var divDate = new Date(fa.divDate.datepicker('getDate')).getTime();
			
			if (((currentAppDate!='')&&(cancelDate!=''))||((divDate!='')&&(cancelDate!=''))||((cancelPostDate!='')&&(cancelDate!=''))){
				if (cancelDate > currentAppDate){
					$('#cancelDate').addClass('fieldError');
					$('#cancelDateError').html("Cannot be later than Application Date ("+$("#currentBusinessDate").val()+")");
					return false;
				} else if (cancelDate < divDate){
					$('#cancelDate').addClass('fieldError');
					$('#cancelDateError').html("Cannot be before than Cancelled Transaction Dividen Date");
					return false;
				} else if (cancelPostDate < cancelDate ){
					$('#cancelDate').addClass('fieldError');
					$('#cancelDateError').html("Cannot be later than Cancel Post Date");
					return false;
				} else {
					$('#cancelDate').removeClass('fieldError');
					$('#cancelDateError').html("");
					return true;
				}
			}del*/
		}
		
		function cancelPostDateValidation(){
			/*var cancelPostDate = new Date(fa.cancelPostDate.datepicker('getDate')).getTime();
			var cancelDate = new Date(fa.cancelDate.datepicker('getDate')).getTime();
			var postDate = new Date(fa.postDate.datepicker('getDate')).getTime();
			
			if (((cancelPostDate!='')&&(cancelDate!=''))||((postDate!='')&&(cancelPostDate!=''))){
				if (cancelPostDate < cancelDate ){
					$('#cancelPostDate').addClass('fieldError');
					$('#cancelPostDateError').html("Cannot be before than Cancel Date");
					return false;
				} else if (cancelPostDate < postDate ){
					$('#cancelPostDate').addClass('fieldError');
					$('#cancelPostDateError').html("Cannot be before than Cancelled Transaction Post Date");
					return false;
				} else {
					$('#cancelPostDate').removeClass('fieldError');
					$('#cancelPostDateError').html("");
					return true;
				}
			}del*/
			
		}
		
		function callDivBy(){
			if (jQuery.trim($('#divBy').val())=='${totAmount}'){
				$('.totalAmount').css('display','');
				$('.factor').css('display','none');
				$('.amountUnit').css('display','none');
				$('.roundTotAmount').css('display','');
//				$('#totAmount').val("");
			}else if (jQuery.trim($('#divBy').val())=='${factor}'){
				$('.factor').css('display','');
				$('.totalAmount').css('display','none');
				$('.amountUnit').css('display','none');
				$('.roundTotAmount').css('display','none');
//				$("p[id=amountUnit] label ").html("Factor");
//				$('#unitAmount').val("");
			}else if (jQuery.trim($('#divBy').val())=='${amountUnit}'){
				$('.amountUnit').css('display','');
				$('.factor').css('display','none');
				$('.totalAmount').css('display','none');
				$('.roundTotAmount').css('display','none');
				
//				$('#unitAmount').val("");
			}
		}
/* =========================================================================== 
 * Initialize
 * ========================================================================= */
//	alert($.browser.version);
	
	fa.divBy.children().eq(0).remove();
	if (fa.productCode.val().isEmpty()) {
		fa.divType.disabled();
	}
	
	if ($.browser.msie){
		fa.divType.val('${fa?.divType}');
		$("#remarks[maxlength]").bind('input propertychange', function() {
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    });
	}
	
	if (!fa.productCode.val().isEmpty()) {
		var rgProduct = html.getRgProduct(fa.productCode.val());
		typeAmount = rgProduct.amountRoundType;
		typeUnit = rgProduct.unitRoundType;
		typePrice = rgProduct.priceRoundType;
		digitAmount = rgProduct.amountRoundValue;
		digitUnit = rgProduct.unitRoundValue;
		digitPrice = rgProduct.priceRoundValue;
		
		divType(rgProduct);
	
		fa.divLock.val(rgProduct.divLock);
		//fa.effectiveDate.val(new Date(rgProduct.effectiveDate).format("dd/mm/yyyy"));
		//fa.liquidDate.val(new Date(rgProduct.liquidDate).format("dd/mm/yyyy"));
		fa.effectiveDate.val(rgProduct.effectiveDate);
		fa.liquidDate.val(rgProduct.liquidDate);
		fa.currency.val(rgProduct.currencyCode);
//		fa.divType.enabled();
	}
	
	fa.proccess.button();
	fa.proccess.css('width', '100px');
	fa.fundActionTab.tabs();
	fa.fundActionTab.css('height', '550px');
	html.clazz("calendar").datepicker();
	
//	formatRound(); PROGRAM SAMPAH DI MATIKAN OLEH ELVINO
//	totalUnitAndNetAmount();
	
	/*$("p[id=navDateP] label span").html("");
	$("p[id=postDateP] label span").html("");
	$("p[id=paymentDateP] label span").html("");*/
	if (fa.divType.val() == BY_CASH) {
//		$("p[id=navDateP] label span").html("");
//		$("p[id=postDateP] label span").html("");
//		$("p[id=paymentDateP] label span").html(" *");
	}
	if (fa.divType.val() == BY_REINVESTMENT) {
//		$("p[id=navDateP] label span").html(" *");
//		$("p[id=postDateP] label span").html(" *");
//		$("p[id=paymentDateP] label span").html("");
	}
	if (fa.divType.val() == BY_REDEMPTION) {
//		$("p[id=navDateP] label span").html(" *");
//		$("p[id=postDateP] label span").html(" *");
//		$("p[id=paymentDateP] label span").html(" *");
	}
	$("p[id=roundValueP] label span").html("");
	if (!(fa.roundType.val().isEmpty())) {
		$("p[id=roundValueP] label span").html(" *");
	}

	if ('${taskId}' == '' ) {
//		fa.allocateTotalAmount.attr("checked", true); --del
		$("input[name=fa.inputMethodBy]").val(ALLOCATE_TOTAL_AMOUNT);
//		fa.ratioPerUnitBy.val(TOTAL_AMOUNT); del
//		fa.ratioPerUnitByHidden.val(TOTAL_AMOUNT); del
//		fa.ratioPerUnitBy.disabled(); -del
//		fa.ratio.disabled(); --del
	}
	
	if ('${fa?.inputMethodBy}' == RATIO_PER_UNIT) {
		$("input[name=fa.inputMethodBy]").val(RATIO_PER_UNIT);
//		alert("1. " + isAllocate);
		isAllocate = false;
		fa.ratioPerUnit.attr("checked", true);
		if ('${fa?.inputBy}' == AMOUNT_PER_UNIT) {
//			fa.ratioPerUnitBy.val(AMOUNT_PER_UNIT); --del
//			fa.ratioPerUnitByHidden.val(AMOUNT_PER_UNIT); --del
		}
		if ('${fa?.inputBy}' == UNIT_PER_UNIT) {
//			fa.ratioPerUnitBy.val(UNIT_PER_UNIT); --del
//			fa.ratioPerUnitByHidden.val(UNIT_PER_UNIT);  -del
		}
		fa.amount.disabled();
//		fa.ratioPerUnitBy.enabled(); --del
		fa.ratio.enabled();
		$("p[id=ampuntP] label span").html("");
		fa.amount.val("");
		fa.amountStripped.val("");
	}
	
	fa.navDate.enabled();
	fa.postDate.enabled();
	
	if ('${confirming}' == 'true' || '${taskId}' != '' || '${calculated}' == 'true' || '${keyId}' != '') {
		fa.divType.disabled();
//		fa.ratioPerUnitBy.disabled(); --del
//		fa.ratio.disabled(); --del
		fa.navDate.disabled();
		fa.postDate.disabled();
		//fa.navDate.disabled();
		//fa.amount.disabled();
	}
	if ('${back}' == 'true'){
		$('.master').attr("disabled", true);
		fa.fundActionTab.tabs("select", "tab-2");
		/*var dividenGrid = $('#dividenGrid').dataTable();
		if (dividenGrid.fnGetNodes().length > 0) {
			fa.proccess.button("disable");
		}*/
		if ($("#pagingMain tbody tr").length > 0) fa.proccess.button("disable");
		//fa.pagingClearAll.removeClass('ui-state-disabled');
		fa.calculated.val(true);
		$("#dividenGrid").attr("style", "width=auto");
	}
	
	if (('${confirming}' == 'true' || '${calculated}' == 'true') && '${from}'=='') {
		$("#fundActionTab").tabs("select", "#tab-2");
	}
	
	if ('${taskId}' ) {
		/*if (fa.ratioPerUnitBy.val() == AMOUNT_PER_UNIT || fa.ratioPerUnitBy.val() == UNIT_PER_UNIT){
			fa.ratioPerUnit.attr("checked", true);
			isAllocate = false;
//			fa.total.enable();
		}del*/
	}
	
//	if ('${calculated}' == 'false') {
//	}
	
	if ($('#divBy').val()==""){
		
		/*'$(totAmount)'
		amountUnit*/
	}
	$('#divBy').change(function(){
		callDivBy();
		$('#amountStripped').val("");
	});
/* =========================================================================== 
 * Event
 * ========================================================================= */
		// === EVENT FOR CANCEL ==== //
		fa.remarkForCancel.change(function(){
			fa.remarkForCancelHidden.val(fa.remarkForCancel.val());
		});	
		
		var result;
		
		fa.roundValue.autoNumeric({vMin: '-3', vMax: '3'});
		fa.factorAmount.autoNumeric({vMax:'100', mDec:15});		
		cancelDateValidation();
		callDivBy();
		/*fa.cancelDate.change(function(){
			if (!fa.cancelDate.hasClass('fieldError')){
				result = cancelDateValidation();
				if (result) {
					fa.cancelDateHidden.val(fa.cancelDate.val());
				} else {
					fa.cancelDateHidden.val('');
					$('#h_cancelDate').val('');
				}
			} else {
				fa.cancelDateHidden.val('');
				$('#h_cancelDate').val('');
			}
			
		});del*/
		
		cancelPostDateValidation();
		/*fa.cancelPostDate.change(function(){
			if (!fa.cancelPostDate.hasClass('fieldError')){
				result = cancelPostDateValidation();
				if (result) {
					fa.cancelPostDateHidden.val(fa.cancelPostDate.val());
				} else {
					fa.cancelPostDateHidden.val('');
					$('#h_cancelPostDate').val('');
				}
			} else {
				fa.cancelPostDateHidden.val('');
				$('#h_cancelPostDate').val('');
			}
			
		});*/
		
		
	// ======================== //		
	
		fa.productCode.popupProduct("type", function(data){
			if (data) {
				rgProduct = data;
				
				typeAmount = rgProduct.amountRoundType;
				typeUnit = rgProduct.unitRoundType;
				typePrice = rgProduct.priceRoundType;
				digitAmount = rgProduct.amountRoundValue;
				digitUnit = rgProduct.unitRoundValue;
				digitPrice = rgProduct.priceRoundValue;
				
				fa.effectiveDate.val(data.effectiveDate);
				fa.liquidDate.val(data.liquidDate);
				fa.fixNav.val(rgProduct.fixnav);
				fa.fixNavPrice.val(rgProduct.fixNavPrice);
				calcRgProduct();
				if (fa.divType.val() == BY_REINVESTMENT) {
					fa.navDate.change();
				}

				if (fa.divType.val() == BY_CASH) {
					if(!'${confirming}' && '${mode}' != 'view') fa.postDate.val("");
				}

				divType(rgProduct);				
				fa.divLock.val(rgProduct.divLock);

				/*fa.effectiveDate.val(new Date(rgProduct.effectiveDate).format("mm/dd/yyyy"));
				fa.liquidDate.val(new Date(rgProduct.liquidDate).format("mm/dd/yyyy"));*/
				fa.effectiveDate.val(rgProduct.effectiveDate);
				fa.liquidDate.val(rgProduct.liquidDate);
				fa.cashPct.val(rgProduct.divIopByCashPct);
				fa.reinvestmentPct.val(rgProduct.divIopByReinvestmentPct);
				fa.redeemPct.val(rgProduct.divIopByRedeemPct);
				fa.currency.val(rgProduct.currencyCode);
				
				fa.amountRoundValue.val(digitAmount);
				fa.amountRoundType.val(typeAmount);
				fa.unitRoundValue.val(digitUnit);
				fa.unitRoundType.val(typeUnit);
				fa.priceRoundValue.val(digitPrice);
				fa.priceRoundType.val(typePrice);
//				fa.divType.enabled();
			}
		});
		
		/*fa.taxCode.popupTax("roundType", function(data){
			rgTax = html.getRgTax(fa.taxCode.val());
			fa.taxRate.val(rgTax.taxRate);
			calcRgTax();
		});del*/
		
		/*fa.amount.change(function(){
			var value = fa.amount.val();
			fa.amount.valueRnd(value.toNumber(","), true, digitAmount, typeAmount);
			calcRgTax();
		});del*/
		
		/*fa.ratio.change(function(){
			calcRgTax();
		});del*/
		
		fa.productCode.change(function(){
			if (fa.productCode.val() == "") {
				fa.divType.disabled();
				fa.divType.val("");
//				fa.navDate.disabled("");
//				fa.postDate.disabled("");
//				fa.paymentDate.disabled("");
//				$("p[id=navDateP] label span").html(" *");
//				$("p[id=postDateP] label span").html(" *");
//				$("p[id=paymentDateP] label span").html(" *");
			}
		});

		fa.divType.change(function(){
			typeChangeView();
		});

		fa.divDate.change(function(){
			calcRgProduct();
			postDate();
		});

		fa.cumDate.change(function(){
			// #4061 Elvino untuk mencegah parseDate, krn untuk pertama kali data kosong
			if (!fa.productCode.isEmpty() && !fa.cumDate.isEmpty()) {
				var totalUnit1 = html.getPortfolioTotalUnit(fa.productCode.val(), fa.cumDate.val().fmtYYYYMMDD("/"));
				console.debug("totalUnit1 from rgNav => " + totalUnit1);
				fa.totalUnit.value("");
				var totalUnit2 = (html.getUnitFromDwnBal(fa.productCode.val(), fa.cumDate.val().fmtYYYYMMDD("/")));
				console.debug("totalUnit2 from vw_trx_unitreg => " + totalUnit2);
				if (totalUnit1 != null || totalUnit2 != null) {
					var totalUnit = totalUnit1 + totalUnit2; 
					fa.totalUnit.valueRnd(totalUnit, true, digitUnit, typeUnit);
				}
			}
		});

		fa.postDate.change(function(){
			postDate();
		});

		fa.unitAmount.change(function(){
			
		});

		fa.totAmount.change(function(){
			$('#totAmount').valueRnd($('#totAmount').val(),true, $('#amountRoundValue').val(), $('#amountRoundType').val());
		});

		fa.navDate.change(function(){
			if (!fa.productCode.isEmpty() && !fa.navDate.isEmpty()) {
				if (fa.fixNav.val() == true || fa.fixNav.val() =='true'){
					fa.price.valueRnd(fa.fixNavPrice.val(), true, digitPrice, typePrice);
				}else{
					rgNav = html.getRgNav(fa.navDate.val().fmtYYYYMMDD("/"), fa.productCode.val());
					fa.price.value("");
					if (rgNav) {
						fa.price.valueRnd(rgNav.nav, true, digitPrice, typePrice);
					}
				}	
			}
		});

		typeChangeView();
		calcRgTax();
		
//		fa.inputBy.change(function(){
//			var inputBy =  fa.inputBy.val();
//			
//			if (inputBy == TOTAL_AMOUNT) {
//				fa.amount.enabled();
//				fa.amountPerUnit.disabled();
//			}
//			if (inputBy == AMOUNT_PER_UNIT) {
//				fa.amount.disabled();
//				fa.amountPerUnit.enabled();
//			}
//		});
		/*fa.allocateTotalAmount.click(function(){
			isAllocate = true;
			fa.allocateTotalAmount.val(ALLOCATE_TOTAL_AMOUNT);
			var inputMethodBy =  fa.allocateTotalAmount.val();
			if (inputMethodBy == ALLOCATE_TOTAL_AMOUNT) {
				fa.allocateTotalAmount.val(ALLOCATE_TOTAL_AMOUNT);
//				fa.ratioPerUnitBy.val(TOTAL_AMOUNT); del
				fa.ratioPerUnitByHidden.val(fa.ratioPerUnitBy.val());
				fa.amount.enabled();
//				fa.amountPerUnit.disabled();
				fa.ratioPerUnitBy.disabled();
//				fa.ratio.disabled(); --del
			}
//			tableDividen.fnClearTable();
			$("input[name=fa.inputMethodBy]").val(ALLOCATE_TOTAL_AMOUNT);
			fa.ratioPerUnit.val("");
			fa.ratio.val("");
			fa.ratioStripped.val("");
			fa.total.val("");
			fa.totalStripped.val("");
			fa.netAmount.val("");
			fa.netAmountStripped.val("");
			$("p[id=ampuntP] label span").html(" *");
		});
		*/
		/*fa.ratioPerUnit.click(function(){
			isAllocate = false;
			fa.ratioPerUnit.val(RATIO_PER_UNIT);
			var inputMethodBy =  fa.ratioPerUnit.val();
			if (inputMethodBy == RATIO_PER_UNIT) {
				fa.ratioPerUnitBy.val(AMOUNT_PER_UNIT);
				fa.ratioPerUnitByHidden.val(fa.ratioPerUnitBy.val());
				fa.amount.disabled();
//				fa.amountPerUnit.enabled();
				fa.ratioPerUnitBy.enabled();
				fa.ratio.enabled();
			}
//			tableDividen.fnClearTable();
			$("input[name=fa.inputMethodBy]").val(RATIO_PER_UNIT);
			fa.allocateTotalAmount.val("");
			fa.amount.val("");
			fa.amountStripped.val("");
			fa.amountPerUnit.val("");
			fa.amountPerUnitStripped.val("");
			fa.netAmount.val("");
			fa.netAmountStripped.val("");
			$("p[id=ampuntP] label span").html("");
		});*/
		
		/*fa.ratioPerUnitBy.change(function(){
			fa.ratio.val("");
			fa.total.val("");
			if (fa.ratioPerUnitBy.val() == AMOUNT_PER_UNIT) {
				fa.ratioPerUnitBy.val(AMOUNT_PER_UNIT);
				fa.ratioPerUnitByHidden.val(fa.ratioPerUnitBy.val());
			}
			if (fa.ratioPerUnitBy.val() == UNIT_PER_UNIT) {
				fa.ratioPerUnitBy.val(UNIT_PER_UNIT);
				fa.ratioPerUnitByHidden.val(fa.ratioPerUnitBy.val());
			}
		});*/
		
		
		fa.roundValue.live('blur', function() {
//			var el = $(this);
//			if (el.val() == '') {
//				return;
//			}
//		});
			var el = $(this);
			var id = this.id;
			var stripped = "#" + id + "Stripped";
			if (el.val() == '') {
				el.siblings(stripped).val('');
				return;
			}
			el.siblings(stripped).val(el.autoNumericGet());
		});
		
		$('input.numAmount').autoNumeric({vMax:'999999999999999.', mDec:12});
		$('input.numAmount').live('blur', function() {
			var el = $(this);
			var id = this.id;
			var stripped = "#" + id + "Stripped";
			if (el.val() == '') {
				el.siblings(stripped).val('');
				return;
			}
			el.siblings(stripped).val(el.autoNumericGet());
		});

		function validateDistribute(){
			
		}

		fa.proccess.click(function(){
			fa.paymentDate.removeClass('fieldError');
			$(totalUnitError).html("");
			$(priceError).html("");
			$(roundTypeError).html("");
			$(roundValueError).html("");
			$(ratioError).html("");

//			validateDistribute();
			if ((fa.totalUnit.val() == "") || ((fa.postDate.val() == fa.divDate.val()) && (fa.price.val() == ""))  ||  (fa.divBy.val() == "") || (fa.divDate.val() == "") ||
					((fa.totAmount.val() == "") && (jQuery.trim(fa.divBy.val()) == '${totAmount}')) ||
					((fa.factorAmount.val() == "") && (jQuery.trim(fa.divBy.val()) == '${factor}')) ||
					((fa.unitAmount.val() == "") && (jQuery.trim(fa.divBy.val()) == '${amountUnit}')) ||
					((fa.roundType.val()=="") && (jQuery.trim(fa.divBy.val()) == '${totAmount}')) ||
					((fa.roundValueStripped.val()=="") && (jQuery.trim(fa.divBy.val()) == '${totAmount}')) ||
					((fa.roundType.val() != "") && (fa.roundValueStripped.val() == "")) ||
					(fa.productCode.val() == "") || (fa.divDate.val() == "") || (fa.divType.val() == "") ||
					(fa.cumDate.val() == "") || (fa.navDate.val() == "") || (fa.postDate.val() == "") ||
					(fa.paymentDate.isEmpty())
					){

				if(fa.paymentDate.isEmpty()){
					$(paymentDateError).html(" Required");
				} else {
					$(paymentDateError).html("");
				}

				if (fa.totalUnit.val() == "") {
					$(totalUnitError).html(" Required");
				}else{
					$(totalUnitError).html("");
				}

				if ((fa.postDate.val() == fa.divDate.val()) && (fa.price.val() == "")) {
					$(priceError).html(" Required");
				} else {
					$(priceError).html("");
				}

				if (fa.divBy.val() == ""){
					$(divByError).html(" Required");
				}else{
					$(divByError).html("");
				}
				if (fa.divDate.val() == ""){
					$(divDateError).html(" Required");
				}else{
					$(divDateError).html("");
				}
				if ((fa.totAmount.val() == "") && (jQuery.trim(fa.divBy.val()) == '${totAmount}')){
					$(totalAmountError).html(" Required");
				}else{
					$(totalAmountError).html("");
				}
				if ((fa.factorAmount.val() == "") && (jQuery.trim(fa.divBy.val()) == '${factor}')){
					$(factorAmountError).html(" Required");		
				}else{
					$(factorAmountError).html("");		
				}
				if ((fa.unitAmount.val() == "") && (jQuery.trim(fa.divBy.val()) == '${amountUnit}')){
					$(unitAmountError).html(" Required");
				}else{
					$(unitAmountError).html("");
				}
				if ((fa.roundType.val()=="") && (jQuery.trim(fa.divBy.val()) == '${totAmount}')){
					$(roundTypeError).html(" Required");
				}else{
					$(roundTypeError).html("");
				}
				if ((fa.roundValueStripped.val()=="") && (jQuery.trim(fa.divBy.val()) == '${totAmount}')){
					$(roundValueError).html(" Required");
				}else{
					$(roundValueError).html("");
				}
				if ((fa.roundType.val() != "") && (fa.roundValue.val() == "")) {
					$(roundValueError).html(" Required");
				}else{
					$(roundValueError).html("");
				}
					
				if (fa.productCode.val() == ""){
					$(productCodeError).html(" Required");
				}else{
					$(productCodeError).html("");
				}
				if (fa.divDate.val() == ""){
					$(divDateError).html(" Required");
				}else{
					$(divDateError).html("");
				}
				if (fa.divType.val() == ""){
					$(divTypeError).html(" Required");
				}else{
					$(divTypeError).html("");
				}
				if (fa.cumDate.val() == ""){
					$(cumDateError).html(" Required");
				}else{
					$(cumDateError).html("");
				}
				if (fa.navDate.val() == ""){
					$(navDateError).html(" Required");
				}else{
					$(navDateError).html("");
				}
				if (fa.postDate.val() == ""){
					$(postDateError).html(" Required");
				}else{
					$(postDateError).html("");
				}
				return false;
			} else {
				$(totalUnitError).html("");
				$(priceError).html("");
				$(divByError).html("");
				$(divDateError).html("");
				$(totalAmountError).html("");
				$(factorAmountError).html("");				
				$(unitAmountError).html("");
				$(roundTypeError).html("");
				$(roundValueError).html("");
				$(productCodeError).html("");
				$(divDateError).html("");
				$(divTypeError).html("");
				$(cumDateError).html("");
				$(navDateError).html("");
				$(postDateError).html("");
				$(paymentDateError).html("");
			}

			var cumDate = new Date(fa.cumDate.datepicker('getDate')).getTime();
			var navDate = new Date(fa.navDate.datepicker('getDate')).getTime();
			var divDate = new Date(fa.divDate.datepicker('getDate')).getTime();
			var paymentDate = new Date(fa.paymentDate.datepicker('getDate')).getTime();
			var currentAppDate = new Date(fa.currentBusinessDate.datepicker('getDate')).getTime();
			var postDate = new Date(fa.postDate.datepicker('getDate')).getTime();

			//Validasi dimana Cum Date <= NAV Date <= Ex Date <= Posting date <= Payment Date
			$(cumDateError).html("");
			$(navDateError).html("");
			$(postDateError).html("");
			$(paymentDateError).html("");
			if ((cumDate > navDate) ||  (navDate > divDate) || (divDate > postDate) || (postDate > paymentDate) ){
				if (cumDate > navDate) $(cumDateError).html("Can not bigger than Nav Date");
				if (navDate > divDate) $(navDateError).html("Can not bigger than Ex Date");
				if (divDate > postDate) $(divDateError).html("Can not bigger than Posting Date");
				if (postDate > paymentDate) $(postDateError).html("Can not bigger than Payment Date");
				return false;
			} else {
				$(divDateError).html("");
				$(cumDateError).html("");
				$(navDateError).html("");
				$(navDateError).html("");
			}

			fa.paymentDate.enabled();
			fa.postDate.enabled();
			fa.navDate.enabled();
			var populate = "@{data()}";
			$.get(populate, fa.registryFundActionForm.serialize(), function(data) {
				checkRedirect(data);
				$("#tab-2").html(data);
				$('.master').attr("disabled", true);
				fa.fundActionTab.tabs("select", "tab-2");
				//var dividenGrid = $('#dividenGrid').dataTable();
				//if (dividenGrid.fnGetNodes().length > 0) fa.proccess.button("disable");
				if ($("#pagingMain tbody tr").length > 0) fa.proccess.button("disable");

				fa.calculated.val(true);
				$("#dividenGrid").attr("style", "width=auto");
			});

//			fa.calculate.val(true);
//			formatRound(); PRGRAM SAMPAH DI MATIKAN OLEH ELVINO
//			totalUnitAndNetAmount();
			return false;
		});

		fa.roundType.change(function(){
			$("p[id=roundValueP] label span").html("");
			if (!(fa.roundType.val().isEmpty())) {
				$("p[id=roundValueP] label span").html(" *");
			}
		});

		if (fa.dispatch.val() == "confirm") alert("Data Fund Action save with id "+fa.fundActionKey.val());		
		if (fa.dispatch.val() == "confirm") location.href='@{list()}';

		/*var valuePrice = fa.price.val();
		var valueAmount = fa.amount.val();
		var valueTotalUnit = fa.totalUnit.val();
		if (fa.price.val()!= 0){
			fa.price.valueRnd(valuePrice.toNumber(","), true, digitPrice, typePrice);
		}
		if (fa.amount.val()!= 0){
			fa.amount.valueRnd(valueAmount.toNumber(","), true, digitAmount, typeAmount);
		}
		if (fa.totalUnit.val()!=0){
			fa.totalUnit.valueRnd(valueTotalUnit.toNumber(","), true, digitUnit, typeUnit);
		}*/

		postDate();
		
		// #4061 Elvino untuk mencegah validasi muncul saat click new data
		if ('${mode}' == 'entry' && fa.cumDate.isEmpty()) {
		}else{ fa.cumDate.change(); }
		
		
		// #4061 Elvino untuk mencegah validasi muncul saat click new data
		if ('${mode}' == 'entry' && fa.navDate.isEmpty()) {
		}else{ fa.navDate.change(); }
		
	} else {	
		return new FundAction(html);
	}
}