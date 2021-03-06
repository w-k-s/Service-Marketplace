variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "public_subnet_ids" {
  description = "VPC Public Subnet IDs"
  type        = list(string)
}

variable "private_subnet_ids" {
  description = "VPC Private Subnet IDs"
  type        = list(string)
}

variable "cloudamqp_customer_api_key" {
  description = "CloudAMQP Customer API Key"
  type        = string
}

variable "notification_email" {
  description = "Email address where notifications are sent e.g. from CloudAMQP"
  type        = string
}