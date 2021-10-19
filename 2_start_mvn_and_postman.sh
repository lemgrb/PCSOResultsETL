# Start STRAPI
mvn clean test
newman run INSERT_GAME_RESULTS.postman_collection.json -d data.csv --reporters cli,json --reporter-json-export testresults.json

