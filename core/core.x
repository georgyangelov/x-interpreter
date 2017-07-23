def extend(klass, block)
  block.bind(klass, klass).call
end

extend Int, do
  def abs
    if self < 0
      0 - self
    else
      self
    end
  end

  def upto(n)
    Range.new(self, n)
  end
end

extend Float, do
  def abs
    if self < 0.0
      0.0 - self
    else
      self
    end
  end
end

class Range
  def initialize(from, to)
    @from = from
    @to = to
  end

  def from
    @from
  end

  def to
    @to
  end

  def stream
    Stream.new(RangeSource.new(self))
  end
end

class RangeSource
  def initialize(range)
    @i = range.from
    @to = range.to
  end

  def next
    if @i > @to
      raise StopIteration.new
    end

    element = @i

    @i = @i + 1

    element
  end
end

class Stream
  def initialize(source)
    @source = source
  end

  def next
    @source.next
  end

  def each(block)
    while true
      block[next]
    end
  catch StopIteration
  end

  def map(block)
    StreamMapper.new self, do |stream|
      block(stream.next)
    end
  end

  def filter(block)
    StreamMapper.new self, do |stream|
      element = stream.next

      while !block(element)
        element = stream.next
      end

      element
    end
  end

  def take(n)
    StreamMapper.new self, do |stream|
      if n == 0
        raise StopIteration.new
      end

      n = n - 1
      stream.next
    end
  end

  def drop(n)
    StreamMapper.new self, do |stream|
      while n > 0
        n = n - 1
        stream.next
      end

      stream.next
    end
  end

  def first
    next
  catch StopIteration
    nil
  end

  def to_a
    result = Array.new

    each { |element| result.push element }

    result
  end
end

class StreamMapper < Stream
  def initialize(source, next)
    super(source)

    @next = next
  end

  def next
    @next(@source)
  end
end

class StopIteration < Error
end

class ArraySource
  def initialize(array)
    @i = 0
    @array = array
  end

  def next
    if @i >= @array.size
      raise StopIteration.new
    end

    element = @array(@i)

    @i = @i + 1

    element
  end
end

extend Array, do
  def each(block)
    stream.each block
  end

  def stream
    Stream.new(ArraySource.new(self))
  end
end
