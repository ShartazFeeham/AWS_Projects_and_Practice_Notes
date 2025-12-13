## Architecting in AWS with environment services
Current plans for the services - 
![img.png](resources/img.png)

Created vpc
![img_2.png](resources/img_2.png)

Here are the security groups
![img_1.png](resources/img_1.png)
![img_3.png](resources/img_3.png)

Need to create 3 target groups for our environment reading application
![img_4.png](resources/img_4.png)
Done
![img_5.png](resources/img_5.png)

Internet facing + IPV4 + Our nature vpc + Right security group and etc then done. 
![img_6.png](resources/img_6.png)
![img_7.png](resources/img_7.png)
And then added the path rules
![img_8.png](resources/img_8.png)

Created this new S3 bucket for this practice
![img_11.png](resources/img_11.png)

Now let's make a CDN
![img_12.png](resources/img_12.png)
Origin is S3 and selected the bucket
![img_13.png](resources/img_13.png)
It's created, now lets edit general and let's set our `th.jpg` file to be default root object
![img_14.png](resources/img_14.png)
![img_15.png](resources/img_15.png)

We'll disable all public access to our S3 and only CDN can access it. 
So lets edit origin and copy the policy and set it to the S3 policies. 
![img_17.png](resources/img_17.png)
![img_18.png](resources/img_18.png)
Boom! Its accessible through the CDN!
![img_19.png](resources/img_19.png)

Till now our CDN only has one origin (S3), let's add another one - ALB.
![img_20.png](resources/img_20.png)
And let's edit behavior to set routing. By default we're delivering to S3. 
![img_21.png](resources/img_21.png)
![img_22.png](resources/img_22.png)

Let's now create our DB subnets
![img_23.png](resources/img_23.png)
Now created a new database 
![img_24.png](resources/img_24.png)
As we set our DB only accessible from our app-sg so we can't access it without an app with that sg but wait, we can!
Using CloudShell. So I created a cloud shell in our nature-vpc and under a public subnet so its in the same network. 
And connected to database then created our nature database.
![img_25.png](resources/img_25.png)

Time to work on Secret manager!
![img_26.png](resources/img_26.png)
![img_27.png](resources/img_27.png)

Created 3 services
![img.png](resources/img_28.png)

Now let's create some ECR to store our images
![img_1.png](resources/img_29.png)
![img_30.png](resources/img_30.png)
In the ESR selected, we can see all commands we need
![img_31.png](resources/img_31.png)



### ECS & Fargate
Amazon ECS (Elastic Container Service) is a container orchestration service for running Docker containers, while Fargate is a serverless compute engine for ECS (and EKS) that lets you run containers without managing underlying servers (EC2 instances)

ECS ~ AWS and Fargate ~ Terraform

Now lets go hands on - 
![img_33.png](resources/img_33.png)
Let's create 3 task definitions for our 3 services
![img_34.png](resources/img_34.png)
Wait a minute! Our docker images are in our ECR and our ECS don't have permission to 
pull from our registry. So we must assign it permission to do that. So, we need to create IAM roles. 
The task is our ECS's permissions and the actions (docker pulling and etc) are called task execution roles. 
![img_35.png](resources/img_35.png)
Added these two task permissions because our ECS have to read from secrets and have to register in the target groups
![img_36.png](resources/img_36.png)
Now we need another role for task execution
![img_37.png](resources/img_37.png)
AWS automatically selection the permission
![img_38.png](resources/img_38.png)
Now, finally time to set them in our Task definition
![img_40.png](resources/img_40.png)
Image and env
![img_41.png](resources/img_41.png)
Created for all 3
![img_42.png](resources/img_42.png)
LOGS: By-default we got logging attached with our ECS. And we can see it in cloud watch. 
![img_44.png](resources/img_44.png)
Inter service communication: We can call one service from another by two ways. 1) Using direct ip - but here it has to 
be inside same subnet and it won't work if there is more than one instance. 2) Through the ALB - but this one is also costly 
and requires a long complicated way of communication. AWS Cloud Map and AWS Service connect comes to rescue. 
First one is simply a discovery server and second one is just side-car pattern load balancer which also offers retry and fault tolerance.  
![img_45.png](resources/img_45.png)
Now we have to create cluster on ECS but prior to that we have to create namespace so that we can separate the instances
group them up like dev and prod, any communication will be happening inside a namespace only. 
![img_46.png](resources/img_46.png)
Now we can create a cluster with the namespace we just created. 
![img_47.png](resources/img_47.png)
![img_48.png](resources/img_48.png)

NOW in the last stages: How do we tell which services are to be combined where our app should run what is the subnet etc. 
I mean the final configuring. So another tool for that: `AWS Services`
![img_49.png](resources/img_49.png)
![img_50.png](resources/img_50.png)
Let's keep replica 0 because we still don't have NAT gateway and our DB is down. We'll later raise it. Also health check
is set to 90 second because we gave 0.5 cpu only and we don't wanna see the service thrown to unhealthy. 
![img_51.png](resources/img_51.png)
Set the correct vpc and subnets
![img_52.png](resources/img_52.png)
Allowing interservice communication (both way). Port alias tells what name of the app is. DNS says how other service may call it.
Both are optional, not specifying them will allow AWS to name it in their convention. 
![img_54.png](resources/img_54.png)
Configuring load balancer
![img_55.png](resources/img_55.png)
![img_56.png](resources/img_56.png)
Done all 3
![img_57.png](resources/img_57.png)
Now we have to create a NAT gateway to allow our instances to communicate with internet. 
![img_58.png](resources/img_58.png)
Now let's assign it to private 1 and 2 (for app) subnets
![img_59.png](resources/img_59.png)
![img_60.png](resources/img_60.png)
Well configured
![img_61.png](resources/img_61.png)
Now in ECS, I updated the temperature service and set replica to 2. 
Here is what we got
![img_62.png](resources/img_62.png)
First got some error in temperature service related to cloudwatch log iam permission. Then adding this fixed the error
![img_63.png](resources/img_63.png)
Now we have error from our app
![img_64.png](resources/img_64.png)
Got the error because in the application properties, I used "/" prefix before the aws secret! Damn! It has always been the culprit. 
Anyway, instead of fixing the application and pushing it in the ECR again, I just created a new secret with the / prefix. 
![img_65.png](resources/img_65.png)
And then finally...
![img_66.png](resources/img_66.png)
![img_67.png](resources/img_67.png)
I've made a stupid mistake! In the app-sg I set inbound allowed port 80. That's why all health check were failing. 
After setting it to 8080 finally services are showing healthy. 
![img_68.png](resources/img_68.png)
![img_69.png](resources/img_69.png)

And they are ready to take requests, Alhamdulillah!
![img_70.png](resources/img_70.png)
![img_71.png](resources/img_71.png)
![img_72.png](resources/img_72.png)



