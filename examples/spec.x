XSpec.describe 'Test', do
  it 'breaks due to an assertion error', do
    expect 1 == 2, '1 == 2'
  end

  it 'does not break', do
    expect 1 == 1, '1 == 1'
  end

  it 'breaks due to an exception', do
    raise Error.new 'This is the exception message'
  end
end
