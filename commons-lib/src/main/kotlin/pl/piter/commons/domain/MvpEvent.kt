package pl.piter.commons.domain

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.time.OffsetDateTime

data class MvpEvent(
    @field:NotBlank
    val gameId: String,
    @field:NotBlank
    val homeTeam: String,
    @field:NotBlank
    val awayTeam: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    val startTime: OffsetDateTime,
    val gameResult: GameResult,
    @field:Valid
    val mvp: Player,
    @field:NotBlank
    val commentAI: String,
)
