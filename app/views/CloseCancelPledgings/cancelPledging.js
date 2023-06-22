$(function() {
		$('.calendar').datepicker();

		var data = new Object();
		tabPledgingDetail(data);
		// ============================ PROCESS IN MAIN PLEDGING FORM ============================ //
		$('#closeCancelDate').change(function(){
			validateCloseCancelDate();
		});
		
		// CIF LOOKUP
		$('#cifCode').change(function(){
			if ($('#cifCode').val() == ""){
				$('#cifCode').removeClass('fieldError');
				$('#cifKey').val("");
				$('#cifDesc').val("");
				$('#h_cifDesc').val("");
				
				$('#tabTransactionNo').val("");
				$('#tabTransactionDate').val("");
				$('#tabTransactionDateHidden').val("");
				$('#tabPledgingAmount').val("");
				$('#tabPledgingAmountStripped').val("");
				$('#tabRemarks').val("");
				$('#tabRemarksHidden').val("");
				tablePledging.fnClearTable();
			}	
		});
		
		$('#cifCode').lookup({
			list:'@{Pick.cifs()}',
			get:{
				url:'@{Pick.customer()}',
				success: function(data){
					$('#cifCode').removeClass('fieldError');
					$('#cifKey').val(data.code);
					$('#cifDesc').val(data.description);
					$('#h_cifDesc').val(data.description);
					
					$('#tabTransNoError').html('');
					$('#tabTransactionNo').removeClass("fieldError");
					$('#tabTransactionNo').val("");
					$('#tabTransactionDate').val("");
					$('#tabTransactionDateHidden').val("");
					$('#tabPledgingAmount').val("");
					$('#tabPledgingAmountStripped').val("");
					$('#tabRemarks').val("");
					$('#tabRemarksHidden').val("");
					tablePledging.fnClearTable();
				},
				error: function(data){
					$('#cifCode').addClass('fieldError');
					$('#cifKey').val('');
					$('#cifCode').val('');
					$('#cifDesc').val('NOT FOUND');
					$('#h_cifDesc').val('');
				}
			},
			description:$('#cifDesc'),
			help:$('#cifHelp')
		});

		// ========================== PROCESS IN DETAIL PLEDGING FORM ========================//
		
		$("#accountType").children().eq(0).remove();
		$('#tabsDetailCancelPledging').tabs();
		
		// DataTables section
		
		
		// end of DataTables
		
		$('.buttons button').button();
		$('.buttons input:button').button();
		$('.buttons #close').click(function() {
			$('#dialog').form('close');
		});
		
		$( "#detailCancelPledging" ).dialog({
			autoOpen:false,
			modal:true,
			heigth:'650px',
			width:'750px',
			resizable:false
		});
		
/*		if($("#tabTransactionNo").val()!=""){
			$('#tabTransactionDate').val($('#tabTransactionDateHidden').val());
			$('#tabPledgingAmount').autoNumericSet($('#tabPledgingAmountStripped').val());
			$('#tabRemarks').val($('#tabRemarksHidden').val());
			alert('${detailCancelPledgings}');
		}*/
		
		$("#tabTransactionNo").change(function(){
			if ($('#tabTransactionNo').val()==""){
				$('#tabTransactionNo').val("");
				$('#tabTransactionDate').val("");
				$('#tabTransactionDateHidden').val("");
				$('#tabPledgingAmount').val("");
				$('#tabPledgingAmountStripped').val("");
				$('#tabRemarks').val("");
				$('#tabRemarksHidden').val("");
				tablePledging.fnClearTable();
			}
		});
		
		// Action Transaction No. in Tab
		$('#tabTransactionNo').lookup({
			list : '@{Pick.pledgings()}',
			get : {
				url : '@{Pick.pledging()}',
				success: function(data){
					$('#tabTransactionNo').removeClass("fieldError");
					$('#tabTransactionNo').val(data.transactionNo);
					$('#tabTransactionDate').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.transactionDate)));
					$('#tabTransactionDateHidden').val($.datepicker.formatDate('${appProp.jqueryDateFormat}', new Date(data.transactionDate)));
					$('#tabRemarks').val(data.remarks);
					$('#tabRemarksHidden').val(data.remarks);
					$('#tabPledgingAmount').autoNumericSet(data.pledgingAmount);
					$('#tabPledgingAmountStripped').val(data.pledgingAmount);
					validateCloseCancelDate();
					var dataJsonPledging = JSON.parse(data.detailPledgings);
					var rows = tablePledging.fnGetNodes().length;
					
					for (var i=0; i<rows; i++){
						var cell = tablePledging.fnGetData(i);
						if (cell!=null) {
							if(cell.plTransaction.transactionKey != data.transactionNo){
								tablePledging.fnClearTable();
								tablePledging.fnAddData(dataJsonPledging);
							}
						}
					}
					if (rows < 1)
						tablePledging.fnAddData(dataJsonPledging);
					
				},
				error: function(data){
					$('#tabTransactionNo').addClass("fieldError");
					$('#tabTransNoError').html("Not Found!");
					$('#tabTransactionNo').val("");
					$('#tabTransactionDate').val("");
					$('#tabTransactionDateHidden').val("");
					$('#tabPledgingAmount').val("");
					$('#tabPledgingAmountStripped').val("");
					$('#tabRemarks').val("");
					$('#tabRemarksHidden').val("");
					tablePledging.fnClearTable();
				}
			},
			filter:$('#cifKey'),
			help:$('#tabTransactionHelp')
		});
		

		$( "#closeCancelPledging" ).click(function() {
			$('#detailCancelPledging').dialog('close');
		});
		
		
	});


	function tabPledgingDetail(data) {
		var detailCancelPledgings;
		if ('${detailCancelPledgings}' != null )
			detailCancelPledgings = ${detailCancelPledgings.raw()};

		if ('${detailCancelPledgings}' == null )
			detailCancelPledgings = new Array();
		
		tablePledging = 
			$('#listCancelPledging #gridCancelPledging').dataTable({
				aaData: detailCancelPledgings,
				aoColumns: [
							{
								fnRender: function(obj){
									var controls;
									controls = obj.aData.account.accountNo;
									controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].account.accountNo" value="' + obj.aData.account.accountNo + '" />';
									controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].account.custodyAccountKey" value="' + obj.aData.account.custodyAccountKey + '" />';
									controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].account.name" value="' + obj.aData.account.name + '" />';
									return controls;
								}
							},
							{
								fnRender: function(obj){
									var controls;
									controls = obj.aData.security.securityType.securityType +" || "+obj.aData.security.securityId;
									controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].security.securityType.securityType" value="' + obj.aData.security.securityType.securityType + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].security.securityType.description" value="' + obj.aData.security.securityType.description + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].security.securityKey" value="' + obj.aData.security.securityKey + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].security.securityId" value="' + obj.aData.security.securityId + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].security.description" value="' + obj.aData.security.description + '" />';
									return controls;
								}
							},
							{
								sClass:'numeric',
								fnRender: function(obj){
									var controls;
									controls = $('#dummy').autoNumericSet(obj.aData.amount, {aPad:true,mDec:2}).val();
									controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].amount" value="' + obj.aData.amount + '" />';
									return controls;
								}
							},
							{
								fnRender: function(obj) {
								 	var controls;
								 	controls = '<center><input id="deleteButton" type="button" value="Delete" #{if releaseOnly}disabled="disabled"#{/if} /></center>';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].plTransaction.transactionKey" value="' + obj.aData.plTransaction.transactionKey + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].transactionDetailKey" value="' + obj.aData.transactionDetailKey + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].accountType.lookupId" value="' + obj.aData.accountType.lookupId + '" />';
								 	//controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].portfolio.portfolioKey" value="' + obj.aData.portfolio.portfolioKey + '" />';
								 	//controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].portfolio.classification.lookupDescription" value="' + obj.aData.portfolio.classification.lookupDescription + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].classification.lookupId" value="' + obj.aData.classification.lookupId + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].holdingRefs" value="' + obj.aData.holdingRefs + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].portoQuantity" value="' + obj.aData.portoQuantity + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].availableQuantity" value="' + obj.aData.availableQuantity + '" />';
								 	return controls;
							 	}
							}
					       	],
				aaSorting:[[0, 'asc']],
				bAutoWidth: false,
				bFilter: false,
				bInfo: false,
				bJQueryUI:true,
				bPaginate: false,
				bSearch: false,
				bLengthChange:false
			});
		
		$('#listCancelPledging #gridCancelPledging ').removeAttr('style'); // tembak nilai widht untuk tampilan
		$('#listCancelPledging #gridCancelPledging tbody tr td').die('click');
		$('#listCancelPledging #gridCancelPledging tbody tr td').live('click', function() {
			var rowPos = $(this).parents('tr');
			var rowPosNumber = tablePledging.fnGetPosition(rowPos[0]);
			var pos = tablePledging.fnGetPosition(this);
			cell = tablePledging.fnGetData(this.parentNode);
			if (pos[1] != 4) {
				
				dataPledging = tablePledging.fnGetData(this.parentNode);
				var filterPledging = "[\""+dataPledging.account.custodyAccountKey+"\",\""
										+dataPledging.security.securityType.securityType+"\",\""+dataPledging.security.securityKey
										+"\",\""+dataPledging.classification.lookupId+"\"]";
				$.get("@{Pick.pledingPortfolio()}", {'code':dataPledging.holdingRefs, 'filter':filterPledging}, function(data) {
					checkRedirect(data);
					$('#detailCancelPledging #pledgingForm #portfolioQuantity').autoNumericSet(data.totalQuantity);
					$('#detailCancelPledging #pledgingForm #portfolioQuantityStripped').val(data.totalQuantity);
					//$('#detailCancelPledging #pledgingForm #availableQty').autoNumericSet(data.portoQuantity);
					//$('#detailCancelPledging #pledgingForm #availableQtyStripped').val(data.portoQuantity);
					$('#detailCancelPledging #pledgingForm #portfolioNoKey').val(data.code);
				});
				
				$('#detailCancelPledging #pledgingForm #rowPosition').val(rowPosNumber);
				$('#detailCancelPledging #pledgingForm #accountType').val(dataPledging.accountType.lookupId);
				$('#detailCancelPledging #pledgingForm #accountTypeDesc').val(dataPledging.accountType.lookupDescription);
				$('#detailCancelPledging #pledgingForm #accountNo').val(dataPledging.account.accountNo);
				$('#detailCancelPledging #pledgingForm #accountKey').val(dataPledging.account.custodyAccountKey);
				$('#detailCancelPledging #pledgingForm #accountDesc').val(dataPledging.account.name);
				$('#detailCancelPledging #pledgingForm #securityType').val(dataPledging.security.securityType.securityType);
				$('#detailCancelPledging #pledgingForm #securityTypeDesc').val(dataPledging.security.securityType.description);
				$('#detailCancelPledging #pledgingForm #securityCode').val(dataPledging.security.securityId);
				$('#detailCancelPledging #pledgingForm #securityKey').val(dataPledging.security.securityKey);
				$('#detailCancelPledging #pledgingForm #securityDesc').val(dataPledging.security.description);
				$('#detailCancelPledging #pledgingForm #pledgingQty').autoNumericSet(dataPledging.amount);
				$('#detailCancelPledging #pledgingForm #pledgingQtyStripped').val(dataPledging.amount);
				$('#detailCancelPledging #pledgingForm #transactionDetailKey').val(dataPledging.transactionDetailKey);
				$('#detailCancelPledging #pledgingForm #portfolioNo').val(dataPledging.holdingRefs);
				//$('#detailCancelPledging #pledgingForm #portfolioNoKey').val(dataPledging.portfolio.portfolioKey);
				//$('#detailCancelPledging #pledgingForm #portfolioNoName').val(dataPledging.portfolio.classification.lookupDescription);
				$('#detailCancelPledging #pledgingForm #classification').val(dataPledging.classification.lookupId);
				//$('#detailCancelPledging #pledgingForm #portfolioQuantity').autoNumericSet(dataPledging.portoQuantity);
				//$('#detailCancelPledging #pledgingForm #portfolioQuantityStripped').val(dataPledging.portoQuantity);
				//$('#detailCancelPledging #pledgingForm #availableQty').autoNumericSet(dataPledging.availableQuantity);
				//$('#detailCancelPledging #pledgingForm #availableQtyStripped').val(dataPledging.availableQuantity);

				$('.error').html("");
				calculateAvailableQty();
				var vAvailableQty = parseFloat($('#detailCancelPledging #pledgingForm #availableQtyStripped').val());
				var vBlockAmount = parseFloat($('#detailCancelPledging #pledgingForm #pledgingQtyStripped').val());
				
				$('#detailCancelPledging #pledgingForm #availableQty').autoNumericSet(vAvailableQty + vBlockAmount);
				$('#detailCancelPledging #pledgingForm #availableQtyStripped').val(vAvailableQty + vBlockAmount);
				
				$("#detailCancelPledging").dialog('open');
				$('.ui-widget-overlay').css('height',$('body').height());
			}
			
		});
	};
	
	
	function doSave(){
/*		if (tablePledging.fnGetNodes().length < 1){
			$('#errCancelPledgingDetail').html("*Error saving! Make sure there is a minimum of one data in 'Detail Pledging'");
			return false;
		}*/
		if ($('#closeCancelDate').hasClass('fieldError')){
			$('#closeCancelDate').focus();
			return false;
		}
			
		return true;
	};
	
	function calculateAvailableQty(){
		var holdingRefs = $('#portfolioNo').val();
		var accountKey = $('#accountKey').val();
		var securityKey = $('#securityKey').val();
		var portfolioQty = $('#portfolioQuantityStripped').val();
		
		$.ajax({
			url: '@{Pledgings.getAvailableQuantity}?holdingRefs='+holdingRefs+'&accountKey='+accountKey+'&securityKey='+securityKey+'&portfolioQty='+portfolioQty,
			success: function(data) {
				checkRedirect(data);
				$('#availableQty').autoNumericSet(data);
				$('#availableQtyStripped').val(data);
			}
		});
	}
	
	
	function validateCloseCancelDate(){
		var currentDate = new Date($('#currentDate').datepicker('getDate')).getTime();
		var cancelDate = new Date($('#closeCancelDate').datepicker('getDate')).getTime();
		var transDate = new Date($('#tabTransactionDate').datepicker('getDate')).getTime();
		//if (!$('#closeCancelDate').hasClass('fieldError')){
		
			if (cancelDate != '') {
				if (cancelDate > currentDate){
					$('#closeCancelDate').addClass('fieldError');
					$('#closeCancelDateError').html('Can not greater than Application Date');
					return false;
				} else if (cancelDate < transDate){
					$('#closeCancelDate').addClass('fieldError');
					$('#closeCancelDateError').html('Can not less than Transaction Date');
					return false;
				} else {
					$('#closeCancelDate').removeClass('fieldError');
					$('#closeCancelDateError').html('');
				}
			}
		//}
	}
	