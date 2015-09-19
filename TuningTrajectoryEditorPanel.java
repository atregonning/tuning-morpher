package com.adriantregonning.javamusic;

import java.awt.GridLayout;
import java.awt.event.*;
import java.text.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/******************************************************************************
 * <p>Compilation  : javac TuningTrajectory.java</p>
 * <p>Dependencies : javax.swing</p>
 * <p>Associated classes:  com.adriantregonning.javamusic.TuningTrajectory</p>
 *
 *  <p> A GUI element for the Tuning Trajectory class. Displays the frequency 
 *  ratios of the tuning table and allows them to be edited individually.</p>
 *  
 *  @author Adrian Tregonning
 *  @version 1.0
 *
 *****************************************************************************/
@SuppressWarnings("serial")
public class TuningTrajectoryEditorPanel extends JPanel {
    // Container for the panel
	private JPanel container;                    
	// Individual editable fields for the scale degrees' frequency ratios
	private JFormattedTextField[] freqFields;    
	// Labels for the fields above
	private JLabel[] labels;                     
	
	// Number formatter, used to display calculated ratios
    private DecimalFormat newFormat = new DecimalFormat("#.####");  

    // The panel's associated tuning trajectory
    private TuningTrajectory traj;                  
    
    /**
     * The panel is initialized with an array of supplied frequency ratios.
     * 
     * @param traj TuningTrajectory to associate the panel
     * @param freqs array of frequency ratios
     */
	public TuningTrajectoryEditorPanel(TuningTrajectory traj, double[] freqs) {
		int steps = freqs.length;
		
		container = new JPanel(new GridLayout(steps, 2));
		labels = new JLabel[steps];
		freqFields = new JFormattedTextField[steps];
		
		// Create the ratio text fields and labels, with callback listeners 
		// for when they are modified
		for(int i = 0; i < steps; i++) {
			labels[i] = new JLabel(newFormat.format(i + 1));
			labels[i].setHorizontalAlignment(JLabel.CENTER);
			freqFields[i] = new JFormattedTextField(newFormat.format(freqs[i]));
			container.add(labels[i]);
			container.add(freqFields[i]);

			// Callback listener for the text fields. 
			// Currently, these are unutilized.
			freqFields[i].getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					// text was changed - debug statement
					// System.out.println("Changed");
				}
				public void removeUpdate(DocumentEvent e) {
				}
				public void insertUpdate(DocumentEvent e) {
				}
			});
			
			this.traj = traj;
		}	
		
		// Add a button for changing the associated trajectory object's 
		// tuning table to the panel's fields
		final TuningTrajectoryEditorPanel thisPanel = this;
        JPanel button = new JPanel();
        final JButton getButton = new JButton("Get");
        button.add(getButton);
        button.add(thisPanel.getPanel());
        ActionListener a = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if(source == getButton) {
                    double [] vals = thisPanel.getFreqTable();
                    thisPanel.traj.setTable(vals);
                }
            }
        };
        getButton.addActionListener(a);
	}
	
	/**
	 *  Returns the object's GUI container.
	 *  
	 * @return The object's JPanel container
	 */
	public JPanel getPanel() {
		return container;
	}
	
	/**
	 * Adds the panel to a given swing frame.
	 * 
	 * @param frame A JPanel frame
	 */
    public void addPanel(JFrame frame) {
        frame.add(container);
        frame.pack();
        frame.setVisible(true);
    }
    
	/**
	 * Sets the fields of the editor panel to the supplied values, 
	 * formatted appropriately.
	 * 
	 * @param newFreqs array of frequency ratios
	 */
	public void setFreqTable(double[] newFreqs) {
		int steps = newFreqs.length;
		
		for(int i = 0; i < steps; i++) {
			freqFields[i].setValue(newFormat.format(newFreqs[i]));
		}	
	}
	
	/**
	 * Gets frequency values from the panel fields.
	 * 
	 * @return array of current frequency ratios
	 */
	public double[] getFreqTable() {
		int steps = freqFields.length;
		double[] freqs = new double[steps];
		
		for(int i = 0; i < steps; i++) {
			try {
				freqs[i] = Double.parseDouble(freqFields[i].getText());
			} catch (NumberFormatException e) {
				System.err.println("Error: invalid numerical input (step " 
						+ (i+1) + " - " + e.getMessage() + ")");
				throw e;
			}
		}	
		return freqs;
	}

}