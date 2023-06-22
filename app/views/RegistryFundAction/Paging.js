function Paging(html) {
	if (this instanceof Paging) {
		this.parameter = function(type) {
			var p = new Object();
			p.query = true;
			p.fundActionKey = $("#fundActionKey").val();
			p.uniqeId = ($("#rgTradeTemp").val() == '') ? 'kosong' : $("#rgTradeTemp").val();
			console.log("p.uniqeId "+p.uniqeId);
			return p;
		};
		
		var unitDigit = $("#unitRoundValue").val();
		var unitType = $("#unitRoundType").val();
		
		var priceDigit = $("#priceRoundValue").val();
		var priceType = $("#priceRoundType").val();
		
		var amountDigit = $("#amountRoundValue").val();
		var amountType = $("#amountRoundType").val();

		var tblPagingDividen = $("#tblSummary").paging("@{RegistryFundAction.pagingDividen()}", this, function(){
			var ph = $("<input>");
			$("tr", $("#tblSummary")).each(function(){
				var prop = $(this).data("prop");
				if (prop) {
					var bean = prop.bean;
					if (bean) {
						var availableUnitNoExp = bean.availabelUnit.toFixed(unitDigit);  //handle nilai exponential, eg: 8.07733E-9
						var formatedUnitBalance = ph.valueRnd(availableUnitNoExp, true, unitDigit, unitType);
						$("td", this).eq(2).html(formatedUnitBalance.val());

						var formatedAmount = ph.valueRnd(bean.amount, true, amountDigit, amountType);
						$("td", this).eq(3).html(formatedAmount.val());
						if(bean.price != undefined){
							var formatedPrice = ph.valueRnd(bean.price, true, priceDigit, priceType);
							$("td", this).eq(4).html(formatedPrice.val());
						}

						if(bean.unit != undefined){
							var formatedUnit = ph.valueRnd(bean.unit, true, unitDigit, unitType);
							$("td", this).eq(5).html(formatedUnit.val());
						}
					}
				}
			});

			var totalUnit = ph.valueRnd($("#ptradeUnit").val().toNumber(","), true, unitDigit, unitType);
			$("#ptradeUnit").val(totalUnit.val());
			
			var totalAmount = ph.valueRnd($("#ptradeNetAmount").val().toNumber(","), true, amountDigit, amountType);
			$("#ptradeNetAmount").val(totalAmount.val());
		});

		var isReadOnly = "${readOnly}";
		$("#pagingClearAll").button();
		$("#pagingClearAll").button("option", "disabled", isReadOnly);

		$("#pagingClearAll").click(function(){
			var param = new Object();
			param.fundActionKey = $("#fundActionKey").val();
			param.uniqeId = ($("#rgTradeTemp").val() == '') ? 'kosong' : $("#rgTradeTemp").val();

			return $().fetchSync("@{RegistryFundAction.resetPaging()}", {'param':param}, function(){
				tblPagingDividen.fnPageChange("first");
				
				$('#fundActionTab').tabs("select", "tab-1");
				$('#proccess').button("enable");
				$('.master').attr("disabled", false);
				$('#ptradeUnit').val("");
				$('#ptradeNetAmount').val("");
				$('#calculate').val(false);
				$('#currency').attr("disabled", true);

				$('#navDate').attr("disabled", false);
				$('#postDate').attr("disabled", false);
				$('#paymentDate').attr("disabled", false);
				if ($('#divType').val() == "") {
					$('#navDate').attr("disabled", true);
					$('#paymentDate').attr("disabled", true);
					$('#postDate').attr("disabled", true);
				}

				if ($('#divType').val() == "By Cash") {
					console.log("masuk cash");
					$('#navDate').attr("disabled", true);
					$('#postDate').attr("disabled", true);
				}

				if ($('#divType').val() == "By Reinvestment") {
					$('#paymentDate').attr("disabled", true);
				}

				$("#ratioPerUnitBy").attr("disabled", true);
				$("#ratio").attr("disabled", true);

				if ($('#ratioPerUnit').val() == "RATIO_PER_UNIT") {
					$("#amount").attr("disabled", true);
					$("#ratioPerUnitBy").attr("disabled", false);
					$("#ratio").attr("disabled", false);
				}

				$('#fundActionKey').attr("disabled", true);
			});
		});
	} else {
		return new Paging(html);
	}
}