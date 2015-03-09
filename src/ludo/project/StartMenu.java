
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ludo.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Displays the startmenu
 * @author Kaj Hejlesen
 */
class StartMenu extends JPanel {
    Game game;
    State state;
    Player[] players;
    JFrame frame;
    // declaring all elements as instance variables to use them in inner classes
    JCheckBox[] playerInGame = new JCheckBox[4];
    JRadioButton[] human = new JRadioButton[4];
    JRadioButton[] easy = new JRadioButton[4];
    JRadioButton[] normal = new JRadioButton[4];
    JRadioButton[] hard = new JRadioButton[4];
    JComboBox<String>[] colors = new JComboBox[4];
    JCheckBox starsAndGlobes;
    JRadioButton classicTheme;
    JRadioButton spaceTheme;

    public StartMenu (State state, Player[] players) {
        this.game = state.getLudoGame();
        this.state = state;
        this.players = players;
        initUI();
    }
    
    /**
     * Creates and fills the JPanels to hold the options
     * @return main JPanel of the frame
     */
    public JPanel selectPlayers() {
        JPanel pane = new JPanel();     // creates main JPanel
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        // creates the header
        JLabel head = new JLabel("New Game");
        head.setForeground(Color.red.darker());
        head.setFont(new Font("Serif", Font.ROMAN_BASELINE, 25));

        // moving the label into a JPanel to center it properly
        JPanel topPane = new JPanel();
        topPane.add(head);


        // setting the two buttons in the bottom
        JPanel bottomPane = new JPanel();
        JButton quitGame = new JButton("Exit");
        JButton startGame = new JButton("Start Game!");
        quitGame.setName("you pressed the mighty quit-button!");

        bottomPane.add(quitGame);
        bottomPane.add(Box.createRigidArea(new Dimension(10, 0)));
        bottomPane.add(startGame);

        // setting a buttongroup for each player
        ButtonGroup player1 = new ButtonGroup();
        ButtonGroup player2 = new ButtonGroup();
        ButtonGroup player3 = new ButtonGroup();
        ButtonGroup player4 = new ButtonGroup();

        // colors supported in the game
        String[] colorsAvailable = {"Green","Red","Blue","Yellow","Purple","Grey","Cyan","Pink","White","Black"};
        
        // creates buttons for each of the 4 players and aligning them in the grid
        for (int i = 0; i < 4; i++) {
            playerInGame[i] = new JCheckBox("Player " + (i+1), true);
            human[i] = new JRadioButton();
            easy[i] = new JRadioButton();
            normal[i] = new JRadioButton();
            hard[i] = new JRadioButton();
            colors[i] = new JComboBox<>(colorsAvailable);
            colors[i].setSelectedIndex(i);
            human[i].setHorizontalAlignment(JRadioButton.CENTER);
            easy[i].setHorizontalAlignment(JRadioButton.CENTER);
            normal[i].setHorizontalAlignment(JRadioButton.CENTER);
            hard[i].setHorizontalAlignment(JRadioButton.CENTER);
            // setting default players
            if (i == 0)
                human[i].setSelected(true);
            else
                normal[i].setSelected(true);
        }

        // Buttongroups in array doesn't seem to work, so doing this manually
        player1.add(human[0]);
        player1.add(easy[0]);
        player1.add(normal[0]);
        player1.add(hard[0]);

        player2.add(human[1]);
        player2.add(easy[1]);
        player2.add(normal[1]);
        player2.add(hard[1]);

        player3.add(human[2]);
        player3.add(easy[2]);
        player3.add(normal[2]);
        player3.add(hard[2]);

        player4.add(human[3]);
        player4.add(easy[3]);
        player4.add(normal[3]);
        player4.add(hard[3]);

        int numColumns = 6;
        int numRows = 5;
        JPanel playerPane = new JPanel();   // creates the JPanel holding the grid with the player selection
        playerPane.setLayout(new GridLayout(numRows,numColumns));

        // Sets the header and centering it
        String[] header = {"", "human","AI (easy)","AI (normal)","AI (hard)","Color"};
        JLabel[] columns = new JLabel[numColumns];
        for (int i = 0; i < numColumns; i++) {
            columns[i] = new JLabel(header[i]);
            columns[i].setHorizontalAlignment(SwingConstants.CENTER);
            playerPane.add(columns[i]);
        }

        // adds the buttons to the JPanel
        for (int i = 0; i < 4; i++) {
            playerPane.add(playerInGame[i]);
            playerPane.add(human[i]);
            playerPane.add(easy[i]);
            playerPane.add(normal[i]);
            playerPane.add(hard[i]);
            playerPane.add(colors[i]);
        }


        // creating the other options, hold by optionPane in a borderLayout
        JPanel optionPane = new JPanel();
        optionPane.setLayout(new BorderLayout());

        JPanel holdOptionsPane = new JPanel();
        holdOptionsPane.setLayout(new BoxLayout(holdOptionsPane, BoxLayout.Y_AXIS));

        JLabel themeText = new JLabel("Use theme:");
        ButtonGroup themes = new ButtonGroup();
        classicTheme = new JRadioButton("Classic", true);
        spaceTheme = new JRadioButton("Space");

        themes.add(classicTheme);
        themes.add(spaceTheme);

        JPanel option1 = new JPanel();
        option1.setLayout(new BorderLayout());
        starsAndGlobes = new JCheckBox("Stars and Globes", true);
        option1.add(starsAndGlobes);

        JPanel option2 = new JPanel();

        option2.add(themeText);
        option2.add(classicTheme);
        option2.add(spaceTheme);

        holdOptionsPane.add(option1);
        holdOptionsPane.add(option2);

        optionPane.add(holdOptionsPane, BorderLayout.WEST);
        playerPane.setBorder(new TitledBorder("Player Selection"));
        optionPane.setBorder(new TitledBorder("Options"));

        // adds all panels to the box layout of pane
        pane.add(topPane);
        pane.add(Box.createRigidArea(new Dimension(0, 5)));
        pane.add(playerPane);
        pane.add(Box.createRigidArea(new Dimension(0, 10)));
        pane.add(optionPane);
        pane.add(Box.createRigidArea(new Dimension(0, 10)));
        pane.add(bottomPane);

        quitGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JButton test = (JButton)event.getSource();
                System.out.println(test.getName());
                System.exit(0);
            }
        });

        // update state and start the game
        startGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                frame.setVisible(false);
                for (int i = 0; i < 4; i++) {
                    if (!playerInGame[i].isSelected())
                        state.setPlaying(i, PlayerType.none);
                    else {
                        if (human[i].isSelected())
                            state.setPlaying(i, PlayerType.human);
                        else if (easy[i].isSelected())
                            state.setPlaying(i, PlayerType.AI_easy);
                        else if (normal[i].isSelected())
                            state.setPlaying(i, PlayerType.AI_normal);
                        else if (hard[i].isSelected())
                            state.setPlaying(i, PlayerType.AI_hard);
                    }

                    switch (colors[i].getSelectedIndex()) {
                        case 0:
                            players[i].setColor(new Color(0, 200, 0)); // green
                            break;
                        case 1:
                            players[i].setColor(new Color(200, 0, 0)); // red
                            break;
                        case 2:
                            players[i].setColor(new Color(20, 50, 255)); // blue
                            break;
                        case 3:
                            players[i].setColor(Color.ORANGE); // Yellow (orange works better)
                            break;
                        case 4:
                            players[i].setColor(Color.MAGENTA);
                            break;
                        case 5:
                            players[i].setColor(Color.GRAY);
                            break;
                        case 6:
                            players[i].setColor(Color.CYAN);
                            break;
                        case 7:
                            players[i].setColor(Color.PINK);
                            break;
                        case 8:
                            players[i].setColor(new Color(230,230,230)); // "white"
                            break;
                        case 9:
                            players[i].setColor(Color.DARK_GRAY); // "black"
                            break;
                    }
                 }

                if (classicTheme.isSelected())
                    state.setLayout("classic");
                else
                    state.setLayout("space");

                if (starsAndGlobes.isSelected())
                    state.setStarsAndGlobes(true);
                else
                    state.setStarsAndGlobes(false);

                game.updateTiles();
                game.finalizeSetup();

            }
        });
        return pane;
    }

    /**
     * Initializes the startmenu
     */
    public final void initUI() {

        frame = new JFrame();
        frame.setTitle("Ludo Game");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);

        JPanel pane = selectPlayers();
        Border padding = BorderFactory.createEmptyBorder(0,10,10,10);
        pane.setBorder(padding);

        frame.add(pane);
        frame.pack();
    }
}