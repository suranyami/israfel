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

guard :jasmine do
  watch(%r{spec/javascripts/spec\.(js\.coffee|js|coffee)$}) { 'spec/javascripts' }
  watch(%r{spec/javascripts/.+_spec\.(js\.coffee|js|coffee)$})
  watch(%r{spec/javascripts/fixtures/.+$})
  watch(%r{app/assets/javascripts/(.+?)\.(js\.coffee|js|coffee)(?:\.\w+)*$}) { |m| "spec/javascripts/#{ m[1] }_spec.#{ m[2] }" }
end
