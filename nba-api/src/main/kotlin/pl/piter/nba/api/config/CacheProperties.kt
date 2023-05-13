package pl.piter.nba.api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cache")
data class CacheProperties(
    val timeoutInMinutes: Long,
    val gameName: String,
    val gameListName: String,
)
