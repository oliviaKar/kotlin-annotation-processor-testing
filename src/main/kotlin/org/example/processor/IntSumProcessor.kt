package org.example.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.asTypeName
import org.example.annotation.IntSum
import org.example.generator.process
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.Diagnostic.Kind

@AutoService(Processor::class)
@SupportedAnnotationTypes("org.example.annotation.IntSum")
class IntSumProcessor: AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>,
                         roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith((IntSum::class.java))
            .forEach {
                if (it.kind != ElementKind.CLASS) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Only classes can be annotated with @IntSum"
                    )
                    return false
                }
                if (it.modifiers.contains(Modifier.ABSTRACT)) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "@IntSum cannot be applied to abstract classes"
                    )
                    return false
                }
                else
                    it.process(processingEnv.filer)
            }
        return true
    }
}


