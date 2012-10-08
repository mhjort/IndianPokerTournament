require "test/unit"

require "../../main/ruby/bot_base.rb"

@@inherited_by = nil

def BotBase.inherited(subclass)
  @@inherited_by = subclass
end

require "../../main/ruby/bot.rb"

class TestBot < Test::Unit::TestCase
  def test_inheritance_hierarchy
    assert_not_nil(@@inherited_by, "Bot class should inherit from BotBase")
  end
end