// Class to handle breakpoint display and store breakpoint line values
package gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AbstractDocument;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

class breakpointHandler {

    private breakpointDisplay breakpointDisplayObject;

    // The values at the same index in the array list `breakpoints` and
    // `programCounterBreakpoints` are related
    // E.g If the array list `breakpoints` has 1 in its 0th index and
    // `programCounterBreakpoints` has 2 in its 0th index,
    // it means the program counter of our program is 2 when we reach line 1 in our
    // input code
    public ArrayList<Integer> breakpoints;
    public ArrayList<Integer> programCounterBreakpoints;

    private Map<Integer, Object> memoryDisplayHighlightTags;
    private Map<Integer, Object> inputCodeAreaHighlightTags;
    private MouseAdapter mouseAdapterObjectInputCode;
    private MouseAdapter mouseAdapterObjectMemory;

    public breakpointHandler(breakpointDisplay breakpointDisplay) {

        breakpointDisplayObject = breakpointDisplay;
        setupMouseListener();
        setupInputCodeAreaMouseListener();
        breakpoints = new ArrayList<>();
        programCounterBreakpoints = new ArrayList<>();
        memoryDisplayHighlightTags = new HashMap<>();
        inputCodeAreaHighlightTags = new HashMap<>();
    }

    // Method to set up a mouse listener on the JTextArea 'memory_display_two'
    public void setupMouseListener() {

        mouseAdapterObjectMemory = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Triple<Integer, Integer, Integer> lineInfo = determineClickedLineLocation(
                            breakpointDisplayObject.second_display, e.getX(), e.getY());
                    int line = lineInfo.first;
                    toggleBreakpointAtLine(line);
                    synchronizeHighlights(line);
                }
            }
        };
        breakpointDisplayObject.second_display.addMouseListener(mouseAdapterObjectMemory);
        ;
    }

    // Method to set up a mouse listener on the JTextArea 'input_code_area'
    public void setupInputCodeAreaMouseListener() {

        mouseAdapterObjectInputCode = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Triple<Integer, Integer, Integer> lineInfo = determineClickedLineLocation(
                            breakpointDisplayObject.first_display, e.getX(), e.getY());
                    int line = lineInfo.first;
                    toggleBreakpointAtLine(line); // Updated to pass only line number
                    synchronizeHighlights(line);
                } else {
                }
            }
        };
        breakpointDisplayObject.first_display.addMouseListener(mouseAdapterObjectInputCode);
    }

    // Method to determine the clicked line location in the JTextArea
    public Triple<Integer, Integer, Integer> determineClickedLineLocation(
            JTextArea textAreaClicked, int xCoordinateOfClick, int yCoordinateOfClick) {

        int textPosition = textAreaClicked.viewToModel(new Point(xCoordinateOfClick, yCoordinateOfClick));
        Document document = textAreaClicked.getDocument();
        Element paragraphElement = ((AbstractDocument) document).getParagraphElement(textPosition);
        int startingOffsetOfLine = paragraphElement.getStartOffset();
        int endingOffsetOfLine = paragraphElement.getEndOffset();
        int lineNumber = 0;
        try {
            lineNumber = textAreaClicked.getLineOfOffset(startingOffsetOfLine);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return new Triple<>(lineNumber, startingOffsetOfLine, endingOffsetOfLine);
    }

    // Method to toggle a breakpoint on a specific line

    private void toggleBreakpointAtLine(int line) {

        if (breakpoints.contains(line)) {

            int index = breakpoints.indexOf(line);
            breakpoints.remove(index);
            programCounterBreakpoints.remove(index);

        } else {

            int programCounterValue = get_program_counter_value(line);
            breakpoints.add(line);
            programCounterBreakpoints.add(programCounterValue);

        }
        System.out.println(breakpoints + "Are the breakpoints currenrly");
        System.out.println(programCounterBreakpoints + "Are the program counter where breakpoints are set");
    }

    public void remove_breakpoint(int index) {
        programCounterBreakpoints.remove(index);
        breakpoints.remove(index);
    }

    public int get_program_counter_value(int line) {

        // find pc value using regex
        try {
            int startOffset = breakpointDisplayObject.first_display.getLineStartOffset(line);
            int endOffset = breakpointDisplayObject.first_display.getLineEndOffset(line);

            Pattern pattern = Pattern.compile("([0-9]+).*");
            Matcher matcher = pattern
                    .matcher(breakpointDisplayObject.first_display.getText(startOffset, endOffset - startOffset));
            matcher.find();
            return Integer.parseInt(matcher.group(1));

        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }

    }
    // Once the JTextArea is updated, the highlighters get removed
    // So, after every instruction is executed, it is necessary to set up breakpoint
    // displays
    // on appropriate lines
    // This method is executed in the guiController.java file after every
    // `machineStepper().step() is called

    public void addHighlightsBack() {
        JTextArea[] text_area_array = { breakpointDisplayObject.first_display, breakpointDisplayObject.second_display };
        try {
            for (int line : breakpoints) {

                for (JTextArea textArea : text_area_array) {
                    Highlighter highlighter = textArea.getHighlighter();
                    Object text_area_tag = memoryDisplayHighlightTags.get(line);
                    breakpointDisplayObject.removeHighlightFromDisplay(highlighter, text_area_tag);
                    breakpointDisplayObject.addHighlight(textArea, line);
                }
            }

        } catch (Exception e) {
        }
    }

    private void addHighlightToLine(int line) {
        try {
            // Determine which map of highlight tags to use based on the JTextArea
            // If the JTextArea is memory_display_two, use memoryDisplayHighlightTags
            // If the JTextArea is input_code_area, use inputCodeAreaHighlightTags
            Object existingTag1 = memoryDisplayHighlightTags.get(line);
            Object existingTag2 = inputCodeAreaHighlightTags.get(line);
            // Check if a highlight already exists for this line
            // Only proceed if there is no existing highlight for this line
            if (existingTag1 == null && existingTag2 == null) {
                // No existing highlight, so add a new one
                // Determine the start and end offsets for the line in the JTextArea
                Object tag1 = breakpointDisplayObject.addHighlight(breakpointDisplayObject.second_display, line);
                Object tag2 = breakpointDisplayObject.addHighlight(breakpointDisplayObject.first_display, line);

                // Store the highlight tag in the appropriate map
                memoryDisplayHighlightTags.put(line, tag1);
                inputCodeAreaHighlightTags.put(line, tag2);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void removeHighlightFromLine(int line) {

        Highlighter highlighter1 = breakpointDisplayObject.second_display.getHighlighter();
        Highlighter highlighter2 = breakpointDisplayObject.first_display.getHighlighter();
        // Determine which highlight tag map to use based on the JTextArea
        // If the JTextArea is memory_display_two, use memoryDisplayHighlightTags
        // If the JTextArea is input_code_area, use inputCodeAreaHighlightTags
        Object tag1 = memoryDisplayHighlightTags.get(line);
        Object tag2 = inputCodeAreaHighlightTags.get(line);

        if (tag1 != null) {
            // If a tag is found, remove the highlight associated with this tag
            // from the JTextArea using the Highlighter
            breakpointDisplayObject.removeHighlightsFromDisplay(highlighter1, highlighter2, tag1, tag2);
            // Also remove the tag from the appropriate highlight tag map
            memoryDisplayHighlightTags.remove(line);
            inputCodeAreaHighlightTags.remove(line);
        } else {
            System.out.println("No highlight tag found for line: " + line);
        }
    }

    private void synchronizeHighlights(int line) {
        // Check if the specified line is contained within the breakpoints set
        if (breakpoints.contains(line)) {
            // If the line is a breakpoint, add a highlight to that line
            // in both JTextAreas (memory_display_two and input_code_area)
            addHighlightToLine(line);
        } else {
            // If the line is not a breakpoint (i.e., if the breakpoint has been removed),
            // then remove the highlight from that line in both JTextAreas

            removeHighlightFromLine(line);
        }
    }

    public void processBreakpoints(int address) {
        // Process the breakpoints as needed,
        int index = programCounterBreakpoints.indexOf(address);
        // remove breakpoint
        remove_breakpoint(index);
        // update event display
        DebugFrame.event_display.append("Breakpoint hit at address " + address + "\n");

    }

    public void clearAllBreakpoints() {

        for (int line : breakpoints) {
            removeHighlightFromLine(line);
        }

        breakpoints.clear();
        programCounterBreakpoints.clear();

        memoryDisplayHighlightTags.clear();
        inputCodeAreaHighlightTags.clear();

    }

    public void reset_variables() {

        breakpoints.clear();
        programCounterBreakpoints.clear();

        memoryDisplayHighlightTags.clear();
        inputCodeAreaHighlightTags.clear();

        Highlighter highlighter1 = breakpointDisplayObject.first_display.getHighlighter();
        highlighter1.removeAllHighlights();

        Highlighter highlighter2 = breakpointDisplayObject.second_display.getHighlighter();
        highlighter2.removeAllHighlights();

        breakpointDisplayObject.first_display.removeMouseListener(mouseAdapterObjectInputCode);
        breakpointDisplayObject.second_display.removeMouseListener(mouseAdapterObjectMemory);
    }
}
