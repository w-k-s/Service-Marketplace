package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.api.InternalServiceProviderClient
import com.wks.servicemarketplace.common.CompanyId
import com.wks.servicemarketplace.common.CountryCode
import com.wks.servicemarketplace.common.CustomerUUID
import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ServiceOrderService constructor(val serviceOrderDao: ServiceOrderDao,
                                      val quoteDao: QuoteDao,
                                      val internalServiceProviderClient: InternalServiceProviderClient) {

    fun createOrder(request: ServiceOrderRequest, authentication: Authentication): OrderIdResponse {

        val orderId = serviceOrderDao.nextOrderId()
        val orderUUID = OrderUUID.random()
        val customerUUID = CustomerUUID.of(authentication.userId
                ?: throw CoreException(ErrorType.AUTHENTICATION, "userId not found"))

        serviceOrderDao.save(request.let {
            ServiceOrder.create(
                    orderId,
                    orderUUID,
                    customerUUID,
                    com.wks.servicemarketplace.common.Service.of(it.serviceCode),
                    it.title,
                    it.description,
                    Address.create(
                            it.address.line1,
                            it.address.line2,
                            it.address.city,
                            CountryCode.of(it.address.country),
                            it.address.latitude,
                            it.address.longitude
                    ),
                    it.orderDateTime,
                    ServiceOrderStatus.VERIFYING,
                    authentication
            )
        })

        return OrderIdResponse(orderUUID)
    }

    fun getOrder(orderUUID: OrderUUID, authentication: Authentication): ServiceOrderResponse {
        return serviceOrderDao.findById(orderUUID)
                ?.let {
                    ServiceOrderResponse(
                            it.uuid,
                            it.customerUUID,
                            it.serviceCode,
                            it.title,
                            it.description,
                            it.status,
                            it.orderDateTime,
                            it.createdDate,
                            it.rejectReason,
                            it.version
                    )
                } ?: throw
        CoreException(
                ErrorType.RESOURCE_NOT_FOUND,
                "Order $orderUUID not found"
        )
    }

    fun createOrUpdateQuote(createQuoteRequest: CreateQuoteRequest, orderId: OrderUUID, authentication: Authentication): QuoteUUIDResponse {
        val serviceOrder = serviceOrderDao.findById(orderId)
                ?: throw CoreException(ErrorType.RESOURCE_NOT_FOUND, "Order $orderId not found")
        val userId = authentication.userId
                ?: throw CoreException(ErrorType.AUTHENTICATION, "UserId missing from token")
        val company = internalServiceProviderClient.companyFromUserId(userId)
        quoteDao.findByCompanyUUID(company.uuid)?.let {
            return updateQuote(createQuoteRequest, it, authentication)
        }
        return QuoteUUIDResponse(createQuote(createQuoteRequest, serviceOrder, company.id, authentication))
    }

    private fun updateQuote(createQuoteRequest: CreateQuoteRequest, quote: Quote, authentication: Authentication): QuoteUUIDResponse {
        quoteDao.update(
                quote.id,
                quote.version,
                quote.copy(
                        price = createQuoteRequest.price,
                        note = createQuoteRequest.note,
                        lastModifiedBy = authentication.name
                ))
        return QuoteUUIDResponse(quote.uuid)
    }

    private fun createQuote(createQuoteRequest: CreateQuoteRequest, serviceOrder: ServiceOrder, companyId: CompanyId, authentication: Authentication): QuoteUUID {
        val quoteId = quoteDao.nextQuoteId()
        val quoteUUID = QuoteUUID.random()
        quoteDao.save(createQuoteRequest.let {
            Quote(
                    quoteId,
                    quoteUUID,
                    serviceOrder.id,
                    companyId,
                    it.price,
                    it.note,
                    authentication.name
            )
        })
        return quoteUUID
    }
}