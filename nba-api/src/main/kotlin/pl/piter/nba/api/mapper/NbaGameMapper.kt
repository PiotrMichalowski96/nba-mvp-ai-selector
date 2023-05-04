package pl.piter.nba.api.mapper

import pl.piter.commons.api.model.nba.NbaGame
import pl.piter.commons.api.model.scores.Game
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.commons.domain.GameResult

fun GameResponse.toNbaGame() = NbaGame(
    id = id,
    homeTeam = "${home.market} ${home.name}",
    awayTeam = "${away.market} ${away.name}",
    startTime = scheduled,
    gameResult = GameResult(home.points, away.points)
)

fun Game.toNbaGame() = NbaGame(
    id = id,
    homeTeam = home.name,
    awayTeam = away.name,
    startTime = scheduled,
    gameResult = GameResult(homePoints, awayPoints)
)

fun GameListResponse.toNbaGames(): List<NbaGame> = games
    .map { it.toNbaGame() }
    .toList()