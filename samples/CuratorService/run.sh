echo "Usage: ./run.sh <port>"

#play -Dhttp.port=$1 -Djava.net.preferIPv4Stack=true run
play -Dhttp.port=disabled -Dhttps.port=9443 -Djava.net.preferIPv4Stack=true run
