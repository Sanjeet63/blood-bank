<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register - LifeLine Blood Bank</title>
</head>
<body>
<%@ include file="header.jspf" %>
<script src="${ctx}/js/validate.js"></script>

<div class="container">
    <div class="card auth" style="max-width: 520px;">
        <h2>Create Account</h2>
        <p class="sub">Join as a donor or a hospital</p>

        <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

        <form method="post" action="${ctx}/register" onsubmit="return validateRegister(this)">
            <label>Register as</label>
            <select name="role" id="role" onchange="toggleDonorFields()">
                <option value="DONOR" ${param.role == 'HOSPITAL' ? '' : 'selected'}>&#129657; Donor</option>
                <option value="HOSPITAL" ${param.role == 'HOSPITAL' ? 'selected' : ''}>&#127973; Hospital</option>
            </select>

            <label>Username</label>
            <input type="text" name="username" value="<c:out value='${param.username}'/>" minlength="3" required>
            <div class="two-col">
                <div>
                    <label>Password</label>
                    <input type="password" name="password" minlength="6" required>
                </div>
                <div>
                    <label>Confirm Password</label>
                    <input type="password" name="confirm" required>
                </div>
            </div>

            <div id="donorFields">
                <label>Full Name</label>
                <input type="text" name="name" value="<c:out value='${param.name}'/>">
                <div class="two-col">
                    <div>
                        <label>Age</label>
                        <input type="number" name="age" value="<c:out value='${param.age}'/>" min="18" max="65">
                    </div>
                    <div>
                        <label>Blood Group</label>
                        <select name="bloodGroup">
                            <c:forEach var="g" items="${applicationScope.bloodGroups}">
                                <option value="${g}" ${param.bloodGroup == g ? 'selected' : ''}>${g}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="two-col">
                    <div>
                        <label>Phone (10 digits)</label>
                        <input type="text" name="phone" value="<c:out value='${param.phone}'/>" pattern="\d{10}">
                    </div>
                    <div>
                        <label>City</label>
                        <input type="text" name="city" value="<c:out value='${param.city}'/>">
                    </div>
                </div>
            </div>
            <button type="submit" class="btn-block">Register</button>
        </form>
        <p class="center">Already registered? <a href="${ctx}/login">Login</a></p>
    </div>
</div>
<script>toggleDonorFields();</script>

<%@ include file="footer.jspf" %>
</body>
</html>
