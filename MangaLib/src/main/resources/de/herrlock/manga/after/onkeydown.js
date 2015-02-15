document.onkeydown = function(e) {
	switch (e.keyCode) {
		case 37: 
				choose(active - 1);
				break;
		case 39: 
				choose(active + 1);
				break;
	}
};