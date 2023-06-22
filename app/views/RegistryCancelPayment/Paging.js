function Paging(html) {
	if (this instanceof Paging) {
		
		var roundType;
		var roundDigit;
		
		var NO_FILTER = "NO_FILTER";
		var NO_TRX = "NO_TRX";
		var NO_SWITCHING = "NO_SWITCHING";
		var NO_PAYMENT = "NO_PAYMENT";
		var NO_BATCH = "NO_BATCH";
		var NO_DIVIDEND = "NO_DIVIDEND";
		
		var TRADETYPE_REDEMPTION = "Redemption";
		var TRADETYPE_SWITCH_OUT = "Switch-Out";

		var flek = '${flek}';
		
		var pay = html.inject(this, true);
		
		var closeDialogMessage = function() {
			$("#dialog-message").dialog("close");
		};
		
		
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
			var props = pay.datatable.selects(0, "checked");
			for (x in props) {
				var listData = props[x].bean;
				totalPayment += listData.paidAmt;
			}
			
			if (pay.type.val() == TRADETYPE_REDEMPTION || pay.type.val() == TRADETYPE_SWITCH_OUT) {
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
			} else if (pay.type.val() == TRADETYPE_SWITCH_OUT ) {
				if ((cancelByHidden == NO_FILTER)||(cancelByHidden == '')){
					$('#cancelBy option').remove();
					var options = $('#cancelBy').attr('options');
						options[options.length] = new Option('All', NO_FILTER);
						options[options.length] = new Option('Switching No', NO_SWITCHING);
				} else {
					$("option", pay.cancelBy).eq(2).remove();
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
			
			var td = $("tbody", pay.tblPaging).children().eq(row).children().eq(0);
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
			pay.selectAll.attr('disabled', false);
			pay.selectAll.attr('checked', false);
			$("#isKlik").val('n');
			$('#tblPaging').dataTable().fnClearTable();
			$('#tblPaging').css('width', '');
			pay.totPaidAmount.val('');
			$('h_totPaidAmount').val('');
		});
		
		pay.selectAll.change(function(){
			var checkedval = pay.selectAll.attr("checked");
			html.name("optionAll").attr("checked", checkedval);
			calculateTotalPayment();
		});
		
		if (pay.type.val() != TRADETYPE_REDEMPTION && pay.type.val() != TRADETYPE_SWITCH_OUT){
			calculateTotalPayment();
		}
		
		/*html.name("optionAll").change(function(){
			pay.selectAll.attr("checked", false);
			calculateTotalPayment();
		});*/
		
		
		
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
		
		$('#btnNext').click(function() {
			
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
			} else {

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
						var label = $("label", pay.cancelBy).html();
						$("#cancelByNoError").html(" Please Fill "+label);
						return;
					}
				}	
				$("#isKlik").val('y');
				$('#result').css('display', '');
				pay.datatable.search.val("");
				pay.datatable.fnPageChange("first");
			}
			}
			
		});
		
		var backToList = function() {
			location = "@{list()}";
		}
		
		pay.btnProcess.click(function(){
			var selected = "";
			var selectedNominal = "";
			
			var props = pay.datatable.selects(0, "checked");
			for (x in props) {
				var listData = props[x].bean;
				if (selected == "") {
					selected = listData.tradeKey;
					selectedNominal = listData.paidAmt;
				} else {
					selected += ("," + listData.tradeKey);
					selectedNominal += ("," + listData.paidAmt);
				}
			}
			
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
		
		if(flek != 'approve'){
			pay.btnClear.click(function(){
				location.href='@{RegistryCancelPayment.list()}';
				return false;
			});
		}
		
		if (pay.dispatch.val() == 'entry') {
			fillCancelByNo(false);
			fillCancelByOption(pay.cancelByHidden.val());

			if (pay.type.val() != TRADETYPE_REDEMPTION && pay.type.val() != TRADETYPE_SWITCH_OUT){
				pay.selectAll.attr('disabled', true);
				pay.selectAll.attr('checked', true);
				$("input[name=optionAll]", html).attr('disabled', true);
				$("tbody tr", pay.tblPaging).each(function(idx, obj){
					checkbox(idx, true);
				});
			}
			
		}
		
		if (pay.dispatch.val() == 'view') {
			pay.selectAll.attr('disabled', true);
			pay.selectAll.attr('checked', true);
			$("input[name=optionAll]", html).attr('disabled', true);
			$("tbody tr", pay.tblPaging).each(function(idx, obj){
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
			if ($("#isKlik").val() == 'n') p.query = false;
			return p;
		};
		
		pay.datatable = pay.tblPaging.paging("@{RegistryCancelPayment.pagingCancelPayment()}", this, function(){
			if (pay.dispatch.val() == 'entry') {
				if (pay.type.val() != TRADETYPE_REDEMPTION && pay.type.val() != TRADETYPE_SWITCH_OUT){
					pay.selectAll.attr('disabled', true);
					pay.selectAll.attr('checked', true);
					$("input[name=optionAll]", html).attr('disabled', true);
					$("tbody tr", pay.tblPaging).each(function(idx, obj){
						checkbox(idx, true);
					});
				}
				
			}
			
			if (pay.dispatch.val() == 'view') {
				pay.selectAll.attr('disabled', true);
				pay.selectAll.attr('checked', true);
				$("input[name=optionAll]", html).attr('disabled', true);
				$("tbody tr", pay.tblPaging).each(function(idx, obj){
					checkbox(idx, true);
				});
			}
			
			var trs = $("tbody tr", pay.tblPaging);
			trs.each(function(idx, e){
				var prop = $(e).data("prop");
				if( prop.bean ){
					// formatting amount
					var formatedAmount = html.valueRnd(prop.bean.paidAmt, true, roundDigit, roundType);
					var xtd = $("td", this).eq(6);
					xtd.html(formatedAmount.val());					
				}
			});

		});
		
		pay.datatable.bind("checkbox", function(event, prop, isChecked){
			calculateTotalPayment();
		});
		
	}else{
		return new Paging(html);
	}
}