tex61
=====

A document interpreter similar to LaTeX. This was a school project and we did get some starter code for it.
This included the Makefile, Main, FormatException, and Defaults. Everything else was given to us as skeletons
(some methods were defined but none were implemented -- no body to the method)

CONTENTS:

Makefile               Configuration file for gmake.  See the comment at the
                       beginning of this file to see what it does for you.
                       

tex61/

    Makefile           A simple makefile that delegates all requests to
                       the outer directory's makefile.

    Main.java          Entry point to program. You may modify this if you
                       want, but it will work as it stands.
    UnitTest.java      Main unit-test file, which calls any others.

    The following files in tex61 are all optional.  Use them if you see fit,
    and remove any you don't use.

     Controller.java   Top-level logic for formatting.
     Defaults.java     Collected default formatting parameters.
     FormatException.java
                       Defines an unchecked exception that can be used to
                       signal (expected) errors in program inputs.  This
                       class also contains some error-reporting routines.
     InputParser.java  Class that reads input files, breaks them into
                        significant pieces, and passes these to a Controller.
     LineAssembler.java
                       Used by a Controller to accumulate, justify, fill, and
                       output lines of text.
     PageAssembler.java
                       Abstract class that receives formatted lines of text
                       (from a Controller), and breaks them into pages as
                       determined by its parameters.
     PageCollector.java
                       A kind of PageAssembler thst simply collects lines into
                       a list for later output.
     PagePrinter.java  A kind of PageAssembler that prints lines it receives.


     PageAssemblerTest.java
                       Start of some unit tests for PageAssemblers.

