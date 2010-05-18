function setupLink(url) {
    return function(e) { $.get(url, function(data) { $("#changer").html(data); }); }
}

function getStep(n) {
    $.get("tutorial", { step: n }, function(data) { $("#tuttext").html(data); }); 
}

$(document).ready(
    function() {
	$("#console").console({
	    promptLabel: 'Clojure> ',
	    commandValidate:function(line){
		if (line == "") return false;
		else return true;
	    },
	    commandHandle:function(line){
		var data;
		jQuery.ajax({
		    url: "magics" + "?code=" + encodeURIComponent(line),
		    success: function(result) {data = result;},
		    async:   false
		}); 
		return [{msg: data,
			 className:"jquery-console-message-value"}];
	    },
	    welcomeMessage:'Enter some Clojure code, and it will be evaluated.',
	    autofocus:true,
	    animateScroll:true,
	    promptHistory:true
	});

	$("#about").click(setupLink("about"));
	$("#links").click(setupLink("links"));
	$("#tutorial").click(function(e) {
	    $.get("tutorial", {step: 0}, function(data) { 
		$("#changer").html(data);
		var step = 1;
		$("#continue").click(function(e) {
		    if(step < 2 ) { step += 1; }
			getStep(step);
		});
		$("#back").click(function(e) {
		    if(step > 1) { step -= 1; }
		    getStep(step);
		});
	    });
	});
    });
