$(function() {
	$("#radioStatus${prd?.defaultProductBankAccount}").attr("checked","checked");

	$('#dialogBankAccount #bankAccountNo').lookup({
		list:'@{Pick.bankAccounts()}?by=customer&domain=CUSTOMER',
		get:{
//			url:'@{Pick.bankAccountForSettlementAccountPick()}?domain=CUSTOMER',
			url:'@{Pick.bankAccountByCustomerKey()}?domain=CUSTOMER',
			success: function(data){
				$("#dialogBankAccount #bankAccountForm #bankAccountNo").removeClass("fieldError");
				//var codeSplit = $('#bankAccountNo').val().split("|");
				$("#dialogBankAccount #bankAccountForm #bankAccountNo").val(data.bankAccountNo);
				$("#dialogBankAccount #bankAccountForm #bankAccountKey").val(data.bankAccountKey);
				$("#dialogBankAccount #bankAccountForm #thirdPartyKey").val(data.thirdPartyKey);
				$("#dialogBankAccount #bankAccountForm #thirdPartyCode").val(data.thirdPartyCode);
				$("#dialogBankAccount #bankAccountForm #thirdPartyName").val(data.thirdPartyName);
				$("#dialogBankAccount #bankAccountForm #beneficiaryName").val(data.name);
				$("#dialogBankAccount #bankAccountForm #currencyCode").val(data.currency.currencyCode);
			},
			error: function(data){
				$("#dialogBankAccount #bankAccountForm #bankAccountNo").addClass('fieldError');
				$("#dialogBankAccount #bankAccountForm #bankAccountNo").val('');
				$("#dialogBankAccount #bankAccountForm #bankAccountKey").val('');
				$("#dialogBankAccount #bankAccountForm #thirdPartyKey").val('');
				$("#dialogBankAccount #bankAccountForm #thirdPartyCode").val('');
				$("#dialogBankAccount #bankAccountForm #thirdPartyName").val('');
				$("#dialogBankAccount #bankAccountForm #beneficiaryName").val('');
				$("#dialogBankAccount #bankAccountForm #currencyCode").val('');
			}
		},
		filter: $("#customerNoKey"),
		description: $("#thirdPartyName"),
		help: $("#bankAccountNoHelp")
	});

	$("#dialogBankAccount #bankAccountNo").blur(function(){
		if ($("#dialogBankAccount #bankAccountNo").val()=="") {
			$("#dialogBankAccount #bankAccountNo").val("");
			$("#dialogBankAccount #bankCodeKey").val("");
			$("#dialogBankAccount #bankCodeDesc").val("");
			$("#dialogBankAccount #beneficiaryName").val("");
			$("#dialogBankAccount #currencyCode").val("");
			$("#dialogBankAccount #thirdPartyKey").val('');
			$("#dialogBankAccount #thirdPartyCode").val('');
			$("#dialogBankAccount #thirdPartyName").val('');
		}
	});

	var closeDialog = function() {
        $("#dialog-message").dialog('close');   
    }

	var data = new Object();
    bankAccount(data);

	$('#addBankDialog').button();
	$('#cancelBankDialog').button();

	$("#dialogBankAccount").dialog({
		autoOpen:false,
		modal:true,
		heigth:'600px',
		width:'600px',
		resizable:false
	});

    $("#listBankAccount #bankAccountTable tbody tr #deleteButtonBank[disabled!=true]").live("click", function() {
        var row = $(this).parents('tr');
        var rowNumber = tableBankAccount.fnGetPosition(row[0]);

//        var deleteBankAccount = function(){
//            tableBankAccount.fnDeleteRow(rowNumber);
//            reordering();
//            $("#dialog-message").dialog('close');
//        }

		var data = tableBankAccount.fnGetData(row[0]);
		var deleteBankAccount = function() {
			if (data.defaultRgProductBankAccount == $('#defaultProductBankAccount').val()){
				$('#btnBankAccountAddError').html("Cannot Delete Default Bank Account");
				$("#dialog-message").dialog("close");
				return false;
			} else {
				$('#btnBankAccountAddError').html("");
			}
			tableBankAccount.fnDeleteRow(rowNumber);
			$("#dialog-message").dialog("close");
		}
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccount, closeDialog);
		return false;
	});

    $("#listBankAccount #bankAccountTable tbody tr td input[name=defaultRgProductBankAccount]").live('change', function(){
    //	$('input[name=defaultRgProductBankAccount]').click(function(){
    	$('#btnBankAccountAddError').html('');
    	var radios = $("[type=radio]", $(this).parents('tr td'));
    	$('#defaultProductBankAccount').val($(radios).val());
    });

    $('#addBankDialog').die("click");
    $('#addBankDialog').live("click", function(){
    	setTimeout(function() {
	        var table = $("#listBankAccount #bankAccountTable").dataTable();
	        var rowPosition = $("#dialogBankAccount #bankAccountForm #rowPosition").val();
	        saveBankAccount();

	        function saveBankAccount()
	        {
	        	if($("#dialogBankAccount #bankAccountForm #bankAccountNo").isEmpty())
	        	{
	        		$("#dialogBankAccount #bankAccountForm").find("span[id*='Error']").html("");

	        		if($("#dialogBankAccount #bankAccountForm #bankAccountNo").val()=="")
	                {
	                    $("#bankAccountNoError").html('Required').show();
	                }
	        	}
	            else
	            {
	                var dataBankAccounts = table.fnGetData(parseFloat(rowPosition));
	                if(rowPosition != "")
	                {
	                    var found = false;
	                    // CHECKED DUPLICATE ROW
	                    var rows = table.fnGetNodes().length;
	                    for (i = 0; i < rows; i++)
	                    {
	                        var cells = table.fnGetData(i);
	                        var bankAccountNo = cells.bnAccount.accountNo;
	                        var thirdPartyKey = cells.bnAccount.bankCode.thirdPartyKey;
	                        var thirdPartyCode = cells.bnAccount.bankCode.thirdPartyCode;

	                        if (($("#dialogBankAccount #bankAccountForm #thirdPartyKey").val() == thirdPartyKey) 
 	                        		&& ($("#dialogBankAccount #bankAccountForm #thirdPartyCode").val() == thirdPartyCode)
 	                        		&& ($("#dialogBankAccount #bankAccountForm #bankAccountNo").val() == bankAccountNo))
 	                        {
 	                        	$("#dialogBankAccount #bankAccountForm #errmsgNull").html("Bank Account already Exist!");
 	                            found = true;
 	                            break;
 	                        }
	                    }

	                    if ($("#customerNoKey").isEmpty())
						{
							$("#dialogBankAccount #bankAccountForm #errmsgNull").html("Customer Code cannot be empty").show();
							found = true;
						}
	                    
	                    var currencyProduct = $("#currencyKey").val();
						var currencyBankAccount = $("#dialogBankAccount #bankAccountForm #currencyCode").val();
						if (currencyProduct != currencyBankAccount)
						{
							$("#dialogBankAccount #bankAccountForm #errmsgNull").html("Bank Account Currency must be same with Product Currency").show();
							found = true;
						}
	                    
	                    if(!found)
	                    {
	                    	table.fnUpdate(dataBankAccounts.bankAccountKey = $("#dialogBankAccount #bankAccountForm #bankAccountKey").val(),parseFloat(rowPosition), 0);
 	                        //table.fnUpdate(dataBankAccounts.productCode = $("#dialogBankAccount #bankAccountForm #productCode").val(),parseFloat(rowPosition), 1);
 	                        
 	                        var constantDefaultRgProductBankAccount = $("#dialogBankAccount #bankAccountForm #thirdPartyKey").val() + $("#dialogBankAccount #bankAccountForm #bankAccountNo").val();
 							data.defaultRgProductBankAccount = constantDefaultRgProductBankAccount.split(' ').join('_');
 							if ($("#dialogBankAccount #bankAccountForm #oldDefaultRgProductBankAccount").val() == $("#defaultRgProductBankAccount").val()) {
 								$("#defaultRgProductBankAccount").val(data.defaultRgProductBankAccount);
 							}
 	                        
	                        $('#dialogBankAccount').dialog('close');
	                    }
	                }
	                else
	                {
	                    var found = false;
	                    // CHECKED DUPLICATE ROW
	                    var rows = table.fnGetNodes().length;
	                    for (i = 0; i < rows; i++)
	                    {
	                        var cells = table.fnGetData(i);
	                        var bankAccountNo = cells.bnAccount.accountNo;
	                        var thirdPartyKey = cells.bnAccount.bankCode.thirdPartyKey;
	                        var thirdPartyCode = cells.bnAccount.bankCode.thirdPartyCode;

 	                        if (($("#dialogBankAccount #bankAccountForm #thirdPartyKey").val() == thirdPartyKey) 
 	                        		&& ($("#dialogBankAccount #bankAccountForm #thirdPartyCode").val() == thirdPartyCode)
 	                        		&& ($("#dialogBankAccount #bankAccountForm #bankAccountNo").val() == bankAccountNo))
 	                        {
 	                        	$("#dialogBankAccount #bankAccountForm #errmsgNull").html("Bank Account already Exist!");
 	                            found = true;
 	                            break;
 	                        }
	                    }
	                    
	                    if ($("#customerNoKey").isEmpty())
						{
							$("#dialogBankAccount #bankAccountForm #errmsgNull").html("Customer Code cannot be empty").show();
							found = true;
						}

	                    var currencyProduct = $("#currencyKey").val();
						var currencyBankAccount = $("#dialogBankAccount #bankAccountForm #currencyCode").val();
						if (currencyProduct != currencyBankAccount)
						{
							$("#dialogBankAccount #bankAccountForm #errmsgNull").html("Bank Account Currency must be same with Product Currency").show();
							found = true;
						}

	                    if(!found)
	                    {
	                        var dataBankAccount = new Object();
	                        dataBankAccount.bnAccount = new Object();
	                        dataBankAccount.bnAccount.bankCode = new Object();
	                        dataBankAccount.bnAccount.customer = new Object();
	                        dataBankAccount.bnAccount.currency = new Object();
	                        dataBankAccount.id = new Object();
	                        //dataBankAccount.bankAccountKey = $("#dialogBankAccount #bankAccountForm #bankAccountKey").val();
	                        //dataBankAccount.productCode = $("#dialogBankAccount #bankAccountForm #productCode").val();
	                        dataBankAccount.bnAccount.accountNo = $("#dialogBankAccount #bankAccountForm #bankAccountNo").val();
	                        dataBankAccount.bnAccount.bankCode.thirdPartyKey = $("#dialogBankAccount #bankAccountForm #thirdPartyKey").val();
	                        dataBankAccount.bnAccount.bankCode.thirdPartyCode = $("#dialogBankAccount #bankAccountForm #thirdPartyCode").val();
	                        dataBankAccount.bnAccount.bankCode.thirdPartyName = $("#dialogBankAccount #bankAccountForm #thirdPartyName").val();
	                        dataBankAccount.bnAccount.customer.customerName = $("#dialogBankAccount #bankAccountForm #beneficiaryName").val();
	                        dataBankAccount.bnAccount.currency.currencyCode = $("#dialogBankAccount #bankAccountForm #currencyCode").val();
	                        dataBankAccount.id.bankAccountKey = $("#dialogBankAccount #bankAccountForm #bankAccountKey").val();

	                        //var productCode = $("#productCode").val().replace(/${specialCharQuote}/g, "'").toString();
	                        dataBankAccount.id.productCode = $("#productCode").val();

	                        var constantDefaultRgProductBankAccount = dataBankAccount.bnAccount.bankCode.thirdPartyKey + dataBankAccount.bnAccount.accountNo;
	                        dataBankAccount.defaultRgProductBankAccount = constantDefaultRgProductBankAccount.split(' ').join('_');

	                        table.fnAddData(dataBankAccount);
	                        $('#dialogBankAccount').dialog('close');
	                    }
	                }
	                return false;
	            }
	        }
       }, 500);
    });

    //Button New Data for Bank Account
    
    $('.buttons #newBankDialog').click(function()
	{
    	if ($('#customerNoKey').val()==''){
    		$('#btnBankAccountAddError').html("Please fill customer code");
    		return false;
        } else {
        	$('#btnBankAccountAddError').html("");
	    	selectedRow = null;
	        $("#dialogBankAccount").dialog('open');
	        $('.ui-widget-overlay').css('height',$('body').height());
	        $("#dialogBankAccount input:text").val(""); 
	        $("#dialogBankAccount input:hidden").val("");
	        $("#dialogBankAccount #bankAccountKey").val("");
	        $("#dialogBankAccount #bankAccountForm .errmsg").html("");
	        $("#dialogBankAccount #bankAccountForm").find("span[id*='Error']").html("");
	        return false;
        }
    });
});

function bankAccount(data) {
    var bankAccounts;

    var ba = ${bankAccounts};

    if (ba != '')
    {
    	bankAccounts = ${bankAccounts.raw()};
    }
    else
    {
    	bankAccounts = new Array();
    }

    tableBankAccount = 
        $('#form #listBankAccount #bankAccountTable').dataTable({
            aaData: bankAccounts,
            aoColumns: [ {
                            fnRender: function(obj) {
                                var controls;
                                controls = obj.aData.bnAccount.accountNo;
                                return controls;
                             }
                          }
                          ,
                          {
                             fnRender: function(obj) {
                            	 var controls;
                            	 controls = obj.aData.bnAccount.bankCode.thirdPartyCode;
                                 return controls;
                             }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  controls = obj.aData.bnAccount.customer.customerName;
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;
                                  controls = obj.aData.bnAccount.currency.currencyCode;
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                                  var controls;

                                  var radios = $("[type=radio]", $('#bankAccountTable tbody'));

				            	  //if (radios.html() == null)
				            	  if($('#defaultProductBankAccount').isEmpty())
                                  {
				            		  $('#defaultProductBankAccount').val(obj.aData.defaultRgProductBankAccount);
				            		  controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccount" value="' + obj.aData.defaultRgProductBankAccount + '" checked="checked" #{if readOnly}disabled="disabled"#{/if}/></center>';
				            	  }
				            	  else
				            	  {
				            		  if (obj.aData.defaultRgProductBankAccount.toLowerCase() == $("#defaultProductBankAccount").val().toLowerCase())
				            		  {
					            		   controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccount" value="' + obj.aData.defaultRgProductBankAccount + '" checked="checked" #{if readOnly}disabled="disabled"#{/if}/></center>';
					            	  }
				            		  else
				            		  {
					            	   	   controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccount" value="' + obj.aData.defaultRgProductBankAccount + '" #{if readOnly}disabled="disabled"#{/if}/></center>';
					            	  }
				            	  }

				            	  //controls += " ORI => " + $('#defaultProductBankAccount').val();
				            	  //controls += " TABLE => " + obj.aData.defaultRgProductBankAccount;

                                  controls += "<input type='hidden' value='" + obj.aData.defaultRgProductBankAccount + "' name='rgProductBnAccounts[" + obj.iDataRow + "].defaultRgProductBankAccount'>";
//                                  controls += "<input type='radio' id='radioStatus" + obj.aData.defaultRgProductBankAccount + "' name='bankStatus' value='" + obj.aData.defaultRgProductBankAccount + "' />";
                                  return controls;
                              }
                          }
                          ,
                          {
                              fnRender: function(obj) {
                            	  var controls;
                            	  controls = '<center><input id="deleteButtonBank" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
                                  //controls += '<input type="hidden" name="rgProductBnAccounts[' + obj.iDataRow + '].bnAccount.bankAccountKey" value="' + obj.aData.bnAccount.bankAccountKey + '" />';                            

                            	  //var productCode = obj.aData.id.productCode.replace(/${specialCharQuote}/g, "'").toString();

                            	  controls += '<input type="hidden" name="rgProductBnAccounts[' + obj.iDataRow + '].id.productCode" value="' + obj.aData.id.productCode + '" />';
                                  controls += '<input type="hidden" name="rgProductBnAccounts[' + obj.iDataRow + '].id.bankAccountKey" value="' + obj.aData.id.bankAccountKey + '" />';
                                  return controls;
                              }
                          }
                        ],
                        aaSorting: [[0, 'asc']],
        				bAutoWidth: false,		
        	        	bDestroy: true,
        	        	bFilter: false,
        	        	bInfo: false,
        	        	//bRetrieve:true,
        	        	//bServerSide: true,
        	        	bJQueryUI: true,
        	        	bPaginate: false,
        	        	bSearch: false,
        	        	bLengthChange: false,
        	        	isDisplayLength:10
    });

//    $("#listBankAccount #bankAccountTable").removeAttr('style');
//    $("#listBankAccount #bankAccountTable tbody tr td").die('click');
//    $("#listBankAccount #bankAccountTable tbody tr td").live('click', function(){
//        var rowPos= $(this).parents('tr');
//        var rowPosNumber = tableBankAccount.fnGetPosition(rowPos[0]);
//        var pos = tableBankAccount.fnGetPosition(this);
//        cell = tableBankAccount.fnGetData(this.parentNode);
//
//        if (pos[1] != 5) {
//        	dataBankAccounts = tableBankAccount.fnGetData(this.parentNode);
//
//            $('#dialogBankAccount #bankAccountForm').removeClass('fieldError');
//            $("#dialogBankAccount #bankAccountForm .errmsg").html("");
//            $("#dialogBankAccount #bankAccountForm").find("span[id*='Error']").html("");
//            $("#dialogBankAccount #bankAccountForm #rowPosition").val(rowPosNumber);
//            $("#dialogBankAccount #bankAccountForm #bankAccountKey").val(dataBankAccounts.bankAccountKey);
//            //$("#dialogBankAccount #bankAccountForm #productCode").autoNumericSet(dataBankAccounts.productCode);
//            $("#dialogBankAccount #bankAccountForm #bankAccountNo").val(dataBankAccounts.bnAccount.accountNo);
//            $("#dialogBankAccount #bankAccountForm #thirdPartyKey").val(dataBankAccounts.bnAccount.bankCode.thirdPartyKey);
//            $("#dialogBankAccount #bankAccountForm #thirdPartyCode").val(dataBankAccounts.bnAccount.bankCode.thirdPartyCode);
//            $("#dialogBankAccount #bankAccountForm #thirdPartyName").val(dataBankAccounts.bnAccount.bankCode.thirdPartyName);
//            $("#dialogBankAccount #bankAccountForm #beneficiaryName").val(dataBankAccounts.bnAccount.customer.customerName);
//            $("#dialogBankAccount #bankAccountForm #currencyCode").val(dataBankAccounts.bnAccount.currency.currencyCode);
//            $("#defaultRgProductBankAccount").val(dataBankAccounts.defaultRgProductBankAccount);
//            $("#dialogBankAccount").dialog('open');
//        }
//    });
}

function reordering() {
    var grid = $("#listBankAccount #bankAccountTable tbody");
    var trs = $("tr", grid);

    $.each(trs, function(idx, data){
        var hiddens = $("[type=hidden]", $(this));

        $(hiddens).eq(0).attr("name", "rgProductBnAccounts["+idx+"].bankAccountKey");
        //$(hiddens).eq(1).attr("name", "rgProductBnAccounts["+idx+"].productCode");
    });
}