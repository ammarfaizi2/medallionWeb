function Paging(html) {
	if (this instanceof Paging) {
	
		this.parameter = function(type) {
			var p = new Object();
			p.uniqeId = $("#rgTradeTemp").val();
			if (p.uniqeId == '') p.uniqeId = 'kosong';
			p.query = true;
			return p;
		};
		
		var unitDigit = $("#unitRoundValue").val();
		var unitType = $("#unitRoundType").val();
		
		var priceDigit = $("#priceRoundValue").val();
		var priceType = $("#priceRoundType").val();
		
		var amountDigit = $("#amountRoundValue").val();
		var amountType = $("#amountRoundType").val();
		
		var tblPagingLiq = $("#tblSummary").paging("@{RegistryLiquidationProcess.pagingLiquidation()}", this, function(){
			var ph = $("<input>");
			$("tr", $("#tblSummary")).each(function(){
				
				var prop = $(this).data("prop");
				if (prop) {
					var bean = prop.bean;
					if (bean) {
						var formatedUnit = ph.valueRnd(bean.unit, true, unitDigit, unitType);
						$("td", this).eq(3).html(formatedUnit.val());
						
						var formatedPrice = ph.valueRnd(bean.price, true, priceDigit, priceType);
						$("td", this).eq(4).html(formatedPrice.val());
						
						var formatedAmount = ph.valueRnd(bean.balanceAmount, true, amountDigit, amountType);
						$("td", this).eq(5).html(formatedAmount.val());
					}
				}
			});
		});
		
		var isReadOnly = "${readOnly}";
		$('#clearAll').button();
		$("#clearAll").button("option", "disabled", isReadOnly);
		$('#clearAll').click(function(){
			var param = new Object();			
			param.uniqeId = $("#rgTradeTemp").val();
			if (param.uniqeId == '') param.uniqeId = 'kosong';
			
			return $().fetchSync("@{RegistryLiquidationProcess.resetPaging()}", {'param':param}, function(){
				tblPagingLiq.fnPageChange("first");
				$('#tabs').tabs("enable", 0);
				$('#tabs').tabs('select', 'tabMaster');
				$('#tabs').tabs("disable", 1);
				$('.modeView').attr('disabled', false);
				$('#processCalculate').button('option', 'disabled', false);
				$('#h_externalreference').attr('disabled', 'disabled');
				$('#h_remarks').attr('disabled', 'disabled');
				$('#calculate').val('true');
			});
		});
	}else{
		return new Paging(html);
	}
}