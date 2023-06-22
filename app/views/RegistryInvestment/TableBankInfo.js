function TableBankInfo(html) {
	if (this instanceof TableBankInfo) {
		var bankMap = new Hashtable();
		
		var table = html.dataTable({
			aoColumnDefs: [{asSorting:["asc"], aTargets:[0]}],
			bFilter:false,
			sScrollX:'100%',	
			bSort:false,
			bInfo:false,
			bJQueryUI:true,
			bPaginate: false			
		});
		
		$('th:first', html).css('width', '');
		
		var closeDialog = function() {
			$("#dialog-message").dialog('close');
		};
		
		function attachDelEvent(id) {
			bankMap.put(id, id);
			reordering();
			//html.id(id).button();
			html.id(id).click(function(){
				var row = $(this).parents('tr');
				var rowDelete = html.fnGetPosition(row[0]);
				cell = html.fnGetData(row[0]);
				
				if($(cell[3]).val() == $("#bankAccountKey").val()) {
					$("#btnBankAccountAddError").html("Cannot Delete Default Bank Account").show();
					//alert($(cell[3]).val());
				} else {
					$("#btnBankAccountAddError").html("");
//					var answer = confirm("Are you sure to delete data ?");
					var answer = true;
					if (answer){
//						$(this).parent().parent().remove();
						var deleteBankInfo = function() {
							bankMap.remove(id);
//							var row = $(this).parents('tr');
//							var rowDelete = html.fnGetPosition(row[0]);
							html.fnDeleteRow(rowDelete);	
							reordering();
							
							var radios = $("[type=radio]", html.tbody());
							$.each(radios, function(i, data){
								if(!($("input[name='bankStatus']").is(":checked"))) {
									//found = true;
								//	if(radios.length == 1 ){
										$("#radioStatus"+$(radios[0]).val()).attr("checked","checked");
										$("#bankAccountKey").val($(radios[0]).val());
										
								//	}
										
									return false;
								}
							});
							$("#dialog-message").dialog('close');
						};
						messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteBankInfo, closeDialog);
					}	
				}
			});
		}
		
		function checkDefault(id) {
			//bankMap.put(id, id);
			//reordering();
			if(html.id(id).val() == $("#bankAccountKey").val()){
				$("#"+id).attr("checked","checked");
			}
			html.id(id).click(function(){
				$("#btnBankAccountAddError").html("");
				$("#bankAccountKey").val(html.id(id).val());
			});
		}
		function initilize() {
			var buttons = $("[type=button]", html.tbody());
			$.each(buttons, function(i, data){
				attachDelEvent($(data).attr("id"));
			});
		};
		function initilizeRadio() {
			//$("#bankAccount").val("");
			var radios = $("[type=radio]", html.tbody());
			$.each(radios, function(i, data){
				checkDefault($(data).attr("id"));
			});
		};
		
		function reordering() {
			var trs = $("tr", html);
			$.each(trs, function(idx, data){
				var hiddens = $("[type=hidden]", $(this));
				//$(hiddens).eq(0).attr("name", "inv.listBankAccounts["+(idx-1)+"].bankAccountKey");
				$(hiddens).eq(0).attr("name", "inv.listBankAccounts["+(idx-1)+"].accountNo");
				$(hiddens).eq(1).attr("name", "inv.listBankAccounts["+(idx-1)+"].name");
				$(hiddens).eq(2).attr("name", "inv.listBankAccounts["+(idx-1)+"].bankCode.thirdPartyCode");
				$(hiddens).eq(3).attr("name", "inv.listBankAccounts["+(idx-1)+"].bankCode.thirdPartyKey");
				$(hiddens).eq(4).attr("name", "inv.listBankAccounts["+(idx-1)+"].bankCode.thirdPartyName");
				$(hiddens).eq(5).attr("name", "inv.listBankAccounts["+(idx-1)+"].bankAccountKey");
			});
		}
		
		this.isExist = function(id) {
			return bankMap.get("btnDel"+id);
		};
		
		this.addRow = function(data) {		
			
			html.clazz("dataTables_empty").parent().remove();
//			var idx = html.tbody().children().length;
//			var rowStatus = 'odd';
//			
//			if(idx % 2 == 1){
//				rowStatus = 'even';
//			}
			
			
			
				//html.clazz("dataTables_empty").parent().remove();
				//var idx = html.tbody().children().length;
				var idx =  html.fnGetNodes().length;
				html.fnAddData([
										data.bankAccountNo+
										"<input type='hidden' value='"+data.bankAccountNo+"' name='prd.listBankAccounts["+idx+"].accountNo'>",
										data.description+
										"<input type='hidden' value='"+data.description+"' name='prd.listBankAccounts["+idx+"].name'>",
										data.thirdPartyCode+
										"<input type='hidden' value='"+data.thirdPartyCode+"' name='prd.listBankAccounts["+idx+"].bankCode.thirdPartyCode'>"+
										"<input type='hidden' value='"+data.thirdPartyKey+"' name='prd.listBankAccounts["+idx+"].bankCode.thirdPartyKey'>"+
										"<input type='hidden' value='"+data.thirdPartyName+"' name='prd.listBankAccounts["+idx+"].bankCode.thirdPartyName'>",
										"<input type='radio' id='radioStatus"+data.bankAccountKey+"' name='bankStatus' value='"+data.bankAccountKey+"' " + ((idx == 0) ? "checked='checked' " : "") + " />",
										"<input sim='btnDelBankAcc' id='btnDel"+data.bankAccountKey+"'+ type='button' value='Delete'>"+
										"<input type='hidden' value='"+data.bankAccountKey+"' name='prd.listBankAccounts["+idx+"].bankAccountKey'>"
												]);
				idx++;
			
			
//			html.tbody().append(
//				"<tr class='"+rowStatus+"'>"+
//					"<td>"+
//						"<input type='text' disabled='disabled' value='"+data.bankAccountNo+"'>"+
//						"<input type='hidden' value='"+data.bankAccountNo+"' name='inv.listBankAccounts["+idx+"].accountNo'>"+
//					"</td>"+
//					"<td>"+
//						"<input type='text' disabled='disabled' value='"+data.description+"'>"+
//						"<input type='hidden' value='"+data.description+"' name='inv.listBankAccounts["+idx+"].name'>"+
//					"</td>"+
//					"<td>"+
//						"<input type='text' disabled='disabled' value='"+data.thirdPartyCode+"'>"+
//						"<input type='hidden' value='"+data.thirdPartyCode+"' name='inv.listBankAccounts["+idx+"].bankCode.thirdPartyCode'>"+
//						"<input type='hidden' value='"+data.thirdPartyKey+"' name='inv.listBankAccounts["+idx+"].bankCode.thirdPartyKey'>"+
//						"<input type='hidden' value='"+data.thirdPartyName+"' name='inv.listBankAccounts["+idx+"].bankCode.thirdPartyName'>"+
//					"</td>"+
//					"<td>"+
//						//"<input type='radio' id='radioStatus"+data.bankAccountKey+"' name='bankStatus' value='" + data.code + "' />"+
//					"<input type='radio' id='radioStatus"+data.bankAccountKey+"' name='bankStatus' value='" + data.code + "' " + ((idx == 0) ? "checked='checked' " : "") + " />"+
//					"</td>"+
//					"<td>"+
//						"<input sim='btnDelBankAcc', id='btnDel"+data.bankAccountKey+"' type='button' value='Delete'>"+
//						"<input type='hidden' value='"+data.bankAccountKey+"' name='inv.listBankAccounts["+idx+"].bankAccountKey'>"+
//					"</td>"+
//				"</tr>"
//			);
			
			if (idx == 1) {
				$("#bankAccountKey").val(data.bankAccountKey);
			}

			//html.id("btnDel"+data.bankAccountKey).button();
			
			attachDelEvent("btnDel"+data.bankAccountKey);
			checkDefault("radioStatus"+data.bankAccountKey);
			
			//if (idx == 0) {
			//	$("#radiostatus"+data.bankAccountKey).click();
			//}

			
		};
		
		initilize();
		initilizeRadio();
	}else{
		return new TableBankInfo(html);
	}
}
	