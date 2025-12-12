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



