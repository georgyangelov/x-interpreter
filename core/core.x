def extend(klass, block)
  block.bind(klass, klass).call
end

extend Array, do
  def each(block)
    i = 0
    length = size

    while i < length
      block.call get(i)

      i = i + 1
    end

    length
  end

  def map(block)
    result = Array.new

    each { |element| result.push(block.call(element)) }

    result
  end

  def filter(block)
    result = Array.new

    each do |element|
      if block.call(element)
        result.push element
      end
    end

    result
  end
end
