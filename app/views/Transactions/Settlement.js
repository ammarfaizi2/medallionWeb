function Settlement(html) {
	if (this instanceof Settlement) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var populated = false;
		var app = html.inject(this, true);
		var depositoryArr = ['DEPOSITORY_CODE-N/A', 'DEPOSITORY_CODE-1', 'DEPOSITORY_CODE-3'];
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.securityType = new Object();
			p.security = new Object();
			
			if (app.settlementDate.datepicker('getDate') != null) 
				p.settlementDateMili = app.settlementDate.datepicker('getDate').getTime(); 
			p.securityType.securityType = app.securityType.val();
			p.security.securityId = app.security.val();
			var pointer = $("input[name=param.transactionKey]", html);
			if (pointer.length > 0) {
				p.transactionKey = [];
				pointer.each(function(){ 
					p.transactionKey.push($(this).val()); 
				});
			}
			
			p.query = app.query.val();; 
			
			return p;
		};
		
/* =========================================================================== 
 * Function
 * ========================================================================= */
		function validate() {
			// validate 1st step
			conSettlementDate = app.settlementDateError.required(app.settlementDate.isEmpty());
			conSecurityType = app.securityTypeError.required(app.securityType.isEmpty());
			conSecurity = app.securityError.required(app.security.isEmpty());

			// validate 2nd step
			conSettlementDateGreater = true;
			
			if (conSettlementDate) {
				var result =  html.fetch("@{Transactions.validateSettlementDate()}", {"milisecond":app.settlementDate.datepicker('getDate').getTime()});
				conSettlementDateGreater = app.settlementDateError.valid(result[0].length == 0, result[0]);
			}
			
			return conSettlementDate && conSecurityType && conSecurity && conSettlementDateGreater;
		}
		
		function validateDataSingle(prop) {
			var sendToDipository = app.sendToDepository1.attr("checked");
			var result =  html.fetch("@{Transactions.validateTransaction()}", {"depositoryId":app.depositoryId.val(), "custodyAccountKey":prop.bean.account.custodyAccountKey, "ctpNo":prop.bean.ctpNo, "ctpRequired":app.ctpRequired.val(), "marketOfRiskId":app.marketOfRiskId.val(), "transactionKey":prop.bean.transactionKey, "sendToDipository":sendToDipository});
			
			$("td", prop.tr).eq(7).html("<label style='color:red'>"+html.concat(result, "|")+"</label>");
			if (result[0].length > 0) return false;
			return true;
		}
		
		function validateSelect() {
			var props = app.datatable.selects(0, "checked");
			if (props.length == 0) {
				if (props.length == 0) {
					messageAlertOk("Must be at least one item to settle!", "ui-icon ui-icon-circle-check", "Information", function(){
						return false;
					});
					return false;
				}
			}else{
				return true;
			}
		}
		
		function behaviourSendToDipository() {
			if (app.readOnly.val() == 'false') {
				if (!app.depositoryId.isEmpty()) {
					if ($.inArray(app.depositoryId.val(), depositoryArr) >= 0) {
						html.radioButton("sendToDepository", false)
							.behaviour("radioButton", "sendToDepository", true);
					}else{
						html.behaviour("radioButton", "sendToDepository", false)
							.radioButton("sendToDepository", true);
							
					}
				}else{
					html.behaviour("radioButton", "sendToDepository", false)
						.radioButton("sendToDepository", true);
				}
			}
		}
		
		function changePopupSecurity() {
			behaviourSendToDipository();
			app.security.securities('?type='+app.securityType.val()+'&filter='+app.securityType.val(), 'populate', 
					function(data){
						app.depositoryError.html('');
						app.securityError.required(app.security.isEmpty());
						app.issuerKey.val(data.issuerKey);
						app.marketOfRiskId.val(data.marketOfRiskId);
						app.depositoryId.val(data.depositoryId);
						app.depository.val(data.depositoryDesc);
						app.depositoryHidden.val(data.depositoryDesc);
						behaviourSendToDipository();
					},
					function() {
						app.depositoryError.html('');
						app.security.popupClear();
						app.issuerKey.val('');;
						app.marketOfRiskId.val('');
						app.depositoryId.val('');;
						app.depository.val('');
						app.depositoryHidden.val('');
						behaviourSendToDipository();
					}
				);	
		}
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.accordion.accordion({collapsible: true});
		
		app.populate.add(app.settle).add(app.reset).add(app.confirm).add(app.back).add(app.close).button();
		
		app.datatable = app.tableSettlement.paging("@{Transactions.pagingSettlement()}", this, function(data){
			var trs = $("tbody tr", app.tableSettlement);
			
			if (app.readOnly.val() == 'true') {
				trs.each(function(){
					if ($("td", $(this)).length > 1) { // cek data kosong
						var prop = $(this).data("prop");
						$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled").attr("checked", "checked");
					}
				});
			}else{
				trs.each(function(){
					if ($("td", $(this)).length > 1) { // cek data kosong
						var prop = $(this).data("prop");
						$("td", $(this)).eq(7).html("");
						
						if (!validateDataSingle(prop)) {
							$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled");
						}
					}
				});
			}
		});
								
		app.settlementDate.datepicker();
		
		app.securityType.securityType('?filter=', 'security', 
			function(data){
				app.ctpRequired.val(data.ctpRequired);
				app.securityTypeError.required(app.securityType.isEmpty());
				var securityType = app.security.data("securityType");
				if (securityType != app.securityType.val()) {
					app.security.popupClear();
					app.depositoryError.html('');
					app.depositoryId.val('');;
					app.issuerKey.val('');
					app.marketOfRiskId.val('');
					app.depository.val('');
					app.depositoryHidden.val('');
					app.security.data("securityType", app.securityType.val());
				}
				
				changePopupSecurity();	
			},
			function(){
				app.security.popupClear();
				app.issuerKey.val('');;
				app.marketOfRiskId.val('');
				app.depositoryError.html('');
				app.depositoryId.val('');;
				app.depository.val('');
				app.depositoryHidden.val('');
				
				changePopupSecurity();
			}
		);
		
		function buttonHideShow() {
			if (app.dispatch.val() == 'view') {
				app.buttonEntry.show();
				app.buttonSettle.hide();
				app.buttonConfirm.hide();
			}
			
			if (app.dispatch.val() == 'settle') {
				app.populate.hide();
				app.buttonEntry.hide();
				app.buttonSettle.show();
				app.buttonConfirm.hide();
			}
			
			if (app.dispatch.val() == 'confirm') {
				app.populate.hide();
				app.buttonEntry.hide();
				app.buttonSettle.hide();
				app.buttonConfirm.show();
			}
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		behaviourSendToDipository();
		
		buttonHideShow();
		
		app.populate.click(function(){
			var valid = validate();
			app.query.val(valid);
			app.datatable.fnPageChange("first");
			if (valid) populated = true;
		});
		
		app.settle.click(function(){
			if (validateSelect()) {
				
				var props = app.datatable.selects(0, "checked");
				
				for (x in props) {
					$("<input type=hidden name='param.transactionKey["+x+"]' value='"+props[x].bean.transactionKey+"' >").appendTo(app.searchForm);
				}
				
				app.searchForm.attr('action', 'settleBatch');
				app.searchForm.submit();
			}
		});
		
		app.reset.add(app.close).click(function(){
			app.searchForm.attr('action', 'reset');
			app.searchForm.submit();
		});
		
		app.confirm.click(function(){
			app.dispatch.val("confirm");
			
			var transactionKeys = [];
			$("input[name=param.transactionKey]", html).each(function(){ 
				transactionKeys.push($(this).val()); 
			});
			
			var sendToDipository = app.sendToDepository1.attr("checked");
			var batchr = html.fetch("@{Transactions.confirmSettleBatch()}", {"transactionKeys" : transactionKeys, "sendToDepository" : sendToDipository});
			if (batchr.transactionBatchKey) { app.batchNo.val(batchr.transactionBatchKey); }
			
			var result = batchr.errors;
			for (x in result) {
				if (result[x][0] == "global") {
					app.globalError.html(result[x][1]);
				}else{
					var prop = app.datatable.findBean("transactionKey", result[x][0]);
					if (prop.length > 0) { 
						if (result[x][1] == 'Save success') {
							$("td", prop[0].tr).eq(7).html("<label style='color:green'>"+result[x][1]+"</label>");
						}else{
							$("td", prop[0].tr).eq(7).html("<label style='color:red'>"+result[x][1]+"</label>");
						}
					}
				}
			}
			
			buttonHideShow();
		});

		app.back.click(function(){
			app.searchForm.attr('action', 'settlement');
			app.searchForm.submit();
		});
		
		app.sendToDepository1.add(app.sendToDepository2).change(function(){
			if (populated) {
				app.query.val(validate());
				app.datatable.fnPageChange("first");
				populated = false;
			}
		});
		
		app.datatable.bind("select", function(event, prop){
			if (app.dispatch.val() == 'view') {
				var con = validateDataSingle(prop);
				if (con) {
					location.href = "@{settleSingle()}?key=" + prop.bean.transactionKey;
				}
			}
		});
	}else{
		return new Settlement(html);
	}
}