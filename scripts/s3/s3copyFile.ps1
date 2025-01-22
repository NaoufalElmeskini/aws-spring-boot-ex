param(
    [string]$file,
    [string]$bucket
)

if (!$file) {
    Write-Host "/!\ you didn't insert file to copy"
    exit
}
if (!$bucket) {
    Write-Host "/!\ you didn't insert bucket"
    exit
}

aws s3 cp $file $bucket