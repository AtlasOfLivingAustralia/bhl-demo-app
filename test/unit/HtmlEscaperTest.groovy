package bhl.ftindex.demo

/**
 * Created by IntelliJ IDEA.
 * User: baird
 * Date: 2/02/12
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
class HtmlEscaperTest extends GroovyTestCase {
    
    void test1() {
        escapeTest("Some text <someTag>", "Some text &lt;someTag&gt;")
    }

    void test2() {
        escapeTest("Some text <e", "Some text &lt;e")
    }

    void test3() {
        escapeTest("Some text <em>someTag</em>", "Some text <em>someTag</em>")
    }

    void test4() {
        escapeTest("Some text <em/>someTag", "Some text <em/>someTag")
    }

    void test5() {
        escapeTest("Some text <em>someTag", "Some text <em>someTag")
    }

    void test6() {
        escapeTest("Some text <em someTag", "Some text &lt;em someTag")
    }

    void escapeTest(String test, String expected) {
        def actual = HtmlEscaper.escape(test);
        println("*** '${expected}' ::: '${actual}'");
        assertEquals(expected, actual)
    }
}
