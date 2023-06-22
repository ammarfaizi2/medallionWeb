function Switch(html) {
	if (this instanceof Switch) {

/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		//var app = html.inject(this, true);
		var componentss = [
		                   "switchTab"
		                   , "clear"
		                   , "subscriptionFee1"
		                   , "subscriptionFee2"
		                   , "switchTableDetail"
		                   , "outTradeBy"
		                   , "inTradeBy"
		                   , "outFeePct"
		                   , "inFeePct"
		                   , "outFeeBy1"
		                   , "outFeeBy2"
		                   , "inFeeBy1"
		                   , "inFeeBy2"
		                   , "outNetAmt"
		                   , "inNetAmt"
		                   , "outFeeAmt"
		                   , "inFeeAmt"
		                   , "outUnit"
		                   , "outUnitStripped"
		                   , "inUnit"
		                   , "inUnitStripped"
		                   , "outPrice"
		                   , "outPriceStripped"
		                   , "inPrice"
		                   , "inPriceStripped"
		                   , "outOtherAmt"
		                   , "outOtherAmtStripped"
		                   , "inOtherAmt"
		                   , "inOtherAmtStripped"
		                   , "inAmt"
		                   , "inAmtStripped"
		                   , "outFundThirdParty"
		                   , "inFundThirdParty"
		                   , "outFeeAmtStripped"
		                   , "inFeeAmtStripped"
		                   , "outFeePctStripped"
		                   , "inFeePctStripped"
		                   , "outTotFeeAmt"
		                   , "outTotFeeAmtStripped"
		                   , "inTotFeeAmt"
		                   , "inTotFeeAmtStripped"
		                   , "outPayAmt"
		                   , "outPayAmtStripped"
		                   , "OUTBYUNIT"
		                   , "OUTBYAMOUNT"
		                   , "INBYGROSS"
		                   , "INBYNET"
		                   , "pOutUnit"
		                   , "pOutPrice"
		                   , "pInNetAmount"
		                   , "pInAmount"
		                   , "outTradeByHidden"
		                   , "inTradeByHidden"
		                   , "liquidation"
		                   , "firstSubscribe"
		                   , "errOutPrice"
		                   , "errInPrice"
		                   , "outNetAmtStripped"
		                   , "inNetAmtStripped"
		                   , "outAvailableUnit"
		                   , "h_outAvailableUnit"
		                   , "outAvailableUnitStripped"
		                   , "dispatch"
		                   , "isApproval"
		                   , "confirming"
		                   , "effectiveDate"
		                   , "switchingKey"
		                   , "goodFundDate"
		                   , "type"
		                   , "lookupSwiTierType"
		                   , "outThirdPartyByFundManagerKey"
		                   , "outThirdPartyByFundManagerCode"
		                   , "outThirdPartyByFundManagerName"
		                   , "inThirdPartyByFundManagerKey"
		                   , "inThirdPartyByFundManagerCode"
		                   , "inThirdPartyByFundManagerName"
		                   , "outRedLock"
		                   , "outDiscPct"
		                   , "outDiscPctStripped"
		                   , "outDiscAmt"
		                   , "outTaxPct"
		                   , "outTaxPctStripped"
		                   , "outTaxAmt"
		                   , "outTaxAmtStripped"
		                   , "outAmount"
		                   , "outAmountStripped"
		                   , "outAvailBalance"
		                   , "outTaxCode"
		                   , "outTaxKey"
		                   , "outCapGainTaxAmount"
		                   , "outOtherPct"
		                   , "outAmountRoundValue"
		                   , "outAmountRoundType"
		                   , "outUnitRoundValue"
		                   , "outUnitRoundType"
		                   , "outPriceRoundValue"
		                   , "outPriceRoundType"
		                   , "inSubLock"
		                   , "inDiscPct"
		                   , "inDiscPctStripped"
		                   , "inDiscAmt"
		                   , "inTaxPct"
		                   , "inTaxPctStripped"
		                   , "inTaxAmt"
		                   , "inTaxAmtStripped"
		                   , "inTaxCode"
		                   , "inTaxKey"
		                   , "inCapGainTaxAmount"
		                   , "inOtherPct"
		                   , "inAmountRoundValue"
		                   , "inAmountRoundType"
		                   , "inUnitRoundValue"
		                   , "inUnitRoundType"
		                   , "inPriceRoundValue"
		                   , "inPriceRoundType"
		                   , "sKey"
		                   , "investorNo"
		                   , "investorId"
		                   , "investorNoKey"
		                   , "investorName"
		                   , "investorNoHelp"
		                   , "investorNoDesc"
		                   , "switchTypeList"
		                   , "switchTypeListHidden"
		                   , "errOutFundCodeValidation"
		                   , "outNonFundThirdParty"
		                   , "outFundCode"
		                   , "outFundCodeKey"
		                   , "outProductCode"
		                   , "outFundCodeHelp"
		                   , "outFundCodeDesc"
		                   , "outCurrencyCode"
		                   , "outCurrencyCodeHidden"
		                   , "outInvest"
		                   , "outInvestKey"
		                   , "outInvestName"
		                   , "outInvestHelp"
		                   , "outInvestDesc"
		                   , "outFmgrCode"
		                   , "outFmgrName"
		                   , "outSaCode"
		                   , "outSaCodeName"
		                   , "outSaCodeHidden"
		                   , "outSaCodeKeyHidden"
		                   , "outSaCodeNameHidden"
		                   , "outCustodianBank"
		                   , "outCustodianBankHidden"
		                   , "outFundCodeThirdParty"
		                   , "outFundCodeKeyThirdPartyHidden"
		                   , "outFundCodeCodeThirdPartyHidden"
		                   , "outFundCodeNameThirdPartyHidden"
		                   , "outFundCodeHelpThirdParty"
		                   , "outFundCodeDescThirdParty"
		                   , "outCurrencyCodeThirdParty"
		                   , "outCurrencyCodeThirdPartyHidden"
		                   , "outInvestThirdParty"
		                   , "outInvestHelpThirdParty"
		                   , "outInvestDescThirdParty"
		                   , "outFmgrCodeThirdParty"
		                   , "outFmgrNameThirdParty"
		                   , "outFmgrKeyThirdPartyHidden"
		                   , "outFmgrCodeThirdPartyHidden"
		                   , "outFmgrNameThirdPartyHidden"
		                   , "outSaCodeThirdParty"
		                   , "outSaCodeNameThirdParty"
		                   , "outCustodianBankThirdParty"
		                   , "outCustodianBankThirdPartyHidden"
		                   , "errInFundCodeValidation"
		                   , "inNonFundThirdParty"
		                   , "inFundCode"
		                   , "inFundCodeKey"
		                   , "inProductCode"
		                   , "inFundCodeHelp"
		                   , "inFundCodeDesc"
		                   , "inCurrencyCode"
		                   , "inCurrencyCodeHidden"
		                   , "inInvest"
		                   , "inInvestKey"
		                   , "inInvestName"
		                   , "inInvestHelp"
		                   , "inInvestDesc"
		                   , "inFmgrCode"
		                   , "inFmgrName"
		                   , "inSaCode"
		                   , "inSaCodeName"
		                   , "inSaCodeHidden"
		                   , "inSaCodeKeyHidden"
		                   , "inSaCodeNameHidden"
		                   , "inCustodianBank"
		                   , "inCustodianBankHidden"
		                   , "inFundThirdParty"
		                   , "inFundCodeThirdParty"
		                   , "inFundCodeKeyThirdPartyHidden"
		                   , "inFundCodeCodeThirdPartyHidden"
		                   , "inFundCodeNameThirdPartyHidden"
		                   , "inFundCodeHelpThirdParty"
		                   , "inFundCodeDescThirdParty"
		                   , "inCurrencyCodeThirdParty"
		                   , "inCurrencyCodeThirdPartyHidden"
		                   , "inInvestThirdParty"
		                   , "inInvestHelpThirdParty"
		                   , "inInvestDescThirdParty"
		                   , "inFmgrCodeThirdParty"
		                   , "inFmgrNameThirdParty"
		                   , "inFmgrKeyThirdPartyHidden"
		                   , "inFmgrCodeThirdPartyHidden"
		                   , "inFmgrNameThirdPartyHidden"
		                   , "inSaCodeThirdParty"
		                   , "inSaCodeNameThirdParty"
		                   , "inCustodianBankThirdParty"
		                   , "inCustodianBankThirdPartyHidden"
		                   , "tradeDate"
		                   , "navDate"
		                   , "postDate"
		                   , "externalReference"
		                   , "remarks"
		                   , "forCancelSwitching"
		                   , "remarkForCancel"
		                   , "remarkForCancelHidden"
		                   , "cancelDateHidden"
		                   , "cancelPostDateHidden"
		                   , "remarkForCancelError"
		                   , "correction"
		                   , "needCorrection"
		                   , "pRemarksCorrection"
		                   , "remarksCorrection"
		                   , "remarksCorrectionHidden"
		                   , "remarksCorrectionError"
		                   , "outFundName"
		                   , "outBankAccountProduct"
		                   , "outBankAccountNo"
		                   , "outBankAccountKey"
		                   , "outBankAccountNoHidden"
		                   , "outBankThirdPartyCodeHidden"
		                   , "outBankThirdPartyNameHidden"
		                   , "outBankBeneficiaryNameHidden"
		                   , "outBankCurrencyCodeHidden"
		                   , "outInvestorBankAccountKey"
		                   , "outBankAccountNoHelp"
		                   , "outBankThirdPartyCode"
		                   , "outBankThirdPartyName"
		                   , "outBankBeneficiaryName"
		                   , "outBankCurrencyCode"
		                   , "outBankAccountExternalProduct"
		                   , "outBankAccountNoExternal"
		                   , "outBankThirdPartyKeyExternalHidden"
		                   , "outBankAccountNoExternalHidden"
		                   , "outBankAccountNameExternalHidden"
		                   , "outBankCodeThirdPartyKeyExternalHidden"
		                   , "outBankCodeThirdPartyCodeExternalHidden"
		                   , "outBankCodeThirdPartyNameExternalHidden"
		                   , "outBankCurrencyCodeExternalHidden"
		                   , "outBankAccountNoHelpExternal"
		                   , "outBankThirdPartyCodeExternal"
		                   , "outBankThirdPartyNameExternal"
		                   , "outBankBeneficiaryNameExternal"
		                   , "outBankCurrencyCodeExternal"
		                   , "inFundName"
		                   , "inBankAccountProduct"
		                   , "inBankAccountNo"
		                   , "inBankAccountKey"
		                   , "inBankAccountNoHidden"
		                   , "inBankThirdPartyCodeHidden"
		                   , "inBankThirdPartyNameHidden"
		                   , "inBankBeneficiaryNameHidden"
		                   , "inBankCurrencyCodeHidden"
		                   , "inInvestorBankAccountKey"
		                   , "inBankAccountNoHelp"
		                   , "inBankThirdPartyCode"
		                   , "inBankThirdPartyName"
		                   , "inBankBeneficiaryName"
		                   , "inBankCurrencyCode"
		                   , "inBankAccountExternalProduct"
		                   , "inBankAccountNoExternal"
		                   , "inBankThirdPartyKeyExternalHidden"
		                   , "inBankAccountNoExternalHidden"
		                   , "inBankAccountNameExternalHidden"
		                   , "inBankCodeThirdPartyKeyExternalHidden"
		                   , "inBankCodeThirdPartyCodeExternalHidden"
		                   , "inBankCodeThirdPartyNameExternalHidden"
		                   , "inBankCurrencyCodeExternalHidden"
		                   , "inBankAccountNoHelpExternal"
		                   , "inBankThirdPartyCodeExternal"
		                   , "inBankThirdPartyNameExternal"
		                   , "inBankBeneficiaryNameExternal"
		                   , "inBankCurrencyCodeExternal"
		                   , "paymentDate"
		                   ];

		var app = html.injectComp(this, false, componentss);
		app["outType"] = html.name("liquidation");
		app["inType"] = html.name("firstSubscribe");

		var SWITCH_IN_OUT = "SWITCHING_TYPE-OUTIN";
		var SWITCH_IN = "SWITCHING_TYPE-IN";
		var SWITCH_OUT = "SWITCHING_TYPE-OUT";

		var TRANSACTION_BY_UNIT = "TRANSACTION_BY_UNIT";
		var TRANSACTION_BY_AMOUNT = "TRANSACTION_BY_AMOUNT";

		var INPUT_BY_GROSS = "INPUT_BY_GROSS";
		var INPUT_BY_NET = "INPUT_BY_NET";
		var TIER_COMPARISON_TYPE_FLAT = "TIER_COMPARISON_TYPE-FLAT";

		var SUBSCRIBE = "SUBSCRIBE";
		var REDEMPTION = "REDEMPTION";
		var SWITCHING = "SWITCHING";

		var outRgProduct = null;
		var inRgProduct = null;

		var outUnitRoundValue = (app.outUnitRoundValue.val()? app.outUnitRoundValue.val() : undefined);
		var outUnitRoundType = (app.outUnitRoundType.val()? app.outUnitRoundType.val() : undefined);

		var inUnitRoundValue = (app.inUnitRoundValue.val()? app.inUnitRoundValue.val() : undefined);
		var inUnitRoundType = (app.inUnitRoundType.val()? app.inUnitRoundType.val() : undefined);

		var outAmountRoundValue = (app.outAmountRoundValue.val()? app.outAmountRoundValue.val() : undefined);
		var outAmountRoundType = (app.outAmountRoundType.val()? app.outAmountRoundType.val() : undefined);

		var inAmountRoundValue = (app.inAmountRoundValue.val()? app.inAmountRoundValue.val() : undefined);
		var inAmountRoundType = (app.inAmountRoundType.val()? app.inAmountRoundType.val() : undefined);

		var outPriceRoundValue = (app.outPriceRoundValue.val()? app.outPriceRoundValue.val() : undefined);
		var outPriceRoundType = (app.outPriceRoundType.val()? app.outPriceRoundType.val() : undefined);

		var inPriceRoundValue = (app.inPriceRoundValue.val()? app.inPriceRoundValue.val() : undefined);
		var inPriceRoundType = (app.inPriceRoundType.val()? app.inPriceRoundType.val() : undefined);

		var aNavDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', app.navDate.val(), null);
		var formattedNavDate = $.datepicker.formatDate('yymmdd', aNavDate);

		var aTradeDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', app.tradeDate.val(), null);
		var formattedTradeDate = $.datepicker.formatDate('yymmdd', aTradeDate);

		var aPostDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', app.postDate.val(), null);
		var formattedPostDate = $.datepicker.formatDate('yymmdd', aPostDate);

		var outRgInvestment = (app.outInvest.val() == "") ? null: html.getRgInvestmentAcct(null, app.outInvest.val());
		var inRgInvestment = (app.inInvest.val() == "") ? null: html.getRgInvestmentAcct(null, app.inInvest.val());

		var outRgPortfolio = (app.outFundCode.isEmpty() || app.outInvest.isEmpty() || app.tradeDate.isEmpty()) ? null : html.getRgPortfolio(app.outFundCode.val(), app.outInvest.val(), formattedPostDate);
		var outRgTax = (app.outTaxCode.isEmpty()) ? null : html.getRgTax(app.outTaxCode.val());

		var outProductCode = (outRgInvestment) ? outRgInvestment.rgProduct.productCode : "";
		var inProductCode = (inRgInvestment) ? inRgInvestment.rgProduct.productCode : "";

		var outRgNav = (app.navDate.val() == "" || outProductCode == "") ? null : html.getRgNav(app.navDate.val().fmtYYYYMMDD("/"), outProductCode);
		var inRgNav = (app.navDate.val() == "" || inProductCode == "") ? null : html.getRgNav(app.navDate.val().fmtYYYYMMDD("/"), inProductCode);

/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		html.clazz('calendar').datepicker();

		if(('${from}' == 'unitRegistry')){
			app.outFeePct.attr('disabled', true);
			app.inFeeAmt.attr('disabled', true);
		}
		
		app.switchTab.tabs();
		app.switchTab.css('height', '380');

		//app.switchTypeList.children().eq(0).remove();
		app.outTradeBy.children().eq(0).remove();
		app.inTradeBy.children().eq(0).remove();

/* =========================================================================== 
 * Method
 * ========================================================================= */
		function rgFeeTiers(isImplement, state) {
			getVarGlobal();
			var amount;
			var tierType;
			var productCode;
			var type;
			var inputBy;

			if((state == "out") && (outRgProduct != null))
			{
				amount = app.outNetAmt.val().toNumber(",");
				tierType = outRgProduct.lookupByRedTierType.lookupId;
				productCode = outRgProduct.productCode;
				type = SWITCHING;

				if(amount != 0)
				{
					if('${haserror}' != 'haserror' && app.confirming.val() != "true"){
						html.setFeeTier(tierType, productCode, amount, app.outFeePct, app.outFeeAmt, type);
					}
					
					/*if(app.outFeePct.val() > 0)
					{
						console.log("1) rgFeeTiers " )
						app.outFeeBy1.attr("checked", true); 
						app.outFeeAmt.attr("disabled", true);
					}*/

					if(app.outTradeBy.val() == TRANSACTION_BY_AMOUNT){
						calcUnit("out");
					}

					calcAmount("out");
					calcFeeAmt("out");
					//calcDiscAmt("out");
					calcTaxAmt("out");
					calcOtherAmt("out");
				}
			}
			
			if((state == "in") && (inRgProduct != null))
			{
				amount = getInputBySwitchInAmount();
				tierType = inRgProduct.lookupBySubTierType.lookupId;
				productCode = inRgProduct.productCode;
				type = SWITCHING;
				inputBy = app.inTradeBy.val();

				if(amount != 0)
				{
					if(app.switchTypeList.val() != SWITCH_IN_OUT)
					{
						html.setFeeTier(tierType, productCode, amount, app.inFeePct, app.inFeeAmt, type, inputBy, inAmountRoundValue, inAmountRoundType);
					}
					calcFeeAmt("in");
					//calcDiscAmt("in");

					if(app.switchTypeList.val() != SWITCH_IN_OUT)
					{
						calcOtherAmt("in");
					}

					calcTaxAmt("in");
					calcTotFeeAmt("in");
					calcNetAmount("in");
					calcUnit("in");
				}
			}
			
		}

		function viewBySwitchTypeList(cond) {
			if((app.switchTypeList.val() == SWITCH_IN_OUT) || (app.switchTypeList.isEmpty()))
			{
				app.outFundThirdParty.hide();
				app.outNonFundThirdParty.show();
				app.outBankAccountProduct.show();
				app.outBankAccountExternalProduct.hide();
				app.inFundThirdParty.hide();
				app.inNonFundThirdParty.show();
				app.inBankAccountProduct.show();
				app.inBankAccountExternalProduct.hide();

				$("#tab-1 table[id=switchTableDetail] td:first label span").html("*");
				$("#tab-1 table[id=switchTableDetail] td:last label span").html("*");

				//app.outTradeBy.val(TRANSACTION_BY_UNIT);

				if((app.confirming.val() != "true") && (app.dispatch.val() != "view"))
				{
					$("input[id*='inType']").removeAttr("disabled");
					$("input[id*='outType']").removeAttr("disabled");
					app.inTradeBy.removeAttr("disabled");

					//app.inTradeBy.attr("disabled", "disabled");

					//$("input[id*='inType']").removeAttr("disabled");
					//app.inTradeBy.removeAttr("disabled");
					app.inTradeBy.val(INPUT_BY_GROSS);
					app.inTradeByHidden.val(INPUT_BY_GROSS);
					viewTradeBy("in");
					app.inTradeBy.attr("disabled", "disabled");
					app.inAmt.attr("disabled", "disabled");
					app.inOtherAmt.removeAttr("disabled");
					//app.inNetAmt.removeAttr("disabled");

					if(!app.inFeePct.isEmpty())
					{
						app.subscriptionFee1.attr("checked",true);
						app.inFeePct.removeAttr("disabled");
						app.inFeeAmt.attr("disabled", "disabled");
					}

					if(!app.inFeeAmt.isEmpty())
					{
						app.subscriptionFee2.attr("checked",true);
						app.inFeePct.attr("disabled", "disabled");
						app.inFeeAmt.removeAttr("disabled");
					}

					if(app.inFeePct.isEmpty() && app.inFeeAmt.isEmpty())
					{
						app.subscriptionFee1.attr("checked",true);
						app.inFeePct.removeAttr("disabled");
						app.inFeeAmt.attr("disabled", "disabled");
					}

					app.subscriptionFee1.removeAttr("disabled");
					app.subscriptionFee2.removeAttr("disabled");

					/*
					app.outTradeBy.removeAttr("disabled");
					app.outOtherAmt.removeAttr("disabled");
					//app.outFeeBy1.attr("checked",true);
					app.outFeeBy1.removeAttr("disabled");
					app.outFeeBy2.removeAttr("disabled");
					app.outFeePct.removeAttr("disabled");
					*/

					$("input[id*='outType']").removeAttr("disabled");
					app.outTradeBy.removeAttr("disabled");
					app.outUnit.removeAttr("disabled");
					app.outFeeBy1.removeAttr("disabled");
					app.outFeePct.removeAttr("disabled");
					app.outFeeBy2.removeAttr("disabled");
					app.outNetAmt.removeAttr("disabled");
					app.outOtherAmt.removeAttr("disabled");
					app.paymentDate.removeAttr("disabled");
					$("p[id=pPaymentDate] label span").html("*");
				}

				viewTradeBy("out");

				app.outFundCodeThirdParty.val("");
				app.outFundCodeKeyThirdPartyHidden.val("");
				app.outFundCodeCodeThirdPartyHidden.val("");
				app.outFundCodeNameThirdPartyHidden.val("");
				app.outFundCodeDescThirdParty.val("");
				app.outCurrencyCodeThirdParty.val("");
				app.outCurrencyCodeThirdPartyHidden.val("");
				app.outFmgrCodeThirdParty.val("");
				app.outFmgrNameThirdParty.val("");
				app.outFmgrKeyThirdPartyHidden.val("");
				app.outFmgrCodeThirdPartyHidden.val("");
				app.outFmgrNameThirdPartyHidden.val("");
				app.outCustodianBankThirdParty.val("");
				app.outCustodianBankThirdPartyHidden.val("");

				app.outBankAccountNoExternal.val("");
				app.outBankThirdPartyKeyExternalHidden.val("");
				app.outBankAccountNoExternalHidden.val("");
				app.outBankAccountNameExternalHidden.val("");
				app.outBankCodeThirdPartyKeyExternalHidden.val("");
				app.outBankCodeThirdPartyCodeExternalHidden.val("");
				app.outBankCodeThirdPartyNameExternalHidden.val("");
				app.outBankCurrencyCodeExternalHidden.val("");
				app.outBankThirdPartyCodeExternal.val("");
				app.outBankThirdPartyNameExternal.val("");
				app.outBankBeneficiaryNameExternal.val("");
				app.outBankCurrencyCodeExternal.val("");

				app.outFundCodeThirdParty.attr("disabled", "disabled");
				app.outFundCodeKeyThirdPartyHidden.attr("disabled", "disabled");
				app.outFundCodeCodeThirdPartyHidden.attr("disabled", "disabled");
				app.outFundCodeNameThirdPartyHidden.attr("disabled", "disabled");
				app.outFundCodeHelpThirdParty.attr("disabled", "disabled");
				app.outFundCodeDescThirdParty.attr("disabled", "disabled");
				app.outCurrencyCodeThirdPartyHidden.attr("disabled", "disabled");
				app.outFmgrKeyThirdPartyHidden.attr("disabled", "disabled");
				app.outFmgrCodeThirdPartyHidden.attr("disabled", "disabled");
				app.outFmgrNameThirdPartyHidden.attr("disabled", "disabled");
				app.outCustodianBankThirdPartyHidden.attr("disabled", "disabled");

				app.outBankAccountExternalProduct.attr("disabled", "disabled");
				app.outBankAccountNoHelpExternal.attr("disabled", "disabled");

				app.outBankThirdPartyKeyExternalHidden.attr("disabled", "disabled");
				app.outBankAccountNoExternalHidden.attr("disabled", "disabled");
				app.outBankAccountNameExternalHidden.attr("disabled", "disabled");
				app.outBankCodeThirdPartyKeyExternalHidden.attr("disabled", "disabled");
				app.outBankCodeThirdPartyCodeExternalHidden.attr("disabled", "disabled");
				app.outBankCodeThirdPartyNameExternalHidden.attr("disabled", "disabled");
				app.outBankCurrencyCodeExternalHidden.attr("disabled", "disabled");
				app.outBankAccountNoHelpExternal.attr("disabled", "disabled");

				//if((app.confirming.val() != "true") && (app.dispatch.val() != "view"))
				//{
					//app.inFeeBy1.attr("checked",true);
					//app.inFeeBy1.removeAttr("disabled");
					//app.inFeeBy2.removeAttr("disabled");
					//app.inFeePct.removeAttr("disabled");

					app.inFundCodeThirdParty.val("");
					app.inFundCodeKeyThirdPartyHidden.val("");
					app.inFundCodeCodeThirdPartyHidden.val("");
					app.inFundCodeNameThirdPartyHidden.val("");
					app.inFundCodeDescThirdParty.val("");
					app.inCurrencyCodeThirdParty.val("");
					app.inCurrencyCodeThirdPartyHidden.val("");
					app.inFmgrCodeThirdParty.val("");
					app.inFmgrNameThirdParty.val("");
					app.inFmgrKeyThirdPartyHidden.val("");
					app.inFmgrCodeThirdPartyHidden.val("");
					app.inFmgrNameThirdPartyHidden.val("");
					app.inCustodianBankThirdParty.val("");
					app.inCustodianBankThirdPartyHidden.val("");

					app.inBankAccountNoExternal.val("");
					app.inBankThirdPartyKeyExternalHidden.val("");
					app.inBankAccountNoExternalHidden.val("");
					app.inBankAccountNameExternalHidden.val("");
					app.inBankCodeThirdPartyKeyExternalHidden.val("");
					app.inBankCodeThirdPartyCodeExternalHidden.val("");
					app.inBankCodeThirdPartyNameExternalHidden.val("");
					app.inBankCurrencyCodeExternalHidden.val("");
					app.inBankThirdPartyCodeExternal.val("");
					app.inBankThirdPartyNameExternal.val("");
					app.inBankBeneficiaryNameExternal.val("");
					app.inBankCurrencyCodeExternal.val("");
				//}

				app.inFundCodeThirdParty.attr("disabled", "disabled");
				app.inFundCodeKeyThirdPartyHidden.attr("disabled", "disabled");
				app.inFundCodeCodeThirdPartyHidden.attr("disabled", "disabled");
				app.inFundCodeNameThirdPartyHidden.attr("disabled", "disabled");
				app.inFundCodeHelpThirdParty.attr("disabled", "disabled");
				app.inFundCodeDescThirdParty.attr("disabled", "disabled");
				app.inCurrencyCodeThirdPartyHidden.attr("disabled", "disabled");
				app.inFmgrKeyThirdPartyHidden.attr("disabled", "disabled");
				app.inFmgrCodeThirdPartyHidden.attr("disabled", "disabled");
				app.inFmgrNameThirdPartyHidden.attr("disabled", "disabled");
				app.inCustodianBankThirdPartyHidden.attr("disabled", "disabled");

				app.inBankAccountExternalProduct.attr("disabled", "disabled");
				app.inBankAccountNoHelpExternal.attr("disabled", "disabled");

				app.inBankThirdPartyKeyExternalHidden.attr("disabled", "disabled");
				app.inBankAccountNoExternalHidden.attr("disabled", "disabled");
				app.inBankAccountNameExternalHidden.attr("disabled", "disabled");
				app.inBankCodeThirdPartyKeyExternalHidden.attr("disabled", "disabled");
				app.inBankCodeThirdPartyCodeExternalHidden.attr("disabled", "disabled");
				app.inBankCodeThirdPartyNameExternalHidden.attr("disabled", "disabled");
				app.inBankCurrencyCodeExternalHidden.attr("disabled", "disabled");
				app.inBankAccountNoHelpExternal.attr("disabled", "disabled");
				$('#outTradeBy option[value=""]').remove();
				$('#inTradeBy option[value=""]').remove();
				
			}

			if(app.switchTypeList.val() == SWITCH_OUT)
			{
				$("input[id*='inType']").attr("disabled", "disabled");
				//$("input[name='firstSubscribe']")[1].checked = true;
				$("input[id*='inType']")[1].checked = true
				app.firstSubscribe.val(false);

				app.outFundThirdParty.hide();
				app.outNonFundThirdParty.show();
				app.outBankAccountProduct.show();
				app.outBankAccountExternalProduct.hide();
				app.inFundThirdParty.show();
				app.inNonFundThirdParty.hide();
				app.inBankAccountProduct.hide();
				app.inBankAccountExternalProduct.show();

				app.inFundCode.removeClass('fieldError');
				app.inInvest.removeClass('fieldError');

				$("#tab-1 table[id=switchTableDetail] td:first label span").html("*");
				$("#tab-1 table[id=switchTableDetail] td:last label span").html("");

				//app.outTradeBy.val(TRANSACTION_BY_UNIT);

				if((app.confirming.val() != "true") && (app.dispatch.val() != "view"))
				{
					$("input[id*='outType']").removeAttr("disabled");
					app.outTradeBy.removeAttr("disabled");
					app.outUnit.removeAttr("disabled");
					app.outFeeBy1.removeAttr("disabled");
					app.outFeePct.removeAttr("disabled");
					app.outFeeBy2.removeAttr("disabled");
					app.outNetAmt.removeAttr("disabled");
					app.outOtherAmt.removeAttr("disabled");
				}

				viewTradeBy("out");

				app.outFundCodeThirdParty.val("");
				app.outFundCodeKeyThirdPartyHidden.val("");
				app.outFundCodeCodeThirdPartyHidden.val("");
				app.outFundCodeNameThirdPartyHidden.val("");
				app.outFundCodeDescThirdParty.val("");
				app.outCurrencyCodeThirdParty.val("");
				app.outCurrencyCodeThirdPartyHidden.val("");
				app.outFmgrCodeThirdParty.val("");
				app.outFmgrNameThirdParty.val("");
				app.outFmgrKeyThirdPartyHidden.val("");
				app.outFmgrCodeThirdPartyHidden.val("");
				app.outFmgrNameThirdPartyHidden.val("");
				app.outCustodianBankThirdParty.val("");
				app.outCustodianBankThirdPartyHidden.val("");

				app.outBankAccountNoExternal.val("");
				app.outBankThirdPartyKeyExternalHidden.val("");
				app.outBankAccountNoExternalHidden.val("");
				app.outBankAccountNameExternalHidden.val("");
				app.outBankCodeThirdPartyKeyExternalHidden.val("");
				app.outBankCodeThirdPartyCodeExternalHidden.val("");
				app.outBankCodeThirdPartyNameExternalHidden.val("");
				app.outBankCurrencyCodeExternalHidden.val("");
				app.outBankThirdPartyCodeExternal.val("");
				app.outBankThirdPartyNameExternal.val("");
				app.outBankBeneficiaryNameExternal.val("");
				app.outBankCurrencyCodeExternal.val("");

				app.outFundCodeKeyThirdPartyHidden.attr("disabled", "disabled");
				app.outFundCodeCodeThirdPartyHidden.attr("disabled", "disabled");
				app.outFundCodeNameThirdPartyHidden.attr("disabled", "disabled");
				app.outFundCodeHelpThirdParty.attr("disabled", "disabled");
				app.outCurrencyCodeThirdPartyHidden.attr("disabled", "disabled");
				app.outFmgrKeyThirdPartyHidden.attr("disabled", "disabled");
				app.outFmgrCodeThirdPartyHidden.attr("disabled", "disabled");
				app.outFmgrNameThirdPartyHidden.attr("disabled", "disabled");
				app.outCustodianBankThirdPartyHidden.attr("disabled", "disabled");

				app.outBankAccountExternalProduct.attr("disabled", "disabled");
				app.outBankAccountNoHelpExternal.attr("disabled", "disabled");

				app.outBankThirdPartyKeyExternalHidden.attr("disabled", "disabled");
				app.outBankAccountNoExternalHidden.attr("disabled", "disabled");
				app.outBankAccountNameExternalHidden.attr("disabled", "disabled");
				app.outBankCodeThirdPartyKeyExternalHidden.attr("disabled", "disabled");
				app.outBankCodeThirdPartyCodeExternalHidden.attr("disabled", "disabled");
				app.outBankCodeThirdPartyNameExternalHidden.attr("disabled", "disabled");
				app.outBankCurrencyCodeExternalHidden.attr("disabled", "disabled");
				app.outBankAccountNoHelpExternal.attr("disabled", "disabled");

				//$("input[id*='inType']").attr("disabled", "disabled");
				app.inTradeBy.attr("disabled", "disabled");
				app.inAmt.attr("disabled", "disabled");
				app.inOtherAmt.attr("disabled", "disabled");
				//app.inFeeBy1.attr("disabled", "disabled");
				app.inFeePct.attr("disabled", "disabled");
				//app.inFeeBy2.attr("disabled", "disabled");
				app.inFeeAmt.attr("disabled", "disabled");
				app.inNetAmt.attr("disabled", "disabled");
				app.subscriptionFee1.attr('disabled', "disabled");
				app.subscriptionFee2.attr('disabled', "disabled");
				
				if((app.confirming.val() != "true") && (app.dispatch.val() != "view"))
				{
					app.inFundCodeThirdParty.removeAttr("disabled");
					app.inFundCodeKeyThirdPartyHidden.removeAttr("disabled");
					app.inFundCodeCodeThirdPartyHidden.removeAttr("disabled");
					app.inFundCodeNameThirdPartyHidden.removeAttr("disabled");
					app.inFundCodeHelpThirdParty.removeAttr("disabled");
					app.inCurrencyCodeThirdPartyHidden.removeAttr("disabled");
					app.inFmgrKeyThirdPartyHidden.removeAttr("disabled");
					app.inFmgrCodeThirdPartyHidden.removeAttr("disabled");
					app.inFmgrNameThirdPartyHidden.removeAttr("disabled");
					app.inCustodianBankThirdPartyHidden.removeAttr("disabled");

					app.inBankAccountExternalProduct.removeAttr("disabled");
					app.inBankAccountNoHelpExternal.removeAttr("disabled");

					app.inBankThirdPartyKeyExternalHidden.removeAttr("disabled");
					app.inBankAccountNoExternalHidden.removeAttr("disabled");
					app.inBankAccountNameExternalHidden.removeAttr("disabled");
					app.inBankCodeThirdPartyKeyExternalHidden.removeAttr("disabled");
					app.inBankCodeThirdPartyCodeExternalHidden.removeAttr("disabled");
					app.inBankCodeThirdPartyNameExternalHidden.removeAttr("disabled");
					app.inBankCurrencyCodeExternalHidden.removeAttr("disabled");
					app.inBankAccountNoHelpExternal.removeAttr("disabled");
					app.paymentDate.removeAttr("disabled");
					$("p[id=pPaymentDate] label span").html("*");
				}

				app.inFundCode.val(null);
				app.inFundCodeKey.val(null);
				app.inProductCode.val(null);
				app.inFundCodeDesc.val(null);
				app.inThirdPartyByFundManagerKey.val(null);
				app.inThirdPartyByFundManagerCode.val(null);
				app.inThirdPartyByFundManagerName.val(null);
				app.inCurrencyCode.val(null);
				app.inCurrencyCodeHidden.val(null);
				app.inInvest.val(null);
				app.inInvestKey.val(null);
				app.inInvestName.val(null);
				app.inInvestDesc.val(null);
				app.inFmgrCode.val(null);
				app.inFmgrName.val(null);
				app.inSaCode.val(null);
				app.inSaCodeName.val(null);
				app.inSaCodeHidden.val(null);
				app.inSaCodeKeyHidden.val(null);
				app.inSaCodeNameHidden.val(null);
				app.inCustodianBank.val(null);
				app.inCustodianBankHidden.val(null);

				app.inTradeBy.append($(document.createElement("option")).attr("value","").text(""));
				app.inTradeBy.val("");
				app.inAmt.val("");
				app.inAmtStripped.val(null);
				app.inNetAmt.val("");
				app.inNetAmtStripped.val(null);
				app.inUnit.val("");
				app.inUnitStripped.val(null);
				app.inPrice.val("");
				app.inPriceStripped.val(null);
				app.inFeeBy1.attr("checked", true);
				app.inFeePct.val("");
				app.inFeePctStripped.val(null);
				app.inFeeAmt.val("");
				app.inFeeAmtStripped.val(null);
				app.inOtherAmt.val("");
				app.inOtherAmtStripped.val(null);
				app.inTotFeeAmt.val("");
				app.inTotFeeAmtStripped.val(null);

				app.inBankAccountNo.val(null);
				app.inBankAccountKey.val(null);
				app.inBankAccountNoHidden.val(null);
				app.inBankThirdPartyCodeHidden.val(null);
				app.inBankThirdPartyNameHidden.val(null);
				app.inBankBeneficiaryNameHidden.val(null);
				app.inBankCurrencyCodeHidden.val(null);
				app.inInvestorBankAccountKey.val(null);
				app.inBankThirdPartyCode.val(null);
				app.inBankThirdPartyName.val(null);
				app.inBankBeneficiaryName.val(null);
				app.inBankCurrencyCode.val(null);
				$('#outTradeBy option[value=""]').remove();
			}
			
			if(app.switchTypeList.val() == SWITCH_IN)
			{
				app.outFundThirdParty.show();
				app.outNonFundThirdParty.hide();
				app.outBankAccountProduct.hide();
				app.outBankAccountExternalProduct.show();
				app.inFundThirdParty.hide();
				app.inNonFundThirdParty.show();
				app.inBankAccountProduct.show();
				app.inBankAccountExternalProduct.hide();

				app.outFundCode.removeClass('fieldError');
				app.outInvest.removeClass('fieldError');

				$("#tab-1 table[id=switchTableDetail] td:first label span").html("");
				$("#tab-1 table[id=switchTableDetail] td:last label span").html("*");
				
				$("p[id=pPaymentDate] label span").html("*");
				app.paymentDate.removeClass('fieldError');
								
				$("input[id*='outType']").attr("disabled", "disabled");
				app.outTradeBy.attr("disabled", "disabled");
				app.outUnit.attr("disabled", "disabled");
				app.outOtherAmt.attr("disabled", "disabled");
				app.outFeeBy1.attr("disabled", "disabled");
				app.outFeePct.attr("disabled", "disabled");
				app.outFeeBy2.attr("disabled", "disabled");
				app.outFeeAmt.attr("disabled", "disabled");
				app.outNetAmt.attr("disabled", "disabled");
//				app.paymentDate.attr("disabled", "disabled");
//				app.paymentDate.val(null);
				

				app.outTradeBy.append($(document.createElement("option")).attr("value","").text(""));
				app.outTradeBy.val("");
				
				viewTradeBy("in");
				
				if((app.confirming.val() != "true") && (app.dispatch.val() != "view"))
				{
					if('${haserror}' == 'haserror'){
						
					} else {
						app.inTradeBy.val(INPUT_BY_GROSS);
						app.inTradeByHidden.val(INPUT_BY_GROSS);
					}
					
					$("input[id*='inType']").removeAttr("disabled");
					app.outFundCodeThirdParty.removeAttr("disabled");
					app.outFundCodeKeyThirdPartyHidden.removeAttr("disabled");
					app.outFundCodeCodeThirdPartyHidden.removeAttr("disabled");
					app.outFundCodeNameThirdPartyHidden.removeAttr("disabled");
					app.outFundCodeHelpThirdParty.removeAttr("disabled");
					app.outFundCodeDescThirdParty.removeAttr("disabled");
					app.outCurrencyCodeThirdPartyHidden.removeAttr("disabled");
					app.outFmgrKeyThirdPartyHidden.removeAttr("disabled");
					app.outFmgrCodeThirdPartyHidden.removeAttr("disabled");
					app.outFmgrNameThirdPartyHidden.removeAttr("disabled");
					app.outCustodianBankThirdPartyHidden.removeAttr("disabled");
	
					app.outBankAccountExternalProduct.removeAttr("disabled");
					app.outBankAccountNoHelpExternal.removeAttr("disabled");

					app.outBankThirdPartyKeyExternalHidden.removeAttr("disabled");
					app.outBankAccountNoExternalHidden.removeAttr("disabled");
					app.outBankAccountNameExternalHidden.removeAttr("disabled");
					app.outBankCodeThirdPartyKeyExternalHidden.removeAttr("disabled");
					app.outBankCodeThirdPartyCodeExternalHidden.removeAttr("disabled");
					app.outBankCodeThirdPartyNameExternalHidden.removeAttr("disabled");
					app.outBankCurrencyCodeExternalHidden.removeAttr("disabled");
					app.outBankAccountNoHelpExternal.removeAttr("disabled");

					//$("input[id*='inType']").removeAttr("disabled");
					app.inTradeBy.removeAttr("disabled");
					//app.inAmt.removeAttr("disabled");
					app.inOtherAmt.removeAttr("disabled");
					//app.inFeeBy1.removeAttr("disabled");
					app.inFeePct.removeAttr("disabled");
					//app.inFeeBy2.removeAttr("disabled");
					app.inNetAmt.removeAttr("disabled");
					app.subscriptionFee1.removeAttr("disabled");
					app.subscriptionFee2.removeAttr("disabled");
				}
				else
				{
					app.inFeePct.attr("disabled", "disabled");
					app.inFeeAmt.attr("disabled", "disabled");
					app.subscriptionFee1.attr("disabled", "disabled");
					app.subscriptionFee2.attr("disabled", "disabled");
				}

				app.outFundCode.val(null);
				app.outFundCodeKey.val(null);
				app.outProductCode.val(null);
				app.outFundCodeDesc.val(null);
				app.outThirdPartyByFundManagerKey.val(null);
				app.outThirdPartyByFundManagerCode.val(null);
				app.outThirdPartyByFundManagerName.val(null);
				app.outCurrencyCode.val(null);
				app.outCurrencyCodeHidden.val(null);
				app.outInvest.val(null);
				app.outInvestKey.val(null);
				app.outInvestName.val(null);
				app.outInvestDesc.val(null);
				app.outFmgrCode.val(null);
				app.outFmgrName.val(null);
				app.outSaCode.val(null);
				app.outSaCodeName.val(null);
				app.outSaCodeHidden.val(null);
				app.outSaCodeKeyHidden.val(null);
				app.outSaCodeNameHidden.val(null);
				app.outCustodianBank.val(null);
				app.outCustodianBankHidden.val(null);

				app.outAvailableUnit.val("");
				app.outAvailableUnitStripped.val("");
				app.outUnit.val("");
				app.outUnitStripped.val(null);
				app.outPrice.val("");
				app.outPriceStripped.val(null);
				
				app.outFeeBy1.attr("checked", true);
				app.outFeePct.val("");
				app.outFeePctStripped.val(null);
				app.outFeeAmt.val("");
				app.outFeeAmtStripped.val(null);
				app.outOtherAmt.val("");
				app.outOtherAmtStripped.val(null);
				app.outTotFeeAmt.val("");
				app.outTotFeeAmtStripped.val(null);
				app.outPayAmt.val("");
				app.outPayAmtStripped.val(null);
				app.outNetAmt.val("");
				app.outNetAmtStripped.val(null);

				app.outBankAccountNo.val(null);
				app.outBankAccountKey.val(null);
				app.outBankAccountNoHidden.val(null);
				app.outBankThirdPartyCodeHidden.val(null);
				app.outBankThirdPartyNameHidden.val(null);
				app.outBankBeneficiaryNameHidden.val(null);
				app.outBankCurrencyCodeHidden.val(null);
				app.outInvestorBankAccountKey.val(null);
				app.outBankThirdPartyCode.val(null);
				app.outBankThirdPartyName.val(null);
				app.outBankBeneficiaryName.val(null);
				app.outBankCurrencyCode.val(null);

				app.inFundCodeThirdParty.val("");
				app.inFundCodeKeyThirdPartyHidden.val("");
				app.inFundCodeCodeThirdPartyHidden.val("");
				app.inFundCodeNameThirdPartyHidden.val("");
				app.inFundCodeDescThirdParty.val("");
				app.inCurrencyCodeThirdParty.val("");
				app.inCurrencyCodeThirdPartyHidden.val("");
				app.inFmgrCodeThirdParty.val("");
				app.inFmgrNameThirdParty.val("");
				app.inFmgrKeyThirdPartyHidden.val("");
				app.inFmgrCodeThirdPartyHidden.val("");
				app.inFmgrNameThirdPartyHidden.val("");
				app.inCustodianBankThirdParty.val("");
				app.inCustodianBankThirdPartyHidden.val("");

				app.inBankAccountNoExternal.val("");
				app.inBankThirdPartyKeyExternalHidden.val("");
				app.inBankAccountNoExternalHidden.val("");
				app.inBankAccountNameExternalHidden.val("");
				app.inBankCodeThirdPartyKeyExternalHidden.val("");
				app.inBankCodeThirdPartyCodeExternalHidden.val("");
				app.inBankCodeThirdPartyNameExternalHidden.val("");
				app.inBankCurrencyCodeExternalHidden.val("");
				app.inBankThirdPartyCodeExternal.val("");
				app.inBankThirdPartyNameExternal.val("");
				app.inBankBeneficiaryNameExternal.val("");
				app.inBankCurrencyCodeExternal.val("");

				app.inFundCodeThirdParty.attr("disabled", "disabled");
				app.inFundCodeKeyThirdPartyHidden.attr("disabled", "disabled");
				app.inFundCodeCodeThirdPartyHidden.attr("disabled", "disabled");
				app.inFundCodeNameThirdPartyHidden.attr("disabled", "disabled");
				app.inFundCodeHelpThirdParty.attr("disabled", "disabled");
				app.inFundCodeDescThirdParty.attr("disabled", "disabled");
				app.inCurrencyCodeThirdPartyHidden.attr("disabled", "disabled");
				app.inFmgrKeyThirdPartyHidden.attr("disabled", "disabled");
				app.inFmgrCodeThirdPartyHidden.attr("disabled", "disabled");
				app.inFmgrNameThirdPartyHidden.attr("disabled", "disabled");
				app.inCustodianBankThirdPartyHidden.attr("disabled", "disabled");

				app.inBankAccountExternalProduct.attr("disabled", "disabled");
				app.inBankAccountNoHelpExternal.attr("disabled", "disabled");

				app.inBankThirdPartyKeyExternalHidden.attr("disabled", "disabled");
				app.inBankAccountNoExternalHidden.attr("disabled", "disabled");
				app.inBankAccountNameExternalHidden.attr("disabled", "disabled");
				app.inBankCodeThirdPartyKeyExternalHidden.attr("disabled", "disabled");
				app.inBankCodeThirdPartyCodeExternalHidden.attr("disabled", "disabled");
				app.inBankCodeThirdPartyNameExternalHidden.attr("disabled", "disabled");
				app.inBankCurrencyCodeExternalHidden.attr("disabled", "disabled");
				app.inBankAccountNoHelpExternal.attr("disabled", "disabled");
				$('#inTradeBy option[value=""]').remove();
			}
			
			if(cond == false){
				checkOutFeeBy();
			}
			//checkInFeeBy();
			app.switchTypeListHidden.val(app.switchTypeList.val());
		}

		function investorLookup() {
			app.investorNo.dynapopup('PICK_CF_MASTER_BY_INVESTOR', app.investorNo.val(), 'switchTypeList', function(data){
				app.investorId.val(data.customerNo);
				app.investorName.val(data.description);
				
				if((app.investorNo.isChange()) || (app.investorNo.val() == "")) {
					app.outInvest.removeClass('fieldError');
					app.outInvest.val("");
					app.outInvestKey.val("");
					app.outInvestName.val("");
					app.outInvestDesc.val("");
					
					app.outSaCode.val("");
					app.outSaCodeName.val("");
					app.outSaCodeHidden.val("");
					app.outSaCodeNameHidden.val("");
					app.outCustodianBank.val("");
					app.outInvestorBankAccountKey.val("");
					app.outFmgrCode.val("");
					app.outFmgrName.val("");
					app.outCurrencyCode.val("");
					app.outCurrencyCodeHidden.val("");
					
					app.outFundCode.val("");
					app.outFundCodeKey.val("");
					app.outProductCode.val("");
					app.outFundCodeDesc.val("");
					
					app.inInvest.removeClass('fieldError');
					app.inFundCode.val("");
					app.inFundCodeKey.val("");
					app.inProductCode.val("");
					app.inFundCodeDesc.val("");
					app.inCurrencyCode.val("");
					app.inCurrencyCodeHidden.val("");
					app.inInvest.val("");
					app.inInvestKey.val("");
					app.inInvestName.val("");
					app.inInvestDesc.val("");
					
					
					app.inFmgrCode.val("");
					app.inFmgrName.val("");
					app.inSaCode.val("");
					app.inSaCodeName.val("");
					app.inCustodianBank.val("");
					
					
				}
				
				
				
			});
			/*
			var urlInvLook = '@{Pick.customerByInvestors()}';
			app.investorNo.lookup({
				list:urlInvLook,
				get:{
					url:'@{Pick.customerByInvestor()}',
					success: function(data) {
						if(data)
						{
							app.investorNo.removeClass('fieldError');
							app.investorNo.val(data.customerNo);
							app.investorId.val(data.customerNo);
							app.investorName.val(data.description);
							app.investorNoKey.val(data.customerKey);
							app.investorNoDesc.val(data.description);
						}
					},
					error : function(data) {
						app.investorNo.addClass('fieldError');
						app.investorNoDesc.val('NOT FOUND');
						app.investorNo.val("");
					}
				},
				description:app.investorNoDesc,
				help:app.investorNoHelp
			});
			*/
		}

		function attachPopupOutInvestment() {
			/*if(outRgProduct)
			{*/
			var investorNoKey = app.investorNoKey.val()!='' ? app.investorNoKey.val() : '-99';
				var urlOutInvLook = '@{Pick.rgInvestmentAcctsByProductCodeAndInvestor()}';
				app.outInvest.lookup({
					list:urlOutInvLook,
					get:{
						url:'@{Pick.rgInvestmentAcctByProductCodeAndInvestor()}',
						success: function(data) {
							if(data)
							{
								app.outInvest.removeClass('fieldError');
								
								outRgInvestment = data;

								app.outInvest.val(outRgInvestment.accountNumber);
								app.outInvestKey.val(outRgInvestment.accountNumber);
								app.outInvestName.val(outRgInvestment.name);
								app.outInvestDesc.val(outRgInvestment.name);
								app.outTaxPct.val(outRgInvestment.rgProduct.taxMasterByRedTaxKey.taxRate);
								app.outSaCode.val(outRgInvestment.thirdPartyBySaCode.thirdPartyCode);
								app.outSaCodeName.val(outRgInvestment.thirdPartyBySaCode.thirdPartyName);
								app.outSaCodeHidden.val(outRgInvestment.thirdPartyBySaCode.thirdPartyCode);
								app.outSaCodeKeyHidden.val(outRgInvestment.thirdPartyBySaCode.thirdPartyKey);
								app.outSaCodeNameHidden.val(outRgInvestment.thirdPartyBySaCode.thirdPartyName);
								app.outCustodianBank.val(outRgInvestment.thirdPartyBySaCode.custodianBank);
								app.outCustodianBankHidden.val(outRgInvestment.thirdPartyBySaCode.custodianBank);
								app.outInvestorBankAccountKey.val(outRgInvestment.bankAccount.bankAccountKey);

								app.outAvailableUnit.val("");
								app.outAvailableUnitStripped.val("");
								
								var aTradeDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', app.tradeDate.val(), null);
								var formattedTradeDate = $.datepicker.formatDate('yymmdd', aTradeDate);
								
								var aPostDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', app.postDate.val(), null);
								var formattedPostDate = $.datepicker.formatDate('yymmdd', aPostDate);

								if(outRgProduct){
								html.getRgPortfolio(outRgProduct.productCode, app.outInvest.val(), formattedPostDate, function(data) {
									if(data)
									{
										outRgPortfolio = data;
										calcRgPortfolio("out");
									}
								});
								}
								attachPopupOutFundAccount();
							}
						},
						error : function(data) {
							app.outInvest.addClass('fieldError');
							app.outInvestDesc.val('NOT FOUND');
							app.outInvest.val("");
						}
					},
					filter : [ app.outFundCode, investorNoKey ],
					help : app.outInvestHelp
				});
			/*}*/
		}

		function attachPopupOutFundAccount() {
			var productCode = app.outFundCode.val().replaceAll(" ", "+");
			productCode = productCode.replace(/#/g, '${specialChar}').toString();
			
			//Fund bank account
			//app.outBankAccountNo.popupFundAccount(productCode, "fund", function(data) {
			app.outBankAccountNo.popupFundAccountDomain(productCode+"|${domainBankRed}", "fund", function(data) {
				app.outBankAccountNo.val(data.bankAccountNo);
				app.outBankAccountKey.val(data.bankAccountKey);
				app.outBankThirdPartyCode.val(data.bankCode.thirdPartyCode);
				app.outBankThirdPartyName.val(data.bankCode.thirdPartyName);
				app.outBankBeneficiaryName.val(data.name );
				app.outBankCurrencyCode.val(data.currency.currencyCode);

				app.outBankAccountNoHidden.val(data.bankAccountNo);
				app.outBankThirdPartyCodeHidden.val(data.bankCode.thirdPartyCode);
				app.outBankThirdPartyNameHidden.val(data.bankCode.thirdPartyName);
				app.outBankBeneficiaryNameHidden.val(data.name);
				app.outBankCurrencyCodeHidden.val(data.currency.currencyCode);
			}, function() {
				app.outBankAccountNo.addClass('fieldError');
				app.outBankBeneficiaryName.val('NOT FOUND');
				app.outBankAccountNo.val("");
				app.outBankAccountKey.val("");
				app.outBankThirdPartyCode.val("");
				app.outBankThirdPartyName.val("");
				app.outBankCurrencyCode.val("");

				app.outBankAccountNoHidden.val("");
				app.outBankThirdPartyCodeHidden.val("");
				app.outBankThirdPartyNameHidden.val("");
				app.outBankBeneficiaryNameHidden.val("");
				app.outBankCurrencyCodeHidden.val("");
			});
		}

		app.outFundCodeThirdParty.lookup({
			list:'@{Pick.thirdParties()}?type=THIRD_PARTY-EXTERNAL_PRODUCT',
			get:{
				url:'@{Pick.thirdParty()}?type=THIRD_PARTY-EXTERNAL_PRODUCT',
				success: function(data) {
					if(data)
					{
						app.outFundCodeThirdParty.removeClass('fieldError');
						app.outFundCodeThirdParty.val(data.name);
						app.outFundCodeKeyThirdPartyHidden.val(data.code);
						app.outFundCodeCodeThirdPartyHidden.val(data.name);
						app.outFundCodeNameThirdPartyHidden.val(data.description);
						app.outFundCodeDescThirdParty.val(data.description);
						app.outCurrencyCodeThirdParty.val(data.currencyCode);
						app.outCurrencyCodeThirdPartyHidden.val(data.currencyCode);
						app.outFmgrCodeThirdParty.val(data.fundManagerCode);
						app.outFmgrNameThirdParty.val(data.fundManagerName);
						app.outFmgrKeyThirdPartyHidden.val(data.fundManagerKey);
						app.outFmgrCodeThirdPartyHidden.val(data.fundManagerCode);
						app.outFmgrNameThirdPartyHidden.val(data.fundManagerName);
						app.outThirdPartyByFundManagerKey.val(data.fundManagerKey);
						app.outThirdPartyByFundManagerCode.val(data.fundManagerCode);
						app.outThirdPartyByFundManagerName.val(data.fundManagerKey);
						app.outCustodianBankThirdParty.val(data.custodianBank);
						app.outCustodianBankThirdPartyHidden.val(data.custodianBank);

						$("#outFundName").html(data.name);

						app.outBankAccountNoExternal.val(data.bankAccountNo);
						app.outBankThirdPartyCodeExternal.val(data.bankCodeThirdPartyCode);
						app.outBankThirdPartyNameExternal.val(data.bankCodeThirdPartyName);
						app.outBankBeneficiaryNameExternal.val(data.bankAccountName );
						app.outBankCurrencyCodeExternal.val(data.bankCurrencyCode);

						app.outBankThirdPartyKeyExternalHidden.val(data.bankThirdPartyBankKey);
						app.outBankAccountNoExternalHidden.val(data.bankAccountNo);
						app.outBankAccountNameExternalHidden.val(data.bankAccountName);
						app.outBankCodeThirdPartyKeyExternalHidden.val(data.bankCodeThirdPartyKey);
						app.outBankCodeThirdPartyCodeExternalHidden.val(data.bankCodeThirdPartyCode);
						app.outBankCodeThirdPartyNameExternalHidden.val(data.bankCodeThirdPartyName);
						app.outBankCurrencyCodeExternalHidden.val(data.bankCurrencyCode);
					}
				},
				error : function(data) {
					app.outFundCodeThirdParty.addClass('fieldError');
					app.outFundCodeDescThirdParty.val('NOT FOUND');
					app.outFundCodeThirdParty.val("");
					app.outFundCodeKeyThirdPartyHidden.val("");
					app.outFundCodeCodeThirdPartyHidden.val("");
					app.outFundCodeNameThirdPartyHidden.val("");
					app.outCurrencyCodeThirdParty.val("");
					app.outCurrencyCodeThirdPartyHidden.val("");
					app.outFmgrCodeThirdParty.val("");
					app.outFmgrNameThirdParty.val("");
					app.outFmgrKeyThirdPartyHidden.val("");
					app.outFmgrCodeThirdPartyHidden.val("");
					app.outFmgrNameThirdPartyHidden.val("");
					app.outCustodianBankThirdParty.val("");
					app.outCustodianBankThirdPartyHidden.val("");

					$("#outFundName").html("");

					app.outBankAccountNoExternal.val("");
					app.outBankThirdPartyCodeExternal.val("");
					app.outBankThirdPartyNameExternal.val("");
					app.outBankBeneficiaryNameExternal.val("");
					app.outBankCurrencyCodeExternal.val("");

					app.outBankThirdPartyKeyExternalHidden.val("");
					app.outBankAccountNoExternalHidden.val("");
					app.outBankAccountNameExternalHidden.val("");
					app.outBankCodeThirdPartyKeyExternalHidden.val("");
					app.outBankCodeThirdPartyCodeExternalHidden.val("");
					app.outBankCodeThirdPartyNameExternalHidden.val("");
					app.outBankCurrencyCodeExternalHidden.val("");
				}
			},
			key: app.outFundCodeKeyThirdParty,
			description: app.outFundCodeDescThirdParty,
			help: app.outFundCodeHelpThirdParty,
			nextControl: app.inFundCode
		});

		app.outBankAccountNoExternal.lookup({
			list:'@{Pick.thirdPartyBanks()}',
			get:{
				url:'@{Pick.thirdPartyBank()}',
				success: function(data) {
					if(data)
					{
						app.outBankAccountNoExternal.val(data.bankAccountNo);
						app.outBankThirdPartyCodeExternal.val(data.bankCodeThirdPartyCode);
						app.outBankThirdPartyNameExternal.val(data.bankCodeThirdPartyName);
						app.outBankBeneficiaryNameExternal.val(data.bankAccountName );
						app.outBankCurrencyCodeExternal.val(data.bankCurrencyCode);

						app.outBankThirdPartyKeyExternalHidden.val(data.bankThirdPartyBankKey);
						app.outBankAccountNoExternalHidden.val(data.bankAccountNo);
						app.outBankAccountNameExternalHidden.val(data.bankAccountName);
						app.outBankCodeThirdPartyKeyExternalHidden.val(data.bankCodeThirdPartyKey);
						app.outBankCodeThirdPartyCodeExternalHidden.val(data.bankCodeThirdPartyCode);
						app.outBankCodeThirdPartyNameExternalHidden.val(data.bankCodeThirdPartyName);
						app.outBankCurrencyCodeExternalHidden.val(data.bankCurrencyCode);
					}
				},
				error : function(data) {
					app.outBankAccountNoExternal.addClass('fieldError');
					app.outBankBeneficiaryNameExternal.val('NOT FOUND');

					app.outBankAccountNoExternal.val("");
					app.outBankThirdPartyCodeExternal.val("");
					app.outBankThirdPartyNameExternal.val("");
					app.outBankCurrencyCodeExternal.val("");

					app.outBankThirdPartyKeyExternalHidden.val("");
					app.outBankAccountNoExternalHidden.val("");
					app.outBankAccountNameExternalHidden.val("");
					app.outBankCodeThirdPartyKeyExternalHidden.val("");
					app.outBankCodeThirdPartyCodeExternalHidden.val("");
					app.outBankCodeThirdPartyNameExternalHidden.val("");
					app.outBankCurrencyCodeExternalHidden.val("");
				}
			},
			filter : [ app.outFundCodeKeyThirdPartyHidden ],
			help: app.outBankAccountNoHelpExternal
		});

		app.outFundCode.popupProductByEffLiqDate("outInvest", function(data) {
			$("#outFundName").text("");
			if(data)
			{
				outRgProduct = data;
				app.outFundCode.val(outRgProduct.productCode);
				app.outProductCode.val(outRgProduct.productCode);
				app.outCurrencyCode.val(outRgProduct.currencyCode);
				app.outCurrencyCodeHidden.val(outRgProduct.currencyCode);
				app.outFmgrCode.val(outRgProduct.thirdPartyByFundManager.thirdPartyCode);
				app.outFmgrName.val(outRgProduct.thirdPartyByFundManager.thirdPartyName);
				app.outThirdPartyByFundManagerKey.val(outRgProduct.thirdPartyByFundManager.thirdPartyKey);
				app.outThirdPartyByFundManagerCode.val(outRgProduct.thirdPartyByFundManager.thirdPartyCode);
				app.outThirdPartyByFundManagerName.val(outRgProduct.thirdPartyByFundManager.thirdPartyName);

				$("#outFundName").html(outRgProduct.description);

				if((app.outFundCode.isChange()) || (app.outFundCode.val() == ""))
				{
					app.outInvest.removeClass('fieldError');
					app.outInvest.val("");
					app.outInvestKey.val("");
					app.outInvestName.val("");
					app.outInvestDesc.val("");
					app.outSaCode.val("");
					app.outSaCodeName.val("");
					app.outSaCodeHidden.val("");
					app.outSaCodeNameHidden.val("");
					app.outCustodianBank.val("");
					app.outInvestorBankAccountKey.val("");

					app.navDate.val("");
					app.postDate.val("");

					//fund bank info				
					app.outBankAccountNo.val("");
					app.outBankAccountKey.val("");
					app.outBankThirdPartyCode.val("");
					app.outBankThirdPartyName.val("");
					app.outBankBeneficiaryName.val("");
					app.outBankCurrencyCode.val("");

					app.outBankAccountNoHidden.val("");
					app.outBankThirdPartyCodeHidden.val("");
					app.outBankThirdPartyNameHidden.val("");
					app.outBankBeneficiaryNameHidden.val("");
					app.outBankCurrencyCodeHidden.val("");
				}

				var aTransactionDate = new Date(html.getApplicationDate());
				if(outRgProduct.transactionDate == 'true' || outRgProduct.transactionDate == true || outRgProduct.transactionDate == '1')
				{
					var aTransactionDateYYYYMMDD = $.datepicker.formatDate("yymmdd", aTransactionDate);
					var maxPaymentDateYYYMMDD = html.getWorkingDate(aTransactionDateYYYYMMDD, -1);
					var dateTrade = $.datepicker.parseDate('yymmdd', maxPaymentDateYYYMMDD, null);
					app.tradeDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', dateTrade));
				}
				else
				{
					app.tradeDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aTransactionDate));
				}			

				getPaymentDate();
				
				app.paymentDate.removeClass('fieldError');
				
				app.goodFundDate.val(app.tradeDate.val());

				calcRgProduct("out");
				app.navDate.change();
				app.effectiveDate.val(outRgProduct.effectiveDate);
				app.outRedLock.val(outRgProduct.redLock);

				outAmountRoundValue = outRgProduct.amountRoundValue;
				outAmountRoundType = outRgProduct.amountRoundType;
				outUnitRoundValue = outRgProduct.unitRoundValue;
				outUnitRoundType = outRgProduct.unitRoundType;
				outPriceRoundValue = outRgProduct.priceRoundValue;
				outPriceRoundType = outRgProduct.priceRoundType;

				app.outFeePct.val(outRgProduct.swiDefaultPct);
				app.outFeePctStripped.val(outRgProduct.swiDefaultPct);
				//console.log("popupFundCode out fee pct = " +app.outFeePct.val());
				//app.outFeePct.change();

				if(outRgProduct.bankAccount)
				{
					app.outBankAccountNo.removeClass('fieldError');
					app.outBankAccountNo.val(outRgProduct.bankAccount.accountNo);
					app.outBankAccountKey.val(outRgProduct.bankAccount.bankAccountKey);
					app.outBankThirdPartyCode.val(outRgProduct.bankAccount.bankCode.thirdPartyCode);
					app.outBankThirdPartyName.val(outRgProduct.bankAccount.bankCode.thirdPartyName);
					app.outBankBeneficiaryName.val(outRgProduct.bankAccount.name);
					app.outBankCurrencyCode.val(outRgProduct.bankAccount.currency.currencyCode);

					app.outBankAccountNoHidden.val(outRgProduct.bankAccount.accountNo);
					app.outBankThirdPartyCodeHidden.val(outRgProduct.bankAccount.bankCode.thirdPartyCode);
					app.outBankThirdPartyNameHidden.val(outRgProduct.bankAccount.bankCode.thirdPartyName);
					app.outBankBeneficiaryNameHidden.val(outRgProduct.bankAccount.name);
					app.outBankCurrencyCodeHidden.val(outRgProduct.bankAccount.currency.currencyCode);
				}

				attachPopupOutInvestment();
				attachPopupOutFundAccount();
			}
		});

		function attachPopupInInvestment() {
			/*if(inRgProduct)
			{*/
				var investorNoKey = app.investorNoKey.val()!='' ? app.investorNoKey.val() : '-99';
				var urlInInvLook = '@{Pick.rgInvestmentAcctsByProductCodeAndInvestor()}';
				app.inInvest.lookup({
					list:urlInInvLook,
					get:{
						url:'@{Pick.rgInvestmentAcctByProductCodeAndInvestor()}',
						success: function(data) {
							if(data)
							{
								app.inInvest.removeClass('fieldError');
								
								inRgInvestment = data;

								app.inInvest.val(inRgInvestment.accountNumber);
								app.inInvestKey.val(inRgInvestment.accountNumber);
								app.inInvestName.val(inRgInvestment.name);
								app.inInvestDesc.val(inRgInvestment.name);
								app.inTaxPct.val(inRgInvestment.rgProduct.taxMasterByRedTaxKey.taxRate);
								app.inSaCode.val(inRgInvestment.thirdPartyBySaCode.thirdPartyCode);
								app.inSaCodeName.val(inRgInvestment.thirdPartyBySaCode.thirdPartyName);
								app.inSaCodeHidden.val(inRgInvestment.thirdPartyBySaCode.thirdPartyCode);
								app.inSaCodeKeyHidden.val(inRgInvestment.thirdPartyBySaCode.thirdPartyKey);
								app.inSaCodeNameHidden.val(inRgInvestment.thirdPartyBySaCode.thirdPartyName);
								app.inCustodianBank.val(inRgInvestment.thirdPartyBySaCode.custodianBank);
								app.inCustodianBankHidden.val(inRgInvestment.thirdPartyBySaCode.custodianBank);
								app.inInvestorBankAccountKey.val(inRgInvestment.bankAccount.bankAccountKey);

								if(inRgInvestment.firstTxn)
								{
									$("input[name='firstSubscribe']")[0].checked = true;
									app.firstSubscribe.val(true);
								}
								else
								{
									$("input[name='firstSubscribe']")[1].checked = true;
									app.firstSubscribe.val(false);
								}

								/*
								var aTradeDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', app.tradeDate.val(), null);
								var formattedTradeDate = $.datepicker.formatDate('yymmdd', aTradeDate);

								html.getRgPortfolio(inRgProduct.productCode, app.inInvest.val(), formattedTradeDate, function(data) {
									if(data)
									{
										inRgPortfolio = data;
										//calcRgPortfolio("in");
									}
								});
								*/

								attachPopupInFundAccount();
							}
						},
						error : function(data) {
							app.inInvest.addClass('fieldError');
							app.inInvestDesc.val('NOT FOUND');
							app.inInvest.val("");
						}
					},
					filter : [ app.inFundCode, investorNoKey ],
					help : app.inInvestHelp
				});		
			/*}*/
		}

		function attachPopupInFundAccount() {
			var productCode = app.inFundCode.val().replaceAll(" ", "+");
			productCode = productCode.replace(/#/g, '${specialChar}').toString();

			//Fund bank account
			//app.inBankAccountNo.popupFundAccount(productCode, "fund", function(data) {
			app.inBankAccountNo.popupFundAccountDomain(productCode+"|${domainBankSub}", "fund", function(data) {
				app.inBankAccountNo.val(data.bankAccountNo);
				app.inBankAccountKey.val(data.bankAccountKey);
				app.inBankThirdPartyCode.val(data.bankCode.thirdPartyCode);
				app.inBankThirdPartyName.val(data.bankCode.thirdPartyName);
				app.inBankBeneficiaryName.val(data.name );
				app.inBankCurrencyCode.val(data.currency.currencyCode);

				app.inBankAccountNoHidden.val(data.bankAccountNo);
				app.inBankThirdPartyCodeHidden.val(data.bankCode.thirdPartyCode);
				app.inBankThirdPartyNameHidden.val(data.bankCode.thirdPartyName);
				app.inBankBeneficiaryNameHidden.val(data.name);
				app.inBankCurrencyCodeHidden.val(data.currency.currencyCode);
			}, function() {
				app.inBankAccountNo.addClass('fieldError');
				app.inBankBeneficiaryName.val('NOT FOUND');
				app.inBankAccountNo.val("");
				app.inBankAccountKey.val("");
				app.inBankThirdPartyCode.val("");
				app.inBankThirdPartyName.val("");
				app.inBankCurrencyCode.val("");

				app.inBankAccountNoHidden.val("");
				app.inBankThirdPartyCodeHidden.val("");
				app.inBankThirdPartyNameHidden.val("");
				app.inBankBeneficiaryNameHidden.val("");
				app.inBankCurrencyCodeHidden.val("");
			});
		}

		app.inFundCodeThirdParty.lookup({
			list:'@{Pick.thirdParties()}?type=THIRD_PARTY-EXTERNAL_PRODUCT',
			get:{
				url:'@{Pick.thirdParty()}?type=THIRD_PARTY-EXTERNAL_PRODUCT',
				success: function(data) {
					if(data)
					{
						app.inFundCodeThirdParty.removeClass('fieldError');
						app.inFundCodeThirdParty.val(data.name);
						app.inFundCodeKeyThirdPartyHidden.val(data.code);
						app.inFundCodeCodeThirdPartyHidden.val(data.name);
						app.inFundCodeNameThirdPartyHidden.val(data.description);
						app.inFundCodeDescThirdParty.val(data.description);
						app.inCurrencyCodeThirdParty.val(data.currencyCode);
						app.inCurrencyCodeThirdPartyHidden.val(data.currencyCode);
						app.inFmgrCodeThirdParty.val(data.fundManagerCode);
						app.inFmgrNameThirdParty.val(data.fundManagerName);
						app.inFmgrKeyThirdPartyHidden.val(data.fundManagerKey);
						app.inFmgrCodeThirdPartyHidden.val(data.fundManagerCode);
						app.inFmgrNameThirdPartyHidden.val(data.fundManagerName);
						app.inThirdPartyByFundManagerKey.val(data.fundManagerKey);
						app.inThirdPartyByFundManagerCode.val(data.fundManagerCode);
						app.inThirdPartyByFundManagerName.val(data.fundManagerKey);
						app.inCustodianBankThirdParty.val(data.custodianBank);
						app.inCustodianBankThirdPartyHidden.val(data.custodianBank);

						$("#inFundName").html(data.name);

						app.inBankAccountNoExternal.val(data.bankAccountNo);
						app.inBankThirdPartyCodeExternal.val(data.bankCodeThirdPartyCode);
						app.inBankThirdPartyNameExternal.val(data.bankCodeThirdPartyName);
						app.inBankBeneficiaryNameExternal.val(data.bankAccountName );
						app.inBankCurrencyCodeExternal.val(data.bankCurrencyCode);

						app.inBankThirdPartyKeyExternalHidden.val(data.bankThirdPartyBankKey);
						app.inBankAccountNoExternalHidden.val(data.bankAccountNo);
						app.inBankAccountNameExternalHidden.val(data.bankAccountName);
						app.inBankCodeThirdPartyKeyExternalHidden.val(data.bankCodeThirdPartyKey);
						app.inBankCodeThirdPartyCodeExternalHidden.val(data.bankCodeThirdPartyCode);
						app.inBankCodeThirdPartyNameExternalHidden.val(data.bankCodeThirdPartyName);
						app.inBankCurrencyCodeExternalHidden.val(data.bankCurrencyCode);
					}
				},
				error : function(data) {
					app.inFundCodeThirdParty.addClass('fieldError');
					app.inFundCodeDescThirdParty.val('NOT FOUND');
					app.inFundCodeThirdParty.val("");
					app.inFundCodeKeyThirdPartyHidden.val("");
					app.inFundCodeCodeThirdPartyHidden.val("");
					app.inFundCodeNameThirdPartyHidden.val("");
					app.inCurrencyCodeThirdParty.val("");
					app.inCurrencyCodeThirdPartyHidden.val("");
					app.inFmgrCodeThirdParty.val("");
					app.inFmgrNameThirdParty.val("");
					app.inFmgrKeyThirdPartyHidden.val("");
					app.inFmgrCodeThirdPartyHidden.val("");
					app.inFmgrNameThirdPartyHidden.val("");
					app.inCustodianBankThirdParty.val("");
					app.inCustodianBankThirdPartyHidden.val("");

					$("#inFundName").html("");

					app.inBankAccountNoExternal.val("");
					app.inBankThirdPartyCodeExternal.val("");
					app.inBankThirdPartyNameExternal.val("");
					app.inBankBeneficiaryNameExternal.val("");
					app.inBankCurrencyCodeExternal.val("");

					app.inBankThirdPartyKeyExternalHidden.val("");
					app.inBankAccountNoExternalHidden.val("");
					app.inBankAccountNameExternalHidden.val("");
					app.inBankCodeThirdPartyKeyExternalHidden.val("");
					app.inBankCodeThirdPartyCodeExternalHidden.val("");
					app.inBankCodeThirdPartyNameExternalHidden.val("");
					app.inBankCurrencyCodeExternalHidden.val("");
				}
			},
			key: app.inFundCodeKeyThirdParty,
			description: app.inFundCodeDescThirdParty,
			help: app.inFundCodeHelpThirdParty
		});

		app.inBankAccountNoExternal.lookup({
			list:'@{Pick.thirdPartyBanks()}',
			get:{
				url:'@{Pick.thirdPartyBank()}',
				success: function(data) {
					if(data)
					{
						app.inBankAccountNoExternal.val(data.bankAccountNo);
						app.inBankThirdPartyCodeExternal.val(data.bankCodeThirdPartyCode);
						app.inBankThirdPartyNameExternal.val(data.bankCodeThirdPartyName);
						app.inBankBeneficiaryNameExternal.val(data.bankAccountName );
						app.inBankCurrencyCodeExternal.val(data.bankCurrencyCode);

						app.inBankThirdPartyKeyExternalHidden.val(data.bankThirdPartyBankKey);
						app.inBankAccountNoExternalHidden.val(data.bankAccountNo);
						app.inBankAccountNameExternalHidden.val(data.bankAccountName);
						app.inBankCodeThirdPartyKeyExternalHidden.val(data.bankCodeThirdPartyKey);
						app.inBankCodeThirdPartyCodeExternalHidden.val(data.bankCodeThirdPartyCode);
						app.inBankCodeThirdPartyNameExternalHidden.val(data.bankCodeThirdPartyName);
						app.inBankCurrencyCodeExternalHidden.val(data.bankCurrencyCode);
					}
				},
				error : function(data) {
					app.inBankAccountNoExternal.addClass('fieldError');
					app.inBankBeneficiaryNameExternal.val('NOT FOUND');

					app.inBankAccountNoExternal.val("");
					app.inBankThirdPartyCodeExternal.val("");
					app.inBankThirdPartyNameExternal.val("");
					app.inBankCurrencyCodeExternal.val("");

					app.inBankThirdPartyKeyExternalHidden.val("");
					app.inBankAccountNoExternalHidden.val("");
					app.inBankAccountNameExternalHidden.val("");
					app.inBankCodeThirdPartyKeyExternalHidden.val("");
					app.inBankCodeThirdPartyCodeExternalHidden.val("");
					app.inBankCodeThirdPartyNameExternalHidden.val("");
					app.inBankCurrencyCodeExternalHidden.val("");
				}
			},
			filter : [ app.inFundCodeKeyThirdPartyHidden ],
			help: app.inBankAccountNoHelpExternal
		});
		
		

		app.inFundCode.popupProductByEffLiqDate("inInvest", function(data) {
			$("#inFundName").text("");
			if(data)
			{
				inRgProduct = data;
				app.inFundCode.val(inRgProduct.productCode);
				app.inProductCode.val(inRgProduct.productCode);
				app.inCurrencyCode.val(inRgProduct.currencyCode);
				app.inCurrencyCodeHidden.val(inRgProduct.currencyCode);
				app.inFmgrCode.val(inRgProduct.thirdPartyByFundManager.thirdPartyCode);
				app.inFmgrName.val(inRgProduct.thirdPartyByFundManager.thirdPartyName);
				app.inThirdPartyByFundManagerKey.val(inRgProduct.thirdPartyByFundManager.thirdPartyKey);
				app.inThirdPartyByFundManagerCode.val(inRgProduct.thirdPartyByFundManager.thirdPartyCode);
				app.inThirdPartyByFundManagerName.val(inRgProduct.thirdPartyByFundManager.thirdPartyName);

				$("#inFundName").html(inRgProduct.description);

				if((app.inFundCode.isChange()) || (app.inFundCode.val() == ""))
				{
					if(app.inFundCode.val() == "")
					{
						app.inCurrencyCode.val("");
						app.inCurrencyCodeHidden.val("");
						app.inFmgrCode.val("");
						app.inFmgrName.val("");
					}

					app.inInvest.removeClass('fieldError');
					app.inInvest.val("");
					app.inInvestKey.val("");
					app.inInvestName.val("");
					app.inInvestDesc.val("");
					app.inSaCode.val("");
					app.inSaCodeName.val("");
					app.inCustodianBank.val("");
					app.inInvestorBankAccountKey.val("");

					if(app.switchTypeList.val() == SWITCH_IN)
					{
						app.navDate.val("");
						app.postDate.val("");
					}

					//fund bank info				
					app.inBankAccountNo.val("");
					app.inBankAccountKey.val("");
					app.inBankThirdPartyCode.val("");
					app.inBankThirdPartyName.val("");
					app.inBankBeneficiaryName.val("");
					app.inBankCurrencyCode.val("");

					app.inBankAccountNoHidden.val("");
					app.inBankThirdPartyCodeHidden.val("");
					app.inBankThirdPartyNameHidden.val("");
					app.inBankBeneficiaryNameHidden.val("");
					app.inBankCurrencyCodeHidden.val("");
				}

				if(app.switchTypeList.val() == SWITCH_IN)
				{
					var aTransactionDate = new Date(html.getApplicationDate());
					if(inRgProduct.transactionDate == 'true' || inRgProduct.transactionDate == true || inRgProduct.transactionDate == '1')
					{
						var aTransactionDateYYYYMMDD = $.datepicker.formatDate("yymmdd", aTransactionDate);
						var maxPaymentDateYYYMMDD = html.getWorkingDate(aTransactionDateYYYYMMDD, -1);
						var dateTrade = $.datepicker.parseDate('yymmdd', maxPaymentDateYYYMMDD, null);
						app.tradeDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', dateTrade));
					}
					else
					{
						app.tradeDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aTransactionDate));
					}

					app.goodFundDate.value(app.tradeDate.val());
	
					calcRgProduct("in");
					app.navDate.change();
					app.effectiveDate.val(inRgProduct.effectiveDate);
				}

				app.inSubLock.val(inRgProduct.subLock);

				inAmountRoundValue = inRgProduct.amountRoundValue;
				inAmountRoundType = inRgProduct.amountRoundType;
				inUnitRoundValue = inRgProduct.unitRoundValue;
				inUnitRoundType = inRgProduct.unitRoundType;
				inPriceRoundValue = inRgProduct.priceRoundValue;
				inPriceRoundType = inRgProduct.priceRoundType;

				/*app.inFeePct.val(inRgProduct.swiDefaultPct);
				app.inFeePctStripped.val(inRgProduct.swiDefaultPct);*/
				//app.inFeePct.change();

				if(inRgProduct.bankAccountSub)
				{
					app.inBankAccountNo.removeClass('fieldError');
					app.inBankAccountNo.val(inRgProduct.bankAccountSub.accountNo);
					app.inBankAccountKey.val(inRgProduct.bankAccountSub.bankAccountKey);
					app.inBankThirdPartyCode.val(inRgProduct.bankAccountSub.bankCode.thirdPartyCode);
					app.inBankThirdPartyName.val(inRgProduct.bankAccountSub.bankCode.thirdPartyName);
					app.inBankBeneficiaryName.val(inRgProduct.bankAccountSub.name);
					app.inBankCurrencyCode.val(inRgProduct.bankAccountSub.currency.currencyCode);

					app.inBankAccountNoHidden.val(inRgProduct.bankAccountSub.accountNo);
					app.inBankThirdPartyCodeHidden.val(inRgProduct.bankAccountSub.bankCode.thirdPartyCode);
					app.inBankThirdPartyNameHidden.val(inRgProduct.bankAccountSub.bankCode.thirdPartyName);
					app.inBankBeneficiaryNameHidden.val(inRgProduct.bankAccountSub.name);
					app.inBankCurrencyCodeHidden.val(inRgProduct.bankAccountSub.currency.currencyCode);
				}

				attachPopupInInvestment();
				attachPopupInFundAccount();
				app.navDate.change();
				getPaymentDate();
			}
		});
 
		function checkOutFeeBy() {
			console.log("checkcoutFeeBy");
			if( outAmountRoundValue == "undefined" ||  outAmountRoundType == "typeUnit")
				getVarGlobal();
			if(app.outFeeBy1.attr("disabled") != true)
			{
				if(app.outFeeBy1.attr("checked") == true)
				{
					app.outFeePct.removeAttr("disabled");
					app.outFeeAmt.attr("disabled", true);
					app.outFeeAmt.valueRnd("", true, outAmountRoundValue, outAmountRoundType);
					app.outFeeAmt.val("");
					app.outFeeAmtStripped.val("");
					//app.outFeePct.val("0");
					//app.outFeePctStripped.val("0");
					app.outFeePct.change();
				}
			}

			if(app.outFeeBy2.attr("disabled") != true)
			{
				if(app.outFeeBy2.attr("checked") == true)
				{
					app.outFeeAmt.removeAttr("disabled");
//					app.outFeeAmt.val("0");
//					app.outFeeAmtStripped.val("0");
					
					if('${haserror}' != 'haserror' && app.confirming.val() != "true"){
						app.outFeePct.attr("disabled", true);
						app.outFeePct.val("");
						app.outFeePctStripped.val("");
					}
					
				}
			}
		}

		/*
		function checkInFeeBy() {
			if(app.inFeeBy1.attr("disabled") != true)
			{
				if(app.inFeeBy1.attr("checked") == true)
				{
					app.inFeePct.removeAttr("disabled");
					app.inFeeAmt.attr("disabled", true);
					app.inFeeAmt.val("");
					app.inFeeAmtStripped.val("");
					app.inFeePct.val("0");
					app.inFeePctStripped.val("0");
				}
			}

			if(app.inFeeBy2.attr("disabled") != true)
			{
				if(app.inFeeBy2.attr("checked") == true)
				{
					app.inFeeAmt.removeAttr("disabled");
					app.inFeePct.attr("disabled", true);
					app.inFeePct.val("");
					app.inFeePctStripped.val("");
				}
			}
		}
		 */

		function calcRgPortfolio(state) {
			console.log("calcRgPortfolio");
			if(state == "out")
			{
				if(outRgPortfolio)
				{
					var tmpUnit = (outRgPortfolio.unit ? outRgPortfolio.unit : 0);
					if(app.isApproval.val() != "true"){
						app.outAvailableUnit.valueRnd((tmpUnit), true, outUnitRoundValue, outUnitRoundType);
						app.outAvailableUnitStripped.value(Number(app.outAvailableUnit.autoNumericGet()));
					} else{
						app.outAvailableUnit.valueRnd((app.outAvailableUnitStripped.val()), true, outUnitRoundValue, outUnitRoundType);
					}
					app.outAvailBalance.value(outRgPortfolio.balanceAmount, true);
				}
			}

			if(state == "in")
			{
				//subscription tidak ada calcRgPortfolio
			}
		}

		function calcFeeAmt(state) {
			console.log("CalcFeeAmt");
			if( outAmountRoundValue == "undefined" ||  outAmountRoundType == "typeUnit")
				getVarGlobal();
			if(state == "out")
			{
				if(app.outFeeBy1.attr("checked") == true)
				{
					//console.log("1) out fee amout = " +app.outFeeAmt.val());
					app.outFeeAmt.calcRedFeeAmount(app.outNetAmt, app.outFeePct, app.outTradeBy, outAmountRoundValue, outAmountRoundType);
					if(Number(app.outFeeAmt.val()) == 0)
					{
						app.outFeeAmtStripped.val("");
					}
					//console.log("2) out fee amout = " +app.outFeeAmt.val());
					
				}
			}

			if(state == "in")
			{
				if(inRgProduct != null)
				{
					var subTierType = inRgProduct.lookupBySubTierType.lookupId;
					var amountTag = getInputBySwitchInAmountTag();
					//Checking flat or progressive
					//var feeAmount = app.inFeeAmt.val();
					/*if (feeAmount != ''){
						app.inFeeAmt.valueRnd(feeAmount, true, inAmountRoundValue, inAmountRoundType);
						//app.inFeeAmtStripped.val(app.inFeeAmt.val().toNumber(","));
					} else {*/
					
					if($('#inSwtFee').val() == 'true' ) {
						if(subTierType == TIER_COMPARISON_TYPE_FLAT)
						{
							app.inFeeAmt.calcSubFeeAmount(amountTag, app.inFeePct, app.inTradeBy, inAmountRoundValue, inAmountRoundType);
						}
						else
						{
							if(app.inFeePct.val() != '')
							{
								app.inFeeAmt.calcSubFeeAmount(amountTag, app.inFeePct, app.inTradeBy, inAmountRoundValue, inAmountRoundType);
							}
						}
					}
					
				}
			}
		}

		function calcDiscAmt(state) {
			console.log("calcDiscAmt");
			if( outAmountRoundValue == "undefined" ||  outAmountRoundType == "typeUnit")
				getVarGlobal();
			if(state == "out")
			{
				app.outDiscAmt.calcRedDiscAmount(app.outFeeAmt, app.outDiscPct, outAmountRoundValue, outAmountRoundType);
			}

			if(state == "in")
			{
				//subscription tidak ada calcDiscAmt
			}
		}

		function calcOtherAmt(state) {
			console.log("calcOtherAmt");
			if(state == "out")
			{
				app.outOtherAmt.calcRedOtherAmount(app.outNetAmt, app.outOtherPct, outAmountRoundValue, outAmountRoundType);
			}

			if(state == "in")
			{
				var amountTag = getInputBySwitchInAmountTag();
				app.inOtherAmt.calcSubOtherAmount(amountTag, app.inOtherPct, app.inTradeBy, inAmountRoundValue, inAmountRoundType);
			}
		}

		function calcTaxAmt(state) {
			console.log("calcTaxAmt");
			if(outAmountRoundValue == "undefined" || outAmountRoundType == "undefined" || inAmountRoundValue == "undefined" || inAmountRoundType  == "undefined")
				getVarGlobal();
			if(state == "out")
			{
				app.outTaxAmt.calcRedTaxFeeAmount(app.outTaxPct, app.outFeeAmt, app.outDiscAmt, app.outOtherAmt, outAmountRoundValue, outAmountRoundType);
			}

			if(state == "in")
			{
				app.inTaxAmt.calcSubTaxFeeAmount(app.inTaxPct, app.inFeeAmt, app.inDiscAmt, app.inOtherAmt, inAmountRoundValue, inAmountRoundType);
			}
		}

		function calcTotFeeAmt(state) {
			console.log("calcTotFeeAmt");
			if(outAmountRoundValue == "undefined" || outAmountRoundType == "undefined" || inAmountRoundValue == "undefined" || inAmountRoundType  == "undefined")
				getVarGlobal();
			if(state == "out")
			{
				app.outTotFeeAmt.calcRedTotalFeeAmount(app.outFeeAmt, app.outDiscAmt, app.outOtherAmt, app.outTaxAmt, outAmountRoundValue, outAmountRoundType);
			}

			if(state == "in")
			{
				app.inTotFeeAmt.calcSubTotalFeeAmount(app.inFeeAmt, app.inDiscAmt, app.inOtherAmt, app.inTaxAmt, inAmountRoundValue, inAmountRoundType);
			}
		}

		function calcAmount(state) {
			console.log("calcAmount");
			if(outAmountRoundValue == "undefined" || outAmountRoundType == "undefined")
				getVarGlobal();
			if(state == "out")
			{
				if(app.outFeeAmt.isEmpty())
				{
					app.outFeeAmt.val(0);
				}

				app.outAmount.calcRedAmount(app.outFeeAmt, app.outNetAmt, app.outTotFeeAmt, outAmountRoundValue, outAmountRoundType);
				if(app.outNetAmt.isEmpty())
				{
					app.outAmount.val('');
					app.outAmountStripped.val('');
				}
			}

			if(state == "in")
			{
				//subscription tidak ada calcAmount
			}
		}

		function calcUnit(state) {
			console.log("calcUnit");
			if(outUnitRoundValue == "undefined" || outUnitRoundType == "undefined" || inUnitRoundValue == "undefined" || inUnitRoundType  == "undefined")
				getVarGlobal();
			if(state == "out")
			{
				app.outUnit.calcRedUnit(app.outNetAmt, app.outPrice, app.outTradeBy, outUnitRoundValue, outUnitRoundType);
			}

			if(state == "in")
			{
				app.inUnit.calcSubUnit(app.inNetAmt, app.inPrice, null, inUnitRoundValue, inUnitRoundType);
			}
		}

		function calcNetAmount(state) {
			console.log("calcNetAmount");
			if( outAmountRoundValue == "undefined" ||  outAmountRoundType == "typeUnit" || inAmountRoundValue == "typeUnit" || inAmountRoundType == "typeUnit")
				getVarGlobal();
			if(state == "out")
			{
				app.outNetAmt.calcRedNetAmount(app.outPrice, app.outUnitStripped, app.outTradeBy, outAmountRoundValue, outAmountRoundType);
			}

			if(state == "in")
			{
				var amountTag = getInputBySwitchInAmountTag();
				if(app.inTradeBy.val() == INPUT_BY_GROSS)
				{
					app.inNetAmt.calcSubNetAmount(amountTag, app.inTotFeeAmt, app.inTradeBy, inAmountRoundValue, inAmountRoundType);
				}
				if(app.inTradeBy.val() == INPUT_BY_NET)
				{
					app.inAmt.calcSubAmount(amountTag, app.inTotFeeAmt, app.inTradeBy, inAmountRoundValue, inAmountRoundType);
				}
			}
		}

		function calcCapGainTaxAmt(state) {
			console.log("calcCapGainTaxAmt");
			if( outAmountRoundValue == "undefined" ||  outAmountRoundType == "typeUnit")
				getVarGlobal();
			if(state == "out")
			{
				if(outRgTax)
				{
					app.outCapGainTaxAmount.calcRedCapGainTaxAmount(Number(outRgTax.taxRate), app.outNetAmt, outAmountRoundValue, outAmountRoundType);
					if(app.outAmount.val() == 0)
					{
						app.outCapGainTaxAmount.val(0);
					}
				}
			}

			if(state == "in")
			{
				//subscription tidak ada calcCapGainTaxAmt
			}
		}
		
		function forceAmountGross() {
			console.log("forceAmountGross");
			if(app.switchTypeList.val() == SWITCH_IN_OUT){
				app.inAmt.val(app.outAmount.val());
				app.inAmtStripped.val(app.inAmt.val());
				app.inAmt.attr("disabled", "disabled");
				app.inAmt.change();
			}
		}

		function calcRgTax(state) {
			if( outAmountRoundValue == "undefined" ||  outAmountRoundType == "typeUnit")
				getVarGlobal();
			if(state == "out")
			{
				if(outRgTax)
				{
					app.outPayAmt.calcRedPayment(Number(outRgTax.taxRate), app.outNetAmt, app.outAmount, outAmountRoundValue, outAmountRoundType);
					if(app.outAmount.val() == 0)
					{
						app.outPayAmt.val(0);
					}
				}				
			}

			if(state == "in")
			{
				//subscription tidak ada calcRgTax
			}
			forceAmountGross();
		}

		function getInputBySwitchInAmount() {
			var amount;
			if(app.inTradeBy.val() == INPUT_BY_GROSS)
			{
				amount = app.inAmt.val().toNumber(",");
			}

			if(app.inTradeBy.val() == INPUT_BY_NET)
			{
				amount = app.inNetAmt.val().toNumber(",");
			}
			return amount;
		}

		function getInputBySwitchInAmountTag() {
			var amountTag;
			if(app.inTradeBy.val() == INPUT_BY_GROSS)
			{
				amountTag = app.inAmt;
			}

			if(app.inTradeBy.val() == INPUT_BY_NET)
			{
				amountTag = app.inNetAmt;
			}

			return amountTag;
		}

		function calcRgProduct(state) {
			var goodFundDate = app.goodFundDate.val().fmtYYYYMMDD("/");

			if(state == "out")
			{
				//var aDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', app.goodFundDate.val(), null);
				//var goodFundDate = $.datepicker.formatDate("yymmdd", aDate);
				var newGoodFundDate = html.addCutOfTime(goodFundDate, (outRgProduct.redCot? outRgProduct.redCot : 0)) + "";
				if(goodFundDate != newGoodFundDate)
				{
					goodFundDate = newGoodFundDate;
					aDate = $.datepicker.parseDate('yymmdd', goodFundDate, null);
					app.goodFundDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aDate));
				}

				var navDate = html.getWorkingDate(goodFundDate, (outRgProduct.redNavUsed ? outRgProduct.redNavUsed : 0 )) + "";
				var aNavDate = $.datepicker.parseDate('yymmdd', navDate, null);
				var postDate = html.getWorkingDate(goodFundDate, (outRgProduct.redPostPeriod ? outRgProduct.redPostPeriod : 0 )) + "";
				var aPostDate = $.datepicker.parseDate('yymmdd', postDate, null);

				app.navDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aNavDate));
				app.postDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', aPostDate));
				app.outTaxPct.value(outRgProduct.taxMasterByRedTaxKey.taxRate, true);
			}

			if(state == "in")
			{
				var newGoodFundDate = html.addCutOfTime(goodFundDate, inRgProduct.subCot) + "";
				if(goodFundDate != newGoodFundDate)
				{
					goodFundDate = newGoodFundDate;
					app.goodFundDate.val(goodFundDate.toMMDDYYYY("/"));
				}

				var navDate = html.getWorkingDate(goodFundDate, inRgProduct.subNavUsed) + "";
				var postDate = html.getWorkingDate(goodFundDate, inRgProduct.subPostPeriod) + "";

				app.navDate.val(navDate.toMMDDYYYY("/"));
				app.postDate.val(postDate.toMMDDYYYY("/"));
			}
		}

		function viewTradeBy(state) {
			if(state == "out")
			{
				var con = app.outType.checked();
				var disabled = app.outTradeBy.attr("disabled");

				if(app.outTradeBy.val() == TRANSACTION_BY_AMOUNT)
				{
					app.OUTBYAMOUNT.append(app.pOutPrice.load()).append(app.pOutUnit.load());

					app.outUnit.disabled();
					app.outNetAmt.enabled();

					$("#tab-1 p[id=pOutUnit] label span").html("");
					$("#tab-1 p[id=pOutAmount] label span").html("*");
					
					if(app.switchTypeList.val() == SWITCH_IN)
					{
						$("#tab-1 p[id=pOutUnit] label span").html("");
						$("#tab-1 p[id=pOutAmount] label span").html("");
					}

					if(disabled)
					{
						app.outUnit.disabled();
						app.outNetAmt.disabled();
					}
				}

				if(app.outTradeBy.val() == TRANSACTION_BY_UNIT)
				{
					app.OUTBYUNIT.append(app.pOutUnit.load()).append(app.pOutPrice.load());

					app.outUnit.enabled();
					app.outNetAmt.disabled();

					$("#tab-1 p[id=pOutUnit] label span").html("*");
					$("#tab-1 p[id=pOutAmount] label span").html("");

					if(app.switchTypeList.val() == SWITCH_IN)
					{
						$("#tab-1 p[id=pOutUnit] label span").html("");
						$("#tab-1 p[id=pOutAmount] label span").html("");
					}

					if(disabled)
					{
						app.outUnit.disabled();
					}

					if(con == 'true')
					{
						app.outUnit.disabled();
						app.outTradeBy.disabled();
					}
				}
				
				var oFeeAmount = app.outFeeAmt.val();
				if (oFeeAmount != ''){
					app.outFeeAmt.valueRnd(app.outFeeAmtStripped.val(), true, outAmountRoundValue, outAmountRoundType);
				}
			}

			if(state == "in")
			{
				var disabled = app.inTradeBy.attr("disabled");

				if(app.inTradeBy.val() == INPUT_BY_GROSS)
				{
					app.INBYGROSS.append(app.pInAmount.load());
					app.INBYNET.append(app.pInNetAmount.load());

					app.inAmt.removeAttr("disabled");
					app.inNetAmt.attr("disabled", "disabled");

					if(disabled)
					{
						app.inAmt.attr("disabled", "disabled");
					}
					
					//$("#tab-1 p[id=pInAmount] label span").html("*");
					if(app.switchTypeList.val() != SWITCH_IN_OUT && app.switchTypeList.val() != "")
					{
						$("#tab-1 p[id=pInAmount] label span").html("*");
						if(('${mode}' != 'view') && ('${confirming}' != 'true')){
							app.inAmt.removeAttr("disabled");
						}
					}
					else
					{
						$("#tab-1 p[id=pInAmount] label span").html("");
					}

					$("#tab-1 p[id=pInNetAmount] label span").html("");

					if(app.switchTypeList.val() == SWITCH_OUT)
					{
						$("#tab-1 p[id=pInAmount] label span").html("");
						$("#tab-1 p[id=pInNetAmount] label span").html("");
					}
					
				}

				if(app.inTradeBy.val() == INPUT_BY_NET)
				{
					app.INBYGROSS.append(app.pInNetAmount.load());
					app.INBYNET.append(app.pInAmount.load());

					$("#tab-1 p[id=pInNetAmount] label span").html("*");
					$("#tab-1 p[id=pInAmount] label span").html("");

					app.inNetAmt.removeAttr("disabled");
					app.inAmt.attr("disabled", "disabled");

					if(app.switchTypeList.val() == SWITCH_OUT)
					{
						$("#tab-1 p[id=pInAmount] label span").html("");
						$("#tab-1 p[id=pInNetAmount] label span").html("");
					}

					if(disabled)
					{
						app.inNetAmt.attr("disabled", "disabled");
					}
				}
				var feeAmount = app.inFeeAmt.val();
				var inNAmount = app.inNetAmt.val();
				var inPric = app.inPrice.val();
				if (feeAmount != ''){
					app.inFeeAmt.valueRnd(app.inFeeAmtStripped.val(), true, inAmountRoundValue, inAmountRoundType);
				}
				if (inNAmount != ''){
					app.inNetAmt.valueRnd(app.inNetAmtStripped.val(), true, inAmountRoundValue, inAmountRoundType);
				}
				if (inPric != ''){
					app.inPrice.valueRnd(app.inPriceStripped.val(), true, inPriceRoundValue, inPriceRoundType);
				}
			}
		}

		/*
		app.outFeePct.autoNumeric({vMax: '100'});
		app.outFeePct.live('blur', function() {
			var el = $(this);
			if(el.val() == '')
			{
				return;
			}
		});

		app.inFeePct.autoNumeric({vMax: '100'});
		app.inFeePct.live('blur', function() {
			var el = $(this);
			if(el.val() == '')
			{
				return;
			}
		});
		 */

		function getPrice(outRgProduct) {	
			var aNavDate = app.navDate.datepicker("getDate");
			var formattedNavDate = $.datepicker.formatDate('yymmdd', aNavDate);
			getVarGlobal();
			//Set price with nav, only when fix nav is not set
			if(outRgProduct.fixnav == false)
			{
				outRgNav = html.getRgNav(formattedNavDate, app.outFundCode.val());
				if(outRgNav)
				{
					if(typeof outRgNav.bidPct === "undefined")
					{ 
						var bidPrice = outRgNav.nav;
					}
					else
					{
						var bidPrice = (1+((outRgNav.bidPct)/100))*rgNav.nav;
					}
					app.outPrice.valueRnd(bidPrice, true, outPriceRoundValue, outPriceRoundType);
				}
			}
			else if(outRgProduct.fixnav == true)
			{
				app.outPrice.value(outRgProduct.fixNavPrice);
			}
		};

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.outFundCode.blur(function(){
			if((app.outFundCode.isChange()) || (app.outFundCode.val() == ""))
			{
				app.outInvest.removeClass('fieldError');
				app.outInvest.val("");
				app.outInvestKey.val("");
				app.outInvestName.val("");
				app.outInvestDesc.value("");
				app.navDate.val("");
				app.postDate.val("");
				app.outAvailableUnit.val("");
				app.outAvailBalance.val("");
				app.outNetAmt.val("");
				app.outTaxPct.val("");
				app.outPrice.val("");

				app.outSaCode.value("");
				app.outSaCodeName.value("");
				app.outSaCodeHidden.value("");
				app.outSaCodeKeyHidden.value("");
				app.outSaCodeNameHidden.value("");

				//Fund bank info				
				app.outBankAccountNo.value("");
				app.outBankAccountKey.value("");
				app.outBankAccountNoHidden.value("");
				app.outBankThirdPartyCodeHidden.value("");
				app.outBankThirdPartyNameHidden.value("");
				app.outBankBeneficiaryNameHidden.value("");
				app.outBankCurrencyCodeHidden.value("");
				app.outBankThirdPartyCode.value("");
				app.outBankThirdPartyName.value("");
				app.outBankBeneficiaryName.value("");
				app.outBankCurrencyCode.value("");
			}
		});

		app.switchTypeList.change(function() {
			viewBySwitchTypeList(true);
//			getPaymentDate();
		});

		app.outFundCode.bind("keyup change", function() {
			app.outProductCode.val("");
			//app.outFundCode.val("");
			//app.outFundCodeDesc.val("");
			app.outThirdPartyByFundManagerKey.val("");
			app.outThirdPartyByFundManagerCode.val("");
			app.outThirdPartyByFundManagerName.val("");
			app.outFmgrName.val("");
			app.outCurrencyCode.val("");
			app.outCurrencyCodeHidden.val("");
			app.outFmgrCode.val("");
			app.outFmgrName.val("");
		});

		app.inFundCode.bind("keyup change", function() {
			app.inProductCode.val("");
			//app.inFundCode.val("");
			//app.inFundCodeDesc.val("");
			app.inThirdPartyByFundManagerKey.val("");
			app.inThirdPartyByFundManagerCode.val("");
			app.inThirdPartyByFundManagerName.val("");
			app.inFmgrName.val("");
			app.inCurrencyCode.val("");
			app.inCurrencyCodeHidden.val("");
			app.inFmgrCode.val("");
			app.inFmgrName.val("");
		});

		app.outFundCodeThirdParty.bind("keyup change", function() {
			app.outFundCodeKeyThirdPartyHidden.val("");
			app.outCurrencyCodeThirdParty.val("");
			app.outCurrencyCodeThirdPartyHidden.val("");
			app.outFmgrCodeThirdParty.val("");
			app.outFmgrNameThirdParty.val("");
			app.outFmgrKeyThirdPartyHidden.val("");
			app.outCustodianBankThirdParty.val("");
			app.outCustodianBankThirdPartyHidden.val("");
		});

		app.inFundCodeThirdParty.bind("keyup change", function() {
			app.inFundCodeKeyThirdPartyHidden.val("");
			app.inCurrencyCodeThirdParty.val("");
			app.inCurrencyCodeThirdPartyHidden.val("");
			app.inFmgrCodeThirdParty.val("");
			app.inFmgrNameThirdParty.val("");
			app.inFmgrKeyThirdPartyHidden.val("");
			app.inCustodianBankThirdParty.val("");
			app.inCustodianBankThirdPartyHidden.val("");
		});

		app.tradeDate.change(function() {
			app.goodFundDate.val(app.tradeDate.val());

			if((app.switchTypeList.val() == SWITCH_IN_OUT) || (app.switchTypeList.val() == SWITCH_OUT) || (app.switchTypeList.isEmpty()))
			{
				if(outRgProduct != null)
				{
					calcRgProduct("out");
				}

				if(inRgProduct != null)
				{
					calcRgProduct("in");
				}
			}

			if((app.switchTypeList.val() == SWITCH_IN))
			{
				calcRgProduct("in");
			}

			var aTradeDate = $.datepicker.parseDate( '${appProp?.jqueryDateFormat}', app.tradeDate.val(), null );
			var formattedTradeDate = $.datepicker.formatDate('yymmdd', aTradeDate);
			
			var aPostDate = $.datepicker.parseDate( '${appProp?.jqueryDateFormat}', app.postDate.val(), null );
			var formattedPostDate = $.datepicker.formatDate('yymmdd', aPostDate);

			if((app.switchTypeList.val() == SWITCH_IN_OUT) || (app.switchTypeList.val() == SWITCH_OUT))
			{
				html.getRgPortfolio(outRgProduct.productCode, app.outInvest.val(), formattedPostDate, function(data) {
					if(data)
					{
						outRgPortfolio = data;
						calcRgPortfolio("out");
					}
					else
					{
						app.outAvailableUnit.val("");
						app.outAvailableUnitStripped.val("");
					}
				});
			}

			app.navDate.change();
			
			getPaymentDate();
		});

		app.postDate.change(function(){
			
			
			var aPostDate = $.datepicker.parseDate( '${appProp?.jqueryDateFormat}', app.postDate.val(), null );
			var formattedPostDate = $.datepicker.formatDate('yymmdd', aPostDate);
			
			if((app.switchTypeList.val() == SWITCH_IN_OUT) || (app.switchTypeList.val() == SWITCH_OUT))
			{
				html.getRgPortfolio(outRgProduct.productCode, app.outInvest.val(), formattedPostDate, function(data) {
					if(data)
					{
						outRgPortfolio = data;
						calcRgPortfolio("out");
					}
					else
					{
						app.outAvailableUnit.val("");
						app.outAvailableUnitStripped.val("");
					}
				});
			}
			
			//red.navDate.change();
		});
		app.navDate.change(function() {
			if(outRgProduct != null)
			{
				if(!app.outFundCode.isEmpty() && !app.navDate.isEmpty())
				{
					if(!outRgProduct.fixnav)
					{
						outRgProduct.fixnav = false;
					}
	
					app.outPrice.value("");
					getPrice(outRgProduct);
					app.outPrice.change();
				}
			}

			setPriceUnitIn();
		});

		app.goodFundDate.change(function() {
			if((app.switchTypeList.val() == SWITCH_IN_OUT) || (app.switchTypeList.val() == SWITCH_OUT))
			{
				calcRgProduct("out");
			}

			if(app.switchTypeList.val() == SWITCH_IN)
			{
				calcRgProduct("in");
			}
		});

		app.outType.change(function() {
			var con = app.outType.checked();
			if(con == 'true')
			{
				app.outTradeBy.val(TRANSACTION_BY_UNIT).change();
				app.outTradeBy.disabled();
				app.outUnit.disabled();

				var outAvailableUnit = (app.h_outAvailableUnit.val() ? app.h_outAvailableUnit.val() : 0);
				app.outUnit.valueRnd(outAvailableUnit, true, outUnitRoundValue, outUnitRoundType);
				app.outUnitStripped.val(app.h_outAvailableUnit.val());
				
				calcNetAmount("out");
			}

			if(con == 'false')
			{
				app.outTradeBy.enabled();
				app.outUnit.enabled();
			}

			app.liquidation.val(con);
		});

		app.outFeeBy1.change(function() {
			checkOutFeeBy();
		});

		app.outFeeBy2.change(function() {
			checkOutFeeBy();
		});

		/*
		app.inFeeBy1.change(function() {
			checkInFeeBy();
		});

		app.inFeeBy2.change(function() {
			checkInFeeBy();
		});
		*/

		app.outNetAmt.change(function() {
			var value = app.outNetAmt.val();
			app.outNetAmt.valueRnd(value.toNumber(","), true, outAmountRoundValue, outAmountRoundType);
			if((app.outNetAmt.val() == "") || (app.outNetAmt.val() == 0))
			{
				app.outFeePct.val("");
			}
			else
			{
				console.log("on change net amount");
				rgFeeTiers(true, "out");
			}

			calcFeeAmt("out");
			//calcDiscAmt("out");
			calcTaxAmt("out");
			calcOtherAmt("out");

			calcTotFeeAmt("out");
			calcAmount("out");
			calcUnit("out");
			calcCapGainTaxAmt("out");
			calcRgTax("out");

//			if(inRgProduct != null)
//			{
//				app.navDate.change();
//			}
		});

		app.outFeeAmt.change(function(){
			console.log("out fee amount on change");
			var value = app.outFeeAmt.val();
			app.outFeeAmt.valueRnd(value.toNumber(","), true, outAmountRoundValue, outAmountRoundType);
			app.outFeePct.val("");

			//calcDiscAmt("out");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcRgTax("out");

			if(value == "")
			{
				app.outTaxPct.val('');
				app.outTaxPctStripped.val('');
				app.outTaxAmt.val(0);
				app.outTaxAmtStripped.val(0);
				app.outAmount.val(0);
				app.outAmountStripped.val(0);
				app.outCapGainTaxAmount.val(0);
				app.outPayAmt.val(0);
				app.outPayAmtStripped.val(0);
			}
		});

		app.outUnit.change(function() {
			var value = app.outUnit.val();
			app.outUnit.valueRnd(value.toNumber(","), true, outUnitRoundValue, outUnitRoundType);
			calcNetAmount("out");
			calcFeeAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcCapGainTaxAmt("out");
			calcRgTax("out");
		});

		app.outPrice.change(function() {
			var value = app.outPrice.val();
			app.outPrice.removeClass('fieldError');
			app.errOutPrice.html("");
			if(value != "")
			{
				app.outPrice.valueRnd(value.toNumber(","), true, outPriceRoundValue, outPriceRoundType);
				calcNetAmount("out");

				if(app.outPrice.val() == 0)
				{
					//app.outPrice.addClass('fieldError');
					//app.errOutPrice.html("can not filled 0");
				}

				if((app.outNetAmt.val() == "") || (app.outNetAmt.val() == 0))
				{
					//app.outFeePct.val("");
				}
				else
				{
					rgFeeTiers(true, "out");
				};
				calcFeeAmt("out");
				//calcDiscAmt("out");
				calcTaxAmt("out");
				calcOtherAmt("out");
				calcTotFeeAmt("out");
				calcCapGainTaxAmt("out");
				calcRgTax("out");
			} else {
				
				if(app.dispatch.val() != 'edit') {
					app.outUnit.valueRnd(0, true, outPriceRoundValue, outPriceRoundType);
					app.outNetAmt.val('');
					app.outNetAmtStripped.val('');
					app.outTotFeeAmt.value(0);
					app.outAmount.value("");
					app.outCapGainTaxAmount.val(0);
					app.outPayAmt.val(0);
					
					if(app.outFeeBy1.attr("checked") == true) {
						app.outFeeAmt.value(0);
					}
				}
				
			}
		});

		app.outFeePct.change(function()
		{
			//app.outFeeAmt.val('');
			//app.outFeeAmtStripped.val('');
			
			if(app.outNetAmt.val() != "")
			{
				if(app.outNetAmt.val() == 0)
				{
					if ($("#outFeeBy1").attr("checked") == true) {
						app.outFeeAmt.val('');
						app.outFeeAmtStripped.val('');
					}
				}
				else
				{
					calcFeeAmt("out");
				}
			} else {
				if ($("#outFeeBy1").attr("checked") == true) {
					app.outFeeAmt.val('');
					app.outFeeAmtStripped.val('');
				}
			}

			//calcDiscAmt("out");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcRgTax("out");

			if(app.outFeePct.val() == "")
			{
				app.outTaxPct.val('');
				app.outTaxAmt.val(0);
				app.outAmount.val(0);
				app.outCapGainTaxAmount.val(0);
			}
//			console.log("on change fee pct , out fee pct = " +app.outFeePct.val());
			app.outFeePctStripped.val(app.outFeePct.val());
			if(app.outFeePct.val() == 0)
			{
				app.outFeePctStripped.val("");
			}
			
//			console.log("on change fee pct , out fee pct strip= " +app.outFeePctStripped.val());
		});

		app.outDiscPct.change(function() {
			app.outDiscPctStripped.val(app.outDiscPct.val());

			//calcDiscAmt("out");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcRgTax("out");

			if(app.outDiscPct.val() == "")
			{
				app.outTotFeeAmt.val(0);
				app.outTotFeeAmtStripped.val(0);
				app.outTaxPct.val('');
				app.outTaxPctStripped.val('');
				app.outTaxAmt.val(0);
				app.outTaxAmtStripped.val(0);
				app.outAmount.val(0);
				app.outAmountStripped.val(0);
				app.outCapGainTaxAmount.value(0);
				app.outPayAmt.value(0);
				app.outPayAmtStripped.val(0);
			}
		});

		app.outOtherPct.change(function() {
			app.outOtherPctStripped.val(app.outOtherPct.val());

			calcOtherAmt("out");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcRgTax("out");

			if(app.outOtherPct.val() == "")
			{
				app.outTotFeeAmt.val(0);
				app.outTotFeeAmtStripped.val(0);
				app.outAmount.val(0);
				app.outAmountStripped.val(0);
				app.outPayAmt.value(0);
				app.outPayAmtStripped.val(0);
			}
		});

		app.outDiscAmt.change(function() {
			app.outDiscPct.value("");

			var value = app.outDiscAmt.val();
			app.outDiscAmt.valueRnd(value.toNumber(","), true, outAmountRoundValue, outAmountRoundType);

			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcRgTax("out");

			if(value == "")
			{
				app.outTotFeeAmt.val(0);
				app.outTotFeeAmtStripped.val(0);
				app.outTaxPct.val('');
				app.outTaxPctStripped.val('');
				app.outTaxAmt.val(0);
				app.outTaxAmtStripped.val(0);
				app.outAmount.val(0);
				app.outAmountStripped.val(0);
				app.outCapGainTaxAmount.value(0);
				app.outPayAmt.value(0);
				app.outPayAmtStripped.val(0);
			}
		});

		app.outOtherAmt.change(function() {
			app.outOtherPct.val("");
			var value = app.outOtherAmt.val();
			app.outOtherAmt.valueRnd(value.toNumber(","), true, outAmountRoundValue, outAmountRoundType);

			calcFeeAmt("out");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
			calcRgTax("out");

			if(value == "")
			{
				app.outTotFeeAmt.val(0);
				app.outTotFeeAmtStripped.val(0);
				app.outAmount.val(0);
				app.outAmountStripped.val(0);
				app.outPayAmt.val(0);
				app.outPayAmtStripped.val(0);
			}
		});

		app.outTradeBy.change(function() {
			app.outTradeByHidden.val(app.outTradeBy.val());
			viewTradeBy("out");
			calcFeeAmt("out");
			//calcDiscAmt("out");
			calcOtherAmt("out");
			calcTaxAmt("out");
			calcTotFeeAmt("out");
			calcAmount("out");
		});

		app.inTradeBy.change(function() {
			app.inTradeByHidden.val(app.inTradeBy.val());
			viewTradeBy("in");
			calcFeeAmt("in");
			//calcDiscAmt("in");
			calcOtherAmt("in");
			calcTaxAmt("in");
			calcTotFeeAmt("in");
			calcNetAmount("in");
			calcUnit("in");
		});

		app.inType.change(function() {
			var con = app.inType.checked();
			app.firstSubscribe.val(con);
		});

		app.inAmt.change(function(data) {
			var value = app.inAmt.val();

			if(app.inAmt.val() != "")
			{
				app.inAmt.valueRnd(value.toNumber(","), true, inAmountRoundValue, inAmountRoundType);
			}
			else
			{
				app.inAmt.val("");
				app.inAmtStripped.val("");
			}

			rgFeeTiers(true, "in");
			app.inFeeAmt.focus();
		});

		app.inNetAmt.change(function() {
			var value = app.inNetAmt.val();

			if(app.inNetAmt.val() != "")
			{
				app.inNetAmt.valueRnd(value.toNumber(","), true, inAmountRoundValue, inAmountRoundType);
			}
			else
			{
				app.inNetAmt.val("");
				app.inNetAmtStripped.val("");
			}

			rgFeeTiers(true, "in");
		});

		app.inFeePct.live('blur', function() {
			var el = $(this);
			if(el.val() == '')
			{
				return;
			}
		});

		app.inFeePct.change(function() {
			app.inFeePctStripped.val(app.inFeePct.val());
			calcFeeAmt("in");
			calcTaxAmt("in");
			calcTotFeeAmt("in");
			calcNetAmount("in");
			calcUnit("in");
		});

		app.inPrice.change(function() {
			var value = app.inPrice.val();
			app.inPrice.removeClass('fieldError');
			app.errInPrice.html("");
			if(value != "")
			{
				app.inPrice.valueRnd(value.toNumber(","), true, inPriceRoundValue, inPriceRoundType);
				calcUnit("in");
				if(app.inPrice.val() == 0)
				{
					app.inPrice.addClass('fieldError');
					app.errInPrice.html("can not filled 0");
				}
			}
			else
			{
				app.inUnit.valueRnd(0, true, inPriceRoundValue, inPriceRoundType);
			}
		});
		app.inFeeAmt.change(function() {
			var value = app.inFeeAmt.val();
			app.inFeeAmt.valueRnd(value.toNumber(","), true, inAmountRoundValue, inAmountRoundType);
			app.inFeePct.val("");
			app.inFeePctStripped.val("");
			calcTaxAmt("in");
			calcTotFeeAmt("in");
			calcNetAmount("in");
			calcUnit("in");
		});
		app.inOtherAmt.change(function() {
			var value = app.inOtherAmt.val();
			app.inOtherAmt.valueRnd(value.toNumber(","), true, inAmountRoundValue, inAmountRoundType);
			calcTaxAmt("in");
			calcTotFeeAmt("in");
			calcNetAmount("in");
			calcUnit("in");
		});
		
		/*$('#remarkForCanceled').live('blur', function() {
	        alert("reakjkj");
	        if ($('#remarkForCanceled').val().length > 10){
	        	alert("arrg");
	        	app.remarkForCanceled.value = app.remarkForCanceled.value.substring(0,10);
	        }
	    });*/
		
		
		if($.browser.msie)
	    {
	    	/*$("#remarkForCanceled[maxlength]").bind('input propertychange', function() {
	    		alert("ajhjhjhjh");
	    			var maxLength = $(this).attr('maxlength');  
	    	        if ($(this).val().length > maxLength) {  
	    	            $(this).val($(this).val().substring(0, maxLength));  
	    	        }  
	    	    });*/
	    	
			$("#remarkForCancel").bind('input propertychange', function() {
	    		var maxLength = 200 ;  
	    	        if ($(this).val().length > maxLength) {  
	    	            $(this).val($(this).val().substring(0, maxLength));  
	    	        }  
	    	 });
			
	        $("#remarksCorrection[maxlength]").bind('input propertychange', function() {
	            var maxLength = $(this).attr('maxlength');  
	            if($(this).val().length > maxLength)
	            {  
	                $(this).val($(this).val().substring(0, maxLength));  
	            };
	        });
	    };

		app.remarkForCancel.change(function() {
			app.remarkForCancelHidden.val(app.remarkForCancel.val());
		});
		
		app.remarksCorrection.change(function() {
			app.remarksCorrectionHidden.val(app.remarksCorrection.val());
		});

		if(app.switchTypeList.val() == SWITCH_IN || app.switchTypeList.val() == SWITCH_IN_OUT || app.switchTypeList.isEmpty())
		{
//			if(app.inFeePct.val() != '')
			if ($('#inSwtFee').val() == 'true')
			{
				$("input[name='subscriptionFee']")[0].checked = true;
				$("input[name='swt.in.feePercent']").val(true);
				if (('${mode}' != 'view') && ('${confirming}' != 'true')) 
				{
					app.inFeePct.removeAttr('disabled');
					app.inFeeAmt.attr('disabled', true);
				}
			}
			else
			{
				$("input[name='subscriptionFee']")[1].checked = true;
				$("input[name='swt.in.feePercent']").val(false);
				if (('${mode}' != 'view') && ('${confirming}' != 'true')) 
				{
					app.inFeeAmt.removeAttr('disabled');
					app.inFeePct.attr('disabled', true);
				}
			}
		}

		app.subscriptionFee1.click(function() {
			$("input[name='subscriptionFee']")[0].checked = true;
			$("input[name='swt.in.feePercent']").val(true);
			if(('${mode}' != 'view') && ('${confirming}' != 'true'))
			{
				app.inFeeAmt.val('');
				app.inFeeAmtStripped.val('');
				app.inFeePct.removeAttr('disabled');
				app.inFeeAmt.attr('disabled', true);
			}
		});

		app.subscriptionFee2.click(function() {
			$("input[name='subscriptionFee']")[1].checked = true;
			$("input[name='swt.in.feePercent']").val(false);
			if(('${mode}' != 'view') && ('${confirming}' != 'true'))
			{
				app.inFeePct.val('');
				app.inFeePctStripped.val('');
				app.inFeeAmt.removeAttr('disabled');
				app.inFeePct.attr('disabled', true);
			}
		});

/* =========================================================================== 
 * Method
 * ========================================================================= */
		function getVarGlobal(){
			if(!app.outFundCode.val().isEmpty() || !app.inFundCode.val().isEmpty()){
				if(!app.outFundCode.val().isEmpty()){
					outAmountRoundValue = outRgProduct.amountRoundValue;
					outAmountRoundType = outRgProduct.amountRoundType;

					outUnitRoundValue = outRgProduct.unitRoundValue;
					outUnitRoundType = outRgProduct.unitRoundType;

					outPriceRoundValue = outRgProduct.priceRoundValue;
					outPriceRoundType = outRgProduct.priceRoundType;
				}
				if(!app.inFundCode.val().isEmpty())
				{
					var productCode = app.inFundCode.val();
					productCode = productCode.replace(/#/g, '${specialChar}').toString();
					inRgProduct = html.getRgProduct(productCode);

					if(inRgProduct != null)
					{
						inAmountRoundValue = inRgProduct.amountRoundValue;
						inAmountRoundType = inRgProduct.amountRoundType;

						inUnitRoundValue = inRgProduct.unitRoundValue;
						inUnitRoundType = inRgProduct.unitRoundType;

						inPriceRoundValue = inRgProduct.priceRoundValue;
						inPriceRoundType = inRgProduct.priceRoundType;
					}
				}
			}
			console.log("**********outAmountRoundValue:"+outAmountRoundValue);
			console.log("**********outAmountRoundType:"+outAmountRoundType);
		}
		if(!app.outFundCode.val().isEmpty() || !app.inFundCode.val().isEmpty())
		{
			if(!app.outFundCode.val().isEmpty())
			{
				var productCode = app.outFundCode.val();
				productCode = productCode.replace(/#/g, '${specialChar}').toString();
				outRgProduct = html.getRgProduct(productCode);

				if(outRgProduct != null)
				{
					outAmountRoundValue = outRgProduct.amountRoundValue;
					outAmountRoundType = outRgProduct.amountRoundType;

					outUnitRoundValue = outRgProduct.unitRoundValue;
					outUnitRoundType = outRgProduct.unitRoundType;

					outPriceRoundValue = outRgProduct.priceRoundValue;
					outPriceRoundType = outRgProduct.priceRoundType;

					app.effectiveDate.val(outRgProduct.effectiveDate);
					app.outRedLock.val(outRgProduct.redLock);
				}
			}

			if(!app.inFundCode.val().isEmpty())
			{
				var productCode = app.inFundCode.val();
				productCode = productCode.replace(/#/g, '${specialChar}').toString();
				inRgProduct = html.getRgProduct(productCode);

				if(inRgProduct != null)
				{
					inAmountRoundValue = inRgProduct.amountRoundValue;
					inAmountRoundType = inRgProduct.amountRoundType;

					inUnitRoundValue = inRgProduct.unitRoundValue;
					inUnitRoundType = inRgProduct.unitRoundType;

					inPriceRoundValue = inRgProduct.priceRoundValue;
					inPriceRoundType = inRgProduct.priceRoundType;
				}
			}
		}

		viewBySwitchTypeList(false);
		investorLookup();

		viewTradeBy("out");
		viewTradeBy("in");

		calcTaxAmt("out");
		calcRgPortfolio("out");
		
		calcTaxAmt("in");
		calcTotFeeAmt("in");

		attachPopupOutInvestment();
		attachPopupOutFundAccount();

		attachPopupInInvestment();
		attachPopupInFundAccount();

		app.outTradeByHidden.val(app.outTradeBy.val());
		app.inTradeByHidden.val(app.inTradeBy.val());

		if(app.dispatch.val() == 'edit')
		{
			app.switchTypeList.attr("disabled", "disabled");

			if(app.switchTypeList.val() != SWITCH_IN)
			{
				app.outFundCode.attr("disabled", "disabled");
				app.outFundCodeHelp.attr("disabled", "disabled");
				app.investorNo.attr("disabled", "disabled");
				app.investorNoHelp.attr("disabled", "disabled");
			}

			app.outInvest.attr("disabled", "disabled");
			app.outInvestHelp.attr("disabled", "disabled");
		}

		if(outRgProduct)
		{
			getPrice(outRgProduct);
		}

		// init event
		if(app.outTradeBy.val() == TRANSACTION_BY_AMOUNT)
		{
			calcUnit("out");
			calcTotFeeAmt("out");
			calcRgTax("out");
		}
		else
		{
			app.outUnit.change();
			app.outFeePct.change();		
			calcNetAmount("out");
		}

		calcUnit("in");

		if(!app.outFundCodeDesc.isEmpty())
		{
			$("#outFundName").text(app.outFundCodeDesc.val());
		}

		if(!app.outFundCodeDescThirdParty.isEmpty())
		{
			$("#outFundName").text(app.outFundCodeDescThirdParty.val());
		}

		if(!app.inFundCodeDesc.isEmpty())
		{
			$("#inFundName").text(app.inFundCodeDesc.val());
		}

		if(!app.inFundCodeDescThirdParty.isEmpty())
		{
			$("#inFundName").text(app.inFundCodeDescThirdParty.val());
		}

		app.switchTypeListHidden.val(app.switchTypeList.val());
		
		if(app.isApproval.val() == "true")
		{
			app.correction.css('display', '');
			app.remarksCorrection.removeAttr('disabled');
			app.needCorrection.removeAttr('disabled');
		}

		if(app.dispatch.val() == "edit")
		{
			app.clear.hide();
			app.correction.css('display', '');
			app.remarksCorrection.attr('disabled', 'disabled');
			app.needCorrection.attr('disabled', 'disabled');

			if(!app.remarksCorrection.isEmpty())
			{
				app.needCorrection.attr('checked', true);
			}
			else
			{
				app.needCorrection.attr('checked', false);
			}

			app.navDate.change();
		}
		
		if($("input[name='swt.out.feePercent']").val() == 'true')
		{
			$("input[name='outFeeBy']")[0].checked = true;
			app.outFeeAmt.attr('disabled', true);
			if((app.confirming.val() != "true") && (app.dispatch.val() != "view") && (app.isApproval.val() != "true") && (app.dispatch.val() != "edit"))
			{
				app.outFeePct.removeAttr("disabled");
			}
		}
		else
		{
			$("input[name='outFeeBy']")[1].checked = true;
			app.outFeePct.attr('disabled', true);
			if((app.confirming.val() != "true") && (app.dispatch.val() != "view") && (app.isApproval.val() != "true") && (app.dispatch.val() != "edit"))
			{
				app.outFeeAmt.removeAttr("disabled");
			}
		}

		if(app.confirming.val() == "true" || app.isApproval.val() == "true" ||
			('${from}' == 'cancelSwitching') || ('${from}' == 'singleList') || ('${from}' == 'listBatch')){
			if(inRgProduct != null){
				if(!app.inFundCode.isEmpty() && !app.navDate.isEmpty()){
					inRgNav = html.getRgNav(app.navDate.val().fmtYYYYMMDD("/"), app.inFundCode.val());
					
					app.inPrice.val("");
					app.inPriceStripped.val("");

					if(inRgProduct.fixnav == true){
						app.inPrice.valueRnd(inRgProduct.fixNavPrice, true, inPriceRoundValue, inPriceRoundType);
					} else{
						if(inRgNav){
							if(typeof inRgNav.offerPct === "undefined"){
								var offerPrice = inRgNav.nav;
							} else {
								var offerPrice = (1+((inRgNav.offerPct)/100))*inRgNav.nav;
							}
							app.inPrice.valueRnd(offerPrice, true, inPriceRoundValue, inPriceRoundType);
						}
					}
					calcUnit("in");
				}
			}

			if(('${from}' != 'cancelSwitching') && ('${from}' != 'singleList') && ('${from}' != 'listBatch')){
				if($("input[name='subscriptionFeeForConfirm']").val() == 'subscriptionFeePct'){
					$("input[name='subscriptionFee']")[0].checked = true;
				} else {
					$("input[name='subscriptionFee']")[1].checked = true;
				}
			}

			calcFeeAmt("in");
			calcFeeAmt("out");
		}

		if((app.switchTypeList.val() == SWITCH_IN_OUT) || (app.switchTypeList.val() == SWITCH_OUT) || (app.switchTypeList.isEmpty()))
		{
			if(app.outFeePctStripped.isEmpty() && app.outFeeAmtStripped.isEmpty())
			{
				app.outFeeBy1.attr('checked', true);
				app.outFeePct.val("0");
				app.outFeePctStripped.val("0");
				if((app.confirming.val() != "true") && (app.dispatch.val() != "view") && (app.isApproval.val() != "true") && (app.dispatch.val() != "edit"))
				{
					app.outFeePct.removeAttr("disabled");
				}
				app.outFeeAmt.attr("disabled", "disabled");
				
				if(app.switchTypeList.val() == SWITCH_IN)
				{
					app.outFeePct.attr("disabled", "disabled");
				}
			}
		}

		/*
		if(!app.inFeePct.isEmpty())
		{
			app.inFeeBy1.attr('checked', true);
		}

		if(!app.inFeeAmt.isEmpty())
		{
			app.inFeeBy2.attr('checked', true);
		}
		 */

		if(!app.outType.isEmpty())
		{
			app.liquidation.attr(app.outType.val());
		}

		if(!app.inType.isEmpty())
		{
			//app.firstSubscribe.val(app.inType.val());
		}

		if(app.switchTypeList.val() == SWITCH_OUT)
		{
			app.inTradeBy.val("");
			app.inTradeByHidden.val("");
			app.inFeePct.val("");
			app.inTotFeeAmt.val("");
			app.inUnit.val("");
			$("input[name='subscriptionFee']")[0].checked = true;
		}
		
		if(app.switchTypeList.val() == SWITCH_IN)
		{
			app.outFeePct.val("");
			app.outTotFeeAmt.val("");
			app.outUnit.val("");
			app.outNetAmt.val("");
			app.outOtherAmt.val("");
			app.outFeeAmt.val("");
			app.outPayAmt.val("");
		}

		if(app.isApproval.val() == "true"){
			app.inFeePct.attr("disabled", "disabled");
			app.inFeeAmt.attr("disabled", "disabled");
			if(app.switchTypeList.val() == SWITCH_IN){
				app.navDate.change();
			}
		}

		// OUT FEE PERCENT, kenapa false karena fee amount, klo true maka fee pct
		if ((app.dispatch.val()!= 'entry') && ( $("input[name='swt.out.feePercent']").val() == 'false')){
			app.outFeePct.calcRedFeePct(app.outNetAmt.val().toNumber(","), app.outFeeAmt.val().toNumber(","));
			app.outFeePct.valueRnd(app.outFeePct.val(), true, outUnitRoundValue, outUnitRoundType);
			app.outFeePct.val(parseFloat(app.outFeePct.val()));
			app.outFeePctStripped.val(null);
		}else if ((app.dispatch.val() != 'entry') && ('${confirming}' != 'true')){
			if('${swt.out}' != ''){
				$("#outFeePct").val('${swt?.out?.feePct}');
				$("#outFeePctStripped").val('${swt?.out?.feePct}');
			}			
		}
	} else{
		return new Switch(html);
	}
	
	function getPaymentDate(){
		var aTradeDateNew = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', app.tradeDate.val(), null);
		var redPayPeriod = null;
		if (app.switchTypeList.val() == SWITCH_IN) {
			redPayPeriod = inRgProduct.redPayPeriod;
		}else {
			redPayPeriod = outRgProduct.redPayPeriod;
		}
		//var redPayPeriod = outRgProduct.redPayPeriod;
		var aTransactionDateYYYYMMDDNew = $.datepicker.formatDate("yymmdd", aTradeDateNew);
		var PaymentDateYYYMMDD = html.getWorkingDate(aTransactionDateYYYYMMDDNew, redPayPeriod);
		
		var datePayment = $.datepicker.parseDate('yymmdd', PaymentDateYYYMMDD, null);
		app.paymentDate.value($.datepicker.formatDate('${appProp?.jqueryDateFormat}', datePayment));
	}
	
	function setPriceUnitIn(){
		if(inRgProduct != null){
			if(!app.inFundCode.isEmpty() && !app.navDate.isEmpty() && app.isApproval.val() != "true"){
				inRgNav = html.getRgNav(app.navDate.val().fmtYYYYMMDD("/"), app.inFundCode.val());
				app.inPrice.val("");
				app.inPriceStripped.val("");

				if(inRgProduct.fixnav == true){
					if(app.isApproval.val() != "true"){
						app.inPrice.valueRnd(inRgProduct.fixNavPrice, true, inPriceRoundValue, inPriceRoundType);
					}
				} else {
					if(inRgNav) {
						if(typeof inRgNav.offerPct === "undefined") {
							var offerPrice = inRgNav.nav;
						} else {
							var offerPrice = (1+((inRgNav.offerPct)/100))*inRgNav.nav;
						}
						if(app.isApproval.val() != "true"){
							app.inPrice.valueRnd(offerPrice, true, inPriceRoundValue, inPriceRoundType);
						}
					}
				}
				calcUnit("in");
			}
		}
	}
	
	app.switchTypeList.change(function() {
		if(outRgProduct != null)
		{
			if(!app.outFundCode.isEmpty() && !app.navDate.isEmpty())
			{
				if(!outRgProduct.fixnav)
				{
					outRgProduct.fixnav = false;
				}

				app.outPrice.value("");
				getPrice(outRgProduct);
				app.outPrice.change();
			}
		}

		if(inRgProduct != null)
		{
			if(!app.inFundCode.isEmpty() && !app.navDate.isEmpty() && app.isApproval.val() != "true")
			{
				inRgNav = html.getRgNav(app.navDate.val().fmtYYYYMMDD("/"), app.inFundCode.val());
				app.inPrice.val("");
				app.inPriceStripped.val("");

				if(inRgProduct.fixnav == true)
				{
					if(app.isApproval.val() != "true"){
						app.inPrice.valueRnd(inRgProduct.fixNavPrice, true, inPriceRoundValue, inPriceRoundType);
					}
				}
				else
				{
					if(inRgNav)
					{
						if(typeof inRgNav.offerPct === "undefined")
						{
							var offerPrice = inRgNav.nav;
						}
						else
						{
							var offerPrice = (1+((inRgNav.offerPct)/100))*inRgNav.nav;
						}
						if(app.isApproval.val() != "true"){
							app.inPrice.valueRnd(offerPrice, true, inPriceRoundValue, inPriceRoundType);
						}
					}
				}
				calcUnit("in");
			}
		}
	});
	if(('${from}' == 'cancelSwitching') || ('${from}' == 'singleList') || ('${from}' == 'listBatch')){
		if(app.switchTypeList.val() == SWITCH_IN) app.switchTypeList.change();

		if ((app.dispatch.val()!= 'entry') && ($("input[name='swt.in.feePercent']").val() == 'false')){
			app.inFeePct.calcRedFeePct(app.inNetAmt.val().toNumber(","), app.inFeeAmt.val().toNumber(","));
			app.inFeePct.valueRnd(app.inFeePct.val(), true, inUnitRoundValue, inUnitRoundType);
			app.inFeePct.val(parseFloat(app.inFeePct.val()));
			app.inFeePctStripped.val(null);
		}else if ((app.dispatch.val() != 'entry') && ('${confirming}' != 'true')){
			if('${swt.in}' != ''){
				$("#inFeePct").val('${swt?.in?.feePct}');
				$("#inFeePctStripped").val('${swt?.in?.feePct}');
			}
		}
	}

	if(app.confirming.val() == "true" || app.isApproval.val() == "true"){
		if(app.switchTypeList.val() == SWITCH_IN) app.switchTypeList.change();
	}
	
	if((app.confirming.val() != "true") && (app.dispatch.val() == "edit")) {
		if((app.switchTypeList.val() == SWITCH_IN_OUT) || (app.switchTypeList.val() == SWITCH_OUT)){
			if(app.outTradeBy.val() == TRANSACTION_BY_UNIT){
				calcFeeAmt("out");
			}
		}
	}
	if(app.isApproval.val() != "true"){
		setPriceUnitIn();
	}
}