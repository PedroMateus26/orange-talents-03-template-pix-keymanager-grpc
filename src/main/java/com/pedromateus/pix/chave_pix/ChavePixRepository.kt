package com.pedromateus.pix.chave_pix

import com.pedromateus.pix.chave_pix.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository :JpaRepository<ChavePix, UUID>{
}