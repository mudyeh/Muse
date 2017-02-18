/**
 * Created by Matt on 2/17/2017.
 * Creates "bluesy" music using blues theory
 */
import org.jfugue.theory.*; // sloppy wild card import
import org.jfugue.pattern.*;
import org.jfugue.player.*;

//import javax.sound.midi.Instrument;

public class BluesGenerator {
    public int measuresInForm;
    public int tempo;
    public String backingInstrument;
    public String leadInstrument;
    public String root;

    public static int voiceNum = 0;
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
    public String determineForm() {
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

    /* The more "computer music" melody generator:
     * Randomly selects diatonic notes for melody */
    public Pattern getRandomLeadPattern() {
        return null;
    }

    /* The more "human" melody generator:
    * More heavily employs blues theory */
    public Pattern getBluesLeadPattern() {
        return null;
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
    // replicates a given string n times
    private String replicate(String baseUnit, int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result = result.concat(baseUnit);
        }
        return result;
    }

    public static void main(String[] args) {
        BluesGenerator b = new BluesGenerator(12, 140, "G",
                "electric_jazz_guitar", "overdriven_guitar");
//        System.out.print(b.determineForm()); //do more extensive testing later!!!

        Player p = new Player();
        p.play(b.getBackingPattern());
//        Pattern pat = new Intervals("1 3 5").setRoot("D").getPattern();
//        System.out.print(pat);
    }
}
