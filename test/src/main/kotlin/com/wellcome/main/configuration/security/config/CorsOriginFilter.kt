package com.wellcome.main.configuration.security.config

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CorsOriginFilter @Autowired constructor(
    @Value(value = "\${accessControlAllowOrigin}") private val accessControlAllowOrigin: String
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE")
        response.setHeader("Allow", "POST, PUT, GET, OPTIONS, DELETE")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.setHeader("Access-Control-Allow-Headers", "jwt-token, Cache-Control, X-Authorization, Content-Type, Accept, X-Requested-With, remember-me, Accept-Ranges, Content-Encoding, Content-Length, Content-Type, DocumentToken")
        response.setHeader("Access-Control-Expose-Headers", "Accept-Ranges, Content-Encoding, Content-Length, Content-Type")
        if (notPreflight(request)) {
            filterChain.doFilter(request, response)
        }
    }

    private fun notPreflight(request: HttpServletRequest): Boolean {
        val api = StringUtils.countMatches(request.requestURI, "api")
        return if (api > 1) {
            false
        } else request.method != "OPTIONS"
    }

}