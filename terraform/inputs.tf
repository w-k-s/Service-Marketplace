variable "aws_access_key" {
  description = "AWS Access Key"
  type        = string
}

variable "aws_secret_key" {
  description = "AWS Secret Key"
  type        = string
}

variable "cloudamqp_region" {
  description = "AWS Region prefixed with amazon-web-services::"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID (VPC must have NAT gateway)"
  type        = string
}

variable "public_subnet_ids" {
  description = "VPC Public Subnet IDs e.g. [\"public-subnet-1\",\"public-subnet-2\"]"
  type        = list(string)
  default     = []
}

variable "private_subnet_ids" {
  description = "VPC Private Subnet IDs e.g. [\"private-subnet-1\",\"private-subnet-2\"]"
  type        = list(string)
  default     = []
}

variable "security_group_ids" {
  description = "Worker group security group ids e.g. [\"security-group1\",\"security-group-2\"]"
  type        = list(string)
  default     = []
}

variable "cloudamqp_customer_api_key" {
  description = "CloudAMQP Customer API Key"
  type        = string
}

variable "notification_email" {
  description = "Email address where notifications are sent e.g. from CloudAMQP"
  type        = string
}