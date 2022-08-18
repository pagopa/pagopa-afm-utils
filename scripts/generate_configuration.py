import json

from bundle import Bundle
from cibundle import CiBundle
from json import JSONEncoder
import requests
import sys


class ConfigurationEncoder(JSONEncoder):
    def default(self, o):
        return o.__dict__


def generate_bundles(how_many_bundles=750, how_many_ci_bundles=22000):
    local_bundles = list()
    for i in range(0, how_many_bundles):
        print("generating bundles for psp %s" % i)
        global ci_bundles

        min = 0 if i == 0 else 1
        max = i+1

        id_psp = str(i).zfill(11)

        bundle = Bundle(counter=i,
                        id_psp=id_psp,
                        # payment_amount_range=((i * 100000) + min, max * 100000)
                        payment_amount_range=(0, 100000)
                        )
        local_bundles.append(bundle)
        ci_bundles += generate_ci_bundles_1(how_many_ci_bundles, bundle)

        bundle = Bundle(counter=(i+1),
                        id_psp=id_psp,
                        # payment_amount_range=((i * 100000) + min, max * 100000),
                        payment_amount_range=(0, 100000),
                        payment_method="PO"
                        )
        local_bundles.append(bundle)
        ci_bundles += generate_ci_bundles_2(how_many_ci_bundles, bundle)

        bundle = Bundle(counter=(i+2),
                        id_psp=id_psp,
                        # payment_amount_range=((i * 100000) + min, max * 100000),
                        payment_amount_range=(0, 100000),
                        payment_method="CP"
                        )
        local_bundles.append(bundle)

        bundle = Bundle(counter=(i+3),
                        id_psp=id_psp,
                        # payment_amount_range=((i * 100000) + min, max * 100000),
                        payment_amount_range=(0, 100000),
                        transferCategoryList=["TAX1"]
                        )
        local_bundles.append(bundle)
        ci_bundles += generate_ci_bundles_3(how_many_ci_bundles, bundle)

        bundle = Bundle(counter=(i+4),
                        id_psp=id_psp,
                        # payment_amount_range=((i * 100000) + min, max * 100000),
                        payment_amount_range=(0, 100000),
                        payment_method="CP",
                        touchpoint="IO",
                        transferCategoryList=["TAX1", "TAX2"]
                        )
        local_bundles.append(bundle)

        bundle = Bundle(counter=(i+5),
                        id_psp=id_psp,
                        # payment_amount_range=((i * 100000) + min, max * 100000),
                        payment_amount_range=(0, 100000),
                        touchpoint="IO",
                        )
        local_bundles.append(bundle)
    return local_bundles


def generate_ci_bundles_1(how_many_bundles=750, bundle=None):
    local_ci_bundles = list()
    for i in range(0, how_many_bundles):
        # print("\tgenerating cibundles 1 for ci %s" % i)
        ci = "fiscalCode-%s" % i
        attributes = [{
            "maxPaymentAmount": 20,
            "transferCategory": "TAX1",
            "transferCategoryRelation": "EQUAL"
        }]
        ci_bundle = CiBundle(ci_fiscal_code=ci,
                             id_bundle=bundle.id,
                             attributes=attributes)
        local_ci_bundles.append(ci_bundle)
    return local_ci_bundles


def generate_ci_bundles_2(how_many_bundles=750, bundle=None):
    local_ci_bundles = list()
    for i in range(0, how_many_bundles):
        # print("\tgenerating cibundles 2 for ci %s" % i)
        ci = "fiscalCode-%s" % i
        attributes = [{
            "maxPaymentAmount": 20,
            "transferCategory": "TAX1",
            "transferCategoryRelation": "NOT_EQUAL"
        }]
        ci_bundle = CiBundle(ci_fiscal_code=ci,
                             id_bundle=bundle.id,
                             attributes=attributes)
        local_ci_bundles.append(ci_bundle)
    return local_ci_bundles


def generate_ci_bundles_3(how_many_bundles=750, bundle=None):
    local_ci_bundles = list()
    for i in range(0, how_many_bundles):
        # print("\tgenerating cibundles 3 for ci %s" % i)
        ci = "fiscalCode-%s" % i
        attributes = [{
            "maxPaymentAmount": 20
        }]
        ci_bundle = CiBundle(ci_fiscal_code=ci,
                             id_bundle=bundle.id,
                             attributes=attributes)
        local_ci_bundles.append(ci_bundle)
    return local_ci_bundles


pspNo = 75
ciNo = 10
if len(sys.argv) < 1:
    print("Specify environment: local or dev")
    print("python3 generate_configuration.py <local|dev> [number_of_psp] [number_of_ci]")
elif len(sys.argv) < 2:
    env = sys.argv[1]
    pspNo = 75
    ciNo = 10
elif len(sys.argv) < 3:
    env = sys.argv[1]
    pspNo = int(sys.argv[2])
    ciNo = 10
else:
    env = sys.argv[1]
    pspNo = int(sys.argv[2])
    ciNo = int(sys.argv[3])


ci_bundles = list()
bundles = generate_bundles(pspNo, ciNo)

print("creating configuration data for calculator...")
environment = {
    "bundles": json.loads(ConfigurationEncoder().encode(bundles)),
    "ciBundles": json.loads(ConfigurationEncoder().encode(ci_bundles))
}

print("saving configuration data on file...")
with open('configuration_data_%s_%s.json' % (len(bundles), len(ci_bundles)), 'w') as outfile:
    json.dump(environment, outfile)

configuration = {}
with open('%s.environment.json' % env) as json_file:
    configuration = json.load(json_file)

print("sending data to calculator...")
url = "%s/configuration" % configuration.get("host")
x = requests.post(url, json=environment)
print('request sent: %s' % x.status_code)
