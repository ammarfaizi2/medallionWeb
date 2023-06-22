$(function(){
	if (('${mode}'=='entry') || (('${mode}'=='edit') && (('${thirdParty?.recordStatus?.decodeStatus()}' == 'Reject') || ($("#status").val() == 'R' )))) {
		$('input[name=isActive]').attr("disabled", "disabled");
	}
	$("input[name='isActive']").change(function() {
		$("input[name='thirdParty.isActive']").val($("input[name='isActive']:checked").val());
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
	$('#address1CountryCode').lookup({
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
	});
	
	$('#bankCode').change(function(){
		if ($('#bankCode').val() == ""){
			$('#bankCode').removeClass('fieldError');
			$('#bankCodeName').val("");
			$('#h_bankCodeName').val("");
		}	
	});
	
	$('#bankCode').popupThirdParties("?type=THIRD_PARTY-BANK", "bankAccountNo", function(data){
		$('#bankCode').removeClass('fieldError');
		//$('#newBankCode').val(data.name);
	}, function(data) {
		$('#bankCode').addClass('fieldError');
	});
	
	
	$('#currencyBank').change(function(){
		if ($('#currencyBank').val() == ""){
			$('#currencyBank').removeClass('fieldError');
			$('#currencyDescBank').val("");
			$('#h_currencyDescBank').val("");
		}	
	});
	
	$('#currencyBank').popupCurrencies("thirdPartyBranch", function(data){
		$('#currencyBank').removeClass('fieldError');
	}, function(data){
		$('#currencyBank').addClass('fieldError');
		$('#currencyDescBank').val('NOT FOUND');
		$('#h_currencyDescBank').val('');
	});
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