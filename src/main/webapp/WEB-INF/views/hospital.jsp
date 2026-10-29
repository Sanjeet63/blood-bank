<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hospital Dashboard - LifeLine Blood Bank</title>
</head>
<body>
<%@ include file="header.jspf" %>
<c:set var="url" value="${ctx}/hospital/home"/>

<div class="container">
    <div class="page-title">
        <h1>Hospital Dashboard</h1>
        <p>Check availability and request blood for your patients</p>
    </div>

    <div class="card">
        <div class="card-head"><h2>&#128202; Live Blood Stock</h2></div>
        <%@ include file="stock.jspf" %>
    </div>

    <div class="card">
        <div class="card-head">
            <h2>&#128269; Find Compatible Blood</h2>
            <form method="get" action="${url}" class="inline">
                <select name="group">
                    <c:forEach var="g" items="${applicationScope.bloodGroups}">
                        <option value="${g}" ${searchGroup == g ? 'selected' : ''}>${g}</option>
                    </c:forEach>
                </select>
                <button type="submit" class="sm">Search</button>
            </form>
        </div>
        <c:choose>
            <c:when test="${not empty searchGroup}">
                <div class="alert ${stock[searchGroup] > 0 ? 'success' : 'error'}">
                    Exact group <b>${searchGroup}</b>: <b>${stock[searchGroup]}</b> unit(s) available.
                </div>
                <p>A <b>${searchGroup}</b> patient can also receive blood from these groups:</p>
                <div class="groups">
                    <c:forEach var="g" items="${compatible}">
                        <div class="group ${stock[g] == 0 ? 'empty' : (stock[g] < 5 ? 'low' : 'ok')}">
                            <div class="name">${g}</div>
                            <div class="units">${stock[g]} unit(s)</div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise><p class="hint" style="text-align:left">Choose the patient's blood group to see compatible stock.</p></c:otherwise>
        </c:choose>
    </div>

    <div class="card">
        <div class="card-head"><h2>&#128221; Raise Blood Request</h2></div>
        <form method="post" action="${url}" class="form-row">
            <div><label>Patient Name</label><input type="text" name="patientName" required></div>
            <div><label>Blood Group</label>
                <select name="bloodGroup">
                    <c:forEach var="g" items="${applicationScope.bloodGroups}">
                        <option value="${g}" ${searchGroup == g ? 'selected' : ''}>${g}</option>
                    </c:forEach>
                </select>
            </div>
            <div><label>Units</label><input type="number" name="units" min="1" max="10" value="1" required></div>
            <div><button type="submit" class="btn-block" style="margin-top:0">Submit Request</button></div>
        </form>
    </div>

    <div class="card">
        <div class="card-head"><h2>&#128203; My Requests</h2></div>
        <div class="table-wrap">
            <table>
                <tr><th>#</th><th>Patient</th><th>Group</th><th>Units</th><th>Date</th><th>Status</th></tr>
                <c:forEach var="r" items="${requests}">
                    <tr>
                        <td>${r.requestId}</td>
                        <td><c:out value="${r.patientName}"/></td>
                        <td><span class="badge group-badge">${r.bloodGroup}</span></td>
                        <td>${r.units}</td>
                        <td>${r.requestDate}</td>
                        <td><span class="badge ${r.status}">${r.status}</span></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty requests}"><tr><td colspan="6" class="empty-row">No requests yet.</td></tr></c:if>
            </table>
        </div>
    </div>
</div>

<%@ include file="footer.jspf" %>
</body>
</html>
