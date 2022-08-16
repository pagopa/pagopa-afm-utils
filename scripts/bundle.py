import uuid
import json


class Bundle:
    id = ""
    idPsp = ""
    name = ""
    description = ""
    paymentAmount = 0
    minPaymentAmount = 0
    maxPaymentAmount = 0
    paymentMethod = None
    touchpoint = None
    type = "GLOBAL"
    transferCategoryList = list()
    validityDateFrom = None
    validityDateTo = None

    counter = 0

    def __init__(self, counter=1, id_psp="12345678901", payment_amount_range=(0, 10000), type="GLOBAL", transferCategoryList=list(), payment_method='ANY', touchpoint='ANY'):
        self.id = str(uuid.uuid1())
        self.idPsp = id_psp
        self.counter = counter

        self.type = type

        self.name = "%s-bundle-%s" % (self.type.lower(), self.counter)
        self.description = "test description %s" % self.counter

        self.paymentAmount = self.counter + 100
        self.minPaymentAmount = payment_amount_range[0]
        self.maxPaymentAmount = payment_amount_range[1]

        self.paymentMethod = payment_method
        self.touchpoint = touchpoint

        self.transferCategoryList = transferCategoryList

    def __repr__(self):
        return json.dumps(self.__dict__)