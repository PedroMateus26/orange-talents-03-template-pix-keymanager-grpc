package com.pedromateus.pix.chave_pix

import com.pedromateus.pix.chave_pix.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository :JpaRepository<ChavePix, UUID>{
    fun existsByChave(chave:String?):Boolean
    fun findByIdAndClienteId(chavePixID:UUID,clinteUUID:UUID):Optional<ChavePix>
    fun findByChave(chave:String):Optional<ChavePix>
    fun findAllByClienteId(clienteId:UUID):List<ChavePix>
}