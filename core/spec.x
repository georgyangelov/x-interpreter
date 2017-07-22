# TODO: Create builtin method and use it
def each(array, block)
  i = 0
  length = array.size

  while i < length
    block.call array.get(i)

    i = i + 1
  end

  length
end

class XSpec
  def results
    @results
  end

  def initialize
    @root = XSpecGroup.new("XSpec")
    @results = XSpecResults.new
  end

  def describe(name, block)
    @root.describe name, block
  end

  def run
    @root.run(@results)

    puts ""
    puts @results.success_count.to_s
         .concat(" succeeded, ")
         .concat(@results.success_count.to_s)
         .concat(" failed")
  end
end

class XSpecResults
  def initialize
    @failed = Array.new
    @succeeded = Array.new
  end

  def success_count
    @succeeded.size
  end

  def fail_count
    @failed.size
  end

  def example_failed(example, error)
    @failed.push example

    puts "✖ It ".concat(example.name)
    puts "     ".concat(error.message)
  end

  def example_succeeded(example)
    @succeeded.push example

    puts "✔ It ".concat(example.name)
  end
end

class XSpecGroup
  def initialize(name)
    @name = name

    @examples = Array.new
    @groups = Array.new
  end

  def it(name, block)
    @examples.push XSpecExample.new(name, block)
  end

  def describe(name, block)
    group = XSpecGroup.new(name)

    @groups.push group

    # TODO: Use bind
    block.call group
  end

  def run(results)
    each @examples, { |example| example.run(results) }
    each @groups,   { |group|   group.run(results)   }
  end
end

class XSpecExample
  def name
    @name
  end

  def initialize(name, block)
    @name = name
    @block = block
  end

  def run(results)
    @block.call

    results.example_succeeded(self)
  catch error:AssertionError
    results.example_failed(self, error)
  end
end

class XSpecError < Error
end

class AssertionError < XSpecError
end

# TODO: Scope using bind

def expect(value, message)
  if !value
    raise AssertionError.new("Assertion failed: ".concat(message))
  end
end
