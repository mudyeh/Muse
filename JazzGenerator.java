/**
 * Created by Matt on 2/19/2017.
 */

import org.jfugue.theory.*;
import org.jfugue.pattern.*;
import org.jfugue.player.*;

import java.util.ArrayList;
import java.util.List;
public class JazzGenerator extends MusicGenerator {

    private List<String> instruments = new ArrayList<>();
    // if we start off the List with two instruments: the lead and the main backing
    // we can still maintain our operations from before of setting leadInstruments
    // but we also have more flexibility in our songs
    public void addInstrument(String instrument) {
        instruments.add(instrument);
    }

    public JazzGenerator(int measures, int tempo, String root,
                         String backingInstrument, String leadInstrument) {
        this.measures = measures;
        this.tempo = tempo;
        this.root = root;
        instruments.add(backingInstrument);
        instruments.add(leadInstrument);

        this.ts = new TimeSignature(4, 4);
        this.chordProgression = new ChordProgression("ii7 V I7");
    }

    public String shiftNote(String note, int shift) {
        int noteVal = new Note(note).getValue() + shift;
        Note newNote = new Note(noteVal);
        return newNote.getToneStringWithoutOctave((byte) noteVal);
    }

    @Override
     public String determineForm() {
        // first pick the last chord of the progression
        // then just return $0 $1 $2 $3
//        int randInt = StdRandom.uniform(0, 4);
//        String newChordProgression = chordProgression.toString();
//        if (randInt == 0) {
//            newChordProgression += (" " + root + "dom7");
//        } else if (randInt == 1) {
//            newChordProgression += (" " + root + "maj6");
//        } else if (randInt == 2) {
//            newChordProgression += (" " + shiftNote(root, 9) + "min7");
//        } else if (randInt == 3) {
//            newChordProgression += (" " + shiftNote(root, 5) + "dom7");
//        }
//        chordProgression = ChordProgression.fromChords(newChordProgression);
        return "$0 $1 $2";
//        return "$0 $1 $2";
     }

     @Override
     public Pattern getBackingPattern() {
         chordProgression = chordProgression
                 .allChordsAs(determineForm())
                 .eachChordAs("$_q. $_i + rh");
         Pattern backingPattern = chordProgression
                 .getPattern()
                 .setVoice(voiceNum)
                 .setTempo(tempo)
                 .setInstrument(instruments.get(0));
         voiceNum+=1;
         return backingPattern;
     }

     public Pattern getLeadPattern() {
         Note iiRoot = new Note(shiftNote(root, 2));
         Note VRoot = new Note(shiftNote(root, 7));

         Intervals scale1 = MAJOR.setRoot(root);
         Intervals scale2 = DORIAN_MODE.setRoot(iiRoot);
         Intervals scale3 = MIXOLYDIAN_MODE.setRoot(VRoot);

         Intervals[] scales = {scale1, scale2, scale3};

         return super.getLeadPattern(scales).setInstrument(instruments.get(1));
     }

     public static void main(String[] args) {
         JazzGenerator j = new JazzGenerator(24, 140, "A",
                 "piano", "electric_jazz_guitar");

         Pattern rhythm = j.getBackingPattern();
         Pattern lead = j.getLeadPattern();
         System.out.println(lead);
         new Player().play(rhythm.repeat(8), lead);
     }
}
