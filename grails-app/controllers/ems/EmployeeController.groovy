package ems

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.*

@Secured(['ROLE_ADMIN'])
class EmployeeController {

    EmployeeService employeeService
    EmployeeManagerService employeeManagerService
    SpringSecurityService springSecurityService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond employeeService.list(params), model:[employeeCount: employeeService.count()]
    }

    def show(Long id) {
        respond employeeService.get(id)
    }

    def create() {
        def roles = Role.list()
        def supervisorList = Employee.list() // Changed: Get all employees instead of empty list
        respond new Employee(params), model: [roles: roles, supervisorList: supervisorList]
    }

    def save(Employee employee) {
        if (employee == null) {
            notFound()
            return
        }

        // Validate required transient fields
        if (!params.password || !params.username) {
            if(!params.password){
                employee.errors.rejectValue('password', 'default.null.message', ['password', 'Employee'] as Object[], 'Password cannot be null')
                respond employee.errors, view: 'create'
                return
            }
            if(!params.username){
                employee.errors.rejectValue('username', 'default.null.message', ['username', 'Employee'] as Object[], 'Username cannot be null')
                respond employee.errors, view: 'create'
                return
            }

            // Pass data back to form on error
            def roles = Role.list()
            def supervisorList = Employee.list()
            respond employee.errors, view: 'create', model: [roles: roles, supervisorList: supervisorList]
            return
        }

        // Set transient fields explicitly
        employee.username = params.username
        employee.password = params.password

        try {
            if (employeeManagerService.save(employee)) {
                request.withFormat {
                    form multipartForm {
                        flash.message = message(code: 'default.created.message', args: [message(code: 'employee.label', default: 'Employee'), employee.id])
                        redirect employee
                    }
                    '*' { respond employee, [status: CREATED] }
                }
            } else {
                // Pass data back to form on error
                def roles = Role.list()
                def supervisorList = Employee.list()
                respond employee.errors, view: 'create', model: [roles: roles, supervisorList: supervisorList]
            }
        } catch (ValidationException e) {
            def roles = Role.list()
            def supervisorList = Employee.list()
            respond employee.errors, view: 'create', model: [roles: roles, supervisorList: supervisorList]
        }
    }

    def edit(Long id) {
        def employee = employeeService.get(id)
        def roles = Role.list()
        def supervisorList = Employee.list()
        def currentUser = User.findByEmployee(employee) // Get the associated user

        respond employee, model: [roles: roles, supervisorList: supervisorList, currentUser: currentUser]
    }

    @Transactional
    def update(Employee employee) {
        if (employee == null) {
            notFound()
            return
        }

        try {
            // Save employee first
            if (!employeeService.save(employee)) {
                def roles = Role.list()
                def supervisorList = Employee.list()
                def currentUser = User.findByEmployee(employee)
                respond employee.errors, view:'edit', model: [roles: roles, supervisorList: supervisorList, currentUser: currentUser]
                return
            }

            // Handle username and password updates
            def user = User.findByEmployee(employee)
            if (user) {
                def usernameChanged = false
                def passwordChanged = false

                // Update username if provided and different
                if (params.username && params.username.trim() != user.username) {
                    // Check if username already exists for another user
                    def existingUser = User.findByUsername(params.username.trim())
                    if (existingUser && existingUser.id != user.id) {
                        employee.errors.rejectValue('username', 'username.unique', 'Username already exists')
                        def roles = Role.list()
                        def supervisorList = Employee.list()
                        def currentUser = User.findByEmployee(employee)
                        respond employee.errors, view:'edit', model: [roles: roles, supervisorList: supervisorList, currentUser: currentUser]
                        return
                    }
                    user.username = params.username.trim()
                    usernameChanged = true
                }

                // Update password if provided
                if (params.password && params.password.trim()) {
                    user.password = springSecurityService.encodePassword(params.password.trim())
                    passwordChanged = true
                }

                // Save user if any changes were made
                if (usernameChanged || passwordChanged) {
                    if (!user.save(flush: true)) {
                        log.error "Failed to update user: ${user.errors}"
                        employee.errors.rejectValue('username', 'user.update.failed', 'Failed to update user credentials')
                        def roles = Role.list()
                        def supervisorList = Employee.list()
                        def currentUser = User.findByEmployee(employee)
                        respond employee.errors, view:'edit', model: [roles: roles, supervisorList: supervisorList, currentUser: currentUser]
                        return
                    }
                }
            }

        } catch (ValidationException e) {
            def roles = Role.list()
            def supervisorList = Employee.list()
            def currentUser = User.findByEmployee(employee)
            respond employee.errors, view:'edit', model: [roles: roles, supervisorList: supervisorList, currentUser: currentUser]
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'employee.label', default: 'Employee'), employee.id])
                redirect employee
            }
            '*'{ respond employee, [status: OK] }
        }
    }

    @Transactional
    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }
        employeeManagerService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'employee.label', default: 'Employee'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employee.label', default: 'Employee'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
