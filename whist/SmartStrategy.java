// Implementation of a smart strategy
import ch.aplu.jcardgame.*;
import java.util.*;

public class SmartStrategy implements IPlayStrategy {

    Whist game;

    SmartStrategy(Whist game) {
        this.game = game;
    }

    public Card play(Hand hand, Hand trick, int playerId) {
        game.setStatusText("Player " + playerId + " thinking...");
        Whist.delay(game.getThinkingTime());

        Whist.Suit suit;
        if (trick.isEmpty()) {
            return lead(hand);
        } else {
            suit = (Whist.Suit) trick.get(0).getSuit();
            boolean trickContainsTrumps = containsTrumps(trick);
            boolean handContainsSuit = hasSuit(hand, suit);
            Card winningCard = getWinningCard(trick);
            if ((!trickContainsTrumps || suit == game.trumps) && handContainsSuit) {
                if (trick.getNumberOfCards() == game.nbPlayers - 1) {
                    // Select the minium high rank card if this is the last player for the trick
                    Card card = getMin(hand.getCardsWithSuit(suit), winningCard);
                    if (card == null)
                        card = getMin(hand.getCardsWithSuit(suit));
                    return card;
                } else {
                    // Otherwise play the high rank card of the suit if there is a chance to win
                    Card maxCard = getMax(hand.getCardsWithSuit(suit));
                    if (game.rankGreater(maxCard, winningCard)) {
                        return maxCard;
                    } else {
                        return getMin(hand.getCardsWithSuit(suit));
                    }
                }
            } else if (trickContainsTrumps && handContainsSuit) {
                return getMin(hand.getCardsWithSuit(suit));
            } else if (!trickContainsTrumps && !handContainsSuit) {
                Card card = getMin(hand.getCardsWithSuit(game.trumps));
                if (card == null)
                    card = discard(hand);
                return card;
            } else if (trickContainsTrumps && !handContainsSuit) {
                Card card = getMin(hand.getCardsWithSuit(game.trumps), winningCard);
                if (card == null)
                    card = discard(hand);
                return card;
            }

        }
        return discard(hand);
    }

    // Check if the trick contains at least one trumps card
    private boolean containsTrumps(Hand trick) {
        for (int i = 0; i < trick.getNumberOfCards(); i++) {
            Card card = trick.get(i);
            if (card.getSuit() == game.trumps) {
                return true;
            }
        }
        return false;
    }

    // Check if the hand contains at least one card of the suit
    private boolean hasSuit(Hand hand, Whist.Suit suit) {
        ArrayList<Card> list = hand.getCardsWithSuit(suit);
        return list.size() > 0;
    }

    // Get the lowest rank card in the cards list. Null is returned if the list is empty
    private Card getMin(ArrayList<Card> list) {
        if (list.size() == 0)
            return null;
        return list.get(list.size() - 1);
    }

    // Get the lowest rank card in the list, that is greater than the card.
    // If list is empty or all cards in the list is less than the card, null is returned
    private Card getMin(ArrayList<Card> list, Card card) {
        if (list.size() == 0)
            return null;
        int i = 0;
        while (i < list.size() && game.rankGreater(list.get(i), card))
            i++;
        if (i == 0)
            return null;
        else
            return list.get(i - 1);
    }

    // Get the highest rank card in the list. Null is returned if the list is empty
    private Card getMax(ArrayList<Card> list) {
        if (list.size() == 0)
            return null;
        return list.get(0);
    }

    // Get the winning card in the trick
    private Card getWinningCard(Hand trick) {
        if (trick.getNumberOfCards() == 0)
            return null;
        Card winningCard = trick.get(0);
        for (int i = 1; i < trick.getNumberOfCards(); i++) {
            Card card = trick.get(i);
            if (card.getSuit() == winningCard.getSuit() && game.rankGreater(card, winningCard)
                    || card.getSuit() == game.trumps && winningCard.getSuit() != game.trumps) {
                winningCard = card;
            }
        }
        return winningCard;
    }

    // Discard a most useless(lowest rank) card. The trumps suit should be specially considered
    private Card discard(Hand hand) {
        hand.sort(Hand.SortType.RANKPRIORITY, false);
        Card card;
        int i = 0;
        for (i = hand.getNumberOfCards() - 1; i >= 0; i--) {
            if (hand.get(i).getSuit() != game.trumps)
                break;
        }
        if (i >= 0)
            card = hand.get(i);
        else
            // All cards in hand are trumps card
            card = hand.get(hand.getNumberOfCards() - 1);
        hand.sort(Hand.SortType.SUITPRIORITY, false);
        return card;
    }

    // Play a highest rank card to lead. The trumps suit should be specially considered
    private Card lead(Hand hand) {
        hand.sort(Hand.SortType.RANKPRIORITY, false);
        Card card;
        int i = 0;
        for (i = 0; i < hand.getNumberOfCards(); i++) {
            if (hand.get(i).getSuit() != game.trumps)
                break;
        }
        if (i < hand.getNumberOfCards())
            card = hand.get(i);
        else
            // All cards in hand are trumps card
            card = hand.get(0);
        hand.sort(Hand.SortType.SUITPRIORITY, false);
        return card;
    }
}