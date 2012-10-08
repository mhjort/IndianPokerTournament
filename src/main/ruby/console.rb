class Console

  def initialize(output=$stdout, input=$stdin)
    @output = output
    @input = input
  end

  def print(message)
    @output.print message
  end

  def read
    @input.readline
  end

  def prompt_for_string(question, default_value=nil)
    prompt(question, default_value) { |value| value.strip != "" }
  end

  def prompt_for_int(question, default_value=nil)
    prompt(question, default_value) { |value| "#{value}" == value.to_i.to_s }.to_i
  end

  private
  def prompt(question, default_value=nil, &validator)
    answer = ""
    while answer.nil? or answer == ""
      print "#{question} "
      print "[#{default_value.to_s}] " unless default_value.nil?
      answer = read.strip
      if answer == '' and not default_value.nil?
        answer = default_value
      end
      if block_given?
        answer = '' unless yield(answer)
      end
    end
    answer
  end

end