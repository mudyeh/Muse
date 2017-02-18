/**
 * Created by Matt on 2/17/2017.
 * Creates "bluesy" music using blues theory
 */
import org.jfugue.theory.*; // sloppy wild card import
import org.jfugue.pattern.*;
import org.jfugue.player.*;

import java.util.List;

//import javax.sound.midi.Instrument;

public class BluesGenerator {
    private int measuresInForm;
    private int tempo;
    private String backingInstrument;
    private String leadInstrument;
    private String root;

    private int voiceNum = 0;
    private int subBeatCount = 0;
    private static final int LOWEST_DIVISION = 32;
    private TimeSignature ts = new TimeSignature(4, 4);

    // For all of these, remember to set the root
    public static final Intervals MAJOR = Scale.MAJOR.getIntervals();
    public static final Intervals MINOR = Scale.MINOR.getIntervals();
    public static final Intervals MAJOR_PENTATONIC = new Intervals("1 2 3 5 6");
    public static final Intervals MINOR_PENTATONIC = new Intervals("1 b3 4 5 b7");
    public static final Intervals MAJOR_BLUES = new Intervals("1 2 b3 3 5 6");
    public static final Intervals MINOR_BLUES = new Intervals("1 b3 4 b5 5 b7");

    //    public String chordProgression;

    public BluesGenerator(int measuresInForm, int tempo, String root,
                          String backingInstrument, String leadInstrument) {
        this.measuresInForm = measuresInForm;
        this.tempo = tempo;
        this.root = root;
        this.backingInstrument = backingInstrument;
        this.leadInstrument = leadInstrument;
        /* for now (and simplicity's sake), the chord progression is
        assumed to be the standard I IV V */
//        this.chordProgression = chordProgression;
    }

    /* Either uses common blues forms (8 bar, 12 bar, 16 bar) to return full chord progression
    or randomly creates a chord progression (more interesting) */
    private String determineForm() {
        if (measuresInForm == 12) {
            return "$0 $0 $0 $0 $1 $1 $0 $0 $2 $1 $0 $2";
        } else if (measuresInForm == 8) {
            return "$0 $0 $1 $1 $0 $2 $1 $2";
        } else if (measuresInForm == 16) {
            return "$0 $0 $0 $0 $1 $1 $0 $0 $2 $1 $2 $1 $2 $1 $0 $0";
        } else {
            String pattern = "";
            for (int i = 0; i < measuresInForm; i++) {
                int scaleDegree = StdRandom.uniform(3);
                pattern = pattern.concat("$" + scaleDegree + " ");
            }
            return pattern;
        }
    }

    public Pattern getBackingPattern() {
        ChordProgression cp = new ChordProgression("I IV V");
        cp = cp.setKey(root)
                .distribute("7%6")
                .allChordsAs(determineForm());
        Pattern root = cp.eachChordAs(replicate("$0ia60 ", 8))
                .getPattern()
                .setVoice(voiceNum)
                .setTempo(tempo)
                .setInstrument(backingInstrument);
        voiceNum+=1;
        Pattern top = cp.eachChordAs(replicate("$2ia80 $2ia80 $3ia90 $2ia80 ", 2))
                .getPattern()
                .setVoice(voiceNum)
                .setTempo(tempo)
                .setInstrument(backingInstrument);
        voiceNum+=1;
        return root.add(top);
    }

    // at some point should also return rests as well ***
    private String getRandNote() {
        int noteNum = StdRandom.uniform(0, 12);
        switch (noteNum) {
            case 0:
                return "C";
            case 1:
                return "C#";
            case 2:
                return "D";
            case 3:
                return "D#";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "F#";
            case 7:
                return "G";
            case 8:
                return "G#";
            case 9:
                return "A";
            case 10:
                return "Bb";
            case 11:
                return "B";
            default:
                return null;
        }
    }

    /* maximum number of options (for now) is 6: whole to 32nd
    * Lowering this number is not a linear process: lowest/highest values aren't
    * necessarily removed first. */
    private String getDurationNum(int numOptions) {
        int durationNum = StdRandom.uniform(0, numOptions);
        switch (durationNum) {
            case 0:
                subBeatCount += 4;
                return "i";
            case 1:
                subBeatCount += 8;
                return "q";
            case 2:
                subBeatCount += 2;
                return "s";
            case 3:
                subBeatCount += 16;
                return "h";
            case 4:
                subBeatCount += 1;
                return "t";
            case 5:
                subBeatCount += 32;
                return "w";
            default:
                return null;
        }
    }

    // inclusive
    private int getOctaveNum(int min, int max) {
        return StdRandom.uniform(min, max+1);
    }

    /* The more "computer music" melody generator:
     * Randomly selects notes from chromatic scale for melody
     * Obviously doesn't sound good */
    public Pattern getRandomLeadPattern() {
        String songString = root
                + getOctaveNum(3, 5)
                + getDurationNum(4)
                + " ";
        // currently hardcode what the ranges/numOptions are; will later add user ability to manipulate
        while (subBeatCount < getTotalSubBeats()) {
            songString = songString.concat(getRandNote()
                    + getOctaveNum(3, 5)
                    + getDurationNum(4)
                    + " ");
        }
        Pattern lead = new Pattern(songString)
                .setVoice(voiceNum)
                .setTempo(tempo)
                .setInstrument(leadInstrument);
        voiceNum += 1;

        return lead;
    }

    /* The more "human" melody generator:
    * More heavily employs blues theory
    * First iteration assumes major blues */
    public Pattern getBluesLeadPattern() {
        Intervals scale1 = MAJOR.setRoot(root);
        Intervals scale2 = MAJOR_PENTATONIC.setRoot(root);
        Intervals scale3 = MAJOR_BLUES.setRoot(root);
        Intervals curScale = scale3;
        String songString = root
                +getOctaveNum(4, 6)
                +getDurationNum(4)
                + " ";

        int measuresPlayed = 0;
        while (subBeatCount < getTotalSubBeats()) {
            if (measuresPlayed % 4 == 0) {
                // Every 4 bars, randomly pick which scale to use
                // Also randomly change the octave of the scale
                int randInt = StdRandom.uniform(3);
                if (randInt == 0) {
                    curScale = scale1;
                } else if (randInt == 1) {
                    curScale = scale2;
                } else {
                    curScale = scale3;
                }
                curScale.setRoot(root + getOctaveNum(3, 5));
            }

            // Convert the set of intervals into a List of Notes
            List<Note> curScaleNotes = curScale.getNotes();

            // Form the randomly chosen note
            songString = songString.concat(
                    curScaleNotes.get(StdRandom.uniform(curScale.size()))
                    + getDurationNum(1)
                    + " ");
            measuresPlayed += 1;
        }

        Pattern lead = new Pattern(songString)
                .setVoice(voiceNum)
                .setTempo(tempo)
                .setInstrument(leadInstrument);
        voiceNum += 1;

        return lead;
    }


    // replicates a given string n times
    private String replicate(String baseUnit, int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result = result.concat(baseUnit);
        }
        return result;
    }

    private int getTotalSubBeats() {
        int totalSubBeats = LOWEST_DIVISION
                * measuresInForm
                * ts.getBeatsPerMeasure()
                / ts.getDurationForBeat();
        return totalSubBeats;
    }

    public static void main(String[] args) {
        BluesGenerator b = new BluesGenerator(16, 140, "C",
                "electric_jazz_guitar", "overdriven_guitar");

//        System.out.print(MAJOR_BLUES.setRoot("D").getNotes());
//        System.out.print(MAJOR_BLUES.getNotes());
        Player p = new Player();
        Pattern rhythm = b.getBackingPattern();
        Pattern lead = b.getBluesLeadPattern();
        p.play(rhythm.add(lead));
    }
}
