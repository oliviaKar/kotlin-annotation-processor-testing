package org.example.test

import org.assertj.core.api.Assertions.assertThat
import org.example.annotation.IntSum
import org.junit.jupiter.api.Test

@IntSum
data class DataClassWithList(
    val myInt: Int = 9,
    val myOtherInt: Int = 99,
    val s: String = "",
    val listInt: List<Int> = listOf(1,2,3),
    val listString: List<String> = emptyList()
)

class DataClassWithListTest() {
    @Test
    fun test1() {
        val d = DataClassWithList()
        assertThat(d.sumInts()).isEqualTo(114) //9+99+1+2+3
    }

    @Test
    fun test2() {
        val d = DataClassWithList(listInt = listOf(10))
        assertThat(d.sumInts()).isEqualTo(118) //9+99+10
    }
}
