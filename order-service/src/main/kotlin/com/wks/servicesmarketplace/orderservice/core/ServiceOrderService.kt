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
                                      val bidDao: BidDao,
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

    fun createOrUpdateBid(bidRequest: BidRequest, orderId: OrderUUID, authentication: Authentication): BidUUIDResponse {
        val serviceOrder = serviceOrderDao.findById(orderId)
                ?: throw CoreException(ErrorType.RESOURCE_NOT_FOUND, "Order $orderId not found")
        val userId = authentication.userId
                ?: throw CoreException(ErrorType.AUTHENTICATION, "UserId missing from token")
        val company = internalServiceProviderClient.companyFromUserId(userId)
        bidDao.findByCompanyUUID(company.uuid)?.let {
            return updateBid(bidRequest, it, authentication)
        }
        return BidUUIDResponse(createBid(bidRequest, serviceOrder, company.id, authentication))
    }

    private fun updateBid(bidRequest: BidRequest, bid: Bid, authentication: Authentication): BidUUIDResponse {
        bidDao.update(
                bid.id,
                bid.version,
                bid.copy(
                        price = bidRequest.price,
                        note = bidRequest.note,
                        lastModifiedBy = authentication.name
                ))
        return BidUUIDResponse(bid.uuid)
    }

    private fun createBid(bidRequest: BidRequest, serviceOrder: ServiceOrder, companyId: CompanyId, authentication: Authentication): BidUUID {
        val bidId = bidDao.nextBidId()
        val bidUUID = BidUUID.random()
        bidDao.save(bidRequest.let {
            Bid(
                    bidId,
                    bidUUID,
                    serviceOrder.id,
                    companyId,
                    it.price,
                    it.note,
                    authentication.name
            )
        })
        return bidUUID
    }
}