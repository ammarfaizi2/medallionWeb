function List(html) {
	if (this instanceof List) {
/* =========================================================================== 
 * Variable
 * ========================================================================= */
			
		var app = html.inject(this, true);
		var trRow;
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.query = app.query.val() == '1';
			p.fromDate = app.fromDate.val();
			p.toDate = app.toDate.val();
			p.securityType = app.securityType.val();
			p.securityKey = app.securityCodeKey.val();
			return p;
		};
								
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});
		
		app.btnSearch.add(app.btnReset).add(app.btnAdd).button();
		
		app.datatable = app.tableCaNews.paging("@{CANewsAnnouncement.paging()}", this);
		
/* =========================================================================== 
 * Function
 * ========================================================================= */
		function validate(){
			app.newsDateError.html('');
			var valid = true;
			
			if (app.fromDate.isEmpty()) { app.newsDateError.required(true); valid = false }
			if (app.toDate.isEmpty()) { app.newsDateError.required(true); valid = false }
			
			if (valid) {
				var fromDate = app.fromDate.datepicker('getDate');
		        var dateTo = app.toDate.datepicker('getDate');
		        if (fromDate.getTime() > dateTo.getTime()) {
		        	app.newsDateError.html('Invalid Date Rage');
		        	valid = false;
		        }
			}
		
			return valid;
		}
		
		function attachSecurityCodePopup(cleanSecurityCode) {
			if (cleanSecurityCode) {
				app.securityCode.val('');
				app.securityCodeKey.val('');
				app.securityCodeDesc.val('');
			}
			
			var securityType = app.securityType.val();
			var picker = (securityType == '') ? 'PICK_SC_MASTER' : 'PICK_SC_MASTER_BY_SEC_TYPE';
			app.securityCode.dynapopup2({key:'securityCodeKey', help:'securityCodeHelp', desc:'securityCodeDesc'}, picker, securityType, 'search', function(data){
				app.securityCode.removeClass('fieldError');
				app.securityCodeKey.val(data.code);
				app.securityCodeDesc.val(data.description);
			},function(data){
				app.securityCode.addClass('fieldError');
				app.securityCode.val('');
				app.securityCodeKey.val('');
				app.securityCodeDesc.val('NOT FOUND');
			});
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		app.securityType.lookup({
			list:'@{Pick.securityTypes()}',
			get:{
				url:'@{Pick.securityType()}',
				success: function(data){
					app.securityType.removeClass('fieldError');
					app.securityType.val(data.code);
					app.securityTypeDesc.val(data.description);
					attachSecurityCodePopup(true);
 				},
				error: function(data){
					app.securityType.addClass('fieldError');
					app.securityType.val('');
					app.securityTypeDesc.val('NOT FOUND');
					attachSecurityCodePopup(true);
				}
			},
			description : app.securityTypeDesc,
			help : app.securityTypeHelp,
			nextControl : app.securityCode
		});
		attachSecurityCodePopup(false);
		
		app.securityType.change(function(){
			if (app.securityType.isEmpty()) {
				attachSecurityCodePopup(false);
			}
		})

		app.btnSearch.click(function(){
			if (validate()){
				app.query.val('1');
				app.datatable.fnPageChange("first");
				app.result.show();
				app.datatable.fnAdjustColumnSizing();
			}
		});
		
		app.btnReset.click(function(){
			location.href="@{list()}";	
		});
		
		app.btnAdd.click(function(){
			location.href='@{entry()}';
		});
		
		app.datatable.bind("select", function(event, prop){
			// N = New, U = Update, L = Deliver
			if (jQuery.trim(prop.bean.record_status) == 'N' || jQuery.trim(prop.bean.record_status) == 'U' || jQuery.trim(prop.bean.record_status) == 'L'){
				location.href='@{view()}/?id=' + prop.bean.ca_news_key;
			}else{
				location.href='@{edit()}/?id=' + prop.bean.ca_news_key;
			}
		});
		
		// Dummy Data
		//app.fromDate.val('01/03/2018');
		//app.toDate.val('30/03/2018');
	}else{
		return new List(html);
	}
	
}