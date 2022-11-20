package org.example.generator

import com.squareup.kotlinpoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.lang.model.type.TypeKind

fun Element.process(filer: Filer) {
    val fileSpecDetails = toFileSpecDetails()

    FileSpec.builder(fileSpecDetails.pack, fileSpecDetails.fileName)
        .addFunction(FunSpec.builder("sumInts")
            .receiver(this.asType().asTypeName())
            .returns(Int::class)
            .addStatement("return ${intFieldsAsList().joinToString(" + ")}")
            .build()
        )
        .build()
        .writeTo(filer)
}

private fun Element.intFieldsAsList() = enclosedElements
                                .filter { it.kind.isField }
                                .filter {
                                    it.filterInt()
                                }
                                .map {
                                    it.toSumInts()
                                }
                                .ifEmpty { listOf("0") }

private fun Element.filterInt() =
    if (asType().asTypeName() is ParameterizedTypeName)
        ((asType().asTypeName() as ParameterizedTypeName).typeArguments[0] as ClassName).canonicalName == "java.lang.Integer"
    else
        asType().kind == TypeKind.INT

private fun Element.toSumInts() =
    if (asType().asTypeName() is ParameterizedTypeName)
        "${simpleName}.sum()"
    else
        simpleName.toString()

private fun Element.toFileSpecDetails() =
    with(asType().asTypeName() as ClassName) {
        FileSpecDetails(
            pack = packageName,
            fileName = "${simpleName}Summable"
        )
    }

data class FileSpecDetails(val pack: String, val fileName: String)