function CpRuleMasterPaging(html) {
	if (this instanceof CpRuleMasterPaging) {
		
		
		html.dataTable({
			"bDestroy": true,
			"aaSorting": [[1,'asc']],
			"aoColumnDefs": [ { bVisible:false, aTargets:[0] }, 
			                { bSearchable: false, aTargets: [ 0 ] 
						  } ],
			"bJQueryUI": true,
			"sScrollX":'100%',
			"bScrollCollapse": true,
			"sPaginationType": "full_numbers",
			
//		    "bPaginate": true,		    
//		    "bProcessing": true,
		    "bServerSide": true,
			"sAjaxSource": "@{ComplianceRulesMaster.paging()}"
//			"fnServerData": function(sSource, aoData, fnCallBack) {
//				alert('sSource '+sSource);
//				alert('aoData '+aoData);
//				alert('fnCallBack '+fnCallBack);
//				console.log('ke server ');
//			}
		});
	}else{
		return new CpRuleMasterPaging(html);
	}
}