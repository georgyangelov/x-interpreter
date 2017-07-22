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

extend Array, do
  def each(block)
    i = 0
    length = size

    while i < length
      block.call self[i]

      i = i + 1
    end

    length
  end

  def map(block)
    result = Array.new

    each { |element| result.push(block[element]) }

    result
  end

  def filter(block)
    result = Array.new

    each do |element|
      if block[element]
        result.push element
      end
    end

    result
  end
end
