package com.wellcome.main.configuration.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wellcome.main.configuration.security.auth.login.LoginAuthenticationProcessingFilter
import com.wellcome.main.configuration.security.auth.token.ModerationTokenAuthenticationProcessingFilter
import com.wellcome.main.configuration.security.auth.token.TokenAuthenticationProcessingFilter
import com.wellcome.main.configuration.security.util.SecurityPaths
import com.wellcome.main.configuration.security.util.SkipPathRequestMatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.channel.ChannelProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
open class SecurityConfiguration : GlobalMethodSecurityConfiguration() {

    @Configuration
    open class WebSecurityConfig @Autowired constructor(
        private val objectMapper: ObjectMapper,
        private val corsOriginFilter: CorsOriginFilter,
        private val failureHandler: AuthenticationFailureHandler,
        private val successHandler: AuthenticationSuccessHandler,
        @Qualifier(value = "rest") private val authenticationEntryPoint: AuthenticationEntryPoint,
        @Qualifier(value = "login") private val loginAuthenticationProvider: AuthenticationProvider,
        @Qualifier(value = "admin-jwt") private val adminJwtTokenAuthenticationProvider: AuthenticationProvider,
        @Qualifier(value = "moderation-jwt") private val moderationJwtTokenAuthenticationProvider: AuthenticationProvider,
        @Qualifier(value = "google") private val googleTokenAuthenticationProvider: AuthenticationProvider
    ) : WebSecurityConfigurerAdapter() {

        @Autowired
        @Qualifier(value = "default")
        private lateinit var authenticationManager: AuthenticationManager

        @Bean(value = ["default"])
        @Throws(Exception::class)
        override fun authenticationManagerBean(): AuthenticationManager {
            return super.authenticationManagerBean()
        }

        override fun configure(web: WebSecurity?) {
            requireNotNull(web).ignoring().antMatchers(SecurityPaths.RESET_PASSWORD_ENTRY_POINT)
        }

        public override fun configure(auth: AuthenticationManagerBuilder?) {
            requireNotNull(auth).authenticationProvider(loginAuthenticationProvider)
            auth.authenticationProvider(adminJwtTokenAuthenticationProvider)
            auth.authenticationProvider(moderationJwtTokenAuthenticationProvider)
            auth.authenticationProvider(googleTokenAuthenticationProvider)
        }

        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            http.addFilterBefore(corsOriginFilter, ChannelProcessingFilter::class.java).cors().and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeRequests()
                .antMatchers(SecurityPaths.FORM_BASED_LOGIN_ENTRY_POINT).permitAll()
                .antMatchers(SecurityPaths.TOKEN_REFRESH_ENTRY_POINT).permitAll()
                .antMatchers(SecurityPaths.USER_INIT_ENTRY_POINT).permitAll()
                .and()
                .authorizeRequests()
                .antMatchers(SecurityPaths.ADMIN_TOKEN_BASED_AUTH_ENTRY_POINT).authenticated()
                .antMatchers(SecurityPaths.MODERATION_TOKEN_BASED_AUTH_ENTRY_POINT).authenticated()
                .and()
                .addFilterBefore(buildLoginAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .addFilterBefore(buildTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .addFilterBefore(buildModerationTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
        }

        private fun buildLoginAuthenticationProcessingFilter(): LoginAuthenticationProcessingFilter =
            LoginAuthenticationProcessingFilter(SecurityPaths.FORM_BASED_LOGIN_ENTRY_POINT, objectMapper, successHandler, failureHandler).also {
                it.setAuthenticationManager(this.authenticationManager)
            }

        private fun buildModerationTokenAuthenticationProcessingFilter(): ModerationTokenAuthenticationProcessingFilter {
            val matcher = SkipPathRequestMatcher("/mock".let(::listOf), SecurityPaths.MODERATION_TOKEN_BASED_AUTH_ENTRY_POINT.let(::listOf))
            return ModerationTokenAuthenticationProcessingFilter(matcher, this.failureHandler).also {
                it.setAuthenticationManager(this.authenticationManager)
            }
        }

        private fun buildTokenAuthenticationProcessingFilter(): TokenAuthenticationProcessingFilter {
            val pathsToSkip = listOf(
                SecurityPaths.TOKEN_REFRESH_ENTRY_POINT,
                SecurityPaths.FORM_BASED_LOGIN_ENTRY_POINT,
                SecurityPaths.USER_INIT_ENTRY_POINT
            )
            val pathsToProcess = listOf(
                SecurityPaths.ADMIN_TOKEN_BASED_AUTH_ENTRY_POINT
            )
            val matcher = SkipPathRequestMatcher(pathsToSkip, pathsToProcess)
            return TokenAuthenticationProcessingFilter(matcher, this.failureHandler).also {
                it.setAuthenticationManager(this.authenticationManager)
            }
        }

    }

}