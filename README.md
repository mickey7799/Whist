# Whist

### Summary

Whist is a simple-version of Whist card game that built with Java. 

It implements the JGameGrid framework and can play against two types of NPC players (Legal/Smart):
- Legal NPC: plays a random selection from all cards that can legally (consistent with the rules) be played
- Smart NPC: a player that records all relevant information and makes a reasonable, legal choice based on that information.

The rules are as the following:
- Played with a standard fifty-two card deck with four suits and thirteen ranks in each suit
- The game involves four players who play independently. The first player to score twenty points wins and the game stopped
- The game starts with a hand of thirteen cards being dealt to each player and a trump suit being randomly selected (showed on the up left corner)
- Every trick, the player taking the lead can play any card they wish from their hand to the centre
- Following players must play a card of the same suit as that lead if they have one. If not, they may play any card they wish
- Once every player has played one card, the winner is the player who has played the highest card of the trump suit if any, or the highest card of the lead suit if not
- The winner receives one point for winning the trick, and then leads for the next round starting a new trick

### Motivation

The purpose of this project was to learn how to design a functional system using the GRASP principles and GoF design patterns to ensure future scalability.

### Getting Started

Clone/download the repository on your local machine.

```
javac *.java -cp dist/lib/JGameGrid.jar:. 
java -cp dist/lib/JGameGrid.jar:. Whist
java -cp dist/lib/JGameGrid.jar:. Whist ../smart.properties
```

### Features

**Design pattern:**
- Strategy pattern (to design varying but related strategies)
- Factory pattern (better cohesion and low coupling)
- Singleton pattern (provides a single access point through global visibility)

#### 1. Game Configuration: 
Use properties files to select different settings of games:
- “original.properties”: one interactive player (0) and three random NPCs
- “legal.properties”: four legal NPCs, with nbStartCards = 4 and winningScore = 6
- “smart.properties": one interactive player (0), one smart NPC (1), and two random NPCs

#### 2. Implementing Smart Player Algorithm: 
The lead and follow should have different strategies: 
- The "Lead" strategies: When the player is leading a new trick, a high rank card is expected to play to ensure there is a chance for the player to win and continue the leading
- The "Follow" strategies: If the player determines the lost of the trick is unavoidable, the player should discard the most useless card – the card of the lowest rank. Otherwise, a high rank card is played to guarantee a potential winning
There are four cases when the follow strategy is applied:

1. The trick contains trumps cards and the player still owns the cards of the lead suit: It is clear that this is a lost trick. The player should discard a useless card.

Note: if the lead suit happens to be the trumps suit. Case 2 will be selected

2. The trick does not contain any trumps card and the player still owns the cards of the lead suit: 
a. If the player is at the end of the trick, a card greater than winning card is played. 
b. Otherwise a highest rank card is played. Note: if the best card cannot win the trick, the discard strategy is applied.
 
3. The trick contain trumps cards and the player owns no cards of the lead suit: A trumps card greater than the current winning card will be played. If there exists no such card, the discard strategy is applied.

4. The trick does not contain any trumps card and the player owns no cards of the lead suit: It is clear that the player should play a trumps card to increase the winning probability. But if the player is out of trumps cards, the discard strategy is applied.

