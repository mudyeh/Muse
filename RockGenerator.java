/**
 * Created by Matt on 2/18/2017.
 * Rock music generator
 * Not to be confused with rock n roll
 */
import org.jfugue.theory.*;
import org.jfugue.pattern.*;
import org.jfugue.player.*;

import javax.annotation.PostConstruct;
import java.util.List;

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
        this.chordProgression = new ChordProgression("I V vi IV");
    }

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

    @Override
    public Pattern getBackingPattern() {
        chordProgression = chordProgression
                .setKey(root)
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

    @Override
    public Pattern getLeadPattern() {
        return null;
    }

    @Override
    public Pattern getRandomLeadPattern() {
        return super.getRandomLeadPattern().setInstrument(leadInstrument);
    }

    public static void main(String[] args) {
        RockGenerator r = new RockGenerator(24, 140, "A",
                "electric_muted_guitar", "distortion_guitar");

        Pattern rhythm = r.getBackingPattern();
        Pattern lead = r.getRandomLeadPattern();
        new Player().play(rhythm.repeat(6), lead);
    }
}
