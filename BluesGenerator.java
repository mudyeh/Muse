/**
 * Created by Matt on 2/17/2017.
 * Creates "bluesy" music using blues theory
 */
import org.jfugue.theory.*;
import org.jfugue.pattern.*;
import org.jfugue.player.*;
import java.util.List;
//import javax.sound.midi.Instrument;

public class BluesGenerator extends MusicGenerator {
    // how do you generalize instrumentation?
    private String backingInstrument;
    private String leadInstrument;

    public BluesGenerator(int measures, int tempo, String root,
                          String backingInstrument, String leadInstrument) {
        this.measures = measures;
        this.tempo = tempo;
        this.root = root;
        this.backingInstrument = backingInstrument;
        this.leadInstrument = leadInstrument;
        this.ts = new TimeSignature(4, 4);
        this.chordProgression = new ChordProgression("I IV V");
    }

    /* Either uses common blues forms (8 bar, 12 bar, 16 bar) to return full chord progression
    or randomly creates a chord progression (more interesting) */
    @Override
    public String determineForm() {
        if (measures == 12) {
            return "$0 $0 $0 $0 $1 $1 $0 $0 $2 $1 $0 $2";
        } else if (measures == 8) {
            return "$0 $0 $1 $1 $0 $2 $1 $2";
        } else if (measures == 16) {
            return "$0 $0 $0 $0 $1 $1 $0 $0 $2 $1 $2 $1 $2 $1 $0 $0";
        } else {
            String pattern = "";
            for (int i = 0; i < measures; i++) {
                int scaleDegree = StdRandom.uniform(3);
                pattern = pattern.concat("$" + scaleDegree + " ");
            }
            return pattern;
        }
    }

    @Override
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

    /* The more "computer music" melody generator:
     * Randomly selects notes from chromatic scale for melody
     * Obviously doesn't sound good */
    @Override
    public Pattern getRandomLeadPattern() {
        return super.getRandomLeadPattern().setInstrument(leadInstrument);
    }

    /* The more "human" melody generator:
    * More heavily employs blues theory
    * First iteration assumes major blues */
    @Override
    public Pattern getLeadPattern() {
        Intervals scale1 = MAJOR.setRoot(root);
        Intervals scale2 = MAJOR_PENTATONIC.setRoot(root);
        Intervals scale3 = MAJOR_BLUES.setRoot(root);
        Intervals curScale = scale3;
        String songString = root
                +genNoteOctave(4, 6)
                +genNoteDuration(4)
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
                curScale.setRoot(root + genNoteOctave(3, 5));
            }

            // Convert the set of intervals into a List of Notes
            List<Note> curScaleNotes = curScale.getNotes();

            // Form the randomly chosen note
            songString = songString.concat(
                    curScaleNotes.get(StdRandom.uniform(curScale.size()))
                    + genNoteDuration(4)
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

    public static void main(String[] args) {
        // Hardcoded example
        BluesGenerator b = new BluesGenerator(16, 140, "C",
                "electric_jazz_guitar", "overdriven_guitar");

        Pattern rhythm = b.getBackingPattern();
        Pattern lead = b.getLeadPattern();
        new Player().play(rhythm.add(lead));
    }
}
