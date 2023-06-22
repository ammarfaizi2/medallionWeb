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
			var el = this;
			
			if ($(el).hasClass('hasLookup')) {
				return;
			} else {
				$(el).addClass('hasLookup');
			}			
		
			//Validate options
			if (!options.list) {
				$.console('error', 'Required parameter \'list\' is not found!');
				return;
			} else if (!options.get) {
				$.console('error', 'Required parameter \'get\' is not found!');
				return;
			}
			var element = "<div id='lookup'></div>"; 
			if ($('#lookup').length == 0) $('body').append(element);
			$('#lookup').dialog({
				autoOpen: false,
				title:options.dialogTitle,
				height:options.dialogHeight,
				width:options.dialogWidth,
				modal:true,
				resizable:false, 
				stack:true
			});
			// Register events
			this.blur(function() {
				el.lookup('get');
			});
			if (options.help && (typeof(options.help) == 'object')) {
				$(options.help).click(function() {
					el.lookup('show');
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
				if (typeof(options.list) == 'function') el.trigger('list');
				else {
					$('#lookup').load(options.list, {'filter':filter,'key':key}, function(data) {
						checkRedirect(data);
						var columnum = Number($('#sizePickColumn').val());
						//console.log("columnum = " +columnum);
						var table;
						if  (columnum < 3) {
							 table = 
								$('#lookup #gridPick').dataTable({
									bDestroy:true,
									bSort:false,
									bLengthChange:false,
									bJQueryUI:true,
									bProcessing:true,
									iDisplayLength:15,
									oSearch: {sSearch:el.val()}
								});
						} else {
							table = 
								$('#lookup #gridPick').dataTable({
									bDestroy:true,
									bLengthChange:false,
									bJQueryUI:true,
									bProcessing:true,
									iDisplayLength:15,
									bAutoWidth:false,
									bScrollCollapse:true,
									sScrollX:'100%',
									oSearch: {sSearch:el.val()}
								});
						}
						$('#lookup #gridPick tbody tr').die('click');
						$('#lookup #gridPick tbody tr').live('click', function() {
							/*var data = table.fnGetData(this);
							var id = data[0];*/
							var id = $(this).attr('id');
							console.log("ID = " +id);
							select.call(el, id);
						});	
						$('#lookup #gridPick').css('width','');	// This code is to hack dataTable width issue
						if  (columnum > 2) {
							$('#titleForGlobalPick').click();
						}
						$('#titleForGlobalPick').attr('style', '');
						$('#lookup').dialog('open');
						$('.ui-widget-overlay').css('height',$('body').height());
					});
				}
			} else {
				$.console('error', 'The Lookup plugin is not initialized properly. The required \'list\' option is missing.');
			}
		},
		get: function(options) {
			var el = this;
			console.log("EL = " +el.val());
			
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
							$.get(url, {'code':el.val(),'filter':filter,'key':key}, function(data, status, xhr) {
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
									//if (options.nextControl && typeof(options.nextControl == 'object')) $(options.nextControl).focus();
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
						$.get(options.get, {'code':el.val(),'filter':filter,'key':key}, function(data) {
							checkRedirect(data);
							if (data) {								
								el.removeClass('fieldError');
								if (options.description && (typeof(options.description) == 'object')) {
									$(options.description).val(data.description);
									var hidden = "#h_" + $(options.description).attr("id");
									if ($(hidden).length > 0) $(hidden).val(data.description);
								}
								if (options.key && (typeof(options.key) == 'object')) $(options.key).val(data.code);
								//if (options.nextControl && typeof(options.nextControl == 'object')) $(options.nextControl).focus();
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
		$('#lookup').dialog('close');
	}
	
	$.fn.lookup = function(method, options) {
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
					if (typeof(fn) == 'function') {
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
