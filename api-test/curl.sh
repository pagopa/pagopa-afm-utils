curl --location --request POST 'http://127.0.0.1:8586/calculate' \
--header 'Content-Type: application/json' \
--data-raw '{
    "paymentAmount": 100,
    "primaryCreditorInstitution": "12345",
    "paymentMethod": null,
    "touchPoint": null,
    "idPspList": null,
    "transferList": [
        {

            "creditorInstitution": "test_28e50e84b749",
            "transferCategory": "t1"
        },
        {
            "creditorInstitution": "12345",
            "transferCategory": "PO"
        }
    ]
}'