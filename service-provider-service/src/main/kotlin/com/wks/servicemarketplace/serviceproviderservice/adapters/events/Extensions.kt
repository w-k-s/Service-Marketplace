package com.wks.servicemarketplace.serviceproviderservice.adapters.events

import com.rabbitmq.client.Delivery

fun Delivery.authorization(headerName: String = "Authorization",
                           tokenType: String = "Bearer") = properties.headers[headerName].toString().substring(tokenType.length).trim { it <= ' ' }