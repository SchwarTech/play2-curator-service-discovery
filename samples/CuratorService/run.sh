echo "Usage: ./run.sh <port>"

play -Dhttp.port=$1 -Djava.net.preferIPv4Stack=true run
