package tex61;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit test of LineAssembler
 * @author Austin Gandy
 */
public class LineAssemblerTest {

    /** This void method sets up an assembler to test later. */
    public void setupAssembler() {
        output = new StringWriter();
        writer = new PrintWriter(output);
        PagePrinter page = new PagePrinter(writer);
        normalLine = new LineAssembler(page, false);
        endnoteLine = new LineAssembler(page, true);
    }

    /** This void method adds words to the LineAssembler instantiated in
     * setupAssembler. */
    public void addLines() {
        setupAssembler();
        for (int i = 0; i < 10; i += 1) {
            normalLine.addWord(String.valueOf(i));
            endnoteLine.addWord(String.valueOf(i));
        }
    }

    /** Ensures addWord, addText, and getWordLine are both fully functional. */
    @Test
    public void testAddWord() {
        addLines();
        ArrayList<String> normalList = normalLine.getWordLine();
        ArrayList<String> endnoteList = endnoteLine.getWordLine();
        ArrayList<String> compare = new ArrayList<String>();
        for (int i = 0; i < 10; i += 1) {
            compare.add(String.valueOf(i));
        }
        for (int i = 0; i < 10; i += 1) {
            assertEquals(normalList.get(i), compare.get(i));
            assertEquals(endnoteList.get(i), compare.get(i));
        }
        compare.clear();
        assertEquals(normalLine.getWordLine(), compare);
        assertEquals(endnoteLine.getWordLine(), compare);
        normalLine.addText("something");
        normalLine.addText(" and something else");
        normalLine.finishWord();
        assertEquals(normalLine.getWordLine().get(0),
                "something and something else");
        endnoteLine.addText("something");
        endnoteLine.addText(" and something else");
        endnoteLine.finishWord();
        assertEquals(endnoteLine.getWordLine().get(0),
                "something and something else");
    }

    /** Checks instantiated values and makes sure setters are operational. */
    @Test
    public void testSettingsAndSetters() {
        setupAssembler();
        assertEquals(normalLine.getIndent(), Defaults.INDENTATION);
        assertEquals(endnoteLine.getIndent(), Defaults.ENDNOTE_INDENTATION);
        assertEquals(normalLine.getJustify(), true);
        assertEquals(endnoteLine.getJustify(), true);
        assertEquals(normalLine.getTextWidth(), Defaults.TEXT_WIDTH);
        assertEquals(endnoteLine.getTextWidth(), Defaults.ENDNOTE_TEXT_WIDTH);
        assertEquals(normalLine.getParindent(), Defaults.PARAGRAPH_INDENTATION);
        assertEquals(endnoteLine.getParindent(),
                Defaults.ENDNOTE_PARAGRAPH_INDENTATION);
        normalLine.setFill(false);
        assertEquals(normalLine.getFill(), false);
        endnoteLine.setFill(false);
        assertEquals(endnoteLine.getFill(), false);
        normalLine.setParSkip(9);
        endnoteLine.setParSkip(9);
        assertEquals(normalLine.getParSkip(), 9);
        assertEquals(endnoteLine.getParSkip(), 9);
    }

    /** Tests auxiliary methods like charsInLine. */
    @Test
    public void testAuxiliaryMethods() {
        setupAssembler();
        LineAssembler[] assemblers = new LineAssembler[2];
        assemblers[0] = normalLine;
        assemblers[1] = endnoteLine;
        ArrayList<String> line = new ArrayList<String>();
        normalLine.addWord("The");
        normalLine.addWord("following");
        normalLine.addWord("quotation");
        normalLine.addWord("about");
        normalLine.addWord("writing");
        normalLine.addWord("test");
        normalLine.addWord("programs");
        normalLine.addWord("for");
        normalLine.addWord("a");
        normalLine.addWord("document");
        line.add("The");
        line.add("following");
        line.add("quotation");
        line.add("about");
        line.add("writing");
        line.add("test");
        line.add("programs");
        line.add("for");
        line.add("a");
        line.add("document");
        String result = "The following  quotation about writing  test "
                + "programs for  a document";
        final int charLength = 48;
        assertEquals(normalLine.pubJustifyLine(line, 1),
                normalLine.pubJustifyLine(normalLine.getWordLine(), 1));
        for (LineAssembler assembler : assemblers) {
            assembler.addWord("something");
            assembler.addWord("Something else");
            assembler.addWord("one more for good measure");
            assertEquals(charLength,
                    assembler.pubCharsIn(assembler.getWordLine()));
            assertEquals(assembler.pubAddSpaces(3), "   ");
            assembler.finishLine();
            assembler.setTextWidth(72);
            assertEquals(assembler.pubJustifyLine(line, 3), result);
        }
    }

    /** Collects output to a PrintWriter. */
    private StringWriter output;
    /** Collects output from a PageAssembler. */
    private PrintWriter writer;
    /** LineAssembler for main text. */
    private LineAssembler normalLine;
    /** LineAssembler for endnotes. */
    private LineAssembler endnoteLine;
}
