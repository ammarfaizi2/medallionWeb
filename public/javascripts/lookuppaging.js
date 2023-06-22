// Lookup Dialog Pop-up Plugin

(function($) {
	var defaults = {
			dialogTitle:'Lookup',
			dialogHeight:'auto',
			dialogWidth:'550',
			list: null,
			get: null
	};
	
	if (navigator.appName == 'Microsoft Internet Explorer') {
		var defaults = {
				dialogTitle:'Lookup',
				dialogHeight:'auto',
				dialogWidth:'550',
				list: null,
				get: null
		};
	}
	
	var methods = {
		initialize: function(options) {
			var pointer = this;
			
			if ($(this).hasClass('hasPagingLookup')) { return;
			} else { $(this).addClass('hasPagingLookup'); }			
		
			if (!options.list || !options.get) {
				$.console('error', 'Required parameter \'list\' or \'get\'  is not found!');
			}
			
			if ($('#lookuppaging').length == 0) $('body').append("<div id='lookuppaging'></div>");
			
			$('#lookuppaging').dialog({
				autoOpen : false,
				title : options.dialogTitle,
				height : options.dialogHeight,
				width : options.dialogWidth,
				modal : true,
				resizable : false, 
				stack : true
			});
			
			this.blur(function() { pointer.lookuppaging('get'); });
			
			if (options.help && (typeof(options.help) == 'object')) {
				$(options.help).click(function() {
					pointer.lookuppaging('show');
				});
			}
		},
		
		show: function(options) {
			var el = this;
			var filter = '';
			var key = '';
			
			if (options.filter) {
				if ($.isArray(options.filter)) {
					for (var i = 0, j = options.filter.length; i < j; i++) {
						var temp = options.filter[i];
						if (typeof(temp) == 'object') {
							filter += ',"' + temp.val() + '"';
						} else if (typeof(temp) == 'string') {
							filter += ',"' + temp + '"';
						}
					}
					
					if (filter.length > 0) {
						filter = '[' + filter.substring(1) + ']';
					} else {
						filter = '';
					}
				} else if (typeof(options.filter) == 'object') {
					var temp = options.filter;
					if (temp.key) {
						key = temp.key;
						if (typeof(temp.value) == 'object') {
							filter = $(temp.value).val();
						} else if(typeof(temp.value) == 'string') {
							filter = temp.value;							
						}
					} else {
						filter=$(options.filter).val();
					}					
				} else if (typeof(options.filter) == 'string') {
					filter=(options.filter);
				}
			}
			
			if (options.list) {
				if (typeof(options.list) == 'function') {
					el.trigger('list');
				}else {
					var container = '#lookuppaging';
					
					var param = el.data('urlParam');
					param.code = $(el).val();
					
					$(container).load(options.list, {'param':param}, function(data){
						checkRedirect(data);
					});
					$('#gridPick2', container) 
						.dataTable()
						.bind("select", function(event, prop){
							el.data('bean', prop.bean);
							select.call(el, prop.bean.pickData.pkcombine);
						});
					
//					if (!el.isEmpty()) {
//						$("#gridPick2_filter input", container).val(el.val());
//					}
					
					
					$("#headertr", container).children().eq(0).click();
					$(container).dialog('open');
					$('.ui-widget-overlay').css('height',$('body').height());
				}
			} else {
				$.console('error', 'The Lookup plugin is not initialized properly. The required \'list\' option is missing.');
			}
		},
		
		get: function(options) {
			var el = this;
			
			if (options.get) {
				if (el.val()) {
					var filter = '';
					var key = '';
					if (options.filter) {
						if ($.isArray(options.filter)) {
							for (var i = 0, j = options.filter.length; i < j; i++) {
								var temp = options.filter[i];
								if (typeof(temp) == 'object') {
									filter += ',"' + temp.val() + '"';
								} else if (typeof(temp) == 'string') {
									filter += ',"' + temp + '"';
								}
							}
							if (filter.length > 0) {
								filter = '[' + filter.substring(1) + ']';
							} else {
								filter = '';
							}
						} else if (typeof(options.filter) == 'object') {
							var temp = options.filter;
							if (temp.key) {
								key = temp.key;
								if (typeof(temp.value) == 'object') {
									filter = $(temp.value).val();
								} else {
									filter = temp.value;
								}
							} else {
								filter = $(options.filter).val();
							}
						} else if (typeof(options.filter) == 'string') {
							filter=(options.filter);
						}
					}
					if (typeof(options.get) == 'function') el.trigger("get");
					else if (typeof(options.get) == 'object') {
						var url = options.get.url;
						var success = options.get.success;
						var error = options.get.error;
						if (url) {
							var data = '';
							var bean = el.data('bean');
							if (bean) {
								for (x in bean.pickData.data) {
									if (data != '') data+= '';
									data += bean.pickData.data[x];
								}
								el.removeData('bean');
							}
							
							var param = el.data('urlParam');
							param.code = el.val();
							param.key = key;
							param.data = data;
							
							var formId = el.data("formId");
							if( formId == null || formId == undefined || formId == ""){
								//console.log( "this is not report" );
							}else{	
								param.reportParams = [];
								$(formId+" .reportinput").each(function(idx, el){
									var id = $(el).attr("id");
									var reportParam = {"field": id, "value":$(el).val(), "type" : $(el).parent().find("#"+id+"Type").val()};
									param.reportParams.push( reportParam );
								});
								//console.log( param );								
								
							}
							
							$.get(url, {'param' : param}, function(data, status, xhr) {
								checkRedirect(data);
								if (data) {
									if (typeof(success) == 'function') success(data);
									else {
										el.removeClass('fieldError');
										if (options.description && (typeof(options.description) == 'object')) {
											$(options.description).val(data.description);
											var hidden = "#h_" + $(options.description).attr("id");
											if ($(hidden).length > 0) $(hidden).val(data.description);
										}
									}
									if (options.key && (typeof(options.key) == 'object')) $(options.key).val(data.code);
								} else {
									if (typeof(error) == 'function') error();
									else {
										el.addClass('fieldError');
										if (options.description && (typeof(options.description) == 'object')) $(options.description).val('NOT FOUND');
									}
								}
							});
						}
					} else {
						var data = '';
						var bean = el.data('bean');
						if (bean) {
							for (x in bean.pickData.data) {
								if (data != '') data+= '';
								data += bean.pickData.data[x];
							}
							el.removeData('bean');
						}
						
						var param = el.data('urlParam');
						param.code = el.val();
						param.key = key;
						param.data = data;

						$.get(options.get, {'param':newParam}, function(data) {
							checkRedirect(data);
							if (data) {
								el.removeClass('fieldError');
								if (options.description && (typeof(options.description) == 'object')) {
									$(options.description).val(data.description);
									var hidden = "#h_" + $(options.description).attr("id");
									if ($(hidden).length > 0) $(hidden).val(data.description);
								}
								if (options.key && (typeof(options.key) == 'object')) $(options.key).val(data.code);
							} else {
								el.addClass('fieldError');
								if (options.description && (typeof(options.description) == 'object')) $(options.description).val('NOT FOUND');
							}
						});
					}
				} else {
					if (options.key && (typeof(options.key) == 'object')) $(options.key).val('');
					if (options.description && (typeof(options.description) == 'object')) $(options.description).val('');
				}
			} else {
				$.console('error', 'The Lookup plugin is not initialized properly. The required \'get\' option is missing.');
			}
		}
	};
	
	function select(id) {
		this.val(id);
		this.blur();
		$('#lookuppaging').dialog('close');
	}
	
	$.fn.lookuppaging = function(method, options) {
		if (!(method) || typeof(method) == 'object') {
			options = method;
			method = 'initialize';
		}
		
		this.each(function(el) {
			el = $(this);
			setOptions(el);
			if (methods[method]) return methods[method].call(el, options);
			else $.console('error', 'Method ' + method + ' does not exist in jquery.form');
		});
		
		function setOptions(el) {
			if (options) {
				$.each(options, function(event, fn) {
					if (typeof(fn) == 'function') { // mencoba memasukan event pada popup, tapi pada sample tidak ada 
						el.unbind(event);
						el.bind(event, fn);
					}
				});
			}
			options = $.extend({}, defaults, el.data('lookups:options'), options);
			el.data('lookups:options', options);
		};
		
		return this;
	};
	
})(jQuery);
