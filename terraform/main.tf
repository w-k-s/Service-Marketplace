terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "3.27"
    }
    cloudamqp = {
      source  = "cloudamqp/cloudamqp"
      version = "1.9.1"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.0.1"
    }

    local = {
      source  = "hashicorp/local"
      version = "2.0.0"
    }

    null = {
      source  = "hashicorp/null"
      version = "3.0.0"
    }

    template = {
      source  = "hashicorp/template"
      version = "2.2.0"
    }
  }

  backend "s3" {
    # This assumes we have a bucket created called io.wks.terraform
    bucket = "io.wks.terraform"
    key    = "services-marketplace.state.json"
    region = "ap-south-1"
  }

  required_version = "~> 0.14"
}

provider "aws" {
  profile = "default"
  region  = "ap-south-1"

  // This is necessary so that tags required for eks can be applied to the vpc without changes to the vpc wiping them out.
  // https://registry.terraform.io/providers/hashicorp/aws/latest/docs/guides/resource-tagging
  ignore_tags {
    key_prefixes = ["kubernetes.io/"]
  }
}