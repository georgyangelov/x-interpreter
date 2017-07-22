# Inline comment
# Comment


# Numbers

# Integer
1234

# Floating-point
1234.5678

# Partial application operator

print_debug = { |x| puts "PRETTY", x }
print_debug = & puts "PRETTY"

[1, 2, 3, 4]
  | filter &.odd?                    # Same lambda as { |x, *args| x.odd? *args }
  | filter &.divisible_by? 5         # Same lambda as { |x, *args| x.divisible_by? 5, *args }
  | filter &5.divisible_by?          # Same lambda as { |*args| 5.divisible_by? *args }
  | filter

# Pipes

def add_one_to_each(array)
  array.map { |x| x + 1 }
end

[1, 2, 3, 4]
  | map { |x| x * x }
  | add_one_to_each
  | filter { |x| x > 2 }
  | reduce 0, { |x, y| x + y }

[1, 2, 3, 4]
  | .map { |x| x * x }
  | add_one_to_each
  | .filter { |x| x > 2 }
  | .reduce 0, { |x, y| x + y }

[1, 2, 3, 4]
  | &.map { |x| x * x }              # | { |self|  self.map { |x| x * x } }
  | add_one_to_each                  # | { |array| array.map { |x| x + 1 } }
  | &.filter { |x| x > 2 }           # | { |self|  self.map { |x| x > 2 } }
  | &.reduce 0, { |x, y| x + y }     # | { |self|  self.reduce 0, { |x, y| x + y } }

[1, 2, 3, 4] | .map do |x|
  x * x
end | .filter do |x|
  x > 2
end | .reduce 0, do |x, y|
  x + y
end