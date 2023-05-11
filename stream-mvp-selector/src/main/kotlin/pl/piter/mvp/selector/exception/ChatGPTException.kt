package pl.piter.mvp.selector.exception

import feign.FeignException

class ChatGPTException(message: String?) : RuntimeException(message) {

    companion object {
        fun of(e: FeignException) = ChatGPTException(e.message)
    }
}
