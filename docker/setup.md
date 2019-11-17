

As reference this was [https://hub.docker.com/_/postgres]()

As stated in the documents use
	
    docker run --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -d postgres
    
In order to get it running locally we also need to define

- port mapping publicly 5400 on inside container 

We come down to this command:

    docker run --name postgres4myApp -e POSTGRES_PASSWORD=mysecretpassword --detach --publish 127.0.0.1:5400:5432 postgres:12.0
    
That should do it.

Check out if container is running:

    docker ps	
    
    