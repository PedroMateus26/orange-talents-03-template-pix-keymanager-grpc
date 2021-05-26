package com.pedromateus.pix.chave_pix.bcb

import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

data class CreatePixRequest(
    val keyType: String,
    val key: String,
    val bankAccount: ContaBancoRequest,
    val owner: TitularRequest
) {
}

data class CreatePixResponse(
    val keyType: String?,
    val key: String?,
    val bankAccount: ContaBancoRequest?,
    val owner: TitularRequest?,
    val createdAt: String?
) {
    @CreationTimestamp
    val criadaEm = LocalDateTime.now()
}

data class ContaBancoRequest(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: TipoDeContaRequest
) {}

data class TitularRequest(
    val type: TipoDoTitular,
    val name: String,
    val taxIdNumber: String
) {
}

enum class TipoDoTitular {
    NATURAL_PERSON, LEGAL_PERSON
}

enum class TipoDeContaRequest {
    CACC, SVGS
}

data class DeletePixKeyRequest(
    val key: String,
    val participant: String
) {

}

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: String
) {

}