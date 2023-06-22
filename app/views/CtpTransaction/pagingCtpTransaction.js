function Paging(html) {
	if (this instanceof Paging) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
		app.ctpReport1.attr("checked",true);
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.transactionNo = app.transactionNo.val();
			p.customerCodeSearchId = app.customerCodeId.val();
			p.securityType = app.securityTypeKey.val();
			p.securityId = app.securityId.val();
			p.transactionDateTo = app.transactionDateTo.val();
			p.transactionDateFrom = app.transactionDateFrom.val();
			p.settlementDateTo = app.settlementDateTo.val();
			p.settlementDateFrom = app.settlementDateFrom.val();
			
			p.ctpReportOperator = $('input[name=ctpReport]:checked').val();
			
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */

		app.accordion.accordion({collapsible: true});
		
		app.datatable = app.tableCsPortofolio.paging("@{CtpTransaction.pagingCtpTransaction()}", this);
		
		$('#search').click(function() {
			
			var resultValidateTransaction = validateSearchTransaction();
			console.log('resultValidateTransaction = '+resultValidateTransaction);
			if (!resultValidateTransaction) {
				return;
			}
			
			var resultValidateSettlement = validateSearchSettlement();
			console.log('resultValidateSettlement = '+resultValidateSettlement);
			if (!resultValidateSettlement) {
				return;
			}
			
			app.query.val(true);
			$('#result').css('display', '');
			app.datatable.search.val("");
			app.datatable.fnPageChange("first");
		});
		
		app.datatable.bind("select", function(event, prop){
			if(prop.bean.seqCtpId == undefined) {
				location.href='@{entry()}?transactionKey='+prop.bean.transaction.transactionKey;
			} else {
				location.href='@{edit()}?id='+prop.bean.seqCtpId+'&transactionKey='+prop.bean.transaction.transactionKey;
			}
			
		});
		

	}else{
		return new Paging(html);
	}
}