$(function(){

	var data = new Object();
	
	var closeDialog = function() {
		$("#dialog-message").dialog('close');
	}

	$('#contactForm #address1CountryCodeContact').dynapopup2(
			{
				key:'#contactForm #address1CountryContact', 
				help:'#contactForm #address1CountryHelpContact', 
				desc:'#contactForm #address1CountryDescContact'
			},'PICK_GN_LOOKUP', "COUNTRY", "#contactForm #address1StateCodeContact", 
			function(data){
				if (data) {
					$('#contactForm address1CountryCodeContact').removeClass('fieldError');
					$('#contactForm #address1CountryContact').val(data.code);
					$('#contactForm #address1CountryDescContact').val(data.description);
				}
			},
			function(data){
				$('#contactForm #address1CountryCodeContact').addClass('fieldError');
				$('#contactForm #address1CountryDescContact').val('NOT FOUND');
				$('#contactForm #address1CountryCodeContact').val('');
				$('#contactForm #address1CountryContact').val('');
			}
	);
	
	$('#saveConCorp').die("click");
	$('#saveConCorp').live("click", function(){
		var oldContactType = $("#detailContact #contactForm #oldContactType").val();
		var newContactType = $("#detailContact #contactForm #newContactType").val();
		var table = $('#grid-contact-corp').dataTable();
		var rowPosition = $('#detailContact #contactForm #rowPosition').val();
		var pos = tableContact.fnGetPosition(this);
		var rows = tableContact.fnGetNodes().length;
		var found = false;
		var checkClass;
		var customerType = $('#customerType').val();
		var occupation = $('#occupation').val();
		var isBO = $('#isBO').val();
		var contactDesc = $('#contactTypeDesc').val();
        var checkBO = chekIsBenefiaryContact();
		
		if (($('#contactType').val()=='')||($('#firstNameContact').val()=='')||
				($('#detailContact #contactForm #identificationTypeContact').val()=='')||
				($('#birthPlaceContact').val()=='')||
				(($('#natureBusinessContact').val()=='')&&(customerType == '${typeIndi}'))||
				($('#identificationNoContact').val()=='')||(!$('#birthDateContact').val())||
				(!$('#address1CountryCodeContact').val())||
				((isBO == 'true')&&(!$('#occupationContact').val())&&(customerType == '${typeIndi}'))||
		   (($('#detailContact #contactForm #identificationTypeContact').val()=='${idKtp}')&&($('#detailContact #contactForm #identificationNoContact').val()==''))||
		   (($('#detailContact #contactForm #identificationTypeContact').val()!='${idKtp}')&&(($('#detailContact #contactForm #identificationNoContact').val()=='')))||
		   (($('#detailContact #contactForm #middleNameContact').val()!='')&&($('#detailContact #contactForm #lastNameContact').val()==''))){
			
			if (!$('#contactType').val()){
				$('#contactTypeError').html('Required');
			} else {
				$('#contactTypeError').html('');
			}
			
			if (!$('#firstNameContact').val()){
				$('#firstNameContactError').html('Required');
			} else {
				$('#firstNameContactError').html('');
			}
			
			if (!$('#identificationTypeContact').val() || !$('#identificationNoContact').val()){
				$('#identificationContactError').html('Required');
			} else {
				$('#identificationContactError').html('');
			}
			
			if (!$('#birthPlaceContact').val()){
				$('#birthPlaceContactError').html('Required');
			} else {
				$('#birthPlaceContactError').html('');
			}
			
			if (!$('#birthDateContact').val()){
				$('#birthDateContactError').html('Required');
			} else {
				$('#birthDateContactError').html('');
			}
			
			if (!$('#address1CountryCodeContact').val()){
				$('#address1CountryContactError').html('Required');
			} else {
				$('#address1CountryContactError').html('');
			}
			
			/*if (customerType == '${typeIndi}'){
				if (!$('#relationContact').val()){
					$('#relationContactError').html('Required');
				} else {
					$('#relationContactError').html('');
				}
			}*/
			if ((isBO == 'true')) {
				if (customerType == '${typeIndi}'){
					if (!$('#occupationContact').val()){
						$('#occupationContactError').html('Required');
					} else {
						$('#occupationContactError').html('');
					}
				}
			}
			
			/*if (!$('#positionContact').val()){
				$('#positionContactError').html('Required');
			} else {
				$('#positionContactError').html('');
			}*/
			
			if (customerType == '${typeIndi}'){
				if (!$('#natureBusinessContact').val()){
					$('#natureBusinessContactError').html('Required');
				} else {
					$('#natureBusinessContactError').html('');
				}
			}
			
			return false;
		}
		
		/*if (contactDesc.includes('UBO')) {
			if (!$('#sourceOfFundContact').val()){
				$('#sourceOfFundContactError').html('Required');
				return false;
			}
		} else {
			$('#sourceOfFundContactError').html('');
		}*/
		
		$('#detailContact #contactForm input').each(function(idx, obj){
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
		$('#detailContact #contactForm input.fieldAppDateError').each(function(idx, obj){
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
		
		$('#detailContact #contactForm .errorMessage').html('');
		var dataContact = table.fnGetData(parseFloat(rowPosition));
			// save contact when data already exist (updated) ===============================================================
			if (rowPosition != "") {
				var rows = table.fnGetNodes().length;
				for (var i = 0; i < rows; i++) {
					var cell = tableContact.fnGetData(i);
					if (($("#contactForm #contactType").val() == $(cell[0]).val()) && (oldContactType != newContactType)) {					
						$('#contactForm #contactType').addClass('fieldError');
						$("#contactTypeError").html("Already Exist");
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
									(dataContact.addressType.lookupId = $("#detailContact #contactForm #contactType").val()) &&
									(dataContact.addressType.lookupDescription = $("#detailContact #contactForm #contactTypeDesc").val()),
									parseFloat(rowPosition), 0
							  );
					
					if ($('#detailContact #contactForm #contactNameContact').val() == '') {
						table.fnUpdate(
										dataContact.contactName = "",
										parseFloat(rowPosition), 1
									 );
					} else {
						table.fnUpdate(
										dataContact.contactName = $('#detailContact #contactForm #contactNameContactHide').val().toUpperCase(),
										parseFloat(rowPosition), 1
									 );
					}
					
					if ($('#detailContact #contactForm #positionContact').val() == '') {
						table.fnUpdate(
										dataContact.position = "",
										parseFloat(rowPosition), 2);
					} else {
						table.fnUpdate(
								
										dataContact.position = $('#detailContact #contactForm #positionContact').val(),
										parseFloat(rowPosition), 2);
					}

					if ($('#detailContact #contactForm #firstNameContact').val() == '') {
						dataContact.firstName = "";
					} else {
						dataContact.firstName = $('#detailContact #contactForm #firstNameContact').val();
					}
						
					if ($('#detailContact #contactForm #middleNameContact').val() == '') {
						dataContact.middleName = "";
					} else {
						dataContact.middleName = $('#detailContact #contactForm #middleNameContact').val();
					}
					
					if ($('#detailContact #contactForm #lastNameContact').val() == '') {
						dataContact.lastName = "";
					} else {
						dataContact.lastName = $('#detailContact #contactForm #lastNameContact').val();
					}
					
					if ($('#detailContact #contactForm #birthPlaceContact').val() == '') {
						dataContact.birthPlace = "";
					} else {
						dataContact.birthPlace = $('#detailContact #contactForm #birthPlaceContact').val();
					}
					
					if ($('#detailContact #contactForm #birthDateContact').val()=='') {
						dataContact.birthDate = "";
					} else {
						dataContact.birthDate = $('#detailContact #contactForm #birthDateContact').val();
					}
					
					if (($('#detailContact #contactForm #address1CountryCodeContact').val() == '') || ($('#contactForm #address1CountryCodeContact').val() == null)) {
						dataContact.address1Country = new Object();
						dataContact.address1Country.lookupCode = "";
						dataContact.address1Country.lookupId = "";
						dataContact.address1Country.lookupDescription = "";
					} else {
						dataContact.address1Country = new Object();
						dataContact.address1Country.lookupCode = $('#detailContact #contactForm #address1CountryCodeContact').val();
						dataContact.address1Country.lookupId = $('#detailContact #contactForm #address1CountryContact').val();
						dataContact.address1Country.lookupDescription = $('#detailContact #contactForm #address1CountryDescContact').val();
					}
					
					if (($('#detailContact #contactForm #identificationTypeContact').val() == '') || ($('#contactForm #identificationTypeContact').val() == null)) {
						dataContact.identificationType = new Object();
						dataContact.identificationType.lookupId = "";
					} else {
						dataContact.identificationType = new Object();
						dataContact.identificationType.lookupId = $('#detailContact #contactForm #identificationTypeContact').val();
					}
					
					if ($('#detailContact #contactForm #identificationNoContact').val() == '') {
						dataContact.identificationNo = "";
					} else {
						dataContact.identificationNo = $('#detailContact #contactForm #identificationNoContact').val();
					}		
					
					if ($('#detailContact #contactForm #sourceOfFundContact').val() == '') {
						dataContact.sourceOfFund.lookupId = "";
					} else {
						dataContact.sourceOfFund.lookupId = $('#detailContact #contactForm #sourceOfFundContact').val();
					}
					
					if ($('#detailContact #contactForm #occupationContact').val() == '') {
						dataContact.occupation.lookupId = "";
					} else {
						dataContact.occupation.lookupId = $('#detailContact #contactForm #occupationContact').val();
					}
					
					if ($('#detailContact #contactForm #positionContact').val() == '') {
						dataContact.position = "";
					} else {
						dataContact.position = $('#detailContact #contactForm #positionContact').val();
					}
					
					if ($('#detailContact #contactForm #relationContact').val() == '') {
						dataContact.relation = "";
					} else {
						dataContact.relation = $('#detailContact #contactForm #relationContact').val();
					}
					
					if ($('#detailContact #contactForm #natureBusinessContact').val() == '') {
						dataContact.natureBusiness.lookupId = "";
					} else {
						dataContact.natureBusiness.lookupId = $('#detailContact #contactForm #natureBusinessContact').val();
					}
					
					table.fnUpdate(dataContact,parseFloat(rowPosition), 3);
					$('#detailContact').dialog('close');
					
				}
			} else {
				
				// save contact when data empty (new) =======================================================================
				var found = false;
				var rows = table.fnGetNodes().length;	

				for (var i = 0; i < rows; i++) {
					var cell = tableContact.fnGetData(i);
					if ($("#contactForm #contactType").val() == $(cell[0]).val()) {					
						$('#contactForm #contactType').addClass('fieldError');
						$("#duplicateErrorMessageCorp").html("Already Exist");
						found = true;
						break;
					} 
					
				}
				
				if (!found) {
					var dataContact = new Object();
					dataContact.addressType = new Object();
					dataContact.identificationType = new Object();
					dataContact.sourceOfFund = new Object();
					dataContact.occupation = new Object();
					dataContact.natureBusiness = new Object();
					dataContact.address1Country = new Object();
					
					dataContact.addressType.lookupId = $('#detailContact #contactForm #contactType').val();
					dataContact.addressType.lookupDescription = $('#detailContact #contactForm #contactTypeDesc').val();
					dataContact.contactName = $('#detailContact #contactForm #contactNameContactHide').val().toUpperCase();
					
					dataContact.firstName = $('#detailContact #contactForm #firstNameContact').val();
					
					if ($('#detailContact #contactForm #middleNameContact').val() == '') {
						dataContact.middleName = "";
					} else {
						dataContact.middleName = $('#detailContact #contactForm #middleNameContact').val();
					}
					
					if ($('#detailContact #contactForm #lastNameContact').val() == '') {
						dataContact.lastName = "";
					} else {
						dataContact.lastName = $('#detailContact #contactForm #lastNameContact').val();
					}
					
					if ($('#detailContact #contactForm #birthPlaceContact').val() == '') {
						dataContact.birthPlace = "";
					} else {
						dataContact.birthPlace = $('#detailContact #contactForm #birthPlaceContact').val();
					}
					
					if ($('#detailContact #contactForm #birthDateContact').val()=='') {
						dataContact.birthDate = "";
					} else {
						dataContact.birthDate = $('#detailContact #contactForm #birthDateContact').val();
					}
					
					if (($('#detailContact #contactForm #address1CountryCodeContact').val() == '') || ($('#contactForm #address1CountryCodeContact').val() == null)) {
						dataContact.address1Country = new Object();
						dataContact.address1Country.lookupCode = "";
						dataContact.address1Country.lookupId = "";
						dataContact.address1Country.lookupDescription = "";
					} else {
						dataContact.address1Country = new Object();
						dataContact.address1Country.lookupCode = $('#detailContact #contactForm #address1CountryCodeContact').val();
						dataContact.address1Country.lookupId = $('#detailContact #contactForm #address1CountryContact').val();
						dataContact.address1Country.lookupDescription = $('#detailContact #contactForm #address1CountryDescContact').val();
					}
					
					if ($('#detailContact #contactForm #identificationTypeContact').val() == '') {
						dataContact.identificationType.lookupId = "";
					} else {
						dataContact.identificationType.lookupId = $('#detailContact #contactForm #identificationTypeContact').val();
					}
					
					if ($('#detailContact #contactForm #identificationNoContact').val() == '') {
						dataContact.identificationNo = "";
					} else {
						dataContact.identificationNo = $('#detailContact #contactForm #identificationNoContact').val();
					}
					
					if ($('#detailContact #contactForm #sourceOfFundContact').val() == '') {
						dataContact.sourceOfFund.lookupId = "";
					} else {
						dataContact.sourceOfFund.lookupId = $('#detailContact #contactForm #sourceOfFundContact').val();
					}
					
					if ($('#detailContact #contactForm #occupationContact').val() == '') {
						dataContact.occupation.lookupId = "";
					} else {
						dataContact.occupation.lookupId = $('#detailContact #contactForm #occupationContact').val();
					}
					
					if ($('#detailContact #contactForm #positionContact').val() == '') {
						dataContact.position = "";
					} else {
						dataContact.position = $('#detailContact #contactForm #positionContact').val();
					}
					
					if ($('#detailContact #contactForm #relationContact').val() == '') {
						dataContact.relation = "";
					} else {
						dataContact.relation = $('#detailContact #contactForm #relationContact').val();
					}
					
					if ($('#detailContact #contactForm #natureBusinessContact').val() == '') {
						dataContact.natureBusiness.lookupId = "";
					} else {
						dataContact.natureBusiness.lookupId = $('#detailContact #contactForm #natureBusinessContact').val();
					}
					
					table.fnAddData(dataContact);
					
					$('#detailContact').dialog('close');
				}
				// ==========================================================================================================
			}
			$('#grid-contact-corp_wrapper.dataTables_wrapper').css('width','1728px');
			return false;
//		}
		
	});	
	// ======================================================================================================================

	// contact event edit ===================================================================================================
	$('#grid-contact-corp').removeAttr('style');
	$('#grid-contact-corp tbody tr td').die('click');
	$('#grid-contact-corp tbody tr td').live('click', function() {
		var rowPos = $(this).parents('tr');
		if (tableContact.fnGetNodes().length == 0) { // tambah pengecekan ini~~
			return false;
		}
		mandatoryContact();
		var rowPosNumber = tableContact.fnGetPosition(rowPos[0]);
		var pos = tableContact.fnGetPosition(this);
		
		var rows = tableContact.fnGetNodes().length;
		var row = $(this).parents('tr');
		var rowNumber = tableContact.fnGetPosition(row[0]);
		var datas = tableContact.fnGetData(row[0]);
		var found = false;
		
		cell = tableContact.fnGetData(this.parentNode);
		if (pos[1] != 3) { 		// kondisi buat posisi button delete tidak ke-live
			var fmtDate = '${appProp.jqueryDateFormat}';
			dataContact = tableContact.fnGetData(this.parentNode);
			
			$("#detailContact #contactForm").find("input[class*='fieldError']").removeClass('fieldError');
			$('#detailContact #contactForm #identificationTypeContact').removeClass('fieldError');
			$('#detailContact #contactForm .errorMessage').html("");
			$('#detailContact #contactForm #contactType').removeClass('fieldError');

			if (((dataContact.addressType.lookupId) == '') || ((dataContact.addressType)=='')) {
				dataContact.addressType = new Object();
				dataContact.addressType.lookupId = '';
				dataContact.addressType.lookupDescription = '';
				$("#detailContact #contactForm #contactType").val(dataContact.addressType.lookupId);
				$("#detailContact #contactForm #contactTypeDesc").val(dataContact.addressType.lookupDescription);
			} else {
				$("#detailContact #contactForm #contactType").val(dataContact.addressType.lookupId);
				$("#detailContact #contactForm #contactTypeDesc").val(dataContact.addressType.lookupDescription);
			}

			getContactTypeLookupDescription();
			$("#detailContact #contactForm #rowPosition").val(rowPosNumber);
			$("#detailContact #contactForm #contactNameContact").val(dataContact.contactName);
			$("#detailContact #contactForm #contactNameContactHide").val(dataContact.contactName);
			$("#detailContact #contactForm #firstNameContact").val(dataContact.firstName);
			$("#detailContact #contactForm #middleNameContact").val(dataContact.middleName);
			$("#detailContact #contactForm #lastNameContact").val(dataContact.lastName);
			$("#detailContact #contactForm #positionContact").val(dataContact.position);
			$("#detailContact #contactForm #birthPlaceContact").val(dataContact.birthPlace);
			if (dataContact.birthDate!=''){
				if (dataContact.birthDate!=null) {
					var stringDate = dataContact.birthDate.toString();
					if (stringDate.substr(2,1) != "/") {
						if ($.browser.msie) {
							if (stringDate.substr(4,1) != "-") {
								$("#detailContact #contactForm #birthDateContact").val($.datepicker.formatDate(fmtDate, new Date(dataContact.birthDate)));
							} else {
								$("#detailContact #contactForm #birthDateContact").val($.datepicker.formatDate(fmtDate, new Date(dataContact.birthDate.toString().replace(/-/g,"/"))));
							}
						} else {
							$("#detailContact #contactForm #birthDateContact").val($.datepicker.formatDate(fmtDate, new Date(dataContact.birthDate)));
						}
					} else {
						$("#detailContact #contactForm #birthDateContact").val(dataContact.birthDate);
					}
				} else {
					$("#detailContact #contactForm #birthDateContact").val('');
				}
			}else {
				$("#detailContact #contactForm #birthDateContact").val('');
			}
			$('#detailContact #contactForm #oldContactType').val($("#detailContact #contactForm #contactType").val());
			$('#detailContact #contactForm #newContactType').val($("#detailContact #contactForm #oldContactType").val());
		
			if (((dataContact.address1Country) == null) || ((dataContact.address1Country) == '') || ((dataContact.address1Country.lookupId) == '')) { 
				dataContact.address1Country = new Object();
				dataContact.address1Country.lookupCode = '';
				dataContact.address1Country.lookupId = '';
				dataContact.address1Country.lookupDescription = '';
				$("#detailContact #contactForm #address1CountryCodeContact").val(dataContact.address1Country.lookupCode);
				$("#detailContact #contactForm #address1CountryContact").val(dataContact.address1Country.lookupId);
				$("#detailContact #contactForm #address1CountryDescContact").val(dataContact.address1Country.lookupDescription);
			} else {
				$("#detailContact #contactForm #address1CountryCodeContact").val(dataContact.address1Country.lookupCode);
				$("#detailContact #contactForm #address1CountryContact").val(dataContact.address1Country.lookupId);
				$("#detailContact #contactForm #address1CountryDescContact").val(dataContact.address1Country.lookupDescription);
			}
			
			if (((dataContact.identificationType) == null) || ((dataContact.identificationType) == '') || ((dataContact.identificationType.lookupId) == '')) {
				dataContact.identificationType = new Object();
				dataContact.identificationType.lookupId = '';
				$("#detailContact #contactForm #identificationTypeContact").val(dataContact.identificationType.lookupId);
			} else {
				$("#detailContact #contactForm #identificationTypeContact").val(dataContact.identificationType.lookupId);
			}
			
			if (((dataContact.sourceOfFund) == null) || ((dataContact.sourceOfFund) == '') || ((dataContact.sourceOfFund.lookupId) == '')) {
				dataContact.sourceOfFund = new Object();
				dataContact.sourceOfFund.lookupId = '';
				$("#detailContact #contactForm #sourceOfFundContact").val(dataContact.sourceOfFund.lookupId);
			} else {
				$("#detailContact #contactForm #sourceOfFundContact").val(dataContact.sourceOfFund.lookupId);
			}
			
			if (((dataContact.occupation) == null) || ((dataContact.occupation) == '') || ((dataContact.occupation.lookupId) == '')) {
				dataContact.occupation = new Object();
				dataContact.occupation.lookupId = '';
				$("#detailContact #contactForm #occupationContact").val(dataContact.occupation.lookupId);
			} else {
				$("#detailContact #contactForm #occupationContact").val(dataContact.occupation.lookupId);
			}
			
			if (((dataContact.natureBusiness) == null) || ((dataContact.natureBusiness) == '') || ((dataContact.natureBusiness.lookupId) == '')) {
				dataContact.natureBusiness = new Object();
				dataContact.natureBusiness.lookupId = '';
				$("#detailContact #contactForm #natureBusinessContact").val(dataContact.natureBusiness.lookupId);
			} else {
				$("#detailContact #contactForm #natureBusinessContact").val(dataContact.natureBusiness.lookupId);
			}

			$("#detailContact #contactForm #identificationNoContact").val(dataContact.identificationNo);
			$("#detailContact #contactForm #positionContact").val(dataContact.position);
			$("#detailContact #contactForm #relationContact").val(dataContact.relation);
			
			if ($('#detailContact #contactForm #middleNameContact').val()==''){
				$('p[id=pLastNameCorp] label span').html('');
			} else {
				$('p[id=pLastNameCorp] label span').html(' *');
			}
			// ==============================================================================================================
				
			$('#detailContact').dialog('open');
			$('.ui-widget-overlay').css('height',$('body').height());
		};
	});
	
	// contact event delete ==================================================================================================
	$('#grid-contact-corp tbody tr #deleteButton').live('click', function() {
		if ('${mode}' != 'view') {
			var table = $('#grid-contact-corp').dataTable();
			var rows = table.fnGetNodes().length;
			var row = $(this).parents('tr');
			var rowNumber = tableContact.fnGetPosition(row[0]);
			var datas = table.fnGetData(row[0]);
			var found = false;
			var deleteContactCorp = function() {
				tableContact.fnDeleteRow(rowNumber);
				reorderingContact();
				$("#dialog-message").dialog("close");
			};
			
			if (!found) {
				//deleteContact(rowNumber);
				messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteContactCorp, closeDialog);
			}
		}
		
	});
	
	$('.buttons #newContact').click(function() {
		mandatoryContact();
		newContact();	
		return false;
	});
	
	$('#cancelConCorp').click(function(){
		$('#detailContact').dialog('close');
		return false;
	});
	
	$("#contactType").change(function() {
		$('#contactType').removeClass('fieldError');
		$('#duplicateErrorMessageCorp').html("");
		getContactTypeLookupDescription();
		
	});
	
	$('#firstNameContact').change(function(){
		fillContactName();
	});
	
	$('#middleNameContact').change(function(){
		fillContactName();
		if ($(this).val()!=''){
			$('p[id=pLastNameCorp] label span').html(' *');
		} else {
			$('p[id=pLastNameCorp] label span').html('');
		}
	});
	
	$('#lastNameContact').change(function(){
		fillContactName();
	});
	
	$('#closeConCorp').click(function(){
		$('#detailContact').dialog('close');
		return false;
	});

	/*$('#occupationContact').change(function() {
        var checkBO = chekIsBenefiaryContact();
        if (checkBO) {
			$("p[atr=natureBusinessMandatory] label span").html(" *");
        } else {
			$("p[atr=natureBusinessMandatory] label span").html(" ");
        }
	});*/
	
});

function getContactTypeLookupDescription(){
	var selected = $("#detailContact #contactForm #contactType").val();
	var selectedText = $("#detailContact #contactForm #contactType option[value=" + selected + "]").text();
	if (selectedText != '') {
			
			selectedText = jQuery.trim(selectedText);
			selectedText = selectedText.substring(jQuery.trim(selectedText).indexOf('-') + 1);
	}
	$("#detailContact #contactForm #contactTypeDesc").val(selectedText);
	// END OF SUBSTRING
	addressTypeDescription = selectedText;
	$("#detailContact #contactForm #contactTypeDesc").val(addressTypeDescription);
	$('#newContactType').val($("#detailContact #contactForm #contactType").val());

	var contactDesc = $('#contactTypeDesc').val();
	if (contactDesc.includes('UBO')) {
		$("p[atr=sourceOfFundMandatory] label span").html(" *");
	} else {
		$('p[atr=sourceOfFundMandatory] label span').html('');
	}
}

//function new contact when data already exist =============================================================================
function newContact() {
	$('#detailContact').dialog('open');
	$("#detailContact #contactForm").find("input[class*='fieldError']").removeClass('fieldError');	
	$("#detailContact #contactForm input:text").val(""); 
	$("#detailContact #contactForm input:hidden").val("");
	$("#detailContact #contactForm #contactType").val("");
	$("#detailContact #contactForm #contactTypeDesc").val("");
	$("#detailContact #contactForm #firstNameContact").val("");
	$("#detailContact #contactForm #middleNameContact").val("");
	$("#detailContact #contactForm #lastNameContact").val("");
	$("#detailContact #contactForm #contactNameContact").val("");
	$("#detailContact #contactForm #identificationTypeContact").val("");
	$("#detailContact #contactForm #birthPlaceContact").val("");
	$("#detailContact #contactForm #birthDateContact").val("");
	$("#detailContact #contactForm #address1CountryCodeContact").val("");
	$("#detailContact #contactForm #sourceOfFundContact").val("");
	$("#detailContact #contactForm #occupationContact").val("");
	$("#detailContact #contactForm #natureBusinessContact").val("");
	$("#detailContact #contactForm #positionContact").val("");
	$("#detailContact #contactForm #relationContact").val("");
	$('.errorMessage').html('');
	$('#identificationTypeContact').enabled();
	
	$('.ui-widget-overlay').css('height',$('body').height());
	return false;
}

function reorderingContact() {
	var tableContact = $('#grid-contact-corp');
	var trs = $("tr", tableContact);
	$.each(trs, function(idx, data){
		var hiddens = $("[type=hidden]", $(this));
		$(hiddens).eq(0).attr("name", "contacts["+(idx-1)+"].addressType.lookupId");
		$(hiddens).eq(1).attr("name", "contacts["+(idx-1)+"].addressType.lookupDescription");
		$(hiddens).eq(2).attr("name", "contacts["+(idx-1)+"].identificationType.lookupId");
		$(hiddens).eq(3).attr("name", "contacts["+(idx-1)+"].sourceOfFund.lookupId");
		$(hiddens).eq(4).attr("name", "contacts["+(idx-1)+"].occupation.lookupId");
		$(hiddens).eq(5).attr("name", "contacts["+(idx-1)+"].natureBusiness.lookupId");
		$(hiddens).eq(6).attr("name", "contacts["+(idx-1)+"].address1Country.lookupId");
		$(hiddens).eq(7).attr("name", "contacts["+(idx-1)+"].address1Country.lookupCode");
		$(hiddens).eq(8).attr("name", "contacts["+(idx-1)+"].address1Country.lookupDescription");
		$(hiddens).eq(9).attr("name", "contacts["+(idx-1)+"].contactName");
		$(hiddens).eq(10).attr("name", "contacts["+(idx-1)+"].firstName");
		$(hiddens).eq(11).attr("name", "contacts["+(idx-1)+"].middleName");
		$(hiddens).eq(12).attr("name", "contacts["+(idx-1)+"].lastName");
		$(hiddens).eq(13).attr("name", "contacts["+(idx-1)+"].birthPlace");
		$(hiddens).eq(14).attr("name", "contacts["+(idx-1)+"].birthDate");
		$(hiddens).eq(15).attr("name", "contacts["+(idx-1)+"].identificationNo");
		$(hiddens).eq(16).attr("name", "contacts["+(idx-1)+"].position");
		$(hiddens).eq(17).attr("name", "contacts["+(idx-1)+"].relation");
	});
}

function tabContact(data) {
	var fmtDate = '${appProp.jqueryDateFormat}';
	
	tableContact = $('#grid-contact-corp').dataTable({
		aaData: data,
		aoColumns: [  	
						{
							fnRender: function(obj){
								var controls;
								if (obj.aData.addressType != null) {
									controls = obj.aData.addressType.lookupDescription;
									controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressType.lookupId" value="' + obj.aData.addressType.lookupId + '" />';
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
								if (obj.aData.contactName != null) {
									controls = obj.aData.contactName;
									controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].contactName" value="' + obj.aData.contactName + '" />';
								} else {
									controls = "";
									controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].contactName" value="" />';
								}
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								if (obj.aData.position != null) {
									controls = obj.aData.position;
									controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].position" value="' + obj.aData.position + '" />';
							 	} else {
							 		controls = '';
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].position" value="" />';
							 	}
								return controls;
							}
						},
						
						{
							fnRender: function(obj) {
							 	var controls;
							 	controls = '';
							 	
							 	var param = '${param}';
							 	if (!param || (param && '${param}' == 'entry')) {
							 	 controls += '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
							 	} else {
							 		controls += '<center><input type="button" value="Delete" disabled="disabled" /></center>';
							 	}
								
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].firstName" value="' + obj.aData.firstName + '" />';
							 	if ((obj.aData.middleName == '')||(obj.aData.middleName == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].middleName" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].middleName" value="' + obj.aData.middleName + '" />';
							 	}
							 	
							 	if ((obj.aData.lastName == '')||(obj.aData.lastName == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].lastName" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].lastName" value="' + obj.aData.lastName + '" />';
							 	}
							 	
							 	if ((obj.aData.identificationType == '')||(obj.aData.identificationType == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identificationType.lookupId" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identificationType.lookupId" value="' + obj.aData.identificationType.lookupId + '" />';
							 	}
							 	
							 	if ((obj.aData.identificationNo == '')||(obj.aData.identificationNo == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identificationNo" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identificationNo" value="' + obj.aData.identificationNo + '" />';
							 	}
							 	
							 	if ((obj.aData.birthPlace == '')||(obj.aData.birthPlace == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].birthPlace" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].birthPlace" value="' + obj.aData.birthPlace + '" />';
							 	}
							 	
							 	if ((obj.aData.birthDate == '')||(obj.aData.birthDate == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].birthDate" value="" />';
							 	} else {
							 		var stringDate = obj.aData.birthDate.toString();
							 		var birthDate;
							 		if (stringDate.substr(2,1) != "/") {
							 			birthDate = $.datepicker.formatDate(fmtDate, new Date(obj.aData.birthDate));
							 		} else {
							 			birthDate =  obj.aData.birthDate
							 		}
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].birthDate" value="' + birthDate + '" />';
							 	}
							 	
							 	if ((obj.aData.address1Country == '')||(obj.aData.address1Country == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupId" value="" />';
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupCode" value="" />';
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupDescription" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupId" value="' + obj.aData.address1Country.lookupId + '" />';
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupCode" value="' + obj.aData.address1Country.lookupCode + '" />';
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].address1Country.lookupDescription" value="' + obj.aData.address1Country.lookupDescription + '" />';
							 	}
							 	
							 	if ((obj.aData.sourceOfFund == '')||(obj.aData.sourceOfFund == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].sourceOfFund.lookupId" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].sourceOfFund.lookupId" value="' + obj.aData.sourceOfFund.lookupId + '" />';
							 	}
							 	
							 	if ((obj.aData.occupation == '')||(obj.aData.occupation == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].occupation.lookupId" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].occupation.lookupId" value="' + obj.aData.occupation.lookupId + '" />';
							 	}
							 	
							 	if ((obj.aData.natureBusiness == '')||(obj.aData.natureBusiness == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].natureBusiness.lookupId" value="" />';
							 	} else {
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].natureBusiness.lookupId" value="' + obj.aData.natureBusiness.lookupId + '" />';
							 	}
							 	
							 	if ((obj.aData.relation == '')||(obj.aData.relation == null)){
							 		controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].relation" value="" />';
							 	} else {
									controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].relation" value="' + obj.aData.relation + '" />';
							 	}
							 	
							 	
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
}

function fillContactName(){
	var firstName = $('#firstNameContact').val();
	var middleName = $('#middleNameContact').val();
	var lastName = $('#lastNameContact').val();
	
	if ((firstName != '')||(middleName != '')||(lastName != '')){
		if (firstName !='') {
			$('#contactNameContact').val(firstName);
			
		}
		if ((firstName != '')&&(middleName!='')){
			$('#contactNameContact').val(firstName + " " + middleName);	
		}
		
		if ((firstName != '')&&(lastName!='')){
			$('#contactNameContact').val(firstName + " " + lastName);	
		}
		
		if ((firstName != '')&&(middleName!='')&&(lastName!='')){
			$('#contactNameContact').val(firstName + " " + middleName + " " + lastName);
		}
		
		$('#contactNameContactHide').val($('#contactNameContact').val());
	}
}

function chekIsBenefiaryContact(lookupId) {
	var select_customer; 
    select_customer = $('#customerType').val();
	var lookupId = $('#occupationContact').val();
    
    var isBo = false; 

	$().fetchSync("@{AmlMaintenance.chekIsBenefiary()}", 
			{
				'lookupId' : lookupId
			}, function(datas) {
				var isBeneficiaryOwner = datas.isBeneficiaryOwner;
				if (isBeneficiaryOwner) {
					isBo = true;
				} else {
					isBo = false;
				}
		});
	
	return isBo;
}

function mandatoryContact() {
	var occupation = $('#occupation').val();
	var isBO = $('#isBO').val();
	if ( (isBO == 'true') && $('#customerType').val()=='${typeIndi}') {
		$("p[atr=sourceOfFundMandatory] label span").html(" ");
		$("p[atr=occupationMandatory] label span").html(" *");
		$("p[atr=natureBusinessMandatory] label span").html(" *");
	} /*else if ( (isBO == 'true') && $('#customerType').val()=='${typeIndi}') {
		$("p[atr=occupationMandatory] label span").html(" *");
		$("p[atr=natureBusinessMandatory] label span").html(" ");
	}*/ else {
		if ($('#customerType').val()=='${typeCorp}') {
			$("p[atr=sourceOfFundMandatory] label span").html(" ");
			$("p[atr=natureBusinessMandatory] label span").html(" ");
		} else {
			$("p[atr=sourceOfFundMandatory] label span").html(" *");
		}
		$("p[atr=occupationMandatory] label span").html(" ");
		/*$("p[atr=natureBusinessMandatory] label span").html(" ");*/

	}
}
