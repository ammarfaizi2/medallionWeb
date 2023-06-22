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
		
		app.datatable = app.tabledeposito.paging("@{DepositoPlacements.search()}", this);

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
		});
		
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
			location.href='@{edit()}?id='+prop.bean.deposito.depositoKey;
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