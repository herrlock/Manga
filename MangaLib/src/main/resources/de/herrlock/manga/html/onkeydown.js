var inAnimation = false;
document.onkeydown = function(e) {
    var active = parseFloat(location.hash.substring(1) || 1);
    switch (e.keyCode) {
    case 37: // arrow left
    case 65: // 'a'
        choose(active - 1);
        break;
    case 39: // arrow right
    case 68: // 'd'
        choose(active + 1);
        break;

    case 87: // 'w'
    case 83: // 's'
        if (!inAnimation) {
            inAnimation = true;
            $('html,body').animate({
                scrollTop : document.documentElement.scrollTop + ((e.keyCode == 87) ? -50 : 50)
            }, 100, function() {
                inAnimation = false;
            });
        }
        break;
    }
};