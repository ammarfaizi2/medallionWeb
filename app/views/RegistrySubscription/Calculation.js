(function(jQuery){
	var TRANSACTION_BY_UNIT = "TRANSACTION_BY_UNIT";
	var TRANSACTION_BY_AMOUNT = "TRANSACTION_BY_AMOUNT";
	
	var INPUT_BY_GROSS = "INPUT_BY_GROSS";
	var INPUT_BY_NET = "INPUT_BY_NET";
	
	var TIER_COMPARISON_TYPE_PROGRESSIVE = "TIER_COMPARISON_TYPE-PROGRESSIVE";
	var TIER_COMPARISON_TYPE_FLAT = "TIER_COMPARISON_TYPE-FLAT";
	
	// Fee Amount
	jQuery.fn.calcRedFeeAmount = function(netAmountTag, feePctTag, tradeBy, mDecimal, mRound) { //OnServer Tested
		var netAmount = netAmountTag.val().toNumber(",");
		var feePct = feePctTag.val().toNumber(",");
		
		var feeAmt = $().calc("@{Calc.calcRedFeeAmount()}", {"feePct":feePct, "tradeBy":tradeBy.val(), "netAmountTag":netAmountTag.val(), "netAmount":netAmount});
		if (feeAmt == null) { $(this).valueRnd("", true, mDecimal, mRound);
		}else $(this).valueRnd(feeAmt, true, mDecimal, mRound);

//		if (feePct != 0) {
//			var feeAmt = 0;
//			if (tradeBy.val() == TRANSACTION_BY_AMOUNT) {
//				if (netAmountTag.isEmpty()) {
//					$(this).value("");
//				}else{
//					feeAmt = (feePct/100) * netAmount;
//					$(this).valueRnd(feeAmt, true, mDecimal, mRound);
//				}
//			}
//			if (tradeBy.val() == TRANSACTION_BY_UNIT) {
//				if (netAmountTag.isEmpty()) {
//					$(this).value("");
//				}else{
//					feeAmt = (feePct/100) * netAmount;
//					$(this).valueRnd(feeAmt, true, mDecimal, mRound);
//				}
//			}			
//		} else if (feePct == 0) {
//			$(this).valueRnd(0, true, mDecimal, mRound);
//		}
	};
	
	jQuery.fn.calcSubFeeAmount = function(amountTag, feePctTag, inputBy, mDecimal, mRound) { // OnServer Tested
		var amount = amountTag.val().toNumber(",");
		var feePct = feePctTag.val().toNumber(",");
		var inputByVal = (inputBy) ? inputBy.val() : INPUT_BY_GROSS;
		
		// block cause performance reason
//		var feeAmt = $().calc("@{Calc.calcSubFeeAmount()}", {"feePct":feePct, "inputByVal":inputByVal, "amount":amount});
//		$(this).valueRnd(feeAmt, true, mDecimal, mRound);
		
		if (feePct != 0) {
			var feeAmt = 0;
			if (inputByVal == INPUT_BY_GROSS) feeAmt = (feePct/100)/((feePct/100)+1) * amount;
			if (inputByVal == INPUT_BY_NET) feeAmt = (feePct/100) * amount;
			$(this).valueRnd(feeAmt, true, mDecimal, mRound);
		} else if (feePct == 0) {
			$(this).valueRnd(0, true, mDecimal, mRound);
		}	
	};
	
	
	// Discount Amount
	jQuery.fn.calcRedDiscAmount = function(feeAmountTag, discPctTag, mDecimal, mRound) { //OnServer
		var feeAmount = feeAmountTag.val().toNumber(",");
		var discPct = discPctTag.val().toNumber(",");
		
		// block cause performance reason
//		var discAmt = $().calc("@{Calc.calcRedDiscAmount()}", {"feeAmountTag":feeAmountTag.val(), "discPct":discPct, "feeAmount":feeAmount});
//		if (discAmt == null) $(this).valueRnd("", true, mDecimal, mRound);
//		else $(this).valueRnd(discAmt, true, mDecimal, mRound);
		
		if (feeAmountTag.isEmpty()) {
			$(this).value("");
		}else{
			if (discPct != 0 && feeAmount != 0) {
				var discAmt = (discPct/100) * feeAmount * -1;
				$(this).valueRnd(discAmt, true, mDecimal, mRound);
			} else if (discPct == 0) {
				$(this).valueRnd(0, true, mDecimal, mRound);
			} else 	if (feeAmount == 0) { $(this).valueRnd("", true, mDecimal, mRound); }
		}
	};
	
	jQuery.fn.calcSubDiscAmount = function(feeAmountTag, discPctTag, mDecimal, mRound) { //OnServer Tested
		var feeAmount = feeAmountTag.val().toNumber(",");
		var discPct = discPctTag.val().toNumber(",");
		
		// block cause performance reason
//		var discAmt = $().calc("@{Calc.calcSubDiscAmount()}", {"discPct":discPct, "feeAmount":feeAmount});
//		if (discAmt == null) $(this).valueRnd("", true, mDecimal, mRound);
//		else $(this).valueRnd(discAmt, true, mDecimal, mRound);
		
		if (discPct != 0 && feeAmount != 0) {
			var discAmt = (discPct/100) * feeAmount * -1;
			$(this).valueRnd(discAmt, true, mDecimal, mRound);
		} else if (discPct == 0){
			$(this).valueRnd(0, true, mDecimal, mRound);
		} else 	if (feeAmount == 0) { 
			$(this).valueRnd("", true, mDecimal, mRound); 
		}
	};
	
	// Other Amount
	jQuery.fn.calcRedOtherAmount = function(netAmountTag, otherPctTag, mDecimal, mRound) { //OnServer Tested
		var netAmount = netAmountTag.val().toNumber(",");
		var otherPct = otherPctTag.val().toNumber(",");
		
		// block cause performance reason
//		var otherAmout = $().calc("@{Calc.calcRedOtherAmount()}", {"netAmountTag":netAmountTag.val(), "otherPct":otherPct, "netAmount":netAmount});
//		$(this).valueRnd(otherAmout, true, mDecimal, mRound); 
		
		if (netAmountTag.isEmpty()) {
			$(this).valueRnd(0, true, mDecimal, mRound);
		}else{
			if (otherPct != 0) {
				var otherAmout =  (otherPct/100) * netAmount;
				$(this).valueRnd(otherAmout, true, mDecimal, mRound);
			} else {
				$(this).valueRnd(0, true, mDecimal, mRound);
			}
		}
	};
	
	jQuery.fn.calcSubOtherAmount = function(amountTag, otherPctTag, inputBy, mDecimal, mRound) { //OnServer Tested
		var amount = amountTag.val().toNumber(",");
		var otherPct = otherPctTag.val().toNumber(",");
		var inputByVal = (inputBy) ? inputBy.val() : INPUT_BY_GROSS;
		
		// block cause performance reason
		var otherAmount = $().calc("@{Calc.calcSubOtherAmount()}", {"otherPct":otherPct, "inputByVal":inputByVal, "amount":amount});
		$(this).valueRnd(otherAmount, true, mDecimal, mRound);

		if (otherPct != 0) {
			var otherAmount = 0;
			if (inputByVal == INPUT_BY_GROSS) otherAmount = (otherPct/100)/((otherPct/100)+1) * amount;
			if (inputByVal == INPUT_BY_NET) otherAmount = (otherPct/100) * amount;
			$(this).valueRnd(otherAmount, true, mDecimal, mRound);
		} else {
			$(this).valueRnd(0, true, mDecimal, mRound);
		}
	};
	
	// Tax Amount
	jQuery.fn.calcRedTaxFeeAmount = function(taxFeePctTag, feeAmountTag, discAmountTag, otherAmount, mDecimal, mRound) { //OnServer Tested
		var taxFeePct = taxFeePctTag.val().toNumber(",");
		var feeAmount = feeAmountTag.val().toNumber(",");
		var discAmount = discAmountTag.val().toNumber(",");
		
		// block cause performance reason
//		var taxFeeAmount = $().calc("@{Calc.calcRedTaxFeeAmount()}", {"feeAmountTag":feeAmountTag.val(), "discAmountTag":discAmountTag.val(), "feeAmount":feeAmount, "discAmount":discAmount, "taxFeePct":taxFeePct});
//		if (taxFeeAmount == null) { $(this).value("");
//		}else{ $(this).valueRnd(taxFeeAmount, true, mDecimal, mRound); }

		
		if (feeAmountTag.isEmpty() || discAmountTag.isEmpty()) {
			$(this).value("");
		}else{
			//var taxFeeAmount = (taxFeePct/100) * (feeAmount + discAmount + otherAmount);
			var taxFeeAmount = ((feeAmount + discAmount) / ((taxFeePct/100) + 1)) * ((taxFeePct/100));
			$(this).valueRnd(taxFeeAmount, true, mDecimal, mRound);
		}
	};
	
	jQuery.fn.calcSubTaxFeeAmount = function(taxFeePctTag, feeAmountTag, discAmountTag, otherAmount, mDecimal, mRound) { //OnServer Tested
		var taxFeePct = taxFeePctTag.val().toNumber(",");
		var feeAmount = feeAmountTag.val().toNumber(",");
		var discAmount = discAmountTag.val().toNumber(",");

		// block cause performance reason
//		var taxFeeAmount = $().calc("@{Calc.calcSubTaxFeeAmount()}", {"feeAmount":feeAmount, "discAmount":discAmount, "taxFeePct":taxFeePct});
//		$(this).valueRnd(taxFeeAmount, true, mDecimal, mRound);
		
		var taxFeeAmount = ((feeAmount + discAmount) / ((taxFeePct/100) + 1)) * ((taxFeePct/100));
		$(this).valueRnd(taxFeeAmount, true, mDecimal, mRound);
	};
	
	// Total Fee Amount
	jQuery.fn.calcRedTotalFeeAmount = function(feeAmountTag, discAmountTag, otherAmountTag, taxFeeAmountTag, mDecimal, mRound) { //OnServer Tested
		var feeAmount = feeAmountTag.val().toNumber(",");
		var discAmount = discAmountTag.val().toNumber(",");
		var otherAmount = otherAmountTag.val().toNumber(",");
		
		// block cause performance reason
//		var totFeeAmount = $().calc("@{Calc.calcRedTotalFeeAmount()}", {"feeAmount":feeAmount, "discAmount":discAmount, "otherAmount":otherAmount});
//		$(this).valueRnd(totFeeAmount, true, mDecimal, mRound);

		var totFeeAmount = (feeAmount + discAmount + otherAmount);
		$(this).valueRnd(totFeeAmount, true, mDecimal, mRound);
	};
	
	jQuery.fn.calcSubTotalFeeAmount = function(feeAmountTag, discAmountTag, otherAmountTag, taxFeeAmountTag, mDecimal, mRound) { //OnSever Tested
		var feeAmount = feeAmountTag.val().toNumber(",");
		var discAmount = discAmountTag.val().toNumber(",");
		var otherAmount = otherAmountTag.val().toNumber(",");
		
		// block cause performance reason
//		var totFeeAmount = $().calc("@{Calc.calcSubTotalFeeAmount()}", {"feeAmount":feeAmount, "discAmount":discAmount, "otherAmount":otherAmount});
//		$(this).valueRnd(totFeeAmount, true, mDecimal, mRound);

		var totFeeAmount = (feeAmount + discAmount + otherAmount);
		$(this).valueRnd(totFeeAmount, true, mDecimal, mRound);
	};
	
	// Unit
	jQuery.fn.calcRedUnit = function(amountOrNetAmountTag, priceTag, tradeBy, mDecimal, mRound) { //OnServer Tested
		var amountOrNetAmount = amountOrNetAmountTag.val().toNumber(",");
		var price = priceTag.val().toNumber(",");
		
		// block cause performance reason
//		var unit = $().calc("@{Calc.calcRedUnit()}", {"tradeBy":tradeBy.val(), "price":price, "amountOrNetAmount":amountOrNetAmount});
//		if (unit == null) { $(this).value("", true);
//		}else{ $(this).valueRnd(unit, true, mDecimal, mRound); }
		
		if (tradeBy.val() == TRANSACTION_BY_AMOUNT) {
			if (price != 0 && amountOrNetAmount != 0) {
				$(this).valueRnd(amountOrNetAmount/price, true, mDecimal, mRound);
			}else {
				$(this).valueRnd("", true, mDecimal, mRound);
			}
		}
	};
	
	jQuery.fn.calcSubUnit = function(amountOrNetAmountTag, priceTag, inputBy, mDecimal, mRound) { //OnServer Tested
		var amountOrNetAmount = amountOrNetAmountTag.val().toNumber(",");
		var price = priceTag.val().toNumber(",");
		
		// block cause performance reason
//		var unit = $().calc("@{Calc.calcSubUnit()}", {"price":price, "amountOrNetAmount":amountOrNetAmount});
//		if (unit == null) { $(this).value("", true);	
//		}else{ $(this).valueRnd(unit, true, mDecimal, mRound); }

		if (price != 0 && amountOrNetAmount != 0) {
			$(this).valueRnd(amountOrNetAmount/price, true, mDecimal, mRound);
		}else {
			$(this).valueRnd("", true, mDecimal, mRound);
		}
	};
	
	// Net Amount
	jQuery.fn.calcRedNetAmount = function(priceTag, unitTag, tradeBy, mDecimal, mRound) { //OnServer
		var price = priceTag.val().toNumber(",");
		var unit = unitTag.val().toNumber(",");
		
		// block cause performance reason
//		var netAmount = $().calc("@{Calc.calcRedNetAmount()}", {"tradeBy":tradeBy.val(), "priceTag":priceTag.val(), "unitTag":unitTag.val(), "price":price, "unit":unit});
//		if (netAmount == null) { $(this).value("", true);	
//		}else{ $(this).valueRnd(netAmount, true, mDecimal, mRound); }
		
		if (tradeBy.val() == TRANSACTION_BY_UNIT) {
			if (priceTag.isEmpty() || unitTag.isEmpty()) {
				$(this).value("");
			}else{
				if (price != 0 && unit != 0) {
					var netAmount = price * unit;
					$(this).valueRnd(netAmount, true, mDecimal, mRound);
					var netAmountS = String(netAmount);
					if(netAmountS.match(/^[-+]?[1-9]\.[0-9]+e[-]?[1-9][0-9]*$/)){
						netAmount = netAmount.noExponents();
						$(this).valueExponen(netAmount, true, mDecimal, mRound);
					}
				}else{
					$(this).valueRnd("", true, mDecimal, mRound);
				}
			}
			
		}
		if (tradeBy.val() == TRANSACTION_BY_AMOUNT) {
			// inputed by user
		}
	};
	
	jQuery.fn.calcSubNetAmount = function(amountTag, totFeeAmountTag, inputBy, mDecimal, mRound) { //OnServer Tested
		var amount = amountTag.val().toNumber(",");
		var totFeeAmount = totFeeAmountTag.val().toNumber(",");
		var inputByVal = (inputBy) ? inputBy.val() : INPUT_BY_GROSS;
		
		// block cause performance reason
//		var netAmount = $().calc("@{Calc.calcSubNetAmount()}", {"inputByVal":inputByVal, "amount":amount, "totFeeAmount":totFeeAmount});
//		if (netAmount == null) { $(this).value("", true);	
//		}else{ $(this).valueRnd(netAmount, true, mDecimal, mRound); }
		
		var netAmount = "";
		if (inputByVal == INPUT_BY_GROSS) {
			netAmount = amount - totFeeAmount;
		}
		if (inputByVal == INPUT_BY_NET)  {netAmount = amount - totFeeAmount;}

		if(amount != ""){
			$(this).valueRnd(netAmount, true, mDecimal, mRound);
		}
	};
	
	// Amount
	jQuery.fn.calcRedAmount = function(feeAmountTag, netAmountTag, totalFeeAmountTag, mDecimal, mRound) { //OnServer Tested
		var netAmount = netAmountTag.val().toNumber(",");
		var totalFeeAmount = totalFeeAmountTag.val().toNumber(",");
		
		// block cause performance reason
//		var amount = $().calc("@{Calc.calcRedAmount()}", {"feeAmountTag":feeAmountTag.val(), "netAmount":netAmount, "totalFeeAmount":totalFeeAmount});
//		if (amount == null) { $(this).value("", true);	
//		}else{ $(this).valueRnd(amount, true, mDecimal, mRound); }
		
		if (feeAmountTag.isEmpty()) {
			$(this).value("");
		}else{
			var amount = netAmount - totalFeeAmount;
			$(this).valueRnd(amount, true, mDecimal, mRound);
		}
	};
	
	jQuery.fn.calcSubAmount = function(netAmountTag, totalFeeAmountTag, inputBy, mDecimal, mRound) { //OnServer Tested
		var netAmount = netAmountTag.val().toNumber(",");
		var totalFeeAmount = totalFeeAmountTag.val().toNumber(",");
		var inputByVal = (inputBy) ? inputBy.val() : INPUT_BY_GROSS;
		
		// block cause performance reason
//		var amount = $().calc("@{Calc.calcSubAmount()}", {"inputByVal":inputByVal, "netAmount":netAmount, "totalFeeAmount":totalFeeAmount});
//		$(this).valueRnd(amount, true, mDecimal, mRound);
		
		var amount = 0;
		if (inputByVal == INPUT_BY_GROSS) amount = netAmount + totalFeeAmount;
		if (inputByVal == INPUT_BY_NET)  amount = netAmount + totalFeeAmount;
		$(this).valueRnd(amount, true, mDecimal, mRound);
	};
	
	// Capital Gain Tax Amount
	jQuery.fn.calcRedCapGainTaxAmount = function(capTaxRate, netAmountTag, mDecimal, mRound) { //OnServer
		var netAmount = netAmountTag.val().toNumber(",");

		// block cause performance reason
//		var capGainTaxAmount = $().calc("@{Calc.calcRedCapGainTaxAmount()}", {"capTaxRate":capTaxRate, "netAmount":netAmount});
//		$(this).valueRnd(capGainTaxAmount, true, mDecimal, mRound);
//		return capGainTaxAmount;

		var capGainTaxAmount = ((capTaxRate/100) * netAmount);
		$(this).valueRnd(capGainTaxAmount, true, mDecimal, mRound);
		return capGainTaxAmount;
	};
	
	// Payment
	jQuery.fn.calcRedPayment = function(capTaxRate, netAmountTag, amountTag, mDecimal, mRound) { //OnServer
		var netAmount = netAmountTag.val().toNumber(",");
		var amount = amountTag.val().toNumber(",");
		var capTaxAmount = (capTaxRate/100) * netAmount;
		
		// block cause performance reason
//		var paymentAmount = $().calc("@{Calc.calcRedPayment()}", {"netAmountTag":netAmountTag.val(), "amount":amount, "capTaxAmount":capTaxAmount});
//		$(this).valueRnd(paymentAmount, true, mDecimal, mRound);
//		return paymentAmount;
		
		if (netAmountTag.isEmpty()) {
			$(this).value("");
		}else{
			var paymentAmount = (amount - capTaxAmount);
			$(this).valueRnd(paymentAmount, true, mDecimal, mRound);
			return paymentAmount;
		}
	};
	
	jQuery.fn.calcSubPayment = function(capTaxRate, netAmount, amount) { //OnServer
	};
	
	// CalcFeePct
	jQuery.fn.calcRedFeePct = function(amount, feeAmount){
		if(amount == "" || amount == 0)
			$(this).val("");
		else{
			var feePct = ( feeAmount / amount ) * 100;
			$(this).val(feePct);
		}
	};
	
	// Fee Percentage
	jQuery.fn.setFeeTier = function(tierType, productCode, amount, feePct, feeAmount, type, inputBy, mDigit, mRound) {
		if (tierType == TIER_COMPARISON_TYPE_FLAT){
			var data = $(this).getRgFeeTier(productCode, type, amount+"");
			if (data) feePct.value(data.value, true);
			
		}
		
		if (tierType == TIER_COMPARISON_TYPE_PROGRESSIVE){
			var data = $(this).getRgFeeAmount(productCode, type, amount+"", inputBy);
			if (data) feeAmount.valueRnd(data, true, mDigit, mRound);
		}
	};
	
})(jQuery);  
	