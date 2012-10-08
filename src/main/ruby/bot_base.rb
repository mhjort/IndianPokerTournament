class BotBase

  # Invoked to notify bots about the tournament starting.
  # 
  # PARAMETERS:
  #   stack_size:   Initial number of chips per player
  #   player_names: List of the names of all players in the tournament,
  #                 indicating the seating order.
  # RETURNS:
  #   nothing
  def tournamentStarts(stack_size, player_names)
  end

  # Invoked to notify bots about a new hand starting
  # 
  # PARAMETERS:
  #   dealer: the name of the player acting as the dealer for this hand
  #   blinds: A hash mapping player names (str) to their blinds (int).
  #           E.g. { "Bob" => 3, "Charles" => 6 }.
  #   cards:  A hash mapping player names (str) to their cards (str).
  #           E.g. { "Bob" => "AS", "Charles" => "2C" }.
  #   stacks: A hash mapping player names (str) to their stacks (int).
  #           E.g. { "Bob" => 38, "Charles" => 20 }.
  # RETURNS:
  #   nothing
  def handStarts(dealer, blinds, cards, stacks)
  end

  # Invoked to notify bots about them paying a blind bet
  # 
  # PARAMETERS:
  #   chips: The size of the blind paid
  # RETURNS:
  #   nothing
  def paidBlind(chips)
  end

  # Invoked to prompt bots for their next action.
  # 
  # PARAMETERS:
  #   price_to_call: The number of chips to call in order to stay in the hand
  #   minimum_raise: The minimum raise (on top of price_to_call)
  #   pot_size:      The size of the pot before this bot's action
  # RETURNS:
  #   A string describing the bot's desired action. One of:
  #     * "fold"
  #     * "call"
  #     * "check"
  #     * "raise by N" (where N is the number of chips to raise with)
  def action(price_to_call, minimum_raise, pot_size)
    "fold"
  end

  # Invoked to notify bots of the interpretation of their latest action.
  # Usually, this is the same action returned by the bot's previous 
  # invocation to action() but the server will convert "invalid" actions 
  # to "valid" actions (for example if the player doesn't have enough 
  # chips for the action it indicated).
  # 
  # PARAMETERS:
  #   action: The action (str) performed by the bot. 
  #           One of "fold", "call", "check", or "raise by X".
  # RETURNS:
  #   nothing
  def performed(action)
  end

  # Invoked to notify bots of the actions of other players.
  # 
  # PARAMETERS:
  #   player: The player (str) who performed an action.
  #   action: The action (str) carried out by the player.
  #           One of "fold", "call", "check", or "raise by X".
  # RETURNS:
  #   nothing
  def playerActed(player, action)
  end

  # Invoked to notify bots of the card they were holding in the hand.
  # This method is obviously called after the hand has been played.
  # 
  # PARAMETERS:
  #   card: The card (str) of the bot. E.g. "TH" for the 10 of Hearts, 
  #         "5C" for the 5 of Clubs, and so forth.
  # RETURNS:
  #   nothing
  def showCard(card)
  end

  # Invoked to notify bots of the number of chips they've won in a
  # hand. This method is only called for the winning bot(s).
  # 
  # PARAMETERS:
  #   chips: The number of chips won.
  # RETURNS:
  #   nothing
  def won(chips)
  end

end