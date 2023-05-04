package pl.piter.commons.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RandomIDTest {

    @Test
    fun `given length when generate id then return alphanumeric string`() {
        //given
        val length = 10

        //when
        val id: String = generateId(length)

        //then
        assertThat(id)
            .isNotBlank()
            .hasSize(length)
    }
}