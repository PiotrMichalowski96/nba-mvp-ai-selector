package pl.piter.nba.api.exception

import feign.FeignException

class ScoreApiException(message: String?) : RuntimeException(message) {

    companion object {
        fun of(e: FeignException) = ScoreApiException(e.message)
    }
}