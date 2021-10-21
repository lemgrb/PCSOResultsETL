cd ../plus63-frontend
npm run build
echo "Change directory to ../aws"
cd ../aws
echo "Copying to s3"
cp -r ../plus63-frontend/out/_next s3
cp -r ../plus63-frontend/out/resulthistory s3
cp ../plus63-frontend/out/sitemap.xml s3
cp ../plus63-frontend/out/index.html s3
echo "Delete old files"
aws s3 rm s3://$S3URI/_next --recursive
aws s3 rm s3://$S3URI/resulthistory --recursive
aws s3 rm s3://$S3URI/sitemap.xml --recursive
aws s3 rm s3://$S3URI/index.html --recursive
echo "Sync to s3"
aws s3 sync s3 s3://$S3URI
echo "Invalidating cache"
aws cloudfront create-invalidation --distribution-id $CLOUDFRONT_DISTRIBUTION --paths "/*"
