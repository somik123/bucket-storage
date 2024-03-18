[![Docker Image Build](https://github.com/somik123/bucket-storage/actions/workflows/main.yaml/badge.svg)](https://github.com/somik123/bucket-storage/actions/workflows/main.yaml)
# Bucket Storage
A simple bucket storage system written in Java Spring Boot

Set the following environment variables if not using official docker image:
```
BUCKET_USER - Admin username to manage all buckets
BUCKET_PASS - Admin password to manage all buckets
BUCKET_PASS_HIDE - Set this to "yes" to disable printing the password to logs when starting up the app
BUCKET_DB_HOST - MySQL database host, usually localhost
BUCKET_DB_NAME - MySQL database name. Note that the database must exist. Tables will be autocreated on boot (if not exist)
BUCKET_DB_USER - MySQL database usernme
BUCKET_DB_PASS - MySQL database password
```

### Installation
Copy/download the `docker-compose.yml` file and run it. You do not require any of the other source files unless you want to build the image yourself.
```
curl -o docker-compose.yml https://raw.githubusercontent.com/somik123/bucket-storage/main/docker-compose.yml
```

Edit the file with your favorit editor to change the db details as well as the admin username/password.

```
docker compose up -d
```
The website will be on port `8080` on the server you run it on. Use nginx proxy to secure it with https.

Default user details (if not set from ENV variables):
- Username: `user`
- Password: `password`

Set the environmental variable `BUCKET_PASS_HIDE` to `no` to display the admin username and password in the log file during boot.

<br>

### To-do list
- [x] Add env variables for db settings.
- [x] Write a dockerfile to build the app for deployment.
- [x] Write a docker compose script to build and run the stack.
- [x] Implement "upload-only" buckets with downloads/file-browser disabled.
- [x] Implement "download-only" buckets with uploads disabled bit file-browser enabled.
- [x] Improve password encryption method.
- [ ] Test for vulnerabilities.


### DB Manager
PhpMyAdmin database manager is disabled by default in the `docker-compose.yml` file. It provides a simple way to troubleshoot or edit your bucket database but use it at your own risk.

To use it, copy paste the following code block at the bottom of your `docker-compose.yml` file and run the `docker compose up -d` command.

```
  db_manager:
    image: phpmyadmin
    container_name: phpmyadmin
    restart: unless-stopped
    environment:
      TZ: Asia/Singapore
      PMA_HOST: db
    ports:
      - 8088:80
    depends_on:
      - db
    links:
      - db
```
It is available on port `8088` once it is up. 

Login with the username `root` and the `MYSQL_ROOT_PASSWORD` you set in your `docker-compose.yml` file. You can also login with username and password you set for `MYSQL_USER` and `MYSQL_PASSWORD` to login.
