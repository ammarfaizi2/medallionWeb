$(function(){

	var data = new Object();
	tabContact(data);
	
	$("#contactForm #contacTypeCorp").change(function() {
		$('#contactForm #contacTypeCorp').removeClass('fieldError');
		$('#duplicateErrorMessageCorp').html("");
		
		getContactTypeLookupDescription();
		
	});
	
	$('#saveCon').die("click");
	$('#saveCon').live("click", function(){
		
		var oldContactType = $("#detailContactCorp #contactForm #oldContactType").val();
		var newContactType = $("#detailContactCorp #contactForm #newContactType").val();
		var table = $('#contact #grid-contact').dataTable();
		var rowPosition = $('#detailContactCorp #contactForm #rowPosition').val();
		var found = false;
		
		if ($('#detailContactCorp #contactForm #contacTypeCorp').val()=='' ||
		    $('#detailContactCorp #contactForm #contactPosition').val()=='' ||
		    $('#detailContactCorp #contactForm #contactName').val()=='' ){
			
			if ($('#contacTypeCorp').val()==''){
				$('#errmsgAddressTypeCorp').html('Required');
			} else { 
				$('#errmsgAddressTypeCorp').html('');
			}
			
			if ($('#contactName').val()==''){
				$('#errmsgContactName').html('Required');
			} else {
				$('#errmsgContactName').html('');
			}
			
			if ($('#contactPosition').val()==''){
				$('#errmsgContactPos').html('Required');
			} else {
				$('#errmsgContactPos').html('');
			}
			
			return false;
		}
		
		if ($('#detailContactCorp #contactForm input').hasClass('fieldError')){
			return false;
		}
		
		var checkClass = $("#detailContactCorp #contactForm .dropdown").hasClass('fieldError');
		
		if (checkClass) {
			return false;
		}
		
		$('#detailContactCorp #contactForm .errorMessage').html('');
		var dataContact = table.fnGetData(parseFloat(rowPosition));
			if (rowPosition != "") {
				var rows = table.fnGetNodes().length;
				for (var i = 0; i < rows; i++) {
					var cell = tableContactCorp.fnGetData(i);
					if (($("#contactForm #contacTypeCorp").val() == cell.addressType.lookupId) && (oldContactType != newContactType)) {					
						$('#contactForm #contacTypeCorp').addClass('fieldError');
						$("#duplicateErrorMessageCorp").html("Already Exist");
						found = true;
						break;
					} 
					
				}
				
				if (!found) {
					
						table.fnUpdate(										
										(dataContact.addressType.lookupId = $("#detailContactCorp #contactForm #contacTypeCorp").val()) &&
										(dataContact.addressType.lookupDescription = $("#detailContactCorp #contactForm #contactTypeDescCorp").val()),
										parseFloat(rowPosition), 0
								  );
					
					if ($('#detailContact #contactForm #contactName').val() == '') {
						table.fnUpdate(
										dataContact.contactName = "",
										parseFloat(rowPosition), 1
									 );
					} else {
						table.fnUpdate(
										dataContact.contactName = $('#detailContactCorp #contactForm #contactName').val().toUpperCase(),
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
					
					
					dataContact.contactKey = $('#detailContactCorp #contactForm #contactKey').val();
					
					table.fnUpdate(dataContact,parseFloat(rowPosition), 3);
					
					$('#detailContactCorp').dialog('close');
					
				}
			} else {
				
				// save contact when data empty (new) =======================================================================
				var found = false;
				var rows = table.fnGetNodes().length;
				
				for (var i = 0; i < rows; i++) {
					var cell = tableContactCorp.fnGetData(i);
					
					if ($("#contactForm #contacTypeCorp").val() == cell.addressType.lookupId) {					
						$('#contactForm #contacTypeCorp').addClass('fieldError');
						$("#duplicateErrorMessageCorp").html("Already Exist");
						found = true;
						break;
					} 
					
				}
				
				if (!found) {
					var dataContact = new Object();
					dataContact.addressType = new Object();
					dataContact.identificationType1 = new Object();
					dataContact.identificationType2 = new Object();
					dataContact.identificationType3 = new Object();
					dataContact.identificationType4 = new Object();
					
					dataContact.addressType.lookupId = $('#detailContactCorp #contactForm #contacTypeCorp').val();
					dataContact.addressType.lookupDescription = $('#detailContactCorp #contactForm #contactTypeDescCorp').val();
					dataContact.contactName = $('#detailContactCorp #contactForm #contactName').val().toUpperCase();
					
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
			$('#grid-contact_wrapper.dataTables_wrapper').css('width','1728px');
			return false;
	});
	
	$('#grid-contact tbody tr #deleteContact').live('click', function() {
		var table = $('#contact #grid-contact').dataTable();
		var row = $(this).parents('tr');
		var rowNumber = tableContactCorp.fnGetPosition(row[0]);
		
		var deleteContactCorp = function() {
			tableContactCorp.fnDeleteRow(rowNumber);
			reorderingContact();
			$("#dialog-message").dialog("close");
		}
		
		messageAlertYesNo("Are you sure to delete Contact Person ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteContactCorp, closeDialog);
		
	});
	
});

//function grid table contact
function tabContact(data) {
	var detailContacts = null;
	detailContacts = ${detailContacts};
	/*var detailContacts;
	
	if ('${detailContacts}' != null )
		detailContacts = ${detailContacts.raw()};

	if ('${detailContacts}' == null )
		detailContacts = new Array();*/
		
	var fmtDate = '${appProp.jqueryDateFormat}';
	// initialize table contact =============================================================================================
	tableContactCorp = 
		$('#contact #grid-contact').dataTable({
			aaData: detailContacts,
			aoColumns: [
						{
							fnRender: function(obj){
								var controls;
								controls = obj.aData.addressType.lookupDescription;
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								controls = obj.aData.contactName;
								return controls;
							}
						},
						{
							fnRender: function(obj){
								var controls;
								controls = obj.aData.contactPosition;
								return controls;
							}
						},
						{
							fnRender: function(obj){
								
								var identification1Expiry = "";
								if(obj.aData.identification1Expiry == null || obj.aData.identification1Expiry == ''){
									identification1Expiry = "";
								} else {
									var stringDate1 = obj.aData.identification1Expiry.toString();
									if (stringDate1.substr(2,1) != "/") {
										identification1Expiry = $.datepicker.formatDate(fmtDate, new Date(obj.aData.identification1Expiry));
							 		} else {
							 			identification1Expiry = obj.aData.identification1Expiry;
							 		}
								}
								
								var identification2Expiry = "";
								if(obj.aData.identification2Expiry == null || obj.aData.identification2Expiry == ''){
									identification2Expiry="";
								} else {
									var stringDate2 = obj.aData.identification2Expiry.toString();
							 		if (stringDate2.substr(2,1) != "/") {
							 			identification2Expiry = $.datepicker.formatDate(fmtDate, new Date(obj.aData.identification2Expiry));
							 		} else {
							 			identification2Expiry = obj.aData.identification2Expiry;
							 		}
								}
						 		
								var identification3Expiry = "";
								if(obj.aData.identification3Expiry == null || obj.aData.identification3Expiry == ''){
									identification3Expiry = "";
								} else {
									var stringDate3 = obj.aData.identification3Expiry.toString();
							 		if (stringDate3.substr(2,1) != "/") {
							 			identification3Expiry = $.datepicker.formatDate(fmtDate, new Date(obj.aData.identification3Expiry));
							 		} else {
							 			identification3Expiry = obj.aData.identification3Expiry;
							 		}
								}
						 		
								var identification4Expiry = "";
								if(obj.aData.identification4Expiry == null || obj.aData.identification4Expiry == ''){
									identification4Expiry = "";
								} else {
									var stringDate4 = obj.aData.identification4Expiry.toString();
							 		if (stringDate4.substr(2,1) != "/") {
							 			identification4Expiry = $.datepicker.formatDate(fmtDate, new Date(obj.aData.identification4Expiry));
							 		} else {
							 			identification4Expiry = obj.aData.identification4Expiry;
							 		}
								}
						 		
								if(obj.aData.npwp==null){
									obj.aData.npwp="";
								}
								
								if(obj.aData.npwpDate==null){
									obj.aData.npwpDate="";
								}
								
								if(obj.aData.identificationType1.lookupId==null){
									obj.aData.identificationType1.lookupId="";
								}
								
								if(obj.aData.identification1No==null){
									obj.aData.identification1No="";
								}
								
								if(obj.aData.identificationType2.lookupId==null){
									obj.aData.identificationType2.lookupId="";
								}
								
								if(obj.aData.identification2No==null){
									obj.aData.identification2No="";
								}
								
								if(obj.aData.identificationType3.lookupId==null){
									obj.aData.identificationType3.lookupId="";
								}
								
								if(obj.aData.identification3No==null){
									obj.aData.identification3No="";
								}
								
								if(obj.aData.identificationType4.lookupId==null){
									obj.aData.identificationType4.lookupId="";
								}
								
								if(obj.aData.identification4No==null){
									obj.aData.identification4No="";
								}
								
								var controls;
								controls = '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressType.lookupId" value="' + obj.aData.addressType.lookupId + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].addressType.lookupDescription" value="' + obj.aData.addressType.lookupDescription + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].contactName" value="' + obj.aData.contactName + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].contactPosition" value="' + obj.aData.contactPosition + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].npwp" value="' + obj.aData.npwp + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].npwpDate" value="' + obj.aData.npwpDate + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identificationType1.lookupId" value="' + obj.aData.identificationType1.lookupId + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identification1No" value="' + obj.aData.identification1No + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identification1Expiry" value="' + identification1Expiry + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identificationType2.lookupId" value="' + obj.aData.identificationType2.lookupId + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identification2No" value="' + obj.aData.identification2No + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identification2Expiry" value="' + identification2Expiry + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identificationType3.lookupId" value="' + obj.aData.identificationType3.lookupId + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identification3No" value="' + obj.aData.identification3No + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identification3Expiry" value="' + identification3Expiry + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identificationType4.lookupId" value="' + obj.aData.identificationType4.lookupId + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identification4No" value="' + obj.aData.identification4No + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].identification4Expiry" value="' + identification4Expiry + '" />';
								controls += '<input type="hidden" name="contacts[' + obj.iDataRow + '].contactKey" value="' + obj.aData.contactKey + '" />';
								controls += '<center><input id="deleteContact" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
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

$('#contact #grid-contact').removeAttr('style');
$('#contact #grid-contact tbody tr td').die('click');
$('#contact #grid-contact tbody tr td').live('click', function() {
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
	
	var fmtDate = '${appProp.jqueryDateFormat}';
	
	cell = tableContactCorp.fnGetData(this.parentNode);
	if (pos[1] != 3) { 		// kondisi buat posisi button delete tidak ke-live
		dataContact = tableContactCorp.fnGetData(this.parentNode);
		
		$("#detailContactCorp #contactForm").find("input[class*='fieldError']").removeClass('fieldError');
		$('#detailContactCorp #contactForm #identificationType1').removeClass('fieldError');
		$('#detailContactCorp #contactForm #identificationType2').removeClass('fieldError');
		$('#detailContactCorp #contactForm #identificationType3').removeClass('fieldError');
		$('#detailContactCorp #contactForm .errorMessage').html("");
		$('#detailContactCorp #contactForm #contacTypeCorp').removeClass('fieldError');
		
		getContactTypeLookupDescription();
		
		if (((dataContact.addressType.lookupId) == '') || ((dataContact.addressType)=='')) {
			dataContact.addressType = new Object();
			dataContact.addressType.lookupId = '';
			dataContact.addressType.lookupDescription = '';
			$("#detailContactCorp #contactForm #contacTypeCorp").val(dataContact.addressType.lookupId);
			$("#detailContactCorp #contactForm #contactTypeDescCorp").val(dataContact.addressType.lookupDescription);
		} else {
			$("#detailContactCorp #contactForm #contacTypeCorp").val(dataContact.addressType.lookupId);
			$("#detailContactCorp #contactForm #contactTypeDescCorp").val(dataContact.addressType.lookupDescription);
		}
		$("#detailContactCorp #contactForm #rowPosition").val(rowPosNumber);
		$("#detailContactCorp #contactForm #contactName").val(dataContact.contactName);
		$("#detailContactCorp #contactForm #contactPosition").val(dataContact.contactPosition);
		$("#detailContactCorp #contactForm #contactNpwpNo").val(dataContact.npwp);
		if (dataContact.npwpDate!=''){
			if (dataContact.npwpDate!=null) {
				var stringDate = dataContact.npwpDate.toString();
				if (stringDate.substr(2,1) != "/") {
					$("#detailContactCorp #contactForm #contactNpwpRegDate").val($.datepicker.formatDate(fmtDate, new Date(dataContact.npwpDate)));
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
		// ==============================================================================================================
			
		$('#detailContactCorp').dialog('open');
		$('.ui-widget-overlay').css('height',$('body').height());
	};
});

function getContactTypeLookupDescription(){
	var selected = $('#contactForm #contacTypeCorp').val();
	var selectedText = $("#contactForm #contacTypeCorp option[value=" + selected + "]").text();
	if (selectedText != '') {
			
			selectedText = jQuery.trim(selectedText);
			selectedText = selectedText.substring(jQuery.trim(selectedText).indexOf('-') + 1);
	}
	$("#contactForm #contactTypeDescCorp").val(selectedText);
	$('#newContactType').val($("#contacTypeCorp").val());
}

function reorderingContact() {
	var tableContactCorp = $('#contact #grid-contact');
	var trs = $("tr", tableContactCorp);
	$.each(trs, function(idx, data){
		var hiddens = $("[type=hidden]", $(this));
		$(hiddens).eq(0).attr("name", "contacts["+(idx-1)+"].addressType.lookupId");
		$(hiddens).eq(1).attr("name", "contacts["+(idx-1)+"].addressType.lookupDescription");
		$(hiddens).eq(2).attr("name", "contacts["+(idx-1)+"].contactName");
		$(hiddens).eq(3).attr("name", "contacts["+(idx-1)+"].contactPosition");
		$(hiddens).eq(4).attr("name", "contacts["+(idx-1)+"].npwp");
		$(hiddens).eq(5).attr("name", "contacts["+(idx-1)+"].npwpDate");
		$(hiddens).eq(6).attr("name", "contacts["+(idx-1)+"].identificationType1.lookupId");
		$(hiddens).eq(7).attr("name", "contacts["+(idx-1)+"].identification1No");
		$(hiddens).eq(8).attr("name", "contacts["+(idx-1)+"].identification1Expiry");
		$(hiddens).eq(9).attr("name", "contacts["+(idx-1)+"].identificationType2.lookupId");
		$(hiddens).eq(10).attr("name", "contacts["+(idx-1)+"].identification2No");
		$(hiddens).eq(11).attr("name", "contacts["+(idx-1)+"].identification2Expiry");
		$(hiddens).eq(12).attr("name", "contacts["+(idx-1)+"].identificationType3.lookupId");
		$(hiddens).eq(13).attr("name", "contacts["+(idx-1)+"].identification3No");
		$(hiddens).eq(14).attr("name", "contacts["+(idx-1)+"].identification3Expiry");
		$(hiddens).eq(15).attr("name", "contacts["+(idx-1)+"].identificationType4.lookupId");
		$(hiddens).eq(16).attr("name", "contacts["+(idx-1)+"].identification4No");
		$(hiddens).eq(17).attr("name", "contacts["+(idx-1)+"].identification4Expiry");
		$(hiddens).eq(18).attr("name", "contacts["+(idx-1)+"].contactKey");
	});
}
