package com.wks.servicemarketplace.accountservice.adapters.auth.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ClaimsStruct {

    private static class ResourceAccess {
        @JsonProperty("service-marketplace-app")
        Client client;

        private Client getClient() {
            return client;
        }
    }

    private static class Client {
        @JsonProperty("roles")
        private List<String> roles;

        private List<String> getRoles() {
            return roles;
        }
    }

    @JsonProperty("resource_access")
    ResourceAccess resourceAccess;

    public List<String> getRoles() {
        return Optional.of(resourceAccess)
                .map(ResourceAccess::getClient)
                .map(Client::getRoles)
                .orElseGet(Collections::emptyList);
    }
}
