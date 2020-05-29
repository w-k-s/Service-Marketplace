package com.wks.servicesmarketplace.jobservice.core.repositories

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.entities.ServiceOrder
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceOrderQueryRepository : CrudRepository<ServiceOrder, String>