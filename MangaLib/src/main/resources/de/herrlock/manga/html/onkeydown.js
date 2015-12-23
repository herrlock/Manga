(function() {
    var inAnimation = false;

    var scrollOn = function(d) {
        if (!inAnimation) {
            inAnimation = true;
            $('html,body').animate({
                scrollTop : document.documentElement.scrollTop + d
            }, 100, function() {
                inAnimation = false;
            });
        }
    };

    document.onkeydown = function(e) {
        var active = parseFloat(location.hash.substring(1) || 1);
        switch (e.keyCode) {
        case 37: // arrow left
        case 65: // 'a'
            MangaUtils.choose(active - 1);
            break;
        case 39: // arrow right
        case 68: // 'd'
            MangaUtils.choose(active + 1);
            break;
        case 87: // 'w'
            scrollOn(-50);
            break;
        case 83: // 's'
            scrollOn(50);
            break;
        default:
            break;
        }
    };
}());