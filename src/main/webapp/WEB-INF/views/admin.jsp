<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Dashboard - LifeLine Blood Bank</title>
</head>
<body>
<%@ include file="header.jspf" %>
<c:set var="url" value="${ctx}/admin/home"/>

<div class="container">
    <div class="page-title">
        <h1>Admin Dashboard</h1>
        <p>Manage donors, blood stock and hospital requests</p>
    </div>

    <div class="stats">
        <div class="stat"><div class="num">${totalUnits}</div><div class="label">Units Available</div></div>
        <div class="stat green"><div class="num">${donorCount}</div><div class="label">Registered Donors</div></div>
        <div class="stat amber"><div class="num">${pendingCount}</div><div class="label">Pending Requests</div></div>
        <div class="stat"><div class="num">${expiringSoon}</div><div class="label">Expiring in 7 Days</div></div>
    </div>

    <div class="card">
        <div class="card-head"><h2>&#128202; Live Blood Stock</h2></div>
        <c:if test="${stock['O-'] < criticalLevel}">
            <div class="alert error"><b>CRITICAL:</b> O- (universal donor) stock is only ${stock['O-']} unit(s).</div>
        </c:if>
        <c:if test="${expired > 0}">
            <div class="alert warn">${expired} unit(s) have expired and are not counted in the stock.</div>
        </c:if>
        <%@ include file="stock.jspf" %>
    </div>

    <div class="card">
        <div class="card-head"><h2>&#127973; Blood Requests</h2></div>
        <div class="table-wrap">
            <table>
                <tr><th>#</th><th>Patient</th><th>Group</th><th>Units</th><th>Date</th><th>Status</th><th>Action</th></tr>
                <c:forEach var="r" items="${requests}">
                    <tr>
                        <td>${r.requestId}</td>
                        <td><c:out value="${r.patientName}"/></td>
                        <td><span class="badge group-badge">${r.bloodGroup}</span></td>
                        <td>${r.units}</td>
                        <td>${r.requestDate}</td>
                        <td><span class="badge ${r.status}">${r.status}</span></td>
                        <td>
                            <c:if test="${r.status == 'PENDING'}">
                                <form method="post" action="${url}" class="inline">
                                    <input type="hidden" name="requestId" value="${r.requestId}">
                                    <button name="action" value="approve" class="sm"
                                            onclick="return confirmAction('Approve and issue ${r.units} unit(s) of ${r.bloodGroup}?')">Approve &amp; Issue</button>
                                    <button name="action" value="reject" class="sm grey"
                                            onclick="return confirmAction('Reject this request?')">Reject</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty requests}"><tr><td colspan="7" class="empty-row">No requests yet.</td></tr></c:if>
            </table>
        </div>
    </div>

    <div class="card">
        <div class="card-head">
            <h2>&#129657; Donors</h2>
            <form method="get" action="${url}" class="inline">
                <select name="group">
                    <option value="">All groups</option>
                    <c:forEach var="g" items="${applicationScope.bloodGroups}">
                        <option value="${g}" ${param.group == g ? 'selected' : ''}>${g}</option>
                    </c:forEach>
                </select>
                <button type="submit" class="sm">Search</button>
            </form>
        </div>
        <div class="table-wrap">
            <table>
                <tr><th>ID</th><th>Name</th><th>Age</th><th>Group</th><th>Phone</th><th>City</th><th>Last Donation</th><th>Record Donation</th></tr>
                <c:forEach var="d" items="${donors}">
                    <tr>
                        <td>${d.donorId}</td>
                        <td><c:out value="${d.name}"/></td>
                        <td>${d.age}</td>
                        <td><span class="badge group-badge">${d.bloodGroup}</span></td>
                        <td><c:out value="${d.phone}"/></td>
                        <td><c:out value="${d.city}"/></td>
                        <td>${empty d.lastDonation ? '-' : d.lastDonation}</td>
                        <td>
                            <form method="post" action="${url}" class="inline">
                                <input type="hidden" name="action" value="donation">
                                <input type="hidden" name="donorId" value="${d.donorId}">
                                <input type="number" name="units" value="1" min="1" max="2" class="tiny">
                                <button type="submit" class="sm">+ Add</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty donors}"><tr><td colspan="8" class="empty-row">No donors found.</td></tr></c:if>
            </table>
        </div>
    </div>

    <div class="card">
        <div class="card-head"><h2>&#10133; Add Walk-in Donor</h2></div>
        <form method="post" action="${url}" class="form-row">
            <input type="hidden" name="action" value="addDonor">
            <div><label>Name</label><input type="text" name="name" required></div>
            <div><label>Age</label><input type="number" name="age" min="18" max="65" required></div>
            <div><label>Blood Group</label>
                <select name="bloodGroup">
                    <c:forEach var="g" items="${applicationScope.bloodGroups}"><option value="${g}">${g}</option></c:forEach>
                </select>
            </div>
            <div><label>Phone</label><input type="text" name="phone" pattern="\d{10}" placeholder="10 digits" required></div>
            <div><label>City</label><input type="text" name="city"></div>
            <div><button type="submit" class="btn-block" style="margin-top:0">Add Donor</button></div>
        </form>
    </div>
</div>

<%@ include file="footer.jspf" %>
</body>
</html>
