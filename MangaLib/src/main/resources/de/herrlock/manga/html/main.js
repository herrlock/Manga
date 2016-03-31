var MangaUtils = {

    /**
     * loads the page with the latest chapter
     */
    init : function() {
        console.group("init");
        // show current chapter
        MangaUtils.choose(parseFloat(location.hash.substring(1) || "1") || 1);
        // show the most top block
        MangaUtils.show(manga.chapterblock);
        console.groupEnd();
    },

    /**
     * shows pages from chapter x
     */
    choose : function(x) {
        console.group("choose", x);
        var toLoad = parseFloat(x);
        // scroll to the top
        $("html,body").animate({
            scrollTop : $("body").offset().top
        }, 2000);
        // hide images
        var $rightdiv = $("#rightdiv");
        $rightdiv.fadeOut(100);
        // change hashvalue
        location.hash = toLoad;
        // change pagetitle
        $("title").html("Kapitel " + toLoad);
        $("#pagetitle").html("Kapitel " + toLoad + ":");
        // check and alter images
        for (var i = 1; i <= manga.max_pages; i++) {
            // build the path the the image
            var url = toLoad + "/" + (i < 10 ? "0" + i : i) + ".jpg";
            // check if image exists
            MangaUtils.testImage(url, i);
            // change image's attributes src and alt
            $("#page" + i).attr({
                "src" : url,
                "alt" : "'" + url + "'"
            });
        }
        // show images
        $rightdiv.fadeIn(1900);
        // alter link to the next chapter
        var hasNextChapter = toLoad < manga.chapter;
        $("#endlink").css({
            textDecoration : hasNextChapter ? "underline" : "none",
            color : hasNextChapter ? "red" : "black"
        });

        var chp = toLoad + 1;
        $("a#chooseNext").attr({
            onclick : "MangaUtils.choose(" + chp + ")",
            title : "Lade Kapitel " + chp
        }).text("Kapitel " + chp);

        // check (and optonally alter) the imagesizes after one second
        setTimeout(MangaUtils.adjustIMGWidth, 1000);
        console.groupEnd();
    },

    /**
     * show block number x in the left menu, hide all the others
     */
    show : function(x) {
        console.group("show", x);
        for (var i = manga.chapterblock - 1; i >= 0; i--) {
            console.log("hide block", i);
            $("#block" + i).slideUp();
            $("#arrow" + i).html("&#x25ba;"); // arrow right
        }
        console.log("show block", x);
        $("#block" + x).slideDown();
        $("#arrow" + x).html("&#x25bc;"); // arrow down
        console.groupEnd();
    },

    /**
     * sets the width of all wide imgs to 100%
     */
    adjustIMGWidth : function() {
        console.group("adjustIMGWidth");
        $("img").each(function() {
            var newClass = this.width >= this.height ? "fullwidth" : "normalwidth";
            console.log(this, newClass);
            $(this).attr("class", newClass);
        });
        console.groupEnd();
    },

    /**
     * hides those blocks ('id = "IMGBlock ' + i + '"'), without an actual image.
     */
    testImage : function(url, i) {
        console.log("testImage", url, i);
        $(new Image()).load(function() {
            $("#IMGBlock" + i).show(500);
        }).error(function() {
            $("#IMGBlock" + i).hide(100);
        }).attr("src", "" + url);
    }
};

jQuery(function($) {
    MangaUtils.init();
});
