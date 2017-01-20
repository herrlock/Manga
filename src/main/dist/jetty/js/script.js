var md = {
	rb : {},
	rben : {},
	downloads : [],
	cnt : 0,
	showOnly : function(c, duration) {
		console.info("showOnly", c, duration);
		if (c === "dl") {
			$("#eingabe > div.valueholder").slideUp(duration, () => $("#eingabe > div.valueholder." + c).slideDown(duration));
		}
	},
	showSelected : function(duration) {
		console.info("showSelected", duration);
		md.showOnly($("#type > option:selected")[0].value, duration);
	},
	updateAll : function() {
		console.info("updateAll");
		$.getJSON("j/download/progress", md.updateProgressBars);
	},
	updateProgressBars : function(response) {
		console.groupCollapsed(">> updateProgressBars - " + new Date().toLocaleTimeString());
		console.log(response);
		md.downloads = response.filter(x => x.progress < x.maxProgress);
		md.updateAllProgressBars();
		md.checkAllProgressBars();
		console.debug(">> updateProgressBars - " + new Date().toLocaleTimeString());
		console.groupEnd();
	},
	updateAllProgressBars : function() {
		console.groupCollapsed("updateAllProgressBars");
		md.downloads.forEach(md.updateProgressBar);
		console.groupEnd();
	},
	updateProgressBar : function(obj) {
		console.log("--");
		console.group("updateProgressBar");
		console.log(obj.toSource());
		var $fs = $("#progress_" + obj.uuid);
		console.log("$fs", $fs);
		if($fs.length === 0) {
			md.createEntry(obj);
		} else {
			$fs.find("> h5").text(obj.url);
			var pct = md.round(100 * obj.progress / obj.maxProgress, 2);
			var $progressBar = $fs.find("div.progress-bar")
				.css("width", pct + "%")
				.text(pct + "%")
				.attr({
					"title" : obj.progress + " / " + obj.maxProgress,
					"data-current" : obj.progress,
					"data-max" : obj.maxProgress,
					"data-pct" : pct
				});
		}
		console.groupEnd();
		console.log("--");
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
		return temp;
	},
	createEntry : function(obj) {
		console.info("createEntry", obj);
		var $progressWrapper = $("<div class='progress-bar-wrapper' style='display: none;'></div>");
		var $url = $("<h5></h5>").attr("title", obj.url).text(obj.url);
		var $progress = $("<div class='progress'></div>");
		var pct = md.round(100 * obj.progress / obj.maxProgress, 2);
		console.log("pct", pct);
		var $progressBar = $("<div class='progress-bar progress-bar-striped active'></div>")
			.text(pct + "%")
			.css("width", pct + "%")
			.attr({
				"data-current" : obj.progress,
				"data-max" : obj.maxProgress,
				"data-pct" : pct
			});
		if(obj.temp === true) {
			$progressWrapper.attr("data-temp", obj.uuid);
		} else {
			$progressWrapper.attr("id", "progress_" + obj.uuid);
		}
		$progressWrapper.append($url, $progress.append($progressBar)).insertBefore($("#bars .dummy"));
		$progressWrapper.slideDown();
	},
	updateTempEntry : function(uuid, tempId) {
		console.info("updateTempEntry", uuid, tempId);
		var div = $("div[data-temp=" + tempId + "]");
		if(div.length > 0) {
			div.attr({
				"data-temp": "",
				"id" : "progress_" + uuid
			});
		}
	},
	removeEntry : function(jqXHR, tempId) {
		console.warn("removeEntry", jqXHR, tempId);
		var tempDiv = $("div[data-temp=" + tempId + "]");
		tempDiv.slideUp(1000, () => tempDiv.remove());
	},
	checkAllProgressBars : function() {
		console.groupCollapsed("checkAllProgressBars");
		$("#bars div.progress-bar-wrapper").toArray().forEach(md.checkProgressBar);
		console.groupEnd();
	},
	checkProgressBar : function(wrapper) {
		console.log("--");
		console.groupCollapsed("checkProgressBar");
		console.log("id: ", wrapper.id, wrapper);
		var progressBar = md.downloads.find(obj => "progress_" + obj.uuid === wrapper.id);
		console.log("progressBar", progressBar);
		var tempData = wrapper.dataset.temp;
		console.log("tempData", tempData);
		if(!progressBar && !tempData) {
			$(wrapper).slideUp(function() {
				var url = wrapper.querySelector("h5").textContent;
				$(wrapper).remove();
				console.log("removed orphaned bar", url);
				md.showNotification("MangaDownloader", {
					body: "Finished " + url
				});
			});
		}
		console.groupEnd();
		console.log("--");
	},
	showNotification : function(title, options) {
		if (Notification.permission === "granted") {
			// show notification if expressly granted
			new Notification(title, options);
		} else if (Notification.permission !== "denied") {
			// ask for permission if not expressly denied (to not annoy the user)
			Notification.requestPermission(function (permission) {
				if (permission === "granted") {
					md.showNotification(title, options);
				}
			});
		}
	},
	encodeHTML : function(string) {
		var tagsToReplace = {
			'&': '&amp;',
			'<': '&lt;',
			'>': '&gt;'
		};
		return (string || "").replace(/[&<>]/g, tag => tagsToReplace[tag] || tag);
	},
	/** taken from https://developer.mozilla.org/de/docs/Web/JavaScript/Reference/Global_Objects/Math/round#PHP_%C3%A4hnliche_Rundungsmethode */
	round : function(number, precision) {
	    var factor = Math.pow(10, precision);
	    var tempNumber = +number * factor;
	    var roundedTempNumber = Math.round(tempNumber);
	    return roundedTempNumber / factor;
	}
};

$(function() {
	var rb = $.Deferred(), rben = $.Deferred();
	$.getJSON("l10n/" + navigator.language.substring(0,2) + ".json")
		.done(response => md.rb = response)
		.fail(console.warn)
		.always(() => rb.resolve());
	$.getJSON("l10n/en.json")
		.done(response => md.rben = response)
		.fail(console.warn)
		.always(() => rben.resolve());
	$.when(rb, rben).done(function() {
		var getDotValue = (array, string) => string.split(".").reduce((e,i) => e[i], array);
		$("[data-i18n]").each(function(index, element) {
			var key = element.dataset.i18n;
			var text = getDotValue(md.rb, key);
			if(!!text) {
				$(element).text(text);
			} else {
				var enText =  getDotValue(md.rben, key);
				if(!!enText) {
					console.warn("No own text for key", key);
					$(element).text(enText);
				} else {
					console.error("No texts for key", key, element);
				}
			}
		});
	});
});

$(function() {
	// regularly update the progress-bars
	var updateAllInterval = window.setInterval(md.updateAll, 5000);
	console.warn("uai", updateAllInterval);
	const $type = $("#type"),
		$url = $("#url"),
		$submit = $("#submit"),
		$stopServer = $("#stopServer");
	// listener for the dropdownbox
	$type.change(() => md.showSelected());
	// en- or disable the download-button
	$url.change(e => $submit.prop("disabled", e.target.value === ""));
	// trigger listener with current value
	$url.change();
	// action for download-button
	$submit.click(function() {
		var queryArr = $("#eingabe > div.form-group > input")
			.filter((i,elem) => $(elem).is(":visible"))
			.map((i,elem) => encodeURIComponent(elem.dataset.key) + "=" + encodeURIComponent(elem.value))
			.toArray();
		var query = "?" + queryArr.join("&");
		var url = "j/download/start" + query;
		var temp = md.createTempEntry();
		$.get(url).done(response => md.updateTempEntry(response, temp)).fail(jqXHR => md.removeEntry(jqXHR, temp));
	});
	// move elements when server was stopped
	var stopServer = function() {
		$.when($("#bars").slideUp(), $("#content").fadeOut()).then(() => $("#closeNotice").show());
		window.clearInterval(updateAllInterval);
		console.warn("+","----------------","+");
		console.warn("|"," stopped server ","|");
		console.warn("+","----------------","+");
	}
	// action for stop-server-button
	$stopServer.click(() => $.post("/shutdown?token=avadakedavra", stopServer));
	// show the correct fields
	md.showSelected(0);
	// initially setting the progress-bars
	md.updateAll();
});
