function list(html) {
	if (this instanceof list) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.messageMode = app.messageMode.val();
			p.fromDate = app.fromDate.val();
			p.toDate = app.toDate.val();
			p.settleFrom = app.settleFromDate.val();
			p.settleTo = app.settleToDate.val();
			p.sender = app.sender.val();
			p.senderDesc = app.senderDesc.val();
			p.receiver = app.receiver.val();
			p.receiverDesc = app.receiverDesc.val();
			p.securityType = app.securityType.val();
			p.securityTypeDesc = app.securityTypeDesc.val();
			p.securityCode = app.securityCode.val();
			p.securityCodeDesc = app.securityCodeDesc.val();
			p.isinCode = app.isinCode.val();
			p.isinCodeDesc = app.isinCodeDesc.val();
			p.status = app.status.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Function
 * ========================================================================= */
		function securityCodePopup(picker, securityType) {
			if (securityType === undefined) { securityType = ''};
			
			app.securityCode.val('');
			app.securityCodeId.val('');
			app.securityCodeDesc.val('');
			
			app.securityCode.dynapopup2({key:'securityCodeId', help:'securityCodeHelp', desc:'securityCodeDesc'}, picker, securityType, 'isinCode', 
				function(data){
					if (data) {
						app.isinCode.val('');
						app.isinCodeId.val('');
						app.isinCodeDesc.val('');
						app.securityCode.removeClass('fieldError'); 
					}
				},
				function(data) { app.securityCode.addClass('fieldError'); }
			);

		}
		
		function isinCodePopup(picker, securityType) {
			if (securityType === undefined) { securityType = ''};
			
			app.isinCode.val('');
			app.isinCodeId.val('');
			app.isinCodeDesc.val('');
			
			app.isinCode.dynapopup2({key:'isinCodeId', help:'isinCodeHelp', desc:'isinCodeDesc'}, picker, securityType, 'status', 
					function(data){
						if (data) {
							app.securityCode.val('');
							app.securityCodeId.val('');
							app.securityCodeDesc.val('');
							app.isinCode.removeClass('fieldError'); 
						}
					},
					function(data) { app.isinCode.addClass('fieldError'); }
				);
		}
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		
		app.sender.dynapopup2({key:'senderId', help:'senderHelp', desc:'senderDesc'}, 'PICK_SWIFT_SENDER', '', 'receiver', 
			function(data) { if (data) { app.senderErr.removeClass('fieldError'); } },
			function(data) { app.senderErr.addClass('fieldError'); }
		);
		
		app.receiver.dynapopup2({key:'receiverId', help:'receiverHelp', desc:'receiverDesc'}, 'PICK_SWIFT_RECEIVER', '', 'messagePriority', 
			function(data) { if (data) { app.receiverErr.removeClass('fieldError'); } },
			function(data) { app.receiverErr.addClass('fieldError'); }
		);		
		
		app.securityType.dynapopup2({key:'securityTypeId', help:'securityTypeHelp', desc:'securityTypeDesc'}, 'PICK_SC_TYPE_MASTER', '', 'securityCode', 
			function(data){
				if (data) {					
					app.securityType.removeClass('fieldError');
					securityCodePopup('PICK_SC_MASTER_BY_SEC_TYPE', app.securityType.val());
					isinCodePopup('PICK_ISIN_CODE_BY_SEC_TYPE', app.securityType.val());
				}
			},
			function(data){
				app.securityType.addClass('fieldError');
				securityCodePopup('PICK_SC_MASTER');
				isinCodePopup('PICK_ISIN_CODE');
			}
		);
		
		securityCodePopup('PICK_SC_MASTER');
		isinCodePopup('PICK_ISIN_CODE')
		
		app.datatable = app.tblSwiftUpdate.paging("@{SwiftUpdate.paging()}", this);
		
		function validate() {
			app.fromDateErr.html('');
			app.toDateErr.html('');
			app.settleFromDateErr.html('');
			app.settleToDateErr.html('');
			app.fromDate.removeClass('fieldError');
			app.toDate.removeClass('fieldError');			
			app.settleFromDate.removeClass('fieldError');
			app.settleToDate.removeClass('fieldError');
			
			isValidForm =true;
			
			var fromDateVal = app.fromDate.val();
			var toDateVal = app.toDate.val();			
			if (fromDateVal == '' && toDateVal != '') {
				app.fromDateErr.html('required');
				isValidForm = false;
			}
			
			if (fromDateVal != '' && toDateVal == '') {
				app.toDateErr.html('required');
				isValidForm = false;
			}
			
			var settleFromDateVal = app.settleFromDate.val();
			var settleToDateVal = app.settleToDate.val();			
			if (settleFromDateVal == '' && settleToDateVal != '') {
				app.settleFromDateErr.html('required');
				isValidForm = false;
			}
			
			if (settleFromDateVal != '' && settleToDateVal == '') {
				app.settleToDateErr.html('required');
				isValidForm = false;
			}
			
			var DateFrom = new Date($('#fromDate').datepicker('getDate'));
			var DateTo = new Date($('#toDate').datepicker('getDate'));
			if (fromDateVal!='' && toDateVal!='') { 
				if (DateTo.getTime() < DateFrom.getTime()) {
					$('#toDate').addClass('fieldError');
					$("#toDateErr").html("Date To must be greather than Date From");
					isValidForm = false;
				} 
			}
			
			var DateSettleFrom = new Date($('#settleFromDate').datepicker('getDate'));
			var DateSettleTo = new Date($('#settleToDate').datepicker('getDate'));
			if (settleFromDateVal!='' && settleToDateVal!='') { 
				if (DateSettleTo.getTime() < DateSettleFrom.getTime()) {
					$('#settleToDate').addClass('fieldError');
					$("#settleToDateErr").html("Date To must be greather than Date From");
					isValidForm = false;
				} 
			}
			
			return isValidForm;
		}

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.search.click(function(){
			if (validate()) {
				app.query.val(true);
				app.datatable.fnPageChange("first");
			}
		});

		app.reset.click(function(){
			location.href="@{SwiftUpdate.list()}";			
		});

		app.datatable.bind("select", function(event, prop){
			location.href="@{SwiftUpdate.edit()}/?id="+ prop.bean.swiftKey;
			return false;
		});
		
		// Query for picker
		// insert into gn_pick(id, query, title, typedata, created_by, created_date, modified_by, modified_date, widthdata, pkdata)
		// values ('PICK_ISIN_CODE', 'SELECT isin_code, description FROM sc_master WHERE is_active = 1 ORDER BY isin_code ASC', 'Isin Code, Description', 'String, String', 'MIGRATION', sysdate, 'MIGRATION', sysdate, '30, 70', 0)
		// insert into gn_pick(id, query, title, typedata, created_by, created_date, modified_by, modified_date, widthdata, pkdata)
		// values ('PICK_ISIN_CODE_BY_SEC_TYPE', 'SELECT isin_code, description FROM sc_master WHERE is_active = 1 and security_type = ? ORDER BY isin_code ASC', 'Isin Code, Description', 'String, String', 'MIGRATION', sysdate, 'MIGRATION', sysdate, '30, 70', 0)
		app.query.val(true); //untuk fix, pertama kali bisa serach di table search
	}else{
		return new list(html);
	}
}