package tex61;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;

import java.io.Reader;

import static tex61.FormatException.reportError;

/** Reads commands and text from an input source and send the results
 *  to a designated Controller. This essentially breaks the input down
 *  into "tokens"---commands and pieces of text.
 *  @author Austin Gandy
 */
class InputParser {

    /** Matches text between { } in a command, including the last
     *  }, but not the opening {.  When matched, group 1 is the matched
     *  text.  Always matches at least one character against a non-empty
     *  string or input source. If it matches and group 1 is null, the
     *  argument was not well-formed (the final } was missing or the
     *  argument list was nested too deeply). */
    private static final Pattern BALANCED_TEXT =
        Pattern.compile("(?s)((?:\\\\.|[^\\\\{}]"
                        + "|[{](?:\\\\.|[^\\\\{}])*[}])*)"
                        + "\\}"
                        + "|.");

    /** Matches input to the text formatter.  Always matches something
     *  in a non-empty string or input source.  After matching, one or
     *  more of the groups described by *_TOKEN declarations will
     *  be non-null.  See these declarations for descriptions of what
     *  this pattern matches.  To test whether .group(*_TOKEN) is null
     *  quickly, check for .end(*_TOKEN) > -1).  */
    private static final Pattern INPUT_PATTERN =
        Pattern.compile("(?s)(\\p{Blank}+)"
                        + "|(\\r?\\n((?:\\r?\\n)+)?)"
                        + "|\\\\([\\p{Blank}{}\\\\])"
                        + "|\\\\(\\p{Alpha}+)([{]?)"
                        + "|((?:[^\\p{Blank}\\r\\n\\\\{}]+))"
                        + "|(.)");

    /** Symbolic names for the groups in INPUT_PATTERN. */
    private static final int
        /** Blank or tab. */
        BLANK_TOKEN = 1,
        /** End of line or paragraph. */
        EOL_TOKEN = 2,
        /** End of paragraph (>1 newline). EOL_TOKEN group will also
         *  be present. */
        EOP_TOKEN = 3,
        /** \{, \}, \\, or \ .  .group(ESCAPED_CHAR_TOKEN) will be the
         *  character after the backslash. */
        ESCAPED_CHAR_TOKEN = 4,
        /** Command (\<alphabetic characters>).  .group(COMMAND_TOKEN)
         *  will be the characters after the backslash.  */
        COMMAND_TOKEN = 5,
        /** A '{' immediately following a command. When this group is present,
         *  .group(COMMAND_TOKEN) will also be present. */
        COMMAND_ARG_TOKEN = 6,
        /** Segment of other text (none of the above, not including
         *  any of the special characters \, {, or }). */
        TEXT_TOKEN = 7,
        /** A character that should not be here. */
        ERROR_TOKEN = 8;

    /** A new InputParser taking input from READER and sending tokens to
     *  OUT. */
    InputParser(Reader reader, Controller out) {
        _input = new Scanner(reader);
        _out = out;
        _endnoteParser = false;
    }

    /** A new InputParser whose input is TEXT and that sends tokens to
     *  OUT. */
    InputParser(String text, Controller out) {
        _input = new Scanner(text);
        _out = out;
        _endnoteParser = true;
    }

    /** Break all input source text into tokens, and send them to our
     *  output controller.  Finishes by calling .close on the controller. */
    void process() {
        String command;
        while (_input.findWithinHorizon(INPUT_PATTERN, 0) != null) {
            MatchResult match = _input.match();
            if (match.group(BLANK_TOKEN) != null) {
                _out.endWord();
                continue;
            } else if (match.group(EOP_TOKEN) != null) {
                _out.endParagraph();
            } else if (match.group(EOL_TOKEN) != null) {
                _out.addNewLine();
                continue;
            } else if (match.group(ESCAPED_CHAR_TOKEN) != null) {
                _out.addText(match.group(ESCAPED_CHAR_TOKEN));
            } else if (match.group(COMMAND_TOKEN) != null) {
                command = match.group(COMMAND_TOKEN);
                String arg = match.group(COMMAND_ARG_TOKEN);
                if (arg != null && !arg.equals("")) {
                    _input.findWithinHorizon(BALANCED_TEXT, 0);
                    match = _input.match();
                    arg = match.group(1);
                    if (arg == null) {
                        System.exit(1);
                    } else {
                        processCommand(command, arg);
                    }
                    continue;
                } else {
                    processCommand(match.group(COMMAND_TOKEN), null);
                    continue;
                }
            } else if (match.group(TEXT_TOKEN) != null) {
                _out.addText(match.group(TEXT_TOKEN));
                continue;
            } else {
                System.exit(1);
            }
        }
        _out.close();
    }


    /** Process \COMMAND{ARG} or (if ARG is null) \COMMAND.  Call the
     *  appropriate methods in our Controller (_out). */
    private void processCommand(String command, String arg) {
        try {
            int num;
            if (!command.equals("endnote") && arg != null && !arg.equals("")) {
                num = Integer.parseInt(arg);
            } else {
                num = 0;
            }
            switch (command) {
            case "indent":
                _out.setIndentation(num);
                break;
            case "parindent":
                _out.setParIndentation(num);
                break;
            case "textwidth":
                _out.setTextWidth(num);
                break;
            case "textheight":
                _out.setTextHeight(num);
                break;
            case "parskip":
                _out.setParSkip(num);
                break;
            case "nofill":
                _out.setFill(false);
                _out.setJustify(false);
                break;
            case "fill":
                _out.setFill(true);
                break;
            case "justify":
                _out.setJustify(true);
                break;
            case "nojustify":
                _out.setJustify(false);
                break;
            case "endnote":
                if (!_endnoteParser) {
                    _out.formatEndnote(arg);
                } else {
                    throw new FormatException("Endnotes nested too deeply");
                }
                break;
            default:
                reportError("unknown command: %s", command);
                throw new IndexOutOfBoundsException();
            }
        } catch (IndexOutOfBoundsException e) {
            System.exit(1);
        } catch (NumberFormatException e) {
            reportError(e.getMessage());
        }
    }

    /** Process \COMMAND{ARG} or (if ARG is null) \COMMAND.  Call the
     *  appropriate methods in our Controller (_out).
     *  Public version of processCommand for testing. */
    public void pubProcessCommand(String command, String arg) {
        processCommand(command, arg);
    }

    /** My input source. */
    private final Scanner _input;
    /** The Controller to which I send input tokens. */
    private Controller _out;
    /** True if this InputParser is parsing endnotes. */
    private boolean _endnoteParser;

}
