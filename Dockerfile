FROM prom/prometheus
ADD prometheus.yml /etc/prometheus/

# -- build and run --
# docker build -t my-prometheus .
# docker run -p 9090:9090 my-prometheus
