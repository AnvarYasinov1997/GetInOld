package com.wellcome.main.configuration.security.util

import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.http.HttpServletRequest

class SkipPathRequestMatcher(pathsToSkip: List<String>, processingPaths: List<String>) : RequestMatcher {

    private var matchers: OrRequestMatcher

    private var processingMatchers: List<RequestMatcher>

    init {
        matchers = OrRequestMatcher(pathsToSkip.map(::AntPathRequestMatcher).toList())
        processingMatchers = processingPaths.map(::AntPathRequestMatcher)
    }

    override fun matches(request: HttpServletRequest): Boolean {
        return if (matchers.matches(request)) {
            false
        } else {
            for (it in processingMatchers) {
                if (it.matches(request)) return true
            }
            return false
        }
    }

}