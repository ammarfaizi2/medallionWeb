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
//		var TRADETYPE_DIV_BY_REDEMPTION = "Dividend By Redemption";
//		var TRADETYPE_DIV_BY_CASH = "Dividend By Cash";
//		var TRADETYPE_DIV_BY_INVEST_OPT = "Dividend By Investor Option";
		
		var pay = html.inject(this);
		 
		var closeDialogMessage = function() {
			$("#dialog-message").dialog("close");
		};
		
/* =========================================================================== 
 * Method
 * ========================================================================= */
		function calculateTotalPayment() {
			var totalPayment = 0;
			html.name("option").each(function(idx, e){
				if (($(e).checked() != "false")) {
					var payAmount = $(e).parent().parent().children().eq(5);
					totalPayment += payAmount.html().toNumber(",");
				}
				if (pay.type.val()!=TRADETYPE_REDEMPTION){
					if (($(e).checked() != false)) {
						var payAmount = $(e).parent().parent().children().eq(5);
						totalPayment += payAmount.html().toNumber(",");
					}
				}
			});
			pay.totPayment.valueRnd(totalPayment, true, roundDigit, roundType);
		}
		
		function validatePaymentDate(){
			var actPayDate = new Date(pay.paymentDate.val());
			result = true;
			html.name('option').each(function(idx, e){
				if (($(e).checked()!='false')){
					var actPaymentDate = $(e).parent().parent().children().eq(4);
					var payDateGrid = new Date(actPaymentDate.html());
					if (actPayDate.getTime() < payDateGrid.getTime()){
						result = false;
					} 
				}
			});
			
			return result;
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
			
//			if (pay.filterBy.val() == NO_DIVIDEND) {
//				if (isClear) pay.filterByNo.val("");
//				pay.filterByNo.enabled();
//			}
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		html.clazz('calendar').datepicker();
		
		$("option", pay.type).eq(0).remove();
		$("option", pay.filterBy).eq(0).remove();
		
		if (!pay.fundCode.val().isEmpty()) {
			var rgProduct = html.getRgProduct(pay.fundCode.val());
			roundType = rgProduct.amountRoundType;
			roundDigit = rgProduct.amountRoundValue;
		}
		
		if (pay.dispatch.val() == 'view') {
			$('#tblTransaction').removeAttr('style');
			$('#tblTransaction tbody tr td').die('click');
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
				$('#tblTransaction').dataTable().fnClearTable();
				$('#tblTransaction').css('width', '');
				pay.totPayment.val('');
				pay.totPaymentStripped.val('');
			}
		});
		
		pay.fundCode.change(function(){
			if (pay.fundCode.val() ==''){
				$('#tblTransaction').dataTable().fnClearTable();
				$('#tblTransaction').css('width', '');
				pay.totPayment.val('');
				pay.totPaymentStripped.val('');
			}
		});
		
		pay.selectAll.change(function(){
			var checkedval = pay.selectAll.attr("checked");
			html.name("option").attr("checked", checkedval);
			calculateTotalPayment();
		});
		if (pay.dispatch.val() == 'entry') {
			if (pay.type.val() != TRADETYPE_REDEMPTION){
				calculateTotalPayment();
			}
		}
		
		html.name("option").change(function(){
			pay.selectAll.attr("checked", false);
			calculateTotalPayment();
		});
		
		pay.btnNext.click(function(){
			if (!pay.fundCode.isEmpty() && !pay.appDate.isEmpty() && !pay.type.isEmpty()) {
				pay.totPayment.valueRnd(0, true, roundDigit, roundType);
				if (pay.type.val() == TRADETYPE_REDEMPTION) {
					pay.registryPaymentForm.attr('action', 'showRedeem');
					pay.registryPaymentForm.submit();
				} else {
					pay.registryPaymentForm.attr('action', 'showDividen');
					pay.registryPaymentForm.submit();
				}				
			}else{
				if (pay.fundCode.isEmpty()) {
					$('#fundCodeError').html('Required');
				} else {
					$('#fundCodeError').html('');
				}
				
				if (pay.filterBy.val()!= NO_FILTER){
					$('#filterByNoError').html("Required");
				} else {
					$('#filterByNoError').html("");
				}
			}
		});
		
		pay.btnClear.click(function(){
			location.href='@{RegistryPayment.list()}';
			return false;
		});
		
		pay.btnProcess.click(function(){
			var selected = "";
			var selectedNominal = "";
			var result = validatePaymentDate();
			if (result == false){
				$('#paymentDate').addClass('fieldError');
				$('#paymentDateError').html('Cannot before than Max Payment Date Selected on Grid');
				return false;
			}
			$("input:checked", html).each(function(){
				var tradeKey = $(this).val();
				var nominal = $(this).attr("nominal");
				if (tradeKey != "on") {
					if (selected == "") {
						selected = tradeKey;
						selectedNominal = nominal;
					}else {
						selected += ("," + tradeKey);
						selectedNominal  += ("," + nominal);
					}
				}
			});
			pay.selected.value(selected);
			pay.selectedNominal.value(selectedNominal);
			if ((selected == "") || (pay.paymentDate.val() == "")){
				if (selected == "")
//					alert("Please select the checkbox");
					messageAlertOk("Please select the checkbox", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
				if (pay.paymentDate.val() == ""){
					$('#paymentDateError').html('Required');
				}
			}else{
				pay.registryPaymentForm.attr('action', 'process');
				pay.registryPaymentForm.submit();
			}
		});
		
		if (pay.dispatch.val() == "entry" && pay.paymentKey.val() != '0') {
			//alert("Your Payment No is #"+pay.paymentKey.val());
			messageAlertOk("Your Payment No is #"+pay.paymentKey.val(), "ui-icon ui-icon-circle-check", "Notification", closeDialogMessage);
			pay.paymentKey.value("0");
		}		
		
		$("tr", $('#tblDividen').tbody()).click(function(){
			pay.fundActionKey.value($(this).children().eq(0).html());
			var totalPayment = $(this).children().eq(3).html().toNumber(",");
			
			pay.totPayment.valueRnd(totalPayment, true, roundDigit, roundType);
			
			
			if (!pay.fundCode.isEmpty() && !pay.appDate.isEmpty() && !pay.type.isEmpty()) {
				pay.registryPaymentForm.attr('action', 'showRedeem');
				pay.registryPaymentForm.submit();
			}else{
//				alert("Please fill Fund Code, Payment Date and Type");
				messageAlertOk("Please fill Fund Code, Payment Date and Type", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
			}
		});
		
		pay.type.change(function(){
			fillFilterByOption('');
			fillFilterByNo(true);
			$('#tblTransaction').dataTable().fnClearTable();
			$('#tblTransaction').css('width', '');
			pay.totPayment.val('');
			$('h_totPayment').val('');
			
		});
		
		pay.filterBy.change(function() {
			fillFilterByNo(true);
			$('#tblTransaction').dataTable().fnClearTable();
			$('#tblTransaction').css('width', '');
			pay.totPayment.val('');
			$('h_totPayment').val('');
		});
		
		if (pay.dispatch.val() == 'entry') {
			fillFilterByNo(false);
			fillFilterByOption(pay.filterByHidden.val());
		}
		
		if (pay.type.val() != TRADETYPE_REDEMPTION){
			pay.selectAll.attr('disabled', true);
			pay.selectAll.attr('checked', true);
			$("input[name=option]", html).attr('disabled', true);
			$("input[name=option]", html).attr('checked', true);
		}
		
		/*
		pay.paymentDate.change(function(){
			if (!$('#paymentDate').hasClass('fieldError')){
				var appDate = new Date(pay.appDate.val()).getTime();
				var paymentDate = new Date(pay.paymentDate.val()).getTime();
				
				if  (paymentDate > appDate) {
					$('#paymentDate').addClass('fieldError');
					$('#paymentDateError').html('Cannot later than Application Date');
				} else {
					$('#paymentDate').removeClass('fieldError');
					$('#paymentDateError').html('');
				}
			}
		});
		*/

		$("td[name=payAmtCell]", html).each(function(){
			var amount = $(this).html();
			pay.formatPayAmount.valueRnd(amount.toNumber(","), true, roundDigit, roundType);
			$(this).html(pay.formatPayAmount.val());
		});
	}else{
		return new Payment(html);
	}
}
	