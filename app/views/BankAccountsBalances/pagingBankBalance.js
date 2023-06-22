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
			p.accountNoOperator = app.accountNoOperator.val();
			p.accountSearchNo = app.accountSearchNo.val();
			p.fundCode = app.fundCode.val();
			p.dateFrom = app.dateFrom.val();
			p.dateTo = app.dateTo.val();
			p.query = app.query.val();
			return p;
		};
		
		function validate() {
			if (($('#fundCodeKey').val() == '')){
				 $('#fundCodeError').html('Required');
					return false;
			}else if($('#dateFrom').val()!='' || $('#dateTo').val()!=''){
				if($('#dateFrom').hasClass('fieldError') || $('#dateTo').hasClass('fieldError'))
					return false;
				else
					return true;
			}else{
				return true;
			}
		}
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
//		app.customerNoOperator.children().eq(0).remove();
//		app.customerNameOperator.children().eq(0).remove();
//		app.contactNameOperator.children().eq(0).remove();
		app.accordion.accordion({collapsible: true});
		
		app.datatable = app.tableBnBalance.paging("@{BankAccountsBalances.pagingBnBalance()}", this);
		
		$('#search').click(function() {
			
			var valid = validate();
			if (valid){
				app.query.val(true);
				$('#result').css('display', '');
				app.datatable.search.val("");
				app.datatable.fnPageChange("first");
			}
		});
		
		app.datatable.bind("select", function(event, prop){
			if (($.trim(prop.bean.recordStatus) == 'N')||($.trim(prop.bean.recordStatus) == 'U')){
				location.href='@{view()}?id='+prop.bean.balanceKey;
			} else {
				location.href='@{edit()}?id='+prop.bean.balanceKey;
			}
			
		});
		

	}else{
		return new Paging(html);
	}
}