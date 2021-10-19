# PCSOResultsETL
Extract results from https://pcso.gov.ph/

## Steps

1. Launch browser and open https://www.pcso.gov.ph/SearchLottoResult.aspx (Done)
2. Search by date range (Done)
3. Copy values from table (Done)
4. Transform values (Done)
5. Save as CSV (Done)
6. Reset latest flag to 'false' for latest game ids (latest_game_ids.csv) via postman
6. Run STRAPI locally
7. Run postman via newman with CSV as input
8. Run ReactJS app
9. Test if data was inserted successfully
10. Upload to S3
11. Invalidate cache on Cloudfront
12. Test data on production
