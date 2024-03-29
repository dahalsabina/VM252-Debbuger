package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.Highlighter.HighlightPainter;

import vm252architecturespecifications.VM252ArchitectureSpecifications;
import vm252architecturespecifications.VM252ArchitectureSpecifications.Instruction;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.text.Element;
import javax.swing.*;

import vm252simulation.VM252Model;
import vm252simulation.VM252Stepper;
import vm252simulation.VM252View;
import vm252utilities.VM252Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

/**
 * 
 * @author : Abigail Wood, Sabina Dahal, and Supreme Paudel
 */

class breakpointDisplay {

    public JTextArea first_display;
    public JTextArea second_display;

    public breakpointDisplay(JTextArea inputTextArea, JTextArea memoryDisplay) {
        first_display = inputTextArea;
        second_display = memoryDisplay;
    }

    public Object addHighlight(JTextArea textArea, int line) throws BadLocationException {

        int startOffset = textArea.getLineStartOffset(line);
        int endOffset = textArea.getLineEndOffset(line);
        HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
        Highlighter highlighter = textArea.getHighlighter();
        Object tag = highlighter.addHighlight(startOffset, endOffset, painter);
        return tag;
    }

    public void removeHighlightsFromDisplay(Highlighter highlighter1, Highlighter highlighter2, Object tag1,
            Object tag2) {

        highlighter1.removeHighlight(tag1);
        highlighter2.removeHighlight(tag2);

    }

    public void removeHighlightFromDisplay(Highlighter highlighter, Object tag) {

        highlighter.removeHighlight(tag);

    }

}

class code_display {

    // ctor
    public VM252Stepper myStepper;
    public String typeOfDisplay = "bytes";

    public code_display(VM252Stepper stepper) {
        myStepper = stepper;
    }

    public void display_entire_memory() {
        // type can be as machine memory as bytes in hex
        // or 2byte data in hex
        // type will be either "two-byte" or "bytes"

        DebugFrame.memory_display_one.setText("");
        for (int address = 0; address < VM252ArchitectureSpecifications.MEMORY_SIZE_IN_BYTES; address++) {

            byte data = DebugFrame.memoryBytePrinterObject.get_data(address);
            if (address % 20 == 0) {
                if (typeOfDisplay == "bytes") {
                    DebugFrame.memory_display_one.append(String.format("[Addr %d] %02x", address, data));
                } else
                    DebugFrame.memory_display_one.append(String.format("[Addr %d] %02x", address, data));
            } else if (address % 20 == 19) {
                if (typeOfDisplay == "bytes") {
                    DebugFrame.memory_display_one.append(String.format(" %02x\n", data));
                } else
                    DebugFrame.memory_display_one.append(String.format("%02x\n", data));
            } else {
                if (typeOfDisplay == "bytes" || address % 2 == 0) {
                    DebugFrame.memory_display_one.append(String.format(" %02x", data));
                } else
                    DebugFrame.memory_display_one.append(String.format("%02x", data));
            }
        }

        DebugFrame.memory_display_one.setCaretPosition(0);
    }

    public void display_code_in_memory_bytes_format() {

        DebugFrame.memory_display_two.setText("");
        int programCounter = 0;
        int instruction_length_in_bytes = myStepper.get_instruction_bytes_length(programCounter);

        while (programCounter < VM252Utilities.byteContentMapSize) {

            if (instruction_length_in_bytes == 1) {

                byte data = DebugFrame.memoryBytePrinterObject.get_data(programCounter);
                DebugFrame.memory_display_two.append(String.format("[Addr %d] %02x\n", programCounter, data));

            } else {

                byte data1 = DebugFrame.memoryBytePrinterObject.get_data(programCounter);
                byte data2 = DebugFrame.memoryBytePrinterObject.get_data(programCounter + 1);
                DebugFrame.memory_display_two
                        .append(String.format("[Addr %d] %02x %02x\n", programCounter, data1, data2));

            }

            programCounter = VM252ArchitectureSpecifications.nextMemoryAddress(programCounter,
                    instruction_length_in_bytes);
            instruction_length_in_bytes = myStepper.get_instruction_bytes_length(programCounter);
        }
    }

    public void display_code_in_human_readable_format() {

        // The idea for displaying code in human-readable format is to fetch memory
        // bytes one after another,
        // and converting them to instructions

        // If the instruction only consists of just the symbolic opcode such as STOP,
        // INPUT, (instructions with a single word),
        // we will just display their symbolic code

        // If the memory address we are fetching is object code that holds data, then we
        // display what the variable name is and the data the address is storing
        // which consists of 2 bytes of data
        // The variable name is found with the help of
        // VM252Utilities.addressSymbolHashMapwhich is a hash map with
        // address(PC) values as hash keys, and
        // the variable name designated for that address as the values

        // If the memory address we are fetching holds executable code but belongs to
        // the VM252Utilities.addressSymbolHashMapwhich array
        // , this means this is of the type (variableName : INSTRUCTION) e.g main: INPUT
        // These are just instructions that are involved in jump statements/ while loops
        // used to jump the PC to a certain PC value
        // (example JUMP main) will change(jump) to wherever main is( wherever the
        // memory address of main is)

        display_code_in_memory_bytes_format();
        display_entire_memory();
        DebugFrame.input_code_area.setText("");

        int programCounter = 0;
        String instruction = myStepper.next_instruction(programCounter);
        int instruction_length_in_bytes = myStepper.get_instruction_bytes_length(programCounter);

        while (programCounter < VM252Utilities.byteContentMapSize) {

            String tmp_instruction;

            // this will be code of the type variableName : someNumber (e.g value : 5)
            if (VM252Utilities.addressSymbolHashMap.get(programCounter) != null
                    && VM252Utilities.addressesWhichHoldsObjectCodeData.contains(programCounter)) {

                byte[] dataBytes = myStepper.fetchMemoryBytes(programCounter, 2);
                int data = ((short) (dataBytes[0] << 8 | dataBytes[1] & 0xff));
                tmp_instruction = VM252Utilities.addressSymbolHashMap.get(programCounter) + ": " + data;

                // this will be code of the type variableName : instruction (e.g main : input)
            } else if (VM252Utilities.addressSymbolHashMap.get(programCounter) != null) {
                tmp_instruction = VM252Utilities.addressSymbolHashMap.get(programCounter) + ": " + instruction;

                // this will be code of the type instruction (e.g STOP, JUMP main)
            } else {
                tmp_instruction = instruction;
            }

            DebugFrame.input_code_area.append(programCounter + "    " + tmp_instruction + "\n");

            programCounter = VM252ArchitectureSpecifications.nextMemoryAddress(programCounter,
                    instruction_length_in_bytes);

            instruction_length_in_bytes = myStepper.get_instruction_bytes_length(programCounter);
            instruction = myStepper.next_instruction(programCounter);

        }
    }
}

// Class to alter the memory address of a VM252Model
class alter_memory_address {

    private final VM252Model myModel;

    public alter_memory_address(VM252Model model) {

        myModel = model;

    }

    public void updateMemoryByte() {
        // Prompt for memory address input from user
        int memoryAddress;
        byte value;

        JTextField memory_address = new JTextField();
        JTextField hexadecimal_value = new JTextField();
        Object[] message = {
                "Enter memory address", memory_address,
                "New hexadecimal value", hexadecimal_value
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Alter memory", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                // Attempt to parse the input string to an integer
                memoryAddress = Integer.parseInt(memory_address.getText());

                // Attempt to parse the input string to a byte, considering it as a hexadecimal
                value = (byte) Integer.parseInt(hexadecimal_value.getText(), 16);

                // Attempt to update the memory byte at the given address with the new value
                myModel.setMemoryByte(memoryAddress, value);

                // reupdate the gui with latest instructions and memory values
                DebugFrame.code_display_object.display_code_in_human_readable_format();
                DebugFrame.lineHighlightPrinterObject.updateHighlighter();
                DebugFrame.breakpointHandlerObject.addHighlightsBack();

                // update event display
                DebugFrame.event_display.append(String.format("memory byte at address %d is now %02x\n", memoryAddress,
                        myModel.memoryByte(memoryAddress)));

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ERROR : Number formatting");

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, "ERROR : Illegal Argument");

            }
        } else {
            JOptionPane.showMessageDialog(null, "No changes made");
        }

    }
}

class lineHighlightPrinter {

    private final VM252Model myModel;
    private final JTextArea myTextArea1;
    private final JTextArea myTextArea2;
    private int currentLine1;
    private int currentLine2;
    private Object currentHighlighter1;
    private Object currentHighlighter2;

    public int getCurrentLine(int pc_value, JTextArea textArea) {
        int line_number = 0;
        String[] list_of_lines = textArea.getText().split("\n");
        for (String line : list_of_lines) {
            if (textArea.equals(myTextArea1) && line.startsWith("" + pc_value)) {
                break;
            } else if (textArea.equals(myTextArea2) && line.startsWith("[Addr " + pc_value)) {
                break;
            }
            line_number++;
        }
        return line_number;
    }

    public void updateHighlighter() {

        try {
            int pc = myModel.programCounter();
            currentLine1 = getCurrentLine(pc, myTextArea1);
            currentLine2 = getCurrentLine(pc, myTextArea2);
            Highlighter high1 = myTextArea1.getHighlighter();
            Highlighter high2 = myTextArea2.getHighlighter();

            try {
                high1.removeHighlight(currentHighlighter1);
                high2.removeHighlight(currentHighlighter2);
            } catch (NullPointerException e) {
                System.out.println(e);
            }

            int start1 = myTextArea1.getLineStartOffset(currentLine1);
            int end1 = myTextArea1.getLineEndOffset(currentLine1);

            int start2 = myTextArea2.getLineStartOffset(currentLine2);
            int end2 = myTextArea2.getLineEndOffset(currentLine2);

            currentHighlighter1 = high1.addHighlight(start1, end1,
                    new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
            currentHighlighter2 = high2.addHighlight(start2, end2,
                    new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
        }

        catch (BadLocationException e) {
        }
    }

    public lineHighlightPrinter(VM252Model model, JTextArea textArea1, JTextArea textArea2) {
        myModel = model;
        myTextArea1 = textArea1;
        myTextArea2 = textArea2;
        currentLine1 = 1;
        currentLine2 = 1;
    }

}

class accumulatorPrinter extends VM252View {

    private final VM252Model myModel;

    public accumulatorPrinter(VM252Model model) {
        myModel = model;
    }

    @Override
    public void updateAccumulator() {
        // Update the gui value
        DebugFrame.accumulator_display.setText("" + myModel.accumulator());
    }

    public void setAccumulator(int value) {

        myModel.setAccumulator(value);

    }
}

class ProgramCounterPrinter extends VM252View {

    private final VM252Model myModel;

    public ProgramCounterPrinter(VM252Model model) {
        myModel = model;
    }

    @Override
    public void updateProgramCounter() {
        DebugFrame.count_diplay.setText("" + myModel.programCounter());
        DebugFrame.lineHighlightPrinterObject.updateHighlighter();
    }

    public void setProgramCounter(int value) {

        myModel.setProgramCounter(value);

    }

}

class MemoryBytePrinter extends VM252View {

    private final VM252Model myModel;

    public MemoryBytePrinter(VM252Model model) {
        myModel = model;
    }

    @Override
    public void updateMemory(int address) {

        System.out.printf("memory byte at address %d is now %02x\n", address, myModel.memoryByte(address));
    }

    public byte get_data(int address) {
        return myModel.memoryByte(address);
    }

}

class StopAnnouncer extends VM252View {

    private final VM252Model myModel;

    public StopAnnouncer(VM252Model model) {
        myModel = model;
    }

    @Override
    public void updateStoppedStatus() {
        // UPDATE GUI AFTER PROGRAM HAS ENDED
        if (DebugFrame.button_clicked == DebugFrame.Pause) {

            String output_line_1 = String.format("machine paused | ACC : %d | PC %d\n", myModel.accumulator(),
                    myModel.programCounter());
            DebugFrame.event_display.setText(DebugFrame.event_display.getText() + output_line_1);
            return;

        } else if (DebugFrame.button_clicked == DebugFrame.Stop) {
            machine_stopped_midway();
            return;

        } else if (DebugFrame.simulatedMachine.stoppedStatus() == VM252Model.StoppedCategory.stopped) {
            DebugFrame.reset_gui_components(false);
            System.out.println("program ended");
            String output_line_1 = String.format("machine stops | ACC : %d | PC : %d\n", myModel.accumulator(),
                    myModel.programCounter());
            DebugFrame.event_display.setText(DebugFrame.event_display.getText() + output_line_1 + "\n");
        } else if (DebugFrame.simulatedMachine.stoppedStatus() == VM252Model.StoppedCategory.notStopped) {
        }
    }

    public void machine_stopped_midway() {
        String output_line_1 = String.format("machine stops | ACC : %d | PC : %d\n", myModel.accumulator(),
                myModel.programCounter());
        DebugFrame.event_display.setText(DebugFrame.event_display.getText() + output_line_1);

    }
}

public class DebugFrame extends javax.swing.JFrame {

    final JFileChooser fileChooser = new JFileChooser();
    String objFileName = "";
    static guiController simulator;
    static accumulatorPrinter accumulatorPrinterObject;
    static code_display code_display_object;
    static lineHighlightPrinter lineHighlightPrinterObject;
    static breakpointHandler breakpointHandlerObject;
    static breakpointDisplay breakpointDisplayObject;
    static ProgramCounterPrinter programCounterPrinterObject;
    static StopAnnouncer stopAnnouncerObject;
    static MemoryBytePrinter memoryBytePrinterObject;
    static String instruction_to_be_executed;
    static JButton button_clicked;
    public static VM252Model simulatedMachine;
    private alter_memory_address alterMemoryAddressObject;

    public static Double getRunSpeedFromSpeedComponent() {

        String line = (String) DebugFrame.adjust_Speed.getSelectedItem();
        if (line == "Speed ∞") {
            return (double) 2;
        } else {
            Pattern pattern = Pattern.compile("x([102].[0-9]{1,2}).*");
            Matcher matcher = pattern.matcher(line);
            matcher.find();
            return Double.parseDouble(matcher.group(1));
        }
    }

    public static void reset_gui_components(boolean enable) {
        Start.setEnabled(enable);
        next_Line.setEnabled(enable);
        Stop.setEnabled(enable);
        Pause.setEnabled(enable);

        if (enable) {
            DebugFrame.output_text.setText("");
            DebugFrame.event_display.setText("");
        }

        accumulator_display.setEditable(enable);
        count_diplay.setEditable(enable);
    }

    /**
     * Creates new form DebugFrame
     */
    public DebugFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox3 = new javax.swing.JComboBox<>();
        Upper_Panel = new javax.swing.JPanel();
        selectFile = new javax.swing.JButton();
        file_Selected = new javax.swing.JTextField();
        JButton Help = new JButton(new ImageIcon(
                Paths.get(System.getProperty("user.dir"), "Packages/gui/images/helpicon.png").toString()));
        Help.setBounds(100, 100, 100, 50);
        Button_Panel = new javax.swing.JPanel();
        Start = new javax.swing.JButton(new ImageIcon(
                Paths.get(System.getProperty("user.dir"), "Packages/gui/images/starticon.png").toString()));
        Start.setBounds(100, 100, 100, 50);
        Pause = new javax.swing.JButton(new ImageIcon(
                Paths.get(System.getProperty("user.dir"), "Packages/gui/images/pauseicon.png").toString()));
        Pause.setBounds(100, 100, 100, 50);
        next_Line = new javax.swing.JButton(new ImageIcon(
                Paths.get(System.getProperty("user.dir"), "Packages/gui/images/nexticon.png").toString()));
        next_Line.setBounds(100, 100, 100, 100);
        executeAgain = new javax.swing.JButton(new ImageIcon(
                Paths.get(System.getProperty("user.dir"), "Packages/gui/images/reseticon.png").toString()));
        executeAgain.setBounds(100, 100, 100, 50);
        Stop = new javax.swing.JButton(new ImageIcon(
                Paths.get(System.getProperty("user.dir"), "Packages/gui/images/stopicon.png").toString()));
        ArrayList<JButton> buttonlist = new ArrayList<>(
                Arrays.asList(Help, Start, Pause, next_Line, executeAgain, Stop));

        for (JButton button : buttonlist) {
            button.setBounds(100, 100, 100, 50);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
        }

        Stop.setOpaque(false);
        adjust_Speed = new javax.swing.JComboBox<>();
        Middle_Panel = new javax.swing.JPanel();
        Middle_West = new javax.swing.JPanel();
        Program_Counter = new javax.swing.JLabel();
        count_diplay = new javax.swing.JTextField();
        Middle_Center = new javax.swing.JPanel();
        accumulator = new javax.swing.JLabel();
        accumulator_display = new javax.swing.JTextField();
        Middle_East = new javax.swing.JPanel();
        next_Instruction = new javax.swing.JLabel();
        instruction_Display = new javax.swing.JTextField();
        break_Clear = new javax.swing.JButton();
        edit_Memorybyte = new javax.swing.JButton();
        Bottom_West = new javax.swing.JPanel();
        input_code_scroll = new javax.swing.JScrollPane();
        input_code_area = new javax.swing.JTextArea();
        Bottom_East = new javax.swing.JPanel();
        Top_Bottom_East = new javax.swing.JPanel();
        input_scroll = new javax.swing.JScrollPane();
        output_text = new javax.swing.JTextArea();
        output_scroll = new javax.swing.JScrollPane();
        event_display = new javax.swing.JTextArea();
        Output_Value = new javax.swing.JLabel();
        event_value = new javax.swing.JLabel();
        Center_Bottom_East = new javax.swing.JPanel();
        memory_options_one = new javax.swing.JComboBox<>();
        memory_display_scroll_one = new javax.swing.JScrollPane();
        memory_display_one = new javax.swing.JTextArea();
        Last_Bottom_East = new javax.swing.JPanel();
        memory_display_scroll_two = new javax.swing.JScrollPane();
        memory_display_two = new javax.swing.JTextArea();

        jComboBox3.setModel(
                new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(153, 153, 153));

        Upper_Panel.setName("Upper_Panel"); // NOI18N

        selectFile.setText("Select File");
        selectFile.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        selectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    selectFileActionPerformed(evt);
                } catch (IOException exception) {
                }
            }
        });

        file_Selected.setEditable(false);
        file_Selected.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        file_Selected.setText("No file selected");
        file_Selected.setAutoscrolls(false);
        file_Selected.setBorder(null);
        file_Selected.setFocusable(false);
        file_Selected.setOpaque(true);
        file_Selected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                file_SelectedActionPerformed(evt);
            }
        });

        Help.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HelpActionPerformed(evt);
            }
        });

        Start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartActionPerformed(evt);
            }
        });
        Start.setToolTipText("Run simulation");

        Pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PauseActionPerformed(evt);
            }
        });
        Pause.setToolTipText("Pause simulation");

        next_Line.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NextActionPerformed(evt);
            }
        });
        next_Line.setToolTipText("Execute next instruction");

        executeAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeAgainActionPerformed(evt);
            }
        });
        executeAgain.setToolTipText("Restart simulation (Discards changes made)");

        adjust_Speed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (simulator != null) {
                    Double runSpeed = getRunSpeedFromSpeedComponent();
                    simulator.setRunSpeed(runSpeed);
                }
            }
        });

        Stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });
        Stop.setToolTipText("Stop simulation");

        adjust_Speed.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Speed ∞", "Speed x1.0", "Speed x0.75", "Speed x0.5", "Speed x0.25" }));

        javax.swing.GroupLayout Button_PanelLayout = new javax.swing.GroupLayout(Button_Panel);
        Button_Panel.setLayout(Button_PanelLayout);
        Button_PanelLayout.setHorizontalGroup(
                Button_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Button_PanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Start)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Pause)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(next_Line)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(executeAgain)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Stop)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(adjust_Speed, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        Button_PanelLayout.setVerticalGroup(
                Button_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Button_PanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(Button_PanelLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(Start)
                                        .addComponent(Pause)
                                        .addComponent(next_Line)
                                        .addComponent(executeAgain)
                                        .addComponent(Stop)
                                        .addComponent(adjust_Speed, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        javax.swing.GroupLayout Upper_PanelLayout = new javax.swing.GroupLayout(Upper_Panel);
        Upper_Panel.setLayout(Upper_PanelLayout);
        Upper_PanelLayout.setHorizontalGroup(
                Upper_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                Upper_PanelLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(Button_Panel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(Upper_PanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(Upper_PanelLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(Upper_PanelLayout.createSequentialGroup()
                                                .addComponent(selectFile, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        98,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(Help))
                                        .addGroup(Upper_PanelLayout.createSequentialGroup()
                                                .addComponent(file_Selected, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap()));
        Upper_PanelLayout.setVerticalGroup(
                Upper_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Upper_PanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(Upper_PanelLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(selectFile, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(Help))
                                .addGap(0, 0, 0)
                                .addComponent(file_Selected, javax.swing.GroupLayout.PREFERRED_SIZE, 16,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18,
                                        Short.MAX_VALUE)
                                .addComponent(Button_Panel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()));

        file_Selected.getAccessibleContext().setAccessibleParent(selectFile);

        Middle_West.setBackground(new java.awt.Color(153, 153, 153));
        Middle_West.setToolTipText("");
        Middle_West.setPreferredSize(new java.awt.Dimension(168, 102));

        Program_Counter.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Program_Counter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Program_Counter.setText("Count");

        count_diplay.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        count_diplay.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        count_diplay.setText("0");
        count_diplay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                count_diplayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Middle_WestLayout = new javax.swing.GroupLayout(Middle_West);
        Middle_West.setLayout(Middle_WestLayout);
        Middle_WestLayout.setHorizontalGroup(
                Middle_WestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Middle_WestLayout.createSequentialGroup()
                                .addContainerGap(52, Short.MAX_VALUE)
                                .addGroup(Middle_WestLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(Program_Counter, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(count_diplay))
                                .addContainerGap(52, Short.MAX_VALUE)));
        Middle_WestLayout.setVerticalGroup(
                Middle_WestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Middle_WestLayout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(Program_Counter, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(count_diplay, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        Middle_Center.setBackground(new java.awt.Color(153, 153, 153));
        Middle_Center.setPreferredSize(new java.awt.Dimension(168, 102));

        accumulator.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        accumulator.setText("ACC");

        accumulator_display.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        accumulator_display.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        accumulator_display.setText("0");
        accumulator_display.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AccumulatorChangeActionPerfomed(evt);
            }
        });

        javax.swing.GroupLayout Middle_CenterLayout = new javax.swing.GroupLayout(Middle_Center);
        Middle_Center.setLayout(Middle_CenterLayout);
        Middle_CenterLayout.setHorizontalGroup(
                Middle_CenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Middle_CenterLayout.createSequentialGroup()
                                .addGroup(Middle_CenterLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(Middle_CenterLayout.createSequentialGroup()
                                                .addGap(49, 49, 49)
                                                .addComponent(accumulator_display,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 71,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(Middle_CenterLayout.createSequentialGroup()
                                                .addGap(65, 65, 65)
                                                .addComponent(accumulator, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        37,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(48, Short.MAX_VALUE)));
        Middle_CenterLayout.setVerticalGroup(
                Middle_CenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Middle_CenterLayout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(accumulator)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(accumulator_display, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        Middle_East.setBackground(new java.awt.Color(153, 153, 153));

        next_Instruction.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        next_Instruction.setText("Next Instruction");

        instruction_Display.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        instruction_Display.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        instruction_Display.setEditable(false);
        instruction_Display.setText("");

        javax.swing.GroupLayout Middle_EastLayout = new javax.swing.GroupLayout(Middle_East);
        Middle_East.setLayout(Middle_EastLayout);
        Middle_EastLayout.setHorizontalGroup(
                Middle_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                Middle_EastLayout.createSequentialGroup()
                                        .addContainerGap(20, Short.MAX_VALUE)
                                        .addGroup(Middle_EastLayout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(instruction_Display,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 124,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(next_Instruction))
                                        .addGap(18, 18, 18)));
        Middle_EastLayout.setVerticalGroup(
                Middle_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Middle_EastLayout.createSequentialGroup()
                                .addContainerGap(24, Short.MAX_VALUE)
                                .addComponent(next_Instruction)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(instruction_Display, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)));

        break_Clear.setText("Clear Breakpoints");
        break_Clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                break_ClearActionPerformed(evt);
            }
        });

        edit_Memorybyte.setText("Edit Memory");
        edit_Memorybyte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_MemorybyteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Middle_PanelLayout = new javax.swing.GroupLayout(Middle_Panel);
        Middle_Panel.setLayout(Middle_PanelLayout);
        Middle_PanelLayout.setHorizontalGroup(
                Middle_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Middle_PanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(break_Clear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(edit_Memorybyte)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90,
                                        Short.MAX_VALUE)
                                .addComponent(Middle_Center, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(100, 100, 100)
                                .addComponent(Middle_West, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(100, 100, 100)
                                .addComponent(Middle_East, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(253, Short.MAX_VALUE)));
        Middle_PanelLayout.setVerticalGroup(
                Middle_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Middle_West, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                        .addComponent(Middle_East, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Middle_Center, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                Middle_PanelLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(Middle_PanelLayout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(break_Clear)
                                                .addComponent(edit_Memorybyte))));

        input_code_area.setColumns(20);
        input_code_area.setRows(5);
        input_code_area.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        input_code_scroll.setViewportView(input_code_area);
        input_code_area.getAccessibleContext().setAccessibleParent(Bottom_West);

        memory_display_two.setEditable(false);
        memory_display_two.setColumns(20);
        memory_display_two.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        memory_display_two.setRows(5);
        memory_display_scroll_two.setViewportView(memory_display_two);

        javax.swing.GroupLayout Bottom_WestLayout = new javax.swing.GroupLayout(Bottom_West);
        Bottom_West.setLayout(Bottom_WestLayout);
        Bottom_WestLayout.setHorizontalGroup(
                Bottom_WestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Bottom_WestLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(input_code_scroll)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        Bottom_WestLayout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(Bottom_WestLayout.createSequentialGroup()
                                                        .addComponent(memory_display_scroll_two)))));
        Bottom_WestLayout.setVerticalGroup(
                Bottom_WestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Bottom_WestLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(memory_display_scroll_two))
                        .addComponent(input_code_scroll));

        Bottom_East.setBackground(new java.awt.Color(153, 153, 153));

        Top_Bottom_East.setBackground(new java.awt.Color(153, 153, 153));

        input_scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        output_text.setColumns(20);
        output_text.setRows(5);
        input_scroll.setViewportView(output_text);
        output_text.getAccessibleContext().setAccessibleParent(Bottom_West);

        output_scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        event_display.setColumns(20);
        event_display.setEditable(false);
        event_display.setRows(5);
        output_scroll.setViewportView(event_display);

        Output_Value.setBackground(new java.awt.Color(153, 153, 153));
        Output_Value.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Output_Value.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Output_Value.setText("Output");
        Output_Value.setOpaque(true);

        event_value.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        event_value.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        event_value.setText("Events");

        javax.swing.GroupLayout Top_Bottom_EastLayout = new javax.swing.GroupLayout(Top_Bottom_East);
        Top_Bottom_East.setLayout(Top_Bottom_EastLayout);
        Top_Bottom_EastLayout.setHorizontalGroup(
                Top_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                Top_Bottom_EastLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(Top_Bottom_EastLayout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(input_scroll)
                                                .addComponent(Output_Value, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(0, 0, 0)
                                        .addGroup(Top_Bottom_EastLayout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                        false)
                                                .addComponent(output_scroll)
                                                .addComponent(event_value, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(0, 0, 0)));
        Top_Bottom_EastLayout.setVerticalGroup(
                Top_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Top_Bottom_EastLayout.createSequentialGroup()
                                .addGroup(Top_Bottom_EastLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(Output_Value)
                                        .addGroup(Top_Bottom_EastLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(event_value)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(Top_Bottom_EastLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(input_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 69,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Top_Bottom_EastLayout
                                                .createSequentialGroup()
                                                .addComponent(output_scroll, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        69,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap()))));

        Center_Bottom_East.setBackground(new java.awt.Color(153, 153, 153));

        memory_options_one.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Machine memory as bytes in Hex", "Machine memory as 2 byte data in Hex", "Edit" }));
        memory_options_one.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memory_options_oneActionPerformed(evt);
            }
        });

        memory_display_one.setEditable(false);
        memory_display_one.setColumns(20);
        memory_display_one.setRows(5);
        memory_display_scroll_one.setViewportView(memory_display_one);

        javax.swing.GroupLayout Center_Bottom_EastLayout = new javax.swing.GroupLayout(Center_Bottom_East);
        Center_Bottom_East.setLayout(Center_Bottom_EastLayout);
        Center_Bottom_EastLayout.setHorizontalGroup(
                Center_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Center_Bottom_EastLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(Center_Bottom_EastLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(memory_options_one, 0, 397, Short.MAX_VALUE)
                                        .addComponent(memory_display_scroll_one))
                                .addContainerGap()));
        Center_Bottom_EastLayout.setVerticalGroup(
                Center_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Center_Bottom_EastLayout.createSequentialGroup()
                                .addComponent(memory_options_one, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(memory_display_scroll_one, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        393,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()));

        javax.swing.GroupLayout Bottom_EastLayout = new javax.swing.GroupLayout(Bottom_East);
        Bottom_East.setLayout(Bottom_EastLayout);
        Bottom_EastLayout.setHorizontalGroup(
                Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Bottom_EastLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addGroup(Bottom_EastLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(Bottom_EastLayout.createSequentialGroup()
                                                .addComponent(Top_Bottom_East, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addComponent(Center_Bottom_East, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
        Bottom_EastLayout.setVerticalGroup(
                Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Bottom_EastLayout.createSequentialGroup()
                                .addComponent(Top_Bottom_East, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Center_Bottom_East, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(Bottom_West, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Bottom_East, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Upper_Panel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addComponent(Middle_Panel, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(Upper_Panel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Middle_Panel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(Bottom_West, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Bottom_East, javax.swing.GroupLayout.PREFERRED_SIZE, 537,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void file_SelectedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_file_SelectedActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_file_SelectedActionPerformed

    private void memory_options_oneActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_memory_options_oneActionPerformed

        String line = (String) memory_options_one.getSelectedItem();
        if (line.contains("2 byte data")) {
            // 2 byte data
            code_display_object.typeOfDisplay = "two-byte";
            code_display_object.display_entire_memory();
        } else {
            // as bytes
            code_display_object.typeOfDisplay = "bytes";
            code_display_object.display_entire_memory();
        }
        // TODO add your handling code here:
    }// GEN-LAST:event_memory_options_oneActionPerformed

    private void NextActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_StartActionPerformed
        try {
            if (file_Selected.getText().equals("No file selected")) {
                JOptionPane.showMessageDialog(this, "Select a file first");
            } else {
                // TO DO what to pass here , not sure
                String input_value = accumulator_display.getText();
                Scanner scanner_object = new Scanner(input_value);
                // TO DO what to pass here , not sure
                button_clicked = next_Line;
                simulatedMachine.setStoppedStatus(VM252Model.StoppedCategory.notStopped);
                simulator.run_simulation(input_value, scanner_object, System.out, "next");
            }
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
        // TODO add your handling code here:
    }// GEN-LAST:event_StartActionPerformed

    private void PauseActionPerformed(ActionEvent evt) {
        if (file_Selected.getText().equals("No file selected")) {
            JOptionPane.showMessageDialog(this, "Select a file first");
        } else {
            button_clicked = Pause;
            simulatedMachine.setStoppedStatus(VM252Model.StoppedCategory.paused);
        }
    }

    private void StartActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_StartActionPerformed
        try {
            if (file_Selected.getText().equals("No file selected")) {
                JOptionPane.showMessageDialog(this, "Select a file first");
            } else {
                String input_value = accumulator_display.getText();
                Scanner scanner_object = new Scanner(input_value);
                button_clicked = Start;
                simulatedMachine.setStoppedStatus(VM252Model.StoppedCategory.notStopped);
                simulator.run_simulation(input_value, scanner_object, System.out, "run");

            }
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
        // TODO add your handling code here:
    }// GEN-LAST:event_StartActionPerformed

    private void HelpActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_HelpActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this,
                "Select a File: You can only input an object file\n Start: Starts the code based off of input given \n Pause: Pauses code until Start is pressed \n Next Line: After a break point stops the code, user may choose to execute the next line \n Redo: Resets the currect execution \n Stop: Halts the execution and resets Speed: Adjusts the execution speed \n Breakpoints: Double click on the instruction or memory displays to have the code execute until it performs the marked instruction ",
                "Help", JOptionPane.INFORMATION_MESSAGE);
    }// GEN-LAST:event_HelpActionPerformed

    private void AccumulatorChangeActionPerfomed(java.awt.event.ActionEvent evt) {
        String new_value = accumulator_display.getText();
        accumulatorPrinterObject.setAccumulator(Integer.parseInt(new_value));
        DebugFrame.event_display
                .setText(DebugFrame.event_display.getText() + "ACC set to " + Integer.parseInt(new_value) + "\n");
    }

    private void break_ClearActionPerformed(java.awt.event.ActionEvent evt) {
        breakpointHandlerObject.clearAllBreakpoints();
    }

    private void edit_MemorybyteActionPerformed(java.awt.event.ActionEvent evt) {

        if (file_Selected.getText().equals("No file selected")) {
            JOptionPane.showMessageDialog(this, "Select a file first");
        } else {

            alterMemoryAddressObject.updateMemoryByte();
        }
    }

    private void create_simulation_machine() throws IOException {

        simulatedMachine = new VM252Model();
        accumulatorPrinterObject = new accumulatorPrinter(simulatedMachine);
        programCounterPrinterObject = new ProgramCounterPrinter(simulatedMachine);
        stopAnnouncerObject = new StopAnnouncer(simulatedMachine);
        memoryBytePrinterObject = new MemoryBytePrinter(simulatedMachine);
        lineHighlightPrinterObject = new lineHighlightPrinter(simulatedMachine, input_code_area,
                memory_display_two);
        breakpointDisplayObject = new breakpointDisplay(input_code_area, memory_display_two);
        breakpointHandlerObject = new breakpointHandler(breakpointDisplayObject);
        alterMemoryAddressObject = new alter_memory_address(simulatedMachine);

        simulatedMachine.attach(accumulatorPrinterObject);
        simulatedMachine.attach(programCounterPrinterObject);
        simulatedMachine.attach(memoryBytePrinterObject);
        simulatedMachine.attach(stopAnnouncerObject);

        if (simulator != null)
            simulator.stop_timer();

        simulator = new guiController(simulatedMachine, lineHighlightPrinterObject, breakpointHandlerObject);
        simulator.loadFile(objFileName, new Scanner(System.in), System.out);
        code_display_object = new code_display(simulator.machineStepper());

        simulator.setCodeDisplayObject(code_display_object);
        code_display_object.display_code_in_human_readable_format();
        lineHighlightPrinterObject.updateHighlighter();
        // also put the first instruction
        instruction_Display.setText(simulator.machineStepper().next_instruction(0));
    }

    private void selectFileActionPerformed(java.awt.event.ActionEvent evt) throws IOException {// GEN-FIRST:event_selectFileActionPerformed
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(fileChooser);
        if (result == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fileChooser.getSelectedFile();
            String selectedFileName = selectedFile.getName();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            if (selectedFileName.endsWith(".vm252obj")) {
                file_Selected.setText(selectedFile.getName());
                objFileName = selectedFile.getAbsolutePath();
                accumulator_display.setText("0");
                count_diplay.setText("0");
                create_simulation_machine();
                reset_gui_components(true);
            } else {
                // System.out.print("Invalid file");
                JOptionPane.showMessageDialog(this, "File must end with .vm252obj");
                objFileName = "";
            }
        } else {
            file_Selected.setText("No file selected");
        }

    }

    private void executeAgainActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_executeAgainActionPerformed
        count_diplay.setText("0");
        accumulator_display.setText("0");
        instruction_Display.setText("");
        input_code_area.setText(" ");
        memory_display_two.setText(" ");
        memory_display_one.setText(" ");

        breakpointHandlerObject.reset_variables();
        // remove all highlight tags

        try {
            create_simulation_machine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        reset_gui_components(true);

    }// GEN-LAST:event_executeAgainActionPerformed

    private void stopActionPerformed(ActionEvent evt) {

        button_clicked = Stop;
        reset_gui_components(false);
        simulatedMachine.setStoppedStatus(VM252Model.StoppedCategory.stopped);
    }

    private void count_diplayActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_count_diplayActionPerformed
        // TODO add your handling code here:
        String new_value = count_diplay.getText();
        programCounterPrinterObject.setProgramCounter(Integer.parseInt(new_value));
        DebugFrame.event_display
                .setText(DebugFrame.event_display.getText() + "PC set to " + Integer.parseInt(new_value) + "\n");

    }// GEN-LAST:event_count_diplayActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DebugFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DebugFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DebugFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DebugFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DebugFrame().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Bottom_East;
    private javax.swing.JPanel Bottom_West;
    private javax.swing.JPanel Button_Panel;
    private javax.swing.JPanel Center_Bottom_East;
    private javax.swing.JButton Help;
    private javax.swing.JLabel Output_Value;
    private javax.swing.JPanel Last_Bottom_East;
    private javax.swing.JPanel Middle_Center;
    private javax.swing.JPanel Middle_East;
    private javax.swing.JPanel Middle_Panel;
    private javax.swing.JPanel Middle_West;
    private javax.swing.JLabel event_value;
    static javax.swing.JButton Pause;
    private javax.swing.JLabel Program_Counter;
    private static javax.swing.JButton Start;
    static javax.swing.JButton Stop;
    private javax.swing.JPanel Top_Bottom_East;
    private javax.swing.JPanel Upper_Panel;
    private javax.swing.JLabel accumulator;
    public static javax.swing.JTextField accumulator_display;
    private static javax.swing.JComboBox<String> adjust_Speed;
    private javax.swing.JButton break_Clear;
    public static javax.swing.JTextField count_diplay;
    private javax.swing.JButton edit_Memorybyte;
    public static javax.swing.JTextArea output_text;
    private javax.swing.JButton executeAgain;
    private javax.swing.JTextField file_Selected;
    public static javax.swing.JTextArea input_code_area;
    private javax.swing.JScrollPane input_code_scroll;
    private javax.swing.JScrollPane input_scroll;
    public static javax.swing.JTextField instruction_Display;
    private javax.swing.JComboBox<String> jComboBox3;
    public static javax.swing.JTextArea memory_display_one;
    public static javax.swing.JScrollPane memory_display_scroll_one;
    public static javax.swing.JScrollPane memory_display_scroll_two;
    public static javax.swing.JTextArea memory_display_two;
    public static javax.swing.JComboBox<String> memory_options_one;
    private javax.swing.JLabel next_Instruction;
    private static javax.swing.JButton next_Line;
    public static javax.swing.JTextArea event_display;
    private javax.swing.JScrollPane output_scroll;
    private javax.swing.JButton selectFile;
    // End of variables declaration//GEN-END:variables

    private Scanner Scanner(String input_value) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static void update_event_display() {
        String output_line_1 = String.format("INPUT %d given | ACC : %d | PC : %d", simulatedMachine.accumulator(),
                simulatedMachine.accumulator(), simulatedMachine.programCounter());
        DebugFrame.event_display.setText(DebugFrame.event_display.getText() + output_line_1 + "\n");

    }

}

class Triple<T, U, V> {
    public final T first;
    public final U second;
    public final V third;

    public Triple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}