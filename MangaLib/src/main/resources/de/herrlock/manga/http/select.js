$(function(){
	$(".eingabe").hide();
	$("#eingabe1").show();
	console.log($("#selector"));
	$("#selector").change(function(){
		console.log("funtion start");
		var auswahl = $(this).find("option:selected").index();
		console.log(auswahl);
		if(auswahl == 0) {
			$(".eingabe").hide();
			$("#eingabe1").show();
		}else if(auswahl == 1) {
			$(".eingabe").hide();
			$("#eingabe2").show();
		}else if(auswahl == 2) {
			$(".eingabe").hide();
			$("#eingabe3").show();
		}
	});
});