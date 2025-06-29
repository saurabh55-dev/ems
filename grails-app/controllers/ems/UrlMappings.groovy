package ems

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/login/$action?"(controller: "login")
        "/login/auth"(controller: "login", action: "auth")
        "/logout"(controller: "logout", action: "index")
        
        "/"(controller: 'login', action: 'index')
        "500"(view:'/error')
        "404"(view:'/notFound')
        "403"(view:'/login/denied')
    }
}