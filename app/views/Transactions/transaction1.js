/**
 * TRANSACTION ENGINE FOR CLIENT RE COPY from public\javascripts\transaction.js
 * 
 * Requires: 
 * - jquery
 * - console
 */
function Transaction1() {	
	// Public properties declaration
	this.accrualBase = 0;
	this.accruedDays = 0;
	this.accruedInterest = 0;
	this.capitalGainTax = 0;
	this.capitalGainTaxRate = 0;
	this.discountAmount = 0;
	this.discountNet = 0;
	this.discountTax = 0;
	this.effectiveDate = null;
	this.frequency = 0;
	this.fractionRatioSource = 0;
	this.fractionRatioTarget = 0;
	this.grossAmount = 0;
	this.grossAmountSetAmt = 0;
	this.hasInterest = false;
	this.interestRate = 0;
	this.isDiscounted = false;
	this.isFraction = false;
	this.lastPaymentDate = null;
	this.maturityDate = null;
	this.netAccruedInterest = 0;
	this.netAmount = 0;
	this.nextPaymentDate = null;
	this.price = 0;
	this.priceUnit = 1;
	this.proceed = 0;
	this.proceedSetAmt = 0;
	this.quantity = 0;
	this.securityClass = '';
	this.securityType = '';
	this.netProceed = 0;
	this.settlementAmount = 0;
	this.settlementDate = null;
	this.transactionDate = null;
	this.taxOnInterest = 0;
	this.taxOnInterestRate = 0;
	this.totalCharges = 0;
	this.sumOfChargeNetAmount = 0;
	this.transactionType = '';
	this.yearBase = 0;
	this.isPrice = false;
	this.parPrice = 0;
	this.parValue = 0;

	/**
	 * Calculate all calculated fields
	 * No parameter is required, will read values from local fields
	 * @returns netProceed
	 */
	this.calculate = function() {
		
		if (this.settlementDate == null || !(this.settlementDate instanceof Date)) return;
		if (this.securityType == '') return;
		var quantity = Number(this.quantity);
		var price = Number(this.price);
		
		var netAmount = Number(this.netAmount);
		var priceUnit = Number(this.priceUnit); 

		var parPrice = this.parPrice;
		var parValue = quantity * parPrice * priceUnit;
		var discountNet = parValue - netAmount;

		this.discountNet = discountNet;

		netAmount = quantity * price * priceUnit;
		this.netAmount = netAmount;
		
		var totalCharges = Number(this.totalCharges);
		var sumOfChargeNetAmount = Number(this.sumOfChargeNetAmount);
		var feeMultiplicant = 1;
		if (this.transactionType == 'S' || this.transactionType == 'D') {
			feeMultiplicant = -1;
		}

		var grossAmount = netAmount + (totalCharges * feeMultiplicant);
		var grossAmountSetAmt = netAmount +(sumOfChargeNetAmount * feeMultiplicant);
		this.grossAmount = grossAmount;
		this.grossAmountSetAmt = grossAmountSetAmt;
		
		if (this.hasInterest == 'true') {
			
			var isnull = false;
			if (this.lastPaymentDate == null || !(this.lastPaymentDate instanceof Date)) isnull = true;;
			if (this.nextPaymentDate == null || !(this.nextPaymentDate instanceof Date)) isnull = true;
			if (this.frequency == 0) isnull = true;
			
			if(isnull == true){
				this.netAccruedInterest = 0;
			} else {
				if (this.accruedInterest < 0 || isNaN(this.accruedInterest)) {
					this.calculateAccruedInterest();
				} else {
					var accruedDays = this.calculateAccruedDays(this.lastPaymentDate, this.settlementDate, this.accrualBase);
					this.accruedDays = accruedDays;
				}
				var taxOnInterest = Number(this.taxOnInterest);
				var netAccruedInterest = this.accruedInterest - taxOnInterest;
				this.netAccruedInterest = netAccruedInterest;
			}
		}		
		
		if (this.hasInterest == 'false') {
			this.accruedDays = 0;
			this.accruedInterest = 0;
		}

		var proceed = grossAmount + this.netAccruedInterest;
		var proceedSetAmt = grossAmountSetAmt + this.netAccruedInterest; 
		this.proceed = proceed;
		this.proceedSetAmt = proceedSetAmt;

		var capitalGainTax = Number(this.capitalGainTax);
		var netProceed = proceed - capitalGainTax;
		var settlementAmount = proceedSetAmt - capitalGainTax;
		this.netProceed = netProceed;
		this.settlementAmount = settlementAmount;

		return netProceed;
	};
	
	/**
	 * Calculate Accrued Interest
	 * No parameter is required, will read values from local fields
	 * @returns accruedInterest
	 */
	this.calculateAccruedInterest = function() {
		if($.browser.msie){
			if (this.lastPaymentDate == null || !(this.lastPaymentDate instanceof Date)) return 0;
			if (this.nextPaymentDate == null || !(this.nextPaymentDate instanceof Date)) return 0;
			if (this.settlementDate == null || !(this.settlementDate instanceof Date)) return 0;
			if (this.frequency == 0) return;
		} else {
			if (this.lastPaymentDate == null || !(this.lastPaymentDate instanceof Date)) return 0;
			if (this.nextPaymentDate == null || !(this.nextPaymentDate instanceof Date)) return 0;
			if (this.settlementDate == null || !(this.settlementDate instanceof Date)) return 0;
			if (this.frequency == 0) return 0;	
		}
		
		var	couponInterest = this.calculateCouponInterest(this.lastPaymentDate, this.nextPaymentDate, 
					this.frequency, this.accrualBase, this.yearBase, this.interestRate);

		var couponAmount = this.quantity * couponInterest;
		var couponDays = this.calculateAccruedDays(this.lastPaymentDate, this.nextPaymentDate, this.accrualBase);

		var dailyInterest = couponAmount / couponDays;
		if(isNaN(dailyInterest)) dailyInterest = 0;
		
		var endDate = this.settlementDate;
		if (this.settlementDate > this.nextPaymentDate) endDate = this.nextPaymentDate;
		var accruedDays = this.calculateAccruedDays(this.lastPaymentDate, endDate, this.accrualBase);
		var accruedInterest = 0; 
		if (this.isFraction == 'true') {
			//accruedInterest = (this.quantity/this.fractionRatioSource) * Math.round(this.fractionRatioTarget * accruedDays / couponDays);
			var a = this.quantity/this.fractionRatioSource;
			var b = this.fractionRatioTarget * accruedDays / couponDays;
			b = b.round_half_down(3);
			accruedInterest = a * b;
		} else {
			accruedInterest = dailyInterest * accruedDays;
		}
		
		this.accruedDays = accruedDays;
		this.accruedInterest = accruedInterest;

		return accruedInterest;
	};
	
	this.calculateCouponFraction = function(ratioSource, ratioTarget) {
		if (ratioSource == 0) return 0;
		return ratioTarget / ratioSource;
	};
	
	/**
	 * Calculate Interest Rate per Coupon period
	 * @param 		lastPaymentDate
	 * @param 		nextPaymentDate
	 * @param 		frequency				12(M)/4(Q)/2(H)/1(Y)
	 * @param 		accrualBase				0(ACT)/30/31
	 * @param 		yearBase				0(ACT)/360/365
	 * @param 		annualInterestRate
	 * @returns 	couponInterest			
	 */
	this.calculateCouponInterest = function(lastPaymentDate, nextPaymentDate, frequency, accrualBase, yearBase, annualInterestRate) {
		if($.browser.msie){
			if (lastPaymentDate == null || !(lastPaymentDate instanceof Date)) return 0;
			if (nextPaymentDate == null || !(nextPaymentDate instanceof Date)) return 0;
			if (frequency == 0) return;
		} else {
			if (lastPaymentDate == null || !(lastPaymentDate instanceof Date)) return 0;
			if (nextPaymentDate == null || !(nextPaymentDate instanceof Date)) return 0;
			if (frequency == 0) return 0;	
		}
		
		
		// Must use new Date, or else will refer to same instance, 
		// which will cause changes to be reflected to the other variable
		var periodLastDate = new Date(nextPaymentDate);
		var periodNextDate = new Date(nextPaymentDate);
		var dayFactor = 0;
		
		var i = 0;
		while (periodLastDate > lastPaymentDate) {
			i++;
			periodLastDate.setMonth(periodLastDate.getMonth() - (12/frequency));
			var periodYearBase = yearBase;
			if (periodYearBase == 0) {
				var actualPeriodDays = this.calculateAccruedDays(periodLastDate, periodNextDate, accrualBase);
				periodYearBase = actualPeriodDays * frequency;
			}
			if (periodLastDate < lastPaymentDate) {
				periodLastDate = lastPaymentDate;
			}
			var periodDays = this.calculateAccruedDays(periodLastDate, periodNextDate, accrualBase);
			if (periodYearBase != 0) {
				dayFactor += (periodDays / periodYearBase);
			}
			periodNextDate = new Date(periodLastDate);
		}
		
		return annualInterestRate * dayFactor * 0.01;
	};

	/**
	 * Calculate Accrued Days
	 * @param		beginningDate
	 * @param		endingDate
	 * @param		accrualBase			0(ACT)/30/31
	 * @returns		accruedDays
	 */
	this.calculateAccruedDays = function(beginningDate, endingDate, accrualBase) {	
		if($.browser.msie){
			if (beginningDate == null || !(beginningDate instanceof Date)) return 0;
			if (endingDate == null || !(endingDate instanceof Date)) return 0;
		} else {
			if (beginningDate == null || !(beginningDate instanceof Date)) return 0;
			if (endingDate == null || !(endingDate instanceof Date)) return 0;	
		}
		
		if (accrualBase == 0) {
			var diff = endingDate.getTime() - beginningDate.getTime();
			return (diff / 86400000);
		} else if (accrualBase > 0) {
			var years = endingDate.getYear() - beginningDate.getYear();
			var months = endingDate.getMonth() - beginningDate.getMonth();
			var date1 = (endingDate.getDate() > accrualBase) ? accrualBase : endingDate.getDate(); 
			var date2 = (beginningDate.getDate() > accrualBase) ? accrualBase : beginningDate.getDate();
			var days = date1 - date2;
			if ( ((((years * 12) + months) * accrualBase) + days) < 0)  
				{return 0;}
			else
				{return (((years * 12) + months) * accrualBase) + days;}
		} else {
			return -1;
		}
	};	
	
	/**
	 * Decode frequency
	 * M=12; Q=4; H=2; Y=1
	 * @param 		code
	 * @returns 	decoded value		
	 */
	this.decodeFrequency = function(code) {
		if (code == 'M') return 12;
		if (code == 'Q') return 4;
		
		if (code == 'H') return 2;
		if (code == 'Y') return 1;
		return 0;
	};
	
	/**
	 * Decode Calculation Base
	 * This function support both accrualBase & yearBase
	 * ACT=0; others: return numeric value (30/31/365/366)
	 * @param		code (calculation base code: ACT/30/31/365/366)
	 * @returns		decoded value
	 */
	this.decodeCalculationBase = function(code) {
		if (code == 'ACT') return 0;
		var number = Number(code);
		if (number != NaN) return number;
		return -1;
	};
	
	/**
	 * Set transaction days based on security class and transactionType
	 * No parameter is required, will read values from local fields
	 */
	
	this.setDates = function() {
		if (this.securityClass == 'FI' || this.securityClass == 'MM') {
			this.effectiveDate = this.settlementDate;
			this.effectiveDate.setDate(this.effectiveDate.getDate() + 1);
		}
		
		if ((this.securityClass == 'MM') && (this.transactionType == 'B')) {
			this.lastPaymentDate = this.settlementDate;
			this.nextPaymentDate = this.maturityDate;
		}		
	};
}
