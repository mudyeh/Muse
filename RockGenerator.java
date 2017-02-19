/**
 * Created by Matt on 2/18/2017.
 * Rock music generator
 * Not to be confused with rock n roll
 */
import org.jfugue.theory.*;
import org.jfugue.pattern.*;
import org.jfugue.player.*;

public class RockGenerator extends MusicGenerator {
    private String backingInstrument;
    private String leadInstrument;

    public RockGenerator(int measures, int tempo, String root,
                          String backingInstrument, String leadInstrument) {
        this.measures = measures;
        this.tempo = tempo;
        this.root = root;
        this.backingInstrument = backingInstrument;
        this.leadInstrument = leadInstrument;

        this.ts = new TimeSignature(4, 4);
        /* Note that the following means the root always refers
         to the tonic in the MAJOR scale
         (Why do this? It makes it easier to choose scales
         based off the limitations of JFugue) */
        this.chordProgression = new ChordProgression("I V vi IV").setKey(root);
    }

     /* randomly determines the 4 bar form
     all are versions of the standard "I V vi IV" */
    @Override
    public String determineForm() {
        int randInt = StdRandom.uniform(0, 5);
        if (randInt < 2) {
            return "$0 $1 $2 $3";
        } else if (randInt <= 3) {
            return "$2 $3 $0 $1";
        } else {
            int numChords = chordProgression.toStringArray().length;
            String pattern = "";
            for (int i = 0; i < numChords; i++) {
                int scaleDegree = StdRandom.uniform(numChords);
                pattern = pattern.concat("$" + scaleDegree + " ");
            }
            return pattern;
        }
    }

    /* This looks really similar to the code in BluesGenerator's
    version of this function...it probably has a way to be generalized */
    @Override
    public Pattern getBackingPattern() {
        chordProgression = chordProgression
                .allChordsAs(determineForm())
                .eachChordAs(replicate("$_ia30 ", 8));
        Pattern backingPattern = chordProgression
                .getPattern()
                .setVoice(voiceNum)
                .setTempo(tempo)
                .setInstrument(backingInstrument);
        voiceNum+=1;
        return backingPattern;
    }

    public Pattern getLeadPattern() {
        // MIDI and interval theory trickery to get the relative minor root
        Note minorRoot = new Note(new Note(root).getValue() - 3);

        // what if we made an array of Intervals? hmm...
        Intervals scale1 = MAJOR_PENTATONIC.setRoot(root);
        Intervals scale2 = MINOR_PENTATONIC.setRoot(minorRoot);
        Intervals scale3 = MAJOR.setRoot(root);
        Intervals scale4 = MINOR.setRoot(minorRoot);
        // minor blues and other "fancier" scales sound more interesting
        // but they also have a tendency to create really dissonant intervals
//        Intervals scale5 = MINOR_BLUES.setRoot(minorRoot);

        Intervals[] scales = {scale1, scale2, scale3, scale4};

        return super.getLeadPattern(scales).setInstrument(leadInstrument);
    }

    @Override
    public Pattern getRandomLeadPattern() {
        return super.getRandomLeadPattern().setInstrument(leadInstrument);
    }

    public static void main(String[] args) {
        RockGenerator r = new RockGenerator(16, 140, "A",
                "electric_muted_guitar", "distortion_guitar");

        Pattern rhythm = r.getBackingPattern();
        Pattern lead = r.getLeadPattern();
        System.out.println(lead);
        new Player().play(rhythm.repeat(r.getMeasures() / 4), lead);
 }
}
