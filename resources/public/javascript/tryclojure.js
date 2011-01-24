var pageNum = -1;
var page = null;
var pages = [
    {
        text: "<h3>Welcome to the first page of the interactive tutorial!</h3>\n" +
              "<p>In order to move on to the next page please type <code>(+ 1 2)</code>.</p>",
        verify: function(data) {
            // strictly validate by expression and result
            // otherwise the user could just type '3' to move on.
            return (data.expr == "(+ 1 2)" && data.result == "3");
        }
    },
    {
        text: "<h3>This is page two!</h3>\n" + 
              "<p>Wow you did it! Looks like you have a talent for this!</p>\n" + 
              "<p>I'm going to have to try something harder for you. In order to make it to " + 
              "the next page type: <code>(map inc [1 2 3 4])</code>.</p>\n",
        verify: function(data) {
            // being lazy and just verifying on result
            return (data.result == "(2 3 4 5)");
        }
    },
    {
        text: "<h3>Third and final page!</h3>\n" + 
              "<p>Your intelligence is too great for this lame tutorial. It's time for you to " +
              "contribute to some open source Clojure projects. Have fun.</p>",
        verify: function(data) { return false; }
    }
];

function showPage(n) {
    var res = pages[n];
    if (res) {
        $("#changer").html(res.text);
        pageNum = n;
        page = res;
    }
}

function setupLink(url) {
    return function(e) { $("#changer").load(url, function(data) { $("#changer").html(data); }); }
}

function setupExamples(controller) {
    $(".code").click(function(e) {
	controller.promptText($(this).text());
    });
}

function getStep(n, controller) {
    $("#tuttext").load("tutorial", { step: n }, function() { setupExamples(controller); });
}

function eval_clojure(code) {
    var data;
    $.ajax({
        url: "eval.json",
        data: { expr : code },
        async: false,
        success: function(res) { data = res; }
    });
    return data;
}

function html_escape(val) {
    var result = val;
    result = result.replace(/\n/g, "<br/>");
    result = result.replace(/[<]/g, "&lt;");
    result = result.replace(/[>]/g, "&gt;");
    return result;
}

function doCommand(input, report) {
    switch (input) {
    case 'tutorial':
        showPage(0);
        report();
        return true;
    case 'back':
        if (pageNum > 0) {
            showPage(pageNum - 1);
            report();
            return true;
        } else {
            return false;
        }
    case 'restart':
        if (pageNum > 0) {
            showPage(0);
            report();
            return true;
        } else {
            return false;
        }
    default:
        return false;
    }
}

function onValidate(input) {
    return (input != "");
}

function onHandle(line, report) {
    var input = line.trim();

    // handle commands
    if (doCommand(input, report)) return;

    // perform evaluation
    var data = eval_clojure(input);

    // handle error
    if (data.error) {
        return [{msg: html_escape(data.message), className: "jquery-console-message-error"}];
    }

    // handle page
    if (page && page.verify(data)) {
        showPage(pageNum + 1);
    }

    // display expr results
    return [{msg: html_escape(data.result), className: "jquery-console-message-value"}];
}

$(document).ready(
    function() {
	var controller = $("#console").console({
	    welcomeMessage:'Enter some Clojure code, and it will be evaluated.',
	    promptLabel: 'Clojure> ',
      commandValidate: onValidate,
      commandHandle: onHandle,
	    autofocus:true,
	    animateScroll:true,
	    promptHistory:true
	});

	$("#about").click(setupLink("about"));
	$("#links").click(setupLink("links"));
        $("#tutorial").click(function(e) {
            showPage(0);
        });
  /*
	$("#tutorial").click(function(e) {
	    $("#changer").load("tutorial", {step: 0}, function(data) { 
		var step = 1;
		$("#continue").click(function(e) {
		    if(step < 6 ) { step += 1; }
		    getStep(step, controller);
		    $("#tuttext").scrollTop(0);
		});
		$("#back").click(function(e) {
		    if(step > 1) { step -= 1; }
		    getStep(step, controller);
		    $("#tuttext").scrollTop(0);
		});
	    });
	});
  */
    });