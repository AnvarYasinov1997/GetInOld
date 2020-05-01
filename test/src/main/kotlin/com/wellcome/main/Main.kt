package com.wellcome.main

import com.wellcome.main.service.cache.InstitutionCacheService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@EnableAsync
@EnableCaching
@EnableScheduling
@EnableWebSecurity
@SpringBootApplication
@EnableTransactionManagement
open class Main {

    //    @Bean
    @Profile(value = ["!prod"])
    open fun flywayMigrationStrategy(): FlywayMigrationStrategy {
        return FlywayMigrationStrategy { flyway ->
            flyway.clean()
            flyway.migrate()
        }
    }

    companion object {
        private lateinit var applicationContext: ConfigurableApplicationContext

        fun restartApplication() {
            val args =
                applicationContext.getBean(ApplicationArguments::class.java)
            Thread {
                applicationContext.close()
                applicationContext =
                    SpringApplication.run(Main::class.java, *args.sourceArgs)
            }.also { it.isDaemon = false }.start()
        }

        fun setNewApplicationContext(newApplicationContext: ConfigurableApplicationContext) {
            applicationContext = newApplicationContext
        }
    }

}

fun main(args: Array<String>) {
    Main.setNewApplicationContext(SpringApplication.run(Main::class.java, *args))
}

@RestController
@RequestMapping
open class PingController {

    @Autowired
    private lateinit var institutionCacheService: InstitutionCacheService

    @GetMapping
    open fun ping(): Long {
        return 200
    }

    @GetMapping(value = ["/test"])
    open fun test() {
        val before = System.nanoTime()
        institutionCacheService.findByLocality(5)
        val after = System.nanoTime()
        println(after - before)
    }

}
