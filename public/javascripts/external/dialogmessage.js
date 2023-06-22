var loading = null;

(function(jQuery){
	$("#dialog-message").css("display","none");
});

function messageAlertBasic(message, icon, dialogTitle){
	$("#message").html(message);	
	$("#messageIcon").addClass(icon);
	$("#dialog-message").dialog({
		autoOpen:false,
		height:120,
		width:"auto",
		modal:true,
		resizable:false,
		title:dialogTitle,
	 	show: {
			effect: "fade",
		 	duration: 700
		},
		hide: {
			effect: "fade",
		 	duration: 700
		},
		open:function() {
			$(this).parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();
		}
	});
	$("#dialog-message").css('overflow','hidden');
	$("#dialog-message").dialog('open');
	$('.ui-widget-overlay').css('height',$('body').height());
}

function messageAlertOk(message, icon, dialogTitle, func){
	$("#message").html(message);	
	$("#messageIcon").addClass(icon);
	$("#dialog-message").dialog({
		autoOpen:false,
		height:120,
		width:"auto",
		modal:true,
		resizable:false,
		title:dialogTitle,
	 	show: {
			effect: "fade",
		 	duration: 700
		},
		hide: {
			effect: "fade",
		 	duration: 700
		},
		buttons: {
			"Ok": function(obj) {
				obj = true;
				$("#messageStatus").val(true);
				func();
				$(this).dialog("close");
			}
		},
		open:function() {
			$(this).parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();
		}
	});
	$("#dialog-message").css('overflow','hidden');
	$("#dialog-message").dialog('open');
	$('.ui-widget-overlay').css('height',$('body').height());
	
}

function messageAlertYesNo(message, icon, dialogTitle, funcYes, funcNo){
	$("#message").html(message);	
	$("#messageIcon").addClass(icon);
	$("#dialog-message").dialog({
		autoOpen:false,
		height:120,
		width:"auto",
		modal:true,
		resizable:false,
		title:dialogTitle,
	 	show: {
			effect: "fade",
		 	duration: 700
		},
		hide: {
			effect: "fade",
		 	duration: 700
		},
		buttons: {
			"Yes": function() {
				funcYes();
			},
			"No ": function() {
				funcNo();
			}	
		},
		open:function() {
			$(this).parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();
		}
	});
	$("#dialog-message").css('overflow','hidden');
	$("#dialog-message").dialog('open');
	$('.ui-widget-overlay').css('height',$('body').height());
}

function messageAlertYesNoCancel(message, icon, dialogTitle, funcYes, funcNo, funcCancel){
	$("#message").html(message);	
	$("#messageIcon").addClass(icon);
	$("#dialog-message").dialog({
		autoOpen:false,
		height:120,
		width:"auto",
		modal:true,
		resizable:false,
		title:dialogTitle,
	 	show: {
			effect: "fade",
		 	duration: 700
		},
		hide: {
			effect: "fade",
		 	duration: 700
		},
		buttons: {
			"Yes": function() {
				funcYes();
			},
			"No ": function() {
				funcNo();
			},
			"Cancel ": function() {
				funcCancel();
			}
		},
		open:function() {
			$(this).parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();
		}
	});
	$("#dialog-message").css('overflow','hidden');
	$("#dialog-message").dialog('open');
	$('.ui-widget-overlay').css('height',$('body').height());
}

function loadingDialog(loading) {
	loading.dialog({
		closeOnEscape: false,
		draggable: false,
		modal: true,
		resizable: false,
		open:function() {
			$(this).parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();
			$('.ui-widget-overlay').css('height',$('body').height());
		} 
	});
}
