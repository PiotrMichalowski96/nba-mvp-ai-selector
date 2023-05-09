package pl.piter.nba.api.mapper

import pl.piter.commons.api.model.nba.NbaGameMvp
import pl.piter.commons.domain.MvpEvent

fun MvpEvent.toNbaGameMvp() = NbaGameMvp(
    homeTeam = homeTeam,
    awayTeam = awayTeam,
    startTime = startTime,
    gameResult = gameResult,
    mvp = mvp,
    reason = commentAI
)

