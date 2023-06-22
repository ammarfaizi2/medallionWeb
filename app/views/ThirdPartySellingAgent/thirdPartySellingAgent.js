$(function(){
	
	
	if (('${mode}'==='entry')||(('${mode}'==='edit')&&(('${thirdParty?.recordStatus?.decodeStatus()}'==='Reject')))){
		$('input[name=isActive]').attr("disabled", "disabled");
	}
	
	$('input[name=isActive]').change(function(){
		$("input[name='thirdParty.isActive']").val($("input[name='isActive']:checked").val());
	});
	
	if ($.browser.msie){
		$("#remarks[maxlength]").bind('input propertychange', function() {
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    });
	}
	
	$('#thirdPartyCode').change(function(){
		$('#thirdPartyCodeHidden').val($(this).val().toUpperCase());
	});
	
	$('#address1CountryCode').change(function(){
		if (($('#address1CountryCode').val() == "") || ($('#address1CountryCode').isChange())) {
			$('#address1Country').val("");
			$('#address1CountryDesc').val("");
			$('#h_address1CountryDesc').val("");
			$('#address1CountryCode').removeClass('fieldError');
			
			$('#address1StateCode').val("");
			$('#address1State').val("");
			$('#address1StateDesc').val("");
			$('#addressStateDesc').val("");
			$('#h_addressStateDesc').val("");
			$('#h_address1StateDesc').val("");
			$('#address1StateCode').removeClass('fieldError');
			
			$('#address1AreaCode').val("");
			$('#address1Area').val("");
			$('#address1AreaDesc').val("");
			$('#h_address1AreaDesc').val("");
			$('#address1AreaCode').removeClass('fieldError');
			
			state("");
			area("");
		}
		
	});
	
	$('#address1StateCode').change(function(){
		if (($('#address1StateCode').val() == "") || ($('#address1StateCode').isChange())) {
			if ($('#address1StateCode').val() == ""){
				$('#address1StateCode').removeClass('fieldError');
				$('#address1StateCode').val("");
				$('#address1StateDesc').val("");
				$('#h_address1StateDesc').val("");
			}
			
			$('#address1State').val("");
			$('#address1StateDesc').val("");
			$('#h_address1StateDesc').val("");
			
			
			
			$('#address1AreaCode').val("");
			$('#address1Area').val("");
			$('#address1AreaDesc').val("");
			$('#h_address1AreaDesc').val("");
			$('#address1AreaCode').removeClass('fieldError');
			
			
		
		}
	});
	
	$('#address1AreaCode').change(function(){
		if ($('#addressAreaCode').val() == "") {
			$('#address1AreaCode').val("");
			$('#address1Area').val("");
			$('#address1AreaDesc').val("");
			$('#h_address1AreaDesc').val("");
			$('#address1AreaCode').removeClass('fieldError');
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
	
	/*$('#address1CountryCode').lookup({
		list:'@{Pick.lookups()}?group=COUNTRY',
		get:{
			url:'@{Pick.lookup()}?group=COUNTRY',
			success: function(data) {
				if (data) {
					$('#address1CountryCode').removeClass('fieldError');
					$('#address1Country').val(data.code);
					$('#address1CountryDesc').val(data.description);
					$('#h_address1CountryDesc').val(data.description);
					
					$('#address1StateCode').val("");
					$('#address1State').val("");
					$('#address1StateDesc').val("");
					$('#h_address1StateDesc').val("");
					$('#address1StateCode').removeClass('fieldError');
					
					$('#address1AreaCode').val("");
					$('#address1Area').val("");
					$('#address1AreaDesc').val("");
					$('#h_address1AreaDesc').val("");
					$('#address1AreaCode').removeClass('fieldError');
					state("STATE");
				}
			},
			error : function(data) {
				$('#address1CountryCode').addClass('fieldError');
				$('#address1CountryDesc').val('NOT FOUND');
				$('#address1CountryCode').val('');
				$('#address1Country').val('');
				$('#h_address1CountryDesc').val('');
			}
		},
		key:$('#address1Country'),
		description:$('#address1CountryDesc'),
		help:$('#address1CountryHelp'),
		nextControl:$('#address1StateCode')
	});*/
	
	$('#address1CountryCode').dynapopup2({key:'address1Country', help:'address1CountryHelp', desc:'address1CountryDesc'},'PICK_GN_LOOKUP', "COUNTRY", "address1StateCode", 
			function(data){
				if (data) {
					$('#address1CountryCode').removeClass('fieldError');
					$('#address1Country').val(data.code);
					$('#address1CountryDesc').val(data.description);
					$('#h_address1CountryDesc').val(data.description);
					
					$('#address1StateCode').val("");
					$('#address1State').val("");
					$('#address1StateDesc').val("");
					$('#h_address1StateDesc').val("");
					$('#address1StateCode').removeClass('fieldError');
					
					$('#address1AreaCode').val("");
					$('#address1Area').val("");
					$('#address1AreaDesc').val("");
					$('#h_address1AreaDesc').val("");
					$('#address1AreaCode').removeClass('fieldError');
					state("STATE");
					area("");
				}
			},
			function(data){
				$('#address1CountryCode').addClass('fieldError');
				$('#address1CountryDesc').val('NOT FOUND');
				$('#address1CountryCode').val('');
				$('#address1Country').val('');
				$('#h_address1CountryDesc').val('');
			}
	);
	

	$('#address1Ext').change(function(){
		fillAddress();
	});
	$('#address2Ext').change(function(){
		fillAddress();
	});
	$('#address3Ext').change(function(){
		fillAddress();
	});
});

function state(data) {
	// address 1 state lookup at tab contacts ===============================================================================
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
					
					$('#address1AreaCode').val("");
					$('#address1Area').val("");
					$('#address1AreaDesc').val("");
					$('#h_address1AreaDesc').val("");
					$('#address1AreaCode').removeClass('fieldError');
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
	// ======================================================================================================================
}


function area(data) {
	// address 1 area lookup at tab contacts ================================================================================
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
		nextControl:$('#address1ZipCode')
	});
	// ======================================================================================================================
}


function fillAddress(){
	var address1 = $('#address1Ext').val().toUpperCase();
	var address2 = $('#address2Ext').val().toUpperCase();
	var address3 = $('#address3Ext').val().toUpperCase();
	
	if ((address1 != '')||(address2 != '')||(address3 != '')){
		if (address1 !='') {
			$('#address1Hide').val(address1);
			
		}
		if ((address1 != '')&&(address2!='')){
			$('#address1Hide').val(address1+"\n"+address2);	
		}
		
		if ((address1 != '')&&(address3!='')){
			$('#address1Hide').val(address1+"\n"+address3);	
		}
		
		if ((address1 != '')&&(address2!='')&&(address3!='')){
			$('#address1Hide').val(address1+"\n"+address2+"\n"+address3);
		}
		
	}
};