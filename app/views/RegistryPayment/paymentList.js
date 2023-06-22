function Payment(html) {
	if (this instanceof Payment) {
/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var roundType;
		var roundDigit;
		
		var NO_FILTER = "NO_FILTER";
		var NO_DIVIDEND ="NO_DIVIDEND";
		var NO_TRX = "NO_TRX";
		
		var TRADETYPE_REDEMPTION = "Redemption";
		
		var pay = html.inject(this);

		var closeDialogMessage = function() {
			$("#dialog-message").dialog("close");
		};
		
/* =========================================================================== 
 * Method
 * ========================================================================= */
		function calculateTotalPayment() {
			var totalPayment = 0;
			html.name("option").each(function(idx, e) {
				if (($(e).checked() != "false")) {
					var payAmount = $(e).parent().parent().children().eq(6);
					totalPayment += payAmount.html().toNumber(",");
				}
			});
			pay.totPayment.valueRnd(totalPayment, true, roundDigit, roundType);
		}

		function fillFilterByOption(filterByHidden) {
			if (pay.type.val() == TRADETYPE_REDEMPTION) {
				if ((filterByHidden == NO_FILTER)||(filterByHidden == '')) {
					$('#filterBy option').remove();
					var options = $('#filterBy').attr('options');
						options[options.length] = new Option('All', NO_FILTER);
						options[options.length] = new Option('Transaction No', NO_TRX);
				} else {
					$("option", pay.filterBy).eq(2).remove();
				}
			} else {
				$('#filterBy option').remove();
				var options = $('#filterBy').attr('options');
					options[options.length] = new Option('Dividend No', NO_DIVIDEND);
			}
		}
		
		function fillFilterByNo(isClear) {
			if (pay.filterBy.val() == NO_FILTER) {
				if (isClear) pay.filterByNo.val("");
				pay.filterByNo.disabled();
				$("p[id=pFilterBy] label span").html("");
			} else {
				if (isClear) pay.filterByNo.val("");
				pay.filterByNo.enabled();
				$("p[id=pFilterBy] label span").html(" *");
			}
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		html.clazz('calendar').datepicker();

		pay.totPayment.attr("disabled", "disabled");

		$("option", pay.type).eq(0).remove();
		$("option", pay.filterBy).eq(0).remove();

		$('#cancel .ui-button-text').text('Reset');
		$('#btnPopulate').button();

		if (!pay.fundCode.val().isEmpty()) {
			var rgProduct = html.getRgProduct(pay.fundCode.val());
			roundType = rgProduct.amountRoundType;
			roundDigit = rgProduct.amountRoundValue;
		}
		
		if (pay.dispatch.val() == 'view' || '${confirming}') {
//			$('#tblTransaction').removeAttr('style');
//			$('#tblTransaction tbody tr td').die('click');
//			$('#tblDividen').removeAttr('style');
//			$('#tblDividen tbody tr td').die('click');
			$('input[name="option"]').attr('disabled', true);
			$('input[name="optionAll"]').attr('disabled', true);
			$('input[name="option"]').attr('checked', true);
			$('input[name="optionAll"]').attr('checked', true);
		}

		pay.fundCode.popupProduct("type", function(data){
			if (data) {
				roundType = data.amountRoundType;
				roundDigit = data.amountRoundValue;
				pay.totPayment.val('');
				pay.totPaymentStripped.val('');
			}
		});

		pay.fundCode.change(function(){
			if (pay.fundCode.val() ==''){
				pay.totPayment.val('');
				pay.totPaymentStripped.val('');
			}
		});

		pay.selectAll.change(function() {
			var checkedval = pay.selectAll.attr("checked");
			html.name("option").attr("checked", checkedval);
			//var isChecked = $(this).attr("checked");
			calculateTotalPayment();
			
//			var trades;
//
//			if ('${trades}' != '')
//			{
//				trades = ${trades.raw()};
//			}
//			else
//			{
//				trades = new Array();
//			}			
//
//	        if(checkedval)
//	        {
//	        	var rows = trades.length;
//	        	var totalAmount = 0;
//				for (var i = 0; i < rows; i++)
//				{
//					totalAmount += trades[i].amount;
//				};
//
//				pay.totPayment.valueRnd(totalAmount, true, roundDigit, roundType);
//	        }
//	        else
//	        {
//	        	pay.totPayment.value(0);
//	        };
		});

//		$("#form #listTrade #tradeTable_paginate span, .fg-button ui-button ui-state-default").live("click", function() {
//			var checkedval = pay.selectAll.attr("checked");
//			html.name("option").attr("checked", checkedval);
//			console.log(html.name("option"));
//		});

		$("#form #listTrade #tradeTable tbody tr input[name='option']").live("change", function() {
//	        var row = $(this).parents('tr');
//	        var rowNumber = tableTrade.fnGetPosition(row[0]);
//	        var data = tableTrade.fnGetData(row[0]);
	        var isChecked = $(this).attr("checked");
	        var nominal = $(this).attr("nominal");

	        if(pay.totPaymentStripped.val() == 0)
	        {
	        	if(isChecked)
	        	{
	        		pay.totPayment.valueRnd(nominal, true, roundDigit, roundType);
	        	};
	        }
	        else
	        {
	        	var oldNominal = pay.totPaymentStripped.val();
	        	var totalNominal;

	        	if(isChecked)
	        	{
	        		totalNominal = Number(oldNominal) + Number(nominal);
	        		pay.totPayment.valueRnd(totalNominal, true, roundDigit, roundType);
	        	}
	        	else
	        	{
	        		totalNominal = Number(oldNominal) - Number(nominal);
	        		pay.totPayment.valueRnd(totalNominal, true, roundDigit, roundType);
	        	};
	        };
		});

//		html.name("option").live('change', function() {
//			pay.selectAll.attr("checked", false);
//			calculateTotalPayment();
//		});

		pay.btnPopulate.click(function() {
			if (!pay.fundCode.isEmpty() && !pay.type.isEmpty())
			{
				if (pay.filterBy.val()!= NO_FILTER)
				{
					if(!pay.filterByNo.isEmpty())
					{
						$('#fundCodeError').html('');
						$('#filterByNoError').html('');
						pay.totPayment.valueRnd(0, true, roundDigit, roundType);
						if (pay.type.val() == TRADETYPE_REDEMPTION) {
							$('#selectAll').removeAttr('checked');
							pay.form.attr('action', 'showRedeem');
							pay.form.submit();
						} else {
							/*
							$.post('@{showDividen()}', $('#form').serialize(), function(data) {
								$('#selectAll').removeAttr('checked');
								trade(data);
			                });
			                */
							$('#selectAll').removeAttr('checked');
							pay.form.attr('action', 'showDividen');
							pay.form.submit();
						}
					}
					else
					{
						$('#filterByNoError').html("Required");
					}
				}
				else
				{
					$('#fundCodeError').html('');
					$('#filterByNoError').html('');
					pay.totPayment.valueRnd(0, true, roundDigit, roundType);
					if (pay.type.val() == TRADETYPE_REDEMPTION) {
						$('#selectAll').removeAttr('checked');
						pay.form.attr('action', 'showRedeem');
						pay.form.submit();
					} else {
						$('#selectAll').removeAttr('checked');
						pay.form.attr('action', 'showDividen');
						pay.form.submit();
					}
				}
			}
			else
			{
				if (pay.fundCode.isEmpty())
				{
					$('#fundCodeError').html('Required');
				}
				else
				{
					$('#fundCodeError').html('');
				}
				
				if (pay.filterBy.val()!= NO_FILTER)
				{
					if(!pay.filterByNo.isEmpty())
					{
						$('#filterByNoError').html("");
					}
					else
					{
						$('#filterByNoError').html("Required");
					}
				}
				else
				{
					$('#filterByNoError').html("");
				}
			}
		});

		$("#save").click(function() {
			var selected = "";
			var selectedNominal = "";
			var selectedProductBankAccountKey = "";
			var selectedInvestorBankAccountKey = "";

			$("input:checked", html).each(function(){
				var tradeKey = $(this).val();
				var nominal = $(this).attr("nominal");
				var productBankAccountKey = $(this).attr("productBankAccountKey");
				var investorBankAccountKey = $(this).attr("investorBankAccountKey");

				if (tradeKey != "on") {
					if (selected == "") {
						selected = tradeKey;
						selectedNominal = nominal;
						selectedProductBankAccountKey = productBankAccountKey;
						selectedInvestorBankAccountKey = investorBankAccountKey;
					}else {
						selected += ("," + tradeKey);
						selectedNominal  += ("," + nominal);
						selectedProductBankAccountKey  += ("," + productBankAccountKey);
						selectedInvestorBankAccountKey  += ("," + investorBankAccountKey);
					};
				};
			});

			pay.selected.value(selected);
			pay.selectedNominal.value(selectedNominal);
			pay.selectedInvestorBankAccountKey.value(selectedInvestorBankAccountKey);
			pay.selectedProductBankAccountKey.value(selectedProductBankAccountKey);

			var tradeKeys = selected.split(",");
			var investorKeys = selectedInvestorBankAccountKey.split(",");
			var productKeys = selectedProductBankAccountKey.split(",");

			var rows = tradeKeys.length;
			for (var i = 0; i < rows; i++)
			{
			    if (productKeys[i]  == "undefined")
				{
					messageAlertOk("Product Bank Account in transaction# " + tradeKeys[i] + " is required", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
					return false;
				}
				
				if (investorKeys[i]  == "undefined")
				{
					messageAlertOk("Investor Bank Account in transaction# " + tradeKeys[i] + " is required", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
					return false;
				};
			}

			if ((selected == "") || (pay.paymentDate.val() == "")){
				if (selected == "")
				{
					messageAlertOk("Please select the checkbox", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
				};
			} else {
				//pay.registryPaymentForm.attr('action', 'process');
				//pay.registryPaymentForm.submit();
				submit('@{save()}?mode=${mode}&isNewRec=${isNewRec}#{if group}&group=${group}#{/}');
			}
			return false;
		});

		$("#confirm").click(function() {
			var selected = "";
			var selectedNominal = "";
			var selectedProductBankAccountKey = "";
			var selectedInvestorBankAccountKey = "";

			$("input:checked", html).each(function(){
				var tradeKey = $(this).val();
				var nominal = $(this).attr("nominal");
				var productBankAccountKey = $(this).attr("productBankAccountKey");
				var investorBankAccountKey = $(this).attr("investorBankAccountKey");

				if (tradeKey != "on") {
					if (selected == "") {
						selected = tradeKey;
						selectedNominal = nominal;
						selectedProductBankAccountKey = productBankAccountKey;
						selectedInvestorBankAccountKey = investorBankAccountKey;
					}else {
						selected += ("," + tradeKey);
						selectedNominal  += ("," + nominal);
						selectedProductBankAccountKey  += ("," + productBankAccountKey);
						selectedInvestorBankAccountKey  += ("," + investorBankAccountKey);
					};
				};
			});

			pay.selected.value(selected);
			pay.selectedNominal.value(selectedNominal);
			pay.selectedInvestorBankAccountKey.value(selectedInvestorBankAccountKey);
			pay.selectedProductBankAccountKey.value(selectedProductBankAccountKey);

			submit('@{confirm()}/?mode=${mode}&isNewRec=${isNewRec}#{if group}&group=${group}#{/}');
			return false;
		});

		pay.type.change(function() {
			fillFilterByOption('');
			fillFilterByNo(true);
			pay.totPayment.val('');
			$('h_totPayment').val('');
		});
		
		pay.filterBy.change(function() {
			fillFilterByNo(true);
			pay.totPayment.val('');
			$('h_totPayment').val('');
		});
		
		if (pay.dispatch.val() == 'entry') {
			if('${confirming}')
			{
				$('#filterByNo').attr('disabled', true);
			}
			else
			{
				fillFilterByNo(false);
			}
			fillFilterByOption(pay.filterByHidden.val());
		}

//		$("td[name=payAmtCell]", html).each(function(){
//			var amount = $(this).html();
//			pay.formatPayAmount.valueRnd(amount.toNumber(","), true, roundDigit, roundType);
//			$(this).html(pay.formatPayAmount.val());
//		});
		
		if ($.browser.msie){
			$("#remarks[maxlength]").bind('input propertychange', function() {
	            var maxLength = $(this).attr('maxlength');  
	            if ($(this).val().length > maxLength) {  
	                $(this).val($(this).val().substring(0, maxLength));  
	            };
	        });
	    };
	} else {
		return new Payment(html);
	}
}