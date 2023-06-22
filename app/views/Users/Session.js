function Session(html) {
	if (this instanceof Session) {
/*================================================================== 
 * GUI Variable
 *================================================================== */

/*==================================================================
 * Function
 *==================================================================*/
		this.parameter = function(type) {
			var p = new Object();
			return p;
		};
		
/*==================================================================
 * Declare popup tabel /detail
 * =================================================================*/
		function disabledButton(condition) {
			html.tag("btnRemove").button("option", "disabled", condition);
			html.tag("btnRefresh").button("option", "disabled", condition);
		}
		
/*================================================================== 
 * Event
 *================================================================== */
		
		disabledButton(true);

		var datatable = html.tag("tableSession").nopaging("@{Users.table()}", this, function(){
			$("tbody tr", html.tag("tableSession")).each(function(){
				var prop = $(this).data("prop");
				if (prop.bean) {
					if (prop.bean.allow) {
						$("td input[type=checkbox]", prop.tr).attr("disabled", "disabled");
					}
				}
			});

			disabledButton(false);
		});
		
		html.tag("btnRemove").click(function(){
			var props = datatable.selects(0, "checked");
			if (props.length == 0) {
//				messageAlertOk("Please choose Session to clear", "ui-icon ui-icon-circle-check", "Error", function(){ return false; });
				alert("Please choose Session to clear");
				return;
			}else{
				var sessionIds = [];
				for (x in props) {
					sessionIds[sessionIds.length] = props[x].bean.sessionId;
				}
				
				html.tag("tableSession").fetch("@{Users.unregisterSession()}", {"sessionIds": sessionIds}, function(){
					datatable.fnPageChange("first");
				});
			}
		});
		
		html.tag("btnRefresh").click(function(){
			datatable.fnPageChange("first");
		});
		
	}else{
		return new Session(html);
	}
}