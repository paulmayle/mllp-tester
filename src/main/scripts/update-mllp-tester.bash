#!/bin/bash


# function to update
update(){

  rm $1
  wget https://esbartifactory.medctr.ucla.edu:6100/artifactory/libs-dev-local/edu/ucla/mednet/iss/it/scripts/$1
  chmod 744 $1

}

# update this script
update update-mllp-tester.bash
# update the startup script
update mllp-tester.bash
# update the executable
update mllp_tester.jar
# kill the java process
sudo pkill -f "[j]ava -jar ./mllp_tester.jar"
echo "killed the running process"
# remove the crontab entry
crontab -l | grep -v '/home2/fuse/hl7web-tester/mllp-tester.bash' | crontab -
echo "Removed the old crontab entry"
# add the crontab entry
(crontab -l ; echo -e "* * * * *  /home2/fuse/hl7web-tester/mllp-tester.bash\n") | crontab -
echo "Replaced it with triggered evert minute"
# wait for the process to start
echo "wait for process start"
unset ps_result
while [ -z "$ps_result" ]
 do
  ps_result=$(ps -ef |grep [m]llp_tester)
  if [ -z "$ps_result" ]; then
    echo " wait for process to start `date`"
    sleep 10
  else
    echo "had a result: $ps_result"
  fi
 done
# started
# remove the start every minute crontab entry
echo "remove the start every minute crontab entry"
crontab -l | grep -v '/home2/fuse/hl7web-tester/mllp-tester.bash' | crontab -
# replace it with start at reboot the crontab entry
echo "replace with start at reboot"
(crontab -l ; echo -e "@reboot  /home2/fuse/hl7web-tester/mllp-tester.bash\n") | crontab -

