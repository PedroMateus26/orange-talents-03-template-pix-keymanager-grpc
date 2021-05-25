package com.pedromateus.pix.chave_pix.bcb

import com.pedromateus.pix.TipoDeConta
import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

class CreatePixRequest(
    val keyType: String?,
    val key: TipoDeChaveImpl?,
    val bankAccount: ContaBancoRequest?,
    val owner: TitularRequest?
) {
}

class CreatePixResponse(
    val keyType: String?,
    val key: TipoDeChaveImpl?,
    val bankAccount: ContaBancoRequest?,
    val owner: TitularRequest?,
    val createdAt: String?
) {
    @CreationTimestamp
    val criadaEm = LocalDateTime.now()
}

class ContaBancoRequest(
    val participant: String?,
    val branch: String?,
    val accountNumber: String?,
    val accountType: TipoDeContaRequest?
) {}

class TitularRequest(
    val type: TipoDoTitular?,
    val name: String?,
    val taxIdNumber: String?
) {
}

enum class TipoDoTitular {
    NATURAL_PERSON, LEGAL_PERSON
}

enum class TipoDeContaRequest {
    CACC, SVGS
}

class DeletePixKeyRequest(
    val key: String?,
    val participant: String?
) {

}

class DeletePixKeyResponse(
    val key: String?,
    val participant: String?,
    val deletedAt: String?
) {

}