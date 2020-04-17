#!/bin/sh
#-------------------------------------------
#--- BizStore Indigo ESB V2.5 Bootstrap Script
#--- Update Date : 2014-05-18 00:12:16
#------------------------------------------- 
echo
echo ------------------------------------------------------------
echo --- BizStore Indigo ESB V2.5
echo --- Copyright \(c\) 2018, Metabuild Corporation,
echo --- All Rights Reserved.
echo ------------------------------------------------------------
echo
usage(){
echo "Usage: $0 {start|stop|check} [ CONFIGS ... ] "
exit 1
}
HOME="/hm_indigo" 
#HOME="%HOMEDIR" 
CHECK=`echo $HOME | cut -c1-1`
if [ "$CHECK" = "%" ] ; then
        HOME=`pwd`
fi
echo 'Indigo Home : ' $HOME
USE_DERBY=0

echo
IMC_HOME="$HOME/indigo/imc/bin"
DERBY_HOME="$HOME/indigo/db-derby-10.14.1.0/bin"
ESB_HOME="$HOME/indigo/esb-2.5.0/bin"
IM_HOME="$HOME/indigo/agent-2.5.0/im/bin"
PID_DIR="$HOME/indigo/agent-2.5.0/im/pid"

#-------------------------------------------------------------
ACTION=$1
##################################################
#Do the action
##################################################
case "$ACTION" in
#-------------------------------------------------------------
start)
#-------------------------------------------------------------
if [ $USE_DERBY -eq 1 ] ; then
	cd $DERBY_HOME
	echo $DERBY_HOME
	run.sh start
fi

echo
cd $IMC_HOME
echo $IMC_HOME
startup.sh
cd $ESB_HOME
echo $ESB_HOME
indigo.sh start
cd $IM_HOME
echo $IM_HOME
im.sh start

;;
#-------------------------------------------------------------
stop)
#-------------------------------------------------------------
if [ $USE_DERBY -eq 1 ] ; then
	cd $DERBY_HOME
	echo $DERBY_HOME
	run.sh stop
fi

cd $IMC_HOME
echo $IMC_HOME
shutdown.sh -force

cd $ESB_HOME
echo $ESB_HOME
indigo.sh stop

yyy=`ps -ef |grep "type:IM" | grep $LOGNAME|grep -v  grep | awk '{ print $2 }'`
if [ "$yyy" != "" ]
then
        cd $IM_HOME
        echo $IM_HOME
        im.sh stop
fi
echo

cd $PID_DIR
for j in `ls "$PID_DIR" `; do
		#if [ 1 = "`expr $j : A `" ]
		#then
				RUN_DESC=$j
				echo $RUN_DESC
				INDIGO_PID="$PID_DIR/$j"
				PID=`cat $INDIGO_PID 2>/dev/null`
				echo "Shutting down $RUN_DESC : $PID"
				kill -9 $PID 2>/dev/null
				rm -f $INDIGO_PID
				echo "$RUN_DESC STOPPED `date`"
				echo
		#fi
done

;;
#-------------------------------------------------------------
check)
#-------------------------------------------------------------
echo "<<Process Summary>>"

echo "[ IMC ]------------------------------------------------------------------------------"
        if [ -f $IMC_HOME/imc.pid ]
        then
                PID=`cat $IMC_HOME/imc.pid 2>/dev/null`
                echo "  IMC Process ID: $PID (구동중)"
                echo `  ps -ef|grep $LOGNAME|grep $PID|grep -v grep`
        else
                echo "  IMC (미구동중)"
        fi
echo
echo "[ ESB ]------------------------------------------------------------------------------"
        if [ -f $ESB_HOME/esb.pid ]
        then
                PID=`cat $ESB_HOME/esb.pid 2>/dev/null`
                echo "  ESB Process ID: $PID (구동중)"
                echo `  ps -ef|grep $LOGNAME|grep $PID|grep -v grep`
        else
                echo "  ESB (미구동중)"
        fi
echo
if [ $USE_DERBY -eq 1 ] ; then
	echo "[ REPDB ]------------------------------------------------------------------------------"
			if [ -f $DERBY_HOME/derby.pid ]
			then
					PID=`cat $DERBY_HOME/derby.pid 2>/dev/null`
					echo "  REPDB Process ID: $PID (구동중)"
					echo `  ps -ef|grep $LOGNAME|grep $PID|grep -v grep`
			else
					echo "  REPDB (미구동중)"
			fi
	echo
fi
echo "[ IM ]------------------------------------------------------------------------------"
        if [ -f $IM_HOME/im.pid ]
        then
                PID=`cat $IM_HOME/im.pid 2>/dev/null`
                echo "  Instance Manager Process ID:$PID (구동중)"
                echo `  ps -ef|grep IM|grep $PID|grep -v grep`
        else
                echo "  Instance Manager (미구동중)"
        fi
echo
echo "[ ADAPTOR ]-------------------------------------------------------------------------"
cd $PID_DIR
        for j in `ls "$PID_DIR" `; do
#                if [ 1 -lt "`expr $j : .*AD.*`" ]
#                then
                        RUN_DESC=$j
                        INDIGO_PID="$PID_DIR/$j"
                        PID=`cat $INDIGO_PID 2>/dev/null`
                        if [ 1 -eq `ps -ef|grep java|grep $PID|grep $LOGNAME|wc -l` ]; then
                                echo "  $RUN_DESC : $PID"
                                echo `  ps -ef|grep $LOGNAME|grep $PID|grep -v grep`
                        fi
                #elif [ 1 = "`expr $j : M`" ]
                #then
                #       RUN_DESC=$j
                #       INDIGO_PID="$PID_DIR/$j"
                #       PID=`cat $INDIGO_PID 2>/dev/null`
                #       echo "  $RUN_DESC : $PID"
#                elif [ 1 = "`expr $j : R`" ]
#                then
#                        RUN_DESC=$j
#                        INDIGO_PID="$PID_DIR/$j"
#                        PID=`cat $INDIGO_PID 2>/dev/null`
#                        echo "  $RUN_DESC : $PID"
#                fi
        done
echo "------------------------------------------------------------------------------------"
;;
#-------------------------------------------------------------
*)
usage
;;
esac
exit 0

