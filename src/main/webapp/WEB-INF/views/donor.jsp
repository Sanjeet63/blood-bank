<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Donor Dashboard - LifeLine Blood Bank</title>
</head>
<body>
<%@ include file="header.jspf" %>

<div class="container">
<c:choose>
    <c:when test="${empty donor}">
        <div class="card"><div class="alert error">Donor profile not found.</div></div>
    </c:when>
    <c:otherwise>
        <div class="page-title">
            <h1>Hello, <c:out value="${donor.name}"/> &#128075;</h1>
            <p>Thank you for being a life saver</p>
        </div>

        <div class="stats">
            <div class="stat"><div class="num">${donor.bloodGroup}</div><div class="label">Blood Group</div></div>
            <div class="stat green"><div class="num">${donations.size()}</div><div class="label">Total Donations</div></div>
            <div class="stat ${empty eligibility ? 'green' : 'amber'}">
                <div class="num">${empty eligibility ? '&#10004; Yes' : '&#10006; No'}</div>
                <div class="label">Eligible to Donate</div>
            </div>
        </div>

        <c:if test="${not empty eligibility}">
            <div class="alert warn"><c:out value="${eligibility}"/></div>
        </c:if>

        <div class="card">
            <div class="card-head"><h2>&#128100; My Profile</h2></div>
            <table class="kv">
                <tr><th>Name</th><td><c:out value="${donor.name}"/></td></tr>
                <tr><th>Age</th><td>${donor.age}</td></tr>
                <tr><th>Blood Group</th><td><span class="badge group-badge">${donor.bloodGroup}</span></td></tr>
                <tr><th>Phone</th><td><c:out value="${donor.phone}"/></td></tr>
                <tr><th>City</th><td><c:out value="${donor.city}"/></td></tr>
                <tr><th>Last Donation</th><td>${empty donor.lastDonation ? 'Never' : donor.lastDonation}</td></tr>
            </table>

            <h3 style="margin-top:24px">Update Contact</h3>
            <form method="post" action="${ctx}/donor/home" class="form-row">
                <div><label>Phone</label><input type="text" name="phone" value="<c:out value='${donor.phone}'/>" pattern="\d{10}" required></div>
                <div><label>City</label><input type="text" name="city" value="<c:out value='${donor.city}'/>"></div>
                <div><button type="submit" class="btn-block" style="margin-top:0">Update</button></div>
            </form>
        </div>

        <div class="card">
            <div class="card-head"><h2>&#128197; Donation History</h2></div>
            <table>
                <tr><th>#</th><th>Date</th><th>Units</th></tr>
                <c:forEach var="dn" items="${donations}">
                    <tr><td>${dn.donationId}</td><td>${dn.donationDate}</td><td>${dn.units}</td></tr>
                </c:forEach>
                <c:if test="${empty donations}"><tr><td colspan="3" class="empty-row">No donations yet. Visit the blood bank to donate!</td></tr></c:if>
            </table>
        </div>
    </c:otherwise>
</c:choose>
</div>

<%@ include file="footer.jspf" %>
</body>
</html>
