var md = {
	downloads : [],
	cnt : 0,
	worker : new Worker("res/worker.js"),
	showOnly : function(c, duration) {
		console.info("showOnly", c, duration);
		if (c === "dl" || c == "jd") {
			$("form#eingabe div").slideUp(duration, function() {
				$("form#eingabe div." + c).slideDown(duration);
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
	createTempEntry : function() {
		console.info("createTempEntry");
		var temp = ++md.cnt;
		md.createEntry({
			uuid : temp,
			url : $("#url").val(),
			progress : 0,
			maxProgress : 100,
			temp : true
		});
		md.checkPBTitle();
		return temp;
	},
	createEntry : function(obj) {
		console.info("createEntry", obj);
		var $legend = $("<legend>" + md.encodeHTML(obj.url) + "</legend>");
		var $loading = $("<img src='res/loading.gif' />");
		var $progress = $("<progress value='" + obj.progress + "' max='" + obj.maxProgress + "'/>");
		var $fieldset = $("<fieldset style='display: none;'></fieldset>");
		if(obj.temp === true) {
			$progress.hide();
			$fieldset.attr("data-temp", obj.uuid);
		} else {
			$loading.hide();
			$fieldset.attr("id", "pb_" + obj.uuid);
		}
		$fieldset.append([$legend, $loading, $progress]);
		$fieldset.appendTo($("#bars"));
		$fieldset.slideDown();
	},
	updateEntry(uuid, tempId) {
		console.info("updateEntry", uuid, tempId);
		var fieldset = $("fieldset[data-temp=" + tempId + "]");
		if(fieldset.length > 0) {
			fieldset.attr({
				"data-temp": "",
				"id" : "pb_" + uuid
			});
		}
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
		var progressBar = md.downloads.find(obj => "pb_" + obj.uuid === fieldset.id);
		console.log("progressBar", progressBar);
		var tempData = fieldset.dataset.temp;
		console.log("tempData", tempData);
		if(!!progressBar || !!tempData) {
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
		var temp = md.createTempEntry();
		$.get(url, response => md.updateEntry(response, temp));
	});
	$("#stopServer").click(function() {
		$.get("server/stop", function() {
			$("#bars").slideUp();
			$("#content").slideUp();
			$("#closeNotice").slideDown();
			window.clearInterval(updateAllInterval);
			console.warn("+","----------------","+");
			console.warn("|"," stopped server ","|");
			console.warn("+","----------------","+");
		});
	});
	md.updateAll();
});