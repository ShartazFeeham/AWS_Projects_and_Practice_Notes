## AMI
We'll running an instance and running all those commands everytime
is boring and repeated. So what if we could make an instance, install all basic things
in it and then use that instance as a template? That's AMI (Aws machine image)!

So, created an instance, connected to it and ran these commands: 
```bash
    sudo yum update
    sudo yum install java docker postgresql15 git wget unzip -y
    sudo systemctl start docker.service 
    sudo systemctl enable docker
    sudo usermod -aG docker $USER

    # Additional: 
    curl -s "https://get.sdkman.io" | bash
    source "/home/ec2-user/.sdkman/bin/sdkman-init.sh"
    sdk install gradle 9.1.0
```
Result: We have now system updated to latest, docker, git, java, postgres and 
gradle installed and docker running with user granted access. 
![img.png](resources/img.png)
Now from that instance, we're creating an AMI. 
![img_2.png](resources/img_2.png)
![img_1.png](resources/img_1.png)
Now we have our own AMI in the AMI section and now we can use it to create instances
![img_3.png](resources/img_3.png)
![img_4.png](resources/img_4.png)
It took a little longer to create but from the first moment we got our things ready.
![img_5.png](resources/img_5.png)
