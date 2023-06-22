function TradePaging(html) {
	if (this instanceof TradePaging) {
		/* =========================================================================== 
		 * Variable
		 * ========================================================================= */
		var app = html.inject(this, true);

		app.search.button();
		app.reset.button();
		app.newdata.button();
		app.root.accordion({collapsible:true});
		/* =========================================================================== 
		 * Table Search Parameter (penamaan harus fix yaitu parameter)
		 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.transactionDateFrom = app.transactionDateFrom.val();
			p.transactionDateTo = app.transactionDateTo.val();
			p.fundKey = app.fundKey.val();
			p.fundCode = app.fundCode.val();
			p.investorNo = app.investorNo.val();
			p.transactionNo = app.transactionNo.val();
			p.transactionNoOperator = app.transactionNoOperator.val();
			p.query = app.query.val();
			return p;
		};
		app.datatable = app.tableTrade.paging("@{RegistryRedemption.tradePaging()}", this);
		app.datatable.bind("select", function(event, prop){
			location.href='@{edit()}?id='+prop.bean.tradeKey;
		});
		/*
		 * Fund Code Picker
		 * */
		if(app.fundCode.val() == ""){
			//app.searchInvtAcct.dynapopup('PICK_RG_INVEST', '', 'transactionSearchNoOperator');
			//alert('ok');
			app.investorNo.dynapopup('PICK_CF_MASTER_BY_INVESTOR', '', 'transactionNoOperator');
		}
		
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
					//investorLookup();
				},
				error : function(data) {
					app.fundCode.addClass('fieldError');
					app.fundCodeDesc.val("NOT FOUND");
					app.fundCode.val("");
				}
			},
			key:app.fundKey,
			description:app.fundCodeDesc,
			help:app.fundCodeHelp
		});
		/*
		 * End of Fund Code Picker
		 * */
		
		/*
		 * Investor Picker
		 * */
		function investorLookup(){
			var urlInvLook = '@{Pick.rgInvestmentAccts()}?type='+app.fundCode.val().replaceAll(" ", "+	");
			app.investorNo.lookup({
				list:urlInvLook,
				get:{
					url:'@{Pick.rgInvestmentAcct()}',
					success: function(data) {
						if (data) {
							app.investorNo.removeClass('fieldError');
							app.investorNo.val(data.acountNumber);
							app.investorNoDesc.val(data.description);
						}
					},
					error : function(data) {
						app.investorNo.addClass('fieldError');
						app.investorNoDesc.val('NOT FOUND');
						app.investorNo.val("");
					}
				},
				description:app.investorNoDesc,
				help:app.investorNoHelp
			});			
		}
		/*
		 * End of Investor Picker
		 * */
		//investorLookup();
		/* =========================================================================== 
		 * Event
		 * ========================================================================= */
		//app.datatable.search.val("");

		$('#transactionDateFrom').change(function(){
			$("#pTransactionDateFrom label span").remove();
	        if($(this).val() != '' || $('#transactionDateTo').val() != ''){
				$("#pTransactionDateFrom label").append("<span class='req'>*</span>");
			}
		});
		
		$('#transactionDateTo').change(function(){
			$("#pTransactionDateFrom label span").remove();
	        if($(this).val() != '' || $('#transactionDateFrom').val() != ''){
				$("#pTransactionDateFrom label").append("<span class='req'>*</span>");
			}
		});

		app.search.click(function(){
			app.transactionDateFromError.html("");
			app.transactionDateToError.html("");
			
			var toPaging = true;
			
			$('#transactionDateFrom').removeClass('fieldError');
			$('#transactionDateTo').removeClass('fieldError');
			$("#transactionDateFromError").html("");
			$("#transactionDateToError").html("");
			
			if(($("#transactionDateFrom").val() == "") && ($("#transactionDateTo").val() == "")){
				
			}else{
				if(($("#transactionDateFrom").val() != "")){
					if(($("#transactionDateTo").val() == "")){
						$("#transactionDateToError").html(" Required");
						$('#transactionDateTo').addClass('fieldError');
						toPaging = false;
					} else {
						$("#transactionDateToError").html("");
						$('#transactionDateTo').removeClass('fieldError');
					}
				}
				
				if(($("#transactionDateTo").val() != "")){
					if(($("#transactionDateFrom").val() == "")){
						$("#transactionDateFromError").html(" Required");
						$('#transactionDateFrom').addClass('fieldError');
						toPaging = false;
					} else {
						$("#transactionDateFromError").html("");
						$('#transactionDateFrom').removeClass('fieldError');
					}
				}
				
				if (($('#transactionDateFrom').val() != '') && ($('#transactionDateTo').val() != '')) {
					var dateFrom = $('#transactionDateFrom').datepicker('getDate');
					var dateTo = $('#transactionDateTo').datepicker('getDate');
					var origin = 'from';
					var id = '#transactionDate';
					
					compareDateFromToEqual(dateFrom, dateTo, origin, id);
					if($('#transactionDateTo').hasClass('fieldError') || $('#transactionDateFrom').hasClass('fieldError')){
						toPaging = false;
					}
				}
			}
			
			if(toPaging){
				$('#result').css('display', '');
				var searchVal = "";
				if( $("#tableTrade_wrapper .capitalize") != null || $("#tableTrade_wrapper .capitalize") != undefined ){
					searchVal = $("#tableTrade_wrapper .capitalize").val();
				}
				app.datatable.search.val( searchVal );
				app.query.val(true);
				app.datatable.fnPageChange("first");
			}
			
			return false;
		});

		app.reset.click(function(){
			location.href="@{RegistryRedemption.search()}";
		});
		
		app.newdata.click(function(){
			location.href="@{entry()}";
		});
		
		$("#tableTrade_wrapper .capitalize").blur(function(){
			app.search.click();
		});
		//investorLookup();
	}else{
		return new TradePaging(html);
	}
}