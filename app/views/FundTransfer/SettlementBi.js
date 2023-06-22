function SettlementBI(html) {
	if (this instanceof SettlementBI) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject2(this, false);
		
		$("[type='button']", html).button();
		
		$('.calendar', html).datepicker();
		
		app.totalBI.hide();
		
		var dataTable = app.tblFundTransferBI.dataTable({
    		"bJQueryUI": true,
	        "bLengthChange": true,
	        "bDestroy":true,
			"sPaginationType": "full_numbers",
	        "bSort": false,
			"bScrollCollapse": true
    	});

		app.tblFundTransferBI.wrap("<div style='overflow:scroll'>");
		app.totAmtDrBI.autoNumeric({vMax: '99999999999999999999.99', dGroup: '${digitBI}'});
		app.totAmtCrBI.autoNumeric({vMax: '99999999999999999999.99', dGroup: '${digitBI}'});
		app.totCharges.autoNumeric({vMax: '99999999999999999999.99', dGroup: '${digitBI}'});
		
/*==================================================================
 * Function
 *==================================================================*/
		
		function validate() {
			$().add(app.asOfDateBIErr).add(app.BIDateToError).html("");
			var gosave = true;
			app.asOfDateBIErr.html("");
			app.BIDateToError.html("");
			app.transTypeBIError.html("");
			app.securityTypeBIError.html("");
			app.transferTypeBIError.html("");
			app.currencyBIError.html("");
			
			if (app.checkAsOfBIDate.is(':checked')) {
				if (app.asOfDateBI.val() == '') { 
					app.asOfDateBIErr.html("As Of Date is required");
					gosave = false;
				}
			}

			if (app.checkBIFromTo.is(':checked')) {
				if (app.BIDateFrom.val() == '' || app.BIDateTo.val() == '') { 
					app.BIDateToError.html("From Date and To Date is required");
					gosave = false;
				}
			}

			if ((app.BIDateFrom.val() != '') && (app.BIDateTo.val() != '')) {
				var dateFrom = app.BIDateFrom.datepicker('getDate');
				var dateTo = app.BIDateTo.datepicker('getDate');
				var origin = 'from';
				var id = '#BIDate';

				compareDateFromToEqual(dateFrom, dateTo, origin, id);
				if(app.BIDateFrom.hasClass('fieldError') || app.BIDateTo.hasClass('fieldError')){
					gosave = false;
				}
			}
			
			if(app.transTypeBI.val() == ""){
				app.transTypeBIError.html("Transaction type is required");
				gosave = false;
			}
			
			/*if(app.securityTypeBI.val() == ""){
				app.securityTypeBIError.html("Security type is required");
				gosave = false;
			}*/
			
			if(app.transferTypeBI.val() == ""){
				app.transferTypeBIError.html("Transfer type is required");
				gosave = false;
			}
			
			if(app.currencyBI.val() == ""){
				app.currencyBIError.html("Currency is required");
				gosave = false;
			}

			return gosave;
		}
		
		//$("#transferTypeBI").find('option').remove();
		$("#transferTypeBI").children().eq(0).remove();
		$("#transTypeBI").children().eq(0).remove();
		$("#transTypeBI").change(function(){
			$("#transferTypeBI").find('option').remove();
			if($(this).val() == "RVP"){
				$("#transferTypeBI").find('option').remove();
				$('#transferTypeBI').append('<option value="ALL">ALL</option>').val('ALL');
			}
			$('#transferTypeBI').append('<option value="TRANSFER_TYPE-POOL">POOL</option>').val('ALL');
			$('#transferTypeBI').append('<option value="TRANSFER_TYPE-MANUAL">MANUAL TO POOL</option>').val('ALL');
		});
		$("#transTypeBI").trigger('change');
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
		app.accountNoBI.dynapopup2({key:'accountNoBIKey', help:'accountNoBIHelp', desc:'accountNoBIDesc'}, 'PICK_CS_ACCOUNT_FUND_TRANSFER', app.accountNoBI.val(), 'actionCode', function(data){
			if (data) {
				console.log(data)
				$('#accountNoBI').removeClass('fieldError');				
				$('#accountNoBI').val(data.accountNo);
				$('#accountNoBIKey').val(data.code);
				$('#accountNoBIDesc').val(data.description);
				$('#h_accountNoBIDesc').val(data.description);
				
			}
		},function(data){
			$('#accountNoBI').addClass('fieldError');
			$('#accountNoBIDesc').val('');
			$('#accountNoBIKey').val('');
			$('#h_accountNoBIDesc').val('');
			$('#accountNoBI').val('');	
		});
		
		$('#securityTypeBI').lookup({
			list:'@{Pick.securityTypes()}',
			get:{
				url:'@{Pick.securityType()}',
				success: function(data){
					$('#securityTypeBI').removeClass('fieldError');
					$('#securityTypeBIId').val(data.code);
					$('#securityTypeBIName').val(data.description);
					$('#h_securityTypeBIName').val(data.description);
					$('#securityTypeBI').change();
 				},
				error: function(data){
					$('#securityTypeBI').addClass('fieldError');
					$('#securityTypeBI').val('');
					$('#securityTypeBIId').val('');
					$('#securityTypeBIName').val('NOT FOUND');
					$('#h_securityTypeBIName').val('');
					$('#securityTypeBI').change();
				}
			},
			description:$('#securityTypeBIName'),
			help:$('#securityTypeBIHelp'),
			nextControl:$('')
		});
		
		$('#currencyBI').lookup({
			list:'@{Pick.currencies()}',
			get:{
				url:'@{Pick.currency()}',
				success: function(data) {
					if (data) {
						$('#currencyBI').removeClass('fieldError');
						$('#currencyBI').val(data.code);
						$('#currencyBIDesc').val(data.description);
						$('#h_currencyBIDesc').val(data.description);
					}
				},
				error : function(data) {
					$('#currencyBI').addClass('fieldError');
					$('#currencyBIDesc').val('NOT FOUND');
					$('#currencyBI').val('');
					$('#h_currencyBIDesc').val('');
				}
			},
			key:$('#currencyBI'),
			description:$('#currencyBIDesc'),
			help:$('#currencyBIHelp')
		});

		$("#checkAllBI", html).click(function(){
			var isChecked = $(this).is(':checked');
			$('#tblFundTransferBi tbody tr').each(function(){
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

		app.checkAsOfBIDate.click(function(){
			app.BIDateFrom.removeClass('fieldError');
			app.BIDateTo.removeClass('fieldError');
			app.asOfDateBI.removeAttr("disabled").val(app.applicationDateBI.val());
			$().add(app.BIDateFrom).add(app.BIDateTo).val("").attr("disabled", "disabled");
			$().add(app.asOfDateBIErr).add(app.BIDateToError).html("");
		}).click();

		app.checkBIFromTo.click(function(){
			app.asOfDateBI.removeClass('fieldError');
			app.asOfDateBI.val("").attr("disabled", "disabled");
			$().add(app.BIDateFrom).add(app.BIDateTo).removeAttr("disabled", "disabled").val(app.applicationDateBI.val());
			$().add(app.asOfDateBIErr).add(app.BIDateToError).html("");
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
			
		app.btnPopulateBI.click(function(){
			if (validate()) {		
				loading.dialog('open');
				
				var param = {
					"asOfFlag" : app.checkAsOfBIDate.is(':checked'),
					"accountNo" : app.accountNoBI.val(),
					"processModeDef" : app.checkDefaultBI.is(':checked'),
					"transactionType" : app.transTypeBI.val(),
					"securityType" : app.securityTypeBI.val(),
					"transferType" : app.transferTypeBI.val(),
					"currency" :  app.currencyBI.val(),
					"caNo" : "",
					"fundCode" : ""
				};

				if (param.asOfFlag) {
					param.strAsOfDate = app.asOfDateBI.val();
				} else {
					param.strFromDate = app.BIDateFrom.val(); 
					param.strToDate = app.BIDateTo.val();
				}
				
				paramScreen = param;
				showTotal();
				$().fetchSync("@{getSettlementBIPopulate()}", {'param':param}, function(datas) {
					loading.dialog('close');
					$("#checkAllBI", html).removeAttr('checked');

					var table = app.tblFundTransferBI.dataTable();
					table.fnClearTable();
					for (var x in datas) {
						var b = datas[x];
						var rownum;
						console.log(b)
						//if(b.colStatus == 'VALID' || b.colStatus == 'SETTLED' ||  b.colStatus == 'WAITING FOR PREMATCH APPROVAL'){
						if(b.colStatus == 'VALID'  && b.colCurrency =='IDR'){
							rownum = table.fnAddData(["<input type='checkbox'/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDrCr, b.colStatus, b.colSenderBranchCode
							                          , b.colSenderBankCode, (b.colSenderAccNo == undefined)? null : b.colSenderAccNo, (b.colSenderAccName == undefined)? null : b.colSenderAccName
							                          , b.colCurrency, b.colBenefBank, b.colBenefBankName, (b.colBenefAccNo == undefined)? null : b.colBenefAccNo
							                          , b.colBenefAccName, b.colCurrency, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
						} else {
							rownum = table.fnAddData(["<input type='checkbox' disabled/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDrCr, b.colStatus, b.colSenderBranchCode
							                          , b.colSenderBankCode, (b.colSenderAccNo == undefined)? null : b.colSenderAccNo, (b.colSenderAccName == undefined)? null : b.colSenderAccName
							                          , b.colCurrency, b.colBenefBank, b.colBenefBankName, (b.colBenefAccNo == undefined)? null : b.colBenefAccNo
							                          , b.colBenefAccName, b.colCurrency, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
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
		
		app.btnSaveBI.click(function(){
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
				var no = 0;
				for (var x in datas) {
					var b = datas[x];
					b.colNo = ++no;
					//b.colNo = ++x;
					var rownum = dataTable.fnAddData(["<input type='checkbox'/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDrCr, b.colStatus, b.colSenderBranchCode
					                                  , b.colSenderBankCode, (b.colSenderAccNo == undefined)? null : b.colSenderAccNo, (b.colSenderAccName == undefined)? null : b.colSenderAccName
									                          , b.colCurrency, b.colBenefBank, b.colBenefBankName, (b.colBenefAccNo == undefined)? null : b.colBenefAccNo
									                          , b.colBenefAccName, b.colCurrency, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
		    		var ptr = dataTable.fnSettings().aoData[rownum].nTr;
		    		ptr.childNodes[7].style.textAlign = 'right';
		    		ptr.childNodes[20].style.textAlign = 'right';
		    		ptr.childNodes[21].style.textAlign = 'right';
		    		$(ptr).data("bean", b);

		    		//if (b.colDrCr == 'CR') totalCredit += parseFloat(replaceAllComma(b.colAmount, ',',''));
		    		//if (b.colDrCr == 'DR') totalDebit += parseFloat(replaceAllComma(b.colAmount, ',',''));
		    		totalDebit += parseFloat(replaceAllComma(b.colAmount, ',',''));
		    		totCharges += parseFloat(replaceAllComma(b.colChargeAmount, ',',''));
		    		bankCharge = b.colBankCharges;
				}
				dataTable.fnDraw();

				$("input[type='checkbox']", app.tblFundTransferBI).attr('checked', 'checked').attr('disabled', 'disabled');
				app.btnSaveBI.hide();
				app.btnConfirmBI.show();

				$().add(app.checkAsOfBIDate).add(app.asOfDateBI).add(app.checkBIFromTo).add(app.BIDateFrom).add(app.BIDateTo)
				.add(app.accountNoBI).add(app.checkDefaultBI).add(app.checkTransactionBI).add(app.transTypeBI).add(app.securityTypeBI).add(app.transferTypeBI).add(app.currencyBI).attr('disabled', 'disabled');

				$().add(app.accountNoBIHelp).add(app.btnPopulateBI).button("disable");

				app.totalBI.show();

				app.totAmtDrBI.autoNumericSet(totalDebit);
				app.totAmtCrBI.autoNumericSet(totalCredit);
				if($("#transTypeBI").val() == "RVP"){
					app.totCharges.autoNumericSet(bankCharge);					
				}else{
					app.totCharges.autoNumericSet(totCharges);		
				}
			}
		});

		app.btnConfirmBI.click(function(){
			//loading.dialog('open');

			var param = [];

			$(dataTable.fnGetNodes()).each(function(i, e){
				var bean = $(this).data("bean").fundTransferDetail;
				bean.noSeq = $(this).data("bean").colNo;
				param[param.length] = bean;
			});
			
			$().submitAsync("@{saveFundTransfer()}", {'paramDetail':param, 'paramScreen':paramScreen, 'processType':'FUND_TRANSFER_PROCESS-3'}, function(respone) {
				loading.dialog('close');

				if(respone.status == 'SUCCESS') app.fundBatchNoBI.val(respone.batchId);

				//loading.dialog('close');
				var closeDialog = function(){
					$("#dialog-message").dialog("close");
					location = "@{FundTransfer.list()}";
				};

				app.btnConfirmBI.hide();
				messageAlertOk(respone.msg, "ui-icon ui-icon-success", "Information", closeDialog);

			});
		}).hide();

		app.btnResetBI.click(function(){
			$().add(app.checkAsOfBIDate).add(app.asOfDateBI).add(app.checkBIFromTo).add(app.BIDateFrom).add(app.BIDateTo)
			.add(app.accountNoBI).add(app.checkDefaultBI).add(app.checkTransactionBI).add(app.transTypeBI).add(app.securityTypeBI).add(app.transferTypeBI).add(app.currencyBI).removeAttr('disabled');

			$().add(app.accountNoBIHelp).add(app.btnPopulateBI).button("enable");

			app.checkAsOfBIDate.click();

			app.accountNoBIKey.val("");
			app.accountNoBI.val("");
			app.accountNoBIDesc.val("");
			app.checkDefaultBI.click();
			app.totAmtDrBI.val("");
			app.totAmtCrBI.val("");
			app.totCharges.val("");
			app.fundBatchNoBI.val("");
			app.transTypeBI.val("");
			app.securityTypeBI.val("");
			app.securityTypeBI.blur();
			app.transferTypeBI.val("");
			app.currencyBI.val("");
			app.currencyBI.blur();

			app.totalBI.hide();

			app.BIDateFromError.html("");
			app.BIDateToError.html("");
			app.asOfDateBI.removeClass('fieldError');
			app.BIDateFrom.removeClass('fieldError');
			app.BIDateTo.removeClass('fieldError');
			app.accountNoBI.removeClass('fieldError');
			$("#checkAllBI", html).removeAttr('checked').removeAttr('disabled');

			dataTable.fnClearTable();

			app.btnSaveBI.show();
			app.btnConfirmBI.hide();
		});

		Date.prototype.ddmmyyyy = function() {
			var mm = this.getMonth() + 1;
			var dd = this.getDate();
			return [(dd > 9 ? '' : '0') + dd,'/', (mm > 9 ? '' : '0') + mm,'/', this.getFullYear()].join('');
		};

	} else {
		return new SettlementBI(html);
	}
}