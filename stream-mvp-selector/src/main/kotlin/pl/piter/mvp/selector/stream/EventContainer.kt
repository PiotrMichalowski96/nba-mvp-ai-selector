package pl.piter.mvp.selector.stream

import pl.piter.commons.api.model.chatgpt.ChatGPTResponse
import pl.piter.commons.domain.NbaGameEvent

data class EventContainer(val nbaGameEvent: NbaGameEvent, val chatGPTResponse: ChatGPTResponse)
