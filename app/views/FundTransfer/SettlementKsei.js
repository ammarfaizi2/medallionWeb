function SettlementKsei(html) {
	if (this instanceof SettlementKsei) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject2(this, false);
		
		$("[type='button']", html).button();
		
		$('.calendar', html).datepicker();
		
		app.totalKsei.hide();
		
		var dataTable = app.tblFundTransferKsei.dataTable({
    		"bJQueryUI": true,
	        "bLengthChange": true,
	        "bDestroy":true,
			"sPaginationType": "full_numbers",
	        "bSort": false,
			"bScrollCollapse": true
    	});

		app.tblFundTransferKsei.wrap("<div style='overflow:scroll'>");
		app.totAmtDrKsei.autoNumeric({vMax: '99999999999999999999.99', dGroup: '${digitKsei}'});
		app.totAmtCrKsei.autoNumeric({vMax: '99999999999999999999.99', dGroup: '${digitKsei}'});
		app.totCharges.autoNumeric({vMax: '99999999999999999999.99', dGroup: '${digitKsei}'});
		
/*==================================================================
 * Function
 *==================================================================*/
		
		function validate() {
			$().add(app.asOfDateKseiErr).add(app.kseiDateToError).html("");
			var gosave = true;
			app.asOfDateKseiErr.html("");
			app.kseiDateToError.html("");
			app.transTypeError.html("");
			app.securityTypeError.html("");
			app.transferTypeError.html("");
			app.currencyError.html("");
			
			if (app.checkAsOfKseiDate.is(':checked')) {
				if (app.asOfDateKsei.val() == '') { 
					app.asOfDateKseiErr.html("As Of Date is required");
					gosave = false;
				}
			}

			if (app.checkKseiFromTo.is(':checked')) {
				if (app.kseiDateFrom.val() == '' || app.kseiDateTo.val() == '') { 
					app.kseiDateToError.html("From Date and To Date is required");
					gosave = false;
				}
			}

			if ((app.kseiDateFrom.val() != '') && (app.kseiDateTo.val() != '')) {
				var dateFrom = app.kseiDateFrom.datepicker('getDate');
				var dateTo = app.kseiDateTo.datepicker('getDate');
				var origin = 'from';
				var id = '#kseiDate';

				compareDateFromToEqual(dateFrom, dateTo, origin, id);
				if(app.kseiDateFrom.hasClass('fieldError') || app.kseiDateTo.hasClass('fieldError')){
					gosave = false;
				}
			}
			
			if(app.transType.val() == ""){
				app.transTypeError.html("Transaction type is required");
				gosave = false;
			}
			
			/*if(app.securityType.val() == ""){
				app.securityTypeError.html("Security type is required");
				gosave = false;
			}*/
			
			if(app.transferType.val() == ""){
				app.transferTypeError.html("Transfer type is required");
				gosave = false;
			}
			
			if(app.currency.val() == ""){
				app.currencyError.html("Currency is required");
				gosave = false;
			}

			return gosave;
		}
		
		$("#transferType").children().eq(0).remove();
		$("#transType").children().eq(0).remove();
		$("#transType").change(function(){
			$("#transferType").find('option').remove();
			if($(this).val() == "RVP"){
				$("#transferType").find('option').remove();
				$('#transferType').append('<option value="ALL">ALL</option>').val('ALL');
			}
			$('#transferType').append('<option value="TRANSFER_TYPE-POOL">POOL</option>').val('ALL');
			$('#transferType').append('<option value="TRANSFER_TYPE-MANUAL">MANUAL TO POOL</option>').val('ALL');
		});
		$("#transType").trigger('change');

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
		app.accountNoKsei.dynapopup2({key:'accountNoKseiKey', help:'accountNoKseiHelp', desc:'accountNoKseiDesc'}, 'PICK_CS_ACCOUNT_FUND_TRANSFER', app.accountNoKsei.val(), 'actionCode', function(data){
			if (data) {
				console.log(data)
				$('#accountNoKsei').removeClass('fieldError');				
				$('#accountNoKsei').val(data.accountNo);
				$('#accountNoKseiKey').val(data.code);
				$('#accountNoKseiDesc').val(data.description);
				$('#h_accountNoKseiDesc').val(data.description);
				
			}
		},function(data){
			$('#accountNoKsei').addClass('fieldError');
			$('#accountNoKseiDesc').val('');
			$('#accountNoKseiKey').val('');
			$('#h_accountNoKseiDesc').val('');
			$('#accountNoKsei').val('');	
		});
		
		$('#securityType').lookup({
			list:'@{Pick.securityTypes()}',
			get:{
				url:'@{Pick.securityType()}',
				success: function(data){
					$('#securityType').removeClass('fieldError');
					$('#securityTypeId').val(data.code);
					$('#securityTypeName').val(data.description);
					$('#h_securityTypeName').val(data.description);
					$('#securityCode').val('');
					$('#securityCodeId').val('');
					$('#securityCodeName').val('');
					$('#securityType').change();
 				},
				error: function(data){
					$('#securityType').addClass('fieldError');
					$('#securityType').val('');
					$('#securityTypeId').val('');
					$('#securityTypeName').val('NOT FOUND');
					$('#h_securityTypeName').val('');
					$('#securityType').change();
				}
			},
			description:$('#securityTypeName'),
			help:$('#securityTypeHelp'),
			nextControl:$('#securityCode')
		});
		
		$('#currency').lookup({
			list:'@{Pick.currencies()}',
			get:{
				url:'@{Pick.currency()}',
				success: function(data) {
					if (data) {
						$('#currency').removeClass('fieldError');
						$('#currency').val(data.code);
						$('#currencyDesc').val(data.description);
						$('#h_currencyDesc').val(data.description);
					}
				},
				error : function(data) {
					$('#currency').addClass('fieldError');
					$('#currencyDesc').val('NOT FOUND');
					$('#currency').val('');
					$('#h_currencyDesc').val('');
				}
			},
			key:$('#currency'),
			description:$('#currencyDesc'),
			help:$('#currencyHelp')
		});

		$("#checkAllKsei", html).click(function(){
			var isChecked = $(this).is(':checked');
			$('#tblFundTransferKsei tbody tr').each(function(){
				var btncheck = $(this).find('td input[type=checkbox]');
				if(isChecked) {
					btncheck.attr("checked", "checked");
				} else {
					btncheck.removeAttr('checked');
				}
			});
			
			$(dataTable.fnGetNodes()).each(function(i, e){
				var b = $(this).data("bean");
				var inputChk = $("input", $("td", $(this)).eq(0));
				if (isChecked) {
					if(b.colStatus == 'VALID'  && b.colCurrency =='IDR'){
						//inputChk.attr("checked", "checked");
					}else{
						inputChk.removeAttr("checked"); 
					}
				} else {
					inputChk.removeAttr("checked"); 
				}
			});
		});

		app.checkAsOfKseiDate.click(function(){
			app.kseiDateFrom.removeClass('fieldError');
			app.kseiDateTo.removeClass('fieldError');
			app.asOfDateKsei.removeAttr("disabled").val(app.applicationDateKsei.val());
			$().add(app.kseiDateFrom).add(app.kseiDateTo).val("").attr("disabled", "disabled");
			$().add(app.asOfDateKseiErr).add(app.kseiDateToError).html("");
		}).click();

		app.checkKseiFromTo.click(function(){
			app.asOfDateKsei.removeClass('fieldError');
			app.asOfDateKsei.val("").attr("disabled", "disabled");
			$().add(app.kseiDateFrom).add(app.kseiDateTo).removeAttr("disabled", "disabled").val(app.applicationDateKsei.val());
			$().add(app.asOfDateKseiErr).add(app.kseiDateToError).html("");
		});

		var paramScreen ;
		
		function showTotal(){
			//alert(paramScreen.transactionType)
			if(paramScreen != null && paramScreen.transactionType != null){
				if(paramScreen.transactionType == "RVP"){
					$("#totalDR").show();
					$("#totalCR").hide();
				}else{
					$("#totalDR").hide();
					$("#totalCR").show();
				}
			}
		}
			
		app.btnPopulateKsei.click(function(){
			if (validate()) {		
				loading.dialog('open');
				
				var param = {
					"asOfFlag" : app.checkAsOfKseiDate.is(':checked'),
					"accountNo" : app.accountNoKsei.val(),
					"processModeDef" : app.checkDefaultKsei.is(':checked'),
					"transactionType" : app.transType.val(),
					"securityType" : app.securityType.val(),
					"transferType" : app.transferType.val(),
					"currency" :  app.currency.val(),
					"fundCode" : ""
				};

				if (param.asOfFlag) {
					param.strAsOfDate = app.asOfDateKsei.val();
				} else {
					param.strFromDate = app.kseiDateFrom.val(); 
					param.strToDate = app.kseiDateTo.val();
				}
				
				paramScreen = param;
				showTotal();
				$().fetchSync("@{getSettlementKSEIPopulate()}", {'param':param}, function(datas) {
					loading.dialog('close');
					$("#checkAllKsei", html).removeAttr('checked');

					var table = app.tblFundTransferKsei.dataTable();
					table.fnClearTable();
					for (var x in datas) {
						var b = datas[x];
						var rownum;
						console.log(b)
						//if(b.colStatus == 'VALID' || b.colStatus == 'SETTLED' ||  b.colStatus == 'WAITING FOR PREMATCH APPROVAL'){
						if(b.colStatus == 'VALID'  && b.colCurrency =='IDR'){
							rownum = table.fnAddData(["<input type='checkbox'/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDrCr, b.colStatus, b.colSenderBranchCode
							                          , b.colSenderBankCode, (b.colSenderAccNo == undefined)? null : b.colSenderAccNo, (b.colSenderAccName == undefined)? null : b.colSenderAccName, b.colSenderCurrency
							                          , b.colBenefBank, b.colBenefBankName, b.colBenefCurrency, (b.colBenefAccNo == undefined)? null : b.colBenefAccNo
							                          , b.colBenefAccName, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
						} else {
							rownum = table.fnAddData(["<input type='checkbox' disabled/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDrCr, b.colStatus, b.colSenderBranchCode
							                          , b.colSenderBankCode, (b.colSenderAccNo == undefined)? null : b.colSenderAccNo, (b.colSenderAccName == undefined)? null : b.colSenderAccName, b.colSenderCurrency
							                          , b.colBenefBank, b.colBenefBankName, (b.colBenefAccNo == undefined)? null : b.colBenefAccNo
							                          , b.colBenefAccName, b.colBenefCurrency, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
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
		
		app.btnSaveKsei.click(function(){
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
			} else {
				dataTable.fnClearTable();
				var totalCredit = 0;
				var totalDebit = 0;
				var totCharges = 0;
				var bankCharge = 0;
				var no = 0;
				for (var x in datas) {
					var b = datas[x];
					b.colNo = ++no;
					//b.colNo = ++x;
					var rownum = dataTable.fnAddData(["<input type='checkbox'/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDrCr, b.colStatus, b.colSenderBranchCode
					                                  		  , b.colSenderBankCode, (b.colSenderAccNo == undefined)? null : b.colSenderAccNo, (b.colSenderAccName == undefined)? null : b.colSenderAccName, b.colSenderCurrency
									                          , b.colBenefBank, b.colBenefBankName, (b.colBenefAccNo == undefined)? null : b.colBenefAccNo
									                          , b.colBenefAccName, b.colBenefCurrency, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
		    		var ptr = dataTable.fnSettings().aoData[rownum].nTr;
		    		ptr.childNodes[7].style.textAlign = 'right';
		    		ptr.childNodes[20].style.textAlign = 'right';
		    		ptr.childNodes[21].style.textAlign = 'right';
		    		$(ptr).data("bean", b);

		    		if (b.colDrCr == 'CR') totalCredit += parseFloat(replaceAllComma(b.colAmount, ',',''));
		    		if (b.colDrCr == 'DR') totalDebit += parseFloat(replaceAllComma(b.colAmount, ',',''));
		    		totCharges += parseFloat(replaceAllComma(b.colChargeAmount, ',',''));
		    		bankCharge = b.colBankCharges;
				}
				dataTable.fnDraw();

				$("input[type='checkbox']", app.tblFundTransferKsei).attr('checked', 'checked').attr('disabled', 'disabled');
				app.btnSaveKsei.hide();
				app.btnConfirmKsei.show();

				$().add(app.checkAsOfKseiDate).add(app.asOfDateKsei).add(app.checkKseiFromTo).add(app.kseiDateFrom).add(app.kseiDateTo)
				.add(app.accountNoKsei).add(app.checkDefaultKsei).add(app.checkTransactionKsei).add(app.transType).add(app.securityType).add(app.transferType).add(app.currency).attr('disabled', 'disabled');

				$().add(app.accountNoKseiHelp).add(app.btnPopulateKsei).button("disable");

				app.totalKsei.show();

				app.totAmtDrKsei.autoNumericSet(totalDebit);
				app.totAmtCrKsei.autoNumericSet(totalCredit);
				if(paramScreen.transactionType == "RVP"){
					app.totCharges.autoNumericSet(bankCharge);
				}else{
					app.totCharges.autoNumericSet(totCharges);
				}
			}
		});

		app.btnConfirmKsei.click(function(){
			//loading.dialog('open');

			var param = [];

			$(dataTable.fnGetNodes()).each(function(i, e){
				var bean = $(this).data("bean").fundTransferDetail;
				bean.noSeq = $(this).data("bean").colNo;
				param[param.length] = bean;
			});
			
			$().submitAsync("@{saveFundTransfer()}", {'paramDetail':param, 'paramScreen':paramScreen, 'processType':'FUND_TRANSFER_PROCESS-2'}, function(respone) {
				loading.dialog('close');

				if(respone.status == 'SUCCESS') app.fundBatchNoKsei.val(respone.batchId);

				//loading.dialog('close');
				var closeDialog = function(){
					$("#dialog-message").dialog("close");
					location = "@{FundTransfer.list()}";
				};

				app.btnConfirmKsei.hide();
				messageAlertOk(respone.msg, "ui-icon ui-icon-success", "Information", closeDialog);

			});
		}).hide();

		app.btnResetKsei.click(function(){
			$().add(app.checkAsOfKseiDate).add(app.asOfDateKsei).add(app.checkKseiFromTo).add(app.kseiDateFrom).add(app.kseiDateTo)
			.add(app.accountNoKsei).add(app.checkDefaultKsei).add(app.checkTransactionKsei).add(app.transType).add(app.securityType).add(app.transferType).add(app.currency).removeAttr('disabled');

			$().add(app.accountNoKseiHelp).add(app.btnPopulateKsei).button("enable");

			app.checkAsOfKseiDate.click();

			app.accountNoKseiKey.val("");
			app.accountNoKsei.val("");
			app.accountNoKseiDesc.val("");
			app.checkDefaultKsei.click();
			app.totAmtDrKsei.val("");
			app.totAmtCrKsei.val("");
			app.totCharges.val("");
			app.fundBatchNoKsei.val("");
			app.transType.val("");
			app.securityType.val("");
			app.securityType.blur();
			app.transferType.val("");
			app.currency.val("");
			app.currency.blur();

			app.totalKsei.hide();

			app.kseiDateFromError.html("");
			app.kseiDateToError.html("");
			app.asOfDateKsei.removeClass('fieldError');
			app.kseiDateFrom.removeClass('fieldError');
			app.kseiDateTo.removeClass('fieldError');
			app.accountNoKsei.removeClass('fieldError');
			$("#checkAllKsei", html).removeAttr('checked').removeAttr('disabled');

			dataTable.fnClearTable();

			app.btnSaveKsei.show();
			app.btnConfirmKsei.hide();
		});

		Date.prototype.ddmmyyyy = function() {
			var mm = this.getMonth() + 1;
			var dd = this.getDate();
			return [(dd > 9 ? '' : '0') + dd,'/', (mm > 9 ? '' : '0') + mm,'/', this.getFullYear()].join('');
		};

	} else {
		return new SettlementKsei(html);
	}
}