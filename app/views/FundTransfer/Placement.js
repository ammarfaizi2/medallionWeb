function Placement(html) {
	if (this instanceof Placement) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject2(this, false);
		
		$("[type='button']", html).button();
		
		$('.calendar', html).datepicker();
		
		app.total.hide();
		
		var dataTable = app.tblFundTransferPlacement.dataTable({
    		"bJQueryUI": true,
	        "bLengthChange": true,
	        "bDestroy":true,
			"sPaginationType": "full_numbers",
	        "bSort": false,
			"bScrollCollapse": true
    	});

		app.tblFundTransferPlacement.wrap("<div style='overflow:scroll'>");
		
		app.totalAmount.autoNumeric({vMax: '99999999999999999999.99', dGroup: '3'});
		
/*==================================================================
 * Function
 *==================================================================*/
		var paramScreen ;
		
		function validate() {
			$().add(app.asOfDateErr).add(app.fromToErr).html("");
			
			if (app.checkAsOfDate.is(':checked')) {
				if (app.asOfDate.val() == '') { 
					app.asOfDateErr.html("As Of Date is required")
					return false;
				}
			}
			if (app.checkFromTo.is(':checked')) {
				if (app.fromDate.val() == '' || app.toDate.val() == '') { 
					app.fromToErr.html("From Date and To Date is required")
					return false;
				}
				
				var from = app.fromDate.val().split("/");
				var to = app.toDate.val().split("/");
				var fromDatePl = new Date(from[2], from[1] - 1 , from[0]);
				var toDatePl = new Date(to[2], to[1] - 1, to[0]);
				
				if (fromDatePl.getTime() > toDatePl.getTime()) {
					app.fromToErr.html("Date From must be less or equal than Date To");
					app.fromDate.addClass('fieldError');
					return false;
				}else {
					app.fromDate.removeClass('fieldError');
				}
				
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
		app.accountNoPl.dynapopup2({key:'accountNoPlKey', help:'accountNoPlHelp', desc:'accountNoPlDesc'}, 'PICK_CS_ACCOUNT_FUND_TRANSFER', app.accountNoPl.val(), 'actionCode', function(data){
			if (data) {
				$('#accountNoPl').removeClass('fieldError');				
				$('#accountNoPl').val(data.accountNo);
				$('#accountNoPlKey').val(data.code);
				$('#accountNoPlDesc').val(data.description);
				$('#h_accountNoPlDesc').val(data.description);
				
			}
		},function(data){
			$('#accountNoPl').addClass('fieldError');
			$('#accountNoPlDesc').val('');
			$('#accountNoPlKey').val('');
			$('#h_accountNoPlDesc').val('');
			$('#accountNoPl').val('');			
		});
		
		$("#checkAll", html).click(function(){
			var isChecked = $(this).is(':checked');
			$(dataTable.fnGetNodes()).each(function(i, e){
				var inputChk = $("input", $("td", $(this)).eq(0));
				if (isChecked) { inputChk.attr("checked", "checked");
				}else{ inputChk.removeAttr("checked"); }
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
		})
		
		app.checkAsOfDate.click(function(){
			app.asOfDate.removeAttr("disabled").val(app.applicationDate.val());
			$().add(app.fromDate).add(app.toDate).val("").attr("disabled", "disabled")
			$().add(app.asOfDateErr).add(app.fromToErr).html("");
		}).click();
		
		app.checkFromTo.click(function(){
			app.asOfDate.val("").attr("disabled", "disabled");
			$().add(app.fromDate).add(app.toDate).removeAttr("disabled", "disabled").val(app.applicationDate.val());
			$().add(app.asOfDateErr).add(app.fromToErr).html("");
		});
		
		app.btnPopulatePa.click(function(){
			if (validate()) {		
				loading.dialog('open');
				
				var param = {
					"asOfFlag" : app.checkAsOfDate.is(':checked'),
					"accountNo" : app.accountNoPl.val(),
					"processModeDef" : "0",
					"transactionType" : "",
					"securityType" : "",
					"transferType" : "",
					"currency" :  "",
					"caNo" : "",
					"fundCode" : ""
				};
				
				
				if (param.asOfFlag) {
					param.strAsOfDate = app.asOfDate.val();
				}else{
					param.strFromDate = app.fromDate.val(); 
					param.strToDate = app.toDate.val();
				}
				
				paramScreen = param;
				
				$().fetchSync("@{getDepositoPlacementPopulate()}", {'param':param}, function(datas) {
					loading.dialog('close');

					checkRedirect(datas);
					$("#checkAll", html).removeAttr('checked')
					
					var table = app.tblFundTransferPlacement.dataTable();
					table.fnClearTable();
					for (var x in datas) {
						var b = datas[x];
						if(b.colStatus == 'VALID' && b.colCurrency =='IDR'){
							var rownum = table.fnAddData(["<input type='checkbox'/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colSenderAccNo, b.colSenderAccName, b.colBenefAccNo, b.colBenefBank, b.colBenefAccName, b.colType, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
						}else{
							var rownum = table.fnAddData(["<input type='checkbox' disabled/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colSenderAccNo, b.colSenderAccName, b.colBenefAccNo, b.colBenefBank, b.colBenefAccName, b.colType, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
						}
						
			    		var ptr = table.fnSettings().aoData[rownum].nTr;
			    		ptr.childNodes[6].style.textAlign = 'right';
			    		
			    		$(ptr).data("bean", b);
					}
					
					table.fnDraw();
				});
				
				loading.dialog('close');
			}			
		});

		$("#checkAll").hide();//di hide bsm hanya pilih satu deposito
		var closeDialog = function(){
			$("#dialog-message").dialog("close");
		};
		
		app.btnSave.click(function(){
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
			
			if (datas.length == 0 || datas.length >1) {
				//messageAlertOk("Please choose data, minimal 1 row", "ui-icon ui-icon-notice", "Warning", closeDialog);
				messageAlertOk("Please choose 1 row", "ui-icon ui-icon-notice", "Warning", closeDialog);
			}else{
				dataTable.fnClearTable();
				var totalAmount = 0;
				var reset = true;
				var no = 0;
				for (var x in datas) {
					var b = datas[x];
					//if(!b.fundTransferDetail.premium && reset){ reset=false; no=0; }
					b.colNo = ++no;
					var rownum = dataTable.fnAddData(["<input type='checkbox'/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colSenderAccNo, b.colSenderAccName, b.colBenefAccNo, b.colBenefBank, b.colBenefAccName, b.colType, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
		    		var ptr = dataTable.fnSettings().aoData[rownum].nTr;
		    		ptr.childNodes[6].style.textAlign = 'right';
		    		$(ptr).data("bean", b);
		    		
		    		//totalAmount += parseFloat(replaceAllComma(b.colAmount, ',',''));
		    		totalAmount += parseFloat(replaceAllComma(b.colNetAmount, ',',''));
				}
				dataTable.fnDraw();
				
				$("input[type='checkbox']", app.tblFundTransferPlacement).attr('checked', 'checked').attr('disabled', 'disabled')
				app.btnSave.hide();
				app.btnConfirm.show();
				
				$().add(app.checkAsOfDate).add(app.asOfDate).add(app.checkFromTo).add(app.fromDate).add(app.toDate)
				.add(app.accountNoPl).attr('disabled', 'disabled');
				
				$().add(app.accountNoPlHelp).add(app.btnPopulatePa).button("disable");
				
				app.total.show();
				
				app.totalAmount.autoNumericSet(totalAmount);
			}
		});
		
		app.btnConfirm.click(function(){
			loading.dialog('open');
			
			var param = [];

			$(dataTable.fnGetNodes()).each(function(i, e){
				var bean = $(this).data("bean").fundTransferDetail;
				bean.noSeq = $(this).data("bean").colNo;
				param[param.length] = bean;
			});
			$().submitAsync("@{saveFundTransfer()}", {'paramDetail':param, 'paramScreen':paramScreen, 'processType':'FUND_TRANSFER_PROCESS-1'}, function(respone) {
				loading.dialog('close');
				
				if (respone.status == 'SUCCESS') {
					app.fundBatchNoDep.val(respone.batchId);
				}
				
				loading.dialog('close');	
				
				var closeDialog = function(){
					$("#dialog-message").dialog("close");
					location = "@{FundTransfer.list()}";
				};
				
				app.btnConfirm.hide();
				messageAlertOk(respone.msg, "ui-icon ui-icon-success", "Information", closeDialog);
			});
		}).hide();
		
		app.btnReset.click(function(){
			$().add(app.checkAsOfDate).add(app.asOfDate).add(app.checkFromTo).add(app.fromDate).add(app.toDate)
			.add(app.accountNoPl).removeAttr('disabled');
			
			$().add(app.accountNoPlHelp).add(app.btnPopulatePa).button("enable");
			
			app.checkAsOfDate.click();
			
			app.accountNoPlKey.val("");
			app.accountNoPl.val("");
			app.accountNoPlDesc.val("");
			app.totalAmount.val("");
			app.fundBatchNoDep.val("");
			
			app.total.hide();
			
			app.asOfDate.removeClass('fieldError');
			app.fromDate.removeClass('fieldError');
			app.toDate.removeClass('fieldError');
			
			$("#checkAll", html).removeAttr('checked').removeAttr('disabled');
			
			dataTable.fnClearTable();
			
			app.btnSave.show();
			app.btnConfirm.hide();
		});
		
		Date.prototype.ddmmyyyy = function() {
			var mm = this.getMonth() + 1;
			var dd = this.getDate();
			
			return [(dd > 9 ? '' : '0') + dd,'/', (mm > 9 ? '' : '0') + mm,'/', this.getFullYear()  ].join('');
		};
		
		
	}else{
		return new Placement(html);
	}
}