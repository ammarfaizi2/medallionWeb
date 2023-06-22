function PublishMF(html) {
	if (this instanceof PublishMF) {
/*================================================================== 
 * GUI Variable
 *================================================================== */
		
		var usingAjax = true;
		
/*==================================================================
 * Function
 *==================================================================*/
		this.parameter = function(type) {
			var p = new Object();
			p.appDate = html.tag("appDate").val();
			return p;
		};
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/
		function disabledButton(condition) {
			html.tag("btnProcess").button("option", "disabled", condition);
			html.tag("btnReset").button("option", "disabled", condition);
			html.tag("btnProcessAjax").button("option", "disabled", condition);
		}
		
		function fetchLog() {
			var param = {
				"sessionTag" : $("input[name='param.sessionTag']", html).val()
			}
			
			$().fetchAsync("@{processAjaxLog()}", {"param":param}, function(data){
				if (data.status == 'G') {
					//do nothing, stop scheduler;
					loading.dialog('close');
					disabledButton(false);
				}else if (data.status == 'W') {
					setTimeout(fetchLog, 3000);
				}else if (data.status == 'F') {
					var nice = "";
					for (var x in data.logs) {						
						var content = data.logs[x];
						if (nice == '') { nice = content;
						}else{ nice = nice + '\n'+content; }
					}
					html.tag("log").val(nice);
					
					loading.dialog('close');
					disabledButton(false);
					html.datatable.fnPageChange("first");
					//do nothing, stop scheduler;
				}
			});
		}
		
/*================================================================== 
 * Event
 *================================================================== */
		
		disabledButton(true);

		var datatable = html.tag("tablePublishMF").paging("@{PublishMF.table()}", this, function(){
				$("tbody tr", html.tag("tablePublishMF")).each(function(){
				var prop = $(this).data("prop");
				if (prop.bean) {
					if (!prop.bean.allow) {
						$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled");
					}
				}
			});
	
			//html.tag("chkAll").click().change();
			
			disabledButton(false);

		});
		html.datatable = datatable;
		
		html.tag("btnProcess").click(function(){
			datatable.trigger("fetch", [0, "checked"]);
		});
		
		html.tag("btnProcessAjax").click(function(){
			datatable.trigger("fetch", [0, "checked"]);
		});
		
		html.tag("btnReset").click(function(){
			disabledButton(true);
			
			html.tag("publishMFForm").attr('action', 'list');
			html.tag("publishMFForm").submit();
		});
		
		datatable.bind("select", function(event, prop){
			//alert(prop.bean.fundCode);
		});

		datatable.bind("selects", function(event, props){
			if (usingAjax) {
				if (props.length == 0) {
					var infoDialog = $("<input>").simpleDialog("Information", "Please choose Fund to publish", function(){
						infoDialog.dialog('close');
					});
					infoDialog.dialog('open');
					return;
				}else{
					loading.dialog('open');
					disabledButton(true);

					var param = {
						"sessionTag" : $("input[name='param.sessionTag']", html).val(),
						"processMark" : $("input[name='param.processMark']", html).val(),
						"parameter" : []
					}
					for (x in props) {
						param.parameter[param.parameter.length] = props[x].bean.fundKey+"|"+new Date(props[x].bean.validDate).getTime();
					}
					
					html.tag("log").html('');
					$().fetchAsync("@{processAjax()}", {"param" : param}, function(data){
						loading.dialog('close');
					});
					setTimeout(fetchLog, 5000);
				}
			}else{
				if (props.length == 0) {
					alert("Please choose Fund to publish");
					return;
				}else{
					for (x in props) {
						$("<input type='hidden' name='param.parameter' value='"+props[x].bean.fundKey+"|"+new Date(props[x].bean.validDate).getTime()+"'/>").appendTo(html.tag("publishMFForm"));
					}	
				}
				
				disabledButton(true);
				
				html.tag("publishMFForm").attr('action', 'process');
				html.tag("publishMFForm").submit();
			}
		});
		
//		location.href = "http://localhost:9000/publishmf/list";
		
		html.tag("btnProcess").hide();
		
		//Bila perubahan ini menyebabkan error maka nyalakan coding di bawah ini
//		html.tag("btnProcess").show();
//		html.tag("btnProcessAjax").hide();
//		usingAjax = false;
	}else{
		return new PublishMF(html);
	}
}