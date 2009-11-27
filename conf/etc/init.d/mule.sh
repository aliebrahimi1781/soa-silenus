#!/bin/sh
### BEGIN INIT INFO
# Provides:          mule
# Required-Start:    $network
# Required-Stop:     $network
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start mule at boot time
# Description:       Enable service provided by mule.
### END INIT INFO


MULE_INSTALL_DIR="@mule.destination@"
JAVA_HOME="/opt/java/java-6-sun"
MULE_OWNER=root

export JAVA_HOME MULE_INSTALL_DIR MULE_OWNER


start() {
    echo -n "Starting Mule:  "
    su $MULE_OWNER -c "$MULE_INSTALL_DIR/bin/mule.sh start"
    sleep 2
}
stop() {
    echo -n "Stopping Mule: "
    su $MULE_OWNER -c "$MULE_INSTALL_DIR/bin/mule.sh stop"
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
        echo $"Usage: mule {start|stop|restart}"
        exit
esac
