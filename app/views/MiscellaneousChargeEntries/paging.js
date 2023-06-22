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
			p.miscellaneousFrom = app.miscellaneousFrom.val();
			p.miscellaneousTo = app.miscellaneousTo.val();
			p.customerCodeId = app.customerKey.val();
			p.chargeKey = app.chargeKey.val();
			p.transactionNoOperator = app.transactionNoOperator.val();
			p.transactionId = app.transactionId.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		
		if ('${menuEntry}'){
			app.newData.button();
		}
		
		app.result.hide();
		
		if ('${menuEntry}'){
			app.datatable = app.tblMiscellaneous.paging("@{MiscellaneousChargeEntries.paging()}", this);
		}
		
		if ('${menuCancel}'){
			app.datatable = app.tblMiscellaneous.paging("@{MiscellaneousChargeCancel.paging()}", this);
		}
		
		app.transactionNoOperator.children().eq(0).remove();
/* =========================================================================== 
 * Event
 * ========================================================================= */
	
		app.reset.click(function(){
			location.href="@{list()}";	
		});

		app.datatable.bind("select", function(event, prop){
			if ('${menuEntry}'){
				if ((jQuery.trim(prop.bean.recordStatus) == '${recordStatusNew}')|| (jQuery.trim(prop.bean.recordStatus) == '${recordStatusUpdate}') || (jQuery.trim(prop.bean.recordStatus) == "N")|| (jQuery.trim(prop.bean.recordStatus) == "U")){
					location.href='@{view()}/?id=' + prop.bean.transactionKey;
				}else{
					location.href='@{edit()}/?id=' + prop.bean.transactionKey;
				}
			}
			if ('${menuCancel}'){
				location.href='@{MiscellaneousChargeCancel.edit()}/?id=' + prop.bean.transactionKey;
			}
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				
			}
		});
		
		//app.customer.popupCustomer('','invMonth');
		app.customer.dynapopup('PICK_CF_MASTER', '', 'invMonth');
		
		if ('${menuEntry}'){
			app.newData.click(function(){
				location.href="@{MiscellaneousChargeEntries.entry()}";		
			});
		}
		
		
		app.search.click(function(){
			
			var toPaging = true;
			
			if(($("#miscellaneousFrom").val() == "") && ($("#miscellaneousTo").val() == "")){
				$('#miscellaneousFrom').removeClass('fieldError');
				$('#miscellaneousTo').removeClass('fieldError');
				$("#miscellaneousFromError").html("");
				$("#miscellaneousToError").html("");
			}else{
				if(($("#miscellaneousFrom").val() != "")){
					if(($("#miscellaneousTo").val() == "")){
						$("#miscellaneousToError").html(" Required");
						$('#miscellaneousTo').addClass('fieldError');
						toPaging = false;
					} else {
						$("#miscellaneousToError").html("");
						$('#miscellaneousTo').removeClass('fieldError');
					}
				}
				
				if(($("#miscellaneousTo").val() != "")){
					if(($("#miscellaneousFrom").val() == "")){
						$("#miscellaneousFromError").html(" Required");
						$('#miscellaneousFrom').addClass('fieldError');
						toPaging = false;
					} else {
						$("#miscellaneousFromError").html("");
						$('#miscellaneousFrom').removeClass('fieldError');
					}
				}
				
				if (($('#miscellaneousFrom').val() != '') && ($('#miscellaneousTo').val() != '')) {
					var dateFrom = $('#miscellaneousFrom').datepicker('getDate');
					var dateTo = $('#miscellaneousTo').datepicker('getDate');
					var origin = 'from';
					var id = '#miscellaneous';
					
					compareDateFromToEqual(dateFrom, dateTo, origin, id);
					if($('#miscellaneousTo').hasClass('fieldError') || $('#miscellaneousFrom').hasClass('fieldError')){
						toPaging = false;
					}
				}
				
			}
			if(toPaging){
				app.query.val(true);
				app.datatable.fnPageChange("first");
				app.result.show();
				app.datatable.fnAdjustColumnSizing();
			}
		});
		
		/*app.reset.click(function(){
			app.csCertificateFrom.val("");
			app.csCertificateTo.val("");
			app.customerCode.val("");
			app.customerCodeId.val("");
			app.customerDesc.val("");
			app.securityType.val("");
			app.securityTypeDesc.val("");
			app.securityCode.val("");
			app.securityCodeDesc.val("");
			app.securityKey.val("");
			app.certificateNoOperator.val("");
			app.csCertificateCertificateId.val("");
			app.datatable.fnPageChange("first");
			app.customerCode.removeClass('fieldError');
			app.securityType.removeClass('fieldError');
			app.securityCode.removeClass('fieldError');
			app.result.hide();
			$('#buttonNotInList').hide();
		});*/
		
		/*app.newData.button();
		
		app.newData.click(function(){
			location.href='@{entry()}#{if group}/${group}#{/}';
		});*/
		
		$('#chargeCode').lookup({
			list:'@{Pick.chargesMiscellaneousByCategoryTransaction()}',
			get: {
				url:'@{Pick.chargeByCategoryTransaction()}',
				success: function(data) {
					if (data) {
						$('#chargeCode').removeClass('fieldError');
						$('#chargeKey').val(data.code);
						$('#chargeCodeDesc').val(data.description);
						$('#h_chargeCodeDesc').val(data.description);
					}
				},
				error: function(data) {
					$('#chargeCode').addClass('fieldError');
					$('#chargeCode').val('');
					$('#chargeKey').val('');
					$('#chargeCodeDesc').val('NOT FOUND');
					$('#h_chargeCodeDesc').val('');		
				}
			},
			description:$('#chargeCodeDesc'),
			help:$('#chargeCodeHelp')
		});
		
		/*$('#chargeCode').blur(function() {
			
			if (($('#chargeCode').val() == "") || ($('#chargeCode').isChange())) {
				
				if ($('#chargeCode').val() == "") {
					$('#chargeCode').val("");
					$('#chargeKey').val("");
					$('#chargeCodeDesc').val("");
					$('#h_chargeCodeDesc').val("");
				}
			}
		});*/
		
		$('#chargeCode').change(function(){
			if ($(this).val()==''){
				$('#chargeCode').val("");
				$('#chargeCode').val("");
				$('#chargeKey').val("");
				$('#chargeCodeDesc').val("");
				$('#h_chargeCodeDesc').val("");
			}
		});
		$('#miscellaneousFrom').change(function(){
			$("#pMiscellaneousFrom label span").remove();
	        if($(this).val() != '' || $('#miscellaneousTo').val() != ''){
				$("#pMiscellaneousFrom label").append("<span class='req'>*</span>");
			}
	    });
		
		$('#miscellaneousTo').change(function(){
			$("#pMiscellaneousFrom label span").remove();
	        if($(this).val() != '' || $('#miscellaneousFrom').val() != ''){
				$("#pMiscellaneousFrom label").append("<span class='req'>*</span>");
			}
	    });
	}else{
		return new Paging(html);
	}
}
