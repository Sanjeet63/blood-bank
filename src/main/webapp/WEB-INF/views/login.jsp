<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login - LifeLine Blood Bank</title>
</head>
<body>
<%@ include file="header.jspf" %>

<div class="container">
    <div class="card auth">
        <h2>&#129656; Welcome Back</h2>
        <p class="sub">Login to your account</p>

        <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
        <c:if test="${not empty success}"><div class="alert success"><c:out value="${success}"/></div></c:if>

        <form method="post" action="${ctx}/login">
            <label>Username</label>
            <input type="text" name="username" placeholder="Enter username" required autofocus>
            <label>Password</label>
            <input type="password" name="password" placeholder="Enter password" required>
            <button type="submit" class="btn-block">Login</button>
        </form>

        <p class="center">New donor or hospital? <a href="${ctx}/register">Create an account</a></p>
        <p class="hint">Default admin: <b>admin</b> / <b>admin123</b></p>
    </div>
</div>

<%@ include file="footer.jspf" %>
</body>
</html>
