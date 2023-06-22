function Paging(html) {
	if (this instanceof Paging) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
	
/* =========================================================================== 
 * Table Search Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.rgTradeSearchTransactionDateFrom = $('#transactionDateFrom').val();
			p.rgTradeSearchTransactionDateTo = $('#transactionDateTo').val();
			p.rgTradeSearchFundKey = $('#searchFundCode').val();
			p.rgTradeSearchInvestorAcct = $('#searchInvtAcct').val();
			p.TransactionNoOperator = $('#TransactionNoOperator').val();
			p.transactionSearchNoOperator = $('#transactionSearchNoOperator').val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		$("#root").add("#result").inject(this, true).root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		$('#newDataSubscription').button();
		
		app.datatable = app.tableSubscription.paging("@{RegistrySubscription.searchPaging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		$('.calendar').datepicker();
		
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
		
		$('#searchFundCode').change(function(){
			if ($('#searchFundCode').val()==''){
				$('#searchFundKey').val('');
				$('#searchFundCodeDesc').val('');
				$('#h_searchFundCodeDesc').val('');
				//app.searchInvtAcct.dynapopup('PICK_RG_INVEST', '', 'transactionSearchNoOperator');
				$('#searchInvtAcct').val('');
				$('#searchInvtAcctKey').val('');
				$('#searchInvtAcctDesc').val('');
				$('#h_searchInvtAcctDesc').val('');
			}
		});
		
		if(app.searchFundCode.val() == ""){
			//app.searchInvtAcct.dynapopup('PICK_RG_INVEST', '', 'transactionSearchNoOperator');
			app.searchInvtAcct.dynapopup('PICK_CF_MASTER_BY_INVESTOR', '', 'transactionSearchNoOperator');
		}
		
		$('#searchFundCode').lookup({
			list:'@{Pick.rgProductByEffDateAndLiqDates()}',
			get:{
				url:'@{Pick.rgProductByEffDateAndLiqDate()}',
				success: function(data){
					$('#searchFundCode').removeClass('fieldError');
					$('#searchFundCode').val(data.productCode);
					var searchFundCode = $('#searchFundCode').val();
					//investorAccount(searchFundCode);
					$('#searchFundCodeDesc').val(data.description);
					$('#h_searchFundCodeDesc').val(data.description);
					
					//app.searchInvtAcct.dynapopup('PICK_RG_INVEST_BY_PROD', app.searchFundCode.val(), 'transactionSearchNoOperator');
					$('#searchInvtAcct').val('');
					$('#searchInvtAcctDesc').val('');
				},
				error: function(data){
					$('#searchFundCode').addClass('fieldError');
					$('#searchFundCode').val('');
					//investorAccount('');
					$('#searchFundCodeDesc').val('NOT FOUND');
					$('#h_searchFundCodeDesc').val('');
				}
			},
			description:$('#searchFundCodeDesc'),
			help:$('#searchFundCodeHelp')
		});
		
		
		function investorAccount(searchFundCode) {
			$('#searchInvtAcct').lookup({
				list:'@{Pick.rgInvestmentAccts()}?type=' + searchFundCode,
				get:{
					url:'@{Pick.rgInvestmentAcct()}?type=' + searchFundCode,
					success: function(data){
						$('#searchInvtAcct').removeClass('fieldError');
						$('#searchInvtAcct').val(data.accountNumber);
						$('#searchInvtAcctDesc').val(data.description);
						$('#h_searchInvtAcctDesc').val(data.description);
					},
					error: function(data){
						$('#searchInvtAcct').addClass('fieldError');
						$('#searchInvtAcct').val('');
						$('#searchInvtAcctDesc').val('NOT FOUND');
						$('#h_searchInvtAcctDesc').val('');
					}
				},
				description:$('#searchInvtAcctDesc'),
				help:$('#searchInvtAcctHelp')
			});
		}
		
		$('#searchInvtAcct').change(function(){
			if ($('#searchInvtAcct').val()==''){
				$('#searchInvtAcctKey').val('');
				$('#searchInvtAcctDesc').val('');
				$('#h_searchInvtAcctDesc').val('');
			}
		});
		
		$('#search').click(function() {
			$("#transactionDateFromError").html("");
			$("#transactionDateToError").html("");
			
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
				app.datatable.search.val("");
				app.query.val(true);
				app.datatable.fnPageChange("first");
				return false;
			}
			
		});
		
		$('#reset').click(function() {
			location.href="@{list()}";	
			return false;
		});

		$('#newDataSubscription').click(function(){
			location.href="@{entry()}";
		});
		
		app.datatable.bind("select", function(event, prop){
			location.href='@{edit()}/?id=' + prop.bean.tradeKey;
		});
		
	}else{
		return new Paging(html);
	}
}