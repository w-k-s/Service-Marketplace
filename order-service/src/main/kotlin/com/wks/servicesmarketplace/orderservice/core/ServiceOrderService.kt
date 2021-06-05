package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.common.CompanyUUID
import com.wks.servicemarketplace.common.CountryCode
import com.wks.servicemarketplace.common.CustomerUUID
import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.security.RolesAllowed

@Service
@Transactional
class ServiceOrderService constructor(val serviceOrderDao: ServiceOrderDao,
                                      val bidDao: BidDao) {

    fun createOrder(request: ServiceOrderRequest, authentication: Authentication): OrderIdResponse {

        val orderId = OrderUUID.random()
        val customerId = CustomerUUID.of(authentication.userId
                ?: throw CoreException(ErrorType.AUTHENTICATION, "userId not found"))

        serviceOrderDao.save(request.let {
            ServiceOrder.create(
                    orderId,
                    customerId,
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

        return OrderIdResponse(orderId)
    }

    fun getOrder(orderUUID: OrderUUID, authentication: Authentication): ServiceOrderResponse {
        return serviceOrderDao.findById(orderUUID)
                ?.let {
                    ServiceOrderResponse(
                            it.orderUUID,
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
        val companyId = serviceProviderApi.getCompanyFromUserId(authentication.userId)
        bidDao.findByCompanyId(companyId)?.let {
            return updateBid(bidRequest, it, authentication)
        }
        return BidUUIDResponse(createBid(bidRequest, serviceOrder, companyId, authentication))
    }

    private fun updateBid(bidRequest: BidRequest, bid: Bid, authentication: Authentication): BidUUIDResponse {
        bidDao.update(
                bid.uuid,
                bid.version,
                bid.copy(
                price = bidRequest.price,
                note = bidRequest.note,
                lastModifiedBy = authentication.name
        ))
        return BidUUIDResponse(bid.uuid)
    }

    private fun createBid(bidRequest: BidRequest, serviceOrder: ServiceOrder, companyId: CompanyUUID, authentication: Authentication): BidUUID {
        val bidUUID = BidUUID.random()
        bidDao.save(bidRequest.let{
            Bid(
                   bidUUID,
                   serviceOrder.orderUUID,
                   companyId,
                   it.price,
                   it.note,
                   authentication.name
            )
        })
        return bidUUID
    }
}