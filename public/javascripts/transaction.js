/**
 * TRANSACTION ENGINE FOR CLIENT 
 * 
 * Requires: 
 * - jquery
 * - console
 */
function Transaction() {	
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
	this.isInputNominal = false;
	this.firstCouponDate = null;
	this.issueDate = null;

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

		if(this.isInputNominal){
			//kalo nominal
			price = (netAmount/quantity)/priceUnit;
			this.price = price;
		}else{
			//kalo price
			try{
				//cs 4001
				var priceSplit = this.price.split(".");
				if(priceSplit.length > 1){
					//netAmount = ((quantity * (price *Math.pow(10,Number(priceSplit[1].length)))) * priceUnit) /Math.pow(10,Number(priceSplit[1].length));
					/*netAmount =quantity * price * Math.pow(10,Number(priceSplit[1].length)) ;
					netAmount = netAmount * priceUnit ;
					netAmount = netAmount/Math.pow(10,Number(priceSplit[1].length));*/
					netAmount = quantity * parseInt(price * Math.pow(10,Number(priceSplit[1].length)));
					netAmount = netAmount * priceUnit ;
					netAmount = netAmount/Math.pow(10,Number(priceSplit[1].length));
				}else{
					netAmount = quantity * price * priceUnit;
				}
			}catch(e){
				netAmount = quantity * price * priceUnit;
			}
			
			//netAmount = quantity * price * priceUnit;
			this.netAmount = netAmount;
		}
		
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
					if (this.accrualBase > 0 && this.lastPaymentDate != null && this.nextPaymentDate != null && this.firstCouponDate != null) {
						
						var firstCoupon = this.firstCouponDate.getDate();
						
						var eom = this.lastEOM(this.settlementDate);
						if(this.isLastDate(this.settlementDate) && this.settlementDate.getMonth() && (eom.getDate() < firstCoupon )) {
							if (firstCoupon > this.accrualBase) firstCoupon = this.accrualBase;
							if (this.settlementDate.getFullYear() % 4 == 0 ) {//jika tahun kabisat
								accruedDays = this.accrualBase - this.settlementDate.getDate() +  accruedDays;
							}else {
								accruedDays = firstCoupon - this.settlementDate.getDate() + accruedDays;
							}
						}
						
						
						eom = this.lastEOM(this.lastPaymentDate);
						if(this.isLastDate(this.lastPaymentDate) && this.lastPaymentDate.getMonth() == 1  && (eom.getDate() < firstCoupon )) {
							if (this.accrualBase <= firstCoupon) {
								accruedDays = accruedDays - (this.accrualBase - this.lastPaymentDate.getDate());
							}else {
								accruedDays = accruedDays - (this.accrualBase - firstCoupon);
							}
						}
					}
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

		if (this.firstCouponDate == null ){
			this.firstCouponDate = new Date();
			this.firstCouponDate.setDate(1);
		}
		var	couponInterest = this.calculateCouponInterest(this.lastPaymentDate, this.nextPaymentDate, this.settlementDate, this.firstCouponDate,
					this.frequency, this.accrualBase, this.yearBase, this.interestRate);
		
//		var couponAmount = this.quantity * couponInterest;
//		var couponDays = this.calculateAccruedDaysPorto(this.lastPaymentDate, this.nextPaymentDate, this.firstCouponDate, this.accrualBase);//di sini
//		var dailyInterest = couponAmount / couponDays; 
		
//		if(isNaN(dailyInterest)) dailyInterest = 0;
		
		var endDate = this.settlementDate;
		if (this.settlementDate > this.nextPaymentDate) endDate = this.nextPaymentDate;
		var accruedDays = this.calculateAccruedDaysPorto(this.lastPaymentDate, endDate, this.firstCouponDate.getDate(), this.accrualBase);
		var accruedInterest = 0;
		if (this.accrualBase > 0 && this.lastPaymentDate != null && this.nextPaymentDate != null && this.firstCouponDate != null ) {
			
			var firstCoupon = this.firstCouponDate.getDate();
			
			var eom = this.lastEOM(endDate);
			if(this.isLastDate(endDate) && endDate.getMonth() && (eom.getDate() < firstCoupon )) {
				if (firstCoupon > this.accrualBase ) firstCoupon = this.accrualBase;
				if (endDate.getFullYear() % 4 == 0 ) {//jika tahun kabisat
					accruedDays = this.accrualBase -  endDate.getDate() + accruedDays;
				}else {
					accruedDays = firstCoupon - endDate.getDate() + accruedDays;
				}
			}
			
			
			eom = this.lastEOM(this.lastPaymentDate);
			if(this.isLastDate(this.lastPaymentDate) && this.lastPaymentDate.getMonth() == 1  && (eom.getDate() < firstCoupon )) {
				if (this.accrualBase <= firstCoupon) {
					accruedDays = accruedDays - (this.accrualBase - this.lastPaymentDate.getDate());
				}else {
					accruedDays = accruedDays - (this.accrualBase - firstCoupon);
				}
			}
		}
		
		if (this.isFraction == 'true') {

			var a = this.quantity/this.fractionRatioSource;
			var b = this.calculateAccruedInterestFraction();
			b = b.round_half_down(4);
			accruedInterest = a * b;
		} else {
			accruedInterest = this.quantity * couponInterest;
//			accruedInterest = dailyInterest * accruedDays;
		}
		
		this.accruedDays = accruedDays;
		this.accruedInterest = accruedInterest;

		return accruedInterest;
	};
	
	//coding existing: lastPaymentDate = issueDate. Sedangkan nextPaymentDate = firstCoupon jika settlementDate < firstCoupon.
	this.calculateAccruedInterestFraction = function(){
		
		var b = 0;
		var range = 12/this.frequency;
		if (this.settlementDate <= this.firstCouponDate) {
			//array berlaku jika issueDate ke first coupon melebihi frequency
			var arraySize = (((this.firstCouponDate.getFullYear() - this.lastPaymentDate.getFullYear())*12 + (this.firstCouponDate.getMonth() - this.lastPaymentDate.getMonth())) / range);
			if (this.firstCouponDate.getDate() > this.lastPaymentDate.getDate() ) arraySize += 1;
			console.log('calculateAccruedInterestFraction >>> ' + arraySize +  ' yearBase ' + this.yearBase);
			var nextDate = null;
			var a = 0;
			var lastDate = this.firstCouponDate;
			for (var i = 0; i <arraySize; i++ ) {
				a = range;
				nextDate = lastDate;
				
				var lastDayNext = this.lastEOM(nextDate);
				
				var lastDay = this.lastEOM(new Date(nextDate.getFullYear(), nextDate.getMonth() - a, 1));
				if (lastDay.getDate() >= this.firstCouponDate.getDate()) {
					lastDate = new Date(nextDate.getFullYear(), nextDate.getMonth() - a, this.firstCouponDate.getDate());
				}else{
					if (nextDate.getDate() >= lastDay.getDate()) {
						lastDate = lastDay;
					} else {
						lastDate = new Date(nextDate.getFullYear(), nextDate.getMonth() - a, nextDate.getDate());
					}
				}
				
				//var oneDay = 86400000; //hours*minutes*seconds*milliseconds = 24*60*60*1000;
				
				//var diffDays = Math.round(Math.abs( (lastDate.getTime() - nextDate.getTime())/oneDay));
				var diffDays = this.calculateAccruedDaysPorto(lastDate, nextDate, this.firstCouponDate.getDate(), this.accrualBase );
				var realDate;
				if ((this.lastPaymentDate.getTime() >= lastDate.getTime()) && (this.lastPaymentDate.getTime() <= nextDate.getTime())) {
					realDate = this.lastPaymentDate;
				}else if ( (this.settlementDate.getTime() >= lastDate.getTime()) && (this.settlementDate.getTime() <= nextDate.getTime())) {
					realDate = this.settlementDate;
				}else {
					if (this.settlementDate.getTime() <= lastDate.getTime()) {
						realDate = lastDate;
					}else {
						realDate = nextDate;
					}
				}
				
				var diffDays2;
				if (realDate == this.lastPaymentDate) {
					if (this.settlementDate.getTime() <= nextDate.getTime()) {
						diffDays2 = this.calculateAccruedDaysPorto(realDate, this.settlementDate, this.firstCouponDate.getDate(), this.accrualBase);
					}else {
						diffDays2 = this.calculateAccruedDaysPorto(realDate, nextDate, this.firstCouponDate.getDate(), this.accrualBase);
					}
				}else {
					diffDays2 = this.calculateAccruedDaysPorto(lastDate, realDate, this.firstCouponDate.getDate(), this.accrualBase);
				}
				
				var accrInt = 0;
				if (this.yearBase == 0 ) {
					accrInt = this.interestRate*diffDays2/(diffDays*this.frequency)*this.fractionRatioSource*0.01;
				}else {
					accrInt = this.interestRate*diffDays2/(this.yearBase)*this.fractionRatioSource*0.01;
				}
				//alert("this.yearBase:"+this.yearBase+"\n this.fractionRatioSource:"+this.fractionRatioSource+"\n this.frequency:"+this.frequency+"\n diffDays2:"+diffDays2+"\n diffDays:"+diffDays+"\n this.interestRate:"+this.interestRate+"\n accrInt:"+accrInt)
				accrInt = accrInt.round_half_down(4);
				b += accrInt;
				console.log('b ' + b);
			}
			
		}else { 
			var accruedDays = this.calculateAccruedDaysPorto(this.lastPaymentDate, this.settlementDate, this.firstCouponDate.getDate(), this.accrualBase);
			var couponDays = this.calculateAccruedDaysPorto(this.lastPaymentDate, this.nextPaymentDate,  this.firstCouponDate.getDate(),  this.accrualBase);
			if (this.yearBase == 0 ) {
				b = this.fractionRatioSource * this.interestRate * accruedDays / (couponDays * this.frequency) * 0.01;
			}else {
				b = this.fractionRatioSource * this.interestRate * accruedDays / (this.yearBase ) * 0.01;
			}
			
		}
		return b;
	};
	
	this.calculateCouponFraction = function(ratioSource, ratioTarget) {
		if (ratioSource == 0) return 0;
		return ratioTarget / ratioSource;
	};
	
	/**
	 * Calculate Interest Rate per Coupon period
	 * @param 		lastPaymentDate
	 * @param 		nextPaymentDate
	 * @param		firstCouponDate
	 * @param 		frequency				12(M)/4(Q)/2(H)/1(Y)
	 * @param 		accrualBase				0(ACT)/30/31
	 * @param 		yearBase				0(ACT)/360/365
	 * @param 		annualInterestRate
	 * @returns 	couponInterest			
	 */
	this.calculateCouponInterest = function(lastPaymentDate, nextPaymentDate, settlementDate, firstCouponDate, frequency, accrualBase, yearBase, annualInterestRate) {
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
			periodLastDate = this.calcMonthsNoRolloverWithCoupon(periodNextDate, (-12/frequency), firstCouponDate.getDate());
//			periodLastDate.setMonth(periodLastDate.getMonth() - (12/frequency)); //ada bugs di sini, e.g: 30 agustus - 6 bulan = 2 maret 
			var periodYearBase = yearBase;
			if (periodYearBase == 0) {
				var actualPeriodDays = this.calculateAccruedDaysPorto(periodLastDate, periodNextDate, firstCouponDate.getDate(), 0);
				periodYearBase = actualPeriodDays * frequency;
			}
			if (periodLastDate < lastPaymentDate) {
				periodLastDate = lastPaymentDate;
			}
			
			var endDate = periodNextDate;
			if ((periodLastDate.getTime() < settlementDate.getTime()) && (settlementDate.getTime() < periodNextDate.getTime())) endDate = settlementDate;
			var periodDays = 0;
			if (settlementDate >= endDate ) periodDays = this.calculateAccruedDaysPorto(periodLastDate, endDate, firstCouponDate.getDate(), accrualBase);
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
	 * Calculate Accrued Days Porto
	 * @param		beginningDate
	 * @param		endingDate
	 * @param		firstCoupon			dd (date) of first coupon. e.g: 15/01/2018 => 15
	 * @param		accrualBase			0(ACT)/30/31
	 * @returns		accruedDays
	 */
	this.calculateAccruedDaysPorto = function(beginningDate, endingDate, firstCoupon, accrualBase) {	
		if($.browser.msie){
			if (beginningDate == null || !(beginningDate instanceof Date)) return 0;
			if (endingDate == null || !(endingDate instanceof Date)) return 0;
		} else {
			if (beginningDate == null || !(beginningDate instanceof Date)) return 0;
			if (endingDate == null || !(endingDate instanceof Date)) return 0;	
		}
		
		var accruedDays = this.calculateAccruedDays(beginningDate, endingDate, accrualBase);
		if (accrualBase > 0 && accrualBase != null && beginningDate != null && endingDate != null && firstCoupon > 0 ) {
			/*var eom = this.lastEOM(endingDate);
			if(this.isLastDate(endingDate) && endingDate.getMonth() && (eom.getDate() < firstCoupon )) {
				
				if(firstCoupon > accrualBase)  firstCoupon = accrualBase;
				
				if (endingDate.getFullYear() % 4 == 0 ) {//jika tahun kabisat
					accruedDays = accrualBase - endingDate.getDate() + accruedDays;
				}else {
					accruedDays = firstCoupon - endingDate.getDate() + accruedDays;
				}
			}
			
			
			eom = this.lastEOM(beginningDate);
			if(this.isLastDate(beginningDate) && beginningDate.getMonth() == 1  && (eom.getDate() < firstCoupon )) {
				if (accrualBase <= firstCoupon) {
					accruedDays = accruedDays - (accrualBase - beginningDate.getDate());
				}else {
					accruedDays = accruedDays - (accrualBase - firstCoupon);
				}
			}*/
			
			//base transactionhelper.calculateAccruedDaysPorto
			var eom = this.lastEOM(endingDate);
			if(this.isLastDate(endingDate) && endingDate.getMonth() == 1 ){
				if(eom.getTime() == endingDate.getTime() && endingDate.getMonth()==1 && eom.getDate()> firstCoupon){
					if (firstCoupon > accrualBase) firstCoupon = accrualBase;
					actDay = (beginningDate.getDate() > accrualBase ? accrualBase : beginningDate.getDate());
					accruedDays = actDay - endingDate.getDate() + accruedDays;
				}
			}else if(this.isLastDate(beginningDate) && beginningDate.getMonth() == 1){
				eom = this.lastEOM(beginningDate);
				if(eom.getTime() == beginningDate.getTime() && endingDate.getMonth()==1 && (endingDate.getDate() - beginningDate.getDate())>0){
					actDay = (endingDate.getDate() > accrualBase ? accrualBase : endingDate.getDate());
					accruedDays = accruedDays - (actDay - beginningDate.getDate());
				}
			}
		}
		
		return accruedDays;
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
	
	this.lastEOM = function(date) {
		var lastDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
		return lastDate;
	};
	
	this.isLastDate = function(date){
		var dDate = date.getDate();
		date = this.lastEOM(date);
		var eomDate = date.getDate();
		return (dDate == eomDate);
	};
	
	this.calcMonthsNoRollover = function(date, monthOffset){
		var dt = new Date(date);
		dt.setMonth(dt.getMonth()+ monthOffset) ;
		if (dt.getDate() < date.getDate()) { dt.setDate(0); }
		return dt;
	};
	
	this.calcMonthsNoRolloverWithCoupon = function(date, monthOffset, couponDD){
		
		var dt = this.calcMonthsNoRollover(date, monthOffset);
		console.log("dt.getDate " + dt.getDate());
		if ((dt.getMonth() != 1) && (dt.getDate() < couponDD)) dt.setDate(couponDD);
		//jika dt.getDate < kupon maka ambil kupon saja
		//kupon 28
		//25 vs 29
//		if (( dt.getDate() + 1) < couponDD) dt.setDate(couponDD); //ini untuk skenario: 28 feb 2017 - 6 bulan harusnya sesuai tanggal first coupon = 30 aug
		
		return dt;
	};
}
