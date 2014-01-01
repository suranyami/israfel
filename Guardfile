guard 'bundler' do
  watch('Gemfile')
end

guard :shotgun, server: 'thin' do
  watch(%r{^source/.*})
end

guard 'middleman' do
  watch(%r{^config.rb})
  watch(%r{^data/.*})
  watch(%r{^source/.*})

  watch(%r{^views/.*})
  watch(%r{^public/.*})
end

guard 'pow' do
  watch('.powrc')
  watch('.powenv')
  watch('.rvmrc')
  watch('.ruby-gemset')
  watch('.ruby-version')
  watch('Gemfile')
  watch('Gemfile.lock')
  watch('config.rb')
  watch('config.ru')
end
