function TableBankInfo(html) {
	if (this instanceof TableBankInfo) {
		var bankMap = new Hashtable();
		
		var table = html.dataTable({
			aoColumnDefs: [{asSorting:["asc"], aTargets:[0]}],
			bFilter:false,
//			sScrollX:'100%',	
			bSort:false,
			bInfo:false,
			bJQueryUI:true,
			bPaginate: false			
		});
		
		$('th:first', html).css('width', '');
		
	/*
		$('#bankInfoTable tbody tr input[id*=btnDel]').live('click', function() {
			//alert("tes");
			var row = $(this).parents('tr');
			var rowDelete = html.fnGetPosition(row[0]);
			//alert(rowDelete);
			//var table = html.dataTable();
			//var datas = table.fnGetData(row[0]);
			//var rows = tableChargeProfile.fnGetNodes().length;
			//for (i = 0; i < rows; i++) {
			//	var cells = tableChargeProfile.fnGetData(i);
			//	if (cells[0] == datas.chargeMaster.chargeKey) {
			//		$('#gridChargeProfile').find("input[name*='overrideButton["+i+"]']").attr('disabled',false);
			//		break;
			//	}	
			//}
			html.fnDeleteRow(rowDelete);	
		});
	*/	
	/*
		$('#bankInfoTable tbody tr input[id*=radioStatus]').live('click', function() {
			var rowPos= $(this).parents('tr');
			var rowPosNumber = html.fnGetPosition(rowPos[0]);
			cell = html.fnGetData(rowPosNumber);
				$("#btnBankAccountAddError").html("");
				$("#defaultProductBankAccount").val($(cell[3]).val());
			
			//var row = $(this).parents('tr');
			//var rowDelete = html.fnGetPosition(row[0]);
			//alert(rowDelete);
			//var table = html.dataTable();
			//var datas = table.fnGetData(row[0]);
			//var rows = tableChargeProfile.fnGetNodes().length;
			//for (i = 0; i < rows; i++) {
			//	var cells = tableChargeProfile.fnGetData(i);
			//	if (cells[0] == datas.chargeMaster.chargeKey) {
			//		$('#gridChargeProfile').find("input[name*='overrideButton["+i+"]']").attr('disabled',false);
			//		break;
			//	}	
			//}
			//html.fnDeleteRow(rowDelete);	
		});
	*/
		$('#bankInfoTable').removeAttr('style');
		$('#bankInfoTable tbody tr td').die('click');
		$('#bankInfoTable tbody tr td').live('click', function() {
			$("#dialogBankAccount").find("span[id*='Error']").html("");
			
			var rowPos= $(this).parents('tr');
			var rowPosNumber = html.fnGetPosition(rowPos[0]);
			cell = html.fnGetData(this.parentNode);
			var pos = html.fnGetPosition(this);
			if ((pos[1] != 4) && (pos[1] != 3)) {
				var hiddens = $("[type=hidden]", rowPos);
				
				$("#rowNumber").val(rowPosNumber);
				$("#dialogBankAccount #bankCode").val($(hiddens).eq(0).attr("name", "prd.listBankAccounts["+(rowPosNumber)+"].bankCode.thirdPartyCode").val());
				$("#dialogBankAccount #bankCodeKey").val($(hiddens).eq(1).attr("name", "prd.listBankAccounts["+(rowPosNumber)+"].bankCode.thirdPartyKey").val());
				$("#dialogBankAccount #bankCodeDesc").val($(hiddens).eq(2).attr("name", "prd.listBankAccounts["+(rowPosNumber)+"].bankCode.thirdPartyName").val());
				$("#dialogBankAccount #bankAccountNo").val($(hiddens).eq(3).attr("name", "prd.listBankAccounts["+(rowPosNumber)+"].accountNo").val());
				$("#dialogBankAccount #accountHolderName").val($(hiddens).eq(4).attr("name", "prd.listBankAccounts["+(rowPosNumber)+"].name").val());
				$("#dialogBankAccount #defaultRgProdBankAccount").val($(hiddens).eq(5).attr("name", "prd.listBankAccounts["+(rowPosNumber)+"].defaultRgProdBankAccount").val());
				$("#dialogBankAccount #bankAccountKey").val($(hiddens).eq(6).attr("name", "prd.listBankAccounts["+(rowPosNumber)+"].bankAccountKey").val());
				$("#dialogBankAccount #productCodeBn").val($(hiddens).eq(7).attr("name", "prd.listBankAccounts["+(rowPosNumber)+"].rgProduct.productCode").val());
				$("#dialogBankAccount #bankBranch").val($(hiddens).eq(8).attr("name", "prd.listBankAccounts["+(rowPosNumber)+"].bankBranch").val());
				$("#dialogBankAccount #oldDefaultRgProdBankAccount").val($("#dialogBankAccount #defaultRgProdBankAccount").val());				
				$("#dialogBankAccount").dialog('open');
				$('.ui-widget-overlay').css('height',$('body').height());
			}
		});
		
		
		function attachDelEvent(id) {
			bankMap.put(id, id);
			reordering();
			//html.id(id).button();
			html.id(id).click(function(){
				var row = $(this).parents('tr');
				cell = html.fnGetData(row[0]);				
				if($(cell[3]).val() == $("#defaultProductBankAccount").val()) {
					$("#btnBankAccountAddError").html("Cannot Delete Default Bank Account").show();
				} else {
					$("#btnBankAccountAddError").html("");
					var answer = confirm("Are you sure to delete data ?");
					if (answer){
						//$(this).parent().parent().remove();
						bankMap.remove(id);
						var row = $(this).parents('tr');
						var rowDelete = html.fnGetPosition(row[0]);
						html.fnDeleteRow(rowDelete);	
						reordering();
						
						//var radios = $("[type=radio]", html.tbody());
						//$.each(radios, function(i, data){
							//if(!($("input[name='bankStatus']").is(":checked"))) {
								//found = true;
							//	if(radios.length == 1 ){
									//$("#radioStatus"+$(radios[0]).val()).attr("checked","checked");
									//$("#bankAccountKey").val($(radios[0]).val());
									
							//	}
									
							//	return false;
							//}
						//});
					}			
				}
				
					
			});
		}
	
		function checkDefault(id) {
			bankMap.put(id, id);
			reordering();
			
			if(html.id(id).val() == $("#defaultProductBankAccount").val()){
				$("#"+id).attr("checked","checked");
			}
			
//			console.debug($("#"+id).is(":checked"));
			
			html.id(id).click(function(){
				$("#btnBankAccountAddError").html("");
				$("#defaultProductBankAccount").val(html.id(id).val());
			});
		}
		function initilize() {
			var buttons = $("[type=button]", html.tbody());
			$.each(buttons, function(i, data){
				attachDelEvent($(data).attr("id"));
			});
		};
		function initilizeRadio() {
			var radios = $("[type=radio]", html.tbody());
			$.each(radios, function(i, data){
				checkDefault($(data).attr("id"));
			});
		};
		
		function reordering() {
			var trs = $("tr", html);
			$.each(trs, function(idx, data){
				var hiddens = $("[type=hidden]", $(this));
				$(hiddens).eq(0).attr("name", "prd.listBankAccounts["+(idx-1)+"].bankCode.thirdPartyCode");
				$(hiddens).eq(1).attr("name", "prd.listBankAccounts["+(idx-1)+"].bankCode.thirdPartyKey");
				$(hiddens).eq(2).attr("name", "prd.listBankAccounts["+(idx-1)+"].bankCode.thirdPartyName");
				$(hiddens).eq(3).attr("name", "prd.listBankAccounts["+(idx-1)+"].accountNo");
				$(hiddens).eq(4).attr("name", "prd.listBankAccounts["+(idx-1)+"].name");
				$(hiddens).eq(5).attr("name", "prd.listBankAccounts["+(idx-1)+"].defaultRgProdBankAccount");
				$(hiddens).eq(6).attr("name", "prd.listBankAccounts["+(idx-1)+"].bankAccountKey");
				$(hiddens).eq(7).attr("name", "prd.listBankAccounts["+(idx-1)+"].rgProduct.productCode");
				$(hiddens).eq(8).attr("name", "prd.listBankAccounts["+(idx-1)+"].bankBranch");
			});
		}
		
		this.isExist = function(id) {
			reordering();
			/*
			bankMap.each(function(k, v) {
			    alert('key is: ' + k + ', value is: ' + v);
			});
			*/
			return bankMap.get("btnDel"+id);
		};
		
//====== UPDATE ROW/ COLUMN ========================================================================//
		this.updateRowCol1 = function(data, row) {
			html.fnUpdate(
					data.thirdPartyCode+
					"<input type='hidden' value='"+data.thirdPartyCode+"' name='prd.listBankAccounts["+row+"].bankCode.thirdPartyCode'>"+
					"<input type='hidden' value='"+data.thirdPartyKey+"' name='prd.listBankAccounts["+row+"].bankCode.thirdPartyKey'>"+
					"<input type='hidden' value='"+data.thirdPartyName+"' name='prd.listBankAccounts["+row+"].bankCode.thirdPartyName'>"
						, parseFloat(row),0);
		};
		
		this.updateRowCol2 = function(data, row) {
			html.fnUpdate(
					data.bankAccountNo+
					"<input type='hidden' value='"+data.bankAccountNo+"' name='prd.listBankAccounts["+row+"].accountNo'>"
						, parseFloat(row),1);
		};
		
		this.updateRowCol3 = function(data, row) {
			html.fnUpdate(
					data.description+
					"<input type='hidden' value='"+data.description+"' name='prd.listBankAccounts["+row+"].name'>"
						, parseFloat(row),2);
		};
		
		this.updateRowCol4 = function(data, row) {
			html.fnUpdate(
					"<input type='hidden' value='"+data.defaultRgProdBankAccount+"' name='prd.listBankAccounts["+row+"].defaultRgProdBankAccount'>"+
					"<input type='radio' id='radioStatus"+data.defaultRgProdBankAccount+"' name='bankStatus' value='"+data.defaultRgProdBankAccount+"'  />"
						, parseFloat(row),3);
			
		};
		
		this.updateRowCol5 = function(data, row) {
			html.fnUpdate(
					"<input sim='btnDelBankAcc' id='btnDel"+data.defaultRgProdBankAccount+"'+ type='button' value='Delete'>"+
					"<input type='hidden' value='"+data.bankAccountKey+"' name='prd.listBankAccounts["+row+"].bankAccountKey'>"+
					"<input type='hidden' value='"+data.productCode+"' name='prd.listBankAccounts["+row+"].rgProduct.productCode'>"+
					"<input type='hidden' value='"+data.bankBranch+"' name='prd.listBankAccounts["+row+"].bankBranch'>"
						, parseFloat(row),4);
//			attachDelEvent("btnDel"+data.defaultRgProdBankAccount);
			initilize();
			initilizeRadio();
//			checkDefault("radioStatus"+data.defaultRgProdBankAccount);
			
//			if(data.defaultRgProdBankAccount == $("#defaultProductBankAccount").val()){
//			//alert(data.defaultRgProdBankAccount +" == "+ $("#defaultProductBankAccount").val());
//			$("#radioStatus"+data.defaultRgProdBankAccount).attr("checked","checked");
//		}
			
		};
		
//=========================================================================================================//

//====================== ADD ROW =========================================================================//	
		this.addRow = function(data) {		
			//html.clazz("dataTables_empty").parent().remove();
			//var idx = html.tbody().children().length;
			var idx =  html.fnGetNodes().length;
			html.fnAddData([
									data.thirdPartyCode+
									"<input type='hidden' value='"+data.thirdPartyCode+"' name='prd.listBankAccounts["+idx+"].bankCode.thirdPartyCode'>"+
									"<input type='hidden' value='"+data.thirdPartyKey+"' name='prd.listBankAccounts["+idx+"].bankCode.thirdPartyKey'>"+
									"<input type='hidden' value='"+data.thirdPartyName+"' name='prd.listBankAccounts["+idx+"].bankCode.thirdPartyName'>",
									data.bankAccountNo+
									"<input type='hidden' value='"+data.bankAccountNo+"' name='prd.listBankAccounts["+idx+"].accountNo'>",
									data.description+
									"<input type='hidden' value='"+data.description+"' name='prd.listBankAccounts["+idx+"].name'>",
									"<input type='hidden' value='"+data.defaultRgProdBankAccount+"' name='prd.listBankAccounts["+idx+"].defaultRgProdBankAccount'>"+
									"<input type='radio' id='radioStatus"+data.defaultRgProdBankAccount+"' name='bankStatus' value='"+data.defaultRgProdBankAccount+"' " + ((idx == 0) ? "checked='checked' " : "") + " />",
									"<input sim='btnDelBankAcc' id='btnDel"+data.defaultRgProdBankAccount+"'+ type='button' value='Delete'>"+
									"<input type='hidden' value='' name='prd.listBankAccounts["+idx+"].bankAccountKey'>"+
									"<input type='hidden' value='"+data.productCode+"' name='prd.listBankAccounts["+idx+"].rgProduct.productCode'>" +
									"<input type='hidden' value='"+data.bankBranch+"' name='prd.listBankAccounts["+idx+"].bankBranch'>"
											]);
			idx++;
				
			
			/*
			html.tbody().append(
				"<tr class='"+rowStatus+"'>"+
					"<td>"+
						"<input type='text' disabled='disabled' value='"+data.thirdPartyCode+"'>"+
						"<input type='hidden' value='"+data.thirdPartyCode+"' name='prd.listBankAccounts["+idx+"].bankCode.thirdPartyCode'>"+
						"<input type='hidden' value='"+data.thirdPartyKey+"' name='prd.listBankAccounts["+idx+"].bankCode.thirdPartyKey'>"+
						"<input type='hidden' value='"+data.thirdPartyName+"' name='prd.listBankAccounts["+idx+"].bankCode.thirdPartyName'>"+
					"</td>"+
					"<td>"+
						"<input type='text' disabled='disabled' value='"+data.bankAccountNo+"'>"+
						"<input type='hidden' value='"+data.bankAccountNo+"' name='prd.listBankAccounts["+idx+"].accountNo'>"+
					"</td>"+
					"<td>"+
						"<input type='text' disabled='disabled' value='"+data.description+"'>"+
						"<input type='hidden' value='"+data.description+"' name='prd.listBankAccounts["+idx+"].name'>"+
					"</td>"+
					"<td>"+
						//"<input type='radio' id='radioStatus"+data.bankAccountKey+"' name='bankStatus' value='" + data.code + "' />"+
					"<input type='radio' id='radioStatus"+data.bankAccountKey+"' name='bankStatus' value='" + data.code + "' " + ((idx == 0) ? "checked='checked' " : "") + " />"+
					"</td>"+
					"<td>"+
						"<input sim='btnDelBankAcc' id='btnDel' type='button' value='Delete'>"+
						"<input type='hidden' value='"+data.bankAccountKey+"' name='prd.listBankAccounts["+idx+"].bankAccountKey'>"+
					"</td>"+
				"</tr>"
			);
			*/
			
			if (idx == 1) {
				$("#defaultProductBankAccount").val(data.defaultRgProdBankAccount);
			}
			//html.id("btnDel"+data.bankAccountKey).button();
			//$("#defaultRgProdBankAccount").val(idBtn.split(' ').join('_'));
			attachDelEvent("btnDel"+data.defaultRgProdBankAccount);
			checkDefault("radioStatus"+data.defaultRgProdBankAccount);
			//if (idx == 0) {
			//	$("#radiostatus"+data.bankAccountKey).click();
			//}
		};
		
//=====================================================================================================//
		
		initilize();
		initilizeRadio();
	}else{
		return new TableBankInfo(html);
	}
}