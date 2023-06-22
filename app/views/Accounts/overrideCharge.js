$(function() {
	
	var closeDialogMessage = function() {
		$("#dialog-message").dialog("close");
		return false;
	};
	
	var closeDialogMessagesOnDefaultChange = function() {
		$("#dialog-message").dialog("close");
		
		if($("input[name='account.dcProfil']").val()==false || $("input[name='account.dcProfil']").val()=='false'){
			
			$("input[name=dcProfil]")[1].checked = true;
		}else{
			
			$("input[name=dcProfil]")[0].checked = true;
		}
		return false;
	};
	
	var closeDialogMessageOnIsChargedDefaultChange = function() {
		$("#dialog-message").dialog("close");

		$('#tabs').tabs('enable', 4);
		var vChargeProfileDefault = null;
		$.get('@{getChargeProfileDefault()}?', function(data) {
			checkRedirect(data);
			vChargeProfileDefault = data.name;
		});
		
		if (vChargeProfileDefault === $('#chargeProfile').val()) {
			$("input[name=dcProfil]")[0].checked = true;
		} else {
			$("input[name=dcProfil]")[1].checked = true;
		}
		
		return false;
	};
	
	var changeProfilOnOverwrite = function(){
		
		$("#dialog-message").dialog("close");
		
		$('#tabs').tabs('enable', 4);
		if($("input[name=dcProfil]")[0].checked == true){
			
			$('#chargeProfile').attr("disabled", "disabled");
			$('#chargeProfileHelp').attr("disabled", "disabled");
			$.get('@{getChargeProfileDefault()}?', function(data) {
				checkRedirect(data);
				$('#chargeProfileKey').val(data.chargeProfileKey);
				$('#chargeProfile').val(data.name);
				$('#chargeProfileDesc').val(data.description);
			});
			
			$('#lChargeProf').val($('#chargeProfile').val());
			$.get('@{getChargeProfile()}?key='+$("#chargeProfileKey").val(), function(data) {
				checkRedirect(data);
				chargeProfile1(data);
			});
			$.get('@{getChargeMaster()}?key='+$("#chargeProfileKey").val(), function(data) {
				checkRedirect(data);
				chargeProfile(data);
			});
			$.get('@{getChargeOverides()}?key='+$("#chargeProfileKey").val(), function(data) {
				checkRedirect(data);
				overwriteCharge(data);
			});
		}else{
			
			$('#chargeProfile').attr("disabled", false);
			$('#chargeProfileHelp').attr("disabled", false);
			$('#chargeProfile').val('');
			$('#chargeProfileKey').val('');
			$('#chargeProfileDesc').val('');
			
			$('#lChargeProf').val($('#chargeProfile').val());
			$.get('@{getChargeProfile()}', function(data) {
				checkRedirect(data);
				chargeProfile1(data);
			});
			$.get('@{getChargeMaster()}', function(data) {
				checkRedirect(data);
				chargeProfile(data);
			});
			$.get('@{getChargeOverides()}', function(data) {
				checkRedirect(data);
				overwriteCharge(data);
			});
		}
		return false;
	};
	
	$('#isCharge').change(function(){
		$("#isCharge").attr('checked', $('#isCharge').is(':checked'));
		$("#isChargeHidden").val($('#isCharge').is(':checked'));
		
		if($('#chargeProfile').val()==null || $('#chargeProfile').val() == ''){
			$('#errChargeProfile').html('Required');
			$("#isCharge").attr('checked',false);
		}else{
			$('#errChargeProfile').html('');
			
			$("input[name='account.isCharge']").val($("input[name='isCharge']:checked").val());
			if($('#isCharge').is(':checked')){
				$('#tabs').tabs('enable', 4);
				
				$.ajax({
					url: '@{getChargeProfile()}?key='+$("#chargeProfileKey").val(),
					success: function(datas) {
						checkRedirect(datas);
						chargeProfile1(datas);
					}
				});
				$.ajax({
					url: '@{getChargeMaster()}?key='+$("#chargeProfileKey").val(),
					success: function(datas) {
						checkRedirect(datas);
						chargeProfile(datas);
					}
				});
				
				$.ajax({
					url: '@{getChargeOverides()}?key=${account?.custodyAccountKey}',
					success: function(datas) {
						checkRedirect(datas);
						overwriteCharge(datas);
					}
				});
				$('#lChargeProf').val($('#chargeProfile').val());
			}else{
				$.ajax({
					url: '@{getChargeOverides()}',
					success: function(datas) {
						checkRedirect(datas);
						overwriteCharge(datas);
					}
				});
				$('#tabs').tabs('disable', 4);
			}
		}
		
	});
	
	var arrChargeProfileKey = [$('#chargeProfileKey').val()];
	
	$('#chargeProfile').lookup({
		list:'@{Pick.chargeProfiles()}',
		get:{
			url:'@{Pick.chargeProfile()}',
			success: function(data){
				$('#chargeProfile').removeClass('fieldError');
				$('#errChargeProfile').html('');
				
				arrChargeProfileKey.push(data.code);
				
				var changeProfilOnNew = function(){
					$("#dialog-message").dialog("close");
					$('#chargeProfileKey').val(data.code);
					$('#chargeProfile').val(data.name);
					$('#chargeProfileDesc').val(data.description);
					$('#h_chargeProfileDesc').val(data.description);
					
					$("#dialog-message").dialog("close");
					
					$('#lChargeProf').val($('#chargeProfile').val());
					$.get('@{getChargeProfile()}?key='+$("#chargeProfileKey").val(), function(data) {
						checkRedirect(data);
						chargeProfile1(data);
					});
					$.get('@{getChargeMaster()}?key='+$("#chargeProfileKey").val(), function(data) {
						checkRedirect(data);
						chargeProfile(data);
					});
					$.get('@{getChargeOverides()}?key='+$("#chargeProfileKey").val(), function(data) {
						checkRedirect(data);
						overwriteCharge(data);
					});
					return false;
				};
				
				var closeDialogMessageOnNew = function() {
					$("#dialog-message").dialog("close");
					
					var prevChargeProfile = "";
					if (arrChargeProfileKey.length >= 2) {
						prevChargeProfile = arrChargeProfileKey[arrChargeProfileKey.length - 2];
					} else {
						prevChargeProfile = arrChargeProfileKey[0];
					}
					$.get('@{getChargeProfileEntry}', {'key':prevChargeProfile}, function(vdata) {
						checkRedirect(vdata);
						$('#chargeProfileKey').val(vdata.code);
						$('#chargeProfile').val(vdata.name);
						$('#chargeProfileDesc').val(vdata.description);
						$('#h_chargeProfileDesc').val(vdata.description);
					});
					
					var vChargeProfileDefault = null;
					$.get('@{getChargeProfileDefault()}', function(data1) {
						checkRedirect(data1);
						vChargeProfileDefault = data1.name;
					});
					if (vChargeProfileDefault === $('#chargeProfile').val()) {
						$("input[name=dcProfil]")[0].checked = true;
					} else {
						$("input[name=dcProfil]")[1].checked = true;
					}

					if (arrChargeProfileKey.length > 1) {
						arrChargeProfileKey.pop();
					}
					
					return false;
				};
				
				if (((arrChargeProfileKey[arrChargeProfileKey.length - 2] !== "") && (arrChargeProfileKey[arrChargeProfileKey.length - 2] != undefined)) && ((arrChargeProfileKey[arrChargeProfileKey.length - 1] !== "") && (arrChargeProfileKey[arrChargeProfileKey.length - 1] != undefined)) && (arrChargeProfileKey[arrChargeProfileKey.length - 2] !== arrChargeProfileKey[arrChargeProfileKey.length - 1]) && (($('#isCharge').is(':checked')))) {
					messageAlertYesNo("This changes will reset your overwrite charges, do you want to continue ?", "ui-icon ui-icon-notice", "Confirmation Message", changeProfilOnNew, closeDialogMessageOnNew);
					return false;
				}
				
				if('${mode}'=='edit'){
					$('#chargeProfileKey').val('${account?.chargeProfile?.chargeProfileKey}');
					$('#chargeProfile').val('${account?.chargeProfile?.name}');
					$('#chargeProfileDesc').val('${account?.chargeProfile?.description}');
					$('#h_chargeProfileDesc').val('${account?.chargeProfile?.description}');
					if(data.code == '${account?.chargeProfile?.chargeProfileKey}'){
						
						$('#chargeProfileKey').val('${account?.chargeProfile?.chargeProfileKey}');
						$('#chargeProfile').val('${account?.chargeProfile?.name}');
						$('#chargeProfileDesc').val('${account?.chargeProfile?.description}');
						$('#h_chargeProfileDesc').val('${account?.chargeProfile?.description}');
					}else{
						
						var cekDefault = '${account?.chargeProfile?.isDefault}';
						if((cekDefault == 'true') || (cekDefault == true) || (cekDefault == null) || cekDefault == ''){
							$('#chargeProfileKey').val(data.code);
							$('#chargeProfile').val(data.name);
							$('#chargeProfileDesc').val(data.description);
							$('#h_chargeProfileDesc').val(data.description);
						}else{
							var changeProfil = function(){
								
								$("#dialog-message").dialog("close");
								$('#chargeProfileKey').val(data.code);
								$('#chargeProfile').val(data.name);
								$('#chargeProfileDesc').val(data.description);
								$('#h_chargeProfileDesc').val(data.description);
								
								$("#isCharge").attr('checked',false);
								$("#isChargeHidden").val(false);
								
								$("#dialog-message").dialog("close");
								$('#tabs').tabs('disable', 4);
								$.ajax({
									url: '@{getChargeOverides()}',
									success: function(datas) {
										checkRedirect(datas);
										overwriteCharge(datas);
									}
								});
								return false;
							}
							messageAlertYesNo("This changes will reset your overwrite charges, do you want to continue ?", "ui-icon ui-icon-notice", "Confirmation Message", changeProfil, closeDialogMessage);

						}
						
					}
					
				}else{
					$('#chargeProfileKey').val(data.code);
					$('#chargeProfile').val(data.name);
					$('#chargeProfileDesc').val(data.description);
					$('#h_chargeProfileDesc').val(data.description);
					
					if($('#isCharge').is(':checked')){
						$('#lChargeProf').val($('#chargeProfile').val());
						$.get('@{getChargeProfile()}?key='+$("#chargeProfileKey").val(), function(data) {
							checkRedirect(data);
							chargeProfile1(data);
						});
						$.get('@{getChargeMaster()}?key='+$("#chargeProfileKey").val(), function(data) {
							checkRedirect(data);
							chargeProfile(data);
						});
						$.get('@{getChargeOverides()}?key='+$("#chargeProfileKey").val(), function(data) {
							checkRedirect(data);
							overwriteCharge(data);
						});
					}
				}
			},
			error: function(data){
				$('#chargeProfile').addClass('fieldError');
				$('#chargeProfileKey').val('');
				$('#chargeProfile').val('');
				$('#chargeProfileDesc').val('NOT FOUND');
				$('#h_chargeProfileDesc').val('');
				
			}
		},
		description:$('#chargeProfileDesc'),
		help:$('#chargeProfileHelp')
	});
	
	$('input[name=dcProfil]').click(function(){
		$("input[name='account.dcProfil']").val($("input[name='dcProfil']:checked").val());
		
		if($('#isCharge').is(':checked')){
			if($("input[name=dcProfil]")[0].checked == true){
				messageAlertYesNo("This changes will reset your overwrite charges, do you want to continue ?", "ui-icon ui-icon-notice", "Confirmation Message", changeProfilOnOverwrite, closeDialogMessageOnIsChargedDefaultChange);
			} else {
				messageAlertYesNo("This changes will reset your overwrite charges, do you want to continue ?", "ui-icon ui-icon-notice", "Confirmation Message", changeProfilOnOverwrite, closeDialogMessageOnIsChargedDefaultChange);
			}
		}
		
		else {
			if('${mode}'=='edit'){
				$('#tabs').tabs('disable', 4);
				
				if($("input[name=dcProfil]")[1].checked == true){
					$('#chargeProfile').attr("disabled", false);
					$('#chargeProfileHelp').attr("disabled", false);
					
					$('#chargeProfileKey').val('${account?.chargeProfile?.chargeProfileKey}');
					$('#chargeProfile').val('${account?.chargeProfile?.name}');
					$('#chargeProfileDesc').val('${account?.chargeProfile?.description}');
					$('#h_chargeProfileDesc').val('${account?.chargeProfile?.description}');
					
				}else{
					var cekDefault = '${account?.chargeProfile?.isDefault}';
					if((cekDefault == 'true') || (cekDefault == true) || (cekDefault == null) || cekDefault == ''){
						$('#chargeProfile').attr("disabled", "disabled");
						$('#chargeProfileHelp').attr("disabled", "disabled");
						$.ajax({
							url: '@{getChargeProfileDefault()}',
							success: function(data) {
								checkRedirect(data);
								$('#chargeProfileKey').val(data.chargeProfileKey);
								$('#chargeProfile').val(data.name);
								$('#chargeProfileDesc').val(data.description);
							}
						});
						
						$.ajax({
							url: '@{getChargeOverides()}',
							success: function(datas) {
								checkRedirect(datas);
								overwriteCharge(datas);
							}
						});
						
					}else{
						var changeProfil = function(){
							
							$("#dialog-message").dialog("close");
							
							if($("input[name=dcProfil]")[0].checked == true){
								
								$('#chargeProfile').attr("disabled", "disabled");
								$('#chargeProfileHelp').attr("disabled", "disabled");
								$.ajax({
									url: '@{getChargeProfileDefault()}',
									success: function(data) {
										checkRedirect(data);
										$('#chargeProfileKey').val(data.chargeProfileKey);
										$('#chargeProfile').val(data.name);
										$('#chargeProfileDesc').val(data.description);
									}
								});
								
							}else{
								
								$('#chargeProfile').attr("disabled", false);
								$('#chargeProfileHelp').attr("disabled", false);
								$('#chargeProfile').val('');
								$('#chargeProfileKey').val('');
								$('#chargeProfileDesc').val('');
							}
							
							$.ajax({
								url: '@{getChargeOverides()}',
								success: function(datas) {
									checkRedirect(datas);
									overwriteCharge(datas);
								}
							});
							return false;
						}
						messageAlertYesNo("This changes will reset your overwrite charges, do you want to continue ?", "ui-icon ui-icon-notice", "Confirmation Message", changeProfil, closeDialogMessagesOnDefaultChange);

					}

				}
				
			}else{
				// for '${mode}' !== 'edit'
				$('#tabs').tabs('disable', 4);
				
				if($("input[name=dcProfil]")[0].checked == true){
					
					$('#chargeProfile').attr("disabled", "disabled");
					$('#chargeProfileHelp').attr("disabled", "disabled");
					$.ajax({
						url: '@{getChargeProfileDefault()}',
						success: function(data) {
							checkRedirect(data);
							$('#chargeProfileKey').val(data.chargeProfileKey);
							$('#chargeProfile').val(data.name);
							$('#chargeProfileDesc').val(data.description);
						}
					});
					
				}else{

					$('#chargeProfile').attr("disabled", false);
					$('#chargeProfileHelp').attr("disabled", false);
					$('#chargeProfile').val('');
					$('#chargeProfileKey').val('');
					$('#chargeProfileDesc').val('');
				}
				$.ajax({
					url: '@{getChargeOverides()}',
					success: function(datas) {
						checkRedirect(datas);
						overwriteCharge(datas);
					}
				});
			}
		}
	});
		
		$("#detailChargeOverrideTier #tierChargeOverrideForm #percentage").css("display","none");
		var tableTier = null;
		var tableOverwriteCharge = null;
		var tableChargeProfile1 = null;
		var tableChargeProfile = null;
// JSON chargeOverrides

		var chargeProfiles;
		if ('${chargeProfiles}' != null) {
			chargeProfiles = ${chargeProfiles.raw()};
		} else {
			chargeProfiles = new Array();
		}
			
		chargeProfile1(chargeProfiles);
		
		var chargeMasterss;
		if('${chargeMasterss}' != null){
			
			chargeMasterss = ${chargeMasterss.raw()};
		}else{
			chargeMasterss = new Array();
		}
		
		chargeProfile(chargeMasterss);
		
		var chargeOverrides;
		if ('${chargeOverrides}' != null) {
			chargeOverrides = ${chargeOverrides.raw()};
		}else {
			chargeOverrides = new Array();
		}
		
		overwriteCharge(chargeOverrides);
		
		
		
		$('.pane').css('height','600' );
		$('.buttons input:button').button();
		
// END OF SECTION

// 	START CHARGE PROFILE

		function chargeProfile1(data){
	
			if( tableChargeProfile1 != null ){
				tableChargeProfile1.fnDestroy();
			}
			
			tableChargeProfile1 = 
				$('#gridChargeProfile1').dataTable({
					aaData: data,
					aoColumns: [ {bVisible:false,
									fnRender: function(obj){
				            		var controls;
				            		if(obj.aData.chargeMaster == null){
				            			controls = "";
				            		}else{
				            			controls = obj.aData.chargeMaster.chargeKey;
				            		}
				            		
				            		
				            		return controls;
				            		}
								 }, 
					             {bVisible:false,
									fnRender: function(obj){
				            		var controls;
				            		if(obj.aData.chargeMaster.tieringType == null){
				            			controls = "";
				            		}else{
				            			controls = obj.aData.chargeMaster.tieringType.lookupId;
				            		}
				            		
				            		
				            		return controls;
				            		}
								 },
								  {
									 fnRender: function(obj){
					            		var controls;
					            		controls = obj.aData.chargeMaster.chargeCode;
					            		
					            		return controls;
					            		}
								  },
								  {
									 fnRender: function(obj){
					            		var controls;
					            		controls = obj.aData.chargeMaster.description;
					            		
					            		return controls;
					            		}
								  },
								  {bVisible:false,
									  fnRender: function(obj){
					            		var controls;
					            		controls = obj.aData.chargeMaster.chargeType.lookupId;
					            		
					            		return controls;
					            		}
								  },
								  {bVisible:false,
									  fnRender: function(obj){
					            		var controls;
					            		if(obj.aData.chargeMaster.valueType == null){
					            			controls = "";
					            		}else{
					            			controls = obj.aData.chargeMaster.valueType.lookupId;	
					            		}
					            		
					            		
					            		return controls;
					            		}
								  },
								  {bVisible:false,
									  fnRender: function(obj){
					            		var controls;
					            		controls = obj.aData.chargeMaster.chargeCategory.lookupId;
					            		
					            		return controls;
					            		}  
								  },
								  {bVisible:false,
									  fnRender: function(obj){
					            		var controls;
					            		controls = obj.aData.chargeMaster.invoiceCharge.lookupId;
					            		
					            		return controls;
					            		}  
								  },
								  {
									  fnRender: function(obj){
					            		var controls;
					            		if(obj.aData.securityClass == null ){
					            			controls = '*ALL';
					            		}else if(obj.aData.securityClass.lookupCode == null || obj.aData.securityClass.lookupCode == ''){
					            			controls = '*ALL';
					            		}else{
					            			controls = obj.aData.securityClass.lookupCode;
					            		}
					            		
					            		return controls;
					            		}
								  },
								  {
									  fnRender: function(obj){
					            		var controls;
					            		if(obj.aData.securityClass == null ){
					            			controls = '*ALL';
					            		}else if(obj.aData.securityClass.securityType == null || obj.aData.securityClass.securityType == ''){
					            			controls = '*ALL';
					            		}else{
					            			controls = obj.aData.securityClass.securityType;
					            		}
					            		
					            		return controls;
					            		}
								  },
								  {
									  fnRender: function(obj){
					            		var controls;
					            		if(obj.aData.transactionTemplate == null ){
					            			controls = '*ALL';
					            		}else if(obj.aData.transactionTemplate.transactionCode == null || obj.aData.transactionTemplate.transactionCode == ''){
					            			controls = '*ALL';
					            		}else{
					            			controls = obj.aData.transactionTemplate.transactionCode;
					            		}
					            		
					            		return controls;
					            		} 
								  },
								  {
									  fnRender: function(obj){
						            		var controls;
						            		if(obj.aData.security == null ){
						            			controls = '*ALL';
						            		}else if(obj.aData.security.securityId == null || obj.aData.security.securityId == ''){
						            			controls = '*ALL';
						            		}else{
						            			controls = obj.aData.security.securityId;
						            		}
						            		
						            		return controls;
						            		}
								  },
								  
								  {bVisible:false,
									  fnRender: function(obj){
					            		var controls;
					            		controls = obj.aData.chargeItemKey;
					            		
					            		return controls;
					            		}  
								  }
					             
								],
					aaSorting:[[2,'asc']],
		        	bAutoWidth: false,											
		        	bFilter: false,
		        	bInfo: false,
		        	bJQueryUI: true,
		        	bDestroy:true,
		        	sScrollX:'100%',
		        	//bRetrieve:true,															
		        	bPaginate: false,
		        	bSearch: false,
		        	bLengthChange: false,
		        	isDisplayLength:10                						
			});
		}
		
		


// 	START CHARGE PROFILE
		function chargeProfile(data){
			
			if( tableChargeProfile != null ){
				tableChargeProfile.fnDestroy();
			}
			
			tableChargeProfile = 
				$('#gridChargeProfile').dataTable({
					aaData: data,
					aoColumns: [ {bVisible:false,
								  fnRender: function(obj){
					            		var controls;
					            		controls = obj.aData.chargeKey;
					            		
					            		return controls;
					            		}  
								  }, 
								  {bVisible:false,
								  fnRender: function(obj){
					            		var controls;
					            		if(obj.aData.tieringType == null){
					            			controls = "";
					            		}else{
					            			controls = obj.aData.tieringType.lookupId;
					            		}
					            		
					            		
					            		return controls;
					            		}  
								  },
					             //	null,
								  {
								  fnRender: function(obj){
					            		var controls;
					            		controls = obj.aData.chargeCode;
					            		
					            		return controls;
					            		}  
								  },
								  {
								  fnRender: function(obj){
				            		var controls;
				            		controls = obj.aData.description;
				            		
				            		return controls;
				            		}  
								  },
								  {bVisible:false,
								  fnRender: function(obj){
					            		var controls;
					            		controls = obj.aData.chargeType.lookupId;
					            		
					            		return controls;
					            		}  
								  },
								  {bVisible:false,
								  fnRender: function(obj){
					            		var controls;
					            		if(obj.aData.valueType == null){
					            			controls = "";
					            		}else{
					            			controls = obj.aData.valueType.lookupId;
					            		}
					            		
					            		
					            		return controls;
					            		}  
								  },
								  {bVisible:false,
								  fnRender: function(obj){
					            		var controls;
					            		controls = obj.aData.chargeCategory.lookupId;
					            		
					            		return controls;
					            		}  
								  },
								  {bVisible:false,
									  fnRender: function(obj){
						            		var controls;
						            		controls = obj.aData.invoiceCharge.lookupId;
						            		
						            		return controls;
						            		}  
									  },{bVisible:false,
										  fnRender: function(obj){
							            		var controls;
							            		controls = obj.aData.chargeFrequency.lookupId;
							            		
							            		return controls;
							            		}  
										  },
								  {
	 								 fnRender: function(obj) {
								 			var controls;
								 			
								 			if('${isChargeProfile}' == 'true') {
								 				
								 				controls = '<center><input id="overrideButton" name="overrideButton['+obj.iDataRow+']" type="button" value="Overwrite" #{if readOnly}disabled="disabled"#{/if} /></center>';
								 			} else {
								 				controls = '<center><input id="overrideButton" name="overrideButton['+obj.iDataRow+']" type="button" value="Overwrite" disabled="disabled" /></center>';
								 			}	
								 			return controls;
									  }
								 }
					             
								],
					aaSorting:[[2,'asc']],
		        	bAutoWidth: false,											
		        	bFilter: false,
		        	bInfo: false,
		        	bJQueryUI: true,
		        	bDestroy:true,
		        	sScrollX:'100%',
		        	//bRetrieve:true,															
		        	bPaginate: false,
		        	bSearch: false,
		        	bLengthChange: false,
		        	isDisplayLength:10                						
			});
			$('#gridChargeProfile').removeAttr('style');
			$('#gridChargeProfile tbody tr td').die('click');
			$('#gridChargeProfile tbody tr td').live('click', function(){
				var pos = tableChargeProfile.fnGetPosition(this);
				// if empty
				if( pos == null ){
					return;
				}
				var detail=$('<div><div id="loading"><img src="@@{'/public/images/loading2.gif'}" style="margin: 10px" /></div></div>');
				if (pos[1] != 2) {
					var cell = tableChargeProfile.fnGetData(this.parentNode);
					//var detail=$('<Form id="tes" class="form"></form>');
					detail.dialog({
						autoOpen:false,
						height:580,
						width:925,
						modal:true,
						//closeOnEscape: false,
						resizable : false,
						title:'View Charge Master',
						buttons: {
							"Close": function() {
								//$(this).html('');
								//detail.dialog('close');
								detail.remove();
								detail.dialog('close');
								//detail.dialog('destroy');
								//return true;
							}
						}			
					});
					
					detail.dialog('open');
					detail.css('overflow','hidden');
					detail.load("@{ChargeMasters.viewForChargeOverride()}/"+ cell[0] + "?param=override");
					$('.ui-widget-overlay').css('height',$('body').height());
				}
				$(".ui-icon-closethick").click(function(){
					detail.remove();
				})
				$(document).keyup(function(e) {
					  if (e.keyCode == 27) { 
						  detail.remove();
					  }   
				});

		    });
		}
		 
		
		//var cell = tableChargeProfile.fnGetData(row[0]);
		//if (cell[5] == "CHARGE_TYPE-M") {
		//	$("#gridChargeProfile tbody tr #overrideButton").button('disable');
		//}
		
		$("#gridChargeProfile tbody tr #overrideButton").live('click', function() {
			var row = $(this).parents('tr');
			var rowNumber = tableChargeProfile.fnGetPosition(row[0]);
			var cell = tableChargeProfile.fnGetData(row[0]);
			var accountStatus = false;
			
			if(('${account?.getRecordStatus()}' != "A ") && ('${mode}'=='edit')){
				accountStatus = true;
				$("#accountStatusError").html("* Approval Account needed for Account ${account?.name} ").show();
			}
			
			if(!(accountStatus)) {
				$("#accountStatusError").html("");
				$("#detailOverride").dialog('open');
				$('.ui-widget-overlay').css('height',$('body').height());
				$("#detailOverride :text").val(""); 
				$(".doc").val('');
				$("#detailOverride").find("span[id*='Error']").html("");
				
				$("#detailOverride #chargeOverrideForm #minimumValue").attr('disabled',false);
				$("#detailOverride #chargeOverrideForm #maximumValue").attr('disabled',false);
				$("#detailOverride #chargeOverrideForm #isChecked").attr('disabled',false);
				
				if(cell[8] == "CHARGE_FREQUENCY-M")
				{
					$.get('@{Pick.taxMaster}', {'code':'NOT'}, function(data) {
						checkRedirect(data);
						$('#detailOverride #chargeOverrideForm #overrideTaxCode').val(data.taxCode);
						$('#detailOverride #chargeOverrideForm #overrideTaxKey').val(data.code);
						$('#detailOverride #chargeOverrideForm #overrideTaxName').val(data.description);
					});
					$("#detailOverride #chargeOverrideForm #overrideTaxCode").attr('disabled','disabled');
					$("#detailOverride #chargeOverrideForm #overrideTaxCodeHelp").attr('disabled','disabled');
				}else{
					$('#detailOverride #chargeOverrideForm #overrideTaxCode').val("");
					$('#detailOverride #chargeOverrideForm #overrideTaxKey').val("");
					$('#detailOverride #chargeOverrideForm #overrideTaxName').val("");
					
					$("#detailOverride #chargeOverrideForm #overrideTaxCode").attr('disabled',false);
					$("#detailOverride #chargeOverrideForm #overrideTaxCodeHelp").attr('disabled',false);
				}
				
				if (cell[4] == "CHARGE_TYPE-M") {
					$("#detailOverride #chargeOverrideForm #minimumValue").val(0);
					$("#detailOverride #chargeOverrideForm #minimumValueStripped").val(0);
					$("#detailOverride #chargeOverrideForm input[name='chargeOverride.isChecked']").attr("checked", true);
					$("#detailOverride #chargeOverrideForm #maximumValue").val("");
					$("#detailOverride #chargeOverrideForm #maximumValueStripped").val("");
					$("#detailOverride #chargeOverrideForm #minimumValue").attr('disabled','disabled');
					$("#detailOverride #chargeOverrideForm #maximumValue").attr('disabled','disabled');
					$("#detailOverride #chargeOverrideForm #isChecked").attr('disabled','disabled');
					$("#detailOverride #chargeOverrideForm #overrideTaxCode").focus();
				} else {
					/*
					$("#detailOverride #chargeOverrideForm #maximumValue").attr("disabled", false);
					$("#detailOverride #chargeOverrideForm #isChecked").attr('disabled',false);
					$("#detailOverride #chargeOverrideForm #isChecked").attr("checked",false);
					$("#detailOverride #chargeOverrideForm #minimumValue").attr('disabled',false);
					$("#detailOverride #chargeOverrideForm #minimumValue").focus();
					*/
					$("#detailOverride #chargeOverrideForm input[name='chargeOverride.isChecked']").attr("checked", true);
					$("#detailOverride #chargeOverrideForm #maximumValue").val("");
					$("#detailOverride #chargeOverrideForm #maximumValueStripped").val("");
					//$("#detailOverride #chargeOverrideForm #minimumValue").attr('disabled','disabled');
					$("#detailOverride #chargeOverrideForm #maximumValue").attr('disabled','disabled');
					//$("#detailOverride #chargeOverrideForm #isChecked").attr('disabled','disabled');
					$("#detailOverride #chargeOverrideForm #overrideTaxCode").focus();
				} 
			
				
				if (cell[1] =='CHARGE_CALCULATION-S'){
					//	alert("Can not add a charge tier for this charge profile, because tiering type is single value!");
						$(".buttons #newTierData").button('disable');
				} else {
						$(".buttons #newTierData").button('enable');
				}
				
				var data = new Object();
				data.chargeOverrideTiers = new Array();
				tabTier(data);	
				//var table = $('#listTier #gridChargeOverrideTier').dataTable();
				var dataTier = new Object();
				dataTier.taxMaster = new Object();
				dataTier.id = new Object();
				dataTier.id.rowNumber = 0;
				dataTier.tierMaximumRange =null;
				dataTier.tierValue = null;
				dataTier.taxMaster.taxCode= "";
				dataTier.taxMaster.taxKey = "";
				dataTier.taxMaster.description = "";
				dataTier.id.chargeOverrideKey = $("#detailChargeOverrideTier #tierChargeOverrideForm #chargeOverrideKey").val();
				tableTier.fnAddData(dataTier);
				
			$("#detailOverride #chargeOverrideForm #tieringType").val(cell[1]);
			$("#detailOverride #chargeOverrideForm #masterChargeKey").val(cell[0]);
			$("#detailOverride #chargeOverrideForm #masterChargeCode").val(cell[2]);
			$("#detailOverride #chargeOverrideForm #masterDescription").val(cell[3]);
			$("#detailOverride #chargeOverrideForm #valueType").val(cell[5]);
			$("#detailOverride #chargeOverrideForm #chargeCode").val(cell[2]);
			$("#detailOverride #chargeOverrideForm #chargeDescription").val(cell[3]);
			$("#detailOverride #chargeOverrideForm #chargeCategory").val(cell[6]);
			$("#detailOverride #chargeOverrideForm #invoiceCharge").val(cell[7]);
			$("#detailOverride #chargeOverrideForm #rowNumber").val(rowNumber);
			
			/*
			console.debug("tieringType : "+cell[1]);
			console.debug("masterChargeKey :"+cell[0]);
			console.debug("masterChargeCode :"+cell[2]);
			console.debug("masterDescription :"+cell[3]);
			console.debug("valueType : "+cell[5]);
			console.debug("chargeCode : "+cell[2]);
			console.debug("chargeDescription : "+cell[3]);
			console.debug("chargeCategory : "+cell[6]);
			console.debug("invoiceCharge :"+cell[7]);
			console.debug("rowNumber : "+rowNumber);
			*/	
				$("#code").html(cell[2]);
				$("input[name='chargeOverride.isChecked']").click(function () {
					if ($("input[name='chargeOverride.isChecked']").is(':checked')) {
						$("#maximumValue").attr("disabled", "disabled");
						$("#maximumValue").val("");
						$("#maximumValueStripped").val("");
						$("#maximumValue").removeClass('fieldError');
						$("#maximumValueError").html("");
					} else {
						$("#maximumValue").attr("disabled", false);
					}
				});
			return false;
			}
		});
		
	// END PROFILE

	// START OVERRIDE
	
		$('#detailOverride').dialog({
			autoOpen:false,
			height:575,
			width:925,
			modal:true,
			resizable:false,
			title:'Charge Overwrite'
		});
	
	// hide overflow scrollbar
		$('#detailOverride').css('overflow','hidden');
	
		$('#cancelOverwiteCharge').click(function(){
			$("#detailOverride").dialog('close');
		})
		
		$('#saveOverwiteCharge').click(function() {
			var rowPosition = $("#detailOverride #chargeOverrideForm #rowPosition").val();
			var min = parseFloat($("#detailOverride #chargeOverrideForm #minimumValueStripped").val());
			var max = parseFloat($("#detailOverride #chargeOverrideForm #maximumValueStripped").val());
			var table = tableOverwriteCharge.dataTable();
			$("#detailOverride #chargeOverrideForm").find("span[id*='Error']").html("");
			if (($("#detailOverride #chargeOverrideForm #minimumValue").val() == "") || ($("#detailOverride #chargeOverrideForm #overrideTaxCode").val() == "") || (($("#detailOverride #chargeOverrideForm #isChecked").is(':checked')==false) && ($("#detailOverride #chargeOverrideForm #maximumValue").val() == ""))  ){
				//alert("Minimum value or Tax Code can not be empty!")
				if ($("#detailOverride #chargeOverrideForm #minimumValue").val() == "") {
					$("#minimumValueError").html("Required").show();
				}
				if ($("#detailOverride #chargeOverrideForm #overrideTaxCode").val() == "") {
					$("#overrideTaxCodeError").html("Required").show();
				}
				
				if (($("#detailOverride #chargeOverrideForm #isChecked").is(':checked')==false) && ($("#detailOverride #chargeOverrideForm #maximumValue").val() == "")) {
					$("#maximumValueError").html("Required").show();
				}
				
				
			} else {
				if ((min > max) && (max != 0) ){
					//alert("maximum value must be greater than or equal minimum value !")
					$("#maximumValueError").html("Maximum value must be greater than or equal minimum value").show();
					$("#detailOverride #chargeOverrideForm #maximumValue").addClass('fieldError');
				} else {
					$("#detailOverride #chargeOverrideForm #maximumValue").removeClass('fieldError');
					var found = false;
					var datas = table.fnGetData(parseFloat(rowPosition));
					if (rowPosition != "") {
						datas.minimumValue = $("#detailOverride #chargeOverrideForm #minimumValueStripped").val();
						datas.maximumValue = $("#detailOverride #chargeOverrideForm #maximumValueStripped").val();
						datas.taxMaster.taxKey = $("#detailOverride #chargeOverrideForm #overrideTaxKey").val();
						datas.taxMaster.taxCode = $("#detailOverride #chargeOverrideForm #overrideTaxCode").val();
						datas.taxMaster.description = $("#detailOverride #chargeOverrideForm #overrideTaxName").val();
						//datas.isChecked = $("input[name='chargeOverride.isChecked']").val();
						if ($("input[name='chargeOverride.isChecked']").is(':checked') || (datas.maximumValue=="") || (datas.maximumValue==null)) {
							datas.isChecked = true;
						} else {
							datas.isChecked = false;
						}
						var tiers =$('#detailOverride #listTier #gridChargeOverrideTier').dataTable();
						var rows = tiers.fnGetData();
						datas.chargeOverrideTiers.length =  rows.length;
						for ( j=0;j < rows.length ;j++) {
							if (datas.chargeOverrideTiers[j] == undefined){
								datas.chargeOverrideTiers[j] = new Object();
								datas.chargeOverrideTiers[j].taxMaster = new Object();
								datas.chargeOverrideTiers[j].id = new Object();
								datas.chargeOverrideTiers[j].id.rowNumber = rows[j].id.rowNumber;
								datas.chargeOverrideTiers[j].id.chargeOverrideKey = rows[j].id.chargeOverrideKey;
								datas.chargeOverrideTiers[j].tierMaximumRange = (rows[j].tierMaximumRange == "MAX" ) ? null:rows[j].tierMaximumRange;
								datas.chargeOverrideTiers[j].tierValue = rows[j].tierValue;
								//datas.chargeOverrideTiers[j].taxMaster.taxKey =rows[j].taxMaster.taxKey;
								//datas.chargeOverrideTiers[j].taxMaster.taxCode= rows[j].taxMaster.taxCode;
								//datas.chargeOverrideTiers[j].taxMaster.description = rows[j].taxMaster.description;
								datas.chargeOverrideTiers[j].taxMaster.taxKey =$("#detailOverride #chargeOverrideForm #overrideTaxKey").val();
								datas.chargeOverrideTiers[j].taxMaster.taxCode= $("#detailOverride #chargeOverrideForm #overrideTaxCode").val();
								datas.chargeOverrideTiers[j].taxMaster.description = $("#detailOverride #chargeOverrideForm #overrideTaxName").val();
							} else {
								//console.debug(j,rows[j].tierMaximumRange);
								if(datas.chargeOverrideTiers[j].taxMaster == null){
									datas.chargeOverrideTiers[j].taxMaster = new Object();
								}
								datas.chargeOverrideTiers[j].tierMaximumRange = (rows[j].tierMaximumRange == "MAX" ) ? null:rows[j].tierMaximumRange;
								datas.chargeOverrideTiers[j].tierValue = rows[j].tierValue;
								//datas.chargeOverrideTiers[j].taxMaster.taxKey = rows[j].taxMaster.taxKey;
								//datas.chargeOverrideTiers[j].taxMaster.taxCode= rows[j].taxMaster.taxCode;
								//datas.chargeOverrideTiers[j].taxMaster.description = rows[j].taxMaster.description;
								datas.chargeOverrideTiers[j].taxMaster.taxKey =$("#detailOverride #chargeOverrideForm #overrideTaxKey").val();
								datas.chargeOverrideTiers[j].taxMaster.taxCode= $("#detailOverride #chargeOverrideForm #overrideTaxCode").val();
								datas.chargeOverrideTiers[j].taxMaster.description = $("#detailOverride #chargeOverrideForm #overrideTaxName").val();
							}
						}	
					} else {
						var tiers =$('#detailOverride #listTier #gridChargeOverrideTier').dataTable();
						var rows = tiers.fnGetData();
						var rowNumber = $("#detailOverride #chargeOverrideForm #rowNumber").val();
				
						var datas = new Object();
						datas.isChecked = new Boolean();
						datas.chargeMaster = new Object();	
						datas.taxMaster = new Object();
						datas.chargeOverrideTiers = new Array();
						datas.chargeOverrideTiers.length = rows.length;					
						//datas.isChecked = $("input[name='chargeOverride.isChecked']").val();
					 	datas.minimumValue = $("#detailOverride #chargeOverrideForm #minimumValueStripped").val();
						datas.maximumValue = $("#detailOverride #chargeOverrideForm #maximumValueStripped").val();
						datas.taxMaster.taxKey = $("#detailOverride #chargeOverrideForm #overrideTaxKey").val();
						datas.taxMaster.taxCode = $("#detailOverride #chargeOverrideForm #overrideTaxCode").val();
						datas.taxMaster.description = $("#detailOverride #chargeOverrideForm #overrideTaxName").val();
						if ($("input[name='chargeOverride.isChecked']").is(':checked')|| (datas.maximumValue=="") || (datas.maximumValue==null)) {
							datas.isChecked = true;
						} else {
							datas.isChecked = false;
						}
						datas.chargeMaster.chargeCode = $("#detailOverride #chargeOverrideForm #masterChargeCode").val();
						datas.chargeMaster.chargeKey = $("#detailOverride #chargeOverrideForm #masterChargeKey").val();
						datas.chargeMaster.description = $("#detailOverride #chargeOverrideForm #masterDescription").val();
						datas.chargeOverrideKey = $("#detailOverride #chargeOverrideForm #chargeOverrideKey").val();
							for ( j=0;j < rows.length ;j++) {
									if ((rows[j].taxMaster.taxCode=="") ||(rows[j].tierValue =="") ) {
										//alert("Fill Tax Code and Value in Charge Override Tier");
										$("#nullError").html("Fill Tier Value in Charge Overwrite Tier").show();
										return false;
									} else {
										$("#nullError").html("");
										datas.chargeOverrideTiers[j] = new Object();
										datas.chargeOverrideTiers[j].taxMaster = new Object();
										datas.chargeOverrideTiers[j].id = new Object();
										datas.chargeOverrideTiers[j].id.rowNumber = rows[j].id.rowNumber;
										datas.chargeOverrideTiers[j].id.chargeOverrideKey = rows[j].id.chargeOverrideKey;
										datas.chargeOverrideTiers[j].tierMaximumRange = (rows[j].tierMaximumRange == "MAX" ) ? null:rows[j].tierMaximumRange;
										datas.chargeOverrideTiers[j].tierValue = rows[j].tierValue;
										datas.chargeOverrideTiers[j].taxMaster.taxKey = rows[j].taxMaster.taxKey;
										datas.chargeOverrideTiers[j].taxMaster.taxCode= rows[j].taxMaster.taxCode;
										datas.chargeOverrideTiers[j].taxMaster.description = rows[j].taxMaster.description;
									}	
									
						}	
						
						table.fnAddData(datas);	
						$('#gridChargeProfile').find("input[name*='overrideButton["+rowNumber+"]']").attr('disabled','disabled');
					}
					$('#detailOverride').dialog('close');
					return false;
				}
			}
			
		})
		
		$('#cancelOverwiteCharge').click(function(){
			$('#detailOverride').dialog('close');
		})
		
		function overwriteCharge(data){
			
			if( tableOverwriteCharge != null ){
				tableOverwriteCharge.fnDestroy();
			}
			
			tableOverwriteCharge = 
				$('#gridOverwriteCharge').dataTable({
					aaData: data,
					aoColumns: [ {bVisible:false,
									mDataProp:'chargeOverrideKey'
					 			 },
					             {bVisible:false,
									mDataProp:'chargeMaster.chargeKey'
								  }, 
								  {mDataProp:'chargeMaster.chargeCode'},
								  {mDataProp:'chargeMaster.description'},
								  { 
									fnRender: function(obj) {           
										var controls;
							 			controls = '<center><input id="deleteButton" type="button" value="delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
							 			return controls;
					            	}
								  }
								],
					aaSorting:[[2,'asc']],
		        	bAutoWidth: false,	
		        	bDestroy:true,
		        	bFilter: false,
		        	bInfo: false,
		        	bJQueryUI: true,
		        	bPaginate: false,
		        	bSearch: false,
		        	bLengthChange: false,
		        	isDisplayLength:10                						
			});
			
			var table = tableOverwriteCharge.dataTable();
			var overwriteLength = table.fnGetNodes().length;
			
			var rows = tableChargeProfile.fnGetNodes().length;
			
			var rowSearch = $().parents('tr');
			
			for (j = 0 ;j < overwriteLength; j++ ) {
				var datas = table.fnGetData(j);
				for (i = 0; i < rows; i++) {
					var cells = tableChargeProfile.fnGetData(i);
					
					if (cells[0] == datas.chargeMaster.chargeKey) {
						
						$('#gridChargeProfile').find("input[name*='overrideButton["+i+"]']").attr('disabled','disable');
						
					}	
				}
			}
		}
		
		// Delete Row for overwrite charge
		$('#gridOverwriteCharge tbody tr #deleteButton').live('click', function() {
			var row = $(this).parents('tr');
			var rowDelete = tableOverwriteCharge.fnGetPosition(row[0]);
			var deleteOverwriteCharge = function() {
				var table = tableOverwriteCharge.dataTable();
				var datas = table.fnGetData(row[0]);
				var rows = tableChargeProfile.fnGetNodes().length;
				for (i = 0; i < rows; i++) {
					var cells = tableChargeProfile.fnGetData(i);
					if (cells[0] == datas.chargeMaster.chargeKey) {
						$('#gridChargeProfile').find("input[name*='overrideButton["+i+"]']").attr('disabled',false);
						break;
					}	
				}
				tableOverwriteCharge.fnDeleteRow(rowDelete);
				
				$("#dialog-message").dialog("close");
				return false;
			}
			
			messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteOverwriteCharge, closeDialogMessage);
		});
		
	
	
// Event Edit for override charge 
		$('#gridOverwriteCharge').removeAttr('style');
		$('#gridOverwriteCharge tbody tr td').die('click');
		$('#gridOverwriteCharge tbody tr td').live('click', function(){
			var rowPos= $(this).parents('tr');
			var rowPosNumber = tableOverwriteCharge.fnGetPosition(rowPos[0]);
			
			
			// if empty table
			if( rowPosNumber == null ){
				return;
			}
			//$("input[name='chargeOverride.isChecked']").attr("checked",false);
			//$("#maximumValue").attr("disabled",false);
			var pos = tableOverwriteCharge.fnGetPosition(this);
			if (pos[1] != 2) {
				var data = tableOverwriteCharge.fnGetData(this.parentNode);
				var key = data.chargeMaster.chargeKey;
				var rows = tableChargeProfile.fnGetNodes().length;
		
// ********************************************************				
				$.get('@{Pick.chargeMaster}', {'chargeCode':data.chargeMaster.chargeCode}, function(data2) {
					checkRedirect(data2);
					if('${fromMethod}' == 'approval'){
						$("#detailOverride #chargeOverrideForm #overrideTaxCode").attr('disabled','disabled');
						$("#detailOverride #chargeOverrideForm #overrideTaxCodeHelp").attr('disabled','disabled');
					}else{
						if(data2.frequencyID == 'CHARGE_FREQUENCY-M'){
							$.get('@{Pick.taxMaster}', {'code':'NOT'}, function(data) {
								checkRedirect(data);
								$('#detailOverride #chargeOverrideForm #overrideTaxCode').val(data.taxCode);
								$('#detailOverride #chargeOverrideForm #overrideTaxKey').val(data.code);
								$('#detailOverride #chargeOverrideForm #overrideTaxName').val(data.description);
							});
								$("#detailOverride #chargeOverrideForm #overrideTaxCode").attr('disabled','disabled');
								$("#detailOverride #chargeOverrideForm #overrideTaxCodeHelp").attr('disabled','disabled');
						}else{
							$('#detailOverride #chargeOverrideForm #overrideTaxCode').val("");
							$('#detailOverride #chargeOverrideForm #overrideTaxKey').val("");
							$('#detailOverride #chargeOverrideForm #overrideTaxName').val("");
							
							$("#detailOverride #chargeOverrideForm #overrideTaxCode").attr('disabled',false);
							$("#detailOverride #chargeOverrideForm #overrideTaxCodeHelp").attr('disabled',false);
							
						}
					}
				});
// ********************************************************				
				for (i = 0; i < rows; i++) {
					var cells = tableChargeProfile.fnGetData(i);
					if (cells[0] == key) {
						$("#chargeOverrideForm #tieringType").val(cells[1]);
						$("#detailOverride #chargeOverrideForm #chargeCode").val(cells[2]);
						$("#detailOverride #chargeOverrideForm #chargeDescription").val(cells[3]);
						$("#detailOverride #chargeOverrideForm #chargeCategory").val(cells[6]);
						$("#detailOverride #chargeOverrideForm #invoiceCharge").val(cells[7]);
						$("#detailOverride #chargeOverrideForm #chargeType").val(cells[4]);
						$("#code").html(cells[2]);
						break;
					}
					
				}	
				
				if ($("#chargeOverrideForm #tieringType").val()=='CHARGE_CALCULATION-S'){
					//	alert("Can not add a charge tier for this charge profile, because tiering type is single value!");
						$(".buttons #newTierData").button('disable');
				} else {
						$(".buttons #newTierData").button('enable');
				}
				
				var key = data.chargeMaster.chargeKey;
				for (i = 0; i < rows; i++) {
					var cells = tableChargeProfile.fnGetData(i);
					if (cells[0] == key) {
						$("#chargeOverrideForm #valueType").val(cells[5]);
						break;
					}
					
				}	
				
				if ($("#detailOverride #chargeOverrideForm #chargeType").val() == "CHARGE_TYPE-M") {
					$("#detailOverride #chargeOverrideForm #minimumValue").val(0);
					$("#detailOverride #chargeOverrideForm #minimumValueStripped").val(0);
					$("#detailOverride #chargeOverrideForm input[name='chargeOverride.isChecked']").attr("checked", true);
					$("#detailOverride #chargeOverrideForm #maximumValue").val("");
					$("#detailOverride #chargeOverrideForm #maximumValueStripped").val("");
					$("#detailOverride #chargeOverrideForm #minimumValue").attr('disabled','disabled');
					$("#detailOverride #chargeOverrideForm #maximumValue").attr('disabled','disabled');
					$("#detailOverride #chargeOverrideForm #isChecked").attr('disabled','disabled');
					$("#detailOverride #chargeOverrideForm #overrideTaxCode").focus();
				} else {
					$("#detailOverride #chargeOverrideForm #maximumValue").attr("disabled", false);
					$("#detailOverride #chargeOverrideForm #isChecked").attr('disabled',false);
					$("#detailOverride #chargeOverrideForm #isChecked").attr("checked",false);
					$("#detailOverride #chargeOverrideForm #minimumValue").attr('disabled',false);
					$("#detailOverride #chargeOverrideForm #minimumValue").focus();
				} 
				$("#detailOverride #chargeOverrideForm #rowPosition").val(rowPosNumber);
				$("#detailOverride #chargeOverrideForm #chargeOverrideKey").val(data.chargeOverrideKey);
				$("#detailOverride #chargeOverrideForm #masterChargeKey").val(data.chargeMaster.chargeKey);
				$("#detailOverride #chargeOverrideForm #minimumValue").autoNumericSet(data.minimumValue, {aPad:true,mDec:4}).val();
				$("#detailOverride #chargeOverrideForm #maximumValue").autoNumericSet(data.maximumValue, {aPad:true,mDec:4}).val();
				$("#detailOverride #chargeOverrideForm #minimumValueStripped").val(data.minimumValue);
				$("#detailOverride #chargeOverrideForm #maximumValueStripped").val(data.maximumValue);
				$("#detailOverride #chargeOverrideForm #overrideTaxKey").val(data.taxMaster.taxKey);
				$("#detailOverride #chargeOverrideForm #overrideTaxCode").val(data.taxMaster.taxCode);
				$("#detailOverride #chargeOverrideForm #overrideTaxName").val(data.taxMaster.description);
				
				$("#detailOverride #chargeOverrideForm #isChecked").val(data.isChecked);
				if ((data.isChecked == true) || (data.maximumValue==0)) {
					$("#detailOverride #chargeOverrideForm #maximumValue").attr("disabled", "disabled");
					$("#detailOverride #chargeOverrideForm input[name='chargeOverride.isChecked']").attr("checked", true);
					$("#maximumValue").val("");
					$("#maximumValueStripped").val("");
				}
				if (('${confirming}' == 'true') || ('${mode}'=='view')){
					$("#maximumValue").attr("disabled", "disabled");
					$("#minimumValue").attr("disabled", "disabled");
					$("#isChecked").attr("disabled", "disabled");
				}

				$("input[name='chargeOverride.isChecked']").click(function () {
					if ($("input[name='chargeOverride.isChecked']").is(':checked')) {
						$("#maximumValue").attr("disabled", "disabled");
						$("#maximumValue").val("");
						$("#maximumValueStripped").val("");
						$("#maximumValue").removeClass('fieldError');
						$("#maximumValueError").html("");
						
					} else {
						$("#maximumValue").attr("disabled", false);
					}
				});
				
				
				
				tabTier(data);
				$("#detailOverride").dialog('open');
				$('.ui-widget-overlay').css('height',$('body').height());
			}
	    });
// END OVERRIDE			
		function tabTier(data){
			tableTier = 
				$('#detailOverride #listTier #gridChargeOverrideTier').dataTable({
					aaData: data.chargeOverrideTiers,
					aoColumns: [ 
								{
									bVisible:false,
									sDefaultContent: "9999999999999999999999999999999999",
									sType : "numeric",
									fnRender: function(obj) {
										var controls;
											controls =  obj.aData.tierMaximumRange;
										return controls;
									}
								},
					             {
									bVisible:false,
									mDataProp:"id.rowNumber"
					 			 },
					             {
					 				//mDataProp: 'tierMaximumRange',
					                sDefaultContent: "MAX",
				                	sClass: 'numeric',
					            	fnRender: function(obj) {
					            		var controls;
					            		if ((obj.aData.tierMaximumRange == null) || (obj.aData.tierMaximumRange == "")|| (obj.aData.tierMaximumRange == "MAX")){
					            			controls =  obj.aData.tierMaximumRange;
					            		} else {
					            			controls = $('#dummy').autoNumericSet(obj.aData.tierMaximumRange, {aPad:true,mDec:4}).val();
					            		}
					            		
					            		return controls;
					            	}
								  }, 
								  {
									
						            fnRender: function(obj) {
					            		var controls;
					            			if(obj.aData.taxMaster == null) {
					            				controls = "";
					            			} else if(obj.aData.taxMaster.taxCode == null){
					            				
					            				controls =  "";
					            			} else {
					            				
					            				controls =  obj.aData.taxMaster.taxCode;
					            			}
					            		return controls;
					            	}
								  },
  
								  {
									//mDataProp: 'tierValue',
							       // sDefaultContent: null,
							        sClass: 'numeric',
					            	fnRender: function(obj) {
					            		var controls;
					            		if ((obj.aData.tierValue == null) || (obj.aData.tierValue == "")){
					            			controls =  obj.aData.tierValue;
					            		} else {
					            			//controls = $('#dummy').autoNumericSet(obj.aData.tierValue, {aPad:true,mDec:4}).val();
					            			controls =  obj.aData.tierValue;
					            		}
					            		
					            		return controls;
					            	}
								  },
								  {
	 								 fnRender: function(obj) {
								 			if ((obj.aData.tierMaximumRange == null) || (obj.aData.tierMaximumRange == "") || (obj.aData.tierMaximumRange == "MAX")) {
								 				var controls = '<center><input id="deleteButton" type="button" value="delete" disabled="disabled" /></center>';
									 			return controls;
								 			} else {
								 				var controls = '<center><input id="deleteButton" type="button" value="delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
									 			return controls;
								 				
								 			}
									  }
								  }
								],
					aaSorting:[[0,'asc']],
		        	bAutoWidth: false,		
		        	bDestroy: true,
		        	bFilter: false,
		        	bInfo: false,
		        	//bRetrieve:true,
		        	//bServerSide: true,
		        	bJQueryUI: true,
		        	bPaginate: false,
		        	bSearch: false,
		        	bLengthChange: false,
		        	isDisplayLength:10                						
			});
			
			$('#listTier #gridChargeOverrideTier ').removeAttr('style');
			$('#listTier #gridChargeOverrideTier tbody tr td').die('click');
			$('#listTier #gridChargeOverrideTier tbody tr td').live('click', function(){
				$("#existError").html("");
				$("#tierValueError").html("");
				$("#tierValue").removeClass('fieldError');
				$("#tierMaximumRange").removeClass("fieldError");
				var rowPos= $(this).parents('tr');
				var rowPosNumber = tableTier.fnGetPosition(rowPos[0]);
				
				// if empty table
				if( rowPosNumber == null ){
					return;
				}
				
				$("#detailChargeOverrideTier #tierChargeOverrideForm #valueType").val($("#chargeOverrideForm #valueType").val()) ;
				
				$("#detailChargeOverrideTier").find("span[id*='Error']").html("");
				$("#detailChargeOverrideTier").find("input[class*='fieldError']").removeClass("fieldError");
				if ($("#detailChargeOverrideTier #tierChargeOverrideForm #valueType").val() == "CHARGE_VALUE-P") {
					$("#detailChargeOverrideTier #tierChargeOverrideForm #percentage").css("display","");
					$("#tierValue").blur(function(){
						isPercent();
					});
				} else {
					$("#detailChargeOverrideTier #tierChargeOverrideForm #percentage").css("display","none");
					$("#tierValue").blur(function(){
						$("#tierValueError").html("");
						$("#tierValue").removeClass('fieldError');
					});
				}
				
				
				var pos = tableTier.fnGetPosition(this);
				cell = tableTier.fnGetData(this.parentNode);
				if (pos[1] != 3) {
					if ($("#detailOverride #chargeOverrideForm #overrideTaxCode").val() == '') {
						$("#overrideTaxCodeError").html("Required").show();
						//alert("Can not edit charge tier , Tax Code Can not be null!");
					} else {
						//$("#overrideTaxCodeError").html("");
						dataTier = tableTier.fnGetData(this.parentNode);
						/*if ((dataTier.tierMaximumRange == null) || (dataTier.tierMaximumRange == "") || (dataTier.tierMaximumRange == "MAX")){
							$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").attr("disabled","disabled");
							$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").val(null);
						} else {
							$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").attr("disabled",false);
							if (('${confirming}' == 'true')||('${mode}'=='view')){
								$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").attr("disabled", "disabled");
							}
						}*/
						$("#detailChargeOverrideTier #tierChargeOverrideForm #rowPosition").val(rowPosNumber);
						$("#detailChargeOverrideTier #tierChargeOverrideForm #rowNumber").val(dataTier.id.rowNumber);
						$("#detailChargeOverrideTier #tierChargeOverrideForm #chargeOverrideKey").val(dataTier.id.chargeOverrideKey);
						if ((dataTier.tierMaximumRange == null) || (dataTier.tierMaximumRange == "") || (dataTier.tierMaximumRange == "MAX")){
							$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").val("MAX");
							$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").attr("disabled","disabled");
	            		} else {
	            			$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").autoNumericSet( dataTier.tierMaximumRange, {aPad:true,mDec:4}).val();
							$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").attr("disabled",false);
							if (('${confirming}' == 'true')||('${mode}'=='view')){
								$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").attr("disabled", "disabled");
							}
	            		}
						
						$("#detailChargeOverrideTier #tierChargeOverrideForm #taxCode").val($("#detailOverride #chargeOverrideForm #overrideTaxCode").val());
						$("#detailChargeOverrideTier #tierChargeOverrideForm #taxKey").val($("#detailOverride #chargeOverrideForm #overrideTaxKey").val());
						$("#detailChargeOverrideTier #tierChargeOverrideForm #taxName").val($("#detailOverride #chargeOverrideForm #overrideTaxName").val());
						
						/*
							if(dataTier.taxMaster != null){
								$("#detailChargeOverrideTier #tierChargeOverrideForm #taxKey").val(dataTier.taxMaster.taxKey);
								$("#detailChargeOverrideTier #tierChargeOverrideForm #taxCode").val(dataTier.taxMaster.taxCode);
								$("#detailChargeOverrideTier #tierChargeOverrideForm #taxName").val(dataTier.taxMaster.description);
							} else {
								$("#detailChargeOverrideTier #tierChargeOverrideForm #taxKey").val("");
								$("#detailChargeOverrideTier #tierChargeOverrideForm #taxCode").val("");
								$("#detailChargeOverrideTier #tierChargeOverrideForm #taxName").val("");
							}
						
						*/
						
						if ((dataTier.tierValue == null) || (dataTier.tierValue == "")){
							$("#detailChargeOverrideTier #tierChargeOverrideForm #tierValue").val(dataTier.tierValue);
	            		} else {
	            			//$("#detailChargeOverrideTier #tierChargeOverrideForm #tierValue").autoNumericSet( dataTier.tierValue, {aPad:true,mDec:4}).val();
	            			$("#detailChargeOverrideTier #tierChargeOverrideForm #tierValue").val(dataTier.tierValue);
	            		}
						
						$("#detailChargeOverrideTier #tierChargeOverrideForm #tierValueStripped").val(dataTier.tierValue);
						$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRangeStripped").val(dataTier.tierMaximumRange);
						$('#detailChargeOverrideTier #tierChargeOverrideForm  #oldMaxRange').val($('#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRangeStripped').val());
						$('#detailChargeOverrideTier #tierChargeOverrideForm  #newMaxRange').val($('#detailChargeOverrideTier #tierChargeOverrideForm #oldMaxRange').val());
						$("#detailChargeOverrideTier").dialog('open');
						$('.ui-widget-overlay').css('height',$('body').height());
					}
				}
				
			});
			
		}
		//Delete tier row
		$('#listTier #gridChargeOverrideTier tbody tr #deleteButton').live('click', function() {
			var row = $(this).parents('tr');
			var rowNumber = tableTier.fnGetPosition(row[0]);
				tableTier.fnDeleteRow(rowNumber);	
			return false;
		});
		
		$('#addOverrideTier').die("click");
		$('#addOverrideTier').live("click", function(){
			$("#existError").html("");
			$("#tierValueError").html("");
			$("#tierValue").removeClass('fieldError');
			$("#tierMaximumRange").removeClass("fieldError");
			var oldMax =  parseFloat($('#detailChargeOverrideTier #tierChargeOverrideForm #oldMaxRange').val());
			var newMax =  parseFloat($('#detailChargeOverrideTier #tierChargeOverrideForm #newMaxRange').val());
			var table = $('#listTier #gridChargeOverrideTier').dataTable();
			var maxChange = $('#detailChargeOverrideTier #tierChargeOverrideForm #maxRangeChange').val();
			var maxTier = $("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRangeStripped").val();
			var rowPosition = $("#detailChargeOverrideTier #tierChargeOverrideForm #rowPosition").val();
			$("#nullError").html("");
			if((maxTier==null) || (maxTier=="") || (maxTier=="MAX" )) {
				maxTier="MAX";
			} 
			//saveTier();		
			
			//function saveTier(){
				
				//var tierValue = parseFloat($("#detailChargeOverrideTier #tierChargeOverrideForm #tierValueStripped").val());
				//var maxValue = parseFloat($("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRangeStripped").val());
				
				
				$("#tierValueError").html("");
				$("#tierValue").removeClass('fieldError');
				var tierValue = parseFloat($("#tierValueStripped").val());
				
				if ((($("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").val() == "") && ($("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").attr("disabled") != true)) || ($("#detailChargeOverrideTier #tierChargeOverrideForm #taxCode").val()=="") || ( $("#detailChargeOverrideTier #tierChargeOverrideForm #tierValue").val()=="")){
					//alert("Max Range, Tax Code or Value can not be Empty! ")
					if ($("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").val() == "") {
						$("#tierMaximumRangeError").html("Required").show();
					}
					if ($("#detailChargeOverrideTier #tierChargeOverrideForm #tierValue").val() == "") {
						$("#tierValueError").html("Required").show();
					}
				} else if (($("#detailChargeOverrideTier #tierChargeOverrideForm #valueType").val() == "CHARGE_VALUE-P") && (tierValue > 100)) {
					$("#tierValue").addClass('fieldError');
					$("#tierValueError").html("Must be less or equal 100").show();
						
				} else {
					//if (tierValue < maxValue) {
					//	alert("maximum range value must be less than or equal tier value !")
					//	$("#detailChargeOverrideTier #tierChargeOverrideForm #tierValue").addClass('fieldError');
					//} else {
					//	$("#detailChargeOverrideTier #tierChargeOverrideForm #tierValue").removeClass('fieldError');
					//	var found = false;
							var dataTiers = new Object();
							dataTiers.tierMaximumRange = new Object();
							if (rowPosition != "") {
								dataTiers = table.fnGetData(parseFloat(rowPosition));
								var found = false;
								// CHECKED DUPLICATE ROW
								var rows = table.fnGetNodes().length;
								for (i = 0; i < rows; i++) {
									var cells = table.fnGetData(i);
										if ((maxTier != "MAX")) {
											$('#maxValidation').autoNumericSet(cells.tierMaximumRange, {aPad:true,mDec:4}).val();
											//alert($('#maxValidation').val());
											if (((maxTier == cells.tierMaximumRange) || (maxTier == $('#maxValidation').autoNumericGet()) )&& (oldMax != newMax)) {
												//alert("Tier Maximum Range already Exist!");
												$("#existError").html("Max Range '"+$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").val()+"' already Exist").show();
												found = true;
												break;
											}
										}
								} 
								if (!found) {
									if(($("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").val()==null) || ($("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").val()=="") || ($("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").val()=="MAX" )) {
										//table.fnUpdate(dataTiers.tierMaximumRange = "99999999999999999999" ,parseFloat(rowPosition),0);
										table.fnUpdate(dataTiers.tierMaximumRange = "MAX" ,parseFloat(rowPosition),2);
									} else {
										table.fnUpdate(dataTiers.tierMaximumRange = $("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRangeStripped").val(),parseFloat(rowPosition),0);
										table.fnUpdate(dataTiers.tierMaximumRange = $("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRangeStripped").val(),parseFloat(rowPosition),2);
									}
									if (dataTiers.taxMaster == null){
										dataTiers.taxMaster = new Object();
									}
									table.fnUpdate(dataTiers.taxMaster.taxCode = $("#detailChargeOverrideTier #tierChargeOverrideForm #taxCode").val(),parseFloat(rowPosition),3);
									table.fnUpdate(dataTiers.tierValue = $("#detailChargeOverrideTier #tierChargeOverrideForm #tierValueStripped").val(),parseFloat(rowPosition),4);
									dataTiers.taxMaster.taxKey = $("#detailChargeOverrideTier #tierChargeOverrideForm #taxKey").val();
									dataTiers.taxMaster.description = $("#detailChargeOverrideTier #tierChargeOverrideForm #taxName").val();
									$('#detailChargeOverrideTier').dialog('close');
								}
								//dataTiers.tierValue = $("#detailChargeOverrideTier #tierChargeOverrideForm #tierValueStripped").val();
								//dataTiers.tierMaximumRange = $("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRangeStripped").val();
							} else {
								var found = false;
								// CHECKED DUPLICATE ROW
								var rows = table.fnGetNodes().length;
								for (i = 0; i < rows; i++) {
									var cells = table.fnGetData(i);
										$('#maxValidation').autoNumericSet(cells.tierMaximumRange, {aPad:true,mDec:4}).val()
										if (((maxTier == cells.tierMaximumRange) || (maxTier == $('#maxValidation').autoNumericGet()) )) {
											//alert("Tier maximum range already Exist!");
											$("#existError").html("Max Range '"+$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").val()+"' already Exist").show();
											found = true;
											break;
										}	
								} 
								if (!found) {
									var dataTier = new Object();
									dataTier.taxMaster = new Object();
									dataTier.id = new Object();
									dataTier.id.rowNumber = $("#detailChargeOverrideTier #tierChargeOverrideForm #rowNumber").val();
									dataTier.tierMaximumRange = $("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRangeStripped").val();
									dataTier.tierValue = $("#detailChargeOverrideTier #tierChargeOverrideForm #tierValueStripped").val();
									dataTier.taxMaster.taxCode= $("#detailChargeOverrideTier #tierChargeOverrideForm #taxCode").val();
									dataTier.taxMaster.taxKey = $("#detailChargeOverrideTier #tierChargeOverrideForm #taxKey").val();
									dataTier.taxMaster.description = $("#detailChargeOverrideTier #tierChargeOverrideForm #taxName").val();
									dataTier.id.chargeOverrideKey = $("#detailChargeOverrideTier #tierChargeOverrideForm #chargeOverrideKey").val();
									table.fnAddData(dataTier);
									$('#detailChargeOverrideTier').dialog('close');
								}
								
							}
							
							return false;
					//}
							$("#detailChargeOverrideTier #tierChargeOverrideForm").find("span[id*='Error']").html("");
				}
			//}

		});	
	// END TIER
	
		
		//Button New Data for Tier
		$('.buttons #newTierData').click(function() {
			$("#existError").html("");
			$("#detailChargeOverrideTier").find("span[id*='Error']").html("");
			$("#detailChargeOverrideTier").find("input[class*='fieldError']").removeClass("fieldError");
			$("#detailChargeOverrideTier #tierChargeOverrideForm #tierMaximumRange").attr("disabled",false);
			$("#detailChargeOverrideTier #tierChargeOverrideForm #tieringType").val($("#chargeOverrideForm #tieringType").val()) ;
			$("#detailChargeOverrideTier #tierChargeOverrideForm #valueType").val($("#chargeOverrideForm #valueType").val());
			if ($("#detailChargeOverrideTier #tierChargeOverrideForm #valueType").val() == "CHARGE_VALUE-P") {
				$("#detailChargeOverrideTier #tierChargeOverrideForm #percentage").css("display","");
				$("#tierValue").blur(function(){
					isPercent();
				});
			} else {
				$("#detailChargeOverrideTier #tierChargeOverrideForm #percentage").css("display","none");
				$("#tierValue").blur(function(){
					$("#tierValueError").html("");
					$("#tierValue").removeClass('fieldError');
				});
			}
			
			if (($("#detailOverride #chargeOverrideForm #overrideTaxCode").val() == "")){
				//alert("Minimum value or Tax Code can not be empty!")
				$("#minimumValueError").html("");
				if ($("#detailOverride #chargeOverrideForm #overrideTaxCode").val() == "") {
					$("#overrideTaxCodeError").html("Required").show();
				}
			
			} else {
				//if ($("#chargeOverrideForm #tieringType").val()=='CHARGE_CALCULATION-S'){
				//	alert("Can not add a charge tier for this charge profile, because tiering type is single value!");
				//} else {
					var found = false;
					var table = $('#listTier #gridChargeOverrideTier').dataTable().dataTable();	
					var cells = table.fnGetData(0);
					$("#nullError").html("");
					if(cells.tierValue == null){
						$("#nullError").html("Fill Tier Value in Charge Overwrite Tier MAX").show();
						found = true;
						return false;
					}
					
					if(!(found)) {
						selectedRow = null;
						$("#detailChargeOverrideTier").dialog('open');
						$('.ui-widget-overlay').css('height',$('body').height());
						$("#detailChargeOverrideTier :text").val(""); 
						$("#detailChargeOverrideTier :hidden").val("");
						$("#detailChargeOverrideTier #tierChargeOverrideForm #tieringType").val($("#chargeOverrideForm #tieringType").val()) ;
						$("#detailChargeOverrideTier #tierChargeOverrideForm #chargeOverrideKey").val($("#chargeOverrideForm #chargeOverrideKey").val()) ;
						$("#detailChargeOverrideTier #tierChargeOverrideForm #valueType").val($("#chargeOverrideForm #valueType").val()) ;
						$("#detailChargeOverrideTier #tierChargeOverrideForm #taxCode").val($("#detailOverride #chargeOverrideForm #overrideTaxCode").val());
						$("#detailChargeOverrideTier #tierChargeOverrideForm #taxKey").val($("#detailOverride #chargeOverrideForm #overrideTaxKey").val());
						$("#detailChargeOverrideTier #tierChargeOverrideForm #taxName").val($("#detailOverride #chargeOverrideForm #overrideTaxName").val());
						
						
					}
					
					//$("#detailChargeOverrideTier #tierChargeOverrideForm #rowPosition").val("");
				//}
			}
			return false;

		});
		
		$("#detailOverride #chargeOverrideForm #maximumValue").blur(function(){
			var el = $(this);
			var max = parseFloat($('#detailOverride #chargeOverrideForm #maximumValueStripped').val());
			var min = parseFloat($('#detailOverride #chargeOverrideForm #minimumValueStripped').val());
			el.removeClass('fieldError');

			$("#maximumValueError").html("");
			if ($("#detailOverride #chargeOverrideForm #maximumValue").val() == ""){
				$("#detailOverride #chargeOverrideForm #maximumValue").attr("disabled", "disabled");
					$("#detailOverride #chargeOverrideForm input[name='chargeOverride.isChecked']").attr("checked", true);
					$("#detailOverride #chargeOverrideForm #maximumValue").val("");
					$("#detailOverride #chargeOverrideForm #maximumValueStripped").val("");
			}
			if ((min > max) && (max != "")){
				el.addClass('fieldError');
				$("#maximumValueError").html("Maximum value must be greater than or equal minimum value").show();
			} 
		});
		
	});