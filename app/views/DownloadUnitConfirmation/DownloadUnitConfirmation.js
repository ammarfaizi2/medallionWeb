function DownloadUnitConfirmation(html) {
	if (this instanceof DownloadUnitConfirmation) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);

/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.tradeDateFrom.datepicker();
	    app.tradeDateTo.datepicker();

	    $('#idPost').css("display","none");
	    $('#idEmail').css("display","none");
	    
	    $("#fundAll").attr("checked", true);
		$("#fundCode").attr('disabled', true);
		$("#fundCode").val("");
		$("#fundKey").val("");
		$("#fundDesc").val("");
		$("#fundHelp").attr('disabled', true);

	    $("#saCodeAll").attr("checked", true);
		$("#saCode").attr('disabled', true);
		$("#saCode").val("");
		$("#saCodeKey").val("");
		$("#saCodeDesc").val("");
		$("#saCodeHelp").attr('disabled', true);

		$("#accountNoAll").attr("checked", true);
		$("#accountCode").attr('disabled', true);
		$("#accountCode").val("");
		$("#accountCodeKey").val("");
		$("#accountCodeDesc").val("");
		$("#accountCodeHelp").attr('disabled', true);
		
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

	    $("#fundAll").click(function(){
	    	changePickTransCode();
			$("input[name=fundOption]")[0].checked == true;
			$("#fundCode").attr('disabled', true);
			$("#fundCode").val("");
			$("#fundKey").val("");
			$("#fundDesc").val("");
			$("#fundHelp").attr('disabled', true);

			//investorAccount('', $("#saCodeKey").val());
			$('#fundCode').removeClass('fieldError');
			$("#errFund").html("");

			$("#accountNoAll").attr("checked", true);
			$("#accountCode").attr('disabled', true);
			$("#accountCode").val("");
			$("#accountCodeKey").val("");
			$("#accountCodeDesc").val("");
			$("#accountCodeHelp").attr('disabled', true);
		});
	    
	    $("#fundBlank").click(function(){
	    	changePickTransCode();
			$("input[name=fundOption]")[0].checked == true;
			$("#fundCode").attr('disabled', false);
			$("#fundCode").val("");
			$("#fundKey").val("");
			$("#fundDesc").val("");
			$("#fundHelp").attr('disabled', false);
			
			//investorAccount($("#fundCode").val(), $("#saCodeKey").val());
			$("#accountNoAll").attr("checked", true);
			$("#accountCode").attr('disabled', true);
			$("#accountCode").val("");
			$("#accountCodeKey").val("");
			$("#accountCodeDesc").val("");
			$("#accountCodeHelp").attr('disabled', true);
		});

	    $("#saCodeAll").click(function(){
	    	changePickTransCode();
			$("input[name=saCodeOption]")[0].checked == true;
			$("#saCode").attr('disabled', true);
			$("#saCode").val("");
			$("#saCodeKey").val("");
			$("#saCodeDesc").val("");
			$("#saCodeHelp").attr('disabled', true);

			//investorAccount($("#fundCode").val(), '');
			$('#saCode').removeClass('fieldError');
			$("#errSaCode").html("");

			$("#accountNoAll").attr("checked", true);
			$("#accountCode").attr('disabled', true);
			$("#accountCode").val("");
			$("#accountCodeKey").val("");
			$("#accountCodeDesc").val("");
			$("#accountCodeHelp").attr('disabled', true);
		});

	    $("#saCodeBlank").click(function(){
	    	changePickTransCode();
			$("input[name=saCodeOption]")[0].checked == true;
			$("#saCode").attr('disabled', false);
			$("#saCode").val("");
			$("#saCodeKey").val("");
			$("#saCodeDesc").val("");
			$("#saCodeHelp").attr('disabled', false);
			
			//investorAccount($("#fundCode").val(), $("#saCodeKey").val());
			$("#accountNoAll").attr("checked", true);
			$("#accountCode").attr('disabled', true);
			$("#accountCode").val("");
			$("#accountCodeKey").val("");
			$("#accountCodeDesc").val("");
			$("#accountCodeHelp").attr('disabled', true);
		});

	    $("#accountNoAll").click(function(){
	    	changePickTransCode();
			$("input[name=accountOption]")[0].checked == true;
			$("#accountCode").attr('disabled', true);
			$("#accountCode").val("");
			$("#accountCodeKey").val("");
			$("#accountCodeDesc").val("");
			$("#accountCodeHelp").attr('disabled', true);
			$('#accountCode').removeClass('fieldError');
			$("#errAccount").html("");
		});

	    $("#accountBlank").click(function(){
	    	changePickTransCode();
			$("input[name=accountOption]")[0].checked == true;
			$("#accountCode").attr('disabled', false);
			$("#accountCode").val("");
			$("#accountCodeKey").val("");
			$("#accountCodeDesc").val("");
			$("#accountCodeHelp").attr('disabled', false);
		});

	    $('#btnGenerate').click(function() {	    	
	    	var fund = $('#fundKey').val();
	    	//var account = $('#accountCodeKey').val();
	    	var account = $('#accountCode').val();
	    	var sa = $('#saCode').val();
	    	var dateFrom = $('#tradeDateFrom').val();
	    	var dateTo = $('#tradeDateTo').val();
	    	
	    	if(document.getElementById('fundBlank').checked && $("#fundKey").val() == "" && document.getElementById('saCodeBlank').checked && document.getElementById('accountBlank').checked && $("#accountCodeKey").val() == ""){
				$("#errFund").html(" Required ");
				$("#errSaCode").html(" Required ");
				$("#errAccount").html(" Required ");
				return false;
			}
	    	
	    	if(document.getElementById('fundBlank').checked && $("#fundKey").val() == "" ){
				$("#errFund").html(" Required ");
				return false;
			}
	    	
	    	if(document.getElementById('saCodeBlank').checked && $("#saCodeKey").val() == "" ){
				$("#errSaCode").html(" Required ");
				return false;
			}
	    	
	    	if(document.getElementById('accountBlank').checked && $("#accountCodeKey").val() == "" ){
				$("#errAccount").html(" Required ");
				return false;
			}
	    	
	    	if(dateFrom == "" || dateTo == ""){
	    		if(dateFrom == ""){
	    			$("#tradeDateFromError").html(" Required ");
	    		}
	    		if(dateTo == ""){
	    			$("#tradeDateToError").html(" Required ");
	    		}
	    		return false;
	    	}
	    	
	    	loading.dialog('open');
			$.get("@{DownloadUnitConfirmation.generateFileDownload()}", {'fund':fund, 'sa':sa, 'account':account, 'dateFrom':dateFrom, 'dateTo':dateTo, 'downloadTo':'txt'}, function(data) {
				checkRedirect(data);
	    		loading.dialog('close');
	    		if(data[0] == 'success'){
	    			$('#reportPost').attr("href", "@{DownloadUnitConfirmation.downloadReport()}/"+data[1]);
	    			$('#reportMail').attr("href", "@{DownloadUnitConfirmation.downloadReport()}/"+data[2]);
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
	    		} else if(data[0] == 'empty'){
	    			$('#post').val('');
	    			$('#email').val('');
	    			$('#idPost').css("display","none");
	    		    $('#idEmail').css("display","none");
	    			messageAlertOk("No Data Generated", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
	    		} else {
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
	    	if(post == null || post == ''){
	    		messageAlertOk("File is empty", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
    			return false;
	    	}else{
	    		loading.dialog('open');
		    	$.get("@{DownloadUnitConfirmation.downloadReport()}", {'id':post}, function(data) {
					checkRedirect(data);
		    		loading.dialog('close');
		    		if(data == "not"){
		    			messageAlertOk("File Not Exist", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
		    			return false;
		    		} else {
		    			cekFile = false;
		    			return true;
		    		}
		    	});
	    	}
	    	if(cekFile){
	    		return false;
	    	}
	    });
	    
	    $('#reportMail').click(function() {
	    	var mail = $('#email').val();
	    	var cekFile = true;
	    	if(mail == null || mail == ''){
	    		messageAlertOk("File is empty", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
    			return false;
	    	}else{
	    		loading.dialog('open');
		    	$.get("@{DownloadUnitConfirmation.downloadReport()}", {'id':mail}, function(data) {
					checkRedirect(data);
		    		loading.dialog('close');
		    		if(data == "not"){
		    			messageAlertOk("File Not Exist", "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
		    			return false;
		    		} else {
		    			cekFile = false;
		    			return true;
		    		}
		    	});
	    	}
	    	if(cekFile){
	    		return false;
	    	}
	    });
	    
	    $('#fundCode').lookup({
			list:'@{Pick.rgProducts()}',
			get:{
				url:'@{Pick.rgProduct()}',
				success: function(data){
					$('#fundCode').removeClass('fieldError');
					$('#fundKey').val(data.productCode);
					//investorAccount(data.productCode, $("#saCode").val());
					$('#fundDesc').val(data.description);
					$('#h_fundDesc').val(data.description);
					$("#errFund").html('');
				},
				error: function(data){
					$('#fundCode').addClass('fieldError');
					$('#fundCode').val('');
					$('#fundKey').val('');
					//investorAccount('', '');
					$('#fundDesc').val('NOT FOUND');
					$('#h_fundDesc').val('');
				}
			},
			description:$('#fundDesc'),
			help:$('#fundHelp')
		});

	    $('#saCode').lookup({
			list:'@{Pick.thirdPartiesSaCode()}',
			get:{
				url:'@{Pick.thirdPartySaCode()}',
				success: function(data) {
					if (data) {
						$('#saCode').removeClass('fieldError');
						$('#saCodeKey').val(data.code);
						$('#saCodeDesc').val(data.description);
						var searchSaCode = $('#saCodeKey').val();
						//investorAccount($('#fundKey').val(), searchSaCode);
					}
				},
				error : function(data) {
					$('#saCode').addClass('fieldError');
					$('#saCodeDesc').val('NOT FOUND');
					$('#saCode').val('');
					$('#saCodeKey').val('');
				}
			},
			key:$('#saCodeKey'),
			description:$('#saCodeDesc'),
			help:$('#saCodeHelp')
		});

	    function changePickTransCode(){
	    	if (app.fundCode.val().isEmpty() && app.saCode.val().isEmpty()){
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_CIF', '', 'accountCode');
	    	} else if (!app.fundCode.val().isEmpty() && app.saCode.val().isEmpty()){
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_CIF_BY_PROD', app.fundCode.val(), 'accountCode');
	    	} else if (app.fundCode.val().isEmpty() && !app.saCode.val().isEmpty()){
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_CIF_BY_SACODE', app.saCodeKey.val(), 'accountCode');
	    	} else {
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_CIF_BY_PROD_AND_SACODE', app.fundCode.val()+"|"+app.saCodeKey.val(), 'accountCode');
	    	}
	    }

	    function investorAccount(searchFundCode, searchSaCode) {
		    /*$('#accountCode').lookup({
				list:'@{Pick.rgInvestmentAcctsByProductCodeAndSaCode()}',
				get:{
					url:'@{Pick.rgInvestmentAcctByProductCodeAndSaCode()}',
					success: function(data){
						$('#accountCode').removeClass('fieldError');
						$('#accountCodeKey').val(data.accountNumber);
						$('#accountCodeDesc').val(data.description);
						$('#h_accountCodeDesc').val(data.description);
						$("#errAccount").html('');
					},
					error: function(data){
						$('#accountCode').addClass('fieldError');
						$('#accountCode').val('');
						$('#accountCodeKey').val('');
						$('#accountCodeDesc').val('NOT FOUND');
						$('#h_accountCodeDesc').val('');
					}
				},
				description:$('#accountCodeDesc'),
				filter : [ searchFundCode, searchSaCode ],
				help:$('#accountCodeHelp')
			});*/
	    	if (app.fundCode.val().isEmpty() && app.saCode.val().isEmpty())
	    		$('#accountCode').dynapopup('PICK_RG_INVEST', '', 'accountCode');
	    	else if (!app.fundCode.val().isEmpty() && app.saCode.val().isEmpty())
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_BY_PROD', app.fundCode.val(), 'accountCode');
	    	else if (app.fundCode.val().isEmpty() && !app.saCode.val().isEmpty())
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_BY_SACODE', app.saCodeKey.val(), 'accountCode');
	    	else
	    		$('#accountCode').dynapopup('PICK_RG_INVEST_BY_PROD_AND_SACODE', app.fundCode.val()+"|"+app.saCodeKey.val(), 'accountCode');
	    }

	    app.fundCode.blur(function(){
			if((app.fundCode.isChange()) || (app.fundCode.val() == "")){
				if (!app.fundCode.val().isEmpty()) $('#errFund').html('');
				app.accountCode.removeClass('fieldError');
				app.accountCode.val("");
				app.accountCodeKey.val("");
				app.accountCodeDesc.val("");
				app.h_accountCodeDesc.val("");
			}
			changePickTransCode();
		});

	    app.saCode.blur(function(){
			if((app.saCode.isChange()) || (app.saCode.val() == "")){
				if (!app.saCode.val().isEmpty()) $('#errSaCode').html('');
				app.accountCode.removeClass('fieldError');
				app.accountCode.val("");
				app.accountCodeKey.val("");
				app.accountCodeDesc.val("");
				app.h_accountCodeDesc.val("");
			}
			changePickTransCode();
		});

	    app.saCode.change(function(){
	    	if (!app.saCode.val().isEmpty()) $('#errSaCode').html('');
	    	changePickTransCode();
	    });

	    app.accountCode.change(function(){
	    	if (!app.accountCode.val().isEmpty()) $('#errAccount').html('');
	    });
	    
	    app.tradeDateFrom.change(function() {
			var dateFrom = $(this).datepicker('getDate');
	        var dateTo = app.tradeDateTo.datepicker('getDate');
	        var origin = 'from';
	        var id = '#tradeDate';
	        if((($(this).val()!='') || ($(this).hasClass('fieldError'))) && (app.tradeDateTo.val()!='')){
	            compareDateFromTo(dateFrom, dateTo, origin, id);
	        }

	        var checkError = $("input").hasClass('fieldError');
	        if(checkError){
	        	app.btnGenerate.button('disable');
	        } else {
	        	if(app.tradeDateTo.val()==''||app.tradeDateFrom.val()==''){
	        		app.btnGenerate.button('disable');
	        	} else {
	        		app.btnGenerate.button('enable');
	        	}
	        }

	        if($(this).val()!='') $("#tradeDateFromError").html('');
	    });

		app.tradeDateTo.change(function() {
	        var dateFrom = app.tradeDateFrom.datepicker('getDate');
	        var dateTo = $(this).datepicker('getDate');
	        var origin = 'to';
	        var id = '#tradeDate';
	        if((($(this).val()!='') || ($(this).hasClass('fieldError'))) && (app.tradeDateFrom.val()!='')){
	            compareDateFromTo(dateFrom, dateTo, origin, id);
	        }

	        var checkError = $("input").hasClass('fieldError');
	        if(checkError){
	            app.btnGenerate.button('disable');
	        } else {
	        	if(app.tradeDateFrom.val()==''||app.tradeDateTo.val()==''){
	        		app.btnGenerate.button('disable');
	        	} else {
	        		app.btnGenerate.button('enable');
	        	}
	        }

	        if($(this).val()!='') $("#tradeDateToError").html('');
	    });

		//investorAccount('', '');
	} else {
		return new DownloadUnitConfirmation(html);
	}
}