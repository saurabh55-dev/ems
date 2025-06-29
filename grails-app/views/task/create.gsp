<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <title>Create Task</title>
</head>
<body>
<div class="container">
    <h1>Create Task</h1>

    <g:if test="${flash.error}">
        <div class="alert alert-danger">${flash.error}</div>
    </g:if>

    <g:form action="save">
        <div class="form-group">
            <label for="title">Title</label>
            <g:textField name="title" class="form-control" required="true"/>
        </div>

        <div class="form-group">
            <label for="description">Description</label>
            <g:textArea name="description" class="form-control" required="true"/>
        </div>

        <div class="form-group">
            <label for="priority">Priority</label>
            <g:select name="priority"
                      from="${['IMMEDIATE', 'HIGH', 'MEDIUM', 'NORMAL']}"
                      class="form-control"
                      required="true"/>
        </div>

        <div class="form-group">
            <label for="deadline">Deadline</label>
            <input type="date" name="deadline" class="form-control" required="true"/>
        </div>

        <div class="form-group">
            <label for="assignedTo">Assigned To</label>
            <g:select name="assignedTo"
                      from="${employeeList}"
                      optionKey="id"
                      optionValue="fullName"
                      class="form-control"
                      required="true"/>
        </div>

        <div class="form-group mt-3">
            <g:submitButton name="create" class="btn btn-primary" value="Create Task"/>
            <g:link action="index" class="btn btn-secondary">Cancel</g:link>
        </div>
    </g:form>
</div>
</body>
</html>