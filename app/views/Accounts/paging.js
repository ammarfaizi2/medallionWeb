function Paging(html) {
	if (this instanceof Paging) {
		var app = html.inject(this, true);

		this.parameter = function(type) {
			var p = new Object();
			p.accountNoOperator = app.accountNoOperator.val();
			p.accountSearchNo = app.accountSearchNo.val();
			p.accountSearchName = app.accountSearchName.val();
			p.accountNameOperator = app.accountNameOperator.val();
			p.query = app.query.val();
			return p;
		};
		
		app.accordion.accordion({collapsible: true});
		
		app.datatable = app.tblAccount.paging("@{Accounts.pagingAccount()}", this);
		
		$('#search').click(function() {
			app.query.val(true);
			$('#result').css('display', '');
			app.datatable.search.val("");
			app.datatable.fnPageChange("first");
		});
		
		app.datatable.bind("select", function(event, prop){
			if(app.param.val() == 'acct-ovr-chg'){
				location.href="@{chargeOverride()}/?mode=${mode}&id="+ data[0]+"&status="+data[4];
				return false;
			}
			if('${mode}' == 'view'){
				location.href='@{view()}?id='+prop.bean.custodyAccountKey;
			}else{
				if ((jQuery.trim(prop.bean.recordStatus) == "N") || (jQuery.trim(prop.bean.recordStatus) == "U")) 
					location.href='@{view()}?id='+prop.bean.custodyAccountKey + "&param=${mode}";
				else
					location.href='@{edit()}/'+prop.bean.custodyAccountKey + "?param=${mode}";
			}
			
			
			
		});
			
		
	}else{
		return new Paging(html);
	}
}