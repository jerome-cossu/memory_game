import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }
    
    String[] cardlist = {
        "darkness",
        "double",
        "fairy",
        "fighting",
        "fire",
        "grass",
        "lightning",
        "metal",
        "psychic",
        "water"
    };
    
    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;
    int maxErrors = 5; // Nombre maximum d'erreurs autorisées

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon; 

    int boardWidth = columns * cardWidth;
    int boardHeight = rows * cardHeight;

    JFrame frame = new JFrame("Pokemon Match Cards");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartGameButton = new JButton();

    int errorCount = 0;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;
    JButton cardSelected;
    JButton card2Selected;

    MatchCards() {
        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setText("Error : " + Integer.toString(errorCount) + "/" + maxErrors);

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++){
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    if (!gameReady || errorCount >= maxErrors) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();
                    if (tile.getIcon() == cardBackImageIcon){
                        if (cardSelected == null){
                            cardSelected = tile;
                            int index = board.indexOf(cardSelected);
                            cardSelected.setIcon(cardSet.get(index).cardImageIcon);
                        }
                        else if (card2Selected == null){
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);
                            
                            if (cardSelected.getIcon() != card2Selected.getIcon()){
                                errorCount += 1;
                                textLabel.setText("Error : " + Integer.toString(errorCount) + "/" + maxErrors);
                                
                                // Vérifier si le joueur a atteint le nombre maximum d'erreurs
                                if (errorCount >= maxErrors) {
                                    gameOver();
                                } else {
                                    hideCardTimer.start();
                                }
                            }
                            else {
                                cardSelected = null;
                                card2Selected = null;
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        restartGameButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartGameButton.setText("Restart Game");
        restartGameButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartGameButton.setFocusable(false);
        restartGameButton.setEnabled(false);
        restartGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady) {
                    return;
                }
                resetGame();
            }
        });
        restartGamePanel.add(restartGameButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();
    }

    void gameOver() {
        // Afficher toutes les cartes
        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }
        textLabel.setText("Game Over! Errors: " + errorCount + "/" + maxErrors);
    }

    void resetGame() {
        gameReady = false;
        restartGameButton.setEnabled(false);
        cardSelected = null;
        card2Selected = null;
        shuffleCards();
        for(int i = 0; i < board.size(); i++){
            board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }
        errorCount = 0;
        textLabel.setText("Error : " + Integer.toString(errorCount) + "/" + maxErrors);
        hideCardTimer.start();
    }

    void setupCards() {
        cardSet = new ArrayList<Card>();
        for (String cardName : cardlist) {
            Image cardImg = new ImageIcon(getClass().getResource("./img/" + cardName + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);

        Image cardBackImg = new ImageIcon(getClass().getResource("./img/back.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }

    void hideCards() {
        if (gameReady && cardSelected != null && card2Selected != null) {
            cardSelected.setIcon(cardBackImageIcon);
            cardSelected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        }
        else {       
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartGameButton.setEnabled(true);
        }
    }
}