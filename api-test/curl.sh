curl --location --request POST 'http://127.0.0.1:8586/calculate' \
--header 'Content-Type: application/json' \
--data-raw '{
    "paymentAmount": 1000,
    "primaryCreditorInstitution": "12345",
    "paymentMethod": null,
    "touchpoint": null,
    "idPspList": null,
    "transferList": [
        {
            "creditorInstitution": "test_28e50e84b749",
            "transferCategory": "pippo"
        },
        {
            "creditorInstitution": "12346",
            "transferCategory": "CP"
        },
        {
            "creditorInstitution": "12345",
            "transferCategory": "PO"
        }
    ]
}'