function DownloadUnitTrust(html) {
	if (this instanceof DownloadUnitTrust) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);

/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.tradeDateFrom.datepicker();
	    app.tradeDateTo.datepicker();
	    
	    $("#activeDiv").css("display","none");

	    $('#idPost').css("display","none");
	    $('#idEmail').css("display","none");
	    
	    $("#fundClassAll").attr("checked", true);
		$("#fundClassCode").attr('disabled', true);
		app.fundClassCode.val("");
		app.fundClassCodeKey.val("");
		app.fundClassCodeDesc.val("");
		$("#fundClassCodeHelp").attr('disabled', true);
	    
	    $("#fundAll").attr("checked", true);
		$("#fundCode").attr('disabled', true);
		app.fundCode.val("");
		app.fundCodeKey.val("");
		app.fundCodeDesc.val("");
		$("#fundCodeHelp").attr('disabled', true);

		$("#saCodeAll").attr("checked", true);
		$("#saCode").attr('disabled', true);
		app.saCode.val("");
		app.saCodeKey.val("");
		app.saCodeDesc.val("");
		$("#saCodeHelp").attr('disabled', true);

		$("#accountNoAll").attr("checked", true);
		$("#accountCode").attr('disabled', true);
		app.accountCode.val("");
		app.accountCodeKey.val("");
		app.accountCodeDesc.val("");
		$("#accountCodeHelp").attr('disabled', true);

		$("input[name='active']")[1].checked = false;

		var appDate = new Date(html.getApplicationDate());
		app.tradeDateFrom.value($.datepicker.formatDate( '${appProp?.jqueryDateFormat}', appDate ));
		app.tradeDateTo.value($.datepicker.formatDate( '${appProp?.jqueryDateFormat}', appDate ));
	    
/* =========================================================================== 
 * Event
 * ========================================================================= */

	    var closeDialogMessage = function() {
			$("dialog-message").dialog('close');
		};

	    $('#btnReset').click(function() {
			location.href='@{list()}';
		});

	    $("#fundClassAll").click(function() {
	    	
	    	$("input[name=fundClassOption]")[0].checked == true;
			$("input[name=fundClassOption]")[1].checked == false;
			$("#fundClassCode").attr('disabled', true);
			app.fundClassCode.val("");
			app.fundClassCodeKey.val("");
			app.fundClassCodeDesc.val("");
			$("#fundClassCodeHelp").attr('disabled', true);
			$("#fundClassArea label:first .req").remove();
			$("#errFundClass").html("");
			$('#fundClassCode').removeClass('fieldError');
	    	app.fundAll.click();
	    	changePopupFund();
	    	
	    });
	    
	    $("#fundClassBlank").click(function() {
	    	
	    	$("input[name=fundClassOption]")[0].checked == false;
			$("input[name=fundClassOption]")[1].checked == true;
			$("#fundClassCode").attr('disabled', false);
			app.fundClassCode.val("");
			app.fundClassCodeKey.val("");
			app.fundClassCodeDesc.val("");
			$("#fundClassCodeHelp").attr('disabled', false);
			app.fundAll.click();
			changePopupFund();
			
	    });

	    $("#fundAll").click(function() {
	    	changePickTransCode();
			$("input[name=fundOption]")[0].checked == true;
			$("input[name=fundOption]")[1].checked == false;
			$("#fundCode").attr('disabled', true);
			app.fundCode.val("");
			app.fundCodeKey.val("");
			app.fundCodeDesc.val("");
			$("#fundCodeHelp").attr('disabled', true);
			$("#fundCodeArea label:first .req").remove();
			$("#errFund").html("");
			$('#fundCode').removeClass('fieldError');

			//investorAccount('', $("#saCodeKey").val());

			$("#accountNoAll").attr("checked", true);
			$("#accountCode").attr('disabled', true);
			app.accountCode.val("");
			app.accountCodeKey.val("");
			app.accountCodeDesc.val("");
			$('#accountCode').removeClass('fieldError');
			$("#accountCodeHelp").attr('disabled', true);
		});

	    $("#fundBlank").click(function() {
	    	changePickTransCode();
	    	$("input[name=fundOption]")[0].checked == false;
			$("input[name=fundOption]")[1].checked == true;
			$("#fundCode").attr('disabled', false);
			app.fundCode.val("");
			app.fundCodeKey.val("");
			app.fundCodeDesc.val("");
			$("#fundCodeHelp").attr('disabled', false);
			/*if($("#fundCodeArea label .req").text() == "")
			{
				$("#fundCodeArea label:first").append($("<span class='req'>*</span>"));
			}*/

			//investorAccount($("#fundCode").val(), $("#saCodeKey").val());
			$("#accountNoAll").attr("checked", true);
			$("#accountCode").attr('disabled', true);
			app.accountCode.val("");
			app.accountCodeKey.val("");
			app.accountCodeDesc.val("");
			$("#accountCodeHelp").attr('disabled', true);
		});

	    $("#saCodeAll").click(function(){
	    	changePickTransCode();
			$("input[name=saCodeOption]")[0].checked == true;
			$("#saCode").attr('disabled', true);
			app.saCode.val("");
			app.saCodeKey.val("");
			app.saCodeDesc.val("");
			$("#saCodeHelp").attr('disabled', true);
			
			//investorAccount($("#fundCode").val(), '');
			$('#saCode').removeClass('fieldError');
			$("#errSaCode").html("");

			$("#accountNoAll").attr("checked", true);
			$("#accountCode").attr('disabled', true);
			app.accountCode.val("");
			app.accountCodeKey.val("");
			app.accountCodeDesc.val("");
			$("#accountCodeHelp").attr('disabled', true);
		});

	    $("#saCodeBlank").click(function(){
	    	changePickTransCode();
			$("input[name=saCodeOption]")[0].checked == true;
			$("#saCode").attr('disabled', false);
			app.saCode.val("");
			app.saCodeKey.val("");
			app.saCodeDesc.val("");
			$("#saCodeHelp").attr('disabled', false);

			//investorAccount($("#fundCode").val(), $("#saCodeKey").val());
			$("#accountNoAll").attr("checked", true);
			$("#accountCode").attr('disabled', true);
			app.accountCode.val("");
			app.accountCodeKey.val("");
			app.accountCodeDesc.val("");
			$("#accountCodeHelp").attr('disabled', true);
		});

	    $("#accountNoAll").click(function() {
	    	changePickTransCode();
			$("input[name=accountOption]")[0].checked == true;
			$("input[name=accountOption]")[1].checked == false;
			$("#accountCode").attr('disabled', true);
			app.accountCode.val("");
			app.accountCodeKey.val("");
			app.accountCodeDesc.val("");
			$("#accountCodeHelp").attr('disabled', true);
			$("#accountArea label:first .req").remove();
			$("#errAccount").html("");
			$('#accountCode').removeClass('fieldError');
		});

	    $("#accountBlank").click(function() {
	    	changePickTransCode();
	    	$("input[name=accountOption]")[0].checked == false;
			$("input[name=accountOption]")[1].checked == true;
			$("#accountCode").attr('disabled', false);
			app.accountCode.val("");
			app.accountCodeKey.val("");
			app.accountCodeDesc.val("");
			$("#accountCodeHelp").attr('disabled', false);
			/*if($("#accountArea label .req").text() == "")
			{
				$("#accountArea label:first").append($("<span class='req'>*</span>"));
			}*/
		});

	    $('#btnGenerate').click(function() {
	    	var fund = app.fundCode.val();
	    	var sa = app.saCode.val();
	    	var account = app.accountCode.val();
	    	var dateFrom = $('#tradeDateFrom').val();
	    	var dateTo = $('#tradeDateTo').val();
	    	var active = $("input[name='active']")[1].checked;

	    	if(document.getElementById('fundBlank').checked && app.fundCode.val() == "" &&
	    			document.getElementById('saCodeBlank').checked && app.saCode.val() == "" &&
	    			document.getElementById('accountBlank').checked && app.accountCode.val() == "")
	    	{
	    		$("#errFund").html(" Required ");
	    		$("#errSaCode").html(" Required ");
	    		$("#errAccount").html(" Required ");
				return false;
	    	}
	    	else
	    	{
	    		$("#errFund").html("");
	    		$("#errSaCode").html("");
	    		$("#errAccount").html("");
	    	}
	    	
	    	if(document.getElementById('fundBlank').checked &&  app.fundCode.val() == "" )
	    	{
				$("#errFund").html(" Required ");
				return false;
			}
	    	else
	    	{
	    		$("#errFund").html("");
	    	}

	    	if(document.getElementById('saCodeBlank').checked && app.saCode.val() == "" )
	    	{
				$("#errSaCode").html(" Required ");
				return false;
			}
	    	else
	    	{
	    		$("#errSaCode").html("");
	    	}

	    	if(document.getElementById('accountBlank').checked && app.accountCode.val() == "" )
	    	{
				$("#errAccount").html(" Required ");
				return false;
			}
	    	else
	    	{
	    		$("#errAccount").html("");
	    	}

	    	if(dateFrom == "" || dateTo == "")
	    	{
	    		if(dateFrom == "")
	    		{
	    			$("#tradeDateFromError").html(" Required ");
	    		}

	    		if(dateTo == "")
	    		{
	    			$("#tradeDateToError").html(" Required ");
	    		}
	    		return false;
	    	}

	    	var checkError = $('input').hasClass('fieldError');
			if(checkError)
			{
				return false;
			}

	    	loading.dialog('open');
			$.get("@{DownloadUnitTrust.generateFileDownload()}", {'fund':fund, 'sa':sa, 'cif':account, 'dateFrom':dateFrom, 'dateTo':dateTo, 'active':active, 'downloadTo':'txt', 'fundClassCode':'' }, function(data) {
				checkRedirect(data);
	    		loading.dialog('close');
	    		if(data[0] == 'success')
	    		{
	    			$('#reportPost').attr("href", "@{DownloadUnitTrust.downloadReport()}/"+data[1]);
	    			$('#reportMail').attr("href", "@{DownloadUnitTrust.downloadReport()}/"+data[2]);
	    			$('#post').val(data[1]);
	    			$('#email').val(data[2]);
	    			$('#idPost').css("display","");
	    		    $('#idEmail').css("display","");
	    		    $("#errFund").html('');
	    		    $("#errSaCode").html('');
	    		    $("#errAccount").html('');
	    		    $("#tradeDateFromError").html('');
	    		    $("#tradeDateToError").html('');
	    			messageAlertOk("Data Success Generated", "ui-icon ui-icon-circle-check", "Notification", closeDialogMessage);
	    		}
	    		else if(data[0] == 'empty')
	    		{
	    			$('#post').val('');
	    			$('#email').val('');
	    			$('#idPost').css("display","none");
	    		    $('#idEmail').css("display","none");
	    			messageAlertOk("No Data Generated", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
	    		}
	    		else
	    		{
	    			$('#post').val('');
	    			$('#email').val('');
	    			$('#idPost').css("display","none");
	    		    $('#idEmail').css("display","none");
	    			messageAlertOk("Data Failed Generated", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
	    		}
	    	});
		});

	    $('#reportPost').click(function() {
	    	var post = $('#post').val();
	    	var cekFile = true;
	    	if(post == null || post == '')
	    	{
	    		messageAlertOk("File is empty", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
    			return false;
	    	}
	    	else
	    	{
	    		loading.dialog('open');
		    	$.get("@{DownloadUnitTrust.downloadReport()}", {'id':post}, function(data) {
					checkRedirect(data);
		    		loading.dialog('close');
		    		if(data == "not")
		    		{
		    			messageAlertOk("File Not Exist", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
		    			return false;
		    		}
		    		else
		    		{
		    			cekFile = false;
		    			return true;
		    		}
		    	});
	    	}
	    	if(cekFile)
	    	{
	    		return false;
	    	}
	    });

	    $('#reportMail').click(function() {
	    	var mail = $('#email').val();
	    	var cekFile = true;
	    	if(mail == null || mail == '')
	    	{
	    		messageAlertOk("File is empty", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
    			return false;
	    	}
	    	else
	    	{
	    		loading.dialog('open');
		    	$.get("@{DownloadUnitTrust.downloadReport()}", {'id':mail}, function(data) {
					checkRedirect(data);
		    		loading.dialog('close');
		    		if(data == "not")
		    		{
		    			messageAlertOk("File Not Exist", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
		    			return false;
		    		}
		    		else
		    		{
		    			cekFile = false;
		    			return true;
		    		}
		    	});
	    	}

	    	if(cekFile)
	    	{
	    		return false;
	    	}
	    });

	    app.fundClassCode.popupLookup("?group=FUND_CLASS", "operator", function(data){
			app.fundClassCode.removeClass('fieldError');
			$("#fundAll").attr("checked", true);
			app.fundAll.click();
			changePopupFund();
		}, function() {
			app.fundClassCode.addClass('fieldError');
			app.fundClassCodeDesc.val('NOT FOUND');
			app.fundClassCodeKey.val('');
			app.fundClassCode.val('');
			app.h_fundClassCodeDesc.val('');
			});
	    
	    $('#saCode').dynapopup('PICK_DWN_SELLING_AGENT', '', 'saCode');
	    $('#accountCode').dynapopup('PICK_RG_INVEST_CIF', '', 'accountCode');
	    
	    function changePickTransCode(){
	    	if (app.fundCode.val().isEmpty() && app.saCode.val().isEmpty())
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_CIF', '', 'accountCode');
	    	else if (!app.fundCode.val().isEmpty() && app.saCode.val().isEmpty())
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_CIF_BY_PROD', app.fundCode.val(), 'accountCode');
	    	else if (app.fundCode.val().isEmpty() && !app.saCode.val().isEmpty())
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_CIF_BY_SACODE', app.saCodeKey.val(), 'accountCode');
	    	else
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_CIF_BY_PROD_AND_SACODE', app.fundCode.val()+"|"+app.saCodeKey.val(), 'accountCode');
	    }
	    
	    function changePopupFund() {
	    	app.fundCode.dynapopup('PICK_RG_PRODUCT', '', 'saCode');
		}
	    
	    app.fundCode.blur(function() {
			if((app.fundCode.isChange()) || (app.fundCode.val() == ""))
			{
				if(!app.fundCode.val().isEmpty()) $('#errFund').html('');
				app.accountCode.removeClass('fieldError');
				app.accountCode.val("");
				app.accountCodeKey.val("");
				app.accountCodeDesc.val("");
				app.h_accountCodeDesc.val("");
			}
			changePickTransCode();
		});
	    
	    app.fundClassCode.change(function() {
	    	changePopupFund();
	    });
	    
	    app.fundCode.change(function() {
	    	changePickTransCode();
	    });
	    app.saCode.blur(function() {
	    	changePickTransCode();
		});

	    app.saCode.change(function(){
	    	changePickTransCode();
	    	if (!app.saCode.val().isEmpty()) $('#errSaCode').html('');
	    });

	    app.accountCode.change(function() {
	    	if(!app.accountCode.val().isEmpty())
	    	{
	    		$('#errAccount').html('');
	    	}
	    });

	    app.tradeDateFrom.change(function() {
			var dateFrom = $(this).datepicker('getDate');
	        var dateTo = app.tradeDateTo.datepicker('getDate');
	        var origin = 'from';
	        var id = '#tradeDate';
	        if((($(this).val()!='') || ($(this).hasClass('fieldError'))) && (app.tradeDateTo.val()!=''))
	        {
	            compareDateFromTo(dateFrom, dateTo, origin, id);
	        }

	        if($(this).val() != '')
	        {
	        	$("#tradeDateFromError").html('');
	        }
	    });

		app.tradeDateTo.change(function() {
	        var dateFrom = app.tradeDateFrom.datepicker('getDate');
	        var dateTo = $(this).datepicker('getDate');
	        var origin = 'to';
	        var id = '#tradeDate';
	        if((($(this).val()!='') || ($(this).hasClass('fieldError'))) && (app.tradeDateFrom.val()!=''))
	        {
	            compareDateFromTo(dateFrom, dateTo, origin, id);
	        }

	        if($(this).val() != '')
	        {
	        	$("#tradeDateToError").html('');
	        }
	    });

		//investorAccount('', '');
		changePopupFund();
	}
	else
	{
		return new DownloadUnitTrust(html);
	}
}