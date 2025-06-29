package ems

import grails.plugin.springsecurity.SpringSecurityService
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*
import grails.converters.JSON

class NotificationController {

    SpringSecurityService springSecurityService
    NotificationManagerService notificationManagerService
    NotificationService notificationService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        def currentUser = springSecurityService.getCurrentUser()
        def currentEmployee = currentUser?.employee

        if (!currentEmployee) {
            flash.error = "Employee profile not found"
            redirect(uri: '/')
            return
        }

        def notifications = Notification.findAllByRecipient(currentEmployee, [sort: 'sentDate', order: 'desc'])
        def unreadCount = Notification.countByRecipientAndIsRead(currentEmployee, false)

        [notifications: notifications, unreadCount: unreadCount, currentEmployee: currentEmployee]
    }

    def markAsRead(Long id) {
        def notification = Notification.get(id)
        def currentUser = springSecurityService.getCurrentUser()
        def currentEmployee = currentUser?.employee

        if (notification && notification.recipient == currentEmployee) {
            notification.markAsRead()
            flash.message = "Notification marked as read"
        } else {
            flash.error = "Notification not found or access denied"
        }

        redirect(action: 'index')
    }

    def markAllAsRead() {
        def currentUser = springSecurityService.getCurrentUser()
        def currentEmployee = currentUser?.employee

        if (currentEmployee) {
            def unreadNotifications = Notification.findAllByRecipientAndIsRead(currentEmployee, false)
            unreadNotifications.each { notification ->
                notification.markAsRead()
            }
            flash.message = "All notifications marked as read"
        }

        redirect(action: 'index')
    }

    def getUnreadCount() {
        def currentUser = springSecurityService.getCurrentUser()
        def currentEmployee = currentUser?.employee

        if (currentEmployee) {
            def count = Notification.countByRecipientAndIsRead(currentEmployee, false)
            render([count: count] as JSON)
        } else {
            render([count: 0] as JSON)
        }
    }

    def delete(Long id) {
        def notification = Notification.get(id)
        def currentUser = springSecurityService.getCurrentUser()
        def currentEmployee = currentUser?.employee

        if (notification && notification.recipient == currentEmployee) {
            notification.delete(flush: true)
            flash.message = "Notification deleted"
        } else {
            flash.error = "Notification not found or access denied"
        }

        redirect(action: 'index')
    }

    def show(Long id) {
        respond notificationService.get(id)
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
