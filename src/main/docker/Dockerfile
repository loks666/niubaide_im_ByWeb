FROM java:8
VOLUME /tmp
ADD niubaide_im_by_web.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 5001
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar" ]