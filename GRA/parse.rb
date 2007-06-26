@rcount = 1
@qcount = 1
running = File.open("running.txt", "w")
queue = File.open("queue.txt", "w")
File.open("log.txt", "r") do |file|
  file.each_line("\n") do |line|
    if line =~ /running/ then
      running.write("#@rcount,#{line.scan(/\d+/)}\n")
      @rcount += 1
    elsif line =~ /queue/ then
      queue.write("#@qcount,#{line.scan(/\d+/)}\n")
      @qcount += 1
    end
  end
end
running.close
queue.close
