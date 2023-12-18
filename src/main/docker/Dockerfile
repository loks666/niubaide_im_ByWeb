FROM java:8
VOLUME /tmp
ADD niubaide_im_by_web.jar app.jar
ENV LANG en_US.UTF-8
RUN bash -c 'touch /app.jar'
EXPOSE 5001
EXPOSE 3001
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom","-Dfile.encoding=UTF-8", "-jar", "/app.jar" ]