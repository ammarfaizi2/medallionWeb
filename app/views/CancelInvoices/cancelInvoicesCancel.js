$(function() {
	$('#cancel').button();
	$('#cancelInvoice').button();
	
	var backToList = function() {
		location = "@{list()}";
	}
	
	$('#cancelDate').change(function(){
	    var invoiceDateParam = $('#invoiceDate').datepicker('getDate');
	    var cancelDateParam = $(this).datepicker('getDate');
	    var errorMsg = 'Cancel Date must be greater or equal to the Invoice Date.';

	    if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#cancelDate').val()!='')) {
	        var invoiceDate = new Date(invoiceDateParam);
	        var cancelDate = new Date(cancelDateParam);
	        if (cancelDate.getTime() < invoiceDate.getTime()) {
                $('#cancelDate').addClass('fieldError');
                $('#cancelDateError').html(errorMsg);
	        } else {
	            $('#cancelDate').removeClass('fieldError');
	            $('#cancelDateError').html('');
	        }
	    }

	    var checkError = $("input").hasClass('fieldError');
	    if (checkError){
	        $('#cancelInvoice').button('disable');
	    }  else {
	        $('#cancelInvoice').button('enable');
	    }
	});

	var closeDialogMessage = function() {
		$("dialog-message").dialog('close');
	}
	
	$('#cancelInvoice').click(function() {
		$("#cancelDateError").html("");
		$("#remarksCancelError").html("");
		
        if (($('#cancelDate').val()=='') || ($('#remarksCancel').val()=='')) {
            
            if ($('#cancelDate').val()=='') {
                $("#cancelDateError").html("Required");
            }

            if ($('#remarksCancel').val()=='') {
                $("#remarksCancelError").html("Required");
            }
            return false;
        }
        
        if ($('#cancelDate').val()!=='') {
            $("#cancelDateError").html("");
        }
        
        if ($('#cancelDate').val()!=='') {
            $("#remarksCancelError").html("");
        }
        
		var action = "@{cancel()}?key=${csBilling?.billingKey}";
		var loading = $('<div id="loading" class="loading"><img src="@@{'/public/images/loading2.gif'}" style="margin:10px" align="middle" /> Saving...</div>').appendTo('body');
		loading.dialog({
			closeOnEscape: false,
			draggable: false,
			modal: true,
			resizable: false,
			open:function() {
				$(this).parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();
			}
		});
		
		//$.post(action, function(data, status, xhr) {
		$.post(action, $('#cancelInvoiceForm').serialize(), function(data, status, xhr) {
    		checkRedirect(data);
			loading.dialog('close');
			if (data.status == 'success') {
			    messageAlertOk("Cancel Successfully For Invoice No : " + data.invoiceNo, "ui-icon ui-icon-circle-check", "Notification", backToList);
			} else {
				//alert(data.error);
				messageAlertOk(data.error, "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
			}
		});
	});
	
	if ($.browser.msie){
        $("#remarksCancel[maxlength]").bind('input propertychange', function() {
            var maxLength = $(this).attr('maxlength');  
            if ($(this).val().length > maxLength) {  
                $(this).val($(this).val().substring(0, maxLength));  
            }  
        });
    }	
		
	$('#cancel').click(function() {
		location.href="@{list()}";
	});

	$('#dvNeedCancel').css("display","");
});