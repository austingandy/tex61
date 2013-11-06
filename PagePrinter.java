package tex61;

import java.io.PrintWriter;

/** A PageAssembler that sends lines immediately to a PrintWriter, with
 *  terminating newlines.
 *  @author Austin Gandy
 */
class PagePrinter extends PageAssembler {

    /** A new PagePrinter that sends lines to OUT. */
    PagePrinter(PrintWriter out) {
        super();
        _out = out;
    }

    /** Print LINE to my output. */
    @Override
    void write(String line) {
        _out.print(line);
    }

    /** Prints a blank line. */
    @Override
    void write() {
        _out.println();
    }

    /** Where we print stuff to. */
    private java.io.PrintWriter _out;
}
