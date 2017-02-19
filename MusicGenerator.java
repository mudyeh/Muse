/**
 * Created by Matt on 2/18/2017.
 * Supreme overlord music generation class
 */
import org.jfugue.theory.*; // sloppy wild card import
import org.jfugue.pattern.*;
import org.jfugue.player.*;
import java.util.List;

public abstract class MusicGenerator {
    protected int measures;
    protected int tempo;
    protected TimeSignature ts;
    protected String root;
    protected ChordProgression chordProgression;

    // doubles used to account for case where for whatever reason,
    // largestDivision is not actually set to the largest division
    protected double largestDivision = 32.0;
    protected double subBeatCount = 0;

    protected int voiceNum = 0;

    // common scales
    public static final Intervals MAJOR = Scale.MAJOR.getIntervals();
    public static final Intervals MINOR = Scale.MINOR.getIntervals();
    public static final Intervals MAJOR_PENTATONIC = new Intervals("1 2 3 5 6");
    public static final Intervals MINOR_PENTATONIC = new Intervals("1 b3 4 5 b7");
    public static final Intervals MAJOR_BLUES = new Intervals("1 2 b3 3 5 6");
    public static final Intervals MINOR_BLUES = new Intervals("1 b3 4 b5 5 b7");
    public static final Intervals MELODIC_MINOR = new Intervals("1 2 b3 4 5 6 7");
    public static final Intervals HARMONIC_MINOR = new Intervals("1 2 b3 4 5 b6 #7");
    public static final Intervals DORIAN_MODE = new Intervals("1 2 b3 4 5 6 7");
    public static final Intervals MIXOLYDIAN_MODE = new Intervals("1 2 3 4 5 6 b7");

    // Returns a note from Western chromatic scale at random
    protected String genRandNote() {
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

    // Returns a random note duration, with numOptions limiting the options
    // Note that the order of note values here is not highest to lowest or vice versa
    // This is so decreasing numOptions will lead to more "solo" like phrasing
    // i.e. faster paced notes
    // Range of values: whole notes to 128th notes
    protected String genNoteDuration(int numOptions) {
        int durationNum = StdRandom.uniform(0, numOptions);
        switch (durationNum) {
            case 0:
                subBeatCount += largestDivision/8;
                return "i";
            case 1:
                subBeatCount += largestDivision/4;
                return "q";
            case 2:
                subBeatCount += largestDivision/16;
                return "s";
            case 3:
                subBeatCount += largestDivision/2;
                return "h";
            case 4:
                subBeatCount += largestDivision/32;
                return "t";
            case 5:
                subBeatCount += largestDivision;
                return "w";
            case 6:
                subBeatCount += largestDivision/64;
                return "x";
            default:
                subBeatCount += largestDivision/128;
                return "o";
        }
    }

    // Get/Set method suite
    protected int genNoteOctave(int min, int max) {
        return StdRandom.uniform(min, max + 1);
    }

    public int getMeasures() {
        return measures;
    }

    public void setMeasures(int num) {
        measures = num;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int num) {
        tempo = num;
    }

    public String getTimeSignature() {
        int bpm = ts.getBeatsPerMeasure();
        int dpb = ts.getDurationForBeat();
        return bpm + "/" + dpb;
    }

    public void setTimeSignature(int bpm, int dbp) {
        ts.setBeatsPerMeasure(bpm);
        ts.setDurationForBeat(dbp);
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String note) {
        root = note;
    }

    public String getChordProgression() {
        return chordProgression.toString();
    }

    public void setChordProgression(String[] cp) {
        chordProgression = new ChordProgression(cp);
    }

    public double getLargestDivision() {
        return largestDivision;
    }
    public void setLargestDivision(int num) {
        largestDivision = num;
    }

    protected abstract String determineForm();
    protected abstract Pattern getBackingPattern();
    protected abstract Pattern getLeadPattern();

    // replicates a given string n times
    protected String replicate(String baseUnit, int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result = result.concat(baseUnit);
        }
        return result;
    }

    protected double getTotalSubBeats() {
        double totalSubBeats = largestDivision
                * measures
                * ts.getBeatsPerMeasure()
                / ts.getDurationForBeat();
        return totalSubBeats;
    }

    protected Pattern getRandomLeadPattern() {
        String songString = root
                + genNoteOctave(3, 5)
                + genNoteDuration(4)
                + " ";
        // currently hardcode what the ranges/numOptions are; will later add user ability to manipulate
        while (subBeatCount < getTotalSubBeats()) {
            songString = songString.concat(genRandNote()
                    + genNoteOctave(3, 5)
                    + genNoteDuration(4)
                    + " ");
        }
        Pattern lead = new Pattern(songString)
                .setVoice(voiceNum)
                .setTempo(tempo);
        voiceNum += 1;

        return lead;
    }
}
