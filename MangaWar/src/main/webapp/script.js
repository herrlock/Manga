var md = {
	downloads : {},
    showOnly : function(c, duration) {
    	console.log("showOnly", c, duration);
        if (c === "dl" || c == "jd") {
            $("form#eingabe div").each(function(i, e) {
                var $this = $(this);
                if ($this.hasClass(c)) {
                    $this.slideDown(duration);
                } else {
                    $this.slideUp(duration);
                }
            });
        }
    },
    showSelected : function(duration) {
    	console.log("showSelected", duration);
        md.showOnly($("#selector > option:selected")[0].value, duration);
    },
    updateAll : function() {
    	console.group("updateAll");
    	$.each(md.downloads, (k,v) => md.update(k));
    	console.groupEnd();
    },
    update : function(uuid) {
    	console.log("update", uuid);
    	var url = "j/download/progress?uuid=" + encodeURIComponent(uuid);
    	$.getJSON(url, function(response) {
    		md.downloads[uuid].current = response.progress;
    		md.downloads[uuid].max = response.maxProgress;
    		var $pb = $("pb_" + uuid);
    		$pb.value = response.progress;
    		$pb.max = response.maxProgress;
    	});
    },
    createEntry : function(uuid) {
    	console.log("createInterval", uuid);
    	var url = $("#url").text();
    	var legend = "<legend>" + md.encodeHTML(url) + "</legend>";
    	var progress = "<progress id='pb_" + uuid + "' value='100' max='100'/>";
    	$("#bars").append("<fieldset>" + legend + progress + "</fieldset>")
    	md.downloads[uuid] = {
    		uuid : uuid,
    		url : url
    	};
    },
    encodeHTML : function(string) {
	    var tagsToReplace = {
	        '&': '&amp;',
	        '<': '&lt;',
	        '>': '&gt;'
	    };
	    return string.replace(/[&<>]/g, function(tag) {
	        return tagsToReplace[tag] || tag;
	    });
	}
};

$(function() {
    md.showSelected(0);
    var updateAllInterval = window.setInterval(md.updateAll, 5000);
    $("#selector").change(md.showSelected);
    $("#submit").click(function() {
        var queryArr = $("#eingabe > div > input")
		    .filter((i,elem) => $(elem).is(":visible"))
            .map((i,elem) => encodeURIComponent(elem.id) + "=" + encodeURIComponent(elem.value))
            .toArray();
        var query = "?" + queryArr.join("&");
        var url = "j/download/start" + query;
        $.get(url, md.createEntry);
    });
    $("#stopServer").click(function() {
        $.get("server/stop", function() {
			$("#content").slideUp();
			$("#closeNotice").slideDown();
			window.clearInterval(updateAllInterval);
		});
    });
});