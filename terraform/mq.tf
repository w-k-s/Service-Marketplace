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

data "cloudamqp_credentials" "credentials" {
  instance_id = cloudamqp_instance.instance.id
}

data "cloudamqp_instance" "instance" {
  instance_id = cloudamqp_instance.instance.id
}

## Output

output "cloudamqp_username" {
  description = "CloudAMQP Username"
  sensitive   = true
  value       = data.cloudamqp_credentials.credentials.username
}

output "cloudamqp_password" {
  description = "CloudAMQP Password"
  sensitive   = true
  value       = data.cloudamqp_credentials.credentials.password
}

output "cloudamqp_url" {
  description = "CloudAMQP URL"
  sensitive   = true
  value       = data.cloudamqp_instance.instance.url
}