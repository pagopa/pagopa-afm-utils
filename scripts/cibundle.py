import uuid
from json import JSONEncoder


class Attribute:

    def __init__(self, attribute=None):
        self.id = str(uuid.uuid1())
        self.maxPaymentAmount = attribute["maxPaymentAmount"]
        self.transferCategory = attribute["transferCategory"] if "transferCategory" in attribute else None
        self.transferCategoryRelation = attribute["transferCategoryRelation"] if "transferCategoryRelation" in attribute else None

    # def __repr__(self):
    #     return json.dumps(self.__dict__)


class CiBundle:
    id = ""
    ciFiscalCode = ""
    idBundle = ""
    attributes = list()

    def __init__(self, ci_fiscal_code="fiscal_code", id_bundle=None, attributes=list()):
        self.id = str(uuid.uuid1())
        self.ciFiscalCode = ci_fiscal_code
        self.idBundle = id_bundle
        self.attributes = list()
        for attr in attributes:
            attribute = Attribute(attribute=attr)
            self.attributes.append(attribute)
