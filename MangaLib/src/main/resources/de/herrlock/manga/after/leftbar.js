
// builds the left bar

document.write('<pre>');
document.write('<span style="display: none;" id="arrow' + chapterblock + '"> hereComesAnArrow! <\/span>');
document.write('<div id="block' + chapterblock + '">');
for(i = chapter % step; i > 0; i--) {
	tmpChp = chapter - (chapter % step) + i;
	document.writeln(whitelinkString('', tmpChp, ''));
}
document.write('<\/div>');
for(i = chapterblock - 1; i >= 0; i--) {
	document.writeln(hidelinkString(i));
	document.write('<div id="block' + i + '">');
	for(j = step; j > 0; j--) {
		tmpChp = (step * i) + j;
		document.writeln(whitelinkString('', tmpChp, ''));
	}
	document.write('<\/div>');
}
document.write('<\/pre>');