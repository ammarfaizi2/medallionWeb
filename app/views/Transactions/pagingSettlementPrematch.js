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
			p.transactionNo = app.transactionSearchNo.val();
			p.customerCode = app.customerCodeId.val();
			p.securityType = app.securityTypeKey.val();
			p.securityId = app.securityId.val();
			p.settlementDateTo = app.settlementDateTo.val();
			p.settlementDateFrom = app.settlementDateFrom.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
//		app.customerNoOperator.children().eq(0).remove();
//		app.customerNameOperator.children().eq(0).remove();
//		app.contactNameOperator.children().eq(0).remove();
		app.accordion.accordion({collapsible: true});
		
		app.datatable = app.tablePagingSettle.paging("@{Transactions.pagingSettlementPrematch()}", this);
		
		$('#search').click(function() {
			
			var toPaging = true;
			
			$('#settlementDateFrom').removeClass('fieldError');
			$('#settlementDateTo').removeClass('fieldError');
			$("#accordion #settlementDateFromError").html("");
			$("#accordion #settlementDateToError").html("");
			
			if(($("#accordion #settlementDateFrom").val() == "") && ($("#accordion #settlementDateTo").val() == "")){
				
			}else{
				if(($("#accordion #settlementDateFrom").val() != "")){
					if(($("#accordion #settlementDateTo").val() == "")){
						$("#accordion #settlementDateToError").html(" Required");
						$('#settlementDateTo').addClass('fieldError');
						toPaging = false;
					} else {
						$("#accordion #settlementDateToError").html("");
						$('#settlementDateTo').removeClass('fieldError');
					}
				}
				
				if(($("#accordion #settlementDateTo").val() != "")){
					if(($("#accordion #settlementDateFrom").val() == "")){
						$("#accordion #settlementDateFromError").html(" Required");
						$('#settlementDateFrom').addClass('fieldError');
						toPaging = false;
					} else {
						$("#accordion #settlementDateFromError").html("");
						$('#settlementDateFrom').removeClass('fieldError');
					}
				}
				
				if (($('#settlementDateFrom').val() != '') && ($('#settlementDateTo').val() != '')) {
					var dateFrom = $('#settlementDateFrom').datepicker('getDate');
					var dateTo = $('#settlementDateTo').datepicker('getDate');
					var origin = 'from';
					var id = '#settlementDate';
					
					compareDateFromToEqual(dateFrom, dateTo, origin, id);
					if($('#settlementDateTo').hasClass('fieldError') || $('#settlementDateFrom').hasClass('fieldError')){
						toPaging = false;
					}
				}
			}
			
			if(toPaging){
				app.query.val(true);
				$('#result').css('display', '');
				app.datatable.search.val("");
				app.datatable.fnPageChange("first");
			}
		});
		
		app.datatable.bind("select", function(event, prop){
			location.href='@{view()}?id='+prop.bean.transactionKey+'&param=${param}';
		});
		

	}else{
		return new Paging(html);
	}
}