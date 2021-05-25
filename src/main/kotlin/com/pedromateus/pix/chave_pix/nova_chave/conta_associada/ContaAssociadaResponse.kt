package com.pedromateus.pix.chave_pix.nova_chave.conta_associada

data class ContaAssociadaResponse(
    val tipo: String?,
    var instituicao: InstituicaoResponse?,
    val agencia: String?,
    val numero: String?,
    val titular: TitularResponse?

) {

    fun toContaAssociada(): ContaAssociada {
        return ContaAssociada(
            instituicao = this.instituicao?.nome,
            nomeDoTitular = this.titular?.nome,
            cpfTitular = this.titular?.cpf,
            agencia = this?.agencia,
            numeroDaConta = this?.numero,
            ispb=this.instituicao?.ispb
        )
    }

}

data class TitularResponse(val id: String?, val nome: String?, val cpf: String?)
data class InstituicaoResponse(val nome: String?, val ispb: String?)