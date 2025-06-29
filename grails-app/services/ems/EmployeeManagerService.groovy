package ems

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService

@Transactional
class EmployeeManagerService {

    SpringSecurityService springSecurityService

    def save(Employee employee) {
        log.debug "Creating new employee: ${employee.username}"

        if (!employee.password || !employee.username) {
            log.error "Username and password are required"
            employee.errors.rejectValue('password', 'password.null', 'Username and password are required')
            return false
        }

        // save employee first (without username/password since they're transient)
        Employee savedEmployee = employee.save(flush: true)

        if (!savedEmployee) {
            log.error "Failed to save employee: ${employee.errors}"
            return false
        }

        // Create User instance
        def user = new User(
                username: employee.username,
                password: springSecurityService.encodePassword(employee.password),
                employee: savedEmployee,
                enabled: true,
                accountExpired: false,
                accountLocked: false,
                passwordExpired: false
        )

        if (!user.save(flush: true)) {
            log.error "Failed to save user: ${user.errors}"
            savedEmployee.delete(flush: true) // Clean up employee if user creation fails
            employee.errors = user.errors
            return false
        }

        def userRole = employee.role

        if (userRole) {
            UserRole.create(user, userRole, true)
        } else {
            log.error "Role not found: ${employee.role}"
            return false
        }
        return true
    }

    def list(Map params) {
        Employee.list(params)
    }

    def count() {
        Employee.count()
    }

    def get(id) {
        Employee.get(id)
    }

    def delete(id) {
        def employee = Employee.get(id)
        if (employee) {
            try{
                // Find the associated user by username
                def user = User.findByEmployee(employee)
                if (user) {
                    // Remove user roles first
                    UserRole.removeAll(user)

                    // Clear the employee reference from user to avoid constraint issues
                    user.employee = null
                    user.save(flush: true)

                    // Now delete the user
                    user.delete(flush: true)
                }
                // finally Delete the employee
                employee.delete(flush: true)
            }catch(Exception e){
                log.error "Error deleting employee ${id}: ${e.message}", e

                // Alternative approach: try to handle the constraint by setting user.employee to null
                try {
                    def user = User.findByEmployee(employee)
                    if (user) {
                        // Remove user roles first
                        UserRole.removeAll(user)

                        // Delete the employee first, then the user
                        employee.delete(flush: true)
                        user.delete(flush: true)
                    } else {
                        employee.delete(flush: true)
                    }
                } catch (Exception e2) {
                    log.error "Failed to delete employee even with alternative approach: ${e2.message}", e2
                    throw e2
                }
            }
        }
    }
}