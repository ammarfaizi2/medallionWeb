function Nav(html) {
	if (this instanceof Nav) {
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var nav = html.inject(this);
		
		var digitAmount;
		var typeAmount;
		var digitUnit;
		var typeUnit;
		var minNavAmt;
		
		var navPerUnitError = $("#navPerUnitErrorMessage");
		var navPerUnitErr = $("#navPerUnitErrMasg");
		
/* =========================================================================== 
 * Method Ajax Call
 * ========================================================================= */
		
/* =========================================================================== 
 * Method
 * ========================================================================= */
		/*function defaulCheckBidOffer() { */
//			if (('${confirming}'!='true')&&('${mode}'!='view')){
//				alert("ddd" +nav.checkBidOffer.val());
//				/*if (nav.checkBidOffer.val()=='true') {
//					alert("ddfdf");
//					nav.offerPct.enabled();
//					nav.bidPct.enabled();
//				}*/
//			}
			
			/*if (('${mode}'=='view')||('${mode}'=='edit')){
				if ((!nav.offerPct.isEmpty()) && (!nav.bidPct.isEmpty())){
					nav.checkBidOffer.attr('checked', true);
				}else {
					if ((!nav.offer.isEmpty()) && (!nav.bid.isEmpty())) {
						nav.checkBidOffer.enabled();
					} else {
						nav.checkBidOffer.disabled();
					}
					nav.offerPct.disabled();
					nav.bidPct.disabled();
				} 
			}
		}*/
		function getTotalUnit() {
			if (!nav.fundCode.isEmpty() && !nav.navDate.isEmpty()) {
				var totalUnit = html.getPortfolioTotalUnit(nav.fundCode.val(), nav.navDate.val().fmtYYYYMMDD("/"));
				
				if (totalUnit == null) totalUnit = 0;
				nav.totalUnit.value(totalUnit, true, digitUnit, typeUnit);
				
				var navperUnit = nav.navPerUnit.val().toNumber(",");
				var navAmount = totalUnit*navperUnit;
				nav.navAmount.value(navAmount, true, digitAmount, typeAmount);
				
			}
		}
		
		function checkNAVDate() {
			$("#navDateError").html('');
			if (nav.navDate.val() != "") {
				var navDateParam = new Date(nav.navDate.datepicker('getDate'));
				nav.navDate.removeClass('fieldError');
				var effectiveDate = new Date(nav.effectiveDate.datepicker('getDate'));
				var liquidDate = new Date(nav.liquidDate.datepicker('getDate'));
				if (!((navDateParam.getTime() >= effectiveDate.getTime()) && (navDateParam.getTime() <= liquidDate.getTime()))) {
					nav.navDate.addClass('fieldError');
	//					$("#navDateError").html('Invalid Date');
					$("#navDateError").html('Must be between Effective Date ('+nav.effectiveDate.val()+') and Liquid Date ('+nav.liquidDate.val()+')');
				} 
			}
		}
		
		/*function getOfferAndBidAmt(){
			if ((!nav.offerPct.isEmpty() || !nav.bidPct.isEmpty())&&(!nav.navPerUnit.isEmpty())) {
				var navperUnit = nav.navPerUnit.val().toNumber(",");
				if (!nav.offerPct.isEmpty() && (!nav.navPerUnit.isEmpty())) {
					var amountOffer = (1+((nav.offerPct.val())/100))*navperUnit;
					nav.offerAmt.value(amountOffer, true, digitAmount, typeAmount);
				}
					
				if (!nav.bidPct.isEmpty() && (!nav.navPerUnit.isEmpty())) {
					var bidOffer = (1+((nav.bidPct.val())/100))*navperUnit;
					nav.bidAmt.value(bidOffer, true, digitAmount, typeAmount);
				}
				
			} else {
				nav.offerAmt.value(0, true, digitAmount, typeAmount);
				nav.bidAmt.value(0, true, digitAmount, typeAmount);
			}
		}*/
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		if (!nav.fundCode.val().isEmpty()) {
			var rgProduct =  html.getRgProduct(nav.fundCode.val());
			digitAmount = rgProduct.amountRoundValue;
			typeAmount = rgProduct.amountRoundType;
			digitUnit = rgProduct.unitRoundValue;
			typeUnit = rgProduct.unitRoundType;
			minNavAmt = rgProduct.minNavAmt;
		}
		
		html.clazz('calendar').datepicker();
		
//		nav.fundCode.popupProduct("navDate", function(data){
		nav.fundCode.popupProductByEffLiqDate("navPerUnit", function(data){
			if (data) {
				nav.fundCode.removeClass('fieldError');
				nav.fundCodeId.val(data.productCode);
				nav.effectiveDate.val(data.effectiveDateForNav);
				nav.liquidDate.val(data.liquidDateForNav);
				getTotalUnit();
				checkNAVDate();
				//nav.navDate.val('');
				//nav.offerPct.val(data.offerPricePct);
				//nav.offerPctStripped.val(nav.offerPct.val());
				//nav.bidPct.val(data.bidPricePct);
				//nav.bidPctStripped.val(nav.bidPct.val());
				//nav.offer.val(data.offerPricePct);
				//nav.bid.val(data.bidPricePct);
				nav.currency.val(data.currencyCode);
				nav.currencyHidden.val(data.currencyCode);
				/*if (!nav.offerPct.isEmpty() || !nav.bidPct.isEmpty()) {
					nav.checkBidOffer.attr('disabled', false);
					nav.checkBidOffer.attr('checked', true);
					nav.checkBidOffer.val('true');
					nav.offerPct.enabled();
					nav.bidPct.enabled();
					$("#reqOffer").html(" *");
					$("#reqBid").html(" *");
					//getOfferAndBidAmt();
				} else {
					nav.checkBidOffer.attr('disabled', 'disabled');
					nav.checkBidOffer.attr('checked', false);
					nav.checkBidOffer.val('false');
					nav.offerPct.disabled();
					nav.bidPct.disabled();
					$("#reqOffer").html("");
					$("#reqBid").html("");
					//getOfferAndBidAmt();
				}*/
				
				digitAmount = data.amountRoundValue;
				typeAmount = data.amountRoundType;
				digitUnit = data.unitRoundValue;
				typeUnit = data.unitRoundType;
				minNavAmt = data.minNavAmt;
				nav.minNavAmt.val(minNavAmt);
			}
		},
		function(data){
			nav.fieldErrorfundCodaddClass('');
			nav.fundCodeDesc.val('NOT FOUND');
			nav.fundCode.val('');
			nav.fundCodeId.val('');
			nav.h_fundCodeDesc.val('');
			nav.currency.val('');
			nav.currencyHidden.val('');
		});
		
		nav.navDate.change(function() {
			if (!nav.fundCode.isEmpty()){
				if (!($('#navDate').hasClass('fieldError'))){
					//alert("effective Date get Time = " +effectiveDate.getTime());
					checkNAVDate();
					
					getTotalUnit();
				}
			}
		});
		
		nav.navPerUnit.change(function(){
			var totalUnit = nav.totalUnit.val().toNumber(",");
			var navperUnit = nav.navPerUnit.val().toNumber(",");
			
			console.debug("digitAmount = " + digitAmount + " typeAmount = " + typeAmount);
			var navAmount = totalUnit*navperUnit;
			nav.navAmount.value(navAmount, true, digitAmount, typeAmount);
			
			if (nav.navPerUnit.val() == 0) {
				navPerUnitErr.html("");
				nav.navPerUnit.addClass('fieldError');
				navPerUnitError.html("can not filled 0");
				return false;
			} else {
				nav.navPerUnit.removeClass('fieldError');
				navPerUnitError.html("");
			}
			
			//getOfferAndBidAmt();
		});
		
		/*nav.checkBidOffer.change(function(){
			if (nav.checkBidOffer.is(':checked')){
				nav.checkBidOffer.val(true);
				nav.offerPct.enabled();
				nav.bidPct.enabled();
				$("#reqOffer").html(" *");
				$("#reqBid").html(" *");
			} else {
				nav.checkBidOffer.val(false);
				nav.offerPct.disabled();
				nav.bidPct.disabled();
				nav.offerPct.val('');
				nav.offerPctStripped.val('');
				nav.bidPct.val('');
				nav.bidPctStripped.val('');
				$("#reqOffer").html("");
				$("#reqBid").html("");
				//getOfferAndBidAmt();
			}
		});*/
		
		//nav.offerPct.blur(function(){
		//	nav.offerPctStripped.val(nav.offerPct.val());
		//	getOfferAndBidAmt();
		//});
		
		//nav.bidPct.blur(function(){
		//	nav.bidPctStripped.val(nav.bidPct.val());
		//	getOfferAndBidAmt();
		//});
		
		//nav.offerPct.autoNumeric({vMax: '100'});
		//nav.offerPct.live('blur', function() {
		//	var el = $(this);
		//	if (el.val() == '') {
		//		return;
		//	}
		//});
		
		//nav.bidPct.autoNumeric({vMax: '100'});
		//nav.bidPct.live('blur', function() {
		//	var el = $(this);
		//	if (el.val() == '') {
		//		return;
		//	}
		//});
		
		getTotalUnit();
		//getOfferAndBidAmt();
		//defaulCheckBidOffer();
		
	}else{
		return new Nav(html);
	}
}
	