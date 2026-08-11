locals {
  name = "${var.project_name}-${var.environment}"
}

# Single-table: PK "CLIENTE#{id}", SK "PROFILE" | "SOLICITACAO#{id}" | "ANEXO#{id}" (ver design.md)
resource "aws_dynamodb_table" "main" {
  name         = "${local.name}-main"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "PK"
  range_key    = "SK"

  attribute {
    name = "PK"
    type = "S"
  }

  attribute {
    name = "SK"
    type = "S"
  }

  tags = { Name = "${local.name}-main" }

  lifecycle {
    prevent_destroy = true
  }
}
