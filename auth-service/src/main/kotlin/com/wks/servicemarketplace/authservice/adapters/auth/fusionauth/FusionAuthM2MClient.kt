package com.wks.servicemarketplace.authservice.adapters.auth.fusionauth

import com.wks.servicemarketplace.authservice.core.Client

data class FusionAuthM2MClient(
        override val clientName: String,
        override val permissions: List<String>
) : Client