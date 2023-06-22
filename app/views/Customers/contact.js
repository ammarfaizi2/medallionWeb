$(function(){
	var addressTypeDescription;
	var sameAsDescription;
	
	var data = new Object();
	var currentDate = $('#currentDate').val();
	
	var closeDialog = function() {
		$("#dialog-message").dialog('close');
	}
	
	tabContactCorp(data);
	
	// event button new data contact ========================================================================================
	$('.buttons #newContact').click(function() {
		newContact();	
		return false;
	});
	
	$('.buttons #newContactCorp').click(function() {
		newContactCorp();	
		return false;
	});
	// ======================================================================================================================
	
	$('#cancelCon').click(function(){
		$('#detailContact').dialog('close');
		return false;
	});
	
	$('#cancelConCorp').click(function(){
		$('#detailContactCorp').dialog('close');
		return false;
	});
	
	$('#closeCon').click(function(){
		$('#detailContact').dialog('close');
		return false;
	});
	
	$('#closeConCorp').click(function(){
		$('#detailContactCorp').dialog('close');
		return false;
	});
	$("#contactForm #contacTypeCorp").change(function() {
		$('#contactForm #contacTypeCorp').removeClass('fieldError');
		$('#duplicateErrorMessageCorp').html("");
		getContactTypeLookupDescription();
		
	});
	
	$('.calendar').datepicker();
	
	$('#firstNameCorp').change(function(){
		fillContactName();
	});
	
	$('#middleNameCorp').change(function(){
		fillContactName();
		if ($(this).val()!=''){
			$('p[id=pLastNameCorp] label span').html(' *');
		} else {
			$('p[id=pLastNameCorp] label span').html('');
		}
	});
	
	$('#lastNameCorp').change(function(){
		fillContactName();
	});
	
	$('#corrAddress').change(function(){
		
		var table = $('#contact #grid-contact').dataTable();
		var lengthAdd = table.fnGetNodes().length;
		var foundSame = false;
		for(var i=0;i<lengthAdd;i++){
			var rowNode = $('#contact #grid-contact').find('tbody tr').get(i);
			var data = table.fnGetData(rowNode);
			if (data.addressType.lookupId == $(this).val()){
				foundSame = true;
			}
		}
		
		$(this).removeClass('fieldError');
		$('#corrAddressError').html('');
		if ($(this).val()!=''){
			if (!foundSame){
				$(this).addClass('fieldError');
				$('#corrAddressError').html('This type not available in table Address');
			}
		}
//		for (var i=0;i<lengthAdd;i++){
//			var cell = table.fnGetData(i);
//			alert(cell[0]);
//			
//		}
		
	});
	
	// contact addressType corporate event ==================================================================================
	$("#detailContact #contactForm #addressType").change(function() {
		/*if ($(this).val()!=$('#corrAddress').val()){
			$('#corrAddress').val('');
		}*/
		$('#contactForm #addressType').removeClass('fieldError');
		$('#duplicateErrorMessage').html("");
	//	checkValueSameAsWithAddressType();
		getAddressTypeLookupDescription();
		if ($("#contactForm #sameAsCorp").val() != $("#contactForm #addressTypeCorp").val()) {
			$("#sameAsCorp").removeClass('fieldError');
			$("#sameAsErrorCorp").html("");
		}
	});
	// ======================================================================================================================
	
	$('#detailContact #contactForm #address1').change(function(){
		fillAddress();
	});
	$('#detailContact #contactForm #address2').change(function(){
		fillAddress();
	});
	$('#detailContact #contactForm #address3').change(function(){
		fillAddress();
	});
	// address 1 country action blur ======================================================================================			
	$('#contactForm #address1CountryCode').change(function(){
		if (($('#contactForm #address1CountryCode').val() == "") || ($('#contactForm #address1CountryCode').isChange())) {
			$('#contactForm #address1Country').val("");
			$('#contactForm #h_address1CountryDesc').val("");
			$('#contactForm #address1CountryCode').removeClass('fieldError');
			
			$('#contactForm #address1StateCode').val("");
			$('#contactForm #address1State').val("");
			$('#contactForm #address1StateDesc').val("");
			$('#contactForm #address1StateCode').removeClass('fieldError');
			
			$('#contactForm #address1AreaCode').val("");
			$('#contactForm #address1Area').val("");
			$('#contactForm #address1AreaDesc').val("");
			$('#contactForm #address1AreaCode').removeClass('fieldError');
			
			state("");
			area("");
		}
		
	});
	// ======================================================================================================================
	
	// address 1 state action blur ========================================================================================
	$('#contactForm #address1StateCode').change(function(){
		if (($('#contactForm #address1StateCode').val() == "") || ($('#contactForm #address1StateCode').isChange())) {
			//$('#contactForm #address1StateCode').val("");
			$('#contactForm #address1State').val("");
			$('#contactForm #address1StateDesc').val("");
			$('#contactForm #h_address1StateDesc').val("");
			$('#contactForm #address1StateCode').removeClass('fieldError');
			
			$('#contactForm #address1AreaCode').val("");
			$('#contactForm #address1Area').val("");
			$('#contactForm #address1AreaDesc').val("");
			$('#contactForm #address1AreaCode').removeClass('fieldError');
			
			area("");
		}
	});
	// ======================================================================================================================
	
	// address 1 area action change =========================================================================================
	$('#contactForm #address1AreaCode').change(function(){
		if ($('#contactForm #address1AreaCode').val() == "") {
			$('#contactForm #address1AreaCode').val("");
			$('#contactForm #address1Area').val("");
			$('#contactForm #address1AreaDesc').val("");
			$('#contactForm #h_address1AreaDesc').val("");
			$('#contactForm #address1AreaCode').removeClass('fieldError');
		}
	});
	// ======================================================================================================================
		
	var stateFilter ="";
	if($('#contactForm #address1CountryCode').val() != ""){
		stateFilter = "STATE";
	}
	state(stateFilter);
	
	var areaFilter ="";
	if($('#contactForm #address1StateCode').val() != ""){
		areaFilter = "AREA";
	}
	area(areaFilter);
	
	// address 1 country lookup at tab contacts =============================================================================
	/*$('#contactForm #address1CountryCode').lookup({
		list:'@{Pick.lookups()}?group=COUNTRY',
		get:{
			url:'@{Pick.lookup()}?group=COUNTRY',
			success: function(data) {
				if (data) {
					$('#contactForm #address1CountryCode').removeClass('fieldError');
					$('#contactForm #address1Country').val(data.code);
					$('#contactForm #address1CountryDesc').val(data.description);
					$('#contactForm #h_address1CountryDesc').val(data.description);
					
					$('#contactForm #address1StateCode').val("");
					$('#contactForm #address1State').val("");
					$('#contactForm #address1StateDesc').val("");
					$('#contactForm #address1StateCode').removeClass('fieldError');
					
					$('#contactForm #address1AreaCode').val("");
					$('#contactForm #address1Area').val("");
					$('#contactForm #address1AreaDesc').val("");
					$('#contactForm #address1AreaCode').removeClass('fieldError');
					state("STATE");
				}
			},
			error : function(data) {
				$('#contactForm #address1CountryCode').addClass('fieldError');
				$('#contactForm #address1CountryDesc').val('NOT FOUND');
				$('#contactForm #address1CountryCode').val('');
				$('#contactForm #address1Country').val('');
				$('#contactForm #h_address1CountryDesc').val('');
			}
		},
		key:$('#contactForm #address1Country'),
		description:$('#contactForm #address1CountryDesc'),
		help:$('#contactForm #address1CountryHelp'),
		nextControl:$('#contactForm #address1StateCode')
	});*/
	
	$('#contactForm #address1CountryCode').dynapopup2({key:'#contactForm #address1Country', help:'#contactForm #address1CountryHelp', desc:'#contactForm #address1CountryDesc'},'PICK_GN_LOOKUP', "COUNTRY", "#contactForm #address1StateCode", 
			function(data){
				if (data) {
					$('#contactForm #address1CountryCode').removeClass('fieldError');
					$('#contactForm #address1Country').val(data.code);
					$('#contactForm #address1CountryDesc').val(data.description);
					$('#contactForm #h_address1CountryDesc').val(data.description);
					
					$('#contactForm #address1StateCode').val("");
					$('#contactForm #address1State').val("");
					$('#contactForm #address1StateDesc').val("");
					$('#contactForm #address1StateCode').removeClass('fieldError');
					
					$('#contactForm #address1AreaCode').val("");
					$('#contactForm #address1Area').val("");
					$('#contactForm #address1AreaDesc').val("");
					$('#contactForm #address1AreaCode').removeClass('fieldError');
					state("STATE");
				}
			},
			function(data){
				$('#contactForm #address1CountryCode').addClass('fieldError');
				$('#contactForm #address1CountryDesc').val('NOT FOUND');
				$('#contactForm #address1CountryCode').val('');
				$('#contactForm #address1Country').val('');
				$('#contactForm #h_address1CountryDesc').val('');
			}
	);
	
	// ======================================================================================================================
	/*
	$('#contactForm #identification1No').change(function(){
		if (($('#detailContactCorp #contactForm #identification1ExpiryCon').val()!='')&&($(this).val()!='')&&($('#customerCategory').val()=='${indirectCat}')){
			$('p[id=pContactNpwpNo] label span').html('');
		} else {
			$('p[id=pContactNpwpNo] label span').html(' *');
		}
	});*/
	
	// for check identification type 1 in tab contacts ======================================================================
	$('#contactForm #identificationType1').change(function() {
		checkIdentificationTypeCon();
		$('#contactForm #identification1No').val('');
		$('#contactForm #identification1Expiry').val('');
		$('#contactForm #identification1Expiry').removeClass('fieldError');
		$('#contactForm #identification1ExpiryError').html('');
		if (($(this).val()!='')&&($('#contactForm #identificationType2').val()!='')&&($('#contactForm #identificationType3').val()!='')
			&&($('#contactForm #identificationType4').val()!='')&&($('#customerCategory').val()=='${indirectCat}')) {
			$('p[id=pContactNpwpNo] label span').html('');
		} else {
			$('p[id=pContactNpwpNo] label span').html(' *');
		}
	});
	// ======================================================================================================================
	
	// for check identification type 2 in tab contacts ======================================================================
	$('#contactForm #identificationType2').change(function() {
		checkIdentificationTypeCon();
		$('#contactForm #identification2No').val('');
		$('#contactForm #identification2Expiry').val('');
		$('#contactForm #identification2Expiry').removeClass('fieldError');
		$('#contactForm #identification2ExpiryError').html('');
		if (($(this).val()!='')&&($('#contactForm #identificationType1').val()!='')&&($('#contactForm #identificationType3').val()!='')
			&&($('#contactForm #identificationType4').val()!='')&&($('#customerCategory').val()=='${indirectCat}')) {
			$('p[id=pContactNpwpNo] label span').html('');
		} else {
			$('p[id=pContactNpwpNo] label span').html(' *');
		}
	});
	// ======================================================================================================================
	
	// for check identification type 3 in tab contacts ======================================================================
	$('#contactForm #identificationType3').change(function() {
		checkIdentificationTypeCon();
		$('#contactForm #identification3No').val('');
		$('#contactForm #identification3Expiry').val('');
		$('#contactForm #identification3Expiry').removeClass('fieldError');
		$('#contactForm #identification3ExpiryError').html('');
		if (($(this).val()!='')&&($('#contactForm #identificationType2').val()!='')&&($('#contactForm #identificationType1').val()!='')
			&&($('#contactForm #identificationType4').val()!='')&&($('#customerCategory').val()=='${indirectCat}')) {
			$('p[id=pContactNpwpNo] label span').html('');
		} else {
			$('p[id=pContactNpwpNo] label span').html(' *');
		}
	});
	// ======================================================================================================================
		
	// for check identification type 4 in tab contacts ======================================================================
	$('#contactForm #identificationType4').change(function() {
		checkIdentificationTypeCon();
		$('#contactForm #identification4No').val('');
		$('#contactForm #identification4Expiry').val('');
		$('#contactForm #identification4Expiry').removeClass('fieldError');
		$('#contactForm #identification4ExpiryError').html('');
		if (($(this).val()!='')&&($('#contactForm #identificationType2').val()!='')&&($('#contactForm #identificationType3').val()!='')
			&&($('#contactForm #identificationType1').val()!='')&&($('#customerCategory').val()=='${indirectCat}')) {
			$('p[id=pContactNpwpNo] label span').html('');
		} else {
			$('p[id=pContactNpwpNo] label span').html(' *');
		}
	});
	// ======================================================================================================================

	$('#contactForm #contactNpwpRegDate').change(function(){
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if (!$(this).hasClass('fieldError')){
			compareDateMustBeforeApplicationDate(thisId, value);
		}
	});
	
	// contact expDate 1 must be greather than regDate ======================================================================
	$('#contactForm  #identification1ExpiryCon').change(function(){
		/*console.log("exp date = " +new Date($(this).val()).getTime());
		var id = this.id;
		var thisId = "#" + id;
		var errorMsg2 = "can not less than Application Date";
		if (!$(this).hasClass('fieldError')){
			compareExpiryDateWithApplicationDate(thisId, currentDate, errorMsg2);
		}*/
		
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ((!$(this).hasClass('fieldError'))&&($(this).val()!='')){
			if (compareDateMustAfterApplicationDate(thisId, value)) {
				console.log("date exp = " +$(this).val());
				console.log("id no = " +$('#detailContactCorp #contactForm #identification1No').val());
				console.log("kondisi = " +(($('#detailContactCorp #contactForm #identification1No').val()!='')&&($(this).val()!='')));
				console.log('category = '+'${indirectCat}');
				console.log('category = '+$('#customerCategory').val());
				if (($('#detailContactCorp #contactForm #identification1No').val()!='')&&($(this).val()!='')&&($('#customerCategory').val()=='${indirectCat}')){
					console.log("this val 1 = " +$(this).val());
					$('p[id=pContactNpwpNo] label span').html('');
				} else {
					$('p[id=pContactNpwpNo] label span').html(' *');
				}
			}
				
		}
		
	});
	// ======================================================================================================================
	
	// contact expDate 2 must be greather than regDate ======================================================================
	$('#contactForm  #identification2ExpiryCon').change(function(){
		//conRegDate2validation();
		/*var id = this.id;
		var thisId = "#" + id;
		var errorMsg2 = "can not less than Application Date";
		if (!$(this).hasClass('fieldError')){
			compareExpiryDateWithApplicationDate(thisId, currentDate, errorMsg2);
		}
		*/
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ((!$(this).hasClass('fieldError'))&&($(this).val()!='')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
		
	});
	// ======================================================================================================================
	
	// contact expDate 3 must be greather than regDate ======================================================================
	$('#contactForm  #identification3ExpiryCon').change(function(){
		//conRegDate3validation();
		/*var id = this.id;
		var thisId = "#" + id;
		var errorMsg2 = "can not less than Application Date";
		if (!$(this).hasClass('fieldError')){
			compareExpiryDateWithApplicationDate(thisId, currentDate, errorMsg2);
		}*/
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ((!$(this).hasClass('fieldError'))&&($(this).val()!='')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
		
	});
	// ======================================================================================================================
		
	// contact expDate 4 must be greather than regDate ======================================================================
	$('#contactForm  #identification4ExpiryCon').change(function(){
		/*var id = this.id;
		var thisId = "#" + id;
		var errorMsg2 = "can not less than Application Date";
		if (!$(this).hasClass('fieldError')){
			compareExpiryDateWithApplicationDate(thisId, currentDate, errorMsg2);
		}*/
		
		var id = this.id;
		var thisId = "#" + id;
		var value = $(this).datepicker("getDate");
		if ((!$(this).hasClass('fieldError'))&&($(this).val()!='')){
			compareDateMustAfterApplicationDate(thisId, value);
		}
	});
	// ======================================================================================================================

	
	// button save contact event ============================================================================================
	$('#saveCon').die("click");
	$('#saveCon').live("click", function(){
		var oldAddressType = $("#detailContact #contactForm #oldAddressType").val();
		var newAddressType = $("#detailContact #contactForm #newAddressType").val();
		var table = $('#contact #grid-contact').dataTable();
		var rowPosition = $('#detailContact #contactForm #rowPosition').val();
		var pos = tableContact.fnGetPosition(this);
		var length = tableContact.fnGetNodes().length;
		var found = false;
		
		if ($('#emailContact').hasClass('fieldError')){
			return false;
		}
		
		if (($('#addressType').val()=='')||($('#address1').val()=='')||($('#address1CountryCode').val()=='')||
			($('#address1StateCode').val()=='')||($('#address1AreaCode').val()=='')||
			(($('#customerCategory').val()=='${directCat}')&&($('#address1Phone1').val()==''))){
			
			if ($('#addressType').val()==''){
				$('#errmsgAddressType').html('Required');
			} else {
				$('#errmsgAddressType').html('');
			}
			
			if ($('#address1').val()==''){
				$('#address1Error').html('Required');
			} else {
				$('#address1Error').html('');
			}
			
			if ($('#address1CountryCode').val()==''){
				$('#address1CountryCodeError').html('Required');
			} else {
				$('#address1CountryCodeError').html('');
			}
			
			if ($('#address1StateCode').val()==''){
				$('#address1StateCodeError').html('Required');
			} else {
				$('#address1StateCodeError').html('');
			}
			
			if ($('#address1AreaCode').val()=='') {
				$('#address1AreaCodeError').html('Required');
			} else {
				$('#address1AreaCodeError').html('');
			}
			
			if (($('#customerCategory').val()=='${directCat}')&&($('#address1Phone1').val()=='')){
				$('#address1Phone1Error').html('Required');
			} else {
				$('#address1Phone1Error').html('');
			}
			
			return false;
		}
		
		if ($('#detailContact #contactForm input').hasClass('fieldError')){
			return false;
		}
		$('#detailContact #contactForm .errorMessage').html('');

		var dataContact = table.fnGetData(parseFloat(rowPosition));
			// save contact when data already exist (updated) ===============================================================
			if (rowPosition != "") {
				var rows = table.fnGetNodes().length;
				
				for (i = 0; i < rows; i++) {
					var cell = tableContact.fnGetData(i);
						if (($("#detailContact #contactForm #addressType").val() == $(cell[0]).val()) && (oldAddressType != newAddressType)) {					
							$('#detailContact #contactForm #addressType').addClass('fieldError');
							$("#duplicateErrorMessage").html("Already Exist");
							found = true;
							break;
						} 
					
				}
				
				var checkClass = $("#detailContact #contactForm .dropdown").hasClass('fieldError');
				
				if (checkClass) {
					return false;
				}
				
				if (!found) {
						table.fnUpdate(										
										(dataContact.addressType.lookupId = $("#detailContact #contactForm #addressType").val()) &&
										(dataContact.addressType.lookupDescription = $("#detailContact #contactForm #addressTypeDesc").val()),
										parseFloat(rowPosition), 0
								  );
					
					
						table.fnUpdate(
										dataContact.address1 = $('#detailContact #contactForm #addressHide').val(),
										parseFloat(rowPosition), 1
									  );
					
					if (($('#detailContact #contactForm #address1AreaCode').val() == '') || ($('#detailContact #contactForm #address1AreaCode').val() == null)) {
						dataContact.address1Area = new Object();
						table.fnUpdate (
										(dataContact.address1Area.lookupCode = "")&&
										(dataContact.address1Area.lookupId = "")&&
										(dataContact.address1Area.lookupDescription = ""), 
										parseFloat(rowPosition), 2
										);
					} else {
						dataContact.address1Area = new Object();
						table.fnUpdate (
										(dataContact.address1Area.lookupCode = $('#detailContact #contactForm #address1AreaCode').val())&&
										(dataContact.address1Area.lookupId = $('#detailContact #contactForm #address1Area').val())&&
										(dataContact.address1Area.lookupDescription = $('#detailContact #contactForm #address1AreaDesc').val()),
										parseFloat(rowPosition), 2
										);
					}						
					if ($('#detailContact #contactForm #address1Phone1').val() == '') {
						table.fnUpdate(
										dataContact.address1Phone1 = "",
										parseFloat(rowPosition), 3);
					} else {
						table.fnUpdate(
										dataContact.address1Phone1 = $('#detailContact #contactForm #address1Phone1').val(),
										parseFloat(rowPosition), 3);
					}
					
					
					if (($('#detailContact #contactForm #address1CountryCode').val() == '') || ($('#contactForm #address1CountryCode').val() == null)) {
						dataContact.address1Country = new Object();
						dataContact.address1Country.lookupCode = "";
						dataContact.address1Country.lookupId = "";
						dataContact.address1Country.lookupDescription = "";
					} else {
						dataContact.address1Country = new Object();
						dataContact.address1Country.lookupCode = $('#detailContact #contactForm #address1CountryCode').val();
						dataContact.address1Country.lookupId = $('#detailContact #contactForm #address1Country').val();
						dataContact.address1Country.lookupDescription = $('#detailContact #contactForm #address1CountryDesc').val();
					}
					
					if (($('#detailContact #contactForm #address1StateCode').val() == '') || ($('#detailContact #contactForm #address1StateCode').val() == null)) {
						dataContact.address1State = new Object();
						dataContact.address1State.lookupCode = "";
						dataContact.address1State.lookupId = "";
						dataContact.address1State.lookupDescription = "";
					} else {
						dataContact.address1State = new Object();
						dataContact.address1State.lookupCode = $('#detailContact #contactForm #address1StateCode').val();
						dataContact.address1State.lookupId = $('#detailContact #contactForm #address1State').val();
						dataContact.address1State.lookupDescription = $('#detailContact #contactForm #address1StateDesc').val();
					}
					
					
					
					if ($('#detailContact #contactForm #address1ZipCode').val() == '') {
						dataContact.address1ZipCode = "";
					} else {
						dataContact.address1ZipCode = $('#detailContact #contactForm #address1ZipCode').val();
					}
					
					
					if ($('#detailContact #contactForm #address1Phone2').val() == '') {
						dataContact.address1Phone2 = "";
					} else {
						dataContact.address1Phone2 = $('#detailContact #contactForm #address1Phone2').val();
					}
					
					if ($('#detailContact #contactForm #address1Phone3').val() == '') {
						dataContact.address1Phone3 = "";
					} else {
						dataContact.address1Phone3 = $('#detailContact #contactForm #address1Phone3').val();
					}
					
					dataContact.addressExt1 = $('#detailContact #contactForm #address1').val();
					dataContact.addressExt2 = $('#detailContact #contactForm #address2').val();
					dataContact.addressExt3 = $('#detailContact #contactForm #address3').val();
					dataContact.isCustomer = $('#detailContact #contactForm #isCustomer').val();
					dataContact.contactKey = $('#detailContact #contactForm #contactKey').val();
					
					table.fnUpdate(dataContact,parseFloat(rowPosition), 4);
					
					$('#detailContact').dialog('close');
					if (dataContact.addressType.lookupId != $('#corrAddress').val()){
						$('#corrAddress').val('');
					}
				}
			} else {
				
				// save contact when data empty (new) =======================================================================
				var found = false;
				var rows = table.fnGetNodes().length;
				
				for (var i = 0; i < rows; i++) {
					var cell = tableContact.fnGetData(i);
						if ($("#detailContact #contactForm #addressType").val() == $(cell[0]).val()) {					
							$('#detailContact #contactForm #addressType').addClass('fieldError');
							$("#duplicateErrorMessage").html("Already Exist");
							found = true;
							break;
						} 
				}
				
				if (!found) {
					var dataContact = new Object();
					dataContact.addressType = new Object();
					dataContact.address1Country = new Object();
					dataContact.address1State = new Object();
					dataContact.address1Area = new Object();
					
					dataContact.addressType.lookupId = $('#detailContact #contactForm #addressType').val();
					dataContact.addressType.lookupDescription = $('#detailContact #contactForm #addressTypeDesc').val();
					
					dataContact.isCustomer = true;
					dataContact.address1 = $('#detailContact #contactForm #addressHide').val();
					dataContact.addressExt1 = $('#detailContact #contactForm #address1').val();
					
					if ($('#detailContact #contactForm #address2').val() == ''){
						dataContact.addressExt2 = "";
					} else {
						dataContact.addressExt2 = $('#detailContact #contactForm #address2').val();
					}
					
					if ($('#detailContact #contactForm #address3').val() == ''){
						dataContact.addressExt3 = "";
					} else {
						dataContact.addressExt3 = $('#detailContact #contactForm #address3').val();
					}
					
					if ($('#contactForm #address1CountryCode').val() == '') {
						dataContact.address1Country.lookupCode = "";
						dataContact.address1Country.lookupId = "";
						dataContact.address1Country.lookupDescription = "";
					} else {
						dataContact.address1Country.lookupCode = $('#detailContact #contactForm #address1CountryCode').val();
						dataContact.address1Country.lookupId = $('#detailContact #contactForm #address1Country').val();
						dataContact.address1Country.lookupDescription = $('#detailContact #contactForm #address1CountryDesc').val();
					}
					
					if ($('#contactForm #address1StateCode').val() == '') {
						dataContact.address1State.lookupCode = "";
						dataContact.address1State.lookupId = "";
						dataContact.address1State.lookupDescription = "";
					} else {
						dataContact.address1State.lookupCode = $('#detailContact #contactForm #address1StateCode').val();
						dataContact.address1State.lookupId = $('#detailContact #contactForm #address1State').val();
						dataContact.address1State.lookupDescription = $('#detailContact #contactForm #address1StateDesc').val();
					}
					
					if ($('#contactForm #address1AreaCode').val() == '') {
						dataContact.address1Area.lookupCode = "";
						dataContact.address1Area.lookupId = "";
						dataContact.address1Area.lookupDescription = "";
					} else {
						dataContact.address1Area.lookupCode = $('#detailContact #contactForm #address1AreaCode').val();
						dataContact.address1Area.lookupId = $('#detailContact #contactForm #address1Area').val();
						dataContact.address1Area.lookupDescription = $('#detailContact #contactForm #address1AreaDesc').val();
					}
					
					if ($('#contactForm #address1ZipCode').val() == '') {
						dataContact.address1ZipCode = "";
					} else {
						dataContact.address1ZipCode = $('#detailContact #contactForm #address1ZipCode').val();
					}
					
					if ($('#contactForm #address1Phone1').val() == '') {
						dataContact.address1Phone1 = "";
					} else {
						dataContact.address1Phone1 = $('#detailContact #contactForm #address1Phone1').val();
					}
					
					if ($('#contactForm #address1Phone2').val() == '') {
						dataContact.address1Phone2 = "";
					} else {
						dataContact.address1Phone2 = $('#detailContact #contactForm #address1Phone2').val();
					}
					
					if ($('#contactForm #address1Phone3').val() == '') {
						dataContact.address1Phone3 = "";
					} else {
						dataContact.address1Phone3 = $('#detailContact #contactForm #address1Phone3').val();
					}
					
					dataContact.contactKey = $('#detailContact #contactForm #contactKey').val();
					
					table.fnAddData(dataContact);
					
					$('#detailContact').dialog('close');
					
					if (dataContact.addressType.lookupId == $('#corrAddress').val()){
						$('#corrAddress').removeClass('fieldError');
						$('#corrAddressError').html('');
					}
					
					 
				}
				// ==========================================================================================================
			
				
			
			}
			
			$('#grid-contact_wrapper.dataTables_wrapper').css('width','1728px');
			return false;
			
			
		
	});	

//	console.log("arrCorrAddress = " +arrCorrAddress);

	$('#saveConCorp').die("click");
	$('#saveConCorp').live("click", function(){
		var oldContactType = $("#detailContactCorp #contactForm #oldContactType").val();
		var newContactType = $("#detailContactCorp #contactForm #newContactType").val();
		var table = $('#contact #grid-contact-corp').dataTable();
		var rowPosition = $('#detailContactCorp #contactForm #rowPosition').val();
		var pos = tableContactCorp.fnGetPosition(this);
		var rows = tableContactCorp.fnGetNodes().length;
		var found = false;
		var checkClass;
		
		if (($('#contacTypeCorp').val()=='')||($('#firstNameCorp').val()=='')||($('#contactPosition').val()=='')||
		   ($('#detailContactCorp #contactForm #identificationType1').val()=='')||
		   (($('#detailContactCorp #contactForm #contactNpwpNo').val()=='')&&($('#customerCategory').val()=='${directCat}'))||
		   (($('#detailContactCorp #contactForm #contactNpwpNo').val()=='')&&($('#customerCategory').val()=='${indirectCat}')&&($('#detailContactCorp #contactForm #identificationType1').val()=='')&&($('#detailContactCorp #contactForm #identificationType2').val()=='')&&
			($('#detailContactCorp #contactForm #identificationType3').val()=='')&&($('#detailContactCorp #contactForm #identificationType4').val()==''))||
		   (($('#detailContactCorp #contactForm #identificationType1').val()=='${idKtp}')&&($('#detailContactCorp #contactForm #identification1No').val()==''))||
		   (($('#detailContactCorp #contactForm #identificationType1').val()!='${idKtp}')&&(($('#detailContactCorp #contactForm #identification1No').val()=='')||($('#detailContactCorp #contactForm #identification1ExpiryCon').val()=='')))||
//		   ($('#detailContactCorp #contactForm #identificationType2').val()!='')||($('#detailContactCorp #contactForm #identificationType3').val()!='')||($('#detailContactCorp #contactForm #identificationType4').val()!='')||
		   (($('#detailContactCorp #contactForm #middleNameCorp').val()!='')&&($('#detailContactCorp #contactForm #lastNameCorp').val()==''))){
			
			if ($('#contacTypeCorp').val()==''){
				$('#contacTypeCorpError').html('Required');
			} else {
				$('#contacTypeCorpError').html('');
			}
			
			if ($('#firstNameCorp').val()==''){
				$('#firstNameCorpError').html('Required');
			} else {
				$('#firstNameCorpError').html('');
			}
			
			if (($('#detailContactCorp #contactForm #middleNameCorp').val()!='')&&($('#detailContactCorp #contactForm #lastNameCorp').val()=='')){
				$('#lastNameCorpError').html('Required');
			} else {
				$('#lastNameCorpError').html('');
			}
			
			if ($('#contactPosition').val()==''){
				$('#contactPositionError').html('Required');
			} else {
				$('#contactPositionError').html('');
			}
			
			if ($('#detailContactCorp #contactForm #identificationType1').val()==''){
				$('#errmsgIDType1').html('Required');
			} else {
				$('#errmsgIDType1').html('');
			}
			
			if (($('#detailContactCorp #contactForm #identificationType1').val()=='${idKtp}')&&($('#detailContactCorp #contactForm #identification1No').val()=='')){
				$('#identification1NoError').html('Required');
			} else {
				$('#identification1NoError').html('');
			}
			
			if (($('#detailContactCorp #contactForm #identificationType1').val()!='${idKtp}')&&(($('#detailContactCorp #contactForm #identification1No').val()=='')||($('#detailContactCorp #contactForm #identification1ExpiryCon').val()==''))) {
				if ($('#detailContactCorp #contactForm #identification1No').val()=='') {
					$('#identification1NoError').html('Required');
				} else {
					$('#identification1NoError').html('');
				}
				
				if ($('#detailContactCorp #contactForm #identification1ExpiryCon').val()==''){
					$('#identification1ExpiryConError').html('Required');
				} else {
					$('#identification1ExpiryConError').html('');
				}
			}
			
			if ($('#detailContactCorp #contactForm #identificationType2').val()!=''){
				if (($('#detailContactCorp #contactForm #identificationType2').val()=='${idKtp}')&&($('#detailContactCorp #contactForm #identification2No').val()=='')){
					$('#identification2NoError').html('Required');
				} else {
					$('#identification2NoError').html('');
				}
				
				if (($('#detailContactCorp #contactForm #identificationType2').val()!='${idKtp}')&&(($('#detailContactCorp #contactForm #identification2No').val()=='')||($('#detailContactCorp #contactForm #identification2ExpiryCon').val()==''))) {
					if ($('#detailContactCorp #contactForm #identification2No').val()=='') {
						$('#identification2NoError').html('Required');
					} else {
						$('#identification2NoError').html('');
					}
					
					if ($('#detailContactCorp #contactForm #identification2ExpiryCon').val()==''){
						$('#identification2ExpiryConError').html('Required');
					} else {
						$('#identification2ExpiryConError').html('');
					}
				}
			}
			
			if ($('#detailContactCorp #contactForm #identificationType3').val()!=''){
				if (($('#detailContactCorp #contactForm #identificationType3').val()=='${idKtp}')&&($('#detailContactCorp #contactForm #identification3No').val()=='')){
					$('#identification3NoError').html('Required');
				} else {
					$('#identification3NoError').html('');
				}
				
				if (($('#detailContactCorp #contactForm #identificationType3').val()!='${idKtp}')&&(($('#detailContactCorp #contactForm #identification3No').val()=='')||($('#detailContactCorp #contactForm #identification3ExpiryCon').val()==''))) {
					if ($('#detailContactCorp #contactForm #identification3No').val()=='') {
						$('#identification3NoError').html('Required');
					} else {
						$('#identification3NoError').html('');
					}
					
					if ($('#detailContactCorp #contactForm #identification3ExpiryCon').val()==''){
						$('#identification3ExpiryConError').html('Required');
					} else {
						$('#identification3ExpiryConError').html('');
					}
				}
			}
			
			if ($('#detailContactCorp #contactForm #identificationType4').val()!=''){
				if (($('#detailContactCorp #contactForm #identificationType4').val()=='${idKtp}')&&($('#detailContactCorp #contactForm #identification4No').val()=='')){
					$('#identification4NoError').html('Required');
				} else {
					$('#identification4NoError').html('');
				}
				
				if (($('#detailContactCorp #contactForm #identificationType4').val()!='${idKtp}')&&(($('#detailContactCorp #contactForm #identification4No').val()=='')||($('#detailContactCorp #contactForm #identification4ExpiryCon').val()==''))) {
					if ($('#detailContactCorp #contactForm #identification4No').val()=='') {
						$('#identification4NoError').html('Required');
					} else {
						$('#identification4NoError').html('');
					}
					
					if ($('#detailContactCorp #contactForm #identification4ExpiryCon').val()==''){
						$('#identification4ExpiryConError').html('Required');
					} else {
						$('#identification4ExpiryConError').html('');
					}
				}
			}
			
			if (($('#detailContactCorp #contactForm #contactNpwpNo').val()=='')&&($('#customerCategory').val()=='${directCat}')){
				$('#contactNpwpNoError').html('Required');
			} else 
//				if (($('#detailContactCorp #contactForm #contactNpwpNo').val()=='')&&($('#customerCategory').val()=='${indirectCat}')&&(($('#detailContactCorp #contactForm #identification1No').val()=='')||($('#detailContactCorp #contactForm #identification1ExpiryCon').val()==''))&&
//					(($('#detailContactCorp #contactForm #identificationType2').val()=='')||($('#detailContactCorp #contactForm #identification2No').val()=='')||($('#detailContactCorp #contactForm #identification2ExpiryCon').val()==''))&&
//					(($('#detailContactCorp #contactForm #identificationType3').val()=='')||($('#detailContactCorp #contactForm #identification3No').val()=='')||($('#detailContactCorp #contactForm #identification3ExpiryCon').val()==''))&&
//					(($('#detailContactCorp #contactForm #identificationType4').val()=='')||($('#detailContactCorp #contactForm #identification4No').val()=='')||($('#detailContactCorp #contactForm #identification4ExpiryCon').val()==''))) {
				if (($('#detailContactCorp #contactForm #contactNpwpNo').val()=='')&&($('#customerCategory').val()=='${indirectCat}')&&($('#detailContactCorp #contactForm #identificationType1').val()=='')&&($('#detailContactCorp #contactForm #identificationType2').val()=='')&&
						($('#detailContactCorp #contactForm #identificationType3').val()=='')&&($('#detailContactCorp #contactForm #identificationType4').val()=='')) {
				$('#contactNpwpNoError').html('Required');
			} else {
				$('#contactNpwpNoError').html('');
			}
			
			return false;
		}
		
		/*
		if ($('#detailContactCorp #contactForm input').hasClass('fieldError')){
			
			return false;
		}
		*/

		$('#detailContactCorp #contactForm input').each(function(idx, obj){
			var curObj = $(obj);
			var reqClass = curObj.parent().find("label span.req");
			if( reqClass[0] == undefined ){// not required
				
			}else{// required
				if( curObj.val() == "" ){
					return false;
				}
			}
		});
		var isFieldAppDateError = false;
		$('#detailContactCorp #contactForm input.fieldAppDateError').each(function(idx, obj){
			var curObj = $(obj);
			isFieldAppDateError = true;
			var reqClass = curObj.parent().find("label span.req");
			if( curObj.val() == '' && reqClass[0] == undefined ) {
				isFieldAppDateError = false;
			}else{
				isFieldAppDateError = true;
			}
			
		});
		
		if( isFieldAppDateError ){
			return false;
		}
		
		$('#detailContactCorp #contactForm .errorMessage').html('');
		var dataContact = table.fnGetData(parseFloat(rowPosition));
			// save contact when data already exist (updated) ===============================================================
			if (rowPosition != "") {
				var rows = table.fnGetNodes().length;
				for (var i = 0; i < rows; i++) {
					var cell = tableContactCorp.fnGetData(i);
					if (($("#contactForm #contacTypeCorp").val() == $(cell[0]).val()) && (oldContactType != newContactType)) {					
						$('#contactForm #contacTypeCorp').addClass('fieldError');
						$("#contacTypeCorpError").html("Already Exist");
						found = true;
						break;
					} 
					
				}
				
				var checkClass = $("#contactForm .dropdown").hasClass('fieldError');
				
				if (checkClass) {
					return false;
				}
				
				if (!found) {
					
						table.fnUpdate(										
										(dataContact.contactType.lookupId = $("#detailContactCorp #contactForm #contacTypeCorp").val()) &&
										(dataContact.contactType.lookupDescription = $("#detailContactCorp #contactForm #contactTypeDescCorp").val()),
										parseFloat(rowPosition), 0
								  );
					
					if ($('#detailContact #contactForm #contactName').val() == '') {
						table.fnUpdate(
										dataContact.contactName = "",
										parseFloat(rowPosition), 1
									 );
					} else {
						table.fnUpdate(
										dataContact.contactName = $('#detailContactCorp #contactForm #contactNameHide').val().toUpperCase(),
										parseFloat(rowPosition), 1
									 );
					}
					
					if ($('#detailContactCorp #contactForm #contactPosition').val() == '') {
						table.fnUpdate(
										dataContact.contactPosition = "",
										parseFloat(rowPosition), 2);
					} else {
						table.fnUpdate(
								
										dataContact.contactPosition = $('#detailContactCorp #contactForm #contactPosition').val(),
										parseFloat(rowPosition), 2);
					}

					if ($('#detailContactCorp #contactForm #firstNameCorp').val() == '') {
						dataContact.firstName = "";
					} else {
						dataContact.firstName = $('#detailContactCorp #contactForm #firstNameCorp').val();
					}
						
					if ($('#detailContactCorp #contactForm #middleNameCorp').val() == '') {
						dataContact.middleName = "";
					} else {
						dataContact.middleName = $('#detailContactCorp #contactForm #middleNameCorp').val();
					}
					
					if ($('#detailContactCorp #contactForm #lastNameCorp').val() == '') {
						dataContact.lastName = "";
					} else {
						dataContact.lastName = $('#detailContactCorp #contactForm #lastNameCorp').val();
					}
					
					
					if ($('#detailContactCorp #contactForm #contactNpwpNo').val() == '') {
						dataContact.npwp = "";
					} else {
						dataContact.npwp = $('#detailContactCorp #contactForm #contactNpwpNo').val();
					}
					
					if ($('#detailContactCorp #contactForm #contactNpwpRegDate').val() == '') {
						dataContact.npwpDate = "";
					} else {
						dataContact.npwpDate = $('#detailContactCorp #contactForm #contactNpwpRegDate').val();
					}
					
					if (($('#detailContactCorp #contactForm #identificationType1').val() == '') || ($('#contactForm #identificationType1').val() == null)) {
						dataContact.identificationType1 = new Object();
						dataContact.identificationType1.lookupId = "";
					} else {
						dataContact.identificationType1 = new Object();
						dataContact.identificationType1.lookupId = $('#detailContactCorp #contactForm #identificationType1').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification1No').val() == '') {
						dataContact.identification1No = "";
					} else {
						dataContact.identification1No = $('#detailContactCorp #contactForm #identification1No').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification1ExpiryCon').val() == '') {
						dataContact.identification1Expiry = "";
					} else {
						dataContact.identification1Expiry = $('#detailContactCorp #contactForm #identification1ExpiryCon').val();
					}
					
					if (($('#detailContactCorp #contactForm #identificationType2').val() == '') || ($('#contactForm #identificationType2').val() == null)) {
						dataContact.identificationType2 = new Object();
						dataContact.identificationType2.lookupId = "";
					} else {
						dataContact.identificationType2 = new Object();
						dataContact.identificationType2.lookupId = $('#detailContactCorp #contactForm #identificationType2').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification2No').val() == '') {
						dataContact.identification2No = "";
					} else {
						dataContact.identification2No = $('#detailContactCorp #contactForm #identification2No').val();
					}
					
				
					if ($('#detailContactCorp #contactForm #identification2ExpiryCon').val() == '') {
						dataContact.identification2Expiry = "";
					} else {
						dataContact.identification2Expiry = $('#detailContactCorp #contactForm #identification2ExpiryCon').val();
					}
					
					if (($('#detailContactCorp #contactForm #identificationType3').val() == '') || ($('#contactForm #identificationType3').val() == null)) {
						dataContact.identificationType3 = new Object();
						dataContact.identificationType3.lookupId = "";
					} else {
						dataContact.identificationType3 = new Object();
						dataContact.identificationType3.lookupId = $('#detailContactCorp #contactForm #identificationType3').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification3No').val() == '') {
						dataContact.identification3No = "";
					} else {
						dataContact.identification3No = $('#detailContactCorp #contactForm #identification3No').val();
					}
					
					
					if ($('#detailContactCorp #contactForm #identification3ExpiryCon').val() == '') {
						dataContact.identification3Expiry = "";
					} else {
						dataContact.identification3Expiry = $('#detailContactCorp #contactForm #identification3ExpiryCon').val();
					}
					
					if (($('#detailContactCorp #contactForm #identificationType4').val() == '') || ($('#contactForm #identificationType4').val() == null)) {
						dataContact.identificationType4 = new Object();
						dataContact.identificationType4.lookupId = "";
					} else {
						dataContact.identificationType4 = new Object();
						dataContact.identificationType4.lookupId = $('#detailContactCorp #contactForm #identificationType4').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification4No').val() == '') {
						dataContact.identification4No = "";
					} else {
						dataContact.identification4No = $('#detailContactCorp #contactForm #identification4No').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification4ExpiryCon').val() == '') {
						dataContact.identification4Expiry = "";
					} else {
						dataContact.identification4Expiry = $('#detailContactCorp #contactForm #identification4ExpiryCon').val();
					}
					
					
					dataContact.isCustomer = $('#detailContactCorp #contactForm #isCustomer').val();
					dataContact.contactKey = $('#detailContactCorp #contactForm #contactKey').val();
					
					table.fnUpdate(dataContact,parseFloat(rowPosition), 3);
					
					$('#detailContactCorp').dialog('close');
					
				}
			} else {
				
				// save contact when data empty (new) =======================================================================
				var found = false;
				var rows = table.fnGetNodes().length;
				
				console.debug("length = " + rows);
				
				for (var i = 0; i < rows; i++) {
					var cell = tableContactCorp.fnGetData(i);
					if ($("#contactForm #contacTypeCorp").val() == $(cell[0]).val()) {					
						$('#contactForm #contacTypeCorp').addClass('fieldError');
						$("#duplicateErrorMessageCorp").html("Already Exist");
						found = true;
						break;
					} 
					
				}
				
				if (!found) {
					var dataContact = new Object();
					dataContact.contactType = new Object();
					dataContact.identificationType1 = new Object();
					dataContact.identificationType2 = new Object();
					dataContact.identificationType3 = new Object();
					dataContact.identificationType4 = new Object();
					
					dataContact.contactType.lookupId = $('#detailContactCorp #contactForm #contacTypeCorp').val();
					dataContact.contactType.lookupDescription = $('#detailContactCorp #contactForm #contactTypeDescCorp').val();
					dataContact.isCustomer = false;
					dataContact.contactName = $('#detailContactCorp #contactForm #contactNameHide').val().toUpperCase();
					
					
					dataContact.firstName = $('#detailContactCorp #contactForm #firstNameCorp').val();
					
					if ($('#detailContactCorp #contactForm #middleNameCorp').val() == '') {
						dataContact.middleName = "";
					} else {
						dataContact.middleName = $('#detailContactCorp #contactForm #middleNameCorp').val();
					}
					
					if ($('#detailContactCorp #contactForm #lastNameCorp').val() == '') {
						dataContact.lastName = "";
					} else {
						dataContact.lastName = $('#detailContactCorp #contactForm #lastNameCorp').val();
					}
					
					if ($('#detailContactCorp #contactForm #contactPosition').val() == '') {
						dataContact.contactPosition = "";
					} else {
						dataContact.contactPosition = $('#detailContactCorp #contactForm #contactPosition').val();
					}
					
					if ($('#detailContactCorp #contactForm #identificationType1').val() == '') {
						dataContact.identificationType1.lookupId = "";
					} else {
						dataContact.identificationType1.lookupId = $('#detailContactCorp #contactForm #identificationType1').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification1No').val() == '') {
						dataContact.identification1No = "";
					} else {
						dataContact.identification1No = $('#detailContactCorp #contactForm #identification1No').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification1ExpiryCon').val() == '') {
						dataContact.identification1Expiry = "";
					} else {
						dataContact.identification1Expiry = $('#detailContactCorp #contactForm #identification1ExpiryCon').val();
					}
					
					if ($('#detailContactCorp #contactForm #identificationType2').val() == '') {
						dataContact.identificationType2.lookupId = "";
					} else {
						dataContact.identificationType2.lookupId = $('#detailContactCorp #contactForm #identificationType2').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification2No').val() == '') {
						dataContact.identification2No = "";
					} else {
						dataContact.identification2No = $('#detailContactCorp #contactForm #identification2No').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification2ExpiryCon').val() == '') {
						dataContact.identification2Expiry = "";
					} else {
						dataContact.identification2Expiry = $('#detailContactCorp #contactForm #identification2ExpiryCon').val();
					}
					
					if ($('#detailContactCorp #contactForm #identificationType3').val() == '') {
						dataContact.identificationType3.lookupId = "";
					} else {
						dataContact.identificationType3.lookupId = $('#detailContactCorp #contactForm #identificationType3').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification3No').val() == '') {
						dataContact.identification3No = "";
					} else {
						dataContact.identification3No = $('#detailContactCorp #contactForm #identification3No').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification3ExpiryCon').val() == '') {
						dataContact.identification3Expiry = "";
					} else {
						dataContact.identification3Expiry = $('#detailContactCorp #contactForm #identification3ExpiryCon').val();
					}
					
					if ($('#detailContactCorp #contactForm #identificationType4').val() == '') {
						dataContact.identificationType4.lookupId = "";
					} else {
						dataContact.identificationType4.lookupId = $('#detailContactCorp #contactForm #identificationType4').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification4No').val() == '') {
						dataContact.identification4No = "";
					} else {
						dataContact.identification4No = $('#detailContactCorp #contactForm #identification4No').val();
					}
					
					if ($('#detailContactCorp #contactForm #identification4ExpiryCon').val() == '') {
						dataContact.identification4Expiry = "";
					} else {
						dataContact.identification4Expiry = $('#detailContactCorp #contactForm #identification4ExpiryCon').val();
					}
					
					if ($('#detailContactCorp #contactForm #contactNpwpNo').val()=='') {
						dataContact.npwp = "";
					} else {
						dataContact.npwp = $('#detailContactCorp #contactForm #contactNpwpNo').val();
					}
					
					if ($('#detailContactCorp #contactForm #contactNpwpNo').val()=='') {
						dataContact.npwpDate = "";
					} else {
						dataContact.npwpDate = $('#detailContactCorp #contactForm #contactNpwpRegDate').val();
					}
					
					dataContact.contactKey = $('#detailContactCorp #contactForm #contactKey').val();
					
					table.fnAddData(dataContact);
					
					$('#detailContactCorp').dialog('close');
				}
				// ==========================================================================================================
			}
			$('#grid-contact-corp_wrapper.dataTables_wrapper').css('width','1728px');
			return false;
//		}
		
	});	
	// ======================================================================================================================

	//	DELETE CONTACT ROW ==================================================================================================
	$('#tabs-4 #grid-contact tbody tr #deleteButton').live('click', function() {
		if ('${mode}' != 'view') {
			var table = $('#contact #grid-contact').dataTable();
			var rows = table.fnGetNodes().length;
			var row = $(this).parents('tr');
			var rowNumber = tableContact.fnGetPosition(row[0]);
			var datas = table.fnGetData(row[0]);
			var found = false;
			var deleteContact = function() {
				tableContact.fnDeleteRow(rowNumber);
				reordering();
				$("#dialog-message").dialog("close");
				if (datas.addressType.lookupId==$('#corrAddress').val()) {
					$('#corrAddress').addClass('fieldError');
					$('#corrAddressError').html('This type not available in table Address');
				}
			};
			
			if (!found) {
				//deleteContact(rowNumber);
				messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteContact, closeDialog);
			}
		}
		
		
		
	});
	// ======================================================================================================================

	$('#tabs-4 #grid-contact-corp tbody tr #deleteButton').live('click', function() {
		if ('${mode}' != 'view') {
			var table = $('#contact #grid-contact-corp').dataTable();
			var rows = table.fnGetNodes().length;
			var row = $(this).parents('tr');
			var rowNumber = tableContactCorp.fnGetPosition(row[0]);
			var datas = table.fnGetData(row[0]);
			var found = false;
			var deleteContactCorp = function() {
				tableContactCorp.fnDeleteRow(rowNumber);
				reorderingContact();
				$("#dialog-message").dialog("close");
			};
			console.debug(" sum of rows = " + rows);
//			for (i = 0; i < rows; i++) {
//				var cell = tableContact.fnGetData(i);
//				//console.debug("cell same as = " + $(cell[4]).val());
//				//console.debug("datas.addressType.lookupId  adalah ' " + datas.addressType.lookupId + " '")
//				//console.debug("1.a. sameAs = " + $("#sameAs").val() + " addressType = " + $("#addressType").val());
//				if (datas.addressType.lookupId == $(cell[4]).val()) {		
//					$("#deleteErrorMessage").html("AddressType : '" + datas.addressType.lookupDescription + "' can not delete");
//					found = true;
//					break;
//				}
//			}
			
			if (!found) {
				//deleteContact(rowNumber);
				messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteContactCorp, closeDialog);
			}
		}
		
	});
});

function fillContactName(){
	var firstName = $('#firstNameCorp').val();
	var middleName = $('#middleNameCorp').val();
	var lastName = $('#lastNameCorp').val();
	
	if ((firstName != '')||(middleName != '')||(lastName != '')){
		if (firstName !='') {
			$('#contactName').val(firstName);
			
		}
		if ((firstName != '')&&(middleName!='')){
			$('#contactName').val(firstName + " " + middleName);	
		}
		
		if ((firstName != '')&&(lastName!='')){
			$('#contactName').val(firstName + " " + lastName);	
		}
		
		if ((firstName != '')&&(middleName!='')&&(lastName!='')){
			$('#contactName').val(firstName + " " + middleName + " " + lastName);
		}
		
		$('#contactNameHide').val($('#contactName').val());
	}
}

function fillAddress(){
	var address1 = $('#detailContact #contactForm #address1').val().toUpperCase();
	var address2 = $('#detailContact #contactForm #address2').val().toUpperCase();
	var address3 = $('#detailContact #contactForm #address3').val().toUpperCase();
	
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
}
//function tab contact =====================================================================================================
function tabContact(data) {
	// initialize table contact =============================================================================================
	tableContact = 
		$('#contact #grid-contact').dataTable({
			aaData: data,
			aoColumns: [  	
							{
								fnRender: function(obj){
									var controls;
									if (obj.aData.addressType != null) {
										controls = obj.aData.addressType.lookupDescription;
										controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressType.lookupId" value="' + obj.aData.addressType.lookupId + '" class="address-type" />';
										controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressType.lookupDescription" value="' + obj.aData.addressType.lookupDescription + '" />';
										
									} else {
										controls = "";
										controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressType.lookupId" value="" />';
										controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressType.lookupDescription" value="" />';
									}
									return controls;										
								}
							},
							{
								fnRender: function(obj){
									var controls;
									if (obj.aData.address1 != null) {
										controls = obj.aData.address1;
										controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1" value="' + obj.aData.address1 + '" />';
									} else {
										controls = "";
										controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1" value="" />';
									}
									return controls;
								}
							},
							{
								fnRender: function(obj){
									var controls;
									if (obj.aData.address1Area != null) {
										controls = obj.aData.address1Area.lookupDescription;
										controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Area.lookupCode" value="' + obj.aData.address1Area.lookupCode + '" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Area.lookupId" value="' + obj.aData.address1Area.lookupId + '" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Area.lookupDescription" value="' + obj.aData.address1Area.lookupDescription + '" />';
								 	} else {
								 		controls = '';
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Area.lookupCode" value="" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Area.lookupId" value="" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Area.lookupDescription" value="" />';
								 	}
									return controls;
								}
							},
							{
								fnRender: function(obj){
									var controls;
									if (obj.aData.address1Phone1 != null) {
										controls = obj.aData.address1Phone1;
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Phone1" value="' + obj.aData.address1Phone1 + '" />';
								 	} else {
								 		controls = '';
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Phone1" value="" />';
								 	}
									return controls;
								}
							},
							{
								fnRender: function(obj) {
								 	var controls;
								 	controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
								 	 	
								 	if (obj.aData.address1Country != null) {
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupCode" value="' + obj.aData.address1Country.lookupCode + '" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupId" value="' + obj.aData.address1Country.lookupId + '" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupDescription" value="' + obj.aData.address1Country.lookupDescription + '" />';
								 	} else {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupCode" value="" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupId" value="" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupDescription" value="" />';
								 	}
								 	
								 	if (obj.aData.address1State != null) {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1State.lookupCode" value="' + obj.aData.address1State.lookupCode + '" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1State.lookupId" value="' + obj.aData.address1State.lookupId + '" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1State.lookupDescription" value="' + obj.aData.address1State.lookupDescription + '" />';
								 	} else {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1State.lookupCode" value="" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1State.lookupId" value="" />';
									 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1State.lookupDescription" value="" />';
								 	}
								 	
								 	if (obj.aData.address1ZipCode != null) {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1ZipCode" value="' + obj.aData.address1ZipCode + '" />';
								 	} else {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1ZipCode" value="" />';
								 	}
								 	
								 	if (obj.aData.address1Phone2 != null) {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Phone2" value="' + obj.aData.address1Phone2 + '" />';
								 	} else {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Phone2" value="" />';
								 	}
								 	
								 	if (obj.aData.address1Phone3 != null) {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Phone3" value="' + obj.aData.address1Phone3 + '" />';
								 	} else {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Phone3" value="" />';
								 	}
								 	
								 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressExt1" value="' + obj.aData.addressExt1 + '" />';
								 	
								 	if (obj.aData.addressExt2 != null) {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressExt2" value="' + obj.aData.addressExt2 + '" />';	
								 	} else {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressExt2" value="" />';
								 	}
								 	
								 	if (obj.aData.addressExt3 != null) {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressExt3" value="' + obj.aData.addressExt3 + '" />';	
								 	} else {
								 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressExt3" value="" />';
								 	}
								 	
								 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].isCustomer" value="' + obj.aData.isCustomer + '" />';
								 	controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].contactKey" value="' + obj.aData.contactKey + '" />';
								 	
								 	return controls;
							 	}
							}
				       	  ],
			aaSorting:[[0, 'asc']],
			bAutoWidth: false,
			//bDestroy: true,
			bFilter: false,
			bInfo: false,
			bJQueryUI:true,
			bPaginate: false,
			bSearch: false,
			bLengthChange:false,
			iDisplayLength:10
	});
	
	
	// ======================================================================================================================
	
	// contact event edit ===================================================================================================
	$('#contact #grid-contact').removeAttr('style');
	$('#contact #grid-contact tbody tr td').die('click');
	$('#contact #grid-contact tbody tr td').live('click', function() {
		var rowPos = $(this).parents('tr');
		if (tableContact.fnGetNodes().length == 0) { // tambah pengecekan ini~~
			return false;
		}
		var rowPosNumber = tableContact.fnGetPosition(rowPos[0]);
		var pos = tableContact.fnGetPosition(this);
		
		var rows = tableContact.fnGetNodes().length;
		var row = $(this).parents('tr');
		var rowNumber = tableContact.fnGetPosition(row[0]);
		var datas = tableContact.fnGetData(row[0]);
		var found = false;
		
		cell = tableContact.fnGetData(this.parentNode);
		if (pos[1] != 4) { 		// kondisi buat posisi button delete tidak ke-live
			dataContact = tableContact.fnGetData(this.parentNode);
			
			$("#detailContact #contactForm").find("input[class*='fieldError']").removeClass('fieldError');
			
			$('#detailContact #contactForm .errorMessage').html("");
			
			getAddressTypeLookupDescription();
			
			if (((dataContact.addressType.lookupId) == '') || ((dataContact.addressType)=='')) {
				dataContact.addressType = new Object();
				dataContact.addressType.lookupId = '';
				dataContact.addressType.lookupDescription = '';
					$("#detailContact #contactForm #addressType").val(dataContact.addressType.lookupId);
					$("#detailContact #contactForm #addressTypeDesc").val(dataContact.addressType.lookupDescription);
				
			} else {
					$("#detailContact #contactForm #addressType").val(dataContact.addressType.lookupId);
					$("#detailContact #contactForm #addressTypeDesc").val(dataContact.addressType.lookupDescription);
			}
			$('#detailContact #contactForm #isCustomer').val(dataContact.isCustomer);
			
			$("#detailContact #contactForm #address1").val(dataContact.addressExt1);
			$("#detailContact #contactForm #address2").val(dataContact.addressExt2);
			$("#detailContact #contactForm #address3").val(dataContact.addressExt3);
			$("#detailContact #contactForm #addressHide").val(dataContact.address1);
			
			$("#detailContact #contactForm #address1City").val(dataContact.address1City);
			
			if (((dataContact.address1Country) == null) || ((dataContact.address1Country) == '') || ((dataContact.address1Country.lookupId) == '')) { 
				dataContact.address1Country = new Object();
				dataContact.address1Country.lookupCode = '';
				dataContact.address1Country.lookupId = '';
				dataContact.address1Country.lookupDescription = '';
				$("#detailContact #contactForm #address1CountryCode").val(dataContact.address1Country.lookupCode);
				$("#detailContact #contactForm #address1Country").val(dataContact.address1Country.lookupId);
				$("#detailContact #contactForm #address1CountryDesc").val(dataContact.address1Country.lookupDescription);
			} else {
				$("#detailContact #contactForm #address1CountryCode").val(dataContact.address1Country.lookupCode);
				$("#detailContact #contactForm #address1Country").val(dataContact.address1Country.lookupId);
				$("#detailContact #contactForm #address1CountryDesc").val(dataContact.address1Country.lookupDescription);
			}
			
			if (((dataContact.address1State) == null) || ((dataContact.address1State) == '') || ((dataContact.address1State.lookupId) == '')) { 
				dataContact.address1State = new Object();
				dataContact.address1State.lookupCode = '';
				dataContact.address1State.lookupId = '';
				dataContact.address1State.lookupDescription = '';
				$("#detailContact #contactForm #address1StateCode").val(dataContact.address1State.lookupCode);
				$("#detailContact #contactForm #address1State").val(dataContact.address1State.lookupId);
				$("#detailContact #contactForm #address1StateDesc").val(dataContact.address1State.lookupDescription);
			} else {
				$("#detailContact #contactForm #address1StateCode").val(dataContact.address1State.lookupCode);
				$("#detailContact #contactForm #address1State").val(dataContact.address1State.lookupId);
				$("#detailContact #contactForm #address1StateDesc").val(dataContact.address1State.lookupDescription);
			}
			
			if (((dataContact.address1Area) == null) || ((dataContact.address1Area) == '') || ((dataContact.address1Area.lookupId) == '')) { 
				dataContact.address1Area = new Object();
				dataContact.address1Area.lookupCode = '';
				dataContact.address1Area.lookupId = '';
				dataContact.address1Area.lookupDescription = '';
				$("#detailContact #contactForm #address1AreaCode").val(dataContact.address1Area.lookupCode);
				$("#detailContact #contactForm #address1Area").val(dataContact.address1Area.lookupId);
				$("#detailContact #contactForm #address1AreaDesc").val(dataContact.address1Area.lookupDescription);
			} else {
				$("#detailContact #contactForm #address1AreaCode").val(dataContact.address1Area.lookupCode);
				$("#detailContact #contactForm #address1Area").val(dataContact.address1Area.lookupId);
				$("#detailContact #contactForm #address1AreaDesc").val(dataContact.address1Area.lookupDescription);
			}
			
			$("#detailContact #contactForm #address1ZipCode").val(dataContact.address1ZipCode);
			$("#detailContact #contactForm #address1Phone1").val(dataContact.address1Phone1);
			$("#detailContact #contactForm #address1Phone2").val(dataContact.address1Phone2);
			$("#detailContact #contactForm #address1Phone3").val(dataContact.address1Phone3);
			if ((dataContact.remarks == 'null') || (dataContact.remarks == null)) {
				dataContact.remarks = '';
				$("#detailContact #contactForm #remarks").val(dataContact.remarks);
			} else {
				$("#detailContact #contactForm #remarks").val(dataContact.remarks);
			}
			
			if ((dataContact.email == 'null') || (dataContact.email == null)) {
				dataContact.email = '';
				$("#detailContact #contactForm #emailContact").val(dataContact.email);
			} else {
				$("#detailContact #contactForm #emailContact").val(dataContact.email);
			}
			
			$("#detailContact #contactForm #rowPosition").val(rowPosNumber);
			$("#detailContact #contactForm #oldAddressType").val($("#detailContact #contactForm #addressType").val());
			$("#detailContact #contactForm #newAddressType").val($("#detailContact #contactForm #oldAddressType").val());
			//console.debug("2. oldAddressType = " + $("#detailContact #contactForm #oldAddressType").val() + " newAddressType = " + $("#detailContact #contactForm #newAddressType").val());
			
			$("#detailContact #contactForm #contactKey").val(dataContact.contactKey);
			// ==============================================================================================================
				
			$('#detailContact').dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
			
			if($('#contactForm #address1CountryCode').val() != ""){
				state("STATE");
			}
			if($('#contactForm #address1StateCode').val() != ""){
				area("AREA");
			}
			
		};
	});
	
	
}

function tabContactCorp(data) {
	var fmtDate = '${appProp.jqueryDateFormat}';
	var detailCorpContacts = null;
	detailCorpContacts = ${detailCorpContacts};
	
	/*if (('${detailCorpContacts}' != ''))
		detailCorpContacts = ${detailCorpContacts.raw()};

	if (('${detailCorpContacts}' == ''))
		detailCorpContacts = new Array();*/
	


	// initialize table contact =============================================================================================
	tableContactCorp = 
		$('#contact #grid-contact-corp').dataTable({
			aaData: detailCorpContacts,
			aoColumns: [  	
							{
								fnRender: function(obj){
									var controls;
									if (obj.aData.contactType != null) {
										controls = obj.aData.contactType.lookupDescription;
										controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].contactType.lookupId" value="' + obj.aData.contactType.lookupId + '" />';
										controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].contactType.lookupDescription" value="' + obj.aData.contactType.lookupDescription + '" />';
									} else {
										controls = "";
										controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].contactType.lookupId" value="" />';
										controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].contactType.lookupDescription" value="" />';
									}
									return controls;										
								}
							},
							{
								fnRender: function(obj){
									var controls;
									if (obj.aData.contactName != null) {
										controls = obj.aData.contactName;
										controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].contactName" value="' + obj.aData.contactName + '" />';
									} else {
										controls = "";
										controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].contactName" value="" />';
									}
									return controls;
								}
							},
							{
								fnRender: function(obj){
									var controls;
									if (obj.aData.contactPosition != null) {
										controls = obj.aData.contactPosition;
										controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].contactPosition" value="' + obj.aData.contactPosition + '" />';
								 	} else {
								 		controls = '';
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].contactPosition" value="" />';
								 	}
									return controls;
								}
							},
							
							{
								fnRender: function(obj) {
								 	var controls;
								 	controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
								 	
								 	controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].firstName" value="' + obj.aData.firstName + '" />';
								 	if ((obj.aData.middleName == '')||(obj.aData.middleName == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].middleName" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].middleName" value="' + obj.aData.middleName + '" />';
								 	}
								 	
								 	if ((obj.aData.lastName == '')||(obj.aData.lastName == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].lastName" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].lastName" value="' + obj.aData.lastName + '" />';
								 	}
								 	
								 	if ((obj.aData.npwp == '')||(obj.aData.npwp == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].npwp" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].npwp" value="' + obj.aData.npwp + '" />';
								 	}
								 	
								 	if ((obj.aData.npwpDate == '')||(obj.aData.npwpDate == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].npwpDate" value="" />';
								 	} else {
								 		var stringDate = obj.aData.npwpDate.toString();
								 		var npwpDate;
								 		if (stringDate.substr(2,1) != "/") {
								 			npwpDate = $.datepicker.formatDate(fmtDate, new Date(obj.aData.npwpDate));
								 		} else {
								 			npwpDate =  obj.aData.npwpDate
								 		}
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].npwpDate" value="' + npwpDate + '" />';
								 	}
								 	
								 	if ((obj.aData.identificationType1 == '')||(obj.aData.identificationType1 == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identificationType1.lookupId" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identificationType1.lookupId" value="' + obj.aData.identificationType1.lookupId + '" />';
								 	}
								 	
								 	if ((obj.aData.identification1No == '')||(obj.aData.identification1No == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification1No" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification1No" value="' + obj.aData.identification1No + '" />';
								 	}
								 	
								 	if ((obj.aData.identification1Expiry == '')||(obj.aData.identification1Expiry == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification1Expiry" value="" />';
								 	} else {
								 		var stringDate = obj.aData.identification1Expiry.toString();
								 		var identification1Expiry;
								 		if (stringDate.substr(2,1) != "/") {
								 			identification1Expiry = $.datepicker.formatDate(fmtDate, new Date(obj.aData.identification1Expiry));
								 		} else {
								 			identification1Expiry =  obj.aData.identification1Expiry
								 		}
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification1Expiry" value="' + identification1Expiry + '" />';
								 	}
								 	
								 	if ((obj.aData.identificationType2 == '')||(obj.aData.identificationType2 == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identificationType2.lookupId" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identificationType2.lookupId" value="' + obj.aData.identificationType2.lookupId + '" />';
								 	}
								 	
								 	if ((obj.aData.identification2No == '')||(obj.aData.identification2No == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification2No" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification2No" value="' + obj.aData.identification2No + '" />';
								 	}
								 	
								 	if ((obj.aData.identification2Expiry == '')||(obj.aData.identification2Expiry == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification2Expiry" value="" />';
								 	} else {
								 		var stringDate = obj.aData.identification2Expiry.toString();
								 		var identification2Expiry;
								 		if (stringDate.substr(2,1) != "/") {
								 			identification2Expiry = $.datepicker.formatDate(fmtDate, new Date(obj.aData.identification2Expiry));
								 		} else {
								 			identification2Expiry =  obj.aData.identification2Expiry;
								 		}
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification2Expiry" value="' + identification2Expiry + '" />';
								 	}
								 	
								 	if ((obj.aData.identificationType3 == '')||(obj.aData.identificationType3 == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identificationType3.lookupId" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identificationType3.lookupId" value="' + obj.aData.identificationType3.lookupId + '" />';
								 	}
								 	
								 	if ((obj.aData.identification3No == '')||(obj.aData.identification3No == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification3No" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification3No" value="' + obj.aData.identification3No + '" />';
								 	}
								 	
								 	if ((obj.aData.identification3Expiry == '')||(obj.aData.identification3Expiry == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification3Expiry" value="" />';
								 	} else {
								 		var stringDate = obj.aData.identification3Expiry.toString();
								 		var identification3Expiry;
								 		if (stringDate.substr(2,1) != "/") {
								 			identification3Expiry = $.datepicker.formatDate(fmtDate, new Date(obj.aData.identification3Expiry));
								 		} else {
								 			identification3Expiry =  obj.aData.identification2Expiry;
								 		}
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification3Expiry" value="' + identification3Expiry + '" />';
								 	}
								 	
								 	if ((obj.aData.identificationType4 == '')||(obj.aData.identificationType4 == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identificationType4.lookupId" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identificationType4.lookupId" value="' + obj.aData.identificationType4.lookupId + '" />';
								 	}
								 	
								 	if ((obj.aData.identification4No == '')||(obj.aData.identification4No == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification4No" value="" />';
								 	} else {
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification4No" value="' + obj.aData.identification4No + '" />';
								 	}
								 	
								 	if ((obj.aData.identification4Expiry == '')||(obj.aData.identification4Expiry == null)){
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification4Expiry" value="" />';
								 	} else {
								 		var stringDate = obj.aData.identification4Expiry.toString();
								 		var identification4Expiry;
								 		if (stringDate.substr(2,1) != "/") {
								 			identification4Expiry = $.datepicker.formatDate(fmtDate, new Date(obj.aData.identification4Expiry));
								 		} else {
								 			identification4Expiry =  obj.aData.identification4Expiry;
								 		}
								 		controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].identification4Expiry" value="' + identification4Expiry + '" />';
								 	}
								 	
								 	controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].isCustomer" value="' + obj.aData.isCustomer + '" />';
								 	controls += '<input type="hidden" name="corpContacts[' + obj.iDataRow + '].contactKey" value="' + obj.aData.contactKey + '" />';
								 	return controls;
							 	}
							}
				       	  ],
			aaSorting:[[0, 'asc']],
			bAutoWidth: false,
			//bDestroy: true,
			bFilter: false,
			bInfo: false,
			bJQueryUI:true,
			bPaginate: false,
			bSearch: false,
			bLengthChange:false,
			iDisplayLength:10
	});
	
	// contact event edit ===================================================================================================
	$('#contact #grid-contact-corp').removeAttr('style');
	$('#contact #grid-contact-corp tbody tr td').die('click');
	$('#contact #grid-contact-corp tbody tr td').live('click', function() {
		var rowPos = $(this).parents('tr');
		if (tableContactCorp.fnGetNodes().length == 0) { // tambah pengecekan ini~~
			return false;
		}
		var rowPosNumber = tableContactCorp.fnGetPosition(rowPos[0]);
		var pos = tableContactCorp.fnGetPosition(this);
		
		var rows = tableContactCorp.fnGetNodes().length;
		var row = $(this).parents('tr');
		var rowNumber = tableContactCorp.fnGetPosition(row[0]);
		var datas = tableContactCorp.fnGetData(row[0]);
		var found = false;
		
		cell = tableContactCorp.fnGetData(this.parentNode);
		if (pos[1] != 3) { 		// kondisi buat posisi button delete tidak ke-live
			var fmtDate = '${appProp.jqueryDateFormat}';
			dataContact = tableContactCorp.fnGetData(this.parentNode);
			
			$("#detailContactCorp #contactForm").find("input[class*='fieldError']").removeClass('fieldError');
			$('#detailContactCorp #contactForm #identificationType1').removeClass('fieldError');
			$('#detailContactCorp #contactForm #identificationType2').removeClass('fieldError');
			$('#detailContactCorp #contactForm #identificationType3').removeClass('fieldError');
			$('#detailContactCorp #contactForm .errorMessage').html("");
			$('#detailContactCorp #contactForm #contacTypeCorp').removeClass('fieldError');
			
			
			getAddressTypeLookupDescription();
			
			if (((dataContact.contactType.lookupId) == '') || ((dataContact.contactType)=='')) {
				dataContact.contactType = new Object();
				dataContact.contactType.lookupId = '';
				dataContact.contactType.lookupDescription = '';
				$("#detailContactCorp #contactForm #contacTypeCorp").val(dataContact.contactType.lookupId);
				$("#detailContactCorp #contactForm #contactTypeDescCorp").val(dataContact.contactType.lookupDescription);
			} else {
				$("#detailContactCorp #contactForm #contacTypeCorp").val(dataContact.contactType.lookupId);
				$("#detailContactCorp #contactForm #contactTypeDescCorp").val(dataContact.contactType.lookupDescription);
			}
			$("#detailContactCorp #contactForm #rowPosition").val(rowPosNumber);
			$("#detailContactCorp #contactForm #isCustomer").val(dataContact.isCustomer);
			$("#detailContactCorp #contactForm #contactName").val(dataContact.contactName);
			$("#detailContactCorp #contactForm #contactNameHide").val(dataContact.contactName);
			$("#detailContactCorp #contactForm #firstNameCorp").val(dataContact.firstName);
			$("#detailContactCorp #contactForm #middleNameCorp").val(dataContact.middleName);
			$("#detailContactCorp #contactForm #lastNameCorp").val(dataContact.lastName);
			$("#detailContactCorp #contactForm #contactPosition").val(dataContact.contactPosition);
			$("#detailContactCorp #contactForm #contactNpwpNo").val(dataContact.npwp);
			if (dataContact.npwpDate!=''){
				if (dataContact.npwpDate!=null) {
					var stringDate = dataContact.npwpDate.toString();
					if (stringDate.substr(2,1) != "/") {
						if ($.browser.msie) {
							if (stringDate.substr(4,1) != "-") {
								$("#detailContactCorp #contactForm #contactNpwpRegDate").val($.datepicker.formatDate(fmtDate, new Date(dataContact.npwpDate)));
							} else {
								$("#detailContactCorp #contactForm #contactNpwpRegDate").val($.datepicker.formatDate(fmtDate, new Date(dataContact.npwpDate.toString().replace(/-/g,"/"))));
							}
						} else {
							$("#detailContactCorp #contactForm #contactNpwpRegDate").val($.datepicker.formatDate(fmtDate, new Date(dataContact.npwpDate)));
						}
					} else {
						$("#detailContactCorp #contactForm #contactNpwpRegDate").val(dataContact.npwpDate);
					}
				} else {
					$("#detailContactCorp #contactForm #contactNpwpRegDate").val('');
				}
			}else {
				$("#detailContactCorp #contactForm #contactNpwpRegDate").val('');
			}
			$('#detailContactCorp #contactForm #oldContactType').val($("#detailContactCorp #contactForm #contacTypeCorp").val());
			$('#detailContactCorp #contactForm #newContactType').val($("#detailContactCorp #contactForm #oldContactType").val());
			
			if (((dataContact.identificationType1) == null) || ((dataContact.identificationType1) == '') || ((dataContact.identificationType1.lookupId) == '')) {
				dataContact.identificationType1 = new Object();
				dataContact.identificationType1.lookupId = '';
				$("#detailContactCorp #contactForm #identificationType1").val(dataContact.identificationType1.lookupId);
			} else {
				$("#detailContactCorp #contactForm #identificationType1").val(dataContact.identificationType1.lookupId);
			}
			
			$("#detailContactCorp #contactForm #identification1No").val(dataContact.identification1No);
			if (dataContact.identification1Expiry != '') {
				if (dataContact.identification1Expiry == null) {
					$("#detailContactCorp #contactForm #identification1ExpiryCon").val('');
				} else {
					var stringDate = dataContact.identification1Expiry.toString();
					if (stringDate.substr(2,1) != "/") {
						$("#detailContactCorp #contactForm #identification1ExpiryCon").val($.datepicker.formatDate(fmtDate, new Date(dataContact.identification1Expiry)));
					}else {
						$("#detailContactCorp #contactForm #identification1ExpiryCon").val(dataContact.identification1Expiry);
						
					}
				}
			} else {
				$("#detailContactCorp #contactForm #identification1ExpiryCon").val('');
			}
			
			
			if (((dataContact.identificationType2) == null) || ((dataContact.identificationType2) == '') || ((dataContact.identificationType2.lookupId) == '')) {
				dataContact.identificationType2 = new Object();
				dataContact.identificationType2.lookupId = '';
				$("#detailContactCorp #contactForm #identificationType2").val(dataContact.identificationType2.lookupId);
			} else {
				$("#detailContactCorp #contactForm #identificationType2").val(dataContact.identificationType2.lookupId);
			}
			
			$("#detailContactCorp #contactForm #identification2No").val(dataContact.identification2No);
			if (dataContact.identification2Expiry != '') {
				if (dataContact.identification2Expiry == null) {
					$("#detailContactCorp #contactForm #identification2ExpiryCon").val('');
				} else {
					var stringDate = dataContact.identification2Expiry.toString();
					if (stringDate.substr(2,1) != "/") {
						$("#detailContactCorp #contactForm #identification2ExpiryCon").val($.datepicker.formatDate(fmtDate, new Date(dataContact.identification2Expiry)));
					} else {
						$("#detailContactCorp #contactForm #identification2ExpiryCon").val(dataContact.identification2Expiry);
					}
				}
			} else {
				$("#detailContactCorp #contactForm #identification2ExpiryCon").val('');
			}
			
			
			if (((dataContact.identificationType3) == null) || ((dataContact.identificationType3) == '') || ((dataContact.identificationType3.lookupId) == '')) {
				dataContact.identificationType3 = new Object();
				dataContact.identificationType3.lookupId = '';
				$("#detailContactCorp #contactForm #identificationType3").val(dataContact.identificationType3.lookupId);
			} else {
				$("#detailContactCorp #contactForm #identificationType3").val(dataContact.identificationType3.lookupId);
			}
			
			$("#detailContactCorp #contactForm #identification3No").val(dataContact.identification3No);
			
			
			if (dataContact.identification3Expiry != '') {
				if (dataContact.identification3Expiry == null) {
					$("#detailContactCorp #contactForm #identification3ExpiryCon").val('');
				} else {
					var stringDate = dataContact.identification3Expiry.toString();
					if (stringDate.substr(2,1) != "/") {
						$("#detailContactCorp #contactForm #identification3ExpiryCon").val($.datepicker.formatDate(fmtDate, new Date(dataContact.identification3Expiry)));
					} else {
						$("#detailContact #contactForm #identification3ExpiryCon").val(dataContact.identification3Expiry);
					}
				}
			} else {
				$("#detailContactCorp #contactForm #identification3ExpiryCon").val('');
			}
			
			//console.debug("added point zero = --" + dataContact.identification3RegDate + "--");
			
			if (((dataContact.identificationType4) == null) || ((dataContact.identificationType4) == '') || ((dataContact.identificationType4.lookupId) == '')) {
				dataContact.identificationType4 = new Object();
				dataContact.identificationType4.lookupId = '';
				$("#detailContactCorp #contactForm #identificationType4").val(dataContact.identificationType4.lookupId);
			} else {
				$("#detailContactCorp #contactForm #identificationType4").val(dataContact.identificationType4.lookupId);
			}
			
			$("#detailContactCorp #contactForm #identification4No").val(dataContact.identification4No);
			
			//dataContact = tableContact.fnGetData(this.parentNode);
			
			if (dataContact.identification4Expiry != '') {
				if (dataContact.identification4Expiry == null) {
					$("#detailContactCorp #contactForm #identification4ExpiryCon").val('');
				} else {
					var stringDate = dataContact.identification4Expiry.toString();
					if (stringDate.substr(2,1) != "/") {
						$("#detailContactCorp #contactForm #identification4ExpiryCon").val($.datepicker.formatDate(fmtDate, new Date(dataContact.identification4Expiry)));
					} else{
						$("#detailContactCorp #contactForm #identification4ExpiryCon").val(dataContact.identification4Expiry);
					}
				}
			} else {
				$("#detailContactCorp #contactForm #identification4ExpiryCon").val('');
			}
			
			
			$("#detailContactCorp #contactForm #contactKey").val(dataContact.contactKey);
			if ($('#detailContactCorp #contactForm #middleNameCorp').val()==''){
				$('p[id=pLastNameCorp] label span').html('');
			} else {
				$('p[id=pLastNameCorp] label span').html(' *');
			}
			// ==============================================================================================================
				
			$('#detailContactCorp').dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
		};
	});
}

function assignmentValueForSameAs(datas) {
	$('#address1').val(datas.address1);
	$('#address1City').val(datas.address1City);
	if (datas.address1Country == null || datas.address1Country == '') {
		$('#address1CountryCode').val('');
		$('#address1Country').val('');
		$('#address1CountryDesc').val('');
	} else {
		$('#address1CountryCode').val(datas.address1Country.lookupCode);
		$('#address1Country').val(datas.address1Country.lookupId);
		$('#address1CountryDesc').val(datas.address1Country.lookupDescription);	
	}
	
	if (datas.address1State == null || datas.address1State == '') {
		$('#address1StateCode').val('');
		$('#address1State').val('');
		$('#address1StateDesc').val('');
	} else {
		$('#address1StateCode').val(datas.address1State.lookupCode);
		$('#address1State').val(datas.address1State.lookupId);
		$('#address1StateDesc').val(datas.address1State.lookupDescription);
	}
	
	if (datas.address1Area == null || datas.address1Area == '') {
		$('#address1AreaCode').val('');
		$('#address1Area').val('');
		$('#address1AreaDesc').val('');
	} else {
		$('#address1AreaCode').val(datas.address1Area.lookupCode);
		$('#address1Area').val(datas.address1Area.lookupId);
		$('#address1AreaDesc').val(datas.address1Area.lookupDescription);
	}
	
	$('#address1ZipCode').val(datas.address1ZipCode);
	$('#address1Phone1').val(datas.address1Phone1);
	$('#address1Phone2').val(datas.address1Phone2);
	$('#address1Phone3').val(datas.address1Phone3);
}


function state(data) {
	// address 1 state lookup at tab contacts ===============================================================================
	$('#contactForm #address1StateCode').lookup({
		list:'@{Pick.lookups()}?group='+data,
		get:{
			url:'@{Pick.lookup()}?group='+data,
			success: function(data) {
				if (data) {
					if ($('#contactForm #address1CountryCode').val() == "" || $('#contactForm #address1CountryCode').val() == null) {
						$('#contactForm #address1StateCode').addClass('fieldError');
						$('#contactForm #address1StateDesc').val('NOT FOUND');
						$('#contactForm #address1StateCode').val('');
						$('#contactForm #address1State').val('');
						$('#contactForm #h_address1StateDesc').val('');
						return false;
					}
					$('#contactForm #address1StateCode').removeClass('fieldError');
					$('#contactForm #address1State').val(data.code);
					$('#contactForm #address1StateDesc').val(data.description);
					$('#contactForm #h_address1StateDesc').val(data.description);
					
					$('#contactForm #address1AreaCode').val("");
					$('#contactForm #address1Area').val("");
					$('#contactForm #address1AreaDesc').val("");
					$('#contactForm #address1AreaCode').removeClass('fieldError');
					area("AREA");
				}
			},
			error : function(data) {
				$('#contactForm #address1StateCode').addClass('fieldError');
				$('#contactForm #address1StateDesc').val('NOT FOUND');
				$('#contactForm #address1StateCode').val('');
				$('#contactForm #address1State').val('');
				$('#contactForm #h_address1StateDesc').val('');
			}
		},
		filter: {'key':'1','value':$('#contactForm #address1Country')},
		key:$('#contactForm #address1State'),
		description:$('#contactForm #address1StateDesc'),
		help:$('#contactForm #address1StateHelp'),
		nextControl:$('#contactForm #address1AreaCode')
	});
	// ======================================================================================================================
}

function area(data) {
	// address 1 area lookup at tab contacts ================================================================================
	$('#contactForm #address1AreaCode').lookup({
		list:'@{Pick.lookups()}?group='+data,
		get:{
			url:'@{Pick.lookup()}?group='+data,
			success: function(data) {
				if (data) {
					if ($('#contactForm #address1StateCode').val() == "" || $('#contactForm #address1StateCode').val() == null) {
						$('#contactForm #address1AreaCode').addClass('fieldError');
						$('#contactForm #address1AreaDesc').val('NOT FOUND');
						$('#contactForm #address1AreaCode').val('');
						$('#contactForm #address1Area').val('');
						$('#contactForm #h_address1AreaDesc').val('');
						return false;
					}
					$('#contactForm #address1AreaCode').removeClass('fieldError');
					$('#contactForm #address1Area').val(data.code);
					$('#contactForm #address1AreaDesc').val(data.description);
					$('#contactForm #h_address1AreaDesc').val(data.description);
				}
			},
			error : function(data) {
				$('#contactForm #address1AreaCode').addClass('fieldError');
				$('#contactForm #address1AreaDesc').val('NOT FOUND');
				$('#contactForm #address1AreaCode').val('');
				$('#contactForm #address1Area').val('');
				$('#contactForm #h_address1AreaDesc').val('');
			}
		},
		filter:{'key':'2','value':$('#contactForm #address1State')},
		key:$('#contactForm #address1Area'),
		description:$('#contactForm #address1AreaDesc'),
		help:$('#contactForm #address1AreaHelp'),
		nextControl:$('#contactForm #address1ZipCode')
	});
	// ======================================================================================================================
}

function reorderingContact() {
	var tableContactCorp = $('#contact #grid-contact-corp');
	var trs = $("tr", tableContactCorp);
	$.each(trs, function(idx, data){
		var hiddens = $("[type=hidden]", $(this));
		$(hiddens).eq(0).attr("name", "corpContacts["+(idx-1)+"].contactType.lookupId");
		$(hiddens).eq(1).attr("name", "corpContacts["+(idx-1)+"].contactType.lookupDescription");
		$(hiddens).eq(2).attr("name", "corpContacts["+(idx-1)+"].contactName");
		$(hiddens).eq(3).attr("name", "corpContacts["+(idx-1)+"].contactPosition");
		$(hiddens).eq(4).attr("name", "corpContacts["+(idx-1)+"].firstName");
		$(hiddens).eq(5).attr("name", "corpContacts["+(idx-1)+"].middleName");
		$(hiddens).eq(6).attr("name", "corpContacts["+(idx-1)+"].lastName");
		$(hiddens).eq(7).attr("name", "corpContacts["+(idx-1)+"].npwp");
		$(hiddens).eq(8).attr("name", "corpContacts["+(idx-1)+"].npwpDate");
		$(hiddens).eq(9).attr("name", "corpContacts["+(idx-1)+"].identificationType1.lookupId");
		$(hiddens).eq(10).attr("name", "corpContacts["+(idx-1)+"].identification1No");
		$(hiddens).eq(11).attr("name", "corpContacts["+(idx-1)+"].identification1Expiry");
		$(hiddens).eq(12).attr("name", "corpContacts["+(idx-1)+"].identificationType2.lookupId");
		$(hiddens).eq(13).attr("name", "corpContacts["+(idx-1)+"].identification2No");
		$(hiddens).eq(14).attr("name", "corpContacts["+(idx-1)+"].identification2Expiry");
		$(hiddens).eq(15).attr("name", "corpContacts["+(idx-1)+"].identificationType3.lookupId");
		$(hiddens).eq(16).attr("name", "corpContacts["+(idx-1)+"].identification3No");
//		$(hiddens).eq(17).attr("name", "corpContacts["+(idx-1)+"].identification3RegDate");
		$(hiddens).eq(17).attr("name", "corpContacts["+(idx-1)+"].identification3Expiry");
		$(hiddens).eq(18).attr("name", "corpContacts["+(idx-1)+"].identificationType4.lookupId");
		$(hiddens).eq(19).attr("name", "corpContacts["+(idx-1)+"].identification4No");
		$(hiddens).eq(20).attr("name", "corpContacts["+(idx-1)+"].identification4Expiry");
		$(hiddens).eq(21).attr("name", "corpContacts["+(idx-1)+"].isCustomer");
		$(hiddens).eq(22).attr("name", "corpContacts["+(idx-1)+"].contactKey");
	});
}

//for reordering contact ===================================================================================================
function reordering() {
	var tableContact = $('#contact #grid-contact');
	var trs = $("tr", tableContact);
	$.each(trs, function(idx, data){
		var hiddens = $("[type=hidden]", $(this));
			$(hiddens).eq(0).attr("name", "contacts["+(idx-1)+"].addressType.lookupId");
			$(hiddens).eq(1).attr("name", "contacts["+(idx-1)+"].addressType.lookupDescription");
			$(hiddens).eq(2).attr("name", "contacts["+(idx-1)+"].address1");
			$(hiddens).eq(3).attr("name", "contacts["+(idx-1)+"].address1Area.lookupCode");
			$(hiddens).eq(4).attr("name", "contacts["+(idx-1)+"].address1Area.lookupId");
			$(hiddens).eq(5).attr("name", "contacts["+(idx-1)+"].address1Area.lookupDescription");
			$(hiddens).eq(6).attr("name", "contacts["+(idx-1)+"].address1Phone1");
			$(hiddens).eq(7).attr("name", "contacts["+(idx-1)+"].address1Country.lookupCode");
			$(hiddens).eq(8).attr("name", "contacts["+(idx-1)+"].address1Country.lookupId");
			$(hiddens).eq(9).attr("name", "contacts["+(idx-1)+"].address1Country.lookupDescription");
			$(hiddens).eq(10).attr("name", "contacts["+(idx-1)+"].address1State.lookupCode");
			$(hiddens).eq(11).attr("name", "contacts["+(idx-1)+"].address1State.lookupId");
			$(hiddens).eq(12).attr("name", "contacts["+(idx-1)+"].address1State.lookupDescription");
			$(hiddens).eq(13).attr("name", "contacts["+(idx-1)+"].address1ZipCode");
			$(hiddens).eq(14).attr("name", "contacts["+(idx-1)+"].address1Phone2");
			$(hiddens).eq(15).attr("name", "contacts["+(idx-1)+"].address1Phone3");
			$(hiddens).eq(16).attr("name", "contacts["+(idx-1)+"].addressExt1");
			$(hiddens).eq(17).attr("name", "contacts["+(idx-1)+"].addressExt2");
			$(hiddens).eq(18).attr("name", "contacts["+(idx-1)+"].addressExt3");
			$(hiddens).eq(19).attr("name", "contacts["+(idx-1)+"].isCustomer");
			$(hiddens).eq(20).attr("name", "contacts["+(idx-1)+"].contactKey");
	});
}
// ==========================================================================================================================

//for check all identification type at tab contact =========================================================================
function checkIdentificationTypeCon() {
	if (($("#contactForm #identificationType1").val() == $("#contactForm #identificationType2").val()) || ($("#contactForm #identificationType2").val() == $("#contactForm #identificationType1").val())) {
		$('#contactForm #identificationType3').removeClass('fieldError');
		$('#errmsgIDType3').html("");
		$('#contactForm #identificationType4').removeClass('fieldError');
		$('#errmsgIDType4').html("");
		if (($("#contactForm #identificationType1").val() !="") && ($("#contactForm #identificationType2").val() !="")) {
			$('#contactForm #identificationType2').addClass('fieldError');
			$('#errmsgIDType2').html("can not same with ID Type 1");
		}
	} else if (($("#contactForm #identificationType1").val() == $("#contactForm #identificationType3").val()) || ($("#contactForm #identificationType3").val() == $("#contactForm #identificationType1").val())) {
		$('#contactForm #identificationType2').removeClass('fieldError');
		$('#errmsgIDType2').html("");
		$('#contactForm #identificationType4').removeClass('fieldError');
		$('#errmsgIDType4').html("");
		if (($("#contactForm #identificationType1").val() !="") && ($("#contactForm #identificationType3").val() !="")) {
			$('#contactForm #identificationType3').addClass('fieldError');
			$('#errmsgIDType3').html("can not same with ID Type 1");
		}
	} else if (($("#contactForm #identificationType1").val() == $("#contactForm #identificationType4").val()) || ($("#contactForm #identificationType4").val() == $("#contactForm #identificationType1").val())) {
		$('#contactForm #identificationType2').removeClass('fieldError');
		$('#contactForm #errmsgIDType2').html("");
		$('#contactForm #identificationType3').removeClass('fieldError');
		$('#contactForm #errmsgIDType3').html("");
		if (($("#contactForm #identificationType1").val() != "") && ($("#contactForm #identificationType4").val() != "")) {
			$('#contactForm #identificationType4').addClass('fieldError');
			$('#errmsgIDType4').html("can not same with Identification 1");
		}
	} else if (($("#contactForm #identificationType2").val() == $("#contactForm #identificationType3").val()) || ($("#contactForm #identificationType3").val() == $("#contactForm #identificationType2").val())) {
		$('#contactForm #identificationType2').removeClass('fieldError');
		$('#errmsgIDType2').html("");
		$('#contactForm #identificationType4').removeClass('fieldError');
		$('#errmsgIDType4').html("");
		if (($("#contactForm #identificationType2").val() !="") && ($("#contactForm #identificationType3").val() !="")) {
			$('#contactForm #identificationType3').addClass('fieldError');
			$('#errmsgIDType3').html("can not same with ID Type 2");
		} else {
			$('#contactForm #identificationType3').removeClass('fieldError');
			$('#errmsgIDType3').html("");
		}
	} else if (($("#contactForm #identificationType2").val() == $("#contactForm #identificationType4").val()) || ($("#contactForm #identificationType4").val() == $("#contactForm #identificationType2").val())) {
		$('#contactForm #identificationType2').removeClass('fieldError');
		$('#contactForm #errmsgIDType2').html("");
		$('#contactForm #identificationType3').removeClass('fieldError');
		$('#contactForm #errmsgIDType3').html("");
		if (($("#contactForm #identificationType2").val() != "") && ($("#contactForm #identificationType4").val() != "")) {
			$('#contactForm #identificationType4').addClass('fieldError');
			$('#errmsgIDType4').html("can not same with Identification 2");
		} else {
			$('#contactForm #identificationType4').removeClass('fieldError');
			$('#errmsgIDType4').html("");
		}
	} else if (($("#contactForm #identificationType3").val() == $("#contactForm #identificationType4").val()) || ($("#contactForm #identificationType4").val() == $("#contactForm #identificationType3").val())) {
		$('#contactForm #identificationType2').removeClass('fieldError');
		$('#contactForm #errmsgIDType2').html("");
		$('#contactForm #identificationType3').removeClass('fieldError');
		$('#contactForm #errmsgIDType3').html("");
		if (($("#contactForm #identificationType3").val() != "") && ($("#contactForm #identificationType4").val() != "")) {
			$('#contactForm #identificationType4').addClass('fieldError');
			$('#errmsgIDType4').html("can not same with Identification 3");
		} else {
			$('#contactForm #identificationType4').removeClass('fieldError');
			$('#errmsgIDType4').html("");
		}
	} else {
		$('#contactForm #identificationType2').removeClass('fieldError');
		$('#errmsgIDType2').html("");
		$('#contactForm #identificationType3').removeClass('fieldError');
		$('#errmsgIDType3').html("");
		$('#contactForm #identificationType4').removeClass('fieldError');
		$('#errmsgIDType4').html("");
	} 
}

function getContactTypeLookupDescription(){
	var selected = $('#contactForm #contacTypeCorp').val();
	var selectedText = $("#contactForm #contacTypeCorp option[value=" + selected + "]").text();
	if (selectedText != '') {
			
			selectedText = jQuery.trim(selectedText);
			selectedText = selectedText.substring(jQuery.trim(selectedText).indexOf('-') + 1);
	}
	$("#contactForm #contactTypeDescCorp").val(selectedText);
	// END OF SUBSTRING
	addressTypeDescription = selectedText;
	$("#contactForm #contactTypeDescCorp").val(addressTypeDescription);
	$('#newContactType').val($("#contacTypeCorp").val());
}
// for get lookupDescription from dropdown addressType ======================================================================
function getAddressTypeLookupDescription() {
		// SUBSTRING WHEN LOOKUPDESCRIPTION/OPTION FROM DROPDOWN SHOW ON DATATABLES SCREEN
		var selected = $('#contactForm #addressType').val();
		var selectedText = $("#contactForm #addressType option[value=" + selected + "]").text();
		if (selectedText != '') {
				selectedText = jQuery.trim(selectedText);
				selectedText = selectedText.substring(jQuery.trim(selectedText).indexOf('-') + 1);
		}
		$("#contactForm #addressTypeDesc").val(selectedText);
		// END OF SUBSTRING
		addressTypeDescription = selectedText;
		$("#contactForm #addressTypeDesc").val(addressTypeDescription);
		$('#newAddressType').val($("#addressType").val());

}
// ==========================================================================================================================

// function new contact when data already exist =============================================================================
function newContactCorp() {
	$('#detailContactCorp').dialog('open');
	$("#detailContactCorp #contactForm").find("input[class*='fieldError']").removeClass('fieldError');	
	$("#detailContactCorp #contactForm input:text").val(""); 
	$("#detailContactCorp #contactForm input:hidden").val("");
	$("#detailContactCorp #contactForm #contacTypeCorp").val("");
	$("#detailContactCorp #contactForm #contacTypeCorp").val("");
	$("#detailContactCorp #contactForm #identificationType1").val("");
	$("#detailContactCorp #contactForm #identificationType2").val("");
	$("#detailContactCorp #contactForm #identificationType3").val("");
	$("#detailContactCorp #contactForm #identificationType4").val("");
	$('#detailContactCorp #contactForm .errorMessage').html('');
	$('#detailContactCorp #contactForm #identificationType1').enabled();
	if (($('#customerType').val()=='${typeCorp}')&&($('#customerCategory').val()=='${directCat}')){
		$('#detailContactCorp #contactForm #identificationType1').val('${idKtp}');
		$('#detailContactCorp #contactForm #identificationType1').disabled();
	}
	/*if (($('#customerType').val()=='${typeCorp}')&&($('#customerCategory').val()=='${indirectCat}')){
		$('#detailContactCorp #contactForm #identificationType1').val('${idPassport}');
		$('#detailContactCorp #contactForm #identificationType1').disabled();
	}*/
	if ($('#detailContactCorp #contactForm #middleNameCorp').val()==''){
		$('p[id=pLastNameCorp] label span').html('');
	}
	$('.ui-widget-overlay').css('height',$('body').height());
	return false;
}
function newContact() {
	//selectedRow = null;
	$('#detailContact').dialog('open');	

	$('#emailContactError').html('');
	
	$('#detailContact #contactForm .errorMessage').html("");
	$("#detailContact #contactForm input:text").val(""); 
	$("#detailContact #contactForm input:hidden").val("");
	$("#detailContact #contactForm").find("input[class*='fieldError']").removeClass('fieldError');
	$('#detailContact #contactForm #addressType').val("");
	$('.ui-widget-overlay').css('height',$('body').height());
//	$('input').removeClass('fieldError');
}
// ==========================================================================================================================
