package com.pedromateus.pix.chave_pix.nova_chave.conta_associada

import javax.persistence.Embeddable

@Embeddable
data class ContaAssociada(

    var instituicao:String?,
    var nomeDoTitular:String?,
    var cpfTitular:String?,
    var agencia:String?,
    var numeroDaConta:String?


) {

}
