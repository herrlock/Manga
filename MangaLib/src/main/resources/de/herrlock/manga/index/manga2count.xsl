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
        <style><![CDATA[
            * {
                margin-left: 1rem;
            }
            html,body {
                margin-left: 0;
            }
            body > * {
                margin: .5rem;
            }
            .temp {
                padding: .1rem;
                margin-left: 0;
            }
        ]]></style>
    </xsl:template>
    
    <xsl:template name="body">
        <header>
            <div class="temp">Header</div>
            <xsl:call-template name="header" />
        </header>
        <div>
            <div class="temp">Content</div>
            <xsl:call-template name="content" />
        </div>
        <footer>
            <div class="temp">Footer</div>
            <xsl:call-template name="footer" />
        </footer>
    </xsl:template>
    
    <xsl:template name="header">
        <h2><![CDATA[Hello World]]></h2>
    </xsl:template>
    
    <xsl:template name="content">
        <xsl:for-each select="index/hoster">
            <div>
                <xsl:value-of select="@name"/>
                <xsl:text>: </xsl:text>
                <xsl:value-of select="count(manga)"/>
            </div>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="footer">
        <h4>Droggelbecher</h4>
    </xsl:template>
    
</xsl:stylesheet>
