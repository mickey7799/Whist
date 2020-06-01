// Implementation of the random strategy
import ch.aplu.jcardgame.*;
import java.util.*;

public class RandomStrategy implements IPlayStrategy {

    Whist game;
    Random random;

    public RandomStrategy(Whist game, Random random) {
        this.game = game;
        this.random = random;
    }

    public Card play(Hand hand, Hand trick, int playerId) {
        hint(playerId);
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    void hint(int playerId) {
        game.setStatusText("Player " + playerId + " thinking...");
        Whist.delay(game.getThinkingTime());
    }
}