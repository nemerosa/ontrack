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

# spring boot options
SPRINGBOOTAPP_OPTIONS="--spring.profiles.active=prod --logging.file=$LOG_DIR/$PROJECT_NAME.log --ontrack.config.applicationWorkingDir=$DATA_DIR/files \"--spring.datasource.url=jdbc:h2:$DATA_DIR/database/data;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;DEFRAG_ALWAYS=TRUE\""

LOCK="/var/lock/subsys/$PROJECT_NAME"

RETVAL=0

pid_of_spring_boot() {
    pgrep -f "java.*/opt/ontrack/lib/ontrack\.jar.*"
}

start() {
    echo -n $"Starting $PROJECT_NAME: "

    cd "$SPRINGBOOTAPP_HOME"
    su $SERVICE_USER -c "nohup $SPRINGBOOTAPP_JAVA -jar \"$SPRINGBOOTAPP_JAR\" $SPRINGBOOTAPP_OPTIONS  >> /dev/null 2>&1 &"

    cnt=10
    while ! { pid_of_spring_boot > /dev/null ; } && [ $cnt -gt 0 ] ; do
        sleep 1
        ((cnt--))
    done

    pid_of_spring_boot > /dev/null
    RETVAL=$?
    [ $RETVAL = 0 ] && echo "[OK]" || echo "[NOK]"
    echo

    [ $RETVAL = 0 ] && touch "$LOCK"
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

    [ $RETVAL = 0 ] && rm -f "$LOCK"
    [ $RETVAL = 0 ] && echo "OK" || echo "NOK"
    echo
}

status() {
    pid=`pid_of_spring_boot`
    if [ -n "$pid" ]; then
        echo "$PROJECT_NAME (pid $pid) is running..."
        return 0
    fi
    if [ -f "$LOCK" ]; then
        echo $"${base} dead but subsys locked"
        return 2
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