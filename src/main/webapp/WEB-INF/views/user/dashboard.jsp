<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>My Progress | Meridian</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/user/nav.jsp">
  <jsp:param name="active" value="dashboard"/>
</jsp:include>

<div class="container">
  <div class="hero">
    <div>
      <h2>Hello, <c:out value="${sessionScope.currentUser.name}"/></h2>
      <p>Your goals and the time you have put into them.</p>
    </div>
    <c:if test="${empty error}">
      <div class="hero-badge">
        <span class="big">${summary.completionRate}%</span>
        <span class="small">Goals completed</span>
      </div>
    </c:if>
  </div>

  <c:if test="${empty error}">
    <div class="stats">
      <div class="stat s-navy"><div class="icon">🎯</div><div><div class="num">${summary.totalGoals}</div><div class="lbl">Goals set</div></div></div>
      <div class="stat s-green"><div class="icon">✅</div><div><div class="num">${summary.completedGoals}</div><div class="lbl">Completed</div></div></div>
      <div class="stat s-orange"><div class="icon">🚀</div><div><div class="num">${summary.activeGoals + summary.overdueGoals}</div><div class="lbl">In progress</div></div></div>
      <div class="stat s-navy"><div class="icon">⏱️</div><div><div class="num">${summary.totalHoursText}</div><div class="lbl">Hours logged</div></div></div>
      <div class="stat s-red"><div class="icon">🔥</div><div><div class="num">${summary.streak}</div><div class="lbl">Day streak</div></div></div>
    </div>

    <div class="chart-grid">
      <div class="panel">
        <h3>Goal progress</h3>
        <c:if test="${empty summary.goals}">
          <p class="empty">No goals yet. <a href="${pageContext.request.contextPath}/user/goals">Set your first goal</a>.</p>
        </c:if>
        <c:forEach var="g" items="${summary.goals}">
          <div class="goal-row">
            <div class="goal-head">
              <div>
                <span class="goal-name"><c:out value="${g.name}"/></span>
                <span class="goal-type"><c:out value="${g.goalTypeName}"/></span>
              </div>
              <span class="tag tag-${g.status.name().toLowerCase()}">${g.status}</span>
            </div>
            <div class="progress"><div class="progress-bar" style="width: ${g.progressPercent}%"></div></div>
            <div class="goal-meta">
              <span>${g.loggedText} / ${g.targetText} hours (${g.progressPercent}%)</span>
              <span>${g.deadlineNote} &middot; ${g.deadlineText}</span>
            </div>
          </div>
        </c:forEach>
      </div>

      <div class="panel">
        <h3>Hours in the last 7 days</h3>
        <div class="chart-box"><canvas id="hoursChart"></canvas></div>
      </div>
    </div>

    <div class="grid shortcuts">
      <a class="card" href="${pageContext.request.contextPath}/user/goals">
        <h3>🎯 Set a goal</h3>
        <p>Pick a target and a deadline for something you want to achieve.</p>
      </a>
      <a class="card" href="${pageContext.request.contextPath}/user/time-logs">
        <h3>⏱️ Log your time</h3>
        <p>Start the focus timer or add the hours you already worked.</p>
      </a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
    <script>
      new Chart(document.getElementById('hoursChart'), {
        type: 'bar',
        data: {
          labels: ${dayLabels},
          datasets: [{ label: 'Hours', data: ${dayValues}, backgroundColor: '#0b2545', borderRadius: 6 }]
        },
        options: {
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: { y: { beginAtZero: true } }
        }
      });
    </script>
  </c:if>
</div>
<div class="footer">Meridian &middot; Make Time. Move Forward.</div>
</body>
</html>
