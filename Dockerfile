FROM tomcat:10.1-jdk17

# Remove app default
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copia WAR
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
