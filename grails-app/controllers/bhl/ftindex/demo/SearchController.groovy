package bhl.ftindex.demo

import grails.converters.JSON

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

        String q = request.getParameter("q");
        
        int start = request.getParameter("start") as int ?: 0;
        int rows = request.getParameter("rows") as int ?: 10;

        if (start < 0) start = 0;
        if (rows <= 0) rows = 10;

        if (q.startsWith("taxon:")) {
            q = q.substring(6);
            useSynonyms = true;
        }

        q = URLEncoder.encode('"' + q + '"', "utf-8") ;
        // def urlRoot = "http://ala-biocachedb1.vm.csiro.au:8080/bhl-ftindex/";
        def urlRoot = 'http://bhlidx.ala.org.au/'

        def urlStr = "${urlRoot}/select?indent=off&version=2.2&q=${q}&fq=&start=${start}&rows=${rows}&fl=name%2CpageId%2CitemId%2Cscore&qt=&wt=json&explainOther=&hl=on&hl.fl=text&hl.fragsize=${fragSize}"
        if (useSynonyms) {
            urlStr += "&taxa=true"
        }

        def url = new URL(urlStr);
        def response = JSON.parse(url.newReader())
        def list = response["response"]["docs"];
        def results = ['numFound':response['response']['numFound'], 'start' : response['response']['start']];
        def resultsList = [];
        def hl = response["highlighting"];

        list.each {doc ->
            def result = ["name": doc.name, "pageId" : doc.pageId, "itemId": doc.itemId, "score" : doc.score];
            if (hl && hl[doc.pageId]) {
                def context = hl[doc.pageId]["text"]
                if (context && context[0]) {
                    result["context"] = context[0];
                }
            }
            resultsList.add(result)
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

        render results as JSON
    }
}
