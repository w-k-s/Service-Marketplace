# https://registry.terraform.io/providers/cloudamqp/cloudamqp/latest/docs

provider "cloudamqp" {
  apikey = var.cloudamqp_customer_api_key
}

# Create a new cloudamqp instance
resource "cloudamqp_instance" "instance" {
  name        = "terraform-cloudamqp-instance"
  plan        = "lemur"
  region      = "amazon-web-services::region=ap-south-1"
  nodes       = 1
  tags        = ["terraform"]
  rmq_version = "3.8.14"
}

# New recipient to receieve notifications
resource "cloudamqp_notification" "recipient_01" {
  instance_id = cloudamqp_instance.instance.id
  type        = "email"
  value       = var.notification_email
  name        = "alarm"
}

# New cpu alarm
resource "cloudamqp_alarm" "cpu_alarm" {
  instance_id     = cloudamqp_instance.instance.id
  type            = "cpu"
  value_threshold = 90
  time_threshold  = 600
  enabled         = true
  recipients      = [cloudamqp_notification.recipient_01.id]
}


# Cloudwatch logs integration
resource "cloudamqp_integration_log" "cloudwatchlog" {
  instance_id = cloudamqp_instance.instance.id
  name        = "cloudwatchlog"
  # access_key_id = var.aws_access_key
  # secret_access_key = var.aws_secret_key
  # region = var.aws_region
}

# Cloudwatch metrics integration
resource "cloudamqp_integration_metric" "cloudwatch" {
  instance_id = cloudamqp_instance.instance.id
  name        = "cloudwatch"
  # access_key_id = var.aws_access_key
  # secret_access_key = var.aws_secret_key
  # region = var.aws_region
}