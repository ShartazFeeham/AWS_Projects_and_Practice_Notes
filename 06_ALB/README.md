This time selected specific availability zone: ap-southeast-1a and choose 2 instances.
So that we can deploy our temperature and humidity service in two instances running in same AZ.
![img.png](resources/img.png)

And then I created two more on **1b** AZ, so that we can cross deploy our apps
![img_1.png](resources/img_1.png)

Now we're cross running our apps
![img_2.png](resources/img_2.png)

There were some build issues in my docker image so couldn't run in the first attempt.
![img_3.png](resources/img_3.png)

Then after building using buildX, platform miss-match issue was solved. 
![img_4.png](resources/img_4.png)
App is running and tested the curl as well. Both apps are ran, two each in two different AZ. 

Now time to group up our instances for load balancing
![img_5.png](resources/img_5.png)

Our own health check path
![img_6.png](resources/img_6.png)

Updated the health check thresholds as well
![img_7.png](resources/img_7.png)

Finally select them
![img_8.png](resources/img_8.png)

Group created
![img_9.png](resources/img_9.png)

Same for humidity app
![img_10.png](resources/img_10.png)

Let's create a security group for our ALB with port 80 open
![img_11.png](resources/img_11.png)

Then edited the app-sg that we used for all of our app instances, and allowed the alb-sg with differnt ports
![img_12.png](resources/img_12.png)

Time to create the ALB
![img_13.png](resources/img_13.png)

Set the availability zones and security group
![img_14.png](resources/img_14.png)

Let's edit the default behavior to 404
![img_15.png](resources/img_15.png)

Time to add path based routing for temperature service
![img_16.png](resources/img_16.png)

Rules are added
![img_17.png](resources/img_17.png)

Now our target groups are shown to be healthy
![img_18.png](resources/img_18.png)

Our load balancer is running! And gave the default response for random path!
![img_19.png](resources/img_19.png)

Actually I messed up the route conditions, now lets fix those
![img_20.png](resources/img_20.png)

Boom! Its routing and balancing!
![img_21.png](resources/img_21.png)
![img_22.png](resources/img_22.png)

Time to test it! Let's turn down one instance of temperature service
![img_23.png](resources/img_23.png)

One down. May day, may day!
![img_24.png](resources/img_24.png)

And it's still working!
![img_25.png](resources/img_25.png)

HAPPY DAY. :)