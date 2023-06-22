function CancelPayment(html) {
	if (this instanceof CancelPayment) {
/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var roundType;
		var roundDigit;
		
		var NO_FILTER = "NO_FILTER";
		var NO_TRX = "NO_TRX";
		var NO_PAYMENT = "NO_PAYMENT";
		var NO_BATCH = "NO_BATCH";
		var NO_DIVIDEND = "NO_DIVIDEND";
		
		var TRADETYPE_REDEMPTION = "Redemption";
		var flek = '${flek}';
//		var TRADETYPE_REDEEM_FUND_ACTION = "Redeem FundAction";
//		var TRADETYPE_CASH_FUND_ACTION = "Cash FundAction";
		
		var pay = html.inject(this);
		
		var closeDialogMessage = function() {
			$("#dialog-message").dialog("close");
		};
		
/* ===========================================================================
* Declare popup tabel /detail
* ===========================================================================*/
		pay.dlgConfirm.dialog({
			autoOpen:false,
			modal:true,
			heigth:'600px',
			width:'465px',
			resizable:false
		});		
		
/* =========================================================================== 
 * Method
 * ========================================================================= */		
		
		function calculateTotalPayment() {
			var totalPayment = 0;
			html.name("option").each(function(idx, e){
				if (($(e).checked() != "false")) {
					var payAmount = $(e).parent().parent().children().eq(6);
					totalPayment += payAmount.html().toNumber(",");
				}
				if (pay.type.val()!=TRADETYPE_REDEMPTION){
					if (($(e).checked() != false)) {
						var payAmount = $(e).parent().parent().children().eq(6);
						totalPayment += payAmount.html().toNumber(",");
					}
				}
			});
			if (pay.type.val() == TRADETYPE_REDEMPTION) {
				pay.totPaidAmount.valueRnd(totalPayment, true, roundDigit, roundType);
			}
		}
		
		function fillCancelByOption(cancelByHidden) {
			if (pay.type.val() == TRADETYPE_REDEMPTION) {
				if ((cancelByHidden == NO_FILTER)||(cancelByHidden == '')){
					$('#cancelBy option').remove();
					var options = $('#cancelBy').attr('options');
						options[options.length] = new Option('All', NO_FILTER);
						options[options.length] = new Option('Transaction No', NO_TRX);
						options[options.length] = new Option('Payment No', NO_PAYMENT);
				} else {
					$("option", pay.cancelBy).eq(3).remove();
				}
			} else {
				if (cancelByHidden == ''){
					$('#cancelBy option').remove();
					var options = $('#cancelBy').attr('options');
					options[options.length] = new Option('Payment No', NO_PAYMENT);
					options[options.length] = new Option('Dividend No', NO_DIVIDEND);
				} else {
					$("option", pay.cancelBy).eq(0).remove();
					$("option", pay.cancelBy).eq(0).remove();
				}
			}	
	
		}
		
		function fillCancelByNo(isClear) {
			if (pay.cancelBy.val() == NO_FILTER) {
				if (isClear) pay.cancelByNo.val("");
				pay.cancelByNo.disabled();
				$('p[id=pCancelBy] label span').html("");
			} else {
				if (isClear) pay.cancelByNo.val("");
				pay.cancelByNo.enabled();
				$('p[id=pCancelBy] label span').html(" *");
			}
			
		}
		
		function checkbox(row, checked) {
			
			var td = $("tbody", pay.tblTransaction).children().eq(row).children().eq(0);
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

		$("option", pay.type).eq(0).remove();
		$("option", pay.cancelBy).eq(0).remove();
		
		pay.fundCode.popupProduct("paymentDate", function(data){
			if (data) {
				var rgProduct = html.getRgProduct(pay.fundCode.val());
				roundType = rgProduct.amountRoundType;
				roundDigit = rgProduct.amountRoundValue;
				$("#fundCodeError").html("");
				$("#fundCode").removeClass('fieldError');				
			}
		});
		
		if (!pay.fundCode.val().isEmpty()) {
			var rgProduct = html.getRgProduct(pay.fundCode.val());
			roundType = rgProduct.amountRoundType;
			roundDigit = rgProduct.amountRoundValue;
		}
		
		pay.cancelByNo.change(function(){
			$("#cancelByNoError").html("");
			$("#cancelByNo").removeClass('fieldError');
		});
		
		pay.type.change(function(){
			fillCancelByOption('');
			fillCancelByNo(true);
			$('#tblTransaction').dataTable().fnClearTable();
			$('#tblTransaction').css('width', '');
			pay.totPaidAmount.val('');
			$('h_totPaidAmount').val('');
		});
		
		pay.selectAll.change(function(){
			var checkedval = pay.selectAll.attr("checked");
			html.name("option").attr("checked", checkedval);
			calculateTotalPayment();
		});
		
		if (pay.type.val() != TRADETYPE_REDEMPTION){
			calculateTotalPayment();
		}
		
		html.name("option").change(function(){
			pay.selectAll.attr("checked", false);
			calculateTotalPayment();
		});
		
		pay.cancelBy.change(function(){
			fillCancelByNo(true);
		});
		
		pay.paymentDate.change(function(){
			var payDate = $("#paymentDate").val();
			if(payDate == ""){
				$("#paymentDateError").html(" Required");
				$("#paymentDate").addClass('fieldError');
			}else{
				$("#paymentDateError").html("");
				$("#paymentDate").removeClass('fieldError');
			}
			if (!($('#paymentDate').hasClass('fieldError'))){
				var paymentDate = new Date(pay.paymentDate.datepicker('getDate'));
				var cancelDate = new Date(pay.cancelPaymentDate.datepicker('getDate'));
				if (cancelDate.getTime() < paymentDate.getTime()) {
					$('#paymentDate').addClass('fieldError');
					$('#paymentDateError').html('Must be before than Cancel Date');
				} else {
					$('#paymentDate').removeClass('fieldError');
					$('#paymentDateError').html('');
				}
			}
		});
		
		pay.cancelPaymentDate.change(function(){
			var canPayDate = $("#cancelPaymentDate").val();
			if(canPayDate == ""){
				$("#cancelPaymentDateError").html(" Required");
				$("#cancelPaymentDate").addClass('fieldError');
			}else{
				$("#cancelPaymentDateError").html("");
				$("#cancelPaymentDate").removeClass('fieldError');
			}
			if (!($('#cancelPaymentDate').hasClass('fieldError'))){
				var appDate = new Date(pay.appDate.val());
				var cancelDate = new Date(pay.cancelPaymentDate.val());
				var paymentDate = new Date(pay.paymentDate.val());
				if ((cancelDate.getTime() > appDate.getTime())||(cancelDate.getTime() < paymentDate.getTime())) {
					if (cancelDate.getTime() > appDate.getTime()) {
						$('#cancelPaymentDate').addClass('fieldError');
						$('#cancelPaymentDateError').html('Must be before than Application Date');
					}
					
					if (cancelDate.getTime() < paymentDate.getTime()){
						$('#cancelPaymentDate').addClass('fieldError');
						$('#cancelPaymentDateError').html('Must be later than Payment Date');	
					}
				
				} else {
					$('#cancelPaymentDate').removeClass('fieldError');
					$('#cancelPaymentDateError').html('');
					
					$('#paymentDate').removeClass('fieldError');
					$('#paymentDateError').html('');
				}
			}
			
		});
		//fillCancelByNo(false);
		
		pay.btnNext.click(function(){

			var fundCode = $("#fundCode").val();
			var payDate = $("#paymentDate").val();
			var canPayDate = $("#cancelPaymentDate").val();
			
			if(fundCode == "" || payDate == "" || canPayDate == ""){
				if(fundCode == ""){
					$("#fundCodeError").html(" Required");
					$("#fundCode").addClass('fieldError');
				}if(payDate == ""){
					$("#paymentDateError").html(" Required");
					$("#paymentDate").addClass('fieldError');
				}if(canPayDate == ""){
					$("#cancelPaymentDateError").html(" Required");
					$("#cancelPaymentDate").addClass('fieldError');
				}
				return false;
			}else if (pay.cancelBy.val() != NO_FILTER && pay.cancelByNo.val() == ""){
					$("#cancelByNoError").html(" Required");
					$("#cancelByNo").addClass('fieldError');
			}
			else{

			var err="0";
			if($("#fundCode").val() == ""){
				$("#fundCodeError").html(" Required");
				err="1";
			}else{
				$("#fundCodeError").html("");
			}
			if($("#paymentDate").val() == ""){
				$("#paymentDateError").html(" Required");
				err="1";
			}else{
				$("#paymentDateError").html("");
			}
			if($("#cancelPaymentDate").val() == ""){
				$("#cancelPaymentDateError").html(" Required");
				err="1";
			}else{
				$("#cancelPaymentDateError").html("");
			}
			if(err == "1"){
				return;
			}
			var paymentDate = new Date(pay.paymentDate.datepicker('getDate'));
			var cancelDate = new Date(pay.cancelPaymentDate.datepicker('getDate'));
			var appDate = new Date($("#appDate").val().split("/")[2],$("#appDate").val().split("/")[1] - 1,$("#appDate").val().split("/")[0]);
			//alert(appDate.getTime());
			if (cancelDate.getTime() < paymentDate.getTime()) {
				$('#paymentDate').addClass('fieldError');
				$('#paymentDateError').html('Payment Date can not greather than Cancel Date');
				return;
			} else{
				$('#paymentDateError').html('');
			}
			
			if (cancelDate.getTime() < appDate.getTime()) {
				$('#cancelPaymentDate').addClass('fieldError');
				$('#cancelPaymentDateError').html('Cancel date can not greather than Aplication date');
				return;
			} else{
				$('#cancelPaymentDateError').html('');
			}
			
			
			if (!pay.fundCode.isEmpty() && !pay.paymentDate.isEmpty() && !pay.type.isEmpty()) {
				if (pay.cancelBy.val() == NO_TRX || pay.cancelBy.val() == NO_PAYMENT || pay.cancelBy.val() == NO_DIVIDEND) {
					if (pay.cancelByNo.isEmpty()) {
						var label = $("label", pay.cancelByP).html();
//						alert("Please Fill "+label);
						//messageAlertOk("Please Fill "+label, "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
						$("#cancelByNoError").html(" Please Fill "+label);
						return;
					}
				}	

				pay.registryCancelPaymentForm.attr('action', 'showTransaction');
				pay.registryCancelPaymentForm.submit();

			}else{
//				alert("Please fill fundCode, paymentDate and type");
				//messageAlertOk("Please fill Fund Code, Payment Date and Type", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
							

			}
			}
//			app.tblPaging.fnPageChange("first");
		});
		
		var backToList = function() {
			location = "@{list()}";
		}
		
		pay.btnProcess.click(function(){
			var selected = "";
			var selectedNominal = "";
				$("input:checked", html).each(function(){
				var tradeKey = $(this).val();
				var nominal = $(this).attr("nominal");
				if (tradeKey != "on") {
					if (selected == "") {
						selected = tradeKey;
						selectedNominal = nominal;
					} else {
						selected += ("," + tradeKey);
						selectedNominal += ("," + nominal);
					}
				}
			});

			pay.selected.value(selected);
			pay.selectedNominal.value(selectedNominal);
			if (selected == "") {
				//alert("Please select the checkbox");
				messageAlertOk("Please select the checkbox", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
			}else{
//				pay.registryCancelPaymentForm.attr('action', 'process');
//				pay.registryCancelPaymentForm.submit();
				var action = "@{process()}";
				$.post(action, $('#registryCancelPaymentForm').serialize(), function(data, status, xhr) {
		    		checkRedirect(data);
					loading.dialog('close');
					if (data.status == 'success') {
						messageAlertOk(data.message, "ui-icon ui-icon-circle-check", "Notification", backToList);
					} else {
						messageAlertOk(data.message, "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
					}
//					messageAlertOk("Payment : " + data.paymentKey + " Successfully", "ui-icon ui-icon-circle-check", "Notification", backToList);
					
				});
			}
		});
		
		
//		$("tbody tr", pay.tblTransaction).each(function(idx, obj){
//			$(this).click(function(){
//				var tds = $("td", $(this));
//				pay.dlgRowNo.val(idx);
//				pay.dlgPayNo.val(tds.eq(1).html());
//				pay.dlgDivNo.val(tds.eq(2).html());
//				pay.dlgTranNo.val(tds.eq(3).html());
//				pay.dlgInvNo.val(tds.eq(4).html());
//				pay.dlgTradeDate.val(tds.eq(5).html());
//				pay.dlgPayDate.val(tds.eq(6).html());
//				pay.dlgPayAmt.val(tds.eq(7).html());
//				pay.dlgBankCode.val(tds.eq(8).html());
//				pay.dlgAN.val(tds.eq(9).html());
//				pay.dlgBankAcct.val(tds.eq(10).html());
//
//				if (pay.type.val() != TRADETYPE_REDEMPTION) {
//					pay.dlgBtnOk.button("disable");
//					pay.dlgBtnCancel.button("disable");
//				}
//				
//				pay.dlgConfirm.dialog("open");
//			});
//		});
		
		if(flek != 'approve'){
			pay.btnClear.click(function(){
				location.href='@{RegistryCancelPayment.list()}';
				return false;
			});
		}
		
		
//		pay.dlgBtnOk.click(function(){
//			pay.dlgConfirm.dialog("close");
//			var row = pay.dlgRowNo.val();
//			checkbox(row, true);
//		});
//		
//		pay.dlgBtnCancel.click(function(){
//			pay.dlgConfirm.dialog("close");
//			var row = pay.dlgRowNo.val();
//			checkbox(row, false);
//		});
//		
//		pay.dlgBtnClose.click(function(){
//			pay.dlgConfirm.dialog("close");
//		});
		
		if (pay.dispatch.val() == 'entry') {
			fillCancelByNo(false);
			fillCancelByOption(pay.cancelByHidden.val());

			if (pay.type.val() != TRADETYPE_REDEMPTION){
				pay.selectAll.attr('disabled', true);
				pay.selectAll.attr('checked', true);
				$("input[name=option]", html).attr('disabled', true);
				$("tbody tr", pay.tblTransaction).each(function(idx, obj){
					checkbox(idx, true);
				});
			}
			
		}
		
		if (pay.dispatch.val() == 'view') {
			pay.selectAll.attr('disabled', true);
			pay.selectAll.attr('checked', true);
			$("input[name=option]", html).attr('disabled', true);
			$("tbody tr", pay.tblTransaction).each(function(idx, obj){
				checkbox(idx, true);
			});
		}
		
		this.parameter = function(type) {
			var p = new Object();
			p.query = true;
			p.productCode = $("#fundCode").val();
			p.paymentDate = $("#paymentDate").val().fmtYYYYMMDD("/");			
			p.type = $("#type").val();
			p.cancelBy = $("#cancelBy").val();
			p.cancelByNo = $("#cancelByNo").val();
			p.cancelByNo = (p.cancelByNo == '') ? '0' : p.cancelByNo;
			if (p.productCode == '') p.query = false;
			return p;
		};
		
	//	$("#tblPaging").paging("@{RegistryCancelPayment.pagingCancelPayment()}", this);
	}else{
		return new CancelPayment(html);
	}
}
	