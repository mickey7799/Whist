// Implementation of the legal strategy where all cards are legally played
import java.util.*;
import ch.aplu.jcardgame.*;

public class LegalStrategy extends RandomStrategy {
    public LegalStrategy(Whist game, Random random) {
        super(game, random);
    }

    public Card play(Hand hand, Hand trick, int playerId) {
        if (trick.isEmpty())
            return super.play(hand, trick, playerId);
        Card lead = trick.get(0);
        ArrayList<Card> sameSuitList = hand.getCardsWithSuit((Whist.Suit) lead.getSuit());
        if (sameSuitList.size() > 0) {
            hint(playerId);
            int x = random.nextInt(sameSuitList.size());
            return sameSuitList.get(x);
        }
        return super.play(hand, trick, playerId);
    }
}