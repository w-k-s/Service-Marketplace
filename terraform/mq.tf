# https://registry.terraform.io/providers/cloudamqp/cloudamqp/latest/docs
# Free plan (so no vpc)

provider "cloudamqp" {
  apikey = var.cloudamqp_customer_api_key
}

# Create a new cloudamqp instance
resource "cloudamqp_instance" "instance" {
  name              = "terraform_cloudamqp_instance"
  plan              = "lemur"
  region            = var.cloudamqp_region
  nodes             = 1
  tags              = ["terraform"]
  rmq_version       = "3.8.14"
  no_default_alarms = true
}

## Output

output "cloudamqp_url" {
  description = "CloudAMQP URL"
  value       = cloudamqp_instance.instance.url
  sensitive   = true
}