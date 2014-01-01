guard 'bundler' do
  watch('Gemfile')
end

guard :shotgun, server: 'thin' do
  watch(%r{^build/.*})
  watch(%r{^source/.*})
end

guard 'middleman', verbose: true do
  watch(%r{^config.rb})
  watch(%r{^data/.*})
  watch(%r{^source/.*})

  watch(%r{^views/.*})
  watch(%r{^public/.*})
end
