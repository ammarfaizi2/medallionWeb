function MaturityPaging(html) {
	if (this instanceof MaturityPaging) {
		/* =========================================================================== 
		 * Variable
		 * ========================================================================= */
		var app = html.inject(this, true);

		app.search.button();
		app.reset.button();
		app.root.accordion({collapsible:true});

		/* =========================================================================== 
		 * Table Search Parameter (penamaan harus fix yaitu parameter)
		 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.maturityDateFrom = app.maturityDateFrom.val();
			p.maturityDateTo = app.maturityDateTo.val();
			p.maxDate = app.maxDate.val();
			p.fundKey = app.fundKey.val();
			p.fundCode = app.fundCode.val();
			p.query = app.query.val();
			return p;
		};
		app.datatable = app.maturityTable.paging("@{RegistryLiquidationProcess.maturityPaging()}", this);
		/*
		 * Fund Code Picker
		 * */
		app.fundCode.lookup({
			list:'@{Pick.rgProducts()}',
			get:{
				url:'@{Pick.rgProduct()}',
				success: function(data) {
					if (data) {
						app.fundCode.removeClass('fieldError');
						app.fundCode.val(data.productCode);
						app.fundCodeDesc.val(data.description);
					}
				},
				error : function(data) {
					app.fundCode.addClass('fieldError');
					app.fundCodeDesc.val('NOT FOUND');
					app.fundCode.val("");
					app.fundCodeDesc.val("");
				}
			},
			key:app.fundKey,
			description:app.fundCodeDesc,
			help:app.fundCodeHelp
		});
		/*
		 * End of Fund Code Picker
		 * */
		
		app.maturityDateFrom.change(function(){
			var dateFrom = $(this).datepicker('getDate');
			var dateTo = $('#maturityDateTo').datepicker('getDate');
			var origin = 'from';
			var id = '#maturityDate';
			if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#maturityDateTo').val()!=''))
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
		});
		
		app.maturityDateTo.change(function(){
			var dateFrom = $('#maturityDateFrom').datepicker('getDate');
			var dateTo = $(this).datepicker('getDate');
			var origin = 'to';
			var id = '#maturityDate';
			if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#maturityDateFrom').val()!=''))
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
		});

		/**
		 * search impl
		 * 
		 */
		app.reset.click(function(){
			app.maturityDateFrom.val("");
			app.maturityDateTo.val("");
			app.maturityDateFrom.removeClass('fieldError');
			app.maturityDateTo.removeClass('fieldError');
			location.reload();
		});

		
		/**
		 * search impl
		 */
		app.search.click(function(){
//			app.maturityDateToError.html("");
//			app.maturityDateFromError.html("");
			app.maxDate.val("31/12/9999");//hardcode tanggal ini karena jika maturity_date = max maka bernilai 31/12/9999 di rg_product
			if( app.maturityDateFrom.hasClass('fieldError') || app.maturityDateTo.hasClass('fieldError') || app.maturityDateFrom.val() == "" || app.maturityDateTo.val() == "" ){
				if( app.maturityDateFrom.val() == "" ){
					app.maturityDateFromError.html("Required");
				}
				
				if( app.maturityDateTo.val() == "" ){
					app.maturityDateToError.html("Required");
				}
			}else{
				var searchVal = "";
				if( $("#maturityTable_wrapper .capitalize") != null || $("#maturityTable_wrapper .capitalize") != undefined ){
					searchVal = $("#maturityTable_wrapper .capitalize").val();
				}
				app.datatable.search.val( searchVal );
				app.datatable.fnClearTable();
				$('#result').css('display', '');
				app.query.val(true);
				app.datatable.fnPageChange("first");
			}
			return false;
		});

		app.datatable.bind("select", function(event, prop){
			location.href='@{edit()}?id='+prop.bean.productCode+'&isNewRec=false';
		});
		
		$("#maturityTable_wrapper .capitalize").blur(function(){
			app.search.click();
		});
		
	}else{
		return new MaturityPaging(html);
	}
}