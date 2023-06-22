function Entry(html) {
	if (this instanceof Entry) {
/* =========================================================================== 
 * Variable
 * ========================================================================= */
			
		var app = html.inject(this, true);
		var isReadOnly = app.readOnly.val() == 'true';
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
	  $("#save").unbind('click');
	  app.narrative.hide();
		
	  //https://quilljs.com
	  var quill = new Quill('#editor-container', {
		  modules: {
			  formula: false,
		      syntax: true,
		      toolbar: '#toolbar-container'
		    },
		    theme: 'snow',
		    readOnly: isReadOnly
	  });
/* =========================================================================== 
 * Function
 * ========================================================================= */
		
		function validate(){
			app.securityTypeError.html('');
			app.securityCodeError.html('');
			app.recordingDateError.html('');
			app.subjectError.html('');
			app.narrativeError.html('');
			app.deliverDateError.html('');
			app.fileError.html('');
			
			var valid = true;
			
			if (app.securityType.isEmpty()) { app.securityTypeError.required(true); valid = false }
			if (app.securityCode.isEmpty()) { app.securityCodeError.required(true); valid = false }
			if (app.recordingDate.isEmpty()) { app.recordingDateError.required(true); valid = false }
			if (app.subject.isEmpty()) { app.subjectError.required(true); valid = false }
			if (app.narrative.isEmpty()) { app.narrativeError.required(true); valid = false }
			if (app.deliverDate.isEmpty()) { app.deliverDateError.required(true); valid = false }
			if (app.deliverDate.isEmpty()) { app.deliverDateError.required(true); valid = false }
			if (app.addAttachment.is(':checked') && app.file.isEmpty() && app.filename.isEmpty()) { app.fileError.required(true); valid = false }
			return valid;
		}
		
		function attachSecurityCodePopup(cleanSecurityCode) {
			if (cleanSecurityCode) {
				app.securityCode.val('');
				app.securityCodeKey.val('');
				app.securityCodeDesc.val('');
				app.h_securityCodeDesc.val('');
			}
			
			var securityType = app.securityType.val();
			var picker = (securityType == '') ? 'PICK_SC_MASTER' : 'PICK_SC_MASTER_BY_SEC_TYPE';
			app.securityCode.dynapopup2({key:'securityCodeKey', help:'securityCodeHelp', desc:'securityCodeDesc'}, picker, securityType, 'search', function(data){
				app.securityCode.removeClass('fieldError');
				app.securityCodeKey.val(data.code);
				app.securityCodeDesc.val(data.description);
				app.h_securityCodeDesc.val(data.description);
			},function(data){
				app.securityCode.addClass('fieldError');
				app.securityCode.val('');
				app.securityCodeKey.val('');
				app.securityCodeDesc.val('NOT FOUND');
				app.h_securityCodeDesc.val('NOT FOUND');
			});
		}
		
		function attachDocumentState() {
			if (app.addAttachment.is(':checked')) { 
				app.attachmentReq.show();
				app.file.removeAttr('disabled');
			}else{
				app.file.attr('disabled', 'disabled');
				app.attachmentReq.hide(); 
			}
			
			if (isReadOnly) {
				app.pFile.hide();
				app.pAttachmentInfo.hide();
			}else{
				app.pFile.show();
				app.pAttachmentInfo.show();
			}
		}
		
		function replaceAll(str, find, replace) {
		    return str.replace(new RegExp(find, 'g'), replace);
		}
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
	
		app.securityType.lookup({
			list:'@{Pick.securityTypes()}',
			get:{
				url:'@{Pick.securityType()}',
				success: function(data){
					app.securityType.removeClass('fieldError');
					app.securityType.val(data.code);
					app.securityTypeDesc.val(data.description);
					app.h_securityTypeDesc.val(data.description);
					attachSecurityCodePopup(true);
 				},
				error: function(data){
					app.securityType.addClass('fieldError');
					app.securityType.val('');
					app.securityTypeDesc.val('NOT FOUND');
					app.h_securityTypeDesc.val('NOT FOUND');
					attachSecurityCodePopup(true);
				}
			},
			description : app.securityTypeDesc,
			help : app.securityTypeHelp,
			nextControl : app.securityCode
		});
		
		// ini untuk handle kalo securityType kosong, krn kalo kosong dia tidak trigger success maupun, error
		// nah fungsi ini untuk reset security code popup
		app.securityType.change(function() {
			if ($(this).val().isEmpty()) {
				attachSecurityCodePopup(false);		
			}
		});
		
		attachSecurityCodePopup(false);
		
		$("#save").bind('click', function(){
			var htmltag = $(".ql-editor").html();
			if (htmltag != '<p><br></p>') { app.narrative.val(htmltag); }
			
			if (validate()) {
				app.isActive1.removeAttr('disabled');
				app.isActive2.removeAttr('disabled');
				
				$("#form").attr('action', "@{save()}?mode=${mode}");
				$("#form").submit();
			}
		});
		
		app.preview.dialog({
			autoOpen:false,
			height:400,
			width:800,
			modal:true,
			resizable : true,
			title:'Preview',
			buttons: {
				"Close": function() {
					app.preview.dialog('close');
				}
			}			
		});
		
		app.btnPreview.click(function(){
			var content =  $(".ql-editor").html();
			
			content = replaceAll(content,'{CA_NEWS_VARIABLE-ACCOUNT_NAME}', 'Budi');
			content = replaceAll(content,'{CA_NEWS_VARIABLE-HOLDING}', '10.000.000');
			content = replaceAll(content,'{CA_NEWS_VARIABLE-SECURITY_CODE}', 'TLKM');
			
			app.preview.html(content).dialog('open');
		}).button();
		
		app.btnUse.click(function(){
			var variable = app.variable.val();
			if (variable != '') {
				var range = quill.getSelection();
				quill.insertText(range.index, "{"+variable+"}");
			}
		});
		
		app.addAttachment.click(function(){
			attachDocumentState();
		});
		
		attachDocumentState();
		
		
		// Varible itu tidk ada mapping di model dan tidak di simpan ke db, inihanya tool utuk insert data di narrative
		// oleh sebab itu data tidak ke mapping saat ganti page, dan gax perlu juga di tampilkan sehingga di hide saja
		if (isReadOnly) {
			app.variable.parent().hide();
		}else {
			app.variable.parent().show();
		}
		
		if (app.status.val() == 'R' || app.status.val() == '') {
			app.isActive1.attr('disabled', 'disabled');
			app.isActive2.attr('disabled', 'disabled');
		}
		

		if (app.narrative.val() != '') {
			$(".ql-editor").html(app.narrative.val());
		}
		
		if (app.filename.val() == '') { app.pDownload.hide();
		}else{ app.pDownload.show(); }
		
		app.file.bind('change', function() {
			if (this.files[0] != null) {
				app.fileError.html("");
				var filesize = this.files[0].size/1024;
				if (filesize > Number(app.attachmentMaxSize.val())) {
					app.fileError.html('Size of file max '+app.attachmentMaxSizeStr.val());
					$(this).val('');
				}			
			}
		});
		
		$("span[data-value='monospace']", $("#standalone-container")).remove();
		
		// Dummy entry
//		app.securityType.val('BOND');
//		app.securityCode.val('ADHI01ACN2');
//		app.recordingDate.val('12/09/2018');
//		app.subject.val('Subject');
//		app.subject.val('Subject');
//		app.narrative.val('Halooo');
//		app.deliverDate.val('18/09/2018');
		
	}else{
		return new Entry(html);
	}
	
}