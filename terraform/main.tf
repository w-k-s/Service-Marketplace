terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.27"
    }
    cloudamqp = {
      source  = "cloudamqp/cloudamqp"
      version = "~> 1.9.1"
    }
  }

  backend "s3" {
    # This assumes we have a bucket created called io.wks.terraform
    bucket = "io.wks.terraform"
    key    = "services-marketplace.state.json"
    region = "ap-south-1"
  }
}

provider "aws" {
  profile = "default"
  region  = "ap-south-1"
}