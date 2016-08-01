#!/bin/bash
### BEGIN INIT INFO
# Provides:          ontrack
# Required-Start:    $remote_fs $syslog
# Required-Stop:     $remote_fs $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start daemon at boot time
# Description:       Enable service provided by daemon.
### END INIT INFO

# Source function library.
[ -f "/lib/lsb/init-functions" ] && . /lib/lsb/init-functions

# the name of the project, will also be used for the war file, log file, ...
PROJECT_NAME=ontrack
# the user which should run the service
SERVICE_USER=ontrack
# base directory for the spring boot jar
# TODO change to /usr/local
SPRINGBOOTAPP_HOME=/opt/$PROJECT_NAME

# the spring boot jar-file
SPRINGBOOTAPP_JAR="$SPRINGBOOTAPP_HOME/lib/$PROJECT_NAME.jar"

# java executable for spring boot app, change if you have multiple jdks installed
SPRINGBOOTAPP_JAVA=java

# log directory
LOG_DIR="/var/log/$PROJECT_NAME"

# data directory
DATA_DIR="/usr/lib/$PROJECT_NAME"

# extension directory
EXT_DIR="$DATA_DIR/extensions"

# java options
SPRINGBOOTAPP_JAVA_OPTIONS="-Dloader.path=$EXT_DIR"

# Additional options
JAVA_OPTIONS=""
[ -r /etc/default/ontrack ] && . /etc/default/ontrack

# spring boot options
SPRINGBOOTAPP_OPTIONS="--logging.file=$LOG_DIR/$PROJECT_NAME.log --ontrack.config.applicationWorkingDir=$DATA_DIR/files"

RETVAL=0

pid_of_spring_boot() {
    pgrep -f "java.*/opt/ontrack/lib/ontrack\.jar.*"
}

start() {
    echo -n $"Starting $PROJECT_NAME: "

    cd "$DATA_DIR"
    su $SERVICE_USER -c "nohup $SPRINGBOOTAPP_JAVA $SPRINGBOOTAPP_JAVA_OPTIONS $JAVA_OPTIONS -jar \"$SPRINGBOOTAPP_JAR\" $SPRINGBOOTAPP_OPTIONS  >> /dev/null 2>&1 &"

    cnt=10
    while ! { pid_of_spring_boot > /dev/null ; } && [ $cnt -gt 0 ] ; do
        sleep 1
        ((cnt--))
    done

    pid_of_spring_boot > /dev/null
    RETVAL=$?
    [ $RETVAL = 0 ] && echo "[OK]" || echo "[NOK]"
    echo

}

stop() {
    echo -n "Stopping $PROJECT_NAME: "

    pid=`pid_of_spring_boot`
    [ -n "$pid" ] && kill -TERM $pid
    RETVAL=$?
    cnt=10
    while [ $RETVAL = 0 -a $cnt -gt 0 ] &&
        { pid_of_spring_boot > /dev/null ; } ; do
            sleep 1
            ((cnt--))
    done

    [ $RETVAL = 0 ] && echo "OK" || echo "NOK"
    echo
}

status() {
    pid=`pid_of_spring_boot`
    if [ -n "$pid" ]; then
        echo "$PROJECT_NAME (pid $pid) is running..."
        return 0
    fi
    echo "$PROJECT_NAME is stopped"
    return 3
}

# See how we were called.
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    status)
        status
        ;;
    restart)
        stop
        start
        ;;
    *)
        echo $"Usage: $0 {start|stop|restart|status}"
        exit 1
esac

exit $RETVAL
