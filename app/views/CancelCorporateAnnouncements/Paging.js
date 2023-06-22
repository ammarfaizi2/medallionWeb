function Paging(html) {
	if (this instanceof Paging) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.actionCode = app.actionCodeKey.val();
			p.securityType = app.securityTypeId.val();
			p.securityCode = app.securityCodeId.val();
			p.dateFrom = app.announecementDateFrom.val();
			p.dateTo = app.announecementDateTo.val();
			p.corporateNoOperator = app.announcementNoOperator.val();
			p.corporateSearchNo = app.announcementSearchNo.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});
		app.search.add(app.reset).button();
		app.result.hide();
		app.datatable = app.tableannouncement.paging("@{CancelCorporateAnnouncements.paging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.search.click(function(){
			
			/*if(app.actionCodeKey.val() == "" || app.securityTypeId.val() == "" || app.securityCodeId.val() == "" || 
				app.announecementDateFrom.hasClass('fieldError') || app.announecementDateTo.hasClass('fieldError')){*/
			if(app.actionCodeKey.val() == "" || app.securityTypeId.val() == "" || app.securityCodeId.val() == ""){
				
				if(app.actionCodeKey.val() == ""){
					app.actionCodeError.html("required");
				}else{
					app.actionCodeError.html("");
				}
				if(app.securityTypeId.val() == ""){
					app.securityTypeError.html("required");
				}else{
					app.securityTypeError.html("");
				}
				if(app.securityCodeId.val() == ""){
					app.securityCodeError.html("required");
				}else{
					app.securityCodeError.html("");
				}
				return false;
			}else if($('#announecementDateFrom').val()!='' || $('#announecementDateTo').val()!=''){
				if($('#announecementDateFrom').hasClass('fieldError') || $('#announecementDateTo').hasClass('fieldError'))
					return false;
				else{
					app.actionCodeError.html("");
					app.securityTypeError.html("");
					app.securityCodeError.html("");
					$('#announecementDateFrom').removeClass('fieldError');
					$('#announecementDateTo').removeClass('fieldError');
					$('#announecementDateFromError').html('');
					$('#announecementDateToError').html('');
					app.result.show();
					app.query.val(true);
					app.datatable.fnPageChange("first");
				}
			}else{
				app.actionCodeError.html("");
				app.securityTypeError.html("");
				app.securityCodeError.html("");
				$('#announecementDateFrom').removeClass('fieldError');
				$('#announecementDateTo').removeClass('fieldError');
				$('#announecementDateFromError').html('');
				$('#announecementDateToError').html('');
				app.result.show();
				app.query.val(true);
				app.datatable.fnPageChange("first");
			}	
		});

		app.reset.click(function(){
			app.datatable.trigger("fetch", [0, "checked"]);
		});

		app.datatable.bind("select", function(event, prop){
			location.href='@{view()}/?id=' + prop.bean.corporateAnnouncementKey;
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				
			}
		});
		
	}else{
		return new Paging(html);
	}
}