function Product(html) {
	if (this instanceof Product) {

		/*
		 * ===========================================================================
		 * GUI Variable
		 * =========================================================================
		 */
		var componentss = [ "subFeeTable", "redFeeTable", "swiFeeTable",
				"divFeeTable", "subExceptionSpecificDate",
				"redExceptionSpecificDate", "swiExceptionSpecificDate",
				"divExceptionSpecificDate"

				, "dialogTier", "dialogBankAccountSub", "dialogBankAccountRed"
				, "dialogType", "dialogId",
				"dialogUpperLimit", "dialogUpperLimitRed",
				"dialogUpperLimitStripped", "dialogValue",
				"dialogValueStripped", "oldDialogUpperLimit",
				"newDialogUpperLimit", "oldDialogValue", "newDialogValue"

				, "rdMinBalAmount", "rdMinBalUnit", "minBal", "minBalAmt",
				"minBalUnit", "maxBalAmt", "maxBalAmtPct", "maxBalUnit",
				"maxBalUnitPct", "rdTotRedeemAmount", "rdTotRedeemUnit",
				"rdTotSwitchAmount", "rdTotSwitchUnit", "totRedeem",
				"totSwitch", "fixnav1", "fixnav2", "fixNavPrice",
				"fixNavPriceStripped"

				, "subTierBy", "subTierType", "redTierBy", "redTierType",
				"swiTierBy", "swiTierType", "divTierBy", "divTierType"

				, "limitrule", "liquidDate", "effectiveDate"

				, "divInvestorOption", "divIopByCashPct",
				"divIopByReivenstPct", "divIopByRedeemPct"

				, "thirdPartyKey", "thirdPartyCode", "thirdPartyName"

				, "subLock2", "subExceptionDate", "subAddDate", "redLock2",
				"redExceptionDate", "redAddDate", "swiLock2",
				"swiExceptionDate", "swiAddDate", "divLock2",
				"divExceptionDate", "divAddDate"

				, "checkBidOffer", "offerPct", "bidPct"

				, "redLimitAmt", "redLimitAmt1", "redLimitPct", "redLimitPct1",
				"swoLimitAmt", "swoLimitPct"

				, "subMaxFee", "isCheckedSubFee", "redMaxFee",
				"isCheckedRedFee", "swiMaxFee", "isCheckedSwiFee"

				, "subMaxAmt", "isCheckedSub", "redMaxAmt", "isCheckedRed",
				"swiMaxAmt", "isCheckedSwi"

				, "maxInvestor", "maxInvestorStripped", "isCheckMaxInvestor"

				, "isActive", "newSubFee", "newRedFee", "newSwiFee",
				"bidPctStripped", "offerPctStripped"

				, "redLimitAmtStripped", "redLimitPctStripped",
				"redLimitPct1Stripped", "redLimitAmt1Stripped",
				"redLimitAmtHidden", "redLimitPctHidden",
				"swoLimitAmtStripped", "swoLimitPctStripped"

				, "subFees", "redFees", "swiFees", "divFees",
				"subLockExceptions", "redLockExceptions", "divLockExceptions",
				"btnBankAccountAdd"

				, "bankInfoTable"

				, "bankAccountKey", "productCode"

				, "bankCode", "bankCodeKey", "bankCodeDesc"

				// , "bankBranch"
				, "bankAccountNo"
				// , "accountHolderName"

				, "beneficiaryName", "currencyCode"

				, "oldDefaultRgProductBankAccount", "defaultProductBankAccount"

				, "subLock1", "redLock1", "divLock1", "swiLock1"

				, "addSubFee", "delSubFee", "addRedFee", "delRedFee",
				"addSwiFee", "delSwiFee", "addDivFee", "delDivFee"

				, "effectiveTo", "maxCounter"

				, "tabs", "tabsBankAccount", "customerNo", "generateToAria", "isMaturityDateMax", "fundManager",
				"fundManagerDesc", "fundManagerKey", "h_fundManagerDesc",
				"custodianBank", "custodianBankKey", "custodianBankDesc",
				"h_custodianBankDesc", "type", "typeKey", "typeDesc",
				"h_typeDesc", "clazz", "clazzKey", "clazzDesc", "h_clazzDesc",
				"currency", "currencyKey", "currencyDesc", "h_currencyDesc",
				"subTax", "subTaxKey", "subTaxDesc", "h_subTaxDesc", "redTax",
				"redTaxKey", "redTaxDesc", "h_redTaxDesc", "swiTax",
				"swiTaxKey", "swiTaxDesc", "h_swiTaxDesc", "divTax",
				"divTaxKey", "divTaxDesc", "h_divTaxDesc", "subMaxAmtStripped",
				"subMinAmtStripped", "subMinAmt", "subMaxFeeStripped",
				"subMinFeeStripped", "subMinFee", "redMaxAmtStripped",
				"redMinAmtStripped", "redMinAmt", "redMaxFeeStripped",
				"redMinFeeStripped", "redMinFee", "swiMaxAmtStripped",
				"swiMinAmtStripped", "swiMinAmt", "swiMaxFeeStripped",
				"swiMinFeeStripped", "swiMinFee", "amountVal", "unitVal",
				"priceVal", "transactionDateVal", "subCot", "redCot", "swiCot" ];
		var prd = html.injectComp(this, false, componentss);

		prd["minBal"] = html.name("minBal");
		prd["totRedeem"] = html.name("totRedeem");
		prd["totSwitch"] = html.name("totSwitch");

		/*
		 * ===========================================================================
		 * Variable
		 * =========================================================================
		 */
		var TYPE_SUBSCRIBE = "SUBSCRIBE";
		var TYPE_REDEMPTION = "REDEMPTION";
		var TYPE_SWITCHING = "SWITCHING";
		var TYPE_DIVIDEND = "DIVIDEND";

		var subTable = new TableFee(prd.subFeeTable, function(object) {
			editTierSub(object);
		}, TYPE_SUBSCRIBE);
		var redTable = new TableFee(prd.redFeeTable, function(object) {
			editTierSub(object);
		}, TYPE_REDEMPTION);
		var swiTable = new TableFee(prd.swiFeeTable, function(object) {
			editTierSub(object);
		}, TYPE_SWITCHING);
		// var divTable = new TableFee(prd.divFeeTable);

		// var bankInfoTable = new TableBankInfo(prd.bankInfoTable);
		var subExceptionSpecificDate = new TableExceptionSpecificDate(
				prd.subExceptionSpecificDate, TYPE_SUBSCRIBE);
		var redExceptionSpecificDate = new TableExceptionSpecificDate(
				prd.redExceptionSpecificDate, TYPE_REDEMPTION);
		var swiExceptionSpecificDate = new TableExceptionSpecificDate(
				prd.swiExceptionSpecificDate, TYPE_SWITCHING);
		var divExceptionSpecificDate = new TableExceptionSpecificDate(
				prd.divExceptionSpecificDate, TYPE_DIVIDEND);

		if("${maturityDateMax}" == $("#liquidDate").val()) $("#liquidDate").val("");
		if($("#liquidDate").isEmpty()) prd.isMaturityDateMax.attr("checked", true);
		/*
		 * ==================================================================
		 * Declare popup tabel /detail
		 * =================================================================
		 */
		prd.dialogTier.dialog({
			autoOpen : false,
			modal : true,
			heigth : '600px',
			width : '465px',
			resizable : false
		});

		// prd.dialogBankAccount.dialog({
		// autoOpen:false,
		// modal:true,
		// heigth:'600px',
		// width:'600px',
		// resizable:false
		// });
		/*
		 * ===========================================================================
		 * Method Ajax Call
		 * =========================================================================
		 */

		/*
		 * ===========================================================================
		 * Method
		 * =========================================================================
		 */
		
		$("#swiIntfAmountType").children().eq(0).remove();

		$("#amountType").children().eq(0).remove();
		$("#unitType").children().eq(0).remove();
		$("#priceType").children().eq(0).remove();

		// if(($("#amountVal").val() != "") && ($("#amountVal").val() != null)){
		// $("#amountVal").valueRnd($("#amountVal").val(), true, 2,
		// $("#amountType").val());
		// }
		//
		// if(($("#unitVal").val() != "") && ($("#unitVal").val() != null)){
		// $("#unitVal").valueRnd($("#unitVal").val(), true, 2,
		// $("#unitType").val());
		// }
		//
		// if(($("#priceVal").val() != "") && ($("#priceVal").val() != null)){
		// $("#priceVal").valueRnd($("#priceVal").val(), true, 2,
		// $("#priceType").val());
		// }

		// $("#amountType").change(function(){
		// if(($("#amountVal").val() != "") && ($("#amountVal").val() != null)){
		// $("#amountVal").valueRnd($("#amountVal").val(), true, 2,
		// $("#amountType").val());
		// }
		// });
		//		
		// $("#unitType").change(function(){
		// if(($("#unitVal").val() != "") && ($("#unitVal").val() != null)){
		// $("#unitVal").valueRnd($("#unitVal").val(), true, 2,
		// $("#unitType").val());
		// }
		// });
		//		
		// $("#priceType").change(function(){
		// if(($("#priceVal").val() != "") && ($("#priceVal").val() != null)){
		// $("#priceVal").valueRnd($("#priceVal").val(), true, 2,
		// $("#priceType").val());
		// }
		// });

		function generateToAria() {
			if ($("#generateToAria1").attr("checked") == true) {
				$("#spanEreportCode").html(" *");
				if (('${confirming}' != 'true') &&  ('${mode}' != 'view')) {
					$("#ereportCode").enabled();
				}
			} else {
				$("#spanEreportCode").html("");
				$("#ereportCode").val("").disabled();
			}
		}

		function checkIsMaturityDateMax() {
			if (prd.isMaturityDateMax.is(":checked")) {
				prd.isMaturityDateMax.val(true);
				$("#spanLiquidDate").html("");
				$("#liquidDate").val("").disabled();
				$("#liquidDate").removeClass('fieldError');

				if($("#effectiveDate").val() == ""){
	    			$('#btnNewNavDialogError').html("Effective Date cannot be empty");
		        	$("#newNavDialog").attr("disabled", true);
	    		} else {
	    			$('#btnNewNavDialogError').html("");
	            	$("#newNavDialog").removeAttr("disabled");
	    		}
			} else {
				prd.isMaturityDateMax.val(false);
				$("#spanLiquidDate").html(" *");
				if (('${confirming}' != 'true') &&  ('${mode}' != 'view')) {
					$("#liquidDate").enabled();
				}

				if(($("#effectiveDate").val() == "") || ($("#liquidDate").val() == "")){
	    			$('#btnNewNavDialogError').html("Effective Date and Maturity Date cannot be empty");
		        	$("#newNavDialog").attr("disabled", true);
	            } else {
	            	if(($("#effectiveDate").val() != "") && ($("#liquidDate").val() != "")){
	            		$('#btnNewNavDialogError').html("");
	                	$("#newNavDialog").removeAttr("disabled");
	            	}
	            }
			}
		}

		$("#generateToAria1").change(function(){
			generateToAria();
		});

		$("#generateToAria2").change(function(){
			generateToAria();
		});

		prd.isMaturityDateMax.change(function(){
			checkIsMaturityDateMax();
		});

		function openFeeDialog(type) {
			$("#dialogTier").dialog('open');
			$('.ui-widget-overlay').css('height', $('body').height());
			$("#dialogTier :text").val("");
			$("#dialogTier :hidden").val("");
			$('#errUpperLimitNotRed').html('');
			$('#errUpperLimitRed').html('');
			$('#errValue').html('');
			$('#dialogUpperLimit').removeClass('fieldError');
			$('#dialogValue').removeClass('fieldError');
			prd.dialogType.val(type);
			if (prd.dialogType.val() == 'REDEMPTION') {
				$('#divDialogUpperLimitRed').show();
				$('#divDialogUpperLimitNotRed').hide();
			} else {
				$('#divDialogUpperLimitRed').hide();
				$('#divDialogUpperLimitNotRed').show();
			}
			prd.dialogId.val("-1");
			prd.dialogUpperLimit.enabled('');
			prd.dialogUpperLimit.focus();
		}

		function editTierSub(object) {
			if (object.upperLimit == "") {
				prd.dialogUpperLimit.val("MAX");
				prd.dialogUpperLimitRed.val("MAX");
				prd.dialogUpperLimitStripped.val("");
				prd.dialogUpperLimit.disabled();
				prd.dialogUpperLimitRed.disabled();
			} else {
				prd.dialogUpperLimit.val(object.upperLimit);
				prd.dialogUpperLimitRed.val(object.upperLimit);
				prd.dialogUpperLimitStripped.val(object.upperLimit);
				prd.dialogUpperLimit.enabled();
				prd.dialogUpperLimitRed.enabled();
			}
			prd.dialogValue.val(object.value);
			prd.dialogValueStripped.val(object.value);
			prd.dialogId.val(object.index);
			prd.dialogType.val(object.type);
			prd.oldDialogUpperLimit.val(prd.dialogUpperLimitStripped.val());
			prd.newDialogUpperLimit.val(prd.oldDialogUpperLimit.val());
			prd.oldDialogValue.val(prd.dialogValueStripped.val());
			prd.newDialogValue.val(prd.oldDialogValue.val());
			$('#errUpperLimitNotRed').html('');
			$('#errUpperLimitRed').html('');
			$('#errValue').html('');
			$('#dialogUpperLimit').removeClass('fieldError');
			$('#dialogValue').removeClass('fieldError');
			// ======
			if (prd.dialogType.val() == 'REDEMPTION') {
				$('#divDialogUpperLimitRed').show();
				$('#divDialogUpperLimitNotRed').hide();
			} else {
				$('#divDialogUpperLimitRed').hide();
				$('#divDialogUpperLimitNotRed').show();
			}
			$("#dialogTier").dialog('open');
			$('.ui-widget-overlay').css('height', $('body').height());
		}

		function radioButtonStatus() {
			// if (!prd.minBalAmt.isEmpty() || !prd.minBalUnit.isEmpty()) {
			// if (!prd.minBalAmt.isEmpty()) prd.rdMinBalAmount.attr("checked",
			// "checked");
			// if (!prd.minBalUnit.isEmpty()) prd.rdMinBalUnit.attr("checked",
			// "checked");
			// prd.minBal.change();
			// }else{
			// prd.rdMinBalAmount.attr("checked", "checked");
			// prd.minBal.change();
			// }

			// if (!prd.maxBalAmt.isEmpty() || !prd.maxBalAmtPct.isEmpty()) {
			// if (!prd.maxBalAmt.isEmpty())
			// prd.rdTotRedeemAmount.attr("checked", "checked");
			// if (!prd.maxBalAmtPct.isEmpty())
			// prd.rdTotRedeemUnit.attr("checked", "checked");
			// prd.totRedeem.change();
			// }else{
			// prd.rdTotRedeemAmount.attr("checked", "checked");
			// prd.totRedeem.change();
			// }

			// if (!prd.maxBalUnit.isEmpty() || !prd.maxBalUnitPct.isEmpty()) {
			// if (!prd.maxBalUnit.isEmpty())
			// prd.rdTotSwitchAmount.attr("checked", "checked");
			// if (!prd.maxBalUnitPct.isEmpty())
			// prd.rdTotSwitchUnit.attr("checked", "checked");
			// prd.totSwitch.change();
			// }else{
			// prd.rdTotSwitchAmount.attr("checked", "checked");
			// prd.totSwitch.change();
			// }
		}

		function fixPriceStatus() {
			var disabled = prd.fixnav1.attr("disabled");
			if (disabled != 'disabled') {
				var yescon = prd.fixnav1.checked();
				var nocon = prd.fixnav2.checked();

				if (yescon == 'true' && nocon == 'false') {
					prd.fixNavPrice.enabled();
				}
				if (nocon == 'false' && yescon == 'false') {
					prd.fixNavPrice.disabled();
					prd.fixNavPrice.val("0");
					prd.fixNavPriceStripped.val("0");
				}
			}
		}

		function clearEmptyOptions() {
			prd.subTierBy.children().eq(0).remove();
			prd.subTierType.children().eq(0).remove();

			prd.redTierBy.children().eq(0).remove();
			prd.redTierType.children().eq(0).remove();

			prd.swiTierBy.children().eq(0).remove();
			prd.swiTierType.children().eq(0).remove();

			// prd.divTierBy.children().eq(0).remove();
			// prd.divTierType.children().eq(0).remove();

			// prd.limitrule.children().eq(0).remove();
		}

		function liquidDateValidate() {
			var effectiveDate = new Date($("#effectiveDate").datepicker('getDate')).getTime();
			var liquidDate = new Date($("#liquidDate").datepicker('getDate')).getTime();

			// if (effectiveDate > liquidDate) {
			// $("#effectiveDate").addClass('fieldError');
			// $("#effectiveDateError").html("Effective Date must be less than
			// Maturity Date");
			// $("#newNavDialog").attr("disabled", true);
			// }
			if (liquidDate) {
				if (liquidDate < effectiveDate) {
					$("#liquidDate").addClass('fieldError');
					$("#liquidDateError").html("Maturity Date must be greater than Effective Date");
					$("#newNavDialog").attr("disabled", true);
				} else {
					if (($("#effectiveDate").val() != "")
							&& ($("#liquidDate").val() != "")) {
						// $("#effectiveDate").removeClass('fieldError');
						// $("#effectiveDateError").html("");
						$("#liquidDate").removeClass('fieldError');
						$("#liquidDateError").html("");
						$("#newNavDialog").removeAttr("disabled");
					}
				}
			}
		};

		function validateMinMax(min, max, el, errMsg) {
			if (min > max) {
				$(el).addClass('fieldError');
				$(errMsg).html("Max value must be greater than Min value");
				return false;
			} else {
				$(el).removeClass('fieldError');
				$(errMsg).html("");
			}
		}
		;

		function sumDefaultInvestorOption() {
			if (prd.divInvestorOption.val() == 'true') {
				var divIopByCashPct = Number(prd.divIopByCashPct.val());
				var divIopByReivenstPct = Number(prd.divIopByReivenstPct.val());
				var divIopByRedeemPct = Number(prd.divIopByRedeemPct.val());

				prd.divIopByCashPct.removeClass('fieldError');
				prd.divIopByReivenstPct.removeClass('fieldError');
				prd.divIopByRedeemPct.removeClass('fieldError');
				$('#defaultInvestortypeError').html("");
				$('.defaultInvtOpt').html("");

				if (((prd.divIopByCashPct.val() != "")
						&& (prd.divIopByReivenstPct.val() != "") && (prd.divIopByRedeemPct
						.val() != ""))) {
					if ((divIopByCashPct + divIopByReivenstPct + divIopByRedeemPct) != 100) {
						prd.divIopByCashPct.addClass('fieldError');
						prd.divIopByReivenstPct.addClass('fieldError');
						prd.divIopByRedeemPct.addClass('fieldError');
						$('#defaultInvestortypeError')
								.html(
										"Total By Cash, By Reinvestment and By Redeem must equals 100");
					}
				}
			}
		}

		function lock(typeLock1, typeLock2, typeExceptionDate, typeAddButton,
				idError, typeTable) {
			var disabled = typeLock1.attr("disabled");
			typeExceptionDate.val("");
			typeExceptionDate.removeClass('fieldError');
			idError.html('');
			if (disabled != 'disabled') {
				var yescon = typeLock1.checked();
				var nocon = typeLock2.checked();
				// console.debug(typeTable);
				if (yescon == 'true' && nocon == 'false') {
					typeExceptionDate.enabled();
					typeAddButton.enabled();
				}
				if (nocon == 'false' && yescon == 'false') {
					typeTable.fnClearTable();
					typeExceptionDate.disabled();
					typeAddButton.disabled();
				}
			}
		}

		/*
		 * ===========================================================================
		 * Condition when mode entry , confirming , view and edit
		 * =========================================================================
		 */
		$('#newRedFee').button();
		$('#newRedFee').button("option", "disabled", true);

		if (prd.subLock2.is(":checked") == true) {
			prd.subExceptionDate.disabled();
			prd.subAddDate.disabled();
		}

		if (prd.redLock2.is(":checked") == true) {
			prd.redExceptionDate.disabled();
			prd.redAddDate.disabled();
		}

		if (prd.swiLock2.is(":checked") == true) {
			prd.swiExceptionDate.disabled();
			prd.swiAddDate.disabled();
		}

		if (prd.divLock2.is(":checked") == true) {
			prd.divExceptionDate.disabled();
			prd.divAddDate.disabled();
		}

		if (('${mode}' == 'entry')
				|| (('${mode}' == 'edit') && (('${prd?.recordStatus?.decodeStatus()}' == 'Reject') || ($(
						"#status").val() == 'R')))) {
			$('input[name=isActive]').attr("disabled", "disabled");
		}

		if (('${mode}' == 'entry') && ('${confirming}' != 'true')) {
			prd.checkBidOffer.attr("checked", true);
			if (prd.checkBidOffer.val() == 'true') {
				prd.offerPct.enabled();
				prd.bidPct.enabled();
			}

			if ($('#limitrule').val() == "ASSUM") {
				$("#checkBoxTotal").val(false);
				$("#checkBoxTotal").attr("checked", false);
				if (($("#redLimitPct").val() != '')
						|| ($("#redLimitPctStripped").val() != '')) {
					$("#isCheckedPercent").attr("checked", true);
				} else {
					$("#isCheckedPercent").attr("checked", false);
				}
			}

			if ($('#limitrule').val() == "SEPARATE") {
				$("#checkBoxTotal").val(true);
				$("#checkBoxTotal").attr("checked", true);
				$(".isSeparate").css("display", "");
				$(".total").css("display", "none");
				if ((($("#redLimitPct1").val() != '') || ($(
						"#redLimitPct1Stripped").val() != ''))
						&& (($('#swoLimitPct').val() != '') || ($(
								'#swoLimitPctStripped').val() != ''))) {
					$("#isCheckedPercent").attr("checked", true);
				} else {
					$("#isCheckedPercent").attr("checked", false);
				}
			}

			if (!($("#checkBoxTotal").is(":checked"))) {
				prd.limitrule.val("ASSUM");
				$("#checkBoxTotal").val(false);
			}

			if (($("#isCheckedPercent").is(":checked"))) {
				$("#isCheckedPercent").val(true);
				prd.redLimitAmt.hide();
				prd.redLimitAmt1.hide();
				prd.swoLimitAmt.hide();
				prd.redLimitPct.show();
				prd.redLimitPct1.show();
				prd.swoLimitPct.show();
				$("#percentSymbol").show();
				$(".percentSym").show();
			}

			if (!($("#isCheckedPercent").is(":checked"))) {
				$("#isCheckedPercent").val(false);
				prd.redLimitAmt.show();
				prd.redLimitAmt1.show();
				prd.swoLimitAmt.show();
				prd.redLimitPct.hide();
				prd.redLimitPct1.hide();
				prd.swoLimitPct.hide();
				$("#percentSymbol").hide();
				$(".percentSym").hide();
			}

			// if (!prd.subMaxAmt.isEmpty()){
			// prd.isCheckedSub.attr("checked", true);
			// prd.subMaxAmt.enabled();
			// }
			//			
			// if (!prd.subMaxFee.isEmpty()){
			// prd.isCheckedSubFee.attr("checked", true);
			// prd.subMaxFee.enabled();
			// }
			//
			// if (!prd.redMaxAmt.isEmpty()){
			// prd.isCheckedRed.attr("checked", true);
			// prd.redMaxAmt.enabled();
			// }
			//
			// if (!prd.redMaxFee.isEmpty){
			// prd.isCheckedRedFee.attr("checked", true);
			// prd.redMaxFee.enabled();
			// }
			//			
			// if(!prd.swiMaxAmt.isEmpty()){
			// prd.isCheckedSwi.attr("checked", true);
			// prd.swiMaxAmt.enabled();
			// }
			//
			// if(!prd.swiMaxFee.isEmpty()){
			// prd.isCheckedSwiFee.attr("checked", true);
			// prd.swiMaxFee.enabled();
			// }

			if (prd.subMaxAmt.isEmpty()) {
				if ($("#isCheckedSubError").html() != "Required") {
					prd.isCheckedSub.attr("checked", true);
					prd.subMaxAmt.disabled();
				}
			}

			// if (prd.subMaxFee.isEmpty()){
			// if ($("#isCheckedSubFeeError").html()!="Required") {
			// prd.isCheckedSubFee.attr("checked", true);
			// prd.subMaxFee.disabled();
			// }
			// }

			if (prd.redMaxAmt.isEmpty()) {
				if ($("#isCheckedRedError").html() != "Required") {
					prd.isCheckedRed.attr("checked", true);
					prd.redMaxAmt.disabled();
				}
			}

			if (prd.redMaxFee.isEmpty()) {
				if ($("#isCheckedRedFeeError").html() != "Required") {
					prd.isCheckedRedFee.attr("checked", true);
					prd.redMaxFee.disabled();
				}
			}

			if (prd.swiMaxAmt.isEmpty()) {
				if ($("#isCheckedSwiError").html() != "Required") {
					prd.isCheckedSwi.attr('checked', true);
					prd.swiMaxAmt.disabled();
				}
			}

			if (prd.swiMaxFee.isEmpty()) {
				if ($("#isCheckedSwiFeeError").html() != "Required") {
					prd.isCheckedSwiFee.attr("checked", true);
					prd.swiMaxFee.disabled();
				}
			}

			if (prd.maxInvestor.isEmpty()) {
				if ($("#isCheckMaxInvestorError").html() != "Required") {
					// console.debug("booked1");
					prd.isCheckMaxInvestor.attr("checked", true);
					prd.maxInvestor.disabled();
					$("#spanMaxInvestor").html("");
				} else {
					console.debug("booked3");
					$("#spanMaxInvestor").html(" *");
				}
			}
		}

		if (('${confirming}' == 'true') || ('${mode}' == 'edit')
				|| ('${mode}' == 'view')) {

			// if(('${mode}'=='edit') || ('${mode}'=='view'))
			// {
			// $('#currency').attr("disabled", "disabled");
			// $('#currencyHelp').attr("disabled", "disabled");
			// }

			if (($('#redLimitPctStripped').val() != '')
					|| ($('#redLimitPct1Stripped').val() != '')
					|| ($('#swoLimitPctStripped').val() != '')) {
				$("#isCheckedPercent").attr("checked", true);
				$("#isCheckedPercent").val(true);
			} else {
				$("#isCheckedPercent").val(false);
			}

			if ((!prd.offerPct.isEmpty()) && (!prd.bidPct.isEmpty())) {
				prd.checkBidOffer.attr("checked", true);
			} else {
				prd.offerPct.disabled();
				prd.bidPct.disabled();
			}

			if ($('#limitrule').val() == "SEPARATE") {
				$("#checkBoxTotal").attr("checked", true);
				$("#checkBoxTotal").val(true);
				$(".isSeparate").css("display", "");
				$(".total").css("display", "none");
				prd.redLimitAmt1.val(prd.redLimitAmtHidden.val());
				if ($("#isCheckedPercent").is(":checked")) {
					prd.redLimitAmt1.hide();
					prd.redLimitPct1.show();

					prd.swoLimitAmt.hide();
					prd.swoLimitPct.show();
					$('.percentSym').show();
				} else {
					prd.redLimitAmt1.show();
					prd.redLimitPct1.hide();

					prd.swoLimitAmt.show();
					prd.swoLimitPct.hide();
					$('.percentSym').hide();
				}

			} else {
				$(".isSeparate").css("display", "none");
				$(".total").css("display", "");
				$("#checkBoxTotal").val(false);

				if (($("#redLimitPct").val() != '')
						|| ($("#redLimitPctStripped").val() != '')) {
					// if ($("#isCheckedPercent").is(":checked")){
					$("#isCheckedPercent").attr("checked", true);
					prd.redLimitAmt.hide();
					prd.redLimitPct.show();
					$("#percentSymbol").show();
				} else {
					$("#isCheckedPercent").attr("checked", false);
					prd.redLimitAmt.show();
					prd.redLimitPct.hide();
					$("#percentSymbol").hide();
				}
			}

			if (prd.subMaxAmt.isEmpty()) {
				if ($("#isCheckedSubError").html() != "Required") {
					prd.isCheckedSub.attr("checked", true);
					prd.subMaxAmt.disabled();
				}
			}

			if (prd.subMaxFee.isEmpty()) {
				if ($("#isCheckedSubFeeError").html() != "Required") {
					prd.isCheckedSubFee.attr("checked", true);
					prd.subMaxFee.disabled();
				}
			}

			if (prd.redMaxAmt.isEmpty()) {
				if ($("#isCheckedRedError").html() != "Required") {
					prd.isCheckedRed.attr("checked", true);
					prd.redMaxAmt.disabled();
				}
			}

			if (prd.redMaxFee.isEmpty()) {
				if ($("#isCheckedRedFeeError").html() != "Required") {
					prd.isCheckedRedFee.attr("checked", true);
					prd.redMaxFee.disabled();
				}
			}

			if (prd.swiMaxAmt.isEmpty()) {
				if ($("#isCheckedSwiError").html() != "Required") {
					prd.isCheckedSwi.attr('checked', true);
					prd.swiMaxAmt.disabled();
				}
			}

			if (prd.swiMaxFee.isEmpty()) {
				if ($("#isCheckedSwiFeeError").html() != "Required") {
					prd.isCheckedSwiFee.attr("checked", true);
					prd.swiMaxFee.disabled();
				}
			}

			if (prd.maxInvestor.isEmpty()) {
				if ($("#isCheckMaxInvestorError").html() != "Required") {
					// console.debug("booked2");
					prd.isCheckMaxInvestor.attr("checked", true);
					prd.maxInvestor.disabled();
					$("#spanMaxInvestor").html("");
				} else {
					$("#spanMaxInvestor").html(" *");
				}
			}

			if (!(prd.divInvestorOption.is(":checked"))) {
				prd.divInvestorOption.val(false);
			}
		}

		/*
		 * ===========================================================================
		 * Event
		 * =========================================================================
		 */
		$("#dialog-message-1").css("display", "none");

		$('input[name=isActive]').change(
				function() {
					$("input[name='prd.isActive']").val(
							$("input[name='isActive']:checked").val());
				});

		var tableBank = $("#listBankAccount #bankAccountTable").dataTable();

		// prd.customerNo.change(function() {
		// $("#listBankAccount #bankAccountTable").dataTable().fnClearTable();
		// });
		//		
		// prd.currency.change(function() {
		// $("#listBankAccount #bankAccountTable").dataTable().fnClearTable();
		// });

		// BID and OFFER Process
		prd.checkBidOffer.change(function() {
			if (prd.checkBidOffer.is(":checked")) {
				prd.checkBidOffer.val(true);
				prd.offerPct.enabled();
				prd.bidPct.enabled();
				$("#reqOffer").html(" *");
				$("#reqBid").html(" *");
			} else {
				prd.checkBidOffer.val(false);
				prd.offerPct.disabled();
				prd.bidPct.disabled();
				prd.offerPct.val('');
				prd.offerPctStripped.val('');
				prd.bidPct.val('');
				prd.bidPctStripped.val('');
				$("#reqOffer").html("");
				$("#reqBid").html("");
			}
		});

		prd.newSubFee
				.click(function() {
					var rateSub = $.trim($('#subFeeTable').tbody().children()
							.children().eq(1).html());
					if (rateSub == '') {
						$('#errRateNotNull')
								.html(
										'Click grid "MAX" and fill "Rate" into grid before add New Data tier !');
						return false;
					} else {
						$('#errRateNotNull').html('');
						openFeeDialog(TYPE_SUBSCRIBE);
						return false;
					}
				});

		prd.newRedFee.click(function() {
			openFeeDialog(TYPE_REDEMPTION);
			return false;
		});

		prd.newSwiFee
				.click(function() {
					var rateSwi = $.trim($('#swiFeeTable').tbody().children()
							.children().eq(1).html());
					if (rateSwi == '') {
						$('#errRateNotNullSwi')
								.html(
										'Click grid "MAX" and fill "Rate" into grid before add New Data tier !');
						return false;
					} else {
						openFeeDialog(TYPE_SWITCHING);
						return false;
					}
				});

		$("#isCheckedPercent").change(function() {
			$('#errTotalLimit').html('');
			if ($("#isCheckedPercent").is(":checked")) {
				prd.redLimitAmt.val('');
				prd.redLimitAmtStripped.val('');
				prd.redLimitAmt.hide();
				prd.redLimitAmtHidden.val('');

				prd.redLimitPct1.val('');
				prd.redLimitPct1Stripped.val('');
				prd.redLimitPct.show();
				$("#percentSymbol").show();
				if ($(".isSeparate").css("display") != "none") {
					if ($("#checkBoxTotal").is(":checked")) {
						prd.redLimitAmt1.hide();
						prd.redLimitAmt1.val('');
						prd.redLimitAmt1Stripped.val('');

						prd.redLimitPct.val('');
						prd.redLimitPctStripped.val('');
						prd.redLimitPct1.show();

						prd.swoLimitAmt.hide();
						prd.swoLimitAmt.val('');
						prd.swoLimitAmtStripped.val('');

						prd.swoLimitPct.val('');
						prd.swoLimitPctStripped.val('');
						prd.swoLimitPct.show();
						$('.percentSym').show();
					}
				}
			} else {
				prd.redLimitAmt.show();
				prd.redLimitPct.val('');
				prd.redLimitPctStripped.val('');
				prd.redLimitPctHidden.val('');
				prd.redLimitPct.hide();
				$("#percentSymbol").hide();
				if ($(".isSeparate").css("display") != "none") {
					if ($("#checkBoxTotal").is(":checked")) {
						prd.redLimitAmt1.show();
						prd.redLimitAmt1.val('');
						prd.redLimitAmt1Stripped.val('');
						prd.redLimitPct1.hide();

						prd.swoLimitAmt.show();
						prd.swoLimitAmt.val('');
						prd.swoLimitAmtStripped.val('');
						prd.swoLimitPct.hide();
						$('.percentSym').hide();
					}
				}
			}
		});

		$("#checkBoxTotal").change(function() {
			if ($("#checkBoxTotal").is(":checked")) {
				$(".isSeparate").css("display", "");
				$(".total").css("display", "none");
				prd.redLimitAmt.val('');
				prd.redLimitPct.val('');

				prd.redLimitAmtStripped.val('');
				prd.redLimitPctStripped.val('');

				prd.redLimitPct1.val('');
				prd.redLimitPct1Stripped.val('');

				prd.swoLimitPct.val('');
				prd.swoLimitPctStripped.val('');
				$("#checkBoxTotal").val(true);
				prd.limitrule.val("SEPARATE");
				if ($(".isSeparate").css("display") != "none") {
					if ($("#isCheckedPercent").is(":checked")) {
						prd.redLimitAmt1.hide();
						prd.redLimitAmt1.val('');
						prd.redLimitAmt1Stripped.val('');
						prd.redLimitPct1.show();

						prd.swoLimitAmt.val('');
						prd.swoLimitAmtStripped.val('');
						prd.swoLimitPct.val('');
						prd.swoLimitPctStripped.val('');
						prd.swoLimitAmt.hide();
						prd.swoLimitPct.show();
						$('.percentSym').show();
						$("#isCheckedPercent").val(true);
					} else {
						prd.redLimitAmt1.val('');
						prd.redLimitAmt1Stripped.val('');
						prd.redLimitAmt1.show();
						prd.redLimitPct1.val('');
						prd.redLimitPct1Stripped.val('');
						prd.redLimitPct1.hide();

						prd.swoLimitAmt.show();
						prd.swoLimitPct.val('');
						$('input[name="prd.swoLimitPct"]').val('');
						prd.swoLimitPctStripped.val('');

						prd.swoLimitPct.hide();
						$('.percentSym').hide();
						$("#isCheckedPercent").val(false);
					}
				}
			} else {
				$(".isSeparate").css("display", "none");
				$(".total").css("display", "");

				prd.redLimitAmt.val('');
				prd.redLimitAmtStripped.val('');

				prd.redLimitPct.val('');
				prd.redLimitPctStripped.val('');

				prd.swoLimitAmt.val('');
				prd.swoLimitAmtStripped.val('');

				prd.swoLimitPct.val('');
				prd.swoLimitPctStripped.val('');
				$("#checkBoxTotal").val(false);
				prd.limitrule.val("ASSUM");
				if ($(".total").css("display") != "none") {
					if ($("#isCheckedPercent").is(":checked")) {
						prd.redLimitAmt.hide();
						prd.redLimitAmt.val('');
						prd.redLimitAmtStripped.val('');
						prd.redLimitPct.show();
						$('.percentSym').show();
						$("#isCheckedPercent").val(true);
					} else {
						prd.redLimitAmt.show();
						prd.redLimitPct.val('');
						prd.redLimitPctStripped.val('');
						prd.redLimitPct.hide();

						$('#percentSymbol').hide();
						$("#isCheckedPercent").val(false);
					}
				}
			}
		});

		prd.redLimitAmt.blur(function() {
			prd.redLimitAmtHidden.val(prd.redLimitAmtStripped.val());
		});

		prd.redLimitAmt1.blur(function() {
			prd.redLimitAmtHidden.val(prd.redLimitAmt1Stripped.val());
		});

		prd.redLimitPct.blur(function() {
			prd.redLimitPctStripped.val(prd.redLimitPct.val());
			prd.redLimitPctHidden.val(prd.redLimitPctStripped.val());
		});

		prd.redLimitPct1.blur(function() {
			prd.redLimitPct1Stripped.val(prd.redLimitPct1.val());
			prd.redLimitPctHidden.val(prd.redLimitPct1Stripped.val());
		});

		prd.swoLimitPct.blur(function() {
			prd.swoLimitPctStripped.val(prd.swoLimitPct.val());
		});

		prd.offerPct.blur(function() {
			prd.offerPctStripped.val(prd.offerPct.val());
		});

		prd.bidPct.blur(function() {
			prd.bidPctStripped.val(prd.bidPct.val());
		});

		$('tbody tr td #deleteButton[disabled!=true]').live(
				'click',
				function() {
					var idx = $(this).parent().parent().parent().index();
					var tableSubExceptionSpecificDate = $(
							'#subExceptionSpecificDate').dataTable();
					var tableRedExceptionSpecificDate = $(
							'#redExceptionSpecificDate').dataTable();
					var tableSwiExceptionSpecificDate = $(
							'#swiExceptionSpecificDate').dataTable();
					var tableDivExceptionSpecificDate = $(
							'#divExceptionSpecificDate').dataTable();
					var row = $(this).parents('tr');
					var subRowDelete = tableSubExceptionSpecificDate
							.fnGetPosition(row[0]);
					var redRowDelete = tableRedExceptionSpecificDate
							.fnGetPosition(row[0]);
					var swiRowDelete = tableSwiExceptionSpecificDate
							.fnGetPosition(row[0]);
					var divRowDelete = tableDivExceptionSpecificDate
							.fnGetPosition(row[0]);
					var type = $(this).attr("feeType");
					var exceDateType = $(this).attr("exceDateType");
					var deleteTable = function() {
						if (type == TYPE_SUBSCRIBE) {
							subTable.delRow("prd.subFees", idx);
						}

						if (type == TYPE_REDEMPTION) {
							redTable.delRow("prd.redFees", idx);
						}

						if (type == TYPE_SWITCHING) {
							swiTable.delRow("prd.swiFees", idx);
						}
						if (exceDateType == TYPE_SUBSCRIBE) {
							subExceptionSpecificDate.delRow(
									"prd.subLockExceptions", subRowDelete);
						}
						if (exceDateType == TYPE_REDEMPTION) {
							redExceptionSpecificDate.delRow(
									"prd.redLockExceptions", redRowDelete);
						}
						if (exceDateType == TYPE_SWITCHING) {
							swiExceptionSpecificDate.delRow(
									"prd.swiLockExceptions", swiRowDelete);
						}
						if (exceDateType == TYPE_DIVIDEND) {
							divExceptionSpecificDate.delRow(
									"prd.divLockExceptions", divRowDelete);
						}
						$("#dialog-message").dialog("close");
					};
					var closeDialog = function() {
						$("#dialog-message").dialog("close");
					};
					/*
					 * $("#dialog-message-1").dialog({ autoOpen:false,
					 * height:120, width:250, modal:true, resizable : false,
					 * buttons: { "Yes": function() { if (type ==
					 * TYPE_SUBSCRIBE){ subTable.delRow("prd.subFees",idx); }
					 * 
					 * if (type == TYPE_REDEMPTION){
					 * redTable.delRow("prd.redFees",idx); }
					 * 
					 * if (type == TYPE_SWITCHING){
					 * swiTable.delRow("prd.swiFees", idx); } if (exceDateType ==
					 * TYPE_SUBSCRIBE) {
					 * subExceptionSpecificDate.delRow("prd.subLockExceptions",
					 * subRowDelete); } if (exceDateType == TYPE_REDEMPTION) {
					 * redExceptionSpecificDate.delRow("prd.redLockExceptions",
					 * redRowDelete); } if (exceDateType == TYPE_DIVIDEND) {
					 * divExceptionSpecificDate.delRow("prd.divLockExceptions",
					 * divRowDelete); } $("#dialog-message-1").dialog("close");
					 *  }, "No ": function() {
					 * $("#dialog-message-1").dialog("close"); } } });
					 * $('#dialog-message-1').css('overflow','hidden');
					 * $("#dialog-message-1").dialog('open');
					 */
					messageAlertYesNo("Are you sure to inactive data ?",
							"ui-icon ui-icon-notice", "Confirmation Message",
							deleteTable, closeDialog);
				});

		$('#cancelTierDialog').click(function() {
			$("#dialogTier").dialog('close');
			$('#errUpperLimit').html('');
			$('#errValue').html('');
			return false;
		});

		prd.dialogUpperLimit.blur(function() {
			prd.dialogUpperLimitStripped.val($.fn.autoNumeric
					.Strip('dialogUpperLimit'));
			prd.newDialogUpperLimit.val(prd.dialogUpperLimitStripped.val());
		});

		prd.dialogUpperLimitRed.blur(function() {
			prd.dialogUpperLimitStripped.val($.fn.autoNumeric
					.Strip('dialogUpperLimitRed'));
		});

		prd.dialogValue.blur(function() {
			prd.newDialogValue.val(prd.dialogValueStripped.val());
		});

		$('#addTierDialog')
				.click(
						function() {
							var row = new Object();
							row.upperLimit = prd.dialogUpperLimitStripped.val();
							row.value = prd.dialogValueStripped.val();
							row.index = prd.dialogId.val();
							row.type = prd.dialogType.val();
							prd.newDialogValue.val(prd.dialogValueStripped
									.val());

							if ($('#divDialogUpperLimitRed').css('display') != 'none') {
								if ((row.value == '')
										|| (prd.dialogUpperLimitRed.val() == '')) {
									if (prd.dialogUpperLimitRed.val() == '') {
										$('#errUpperLimitRed').html('Required');
									} else {
										$('#errUpperLimitRed').html('');
									}

									if (row.value == '') {
										$('#errValue').html('Required');
									} else {
										$('#errValue').html('');
									}

									return false;
								}
							} else {
								if ((prd.dialogUpperLimit.val() == '')
										|| (row.value == '')) {

									if ((prd.dialogUpperLimit.val() == '')) {
										$('#errUpperLimitNotRed').html(
												'Required');
									} else {
										$('#errUpperLimitNotRed').html('');
									}

									if (row.value == '') {
										$('#errValue').html('Required');
									} else {
										$('#errValue').html('');
									}

									return false;
								}
							}

							var arrMap = [];
							var newValue = prd.newDialogUpperLimit.val();
							var oldValue = prd.oldDialogUpperLimit.val();
							var newRate = prd.newDialogValue.val();
							var oldRate = prd.oldDialogValue.val();
							var pValue = row.upperLimit;
							var pRate = row.value;

							if (row.type == TYPE_SUBSCRIBE) {

								$("#subFeeTable tbody tr td")
										.each(
												function(html) {
													var value = $.trim($(this)
															.html());
													arrMap[arrMap.length] = value;

													if ((($.inArray(pValue,
															arrMap) != -1) && (oldValue != newValue))
															|| (($.inArray(
																	pRate,
																	arrMap) != -1) && (oldRate != newRate))) {
														if (($.inArray(pValue,
																arrMap) != -1)
																&& (oldValue != newValue)) {
															$(
																	'#dialogUpperLimit')
																	.addClass(
																			'fieldError');
															$(
																	'#errUpperLimitNotRed')
																	.html(
																			'Duplicate Value!');
														} else {
															$(
																	'#dialogUpperLimit')
																	.removeClass(
																			'fieldError');
															$(
																	'#errUpperLimitNotRed')
																	.html('');
														}

														if (($.inArray(pRate,
																arrMap) != -1)
																&& (oldRate != newRate)) {
															$('#dialogValue')
																	.addClass(
																			'fieldError');
															$('#errValue')
																	.html(
																			'Duplicate Rate !');
														} else {
															$('#dialogValue')
																	.removeClass(
																			'fieldError');
															$('#errValue')
																	.html('');
														}
														throw $break;
													}
												});

								if (row.index < 0) {
									subTable.addRow("prd.subFees", row);
								} else {
									subTable.updateRow(row);
								}
							}

							if (row.type == TYPE_REDEMPTION) {
								if (row.index < 0) {
									redTable.addRow("prd.redFees", row);
								} else {
									redTable.updateRow(row);
								}
							}

							if (row.type == TYPE_SWITCHING) {
								$("#swiFeeTable tbody tr td")
										.each(
												function(html) {
													var value = $.trim($(this)
															.html());
													arrMap[arrMap.length] = value;

													if ((($.inArray(pValue,
															arrMap) != -1) && (oldValue != newValue))
															|| (($.inArray(
																	pRate,
																	arrMap) != -1) && (oldRate != newRate))) {
														if (($.inArray(pValue,
																arrMap) != -1)
																&& (oldValue != newValue)) {
															$(
																	'#dialogUpperLimit')
																	.addClass(
																			'fieldError');
															$(
																	'#errUpperLimitNotRed')
																	.html(
																			'Duplicate Value!');
														} else {
															$(
																	'#dialogUpperLimit')
																	.removeClass(
																			'fieldError');
															$(
																	'#errUpperLimitNotRed')
																	.html('');
														}

														if (($.inArray(pRate,
																arrMap) != -1)
																&& (oldRate != newRate)) {
															$('#dialogValue')
																	.addClass(
																			'fieldError');
															$('#errValue')
																	.html(
																			'Duplicate Rate !');
														} else {
															$('#dialogValue')
																	.removeClass(
																			'fieldError');
															$('#errValue')
																	.html('');
														}
														throw $break;
													}
												});
								if (row.index < 0) {
									swiTable.addRow("prd.swiFees", row);
								} else {
									swiTable.updateRow(row);
								}
							}

							$("#dialogTier").dialog('close');
							$('#errUpperLimit').html('');
							$('#errValue').html('');

						});

		// prd.btnBankAccountAdd.click(function(){
		// prd.dialogBankAccount.dialog('open');
		// $("#dialogBankAccount :text").val("");
		// $("#dialogBankAccount :hidden").val("");
		// $("#dialogBankAccount").find("span[id*='Error']").html("");
		// $("#btnBankAccountAddError").html("");
		// });

		// $("#addBankDialog").click(function(){
		// if ((prd.bankCode.val() == "") || (prd.bankAccountNo.val()=="") || (
		// prd.accountHolderName.val()=="") || (prd.bankBranch.val() == "")){
		// $("#dialogBankAccount").find("span[id*='Error']").html("");
		//				
		// if (prd.bankCode.val() == "") {
		// $("#bankCodeError").html('Required').show();
		// }
		// if (prd.bankBranch.val() == "") {
		// $("#bankBranchError").html('Required').show();
		// }
		// if (prd.bankAccountNo.val()=="") {
		// $("#bankAccountNoError").html('Required').show();
		// }
		// if (prd.accountHolderName.val()=="") {
		// $("#accountHolderNameError").html('Required').show();
		// }
		// } else {
		// var row = $("#rowNumber").val();
		// if(row != "") {
		// var data = new Object();
		// data.thirdPartyCode = prd.bankCode.val();
		// data.thirdPartyKey = prd.bankCodeKey.val();
		// data.thirdPartyName = prd.bankCodeDesc.val();
		// data.bankAccountNo = prd.bankAccountNo.val();
		// data.description = prd.accountHolderName.val();
		// data.bankAccountKey = prd.bankAccountKey.val();
		// data.productCode = prd.productCode.val();
		// data.bankBranch = prd.bankBranch.val();
		// var constantDefaultRgProdBankAccount = prd.bankCodeKey.val()+
		// prd.bankAccountNo.val();
		// data.defaultRgProdBankAccount =
		// constantDefaultRgProdBankAccount.split(' ').join('_');
		// if (prd.oldDefaultRgProdBankAccount.val() ==
		// prd.defaultProductBankAccount.val()) {
		// prd.defaultProductBankAccount.val(data.defaultRgProdBankAccount);
		// }
		//						
		// var checkExist = prd.bankCodeKey.val() + prd.bankAccountNo.val();
		// var found = false;
		// var exist = bankInfoTable.isExist(checkExist.split(' ').join('_'));
		// if ((exist != null) && (prd.oldDefaultRgProdBankAccount.val() !=
		// data.defaultRgProdBankAccount )) {
		// $("#dialogBankAccount #btnBankAccountAddError").html(" already exist
		// ").show();
		// found = true;
		// }
		//						
		// if (!found) {
		// bankInfoTable.updateRowCol1(data,row);
		// bankInfoTable.updateRowCol2(data,row);
		// bankInfoTable.updateRowCol3(data,row);
		// bankInfoTable.updateRowCol4(data,row);
		// bankInfoTable.updateRowCol5(data,row);
		// prd.dialogBankAccount.dialog('close');
		// }
		// } else {
		// var checkExist = prd.bankCodeKey.val() + prd.bankAccountNo.val();
		// var found = false;
		// var exist = bankInfoTable.isExist(checkExist.split(' ').join('_'));
		// if (exist != null) {
		// $("#dialogBankAccount #btnBankAccountAddError").html(" already exist
		// ").show();
		// found = true;
		// }
		// if (!found) {
		// var data = new Object();
		// data.bankAccountNo = prd.bankAccountNo.val();
		// data.description = prd.accountHolderName.val();
		// data.thirdPartyCode = prd.bankCode.val();
		// data.thirdPartyKey = prd.bankCodeKey.val();
		// data.thirdPartyName = prd.bankCodeDesc.val();
		// data.productCode = prd.productCode.val();
		// data.bankBranch = prd.bankBranch.val();
		// var constantDefaultRgProdBankAccount = prd.bankCodeKey.val()+
		// prd.bankAccountNo.val();
		// data.defaultRgProdBankAccount =
		// constantDefaultRgProdBankAccount.split(' ').join('_');
		// bankInfoTable.addRow(data);
		// prd.dialogBankAccount.dialog('close');
		// }
		// }
		// }
		// });

		// $("#addBankDialog").click(function(){
		// if (prd.bankAccountNo.val() == "")
		// {
		// $("#dialogBankAccount").find("span[id*='Error']").html("");
		//
		// if (prd.bankAccountNo.val()=="") {
		// $("#bankAccountNoError").html('Required').show();
		// }
		// }
		// else
		// {
		// var row = $("#rowNumber").val();
		// if(row != "")
		// {
		// var data = new Object();
		// data.bankAccountKey = prd.bankAccountKey.val();
		// data.bankAccountNo = prd.bankAccountNo.val();
		// data.thirdPartyKey = prd.thirdPartyKey.val();
		// data.thirdPartyCode = prd.thirdPartyCode.val();
		// data.thirdPartyName = prd.thirdPartyName.val();
		// data.beneficiaryName = prd.beneficiaryName.val();
		// data.currencyCode = prd.currencyCode.val();
		// data.productCode = prd.productCode.val();
		//
		// var constantDefaultRgProductBankAccount = prd.thirdPartyKey.val() +
		// prd.bankAccountNo.val();
		// data.defaultRgProductBankAccount =
		// constantDefaultRgProductBankAccount.split(' ').join('_');
		// if (prd.oldDefaultRgProductBankAccount.val() ==
		// prd.defaultProductBankAccount.val())
		// {
		// prd.defaultProductBankAccount.val(data.defaultRgProductBankAccount);
		// }
		//
		// var found = false;
		// var exist = bankInfoTable.isExist(prd.thirdPartyCode);
		// if ((exist != null) && (prd.oldDefaultRgProductBankAccount.val() !=
		// data.defaultRgProductBankAccount))
		// {
		// $("#dialogBankAccount #btnBankAccountAddError").html(" already exist
		// ").show();
		// found = true;
		// }
		//
		// if(!found)
		// {
		// bankInfoTable.updateRowCol1(data, row);
		// bankInfoTable.updateRowCol2(data, row);
		// bankInfoTable.updateRowCol3(data, row);
		// bankInfoTable.updateRowCol4(data, row);
		// bankInfoTable.updateRowCol5(data, row);
		// bankInfoTable.updateRowCol6(data, row);
		// prd.dialogBankAccount.dialog('close');
		// }
		// }
		// else
		// {
		// var found = false;
		// var exist = bankInfoTable.isExist(prd.thirdPartyCode);
		// if (exist != null)
		// {
		// $("#dialogBankAccount #btnBankAccountAddError").html(" already exist
		// ").show();
		// found = true;
		// }
		//
		// var currencyProduct = prd.currencyKey.val();
		// var currencyBankAccount = prd.currencyCode.val()
		// if (currencyProduct != currencyBankAccount)
		// {
		// $("#dialogBankAccount #btnBankAccountAddError").html(" currency bank
		// account must be same with currency product ").show();
		// found = true;
		// }
		//					
		// if (!found)
		// {
		// var data = new Object();
		// data.bankAccountKey = prd.bankAccountKey.val();
		// data.bankAccountNo = prd.bankAccountNo.val();
		// data.thirdPartyKey = prd.thirdPartyKey.val();
		// data.thirdPartyCode = prd.thirdPartyCode.val();
		// data.thirdPartyName = prd.thirdPartyName.val();
		// data.beneficiaryName = prd.beneficiaryName.val();
		// data.currencyCode = prd.currencyCode.val();
		// data.productCode = prd.productCode.val();
		//						
		// var constantDefaultRgProductBankAccount = prd.thirdPartyKey.val()+
		// prd.bankAccountNo.val();
		// data.defaultRgProductBankAccount =
		// constantDefaultRgProductBankAccount.split(' ').join('_');
		// bankInfoTable.addRow(data);
		// prd.dialogBankAccount.dialog('close');
		// }
		// }
		// }
		// });
		//
		// $("#cancelBankDialog").click(function(){
		// prd.dialogBankAccount.dialog('close');
		// });

		prd.divInvestorOption.change(function() {
			if (prd.divInvestorOption.is(":checked")) {
				prd.divInvestorOption.val(true);
				prd.divIopByCashPct.enabled();
				prd.divIopByReivenstPct.enabled();
				prd.divIopByRedeemPct.enabled();
			} else {
				prd.divIopByCashPct.removeClass('fieldError');
				prd.divIopByReivenstPct.removeClass('fieldError');
				prd.divIopByRedeemPct.removeClass('fieldError');
				$('#defaultInvestortypeError').html("");
				$('.defaultInvtOpt').html("");
				prd.divInvestorOption.val(false);
				prd.divIopByCashPct.disabled().value("");
				prd.divIopByReivenstPct.disabled().value("");
				prd.divIopByRedeemPct.disabled().value("");
			}
			// alert($('#divInvestorOption').val());
		});

		// by default set to disabled
		if (!prd.divInvestorOption.is(":checked")) {
			prd.divIopByCashPct.disabled().value("");
			prd.divIopByReivenstPct.disabled().value("");
			prd.divIopByRedeemPct.disabled().value("");
		}

		prd.divIopByCashPct.change(function() {
			sumDefaultInvestorOption();
		});

		prd.divIopByReivenstPct.change(function() {
			sumDefaultInvestorOption();
		});

		prd.divIopByRedeemPct.change(function() {
			sumDefaultInvestorOption();
		});

		$("#radioStatus${prd?.defaultProductBankAccountSub}").attr("checked", "checked");

		prd.subAddDate.click(function() {
			var checkError = $('#subExceptionDate').hasClass('fieldError');
			if (checkError) {
				return false;
			} else {
				if (prd.subExceptionDate.val() != "") {
					// alert(TYPE_DIVIDEND);
					var row = new Object();
					row.exceptionDate = prd.subExceptionDate.val();
					row.productCode = prd.productCode.val();
					row.type = TYPE_SUBSCRIBE;
					var exceptionDate = new Date(row.exceptionDate).getTime();
					var table = $("#subExceptionSpecificDate").dataTable();
					var rows = table.fnGetNodes().length;
					if (rows == 0) {
						prd.subExceptionDate.removeClass('fieldError');
						$('#subExceptionDateError').html("");
						subExceptionSpecificDate.addRow(
								"prd.subLockExceptions", row);
						prd.subExceptionDate.val("");
						return false;
					}
					var checkDate = true;
					for ( var i = 0; i < rows; i++) {
						var cell = table.fnGetData(i);
						var tableExceptionDate = new Date(cell[0]).getTime();
						if (exceptionDate == tableExceptionDate) {
							// prd.subExceptionDate.addClass('fieldError');
							// $('#subExceptionDateError').html("Already
							// Exist");
							prd.subExceptionDate.val("");
							checkDate = false;
							break;
						}
					}
					if (checkDate) {
						prd.subExceptionDate.removeClass('fieldError');
						$('#subExceptionDateError').html("");
						subExceptionSpecificDate.addRow(
								"prd.subLockExceptions", row);
						prd.subExceptionDate.val("");
					}
				}
			}
		});

		prd.redAddDate.click(function() {
			var checkError = $('#redExceptionDate').hasClass('fieldError');
			if (checkError) {
				return false;
			} else {
				if (prd.redExceptionDate.val() != "") {
					var row = new Object();
					row.exceptionDate = prd.redExceptionDate.val();
					row.productCode = prd.productCode.val();
					row.type = TYPE_REDEMPTION;
					var exceptionDate = new Date(row.exceptionDate).getTime();
					var table = $("#redExceptionSpecificDate").dataTable();
					var rows = table.fnGetNodes().length;
					if (rows == 0) {
						prd.redExceptionDate.removeClass('fieldError');
						$('#redExceptionDateError').html("");
						redExceptionSpecificDate.addRow(
								"prd.redLockExceptions", row);
						prd.redExceptionDate.val("");
						return false;
					}
					var checkDate = true;
					for ( var i = 0; i < rows; i++) {
						var cell = table.fnGetData(i);
						var tableExceptionDate = new Date(cell[0]).getTime();
						if (exceptionDate == tableExceptionDate) {
							// prd.redExceptionDate.addClass('fieldError');
							// $('#redExceptionDateError').html("Already
							// Exist");
							prd.redExceptionDate.val("");
							checkDate = false;
							break;
						}
					}
					if (checkDate) {
						prd.redExceptionDate.removeClass('fieldError');
						$('#redExceptionDateError').html("");
						redExceptionSpecificDate.addRow(
								"prd.redLockExceptions", row);
						prd.redExceptionDate.val("");
					}
				}
			}
		});

		prd.swiAddDate.click(function() {
			var checkError = $('#swiExceptionDate').hasClass('fieldError');
			if (checkError) {
				return false;
			} else {
				if (prd.swiExceptionDate.val() != "") {
					var row = new Object();
					row.exceptionDate = prd.swiExceptionDate.val();
					row.productCode = prd.productCode.val();
					row.type = TYPE_SWITCHING;
					var exceptionDate = new Date(row.exceptionDate).getTime();
					var table = $("#swiExceptionSpecificDate").dataTable();
					var rows = table.fnGetNodes().length;
					if (rows == 0) {
						prd.swiExceptionDate.removeClass('fieldError');
						$('#swiExceptionDateError').html("");
						swiExceptionSpecificDate.addRow(
								"prd.swiLockExceptions", row);
						prd.swiExceptionDate.val("");
						return false;
					}
					var checkDate = true;
					for ( var i = 0; i < rows; i++) {
						var cell = table.fnGetData(i);
						var tableExceptionDate = new Date(cell[0]).getTime();
						if (exceptionDate == tableExceptionDate) {
							// prd.swiExceptionDate.addClass('fieldError');
							// $('#swiExceptionDateError').html("Already
							// Exist");
							prd.swiExceptionDate.val("");
							checkDate = false;
							break;
						}
					}
					if (checkDate) {
						prd.swiExceptionDate.removeClass('fieldError');
						$('#swiExceptionDateError').html("");
						swiExceptionSpecificDate.addRow(
								"prd.swiLockExceptions", row);
						prd.swiExceptionDate.val("");
					}
				}
			}
		});

		prd.divAddDate.click(function() {
			var checkError = $('#divExceptionDate').hasClass('fieldError');
			if (checkError) {
				return false;
			} else {
				if (prd.divExceptionDate.val() != "") {
					var row = new Object();
					row.exceptionDate = prd.divExceptionDate.val();
					row.productCode = prd.productCode.val();
					row.type = TYPE_DIVIDEND;
					var exceptionDate = new Date(row.exceptionDate).getTime();
					var table = $("#divExceptionSpecificDate").dataTable();
					var rows = table.fnGetNodes().length;
					if (rows == 0) {
						prd.divExceptionDate.removeClass('fieldError');
						$('#divExceptionDateError').html("");
						divExceptionSpecificDate.addRow(
								"prd.divLockExceptions", row);
						prd.divExceptionDate.val("");
						return false;
					}
					var checkDate = true;
					for ( var i = 0; i < rows; i++) {
						var cell = table.fnGetData(i);
						var tableExceptionDate = new Date(cell[0]).getTime();
						if (exceptionDate == tableExceptionDate) {
							// prd.divExceptionDate.addClass('fieldError');
							// $('#divExceptionDateError').html("Already
							// Exist");
							prd.divExceptionDate.val("");
							checkDate = false;
							break;
						}
					}
					if (checkDate) {
						prd.divExceptionDate.removeClass('fieldError');
						$('#divExceptionDateError').html("");
						divExceptionSpecificDate.addRow(
								"prd.divLockExceptions", row);
						prd.divExceptionDate.val("");
					}
				}
			}
		});

		prd.subLock1.add(prd.subLock2).click(
				function() {
					var typeLock1 = prd.subLock1;
					var typeLock2 = prd.subLock2;
					var typeExceptionDate = prd.subExceptionDate;
					var typeAddButton = prd.subAddDate;
					var idError = $('#subExceptionDateError');
					var typeTable = prd.subExceptionSpecificDate;
					console.debug(typeTable);
					lock(typeLock1, typeLock2, typeExceptionDate,
							typeAddButton, idError, typeTable);
				});

		prd.redLock1.add(prd.redLock2).click(
				function() {
					var typeLock1 = prd.redLock1;
					var typeLock2 = prd.redLock2;
					var typeExceptionDate = prd.redExceptionDate;
					var typeAddButton = prd.redAddDate;
					var idError = $('#redExceptionDateError');
					var typeTable = prd.redExceptionSpecificDate;
					lock(typeLock1, typeLock2, typeExceptionDate,
							typeAddButton, idError, typeTable);
				});

		prd.swiLock1.add(prd.swiLock2).click(
				function() {
					var typeLock1 = prd.swiLock1;
					var typeLock2 = prd.swiLock2;
					var typeExceptionDate = prd.swiExceptionDate;
					var typeAddButton = prd.swiAddDate;
					var idError = $('#swiExceptionDateError');
					var typeTable = prd.swiExceptionSpecificDate;
					lock(typeLock1, typeLock2, typeExceptionDate,
							typeAddButton, idError, typeTable);
				});

		prd.divLock1.add(prd.divLock2).click(
				function() {
					var typeLock1 = prd.divLock1;
					var typeLock2 = prd.divLock2;
					var typeExceptionDate = prd.divExceptionDate;
					var typeAddButton = prd.divAddDate;
					var idError = $('#divExceptionDateError');
					var typeTable = prd.divExceptionSpecificDate;
					lock(typeLock1, typeLock2, typeExceptionDate,
							typeAddButton, idError, typeTable);
				});

		// prd.addSubFee.click(function(){ subTable.addRow("prd.subFees"); });
		// prd.delSubFee.click(function(){ subTable.delRow(); });

		// prd.addRedFee.click(function(){ redTable.addRow("prd.redFees"); });
		// prd.delRedFee.click(function(){ redTable.delRow(); });

		// prd.addSwiFee.click(function(){ swiTable.addRow("prd.swiFees"); });
		// prd.delSwiFee.click(function(){ swiTable.delRow(); });

		// prd.addDivFee.click(function(){ divTable.addRow("prd.divFees"); });
		// prd.delDivFee.click(function(){ divTable.delRow(); });

		/*
		 * ===========================================================================
		 * Initilize
		 * =========================================================================
		 */
		$(".buttons input:button", html).button();
		html.clazz('calendar').datepicker();

		prd.tabs.tabs();
		prd.tabs.css('height', '500');

		prd.tabsBankAccount.tabs();
		prd.tabsBankAccount.css('height', 'auto');

		$("#customerNo").dynapopup(
				'PICK_CF_MASTER',
				'',
				'fundManager',
				function(data) {
					$("#listBankAccountSub #bankAccountTableSub").dataTable().fnClearTable();
					$("#listBankAccountRed #bankAccountTableRed").dataTable().fnClearTable();
					if (data.customerKey != $("#customerNoKey").val()) {
						$("#bankAccountKey").val("");
						$("#defaultProductBankAccount").val("");
					}

					$('#customerNo').removeClass('fieldError');
					$('#customerNoDesc').val(data.description);
					$('#h_customerNoDesc').val(data.description);
					$('#customerNo').val(data.customerNo);
					$('#customerNoKey').val(data.customerKey);
				}, function() {
					$("#listBankAccountSub #bankAccountTableSub").dataTable().fnClearTable();
					$("#listBankAccountRed #bankAccountTableRed").dataTable().fnClearTable();
					$('#customerNo').addClass('fieldError');
					$('#customerNo').val('');
					$('#customerNoKey').val('');
					$('#customerNoDesc').val('NOT FOUND');
					$('#h_customerNoDesc').val('');
				});

		/*
		 * //prd.customerNo.popupCustomer(''); $('#customerNo').lookup({ list :
		 * '@{Pick.customers()}', get : { url: '@{Pick.customer()}', success:
		 * function(data) { if(data) { if(data.customerKey !=
		 * $("#customerNoKey").val()) { $("#listBankAccount
		 * #bankAccountTable").dataTable().fnClearTable();
		 * $("#bankAccountKey").val("");
		 * $("#defaultProductBankAccount").val(""); }
		 * 
		 * $('#customerNo').removeClass('fieldError');
		 * $('#customerNoDesc').val(data.description);
		 * $('#h_customerNoDesc').val(data.description);
		 * $('#customerNo').val(data.customerNo);
		 * $('#customerNoKey').val(data.customerKey); } }, error: function(data) {
		 * $('#customerNo').addClass('fieldError'); $('#customerNo').val('');
		 * $('#customerNoKey').val(''); $('#customerNoDesc').val('NOT FOUND');
		 * $('#h_customerNoDesc').val(''); } }, help : $('#customerNoHelp'),
		 * nextControl: $('#fundManager') });
		 */

		prd.customerNo.blur(function() {
			if (prd.customerNo.val() != null) {
				$('#btnBankAccountAddError').html("");
			} else {
				$("#listBankAccountSub #bankAccountTableSub").dataTable().fnClearTable();
				$("#listBankAccountRed #bankAccountTableRed").dataTable().fnClearTable();
				$('#customerNo').val("");
				$('#customerNoDesc').val("");
				$('#customerNoKey').val("");
				$('#h_customerNoDesc').val("");
			}
		});

		prd.btnBankAccountAdd.button();
		prd.btnBankAccountAdd.css('width', '100');

		prd.bankCode.popupThirdParties("?type=THIRD_PARTY-BANK");

		$("#fundManager").dynapopup('PICK_GN_THIRD_PARTY',
				'THIRD_PARTY-FUND_MANAGER', 'custodianBank', function(data) {
					$("#fundManager").removeClass('fieldError');
				}, function() {
					$("#fundManager").addClass('fieldError');
					$("#fundManagerDesc").val('NOT FOUND');
					$("#fundManagerKey").val('');
					$("#fundManager").val('');
					$("#h_fundManagerDesc").val('');
				});

		// prd.fundManager.popupThirdParties("?type=THIRD_PARTY-FUND_MANAGER",
		// "custodianBank", function(data){
		// prd.fundManager.removeClass('fieldError');
		// },
		// function(data){
		// prd.fundManager.addClass('fieldError');
		// prd.fundManagerDesc.val('NOT FOUND');
		// prd.fundManagerKey.val('');
		// prd.fundManager.val('');
		// prd.h_fundManagerDesc.val('');
		// });

		// prd.custodianBank.popupThirdParties("?type=THIRD_PARTY-CUSTODIAN",
		// "type", function(data){
		// prd.custodianBank.removeClass('fieldError');
		// },
		// function(data){
		// prd.custodianBank.addClass('fieldError');
		// prd.custodianBankKey.val('');
		// prd.custodianBankDesc.val('NOT FOUND');
		// prd.h_custodianBankDesc.val('');
		// prd.custodianBank.val('');
		// });

		prd.type.popupLookup("?group=FUND_TYPE", "clazz", function(data) {
			prd.type.removeClass('fieldError');
		}, function(data) {
			prd.type.addClass('fieldError');
			prd.typeKey.val('');
			prd.typeDesc.val('NOT FOUND');
			prd.h_typeDesc.val('');
			prd.type.val('');
		});

		prd.clazz.popupLookup("?group=FUND_CLASS", "currency", function(data) {
			prd.clazz.removeClass('fieldError');
		}, function(data) {
			prd.clazz.addClass('fieldError');
			prd.clazzKey.val('');
			prd.clazzDesc.val('NOT FOUND');
			prd.h_clazzDesc.val('');
			prd.clazz.val('');
		});

		// prd.currency.popupCurrencies("subMinAmt", function(data){
		// prd.currency.removeClass('fieldError');
		// }, function(data){
		// prd.currency.addClass('fieldError');
		// prd.currencyKey.val('');
		// prd.currencyDesc.val('NOT FOUND');
		// prd.h_currencyDesc.val('');
		// prd.currency.val('');
		// });

		// --Lookup currency
		$('#currency').lookup(
				{
					list : '@{Pick.currencies()}',
					get : {
						url : '@{Pick.currency()}',
						success : function(data) {
							if (data.code != prd.currencyKey.val()) {
								$("#listBankAccountSub #bankAccountTableSub").dataTable().fnClearTable();
								$("#listBankAccountRed #bankAccountTableRed").dataTable().fnClearTable();
								$("#bankAccountKey").val("");
								$("#defaultProductBankAccount").val("");
							}

							$('#currency').removeClass('fieldError');
							$('#currencyDesc').val(data.description);
							$('#h_currencyDesc').val(data.description);
							$('#currencyKey').val(data.code);
						},
						error : function(data) {
							$('#currency').addClass('fieldError');
							$('#currency').val('');
							$('#currencyKey').val('');
							$('#currencyDesc').val('NOT FOUND');
							$('#h_currencyDesc').val('');
						}
					},
					description : $('#currencyDesc'),
					help : $('#currencyHelp')
				});

		prd.currency.blur(function() {
			if (prd.currency.val() == '') {
				$('#currency').val("");
				$('#currencyDesc').val("");
				$('#currencyKey').val("");
				$('#h_currencyDesc').val('');
			}
		});

		prd.subTax.popupTax("addSubFee", function(data) {
			prd.subTax.removeClass('fieldError');
		}, function(data) {
			prd.subTax.addClass('fieldError');
			prd.subTaxKey.val('');
			prd.subTaxDesc.val('NOT FOUND');
			prd.h_subTaxDesc.val('');
			prd.subTax.val('');
		});

		prd.redTax.popupTax("addRedFee", function(data) {
			prd.redTax.removeClass('fieldError');
		}, function(data) {
			prd.redTax.addClass('fieldError');
			prd.redTaxKey.val('');
			prd.redTaxDesc.val('NOT FOUND');
			prd.h_redTaxDesc.val('');
			prd.redTax.val('');
		});

		prd.swiTax.popupTax("addSwiFee", function(data) {
			prd.swiTax.removeClass('fieldError');
		}, function(data) {
			prd.swiTax.addClass('fieldError');
			prd.swiTaxKey.val('');
			prd.swiTaxDesc.val('NOT FOUND');
			prd.h_swiTaxDesc.val('');
			prd.swiTax.val('');
		});

		/*
		 * prd.divTax.popupTax("addDivFee", function(data){
		 * prd.divTax.removeClass('fieldError'); }, function(data){
		 * prd.divTax.addClass('fieldError'); prd.divTaxKey.val('');
		 * prd.divTaxDesc.val('NOT FOUND'); prd.h_divTaxDesc.val('');
		 * prd.divTax.val(''); });
		 */
		prd.fixnav1.add(prd.fixnav2).click(function() {
			fixPriceStatus();
		});

		// prd.subMinAmt.change(function(){
		// prd.subMinAmt.value(prd.subMinAmt.val(), true);
		// });

		// prd.minBal.change(function(){
		// var value = prd.minBal.rdchecked();
		// if (value == "amount") {
		// prd.minBalAmt.enabled();
		// prd.minBalUnit.disabled().value("");
		// $('#errMinBalUnit').html('');
		// }
		// if (value == "unit") {
		// prd.minBalUnit.enabled();
		// prd.minBalAmt.disabled().value("");
		// $('#errMinBalAmt').html('');
		// }
		// });

		prd.totRedeem.change(function() {
			var value = prd.totRedeem.rdchecked();
			if (value == "amount") {
				prd.redLimitAmt.enabled();
				prd.redLimitPct.disabled().value("");
			}
			if (value == "unit") {
				prd.redLimitPct.enabled();
				prd.redLimitAmt.disabled().value("");
			}
		});

		prd.totSwitch.change(function() {
			var value = prd.totSwitch.rdchecked();
			if (value == "amount") {
				prd.swoLimitAmt.enabled();
				prd.swoLimitPct.disabled().value("");
			}
			if (value == "unit") {
				prd.swoLimitPct.enabled();
				prd.swoLimitAmt.disabled().value("");
			}
		});

		prd.liquidDate.change(function() {
			if (!(prd.liquidDate.hasClass('fieldError'))) {
				liquidDateValidate();
			}
			return false;
		});

		prd.effectiveDate.change(function() {
			if (!(prd.liquidDate.hasClass('fieldError'))) {
				liquidDateValidate();
			}
			return false;
		});

		// if (prd.isCheckedSub.val() == 'true'){
		// prd.isCheckedSub.attr("checked", true);
		// }

		if (prd.isCheckedSub.is(":checked")) {
			prd.isCheckedSub.val(true);
		} else {
			prd.isCheckedSub.val(false);
		}

		$("#spanSubMaxAmt").html("");
		prd.isCheckedSub.change(function() {
			var el = $("#subMaxAmt");
			if (prd.isCheckedSub.is(":checked")) {
				prd.isCheckedSub.val(true);
			} else {
				prd.isCheckedSub.val(false);
			}
			if ((prd.isCheckedSub.val() == 'true')
					|| (prd.subMaxAmt.hasClass('fieldError'))) {
				el.removeClass('fieldError');
				$("#errmsgMaxSub").html('');
				prd.subMaxAmt.disabled();
				prd.subMaxAmt.val('');
				prd.subMaxAmtStripped.val('');
				$("#spanSubMaxAmt").html("");
			} else {
				prd.subMaxAmt.enabled();
				$("#spanSubMaxAmt").html(" *");
			}
		});

		prd.subMaxAmt.blur(function() {
			var el = $(this);
			var errMsg = $("#errmsgMaxSub");
			var min = parseFloat(prd.subMinAmtStripped.val());
			var max = parseFloat(prd.subMaxAmtStripped.val());
			validateMinMax(min, max, el, errMsg);
		});

		prd.subMinAmt.blur(function() {
			var el = $("#subMaxAmt");
			var errMsg = $("#errmsgMaxSub");
			var min = parseFloat(prd.subMinAmtStripped.val());
			var max = parseFloat(prd.subMaxAmtStripped.val());
			validateMinMax(min, max, el, errMsg);
		});

		// if (prd.isCheckedSubFee.val()=='true'){
		// prd.isCheckedSubFee.attr("checked", true);
		// }

		// if (prd.isCheckedSubFee.is(":checked")){
		// prd.isCheckedSubFee.val(true);
		// } else {
		// prd.isCheckedSubFee.val(false);
		// }

		// $("#spanSubMaxFee").html("");
		// prd.isCheckedSubFee.change(function () {
		// var el = $("#subMaxFee");
		// if (prd.isCheckedSubFee.is(":checked")){
		// prd.isCheckedSubFee.val(true);
		// } else {
		// prd.isCheckedSubFee.val(false);
		// }
		// if ((prd.isCheckedSubFee.val() ==
		// "true")||(prd.subMaxFee.hasClass('fieldError'))){
		// el.removeClass('fieldError');
		// $("#errmsgMaxSubFee").hide();
		// prd.subMaxFee.disabled();
		// prd.subMaxFee.val('');
		// prd.subMaxFeeStripped.val('');
		// $("#spanSubMaxFee").html("");
		// } else {
		// prd.subMaxFee.enabled();
		// $("#spanSubMaxFee").html(" *");
		// }
		// });

		// prd.subMaxFee.blur(function(){
		// var el = $(this);
		// var errMsg = $("#errmsgMaxSubFee");
		// var min = parseFloat(prd.subMinFeeStripped.val());
		// var max = parseFloat(prd.subMaxFeeStripped.val());
		// validateMinMax(min, max, el, errMsg);
		// });

		// prd.subMinFee.blur(function(){
		// var el = $("#subMaxFee");
		// var errMsg = $("#errmsgMaxSubFee");
		// var min = parseFloat(prd.subMinFeeStripped.val());
		// var max = parseFloat(prd.subMaxFeeStripped.val());
		// validateMinMax(min, max, el, errMsg);
		// });

		function validateMaxFeeTier(value, maxValue, el, errMsg) {
			if (new Number(value) > new Number(maxValue)) {
				$(el).addClass('fieldError');
				$(errMsg).html("Maximum Fee must be greater than Default Fee");
				return false;
			} else {
				$(el).removeClass('fieldError');
				$(errMsg).html("");
			}
		}
		;

		// ** Start Subscription Validation **//
		$("input[id='valueSub[0]']").autoNumeric({
			vMax : '100'
		});
		$("input[id='valueSub[0]']").attr("style", "text-align:right;");
		$("input[id='valueSub[0]']").live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		$("input[id='maxValueSub[0]']").autoNumeric({
			vMax : '100'
		});
		$("input[id='maxValueSub[0]']").attr("style", "text-align:right;");
		$("input[id='maxValueSub[0]']").live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		if ($("input[id='maxValueSub[0]']").isEmpty()) {
			if ($("#isCheckedSubMaxFeeError").html() != "Required") {
				$("input[id='isCheckedSubMaxValue[0]']").attr("checked", true);
				$("input[id='isCheckedSubMaxValue[0]Stripped']").val(true);
				$("input[id='maxValueSub[0]']").disabled();
			} else {
				$("input[id='maxValueSub[0]']").enabled();
				$("input[id='isCheckedSubMaxValue[0]']").attr("checked", false);
				$("input[id='isCheckedSubMaxValue[0]Stripped']").val(false);
			}
		}

		if ($("input[id='isCheckedSubMaxValue[0]Stripped']").isEmpty()) {
			if ($("input[id='maxValueSub[0]Stripped']").isEmpty()) {
				$("input[id='maxValueSub[0]']").disabled();
				$("input[id='isCheckedSubMaxValue[0]']").attr("checked", true);
				$("input[id='isCheckedSubMaxValue[0]Stripped']").val(true);
			} else {
				if (('${mode}' == 'view') || ('${confirming}' == 'true')) {
					$("input[id='maxValueSub[0]']").disabled();
					$("input[id='isCheckedSubMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedSubMaxValue[0]Stripped']").val(false);
				} else {
					$("input[id='maxValueSub[0]']").enabled();
					$("input[id='isCheckedSubMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedSubMaxValue[0]Stripped']").val(false);
				}
			}
		} else {
			if ($("input[id='isCheckedSubMaxValue[0]Stripped']").val() == "true") {
				$("input[id='maxValueSub[0]']").disabled();
				$("input[id='isCheckedSubMaxValue[0]']").attr("checked", true);
				$("input[id='isCheckedSubMaxValue[0]Stripped']").val(true);
			} else {
				if (('${mode}' == 'view') || ('${confirming}' == 'true')) {
					$("input[id='maxValueSub[0]']").disabled();
					$("input[id='isCheckedSubMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedSubMaxValue[0]Stripped']").val(false);
				} else {
					$("input[id='maxValueSub[0]']").enabled();
					$("input[id='isCheckedSubMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedSubMaxValue[0]Stripped']").val(false);
				}
			}
		}

		$("#spanSubFeeTierMaxValue").html("");
		$("input[id='isCheckedSubMaxValue[0]']")
				.change(
						function() {
							var elCheckedSubMaxValue = $("input[id='isCheckedSubMaxValue[0]']");
							var elCheckedSubMaxValueStripped = $("input[id='isCheckedSubMaxValue[0]Stripped']");
							var elMaxValue = $("input[id='maxValueSub[0]']");
							var elMaxValueStripped = $("input[id='maxValueSub[0]Stripped']");

							if (elCheckedSubMaxValue.is(":checked")) {
								elCheckedSubMaxValue.val(true);
								elCheckedSubMaxValueStripped.val(true);
							} else {
								elMaxValue.val('');
								elMaxValueStripped.val('');
								elCheckedSubMaxValue.val(false);
								elCheckedSubMaxValueStripped.val(false);
							}

							if ((elCheckedSubMaxValue.val() == "true")
									|| (elMaxValue.hasClass('fieldError'))) {
								elMaxValue.removeClass('fieldError');
								elMaxValue.disabled();
								elMaxValue.val('');
								$("#spanSubFeeTierMaxValue").html("");
							} else {
								elMaxValue.enabled();
								$("#spanSubFeeTierMaxValue").html(" *");
							}
						});

		// $("input[id='valueSub[0]'],
		// input[id='maxValueSub[0]']").blur(function(){
		// var el = $(this);
		// var errMsg = $("#isCheckedSubMaxFeeError");
		//
		// var value =
		// $("input[id='valueSub[0]Stripped']").val($("input[id='valueSub[0]']").val());
		// var maxValue =
		// $("input[id='maxValueSub[0]Stripped']").val($("input[id='maxValueSub[0]']").val());
		//
		// if ($("input[id='isCheckedSubMaxValue[0]']").val() != "true")
		// {
		// validateMaxFeeTier(value.val(), maxValue.val(), el, errMsg);
		// }
		// });
		// ** End Subscription Validation **//

		// ** Start Redemption Validation **//
		$("input[id='valueRed[0]']").autoNumeric({
			vMax : '100'
		});
		$("input[id='valueRed[0]']").attr("style", "text-align:right;");
		$("input[id='valueRed[0]']").live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		$("input[id='maxValueRed[0]']").autoNumeric({
			vMax : '100'
		});
		$("input[id='maxValueRed[0]']").attr("style", "text-align:right;");
		$("input[id='maxValueRed[0]']").live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		if ($("input[id='maxValueRed[0]']").isEmpty()) {
			if ($("#isCheckedRedMaxFeeError").html() != "Required") {
				$("input[id='isCheckedRedMaxValue[0]']").attr("checked", true);
				$("input[id='isCheckedRedMaxValue[0]Stripped']").val(true);
				$("input[id='maxValueRed[0]']").disabled();
			} else {
				$("input[id='maxValueRed[0]']").enabled();
				$("input[id='isCheckedRedMaxValue[0]']").attr("checked", false);
				$("input[id='isCheckedRedMaxValue[0]Stripped']").val(false);
			}
		}

		if ($("input[id='isCheckedRedMaxValue[0]Stripped']").isEmpty()) {
			if ($("input[id='maxValueRed[0]Stripped']").isEmpty()) {
				$("input[id='maxValueRed[0]']").disabled();
				$("input[id='isCheckedRedMaxValue[0]']").attr("checked", true);
				$("input[id='isCheckedRedMaxValue[0]Stripped']").val(true);
			} else {
				if (('${mode}' == 'view') || ('${confirming}' == 'true')) {
					$("input[id='maxValueRed[0]']").disabled();
					$("input[id='isCheckedRedMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedRedMaxValue[0]Stripped']").val(false);
				} else {
					$("input[id='maxValueRed[0]']").enabled();
					$("input[id='isCheckedRedMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedRedMaxValue[0]Stripped']").val(false);
				}
			}
		} else {
			if ($("input[id='isCheckedRedMaxValue[0]Stripped']").val() == "true") {
				$("input[id='maxValueRed[0]']").disabled();
				$("input[id='isCheckedRedMaxValue[0]']").attr("checked", true);
				$("input[id='isCheckedRedMaxValue[0]Stripped']").val(true);
			} else {
				if (('${mode}' == 'view') || ('${confirming}' == 'true')) {
					$("input[id='maxValueRed[0]']").disabled();
					$("input[id='isCheckedRedMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedRedMaxValue[0]Stripped']").val(false);
				} else {
					$("input[id='maxValueRed[0]']").enabled();
					$("input[id='isCheckedRedMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedRedMaxValue[0]Stripped']").val(false);
				}
			}
		}

		$("#spanRedFeeTierMaxValue").html("");
		$("input[id='isCheckedRedMaxValue[0]']")
				.change(
						function() {
							var elCheckedRedMaxValue = $("input[id='isCheckedRedMaxValue[0]']");
							var elCheckedRedMaxValueStripped = $("input[id='isCheckedRedMaxValue[0]Stripped']");
							var elMaxValue = $("input[id='maxValueRed[0]']");
							var elMaxValueStripped = $("input[id='maxValueRed[0]Stripped']");

							if (elCheckedRedMaxValue.is(":checked")) {
								elCheckedRedMaxValue.val(true);
								elCheckedRedMaxValueStripped.val(true);
							} else {
								elMaxValue.val('');
								elMaxValueStripped.val('');
								elCheckedRedMaxValue.val(false);
								elCheckedRedMaxValueStripped.val(false);
							}

							if ((elCheckedRedMaxValue.val() == "true")
									|| (elMaxValue.hasClass('fieldError'))) {
								elMaxValue.removeClass('fieldError');
								elMaxValue.disabled();
								elMaxValue.val('');
								$("#spanRedFeeTierMaxValue").html("");
							} else {
								elMaxValue.enabled();
								$("#spanRedFeeTierMaxValue").html(" *");
							}
						});

		// $("input[id='valueRed[0]'],
		// input[id='maxValueRed[0]']").blur(function(){
		// var el = $(this);
		// var errMsg = $("#isCheckedRedMaxFeeError");
		//
		// var value =
		// $("input[id='valueRed[0]Stripped']").val($("input[id='valueRed[0]']").val());
		// var maxValue =
		// $("input[id='maxValueRed[0]Stripped']").val($("input[id='maxValueRed[0]']").val());
		//
		// if ($("input[id='isCheckedRedMaxValue[0]']").val() != "true")
		// {
		// validateMaxFeeTier(value.val(), maxValue.val(), el, errMsg);
		// }
		// });
		// ** End Redemption Validation **//

		// ** Start Switching Validation **//
		$("input[id='valueSwi[0]']").autoNumeric({
			vMax : '100'
		});
		$("input[id='valueSwi[0]']").attr("style", "text-align:right;");
		$("input[id='valueSwi[0]']").live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		$("input[id='maxValueSwi[0]']").autoNumeric({
			vMax : '100'
		});
		$("input[id='maxValueSwi[0]']").attr("style", "text-align:right;");
		$("input[id='maxValueSwi[0]']").live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		if ($("input[id='maxValueSwi[0]']").isEmpty()) {
			if ($("#isCheckedSwiMaxFeeError").html() != "Required") {
				$("input[id='isCheckedSwiMaxValue[0]']").attr("checked", true);
				$("input[id='isCheckedSwiMaxValue[0]Stripped']").val(true);
				$("input[id='maxValueSwi[0]']").disabled();
			} else {
				$("input[id='maxValueSwi[0]']").enabled();
				$("input[id='isCheckedSwiMaxValue[0]']").attr("checked", false);
				$("input[id='isCheckedSwiMaxValue[0]Stripped']").val(false);
			}
		}

		if ($("input[id='isCheckedSwiMaxValue[0]Stripped']").isEmpty()) {
			if ($("input[id='maxValueSwi[0]Stripped']").isEmpty()) {
				$("input[id='maxValueSwi[0]']").disabled();
				$("input[id='isCheckedSwiMaxValue[0]']").attr("checked", true);
				$("input[id='isCheckedSwiMaxValue[0]Stripped']").val(true);
			} else {
				if (('${mode}' == 'view') || ('${confirming}' == 'true')) {
					$("input[id='maxValueSwi[0]']").disabled();
					$("input[id='isCheckedSwiMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedSwiMaxValue[0]Stripped']").val(false);
				} else {
					$("input[id='maxValueSwi[0]']").enabled();
					$("input[id='isCheckedSwiMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedSwiMaxValue[0]Stripped']").val(false);
				}
			}
		} else {
			if ($("input[id='isCheckedSwiMaxValue[0]Stripped']").val() == "true") {
				$("input[id='maxValueSwi[0]']").disabled();
				$("input[id='isCheckedSwiMaxValue[0]']").attr("checked", true);
				$("input[id='isCheckedSwiMaxValue[0]Stripped']").val(true);
			} else {
				if (('${mode}' == 'view') || ('${confirming}' == 'true')) {
					$("input[id='maxValueSwi[0]']").disabled();
					$("input[id='isCheckedSwiMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedSwiMaxValue[0]Stripped']").val(false);
				} else {
					$("input[id='maxValueSwi[0]']").enabled();
					$("input[id='isCheckedSwiMaxValue[0]']").attr("checked",
							false);
					$("input[id='isCheckedSwiMaxValue[0]Stripped']").val(false);
				}
			}
		}

		$("#spanSwiFeeTierMaxValue").html("");
		$("input[id='isCheckedSwiMaxValue[0]']")
				.change(
						function() {
							var elCheckedSwiMaxValue = $("input[id='isCheckedSwiMaxValue[0]']");
							var elCheckedSwiMaxValueStripped = $("input[id='isCheckedSwiMaxValue[0]Stripped']");
							var elMaxValue = $("input[id='maxValueSwi[0]']");
							var elMaxValueStripped = $("input[id='maxValueSwi[0]Stripped']");

							if (elCheckedSwiMaxValue.is(":checked")) {
								elCheckedSwiMaxValue.val(true);
								elCheckedSwiMaxValueStripped.val(true);
							} else {
								elMaxValue.val('');
								elMaxValueStripped.val('');
								elCheckedSwiMaxValue.val(false);
								elCheckedSwiMaxValueStripped.val(false);
							}

							if ((elCheckedSwiMaxValue.val() == "true")
									|| (elMaxValue.hasClass('fieldError'))) {
								elMaxValue.removeClass('fieldError');
								elMaxValue.disabled();
								elMaxValue.val('');
								$("#spanSwiFeeTierMaxValue").html("");
							} else {
								elMaxValue.enabled();
								$("#spanSwiFeeTierMaxValue").html(" *");
							}
						});

		// $("input[id='valueSwi[0]'],
		// input[id='maxValueSwi[0]']").blur(function(){
		// var el = $(this);
		// var errMsg = $("#isCheckedSwiMaxFeeError");
		//
		// var value =
		// $("input[id='valueSwi[0]Stripped']").val($("input[id='valueSwi[0]']").val());
		// var maxValue =
		// $("input[id='maxValueSwi[0]Stripped']").val($("input[id='maxValueSwi[0]']").val());
		//
		// if ($("input[id='isCheckedSwiMaxValue[0]']").val() != "true")
		// {
		// validateMaxFeeTier(value.val(), maxValue.val(), el, errMsg);
		// }
		// });
		// ** End Switching Validation **//

		// ** Start Dividen Validation **//
		if ($("#divByCash").is(":checked") == true
				|| $("#divByRedemption").is(":checked") == true
				|| $("#divByReInvestment").is(":checked") == true) {
			$(
					"#spanDivCumPeriod, #spanDivNavUsed, #spanDivPostPeriod, #spanDivPayPeriod")
					.html(" *");
			if (('${mode}' == 'view') || ('${confirming}' == 'true')) {
				$("#divCumPeriod, #divNavUsed, #divPostPeriod, #divPayPeriod")
						.disabled();
			} else {
				$("#divCumPeriod, #divNavUsed, #divPostPeriod, #divPayPeriod")
						.enabled();
			}
		} else {
			$(
					"#spanDivCumPeriod, #spanDivNavUsed, #spanDivPostPeriod, #spanDivPayPeriod")
					.html("");
			$("#divCumPeriod, #divNavUsed, #divPostPeriod, #divPayPeriod")
					.disabled();
		}

		$("#divByCash, #divByRedemption, #divByReInvestment")
				.change(
						function() {
							if (($("#divByCash").is(":checked"))
									|| ($("#divByRedemption").is(":checked"))
									|| ($("#divByReInvestment").is(":checked"))) {
								$(
										"#spanDivCumPeriod, #spanDivNavUsed, #spanDivPostPeriod, #spanDivPayPeriod")
										.html(" *");

								$(
										"#divCumPeriod, #divNavUsed, #divPostPeriod, #divPayPeriod")
										.enabled();
							} else {
								$(
										"#divCumPeriod, #divNavUsed, #divPostPeriod, #divPayPeriod")
										.val('');

								$(
										"#divCumPeriod, #divNavUsed, #divPostPeriod, #divPayPeriod")
										.disabled();

								$(
										"#spanDivCumPeriod, #spanDivNavUsed, #spanDivPostPeriod, #spanDivPayPeriod")
										.html("");
							}
						});
		// ** End Dividen Validation **//

		// ** Start More Info Validation **//
		// $("#redPayPeriod").blur(function(){
		// var paymentDate = $("#redPayPeriod").val();
		// var maxPaymentDate = $("#maximumPaymentDate").val();
		// var errMsg = $("#paymentDateError");
		//
		// if (new Number(paymentDate) > new Number(maxPaymentDate)){
		// $("#redPayPeriod").addClass('fieldError');
		// $(errMsg).html("Payment Date must be smaller than Maximum Payment
		// Date in tab More Info");
		// $('#tabs').tabs("select","tab-2");
		// return false;
		// } else {
		// $("#redPayPeriod").removeClass('fieldError');
		// $(errMsg).html("");
		// }
		// });
		//
		// $("#maximumPaymentDate").blur(function(){
		// var paymentDate = $("#redPayPeriod").val();
		// var maxPaymentDate = $("#maximumPaymentDate").val();
		// var errMsg = $("#paymentDateError");
		//
		// if (new Number(paymentDate) > new Number(maxPaymentDate)){
		// $("#redPayPeriod").addClass('fieldError');
		// $(errMsg).html("Payment Date must be smaller than Maximum Payment
		// Date in tab More Info");
		// $('#tabs').tabs("select","tab-2");
		// return false;
		// } else {
		// $("#redPayPeriod").removeClass('fieldError');
		// $(errMsg).html("");
		// }
		// });

		if ($("#minBalAmt").isEmpty() != true) {
			$("#isMinBalAmount").attr("checked", true);
		}

		if ($("#minBalUnit").isEmpty() != true) {
			$("#isMinBalUnit").attr("checked", true);
		}

		if ($("#isMinBalAmount").is(":checked") == true) {
			$("#spanByAmount").html(" *");
			$("#minBalAmt").enabled();
		} else {
			$("#spanByAmount").html('');
			$("#minBalAmt").disabled();
		}

		if ($("#isMinBalUnit").is(":checked") == true) {
			$("#spanByUnit").html(" *");
			$("#minBalUnit").enabled();
		} else {
			$("#spanByUnit").html('');
			$("#minBalUnit").disabled();
		}

		$("#isMinBalAmount").change(function() {
			if ($("#isMinBalAmount").is(":checked") == true) {
				$("#spanByAmount").html(" *");
				$("#minBalAmt").enabled();
			} else {
				$("#spanByAmount").html('');
				$("#minBalAmt").val('');
				$("#minBalAmtStripped").val('');
				$("#minBalAmt").disabled();
			}
		});

		$("#isMinBalUnit").change(function() {
			if ($("#isMinBalUnit").is(":checked") == true) {
				$("#spanByUnit").html(" *");
				$("#minBalUnit").enabled();
			} else {
				$("#spanByUnit").html('');
				$("#minBalUnit").val('');
				$("#minBalUnitStripped").val('');
				$("#minBalUnit").disabled();
			}
		});
		// ** End More Info Validation **//

		// if (prd.isCheckedRed.val()=='true'){
		// prd.isCheckedRed.attr("checked", true);
		// }

		if (prd.isCheckedRed.is(":checked")) {
			prd.isCheckedRed.val(true);
		} else {
			prd.isCheckedRed.val(false);
		}

		$("#spanRedMaxAmt").html('');
		prd.isCheckedRed.change(function() {
			var el = $("#redMaxAmt");
			if (prd.isCheckedRed.is(":checked")) {
				prd.isCheckedRed.val(true);
			} else {
				prd.isCheckedRed.val(false);
			}
			if ((prd.isCheckedRed.val() == 'true')
					|| (prd.redMaxAmt.hasClass('fieldError'))) {
				el.removeClass('fieldError');
				$("#errmsgMaxRed").hide();
				prd.redMaxAmt.disabled();
				prd.redMaxAmt.val('');
				prd.redMaxAmtStripped.val('');
				$("#spanRedMaxAmt").html('');
			} else {
				prd.redMaxAmt.enabled();
				$("#spanRedMaxAmt").html(' *');
			}
		});

		prd.redMaxAmt.blur(function() {
			var el = $(this);
			var errMsg = $("#errmsgMaxRed");
			var min = parseFloat(prd.redMinAmtStripped.val());
			var max = parseFloat(prd.redMaxAmtStripped.val());
			validateMinMax(min, max, el, errMsg);
		});

		prd.redMinAmt.blur(function() {
			var el = $("#redMaxAmt");
			var errMsg = $("#errmsgMaxRed");
			var min = parseFloat(prd.redMinAmtStripped.val());
			var max = parseFloat(prd.redMaxAmtStripped.val());
			validateMinMax(min, max, el, errMsg);
		});

		// if (prd.isCheckedRedFee.val()=='true'){
		// prd.isCheckedRedFee.attr('checked',true);
		// }

		if (prd.isCheckedRedFee.is(":checked")) {
			prd.isCheckedRedFee.val(true);
		} else {
			prd.isCheckedRedFee.val(false);
		}

		$("#spanRedMaxFee").html('');
		prd.isCheckedRedFee.change(function() {
			var el = $("#redMaxFee");
			if (prd.isCheckedRedFee.is(":checked")) {
				prd.isCheckedRedFee.val(true);
			} else {
				prd.isCheckedRedFee.val(false);
			}
			if ((prd.isCheckedRedFee.val() == 'true')
					|| (prd.redMaxFee.hasClass('fieldError'))) {
				el.removeClass('fieldError');
				$("#errmsgMaxRedFee").hide();
				prd.redMaxFee.disabled();
				prd.redMaxFee.val('');
				prd.redMaxFeeStripped.val('');
				$("#spanRedMaxFee").html('');
			} else {
				prd.redMaxFee.enabled();
				$("#spanRedMaxFee").html(' *');
			}
		});

		prd.redMaxFee.blur(function() {
			var el = $(this);
			var errMsg = $("#errmsgMaxRedFee");
			var min = parseFloat(prd.redMinFeeStripped.val());
			var max = parseFloat(prd.redMaxFeeStripped.val());
			validateMinMax(min, max, el, errMsg);
		});

		prd.redMinFee.blur(function() {
			var el = $("#redMaxFee");
			var errMsg = $("#errmsgMaxRedFee");
			var min = parseFloat(prd.redMinFeeStripped.val());
			var max = parseFloat(prd.redMaxFeeStripped.val());
			validateMinMax(min, max, el, errMsg);
		});

		// if (prd.isCheckedSwi.val() == 'true'){
		// prd.isCheckedSwi.attr("checked", true);
		// }

		if (prd.isCheckedSwi.is(":checked")) {
			prd.isCheckedSwi.val(true);
		} else {
			prd.isCheckedSwi.val(false);
		}

		$("#spanSwiMaxAmt").html('');
		prd.isCheckedSwi.change(function() {
			var el = $("#swiMaxAmt");
			if (prd.isCheckedSwi.is(":checked")) {
				prd.isCheckedSwi.val(true);
			} else {
				prd.isCheckedSwi.val(false);
			}
			if ((prd.isCheckedSwi.val() == 'true')
					|| (prd.swiMaxAmt.hasClass('fieldError'))) {
				el.removeClass('fieldError');
				$("#errmsgMaxSwi").hide();
				prd.swiMaxAmt.disabled();
				prd.swiMaxAmt.val('');
				prd.swiMaxAmtStripped.val('');
				$("#spanSwiMaxAmt").html('');
			} else {
				prd.swiMaxAmt.enabled();
				$("#spanSwiMaxAmt").html(' *');
			}
		});

		prd.swiMaxAmt.blur(function() {
			var el = $(this);
			var errMsg = $("#errmsgMaxSwi");
			var min = parseFloat(prd.swiMinAmtStripped.val());
			var max = parseFloat(prd.swiMaxAmtStripped.val());
			validateMinMax(min, max, el, errMsg);
		});

		prd.swiMinAmt.blur(function() {
			var el = $("#swiMaxAmt");
			var errMsg = $("#errmsgMaxSwi");
			var min = parseFloat(prd.swiMinAmtStripped.val());
			var max = parseFloat(prd.swiMaxAmtStripped.val());
			validateMinMax(min, max, el, errMsg);
		});

		// if (prd.isCheckedSwiFee.val() == 'true'){
		// prd.isCheckedSwiFee.attr("checked", true);
		// }

		if (prd.isCheckedSwiFee.is(":checked")) {
			prd.isCheckedSwiFee.val(true);
		} else {
			prd.isCheckedSwiFee.val(false);
		}

		$("#spanSwiMaxFee").html('');
		prd.isCheckedSwiFee.change(function() {
			var el = $("#swiMaxFee");
			if (prd.isCheckedSwiFee.is(":checked")) {
				prd.isCheckedSwiFee.val(true);
			} else {
				prd.isCheckedSwiFee.val(false);
			}
			if ((prd.isCheckedSwiFee.val() == 'true')
					|| (prd.swiMaxFee.hasClass('fieldError'))) {
				el.removeClass('fieldError');
				$("#errmsgMaxSwi").hide();
				prd.swiMaxFee.disabled();
				prd.swiMaxFee.val('');
				prd.swiMaxFeeStripped.val('');
				$("#spanSwiMaxFee").html('');
			} else {
				prd.swiMaxFee.enabled();
				$("#spanSwiMaxFee").html(' *');
			}
		});

		if (prd.isCheckMaxInvestor.is(":checked")) {
			prd.isCheckMaxInvestor.val(true);
		} else {
			prd.isCheckMaxInvestor.val(false);
		}

		prd.isCheckMaxInvestor.change(function() {
			var el = $("#maxInvestor");
			if (prd.isCheckMaxInvestor.is(":checked")) {
				prd.isCheckMaxInvestor.val(true);
			} else {
				prd.isCheckMaxInvestor.val(false);
			}
			if ((prd.isCheckMaxInvestor.val() == 'true')
					|| (prd.maxInvestor.hasClass('fieldError'))) {
				el.removeClass('fieldError');
				prd.maxInvestor.disabled();
				prd.maxInvestor.val('');
				prd.maxInvestorStripped.val('');
				$("#spanMaxInvestor").html("");
			} else {
				prd.maxInvestor.enabled();
				$("#spanMaxInvestor").html(" *");
			}
		});

		prd.swiMaxFee.blur(function() {
			var el = $(this);
			var errMsg = $("#errmsgMaxSwiFee");
			var min = parseFloat(prd.swiMinFeeStripped.val());
			var max = parseFloat(prd.swiMaxFeeStripped.val());
			validateMinMax(min, max, el, errMsg);
		});

		prd.swiMinFee.blur(function() {
			var el = $("#swiMaxFee");
			var errMsg = $("#errmsgMaxSwiFee");
			var min = parseFloat(prd.swiMinFeeStripped.val());
			var max = parseFloat(prd.swiMaxFeeStripped.val());
			validateMinMax(min, max, el, errMsg);
		});

		prd.amountVal.autoNumeric({
			vMax : '6'
		});
		prd.amountVal.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		prd.unitVal.autoNumeric({
			vMax : '8'
		});
		prd.unitVal.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		$("#transactionDateVal").autoNumeric({
			vMax : '1'
		});
		$("#transactionDateVal").attr("style", "text-align:right;width:70px;");
		$("#transactionDateVal").live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		$("#postingDate").bind("keyup change", function() {
			$("#subPostPeriodHidden").val($("#postingDate").val());
			$("#redPostPeriodHidden").val($("#postingDate").val());
			$("#swiPostPeriodHidden").val($("#postingDate").val());
		});

		prd.priceVal.autoNumeric({
			vMax : '6'
		});
		prd.priceVal.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		prd.subCot.autoNumeric({
			vMax : '24'
		});
		prd.subCot.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		prd.redCot.autoNumeric({
			vMax : '24'
		});
		prd.redCot.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		prd.swiCot.autoNumeric({
			vMax : '24'
		});
		prd.swiCot.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		prd.redLimitPct.autoNumeric({
			vMax : '100'
		});
		prd.redLimitPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		prd.redLimitPct1.autoNumeric({
			vMax : '100'
		});
		prd.redLimitPct1.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		prd.swoLimitPct.autoNumeric({
			vMax : '100'
		});
		prd.swoLimitPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		prd.offerPct.autoNumeric({
			vMax : '100'
		});
		prd.offerPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		prd.bidPct.autoNumeric({
			vMax : '100'
		});
		prd.bidPct.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		prd.dialogValue.blur(function() {
			prd.dialogValueStripped.val(prd.dialogValue.val());
		});

		prd.dialogValue.autoNumeric({
			vMax : '100'
		});
		prd.dialogValue.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});

		$("#unitLimitTolerance").autoNumeric({
			vMax : '100'
		});
		$("#unitLimitTolerance").attr("style", "text-align:right;width:70px;");
		$("#unitLimitTolerance").live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}

			if (el.val() == 0) {
				el.val("");
			}
		});

		/*
		 * $("input.invalidDateFormat").mask("99/99/9999", { placeholder:" " });
		 * $('input.invalidDateFormat').change( function(){ var el = $(this);
		 * var dateVal = el.val(); var id = this.id; var errorId =
		 * "#"+id+"Error"; try { $.datepicker.parseDate('mm/dd/yy', dateVal,
		 * null); } catch(error) { el.addClass("fieldError");
		 * $(errorId).html("Invalid date format").show(); } });
		 */

		fixPriceStatus();

		radioButtonStatus();

		clearEmptyOptions();

		var productCode = prd.productCode.val();
		prd.productCode.val(productCode.replace(/${specialCharQuote}/g, "'").toString());

		generateToAria();
		checkIsMaturityDateMax();
	} else {
		return new Product(html);
	}
}