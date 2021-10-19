# PCSOResultsETL
Extract results from https://pcso.gov.ph/

## Steps

1. Launch browser and open https://www.pcso.gov.ph/SearchLottoResult.aspx
2. Search by date range
3. Copy values from table
4. Transform values
5. Save as CSV
6. Run STRAPI locally
7. Run postman via newman with CSV as input
8. Run ReactJS app
9. Test if data was inserted successfully
10. Upload to S3
11. Invalidate cache on Cloudfront
12. Test data on production