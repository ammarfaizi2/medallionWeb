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
			p.customerCode = app.customerCodeKey.val();
			p.securityType = app.securityTypeId.val();
			p.securityCode = app.securityCodeId.val();
			p.depositoNoOperator = app.depositoNoOperator.val();
			p.depositoNo = app.depositoSearchNo.val();
			p.nominal = app.nominalHidden.val();
			return p;
		};
			
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		
		app.datatable = app.tabledeposito.paging("@{InquiryDeposito.search()}", this);

		app.nominalHidden.value(true);
		app.nominal1.attr("checked", true);
/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		$('input[name=nominal]').change(function(){
			if( app.nominal1.attr("checked") == true  ){
				app.nominalHidden.value(true);
			} else {
				app.nominalHidden.value(false);
			}
		});
		
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
		//app.customerCode.popupCustomer('');
		
		app.securityType.change(function(){
			if ($(this).val()==''){
				app.securityTypeId.val('');
				app.securityTypeName.val('');
				app.h_securityTypeName.val('');
				
				app.securityCode.val('');
				app.securityCodeId.val('');
				app.securityCodeName.val('');
				ap.h_securityCodeName.val('');
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
//		
//		$('#securityCode').lookup({
//			list:'@{Pick.securities()}',
//			get:{
//				url:'@{Pick.security()}',
//				success: function(data){
//					app.securityCode.removeClass('fieldError');
//					app.securityCodeId.val(data.code);
//					app.securityCodeName.val(data.description);
//					app.h_securityCodeName.val(data.description);
//				},
//				error: function(data){
//					app.securityCode.addClass('fieldError');
//					app.securityCode.val('');
//					app.securityCodeId.val('');
//					app.securityCodeName.val('NOT FOUND');
//					app.h_securityCodeName.val('');
//				}
//			},
//			description:$('#securityCodeName'),
//			filter:$('#securityType'),
//			help:$('#securityCodeHelp')
//		});
		
		var search = function() {
			app.customerCodeError.html('');
			if ((app.customerCode.hasClass('fieldError')) || (app.customerCode.val() == '')) {
				app.customerCodeError.html('Required');
				return false;
			}
			return true;
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
		
		app.datatable.bind("select", function(event, prop){
			location.href='@{view()}?id='+prop.bean.depositoKey;
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