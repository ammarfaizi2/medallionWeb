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
			p.individualUserKey = app.byGroupAuditHidden.val();
			p.dateFrom = app.dateFrom.val();
			p.dateTo = app.dateTo.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
//		app.customerNoOperator.children().eq(0).remove();
//		app.customerNameOperator.children().eq(0).remove();
//		app.contactNameOperator.children().eq(0).remove();
		app.accordion.accordion({collapsible: true});
		
		//app.print.add(app.reset).button();
		if ('${param}' == 'dedupe')
			app.newDataCust.button();
		
		
		app.datatable = app.tableaudit.paging("@{AuditTrail.auditPaging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		//app.datatable.search.val("");
		/*app.populate.click(function(){
			$('#result').css('display', '');
			app.datatable.search.val("");
			app.datatable.fnPageChange("first");
			return false;
		});*/
		
$('#populate').click(function() {
			
			
			var dateFrom = new Date($('#dateFrom').datepicker('getDate'));
			var dateTo = new Date($(this).datepicker('getDate'));
			
			if(($("#accordion #dateFrom").val() == "") || ($("#accordion #dateTo").val() == "")){
				if($("#accordion #dateFrom").val() == ""){
					$("#accordion #dateFromError").html(" Required");
				}
				if($("#accordion #dateTo").val() == ""){
					$("#accordion #dateToError").html(" Required");
				}
				if(document.getElementById('radioIndividual').checked && $("#accordion #byGroupAudit").val() == ""){
					$("#accordion #individualError").html(" Required");
				}
				return false
			}else if(($('#dateFrom').hasClass('fieldError')) || ($('#dateTo').hasClass('fieldError'))){
				return false;
			/* }else if($("#accordion #dateFrom").val() > $("#accordion #dateTo").val()){
				$("#accordion #dateFromError").html(" Date From is greater than Date To");
				return false; */
			}else if(document.getElementById('radioIndividual').checked && $("#accordion #byGroupAudit").val() == "" ){
				$("#accordion #individualError").html(" Required ");
				return false;
			}else{
				$("#accordion #individualError").html("");
				$("#accordion #dateFromError").html("");
				$("#accordion #dateToError").html("");
				
				$('#result').css('display', '');
				app.datatable.search.val("");
				app.datatable.fnPageChange("first");
			}
			//
		});
		
	}else{
		return new Paging(html);
	}
}