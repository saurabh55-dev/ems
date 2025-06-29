<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
<div id="content" role="main">
    <div class="container">
        <section class="row">
            <div class="nav" role="navigation">
                <ul>
                    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                    <sec:ifAnyGranted roles="ROLE_DIRECTOR,ROLE_MANAGER">
                        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                    </sec:ifAnyGranted>
                </ul>
            </div>
        </section>

        <section class="row">
            <div id="list-task" class="col-12 content scaffold-list" role="main">
                <h1><g:message code="default.list.label" args="[entityName]" /></h1>
                <g:if test="${flash.message}">
                    <div class="message" role="status">${flash.message}</div>
                </g:if>

                <table class="table">
                    <thead>
                    <tr>
                        <th>Title</th>
                        <th>Description</th>
                        <th>Priority</th>
                        <th>Deadline</th>
                        <th>Assigned To</th>
                        <th>Assigned By</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${taskList}" var="task">
                        <tr>
                            <td>${task.title}</td>
                            <td>${task.description}</td>
                            <td>${task.priority}</td>
                            <td><g:formatDate date="${task.deadline}" format="dd-MM-yyyy"/></td>
                            <td>${task.assignedTo?.fullName} (${task.assignedTo?.role})</td>
                            <td>${task.assignedBy?.fullName}</td>
                            <td>
                                <span class="badge badge-${task.status == 'COMPLETED' ? 'success' : task.status == 'IN_PROGRESS' ? 'warning' : task.status == 'ASSIGNED' ? 'info' : 'secondary'}">
                                    ${task.status}
                                </span>
                            </td>
                            <td>
                                <g:link action="show" id="${task.id}" class="btn btn-sm btn-info">View</g:link>
                                <sec:ifAnyGranted roles="ROLE_DIRECTOR,ROLE_MANAGER">
                                    <g:link action="edit" id="${task.id}" class="btn btn-sm btn-primary">Edit</g:link>
                                </sec:ifAnyGranted>
                            </td>
                        </tr>
                    </g:each>

                    </tbody>
                </table>

                <g:if test="${taskCount > params.int('max')}">
                    <div class="pagination">
                        <g:paginate total="${taskCount ?: 0}" />
                    </div>
                </g:if>
            </div>
        </section>
    </div>
</div>

<asset:javascript src="application.js"/>
</body>
</html>