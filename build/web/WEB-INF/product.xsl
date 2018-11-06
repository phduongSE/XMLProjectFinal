<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : product.xsl
    Created on : October 17, 2018, 3:37 PM
    Author     : phduo
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <!-- params -->        
    <xsl:param name="context"></xsl:param>
    <xsl:param name="categoryId"></xsl:param>
    <xsl:param name="pIndex"></xsl:param>
    <xsl:param name="lastPage"></xsl:param>
    <xsl:param name="startLoop"></xsl:param>
    <!-- /params -->      
    
    <xsl:template match="/">
        <xsl:call-template name="ProductTemplate">
            <xsl:with-param name="context" select="/"/>
            <xsl:with-param name="categoryId" select="0"/>
            <xsl:with-param name="pIndex" select="0"/>
            <xsl:with-param name="lastPage" select="0"/>
            <xsl:with-param name="startLoop" select="1"/>
        </xsl:call-template>
    </xsl:template>
    
    <!--Product Template-->
    <xsl:template name="ProductTemplate">
        <table border="1">
            <tr>
                <th>No.</th>
                <th>ID</th>
                <th>CategoryID</th>
                <th>Name</th>
                <th>Price</th>
                <th>Picture</th>
            </tr>
            <xsl:for-each select="products/product">
                <tr>
                    <td>
                        <xsl:value-of select="position()"/>
                    </td>
                    <td>
                        <xsl:value-of select="./@id"/>
                    </td>
                    <td>
                        <xsl:value-of select="./@categoryId"/>
                    </td>
                    <td>
                        <xsl:value-of select="./productName"/>
                    </td>
                    <td>
                        <xsl:value-of select="./productPrice"/> VND</td>
                    <!--<td><img src="{imgSrc}"/></td>-->
                </tr>
            </xsl:for-each>
        </table>
        <div class="paging-container">
            <ul class="paging">
                <xsl:if test="($lastPage > 0)">
                    <li>
                        <a href="{$context}?pIndex=1&amp;categoryId={$categoryId}">
                        &lt;&lt;
                        </a>
                    </li>
                </xsl:if>
                <xsl:call-template name="PagingTemplate">
                    <xsl:with-param name="startLoop" select="$pIndex"/>
                </xsl:call-template>
                <xsl:if test="($lastPage > 0)">
                    <li>
                        <a href="{$context}?pIndex={$lastPage}&amp;categoryId={$categoryId}">
                        &gt;&gt;
                        </a>
                    </li>
                </xsl:if>
            </ul>
        </div>
    </xsl:template>
    <!--Product Template-->
    
    <!--Paging Template-->
    <xsl:template name="PagingTemplate">
        <xsl:param name="startLoop" select="1"/>
        <xsl:if test="($lastPage >= $startLoop) and (($pIndex + 10) > $startLoop)">
            <!--paging-->
            <xsl:element name="li">
                <xsl:if test="($pIndex = $startLoop)">
                    <xsl:attribute name="class">active</xsl:attribute>
                </xsl:if>
                <a href="{$context}?pIndex={$startLoop}&amp;categoryId={$categoryId}">
                    <xsl:value-of select="$startLoop"/>
                </a>
            </xsl:element>
            <!--/paging-->
            <xsl:call-template name="PagingTemplate">
                <xsl:with-param name="startLoop" select="$startLoop + 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <!--/Paging Template-->
</xsl:stylesheet>
