// Implentation of strategy factory for creating all kinds of strategies
import ch.aplu.jcardgame.*;
import java.util.Random;

public class PlayStrategryFactory {
    
    public static PlayStrategryFactory strategyFactory = new PlayStrategryFactory();

    static public PlayStrategryFactory getInstance() {
        if (strategyFactory == null) {            
            strategyFactory = new PlayStrategryFactory();
        }
        return strategyFactory;
    }

    public IPlayStrategy getClickStrategy(Whist game, Hand hand) {
        return new ClickStrategy(game, hand);
    }

    public IPlayStrategy getRandomStrategy(Whist game, Random random) {
        return new RandomStrategy(game, random);
    }

    public IPlayStrategy getLegalStrategy(Whist game, Random random) {
        return new LegalStrategy(game, random);
    }

    public IPlayStrategy getSmartStrategy(Whist game) {
        return new SmartStrategy(game);
    }
}