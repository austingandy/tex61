package tex61;

/** A PageAssembler accepts complete lines of text (minus any
 *  terminating newlines) and turns them into pages, adding form
 *  feeds as needed.  It prepends a form feed (Control-L  or ASCII 12)
 *  to the first line of each page after the first.  By overriding the
 *  'write' method, subtypes can determine what is done with
 *  the finished lines.
 *  @author Austin Gandy
 */
abstract class PageAssembler {

    /** Create a new PageAssembler that sends its output to OUT.
     *  Initially, its text height is unlimited. It prepends a form
     *  feed character to the first line of each page except the first. */
    PageAssembler() {
        _textHeight = Integer.MAX_VALUE;
        _currentHeight = 0;
    }

    /** Add LINE to the current page, starting a new page with it if
     *  the previous page is full. A null LINE indicates a skipped line,
     *  and has no effect at the top of a page. */
    void addLine(String line) {
        if (_currentHeight < _textHeight) {
            if (line.matches("\\n")) {
                write(line);
            } else {
                write(line + "\n");
            }
            _currentHeight += 1;
        } else if (line != null && !line.contains("\n")) {
            write("\f" + line);
            _currentHeight = 1;
        }
    }

    /** Set text height to VAL, where VAL > 0. */
    void setTextHeight(int val) {
        _textHeight = val;
    }

    /** return true if _currentHeight equals _textHeight. */
    public boolean getFirstLine() {
        return (_currentHeight == _textHeight);
    }

    /** Perform final disposition of LINE, as determined by the
     *  concrete subtype. */
    abstract void write(String line);
    /** Write a blank line to the output. */
    abstract void write();
    /** Amount of lines allowed on each page. */
    private int _textHeight;
    /** The current height of the line on the page. */
    private int _currentHeight;
}
