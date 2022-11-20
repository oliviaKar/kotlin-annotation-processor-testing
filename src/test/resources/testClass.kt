
package org.example.test

import org.example.annotation.IntSum
import org.assertj.core.api.Assertions.assertThat


@IntSum
class MyClass {
    var int1: Int = 0
    var int2: Int = 0

}

@IntSum
data class MyDataClass(
    val int1: Int = 9,
    val int2: Int = 99,
    val s: String = ""
)

class TestClass {

    fun test1() {
        val obj = MyClass()
        assertThat(obj.sumInts()).isEqualTo(0)
    }

    fun test2() {
        val obj = MyClass()
        obj.int1 = 100
        assertThat(obj.sumInts()).isEqualTo(100)
    }

    fun test3() {
        val obj = MyDataClass()
        assertThat(obj.sumInts()).isEqualTo(108)
    }

    fun test4() {
        val obj = MyDataClass(int1 = 5, int2 = 10)
        assertThat(obj.sumInts()).isEqualTo(15)
    }
}