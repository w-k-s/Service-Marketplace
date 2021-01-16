package com.wks.servicemarketplace.serviceproviderservice.core

import com.wks.servicemarketplace.common.events.DomainEvent

data class ResultWithEvents<R,E: DomainEvent>(val result: R,
                                              val events: List<E> = emptyList())