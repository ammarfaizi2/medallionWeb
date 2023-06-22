function Subscription(html) {
	if (this instanceof Subscription) {
/* =========================================================================== 
 * Constant
* ========================================================================= */
		var INPUT_BY_GROSS = "INPUT_BY_GROSS";
		var INPUT_BY_NET = "INPUT_BY_NET";
		TIER_COMPARISON_TYPE_FLAT = "TIER_COMPARISON_TYPE-FLAT";
		var SUBSCRIBE = "SUBSCRIBE";
//		var formatDDMMYYYY = 'dd/MM/yyyy';
//		var formatMMDDYYYY = 'MM/dd/yyyy';
	
		var priceError = $("#priceErrorMessage");
	
		var digitAmount;
		var typeAmount;
		var digitUnit;
		var typeUnit;
		var digitPrice;
		var typePrice;

/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var sub = html.inject(this);

		if ($.browser.msie){
			$("#remarksCorrection[maxlength]").bind('input propertychange', function() {
		        var maxLength = $(this).attr('maxlength');  
		        if ($(this).val().length > maxLength) {  
		            $(this).val($(this).val().substring(0, maxLength));  
		        }  
		    });
		}
/* =========================================================================== 
 * Variable
* ========================================================================= */
//		var rgProduct = (sub.fundCode.isEmpty()) ? null: html.getRgProduct(sub.fundCode.val());
		var rgInvestment = (sub.invtAcct.isEmpty() || sub.fundCode.isEmpty()) ? null: html.getRgInvestmentAcct(sub.fundCode.val(), sub.invtAcct.val());
		var rgNav = (sub.navDate.isEmpty() || sub.fundCode.isEmpty()) ? null : html.getRgNav(sub.navDate.val().fmtYYYYMMDD("/"), sub.fundCode.val());
		//var gnThdPrty = (sub.invtAcct.isEmpty() || sub.fundCode.isEmpty() || sub.trnSABranchKey.isEmpty()) ? null : html.getGnThdPrty(sub.trnSABranchKey.val());
/* =========================================================================== 
 * Ajax Method
 * ========================================================================= */
		function rgFeeTiers(isImplement) {
			console.log('rgFeeTiers');
		
			var amount = getInputByAmount();
			var tierType = rgProduct.lookupBySubTierType.lookupId;
			var productCode = rgProduct.productCode;
			var type = SUBSCRIBE;
			var inputBy = sub.inputBy.val();
			
			if (amount != 0) {
				html.setFeeTier(tierType, productCode, amount, sub.feePct, sub.feeAmt, type, inputBy,digitAmount,typeAmount);
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
		
		function getInputByAmount() {
			var amount;
			if (sub.inputBy.val() == INPUT_BY_GROSS) amount = sub.amount.val().toNumber(",");
			if (sub.inputBy.val() == INPUT_BY_NET) amount = sub.netAmt.val().toNumber(",");
/*
			if (sub.inputBy.val() == INPUT_BY_GROSS) {
				if (sub.amount.val()!="") {
					amount = sub.amount.val().toNumber(",");
				} else {
					amount = "";
				}
				
			}
			if (sub.inputBy.val() == INPUT_BY_NET) {
				if (sub.amount.val()!="") {
					amount = sub.netAmt.val().toNumber(",");
					alert(sub.netAmtStripped.val());
				} else {
					amount = "";
				}
			}
*/			
			return amount;
		}
		
		function getInputByAmountTag() {
			var amountTag;
			if (sub.inputBy.val() == INPUT_BY_GROSS) amountTag = sub.amount;
			if (sub.inputBy.val() == INPUT_BY_NET) amountTag = sub.netAmt;
			
			return amountTag;
		}
		
		function calcRgProduct() {
			console.log('calcRgProduct');
			var goodFundDate = sub.goodFundDate.val().fmtYYYYMMDD("/");
			console.log('calcRgProduct; goodFundDate ' + goodFundDate);
			
			var newGoodFundDate = html.addCutOfTime(goodFundDate, rgProduct.subCot) + "";
			if (goodFundDate != newGoodFundDate) {
				goodFundDate = newGoodFundDate;
				sub.goodFundDate.value(goodFundDate.toMMDDYYYY("/"));
				/*if (sub.formatDate.val() == formatMMDDYYYY)
					sub.goodFundDate.value(goodFundDate.toMMDDYYYY("/"));*/
			}
			
			var navDate = html.getWorkingDate(goodFundDate, rgProduct.subNavUsed) + "";
			var postDate = html.getWorkingDate(goodFundDate, rgProduct.subPostPeriod) + "";
//			if (sub.formatDate.val() == formatDDMMYYYY) {
				sub.navDate.value(navDate.toMMDDYYYY("/"));
				sub.postDate.value(postDate.toMMDDYYYY("/"));
				sub.defaultPostDate.value(sub.postDate.val());
//			}
			
			/*if (sub.formatDate.val() == formatMMDDYYYY) {
				sub.navDate.value(navDate.toMMDDYYYY("/"));
				sub.postDate.value(postDate.toMMDDYYYY("/"));
			}*/
			//sub.taxPct.value(rgProduct.taxMasterBySubTaxKey.taxRate, true);
		}
		
		function calcFeeAmt(feeAmt) {
			console.log('calcFeeAmt '+feeAmt);
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit"){
				getVarGlobal();
			}
			var subTierType = rgProduct.lookupBySubTierType.lookupId;
			var amountTag = getInputByAmountTag();
			if (feeAmt) {
				sub.feeAmt.valueRnd(feeAmt, true, digitAmount, typeAmount);
			}else{
				// checking flat or progressive
				if (subTierType == TIER_COMPARISON_TYPE_FLAT) {
					sub.feeAmt.calcSubFeeAmount(amountTag, sub.feePct, sub.inputBy, digitAmount, typeAmount);
				} else {
					if (sub.feePct.val() != '') {
						sub.feeAmt.calcSubFeeAmount(amountTag, sub.feePct, sub.inputBy, digitAmount, typeAmount);
					}
				}
			}
			
		};

		function calcDiscAmt() {
			console.log('calcDiscAmt');
			//sub.discAmt.calcSubDiscAmount(sub.feeAmt, sub.discPct, digitAmount, typeAmount);
		}
		
		function calcOtherAmt() {
			console.log('calcOtherAmt');	
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit"){
				getVarGlobal();
			}
			var amountTag = getInputByAmountTag();
			sub.otherAmt.calcSubOtherAmount(amountTag, sub.otherPct, sub.inputBy, digitAmount, typeAmount);
		}
		
		function calcTaxAmt() {
			console.log('calcTaxAmt');
			console.log('rgProduct'+rgProduct);
			//sub.taxAmt.calcSubTaxFeeAmount(sub.taxPct, sub.feeAmt, sub.discAmt, sub.otherAmt, digitAmount, typeAmount);
		};
		
		function calcTotFeeAmt() {
			if( digitAmount == "undefined" ||  typeAmount == "typeUnit"){
				getVarGlobal();
			}
			console.log('calcTotFeeAmt');
			sub.totFeeAmt.calcSubTotalFeeAmount(sub.feeAmt, sub.discAmt, sub.otherAmt, sub.taxAmt, digitAmount, typeAmount);
		}
		
		function calcNetAmount() {
			console.log('calcNetAmount');	
			console.log("-----digitAmount:"+digitAmount);
			console.log("-----typeAmount:"+typeAmount);
			if( digitAmount == "undefined" ||  typeAmount == "undefined"){
				getVarGlobal();
			}
			var amountTag = getInputByAmountTag();
			if (sub.inputBy.val() == INPUT_BY_GROSS) sub.netAmt.calcSubNetAmount(amountTag, sub.totFeeAmt, sub.inputBy, digitAmount, typeAmount);
			if (sub.inputBy.val() == INPUT_BY_NET)   sub.amount.calcSubAmount(amountTag, sub.totFeeAmt, sub.inputBy, digitAmount, typeAmount);
			
		}
		function getVarGlobal(){
			if (!sub.fundCode.val().isEmpty() ) {
				productCode = sub.fundCode.val();
				productCode = productCode.replace(/#/g, '${specialChar}').toString();
				rgProduct = html.getRgProduct(productCode);
				digitAmount = rgProduct.amountRoundValue;
				typeAmount = rgProduct.amountRoundType;
				digitUnit = rgProduct.unitRoundValue;
				typeUnit = rgProduct.unitRoundType;
				digitPrice = rgProduct.priceRoundValue;
				typePrice = rgProduct.priceRoundType;
				//attachPopupTrnSABranch(sub.saCodeId.val());
			}
		}
		function calcUnit() {
			console.log('calcUnit');
			console.log("******digitUnit:"+digitUnit);
			console.log("******typeUnit:"+typeUnit);
			if( digitUnit == "undefined" ||  typeAmount == "typeUnit"){
				getVarGlobal();
			}
			sub.unit.calcSubUnit(sub.netAmt, sub.price, null, digitUnit, typeUnit);
		}
		
		function viewInputBy() {
			var disabled = sub.inputBy.attr("disabled");
			
			if (sub.inputBy.val() == INPUT_BY_GROSS) {
				sub.BYGROSS.append(sub.pAmount.load());
				sub.BYNET.append(sub.pNetAmount.load());
				
				sub.amount.removeAttr("disabled");
				sub.netAmt.attr("disabled", "disabled");
				$("#subscribeTab #tab-1 p[id=pAmount] label span").html("*");
				$("#subscribeTab #tab-1 p[id=pNetAmount] label span").html("");
				if (disabled) sub.amount.attr("disabled", "disabled");
			}
			
			if (sub.inputBy.val() == INPUT_BY_NET) {
				sub.BYGROSS.append(sub.pNetAmount.load());
				sub.BYNET.append(sub.pAmount.load());
				$("#subscribeTab #tab-1 p[id=pNetAmount] label span").html("*");
				$("#subscribeTab #tab-1 p[id=pAmount] label span").html("");
				sub.netAmt.removeAttr("disabled");
				sub.amount.attr("disabled", "disabled");
				
				if (disabled) sub.netAmt.attr("disabled", "disabled");
			}
		};
		
		sub.invtAcct.dynapopup('PICK_RG_INVEST_BY_PROD', 'null', "tradeDate");
		
		function attachPopupInvestment(data) {
			if (rgProduct) {
				var productCode = data;
				productCode = productCode.replace(/#/g, '${specialChar}').toString();
				console.log("productCode="+productCode)
//				sub.invtAcct.popupInvestmentAcct("?type=" + productCode, "tradeDate", function(productCode){
				sub.invtAcct.dynapopup('PICK_RG_INVEST_BY_PROD', productCode, "tradeDate", function(data) {
					html.getRgInvestmentAcct(productCode, sub.invtAcct.val(), function(data){
						if (data) {
							rgInvestment = data;
							$('#invtAcctBankAcctCurrency').val(rgInvestment.bankAccount.currency.currencyCode);
							$('td[id=tdInvestor] label b').html(data.description);
							//sub.investBankCurrency.val(data.rgProduct.currency.currencyCode);
							if (data.firstTxn == true) {
								$("input[name='subscriptionType']")[0].checked = true;
								$('#subscriptionTypeHidden').val($('#subscriptionType1').val());
							} else if (data.firstTxn == false) {
								$("input[name='subscriptionType']")[1].checked = true;
								$('#subscriptionTypeHidden').val($('#subscriptionType2').val());
							}
							sub.saCode.removeClass('fieldError');
							sub.saCodeId.value(rgInvestment.thirdPartyBySaCode.thirdPartyKey);
							sub.saCode.value(rgInvestment.thirdPartyBySaCode.thirdPartyCode);
							sub.h_saCode.value(rgInvestment.thirdPartyBySaCode.thirdPartyCode);
							sub.saCodeDesc.value(rgInvestment.thirdPartyBySaCode.thirdPartyName);
							sub.h_saCodeDesc.value(rgInvestment.thirdPartyBySaCode.thirdPartyName);
							//sub.accNo.value(rgInvestment.acountNumber);
							/*sub.trnSABranchKey.value(rgInvestment.thirdPartyByTrnSABranch.trnSABranchKey);
							gnThdPrty = html.getGnThdPrty(sub.trnSABranchKey.val());
							if(gnThdPrty){
								sub.trnSABranchCode.value(gnThdPrty.name);
								sub.trnSABranch.value(gnThdPrty.code);
								sub.trnSABranchDesc.value(gnThdPrty.description);
							}*/
							
							//attachPopupTrnSABranch(rgInvestment.thirdPartyBySaCode.thirdPartyKey);
							
							//TO DO: change to bank account for investment account
							/*if (rgInvestment.bankAccount) {
								sub.invest.value(rgInvestment.bankAccount.accountNo);
								sub.investKey.value(rgInvestment.bankAccount.bankAccountKey);
								//sub.investDesc.value(rgInvestment.bankAccount.name);
								
								sub.investBank.value(rgInvestment.bankAccount.bankCode.thirdPartyCode);
								sub.investBankName.value(rgInvestment.bankAccount.bankCode.thirdPartyName);
							}*/
							
							var vprvFundCode = $('#fundCode').val();
							//vprvFundCode = vprvFundCode.replace(" ","+");
							attachPopupInvestmentNull();
							attachPopupBankAccount(vprvFundCode, sub.invtAcct.val().replace(" ","+"));
						}
					}, function(data){
						sub.saCodeId.value("");
						sub.saCode.value("");
						sub.h_saCode.value("");
						sub.saCodeDesc.value("");
						sub.h_saCodeDesc.value("");
						
						sub.saCode.removeClass('fieldError');
						
						attachPopupInvestmentNull();
						attachPopupBankAccount("");
					});
				});
			} else {
//				sub.invtAcct.popupInvestmentAcct("?type="+data, "tradeDate", function(data){
				sub.invtAcct.dynapopup('PICK_RG_INVEST_BY_PROD', data, "tradeDate", function(data) {
				});
			}
		}
		
		function attachPopupBankAccount(productCode, accountNumber) {
			if (rgInvestment) {
				//TO DO: change to bank account for investment account
				/*sub.invest.popupBankAccountInvt("?accountnumber="+data, null, function(data){
					rgInvestment.bankAccount = data;
					
					sub.investBank.value(rgInvestment.bankAccount.bankCode.thirdPartyCode);
					sub.investBankName.value(rgInvestment.bankAccount.bankCode.thirdPartyName);
				});*/
				var customerKey = 0;
				if( rgInvestment.customer ){
					customerKey = rgInvestment.customer.customerKey;
				}
				$('#investAccountNo').lookup({
					list:'@{Pick.bankAccountsByAccountNumberCurrency()}?currencyCode=' + $('#currencyHidden').val() + '&customerKey=' + customerKey,
					get:{
						url:'@{Pick.bankAccountByInvestmentProductCurrAccNo()}?currencyCode=' + $('#currencyHidden').val() + '&customerKey=' + customerKey,
						success: function(data){
							rgInvestment.bankAccount = data;
							$('#investAccountNo').removeClass('fieldError');
							$('#investAccountNo').val(data.bankAccountNo);
							$('#investAccountKey').val(data.bankAccountKey);
							$('#investBankAccount').val(rgInvestment.bankAccount.bankCode.thirdPartyCode);
							$('#investBankAccountKey').val(rgInvestment.bankAccount.bankCode.thirdPartyKey);
							$('#investBankAccountName').val(rgInvestment.bankAccount.bankCode.thirdPartyName);
							$('#investBeneficiary').val(data.name);
							$('#investBankCurrency').val(data.currency.currencyCode);
						},
						error: function(data){
							$('#investAccountNo').addClass('fieldError');
							$('#investAccountNo').val('');
							$('#investAccountKey').val('');
							$('#investBankAccount').val('');
							$('#investBankAccountKey').val('');
							$('#investBankAccountName').val('');
							$('#investBeneficiary').val('NOT FOUND');
							$('#investBankCurrency').val('');
						}
					},
					key : $('#investAccountKey'),
					description:$('#investBankAccountName'),
					help:$('#investAccountHelp'),
					filter: {'productCode':$('#fundCode'), 'accountNumber':$('#invtAcct')}
				});
			} else {
				$('#investAccountNo').lookup({
					list:'@{Pick.bankAccountsByAccountNumberCurrency()}?currencyCode=' + '' + '&customerKey=' + '-99',
					get:{
						url:'@{Pick.bankAccountByInvestmentProductCurrAccNo()}?currencyCode=' + '' + '&customerKey=' + '-99',
						error: function(data){
							$('#investAccountNo').addClass('fieldError');
							$('#investAccountNo').val('');
							$('#investAccountKey').val('');
							$('#investBankAccount').val('');
							$('#investBankAccountKey').val('');
							$('#investBankAccountName').val('');
							$('#investBeneficiary').val('NOT FOUND');
							$('#investBankCurrency').val('');
						}
					},
					key : $('#investAccountKey'),
					description:$('#investBankAccountName'),
					help:$('#investAccountHelp'),
					filter: {'productCode':$('#fundCode'), 'accountNumber':$('#invtAcct')}
				});
			}
		}
		
		function attachPopupBankAccountProd(productCode) {
			//TO DO: change to bank account for fund account

			productCode = productCode.replace(/#/g, '${specialChar}').toString();
			
			if (rgProduct) {
				/*sub.fund.popupBankAccountProd("?productCode="+data, null, function(data){
					rgProduct.bankAccount = data;
					
					sub.fundBank.value(rgProduct.bankAccount.bankCode.thirdPartyCode);
					sub.fundBankName.value(rgProduct.bankAccount.bankCode.thirdPartyName);
				});*/
				sub.fundAccount.dynapopup('PICK_BN_ACCOUNT_BY_RG_PRODUCT_BN_ACCOUNT_DOMAIN',productCode+"|${domainBankSub}", "investAccountNo", function(data){
					//html.getRgInvestmentByProd( sub.fundCode.val().replace(/#/g, '${specialChar}').toString(), sub.fundAccount.val(), function(data){
					html.getRgInvestmentByProdDomain( sub.fundCode.val().replace(/#/g, '${specialChar}').toString(), "${domainBankSub}", sub.fundAccount.val(), function(data){
						if(data){
							rgProduct.bankAccount = data;
							$('#fundAccount').removeClass('fieldError');
							$('#fundAccount').val(data.bankAccountNo);
							$('#fundAccountKey').val(data.key);
							$('#fundBankAccount').val(rgProduct.bankAccount.bankCode.thirdPartyCode);
							$('#fundBankAccountKey').val(rgProduct.bankAccount.bankCode.thirdPartyKey);
							$('#fundBankAccountName').val(rgProduct.bankAccount.bankCode.thirdPartyName);
							$('#fundBeneficiary').val(data.name);
							$('#fundCurrency').val(data.currency.currencyCode);
						} else {
							$('#fundAccount').addClass('fieldError');
							$('#fundAccount').val('');
							$('#fundAccountKey').val('');
							$('#fundBankAccount').val('');
							$('#fundBankAccountKey').val('');
							$('#fundBankAccountName').val('');
							$('#fundBeneficiary').val('NOT FOUND');
							$('#fundCurrency').val('');
						}
						
					});
					
				});
				
				/*$('#fundAccount').lookup({
					list:'@{Pick.bankAccountsByRgProductBnAccounts()}?productCode=' + productCode,
					get:{
						url:'@{Pick.bankAccountsByRgProductBnAccount()}?productCode=' + productCode,
						success: function(data){
							rgProduct.bankAccount = data;
							$('#fundAccount').removeClass('fieldError');
							$('#fundAccount').val(data.bankAccountNo);
							$('#fundAccountKey').val(data.key);
							$('#fundBankAccount').val(rgProduct.bankAccount.bankCode.thirdPartyCode);
							$('#fundBankAccountKey').val(rgProduct.bankAccount.bankCode.thirdPartyKey);
							$('#fundBankAccountName').val(rgProduct.bankAccount.bankCode.thirdPartyName);
							$('#fundBeneficiary').val(data.name);
							$('#fundCurrency').val(data.currency.currencyCode);
						},
						error: function(data){
							$('#fundAccount').addClass('fieldError');
							$('#fundAccount').val('');
							$('#fundAccountKey').val('');
							$('#fundBankAccount').val('');
							$('#fundBankAccountKey').val('');
							$('#fundBankAccountName').val('');
							$('#fundBeneficiary').val('NOT FOUND');
							$('#fundCurrency').val('');
						}
					},
					key : $('#fundAccountKey'),
					description:$('#fundBankAccountName'),
					help:$('#fundAccountHelp'),
					filter: {'productCode':$('#fundCode')},
					nextControl:$('#investAccountNo')
				});*/
			} else {
				sub.fundAccount.dynapopup('PICK_BN_ACCOUNT_BY_RG_PRODUCT_BN_ACCOUNT','', "investAccountNo", function(data){
					html.getRgInvestmentByProd( sub.fundCode.val().replace(/#/g, '${specialChar}').toString(), sub.fundAccount.val(), function(data){
						if(data){
							rgProduct.bankAccount = data;
							$('#fundAccount').removeClass('fieldError');
							$('#fundAccount').val(data.bankAccountNo);
							$('#fundAccountKey').val(data.key);
							$('#fundBankAccount').val(rgProduct.bankAccount.bankCode.thirdPartyCode);
							$('#fundBankAccountKey').val(rgProduct.bankAccount.bankCode.thirdPartyKey);
							$('#fundBankAccountName').val(rgProduct.bankAccount.bankCode.thirdPartyName);
							$('#fundBeneficiary').val(data.name);
							$('#fundCurrency').val(data.currency.currencyCode);
						} else {
							$('#fundAccount').addClass('fieldError');
							$('#fundAccount').val('');
							$('#fundAccountKey').val('');
							$('#fundBankAccount').val('');
							$('#fundBankAccountKey').val('');
							$('#fundBankAccountName').val('');
							$('#fundBeneficiary').val('NOT FOUND');
							$('#fundCurrency').val('');
						}
						
					});
					
				});
				
				/*$('#fundAccount').lookup({
					list:'@{Pick.bankAccountsByRgProductBnAccounts()}?productCode=' + '',
					get:{
						url:'@{Pick.bankAccountsByRgProductBnAccount()}?productCode=' + '',
						
						error: function(data){
							$('#fundAccount').addClass('fieldError');
							$('#fundAccount').val('');
							$('#fundAccountKey').val('');
							$('#fundBankAccount').val('');
							$('#fundBankAccountKey').val('');
							$('#fundBankAccountName').val('');
							$('#fundBeneficiary').val('NOT FOUND');
							$('#fundCurrency').val('');
						}
					},
					key : $('#fundAccountKey'),
					description:$('#fundBankAccountName'),
					help:$('#fundAccountHelp'),
					filter: {'productCode':$('#fundCode')},
					nextControl:$('#investAccountNo')
				});*/
			}
		}
		
		$('#fundAccount').change(function(){
			if ($('#fundAccount').val() == '') {
				attachPopupProductNull();
			}
		});
		
		function attachPopupTrnSABranch(data) {
			/*sub.trnSABranchCode.lookup({
				list:'@{Pick.thirdPartiesWithChild()}?key='+data,
				get:{
					url:'@{Pick.thirdParty()}?type=THIRD_PARTY-SELLING_AGENT',
					success: function(data){
							sub.trnSABranchCode.removeClass('fieldError');
							sub.trnSABranch.val(data.code);
							sub.trnSABranchDesc.val(data.description);
							sub.h_trnSABranchDesc.val(data.description);
							sub.trnSABranchKey.val(data.code);
						
					},
					error: function(data){
						sub.trnSABranchCode.addClass('fieldError');
						sub.trnSABranchCode.val('');
						sub.trnSABranch.val('');
						sub.trnSABranchDesc.val('NOT FOUND');
						sub.h_trnSABranchDesc.val('');
						sub.trnSABranchKey.val('');
						
					}
				},
				description:sub.trnSABranchDesc,
				help:sub.trnSABranchHelp
			});*/
		}
		
		function attachPopupInvestmentNull() {
			//TO DO: change to bank account for investment account
			//sub.invest.value("");
			//sub.investKey.value("");
			//sub.investDesc.value("");
			//sub.h_investDesc.value("");
			
			//sub.accNo.value("");
			//sub.h_accNo.value("");
			
			//sub.investBank.value("");
			//sub.investBankName.value("");
			
			$('#investAccountNo').val('');
			$('#investAccountKey').val('');
			$('#investBankAccount').val('');
			$('#investBankAccountKey').val('');
			$('#investBankAccountName').val('');
			$('#investBeneficiary').val('');
			$('#investBankCurrency').val('');
		}
		
		$('#investAccountNo').change(function(){
			if ($('#investAccountNo').val() == '') {
				attachPopupInvestmentNull();
			}
		});

		function attachPopupProductNull() {
			//TO DO: change to bank account for fund account
			//sub.fund.value("");
			//sub.fundKey.value("");
			//sub.investDesc.value("");
			//sub.h_investDesc.value("");
			
			//sub.fundCd.value("");
			//sub.h_fundCd.value("");
			
			//sub.fundBank.value("");
			//sub.fundBankName.value("");
			
			$('#fundAccount').val('');
			$('#fundAccountKey').val('');
			$('#fundBankAccount').val('');
			$('#fundBankAccountKey').val('');
			$('#fundBankAccountName').val('');
			$('#fundBeneficiary').val('');
			$('#fundCurrency').val('');
		}
		
		function cancelDateValidation(){
			var cancelPostDate = new Date(sub.cancelPostDate.datepicker('getDate')).getTime();
			var cancelDate = new Date(sub.cancelDate.datepicker('getDate')).getTime();
			var currentAppDate = new Date(sub.currentBusinessDate.datepicker('getDate')).getTime();
			var goodFundDate = new Date(sub.goodFundDate.datepicker('getDate')).getTime();
			
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
			var cancelPostDate = new Date(sub.cancelPostDate.datepicker('getDate')).getTime();
			var cancelDate = new Date(sub.cancelDate.datepicker('getDate')).getTime();
			var postDate = new Date(sub.postDate.datepicker('getDate')).getTime();
			
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
		sub.cancelDate.change(function(){
			if (!sub.cancelDate.hasClass('fieldError')){
				result = cancelDateValidation();
				if (result) {
					sub.cancelDateHidden.val(sub.cancelDate.val());
				} else {
					sub.cancelDateHidden.val('');
					$('#h_cancelDate').val('');
				}
			} else {
				sub.cancelDateHidden.val('');
				$('#h_cancelDate').val('');
			}
			
		});
		
		cancelPostDateValidation();
		sub.cancelPostDate.change(function(){
			if (!sub.cancelPostDate.hasClass('fieldError')){
				result = cancelPostDateValidation();
				if (result) {
					sub.cancelPostDateHidden.val(sub.cancelPostDate.val());
				} else {
					sub.cancelPostDateHidden.val('');
					$('#h_cancelPostDate').val('');
				}
			} else {
				sub.cancelPostDateHidden.val('');
				$('#h_cancelPostDate').val('');
			}
			
		});
		
		sub.remarkForCancel.change(function(){
			sub.remarkForCancelHidden.val(sub.remarkForCancel.val());
		});
		// ======================== //		
		
		if (!sub.fundCode.val().isEmpty()) {
			var productCode = sub.fundCode.val();
			productCode = productCode.replace(/#/g, '${specialChar}').toString();
			var rgProduct = html.getRgProduct(productCode);
			digitAmount = rgProduct.amountRoundValue;
			typeAmount = rgProduct.amountRoundType;
			digitUnit = rgProduct.unitRoundValue;
			typeUnit = rgProduct.unitRoundType;
			digitPrice = rgProduct.priceRoundValue;
			typePrice = rgProduct.priceRoundType;
			sub.effectiveDate.val(rgProduct.effectiveDate);
			sub.liquidDate.val(rgProduct.liquidDate);
			sub.subLock.val(rgProduct.subLock);
			//attachPopupTrnSABranch(sub.saCodeId.val());
		}
		
		html.clazz('calendar').datepicker();
		
		sub.subscribeTab.tabs();
		sub.subscribeTab.css('height', '350');
		
//		sub.fundCode.popupProduct("invtAcct", function(data){
//			if (data) {
//				rgProduct = data;
//				sub.invtAcct.value("");
//				sub.invtAcctKey.value("");
//				sub.invtAcctDesc.value("");
//			
//				calcRgProduct();
//				sub.navDate.change();
//				
//				attachPopupInvestment();
//			}
//		});
		
		
		sub.fundCode.blur(function(){
			if((sub.fundCode.isChange()) || (sub.fundCode.val() == "")){
				sub.invtAcct.removeClass('fieldError');
				sub.invtAcct.val("");
				sub.invtAcctKey.val("");
				sub.invtAcctDesc.val("");
				sub.h_invtAcctDesc.val("");
				sub.navDate.val("");
				sub.postDate.val("");

				sub.currency.val("");
				sub.currencyHidden.val("");
				
				sub.saCodeId.value("");
				sub.saCode.value("");
				sub.h_saCode.value("");
				sub.saCodeDesc.value("");
				sub.h_saCodeDesc.value("");
				
				/*sub.trnSABranchCode.removeClass('fieldError');
				sub.trnSABranchCode.val("");
				sub.trnSABranch.val("");
				sub.trnSABranchDesc.val("");
				sub.h_trnSABranchDesc.val("");
				sub.trnSABranchKey.val("");*/
				attachPopupInvestment("");
				//attachPopupTrnSABranch(0);
				attachPopupInvestmentNull();
				attachPopupBankAccountProd("");
				attachPopupProductNull();
				attachPopupBankAccount("");
			}
		});
		
		sub.invtAcct.blur(function(){
			if( sub.invtAcct.val() == "" ){
				sub.saCodeId.value("");
				sub.saCode.value("");
				sub.h_saCode.value("");
				sub.saCodeDesc.value("");
				sub.h_saCodeDesc.value("");
				
				sub.saCode.removeClass('fieldError');
				/*sub.trnSABranchCode.val("");
				sub.trnSABranch.val("");
				sub.trnSABranchDesc.val("");
				sub.h_trnSABranchDesc.val("");
				sub.trnSABranchKey.val("");*/
				//attachPopupTrnSABranch(0);
				attachPopupInvestmentNull();
				attachPopupBankAccount("");
				
			}
		});
		
		sub.fundCode.popupProductByEffLiqDate("invtAcct", function(data){
			if (data) {
				rgProduct = data;
				sub.invtAcct.value("");
				sub.invtAcctKey.value("");
				sub.invtAcctDesc.value("");
				sub.currency.val(data.currencyCode);
				sub.currencyHidden.val(data.currencyCode);
				$('td[id=tdProduct] label b').html(data.description);
				
				if((sub.fundCode.isChange()) || (sub.fundCode.val() == "")){
					sub.invtAcct.removeClass('fieldError');
					sub.invtAcct.val("");
					sub.invtAcctKey.val("");
					sub.invtAcctDesc.val("");
					sub.h_invtAcctDesc.val("");
					sub.navDate.val("");
					sub.postDate.val("");

					sub.currency.val("");
					sub.currencyHidden.val("");
					sub.fundBankCurrency.val("");
					
					sub.saCodeId.value("");
					sub.saCode.value("");
					sub.h_saCode.value("");
					sub.saCodeDesc.value("");
					sub.h_saCodeDesc.value("");
					
					/*sub.trnSABranchCode.removeClass('fieldError');
					sub.trnSABranchCode.val("");
					sub.trnSABranch.val("");
					sub.trnSABranchDesc.val("");
					sub.h_trnSABranchDesc.val("");*/
					
					//attachPopupTrnSABranch(0);
					attachPopupInvestmentNull();
					attachPopupProductNull();
				}
				
				//TO DO: change to bank account for fund account
				attachPopupBankAccountProd(rgProduct.productCode);
				
				var aTransactionDate = new Date(html.getApplicationDate());
				if(data.transactionDate == 'true' || data.transactionDate == true){
					var aTransactionDateYYYYMMDD = $.datepicker.formatDate( "yymmdd", aTransactionDate );
					var maxPaymentDateYYYMMDD = html.getWorkingDate(aTransactionDateYYYYMMDD, -1);
					var dateTrade = $.datepicker.parseDate('yymmdd', maxPaymentDateYYYMMDD, null);
					sub.tradeDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', dateTrade));
				} else {
					sub.tradeDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aTransactionDate));
				}
				
				sub.goodFundDate.value(sub.tradeDate.val());
				
				calcRgProduct();
				sub.navDate.change();
				sub.effectiveDate.val(data.effectiveDate);
				sub.liquidDate.val(data.liquidDate);
				digitAmount = data.amountRoundValue;
				typeAmount = data.amountRoundType;
				digitUnit = data.unitRoundValue;
				typeUnit = data.unitRoundType;
				digitPrice = data.priceRoundValue;
				typePrice = data.priceRoundType;
				sub.subLock.val(data.subLock);
				attachPopupInvestment(sub.fundCode.val());
				
				calcFeeAmt();
				calcDiscAmt();
				calcOtherAmt();
				calcTaxAmt();
				calcTotFeeAmt();
				calcNetAmount();
				calcUnit();
				
				if (jQuery.trim($('#fundAccountKey').val()).length == 0) {
					$.get("@{getBankAccountFromRgProduct()}", {'productCode':sub.fundCode.val()}, function(data) {
						checkRedirect(data);
						if (data) {
							rgProduct.bankAccountSub = data;

							sub.fundAccount.value(rgProduct.bankAccountSub.bankAccountNo);
							sub.fundAccountKey.value(rgProduct.bankAccountSub.bankAccountKey);
							sub.fundBeneficiary.value(rgProduct.bankAccountSub.name);
							
							sub.fundBankAccount.value(rgProduct.bankAccountSub.bankCode.thirdPartyCode);
							sub.fundBankAccountKey.value(rgProduct.bankAccountSub.bankCode.thirdPartyKey);
							sub.fundBankAccountName.value(rgProduct.bankAccountSub.bankCode.thirdPartyName);
							
							$('#fundCurrency').val(rgProduct.bankAccountSub.currency.currencyCode);
						}
					});
				}
			}
		});
		
		sub.tradeDate.change(function(){
			sub.goodFundDate.value(sub.tradeDate.val());
			calcRgProduct();
			sub.navDate.change();
		});
		
		sub.navDate.change(function(){
			if (!sub.fundCode.isEmpty() && !sub.navDate.isEmpty()) {
				rgNav = html.getRgNav(sub.navDate.val().fmtYYYYMMDD("/"), sub.fundCode.val());
				getVarGlobal();
				sub.price.valueRnd(0, true, digitPrice, typePrice);
				console.log(digitPrice+"---"+ typePrice);
				if (rgProduct.fixnav == true) {
					sub.price.valueRnd(rgProduct.fixNavPrice, true, digitPrice, typePrice);
				} else {
					if (rgNav) {
						if (typeof rgNav.offerPct === "undefined") { 
							var offerPrice = rgNav.nav;
						} else {
							var offerPrice = (1+((rgNav.offerPct)/100))*rgNav.nav;
						}
						sub.price.valueRnd(offerPrice, true, digitPrice, typePrice);
					}
				}
				calcUnit();
			}
		});
		
		sub.goodFundDate.change(function(){
			calcRgProduct();
		});
		
		/*sub.invest.blur(function(){
			if((sub.invest.val()=="") || (sub.invest.isChange())){
				sub.investBank.value("");
				sub.investBankName.value("");
			}
		});*/
		
		/*sub.fund.blur(function(){
			if((sub.fund.val()=="") || (sub.fund.isChange())){
				sub.fundBank.value("");
				sub.fundBankName.value("");
			}
		});*/
		
		sub.inputBy.change(function(){	
			$("#netAmtError").html("");
			$("#amountError").html("");
			viewInputBy();
			calcFeeAmt();
			calcDiscAmt();
			calcOtherAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcNetAmount();
			calcUnit();
		});
		
		
		sub.amount.change(function(data){
//			var digit =sub.amountRoundValue.val();
//			var roundType =sub.amountRoundType.val();
			var value = sub.amount.val();
//			sub.amount.value(value.toNumber(","), true, digitAmount, typeAmount);

			if (sub.amount.val()!="") {
				sub.amount.valueRnd(value.toNumber(","), true, digitAmount, typeAmount);
			} else {
				sub.amount.val("");
				sub.amountStripped.val("");
			}
		
			rgFeeTiers(true);
			/*sub.feePct.val('');
			sub.feePctStripped.val('');*/
			sub.feeAmt.focus();
		});
		
		sub.netAmt.change(function(){
//			var digit =sub.amountRoundValue.val();
//			var roundType =sub.amountRoundType.val();
//			var id = 'netAmt';
			var value = sub.netAmt.val();
//			roundAmount(id, value, digit, roundType);
//			sub.netAmt.value(value.toNumber(","), true, digitAmount, typeAmount);

			if (sub.netAmt.val() != "") {
				sub.netAmt.valueRnd(value.toNumber(","), true, digitAmount, typeAmount);
			} else {
				sub.netAmt.val("");
				sub.netAmtStripped.val("");
			}

			rgFeeTiers(true);
			/*sub.feePct.val('');
			sub.feePctStripped.val('');*/
		});
		
		//sub.feePct.autoNumeric({vMax: '100'});
		sub.feePct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		sub.feePct.change(function(){
			sub.feePctStripped.val(sub.feePct.val());
			calcFeeAmt();
			//calcDiscAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcNetAmount();
			calcUnit();
		});
		
		/*sub.discPct.autoNumeric({vMax: '100'});
		sub.discPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		sub.discPct.change(function(){
			sub.discPctStripped.val(sub.discPct.val());
			calcDiscAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcNetAmount();
			calcUnit();
		});*/
		
		/*sub.otherPct.autoNumeric({vMax: '100'});
		sub.otherPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
		
		sub.otherPct.change(function(){
			sub.otherPctStripped.val(sub.otherPct.val());
			calcOtherAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcNetAmount();
			calcUnit();
		});	*/
		
		sub.price.change(function(){
			var value = sub.price.val();
			sub.price.removeClass('fieldError');
			priceError.html("");
			if(value != "") {
				sub.price.valueRnd(value.toNumber(","), true, digitPrice, typePrice);
				calcUnit();
				if (sub.price.val() == 0) {
					sub.price.addClass('fieldError');
					priceError.html("can not filled 0");
				} 
			} else {
				sub.unit.valueRnd(0, true, digitPrice, typePrice);
			}
		});
		
		sub.feeAmt.change(function(){
			var value = sub.feeAmt.val();
			sub.feeAmt.valueRnd(value.toNumber(","), true, digitAmount, typeAmount);
			sub.feePct.value("");
			//calcDiscAmt();
			calcTaxAmt();
			calcTotFeeAmt();
			calcNetAmount();
			calcUnit();
		});
		
		/*sub.discAmt.change(function(){
			var value = sub.discAmt.val();
			sub.discAmt.value(value.toNumber(","), true, digitAmount, typeAmount);
			sub.discPct.value("");
			calcTaxAmt();
			calcTotFeeAmt();
			calcNetAmount();
			calcUnit();
		});*/
		
		sub.otherAmt.change(function(){
			var value = sub.otherAmt.val();
			console.log("otherAmt changed " + value + ", " + value.toNumber(","));
			sub.otherAmt.valueRnd(value.toNumber(","), true, digitAmount, typeAmount);
			//sub.otherPct.value("");
			calcTaxAmt();
			calcTotFeeAmt();
			calcNetAmount();
			calcUnit();
		});
		
		var dataType =0;
		if(sub.saCode.val() != ""){
			dataType = sub.saCodeId.val();
		}
		attachPopupTrnSABranch(dataType);
		
		
//		sub.trnSABranchCode.blur(function(){
//			if((sub.trnSABranchCode.isChange()) || (sub.trnSABranchCode.val() == "")) {
//				sub.trnSABranchCode.removeClass('fieldError');
//				sub.trnSABranchCode.val("");
//				sub.trnSABranch.val("");
//				sub.trnSABranchDesc.val("");
//				sub.h_trnSABranchDesc.val("");
//				sub.trnSABranchKey.val("");
////				attachPopupTrnSABranch(0);
//			}
//				
//		});
		
		if (jQuery.trim($('#fundCode').val()).length > 0) {
			var vFundCode = $('#fundCode').val();
			$.get("@{Pick.rgProductByEffDateAndLiqDate()}", {'code':vFundCode}, function(data) {
				checkRedirect(data);
				$('#currency').val(data.currencyCode);
				$('#currencyHidden').val(data.currencyCode);
			});
		}
		
		if (jQuery.trim($('#fundAccountKey').val()).length > 0) {
			var vFundAccount = $('#fundAccountKey').val();
			$.get("@{Pick.fundBankAccount()}", {'code':vFundAccount}, function(data) {
				checkRedirect(data);
				$('#fundAccount').val(data.bankAccountNo);
				$('#fundAccountKey').val(data.key);
				$('#fundBankAccount').val(data.bankCode.thirdPartyCode);
				$('#fundBankAccountKey').val(data.bankCode.thirdPartyKey);
				$('#fundBankAccountName').val(data.bankCode.thirdPartyName);
				$('#fundBeneficiary').val(data.name);
				$('#fundCurrency').val(data.currency.currencyCode);
			});
			
		}
		
		if (jQuery.trim($('#investAccountNo').val()).length > 0) {
			$.get("@{Pick.rgInvestmentAcct()}", {'type':$('#fundCode').val(), 'code':$('#invtAcct').val()}, function(data) {
				checkRedirect(data);
				rgInvestment = data;
				$('#invtAcctBankAcctCurrency').val(rgInvestment.bankAccount.currency.currencyCode);
			});
			var customerKey = 0;
			if( rgInvestment.customer ){
				customerKey = rgInvestment.customer.customerKey;
			}
			$.get("@{Pick.bankAccountByInvestmentProductCurrAccNo()}", {'currencyCode':$('#currencyHidden').val(), 'code':$('#investAccountNo').val(), 'customerKey':customerKey}, function(data) {
				checkRedirect(data);
				$('#investAccountKey').val(data.bankAccountKey);
				$('#investBankAccount').val(rgInvestment.bankAccount.bankCode.thirdPartyCode);
				$('#investBankAccountKey').val(rgInvestment.bankAccount.bankCode.thirdPartyKey);
				$('#investBankAccountName').val(rgInvestment.bankAccount.bankCode.thirdPartyName);
				$('#investBeneficiary').val(data.name);
				$('#investBankCurrency').val(data.currency.currencyCode);
			});
		}
		
		viewInputBy();
		calcTaxAmt();
		console.debug('calculate tax amount');
		calcTotFeeAmt();
		var fundCode ="_";
		if(sub.fundCode.val() != ""){
			fundCode = sub.fundCode.val();
			//fundCode = fundCode.replace(" ","+");
		}
		console.log('fundCode ' + fundCode);
		attachPopupInvestment(fundCode);
		
		var accountNo ="_";
		if(sub.invtAcct.val() != ""){
			accountNo = sub.invtAcct.val();
			accountNo = accountNo.replace(" ","+");
		}
		attachPopupBankAccount(fundCode, accountNo);
		
		var accountNoProd ="_";
		if(sub.fundCode.val() != ""){
			accountNoProd = sub.fundCode.val();
			accountNoProd = accountNoProd.replace(" ","+");
		}
		attachPopupBankAccountProd(accountNoProd);
		
		if (sub.dispatch.val() == "confirm") {
			alert("Data Subscribe save with id "+sub.tradeKey.val());
		}
		if (sub.dispatch.val() == "confirm") {
			//location.href='@{list()}';
			location.href='@{entry()}';
		}
		
		if (($('#subscriptionTypeHidden').val()=='${subscriptiontype_new_val}') || ($('#subscriptionTypeHidden').val()=='true')){
			$("input[name='subscriptionType']")[0].checked = true;
			$('#subscriptionTypeHidden').val($('#subscriptionType1').val());
		}
		
		if (($('#subscriptionTypeHidden').val()=='${subscriptiontype_topup_val}') || ($('#subscriptionTypeHidden').val()=='false')){
			$("input[name='subscriptionType']")[1].checked = true;
			$('#subscriptionTypeHidden').val($('#subscriptionType2').val());
		}
		
		$('#subscriptionType1').click(function(){
			$("input[name='subscriptionType']")[0].checked = true;
			$('#subscriptionTypeHidden').val($(this).val());
		});
		
		$('#subscriptionType2').click(function(){
			$("input[name='subscriptionType']")[1].checked = true;
			$('#subscriptionTypeHidden').val($(this).val());
		});
		
		if ($("#subFee").val() == 'true'){
			$("input[name='feePercent']")[0].checked = true;
			$("input[name='sub.feePercent']").val(true);
			if (('${mode}' != 'view') && ('${confirming}' != 'true')) {
				$('#feePct').removeAttr('disabled');
				$('#feeAmt').attr('disabled', true);
			}
		} else {
			$("input[name='feePercent']")[1].checked = true;
			$("input[name='sub.feePercent']").val(false);
			if (('${mode}' != 'view') && ('${confirming}' != 'true')) {
				$('#feeAmt').removeAttr('disabled');
				$('#feePct').attr('disabled', true);
			}
		}
		
		
		$('#subscriptionFee1').click(function(){
			$("input[name='feePercent']")[0].checked = true;
			$("input[name='sub.feePercent']").val(true);
			if (('${mode}' != 'view') && ('${confirming}' != 'true')) {
				$('#feeAmt').val('');
				$('#feeAmtStripped').val('');
				$('#feePct').removeAttr('disabled');
				$('#feeAmt').attr('disabled', true);
			}
		});
		
		$('#subscriptionFee2').click(function(){
			$("input[name='feePercent']")[1].checked = true;
			$("input[name='sub.feePercent']").val(false);
			if (('${mode}' != 'view') && ('${confirming}' != 'true')) {
				$('#feePct').val('');
				$('#feePctStripped').val('');
				$('#feeAmt').removeAttr('disabled');
				$('#feePct').attr('disabled', true);
			}
		});
		
		$('td[id=tdProduct] label b').html($('#fundCodeDesc').val());
		$('td[id=tdInvestor] label b').html($('#invtAcctDesc').val());
		
		var productCode = $('#fundCode').val();
		productCode = productCode.replace(/#/g, '${specialChar}').toString();
		var rgProduct = html.getRgProduct(productCode);
		
		// #4061 Elvino untuk mencegah validasi muncul saat click new data
		if ('${mode}' == 'entry' && $('#navDate').val() == '') {
		}else { $('#navDate').change(); }
		
		calcUnit();
	}else{
		return new Subscription(html);
	}
}