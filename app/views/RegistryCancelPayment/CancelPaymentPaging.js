function CancelPaymentPaging(html) {
	if (this instanceof CancelPaymentPaging) {
		
		/* =========================================================================== 
		 * Variable
		 * ========================================================================= */
				var app = html.inject(this, true);
				var roundType;
				var roundDigit;
				
				var NO_FILTER = "NO_FILTER";
				var NO_TRX = "NO_TRX";
				var NO_PAYMENT = "NO_PAYMENT";
				var NO_DIVIDEND = "NO_DIVIDEND";
				
				var TRADETYPE_REDEMPTION = "Redemption";
				var iTotalAmount = 0;
				var totalPayment = 0;
				var isOptionAll = false;
				var pagingData;


		//String productCode, String type, String cancelBy, long cancelByNo, String paymentDate
		/* =========================================================================== 
		 * Table Serach Parameter (penamaan harus fix yaitu parameter)
		 * ========================================================================= */
		
		this.parameter = function() {
			var p = new Object();
			p.fundCode = app.fundCode.val();
			p.paymentDate = app.paymentDate.val();
			p.type = app.type.val();
			p.cancelBy = app.cancelBy.val();
			p.cancelByNo = app.cancelByNo.val();
			p.cancelPaymentDate = app.cancelPaymentDate.val();
			p.remarks = app.remarks.val();
			p.query = true;
			return p;
		};
		
		app.datatable = app.tblTransaction.paging("@{RegistryCancelPayment.showTransactionPaging()}", this, function(data){
			console.log( "iTotalAmount:"+iTotalAmount );
			console.log( "isOptionAll:"+isOptionAll );
			iTotalAmount = data.iTotalAmount;
			app.optionAll.attr("checked", isOptionAll);
			app.optionAll.change();
			// bind the event
			html.name("optionAll").change(function(){
				if( $(this).attr("id") != "optionAll" ){
					app.optionAll.attr("checked", false);
					calculateTotalPayment();				
				}
			});
			
			pagingData = data;

		});

		var closeDialogMessage = function() {
			$("#dialog-message").dialog("close");
		};


		/* ===========================================================================
		* Declare popup tabel /detail
		* ===========================================================================*/
		app.dlgConfirm.dialog({
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
			totalPayment = 0;
			$("#registryCancelPaymentForm").name("optionAll").each(function(idx, e){
				var id_ = $(e).attr("id");
				if( id_ != "optionAll" ){
					if (($(e).checked() != "false")) {
						var payAmount = $(e).parent().parent().children().eq(6);
						totalPayment += payAmount.html().toNumber(",");
					}
					if (app.type.val()!=TRADETYPE_REDEMPTION){
						if (($(e).checked() != false)) {
							var payAmount = $(e).parent().parent().children().eq(6);
							totalPayment += payAmount.html().toNumber(",");
						}
					}					
				}
			});
			if (app.type.val() == TRADETYPE_REDEMPTION) {
				app.totPaidAmount.valueRnd(totalPayment, true, roundDigit, roundType);
			}
		}
		
		function fillCancelByOption(cancelByHidden) {
			if (app.type.val() == TRADETYPE_REDEMPTION) {
				if ((cancelByHidden == NO_FILTER)||(cancelByHidden == '')){
					$('#cancelBy option').remove();
					var options = $('#cancelBy').attr('options');
						options[options.length] = new Option('All', NO_FILTER);
						options[options.length] = new Option('Transaction No', NO_TRX);
						options[options.length] = new Option('Payment No', NO_PAYMENT);
				} else {
					$("option", app.cancelBy).eq(3).remove();
				}
			} else {
				if (cancelByHidden == ''){
					$('#cancelBy option').remove();
					var options = $('#cancelBy').attr('options');
					options[options.length] = new Option('Payment No', NO_PAYMENT);
					options[options.length] = new Option('Dividend No', NO_DIVIDEND);
				} else {
					$("option", app.cancelBy).eq(0).remove();
					$("option", app.cancelBy).eq(0).remove();
				}
			}	
	
		}
		
		function fillCancelByNo(isClear) {
			if (app.cancelBy.val() == NO_FILTER) {
				if (isClear) app.cancelByNo.val("");
				app.cancelByNo.disabled();
				$('p[id=pCancelBy] label span').html("");
			} else {
				if (isClear) app.cancelByNo.val("");
				app.cancelByNo.enabled();
				$('p[id=pCancelBy] label span').html(" *");
			}
			
		}
		
		function checkbox(row, checked) {			
			var td = $("tbody", app.tblTransaction).children().eq(row).children().eq(0);
			if (checked) {
				$("input", td).attr("checked", "checked");	
			}else{
				$("input", td).removeAttr("checked");	
			}
		}

		/* =========================================================================== 
		 * Event
		 * ========================================================================= */

		$("option", app.type).eq(0).remove();
		$("option", app.cancelBy).eq(0).remove();
		
		app.fundCode.popupProduct("paymentDate", function(data){
			if (data) {
				var rgProduct = html.getRgProduct(app.fundCode.val());
				roundType = rgProduct.amountRoundType;
				roundDigit = rgProduct.amountRoundValue;
				$("#fundCodeError").html("");
				$("#fundCode").removeClass('fieldError');				
			}
		});
		
		if (!app.fundCode.val().isEmpty()) {
			var rgProduct = html.getRgProduct(app.fundCode.val());
			roundType = rgProduct.amountRoundType;
			roundDigit = rgProduct.amountRoundValue;
		}
		
		app.cancelByNo.change(function(){
			$("#cancelByNoError").html("");
			$("#cancelByNo").removeClass('fieldError');
		});
		
		app.type.change(function(){
			fillCancelByOption('');
			fillCancelByNo(true);
			$('#tblTransaction').dataTable().fnClearTable();
			$('#tblTransaction').css('width', '');
			app.optionAll.attr("checked", false);
			isOptionAll = false;
		});
		
		app.optionAll.change(function(){
			var checkedval = app.optionAll.attr("checked");
			html.name("optionAll").attr("checked", checkedval);
			isOptionAll = checkedval;
			if (app.type.val() == TRADETYPE_REDEMPTION) {
				if( checkedval ){
					app.totPaidAmount.valueRnd(iTotalAmount, true, roundDigit, roundType);
				}else{
					app.totPaidAmount.valueRnd(0, true, roundDigit, roundType);
				}
			}else{
				app.totPaidAmount.valueRnd(0, true, roundDigit, roundType);
			}
		});
		
		if (app.type.val() != TRADETYPE_REDEMPTION){
			calculateTotalPayment();
		}
		
		
		app.cancelBy.change(function(){
			fillCancelByNo(true);
		});
		
		app.paymentDate.change(function(){
			var payDate = $("#paymentDate").val();
			if(payDate == ""){
				$("#paymentDateError").html(" Required");
				$("#paymentDate").addClass('fieldError');
			}else{
				$("#paymentDateError").html("");
				$("#paymentDate").removeClass('fieldError');
			}
			if (!($('#paymentDate').hasClass('fieldError'))){
				var paymentDate = new Date(app.paymentDate.datepicker('getDate'));
				var cancelDate = new Date(app.cancelPaymentDate.datepicker('getDate'));
				if (cancelDate.getTime() < paymentDate.getTime()) {
					$('#paymentDate').addClass('fieldError');
					$('#paymentDateError').html('Must be before than Cancel Date');
				} else {
					$('#paymentDate').removeClass('fieldError');
					$('#paymentDateError').html('');
				}
			}
		});
		
		app.cancelPaymentDate.change(function(){
			var canPayDate = $("#cancelPaymentDate").val();
			if(canPayDate == ""){
				$("#cancelPaymentDateError").html(" Required");
				$("#cancelPaymentDate").addClass('fieldError');
			}else{
				$("#cancelPaymentDateError").html("");
				$("#cancelPaymentDate").removeClass('fieldError');
			}
			if (!($('#cancelPaymentDate').hasClass('fieldError'))){
				var appDate = new Date(app.appDate.val());
				var cancelDate = new Date(app.cancelPaymentDate.val());
				var paymentDate = new Date(app.paymentDate.val());
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
		
		app.btnNext.click(function(){

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
			}else if (app.cancelBy.val() != NO_FILTER && app.cancelByNo.val() == ""){
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
				var paymentDate = new Date(app.paymentDate.datepicker('getDate'));
				var cancelDate = new Date(app.cancelPaymentDate.datepicker('getDate'));
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
				
				
				if (!app.fundCode.isEmpty() && !app.paymentDate.isEmpty() && !app.type.isEmpty()) {
					if (app.cancelBy.val() == NO_TRX || app.cancelBy.val() == NO_PAYMENT || app.cancelBy.val() == NO_DIVIDEND) {
						if (app.cancelByNo.isEmpty()) {
							var label = $("label", app.cancelByP).html();
	//						alert("Please Fill "+label);
							//messageAlertOk("Please Fill "+label, "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
							$("#cancelByNoError").html(" Please Fill "+label);
							return;
						}
					}	
	
					app.datatable.fnPageChange("first");
				}else{
	//				alert("Please fill fundCode, paymentDate and type");
					//messageAlertOk("Please fill Fund Code, Payment Date and Type", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
								
	
				}
			}
		});
		
		var backToList = function() {
			location = "@{list()}";
		}
		
		app.btnProcess.click(function(){
			var selected = "";
			if( isOptionAll ){
				selected = "*";
			}else{
				$("#registryCancelPaymentForm").name("optionAll").each(function(idx, e){
					var id_ = $(e).attr("id");
					if( id_ != "optionAll" ){
						if (($(e).checked() != "false")) {
							var payAmount = $(e).parent().parent().children().eq(6);
							totalPayment += payAmount.html().toNumber(",");
						}
						if (app.type.val()!=TRADETYPE_REDEMPTION){
							if (($(e).checked() != false)) {
								var payAmount = $(e).parent().parent().children().eq(6);
								totalPayment += payAmount.html().toNumber(",");
							}
						}					
					}
				});
				
				$("#registryCancelPaymentForm").name("optionAll").each(function(idx, e){
					var id_ = $(e).attr("id");
					if( id_ != "optionAll" ){
						var tradeCell = $(e).parent().parent().children().eq(2);
						if (($(e).checked() != "false")) {							
							if (selected == "") {
								selected = tradeCell.html();
							} else {
								selected += ("," + tradeCell.html());
							}
						}
					}
				});				
			}
			app.selected.value(selected);
			app.selectedNominal.value(app.totPaidAmount.val());
			if (selected == "") {
				//alert("Please select the checkbox");
				messageAlertOk("Please select the checkbox", "ui-icon1 ui-icon-alert", "Warning Message", closeDialogMessage);
			}else{
				console.log( pagingData );
				console.log( selected );
				console.log( app.totPaidAmount.val() );
				console.log( $('#registryCancelPaymentForm').serialize() );
//				app.registryCancelPaymentForm.attr('action', 'process');
//				app.registryCancelPaymentForm.submit();
				pagingData.data = null;
				var action = "@{process()}?"+$('#registryCancelPaymentForm').serialize();
				$.post(action, {"page":pagingData}, function(data, status, xhr) {
		    		checkRedirect(data);
					loading.dialog('close');
					if (data.status == 'success') {
						//messageAlertOk(data.message, "ui-icon ui-icon-circle-check", "Notification", backToList);
						messageAlertOk(data.message, "ui-icon ui-icon-circle-check", "Notification", closeDialogMessage);
					} else {
						messageAlertOk(data.message, "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
					}
//					messageAlertOk("Payment : " + data.paymentKey + " Successfully", "ui-icon ui-icon-circle-check", "Notification", backToList);
					
				});
			}
		});
		
		app.btnClear.click(function(){
			location.href='@{RegistryCancelPayment.list()}';
			return false;
		});

		if (app.dispatch.val() == 'entry') {
			fillCancelByNo(false);
			fillCancelByOption(app.cancelByHidden.val());

			if (app.type.val() != TRADETYPE_REDEMPTION){
				app.optionAll.attr('disabled', true);
				app.optionAll.attr('checked', true);
				$("input[name=option]", html).attr('disabled', true);
				$("tbody tr", app.tblTransaction).each(function(idx, obj){
					checkbox(idx, true);
				});
			}
			
		}
		
		if (app.dispatch.val() == 'view') {
			app.optionAll.attr('disabled', true);
			app.optionAll.attr('checked', true);
			$("input[name=option]", html).attr('disabled', true);
			$("tbody tr", app.tblTransaction).each(function(idx, obj){
				checkbox(idx, true);
			});
		}
		
	} else {
		return new CancelPaymentPaging(html);
	}
}