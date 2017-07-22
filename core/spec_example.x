spec = XSpec.new

spec.describe 'Test', do
  it 'breaks due to assertion error', do
    expect 1 == 2, '1 == 2'
  end

  it 'does not break', do
    expect 1 == 1, '1 == 1'
  end

  it 'breaks due to an exception', do
    raise Error.new 'This is the exception message'
  end
end

spec.run
