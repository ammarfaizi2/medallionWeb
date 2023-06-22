$(function() {
	// Search section
	$('#accordion').accordion({
		collapsible: true
	});

	$('.buttons #new').show();
	$('#birthDate').datepicker();
	$('.calendar').datepicker();
	$('.buttons button:first').button({
		icons: {
			primary: 'ui-icon-search'
		}
	}).next().button();	
	/*$('#search').click(function() {
		$('#accountNoError').html('');
		$('#securityTypeError').html('');
		$('#securityCodeError').html('');
		$('#result .response').hide();
		$('#buttonNotInList').hide();
		if ('${param}' == 'dedupe') {
			if (($('#csCertificateFrom').val()=='') || ($('#csCertificateTo').val()=='')){
				if ($('#csCertificateFrom').val()==''){
					$('#certificateFromError').html("Required");
				}
				if ($('#csCertificateTo').val()==''){
					$('#certificateToError').html("Required");
				}
				return false
			}else{
				search();
			}
			
			
			 if (($('#accountNo').val() == "") || ($('#securityType').val() == "") || ($('#securityCode').val() == "")) {
				if($('#accountNo').val() == ""){
					$('#accountNoError').html('Required');
				}
				if($('#securityType').val() == ""){
					$('#securityTypeError').html('Required');
				}
				if($('#securityCode').val() == ""){
					$('#securityCodeError').html('Required');
				}
			} else {
				search();
			} 
		} else {
			search();
		}
	});*/
	
	/*$('#csCertificateFrom').change(function(){
        var dateFrom = $(this).datepicker('getDate');
        var dateTo = $('#csCertificateTo').datepicker('getDate');
        var origin = 'from';
        var id = '#csCertificate';
        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#csCertificateTo').val()!='')) {
        	compareDateFromToEqual(dateFrom, dateTo, origin, id);
        }
        
    });
	
	$('#csCertificateTo').change(function(){
        var dateFrom = $('#csCertificateFrom').datepicker('getDate');
        var dateTo = $(this).datepicker('getDate');
        var origin = 'to';
        var id = '#csCertificate';
        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#csCertificateFrom').val()!='')) {
        	compareDateFromToEqual(dateFrom, dateTo, origin, id);
        }
    });*/
	
	// end of Search
	/*$('#reset').click(function() {
		#{if dedupe}
			location.href="@{Certificates.dedupe()}";
		#{/if}
		#{else}
			location.href="@{Certificates.list()}?mode=${mode}#{if param}&param=${param}#{/if}";
		#{/else}			
	});
	*/
//	$('#notInList').change(function() {
//		if ($('#notInList').is(':checked')) 
//	    	$('.buttons #new').show();
//		else 
//			$('.buttons #new').hide();
//	});
	
	$('.buttons input:button').button();
	$('.buttons input:button').button();
				 
	$('.buttons #new').click(function() {
		//$.get('@{entry()}', $('#searchForm').serialize(), function(data) {

		//});
		//location.href="@{Certificates.entry()}?id="+$('#securityKey').val()+'&key='+$('#accountKey').val()+'&certificateId='+$('#certificateId').val();
		location.href="@{Certificates.entry()}?id="+$('#securityKey').val()+'&certificateId='+$('#certificateId').val();
	});

	$('#customerCode').change(function(){
		if ($(this).val()==''){
			$('#customerCode').removeClass('fieldError');
			$('#customerCode').val('');
			$('#customerCodeId').val('');
			$('#customerDesc').val('');
			$('#h_customerDesc').val('');
		}
	});
    
	$('#customerCode').lookup({
		list : '@{Pick.customerAccounts()}',
		get : {
			url: '@{Pick.customerAccount()}',
			success: function(data) {
				$('#customerCode').removeClass('fieldError');
				$('#customerCodeId').val(data.code);
				$('#customerCode').val(data.accountNo);
				$('#customerDesc').val(data.description);
				$('#h_customerDesc').val(data.description);
			},
			error: function(data) {
				$('#customerCode').addClass('fieldError');
				$('#customerCode').val('');
				$('#customerCodeId').val('');
				$('#customerDesc').val('NOT FOUND');
				$('#h_customerDesc').val('');
			}
		},
		description:$('#customerDesc'),
		help : $('#customerCodeHelp')
	});
	
	var securityCodeFilter = "";
	if ($('#securityType').val() != "") {
		securityCodeFilter = $('#securityType');
	}
	securityCode(securityCodeFilter);
	
	$('#securityType').blur(function() {
		if (($('#securityType').val() == "") || ($('#securityType').isChange())) {
			$('#securityKey').val("");
			$('#securityCode').val("");
			$('#securityCodeDesc').val("");
			$('#h_securityCodeDesc').val("");
			$('#h_securityTypeDesc').val("");
			securityCode($('#securityType'));
		}
	}); 
	
	$('#securityType').lookup({
			list:'@{Pick.securityTypesDeposito()}',
			get:{
				url:'@{Pick.securityTypeDeposito()}',
				success: function(data){
						$('#securityType').removeClass('fieldError');
						$('#securityTypeDesc').val(data.description);
						$('#h_securityTypeDesc').val(data.description);
						securityCode($('#securityType'));
				},
				error: function(data){
					$('#securityType').addClass('fieldError');
					$('#securityType').val('');
					$('#securityTypeDesc').val('NOT FOUND');
					$('#h_securityTypeDesc').val('');
				}
		},
		//filter:$('#securityType'),
		description:$('#securityTypeDesc'),
		help:$('#securityTypeHelp')
	});

	if ('${param}' != 'dedupe') {
		$("p[id=pAccountNo] label span").html("");
		$("p[id=pSecurityType] label span").html("");
		$("p[id=pSecurityCode] label span").html("");
		$("p[id=pCertificateId] label span").html("");
	}
	
});

function doEdit(data) {
	if ((data[5] == "New") || (data[5] == "Updated")) {
		view(data[0]);
		return false;
	} else {
		return true;
	}
}

/*var search = function() {
	$('#result .loading').show();
	$('#result .response').hide();
	$('#result').show();
	$.get('@{search()}', $('#searchForm').serialize(), function(data) {
		$('#result .response').html(data);
		$('#result .loading').hide();
		$('#result .response').show();
		$('#buttonNotInList').show();	
		setupTable();
	});
	app.datatable.fnPageChange("first");
	
	app.result.show();
//	app.btnGenerate.attr("disabled",false);
	app.datatable.fnAdjustColumnSizing();
};*/

function securityCode(pointer) {
		var securityType =$('#securityType').val();
		var pickName = (securityType == '') ? 'PICK_SC_MASTER' : 'PICK_SC_MASTER_BY_SEC_TYPE';
		$('#securityCode').dynapopup2({key:'securityKey', help:'securityCodeHelp', desc:'securityCodeDesc'}, pickName, securityType, 'csCertificateCertificateId', function(data){
			if (data) {
				$('#securityCode').removeClass('fieldError');
				$('#securityKey').val(data.code);
				$('#securityCodeDesc').val(data.description);
				$('#h_securityCodeDesc').val(data.description);
				$('#certificateCurrency').val(data.currency);
				
			}
		},function(data){
			$('#securityCode').addClass('fieldError');
			$('#securityCodeDesc').val('NOT FOUND');
			//$('#securityKey').val(data.code);
			$('#securityKey').val('');
			$('#securityCode').val('');
			$('#h_securityCodeDesc').val('');
			$('#certificateCurrency').val('');
		});
//	$('#securityCode').lookup({
//		list:'@{Pick.securities()}',
//		get: {
//			url:'@{Pick.security()}',
//			success: function(data) {
//				if (data) {
//					$('#securityCode').removeClass('fieldError');
//					$('#securityKey').val(data.code);
//					$('#securityCodeDesc').val(data.description);
//					$('#h_securityCodeDesc').val(data.description);
//					$('#certificateCurrency').val(data.currency);
//					
//				}
//			},
//			error: function(data) {
//				$('#securityCode').addClass('fieldError');
//				$('#securityCodeDesc').val('NOT FOUND');
//				//$('#securityKey').val(data.code);
//				$('#securityKey').val('');
//				$('#securityCode').val('');
//				$('#h_securityCodeDesc').val('');
//				$('#certificateCurrency').val('');
//			}
//		},
//		description:$('#securityCodeDesc'),
//		filter:data,
//		help:$('#securityCodeHelp')
//	});
}