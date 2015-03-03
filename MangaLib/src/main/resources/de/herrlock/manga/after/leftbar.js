(function() {
    // builds the left bar
    document.write('<pre>');
    document.write('<span style="display: none;" id="arrow' + chapterblock + '"> hereComesAnArrow! <\/span>');
    document.write('<div id="block' + chapterblock + '">');
    var tmpChp;
    for (var i = chapter % step; i > 0; i--) {
        tmpChp = chapter - (chapter % step) + i;
        document.writeln(whitelinkString('', tmpChp, ''));
    }
    document.write('<\/div>');
    for (var i = chapterblock - 1; i >= 0; i--) {
        document.writeln(hidelinkString(i));
        document.write('<div id="block' + i + '">');
        for (var j = step; j > 0; j--) {
            tmpChp = (step * i) + j;
            document.writeln(whitelinkString('', tmpChp, ''));
        }
        document.write('<\/div>');
    }
    document.write('<\/pre>');
})();