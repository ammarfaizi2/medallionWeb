$(function() {

		$('#holdingBasedOn').val('HOLDING_BASED_ON-2') ;
		$('.calendar').datepicker();
		$('#tabs').tabs();
		$('.buttons input:button').button();
		//$( "#form.form" ).attr("enctype", "multipart/form-data");
		if ($.browser.msie){
			$("#remarks[maxlength]").bind('input propertychange', function() {
		        var maxLength = $(this).attr('maxlength');  
		        if ($(this).val().length > maxLength) {  
		            $(this).val($(this).val().substring(0, maxLength));  
		        }  
		    });
			$("#remarkCorrection[maxlength]").bind('input propertychange', function() {
		        var maxLength = $(this).attr('maxlength');  
		        if ($(this).val().length > maxLength) {  
		            $(this).val($(this).val().substring(0, maxLength));  
		        }  
		    });
			$("#remarksCancel[maxlength]").bind('input propertychange', function() {
		        var maxLength = $(this).attr('maxlength');  
		        if ($(this).val().length > maxLength) {  
		            $(this).val($(this).val().substring(0, maxLength));  
		        }  
		    });
		}
		
		if ('${maintenanceLogKey}' != '') {
			$('#correction').show();
			$("#correction").css("display","");
		} else {
			if (($("input[name='corporateAnnouncement.needCorrection']").val() == 'true') && ($("input[name='corporateAnnouncement.remarkCorrection']").val() != '')) {
				
				$('#correction').show();
				$("#correction").css("display","");
				$('#needCorrection').attr('disabled', true);
				$('#needCorrection').attr("checked", true);
				$('#remarkCorrection').attr('disabled', true);
			}
			
		}
		
		if ('${inquiry}' == 'inquiry'){
			$('p[id=pRemarkCorrection] label span').html('');
		}
		
		if ($('#statusHidden').val().trim()== 'E' || $('#statusHidden').val().trim() == 'WS') {
			$('#pCancelDate label').html("Cancel Date <span class=\"req\">*</span>");
			$('#cancelDate').removeAttr("disabled");
			$('#cancelDate').val('${entitleDate?.format(appProp.dateFormat)}');
		} else {
			$('#pCancelDate label').html("Cancel Date ");
			$('#cancelDate').attr("disabled","disabled");
			$('#cancelDate').val('');
		}
		
		if (!$('#flagAttachFile').is(':checked')){
			$('p[id=pAttach] label span').html("");
			$("#attachFile").attr("disabled", "disabled");
		}
		
		
		$("#flagAttachFile").change(function(){
			if ($(this).is(":checked")){
				$('p[id=pAttach] label span').html(" *");
				$("#attachFile").removeAttr("disabled");
			} else {
				$('p[id=pAttach] label span').html("");
				$("#attachFile").attr("disabled", "disabled");
			}
		});
			
		$('#securityType').change(function() {
			if ($(this).val() == "") {
				$(this).removeClass('fieldError');
				$('#securityCode').removeClass('fieldError');
				$('#securityKey').val("");
				$('#securityCode').val("");
				$('#securityCodeDesc').val("");
				
				$('#securityTypeSource').val("");
				$('#securityTypeSourceHidden').val("");
				
				$('#securityCodeSource').val("");
				$('#securityCodeSourceHidden').val("");
				$('#securityCodeSourceKeyHidden').val("");
				
				$('#actionCode').val('');
				$('#actionCode').removeClass('fieldError');
				$('#actionCodeKey').val('');
				$('#actionCodeDesc').val('');
				$('#h_actionCodeDesc').val('');
				
				DefaultCouponSchedule();
				DefaultScreenRatioTarget();
				DefaultInfoCoupon();
			}
		});
		
		$('#securityType').lookup({
			list:'@{Pick.securityTypes()}',
			get:{
				url:'@{Pick.securityType()}',
				success: function(data) {
					if (data) {
						$('#securityType').removeClass('fieldError');
						$('#securityType').val(data.code);
						$('#securityTypeDesc').val(data.description);
						$('#h_securityTypeDesc').val(data.description);
						$('#securityTypeSource').val(data.code);
						$('#securityTypeSourceHidden').val(data.code);
						
						$('#securityCode').removeClass('fieldError');
						$('#securityCode').val('');
						$('#securityCodeDesc').val('');
						$('#securityKey').val('');
						$('#h_securityCodeDesc').val('');
						
						$('#securityCodeSource').val("");
						$('#securityCodeSourceHidden').val("");
						$('#securityCodeSourceKeyHidden').val("");
						
						$('#actionCode').removeClass('fieldError');
						$('#actionCode').val('');
						$('#actionCodeKey').val('');
						$('#actionCodeDesc').val('');
						$('#h_actionCodeDesc').val('');
						
						DefaultCouponSchedule();
						DefaultScreenRatioTarget();
						DefaultScreenTaxable();
						DefaultInfoCoupon();
						
					}	
				},
				error: function(data) {
					$('#securityType').addClass('fieldError');
					$('#securityTypeDesc').val('NOT FOUND');
					//$('#securityType').val(data.code);
					$('#securityType').val('');
					$('#h_securityTypeDesc').val('');
					
					$('#securityTypeSource').val('');
					$('#securityTypeSourceHidden').val('');
					
					$('#securityCode').removeClass('fieldError');
					$('#securityCode').val('');
					$('#securityCodeDesc').val('');
					$('#securityKey').val('');
					$('#h_securityCodeDesc').val('');
					
					$('#securityCodeSource').val("");
					$('#securityCodeSourceHidden').val("");
					$('#securityCodeSourceKeyHidden').val("");
					
					$('#actionCode').removeClass('fieldError');
					$('#actionCode').val('');
					$('#actionCodeKey').val('');
					$('#actionCodeDesc').val('');
					$('#h_actionCodeDesc').val('');
					
					DefaultCouponSchedule();
					DefaultScreenRatioTarget();
					DefaultScreenTaxable();
					DefaultInfoCoupon();
				}
			},
			description:$('#securityTypeDesc'),
			help:$('#securityTypeHelp')
		});
		
		
		$('#securityCode').change(function(){
			if ($(this).val()==''){
				$(this).removeClass('fieldError');
				$('#securityKey').val('');
				$('#securityCode').val('');
				$('#h_securityCodeDesc').val('');
				
				DefaultCouponSchedule();
				DefaultInfoCoupon();
			}
		});
		
		$('#securityCode').lookup({
			list:'@{Pick.securities()}',
			get: {
				url:'@{Pick.security()}',
				success: function(data) {
					if (data) {
						$('#securityCode').removeClass('fieldError');
						$('#securityKey').val(data.code);
						$('#securityCodeDesc').val(data.description);
						$('#h_securityCodeDesc').val(data.description);
						$("#securityCodeSource").val($('#securityCode').val());
						$("#securityCodeSourceHidden").val($('#securityCode').val());
						$('#securityCodeSourceKeyHidden').val(data.code);
						
						$('#couponSchedule').removeClass('fieldError');
						$('#couponSchedule').val('');
						$('#couponScheduleKey').val('');
						$('#couponScheduleDesc').val('');
						$('#h_couponScheduleDesc').val('');
						DefaultInfoCoupon();
					}
				},
				error: function(data) {
					$('#securityCode').addClass('fieldError');
					$('#securityCodeDesc').val('NOT FOUND');
					$('#securityKey').val('');
					$('#securityCode').val('');
					$('#h_securityCodeDesc').val('');
					
					$("#securityCodeSource").val('');
					$("#securityCodeSourceHidden").val('');
					$('#securityCodeSourceKeyHidden').val('');
					
					DefaultCouponSchedule();
					DefaultInfoCoupon();
				}
			},
			description:$('#securityCodeDesc'),
			filter:$('#securityType'),
			help:$('#securityCodeHelp')
		});
		
		$('#actionCode').change(function(){
			if ($(this).val()==''){
				$(this).removeClass('fieldError');
				$('#actionCodeKey').val('');
				$('#actionCodeDesc').val('');
				$('#h_actionCodeDesc').val('');
				
				DefaultCouponSchedule();
				DefaultScreenRatioTarget();
				DefaultScreenTaxable();
				DefaultInfoCoupon();
			}
		});
		
		$('#actionCode').lookup({
			list:'@{Pick.actionTemplates()}',
			get:{
				url:'@{Pick.actionTemplate()}',
				success: function(data) {
					if (data) {
						$('#actionCode').removeClass('fieldError');
						$('#actionCodeKey').val(data.code);
						$('#actionCodeDesc').val(data.description);
						$('#h_actionCodeDesc').val(data.description);
						$('#hasExercisePrice').val(data.hasExercisePrice);
						$('#announcementLinkActionKey').val(data.actionCodeLinkKey);
						$('#targetType').val(data.targetType);
						if (data.taxObject!=''){
							$('input[name="corporateAnnouncement.taxable"]').removeAttr("disabled");
							$('input[name="corporateAnnouncement.taxable"]').val(true);
							$('#taxable1').attr("checked", true);
							$('#taxable2').attr("checked", false);
							$('#taxableObject').val(data.taxObjectDesc);
							$('#taxableObjectId').val(data.taxObject);
							$('#taxObject').val(data.taxObject);
							$('#taxableObjectOri').val(data.taxObjectDesc);
							$('#taxableObjectIdOri').val(data.taxObject);
							$('#holdingBaseOn').val(data.holdingBaseOn);
							if (data.hasExercisePrice) {
								$('p[id=pExercisePrice] label span').html(" *");
								$('#exercisePrice').removeAttr("disabled");
							} else {
								$('p[id=pExercisePrice] label span').html("");
								$('#exercisePrice').attr("disabled","disabled");
							}
							
							console.log("data.actionCodeLinkKey =" +data.actionCodeLinkKey);
							if (data.actionCodeLinkKey!=undefined){
								$('#cekBoxTaxApplied').val(true);
								$('#hidCekBoxTaxApplied').val(true);
								$('#cekBoxTaxApplied').attr("checked", true);
								$('p[id=pActionTemplateLink] label span').html(" *");
								$('#announcementLink').removeAttr("disabled");
								$('#announcementLinkHelp').removeAttr("disabled");
								/*$('#announcementLink').val(data.actionCodeLinkCode);
								$('#actionTemplateLinkKey').val(data.actionCodeLink);
								$('#actionTemplateLinkDesc').val(data.actionCodeLinkDesc);*/
							} else {
								$('#cekBoxTaxApplied').val(false);
								$('#hidCekBoxTaxApplied').val(false);
								$('#cekBoxTaxApplied').attr("checked", false);
								$('p[id=pActionTemplateLink] label span').html(" ");
								$('#announcementLink').attr("disabled","disabled");
								$('#announcementLinkHelp').attr("disabled","disabled");
							}
						} else {
							DefaultScreenTaxable();
						}
						
						$('#securityTypeTarget').attr('disabled','disabled');
						$('#securityTypeTargetHelp').attr('disabled','disabled');
						$('#securityCodeTarget').attr('disabled','disabled');
						$('#securityCodeTargetHelp').attr('disabled','disabled');
						if (data.targetType == '${targetTypeCash}'){
							$('#securityTypeTarget').val('${securityTypeCash}');
							$('#securityTypeTargetDesc').val('${securityTypeCash}');
							$('#securityTypeTargetHidden').val('${securityTypeCash}');
							$('#securityTypeTargetDescHidden').val('${securityTypeCash}');
							
							$('#securityCodeTarget').val('${securityTypeCash}');
							$('#securityCodeTargetKey').val('${securityKeyCash}');
							$('#securityCodeTargetDesc').val('${securityTypeCash}');
							$('#securityCodeTargetHidden').val('${securityTypeCash}');
							$('#securityCodeTargetDescHidden').val('${securityTypeCash}');
						}
						
						if (data.targetType == '${targetTypeSame}'){
							$('#securityTypeTarget').val($('#securityType').val());
							$('#securityTypeTargetHidden').val($('#securityType').val());
							$('#securityTypeTargetDesc').val($('#securityTypeDesc').val());
							$('#securityTypeTargetDescHidden').val($('#securityTypeDesc').val());
							
							$('#securityCodeTarget').val($('#securityCode').val());
							$('#securityCodeTargetKey').val($('#securityKey').val());
							$('#securityCodeTargetDesc').val($('#securityCodeDesc').val());
							$('#securityCodeTargetHidden').val($('#securityCode').val());
							$('#securityCodeTargetDescHidden').val($('#securityCodeDesc').val());
						}
						
						if (data.targetType == '${targetTypeOther}'){
							$('#securityTypeTarget').removeAttr('disabled');
							$('#securityTypeTargetHelp').removeAttr('disabled');
							$('#securityTypeTarget').val('');
							$('#securityTypeTargetHidden').val('');
							$('#securityTypeTargetDesc').val('');
							$('#securityTypeTargetDescHidden').val('');
							
							$('#securityCodeTarget').removeAttr('disabled');
							$('#securityCodeTargetHelp').removeAttr('disabled');
							$('#securityCodeTarget').val('');
							$('#securityCodeTargetKey').val('');
							$('#securityCodeTargetDesc').val('');
							$('#securityCodeTargetHidden').val('');
							$('#securityCodeTargetDescHidden').val('');
						}
						
						$('#isCoupon').val(data.isCoupon);
						$('#transactionSource').val(data.sourceTransaction);
						$('#transactionTarget').val(data.targetTransaction);
						
						if (data.isCoupon){
							$('#couponSchedule').removeAttr('disabled');
							$('#couponScheduleHelp').removeAttr('disabled');
							$('p[id="pCouponSchedule"] label span').html(' *');
							$('#sourceQuantityRow').attr('disabled', 'disabled');
							$('#targetQuantityRow').attr('disabled', 'disabled');
							$('p[id=pSourceTargetQty] label span').html(' ');
							//DefaultCouponSchedule()
						} else {
							if ((data.sourceTransaction!='')&&(data.targetTransaction==null)){
								$('#sourceQuantityRow').attr('disabled', 'disabled');
								$('#sourceQuantityRow').val('1');
								$('#sourceQuantityRowStripped').val('1');
								$('#targetQuantityRow').attr('disabled', 'disabled');
								$('#targetQuantityRow').val('1');
								$('#targetQuantityRowStripped').val('1');
								$('p[id=pSourceTargetQty] label span').html('');
							} else {
//								DefaultCouponSchedule();
								$('#sourceQuantityRow').removeAttr('disabled');
								$('#targetQuantityRow').removeAttr('disabled');
								$('p[id=pSourceTargetQty] label span').html(' *');
							}
						}
						
						$('#couponSchedule').val('');
						$('#couponScheduleKey').val('');
						$('#couponScheduleDesc').val('');
						$('#h_couponScheduleDesc').val('');
						DefaultInfoCoupon();
					}	
				},
				error: function(data) {
					$('#actionCode').addClass('fieldError');
					$('#actionCodeDesc').val('NOT FOUND');
					$('#actionCode').val('');
					$('#h_actionCodeDesc').val('');
					DefaultCouponSchedule();
					DefaultScreenRatioTarget();
					DefaultScreenTaxable();
					DefaultInfoCoupon();
				}
			},
			filter:$('#securityType'),
			description:$('#actionCodeDesc'),
			help:$('#actionCodeHelp')
		});
		
		if	($('#announcementLinkActionKey').val()==''){
			$('#announcementLink').attr("disabled","disabled");
			$('#announcementLinkHelp').attr("disabled","disabled");
		}
		if (($('#isCoupon').val()=='false')||($('#isCoupon').val()=='')){
			$('#couponSchedule').attr('disabled', 'disabled');
			$('#couponScheduleHelp').attr('disabled', 'disabled');
			$('p[id="pCouponSchedule"] label span').html('');
			if (($('#transactionSource').val()!='')&&($('#transactionTarget').val()=='')){
				$('#sourceQuantityRow').attr('disabled', 'disabled');
				$('#targetQuantityRow').attr('disabled', 'disabled');
				$('p[id=pSourceTargetQty] label span').html('');
			}
		} else {
			$('#sourceQuantityRow').attr('disabled', 'disabled');
			$('#targetQuantityRow').attr('disabled', 'disabled');
			$('p[id=pSourceTargetQty] label span').html('');
			if (($('#transactionSource').val()=='')&&($('#transactionTarget').val()=='')){
				$('#sourceQuantityRow').attr('disabled', 'disabled');
				$('#targetQuantityRow').attr('disabled', 'disabled');
				$('p[id=pSourceTargetQty] label span').html('');
			}
		}
		
		$('#couponSchedule').change(function(){
			if ($(this).val()==''){
				$(this).removeClass('fieldError');
				$('#couponScheduleKey').val('');
				$('#couponScheduleDesc').val('');
				$('#h_couponScheduleDesc').val('');
				DefaultInfoCoupon();
			}
		});
		$('#couponSchedule').lookup({
			list:'@{Pick.couponSchedulesWithNoPaid()}',
			get: {
				url: '@{Pick.couponScheduleWithNoPaid()}',
				success: function(data) {
					if (data) {
						$('#couponSchedule').removeClass('fieldError');
						$('#couponSchedule').val(data.couponNo);
						$('#couponScheduleKey').val(data.securityKey);
						$('#couponScheduleDesc').val(data.description);
						$('#h_couponScheduleDesc').val(data.description);
						$('#fractionAmount').autoNumericSet(data.fraction);
						$('#fractionAmountStripped').val(data.fraction);
						$('#fractionRatio').autoNumericSet(data.fractionBase);
						$('#fractionRatioStripped').val(data.fractionBase);
						$('#interestRate').autoNumericSet(data.interestRate);
						$('#interestRateStripped').val(data.interestRate);
						$('#distributionDate').val(data.description);
						var distDate = new Date($('#distributionDate').datepicker('getDate')).getTime();
						var recDate = new Date($('#recordingDate').datepicker('getDate')).getTime();
						if ($('#recordingDate').val()!=''){
							console.log("distribution Date = " +distDate);
							console.log("recording Date = " +recDate);
							if (recDate > distDate){
								$('#distributionDate').addClass("fieldError");
								$('#distributionDateError').html("must be greather or equal than Recording Date");
							} else {
								$('#distributionDate').removeClass("fieldError");
								$('#distributionDateError').html("");
							}
						}
						/*var sourceQuantity = $('#fractionBase').val();
						var targetQuantity = $('#fraction').val();
						var interestRate = $('#interestRate').val();*/
						
						$('#sourceQuantity').attr("disabled", false);
						$('#targetQuantity').attr("disabled", false);
						$("b[id=ratioReq]").html(" *");
						
						//alert(data.isFraction);
						if (data.isFraction == true) {
							$('#isFraction1').attr("checked", true);
							$('#isFraction2').attr("checked", false);
							/*//alert("not null");
							$('#sourceQuantityStripped').val(sourceQuantity);
							$('#targetQuantityStripped').val(targetQuantity);	
							$('#sourceQuantity').autoNumericSet( sourceQuantity, {vMAx:'999999999999.9999999999'}).val();
							$('#targetQuantity').autoNumericSet( targetQuantity, {vMAx:'999999999999.9999999999'}).val();	
							console.debug("sourceQuantity = " + sourceQuantity + " targetQuantity = " + targetQuantity);
							if (($('#sourceQuantityStripped').val() == "") && ($('#targetQuantityStripped').val() == "")) {
								$('#sourceQuantity').attr("disabled", true);
								$('#targetQuantity').attr("disabled", true);
								$("b[id=ratioReq]").html("");
							}*/
						} else {
							//alert("null");
							$('#isFraction1').attr("checked", false);
							$('#isFraction2').attr("checked", true);
							/*$('#sourceQuantity').val(null);
							$('#targetQuantity').val(null);
							$('#sourceQuantityStripped').val($('#sourceQuantity').val());
							$('#targetQuantityStripped').val($('#targetQuantity').val());
							$('#sourceQuantity').attr("disabled", true);
							$('#targetQuantity').attr("disabled", true);
							$("b[id=ratioReq]").html("");*/
						}
						
//						$('#interestRate').val(interestRate);
						
					}
				},
				error: function(data) {
					$('#couponSchedule').addClass('fieldError');
					$('#couponSchedule').val('');
					$('#couponScheduleKey').val('');
					$('#couponScheduleDesc').val('NOT FOUND');
					$('#h_couponScheduleDesc').val('');
					$('#distributionDate').val('');
				}
			},
			description : $('#couponScheduleDesc'),
			filter:$('#securityKey'),
			help:$('#couponScheduleHelp')
		});
		
		// ON FIELDSET TAX
		if (('${mode}'=='entry')&&('${confirming}'!='true')){
			if ($('#taxObject').val()==''){
				$('input[name="corporateAnnouncement.taxable"]').attr("disabled", "disabled");
				$('input[name="corporateAnnouncement.taxable"]').val(false);
				$('#exercisePrice').attr("disabled", "disabled");
				$('#cekBoxTaxApplied').val(false);
				$('#hidCekBoxTaxApplied').val(false);
				$('#announcementLink').attr("disabled", "disabled");
				$('#announcementLinkHelp').attr("disabled", "disabled");
				$('p[id=pExercisePrice] label span').html("");
				$('p[id=pActionTemplateLink] label span').html("");
				$('#securityTypeTarget').attr('disabled','disabled');
				$('#securityTypeTargetHelp').attr('disabled','disabled');
				$('#securityCodeTarget').attr('disabled','disabled');
				$('#securityCodeTargetHelp').attr('disabled','disabled');
			}
		}
		
		if (($('#targetType').val()=='${targetTypeCash}') || ($('#targetType').val()=='${targetTypeSame}')){
			$('#securityTypeTarget').attr('disabled','disabled');
			$('#securityTypeTargetHelp').attr('disabled','disabled');
			$('#securityCodeTarget').attr('disabled','disabled');
			$('#securityCodeTargetHelp').attr('disabled','disabled');
		}
		
		if ($('#announcementLinkKey').val()!=''){
			$('#cekBoxTaxApplied').attr('checked', true);
			$('#hidCekBoxTaxApplied').val(true);
		}
		
		if ($('#announcementLink').val()==''){
			$('p[id=pActionTemplateLink] label span').html("");
		}
		if ($('#hasExercisePrice').val()=='false'){
			$('#exercisePrice').attr('disabled', 'disabled');
			$('p[id=pExercisePrice] label span').html("");
		}
		
		$('#taxable1').add($('#taxable2')).click(function(){
			if ($('#taxable1').is(':checked')){
				$('#taxableObject').val($('#taxableObjectOri').val());
				$('#taxableObjectId').val($('#taxableObjectIdOri').val());
				$('input[name="corporateAnnouncement.taxable"]').val(true);
			}
			
			if ($('#taxable2').is(':checked')){
				$('#taxableObject').val('');
				$('#taxableObjectId').val('');
				$('input[name="corporateAnnouncement.taxable"]').val(false);
			}
		});
		
		$('#announcementLink').lookup({
			list:'@{Pick.announcementLinks()}',
			get:{
				url:'@{Pick.announcementLink()}',
				success: function(data) {
					if (data) {
						$('#announcementLink').removeClass('fieldError');
						$('#announcementLinkKey').val(data.code);
						$('#announcementLinkDesc').val(data.description);
						$('#h_announcementLinkDesc').val(data.description);
						
						
					}	
				},
				error: function(data) {
					$('#announcementLink').addClass('fieldError');
					$('#announcementLinkDesc').val('NOT FOUND');
					$('#announcementLink').val('');
					$('#h_announcementLinkDesc').val('');
				}
			},
			filter:$('#actionCodeKey'),
			description:$('#announcementLinkDesc'),
			help:$('#announcementLinkHelp')
		});
	
		$("#announcementDate").change(function(){
			var idDate1 = "#"+this.id;
			var idDate2 = "#cumDate";
			var date1 = new Date($(idDate1).datepicker('getDate')).getTime();
			var date2 = new Date($(idDate2).datepicker('getDate')).getTime();
			var errorId = idDate2+"Error";
			var errorMsg = "must be greather or equal than Announcement Date";
			
//			if (($(this).val()!='')&&(!$(this).hasClass('fieldError'))&&($(idDate2).val()!='')&&(!$(idDate2).hasClass('fieldError'))){
			if (($(this).val()!='')&&($(idDate2).val()!='')){
				if (date1 > date2) {
					$(idDate2).addClass('fieldError');
					$(errorId).html(errorMsg);
				} else {
					$(idDate2).removeClass('fieldError');
					$(errorId).html('');
				}
				
			}
		});
		
		$("#cumDate").change(function() {
			var idDate1 = "#announcementDate";
			var idDate2 = "#"+this.id;
			var idDate3 = "#exDate";
			var date1 = new Date($(idDate1).datepicker('getDate')).getTime();
			var date2 = new Date($(idDate2).datepicker('getDate')).getTime();
			var date3 = new Date($(idDate3).datepicker('getDate')).getTime();
			var errorId1 = idDate2+"Error";
			var errorId2 = idDate3+"Error";
			var errorMsg1 = "must be greather or equal than Announcement Date";
			var errorMsg2 = "must be greather or equal than Cum Date";
			
//			if (($(this).val()!='')&&(!$(this).hasClass('fieldError'))){
			if ($(this).val()!='') {
//				if (($(idDate1).val()!='')&&(!$(idDate1).hasClass('fieldError'))){
				if ($(idDate1).val()!=''){
					if (date1 > date2) {
						$(idDate2).addClass('fieldError');
						$(errorId1).html(errorMsg1);
					} else {
						$(idDate2).removeClass('fieldError');
						$(errorId1).html('');
						$(idDate3).removeClass('fieldError');
						$(errorId2).html('');
					}
				}
				
				if (($(idDate3).val()!='')){
					if (date2 > date3) {
						$(idDate3).addClass('fieldError');
						$(errorId2).html(errorMsg2);
					} else {
						$(idDate3).removeClass('fieldError');
						$(errorId2).html('');
					}
				}
			}
		});
		
		$("#exDate").change(function() {
			var idDate1 = "#cumDate";
			var idDate2 = "#"+this.id;
			var idDate3 = "#recordingDate";
			var date1 = new Date($(idDate1).datepicker('getDate')).getTime();
			var date2 = new Date($(idDate2).datepicker('getDate')).getTime();
			var date3 = new Date($(idDate3).datepicker('getDate')).getTime();
			var errorId1 = idDate2+"Error";
			var errorId2 = idDate3+"Error";
			var errorMsg1 = "must be greather or equal than Cum Date";
			var errorMsg2 = "must be greather or equal than Ex Date";
			
			if ($(this).val()!='') {

				if ($(idDate1).val()!='') {
					if (date1 > date2) {
						$(idDate2).addClass('fieldError');
						$(errorId1).html(errorMsg1);
					} else {
						$(idDate2).removeClass('fieldError');
						$(errorId1).html('');
						$(idDate3).removeClass('fieldError');
						$(errorId2).html('');
					}
				}
				
//				if (($(idDate3).val()!='')&&(!$(idDate3).hasClass('fieldError'))){
				if ($(idDate3).val()!='') {
					if (date2 > date3) {
						$(idDate3).addClass('fieldError');
						$(errorId2).html(errorMsg2);
					} else {
						$(idDate3).removeClass('fieldError');
						$(errorId2).html('');
					}
				}
			}
		
		});
		
		$("#recordingDate").change(function() {
			var idDate1 = "#exDate";
			var idDate2 = "#"+this.id;
			var idDate3 = "#distributionDate";
			var date1 = new Date($(idDate1).datepicker('getDate')).getTime();
			var date2 = new Date($(idDate2).datepicker('getDate')).getTime();
			var date3 = new Date($(idDate3).datepicker('getDate')).getTime();
			var errorId1 = idDate2+"Error";
			var errorId2 = idDate3+"Error";
			var errorMsg1 = "must be greather or equal than Ex Date";
			var errorMsg2 = "must be greather or equal than Recording Date";
			
//			if (($(this).val()!='')&&(!$(this).hasClass('fieldError'))){
			if ($(this).val()!=''){
//				if (($(idDate1).val()!='')&&(!$(idDate1).hasClass('fieldError'))){
				if ($(idDate1).val()!='') {
					if (date1 > date2) {
						$(idDate2).addClass('fieldError');
						$(errorId1).html(errorMsg1);
					} else {
						$(idDate2).removeClass('fieldError');
						$(errorId1).html('');
						$(idDate3).removeClass('fieldError');
						$(errorId2).html('');
					}
				}
				
//				if (($(idDate3).val()!='')&&(!$(idDate3).hasClass('fieldError'))){
				if ($(idDate3).val()!='') {
					if (date2 > date3) {
						$(idDate3).addClass('fieldError');
						$(errorId2).html(errorMsg2);
					} else {
						$(idDate3).removeClass('fieldError');
						$(errorId2).html('');
					}
				}
			}
		});
		
		$("#distributionDate").change(function() {
			var idDate1 = "#recordingDate";
			var idDate2 = "#"+this.id;
			var date1 = new Date($(idDate1).datepicker('getDate')).getTime();
			var date2 = new Date($(idDate2).datepicker('getDate')).getTime();
			var errorId = idDate2+"Error";
			var errorMsg = "must be greather or equal than Distribution Date";
			
//			if (($(this).val()!='')&&(!$(this).hasClass('fieldError'))&&($(idDate2).val()!='')&&(!$(idDate2).hasClass('fieldError'))){
			if (($(this).val()!='')&&($(idDate2).val()!='')){
				if (date1 > date2) {
					$(idDate2).addClass('fieldError');
					$(errorId).html(errorMsg);
				} else {
					$(idDate2).removeClass('fieldError');
					$(errorId).html('');
				}
				
			}
		});
		
		$('#securityTypeTarget').change(function(){
			if ($(this).val()==''){
				$('#securityType').val('');
				$('#securityTypeDesc').val('');
				$('#h_securityTypeDesc').val('');
				$('#securityTypeTargetHidden').val('');
				$('#securityTypeTargetDescHidden').val('');
				
				$('#securityCodeTargetKey').val('');
				$('#securityCodeTarget').val('');
				$('#securityCodeTargetDesc').val('');
				$('#h_securityCodeTargetDesc').val('');
				$('#securityCodeTargetHidden').val('');
				$('#securityCodeTargetDescHidden').val('');
				
				$(this).removeClass('fieldError');
				$('#securityCodeTarget').removeClass('fieldError');
				$('#errorSecTypeAndCode').html('');
				
			}
		});
		
		$('#securityTypeTarget').lookup({
			list:'@{Pick.securityTypes()}',
			get:{
				url:'@{Pick.securityType()}',
				success: function(data) {
					if (data) {
						$('#securityTypeTarget').removeClass('fieldError');
						$('#securityTypeTarget').val(data.code);
						$('#securityTypeTargetDesc').val(data.description);
						$('#h_securityTypeTargetDesc').val(data.description);
						$('#securityTypeTargetHidden').val(data.code);
						$('#securityTypeTargetDescHidden').val(data.description);
						
						$('#securityCodeTargetKey').val('');
						$('#securityCodeTarget').val('');
						$('#securityCodeTargetDesc').val('');
						$('#h_securityCodeTargetDesc').val('');
						$('#securityCodeTargetHidden').val('');
						$('#securityCodeTargetDescHidden').val('');
					}	
				},
				error: function(data) {
					$('#securityTypeTarget').addClass('fieldError');
					$('#securityTypeTargetDesc').val('NOT FOUND');
					$('#securityTypeTarget').val('');
					$('#h_securityTypeTargetDesc').val('');
					$('#securityTypeTargetHidden').val('');
					$('#securityTypeTargetDescHidden').val('');
				}
			},
			description:$('#securityTypeTargetDesc'),
			help:$('#securityTypeTargetHelp')
		});
		
		$('#securityCodeTarget').change(function(){
			if ($(this).val()==''){
				$('#securityCodeTargetKey').val('');
				$('#securityCodeTarget').val('');
				$('#securityCodeTargetDesc').val('');
				$('#h_securityCodeTargetDesc').val('');
				$('#securityCodeTargetHidden').val('');
				$('#securityCodeTargetDescHidden').val('');
				
				$(this).removeClass('fieldError');
				if (!$('#securityTypeTarget').hasClass('fieldError')){
					$('#errorSecTypeAndCode').html('');
				}
			}
		});
		
		$('#securityCodeTarget').lookup({
			list:'@{Pick.securities()}',
			get: {
				url:'@{Pick.security()}',
				success: function(data) {
					if (data) {
						$('#securityCodeTarget').removeClass('fieldError');
						$('#securityCodeTargetKey').val(data.code);
						$('#securityCodeTargetDesc').val(data.description);
						$('#h_securityCodeTargetDesc').val(data.description);
						$('#securityCodeTargetHidden').val($('#securityCodeTarget').val());
						$('#securityCodeTargetDescHidden').val(data.description);
						
						if ((data.code == $('#securityKey').val()) && ($('#securityTypeSource').val() == $('#securityTypeTarget').val())){
							$('#securityCodeTarget').addClass('fieldError');
							$('#errorSecTypeAndCode').html('Target can not same with Source');
						} else {
							$('#securityCodeTarget').removeClass('fieldError');
							$('#errorSecTypeAndCode').html('');
						}
					}
				},
				error: function(data) {
					$('#securityCodeTarget').addClass('fieldError');
					$('#securityCodeTargetDesc').val('NOT FOUND');
					$('#securityCodeTargetKey').val('');
					$('#securityCodeTarget').val('');
					$('#h_securityCodeTargetDesc').val('');
					$('#securityCodeTargetHidden').val('');
					$('#securityCodeTargetDescHidden').val('');
				}
			},
			description:$('#securityCodeTargetDesc'),
			filter:$('#securityTypeTarget'),
			help:$('#securityCodeTargetHelp')
		});

		// validasi untuk rounding type tidak boleh lebih dari 6
		$("#roundingValue").blur(function(){
			var el = $(this);
			var roundingValue = $('#roundingValueStripped').val();
					
			el.removeClass('fieldError');
			
			$("#errmsg").html("");
			if ((roundingValue > 6)){
				el.addClass('fieldError');
				$("#errmsg").html("value 0 - 6").show();
			}
			
		});	
		
		
		
});
	function DefaultCouponSchedule(){
		$('#couponSchedule').attr('disabled','disabled');
		$('#couponScheduleHelp').attr('disabled','disabled');
		$('p[id="pCouponSchedule"] label span').html('');
		$('#couponSchedule').removeClass('fieldError');
		$('#couponSchedule').val('');
		$('#couponScheduleKey').val('');
		$('#couponScheduleDesc').val('');
		$('#h_couponScheduleDesc').val('');
		$('#distributionDate').val('');
		$('#sourceQuantityRow').attr('disabled', 'disabled');
		$('#sourceQuantityRow').val('');
		$('#sourceQuantityRowStripped').val('');
		$('#targetQuantityRow').attr('disabled', 'disabled');
		$('#targetQuantityRow').val('');
		$('#targetQuantityRowStripped').val('');
		$('p[id=pSourceTargetQty] label span').html('');
	}
	
	function DefaultScreenRatioTarget(){
		$('#securityTypeTarget').attr('disabled','disabled');
		$('#securityTypeTargetHelp').attr('disabled','disabled');
		$('#securityCodeTarget').attr('disabled','disabled');
		$('#securityCodeTargetHelp').attr('disabled','disabled');
		
		$('#securityTypeTarget').val('');
		$('#securityTypeTargetDesc').val('');
		$('#securityTypeTargetHidden').val('');
		$('#securityTypeTargetDescHidden').val('');
		
		$('#securityCodeTargetKey').val('');
		$('#securityCodeTarget').val('');
		$('#securityCodeTargetDesc').val('');
		$('#securityCodeTargetHidden').val('');
		$('#securityCodeTargetDescHidden').val('');
		
		$('#errorSecTypeAndCode').html('');
		$('#securityTypeTarget').removeClass('fieldError');
		$('#securityCodeTarget').removeClass('fieldError');
	}
	
	function DefaultScreenTaxable(){
		$('input[name="corporateAnnouncement.taxable"]').attr("disabled", "disabled");
		$('input[name="corporateAnnouncement.taxable"]').val(false);
		$('#taxable1').attr('checked', false);
		$('#taxable2').attr('checked', true);
		$('#taxableObject').val('');
		$('#exercisePrice').attr("disabled", "disabled");
		$('#exercisePrice').val("");
		$('#cekBoxTaxApplied').val(false);
		$('#cekBoxTaxApplied').attr("checked", false);
		$('#announcementLink').attr("disabled", "disabled");
		$('#announcementLinkHelp').attr("disabled", "disabled");
		$('#announcementLink').val('');
		$('#announcementLinkKey').val('');
		$('#announcementLinkDesc').val('');
		$('p[id=pExercisePrice] label span').html("");
		$('p[id=pActionTemplateLink] label span').html("");
		
	}
	
	function DefaultInfoCoupon(){
		$('#fractionAmount').val('');
		$('#fractionAmountStripped').val('');
		$('#fractionRatio').val('');
		$('#fractionRatioStripped').val('');
		$('#interestRate').val('');
		$('#interestRateStripped').val('');
		$('#isFraction1').attr("checked", false);
		$('#isFraction2').attr("checked", true);
		$('#distributionDate').val('');
	}