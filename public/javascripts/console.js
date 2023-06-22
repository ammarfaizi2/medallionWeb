//$.enableConsole = true;
//$.fn.console = $.console = function(method) {
//	if (window.console && console[method] && $.enableConsole)
//		console[method].apply(this, [].splice.call(arguments,1));
//	return this;
//}
//
if (!window.console) console = {};
console.log = console.log || function(){};
console.warn = console.warn || function(){};
console.error = console.error || function(){};
console.info = console.info || function(){};
console.debug = console.debug || function(message){console.log(message)};