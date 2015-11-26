<%@ page language="java" session="false" %>
<jsp:useBean id="it" scope="request" type="com.jay.montior.common.ExceptionView"/>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="root" value="${pageContext.request.contextPath}" />

<t:genericpage>
    <jsp:body>
        <div class="col-lg-3">
            <i class="fa fa-fire fa-5 jumbo" style="color: orangered"></i>
        </div>
        <h1>Oops</h1>
        <p>Error ${it.message}</p>

        <div class="mono">
            <c:set var="lines" value="${it.stacktrace}" />
            <c:forEach items='${lines}' var='line'>
                <p>${line}</p>
            </c:forEach>
        </div>
    </jsp:body>

</t:genericpage>
