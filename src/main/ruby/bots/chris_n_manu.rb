require File.dirname( __FILE__ ) + '/../bot_base.rb'

class Bot < BotBase
  
	 def action(price_to_call, minimum_raise, pot_size)
	   "call"
	 end
end
