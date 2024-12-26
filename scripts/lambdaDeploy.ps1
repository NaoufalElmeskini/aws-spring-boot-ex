# prerequisite :
#   aws sdk configured locally
# usage :
#   .\lambdaDeploy.ps1 MaLambda25


param(
    [string]$lambdaName
)

if (!$lambdaName) {
    Write-Host "/!\ you didn't insert lambda name"
    exit
} else {
    Write-Output "lambda with name: " $lambdaName
}

Write-Output "packaging..." $lambdaName
mvn clean package --file ..\pom.xml

Write-Output "deploying..." $lambdaName
aws lambda update-function-code --function-name $lambdaName --zip-file "fileb://..\target\examples-1.jar"