function DepositoRollbackProcess(html) {
	if (this instanceof DepositoRollbackProcess) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		popUpCustomer();

/* =========================================================================== 
 * Table Parameter
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.cfMaster = new Object();
			p.scTypeMaster = new Object();
			p.scMaster = new Object();
			
			p.maturityDateMili = -1;
			if (app.maturityDate.datepicker('getDate') != null) {
				p.maturityDateMili = app.maturityDate.datepicker('getDate').getTime();
			}
			
			p.all = app.allYes.attr("checked");
			p.cfMaster.customerNo = app.customer.val();
			p.scTypeMaster.securityType = app.securityType.val();
			p.scMaster.securityId = app.security.val();
			p.depositNoSign = app.depositNoSign.val();
			p.depositNo = app.depositNo.val();
			
			var pointer = $("input[name=param.transactionKey]", html);
			if (pointer.length > 0) {
				p.transactionKey = [];
				pointer.each(function(){ p.transactionKey.push($(this).val()); });
			}
			
			p.query = app.query.val();; 

			return p;
		};
/* =========================================================================== 
 * Function
 * ========================================================================= */
		function validate() {
			var conMaturityDate = app.maturityDateError.required(app.maturityDate.isEmpty());
			var checkAllNo = app.allNo.attr("checked");
			if (checkAllNo) {
				conCustomer = app.customerError.required(app.customer.isEmpty());
				return conCustomer && conMaturityDate;
			}else{
				return conMaturityDate;
			}
			
			return true;
		}
		
		function changePopupSecurity(filtered) {
//			if (filtered) {
//				app.security.securities('?type='+app.securityType.val()+'&filter='+app.securityType.val(), 'populate');
//			}else{
//				app.security.popup('@{Pick.securities()}', '@{Pick.security()}', '', 'populate');
//			}
			if (filtered) {
				app.security.dynapopup('PICK_SC_MASTER_BY_SEC_TYPE', app.securityType.val(), 'populate');
			}else{
				app.security.dynapopup('PICK_SC_MASTER', '', 'populate');
			}
		}  
		
		function validateSelect() {
			var props = app.datatable.selects(0, "checked");
			if (props.length == 0) {
				if (props.length == 0) {
					messageAlertOk("Must be at least one item to procced", "ui-icon ui-icon-circle-check", "Information", function(){
						return false;
					});
					return false;
				}
			}else{
				return true;
			}
		}
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.securityType.blur(function(){
			app.security.data("securityType", '');
			changePopupSecurity(!$(this).isEmpty());
		});
		changePopupSecurity(false);
		
		app.maturityDate.datepicker();
		
		app.accordionMandatory.add(app.accordionOptional).accordion({collapsible: true});
		
		app.populate.add(app.process).add(app.reset).add(app.confirm).add(app.back).button();
		
		app.buttonList.add(app.buttonConfirm).hide();
		
		app.depositNoSign.children().eq(0).remove();
		
		if (app.dispatch.equal('list')) { 
			app.buttonList.show(); 
		}
		if (app.dispatch.equal('process')) { app.buttonConfirm.show(); }
		
		app.datatable = app.tableDeposito.paging("@{DepositoRollbackProcess.pagingDeposito()}", this, function(data){
			var trs = $("tbody tr", app.tableDeposito);
			
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
						if (!prop.bean.valid) {
							$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled");
						}
					}
				});
			}
		}, {"sScrollXInner":"150%"});

//		app.customer.popupCustomer('', 'securityType', function(){
//			app.customerError.html('');
//		});

		app.securityType.securityTypeDeposito('', 'security', 
				function(data){
					var securityType = app.security.data("securityType");
					if (securityType != app.securityType.val()) {
						app.security.popupClear()
							.data("securityType", app.securityType.val());
					}
					changePopupSecurity(true);	
				},
				function(){
					app.security.popupClear();
					changePopupSecurity(false);
				}
			);

		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.allNo.change(function(){
			$("span", app.customer.siblings()).show();
			app.customer.popupEnabled(true);
		});
		
		app.allYes.change(function(){
			$("span", app.customer.siblings()).hide();
			app.customer.popupClear().popupEnabled(false);
			app.customerError.html('');
		});
		
		app.populate.click(function(){
			var valid = validate();
			app.query.val(valid);
			app.datatable.fnPageChange("first");
			if (valid) populated = true;
		});
		
		app.process.click(function(){
			app.customerNo.val(app.customer.val());
			
			if (validateSelect()) {
				var props = app.datatable.selects(0, "checked");
				
				for (x in props) {
					$("<input type=hidden name='param.transactionKey["+x+"]' value='"+props[x].bean.transactionKey+"' >").appendTo(app.searchForm);
				}
				
				app.searchForm.attr('action', 'process');
				app.searchForm.submit();
			}
		});
		
		app.reset.click(function(){
			app.searchForm.attr('action', 'reset');
			app.searchForm.submit();
		});
		
		app.confirm.click(function(){
			var loadingDialog = html.loadingDialog().dialog("open");
			app.dispatch.val("confirm");
			
			var transactionKeys = [];
			$("input[name=param.transactionKey]", html).each(function(){ 
				transactionKeys.push($(this).val()); 
			});
			
			var param = html.fetch("@{DepositoRollbackProcess.confirmDepositos()}", {"transactionKeys" : transactionKeys});
			loadingDialog.dialog("close");
			for (x in param.keys) {
				var key = param.keys[x];
				var message = param.messages[x];
				var prop = app.datatable.findBean("transactionKey", key);
				if (prop.length > 0) { 
					if (message == 'Save Success') {
						$("td", prop[0].tr).eq(1).html("<label style='color:green'>"+message+"</label>");
					}else{
						$("td", prop[0].tr).eq(1).html("<label style='color:red'>"+message+"</label>");
					}
				}
			}
			
			
			messageAlertOk(param.global, "ui-icon ui-icon-circle-check", "Information", function(){
				app.searchForm.attr('action', 'reset');
				app.searchForm.submit();
			});
		});
		
		app.back.click(function(){
			app.searchForm.attr('action', 'back');
			app.searchForm.submit();
		});
		
	}else{
		return new DepositoRollbackProcess(html);
	}
}