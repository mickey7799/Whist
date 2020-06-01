// Implementation of the click strategy, used by a human interactive player
import ch.aplu.jcardgame.*;

public class ClickStrategy implements IPlayStrategy {
    Whist game;
    Hand hand;

    public ClickStrategy(Whist game, Hand hand) {
        this.game = game;
        this.hand = hand;

        // Set up human player for interaction
        CardListener cardListener = new CardAdapter() // Human Player plays card
        {
            public void leftDoubleClicked(Card card) {
                game.setSelected(card);
                hand.setTouchEnabled(false);
            }
        };
        hand.addCardListener(cardListener);
    }

    public Card play(Hand hand, Hand trick, int playerId) {
        if (!trick.isEmpty())
            game.setStatus("Player 0 double-click on card to follow.");
        else
            game.setStatus("Player 0 double-click on card to lead.");
        hand.setTouchEnabled(true);
        game.setSelected(null);
        while (null == game.getSelected())
            Whist.delay(100);
        return game.getSelected();
    }
}
