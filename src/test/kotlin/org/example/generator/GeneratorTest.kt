package org.example.generator

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.assertj.core.api.Assertions.assertThat

import org.example.processor.IntSumProcessor
import org.junit.jupiter.api.Test
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.test.assertEquals

class GeneratorTest {

    private fun compile(source: SourceFile) = KotlinCompilation().apply {
        inheritClassPath = true
        sources = listOf(source)
        annotationProcessors = listOf(IntSumProcessor()) //!!!
        verbose = false
    }.compile()


    @Test
    internal fun experimentWithResult() {
        val source = SourceFile.kotlin("File1.kt",
            GeneratorTest::class.java.classLoader.getResource("dataClass")!!.readText())
        val result = compile(source)
        val generatedFile = result.sourcesGeneratedByAnnotationProcessor[0].readText()
    }

    @Test
    internal fun testFailureWhenAbstractClass() {
        val source = SourceFile.kotlin("File1.kt",
            GeneratorTest::class.java.classLoader.getResource("abstract")!!.readText())
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertThat(result.messages).contains("error: @IntSum cannot be applied to abstract classes")
    }

    @Test
    internal fun testFailureWhenAnnotatedField() {
        val source = SourceFile.kotlin("File1.kt",
            GeneratorTest::class.java.classLoader.getResource("annotatedField")!!.readText())
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertThat(result.messages).contains("error: Only classes can be annotated with @IntSum")
    }

    @Test
    fun testWithDataClass() {
        val source = SourceFile.kotlin("File1.kt",
            GeneratorTest::class.java.classLoader.getResource("dataClass")!!.readText())
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val myDataClass = result.classLoader.loadClass("org.example.test.MyDataClass").kotlin

        val myDataClassObj = myDataClass.constructors.first().call(13, 3, "randomString")
        val myDataClassObjWithDefaultValues = myDataClass.createInstance()

        val sumIntsMethod = result.classLoader.loadClass("org.example.test.MyDataClassSummableKt")
            .declaredMethods.find { it.name == "sumInts" }

        val sumIntResult = sumIntsMethod!!.invoke(null, myDataClassObj) as Int
        val sumIntResultWithDefaultValues = sumIntsMethod.invoke(null, myDataClassObjWithDefaultValues) as Int

        assertThat(sumIntResult).isEqualTo(16) //13+3
        assertThat(sumIntResultWithDefaultValues).isEqualTo(108)  //99+9
    }

    @Test
    internal fun testWithTestClass() {
        val source = SourceFile.kotlin("File1.kt",
            GeneratorTest::class.java.classLoader.getResource("testClass.kt")!!.readText())
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val testClass = result.classLoader.loadClass("org.example.test.TestClass").kotlin
        val testClassObj = testClass.createInstance()
        testClass.declaredFunctions
            .filter {
                it.name.matches("test\\d*".toRegex()) }
            .onEach {
                it.call(testClassObj)
            }
    }

    @Test
    internal fun testWithDataClassWithList() {
        val source = SourceFile.kotlin("File1.kt",
            GeneratorTest::class.java.classLoader.getResource("dataClassWithList.kt")!!.readText())
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val testClass = result.classLoader.loadClass("org.example.test.DataClassWithListTest").kotlin
        val testClassObj = testClass.createInstance()
        testClass.declaredFunctions
            .filter {
                it.name.matches("test\\d*".toRegex()) }
            .onEach {
                it.call(testClassObj)
            }
    }

}