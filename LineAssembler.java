package tex61;

import java.util.ArrayList;

import static tex61.Defaults.*;

/** An object that receives a sequence of words of text and formats
 *  the words into filled and justified text lines that are sent to a receiver.
 *  @author Austin Gandy
 */
class LineAssembler {

    /** A new, empty line assembler with default settings of all
     *  parameters, sending finished lines to PAGES.
     *  Default values set according to ENDNOTES. */
    LineAssembler(PageAssembler pages, boolean endnotes) {
        _pages = pages;
        _firstPar = true;
        _currentWord = "";
        _endnote = endnotes;
        _wordLine = new ArrayList<String>();
        _justify = true;
        _fill = true;
        _firstLine = true;
        if (!endnotes) {
            _parindent = PARAGRAPH_INDENTATION;
            _indent = INDENTATION;
            _textWidth = TEXT_WIDTH;
            _parSkip = PARAGRAPH_SKIP;
        } else {
            _parindent = ENDNOTE_PARAGRAPH_INDENTATION;
            _indent = ENDNOTE_INDENTATION;
            _textWidth = ENDNOTE_TEXT_WIDTH;
            _parSkip = ENDNOTE_PARAGRAPH_SKIP;
        }
    }

    /** Add TEXT to the word currently being built. */
    void addText(String text) {
        _currentWord = _currentWord + text;
    }

    /** Finish the current word, if any, and add to words being accumulated. */
    void finishWord() {
        if (!_currentWord.equals("") || _currentWord == null) {
            addWord(_currentWord);
            _currentWord = "";
        }
    }

    /** Add WORD to the formatted text. */
    void addWord(String word) {
        _wordLine.add(word);
    }

    /** Sends contents of current line to _pages then starts a new line. */
    void finishLine() {
        if (!_currentWord.matches("\\s") && !_currentWord.equals("")) {
            addWord(_currentWord);
        }
        _pages.addLine(lineToString(_wordLine));
        _firstLine = false;
        _wordLine.clear();
    }

    /** Set the current indentation to VAL. VAL >= 0. */
    void setIndentation(int val) {
        try {
            if (val >= 0) {
                _indent = val;
            } else {
                throw new FormatException("Error: attempted to set negative"
                        + "indentation");
            }
        } catch (FormatException e) {
            FormatException.reportError(e.getMessage());
        }
    }

    /** Set the current paragraph indentation to VAL. VAL >= 0. */
    void setParIndentation(int val) {
        _parindent = val;
    }

    /** Set the text width to VAL, where VAL >= 0. */
    void setTextWidth(int val) {
        try {
            if (val <= 0) {
                throw new FormatException("Error: attempted to set negative"
                        + "");
            } else {
                _textWidth = val;
            }
        } catch (FormatException e) {
            FormatException.reportError(e.getMessage());
        }
    }

    /** Iff ON, set fill mode. */
    void setFill(boolean on) {
        _fill = on;
    }

    /** Iff ON, set justify mode (which is active only when filling is
     *  also on). */
    void setJustify(boolean on) {
        _justify = on;
    }

    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParSkip(int val) {
        try {
            if (_wordLine.size() == 0) {
                if (val >= 0) {
                    _parSkip = val;
                } else {
                    throw new FormatException("Error: attempted to make parskip"
                            + "negative");
                }
            } else {
                _holding = true;
                _nextSkip = val;
            }
        } catch (FormatException e) {
            FormatException.reportError(e.getMessage());
        }
    }

    /** Set page height to VAL > 0. */
    void setTextHeight(int val) {
        try {
            if (val > 0) {
                _pages.setTextHeight(val);
            } else {
                throw new FormatException("Error: attempted to make testheight"
                        + "negative");
            }
        } catch (FormatException e) {
            FormatException.reportError(e.getMessage());
        }
    }

    /** This method iterates through LIST and constructs a string of the words
     * with spaces inserted in between each word.
     *  @return words with spaces. */
    private String lineToString(ArrayList<String> list) {
        String output = "";
        for (int i = 0; i < list.size(); i += 1) {
            output += list.get(i);
            if (i < list.size() - 1) {
                output += " ";
            }
        }
        return output;
    }

    /** Process the end of the current input line.  No effect if
     *  current line accumulator is empty or in fill mode.  Otherwise,
     *  adds a new complete line to the finished line queue and clears
     *  the line accumulator. */
    void newLine() {
        if (!_fill && _wordLine.size() > 0) {
            String indent = addSpaces(_firstLine ? _indent + _parindent
                    : _indent);
            for (String word : _wordLine) {
                if (word.equals("")) {
                    return;
                }
            }
            String output = lineToString(_wordLine);
            _pages.addLine(indent + output);
            _wordLine.clear();
            _firstLine = false;
        }
    }

    /** If there is a current unfinished paragraph pending, close it
     *  out and start a new one. */
    void endParagraph() {
        finishWord();
        if (!_endnote) {
            if (!_firstPar && _fill) {
                writeBlankLines();
            }
            _firstPar = false;
            if (_fill) {
                processPhil();
            } else {
                emitLine(_wordLine, _firstLine
                        ? _indent + _parindent : _indent);
                if (!_firstPar) {
                    writeBlankLines();
                }
            }
            _wordLine.clear();
            _firstLine = true;
            if (_holding) {
                _parSkip = _nextSkip;
            }
        }
    }

    /** Transfer contents of LINE to _pages, adding INDENT characters of
     *  indentation, and a total of SPACES spaces between words, evenly
     *  distributed.  Assumes _words is not empty.
     *  Clears _words and _chars. */
    private void emitLine(ArrayList<String> line, int indent) {
        String indentation = addSpaces(indent);
        if (_justify) {
            _pages.addLine(indentation + justifyLine(line,
                    indentation.length()));
        } else {
            _pages.addLine(indentation + lineToString(line));
        }
    }

    /** Writes _parSkip spaces to _pages. */
    private void writeBlankLines() {
        int i = _parSkip;
        while (i > 0) {
            if (!_pages.getFirstLine()) {
                _pages.addLine("\n");
                i -= 1;
            }
        }
    }
    /** This method will separate words into lines and place the optimal
     *  amount of words into each line outputting the lines as we go. */
    private void processPhil() {
        ArrayList<String> line = new ArrayList<String>();
        int indent = _firstLine ? _indent + _parindent : _indent;
        for (String word : _wordLine) {
            if (word == null || word.matches("\\s")
                    || word.matches("\\n")) {
                continue;
            }
            int nonBlankChars = charsIn(line);
            if (word.length() > _textWidth) {
                if (line.size() > 0) {
                    emitLine(line, indent);
                    indent = _indent;
                    line.clear();
                }
                line.add(word);
                emitLine(line, indent);
                indent = _indent;
                line.clear();
                continue;
            }
            if (nonBlankChars + word.length() + line.size() + indent
                    > _textWidth) {
                emitLine(line, indent);
                line.clear();
                indent = _indent;
            }
            line.add(word);
        }
        boolean temp = _justify;
        _justify = false;
        emitLine(line, indent);
        _justify = temp;
    }

    /** Justifies LINE and adds INDENT according to the specifications in the
     * project description.
     * @return a justified line. */
    private String justifyLine(ArrayList<String> line, int indent) {
        if (line.size() == 1) {
            return line.get(0);
        }
        String result = "";
        int l = charsIn(line);
        if (line.size() - 1 + l == _textWidth || !_justify) {
            return lineToString(line);
        }
        double b = _textWidth - indent - l;
        if (b >= 3 * (line.size())) {
            for (int i = 0; i < line.size(); i += 1) {
                if (i != 0) {
                    result = result + "   " + line.get(i);
                } else {
                    result = result + line.get(i);
                }
            }
        } else {
            int currNumSpaces = 0;
            int prevNumSpaces;
            int insertBlanks;
            for (double i = 0; i < line.size(); i += 1) {
                prevNumSpaces = currNumSpaces;
                currNumSpaces = (int) (0.5 + b * i
                        / (double) (line.size() - 1));
                insertBlanks = currNumSpaces - prevNumSpaces;
                result = result + addSpaces(Math.min(3, insertBlanks))
                        + line.get((int) i);
            }
        }
        return result;
    }

    /** A method that takes in a LINE and INDENT, and returns the output
     * of justifyLine publicly.
     * @param line
     * @param indent
     * @return justified line. */
    public String pubJustifyLine(ArrayList<String> line, int indent) {
        return justifyLine(line, indent);
    }

    /** Returns NUM spaces.
     * @return NUM characters of blank space. */
    private String addSpaces(int num) {
        String spaces = "";
        for (int i = 0; i < num; i += 1) {
            spaces += " ";
        }
        return spaces;
    }

    /** Public version of addSpaces for unit testing. Take in NUM and
     * @return NUM spaces */
    public String pubAddSpaces(int num) {
        return addSpaces(num);
    }

    /** Determines the number of characters in WORDS.
     *  @return number of characters in WORDS. */
    private int charsIn(ArrayList<String> words) {
        int count = 0;
        for (String word : words) {
            count += word.length();
        }
        return count;
    }

    /** Public call to charsIn used for unit testing. Take in WORDS and
     * @return the number of characters in WORDS. */
    public int pubCharsIn(ArrayList<String> words) {
        return charsIn(words);
    }

    /** @return PageAssembler associated with the page. */
    public PageAssembler getPages() {
        return _pages;
    }

    /** @return the current indentation setting. */
    public int getIndent() {
        return _indent;
    }

    /** @return the current parindentation setting. */
    public int getParindent() {
        return _parindent;
    }

    /** @return ArrayList of words _wordLine. Clears _wordLine to avoid double
     *  printing endnotes. */
    @SuppressWarnings("unchecked")
    public ArrayList<String> getWordLine() {
        ArrayList<String> line = (ArrayList<String>) _wordLine.clone();
        _wordLine.clear();
        return line;
    }

    /** Sets _endnote to ON. */
    public void setEndnote(boolean on) {
        _endnote = on;
    }

    /** @return boolean _endnote. */
    public boolean getEndnote() {
        return _endnote;
    }

    /** @return String _currentWord. */
    public String getCurrentWord() {
        return _currentWord;
    }

    /** @return boolean _justify. */
    public boolean getJustify() {
        return _justify;
    }

    /** @return int _textWidth. */
    public int getTextWidth() {
        return _textWidth;
    }

    /** @return boolean _fill. */
    public boolean getFill() {
        return _fill;
    }

    /** @return integer _parSkip. */
    public int getParSkip() {
        return _parSkip;
    }

    /** Destination given in constructor for formatted lines. */
    private final PageAssembler _pages;
    /** Array of words in a line. Size starts at 2 and must be
     *  re-sized as the array grows. */
    private ArrayList<String> _wordLine;
    /** The current word we're working on. */
    private String _currentWord;
    /** True if we should be justifying lines. */
    private boolean _justify;
    /** Number of characters and/or spaces allowed on the current line. */
    private int _textWidth;
    /** Number of spaces before each line. */
    private int _indent;
    /** Number of additional spaces before each paragraph. */
    private int _parindent;
    /** Number of blank lines between paragraphs. */
    private int _parSkip;
    /** True if we should be filling lines. */
    private boolean _fill;
    /** True if we are assembling lines of an endnote. */
    private boolean _endnote;
    /** Indicates whether or not we are processing the first line in a
     *  paragraph. */
    private boolean _firstLine;
    /** Indicates whether or not we are processing the first paragraph of
     * a file. */
    private boolean _firstPar;
    /** True if we are holding a current value for _nextSkip. */
    private boolean _holding;
    /** Number of lines to be inserted after the next paragraph. */
    private int _nextSkip;
}

