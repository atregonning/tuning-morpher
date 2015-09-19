package com.adriantregonning.javamusic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;

import com.adriantregonning.javamusic.TuningTrajectory.*;
import com.adriantregonning.javamusic.TuningTrajectoryEditorPanel;
import com.adriantregonning.jscore.ScorePiece;
import com.softsynth.jmsl.JMSL;
import com.softsynth.jmsl.score.ScoreControlPanel;
import com.softsynth.view.*;

/**
 * Tuning Trajectory GUI demonstration
 * 
 * <p>TuningTrajectoryGUI is a JApplet that demonstrates the functionality of the 
 * Tuning trajectory class. A JMSL score is generated, along with facilities 
 * for editing the tuning system used in the score.</p>
 * 
 * <p>An example of it's use can be seen 
 * <a href="https://www.youtube.com/watch?v=0A6KDA9k3og>">here</a>.</p>
 * 
 * @author Adrian Tregonning
 *   
 */
@SuppressWarnings("serial")
public class TuningTrajectoryGUI extends JApplet 
                    implements CustomFaderListener, ActionListener {
        
    // Preset source and destination tunings to use
    Tunings sourcePreset = TuningTrajectory.Tunings.TWELVE_TET;
    Tunings destPreset = TuningTrajectory.Tunings.PYTHAG;
    
    // Source and destination Tuning Trajectory objects
    TuningTrajectory sourceTraj;
    TuningTrajectory destTraj;
    TuningTrajectory morphTraj;
    
    // Flag for including GUI in a JMSL score frame
    boolean addToScoreFrame = true;
    
    // Applet title
    JLabel title;

    // Section labels
    JLabel sourcePanelLabel;
    JLabel destPanelLabel;
    JLabel morphPanelLabel;
    JLabel sourcePresetsLabel;
    JLabel destPresetsLabel;

    // Panels and sub-panels
    JPanel mainPanel;
    JPanel bodyPanel;
    JPanel sourcePanel;
    JPanel morphPanel;
    JPanel destPanel;
    JPanel morphFaderPanel;
    JPanel morphValuePanel;
    JPanel morphTypeAndButtonPanel;
    JPanel sourcePresetsPanel;
    JPanel destPresetsPanel;
    JPanel pieceControlPanel;

    TuningTrajectoryEditorPanel sourceTablePanel;
    TuningTrajectoryEditorPanel destTablePanel;
    TuningTrajectoryEditorPanel morphTablePanel;

    JComboBox sourcePresetsBox;
    JComboBox destPresetsBox;

    // Components for the morphing panel: 
    // Morph index fader, dropdown list for interpolator choice, text area 
    // showing current morph index, button
    CustomFader morphFader;
    JTextArea morphFaderVal;
    String[] interpTypes = {"Linear", "Half Cosine", "Exponential"};
    JComboBox interpMenu;
    JButton morphButton;
    
    /// Buttons for starting/stopping piece
    JButton startPieceButton;
    JButton stopPieceButton;
    
    // JMSL Score for playing music
    ScorePiece scorePiece;
    
    // Number formatter (used for formatting ratios)
    DecimalFormat newFormat = new DecimalFormat("#.##");
        
    // Build the score and source and destination tuning trajectories.
    private void buildPiece(boolean withTuningTables) {
        int width, height;
        
        if(withTuningTables) {
            width = 1250;
            height = 200;
        } else {
            width = 800;
            height  = 500;
        }
        scorePiece = new ScorePiece();
        scorePiece.makeScore(width, height);
        
        sourceTraj = new TuningTrajectory();
        sourceTraj.setFrequencies(sourcePreset.ratios());
        sourceTraj.makeEditorPanel();
        
        destTraj = new TuningTrajectory();
        destTraj.setFrequencies(destPreset.ratios());
        destTraj.makeEditorPanel();      
    }
    
    // GUI constructor. If inScoreframe is true the tuning tables are added to the JMSL score window,
    // or else they are created in a separate window.
    private void buildGUI(boolean inScoreFrame) {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        title = new JLabel("Tuning Trajectory");
        title.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(title, BorderLayout.PAGE_START);

        bodyPanel = new JPanel(new GridLayout (1,3));
        
        // Components for source tuning table panel
        sourcePanelLabel = new JLabel("Source Tuning");
        sourcePanelLabel.setHorizontalAlignment(JLabel.CENTER);
        sourceTablePanel = sourceTraj.getPanel();
        
        sourcePresetsPanel = new JPanel(new GridLayout(1,2));
        sourcePresetsLabel = new JLabel("Presets:");
        sourcePresetsLabel.setHorizontalAlignment(JLabel.CENTER);
        sourcePresetsBox = new JComboBox(Tunings.labels);
        sourcePresetsBox.setSelectedIndex(sourcePreset.idx());
        sourcePresetsPanel.add(sourcePresetsLabel);
        sourcePresetsPanel.add(sourcePresetsBox);
        
        sourcePanel = new JPanel();
        sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.Y_AXIS));
        sourcePanel.add(sourcePanelLabel);
        sourcePanel.add(sourcePresetsPanel);
        sourcePanel.add(sourceTablePanel);
        
        // Components for destination tuning table panel
        destPanelLabel = new JLabel("Destination Tuning");
        destPanelLabel.setHorizontalAlignment(JLabel.CENTER);
        destTablePanel = destTraj.getPanel();
        
        destPresetsPanel = new JPanel(new GridLayout(1,2));
        destPresetsLabel = new JLabel("Presets:");
        destPresetsLabel.setHorizontalAlignment(JLabel.CENTER);
        destPresetsBox = new JComboBox(Tunings.labels);
        destPresetsBox.setSelectedIndex(destPreset.idx());
        destPresetsPanel.add(destPresetsLabel);
        destPresetsPanel.add(destPresetsBox);
        
        destPanel = new JPanel();
        destPanel.setLayout(new BoxLayout(destPanel, BoxLayout.Y_AXIS));
        destPanel.add(destPanelLabel);
        destPanel.add(destPresetsPanel);
        destPanel.add(destTablePanel);
        
        // Components for morphing panel
        morphPanel = new JPanel();
        morphPanel.setLayout(new BoxLayout(morphPanel, BoxLayout.Y_AXIS));
        
        morphButton = new JButton("Morph tuning!");
        interpMenu = new JComboBox(interpTypes);
        interpMenu.setSelectedIndex(0);
        
        morphFaderPanel = new JPanel();
        morphFader = new CustomFader(CustomFader.HORIZONTAL, 0, 1, 0, 100);
        morphFader.setPreferredSize(new Dimension(200,30));
        morphFaderVal = new JTextArea("%");
        morphFaderPanel.add(morphFader);
        morphFaderPanel.add(morphFaderVal);
        
//      morphPanel.add(morphPanelLabel);
        morphPanel.add(morphFaderPanel);
        
        morphTypeAndButtonPanel = new JPanel();
        morphTypeAndButtonPanel.add(interpMenu);
        morphTypeAndButtonPanel.add(morphButton);
        morphFaderPanel.add(morphTypeAndButtonPanel);
 
        // Set some additional properties/layouts depending on placement of trajectory panel
        if(inScoreFrame) {
            bodyPanel.setPreferredSize(new Dimension(WIDTH, (HEIGHT * 2) + 30));

            // Create main panel
            mainPanel.add(bodyPanel, BorderLayout.CENTER);
            scorePiece.getScoreFrame().setFrameLayout(new FlowLayout());
            scorePiece.getScoreFrame().add(mainPanel);          
        } else {            
            // Playback controls
            pieceControlPanel = new JPanel(new GridLayout(1,2));
            startPieceButton = new JButton("Start piece");
            stopPieceButton = new JButton("Stop piece");
            stopPieceButton.setEnabled(false);
            pieceControlPanel.add(startPieceButton);
            pieceControlPanel.add(stopPieceButton);

            // Create main panel
            mainPanel.add(bodyPanel, BorderLayout.CENTER);
            scorePiece.getScoreFrame().setFrameLayout(new FlowLayout());
            mainPanel.add(pieceControlPanel, BorderLayout.PAGE_END);
            add(mainPanel);
        }
        
        // Add sub-panels to body of main panel
        bodyPanel.add(sourcePanel);
        bodyPanel.add(morphPanel);
        bodyPanel.add(destPanel);
                
        // Set borders etc.
        sourcePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        morphPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        destPanel.setBorder(BorderFactory.createLineBorder(Color.black));
//      sourcePanel.setBackground(Color.BLUE);
//      scorePiece.getScoreFrame().setBackground(new Color(105, 0, 205));
        
        // Add action listeners
        morphButton.setEnabled(false);
        morphButton.addActionListener(this);
        morphFader.addCustomFaderListener(this);
        sourcePresetsBox.addActionListener(this);
        destPresetsBox.addActionListener(this);
        interpMenu.addActionListener(this);
        startPieceButton.addActionListener(this);
        stopPieceButton.addActionListener(this);
    }
        
    // Callback listener
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        ScoreControlPanel ctrls = scorePiece.getScoreFrameControls();
        
        if(source == morphButton) {
            double index = ((double) morphFader.getValue()) / 100;
            int interpType = interpMenu.getSelectedIndex();
            try {
                double [] sourceRatios = sourceTablePanel.getFreqTable();
                double [] destRatios = destTablePanel.getFreqTable();
                morphTraj = TuningTrajectory.makeInterpolatedTuning(
                        sourceRatios, destRatios, index, interpType);
                morphTraj.makeEditorPanel();
                if (morphTablePanel != null) {
                    morphTablePanel.setFreqTable(morphTraj.getRatios());
                } else {
                    morphTablePanel = morphTraj.getPanel();
                    morphPanel.add(morphTablePanel);
                }
                morphTablePanel = morphTraj.getPanel();
                scorePiece.setOrchestraTuning(morphTraj);
            } 
            catch (NumberFormatException e1) {
                    throw e1;
                }
            morphButton.setEnabled(false);
        } else if(source == interpMenu) {
            morphButton.setEnabled(true);
        } else if(source == startPieceButton) {
            ctrls.launch(JMSL.now(), 0, scorePiece.getScoreSize() - 1, false);
            startPieceButton.setEnabled(false);
            stopPieceButton.setEnabled(true);
        } else if(source == stopPieceButton) {
            ctrls.finish();
            stopPieceButton.setEnabled(false);
            startPieceButton.setEnabled(true);
        } else {
            if(source == sourcePresetsBox) {
                sourceTraj.setToPreset(sourcePresetsBox.getSelectedIndex());
                sourceTablePanel.setFreqTable(sourceTraj.getRatios());
            } else if(source == destPresetsBox) {
                destTraj.setToPreset(destPresetsBox.getSelectedIndex());
                destTablePanel.setFreqTable(destTraj.getRatios());
            }
            morphButton.setEnabled(true);
        }
    }
    
    // Listener for morph fader
    @Override
    public void customFaderValueChanged(CustomFader fader, int val) {
        morphButton.setEnabled(true);
        morphFaderVal.setText(newFormat.format(val) + "%");
        if(addToScoreFrame) {
            scorePiece.getScoreFrame().validate();
        }
    }
    
    // Applet start method
    public void start() {
        buildPiece(addToScoreFrame);
        buildGUI(addToScoreFrame);
    }
}
