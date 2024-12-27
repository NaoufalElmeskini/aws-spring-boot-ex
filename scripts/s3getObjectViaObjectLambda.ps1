# prerequisite :
    # existing lambda object access point : arn:aws:s3-object-lambda:eu-north-1:540556776725:accesspoint/my-obj-lambda-acc-point
    # existing object key : tintin.wav
# usage :
    #   .\s3getObjectViaObjectLambda.ps1 tintin.wav arn:aws:xxx/my-obj-lambda
param(
    [string]$key,
    [string]$arn
)

if (!$key) {
    Write-Host "/!\ (exiting...) you didn't insert key"
    exit
} else {
    Write-Output "(loading...) get-object for key: " $key
}

if (!$arn) {
    Write-Host "/!\ (exiting...) you didn't insert object-lambda"
    exit
} else {
    Write-Output "(loading...) object-lambda with name: " $arn
}

#get-object via bucketName
#aws s3api get-object --key tintin.jpg --bucket tintin-buck localTintin.jpg

#get-object via lambdaObjAccPoint
aws s3api get-object --key $key --bucket $arn output/$key