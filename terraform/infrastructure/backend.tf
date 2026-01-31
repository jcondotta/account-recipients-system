terraform {
  cloud {
    organization = "jcondotta"

    workspaces {
      name = "account-recipients-system"
    }
  }
}