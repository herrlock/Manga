onmessage = function(e) {
	console.log("Message received from main script");
	new Notification(e.data[0], e.data[1])
	console.log("Posting message back to main script");
	postMessage({success: true});
}