<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Notifications</title>
    <style>
    .notification-item {
        border-left: 4px solid #17a2b8;
        transition: all 0.3s ease;
    }
    .notification-unread {
        background-color: #f8f9fa;
        border-left-color: #dc3545;
    }
    .notification-read {
        opacity: 0.8;
        border-left-color: #28a745;
    }
    .notification-escalated {
        border-left-color: #ffc107;
    }
    .notification-critical {
        border-left-color: #dc3545;
        background-color: #fff5f5;
    }
    </style>
</head>
<body>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-12">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="fas fa-bell"></i> Notifications</h2>

                <div>
                    <g:if test="${unreadCount > 0}">
                        <g:link action="markAllAsRead" class="btn btn-sm btn-outline-primary mr-2">
                            <i class="fas fa-check-double"></i> Mark All Read (${unreadCount})
                        </g:link>
                    </g:if>
                    <span class="badge badge-info">${notifications.size()} Total</span>
                </div>
            </div>

            <g:if test="${flash.message}">
                <div class="alert alert-success alert-dismissible fade show">
                    ${flash.message}
                    <button type="button" class="close" data-dismiss="alert">
                        <span>&times;</span>
                    </button>
                </div>
            </g:if>

            <g:if test="${flash.error}">
                <div class="alert alert-danger alert-dismissible fade show">
                    ${flash.error}
                    <button type="button" class="close" data-dismiss="alert">
                        <span>&times;</span>
                    </button>
                </div>
            </g:if>

            <div class="card">
                <div class="card-body">
                    <g:if test="${notifications}">
                        <g:each in="${notifications}" var="notification">
                            <div class="notification-item card mb-3 ${notification.isRead ? 'notification-read' : 'notification-unread'} ${notification.escalationLevel > 2 ? 'notification-critical' : (notification.escalationLevel > 1 ? 'notification-escalated' : '')}">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-start">
                                        <div class="flex-grow-1">
                                            <div class="d-flex align-items-center mb-2">
                                                <i class="fas ${notification.type == 'TASK_OVERDUE' ? 'fa-exclamation-triangle' : 'fa-info-circle'} mr-2
                                                   ${notification.escalationLevel > 2 ? 'text-danger' : (notification.escalationLevel > 1 ? 'text-warning' : 'text-info')}"></i>

                                                <span class="badge badge-${notification.escalationLevel > 2 ? 'danger' : (notification.escalationLevel > 1 ? 'warning' : 'info')} mr-2">
                                                    ${notification.escalationLevel > 1 ? 'ESCALATED L' + notification.escalationLevel : 'NORMAL'}
                                                </span>

                                                <small class="text-muted">
                                                    <g:formatDate date="${notification.sentDate}" format="dd/MM/yyyy HH:mm"/>
                                                </small>

                                                <g:if test="${!notification.isRead}">
                                                    <span class="badge badge-danger ml-2">New</span>
                                                </g:if>
                                            </div>

                                            <p class="mb-2">${notification.message}</p>

                                            <g:if test="${notification.relatedTask}">
                                                <div class="small text-muted">
                                                    <strong>Task:</strong> ${notification.relatedTask.title}<br>
                                                    <strong>Assignee:</strong> ${notification.relatedTask.assignedTo.firstName} ${notification.relatedTask.assignedTo.lastName}<br>
                                                    <strong>Deadline:</strong> <g:formatDate date="${notification.relatedTask.deadlineDate}" format="dd/MM/yyyy"/>
                                                    <span class="badge badge-${notification.relatedTask.status == 'COMPLETED' ? 'success' : (notification.relatedTask.status == 'CANCELLED' ? 'secondary' : 'warning')} ml-2">
                                                        ${notification.relatedTask.status}
                                                    </span>
                                                </div>
                                            </g:if>
                                        </div>

                                        <div class="ml-3">
                                            <g:if test="${!notification.isRead}">
                                                <g:link action="markAsRead" id="${notification.id}" class="btn btn-sm btn-outline-success mr-1" title="Mark as Read">
                                                    <i class="fas fa-check"></i>
                                                </g:link>
                                            </g:if>

                                            <g:link action="delete" id="${notification.id}" class="btn btn-sm btn-outline-danger"
                                                    onclick="return confirm('Are you sure you want to delete this notification?')" title="Delete">
                                                <i class="fas fa-trash"></i>
                                            </g:link>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </g:each>
                    </g:if>
                    <g:else>
                        <div class="text-center py-5">
                            <i class="fas fa-bell-slash fa-3x text-muted mb-3"></i>
                            <h5 class="text-muted">No notifications found</h5>
                            <p class="text-muted">You're all caught up!</p>
                        </div>
                    </g:else>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // Auto-refresh notification count in navbar
    function updateNotificationBadge() {
        $.ajax({
            url: '${createLink(controller: 'notification', action: 'getUnreadCount')}',
            type: 'GET',
            success: function(data) {
                const badge = $('#notification-badge');
                if (data.count > 0) {
                    badge.text(data.count).show();
                } else {
                    badge.hide();
                }
            }
        });
    }

    // Update badge on page load
    $(document).ready(function() {
        updateNotificationBadge();
    });
</script>

</body>
</html>