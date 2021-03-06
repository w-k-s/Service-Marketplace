# https://registry.terraform.io/providers/cloudamqp/cloudamqp/latest/docs

provider "cloudamqp" {
  apikey = var.cloudamqp_customer_api_key
}

# Create a new cloudamqp instance
resource "cloudamqp_instance" "instance" {
  name        = "terraform-cloudamqp-instance"
  plan        = "lemur"
  region      = var.cloudamqp_region
  nodes       = 1
  tags        = ["terraform"]
  rmq_version = "3.8.14"
}