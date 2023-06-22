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
			p.customerCode = app.customerCode.val();
			p.securityType = app.securityType.val();
			p.securityCode = app.securityCode.val();
			p.typeId = app.typeId.val();
			p.strDateFrom = app.dateFrom.val();
			p.strDateTo = app.dateTo.val();
			return p;
		};
	
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		
		app.datatable = app.tabledeposito.paging("@{DepositoLetter.search()}", this);
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		app.customerCode.change(function(){
			if ($(this).val()==''){
				app.customerCode.removeClass('fieldError');
				app.customerCode.val('');
				app.customerCodeKey.val('');
				app.customerCodeDesc.val('');
				app.h_customerCodeDesc.val('');
			}
		});
		
		app.customerCode.dynapopup('PICK_CF_MASTER', '', 'securityType');
		/*app.customerCode.dynapopup2({key:'customerCodeKey',help:'customerCodeHelp',desc:'customerCodeDesc'},'PICK_CF_MASTER','','customerCode',
				function(data){
					if(data){
						app.customerCode.removeClass('fieldError');
						app.customerCode.val(data.customerNo);
						app.customerCodeKey.val(data.code);
						app.customerCodeDesc.val(data.description);
					}
				},function(data){
					app.customerCode.addClass('fieldError');
					app.customerCode.val('');
					app.customerCodeKey.val('');
					app.customerCodeDesc.val('NOT FOUND');
				}
			);*/
		
		app.securityType.change(function(){
			if ($(this).val()==''){
				app.securityTypeId.val('');
				app.securityTypeName.val('');
				app.h_securityTypeName.val('');
				
				app.securityCode.val('');
				app.securityCodeId.val('');
				app.securityCodeName.val('');
				app.h_securityCodeName.val('');
			}
			attachSecuritiesPaging();
		});
		
		$('#securityType').lookup({
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
					app.securityType.change();
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
					app.securityType.change();
				}
			},
			description:$('#securityTypeName'),
			help:$('#securityTypeHelp'),
			nextControl:$('#securityCode')
		});
		
		app.securityCode.change(function(){
			if ($(this).val()==''){
				app.securityCode.val('');
				app.securityCodeId.val('');
				app.securityCodeName.val('');
				app.h_securityCodeName.val('');
			}
		});
		
		function attachSecuritiesPaging() {
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
		
		app.type.change(function(){
			if ($(this).val()==''){
				app.type.val('');
				app.typeId.val('');
				app.typeDesc.val('');
			}
		});
		
		function attachTypePaging(){
			$("#type").dynapopup2({key:'typeId',help:'typeHelp',desc:'typeDesc'},'PICKRPT_PRINT_LISTOFDEP_TYPE','','type',
				function(data){
					if(data){
						app.type.removeClass('fieldError');
						app.type.val(data.code);
						app.typeId.val(data.code);
						app.typeDesc.val(data.description);
					}
				},function(data){
					app.type.addClass('fieldError');
					app.type.val('');
					app.typeId.val('');
					app.typeDesc.val('NOT FOUND');
				}
			);
		}
		attachTypePaging();

		function validateSearch() {
			$('#dateToError').html('');
			$('#dateFromError').html('');
			$('#dateFrom').removeClass('fieldError');
			$('#dateTo').removeClass('fieldError');
			
			if (($('#dateFrom').val() == '') && ($('#dateTo').val() == '')) {
				$('#dateToError').html('');
				$('#dateFromError').html('');
				$('#dateFrom').addClass('fieldError');
				$('#dateTo').addClass('fieldError');
				app.dateToError.html('Required');
				return false;
			} else {
				if ($('#dateFrom').val() != '') {
					if ($('#dateTo').val() == '') {
						$('#dateToError').html('Required');
						return false;
					} else {
						$('#dateToError').html('');
					}
					if($('#dateFrom').hasClass('fieldError')){
						return false;
					}
				}
				if ($('#dateTo').val() !='') {
					if ($('#dateFrom').val() == '') {
						$('#dateFromError').html('Required');
						return false;
					} else {
						$('#dateFromError').html('');
					}
					
					if($('#dateTo').hasClass('fieldError')){
						return false;
					}
				}
				
				if (($('#dateFrom').val() != '') && ($('#dateTo').val() != '')) {
					var dateFrom = $('#dateFrom').datepicker('getDate');
					var dateTo = $('#dateTo').datepicker('getDate');
					var origin = 'from';
					var id = '#date';
					
					compareDateFromToEqual(dateFrom, dateTo, origin, id);
					if($('#dateTo').hasClass('fieldError') || $('#dateFrom').hasClass('fieldError')){
						return false;
					}
				}
			}
			
			return true;
		}
		
		var search = function() {
			var valid = true;
			app.customerCodeError.html('');
			if ((app.customerCode.hasClass('fieldError')) || (app.customerCode.val() == '')) {
				app.customerCodeError.html('Required');
				valid = false;
			}
			
			if(!validateSearch())valid = false;
			return valid;
		}
		
		app.search.click(function(){
			var validateSearch = search();
			if (!validateSearch) {
				return;
			}
			$('#result').css('display', '');
			app.datatable.search.val("");
			app.datatable.fnPageChange("first");
			
			return false;
		});

		app.reset.click(function(){
			location.href="@{list()}";	
		});

		/*function setDefaultVal(){
			app.customerCode.val("CUSTI001-000001").blur();
			app.dateFrom.val("30/01/2018").blur();
			app.dateTo.val("30/01/2018").blur();
		}
		setDefaultVal();
		app.search.click();*/
		
		app.datatable.bind("select", function(event, prop){
			var transNo = prop.bean.transactionNo;
			var custCode = app.customerCode.val();
			var typeId = app.typeId.val();
			var secCode = app.securityCode.val();
			var dtFrom = app.dateFrom.val();
			var dtTo = app.dateTo.val();
			var newIntRateParam = prop.bean.newIntRate;
			var settlementAmountList = prop.bean.settlementAmount;
			var lookupDescription = prop.bean.lookupDescription;
			
			location.href='@{view()}?transNo='+transNo+'&custCode='+custCode+'&type='+typeId+'&secCode='+secCode+'&dtFrom='+dtFrom+'&dtTo='+dtTo+'&newIntRateParam='+newIntRateParam+'&settlementAmountList='+settlementAmountList+'&lookupDescription='+lookupDescription;
		});
		
	}else{
		return new Paging(html);
	}
}