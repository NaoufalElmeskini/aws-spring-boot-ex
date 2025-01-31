aws dynamodb query `
--table-name TranscriptionTable `
--key-condition-expression "TUUID = :id" `
--expression-attribute-values file://queryExpression.json