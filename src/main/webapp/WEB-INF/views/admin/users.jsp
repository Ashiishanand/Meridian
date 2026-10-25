<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>User Accounts | Meridian</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/admin/nav.jsp">
  <jsp:param name="active" value="users"/>
</jsp:include>

<div class="container">
  <h2>User Accounts</h2>
  <p class="sub">Create accounts and control who can log in.</p>

  <div class="panel">
    <h3>Add a user</h3>
    <form action="${pageContext.request.contextPath}/admin/users" method="post">
      <input type="hidden" name="action" value="add">
      <div class="form-row">
        <div>
          <label for="name">Name</label>
          <input type="text" id="name" name="name" required>
        </div>
        <div>
          <label for="email">Email</label>
          <input type="email" id="email" name="email" required>
        </div>
        <div>
          <label for="password">Password</label>
          <input type="password" id="password" name="password" required>
        </div>
        <button type="submit" class="btn">Add user</button>
      </div>
    </form>
  </div>

  <div class="panel">
    <h3>All accounts</h3>
    <c:if test="${empty users}">
      <p class="empty">No accounts found.</p>
    </c:if>
    <c:if test="${not empty users}">
      <div class="table-wrap">
        <table class="data">
          <thead>
            <tr>
              <th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Status</th>
              <th>Joined</th><th>Last login</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="u" items="${users}">
              <tr>
                <td>${u.id}</td>
                <td><c:out value="${u.name}"/></td>
                <td><c:out value="${u.email}"/></td>
                <td>${u.roleLabel}</td>
                <td><span class="tag ${u.active ? 'tag-on' : 'tag-off'}">${u.active ? 'Active' : 'Inactive'}</span></td>
                <td>${u.createdAtText}</td>
                <td>${u.lastLoginText}</td>
                <td>
                  <c:if test="${u.role.name() eq 'USER'}">
                    <form class="inline" action="${pageContext.request.contextPath}/admin/users" method="post">
                      <input type="hidden" name="action" value="toggle">
                      <input type="hidden" name="id" value="${u.id}">
                      <input type="hidden" name="active" value="${!u.active}">
                      <button type="submit" class="btn btn-sm btn-grey">${u.active ? 'Deactivate' : 'Activate'}</button>
                    </form>
                    <form class="inline" action="${pageContext.request.contextPath}/admin/users" method="post"
                          onsubmit="return confirm('Delete this user and all their goals?');">
                      <input type="hidden" name="action" value="delete">
                      <input type="hidden" name="id" value="${u.id}">
                      <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                    </form>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:if>
  </div>
</div>
<div class="footer">Meridian &middot; Make Time. Move Forward.</div>
</body>
</html>
