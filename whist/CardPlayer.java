import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public abstract class CardPlayer {
	Card selected;
	abstract Card selectCard(Hand hand);
	
}
