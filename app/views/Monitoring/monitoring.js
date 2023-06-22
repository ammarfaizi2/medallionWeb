var listAll;

$(document).ready(function() {	
	$('#download').hide();
	$('.buttons input:button , .logout').button();
	$('#tabs').tabs();
	$('#gridLogServer').dataTable({
		aaSorting: [[1, 'asc']],
		bAutoWidth: false,
		bDestroy: true,
		bJQueryUI: true,
		bLengthChange: false,
		sPaginationType: 'full_numbers',
		bInfo: true,
		bPaginate: true,
		iDisplayLength:7               						
	});
	$('#gridLogWeb').dataTable({
		aaSorting: [[1, 'asc']],
		bAutoWidth: false,
		bDestroy: true,
		bJQueryUI: true,
		bLengthChange: false,
		sPaginationType: 'full_numbers',
		bInfo: true,
		bPaginate: true,
		iDisplayLength:7               						
	});
	$('#gridLogScheduler').dataTable({
		aaSorting: [[1, 'asc']],
		bAutoWidth: false,
		bDestroy: true,
		bJQueryUI: true,
		bLengthChange: false,
		sPaginationType: 'full_numbers',
		bInfo: true,
		bPaginate: true,
		iDisplayLength:7               						
	});
	
	//tab query

	$('#btnRun').click(function(){	
		loading.dialog('open');
		//listAll="";
		//$('#btnRun').disable();
		$("#btnRun").attr('disabled','disabled');
		console.log("asds");
		
		$('#tableQueryAjax').html('').show();
		
		$('#tableQueryAjax').append("<table id='queryTable'><thead id='queryHead'></thead><tbody id='queryBody'></tbody></table>");

		
		var sQuery = $('#query').val();
		var limit = $('#limit').val();
		$.post("@{goQuery()}", {"query" : sQuery, "limit" : limit}, function(data,result){
			listAll=data.data;
			var head = $("#queryHead");
			if (data.columns) {
				var str = "<tr>";
				for (idx in data.columns) {
					str += "<td>"+data.columns[idx]+"</td>";
				}
				str += "</tr>";
				head.append(str);
			}

			var body = $("#queryBody");
			if (data.data) {
				console.log(data.data);

				for (idx in data.data) {
					var str = "<tr>";
					var row = data.data[idx];
					for (idx2 in row) {
						var value = (row[idx2]==null) ? "" : row[idx2];
						str += "<td>"+value+"</td>";
					}
					str += "</tr>";
					body.append(str);
				}
			} 
			var pDt = $("#queryTable").dataTable({
				aaSorting: [[1, 'asc']],
				bAutoWidth: false,
				bDestroy: true,
				bJQueryUI: true,
				bLengthChange: false,
				sPaginationType: 'full_numbers',
				bInfo: true,
				bPaginate: true
			});
			pDt.wrap("<div style='overflow:scroll'>");
			str = null;
			
			console.log("zzzz");
			$('#download').show();
			loading.dialog('close');
			$("#btnRun").removeAttr('disabled');

		});
		console.log("yyy");
	});

	$('#download').click(function(){  
		var reportName = prompt("Report Name :");
	    JSONToCSVConvertor(listAll, reportName, true);
	});
	$('#btnReset').click(function(){  
		$('#query').val('');
	});
	
	//end tab query
});

function JSONToCSVConvertor(JSONData, ReportTitle, ShowLabel) {
    //If JSONData is not an object then JSON.parse will parse the JSON string in an Object
    var arrData = typeof JSONData != 'object' ? JSON.parse(JSONData) : JSONData;
    
    var CSV = '';    
    //Set Report title in first row or line
    
    CSV += ReportTitle + '\r\n\n';

    //This condition will generate the Label/Header
    if (ShowLabel) {
        var row = "";
        
        //This loop will extract the label from 1st index of on array
        for (var index in arrData[0]) {
            
            //Now convert each value to string and comma-seprated
            row += index + ',';
        }

        row = row.slice(0, -1);
        
        //append Label row with line break
        CSV += row + '\r\n';
    }
    
    //1st loop is to extract each row
    for (var i = 0; i < arrData.length; i++) {
        var row = "";
        
        //2nd loop will extract each column and convert it in string comma-seprated
        for (var index in arrData[i]) {
        	var value = (arrData[i][index]==null)?"":arrData[i][index];
            row += '"' + value + '",';
        }

        row.slice(0, row.length - 1);
        
        //add a line break after each row
        CSV += row + '\r\n';
    }

    if (CSV == '') {        
        alert("Invalid data");
        return;
    }   
    
    //Generate a file name
    //var fileName = "MyReport_";
    //this will remove the blank-spaces from the title and replace it with an underscore
    var fileName = ReportTitle.replace(/ /g,"_");   
    
    //Initialize file format you want csv or xls
    var uri = 'data:text/csv;charset=utf-8,' + escape(CSV);
    
    // Now the little tricky part.
    // you can use either>> window.open(uri);
    // but this will not work in some browsers
    // or you will not get the correct file extension    
    
    
    //For IE
    if (navigator.userAgent.search("MSIE") >= 0) {
    	var IEwindow = window.open();
    	IEwindow.document.write('sep=,\r\n' + CSV);
    	IEwindow.document.close();
    	IEwindow.document.execCommand('SaveAs', true, fileName + ".csv");
    	IEwindow.close();
    //Another browser
    } else {
    	//this trick will generate a temp <a /> tag
        var link = document.createElement("a");    
        link.href = uri;
        
        //set the visibility hidden so it will not effect on your web-layout
        link.style = "visibility:hidden";
        link.download = fileName + ".csv";
        
        //this part will append the anchor tag and remove it after automatic click
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
    
}