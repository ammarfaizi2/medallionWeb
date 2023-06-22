function RgPayment(html) {
	if (this instanceof RgPayment) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject2(this, false);
		
		$("[type='button']", html).button();
		
		$('.calendar', html).datepicker();
		
		app.total.hide();
		//app.download.hide();
		
		var dataTable = app.tblFundTransferRgPayment.dataTable({
			"bJQueryUI": true,
	        "bLengthChange": true,
	        "bDestroy":true,
			"sPaginationType": "full_numbers",
	        "bSort": false,
			"bScrollCollapse": true
    	});

		app.tblFundTransferRgPayment.wrap("<div style='overflow:scroll'>");
		
		app.totalAmount.autoNumeric({vMax: '99999999999999999999.99', dGroup: '3'});
		
		app.cmsRGPayDate.val((new Date()).getTime());
		
/*==================================================================
 * Function
 *==================================================================*/
		
		function validate() {
			$().add(app.asOfDateErrRgp).add(app.fromToErr).html("");
			app.currencyRG.html("");
			
			if (app.checkAsOfDateRgp.is(':checked')) {
				if (app.asOfDateRgp.val() == '') { 
					app.asOfDateErrRgp.html("As Of Date is required")
					return false;
				}
			}
			
			if (app.checkFromToRgp.is(':checked')) {
				if (app.fromDateRgp.val() == '' || app.toDateRgp.val() == '') { 
					app.fromToErr.html("From Date and To Date is required");
					return false;
				}
				var from = app.fromDateRgp.val().split("/");
				var to = app.toDateRgp.val().split("/");
				var fromDatePl = new Date(from[2], from[1] - 1 , from[0]);
				var toDatePl = new Date(to[2], to[1] - 1, to[0]);
				
				if (fromDatePl.getTime() > toDatePl.getTime()) {
					app.fromToErr.html("Date From must be less or equal than Date To");
					app.fromDateRgp.addClass('fieldError');
					return false;
				}else {
					app.fromDateRgp.removeClass('fieldError');
				}
			}
			
			if(app.currencyRG.val() == ""){
				app.currencyRGError.html("Currency is required");
				return false;
			}
			
			return true;
		}	
		
		function replaceAllComma(text, from, to) {
			for (var i = 0; i < 5; i++) {
				text = text.replace(from, to);
			}
			return text;
		}
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/

/*================================================================== 
 * Event
 *================================================================== */
		//app.fundCode.dynapopup('PICK_RG_PRODUCT', '', 'btnPopulate');
		
		app.fundCode.dynapopup2({key:'fundCodeKey', help:'fundCodeHelp', desc:'fundCodeDesc'}, 'PICK_RG_PRODUCT', app.fundCode.val(), 'actionCode', function(data){
			if (data) {
				console.log(data)
				$('#fundCode').removeClass('fieldError');				
				$('#fundCode').val(data.productCode);
				$('#fundCodeKey').val(data.code);
				$('#fundCodeDesc').val(data.description);
				$('#h_fundCodeDesc').val(data.description);
				
			}
		},function(data){
			$('#fundCode').addClass('fieldError');
			$('#fundCodeDesc').val('');
			$('#fundCodeKey').val('');
			$('#h_fundCodeDesc').val('');
			$('#fundCode').val('');	
		});
		
		$("#transactionTypeRg").children().eq(0).remove();
		
		var paramScreen ;
		
		$("#checkAll", html).click(function(){
			var isChecked = $(this).is(':checked');
			$(dataTable.fnGetNodes()).each(function(i, e){
				var b = $(this).data("bean");
				var inputChk = $("input", $("td", $(this)).eq(0));
				if (isChecked && b.colStatus == 'VALID') {
					inputChk.attr("checked", "checked");
				}else
					inputChk.removeAttr("checked");
			});
			
			$(dataTable.fnGetNodes()).each(function(i, e){
				var b = $(this).data("bean");
				var inputChk = $("input", $("td", $(this)).eq(0));
				if (isChecked) {
					if(b.colStatus == 'VALID' && b.colCurrency =='IDR'){
						//inputChk.attr("checked", "checked");
					}else{
						inputChk.removeAttr("checked"); 
					}
				} else {
					inputChk.removeAttr("checked"); 
				}
			});
		});
		
		app.checkAsOfDateRgp.click(function(){
			app.asOfDateRgp.removeAttr("disabled").val(app.applicationDate.val());
			$().add(app.fromDateRgp).add(app.toDateRgp).val("").attr("disabled", "disabled");
			$().add(app.asOfDateErrRgp).add(app.fromToErr).html("");
		}).click();
		
		app.checkFromToRgp.click(function(){
			app.asOfDateRgp.val("").attr("disabled", "disabled");
			$().add(app.fromDateRgp).add(app.toDateRgp).removeAttr("disabled", "disabled").val(app.applicationDate.val());
			$().add(app.asOfDateErrRgp).add(app.fromToErr).html("");
		});
		
		app.btnPopulate.click(function(){
			if (validate()) {		
				loading.dialog('open');
				app.currencyRGError.html("");
				app.currencyRG.removeClass('fieldError');
				var param = {
					"asOfFlag" : app.checkAsOfDateRgp.is(':checked'),
					"fundCode" : app.fundCode.val(),
					"transactionType" : app.transactionTypeRg.val(),
					"currency" :  app.currencyRG.val()
				};
				
				if (param.asOfFlag) {
					param.strAsOfDate = app.asOfDateRgp.val();
				}else{
					param.strFromDate = app.fromDateRgp.val(); 
					param.strToDate = app.toDateRgp.val(); 
				}
				
				paramScreen = param;
				
				$().fetchSync("@{getRgPaymentPopulate()}", {'param':param}, function(datas) {
					loading.dialog('close');
					$("#checkAll", html).removeAttr('checked')
					
					var table = app.tblFundTransferRgPayment.dataTable();
					table.fnClearTable();
					for (var x in datas) {
						var b = datas[x];
						var rownum;
						if(b.colStatus == 'VALID' && b.colCurrency =='IDR'){
							rownum = table.fnAddData(["<input type='checkbox'/>", b.colNo, b.colDate, b.colAccountName, 
							                          b.colFundCode, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDrCr, b.colStatus, 
							                          b.colSenderBranchCode, b.colSenderBankCode, b.colSenderAccName, b.colSenderAccNo, b.colSenderCurrency, b.colBenefBank, b.colBenefBankName, b.colBenefAccNo, b.colBenefAccName, b.colBenefCurrency, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
						} else {
							rownum = table.fnAddData(["<input type='checkbox'  disabled/>", b.colNo, b.colDate, b.colAccountName, 
							                          b.colFundCode, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDrCr, b.colStatus, 
							                          b.colSenderBranchCode, b.colSenderBankCode, b.colSenderAccName, b.colSenderAccNo, b.colSenderCurrency, b.colBenefBank, b.colBenefBankName, b.colBenefAccNo, b.colBenefAccName, b.colBenefCurrency, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
						}
						
						var ptr = table.fnSettings().aoData[rownum].nTr;
						ptr.childNodes[7].style.textAlign = 'right';
						ptr.childNodes[20].style.textAlign = 'right';
						ptr.childNodes[21].style.textAlign = 'right';
			    		$(ptr).data("bean", b);
					}
					table.fnDraw();
					checkRedirect(datas);
				});
			}			
		});
		
		$('#currencyRG').lookup({
			list:'@{Pick.currencies()}',
			get:{
				url:'@{Pick.currency()}',
				success: function(data) {
					if (data) {
						$('#currencyRG').removeClass('fieldError');
						$('#currencyRG').val(data.code);
						$('#currencyRGDesc').val(data.description);
						$('#h_currencyRGDesc').val(data.description);
					}
				},
				error : function(data) {
					$('#currencyRG').addClass('fieldError');
					$('#currencyRGDesc').val('NOT FOUND');
					$('#currencyRG').val('');
					$('#h_currencyRGDesc').val('');
				}
			},
			key:$('#currencyRG'),
			description:$('#currencyRGDesc'),
			help:$('#currencyRGHelp')
		});
		
		app.btnSaveRGPay.click(function(){
			loading.dialog('open');
			
			var datas = [];
			
			var ctr = 0;
			$(dataTable.fnGetNodes()).each(function(i, e){
				var inputChk = $("input", $("td", $(this)).eq(0));
				var isChecked = inputChk.is(':checked');
				if (isChecked) {
					var tr = inputChk.parent().parent();
					tr.data("bean").colNo = ctr++; 
					datas[datas.length] = tr.data("bean");
				}
			});
			
			loading.dialog('close');
			
			if (datas.length == 0) {
				var closeDialog = function(){
					$("#dialog-message").dialog("close");
				};
				
				messageAlertOk("Please choose data, minimal 1 row", "ui-icon ui-icon-notice", "Warning", closeDialog);
			}else{
				dataTable.fnClearTable();
				var totalAmount = 0;
				var totCharges = 0;
				for (var x in datas) {
					var b = datas[x];
					b.colNo = ++x;
					var rownum = dataTable.fnAddData(["<input type='checkbox'/>", b.colNo, b.colDate, b.colAccountName, 
							                          b.colFundCode, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDrCr, b.colStatus, 
							                          b.colSenderBranchCode, b.colSenderBankCode, b.colSenderAccName, b.colSenderAccNo, b.colSenderCurrency, b.colBenefBank, b.colBenefBankName, b.colBenefAccNo, b.colBenefAccName, b.colBenefCurrency, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
		    		var ptr = dataTable.fnSettings().aoData[rownum].nTr;
		    		
					ptr.childNodes[7].style.textAlign = 'right';
					ptr.childNodes[20].style.textAlign = 'right';
					ptr.childNodes[21].style.textAlign = 'right';
		    		
		    		$(ptr).data("bean", b);
		    		
		    		totalAmount += parseFloat(replaceAllComma(b.colNetAmount, ',',''));
		    		totCharges += parseFloat(replaceAllComma(b.colChargeAmount, ',',''));
				}
				dataTable.fnDraw();
				
				$("input[type='checkbox']", app.tblFundTransferRgPayment).attr('checked', 'checked').attr('disabled', 'disabled')
				app.btnSaveRGPay.hide();
				app.btnConfirmRGPay.show();
				
				$().add(app.checkAsOfDateRgp).add(app.asOfDateRgp).add(app.checkFromToRgp).add(app.fromDateRgp).add(app.toDateRgp)
				.add(app.transactionTypeRg).add(app.fundCode).add(app.currencyRG).attr('disabled', 'disabled');
				
				$().add(app.fundCodeHelp).add(app.btnPopulate).add(app.currencyRGHelp).button("disable");
				
				app.total.show();
				
				app.totalAmount.autoNumericSet(totalAmount);
				app.totalChageAmount.autoNumericSet(totCharges);
				
			}
		});
		
		
		app.btnConfirmRGPay.click(function(){
			loading.dialog('open');
			
			console.log("btnConfirmRGPay");
			
			var param = [];
			
			$(dataTable.fnGetNodes()).each(function(i, e){
				var bean = $(this).data("bean").fundTransferDetail;
				bean.noSeq = $(this).data("bean").colNo;
				param[param.length] = bean;
			});
			
			$().submitAsync("@{saveFundTransfer()}", {'paramDetail':param, 'paramScreen':paramScreen, 'processType':'FUND_TRANSFER_PROCESS-5'}, function(respone) {
				loading.dialog('close');
				loading.dialog('close');
				
				if (respone.status == 'SUCCESS'){
					app.fundBatchNoRGPay.val(respone.batchId);
					$().add(app.btnConfirmRGPay).button("disable");
				}
				
				var closeDialog = function(){
					$("#dialog-message").dialog("close");
					location = "@{FundTransfer.list()}";
				};

				messageAlertOk(respone.msg, "ui-icon ui-icon-success", "Information", closeDialog);
			});			
		}).hide();
		
		app.btnReset.click(function(){
			$().add(app.checkAsOfDateRgp).add(app.asOfDateRgp).add(app.checkFromToRgp).add(app.fromDateRgp).add(app.toDateRgp).add(app.fundCode).removeAttr('disabled')
			$().add(app.fundCodeHelp).add(app.btnPopulate).add(app.currencyRGHelp).button("enable");
			
			app.checkAsOfDateRgp.click();
			
			app.fundCodeKey.val("");
			app.fundCode.val("");
			app.fundCodeDesc.val("");
			app.totalAmount.val("");
			app.fundBatchNoRGPay.val("");
			app.transactionTypeRg.val("ALL");
			app.currencyRG.val("");
			app.currencyRG.blur();
			
			app.total.hide();
			
			app.asOfDateRgp.removeClass('fieldError');
			app.fromDateRgp.removeClass('fieldError');
			app.toDateRgp.removeClass('fieldError');
			app.transactionTypeRg.removeClass('fieldError');
			app.currencyRG.removeClass('fieldError');
			
			$("#checkAll", html).removeAttr('checked').removeAttr('disabled');
			app.transactionTypeRg.removeAttr('disabled');
			app.currencyRG.removeAttr('disabled');
			
			dataTable.fnClearTable();
			
			app.btnSaveRGPay.show();
			$().add(app.btnConfirmRGPay).button("enable");
			app.btnConfirmRGPay.hide();
		});

		Date.prototype.ddmmyyyy = function() {
			var mm = this.getMonth() + 1;
			var dd = this.getDate();
			return [(dd > 9 ? '' : '0') + dd,'/', (mm > 9 ? '' : '0') + mm,'/', this.getFullYear()].join('');
		};
		
	}else{
		return new RgPayment(html);
	}
}