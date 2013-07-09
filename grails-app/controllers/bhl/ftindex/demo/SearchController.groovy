package bhl.ftindex.demo

import grails.converters.JSON
import javax.swing.text.html.HTML
import com.lowagie.text.html.HtmlEncoder

class SearchController {

    def index() {
        redirect([action: home])
    }

    def home = {
        [:]
    }

    def ajaxSearch = {
        boolean useSynonyms = false;

        int fragSize = 200;

        String q = params.q?.trim()
        
        int start = params.int("start") ?: 0
        int rows = params.int("rows") ?: 10
        int pageLimit = params.int("pageLimit") ?: 7
        int pageOffset = params.int("pageOffset") ?: 0

        if (start < 0) start = 0;
        if (rows <= 0) rows = 10;

        if (q.startsWith("taxon:")) {
            if (!(q.startsWith('"') && q.endsWith('"'))) {
                q = '"' + q.substring(6) + '"'
            }
            useSynonyms = true;
        }

        q = URLEncoder.encode(q, "utf-8")

        def urlRoot = grailsApplication.config.bhlftindex.solrUrl

        def urlStr = "${urlRoot}/select?indent=off&version=2.2&q=${q}&fq=&start=${start}&rows=${rows}&fl=name%2CpageId%2CitemId%2Cscore&qt=&wt=json&explainOther="
        urlStr += "&hl=on&hl.fl=text&hl.fragsize=${fragSize}"
        urlStr += "&group=true&group.field=itemId&group.limit=${pageLimit}&group.offset=${pageOffset}"
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
            def pageCount = itemGroup['doclist']['numFound']
            def item = [
                    "name":bhl.ftindex.demo.HtmlEscaper.escape(itemGroup['doclist']['docs'][0]['name']),
                    'pageCount': pageCount,
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
            if (pageCount > pageLimit) {
                item['showPagesLink'] = true;
            }
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
        results["q"] = params.q

        render results as JSON
    }

    def pagesForItem = {
        def itemId = params.int("itemId")
        def q = params.q ?: ""
        q = "itemId:${itemId}%20AND%20" + q;
        [itemId: itemId, q: q, originalQuery: params.q]
    }
}
