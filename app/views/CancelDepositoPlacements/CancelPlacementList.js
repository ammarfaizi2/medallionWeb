function Paging(html) {
	if (this instanceof Paging) {
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
			
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.dateFrom = app.placementDateFrom.val();
			p.dateTo = app.placementDateTo.val();
			p.customerCode = app.customerCodeKey.val();
			p.securityType = app.securityTypeId.val();
			p.securityCode = app.securityCodeId.val();
			p.depositoNoOperator = app.depositoNoOperator.val();
			p.depositoNo = app.depositoSearchNo.val();
			return p;
		};
			
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.depositoNoOperator.children().eq(0).remove();
		
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		$('#newDataDeposito').button();
		
		app.datatable = app.tabledeposito.paging("@{CancelDepositoPlacements.search()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.customerCode.blur(function(){
			if ($(this).val()==''){
				app.customerCode.removeClass('fieldError');
				app.customerCode.val('');
				app.customerCodeKey.val('');
				app.customerCodeDesc.val('');
				app.h_customerCodeDesc.val('');
			}
		});

		app.customerCode.dynapopup('PICK_CF_MASTER', '', 'securityType');
		//app.customerCode.popupCustomer('');
		
		app.securityType.blur(function(){
			if ($(this).val()==''){
				app.securityType.removeClass('fieldError');
				app.securityTypeId.val('');
				app.securityTypeName.val('');
				app.h_securityTypeName.val('');
				
				app.securityCode.val('');
				app.securityCodeId.val('');
				app.securityCodeName.val('');
				app.h_securityCodeName.val('');
				
				attachSecuritiesPaging();
			}
		});
		
		/*$('#securityType').lookup({
			list:'@{Pick.securityTypesDeposito()}',
			get:{
				url:'@{Pick.securityTypeDeposito()}',
				success: function(data){
					app.securityType.removeClass('fieldError');
					app.securityTypeId.val(data.code);
					app.securityTypeName.val(data.description);
					app.h_securityTypeName.val(data.description);
					app.securityCode.val('');
					app.securityCodeId.val('');
					app.securityCodeName.val('');
					app.h_securityCodeName.val('');
					attachSecuritiesPaging();
					},
				error: function(data){
					app.securityType.addClass('fieldError');
					app.securityType.val('');
					app.securityTypeId.val('');
					app.securityTypeName.val('NOT FOUND');
					app.h_securityTypeName.val('');
					
					app.securityCode.val('');
					app.securityCodeId.val('');
					app.securityCodeName.val('');
					app.h_securityCodeName.val('');
					attachSecuritiesPaging();
				}
			},
			description:$('#securityTypeName'),
			//filter:'${depositoTemplatePlacement}',
			help:$('#securityTypeHelp'),
			nextControl:$('#securityCode')
		});*/
		
		app.securityType.dynapopup2({key:'securityTypeId', help:'securityTypeHelp', desc:'securityTypeName'}, 'PICK_SC_TYPE_MASTER_DEPOSITO', '', 'securityCode', 
				function(){ attachSecuritiesPaging(); },
				function(){ attachSecuritiesPaging(); }
			);
		
		app.securityCode.blur(function(){
			if ($(this).val()==''){
				app.securityCode.removeClass('fieldError');
				app.securityCode.val('');
				app.securityCodeId.val('');
				app.securityCodeName.val('');
				app.h_securityCodeName.val('');
			}
		});
		
		function attachSecuritiesPaging() {
			
			app.securityCode.val('');
			app.securityCodeId.val('');
			app.securityCodeName.val('');
			app.h_securityCodeName.val('');
			
			var securityType = $('#securityType').val();
			var pickName = (securityType == '') ? 'PICK_SC_MASTER_TD' : 'PICK_SC_MASTER_BY_SEC_TYPE';
			$('#securityCode').dynapopup2({key:'securityCodeId', help:'securityCodeHelp', desc:'securityCodeName'}, pickName, securityType, 'depositoSearchNo', function(data){
				app.securityCode.removeClass('fieldError');
				app.securityCodeId.val(data.code);
				app.securityCodeName.val(data.description);
				app.h_securityCodeName.val(data.description);
			},function(data){
				app.securityCode.addClass('fieldError');
				app.securityCode.val('');
				app.securityCodeId.val('');
				app.securityCodeName.val('NOT FOUND');
				app.h_securityCodeName.val('');
			});
		}
		
		attachSecuritiesPaging();
		
		app.placementDateFrom.change(function(){
			var dateFrom = $(this).datepicker('getDate');
			var dateTo = $('#placementDateTo').datepicker('getDate');
			var origin = 'from';
			var id = '#placementDate';
			if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#placementDateTo').val()!=''))
				//compareDateFromTo(dateFrom, dateTo, origin, id);
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
			//validateDate("From");
		});
		
		app.placementDateTo.change(function(){
			var dateFrom = $('#placementDateFrom').datepicker('getDate');
			var dateTo = $(this).datepicker('getDate');
			var origin = 'to';
			var id = '#placementDate';
			if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#placementDateFrom').val()!=''))
				//compareDateFromTo(dateFrom, dateTo, origin, id);
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
			//validateDate("To");
		});
		
//		var validateDate = function(from) {
//			var DateFrom = new Date($('#placementDateFrom').datepicker('getDate'));
//			var DateTo = new Date($('#placementDateTo').datepicker('getDate'));
//			if (($('#placementDateFrom').val()!='') && ($('#placementDateTo').val()!='')) { 
//				if (DateTo.getTime() < DateFrom.getTime()) {
//					if (from=='From'){
//						$('#placementDateFrom').addClass('fieldError');
//						$("#placementDateFromError").html("Date From must be less or equal than Date To");
//					} else {
//						$('#placementDateTo').addClass('fieldError');
//						$("#placementDateToError").html("Date To must be greather or equal than Date From");
//					}
//				} else {
//					$('#placementDateTo').removeClass('fieldError');
//					$("#placementDateToError").html("");
//					$('#placementDateFrom').removeClass('fieldError');
//					$("#placementDateFromError").html("");
//				}
//			}
//		};
		
		app.search.click(function(){
			if ((app.placementDateFrom.hasClass('fieldError')) || (app.placementDateTo.hasClass('fieldError')) || 
				(app.placementDateTo.val()=='') || (app.placementDateFrom.val()=='')){
					
					if (app.placementDateFrom.val()=='') {
						app.placementDateFromError.html('Required');
					} else {
						app.placementDateFromError.html('');
					}
					
					if (app.placementDateTo.val()=='') {
						app.placementDateToError.html('Required');
					} else {
						app.placementDateToError.html('');
					}
					
					return false;
				} else {
					$('#result').css('display', '');
					app.datatable.search.val("");
					app.datatable.fnPageChange("first");
					return false;
				}
		});

		app.reset.click(function(){
			location.href="@{list()}";	
		});

		app.datatable.bind("select", function(event, prop){
			location.href='@{edit()}?id='+prop.bean.transactionKey;
			//alert(prop.bean.customerNo+"-"+prop.row[1]+"-"+prop.tr);
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				//alert(props.length+"  "+props[x].bean.customerNo+"-"+props[x].row[1]);
			}
		});
			
	}else{
		return new Paging(html);
	}
}