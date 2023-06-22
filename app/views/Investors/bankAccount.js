$(function(){
	
	var data = new Object();
	tabBankAccount(data);
	
	$('#gBank #grid-bank tbody tr #deleteBank').live('click', function() {
		var row = $(this).parents('tr');
		var rowNumber = tableBankAccount.fnGetPosition(row[0]);
		var deleteBnAccount = function(){
			tableBankAccount.fnDeleteRow(rowNumber);
			reorderingAccount();
			$("#dialog-message").dialog("close");
		}
		messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBnAccount, closeDialog);
	});
	
	$('#saveBnAccount').die("click");
	$('#saveBnAccount').live("click", function(){
		
		if($('#bankCodeKey').val()=='' || $('#bAccountNo').val()=='' || 
			$('#accountHolderName').val()==''|| $('#currency').val()=='' ){
			
			if ($('#bankCodeKey').val()==''){
				$('#bankCodeError').html('Required');
			} else {
				$('#bankCodeError').html('');
			}
			
			if ($('#bAccountNo').val()==''){
				$('#bAccountNoError').html('Required');
			} else {
				$('#bAccountNoError').html('');
			}
			
			if ($('#accountHolderName').val()==''){
				$('#accountHolderNameError').html('Required');
			} else {
				$('#accountHolderNameError').html('');
			}
			
			if ($('#currency').val()==''){
				$('#currencyError').html('Required');
			} else {
				$('#currencyError').html('');
			}
			
			return false;
		}
		
		var table = $('#gBank #grid-bank').dataTable();
		var rowPosition = $('#detailBankAccount #bankAccountDetailForm #rowPosition').val();
		var rows = table.fnGetNodes().length;
		var dataBank = table.fnGetData(parseFloat(rowPosition));
		
		if (rowPosition != "") {
			for (var i = 0; i < rows; i++) {
				var cells = table.fnGetData(i);
				if($("#bankAccountDetailForm #bankCodeKey").val() == cells.bankCode.thirdPartyKey 
					&& $("#bankAccountDetailForm #bAccountNo").val() == cells.accountNo
					&& rowPosition != i ){
					$('#bankAccountDetailForm #bankCode').addClass('fieldError');
					$("#bankCodeError").html("Already Exist");
					return false;
				}
			}
			dataBank.bankCode = new Object();
			dataBank.currency = new Object();
			
			table.fnUpdate(dataBank.accountNo = $('#detailBankAccount #bankAccountDetailForm #bAccountNo').val(), parseFloat(rowPosition), 0);
			table.fnUpdate(dataBank.bankCode.thirdPartyName = $('#detailBankAccount #bankAccountDetailForm #bankCodeName').val(), parseFloat(rowPosition), 1);
			table.fnUpdate(dataBank.name = $('#detailBankAccount #bankAccountDetailForm #accountHolderName').val(), parseFloat(rowPosition), 2);
			//table.fnUpdate(dataBank.currency.currencyCode = $('#detailBankAccount #bankAccountDetailForm #currency').val(), parseFloat(rowPosition), 3);
			
			dataBank.bankAccountKey = $('#detailBankAccount #bankAccountDetailForm #bankAccountKey').val();
			dataBank.bankCode.thirdPartyKey = $('#detailBankAccount #bankAccountDetailForm #bankCodeKey').val();
			dataBank.bankCode.thirdPartyCode = $('#detailBankAccount #bankAccountDetailForm #bankCode').val();
			dataBank.bankCode.thirdPartyName = $('#detailBankAccount #bankAccountDetailForm #bankCodeName').val();
			dataBank.accountNo = $('#detailBankAccount #bankAccountDetailForm #bAccountNo').val();
			dataBank.name = $('#detailBankAccount #bankAccountDetailForm #accountHolderName').val();
			dataBank.currency.currencyCode = $('#detailBankAccount #bankAccountDetailForm #currency').val();
			dataBank.currency.description = $('#detailBankAccount #bankAccountDetailForm #currencyDesc').val();
			dataBank.branch = $('#detailBankAccount #bankAccountDetailForm #branch').val();
			dataBank.bicCode = $('#detailBankAccount #bankAccountDetailForm #bicCode').val();
			dataBank.description = $('#detailBankAccount #bankAccountDetailForm #description').val();
			table.fnUpdate(dataBank, parseFloat(rowPosition), 3);
		}else{
			for (var i = 0; i < rows; i++) {
				var cells = table.fnGetData(i);
				if($("#bankAccountDetailForm #bankCodeKey").val() == cells.bankCode.thirdPartyKey 
					&& $("#bankAccountDetailForm #bAccountNo").val() == cells.accountNo ){
					$('#bankAccountDetailForm #bankCode').addClass('fieldError');
					$("#bankCodeError").html("Already Exist");
					return false;
				}
			}
			var dataBank = new Object();
			dataBank.bankCode = new Object();
			dataBank.currency = new Object();
			dataBank.bankAccountKey = $('#detailBankAccount #bankAccountDetailForm #bankAccountKey').val();
			dataBank.bankCode.thirdPartyKey = $('#detailBankAccount #bankAccountDetailForm #bankCodeKey').val();
			dataBank.bankCode.thirdPartyCode = $('#detailBankAccount #bankAccountDetailForm #bankCode').val();
			dataBank.bankCode.thirdPartyName = $('#detailBankAccount #bankAccountDetailForm #bankCodeName').val();
			dataBank.accountNo = $('#detailBankAccount #bankAccountDetailForm #bAccountNo').val();
			dataBank.name = $('#detailBankAccount #bankAccountDetailForm #accountHolderName').val();
			dataBank.currency.currencyCode = $('#detailBankAccount #bankAccountDetailForm #currency').val();
			dataBank.currency.description = $('#detailBankAccount #bankAccountDetailForm #currencyDesc').val();
			dataBank.branch = $('#detailBankAccount #bankAccountDetailForm #branch').val();
			dataBank.bicCode = $('#detailBankAccount #bankAccountDetailForm #bicCode').val();
			dataBank.description = $('#detailBankAccount #bankAccountDetailForm #description').val();
			table.fnAddData(dataBank);
		}
		reorderingAccount();
		$('#detailBankAccount').dialog('close');
		$('#grid-bank_wrapper.dataTables_wrapper').css('width','1728px');
		return false;
	});
});

//function grid table bank account 
function tabBankAccount(data){
	var detailBanks = null;
	detailBanks = ${detailBanks};
	/*var detailBanks;
	
	if ('${detailBanks}' != null )
		detailBanks = ${detailBanks.raw()};

	if ('${detailBanks}' == null )
		detailBanks = new Array();*/
	
	tableBankAccount = $('#gBank #grid-bank').dataTable({
		aaData: detailBanks,
		aoColumns: [ 
					{
						fnRender: function(obj){
							var controls;
							controls = obj.aData.accountNo;
							return controls;
						}
					},
					{
						fnRender: function(obj){
							var controls;
							controls = obj.aData.bankCode.thirdPartyName;
							return controls;
						}
					},
					{
						fnRender: function(obj){
							var controls;
							controls = obj.aData.name;
							return controls;
						}
					},
					{
						fnRender: function(obj){
							
							if(obj.aData.bankAccountKey==null){
								obj.aData.bankAccountKey="";
							}
							
							if(obj.aData.branch==null){
								obj.aData.branch="";
							}
							
							if(obj.aData.bicCode==null){
								obj.aData.bicCode="";
							}
							
							if(obj.aData.description==null){
								obj.aData.description="";
							}
							
							var controls;
							controls = '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].bankAccountKey" value="' + obj.aData.bankAccountKey + '" />';
							controls += '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].bankCode.thirdPartyKey" value="' + obj.aData.bankCode.thirdPartyKey + '" />';
							controls += '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].bankCode.thirdPartyCode" value="' + obj.aData.bankCode.thirdPartyCode + '" />';
							controls += '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].bankCode.thirdPartyName" value="' + obj.aData.bankCode.thirdPartyName + '" />';
							controls += '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].accountNo" value="' + obj.aData.accountNo.replace(/"/g,"&quot;") + '" />';
							controls += '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].name" value="' + obj.aData.name.replace(/"/g,"&quot;")+ '" />';
							controls += '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].currency.currencyCode" value="' + obj.aData.currency.currencyCode + '" />';
							controls += '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].currency.description" value="' + obj.aData.currency.description + '" />';
							controls += '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].branch" value="' + obj.aData.branch.replace(/"/g,"&quot;") + '" />';
							controls += '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].bicCode" value="' + obj.aData.bicCode.replace(/"/g,"&quot;") + '" />';
							controls += '<input type="hidden" name="bankAccounts[' + obj.iDataRow + '].description" value="' + obj.aData.description.replace(/"/g,"&quot;") + '" />';
							controls += '<center><input id="deleteBank" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
							return controls;
						}
					}
		           ],
       aaSorting:[[0, 'asc']],
		bAutoWidth: false,
		//bDestroy: true,
		bFilter: false,
		bInfo: false,
		bJQueryUI:true,
		bPaginate: false,
		bSearch: false,
		bLengthChange:false
	});
	reorderingAccount();
}

$('#gBank #grid-bank ').removeAttr('style');
$('#gBank #grid-bank tbody tr td').die('click');
$('#gBank #grid-bank tbody tr td').live('click', function() {
	
	var rowPos = $(this).parents('tr');
	var rowPosNumber = tableBankAccount.fnGetPosition(rowPos[0]);
	var pos = tableBankAccount.fnGetPosition(this);
	cells = tableBankAccount.fnGetData(this.parentNode);
	
	if (pos[1] != 3) {
		dataBank = tableBankAccount.fnGetData(this.parentNode);
		$("#detailBankAccount #bankAccountDetailForm #bankCode").removeClass('fieldError');
		$("#detailBankAccount #bankAccountDetailForm #bAccountNo").removeClass('fieldError');
		$("#detailBankAccount #bankAccountDetailForm #accountHolderName").removeClass('fieldError');
		$("#detailBankAccount #bankAccountDetailForm #currency").removeClass('fieldError');
		$("#detailBankAccount #bankAccountDetailForm #branch").removeClass('fieldError');
		$("#detailBankAccount #bankAccountDetailForm #bicCode").removeClass('fieldError');
		$("#detailBankAccount #bankAccountDetailForm .errmsg").html("");
		$('#detailBankAccount #bankAccountDetailForm #rowPosition').val(rowPosNumber);
		
		$("#detailBankAccount #bankAccountDetailForm #bankAccountKey").val(dataBank.bankAccountKey);
		
		if(dataBank.bankCode == null) {
		   dataBank.bankCode = new Object();
		}
		$("#detailBankAccount #bankAccountDetailForm #bankCode").val(dataBank.bankCode.thirdPartyCode);
		$("#detailBankAccount #bankAccountDetailForm #bankCodeKey").val(dataBank.bankCode.thirdPartyKey);
		$("#detailBankAccount #bankAccountDetailForm #bankCodeName").val(dataBank.bankCode.thirdPartyName);
		
		$("#detailBankAccount #bankAccountDetailForm #bAccountNo").val(dataBank.accountNo);
		$("#detailBankAccount #bankAccountDetailForm #accountHolderName").val(dataBank.name);
		
		if(dataBank.currency == null) {
		   dataBank.currency = new Object();
		}
		$("#detailBankAccount #bankAccountDetailForm #currency").val(dataBank.currency.currencyCode);
		$("#detailBankAccount #bankAccountDetailForm #currencyDesc").val(dataBank.currency.description);
		
		$("#detailBankAccount #bankAccountDetailForm #branch").val(dataBank.branch);
		$("#detailBankAccount #bankAccountDetailForm #bicCode").val(dataBank.bicCode);
		$("#detailBankAccount #bankAccountDetailForm #description").val(dataBank.description);
		
		$('#detailBankAccount #bankAccountDetailForm .error').html("");
		$("#detailBankAccount").dialog('open');
		$('.ui-widget-overlay').css('height',$('body').height());
	}
});

function reorderingAccount(){
	var tableBankAccount = $('#gBank #grid-bank');
	var trs = $("tr", tableBankAccount);
	$.each(trs, function(idx, data){
		var hiddens = $("[type=hidden]", $(this));
		$(hiddens).eq(0).attr("name", "bankAccounts["+(idx-1)+"].bankAccountKey");
		$(hiddens).eq(1).attr("name", "bankAccounts["+(idx-1)+"].bankCode.thirdPartyKey");
		$(hiddens).eq(2).attr("name", "bankAccounts["+(idx-1)+"].bankCode.thirdPartyCode");
		$(hiddens).eq(3).attr("name", "bankAccounts["+(idx-1)+"].bankCode.thirdPartyName");
		$(hiddens).eq(4).attr("name", "bankAccounts["+(idx-1)+"].accountNo");
		$(hiddens).eq(5).attr("name", "bankAccounts["+(idx-1)+"].name");
		$(hiddens).eq(6).attr("name", "bankAccounts["+(idx-1)+"].currency.currencyCode");
		$(hiddens).eq(7).attr("name", "bankAccounts["+(idx-1)+"].currency.description");
		$(hiddens).eq(8).attr("name", "bankAccounts["+(idx-1)+"].branch");
		$(hiddens).eq(9).attr("name", "bankAccounts["+(idx-1)+"].bicCode");
		$(hiddens).eq(10).attr("name", "bankAccounts["+(idx-1)+"].description");
	});
}