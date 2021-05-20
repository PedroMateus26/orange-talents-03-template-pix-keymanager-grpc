package com.pedromateus.pix.chave_pix

import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociada
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
data class ChavePix(
    @Column(nullable = false)
    var clieteId: UUID?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipoDeChave: TipoDeChaveImpl?,

    @Column(nullable = false)
    var chave: String?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipoDeConta: TipoDeContaImpl?,

    var contaAssociada: ContaAssociada?

) {
    @Id
    @GeneratedValue
    var id: UUID? = null

    @CreationTimestamp
    var criadoEm = LocalDateTime.now()

    init {
        if (tipoDeChave == TipoDeChaveImpl.ALEATORIA) {
            chave = UUID.randomUUID().toString()
        }
    }
    fun toCreateRequest(){
        var chave=this.chave
        if(tipoDeChave==TipoDeChaveImpl.ALEATORIA){
            chave=""
        }
    }
}