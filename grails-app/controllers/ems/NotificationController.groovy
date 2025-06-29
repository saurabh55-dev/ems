package ems

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

@Secured(['ROLE_DIRECTOR', 'ROLE_MANAGER'])
class NotificationController {

    SpringSecurityService springSecurityService
    NotificationService notificationService
    NotificationManagerService notificationManagerService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 20, 100)

        // Get current user's employee record
        def currentUser = springSecurityService.currentUser
        def currentEmployee = Employee.findById(currentUser.employee?.id)

        if (!currentEmployee) {
            flash.error = "Employee record not found"
            redirect(controller: 'home', action: 'index')
            return
        }

        // Filter notifications specifically for the current employee
        def notifications = Notification.createCriteria().list(params) {
            eq('recipient', currentEmployee)
            order('dateCreated', 'desc')
        }

        def notificationCount = Notification.countByRecipient(currentEmployee)
        def unreadCount = Notification.countByRecipientAndIsRead(currentEmployee, false)

        respond notifications, model: [
                notificationCount: notificationCount,
                unreadCount: unreadCount,
                currentEmployee: currentEmployee
        ]
    }

    def show(Long id) {
        if (!id) {
            flash.error = "Notification ID is required"
            redirect(action: 'index')
            return
        }

        def notification = Notification.get(id)
        if (!notification) {
            flash.error = "Notification not found"
            redirect(action: 'index')
            return
        }

        // Check if current user is the recipient
        def currentUser = springSecurityService.currentUser
        if (!currentUser || !currentUser.employee) {
            flash.error = "User or employee record not found"
            redirect(action: 'index')
            return
        }
        def currentEmployee = Employee.get(currentUser.employee.id)

        if (!currentEmployee || notification.recipient != currentEmployee) {
            flash.error = "Access denied"
            redirect(action: 'index')
            return
        }

        // Mark as read when viewed
        if (!notification.isRead) {
            try {
                notificationManagerService.markAsRead(id)
            } catch (Exception e) {
                log.error("Error marking notification as read", e)
                // Don't fail the show action if marking as read fails
            }
        }

        respond notification
    }

    def create() {
        respond new Notification(params)
    }

    def save(Notification notification) {
        if (notification == null) {
            notFound()
            return
        }

        try {
            notificationService.save(notification)
        } catch (ValidationException e) {
            respond notification.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'notification.label', default: 'Notification'), notification.id])
                redirect notification
            }
            '*' { respond notification, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond notificationService.get(id)
    }

    def update(Notification notification) {
        if (notification == null) {
            notFound()
            return
        }

        try {
            notificationService.save(notification)
        } catch (ValidationException e) {
            respond notification.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'notification.label', default: 'Notification'), notification.id])
                redirect notification
            }
            '*'{ respond notification, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }
        notificationService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'notification.label', default: 'Notification'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'notification.label', default: 'Notification'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
