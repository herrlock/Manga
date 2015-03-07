document.onkeydown = function(e) {
    var active = location.hash.substring(1) || 1;
    switch (e.keyCode) {
    case 37:
        choose(active - 1);
        break;
    case 39:
        choose(active + 1);
        break;
    }
};