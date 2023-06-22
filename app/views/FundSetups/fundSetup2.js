function FundSetups(html) {
	if (this instanceof FundSetups) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
//		var populated = true;
		var app = html.inject(this, true);
		
		app.account.dynapopup('PICK_CF_MASTER', '', 'fundManagerCode');
		
	}else{
		return new FundSetups(html);
	}
}