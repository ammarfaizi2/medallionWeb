function Input(tag, jaction, cb) {
	if (this instanceof Input) {
		if (cb) {
			tag.data('action', jaction);
			if (cb[jaction.id]) cb[jaction.id](tag);

			// Key
			if (cb[jaction.id+"Keyup"]) { tag.bind('keyup', function(e){ cb[jaction.id+"Keyup"](tag, e); }); }
			if (cb[jaction.id+"Keydown"]) { tag.bind('keydown', function(e){ cb[jaction.id+"Keydown"](tag, e); }); }
			if (cb[jaction.id+"Keypress"]) { tag.bind('keypress', function(e){ cb[jaction.id+"Keypress"](tag, e); }); }
			
			// Focus
			if (cb[jaction.id+"Focus"]) { tag.bind('focus', function(e){ cb[jaction.id+"Focus"](tag, e); }); }
			if (cb[jaction.id+"Focusin"]) { tag.bind('focusin', function(e){ cb[jaction.id+"Focusin"](tag, e); }); }
			if (cb[jaction.id+"Focusout"]) { tag.bind('focusout', function(e){ cb[jaction.id+"Focusout"](tag, e); }); }

			// Click
			if (cb[jaction.id+"Click"]) { tag.bind('click', function(e){ cb[jaction.id+"Click"](tag, e); }); }
			if (cb[jaction.id+"Dblclick"]) { tag.bind('dblclick', function(e){ cb[jaction.id+"Dblclick"](tag, e); }); }
			
			// Mouse
			if (cb[jaction.id+"Mousedown"]) { tag.bind('mousedown', function(e){ cb[jaction.id+"Mousedown"](tag, e); }); }
			if (cb[jaction.id+"Mouseenter"]) { tag.bind('mouseenter', function(e){ cb[jaction.id+"Mouseenter"](tag, e); }); }
			if (cb[jaction.id+"Mouseleave"]) { tag.bind('mouseleave', function(e){ cb[jaction.id+"Mouseleave"](tag, e); }); }
			if (cb[jaction.id+"Mousemove"]) { tag.bind('mousemove', function(e){ cb[jaction.id+"Mousemove"](tag, e); }); }
			if (cb[jaction.id+"Mouseout"]) { tag.bind('mouseout', function(e){ cb[jaction.id+"Mouseout"](tag, e); }); }
			if (cb[jaction.id+"Mouseover"]) { tag.bind('mouseover', function(e){ cb[jaction.id+"Mouseover"](tag, e); }); }
			if (cb[jaction.id+"Mouseup"]) { tag.bind('mouseup', function(e){ cb[jaction.id+"Mouseup"](tag, e); }); }

			// Misc
			if (cb[jaction.id+"Blur"]) { tag.bind('blur', function(e){ cb[jaction.id+"Blur"](tag, e); }); }
			if (cb[jaction.id+"Change"]) { tag.bind('change', function(e){ cb[jaction.id+"Change"](tag, e); }); }
			if (cb[jaction.id+"Hover"]) { tag.bind('hover', function(e){ cb[jaction.id+"Hover"](tag, e); }); }
		}
	}else{
		return new Input(tag, jaction, cb);
	}
};