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
