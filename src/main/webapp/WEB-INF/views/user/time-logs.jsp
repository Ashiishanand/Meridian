<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Time Logs | Meridian</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/user/nav.jsp">
  <jsp:param name="active" value="timelogs"/>
</jsp:include>

<div class="container">
  <h2>Time Logs</h2>
  <p class="sub">Record the time you spend working towards your goals.</p>

  <div class="panel timer">
    <h3>Focus timer</h3>
    <div class="clock" id="clock">00:00:00</div>
    <button type="button" class="btn" id="timerBtn">Start</button>
    <span class="empty">Stop the timer to fill in the start and end time below.</span>
  </div>

  <div class="panel">
    <h3>Log time</h3>
    <c:if test="${empty openGoals}">
      <p class="empty">You need an open goal to log time. <a href="${pageContext.request.contextPath}/user/goals">Create one</a>.</p>
    </c:if>
    <c:if test="${not empty openGoals}">
      <form action="${pageContext.request.contextPath}/user/time-logs" method="post">
        <input type="hidden" name="action" value="add">
        <div class="form-row">
          <div>
            <label for="goalId">Goal</label>
            <select id="goalId" name="goalId" required>
              <c:forEach var="g" items="${openGoals}">
                <option value="${g.id}"><c:out value="${g.name}"/></option>
              </c:forEach>
            </select>
          </div>
          <div>
            <label for="start">Start time</label>
            <input type="datetime-local" id="start" name="start" required>
          </div>
          <div>
            <label for="end">End time</label>
            <input type="datetime-local" id="end" name="end" required>
          </div>
          <div>
            <label for="notes">Notes</label>
            <input type="text" id="notes" name="notes" maxlength="255">
          </div>
          <button type="submit" class="btn">Save entry</button>
        </div>
      </form>
    </c:if>
  </div>

  <div class="panel">
    <h3>Your time entries</h3>
    <c:if test="${empty entries}">
      <p class="empty">No time logged yet.</p>
    </c:if>
    <c:if test="${not empty entries}">
      <div class="table-wrap">
        <table class="data">
          <thead>
            <tr><th>Goal</th><th>Start</th><th>End</th><th>Duration</th><th>Notes</th><th>Actions</th></tr>
          </thead>
          <tbody>
            <c:forEach var="e" items="${entries}">
              <tr>
                <td><c:out value="${e.goalName}"/></td>
                <td>${e.startText}</td>
                <td>${e.endText}</td>
                <td>${e.durationText}</td>
                <td><c:out value="${e.notes}"/></td>
                <td>
                  <form class="inline" action="${pageContext.request.contextPath}/user/time-logs" method="post"
                        onsubmit="return confirm('Delete this time entry?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${e.id}">
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

<script>
  (function () {
    var button = document.getElementById('timerBtn');
    var clock = document.getElementById('clock');
    var startedAt = null;
    var ticker = null;

    function pad(n) { return n < 10 ? '0' + n : String(n); }

    function toLocalValue(d) {
      return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate())
           + 'T' + pad(d.getHours()) + ':' + pad(d.getMinutes());
    }

    button.addEventListener('click', function () {
      if (startedAt === null) {
        startedAt = new Date();
        button.textContent = 'Stop';
        ticker = setInterval(function () {
          var s = Math.floor((new Date() - startedAt) / 1000);
          clock.textContent = pad(Math.floor(s / 3600)) + ':' + pad(Math.floor(s % 3600 / 60)) + ':' + pad(s % 60);
        }, 1000);
      } else {
        clearInterval(ticker);
        var start = document.getElementById('start');
        var end = document.getElementById('end');
        if (start && end) {
          start.value = toLocalValue(startedAt);
          end.value = toLocalValue(new Date());
        }
        startedAt = null;
        button.textContent = 'Start';
        clock.textContent = '00:00:00';
      }
    });
  })();
</script>
<div class="footer">Meridian &middot; Make Time. Move Forward.</div>
</body>
</html>
