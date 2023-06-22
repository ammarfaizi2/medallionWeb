function CaDist(html) {
	if (this instanceof CaDist) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject2(this, false);
		
		var depBI = 'BI';
		var depKSEI = 'KSEI';
		
		$("[type='button']", html).button();
		
		$('.calendar', html).datepicker();
		
		app.total.hide();
		
		var dataTable = app.tblFundTransferCADist.dataTable({
    		"bJQueryUI": true,
	        "bLengthChange": true,
	        "bDestroy":true,
			"sPaginationType": "full_numbers",
	        "bSort": false,
			"bScrollCollapse": true
    	});

		app.tblFundTransferCADist.wrap("<div style='overflow:scroll'>");
		
		app.totalAmountKSEI.autoNumeric({vMax: '99999999999999999999999999999.99', dGroup: '3'});
		app.totalAmountBI.autoNumeric({vMax: '99999999999999999999999999999.99', dGroup: '3'});
		//app.totalAmountEuro.autoNumeric({vMax: '99999999999999999999999999999.99', dGroup: '3'});

/*==================================================================
 * Function
 *==================================================================*/
		
		var paramScreen;
		
		function validate() {
			$().add(app.asOfDateErr).add(app.fromToErr).html("");
			
			if (app.checkAsOfDate.is(':checked')) {
				if (app.asOfDateCA.val() == '') { 
					app.asOfDateErr.html("As Of Date is required");
					return false;
				}
			}
			if (app.checkFromTo.is(':checked')) {
				if (app.fromDateCA.val() == '' || app.toDateCA.val() == '') { 
					app.fromToErr.html("From Date and To Date is required");
					return false;
				}
				
				var from = app.fromDateCA.val().split("/");
				var to = app.toDateCA.val().split("/");
				var fromDateCADate = new Date(from[2], from[1] - 1 , from[0]);
				var toDateCADate = new Date(to[2], to[1] - 1, to[0]);
//				console.log("from date ca: "+fromDateCADate);
//				console.log("to date ca: "+toDateCADate);
				if (fromDateCADate.getTime() > toDateCADate.getTime()) {
					app.fromToErr.html("Date From must be less or equal than Date To");
					app.fromDateCA.addClass('fieldError');
					return false;
				}else {
					app.fromDateCA.removeClass('fieldError');
				}
			}
			
			return true;
		}
		
		
		function replaceAllComma(text, from, to) {
			for (var i = 0; i < 100; i++) {
				text = text.replace(from, to);
			}
			return text;
		}

/*================================================================== 
 * Event
 *================================================================== */
		$("#checkAll", html).click(function(){
			var isChecked = $(this).is(':checked');
			$(dataTable.fnGetNodes()).each(function(i, e){
				var inputChk = $("input", $("td", $(this)).eq(0));
				if (isChecked) { 
					inputChk.attr("checked", "checked");
				}else{ 
					inputChk.removeAttr("checked"); 
				}
			});
			
			$(dataTable.fnGetNodes()).each(function(i, e){
				var b = $(this).data("bean");
				var inputChk = $("input", $("td", $(this)).eq(0));
				if (isChecked) {
					if(b.colStatus == 'VALID' ){
						//inputChk.attr("checked", "checked");
					}else{
						inputChk.removeAttr("checked"); 
					}
				} else {
					inputChk.removeAttr("checked"); 
				}
			});
		});
		
		app.checkAsOfDate.click(function(){
			app.asOfDateCA.removeAttr("disabled").val(app.applicationDate.val());
			$().add(app.fromDateCA).add(app.toDateCA).val("").attr("disabled", "disabled");
			$().add(app.asOfDateErr).add(app.fromToErr).html("");
			
		}).click();
		
		app.checkFromTo.click(function(){
			app.asOfDateCA.val("").attr("disabled", "disabled");
			$().add(app.fromDateCA).add(app.toDateCA).removeAttr("disabled", "disabled").val(app.applicationDate.val());
			$().add(app.asOfDateErr).add(app.fromToErr).html("");
		});
		
		app.fromDateCA.change(function(){
			$("#fromDateCA").removeClass("fieldError");
			$("#toDateCA").removeClass("fieldError");
			app.fromToErr.html("");
		});
		
		app.toDateCA.change(function(){
			$("#fromDateCA").removeClass("fieldError");
			$("#toDateCA").removeClass("fieldError");
			app.fromToErr.html("");
		});
		
		//ANNOUNCEMENT NO
		app.announcementNo.lookup({
			list:'@{Pick.announcements()}',
			get:{
				url:'@{Pick.announcement()}',
				success: function(data) {
					if (data) {
						$('#announcementNo').removeClass('fieldError');
						$('#announcementNo').val(data.code);
						$('#announcementNoKey').val(data.key);
						$('#announcementNoDesc').val(data.description);
						$('#h_announcementNoDesc').val(data.description);
					}
				},
				error : function(data) {
					$('#announcementNo').addClass('fieldError');
					$('#announcementNoDesc').val('NOT FOUND');
					$('#announcementNoKey').val('');
					$('#announcementNo').val('');
					$('#h_announcementNoDesc').val('');
				}
			},
			key:$('#announcementNo'),
			description:$('#announcementNoDesc'),
			help:$('#announcementNoHelp')
		});
		
		
		app.btnPopulate.click(function(){
		
			if (validate()) {
				loading.dialog('open');
				var param = {
					"asOfFlag" : app.checkAsOfDate.is(':checked'),
					"caNo" : app.announcementNo.val(),
					"accountNo" : "",
					"processModeDef" : "0",
					"transactionType" : "",
					"securityType" : "",
					"transferType" : "",
					"currency" :  "",
					"fundCode" : ""
				};
				
				paramScreen = param;
				console.log(param);

				if (param.asOfFlag) {
					param.strAsOfDate = app.asOfDateCA.val();
				}else{
					param.strFromDate = app.fromDateCA.val(); 
					param.strToDate = app.toDateCA.val();
				}
				
				var datatable = $().fetchSync("@{getCASettlementPopulate()}", {'param':param}, function(datas) {
					loading.dialog('close');
					$("#checkAll", html).removeAttr('checked');
					
					var table = app.tblFundTransferCADist.dataTable();
					table.fnClearTable();
					for (var x in datas) {
						var b = datas[x];
						if(b.colStatus == 'VALID' && b.colCurrency == 'IDR'){
							var rownum = table.fnAddData(["<input type='checkbox'/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDepository, b.colDescription, b.colSenderAccNo, b.colSenderAccName, b.colBenefAccNo, b.colBenefBank, b.colBenefAccName, b.colType, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
						}else{
							var rownum = table.fnAddData(["<input type='checkbox' disabled/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDepository, b.colDescription, b.colSenderAccNo, b.colSenderAccName, b.colBenefAccNo, b.colBenefBank, b.colBenefAccName, b.colType, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
						}
						var ptr = table.fnSettings().aoData[rownum].nTr;
			    		ptr.childNodes[7].style.textAlign = 'right';
			    		$(ptr).data("bean", b);
					}
					
					table.fnDraw();
					checkRedirect(datas);
				});
			}			
		});
		
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

			if (datas.length == 0) {
				var closeDialog = function(){
					$("#dialog-message").dialog("close");
				};
				
				messageAlertOk("Please choose data, minimal 1 row", "ui-icon ui-icon-notice", "Warning", closeDialog);
			}else{
				dataTable.fnClearTable();
				var totalAmountBI = 0;
				var totalAmountKSEI = 0;
				var colNumberBI = 0;
				var colNumberKSEI = 0;
				var totalAmountEuro = 0;
				for (var x in datas) {
					var b = datas[x];
					//b.colNo = ++x;
					//reset colNo tiap kali ganti depository
					if (b.colDepository == 'BI') {
						++colNumberBI;
						b.colNo = colNumberBI;
					}else {
						++colNumberKSEI;
						b.colNo = colNumberKSEI;
					}
					
					b.fundTransferDetail.noSeq = b.colNo;
					var rownum = dataTable.fnAddData(["<input type='checkbox'/>", b.colNo, b.colDate, b.colAccountNo, b.colSecurity, b.colCurrency, b.colTransactionNo, b.colAmount, b.colDepository, b.colDescription, b.colSenderAccNo, b.colSenderAccName, b.colBenefAccNo, b.colBenefBank, b.colBenefAccName, b.colType, b.colChargeAmount, b.colNetAmount, b.colPaymentTypeDesc], false);
					var ptr = dataTable.fnSettings().aoData[rownum].nTr;
					ptr.childNodes[7].style.textAlign = 'right';
					$(ptr).data("bean", b);
					if (b.colDepository == 'KSEI') {
						totalAmountKSEI += parseFloat(replaceAllComma(b.colAmount, ',',''));
					}else if (b.colDepository == 'EUROCLEAR') {
						totalAmountEuro += parseFloat(replaceAllComma(b.colAmount, ',',''));
					}else {
						totalAmountBI += parseFloat(replaceAllComma(b.colAmount, ',',''));
					}		    		
				}
				dataTable.fnDraw();
				
				$("input[type='checkbox']", app.tblFundTransferCADist).attr('checked', 'checked').attr('disabled', 'disabled');
				app.btnSave.hide();
				app.btnConfirm.show();
				
				$().add(app.checkAsOfDate).add(app.asOfDate).add(app.checkFromTo).add(app.fromDateCA).add(app.toDateCA)
				.add(app.announcementNo).attr('disabled', 'disabled');
				
				$().add(app.announcementNoHelp).add(app.btnPopulate).button("disable");
				
				app.total.show();
				
				app.totalAmountKSEI.val(numberWithCommas2(totalAmountKSEI));
				app.totalAmountBI.val(numberWithCommas2(totalAmountBI));
				//app.totalAmountEuro.val(numberWithCommas2(totalAmountEuro));
			}
		});
				
		
		app.btnConfirm.click(function(){			
			var param = [];

			$(dataTable.fnGetNodes()).each(function(i, e){
				var bean = $(this).data("bean").fundTransferDetail;
				bean.noSeq = $(this).data("bean").colNo;
				param[param.length] = bean;
			});
			
			$().submitAsync("@{saveFundTransfer()}", {'paramDetail':param, 'paramScreen':paramScreen, 'processType':'FUND_TRANSFER_PROCESS-4'}, function(respone) {	
				if (respone.status == 'SUCCESS') {
					app.fundBatchNo.val(respone.batchId);
				}
				
				loading.dialog('close');
				
				app.btnConfirm.hide();
				
				var closeDialog = function(){
					$("#dialog-message").dialog("close");
					location = "@{FundTransfer.list()}";
				};
				messageAlertOk(respone.msg, "ui-icon ui-icon-success", "Information", closeDialog);
				
			});
			
		}).hide();
		
		app.btnReset.click(function(){
			$().add(app.checkAsOfDate).add(app.asOfDate).add(app.checkFromTo).add(app.fromDateCA).add(app.toDateCA)
			.add(app.announcementNo).removeAttr('disabled');
			
			$().add(app.announcementNoHelp).add(app.btnPopulate).button("enable");
			
			app.checkAsOfDate.click();
			
			app.announcementNoKey.val("");
			app.announcementNo.val("");
			app.announcementNoDesc.val("");
			app.totalAmountKSEI.val("");
			app.totalAmountBI.val("");
			//app.totalAmountEuro.val("");
			app.fundBatchNo.val("");
			
			app.total.hide();
			
			
			$("#checkAll", html).removeAttr('checked').removeAttr('disabled');
			
			dataTable.fnClearTable();
			app.fromDateCA.removeClass('fieldError');
			app.asOfDateCA.removeClass('fieldError');
			
			app.btnSave.show();
			app.btnConfirm.hide();
		});
		
		function numberWithCommas(x) {
		    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		}
		
		function numberWithCommas2(x) {
		    var parts = x.toString().split(".");
		    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		    return parts.join(".");
		}
		
		Date.prototype.ddmmyyyy = function() {
			var mm = this.getMonth() + 1;
			var dd = this.getDate();
			
			return [(dd > 9 ? '' : '0') + dd,'/', (mm > 9 ? '' : '0') + mm,'/', this.getFullYear()  ].join('');
		};
		
	}else{
		return new CaDist(html);
	}
}