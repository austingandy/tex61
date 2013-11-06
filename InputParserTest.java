package tex61;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import static org.junit.Assert.*;

/** Unit tests of InputParser. Also tests for controller by nature of getting
 *  values from a LineAssembler which have been passed from the InputParser
 *  to the Controller, and then from the controller to the LineAssembler.
 *  @author Austin Gandy */
public class InputParserTest {

    public void setupObjects() {
        try {
            writer = new PrintWriter("output.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        reader = new StringReader("Some arbitrary input.");
        controller = new Controller(writer);
        otherController = new Controller(writer);
        parser = new InputParser(reader, controller);
        assembler = controller.getCurrentAssembler();
        String input = "This is my input I'm going to type for a while and"
                + " eventually I'll \\parskip{2} change some stuff so I can"
                + "test my process() command \\indent{2}\\parindent{4}";
        textParser = new InputParser(input, otherController);
    }

    /** Tests InputParser.processCommand() indirectly through pubProcessCommand
     * since processCommand is private. */
    @Test
    public void testProcessCommand() {
        setupObjects();
        parser.pubProcessCommand("nofill", null);
        assertEquals(assembler.getFill(), false);
        parser.pubProcessCommand("parindent", "3");
        assertEquals(assembler.getParindent(), 3);
        parser.pubProcessCommand("textwidth",  "25");
        assertEquals(assembler.getTextWidth(), 25);
        parser.pubProcessCommand("parskip", "7");
        assertEquals(assembler.getParSkip(), 7);
        parser.pubProcessCommand("nojustify", null);
        assertEquals(assembler.getJustify(), false);
    }

    /** Tests InputParser.process(). Expects default endnote settings since
     * .close() sets LineAssembler to default endnote values. */
    @Test
    public void testProcess() {
        setupObjects();
        textParser.process();
        LineAssembler otherAssembler = otherController.getCurrentAssembler();
        boolean fill = otherAssembler.getFill();
        int indent = otherAssembler.getIndent();
        int parindent = otherAssembler.getParindent();
        int parskip = otherAssembler.getParSkip();
        assertEquals(fill, true);
        assertEquals(indent, Defaults.ENDNOTE_INDENTATION);
        assertEquals(parindent, Defaults.ENDNOTE_PARAGRAPH_INDENTATION);
        assertEquals(parskip, Defaults.ENDNOTE_PARAGRAPH_SKIP);
    }

    /** Input for the controller. */
    private PrintWriter writer;
    /** Reader to feed in to InputParser. */
    private Reader reader;
    /** output for the parser. */
    private Controller controller;
    /** InputParser we will be testing. */
    private InputParser parser;
    /** LineAssembler that controller feeds commands to. */
    private LineAssembler assembler;
    /** InputParser that processes text. */
    private InputParser textParser;
    /** another controller. */
    private Controller otherController;
}

