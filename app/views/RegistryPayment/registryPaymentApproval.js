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
		
		var TRADETYPE_REDEMPTION = "Redemption";

		var closeDialogMessage = function() {
			$("#dialog-message").dialog("close");
		};
		
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
			p.selected = app.selected.val();
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

/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.accordion.accordion({collapsible: true});
		
		app.populate.add(app.save).add(app.reset).add(app.confirm).add(app.back).add(app.close).add(app.saveDialog).add(app.cancelDialog).add(app.approve).add(app.reject).add(app.closeWorkflow).button();

		

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
			if (app.type.val() == TRADETYPE_REDEMPTION) {
				if ((filterByHidden == NO_FILTER)||(filterByHidden == '')) {
					$('#filterBy option').remove();
					var options = $('#filterBy').attr('options');
					options[options.length] = new Option('ALL', NO_FILTER);
					options[options.length] = new Option('TRANSACTION NO', NO_TRX);
				} else {
					$("option", app.filterBy).eq(2).remove();
				}

//				if(app.dispatch.val() == 'approve' && app.filterByNo.value() != null)
//				{
//					$('#filterBy option').remove();
//					var options = $('#filterBy').attr('options');
//					options[options.length] = new Option('Transaction No', NO_TRX);
//				}
			} else {
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
			var valid = validate();
			app.query.val(valid);
			app.datatable.fnPageChange("first");
		});

		app.reset.add(app.close).click(function(){
			app.searchForm.attr('action', 'reset');
			app.searchForm.submit();
		});

		app.back.click(function(){
			app.totalPaidAmount.value(0);
			app.searchForm.attr('action', 'list');
			app.searchForm.submit();
		});
		
		app.save.click(function(){
			app.datatable.trigger("fetch", [0, "checked"]);
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
					if (prop.length > 0) {
						//rubah prop product bank account.
						prop[0].bean.rgProductBnAccount.bankAccountKey = app.rgProductBnAccountBankAccountKey.val();
						prop[0].bean.rgProductBnAccount.accountNo = app.rgProductBnAccount.val();

						if(prop[0].bean.rgProductBnAccount.bankCode == null)
						{
							prop[0].bean.rgProductBnAccount.bankCode = new Object();
							prop[0].bean.rgProductBnAccount.bankCode.thirdPartyCode = app.rgProductBnAccountThirdPartyCode.val();
						}
						else
						{
							prop[0].bean.rgProductBnAccount.bankCode.thirdPartyCode = app.rgProductBnAccountThirdPartyCode.val();
						}

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
				messageAlertOk("Payment : " + data.paymentKey + " Successfully", "ui-icon ui-icon-circle-check", "Notification", backToList);
				buttonHideShow();
			});
		});

		app.type.change(function(){
			fillFilterByOption('');
			fillFilterByNo(true);
			app.totPayment.val('');
			$('h_totPayment').val('');
		});

		app.filterBy.change(function() {
			fillFilterByNo(true);
			app.totPayment.val('');
			$('h_totPayment').val('');
		});

		if (app.dispatch.val() == 'view' || app.dispatch.val() == 'approve') {
			fillFilterByNo(false);
			fillFilterByOption();
		}

		$(".dataTables_scrollHeadInner thead th div input[type='checkbox']").live("change", function() {
			alert("mocca");
			var isChecked = $(this).checked();
			var table = app.datatable;
	        var rows = table.fnGetNodes().length;
	        var totalAmount = 0;

			if(isChecked == "on")
			{
				for (var i = 0; i < rows; i++)
		        {
		            var cells = table.fnGetData(i);
		            var amount = cells[6].replaceAll(",", "");
		            totalAmount += Number(amount);
		        }

				app.totPayment.valueRnd(totalAmount, true, roundDigit, roundType);
				app.totalPaidAmount.value(totalAmount);
			}
			else
			{
				app.totPayment.value(0);
				app.totalPaidAmount.value(0);
			}
		});
		
		$("#tblTransaction").live("change", function() {
	        var nominal = 0;
	        var totalPaidAmount = 0;
	      
	        $("tbody tr", app.tblTransaction).each(function(){
	        	var isChecked = $(this).find(':checkbox:eq(0)').checked();
	        	var amount = $(this).find('td:eq(6)').text().replaceAll(",", "");
	        	if(isChecked == "on")
	        	{
	        		nominal = Number(nominal) + Number(amount);
	        		
	        	}
	        	
	        });
	        app.totPayment.valueRnd(nominal, true, roundDigit, roundType);
    		app.totalPaidAmount.value(nominal);
		});
		
		$("#tblTransaction tbody tr").live("change", function() {
			var isChecked = $(this).find(':checkbox:eq(0)').checked();
	        var nominal = $(this).find('td:eq(6)').text().replaceAll(",", "");

	        if(app.totalPaidAmount.val() == 0)
	        {
	        	if(isChecked == "on")
	        	{
	        		app.totPayment.valueRnd(nominal, true, roundDigit, roundType);
	        		app.totalPaidAmount.value(nominal);
	        	};
	        }
	        else
	        {
	        	var oldNominal = app.totalPaidAmount.val();
	        	var totalNominal;

	        	if(isChecked == "on")
	        	{
	        		totalNominal = Number(oldNominal) + Number(nominal);
	        		app.totPayment.valueRnd(totalNominal, true, roundDigit, roundType);
	        		app.totalPaidAmount.value(totalNominal);
	        	}
	        	else
	        	{
	        		totalNominal = Number(oldNominal) - Number(nominal);
	        		app.totPayment.valueRnd(totalNominal, true, roundDigit, roundType);
	        		app.totalPaidAmount.value(totalNominal);
	        	};
	        };
		});

	} else {
		return new Payment(html);
	}

}