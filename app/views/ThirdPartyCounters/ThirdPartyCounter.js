$(function(){
	if(('${mode}' != 'view') && ('${confirming}' != 'true') || ('${mode}' == 'edit')){
		if($('#participanFlag2').is(':checked')){
			validationParticipanFlag(false);
		}else{
			validationParticipanFlag(true);
		}
	}
	
	if('${confirming}' == 'true'){
		if($('#participanFlag2').is(':checked')){
			validationParticipanFlag(false);
		}else{
			validationParticipanFlag(true);
			$('#ctpParticipanAs').attr("disabled","disabled");
			$('#ctpParticipanCode').attr("disabled","disabled");
		}
	}
	
	if($("input[name='participanFlag']:checked").val()==false){
		validationParticipanFlag(false);
	}
	
	$('#participanFlag1').click(function(){
		validationParticipanFlag(true);
	});
	
	$('#participanFlag2').click(function(){
		validationParticipanFlag(false);
	});
	
	
	if (('${mode}'=='entry') || (('${mode}'=='edit') && (('${thirdParty?.recordStatus?.decodeStatus()}' == 'Reject') || ($("#status").val() == 'R' )))) {
		$('input[name=isActive]').attr("disabled", "disabled");
	}
	$("input[name='isActive']").change(function() {
		$("input[name='thirdParty.isActive']").val($("input[name='isActive']:checked").val());
	});
	
	$("input[name='participanFlag']").change(function() {
		$("input[name='thirdParty.ctpParticipantFlag']").val($("input[name='participanFlag']:checked").val());
	});
	
	$('#address1').change(function(){
		fillAddress();
	});
	$('#address2').change(function(){
		fillAddress();
	});
	$('#address3').change(function(){
		fillAddress();
	});
	
	if (!$('#cbestFlag').is(':checked')){
		$('p[id=pCbestCode] label span').html("");
		$('#cbestCode').disabled();	
	}
	
	if (!$('#biFlag').is(':checked')){
		$('p[id=pBiCode] label span').html("");
		$('p[id=pBiAgentCode] label span').html("");
		$('#biCode').disabled();
		$('#biAgentCode').disabled();
	}
	
	$('#cbestFlag').change(function(){
		if ($(this).is(":checked")){
			$('#cbestCode').enabled();
			$('#externalCode').enabled();
		} else {
			$('#cbestCode').disabled();
			$('#cbestCode').val('');
			$('#externalCode').disabled();
			$('#externalCode').val('');
			
		}
	});	
	
	$('#biFlag').change(function(){
		if ($(this).is(":checked")){
			$('p[id=pBiCode] label span').html(" *");
			$('p[id=pBiAgentCode] label span').html(" *");
			$('#biCode').enabled();
			$('#biAgentCode').enabled();
		} else {
			$('p[id=pBiCode] label span').html("");
			$('p[id=pBiAgentCode] label span').html("");
			$('#biCode').disabled();
			$('#biAgentCode').disabled();
			$('#biCode').val('');
			$('#biAgentCode').val('');
		}
	});	
	$('#address1CountryCode').blur(function(){
		if (($('#address1CountryCode').val() == "") || ($('#address1CountryCode').isChange())) {
			$('#address1CountryCode').removeClass('fieldError');
			$('#address1StateCode').val('');
			$('#address1StateCode').removeClass('fieldError');
			$('#address1State').val('');
			$('#address1StateDesc').val('');
			$('#h_address1StateDesc').val('');
			
			$('#address1AreaCode').val("");
			$('#address1AreaCode').removeClass('fieldError');
			$('#address1Area').val("");
			$('#address1AreaDesc').val("");
			$('#h_address1AreaDesc').val("");
			
			$('#address1Country').val('');
			$('#address1CountryDesc').val('');
			$('#h_address1CountryDesc').val('');
			state("_");
			area("_");
		}
	});
	
		$('#address1StateCode').blur(function(){
		if (($('#address1StateCode').val() == "") || ($('#address1StateCode').isChange())){
			$('#address1StateCode').removeClass('fieldError');
			$('#address1AreaCode').val("");
			$('#address1AreaCode').removeClass('fieldError');
			$('#address1Area').val("");
			$('#address1AreaDesc').val("");
			$('#h_address1AreaDesc').val("");
			
			$('#address1State').val('');
			$('#address1StateDesc').val('');
			$('#h_address1StateDesc').val('');
			area("_");
		}
	});
	
	
	$('#address1AreaCode').blur(function(){
		if ($('#address1AreaCode').val() == ""){
			$('#address1AreaCode').removeClass('fieldError');
			$('#address1AreaDesc').val("");
			$('#h_address1AreaDesc').val("");
			$('#address1Area').val("");
		}	
	});
	
	var stateFilter ="";
	if($('#address1CountryCode').val() != ""){
		stateFilter = "STATE";
	}
	state(stateFilter);
	
	var areaFilter ="";
	if($('#address1StateCode').val() != ""){
		areaFilter = "AREA";
	}
	area(areaFilter);
	
	// COUNTRY LOOKUP
	/*$('#address1CountryCode').lookup({
		list:'@{Pick.lookups()}?group=COUNTRY',
		get:{
			url:'@{Pick.lookup()}?group=COUNTRY',
			success: function(data){
				$('#address1CountryCode').removeClass('fieldError');
				$('#address1Country').val(data.code);
				$('#address1CountryDesc').val(data.description);
				$('#h_address1CountryDesc').val(data.description);
				state("STATE");
			},
			error: function(data){
				$('#address1CountryCode').addClass('fieldError');
				$('#address1Country').val('');
				$('#address1CountryCode').val('');
				$('#address1CountryDesc').val('NOT FOUND');
				$('#h_address1CountryDesc').val('');
			}
		},
		description:$('#address1CountryDesc'),
		help:$('#address1CountryHelp'),
		nextControl:$('#address1StateCode')
	});*/
	
	$('#address1CountryCode').dynapopup2({help:'address1CountryHelp', desc:'address1CountryDesc'},'PICK_GN_LOOKUP', "COUNTRY", "address1StateCode", 
			function(data){
				if (data) {
					$('#address1CountryCode').removeClass('fieldError');
					$('#address1Country').val(data.code);
					$('#address1CountryDesc').val(data.description);
					$('#h_address1CountryDesc').val(data.description);
					state("STATE");
					area("_");
				}
			},
			function(data){
				$('#address1CountryCode').addClass('fieldError');
				$('#address1Country').val('');
				$('#address1CountryCode').val('');
				$('#address1CountryDesc').val('NOT FOUND');
				$('#h_address1CountryDesc').val('');
			}
	);
	
	$('#bankCode').change(function(){
		if ($('#bankCode').val() == ""){
			$('#bankCode').removeClass('fieldError');
			$('#bankCodeName').val("");
			$('#h_bankCodeName').val("");
		}	
	});
	
	$('#bankCode').dynapopup('PICK_GN_THIRD_PARTY', "THIRD_PARTY-BANK", "bankAccountNo", 
			function(data){
				if (data) {
					$('#bankCode').removeClass('fieldError');
					//$('#newBankCode').val(data.name);
				}
			},
			function(data){
				$('#bankCode').addClass('fieldError');
				$('#bankCodeDesc').val('NOT FOUND');
				$('#bankCode').val('');
				$('#bankCodeKey').val('');
				$('#h_bankCodeDesc').val('');
			}
	); 
	/*$('#bankCode').popupThirdParties("?type=THIRD_PARTY-BANK", "bankAccountNo", function(data){
		$('#bankCode').removeClass('fieldError');
		//$('#newBankCode').val(data.name);
	}, function(data) {
		$('#bankCode').addClass('fieldError');
	});*/
	
	$('#currency').change(function(){
		if ($('#currency').val() == ""){
			$('#currency').removeClass('fieldError');
			$('#currencyDesc').val("");
			$('#h_currencyDesc').val("");
		}	
	});
	
	$('#currency').popupCurrencies("thirdPartyBranch", function(data){
		$('#currency').removeClass('fieldError');
	}, function(data){
		$('#currency').addClass('fieldError');
		$('#currencyDesc').val('NOT FOUND');
		$('#h_currencyDesc').val('');
	});
	
	if ($.browser.msie){
		$("#remarks[maxlength]").bind('input propertychange', function() {  
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    });
	}
});

function state(data) {
$('#address1StateCode').lookup({
	list:'@{Pick.lookups()}?group='+data,
	get:{
		url:'@{Pick.lookup()}?group='+data,
		success: function(data) {
			if (data) {
				if ($('#address1CountryCode').val() == "" || $('#address1CountryCode').val() == null) {
					$('#address1StateCode').addClass('fieldError');
					$('#address1StateDesc').val('NOT FOUND');
					$('#address1StateCode').val('');
					$('#address1State').val('');
					$('#h_address1StateDesc').val('');
					return false;
				}
				$('#address1StateCode').removeClass('fieldError');
				$('#address1State').val(data.code);
				$('#address1StateDesc').val(data.description);
				$('#h_address1StateDesc').val(data.description);
				area("AREA");
			}
		},
		error : function(data) {
			$('#address1StateCode').addClass('fieldError');
			$('#address1StateDesc').val('NOT FOUND');
			$('#address1StateCode').val('');
			$('#address1State').val('');
			$('#h_address1StateDesc').val('');
		}
	},
	filter: {'key':'1','value':$('#address1Country')},
	key:$('#address1State'),
	description:$('#address1StateDesc'),
	help:$('#address1StateHelp'),
	nextControl:$('#address1AreaCode')
});

}

function area(data) {
//AREA LOOKUP
$('#address1AreaCode').lookup({
		list:'@{Pick.lookups()}?group='+data,
		get:{
			url:'@{Pick.lookup()}?group='+data,
			success: function(data) {
				if (data) {
					if ($('#address1StateCode').val() == "" || $('#address1StateCode').val() == null) {
						$('#address1AreaCode').addClass('fieldError');
						$('#address1AreaDesc').val('NOT FOUND');
						$('#address1AreaCode').val('');
						$('#address1Area').val('');
						$('#h_address1AreaDesc').val('');
						return false;
					}
					$('#address1AreaCode').removeClass('fieldError');
					$('#address1Area').val(data.code);
					$('#address1AreaDesc').val(data.description);
					$('#h_address1AreaDesc').val(data.description);
				}
			},
			error : function(data) {
				$('#address1AreaCode').addClass('fieldError');
				$('#address1AreaDesc').val('NOT FOUND');
				$('#address1AreaCode').val('');
				$('#address1Area').val('');
				$('#h_address1AreaDesc').val('');
			}
		},
		filter:{'key':'2','value':$('#address1State')},
		key:$('#address1Area'),
		description:$('#address1AreaDesc'),
		help:$('#address1AreaHelp'),
		nextControl:$('#address1SubareaCode')
	});
}

function fillAddress(){
	var address1 = $('#address1').val().toUpperCase();
	var address2 = $('#address2').val().toUpperCase();
	var address3 = $('#address3').val().toUpperCase();
	
	if ((address1 != '')||(address2 != '')||(address3 != '')){
		if (address1 !='') {
			$('#addressHide').val(address1);
			
		}
		if ((address1 != '')&&(address2!='')){
			$('#addressHide').val(address1+"\n"+address2);	
		}
		
		if ((address1 != '')&&(address3!='')){
			$('#addressHide').val(address1+"\n"+address3);	
		}
		
		if ((address1 != '')&&(address2!='')&&(address3!='')){
			$('#addressHide').val(address1+"\n"+address2+"\n"+address3);
		}
		
	}
};

function validationParticipanFlag(valid){
	if(valid==true){
		$('div[id=pPartAs] label span').html(" *");
		$('#ctpParticipanAs').removeAttr('disabled');
		$('#ctpParticipanCode').removeAttr('disabled');	
	}else{
		$('div[id=pPartAs] label span').html("");
		$('#ctpParticipanAs').val("");
		$('#ctpParticipanCode').val("");
		$('#ctpParticipanError').html("");
		$('#ctpParticipanAs').attr("disabled","disabled");
		$('#ctpParticipanCode').attr("disabled","disabled");
	}
};
