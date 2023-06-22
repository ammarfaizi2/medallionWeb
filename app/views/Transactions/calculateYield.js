function calculateYield(
		amortizationMethod,
		parValue,
		quantity,
		nominal,
		interestRate,
		discAfterTax,
		maturityDate,
		settlementDate,
		parPrice) {
	
	//if ((amortizationMethod !='')&&(quantity>0)&&(nominal>0)&&(interestRate > 0)){
	if ((amortizationMethod !='')&&(quantity>0)&&(nominal>0)){
		var METHOD_EIR = 'AMORTIZATION_METHOD-EIR';
		var METHOD_SL = 'AMORTIZATION_METHOD-SL';
		var METHOD_NPV = 'AMORTIZATION_METHOD-NPV';
		
		console.log("amortizationMethod on func = " +amortizationMethod);
		console.log("discAfterTax on func = " +discAfterTax);
		console.log("quantity on func = " +quantity);
		console.log("nominal on func = " +nominal);
		console.log("interestRate on func = " +interestRate);
		console.log("maturityDate on func = " +maturityDate);
		console.log("settlementDate on func = " +settlementDate);
		console.log("parValue on func = " +parValue);
		console.log("parPrice = " +parPrice);
		
		var yield;
		var endDate = new Date(maturityDate);
		var startDate = new Date(settlementDate);
		var totalDay = Math.round((endDate - startDate) / (1000 * 60 * 60 * 24) );
		var countInterestRate = 0;
		if (parPrice > 100){
			countInterestRate = interestRate/parPrice;
		} else {
			countInterestRate = interestRate/100;
		}
		if (amortizationMethod == METHOD_EIR){
			yield = yieldByEIR(
					parseFloat(parValue),
					parseFloat(nominal),
					360,
					parseFloat(countInterestRate),
					settlementDate,
					maturityDate);
			console.log("YIELD EIR on func = " +yield);
			console.log("YIELD EIR Min 1 on func = " +(yield-0.1));
			return yield;
		}
		
		if (amortizationMethod == METHOD_SL){
			yield = (discAfterTax/quantity) * 360 / totalDay;
			console.log("YIELD SL on func = " +yield);
			return yield;
		}
		
		if (amortizationMethod == METHOD_NPV){
			yield = (discAfterTax/nominal) * 360 / totalDay;
			console.log("YIELD NPV on func = " +yield);
			return yield;
		}
	} else {
		return '';
	}
}