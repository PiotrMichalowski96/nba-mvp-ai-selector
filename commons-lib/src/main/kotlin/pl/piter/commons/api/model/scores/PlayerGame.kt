package pl.piter.commons.api.model.scores

import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerGame(
    val id: String,
    @JsonProperty("full_name")
    val fullName: String,
    val statistics: Statistics,
)
