function CpRuleMaster(html) {
	if (this instanceof CpRuleMaster) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		var rule = html.inject(this);
		var options = rule.operator.html();
		var optComparisonValue = $('#comparisonValue').html();
		var vComparisonValue = $('#comparisonValue').val();
		var backToWorkList = function() {
			loading.dialog('open');
			if ($("#from").val() == 'listBatch') {
				location = "@{Approvals.listbatch()}";
			} else {
				location = "@{Approvals.list()}";
			};
		};
/*==================================================================
 * Function
 *==================================================================*/
		function disableOperator() {
			$('#operator').html('<option value="NA">N/A</option>');
			$('#operator').attr('disabled', true);
			$('#value').val('');
			$('#value').attr('disabled', true);
			$('p[id=pOperator] label span').html('');
			$('p[id=pValue] label span').html('');
		}
		
		function enableOperator() {
			$('#operator').html(options);
			$('#operator').removeAttr('disabled');
			$('#value').removeAttr('disabled', true);
			$('p[id=pOperator] label span').html(' *');
			$('p[id=pValue] label span').html(' *');
		}
		
		function disableComparisonValue() {
			$('p[id=pComparisonValue] label span').html('');
			$('#comparisonValue').attr('disabled', true);
		}
		
		function enableComparisonValue() {
			$('p[id=pComparisonValue] label span').html(' *');
			$('#comparisonValue').removeAttr('disabled');
		}
		
		function setOperator() {
			var ruletype = rule.ruleType.val();
			
			if (rule.isReadOnly.val() == "true") {
				if ((ruletype == '${ruleType_investmentModel}') || (ruletype == '${ruleType_positiveSecurities}') || (ruletype == '${ruleType_positiveSecuritiesSector}')) {
					rule.operator.html('<option value="NA">N/A</option>');
					$('p[id=pValue] label span').html('');
				} else if (ruletype == '${ruleType_upperLowerSecuritiesPrice}') {
					rule.operator.html('<option value="NA">N/A</option>');
					$('p[id=pValue] label span').html('');
					$('p[id=pComparisonValue] label span').html('');
				}
			}else{
				// enable/disable operator & value
				if ((ruletype == '${ruleType_investmentModel}') || (ruletype == '${ruleType_positiveSecurities}') || (ruletype == '${ruleType_positiveSecuritiesSector}')) {
					disableOperator();
					enableComparisonValue();
				} else if (ruletype == '${ruleType_upperLowerSecuritiesPrice}') {
					disableOperator();
					$('#comparisonValueError').html('');
					$('#comparisonValue').removeClass('fieldError');
					$('#comparisonValue').val('');
					disableComparisonValue();
				} else {
					enableOperator();
					enableComparisonValue();
				}
				
				// enable/disable comparison value
				var arrComparisonValueCriterion = new Array();
				if ((ruletype == '${ruleType_foreignPortfolioPerIssuer}') || (ruletype == '${ruleType_localEquityPortfolioPerIssuer}') || (ruletype == '')) {
					$('#comparisonValue option').remove();
					$('#comparisonValue').html(optComparisonValue);
				} else {
					$('#comparisonValue option').remove();
					arrComparisonValueCriterion.push('${cpNAV}');
					arrComparisonValueCriterion.push('${cpPortfolio}');
					arrComparisonValueCriterion.push('${cpAsset}');
					
					if((ruletype=='003')||(ruletype=='008')||(ruletype=='013')) arrComparisonValueCriterion.push('${cpMarketCapitalization}');
					
					
					
					$.get("@{createDropdownListByComparisonValueCriteria()}", {'comparisonValue':arrComparisonValueCriterion}, function(data) {
						checkRedirect(data);
						for( var elem in data ){
							var drdown_ = data[elem];
							$("#comparisonValue" ).append("<option value='"+drdown_.value+"'>"+drdown_.text+"</option>");
						}
						$("#comparisonValue" ).val(vComparisonValue);
						if(ruletype=='013'){
							arrComparisonValueCriterion.push('${cpMarketCapitalization}');
							disableOperator();
							$("#comparisonValue").val('${cpMarketCapitalization}').attr('disabled',true);
						}
					});
				}
			}
		}
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/
		
		
		rule.ruleType.popupLookup("?group=CP_RULE_TYPE", "operator", function(data){
			rule.ruleType.removeClass('fieldError');
			setOperator();			
		}, function() {
			rule.ruleType.addClass('fieldError');
			rule.ruleTypeDesc.val('NOT FOUND');
			rule.ruleTypeKey.val('');
			rule.ruleType.val('');
			rule.h_ruleTypeDesc.val('');
		});
		
		rule.value.autoNumeric({vMax: '100'});
		rule.value.live('blur', function() {
			var el = $(this);
			if (el.val() == '') {
				return;
			}
		});
/*================================================================== 
 * Event
 *================================================================== */
		if ($.browser.msie){
			$("#description[maxlength]").bind('input propertychange', function() {  
		        var maxLength = $(this).attr('maxlength');  
		        if ($(this).val().length > maxLength) {  
		            $(this).val($(this).val().substring(0, maxLength));  
		        }  
		    });
		}
		
		if (rule.dispatch.val() == 'entry' || (rule.dispatch.val() == 'edit' && rule.status.val() == 'R')) {
			rule.isActive1.disabled();
			rule.isActive2.disabled();
		}
		
		$('input[name=isActive]').change(function(){
			$("input[name='cpComplianceRule.active']").val($("input[name='isActive']:checked").val());
		});
		
		if (rule.clear)
			rule.clear.click(function() {
				rule.cpruleitemform.attr('action', '@{entry()}');
				rule.cpruleitemform.submit();
				
			});
		
		if (rule.cancel)
			rule.cancel.click(function() {
				rule.cpruleitemform.attr('action', '@{list()}');
				rule.cpruleitemform.submit();
			});
		
		if (rule.save)
			rule.save.click(function(){
				html.loadingDialog();
				$("#comparisonValue").attr('disabled',false);
				rule.cpruleitemform.attr('action', "@{save()}?mode=${mode}");
				rule.cpruleitemform.submit();
			});	
		
		if (rule.back)
			rule.back.click(function(){
				rule.cpruleitemform.attr('action', '@{back()}?id=${id}&mode=${mode}');
				rule.cpruleitemform.submit();
			});
		
		if (rule.close)
			rule.close.click(function() {
				rule.cpruleitemform.attr('action', '@{list()}');
				rule.cpruleitemform.submit();
			});
		
		if (rule.confirm)
			rule.confirm.click(function(){
				html.loadingDialog();	
				rule.cpruleitemform.attr('action', "@{save()}?mode=${mode}");
				rule.cpruleitemform.attr('action', "@{confirm()}?mode=${mode}");
				rule.cpruleitemform.submit();
//				var loadingDialog = html.loadingDialog();
//				
//				$.post('@{confirm()}?mode=${mode}', rule.cpruleitemform.serialize(), function(data, status) {
//					loadingDialog.dialog('close');
//					if (data.status == 'success') {
//						alert("Your Rule Code is #" + data.result);
//						location = "@{entry()}";
//					} else {
//						alert(data.error);
//					}
//				});
			});	
		
/*================================================================== 
* WORKFLOW  .....................................
*================================================================== */
		if (rule.cancelWorkflow)
			rule.cancelWorkflow.click(function(){
				if (rule.from.val() == 'listBatch') {
					location = "@{Approvals.listbatch()}";
				} else {
					location = "@{Approvals.list()}";
				}
			});
		
		if (rule.reject)
			rule.reject.click(function(){
				var action = "@{reject()}?taskId=${taskId}&maintenanceLogKey=${maintenanceLogKey}&operation=${operation}";
				loading.dialog('open');
				$.post(action, function(data, status, xhr) {
		    		checkRedirect(data);
					loading.dialog('close');
					if (status == 'success') {
//						alert("Rule Code ${cpComplianceRule?.ruleCode} is Rejected " + data);
						messageAlertOk("Rule Code '${cpComplianceRule?.ruleCode}' is Rejected", "ui-icon ui-icon-circle-check", "Approval Message", backToWorkList);
					}
				});
			});
		
		if (rule.approve)
			rule.approve.click(function(){
				var action = "@{approve()}?taskId=${taskId}&maintenanceLogKey=${maintenanceLogKey}&operation=${operation}";
				loading.dialog('open');
				$.post(action, function(data, status, xhr) {
		    		checkRedirect(data);
					loading.dialog('close');
					if (status == 'success') {
//						alert("RuleCode ${cpComplianceRule?.ruleCode} is Approved " + data);
						messageAlertOk("Rule Code '${cpComplianceRule?.ruleCode}' is Approved", "ui-icon ui-icon-circle-check", "Approval Message", backToWorkList);
					}else{
//						alert("Fail to approve ${cpComplianceRule?.ruleCode} " + data);
						messageAlertOk("Fail to Approve '${cpComplianceRule?.ruleCode}'", "ui-icon1 ui-icon-circle-close", "Error Message", backToWorkList);
					}
				});
			});
		
		setOperator();	
	}else{
		return new CpRuleMaster(html);
	}
}