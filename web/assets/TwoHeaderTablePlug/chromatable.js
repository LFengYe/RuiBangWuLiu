(function ($) {
    $.chromatable = {
        defaults: {
            width: "900px",
            height: "300px",
            scrolling: "yes",
            headerClickCallback: function (name) {}
        }
    };

    $.fn.chromatable = function (options) {
        var options = $.extend({}, $.chromatable.defaults, options);
        var that = this;
        return this.each(function () {
            var $this = $(this);

            //console.log(this.parentNode.className);
            $(this).css('width', options.width).addClass("_scrolling");

            if (this.parentNode.className == "scrolling_inner") {
                console.log("has parent");
                return ;
            } else {
                console.log("new");
                $(this).wrap('<div class="scrolling_outer"><div class="scrolling_inner"></div></div>');
            }
            $(this).parents(".scrolling_outer").css({
                'position': 'relative',
                'overflow-x': 'auto'
            });
            $(this).parents(".scrolling_inner").css({
                'border': '1px solid #CCCCCC',
                'overflow-x': 'hidden',
                'overflow-y': 'auto',
                'padding-right': '17px'
            });

            $(this).parents(".scrolling_inner").css('height', options.height);
            $(this).parents(".scrolling_inner").css('width', options.width);
            if (this.previousSibling && this.previousSibling.nodeType == 1) {
                //console.log(this.previousSibling.nodeType);
            } else {
                //console.log(this.previousSibling);
                $(this).before($(this).clone().attr("id", "").addClass("_thead").css({
                    'width': options.width,
                    'display': 'block',
                    'position': 'absolute',
                    'border': 'none',
                    'border-bottom': '1px solid #CCC',
                    'top': '1px'
                }));
                $('._thead').children('tbody').remove();
                $('._thead').children('thead').find("tr").children("th").click(function (e) {
                    if (options.headerClickCallback) {
                        options.headerClickCallback($(this).attr("name"));
                    }
                });
            }
            $(this).each(function ($this) {
                if (options.width == "100%" || options.width == "auto") {
                    $(this).parents(".scrolling_inner").css({'padding-right': '0px'});
                }
                if (options.scrolling == "no") {
                    $(this).parents(".scrolling_inner").before('<a href="#" class="expander" style="width:100%;">Expand table</a>');
                    $(this).parents(".scrolling_inner").css({'padding-right': '0px'});
                    $(".expander").each(
                            function (int) {
                                $(this).attr("ID", int);
                                $(this).bind("click", function () {
                                    $(this).parents(".scrolling_inner").css({'height': 'auto'});
                                    $(this).parents(".scrolling_inner").find("._thead").remove();
                                    $(this).remove();
                                });
                            });
                    $(this).parents(".scrolling_inner").resizable({handles: 's'}).css("overflow-y", "hidden");
                }
            });
            $curr = $this.prev();
            $("thead>tr th", this).each(function (i) {
                $("thead>tr th:eq(" + i + ")", $curr).width($(this).width());
                $("thead>tr th:eq(" + i + ")", $this).width($(this).width());
            });
            if (options.width == "100%" || "auto") {
                $(window).resize(function () {
                    resizer($this);
                });
            }
        });
    };

    $.fn.clearChromaTable = function () {
        if ($(this).parents(".scrolling_outer")) {
            $(this).parents(".wrapper").append($(this).clone());
            $(this).parents(".scrolling_outer").remove();
        }
    };

    function resizer($this) {
        $curr = $this.prev();
        $("thead>tr th", $this).each(function (i) {
            $("thead>tr th:eq(" + i + ")", $curr).width($(this).width());
            $("thead>tr th:eq(" + i + ")", $this).width($(this).width());
        });
    }
    ;
})(jQuery);