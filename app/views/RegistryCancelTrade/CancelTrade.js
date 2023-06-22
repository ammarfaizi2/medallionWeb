function CancelTrade(html) {
	if (this instanceof CancelTrade) {

/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var TRADETYPE_SUBSCRIBE = "Subscribe";
		var TRADETYPE_REDEEM = "Redeem";
		var TYPE_SWITCHING = "Switching";		
		//var TRADETYPE_FUND_ACTION = "FundAction";
		var TYPE_DIVIDEND = "Dividend";
		
		var rg = html.inject(this);
		
		var closeDialogMessage = function() {
			$("#dialog-message").dialog("close");
		};
		
/* =========================================================================== 
 * Method
 * ========================================================================= */		
		function isDividend() {
			return (rg.type.val() == TYPE_DIVIDEND);
		}
		
		function isSwiching() {
			return (rg.type.val() == TYPE_SWITCHING);
		}
		
		function changeTransactionLabel() {
			if (isDividend()) {
				$("label", rg.transactionNoP).html("Dividend No");
			} else if (isSwiching()){
				$("label", rg.transactionNoP).html("Switching No");
			}else{
				$("label", rg.transactionNoP).html("Transaction No");
			}			
		};
		
		function isSelected(tradeKey) {
			var tradeKeys = rg.remark.val().split(",");
			var idx = jQuery.inArray(tradeKey, tradeKeys);
			return idx != -1;
		}
		
		function checkbox(row, checked) {
			var td = $("tbody", rg.tblTransaction).children().eq(row).children().eq(0);
			if (checked) {
				$("input", td).attr("checked", "checked");	
			}else{
				$("input", td).removeAttr("checked");	
			}
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */

		html.clazz('calendar').datepicker();
		
		rg.fundCode.popupProduct("tradeDate");
		
		$("option", rg.type).eq(0).remove();

		rg.type.change(function(){
			changeTransactionLabel();
			$('#tblTransaction').dataTable().fnClearTable();
			$('#tblTransaction').css('width', '');
		});

		rg.btnNext.click(function(){
			if ((rg.fundCode.isEmpty()) || (rg.tradeDate.isEmpty())){
				if (rg.fundCode.isEmpty()){
					$('#fundCodeError').html('Required');
				}
				
				if (rg.tradeDate.isEmpty()){
					$('#tradeDateError').html('Required');
				}
				
				return false;
			}
			
			rg.remark.val("");
			rg.selected.val("");
			rg.registryCancelTradeForm.attr('action', 'showTransaction');
			rg.registryCancelTradeForm.submit();			
		});
		
		rg.btnClear.click(function(){
			location.href='@{RegistryCancelTrade.list()}';
			return false;
		});
		
//		rg.btnProcess.click(function(){
//			var remark = "";
//			
//			$("input:checked", html).each(function(){
//				var tradeKey = $(this).val();
//				var ordertype = $(this).attr("ordertype");
//				if (tradeKey != "on") {
//					if (remark == "") {
//						remark = tradeKey;
//					}else {
//						remark += ("," + tradeKey);
//					}
//				}
//			});
//
//			rg.remark.value(remark);
//			if (remark == "") {
//				alert("Please select the checkbox");
//			}else{
//				rg.registryCancelTradeForm.attr('action', 'process');
//				rg.registryCancelTradeForm.submit();
//			}
//		});
		
//		$("tbody tr", rg.tblTransaction).each(function(idx, obj){
//			$(this).click(function(){
		$("tbody tr", rg.tblTransaction).live('click',function(){
				// for display message
				if ($("td ", $(this)).eq(4).html() == 'Open'){
//					alert("Please reject transaction!");
					messageAlertOk("Please reject Transaction", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
					return false;
				};
				if ($("td ", $(this)).eq(4).html() == 'Paid(O)'){
//					alert("Please reject payment!");
					messageAlertOk("Please reject Payment", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
					return false;
				}
				if ($("td ", $(this)).eq(4).html() == 'Paid(A)'){
//					alert("Please cancel payment!");
					messageAlertOk("Please cancel Payment", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
					return false;
				}

				var url = $(this).attr("url");
				
				if('${mode}'=='view'){
					if (rg.type.val()==TRADETYPE_SUBSCRIBE){
						location.href='@{RegistrySubscription.cancel()}?keyId=${keyId}&from=cancelTradeApp';
					}
					if (rg.type.val()==TRADETYPE_REDEEM){
						location.href='@{RegistryRedemption.cancel()}?keyId=${keyId}&from=cancelTradeApp';
					}
					if (rg.type.val()==TYPE_SWITCHING){
						location.href='@{RegistrySwitch.cancel()}?keyId=${keyId}&from=cancelTradeApp';
					}
					if (rg.type.val()==TYPE_DIVIDEND){
						location.href='@{RegistryFundAction.cancel()}?keyId=${keyId}&from=cancelTradeApp';
					}
//					rg.main.attr("action", url);
//					rg.main.submit();
				} else {
					rg.registryCancelTradeForm.attr("action", url);
					rg.registryCancelTradeForm.submit();
				}
			});
//		});

		$("tbody tr", rg.tblTransaction).each(function(idx, obj){
			var tradeKey = $(this).attr("tradeKey");
			if (tradeKey) checkbox(idx, isSelected(tradeKey));
		});
		
		changeTransactionLabel();
	}else{
		return new CancelTrade(html);
	}
}
	