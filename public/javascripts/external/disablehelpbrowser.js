// disable MSIE standard help function
		document.onhelp = new Function("return false;");
		window.onhelp = new Function("return false;");

		document.focus;

		// get key stroke for Firefox, Opera, Google Chrome
		document.onkeypress = keyHit;

		// get key stroke for MSIE, Safari
		var browser=navigator.userAgent;
		if (browser.search(/msie|safari/i) != -1) document.onkeydown = keyHit;

		// keystroke handling
		function keyHit(event) { 

		    // get correct keycode depending on browser
		    var keyStruck;
		    if (browser.search(/msie|safari/i) != -1) keyStruck = window.event.keyCode; else keyStruck = event.keyCode;

		    if (keyStruck == 112) {
		      if (browser.search(/msie/i) == -1) {    // disable browser standard help function for all but MSIE (see above for MSIE)
		        event.stopPropagation();
		        event.preventDefault();    
		      }
		      keyAction('help');     // call routine to replace standard help function
		    }

		    // You can add the capture of other keystrokes here

		}

		// replacement help function
		function keyAction(action) {

		  if (action == 'help') {
		    // Your new help function goes here
		    // I have used in conjunction with DHTML Window widget (v1.1) from Dynamic Drive to deliver a new help file  
		  }

		  // You can add the actions for other keystrokes here

		}