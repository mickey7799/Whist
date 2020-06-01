import ch.aplu.jcardgame.*;

public interface IPlayStrategy {
	public Card play(Hand hand, Hand trick, int playerId);
}
