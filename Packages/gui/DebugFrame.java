package gui;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import vm252simulation.VM252Model;
import vm252simulation.VM252View;

import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * 
 * @author : Abigail Wood, Sabina Dahal, and Supreme Paudel
 */

class lineHighlightPrinter{

    private final VM252Model myModel;
    private final JTextArea myTextArea;
    private int currentLine;
    private Object currentHighlighter;

    public int getCurrentLine(int pc_value){
        int line_number = 0;
        String [] list_of_lines =myTextArea.getText().split("\n");
        for (String line: list_of_lines){
            if (line.startsWith(""+pc_value)){
                break;
            }
            line_number++;
        }
        currentLine = line_number;
        return currentLine;
    }

    public void updateHighlighter(){

        try {
        int pc = myModel.programCounter();
        currentLine = getCurrentLine(pc);
        Highlighter high = myTextArea.getHighlighter();

        try {
        high.removeHighlight(currentHighlighter);}
        catch (NullPointerException e){
            System.out.println(e);
        }

        int start = myTextArea.getLineStartOffset(currentLine);
        int end   = myTextArea.getLineEndOffset(currentLine);
        currentHighlighter = high.addHighlight(start,end,new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
        }

        catch (BadLocationException e) {
            }
        }
    
    public lineHighlightPrinter(VM252Model model, JTextArea textArea)
    {       
        myModel = model;       
        myTextArea = textArea;
        currentLine = 1;
        }
   
    public void setCurrentLine(int value){
        currentLine = value;
    
    }

        }

class accumulatorPrinter extends VM252View {
         
    private final VM252Model myModel;
    
    public accumulatorPrinter(VM252Model model)
    {       
        myModel = model;       
        }
    
    @Override
    public void updateAccumulator()
    {       
        // Update the gui value
        DebugFrame.accumulator_display.setText(""+myModel.accumulator());
        }
    
    public void setAccumulator(int value){
        
        myModel.setAccumulator(value);
    
    }
    }

class ProgramCounterPrinter extends VM252View
    {
    
    private final VM252Model myModel;
    
    public ProgramCounterPrinter(VM252Model model)
    {        
        myModel = model;        
        }
    
    @Override
    public void updateProgramCounter()
    {        
        DebugFrame.count_diplay.setText(""+myModel.programCounter());
        } 
    
    
    public void setProgramCounter(int value){
        
        myModel.setProgramCounter(value);
    
    }
    
    }

class MemoryBytePrinter extends VM252View
    {
    
    private final VM252Model myModel;
    
    public MemoryBytePrinter(VM252Model model)
    {        
        myModel = model;        
        }
    
   @Override
    public void updateMemory(int address)
    {        
        System.out.printf("memory byte at address %d is now %02x\n", address, myModel.memoryByte(address));        
        String formattedString = String.format("memory byte at address %d is now %02x\n", address, myModel.memoryByte(address));
        DebugFrame.memory_display_one.append(formattedString);       

        }

    }

class StopAnnouncer extends VM252View
    {
    
    private final VM252Model myModel;
    
    public StopAnnouncer(VM252Model model)
    {        
        myModel = model;        
        }
    
    @Override
    public void updateStoppedStatus()
    {        
        // UPDATE GUI AFTER PROGRAM HAS ENDED
        if (DebugFrame.button_clicked == DebugFrame.Pause){

        String output_line_1 = String.format("machine paused | ACC : %d | PC %d\n", myModel.accumulator(), myModel.programCounter());
        DebugFrame.event_display.setText(DebugFrame.event_display.getText()+output_line_1);
            return;

        } else if (DebugFrame.button_clicked == DebugFrame.Stop){
            machine_stopped_midway();
            return;

        } else if (DebugFrame.simulatedMachine.stoppedStatus() == VM252Model.StoppedCategory.stopped){
        DebugFrame.reset_gui_components(false);
        System.out.println("program ended");
        String output_line_1 = String.format("machine stops | ACC : %d | PC : %d\n", myModel.accumulator(), myModel.programCounter());
        DebugFrame.event_display.setText(DebugFrame.event_display.getText()+ output_line_1 + "\n"
            );
        } else if (DebugFrame.simulatedMachine.stoppedStatus() == VM252Model.StoppedCategory.notStopped){
        }}
    
    public void machine_stopped_midway(){
        String output_line_1 = String.format("machine stops | ACC : %d | PC : %d\n", myModel.accumulator(), myModel.programCounter());
        DebugFrame.event_display.setText(DebugFrame.event_display.getText() + output_line_1);

    }
    }

public class DebugFrame extends javax.swing.JFrame {

    final JFileChooser fileChooser = new JFileChooser();
    String objFileName = "";
    static guiController simulator;
    static accumulatorPrinter accumulatorPrinterObject;
    static lineHighlightPrinter lineHighlightPrinterObject;
    static ProgramCounterPrinter programCounterPrinterObject;
    static StopAnnouncer stopAnnouncerObject;
    static MemoryBytePrinter memoryBytePrinterObject;
    static String instruction_to_be_executed;
    static JButton button_clicked;
    public static VM252Model simulatedMachine;
    
    public static void reset_gui_components(boolean enable){
        Start.setEnabled(enable);
        next_Line.setEnabled(enable);
        Stop.setEnabled(enable);
        Pause.setEnabled(enable);

        if (enable) {
            DebugFrame.output_text.setText("");
            DebugFrame.event_display.setText("");}

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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox3 = new javax.swing.JComboBox<>();
        Upper_Panel = new javax.swing.JPanel();
        selectFile = new javax.swing.JButton();
        file_Selected = new javax.swing.JTextField();
        Icon helpIcon = new ImageIcon("C: ./icons/Help_Icon.png");
        Help = new javax.swing.JButton(helpIcon);
        Button_Panel = new javax.swing.JPanel();
        Start = new javax.swing.JButton();
        Pause = new javax.swing.JButton();
        next_Line = new javax.swing.JButton();
        executeAgain = new javax.swing.JButton();
        Stop = new javax.swing.JButton();
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
        memory_options_two = new javax.swing.JComboBox<>();
        memory_display_scroll_two = new javax.swing.JScrollPane();
        memory_display_two = new javax.swing.JTextArea();

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(153, 153, 153));

        Upper_Panel.setName("Upper_Panel"); // NOI18N

        selectFile.setText("Select File");
        selectFile.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        selectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                selectFileActionPerformed(evt);}
                catch (IOException exception){
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

        Help.setLabel("Help");
        Help.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HelpActionPerformed(evt);
            }
        });

        Start.setText("Start");
        Start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartActionPerformed(evt);
            }
        });

        Pause.setText("Pause");
        Pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt){
                PauseActionPerformed(evt);
            }
        });

        next_Line.setText("Next line");
        next_Line.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt){
                NextActionPerformed(evt);
            }
        });

        executeAgain.setText("Again");
        executeAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeAgainActionPerformed(evt);
            }
        });
       
        
        
        

        Stop.setText("Stop");
        Stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt){
                stopActionPerformed(evt);
            }
        });
        adjust_Speed.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Speed", "Speed x1.5", "Speed x2" }));

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
                .addComponent(adjust_Speed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Button_PanelLayout.setVerticalGroup(
            Button_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Button_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Button_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Start)
                    .addComponent(Pause)
                    .addComponent(next_Line)
                    .addComponent(executeAgain)
                    .addComponent(Stop)
                    .addComponent(adjust_Speed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout Upper_PanelLayout = new javax.swing.GroupLayout(Upper_Panel);
        Upper_Panel.setLayout(Upper_PanelLayout);
        Upper_PanelLayout.setHorizontalGroup(
            Upper_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Upper_PanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(Button_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(Upper_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Upper_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Upper_PanelLayout.createSequentialGroup()
                        .addComponent(selectFile, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Help))
                    .addGroup(Upper_PanelLayout.createSequentialGroup()
                        .addComponent(file_Selected, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        Upper_PanelLayout.setVerticalGroup(
            Upper_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Upper_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Upper_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectFile, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Help, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(file_Selected, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(Button_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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
                .addGroup(Middle_WestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Program_Counter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(count_diplay))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        Middle_WestLayout.setVerticalGroup(
            Middle_WestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Middle_WestLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(Program_Counter, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(count_diplay, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
                .addGroup(Middle_CenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Middle_CenterLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(accumulator_display, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Middle_CenterLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(accumulator, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        Middle_CenterLayout.setVerticalGroup(
            Middle_CenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Middle_CenterLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(accumulator)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accumulator_display, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Middle_EastLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(Middle_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(instruction_Display, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(next_Instruction))
                .addGap(18, 18, 18))
        );
        Middle_EastLayout.setVerticalGroup(
            Middle_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Middle_EastLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(next_Instruction)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instruction_Display, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout Middle_PanelLayout = new javax.swing.GroupLayout(Middle_Panel);
        Middle_Panel.setLayout(Middle_PanelLayout);
        Middle_PanelLayout.setHorizontalGroup(
            Middle_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Middle_PanelLayout.createSequentialGroup()
                .addContainerGap(253, Short.MAX_VALUE)
                .addComponent(Middle_Center, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(Middle_West, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(Middle_East, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(253, Short.MAX_VALUE))
        );
        Middle_PanelLayout.setVerticalGroup(
            Middle_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Middle_West, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
            .addComponent(Middle_East, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Middle_Center, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
        );

        input_code_area.setColumns(20);
        input_code_area.setRows(5);
        input_code_area.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        input_code_scroll.setViewportView(input_code_area);
        input_code_area.getAccessibleContext().setAccessibleParent(Bottom_West);

        javax.swing.GroupLayout Bottom_WestLayout = new javax.swing.GroupLayout(Bottom_West);
        Bottom_West.setLayout(Bottom_WestLayout);
        Bottom_WestLayout.setHorizontalGroup(
            Bottom_WestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(Bottom_WestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Bottom_WestLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(input_code_scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        Bottom_WestLayout.setVerticalGroup(
            Bottom_WestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(Bottom_WestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Bottom_WestLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(input_code_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(90, Short.MAX_VALUE)))
        );

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Top_Bottom_EastLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Top_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(input_scroll)
                    .addComponent(Output_Value, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addGroup(Top_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(output_scroll)
                    .addComponent(event_value, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        Top_Bottom_EastLayout.setVerticalGroup(
            Top_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Top_Bottom_EastLayout.createSequentialGroup()
                .addGroup(Top_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Output_Value)
                    .addGroup(Top_Bottom_EastLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(event_value)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Top_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(input_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Top_Bottom_EastLayout.createSequentialGroup()
                        .addComponent(output_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        Center_Bottom_East.setBackground(new java.awt.Color(153, 153, 153));

        memory_options_one.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Object code as bytes in Hex", "Object code as 2 byte data in Hex", "Object code as instructions-data and labels", "Edit" }));
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
                .addGroup(Center_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(memory_display_scroll_one)
                    .addComponent(memory_options_one, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        Center_Bottom_EastLayout.setVerticalGroup(
            Center_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Center_Bottom_EastLayout.createSequentialGroup()
                .addComponent(memory_options_one, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(memory_display_scroll_one, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        Last_Bottom_East.setBackground(new java.awt.Color(153, 153, 153));

        memory_options_two.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Object code as bytes in Hex", "Object code as 2 byte data in Hex", "Object code as instructions-data and labels", "Edit" }));
        memory_options_two.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memory_options_twoActionPerformed(evt);
            }
        });

        memory_display_two.setEditable(false);
        memory_display_two.setColumns(20);
        memory_display_two.setRows(5);
        memory_display_scroll_two.setViewportView(memory_display_two);

        javax.swing.GroupLayout Last_Bottom_EastLayout = new javax.swing.GroupLayout(Last_Bottom_East);
        Last_Bottom_East.setLayout(Last_Bottom_EastLayout);
        Last_Bottom_EastLayout.setHorizontalGroup(
            Last_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Last_Bottom_EastLayout.createSequentialGroup()
                .addGroup(Last_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(memory_options_two, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(memory_display_scroll_two))
                .addContainerGap())
        );
        Last_Bottom_EastLayout.setVerticalGroup(
            Last_Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Last_Bottom_EastLayout.createSequentialGroup()
                .addComponent(memory_options_two, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(memory_display_scroll_two, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout Bottom_EastLayout = new javax.swing.GroupLayout(Bottom_East);
        Bottom_East.setLayout(Bottom_EastLayout);
        Bottom_EastLayout.setHorizontalGroup(
            Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Bottom_EastLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Bottom_EastLayout.createSequentialGroup()
                        .addComponent(Top_Bottom_East, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(Center_Bottom_East, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Last_Bottom_East, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        Bottom_EastLayout.setVerticalGroup(
            Bottom_EastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Bottom_EastLayout.createSequentialGroup()
                .addComponent(Top_Bottom_East, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Center_Bottom_East, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Last_Bottom_East, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Bottom_West, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(Bottom_East, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Upper_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(Middle_Panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Upper_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Middle_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Bottom_West, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Bottom_East, javax.swing.GroupLayout.PREFERRED_SIZE, 537, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 6, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void file_SelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_file_SelectedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_file_SelectedActionPerformed

    private void memory_options_oneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memory_options_oneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_memory_options_oneActionPerformed

    private void memory_options_twoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memory_options_twoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_memory_options_twoActionPerformed

    private void NextActionPerformed(java.awt.event.ActionEvent evt){//GEN-FIRST:event_StartActionPerformed
        try {
            if (file_Selected.getText().equals("No file selected")){
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
    }
        catch (IOException e){
    System.out.println("IO Exception");
}
        // TODO add your handling code here:
    }//GEN-LAST:event_StartActionPerformed

    private void PauseActionPerformed(ActionEvent evt){
        if (file_Selected.getText().equals("No file selected")){
            JOptionPane.showMessageDialog(this, "Select a file first");
        } else {
            button_clicked = Pause;
            simulatedMachine.setStoppedStatus(VM252Model.StoppedCategory.paused);
        }
    }

    private void StartActionPerformed(java.awt.event.ActionEvent evt){//GEN-FIRST:event_StartActionPerformed
        try {
        if (file_Selected.getText().equals("No file selected")){
            JOptionPane.showMessageDialog(this, "Select a file first");
        } else {
            String input_value = accumulator_display.getText();
            Scanner scanner_object = new Scanner(input_value);
            button_clicked = Start;
            simulatedMachine.setStoppedStatus(VM252Model.StoppedCategory.notStopped);
            simulator.run_simulation(input_value, scanner_object, System.out, "run");

        }}
        catch (IOException e){
    System.out.println("IO Exception");
}
        // TODO add your handling code here:
    }//GEN-LAST:event_StartActionPerformed

    private void HelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HelpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_HelpActionPerformed

    private void AccumulatorChangeActionPerfomed(java.awt.event.ActionEvent evt){
        String new_value = accumulator_display.getText();
        accumulatorPrinterObject.setAccumulator(Integer.parseInt(new_value));
        DebugFrame.event_display.setText(DebugFrame.event_display.getText()+ "ACC set to " + Integer.parseInt(new_value)+ "\n");
    }
    
    private void create_simulation_machine() throws IOException{

    simulatedMachine = new VM252Model();
    accumulatorPrinterObject = new accumulatorPrinter(simulatedMachine);
    programCounterPrinterObject = new ProgramCounterPrinter(simulatedMachine);
    stopAnnouncerObject = new StopAnnouncer(simulatedMachine);
    memoryBytePrinterObject = new MemoryBytePrinter(simulatedMachine);
    lineHighlightPrinterObject = new lineHighlightPrinter(simulatedMachine, DebugFrame.input_code_area);

    simulatedMachine.attach(accumulatorPrinterObject);
    simulatedMachine.attach(programCounterPrinterObject);
    simulatedMachine.attach(memoryBytePrinterObject);
    simulatedMachine.attach(stopAnnouncerObject);
    simulator = new guiController(simulatedMachine, lineHighlightPrinterObject);
    simulator.loadFile(objFileName, new Scanner(System.in), System.out);
    lineHighlightPrinterObject.updateHighlighter();

    // also put the first instruction
    instruction_Display.setText(simulator.machineStepper().next_instruction(true, 0));

    }

    private void selectFileActionPerformed(java.awt.event.ActionEvent evt) throws IOException{//GEN-FIRST:event_selectFileActionPerformed

        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(fileChooser);
        if (result == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fileChooser.getSelectedFile();
            String selectedFileName = selectedFile.getName();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            if (selectedFileName.endsWith(".vm252obj")) 
            {
            file_Selected.setText(selectedFile.getName());
            objFileName = selectedFile.getAbsolutePath();
            accumulator_display.setText("0");
            count_diplay.setText("0");
            create_simulation_machine();
            reset_gui_components(true);
        }
            else {
                //System.out.print("Invalid file");
            JOptionPane.showMessageDialog(this, "File must end with .vm252obj");  
            objFileName = "";
        }
        } else {
        file_Selected.setText("No file selected");
                }

        
    }
    private void executeAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeAgainActionPerformed
         count_diplay.setText("0");
         accumulator_display.setText("0");
         instruction_Display.setText("");
         DebugFrame.memory_display_one.setText(" ");
         try {
            create_simulation_machine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         reset_gui_components(true);
       
        
    }//GEN-LAST:event_executeAgainActionPerformed

    private void stopActionPerformed(ActionEvent evt){

        button_clicked = Stop;
        reset_gui_components(false);
        simulatedMachine.setStoppedStatus(VM252Model.StoppedCategory.stopped);
    }


    private void count_diplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_count_diplayActionPerformed
        // TODO add your handling code here:
        String new_value = count_diplay.getText();
        programCounterPrinterObject.setProgramCounter(Integer.parseInt(new_value));
        DebugFrame.event_display.setText(DebugFrame.event_display.getText()+ "PC set to " + Integer.parseInt(new_value)+ "\n");
    
    }//GEN-LAST:event_count_diplayActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException{
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DebugFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DebugFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DebugFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DebugFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

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
    private javax.swing.JComboBox<String> adjust_Speed;
    public static javax.swing.JTextField count_diplay;
    public static javax.swing.JTextArea output_text;
    private javax.swing.JButton executeAgain;
    private javax.swing.JTextField file_Selected;
    public static  javax.swing.JTextArea input_code_area;
    private javax.swing.JScrollPane input_code_scroll;
    private javax.swing.JScrollPane input_scroll;
    public static javax.swing.JTextField instruction_Display;
    private javax.swing.JComboBox<String> jComboBox3;
    public static javax.swing.JTextArea memory_display_one;
    public static javax.swing.JScrollPane memory_display_scroll_one;
    public static javax.swing.JScrollPane memory_display_scroll_two;
    private javax.swing.JTextArea memory_display_two;
    public static javax.swing.JComboBox<String> memory_options_one;
    private javax.swing.JComboBox<String> memory_options_two;
    private javax.swing.JLabel next_Instruction;
    private static javax.swing.JButton next_Line;
    public static javax.swing.JTextArea event_display;
    private javax.swing.JScrollPane output_scroll;
    private javax.swing.JButton selectFile;
    // End of variables declaration//GEN-END:variables

    private Scanner Scanner(String input_value) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static void update_event_display(){
        String output_line_1 = String.format("INPUT %d given | ACC : %d | PC : %d", simulatedMachine.accumulator(), simulatedMachine.accumulator(), simulatedMachine.programCounter());
        DebugFrame.event_display.setText(DebugFrame.event_display.getText()+ output_line_1 + "\n");

    }


}
