require "test/unit"

require "../../main/ruby/bot_base.rb"

class TestBotBase < Test::Unit::TestCase

  def setup
    @bot = BotBase.new
  end

  def test_bot_defines_interface_methods
    assert_has_method("tournamentStarts")
    assert_has_method("handStarts")
    assert_has_method("paidBlind")
    assert_has_method("action")
    assert_has_method("performed")
    assert_has_method("playerActed")
    assert_has_method("won")
    assert_has_method("showCard")
  end

  def test_args_for_tournamentStarts
    initial_stack = 123
    player_names = ["Alice", "Bob", "Charles"]
    @bot.tournamentStarts(initial_stack, player_names)
  end

  def test_args_for_handStarts
    dealer = "Alice"
    blinds = { "Bob"=>1, "Charles"=>2 }
    cards = { "Alice"=>"AS", "Bob"=>"KS", "Charles"=>"QS" }
    stacks = { "Alice"=>100, "Bob"=>80, "Charles"=>120 }
    @bot.handStarts(dealer, blinds, cards, stacks)
  end

  def test_args_for_paidBlind
    @bot.paidBlind(15)
  end
  
  def test_args_and_default_value_for_action
    assert_equal("fold", @bot.action(100, 100, 300))
  end

  def test_args_for_performed
    @bot.performed("raise by 5")
  end
  
  def test_args_for_playerActed
    @bot.playerActed("Bob", "raise by 2")
  end
  
  def test_args_for_showCard
    @bot.showCard("2S")
  end

  def test_args_for_won
    @bot.won(123)
  end

  private
  
  def assert_has_method(name)
    msg = "BotBase class doesn't define method #{name}."
    assert(BotBase.public_method_defined?(name), msg)
  end

end