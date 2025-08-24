Request:

```http
POST http://{{host}}/{{path}}/contracts
x-api-key: {{login.response.body.$.apiKey}}
Accept: application/json
Content-Type: application/json

{
  "userContact": {
    "userId": "123456789",
    "username": "alexdoe" 
  },
  "billingPeriod": {
    "autoRenew": false
  },
  "contractedServices": {
    "Boolean pricing": "v1"
  },
  "subscriptionPlans": {
    "Boolean pricing": "PRO"
  },
  "subscriptionAddOns": {}
}
```

Response:

```json
{
  "userContact": {
    "userId": "123456789",
    "username": "alexdoe"
  },
  "billingPeriod": {
    "startDate": "2025-08-20T15:28:47.873Z",
    "endDate": "2025-09-19T15:28:47.873Z",
    "autoRenew": false,
    "renewalDays": 30
  },
  "usageLevels": {},
  "contractedServices": {
    "boolean pricing": "v1"
  },
  "subscriptionPlans": {
    "boolean pricing": "PRO"
  },
  "subscriptionAddOns": {},
  "history": [],
  "id": "68a5e9af9adad1f45fc88db8"
}
```
