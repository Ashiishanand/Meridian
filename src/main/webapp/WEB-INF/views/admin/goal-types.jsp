<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Goal Parameters | Meridian</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/admin/nav.jsp">
  <jsp:param name="active" value="goaltypes"/>
</jsp:include>

<div class="container">
  <h2>Goal Parameters</h2>
  <p class="sub">Goal types and tracking metrics available to users.</p>

  <div class="panel">
    <h3>Add a goal type</h3>
    <form action="${pageContext.request.contextPath}/admin/goal-types" method="post">
      <input type="hidden" name="action" value="add">
      <div class="form-row">
        <div>
          <label for="name">Goal type</label>
          <input type="text" id="name" name="name" maxlength="60" required>
        </div>
        <div>
          <label for="description">Description</label>
          <input type="text" id="description" name="description" maxlength="255">
        </div>
        <div>
          <label for="metric">Tracking metric</label>
          <select id="metric" name="metric">
            <option value="HOURS">Hours</option>
            <option value="SESSIONS">Sessions</option>
            <option value="PERCENT">Percent</option>
          </select>
        </div>
        <button type="submit" class="btn">Save</button>
      </div>
    </form>
  </div>

  <div class="panel">
    <h3>Configured goal types</h3>
    <c:if test="${empty goalTypes}">
      <p class="empty">No goal types configured.</p>
    </c:if>
    <c:if test="${not empty goalTypes}">
      <div class="table-wrap">
        <table class="data">
          <thead>
            <tr><th>Name</th><th>Description</th><th>Metric</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            <c:forEach var="g" items="${goalTypes}">
              <tr>
                <td><c:out value="${g.name}"/></td>
                <td><c:out value="${g.description}"/></td>
                <td>${g.trackingMetric}</td>
                <td><span class="tag ${g.active ? 'tag-on' : 'tag-off'}">${g.active ? 'Enabled' : 'Disabled'}</span></td>
                <td>
                  <form class="inline" action="${pageContext.request.contextPath}/admin/goal-types" method="post">
                    <input type="hidden" name="action" value="toggle">
                    <input type="hidden" name="id" value="${g.id}">
                    <input type="hidden" name="active" value="${!g.active}">
                    <button type="submit" class="btn btn-sm btn-grey">${g.active ? 'Disable' : 'Enable'}</button>
                  </form>
                  <form class="inline" action="${pageContext.request.contextPath}/admin/goal-types" method="post"
                        onsubmit="return confirm('Delete this goal type?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${g.id}">
                    <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                  </form>
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
