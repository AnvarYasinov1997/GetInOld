package com.wellcome.main.dto

data class InitResponse(val userId: Long,
                        val googleUid: String,
                        val name: String,
                        val localityName: String,
                        val topic: String,
                        val photoUrl: String)