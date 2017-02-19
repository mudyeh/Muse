/**
 * Created by Matt on 2/17/2017.
 * Creates "bluesy" music using blues theory
 */
import org.jfugue.theory.*;
import org.jfugue.pattern.*;
import org.jfugue.player.*;

public class BluesGenerator extends MusicGenerator {
    /* instrumentation is unique to each particular song
    more instruments can be added here */
    private String backingInstrument;
    private String leadInstrument;

    // ideally, a GUI would allow users to set these values
    public BluesGenerator(int measures, int tempo, String root,
                          String backingInstrument, String leadInstrument) {
        this.measures = measures;
        this.tempo = tempo;
        this.root = root;
        this.backingInstrument = backingInstrument;
        this.leadInstrument = leadInstrument;

        // automatically assigned features of the blues
        ts = new TimeSignature(4, 4);
        chordProgression = new ChordProgression("I IV V");
    }

    /* Either uses common blues forms (8 bar, 12 bar, 16 bar)
    to return full chord progression or randomly creates
    a chord progression (more interesting) */
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

    /* Backing patterns are hard to generalize because of the different
    * distribution of notes, and because of the different
    * "base units" of the chord progressions */
    @Override
    public Pattern getBackingPattern() {
        chordProgression = chordProgression.setKey(root)
                .distribute("7%6")
                .allChordsAs(determineForm());
        Pattern root = chordProgression.eachChordAs(replicate("$0ia60 ", 8))
                .getPattern()
                .setVoice(voiceNum)
                .setTempo(tempo)
                .setInstrument(backingInstrument);
        voiceNum+=1;
        Pattern top = chordProgression.eachChordAs(replicate("$2ia80 $2ia80 $3ia90 $2ia80 ", 2))
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
    * This version assumes major blues */
    public Pattern getLeadPattern() {
        Intervals scale1 = MIXOLYDIAN_MODE.setRoot(root);
        Intervals scale2 = MAJOR_PENTATONIC.setRoot(root);
        Intervals scale3 = MAJOR_BLUES.setRoot(root);

        Intervals[] scales = {scale1, scale2, scale3};

        return super.getLeadPattern(scales).setInstrument(leadInstrument);
    }

    public static void main(String[] args) {
        // Hardcoded example
        BluesGenerator b = new BluesGenerator(16, 140, "Bb",
                "electric_jazz_guitar", "cello");

        Pattern rhythm = b.getBackingPattern();
        Pattern lead = b.getLeadPattern();
        new Player().play(lead, rhythm);
    }
}
