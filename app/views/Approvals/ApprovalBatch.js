function ApprovalBatch(html) {
	if (this instanceof ApprovalBatch) {
		var componentss = ["grid","checkAll","approve","reject","search","approvalForm","reset","operator","referenceno","batchno"];
		var app = html.injectComp(this, false, componentss);
		var pageOfCheck;
		var checkboxsprev = null;
		
		$.fn.dataTableExt.oApi.fnStandingRedraw = function(oSettings) {
			if (oSettings.oFeatures.bServerSide === false){
				var before = oSettings._iDisplayStart;
		 
		        oSettings.oApi._fnReDraw(oSettings);
		 
		        // iDisplayStart has been reset to zero - so lets change it back
		        oSettings._iDisplayStart = before;
		        oSettings.oApi._fnCalculateEnd(oSettings);
		    } 
		    
		    // draw the 'current' page
		    oSettings.oApi._fnDraw(oSettings);
		};

		$.fn.dataTableExt.oApi.fnPagingInfo = function(oSettings) {
			return {
				"iStart":oSettings._iDisplayStart,
				"iEnd":oSettings.fnDisplayEnd(),
				"iLength":oSettings._iDisplayLength,
				"iTotal":oSettings.fnRecordsTotal(),
				"iFilteredTotal":oSettings.fnRecordsDisplay(),
				"iPage":Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength +1),
				"iTotalPages":Math.ceil( oSettings.fnRecordsDisplay() / oSettings._iDisplayLength )
			};
		};

		
		var dontSort = [];
        $('thead th', app.grid).each( function () {
            if ($(this).hasClass('no_sort')) { dontSort.push({ "bSortable": false });
            } else { dontSort.push({ "bSortable": true }); }
        } );
        
		var table = app.grid.dataTable({
			aaSorting:[[1,'desc']],
			aoColumns: dontSort,	
			bJQueryUI:true,
			sScrollX:'100%',
			sPaginationType:'full_numbers',
			bLengthChange:false,
			iDisplayLength:15,
			fnDrawCallback: function() {    
				var currentPage = this.fnPagingInfo().iPage;
				if (pageOfCheck != undefined && pageOfCheck != currentPage){
					if (checkboxsprev != null && checkboxsprev != undefined) { checkboxsprev.removeAttr('checked'); }
				}
				
				checkboxsprev = $('input[type=checkbox]', $(this).fnGetFilteredNodes);
				checkboxsprev.each(function(i, e){
					if (i != 0) {
						$(e).click(function(){
							if (!$(this).is(':checked')) app.checkAll.removeAttr('checked');
						});
					}
				});
			}   
		});
		
		app.checkAll.click(function (){
			var checkboxs = $('input[type=checkbox]', table.fnGetFilteredNodes);

			if ($(this).is(':checked')) {
				checkboxs.attr('checked', this.checked);
				pageOfCheck = table.fnPagingInfo().iPage;
			} else {
				checkboxs.removeAttr('checked');
			}
		});
		
		app.approve.click(function() {
			var rowNum = table.fnGetNodes().length;
			var listBatch = "";

			var selectCount = 0;
			for (var i=0; i<rowNum; i++) {
				var tr_row = table.fnGetNodes(i);
				if ($('#checkList', tr_row).is(':checked')) {
					selectCount+=1;
					listBatch = listBatch + 'listBatch[]=' + $('#urlList', tr_row).val() + '&'; 
				}
			}

			if (listBatch != '') {
				var answer = confirm("Total item to be approve = "+selectCount+", process ?");
				if (answer) {
					loading.dialog('open');
					$.post('@{approveBatch()}?' + listBatch, function(data) {
			    		checkRedirect(data);
						location = "@{Approvals.listbatch()}";
						//loading.dialog('close');
					});
				}
			}else{
				alert('Please select data');
			}
		}); 
		
		app.reject.click(function() {
			var rowNum = table.fnGetNodes().length;
			var listBatch = "";

			var selectCount = 0;
			for (var i=0; i<rowNum; i++) {
				var tr_row = table.fnGetNodes(i);
				if ($('#checkList', tr_row).is(':checked')) {
					selectCount+=1;
					listBatch = listBatch + 'listBatch[]=' + $('#urlList', tr_row).val() + '&'; 
				}
			}
			
			if (listBatch != '') {
				var answer = confirm("Total item to be reject = "+selectCount+", process ?");
				if (answer) {
					loading.dialog('open');
					$.post('@{rejectBatch()}?' + listBatch, function(data) {
			    		checkRedirect(data);
						location = "@{Approvals.listbatch()}";				
					});
				}
			}else{
				alert('Please select data');
			}
		});
		
		app.search.click(function(){
			app.approvalForm.attr('action', "@{listbatch()}");
			app.approvalForm.submit();
		});
		
		app.reset.click(function(){
			app.operator.val("");
			app.referenceno.val("");
			app.batchno.val("");
		});
	}else{
		return new ApprovalBatch(html);
	}
}