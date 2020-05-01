package com.wellcome.main.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
@Configuration
@ConfigurationProperties(prefix = "firebase")
open class FirebaseProperty(var serviceAccount: String = "",
                            var databaseUrl: String = "",
                            var firebaseServerKey: String = "",
                            var firebaseApiUrl: String = "")