<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body onload="document.f.username.focus();">
<div class="container">
        <div class="content">
            <c:if test="${not empty logout}">
            <p class="alert">${logout}</p>
        </c:if>
            <c:if test="${not empty error}">
                <p class="alert alert-error">${error}</p>
            </c:if>
        <h2>Login with Username and Password</h2>
        <form name="form" action="/login" method="POST">
            <fieldset>
                <input type="text" name="username" value="" placeholder="Username" />
                <input type="password" name="password" placeholder="Password" />
            </fieldset>
            <input type="submit" id="login" value="Login" class="btn btn-primary" />

        </form>
    </div>
</div>
</body>
</html>