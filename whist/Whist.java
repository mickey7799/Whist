// Whist.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.util.*;

@SuppressWarnings("serial")
public class Whist extends CardGame {

    public enum Suit {
        SPADES, HEARTS, DIAMONDS, CLUBS
    }

    public enum Rank {
        // Reverse order of rank importance (see rankGreater() below)
        // Order of cards is tied to card images
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
    }

    final String trumpImage[] = { "bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif" };

    static Random random = new Random(30006);

    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    private final String version = "1.0";
    public final int nbPlayers = 4;
    public int nbStartCards = 13;
    public int winningScore = 11;
    public int nbInteractive = 1;
    public int nbRandom = 3;
    public int nbLegal = 0;
    public int nbSmart = 0;
    public Suit trumps;
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = { new Location(350, 625), new Location(75, 350), new Location(350, 75),
            new Location(625, 350) };
    private final Location[] scoreLocations = { new Location(575, 675), new Location(25, 575), new Location(575, 25),
            new Location(650, 575) };
    private Actor[] scoreActors = { null, null, null, null };
    private final Location trickLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private int thinkingTime = 2000;
    private Hand[] hands;
    private IPlayStrategy[] strategies;
    private Location hideLocation = new Location(-500, -500);
    private Location trumpsActorLocation = new Location(50, 50);
    private boolean enforceRules = false;

    public void setStatus(String string) {
        setStatusText(string);
    }

    private int[] scores = new int[nbPlayers];

    Font bigFont = new Font("Serif", Font.BOLD, 36);

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
            scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private Card selected;

    private void initRound() {
        hands = deck.dealingOut(nbPlayers, nbStartCards); // Last element of hands is leftover cards; these are ignored
        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, true);
        }

        // Set up strategies
        initStrategies();

        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            hands[i].setView(this, layouts[i]);
            hands[i].setTargetArea(new TargetArea(trickLocation));
            hands[i].draw();
        }
        // for (int i = 1; i < nbPlayers; i++) // This code can be used to visually hide
        // the cards in a hand (make them face down)
        // hands[i].setVerso(true);
        // End graphics
    }

    private Optional<Integer> playRound() { // Returns winner, if any
        // Select and display trump suit
        trumps = randomEnum(Suit.class);
        final Actor trumpsActor = new Actor("sprites/" + trumpImage[trumps.ordinal()]);
        addActor(trumpsActor, trumpsActorLocation);
        // End trump suit
        Hand trick;
        int winner;
        Card winningCard;
        Suit lead;
        int nextPlayer = random.nextInt(nbPlayers); // randomly select player to lead for this round
        for (int i = 0; i < nbStartCards; i++) {
            trick = new Hand(deck);
            selected = strategies[nextPlayer].play(hands[nextPlayer], trick, nextPlayer);
            // Lead with selected card
            trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
            trick.draw();
            selected.setVerso(false);
            // No restrictions on the card being lead
            lead = (Suit) selected.getSuit();
            selected.transfer(trick, true); // transfer to trick (includes graphic effect)
            winner = nextPlayer;
            winningCard = selected;
            // End Lead
            for (int j = 1; j < nbPlayers; j++) {
                if (++nextPlayer >= nbPlayers)
                    nextPlayer = 0; // From last back to first
                selected = strategies[nextPlayer].play(hands[nextPlayer], trick, nextPlayer);
                // Follow with selected card
                trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
                trick.draw();
                selected.setVerso(false); // In case it is upside down
                // Check: Following card must follow suit if possible
                if (selected.getSuit() != lead && hands[nextPlayer].getNumberOfCardsWithSuit(lead) > 0) {
                    // Rule violation
                    String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
                    System.out.println(violation);
                    if (enforceRules)
                        try {
                            throw (new BrokeRuleException(violation));
                        } catch (BrokeRuleException e) {
                            e.printStackTrace();
                            System.out.println("A cheating player spoiled the game!");
                            System.exit(0);
                        }
                }
                // End Check
                selected.transfer(trick, true); // transfer to trick (includes graphic effect)
                System.out.println("winning: suit = " + winningCard.getSuit() + ", rank = " + winningCard.getRankId());
                System.out.println(" played: suit = " + selected.getSuit() + ", rank = " + selected.getRankId());
                if ( // beat current winner with higher card
                (selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
                // trumped when non-trump was winning
                        (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
                    System.out.println("NEW WINNER");
                    winner = nextPlayer;
                    winningCard = selected;
                }
                // End Follow
            }
            delay(600);
            trick.setView(this, new RowLayout(hideLocation, 0));
            trick.draw();
            nextPlayer = winner;
            setStatusText("Player " + nextPlayer + " wins trick.");
            scores[nextPlayer]++;
            updateScore(nextPlayer);
            if (winningScore == scores[nextPlayer])
                return Optional.of(nextPlayer);
        }
        removeActor(trumpsActor);
        return Optional.empty();
    }

    // Initialize strategies array based on player types
    private void initStrategies() {
        PlayStrategryFactory factory = PlayStrategryFactory.getInstance();
        strategies = new IPlayStrategy[nbPlayers];
        int i = 0;
        while (i < nbPlayers) {
            for (int j = 0; j < nbInteractive; j++) {
                strategies[i] = factory.getClickStrategy(this, hands[i]);
                i++;
            }
            for (int j = 0; j < nbRandom; j++) {
                strategies[i++] = factory.getRandomStrategy(this, random);
            }
            for (int j = 0; j < nbLegal; j++) {
                strategies[i++] = factory.getLegalStrategy(this, random);
            }
            for (int j = 0; j < nbSmart; j++) {
                strategies[i++] = factory.getSmartStrategy(this);
            }
        }
    }

    // Codes extracted from the original constructor. As there exists two constructors now,
    // the common logic should be extracted into one function
    private void startGame() {
        setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScore();
        Optional<Integer> winner;
        do {
            initRound();
            winner = playRound();
        } while (!winner.isPresent());
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText("Game over. Winner is player: " + winner.get());
        refresh();
    }

    // Constructor with a specified property file
    public Whist(String propertiesFilePath) {
        super(700, 700, 30);
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFilePath));
            nbStartCards = Integer.parseInt(properties.getProperty("nbStartCards"));
            winningScore = Integer.parseInt(properties.getProperty("winningScore"));
            int seed = Integer.parseInt(properties.getProperty("seed"));
            random = new Random(seed);
            nbInteractive = Integer.parseInt(properties.getProperty("interactive"));
            nbRandom = Integer.parseInt(properties.getProperty("random"));
            nbLegal = Integer.parseInt(properties.getProperty("legal"));
            nbSmart = Integer.parseInt(properties.getProperty("smart"));
            String strThinkingTime = properties.getProperty("thinkingTime");
            if (strThinkingTime != null) {
                thinkingTime = Integer.parseInt(strThinkingTime);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        startGame();
    }

    // Default constructor
    public Whist() {
        super(700, 700, 30);
        startGame();
    }

    public void setSelected(Card card) {
        selected = card;
    }

    public Card getSelected() {
        return selected;
    }

    public int getThinkingTime() {
        return thinkingTime;
    }

    public static void main(String[] args) {
        // System.out.println("Working Directory = " + System.getProperty("user.dir"));
        if (args.length > 0) {
            new Whist(args[0]);
        } else {
            new Whist();
        }
    }

}
