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
		
		app.datatable = app.tabledeposito.paging("@{DepositoBreaks.paging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.customerCode.change(function(){
			if ($(this).val()==''){
				app.customerCode.removeClass('fieldError');
				app.customerCode.val('');
				app.customerCodeId.val('');
				app.customerCodeName.val('');
				app.h_customerCodeName.val('');
			}
		});
		//app.customerCode.popupCustomer('');
		app.customerCode.dynapopup('PICK_CF_MASTER', '', 'securityType');
		
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
//			filter:'${depositoTemplateBreak}',
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
			var pickName = (securityType == '') ? 'PICK_SC_MASTER' : 'PICK_SC_MASTER_BY_SEC_TYPE';
			$('#securityCode').dynapopup2({key:'securityCodeId', help:'securityCodeHelp', desc:'securityCodeName'}, pickName, securityType, 'depositoNoOperator', function(data){
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
		
		app.placementDateFrom.change(function(){
			$("#pPlacementDateFrom label span").remove();
	        if($(this).val() != '' || $('#placementDateTo').val() != ''){
				$("#pPlacementDateFrom label").append("<span class='req'>*</span>");
			}
		});
		
		app.placementDateTo.change(function(){
			$("#pPlacementDateFrom label span").remove();
	        if($(this).val() != '' || $('#placementDateFrom').val() != ''){
				$("#pPlacementDateFrom label").append("<span class='req'>*</span>");
			}
		});
		
		app.search.click(function(){
			var toPaging = true;
			if(($("#placementDateFrom").val() == "") && ($("#placementDateTo").val() == "")){
				$('#placementDateFrom').removeClass('fieldError');
				$('#placementDateTo').removeClass('fieldError');
				$("#placementDateFromError").html("");
				$("#placementDateToError").html("");
			}else{
				if(($("#placementDateFrom").val() != "")){
					if(($("#placementDateTo").val() == "")){
						$("#placementDateToError").html(" Required");
						$('#placementDateTo').addClass('fieldError');
						toPaging = false;
					} else {
						$("#placementDateToError").html("");
						$('#placementDateTo').removeClass('fieldError');
					}
				}
				
				if(($("#placementDateTo").val() != "")){
					if(($("#placementDateFrom").val() == "")){
						$("#placementDateFromError").html(" Required");
						$('#placementDateFrom').addClass('fieldError');
						toPaging = false;
					} else {
						$("#placementDateFromError").html("");
						$('#placementDateFrom').removeClass('fieldError');
					}
				}
				
				if (($('#placementDateFrom').val() != '') && ($('#placementDateTo').val() != '')) {
					var dateFrom = $('#placementDateFrom').datepicker('getDate');
					var dateTo = $('#placementDateTo').datepicker('getDate');
					var origin = 'from';
					var id = '#placementDate';
					
					compareDateFromToEqual(dateFrom, dateTo, origin, id);
					if($('#placementDateTo').hasClass('fieldError') || $('#placementDateFrom').hasClass('fieldError')){
						toPaging = false;
					}
				}
				
			}
			if(toPaging){
				$('#result').css('display', '');
				app.datatable.search.val("");
				app.datatable.fnPageChange("first");
				return false;
			}
		});

		app.reset.click(function(){
			location.href="@{list()}";	
		});

		$('#newDataDeposito').click(function(){
			location.href="@{entry()}";
		});
		
		app.datatable.bind("select", function(event, prop){
			if (($.trim(prop.bean.deposito.recordStatus) == 'N')||($.trim(prop.bean.deposito.recordStatus) == 'U')){
				location.href='@{view()}?id='+prop.bean.transactionKey;
			} else {
				location.href='@{edit()}?id='+prop.bean.transactionKey;
			}
			
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