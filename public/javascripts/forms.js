//***************************************************
// Pop-up Dialog Form Plugin
//***************************************************
// Author		: Junid Baharuddin
// Description	: Include this script for pop-up 
//                dialog style form
//***************************************************
// References: 
// http://docs.jquery.com/Plugins/Authoring
// http://wintoni.us/post/123029056/jquery-plugin-patterns
// http://www.authenticsociety.com/blog/jQueryPluginTutorial_Beginner
//***************************************************
// Notes:
// At this point the plugin does not support events,
// since there is currently no requirement for it
//***************************************************

//***************************************************
// This is the initialization code
//***************************************************
(function($) {
	var defaults = {
		'title': 'Title',
		'height': 500,
		'width': 800
	};
	
	var methods = {
		initialize: function(options) {
			var element = 
				"<div id='loading' class='loading'><img src='@@{'/public/images/loading2.gif'}' style='margin: 10px' /></div>" +
				"<form id='form' class='form' />" +
				"<div id='error' class='error' style='display:none' />";
			this.html(element);	
			this.dialog({
				'autoOpen': false,
				'title':options.title,
				'closeOnEscape':false,
				'modal':true,
				'resizable':false,
				'height':options.height,
				'width':options.width
			});
			this.data('form:init', true);			
		},
		entry: function(options) {
			show.call(this, 'entry/', options.id, options.param);
		},
		edit: function(options) {
			show.call(this, 'edit/', options.id, options.param);
		},
		view: function(options) {
			show.call(this, 'view/', options.id, options.param);			
		},
		save: function(options) {
			var action = 'save/' + options.id + '?mode='+ options.mode;
			post.call(this, action, options.id, options.mode);
		},
		confirm: function(options) {
			var action = 'confirm/' + options.id + '?mode=' + options.mode;
			post.call(this, action, options.id, options.mode);
			this.dialog('close');
		},		
		hapus: function(options) {
			show.call(this, 'hapus/', options.id, options.param);	
			this.dialog('close');
		},
		back: function(options) {
			var action = 'back/' + options.id + '?mode=' + options.mode;
			post.call(this, action, options.id, options.mode);
		},
		close: function() {
			this.dialog('close');
		}
	};	
	
	//***************************************************
	// Helper function to display form
	//***************************************************
	function show(action, id, param) {
		$.console('debug', 'action=' + action);
		var init = this.data('form:init');
		if (!init) {
			$.error('jquery.form has not been initialized!');
			return;
		}		
		
		var el = this;
		el.find('#loading').show();
		el.find('#form').hide();
		el.find('#error').hide();
		el.dialog('open');
		el.find('#form').load(action, {'id':id,'param':param} , function(response, status, xhr) {
			if(status == 'error') {
				var msg = "Error when loading form: ";
				el.find('#error').html(msg + xhr.status + " " + xhr.statusText);
				el.find('#error').show();
				el.find('#loading').hide();
			} else {
				el.find('#loading').hide();
				el.find('#form').show();
			}
		});	
	};
	//***************************************************
	// Helper function to post data
	//***************************************************
	function post(action, id, mode) {
		var init = this.data('form:init');
		if (!init) {
			$.error('jquery.form has not been initialized!');
			return;
		}		

		var el = this;
		this.find('#loading').show();
		this.find('#form').hide();
		this.find('#error').hide();
		$.post(action, this.find('#form').serialize(), function(response, status, xhr) {
			el.find('#form').html(response);
			el.find('#loading').hide();
			el.find('#form').show();
		});
	};
	
	$.fn.form = function(method, options) {
		if (!(method) || typeof(method) == 'object') {
			options = method;
			method = 'initialize';
		}
		
		//alert(this.attr("id") + ":" + method + "(" + this.length + ")");
		this.each(function(el) {
			el = $(this);
			setOptions(el);
			
			if (methods[method]) return methods[method].call(el, options);
			else $.error('Method ' + method + ' does not exist in jquery.form');
		});
		
		function setOptions(el) {
			options = $.extend({}, defaults, el.data('form:options'), options);
			el.data('form:options', options);
		};
		
		return this;
	};
})(jQuery);
