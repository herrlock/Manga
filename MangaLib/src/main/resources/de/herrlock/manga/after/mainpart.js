(function() {
    // builds the raw version of the page's main part
    document.write('<div id="bandindex"> <a href="bandindex.txt"> bandindex <\/a> <\/div>');
    document.write('<h1 id="pagetitle">If you can read this something BAD happened...<\/h1>');
    for (var i = 1; i <= max_pages; i++) {
        document.write('<div id="IMGBlock' + i + '" class="IMGBlock">')
        document.write('<h2> ' + (i < 10 ? "0" + i : i) + ' <\/h2>');
        document.write('<div align="center"> <img id="page' + i + '" class="image" src="null.jpg" alt="null' + i + '.jpg" \/> <\/div>');
        document.write('<hr\/><\/div>');
    }
    document.write('<h1 id="endlink">Link to the next chapter<\/h1>');
})()