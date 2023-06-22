var rowRed = 0;
var dataRed = 0; //monitor jumlah row di table
$(function() {
	$("#radioStatus${prd?.defaultProductBankAccountRed}").attr("checked","checked");

	$('#dialogBankAccountRed #bankAccountNoRed').lookup({
		list:'@{Pick.bankAccounts()}?by=customer&domain=CUSTOMER',
		get:{
			url:'@{Pick.bankAccountProductByCustomerKey()}?domain=CUSTOMER',
			success: function(data){
				$("#dialogBankAccountRed #bankAccountFormRed #bankAccountNoRed").removeClass("fieldError");
				$("#dialogBankAccountRed #bankAccountFormRed #bankAccountNoRed").val(data.bankAccountNo);
				$("#dialogBankAccountRed #bankAccountFormRed #bankAccountKeyRed").val(data.bankAccountKey);
				$("#dialogBankAccountRed #bankAccountFormRed #thirdPartyKeyRed").val(data.thirdPartyKey);
				$("#dialogBankAccountRed #bankAccountFormRed #thirdPartyCodeRed").val(data.thirdPartyCode);
				$("#dialogBankAccountRed #bankAccountFormRed #thirdPartyNameRed").val(data.thirdPartyName);
				$("#dialogBankAccountRed #bankAccountFormRed #beneficiaryNameRed").val(data.name);
				$("#dialogBankAccountRed #bankAccountFormRed #currencyCodeRed").val(data.currency.currencyCode);
				$("#dialogBankAccountRed #bankAccountFormRed #branchRed").val(data.branch);
				$("#dialogBankAccountRed #bankAccountFormRed #descriptionBankRed").val(data.description);
			},
			error: function(data){
				$("#dialogBankAccountRed #bankAccountFormRed #bankAccountNoRed").addClass('fieldError');
				$("#dialogBankAccountRed #bankAccountFormRed #bankAccountNoRed").val('');
				$("#dialogBankAccountRed #bankAccountFormRed #bankAccountKeyRed").val('');
				$("#dialogBankAccountRed #bankAccountFormRed #thirdPartyKeyRed").val('');
				$("#dialogBankAccountRed #bankAccountFormRed #thirdPartyCodeRed").val('');
				$("#dialogBankAccountRed #bankAccountFormRed #thirdPartyNameRed").val('');
				$("#dialogBankAccountRed #bankAccountFormRed #beneficiaryNameRed").val('');
				$("#dialogBankAccountRed #bankAccountFormRed #currencyCodeRed").val('');
				$("#dialogBankAccountRed #bankAccountFormRed #branchRed").val('');
				$("#dialogBankAccountRed #bankAccountFormRed #descriptionBankRed").val('');
			}
		},
		filter: $("#customerNoKey"),
		description: $("#thirdPartyNameRed"),
		help: $("#bankAccountNoRedHelp")
	});

	$("#dialogBankAccountRed #bankAccountNoRed").blur(function(){
		if ($("#dialogBankAccountRed #bankAccountNoRed").val()=="") {
			$("#dialogBankAccountRed #bankAccountNoRed").val("");
			$("#dialogBankAccountRed #bankCodeKeyRed").val("");
			$("#dialogBankAccountRed #bankCodeDescRed").val("");
			$("#dialogBankAccountRed #beneficiaryNameRed").val("");
			$("#dialogBankAccountRed #currencyCodeRed").val("");
			$("#dialogBankAccountRed #thirdPartyKeyRed").val('');
			$("#dialogBankAccountRed #thirdPartyCodeRed").val('');
			$("#dialogBankAccountRed #thirdPartyName").val('');
			$("#dialogBankAccountRed #branchRed").val('');
			$("#dialogBankAccountRed #descriptionBankRed").val('');
		}
	});

	var closeDialog = function() {
        $("#dialog-message").dialog('close');   
    };

	var data = new Object();
    bankAccountRed(data);

	$('#addBankDialogRed').button();
	$('#cancelBankDialogRed').button();

	$("#dialogBankAccountRed").dialog({
		autoOpen:false,
		modal:true,
		heigth:'600px',
		width:'600px',
		resizable:false
	});

    $("#listBankAccountRed #bankAccountTableRed tbody tr #deleteButtonBankRed[disabled!=true]").live("click", function() {
    	//rowRed = 0;
        var row = $(this).parents('tr');
        var rowNumber = tableBankAccountRed.fnGetPosition(row[0]);
		var data = tableBankAccountRed.fnGetData(row[0]);
		var deleteBankAccount = function() {
			if (data.defaultRgProductBankAccount == $('#defaultProductBankAccountRed').val()){
				$('#btnBankAccountAddErrorRed').html("Cannot Delete Default Bank Account");
				$("#dialog-message").dialog("close");
				return false;
			} else {
				$('#btnBankAccountAddErrorRed').html("");
			}
			tableBankAccountRed.fnDeleteRow(rowNumber);
			
			var foundDefaultbank1 = false;
			var table = $("#listBankAccountRed #bankAccountTableRed").dataTable();
		    var rows = table.fnGetNodes().length;
		    
		    for (i = 0; i < rows; i++)
		    {
		    	var cells = table.fnGetData(i);
		    	var thirdPartyCode = cells.bnAccount.bankCode.thirdPartyCode;
		    	if('${defaultbankcode}' == thirdPartyCode){
		    		foundDefaultbank1 =true;
		        }
		    }
		    
		    if(!foundDefaultbank1){
		    	$("#swiIntfAccessAccount").val("");
		    	$("#swiIntfAccessAccount").disabled();
		    }else{
	    		$("#swiIntfAccessAccount").enabled();        	
	    		$("#swiIntfAccessAccount").val(oldInterfaceCharge);
		    }
		    
			
			$("#dialog-message").dialog("close");
		}
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankAccount, closeDialog);
		return false;
	});

    $("#listBankAccountRed #bankAccountTableRed tbody tr td input[name=defaultRgProductBankAccountRed]").live('change', function(){
    	$('#btnBankAccountAddErrorRed').html('');
    	var radios = $("[type=radio]", $(this).parents('tr td'));
    	$('#defaultProductBankAccountRed').val($(radios).val());
    });

    var oldInterfaceCharge = "${prd?.swiIntfAccessAccount?.lookupId}";
    
    $('#addBankDialogRed').die("click");
    $('#addBankDialogRed').live("click", function(){
    	setTimeout(function() {
	        var table = $("#listBankAccountRed #bankAccountTableRed").dataTable();
	        var rowPosition = $("#dialogBankAccountRed #bankAccountFormRed #rowPositionRed").val();
	        saveBankAccount();

	        function saveBankAccount(){

                if($("#dialogBankAccountRed #bankAccountFormRed #bankAccountNoRed").isEmpty()){
	        		$("#dialogBankAccountRed #bankAccountFormRed").find("span[id*='Error']").html("");
	        		if($("#dialogBankAccountRed #bankAccountFormRed #bankAccountNoRed").val()==""){
	                    $("#bankAccountNoRedError").html('Required').show();
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

	                        if (($("#dialogBankAccountRed #bankAccountFormRed #thirdPartyKeyRed").val() == thirdPartyKey) 
 	                        		&& ($("#dialogBankAccountRed #bankAccountFormRed #thirdPartyCodeRed").val() == thirdPartyCode)
 	                        		&& ($("#dialogBankAccountRed #bankAccountFormRed #bankAccountNoRed").val() == bankAccountNo)){
 	                        	$("#dialogBankAccountRed #bankAccountFormRed #errmsgNullRed").html("Bank Account already Exist!");
 	                            found = true;
 	                            break;
 	                        }
	                    }

	                    if ($("#customerNoKey").isEmpty()){
							$("#dialogBankAccountRed #bankAccountFormRed #errmsgNullRed").html("Customer Code cannot be empty").show();
							found = true;
						}

	                    var currencyProduct = $("#currencyKey").val();
						var currencyBankAccount = $("#dialogBankAccountRed #bankAccountFormRed #currencyCodeRed").val();
						if (currencyProduct != currencyBankAccount){
							$("#dialogBankAccountRed #bankAccountFormRed #errmsgNullRed").html("Bank Account Currency must be same with Product Currency").show();
							found = true;
						}

	                    if(!found){
	                    	table.fnUpdate(dataBankAccounts.bankAccountKey = $("#dialogBankAccountRed #bankAccountFormRed #bankAccountKeyRed").val(),parseFloat(rowPosition), 0);
              
 	                        var constantDefaultRgProductBankAccount = $("#dialogBankAccountRed #bankAccountFormRed #thirdPartyKeyRed").val() + $("#dialogBankAccountRed #bankAccountFormRed #bankAccountNoRed").val();
 							data.defaultRgProductBankAccount = constantDefaultRgProductBankAccount.split(' ').join('_');
 							if ($("#dialogBankAccountRed #bankAccountFormRed #oldDefaultRgProductBankAccountRed").val() == $("#defaultRgProductBankAccount").val()) {
 								$("#defaultRgProductBankAccount").val(data.defaultRgProductBankAccount);
 							}
 	                        
	                        $('#dialogBankAccountRed').dialog('close');
	                    }
	                } else {
	                    var found = false;
	                    // CHECKED DUPLICATE ROW
	                    var foundDefaultbank = 0;
	                    var rows = table.fnGetNodes().length;
	                    for (i = 0; i < rows; i++){
	                        var cells = table.fnGetData(i);
	                        var bankAccountNo = cells.bnAccount.accountNo;
	                        var thirdPartyKey = cells.bnAccount.bankCode.thirdPartyKey;
	                        var thirdPartyCode = cells.bnAccount.bankCode.thirdPartyCode;
	                        
	                        if('${defaultbankcode}' == thirdPartyCode){
	                        	foundDefaultbank =1;
	                        }


 	                        if (($("#dialogBankAccountRed #bankAccountFormRed #thirdPartyKeyRed").val() == thirdPartyKey) 
 	                        		&& ($("#dialogBankAccountRed #bankAccountFormRed #thirdPartyCodeRed").val() == thirdPartyCode)
 	                        		&& ($("#dialogBankAccountRed #bankAccountFormRed #bankAccountNoRed").val() == bankAccountNo)){
 	                        	$("#dialogBankAccountRed #bankAccountFormRed #errmsgNull").html("Bank Account already Exist!");
 	                            found = true;
 	                            break;
 	                        }
	                    }

	                    if ($("#customerNoKey").isEmpty()){
							$("#dialogBankAccountRed #bankAccountFormRed #errmsgNullRed").html("Customer Code cannot be empty").show();
							found = true;
						}

	                    var currencyProduct = $("#currencyKey").val();
						var currencyBankAccount = $("#dialogBankAccountRed #bankAccountFormRed #currencyCodeRed").val();
						if (currencyProduct != currencyBankAccount){
							$("#dialogBankAccountRed #bankAccountFormRed #errmsgNullRed").html("Bank Account Currency must be same with Product Currency").show();
							found = true;
						}

	                    if(!found){
	                        var dataBankAccount = new Object();
	                        dataBankAccount.bnAccount = new Object();
	                        dataBankAccount.bnAccount.bankCode = new Object();
	                        dataBankAccount.bnAccount.customer = new Object();
	                        dataBankAccount.bnAccount.currency = new Object();
	                        dataBankAccount.id = new Object();
	                        dataBankAccount.bnAccount.accountNo = $("#dialogBankAccountRed #bankAccountFormRed #bankAccountNoRed").val();
	                        dataBankAccount.bnAccount.bankCode.thirdPartyKey = $("#dialogBankAccountRed #bankAccountFormRed #thirdPartyKeyRed").val();
	                        dataBankAccount.bnAccount.bankCode.thirdPartyCode = $("#dialogBankAccountRed #bankAccountFormRed #thirdPartyCodeRed").val();
	                        dataBankAccount.bnAccount.bankCode.thirdPartyName = $("#dialogBankAccountRed #bankAccountFormRed #thirdPartyNameRed").val();
	                        dataBankAccount.bnAccount.customer.customerName = $("#dialogBankAccountRed #bankAccountFormRed #beneficiaryNameRed").val();
	                        dataBankAccount.bnAccount.currency.currencyCode = $("#dialogBankAccountRed #bankAccountFormRed #currencyCodeRed").val();
	                        dataBankAccount.bnAccount.branch = $("#dialogBankAccountRed #bankAccountFormRed #branchRed").val();
	                        dataBankAccount.bnAccount.description = $("#dialogBankAccountRed #bankAccountFormRed #descriptionBankRed").val();
	                        dataBankAccount.id.bankAccountKey = $("#dialogBankAccountRed #bankAccountFormRed #bankAccountKeyRed").val();
	                        dataBankAccount.id.productCode = $("#productCode").val();
	                        dataBankAccount.id.domain = "${domainBankRed}";
	                        
	                        if('${defaultbankcode}' == dataBankAccount.bnAccount.bankCode.thirdPartyCode){
	                        	foundDefaultbank =1;
	                        }
	                        
	                        dataRed = $("#listBankAccountRed #bankAccountTableRed").dataTable().length;

	                        var constantDefaultRgProductBankAccount = dataBankAccount.bnAccount.bankCode.thirdPartyKey + dataBankAccount.bnAccount.accountNo;
	                        dataBankAccount.defaultRgProductBankAccount = constantDefaultRgProductBankAccount.split(' ').join('_');
	                        
	                        $('#defaultProductBankAccountRed').val(constantDefaultRgProductBankAccount);

	                        table.fnAddData(dataBankAccount);
	                        
	                        if(!foundDefaultbank){
	                        	$("#swiIntfAccessAccount").val("");
	                        	$("#swiIntfAccessAccount").disabled();
	                        }else{
	                        	$("#swiIntfAccessAccount").enabled();
	                        	if(oldInterfaceCharge == ""){
	                        		$("#swiIntfAccessAccount").val("${fullAccess}");
	                        	}
	                        }
	                        $('#dialogBankAccountRed').dialog('close');
	                    }
	                }
	                return false;
	            }
	        }
       }, 500);
    });
    
    
    var foundDefaultbank1 = false;
    var table = $("#listBankAccountRed #bankAccountTableRed").dataTable();
    var rows = table.fnGetNodes().length;
    
    for (i = 0; i < rows; i++)
    {
    	var cells = table.fnGetData(i);
    	var thirdPartyCode = cells.bnAccount.bankCode.thirdPartyCode;
    	if('${defaultbankcode}' == thirdPartyCode){
    		foundDefaultbank1 =true;
        }
    }
    if(!foundDefaultbank1){
    	$("#swiIntfAccessAccount").val("");
    	$("#swiIntfAccessAccount").disabled();
    }else{
    	if (('${mode}' == 'entry')&&('${confirming}'!='true')){
    		$("#swiIntfAccessAccount").enabled();        	
    		$("#swiIntfAccessAccount").val(oldInterfaceCharge);
    	}
    	if (('${mode}' == 'edit')&&('${confirming}'!='true')){
    		if(oldInterfaceCharge == "") oldInterfaceCharge ="${fullAccess}";
    		$("#swiIntfAccessAccount").enabled();        	
    		$("#swiIntfAccessAccount").val(oldInterfaceCharge);
    	}
    }

    //Button New Data for Bank Account
    $('.buttons #newBankDialogRed').click(function(){
    	if ($('#customerNoKey').val()==''){
    		$('#btnBankAccountAddErrorRed').html("Please fill customer code");
    		return false;
        } else {
        	$('#btnBankAccountAddErrorRed').html("");
	    	selectedRow = null;
	        $("#dialogBankAccountRed").dialog('open');
	        $('.ui-widget-overlay').css('height',$('body').height());
	        $("#dialogBankAccountRed input:text").val(""); 
	        $("#dialogBankAccountRed input:hidden").val("");
	        $("#dialogBankAccountRed #bankAccountKeyRed").val("");
	        $("#dialogBankAccountRed #bankAccountFormRed .errmsg").html("");
	        $("#dialogBankAccountRed #bankAccountFormRed").find("span[id*='Error']").html("");
	        return false;
        }
    });
});

function bankAccountRed(data) {
    var bankAccountsRed;

    var ba = ${bankAccountsRed};
    if (ba != ''){
    	bankAccountsRed = ${bankAccountsRed.raw()};
    } else {
    	bankAccountsRed = new Array();
    }

    tableBankAccountRed = 
        $('#form #listBankAccountRed #bankAccountTableRed').dataTable({
            aaData: bankAccountsRed,
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
				            	  if($('#defaultProductBankAccountRed').isEmpty()){
				            		  $('#defaultProductBankAccountRed').val(obj.aData.defaultRgProductBankAccount);
				            		  controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccountRed" value="' + obj.aData.defaultRgProductBankAccount + '" checked="checked" #{if readOnly}disabled="disabled"#{/if}/></center>';
				            	  } else {
				            		  
				            		  if ( dataRed == 1) {
				            			  controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccountRed" value="' + obj.aData.defaultRgProductBankAccount + '" checked="checked" #{if readOnly}disabled="disabled"#{/if}/></center>';
					            		  $('#defaultProductBankAccountRed').val(obj.aData.defaultRgProductBankAccount);
				            		  }else {
				            			  if (obj.aData.defaultRgProductBankAccount.toLowerCase() == $("#defaultProductBankAccountRed").val().toLowerCase()){
						            		   controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccountRed" value="' + obj.aData.defaultRgProductBankAccount + '" checked="checked" #{if readOnly}disabled="disabled"#{/if}/></center>';
						            		   $('#defaultProductBankAccountRed').val(obj.aData.defaultRgProductBankAccount);
						            	  } else {
						            	   	   controls = '<center><input type="radio" id="radio' + obj.aData.defaultRgProductBankAccount + '" name="defaultRgProductBankAccountRed" value="' + obj.aData.defaultRgProductBankAccount + '" #{if readOnly}disabled="disabled"#{/if}/></center>';
						            	  }
				            		  }
				            		  
				            	  }
                                  controls += "<input type='hidden' value='" + obj.aData.defaultRgProductBankAccount + "' name='rgProductBnAccountsRed[" + rowRed + "].defaultRgProductBankAccount'>";
                                  return controls;
                              }
                          },{
                              fnRender: function(obj) {
                            	  var controls;
                            	  controls = '<center><input id="deleteButtonBankRed" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
                            	  controls += '<input type="hidden" name="rgProductBnAccountsRed[' + rowRed + '].id.productCode" value="' + obj.aData.id.productCode + '" />';
                                  controls += '<input type="hidden" name="rgProductBnAccountsRed[' + rowRed + '].id.bankAccountKey" value="' + obj.aData.id.bankAccountKey + '" />';
                                  controls += '<input type="hidden" name="rgProductBnAccountsRed[' + rowRed + '].id.domain" value="' + obj.aData.id.domain + '" />';
                                  rowRed++;
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