package com.pedromateus.pix.chave_pix.bcb

import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import com.pedromateus.pix.chave_pix.busca_chave_pix_unica.ChavePixInfo
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociada
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

data class PixKeyDetailsResponse(
    val keyType:TipoDeChaveImpl,
    val key:String,
    val bankAccountResponse:ContaBancoRequest,
    val owner:TitularRequest,
    val createdAt:String
){
    fun toContaAssociada():ContaAssociada{
        return ContaAssociada(
            instituicao = "ITAU",
            nomeDoTitular = owner.name,
            cpfTitular = owner.taxIdNumber,
            agencia = bankAccountResponse.branch,
            numeroDaConta = bankAccountResponse.accountNumber,
            ispb = bankAccountResponse.participant
        )
    }
    fun toChavePixInfo():ChavePixInfo{
        return ChavePixInfo(
            keyType = keyType,
            key = key,
            banckAccountResponse=toContaAssociada(),
            tipoDeConta = TipoDeContaImpl.valueOf(bankAccountResponse.accountType.name),
            createdAt = createdAt
        )
    }

}
