$(function() {
	var data = new Object();
	trade(data);

	$('#dummy').autoNumeric();
	
	$("#dialogTrade #tradeForm #rgProductBnAccount").lookup({
		list:'@{Pick.bankAccounts()}?by=customer&domain=CUSTOMER',
		get:{
			url:'@{Pick.bankAccountForSettlementAccountPick()}?domain=CUSTOMER',
			success: function(data){
				$("#dialogTrade #tradeForm #rgProductBnAccount").removeClass("fieldError");
				$("#dialogTrade #tradeForm #rgProductBnAccount").val(data.bankAccountNo);
				$("#dialogTrade #tradeForm #rgProductBnAccountBankAccountKey").val(data.bankAccountKey);
				$("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyKey").val(data.thirdPartyKey);
				$("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyCode").val(data.thirdPartyCode);
				$("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyName").val(data.thirdPartyName);
				$("#dialogTrade #tradeForm #rgProductBnAccountBeneficiaryName").val(data.customer.customerName);
				$("#dialogTrade #tradeForm #rgProductBnAccountCurrencyCode").val(data.currency.currencyCode);
			},
			error: function(data){
				$("#dialogTrade #tradeForm #rgProductBnAccount").addClass('fieldError');
				$("#dialogTrade #tradeForm #rgProductBnAccount").val("");
				$("#dialogTrade #tradeForm #rgProductBnAccountBankAccountKey").val("");
				$("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyKey").val("");
				$("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyCode").val("");
				$("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyName").val("");
				$("#dialogTrade #tradeForm #rgProductBnAccountBeneficiaryName").val("");
				$("#dialogTrade #tradeForm #rgProductBnAccountCurrencyCode").val("");
			}
		},
		filter: $("#dialogTrade #tradeForm #productCustomerKeyDialog"),
		description: $("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyName"),
		help: $("#dialogTrade #tradeForm #rgProductBnAccountHelp")
	});

	$("#dialogTrade #tradeForm #rgProductBnAccount").blur(function(){
		if ($("#dialogTrade #tradeForm #rgProductBnAccount").val() == "") {
			$("#dialogTrade #tradeForm #rgProductBnAccount").val("");
			$("#dialogTrade #tradeForm #rgProductBnAccountError").html("");
			$("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyKey").val("");
			$("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyCode").val("");
			$("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyName").val("");
			$("#dialogTrade #tradeForm #rgProductBnAccountBeneficiaryName").val("");
			$("#dialogTrade #tradeForm #rgProductBnAccountCurrencyCode").val("");
		}
	});

	$("#dialogTrade #tradeForm #bankAccount").lookup({
		list:'@{Pick.bankAccounts()}?by=customer&domain=CUSTOMER',
		get:{
			url:'@{Pick.bankAccountForSettlementAccountPick()}?domain=CUSTOMER',
			success: function(data){
				$("#dialogTrade #tradeForm #bankAccount").removeClass("fieldError");
				$("#dialogTrade #tradeForm #bankAccount").val(data.bankAccountNo);;
				$("#dialogTrade #tradeForm #bankAccountBankAccountKey").val(data.bankAccountKey);
				$("#dialogTrade #tradeForm #bankAccountThirdPartyKey").val(data.thirdPartyKey);
				$("#dialogTrade #tradeForm #bankAccountThirdPartyCode").val(data.thirdPartyCode);
				$("#dialogTrade #tradeForm #bankAccountThirdPartyName").val(data.thirdPartyName);
				$("#dialogTrade #tradeForm #bankAccountBeneficiaryName").val(data.customer.customerName);
				$("#dialogTrade #tradeForm #bankAccountCurrencyCode").val(data.currency.currencyCode);
			},
			error: function(data){
				$("#dialogTrade #tradeForm #bankAccount").addClass('fieldError');
				$("#dialogTrade #tradeForm #bankAccount").val("");
				$("#dialogTrade #tradeForm #bankAccountBankAccountKey").val("");
				$("#dialogTrade #tradeForm #bankAccountThirdPartyKey").val("");
				$("#dialogTrade #tradeForm #bankAccountThirdPartyCode").val("");
				$("#dialogTrade #tradeForm #bankAccountThirdPartyName").val("");
				$("#dialogTrade #tradeForm #bankAccountBeneficiaryName").val("");
				$("#dialogTrade #tradeForm #bankAccountCurrencyCode").val("");
			}
		},
		filter: $("#dialogTrade #tradeForm #productCustomerKeyDialog"),
		description: $("#dialogTrade #tradeForm #bankAccountThirdPartyName"),
		help: $("#dialogTrade #tradeForm #bankAccountHelp")
	});

	$("#dialogTrade #tradeForm #bankAccount").blur(function(){
		if ($("#dialogTrade #tradeForm #bankAccount").val() == "") {
			$("#dialogTrade #tradeForm #bankAccount").val("");
			$("#dialogTrade #tradeForm #bankAccountError").html("");
			$("#dialogTrade #tradeForm #bankAccountKey").val("");
			$("#dialogTrade #tradeForm #bankAccountThirdPartyKey").val("");
			$("#dialogTrade #tradeForm #bankAccountThirdPartyCode").val("");
			$("#dialogTrade #tradeForm #bankAccountThirdPartyName").val("");
			$("#dialogTrade #tradeForm #bankAccountBeneficiaryName").val("");
			$("#dialogTrade #tradeForm #bankAccountCurrencyCode").val("");
		}
	});
	
	var closeDialog = function() {
        $("#dialog-message").dialog('close');   
    };

	$('#addTradeDialog').button();
	$('#cancelTradeDialog').button();

	$("#dialogTrade").dialog({
        autoOpen:false,
        modal:true,
        heigth:'420px',
        width:'950px',
        resizable:false
    });

	$("#cancelTradeDialog").click(function() {
		$("#dialogTrade #tradeForm #rgProductBnAccount").val("");
		$("#dialogTrade #tradeForm #investorBankAccountNo").val("");
    	$("#dialogTrade #tradeForm #bankAccount").removeClass('fieldError');
        $("#dialogTrade #tradeForm #rgProductBnAccount").removeClass('fieldError');
    	$("#dialogTrade #tradeForm #rgProductBnAccountError").html("");
    	$("#dialogTrade #tradeForm #bankAccountError").html("");
        $("#dialogTrade").dialog('close');
        return false;
    });

    $("#listTrade #tradeTable tbody tr #deleteButtonTrade").live("click", function() {
        var row = $(this).parents('tr');
        var rowNumber = tableTrade.fnGetPosition(row[0]);
        var deleteTrade = function(){
            tableTrade.fnDeleteRow(rowNumber);
            $("#dialog-message").dialog('close');
        };
        messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteTrade, closeDialog);
        return false;
    });

    $('#addTradeDialog').die("click");
    $('#addTradeDialog').live("click", function(){
        setTimeout(function() {
	        var table = $("#listTrade #tradeTable").dataTable();
	        var rowPosition = $("#dialogTrade #tradeForm #rowPosition").val();
	        var dataTrades = table.fnGetData(parseFloat(rowPosition));

	        saveTrade();

	        function saveTrade()
	        {
	        	if($("#dialogTrade #tradeForm #bankAccount").isEmpty() || $("#dialogTrade #tradeForm #rgProductBnAccount").isEmpty())
	        	{
            		$("#dialogTrade #tradeForm").find("span[id*='Error']").html("");

        			if($("#dialogTrade #tradeForm #bankAccount").val() == "")
	                {
	                    $("#bankAccountError").html('Required').show();
	                }
        			
        			if($("#dialogTrade #tradeForm #rgProductBnAccount").val() == "")
	                {
	                    $("#rgProductBnAccountError").html('Required').show();
	                }
	        	}
	            else
	            {
	            	$("#dialogTrade #tradeForm #bankAccount").removeClass('fieldError');
	            	$("#dialogTrade #tradeForm #rgProductBnAccount").removeClass('fieldError');
	            	$("#dialogTrade #tradeForm #bankAccountError").html("");
	            	$("#dialogTrade #tradeForm #rgProductBnAccountError").html("");

	                if(rowPosition != "")
	                {
	                	var found = false;

	                	var currencyProduct = $("#dialogTrade #tradeForm #currencyDialog").val();
						var currencyProductBankAccount = $("#dialogTrade #tradeForm #rgProductBnAccountCurrencyCode").val();
						var currencyInvestorBankAccount = $("#dialogTrade #tradeForm #bankAccountCurrencyCode").val();

						if (currencyProduct != currencyProductBankAccount)
						{
							$("#dialogTrade #tradeForm #rgProductBnAccountErrorGlobal").html("Product Bank Account Currency must be same with Product Currency").show();
							found = true;
						}
						else
						{
							$("#dialogTrade #tradeForm #rgProductBnAccountErrorGlobal").html("");
						}

						if (currencyProduct != currencyInvestorBankAccount)
						{
							$("#dialogTrade #tradeForm #bankAccountErrorGlobal").html("Investor Bank Account Currency must be same with Product Currency").show();
							found = true;
						}
						else
						{
							$("#dialogTrade #tradeForm #rgProductBnAccountErrorGlobal").html("");
						}

						if(!found)
	                    {
							$("#dialogTrade #tradeForm #rgProductBnAccountErrorGlobal").html("");

							if(dataTrades.rgProductBnAccount != null)
				 			{
								table.fnUpdate(dataTrades.rgProductBnAccount.bankAccountKey = $("#dialogTrade #tradeForm #rgProductBnAccountBankAccountKey").val(), parseFloat(rowPosition), 2);
								table.fnUpdate(dataTrades.rgProductBnAccount.accountNo = $("#dialogTrade #tradeForm #rgProductBnAccount").val(), parseFloat(rowPosition), 0);
		                    	table.fnUpdate(dataTrades.rgProductBnAccount.bankCode.thirdPartyCode = $("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyCode").val(), parseFloat(rowPosition), 0);
								table.fnUpdate(dataTrades.rgProductBnAccount.accountNo = $("#dialogTrade #tradeForm #rgProductBnAccount").val(), parseFloat(rowPosition), 2);
		                    	table.fnUpdate(dataTrades.rgProductBnAccount.bankCode.thirdPartyCode = $("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyCode").val(), parseFloat(rowPosition), 2);
				 			}
							else
							{
								dataTrades.rgProductBnAccount = new Object();
								dataTrades.rgProductBnAccount.bankCode = new Object();
								table.fnUpdate(dataTrades.rgProductBnAccount.bankAccountKey = $("#dialogTrade #tradeForm #rgProductBnAccountBankAccountKey").val(), parseFloat(rowPosition), 2);
								table.fnUpdate(dataTrades.rgProductBnAccount.accountNo = $("#dialogTrade #tradeForm #rgProductBnAccount").val(), parseFloat(rowPosition), 0);
		                    	table.fnUpdate(dataTrades.rgProductBnAccount.bankCode.thirdPartyCode = $("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyCode").val(), parseFloat(rowPosition), 0);
		                    	table.fnUpdate(dataTrades.rgProductBnAccount.accountNo = $("#dialogTrade #tradeForm #rgProductBnAccount").val(), parseFloat(rowPosition), 2);
		                    	table.fnUpdate(dataTrades.rgProductBnAccount.bankCode.thirdPartyCode = $("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyCode").val(), parseFloat(rowPosition), 2);
							}

				 			if(dataTrades.bankAccount != null)
				 			{
				 				table.fnUpdate(dataTrades.bankAccount.bankAccountKey = $("#dialogTrade #tradeForm #bankAccountBankAccountKey").val(), parseFloat(rowPosition), 2);
				 				table.fnUpdate(dataTrades.bankAccount.accountNo = $("#dialogTrade #tradeForm #bankAccount").val(), parseFloat(rowPosition), 0);
		                        table.fnUpdate(dataTrades.bankAccount.bankCode.thirdPartyCode = $("#dialogTrade #tradeForm #bankAccountThirdPartyCode").val(), parseFloat(rowPosition), 0);
		                        table.fnUpdate(dataTrades.bankAccount.accountNo = $("#dialogTrade #tradeForm #bankAccount").val(), parseFloat(rowPosition), 2);
		                        table.fnUpdate(dataTrades.bankAccount.bankCode.thirdPartyCode = $("#dialogTrade #tradeForm #bankAccountThirdPartyCode").val(), parseFloat(rowPosition), 2);
				 			}
				 			else
				 			{
				 				dataTrades.bankAccount = new Object();
								dataTrades.bankAccount.bankCode = new Object();
				 				table.fnUpdate(dataTrades.bankAccount.bankAccountKey = $("#dialogTrade #tradeForm #bankAccountBankAccountKey").val(), parseFloat(rowPosition), 2);
				 				table.fnUpdate(dataTrades.bankAccount.accountNo = $("#dialogTrade #tradeForm #bankAccount").val(), parseFloat(rowPosition), 0);
		                        table.fnUpdate(dataTrades.bankAccount.bankCode.thirdPartyCode = $("#dialogTrade #tradeForm #bankAccountThirdPartyCode").val(), parseFloat(rowPosition), 0);
		                        table.fnUpdate(dataTrades.bankAccount.accountNo = $("#dialogTrade #tradeForm #bankAccount").val(), parseFloat(rowPosition), 2);
		                        table.fnUpdate(dataTrades.bankAccount.bankCode.thirdPartyCode = $("#dialogTrade #tradeForm #bankAccountThirdPartyCode").val(), parseFloat(rowPosition), 2);
				 			}

//				 			$('#form #listTrade #tradeTable input[name="option"]').eq(rowPosition).attr("productBankAccountKey", dataTrades.rgProductBnAccount.bankAccountKey);
//				 			$('#form #listTrade #tradeTable input[name="option"]').eq(rowPosition).attr("investorBankAccountKey", dataTrades.bankAccount.bankAccountKey);
							$("#dialogTrade #tradeForm #rgProductBnAccount").val("");
							$("#dialogTrade #tradeForm #investorBankAccountNo").val("");

	                        $('#dialogTrade').dialog('close');
	                    }
	                }
	                return false;       
	            }
	        }
       }, 500);
    });
});

function trade(data) {
    var fmtDate = '${appProp.jqueryDateFormat}';

	var trades;

	if ('${trades}' != '')
	{
		trades = ${trades.raw()};
	}
	else
	{
		trades = new Array();
	}

    tableTrade = 
        $('#form #listTrade #tradeTable').dataTable({
            aaData: trades,
            aoColumns: [  {
            				bSortable:false,
                            fnRender: function(obj) {
                                var controls;
                                var productBankAccountKey;
                                var investorBankAccountKey;
                                
					 			if(obj.aData.rgProductBnAccount != null)
					 			{
					 				productBankAccountKey = obj.aData.rgProductBnAccount.bankAccountKey;
					 			}

					 			if(obj.aData.bankAccount != null)
					 			{
					 				investorBankAccountKey = obj.aData.bankAccount.bankAccountKey;
					 			}

					 			var tradeKeys = $("#selected").val().split(",");

					 			if(tradeKeys[obj.iDataRow] == obj.aData.tradeKey)
					 			{
					 				controls = "<input name='option' type='checkbox' value='" + obj.aData.tradeKey 
						 			+ "' nominal='" + obj.aData.amount
						 			+ "' productBankAccountKey='"+ productBankAccountKey
						 			+ "' investorBankAccountKey='" + investorBankAccountKey + "' checked />";
					 			}
					 			else
					 			{
					 				controls = "<input name='option' type='checkbox' value='" + obj.aData.tradeKey 
						 			+ "' nominal='" + obj.aData.amount
						 			+ "' productBankAccountKey='"+ productBankAccountKey
						 			+ "' investorBankAccountKey='" + investorBankAccountKey + "' />";
					 			}

					 			return controls;
                             }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  var stringSetDate = obj.aData.paymentDate.toString();
				            		
                                  if (stringSetDate.substr(2,1) != "/") {
                                	  controls = "<center>" + $.datepicker.formatDate(fmtDate, new Date(obj.aData.paymentDate)) + "</center>";
                                  } else {
                                	  controls = "<center>" + obj.aData.paymentDate + "</center>";
                                  }
                                  
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  
                                  var productBankAccountKey;
                                  var investorBankAccountKey;
                                  
	  					 	   	  if(obj.aData.rgProductBnAccount != null)
	  					 		  {
	  					 	   		  productBankAccountKey = obj.aData.rgProductBnAccount.bankAccountKey;
	  					 		  }
	                                  
	  					 	   	  if(obj.aData.bankAccount != null)
	  					 	   	  {
	  					 	   		  investorBankAccountKey = obj.aData.bankAccount.bankAccountKey;
	  					 		  }
                                  
                                  controls = obj.aData.tradeKey;
                                  
                                  controls += '<input type="hidden" name="rgTrades[' + obj.iDataRow + '].rgProductBnAccount.bankAccountKey" value="' + productBankAccountKey + '" />';
	  					 		  //controls += '<input type="hidden" name="rgTrades[' + obj.iDataRow + '].rgProductBnAccount.accountNo" value="' + obj.aData.rgProductBnAccount.accountNo + '" />';
	  					 		  //controls += '<input type="hidden" name="rgTrades[' + obj.iDataRow + '].rgProductBnAccount.thirdPartyCode" value="' + obj.aData.rgProductBnAccount.bankCode.thirdPartyCode + '" />';
	
	  					 		  controls += '<input type="hidden" name="rgTrades[' + obj.iDataRow + '].bankAccount.bankAccountKey" value="' + investorBankAccountKey + '" />';
	  					 		  //controls += '<input type="hidden" name="rgTrades[' + obj.iDataRow + '].bankAccount.accountNo" value="' + obj.aData.bankAccount.accountNo + '" />';
	  					 		  //controls += '<input type="hidden" name="rgTrades[' + obj.iDataRow + '].bankAccount.bankCode.thirdPartyCode" value="' + obj.bankAccount.bankCode.thirdPartyCode + '" />';
	
	  					 		  controls += '<input type="hidden" name="rgTrades[' + obj.iDataRow + '].tradeKey" value="' + obj.aData.tradeKey + '" />';
	  					 		  controls += '<input type="hidden" name="rgTrades[' + obj.iDataRow + '].liquidation" value="' + obj.aData.liquidation + '" />';

                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  controls = obj.aData.rgInvestmentaccount.accountNumber;
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  controls = obj.aData.rgInvestmentaccount.name;
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  var stringSetDate = obj.aData.tradeDate.toString();
				            		
                                  if (stringSetDate.substr(2,1) != "/") {
                                	  controls = "<center>" + $.datepicker.formatDate(fmtDate, new Date(obj.aData.tradeDate)) + "</center>";
                                  } else {
                                	  controls = "<center>"+ obj.aData.tradeDate + "</center>";
                                  }
                                  
                                  return controls;
                              }
                          }
                          ,
                          {
                        	  sClass: 'numeric',
                              fnRender: function(obj) {
                                  var controls;
                                  controls = obj.aData.amount.fmtNumber(2);
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  controls = obj.aData.bankAccount.accountNo;
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  controls = obj.aData.bankAccount.bankCode.thirdPartyCode;
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                            	  var controls;
                            	  controls = obj.aData.bankAccount.customer.customerName;
                                  return controls;
                              }
                          }
                        ],
        aaSorting: [[2, 'asc']],
		bDestroy: true,
		bJQueryUI: true,
		bInfo: true,
		bPaginate: true,
		iDisplayLength: 10,
		bScrollCollapse: true,
		sPaginationType: 'full_numbers'
    });

    $("#listTrade #tradeTable").removeAttr('style');
    $("#listTrade #tradeTable tbody tr td").die('click');
    $("#listTrade #tradeTable tbody tr td").live('click', function(){
        var rowPos= $(this).parents('tr');
        var rowPosNumber = tableTrade.fnGetPosition(rowPos[0]);

        if(rowPosNumber != null)
		{
        	var pos = tableTrade.fnGetPosition(this);
            dataTrades = tableTrade.fnGetData(this.parentNode);

            if (pos[1] != 0) {
                $('#dialogTrade #tradeForm').removeClass('fieldError');
                $("#dialogTrade #tradeForm .errmsg").html("");
                $("#dialogTrade #tradeForm").find("span[id*='Error']").html("");
                $("#dialogTrade #tradeForm #rowPosition").val(rowPosNumber);

                $("#dialogTrade #tradeForm #tradeKeyDialog").val(dataTrades.tradeKey);
                $("#dialogTrade #tradeForm #productCustomerKeyDialog").val(dataTrades.rgProduct.cfMaster.customerKey);
                $("#dialogTrade #tradeForm #fundCodeDialog").val(dataTrades.rgProduct.productCode);
                $("#dialogTrade #tradeForm #fundCodeDescDialog").val(dataTrades.rgProduct.name);
                $("#dialogTrade #tradeForm #currencyDialog").val(dataTrades.rgProduct.currency.currencyCode);
                $("#dialogTrade #tradeForm #accountDialog").val(dataTrades.rgInvestmentaccount.accountNumber);
                $("#dialogTrade #tradeForm #accountDescDialog").val(dataTrades.rgInvestmentaccount.name);
                $("#dialogTrade #tradeForm #transactionDateDialog").val($.datepicker.formatDate(fmtDate, new Date(dataTrades.tradeDate)));
                $("#dialogTrade #tradeForm #paymentDateDialog").val($.datepicker.formatDate(fmtDate, new Date(dataTrades.paymentDate)));

                if(dataTrades.saCode != null)
                {
                	$("#dialogTrade #tradeForm #sellingAgentDialog").val(dataTrades.saCode.thirdPartyCode);
                    $("#dialogTrade #tradeForm #sellingAgentDescDialog").val(dataTrades.saCode.thirdPartyName);
                }

                $("#dialogTrade #tradeForm #externalReferenceDialog").val(dataTrades.externalReference);
                
                if(dataTrades.rgTransaction != null)
                {
	                $("#dialogTrade #tradeForm #unitDialog").autoNumericSet(dataTrades.rgTransaction.unit, {aPad:true,mDec:2}).val();
	                $("#dialogTrade #tradeForm #priceDialog").autoNumericSet(dataTrades.rgTransaction.price, {aPad:true,mDec:2}).val();
	                $("#dialogTrade #tradeForm #amountDialog").autoNumericSet(dataTrades.rgTransaction.amount, {aPad:true,mDec:2}).val();
	                
	                var feeAmount = Number(dataTrades.rgTransaction.feeAmount);
	                var discAmount = Number(dataTrades.rgTransaction.discAmount);
	                var otherAmount = Number(dataTrades.rgTransaction.otherAmount);
	                var totalFeeAmount = feeAmount - discAmount + otherAmount;
	                $("#dialogTrade #tradeForm #totalFeeAmountDialog").autoNumericSet(totalFeeAmount, {aPad:true,mDec:2}).val();
                }

                $("#dialogTrade #tradeForm #paymentDialog").autoNumericSet(dataTrades.amount, {aPad:true,mDec:2}).val();

                $("#dialogTrade #tradeForm #productBankAccount b").text(dataTrades.rgProduct.name);
    			$("#dialogTrade #tradeForm #investorBankAccount b").text(dataTrades.rgInvestmentaccount.name);

                if(dataTrades.rgProductBnAccount != null)
                {
                	$("#dialogTrade #tradeForm #rgProductBnAccount").val(dataTrades.rgProductBnAccount.accountNo);
                    $("#dialogTrade #tradeForm #rgProductBnAccountBankAccountKey").val(dataTrades.rgProductBnAccount.bankAccountKey);
                    $("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyKey").val(dataTrades.rgProductBnAccount.bankCode.thirdPartyKey);
                    $("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyCode").val(dataTrades.rgProductBnAccount.bankCode.thirdPartyCode);
                    $("#dialogTrade #tradeForm #rgProductBnAccountThirdPartyName").val(dataTrades.rgProductBnAccount.bankCode.thirdPartyName);

                    if('${confirming}' || '${mode}' == 'view')
                    {
                    	$("#dialogTrade #tradeForm #rgProductBnAccountBeneficiaryName").val(dataTrades.rgProductBnAccount.customer.customerName);
                        $("#dialogTrade #tradeForm #rgProductBnAccountCurrencyCode").val(dataTrades.rgProductBnAccount.currency.currencyCode);
                    }
                    else
                    {
                    	if(dataTrades.rgProductBnAccount.accountNo != '' && dataTrades.rgProductBnAccount.bankCode.thirdPartyCode != '')
            			{
                        	 $("#dialogTrade #tradeForm #rgProductBnAccount").val(dataTrades.rgProductBnAccount.accountNo + "|" + dataTrades.rgProductBnAccount.bankCode.thirdPartyCode);
                        	 //$("#dialogTrade #tradeForm #rgProductBnAccount").blur();
            			}
                    }
                }

                if(dataTrades.bankAccount != null)
                {
	                $("#dialogTrade #tradeForm #bankAccount").val(dataTrades.bankAccount.accountNo);
	                $("#dialogTrade #tradeForm #bankAccountBankAccountKey").val(dataTrades.bankAccount.bankAccountKey);
	                $("#dialogTrade #tradeForm #bankAccountThirdPartyKey").val(dataTrades.bankAccount.bankCode.thirdPartyKey);
	                $("#dialogTrade #tradeForm #bankAccountThirdPartyCode").val(dataTrades.bankAccount.bankCode.thirdPartyCode);
	                $("#dialogTrade #tradeForm #bankAccountThirdPartyName").val(dataTrades.bankAccount.bankCode.thirdPartyName);
	                $("#dialogTrade #tradeForm #bankAccountBeneficiaryName").val(dataTrades.bankAccount.bankCode.customerName);
	                $("#dialogTrade #tradeForm #bankAccountCurrencyCode").val(dataTrades.bankAccount.currency.currencyCode);

	                if(dataTrades.bankAccount.accountNo != '' && dataTrades.bankAccount.bankCode.thirdPartyCode != '')
	    			{
	    				 $("#dialogTrade #tradeForm #bankAccount").val(dataTrades.bankAccount.accountNo + "|" + dataTrades.bankAccount.bankCode.thirdPartyCode);
	    				 $("#dialogTrade #tradeForm #bankAccount").blur();
	    			}
                }

                $("#dialogTrade").dialog('open');
                $('.ui-widget-overlay').css('height',$('body').height());
                $("#cancelTradeDialog").focus();
            }
		}
    });
};