function Migrate(html) {
	if (this instanceof Migrate) {
		
/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, false);
		var PROGRESS_STATUS = "PROGRESS_STATUS";
		var PROGRESS_MESSAGE = "PROGRESS_MESSAGE";
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		
/* =========================================================================== 
 * Function
 * ========================================================================= */
		
		function getParameter(c) {
			var o = new Object();
			$("input[type=text]", c).each(function(){
				var name = $(this).attr("id");
				var disabled = $(this).attr("disabled");
				if (!disabled) {
					var value = $(this).val();
					o[name] = value;
				}
			});
			return o;
		}
		
		function processData(process, tr) {
			var filter = getParameter($(".parameter", tr));
			var result = html.fetchAsync("@{GeneralUpload.migrateData()}", {"process" : process, "filter" : filter}, function(data){
				console.log("Return with "+data.status+"   "+data.message);
				tr.data(PROGRESS_STATUS, data.status);
				tr.data(PROGRESS_MESSAGE, data.message);
				
				$(".btnProcess", html).button("option", "disabled", false);
				
				if (data.status == "SUCCESS") {
					$(".lblLastProcess", tr).html(data.audit.strLastProcess);
				}
			});
		}
		
		function doTimer(process, tr) {
			setTimeout(function(){
				var result = html.fetchAsync("@{GeneralUpload.progressData()}", {"process" : process}, function(data){
					var progressStatus = tr.data(PROGRESS_STATUS);
					
					if (progressStatus == "FAIL") {
						var errormsg = tr.data(PROGRESS_MESSAGE);
						$(".lblStatus", tr).html(errormsg);
					}else{
						$(".barProgress", tr).progressbar({value: data.progress });
						
							
						$(".lblDuration", tr).html(data.duration);
						$(".lblStatus", tr).html(data.info);
						
						if (data.progress < 100) {
							doTimer(process, tr);
						}
					}
				});	
			}, 1000);
		}
		
		function isMonth(val) {
			if (Number(val) >= 1 || Number(val) <= 12) return true;
			return false;
		}
		
		function isYear(val) {
			if (val.length != 4) return false;
			if (Number(val) >= 1000 || Number(val) <= 9999) return true;
			return false;
		}
		
		function validateMonthYear(scope, process) {
			var conMonth = $("#monthErr", scope).required($("#month", scope).isEmpty());
			var conYear = $("#yearErr", scope).required($("#year", scope).isEmpty());
			var conReq = conMonth && conYear;
			
			var conValidMonth = true;
			if (conReq) { conValidMonth = $("#monthErr", scope).valid($("#month", scope).isMonth(), "Invalid Month"); }
			
			var conValidYear = true;
			if (conReq) { conValidYear = $("#yearErr", scope).valid($("#year", scope).isYear(), "Invalid Year"); }
			
			return conMonth && conYear && conValidMonth && conValidYear; 
		}
		
		function processRunning() {
			var conResult = true;
			var result = html.fetchSync("@{getRunningProcess()}", {}, function(data){
				if (data.runProcess) {
					alert("Please wait, other process is currently running");
					conResult = true;
				}else{
					conResult = false;
				}				
			});
			return conResult;
		}		
		
		function validate(scope, process) {
			if (process == 'PROCESS_CIPS_ENTITTLEMENT') {
				return $("#recordingDateErr", scope).required($("#recordingDate", scope).isEmpty());
			}
			if (process == 'PROCESS_CIPS_SUMMARY_HOLDING') {
//				var conCaType = $("#caTypeErr", scope).required($("#caType", scope).isEmpty());
//				var conSecurity = $("#securityErr", scope).required($("#security", scope).isEmpty());
//				var conRecDate = $("#recordingDateErr", scope).required($("#recordingDate", scope).isEmpty());
//				var conRefNo = $("#referenceNoErr", scope).required($("#referenceNo", scope).isEmpty());
//				return conCaType && conSecurity && conRecDate && conRefNo;
				
				return $("#asOfDateErr", scope).required($("#asOfDate", scope).isEmpty());
			}
			if (process == 'PROCESS_CIPS_CUST_ACTIVITY') { return validateMonthYear(scope, process); }
			if (process == 'PROCESS_CIPS_LKPBU') { return validateMonthYear(scope, process); }
			if (process == 'PROCESS_CIPS_POSISI_BI') { return validateMonthYear(scope, process); }
		}
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.table.dataTable({
    		"bJQueryUI": true,
	        "bLengthChange": false,
	        "bSort": false,
			"bPaginate": false,
			"bFilter": false
    	});
		
/* =========================================================================== 
 * Event
 * ========================================================================= */
//		app.caType.dynapopup('PICK_CA_TYPE', '', 'security', function(data){
//			if (data.securityType) {
//				app.security.dynapopup('PICK_SC_MASTER_BY_SEC_TYPE', data.securityType, 'recordingDate', function(data){
//					
//				});
//			}
//		});
		
		if ($.browser.msie) {
			$(".barProgress", html).css("padding", "0em 0em 0.5em 0em");
			$(".progress-label", html).css("padding", "0em 0em 0em 0em");
		}
		
		$("div.buttons").hide();
		$(".barProgress", html).progressbar({value: 0 });
		$(".divAccordion", html).accordion({collapsible: false});
		$(".btnProcess", html).button().click(function(){
			var process = $(this).attr("process");
			
			if (!processRunning()) {
				if (validate($(this).parent().parent(), process)) {
					$(".btnProcess", html).button("option", "disabled", true);
					var tr = $(this).parents("tr:first");
					
					$(".lblStatus", tr).html("...");
					$(".lblDuration", tr).html("...");
					
					processData(process, tr);
					doTimer(process, tr);
				}
			}
		});
	}else{
		return new Migrate(html);
	}
	
//	select * from SC_ACTION_TEMPLATE
//	where action_code = 'BOND-AMORT'
//
//	SC_ACTION_TEMPLATE.security_type
//	 
//	select * from sc_master 
//	where security_type = 'BOND'	

}
