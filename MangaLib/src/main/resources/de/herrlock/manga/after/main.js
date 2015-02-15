
/** the number of the newest chapter */
var chapter = 	744;
/** the maximal amount of pages that a chapter may have */
var max_pages = 60;
/** the amount of chapters per block on the left sidebar */
var step = 10;
/** the amount of blocks on the left sidebar */
var chapterblock = ( chapter - (chapter % step) ) / step;
/** the current active chapter */
var active;

/**
 * loads the page with the latest chapter
 */
function init() {
	// das aktuelle kapitel anzeigen
	var post = parseInt(location.hash.substring(1));
	if(post === NaN) {
		choose( chapter );
	} else {
		choose( post );
	}
	// den passenden sidebarblock öffnen
	show(chapterblock);
}
/**
 * shows pages from chapter x
 */
function choose(x) {
    active = x = parseInt(x);
    // nach oben scollen
    $('html,body').animate({ scrollTop: $('body').offset().top }, 2000);
	// bilder ausblenden
    $('#rightdiv').fadeOut(100);
    // hashangabe verändern
	location.hash = x;
	// title ändern
	$('title').html('Kapitel ' + x);
	// seitenüberschrift ändern (z.B. "Kapitel 623:"
	$('#pagetitle').html('Kapitel ' + x + ':');
	for (i = 1; i <= max_pages; i++) {
		// pfad zur standardisierten bilddatei
		var url = x + '\/' + (i < 10 ? "0"+i : i) + '.jpg';
		// bild auf existenz prüfen
		testImage(url, i);
		// bildpfad und alternative bildbeschreibung setzen
		$('#page' + i).attr({'src': url, 'alt': '\'' + url + '\''});
	}
    // bilder wieder einblenden
    $('#rightdiv').fadeIn(1900);
	// der link, der zum nächsten kapitel führt
	var el = $('#endlink');
	if(x >= chapter) {
		// es wird auf ein kapitel gewechselt, das noch keinen nachfolger hat
		el.css({'textDecoration': 'none', 'color': 'black'});
		el.html(whitelinkString('Kapitel ', (x + 1), ''));
	} else {
		// der link auf das nächst kapitel wird geändert
		el.css({'textDecoration': 'underline', 'color': 'red'});
		el.html(whitelinkString('Kapitel ', (x + 1), ''));
	}
	// nach einer sekunde (1000 ms) werden die bildgrößen überprüft und ggf. angepasst
	window.setTimeout('adjustIMGWidth()', 1000);
}
/**
 * show block number x in the left menu, hide all the others
 */
function show(x) {
	
	for(i = chapterblock - 1; i >= 0; i--) {
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
	for( i = 0; i < $('img').length; i++ ) {
		var newClass = 
			$('#page' + i).prop('width') >= $('#page' + i).prop('height') ? 
			'fullwidth' : 'normalwidth';
		$('#page' + i).attr('class', newClass);
	}
}

/**
 * returns the string for the "whitelink"-classed link to avoid redundancy
 */
function whitelinkString(pretext, chp, posttext) {
	return '<a class="whitelink" ' + 
				'id="choose' + chp + '" ' + 
				'href="javascript:void(0)"' +
				'onclick="choose(' + chp + ')" ' + 
				'title="Lade Kapitel ' + chp + '">' + 
				pretext + chp + posttext + '<\/a>';
}

/**
 * returns the string for the "hidelink"-classed link to avoid redundancy
 */
function hidelinkString(blockId) {
	var blockName = (step * (blockId + 1));
	return '<a class="hidelink" ' + 
				'id="hidelink' + blockId + '" ' + 
				'href="javascript:void(0)"' +
				'onclick="show(' + blockId + ')" ' + 
				'title="Zeige Kapitel ' + (blockName - step + 1) + ' bis ' + blockName + '">' + 
			'<span id="arrow' + blockId + '"> hereComesAnArrow! <\/span> ' + blockName + '<\/a>';
}

/**
 * versteckt die bildblöcke ('id = "IMGBlock ' + i + '"'), deren bilder nicht vorhanden sind.
 * muss für jedes bild einzeln aufgerufen werden!
 * @param url die url des zu testenden bildes
 * @param i die id-nummer des bildblocks
 */
function testImage(url, i) {
	var img = new Image();
	img.src = url;
	
	// bild existiert nicht oder wurde falsch geladen
	img.onerror = img.onabort = function() {
		$('#IMGBlock' + i).hide(100);
	};
	// bild existiert
	img.onload = function() {
		$('#IMGBlock' + i).show(500);
	};
}