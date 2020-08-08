package com.wks.servicemarketplace.authservice.adapters.web.resources

import com.fasterxml.jackson.annotation.JsonProperty

data class GraphQLRequest(@JsonProperty("query") val query: String?,
                          @JsonProperty("operationName") val operationName: String?,
                          @JsonProperty("variables") val variables: Map<String, Any>?)