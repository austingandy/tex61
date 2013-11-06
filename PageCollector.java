package tex61;

import java.util.List;

/** A PageAssembler that collects its lines into a designated List.
 *  @author Austin Gandy
 */
class PageCollector extends PageAssembler {

    /** A new PageCollector that stores lines in OUT. */
    PageCollector(List<String> out) {
        _out = out;
    }

    /** Add LINE to my List. */
    @Override
    void write(String line) {
        _out.add(line);
    }

    /** Writes a blank line. */
    @Override
    void write() {
        write("");
    }

    /** ArrayList where lines sent to the PageAssembler are stored. */
    private List<String> _out;
}
