<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>LifeLine Blood Bank</title>
</head>
<body>
<%@ include file="header.jspf" %>

<section class="hero">
    <div class="container">
        <h1>Donate Blood, Save Lives</h1>
        <p>Register donors, track live blood stock and handle hospital requests online. No paper registers, no delays.</p>
        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <a href="${ctx}/register" class="btn btn-white">Become a Donor</a>
                <a href="${ctx}/login" class="btn btn-white">Login</a>
            </c:when>
            <c:otherwise>
                <a href="${ctx}/${sessionScope.user.role.toLowerCase()}/home" class="btn btn-white">Go to Dashboard</a>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<div class="container">
    <div class="card lift">
        <div class="card-head"><h2>&#128202; Live Blood Stock</h2></div>
        <c:choose>
            <c:when test="${not empty dbError}"><div class="alert warn">${dbError}</div></c:when>
            <c:otherwise><%@ include file="stock.jspf" %></c:otherwise>
        </c:choose>
    </div>

    <div class="features">
        <div class="card feature">
            <div class="icon">&#129657;</div>
            <h3>Donor Registration</h3>
            <p>Register online, with automatic eligibility checks for age and donation gap.</p>
        </div>
        <div class="card feature">
            <div class="icon">&#127973;</div>
            <h3>Hospital Requests</h3>
            <p>Hospitals search availability and raise blood requests in seconds.</p>
        </div>
        <div class="card feature">
            <div class="icon">&#9200;</div>
            <h3>Expiry Tracking</h3>
            <p>Every unit gets a 35-day expiry. The oldest units are issued first.</p>
        </div>
        <div class="card feature">
            <div class="icon">&#128274;</div>
            <h3>Secure Access</h3>
            <p>Role-based login with hashed passwords and protected pages.</p>
        </div>
    </div>

    <div class="card">
        <div class="card-head"><h2>&#128260; Blood Group Compatibility</h2></div>
        <div class="table-wrap">
            <table>
                <tr><th>Patient Group</th><th>Can Receive From</th></tr>
                <c:forEach var="row" items="${chart}">
                    <tr><td><span class="badge group-badge">${row.key}</span></td><td>${row.value}</td></tr>
                </c:forEach>
            </table>
        </div>
        <p class="hint">O- is the universal donor. AB+ is the universal recipient.</p>
    </div>
</div>

<%@ include file="footer.jspf" %>
</body>
</html>
