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
			p.navSearchNavDateFrom = $('#navDateFrom').val();
			p.navSearchNavDateTo = $('#navDateTo').val();
			p.navSearchProductCode = $('#searchFundCode').val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		$("#root").add("#result").inject(this, true).root.accordion({collapsible: true});
		
		$('#search').button();
		$('#reset').button();
		$('#newDataNAVPrice').button();
		
		app.datatable = app.tableNAVPrice.paging("@{RegistryNav.search()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		$('.calendar').datepicker();
		
		$('#navDateFrom').change(function(){
			var dateFrom = $(this).datepicker('getDate');
			var dateTo = $('#navDateTo').datepicker('getDate');
			var origin = 'from';
			var id = '#navDate';
			if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#navDateTo').val()!='')) {
				//compareDateFromTo(dateFrom, dateTo, origin, id);
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
			}

			//validateDate("From");
			var checkError = $("input").hasClass('fieldError');
			if (checkError){
				$('#search').button('disable');
			}  else {
				$('#search').button('enable');
			}
		});
		
		$('#navDateTo').change(function(){
			var dateFrom = $('#navDateFrom').datepicker('getDate');
			var dateTo = $(this).datepicker('getDate');
			var origin = 'to';
			var id = '#navDate';
			if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#navDateFrom').val()!='')) {
				//compareDateFromTo(dateFrom, dateTo, origin, id);
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
			}

			//validateDate("To");
			var checkError = $("input").hasClass('fieldError');
			if (checkError){
				$('#search').button('disable');
			}  else {
				$('#search').button('enable');
			}
		});

//		var validateDate = function(from){
//			var DateFrom = new Date($('#navDateFrom').datepicker('getDate'));
//			var DateTo = new Date($('#navDateTo').datepicker('getDate'));
//			if (($('#navDateFrom').val()!='') && ($('#navDateTo').val()!='')) { 
//				if (DateTo.getTime() < DateFrom.getTime()) {
//					if (from=='From'){
//						$('#navDateFrom').addClass('fieldError');
//						$("#navDateFromError").html("Date From must be less or equal than Date To ");
//					} else {
//						$('#navDateTo').addClass('fieldError');
//						$("#navDateToError").html("Date To must be greather or equal than Date From");
//					}
//				} else {
//					$('#navDateTo').removeClass('fieldError');
//					$("#navDateToError").html("");
//					$('#navDateFrom').removeClass('fieldError');
//					$("#navDateFromError").html("");
//				}
//			}
//		};

		$('#searchFundCode').lookup({
			list:'@{Pick.rgProducts()}',
			get:{
				url:'@{Pick.rgProduct()}',
				success: function(data){
					$('#searchFundCode').removeClass('fieldError');
					$('#searchFundCodeKey').val(data.code);
					$('#searchFundCodeDesc').val(data.description);
					$('#h_searchFundCodeDesc').val(data.description);
					
					//$('#amountRoundValueLs').val(data.amountRoundValue);
					//$('#amountRoundTypeLs').val(data.amountRoundType);
				},
				error: function(data){
					$('#searchFundCode').addClass('fieldError');
					$('#searchFundCode').val('');
					$('#searchFundCodeKey').val('');
					$('#searchFundCodeDesc').val('NOT FOUND');
					$('#h_searchFundCodeDesc').val('');
				}
			},
			key:$('#searchFundCodeKey'),
			description:$('#searchFundCodeDesc'),
			help:$('#searchFundCodeHelp')
		});
		
		$('#search').click(function() {
			if (($('#navDateFrom').val()=='') || ($('#navDateTo').val()=='')) {
				if (($('#navDateFrom').val()=='')) {
					$("#navDateFromError").html("Required");
				} else {
					$("#navDateFromError").html('');
				}
				
				if (($('#navDateTo').val()=='')) {
					$("#navDateToError").html('Required');
				} else {
					$("#navDateToError").html('');
				}
				return false;
			} else {
				$('#result').css('display', '');
				app.datatable.search.val("");
				app.query.val(true);
				app.datatable.fnPageChange("first");
				return false;
			}
		});
		
		$('#reset').click(function() {
			location.href="@{list()}";
		});

		$('#newDataNAVPrice').click(function(){
			location.href="@{entry()}/?isNewRec=true";
		});
		
		app.datatable.bind("select", function(event, prop){
			if ((jQuery.trim(prop.bean.recordStatus) == '${recordStatusNew}')|| (jQuery.trim(prop.bean.recordStatus) == '${recordStatusUpdate}')){
				location.href='@{view()}/?productCode=' + prop.bean.id.productCode +'&navDate=' + $.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(prop.bean.id.navDate));
			}else{
				location.href='@{edit()}/?productCode=' + prop.bean.id.productCode +'&navDate=' + $.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(prop.bean.id.navDate));
			}
//			
		});
		
	}else{
		return new Paging(html);
	}
}