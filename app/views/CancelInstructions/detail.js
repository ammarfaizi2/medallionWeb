function detail(html) {
	if (this instanceof detail) {
		/*==================================================================
		 * Function
		 *==================================================================*/
		function hideFormUI() {
			$('#m_OTC').hide();
			$('#m_OTCBond').hide();
			$('#m_BondTransfer').hide();
			$('#m_SecuritiesTransfer').hide();
			$('#m_prematchdata').hide();
		}
		
		var closeDialogMessage = function() {
			$("#dialog-message").dialog("close");
		};
		
		function validateCancelInstruction() {
			console.log("validating form");
			if (($('#externalreference').val() == '') || ($('#externalreference').val() == null)) {
				$('#externalreferenceError').html('Required');
			} else {
				$('#externalreferenceError').html('');
				return true;
			}
			return false;
		}
		
		/*================================================================== 
		 * GUI Variable
		 *================================================================== */
		hideFormUI();
		
		/*==================================================================
		 * Declare popup tabel /detail
		 * =================================================================*/
		$('#externalreference').lookup({
			list:'@{Pick.getCancelInstructionsMessageType()}',
			get:{
				url:'@{Pick.getCancelInstructionMessageType()}',
				success: function(data){
					$('#externalreference').removeClass('fieldError');
					$('#externalreference').val(data.externalreference);
					$('#uniqueidentifier').val(data.uniqueidentifier);
					$('#h_uniqueidentifier').val(data.uniqueidentifier);
					
					$('#externalreferenceError').html('');
					
					var vExternalRef = data.externalreference;
					var vOriginMessageType = data.name;
					
					$.get("@{CancelInstructions.getOutMessageNotCancel()}", {'externalReference':vExternalRef}, function(data) {
						checkRedirect(data);
						hideFormUI();
						
						console.log(data);
						if (data != null) {
							var messageType = data.cbestmessage.name;
							console.log(vOriginMessageType);
							console.log(messageType);
							if (messageType == "OTC" || messageType == "PrematchOTC") {
								$('#m_OTC #externalreference').val(data.cbestmessage.externalreference);
								$('#m_OTC #instructiontype').val(data.cbestmessage.instructiontype);
								$('#m_OTC #participantcode').val(data.cbestmessage.participantcode);
								$('#m_OTC #participantaccount').val(data.cbestmessage.participantaccount);
								$('#m_OTC #participantaccountcash').val(data.cbestmessage.participantaccountcash);
								$('#m_OTC #counterpartcode').val(data.cbestmessage.counterpartcode);
								$('#m_OTC #securitycodetype').val(data.cbestmessage.securitycodetype);
								$('#m_OTC #securitycode').val(data.cbestmessage.securitycode);
								$('#m_OTC #numberofsecurities').val(data.cbestmessage.numberofsecurities);
								$('#m_OTC #currencycode').val(data.cbestmessage.currencycode);
								$('#m_OTC #settlementamount').val(data.cbestmessage.settlementamount);
								$('#m_OTC #tradedate').val(data.cbestmessage.tradedate);
								$('#m_OTC #settlementdate').val(data.cbestmessage.settlementdate);
								$('#m_OTC #description').val(data.cbestmessage.description);
								
								$('#m_OTC').show();
							}
							if (messageType == "OTCBond" || messageType == "PrematchOTCBond") {
								$('#m_OTCBond #externalreference').val(data.cbestmessage.externalreference);
								$('#m_OTCBond #ctpreference').val(data.cbestmessage.ctpreference);
								$('#m_OTCBond #instructiontype').val(data.cbestmessage.instructiontype);
								$('#m_OTCBond #participantcode').val(data.cbestmessage.participantcode);
								$('#m_OTCBond #participantaccount').val(data.cbestmessage.participantaccount);
								$('#m_OTCBond #participantaccountcash').val(data.cbestmessage.participantaccountcash);
								$('#m_OTCBond #counterpartcode').val(data.cbestmessage.counterpartcode);
								$('#m_OTCBond #counterparttype').val(data.cbestmessage.counterparttype);
								$('#m_OTCBond #taxsubmittedbyksei').val(data.cbestmessage.taxsubmittedbyksei);
								if (data.cbestmessage.taxsubmittedbyksei === "Y") {
									$("#m_OTCBond #taxsubmittedbyksei1").attr("checked","true");
									$("#m_OTCBond #taxsubmittedbyksei2").attr("checked","");
								} else if (data.cbestmessage.taxsubmittedbyksei === "N") {
									$("#m_OTCBond #taxsubmittedbyksei1").attr("checked","");
									$("#m_OTCBond #taxsubmittedbyksei2").attr("checked","true");
								} else if (data.cbestmessage.taxsubmittedbyksei === undefined) {
									$("#m_OTCBond #taxsubmittedbyksei1").attr("checked","");
									$("#m_OTCBond #taxsubmittedbyksei2").attr("checked","");
								}
								$('#m_OTCBond #securitycodetype').val(data.cbestmessage.securitycodetype);
								$('#m_OTCBond #securitycode').val(data.cbestmessage.securitycode);
								$('#m_OTCBond #currencycode').val(data.cbestmessage.currencycode);
								$('#m_OTCBond #maturitydate').val(data.cbestmessage.maturitydate);
								$('#m_OTCBond #tradedate').val(data.cbestmessage.tradedate);
								$('#m_OTCBond #settlementdate').val(data.cbestmessage.settlementdate);
								$('#m_OTCBond #facevalue').val(data.cbestmessage.facevalue);
								$('#m_OTCBond #price').val(data.cbestmessage.price);
								$('#m_OTCBond #yield').val(data.cbestmessage.yield);
								$('#m_OTCBond #interestrate').val(data.cbestmessage.interestrate);
								$('#m_OTCBond #accrueddays').val(data.cbestmessage.accrueddays);
								$('#m_OTCBond #accruedinterest').val(data.cbestmessage.accruedinterest);
								$('#m_OTCBond #miscamount').val(data.cbestmessage.miscamount);
								$('#m_OTCBond #capitalgaintax').val(data.cbestmessage.capitalgaintax);
								$('#m_OTCBond #accruedinteresttax').val(data.cbestmessage.accruedinteresttax);
								$('#m_OTCBond #netproceeds').val(data.cbestmessage.netproceeds);
								$('#m_OTCBond #description').val(data.cbestmessage.description);
								
								$('#m_OTCBond').show();
							}
							if (messageType == "BondTransfer") {
								$('#m_BondTransfer #externalreference').val(data.cbestmessage.externalreference);
								$('#m_BondTransfer #participantcode').val(data.cbestmessage.participantcode);
								$('#m_BondTransfer #participantaccount').val(data.cbestmessage.participantaccount);
								$('#m_BondTransfer #sourceaccount').val(data.cbestmessage.sourceaccount);
								$('#m_BondTransfer #targetaccount').val(data.cbestmessage.targetaccount);
								$('#m_BondTransfer #securitycodetype').val(data.cbestmessage.securitycodetype);
								$('#m_BondTransfer #securitycode').val(data.cbestmessage.securitycode);
								$('#m_BondTransfer #numberofsecurities').val(data.cbestmessage.numberofsecurities);
								$('#m_BondTransfer #cleanprice').val(data.cbestmessage.cleanprice);
								$('#m_BondTransfer #yield').val(data.cbestmessage.yield);
								$('#m_BondTransfer #accruedinterestamount').val(data.cbestmessage.accruedinterestamount);
								$('#m_BondTransfer #netproceeds').val(data.cbestmessage.netproceeds);
								$('#m_BondTransfer #settlementdate').val(data.cbestmessage.settlementdate);
								$('#m_BondTransfer #description').val(data.cbestmessage.description);
								
								$('#m_BondTransfer').show();
							}
							if (messageType == "SecuritiesTransfer") {
								$('#m_SecuritiesTransfer #externalreference').val(data.cbestmessage.externalreference);
								$('#m_SecuritiesTransfer #instructiontype').val(data.cbestmessage.instructiontype);
								$('#m_SecuritiesTransfer #participantcode').val(data.cbestmessage.participantcode);
								$('#m_SecuritiesTransfer #sourceaccount').val(data.cbestmessage.sourceaccount);
								$('#m_SecuritiesTransfer #targetaccount').val(data.cbestmessage.targetaccount);
								$('#m_SecuritiesTransfer #securitycodetype').val(data.cbestmessage.securitycodetype);
								$('#m_SecuritiesTransfer #securitycode').val(data.cbestmessage.securitycode);
								$('#m_SecuritiesTransfer #numberofsecurities').val(data.cbestmessage.numberofsecurities);
								$('#m_SecuritiesTransfer #settlementdate').val(data.cbestmessage.settlementdate);
								$('#m_SecuritiesTransfer #description').val(data.cbestmessage.description);
								
								$('#m_SecuritiesTransfer').show();
							}
							if (vOriginMessageType == "PrematchData") {
								hideFormUI();

								$('#m_prematchdata #ctpreference').val(data.cbestmessage.ctpreference);
								$('#m_prematchdata #instructiontype').val(data.cbestmessage.instructiontype);							
								$('#m_prematchdata #participantcode').val(data.cbestmessage.participantcode);
								$('#m_prematchdata #participantaccount').val(data.cbestmessage.participantaccount);
								$('#m_prematchdata #participantaccountcash').val(data.cbestmessage.participantaccountcash);
								$('#m_prematchdata #counterpartcode').val(data.cbestmessage.counterpartcode);
								$('#m_prematchdata #counterparttype').val(data.cbestmessage.counterparttype);
								$('#m_prematchdata #taxsubmittedbyksei').val(data.cbestmessage.taxsubmittedbyksei);
								if (data.cbestmessage.taxsubmittedbyksei === "Y") {
									$("#m_prematchdata #taxsubmittedbyksei1").attr("checked","true");
									$("#m_prematchdata #taxsubmittedbyksei2").attr("checked","");
								} else if (data.cbestmessage.taxsubmittedbyksei === "N") {
									$("#m_prematchdata #taxsubmittedbyksei1").attr("checked","");
									$("#m_prematchdata #taxsubmittedbyksei2").attr("checked","true");
								} else if (data.cbestmessage.taxsubmittedbyksei === undefined) {
									$("#m_prematchdata #taxsubmittedbyksei1").attr("checked","");
									$("#m_prematchdata #taxsubmittedbyksei2").attr("checked","");
								}
								$('#m_prematchdata #securitycodetype').val(data.cbestmessage.securitycodetype);
								$('#m_prematchdata #securitycode').val(data.cbestmessage.securitycode);
								$('#m_prematchdata #currencycode').val(data.cbestmessage.currencycode);
								$('#m_prematchdata #maturitydate').val(data.cbestmessage.maturitydate);
								$('#m_prematchdata #tradedate').val(data.cbestmessage.tradedate);
								$('#m_prematchdata #settlementdate').val(data.cbestmessage.settlementdate);
								$('#m_prematchdata #facevalue').val(data.cbestmessage.facevalue);
								$('#m_prematchdata #price').val(data.cbestmessage.price);
								$('#m_prematchdata #yield').val(data.cbestmessage.yield);
								$('#m_prematchdata #interestrate').val(data.cbestmessage.interestrate);
								$('#m_prematchdata #accrueddays').val(data.cbestmessage.accrueddays);
								$('#m_prematchdata #accruedinterestamount').val(data.cbestmessage.accruedinterestamount);
								$('#m_prematchdata #miscamount').val(data.cbestmessage.miscamount);
								$('#m_prematchdata #capitalgaintax').val(data.cbestmessage.capitalgaintax);
								$('#m_prematchdata #accruedinteresttax').val(data.cbestmessage.accruedinteresttax);
								$('#m_prematchdata #netproceeds').val(data.cbestmessage.netproceeds);
								$('#m_prematchdata #description').val(data.cbestmessage.description);
								
								$('#m_prematchdata').show();
							}
						} else {
							console.log( "result is null" );
						}
					});
				},
				error: function(data){
					console.log("error lookup.");
					$('#externalreference').addClass('fieldError');
					$('#externalreference').val('');
					$('#uniqueidentifier').val('NOT FOUND');
				}
			},
			
			filter:$('#cancelmessagetype'),
			description:$('#uniqueidentifier'),
			help:$('#groupExternalReferenceHelp')
		});
		
		/*================================================================== 
		 * Event
		 *================================================================== */
		$('#cancel').click(function() { // action button for cancel instruction
			var resultValidate = validateCancelInstruction();
			if (!resultValidate) {
				return;
			}
			loading.dialog('open');
			$.post("@{save()}", $('#cancelForm').serialize(), function(data, status, xhr) {
	    		checkRedirect(data);
				loading.dialog('close');
				var backToEntry = function() {
					location = "@{entry()}";
				};
				console.log( data );
				if (data.status == 'success') {
					messageAlertOk(data.message, "ui-icon ui-icon-circle-check", "Notification", backToEntry);
					return;
				} else {
					messageAlertOk(data.error, "ui-icon1 ui-icon-circle-close", "Error Message", closeDialogMessage);
					return;
				}
			});
		});
		
		$('#reset').click(function() {
			location.href="@{CancelInstructions.entry()}";
		});
		
		$('#cancelmessagetype').change(function() {
			$('#externalreference').val('');
			$('#uniqueidentifier').val('');
			$('#h_uniqueidentifier').val('');
			
			hideFormUI();
		});
	} else {
		return new detail(html);
	}
}