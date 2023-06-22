$(function() {
		$('.calendar').datepicker();
		
		var otherToBase = '${inputothertobaseval}';
		var baseToOther = '${inputbasetootherval}';
		
		if (('${mode}'==='entry')||(('${mode}'==='edit')&&(('${currencyProfile?.recordStatus?.decodeStatus()}'==='Reject')))){
			$('input[name=active]').attr("disabled", "disabled");
		}
		
		$('input[name=active]').change(function(){
			$("input[name='currencyProfile.active']").val($("input[name='active']:checked").val());
		});
		
		if ($('#inputMethodHidden').val()==otherToBase){
			$("input[name='inputMethod']")[0].checked = true;
		}
		
		if ($('#inputMethodHidden').val()==baseToOther){
			$("input[name='inputMethod']")[1].checked = true;
		}
		
		$('#inputMethod1').click(function(){
			$("input[name='inputMethod']")[0].checked = true;
			$('#inputMethodHidden').val($(this).val());
			
			// copy base to other
			var table = $('#listCurrencyExchangeDetail #gridCurrencyExchange').dataTable();
			var rows = table.fnGetNodes().length;
			for(i=0; i<rows; i++) {
				var cells = table.fnGetData(i);
				cells.sourceCurrency = new Object();
				
				if ((cells.targetCurrency.currencyCode !== '') && (cells.sourceCurrency.currencyCode == undefined)) {
					$('#entryOtherToBase').css("display","");
					$('#entryBaseToOther').css("display","none");
					
					table.fnUpdate(((cells.sourceCurrency.currencyCode = cells.targetCurrency.currencyCode)/* && (cells.targetCurrency.currencyCode = undefined)*/ && (cells.sourceCurrency.description = cells.targetCurrency.description)/* && (cells.targetCurrency.description = undefined)*/), parseFloat(i), 0);
				}
				
				/*var tempUnit = cells.unitCurrency;
				var tempExch = cells.exchangeRate;
				
				table.fnUpdate((cells.unitCurrency = tempExch), parseFloat(i), 1);
				table.fnUpdate((cells.exchangeRate = tempUnit), parseFloat(i), 2);*/
			}
		});
		
		$('#inputMethod2').click(function(){
			$("input[name='inputMethod']")[1].checked = true;
			$('#inputMethodHidden').val($(this).val());
			
			// copy other to base
			var table = $('#listCurrencyExchangeDetail #gridCurrencyExchange').dataTable();
			var rows = table.fnGetNodes().length;
			for(i=0; i<rows; i++) {
				var cells = table.fnGetData(i);
				cells.targetCurrency = new Object();
				
				if ((cells.sourceCurrency.currencyCode !== '') && (cells.targetCurrency.currencyCode == undefined)) {
					$('#entryBaseToOther').css("display","");
					$('#entryOtherToBase').css("display","none");
					
					table.fnUpdate(((cells.targetCurrency.currencyCode = cells.sourceCurrency.currencyCode)/* && (cells.sourceCurrency.currencyCode = undefined)*/ && (cells.targetCurrency.description = cells.sourceCurrency.description)/* && (cells.sourceCurrency.description = undefined)*/), parseFloat(i), 0);
				}
				
				/*var tempUnit = cells.unitCurrency;
				var tempExch = cells.exchangeRate;
				
				table.fnUpdate((cells.unitCurrency = tempExch), parseFloat(i), 1);
				table.fnUpdate((cells.exchangeRate = tempUnit), parseFloat(i), 2);*/
			}
		});
		
		var arrBaseCurr = [$('#baseCurrency').val()];
		
		$('#baseCurrency').change(function(){
			if ($('#baseCurrency').val() === "") {
				$('#baseCurrencyDesc').val("");
				$('#h_baseCurrencyDesc').val("");
			}
			
			if ('${mode}'==='edit') {
				var unchangedGridExchangeRate = function() {
					var prevBaseCurrency = "";
					if (arrBaseCurr.length >= 2) {
						prevBaseCurrency = arrBaseCurr[arrBaseCurr.length - 2];
					} else {
						prevBaseCurrency = arrBaseCurr[0];
					}
					$('#baseCurrency').val(prevBaseCurrency);
					$.get("@{Pick.currency()}?code="+prevBaseCurrency, function(data) {
						checkRedirect(data);
						$('#baseCurrencyDesc').val(data.description);
						$('#h_baseCurrencyDesc').val(data.description);
					});
					if (arrBaseCurr.length > 1) {
						arrBaseCurr.pop();
					}
					$("#dialog-message").dialog("close");
					
					return false;
				};
				
				var deleteGridExchangeRate = function(){
					tableExchangeRate.fnClearTable();
					
					$("#dialog-message").dialog("close");
					
					return false;
				};
				
				if (((arrBaseCurr[arrBaseCurr.length - 2] !== "") && (arrBaseCurr[arrBaseCurr.length - 2] != undefined)) && ((arrBaseCurr[arrBaseCurr.length - 1] !== "") && (arrBaseCurr[arrBaseCurr.length - 1] != undefined)) && (arrBaseCurr[arrBaseCurr.length - 2] !== arrBaseCurr[arrBaseCurr.length - 1])) {
					messageAlertYesNo("Are you sure to delete all Currency Exchange data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteGridExchangeRate, unchangedGridExchangeRate);
				}
				
			}
		});

		$('#baseCurrency').lookup({
			list:'@{Pick.currencies()}',
			get:{
				url:'@{Pick.currency()}',
				success: function(data) {
					if (data) {
						$('#baseCurrency').removeClass('fieldError');
						$('#baseCurrency').val(data.code);
						$('#baseCurrencyDesc').val(data.description);
						$('#h_baseCurrencyDesc').val(data.description);
						
						arrBaseCurr.push(data.code);
						
						if ('${mode}'==='edit') {
							var unchangedGridExchangeRate = function() {
								var prevBaseCurrency = "";
								if (arrBaseCurr.length >= 2) {
									prevBaseCurrency = arrBaseCurr[arrBaseCurr.length - 2];
								} else {
									prevBaseCurrency = arrBaseCurr[0];
								}
								$('#baseCurrency').val(prevBaseCurrency);
								$.get("@{Pick.currency()}?code="+prevBaseCurrency, function(data) {
									checkRedirect(data);
									$('#baseCurrencyDesc').val(data.description);
									$('#h_baseCurrencyDesc').val(data.description);
								});
								if (arrBaseCurr.length > 1) {
									arrBaseCurr.pop();
								}
								$("#dialog-message").dialog("close");
								
								return false;
							};
							
							var deleteGridExchangeRate = function(){
								tableExchangeRate.fnClearTable();
								
								$("#dialog-message").dialog("close");
								
								return false;
							};
							
							if (((arrBaseCurr[arrBaseCurr.length - 2] !== "") && (arrBaseCurr[arrBaseCurr.length - 2] != undefined)) && ((arrBaseCurr[arrBaseCurr.length - 1] !== "") && (arrBaseCurr[arrBaseCurr.length - 1] != undefined)) && (arrBaseCurr[arrBaseCurr.length - 2] !== arrBaseCurr[arrBaseCurr.length - 1])) {
								messageAlertYesNo("Are you sure to delete all Currency Exchange data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteGridExchangeRate, unchangedGridExchangeRate);
							}
						}
					}
				},
				error : function(data) {
					$('#baseCurrency').addClass('fieldError');
					$('#baseCurrencyDesc').val('NOT FOUND');
					$('#baseCurrency').val('');
					$('#h_baseCurrencyDesc').val('');
				}
			},
			key:$('#baseCurrency'),
			description:$('#baseCurrencyDesc'),
			help:$('#baseCurrencyHelp')
		});
		
		$('#sourceCurrency').change(function(){
			if ($('#sourceCurrency').val() === "") {
				$('#sourceCurrencyDesc').val("");
				$('#h_sourceCurrencyDesc').val("");
			}
		});

		$('#sourceCurrency').lookup({
			list:'@{Pick.currenciesWithExclude()}',
			get:{
				url:'@{Pick.currencyWithExclude()}',
				success: function(data) {
					if (data) {
						$('#sourceCurrency').removeClass('fieldError');
						$('#sourceCurrency').val(data.code);
						$('#sourceCurrencyDesc').val(data.description);
						$('#h_sourceCurrencyDesc').val(data.description);
					}
				},
				error : function(data) {
					$('#sourceCurrency').addClass('fieldError');
					$('#sourceCurrencyDesc').val('NOT FOUND');
					$('#sourceCurrency').val('');
					$('#h_sourceCurrencyDesc').val('');
				}
			},
			filter:$('#baseCurrency'),
			key:$('#sourceCurrency'),
			description:$('#sourceCurrencyDesc'),
			help:$('#sourceCurrencyHelp')
		});
		
		$('#targetCurrency').change(function(){
			if ($('#targetCurrency').val() === "") {
				$('#targetCurrencyDesc').val("");
				$('#h_targetCurrencyDesc').val("");
			}
		});

		$('#targetCurrency').lookup({
			list:'@{Pick.currenciesWithExclude()}',
			get:{
				url:'@{Pick.currencyWithExclude()}',
				success: function(data) {
					if (data) {
						$('#targetCurrency').removeClass('fieldError');
						$('#targetCurrency').val(data.code);
						$('#targetCurrencyDesc').val(data.description);
						$('#h_targetCurrencyDesc').val(data.description);
					}
				},
				error : function(data) {
					$('#targetCurrency').addClass('fieldError');
					$('#targetCurrencyDesc').val('NOT FOUND');
					$('#targetCurrency').val('');
					$('#h_targetCurrencyDesc').val('');
				}
			},
			filter:$('#baseCurrency'),
			key:$('#targetCurrency'),
			description:$('#targetCurrencyDesc'),
			help:$('#targetCurrencyHelp')
		});
		
		var exchangeRateRef = 'EXCHANGE_RATE_REF';
		$('#exchangeRateRefCode').change(function(){
			if ($('#exchangeRateRefCode').val() === ""){
				$('#exchangeRateRef').val("");
				$('#exchangeRateRefDesc').val("");
				$('#h_exchangeRateRefDesc').val("");
			}
		});
		
		$('#exchangeRateRefCode').lookup({
			list:'@{Pick.lookups()}?group='+exchangeRateRef,
			get:{
				url:'@{Pick.lookup()}?group='+exchangeRateRef,
				success: function(data) {
					if (data) {
						$('#exchangeRateRefCode').removeClass('fieldError');
						$('#exchangeRateRefCode').val(data.lookupCode);
						$('#exchangeRateRef').val(data.code);
						$('#exchangeRateRefDesc').val(data.description);
						$('#h_exchangeRateRefDesc').val(data.description);
					}
				},
				error : function(data) {
					$('#exchangeRateRefCode').addClass('fieldError');
					$('#exchangeRateRefDesc').val('NOT FOUND');
					$('#exchangeRateRefCode').val('');
					$('#exchangeRateRef').val('');
					$('#h_exchangeRateRefDesc').val('');
				}
			},
			key:$('#exchangeRateRef'),
			description:$('#exchangeRateRefDesc'),
			help:$('#exchangeRateRefHelp')
		});

		var frmDivCurrencyExchangeOtherToBase = $('#divCurrencyExchangeOtherToBase');
		var frmDivCurrencyExchangeBaseToOther = $('#divCurrencyExchangeBaseToOther');
		
		var data = new Object();
		tabCurrencyExchangeDetail(data);

		// ========================== PROCESS IN DETAIL CURRENCY EXCHANGE FORM ========================//
		

		$('#tabsDetailCurrencyExchange').tabs();
		
		$('.buttons button').button();
		$('.buttons input:button').button();
		
		$( "#detailCurrencyExchange" ).dialog({
			autoOpen:false,
			modal:true,
			heigth:'650px',
			width:'750px',
			resizable:false
		});
		
		$('.buttons #newCurrencyExchangeDetailData').live('click', function() {
			selectedRow = null;
			$("#detailCurrencyExchange").dialog('open');
			$("#detailCurrencyExchange :text").removeClass("fieldError");
			$("#detailCurrencyExchange :text").val('');
			$("#detailCurrencyExchange :hidden").val('');
			$(".error").html("");
			
			if ($('#inputMethodHidden').val()==='') {
				$("#errCurrencyExchangeDetail").html('Please choose Input Method before Add, Delete or Choose Exchange Rate Item');
				
				$('#detailCurrencyExchange').dialog('close');
				return false;
			}
			
			if ($('#baseCurrency').val()==='') {
				$("#errCurrencyExchangeDetail").html('Please choose Base Currency before Add, Delete or Choose Exchange Rate Item');
				
				$('#detailCurrencyExchange').dialog('close');
				return false;
			}
			
			if ($('#inputMethodHidden').val() === otherToBase) {
				frmDivCurrencyExchangeBaseToOther.css("display","none");
				frmDivCurrencyExchangeOtherToBase.css("display","");
				$('#sourceCurrencyHelp').val('?');
			} else if ($('#inputMethodHidden').val()=== baseToOther) {
				frmDivCurrencyExchangeOtherToBase.css("display","none");
				frmDivCurrencyExchangeBaseToOther.css("display","");
				$('#targetCurrencyHelp').val('?');
			}
			
			$('#detailCurrencyExchange #currencyExchangeForm #unitCurrency').autoNumericSet('0');
			$('#detailCurrencyExchange #currencyExchangeForm #unitCurrencyStripped').val(0);
			$('#detailCurrencyExchange #currencyExchangeForm #exchangeRate').autoNumericSet('0');
			$('#detailCurrencyExchange #currencyExchangeForm #exchangeRateStripped').val(0);
			$('.ui-widget-overlay').css('height',$('body').height());
			return false;
		});
		
		// ACTION SAVE CURRENCY EXCHANGE DETAIL
		$('#saveCurrencyExchangeDetail').die("click");
		$('#saveCurrencyExchangeDetail').live("click", function(){
			var table = $('#listCurrencyExchangeDetail #gridCurrencyExchange').dataTable();
			var rowPosition = $('#detailCurrencyExchange #currencyExchangeForm #rowPosition').val();
			
			if ((($('#inputMethodHidden').val() === otherToBase) && ($('#sourceCurrency').val()==='')) || (($('#inputMethodHidden').val()=== baseToOther) && ($('#targetCurrency').val()==='')) || ($('#unitCurrency').val()==='') || ($('#exchangeRate').val()==='')) {
				if (($('#inputMethodHidden').val() === otherToBase) && ($('#sourceCurrency').val()==='')){
					$("#sourceCurrencyError").html('Required');
				}
				if (($('#inputMethodHidden').val()=== baseToOther) && ($('#targetCurrency').val()==='')){
					$("#targetCurrencyError").html('Required');
				}
				if ($('#unitCurrency').val()===''){
					$("#unitCurrencyError").html('Required');
				}
				if ($('#exchangeRate').val()===''){
					$("#exchangeRateError").html('Required');
				}
				return false;
			}
			
			var dataCurrencyExchange = table.fnGetData(parseFloat(rowPosition));
			var sourceCurrency = $('#detailCurrencyExchange #currencyExchangeForm #sourceCurrency').val();
			var oldSourceCurrency = $('#detailCurrencyExchange #currencyExchangeForm #oldSourceCurrency').val();
			var newSourceCurrency = $('#detailCurrencyExchange #currencyExchangeForm #newSourceCurrency').val();
			var targetCurrency = $('#detailCurrencyExchange #currencyExchangeForm #targetCurrency').val();
			var oldTargetCurrency = $('#detailCurrencyExchange #currencyExchangeForm #oldTargetCurrency').val();
			var newTargetCurrency = $('#detailCurrencyExchange #currencyExchangeForm #newTargetCurrency').val();
			var errMsg = "Other Currency already exist!";
			if (rowPosition != "") {
				var found = false;
				var rows = table.fnGetNodes().length;
				for(i=0; i<rows; i++) {
					var cells = table.fnGetData(i);
					if (($('#inputMethodHidden').val() === otherToBase) && (cells.sourceCurrency.currencyCode == sourceCurrency)&&(oldSourceCurrency!=newSourceCurrency)){
						$('#errDetailCurrExchgGlobal').html(errMsg);
						return false;
					}
					if (($('#inputMethodHidden').val()=== baseToOther) && (cells.targetCurrency.currencyCode == targetCurrency)&&(oldTargetCurrency!=newTargetCurrency)){
						$('#errDetailCurrExchgGlobal').html(errMsg);
						return false;
					}
				}
				
				if (!found) {
					
					//seharusnya pake yang ini
					//table.fnUpdate(((dataPledging.accountType.lookupDescription = $('#detailSecurityLimitDetail #securityLimitForm #accountTypeDesc').val())&&(dataPledging.accountType.lookupId = $('#detailSecurityLimitDetail #securityLimitForm #accountType').val())), parseFloat(rowPosition), 0);
					//sementara
					
					if ($('#inputMethodHidden').val() === otherToBase) {
						$('#entryOtherToBase').css("display","");
						$('#entryBaseToOther').css("display","none");
						
						table.fnUpdate(((dataCurrencyExchange.sourceCurrency.currencyCode = $('#detailCurrencyExchange #currencyExchangeForm #sourceCurrency').val()) && (dataCurrencyExchange.sourceCurrency.description = $('#detailCurrencyExchange #currencyExchangeForm #sourceCurrencyDesc').val())), parseFloat(rowPosition), 0);
						
						dataCurrencyExchange.sourceCurrency.currencyCode = $('#detailCurrencyExchange #currencyExchangeForm #sourceCurrency').val();
						dataCurrencyExchange.sourceCurrency.description = $('#detailCurrencyExchange #currencyExchangeForm #sourceCurrencyDesc').val();
					} else if ($('#inputMethodHidden').val()=== baseToOther) {
						$('#entryBaseToOther').css("display","");
						$('#entryOtherToBase').css("display","none");
						
						table.fnUpdate(((dataCurrencyExchange.targetCurrency.currencyCode = $('#detailCurrencyExchange #currencyExchangeForm #targetCurrency').val()) && (dataCurrencyExchange.targetCurrency.description = $('#detailCurrencyExchange #currencyExchangeForm #targetCurrencyDesc').val())), parseFloat(rowPosition), 0);
						
						dataCurrencyExchange.targetCurrency.currencyCode = $('#detailCurrencyExchange #currencyExchangeForm #targetCurrency').val();
						dataCurrencyExchange.targetCurrency.description = $('#detailCurrencyExchange #currencyExchangeForm #targetCurrencyDesc').val();
					}
					table.fnUpdate((dataCurrencyExchange.unitCurrency = $('#detailCurrencyExchange #currencyExchangeForm #unitCurrencyStripped').val()), parseFloat(rowPosition), 1);
					table.fnUpdate((dataCurrencyExchange.exchangeRate = $('#detailCurrencyExchange #currencyExchangeForm #exchangeRateStripped').val()), parseFloat(rowPosition), 2);
					table.fnUpdate((dataCurrencyExchange.currencyExchangeKey = $('#detailCurrencyExchange #currencyExchangeForm #currencyExchangeKey').val()), parseFloat(rowPosition), 3);
					
					dataCurrencyExchange.unitCurrency = $('#detailCurrencyExchange #currencyExchangeForm #unitCurrencyStripped').val();
					dataCurrencyExchange.exchangeRate = $('#detailCurrencyExchange #currencyExchangeForm #exchangeRateStripped').val();
					
					$('#detailCurrencyExchange').dialog('close');

					return false;
				}
			} else {
				var found = false;
				var rows = table.fnGetNodes().length;
				for(i=0; i<rows; i++) {
					var cells = table.fnGetData(i);
					if (($('#inputMethodHidden').val() === otherToBase) && (cells.sourceCurrency.currencyCode == sourceCurrency)){
						$('#errDetailCurrExchgGlobal').html(errMsg);
						return false;
					}
					if (($('#inputMethodHidden').val()=== baseToOther) && (cells.targetCurrency.currencyCode == targetCurrency)){
						$('#errDetailCurrExchgGlobal').html(errMsg);
						return false;
					}
				}
				if (!found) {
					var dataCurrencyExchange = new Object();
					dataCurrencyExchange.sourceCurrency = new Object();
					dataCurrencyExchange.targetCurrency = new Object();
					
					//seharusnya pake yg ini
//					dataPledging.accountType.lookupId = $('#detailSecurityLimitDetail #securityLimitForm #accountType').val();
					//sementara
					if ($('#inputMethodHidden').val() === otherToBase) {
						$('#entryOtherToBase').css("display","");
						$('#entryBaseToOther').css("display","none");
						
						dataCurrencyExchange.sourceCurrency.currencyCode = $('#detailCurrencyExchange #currencyExchangeForm #sourceCurrency').val();
						dataCurrencyExchange.sourceCurrency.description = $('#detailCurrencyExchange #currencyExchangeForm #sourceCurrencyDesc').val();
					} else if ($('#inputMethodHidden').val()=== baseToOther) {
						$('#entryBaseToOther').css("display","");
						$('#entryOtherToBase').css("display","none");
						
						dataCurrencyExchange.targetCurrency.currencyCode = $('#detailCurrencyExchange #currencyExchangeForm #targetCurrency').val();
						dataCurrencyExchange.targetCurrency.description = $('#detailCurrencyExchange #currencyExchangeForm #targetCurrencyDesc').val();
					}
					
					dataCurrencyExchange.unitCurrency = $('#detailCurrencyExchange #currencyExchangeForm #unitCurrencyStripped').val();
					dataCurrencyExchange.exchangeRate = $('#detailCurrencyExchange #currencyExchangeForm #exchangeRateStripped').val();
					dataCurrencyExchange.currencyExchangeKey = $('#detailCurrencyExchange #currencyExchangeForm #currencyExchangeKey').val();
					table.fnAddData(dataCurrencyExchange);
					
					$('#detailCurrencyExchange').dialog('close');
					
					return false;
				}
			}
		});
		
		$( "#cancelCurrencyExchangeDetail" ).click(function() {
			
			$('#detailCurrencyExchange').dialog('close');
			return false;
		});
		
		var closeDialog = function() {
			$("#dialog-message").dialog("close");
		};
		
		$('#listCurrencyExchangeDetail #gridCurrencyExchange tbody tr #deleteButton').live('click', function() {
			var row = $(this).parents('tr');
			var rowNumber = tableExchangeRate.fnGetPosition(row[0]);
			var deleteExchangeRate = function(){
				tableExchangeRate.fnDeleteRow(rowNumber);
				var idTable = $("#listCurrencyExchangeDetail #gridCurrencyExchange");
				var trs = $("tr", idTable);
				$.each(trs, function(idx, data){
					var hiddens = $("[type=hidden]", $(this));
						if ($('#inputMethodHidden').val() === otherToBase) {
							$(hiddens).eq(0).attr("name", "currencyExchangeDetails["+(idx-1)+"].sourceCurrency.currencyCode");
							$(hiddens).eq(1).attr("name", "currencyExchangeDetails["+(idx-1)+"].sourceCurrency.description");
						} else if ($('#inputMethodHidden').val()=== baseToOther) {
							$(hiddens).eq(0).attr("name", "currencyExchangeDetails["+(idx-1)+"].targetCurrency.currencyCode");
							$(hiddens).eq(1).attr("name", "currencyExchangeDetails["+(idx-1)+"].targetCurrency.description");
						}
						$(hiddens).eq(2).attr("name", "currencyExchangeDetails["+(idx-1)+"].unitCurrency");
						$(hiddens).eq(3).attr("name", "currencyExchangeDetails["+(idx-1)+"].exchangeRate");
						$(hiddens).eq(4).attr("name", "currencyExchangeDetails["+(idx-1)+"].currencyExchangeKey");
				});
				$("#dialog-message").dialog("close");
				
				return false;
			}
			messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteExchangeRate, closeDialog);
		
		});
		
	}); // end ready function

	function tabCurrencyExchangeDetail(data) {
		var frmDivCurrencyExchangeOtherToBase = $('#divCurrencyExchangeOtherToBase');
		var frmDivCurrencyExchangeBaseToOther = $('#divCurrencyExchangeBaseToOther');
		var otherToBase = '${inputothertobaseval}';
		var baseToOther = '${inputbasetootherval}';
		var currencyExchangeDetails;
		if ('${currencyExchangeTabs}' != null )
			currencyExchangeDetails = ${currencyExchangeTabs.raw()};

		if ('${currencyExchangeDetails}' == null )
			currencyExchangeDetails = new Array();
		
		tableExchangeRate = 
			$('#listCurrencyExchangeDetail #gridCurrencyExchange').dataTable({
				aaData: currencyExchangeDetails,
				aoColumns: [
							{
								fnRender: function(obj){
									var controls;
									if ($('#inputMethodHidden').val() === otherToBase) {
										$('#entryOtherToBase').css("display","");
										$('#entryBaseToOther').css("display","none");
										controls = obj.aData.sourceCurrency.currencyCode;
										controls += '<input type="hidden" name="currencyExchangeDetails[' + obj.iDataRow + '].sourceCurrency.currencyCode" value="' + obj.aData.sourceCurrency.currencyCode + '" />';
										controls += '<input type="hidden" name="currencyExchangeDetails[' + obj.iDataRow + '].sourceCurrency.description" value="' + obj.aData.sourceCurrency.description + '" />';
									} else if ($('#inputMethodHidden').val()=== baseToOther) {
										$('#entryBaseToOther').css("display","");
										$('#entryOtherToBase').css("display","none");
										controls = obj.aData.targetCurrency.currencyCode;
										controls += '<input type="hidden" name="currencyExchangeDetails[' + obj.iDataRow + '].targetCurrency.currencyCode" value="' + obj.aData.targetCurrency.currencyCode + '" />';
										controls += '<input type="hidden" name="currencyExchangeDetails[' + obj.iDataRow + '].targetCurrency.description" value="' + obj.aData.targetCurrency.description + '" />';
									}
									return controls;
								}
							},
							{
								fnRender: function(obj){
									var controls;
									controls = $('#dummy').autoNumericSet(obj.aData.unitCurrency, {aPad:true,mDec:4}).val();
									controls += '<input type="hidden" name="currencyExchangeDetails[' + obj.iDataRow + '].unitCurrency" value="' + obj.aData.unitCurrency + '" />';
									return controls;
								}
							},
							{
								fnRender: function(obj){
									var controls;
									controls = $('#dummy').autoNumericSet(obj.aData.exchangeRate, {aPad:true,mDec:4}).val();
									controls += '<input type="hidden" name="currencyExchangeDetails[' + obj.iDataRow + '].exchangeRate" value="' + obj.aData.exchangeRate + '" />';
									return controls;
								}
							},
							{
								fnRender: function(obj) {
								 	var controls;
								 	controls = '<center><input id="deleteButton" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
									controls += '<input type="hidden" name="currencyExchangeDetails[' + obj.iDataRow + '].currencyExchangeKey" value="' + obj.aData.currencyExchangeKey + '" />';
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
		
		$('#listCurrencyExchangeDetail #gridCurrencyExchange ').removeAttr('style'); // tembak nilai widht untuk tampilan
		$('#listCurrencyExchangeDetail #gridCurrencyExchange tbody tr td').die('click');
		$('#listCurrencyExchangeDetail #gridCurrencyExchange tbody tr td').live('click', function() {
			var rowPos = $(this).parents('tr');
			var rowPosNumber = tableExchangeRate.fnGetPosition(rowPos[0]);
			var pos = tableExchangeRate.fnGetPosition(this);
			cell = tableExchangeRate.fnGetData(this.parentNode);
			
			if ($('#inputMethodHidden').val()==='') {
				$("#errCurrencyExchangeDetail").html('Please choose Input Method before Add, Delete or Choose Exchange Rate Item');
				
				$('#detailCurrencyExchange').dialog('close');
				return false;
			}
			
			if ($('#baseCurrency').val()==='') {
				$("#errCurrencyExchangeDetail").html('Please choose Base Currency before Add, Delete or Choose Exchange Rate Item');
				
				$('#detailCurrencyExchange').dialog('close');
				return false;
			}
			
			if (pos[1] != 3) {
				
				if ($('#inputMethodHidden').val() === otherToBase) {
					frmDivCurrencyExchangeBaseToOther.css("display","none");
					frmDivCurrencyExchangeOtherToBase.css("display","");
					$('#sourceCurrencyHelp').val('?');
				} else if ($('#inputMethodHidden').val()=== baseToOther) {
					frmDivCurrencyExchangeOtherToBase.css("display","none");
					frmDivCurrencyExchangeBaseToOther.css("display","");
					$('#targetCurrencyHelp').val('?');
				}
				
				dataCurrencyExchange = tableExchangeRate.fnGetData(this.parentNode);
				
				$('#detailCurrencyExchange #currencyExchangeForm #rowPosition').val(rowPosNumber);
				
				if ($('#inputMethodHidden').val() === otherToBase) {
					$('#detailCurrencyExchange #currencyExchangeForm #sourceCurrency').val(dataCurrencyExchange.sourceCurrency.currencyCode);
					$('#detailCurrencyExchange #currencyExchangeForm #sourceCurrencyDesc').val(dataCurrencyExchange.sourceCurrency.description);
					
					$('#detailCurrencyExchange #currencyExchangeForm #oldSourceCurrency').val($('#detailCurrencyExchange #currencyExchangeForm #sourceCurrency').val());
					$('#detailCurrencyExchange #currencyExchangeForm #newSourceCurrency').val($('#detailCurrencyExchange #currencyExchangeForm #oldSourceCurrency').val());
					
				} else if ($('#inputMethodHidden').val()=== baseToOther) {
					$('#detailCurrencyExchange #currencyExchangeForm #targetCurrency').val(dataCurrencyExchange.targetCurrency.currencyCode);
					$('#detailCurrencyExchange #currencyExchangeForm #targetCurrencyDesc').val(dataCurrencyExchange.targetCurrency.description);
					
					$('#detailCurrencyExchange #currencyExchangeForm #oldTargetCurrency').val($('#detailCurrencyExchange #currencyExchangeForm #targetCurrency').val());
					$('#detailCurrencyExchange #currencyExchangeForm #newTargetCurrency').val($('#detailCurrencyExchange #currencyExchangeForm #oldTargetCurrency').val());
					
				}
				
				$('#detailCurrencyExchange #currencyExchangeForm #unitCurrency').autoNumericSet(dataCurrencyExchange.unitCurrency);
				$('#detailCurrencyExchange #currencyExchangeForm #unitCurrencyStripped').val(dataCurrencyExchange.unitCurrency);
				$('#detailCurrencyExchange #currencyExchangeForm #exchangeRate').autoNumericSet(dataCurrencyExchange.exchangeRate);
				$('#detailCurrencyExchange #currencyExchangeForm #exchangeRateStripped').val(dataCurrencyExchange.exchangeRate);
				$('#detailCurrencyExchange #currencyExchangeForm #currencyExchangeKey').val(dataCurrencyExchange.currencyExchangeKey);
				
				$('.error').html("");
				$("#detailCurrencyExchange").dialog('open');
				$('.ui-widget-overlay').css('height',$('body').height());
			}
			
		});
	};
	
	function doSave(){
		if (tableExchangeRate.fnGetNodes().length < 1){
			$('#errCurrencyExchangeDetail').html("*Error saving! Make sure there is a minimum of one data in 'Exchange Rate'");
			return false;
		}
		
		return true;
	};
	