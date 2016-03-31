var properties = (function() {
	var data = {
		step1 : {
			de : "URL des Mangas holen (z.b. http://mangafox.me/manga/log_horizon/)",
			en : "get the url of the manga (eg. http://mangafox.me/manga/log_horizon/)"
		},
		step2 : {
			de : "val2_de",
			en : "val2_en"
		},
		step3 : {
			en : "val3_en"
		}
	};
	console.dir([data]);
	var locale = (location.search.substring(1).split("&").find(s => s.match(/^lang=..$/)) || "").substring(5) || navigator.language.substring(0, 2);
	console.warn("locale", locale);
	var result = {};
	Object.keys(data).forEach(function(value) {
		var obj = data[value];
		result[value] = (obj[locale] || obj.en);
	});
	console.dir(result);
	return result;
}());
