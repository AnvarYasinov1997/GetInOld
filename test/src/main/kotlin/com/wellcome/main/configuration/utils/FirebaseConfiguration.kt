package com.wellcome.main.configuration.utils

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.wellcome.main.property.FirebaseProperty
import com.wellcome.main.util.functions.ifNotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
open class FirebaseConfiguration @Autowired constructor(
    private val firebaseProperty: FirebaseProperty
) {

    @Bean
    open fun firebaseApp(): FirebaseApp {
        val databaseUrl = firebaseProperty.databaseUrl
        val serviceAccount = firebaseProperty.serviceAccount
        val resource = ClassPathResource(serviceAccount)
        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(resource.inputStream))
            .setDatabaseUrl(databaseUrl)
            .build()
        return FirebaseApp.getApps().ifNotEmpty()
            ?.first()
            ?: FirebaseApp.initializeApp(options)
    }

    @Bean
    open fun firestoreApp(): Firestore {
        return FirestoreClient.getFirestore(firebaseApp())
    }
}