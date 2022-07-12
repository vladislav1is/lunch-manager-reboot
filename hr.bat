call mvn -B -s settings.xml -DskipTests=true clean package
call java -Dspring.profiles.active=prod -Dfile.encoding=UTF-8 -jar target/dependency/webapp-runner.jar target/*.war