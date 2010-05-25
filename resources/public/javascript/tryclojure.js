function setupLink(url) {
    return function(e) { $("#changer").load(url, function(data) { $("#changer").html(data); }); }
}

function getStep(n) {
    $("#tuttext").load("tutorial", { step: n });
}

function setupExamples() {
    $("span").click(function(e) { alert($("this").html()); });
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
	    $("#changer").load("tutorial", {step: 0}, function(data) { 
		var step = 1;
		$("#continue").click(function(e) {
		    if(step < 6 ) { step += 1; }
		    getStep(step);
		    $("#tuttext").scrollTop(0);
		    //setupExamples();
		});
		$("#back").click(function(e) {
		    if(step > 1) { step -= 1; }
		    getStep(step);
		    $("#tuttext").scrollTop(0);
		});
	    });
	});
    });