require "test/unit"

require "../../main/ruby/console.rb"

class TestConsole < Test::Unit::TestCase

  DATA = "ThisIsData"

  def setup
    @output = open("#{self.class.name}.output", "w")
    @input = open("#{self.class.name}.input", "w")
    @input << DATA
    @input.close
    @input.reopen(@input.path, "r")
  end

  def teardown
    @output.close unless @output.closed?
    @input.close unless @input.closed?
    File.delete(@output.path)
    File.delete(@input.path)
  end

  def test_console_prints_to_configured_stream
    console = Console.new(@output)
    console.print DATA
    reopen_for_reading(@output)
    assert_equal(DATA, @output.read)
  end

  def test_console_reads_from_configured_stream
    console = Console.new(@output, @input)
    assert_equal(DATA, console.read)
  end

  def test_console_with_default_streams
    code = "require '../../main/ruby/console.rb';"
    code << "console = Console.new;"
    code << "input = console.read;"
    code << "console.print input;"
    command = "ruby -e \"#{code}\" < #{@input.path} &> #{@output.path}"
    system(command)
    reopen_for_reading(@output)
    assert_equal(DATA, @output.read)
  end

  def test_console_prompts_for_input
    console = Console.new(@output, @input)
    answer = console.prompt_for_string "What's your name?"
    assert_equal(DATA, answer)
  end

  def test_console_prompts_for_text_input_until_it_gets_something
    reopen_for_writing(@input)
    @input << "\n\n\n#{DATA}"
    reopen_for_reading(@input)
    console = Console.new(@output, @input)
    answer = console.prompt_for_string "What's your name?"
    assert_equal(DATA, answer)
  end

  def test_console_prompts_for_numeric_input_until_it_gets_something
    reopen_for_writing(@input)
    @input << "\n\n\n42"
    reopen_for_reading(@input)
    console = Console.new(@output, @input)
    answer = console.prompt_for_int "How old are you?"
    assert_equal(42, answer)
  end

  def test_console_prompts_for_text_input_while_it_gets_something_if_default_is_given
    reopen_for_writing(@input)
    @input << "#{DATA}\nbar\n\n"
    reopen_for_reading(@input)
    console = Console.new(@output, @input)
    answer = console.prompt_for_string "What's your name?", "#{DATA}_DEFAULT"
    assert_equal(DATA, answer)
  end

  def test_console_prompts_for_numeric_input_while_it_gets_something_invalid_even_if_default_is_given
    reopen_for_writing(@input)
    @input << "foo\nbar\n42\n"
    reopen_for_reading(@input)
    console = Console.new(@output, @input)
    answer = console.prompt_for_int "What's your name?", 1234
    assert_equal(42, answer)
  end

  def test_console_prompts_for_text_input_until_it_gets_something_unless_default_is_given
    reopen_for_writing(@input)
    @input << "\n\n\n"
    reopen_for_reading(@input)
    console = Console.new(@output, @input)
    answer = console.prompt_for_string "What's your name?", DATA
    assert_equal(DATA, answer)
  end

  def test_console_prompts_for_numeric_input_until_it_gets_something_unless_default_is_given
    reopen_for_writing(@input)
    @input << "foo\nabc\nabc500\n42\n"
    reopen_for_reading(@input)
    console = Console.new(@output, @input)
    answer = console.prompt_for_int "How old are you?", 12345
    assert_equal(42, answer)
  end

  def test_console_prompts_for_numeric_input_until_it_gets_something_valid
    reopen_for_writing(@input)
    @input << "\ntext\n123\n"
    reopen_for_reading(@input)
    console = Console.new(@output, @input)
    answer = console.prompt_for_int "How old are you?"
    assert_equal(123, answer)
  end

  private
  
  def reopen_for_reading(file)
    reopen_in_mode(file, "r")
  end

  def reopen_for_writing(file)
    reopen_in_mode(file, "w")
  end

  def reopen_in_mode(file, mode)
    file.close
    file.reopen(file.path, mode)
  end

end