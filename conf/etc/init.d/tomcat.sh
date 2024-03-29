#!/bin/sh
### BEGIN INIT INFO
# Provides:          tomcat
# Required-Start:    $network
# Required-Stop:     $network
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start tomcat at boot time
# Description:       Enable service provided by tomcat.
### END INIT INFO


CATALINA_HOME="@alfresco.destination@/tomcat"
JAVA_HOME="@env.JAVA_HOME@"
TOMCAT_OWNER=root

export CATALINA_HOME TOMCAT_HOME JAVA_HOME TOMCAT_OWNER


start() {
	echo -n "Starting Tomcat:  "
	su $TOMCAT_OWNER -c $CATALINA_HOME/bin/startup.sh
	sleep 2
}
stop() {
	echo -n "Stopping Tomcat: "
	su $TOMCAT_OWNER -c $CATALINA_HOME/bin/shutdown.sh
}

# See how we were called.
case "$1" in
  start)
        start
        ;;
  stop)
        stop
        ;;
  restart)
        stop
        start
        ;;
  *)
        echo $"Usage: tomcat {start|stop|restart}"
        exit
esac
