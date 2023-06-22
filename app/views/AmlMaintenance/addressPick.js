$(function(){

	$('#customerForm #address1CountryCode').dynapopup2(
			{
				key:'#customerForm #address1Country', 
				help:'#customerForm #address1CountryHelp', 
				desc:'#customerForm #address1CountryDesc'},
				'PICK_GN_LOOKUP', "COUNTRY", "#customerForm #address1StateCode", 
			function(data){
				if (data) {
					$('#address1CountryCode').removeClass('fieldError');
					$('#address1Country').val(data.code);
					$('#address1CountryDesc').val(data.description);
					$('#h_address1CountryDesc').val(data.description);
					
					$('#address1StateCode').val("");
					$('#address1State').val("");
					$('#address1StateDesc').val("");
					$('#address1StateCode').removeClass('fieldError');
					
					$('#address1AreaCode').val("");
					$('#address1Area').val("");
					$('#address1AreaDesc').val("");
					$('#address1AreaCode').removeClass('fieldError');
					state("STATE");
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