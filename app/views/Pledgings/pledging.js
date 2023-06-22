$(function() {
		$('.calendar').datepicker();
		
		$('#transactionDate').change(function(){
			validatePledgingDate();
		});
		
		if (!$('#checkDueDate').is(':checked')){
			$('#dueDate').attr('disabled','disabled');
			$("p[id=pCheckDueDate] label span").html("");
			
		}
		
		var data = new Object();
		tabPledgingDetail(data);
		// ============================ PROCESS IN MAIN PLEDGING FORM ============================ //
		// CIF LOOKUP
		$('#cifCode').change(function(){
			if ($('#cifCode').val() == ""){
				$('#cifCode').removeClass('fieldError');
				$('#cifKey').val("");
				$('#cifDesc').val("");
				$('#h_cifDesc').val("");
				tablePledging.fnClearTable();
			}	
		});

		$('#cifCode').dynapopup2({key:'cifKey', help:'cifHelp', desc:'cifDesc'},'PICK_CF_MASTER', "", "pledgingAmount", 
			function(data){
				if (data) {
					$('#cifCode').removeClass('fieldError');
					$('#cifKey').val(data.code);
					$('#cifDesc').val(data.description);
					$('#h_cifDesc').val(data.description);
					
					tablePledging.fnClearTable();
				}
			},
			function(){
				$('#cifCode').addClass('fieldError');
				$('#cifKey').val('');
				$('#cifCode').val('');
				$('#cifDesc').val('NOT FOUND');
				$('#h_cifDesc').val('');
			}
		);

//		$('#cifCode').lookup({
//			list:'@{Pick.cifs()}',
//			get:{
//				url:'@{Pick.customer()}',
//				success: function(data){
//					$('#cifCode').removeClass('fieldError');
//					$('#cifKey').val(data.code);
//					$('#cifDesc').val(data.description);
//					$('#h_cifDesc').val(data.description);
//					
//					tablePledging.fnClearTable();
//				},
//				error: function(data){
//					$('#cifCode').addClass('fieldError');
//					$('#cifKey').val('');
//					$('#cifCode').val('');
//					$('#cifDesc').val('NOT FOUND');
//					$('#h_cifDesc').val('');
//				}
//			},
//			description:$('#cifDesc'),
//			help:$('#cifHelp')
//		});

		$('#checkDueDate').change(function(){
			if ($('#checkDueDate').is(':checked')){
				$('#dueDate').attr('disabled', false);
				$("p[id=pCheckDueDate] label span").html(" *");
			} else {
				$('#dueDate').attr('disabled','disabled');
				$("p[id=pCheckDueDate] label span").html("");
			}
			
		});

		// ========================== PROCESS IN DETAIL PLEDGING FORM ========================//
		

		$('#tabsDetailPledging').tabs();
		
		// DataTables section
		
		
		// end of DataTables
		
		$('.buttons button').button();
		$('.buttons input:button').button();
		$('.buttons #close').click(function() {
			$('#dialog').form('close');
		});
		
		$( "#detailPledging" ).dialog({
			autoOpen:false,
			modal:true,
			heigth:'650px',
			width:'750px',
			resizable:false
		});

		$("#accountType").children().eq(0).remove();
		
		$('#accountType').change(function(){
			/*if ($('#accountType').val()=="ACCOUNT_TYPE_PLD-2"){
				$('#divTypeCustody').css('display','none');
				$('#divTypeBank').css('display','block');
				$('#accountBankHelp').val('?');
				$('#divTypeInvestment').css('display','none');
			} else if ($('#accountType').val()=="ACCOUNT_TYPE_PLD-3"){
				$('#divTypeCustody').css('display','none');
				$('#divTypeBank').css('display','none');
				$('#divTypeInvestment').css('display','block');
				$('#accountInvHelp').val('?');
			} else {
				$('#divTypeCustody').css('display','block');
				$('#divTypeBank').css('display','none');
				$('#divTypeInvestment').css('display','none');
			}*/
		});
		
		$('.buttons #newPledgingData').live('click', function() {
			selectedRow = null;
			$("#detailPledging").dialog('open');
			
			//$("#detailPledging :text").val("");
			$("#detailPledging :text").removeClass("fieldError");
			//$("#detailPledging :hidden").val("");
			$('#detailPledging #pledgingForm #rowPosition').val('');
			$('#detailPledging #pledgingForm #oldPortoKey').val('');
			$('#detailPledging #pledgingForm #newPortoKey').val('');
			$('#detailPledging #pledgingForm #transactionDetailKey').val('');
			$('#detailPledging #pledgingForm #classification').val('');
			$('#detailPledging #pledgingForm #accountNo').val('');
			$('#detailPledging #pledgingForm #accountKey').val('');
			$('#detailPledging #pledgingForm #accountDesc').val('');
			$('#detailPledging #pledgingForm #securityType').val('');
			$('#detailPledging #pledgingForm #securityTypeDesc').val('');
			$('#detailPledging #pledgingForm #securityCode').val('');
			$('#detailPledging #pledgingForm #securityKey').val('');
			$('#detailPledging #pledgingForm #securityDesc').val('');
			$('#detailPledging #pledgingForm #portfolioNo').val('');
			$('#detailPledging #pledgingForm #portfolioNoKey').val('');
			$('#detailPledging #pledgingForm #portfolioQuantity').val('');
			$('#detailPledging #pledgingForm #portfolioQuantityStripped').val('');
			$('#detailPledging #pledgingForm #availableQty').val('');
			$('#detailPledging #pledgingForm #availableQtyStripped').val('');
			$('#detailPledging #pledgingForm #pledgingQty').val('');
			$('#detailPledging #pledgingForm #pledgingQtyStripped').val('');
			$(".error").html("");
			
			//$('#accountType').val('ACCOUNT_TYPE_PLD-1');
			/*$('#divTypeCustody').css('display','block');
			$('#divTypeBank').css('display','none');
			$('#divTypeInvestment').css('display','none');
			$('#accountHelp').val('?');
			$('#securityTypeHelp').val('?');
			$('#securityHelp').val('?');
			$('#portfolioNoHelp').val('?');*/
			$('.ui-widget-overlay').css('height',$('body').height());
			return false;
		});
		
		// === FOR ACCOUNT TYPE CUSTODIAN ====//
		$('#accountNo').change(function(){
			if ($('#accountNo').val() == ""){
				$('#accountNo').removeClass('fieldError');
				$('#accountKey').val("");
				$('#accountDesc').val("");
				$('#h_accountDesc').val("");
			}	
		});
		
		$('#accountNo').lookup({
			list:'@{Pick.accountsByCustomer()}',
			get:{
				url:'@{Pick.accountByCustomer()}',
				success: function(data){
					$('#accountNo').removeClass('fieldError');
					$('#accountKey').val(data.code);
					$('#accountDesc').val(data.description);
					$('#h_accountDesc').val(data.description);
				},
				error: function(data){
					$('#accountNo').addClass('fieldError');
					$('#accountKey').val('');
					$('#accountNo').val('');
					$('#accountDesc').val('NOT FOUND');
					$('#h_accountDesc').val('');
				}
			},
			filter : $('#cifKey'),
			description:$('#accountDesc'),
			help:$('#accountHelp')
		});

		$('#securityType').change(function(){
			if ($('#securityType').val() == ""){
				$('#securityType').removeClass('fieldError');
				$('#securityType').val("");
				$('#securityTypeDesc').val("");
				$('#h_securityTypeDesc').val("");
				clear();
			}	
			attachSecuritiesPaging();
		});
		
		$('#securityType').lookup({
			list:'@{Pick.securityTypes()}',
			get:{
				url:'@{Pick.securityType()}',
				success: function(data){
					$('#securityType').removeClass('fieldError');
					$('#securityType').val(data.code);
					$('#securityTypeDesc').val(data.description);
					$('#h_securityTypeDesc').val(data.description);
					
					clear();
					$('#securityType').change();
				},
				error: function(data){
					$('#securityType').addClass('fieldError');
					$('#securityType').val('');
					$('#securityType').val('');
					$('#securityTypeDesc').val('NOT FOUND');
					$('#h_securityTypeDesc').val('');
					$('#securityType').change();
				}
			},
			description:$('#securityTypeDesc'),
			help:$('#securityTypeHelp')
		});
		
		$('#securityCode').change(function(){
			if ($('#securityCode').val() == ""){
				$('#securityCode').removeClass('fieldError');
				clear();
			}	
		});
		
		function attachSecuritiesPaging() {
			var securityType = $('#securityType').val();
			var pickName = (securityType == '') ? 'PICK_SC_MASTER' : 'PICK_SC_MASTER_BY_SEC_TYPE';
			$('#securityCode').dynapopup2({key:'securityKey', help:'securityHelp', desc:'securityDesc'}, pickName, securityType, 'classification', function(data){
				$('#securityCode').removeClass('fieldError');
				$('#securityKey').val(data.code);
				$('#securityDesc').val(data.description);
				$('#h_securityDesc').val(data.description);
				
				$('#portfolioNo').val("");
				$('#h_portfolioNo').val("");
				$('#portfolioNoKey').val("");
				$('#portfolioNoName').val("");
				$('#h_portfolioNoName').val("");
				$('#classification').val("");
				$('#portfolioNo').val("");
				
				$('#pledgingQty').val("");
				$('#pledgingQtyStripped').val("");
				
				$('#portfolioQuantity').val("");
				$('#portfolioQuantityStripped').val("");
				
				$('#availableQty').val("");
				$('#availableQtyStripped').val("");
			},function(data){
				$('#securityCode').addClass('fieldError');
				$('#securityCode').val('');
				$('#securityKey').val('');
				$('#securityDesc').val('NOT FOUND');
				$('#h_securityDesc').val('');
			});
		}
		attachSecuritiesPaging();
		
//		$('#securityCode').lookup({
//			list:'@{Pick.securities()}',
//			get:{
//				url:'@{Pick.security()}',
//				success: function(data){
//					$('#securityCode').removeClass('fieldError');
//					$('#securityKey').val(data.code);
//					$('#securityDesc').val(data.description);
//					$('#h_securityDesc').val(data.description);
//					
//					$('#portfolioNo').val("");
//					$('#h_portfolioNo').val("");
//					$('#portfolioNoKey').val("");
//					$('#portfolioNoName').val("");
//					$('#h_portfolioNoName').val("");
//					$('#classification').val("");
//					$('#portfolioNo').val("");
//					
//					$('#pledgingQty').val("");
//					$('#pledgingQtyStripped').val("");
//					
//					$('#portfolioQuantity').val("");
//					$('#portfolioQuantityStripped').val("");
//					
//					$('#availableQty').val("");
//					$('#availableQtyStripped').val("");
//				},
//				error: function(data){
//					$('#securityCode').addClass('fieldError');
//					$('#securityCode').val('');
//					$('#securityKey').val('');
//					$('#securityDesc').val('NOT FOUND');
//					$('#h_securityDesc').val('');
//				}
//			},
//			filter:$('#securityType'),
//			description:$('#securityDesc'),
//			help:$('#securityHelp')
//		});
		
		$('#portfolioNo').change(function(){
			if ($('#portfolioNo').val() == ""){
				$('#portfolioNo').removeClass('fieldError');
				$('#portfolioNo').val("");
				$('#h_portfolioNo').val("");
				$('#portfolioNoKey').val("");
				$('#newPortoKey').val("");
				$('#portfolioQuantity').val("");
				$('#portfolioQuantityStripped').val("");
				$('#availableQty').val("");
				$('#availableQtyStripped').val("");
			}	
		});
		
		// fungsi clear classification
		$('#classification').change(function(){
			
			$('#portfolioNo').removeClass('fieldError');
			$('#portfolioNo').val('');
			$('#portfolioNoKey').val('');
			$('#portfolioQuantity').val('');
			$('#portfolioQuantityStripped').val('');
			$('#availableQty').val('');
			$('#availableQtyStripped').val('');
			$('#pledgingQty').val('');
			$('#pledgingQtyStripped').val('');
			
//			if ($('#portfolioNo').val() == ""){
//				$('#portfolioNo').removeClass('fieldError');
//				$('#portfolioNo').val("");
//				$('#h_portfolioNo').val("");
//				$('#portfolioNoKey').val("");
//				$('#newPortoKey').val("");
//				$('#portfolioQuantity').val("");
//				$('#portfolioQuantityStripped').val("");
//				$('#availableQty').val("");
//				$('#availableQtyStripped').val("");
//			}	
		});
		
		
		$('#portfolioNo').lookup({
			list : '@{Pick.pledingPortfolios()}',
			get : {
				url : '@{Pick.pledingPortfolio()}',
				success: function(data){
					$('#portfolioNo').removeClass('fieldError');
					$('#portfolioNo').val(data.holdingRefs);
					$('#portfolioNoKey').val(data.code);
					$('#newPortoKey').val(data.code);
					$('#portfolioQuantity').autoNumericSet(data.totalQuantity);
					$('#portfolioQuantityStripped').val(data.totalQuantity);
					$('#availableQty').autoNumericSet(data.portoQuantity);
					$('#availableQtyStripped').val(data.portoQuantity);
				},
				error: function(data){
					$('#portfolioNo').addClass('fieldError');
					$('#portfolioNo').val('');
					$('#portfolioNoKey').val('');
					$('#portfolioQuantity').val('NOT FOUND');
					$('#portfolioQuantityStripped').val('');
					$('#availableQty').val('');
					$('#availableQtyStripped').val('');
				}
			},
			filter:[$('#accountKey'), $('#securityType'),
			        $('#securityKey'), $('#classification')],
			description:$('#portfolioQuantity'),
			help:$('#portfolioNoHelp')
		});
		
		$('#pledgingQty').blur(function(){
			var pledQty = parseFloat($('#pledgingQtyStripped').val());
			var availableQty = parseFloat($('#availableQtyStripped').val());
			
			if (pledQty > availableQty){
				$('#pledgingQty').addClass("fieldError");
				$('#pledgingQtyError').html("Can not greater than Available Qty");
				return false;
			} else {
				$('#pledgingQty').removeClass("fieldError");
				$('#pledgingQtyError').html("");
			}
			
		});
		
		// ACTION SAVE PLEDGING DETAIL
		$('#savePledging').die("click");
		$('#savePledging').live("click", function(){
			var table = $('#listPledging #gridPledging').dataTable();
			var rowPosition = $('#detailPledging #pledgingForm #rowPosition').val();
			if ($('#pledgingQty').hasClass('fieldError')){
				return false;
			}
			if (($('#accountNo').val()=='')||($('#securityType').val()=='')||($('#securityCode').val()=='')||($('#portfolioNo').val()=='')||($('#pledgingQty').val()=='')||($('#classification').val()=='')){
				if ($('#accountNo').val()==''){
					$("#accountNoError").html('Required');
				}
				if ($('#securityType').val()==''){
					$("#securityTypeError").html('Required');
				}
				if ($('#securityCode').val()==''){
					$("#securityCodeError").html('Required');
				}
				if ($('#portfolioNo').val()==''){
					$("#portfolioNoError").html('Required');
				}
				if ($('#pledgingQty').val()==''){
					$("#pledgingQtyError").html('Required');
				}
				if ($('#classification').val()==''){
					$("#classificationError").html('Required');
				}
				
				return false;
			}
			
			var dataPledging = table.fnGetData(parseFloat(rowPosition));
			var portoKey = $('#detailPledging #pledgingForm #portfolioNoKey').val();
			var oldPortoKey = $('#detailPledging #pledgingForm #oldPortoKey').val();
			var newPortoKey = $('#detailPledging #pledgingForm #newPortoKey').val();
			var errMsg = "Porto No. already exist!";
			if (rowPosition != "") {
				var found = false;
				var rows = table.fnGetNodes().length;
				for(i=0; i<rows; i++) {
					var cells = table.fnGetData(i);
					if ((cells.portfolio.portfolioKey == portoKey)&&(oldPortoKey!=newPortoKey)){
						$('#errDetailPledGlobal').html(errMsg);
						return false;
					}
				}
				
				if (!found) {
					
					//seharusnya pake yang ini
					//table.fnUpdate(((dataPledging.accountType.lookupDescription = $('#detailPledging #pledgingForm #accountTypeDesc').val())&&(dataPledging.accountType.lookupId = $('#detailPledging #pledgingForm #accountType').val())), parseFloat(rowPosition), 0);
					//sementara
					table.fnUpdate(((dataPledging.accountType.lookupDescription = $('#detailPledging #pledgingForm #accountTypeDesc').val())&&(dataPledging.accountType.lookupId = 'ACCOUNT_TYPE_PLD-1')), parseFloat(rowPosition), 0);
					table.fnUpdate(((dataPledging.account.accountNo = $('#detailPledging #pledgingForm #accountNo').val())&&(dataPledging.account.custodyAccountKey = $('#detailPledging #pledgingForm #accountKey').val())), parseFloat(rowPosition), 1);
					table.fnUpdate((dataPledging.account.name = $('#detailPledging #pledgingForm #accountDesc').val()), parseFloat(rowPosition), 2);
					table.fnUpdate((dataPledging.amount = $('#detailPledging #pledgingForm #pledgingQtyStripped').val()), parseFloat(rowPosition), 3);
					
					dataPledging.accountType.lookupId = $("#detailPledging #pledgingForm #accountType").val();
					dataPledging.transactionDetailKey = $("#detailPledging #pledgingForm #transactionDetailKey").val();
					dataPledging.security.securityType.securityType = $('#detailPledging #pledgingForm #securityType').val();
					dataPledging.security.securityType.description = $('#detailPledging #pledgingForm #securityTypeDesc').val();
					dataPledging.security.securityId = $('#detailPledging #pledgingForm #securityCode').val();
					dataPledging.security.securityKey = $('#detailPledging #pledgingForm #securityKey').val();
					dataPledging.security.description = $('#detailPledging #pledgingForm #securityDesc').val();
					dataPledging.portfolio.portfolioKey = $('#detailPledging #pledgingForm #portfolioNo').val();
					dataPledging.portfolio.portfolioKey = $('#detailPledging #pledgingForm #portfolioNoKey').val();
					//dataPledging.portfolio.classification.lookupDescription = $('#detailPledging #pledgingForm #portfolioNoName').val();
					dataPledging.classification.lookupId = $('#detailPledging #pledgingForm #classification').val();
					dataPledging.holdingRefs = $('#detailPledging #pledgingForm #portfolioNo').val();
					dataPledging.portoQuantity = $('#detailPledging #pledgingForm #portfolioQuantityStripped').val();
					dataPledging.availableQuantity = $('#detailPledging #pledgingForm #availableQtyStripped').val();
					table.fnUpdate(dataPledging, parseFloat(rowPosition), 5);
					
					$('#detailPledging').dialog('close');
					return false;
				}
			} else {
				
				var found = false;
				var rows = table.fnGetNodes().length;
				for(i=0; i<rows; i++) {
					var cells = table.fnGetData(i);
					if (cells.portfolio.portfolioKey == portoKey){
						$('#errDetailPledGlobal').html(errMsg);
						return false;
					}
				}
				if (!found) {
					var dataPledging = new Object();
					dataPledging.accountType = new Object();
					dataPledging.account = new Object();
					dataPledging.security = new Object();
					dataPledging.security.securityType = new Object();
					dataPledging.portfolio = new Object();
					dataPledging.portfolio.classification = new Object();
					dataPledging.classification = new Object();
					
					var selected = $('#pledgingForm #accountType').val();
					var selectedText = $('#pledgingForm #accountType option[value=' + selected + ']').text();
					if (selectedText != '') {
						selectedText = jQuery.trim(selectedText);
						selectedText = selectedText.substring(jQuery.trim(selectedText).indexOf('-') + 1);
					}
					
					dataPledging.accountType.lookupDescription = selectedText;
					//seharusnya pake yg ini
//					dataPledging.accountType.lookupId = $('#detailPledging #pledgingForm #accountType').val();
					//sementara
					dataPledging.accountType.lookupId = 'ACCOUNT_TYPE_PLD-1';
					dataPledging.account.accountNo = $('#detailPledging #pledgingForm #accountNo').val();
					dataPledging.account.custodyAccountKey = $('#detailPledging #pledgingForm #accountKey').val();
					dataPledging.account.name = $('#detailPledging #pledgingForm #accountDesc').val();
					dataPledging.security.securityType.securityType = $('#detailPledging #pledgingForm #securityType').val();
					dataPledging.security.securityType.description = $('#detailPledging #pledgingForm #securityTypeDesc').val();
					dataPledging.security.securityId = $('#detailPledging #pledgingForm #securityCode').val();
					dataPledging.security.securityKey = $('#detailPledging #pledgingForm #securityKey').val();
					dataPledging.security.description = $('#detailPledging #pledgingForm #securityDesc').val();
					dataPledging.amount = $('#detailPledging #pledgingForm #pledgingQtyStripped').val();
					dataPledging.transactionDetailKey = $('#detailPledging #pledgingForm #transactionDetailKey').val();
					dataPledging.portfolio.portfolioKey = $('#detailPledging #pledgingForm #portfolioNo').val();
					dataPledging.portfolio.portfolioKey = $('#detailPledging #pledgingForm #portfolioNoKey').val();
					//dataPledging.portfolio.classification.lookupDescription = $('#detailPledging #pledgingForm #portfolioNoName').val();
					dataPledging.classification.lookupId = $('#detailPledging #pledgingForm #classification').val();
					dataPledging.holdingRefs = $('#detailPledging #pledgingForm #portfolioNo').val();
					dataPledging.portoQuantity = $('#detailPledging #pledgingForm #portfolioQuantityStripped').val();
					dataPledging.availableQuantity = $('#detailPledging #pledgingForm #availableQtyStripped').val();
					table.fnAddData(dataPledging);
					$('#detailPledging').dialog('close');
					return false;
				}
			}
		});

		$( "#cancelPledging" ).click(function() {
			$('#detailPledging').dialog('close');
			return false;
		});
		
		var closeDialog = function() {
			$("#dialog-message").dialog("close");
			return false;
		};
		
		$('#listPledging #gridPledging tbody tr #deleteButton').live('click', function() {
			var row = $(this).parents('tr');
			var rowNumber = tablePledging.fnGetPosition(row[0]);
			var deletePledging = function(){
				tablePledging.fnDeleteRow(rowNumber);
				var idTable = $("#listPledging #gridPledging");
				var trs = $("tr", idTable);
				$.each(trs, function(idx, data){
					var hiddens = $("[type=hidden]", $(this));
						$(hiddens).eq(0).attr("name", "pledgingDetails["+(idx-1)+"].account.accountNo");
						$(hiddens).eq(1).attr("name", "pledgingDetails["+(idx-1)+"].account.custodyAccountKey");
						$(hiddens).eq(2).attr("name", "pledgingDetails["+(idx-1)+"].account.name");
						$(hiddens).eq(3).attr("name", "pledgingDetails["+(idx-1)+"].security.securityType.securityType");
						$(hiddens).eq(4).attr("name", "pledgingDetails["+(idx-1)+"].security.securityType.description");
						$(hiddens).eq(5).attr("name", "pledgingDetails["+(idx-1)+"].security.securityKey");
						$(hiddens).eq(6).attr("name", "pledgingDetails["+(idx-1)+"].security.securityId");
						$(hiddens).eq(7).attr("name", "pledgingDetails["+(idx-1)+"].security.description");
						$(hiddens).eq(8).attr("name", "pledgingDetails["+(idx-1)+"].amount");
						$(hiddens).eq(9).attr("name", "pledgingDetails["+(idx-1)+"].transactionDetailKey");
						$(hiddens).eq(10).attr("name", "pledgingDetails["+(idx-1)+"].account.lookupId");
						$(hiddens).eq(11).attr("name", "pledgingDetails["+(idx-1)+"].portfolio.portfolioKey");
						//$(hiddens).eq(13).attr("name", "pledgingDetails["+(idx-1)+"].portfolio.classification.lookupDescription");
						$(hiddens).eq(12).attr("name", "pledgingDetails["+(idx-1)+"].classification.lookupId");
						$(hiddens).eq(13).attr("name", "pledgingDetails["+(idx-1)+"].holdingRefs");
						$(hiddens).eq(14).attr("name", "pledgingDetails["+(idx-1)+"].portoQuantity");
						$(hiddens).eq(15).attr("name", "pledgingDetails["+(idx-1)+"].availableQuantity");
				});
				$("#dialog-message").dialog("close");
				return false;
			}
			messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deletePledging, closeDialog);
		
		});
		
		$('#autoRelease').change(function(){
			if ($('#autoRelease').is(':checked')){
				$('p[id=pReleaseDate] label span').html(' *');
			} else {
				$('p[id=pReleaseDate] label span').html('');
			}
		});
		
	}); // end ready function


	function tabPledgingDetail(data) {
		var detailPledgings;
		if ('${detailPledgings}' != null )
			detailPledgings = ${detailPledgings.raw()};

		if ('${detailPledgings}' == null )
			detailPledgings = new Array();
		
		tablePledging = 
			$('#listPledging #gridPledging').dataTable({
				aaData: detailPledgings,
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
								 	controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].transactionDetailKey" value="' + obj.aData.transactionDetailKey + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].accountType.lookupId" value="' + obj.aData.accountType.lookupId + '" />';
								 	controls += '<input type="hidden" name="pledgingDetails[' + obj.iDataRow + '].portfolio.portfolioKey" value="' + obj.aData.portfolio.portfolioKey + '" />';
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
		
		$('#listPledging #gridPledging ').removeAttr('style'); // tembak nilai widht untuk tampilan
		$('#listPledging #gridPledging tbody tr td').die('click');
		$('#listPledging #gridPledging tbody tr td').live('click', function() {
			var txt = $(this).text();
			if(txt == 'No data available in table'){
				return false;
			}
			else{
			var rowPos = $(this).parents('tr');
			var rowPosNumber = tablePledging.fnGetPosition(rowPos[0]);
			var pos = tablePledging.fnGetPosition(this);
			cell = tablePledging.fnGetData(this.parentNode);
			if (pos[1] != 3) {
				
				dataPledging = tablePledging.fnGetData(this.parentNode);
				
				$('#detailPledging #pledgingForm #rowPosition').val(rowPosNumber);
				$('#detailPledging #pledgingForm #accountType').val(dataPledging.accountType.lookupId);
				$('#detailPledging #pledgingForm #accountTypeDesc').val(dataPledging.accountType.lookupDescription);
				$('#detailPledging #pledgingForm #accountNo').val(dataPledging.account.accountNo);
				$('#detailPledging #pledgingForm #accountKey').val(dataPledging.account.custodyAccountKey);
				$('#detailPledging #pledgingForm #accountDesc').val(dataPledging.account.name);
				$('#detailPledging #pledgingForm #securityType').val(dataPledging.security.securityType.securityType);
				$('#detailPledging #pledgingForm #securityTypeDesc').val(dataPledging.security.securityType.description);
				$('#detailPledging #pledgingForm #securityCode').val(dataPledging.security.securityId);
				$('#detailPledging #pledgingForm #securityKey').val(dataPledging.security.securityKey);
				$('#detailPledging #pledgingForm #securityDesc').val(dataPledging.security.description);
				$('#detailPledging #pledgingForm #pledgingQty').autoNumericSet(dataPledging.amount);
				$('#detailPledging #pledgingForm #pledgingQtyStripped').val(dataPledging.amount);
				$('#detailPledging #pledgingForm #transactionDetailKey').val(dataPledging.transactionDetailKey);
				$('#detailPledging #pledgingForm #portfolioNoKey').val(dataPledging.portfolio.portfolioKey);
				$('#detailPledging #pledgingForm #oldPortoKey').val($('#detailPledging #pledgingForm #portfolioNoKey').val());
				$('#detailPledging #pledgingForm #newPortoKey').val($('#detailPledging #pledgingForm #oldPortoKey').val());
				//$('#detailPledging #pledgingForm #portfolioNoName').val(dataPledging.portfolio.classification.lookupDescription);
				$('#detailPledging #pledgingForm #classification').val(dataPledging.classification.lookupId);
				$('#detailPledging #pledgingForm #portfolioNo').val(dataPledging.holdingRefs);
				$('#detailPledging #pledgingForm #portfolioQuantity').autoNumericSet(dataPledging.portoQuantity);
				$('#detailPledging #pledgingForm #portfolioQuantityStripped').val(dataPledging.portoQuantity);
				$('#detailPledging #pledgingForm #availableQty').autoNumericSet(dataPledging.availableQuantity);
				$('#detailPledging #pledgingForm #availableQtyStripped').val(dataPledging.availableQuantity);
				calculateAvailableQty();
				$('.error').html("");
				$("#detailPledging").dialog('open');
				$('.ui-widget-overlay').css('height',$('body').height());
			}
		  }
		});
	};
	
	
	function doSave(){
		if ($('#transactionDate').hasClass('fieldError')){
			$('#transactionDate').focus();
			return false;
		}
		
		if (tablePledging.fnGetNodes().length < 1){
			$('#errPledgingDetail').html("*Error saving! Make sure there is a minimum of one data in 'Detail Pledging'");
			return false;
		}
		
		return true;
	};
	
	function calculateAvailableQty(){
		var holdingRefs = $('#portfolioNo').val();
		var accountKey = $('#accountKey').val();
		var securityKey = $('#securityKey').val();
		var portfolioQty = $('#portfolioQuantityStripped').val();
		var transNo = $('#transactionNo').val();
		
		$.ajax({
			url: '@{Pledgings.getAvailableQuantity}?holdingRefs='+holdingRefs+'&accountKey='+accountKey+'&securityKey='+securityKey+'&portfolioQty='+portfolioQty+'&transNo='+transNo,
			success: function(data) {
				checkRedirect(data);
				$('#availableQty').autoNumericSet(data);
				$('#availableQtyStripped').val(data);
			}
		});
	}
	
	function clear(){
		
		
		$('#securityCode').val("");
		$('#securityKey').val("");
		$('#securityDesc').val("");
		$('#h_securityDesc').val("");
		
		$('#portfolioNo').val("");
		$('#h_portfolioNo').val("");
		$('#portfolioNoKey').val("");
		$('#portfolioNoName').val("");
		$('#h_portfolioNoName').val("");
		$('#classification').val("");
		$('#portfolioNo').val("");
		
		$('#pledgingQty').val("");
		$('#pledgingQtyStripped').val("");
		
		$('#portfolioQuantity').val("");
		$('#portfolioQuantityStripped').val("");
		
		$('#availableQty').val("");
		$('#availableQtyStripped').val("");
	}
	
	function validatePledgingDate(){
		var currentDate = new Date($('#currentDate').datepicker('getDate')).getTime();
		var transDate = new Date($('#transactionDate').datepicker('getDate')).getTime();
		if (!$('#transactionDate').hasClass('fieldError')){
			if (transDate != '') {
				if (transDate > currentDate){
					$('#transactionDate').addClass('fieldError');
					$('#transactionDateError').html('Can not greater than Application Date');
					return false;
				} else {
					$('#transactionDate').removeClass('fieldError');
					$('#transactionDateError').html('');
				}
			}
		}
	}