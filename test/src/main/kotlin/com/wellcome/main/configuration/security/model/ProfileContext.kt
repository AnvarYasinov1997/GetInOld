package com.wellcome.main.configuration.security.model

class ProfileContext(val profileModels: List<ProfileModel>)

class ProfileModel(val login: String,
                   val accessKey: String,
                   val institutionId: Long,
                   val institutionProfileId: Long,
                   val authorities: List<String>)