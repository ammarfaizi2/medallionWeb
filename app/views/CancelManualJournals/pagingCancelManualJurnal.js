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
			p.faTransactionSearchFundKey = app.searchFundCodeKey.val();
			p.transactionSearchNoOperator = app.transactionSearchNoOperator.val();
			p.TransactionNoOperator = app.TransactionNoOperator.val();
			p.faTransactionSearchTransactionDateFrom = app.transactionDateFrom.val();
			p.faTransactionSearchTransactionDateTo = app.transactionDateTo.val();
			p.journalTypeOperator = $('input[name=journalType]:checked').val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */

		app.accordion.accordion({collapsible: true});
		
		app.datatable = app.tableCancelJurnal.paging("@{CancelManualJournals.pagingCancelFaTransaction()}", this);
		
		$('#search').click(function() {
			
			$("#transactionDateFromError").html("");
			$("#transactionDateToError").html("");
			$("#errmsgSearchFundCode").html("");
			if (($('#transactionDateFrom').val()=='') || ($('#transactionDateTo').val()=='') || ($('#searchFundCode').val()=='')) {
				
				if ($('#transactionDateFrom').val()=='') {
					$("#transactionDateFromError").html("Required");
				}
				
				if ($('#transactionDateTo').val()=='') {
					$("#transactionDateToError").html("Required");
				}
				
				if ($('#searchFundCode').val()=='') {
					$("#errmsgSearchFundCode").html("Required");
				}
				
				return false;
			} else {
				if ($('#transactionDateFrom').val()!=='') {
					$("#transactionDateFromError").html("");
				}
				
				if ($('#transactionDateTo').val()!=='') {
					$("#transactionDateToError").html("");
				}
				
				if ($('#searchFundCode').val()!=='') {
					$("#errmsgSearchFundCode").html("");
				}
				
				app.query.val(true);
				$('#result').css('display', '');
				app.datatable.search.val("");
				app.datatable.fnPageChange("first");
			}
			
			
			
		});
		
		app.datatable.bind("select", function(event, prop){
			location.href='@{view()}?key='+prop.bean.transactionKey;
		});
		

	}else{
		return new Paging(html);
	}
}