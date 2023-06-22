function detail(html) {
	if (this instanceof detail) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
//		this.parameter = function() {
//			var p = new Object();
//			p.messageMode = app.messageMode.val();
//			p.fromDate = app.fromDate.val();
//			p.toDate = app.toDate.val();
//			p.sender = app.sender.val();
//			p.receiver = app.receiver.val();
//			p.securityType = app.securityType.val();
//			p.securityCode = app.securityCode.val();
//			p.isinCode = app.isinCode.val();
//			p.status = app.status.val();
//			return p;
//		};
		app.errors.css('height','100px');
		app.field95RReagDataSourceScheme.css('width', '60px');
		app.field95RDeagDataSourceScheme.css('width', '60px');
		app.field95RRecuDataSourceScheme.css('width', '60px');
		app.field95RDecuDataSourceScheme.css('width', '60px');
		
		$('label', app.field95RReagDataSourceScheme.parent()).css('width', '40px')
		$('label', app.field95RDeagDataSourceScheme.parent()).css('width', '40px')
		$('label', app.field95RRecuDataSourceScheme.parent()).css('width', '40px')
		$('label', app.field95RDecuDataSourceScheme.parent()).css('width', '40px')
		
		
		
/* =========================================================================== 
 * Function
 * ========================================================================= */
		this.setEnabled = function() {
			// Header
			app.messageMode.enabled();
			app.messageType.enabled();
			app.sender.enabled();
			app.senderHelp.enabled();
			app.receiver.enabled();
			app.receiverHelp.enabled();
			app.messagePriority.enabled();	
			app.deliveryMonitoring.enabled();	
			app.obsolescencePeriod.enabled();
			app.bankingPriority.enabled();
			app.messageUserReference.enabled();
			
			// General Information
			app.field20CSemeReference.enabled();
			app.field23G.enabled();
			app.field20CCommReference.enabled();
			
			//Trade Details
			app.field98ATradDate.enabled();
			app.field98CSettDate.enabled();
			app.field90ADealPrice.enabled();
			app.field35BIsin.enabled();
			app.field35BIsinHelp.enabled();
			app.field35BDesciption.enabled();
			app.field35BDesciptionHelp.enabled();
			
			// Financial Information
			app.field36BSettQuantity.enabled();
			app.field97AFiacQualifier.enabled();
			app.field97AFiacAccount.enabled();
			
			// Settlement Details
			app.field22FSetrIndicator.enabled();
			app.field22FRtgsIndicator.enabled();
			
			app.field95PBuyrBic.enabled();
			app.field95PBuyrBicHelp.enabled();
			
			app.field95PSellBic.enabled();
			app.field95PSellBicHelp.enabled();
			
			app.field95RReagDataSourceScheme.enabled();
			app.field95RReagProprietaryCode.enabled();
			app.field95RDeagDataSourceScheme.enabled();
			app.field95RDeagProprietaryCode.enabled();
			
			app.field95PReagBic.enabled();
			app.field97AReagBic.enabled();
			app.field95PDeagBic.enabled();
			app.field97ADeagBic.enabled();			
			
			app.field95RRecuDataSourceScheme.enabled();
			app.field95RRecuProprietaryCode.enabled();
			app.field95RDecuDataSourceScheme.enabled();
			app.field95RDecuProprietaryCode.enabled();
			
			app.field95PRecuBic.enabled();
			app.field97ARecuBic.enabled();
			app.field95PDecuBic.enabled();
			app.field97ADecuBic.enabled();	
			
			app.field95PPsetBic.enabled();
			app.field19ASettCurrency.enabled();
			app.field19ASettCurrencyHelp.enabled();
			app.field19ASettAmount.enabled();
			
			// Others
			app.mac.enabled();
			app.chk.enabled();
			app.systemStatus.enabled();
			
			// Text
			app.realMessage.enabled();
			app.message.enabled();
		}
		
		function ReagDeagRequired() {
			if (app.messageType.val() == '540' || app.messageType.val() == '541') {
				// field Deag menjadi mandatory dan field Reag menjadi optional
				$(".req", app.field95PDeagBic.parent()).show();
				$(".req", app.field97ADeagBic.parent()).show();				
				$(".req", app.field95RDeagDataSourceScheme.parent()).show();
				$(".req", app.field95RDeagProprietaryCode.parent()).show();
				
				$(".req", app.field95PReagBic.parent()).hide();
				$(".req", app.field97AReagBic.parent()).hide();
				$(".req", app.field95RReagDataSourceScheme.parent()).hide();
				$(".req", app.field95RReagProprietaryCode.parent()).hide();
			}
			
			if (app.messageType.val() == '542' || app.messageType.val() == '543') {
				// field Reag menjadi mandatory dan field Deag menjadi optional
				$(".req", app.field95PDeagBic.parent()).hide();
				$(".req", app.field97ADeagBic.parent()).hide();
				$(".req", app.field95RDeagDataSourceScheme.parent()).hide();
				$(".req", app.field95RDeagProprietaryCode.parent()).hide();
				
				$(".req", app.field95PReagBic.parent()).show();
				$(".req", app.field97AReagBic.parent()).show();
				$(".req", app.field95RReagDataSourceScheme.parent()).show();
				$(".req", app.field95RReagProprietaryCode.parent()).show();
			}
		}
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		if (app.messageMode.val() === "I") { 
			// Header
			app.messageMode.disabled();
			app.messageType.disabled();
			app.sender.disabled();
			app.senderHelp.disabled();
			app.receiver.disabled();
			app.receiverHelp.disabled();
			app.messagePriority.disabled();	
			app.deliveryMonitoring.disabled();	
			app.obsolescencePeriod.disabled();
			app.bankingPriority.disabled();
			app.messageUserReference.disabled();
			
			// General Information
			app.field20CSemeReference.disabled();
			app.field23G.disabled();
			app.field20CCommReference.disabled();
			
			//Trade Details
			app.field98ATradDate.disabled();
			app.field98CSettDate.disabled();
			app.field90ADealPrice.disabled();
			app.field35BIsin.disabled();
			app.field35BIsinHelp.disabled();
			app.field35BDesciption.disabled();
			app.field35BDesciptionHelp.disabled();
			
			// Financial Information
			app.field36BSettQuantity.disabled();
			app.field97AFiacQualifier.disabled();
			app.field97AFiacAccount.disabled();
			
			// Settlement Details
			app.field22FSetrIndicator.disabled();
			app.field22FRtgsIndicator.disabled();
			
			app.field95PBuyrBic.disabled();
			app.field95PBuyrBicHelp.disabled();
			
			app.field95PSellBic.disabled();
			app.field95PSellBicHelp.disabled();
			
			app.field95RReagDataSourceScheme.disabled();
			app.field95RReagProprietaryCode.disabled();
			app.field95RDeagDataSourceScheme.disabled();
			app.field95RDeagProprietaryCode.disabled();
			
			app.field95PReagBic.disabled();
			app.field97AReagBic.disabled();
			app.field95PDeagBic.disabled();
			app.field97ADeagBic.disabled();			
			
			app.field95RRecuDataSourceScheme.disabled();
			app.field95RRecuProprietaryCode.disabled();
			app.field95RDecuDataSourceScheme.disabled();
			app.field95RDecuProprietaryCode.disabled();
			
			app.field95PRecuBic.disabled();
			app.field97ARecuBic.disabled();
			app.field95PDecuBic.disabled();
			app.field97ADecuBic.disabled();			
			
			app.field95PPsetBic.disabled();
			app.field19ASettCurrency.disabled();
			app.field19ASettCurrencyHelp.disabled();
			app.field19ASettAmount.disabled();
			
			// Others
//			app.ack.disabled();
			app.chk.disabled();
			app.systemStatus.disabled();
			
			// Text
			app.realMessage.disabled();
			app.message.disabled();
		}else{ 
		}
		
		app.messagePriority.change(function(){
			if ($(this).val() == 'N') {
				app.obsolescencePeriod.val('020')
			}else if ($(this).val() == 'U') {
				app.obsolescencePeriod.val('003')
			}else{
				app.obsolescencePeriod.val('')
			}
		});
		
		app.field23G.change(function(){
			if ($(this).val() == 'NEWM') {
				app.p20CCommReference.show();
				app.p20CPrevReference.hide();
			}else if ($(this).val() == 'CANC') {
				app.p20CCommReference.hide();
				app.p20CPrevReference.show();
			}
		}).change();
		
		app.tabs.tabs();
		$("#tabs").css("height", "520px");
		
		app.messageType.dynapopup2({key:'messageTypeId', help:'messageTypeHelp'}, 'PICK_GN_LOOKUP', 'SWIFT_MSG_TYPE', 'sender', 
			function(data) { if (data) { app.messageTypeErr.removeClass('fieldError'); } },
			function(data) { app.messageTypeErr.addClass('fieldError'); }
		);
		
		app.sender.dynapopup2({key:'senderId', help:'senderHelp', desc:'senderDesc'}, 'PICK_SWIFT_SENDER', '', 'receiver', 
			function(data) { if (data) { app.senderErr.removeClass('fieldError'); } },
			function(data) { app.senderErr.addClass('fieldError'); }
		);
		
		app.receiver.dynapopup2({key:'receiverId', help:'receiverHelp', desc:'receiverDesc'}, 'PICK_SWIFT_RECEIVER', '', 'messagePriority', 
			function(data) { if (data) { app.receiverErr.removeClass('fieldError'); } },
			function(data) { app.receiverErr.addClass('fieldError'); }
		);
		
		app.messagePriority.dynapopup2({key:'messagePriorityId', help:'messagePriorityHelp'}, 'PICK_GN_LOOKUP', 'SWIFT_MSG_PRIORITY', 'deliveryMonitoring', 
			function(data) { if (data) { app.messagePriorityErr.removeClass('fieldError'); } },
			function(data) { app.messagePriorityErr.addClass('fieldError'); }
		);
		
		app.field35BIsin.dynapopup2({key:'field35BIsinId', help:'field35BIsinHelp', desc:'field35BIsinDesc'}, 'PICK_ISIN_CODE', '', 'field35BToggle', 
			function(data) { if (data) { app.field35BIsinErr.removeClass('fieldError'); } },
			function(data) { app.field35BIsinErr.addClass('fieldError'); }
		);
			
		app.field35BDesciption.dynapopup2({key:'field35BDesciptionId', help:'field35BDesciptionHelp', desc:'field35BDesciptionDesc'}, 'PICK_SC_MASTER', '', 'field35BToggle', 
			function(data) { if (data) { app.field35BDesciptionErr.removeClass('fieldError'); } },
			function(data) { app.field35BDesciptionErr.addClass('fieldError'); }
		);
		
		app.field35BToggle1.add(app.field35BToggle2).change(function(){
			if ($(this).attr("checked")) {
				if ($(this).val() == 'ISIN') {
					app.p35BIsin.show();
					app.p35BDesciption.hide();
				}else{
					app.p35BIsin.hide();
					app.p35BDesciption.show();
				}	
			}
		}).change();
		
		app.field95PBuyrBic.dynapopup2({key:'field95PBuyrBicId', help:'field95PBuyrBicHelp', desc:'field95PBuyrBicDesc'}, 'PICK_SWIFT_SENDER', '', 'field95PSellBic', 
			function(data) { if (data) { app.field95PBuyrBicErr.removeClass('fieldError'); } },
			function(data) { app.field95PBuyrBicErr.addClass('fieldError'); }
		);
		
		app.field95PSellBic.dynapopup2({key:'field95PSellBicId', help:'field95PSellBicHelp', desc:'field95PSellBicDesc'}, 'PICK_SWIFT_SENDER', '', 'field95PReagBic', 
			function(data) { if (data) { app.field95PSellBicErr.removeClass('fieldError'); } },
			function(data) { app.field95PSellBicErr.addClass('fieldError'); }
		);
		
		app.field95ToggleA1.add(app.field95ToggleA2).change(function(){
			if ($(this).attr("checked")) {
				if ($(this).val() == 'BIC') {
					app.field95ToggleAgentBIC.show();
					app.field95ToggleAgentDSC.hide();
				}else{
					app.field95ToggleAgentBIC.hide();
					app.field95ToggleAgentDSC.show();
				}	
			}			
			ReagDeagRequired();
		}).change();
		
		app.field95ToggleB1.add(app.field95ToggleB2).change(function(){
			if ($(this).attr("checked")) {
				if ($(this).val() == 'BIC') {
					app.field95ToggleCustodianBIC.show();
					app.field95ToggleCustodianDSC.hide();
				}else{
					app.field95ToggleCustodianBIC.hide();
					app.field95ToggleCustodianDSC.show();
				}	
			}			
		}).change();
		
		
		app.field19ASettCurrency.dynapopup2({key:'field19ASettCurrencyId', help:'field19ASettCurrencyHelp', desc:'field19ASettCurrencyDesc'}, 'PICK_GN_CURRENCY', '', 'field19ASettAmount', 
			function(data) { if (data) { app.field19ASettCurrencyErr.removeClass('fieldError'); } },
			function(data) { app.field19ASettCurrencyErr.addClass('fieldError'); }
		);
		
		app.fieldSettDateFormat.change(function(){
			if ($(this).val() == 'YYYYMMDD') {
				app.settDate98A.show();
				app.settDate98C.hide();
			}else if ($(this).val() == 'YYYYMMDDHHMMSS') {
				app.settDate98C.show();
				app.settDate98A.hide();
			}else{
				app.settDate98A.show();
				app.settDate98C.hide();
			}
		}).change();
		
		var dlgSts = $("#dialog-message").dialog({
			autoOpen: false,
			resizable: false,
			modal: true,
			width: 500,
			height: 130,
		    buttons: {
		    	Close: function() { 
		    		location.href="@{SwiftUpdate.list()}";
					return false;
		    	}
		    }
		});
	
		if (app.tagSwiftStatus.val() == 'CO' || app.tagSwiftStatus.val() == 'IN') {
			if (app.tagSwiftStatus.val() == 'CO') {
				$("#dialogStsMsg").html("Your SWIFT Instruction has been COMPLETED. Your Transaction No is ["+app.tagSwiftEntityNo.val()+"]");
			}
			if (app.tagSwiftStatus.val() == 'IN') {
				$("#dialogStsMsg").html("Your SWIFT Instruction still INCOMPLETE : [Error : "+app.tagSwiftErrorDescription.val()+"]");
			}
			
			dlgSts.dialog('open');
		}
		
		if (app.depositoryId.val() == 'DEPOSITORY_CODE-3' && 
		   (app.messageType.val() == '541' || app.messageType.val() == '543')) {
			// Payment Indicator  is Required
			$("span", app.field22FRtgsIndicator.parent()).show();
		}else{
			$("span", app.field22FRtgsIndicator.parent()).hide();
		}
		
//		app.root.accordion({collapsible: true});
//		
//		app.search.add(app.reset).button();
//		
//		app.sender.dynapopup2({key:'senderId', help:'senderHelp'}, 'PICK_GN_LOOKUP', 'SWIFT_SENDER', 'receiver', 
//			function(data) { if (data) { app.senderErr.removeClass('fieldError'); } },
//			function(data) { app.senderErr.addClass('fieldError'); }
//		);
//		
//		app.receiver.dynapopup2({key:'receiverId', help:'receiverHelp'}, 'PICK_GN_LOOKUP', 'SWIFT_RECEIVER', 'securityType', 
//			function(data) { if (data) { app.receiverErr.removeClass('fieldError'); } },
//			function(data) { app.receiverErr.addClass('fieldError'); }
//		);
//		
//		app.securityType.dynapopup2({key:'securityTypeId', help:'securityTypeHelp'}, 'PICK_SC_TYPE_MASTER', '', 'securityCode', 
//			function(data){
//				if (data) {					
//					app.securityType.removeClass('fieldError');
//					securityCodePopup('PICK_SC_MASTER_BY_SEC_TYPE', app.securityType.val());
//					isinCodePopup('PICK_ISIN_CODE_BY_SEC_TYPE', app.securityType.val());
//				}
//			},
//			function(data){
//				app.securityType.addClass('fieldError');
//				securityCodePopup('PICK_SC_MASTER');
//				isinCodePopup('PICK_ISIN_CODE');
//			}
//		);
//		
//		securityCodePopup('PICK_SC_MASTER');
//		isinCodePopup('PICK_ISIN_CODE')
//		
//		app.datatable = app.tblSwiftUpdate.paging("@{SwiftUpdate.paging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
//		app.search.click(function(){
//			app.datatable.fnPageChange("first");
//		});
//
//		app.reset.click(function(){
//			app.messageMode.val("");
//			app.fromDate.val("");
//			app.toDate.val("");
//			app.sender.val("");
//			app.senderId.val("");
//			app.receiver.val("");
//			app.receiverId.val("");
//			app.securityType.val("");
//			app.securityTypeId.val("");
//			app.securityCode.val("");
//			app.securityCodeId.val("");
//			app.isinCode.val("");
//			app.isinCodeId.val("");
//			app.status.val("");
//		});
//
//		app.datatable.bind("select", function(event, prop){
//			location.href="@{SwiftUpdate.edit()}/?id="+ prop.bean.swiftKey;
//			alert(location.href);
//			return false;
//		});
		
		// Query for picker
		// insert into gn_pick(id, query, title, typedata, created_by, created_date, modified_by, modified_date, widthdata, pkdata)
		// values ('PICK_ISIN_CODE', 'SELECT isin_code, description FROM sc_master WHERE is_active = 1 ORDER BY isin_code ASC', 'Isin Code, Description', 'String, String', 'MIGRATION', sysdate, 'MIGRATION', sysdate, '30, 70', 0)
		// insert into gn_pick(id, query, title, typedata, created_by, created_date, modified_by, modified_date, widthdata, pkdata)
		// values ('PICK_ISIN_CODE_BY_SEC_TYPE', 'SELECT isin_code, description FROM sc_master WHERE is_active = 1 and security_type = ? ORDER BY isin_code ASC', 'Isin Code, Description', 'String, String', 'MIGRATION', sysdate, 'MIGRATION', sysdate, '30, 70', 0)

	}else{
		return new detail(html);
	}
}