package com.wks.servicesmarketplace.jobservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import graphql.servlet.ObjectMapperConfigurer
import org.springframework.stereotype.Component

@Component
class GraphQLConfiguration : ObjectMapperConfigurer {
    override fun configure(mapper: ObjectMapper?) {
        // TODO: Doesn't work, need to find out why
        mapper?.registerModule(JavaTimeModule())
                ?.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
}