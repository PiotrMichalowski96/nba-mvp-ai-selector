package pl.piter.commons.domain

data class Stats(
    val points: Int,
    val assists: Int,
    val rebounds: Int,
    val steals: Int,
    val blocks: Int,
) {
    override fun toString(): String =
        "$points points, $assists assists, $rebounds rebounds, $steals steals, $blocks blocks"
}
