$(function() {
	$('.buttons button').button();
	$('.buttons input:button').button();
	$('#autoSchedule').children().eq(0).remove();

	/*	var tabs = 
			$('#tabsCorporateAction').tabs({
				select: function(event, ui) {
					//alert($(ui.panel).attr('id'));
					activeTab = $(ui.panel).attr('id');
					activeTabCapital = activeTab.toString().slice(0,1).toUpperCase() + activeTab.slice(1);
					}
			});*/
		
		
		if (('${mode}'=='entry') || (('${mode}'=='edit') && (('${actionTemplate?.recordStatus?.decodeStatus()}' == 'Reject')|| ($("#status").val() == 'R' )))) {
			$("input[name='isActive']").attr("disabled", "disabled");
		}
		$("input[name='isActive']").change(function() {
			$("input[name='actionTemplate.isActive']").val($("input[name='isActive']:checked").val());
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
					}	
				},
				error: function(data) {
					$('#securityType').addClass('fieldError');
					$('#securityTypeDesc').val('NOT FOUND');
					$('#securityType').val('');
					$('#h_securityTypeDesc').val('');
				}
			},
			description:$('#securityTypeDesc'),
			help:$('#securityTypeHelp'),
			nextControl:$('#sourceTransaction')
		});
		
		$('#sourceTransaction').lookup({
			list:'@{Pick.transactionTemplates()}',
			get:{
				url:'@{Pick.transactionTemplate()}',
				success: function(data) {
					if (data) {
						$('#sourceTransaction').removeClass('fieldError');
						$('#sourceTransactionKey').val(data.code);
						$('#sourceTransactionDesc').val(data.description);
						$('#h_sourceTransactionDesc').val(data.description);
						$("#requiredError").html("");
						$("#sourceTransactionError").html("");
						$("#targetTransactionError").html("");
					}	
				},
				error: function(data) {
					$('#sourceTransaction').addClass('fieldError');
					$('#sourceTransactionDesc').val('NOT FOUND');
					$('#sourceTransaction').val('');
					$('#sourceTransactionKey').val('');
					$('#h_sourceTransactionDesc').val('');
				}
			},
			key:$('#sourceTransactionKey'),
			filter:'USED_BY-3',
			description:$('#sourceTransactionDesc'),
			help:$('#sourceTransactionHelp'),
			nextControl:$('#targetTransaction')
		});

		$('#targetTransaction').lookup({
			list:'@{Pick.transactionTemplates()}',
			get:{
				url:'@{Pick.transactionTemplate()}',
				success: function(data) {
					if (data) {
						$('#targetTransaction').removeClass('fieldError');
						$('#targetTransactionKey').val(data.code);
						$('#targetTransactionDesc').val(data.description);
						$('#h_targetTransactionDesc').val(data.description);
						$("#requiredError").html("");
						$("#sourceTransactionError").html("");
						$("#targetTransactionError").html("");
					}	
				},
				error: function(data) {
					$('#targetTransaction').addClass('fieldError');
					$('#targetTransactionDesc').val('NOT FOUND');
					$('#targetTransaction').val('');
					$('#targetTransactionKey').val('');
					$('#h_targetTransactionDesc').val('');
				}
			},
			key:$('#targetTransactionKey'),
			//filter:'$transaction',
			filter:'USED_BY-3',
			description:$('#targetTransactionDesc'),
			help:$('#targetTransactionHelp')
		});
		
		$('#sourceTransaction').change(function(){
			if ($('#sourceTransaction').val() == "") {
				$('#sourceTransaction').val("");
				$('#sourceTransactionKey').val("");
				$('#sourceTransactionDesc').val("");
				$('#h_sourceTransactionDesc').val("");
			} else {
				$("#requiredError").html("");
				$("#sourceTransactionError").html("");
				$("#targetTransactionError").html("");
				
			}
		});
		
		$('#targetTransaction').change(function(){
			if ($('#targetTransaction').val() == "") {
				$('#targetTransaction').val("");
				$('#targetTransactionKey').val("");
				$('#targetTransactionDesc').val("");
				$('#h_targetTransactionDesc').val("");
			} else {
				$("#requiredError").html("");
				$("#sourceTransactionError").html("");
				$("#targetTransactionError").html("");
			}
		});
		
		$('#target').change(function(){
			target();
		});
		
		// DataTables section
		/*var table = 
			$('#listCorporateAction #gridCorporateAction').dataTable({
				aoColumns: [ 
				             {bVisible:false},
				             {bVisible:false},
				             {bVisible:false},
				             null,
			    	         null,
			    	         null
					    ],
				aaSorting:[[0,'asc']],
				aoColumnDefs: [{asSorting:["asc"], aTargets:[0]}, {asSorting:[""], aTargets:[1,2]}],
				bJQueryUI:true,
				//bAutoWidth: true,
				//bLengthChange:true,
				//iDisplayLength:15,	
				bFilter:false,
				bSort: false,
				bPaginate:false,
				bInfo:false
				
			});
	
		$('#listCorporateAction #gridCorporateAction ').removeAttr('style'); // tembak nilai widht untuk tampilan
		$('#listCorporateAction #gridCorporateAction tbody tr').die('click');
		$('#listCorporateAction #gridCorporateAction tbody tr').live('click', function() {
			editCorporateAction(this);
			$("#detailCorporateAction").dialog('open');
		});*/
		
		// end of DataTables
		
		
		$( "#detailCorporateAction" ).dialog({
			autoOpen:false,
			modal:true,
			heigth:'800px',
			width:'650px',
			resizable:false
		});

		//--Even when click radio button Coupon
		$("input[name='actionTemplate.isCoupon']").click(function() {
			if ($("input[name='actionTemplate.isCoupon']:checked").val() == "true") {
				if($("#target").val() != "${targetTypeC}" ){
					target();
				} 
				$("#target").val("${targetTypeC}");
				$("#targetHidden").val($("#target").val());
				$("#targetForFilter").val("C");
				$("#target").attr("disabled","disabled");
				
				if ($('#autoSchedule').val()!=''){
					if ($('#autoSchedule').val()=='${caScheduleCouponDate}'){
						$('#autoScheduleError').html('');
						$('#autoSchedule').removeClass('fieldError');
					}
				}
				
				if ($('#holdingBaseOn').val()!=''){
					if ($('#holdingBaseOn').val()=='${caScheduleCouponDate}'){
						$('#holdingBaseOnError').html('');
						$('#holdingBaseOn').removeClass('fieldError');
					}
				}
				
			} else {
				$("#target").attr("disabled",false);
				$("#target").val('');
				$("#targetHidden").val('');
				//$("#targetHidden").val($("#target").val());
				
				if ($('#autoSchedule').val()!=''){
					if ($('#autoSchedule').val()=='${caScheduleCouponDate}'){
						$('#autoScheduleError').html('This value is allowed if coupon payment');
						$('#autoSchedule').addClass('fieldError');
					}
				}
				
				if ($('#holdingBaseOn').val()!=''){
					if ($('#holdingBaseOn').val()=='${caScheduleCouponDate}'){
						$('#holdingBaseOnError').html('This value is allowed if coupon payment');
						$('#holdingBaseOn').addClass('fieldError');
					}
				}
			}
		})

		//--Even when change target dropdown
		if ($("#target").val()=="${targetTypeC}") {
			$("#targetForFilter").val("C");
		} else {
			$("#targetForFilter").val("");
		}
		
		$("#target").change(function(){
			if ($("#target").val()=="${targetTypeC}") {
				$("#targetForFilter").val("C");
				$("#targetHidden").val($("#target").val());
			} else {
				$("#targetForFilter").val("");
				$("#targetHidden").val($("#target").val());
			}
		})

		/*$('.buttons #newCorporateActionData').live('click', function() {
			selectedRow = null;
			$("#detailCorporateAction").dialog('open');
			$("#detailCorporateAction :text").val(""); 
			$("#detailCorporateAction :hidden").val(""); 
			$("#detailCorporateAction #isSource").val(""); 
			$("#detailCorporateAction #filterTransaction").val($("#targetForFilter").val());
		//	alert($("#detailCorporateAction #filterTransaction").val());
			return false;
		});*/
		
		$('#autoSchedule').change(function(){
			if ($("input[name='actionTemplate.isCoupon']:checked").val() == 'false'){
				if ($(this).val()=='${caScheduleCouponDate}'){
					$('#autoScheduleError').html('This value only allow if coupon payment');
					$(this).addClass('fieldError');
				} else {
					$('#autoScheduleError').html('');
					$(this).removeClass('fieldError');
				}
			} else {
				$('#autoScheduleError').html('');
				$(this).removeClass('fieldError');
			}
				
		});
		
		$('#holdingBaseOn').change(function(){
			if ($("input[name='actionTemplate.isCoupon']:checked").val() == 'false'){
				if ($(this).val()=='${caScheduleCouponDate}'){
					$('#holdingBaseOnError').html('This value only allow if coupon payment');
					$(this).addClass('fieldError');
				} else {
					$('#holdingBaseOnError').html('');
					$(this).removeClass('fieldError');
				}
			} else {
				$('#holdingBaseOnError').html('');
				$(this).removeClass('fieldError');
			}
		});
		
		if ((('${mode}'=='entry')&&('${confirming}'!='true')) ||
			(('${mode}'=='edit')&&('${confirming}'!='true'))){
			if ($('input[name="actionTemplate.taxApply"]:checked').val()=='false'){
				$('.taxApp').attr('disabled','disabled');
				$('p[id="pTaxObject"] label span').html("");
				$('#cekBoxLinkAnnouncement').attr('disabled', 'disabled');
			}
			
			if (!$('#cekBoxLinkAnnouncement').is(':checked')) {
				$('#actionTemplateLink').attr('disabled', 'disabled');
				$('#actionTemplateLinkHelp').attr('disabled', 'disabled');
			}
		}
		
		if ($('input[name="actionTemplate.taxApply"]').val()=='false'){
			$('p[id="pTaxObject"] label span').html("");
		}
		
		$('input[name="actionTemplate.taxApply"]').click(function(){
			if ($("input[name='actionTemplate.taxApply']:checked").val() == "true") {
				$('.taxApp').attr('disabled',false);
				$('#cekBoxLinkAnnouncement').attr('disabled', false);
				$('p[id="pTaxObject"] label span').html(" *");
				
			} else {
				$('.taxApp').attr('disabled','disabled');
				$('p[id="pTaxObject"] label span').html("");
				$('#cekBoxLinkAnnouncement').attr('disabled', 'disabled');
				$('#hasExercisePrice2').attr("checked", true);
				$("input[name='actionTemplate.exercisePrice']").val(false);
				$('#actionTemplateLink').attr('disabled', 'disabled');
				$('#actionTemplateLinkHelp').attr('disabled', 'disabled');
				$('#actionTemplateLink').val('');
				$('#actionTemplateLinkKey').val('');
				$('#actionTemplateLinkDesc').val('');
				$('#actionTemplateLinkError').html('');
				$('#actionTemplateLink').removeClass('fieldError');
				
				$('.taxApp').val('');
				$('#cekBoxLinkAnnouncement').val('');
				$('#cekBoxLinkAnnouncement').attr('checked', false);
			}
		});
		
		$('#cekBoxLinkAnnouncement').change(function(){
			if ($(this).is(':checked')){
				$('#actionTemplateLink').attr('disabled', false);
				$('#actionTemplateLinkHelp').attr('disabled', false);
			} else {
				$('#actionTemplateLink').attr('disabled', 'disabled');
				$('#actionTemplateLinkHelp').attr('disabled', 'disabled');
				$('#actionTemplateLink').val('');
				$('#actionTemplateLinkKey').val('');
				$('#actionTemplateLinkDesc').val('');
				$('#h_actionTemplateLinkDesc').val('');
				$('#actionTemplateLinkError').html('');
				$('#actionTemplateLink').removeClass('fieldError');
			}
		});
		
		$('#actionTemplateLink').change(function(){
			if ($(this).val()==''){
				$('#actionTemplateLinkKey').val('');
				$('#actionTemplateLinkDesc').val('');
				$('#h_actionTemplateLinkDesc').val('');
				$('#actionTemplateLink').removeClass('fieldError');
				$('#actionTemplateLinkError').html('');
			}
		});
		
		$("#actionTemplateLink").lookup({
			list:'@{Pick.actionTemplates()}',
			get:{
				url:'@{Pick.actionTemplate()}',
				success: function(data) {
					if (data) {
						if (data.code == $('#actionTemplateKey').val()){
							$('#actionTemplateLink').addClass('fieldError');
							$('#actionTemplateLinkDesc').val(data.description);
							$('#actionTemplateLinkError').html('Disallowed for this code');
							return false;
						}
						$('#actionTemplateLink').removeClass('fieldError');
						$('#actionTemplateLinkKey').val(data.code);
						$('#actionTemplateLinkDesc').val(data.description);
						$('#h_actionTemplateLinkDesc').val(data.description);
						$('#actionTemplateLinkError').html('');
					}	
				},
				error: function(data) {
					$('#actionTemplateLink').addClass('fieldError');
					$('#actionTemplateLinkDesc').val('NOT FOUND');
					$('#actionTemplateLink').val('');
					$('#actionTemplateLinkKey').val('');
					$('#h_actionTemplateLinkDesc').val('');
				}
			},
			key:$('#actionTemplateLinkKey'),
			description:$('#actionTemplateLinkDesc'),
			help:$('#actionTemplateLinkHelp')
		})
	});

	function doSave() {
		if ($('#sourceTransaction').val() == "") {
			$('#sourceTransactionKey').val('');
			$('#sourceTransactionDesc').val('');
			$('#h_sourceTransactionDesc').val('');
		}
	
		if ($('#targetTransaction').val() == "") {
			$('#targetTransactionKey').val('');
			$('#targetTransactionDesc').val('');
			$('#h_targetTransactionDesc').val('');
		}
		
		if (($('#autoSchedule').hasClass('fieldError'))||
			($('#holdingBaseOn').hasClass('fieldError'))||
			($('#actionTemplateLink').hasClass('fieldError'))){
			return false;
		}
		
		return true;
		
	}
	
	function target() {
			$('#sourceTransaction').val("");
			$('#sourceTransactionKey').val("");
			$('#sourceTransactionDesc').val("");
			$('#h_sourceTransactionDesc').val("");
			$('#targetTransaction').val("");
			$('#targetTransactionKey').val("");
			$('#targetTransactionDesc').val("");
			$('#h_targetTransactionDesc').val("");
	} 