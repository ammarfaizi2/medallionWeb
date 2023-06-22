(function($) {
	var defaults = {
		dialogTitle:'Lookup',
		dialogHeight:'420',
		dialogWidth:'550',
		list: null,
		get: null
	};
	var methods = {
		initialize: function(options) {
			var el = this;
			
			if ($(el).hasClass('hasBinder')) {
				return;
			} else {
				$(el).addClass('hasBinder');
			}				
		},
		bind: function(source) {
			$(this).find('input').each(function() {
				var el = $(this);
				var binder = el.attr('binder');
				if (binder) {
					var value = null;				
					if (binder.contains(".")) {
						var binders = binder.split(".");
						var temp = source;
						for (var i = 0; i < binders.length; i++) {
							if (temp[binders[i]]) {
								if (i == binders.length - 1)
									value = temp[binders[i]];
								else
									temp = temp[binders[i]];
							}
						}
					} else {
						value = source[binder];
					}				
					if (value) {
						if (el.attr('type') == 'text') {
							if (el.hasClass('hasDatepicker')) {
								el.datepicker('setDate', new Date(value));
							} else if (el.hasClass('numeric')) {
								el.autoNumericSet(value);
							} else {
								el.val(value);
							}						
						}
					}
				}
			});
		}
	};
	
	$.fn.binder = function(method, options) {
		if (!(method) || typeof(method) == 'object') {
			options = method;
			method = 'initialize';
		}
		this.each(function(el) {
			el = $(this);
			setOptions(el);
			if (methods[method]) return methods[method].call(el, options);
			else $.console('error', 'Method ' + method + ' does not exist in jquery.binder');
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
			options = $.extend({}, defaults, el.data('binder:options'), options);
			el.data('binder:options', options);
		};
		
		return this;
	};
})(jQuery);