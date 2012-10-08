require 'console'

$console = Console.new
def ask_str(question, default=nil)
  $console.prompt_for_string(question, default)
end
def ask_int(question, default=nil)
  $console.prompt_for_int(question, default)
end

bot_name = ask_str("What is the name of your bot?")
bot_file = ask_str("In which file is your Bot class defined?", "bot.rb")
bot_host = ask_str("What is your IP address or hostname?", "localhost")
bot_port = ask_int("What port you want to run your client in?", 5001)
server_host = ask_str("What is the server's hostname?", "localhost")
server_port = ask_int("What is the server's port number?", 5000)

puts "\n\n"

puts "Attempting to require Bot class from '#{bot_file}'..."
require bot_file

puts "Instantiating the Bot class..."
bot = eval "Bot.new"

require 'xmlrpc/client'
require 'xmlrpc/server'

server_thread = Thread.new {
  puts "Creating an XML-RPC server for server-to-bot communication on port #{bot_port}..."
  XMLRPC::Config::ENABLE_NIL_CREATE = true
  bot_server = XMLRPC::Server.new(port=bot_port, host=bot_host, maxConnections=4, stdlog=nil, audit=false, debug=false)

  puts "Registering Bot instance as a handler..."
  bot_server.add_handler("Bot", bot)

  bot_server.add_handler("Bot.tournamentStarts") { |stack, players|
    bot.tournamentStarts(stack, players)
    true
  }
  bot_server.add_handler("Bot.handStarts") { |dealer, blinds, cards, stacks|
    bot.handStarts(dealer, blinds, cards, stacks)
    true
  }
  bot_server.add_handler("Bot.paidBlind") { |chips|
    bot.paidBlind(chips)
    true
  }
  bot_server.add_handler("Bot.action") { |price_to_call, minimum_raise, pot_size|
    bot.action(price_to_call, minimum_raise, pot_size)
  }
  bot_server.add_handler("Bot.performed") { |performed_action|
    bot.performed(performed_action)
    true
  }
  bot_server.add_handler("Bot.playerActed") { |player, action|
    bot.playerActed(player, action)
    true
  }
  bot_server.add_handler("Bot.showCard") { |card|
    bot.showCard(card)
    true
  }
  bot_server.add_handler("Bot.won") { |chips|
    bot.won(chips)
    true
  }

  puts "Done registering Bot, waiting for incoming messages..."
  bot_server.serve
}

puts "Creating an XML-RPC client for registering with the server..."
bot_client = XMLRPC::Client.new2("http://#{server_host}:#{server_port}/")

puts "Registering bot as '#{bot_host}:#{bot_port.to_s}/#{bot_name}'..."
bot_client.call("Server.join", bot_host, bot_port, bot_name)

server_thread.join
puts "XML-RPC server exited."
