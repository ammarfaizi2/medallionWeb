function list(html) {
	if (this instanceof list) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var app = html.inject2(this, false);
		
		app.accordion.accordion({collapsible: true});
		
		$().add(app.search).add(app.reset).button();
		
		this.parameter = function(type) {
			var p = new Object();
			p.query = app.query.val();
			p.fromDate = app.transactionDateFrom.val();
			p.toDate = app.transactionDateTo.val();
			p.customerKey = app.customerCodeKey.val();
			p.securityType = app.securityTypeKey.val();
			p.securityKey = app.securityCodeKey.val();
			return p;
		};
		
		app.customerCode.lookup({
			list : '@{Pick.customers2()}',
			get : {
				url: '@{Pick.customer()}',
				success: function(data) {
					app.customerCodeKey.val(data.customerKey);
					app.customerCodeDesc.val(data.description);
				},
				error: function(data) {
					app.customerCodeKey.val('');
					app.customerCode.val('');
					app.customerCode.addClass('fieldError');
					app.customerCodeDesc.val('NOT FOUND');
				}
			},
			description : app.customerCodeDesc,
			help : app.customerCodeHelp
		});
		
		app.securityType.dynapopup2({key:'securityTypeKey', help:'securityTypeHelp', desc:'securityTypeDesc'}, 'PICK_SC_TYPE_MASTER_BY_SEC_CLASS', 'SECURITY_CLASS-FI', 'securityCode', 
			function(){ securityCodePopup(); },
			function(){ securityCodePopup(); }
		);
		
		securityCodePopup();
		
		app.datatable = app.tblAcquisition.paging("@{Acquisition.pagingAcquisition()}", this);
		
		app.datatable.bind("select", function(event, prop){
			location.href="@{edit()}/"+prop.bean.transctionKey;
		});
		
/*==================================================================
 * Function
 *==================================================================*/
		function validate() {
			app.transactionDateError.html("");
			
			var fromDate = app.transactionDateFrom.val();
			var toDate = app.transactionDateTo.val();
			if (fromDate === '' || toDate === '' ) {
				app.transactionDateError.html("required");
				return false;
			}
			
			var fromDate = app.transactionDateFrom.datepicker("getDate")
			var toDate = app.transactionDateTo.datepicker("getDate")
			if (fromDate > toDate) {
				app.transactionDateError.html("Date From must be less or equal then Date To");
				return false;
			}
			
			return true;
		}
		
		function securityCodePopup() {
			app.securityCodeKey.val('');
			app.securityCode.val('');
			app.securityCodeDesc.val('');
			
			var securityType = app.securityType.val();
			var picker = (securityType == '') ? 'PICK_SC_MASTER_BY_SECURITY_CLASS' : 'PICK_SC_MASTER_BY_SEC_TYPE';
			var filter = (securityType == '') ? 'SECURITY_CLASS-FI' : securityType;
			
			app.securityCode.dynapopup2({key:'securityCodeKey', help:'securityCodeHelp', desc:'securityCodeDesc'}, picker, filter, 'search', function(data){
				app.securityCodeKey.val(data.code);
				app.securityCodeDesc.val(data.description);
			},function(data){
				app.securityCode.val('');
				app.securityCodeKey.val('');
				app.securityCodeDesc.val('NOT FOUND');
			});
		}
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/

/*================================================================== 
 * Event
 *================================================================== */
		app.search.click(function(){
			if (validate()) {
				if (app.customerCode.val() == '') { app.customerCodeKey.val('') }
				if (app.securityType.val() == '') { app.securityTypeKey.val('') }
				if (app.securityCode.val() == '') { app.securityCodeKey.val('') }
				
				var fromDate = app.transactionDateFrom.val();
				var toDate = app.transactionDateTo.val();
				var customerKey = app.customerCodeKey.val();
				var securityTypeKey = app.securityTypeKey.val();
				var securityCodeKey = app.securityCodeKey.val();
				
				app.query.val(true);
				app.result.show();
				app.datatable.search.val(""); 
				app.datatable.fnPageChange("first");
			}
		});
		
		app.reset.click(function(){
			app.transactionDateError.html('');
			app.customerCodeError.html('');
			app.securityTypeError.html('');
			app.securityCodeError.html('');
			
			app.transactionDateFrom.removeClass('fieldError');
			app.transactionDateFrom.val('');
			
			app.transactionDateTo.removeClass('fieldError');
			app.transactionDateTo.val('');
			
			app.customerCode.removeClass('fieldError');
			app.customerCodeKey.val('')
			app.customerCode.val('');
			app.customerCodeDesc.val('');
			
			app.securityType.removeClass('fieldError');
			app.securityTypeKey.val('');
			app.securityType.val('');
			app.securityTypeDesc.val('');
			
			app.securityCode.removeClass('fieldError');
			app.securityCodeKey.val('');
			app.securityCode.val('');
			app.securityCodeDesc.val('');
		});
		
	}else{
		return new list(html);
	}
}