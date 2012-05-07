package bhl.ftindex.demo

import grails.converters.JSON
import javax.swing.text.html.HTML
import com.lowagie.text.html.HtmlEncoder

class SearchController {

    def index() {
        redirect([action: home])
    }

    def home = {
        [foo:"bar"]
    }

    def ajaxSearch = {
        boolean useSynonyms = false;

        int fragSize = 200;

        String q = request.getParameter("q").trim();
        
        int start = request.getParameter("start") as int ?: 0;
        int rows = request.getParameter("rows") as int ?: 10;

        if (start < 0) start = 0;
        if (rows <= 0) rows = 10;

        if (q.startsWith("taxon:")) {
            if (!(q.startsWith('"') && q.endsWith('"'))) {
                q = '"' + q.substring(6) + '"'
            }
            useSynonyms = true;
        }

        q = URLEncoder.encode(q, "utf-8")

        def urlRoot = 'http://bhlidx.ala.org.au'

        def urlStr = "${urlRoot}/select?indent=off&version=2.2&q=${q}&fq=&start=${start}&rows=${rows}&fl=name%2CpageId%2CitemId%2Cscore&qt=&wt=json&explainOther="
        urlStr += "&hl=on&hl.fl=text&hl.fragsize=${fragSize}"
        urlStr += "&group=true&group.field=itemId&group.limit=7"
        if (useSynonyms) {
            urlStr += "&taxa=true"
        }

        def url = new URL(urlStr);
        def response = JSON.parse(url.newReader())
        
        // println response as JSON

        def results = ['numFound':response['grouped']['itemId']['matches'], 'start' : start];

        def itemList = response["grouped"]['itemId']['groups'];
        def resultsList = [];
        def hl = response["highlighting"];

        for (itemGroup in itemList) {
            def item = [
                    "name":bhl.ftindex.demo.HtmlEscaper.escape(itemGroup['doclist']['docs'][0]['name']),
                    'pageCount':itemGroup['doclist']['numFound'],
                    'score':itemGroup['doclist']['maxScore'],
                    'itemId':itemGroup['groupValue']
            ]
            def pages = []
            for (itemPage in itemGroup['doclist']['docs']) {
                def page = ["pageId": itemPage.pageId, "score": itemPage.score];
                if (hl && hl[itemPage.pageId]) {
                    def context = hl[itemPage.pageId]["text"]
                    if (context && context[0]) {
                        page["context"] = HtmlEscaper.escape(context[0]);
                    }
                }
                pages.add(page);
            }
            item['pages'] = pages;
            resultsList.add(item)
        }

        if (useSynonyms) {
            def syns = response["synonyms"];
            def synonyms = []
            if (syns) {
                syns.each { item -> 
                    if (!item.toString().isNumber()) {
                        synonyms.add(item)
                    }
                }
            }
            results["synonyms"] = synonyms;
        }
        
        results["resultList"] = resultsList
        
        // println(results as JSON)

        render results as JSON
    }
}
