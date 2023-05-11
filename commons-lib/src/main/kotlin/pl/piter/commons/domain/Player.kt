package pl.piter.commons.domain

import jakarta.validation.constraints.NotBlank

data class Player(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val team: String,
    val stats: Stats,
)
