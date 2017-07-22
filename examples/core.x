extend Array, do
  def size_plus_one
    size + 1
  end
end

puts 'Size plus one: ', [1, 2, 3].size_plus_one

puts [1, 2, 3]
  .map({ |x| x * 2 })
  .filter { |x| x > 2 }
