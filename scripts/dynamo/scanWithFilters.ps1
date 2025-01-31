aws dynamodb scan `
--table-name transcription `
--filter-expression "theatre = :u" `
--expression-attribute-values file://scanExpression.json