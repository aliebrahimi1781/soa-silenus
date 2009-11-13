#!/bin/sh

ALF_HOME="@alfresco.destination@"
ALFRESCO_OPTS="-XX:CompileCommand=exclude,org/apache/lucene/index/IndexReader\$1,doBody -XX:CompileCommand=exclude,org/alfresco/repo/search/impl/lucene/index/IndexInfo\$Merger,mergeIndexes -XX:CompileCommand=exclude,org/alfresco/repo/search/impl/lucene/index/IndexInfo\$Merger,mergeDeletions"


JAVA_HOME="@env.JAVA_HOME@"
JAVA_OPTS="-Xms200m -Xmx1024m -XX:MaxPermSize=256m -server -Duser.language=@user.language@ -Duser.country=@user.country@ -Dfile.encoding=@file.encoding@ -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dlog.home=@alfresco.destination@/tomcat/logs ${ALFRESCO_OPTS}"


JPDA_TRANSPORT="dt_socket"
JPDA_ADDRESS="8000"

export JAVA_HOME JAVA_OPTS JPDA_TRANSPORT JPDA_ADDRESS ALF_HOME
