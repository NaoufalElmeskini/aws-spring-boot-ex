aws dynamodb create-table `
--table-name TranscriptionTable `
--attribute-definitions AttributeName=TUUID,AttributeType=S AttributeName=Title,AttributeType=S `
--key-schema AttributeName=TUUID,KeyType=HASH AttributeName=Title,KeyType=RANGE `
--provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1