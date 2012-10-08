/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laughingpanda.games.poker.indian.server.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.laughingpanda.games.poker.indian.domain.Card;
import org.laughingpanda.games.poker.indian.domain.HandListener;
import org.laughingpanda.games.poker.indian.domain.Player;
import org.laughingpanda.games.poker.indian.domain.TournamentListener;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.domain.events.BlindEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerActionEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerPromptEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerWinEvent;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Pekka Enberg
 * @author Antti Mattila
 */
public class Application implements HandListener, TournamentListener {
	private static final int PLAYER_WIDTH = 179;

	private static final int PLAYER_HEIGHT = 20;

	private static final int CARD_HEIGHT = 259;

	private PanelBuilder builder;

	private FormLayout layout;

	private JFrame frame;

	private ArrayList<Player> players;

	public Application() {
		startApplication();
	}

	private void startApplication() {
		frame = new JFrame("Indian Poker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		players = new ArrayList<Player>();

		layout = new FormLayout("pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref", // columns
				"p, p, p, 3dlu, p, p, p, 3dlu, p, p, p, 3dlu, p, p, p");

		builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		JPanel panel = builder.getPanel();
		Color green = Color.GREEN;
		green = green.darker();
		panel.setBackground(green);
		frame.setContentPane(panel);
		refresh();
	}

	private void addPlayer(Player player, int x, int y) {
		CellConstraints cc = new CellConstraints();
		builder.add(createNameLabel(player), cc.xy(x, y));
		builder.add(createStackLabel(player), cc.xy(x, y + 1));
		JLabel cardLabel = createCardLabel(player);
		cardLabel.setOpaque(true);
		builder.add(cardLabel, cc.xy(x, y + 2));
	}

	private JLabel createNameLabel(final Player player) {
		return configurePlayerLabel(new JLabel() {
			@Override
			public String getText() {
				return player.getName();
			}
		});
	}

	private JLabel createStackLabel(final Player player) {
		return configurePlayerLabel(new JLabel() {
			@Override
			public String getText() {
				return "" + player.getStack();
			}
		});
	}

	private JLabel configurePlayerLabel(JLabel label) {
		label.setFont(new Font("Arial", Font.BOLD, 18));
		label.setForeground(Color.BLACK);
		label.setPreferredSize(new Dimension(PLAYER_WIDTH, PLAYER_HEIGHT));
		return label;
	}

	private JLabel createCardLabel(final Player player) {
		JLabel cardLabel = new JLabel() {
			@Override
			public Icon getIcon() {
				Image image = getCardImage(player);
				if (image == null)
					return null;
				return new ImageIcon(image.getScaledInstance(PLAYER_WIDTH, CARD_HEIGHT, 0));
			}

			@Override
			public Color getBackground() {
				return Color.GREEN.darker();
			}
		};
		cardLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cardLabel.setForeground(Color.BLACK);
		cardLabel.setFont(new Font("Arial", Font.PLAIN, 32));
		cardLabel.setPreferredSize(new Dimension(PLAYER_WIDTH, CARD_HEIGHT));
		return cardLabel;
	}

	private Image getCardImage(Player player) {
		Card card = player.getCard();
		if (card != null)
			return card.getImage();
		if (player.hasChips())
			return Card.BACKWARDS;
		return null;
	}

	private void addPlayer(Player player) {
		players.add(player);
		addPlayer(player, getHorizontalPosition(players.size()), getVerticalPosition(players.size()));
		refresh();
	}

	private void refresh() {
		builder.getPanel().repaint();
		frame.pack();
		frame.setVisible(true);
	}

	private int getVerticalPosition(int i) {
		if (i <= 5)
			return 1;
		if (i <= 10)
			return 5;
		if (i <= 15)
			return 9;
		throw new IllegalStateException("Only 15 users are supported.");
	}

	private int getHorizontalPosition(int i) {
		if (i == 1 || i == 6 || i == 11)
			return 1;
		if (i == 2 || i == 7 || i == 12)
			return 3;
		if (i == 3 || i == 8 || i == 13)
			return 5;
		if (i == 4 || i == 9 || i == 14)
			return 7;
		if (i == 5 || i == 10 || i == 15)
			return 9;
		throw new IllegalStateException("Only 15 users are supported.");
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public HandListener getHandListener() {
		return this;
	}

	public void onActionPrompt(PlayerPromptEvent e) {
		refresh();
	}

	public void onBigBlind(BlindEvent e) {
		refresh();
	}

	public void onHandEnd(Map<Player, Integer> wins) {
		refresh();
	}

	public void onHandStart(List<Player> players) {
		refresh();
	}

	public void onPlayerAction(PlayerActionEvent e) {
		if (e.action instanceof Fold) {
			e.player.setCard(null);
		}
		refresh();
	}

	public void onPlayerWin(PlayerWinEvent e) {
		refresh();
	}

	public void onShowdown() {
		refresh();
	}

	public void onSmallBlind(BlindEvent e) {
		refresh();
	}

	public TournamentListener getTournamentListener() {
		return this;
	}

	public void onPlayerDrop(Player player) {
		player.setCard(null);
		refresh();
	}

	public void onPlayerJoin(Player player) {
		addPlayer(player);
	}

	public void onTournamentFinish(List<Player> results) {
		refresh();
	}
}
