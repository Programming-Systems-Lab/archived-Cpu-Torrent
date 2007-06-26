require 'net/http'

def log
   @response = Net::HTTP.get_response("www.predictprotein.org", "/status.php")
   @body = @response.body
   File.open("log.txt", "a+") do |x|
     x.write("\n\n")
     x.write(@body.scan(/Predict Protein (.+)/))
     x.write("\n")
     x.write(@body.scan(/Processes (.+\d+)/).join("\n"))
   end
end

while true
  begin
    log
    sleep(600)
  rescue
     File.open("log.txt", "a+") do |x|
     x.write("\n\nTimed Out: "+Time.now.to_s)
    end
    sleep(60)
    retry
  end
end
