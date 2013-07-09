<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title></title>
        <style type="text/css">

        .page-box {
            padding: 5px;
        }

        .thumbnail {

            display: inline-block;
            padding: 5px;
            margin: 5px;
            background: none repeat scroll 0 0 white;
            width: 60px;
            height: 100px;
            border: 1px solid #C5CED3;
            border-radius: 5px 5px 5px 5px;
        }

        </style>
        <script type="text/javascript">

          $(document).ready(function() {
              loadPages(0);
          });

          function loadPages(start) {
            $("#status-box").css('display', 'block');
            var url = "ajaxSearch?q=${q}&pageLimit=-1&pageOffset=" + start;
            var buf = "";
            $.ajax({
                url: url,
                dataType: 'json',
                data: null,
                success:  function(data) {
                  var itemNumber = data.start + 1;
                  $.each(data.resultList, function(idx, obj) {
                      buf += '<h2>Item: <A target="item" HREF="http://bhl.ala.org.au/item/' + obj.itemId + '">' + obj.name + '</A></h2>'
                      buf += '<div class="page-box">'
                      var pageCount = 0;
                      $.each(obj.pages, function(idx, page) {
                          buf += '<div class="thumbnail"><A target="page image" HREF="http://bhl.ala.org.au/page/' + page.pageId + '"><IMG SRC="http://bhl.ala.org.au/pagethumb/' + page.pageId + '" title="Page Id ' + page.pageId + '"  width="60px" height="100px"></IMG><div class="highlight-context">' + page.context + '</div></A></div>'
                        if (++pageCount % 10 == 0) {
                          buf += "<br />"
                        }
                      })
                      buf += "</div>"
                      var suffix = '';
                      if (obj.pageCount > 1) {
                          suffix = 's';
                      }
                      buf += '<br /><div><hr/><b>' + obj.pageCount + '</b> matching page' + suffix + ' in item ' + obj.itemId + ', Score ' + obj.score
                      buf += "</div>";
                  })

                  $("#results").html(buf);
                  if (data.synonyms) {
                      buf = "<b>Synonyms used:</b>&nbsp;";
                      buf += data.synonyms.join(", ");
                      $("#synonyms").html(buf).css("display", "block")
                  } else {
                      $("#synonyms").html("").css("display", "none")
                  }
                  $("#status-box").css("display", "none");
              },
              error: function(jqXHR, textStatus, errorThrown) {
                  $("#status-box").css("display", "none");
                  $("#results").html('An error has occurred, probably due to invalid query syntax');
              }
            });
          }

        </script>
    </head>
    <body>
        <div class="column-wrap">
            <h1>Pages matching &quot;${originalQuery}&quot;</h1>

            <div id="status-box" class="column-wrap" style="display: none;">
                <div id="search-status" class="column-wrap" >
                    <span style="vertical-align: middle; ">
                        Finding pages, please wait...
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
    </body>
</html>