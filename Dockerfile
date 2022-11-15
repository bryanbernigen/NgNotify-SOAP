FROM tomcat:8.5.83-jdk8

WORKDIR /usr/local/tomcat/bin
COPY run.sh run.sh
RUN chmod +x run.sh

# RUN apt-get update
# RUN apt-get install default-jre --assume-yes
# RUN apt-get install default-jdk --assume-yes
# RUN apt-get install zip --assume-yes

ENV JPDA_ADDRESS="8000"
ENV JPDA_TRANSPORT="dt_socket"

COPY . .

COPY entrypoint.sh /entrypoint.sh
RUN chmod 755 /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]

WORKDIR /usr/local/tomcat/bin