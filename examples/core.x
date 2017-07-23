extend Array, do
  def size_plus_one
    size + 1
  end
end

# puts 'Size plus one: ', [1, 2, 3].size_plus_one


puts 0.upto(100000).stream
  .map({ |x|
    puts 'map ', x
    x * x })
  .filter({ |x| x > 100 })
  .take(10)
  .to_a


# In the future...
# puts (0..100000).stream
#   | .map { |x| x * x }
#   | .filter { |x| x > 100 }
#   | .take 10
#   | .to_a
