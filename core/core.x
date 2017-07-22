def extend(klass, block)
  block.bind(klass, klass).call
end
