function Payment(html) {
	if (this instanceof Payment) {
/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
		var roundType;
		var roundDigit;
		
		var NO_FILTER = "NO_FILTER";
		var NO_DIVIDEND ="NO_DIVIDEND";
		var NO_TRX = "NO_TRX";
		var NO_SWITCHING = "NO_SWITCHING";
		
		var TRADETYPE_REDEMPTION = "Redemption";
		var TRADETYPE_SWITCH_OUT= "Switch-Out";
		var TRADETYPE_DIVINED_CASH = "Dividend By Cash";

		var closeDialogMessage = function() {
			$("#dialog-message").dialog("close");
		};
		
		var rgInvestment = {};
/* =========================================================================== 
 * Table Search Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.rgProduct = new Object();
			p.rgProduct.productCode = app.fundCode.val();
			p.type = app.type.val();
			p.paymentDate = app.paymentDate.val();
			p.filterByNo = app.filterByNo.val();
			p.remarks = app.remarks.val();
			p.totalPaidAmount = app.totalPaidAmount.val();
			if (app.type.val() == 'Redemption') {
				p.selected = app.selected.val();
			}else{
				p.selected = '';
			}
			p.selectedProductBankAccountKey = app.selectedProductBankAccountKey.val();
			p.selectedInvestorBankAccountKey = app.selectedInvestorBankAccountKey.val();
			p.paymentKey = app.paymentNo.val();
			p.dispatch = app.dispatch.val();
			p.query = app.query.val();
			return p;
		};

/* =========================================================================== 
 * Function
 * ========================================================================= */
		
		function validate() {
			var validProduct = app.fundCodeError.required(app.fundCode.isEmpty());
			var validFilterNo = true;
			
			if (app.filterBy.val() != NO_FILTER)
			{
				validFilterNo = app.filterByNoError.required(app.filterByNo.isEmpty());
			}

			return validProduct && validFilterNo;
		}
		
		function calculateAmount() {
			var data = app.datatable.selects(0, "checked");
			var amount= 0;
			for(i=0;i<data.length;i++){
				if(data[i].bean.amount)
					amount = amount + new Number(data[i].bean.amount);
			}
			//app.totPayment.val(amount) ;
			app.totPayment.valueRnd(amount, true, roundDigit, roundType);
			app.totalPaidAmount.value(amount);
			
		}

/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.accordion.accordion({collapsible: true});
		
		app.populate.add(app.save).add(app.reset).add(app.confirm).add(app.back).add(app.close).add(app.saveDialog).add(app.cancelDialog).add(app.approve).add(app.reject).add(app.closeWorkflow).button();

		app.datatable = app.tblTransaction.paging("@{RegistryPayment.paging()}", this, function(data){
			var trs = $("tbody tr", app.tblTransaction);
			if(app.dispatch.val() == 'save' || app.dispatch.val() == 'approve'){
				app.selectAll.attr("disabled", "disabled").attr("checked", false);
			}
			//approve,save
			if(app.dispatch.val() == 'view')
			{
				app.totalPaidAmount.value(0);
				app.totPayment.value(0);
			}

			var selected = app.selected.val().split("_");
			if( roundDigit == undefined || roundDigit == "" ){
				roundDigit = $("#digitAmount").val();
				roundType = $("#typeAmount").val();
			}
			trs.each(function(idx, e){
				var prop = $(e).data("prop");
				if( prop.bean ){
					// formatting amount
					var formatedAmount = html.valueRnd(prop.bean.amount, true, roundDigit, roundType);
					var xtd = $("td", this).eq(6);
					xtd.html(formatedAmount.val());					
				}
			});
			
			
			if(app.type.val()=="Dividend By Cash" || app.type.val()=="Dividend By Redemption"){	
				app.selectAll.attr("disabled", "disabled").attr("checked", "checked");
					trs.each(function(idx, e){
						if ($("td", $(this)).length > 1) { // cek data kosong
							var prop = $(this).data("prop");
							$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled").attr("checked", "checked");
						}
					});
				app.totPayment.valueRnd(data.iTotalAmount, true, roundDigit, roundType);
				app.totalPaidAmount.value(data.iTotalAmount);
			}
		
			for (var i = 0; i < selected.length; i++)
	        {
	            var tradeKey = selected[i];
	            if (app.readOnly.val() == 'true') {
					trs.each(function(){
						if ($("td", $(this)).length > 1) { // cek data kosong
							var prop = $(this).data("prop");
							if(prop.bean.tradeKey == tradeKey)
							{
								$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled").attr("checked", "checked");
							}
							else
							{
								$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled");
							}														
						}
					});
				}
	        }
		});
		
		app.datatable.bind("selects", function(event, props){
			//alert("oooi")
			for (x in props) {
				//alert("Test");
			}
		});

		if ($.browser.msie){
			$("#remarks[maxlength]").bind('input propertychange', function() {
	            var maxLength = $(this).attr('maxlength');  
	            if ($(this).val().length > maxLength) {  
	                $(this).val($(this).val().substring(0, maxLength));  
	            };
	        });
	    };
	    
	    function buttonHideShow() {
			if (app.dispatch.val() == 'view') {
				app.buttonEntry.show();
				app.buttonSave.hide();
				app.buttonConfirm.hide();
			}
			
			if (app.dispatch.val() == 'save') {
				app.populate.hide();
				app.buttonEntry.hide();
				app.saveDialog.hide();
				app.buttonSave.show();
				app.buttonConfirm.hide();
			}
			
			if (app.dispatch.val() == 'confirm') {
				app.populate.hide();
				app.buttonEntry.hide();
				app.saveDialog.hide();
				app.buttonSave.hide();
				app.buttonConfirm.show();
			}
			
			if (app.dispatch.val() == 'approve') {
				app.populate.hide();
				app.buttonEntry.hide();
				app.saveDialog.disabled();
				app.buttonSave.hide();
				app.buttonConfirm.hide();
			}
		}

/* =========================================================================== 
 * Method
 * ========================================================================= */
	    buttonHideShow();

		function fillFilterByOption(filterByHidden) {
			bindDatatable();
			if (app.type.val() == TRADETYPE_REDEMPTION) {
				if ((filterByHidden == NO_FILTER)||(filterByHidden == '')) {
					$('#filterBy option').remove();
					var options = $('#filterBy').attr('options');
					options[options.length] = new Option('ALL', NO_FILTER);
					options[options.length] = new Option('TRANSACTION NO', NO_TRX);

				} else {
					$("option", app.filterBy).eq(2).remove();
					$("option", app.filterBy).eq(2).remove();
				}

//				if(app.dispatch.val() == 'approve' && app.filterByNo.value() != null)
//				{
//					$('#filterBy option').remove();
//					var options = $('#filterBy').attr('options');
//					options[options.length] = new Option('Transaction No', NO_TRX);
//				}
			} else if(app.type.val() == TRADETYPE_SWITCH_OUT){
				$('#filterBy option').remove();
				var options = $('#filterBy').attr('options');
				options[options.length] = new Option('ALL', NO_FILTER);
				options[options.length] = new Option('SWITCHING NO', NO_SWITCHING);
				app.datatable.unbind("select");
				$("option", app.filterBy).eq(2).remove();
				$("option", app.filterBy).eq(2).remove();
			}
			else {
				$('#filterBy option').remove();
				var options = $('#filterBy').attr('options');
				options[options.length] = new Option('DIVIDEND NO', NO_DIVIDEND);
				
//				if(app.dispatch.val() == 'approve' && app.filterByNo.value() != null)
//				{
//					$('#filterBy option').remove();
//					var options = $('#filterBy').attr('options');
//					options[options.length] = new Option('Dividend No', NO_TRX);
//				}
			}
		}

		function fillFilterByNo(isClear) {
			if (app.filterBy.val() == NO_FILTER) {
				if (isClear) app.filterByNo.val("");
				app.filterByNo.disabled();
				$("p[id=pFilterBy] label span").html("");
			} else {
				if (isClear) app.filterByNo.val("");
				app.filterByNo.enabled();
				$("p[id=pFilterBy] label span").html(" *");
			}
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */

		$("option", app.type).eq(0).remove();
		$("option", app.filterBy).eq(0).remove();

		if(app.dispatch.val() == 'approve')
		{
			app.filterByNo.disabled();
		}
		else
		{
			app.paymentNo.value("");
		}

		app.popupDetailPop = app.popupDetail.dialog({
			autoOpen: false,
			height: 420,
			width: 950,
			modal: true
		});
		
		
		function bindDatatable(){
			app.datatable.bind("select", function(event, prop) {
				app.tradeKeyDialog.val(prop.bean.tradeKey);
				var fmtDate = '${appProp.jqueryDateFormat}';
				var productCode = prop.bean.rgProduct.productCode.replaceAll(" ", "+");
				var OldproductCode = prop.bean.rgProduct.productCode.replaceAll(" ", "+");
				
				var newProductPopup = prop.bean.rgProduct.productCode;	
				var tradeKey = prop.bean.tradeKey;
				console.log("****tradeKey:"+tradeKey);			
				
				
				/*if(newProduct != '')
					productCode = newProduct;*/
				
				var accountNumber = prop.bean.rgInvestmentaccount.accountNumber.replaceAll(" ", "+");
				
				/*console.log("****productCode:"+productCode);
				console.log("****accountNumber:"+accountNumber);	*/		
				
				app.rgProductBnAccount.lookup({
					list:'@{Pick.bankAccountsByRgProductBnAccountsDomain()}?productCode=' + productCode+"|${domainBankRed}",
					get:{
						url:'@{Pick.bankAccountsByRgProductBnAccountDomain()}?productCode=' + productCode+"|${domainBankRed}",
						success: function(data){
							console.log("tradeKey:"+tradeKey);
							app.rgProductBnAccount.removeClass("fieldError");
							app.rgProductBnAccount.val(data.bankAccountNo);
							if(app.dispatch.val() == 'save'){
								newProductPopup = data.bankAccountNo;
								var productThridPartyChange = app.selectedProductThirdPartyCodeChange.val();
								var arrProductThridPartyChange = productThridPartyChange.split("#");
								console.log("****arrProductThridPartyChange:"+arrProductThridPartyChange);
								for(i=0;i<arrProductThridPartyChange.length;i++){
									var thridPartyChangeInfo = arrProductThridPartyChange[i];
									var arrThridPartyChangeInfo = thridPartyChangeInfo.split("|");
									console.log("****arrThridPartyChangeInfo:"+arrThridPartyChangeInfo);
									if(tradeKey == arrThridPartyChangeInfo[0]){
										newProductPopup = arrThridPartyChangeInfo[1];
									}				
								}
								app.rgProductBnAccount.val(newProductPopup);
							}else{
								app.rgProductBnAccount.val(data.bankAccountNo);
							}
							app.rgProductBnAccountBankAccountKey.val(data.bankAccountKey);
							app.rgProductBnAccountThirdPartyKey.val(data.thirdPartyKey);
							app.rgProductBnAccountThirdPartyCode.val(data.thirdPartyCode);
							app.rgProductBnAccountThirdPartyName.val(data.thirdPartyName);
							app.rgProductBnAccountBeneficiaryName.val(data.customer.customerName);
							app.rgProductBnAccountCurrencyCode.val(data.currency.currencyCode);
						},
						error: function(data){
							app.rgProductBnAccount.addClass('fieldError');
							app.rgProductBnAccount.val("");
							app.rgProductBnAccountBankAccountKey.val("");
							app.rgProductBnAccountThirdPartyKey.val("");
							app.rgProductBnAccountThirdPartyCode.val("");
							app.rgProductBnAccountThirdPartyName.val("");
							app.rgProductBnAccountBeneficiaryName.val("");
							app.rgProductBnAccountCurrencyCode.val("");
						}
					},
					filter: prop.bean.rgProduct.productCode,
					description: app.rgProductBnAccountThirdPartyName,
					help: app.rgProductBnAccountHelp
				});
				
	
				app.rgProductBnAccount.blur(function(){
					if (app.rgProductBnAccount.val() == "") {
						app.rgProductBnAccount.val("");
						app.rgProductBnAccount.removeClass("fieldError");
						app.rgProductBnAccountError.html("");
						app.rgProductBnAccountThirdPartyKey.val("");
						app.rgProductBnAccountThirdPartyCode.val("");
						app.rgProductBnAccountThirdPartyName.val("");
						app.rgProductBnAccountBeneficiaryName.val("");
						app.rgProductBnAccountCurrencyCode.val("");
					}
				});
				
				html.getRgInvestmentAcct(app.fundCode.val(), prop.bean.rgInvestmentaccount.accountNumber, function(data){
					if (data) {
						rgInvestment = data;
					}
				});
				
				var customerKeys = 0;
				if( rgInvestment.customer ){
					customerKeys = rgInvestment.customer.customerKey;
				}
				
				app.bankAccount.lookup({
					list:'@{Pick.bankAccountbyRgInvestmentspayments()}?currencyCode=' + prop.bean.rgProduct.currency.currencyCode + '&customerKey=' + customerKeys,
					get:{
						url:'@{Pick.bankAccountByInvestmentProductCurrAccNo()}?currencyCode=' + prop.bean.rgProduct.currency.currencyCode + '&customerKey=' + customerKeys,
						success: function(data){
							app.bankAccount.removeClass("fieldError");						
							if(app.dispatch.val() == 'save'){
								var selectedbankCodeNameChange = app.selectedbankCodeNameChange.val();
								var arrselectedbankCodeNameChange = selectedbankCodeNameChange.split("#");
								var newbank = data.bankAccountNo;
								for(i=0;i<arrselectedbankCodeNameChange.length;i++){
									var bankInfo = arrselectedbankCodeNameChange[i];
									var arrbankInfo = bankInfo.split("|");
									if(tradeKey == arrbankInfo[0]){
										newbank = arrbankInfo[1];
									}
								}
								app.bankAccount.val(newbank);
							}else{
								app.bankAccount.val(data.bankAccountNo);
							}
							app.bankAccountBankAccountKey.val(data.bankAccountKey);
							app.bankAccountThirdPartyKey.val(data.thirdPartyKey);
							app.bankAccountThirdPartyCode.val(data.thirdPartyCode);
							app.bankAccountThirdPartyName.val(data.thirdPartyName);
							app.bankAccountBeneficiaryName.val(data.customer.customerName);
							app.bankAccountCurrencyCode.val(data.currency.currencyCode);
						},
						error: function(data){
							app.bankAccount.addClass('fieldError');
							app.bankAccount.val("");
							app.bankAccountBankAccountKey.val("");
							app.bankAccountThirdPartyKey.val("");
							app.bankAccountThirdPartyCode.val("");
							app.bankAccountThirdPartyName.val("");
							app.bankAccountBeneficiaryName.val("");
							app.bankAccountCurrencyCode.val("");
						}
					},
					filter: prop.bean.rgInvestmentaccount.accountNumber,
					description: app.bankAccountThirdPartyName,
					help: app.bankAccountHelp
				});
	
				app.bankAccount.blur(function(){
					if (app.bankAccount.val() == "") {
						app.bankAccount.val("");
						app.bankAccount.removeClass("fieldError");
						app.bankAccountError.html("");
						app.bankAccountBankAccountKey.val("");
						app.bankAccountThirdPartyKey.val("");
						app.bankAccountThirdPartyCode.val("");
						app.bankAccountThirdPartyName.val("");
						app.bankAccountBeneficiaryName.val("");
						app.bankAccountCurrencyCode.val("");
					}
				});
				
				
				
				app.fundCodeDialog.val(prop.bean.rgProduct.productCode);
				app.fundCodeDescDialog.val(prop.bean.rgProduct.name);
	
				app.currencyDialog.val(prop.bean.rgProduct.currency.currencyCode);
				app.currencyDescDialog.val(prop.bean.rgProduct.currency.description);
	
				app.accountDialog.val(prop.bean.rgInvestmentaccount.accountNumber);
				app.accountDescDialog.val(prop.bean.rgInvestmentaccount.name);
	
				app.transactionDateDialog.val(new Date(prop.bean.tradeDate).format(fmtDate));
				app.paymentDateDialog.val(new Date(prop.bean.paymentDate).format(fmtDate));
	
				app.sellingAgentDialog.val(prop.bean.saCode.thirdPartyCode);
				app.sellingAgentDescDialog.val(prop.bean.saCode.thirdPartyCode);
	
				app.externalReferenceDialog.val(prop.bean.externalReference);
				
				app.unitDialog.valueRnd(0, true, roundDigit, roundType);
				app.priceDialog.valueRnd(0, true, roundDigit, roundType);
				app.amountDialog.valueRnd(0, true, roundDigit, roundType);
				app.totalFeeAmountDialog.valueRnd(0, true, roundDigit, roundType);
				app.paymentDialog.valueRnd(0, true, roundDigit, roundType);
				
				if(app.type.val() ==TRADETYPE_DIVINED_CASH){
					app.unitDialog.valueRnd(prop.bean.unit, true, roundDigit, roundType);
					app.priceDialog.valueRnd(prop.bean.price, true, roundDigit, roundType);
					app.amountDialog.valueRnd(prop.bean.netAmount, true, roundDigit, roundType);
					app.totalFeeAmountDialog.valueRnd(prop.bean.feeAmt, true, roundDigit, roundType);
					app.paymentDialog.valueRnd(prop.bean.amount, true, roundDigit, roundType);
				}else{
					app.unitDialog.valueRnd(prop.bean.rgTransaction.unit, true, roundDigit, roundType);
					app.priceDialog.valueRnd(prop.bean.rgTransaction.price, true, roundDigit, roundType);
					app.amountDialog.valueRnd(prop.bean.rgTransaction.netAmount, true, roundDigit, roundType);
					app.totalFeeAmountDialog.valueRnd(prop.bean.rgTransaction.feeAmount, true, roundDigit, roundType);
					app.paymentDialog.valueRnd(prop.bean.rgTransaction.amount, true, roundDigit, roundType);
				}			
	
				$("#productBankAccount b").text(prop.bean.rgProduct.name);
				$("#investorBankAccount b").text(prop.bean.rgInvestmentaccount.name);
				
				app.customerKeyDialog.val(prop.bean.rgProduct.cfMaster.customerKey);
	
				var selected = app.selected.val().split("_");
				var selectedProductBankAccountKey = app.selectedProductBankAccountKey.val().split("_");
				var selectedProductAccountNo = app.selectedProductAccountNo.val().split("_");
				//var selectedProductThirdPartyCode = app.selectedProductThirdPartyCode.val().split("_");
				var selectedInvestorBankAccountKey = app.selectedInvestorBankAccountKey.val().split("_");
				var selectedInvestorAccountNo = app.selectedInvestorAccountNo.val().split("_");
				var selectedInvestorThirdPartyCode = app.selectedInvestorThirdPartyCode.val().split("_");
	
				if(selected != null && selected != "")
				{
					for (var i = 0; i < selected.length; i++)
			        {
			            if((selected[i] != null) && (selected[i] == prop.bean.tradeKey))
			            {
			            	if(selectedProductAccountNo[i] != null)
			            	{
			            		//app.rgProductBnAccount.val(selectedProductAccountNo[i] + "|" + selectedProductThirdPartyCode[i]);
			            		app.rgProductBnAccount.val(selectedProductAccountNo[i]);
			            	}
	
			            	if(selectedInvestorAccountNo[i] != null)
			            	{
			            		//app.bankAccount.val(selectedInvestorAccountNo[i] + "|" + selectedInvestorThirdPartyCode[i]);
			            		app.bankAccount.val(selectedInvestorAccountNo[i]);
			            	}
			            }
			        }
	
					if(app.dispatch.val() == 'save')
					{
						//app.rgProductBnAccount.blur();
						app.bankAccount.blur();
					}
				}
	
				if(prop.bean.rgProductBnAccount != null)
				{
					//app.rgProductBnAccount.val(prop.bean.rgProductBnAccount.accountNo + "|" + prop.bean.rgProductBnAccount.bankCode.thirdPartyCode);
					app.rgProductBnAccount.val(prop.bean.rgProductBnAccount.accountNo);
					//if(app.dispatch.val() == 'approve')
					//{
						app.rgProductBnAccount.blur();
					//}
				}
	
				if(prop.bean.bankAccount != null)
				{
					//app.bankAccount.val(prop.bean.bankAccount.accountNo + "|" + prop.bean.bankAccount.bankCode.thirdPartyCode);
					app.bankAccount.val(prop.bean.bankAccount.accountNo);
					//if(app.dispatch.val() == 'approve')
					//{
						app.bankAccount.blur();
					//}
				}
	
				app.popupDetail.dialog('open');
				app.cancelDialog.focus();
			});
		}
		
		bindDatatable();

		app.datatable.bind("selects", function(event, props){
			var tradeKeys = "";
			var tradeAmounts = "";
			var productBanks = "";
			var productAccountNos = "";
			var productThirdPartyCodes = "";
			var investorBanks = "";
			var investorAccountNos = "";
			var investorThirdPartyCodes = "";
			var found = false;

			if(props.length == 0)
			{
				messageAlertOk("Please select the checkbox", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
				return;
			}
			else
			{
				for(tk in props)
				{
					if(tradeKeys == "")
					{
						tradeKeys = props[tk].bean.tradeKey;
						tradeAmounts = props[tk].bean.amount;
						
						if(props[tk].bean.rgProductBnAccount.bankAccountKey != null)
						{
							productBanks = props[tk].bean.rgProductBnAccount.bankAccountKey;
							productAccountNos = props[tk].bean.rgProductBnAccount.accountNo;
							productThirdPartyCodes = props[tk].bean.rgProductBnAccount.bankCode.thirdPartyCode;
						}
						else
						{
							messageAlertOk("Product Bank Account in transaction# " + props[tk].bean.tradeKey + " is required", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
							found = true;
						}
						
						if(props[tk].bean.bankAccount.bankAccountKey != null)
						{
							investorBanks = props[tk].bean.bankAccount.bankAccountKey;
							investorAccountNos = props[tk].bean.bankAccount.accountNo;
							investorThirdPartyCodes = props[tk].bean.bankAccount.bankCode.thirdPartyCode;
						}
						else
						{
							messageAlertOk("Investor Bank Account in transaction# " + props[tk].bean.tradeKey + " is required", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
							found = true;
						}
					}
					else
					{
						tradeKeys += "_" + props[tk].bean.tradeKey;
						tradeAmounts += "_" + props[tk].bean.amount;

						if(props[tk].bean.rgProductBnAccount.bankAccountKey != null)
						{
							productBanks += "_" + props[tk].bean.rgProductBnAccount.bankAccountKey;
							productAccountNos += "_" + props[tk].bean.rgProductBnAccount.accountNo;
							productThirdPartyCodes += "_" + props[tk].bean.rgProductBnAccount.bankCode.thirdPartyCode;
						}
						else
						{
							messageAlertOk("Product Bank Account in transaction# " + props[tk].bean.tradeKey + " is required", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
							found = true;
						}

						if(props[tk].bean.bankAccount.bankAccountKey != null)
						{
							investorBanks += "_" + props[tk].bean.bankAccount.bankAccountKey;
							investorAccountNos += "_" + props[tk].bean.bankAccount.accountNo;
							investorThirdPartyCodes += "_" + props[tk].bean.bankAccount.bankCode.thirdPartyCode;
						}
						else
						{
							messageAlertOk("Investor Bank Account in transaction# " + props[tk].bean.tradeKey + " is required", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
							found = true;
						}
					}
				}

				app.selected.val(tradeKeys);
				app.selectedNominal.val(tradeAmounts);
				app.selectedInvestorBankAccountKey.val(investorBanks);
				app.selectedInvestorAccountNo.val(investorAccountNos);
				app.selectedInvestorThirdPartyCode.val(investorThirdPartyCodes);
				app.selectedProductBankAccountKey.val(productBanks);
				app.selectedProductAccountNo.val(productAccountNos);
				app.selectedProductThirdPartyCode.val(productThirdPartyCodes);

				if(!found)
				{
					app.transferMethod.removeAttr('disabled');
					app.searchForm.attr('action', 'save');
					app.searchForm.submit();
				}
			}
		});

		if (!app.fundCode.val().isEmpty()) {
			var rgProduct = html.getRgProduct(app.fundCode.val());
			roundType = rgProduct.amountRoundType;
			roundDigit = rgProduct.amountRoundValue;
		}

		app.fundCode.popupProduct("type", function(data){
			if (data) {
				roundType = data.amountRoundType;
				roundDigit = data.amountRoundValue;
				app.totPayment.val('');
				app.totalPaidAmount.val('');
			}
		});
		
		app.fundCode.change(function(){
			if (app.fundCode.val() ==''){
				app.totPayment.val('');
				app.totalPaidAmount.val('');
			}
		});

		app.populate.click(function(){
			if (validateTransferMethod()) {
				var valid = validate();
				app.query.val(valid);
				app.datatable.fnPageChange("first");
			}
		});

		app.reset.add(app.close).click(function(){
			app.searchForm.attr('action', 'reset');
			app.searchForm.submit();
		});

		app.back.add().click(function(){
			app.totalPaidAmount.value(0);
			
			app.selected.val("");
			app.selectedNominal.val("");
			app.selectedInvestorBankAccountKey.val("");
			app.selectedInvestorAccountNo.val("");
			app.selectedInvestorThirdPartyCode.val("");
			app.selectedProductBankAccountKey.val("");
			app.selectedProductAccountNo.val("");
			app.selectedProductThirdPartyCode.val("");
			
			app.searchForm.attr('action', 'listBack');
			app.searchForm.submit();
		});
		
		app.transferMethod.change(function(){
			if ($(this).val() != '') {
				app.transferMethodError.html('');
			}
		});
		
		function validateTransferMethod() {
			app.transferMethodError.html('');
			
			if (app.transferMethod.val() == '') {
				app.transferMethodError.html('required');
				return false;
			}
			
			return true;
		}
		
		app.save.click(function(){
			if (validateTransferMethod()) {
				app.datatable.trigger("fetch", [0, "checked"]);	
			}			
		});

		app.saveDialog.click(function(){
			if (app.rgProductBnAccount.val() == '' || app.bankAccount.val() == '')
			{
				if (app.rgProductBnAccount.val() == '' || app.rgProductBnAccount.val() == null)
				{
					app.rgProductBnAccountError.html('Required');
				}
				else
				{
					app.rgProductBnAccountError.html("");
				}

				if (app.bankAccount.val() == '' || app.bankAccount.val() == null)
				{
					app.bankAccountError.html('Required');
				}
				else
				{
					app.bankAccountError.html("");
				}
				return false;
			}
			else
			{
				var found = false;

				app.rgProductBnAccountError.html("");
				app.bankAccountError.html("");
				
				var currencyProduct = app.currencyDialog.val();
				var currencyProductBankAccount = app.rgProductBnAccountCurrencyCode.val();
				var currencyInvestorBankAccount = app.bankAccountCurrencyCode.val();

				if (currencyProduct != currencyProductBankAccount)
				{
					$("#rgProductBnAccountErrorGlobal").html("Product Bank Account Currency must be same with Product Currency").show();
					found = true;
				}
				else
				{
					$("#rgProductBnAccountErrorGlobal").html("");
				}

				if (currencyProduct != currencyInvestorBankAccount)
				{
					$("#bankAccountErrorGlobal").html("Investor Bank Account Currency must be same with Product Currency").show();
					found = true;
				}
				else
				{
					$("#bankAccountErrorGlobal").html("");
				}

				if(!found)
                {
					var prop = app.datatable.findBean("tradeKey", app.tradeKeyDialog.val());
					console.log("prop.length:"+prop.length);
					console.log("prop[0].bean.bankAccount.bankCode:"+prop[0].bean.bankAccount.bankCode);
					if (prop.length > 0) {
						//rubah prop product bank account.
						prop[0].bean.rgProductBnAccount.bankAccountKey = app.rgProductBnAccountBankAccountKey.val();
						prop[0].bean.rgProductBnAccount.accountNo = app.rgProductBnAccount.val();
						
						//#1rgProductBnAccountThirdPartyCode
						
						if(prop[0].bean.rgProductBnAccount.bankCode == null)
						{
							prop[0].bean.rgProductBnAccount.bankCode = new Object();
							prop[0].bean.rgProductBnAccount.bankCode.thirdPartyCode = app.rgProductBnAccountThirdPartyCode.val();
						}
						else
						{
							prop[0].bean.rgProductBnAccount.bankCode.thirdPartyCode = app.rgProductBnAccountThirdPartyCode.val();
						}
						
						//#1
						var productTrdparty = app.selectedProductThirdPartyCodeChange.val();
						if(productTrdparty ==''){
							productTrdparty = app.tradeKeyDialog.val() +'|'+app.rgProductBnAccount.val();
						}else{
							productTrdparty = productTrdparty + '#'+app.tradeKeyDialog.val() +'|'+app.rgProductBnAccount.val();
						}
						app.selectedProductThirdPartyCodeChange.val(productTrdparty);
						
						var bankAccountThirdPartyCode = app.selectedInvestorThirdPartyCodeChange.val();
						if(bankAccountThirdPartyCode ==''){
							bankAccountThirdPartyCode = app.tradeKeyDialog.val() +'|'+app.rgProductBnAccountBankAccountKey.val();
						}else{
							bankAccountThirdPartyCode = bankAccountThirdPartyCode + '#'+app.tradeKeyDialog.val() +'|'+app.rgProductBnAccountBankAccountKey.val();
						}
						app.selectedInvestorThirdPartyCodeChange.val(bankAccountThirdPartyCode);
						//end #1
						
						//#2
						var bankCodeChange = app.selectedbankCodeChange.val();
						if(bankCodeChange ==''){
							bankCodeChange = app.tradeKeyDialog.val() +'|'+app.bankAccountBankAccountKey.val();
						}else{
							bankCodeChange = bankCodeChange + '#'+app.tradeKeyDialog.val() +'|'+app.bankAccountBankAccountKey.val();
						}
						app.selectedbankCodeChange.val(bankCodeChange);
						
						var bankCodeNameChange = app.selectedbankCodeNameChange.val();
						if(bankCodeNameChange ==''){
							bankCodeNameChange = app.tradeKeyDialog.val() +'|'+app.bankAccount.val();
						}else{
							bankCodeNameChange = bankCodeChange + '#'+app.tradeKeyDialog.val() +'|'+app.bankAccount.val();
						}
						app.selectedbankCodeNameChange.val(bankCodeNameChange);
						//end #2
						
						//rubah prop investor bank account
						prop[0].bean.bankAccount.bankAccountKey = app.bankAccountBankAccountKey.val();
						prop[0].bean.bankAccount.accountNo = app.bankAccount.val();
						
						if(prop[0].bean.bankAccount.bankCode == null)
						{
							prop[0].bean.bankAccount.bankCode = new Object();
							prop[0].bean.bankAccount.bankCode.thirdPartyCode = app.bankAccountThirdPartyCode.val();
							prop[0].bean.bankAccount.customer.customerName = app.bankAccountBeneficiaryName.val();
						}
						else
						{
							prop[0].bean.bankAccount.bankCode.thirdPartyCode = app.bankAccountThirdPartyCode.val();
							prop[0].bean.bankAccount.customer.customerName = app.bankAccountBeneficiaryName.val();
						}

						//update di view table bank accountnya.
						$("td", prop[0].tr).eq(7).html(prop[0].bean.bankAccount.accountNo);
						$("td", prop[0].tr).eq(8).html(prop[0].bean.bankAccount.bankCode.thirdPartyCode);
						$("td", prop[0].tr).eq(9).html(prop[0].bean.bankAccount.customer.customerName);
					}
					
					app.popupDetail.dialog("close");
                }
			}
		});

		app.cancelDialog.click(function(){
			app.popupDetailPop.dialog("close");
		});

		var backToList = function() {
			location = "@{list()}";
		}

		app.confirm.click(function(){
			app.dispatch.val("confirm");
			//html.fetch("@{RegistryPayment.confirm()}");
			var action = "@{confirm()}";
			$.post(action, $('#searchForm').serialize(), function(data, status, xhr) {
	    		checkRedirect(data);
				loading.dialog('close');
				if (data.remarks == null) {
					messageAlertOk("Payment : " + data.paymentKey + " Successfully", "ui-icon ui-icon-circle-check", "Notification", backToList);
				}else{
					messageAlertOk("Payment : " + data.remarks, "ui-icon ui-icon-circle-check", "Notification", backToList);
				}
				buttonHideShow();
			});
		});

		app.type.change(function(){
			fillFilterByOption('');
			fillFilterByNo(true);
			app.totPayment.val('');
			$('h_totPayment').val('');
			
			app.transferMethod.val('FUND_TRANSFER_METHOD-1');
			
			if ($(this).val() == 'Dividend By Cash') {
				//app.transferMethod.val('FUND_TRANSFER_METHOD-0');
				app.transferMethodError.html('');
			}else{
				app.transferMethodError.html('');
				app.transferMethod.removeAttr('disabled');
				app.filterByNoError.html('');
			}

			app.searchForm.attr('action', 'list');
			app.searchForm.submit();
		});
		
		if (app.type.val() == 'Dividend By Cash') {
			//app.transferMethod.attr('disabled', 'disabled');
			app.transferMethodError.html('');
		}

		app.filterBy.change(function() {
			fillFilterByNo(true);
			app.totPayment.val('');
			$('h_totPayment').val('');
		});

		if (app.dispatch.val() == 'view' || app.dispatch.val() == 'approve') {
			fillFilterByNo(false);
			fillFilterByOption();
		}
		app.datatable.bind("checkbox", function(event, prop, isChecked){
			calculateAmount();
		});
		app.datatable.bind("checkboxs", function(event, prop, isChecked){
			calculateAmount();
		});		
		
	} else {
		return new Payment(html);
	}

}