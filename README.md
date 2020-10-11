# circuit-breaker-impl

for circuit breaker testing - use 
https://github.com/hocyadav/sample-service2-for-circuit-breaker


```curl
#from CB call get method to sample-service2
curl --location --request GET 'localhost:8081/v1/cb'

#from CB call post method to sample-service2
curl --location --request POST 'localhost:8081/v1/cb'

#actualtor endpoint
curl --location --request GET 'localhost:8081/actuator/health'
```

![image](https://user-images.githubusercontent.com/56931032/95672735-ee9fab80-0bc0-11eb-96ab-6cd4b93d7463.png)




prometheus setup 
1. app setup (using docker)
2. config file (to connect prometheus and spring boot app) 
  - 1st entry for prometheus 
  - 2nd entry for spring boot app
3. open localhost 9090 and see same metrics as spring boot localhost../actuator/metrics

create prometheus yml file - locally and add to docker https://www.youtube.com/watch?v=hOhHmnE9uXs&list=PLq3uEqRnr_2GlhVSqltfLtpO8GF4VIICY&index=5
```yml
# my global config
global:
  scrape_interval:     15s # Set the scrape interval to every 15 seconds. Default is every 1 minute.
  evaluation_interval: 15s # Evaluate rules every 15 seconds. The default is every 1 minute.
  # scrape_timeout is set to the global default (10s).

# Alertmanager configuration
# alerting:
#   alertmanagers:
#   - static_configs:
#     - targets:
      # - alertmanager:9093

# Load rules once and periodically evaluate them according to the global 'evaluation_interval'.
rule_files:
  # - "first_rules.yml"
  # - "second_rules.yml"

# A scrape configuration containing exactly one endpoint to scrape:
# Here it's Prometheus itself.
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: 'prometheus'
    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.
    static_configs:
    - targets: ['127.0.0.1:9090']

  - job_name: 'spring-resilience4j'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['127.0.0.1:8081']
```

create docker file prometheus 
```dockerfile
FROM prom/prometheus
ADD prometheus.yml /etc/prometheus/
```

start docker file 
```curl
docker build -t my-prometheus .
docker run -p 9090:9090 my-prometheus
```

now we can see http://localhost:8081/actuator/prometheus this data on http://localhost:9090/

----
```curl
#1. Install grafana using brew
brew install grafana
#2. start grafana (default port 3000)
brew services start grafana
```
