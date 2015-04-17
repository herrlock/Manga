/**
 * loads the page with the latest chapter
 */
function init() {
    // das aktuelle kapitel anzeigen
    choose(parseFloat(location.hash.substring(1) || "1") || 1);
    // den obersten sidebarblock (die freien kapitel) öffnen
    show(chapterblock);
}

/**
 * shows pages from chapter x
 */
function choose(x) {
    var toLoad = parseFloat(x);
    // nach oben scollen
    $('html,body').animate({
        scrollTop : $('body').offset().top
    }, 2000);
    // bilder ausblenden
    var $rightdiv = $('#rightdiv');
    $rightdiv.fadeOut(100);
    // hashangabe verändern
    location.hash = toLoad;
    // title ändern
    $('title').html('Kapitel ' + toLoad);
    // seitenüberschrift ändern (z.B. "Kapitel 623:"
    $('#pagetitle').html('Kapitel ' + toLoad + ':');
    for (var i = 1; i <= max_pages; i++) {
        // pfad zur bilddatei
        var url = toLoad + '\/' + (i < 10 ? "0" + i : i) + '.jpg';
        // bild auf existenz prüfen
        testImage(url, i);
        // bildpfad und alternative bildbeschreibung setzen
        $('#page' + i).attr({
            'src' : url,
            'alt' : '\'' + url + '\''
        });
    }
    // bilder wieder einblenden
    $rightdiv.fadeIn(1900);
    // der link, der zum nächsten kapitel führt
    var $endlink = $('#endlink');
    var css = {
        textDecoration : "",
        color : ""
    };
    if (toLoad < chapter) {
        // der link auf das nächste kapitel wird geändert
        css.textDecoration = 'underline';
        css.color = 'red';
    } else {
        // es wird auf ein kapitel gewechselt, das noch keinen nachfolger hat
        css.textDecoration = 'none';
        css.color = 'black';
    }
    $endlink.css(css);

    var chp = toLoad + 1;
    $endlink.replaceWith($('<a/>', {
        'class' : 'whitelink',
        id : 'endlink',
        href : 'javascript:void(0)',
        onclick : 'choose(' + chp + ')',
        title : 'Lade Kapitel ' + chp,
        text : 'Kapitel ' + chp
    }));

    // nach einer sekunde (1000 ms) werden die bildgrößen überprüft und ggf. angepasst
    setTimeout(adjustIMGWidth, 1000);
}

/**
 * show block number x in the left menu, hide all the others
 */
function show(x) {
    for (var i = chapterblock - 1; i >= 0; i--) {
        $('#block' + i).slideUp();
        $('#arrow' + i).html('&#x25ba;');
    }
    $('#block' + x).slideDown();
    $('#arrow' + x).html('&#x25bc;');
}

/**
 * sets the width of all wide imgs to 100%
 */
function adjustIMGWidth() {
    $('img').each(function() {
        var newClass = this.width >= this.height ? 'fullwidth' : 'normalwidth';
        $(this).attr('class', newClass);
    });
}

/**
 * versteckt die bildblöcke ('id = "IMGBlock ' + i + '"'), deren bilder nicht vorhanden sind. muss für jedes bild einzeln aufgerufen werden!
 * 
 * @param url
 *            die url des zu testenden bildes
 * @param i
 *            die id-nummer des bildblocks
 */
function testImage(url, i) {
    $(new Image()).load(function() {
        $('#IMGBlock' + i).show(500);
    }).error(function() {
        $('#IMGBlock' + i).hide(100);
    }).attr('src', '' + url);
}