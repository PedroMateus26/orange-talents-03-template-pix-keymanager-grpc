package com.pedromateus.pix.chave_pix.validacoes

import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.nova_chave.dtos.NovaChavePixRequest
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(
    val message: String = "Chave pix inválida!",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidPixKeyValidator : ConstraintValidator<ValidPixKey, NovaChavePixRequest> {
    override fun isValid(
        value: NovaChavePixRequest?,
        context: ConstraintValidatorContext?
    ): Boolean {
        if (value?.tipoDeChave == null) {
            println("DEU RUIM ${value?.tipoDeChave}")
            return false
        }
        return TipoDeChaveImpl.valueOf(value.tipoDeChave.name).valida(value.chave)
    }
}

