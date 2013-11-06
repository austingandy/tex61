package tex61;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/** Receives (partial) words and commands, performs commands, and
 *  accumulates and formats words into lines of text, which are sent to a
 *  designated PageAssembler.  At any given time, a Controller has a
 *  current word, which may be added to by addText, a current list of
 *  words that are being accumulated into a line of text, and a list of
 *  lines of endnotes.
 *  @author Austin Gandy
 */
class Controller {

    /** A new Controller that sends formatted output to OUT. */
    Controller(PrintWriter out) {
        _output = out;
        _mainPage = new PagePrinter(_output);
        _currentAssembler = new LineAssembler(_mainPage, false);
        _endnotePage = new PageCollector(new ArrayList<String>());
        _endnoteSettings = new LineAssembler(_endnotePage, true);
        _refNum = 1;
        _endNotes = new ArrayList<ArrayList<String>>();
        _endnoteMode = false;
    }

    /** A new Controller for endnotes that sends lines to ASSEMBLER. */
    Controller(LineAssembler assembler) {
        _currentAssembler = assembler;
        _endnoteMode = true;
    }

    /** Add TEXT to the end of the word of formatted text currently
     *  being accumulated. */
    void addText(String text) {
        _currentAssembler.addText(text);
    }

    /** Finish any current word of text and, if present, add to the
     *  list of words for the next line.  Has no effect if no unfinished
     *  word is being accumulated. */
    void endWord() {
        _currentAssembler.finishWord();
    }

    /** Finish any current word of formatted text and process an end-of-line
     *  according to the current formatting parameters. */
    public void addNewLine() {
        endWord();
        _currentAssembler.newLine();
    }

    /** Finish any current word of formatted text, format and output any
     *  current line of text, and start a new paragraph. */
    void endParagraph() {
        _currentAssembler.endParagraph();
    }

    /** Write all accumulated endnotes to _mainText. */
    public void writeEndnotes() {
        _currentAssembler.setParIndentation(_endnoteSettings.getParindent());
        _currentAssembler.setIndentation(_endnoteSettings.getIndent());
        _currentAssembler.setParSkip(0);
        _currentAssembler.setTextWidth(_endnoteSettings.getTextWidth());
        for (int i = 0; i < _endNotes.size(); i += 1) {
            ArrayList<String> line = _endNotes.get(i);
            addText("[" + (i + 1) + "] ");
            for (String word : line) {
                addText(word);
                endWord();
            }
            _currentAssembler.endParagraph();
        }
    }

    /** If valid, process TEXT into an endnote, first appending a reference
     *  to it to the line currently being accumulated. */
    void formatEndnote(String text) {
        addText("[" + String.valueOf(_refNum) + "]");
        Controller controller = new Controller(_endnoteSettings);
        InputParser endParse = new InputParser(text, controller);
        endParse.process();
        _endNotes.add(_endnoteSettings.getWordLine());
        _refNum += 1;
    }

    /** Set the current text height (number of lines per page) to VAL, if
     *  it is a valid setting.  Ignored when accumulating an endnote. */
    void setTextHeight(int val) {
        _currentAssembler.setTextHeight(val);
    }

    /** Set the current text width (width of lines including indentation)
     *  to VAL, if it is a valid setting. */
    void setTextWidth(int val) {
        _currentAssembler.setTextWidth(val);
    }

    /** Set the current text indentation (number of spaces inserted before
     *  each line of formatted text) to VAL, if it is a valid setting. */
    void setIndentation(int val) {
        _currentAssembler.setIndentation(val);
    }

    /** Set the current paragraph indentation (number of spaces inserted before
     *  first line of a paragraph in addition to indentation) to VAL, if it is
     *  a valid setting. */
    void setParIndentation(int val) {
        _currentAssembler.setParIndentation(val);
    }

    /** Set the current paragraph skip (number of blank lines inserted before
     *  a new paragraph, if it is not the first on a page) to VAL, if it is
     *  a valid setting. */
    void setParSkip(int val) {
        _currentAssembler.setParSkip(val);
    }

    /** Iff ON, begin filling lines of formatted text. */
    void setFill(boolean on) {
        _currentAssembler.setFill(on);
    }

    /** Iff ON, begin justifying lines of formatted text whenever filling is
     *  also on. */
    void setJustify(boolean on) {
        _currentAssembler.setJustify(on);
    }

    /** Finish the current formatted document or endnote (depending on mode).
     *  Formats and outputs all pending text. */
    void close() {
        endWord();
        addNewLine();
        endParagraph();
        if (!_endnoteMode) {
            writeEndnotes();
        }
    }

    /** @return the PageAssembler associated with _currentAssembler. */
    public PageAssembler getPages() {
        return _currentAssembler.getPages();
    }

    /** @return the list of words being accumulated in _currentAssembler. */
    public ArrayList<String> getWordLine() {
        return _currentAssembler.getWordLine();
    }

    /** @return _currentAssembler. */
    public LineAssembler getCurrentAssembler() {
        return _currentAssembler;
    }

    /** Number of next endnote. */
    private int _refNum;
    /** Instance variable for the input out. */
    private PrintWriter _output;
    /** LineAssembler that lines from the main document go to. */
    private LineAssembler _mainText;
    /** Line assembler that the endnote lines go to. */
    private LineAssembler _endnoteText;
    /** PageAssembler used by _mainText. */
    private PagePrinter _mainPage;
    /** PageAssembler used by _endnoteText. */
    private PageCollector _endnotePage;
    /** LineAssembler that the controller should be feeding
     * lines to currently. */
    private LineAssembler _currentAssembler;
    /** List of endnote lines. */
    private List<ArrayList<String>> _endNotes;
    /** LineAssembler that stores the settings that endnotes should have. Used
     * to retrieve instance variables of previous LineAssembler used to process
     * an endnote and set the new LineAssembler to start with those same
     * settings. */
    private LineAssembler _endnoteSettings;
    /** True iff an InputParser is parsing an endnote. */
    private boolean _endnoteMode;
    /** List of words in an endnote. */
    private List<String> _endWords;
}
