function Redemption(html) {
	if (this instanceof Redemption) {
/* =========================================================================== 
 * Constant
 * ========================================================================= */
		var TRANSACTION_BY_UNIT = "TRANSACTION_BY_UNIT";
		var TRANSACTION_BY_AMOUNT = "TRANSACTION_BY_AMOUNT";
		
		var REDEMPTION = "REDEMPTION";
		
		var digitAmount;
		var typeAmount;
		var digitUnit;
		var typeUnit;
		var digitPrice;
		var typePrice;
		
		var priceError = $("#priceErrorMessage");
		var rgInvestment = {};
		var rgProduct = null;
		var isApproval = '${isApproval}';
/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var red = html.inject(this);		
		red["liquidation"] = html.name("red.liquidation");
		
/* =========================================================================== 
 * Variable
 * ========================================================================= */

		var aNavDate = red.navDate.datepicker("getDate");
		var formattedNavDate = $.datepicker.formatDate('yymmdd', aNavDate);

		var aTradeDate = red.tradeDate.datepicker("getDate");
		var formattedTradeDate = $.datepicker.formatDate('yymmdd', aTradeDate);

		var aPostDate = red.postDate.datepicker("getDate");
		var formattedPostDate = $.datepicker.formatDate('yymmdd', aPostDate);

		var rgNav = (red.navDate.isEmpty() || red.fundCode.isEmpty()) ? null : html.getRgNav(formattedNavDate, red.fundCode.val());
		//var rgPortfolio = (red.fundCode.isEmpty() || red.invtAcct.isEmpty() || red.tradeDate.isEmpty()) ? null : html.getRgPortfolio(red.fundCode.val(), red.invtAcct.val(), formattedTradeDate);
		var rgPortfolio = (red.fundCode.isEmpty() || red.invtAcct.isEmpty() || red.postDate.isEmpty()) ? null : html.getRgPortfolio(red.fundCode.val(), red.invtAcct.val(), formattedPostDate);
		var rgTax = (red.tax.isEmpty()) ? null : html.getRgTax(red.tax.val());

/* =========================================================================== 
 * Method
 * ========================================================================= */		
		function calcRgProduct() {
			console.log('calcRgProduct');		
			var aDate = red.goodFundDate.datepicker("getDate");
			var goodFundDate = $.datepicker.formatDate("yymmdd", aDate);
			var newGoodFundDate = html.addCutOfTime(goodFundDate, (rgProduct.redCot ? rgProduct.redCot : 0)) + "";
			if (goodFundDate != newGoodFundDate) {
				goodFundDate = newGoodFundDate;
				aDate = $.datepicker.parseDate('yymmdd', goodFundDate, null);
				red.goodFundDate.value( $.datepicker.formatDate('${appProp?.jqueryDateFormat}', aDate) );
			}
			
			var navDate = html.getWorkingDate(goodFundDate, (rgProduct.redNavUsed ? rgProduct.redNavUsed : 0 )) + "";
			var aNavDate = $.datepicker.parseDate('yymmdd', navDate, null);
			var postDate = html.getWorkingDate(goodFundDate, (rgProduct.redPostPeriod ? rgProduct.redPostPeriod : 0 )) + "";
			var aPostDate = $.datepicker.parseDate('yymmdd', postDate, null);
			var paymentDate = html.getWorkingDate(goodFundDate, (rgProduct.redPayPeriod ? rgProduct.redPayPeriod : 0 )) + "";
			var aPaymentDate = $.datepicker.parseDate('yymmdd', paymentDate, null);
			
			red.navDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aNavDate));
			red.postDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aPostDate));
			red.defaultPostDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aPostDate));
			red.paymentDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aPaymentDate));
			red.taxPct.value(rgProduct.taxMasterByRedTaxKey.taxRate, true);
		}
		
		function checkFeeBy(){
			if( $("#feeby1").attr("disabled") != true ){
				if( $("#feeby1").attr("checked") == true ){
					red.feePct.removeAttr("disabled");
					red.feeAmt.attr("disabled", true);
					red.feeAmt.valueRnd("", true, digitAmount, typeAmount);
					red.feeAmt.val("");
					red.feeAmtStripped.val("");
					red.feePct.val("0");
					red.feePct.change();
					rgFeeTiersOnlyFee(true);
				}
			}
			if( $("#feeby2").attr("disabled") != true ){
				if( $("#feeby2").attr("checked") == true ){
					red.feeAmt.removeAttr("disabled");
					red.feePct.attr("disabled", true);
					red.feePct.val("");
				}
			}
		}
		
		function calcRgPortfolio() {
			console.log("calcRgPortfolio");
			if (rgPortfolio) {
				console.log( "found rgPortfolio..." );
	
				if(digitUnit == "undefined" ||  typeAmount == "typeUnit")
					getVarGlobal();
				
				var tmpUnit = (rgPortfolio.unit ? rgPortfolio.unit.toFixed(digitUnit) : 0);
				red.availUnit.valueRnd((tmpUnit), true, digitUnit, typeUnit);
				var tmpUnitFix = (rgPortfolio.unit ? rgPortfolio.unit : 0);
				red.availUnitStripped.val(tmpUnitFix.noExponents());
				red.availBal.value(rgPortfolio.balanceAmount, true);
			}
			else
			{
				red.availUnit.valueRnd(0, true, digitUnit, typeUnit);
				red.availBal.value(0, true);
			}
		}
		
		function calcFeeAmt() {
			console.log('calcFeeAmt');
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit")
				getVarGlobal();
			if( $("#feeby1").attr("checked") == true ){
				red.feeAmt.calcRedFeeAmount(red.netAmt, red.feePct, red.tradeBy, digitAmount, typeAmount);
				if ((red.netAmt.val() == "")||(red.netAmt.val() == 0)) {
					red.feeAmt.val('');
					red.feeAmtStripped.val('');
				}
			}else{
				// just leave value of fee amt as it is
			}
		}
		
		function calcDiscAmt() {
			console.log('calcDiscAmt');
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit")
				getVarGlobal();
			red.discAmt.calcRedDiscAmount(red.feeAmt, red.discPct, digitAmount, typeAmount);
		}
		
		function calcOtherAmt() {
			console.log('calcOtherAmt');
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit")
				getVarGlobal();
			red.otherAmt.calcRedOtherAmount(red.netAmt, red.otherPct, digitAmount, typeAmount);
		}
		
		function calcTaxAmt() {
			console.log('calcTaxAmt');
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit")
				getVarGlobal();
			red.taxAmt.calcRedTaxFeeAmount(red.taxPct, red.feeAmt, red.discAmt, red.otherAmt, digitAmount, typeAmount);
		}
		
		function calcTotFeeAmt() {
			console.log('calcTotFeeAmt');
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit")
				getVarGlobal();
			red.totFeeAmt.calcRedTotalFeeAmount(red.feeAmt, red.discAmt, red.otherAmt, red.taxAmt, digitAmount, typeAmount);
		}
		
		function calcAmount() {
			console.log('calcAmount');
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit")
				getVarGlobal();
			red.amount.calcRedAmount(red.feeAmt, red.netAmt, red.totFeeAmt, digitAmount, typeAmount);
			if (red.netAmt.isEmpty()) {
				red.amount.val('');
				red.amountStripped.val('');
			}
		}
		
		function calcUnit() {
			console.log('calcUnit');
			if( digitUnit == "undefined" ||  typeAmount == "typeUnit")
				getVarGlobal();
			red.unit.calcRedUnit(red.netAmt, red.price, red.tradeBy, digitUnit, typeUnit);
		}
		
		function calcNetAmount() {
			console.log('calcNetAmount');
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit")
				getVarGlobal();
			red.netAmt.calcRedNetAmount(red.price, red.unitStripped, red.tradeBy, digitAmount, typeAmount);
		}
		
		function calcCapGainTaxAmt() {
			console.log('calcCapGainTaxAmt');
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit")
				getVarGlobal();
			if (rgTax) {
				red.capGainTaxAmount.calcRedCapGainTaxAmount(Number(rgTax.taxRate), red.netAmt, digitAmount, typeAmount);
				if (red.amount.val() == 0) {
					red.capGainTaxAmount.val(0);
				}
			}
		}
		
		function calcRgTax() {
			console.log('calcRgTax');
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit")
				getVarGlobal();
			if (rgTax) {
				red.payAmt.calcRedPayment(Number(rgTax.taxRate), red.netAmtStripped, red.amount, digitAmount, typeAmount);
				if (red.amount.val() == 0) {
					red.payAmt.val(0);
				}
			}
		}
		
		function viewTradeBy() {
			var con = red.liquidation.checked();
			var disabled = red.tradeBy.attr("disabled");
			
			if (red.tradeBy.val() == TRANSACTION_BY_AMOUNT) {
				red.BYAMOUNT.append(red.pPrice.load()).append(red.pUnit.load());
				red.unit.disabled();
				red.netAmt.enabled();
				$("#redemptionTab #tab-1 p[id=grossAmount] label span").html("*");
				$("#redemptionTab #tab-1 p[id=pUnit] label span").html("");
				if (disabled) {
					red.unit.disabled();
					red.netAmt.disabled();
				}
			}
			
			
			if (red.tradeBy.val() == TRANSACTION_BY_UNIT) {
				red.BYUNIT.append(red.pUnit.load()).append(red.pPrice.load());
				red.unit.enabled();
				red.netAmt.disabled();
				$("#redemptionTab #tab-1 p[id=grossAmount] label span").html("");
				$("#redemptionTab #tab-1 p[id=pUnit] label span").html("*");
				if (disabled) red.unit.disabled();
				if (con == 'true') {
					red.unit.disabled();
					red.tradeBy.disabled();
					$("#redemptionTab #tab-1 p[id=pUnit] label span").html("");
					$("#redemptionTab #tab-1 p[id=pTradeBy] label span").html("");
//					red.unit.value(red.availUnit.val(), true);
				}
			}
			
			red.tradeByHidden.val(red.tradeBy.val());
		}
		
		
		function rgFeeTiers(isImplement) {
			console.log('rgFeeTiers');
			if (rgProduct){
				var amount = red.netAmt.val().toNumber(",");
				var tierType = rgProduct.lookupByRedTierType.lookupId;
				var productCode = rgProduct.productCode;
				var type = REDEMPTION;
				
				if (amount != 0) {
					html.setFeeTier(tierType, productCode, amount, red.feePct, red.feeAmt, type);
					if( red.feePct.val() > 0 ){
						$("#feeby1").attr("checked", true); 
						red.feeAmt.attr("disabled", true);
					}
					if (red.tradeBy.val() == TRANSACTION_BY_AMOUNT) {
						calcUnit();
					}
					calcAmount();
					
					calcFeeAmt();
					calcDiscAmt();
					calcTaxAmt();
					calcOtherAmt();
				}
			}
		}
		
		function rgFeeTiersOnlyFee(isImplement) {
			console.log('rgFeeTiers');
			var amount = red.netAmt.val().toNumber(",");
			if (rgProduct){
				var tierType = rgProduct.lookupByRedTierType.lookupId;
				var productCode = rgProduct.productCode;
				var type = REDEMPTION;
				
				html.setFeeTier(tierType, productCode, amount, red.feePct, red.feeAmt, type);
				if( red.feePct.val() > 0 ){
					$("#feeby1").attr("checked", true); 
					red.feeAmt.attr("disabled", true);
				}
				if (red.tradeBy.val() == TRANSACTION_BY_AMOUNT) {
					calcUnit();
				}
				calcAmount();
				
				calcFeeAmt();
				calcDiscAmt();
				calcTaxAmt();
				calcOtherAmt();
			}
		}
		
				
		function cancelDateValidation(){
			var cancelPostDate = new Date(red.cancelPostDate.val()).getTime();
			var cancelDate = new Date(red.cancelDate.val()).getTime();
			var currentAppDate = new Date(red.currentBusinessDate.val()).getTime();
			var goodFundDate = new Date(red.goodFundDate.val()).getTime();
			
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
			var cancelPostDate = new Date(red.cancelPostDate.val()).getTime();
			var cancelDate = new Date(red.cancelDate.val()).getTime();
			var postDate = new Date(red.postDate.val()).getTime();
			
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
		
		// harus di pancing dahulu baru yang replace jalan
		red.invtAcct.dynapopup('PICK_RG_INVEST_BY_PROD', 'null', "tradeDate");
		
		function attachPopupInvestment() {
			if (rgProduct) {
				var productCode = red.fundCode.val();
				productCode = productCode.replace(/#/g, '${specialChar}').toString();

//				red.invtAcct.popupInvestmentAcct("?type=" + productCode, "tradeDate", function(data){
				red.invtAcct.dynapopup('PICK_RG_INVEST_BY_PROD', productCode, "tradeDate", function(data) {
					html.getRgInvestmentAcct( red.fundCode.val().replace(/#/g, '${specialChar}').toString(), red.invtAcct.val(), function(data){
						red.investorName.html( "" );
						if (data) {
							rgInvestment = data;
							red.investorName.html( rgInvestment.name );
							
							red.saCodeId.value(rgInvestment.thirdPartyBySaCode.thirdPartyKey);
							red.saCode.value(rgInvestment.thirdPartyBySaCode.thirdPartyCode);
							red.h_saCode.value(rgInvestment.thirdPartyBySaCode.thirdPartyCode);
							red.saCodeDesc.value(rgInvestment.thirdPartyBySaCode.thirdPartyName);
							red.h_saCodeDesc.value(rgInvestment.thirdPartyBySaCode.thirdPartyName);
							red.trnSABranchKey.value(rgInvestment.thirdPartyByTrnSABranch.trnSABranchKey);
							gnThdPrty = html.getGnThdPrty(red.trnSABranchKey.val());
							if(gnThdPrty){
								red.trnSABranchCode.value(gnThdPrty.name);
								red.trnSABranch.value(gnThdPrty.code);
								red.trnSABranchDesc.value(gnThdPrty.description);
							}
							
							var aTradeDate = red.tradeDate.datepicker("getDate");
							var formattedTradeDate = $.datepicker.formatDate('yymmdd', aTradeDate);
							
							var aPostDate = red.postDate.datepicker("getDate");
							var formattedPostDate = $.datepicker.formatDate('yymmdd', aPostDate);
							
							html.getRgPortfolio(rgProduct.productCode, red.invtAcct.val(), formattedPostDate, function(data){
								if (data) {
									rgPortfolio = data;
									calcRgPortfolio();
								}
								else
								{
									red.availUnit.valueRnd(0, true, digitUnit, typeUnit);
									red.availBal.value(0, true);
								}
							});
							
							if (rgInvestment.bankAccount) {
								if( rgInvestment.bankAccount.currency && rgProduct.currencyCode ){
									if( rgInvestment.bankAccount.currency.currencyCode ==  rgProduct.currencyCode ){
										red.investor.value(rgInvestment.bankAccount.accountNo);
										red.investorKey.value(rgInvestment.bankAccount.bankAccountKey);
										//red.investDesc.value(rgInvestment.bankAccount.name);
										red.investorBankCode.value(rgInvestment.bankAccount.bankCode.thirdPartyCode);
										red.investorBankName.value(rgInvestment.bankAccount.name);
										red.investorCurrency.value(rgInvestment.bankAccount.currency.currencyCode);										
									}
								}
							}
							attachFundAccountLookup();
							
							red.saCode.removeClass('fieldError');
						}
					}, function(data){
						red.saCodeId.value("");
						red.saCode.value("");
						red.h_saCode.value("");
						red.saCodeDesc.value("");
						red.h_saCodeDesc.value("");
					});
				});				
			}
		}
		
		function attachFundAccountLookup(){
			// fund bank account
			var customerKey = 0;
			if( rgInvestment.customer ){
				customerKey = rgInvestment.customer.customerKey;
			}
			var productCode = red.fundCode.val();
			productCode = productCode.replace(/#/g, '${specialChar}').toString();
			
			/*red.fund.popupFundAccount(productCode, "fund", function(data){
				red.fund.value( data.bankAccountNo );
				red.fundBankCode.value( data.bankCode.thirdPartyCode );
				red.fundBankName.value( data.name );
				red.fundCurrency.value( data.currency.currencyCode );
			}, function(){
				red.fund.value( "" );
				red.fundBankCode.value( "" );
				red.fundBankName.value( "NOT FOUND" );
				red.fundCurrency.value( "" );
			});*/
			
			//red.fund.dynapopup('PICK_BN_ACCOUNT_BY_RG_PRODUCT_BN_ACCOUNT',productCode, "fund", function(data){
			red.fund.dynapopup('PICK_BN_ACCOUNT_BY_RG_PRODUCT_BN_ACCOUNT_DOMAIN',productCode+"|${domainBankRed}", "fund", function(data){
				//html.getRgInvestmentByProd( red.fundCode.val().replace(/#/g, '${specialChar}').toString(), red.fund.val(), function(data){
				html.getRgInvestmentByProdDomain( red.fundCode.val().replace(/#/g, '${specialChar}').toString(), "${domainBankRed}", red.fund.val(), function(data){
					if(data){
						red.fund.value( data.bankAccountNo );
						red.fundBankCode.value( data.bankCode.thirdPartyCode );
						red.fundBankName.value( data.name );
						red.fundCurrency.value( data.currency.currencyCode );
					} else {
						red.fund.value( "" );
						red.fundBankCode.value( "" );
						red.fundBankName.value( "NOT FOUND" );
						red.fundCurrency.value( "" );
					}
					
				});
				
			});
				
			/*red.investor.popupInvestorBank("?currencyCode="+red.h_currencyFund.val().replaceAll(" ", "+")+
					"&customerKey="+customerKey, "investor", function(data){
				red.investor.value( data.bankAccountNo );
				red.investorBankCode.value( data.bankCode.thirdPartyCode );
				red.investorBankName.value( data.name );
				red.investorCurrency.value( data.currency.currencyCode );
			}, function(){
				red.investorBankCode.value( "" );
				red.investorBankName.value( "NOT FOUND" );
				red.investorCurrency.value( "" );
			});*/
			
			red.investor.dynapopup('PICK_BN_ACCOUNT_BY_INVEST_ACC_ALL',customerKey+"|"+red.h_currencyFund.val().replaceAll(" ", "+"), "investor", function(data){
				html.getBankAccountByInvestmentProductCurrAccNo( red.h_currencyFund.val().replaceAll(" ", "+"), red.investor.val(), customerKey, function(data){
					if(data){
						red.investor.value( data.bankAccountNo );
						red.investorBankCode.value( data.bankCode.thirdPartyCode );
						red.investorBankName.value( data.name );
						red.investorCurrency.value( data.currency.currencyCode );
					} else {
						red.investorBankCode.value( "" );
						red.investorBankName.value( "NOT FOUND" );
						red.investorCurrency.value( "" );
					}
				});
			});
		}
		
		function getPrice(rgProduct){
			//if( digitPrice == "undefined" ||  typePrice == "typeUnit")
				getVarGlobal();
			var aNavDate = red.navDate.datepicker("getDate");
			var formattedNavDate = $.datepicker.formatDate('yymmdd', aNavDate);
			
			// set price with nav, only when fix nav is not set
			if( rgProduct.fixnav == false ){
				rgNav = html.getRgNav(formattedNavDate, red.fundCode.val());
				if (rgNav) {
					if (typeof rgNav.bidPct === "undefined") { 
						var bidPrice = rgNav.nav;
					}
					else {
						var bidPrice = (1+((rgNav.bidPct)/100))*rgNav.nav;
					}
					red.price.valueRnd(bidPrice, true, digitPrice, typePrice);
				}
			}else if( rgProduct.fixnav == true){
				red.price.valueRnd( rgProduct.fixNavPrice , true, digitPrice, typePrice);
			}
		};
/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		// === EVENT FOR CANCEL ==== //
		var result;
		
		cancelDateValidation();
		red.cancelDate.change(function(){
			if (!red.cancelDate.hasClass('fieldError')){
				result = cancelDateValidation();
				if (result) {
					red.cancelDateHidden.val(red.cancelDate.val());
				} else {
					red.cancelDateHidden.val('');
					$('#h_cancelDate').val('');
				}
			} else {
				red.cancelDateHidden.val('');
				$('#h_cancelDate').val('');
			}
			
		});
		
		cancelPostDateValidation();
		red.cancelPostDate.change(function(){
			if (!red.cancelPostDate.hasClass('fieldError')){
				result = cancelPostDateValidation();
				if (result) {
					red.cancelPostDateHidden.val(red.cancelPostDate.val());
				} else {
					red.cancelPostDateHidden.val('');
					$('#h_cancelPostDate').val('');
				}
			} else {
				red.cancelPostDateHidden.val('');
				$('#h_cancelPostDate').val('');
			}
			
			
		});
		
		red.remarkForCancel.change(function(){
			red.remarkForCancelHidden.val(red.remarkForCancel.val());
		});
		// ======================== //	
				
		
		html.clazz('calendar').datepicker();
		
		html.clazz('numericMaxLength').autoNumeric({vMax: '100'});
		html.clazz('numericMaxLength').live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		red.redemptionTab.tabs();
		red.redemptionTab.css('height', '450');		
		
		red.fundCode.blur(function(){
			if((red.fundCode.isChange()) || (red.fundCode.val() == "")){
				red.invtAcct.removeClass('fieldError');
				red.invtAcct.val("");
				red.invtAcctKey.val("");
				red.invtAcctDesc.value("");
				red.currencyFund.val("");
				red.navDate.val("");
				red.paymentDate.val("");
				red.postDate.val("");
				red.availUnit.val("");
				red.availBal.val("");
				red.netAmt.val("");
				red.taxPct.val("");
				red.price.val("");
				
				red.saCodeId.value("");
				red.saCode.value("");
				red.h_saCode.value("");
				red.saCodeDesc.value("");
				red.h_saCodeDesc.value("");
				
				red.trnSABranch.val("");
				
				// investor bank info
				red.investor.value("");
				red.investorKey.value("");
				red.investorBankCode.value("");
				red.investorBankName.value("");
				red.investorCurrency.value("");
				
				// fund bank info				
				red.fund.value("");
				red.fundKey.value("");
				red.fundBankCode.value("");
				red.fundBankName.value("");
				red.fundCurrency.value("");

			}
		});
		
		red.invtAcct.blur(function(){
			if(red.invtAcct.val() == ""){
				red.availUnit.val("");
				red.availBal.val("");
				red.netAmt.val("");
				red.saCodeId.value("");
				red.saCode.value("");
				red.h_saCode.value("");
				red.saCodeDesc.value("");
				red.h_saCodeDesc.value("");
				
				red.saCode.removeClass('fieldError');
				red.trnSABranch.val("");
			}
		});
		
		red.fundCode.popupProductByEffLiqDate("invtAcct", function(data){
			red.fundAccountName.html( "" );
			if (data) {
				rgProduct = data;
				red.currencyFund.value( rgProduct.currencyCode );
				
				if((red.fundCode.isChange()) || (red.fundCode.val() == "")){
					red.invtAcct.removeClass('fieldError');
					red.invtAcct.val("");
					red.invtAcctKey.val("");
					red.invtAcctDesc.value("");
					red.navDate.val("");
					red.postDate.val("");
					
					red.saCodeId.value("");
					red.saCode.value("");
					red.h_saCode.value("");
					red.saCodeDesc.value("");
					red.h_saCodeDesc.value("");
					
					red.trnSABranch.val("");
				}
				
				red.fundAccountName.html( rgProduct.description );
				
				var aTransactionDate = new Date(html.getApplicationDate());
				if(data.transactionDate == 'true' || data.transactionDate == true){
					var aTransactionDateYYYYMMDD = $.datepicker.formatDate( "yymmdd", aTransactionDate );
					var maxPaymentDateYYYMMDD = html.getWorkingDate(aTransactionDateYYYYMMDD, -1);
					var dateTrade = $.datepicker.parseDate('yymmdd', maxPaymentDateYYYMMDD, null);
					red.tradeDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', dateTrade));
				} else {
					red.tradeDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aTransactionDate));
				}
				
				red.goodFundDate.value(red.tradeDate.val());
				
				calcRgProduct();
				red.navDate.change();
				red.effectiveDate.val(data.effectiveDate);
				$("#feeby1").attr("checked", true); 
				rgFeeTiersOnlyFee(true);
				red.feePct.removeAttr("disabled");
				red.feeAmt.attr("disabled", true);
				
				// investor bank info
				red.investor.value("");
				red.investorKey.value("");
				red.investorBankCode.value("");
				red.investorBankName.value("");
				red.investorCurrency.value("");
				
				// fund bank info				
				red.fund.value("");
				red.fundKey.value("");
				red.fundBankCode.value("");
				red.fundBankName.value("");
				red.fundCurrency.value("");
				
				
				digitAmount = data.amountRoundValue;
				typeAmount = data.amountRoundType;
				digitUnit = data.unitRoundValue;
				typeUnit = data.unitRoundType;
				digitPrice = data.priceRoundValue;
				typePrice = data.priceRoundType;
				red.redLock.val(data.redLock);
				
				// bank info
				if(rgProduct.bankAccount){
					red.fund.value( rgProduct.bankAccount.accountNo );
					red.fundKey.value( rgProduct.bankAccount.bankAccountKey );
					red.fundBankCode.value(rgProduct.bankAccount.bankCode.thirdPartyCode);
					red.fundBankName.value(rgProduct.bankAccount.name);
					red.fundCurrency.value(rgProduct.bankAccount.currency.currencyCode);					
				}
				
				attachPopupInvestment();
				
				attachFundAccountLookup();
			}
		});
				
		red.tradeDate.change(function(){
			red.goodFundDate.value(red.tradeDate.val());
			calcRgProduct();
			
			var aTradeDate = red.tradeDate.datepicker("getDate");
			var formattedTradeDate = $.datepicker.formatDate('yymmdd', aTradeDate);
			
			/*html.getRgPortfolio(rgProduct.productCode, red.invtAcct.val(), formattedTradeDate, function(data){
				if (data) {
					rgPortfolio = data;
					calcRgPortfolio();
				}
			});*/
//			calcRgPortfolio();
			red.navDate.change();
			red.postDate.change();
		});
		
		red.postDate.change(function(){
			
			var aPostDate = red.postDate.datepicker("getDate");
			var formattedPostDate = $.datepicker.formatDate('yymmdd', aPostDate);
			
			html.getRgPortfolio(rgProduct.productCode, red.invtAcct.val(), formattedPostDate, function(data){
				if (data) {
					rgPortfolio = data;
					calcRgPortfolio();
				}
				else
				{
					red.availUnit.valueRnd(0, true, digitUnit, typeUnit);
					red.availBal.value(0, true);
				}
			});
			//red.navDate.change();
		});
		
		red.navDate.change(function(){
			if (!red.fundCode.isEmpty() && !red.navDate.isEmpty()) {
				if( !rgProduct.fixnav ){
					rgProduct.fixnav = false;
				}
				
				red.price.value("");
				getPrice( rgProduct );
				red.price.change();
			}
		});
		
		red.goodFundDate.change(function(){
			calcRgProduct();
		});
		
		red.fund.change(function(){
			if ($(this).val()==''){
				red.fundKey.val('');
				red.fundBankCode.val('');
				$('#h_fundBankCode').val('');
				red.fundBankName.val('');
				$('#h_fundBankName').val('');
				red.fundCurrency.val('');
				$('#h_fundCurrency').val('');
			}
		});
		
		red.investor.change(function(){
			if ($(this).val()==''){
				red.investorKey.val('');
				red.investorBankCode.val('');
				$('#h_investorBankCode').val('');
				red.investorBankName.val('');
				$('#h_investorBankName').val('');
				red.investorCurrency.val('');
				$('#h_investorCurrency').val('');
			}
			
		});
		
		red.tax.popupTax(null, function(data){
			rgTax = html.getRgTax(red.tax.val());
			calcRgTax();
			calcCapGainTaxAmt();
		});
		
		red.netAmt.change(function(){
			$('.error').html('');
			var value = red.netAmt.val();
//			var digit = red.amountRoundValue.val();
//			var roundType = red.amountRoundType.val();
			//console.log("value = " + value);
			//console.log("digitAmount = " + digitAmount + " typeAmount = " + typeAmount);
			red.netAmt.valueRnd(value.toNumber(","), true, digitAmount, typeAmount);
			if ((red.netAmt.val() == "")||(red.netAmt.val() == 0)) {
				if( $("#feeby2").attr("checked") == true ){
					red.feePct.val("");
				}
			} else {
				rgFeeTiers(true);
			}
//			calcUnit();
//			calcAmount();
			
			calcFeeAmt();
			calcDiscAmt();
			calcTaxAmt();
			calcOtherAmt();
			
			calcTotFeeAmt();
			calcAmount();
			calcUnit();
			calcCapGainTaxAmt();
			calcRgTax();
			
		});
		
		red.feeAmt.change(function(){
			var value = red.feeAmt.val();
			red.feeAmt.valueRnd(value.toNumber(","), true, digitAmount, typeAmount);
			red.feePct.value("");
			calcDiscAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcAmount();
			calcRgTax();
			if (value == "") {
				red.taxPct.val('');
				red.taxPctStripped.val('');
				red.taxAmt.val(0);
				red.taxAmtStripped.val(0);
				red.amount.val(0);
				red.amountStripped.val(0);
				red.capGainTaxAmount.value(0);
				red.payAmt.value(0);
				red.payAmtStripped.val(0);
			}
		});
		
		red.unit.change(function(){
			var value = red.unit.val();
			red.unit.valueRnd(value.toNumber(","), true, digitUnit, typeUnit);
			
			calcNetAmount();
			
			if ((red.netAmt.val() == "")||(red.netAmt.val() == 0)) {
				/*
				 * just leave as it is
				 * */
				//red.feePct.val("");
				if( $("#feeby1").attr("checked") == true ){
					red.feeAmt.val('');
					red.feeAmtStripped.val('');
				}
			} else {
				//rgFeeTiers(true);
				if( $("#feeby1").attr("checked") == true ){
					red.feeAmt.val('');
					red.feeAmtStripped.val('');
				}
			};
			
			calcFeeAmt();
			calcTotFeeAmt();
			calcAmount();
			calcCapGainTaxAmt();
			calcRgTax();
//			calcDiscAmt();
//			calcTaxAmt();
//			calcOtherAmt();
		});
		
		red.price.change(function(){
			var value = red.price.val();
//			var digit = red.priceRoundValue.val();
//			var roundType = red.priceRoundType.val();
//			red.price.value(value.toNumber(","), true, digitPrice, typePrice);
//			calcUnit();
//			calcNetAmount();
//			calcFeeAmt();
//			calcDiscAmt();
//			calcTaxAmt();
//			calcOtherAmt();
			
//			if (red.price.val() == 0) {
//				red.price.addClass('fieldError');
//				priceError.html("can not filled 0");
//			} else {
//				red.price.removeClass('fieldError');
//				priceError.html("");
//			}
			
			
			red.price.removeClass('fieldError');
			priceError.html("");
			if(value != "") {
				red.price.valueRnd(value.toNumber(","), true, digitPrice, typePrice);
				//calcUnit();
				calcNetAmount();
				if (red.price.val() == 0) {
					red.price.addClass('fieldError');
					priceError.html("can not filled 0");
				}
				if ((red.netAmt.val() == "")||(red.netAmt.val() == 0)) {
					if( $("#feeby2").attr("checked") == true ){
						red.feePct.value("");
					}
				} else {
					rgFeeTiers(true);
				};
				calcFeeAmt();
				calcDiscAmt();
				calcTaxAmt();
				calcOtherAmt();
				calcTotFeeAmt();
				calcCapGainTaxAmt();
				calcRgTax();
			} else {
				red.unit.valueRnd(0, true, digitPrice, typePrice);
				// value null
				red.netAmt.val('');
				red.netAmtStripped.val('');
				
				/*
				 * in case o empty price or amount, save and leave as it is fee pct/amount
				 * */
				//red.feePct.value('');
				//red.feeAmt.value(0);
				if( $("#feeby1").attr("checked") == true ){
					if ((red.netAmt.val() == "")||(red.netAmt.val() == 0)) {
						red.feeAmt.value('');
						red.feeAmtStripped.value('');
					} else {
						red.feeAmt.value(0);
						red.feeAmtStripped.value(0);
					}
					
				}
				
				red.totFeeAmt.value(0);
				red.amount.value("");
				red.capGainTaxAmount.val(0);
				red.payAmt.val(0);
				
				// value 0
//				red.netAmt.value(0, true, digitAmount, typeAmount);
			}
			
		});
		
		

		red.feePct.change(function(){
			red.feePctStripped.val(red.feePct.val());
			if (red.netAmt.val() != "") {
				if (red.netAmt.val() == 0) {
					if( $("#feeby1").attr("checked") == true ){
						red.feeAmt.val('');
						red.feeAmtStripped.val('');
					}
				}else {
					calcFeeAmt();
				}
			} else {
				if( $("#feeby1").attr("checked") == true ){
					red.feeAmt.val('');
					red.feeAmtStripped.val('');
				}
			}
			calcDiscAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcAmount();
//			calcUnit();
			
			calcRgTax();
			
			if (red.feePct.val() == "") {
				red.taxPct.value('');
				red.taxAmt.value(0);
				red.amount.value(0);
				red.capGainTaxAmount.value(0);
				//red.payAmt.value(0);
			}
		});
		
		red.discPct.change(function(){
			red.discPctStripped.val(red.discPct.val());
			calcDiscAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcAmount();
//			calcUnit();
			calcRgTax();
			if (red.discPct.val() == "") {
				red.totFeeAmt.val(0);
				red.totFeeAmtStripped.val(0);
				red.taxPct.val('');
				red.taxPctStripped.val('');
				red.taxAmt.val(0);
				red.taxAmtStripped.val(0);
				red.amount.val(0);
				red.amountStripped.val(0);
				red.capGainTaxAmount.value(0);
				red.payAmt.value(0);
				red.payAmtStripped.val(0);
			}
		});		
		
		
		red.otherPct.change(function(){
			red.otherPctStripped.val(red.otherPct.val());
			calcOtherAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcAmount();
//			calcUnit();
			calcRgTax();
			if (red.otherPct.val() == "") {
				red.totFeeAmt.val(0);
				red.totFeeAmtStripped.val(0);
				red.amount.val(0);
				red.amountStripped.val(0);
				red.payAmt.value(0);
				red.payAmtStripped.val(0);
			}
		});	
		
		red.discAmt.change(function(){
			red.discPct.value("");
			
			var value = red.discAmt.val();
			red.discAmt.valueRnd(value.toNumber(","), true, digitAmount, typeAmount);
			
			calcTaxAmt();
			calcTotFeeAmt();
			calcAmount();
			calcRgTax();
			if (value == "") {
				red.totFeeAmt.val(0);
				red.totFeeAmtStripped.val(0);
				red.taxPct.val('');
				red.taxPctStripped.val('');
				red.taxAmt.val(0);
				red.taxAmtStripped.val(0);
				red.amount.val(0);
				red.amountStripped.val(0);
				red.capGainTaxAmount.value(0);
				red.payAmt.value(0);
				red.payAmtStripped.val(0);
			}
		});
		
		red.otherAmt.change(function(){
			red.otherPct.value("");
			
			var value = red.otherAmt.val();
			red.otherAmt.valueRnd(value.toNumber(","), true, digitAmount, typeAmount);
			
			calcFeeAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcAmount();
			calcRgTax();
			if (value == "") {
				red.totFeeAmt.val(0);
				red.totFeeAmtStripped.val(0);
				red.amount.val(0);
				red.amountStripped.val(0);
				red.payAmt.value(0);
				red.payAmtStripped.val(0);
			}
		});
		
		red.tradeBy.change(function(){
			viewTradeBy();
			if (red.tradeBy.val() == TRANSACTION_BY_UNIT) {
				rgFeeTiersOnlyFee(true);
			}
			calcFeeAmt();
			calcDiscAmt();
			calcOtherAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcAmount();
		});
		
		red.liquidation.change(function(){
			var con = red.liquidation.checked();
			if (con == 'true') {
				red.tradeBy.value(TRANSACTION_BY_UNIT).change();
				red.tradeBy.disabled();
				red.tradeByHidden.val(red.tradeBy.val());
				red.unit.disabled();
				var availabelUnit = red.availUnit.val().toNumber(",");
				red.unit.valueRnd(availabelUnit, true, digitUnit, typeUnit);
				red.unitStripped.val(red.availUnitStripped.val());
				calcNetAmount();
			}
			if (con == 'false') {
				red.tradeBy.enabled();
				red.unit.enabled();
				$("#redemptionTab #tab-1 p[id=pTradeBy] label span").html(" *");
				$("#redemptionTab #tab-1 #BYUNIT p[id=pUnit] label span").html(" *");
			}
		});
		
		function getVarGlobal(){
			if (!red.fundCode.val().isEmpty()) {
				var productCode = red.fundCode.val();
				productCode = productCode.replace(/#/g, '${specialChar}').toString();
				rgProduct = html.getRgProduct(productCode);
				digitAmount = rgProduct.amountRoundValue;
				typeAmount = rgProduct.amountRoundType;
				digitUnit = rgProduct.unitRoundValue;
				typeUnit = rgProduct.unitRoundType;
				digitPrice = rgProduct.priceRoundValue;
				typePrice = rgProduct.priceRoundType;
				
				/**
				 * disabled this to avoid recounting the amount when price/unit is 0
				 */
				//red.navDate.change(); //x
			}
		}
		
		if (!red.fundCode.val().isEmpty()) {
			var productCode = red.fundCode.val();
			productCode = productCode.replace(/#/g, '${specialChar}').toString();
			rgProduct = html.getRgProduct(productCode);
			digitAmount = rgProduct.amountRoundValue;
			typeAmount = rgProduct.amountRoundType;
			digitUnit = rgProduct.unitRoundValue;
			typeUnit = rgProduct.unitRoundType;
			digitPrice = rgProduct.priceRoundValue;
			typePrice = rgProduct.priceRoundType;
			red.effectiveDate.val(rgProduct.effectiveDate);
			red.redLock.val(rgProduct.redLock);
			
			/**
			 * disabled this to avoid recounting the amount when price/unit is 0
			 */
			//red.navDate.change(); //x
		}
		
		
		var dataType =0;
		if(red.saCode.val() != ""){
			dataType = red.saCodeId.val();
		}
		//attachPopupTrnSABranch(dataType);

		viewTradeBy();
		calcTaxAmt();
		//calcFeeAmt(); //x
		//calcTotFeeAmt(); //x
		calcRgPortfolio();
		//calcRgTax(); //x
		//calcCapGainTaxAmt(); //x

		var fundCode =" ";
		if(red.fundCode.val() != ""){
			fundCode = red.fundCode.val();
		}
		attachPopupInvestment();
		attachFundAccountLookup();
		
		var accountNo =" ";
		if(red.invtAcct.val() != ""){
			accountNo = red.invtAcct.val();
		}
		
		if (red.dispatch.val() == "confirm") {
			alert("Data Redemption save with id "+red.tradeKey.val());
		}
		if (red.dispatch.val() == "confirm") {
			location.href='@{entry()}';
		}
		$("#feeby1").change(function(){
			checkFeeBy();
		});
		$("#feeby2").change(function(){
			checkFeeBy();
		});

		// check if invtAcct is already there
		// then query to get customer
		if( jQuery.trim( red.invtAcct.val() ) !== "" ){
			html.getRgInvestmentAcct(red.fundCode.val().replace(/#/g, '${specialChar}').toString(), red.invtAcct.val(), function(data){
				if (data) {
					rgInvestment = data;
					attachFundAccountLookup();
				}
			});			
		}
		if( rgProduct ){
			getPrice(rgProduct);	
		}

		// init event
		if (red.tradeBy.val() == TRANSACTION_BY_AMOUNT) {
			calcUnit();
			calcTotFeeAmt();
			calcRgTax();
		}else{
			red.unit.change();
			red.feePct.change();		
			calcNetAmount();
		}
		
		// kenapa false karena fee amount, klo true maka fee pct
		if ((red.dispatch.val()!= 'entry') && ( $("input[name='red.feePercent']").val() == 'false')) {
			if(red.from.val() != 'unitRegistry')
				red.feePct.calcRedFeePct(red.netAmt.val().toNumber(","), red.feeAmt.val().toNumber(","));
		}
		
		if(isApproval){
			var availableUnitA = '${red.availabelUnit}';
			var tmpUnitA = (availableUnitA ? Number(availableUnitA).toFixed(digitUnit) : 0);
			if(availableUnitA != ''){
				red.availUnit.valueRnd((tmpUnitA), true, digitUnit, typeUnit);
			}
		}
	}else{
		return new Redemption(html);
	}
}
	