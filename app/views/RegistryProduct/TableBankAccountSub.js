var rowSub = 0;
var dataSubs = 0; //monitor jumlah row di table
$(function() {
	//$("#radioStatus${prd?.defaultProductBankAccountSub}").attr("checked","checked");
	

	$('#dialogBankAccountSub #bankAccountNoSub').lookup({
		list:'@{Pick.bankAccounts()}?by=customer&domain=CUSTOMER',
		get:{
			url:'@{Pick.bankAccountProductByCustomerKey()}?domain=CUSTOMER',
			success: function(data){
				$("#dialogBankAccountSub #bankAccountFormSub #bankAccountNoSub").removeClass("fieldError");
				$("#dialogBankAccountSub #bankAccountFormSub #bankAccountNoSub").val(data.bankAccountNo);
				$("#dialogBankAccountSub #bankAccountFormSub #bankAccountKeySub").val(data.bankAccountKey);
				$("#dialogBankAccountSub #bankAccountFormSub #thirdPartyKeySub").val(data.thirdPartyKey);
				$("#dialogBankAccountSub #bankAccountFormSub #thirdPartyCodeSub").val(data.thirdPartyCode);
				$("#dialogBankAccountSub #bankAccountFormSub #thirdPartyNameSub").val(data.thirdPartyName);
				$("#dialogBankAccountSub #bankAccountFormSub #beneficiaryNameSub").val(data.customer.customerName);
				$("#dialogBankAccountSub #bankAccountFormSub #currencyCodeSub").val(data.currency.currencyCode);
				$("#dialogBankAccountSub #bankAccountFormSub #branchSub").val(data.branch);
				$("#dialogBankAccountSub #bankAccountFormSub #descriptionBankSub").val(data.description);
			},
			error: function(data){
				$("#dialogBankAccountSub #bankAccountFormSub #bankAccountNoSub").addClass('fieldError');
				$("#dialogBankAccountSub #bankAccountFormSub #bankAccountNoSub").val('');
				$("#dialogBankAccountSub #bankAccountFormSub #bankAccountKeySub").val('');
				$("#dialogBankAccountSub #bankAccountFormSub #thirdPartyKeySub").val('');
				$("#dialogBankAccountSub #bankAccountFormSub #thirdPartyCodeSub").val('');
				$("#dialogBankAccountSub #bankAccountFormSub #thirdPartyNameSub").val('');
				$("#dialogBankAccountSub #bankAccountFormSub #beneficiaryNameSub").val('');
				$("#dialogBankAccountSub #bankAccountFormSub #currencyCodeSub").val('');
				$("#dialogBankAccountSub #bankAccountFormSub #branchSub").val('');
				$("#dialogBankAccountSub #bankAccountFormSub #descriptionBankSub").val('');
			}
		},
		filter: $("#customerNoKey"),
		description: $("#thirdPartyNameSub"),
		help: $("#bankAccountNoSubHelp")
	});

	$("#dialogBankAccountSub #bankAccountNoSub").blur(function(){
		if ($("#dialogBankAccountSub #bankAccountNoSub").val()=="") {
			$("#dialogBankAccountSub #bankAccountNoSub").val("");
			$("#dialogBankAccountSub #bankCodeKeySub").val("");
			$("#dialogBankAccountSub #bankCodeDescSub").val("");
			$("#dialogBankAccountSub #beneficiaryNameSub").val("");
			$("#dialogBankAccountSub #currencyCodSube").val("");
			$("#dialogBankAccountSub #thirdPartyKeySub").val('');
			$("#dialogBankAccountSub #thirdPartyCodeSub").val('');
			$("#dialogBankAccountSub #thirdPartyNameSub").val('');
			$("#dialogBankAccountSub #branchSub").val('');
			$("#dialogBankAccountSub #descriptionBankSub").val('');
		}
	});

	var closeDialog = function() {
        $("#dialog-message").dialog('close');   
    };

	var data = new Object();
    bankAccountSub(data);

	$('#addBankDialogSub').button();
	$('#cancelBankDialogSub').button();

	$("#dialogBankAccountSub").dialog({
		autoOpen:false,
		modal:true,
		heigth:'600px',
		width:'600px',
		resizable:false
	});

    $("#listBankAccountSub #bankAccountTableSub tbody tr #deleteButtonBankSub[disabled!=true]").live("click", function() {
    	//rowSub = 0;
        var row = $(this).parents('tr');
        var rowNumber = tableBankAccountSub.fnGetPosition(row[0]);
		var data = tableBankAccountSub.fnGetData(row[0]);
		var deleteBankAccount = function() {
			if (data.defaultRgProductBankAccount == $('#defaultProductBankAccountSub').val()){
				$('#btnBankAccountAddErrorSub').html("Cannot Delete Default Bank Account");
				$("#dialog-message").dialog("close");
				return false;
			} else {
				$('#btnBankAccountAddErrorSub').html("");
			}
			tableBankAccountSub.fnDeleteRow(rowNumber);
			$("#dialog-message").dialog("close");
		}
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccount, closeDialog);
		return false;
	});

    $("#listBankAccountSub #bankAccountTableSub tbody tr td input[name=defaultRgProductBankAccountSub]").live('change', function(){
    	$('#btnBankAccountAddErrorSub').html('');
    	var radios = $("[type=radio]", $(this).parents('tr td'));
    	$('#defaultProductBankAccountSub').val($(radios).val());
    });

    $('#addBankDialogSub').die("click");
    $('#addBankDialogSub').live("click", function(){
    	setTimeout(function() {
	        var table = $("#listBankAccountSub #bankAccountTableSub").dataTable();
	        var rowPosition = $("#dialogBankAccountSub #bankAccountFormSub #rowPositionSub").val();
	        saveBankAccount();

	        function saveBankAccount(){
	        	if($("#dialogBankAccountSub #bankAccountFormSub #bankAccountNoSub").isEmpty()){
	        		$("#dialogBankAccountSub #bankAccountFormSub").find("span[id*='Error']").html("");
	        		if($("#dialogBankAccountSub #bankAccountFormSub #bankAccountNoSub").val()==""){
	                    $("#bankAccountNoSubError").html('Required').show();
	                }
	        	} else {
	                var dataBankAccounts = table.fnGetData(parseFloat(rowPosition));
	                if(rowPosition != ""){
	                    var found = false;
	                    // CHECKED DUPLICATE ROW
	                    var rows = table.fnGetNodes().length;
	                    for (i = 0; i < rows; i++){
	                        var cells = table.fnGetData(i);
	                        var bankAccountNo = cells.bnAccount.accountNo;
	                        var thirdPartyKey = cells.bnAccount.bankCode.thirdPartyKey;
	                        var thirdPartyCode = cells.bnAccount.bankCode.thirdPartyCode;

	                        if (($("#dialogBankAccountSub #bankAccountFormSub #thirdPartyKeySub").val() == thirdPartyKey) 
 	                        		&& ($("#dialogBankAccountSub #bankAccountFormSub #thirdPartyCodeSub").val() == thirdPartyCode)
 	                        		&& ($("#dialogBankAccountSub #bankAccountFormSub #bankAccountNoSub").val() == bankAccountNo)){
 	                        	$("#dialogBankAccountSub #bankAccountFormSub #errmsgNullSub").html("Bank Account already Exist!");
 	                            found = true;
 	                            break;
 	                        }
	                    }

	                    if ($("#customerNoKey").isEmpty()){
							$("#dialogBankAccountSub #bankAccountFormSub #errmsgNullSub").html("Customer Code cannot be empty").show();
							found = true;
						}

	                    var currencyProduct = $("#currencyKey").val();
						var currencyBankAccount = $("#dialogBankAccountSub #bankAccountFormSub #currencyCodeSub").val();
						if (currencyProduct != currencyBankAccount){
							$("#dialogBankAccountSub #bankAccountFormSub #errmsgNullSub").html("Bank Account Currency must be same with Product Currency").show();
							found = true;
						}

	                    if(!found){
	                    	table.fnUpdate(dataBankAccounts.bankAccountKey = $("#dialogBankAccountSub #bankAccountFormSub #bankAccountKeySub").val(),parseFloat(rowPosition), 0);
              
 	                        var constantDefaultRgProductBankAccount = $("#dialogBankAccountSub #bankAccountFormSub #thirdPartyKeySub").val() + $("#dialogBankAccountSub #bankAccountFormSub #bankAccountNoSub").val();
 							data.defaultRgProductBankAccount = constantDefaultRgProductBankAccount.split(' ').join('_');
 							if ($("#dialogBankAccountSub #bankAccountFormSub #oldDefaultRgProductBankAccountSub").val() == $("#defaultRgProductBankAccount").val()) {
 								$("#defaultRgProductBankAccount").val(data.defaultRgProductBankAccount);
 							}
 	                        
	                        $('#dialogBankAccountSub').dialog('close');
	                    }
	                } else {
	                    var found = false;
	                    // CHECKED DUPLICATE ROW
	                    var rows = table.fnGetNodes().length;
	                    for (i = 0; i < rows; i++){
	                        var cells = table.fnGetData(i);
	                        var bankAccountNo = cells.bnAccount.accountNo;
	                        var thirdPartyKey = cells.bnAccount.bankCode.thirdPartyKey;
	                        var thirdPartyCode = cells.bnAccount.bankCode.thirdPartyCode;

 	                        if (($("#dialogBankAccountSub #bankAccountFormSub #thirdPartyKeySub").val() == thirdPartyKey) 
 	                        		&& ($("#dialogBankAccountSub #bankAccountFormSub #thirdPartyCodeSub").val() == thirdPartyCode)
 	                        		&& ($("#dialogBankAccountSub #bankAccountFormSub #bankAccountNoSub").val() == bankAccountNo)){
 	                        	$("#dialogBankAccountSub #bankAccountFormSub #errmsgNullSub").html("Bank Account already Exist!");
 	                            found = true;
 	                            break;
 	                        }
	                    }

	                    if ($("#customerNoKey").isEmpty()){
							$("#dialogBankAccountSub #bankAccountFormSub #errmsgNullSub").html("Customer Code cannot be empty").show();
							found = true;
						}

	                    var currencyProduct = $("#currencyKey").val();
						var currencyBankAccount = $("#dialogBankAccountSub #bankAccountFormSub #currencyCodeSub").val();
						if (currencyProduct != currencyBankAccount){
							$("#dialogBankAccountSub #bankAccountFormSub #errmsgNullSub").html("Bank Account Currency must be same with Product Currency").show();
							found = true;
						}

	                    if(!found){
	                        var dataBankAccount = new Object();
	                        dataBankAccount.bnAccount = new Object();
	                        dataBankAccount.bnAccount.bankCode = new Object();
	                        dataBankAccount.bnAccount.customer = new Object();
	                        dataBankAccount.bnAccount.currency = new Object();
	                        dataBankAccount.id = new Object();
	                        dataBankAccount.bnAccount.accountNo = $("#dialogBankAccountSub #bankAccountFormSub #bankAccountNoSub").val();
	                        dataBankAccount.bnAccount.bankCode.thirdPartyKey = $("#dialogBankAccountSub #bankAccountFormSub #thirdPartyKeySub").val();
	                        dataBankAccount.bnAccount.bankCode.thirdPartyCode = $("#dialogBankAccountSub #bankAccountFormSub #thirdPartyCodeSub").val();
	                        dataBankAccount.bnAccount.bankCode.thirdPartyName = $("#dialogBankAccountSub #bankAccountFormSub #thirdPartyNameSub").val();
	                        dataBankAccount.bnAccount.customer.customerName = $("#dialogBankAccountSub #bankAccountFormSub #beneficiaryNameSub").val();
	                        dataBankAccount.bnAccount.currency.currencyCode = $("#dialogBankAccountSub #bankAccountFormSub #currencyCodeSub").val();
	                        dataBankAccount.bnAccount.branch = $("#dialogBankAccountSub #bankAccountFormSub #branchSub").val();
	                        dataBankAccount.bnAccount.description = $("#dialogBankAccountSub #bankAccountFormSub #descriptionBankSub").val();
	                        dataBankAccount.id.bankAccountKey = $("#dialogBankAccountSub #bankAccountFormSub #bankAccountKeySub").val();
	                        dataBankAccount.id.productCode = $("#productCode").val();
	                        dataBankAccount.id.domain = "${domainBankSub}";

	                        dataSubs = $("#listBankAccountSub #bankAccountTableSub").dataTable().length;
	                        
	                        var constantDefaultRgProductBankAccount = dataBankAccount.bnAccount.bankCode.thirdPartyKey + dataBankAccount.bnAccount.accountNo;
	                        dataBankAccount.defaultRgProductBankAccount = constantDefaultRgProductBankAccount.split(' ').join('_');
	                        
	                        $('#defaultProductBankAccountSub').val(constantDefaultRgProductBankAccount);

	                        table.fnAddData(dataBankAccount);
	                        $('#dialogBankAccountSub').dialog('close');
	                    }
	                }
	                return false;
	            }
	        }
       }, 500);
    });

    //Button New Data for Bank Account
    $('.buttons #newBankDialogSub').click(function(){
    	if ($('#customerNoKey').val()==''){
    		$('#btnBankAccountAddErrorSub').html("Please fill customer code");
    		return false;
        } else {
        	$('#btnBankAccountAddErrorSub').html("");
	    	selectedRow = null;
	        $("#dialogBankAccountSub").dialog('open');
	        $('.ui-widget-overlay').css('height',$('body').height());
	        $("#dialogBankAccountSub input:text").val(""); 
	        $("#dialogBankAccountSub input:hidden").val("");
	        $("#dialogBankAccountSub #bankAccountKeySub").val("");
	        $("#dialogBankAccountSub #bankAccountFormSub .errmsg").html("");
	        $("#dialogBankAccountSub #bankAccountFormSub").find("span[id*='Error']").html("");
	        return false;
        }
    });
});

function bankAccountSub(data) {
    var bankAccountsSub;

    var ba = ${bankAccountsSub};
    if (ba != ''){
    	bankAccountsSub = ${bankAccountsSub.raw()};
    } else {
    	bankAccountsSub = new Array();
    }

    tableBankAccountSub = 
        $('#form #listBankAccountSub #bankAccountTableSub').dataTable({
            aaData: bankAccountsSub,
            aoColumns: [ {
                            fnRender: function(obj) {
                                var controls;
                                controls = obj.aData.bnAccount.accountNo;
                                return controls;
                             }
                          },{
                             fnRender: function(obj) {
                            	 var controls;
                            	 controls = obj.aData.bnAccount.bankCode.thirdPartyName;
                                 return controls;
                             }
                          },{
                              fnRender: function(obj) {
                                  var controls;
                                  controls = obj.aData.bnAccount.customer.customerName;
                                  return controls;
                              }
                          },{
                              fnRender: function(obj) {
                                  var controls;
				            	  if($('#defaultProductBankAccountSub').isEmpty()){
				            		  $('#defaultProductBankAccountSub').val(obj.aData.defaultRgProductBankAccount);
				            		  controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccountSub" value="' + obj.aData.defaultRgProductBankAccount + '" checked="checked" #{if readOnly}disabled="disabled"#{/if}/></center>';
				            	  } else {
				            		  if ( dataSubs == 1) {
					            		   controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccountSub" value="' + obj.aData.defaultRgProductBankAccount + '" checked="checked" #{if readOnly}disabled="disabled"#{/if}/></center>';
					            		   $('#defaultProductBankAccountSub').val(obj.aData.defaultRgProductBankAccount);
					            	  } else {
					            		  if (obj.aData.defaultRgProductBankAccount.toLowerCase() == $("#defaultProductBankAccountSub").val().toLowerCase()){
						            		   controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccountSub" value="' + obj.aData.defaultRgProductBankAccount + '" checked="checked" #{if readOnly}disabled="disabled"#{/if}/></center>';
						            		   $('#defaultProductBankAccountSub').val(obj.aData.defaultRgProductBankAccount);
						            	  } else {
						            	   	   controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccountSub" value="' + obj.aData.defaultRgProductBankAccount + '" #{if readOnly}disabled="disabled"#{/if}/></center>';
						            	  }
					            	  }
				            	  }
                                  controls += "<input type='hidden' value='" + obj.aData.defaultRgProductBankAccount + "' name='rgProductBnAccountsSub[" + rowSub + "].defaultRgProductBankAccount'>";
                                  return controls;
                              }
                          },{
                              fnRender: function(obj) {
                            	  var controls;
                            	  controls = '<center><input id="deleteButtonBankSub" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
                            	  controls += '<input type="hidden" name="rgProductBnAccountsSub[' + rowSub + '].id.productCode" value="' + obj.aData.id.productCode + '" />';
                                  controls += '<input type="hidden" name="rgProductBnAccountsSub[' + rowSub + '].id.bankAccountKey" value="' + obj.aData.id.bankAccountKey + '" />';
                                  controls += '<input type="hidden" name="rgProductBnAccountsSub[' + rowSub + '].id.domain" value="' + obj.aData.id.domain + '" />';
                                  rowSub++;
                                  return controls;
                              }
                          }
                        ],
                        aaSorting: [[0, 'asc']],
        				bAutoWidth: false,		
        	        	bDestroy: true,
        	        	bFilter: false,
        	        	bInfo: false,
        	        	bJQueryUI: true,
        	        	bPaginate: false,
        	        	bSearch: false,
        	        	bLengthChange: false,
        	        	isDisplayLength:10
    });
};