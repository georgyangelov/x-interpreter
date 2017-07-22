spec = XSpec.new

spec.describe "Test", do |s|
  s.it "breaks", do
    expect 1 == 2, "1 == 2"
  end

  s.it "does not break", do
    expect 1 == 1, "1 == 1"
  end
end

spec.run
