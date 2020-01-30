# mllp-tester-centos7
FROM openshift/base-centos7

LABEL maintainer="Paul Mayle <pmayle@mednet.ucla.edu>"

ENV BUILDER_VERSION 1.0

LABEL io.k8s.description="Platform for testing mllp services" \
      io.k8s.display-name="mllptester 0.0.1" \
      io.openshift.expose-services="8080:http" \
      io.openshift.tags="builder,tester"

# Update and install java, maven
RUN yum -y update && \
    yum -y install java-1.8.0-openjdk && \
    yum -y install maven && \
    yum -y clean all
    
# Set JAVA_HOME
ENV JAVA_HOME=/etc/alternatives/jre 

# TODO (optional): Copy the builder files into /opt/app-root
# COPY ./<builder_folder>/ /opt/app-root/

# Copy the S2I scripts to /usr/libexec/s2i, since openshift/base-centos7 image
# sets io.openshift.s2i.scripts-url label that way (or update that label)
COPY ./s2i/bin/ /usr/libexec/s2i

# TODO: Drop the root user and make the content of /opt/app-root owned by user 1001
RUN chown -R 1001:1001 /opt/app-root

# This default user is created in the openshift/base-centos7 image
USER 1001

EXPOSE 7000-8000

# Start the tester
# TODO is this replaced by the run script ??
#  CMD ["/usr/bin/java",  "-jar" , "/opt/app-root/mllp_tester.jar"]

