function DepositoMaturityProcess(html) {
	if (this instanceof DepositoMaturityProcess) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
//		var populated = true;
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Parameter
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.cfMaster = new Object();
			p.scTypeMaster = new Object();
			p.scMaster = new Object();
			
			if (app.maturityDate.datepicker('getDate') != null) 
				p.maturityDateMili = app.maturityDate.datepicker('getDate').getTime();
			
			p.all = app.allYes.attr("checked");
			p.cfMaster.customerNo = app.customer.val();
			p.scTypeMaster.securityType = app.securityType.val();
			p.scMaster.securityId = app.security.val();
			p.depositNoSign = app.depositNoSign.val();
			p.depositNo = app.depositNo.val();
			if (p.all == true){
				$("span", app.customer.siblings()).hide();
			}
			var pointer = $("input[name=param.depositoKey]", html);
			if (pointer.length > 0) {
				p.depositoKey = [];
				pointer.each(function(){ p.depositoKey.push($(this).val()); });
			}
			
			p.query = app.query.val();; 

			return p;
		};
/* =========================================================================== 
 * Function
 * ========================================================================= */
		function validate() {
			var checkAllNo = app.allNo.attr("checked");
			if (checkAllNo) {
				conCustomer = app.customerError.required(app.customer.isEmpty());
				return conCustomer;
			}
			
			return true;
		}
		
		function changePopupSecurity(filtered) {
			if (filtered) {
				app.security.dynapopup('PICK_SC_MASTER_BY_SEC_TYPE', app.securityType.val(), 'populate');
			}else{
				app.security.dynapopup('PICK_SC_MASTER', '', 'populate');
			}
		}  
		
//		function changePopupSecurity(filtered) {
//			if (filtered) {
//				app.security.securities('?type='+app.securityType.val()+'&filter='+app.securityType.val(), 'populate');
//			}else{
//				app.security.popup('@{Pick.securities()}', '@{Pick.security()}', '', 'populate');
//			}
//		}  
		
//		function realoadTable() {
//			if (populated) {
//				app.query.val(false);
//				app.datatable.fnPageChange("first");
//				populated = false;
//			}
//		}
		
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
		
		app.accordionMandatory.add(app.accordionOptional).accordion({collapsible: true});
		
		app.populate.add(app.process).add(app.reset).add(app.confirm).add(app.back).button();
		
		app.buttonList.add(app.buttonConfirm).hide();
		
		app.depositNoSign.children().eq(0).remove();
		
		if (app.dispatch.equal('list')) { 
			app.buttonList.show(); 
		}
		if (app.dispatch.equal('process')) { app.buttonConfirm.show(); }
		
		app.datatable = app.tableDeposito.paging("@{DepositoMaturityProcess.pagingDeposito()}", this, function(data){
			var trs = $("tbody tr", app.tableDeposito);
			
			if (app.readOnly.val() == 'true') {
				trs.each(function(){
					if ($("td", $(this)).length > 1) { // cek data kosong
						var prop = $(this).data("prop");
						$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled").attr("checked", "checked");
					}
				});
			}
		}, {"sScrollXInner":"190%"});
		
//		depricated di ganti dgn PICK_CF_MASTER
//		app.customer.popupCustomer('', 'securityType', function(){
//			app.customerError.html('');
//		});
		
		app.customer.dynapopup('PICK_CF_MASTER', '', 'securityType');
		app.securityType.dynapopup('PICK_SC_TYPE_MASTER_DEPOSITO', '', 'security', 
				function(data){
					var securityType = app.security.data("securityType");
					if (securityType != app.securityType.val()) {
						app.security.popupClear().data("securityType", app.securityType.val());
					}
					changePopupSecurity(true);	
				},
				function(){
					app.security.popupClear();
					changePopupSecurity(false);
				}
		);
		
		
//		app.securityType.securityTypeDeposito('', 'security', 
//				function(data){
//					var securityType = app.security.data("securityType");
//					if (securityType != app.securityType.val()) {
//						app.security.popupClear()
//							.data("securityType", app.securityType.val());
//					}
//					changePopupSecurity(true);	
//				},
//				function(){
//					app.security.popupClear();
//					changePopupSecurity(false);
//				}
//			);

		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.securityType.blur(function(){
			app.security.data("securityType", '');
			changePopupSecurity(!$(this).isEmpty());
		});
		changePopupSecurity(false);

		
		app.allNo.change(function(){
			$("span", app.customer.siblings()).show();
			app.customer.popupEnabled(true);
//			realoadTable();
		});
		
		app.allYes.change(function(){
			$("span", app.customer.siblings()).hide();
			app.customer.popupClear().popupEnabled(false);
			app.customerError.html('');
//			realoadTable();
		});
		
//		if (app.dispatch.equal('list') && app.allYes.attr("checked")) {
//			app.customer.popupClear().popupEnabled(false);
//			app.customerError.html('');
//		}
		
//		app.depositNoSign.change(function(){
//			if (!app.depositNo.isEmpty()) realoadTable();
//		});
		
//		app.depositNo.keyup(function(){
//			realoadTable();
//		});
		
//		app.depositNo.change(function(){
//			realoadTable();
//		});
		
		app.populate.click(function(){
			var valid = validate();
			app.query.val(valid);
			app.datatable.fnPageChange("first");
			if (valid) populated = true;
		});
		
		app.process.click(function(){
			if (validateSelect()) {
				
				var props = app.datatable.selects(0, "checked");
				
				for (x in props) {
					$("<input type=hidden name='param.depositoKey["+x+"]' value='"+props[x].bean.depositoKey+"' >").appendTo(app.searchForm);
				}
				
				app.searchForm.attr('action', 'process');
				app.searchForm.submit();
			}
		});
		
		app.reset.click(function(){
			app.searchForm.attr('action', 'reset');
			app.searchForm.submit();
		});
		
		var isConfirm = 0;
		app.confirm.click(function(){
			if(isConfirm == 1)
				return;
			isConfirm = 1;
			var loadingDialog = html.loadingDialog().dialog("open");
			app.dispatch.val("confirm");
			
			var depositoKeys = [];
			$("input[name=param.depositoKey]", html).each(function(){ 
				depositoKeys.push($(this).val()); 
			});
			
			var param = html.fetch("@{DepositoMaturityProcess.confirmDepositos()}", {"depositoKeys" : depositoKeys});
			loadingDialog.dialog("close");
			for (x in param.keys) {
				var key = param.keys[x];
				var message = param.messages[x];
				var prop = app.datatable.findBean("depositoKey", key);
				if (prop.length > 0) { 
					if (message == 'Save Success') {
						$("td", prop[0].tr).eq(1).html("<label style='color:green'>"+message+"</label>");
					}else{
						$("td", prop[0].tr).eq(1).html("<label style='color:red'>"+message+"</label>");
					}
				}
			}
			
			
			messageAlertOk(param.global, "ui-icon ui-icon-circle-check", "Information", function(){
				app.buttonList.add(app.confirm).hide();
				//app.searchForm.attr('action', 'reset');
				//app.searchForm.submit();
			});
		});
		
		app.back.click(function(){
			app.searchForm.attr('action', 'back');
			app.searchForm.submit();
		});
		
		
	}else{
		return new DepositoMaturityProcess(html);
	}
}