require 'console.rb'
console = Console.new
name = console.prompt_for_string "What's your name?"
age = console.prompt_for_int "How old are you?"
puts "Your name is #{name} and you're #{age} years old"
