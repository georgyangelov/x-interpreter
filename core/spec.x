class XSpec
  static do
    def describe(name, block)
      spec = XSpec.new
      spec.describe name, block
      spec.run
      spec
    end
  end

  def results
    @results
  end

  def initialize
    @root = XSpecGroup.new('XSpec')
    @results = XSpecResults.new
  end

  def describe(name, block)
    @root.describe name, block
  end

  def run
    @root.run(@results)

    puts ''
    puts @results.success_count.to_s, ' succeeded, ',
         @results.fail_count.to_s,    ' failed'
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

    puts '✖ ', example.name
    puts '  ', error.message
  end

  def example_succeeded(example)
    @succeeded.push example

    puts '✔ ', example.name
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

    block.bind(self).call
  end

  def run(results)
    @examples.each { |example| example.run(results) }
    @groups.each   { |group|   group.run(results)   }
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
    context_class = Class.new 'Anonymous', XSpecAssertions
    context = context_class.new

    @block.bind(context, context_class).call

    results.example_succeeded(self)
  catch error
    results.example_failed(self, error)
  end
end

class XSpecError < Error
end

class AssertionError < XSpecError
end

class XSpecAssertions
  def expect(value, message)
    if !value
      raise AssertionError.new('Assertion failed: '.concat(message))
    end
  end

  def expect_eq(a, b)
    if a != b
      raise AssertionError.new('Expected '.concat(a.inspect, ' == ', b.inspect))
    end
  end

  def expect_between(a, x, b)
    if !(a < x and x < b)
      raise AssertionError.new('Expected '.concat(a.inspect, ' < ', x.inspect, ' < ', b.inspect))
    end
  end
end
