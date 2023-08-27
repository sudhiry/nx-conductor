FROM nginx:stable
RUN rm -rf /usr/share/nginx/html/
COPY ./build/ /usr/share/nginx/html/
COPY ./nginx/nginx.conf /etc/nginx/conf.d/nginx.conf.template
COPY ./nginx/startup.sh /startup.sh
RUN ["chmod", "+x", "/startup.sh"]
EXPOSE 80
CMD ["/startup.sh"]



