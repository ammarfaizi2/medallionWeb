function Paging(html) {
	if (this instanceof Paging) {
		var app = html.inject(this, true);

		this.parameter = function(type) {
			var p = new Object();
			p.bankAccountNoOperator = app.bankAccountNoOperator.val();
			p.bankAccountSearchNo = app.bankAccountSearchNo.val();
			p.bankAccountNameOperator = app.bankAccountNameOperator.val();
			p.bankAccountSearchName = app.bankAccountSearchName.val();
			p.query = app.query.val();
			return p;
		};
		
		app.accordion.accordion({collapsible: true});
		
		app.datatable = app.tblBankAccount.paging("@{BankAccounts.pagingBankAccount()}", this);
		
		$('#search').click(function() {
			app.query.val(true);
			$('#result').css('display', '');
			app.datatable.search.val("");
			app.datatable.fnPageChange("first");
		});
		
		app.datatable.bind("select", function(event, prop){
			if('${mode}' == 'view'){
				location.href='@{view()}?id='+prop.bean.bankAccountKey;
			}else{
				if ((jQuery.trim(prop.bean.recordStatus) == "N") || (jQuery.trim(prop.bean.recordStatus) == "U")) 
					location.href='@{view()}?id='+prop.bean.bankAccountKey + "&param=${mode}";
				else
					location.href='@{edit()}/'+prop.bean.bankAccountKey + "?param=${mode}";//location.href='@{edit()}?id='+prop.bean.bankAccountKey+'?param=edit';
			}
			
		});
			
		
	}else{
		return new Paging(html);
	}
}