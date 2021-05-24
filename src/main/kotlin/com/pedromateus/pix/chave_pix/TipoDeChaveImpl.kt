package com.pedromateus.pix.chave_pix

import com.pedromateus.pix.chave_pix.validacoes.testaRegexCpf
import com.pedromateus.pix.excecoes.ApiErrorException
import io.grpc.Status
import io.micronaut.http.HttpStatus
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator


enum class TipoDeChaveImpl {
    CPF {
        override fun valida(chave: String?): TipoDeChaveImpl {
            return when {
                chave.isNullOrBlank() -> {
                    val mensagem = "Ao menos um cpf deve ser informado!"
                    throw ApiErrorException(mensagem, HttpStatus.BAD_REQUEST, Status.FAILED_PRECONDITION)
                }
                    !chave.matches("^[0-9]{11}\$".toRegex()) -> {
                    val mensagem = "Cpf deve ter formato válido!"
                    throw ApiErrorException(mensagem, HttpStatus.BAD_REQUEST, Status.FAILED_PRECONDITION)
                }
                else -> CPF
            }
        }
    }
,
CELULAR{
    override fun valida(chave: String?): TipoDeChaveImpl {
        return when {

            chave.isNullOrBlank() -> {
                val mensagem = "Ao menos um celular deve ser informado!"
                throw ApiErrorException(mensagem, HttpStatus.BAD_REQUEST, Status.FAILED_PRECONDITION)
            }
            !chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex()) -> {
                val mensagem = "O celular deve ter formato válido!"
                throw ApiErrorException(mensagem, HttpStatus.BAD_REQUEST, Status.FAILED_PRECONDITION)
            }
            else -> CELULAR
        }
    }

},
EMAIL{
    override fun valida(chave: String?): TipoDeChaveImpl {
        return when {

            chave.isNullOrBlank() -> {
                val mensagem = "Ao menos um email deve ser informado!"
                throw ApiErrorException(mensagem, HttpStatus.BAD_REQUEST, Status.FAILED_PRECONDITION)
            }
            !chave.matches("^[A-Za-z0-9+_.-]+@(.+)$".toRegex())-> {
                val mensagem = "Emai deve ter formato válido!"
                throw ApiErrorException(mensagem, HttpStatus.BAD_REQUEST, Status.FAILED_PRECONDITION)
            }
            else -> CELULAR
        }
    }

},
ALEATORIA {
    override fun valida(chave: String?)=ALEATORIA
};

    abstract fun valida(chave:String?):Any

}