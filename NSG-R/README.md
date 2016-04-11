### NSG REST API 

**This is a work in progress!!** This feature may change or be removed without notice!

This work is based on the REST API from  [CIPRES - Cyberinfrastructure for Phylogenetic Research](http://www.phylo.org/index.php/news/detail/announcing-cipres-restful-services-a-new-way-to-use-cipres).

#### Sign up to the NSG REST test server

1. Go to https://nsgr.sdsc.edu:8444/restusers and register for a new account
2. Log in to the service
3. Go to Developer -> Application Management
4. Click Create New Application
5. Create the application, describing what you'll be using the service for. **Please take the time to add relevant info here. Having access to this information will help the developers justify the resource and keep it free for the community!**
6. Make sure to set Authentication Type to DIRECT.
7. Make a note of the Application ID
8. Clone a copy of this repository:
    ```
    git clone https://github.com/OpenSourceBrain/NSGPortalShowcase.git
    cd NSGPortalShowcase/NSG-R/
    ```

9. Copy the [nsgrest.conf.example](https://github.com/OpenSourceBrain/NSGPortalShowcase/blob/master/NSG-R/nsgrest.conf.example) file to **nsgrest.conf** and move it to your home folder
10. Update the details there with the information you entered on the NSG REST server:

    ```
    URL=https://nsgr.sdsc.edu:8444/cipresrest/v1
    USERNAME=uuuuuu
    PASSWORD=xxxxxx
    DIRECT_APPID=Direct111111111111
    ```
    
11. DIRECT_APPID should be set to your Application ID


#### Access using shell scripts


1. Test listing/submitting of jobs with:

    ```
    ./list_jobs.sh              # none yet...
    ./submit_python_job.sh      # submit a simple Python job
    ./list_jobs.sh              # should be listed
    ./status_job.sh JOBIDXXXX   # Replace job ID with that listed above
    ```
    

#### Access using Java API
    
See [here](https://github.com/OpenSourceBrain/NSGPortalShowcase/tree/master/NSG-R/Java).





