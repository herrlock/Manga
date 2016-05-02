var md = {
	downloads : [],
    showOnly : function(c, duration) {
    	console.info("showOnly", c, duration);
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
    	console.info("showSelected", duration);
        md.showOnly($("#selector > option:selected")[0].value, duration);
    },
    updateAll : function() {
        console.info("updateAll", new Date().toLocaleTimeString());
    	$.getJSON("j/download/progress", md.updateProgressBars);
    },
    updateProgressBars : function(response) {
    	console.group("updateProgressBars");
    	console.info("response", response);
        md.downloads = response.filter(x => x.progress < x.maxProgress);
    	
    	console.groupCollapsed("updateProgressBar");
    	md.downloads.forEach(md.updateProgressBar);
    	console.groupEnd();
    	
    	console.groupCollapsed("checkProgressBar");
    	$("#bars>fieldset").toArray().forEach(md.checkProgressBar);
    	console.groupEnd();
    	
        md.checkPBTitle();
                
    	console.groupEnd();
	},
    updateProgressBar : function(obj) {
    	console.info("updateProgressBar", obj);
		var $fs = $("#pb_" + obj.uuid);
		if($fs.length === 0) {
			md.createEntry(obj);
		} else {
			var $pb = $fs.find(">progress")[0];
			$pb.value = obj.progress;
			$pb.max = obj.maxProgress;
			$pb.title = obj.progress + " / " + obj.maxProgress;
		}
    },
    createTempEntry : function(uuid) {
    	console.info("createTempEntry", uuid);
    	md.createEntry({
    		uuid : uuid,
    		url : $("#url").val(),
    		progress : 0,
    		maxProgress : 100
    	});
    },
    createEntry : function(obj) {
    	console.info("createEntry", obj);
    	var legend = "<legend>" + md.encodeHTML(obj.url) + "</legend>";
    	var progress = "<progress value='" + obj.progress + "' max='" + obj.maxProgress + "'/>";
    	$("#bars").append("<fieldset id='pb_" + obj.uuid + "' style='display: none'>" + legend + progress + "</fieldset>");
    	$("#pb_" + obj.uuid).slideDown();
    },
    checkProgressBar : function(fieldset) {
    	console.info("checkProgressBar", fieldset);
    	console.log("id: ", fieldset.id);
    	var hasPB = md.downloads.some(obj => "pb_" + obj.uuid === fieldset.id);
    	console.log("hasPB", hasPB);
    	if(!hasPB) {
	    	$(fieldset).slideUp(function() {
	    		$(fieldset).remove();
    			console.log("removed orphaned bar");
                md.checkPBTitle();
	    	});
    	}
    },
    checkPBTitle : function() {
    	console.info("checkPBTitle");
    	var hasBars = $("#bars>fieldset").length > 0;
    	console.log("hasBars", hasBars);
    	if(hasBars) {
    		$("#bars").show();
		} else {
			$("#bars").hide();
		}
    },
    encodeHTML : function(string) {
	    var tagsToReplace = {
	        '&': '&amp;',
	        '<': '&lt;',
	        '>': '&gt;'
	    };
	    return (string || "").replace(/[&<>]/g, function(tag) {
	        return tagsToReplace[tag] || tag;
	    });
	}
};

$(function() {
    md.showSelected(0);
    var updateAllInterval = window.setInterval(md.updateAll, 5000);
    console.warn("uai", updateAllInterval);
    $("#selector").change(md.showSelected);
    $("#submit").click(function() {
        var queryArr = $("#eingabe > div > input")
		    .filter((i,elem) => $(elem).is(":visible"))
            .map((i,elem) => encodeURIComponent(elem.id) + "=" + encodeURIComponent(elem.value))
            .toArray();
        var query = "?" + queryArr.join("&");
        var url = "j/download/start" + query;
        $.get(url, md.createTempEntry);
    });
    $("#stopServer").click(function() {
        $.get("server/stop", function() {
			$("#content").slideUp();
			$("#closeNotice").slideDown();
			window.clearInterval(updateAllInterval);
		});
    });
    md.updateAll();
});