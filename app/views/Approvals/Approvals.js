function Approvals(html) {
	if (this instanceof Approvals) {
		var app = html.inject(this);
		
		var table = app.tblApproval.dataTable({
			aaSorting:[[0,'desc']],
			aoColumns:[null, null, null, null, null, null],	
			bJQueryUI:true,
			sScrollX:'100%',
			sPaginationType:'full_numbers',
			bLengthChange:false,
			iDisplayLength:15
		});
		
		app.search.click(function(){
			app.approvalForm.attr('action', "@{list()}");
			app.approvalForm.submit();
		});		

		app.reset.click(function(){
			app.operator.val("");
			app.referenceno.val("");
			app.batchno.val("");
			location.href='@{list()}';
		});
	}else{
		return new Approvals(html);
	}
}