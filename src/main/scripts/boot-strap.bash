sudo pkill -ef "java -jar ./mllp_receive_view.jar"
sudo pkill -ef "/bin/sh -c /home2/fuse/scripts/mllp-receiver-viewer.bash"
rm /home2/fuse/scripts/mllp-receiver-viewer.bash
rm /home2/fuse/scripts/mllp_receive_view.jar
mkdir /home2/fuse/hl7web-tester
cd /home2/fuse/hl7web-tester

# remove the crontab entry
crontab -l | grep -v '/home2/fuse/scripts/mllp-receiver-viewer.bash' | crontab -


# function to update
update(){

  rm $1
  wget https://esbartifactory.medctr.ucla.edu:6100/artifactory/libs-dev-local/edu/ucla/mednet/iss/it/scripts/$1
  chmod 744 $1
  

}

# update this script
update update-mllp-tester.bash

./update-mllp-tester.bash


