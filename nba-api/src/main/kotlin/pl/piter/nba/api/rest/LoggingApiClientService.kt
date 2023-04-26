package pl.piter.nba.api.rest

import org.slf4j.LoggerFactory.getLogger
import pl.piter.commons.api.model.scores.GameResponse

class LoggingApiClientService(private val service: ApiClientService) : ApiClientService by service {

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    override fun getGame(id: String): GameResponse? {
        logger.info("Call external NBA API to retrieve game with id: $id")
        return service.getGame(id)
    }
}