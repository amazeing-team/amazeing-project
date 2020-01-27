# Setup and Execution
We provide a setup- and execution-script `amazing.py` to install all dependencies and execute the program.

This script installs all python-dependencies, starts the server and the client.

The script can be used **after** [`java, gradle, python3, pip3 and yarn`(and `nodejs` for Ubuntu)](#dependencies-with-linux-installation-guide) are installed by running: <br/>
```
python3 amazing.py
```

**CAVE:** If you want to rebuild the project or you build the project for the **first time**, use `-n` or `--new`.

For advanced users we provide additional options for both the script and the Java archive.
```
usage: java -jar <path> [--debug] [--dry] [-h] [-p <port>] [--remote] [-v]
     --debug         enables debug mode
     --dry           dry run
  -h,--help          print help
  -p,--port <port>
     --remote        enables remote mode (this will ignore any shut down
                     commands and requires manual termination - only use, if you
                     know what you are doing)
  -v,--version       print version
```

If this script receives the Signal `SIGINT` or `SIGTERM` (for example by using a `KeyboardInterrupt (Ctrl + C)`), it kills all subprocesses of the server and the client and terminates.

# dependencies (with linux installation guide)

### arch

* java >= 11 <br/>
  ```
  pacman -S jdk11-openjdk
  ```
* gradle >= 5.0 <br/>
  ```
  pacman -S gradle
  ```
* python3 <br/>
  ```
  pacman -S python3
  ```
* pip <br/>
  ```
  pacman -S python3-pip
  ```
* yarn <br/>
  ```
  pacman -S yarn
  ```

### debian/ubuntu

* java >= 11 <br/>
  ```
  apt install openjdk-11-jdk
  ```
* gradle >= 5.0 <br/>
  ```
  curl -s "https://get.sdkman.io" | bash
  sdk install gradle 6.0.1
  ```
* python3 <br/>
  ```
  apt install python3
  ```
* pip (and venv) <br/>
  ```
  apt install build-essential libssl-dev libffi-dev python-dev python3-pip python3-venv
  ```
* yarn <br/>
  ```
  curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | sudo apt-key add -
  echo "deb https://dl.yarnpkg.com/debian/ stable main" | sudo tee /etc/apt/sources.list.d/yarn.list
  sudo apt update && sudo apt install yarn
  ```
  Ubuntu 17.04 comes with cmdtest installed by default. If you’re getting errors from installing yarn, you may want to run `sudo apt remove cmdtest` first.

* nodejs <br/>
  ```
  # Using Ubuntu
  curl -sL https://deb.nodesource.com/setup_12.x | sudo -E bash -
  sudo apt-get install -y nodejs

  # Using Debian, as root
  curl -sL https://deb.nodesource.com/setup_12.x | bash -
  apt-get install -y nodejs
  ```


## python dependencies (installed by script `amazing.py`, see `/admin/requirements.txt`)

* asgiref 3.2.3 <br/>
  ```
  pip3 install --user asgiref==3.2.3
  ```
* certifi 2019.11.28 <br/>
  ```
  pip3 install --user certifi==2019.11.28
  ```
* chardet 3.0.4 <br/>
  ```
  pip3 install --user chardet==3.0.4
  ```
* coreapi 2.3.3 <br/>
  ```
  pip3 install --user coreapi==2.3.3
  ```
* coreschema 0.0.4 <br/>
  ```
  pip3 install --user coreschema==0.0.4
  ```
* Django 3.0 <br/>
  ```
  pip3 install --user Django==3.0
  ```
* django-cors-headers 3.2.0 <br/>
  ```
  pip3 install --user django-cors-headers==3.2.0`

* django-filter 2.2.0 <br/>
  ```
  pip3 install --user django-filter==2.2.0
  ```
* djangorestframework 3.10.3 <br/>
  ```
  pip3 install --user djangorestframework==3.10.3
  ```
* idna 2.8 <br/>
  ```
  pip3 install --user idna==2.8
  ```
* itypes 1.1.0 <br/>
  ```
  pip3 install --user itypes==1.1.0
  ```
* Jinja2 2.10.3 <br/>
  ```
  pip3 install --user Jinja2==2.10.3
  ```
* Markdown 3.1.1 <br/>
  ```
  pip3 install --user Markdown==3.1.1
  ```
* MarkupSafe 1.1.1 <br/>
  ```
  pip3 install --user MarkupSafe==1.1.1
  ```
* pytz 2019.3<br/>
  ```
  pip3 install --user pytz==2019.3
  ```
* PyYAML 5.2 <br/>
  ```
  pip3 install --user PyYAML==5.2
  ```
* requests==2.22.0 <br/>
  ```
  pip3 install --user requests==2.22.0
  ```
* MarkupSafe 1.1.1 <br/>
  ```
  pip3 install --user MarkupSafe==1.1.1
  ```
* sqlparse 0.3.0 <br/>
  ```
  pip3 install --user itypes==1.1.0
  ```
* uritemplate 3.0.0 <br/>
  ```
  pip3 install --user uritemplate==3.0.0
  ```
* urllib3 1.25.7 <br/>
  ```
  pip3 install --user urllib3==1.25.7
  ```
* wheel 0.33.6 <br/>
  ```
  pip3 install --user wheel==0.33.6
  ```

# usage without script (requires manual termination of background processes)

### build project
```
cd server

gradle wrapper
./gradlew build -x check

cd ..
```

### install client dependencies using yarn
```
cd client

yarn install

cd ..
```

### activate venv
```
cd admin

python3 -m venv venv
source venv/bin/activate
pip3 install wheel
pip3 install -r requirements.txt

cd ..
```


### Set up task server (only once)
```
cd admin

python3 manage.py migrate task zero
python3 manage.py migrate
python3 manage.py loaddata admin/fixtures/fixture.yaml

cd ..
```

## start task server
```
cd admin

python3 manage.py runserver &

cd ..
```
### start server
```
cd server

java -jar ./libs/amazing-0.3.jar --port 8081 &

cd ..
```
### start client
```
cd client

yarn serve --host localhost --port 8080 & 

cd ..
```

### play game
```
firefox http://localhost:8080
```
