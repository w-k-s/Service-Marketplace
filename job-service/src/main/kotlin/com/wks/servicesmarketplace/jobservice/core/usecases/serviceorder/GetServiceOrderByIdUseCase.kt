package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.aggregates.ServiceOrder
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.queries.GetServiceOrderByIdQuery
import com.wks.servicesmarketplace.jobservice.core.usecases.UseCase
import org.axonframework.modelling.command.Repository
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class GetServiceOrderByIdUseCase(private val queryGateway: QueryGateway,
                                 private val serviceOrderEventSourcingRepository: Repository<ServiceOrder>) : UseCase<GetServiceOrderByIdQuery, ServiceOrderResponse> {

    override fun execute(request: GetServiceOrderByIdQuery): ServiceOrderResponse {
        return queryGateway.query(request, ServiceOrderResponse::class.java).get()
    }

    @QueryHandler
    fun getQueryOrderById(query: GetServiceOrderByIdQuery): ServiceOrderResponse {
        val future = CompletableFuture<ServiceOrder>()
        serviceOrderEventSourcingRepository.load(query.orderId).execute { future.complete(it) }
        return future.get().let {
            ServiceOrderResponse(
                    it.orderId,
                    it.customerId,
                    it.serviceCategoryId,
                    it.title,
                    it.description,
                    it.status,
                    it.orderDateTime,
                    it.createdDate,
                    it.rejectReason,
                    it.lastModifiedDate
            )
        }
    }
}