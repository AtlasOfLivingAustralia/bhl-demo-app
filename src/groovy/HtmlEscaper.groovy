package bhl.ftindex.demo

/**
 * Created by IntelliJ IDEA.
 * User: baird
 * Date: 2/02/12
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class HtmlEscaper {

    public static String escape(String text, allowed = ["em"]) {

        def biggest = allowed.max {
            s -> s.size()
        }

        def stream = new PushbackInputStream(new ByteArrayInputStream(text.getBytes()), biggest.size() + 1);

        StringBuilder b = new StringBuilder();
        def iChar = stream.read();
        while (iChar >= 0) {
            char ch = (char) iChar;
            switch (ch) {
                case '<':
                    if (!processTag(stream, allowed, b)) {
                        b.append("&lt;")
                    }
                    break;
                case '>':
                    b.append("&gt;")
                    break;
                case '&':
                    b.append("&amp;")
                    break;
                case '"':
                    b.append("&quot;")
                    break;
                default:
                    b.append(ch);
            }

            iChar = stream.read();
        }

        return b.toString();
    }

    static def processTag(PushbackInputStream stream, List<String> allowed, StringBuilder buffer) {
        def candidates = new ArrayList<String>(allowed);
        def tok = ""

        def charsRead = 0;
        boolean isClosing = false;
        boolean isSelfClosing = false;
        def iChar = stream.read();
        while (iChar >= 0) {
            charsRead++;
            char ch = (char) iChar;
            switch (ch) {
                case '/':
                    isClosing = true;
                    isSelfClosing = charsRead > 1;
                    break;
                case '>':
                    if (candidates.size() == 1) {
                        buffer.append("<");
                        if (isClosing && !isSelfClosing) {
                            buffer.append("/");
                        }
                        buffer.append(tok);
                        if (isSelfClosing) {
                            buffer.append("/");
                        }
                        buffer.append(">");
                    }
                    return true;
                default:
                    tok += (char) iChar;
                    candidates.removeAll {
                        !it.startsWith(tok.toLowerCase())
                    }
            }
            if (candidates.size() == 0) {
                break;
            }
            iChar = stream.read();
        }
        // If we get here this 'tag' is not allowed...
        // push back everything we read
        tok.reverse().chars.each {
            stream.unread((int) it);
        }
        return false;
    }
}
