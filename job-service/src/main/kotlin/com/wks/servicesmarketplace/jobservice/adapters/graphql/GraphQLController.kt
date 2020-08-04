package com.wks.servicesmarketplace.jobservice.adapters.graphql

import com.wks.servicesmarketplace.jobservice.adapters.auth.keycloak.KeycloakRole
import com.wks.servicesmarketplace.jobservice.adapters.auth.keycloak.KeycloakUser
import com.wks.servicesmarketplace.jobservice.core.auth.User
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import org.apache.tomcat.jni.User.username
import org.keycloak.KeycloakPrincipal
import org.keycloak.KeycloakSecurityContext
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
import org.keycloak.representations.AccessToken
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


@RestController
class GraphQLController(private val graphQL: GraphQL) {


    @PostMapping(value = ["/graphql"],
            consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun execute(request: HttpServletRequest, @RequestBody body: Map<String?, Any?>): ExecutionResult {
        return graphQL.execute(ExecutionInput.newExecutionInput()
                .query(body["query"] as String?)
                .operationName(body["operationName"] as String?)
                .context(userFromToken(request))
                .build())
    }

    fun userFromToken(request: HttpServletRequest): User? {
        val token = request.userPrincipal as? KeycloakAuthenticationToken
        return token?.let {
            val principal = token.principal as KeycloakPrincipal<*>
            val session = principal.keycloakSecurityContext
            val accessToken: AccessToken = session.token

            return KeycloakUser(
                    accessToken.subject,
                    accessToken.scope,
                    accessToken.email,
                    accessToken.emailVerified,
                    accessToken.name,
                    accessToken.preferredUsername,
                    accessToken.givenName,
                    accessToken.familyName,
                    accessToken.resourceAccess[session.token.issuedFor]?.roles?.map { KeycloakRole(it) } ?: emptyList()
            )
        }
    }
}