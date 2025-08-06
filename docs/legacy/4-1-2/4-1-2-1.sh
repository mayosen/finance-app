curl -X 'POST' \
  'http://localhost:8080/api/v1/commands/deposit' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "accountId": "7e668605-04b0-466c-b1bc-1a431c8aa27b",
  "amount": 100
}'
