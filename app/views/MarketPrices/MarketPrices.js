function MarketPrices(html) {
	if (this instanceof MarketPrices) {
		/* =========================================================================== 
		 * Variable
		 * ========================================================================= */
			
			var app = html.inject(this, true);
			var trRow;
		/* =========================================================================== 
		 * Table Serach Parameter (penamaan harus fix yaitu parameter)
		 * ========================================================================= */
				this.parameter = function() {
					var p = new Object();
					p.marketPriceSearchFrom = app.marketPriceSearchFrom.val();
					p.marketPriceSearchTo = app.marketPriceSearchTo.val();
					p.groupCodeId = app.groupCodeId.val();
					p.securityType = app.securityType.val();
					p.securityKey = app.securityKey.val();
					return p;
				};
								
		/* =========================================================================== 
		 * Runtime
		 * ========================================================================= */
				app.root.accordion({collapsible: true});
				
				app.search.add(app.reset).button();
				
				app.result.hide();
				
				app.datatable = app.tableMarket.paging("@{MarketPrices.pagingList()}", this);

		
		/* =========================================================================== 
		 * Event
		 * ========================================================================= */
			
				$('#groupCode').lookup({
					list:'@{Pick.lookups()}?group=${securityPrice}',
					get:{
						url:'@{Pick.lookup()}?group=${securityPrice}',
						success: function(data){
							$('#groupCode').removeClass('fieldError');
							$('#groupCodeId').val(data.code);
							$('#groupCodeName').val(data.description);
							$('#h_groupCodeName').val(data.description);
						},
						error: function(data){
							$('#groupCode').addClass('fieldError');
							$('#groupCodeId').val('');
							$('#groupCode').val('');
							$('#groupCodeName').val('NOT FOUND');
							$('#h_groupCodeName').val('');
						}
					},
					description:$('#groupCodeName'),
					help:$('#groupCodeHelp')
				}); 
		
				$('#securityType').lookup({
					list:'@{Pick.securityTypesWithPrice()}',
					get:{
						url:'@{Pick.securityTypeWithPrice()}',
						success: function(data){
							$('#securityType').removeClass('fieldError');
							$('#securityType').val(data.code);
							$('#securityTypeDescription').val(data.description);
							$('#h_securityTypeDescription').val(data.description);
							$('#priceUnit').val(data.priceUnit);
							$('#securityType').change();
		 				},
						error: function(data){
							$('#securityType').addClass('fieldError');
							$('#securityType').val('');
							$('#securityTypeDescription').val('NOT FOUND');
							$('#h_securityTypeDescription').val('');
							$('#securityType').change();
						}
					},
					description:$('#securityTypeDescription'),
					help:$('#securityTypeHelp'),
					nextControl:$('#securityCode')
				});
				
				$('#securityType').change(function(){
					//if ($('#securityType').val()==''){
						$('#securityCode').val('');
						$('#securityKey').val('');
						$('#securityCodeDesc').val('');
						$('#h_securityCodeDesc').val('');
					//}
					attachSecuritiesPaging();
				});
				
				function attachSecuritiesPaging() {
					var securityType = $('#securityType').val();
					var pickName = (securityType == '') ? 'PICK_SC_MASTER' : 'PICK_SC_MASTER_BY_SEC_TYPE';
					$('#securityCode').dynapopup2({key:'securityKey', help:'securityCodeHelp', desc:'securityCodeDesc'}, pickName, securityType, 'search', function(data){
						$('#securityCode').removeClass('fieldError');
						$('#securityKey').val(data.code);
						$('#securityCodeDesc').val(data.description);
						$('#h_securityCodeDesc').val(data.description);
					},function(data){
						$('#securityCode').addClass('fieldError');
						$('#securityCode').val('');
						$('#securityKey').val('');
						$('#securityCodeDesc').val('NOT FOUND');
						$('#h_securityCodeDesc').val('');
					});
				}
				attachSecuritiesPaging();
				
//				$('#securityCode').lookup({
//					list:'@{Pick.securities()}',
//					get:{
//						url:'@{Pick.security()}',
//						success: function(data){
//							$('#securityCode').removeClass('fieldError');
//							$('#securityKey').val(data.code);
//							$('#securityCodeDesc').val(data.description);
//							$('#h_securityCodeDesc').val(data.description);
//						},
//						error: function(data){
//							$('#securityCode').addClass('fieldError');
//							$('#securityCode').val('');
//							$('#securityKey').val('');
//							$('#securityCodeDesc').val('NOT FOUND');
//							$('#h_securityCodeDesc').val('');
//						}
//					},
//					description:$('#securityCodeDesc'),
//					filter:$('#securityType'),
//					help:$('#securityCodeHelp')
//				});
		
				$('#marketPriceSearchFrom').change(function(){
			        var dateFrom = $(this).datepicker('getDate');
			        var dateTo = $('#marketPriceSearchTo').datepicker('getDate');
			        var origin = 'from';
			        var id = '#marketPriceSearch';
			        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#marketPriceSearchTo').val()!='')) {
			            compareDateFromTo(dateFrom, dateTo, origin, id);
			        }
			        
			        var checkError = $("input").hasClass('fieldError');
			        
			        if (checkError){
			            $('#search').button('disable');
			        }  else {
			            $('#search').button('enable');
			        }
			    });
				
				
				$('#marketPriceSearchTo').change(function(){
			        var dateFrom = $('#marketPriceSearchFrom').datepicker('getDate');
			        var dateTo = $(this).datepicker('getDate');
			        var origin = 'to';
			        var id = '#marketPriceSearch';
			        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#marketPriceSearchFrom').val()!='')) {
			            compareDateFromTo(dateFrom, dateTo, origin, id);
			        }
			        
			        var checkError = $("input").hasClass('fieldError');
			        if (checkError){
			            $('#search').button('disable');
			        }  else {
			            $('#search').button('enable');
			        }
			    });
				
				app.newData.button();
				
				app.newData.click(function(){
					location.href='@{entry()}';
				});
				app.search.click(function(){
					if (validate()){
						app.datatable.fnPageChange("first");
						app.result.show();
						app.datatable.fnAdjustColumnSizing();
					}
					return false;
				});
				
				app.reset.click(function(){
					location.href="@{list()}";	
					return false;
				});
				
				app.datatable.bind("select", function(event, prop){
					if ((jQuery.trim(prop.bean.recordStatus) == '${recordStatusNew}')|| (jQuery.trim(prop.bean.recordStatus) == '${recordStatusUpdate}')){
						location.href='@{view()}/?id=' + prop.bean.marketPriceKey;
					}else{
						location.href='@{edit()}/?id=' + prop.bean.marketPriceKey;
					}
//					
				});
				
				app.datatable.bind("selects", function(event, props){
					alert("a");
					for (x in props) {
						alert(x);
						alert(props.length+"  "+props[x].bean.customerNo+"-"+props[x].row[1]);
					}
				});
				
				
				
				function validate(){
					var marketDateFrom= $('#marketPriceSearchFromError').required(app.marketPriceSearchFrom.isEmpty());
					var marketDateTo = $('#marketPriceSearchToError').required(app.marketPriceSearchTo.isEmpty());
					var groupCode = $('#errGroupCode').required(app.groupCode.isEmpty());
				
					return marketDateFrom && marketDateTo && groupCode;
				}
		/*var app = html.inject(this, true);
		app.groupCode.popupLookup('${securityPrice}','');*/
	}else{
		return new MarketPrices(html);
	}
	
}