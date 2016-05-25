var MangaUtils = {

	getHash : function() {
        var split = location.hash.substring(1).split("/");
        var obj = {
            chapter: undefined,
            image: undefined
        };
        if(!!split[1]) {
            obj.chapter = parseFloat(split[1]) || undefined;
            if(!!obj.chapter && !!split[2]) {
                obj.image = parseFloat(split[2]) || undefined;
            }
        }
        return obj;
	},
	
	setHash : function(chapter, image) {
        var split = [""];
        if(!!chapter) {
            split.push(chapter);
            if(!!image) {
                split.push(image);
            }
        }
        location.hash = split.join("/");
	},

    /**
     * loads the page with the latest chapter
     */
    init : function() {
        console.group("init");
        //console.groupCollapsed("init");
        // show current chapter
        MangaUtils.choose((MangaUtils.getHash().chapter || "1") || 1);
        // show the most top block
        MangaUtils.show(manga.chapterblock);
        console.groupEnd();
    },

    /**
     * shows pages from chapter x
     */
    choose : function(x) {
        console.groupCollapsed("choose", x);
        var deferreds = [];
        var toLoad = parseFloat(x);
        // scroll to the top
        $("html,body").animate({
            scrollTop : $("body").offset().top
        }, 2000);
        // hide images
        var $rightdiv = $("#rightdiv");
        $rightdiv.fadeOut(100);
        // change hashvalue
        MangaUtils.setHash(toLoad);
        // change pagetitle
        $("title").html("Kapitel " + toLoad);
        $("#pagetitle").html("Kapitel " + toLoad + ":");
        // check and alter images
        for (var i = 1; i <= manga.max_pages; i++) {
            var url = toLoad + "/" + (i < 10 ? "0" + i : i) + ".jpg";
            deferreds.push(MangaUtils.setupImage(url, i));
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

        console.groupEnd();
        
        // check (and optionally alter) the imagesizes
        $.when(...deferreds).then(MangaUtils.adjustIMGWidth);
    },
    
    setupImage : function(url, i) {
        console.log("setupImage", url, i);
        // check if image exists
        var deferred = MangaUtils.testImage(url, i);
        // change image's attributes src and alt
        $("#page" + i).attr({
            "src" : url,
            "alt" : "'" + url + "'"
        });
        return deferred;
    },

    /**
     * show block number x in the left menu, hide all the others
     */
    show : function(x) {
        console.groupCollapsed("show", x);
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
        console.groupCollapsed("adjustIMGWidth");
        $("img").each(function() {
            var newClass = this.width >= this.height ? "fullwidth" : "normalwidth";
            console[this.width > 0 ? "info" : "log"](this, newClass, this.width, this.height);
            $(this).attr("class", newClass);
        });
        console.groupEnd();
    },

    /**
     * hides those blocks ('id = "IMGBlock ' + i + '"'), without an actual image.
     */
    testImage : function(url, i) {
        console.log("testImage", url, i);
        var def = $.Deferred();
        $(new Image()).load(function() {
            $("#IMGBlock" + i).show(500, () => def.resolve());
        }).error(function() {
            $("#IMGBlock" + i).hide(100, () => def.resolve());
        }).attr("src", "" + url);
        return def;
    }
};

jQuery(function() {
    MangaUtils.init();
});

jQuery(function() {
    var scroll = {
        direction : 1,
        delay : undefined,
        dirbase : 5,
        up : () => scroll.direction = -scroll.dirbase,
        down : () => scroll.direction = scroll.dirbase
    };
    var pageScroll = function(force) {
        if(scroll.delay === undefined || force === true) {
            window.scrollBy(0, scroll.direction);
            scroll.delay = setTimeout(() => pageScroll(true), 5);   
        }
	};
    
    var nav = {
        getCurrentChapter : function() {
            return parseFloat(MangaUtils.getHash().chapter || 1);
        },
        left : function() {
            var currentChapter = nav.getCurrentChapter();
            MangaUtils.choose(currentChapter - 1);
        },
        right : function() {
            var currentChapter = nav.getCurrentChapter();
            MangaUtils.choose(currentChapter + 1);
        },
        up : function(amount) {
        	scroll.up();
            pageScroll();
        },
        down : function(amount) {
        	scroll.down();
            pageScroll();
        }
    };
    var keys = {
        37: nav.left, // arrow left
        38: nav.up, // arrow up
        39: nav.right, // arrow right
        40: nav.down, // arrow down
        65: nav.left, // a
        68: nav.right, // d
        83: nav.down, // s
        87: nav.up, // w
    };
    
	var $doc = $(document);
    $doc.keydown(function(e) {
        var action = keys[e.keyCode];
        if(!!action) {
            action();
        }
    });
    $doc.keyup(function(e) {
    	clearTimeout(scroll.delay);
        scroll.delay = undefined;
    });
});
