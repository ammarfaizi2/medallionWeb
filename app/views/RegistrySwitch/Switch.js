function Switch(html) {
	if (this instanceof Switch) {
/* =========================================================================== 
 * Constant
 * ========================================================================= */
		var TRANSACTION_BY_UNIT = "TRANSACTION_BY_UNIT";
		var TRANSACTION_BY_AMOUNT = "TRANSACTION_BY_AMOUNT";
		var SWITCHING = "SWITCHING";
	
		var outPriceError = $("#outPriceErrorMessage");
		var inPriceError = $("#inPriceErrorMessage");
		
/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var swt = html.inject(this);
		swt["switchAll"] = html.name("swt.switchAll");
		swt.outTradeBy.children().eq(0).remove();
		
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var mode = swt.dispatch.val();
		var redRgInvestment = (swt.investFrom.val() == "") ? null: html.getRgInvestmentAcct(null, swt.investFrom.val()); 
		var subRgInvestment = (swt.investTo.val() == "") ? null: html.getRgInvestmentAcct(null, swt.investTo.val());
//		var redRgPortfolio = (swt.investFrom.val() == "" || swt.tradeDate.val() == "") ? null : html.getRgPortfolio(swt.outProductCode.val(), swt.investFrom.val(), swt.tradeDate.val().fmtYYYYMMDD("/"));
		var redRgPortfolio = (swt.investFrom.val() == "" || swt.tradeDate.val() == "") ? null : html.getRgPortfolio(swt.fundCodeFrom.val(), swt.investFrom.val(), swt.tradeDate.val().fmtYYYYMMDD("/"));
		var redRgTax = (swt.outTax.val() == "") ? null : html.getRgTax(swt.outTax.val()); 
		
		var redProductCode = (redRgInvestment) ? redRgInvestment.rgProduct.productCode : "";
		var subProductCode = (subRgInvestment) ? subRgInvestment.rgProduct.productCode : "";

		var redRgNav = (swt.navDate.val() == "" || redProductCode == "") ? null : html.getRgNav(swt.navDate.val().fmtYYYYMMDD("/"), redProductCode);
		var subRgNav = (swt.navDate.val() == "" || subProductCode == "") ? null : html.getRgNav(swt.navDate.val().fmtYYYYMMDD("/"), subProductCode);
		
		var gnThdPrty = (swt.investFrom.isEmpty() || swt.fundCodeFrom.isEmpty() || swt.trnSABranchKey.isEmpty()) ? null : html.getGnThdPrty(swt.trnSABranchKey.val());
/* =========================================================================== 
 * Ajax Method
 * ========================================================================= */
		function rgFeeTiers(isImplement) {
			console.log('rgFeeTiers');
				
			//var amount = swt.outNetAmt.val().toNumber(",");
			var amount = swt.outNetAmtStripped.val().toNumber(",");
			var tierType = swt.lookupSwiTierType.val();
			var productCode = swt.fundCodeFrom.val();
			var type = SWITCHING;
			
			if (amount != 0) {
				//html.setFeeTier(tierType, productCode, amount, swt.outFeePct, swt.outFeeAmt, type);
				html.setFeeTier(tierType, productCode, amount, swt.outFeePctStripped, swt.outFeeAmtStripped, type);
				calcFeeAmt();
				calcDiscAmt();
				calcOtherAmt();
				calcTaxAmt();
				calcTotFeeAmt();
				calcNetAmount();
				calcUnit();
				
				}
			}
/* =========================================================================== 
 * Method
 * ========================================================================= */
		function calcUnit(state) {
			console.log('calcUnit '+state);

			// TODO untuk yang state in, nanti harus ada operan inputBy , samain hitungan dari subscribe untuk hitung unit
			if (state == "out") {
				var digit =swt.outUnitRoundValue.val();
				var roundType =swt.outUnitRoundType.val();
				//swt.outUnit.calcRedUnit(swt.outNetAmt, swt.outPrice, swt.outTradeBy, digit, roundType);
				swt.outUnit.calcRedUnit(swt.outNetAmtStripped, swt.outPriceStripped, swt.outTradeBy, digit, roundType);
				
			}
			if (state == "in") {
				var digit =swt.inUnitRoundValue.val();
				var roundType =swt.inUnitRoundType.val();
				//swt.inUnit.calcSubUnit(swt.inNetAmt, swt.inPrice, null, digit, roundType);
				swt.inUnit.calcSubUnit(swt.inAmtStripped, swt.inPriceStripped, null, digit, roundType);
			}
		}
		
		function calcFeeAmt(state) {
			console.log('calcFeeAmt '+state);
			
			// TODO untuk yang state in, nanti harus ada operan inputBy Gross, samain hitungan dari subscribe
			if (state == "out") {
				var digit =swt.outAmountRoundValue.val();
				var roundType =swt.outAmountRoundType.val();
				//swt.outFeeAmt.calcRedFeeAmount(swt.outNetAmt, swt.outFeePct, swt.outTradeBy, digit, roundType);
				console.log('Gross AMOUNT outNetAmtStripped = '+swt.outNetAmtStripped.val());
				swt.outFeeAmt.calcRedFeeAmount(swt.outNetAmtStripped, swt.outFeePctStripped, swt.outTradeBy, digit, roundType);
			}
			
			if (state == "in") { 
				var digit =swt.inAmountRoundValue.val();
				var roundType =swt.inAmountRoundType.val();
				//swt.inFeeAmt.calcSubFeeAmount(swt.inAmt, swt.inFeePct, null, digit, roundType);
				swt.inFeeAmt.calcSubFeeAmount(swt.inAmtStripped, swt.inFeePctStripped, null, digit, roundType);
			}
		}
		
		function calcDiscAmt(state) {
			console.log('calcDiscAmt '+state);
			
			if (state == "out") {
				var digit =swt.outAmountRoundValue.val();
				var roundType =swt.outAmountRoundType.val();
				//swt.outDiscAmt.calcRedDiscAmount(swt.outFeeAmt, swt.outDiscPct, digit, roundType);
				swt.outDiscAmt.calcRedDiscAmount(swt.outFeeAmtStripped, swt.outDiscPctStripped, digit, roundType);
			}
			
			if (state == "in") {
				var digit =swt.inAmountRoundValue.val();
				var roundType =swt.inAmountRoundType.val();
				//swt.inDiscAmt.calcSubDiscAmount(swt.inFeeAmt, swt.inDiscPct, digit, roundType);
				swt.inDiscAmt.calcSubDiscAmount(swt.inFeeAmtStripped, swt.inDiscPctStripped, digit, roundType);
			}
			
		}
		
		function calcOtherAmt(state) {
			console.log('calcOtherAmt '+state);
			
			if (state == "out") {
				var digit =swt.outAmountRoundValue.val();
				var roundType =swt.outAmountRoundType.val();
				//swt.outOthrAmt.calcRedOtherAmount(swt.outNetAmt, swt.outOthrPct, digit, roundType);
				swt.outOthrAmt.calcRedOtherAmount(swt.outNetAmtStripped, swt.outOthrPctStripped, digit, roundType);
			}
			
			if (state == "in") {
				var digit =swt.inAmountRoundValue.val();
				var roundType =swt.inAmountRoundType.val();
				//swt.inOthrAmt.calcSubOtherAmount(swt.inAmt, swt.inOthrPct, null, digit, roundType);
				swt.inOthrAmt.calcSubOtherAmount(swt.inAmtStripped, swt.inOthrPctStripped, null, digit, roundType);
			}
		}
		
		function calcTaxAmt(state) {
			console.log('calcTaxAmt '+state);
			
			if (state == "out") {
				var digit =swt.outAmountRoundValue.val();
				var roundType =swt.outAmountRoundType.val();
				//swt.outTaxAmt.calcRedTaxFeeAmount(swt.outTaxPct, swt.outFeeAmt, swt.outDiscAmt, swt.outOthrAmt, digit, roundType);
				swt.outTaxAmt.calcRedTaxFeeAmount(swt.outTaxPctStripped, swt.outFeeAmtStripped, swt.outDiscAmtStripped, swt.outOthrAmtStripped, digit, roundType);
			}
			
			if (state == "in") {
				var digit =swt.inAmountRoundValue.val();
				var roundType =swt.inAmountRoundType.val();
				//swt.inTaxAmt.calcSubTaxFeeAmount(swt.inTaxPct, swt.inFeeAmt, swt.inDiscAmt, swt.inOthrAmt, digit, roundType);
				swt.inTaxAmt.calcSubTaxFeeAmount(swt.inTaxPct, swt.inFeeAmtStripped, swt.inDiscAmtStripped, swt.inOthrAmtStripped, digit, roundType);
			}
			
		};
		
		function calcTotFeeAmt(state) {
			console.log('calcTotFeeAmt '+state);
			
			if (state == "out") {
				var digit =swt.outAmountRoundValue.val();
				var roundType =swt.outAmountRoundType.val();
				//swt.outTotFeeAmt.calcRedTotalFeeAmount(swt.outFeeAmt, swt.outDiscAmt, swt.outOthrAmt, swt.outTaxAmt, digit, roundType);
				swt.outTotFeeAmt.calcRedTotalFeeAmount(swt.outFeeAmtStripped, swt.outDiscAmtStripped, swt.outOthrAmtStripped, swt.outTaxAmtStripped, digit, roundType);
			}
			if (state == "in") {
				var digit =swt.inAmountRoundValue.val();
				var roundType =swt.inAmountRoundType.val();
				//swt.inTotFeeAmt.calcSubTotalFeeAmount(swt.inFeeAmt, swt.inDiscAmt, swt.inOthrAmt, swt.inTaxAmt, digit, roundType);
				swt.inTotFeeAmt.calcSubTotalFeeAmount(swt.inFeeAmtStripped, swt.inDiscAmtStripped, swt.inOthrAmtStripped, swt.inTaxAmtStripped, digit, roundType);
			}
		}
		
		function calcAmount(state) {
			console.log('calcAmount '+state);
			
			if (state == "out") {
				var digit =swt.outAmountRoundValue.val();
				var roundType =swt.outAmountRoundType.val();
				//swt.outAmt.calcRedAmount(swt.outFeeAmt, swt.outNetAmt, swt.outTotFeeAmt, digit, roundType);
				swt.outAmt.calcRedAmount(swt.outFeeAmtStripped, swt.outNetAmtStripped, swt.outTotFeeAmt, digit, roundType);
				if (swt.outNetAmt.isEmpty()) {
					swt.outAmt.val('');
					swt.outAmtStripped.val('');
				}
			}
		}
		
		function calcPayment(state) {
			console.log('calcPayment '+state);
			
			if (state == "out") {
				if (redRgTax && subRgInvestment) {
					var digit =swt.outAmountRoundValue.val();
					var roundType =swt.outAmountRoundType.val();
					//var paymentAmount = swt.outPayAmt.calcRedPayment(Number(redRgTax.taxRate), swt.outNetAmt, swt.outAmt, digit, roundType);
					var paymentAmount = swt.outPayAmt.calcRedPayment(Number(redRgTax.taxRate), swt.outNetAmtStripped, swt.outAmtStripped, digit, roundType);
					var inputBy = '';
					var digit =swt.inAmountRoundValue.val();
					var roundType =swt.inAmountRoundType.val();
					swt.inAmt.valueRnd(paymentAmount, true, digit, roundType);
					calcNetAmount("in");
					calcFeeAmt("in");
					calcDiscAmt("in");
					calcTaxAmt("in");
					calcOtherAmt("in");
					calcTotFeeAmt("in");
					calcAmount("in");
					calcUnit("in");
					
					var tierType = subRgInvestment.rgProduct.lookupBySubTierType.lookupId;
					var productCode = subRgInvestment.rgProduct.productCode;
					var type = SWITCHING;
					//html.setFeeTier(tierType, productCode, paymentAmount, swt.inFeePct, swt.inFeeAmt, "", inputBy, digit, roundType);
					html.setFeeTier(tierType, productCode, paymentAmount, swt.inFeePctStripped, swt.inFeeAmtStripped, "", inputBy, digit, roundType);
				} else {
					//swt.outPayAmt.valueRnd(0,true, digit, roundType);
				}
			}
		}
		
		function calcNetAmount(state) {
			console.log('calcNetAmount '+state);
			var digitIn =swt.inAmountRoundValue.val();
			var roundTypeIn =swt.inAmountRoundType.val();
			
			var digitOut =swt.outAmountRoundValue.val();
			var roundTypeOut =swt.outAmountRoundType.val();
			
			if (state == "out") {
				//swt.outNetAmt.calcRedNetAmount(swt.outPrice, swt.outUnit, swt.outTradeBy, digitOut, roundTypeOut);
				swt.outNetAmt.calcRedNetAmount(swt.outPriceStripped, swt.outUnitStripped, swt.outTradeBy, digitOut, roundTypeOut);
			}
			if (state == "in") {
				//swt.inNetAmt.calcSubNetAmount(swt.inAmt, swt.inTotFeeAmt,null, digitIn, roundTypeIn);
				swt.inNetAmt.calcSubNetAmount(swt.inAmtStripped, swt.inTotFeeAmtStripped,null, digitIn, roundTypeIn);
			}
		}
		
		function calcCapGainTaxAmt() {
			console.log('calcCapGainTaxAmt');
			var digit = swt.outAmountRoundValue.val();
			var roundType = swt.outAmountRoundType.val();

			if (redRgTax)  
				swt.outCapGainTaxAmount.calcRedCapGainTaxAmount(Number(redRgTax.taxRate), swt.outNetAmtStripped, digit, roundType);
				//swt.outCapGainTaxAmount.calcRedCapGainTaxAmount(Number(redRgTax.taxRate), swt.outNetAmt, digit, roundType);
				//swt.outCapGainTaxAmount.valueRnd(0,true, digit, roundType);
			
		}

		
		function viewTradeBy(state) {
			console.log('viewTradeBy '+state);
			
			var con = swt.switchAll.checked();
			var disabled = swt.outTradeBy.attr("disabled");
			
			if (swt.outTradeBy.val() == TRANSACTION_BY_AMOUNT) {
				swt.outTradeByHidden.val(TRANSACTION_BY_AMOUNT);
				swt.BYAMOUNT.append(swt.pPrice.load()).append(swt.pUnit.load());
				
				swt.outUnit.disabled();
				swt.outNetAmt.enabled();
				$("#switchTab #tab-1 p[id=grossAmount] label span").html("*");
				$("#switchTab #tab-1 p[id=pUnit] label span").html("");
				if (disabled) {
					swt.outUnit.disabled();
					swt.outNetAmt.disabled();
				}
			}
			
			if (swt.outTradeBy.val() == TRANSACTION_BY_UNIT) {
				swt.outTradeByHidden.val(TRANSACTION_BY_UNIT);
				swt.BYUNIT.append(swt.pUnit.load()).append(swt.pPrice.load());
				swt.outUnit.enabled();
				swt.outNetAmt.disabled();
				$("#switchTab #tab-1 p[id=grossAmount] label span").html("");
				$("#switchTab #tab-1 p[id=pUnit] label span").html("*");
				if (disabled) swt.outUnit.disabled();				
				if (con == 'true') {
					swt.outTradeBy.disabled();
					swt.outUnit.disabled();
				}
			}
		}
		
		function calcRgPortfolio() {
			console.log('calcRgPortfolio '+redRgPortfolio);
//			var digit = swt.outUnitRoundValue.val();
//			var roundType = swt.outUnitRoundType.val();
			if (redRgPortfolio) {
//				swt.outAvailUnit.valueRnd(redRgPortfolio.unit, true, digit, roundType);
				if (('${from}'=='' )||('${from}'==null))
					swt.outAvailUnit.value(redRgPortfolio.unit, true);
				swt.outAvailBal.value(redRgPortfolio.balanceAmount, true);
			}
		}
		
		function clearInvestForm() {
			if((swt.fundCodeFrom.isChange()) || (swt.fundCodeFrom.val() == "")){
				swt.investFrom.removeClass('fieldError');
				swt.investFrom.val("");
				swt.investFromKey.val("");
				swt.investFromDesc.val("");
				swt.h_investFromDesc.val("");
				swt.navDate.val("");
				swt.postDate.val("");
				
				swt.saCodeId.value("");
				swt.saCode.value("");
				
				swt.trnSABranchCode.removeClass('fieldError');
				swt.trnSABranchCode.val("");
				swt.trnSABranch.val("");
				swt.trnSABranchDesc.val("");
				swt.h_trnSABranchDesc.val("");
				attachPopupTrnSABranch(0);
				attachPopupInvestmentNull();
				
//				swt.outAccNo.value("");
//				swt.outInvest.value("");
//				swt.outInvestKey.value("");
//				swt.outInvestDesc.value("");
//
//				swt.outInvestBank.value("");
//				swt.outInvestBankName.value("");
				
				swt.fundCodeTo.value("");
				swt.fundCodeToKey.value("");
				swt.fundCodeToDesc.value("");
				
				swt.fmgrCode.value("");
				//swt.saCode.value("");
				swt.navDate.value("");
				swt.postDate.value("");
				
				swt.investTo.value("");
				swt.investToKey.value("");
				swt.investToDesc.value("");
				
				swt.inAccNo.value("");
				swt.inInvest.value("");
				swt.inInvestKey.value("");
//				swt.inInvestDesc.value("");
				
				swt.inInvestBank.value("");
				swt.inInvestBankName.value("");
				
				swt.inFundCd.value("");
				swt.inFund.value("");
				swt.inFundKey.value("");
//				swt.inInvestDesc.value("");
				
				swt.inFundBank.value("");
				swt.inFundBankName.value("");
				
				swt.outAvailUnit.value("");
				swt.outAvailUnitStripped.value("");
				
				swt.outAvailBal.value("");
				swt.outAvailBalStripped.value("");
			}
			
			
//			swt.investFrom.value("");
//			swt.investFromKey.value("");
//			swt.investFromDesc.value("");
			
			
			
		}
		
		function clearInvestTo() {

			swt.investTo.value("");
			swt.investToKey.value("");
			swt.investToDesc.value("");
			
			swt.inAccNo.value("");
			swt.inInvest.value("");
			swt.inInvestKey.value("");
//			swt.inInvestDesc.value("");
			
			swt.inInvestBank.value("");
			swt.inInvestBankName.value("");
			
			attachPopupInvestmentTo("_");
		}
		
		function clearValueToInvestFrom(){
			swt.fundCodeTo.value("");
			swt.fundCodeToKey.value("");
			swt.fundCodeToDesc.value("");
			
			swt.investTo.value("");
			swt.investToKey.value("");
			swt.investToDesc.value("");
			
			swt.inAccNo.value("");
			swt.inInvest.value("");
			swt.inInvestKey.value("");
//			swt.inInvestDesc.value("");
			
			swt.inInvestBank.value("");
			swt.inInvestBankName.value("");
			
			swt.inFundCd.value("");
			swt.inFund.value("");
			swt.inFundKey.value("");
			
			swt.inFundBank.value("");
			swt.inFundBankName.value("");
			
			attachPopupBankAccountProdIn("_");
			attachPopupBankAccountIn("_");
			attachPopupBankAccountOut("_");
			
			swt.outAvailUnit.value("");
			swt.outAvailUnitStripped.value("");
			
			swt.outAvailBal.value("");
			swt.outAvailBalStripped.value("");
		}
		
		function clearValueToInvestTo(){
			swt.inAccNo.value("");
			swt.inInvest.value("");
			swt.inInvestKey.value("");
//			swt.inInvestDesc.value("");
			
			swt.inInvestBank.value("");
			swt.inInvestBankName.value("");
		}
		
		function attachPopupBankAccountIn(data) {
			if (subRgInvestment) {
				swt.inInvest.popupBankAccountInvt("?accountnumber="+data, null, function(data){
					subRgInvestment.bankAccount = data;
					
					swt.inInvestBank.value(subRgInvestment.bankAccount.bankCode.thirdPartyCode);
					swt.inInvestBankName.value(subRgInvestment.bankAccount.bankCode.thirdPartyName);
				});
			}
		}
		
		function attachPopupBankAccountOut(data) {
			if (redRgInvestment) {
				swt.outInvest.popupBankAccountInvt("?accountnumber="+data, null, function(data){
					redRgInvestment.bankAccount = data;
					swt.outInvestBank.value(redRgInvestment.bankAccount.bankCode.thirdPartyCode);
					swt.outInvestBankName.value(redRgInvestment.bankAccount.bankCode.thirdPartyName);
				});
			}
		}
		
		function attachPopupBankAccountProdOut(data) {
			if (data) {
				swt.outFund.popupBankAccountProd("?productCode="+data, null, function(data){
					//rgProduct.bankAccount = data;
					swt.outFund.value(data.bankAccountNo);
					swt.outFundKey.value(data.bankAccountKey);	
					swt.outFundBank.value(data.thirdPartyCode);
					swt.outFundBankName.value(data.thirdPartyName);
				});
			}
		}
		
		function attachPopupBankAccountProdIn(data) {
			if (data) {
				swt.inFund.popupBankAccountProd("?productCode="+data, null, function(data){
					//rgProduct.bankAccount = data;
					swt.inFund.value(data.bankAccountNo);
					swt.inFundKey.value(data.bankAccountKey);	
					swt.inFundBank.value(data.thirdPartyCode);
					swt.inFundBankName.value(data.thirdPartyName);
				});
			}
		}
		
		//Invesment Account From
		function attachPopupInvestmentFrom(data) {

//			swt.investFrom.popupInvestmentByCustomer("?customercode="+swt.customer.val(), "invtAcctTo", function(data){
			swt.investFrom.popupInvestmentByProduct("?productCode="+data, "invtAcctTo", function(data){
				if (data) {
					redRgInvestment = data;
					redRgPortfolio = html.getRgPortfolio(redRgInvestment.rgProduct.productCode, swt.investFrom.val(), swt.postDate.val().fmtYYYYMMDD("/"), function(data){
						if (data) {
							redRgPortfolio = data;
							calcRgPortfolio();
						}
					});
					
					
					redProductCode = redRgInvestment.rgProduct.productCode;
					swt.lookupSwiTierType.value(redRgInvestment.rgProduct.lookupBySwiTierType.lookupId);

					var goodFundDate = swt.goodFundDate.val().fmtYYYYMMDD("/");
					
					var newGoodFundDate = html.addCutOfTime(goodFundDate, redRgInvestment.rgProduct.swiCot) + "";
					if (goodFundDate != newGoodFundDate) {
						goodFundDate = newGoodFundDate;
						swt.goodFundDate.value(goodFundDate.toMMDDYYYY("/"));
					}
					
					var navDate = html.getWorkingDate(goodFundDate, redRgInvestment.rgProduct.swiNavUsed) + "";
					var postDate = html.getWorkingDate(goodFundDate, redRgInvestment.rgProduct.swiPostPeriod) + "";
					
					swt.navDate.value(navDate.toMMDDYYYY("/"));
					swt.postDate.value(postDate.toMMDDYYYY("/"));
					
					swt.outTaxPct.value(redRgInvestment.rgProduct.taxMasterByRedTaxKey.taxRate, true);
					swt.fmgrCode.value(redRgInvestment.rgProduct.thirdPartyByFundManager.thirdPartyCode);
					swt.saCode.value(redRgInvestment.thirdPartyBySaCode.thirdPartyCode);
					swt.saCodeId.value(redRgInvestment.thirdPartyBySaCode.thirdPartyKey);
//					swt.trnSABranchCode.value(redRgInvestment.thirdPartyByTrnSABranch.thirdPartyCode);
//					swt.trnSABranch.value(redRgInvestment.thirdPartyByTrnSABranch.thirdPartyKey);
//					swt.trnSABranchDesc.value(redRgInvestment.thirdPartyByTrnSABranch.thirdPartyName);
					swt.trnSABranchKey.value(redRgInvestment.thirdPartyByTrnSABranch.trnSABranchKey);
					gnThdPrty = html.getGnThdPrty(swt.trnSABranchKey.val());
					if(gnThdPrty){
						swt.trnSABranchCode.value(gnThdPrty.name);
						swt.trnSABranch.value(gnThdPrty.code);
						swt.trnSABranchDesc.value(gnThdPrty.description);
					}
					attachPopupTrnSABranch(redRgInvestment.thirdPartyBySaCode.thirdPartyKey);
					
					swt.outAccNo.value(swt.investFrom.val());
					if (redRgInvestment.bankAccount) {
						swt.outInvest.value(redRgInvestment.bankAccount.accountNo);
						swt.outInvestKey.value(redRgInvestment.bankAccount.bankAccountKey);
						//swt.outInvestDesc.value(redRgInvestment.bankAccount.name);
						
						swt.outInvestBank.value(redRgInvestment.bankAccount.bankCode.thirdPartyCode);
						swt.outInvestBankName.value(redRgInvestment.bankAccount.bankCode.thirdPartyName);
					}
					
					swt.outAmountRoundValue.value(redRgInvestment.rgProduct.amountRoundValue);
					swt.outAmountRoundType.value(redRgInvestment.rgProduct.amountRoundType);
					swt.outUnitRoundValue.value(redRgInvestment.rgProduct.unitRoundValue);
					swt.outUnitRoundType.value(redRgInvestment.rgProduct.unitRoundType);
					swt.outPriceRoundValue.value(redRgInvestment.rgProduct.priceRoundValue);
					swt.outPriceRoundType.value(redRgInvestment.rgProduct.priceRoundType);
					
					swt.customerKey.value(redRgInvestment.customer.customerKey);
					$('#navDate').removeClass('fieldError');
					$('#postDate').removeClass('fieldError');
					$('#navDateError').html('');
					$('#postDateError').html('');
					attachPopupBankAccountOut(redRgInvestment.accountNumber.replace(" ","+"));
					if ((swt.investFrom.isChange()) || (swt.investFrom.val() == "")){
						clearValueToInvestFrom();
					}
					
				}
				
			});			
		}
		
		// investment account To
		function attachPopupInvestmentTo(data) {
			var currencyCode = "";
			if (redRgInvestment)
				currencyCode = redRgInvestment.rgProduct.currency.currencyCode;
//			swt.investTo.popupInvestmentByCustomer("?customercode="+swt.customer.val()+"&fmgrCode="+swt.fmgrCode.val()+"&saCode="+swt.saCode.val()+"&currencyCode="+currencyCode, "remarks", function(data){
			swt.investTo.popupInvestmentByProduct("?productCode="+data+"&customerKey="+swt.customerKey.val()+"&fmgrCode="+swt.fmgrCode.val()+"&saCode="+swt.saCode.val()+"&currencyCode="+currencyCode, "remarks", function(data){
				if (data) {
					if (swt.investFrom.val() == swt.investTo.val()){
						swt.investTo.addClass('fieldError');
						$("#errInvestTo").html("should not be the same with Investment Acc From");
						return false;
					}
					swt.investTo.removeClass('fieldError');
					$("#errInvestTo").html("");
					subRgInvestment = data;
					
					subProductCode = subRgInvestment.rgProduct.productCode;
				//	swt.inProductCode.value(subRgInvestment.rgProduct.productCode);
					swt.inTaxPct.value(subRgInvestment.rgProduct.taxMasterBySubTaxKey.taxRate, true);
					swt.inAccNo.value(swt.investTo.val());
					
					
					if (subRgInvestment.bankAccount) {
						swt.inInvest.value(subRgInvestment.bankAccount.accountNo);
						swt.inInvestKey.value(subRgInvestment.bankAccount.bankAccountKey);
//						swt.inInvestDesc.value(subRgInvestment.bankAccount.name);
						
						swt.inInvestBank.value(subRgInvestment.bankAccount.bankCode.thirdPartyCode);
						swt.inInvestBankName.value(subRgInvestment.bankAccount.bankCode.thirdPartyName);
					}
					
					swt.inAmountRoundValue.value(subRgInvestment.rgProduct.amountRoundValue);
					swt.inAmountRoundType.value(subRgInvestment.rgProduct.amountRoundType);
					swt.inUnitRoundValue.value(subRgInvestment.rgProduct.unitRoundValue);
					swt.inUnitRoundType.value(subRgInvestment.rgProduct.unitRoundType);
					swt.inPriceRoundValue.value(subRgInvestment.rgProduct.priceRoundValue);
					swt.inPriceRoundType.value(subRgInvestment.rgProduct.priceRoundType);
					
					attachPopupBankAccountIn(subRgInvestment.accountNumber.replace(" ","+"));
					
					swt.navDate.change();
				}
			});
		}
		
		function attachPopupTrnSABranch(data) {
			swt.trnSABranchCode.lookup({
				list:'@{Pick.thirdPartiesWithChild()}?key='+data,
				get:{
					url:'@{Pick.thirdParty()}?type=THIRD_PARTY-SELLING_AGENT',
					success: function(data){
							swt.trnSABranchCode.removeClass('fieldError');
							swt.trnSABranch.val(data.code);
							swt.trnSABranchDesc.val(data.description);
							swt.h_trnSABranchDesc.val(data.description);
							swt.trnSABranchKey.val(data.code);
					},
					error: function(data){
						swt.trnSABranchCode.addClass('fieldError');
						swt.trnSABranchCode.val('');
						swt.trnSABranch.val('');
						swt.trnSABranchDesc.val('NOT FOUND');
						swt.h_trnSABranchDesc.val('');
						swt.trnSABranchKey.val('');
						
					}
				},
				description:swt.trnSABranchDesc,
				help:swt.trnSABranchHelp
			});
		}
		
		function attachPopupInvestmentNull() {
			swt.outInvest.value("");
			swt.outInvestKey.value("");
			//swt.outInvestDesc.value("");
			//swt.h_outInvestDesc.value("");
			
			swt.outAccNo.value("");
			swt.h_outAccNo.value("");
			
			swt.outInvestBank.value("");
			swt.outInvestBankName.value("");
			
		}
		
		
		function cancelDateValidation(){
			var cancelPostDate = new Date(swt.cancelPostDate.val()).getTime();
			var cancelDate = new Date(swt.cancelDate.val()).getTime();
			var currentAppDate = new Date(swt.currentBusinessDate.val()).getTime();
			var goodFundDate = new Date(swt.goodFundDate.val()).getTime();
			
			if (((currentAppDate!='')&&(cancelDate!=''))||((goodFundDate!='')&&(cancelDate!=''))||((cancelPostDate!='')&&(cancelDate!=''))){
				if (cancelDate > currentAppDate){
					$('#cancelDate').addClass('fieldError');
					$('#cancelDateError').html("Cannot be later than Application Date ("+$("#currentBusinessDate").val()+")");
					return false;
				} else if (cancelDate < goodFundDate){
					$('#cancelDate').addClass('fieldError');
					$('#cancelDateError').html("Cannot be before than Cancelled Transaction Good Fund Date");
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
			}
		}
		
		function cancelPostDateValidation(){
			var cancelPostDate = new Date(swt.cancelPostDate.val()).getTime();
			var cancelDate = new Date(swt.cancelDate.val()).getTime();
			var postDate = new Date(swt.postDate.val()).getTime();
			
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
			}
			
		}
		

/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		
		// === EVENT FOR CANCEL ==== //
		var result;
		
		cancelDateValidation();
		swt.cancelDate.change(function(){
			if (!swt.cancelDate.hasClass('fieldError')){
				result = cancelDateValidation();
				if (result) {
					swt.cancelDateHidden.val(swt.cancelDate.val());
				} else {
					swt.cancelDateHidden.val('');
					$('#h_cancelDate').val('');
				}
			} else {
				swt.cancelDateHidden.val('');
				$('#h_cancelDate').val('');
			}
			
		});
		
		cancelPostDateValidation();
		swt.cancelPostDate.change(function(){
			if (!swt.cancelPostDate.hasClass('fieldError')){
				result = cancelPostDateValidation();
				if (result) {
					swt.cancelPostDateHidden.val(swt.cancelPostDate.val());
				} else {
					swt.cancelPostDateHidden.val('');
					$('#h_cancelPostDate').val('');
				}
			} else {
				swt.cancelPostDateHidden.val('');
				$('#h_cancelPostDate').val('');
			}
			
		});
		
		swt.remarkForCancel.change(function(){
			swt.remarkForCancelHidden.val(swt.remarkForCancel.val());
		});
		// ======================== //	
		
		
		if (!swt.fundCodeFrom.val().isEmpty()) {
			var rgProduct = html.getRgProduct(swt.fundCodeFrom.val());
			swt.effectiveDate.val(rgProduct.effectiveDate);
		}
		
		html.clazz('calendar').datepicker();
		
		swt.switchTab.tabs();
		swt.switchTab.css('height', '490');
		
/*		swt.customer.popupCustomerInvestment("invtAcctFrom", function(data){
			clearInvestForm();
			attachPopupInvestmentFrom();
		});
*/
		swt.fundCodeFrom.popupProduct("investFrom", function(data){
			
			swt.outFundCd.value(data.productCode);
			swt.outFund.value(data.bankAccount.accountNo);
			swt.outFundKey.value(data.bankAccount.bankAccountKey);
			
			swt.outFundBank.value(data.bankAccount.bankCode.thirdPartyCode);
			swt.outFundBankName.value(data.bankAccount.bankCode.thirdPartyName);
			
			attachPopupBankAccountProdOut(data.productCode.replace(" ","+"));
			clearInvestForm();
			//attachPopupInvestmentFrom();
			attachPopupInvestmentFrom(swt.outFundCd.val().replace(" ","+"));
			swt.effectiveDate.val(data.effectiveDate);
		
			/*var goodFundDate = swt.goodFundDate.val().fmtYYYYMMDD("/");
			
			var newGoodFundDate = html.addCutOfTime(goodFundDate, data.swiCot) + "";
			if (goodFundDate != newGoodFundDate) {
				goodFundDate = newGoodFundDate;
				swt.goodFundDate.value(goodFundDate.toMMDDYYYY("/"));
			}*/
		});
		
		swt.fundCodeTo.popupProduct("investTo", function(data) {
			if (swt.fundCodeTo.val() == swt.fundCodeFrom.val()) {
				swt.fundCodeTo.addClass("fieldError");
				$("#errFundCodeTo").html("should not be the same with Fund Code (From)");
				//swt.investToHelp.disabled();
				return false;
			}
			if (data.thirdPartyByFundManager.thirdPartyCode != swt.fmgrCode.val()){
				swt.fundCodeTo.addClass("fieldError");
				$("#errFundCodeTo").html("Fund Manager ("+data.thirdPartyByFundManager.thirdPartyCode+") in this Fund Code not same with Fmgr Code");
				//swt.investToHelp.disabled();
				return false;
			}
			swt.inFundCd.value(data.productCode);
			swt.inFund.value(data.bankAccount.accountNo);
			swt.inFundKey.value(data.bankAccount.bankAccountKey);
			
			swt.inFundBank.value(data.bankAccount.bankCode.thirdPartyCode);
			swt.inFundBankName.value(data.bankAccount.bankCode.thirdPartyName);
			attachPopupBankAccountProdIn(data.productCode.replace(" ","+"));
			swt.fundCodeTo.removeClass("fieldError");
			$("#errFundCodeTo").html("");
			//swt.investToHelp.enabled();
			if ((swt.fundCodeTo.isChange()) || (swt.fundCodeTo.val() == "")){
				clearInvestTo();
			}
			attachPopupInvestmentTo(data.productCode);
		});
		swt.outTax.popupTax("feePctin");
		
		swt.fundCodeFrom.change(function(){
			if ((swt.fundCodeFrom.isChange()) || swt.fundCodeFrom.val() == ""){
				swt.outFundCd.value("");
				swt.outFund.value("");
				swt.outFundKey.value("");
//				swt.inInvestDesc.value("");
				
				swt.outFundBank.value("");
				swt.outFundBankName.value("");
				
				attachPopupBankAccountProdIn("_");
				attachPopupBankAccountProdOut("_");
				attachPopupBankAccountIn("_");
				attachPopupBankAccountOut("_");
				
				clearInvestForm();	
			}
		});
		
		swt.investFrom.change(function(){
			if ((swt.investFrom.isChange()) || (swt.investFrom.val() == "")){
				clearValueToInvestFrom();
			}
		});
		
		swt.fundCodeTo.change(function(){
			if ((swt.fundCodeTo.isChange()) || (swt.fundCodeTo.val() == "")){
				swt.inFundCd.value("");
				swt.inFund.value("");
				swt.inFundKey.value("");
//				swt.inInvestDesc.value("");
				
				swt.inFundBank.value("");
				swt.inFundBankName.value("");
				
				attachPopupBankAccountIn("_");
				attachPopupBankAccountProdIn("_");
				
				clearInvestTo();
			}
		});
		
		swt.investTo.change(function(){
			if ((swt.investTo.isChange()) || (swt.investTo.val() == "")){
				$('#investTo').removeClass('fieldError');
				$("#errInvestTo").html('');
				
				attachPopupBankAccountIn("_");
				clearValueToInvestTo();
			}
		});
		
		swt.outInvest.blur(function(){
			if((swt.outInvest.val()=="") || (swt.outInvest.isChange())){
				swt.outInvestBank.value("");
				swt.outInvestBankName.value("");
			}
		});
		
		swt.outFund.blur(function(){
			if((swt.outFund.val()=="") || (swt.outFund.isChange())){
				swt.outFundBank.value("");
				swt.outFundBankName.value("");
			}
		});
		
		swt.inInvest.blur(function(){
			if((swt.inInvest.val()=="") || (swt.inInvest.isChange())){
				swt.inInvestBank.value("");
				swt.inInvestBankName.value("");
			}
		});
		
		swt.inFund.blur(function(){
			if((swt.inFund.val()=="") || (swt.inFund.isChange())){
				swt.inFundBank.value("");
				swt.inFundBankName.value("");
			}
		});
		
		swt.tradeDate.change(function(){
			swt.goodFundDate.value(swt.tradeDate.val());
			
			var goodFundDate = swt.goodFundDate.val().fmtYYYYMMDD("/");
			
			if (subRgInvestment) {
				var newGoodFundDate = html.addCutOfTime(goodFundDate, subRgInvestment.rgProduct.swiCot) + "";
				if (goodFundDate != newGoodFundDate) {
					goodFundDate = newGoodFundDate;
					swt.goodFundDate.value(goodFundDate.toMMDDYYYY("/"));
				}
			}
			
			swt.navDate.change();
		});
		
		swt.navDate.change(function(){
			if (!swt.navDate.isEmpty()) {
				redRgNav = html.getRgNav(swt.navDate.val().fmtYYYYMMDD("/"), redProductCode);
				subRgNav = html.getRgNav(swt.navDate.val().fmtYYYYMMDD("/"), subProductCode);
				
				if (redRgNav) swt.outPrice.value(redRgNav.nav, true); 
				if (subRgNav) swt.inPrice.value(subRgNav.nav, true);
			}
		});
		
		// ======================== SWITCH OUT =========================== //
		
		swt.outNetAmt.change(function(){
			var digit =swt.outAmountRoundValue.val();
			var roundType =swt.outAmountRoundType.val();
			var value = swt.outNetAmt.val();
			swt.outNetAmt.valueRnd(value.toNumber(","), true, digit, roundType);
			rgFeeTiers(true);
			calcFeeAmt("out");
			calcDiscAmt("out");
			calcTaxAmt("out");
			calcOtherAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcUnit("out");
			calcCapGainTaxAmt();
			calcPayment("out");
		});
		
		/****  Fee % dan Amount Section ******/
		
		/**** === Fee % == ***/
		swt.outFeePct.change(function(){
			swt.outFeePctStripped.val(swt.outFeePct.val());
			calcFeeAmt("out");
			calcDiscAmt("out");
//			calcOtherAmt("out");  effect on netamount and other pct
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
//			calcUnit("out");  effect on netamount and price
			calcPayment("out");
		});
		
		swt.outFeePct.autoNumeric({vMax: '100'});
		swt.outFeePct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		/**** === Fee Amount == ***/
		swt.outFeeAmt.change(function(){
			var digit =swt.outAmountRoundValue.val();
			var roundType =swt.outAmountRoundType.val();
			var value = swt.outFeeAmt.val();
			swt.outFeeAmt.valueRnd(value.toNumber(","), true, digit, roundType);
			swt.outFeePct.value("");
			calcDiscAmt("out");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcPayment("out");
		});
		
		/****  Discount % dan Amount Section ******/
		
		/**** === Discount % == ***/
		swt.outDiscPct.change(function(){
			swt.outDiscPctStripped.val(swt.outDiscPct.val());
			calcDiscAmt("out");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
//			calcUnit(); effect on netamount and price
			calcPayment("out");
		});	
		
		swt.outDiscPct.autoNumeric({vMax: '100'});
		swt.outDiscPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		/**** === Discount Amount == ***/
		swt.outDiscAmt.change(function(){
			var digit =swt.outAmountRoundValue.val();
			var roundType =swt.outAmountRoundType.val();
			var value = swt.outDiscAmt.val();
			swt.outDiscAmt.valueRnd(value.toNumber(","), true, digit, roundType);
			swt.outDiscPct.value("");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcPayment("out");
			
		});
		
		
		/****  Other % dan Amount Section ******/
		
		/**** === Other % == ***/
		swt.outOthrPct.change(function(){
			swt.outOthrPctStripped.val(swt.outOthrPct.val());
			calcOtherAmt("out");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
//			calcUnit("out");
			calcPayment("out");
		});
		
		swt.outOthrPct.autoNumeric({vMax: '100'});
		swt.outOthrPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		/**** === Other Amount == ***/
		swt.outOthrAmt.change(function(){
			var digit =swt.outAmountRoundValue.val();
			var roundType =swt.outAmountRoundType.val();
			var value = swt.outOthrAmt.val();
			swt.outOthrAmt.valueRnd(value.toNumber(","), true, digit, roundType);
			swt.outOthrPct.value("");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");	
			calcPayment("out");
		});
		
		
		/****  Unit Section ******/
		swt.outUnit.change(function(){
			var digit =swt.outUnitRoundValue.val();
			var roundType =swt.outUnitRoundType.val();
			var value = swt.outUnit.val();
			swt.outUnit.valueRnd(value.toNumber(","), true, digit, roundType);
			if (swt.outPrice.val()!=''){
				calcNetAmount("out");
				calcFeeAmt("out");
				calcDiscAmt("out");
				calcTaxAmt("out");
				calcTotFeeAmt("out");
				calcAmount("out");
				calcUnit("out");
				calcCapGainTaxAmt();
				calcPayment("out");
			}
		});
		
		/****  Price Section ******/
		swt.outPrice.change(function(){
			var digit =swt.outPriceRoundValue.val();
			var roundType =swt.outPriceRoundType.val();
			var value = swt.outPrice.val();
//			swt.outPrice.valueRnd(value.toNumber(","), true, digit, roundType);
//			calcUnit("out");
//			calcNetAmount("out");
//			
//			if (swt.outPrice.val() == 0) {
//				swt.outPrice.addClass('fieldError');
//				outPriceError.html("can not filled 0");
//			} else {
//				swt.outPrice.removeClass('fieldError');
//				outPriceError.html("");
//			}
			swt.outPrice.removeClass('fieldError');
			outPriceError.html("");
			if(value != "") {
				swt.outPrice.valueRnd(value.toNumber(","), true, digit, roundType);
				//calcUnit("out");
				calcNetAmount("out");
				calcFeeAmt("out");
				calcDiscAmt("out");
				calcTaxAmt("out");
				calcTotFeeAmt("out");
				calcAmount("out");
				//calcUnit("out");
				calcCapGainTaxAmt();
				calcPayment("out");
				if (swt.outPrice.val() == 0) {
					swt.outPrice.addClass('fieldError');
					outPriceError.html("can not filled 0");
				}
			} else {
				//swt.outUnit.valueRnd(0, true, digit, roundType);
				swt.outPriceStripped.value("");
				calcNetAmount("out");
				calcFeeAmt("out");
				calcDiscAmt("out");
				calcTaxAmt("out");
				calcTotFeeAmt("out");
				calcAmount("out");
				calcUnit("out");
				calcCapGainTaxAmt();
				calcPayment("out");
			}
		});
		
		/****** Popup Tax *******/
		swt.outTax.popupTax(null, function(data){
			console.log("popup tax");
			redRgTax = html.getRgTax(swt.outTax.val());
			calcCapGainTaxAmt();
			calcPayment("out");
		});
		
		/**** Transaction By Action *****/
		swt.outTradeBy.change(function(){
			viewTradeBy("out");
			calcFeeAmt("out");
			calcDiscAmt("out");
			calcOtherAmt("out");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcPayment("out");
		});
		
		// ============================== END SWITCH OUT ============================= //
		
		
		// =============================== SWITCH IN ================================= //
		
		/****  Fee % dan Amount Section ******/
		
		/**** === Fee % == ***/
		swt.inFeePct.change(function(){
			swt.inFeePctStripped.val(swt.inFeePct.val());
			calcFeeAmt("in");
			calcDiscAmt("in");
//			calcOtherAmt("out");  effect on netamount and other pct
			calcTaxAmt("in");
			calcTotFeeAmt("in");
			calcNetAmount("in");
			calcUnit("in"); 
			//calcPayment("in");
		});
		
		swt.inFeePct.autoNumeric({vMax: '100'});
		swt.inFeePct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		/**** === Fee Amount == ***/
		swt.inFeeAmt.change(function(){
			var digit =swt.inAmountRoundValue.val();
			var roundType =swt.inAmountRoundType.val();
			var value = swt.inFeeAmt.val();
			swt.inFeeAmt.valueRnd(value.toNumber(","), true, digit, roundType);
			swt.inFeePct.value("");
			calcDiscAmt("in");
			calcTaxAmt("in");
			calcTotFeeAmt("in");
			calcNetAmount("in");
			calcUnit("in"); 
		});
		
		
		/****  Discount % dan Amount Section ******/
		
		/**** === Discount % == ***/
		swt.inDiscPct.change(function(){
			swt.inDiscPctStripped.val(swt.inDiscPct.val());
			calcDiscAmt("in");
			calcTaxAmt("in");
			calcTotFeeAmt("in");
			calcNetAmount("in");
			calcUnit("in");
//			calcPayment("in");
		});	
		
		swt.inDiscPct.autoNumeric({vMax: '100'});
		swt.inDiscPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		/**** === Discount Amount == ***/
		swt.inDiscAmt.change(function(){
			var digit =swt.inAmountRoundValue.val();
			var roundType =swt.inAmountRoundType.val();
			var value = swt.inDiscAmt.val();
			swt.inDiscAmt.valueRnd(value.toNumber(","), true, digit, roundType);
			swt.inDiscPct.value("");
			calcTaxAmt("in");
			calcTotFeeAmt("in");
			calcNetAmount("in");
			calcUnit("in"); 
//			calcPayment("out");
		});
		
		

		/****  Other % dan Amount Section ******/
		
		/**** === Other % == ***/
		swt.inOthrPct.change(function(){
			swt.inOthrPctStripped.val(swt.inOthrPct.val());
			calcOtherAmt("in");
			calcTaxAmt("in");
			calcTotFeeAmt("in");
			calcNetAmount("in");
			calcUnit("in");
//			calcPayment("out");
		});	

		swt.inOthrPct.autoNumeric({vMax: '100'});
		swt.inOthrPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		
		/**** === Other Amount == ***/
		swt.inOthrAmt.change(function(){
			var digit =swt.inAmountRoundValue.val();
			var roundType =swt.inAmountRoundType.val();
			var value = swt.inOthrAmt.val();
			swt.inOthrAmt.valueRnd(value.toNumber(","), true, digit, roundType);
			swt.inOthrPct.value("");
			calcTaxAmt("in");
			calcTotFeeAmt("in");
			calcNetAmount("in");	
			calcUnit("in"); 
//			calcPayment("in");
		});
		
		
		/******  Price Section ******/
		swt.inPrice.change(function(){
			var digit =swt.inPriceRoundValue.val();
			var roundType =swt.inPriceRoundType.val();
			var value = swt.inPrice.val();
//			swt.inPrice.valueRnd(value.toNumber(","), true, digit, roundType);
//			calcUnit("in");
//			calcNetAmount("in");
//			
//			if (swt.inPrice.val() == 0) {
//				swt.inPrice.addClass('fieldError');
//				inPriceError.html("can not filled 0");
//			} else {
//				swt.inPrice.removeClass('fieldError');
//				inPriceError.html("");
//			}
			
			swt.inPrice.removeClass('fieldError');
			inPriceError.html("");
			if(value != "") {
				swt.inPrice.valueRnd(value.toNumber(","), true, digit, roundType);
				calcUnit("in");
				calcNetAmount("in");
				if (swt.inPrice.val() == 0) {
					swt.inPrice.addClass('fieldError');
					inPriceError.html("can not filled 0");
				}
			} else {
				swt.inUnit.valueRnd(0, true, digit, roundType);
			}
			
		});
		
		// ============================== END SWITCH IN ============================= //
		
		/**** Switch All Action ****/
		swt.switchAll.change(function(){
			var con = swt.switchAll.checked();
			if (con == 'true') {
				
				swt.outTradeBy.val(TRANSACTION_BY_UNIT).change();
				swt.outTradeByHidden.val(TRANSACTION_BY_UNIT);
				swt.outTradeBy.disabled();
				swt.outUnit.disabled();
				
				var digit =swt.outUnitRoundValue.val();
				var roundType =swt.outUnitRoundType.val();
//				var value = swt.outUnit.val();
//				swt.outUnit.valueRnd(value.toNumber(","), true, digit, roundType);
				
				swt.outUnit.valueRnd(redRgPortfolio.unit, true, digit, roundType);
				calcNetAmount("out");
			}
			if (con == 'false') {
				swt.outTradeBy.removeAttr("disabled");
				swt.outUnit.enabled();
			}
		});
		
		
		/****** Fund Code From Action ******/
		swt.fundCodeFrom.blur(function(){
			if((swt.fundCodeFrom.isChange()) || (swt.fundCodeFrom.val() == "")){
				swt.investFrom.removeClass('fieldError');
				swt.investFrom.val("");
				swt.investFromKey.val("");
				swt.investFromDesc.val("");
				swt.h_investFromDesc.val("");
				swt.navDate.val("");
				swt.postDate.val("");
				
				swt.saCodeId.value("");
				swt.saCode.value("");
				
				swt.trnSABranchCode.removeClass('fieldError');
				swt.trnSABranchCode.val("");
				swt.trnSABranch.val("");
				swt.trnSABranchDesc.val("");
				swt.h_trnSABranchDesc.val("");
				attachPopupTrnSABranch(0);
				attachPopupInvestmentNull();
				attachPopupInvestmentFrom("_");
				attachPopupInvestmentTo("_");
			}
		});
		
		/****** Investment Account From Action ******/
		swt.investFrom.blur(function(){
			if((swt.investFrom.isChange()) || (swt.investFrom.val() == "")){
				swt.saCodeId.value("");
				swt.saCode.value("");
				
				swt.saCode.removeClass('fieldError');
				swt.trnSABranchCode.val("");
				swt.trnSABranch.val("");
				swt.trnSABranchDesc.val("");
				swt.h_trnSABranchDesc.val("");
				attachPopupTrnSABranch(0);
				attachPopupInvestmentNull();
				attachPopupInvestmentTo("_");
				
				swt.fmgrCode.value("");
				swt.navDate.value("");
				swt.postDate.value("");
			}
		});
		
		viewTradeBy("out");
		calcTaxAmt("out");
		calcTotFeeAmt("out");
		calcPayment("out");

		if (swt.inFeePct.val() != "") {
			calcFeeAmt("in");
		}
		if (swt.inDiscPct.val() != "") {
			calcDiscAmt("in");
		}
		calcTaxAmt("in");


		if (swt.inOthrPct.val() != "") {
			calcOtherAmt("in");
		}
		
		var dataType =0;
		if(swt.saCode.val() != ""){
			dataType = swt.saCodeId.val();
		}
		attachPopupTrnSABranch(dataType);
		
		
		
//		swt.trnSABranchCode.blur(function(){
//			if(swt.trnSABranchCode.val() == ""){
//				swt.trnSABranchCode.removeClass('fieldError');
//				swt.trnSABranchCode.val("");
//				swt.trnSABranch.val("");
//				swt.trnSABranchDesc.val("");
//				swt.h_trnSABranchDesc.val("");
//				attachPopupOpeningSABranch(0);
//			}
//				
//		});
	
		calcFeeAmt("in");
		calcDiscAmt("in");
		calcTotFeeAmt("in");

		calcRgPortfolio();
		calcCapGainTaxAmt();
//		attachPopupInvestmentFrom();
//		attachPopupBankAccountOut();
//		attachPopupInvestmentTo();
//		attachPopupBankAccountIn();

		var fundCdFrom ="_";
		if(swt.fundCodeFrom.val() != ""){
			fundCdFrom = swt.fundCodeFrom.val();
		}
		attachPopupInvestmentFrom(fundCdFrom);
		
		var fundCdTo ="_";
		if(swt.fundCodeTo.val() != ""){
			fundCdTo = swt.fundCodeTo.val();
		}
		attachPopupInvestmentTo(fundCdTo);
		
		
		var outAccNo ="_";
		if(swt.outAccNo.val() != ""){
			outAccNo = swt.outAccNo.val();
		}
		attachPopupBankAccountOut(outAccNo);
		
		var inAccNo ="_";
		if(swt.inAccNo.val() != ""){
			inAccNo = swt.inAccNo.val();
		}
		attachPopupBankAccountIn(inAccNo);
		
		var accountNoProdIn ="_";
		if(swt.inFundCd.val() != ""){
			accountNoProdIn = swt.inFundCd.val();
		}
		attachPopupBankAccountProdIn(accountNoProdIn);
		
		var accountNoProdOut ="_";
		if(swt.outFundCd.val() != ""){
			accountNoProdOut = swt.outFundCd.val();
		}
		attachPopupBankAccountProdOut(accountNoProdOut);
		
//		var accountNoFrom =" ";
//		if(swt.investFrom.val() != ""){
//			accountNoFrom = sub.investFrom.val();
//		}
//		attachPopupBankAccountFrom(accountNoFrom);
	
//		var accountNoProdFrom =" ";
//		if(swt.outFundCd.val() != ""){
//			accountNoProdFrom = sub.outFundCd.val();
//		}
//		attachPopupBankAccountProdFrom(accountNoProdFrom);
		
		if (swt.dispatch.val() == "confirm") {
			alert("Data Subscribe save with id "+swt.switchingKey.val());
		}
		if (swt.dispatch.val() == "confirm") {
			location.href='@{entry()}';
		}
	}else{
		return new Switch(html);
	}
}
	