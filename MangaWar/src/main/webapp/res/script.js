var md = {
	downloads : [],
	worker : new Worker("res/worker.js"),
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
		$.getJSON("j/download/progress", md.updateProgressBars);
	},
	updateProgressBars : function(response) {
		console.groupCollapsed("updateProgressBars - " + new Date().toLocaleTimeString());
		console.dir(response);
		md.downloads = response.filter(x => x.progress < x.maxProgress);
		md.updateAllProgressBars();
		md.checkAllProgressBars();
		console.groupEnd();
	},
	updateAllProgressBars : function() {
		console.groupCollapsed("updateAllProgressBars");
		md.downloads.forEach(md.updateProgressBar);
		console.groupEnd();
	},
	updateProgressBar : function(obj) {
		console.info("updateProgressBar", obj);
		var $fs = $("#pb_" + obj.uuid);
		if($fs.length === 0) {
			md.createEntry(obj);
		} else {
			$fs.find(">legend").text(obj.url);
			var $progress = $fs.find(">progress");
			$progress.attr({
				value : obj.progress,
				max : obj.maxProgress,
				title : obj.progress + " / " + obj.maxProgress
			});
			$fs.find(">img").hide(() => $progress.show());
			
		}
	},
	createTempEntry : function(uuid) {
		console.info("createTempEntry", uuid);
		md.createEntry({
			uuid : uuid,
			url : $("#url").val(),
			progress : 0,
			maxProgress : 100,
			temp : true
		});
		md.checkPBTitle();
	},
	createEntry : function(obj) {
		console.info("createEntry", obj);
		var $legend = $("<legend>" + md.encodeHTML(obj.url) + "</legend>");
		var $loading = $("<img src='res/loading.gif' />");
		var $progress = $("<progress value='" + obj.progress + "' max='" + obj.maxProgress + "'/>");
		if(obj.temp === true) {
			$progress.hide();
		} else {
			$loading.hide();
		}
		var $fieldset = $("<fieldset id='pb_" + obj.uuid + "' style='display: none'></fieldset>");
		$fieldset.append([$legend, $loading, $progress]);
		$fieldset.appendTo($("#bars"));
		$("#pb_" + obj.uuid).slideDown();
	},
	checkAllProgressBars : function() {
		console.groupCollapsed("checkAllProgressBars");
		var list = [""].concat($("#bars>fieldset").toArray().map(md.checkProgressBar));
		$.when(...list).then(md.checkPBTitle);
		console.groupEnd();
	},
	checkProgressBar : function(fieldset) {
		console.info("checkProgressBar", fieldset);
		console.log("id: ", fieldset.id);
		var pb = md.downloads.find(obj => "pb_" + obj.uuid === fieldset.id);
		console.log("pb", pb);
		if(!!pb) {
			return "";
		} else {
			var deferred = $.Deferred();
			$(fieldset).slideUp(function() {
				var url = fieldset.querySelector("legend").textContent;
				$(fieldset).remove();
				console.log("removed orphaned bar");
				md.showNotification("MangaDownloader", {
					body: "Finished " + url
				});
				deferred.resolve();
			});
			return deferred;
		}
	},
	showNotification : function(title, options) {
		if (Notification.permission === "granted") {
			// show notification
			md.worker.postMessage([title, options]);
		} else if (Notification.permission !== 'denied') {
			Notification.requestPermission(function (permission) {
				if (permission === "granted") {
					md.showNotification(title, options);
				}
			});
		}
	},
	checkPBTitle : function() {
		console.group("checkPBTitle");
		var hasBars = $("#bars>fieldset").length > 0;
		console.log("hasBars: ", hasBars);
		var $bars = $("#bars");
		if(hasBars) {
			$bars.show();
		} else {
			$bars.hide();
		}
		console.groupEnd();
	},
	encodeHTML : function(string) {
		var tagsToReplace = {
			'&': '&amp;',
			'<': '&lt;',
			'>': '&gt;'
		};
		return (string || "").replace(/[&<>]/g, tag => tagsToReplace[tag] || tag);
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
			$("#bars").slideUp();
			$("#content").slideUp();
			$("#closeNotice").slideDown();
			window.clearInterval(updateAllInterval);
			console.warn("stopped server");
		});
	});
	md.updateAll();
});