<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    extension-element-prefixes="exslt" xmlns:exslt="http://exslt.org/common">
    <xsl:output method="html" version="5.0" omit-xml-declaration="yes" indent="yes" encoding="UTF-8"/>
    <xsl:template match="/">
        <html>
            <head>
                <xsl:call-template name="head" />
            </head>
            <body>
                <xsl:call-template name="body" />
            </body>
        </html>
    </xsl:template>
    
    <xsl:template name="head">
        <meta charset="UTF-8" />
        <style>@import "Datatables/datatables.min.css";
		#footer { position: fixed; bottom: .5em; right: .5em; font-size: x-small; font-family: sans-serif; }</style>
        <script src="DataTables/datatables.min.js"></script>
        <script>$(() => $("#mangatable").dataTable({processing: true, lengthChange: false, pageLength: 15}));</script>
    </xsl:template>
    
    <xsl:template name="body">
        <table id="mangatable" cellpadding="0" cellspacing="0" border="0" class="display">
        <thead>
            <tr><th>Hoster</th><th>Name</th></tr>
        </thead>
        <tbody>
	        <xsl:for-each select="index/hoster">
	            <xsl:variable name="hostername" select="@name" />
	            <xsl:for-each select="manga">
	                <tr>
			            <xsl:variable name="mangaurl" select="@url" />
	                    <td><xsl:value-of select="$hostername"/></td>
	                    <td><a href="{$mangaurl}"><xsl:value-of select="@name"/></a></td>
	                </tr>
	            </xsl:for-each>
	        </xsl:for-each>
        </tbody>
        </table>
		<div id="footer">
			<span>This page uses <a href="https://datatables.net" target="_blank">DataTables</a> for jQuery</span>
		</div>
    </xsl:template>
    
</xsl:stylesheet>
