Request:

```http
POST http://{{host}}/{{path}}/contracts
x-api-key: {{login.response.body.$.apiKey}}
Accept: application/json
Content-Type: application/json

{
  "userContact": {
    "userId": "4242424242",
    "username": "janedoe" 
  },
  "billingPeriod": {
    "autoRenew": true,
    "renewalDays": 365
  },
  "contractedServices": {
    "Boolean pricing": "v1",
    "Renewable and Non-Renewable": "v1"
  },
  "subscriptionPlans": {
    "Boolean pricing": "PRO",
    "Renewable and Non-Renewable": "PRO"
  },
  "subscriptionAddOns": {}
}
```

Response:

```json
{
  "userContact": {
    "userId": "4242424242",
    "username": "janedoe"
  },
  "billingPeriod": {
    "startDate": "2025-08-20T16:25:33.200Z",
    "endDate": "2026-08-20T16:25:33.200Z",
    "autoRenew": true,
    "renewalDays": 365
  },
  "usageLevels": {
    "renewable and non-renewable": {
      "pdfCompileLimit": {
        "resetTimeStamp": "2025-09-20T16:25:33.222Z",
        "consumed": 0
      },
      "guestsLimit": {
        "consumed": 0
      }
    }
  },
  "contractedServices": {
    "boolean pricing": "v1",
    "renewable and non-renewable": "v1"
  },
  "subscriptionPlans": {
    "boolean pricing": "PRO",
    "renewable and non-renewable": "PRO"
  },
  "subscriptionAddOns": {},
  "history": [],
  "id": "68a5f6fd1f5f155084300877"
}
```
