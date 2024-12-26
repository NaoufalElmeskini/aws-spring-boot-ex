# usage :
#   .\s3getObjectViaAcPoint.ps1 tintin.jpg
param(
    [string]$key
)

if (!$key) {
    Write-Host "/!\ you didn't insert key"
    exit
} else {
    Write-Output "key with name: " $key
}

#get-object via bucketName
#aws s3api get-object --key tintin.jpg --bucket tintin-buck localTintin.jpg

#get-object via lambdaObjAccPoint
aws s3api get-object --key $key --bucket arn:aws:s3-object-lambda:eu-north-1:540556776725:accesspoint/my-obj-lambda-acc-point output/$key