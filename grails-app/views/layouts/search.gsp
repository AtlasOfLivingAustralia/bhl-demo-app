<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"> <!--<![endif]-->
    <head>
        <title>bhl-ft-index Demo &raquo; <g:layoutTitle/></title>
        <link rel="stylesheet" href="<g:createLinkTo dir='css' file='snazzy.css' />" />
        <script src="/js/libs/modernizr-1.6.min.js"></script>
        <g:layoutHead />
    </head>
    <body>

            <div id="content-wrap">

                <header>
                    <div class="column-wrap">
                        <img src="<g:createLinkTo dir='images' file='logo.png'/>" alt="logo"/>
                    </div>
                </header>

                <div id="main">
                    <div id="content">
                        <div class="column-wrap" id="intro">
                            <img width="313" height="470" alt="Rosellas" src="../images/hero_rosellas.png" id="hero">
                            <!--
                            <p>The Biodiversity Heritage Library&ndash;Australia is the digital literature component of the Atlas of Living Australia. BHL-Au also participates in the consortium of Biodiversity Heritage Libraries and affiliated literature digitisation projects around the world.</p>
                            -->
                            <p>BHL Full text index demonstration project
                                <br /><br />
                                To search for a taxa name, prefix the name with <code>taxon:</code>, which will result in synonyms being added to list of search terms.
                                <br /><br />
                                For example: <code>taxon:iridomyrmex purpureus</code>
                            </p>

                        </div>
                        <div id="searchbar-home">
                            <div class="column-wrap" id="searchbox-home">
                                <input type="text" class="field" id="tbSearchTerm" value="Full text search" name="ctl00$mainContentPlaceHolder$tbSearchTerm">
                                <input type="button" class="button" id="btnSearchSubmit" value="submit" name="ctl00$mainContentPlaceHolder$btnSearchSubmit" onclick="doSearch(0, 10)">
                                <!--[if lt IE 9 ]> <input name="ctl00$mainContentPlaceHolder$ctl00" type="text" class="hidden" /> <![endif]-->
                            </div>
                        </div>
                        <div id="status-box" class="column-wrap" style="display: none;">
                            <div id="search-status" class="column-wrap" >
                                <span style="vertical-align: middle; ">
                                    Searching, please wait...
                                <img src="../images/spinner_square.gif" alt="Searching" style="vertical-align: middle;"/>
                                </span>
                            </div>
                        </div>
                        <div id="results-home" class="column-wrap">
                            <div id="synonyms" style="display: none">
                            </div>
                            <div class="column-wrap" id="results">
                            </div>
                        </div>

                    </div>
                </div>
            </div>
            <script src="//ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js"></script>
            <script>!window.jQuery && document.write(unescape('%3Cscript src="/js/libs/jquery-1.4.4.min.js"%3E%3C/script%3E'))</script>
            <script src="/js/libs/jquery.text-overflow.min.js"></script>
            <script type="text/javascript">
                //<![CDATA[
                $(document).ready(function () {

                    var searchDefaultText = "Full Text Search";

                    $('#tbSearchTerm')
                            .val(searchDefaultText)
                            .focus(function () {
                                if ($(this).val() == searchDefaultText) {
                                    $(this).val("");
                                }
                            })
                            .blur(function () {
                                if ($.trim($(this).val()) == "") {
                                    $(this).val(searchDefaultText);
                                }
                            })
                            .keydown(function(e) {
                                if (e.keyCode == 13) {
                                    doSearch(0, 10);
                                }
                            });


                    $('#btnSearchSubmit').click(function (e) {
                        if ($('#tbSearchTerm').val() == searchDefaultText) {
                            e.preventDefault();
                            return false;
                        }
                    });

                });

                function doSearch(start, rows) {
                    // var url = "http://localhost:8080/bhl-ftindex-demo/search/ajaxSearch?q=" + $("#tbSearchTerm").val();
                    var url = "ajaxSearch?q=" + $("#tbSearchTerm").val() + '&start=' + start + "&rows=" + rows;
                    var buf = "";
                    $("#status-box").css("display", "block");
                    $("#synonyms").html("").css("display", "none")
                    $("#results").html("");

                    $.getJSON(url, function(data) {
                        var itemNumber = data.start + 1;
                        buf += '<div class="results-summary">' + data.numFound + ' results.</div>'
                        $.each(data.resultList, function(idx, obj) {
                            buf += '<div class="result-box">'
                            buf += '<b>' + itemNumber++;
                            buf += '.</b> <A target="item" HREF="http://bhl.ala.org.au/item/' + obj.itemId + '">' + obj.name + '</A> '
                            buf += '<br />BHL Item ' + obj.itemId + ', Score ' + obj.score
                            // buf += '<br />Page:<A style="vertical-align: middle" target="page image" HREF="http://bhl.ala.org.au/page/' + obj.pageId + '"><IMG SRC="http://bhl.ala.org.au/pagethumb/' + obj.pageId + '" width="50px"></IMG></A>'

                            if (obj.context) {
                                buf += '<div class="highlight-context">'
                                buf += '<div style="float:left; width: 530px">' + obj.context + '<br /><A style="vertical-align: bottom;" HREF="http://bhl.ala.org.au/pageOcr/' + obj.pageId + '" target="page_ocr">More...</A></div>'
                                buf += '<div style="" class="page-thumbnail" align="center"><A target="page image" HREF="http://bhl.ala.org.au/page/' + obj.pageId + '"><IMG SRC="http://bhl.ala.org.au/pagethumb/' + obj.pageId + '" height="100px"></IMG></A></div>'
                                buf += "</div>"
                            }
                            buf += "</div>";
                        })

                        var prevStart = start - rows;
                        var nextStart = start + rows;

                        buf += '<div id="button-bar">'
                        buf += '<input type="button" value="Previous page" onclick="doSearch(' + prevStart + ',' + rows + ')">'
                        buf += '&nbsp;&nbsp;&nbsp;'
                        buf += '<input type="button" value="Next page" onclick="doSearch(' + nextStart + ',' + rows + ')">'
                        buf += '</div>'

                        $("#results").html(buf);
                        if (data.synonyms) {
                            buf = "<b>Synonyms used:</b>&nbsp;";
                            buf += data.synonyms.join(", ");
                            $("#synonyms").html(buf).css("display", "block")
                        } else {
                            $("#synonyms").html("").css("display", "none")
                        }
                        $("#status-box").css("display", "none");
                    });

                }
                //]]>
            </script>

    </body>
</html>