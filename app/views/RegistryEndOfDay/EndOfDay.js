function EndOfDay(html) {
	if (this instanceof EndOfDay) {
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var eod = html.inject(this);
		
/* =========================================================================== 
 * Method
 * ========================================================================= */
		function next() {
			if (!eod.fundCode.isEmpty() && !eod.goodfundDate.isEmpty()) {
				eod.registryEndOfDayForm.attr('action', 'next');
				eod.registryEndOfDayForm.submit();
			}
		}
		
		function process() {
			eod.registryEndOfDayForm.attr('action', 'process');
			eod.registryEndOfDayForm.submit();
		}
		
		function list() {
			eod.registryEndOfDayForm.attr('action', 'list');
			eod.registryEndOfDayForm.submit();
		}
/* =========================================================================== 
 * Event
 * ========================================================================= */
		$('#new').remove();
		html.clazz('calendar').datepicker();
		
		eod.fundCode.popupProduct("goodfundDate");
		
		eod.btnNext.button();
		eod.btnNext.click(function(){
			next();
		});
		
		eod.btnProcess.button();
		eod.btnProcess.click(function(){
			process();
		});
		
		eod.btnCancel.button();
		eod.btnCancel.click(function(){
			list();
		});
	}else{
		return new EndOfDay(html);
	}
}