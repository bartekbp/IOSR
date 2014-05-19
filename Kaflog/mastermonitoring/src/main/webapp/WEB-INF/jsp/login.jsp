<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.0.3/css/bootstrap.min.css"/>
    <script type="text/javascript" src="/webjars/jquery/2.0.3/jquery.min.js"></script>
    <title>Login</title>
    <style type="text/css">
        body {
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #eee;
        }

        .form-signin {
            max-width: 330px;
            padding: 15px;
            margin: 0 auto;
        }
        .form-signin .form-signin-heading,
        .form-signin .checkbox {
            margin-bottom: 10px;
        }
        .form-signin .checkbox {
            font-weight: normal;
        }
        .form-signin .form-control {
            position: relative;
            height: auto;
            -webkit-box-sizing: border-box;
            -moz-box-sizing: border-box;
            box-sizing: border-box;
            padding: 10px;
            font-size: 16px;
        }
        .form-signin .form-control:focus {
            z-index: 2;
        }
        .form-signin input[type="email"] {
            margin-bottom: -1px;
            border-bottom-right-radius: 0;
            border-bottom-left-radius: 0;
        }
        .form-signin input[type="password"] {
            margin-bottom: 10px;
            border-top-left-radius: 0;
            border-top-right-radius: 0;
        }
    </style>
</head>

<body onload="document.f.username.focus();">

<div class="container">
    <c:if test="${not empty logout}">
        <div style="text-align: center; background-color: #F4F8FA; border-color: #5BC0DE;"
             class="bs-callout bs-callout-info">
            <h4 class="alert">${logout}</h4>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div style="text-align: center; background-color: #FDF7F7; border-color: #D9534F;"
             class="bs-callout bs-callout-danger">
            <h4 class="alert alert-error">${error}</h4>
        </div>
    </c:if>

    <form class="form-signin" role="form" name="form" action="/login" method="POST">
        <h2 style="text-align: center;" class="form-signin-heading">Please sign in</h2>
        <input class="form-control" name="username" type="text" placeholder="username">
        <input class="form-control" type="password" name="password" placeholder="password">
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
    </form>
</div>

</body>

</html>