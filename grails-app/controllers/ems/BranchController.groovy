package ems

import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

@Secured(['ROLE_ADMIN'])
class BranchController {

    BranchService branchService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond branchService.list(params), model:[branchCount: branchService.count()]
    }

    def show(Long id) {
        respond branchService.get(id)
    }

    def create() {
        respond new Branch(params)
    }

    def save(Branch branch) {
        if (branch == null) {
            notFound()
            return
        }

        try {
            branchService.save(branch)
        } catch (ValidationException e) {
            respond branch.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'branch.label', default: 'Branch'), branch.id])
                redirect branch
            }
            '*' { respond branch, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond branchService.get(id)
    }

    def update(Branch branch) {
        if (branch == null) {
            notFound()
            return
        }

        try {
            branchService.save(branch)
        } catch (ValidationException e) {
            respond branch.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'branch.label', default: 'Branch'), branch.id])
                redirect branch
            }
            '*'{ respond branch, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        branchService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'branch.label', default: 'Branch'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'branch.label', default: 'Branch'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
