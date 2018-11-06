<%-- 
    Document   : index
    Created on : Oct 30, 2018, 9:20:25 PM
    Author     : phduo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="Styles/index.css" rel="stylesheet" type="text/css"/>
        <title>Product Page</title>
    </head>
    <body>
        <div class="container">
            <%--<c:set var="xsldoc" value="${applicationScope['XSLDOC']}"/>--%>
            <c:set var="type" value="${applicationScope['TYPE']}"/>
            <c:set var="context" value="${pageContext.request.contextPath}/ProductServlet" />
            <c:set var="doc" value="${requestScope['RESULT']}"/>
            <c:set var="lastPage" value="${requestScope['LASTPAGE']}"/>
            <c:set var="categoryId" value="${requestScope['CATEGORYID']}"/>
            <c:set var="pIndex" value="${requestScope['PINDEX']}"/>

            <%--<c:out value="${categoryId}"/>--%>
            <%--<c:out value="${size}"/>--%>
            <%--<c:out value="${lastPage}"/>--%>
            <c:out value="${xsldoc}"/>
            <c:out value="${type}"/>
            <c:if test="${not empty doc}">
                <x:set var="product" select="$doc"/>

                <c:import url="WEB-INF/product.xsl" var="xsldoc"/>
                <x:transform xml="${product}" xslt="${xsldoc}">
                    <x:param name="context" value="${context}"/>
                    <x:param name="categoryId" value="${categoryId}"/>
                    <x:param name="pIndex" value="${pIndex}"/>
                    <x:param name="lastPage" value="${lastPage}"/>
                    <x:param name="startLoop" value="${pIndex}"/>
                </x:transform>

            </c:if>
            <c:if test="${empty doc}">
                Empty
            </c:if>
        </div>
    </body>
</html>
