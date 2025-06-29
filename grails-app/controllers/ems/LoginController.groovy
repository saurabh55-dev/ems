package ems

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured

class LoginController {
    
    SpringSecurityService springSecurityService

    @Secured('permitAll')
    def index() {
        if (springSecurityService.isLoggedIn()) {
            redirect(controller: determineHomeController())
            return
        }
        render view: 'index'
    }

    @Secured('permitAll')
    def auth() {}
    
    @Secured('permitAll')
    def denied() {
        render view: 'denied'
    }

    private String determineHomeController() {
        def authorities = springSecurityService.principal.authorities
        def roles = authorities*.authority

        if (roles.contains('ROLE_ADMIN')) {
            return 'employee'
        } else {
            return 'task'
        }
    }
}