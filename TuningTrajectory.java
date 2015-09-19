package com.adriantregonning.javamusic;

import com.softsynth.jmsl.util.*;

/******************************************************************************
 * <p>Compilation           : javac TuningTrajectory.java</p>
 * <p>Dependencies          : com.softsynth.jmsl.util</p>
 * <p>Associated classes    : com.adriantregonning.javamusic.TuningTrajectory.
 *                            EditorPanel</p>
 *
 *  <p> A Java class that extends JMSL's tuning table to allow for real-time 
 *  modification of the tuning system used for computer music synthesis. 
 *  Tuning systems are based on the frequency ratios between the notes in a 
 *  scale (where the tonic/fundamental frequency = 1).A number of standard 
 *  tuning systems (12-tone equal temperament, Pythagorean, etc.) are available
 *  as presets. NOTE: This class uses the default (empty) constructor.</p>
 *  
 *  @author Adrian Tregonning
 *  @version 1.0
 *
 *****************************************************************************/
public class TuningTrajectory extends TuningTable {
	
    /** 
     * Enumeration of the frequency ratios used in some standard tuning systems
     * This includes the reference pitch and frequency of Middle C.
     */
	public enum Tunings {
	    TWELVE_TET ("12-TET", 0),
	    PYTHAG ("Pythagorean", 1),
	    PTOLEMY ("Ptolemy", 2), 
        ERHU ("Erhu", 3);
        
	    // Reference frequency and pitches upon which to base the above 
	    // tuning systems.
        public static final double refFreq = TuningET.MIDDLE_C_FREQ;
        public static final double refPitch = TuningET.MIDDLE_C_PITCH;
        public static final String[] labels = {"12-TET", "Pythagorean", 
                "Ptolemy", "Erhu"};
            
        // 12-tone equal temperament
        private final double[] twelveTetRatios = {
                1.0,
                Math.pow(2.0, 1.0/12),
                Math.pow(2.0, 2.0/12),
                Math.pow(2.0, 3.0/12),
                Math.pow(2.0, 4.0/12),
                Math.pow(2.0, 5.0/12),
                Math.pow(2.0, 6.0/12),
                Math.pow(2.0, 7.0/12),
                Math.pow(2.0, 8.0/12),
                Math.pow(2.0, 9.0/12),
                Math.pow(2.0, 10.0/12),
                Math.pow(2.0, 11.0/12),
        };
        
        // Gb is discarded
        private final double[] pythagRatios = {         
                1.0,
                256.0/243,
                9.0/8.0,
                32.0/27,
                81.0/64,
                4.0/3,
                729.0/512,
                3.0/2, 
                128.0/81,
                27.0/16,
                16.0/9,
                243.0/128
        };
        
        // Based on PtolemyTuning
        private final double[] ptolemyRatios = {     
                1.0,
                16.0/15,
                9.0/8,
                6.0/5,
                5.0/4,
                4.0/3,
                1.40625,
                3.0/2,
                8.0/5,
                5.0/3,
                9.0/5,
                15.0/8
        };
        
        // Derived from Sethares' theory of dissonance curves 
        // for the Chinese erhu
        private final double[] erhuRatios = {        
                1.0,
                1.0,
                1.17,
                1.17,
                1.30,
                1.34,
                1.34,
                1.51,
                1.51,
                1.67,
                1.67,
                1.85
        };
        
        private String label;
        private int idx;
        private double[] ratios;
        
	    Tunings(String label, int idx) {
	        this.label = label;
	        this.idx = idx;
	        
	        switch (idx) {
            case 0: 
                this.ratios = twelveTetRatios;
                break;
            case 1: 
                this.ratios = pythagRatios;
                break;
            case 2:
                this.ratios = ptolemyRatios;
                break;
            case 3: 
                this.ratios = erhuRatios;
                break;
	        }  
	    }
	    
	    // Getters
	    public String label() { return label; }
	    public int idx() { return idx; }
        public double[] ratios() { return ratios; }

	}
	
	private TuningTrajectoryEditorPanel editPanel;  // JPanel GUI element   
    private double[] ratios;                       // Frequency ratios.
    
    // Static helper method that generates an arrays of frequencies (in Hz) 
    // from given frequency ratios and a reference pitch.
    private static double[] buildFreqsFromRatios(double[] ratios, 
            double refFreq) {
        double[] freqs = new double[ratios.length];
        for(int i = 0; i < ratios.length; i++) {
            freqs[i] = ratios[i] * refFreq;
        }
        //System.out.println(Arrays.toString(freqs));
        return freqs;
    }
    
    /**
     * Returns current tuning's frequency ratios
     * 
     * @return Array of ratios
     */
    public double[] getRatios() {
        return ratios;
    }
   
    /**
     * Returns the current object's GUI panel, if present.
     * 
     * @return The GUI panel object, <tt>null</tt> if nonexistent
     */
    public TuningTrajectoryEditorPanel getPanel() {
        if (editPanel != null) {
            return editPanel;
        }
        return null;
    };
    
    /**
     * Sets the tuning table to the supplied frequency ratios.
     * 
     * @param newRatios An array of the desired frequency ratios
     */
    public void setTable(double[] newRatios) {
		ratios = newRatios;
		// Start table data
		setFrequencies(buildFreqsFromRatios(ratios, Tunings.refFreq)); 	
		setReferencePitch(Tunings.refPitch);
	}
	
	/** 
	 * Resets a tuning table to a given preset from the Tunings enum 
	 * presetIndex is the index of the preset as outlined in the Tunings enum.
     * 
     * @param presetIndex integer index of preset
     */
    public void setToPreset(int presetIndex) {
        double[] ratios = null;
        
        switch (presetIndex) {
            case 0:
                ratios = Tunings.TWELVE_TET.ratios();
                break;
            case 1:
                ratios = Tunings.PYTHAG.ratios();
                break;
            case 2:
                ratios = Tunings.PTOLEMY.ratios();
                break;
            case 3:
                ratios = Tunings.ERHU.ratios();
                break;   
        }
        setFrequencies(buildFreqsFromRatios(ratios, Tunings.refFreq));
    }
	
    /**
     *  Create a GUI element for the object
     */
    public void makeEditorPanel() {
        editPanel = new TuningTrajectoryEditorPanel(this, getFrequencies());
    }
    
	/**
	 *  Static method for generating a new tuning trajectory that is an 
	 *  interpolated mix between the supplied source and destination frequency 
	 *  ratios.
	 *  
	 * @param  sourceRatios    array of source frequency ratios
	 * @param  destRatios      array of destination frequency ratios
	 * @param  morphIndex      describes the percentage mix between the source 
	 *                         and destination ratios
	 * @param  intType         specifies the type of interpolation to use
	 * 
	 * @return A new tuning trajectory object with the interpolated ratios
	 */
	public static TuningTrajectory makeInterpolatedTuning(double[] sourceRatios, 
	        double[] destRatios, double morphIndex, int intType) {
	    Interpolator interpol = null;
	    // Set the type of interpolation used to generate the ratios
	    switch(intType) {
	    case 0:
	        interpol = new LinearInterpolator(0, 0, 1, 1);
	        break;
	    case 1:
	        interpol = new HalfCosineInterpolator(0, 0, 1, 1);
	        break;
	    case 2:
	        interpol = new ExponentialInterpolator(0, 0, 1, 1);
	        break;
	    }

	    int steps = sourceRatios.length;
	    double[] newRatios = new double[steps];

	    for(int i = 0; i < steps; i++) {
	        interpol.setInterp(0, sourceRatios[i], 1, destRatios[i]);
	        newRatios[i] = interpol.interp(morphIndex);
	    }

	    // Create and return a new tuning trajectory object
	    TuningTrajectory newTrajectory = new TuningTrajectory();
	    newTrajectory.setFrequencies(buildFreqsFromRatios(newRatios, 
	            Tunings.refFreq));
	    newTrajectory.setReferencePitch(Tunings.refPitch);
	    if(newTrajectory.editPanel != null)
	        newTrajectory.editPanel.setFreqTable(newRatios);

	    return newTrajectory;
	}	

}