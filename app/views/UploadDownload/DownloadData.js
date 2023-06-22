function DownloadData(html) {
	if (this instanceof DownloadData) {
		var vTable;
		var dataFilters;
/*================================================================== 
 * GUI Variable
 *================================================================== */

/*==================================================================
 * Function
 *==================================================================*/
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/
		function clearFilter() {
			$('#filterDownload').empty('');
		}
		function refreshFilter(data) {
			clearFilter();
			var safieldset = $("<fieldset id='filterBy'>");
			safieldset.appendTo($('#filterDownload'));
			var alegend = $("<legend>Filter by</legend>");
			alegend.appendTo(safieldset);
			
			for(var i=0; i<data.length;i++){
				var curF_ = data[i];

				var adiv = $( "<div id=\"div-"+curF_.fieldName+"\" >" );
				adiv.appendTo(safieldset);
				
				var alabel = $("<label id='fieldName"+i+"'>"+curF_.fieldName+"</label>");
				alabel.appendTo( adiv );
				
				var aSelect = $("<select id=\"defOperator"+i+"\" class=\"filterForDownload\">");
				aSelect.append("<option value=\""+curF_.defOperator.lookupId+"\" selected=\"selected\">"+curF_.defOperator.lookupDescription+"</option>");
				aSelect.appendTo( adiv );

				var ainput = $('<input type="text" id="defValue'+i+'" value="'+( curF_.defValue === null ? "" : curF_.defValue )+'" style="width:150px;margin-left: 10px" class="filterForDownload"/>');
				if( curF_.dataType === "DATE" ){
					var formatKu = "${appProp.jqueryDateFormat}";
					ainput.datepicker({dateFormat:formatKu});
				}
				ainput.appendTo( adiv );
				
				var aDataType = $("<input type=\"hidden\" value=\""+curF_.dataType+"\" id=\"dataType"+i+"\"/>"); 
				aDataType.appendTo( adiv );
			}
		}
		
/*================================================================== 
 * Event
 *================================================================== */
		
		$("#pFilter1").add($("#pFilter2")).hide();
		
		
		$('#template').lookup({
			list:'@{Pick.udProfiles()}',
			get:{
				url:'@{Pick.udProfileByName()}',
				success: function(data){
					$("#template").removeClass('fieldError');
					$("#templateKey").val(data.profileKey);
					$("#templateDesc").val(data.name);
					$("#separatorCsv").val(data.separatorCs);
					$("#separatorTxt").val(data.separatorTxt);
					if ((data.filters!=undefined)) {
						dataFilters = JSON.parse(data.filters);
						refreshFilter(dataFilters);
					} else {
						dataFilters = undefined;
						clearFilter();
					}
					$('#errTemplate').html('');
				},
				error : function(data){
					$("#template").addClass('fieldError');
					$("#template").val('');
					$("#templateKey").val('');
					$("#templateDesc").val('NOT FOUND');
					$("#separatorCsv").val('');
					$("#separatorTxt").val('');
					$('#filterDownload').empty('');
				}
			},
			filter:'Download',
			description:$('#templateDesc'),
			help:$('#templateHelp')
		});
		
		$('#template').change(function(){
			if($('#template').val()==''){
				$('#filterDownload').empty('');
			}
		});
		
		$("#btnPopulate").click(function(){
			$('#errGlobalDownload').html("");
			if (($('#template').hasClass('fieldError'))||($('#template').val()=='')){
				$('#errTemplate').html("Required");
				return false;
			}
			$('#errTemplate').html("");
			var arrFilter = [];
			if (dataFilters!=undefined){
				for(var i=0; i<dataFilters.length;i++){
					if (($('#defOperator'+i+'').val() == '') &&($('#defValue'+i+'').val() != '')){
						$('#errGlobalDownload').html('Operator not found!');
						return false;
					}else{
						var defVal_ = ( $('#defValue'+i+'').val() == "" ? " " : $('#defValue'+i+'').val() );
						arrFilter.push($('#fieldName'+i+'').html()+";"+$('#defOperator'+i+'').val()+";"+defVal_+";"+$('#dataType'+i+'').val());
					}
				}
			} else {
				arrFilter.push('');
			}

			var profileId = $("#templateKey").val();
			
			if (profileId != '') {
				$.get("@{UploadDownload.populateDownload()}", {'id':profileId, 'arrFilter':arrFilter}, function(data) {
					checkRedirect(data);
					if(vTable != null) vTable.fnDestroy();
					
					$("#tblHead").empty();
					$("#tblBody").empty();
					$('#idTablePopulate').css('display','');
					var tr = $("<tr>").appendTo($("#tblHead"));
					var details = data.details;
					for (x in details) {
						tr.append("<td width='20%'><b>"+details[x].targetField+"</b></td>");
					}
					
					for (x in data.datas) {
						var vTr = $("<tr>").appendTo($("#tblBody"));
						var dataTr = data.datas[x];
						for (var y = 0; y < data.details.length; y++) {	
							$("<td>"+dataTr[y]+"</td>").appendTo(vTr);	
						}
					}
					var separatorCsv = $('#separatorCsv').val();
					var separatorTxt = $('#separatorTxt').val();
					var filename = $.trim($("#templete option:selected").html());
					
					vTable = $("#tblPopulate").dataTable({
						bJQueryUI:true,
						bRetrieve:true, 
						sScrollX:'100%',
						bAutoWidth: true,
						bScrollCollapse: true,		
						sPaginationType: 'full_numbers'
					});
				});
			}
			$('.buttonDownloadFile').css('display','');
			$('#btnPopulate').css('display','none');
			$('#btnClear').css('display','none');
			$('#btnReset').css('display','');
			$('#template').attr('disabled', 'disabled');
			$('#templateHelp').attr('disabled', 'disabled');
			$('.filterForDownload').attr('disabled', 'disabled');
		});
		
		$('#btnClear').click(function(){
			location.href = '@{UploadDownload.downloaddata()}';
		});
		
		$('#btnReset').click(function(){
			if(vTable != null) vTable.fnClearTable();
			$("#tblHead").empty();
			$("#tblBody").empty();
			$('#idTablePopulate').css('display','none');
			$('.buttonDownloadFile').css('display','none');
			$('#btnPopulate').css('display','');
			$('#btnClear').css('display','');
			$('#btnReset').css('display','none');
			$('#template').attr('disabled', false);
			$('#templateHelp').attr('disabled', false);
			$('.filterForDownload').attr('disabled', false);		
		});
				
		$('#btnDownloadXls').click(function(){
			var profileId = $("#templateKey").val();
			var allFilter = "";
			if (dataFilters != undefined){
				allFilter = parseFilters(dataFilters);
			}
			if (profileId != ''){
				location.href='@{generateFileDownload()}?id='+profileId+'&downloadTo=xls&'+allFilter;	
			}
		});
		
		$('#btnDownloadXlsx').click(function(){
			var profileId = $("#templateKey").val();
			var allFilter = "";
			if (dataFilters != undefined){
				allFilter = parseFilters(dataFilters);
			}
			if (profileId != ''){
				location.href='@{generateFileDownload()}?id='+profileId+'&downloadTo=xlsx&'+allFilter;	
			}
		});
		
		$('#btnDownloadCsv').click(function(){
			var profileId = $("#templateKey").val();
			
			var allFilter = "";
			if (dataFilters != undefined){
				allFilter = parseFilters(dataFilters);
			}
			if (profileId != ''){
				location.href='@{generateFileDownload()}?id='+profileId+'&downloadTo=csv&'+allFilter;	
			}
		});

		$('#btnDownloadTxt').click(function(){
			var profileId = $("#templateKey").val();
			var allFilter = "";
			if (dataFilters != undefined){
				allFilter = parseFilters(dataFilters);
			}
			if (profileId != ''){
				location.href='@{generateFileDownload()}?id='+profileId+'&downloadTo=txt&'+allFilter;	
			}
		});
		$('#btnDownloadXml').click(function(){
			var profileId = $("#templateKey").val();
			var allFilter = "";
			if (dataFilters != undefined){
				allFilter = parseFilters(dataFilters);
			}
			if (profileId != ''){
				location.href='@{generateFileDownload()}?id='+profileId+'&downloadTo=xml&'+allFilter;	
			}
		});
		
		function parseFilters(dataFilters){
			var allFilter = "";
			for( var x_ in dataFilters ){
				var arrFilter = [];
				var tmpFilter = "";
				var curObj = dataFilters[x_];
				arrFilter.push( curObj.fieldName+";"+curObj.defOperator.lookupId+";"+$('#defValue'+x_+'').val()+";"+curObj.dataType);
				tmpFilter = "arrFilter[]="+arrFilter+"&";					
				allFilter = allFilter + tmpFilter;
			}
			return allFilter;
		}
		
	}else{
		return new DownloadData(html);
	}
}