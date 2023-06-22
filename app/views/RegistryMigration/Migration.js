function Migration(html) {
	if (this instanceof Migration) {
		
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var val = html.inject(this);
/* =========================================================================== 
 * Method
 * ========================================================================= */
		function process() {
			if (!val.fundCode.isEmpty() && !val.fromDate.isEmpty() && !val.toDate.isEmpty()) {
				val.registryMigrationForm.attr('action', 'process');
				val.registryMigrationForm.submit();
			}
		}
		
		function list() {
			val.registryMigrationForm.attr('action', 'list');
			val.registryMigrationForm.submit();
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */	
		$('#new').remove();
		html.clazz('calendar').datepicker();
		
		val.fundCode.popupProduct("goodfundDate");
				
		val.btnProcess.button();
		val.btnProcess.click(function(){
			process();
		});
	}else{
		return new Migration(html);
	}
}
	