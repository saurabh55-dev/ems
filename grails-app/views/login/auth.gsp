<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Login</title>
</head>
<body>
    <div class="container">
        <h2>Login</h2>
        <g:if test='${flash.message}'>
            <div class="alert alert-danger">${flash.message}</div>
        </g:if>
        <form action="${postUrl ?: '/login/authenticate'}" method="POST" id="loginForm" class="form">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" class="form-control" name="username" id="username" required/>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" class="form-control" name="password" id="password" required/>
            </div>
            <button type="submit" class="btn btn-primary">Login</button>
        </form>
    </div>
</body>
</html>