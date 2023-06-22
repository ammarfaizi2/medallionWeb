function Validation(html) {
	if (this instanceof Validation) {
		
/* =========================================================================== 
 * Constant
 * ========================================================================= */
				var digitAmount;
				var typeAmount;
				var digitUnit;
				var typeUnit;
				var digitPrice;
				var typePrice;
				
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var val = html.inject(this);
/* =========================================================================== 
 * Method
 * ========================================================================= */
		function next() {
			//if (!val.fundCode.isEmpty() && !val.goodfundDate.isEmpty()) {
				val.registryValidationForm.attr('action', 'next');
				formatRound();
				val.registryValidationForm.submit();
			//}
			
		}
		
		function process() {
			val.registryValidationForm.attr('action', 'process');
			val.registryValidationForm.submit();					
		}
		
		function list() {
			val.registryValidationForm.attr('action', 'list');
			val.registryValidationForm.submit();
		}
		
		function formatRound() {
			$("td.amountCell", html).each(function(){
				var amount = $(this).html();
				val.formatAmount.valueRnd(amount.toNumber(","), true, digitAmount, typeAmount);
				$(this).html(val.formatAmount.val());
			});
			
			$("td.unitCell", html).each(function(){
				var unit = $(this).html();
				val.formatUnit.valueRnd(unit.toNumber(","), true, digitUnit, typeUnit);
				$(this).html(val.formatUnit.val());
			});
			
			$("td.priceCell", html).each(function(){
				var price = $(this).html();
				val.formatPrice.valueRnd(price.toNumber(","), true, digitPrice, typePrice);
				$(this).html(val.formatPrice.val());
			});
		}
		
		function rounding(data) {
			digitAmount = rgProduct.amountRoundValue;
			typeAmount = rgProduct.amountRoundType;
			digitUnit = rgProduct.unitRoundValue;
			typeUnit = rgProduct.unitRoundType;
			digitPrice = rgProduct.priceRoundValue;
			typePrice = rgProduct.priceRoundType;
		}
/* =========================================================================== 
 * Event
 * ========================================================================= */
		if (!val.fundCode.val().isEmpty()) {
			var rgProduct = html.getRgProduct(val.fundCode.val());
			rounding(rgProduct);
			formatRound();
		}
		
		$('#new').remove();
		
		val.fundCode.popupProduct("goodfundDate", function(data){
			if (data) {
				$.ajax({
					url: '@{RegistryValidation.setGoodFundDate()}?productCode='+data.productCode,
					success: function(data) {
						checkRedirect(data);
						var goodFundDate = new Date( parseInt(data) );
						val.goodfundDate.datepicker("setDate", goodFundDate);
						$('#h_goodfundDate').val(data);
					}
				});
			}
		});
		
		val.btnNext.button();
		val.btnNext.click(function(){
			next();
		});
		
		val.btnProcess.button();
		val.btnProcess.click(function(){
			process();
		});
		
		val.btnCancel.button();
		val.btnCancel.click(function(){
			list();
		});
		
		$('#validationTable').resize(function(){
			validationTbl.fnAdjustColumnSizing();
		});
		
		var summaryTbl = html.id("summaryTable");
		if (summaryTbl) {
			if (summaryTbl.attr("id") != undefined) {
				summaryTbl.dataTable().fnDestroy();
			}
		}

		summaryTbl.dataTable({
			aaSorting: [[1,'asc']],
			aoColumnDefs: [ { bVisible:false, aTargets:[0] } ],	
			bJQueryUI:true,
			sScrollX:'100%',												// buat scrollbar di datatables (grid)
			bScrollCollapse: true,
			bInfo:false,
			bFilter:false,
			bPaginate:false,
			bSort:false
		});
		
		var validationTbl = html.id("validationTable");
		
		validationTbl.dataTable({
			aaSorting: [[1,'asc']],
			aoColumnDefs: [ { bVisible:false, aTargets:[0] },
			                { bSearchable: false, aTargets: [0] }
						  ],	
			bJQueryUI:true,
			bAutoWidth:false,
			sScrollX:'100%',												// buat scrollbar di datatables (grid)
			bScrollCollapse: true,
			sPaginationType: 'full_numbers'
		});
		
		if (val.notValid.val()=='Not Valid'){
			alert('Please check report error validate');
			return false;
		}
	}else{
		return new Validation(html);
	}
}
	