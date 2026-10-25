<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>My Goals | Meridian</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/user/nav.jsp">
  <jsp:param name="active" value="goals"/>
</jsp:include>

<div class="container">
  <h2>My Goals</h2>
  <p class="sub">Set a target and a deadline for what you want to achieve.</p>

  <div class="panel">
    <h3>New goal</h3>
    <form action="${pageContext.request.contextPath}/user/goals" method="post">
      <input type="hidden" name="action" value="add">
      <div class="form-row">
        <div>
          <label for="name">Goal name</label>
          <input type="text" id="name" name="name" maxlength="150" required>
        </div>
        <div>
          <label for="typeId">Type</label>
          <select id="typeId" name="typeId" required>
            <c:forEach var="t" items="${goalTypes}">
              <option value="${t.id}"><c:out value="${t.name}"/> (${t.trackingMetric})</option>
            </c:forEach>
          </select>
        </div>
        <div>
          <label for="target">Target (hours)</label>
          <input type="number" id="target" name="target" min="0.5" max="1000" step="0.5" required>
        </div>
        <div>
          <label for="deadline">Deadline</label>
          <input type="date" id="deadline" name="deadline" min="${today}" required>
        </div>
        <button type="submit" class="btn">Create goal</button>
      </div>
    </form>
  </div>

  <div class="panel">
    <h3>All goals</h3>
    <c:if test="${empty goals}">
      <p class="empty">You have not set any goals yet.</p>
    </c:if>
    <c:if test="${not empty goals}">
      <div class="table-wrap">
        <table class="data">
          <thead>
            <tr><th>Goal</th><th>Type</th><th>Logged / Target</th><th>Progress</th><th>Deadline</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            <c:forEach var="g" items="${goals}">
              <tr>
                <td><c:out value="${g.name}"/></td>
                <td><c:out value="${g.goalTypeName}"/></td>
                <td>${g.loggedText} / ${g.targetText} h</td>
                <td>
                  <div class="progress"><div class="progress-bar" style="width: ${g.progressPercent}%"></div></div>
                  ${g.progressPercent}%
                </td>
                <td>${g.deadlineText}<br><small>${g.deadlineNote}</small></td>
                <td><span class="tag tag-${g.status.name().toLowerCase()}">${g.status}</span></td>
                <td>
                  <form class="inline" action="${pageContext.request.contextPath}/user/goals" method="post"
                        onsubmit="return confirm('Delete this goal and its time entries?');">
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
