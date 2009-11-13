#!/bin/bash

# Normalize MULE_HOME
MULE_HOME=$(dirname $0)/..
MULE_HOME=$( cd $MULE_HOME && pwd )


# Set java opts
JAVA_OPTS=""
# Set debug opts
JPDA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
# Set MULE_OPTS
MULE_OPTS="-Dmule.base=$MULE_HOME -Dlog4j.configuration=file://$MULE_HOME/conf/log4j.properties"



# Prepare class path
CP="."
for i in $(ls $MULE_HOME/lib)
	do CP=$CP:$MULE_HOME/lib/$i
done
JAVA_OPTS="$JAVA_OPTS -cp $CP"


# Pid file
PIDFILE="$MULE_HOME/mule.pid"
CMD="-Dmule.base=$MULE_HOME"

getpid() {
  if [ -f "$PIDFILE" ]; then
  	if [ -r "$PIDFILE" ]; then
  		pid=$(cat "$PIDFILE")
  	fi
  fi
}



# Mule run options
MULE_OPTS="$MULE_OPTS org.mule.MuleServer -config $MULE_HOME/conf/mule-config.xml"


case "$1" in
	'start')
		getpid
		if [ "X$pid" = "X" ]; then
			echo "Starting mule..."
			java $JAVA_OPTS $MULE_OPTS &
			echo $! > $PIDFILE
		else
			echo "Mule is already running"
			exit 1
		fi
		;;
	'stop')
		getpid
		if [ "X$pid" = "X" ]; then
			echo "Mule is not running"
			exit 1
		else
			echo "Killing mule..."
			kill $pid
			rm -f $PIDFILE
			exit 0
		fi
		;;
	'debug')
		getpid
		echo "Starting mule for debugging..."
		java $JAVA_OPTS $JPDA_OPTS $MULE_OPTS &
		echo $! > $PIDFILE
		;;
	*)
		echo "$0 start|stop|debug"
		;;
esac

exit 0