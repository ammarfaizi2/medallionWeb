function Paging(html) {
	if (this instanceof Paging) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.InvestmentAccountNoOperator = app.investmentAccountNoOperator.val();
			p.InvestmentAccountSearchNo = app.investmentAccountSearchNo.val();
			p.InvestmentAccountNameOperator = app.investmentAccountNameOperator.val();
			p.InvestmentAccountSearchName = app.investmentAccountSearchName.val();
			p.IdentificationNoOperator = app.identificationNoOperator.val();
			p.InvestmentAccountSearchIdentificationNo = app.investmentAccountSearchIdentificationNo.val();
			p.InvestmentAccountFundCode = app.investmentAccountFundCode.val();
			p.InvestmentAccountCifAperdOperator = app.investmentAccountCifAperdOperator.val();
			p.InvestmentAccountSearchCifAperd = app.investmentAccountSearchCifAperd.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.investmentAccountNoOperator.children().eq(0).remove();
		app.investmentAccountNoOperator.attr('selectedIndex', 1);
		app.investmentAccountNameOperator.children().eq(0).remove();
		app.investmentAccountNameOperator.attr('selectedIndex', 1);
		app.identificationNoOperator.children().eq(0).remove();
		app.identificationNoOperator.attr('selectedIndex', 1);
		app.investmentAccountCifAperdOperator.children().eq(0).remove();
		app.investmentAccountCifAperdOperator.attr('selectedIndex', 1);
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		if ('${param}' == 'dedupe')
			app.newDataCust.button();
		
		
		app.datatable = app.tableInvestment.paging("@{RegistryInvestment.investmentPaging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		$('#investmentAccountFundCode').change(function(){
			if ($('#investmentAccountFundCode').val()==''){
				$('#investmentAccountFundCodeKey').val('');
				$('#investmentAccountFundCodeDesc').val('');
				$('#h_investmentAccountFundCodeDesc').val('');
			}
		});

		$('#investmentAccountFundCode').lookup({
			list:'@{Pick.listRgProducts()}',
			get:{
				url:'@{Pick.getRgProduct()}',
				success: function(data){
					$('#investmentAccountFundCode').removeClass('fieldError');
					$('#investmentAccountFundCode').val(data.productCode);
					$('#investmentAccountFundCodeDesc').val(data.description);
					$('#h_investmentAccountFundCodeDesc').val(data.description);
				},
				error: function(data){
					$('#investmentAccountFundCode').addClass('fieldError');
					$('#investmentAccountFundCode').val('');
					$('#investmentAccountFundCodeDesc').val('NOT FOUND');
					$('#h_investmentAccountFundCodeDesc').val('');
				}
			},
			description:$('#investmentAccountFundCodeDesc'),
			help:$('#investmentAccountFundCodeHelp')
		});
		
		//app.datatable.search.val("");
		app.search.click(function(){
			$('#result').css('display', '');
			app.query.val(true);
			app.datatable.search.val("");
			app.datatable.fnPageChange("first");
			return false;
		});

		app.reset.click(function(){
			//app.datatable.trigger("fetch", [0, "checked"]);
			location.href="@{list()}?mode=${mode}#{if param}&param=${param}#{/if}";
		});

		app.datatable.bind("select", function(event, prop){
			if ('${param}'=='edit'){
				if (($.trim(prop.bean.recordStatus) == 'N')||($.trim(prop.bean.recordStatus) == 'U')){
					location.href='@{view()}?id='+prop.bean.accountNumber+'&param=${param}';
				} else {
					location.href='@{edit()}?id='+prop.bean.accountNumber+'&param=${param}';
				}
			}
			if ('${param}'=='view'){
				location.href='@{view()}?id='+prop.bean.accountNumber+'&param=${param}';
			}
			
			//alert(prop.bean.customerNo+"-"+prop.row[1]+"-"+prop.tr);
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				//alert(props.length+"  "+props[x].bean.customerNo+"-"+props[x].row[1]);
			}
		});
		
	}else{
		return new Paging(html);
	}
}