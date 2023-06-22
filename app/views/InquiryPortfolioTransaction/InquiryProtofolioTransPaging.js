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
			p.transactionNo = app.transactionNo.val();
			p.customerCodeSearchId = app.customerCodeId.val();
			p.securityType = app.securityTypeKey.val();
			p.securityId = app.securityId.val();
			p.transactionDateTo = app.transactionDateTo.val();
			p.transactionDateFrom = app.transactionDateFrom.val();
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
		
		app.datatable = app.tableInqPortofolio.paging("@{InquiryPortfolioTransaction.inquiryCancelTradePaging()}", this);
		
		$('#search').click(function() {
			
			if(($("#accordion #transactionDateFrom").val() == "") || ($("#accordion #transactionDateTo").val() == "")){
				if(($("#accordion #transactionDateFrom").val() == "")){
					$("#accordion #transactionDateFromError").html(" Required");
					$('#transactionDateFrom').addClass('fieldError');
				}if(($("#accordion #transactionDateTo").val() == "")){
					$("#accordion #transactionDateToError").html(" Required");
					$('#transactionDateTo').addClass('fieldError');
				}
				return false;
			}else if(($('#transactionDateFrom').hasClass('fieldError')) || ($('#transactionDateTo').hasClass('fieldError'))){
				return false;
			}
			else{
				app.query.val(true);
				$('#result').css('display', '');
				app.datatable.search.val("");
				app.datatable.fnPageChange("first");
			}
		});
		
		app.datatable.bind("select", function(event, prop){
			location.href='@{view()}?id='+prop.bean.transactionKey+'&fromInquiry=fromInquiry';
			//alert(prop.bean.customerNo+"-"+prop.row[1]+"-"+prop.tr);
		});
		

	}else{
		return new Paging(html);
	}
}