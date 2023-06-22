function MappingPaging(html){
	if(this instanceof MappingPaging){
		var app = html.inject(this, true);
		app.datatable = app.mappingTable.offlinepaging();
		var newNoSeq = 0;
		app.btnAddMapping.click(function(){
			app.datatable.fnAddData( ["<input type=\"text\" name=\"pdetails["+newNoSeq+"].mappingCode[].fromCode\"/>", 
			                          "<input type=\"text\" name=\"pdetails["+newNoSeq+"].mappingCode[].toCode\"/>", 
			                          "<input type=\"button\" value=\"delete\"/ class=\"deletemappingbutton ui-button ui-widget ui-state-default ui-corner-all\">"] );
		});
		$("input.deletemappingbutton").live("click", function(){
			var cRow = $(this).parents('tr');
			var rowNumber = app.datatable.fnGetPosition(cRow[0]);
			app.datatable.fnDeleteRow(rowNumber);
		});
	}else{
		return new MappingPaging(html);
	}
}