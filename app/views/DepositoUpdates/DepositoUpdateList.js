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
			return p;
		};
			
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.depositoNoOperator.children().eq(0).remove();
		
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		/*$('#newDataDeposito').button();*/
		
		app.datatable = app.tabledeposito.paging("@{DepositoUpdates.search()}", this);

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
		//app.customerCode.popupCustomer('');
		app.customerCode.dynapopup('PICK_CF_MASTER', '', 'securityType');
		
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
			}
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
					},
				error: function(data){
					app.securityType.addClass('fieldError');
					app.securityType.val('');
					app.securityTypeId.val('');
					app.securityTypeName.val('NOT FOUND');
					app.h_securityTypeName.val('');
				}
			},
			description:$('#securityTypeName'),
//			filter:'${deposito?.depositoTemplate?.lookupId}',
			help:$('#securityTypeHelp'),
			nextControl:$('#securityCode')
		});
		
		app.securityCode.blur(function(){
			if ($(this).val()==''){
				app.securityCode.val('');
				app.securityCodeId.val('');
				app.securityCodeName.val('');
				app.h_securityCodeName.val('');
			}
		});
		
		$('#securityCode').lookup({
			list:'@{Pick.securities()}',
			get:{
				url:'@{Pick.security()}',
				success: function(data){
					app.securityCode.removeClass('fieldError');
					app.securityCodeId.val(data.code);
					app.securityCodeName.val(data.description);
					app.h_securityCodeName.val(data.description);
				},
				error: function(data){
					app.securityCode.addClass('fieldError');
					app.securityCode.val('');
					app.securityCodeId.val('');
					app.securityCodeName.val('NOT FOUND');
					app.h_securityCodeName.val('');
				}
			},
			description:$('#securityCodeName'),
			filter:$('#securityType'),
			help:$('#securityCodeHelp')
		});
		
		app.search.click(function(){
			if (app.customerCodeKey.val()==''){
				$('#customerCodeError').html('Required');
				return false;
			} else {
				$('#customerCodeError').html('');
				$('#result').css('display', '');
				app.datatable.search.val("");
				app.datatable.fnPageChange("first");
				return false;
			}
		});

		app.reset.click(function(){
			location.href="@{list()}";	
		});

		/*$('#newDataDeposito').click(function(){
			location.href="@{entry()}";
		});*/
		
		app.datatable.bind("select", function(event, prop){
			location.href='@{edit()}?id='+prop.bean.depositoKey;
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