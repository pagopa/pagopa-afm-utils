import json
import sys
import requests

if sys.argv[1] is None or sys.argv[2] is None:
    print("Specify arguments: sending_configuration.py <configuration_data.json> <local|dev>")


configuration = {}
print("reading configuration data from file...")
with open(sys.argv[1]) as json_file:
    configuration = json.load(json_file)

print("sending data to calculator...")

environment = {}
with open('%s.environment.json' % sys.argv[2]) as json_file:
    environment = json.load(json_file)

print("sending data to calculator...")
url = "%s/configuration" % environment.get("host")
x = requests.post(url, json=configuration)
print("STATUS " + str(x.status_code))
