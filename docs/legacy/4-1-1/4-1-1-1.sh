curl -X 'POST' \
  'http://localhost:8080/api/v1/commands/accounts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "ownerId": "user123"
}'
