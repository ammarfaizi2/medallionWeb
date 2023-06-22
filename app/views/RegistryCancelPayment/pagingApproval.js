function Paging(html) {
	if (this instanceof Paging) {

		var roundType;
		var roundDigit;

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter
 * )
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.query = true;
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
//		app.root.accordion({collapsible: true});
		
//		app.search.add(app.reset).button();
		app.datatable = app.tblPTransaction.paging("@{RegistryCancelPayment.paging()}/?id=${maintenanceLogKey}", this, function(){
			$('.cApp').attr("checked", "checked");
			
			if( roundDigit == undefined || roundDigit == "" ){
				roundDigit = $("#digitAmount").val();
				roundType = $("#typeAmount").val();
			}

			var trs = $("tbody tr", app.tblPTransaction);
			trs.each(function(idx, e){
				var prop = $(e).data("prop");
				if( prop.bean ){
					// formatting amount					
					var formatedAmount = html.valueRnd(prop.bean.paidAmt, true, roundDigit, roundType);
					var xtd = $("td", this).eq(6);
					xtd.html(formatedAmount.val());					
				}
			});
			
		});

/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		
		
	}else{
		return new Paging(html);
	}
}