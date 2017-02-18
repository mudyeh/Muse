/**
 * Created by Matt on 2/17/2017.
 * Short JFugue Demo
 */
import org.jfugue.player.*;
import org.jfugue.pattern.*;

public class HelloWorld {
    public static void main(String[] args) {
        Player p = new Player();
        p.play("a b c d e f g");

        // multiple voices example
        Pattern p1 = new Pattern("Eq Ch. | Eq Ch. | Dq Eq Dq Cq").setVoice(0).setInstrument("Piano");
        Pattern p2 = new Pattern("Rw     | Rw     | GmajQQQ  CmajQ").setVoice(1).setInstrument("Flute");
        Player player = new Player();
        player.play(p1, p2);
    }
}
