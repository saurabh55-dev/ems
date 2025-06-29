<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'notification.label', default: 'Notification')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
<a href="#list-notification" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="list-notification" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <g:if test="${flash.error}">
        <div class="errors" role="alert">${flash.error}</div>
    </g:if>

    <g:if test="${notificationList}">
        <table>
            <thead>
            <tr>
                <th>Type</th>
                <th>Message</th>
                <th>Task</th>
                <th>Assigned To</th>
                <th>Deadline</th>
                <th>Status</th>
                <th>Date Created</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${notificationList}" var="notification" status="i">
                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                    <td>
                        <g:if test="${notification.type == 'TASK_OVERDUE'}">
                            Task Overdue
                        </g:if>
                        <g:elseif test="${notification.type == 'TASK_ESCALATION'}">
                            Escalation
                        </g:elseif>
                        <g:else>
                            ${notification.type}
                        </g:else>
                    </td>
                    <td>${notification.message}</td>
                    <td>
                        <g:if test="${notification.task}">
                            ${notification.task.title}
                        </g:if>
                    </td>
                    <td>
                        <g:if test="${notification.task}">
                            ${notification.task.assignedTo.fullName}
                        </g:if>
                    </td>
                    <td>
                        <g:if test="${notification.task}">
                            <g:formatDate date="${notification.task.deadline}" format="dd/MM/yyyy"/>
                        </g:if>
                    </td>
                    <td>
                        <g:if test="${notification.task}">
                            ${notification.task.status}
                        </g:if>
                    </td>
                    <td>
                        <g:formatDate date="${notification.dateCreated}" format="dd/MM/yyyy HH:mm"/>
                    </td>
                    <td>
                        <g:if test="${notification.task}">
                            <g:link controller="task" action="show" id="${notification.task.id}">
                                View Task
                            </g:link>
                        </g:if>
                        <g:form controller="notification" action="delete" id="${notification.id}" method="DELETE" style="display:inline;">
                            <g:actionSubmit value="Delete" onclick="return confirm('Are you sure you want to delete this notification?');"/>
                        </g:form>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </g:if>
    <g:else>
        <p>No notifications found.</p>
    </g:else>

    <g:if test="${notificationCount > params.int('max')}">
        <div class="pagination">
            <g:paginate total="${notificationCount ?: 0}" />
        </div>
    </g:if>
</div>
</body>
</html>